package eu.indiewalkabout.fridgemanager.core.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.theme.Fredoka
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_18

@Composable
fun TopBar(
    title: String = "",
    titleColor: Color = LocalAppColors.current.colorText,
    titleStyle: TextStyle = text_18(titleColor, true),
    @DrawableRes drawableLeftIcon: Int? = null,
    @DrawableRes drawableRightIcon: Int? = null,
    leftIconColor: Color = LocalAppColors.current.iconColor,
    rightIconColor: Color = LocalAppColors.current.iconColor,
    onLeftIconClick: () -> Unit = {},
    onRightIconClick: () -> Unit = {},
    backgroundColor: Color = Color.Transparent,
    paddingStart: Dp = 0.dp,
    paddingEnd: Dp = 0.dp,
    paddingTop: Dp = 0.dp,
    paddingBottom: Dp = 0.dp,
    ) {


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(
                start = paddingStart,
                end = paddingEnd,
                top = paddingTop,
                bottom = paddingBottom
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (drawableLeftIcon != null) {
            Icon(
                painter = painterResource(id = drawableLeftIcon),
                contentDescription = stringResource(R.string.help_icon),
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onLeftIconClick() },
                tint = leftIconColor
            )
        }

        Text(
            text = title,
            fontFamily = Fredoka,
            style = titleStyle,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )

        if (drawableRightIcon != null) {
            Icon(
                painter = painterResource(id = drawableRightIcon),
                contentDescription = stringResource(R.string.settings_icon),
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onRightIconClick() },
                tint = rightIconColor
            )
        }
    }
}


@Composable
@Preview
private fun TopBarPreview() {
    TopBar(title = "Titolo", titleColor = LocalAppColors.current.brown)
}

@Composable
@Preview
private fun TopBarPreviewNoLeft() {
    TopBar(drawableLeftIcon = R.drawable.ic_help_outline, title = "Titolo", titleColor = LocalAppColors.current.brown)
}

@Composable
@Preview
private fun TopBarPreviewNoRight() {
    TopBar(drawableRightIcon = R.drawable.ic_flower_white, title = "Titolo", titleColor = LocalAppColors.current.brown)
}

@Composable
@Preview
private fun TopBarPreviewNoIcons() {
    TopBar(drawableLeftIcon = R.drawable.ic_help_outline, drawableRightIcon = R.drawable.ic_flower_white, title = "Titolo", titleColor = LocalAppColors.current.colorText)
}
