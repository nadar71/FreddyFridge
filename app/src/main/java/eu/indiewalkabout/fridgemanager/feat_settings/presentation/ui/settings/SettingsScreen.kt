package eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.settings

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.data.locals.Constants.NUM_MAX_DAYS_BEFORE_DEADLINE
import eu.indiewalkabout.fridgemanager.core.data.locals.Constants.NUM_MAX_DAILY_NOTIFICATIONS_NUMBER
import eu.indiewalkabout.fridgemanager.core.data.locals.Constants.support_email
import eu.indiewalkabout.fridgemanager.core.presentation.components.BackgroundPattern
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_16
import eu.indiewalkabout.fridgemanager.core.presentation.components.TopBar
import eu.indiewalkabout.fridgemanager.core.util.GenericUtility.openAppSettings
import eu.indiewalkabout.fridgemanager.core.util.GenericUtility.openAppStore
import eu.indiewalkabout.fridgemanager.core.util.extensions.sendEmail
import eu.indiewalkabout.fridgemanager.feat_food.presentation.components.NumberPickerWithTitle
import eu.indiewalkabout.fridgemanager.feat_navigation.domain.navigation.AppDestinationRoutes
import eu.indiewalkabout.fridgemanager.feat_navigation.domain.navigation.AppNavigation
import eu.indiewalkabout.fridgemanager.feat_navigation.domain.navigation.AppNavigation.navigate
import eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.settings.components.SettingsGroupTitle
import eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.settings.components.SettingsItem

@Composable
fun SettingsScreen() {
    val TAG = "SettingsScreen"
    Log.d(TAG, "SettingsScreen: shown")
    val colors = LocalAppColors.current
    val context = LocalContext.current

    var daysBefore by remember { mutableStateOf(0) }
    var dailyNotificationNumber by remember { mutableStateOf(0) }

    var showDaysBeforeWheelPicker by remember { mutableStateOf(false) }
    var showNotificationNumEachDayWheelPicker by remember { mutableStateOf(false) }

    // Days before wheel picker
    if (showDaysBeforeWheelPicker) {
        NumberPickerWithTitle(
            title = stringResource(R.string.settings_days_label),
            max = NUM_MAX_DAYS_BEFORE_DEADLINE,
            onItemSelected = {
                daysBefore = it.toInt()
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
                dailyNotificationNumber = it.toInt()
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // Top Bar with back + settings
                TopBar(
                    title = stringResource(id = R.string.settings_title),
                    titleColor = colors.brown,
                )

                Spacer(modifier = Modifier.height(32.dp))

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
                    subtitle = dailyNotificationNumber.toString(),
                    modifier = Modifier.clickable {
                        showNotificationNumEachDayWheelPicker = true
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                SettingsGroupTitle(
                    title = stringResource(id = R.string.settings_category_help_title),
                    style = text_16(colors.brown)
                )

                SettingsItem(
                    title = stringResource(id = R.string.credits_btn_title),
                    subtitle = stringResource(id = R.string.credits_btn_summary),
                    modifier = Modifier.clickable {
                        navigate(AppDestinationRoutes.CreditsScreen.route)
                    }
                )

                /*SettingsItem(
                    title = stringResource(id = R.string.gdpr_btn_title),
                    subtitle = stringResource(id = R.string.gdpr_btn_summary)
                )*/

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
                    subtitle = stringResource(id = R.string.settings_support_btn_summary) + " " + support_email,
                    modifier = Modifier.clickable {
                        sendEmail(context, support_email)
                    }
                )
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



