package com.nearaid.core.proximity

/**
 * The pure decision at the heart of the proximity check: does an observed advertisement belong to
 * *this* handoff and is it near enough to accept? Extracted from the platform scan callback so it is
 * unit-testable without a Bluetooth radio.
 *
 * @param advertised the service-data payload seen on an advertisement (null if the packet carried none).
 * @param expected this device's [HandoffToken.payload] for the claim being handed off.
 * @param rssi the observed signal strength in dBm (higher, i.e. closer to 0, is stronger/nearer).
 * @param threshold the minimum acceptable RSSI ([ProximityConfig.nearRssiThreshold]).
 * @return true only when the payload matches exactly **and** the signal is at or above [threshold].
 */
internal fun isHandoffMatch(
    advertised: ByteArray?,
    expected: ByteArray,
    rssi: Int,
    threshold: Int,
): Boolean = advertised != null && advertised.contentEquals(expected) && rssi >= threshold
