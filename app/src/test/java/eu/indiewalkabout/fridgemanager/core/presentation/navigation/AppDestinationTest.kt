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
    fun `every destination round trips through its legacy route`() {
        val destinations = listOf(
            AppDestination.Main,
            AppDestination.Expiring,
            AppDestination.Expired,
            AppDestination.Consumed,
            AppDestination.Settings,
            AppDestination.Credits,
        )

        destinations.forEach { destination ->
            assertEquals(
                destination,
                AppDestination.fromLegacyRoute(AppDestination.toLegacyRoute(destination)),
            )
        }
    }

    @Test
    fun `missing or unknown legacy route is ignored`() {
        assertNull(AppDestination.fromLegacyRoute(null))
        assertNull(AppDestination.fromLegacyRoute("unknown_route"))
    }
}
