package com.nearaid.core.proximity

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Covers the pre-flight guard branches of the Android BLE confirmer with mocked framework objects —
 * no radio needed. The advertise/scan callback flow itself is genuine platform glue and stays
 * exercised only on-device; the near-enough decision it makes is tested in `ProximityMatchTest`.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BleProximityConfirmerTest {

    private val token = HandoffToken("claim-1")

    private fun contextWith(manager: BluetoothManager?): Context = mockk {
        every { getSystemService(Context.BLUETOOTH_SERVICE) } returns manager
    }

    @Test
    fun `unavailable when there is no bluetooth manager`() = runTest {
        val result = BleProximityConfirmer(contextWith(manager = null)).confirmNearby(token)
        assertEquals(ProximityResult.Unavailable, result)
    }

    @Test
    fun `unavailable when there is no adapter`() = runTest {
        val manager = mockk<BluetoothManager> { every { adapter } returns null }
        val result = BleProximityConfirmer(contextWith(manager)).confirmNearby(token)
        assertEquals(ProximityResult.Unavailable, result)
    }

    @Test
    fun `unavailable when bluetooth is disabled`() = runTest {
        val bt = mockk<BluetoothAdapter> { every { isEnabled } returns false }
        val manager = mockk<BluetoothManager> { every { adapter } returns bt }
        val result = BleProximityConfirmer(contextWith(manager)).confirmNearby(token)
        assertEquals(ProximityResult.Unavailable, result)
    }

    @Test
    fun `unavailable when the advertiser is missing`() = runTest {
        val bt = mockk<BluetoothAdapter> {
            every { isEnabled } returns true
            every { bluetoothLeAdvertiser } returns null
        }
        val manager = mockk<BluetoothManager> { every { adapter } returns bt }
        val result = BleProximityConfirmer(contextWith(manager)).confirmNearby(token)
        assertEquals(ProximityResult.Unavailable, result)
    }

    @Test
    fun `unavailable when the scanner is missing`() = runTest {
        val bt = mockk<BluetoothAdapter> {
            every { isEnabled } returns true
            every { bluetoothLeAdvertiser } returns mockk()
            every { bluetoothLeScanner } returns null
        }
        val manager = mockk<BluetoothManager> { every { adapter } returns bt }
        val result = BleProximityConfirmer(contextWith(manager)).confirmNearby(token)
        assertEquals(ProximityResult.Unavailable, result)
    }

    @Test
    fun `permission denied when the bluetooth permission is not granted`() = runTest {
        val bt = mockk<BluetoothAdapter> {
            every { isEnabled } returns true
            every { bluetoothLeAdvertiser } returns mockk()
            every { bluetoothLeScanner } returns mockk()
        }
        val manager = mockk<BluetoothManager> { every { adapter } returns bt }
        val context = mockk<Context> {
            every { getSystemService(Context.BLUETOOTH_SERVICE) } returns manager
            every { checkSelfPermission(any()) } returns PackageManager.PERMISSION_DENIED
        }
        val result = BleProximityConfirmer(context).confirmNearby(token)
        assertEquals(ProximityResult.PermissionDenied, result)
    }
}
