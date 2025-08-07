package eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.settings

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.ump.UserMessagingPlatform
import eu.indiewalkabout.fridgemanager.FreddyFridgeApp.Companion.alarmReminderScheduler
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.data.locals.AppPreferences
import eu.indiewalkabout.fridgemanager.core.data.locals.Constants.NUM_MAX_DAYS_BEFORE_DEADLINE
import eu.indiewalkabout.fridgemanager.core.data.locals.Constants.NUM_MAX_DAILY_NOTIFICATIONS_NUMBER
import eu.indiewalkabout.fridgemanager.core.data.locals.Constants.support_email
import eu.indiewalkabout.fridgemanager.core.presentation.components.BackgroundPattern
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_16
import eu.indiewalkabout.fridgemanager.core.presentation.components.TopBar
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_20
import eu.indiewalkabout.fridgemanager.core.util.GenericUtility.openAppSettings
import eu.indiewalkabout.fridgemanager.core.util.GenericUtility.openAppStore
import eu.indiewalkabout.fridgemanager.feat_notifications.util.extensions.openAlarmSettings
import eu.indiewalkabout.fridgemanager.core.util.extensions.sendEmail
import eu.indiewalkabout.fridgemanager.feat_ads.presentation.AdMobBannerView
import eu.indiewalkabout.fridgemanager.feat_ads.util.ConsentManager
import eu.indiewalkabout.fridgemanager.feat_food.presentation.components.NumberPickerWithTitle
import eu.indiewalkabout.fridgemanager.feat_navigation.domain.navigation.AppDestinationRoutes
import eu.indiewalkabout.fridgemanager.feat_navigation.domain.navigation.AppNavigation.navigate
import eu.indiewalkabout.fridgemanager.feat_notifications.util.extensions.openAppSettings
import eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.settings.components.SettingsGroupTitle
import eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.settings.components.SettingsItem

@Composable
fun SettingsScreen() {
    val TAG = "SettingsScreen"
    Log.d(TAG, "SettingsScreen: shown")
    val colors = LocalAppColors.current
    val context = LocalContext.current
    val activity = LocalActivity.current

    var daysBefore by remember { mutableStateOf(AppPreferences.days_before_deadline) }
    var dailyNotificationsNumber by remember { mutableStateOf(AppPreferences.daily_notifications_number) }

    var showDaysBeforeWheelPicker by remember { mutableStateOf(false) }
    var showNotificationNumEachDayWheelPicker by remember { mutableStateOf(false) }

    // Days before wheel picker
    if (showDaysBeforeWheelPicker) {
        NumberPickerWithTitle(
            title = stringResource(R.string.settings_days_label),
            max = NUM_MAX_DAYS_BEFORE_DEADLINE,
            onItemSelected = {
                daysBefore = it.toInt()
                AppPreferences.days_before_deadline = daysBefore
                Log.d(TAG, "Days before notification selected: $it")
                showDaysBeforeWheelPicker = false
            },
            onDismiss = {
                showDaysBeforeWheelPicker = false
            }
        )
    }

    // Hours frequency wheel picker
    if (showNotificationNumEachDayWheelPicker) {
        NumberPickerWithTitle(
            title = stringResource(R.string.settings_hours_label),
            max = NUM_MAX_DAILY_NOTIFICATIONS_NUMBER,
            onItemSelected = {
                dailyNotificationsNumber = it.toInt()
                AppPreferences.daily_notifications_number = dailyNotificationsNumber
                alarmReminderScheduler.setRepeatingAlarm()
                Log.d(TAG, "Notifications number each day selected: $it")
                showNotificationNumEachDayWheelPicker = false
            },
            onDismiss = {
                showNotificationNumEachDayWheelPicker = false
            }
        )
    }




    Scaffold(
        bottomBar = {
            // BottomNavigationBar(AppNavigation.getNavController())
            // BottomNavigationBar()
        },
        containerColor = colors.primaryColor
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            BackgroundPattern()

            Column {// Top Bar with back + settings
                TopBar(
                    title = stringResource(id = R.string.settings_title),
                    titleColor = colors.brown,
                    titleStyle = text_20(colors.brown, true),
                    paddingTop = 20.dp,
                    paddingBottom = 8.dp,
                )

                AdMobBannerView()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 0.dp)
                        .verticalScroll(rememberScrollState())
                ) {


                    Spacer(modifier = Modifier.height(16.dp))

                    SettingsGroupTitle(
                        title = stringResource(id = R.string.settings_category_title),
                        style = text_16(colors.brown)
                    )

                    SettingsItem(
                        title = stringResource(id = R.string.settings_how_many_days_before_title),
                        subtitle = daysBefore.toString(),
                        modifier = Modifier.clickable {
                            showDaysBeforeWheelPicker = true
                        }
                    )

                    SettingsItem(
                        title = stringResource(id = R.string.settings_how_many_hours_title),
                        subtitle = dailyNotificationsNumber.toString(),
                        modifier = Modifier.clickable {
                            showNotificationNumEachDayWheelPicker = true
                        }
                    )

                    SettingsItem(
                        title = stringResource(id = R.string.exact_alarm_permission_settings_title),
                        subtitle = stringResource(id = R.string.exact_alarm_permission_settings_message),
                        modifier = Modifier.clickable {
                            context.openAlarmSettings()
                        }
                    )

                    SettingsItem(
                        title = stringResource(id = R.string.notification_permission_title),
                        subtitle = stringResource(id = R.string.notification_permission_message),
                        modifier = Modifier.clickable {
                            context.openAppSettings()
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    SettingsGroupTitle(
                        title = stringResource(id = R.string.settings_category_help_title),
                        style = text_16(colors.brown)
                    )

                    SettingsItem(
                        title = stringResource(id = R.string.credits_title_label),
                        subtitle = stringResource(id = R.string.credits_section_subtitle_summary),
                        modifier = Modifier.clickable {
                            navigate(AppDestinationRoutes.CreditsScreen.route)
                        }
                    )

                    /*SettingsItem(
                        title = stringResource(id = R.string.gdpr_btn_title),
                        subtitle = stringResource(id = R.string.gdpr_btn_summary)
                    )*/

                    SettingsItem(
                        title = stringResource(id = R.string.gdpr_btn_title),
                        subtitle = stringResource(id = R.string.gdpr_btn_summary),
                        modifier = Modifier.clickable {
                            UserMessagingPlatform.getConsentInformation(context).reset()
                            Toast.makeText(
                                context,
                                context.getString(R.string.gdpr_dialog_will_show_again),
                                Toast.LENGTH_LONG
                            ).show()

                            // Trigger re-consent (optional)
                            ConsentManager.requestConsent(
                                context = context,
                                activity = activity,
                                onConsentReady = { canRequestAds ->
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.gdpr_dialog_reset_done),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    )


                    SettingsItem(
                        title = stringResource(id = R.string.settings_goto_system_app_settings_title),
                        subtitle = stringResource(id = R.string.settings_goto_system_app_settings_label),
                        modifier = Modifier.clickable {
                            openAppSettings(context)
                        }
                    )

                    SettingsItem(
                        title = stringResource(id = R.string.settings_review_btn_title),
                        subtitle = stringResource(id = R.string.settings_review_btn_summary),
                        modifier = Modifier.clickable {
                            openAppStore(context, context.packageName)
                        }
                    )

                    /*SettingsItem(
                        title = stringResource(id = R.string.settings_myapps_btn_title),
                        subtitle = stringResource(id = R.string.settings_myapps_btn_summary)
                    )*/

                    SettingsItem(
                        title = stringResource(id = R.string.settings_support_btn_title),
                        subtitle = stringResource(id = R.string.settings_support_btn_summary)
                                + " " + support_email,
                        modifier = Modifier.clickable {
                            sendEmail(context, support_email)
                        }
                    )


                    // Test Notifications Section
                    /*Text(
                        text = "Test Notifications",
                        style = MaterialTheme.typography.titleMedium,
                        color = primaryColor,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    // Test Today's Notification Button
                    Button(
                        onClick = {
                            val testFood = listOf(
                                FoodEntry(
                                    id = 9991,
                                    name = "Test Milk",
                                    expiringAt = LocalDate.now(),
                                    timezoneId = ZoneId.systemDefault().id,
                                    isProductOpen = false,
                                    order_number = 1
                                ),
                                FoodEntry(
                                    id = 9992,
                                    name = "Test Yogurt",
                                    expiringAt = LocalDate.now(),
                                    timezoneId = ZoneId.systemDefault().id,
                                    isProductOpen = true,
                                    order_number = 2
                                )
                            )
                            NotificationsUtility.remindTodayExpiringFood(context, testFood)
                            Toast.makeText(context, "Today's test notification triggered", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text("Test Today's Notification")
                    }

                    // Test Next Days Notification Button
                    Button(
                        onClick = {
                            val testFood = listOf(
                                FoodEntry(
                                    id = 9993,
                                    name = "Test Cheese",
                                    expiringAt = LocalDate.now().plusDays(2),
                                    timezoneId = ZoneId.systemDefault().id,
                                    isProductOpen = false,
                                    order_number = 3
                                ),
                                FoodEntry(
                                    id = 9994,
                                    name = "Test Eggs",
                                    expiringAt = LocalDate.now().plusDays(3),
                                    timezoneId = ZoneId.systemDefault().id,
                                    isProductOpen = true,
                                    order_number = 4
                                )
                            )
                            NotificationsUtility.remindNextDaysExpiringFood(context, testFood)
                            Toast.makeText(context, "Next days test notification triggered", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text("Test Next Days Notification")
                    }

                    Spacer(modifier = Modifier.height(16.dp))*/
                }
            }
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    FreddyFridgeTheme {
        SettingsScreen()
    }
}



