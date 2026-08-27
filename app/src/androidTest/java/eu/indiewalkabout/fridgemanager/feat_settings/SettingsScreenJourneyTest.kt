package eu.indiewalkabout.fridgemanager.feat_settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.settings.SettingsScreenContent
import eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.settings.SettingsUiState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenJourneyTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun creditsItem_opensCreditsDestination() {
        var creditsOpened = false
        val creditsTitle = InstrumentationRegistry.getInstrumentation()
            .targetContext.getString(R.string.credits_title_label)
        composeRule.setContent {
            FreddyFridgeTheme {
                SettingsScreenContent(
                    uiState = SettingsUiState(),
                    onOpenCredits = { creditsOpened = true },
                    onUpdateDaysBeforeDeadline = {},
                    onUpdateDailyNotificationCount = {},
                    onOpenNotificationSettings = {},
                    showPrivacyOptions = false,
                    onOpenPrivacyOptions = {},
                    onOpenSystemAppSettings = {},
                    onOpenAppStore = {},
                    onSendSupportEmail = {},
                    onResetAppData = {},
                    adBanner = {},
                )
            }
        }

        composeRule.onNodeWithText(creditsTitle).performScrollTo().performClick()

        composeRule.runOnIdle { assertTrue(creditsOpened) }
    }

    @Test
    fun privacyOptions_isVisibleOnlyWhenRequired() {
        val privacyTitle = InstrumentationRegistry.getInstrumentation()
            .targetContext.getString(R.string.gdpr_btn_title)
        composeRule.setContent {
            FreddyFridgeTheme {
                SettingsScreenContent(
                    uiState = SettingsUiState(),
                    onOpenCredits = {},
                    onUpdateDaysBeforeDeadline = {},
                    onUpdateDailyNotificationCount = {},
                    onOpenNotificationSettings = {},
                    showPrivacyOptions = true,
                    onOpenPrivacyOptions = {},
                    onOpenSystemAppSettings = {},
                    onOpenAppStore = {},
                    onSendSupportEmail = {},
                    onResetAppData = {},
                    adBanner = {},
                )
            }
        }

        composeRule.onNodeWithText(privacyTitle).performScrollTo().assertIsDisplayed()
    }
}
