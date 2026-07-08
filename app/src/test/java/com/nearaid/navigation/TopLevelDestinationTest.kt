package com.nearaid.navigation

import com.nearaid.core.navigation.ActivityRoute
import com.nearaid.core.navigation.HomeRoute
import com.nearaid.core.navigation.MessagesRoute
import com.nearaid.core.navigation.ProfileRoute
import com.nearaid.shared.TopLevelDestination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class TopLevelDestinationTest {

    @Test
    fun `exposes the four bottom-nav tabs in order`() {
        assertEquals(
            listOf(
                TopLevelDestination.HOME,
                TopLevelDestination.ACTIVITY,
                TopLevelDestination.MESSAGES,
                TopLevelDestination.PROFILE,
            ),
            TopLevelDestination.entries,
        )
    }

    @Test
    fun `each tab maps to its own route`() {
        assertEquals(HomeRoute, TopLevelDestination.HOME.route)
        assertEquals(ActivityRoute, TopLevelDestination.ACTIVITY.route)
        assertEquals(MessagesRoute, TopLevelDestination.MESSAGES.route)
        assertEquals(ProfileRoute, TopLevelDestination.PROFILE.route)
    }

    @Test
    fun `routes are unique across tabs`() {
        val routes = TopLevelDestination.entries.map { it.route }
        assertEquals(routes.size, routes.toSet().size)
    }

    @Test
    fun `every tab has a route and icon`() {
        TopLevelDestination.entries.forEach { destination ->
            assertNotNull("Expected a route for $destination", destination.route)
            assertNotNull("Expected an icon for $destination", destination.icon)
        }
    }
}
