package com.nearaid.core.proximity

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Demonstrates the domain-testable seam: consumers (a use case / ViewModel) depend on the
 * [ProximityConfirmer] interface and can be driven with a fake, no radio required.
 */
class FakeProximityConfirmerTest {

    private class FakeProximityConfirmer(private val result: ProximityResult) : ProximityConfirmer {
        var lastToken: HandoffToken? = null
        override suspend fun confirmNearby(token: HandoffToken, config: ProximityConfig): ProximityResult {
            lastToken = token
            return result
        }
    }

    @Test
    fun forwards_token_and_returns_configured_result() = runTest {
        val fake = FakeProximityConfirmer(ProximityResult.Confirmed(rssi = -55))
        val result = fake.confirmNearby(HandoffToken("claim-9"))
        assertEquals(HandoffToken("claim-9"), fake.lastToken)
        assertEquals(ProximityResult.Confirmed(-55), result)
    }

    @Test
    fun config_defaults_are_sane() {
        val config = ProximityConfig()
        assertEquals(20, config.timeout.inWholeSeconds)
        assertEquals(-70, config.nearRssiThreshold)
    }
}
