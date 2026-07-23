package com.nearaid.core.data.mapper

import com.nearaid.core.model.AccountStatus
import com.nearaid.core.model.AppLanguage
import com.nearaid.core.model.ClaimRole
import com.nearaid.core.model.ClaimStatus
import com.nearaid.core.model.ListingStatus
import com.nearaid.core.model.ListingType
import com.nearaid.core.model.MessageType
import com.nearaid.core.model.Urgency
import com.nearaid.core.network.dto.AuthorDto
import com.nearaid.core.network.dto.CategoryDto
import com.nearaid.core.network.dto.CategoryRefDto
import com.nearaid.core.network.dto.ClaimDto
import com.nearaid.core.network.dto.ConversationDto
import com.nearaid.core.network.dto.ConversationListingDto
import com.nearaid.core.network.dto.GeoDto
import com.nearaid.core.network.dto.LastMessageDto
import com.nearaid.core.network.dto.ListingCardDto
import com.nearaid.core.network.dto.ListingDetailDto
import com.nearaid.core.network.dto.ListingImageDto
import com.nearaid.core.network.dto.MeDto
import com.nearaid.core.network.dto.MessageDto
import com.nearaid.core.network.dto.NotificationDto
import com.nearaid.core.network.dto.PublicUserDto
import com.nearaid.core.network.dto.RatingDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MappersTest {

    // --- Enum string decoders ---

    @Test
    fun toListingType_maps_offer_and_defaults_everything_else_to_request() {
        assertEquals(ListingType.OFFER, "offer".toListingType())
        assertEquals(ListingType.REQUEST, "request".toListingType())
        assertEquals(ListingType.REQUEST, "garbage".toListingType())
        assertEquals(ListingType.REQUEST, (null as String?).toListingType())
    }

    @Test
    fun toUrgency_maps_all_levels_and_null_for_unknown() {
        assertEquals(Urgency.LOW, "low".toUrgency())
        assertEquals(Urgency.MEDIUM, "medium".toUrgency())
        assertEquals(Urgency.HIGH, "high".toUrgency())
        assertEquals(Urgency.CRITICAL, "critical".toUrgency())
        assertNull("whatever".toUrgency())
        assertNull((null as String?).toUrgency())
    }

    @Test
    fun toListingStatus_maps_all_states_and_defaults_to_open() {
        assertEquals(ListingStatus.CLAIMED, "claimed".toListingStatus())
        assertEquals(ListingStatus.DELIVERED, "delivered".toListingStatus())
        assertEquals(ListingStatus.COMPLETED, "completed".toListingStatus())
        assertEquals(ListingStatus.CANCELLED, "cancelled".toListingStatus())
        assertEquals(ListingStatus.EXPIRED, "expired".toListingStatus())
        assertEquals(ListingStatus.OPEN, "open".toListingStatus())
        assertEquals(ListingStatus.OPEN, (null as String?).toListingStatus())
    }

    @Test
    fun toClaimStatus_maps_states_and_defaults_to_active() {
        assertEquals(ClaimStatus.WITHDRAWN, "withdrawn".toClaimStatus())
        assertEquals(ClaimStatus.COMPLETED, "completed".toClaimStatus())
        assertEquals(ClaimStatus.CANCELLED, "cancelled".toClaimStatus())
        assertEquals(ClaimStatus.ACTIVE, "active".toClaimStatus())
        assertEquals(ClaimStatus.ACTIVE, (null as String?).toClaimStatus())
    }

    // --- Simple DTO mappers ---

    @Test
    fun geoDto_maps_lat_lng() {
        val geo = GeoDto(23.7, 90.4).toDomain()
        assertEquals(23.7, geo.lat)
        assertEquals(90.4, geo.lng)
    }

    @Test
    fun categoryDto_maps_all_fields() {
        val c = CategoryDto(1, "food", "Food", "খাবার", "icon").toDomain()
        assertEquals(1, c.id)
        assertEquals("food", c.key)
        assertEquals("Food", c.nameEn)
        assertEquals("খাবার", c.nameBn)
        assertEquals("icon", c.icon)
    }

    @Test
    fun categoryRefDto_falls_back_to_capitalised_key_and_empty_bn() {
        val c = CategoryRefDto(key = "medicine").toDomain()
        assertEquals("Medicine", c.nameEn)
        assertEquals("", c.nameBn)
    }

    @Test
    fun categoryRefDto_keeps_provided_names() {
        val c = CategoryRefDto(id = 2, key = "food", nameEn = "Food", nameBn = "খাবার").toDomain()
        assertEquals("Food", c.nameEn)
        assertEquals("খাবার", c.nameBn)
    }

    @Test
    fun meDto_maps_status_and_language() {
        assertEquals(AccountStatus.SUSPENDED, meDto(status = "suspended").toDomain().status)
        assertEquals(AccountStatus.BANNED, meDto(status = "banned").toDomain().status)
        assertEquals(AccountStatus.ACTIVE, meDto(status = "active").toDomain().status)
        assertEquals(AccountStatus.ACTIVE, meDto(status = "unknown").toDomain().status)
        assertEquals(AppLanguage.EN, meDto(language = "en").toDomain().language)
    }

    @Test
    fun claimDto_prefers_claimId_then_id_then_empty() {
        assertEquals("cid", claimDto(claimId = "cid", id = "other").toDomain().id)
        assertEquals("other", claimDto(claimId = null, id = "other").toDomain().id)
        assertEquals("", claimDto(claimId = null, id = null).toDomain().id)
    }

    @Test
    fun messageDto_decodes_image_type_else_text() {
        assertEquals(MessageType.IMAGE, message(type = "image").toDomain().type)
        assertEquals(MessageType.TEXT, message(type = "text").toDomain().type)
        assertEquals(MessageType.TEXT, message(type = "weird").toDomain().type)
    }

    @Test
    fun notificationDto_maps_isRead_from_readAt_presence() {
        assertEquals(true, notification(readAt = "2026-01-01").toDomain().isRead)
        assertEquals(false, notification(readAt = null).toDomain().isRead)
    }

    @Test
    fun listingCardDto_maps_nested_category_author_and_geo() {
        val card = ListingCardDto(
            id = "l1",
            type = "offer",
            title = "Rice",
            category = CategoryRefDto(key = "food"),
            urgency = "high",
            locationFuzzed = GeoDto(1.0, 2.0),
            author = AuthorDto(id = "u1", displayName = "Rahim"),
            status = "claimed",
        ).toDomain()
        assertEquals(ListingType.OFFER, card.type)
        assertEquals(Urgency.HIGH, card.urgency)
        assertEquals(ListingStatus.CLAIMED, card.status)
        assertEquals("food", card.category?.key)
        assertEquals(GeoDto(1.0, 2.0).toDomain(), card.locationFuzzed)
        assertEquals("u1", card.author.id)
    }

    @Test
    fun listingDetailDto_maps_images_and_both_geo_points() {
        val detail = ListingDetailDto(
            id = "l1",
            type = "request",
            title = "Rice",
            images = listOf(ListingImageDto("i1", "url", "thumb")),
            locationFuzzed = GeoDto(1.0, 2.0),
            locationExact = GeoDto(3.0, 4.0),
            author = AuthorDto(id = "u1"),
        ).toDomain()
        assertEquals(1, detail.images.size)
        assertEquals("i1", detail.images.first().id)
        assertEquals(GeoDto(1.0, 2.0).toDomain(), detail.locationFuzzed)
        assertEquals(GeoDto(3.0, 4.0).toDomain(), detail.locationExact)
    }

    @Test
    fun conversationDto_prefers_listingStatus_override_and_maps_role() {
        val base = conversation(role = "helping", listingStatus = "completed")
        val conv = base.toDomain()
        assertEquals(ClaimRole.HELPING, conv.role)
        assertEquals(ListingStatus.COMPLETED, conv.listingStatus)
        assertEquals("body", conv.lastMessageBody)
    }

    @Test
    fun conversationDto_falls_back_to_listing_status_when_no_override() {
        val conv = conversation(role = "unknown", listingStatus = null, listingStatusInner = "claimed").toDomain()
        assertEquals(ClaimRole.RECEIVING, conv.role)
        assertEquals(ListingStatus.CLAIMED, conv.listingStatus)
    }

    @Test
    fun conversationDto_maps_giving_and_requesting_roles() {
        assertEquals(ClaimRole.GIVING, conversation(role = "giving").toDomain().role)
        assertEquals(ClaimRole.REQUESTING, conversation(role = "requesting").toDomain().role)
    }

    @Test
    fun publicUserDto_and_ratingDto_map_fields() {
        val pu = PublicUserDto(id = "u2", displayName = "Karim", trustScore = 4.0).toDomain()
        assertEquals("u2", pu.id)
        assertEquals(4.0, pu.trustScore)

        val r = RatingDto(id = "r1", score = 5, comment = "great", createdAt = "2026").toDomain()
        assertEquals(5, r.score)
        assertEquals("great", r.comment)
    }

    @Test
    fun authorDto_maps_all_fields() {
        val a = AuthorDto(id = "u1", displayName = "Rahim", trustScore = 4.5, isIdVerified = true).toDomain()
        assertEquals("u1", a.id)
        assertEquals("Rahim", a.displayName)
        assertEquals(4.5, a.trustScore)
        assertEquals(true, a.isIdVerified)
    }

    // --- helpers ---

    private fun meDto(status: String = "active", language: String = "bn") = MeDto(
        id = "u1",
        phone = "+8801712345678",
        language = language,
        status = status,
    )

    private fun claimDto(claimId: String?, id: String?) = ClaimDto(
        claimId = claimId,
        id = id,
        listingId = "l1",
    )

    private fun message(type: String) = MessageDto(id = "m1", senderId = "u1", type = type)

    private fun notification(readAt: String?) = NotificationDto(id = "n1", readAt = readAt)

    private fun conversation(
        role: String = "",
        listingStatus: String? = null,
        listingStatusInner: String = "open",
    ) = ConversationDto(
        threadId = "t1",
        claimId = "c1",
        listing = ConversationListingDto(id = "l1", type = "offer", title = "Rice", status = listingStatusInner),
        counterpart = AuthorDto(id = "u2", displayName = "Karim"),
        role = role,
        lastMessage = LastMessageDto(body = "body", createdAt = "2026"),
        unreadCount = 2,
        listingStatus = listingStatus,
    )
}
