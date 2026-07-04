package com.nearaid.core.network.util

import com.nearaid.core.common.result.AppError
import com.nearaid.core.common.result.DataResult
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SafeApiCallTest {

    private fun clientReturning(status: HttpStatusCode, body: String = ""): HttpClient {
        val engine = MockEngine {
            respond(
                content = body,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        return HttpClient(engine) {
            expectSuccess = true
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
    }

    // --- safeApiCall wrapper -------------------------------------------------

    @Test
    fun safeApiCall_wraps_a_returned_value_in_Success() = runTest {
        assertEquals(DataResult.Success(42), safeApiCall { 42 })
    }

    @Test
    fun safeApiCall_maps_a_connectivity_failure_to_Network() = runTest {
        val result = safeApiCall<Int> { throw RuntimeException("offline") }
        assertEquals(DataResult.Failure(AppError.Network("offline")), result)
    }

    @Test
    fun safeApiCall_maps_an_http_error_via_toAppError() = runTest {
        val result = safeApiCall { clientReturning(HttpStatusCode.NotFound).get("x") }
        assertTrue(result is DataResult.Failure && result.error is AppError.NotFound)
    }

    @Test
    fun safeApiCall_rethrows_cancellation() = runTest {
        assertFailsWith<CancellationException> {
            safeApiCall<Int> { throw CancellationException("cancelled") }
        }
    }

    // --- status-code mapping -------------------------------------------------

    @Test
    fun maps_400_and_422_to_Validation() = runTest {
        assertTrue(errorFor(HttpStatusCode.BadRequest) is AppError.Validation)
        assertTrue(errorFor(HttpStatusCode.UnprocessableEntity) is AppError.Validation)
    }

    @Test
    fun maps_the_standard_4xx_codes() = runTest {
        assertTrue(errorFor(HttpStatusCode.Unauthorized) is AppError.Unauthorized)
        assertTrue(errorFor(HttpStatusCode.Forbidden) is AppError.Forbidden)
        assertTrue(errorFor(HttpStatusCode.NotFound) is AppError.NotFound)
        assertTrue(errorFor(HttpStatusCode.Conflict) is AppError.Conflict)
        assertTrue(errorFor(HttpStatusCode.TooManyRequests) is AppError.RateLimited)
    }

    @Test
    fun maps_5xx_to_Server_carrying_the_code() = runTest {
        val error = errorFor(HttpStatusCode.ServiceUnavailable)
        assertTrue(error is AppError.Server)
        assertEquals(503, (error as AppError.Server).code)
    }

    @Test
    fun decodes_the_error_envelope_message_and_field_errors() = runTest {
        val body = """{"error":{"code":"invalid","message":"Bad phone","details":{"phone":["required"]}}}"""
        val error = errorFor(HttpStatusCode.UnprocessableEntity, body) as AppError.Validation
        assertEquals("Bad phone", error.message)
        assertEquals(listOf("required"), error.fieldErrors["phone"])
    }

    private suspend fun errorFor(status: HttpStatusCode, body: String = ""): AppError {
        val result = safeApiCall { clientReturning(status, body).get("x") }
        return (result as DataResult.Failure).error
    }
}
