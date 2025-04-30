package eu.indiewalkabout.fridgemanager.core.presentation.components

import android.R.attr.text
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors

@Composable
fun BottomNavigationBar() {
    val colors = LocalAppColors.current

    NavigationBar(containerColor = colors.primaryColor) {
        NavigationBarItem(
            selected = false,
            onClick = {  },
            icon = {
                Icon(
                painter = painterResource(id = R.drawable.ic_ghost),
                contentDescription = stringResource(R.string.content_menu_expired_label_item))
                   },
            label = { Text(
                text = stringResource(R.string.menu_expired_label_item),
            ) }
        )
        NavigationBarItem(
            selected = false,
            onClick = {  },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_done_all_white),
                    contentDescription = stringResource(R.string.content_menu_consumed_label_item))
            },
            label = { Text(

                text = stringResource(R.string.menu_consumed_label_item)
            ) }
        )
        NavigationBarItem(
            selected = false,
            onClick = {  },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_hourglass_empty_white),
                    contentDescription = stringResource(R.string.content_menu_expiring_label_item))
            },
            label = { Text(
                text = stringResource(R.string.menu_expiring_label_item)
            ) }
        )

        NavigationBarItem(
            selected = false,
            onClick = {  },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_box_white),
                    contentDescription = stringResource(R.string.content_menu_add_label_item))
            },
            label = { Text(
                text = stringResource(R.string.menu_add_label_item)
            ) }
        )
    }
}
