package eu.indiewalkabout.fridgemanager.feat_starting.presentation.ui.intromain

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import eu.indiewalkabout.fridgemanager.core.util.extensions.needsExactAlarmPermission
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.android.AndroidEntryPoint
import eu.indiewalkabout.fridgemanager.FreddyFridgeApp.Companion.alarmReminderScheduler
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.reminder.withalarmmanager.AlarmReminderScheduler
import eu.indiewalkabout.fridgemanager.core.util.extensions.RequestExactAlarmPermissionDialog
import eu.indiewalkabout.fridgemanager.core.util.extensions.checkAndRequestExactAlarmPermission
import eu.indiewalkabout.fridgemanager.feat_navigation.domain.navigation.AppNavigation
import eu.indiewalkabout.fridgemanager.feat_navigation.domain.navigation.NavigationGraph


@AndroidEntryPoint
class MainActivity: AppCompatActivity()  {
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Main_activity created")

        // start scheduler for notifications reminder
        alarmReminderScheduler = AlarmReminderScheduler(this)

        // Handle deep link from notification
        handleIntent(intent)

        setContent {
            FreddyFridgeTheme {
                val context = LocalContext.current
                var showPermissionDialog by remember { mutableStateOf(needsExactAlarmPermission(context)) }

                if (showPermissionDialog) {
                    RequestExactAlarmPermissionDialog(
                        onDismiss = { showPermissionDialog = false },
                        onPermissionGranted = {
                            // Recheck permission after user returns from settings
                            showPermissionDialog = needsExactAlarmPermission(context)
                            if (!showPermissionDialog) {
                                alarmReminderScheduler.setRepeatingAlarm()
                            }
                        }
                    )
                }

                AppNavigation.NavigationInit()
                NavigationGraph(AppNavigation.appNavHostController)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.let { handleIntent(it) }
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

    override fun onResume() {
        super.onResume()
        checkAndRequestExactAlarmPermission()
        alarmReminderScheduler.setRepeatingAlarm()
    }
}