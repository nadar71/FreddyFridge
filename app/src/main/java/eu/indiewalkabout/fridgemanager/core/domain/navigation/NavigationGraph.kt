package eu.indiewalkabout.fridgemanager.core.domain.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavigationGraph(
    navController: NavHostController,
) {

    Log.d("DEBUG_ROUTING", "NavigationGraph: called")

    // Padding parameter provided by Scaffold
    // Applying innerPadding to NavHos avoid overlapping with BottomNavigationBar
    NavHost(
        navController,
        startDestination = AppDestinationRoutes.HomeScreen.route,
    ) {

        Log.d("DEBUG_ROUTING", "NavHost: called")

        composable(route = AppDestinationRoutes.HomeScreen.route) {
            AppDestinationRoutes.HomeScreen()
        }
        
        composable(route = AppDestinationRoutes.InsertFoodScreen.route) {
            AppDestinationRoutes.InsertFoodScreen()
        }

        composable(route = AppDestinationRoutes.FoodExpiringScreen.route) {
            AppDestinationRoutes.FoodExpiringScreen()
        }

        composable(route = AppDestinationRoutes.FoodExpiredScreen.route) {
            AppDestinationRoutes.FoodExpiredScreen()
        }

        composable(route = AppDestinationRoutes.FoodConsumedScreen.route) {
            AppDestinationRoutes.FoodConsumedScreen()
        }

        composable(route = AppDestinationRoutes.SettingsScreen.route) {
            AppDestinationRoutes.SettingsScreen()

    }

}