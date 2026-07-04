package com.nearaid.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.nearaid.R
import com.nearaid.core.navigation.ActivityRoute
import com.nearaid.core.navigation.HomeRoute
import com.nearaid.core.navigation.MessagesRoute
import com.nearaid.core.navigation.ProfileRoute

/** The four bottom-navigation tabs (the centre Post action is a FAB, not a tab). */
enum class TopLevelDestination(
    @StringRes val labelRes: Int,
    val icon: ImageVector,
    val route: Any,
) {
    HOME(R.string.nav_home, Icons.Filled.Home, HomeRoute),
    ACTIVITY(R.string.nav_activity, Icons.Filled.CheckCircle, ActivityRoute),
    MESSAGES(R.string.nav_messages, Icons.AutoMirrored.Filled.Chat, MessagesRoute),
    PROFILE(R.string.nav_profile, Icons.Filled.Person, ProfileRoute),
}
