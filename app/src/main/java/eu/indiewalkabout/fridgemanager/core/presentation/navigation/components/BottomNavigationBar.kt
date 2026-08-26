package eu.indiewalkabout.fridgemanager.core.presentation.navigation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.navigation.AppDestination
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_12


@Composable
fun BottomNavigationBar(
    selectedDestination: AppDestination?,
    onDestinationSelected: (AppDestination) -> Unit,
    onNewItemClicked: (() -> Unit)? = null
) {
    val colors = LocalAppColors.current

    NavigationBar(containerColor = colors.primaryColor) {
        NavigationBarItem(
            selected = selectedDestination == AppDestination.Expired,
            onClick = { onDestinationSelected(AppDestination.Expired) },
            icon = {
                Icon(
                painter = painterResource(id = R.drawable.ic_ghost),
                contentDescription = stringResource(R.string.content_menu_expired_label_item),
                tint = if (selectedDestination == AppDestination.Expired) colors.iconColor else colors.lightGreyVeryTransparent
                )
                   },
            label = { Text(
                text = stringResource(R.string.menu_expired_label_item),
                style = text_12(
                    if (selectedDestination == AppDestination.Expired) colors.colorText else colors.lightGreyVeryTransparent
                )
            ) }
        )
        NavigationBarItem(
            selected = selectedDestination == AppDestination.Consumed,
            onClick = { onDestinationSelected(AppDestination.Consumed) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_done_all_white),
                    contentDescription = stringResource(R.string.content_menu_consumed_label_item),
                    tint = if (selectedDestination == AppDestination.Consumed) colors.iconColor else colors.lightGreyVeryTransparent
                )
            },
            label = { Text(
                text = stringResource(R.string.menu_consumed_label_item),
                style = text_12(
                    if (selectedDestination == AppDestination.Consumed) colors.colorText else colors.lightGreyVeryTransparent
                )
            ) }
        )


        NavigationBarItem(
            selected = selectedDestination == AppDestination.Main,
            onClick = { onDestinationSelected(AppDestination.Main) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = stringResource(R.string.menu_home_item),
                    tint = if (selectedDestination == AppDestination.Main) colors.iconColor else colors.lightGreyVeryTransparent
                )
            },
            label = { Text(
                text = stringResource(R.string.menu_home_item),
                style = text_12(
                    if (selectedDestination == AppDestination.Main) colors.colorText else colors.lightGreyVeryTransparent
                )
            ) }
        )


        NavigationBarItem(
            selected = selectedDestination == AppDestination.Expiring,
            onClick = { onDestinationSelected(AppDestination.Expiring) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_hourglass),
                    contentDescription = stringResource(R.string.content_menu_expiring_label_item),
                    tint = if (selectedDestination == AppDestination.Expiring) colors.iconColor else colors.lightGreyVeryTransparent
                )
            },
            label = { Text(
                text = stringResource(R.string.menu_expiring_label_item),
                style = text_12(
                    if (selectedDestination == AppDestination.Expiring) colors.colorText else colors.lightGreyVeryTransparent
                )
            ) }
        )

        NavigationBarItem(
            selected = false,
            onClick = {
                onNewItemClicked?.invoke()
                      },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_box),
                    contentDescription = stringResource(R.string.content_menu_add_label_item),
                    tint = colors.lightGreyVeryTransparent
                )
            },
            label = { Text(
                text = stringResource(R.string.menu_add_label_item),
                style = text_12(
                    colors.lightGreyVeryTransparent
                )
            ) }
        )
    }
}

