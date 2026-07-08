package com.nearaid

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nearaid.shared.App
import org.koin.androidx.compose.KoinAndroidContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoinAndroidContext {
                NotificationPermissionRequester()
                // The entire UI is the shared Compose tree (also hosted on iOS via MainViewController).
                App()
            }
        }
    }
}

@Composable
private fun NotificationPermissionRequester() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { /* result ignored — push is best-effort */ }
        LaunchedEffect(Unit) { launcher.launch(Manifest.permission.POST_NOTIFICATIONS) }
    }
}
