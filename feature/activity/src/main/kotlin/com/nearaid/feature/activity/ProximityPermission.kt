package com.nearaid.feature.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext

/**
 * Returns a callback for the handoff tap that first makes sure the BLE runtime permissions have
 * been prompted for, then runs [onReady] to start the handoff.
 *
 * It only *surfaces the OS permission prompt* on the first tap — it does not gate the handoff on the
 * outcome. Whether the user grants or denies, [onReady] runs and the ViewModel's proximity check
 * degrades gracefully (a denied permission simply yields the manual-confirm fallback). Proximity
 * strengthens the handoff; it never blocks it.
 *
 * Requests `BLUETOOTH_SCAN`/`ADVERTISE` (Android 12+) or `ACCESS_FINE_LOCATION` (pre-12) if not
 * already granted.
 */
@Composable
fun rememberHandoffPermissionGate(onReady: () -> Unit): () -> Unit {
    val context = LocalContext.current
    val ready = rememberUpdatedState(onReady)

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE)
    } else {
        // Pre-Android 12 a BLE scan can infer location, so it required a location permission.
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // Proceed regardless of the grant result — the confirmer re-checks and degrades gracefully.
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { ready.value() }

    return {
        val allGranted = permissions.all {
            context.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
        }
        if (allGranted) ready.value() else launcher.launch(permissions)
    }
}
