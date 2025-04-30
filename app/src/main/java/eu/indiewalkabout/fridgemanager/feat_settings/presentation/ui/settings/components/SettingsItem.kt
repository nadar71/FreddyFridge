package eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.core.presentation.theme.Fredoka
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_14
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_16

@Composable
fun SettingsItem(
    title: String,
    titleStyle: TextStyle = text_16(LocalAppColors.current.colorText),
    subtitle: String,
    subTitleStyle: TextStyle = text_14(LocalAppColors.current.colorText.copy(alpha = 0.8f))
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = title,
            fontFamily = Fredoka,
            style = titleStyle,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = subtitle,
            fontFamily = Fredoka,
            style = subTitleStyle,
        )
    }
}