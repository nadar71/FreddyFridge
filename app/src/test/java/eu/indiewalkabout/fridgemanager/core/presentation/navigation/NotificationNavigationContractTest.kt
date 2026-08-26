package eu.indiewalkabout.fridgemanager.core.presentation.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NotificationNavigationContractTest {

    @Test
    fun `next-days notification targets expiring screen`() {
        assertEquals(
            AppDestination.Expiring,
            NotificationNavigationContract.destinationFromRoute(
                NotificationNavigationContract.NEXT_DAYS_ROUTE
            ),
        )
    }

    @Test
    fun `today notification targets main screen`() {
        assertEquals(
            AppDestination.Main,
            NotificationNavigationContract.destinationFromRoute(
                NotificationNavigationContract.TODAY_ROUTE
            ),
        )
    }

    @Test
    fun `unknown notification route is ignored`() {
        assertNull(NotificationNavigationContract.destinationFromRoute("unknown_route"))
        assertNull(NotificationNavigationContract.destinationFromRoute(null))
    }
}
