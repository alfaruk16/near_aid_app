package com.nearaid.core.common.result

/**
 * Domain-level error model. The data layer maps transport-specific failures
 * (HTTP codes, IO exceptions, serialization errors) into one of these so the
 * rest of the app never depends on Retrofit/OkHttp types. (§9.1 error envelope.)
 */
sealed class AppError(open val message: String?) {
    /** No connectivity / socket timeout / DNS failure. */
    data class Network(override val message: String? = null) : AppError(message)

    /** 401 — token missing or expired. */
    data class Unauthorized(override val message: String? = null) : AppError(message)

    /** 403 — authenticated but not allowed. */
    data class Forbidden(override val message: String? = null) : AppError(message)

    /** 404 — requested resource does not exist. */
    data class NotFound(override val message: String? = null) : AppError(message)

    /** 400 / 422 — request rejected, optionally with per-field messages. */
    data class Validation(
        override val message: String? = null,
        val fieldErrors: Map<String, List<String>> = emptyMap(),
    ) : AppError(message)

    /** 409 — conflict (e.g. listing already claimed). */
    data class Conflict(override val message: String? = null) : AppError(message)

    /** 429 — rate limited. */
    data class RateLimited(override val message: String? = null) : AppError(message)

    /** 5xx — server-side failure. */
    data class Server(val code: Int, override val message: String? = null) : AppError(message)

    /** Anything we could not classify. */
    data class Unknown(override val message: String? = null) : AppError(message)
}
