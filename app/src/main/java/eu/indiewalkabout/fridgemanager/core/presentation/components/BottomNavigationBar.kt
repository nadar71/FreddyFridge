package eu.indiewalkabout.fridgemanager.core.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
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
                contentDescription = "Scaduto")
                   },
            label = { Text("Scaduto") }
        )
        NavigationBarItem(
            selected = false,
            onClick = {  },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_done_all_white),
                    contentDescription = "Usato")
            },
            label = { Text("Usato") }
        )
        NavigationBarItem(
            selected = false,
            onClick = {  },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_hourglass_empty_white),
                    contentDescription = "Scade")
            },
            label = { Text("Scade") }
        )
        NavigationBarItem(
            selected = false,
            onClick = {  },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_box_white),
                    contentDescription = "Nuovo")
            },
            label = { Text("Nuovo") }
        )
    }
}
