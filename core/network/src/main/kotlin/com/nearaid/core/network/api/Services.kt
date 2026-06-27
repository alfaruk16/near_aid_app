package com.nearaid.core.network.api

import com.nearaid.core.network.dto.AuthResponse
import com.nearaid.core.network.dto.CancelBody
import com.nearaid.core.network.dto.CategoriesResponse
import com.nearaid.core.network.dto.ClaimDto
import com.nearaid.core.network.dto.ClaimsResponse
import com.nearaid.core.network.dto.ConversationsResponse
import com.nearaid.core.network.dto.CreateListingBody
import com.nearaid.core.network.dto.DeviceBody
import com.nearaid.core.network.dto.ListingDetailDto
import com.nearaid.core.network.dto.MeDto
import com.nearaid.core.network.dto.MessageDto
import com.nearaid.core.network.dto.MessagesResponse
import com.nearaid.core.network.dto.MyListingsResponse
import com.nearaid.core.network.dto.NearbyResponse
import com.nearaid.core.network.dto.NotificationsResponse
import com.nearaid.core.network.dto.OtpRequestBody
import com.nearaid.core.network.dto.OtpRequestResponse
import com.nearaid.core.network.dto.OtpVerifyBody
import com.nearaid.core.network.dto.PatchMeBody
import com.nearaid.core.network.dto.PublicUserDto
import com.nearaid.core.network.dto.RatingBody
import com.nearaid.core.network.dto.RatingDto
import com.nearaid.core.network.dto.ReportBody
import com.nearaid.core.network.dto.SendMessageBody
import com.nearaid.core.network.dto.TokenRefreshRequestDto
import com.nearaid.core.network.dto.TokenRefreshResponseDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthApi {
    @POST("auth/otp/request")
    suspend fun requestOtp(@Body body: OtpRequestBody): OtpRequestResponse

    @POST("auth/otp/verify")
    suspend fun verifyOtp(@Body body: OtpVerifyBody): AuthResponse

    @POST("auth/refresh")
    suspend fun refresh(@Body body: TokenRefreshRequestDto): TokenRefreshResponseDto

    @POST("auth/logout")
    suspend fun logout()
}

interface UserApi {
    @GET("me")
    suspend fun getMe(): MeDto

    @PATCH("me")
    suspend fun updateMe(@Body body: PatchMeBody): MeDto

    @GET("users/{id}")
    suspend fun getPublicUser(@Path("id") id: String): PublicUserDto

    @GET("users/{id}/ratings")
    suspend fun getRatings(
        @Path("id") id: String,
        @Query("cursor") cursor: String?,
    ): RatingsPage

    @POST("me/devices")
    suspend fun registerDevice(@Body body: DeviceBody)

    @Multipart
    @POST("me/verification")
    suspend fun submitVerification(@Part document: MultipartBody.Part)
}

interface CategoryApi {
    @GET("categories")
    suspend fun getCategories(): CategoriesResponse
}

interface ListingApi {
    @GET("listings/nearby")
    suspend fun getNearby(
        @Query("type") type: String,
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius_km") radiusKm: Double,
        @Query("category") categories: List<String>?,
        @Query("urgency") urgency: String?,
        @Query("q") query: String?,
        @Query("cursor") cursor: String?,
    ): NearbyResponse

    @GET("listings/{id}")
    suspend fun getListing(@Path("id") id: String): ListingDetailDto

    @POST("listings")
    suspend fun createListing(@Body body: CreateListingBody): ListingDetailDto

    @POST("listings/{id}/cancel")
    suspend fun cancelListing(@Path("id") id: String, @Body body: CancelBody)

    @POST("listings/{id}/claim")
    suspend fun claim(@Path("id") id: String): ClaimDto

    @GET("me/listings")
    suspend fun getMyListings(
        @Query("type") type: String,
        @Query("status") status: String?,
    ): MyListingsResponse
}

interface ClaimApi {
    @POST("claims/{id}/withdraw")
    suspend fun withdraw(@Path("id") id: String)

    @POST("claims/{id}/deliver")
    suspend fun deliver(@Path("id") id: String)

    @POST("claims/{id}/confirm")
    suspend fun confirm(@Path("id") id: String)

    @POST("claims/{id}/rating")
    suspend fun rate(@Path("id") id: String, @Body body: RatingBody)

    @GET("me/claims")
    suspend fun getMyClaims(@Query("status") status: String?): ClaimsResponse

    @GET("claims/{id}/messages")
    suspend fun getMessages(@Path("id") id: String, @Query("cursor") cursor: String?): MessagesResponse

    @POST("claims/{id}/messages")
    suspend fun sendMessage(@Path("id") id: String, @Body body: SendMessageBody): MessageDto

    @POST("claims/{id}/messages/read")
    suspend fun markRead(@Path("id") id: String)
}

interface ChatApi {
    @GET("me/conversations")
    suspend fun getConversations(@Query("cursor") cursor: String?): ConversationsResponse
}

interface SafetyApi {
    @POST("reports")
    suspend fun report(@Body body: ReportBody)

    @POST("blocks")
    suspend fun block(@Body body: com.nearaid.core.network.dto.BlockBody)

    @DELETE("blocks/{id}")
    suspend fun unblock(@Path("id") userId: String)

    @GET("me/blocks")
    suspend fun getBlocked(): BlockedResponse
}

interface NotificationApi {
    @GET("me/notifications")
    suspend fun getNotifications(): NotificationsResponse

    @POST("me/notifications/read")
    suspend fun markAllRead()
}

// Lightweight wrappers for paginated/list-only endpoints not given a dedicated schema.
@kotlinx.serialization.Serializable
data class RatingsPage(
    val results: List<RatingDto> = emptyList(),
    @kotlinx.serialization.SerialName("next_cursor") val nextCursor: String? = null,
    @kotlinx.serialization.SerialName("has_more") val hasMore: Boolean = false,
)

@kotlinx.serialization.Serializable
data class BlockedResponse(val results: List<PublicUserDto> = emptyList())
