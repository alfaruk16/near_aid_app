package com.nearaid.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nearaid.core.database.entity.CachedConversationEntity
import com.nearaid.core.database.entity.CachedListingEntity

@Dao
interface ListingCacheDao {
    @Query("SELECT * FROM cached_listings WHERE feedType = :type ORDER BY distanceKm")
    suspend fun getByType(type: String): List<CachedListingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CachedListingEntity>)

    @Query("DELETE FROM cached_listings WHERE feedType = :type")
    suspend fun clearByType(type: String)
}

@Dao
interface ConversationCacheDao {
    @Query("SELECT * FROM cached_conversations ORDER BY lastMessageAt DESC")
    suspend fun getAll(): List<CachedConversationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CachedConversationEntity>)

    @Query("DELETE FROM cached_conversations")
    suspend fun clear()
}
