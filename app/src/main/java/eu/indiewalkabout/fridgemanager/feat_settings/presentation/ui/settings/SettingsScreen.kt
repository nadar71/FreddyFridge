package eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.settings

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.ump.UserMessagingPlatform
import eu.indiewalkabout.fridgemanager.FreddyFridgeApp.Companion.alarmReminderScheduler
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.data.locals.Constants.NUM_MAX_DAYS_BEFORE_DEADLINE
import eu.indiewalkabout.fridgemanager.core.data.locals.Constants.NUM_MAX_DAILY_NOTIFICATIONS_NUMBER
import eu.indiewalkabout.fridgemanager.core.data.locals.Constants.support_email
import eu.indiewalkabout.fridgemanager.core.presentation.components.GeneralModalDialog
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_16
import eu.indiewalkabout.fridgemanager.core.presentation.components.TopBar
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.alertRed
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.secondaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_20
import eu.indiewalkabout.fridgemanager.core.util.GenericUtility.openAppSettings
import eu.indiewalkabout.fridgemanager.core.util.GenericUtility.openAppStore
import eu.indiewalkabout.fridgemanager.core.util.extensions.sendEmail
import eu.indiewalkabout.fridgemanager.feat_ads.presentation.AdMobBannerView
import eu.indiewalkabout.fridgemanager.feat_ads.util.ConsentManager
import eu.indiewalkabout.fridgemanager.feat_food.presentation.components.NumberPickerWithTitle
import eu.indiewalkabout.fridgemanager.core.presentation.navigation.components.BottomNavigationBar
import eu.indiewalkabout.fridgemanager.core.presentation.navigation.components.SecondaryScreenScaffold
import eu.indiewalkabout.fridgemanager.feat_notifications.util.extensions.openAppSettings
import eu.indiewalkabout.fridgemanager.feat_settings.presentation.components.SettingsGroupTitle
import eu.indiewalkabout.fridgemanager.feat_settings.presentation.components.SettingsItem

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    onOpenCredits: () -> Unit = {},
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreenEffects(
        events = settingsViewModel.events,
        onRescheduleNotifications = alarmReminderScheduler::setRepeatingAlarm,
        onClearAppData = {
            context.databaseList().forEach(context::deleteDatabase)
            context.cacheDir.deleteRecursively()
            Toast.makeText(
                context,
                context.getString(R.string.settings_delete_end_description),
                Toast.LENGTH_LONG,
            ).show()
        },
    )

    SettingsScreenContent(
        uiState = uiState,
        onOpenCredits = onOpenCredits,
        onUpdateDaysBeforeDeadline = settingsViewModel::updateDaysBeforeDeadline,
        onUpdateDailyNotificationCount = settingsViewModel::updateDailyNotificationCount,
        onOpenNotificationSettings = context::openAppSettings,
        onResetConsent = {
            UserMessagingPlatform.getConsentInformation(context).reset()
            Toast.makeText(
                context,
                context.getString(R.string.gdpr_dialog_will_show_again),
                Toast.LENGTH_LONG,
            ).show()
            ConsentManager.requestConsent(
                context = context,
                activity = activity,
                onConsentReady = {
                    Toast.makeText(
                        context,
                        context.getString(R.string.gdpr_dialog_reset_done),
                        Toast.LENGTH_SHORT,
                    ).show()
                },
            )
        },
        onOpenSystemAppSettings = { openAppSettings(context) },
        onOpenAppStore = { openAppStore(context, context.packageName) },
        onSendSupportEmail = { sendEmail(context, support_email) },
        onResetAppData = settingsViewModel::resetPreferences,
        adBanner = {
            AdMobBannerView(adUnitId = stringResource(R.string.admob_key_bottom_banner))
        },
    )
}

@Composable
fun SettingsScreenContent(
    uiState: SettingsUiState,
    onOpenCredits: () -> Unit,
    onUpdateDaysBeforeDeadline: (Int) -> Unit,
    onUpdateDailyNotificationCount: (Int) -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onResetConsent: () -> Unit,
    onOpenSystemAppSettings: () -> Unit,
    onOpenAppStore: () -> Unit,
    onSendSupportEmail: () -> Unit,
    onResetAppData: () -> Unit,
    adBanner: @Composable () -> Unit,
) {
    val tag = "SettingsScreen"
    val colors = LocalAppColors.current

    val scrollState = rememberScrollState()
    var showDeleteDialog by remember { mutableStateOf(false) }


    var showDaysBeforeWheelPicker by remember { mutableStateOf(false) }
    var showNotificationNumEachDayWheelPicker by remember { mutableStateOf(false) }

    // ------------------------------------ LOGIC -----------------------------------------------------

    // Days before wheel picker
    if (showDaysBeforeWheelPicker) {
        NumberPickerWithTitle(
            title = stringResource(R.string.settings_days_label),
            max = NUM_MAX_DAYS_BEFORE_DEADLINE,
            onItemSelected = {
                onUpdateDaysBeforeDeadline(it.toInt())
                Log.d(tag, "Days before notification selected: $it")
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
                onUpdateDailyNotificationCount(it.toInt())
                Log.d(tag, "Notifications number each day selected: $it")
                showNotificationNumEachDayWheelPicker = false
            },
            onDismiss = {
                showNotificationNumEachDayWheelPicker = false
            }
        )
    }

    // ------------------------------------ UI -----------------------------------------------------
    SecondaryScreenScaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedDestination = null,
                onDestinationSelected = {},
            )
        },
    ) {
        Column {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 0.dp)
                    .verticalScroll(scrollState)
            ) {

                    TopBar(
                        title = stringResource(id = R.string.settings_title),
                        titleColor = colors.brown,
                        titleStyle = text_20(colors.brown, true),
                        paddingTop = 20.dp,
                        paddingBottom = 8.dp,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SettingsGroupTitle(
                        title = stringResource(id = R.string.settings_category_title),
                        style = text_16(colors.brown)
                    )

                    SettingsItem(
                        title = stringResource(id = R.string.settings_how_many_days_before_title),
                        subtitle = uiState.daysBeforeDeadline.toString(),
                        modifier = Modifier.clickable {
                            showDaysBeforeWheelPicker = true
                        }
                    )

                    SettingsItem(
                        title = stringResource(id = R.string.settings_how_many_hours_title),
                        subtitle = uiState.dailyNotificationCount.toString(),
                        modifier = Modifier.clickable {
                            showNotificationNumEachDayWheelPicker = true
                        }
                    )

                    SettingsItem(
                        title = stringResource(id = R.string.notification_permission_title),
                        subtitle = stringResource(id = R.string.notification_permission_message),
                        modifier = Modifier.clickable(onClick = onOpenNotificationSettings)
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
                            onOpenCredits()
                        }
                    )

                    /*SettingsItem(
                        title = stringResource(id = R.string.gdpr_btn_title),
                        subtitle = stringResource(id = R.string.gdpr_btn_summary)
                    )*/

                    SettingsItem(
                        title = stringResource(id = R.string.gdpr_btn_title),
                        subtitle = stringResource(id = R.string.gdpr_btn_summary),
                        modifier = Modifier.clickable(onClick = onResetConsent)
                    )


                    SettingsItem(
                        title = stringResource(id = R.string.settings_goto_system_app_settings_title),
                        subtitle = stringResource(id = R.string.settings_goto_system_app_settings_label),
                        modifier = Modifier.clickable(onClick = onOpenSystemAppSettings)
                    )

                    SettingsItem(
                        title = stringResource(id = R.string.settings_review_btn_title),
                        subtitle = stringResource(id = R.string.settings_review_btn_summary),
                        modifier = Modifier.clickable(onClick = onOpenAppStore)
                    )

                    /*SettingsItem(
                        title = stringResource(id = R.string.settings_myapps_btn_title),
                        subtitle = stringResource(id = R.string.settings_myapps_btn_summary)
                    )*/

                    SettingsItem(
                        title = stringResource(id = R.string.settings_support_btn_title),
                        subtitle = stringResource(id = R.string.settings_support_btn_summary)
                                + " " + support_email,
                        modifier = Modifier.clickable(onClick = onSendSupportEmail)
                    )

                    SettingsItem(
                        title = stringResource(id = R.string.settings_reset_btn_title),
                        subtitle = stringResource(id = R.string.settings_reset_btn_description),
                        modifier = Modifier.clickable {
                            showDeleteDialog = true
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

            Spacer(modifier = Modifier.height(4.dp))

            adBanner()

            Spacer(modifier = Modifier.height(4.dp))

        }
    }

    // delete dialog
    if (showDeleteDialog) {
        GeneralModalDialog(
            title = stringResource(id = R.string.settings_delete_confirm_title),
            titleStyle = text_20(colors.brown, true),
            message = stringResource(id = R.string.settings_delete_confirm_description),
            messageStyle = text_16(colors.brown),
            image = R.drawable.ic_warning_white,
            buttonStrokeWidth = 1.dp,
            buttonStrokeColor = secondaryColor,
            leftButtonLabel = stringResource(id = R.string.generic_reset_label),
            rightButtonLabel = stringResource(id = R.string.generic_cancel),
            leftButtonBackgroundColor = alertRed,
            rightButtonBackgroundColor = Color.Gray,
            onLeftButtonAction = {
                showDeleteDialog = false
                onResetAppData()
            },
            onRightButtonAction = {
                showDeleteDialog = false
            },
            onDismissRequest = {
                showDeleteDialog = false
            }
        )
    }

}

@Preview
@Composable
fun SettingsScreenPreview() {
    FreddyFridgeTheme {
        SettingsScreenContent(
            uiState = SettingsUiState(),
            onOpenCredits = {},
            onUpdateDaysBeforeDeadline = {},
            onUpdateDailyNotificationCount = {},
            onOpenNotificationSettings = {},
            onResetConsent = {},
            onOpenSystemAppSettings = {},
            onOpenAppStore = {},
            onSendSupportEmail = {},
            onResetAppData = {},
            adBanner = {},
        )
    }
}
