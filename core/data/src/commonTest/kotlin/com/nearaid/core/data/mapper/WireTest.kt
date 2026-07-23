package com.nearaid.core.data.mapper

import com.nearaid.core.model.ClaimStatus
import com.nearaid.core.model.ListingStatus
import com.nearaid.core.model.ListingType
import com.nearaid.core.model.Urgency
import kotlin.test.Test
import kotlin.test.assertEquals

class WireTest {

    @Test
    fun listingType_wire_uses_lowercase_offer_request() {
        assertEquals("offer", ListingType.OFFER.wire())
        assertEquals("request", ListingType.REQUEST.wire())
    }

    @Test
    fun listingType_wire_round_trips_through_toListingType() {
        for (t in ListingType.entries) {
            assertEquals(t, t.wire().toListingType())
        }
    }

    @Test
    fun urgency_wire_is_lowercase_name() {
        assertEquals("low", Urgency.LOW.wire())
        assertEquals("critical", Urgency.CRITICAL.wire())
    }

    @Test
    fun urgency_wire_round_trips_through_toUrgency() {
        for (u in Urgency.entries) {
            assertEquals(u, u.wire().toUrgency())
        }
    }

    @Test
    fun listingStatus_wire_round_trips_through_toListingStatus() {
        for (s in ListingStatus.entries) {
            assertEquals(s, s.wire().toListingStatus())
        }
    }

    @Test
    fun claimStatus_wire_round_trips_through_toClaimStatus() {
        for (s in ClaimStatus.entries) {
            assertEquals(s, s.wire().toClaimStatus())
        }
    }
}
