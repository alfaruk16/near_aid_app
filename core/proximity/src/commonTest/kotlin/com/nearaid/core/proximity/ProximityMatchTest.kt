package com.nearaid.core.proximity

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProximityMatchTest {

    private val expected = HandoffToken("claim-123").payload()
    private val threshold = -70

    @Test
    fun matches_when_payload_equal_and_signal_at_threshold() {
        assertTrue(isHandoffMatch(expected.copyOf(), expected, rssi = -70, threshold = threshold))
    }

    @Test
    fun matches_when_signal_stronger_than_threshold() {
        assertTrue(isHandoffMatch(expected.copyOf(), expected, rssi = -40, threshold = threshold))
    }

    @Test
    fun rejects_when_signal_below_threshold() {
        // Same claim, but too far away (weaker than the gate).
        assertFalse(isHandoffMatch(expected.copyOf(), expected, rssi = -71, threshold = threshold))
    }

    @Test
    fun rejects_when_payload_differs() {
        val other = HandoffToken("claim-999").payload()
        assertFalse(isHandoffMatch(other, expected, rssi = -40, threshold = threshold))
    }

    @Test
    fun rejects_when_no_advertised_payload() {
        assertFalse(isHandoffMatch(null, expected, rssi = -40, threshold = threshold))
    }

    @Test
    fun rejects_when_payload_same_prefix_but_different_length() {
        val shorter = expected.copyOf(expected.size - 1)
        assertFalse(isHandoffMatch(shorter, expected, rssi = -40, threshold = threshold))
    }
}
