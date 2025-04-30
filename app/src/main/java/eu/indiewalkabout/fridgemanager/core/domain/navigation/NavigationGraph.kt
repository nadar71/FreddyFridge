package eu.indiewalkabout.fridgemanager.core.domain.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import eu.indiewalkabout.fridgemanager.presentation.ui.credits.CreditsScreen
import eu.indiewalkabout.fridgemanager.presentation.ui.food.FoodConsumedScreen
import eu.indiewalkabout.fridgemanager.presentation.ui.food.FoodExpiredScreen
import eu.indiewalkabout.fridgemanager.presentation.ui.food.FoodExpiringScreen
import eu.indiewalkabout.fridgemanager.presentation.ui.food.InsertFoodScreen
import eu.indiewalkabout.fridgemanager.presentation.ui.intromain.MainScreen
import eu.indiewalkabout.fridgemanager.presentation.ui.settings.SettingsScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
) {

    Log.d("DEBUG_ROUTING", "NavigationGraph: called")

    // Padding parameter provided by Scaffold
    // Applying innerPadding to NavHos avoid overlapping with BottomNavigationBar
    NavHost(
        navController,
        startDestination = AppDestinationRoutes.MainScreen.route,
    ) {

        Log.d("DEBUG_ROUTING", "NavHost: called")

        composable(route = AppDestinationRoutes.MainScreen.route) {
            Log.d("DEBUG_ROUTING", "NavigationGraph: go to MainScreen")
            MainScreen()
        }

        /*composable(route = AppDestinationRoutes.IntroScreen.route) {
            IntroScreen()
        }*/

        composable(route = AppDestinationRoutes.InsertFoodScreen.route) {
            Log.d("DEBUG_ROUTING", "NavigationGraph: go to InsertFoodScreen")
            InsertFoodScreen()
        }

        composable(route = AppDestinationRoutes.FoodExpiringScreen.route) {
            Log.d("DEBUG_ROUTING", "NavigationGraph: go to FoodExpiringScreen")
            FoodExpiringScreen()
        }

        composable(route = AppDestinationRoutes.FoodExpiredScreen.route) {
            FoodExpiredScreen()
        }

        composable(route = AppDestinationRoutes.FoodConsumedScreen.route) {
            FoodConsumedScreen()
        }

        composable(route = AppDestinationRoutes.SettingsScreen.route) {
            Log.d("DEBUG_ROUTING", "NavigationGraph: go to SettingsScreen")
            SettingsScreen()
        }

        composable(route = AppDestinationRoutes.CreditsScreen.route) {
            CreditsScreen()
        }
    }

}