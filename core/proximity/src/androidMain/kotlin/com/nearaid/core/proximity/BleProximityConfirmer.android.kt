package com.nearaid.core.proximity

import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.seconds

/**
 * Android BLE proximity confirmer. Simultaneously advertises the claim's [HandoffToken] as BLE
 * service data and scans for a peer advertising the same payload; resolves [ProximityResult.Confirmed]
 * on the first near-enough match. Foreground use only — the caller awaits the result.
 */
internal class BleProximityConfirmer(private val context: Context) : ProximityConfirmer {

    override suspend fun confirmNearby(token: HandoffToken, config: ProximityConfig): ProximityResult {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val adapter = manager?.adapter ?: return ProximityResult.Unavailable
        if (!adapter.isEnabled) return ProximityResult.Unavailable
        val advertiser = adapter.bluetoothLeAdvertiser ?: return ProximityResult.Unavailable
        val scanner = adapter.bluetoothLeScanner ?: return ProximityResult.Unavailable
        if (!hasPermissions()) return ProximityResult.PermissionDenied

        val payload = token.payload()
        // Completes with null on onStartSuccess, or an Error result on onStartFailure. Without this
        // the peer can never see us and we would silently scan until Timeout — the exact failure that
        // an over-31-byte advertisement (ADVERTISE_FAILED_DATA_TOO_LARGE) produced on real hardware.
        val advertiseStarted = CompletableDeferred<ProximityResult?>()
        val advertiseCallback = object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                advertiseStarted.complete(null)
            }

            override fun onStartFailure(errorCode: Int) {
                advertiseStarted.complete(ProximityResult.Error("advertise failed: $errorCode"))
            }
        }
        return try {
            advertiser.startAdvertising(advertiseSettings(), advertiseData(payload), advertiseCallback)
            // Fail fast if the radio rejects our advertisement rather than scanning in vain. A null
            // here means success (or the callback was slow) — either way we go on to scan.
            val advertiseFailure = withTimeoutOrNull(ADVERTISE_START_TIMEOUT) { advertiseStarted.await() }
            if (advertiseFailure != null) return advertiseFailure
            withTimeoutOrNull(config.timeout) {
                scanForPeer(scanner, payload, config)
            } ?: ProximityResult.Timeout
        } catch (e: SecurityException) {
            ProximityResult.PermissionDenied
        } finally {
            runCatching { advertiser.stopAdvertising(advertiseCallback) }
        }
    }

    private suspend fun scanForPeer(
        scanner: BluetoothLeScanner,
        payload: ByteArray,
        config: ProximityConfig,
    ): ProximityResult = suspendCancellableCoroutine { continuation ->
        // Match on service *data* (UUID + this claim's payload), not a standalone service-UUID field:
        // the advertisement carries only service data to stay within the 31-byte legacy limit, and
        // filtering on the payload means the scanner only surfaces a peer on the same claim.
        val filter = ScanFilter.Builder().setServiceData(ParcelUuid(SERVICE_UUID), payload).build()
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val data = result.scanRecord?.getServiceData(ParcelUuid(SERVICE_UUID))
                if (isHandoffMatch(data, payload, result.rssi, config.nearRssiThreshold)) {
                    if (continuation.isActive) {
                        runCatching { scanner.stopScan(this) }
                        continuation.resume(ProximityResult.Confirmed(result.rssi))
                    }
                }
            }

            override fun onScanFailed(errorCode: Int) {
                if (continuation.isActive) {
                    continuation.resume(ProximityResult.Error("scan failed: $errorCode"))
                }
            }
        }
        continuation.invokeOnCancellation { runCatching { scanner.stopScan(callback) } }
        try {
            scanner.startScan(listOf(filter), settings, callback)
        } catch (e: SecurityException) {
            if (continuation.isActive) continuation.resume(ProximityResult.PermissionDenied)
        }
    }

    private fun advertiseSettings(): AdvertiseSettings = AdvertiseSettings.Builder()
        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
        .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
        .setConnectable(false)
        .build()

    // Service data only (UUID + 4-byte payload ≈ 22 bytes). Adding a separate 128-bit service-UUID
    // field too would push the packet past the 31-byte legacy advertising limit and the radio would
    // reject it with ADVERTISE_FAILED_DATA_TOO_LARGE — the scanner reads the UUID from service data.
    private fun advertiseData(payload: ByteArray): AdvertiseData = AdvertiseData.Builder()
        .setIncludeDeviceName(false)
        .addServiceData(ParcelUuid(SERVICE_UUID), payload)
        .build()

    private fun hasPermissions(): Boolean {
        val required = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE)
        } else {
            // Pre-Android 12 a BLE scan can infer location, so it required a location permission.
            listOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        return required.all {
            context.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private companion object {
        /** Custom 128-bit service UUID that scopes NearAid handoff advertisements. */
        val SERVICE_UUID: UUID = UUID.fromString("6e617261-6964-4841-4e44-4f46460001a0")

        /** How long to wait for the advertiser's start callback before scanning regardless. */
        val ADVERTISE_START_TIMEOUT = 3.seconds
    }
}
