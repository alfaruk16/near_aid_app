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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CacheMappersTest {

    private val author = Author("u1", "Rahim", "photo", 4.5, true)

    private fun card() = ListingCard(
        id = "l1",
        type = ListingType.OFFER,
        title = "Rice",
        category = Category(7, "food", "Food", "খাবার", "icon"),
        urgency = Urgency.HIGH,
        availableUntil = "2026",
        quantity = "5kg",
        distanceKm = 1.2,
        areaLabel = "Dhanmondi",
        locationFuzzed = null,
        thumbnailUrl = "thumb",
        author = author,
        status = ListingStatus.CLAIMED,
        createdAt = "2026-01-01",
    )

    @Test
    fun listingCard_survives_a_round_trip_through_the_cache_entity() {
        val restored = card().toEntity(feedType = "nearby").toDomain()
        val original = card()
        assertEquals(original.id, restored.id)
        assertEquals(original.type, restored.type)
        assertEquals(original.title, restored.title)
        assertEquals(original.urgency, restored.urgency)
        assertEquals(original.status, restored.status)
        assertEquals(original.quantity, restored.quantity)
        assertEquals(original.distanceKm, restored.distanceKm)
        assertEquals(original.author, restored.author)
        assertEquals(original.createdAt, restored.createdAt)
        // Category key/names survive; id and icon are not cached.
        assertEquals("food", restored.category?.key)
        assertEquals("Food", restored.category?.nameEn)
        assertEquals(0, restored.category?.id)
        assertNull(restored.category?.icon)
    }

    @Test
    fun toEntity_stores_the_feed_type_and_enum_names() {
        val entity = card().toEntity(feedType = "mine")
        assertEquals("mine", entity.feedType)
        assertEquals("OFFER", entity.type)
        assertEquals("HIGH", entity.urgency)
        assertEquals("CLAIMED", entity.status)
    }

    @Test
    fun cachedListing_with_null_category_maps_to_null_category() {
        val restored = card().copy(category = null).toEntity("nearby").toDomain()
        assertNull(restored.category)
    }

    @Test
    fun cachedListing_falls_back_gracefully_on_corrupt_enum_values() {
        val corrupt = card().toEntity("nearby").copy(type = "??", urgency = "??", status = "??")
        val restored = corrupt.toDomain()
        assertEquals(ListingType.REQUEST, restored.type)
        assertNull(restored.urgency)
        assertEquals(ListingStatus.OPEN, restored.status)
    }

    private fun conversation() = Conversation(
        threadId = "t1",
        claimId = "c1",
        listingId = "l1",
        listingType = ListingType.REQUEST,
        listingTitle = "Rice",
        listingStatus = ListingStatus.OPEN,
        counterpart = author,
        role = ClaimRole.HELPING,
        lastMessageBody = "hi",
        lastMessageAt = "2026",
        unreadCount = 3,
    )

    @Test
    fun conversation_survives_a_round_trip_through_the_cache_entity() {
        assertEquals(conversation(), conversation().toEntity().toDomain())
    }

    @Test
    fun cachedConversation_falls_back_gracefully_on_corrupt_enum_values() {
        val corrupt = conversation().toEntity().copy(
            listingType = "??",
            listingStatus = "??",
            role = "??",
        )
        val restored = corrupt.toDomain()
        assertEquals(ListingType.REQUEST, restored.listingType)
        assertEquals(ListingStatus.OPEN, restored.listingStatus)
        assertEquals(ClaimRole.RECEIVING, restored.role)
    }
}
