package com.nearaid.feature.post.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nearaid.core.model.ListingType
import com.nearaid.core.navigation.CreateListingRoute
import com.nearaid.core.navigation.HomeRoute
import com.nearaid.core.navigation.PostChooserRoute
import com.nearaid.feature.post.chooser.PostChooserScreen
import com.nearaid.feature.post.create.CreateListingScreen

fun NavGraphBuilder.postGraph(navController: NavController) {
    composable<PostChooserRoute> {
        PostChooserScreen(
            onPickRequest = { navController.navigate(CreateListingRoute("request")) },
            onPickOffer = { navController.navigate(CreateListingRoute("offer")) },
            onDismiss = { navController.popBackStack() },
        )
    }
    composable<CreateListingRoute> { entry ->
        val r = entry.toRoute<CreateListingRoute>()
        val type = if (r.type == "offer") ListingType.OFFER else ListingType.REQUEST
        CreateListingScreen(
            type = type,
            onBack = { navController.popBackStack() },
            onPosted = { navController.popBackStack(HomeRoute, inclusive = false) },
        )
    }
}
