package eu.indiewalkabout.fridgemanager.feat_starting.presentation.ui.intromain

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.feat_navigation.domain.navigation.AppNavigation
import eu.indiewalkabout.fridgemanager.feat_navigation.domain.navigation.NavigationGraph


@AndroidEntryPoint
class MainActivity : AppCompatActivity()  {
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Main_activity created")

        setContent {
            FreddyFridgeTheme {
                AppNavigation.NavigationInit()
                NavigationGraph(AppNavigation.appNavHostController)
            }
        }
    }


    override fun onStart() {
        super.onStart()
    }
}