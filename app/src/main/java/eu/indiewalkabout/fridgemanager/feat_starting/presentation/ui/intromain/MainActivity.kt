package eu.indiewalkabout.fridgemanager.feat_starting.presentation.ui.intromain

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import eu.indiewalkabout.fridgemanager.FreddyFridgeApp
import eu.indiewalkabout.fridgemanager.FreddyFridgeApp.Companion.alarmReminderScheduler
import eu.indiewalkabout.fridgemanager.core.data.locals.AppPreferences
import eu.indiewalkabout.fridgemanager.core.data.locals.Constants.NUM_MAX_OPENINGS
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.feat_ads.util.ConsentManager
import eu.indiewalkabout.fridgemanager.feat_navigation.domain.navigation.AppNavigation
import eu.indiewalkabout.fridgemanager.feat_navigation.domain.navigation.NavigationGraph
import eu.indiewalkabout.fridgemanager.feat_notifications.domain.reminder.AlarmReminderScheduler
import eu.indiewalkabout.fridgemanager.feat_notifications.presentation.components.NotificationPermissionDialog
import eu.indiewalkabout.fridgemanager.feat_notifications.util.extensions.RequestExactAlarmPermissionDialog
import eu.indiewalkabout.fridgemanager.feat_notifications.util.extensions.canScheduleExactAlarms
import eu.indiewalkabout.fridgemanager.feat_notifications.util.extensions.needsExactAlarmPermissionCheck
import eu.indiewalkabout.fridgemanager.feat_notifications.util.extensions.openAlarmSettings


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

        // Check consent
        ConsentManager.requestConsent(this, this@MainActivity) { canRequestAds ->
            MobileAds.initialize(this)
            setContent {
                FreddyFridgeApp.canRequestAdsFlag = canRequestAds
                FreddyFridgeTheme {
                    val context = LocalContext.current
                    var showNotificationPermissionDialog by remember { mutableStateOf(true) }
                    var askForExactAlarmPermission by remember { mutableStateOf(false) }
                    var showExactAlarmPermissionDialog by remember {
                        mutableStateOf(needsExactAlarmPermissionCheck() && !context.canScheduleExactAlarms())
                    }

                    if (showNotificationPermissionDialog &&
                        AppPreferences.app_opening_counter < NUM_MAX_OPENINGS &&
                        !AppPreferences.dontask_again_notification_permissions) {
                        NotificationPermissionDialog(
                            onDismiss = {
                                showNotificationPermissionDialog = false
                                askForExactAlarmPermission = true
                            },
                            onPermissionGranted = {
                                showNotificationPermissionDialog = false
                                askForExactAlarmPermission = true
                            }
                        )
                    } else {
                        askForExactAlarmPermission = true
                    }

                    if (askForExactAlarmPermission &&
                        showExactAlarmPermissionDialog &&
                        AppPreferences.app_opening_counter < NUM_MAX_OPENINGS) {
                        RequestExactAlarmPermissionDialog(
                            onDismiss = {
                                showExactAlarmPermissionDialog = false
                                // Schedule alarms anyway, even if inexact
                                alarmReminderScheduler.setRepeatingAlarm()
                            },
                            onPermissionGranted = {
                                // Only open settings if we're on Android S+
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    context.openAlarmSettings()
                                }
                                // Schedule alarms, they'll be exact if permission was granted
                                alarmReminderScheduler.setRepeatingAlarm()
                                showExactAlarmPermissionDialog = false
                            }
                        )
                    } else {
                        // If we don't need to show the dialog, just schedule the alarms
                        LaunchedEffect(Unit) {
                            alarmReminderScheduler.setRepeatingAlarm()
                        }
                    }

                    AppNavigation.NavigationInit()
                    NavigationGraph(AppNavigation.appNavHostController)
                }
            }
        }

        /*setContent {
            FreddyFridgeTheme {
                val context = LocalContext.current
                var showNotificationPermissionDialog by remember { mutableStateOf(true) }
                var askForExactAlarmPermission by remember { mutableStateOf(false) }
                var showExactAlarmPermissionDialog by remember {
                    mutableStateOf(needsExactAlarmPermissionCheck() && !context.canScheduleExactAlarms())
                }

                if (showNotificationPermissionDialog &&
                    AppPreferences.app_opening_counter < NUM_MAX_OPENINGS &&
                    !AppPreferences.dontask_again_notification_permissions) {
                    NotificationPermissionDialog(
                        onDismiss = {
                            showNotificationPermissionDialog = false
                            askForExactAlarmPermission = true
                                    },
                        onPermissionGranted = {
                            showNotificationPermissionDialog = false
                            askForExactAlarmPermission = true
                        }
                    )
                } else {
                    askForExactAlarmPermission = true
                }

                if (askForExactAlarmPermission &&
                    showExactAlarmPermissionDialog &&
                    AppPreferences.app_opening_counter < NUM_MAX_OPENINGS) {
                    RequestExactAlarmPermissionDialog(
                        onDismiss = {
                            showExactAlarmPermissionDialog = false
                            // Schedule alarms anyway, even if inexact
                            alarmReminderScheduler.setRepeatingAlarm()
                        },
                        onPermissionGranted = {
                            // Only open settings if we're on Android S+
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                context.openAlarmSettings()
                            }
                            // Schedule alarms, they'll be exact if permission was granted
                            alarmReminderScheduler.setRepeatingAlarm()
                            showExactAlarmPermissionDialog = false
                        }
                    )
                } else {
                    // If we don't need to show the dialog, just schedule the alarms
                    LaunchedEffect(Unit) {
                        alarmReminderScheduler.setRepeatingAlarm()
                    }
                }

                AppNavigation.NavigationInit()
                NavigationGraph(AppNavigation.appNavHostController)
            }
        }*/
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
        alarmReminderScheduler.setRepeatingAlarm()
    }

}