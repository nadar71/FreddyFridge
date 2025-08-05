package eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.credits

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.data.locals.Constants.my_website
import eu.indiewalkabout.fridgemanager.core.presentation.components.BackgroundPattern
import eu.indiewalkabout.fridgemanager.core.presentation.components.TopBar
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.secondaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.presentation.theme.Fredoka
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_14
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_16
import eu.indiewalkabout.fridgemanager.core.util.GenericUtility.openUrlInBrowserNotCompose
import eu.indiewalkabout.fridgemanager.feat_navigation.domain.navigation.AppDestinationRoutes
import eu.indiewalkabout.fridgemanager.feat_navigation.domain.navigation.AppNavigation.navigate
import eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.settings.components.SettingsGroupTitle
import eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.settings.components.SettingsItem

@Composable
fun CreditsScreen() {

    val TAG = "CreditsScreen"
    val colors = LocalAppColors.current
    val context = LocalContext.current

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
                    title = stringResource(id = R.string.credits_title_label),
                    titleColor = colors.brown,
                    drawableLeftIcon = R.drawable.ic_arrow_back,
                    onLeftIconClick = {
                        navigate(AppDestinationRoutes.SettingsScreen.route)
                    },
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
                                context.getString(R.string.credits_gil_instagram_link)
                            )
                        }
                    )
                }

                /*Text(
                    text = stringResource(id = R.string.credits_gil_instagram_link),
                    fontFamily = Fredoka,
                    style = text_14(secondaryColor, false)
                        .copy(textDecoration = TextDecoration.Underline),
                    modifier = Modifier.padding(horizontal = 26.dp)
                )*/

                SettingsItem(
                    title = stringResource(id = R.string.credits_dev_design_label),
                    subtitle = stringResource(id = R.string.credits_dev_design_SM),
                    rightIcon = R.drawable.ic_globe,
                    iconDescription = stringResource(id = R.string.credits_devsubtitle_icon),
                    modifier = Modifier.clickable {
                        openUrlInBrowserNotCompose(context, my_website)
                    }
                )

                /*Text(
                    text = my_website,
                    fontFamily = Fredoka,
                    style = text_14(secondaryColor, false)
                        .copy(textDecoration = TextDecoration.Underline),
                    modifier = Modifier.padding(horizontal = 26.dp)
                )*/

                Spacer(modifier = Modifier.height(32.dp))

                SettingsGroupTitle(
                    title = stringResource(id = R.string.credits_attributions_title),
                    style = text_16(colors.brown)
                )

                SettingsItem(
                    title = stringResource(id = R.string.credits_alarm_icons_label),
                    subtitle = "", // stringResource(id = R.string.credits_alarm_icons_link),
                    modifier = Modifier.clickable {
                        openUrlInBrowserNotCompose(
                            context,
                            context.getString(R.string.credits_alarm_icons_link)
                        )
                    }
                )

                SettingsItem(
                    title = stringResource(id = R.string.credits_fridge_img_label),
                    subtitle = "", // stringResource(id = R.string.credits_fridge_img_link),
                    modifier = Modifier.clickable {
                        openUrlInBrowserNotCompose(
                            context,
                            context.getString(R.string.credits_fridge_img_link)
                        )
                    }
                )

                SettingsItem(
                    title = stringResource(id = R.string.credits_fruits_img_label),
                    subtitle = "", // stringResource(id = R.string.credits_fruits_img_link),
                    modifier = Modifier.clickable {
                        openUrlInBrowserNotCompose(
                            context,
                            context.getString(R.string.credits_fruits_img_link)
                        )
                    }
                )

                SettingsItem(
                    title = stringResource(id = R.string.credits_background_label),
                    subtitle = "", // stringResource(id = R.string.credits_background_link),
                    modifier = Modifier.clickable {
                        openUrlInBrowserNotCompose(
                            context,
                            context.getString(R.string.credits_background_link)
                        )
                    }
                )


            }
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






