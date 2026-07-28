package com.nearaid.feature.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberHandoffPermissionGate(onReady: () -> Unit): () -> Unit {
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
