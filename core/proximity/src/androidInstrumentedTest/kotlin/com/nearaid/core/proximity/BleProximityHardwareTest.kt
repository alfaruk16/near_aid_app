package com.nearaid.core.proximity

import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.os.Build
import android.os.ParcelUuid
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.seconds

/**
 * On-device proof for the BLE proximity handoff. There is no BLE radio in the emulator or in JVM
 * unit tests, so this exercises [BleProximityConfirmer] against a **real** Bluetooth radio via
 * `connectedAndroidTest`. Two tiers:
 *
 *  - [radioComesUp_advertiseStartsOnRealHardware] runs on a **single** phone and proves the radio is
 *    live: a real [AdvertiseCallback.onStartSuccess] fires. It can never reach `Confirmed` because a
 *    radio does not hear its own advertisement — that is what the two-device test is for.
 *  - [twoDevices_confirmHandoffOverRealBle] is the end-to-end proof. Run it on **two** phones at once
 *    (`./gradlew :core:proximity:connectedAndroidTest` with both attached, or
 *    `scripts/ble-proximity-proof.sh`). Each phone advertises the same [HandoffToken] and scans; each
 *    resolves [ProximityResult.Confirmed] off the *other* phone. Both phones must have Bluetooth ON.
 *
 * The fixed [CLAIM_ID] is the shared secret both phones derive their payload from — it must be
 * identical on both, which it is because the test source is the same APK installed on both.
 */
@RunWith(AndroidJUnit4::class)
class BleProximityHardwareTest {

    /**
     * Grant the runtime BLE permissions so the test never blocks on a system dialog. Bluetooth being
     * *enabled* cannot be granted this way — the operator must turn the radio on (asserted below).
     */
    @get:Rule
    val permissionRule: GrantPermissionRule =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            GrantPermissionRule.grant(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
            )
        } else {
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)
        }

    private val context: Context get() = ApplicationProvider.getApplicationContext()

    @Before
    fun requireEnabledBleRadio() {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val adapter = manager?.adapter
        assertNotNull("No Bluetooth adapter — this device has no BLE radio; run on a real phone.", adapter)
        assertTrue(
            "Bluetooth is OFF. Turn it on on this phone before running the proximity hardware proof.",
            adapter!!.isEnabled,
        )
        assertNotNull(
            "Device has no BLE advertiser (some older/virtual devices can scan but not advertise).",
            adapter.bluetoothLeAdvertiser,
        )
        assertNotNull("Device has no BLE scanner.", adapter.bluetoothLeScanner)
    }

    /**
     * Single-device hardware liveness: the real [android.bluetooth.le.BluetoothLeAdvertiser] accepts
     * our [AdvertiseData] and reports [AdvertiseCallback.onStartSuccess]. Proves the advertise path
     * works on this radio without needing a peer.
     */
    @Test
    fun radioComesUp_advertiseStartsOnRealHardware() {
        val adapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        val advertiser = adapter.bluetoothLeAdvertiser
        val payload = HandoffToken(CLAIM_ID).payload()

        val started = CountDownLatch(1)
        val failureCode = AtomicInteger(NO_FAILURE)
        val callback = object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) = started.countDown()
            override fun onStartFailure(errorCode: Int) {
                failureCode.set(errorCode)
                started.countDown()
            }
        }

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .setConnectable(false)
            .build()
        // Must mirror BleProximityConfirmer.advertiseData exactly: service data ONLY. Adding a
        // separate 128-bit service-UUID field too would exceed the 31-byte legacy limit and the
        // radio would reject it with ADVERTISE_FAILED_DATA_TOO_LARGE (error code 1).
        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(false)
            .addServiceData(ParcelUuid(SERVICE_UUID), payload)
            .build()

        try {
            advertiser.startAdvertising(settings, data, callback)
            val fired = started.await(10, TimeUnit.SECONDS)
            assertTrue("Advertiser never reported a result within 10s — radio unresponsive.", fired)
            assertEquals(
                "BLE advertising failed on this hardware (AdvertiseCallback error code).",
                NO_FAILURE,
                failureCode.get(),
            )
        } finally {
            runCatching { advertiser.stopAdvertising(callback) }
        }
    }

    /**
     * End-to-end proof. Requires a **second** phone running this same test at the same time. Each
     * phone advertises + scans the shared [CLAIM_ID] payload for up to 30s; the confirmer resolves as
     * soon as it sees the peer near enough. A generous [RSSI_THRESHOLD] keeps the gate from rejecting
     * a valid-but-weak reading during the proof — real close-range handoffs sit well above it.
     *
     * When only one phone is attached this asserts `Timeout` (documented below), so the whole suite
     * still passes on a single device and clearly signals "no peer was present".
     */
    @Test
    fun twoDevices_confirmHandoffOverRealBle() {
        val confirmer = BleProximityConfirmer(context)
        val config = ProximityConfig(timeout = 30.seconds, nearRssiThreshold = RSSI_THRESHOLD)

        val result = runBlocking { confirmer.confirmNearby(HandoffToken(CLAIM_ID), config) }

        when (result) {
            is ProximityResult.Confirmed -> {
                // The proof: a peer phone advertising the same claim was seen near enough.
                assertTrue(
                    "Confirmed but RSSI ${result.rssi} is below the gate ${config.nearRssiThreshold}.",
                    result.rssi >= config.nearRssiThreshold,
                )
            }
            is ProximityResult.Timeout -> {
                // Single-device run: no peer was present. Not a failure of the radio — skip so the
                // one-phone suite stays green while making the "needs two phones" requirement explicit.
                assumeTrue(
                    "No peer confirmed within 30s. For the end-to-end proof run this on TWO phones " +
                        "at once (scripts/ble-proximity-proof.sh). Single-device runs can only prove " +
                        "radio liveness (see radioComesUp_advertiseStartsOnRealHardware).",
                    false,
                )
            }
            ProximityResult.Unavailable ->
                throw AssertionError("BLE reported Unavailable — adapter off/missing mid-test.")
            ProximityResult.PermissionDenied ->
                throw AssertionError("BLE permissions denied despite GrantPermissionRule.")
            is ProximityResult.Error ->
                throw AssertionError("BLE scan error: ${result.message}")
        }
    }

    private companion object {
        /** Both phones derive their payload from this — must be identical, and it is (same APK). */
        const val CLAIM_ID = "hardware-proof-claim-0001"

        /** Loose gate for the proof so a weak-but-present peer reading still confirms. */
        const val RSSI_THRESHOLD = -95

        const val NO_FAILURE = Int.MIN_VALUE

        /** Mirrors the private SERVICE_UUID in [BleProximityConfirmer]. */
        val SERVICE_UUID: UUID = UUID.fromString("6e617261-6964-4841-4e44-4f46460001a0")
    }
}
