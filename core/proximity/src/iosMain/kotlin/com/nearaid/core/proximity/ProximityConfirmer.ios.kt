package com.nearaid.core.proximity

/**
 * iOS proximity confirmer — deferred stub (v1), consistent with the project's other iOS-deferred
 * platform edges. Reports [ProximityResult.Unavailable] so callers transparently fall back to
 * manual confirmation on iOS while the feature ships on Android first.
 *
 * TODO(BLE-iOS): implement with CoreBluetooth — `CBPeripheralManager` advertises the token as
 * service data under `BleProximityConfirmer.SERVICE_UUID`; `CBCentralManager` scans and gates on
 * RSSI. Foreground-only, so no background-mode entitlement is needed. Add
 * `NSBluetoothAlwaysUsageDescription` to the iOS app Info.plist when implemented.
 */
internal class IosProximityConfirmer : ProximityConfirmer {
    override suspend fun confirmNearby(token: HandoffToken, config: ProximityConfig): ProximityResult =
        ProximityResult.Unavailable
}
