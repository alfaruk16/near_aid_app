package com.nearaid.feature.discovery.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nearaid.core.navigation.ChatRoute
import com.nearaid.core.navigation.HomeRoute
import com.nearaid.core.navigation.ListingDetailRoute
import com.nearaid.core.navigation.NotificationsRoute
import com.nearaid.core.navigation.PublicProfileRoute
import com.nearaid.feature.discovery.home.HomeScreen
import com.nearaid.feature.discovery.listingdetail.ListingDetailScreen
import com.nearaid.feature.discovery.notifications.NotificationsScreen

fun NavGraphBuilder.discoveryGraph(navController: NavController) {
    composable<HomeRoute> {
        HomeScreen(
            onListingClick = { navController.navigate(ListingDetailRoute(it)) },
            onOpenNotifications = { navController.navigate(NotificationsRoute) },
        )
    }
    composable<ListingDetailRoute> { entry ->
        val r = entry.toRoute<ListingDetailRoute>()
        ListingDetailScreen(
            listingId = r.listingId,
            onBack = { navController.popBackStack() },
            onOpenChat = { c, t, title -> navController.navigate(ChatRoute(c, t, title)) },
            onAuthorClick = { navController.navigate(PublicProfileRoute(it)) },
        )
    }
    composable<NotificationsRoute> {
        NotificationsScreen(onBack = { navController.popBackStack() })
    }
}
