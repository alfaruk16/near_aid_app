package com.nearaid.core.model

/** The JWT pair issued by /auth/otp/verify and refreshed via /auth/refresh (§9.2). */
data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
)
