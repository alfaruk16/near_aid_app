package com.nearaid.feature.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.nearaid.core.navigation.AuthGraph
import com.nearaid.core.navigation.OtpRoute
import com.nearaid.core.navigation.PhoneRoute
import com.nearaid.core.navigation.ProfileSetupRoute
import com.nearaid.core.navigation.WelcomeRoute
import com.nearaid.feature.auth.WelcomeScreen
import com.nearaid.feature.auth.otp.OtpScreen
import com.nearaid.feature.auth.phone.PhoneScreen
import com.nearaid.feature.auth.profilesetup.ProfileSetupScreen

fun NavGraphBuilder.authGraph(navController: NavController, onAuthenticated: () -> Unit) {
    navigation<AuthGraph>(startDestination = WelcomeRoute) {
        composable<WelcomeRoute> { WelcomeScreen(onGetStarted = { navController.navigate(PhoneRoute) }) }
        composable<PhoneRoute> { PhoneScreen(onBack = { navController.popBackStack() }, onCodeSent = { reqId, e164 -> navController.navigate(OtpRoute(reqId, e164)) }) }
        composable<OtpRoute> { entry -> val r = entry.toRoute<OtpRoute>(); OtpScreen(requestId = r.requestId, phoneE164 = r.phone, onBack = { navController.popBackStack() }, onVerified = { isNewUser -> if (isNewUser) navController.navigate(ProfileSetupRoute) else onAuthenticated() }) }
        composable<ProfileSetupRoute> { ProfileSetupScreen(onDone = onAuthenticated) }
    }
}
