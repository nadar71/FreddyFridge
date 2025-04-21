package eu.indiewalkabout.fridgemanager.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors

@Composable
fun TopBar() {
    val colors = LocalAppColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.HelpOutline,
            contentDescription = "Help",
            modifier = Modifier
                .size(28.dp)
                .clickable { /* Handle help */ },
            tint = colors.iconColor
        )
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            modifier = Modifier
                .size(28.dp)
                .clickable { /* Handle settings */ },
            tint = colors.iconColor
        )
    }
}


@Composable
@Preview
private fun TopBarPreview() {
    TopBar()
}
