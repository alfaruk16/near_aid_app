package com.nearaid.core.network.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.nearaid.core.network.BuildConfig
import com.nearaid.core.network.api.AuthApi
import com.nearaid.core.network.api.CategoryApi
import com.nearaid.core.network.api.ChatApi
import com.nearaid.core.network.api.ClaimApi
import com.nearaid.core.network.api.ListingApi
import com.nearaid.core.network.api.NotificationApi
import com.nearaid.core.network.api.SafetyApi
import com.nearaid.core.network.api.UserApi
import com.nearaid.core.network.interceptor.AuthInterceptor
import com.nearaid.core.network.interceptor.TokenAuthenticator
import com.nearaid.core.network.socket.ChatSocket
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.TimeUnit

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
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
            redactHeader("Authorization")
            redactHeader("Cookie")
        }
    }

    singleOf(::AuthInterceptor)

    // AuthApi is resolved lazily to break the OkHttp ⇄ Retrofit ⇄ AuthApi cycle.
    single { TokenAuthenticator(get()) { get<AuthApi>() } }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .authenticator(get<TokenAuthenticator>())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(get<NetworkConfig>().baseUrl)
            .client(get())
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
    }

    single<AuthApi> { get<Retrofit>().create() }
    single<UserApi> { get<Retrofit>().create() }
    single<CategoryApi> { get<Retrofit>().create() }
    single<ListingApi> { get<Retrofit>().create() }
    single<ClaimApi> { get<Retrofit>().create() }
    single<ChatApi> { get<Retrofit>().create() }
    single<SafetyApi> { get<Retrofit>().create() }
    single<NotificationApi> { get<Retrofit>().create() }

    single { ChatSocket(get(), get(), get(), get<NetworkConfig>().wsUrl) }
}
