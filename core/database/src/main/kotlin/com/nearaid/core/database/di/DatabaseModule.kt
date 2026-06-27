package com.nearaid.core.database.di

import android.content.Context
import androidx.room.Room
import com.nearaid.core.database.NearAidDatabase
import com.nearaid.core.database.dao.ConversationCacheDao
import com.nearaid.core.database.dao.ListingCacheDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NearAidDatabase =
        Room.databaseBuilder(context, NearAidDatabase::class.java, "nearaid.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideListingCacheDao(db: NearAidDatabase): ListingCacheDao = db.listingCacheDao()

    @Provides
    fun provideConversationCacheDao(db: NearAidDatabase): ConversationCacheDao = db.conversationCacheDao()
}
