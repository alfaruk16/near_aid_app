package com.nearaid.core.data.mapper

import com.nearaid.core.database.entity.CachedConversationEntity
import com.nearaid.core.database.entity.CachedListingEntity
import com.nearaid.core.model.Author
import com.nearaid.core.model.Category
import com.nearaid.core.model.ClaimRole
import com.nearaid.core.model.Conversation
import com.nearaid.core.model.ListingCard
import com.nearaid.core.model.ListingStatus
import com.nearaid.core.model.ListingType
import com.nearaid.core.model.Urgency

fun ListingCard.toEntity(feedType: String) = CachedListingEntity(
    id = id,
    feedType = feedType,
    type = type.name,
    title = title,
    categoryKey = category?.key,
    categoryNameEn = category?.nameEn,
    categoryNameBn = category?.nameBn,
    urgency = urgency?.name,
    availableUntil = availableUntil,
    quantity = quantity,
    distanceKm = distanceKm,
    areaLabel = areaLabel,
    thumbnailUrl = thumbnailUrl,
    authorId = author.id,
    authorName = author.displayName,
    authorPhoto = author.photoUrl,
    authorTrust = author.trustScore,
    authorVerified = author.isIdVerified,
    status = status.name,
    createdAt = createdAt,
)

fun CachedListingEntity.toDomain() = ListingCard(
    id = id,
    type = runCatching { ListingType.valueOf(type) }.getOrDefault(ListingType.REQUEST),
    title = title,
    category = categoryKey?.let { Category(0, it, categoryNameEn ?: it, categoryNameBn ?: "", null) },
    urgency = urgency?.let { runCatching { Urgency.valueOf(it) }.getOrNull() },
    availableUntil = availableUntil,
    quantity = quantity,
    distanceKm = distanceKm,
    areaLabel = areaLabel,
    locationFuzzed = null,
    thumbnailUrl = thumbnailUrl,
    author = Author(authorId, authorName, authorPhoto, authorTrust, authorVerified),
    status = runCatching { ListingStatus.valueOf(status) }.getOrDefault(ListingStatus.OPEN),
    createdAt = createdAt,
)

fun Conversation.toEntity() = CachedConversationEntity(
    threadId = threadId,
    claimId = claimId,
    listingId = listingId,
    listingType = listingType.name,
    listingTitle = listingTitle,
    listingStatus = listingStatus.name,
    counterpartId = counterpart.id,
    counterpartName = counterpart.displayName,
    counterpartPhoto = counterpart.photoUrl,
    counterpartTrust = counterpart.trustScore,
    counterpartVerified = counterpart.isIdVerified,
    role = role.name,
    lastMessageBody = lastMessageBody,
    lastMessageAt = lastMessageAt,
    unreadCount = unreadCount,
)

fun CachedConversationEntity.toDomain() = Conversation(
    threadId = threadId,
    claimId = claimId,
    listingId = listingId,
    listingType = runCatching { ListingType.valueOf(listingType) }.getOrDefault(ListingType.REQUEST),
    listingTitle = listingTitle,
    listingStatus = runCatching { ListingStatus.valueOf(listingStatus) }.getOrDefault(ListingStatus.OPEN),
    counterpart = Author(counterpartId, counterpartName, counterpartPhoto, counterpartTrust, counterpartVerified),
    role = runCatching { ClaimRole.valueOf(role) }.getOrDefault(ClaimRole.RECEIVING),
    lastMessageBody = lastMessageBody,
    lastMessageAt = lastMessageAt,
    unreadCount = unreadCount,
)
