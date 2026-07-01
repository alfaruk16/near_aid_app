package com.nearaid.core.network.util

import com.nearaid.core.common.result.AppError
import com.nearaid.core.common.result.DataResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class SafeApiCallTest {

    private fun httpException(code: Int, body: String = ""): HttpException {
        val response = Response.error<Any>(
            code,
            body.toResponseBody("application/json".toMediaTypeOrNull()),
        )
        return HttpException(response)
    }

    // --- safeApiCall wrapper -------------------------------------------------

    @Test
    fun `safeApiCall wraps a returned value in Success`() = runTest {
        val result = safeApiCall { 42 }
        assertEquals(DataResult.Success(42), result)
    }

    @Test
    fun `safeApiCall maps an IOException to a Network error`() = runTest {
        val result = safeApiCall<Int> { throw java.io.IOException("offline") }
        assertEquals(DataResult.Failure(AppError.Network("offline")), result)
    }

    @Test
    fun `safeApiCall maps an unclassified throwable to Unknown`() = runTest {
        val result = safeApiCall<Int> { throw IllegalStateException("boom") }
        assertEquals(DataResult.Failure(AppError.Unknown("boom")), result)
    }

    @Test
    fun `safeApiCall maps an HttpException via toAppError`() = runTest {
        val result = safeApiCall<Int> { throw httpException(404) }
        assertTrue(result is DataResult.Failure && result.error is AppError.NotFound)
    }

    @Test(expected = CancellationException::class)
    fun `safeApiCall rethrows cancellation`() = runTest {
        safeApiCall<Int> { throw CancellationException("cancelled") }
    }

    // --- HttpException.toAppError status-code mapping -------------------------

    @Test
    fun `toAppError maps 400 and 422 to Validation`() {
        assertTrue(httpException(400).toAppError() is AppError.Validation)
        assertTrue(httpException(422).toAppError() is AppError.Validation)
    }

    @Test
    fun `toAppError maps the standard 4xx codes`() {
        assertTrue(httpException(401).toAppError() is AppError.Unauthorized)
        assertTrue(httpException(403).toAppError() is AppError.Forbidden)
        assertTrue(httpException(404).toAppError() is AppError.NotFound)
        assertTrue(httpException(409).toAppError() is AppError.Conflict)
        assertTrue(httpException(429).toAppError() is AppError.RateLimited)
    }

    @Test
    fun `toAppError maps 5xx to Server carrying the code`() {
        val error = httpException(503).toAppError()
        assertTrue(error is AppError.Server)
        assertEquals(503, (error as AppError.Server).code)
    }

    @Test
    fun `toAppError maps an unexpected code to Unknown`() {
        assertTrue(httpException(418).toAppError() is AppError.Unknown)
    }

    @Test
    fun `toAppError decodes the error envelope message and field errors`() {
        val body = """
            {"error":{"code":"invalid","message":"Bad phone","details":{"phone":["required"]}}}
        """.trimIndent()

        val error = httpException(422, body).toAppError() as AppError.Validation

        assertEquals("Bad phone", error.message)
        assertEquals(listOf("required"), error.fieldErrors["phone"])
    }
}
