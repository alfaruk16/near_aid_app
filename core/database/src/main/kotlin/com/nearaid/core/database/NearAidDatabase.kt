package com.nearaid.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nearaid.core.database.dao.ConversationCacheDao
import com.nearaid.core.database.dao.ListingCacheDao
import com.nearaid.core.database.entity.CachedConversationEntity
import com.nearaid.core.database.entity.CachedListingEntity

@Database(
    entities = [CachedListingEntity::class, CachedConversationEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class NearAidDatabase : RoomDatabase() {
    abstract fun listingCacheDao(): ListingCacheDao
    abstract fun conversationCacheDao(): ConversationCacheDao
}
