package eu.indiewalkabout.fridgemanager.core.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.secondaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_14


// TODO : customize
@Composable
fun RoundedCornerButton(
    onClick: () -> Unit,
    shape: Shape,
    borderStroke: BorderStroke? = null,
    backgroundColor: Color = Color.White,
    elevation: Int = 5,
    text: String,
    textPadding: Int = 4,
    contentPadding: PaddingValues = PaddingValues(16.dp, 0.dp, 16.dp, 0.dp),
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = shape,
        border = borderStroke,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = elevation.dp, //5.dp,
            pressedElevation = if (elevation > 0) 2.dp else 0.dp,
            disabledElevation = 0.dp
        ),
        contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 0.dp),
        modifier = modifier
    ) {
        Text(
            text = text,
            style = style,
            modifier = Modifier.padding(textPadding.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun RoundedCornerButtonPreview2() {
    RoundedCornerButton(
        modifier = Modifier
            .padding(16.dp),
        shape = RoundedCornerShape(15.dp),
        elevation = 0,
        borderStroke = BorderStroke(2.dp, secondaryColor),
        onClick = { /*TODO*/ },
        backgroundColor = primaryColor,
        text = "Button",
        textPadding = 0,
        style = text_14(secondaryColor, true)
    )
}