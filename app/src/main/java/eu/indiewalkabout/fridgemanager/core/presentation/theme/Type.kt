package eu.indiewalkabout.fridgemanager.core.presentation.theme


import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp


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







