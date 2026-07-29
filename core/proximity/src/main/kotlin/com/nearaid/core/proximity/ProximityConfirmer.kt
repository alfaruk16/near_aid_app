package com.nearaid.core.proximity

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Confirms that two devices on the same claim are physically next to each other, by having each
 * device simultaneously advertise and scan for the other's [HandoffToken] over Bluetooth LE.
 *
 * The handoff is a live, both-users-present moment, so the scan runs only while the caller awaits
 * it (foreground) — this sidesteps background-BLE restrictions.
 */
interface ProximityConfirmer {

    /**
     * Advertise [token] and scan for a peer advertising the same token. Suspends until a near-enough
     * peer is seen, the [config] window elapses, or the radio is unusable. Safe to cancel — doing so
     * stops advertising and scanning.
     */
    suspend fun confirmNearby(
        token: HandoffToken,
        config: ProximityConfig = ProximityConfig(),
    ): ProximityResult
}

/**
 * Tuning for a proximity attempt.
 *
 * @param timeout how long to keep advertising/scanning before giving up.
 * @param nearRssiThreshold minimum signal strength in dBm to accept as "near"; higher (closer to 0)
 *   is stronger/closer. `-70` is roughly within a few metres — RSSI is noisy, so treat it as a
 *   "close enough" gate, never an exact distance.
 */
data class ProximityConfig(
    val timeout: Duration = 20.seconds,
    val nearRssiThreshold: Int = -70,
)
