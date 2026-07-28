package com.nearaid.core.proximity.di

import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Binds the platform [com.nearaid.core.proximity.ProximityConfirmer] — real BLE on Android,
 * a deferred stub on iOS (see the module KDoc). Supplied per-platform because the radio APIs
 * (Android `BluetoothLeAdvertiser`/`BluetoothLeScanner`, iOS CoreBluetooth) have no common type.
 */
expect val proximityPlatformModule: Module

/** Aggregate proximity module; add to the app/shared Koin graph. */
val proximityModule: Module = module {
    includes(proximityPlatformModule)
}
