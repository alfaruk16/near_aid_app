package com.nearaid.core.network.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/** Captures each outgoing request so tests can assert method/path/params/body. */
class Recorder {
    val requests = mutableListOf<HttpRequestData>()
    val last: HttpRequestData get() = requests.last()
    val lastPath: String get() = last.url.encodedPath
    val lastMethod: String get() = last.method.value
    fun param(name: String): String? = last.url.parameters[name]
    val lastBody: String get() = (last.body as TextContent).text
}

/** Builds an [HttpClient] whose every request returns [responseBody] as JSON with [status]. */
fun mockClient(
    recorder: Recorder = Recorder(),
    status: HttpStatusCode = HttpStatusCode.OK,
    responseBody: String = "",
): HttpClient {
    val engine = MockEngine { request ->
        recorder.requests += request
        respond(
            content = responseBody,
            status = status,
            headers = headersOf(HttpHeaders.ContentType, "application/json"),
        )
    }
    return HttpClient(engine) {
        expectSuccess = true
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    }
}
