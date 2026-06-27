package com.nearaid.core.data.mapper

import com.nearaid.core.network.dto.AuthorDto
import com.nearaid.core.network.dto.CategoryDto
import com.nearaid.core.network.dto.CategoryRefDto
import com.nearaid.core.network.dto.ClaimDto
import com.nearaid.core.network.dto.ConversationDto
import com.nearaid.core.network.dto.GeoDto
import com.nearaid.core.network.dto.ListingCardDto
import com.nearaid.core.network.dto.ListingDetailDto
import com.nearaid.core.network.dto.ListingImageDto
import com.nearaid.core.network.dto.MeDto
import com.nearaid.core.network.dto.MessageDto
import com.nearaid.core.network.dto.NotificationDto
import com.nearaid.core.network.dto.PublicUserDto
import com.nearaid.core.network.dto.RatingDto
import com.nearaid.core.model.AccountStatus
import com.nearaid.core.model.AppLanguage
import com.nearaid.core.model.Author
import com.nearaid.core.model.Category
import com.nearaid.core.model.ChatMessage
import com.nearaid.core.model.Claim
import com.nearaid.core.model.ClaimRole
import com.nearaid.core.model.ClaimStatus
import com.nearaid.core.model.Conversation
import com.nearaid.core.model.GeoPoint
import com.nearaid.core.model.ListingCard
import com.nearaid.core.model.ListingDetail
import com.nearaid.core.model.ListingImage
import com.nearaid.core.model.ListingStatus
import com.nearaid.core.model.ListingType
import com.nearaid.core.model.Me
import com.nearaid.core.model.MessageType
import com.nearaid.core.model.NotificationItem
import com.nearaid.core.model.PublicUser
import com.nearaid.core.model.Rating

fun String?.toListingType(): ListingType =
    if (this == "offer") ListingType.OFFER else ListingType.REQUEST

fun String?.toUrgency() = when (this) {
    "low" -> com.nearaid.core.model.Urgency.LOW
    "medium" -> com.nearaid.core.model.Urgency.MEDIUM
    "high" -> com.nearaid.core.model.Urgency.HIGH
    "critical" -> com.nearaid.core.model.Urgency.CRITICAL
    else -> null
}

fun String?.toListingStatus(): ListingStatus = when (this) {
    "claimed" -> ListingStatus.CLAIMED
    "delivered" -> ListingStatus.DELIVERED
    "completed" -> ListingStatus.COMPLETED
    "cancelled" -> ListingStatus.CANCELLED
    "expired" -> ListingStatus.EXPIRED
    else -> ListingStatus.OPEN
}

fun String?.toClaimStatus(): ClaimStatus = when (this) {
    "withdrawn" -> ClaimStatus.WITHDRAWN
    "completed" -> ClaimStatus.COMPLETED
    "cancelled" -> ClaimStatus.CANCELLED
    else -> ClaimStatus.ACTIVE
}

fun GeoDto.toDomain() = GeoPoint(lat, lng)

fun AuthorDto.toDomain() = Author(
    id = id,
    displayName = displayName,
    photoUrl = photoUrl,
    trustScore = trustScore,
    isIdVerified = isIdVerified,
)

fun CategoryDto.toDomain() = Category(id, key, nameEn, nameBn, icon)

fun CategoryRefDto.toDomain() = Category(
    id = id,
    key = key,
    nameEn = nameEn ?: key.replaceFirstChar { it.uppercase() },
    nameBn = nameBn ?: "",
    icon = icon,
)

fun ListingImageDto.toDomain() = ListingImage(id, url, thumbnailUrl)

fun ListingCardDto.toDomain() = ListingCard(
    id = id,
    type = type.toListingType(),
    title = title,
    category = category?.toDomain(),
    urgency = urgency.toUrgency(),
    availableUntil = availableUntil,
    quantity = quantity,
    distanceKm = distanceKm,
    areaLabel = areaLabel,
    locationFuzzed = locationFuzzed?.toDomain(),
    thumbnailUrl = thumbnailUrl,
    author = author.toDomain(),
    status = status.toListingStatus(),
    createdAt = createdAt,
)

fun ListingDetailDto.toDomain() = ListingDetail(
    id = id,
    type = type.toListingType(),
    status = status.toListingStatus(),
    title = title,
    description = description,
    quantity = quantity,
    category = category?.toDomain(),
    urgency = urgency.toUrgency(),
    availableUntil = availableUntil,
    areaLabel = areaLabel,
    locationFuzzed = locationFuzzed?.toDomain(),
    locationExact = locationExact?.toDomain(),
    images = images.map { it.toDomain() },
    author = author.toDomain(),
    expiresAt = expiresAt,
    createdAt = createdAt,
)

fun ClaimDto.toDomain() = Claim(
    id = claimId ?: id.orEmpty(),
    listingId = listingId,
    listingType = listingType.toListingType(),
    status = status.toClaimStatus(),
    chatThreadId = chatThreadId,
    claimedAt = claimedAt,
)

fun MeDto.toDomain() = Me(
    id = id,
    phone = phone,
    email = email,
    displayName = displayName,
    photoUrl = photoUrl,
    language = AppLanguage.fromCode(language),
    defaultArea = defaultArea,
    isPhoneVerified = isPhoneVerified,
    isIdVerified = isIdVerified,
    trustScore = trustScore,
    status = when (status) {
        "suspended" -> AccountStatus.SUSPENDED
        "banned" -> AccountStatus.BANNED
        else -> AccountStatus.ACTIVE
    },
)

fun PublicUserDto.toDomain() = PublicUser(
    id = id,
    displayName = displayName,
    photoUrl = photoUrl,
    trustScore = trustScore,
    isIdVerified = isIdVerified,
    aggregateRating = aggregateRating,
    completedHelpCount = completedHelpCount,
)

fun RatingDto.toDomain() = Rating(id, raterName, raterPhotoUrl, score, comment, createdAt)

fun MessageDto.toDomain() = ChatMessage(
    id = id,
    senderId = senderId,
    type = if (type == "image") MessageType.IMAGE else MessageType.TEXT,
    body = body,
    imageUrl = imageUrl,
    createdAt = createdAt,
    readAt = readAt,
)

fun ConversationDto.toDomain(): Conversation = Conversation(
    threadId = threadId,
    claimId = claimId,
    listingId = listing.id,
    listingType = listing.type.toListingType(),
    listingTitle = listing.title,
    listingStatus = (listingStatus ?: listing.status).toListingStatus(),
    counterpart = counterpart.toDomain(),
    role = when (role) {
        "helping" -> ClaimRole.HELPING
        "giving" -> ClaimRole.GIVING
        "requesting" -> ClaimRole.REQUESTING
        else -> ClaimRole.RECEIVING
    },
    lastMessageBody = lastMessage?.body,
    lastMessageAt = lastMessage?.createdAt,
    unreadCount = unreadCount,
)

fun NotificationDto.toDomain() = NotificationItem(
    id = id,
    type = type,
    title = title,
    body = body,
    createdAt = createdAt,
    isRead = readAt != null,
    deeplink = deeplink,
)
