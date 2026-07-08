package com.nearaid.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nearaid.core.designsystem.theme.NearAidTheme
import com.nearaid.core.navigation.AuthGraph
import com.nearaid.core.navigation.MainGraph
import com.nearaid.feature.auth.SplashScreen
import com.nearaid.feature.auth.navigation.authGraph
import org.koin.compose.viewmodel.koinViewModel

/**
 * The whole NearAid UI — one Compose tree shared by Android (`setContent { App() }`) and iOS
 * (`ComposeUIViewController { App() }`). Routes between the auth graph and the main (bottom-nav)
 * graph based on the shared [MainViewModel]'s login state; the splash shows while it resolves.
 */
@Composable
fun App() {
    NearAidTheme {
        val mainViewModel = koinViewModel<MainViewModel>()
        val isLoggedIn by mainViewModel.isLoggedIn.collectAsStateWithLifecycle()
        when (val loggedIn = isLoggedIn) {
            null -> SplashScreen(modifier = Modifier.fillMaxSize().background(NearAidTheme.colors.paper))
            else -> RootNavHost(startLoggedIn = loggedIn)
        }
    }
}

@Composable
private fun RootNavHost(startLoggedIn: Boolean) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = if (startLoggedIn) MainGraph else AuthGraph,
    ) {
        authGraph(
            navController = navController,
            onAuthenticated = {
                navController.navigate(MainGraph) { popUpTo(AuthGraph) { inclusive = true } }
            },
        )
        composable<MainGraph> {
            MainScreen(
                onLoggedOut = {
                    navController.navigate(AuthGraph) { popUpTo(MainGraph) { inclusive = true } }
                },
            )
        }
    }
}
