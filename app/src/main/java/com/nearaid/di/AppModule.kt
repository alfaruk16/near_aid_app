package com.nearaid.di

import com.nearaid.BuildConfig
import com.nearaid.core.network.di.BaseUrl
import com.nearaid.core.network.di.WsUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** App-level bindings — supplies values the core modules need but can't know themselves. */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @BaseUrl
    fun provideBaseUrl(): String = BuildConfig.BASE_URL

    @Provides
    @Singleton
    @WsUrl
    fun provideWsUrl(): String = BuildConfig.WS_URL
}
