package com.nearaid.core.model

data class Category(
    val id: Int,
    val key: String,
    val nameEn: String,
    val nameBn: String,
    val icon: String?,
)

/** Compact author/counterpart block embedded in listings, claims and chat. */
data class Author(
    val id: String,
    val displayName: String?,
    val photoUrl: String?,
    val trustScore: Double?,
    val isIdVerified: Boolean,
)

data class GeoPoint(val lat: Double, val lng: Double)

/** A discovery-feed card (§9.5 GET /listings/nearby) — fuzzed point + banded distance only. */
data class ListingCard(
    val id: String,
    val type: ListingType,
    val title: String,
    val category: Category?,
    val urgency: Urgency?,
    val availableUntil: String?,
    val quantity: String?,
    val distanceKm: Double?,
    val areaLabel: String?,
    val locationFuzzed: GeoPoint?,
    val thumbnailUrl: String?,
    val author: Author,
    val status: ListingStatus,
    val createdAt: String,
)

data class ListingImage(
    val id: String,
    val url: String,
    val thumbnailUrl: String?,
)

/** Full detail (§9.5 GET /listings/{id}). Exact location is present only for owner/counterpart. */
data class ListingDetail(
    val id: String,
    val type: ListingType,
    val status: ListingStatus,
    val title: String,
    val description: String,
    val quantity: String?,
    val category: Category?,
    val urgency: Urgency?,
    val availableUntil: String?,
    val areaLabel: String?,
    val locationFuzzed: GeoPoint?,
    val locationExact: GeoPoint?,
    val images: List<ListingImage>,
    val author: Author,
    val expiresAt: String?,
    val createdAt: String,
)

/** Input for POST /listings (§9.5). Requests carry urgency; offers carry availableUntil. */
data class NewListing(
    val type: ListingType,
    val categoryId: Int,
    val title: String,
    val description: String,
    val quantity: String?,
    val urgency: Urgency?,
    val availableUntil: String?,
    val lat: Double,
    val lng: Double,
    val areaLabel: String?,
    val imageUrls: List<String> = emptyList(),
)

data class Claim(
    val id: String,
    val listingId: String,
    val listingType: ListingType,
    val status: ClaimStatus,
    val chatThreadId: String?,
    val claimedAt: String?,
)

data class Me(
    val id: String,
    val phone: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val language: AppLanguage,
    val defaultArea: String?,
    val isPhoneVerified: Boolean,
    val isIdVerified: Boolean,
    val trustScore: Double,
    val status: AccountStatus,
)

/** Public profile (§9.3 GET /users/{id}) — no phone, no location. */
data class PublicUser(
    val id: String,
    val displayName: String?,
    val photoUrl: String?,
    val trustScore: Double,
    val isIdVerified: Boolean,
    val aggregateRating: Double?,
    val completedHelpCount: Int?,
)

data class Rating(
    val id: String,
    val raterName: String?,
    val raterPhotoUrl: String?,
    val score: Int,
    val comment: String?,
    val createdAt: String,
)

data class ChatMessage(
    val id: String,
    val senderId: String,
    val type: MessageType,
    val body: String?,
    val imageUrl: String?,
    val createdAt: String,
    val readAt: String?,
)

/** A Messages-tab row (§9.7 GET /me/conversations, FR-MSG-1). */
data class Conversation(
    val threadId: String,
    val claimId: String,
    val listingId: String,
    val listingType: ListingType,
    val listingTitle: String,
    val listingStatus: ListingStatus,
    val counterpart: Author,
    val role: ClaimRole,
    val lastMessageBody: String?,
    val lastMessageAt: String?,
    val unreadCount: Int,
)

data class NotificationItem(
    val id: String,
    val type: String,
    val title: String,
    val body: String,
    val createdAt: String,
    val isRead: Boolean,
    val deeplink: String?,
)

data class Page<T>(
    val items: List<T>,
    val nextCursor: String?,
    val hasMore: Boolean,
)

/** Filters applied to the discovery feed (§9.5 query params). */
data class DiscoveryQuery(
    val type: ListingType,
    val lat: Double,
    val lng: Double,
    val radiusKm: Double = 5.0,
    val categories: List<String> = emptyList(),
    val urgency: Urgency? = null,
    val query: String? = null,
)

data class AuthSession(
    val accessToken: String,
    val refreshToken: String,
    val isNewUser: Boolean,
    val userId: String,
)

data class OtpChallenge(
    val requestId: String,
    val expiresInSeconds: Int,
)
