package com.nearaid.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nearaid.core.designsystem.theme.Ink3
import com.nearaid.core.designsystem.theme.Line
import com.nearaid.core.designsystem.theme.Marigold
import com.nearaid.core.designsystem.theme.MarigoldDeep
import com.nearaid.core.designsystem.theme.Surface as SurfaceColor
import com.nearaid.core.navigation.HomeRoute
import com.nearaid.core.navigation.PostChooserRoute
import com.nearaid.feature.activity.navigation.activityGraph
import com.nearaid.feature.discovery.navigation.discoveryGraph
import com.nearaid.feature.messages.navigation.messagesGraph
import com.nearaid.feature.post.navigation.postGraph
import com.nearaid.feature.profile.navigation.profileGraph

@Composable
fun MainScreen(onLoggedOut: () -> Unit) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    fun isSelected(tab: TopLevelDestination): Boolean =
        currentDestination?.hierarchy?.any { it.hasRoute(tab.route::class) } == true

    val showBottomBar = TopLevelDestination.entries.any { isSelected(it) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                NearAidBottomBar(
                    isSelected = ::isSelected,
                    onTabSelected = { tab ->
                        navController.navigate(tab.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onPost = { navController.navigate(PostChooserRoute) },
                )
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(padding),
        ) {
            discoveryGraph(navController)
            activityGraph(navController)
            messagesGraph(navController)
            postGraph(navController)
            profileGraph(navController, onLoggedOut = onLoggedOut)
        }
    }
}

@Composable
private fun NearAidBottomBar(
    isSelected: (TopLevelDestination) -> Boolean,
    onTabSelected: (TopLevelDestination) -> Unit,
    onPost: () -> Unit,
) {
    val tabs = TopLevelDestination.entries
    Surface(color = SurfaceColor) {
        Column {
            HorizontalDivider(color = Line)
            Row(
                modifier = Modifier.fillMaxWidth().height(66.dp).padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                BottomItem(tabs[0], isSelected, onTabSelected, Modifier.weight(1f))
                BottomItem(tabs[1], isSelected, onTabSelected, Modifier.weight(1f))
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .offset(y = (-10).dp)
                            .size(54.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Brush.linearGradient(listOf(Marigold, MarigoldDeep)))
                            .noRippleClickable(onPost),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Post", tint = SurfaceColor, modifier = Modifier.size(26.dp))
                    }
                }
                BottomItem(tabs[2], isSelected, onTabSelected, Modifier.weight(1f))
                BottomItem(tabs[3], isSelected, onTabSelected, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun BottomItem(
    tab: TopLevelDestination,
    isSelected: (TopLevelDestination) -> Boolean,
    onTabSelected: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = isSelected(tab)
    val color = if (selected) MarigoldDeep else Ink3
    Column(
        modifier = modifier.noRippleClickable { onTabSelected(tab) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Icon(tab.icon, contentDescription = tab.label, tint = color, modifier = Modifier.size(22.dp))
        Text(tab.label, color = color, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return this.clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
}
