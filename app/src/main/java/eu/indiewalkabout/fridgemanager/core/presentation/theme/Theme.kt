package eu.indiewalkabout.fridgemanager.core.presentation.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// customized color palette no material no darkmode allowed for now
val LocalAppColors = staticCompositionLocalOf { AppColors }

@Composable
fun FreddyFridgeTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalAppColors provides AppColors) {
        content()
    }
}


// USAGE PREVIEW : fix compose libs &c. before using this
@Composable
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
                onClick = {  /*do something*/  }
            ) {
                Text("Click Me", color = colors.colorText)
            }

            SubScreen()
        }
    }
}

@Composable
fun SubScreen() {
    val colors = LocalAppColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.primaryColorDark)
            .padding(16.dp)
    ) {
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = colors.brown),
                onClick = {  /*do something*/  }
            ) {
                Text("Click Me now", color = colors.colorText)
            }

    }
}


@Preview(showBackground = true)
@Composable
fun MyApp() {
    FreddyFridgeTheme {
        HomeScreen()
    }
}




// ---------- OLD

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
}


// var localMyColorScheme = setupColorScheme()
val LocalMyColorScheme: ProvidableCompositionLocal<MyColorScheme> = compositionLocalOf {
    error("No MyColorScheme provided")
}


@Composable
fun FridgeManagerTheme(
    darkTheme: Boolean = false, // no dark for this app
    content: @Composable () -> Unit
) {

    val myColorScheme = MyColorScheme(
        primaryColor = primaryColor,
        primaryColorDark = primaryColorDark,
        colorAccent = colorAccent,
        colorText = colorText,
        brown = brown,
        todayListRecordLightGrey = todayListRecordLightGrey,
        fabYellow = fabYellow,
        todayListRecordLightBlue = todayListRecordLightBlue,
        backgroundLightGreen = backgroundLightGreen,
        backgroundLightGreenSemitransparent = backgroundLightGreenSemitransparent,
        fabLightGreen = fabLightGreen,
        lightWhiteSemitransparent = lightWhiteSemitransparent,
        lightWhiteTransparent = lightWhiteTransparent,
        primaryColorSemitransparent = primaryColorSemitransparent,
        lightGreyVeryTransparent = lightGreyVeryTransparent,
        foodGreen = foodGreen,
        foodYellow = foodYellow,
        foodOrange = foodOrange,
        foodRed = foodRed
    )

    val colorScheme = if (darkTheme) {
        MyMaterialDarkColorScheme(myColorScheme)
    } else {
        MyMaterialLightColorScheme(myColorScheme)
    }

    CompositionLocalProvider(LocalMyColorScheme provides myColorScheme){
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}*/












