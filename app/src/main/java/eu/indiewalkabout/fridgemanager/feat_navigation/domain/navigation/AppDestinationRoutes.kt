package eu.indiewalkabout.fridgemanager.feat_navigation.domain.navigation

sealed class AppDestinationRoutes(var name:String, var route:String) {

    object MainScreen : AppDestinationRoutes("MainScreen", NavigationScreenConstants.MAIN_SCREEN)
    // object IntroScreen : AppDestinationRoutes("IntroScreen", INTRO_SCREEN )
    object InsertFoodScreen : AppDestinationRoutes("InsertFoodScreen",
        NavigationScreenConstants.INSERT_FOOD_SCREEN
    )
    object FoodExpiringScreen : AppDestinationRoutes("FoodExpiringScreen",
        NavigationScreenConstants.FOOD_EXPIRING_SCREEN
    )
    object FoodExpiredScreen : AppDestinationRoutes("FoodExpiredScreen",
        NavigationScreenConstants.FOOD_EXPIRED_SCREEN
    )
    object FoodConsumedScreen : AppDestinationRoutes("FoodConsumedScreen",
        NavigationScreenConstants.FOOD_CONSUMED_SCREEN
    )
    object SettingsScreen : AppDestinationRoutes("SettingsScreen",
        NavigationScreenConstants.SETTINGS_SCREEN
    )
    object CreditsScreen : AppDestinationRoutes("CreditsScreen",
        NavigationScreenConstants.CREDITS_SCREEN
    )

}
