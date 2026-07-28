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
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.util.UUID
import kotlin.coroutines.resume

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
        val advertiseCallback = object : AdvertiseCallback() {}
        return try {
            advertiser.startAdvertising(advertiseSettings(), advertiseData(payload), advertiseCallback)
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
        val filter = ScanFilter.Builder().setServiceUuid(ParcelUuid(SERVICE_UUID)).build()
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

    private fun advertiseData(payload: ByteArray): AdvertiseData = AdvertiseData.Builder()
        .setIncludeDeviceName(false)
        .addServiceUuid(ParcelUuid(SERVICE_UUID))
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
    }
}
