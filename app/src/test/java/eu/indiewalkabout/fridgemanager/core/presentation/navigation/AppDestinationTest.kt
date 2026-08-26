package eu.indiewalkabout.fridgemanager.core.presentation.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AppDestinationTest {

    @Test
    fun `top level destinations are exposed only for root screens`() {
        assertEquals(TopLevelDestination.MAIN, AppDestination.Main.topLevelDestination)
        assertEquals(TopLevelDestination.EXPIRING, AppDestination.Expiring.topLevelDestination)
        assertEquals(TopLevelDestination.EXPIRED, AppDestination.Expired.topLevelDestination)
        assertEquals(TopLevelDestination.CONSUMED, AppDestination.Consumed.topLevelDestination)
        assertNull(AppDestination.Settings.topLevelDestination)
        assertNull(AppDestination.Credits.topLevelDestination)
    }

    @Test
    fun `every destination has the expected legacy route in both directions`() {
        val mappings = listOf(
            AppDestination.Main to NavigationScreenConstants.MAIN_SCREEN,
            AppDestination.Expiring to NavigationScreenConstants.FOOD_EXPIRING_SCREEN,
            AppDestination.Expired to NavigationScreenConstants.FOOD_EXPIRED_SCREEN,
            AppDestination.Consumed to NavigationScreenConstants.FOOD_CONSUMED_SCREEN,
            AppDestination.Settings to NavigationScreenConstants.SETTINGS_SCREEN,
            AppDestination.Credits to NavigationScreenConstants.CREDITS_SCREEN,
        )

        mappings.forEach { (destination, route) ->
            assertEquals(route, AppDestination.toLegacyRoute(destination))
            assertEquals(destination, AppDestination.fromLegacyRoute(route))
        }
    }

    @Test
    fun `missing or unknown legacy route is ignored`() {
        assertNull(AppDestination.fromLegacyRoute(null))
        assertNull(AppDestination.fromLegacyRoute("unknown_route"))
    }
}
