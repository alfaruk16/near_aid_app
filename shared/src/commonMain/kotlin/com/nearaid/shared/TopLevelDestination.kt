package com.nearaid.shared

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
import com.nearaid.shared.resources.Res
import com.nearaid.shared.resources.nav_activity
import com.nearaid.shared.resources.nav_home
import com.nearaid.shared.resources.nav_messages
import com.nearaid.shared.resources.nav_profile
import org.jetbrains.compose.resources.StringResource

/** The four bottom-navigation tabs (the centre Post action is a FAB, not a tab). */
enum class TopLevelDestination(
    val label: StringResource,
    val icon: ImageVector,
    val route: Any,
) {
    HOME(Res.string.nav_home, Icons.Filled.Home, HomeRoute),
    ACTIVITY(Res.string.nav_activity, Icons.Filled.CheckCircle, ActivityRoute),
    MESSAGES(Res.string.nav_messages, Icons.AutoMirrored.Filled.Chat, MessagesRoute),
    PROFILE(Res.string.nav_profile, Icons.Filled.Person, ProfileRoute),
}
