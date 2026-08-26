package eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.credits

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.data.locals.Constants.my_website
import eu.indiewalkabout.fridgemanager.core.presentation.components.TopBar
import eu.indiewalkabout.fridgemanager.core.presentation.navigation.components.SecondaryScreenScaffold
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_16
import eu.indiewalkabout.fridgemanager.core.util.GenericUtility.openUrlInBrowserNotCompose
import eu.indiewalkabout.fridgemanager.feat_settings.presentation.components.SettingsGroupTitle
import eu.indiewalkabout.fridgemanager.feat_settings.presentation.components.SettingsItem

@Composable
fun CreditsScreen(
    onBack: () -> Unit = {},
) {
    val colors = LocalAppColors.current
    val context = LocalContext.current
    val gilInstagramLink = stringResource(R.string.credits_gil_instagram_link)
    val alarmIconsLink = stringResource(R.string.credits_alarm_icons_link)
    val fridgeImageLink = stringResource(R.string.credits_fridge_img_link)
    val fruitsImageLink = stringResource(R.string.credits_fruits_img_link)
    val backgroundLink = stringResource(R.string.credits_background_link)

    SecondaryScreenScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            TopBar(
                title = stringResource(id = R.string.credits_title_label),
                titleColor = colors.brown,
                drawableLeftIcon = R.drawable.ic_arrow_back,
                onLeftIconClick = onBack,
                backgroundColor = colors.primaryColor
            )

            Spacer(modifier = Modifier.height(32.dp))

            SettingsGroupTitle(
                title = stringResource(id = R.string.credits_section_subtitle_summary),
                style = text_16(colors.brown)
            )

            Row {
                SettingsItem(
                    title = stringResource(id = R.string.credits_idea_label),
                    subtitle = stringResource(id = R.string.credits_idea_Gil),
                    rightIcon = R.drawable.ic_instagram,
                    iconDescription = stringResource(id = R.string.credits_devsubtitle_icon),
                    modifier = Modifier.clickable {
                        openUrlInBrowserNotCompose(
                            context,
                            gilInstagramLink
                        )
                    }
                )
            }

            SettingsItem(
                title = stringResource(id = R.string.credits_dev_design_label),
                subtitle = stringResource(id = R.string.credits_dev_design_SM),
                rightIcon = R.drawable.ic_globe,
                iconDescription = stringResource(id = R.string.credits_devsubtitle_icon),
                modifier = Modifier.clickable {
                    openUrlInBrowserNotCompose(context, my_website)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            SettingsGroupTitle(
                title = stringResource(id = R.string.credits_attributions_title),
                style = text_16(colors.brown)
            )

            SettingsItem(
                title = stringResource(id = R.string.credits_alarm_icons_label),
                subtitle = "",
                modifier = Modifier.clickable {
                    openUrlInBrowserNotCompose(
                        context,
                        alarmIconsLink
                    )
                }
            )

            SettingsItem(
                title = stringResource(id = R.string.credits_fridge_img_label),
                subtitle = "",
                modifier = Modifier.clickable {
                    openUrlInBrowserNotCompose(
                        context,
                        fridgeImageLink
                    )
                }
            )

            SettingsItem(
                title = stringResource(id = R.string.credits_fruits_img_label),
                subtitle = "",
                modifier = Modifier.clickable {
                    openUrlInBrowserNotCompose(
                        context,
                        fruitsImageLink
                    )
                }
            )

            SettingsItem(
                title = stringResource(id = R.string.credits_background_label),
                subtitle = "",
                modifier = Modifier.clickable {
                    openUrlInBrowserNotCompose(
                        context,
                        backgroundLink
                    )
                }
            )

        }
    }
}

@Preview
@Composable
fun CreditsScreenPreview() {
    FreddyFridgeTheme {
        CreditsScreen()
    }
}


