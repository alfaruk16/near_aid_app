package com.nearaid.shared

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * The iOS entry point into the shared Compose UI. `iosApp` embeds this via a
 * `UIViewControllerRepresentable`, so the whole NearAid app renders through Compose Multiplatform.
 */
fun MainViewController(): UIViewController = ComposeUIViewController { App() }
