package eu.indiewalkabout.fridgemanager.feat_starting.presentation.ui.intromain

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.feat_navigation.domain.navigation.AppNavigation
import eu.indiewalkabout.fridgemanager.feat_navigation.domain.navigation.NavigationGraph


@AndroidEntryPoint
class MainActivity: AppCompatActivity()  {
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Main_activity created")

        // Handle deep link from notification
        handleIntent(intent)

        setContent {
            FreddyFridgeTheme {
                AppNavigation.NavigationInit()
                NavigationGraph(AppNavigation.appNavHostController)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    override fun onStart() {
        super.onStart()
    }

    private fun handleIntent(intent: Intent) {
        val destination = intent.getStringExtra("destination")
        destination?.let { route ->
            Log.d(TAG, "handleIntent: Navigating to $route")
            setContent {
                AppNavigation.NavigationInit()
                AppNavigation.appNavHostController.navigate(route) {
                    // Clear the back stack and make this the start destination
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            }
        }
    }
}