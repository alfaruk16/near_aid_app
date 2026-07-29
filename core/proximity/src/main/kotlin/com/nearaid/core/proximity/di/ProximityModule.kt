package com.nearaid.core.proximity.di

import android.content.Context
import com.nearaid.core.proximity.BleProximityConfirmer
import com.nearaid.core.proximity.ProximityConfirmer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds the platform [ProximityConfirmer] — real BLE on Android. Supplied here because the radio
 * APIs (`BluetoothLeAdvertiser`/`BluetoothLeScanner`) need the application [Context].
 */
@Module
@InstallIn(SingletonComponent::class)
object ProximityModule {

    @Provides
    @Singleton
    fun provideProximityConfirmer(
        @ApplicationContext context: Context,
    ): ProximityConfirmer = BleProximityConfirmer(context)
}
