package com.triberunclub.tribe.core.presentation.theme


import com.triberunclub.tribe.core.presentation.theme.TribeAreaDimen.fontSize_normal_10_txt
import com.triberunclub.tribe.core.presentation.theme.TribeAreaDimen.fontSize_normal_12_txt
import com.triberunclub.tribe.core.presentation.theme.TribeAreaDimen.fontSize_normal_14_txt
import com.triberunclub.tribe.core.presentation.theme.TribeAreaDimen.fontSize_normal_15_txt
import com.triberunclub.tribe.core.presentation.theme.TribeAreaDimen.fontSize_normal_16_txt
import com.triberunclub.tribe.core.presentation.theme.TribeAreaDimen.fontSize_normal_18_txt
import com.triberunclub.tribe.core.presentation.theme.TribeAreaDimen.fontSize_normal_20_txt
import com.triberunclub.tribe.core.presentation.theme.TribeAreaDimen.fontSize_normal_22_txt
import com.triberunclub.tribe.core.presentation.theme.TribeAreaDimen.fontSize_normal_24_txt
import com.triberunclub.tribe.core.presentation.theme.TribeAreaDimen.fontSize_small_08_txt
import com.triberunclub.tribe.core.presentation.theme.TribeAreaDimen.fontSize_title_08_txt
import com.triberunclub.tribe.core.presentation.theme.TribeAreaDimen.fontSize_title_20_txt
import com.triberunclub.tribe.core.presentation.theme.TribeAreaDimen.fontSize_title_22_txt
import com.triberunclub.tribe.core.presentation.theme.TribeAreaDimen.fontSize_title_24_txt
import com.triberunclub.tribe.core.presentation.theme.TribeAreaDimen.fontSize_title_28_txt
import com.triberunclub.tribe.core.presentation.theme.TribeAreaDimen.fontSize_title_30_txt
import com.triberunclub.tribe.core.presentation.theme.TribeAreaDimen.fontSize_title_32_txt
import com.triberunclub.tribe.core.presentation.theme.TribeAreaDimen.fontSize_title_36_txt
import com.triberunclub.tribe.core.presentation.theme.TribeAreaDimen.fontSize_title_38_txt
import com.triberunclub.tribe.core.presentation.theme.TribeAreaDimen.fontSize_title_40_txt
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.triberunclub.tribe.core.presentation.theme.nodarkmode_colors.secondaryColor


val fontSize_title_16_txt: TextUnit
    get() {
        TODO()
    }




// Set of Material typography styles to start with
val Typography = androidx.compose.material3.Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,  //W500 is roughly equivalent to Medium
        fontSize = 14.sp
    ),
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)








@Composable
fun text_26(color: Color, isBold: Boolean = false): TextStyle = TextStyle(
    color = color,
    fontFamily = FontFamily.Default,
    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
    fontSize = 26.sp
)

@Composable
fun text_24(color: Color, isBold: Boolean = false): TextStyle = TextStyle(
    color = color,
    fontFamily = FontFamily.Default,
    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
    fontSize = 24.sp
)

@Composable
fun text_20(color: Color, isBold: Boolean = false): TextStyle = TextStyle(
    color = color,
    fontFamily = FontFamily.Default,
    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
    fontSize = 20.sp
)

@Composable
fun text_18(color: Color, isBold: Boolean = false): TextStyle = TextStyle(
    color = color,
    fontFamily = FontFamily.Default,
    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
    fontSize = 18.sp
)

@Composable
fun text_17(color: Color, isBold: Boolean = false): TextStyle = TextStyle(
    color = color,
    fontFamily = FontFamily.Default,
    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
    fontSize = 17.sp
)

@Composable
fun text_16(color: Color, isBold: Boolean = false): TextStyle = TextStyle(
    color = color,
    fontFamily = FontFamily.Default,
    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
    fontSize = 16.sp
)

@Composable
fun text_15(color: Color, isBold: Boolean = false): TextStyle = TextStyle(
    color = color,
    fontFamily = FontFamily.Default,
    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
    fontSize = 15.sp
)

@Composable
fun text_14(color: Color, isBold: Boolean = false): TextStyle = TextStyle(
    color = color,
    fontFamily = FontFamily.Default,
    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
    fontSize = 14.sp
)

@Composable
fun text_13(color: Color, isBold: Boolean = false): TextStyle = TextStyle(
    color = color,
    fontFamily = FontFamily.Default,
    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
    fontSize = 13.sp
)

@Composable
fun text_12(color: Color, isBold: Boolean = false): TextStyle = TextStyle(
    color = color,
    fontFamily = FontFamily.Default,
    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
    fontSize = 12.sp
)

@Composable
fun text_8(color: Color, isBold: Boolean = false): TextStyle = TextStyle(
    color = color,
    fontFamily = FontFamily.Default,
    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
    fontSize = 8.sp
)



// TODO: eliminate -------------------
@Composable
fun title_40(): TextStyle = TextStyle(
    color = LocalMyColorScheme.current.GenericTextColor,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = fontSize_title_40_txt,
    textAlign = TextAlign.Center
)

@Composable
fun title_38(): TextStyle = TextStyle(
    color = LocalMyColorScheme.current.GenericTextColor,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = fontSize_title_38_txt,
    textAlign = TextAlign.Center
)

@Composable
fun title_36(): TextStyle = TextStyle(
    color = LocalMyColorScheme.current.GenericTextColor,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = fontSize_title_36_txt,
    textAlign = TextAlign.Center
)

@Composable
fun title_32(): TextStyle = TextStyle(
    color = LocalMyColorScheme.current.GenericTextColor,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = fontSize_title_32_txt,
    textAlign = TextAlign.Center
)

@Composable
fun title_30(): TextStyle = TextStyle(
    color = LocalMyColorScheme.current.GenericTextColor,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = fontSize_title_30_txt,
    textAlign = TextAlign.Center
)

@Composable
fun title_28(): TextStyle = TextStyle(
    color = LocalMyColorScheme.current.GenericTextColor,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = fontSize_title_28_txt,
    textAlign = TextAlign.Center
)

@Composable
fun title_24(): TextStyle = TextStyle(
    color = LocalMyColorScheme.current.GenericTextColor,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = fontSize_title_24_txt,
    textAlign = TextAlign.Center
)

@Composable
fun title_22(): TextStyle = TextStyle(
    color = LocalMyColorScheme.current.GenericTextColor,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = fontSize_title_22_txt,
    textAlign = TextAlign.Center
)

@Composable
fun title_20(): TextStyle = TextStyle(
    color = LocalMyColorScheme.current.GenericTextColor,
    // color = ColorText,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = fontSize_title_20_txt,
    textAlign = TextAlign.Center
)

@Composable
fun title_16(): TextStyle = TextStyle(
    color = LocalMyColorScheme.current.GenericTextColor,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = fontSize_title_16_txt,
    textAlign = TextAlign.Center
)

@Composable
fun title_08(): TextStyle = TextStyle(
    color = LocalMyColorScheme.current.GenericTextColor,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = fontSize_title_08_txt,
    textAlign = TextAlign.Center
)

@Composable
fun normalText_24(): TextStyle = TextStyle(
    color = LocalMyColorScheme.current.GenericTextColor,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = fontSize_normal_24_txt
)

@Composable
fun normalText_22(): TextStyle = TextStyle(
    color = LocalMyColorScheme.current.GenericTextColor,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = fontSize_normal_22_txt
)

@Composable
fun normalText_20(): TextStyle = TextStyle(
    color = LocalMyColorScheme.current.GenericTextColor,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = fontSize_normal_20_txt
)

@Composable
fun normalText_18(): TextStyle = TextStyle(
    color = LocalMyColorScheme.current.GenericTextColor,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = fontSize_normal_18_txt
)

@Composable
fun normalText_16(): TextStyle =TextStyle(
    color = LocalMyColorScheme.current.GenericTextColor,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = fontSize_normal_16_txt
)

@Composable
fun normalText_15(): TextStyle = TextStyle(
    color = LocalMyColorScheme.current.GenericTextColor,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = fontSize_normal_15_txt
)

@Composable
fun normalText_14(): TextStyle = TextStyle(
    color = LocalMyColorScheme.current.GenericTextColor,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = fontSize_normal_14_txt
)

@Composable
fun normalText_12(): TextStyle = TextStyle(
    color = LocalMyColorScheme.current.GenericTextColor,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = fontSize_normal_12_txt
)

@Composable
fun normalText_10(): TextStyle = TextStyle(
    color = LocalMyColorScheme.current.GenericTextColor,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = fontSize_normal_10_txt
)

@Composable
fun normalText_08(): TextStyle = TextStyle(
    color = LocalMyColorScheme.current.GenericTextColor,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = fontSize_small_08_txt
)

@Composable
fun hint_01(): TextStyle = TextStyle(
    color = LocalMyColorScheme.current.GenericTextColor,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = fontSize_normal_16_txt
)

@Composable
fun placeholder_01(): TextStyle = TextStyle(
    color = LocalMyColorScheme.current.GenericTextColor,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = fontSize_normal_16_txt
)

@Composable
fun normalText_14_white(): TextStyle = TextStyle(
    color = secondaryColor,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = fontSize_normal_14_txt
)

@Composable
fun normalText_16_white(): TextStyle = TextStyle(
    color = secondaryColor,
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = fontSize_normal_16_txt
)



