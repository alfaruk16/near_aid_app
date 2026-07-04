package com.nearaid.core.network.util

import com.nearaid.core.common.result.AppError
import com.nearaid.core.common.result.DataResult
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/**
 * Wraps a suspending Ktor call, translating transport failures into a [DataResult.Failure]
 * carrying a domain [AppError]. Decodes NearAid's consistent error envelope
 * `{"error":{code,message,details}}` (§9.1). Cancellation is rethrown so structured
 * concurrency keeps working.
 */
suspend inline fun <T> safeApiCall(crossinline block: suspend () -> T): DataResult<T> {
    return try {
        DataResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: ResponseException) {
        DataResult.Failure(e.response.toAppError())
    } catch (e: SerializationException) {
        DataResult.Failure(AppError.Unknown(e.message))
    } catch (e: Throwable) {
        // Connectivity / timeout / DNS — no HTTP response was received.
        DataResult.Failure(AppError.Network(e.message))
    }
}

suspend fun HttpResponse.toAppError(): AppError {
    val raw = runCatching { bodyAsText() }.getOrNull()
    val body = raw?.let { runCatching { errorJson.decodeFromString<ErrorEnvelope>(it).error }.getOrNull() }
    val message = body?.message ?: status.description
    val fieldErrors = body?.details.orEmpty()
    return when (val code = status.value) {
        400, 422 -> AppError.Validation(message = message, fieldErrors = fieldErrors)
        401 -> AppError.Unauthorized(message = message)
        403 -> AppError.Forbidden(message = message)
        404 -> AppError.NotFound(message = message)
        409 -> AppError.Conflict(message = message)
        429 -> AppError.RateLimited(message = message)
        in 500..599 -> AppError.Server(code = code, message = message)
        else -> AppError.Unknown(message = message)
    }
}

@kotlinx.serialization.Serializable
private data class ErrorEnvelope(val error: ErrorBody? = null)

@kotlinx.serialization.Serializable
private data class ErrorBody(
    val code: String? = null,
    val message: String? = null,
    val details: Map<String, List<String>> = emptyMap(),
)

private val errorJson = Json { ignoreUnknownKeys = true; coerceInputValues = true }
