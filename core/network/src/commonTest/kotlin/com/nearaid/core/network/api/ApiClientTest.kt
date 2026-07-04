package com.nearaid.core.network.api

import com.nearaid.core.network.dto.OtpRequestBody
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Guards that JSON bodies are actually serialized with a Content-Type (regression: Ktor
 *  refuses to serialize a `setBody(dto)` unless the request Content-Type is set). */
class ApiClientTest {

    @Test
    fun requestOtp_serializes_json_body_and_parses_response() = runTest {
        var contentType: String? = null
        var bodyText: String? = null

        val engine = MockEngine { request ->
            contentType = request.body.contentType?.toString()
            bodyText = (request.body as? TextContent)?.text
            respond(
                content = """{"request_id":"abc","expires_in":120}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = HttpClient(engine) {
            expectSuccess = true
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }

        val response = AuthApi(client).requestOtp(OtpRequestBody(phone = "+8801712345678"))

        assertEquals("abc", response.requestId)
        assertTrue(contentType?.contains("application/json") == true, "Content-Type was $contentType")
        assertTrue(bodyText?.contains("+8801712345678") == true, "body was $bodyText")
    }
}
