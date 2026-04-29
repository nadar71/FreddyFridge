package eu.indiewalkabout.fridgemanager.core.presentation.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.compose.rememberNavController


// This Make NavHost Controller available all over the app session without passing it in each screen
// - Fixed orientation (no configuration changes)
// - Single Activity that lives exactly as long as the app process
// - Only supports light mode
@SuppressLint("StaticFieldLeak")
object AppNavigation{

    lateinit var appNavHostController: NavHostController

    // simplify navigation call
    fun navigate(route: String, navOptions: NavOptions? = null, extras: Navigator.Extras? = null) {
        appNavHostController.navigate(route, navOptions, extras)
    }

    @Composable
    fun getNavController(): NavHostController {
        Log.d("DEBUG_ROUTING", "getNavController(): called")
        return rememberNavController()
    }


    @Composable
    fun NavigationInit() {
        appNavHostController = getNavController()
        Log.d("DEBUG_ROUTING", "navigationInit(): called, appNavHostController : $appNavHostController")
    }
}