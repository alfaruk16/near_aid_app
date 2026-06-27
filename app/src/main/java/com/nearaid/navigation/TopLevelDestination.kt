package com.nearaid.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.nearaid.core.navigation.ActivityRoute
import com.nearaid.core.navigation.HomeRoute
import com.nearaid.core.navigation.MessagesRoute
import com.nearaid.core.navigation.ProfileRoute

/** The four bottom-navigation tabs (the centre Post action is a FAB, not a tab). */
enum class TopLevelDestination(
    val label: String,
    val icon: ImageVector,
    val route: Any,
) {
    HOME("Home", Icons.Filled.Home, HomeRoute),
    ACTIVITY("Activity", Icons.Filled.CheckCircle, ActivityRoute),
    MESSAGES("Messages", Icons.AutoMirrored.Filled.Chat, MessagesRoute),
    PROFILE("Profile", Icons.Filled.Person, ProfileRoute),
}
