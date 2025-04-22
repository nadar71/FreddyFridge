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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.theme.Fredoka
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_18

@Composable
fun TopBar(
    showLeftIcon: Boolean = true,
    showRightIcon: Boolean = true,
    title: String = ""
) {
    val colors = LocalAppColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.HelpOutline,
            contentDescription = stringResource(R.string.help_icon),
            modifier = Modifier
                .size(28.dp)
                .clickable { /* Handle help */ },
            tint = colors.iconColor
        )

        Text(
            text = title,
            fontFamily = Fredoka,
            style = text_18(colors.colorText, true),
        )

        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = stringResource(R.string.settings_icon),
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
