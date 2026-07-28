package com.nearaid.feature.activity

import androidx.compose.runtime.Composable

/**
 * Returns a callback for the "Mark delivered" tap that first makes sure the BLE runtime permissions
 * have been prompted for, then runs [onReady] to start the handoff.
 *
 * It only *surfaces the OS permission prompt* on the first tap — it does not gate the handoff on the
 * outcome. Whether the user grants or denies, [onReady] runs and the ViewModel's proximity check
 * degrades gracefully (a denied permission simply yields the manual-confirm fallback). Proximity
 * strengthens the handoff; it never blocks it.
 *
 * - **Android:** requests `BLUETOOTH_SCAN`/`ADVERTISE` (12+) or `ACCESS_FINE_LOCATION` (pre-12) if
 *   not already granted.
 * - **iOS:** no pre-grant (CoreBluetooth prompts on first use; the confirmer is a v1 stub), so it
 *   runs [onReady] directly.
 */
@Composable
expect fun rememberHandoffPermissionGate(onReady: () -> Unit): () -> Unit
