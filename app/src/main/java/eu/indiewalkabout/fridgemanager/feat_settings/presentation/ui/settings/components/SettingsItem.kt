package eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.settings.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.secondaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.Fredoka
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_14
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_16

@Composable
fun SettingsItem(
    title: String,
    titleStyle: TextStyle = text_16(LocalAppColors.current.colorText),
    subtitle: String,
    subTitleStyle: TextStyle = text_14(LocalAppColors.current.colorText.copy(alpha = 0.8f)),
    @DrawableRes rightIcon: Int = R.drawable.ic_arrow_right,
    iconDescription: String = stringResource(R.string.settings_right_arrow),
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .weight(1f)
            ) {
                Text(
                    text = title,
                    fontFamily = Fredoka,
                    style = titleStyle,
                    maxLines = 3,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    fontFamily = Fredoka,
                    style = subTitleStyle,
                )

            }
            Icon(
                painter = painterResource(id = rightIcon),
                contentDescription = iconDescription,
                tint = secondaryColor,
                modifier = Modifier
                    .size(24.dp)
            )
        }
    }
}


@Preview
@Composable
fun PreviewSettingsItem() {
    SettingsItem(
        title = "Title",
        subtitle = "Subtitle"
    )
}

@Preview
@Composable
fun PreviewSettingsItem_01() {
    SettingsItem(
        title = "Title adasdf asdfasdffff asdfa asfd asdf  asdf asdfasd fasdf a sdf",
        subtitle = "Subtitle"
    )
}