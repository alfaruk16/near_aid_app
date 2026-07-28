package com.nearaid

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.nearaid.core.common.di.commonModule
import com.nearaid.core.data.di.dataModule
import com.nearaid.core.database.di.databaseModule
import com.nearaid.core.datastore.di.dataStoreModule
import com.nearaid.core.domain.di.domainModule
import com.nearaid.core.network.di.networkModule
import com.nearaid.core.proximity.di.proximityModule
import com.nearaid.di.appModule
import com.nearaid.feature.activity.di.activityModule
import com.nearaid.feature.auth.di.authModule
import com.nearaid.feature.discovery.di.discoveryModule
import com.nearaid.feature.messages.di.messagesModule
import com.nearaid.feature.post.di.postModule
import com.nearaid.feature.profile.di.profileModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class NearAidApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@NearAidApplication)
            modules(
                commonModule,
                networkModule,
                dataStoreModule,
                databaseModule,
                dataModule,
                domainModule,
                proximityModule,
                appModule,
                authModule,
                discoveryModule,
                postModule,
                activityModule,
                messagesModule,
                profileModule,
            )
        }
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "NearAid alerts",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Nearby needs, offers, claims and messages"
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "nearaid_alerts"
    }
}
