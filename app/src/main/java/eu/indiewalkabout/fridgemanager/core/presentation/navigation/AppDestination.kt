package eu.indiewalkabout.fridgemanager.core.presentation.navigation

import androidx.navigation3.runtime.NavKey

sealed interface AppDestination : NavKey {

    data object Main : AppDestination

    data object Expiring : AppDestination

    data object Expired : AppDestination

    data object Consumed : AppDestination

    data object Settings : AppDestination

    data object Credits : AppDestination

    val topLevelDestination: TopLevelDestination?
        get() = when (this) {
            Main -> TopLevelDestination.MAIN
            Expiring -> TopLevelDestination.EXPIRING
            Expired -> TopLevelDestination.EXPIRED
            Consumed -> TopLevelDestination.CONSUMED
            Settings,
            Credits -> null
        }

    companion object {
        fun fromLegacyRoute(route: String?): AppDestination? {
            return when (route) {
                NavigationScreenConstants.MAIN_SCREEN -> Main
                NavigationScreenConstants.FOOD_EXPIRING_SCREEN -> Expiring
                NavigationScreenConstants.FOOD_EXPIRED_SCREEN -> Expired
                NavigationScreenConstants.FOOD_CONSUMED_SCREEN -> Consumed
                NavigationScreenConstants.SETTINGS_SCREEN -> Settings
                NavigationScreenConstants.CREDITS_SCREEN -> Credits
                else -> null
            }
        }

        fun toLegacyRoute(destination: AppDestination): String {
            return when (destination) {
                Main -> NavigationScreenConstants.MAIN_SCREEN
                Expiring -> NavigationScreenConstants.FOOD_EXPIRING_SCREEN
                Expired -> NavigationScreenConstants.FOOD_EXPIRED_SCREEN
                Consumed -> NavigationScreenConstants.FOOD_CONSUMED_SCREEN
                Settings -> NavigationScreenConstants.SETTINGS_SCREEN
                Credits -> NavigationScreenConstants.CREDITS_SCREEN
            }
        }
    }
}

enum class TopLevelDestination(
    val root: AppDestination,
) {
    EXPIRED(AppDestination.Expired),
    CONSUMED(AppDestination.Consumed),
    MAIN(AppDestination.Main),
    EXPIRING(AppDestination.Expiring),
}
