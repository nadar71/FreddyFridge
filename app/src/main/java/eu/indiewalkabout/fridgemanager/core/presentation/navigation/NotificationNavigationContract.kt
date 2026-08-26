package eu.indiewalkabout.fridgemanager.core.presentation.navigation

object NotificationNavigationContract {
    const val DESTINATION_EXTRA = "destination"
    const val NEXT_DAYS_ROUTE = NavigationScreenConstants.FOOD_EXPIRING_SCREEN
    const val TODAY_ROUTE = NavigationScreenConstants.MAIN_SCREEN

    fun destinationFromRoute(route: String?): AppDestination? {
        return AppDestination.fromLegacyRoute(route)
    }
}
