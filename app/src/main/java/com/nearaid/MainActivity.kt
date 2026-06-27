package com.nearaid

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nearaid.core.designsystem.theme.NearAidTheme
import com.nearaid.core.designsystem.theme.Paper
import com.nearaid.feature.auth.SplashScreen
import com.nearaid.navigation.NearAidNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        // Keep the system splash until we know whether the user is signed in.
        splash.setKeepOnScreenCondition { viewModel.isLoggedIn.value == null }
        enableEdgeToEdge()
        setContent {
            NearAidTheme {
                NotificationPermissionRequester()
                val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
                when (val loggedIn = isLoggedIn) {
                    null -> SplashScreen(modifier = Modifier.fillMaxSize().background(Paper))
                    else -> NearAidNavHost(startLoggedIn = loggedIn)
                }
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
