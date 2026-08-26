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

    @Test
    fun `reselecting a top level destination preserves its nested stack`() {
        val navigationState = AppNavigationState()
        navigationState.openSettings()

        navigationState.navigate(AppDestination.Main)

        assertEquals(TopLevelDestination.MAIN, navigationState.selectedTab)
        assertEquals(
            listOf(AppDestination.Main, AppDestination.Settings),
            navigationState.currentBackStack.toList(),
        )
    }

    @Test
    fun `notification legacy route selects its matching top level tab`() {
        val navigationState = AppNavigationState()
        val destination = AppDestination.fromLegacyRoute(
            NavigationScreenConstants.FOOD_EXPIRED_SCREEN
        )

        requireNotNull(destination)
        navigationState.navigate(destination)

        assertEquals(TopLevelDestination.EXPIRED, navigationState.selectedTab)
        assertEquals(listOf(AppDestination.Expired), navigationState.currentBackStack.toList())
    }

    @Test
    fun `restoring empty state recreates all default roots`() {
        val navigationState = AppNavigationState.restore(emptyList())

        assertEquals(TopLevelDestination.MAIN, navigationState.selectedTab)
        TopLevelDestination.entries.forEach { tab ->
            navigationState.selectTab(tab)
            assertEquals(listOf(tab.root), navigationState.currentBackStack.toList())
        }
    }
}
