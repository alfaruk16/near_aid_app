package com.nearaid.shared

import com.nearaid.core.common.di.commonModule
import com.nearaid.core.data.di.dataModule
import com.nearaid.core.database.di.databaseModule
import com.nearaid.core.datastore.di.dataStoreModule
import com.nearaid.core.domain.di.domainModule
import com.nearaid.core.network.di.NetworkConfig
import com.nearaid.core.network.di.networkModule
import com.nearaid.feature.activity.di.activityModule
import com.nearaid.feature.auth.di.authModule
import com.nearaid.feature.discovery.di.discoveryModule
import com.nearaid.feature.messages.di.messagesModule
import com.nearaid.feature.post.di.postModule
import com.nearaid.feature.profile.di.profileModule
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * Starts the shared Koin graph on iOS. Mirrors the Android app's `startKoin` wiring, minus the
 * Android-only bits: `NetworkConfig` is supplied here (Android reads it from `BuildConfig`), and the
 * DataStore/Room platform modules resolve their iOS `actual`s automatically — no `androidContext`.
 *
 * Called once from Swift (`KoinKt.doInitKoin(...)`) at app launch.
 */
fun doInitKoin(baseUrl: String, wsUrl: String) {
    startKoin {
        modules(
            module {
                single { NetworkConfig(baseUrl = baseUrl, wsUrl = wsUrl, debugLogging = false) }
            },
            commonModule,
            networkModule,
            dataStoreModule,
            databaseModule,
            dataModule,
            domainModule,
            authModule,
            discoveryModule,
            postModule,
            activityModule,
            messagesModule,
            profileModule,
        )
    }
}
