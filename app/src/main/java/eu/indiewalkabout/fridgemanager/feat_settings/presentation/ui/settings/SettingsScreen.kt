package eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.settings

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.components.BackgroundPattern
import eu.indiewalkabout.fridgemanager.core.presentation.components.BottomNavigationBar
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_16
import eu.indiewalkabout.fridgemanager.core.presentation.components.TopBar
import eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.settings.components.SettingsGroupTitle
import eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.settings.components.SettingsItem

@Composable
fun SettingsScreen() {
    val TAG = "SettingsScreen"
    Log.d(TAG, "SettingsScreen: shown")
    val colors = LocalAppColors.current

    Scaffold(
        bottomBar = {
            BottomNavigationBar()
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
                    subtitle = "2" // Ideally this comes from your settings storage
                )

                SettingsItem(
                    title = stringResource(id = R.string.settings_how_many_hours_title),
                    subtitle = stringResource(id = R.string.settings_notify_every_01) + "6"
                            + stringResource(id = R.string.settings_notify_every_02)
                )

                Spacer(modifier = Modifier.height(32.dp))

                SettingsGroupTitle(
                    title = stringResource(id = R.string.settings_category_help_title),
                    style = text_16(colors.brown)
                )

                SettingsItem(
                    title = stringResource(id = R.string.credits_btn_title),
                    subtitle = stringResource(id = R.string.credits_btn_summary)
                )

                SettingsItem(
                    title = stringResource(id = R.string.gdpr_btn_title),
                    subtitle = stringResource(id = R.string.gdpr_btn_summary)
                )

                SettingsItem(
                    title = stringResource(id = R.string.settings_goto_system_app_settings_title),
                    subtitle = stringResource(id = R.string.settings_goto_system_app_settings_label)
                )

                SettingsItem(
                    title = stringResource(id = R.string.settings_review_btn_title),
                    subtitle = stringResource(id = R.string.settings_review_btn_summary)
                )

                SettingsItem(
                    title = stringResource(id = R.string.settings_myapps_btn_title),
                    subtitle = stringResource(id = R.string.settings_myapps_btn_summary)
                )

                SettingsItem(
                    title = stringResource(id = R.string.settings_support_btn_title),
                    subtitle = stringResource(id = R.string.settings_support_btn_summary)
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



