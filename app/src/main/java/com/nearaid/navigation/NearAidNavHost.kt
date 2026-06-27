package com.nearaid.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nearaid.core.navigation.AuthGraph
import com.nearaid.core.navigation.MainGraph
import com.nearaid.feature.auth.navigation.authGraph

/**
 * Root navigation. Switches between the auth graph and the main (bottom-nav) graph.
 * Authenticating pops the auth graph; logging out pops the main graph — so back never
 * crosses the authentication boundary.
 */
@Composable
fun NearAidNavHost(startLoggedIn: Boolean) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (startLoggedIn) MainGraph else AuthGraph,
    ) {
        authGraph(
            navController = navController,
            onAuthenticated = {
                navController.navigate(MainGraph) {
                    popUpTo(AuthGraph) { inclusive = true }
                }
            },
        )

        composable<MainGraph> {
            MainScreen(
                onLoggedOut = {
                    navController.navigate(AuthGraph) {
                        popUpTo(MainGraph) { inclusive = true }
                    }
                },
            )
        }
    }
}
