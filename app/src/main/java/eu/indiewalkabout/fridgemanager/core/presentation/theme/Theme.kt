package com.triberunclub.tribe.core.presentation.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.backgroundLightGreen
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.backgroundLightGreenSemitransparent
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.brown
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorAccent
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.fabLightGreen
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.fabYellow
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.foodGreen
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.foodOrange
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.foodRed
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.foodYellow
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.lightGreyVeryTransparent
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.lightWhiteSemitransparent
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.lightWhiteTransparent
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColorDark
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColorSemitransparent
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.todayListRecordLightBlue
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.todayListRecordLightGrey


val LocalAppColors = staticCompositionLocalOf { AppColors }

@Composable
fun AppCustomTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalAppColors provides AppColors) {
        content()
    }
}


// USAGE PREVIEW : fix compose libs &c. before using this
/*@Composable
fun HomeScreen() {
    val colors = LocalAppColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.backgroundLightGreen)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Welcome!",
                color = colors.primaryColor,
                fontSize = 22.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = colors.primaryColorSemitransparent),
                onClick = { *//* do something *//* }
            ) {
                Text("Click Me", color = colors.onButton)
            }
        }
    }
}


@Composable
fun MyApp() {
    AppCustomTheme {
        HomeScreen()
    }
}*/




// ---------- OLD


fun setupColorScheme(): ProvidableCompositionLocal<MyColorScheme> {
    return compositionLocalOf {
            MyColorScheme(
                primaryColor = primaryColor,
                primaryColorDark,
                colorAccent,
                brown,
                todayListRecordLightGrey,
                fabYellow,
                todayListRecordLightBlue,
                backgroundLightGreen,
                backgroundLightGreenSemitransparent,
                fabLightGreen,
                lightWhiteSemitransparent,
                lightWhiteTransparent,
                primaryColorSemitransparent,
                lightGreyVeryTransparent,
                foodGreen,
                foodYellow,
                foodOrange,
                foodRed
            )
    }
}

var LocalMyColorScheme = setupColorScheme()



@Composable
fun FridgeManagerTheme(
    darkTheme: Boolean = false, // no dark for this app
    content: @Composable () -> Unit
) {

    // use my
    val myColorScheme = LocalMyColorScheme.current
    val colorScheme = if (darkTheme) {
        MyMaterialDarkColorScheme(myColorScheme)
    } else {
        MyMaterialLightColorScheme(myColorScheme)
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



// Not used: no dark mode, used a different color logic
/*fun MyMaterialLightColorScheme(myColorScheme: MyColorScheme): ColorScheme {
    return lightColorScheme(
        primary = myColorScheme.primaryColor,
        secondary = myColorScheme.primaryColorDark,
        tertiary = myColorScheme.colorAccent,
        background = myColorScheme.backgroundLightGreen,
        surface = myColorScheme.backgroundLightGreenSemitransparent,
        onPrimary = myColorScheme.primaryColorSemitransparent,
        onSecondary = myColorScheme.lightGreyVeryTransparent,
        onTertiary = myColorScheme.lightWhiteSemitransparent,
        onBackground = myColorScheme.lightWhiteTransparent,
        onSurface = myColorScheme.backgroundLightGreenSemitransparent,
        error = myColorScheme.colorAccent,
        onError = myColorScheme.foodRed
    )
}

fun MyMaterialDarkColorScheme(myColorScheme: MyColorScheme): ColorScheme {
    return darkColorScheme(
        primary = myColorScheme.primaryColor,
        secondary = myColorScheme.primaryColorDark,
        tertiary = myColorScheme.colorAccent,
        background = myColorScheme.backgroundLightGreen,
        surface = myColorScheme.backgroundLightGreenSemitransparent,
        onPrimary = myColorScheme.primaryColorSemitransparent,
        onSecondary = myColorScheme.lightGreyVeryTransparent,
        onTertiary = myColorScheme.lightWhiteSemitransparent,
        onBackground = myColorScheme.lightWhiteTransparent,
        onSurface = myColorScheme.backgroundLightGreenSemitransparent,
        error = myColorScheme.colorAccent,
        onError = myColorScheme.foodRed
    )
}*/


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



