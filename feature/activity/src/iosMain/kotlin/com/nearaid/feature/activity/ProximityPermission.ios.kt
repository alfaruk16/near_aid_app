package com.nearaid.feature.activity

import androidx.compose.runtime.Composable

/**
 * iOS has no runtime pre-grant step here: CoreBluetooth prompts on first use, and the proximity
 * confirmer is a v1 stub that reports Unavailable. So the tap proceeds straight to [onReady].
 */
@Composable
actual fun rememberHandoffPermissionGate(onReady: () -> Unit): () -> Unit = onReady
