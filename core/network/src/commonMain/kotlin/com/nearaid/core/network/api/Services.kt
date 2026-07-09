package com.nearaid.core.network.api

import com.nearaid.core.network.dto.AuthResponse
import com.nearaid.core.network.dto.BlockBody
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
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class AuthApi(private val client: HttpClient) {
    suspend fun requestOtp(body: OtpRequestBody): OtpRequestResponse =
        client.post("auth/otp/request") { contentType(ContentType.Application.Json); setBody(body) }.body()

    suspend fun verifyOtp(body: OtpVerifyBody): AuthResponse =
        client.post("auth/otp/verify") { contentType(ContentType.Application.Json); setBody(body) }.body()

    suspend fun refresh(body: TokenRefreshRequestDto): TokenRefreshResponseDto =
        client.post("auth/refresh") { contentType(ContentType.Application.Json); setBody(body) }.body()

    suspend fun logout() {
        client.post("auth/logout")
    }

    /**
     * Drops the Bearer plugin's in-memory token cache. Ktor caches tokens from [loadTokens] and only
     * re-reads them on a 401; without this, a logout→login as a different account keeps reusing the
     * previous user's still-valid access token, so requests like `getMe` return the old user's data.
     */
    fun clearAuthCache() {
        client.authProvider<BearerAuthProvider>()?.clearToken()
    }
}

class UserApi(private val client: HttpClient) {
    suspend fun getMe(): MeDto = client.get("me").body()

    suspend fun updateMe(body: PatchMeBody): MeDto =
        client.patch("me") { contentType(ContentType.Application.Json); setBody(body) }.body()

    suspend fun getPublicUser(id: String): PublicUserDto =
        client.get("users/$id").body()

    suspend fun getRatings(id: String, cursor: String?): RatingsPage =
        client.get("users/$id/ratings") { parameter("cursor", cursor) }.body()

    suspend fun registerDevice(body: DeviceBody) {
        client.post("me/devices") { contentType(ContentType.Application.Json); setBody(body) }
    }

    suspend fun submitVerification(bytes: ByteArray, fileName: String) {
        client.post("me/verification") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            key = "document",
                            value = bytes,
                            headers = Headers.build {
                                append(
                                    HttpHeaders.ContentDisposition,
                                    ContentDisposition.File.withParameter(
                                        ContentDisposition.Parameters.FileName,
                                        fileName,
                                    ).toString(),
                                )
                            },
                        )
                    },
                ),
            )
        }
    }
}

class CategoryApi(private val client: HttpClient) {
    suspend fun getCategories(): CategoriesResponse = client.get("categories").body()
}

class ListingApi(private val client: HttpClient) {
    suspend fun getNearby(
        type: String,
        lat: Double,
        lng: Double,
        radiusKm: Double,
        categories: List<String>?,
        urgency: String?,
        query: String?,
        cursor: String?,
    ): NearbyResponse = client.get("listings/nearby") {
        parameter("type", type)
        parameter("lat", lat)
        parameter("lng", lng)
        parameter("radius_km", radiusKm)
        categories?.forEach { parameter("category", it) }
        parameter("urgency", urgency)
        parameter("q", query)
        parameter("cursor", cursor)
    }.body()

    suspend fun getListing(id: String): ListingDetailDto =
        client.get("listings/$id").body()

    suspend fun createListing(body: CreateListingBody): ListingDetailDto =
        client.post("listings") { contentType(ContentType.Application.Json); setBody(body) }.body()

    suspend fun cancelListing(id: String, body: CancelBody) {
        client.post("listings/$id/cancel") { contentType(ContentType.Application.Json); setBody(body) }
    }

    suspend fun claim(id: String): ClaimDto =
        client.post("listings/$id/claim").body()

    suspend fun getMyListings(type: String, status: String?): MyListingsResponse =
        client.get("me/listings") {
            parameter("type", type)
            parameter("status", status)
        }.body()
}

class ClaimApi(private val client: HttpClient) {
    suspend fun withdraw(id: String) {
        client.post("claims/$id/withdraw")
    }

    suspend fun deliver(id: String) {
        client.post("claims/$id/deliver")
    }

    suspend fun confirm(id: String) {
        client.post("claims/$id/confirm")
    }

    suspend fun rate(id: String, body: RatingBody) {
        client.post("claims/$id/rating") { contentType(ContentType.Application.Json); setBody(body) }
    }

    suspend fun getMyClaims(status: String?): ClaimsResponse =
        client.get("me/claims") { parameter("status", status) }.body()

    suspend fun getMessages(id: String, cursor: String?): MessagesResponse =
        client.get("claims/$id/messages") { parameter("cursor", cursor) }.body()

    suspend fun sendMessage(id: String, body: SendMessageBody): MessageDto =
        client.post("claims/$id/messages") { contentType(ContentType.Application.Json); setBody(body) }.body()

    suspend fun markRead(id: String) {
        client.post("claims/$id/messages/read")
    }
}

class ChatApi(private val client: HttpClient) {
    suspend fun getConversations(cursor: String?): ConversationsResponse =
        client.get("me/conversations") { parameter("cursor", cursor) }.body()
}

class SafetyApi(private val client: HttpClient) {
    suspend fun report(body: ReportBody) {
        client.post("reports") { contentType(ContentType.Application.Json); setBody(body) }
    }

    suspend fun block(body: BlockBody) {
        client.post("blocks") { contentType(ContentType.Application.Json); setBody(body) }
    }

    suspend fun unblock(userId: String) {
        client.delete("blocks/$userId")
    }

    suspend fun getBlocked(): BlockedResponse = client.get("me/blocks").body()
}

class NotificationApi(private val client: HttpClient) {
    suspend fun getNotifications(): NotificationsResponse = client.get("me/notifications").body()

    suspend fun markAllRead() {
        client.post("me/notifications/read")
    }
}

// Lightweight wrappers for paginated/list-only endpoints not given a dedicated schema.
@Serializable
data class RatingsPage(
    val results: List<RatingDto> = emptyList(),
    @SerialName("next_cursor") val nextCursor: String? = null,
    @SerialName("has_more") val hasMore: Boolean = false,
)

@Serializable
data class BlockedResponse(val results: List<PublicUserDto> = emptyList())
