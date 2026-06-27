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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        explicitNulls = false
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
            redactHeader("Authorization")
            redactHeader("Cookie")
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
        logging: HttpLoggingInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .authenticator(tokenAuthenticator)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        @BaseUrl baseUrl: String,
        okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides @Singleton fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create()
    @Provides @Singleton fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create()
    @Provides @Singleton fun provideCategoryApi(retrofit: Retrofit): CategoryApi = retrofit.create()
    @Provides @Singleton fun provideListingApi(retrofit: Retrofit): ListingApi = retrofit.create()
    @Provides @Singleton fun provideClaimApi(retrofit: Retrofit): ClaimApi = retrofit.create()
    @Provides @Singleton fun provideChatApi(retrofit: Retrofit): ChatApi = retrofit.create()
    @Provides @Singleton fun provideSafetyApi(retrofit: Retrofit): SafetyApi = retrofit.create()
    @Provides @Singleton fun provideNotificationApi(retrofit: Retrofit): NotificationApi = retrofit.create()
}
