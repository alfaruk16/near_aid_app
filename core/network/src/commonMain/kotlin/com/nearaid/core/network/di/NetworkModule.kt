package com.nearaid.core.network.di

import com.nearaid.core.datastore.AuthPreferencesDataSource
import com.nearaid.core.network.api.AuthApi
import com.nearaid.core.network.api.CategoryApi
import com.nearaid.core.network.api.ChatApi
import com.nearaid.core.network.api.ClaimApi
import com.nearaid.core.network.api.ListingApi
import com.nearaid.core.network.api.NotificationApi
import com.nearaid.core.network.api.SafetyApi
import com.nearaid.core.network.api.UserApi
import com.nearaid.core.network.dto.TokenRefreshRequestDto
import com.nearaid.core.network.dto.TokenRefreshResponseDto
import com.nearaid.core.network.socket.ChatSocket
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {

    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            explicitNulls = false
            isLenient = true
        }
    }

    single {
        val json = get<Json>()
        val config = get<NetworkConfig>()
        val authPrefs = get<AuthPreferencesDataSource>()

        HttpClient {
            expectSuccess = true

            install(ContentNegotiation) { json(json) }

            install(DefaultRequest) { url(config.baseUrl) }

            install(WebSockets)

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("HTTP: $message")
                    }
                }
                level = if (config.debugLogging) LogLevel.ALL else LogLevel.NONE
                sanitizeHeader { header -> header == HttpHeaders.Authorization }
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        authPrefs.currentTokens()?.let { BearerTokens(it.accessToken, it.refreshToken) }
                    }
                    refreshTokens {
                        val current = authPrefs.currentTokens() ?: return@refreshTokens null
                        val refreshed = runCatching {
                            client.post("auth/refresh") {
                                contentType(ContentType.Application.Json)
                                setBody(TokenRefreshRequestDto(current.refreshToken))
                            }.body<TokenRefreshResponseDto>()
                        }.getOrNull()
                        if (refreshed != null) {
                            authPrefs.updateAccessToken(refreshed.access)
                            BearerTokens(refreshed.access, current.refreshToken)
                        } else {
                            authPrefs.clear()
                            null
                        }
                    }
                    sendWithoutRequest { request -> !request.url.pathSegments.contains("auth") }
                }
            }
        }
    }

    single { AuthApi(get()) }
    single { UserApi(get()) }
    single { CategoryApi(get()) }
    single { ListingApi(get()) }
    single { ClaimApi(get()) }
    single { ChatApi(get()) }
    single { SafetyApi(get()) }
    single { NotificationApi(get()) }

    single { ChatSocket(get(), get(), get(), get<NetworkConfig>().wsUrl) }
}
