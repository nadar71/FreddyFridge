package com.triberunclub.tribe.core.presentation.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.triberunclub.tribe.core.presentation.theme.nodarkmode_colors.colorAlert
import com.triberunclub.tribe.core.presentation.theme.nodarkmode_colors.colorConfirm
import com.triberunclub.tribe.core.presentation.theme.nodarkmode_colors.colorHintText
import com.triberunclub.tribe.core.presentation.theme.nodarkmode_colors.colorInfo
import com.triberunclub.tribe.core.presentation.theme.nodarkmode_colors.colorNotificationBackground
import com.triberunclub.tribe.core.presentation.theme.nodarkmode_colors.colorSearch
import com.triberunclub.tribe.core.presentation.theme.nodarkmode_colors.colorSearchTraining
import com.triberunclub.tribe.core.presentation.theme.nodarkmode_colors.colorSkillSelected
import com.triberunclub.tribe.core.presentation.theme.nodarkmode_colors.colorStrokeTextFields
import com.triberunclub.tribe.core.presentation.theme.nodarkmode_colors.colorStroke_01
import com.triberunclub.tribe.core.presentation.theme.nodarkmode_colors.colorStroke_02
import com.triberunclub.tribe.core.presentation.theme.nodarkmode_colors.colorStroke_03
import com.triberunclub.tribe.core.presentation.theme.nodarkmode_colors.colorStroke_04
import com.triberunclub.tribe.core.presentation.theme.nodarkmode_colors.colorText
import com.triberunclub.tribe.core.presentation.theme.nodarkmode_colors.colorTextLight
import com.triberunclub.tribe.core.presentation.theme.nodarkmode_colors.colorText_02
import com.triberunclub.tribe.core.presentation.theme.nodarkmode_colors.colorText_03
import com.triberunclub.tribe.core.presentation.theme.nodarkmode_colors.colorText_04
import com.triberunclub.tribe.core.presentation.theme.nodarkmode_colors.colorText_05
import com.triberunclub.tribe.core.presentation.theme.nodarkmode_colors.colorText_alpha_60
import com.triberunclub.tribe.core.presentation.theme.nodarkmode_colors.primaryColor
import com.triberunclub.tribe.core.presentation.theme.nodarkmode_colors.secondaryColor


fun setupColorScheme(): ProvidableCompositionLocal<MyColorScheme> {
    return compositionLocalOf {
            MyColorScheme(
                primaryColor,
                secondaryColor,
                colorAlert,
                colorSearch,
                colorSearchTraining,
                colorConfirm,
                colorInfo,
                colorTextLight,
                colorText,
                colorStroke_01,
                colorSkillSelected,
                colorStrokeTextFields,
                colorText_02,
                colorText_alpha_60,
                colorText_03,
                colorText_04,
                colorText_05,
                colorStroke_02,
                colorNotificationBackground,
                colorHintText,
                colorStroke_03,
                colorStroke_04,
            )
    }
}

var LocalMyColorScheme = setupColorScheme()

fun MyMaterialColorScheme(myColorScheme: MyColorScheme): ColorScheme {
    return lightColorScheme(
        primary = myColorScheme.primaryColor,
        secondary = myColorScheme.secondaryColor,
        tertiary = myColorScheme.colorSearch,
        background = myColorScheme.colorTextLight,
        surface = myColorScheme.colorTextLight,
        onPrimary = myColorScheme.colorTextLight,
        onSecondary = myColorScheme.colorTextLight,
        onTertiary = myColorScheme.colorTextLight,
        onBackground = myColorScheme.colorText,
        onSurface = myColorScheme.colorText,
        error = myColorScheme.colorAlert,
        onError = myColorScheme.colorTextLight
    )
}

fun MyMaterialDarkColorScheme(myColorScheme: MyColorScheme): ColorScheme {
    return darkColorScheme(
        primary = myColorScheme.primaryColor,
        secondary = myColorScheme.secondaryColor,
        tertiary = myColorScheme.colorSearch,
        background = myColorScheme.colorText,
        surface = myColorScheme.colorText,
        onPrimary = myColorScheme.colorText,
        onSecondary = myColorScheme.colorText,
        onTertiary = myColorScheme.colorText,
        onBackground = myColorScheme.colorTextLight,
        onSurface = myColorScheme.colorTextLight,
        error = myColorScheme.colorAlert,
        onError = myColorScheme.colorTextLight
    )
}


@Composable
fun FridgeManagerTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {

    val myColorScheme = LocalMyColorScheme.current
    val colorScheme = if (darkTheme) {
        MyMaterialDarkColorScheme(myColorScheme)
    } else {
        MyMaterialColorScheme(myColorScheme)
    }

    LocalMyColorScheme = setupColorScheme()
    CompositionLocalProvider(LocalMyColorScheme provides myColorScheme){
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}



// -------------------------------------------- TESTING --------------------------------------------

/*@Composable
fun MyComposableView01() {
    val colorScheme = LocalMyColorScheme.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.GenericTextColor)
    ) {
        Text(
            text = "View 01",
            color = MaterialTheme.colorScheme.onPrimary
        )
        Button( onClick = {
            darkThemeOn = if (darkThemeOn == 1) 0 else 1
        }) {
            Text(
                text = "Toggle Theme",
                color = SecondaryColor
            )
        }
        ColorGrid(colorScheme)
    }
}


@Composable
fun ColorGrid(colorScheme: MyColorScheme) {

    val colorList = listOf(
        colorScheme.PrimaryColor to "PrimaryColor",
        colorScheme.SecondaryColor to "SecondaryColor",
        colorScheme.ColorAlert to "ColorAlert",
        colorScheme.ColorConfirm to "ColorConfirm",
        colorScheme.ColorInfo to "ColorInfo",
        colorScheme.ColorTextLight to "ColorTextLight",
        colorScheme.ColorText to "ColorText",
        colorScheme.ColorStroke_01 to "ColorStroke_01",
        colorScheme.ColorSkillSelected to "ColorSkillSelected",
        colorScheme.ColorStrokeTextFields to "ColorStrokeTextFields",
        colorScheme.ColorText_02 to "ColorText_02",
        colorScheme.ColorText_alpha_60 to "ColorText_alpha_60",
        colorScheme.ColorText_03 to "ColorText_03",
        colorScheme.ColorText_04 to "ColorText_04",
        colorScheme.ColorText_05 to "ColorText_05",
        colorScheme.ColorStroke_02 to "ColorStroke_02",
        colorScheme.ColorNotificationBackground to "ColorNotificationBackground",
        colorScheme.ColorHintText to "ColorHintText",



        colorScheme.ColorStroke_03 to "ColorStroke_03",
        colorScheme.Yellow01 to "Yellow01",
        colorScheme.Yellow02 to "Yellow02",
        colorScheme.Grey to "Grey",
        colorScheme.Green00 to "Green00",
        colorScheme.Green01 to "Green01",
        colorScheme.Green02 to "Green02",
        colorScheme.Blue00 to "Blue00",
        colorScheme.Black to "Black",
        colorScheme.ColorRedLeftEditText to "ColorRedLeftEditText",
        colorScheme.TodayListRecordDarkGrey to "TodayListRecordDarkGrey",
        colorScheme.TodayListRecordLightGrey to "TodayListRecordLightGrey",
        colorScheme.GoogleRed to "GoogleRed",
        colorScheme.AppleSigninGrey to "AppleSigninGrey",
        colorScheme.Fascia1 to "Fascia1",
        colorScheme.Fascia2 to "Fascia2",
        colorScheme.Fascia3 to "Fascia3",
        colorScheme.Fascia1Blue to "Fascia1Blue",
        colorScheme.Fascia2Blue to "Fascia2Blue",
        colorScheme.Fascia3Blue to "Fascia3Blue",
        colorScheme.StartBtnColor to "StartBtnColor",
        colorScheme.BackgroundMain to "BackgroundMain",
        colorScheme.HeaderCustomerCode to "HeaderCustomerCode",
        colorScheme.FormField to "FormField",
        colorScheme.HintColor to "HintColor",
        colorScheme.ButtonWhiteBackground to "ButtonWhiteBackground",
        colorScheme.ButtonTextBlue to "ButtonTextBlue",
        colorScheme.ItemSaleDocumentBackground to "ItemSaleDocumentBackground",
        colorScheme.ItemText to "ItemText",
        colorScheme.GenericTextColor to "SupplyTextColor",
        colorScheme.BarchartBackground to "BarchartBackground",
        colorScheme.ButtonRedBackground to "ButtonRedBackground",
        colorScheme.ButtonTextWhite to "ButtonTextWhite",
        colorScheme.ProfileGrey to "ProfileGrey",
        colorScheme.FilterButtonOutline to "FilterButtonOutline",
        colorScheme.FilterSelectedButtonText to "FilterSelectedButtonText",
        colorScheme.FilterUnselectedButtonText to "FilterUnselectedButtonText",
        colorScheme.FilterSelectedButtonFill to "FilterSelectedButtonFill",
        colorScheme.FilterUnselectedButtonFill to "FilterUnselectedButtonFill",
        colorScheme.FilterTextColor to "FilterTextColor",
        colorScheme.YearHeaderColor to "YearHeaderColor",
        colorScheme.CardTextColor to "CardTextColor"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(colorList.size) { index ->
            val (color, name) = colorList[index]
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (color.luminance() > 0.5) ColorText else SecondaryColor
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun Preview_MyComposableView01() {
    TribeRunTheme {
        MyComposableView01()
    }
}*/



