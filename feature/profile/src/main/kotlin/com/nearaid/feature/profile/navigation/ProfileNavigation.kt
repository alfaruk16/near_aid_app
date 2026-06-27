package com.nearaid.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nearaid.core.navigation.ProfileRoute
import com.nearaid.core.navigation.PublicProfileRoute
import com.nearaid.core.navigation.SettingsRoute
import com.nearaid.core.navigation.VerificationRoute
import com.nearaid.feature.profile.profile.ProfileScreen
import com.nearaid.feature.profile.publicprofile.PublicProfileScreen
import com.nearaid.feature.profile.settings.SettingsScreen
import com.nearaid.feature.profile.verification.VerificationScreen

fun NavGraphBuilder.profileGraph(navController: NavController, onLoggedOut: () -> Unit) {
    composable<ProfileRoute> {
        ProfileScreen(
            onOpenVerification = { navController.navigate(VerificationRoute) },
            onOpenSettings = { navController.navigate(SettingsRoute) },
            onOpenPublicProfile = { navController.navigate(PublicProfileRoute(it)) },
        )
    }
    composable<PublicProfileRoute> { entry ->
        val r = entry.toRoute<PublicProfileRoute>()
        PublicProfileScreen(
            userId = r.userId,
            onBack = { navController.popBackStack() },
        )
    }
    composable<VerificationRoute> {
        VerificationScreen(onBack = { navController.popBackStack() })
    }
    composable<SettingsRoute> {
        SettingsScreen(
            onBack = { navController.popBackStack() },
            onLoggedOut = onLoggedOut,
        )
    }
}
