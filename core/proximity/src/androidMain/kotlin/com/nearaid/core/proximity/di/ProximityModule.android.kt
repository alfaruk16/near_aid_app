package com.nearaid.core.proximity.di

import com.nearaid.core.proximity.BleProximityConfirmer
import com.nearaid.core.proximity.ProximityConfirmer
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val proximityPlatformModule: Module = module {
    single<ProximityConfirmer> { BleProximityConfirmer(androidContext()) }
}
