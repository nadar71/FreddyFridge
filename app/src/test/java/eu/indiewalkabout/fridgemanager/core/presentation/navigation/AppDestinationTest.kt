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
}
