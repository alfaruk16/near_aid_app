package com.nearaid.core.proximity

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HandoffTokenTest {

    @Test
    fun payload_is_four_bytes_and_deterministic() {
        val a = HandoffToken("claim-123").payload()
        val b = HandoffToken("claim-123").payload()
        assertEquals(4, a.size)
        assertArrayEquals(a, b)
    }

    @Test
    fun different_claims_produce_different_payloads() {
        val a = HandoffToken("claim-123").payload()
        val b = HandoffToken("claim-456").payload()
        assertFalse(a.contentEquals(b))
    }

    @Test
    fun isHandoffMatch_requires_exact_payload_and_near_enough_rssi() {
        val expected = HandoffToken("claim-123").payload()
        val other = HandoffToken("claim-999").payload()

        // Match: same payload, signal at/above threshold.
        assertTrue(isHandoffMatch(expected, expected, rssi = -60, threshold = -70))
        // Too weak a signal.
        assertFalse(isHandoffMatch(expected, expected, rssi = -80, threshold = -70))
        // Wrong claim.
        assertFalse(isHandoffMatch(other, expected, rssi = -60, threshold = -70))
        // No service data on the packet.
        assertFalse(isHandoffMatch(null, expected, rssi = -60, threshold = -70))
    }
}
