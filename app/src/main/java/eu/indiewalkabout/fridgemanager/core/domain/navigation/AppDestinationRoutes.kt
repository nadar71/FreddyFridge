package eu.indiewalkabout.fridgemanager.core.domain.navigation

import eu.indiewalkabout.fridgemanager.core.domain.navigation.NavigationScreenConstants.CREDITS_SCREEN
import eu.indiewalkabout.fridgemanager.core.domain.navigation.NavigationScreenConstants.FOOD_CONSUMED_SCREEN
import eu.indiewalkabout.fridgemanager.core.domain.navigation.NavigationScreenConstants.FOOD_EXPIRED_SCREEN
import eu.indiewalkabout.fridgemanager.core.domain.navigation.NavigationScreenConstants.FOOD_EXPIRING_SCREEN
import eu.indiewalkabout.fridgemanager.core.domain.navigation.NavigationScreenConstants.INSERT_FOOD_SCREEN
import eu.indiewalkabout.fridgemanager.core.domain.navigation.NavigationScreenConstants.MAIN_SCREEN
import eu.indiewalkabout.fridgemanager.core.domain.navigation.NavigationScreenConstants.SETTINGS_SCREEN

sealed class AppDestinationRoutes(var name:String, var route:String) {

    object MainScreen : AppDestinationRoutes("MainScreen", MAIN_SCREEN )
    // object IntroScreen : AppDestinationRoutes("IntroScreen", INTRO_SCREEN )
    object InsertFoodScreen : AppDestinationRoutes("InsertFoodScreen", INSERT_FOOD_SCREEN)
    object FoodExpiringScreen : AppDestinationRoutes("FoodExpiringScreen", FOOD_EXPIRING_SCREEN)
    object FoodExpiredScreen : AppDestinationRoutes("FoodExpiredScreen", FOOD_EXPIRED_SCREEN)
    object FoodConsumedScreen : AppDestinationRoutes("FoodConsumedScreen", FOOD_CONSUMED_SCREEN)
    object SettingsScreen : AppDestinationRoutes("SettingsScreen", SETTINGS_SCREEN)
    object CreditsScreen : AppDestinationRoutes("CreditsScreen", CREDITS_SCREEN)

}
