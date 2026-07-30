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
import com.nearaid.core.network.dto.MyClaimDto
import com.nearaid.core.network.dto.MyClaimListingDto
import com.nearaid.core.network.dto.NotificationDto
import com.nearaid.core.network.dto.PublicUserDto
import com.nearaid.core.network.dto.RatingDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MappersTest {

    // --- String -> enum helpers ------------------------------------------------

    @Test
    fun toListingType_maps_offer_else_request() {
        assertEquals(ListingType.OFFER, "offer".toListingType())
        assertEquals(ListingType.REQUEST, "request".toListingType())
        assertEquals(ListingType.REQUEST, "anything-else".toListingType())
        assertEquals(ListingType.REQUEST, (null as String?).toListingType())
    }

    @Test
    fun toUrgency_maps_known_values_and_null_otherwise() {
        assertEquals(Urgency.LOW, "low".toUrgency())
        assertEquals(Urgency.MEDIUM, "medium".toUrgency())
        assertEquals(Urgency.HIGH, "high".toUrgency())
        assertEquals(Urgency.CRITICAL, "critical".toUrgency())
        assertNull("unknown".toUrgency())
        assertNull((null as String?).toUrgency())
    }

    @Test
    fun toListingStatus_maps_states_and_defaults_to_open() {
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
        assertEquals(ClaimStatus.DELIVERED, "delivered".toClaimStatus())
        assertEquals(ClaimStatus.WITHDRAWN, "withdrawn".toClaimStatus())
        assertEquals(ClaimStatus.COMPLETED, "completed".toClaimStatus())
        assertEquals(ClaimStatus.CANCELLED, "cancelled".toClaimStatus())
        assertEquals(ClaimStatus.ACTIVE, "active".toClaimStatus())
        assertEquals(ClaimStatus.ACTIVE, (null as String?).toClaimStatus())
    }

    // --- Simple DTO mappers ----------------------------------------------------

    @Test
    fun geoDto_maps_lat_lng() {
        val g = GeoDto(lat = 23.81, lng = 90.41).toDomain()
        assertEquals(23.81, g.lat, 0.0)
        assertEquals(90.41, g.lng, 0.0)
    }

    @Test
    fun authorDto_maps_all_fields() {
        val a = AuthorDto("u1", "Ann", "photo", 72.5, isIdVerified = true).toDomain()
        assertEquals("u1", a.id)
        assertEquals("Ann", a.displayName)
        assertEquals("photo", a.photoUrl)
        assertEquals(72.5, a.trustScore!!, 0.0)
        assertTrue(a.isIdVerified)
    }

    @Test
    fun categoryDto_maps_directly() {
        val c = CategoryDto(3, "food", "Food", "খাবার", "icon").toDomain()
        assertEquals(3, c.id)
        assertEquals("food", c.key)
        assertEquals("Food", c.nameEn)
        assertEquals("খাবার", c.nameBn)
        assertEquals("icon", c.icon)
    }

    @Test
    fun categoryRefDto_falls_back_to_capitalized_key_and_empty_bn() {
        val c = CategoryRefDto(key = "medicine").toDomain()
        assertEquals("Medicine", c.nameEn)
        assertEquals("", c.nameBn)
    }

    @Test
    fun listingImageDto_maps_fields() {
        val img = ListingImageDto("i1", "u", "t").toDomain()
        assertEquals("i1", img.id)
        assertEquals("u", img.url)
        assertEquals("t", img.thumbnailUrl)
    }

    // --- Listing cards / detail ------------------------------------------------

    @Test
    fun listingCardDto_maps_including_owner_active_claim_id() {
        val card = ListingCardDto(
            id = "l1",
            type = "offer",
            title = "Rice",
            category = CategoryRefDto(key = "food"),
            urgency = "high",
            author = AuthorDto("u1"),
            status = "claimed",
            activeClaimId = "c9",
        ).toDomain()

        assertEquals(ListingType.OFFER, card.type)
        assertEquals(Urgency.HIGH, card.urgency)
        assertEquals(ListingStatus.CLAIMED, card.status)
        assertEquals("c9", card.activeClaimId)
        assertEquals("food", card.category?.key)
    }

    @Test
    fun listingCardDto_defaults_null_optionals() {
        val card = ListingCardDto(id = "l1", type = "request", title = "Help", author = AuthorDto("u1")).toDomain()
        assertNull(card.category)
        assertNull(card.urgency)
        assertNull(card.activeClaimId)
        assertNull(card.locationFuzzed)
        assertEquals(ListingStatus.OPEN, card.status)
    }

    @Test
    fun listingDetailDto_maps_images_and_locations() {
        val detail = ListingDetailDto(
            id = "l1",
            type = "request",
            title = "Blankets",
            author = AuthorDto("u1"),
            images = listOf(ListingImageDto("i1", "u", null)),
            locationFuzzed = GeoDto(1.0, 2.0),
            locationExact = GeoDto(3.0, 4.0),
        ).toDomain()

        assertEquals(1, detail.images.size)
        assertEquals(1.0, detail.locationFuzzed!!.lat, 0.0)
        assertEquals(3.0, detail.locationExact!!.lat, 0.0)
    }

    // --- ClaimDto (serialization + two-step handoff fixes) ---------------------

    @Test
    fun claimDto_prefers_claimId_then_id_then_empty() {
        assertEquals("cid", ClaimDto(claimId = "cid", id = "x").toDomain().id)
        assertEquals("x", ClaimDto(claimId = null, id = "x").toDomain().id)
        assertEquals("", ClaimDto(claimId = null, id = null).toDomain().id)
    }

    @Test
    fun claimDto_tolerates_missing_listing_id() {
        // Backend may omit listing_id; the mapper coerces null to "".
        assertEquals("", ClaimDto(claimId = "c1", listingId = null).toDomain().listingId)
        assertEquals("l1", ClaimDto(claimId = "c1", listingId = "l1").toDomain().listingId)
    }

    @Test
    fun claimDto_derives_delivered_from_deliveredAt_while_status_still_active() {
        // Backend keeps status="active" after delivery and only sets delivered_at.
        assertEquals(
            ClaimStatus.DELIVERED,
            ClaimDto(claimId = "c1", status = "active", deliveredAt = "2026-07-29T00:00:00Z").toDomain().status,
        )
        // No delivered_at yet -> still active.
        assertEquals(ClaimStatus.ACTIVE, ClaimDto(claimId = "c1", status = "active").toDomain().status)
        // A terminal status wins over delivered_at.
        assertEquals(
            ClaimStatus.COMPLETED,
            ClaimDto(claimId = "c1", status = "completed", deliveredAt = "2026-07-29T00:00:00Z").toDomain().status,
        )
    }

    // --- MyClaimDto (nested /me/claims shape) ----------------------------------

    @Test
    fun myClaimDto_reads_nested_listing_type_and_id() {
        val claim = MyClaimDto(
            id = "c1",
            status = "active",
            deliveredAt = "2026-07-29T00:00:00Z",
            listing = MyClaimListingDto(id = "l1", type = "offer", title = "Rice", status = "delivered"),
        ).toDomain()

        assertEquals("c1", claim.id)
        assertEquals("l1", claim.listingId)
        assertEquals(ListingType.OFFER, claim.listingType)
        assertEquals(ClaimStatus.DELIVERED, claim.status)
    }

    @Test
    fun myClaimDto_defaults_when_listing_absent() {
        val claim = MyClaimDto(id = "c1", listing = null).toDomain()
        assertEquals("", claim.listingId)
        assertEquals(ListingType.REQUEST, claim.listingType)
    }

    // --- Me / users ------------------------------------------------------------

    @Test
    fun meDto_maps_language_and_account_status() {
        assertEquals(AppLanguage.EN, MeDto(id = "u1", phone = "+8801", language = "en").toDomain().language)
        assertEquals(AppLanguage.BN, MeDto(id = "u1", phone = "+8801", language = "??").toDomain().language)
        assertEquals(AccountStatus.SUSPENDED, MeDto(id = "u1", phone = "+8801", status = "suspended").toDomain().status)
        assertEquals(AccountStatus.BANNED, MeDto(id = "u1", phone = "+8801", status = "banned").toDomain().status)
        assertEquals(AccountStatus.ACTIVE, MeDto(id = "u1", phone = "+8801", status = "active").toDomain().status)
    }

    @Test
    fun publicUserDto_and_ratingDto_map_fields() {
        val u = PublicUserDto(id = "u1", aggregateRating = 4.5, completedHelpCount = 12).toDomain()
        assertEquals("u1", u.id)
        assertEquals(4.5, u.aggregateRating!!, 0.0)
        assertEquals(12, u.completedHelpCount)

        val r = RatingDto(id = "r1", raterName = "Ann", score = 5, comment = "great").toDomain()
        assertEquals(5, r.score)
        assertEquals("great", r.comment)
    }

    // --- Messages / conversations / notifications ------------------------------

    @Test
    fun messageDto_decodes_image_type_else_text() {
        assertEquals(MessageType.IMAGE, MessageDto(id = "m1", senderId = "u1", type = "image").toDomain().type)
        assertEquals(MessageType.TEXT, MessageDto(id = "m1", senderId = "u1", type = "text").toDomain().type)
        assertEquals(MessageType.TEXT, MessageDto(id = "m1", senderId = "u1", type = "weird").toDomain().type)
    }

    @Test
    fun conversationDto_maps_role_status_fallback_and_last_message() {
        val dto = ConversationDto(
            threadId = "t1",
            claimId = "c1",
            listing = ConversationListingDto(id = "l1", type = "offer", title = "Rice", status = "claimed"),
            counterpart = AuthorDto("u2", "Bob"),
            role = "giving",
            lastMessage = LastMessageDto(body = "hi", createdAt = "2026-07-29T10:00:00Z"),
            unreadCount = 3,
            listingStatus = null, // falls back to the nested listing.status
        )
        val c = dto.toDomain()
        assertEquals(ClaimRole.GIVING, c.role)
        assertEquals(ListingType.OFFER, c.listingType)
        assertEquals(ListingStatus.CLAIMED, c.listingStatus)
        assertEquals("hi", c.lastMessageBody)
        assertEquals(3, c.unreadCount)
    }

    @Test
    fun conversationDto_unknown_role_defaults_to_receiving_and_top_level_status_wins() {
        val dto = ConversationDto(
            threadId = "t1",
            claimId = "c1",
            listing = ConversationListingDto(id = "l1", type = "request", status = "open"),
            counterpart = AuthorDto("u2"),
            role = "mystery",
            listingStatus = "completed", // top-level status overrides the nested one
        )
        val c = dto.toDomain()
        assertEquals(ClaimRole.RECEIVING, c.role)
        assertEquals(ListingStatus.COMPLETED, c.listingStatus)
    }

    @Test
    fun notificationDto_isRead_derives_from_readAt() {
        assertFalse(NotificationDto(id = "n1", readAt = null).toDomain().isRead)
        assertTrue(NotificationDto(id = "n1", readAt = "2026-07-29T00:00:00Z").toDomain().isRead)
    }

    // --- Wire helpers ----------------------------------------------------------

    @Test
    fun wire_encodes_enums_to_backend_strings() {
        assertEquals("offer", ListingType.OFFER.wire())
        assertEquals("request", ListingType.REQUEST.wire())
        assertEquals("high", Urgency.HIGH.wire())
        assertEquals("delivered", ListingStatus.DELIVERED.wire())
        assertEquals("active", ClaimStatus.ACTIVE.wire())
    }
}
