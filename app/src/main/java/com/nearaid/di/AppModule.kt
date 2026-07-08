package com.nearaid.di

import com.nearaid.BuildConfig
import com.nearaid.core.network.di.NetworkConfig
import com.nearaid.shared.MainViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** App-level bindings — supplies values the core modules need but can't know themselves. */
val appModule = module {
    single {
        NetworkConfig(
            baseUrl = BuildConfig.BASE_URL,
            wsUrl = BuildConfig.WS_URL,
            debugLogging = BuildConfig.DEBUG,
        )
    }
    viewModelOf(::MainViewModel)
}
