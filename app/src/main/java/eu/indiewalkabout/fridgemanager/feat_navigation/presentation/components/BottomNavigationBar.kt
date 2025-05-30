package eu.indiewalkabout.fridgemanager.feat_navigation.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_12
import eu.indiewalkabout.fridgemanager.feat_navigation.domain.navigation.AppDestinationRoutes
import eu.indiewalkabout.fridgemanager.feat_navigation.domain.navigation.AppNavigation.navigate


@Composable
fun BottomNavigationBar(
    itemLabelSelected: String,
    onNewItemClicked: (() -> Unit)? = null
) {
    val colors = LocalAppColors.current
    var activeIndex = 3

    NavigationBar(containerColor = colors.primaryColor) {
        when(itemLabelSelected) {
            stringResource(R.string.menu_expired_label_item) -> {
                activeIndex = 1
            }
            stringResource(R.string.menu_consumed_label_item) -> {
                activeIndex = 2
            }
            stringResource(R.string.menu_home_item) -> {
                activeIndex = 3
            }
            stringResource(R.string.menu_expiring_label_item) -> {
                activeIndex = 4
            }
            stringResource(R.string.menu_add_label_item) -> {
                activeIndex = 5
            }
        }

        NavigationBarItem(
            selected = false,
            onClick = { navigate(AppDestinationRoutes.FoodExpiredScreen.route) },
            icon = {
                Icon(
                painter = painterResource(id = R.drawable.ic_ghost),
                contentDescription = stringResource(R.string.content_menu_expired_label_item),
                tint = if (activeIndex == 1) colors.iconColor else colors.lightGreyVeryTransparent
                )
                   },
            label = { Text(
                text = stringResource(R.string.menu_expired_label_item),
                style = text_12(
                    if (activeIndex == 1) colors.colorText else colors.lightGreyVeryTransparent
                )
            ) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navigate(AppDestinationRoutes.FoodConsumedScreen.route) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_done_all_white),
                    contentDescription = stringResource(R.string.content_menu_consumed_label_item),
                    tint = if (activeIndex == 2) colors.iconColor else colors.lightGreyVeryTransparent
                )
            },
            label = { Text(
                text = stringResource(R.string.menu_consumed_label_item),
                style = text_12(
                    if (activeIndex == 2) colors.colorText else colors.lightGreyVeryTransparent
                )
            ) }
        )


        NavigationBarItem(
            selected = false,
            onClick = { navigate(AppDestinationRoutes.MainScreen.route) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home_white_24dp),
                    contentDescription = stringResource(R.string.menu_home_item),
                    tint = if (activeIndex == 3) colors.iconColor else colors.lightGreyVeryTransparent
                )
            },
            label = { Text(
                text = stringResource(R.string.menu_home_item),
                style = text_12(
                    if (activeIndex == 3) colors.colorText else colors.lightGreyVeryTransparent
                )
            ) }
        )


        NavigationBarItem(
            selected = false,
            onClick = { navigate(AppDestinationRoutes.FoodExpiringScreen.route) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_hourglass_empty_white),
                    contentDescription = stringResource(R.string.content_menu_expiring_label_item),
                    tint = if (activeIndex == 4) colors.iconColor else colors.lightGreyVeryTransparent
                )
            },
            label = { Text(
                text = stringResource(R.string.menu_expiring_label_item),
                style = text_12(
                    if (activeIndex == 4) colors.colorText else colors.lightGreyVeryTransparent
                )
            ) }
        )

        NavigationBarItem(
            selected = false,
            onClick = {
                // navigate(AppDestinationRoutes.InsertFoodScreen.route)
                onNewItemClicked?.invoke()
                      },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_box_white),
                    contentDescription = stringResource(R.string.content_menu_add_label_item),
                    tint = if (activeIndex == 5) colors.iconColor else colors.lightGreyVeryTransparent
                )
            },
            label = { Text(
                text = stringResource(R.string.menu_add_label_item),
                style = text_12(
                    if (activeIndex == 5) colors.colorText else colors.lightGreyVeryTransparent
                )
            ) }
        )
    }
}



