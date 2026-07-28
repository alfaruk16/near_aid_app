package com.nearaid.core.proximity

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HandoffTokenTest {

    @Test
    fun payload_is_four_bytes() {
        assertEquals(4, HandoffToken("claim-123").payload().size)
    }

    @Test
    fun payload_is_deterministic_for_the_same_claim() {
        // Both devices derive the same payload from the same claim id — the whole basis of the match.
        assertTrue(HandoffToken("claim-123").payload().contentEquals(HandoffToken("claim-123").payload()))
    }

    @Test
    fun payload_differs_across_claims() {
        assertFalse(HandoffToken("claim-a").payload().contentEquals(HandoffToken("claim-b").payload()))
    }
}
