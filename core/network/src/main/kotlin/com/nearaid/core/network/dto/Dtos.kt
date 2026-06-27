package com.nearaid.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ---- Auth (§9.2) ----
@Serializable
data class OtpRequestBody(val phone: String)

@Serializable
data class OtpRequestResponse(
    @SerialName("request_id") val requestId: String,
    @SerialName("expires_in") val expiresIn: Int = 120,
)

@Serializable
data class OtpVerifyBody(
    @SerialName("request_id") val requestId: String,
    val code: String,
)

@Serializable
data class AuthResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("is_new_user") val isNewUser: Boolean = false,
    val user: UserBriefDto,
)

@Serializable
data class UserBriefDto(
    val id: String,
    val phone: String? = null,
    @SerialName("display_name") val displayName: String? = null,
)

@Serializable
data class TokenRefreshRequestDto(val refresh: String)

@Serializable
data class TokenRefreshResponseDto(val access: String)

// ---- Users & profile (§9.3) ----
@Serializable
data class MeDto(
    val id: String,
    val phone: String,
    val email: String? = null,
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("photo_url") val photoUrl: String? = null,
    val language: String = "bn",
    @SerialName("default_area") val defaultArea: String? = null,
    @SerialName("is_phone_verified") val isPhoneVerified: Boolean = false,
    @SerialName("is_id_verified") val isIdVerified: Boolean = false,
    @SerialName("trust_score") val trustScore: Double = 50.0,
    val status: String = "active",
)

@Serializable
data class PatchMeBody(
    @SerialName("display_name") val displayName: String? = null,
    val language: String? = null,
    @SerialName("photo_url") val photoUrl: String? = null,
    @SerialName("default_area") val defaultArea: String? = null,
    val email: String? = null,
)

@Serializable
data class PublicUserDto(
    val id: String,
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("photo_url") val photoUrl: String? = null,
    @SerialName("trust_score") val trustScore: Double = 0.0,
    @SerialName("is_id_verified") val isIdVerified: Boolean = false,
    @SerialName("aggregate_rating") val aggregateRating: Double? = null,
    @SerialName("completed_help_count") val completedHelpCount: Int? = null,
)

@Serializable
data class RatingDto(
    val id: String,
    @SerialName("rater_name") val raterName: String? = null,
    @SerialName("rater_photo_url") val raterPhotoUrl: String? = null,
    val score: Int,
    val comment: String? = null,
    @SerialName("created_at") val createdAt: String = "",
)

@Serializable
data class DeviceBody(
    @SerialName("fcm_token") val fcmToken: String,
    val platform: String = "android",
)

// ---- Categories (§9.4) ----
@Serializable
data class CategoryDto(
    val id: Int,
    val key: String,
    @SerialName("name_en") val nameEn: String,
    @SerialName("name_bn") val nameBn: String,
    val icon: String? = null,
)

@Serializable
data class CategoriesResponse(val results: List<CategoryDto> = emptyList())

// ---- Listings (§9.5) ----
@Serializable
data class GeoDto(val lat: Double, val lng: Double)

@Serializable
data class AuthorDto(
    val id: String,
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("photo_url") val photoUrl: String? = null,
    @SerialName("trust_score") val trustScore: Double? = null,
    @SerialName("is_id_verified") val isIdVerified: Boolean = false,
)

@Serializable
data class CategoryRefDto(
    val id: Int = 0,
    val key: String,
    @SerialName("name_en") val nameEn: String? = null,
    @SerialName("name_bn") val nameBn: String? = null,
    val icon: String? = null,
)

@Serializable
data class ListingImageDto(
    val id: String,
    val url: String,
    @SerialName("thumbnail_url") val thumbnailUrl: String? = null,
)

@Serializable
data class ListingCardDto(
    val id: String,
    val type: String,
    val title: String,
    val category: CategoryRefDto? = null,
    val urgency: String? = null,
    @SerialName("available_until") val availableUntil: String? = null,
    val quantity: String? = null,
    @SerialName("distance_km") val distanceKm: Double? = null,
    @SerialName("area_label") val areaLabel: String? = null,
    @SerialName("location_fuzzed") val locationFuzzed: GeoDto? = null,
    @SerialName("thumbnail_url") val thumbnailUrl: String? = null,
    val author: AuthorDto,
    val status: String = "open",
    @SerialName("created_at") val createdAt: String = "",
)

@Serializable
data class ListingDetailDto(
    val id: String,
    val type: String,
    val status: String = "open",
    val title: String,
    val description: String = "",
    val quantity: String? = null,
    val category: CategoryRefDto? = null,
    val urgency: String? = null,
    @SerialName("available_until") val availableUntil: String? = null,
    @SerialName("area_label") val areaLabel: String? = null,
    @SerialName("location_fuzzed") val locationFuzzed: GeoDto? = null,
    @SerialName("location_exact") val locationExact: GeoDto? = null,
    val images: List<ListingImageDto> = emptyList(),
    val author: AuthorDto,
    @SerialName("expires_at") val expiresAt: String? = null,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String? = null,
)

@Serializable
data class CreateListingBody(
    val type: String,
    @SerialName("category_id") val categoryId: Int,
    val title: String,
    val description: String = "",
    val quantity: String = "",
    val urgency: String? = null,
    @SerialName("available_until") val availableUntil: String? = null,
    val lat: Double,
    val lng: Double,
    @SerialName("area_label") val areaLabel: String = "",
    @SerialName("image_urls") val imageUrls: List<String> = emptyList(),
)

@Serializable
data class CancelBody(val reason: String)

@Serializable
data class NearbyResponse(
    val results: List<ListingCardDto> = emptyList(),
    @SerialName("next_cursor") val nextCursor: String? = null,
    @SerialName("has_more") val hasMore: Boolean = false,
)

@Serializable
data class MyListingsResponse(val results: List<ListingCardDto> = emptyList())

// ---- Claims (§9.6) ----
@Serializable
data class ClaimDto(
    @SerialName("claim_id") val claimId: String? = null,
    val id: String? = null,
    @SerialName("listing_id") val listingId: String,
    @SerialName("listing_type") val listingType: String = "request",
    val status: String = "active",
    @SerialName("chat_thread_id") val chatThreadId: String? = null,
    @SerialName("claimed_at") val claimedAt: String? = null,
)

@Serializable
data class ClaimsResponse(val results: List<ClaimDto> = emptyList())

@Serializable
data class RatingBody(val score: Int, val comment: String? = null)

// ---- Chat & conversations (§9.7) ----
@Serializable
data class ConversationListingDto(
    val id: String,
    val type: String = "request",
    val title: String = "",
    val status: String = "open",
)

@Serializable
data class ConversationDto(
    @SerialName("thread_id") val threadId: String,
    @SerialName("claim_id") val claimId: String,
    val listing: ConversationListingDto,
    val counterpart: AuthorDto,
    val role: String = "",
    @SerialName("last_message") val lastMessage: LastMessageDto? = null,
    @SerialName("unread_count") val unreadCount: Int = 0,
    @SerialName("listing_status") val listingStatus: String? = null,
)

@Serializable
data class LastMessageDto(
    val body: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class ConversationsResponse(
    val results: List<ConversationDto> = emptyList(),
    @SerialName("next_cursor") val nextCursor: String? = null,
    @SerialName("has_more") val hasMore: Boolean = false,
)

@Serializable
data class MessageDto(
    val id: String,
    @SerialName("sender_id") val senderId: String,
    val type: String = "text",
    val body: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("read_at") val readAt: String? = null,
)

@Serializable
data class MessagesResponse(
    val results: List<MessageDto> = emptyList(),
    @SerialName("next_cursor") val nextCursor: String? = null,
    @SerialName("has_more") val hasMore: Boolean = false,
)

@Serializable
data class SendMessageBody(val type: String = "text", val body: String)

// ---- Safety (§9.9) ----
@Serializable
data class ReportBody(
    @SerialName("target_type") val targetType: String,
    @SerialName("target_id") val targetId: String,
    val reason: String,
)

@Serializable
data class BlockBody(@SerialName("user_id") val userId: String)

// ---- Notifications (§11) ----
@Serializable
data class NotificationDto(
    val id: String,
    val type: String = "",
    val title: String = "",
    val body: String = "",
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("read_at") val readAt: String? = null,
    val deeplink: String? = null,
)

@Serializable
data class NotificationsResponse(val results: List<NotificationDto> = emptyList())
