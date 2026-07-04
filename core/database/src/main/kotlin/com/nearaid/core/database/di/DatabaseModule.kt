package com.nearaid.core.database.di

import androidx.room.Room
import com.nearaid.core.database.NearAidDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(androidContext(), NearAidDatabase::class.java, "nearaid.db")
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<NearAidDatabase>().listingCacheDao() }
    single { get<NearAidDatabase>().conversationCacheDao() }
}
