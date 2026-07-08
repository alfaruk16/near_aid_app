package com.nearaid.feature.activity.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nearaid.core.navigation.ActivityRoute
import com.nearaid.core.navigation.ChatRoute
import com.nearaid.core.navigation.ListingDetailRoute
import com.nearaid.feature.activity.ActivityScreen

fun NavGraphBuilder.activityGraph(navController: NavController) {
    composable<ActivityRoute> {
        ActivityScreen(
            onListingClick = { navController.navigate(ListingDetailRoute(it)) },
            onOpenChat = { c, t, title -> navController.navigate(ChatRoute(c, t, title)) },
        )
    }
}
