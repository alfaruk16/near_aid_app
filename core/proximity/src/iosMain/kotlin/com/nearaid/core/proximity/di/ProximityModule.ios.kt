package com.nearaid.core.proximity.di

import com.nearaid.core.proximity.IosProximityConfirmer
import com.nearaid.core.proximity.ProximityConfirmer
import org.koin.core.module.Module
import org.koin.dsl.module

actual val proximityPlatformModule: Module = module {
    single<ProximityConfirmer> { IosProximityConfirmer() }
}
