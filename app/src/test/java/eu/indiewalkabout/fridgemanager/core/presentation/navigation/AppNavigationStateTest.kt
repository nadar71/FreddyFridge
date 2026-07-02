package eu.indiewalkabout.fridgemanager.core.presentation.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class AppNavigationStateTest {

    @Test
    fun `top level navigation switches selected tab without growing root stack`() {
        val navigationState = AppNavigationState()

        navigationState.navigate(AppDestination.Expired)

        assertEquals(TopLevelDestination.EXPIRED, navigationState.selectedTab)
        assertEquals(listOf(AppDestination.Expired), navigationState.currentBackStack.toList())
    }

    @Test
    fun `secondary destinations are pushed once on current tab stack`() {
        val navigationState = AppNavigationState()

        navigationState.openSettings()
        navigationState.openSettings()
        navigationState.openCredits()

        assertEquals(
            listOf(
                AppDestination.Main,
                AppDestination.Settings,
                AppDestination.Credits
            ),
            navigationState.currentBackStack.toList()
        )
    }

    @Test
    fun `go back pops only secondary destinations`() {
        val navigationState = AppNavigationState()

        navigationState.openSettings()
        navigationState.goBack()
        navigationState.goBack()

        assertEquals(listOf(AppDestination.Main), navigationState.currentBackStack.toList())
        assertEquals(TopLevelDestination.MAIN, navigationState.selectedTab)
    }

    @Test
    fun `snapshot restore keeps selected tab and nested stacks`() {
        val navigationState = AppNavigationState()

        navigationState.openSettings()
        navigationState.navigate(AppDestination.Expired)
        navigationState.openSettings()

        val restoredState = AppNavigationState.restore(navigationState.snapshot())

        assertEquals(TopLevelDestination.EXPIRED, restoredState.selectedTab)
        assertEquals(
            listOf(AppDestination.Expired, AppDestination.Settings),
            restoredState.currentBackStack.toList()
        )

        restoredState.selectTab(TopLevelDestination.MAIN)
        assertEquals(
            listOf(AppDestination.Main, AppDestination.Settings),
            restoredState.currentBackStack.toList()
        )
    }
}
