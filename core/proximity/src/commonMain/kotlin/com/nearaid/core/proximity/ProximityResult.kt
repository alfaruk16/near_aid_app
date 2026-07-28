package com.nearaid.core.proximity

/** Outcome of a proximity-confirmation attempt. */
sealed interface ProximityResult {

    /** A peer advertising the same [HandoffToken] was seen at a near-enough signal (`rssi` in dBm). */
    data class Confirmed(val rssi: Int) : ProximityResult

    /** No matching peer was seen within the configured window. */
    data object Timeout : ProximityResult

    /** BLE is unusable on this device/platform (no adapter, turned off, or not yet implemented). */
    data object Unavailable : ProximityResult

    /** The runtime Bluetooth permissions have not been granted. */
    data object PermissionDenied : ProximityResult

    /** The radio reported a failure; [message] is a diagnostic, not for display. */
    data class Error(val message: String) : ProximityResult
}
