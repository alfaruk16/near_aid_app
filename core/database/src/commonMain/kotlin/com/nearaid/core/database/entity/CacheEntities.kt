package com.nearaid.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_listings")
data class CachedListingEntity(
    @PrimaryKey val id: String,
    val feedType: String,
    val type: String,
    val title: String,
    val categoryKey: String?,
    val categoryNameEn: String?,
    val categoryNameBn: String?,
    val urgency: String?,
    val availableUntil: String?,
    val quantity: String?,
    val distanceKm: Double?,
    val areaLabel: String?,
    val thumbnailUrl: String?,
    val authorId: String,
    val authorName: String?,
    val authorPhoto: String?,
    val authorTrust: Double?,
    val authorVerified: Boolean,
    val status: String,
    val createdAt: String,
)

@Entity(tableName = "cached_conversations")
data class CachedConversationEntity(
    @PrimaryKey val threadId: String,
    val claimId: String,
    val listingId: String,
    val listingType: String,
    val listingTitle: String,
    val listingStatus: String,
    val counterpartId: String,
    val counterpartName: String?,
    val counterpartPhoto: String?,
    val counterpartTrust: Double?,
    val counterpartVerified: Boolean,
    val role: String,
    val lastMessageBody: String?,
    val lastMessageAt: String?,
    val unreadCount: Int,
)
