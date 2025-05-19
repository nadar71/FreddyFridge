package eu.indiewalkabout.fridgemanager.feat_food.presentation.components

import android.R.attr.textStyle
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.backgroundLightGrey
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorHintText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.secondaryColor

@Composable
fun SimpleTextField(
    modifier: Modifier = Modifier,
    hintText: String,
    hintTextStyle: TextStyle = TextStyle(color = colorHintText, fontSize = 14.sp),
    inputTextStyle: TextStyle = TextStyle(color = colorText, fontSize = 14.sp),
    isNumeric: Boolean = false,
    isDecimal: Boolean = false,
    value: String,
    onValueChange: (String) -> Unit,
) {

    val keyboardType = when {
        isNumeric && isDecimal -> KeyboardType.Decimal
        isNumeric && !isDecimal -> KeyboardType.Number
        else -> KeyboardType.Text
    }

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(
                align = Alignment.CenterVertically

            )
            .border(1.dp, secondaryColor, RoundedCornerShape(8.dp)),
        placeholder = {
            Text(
                text = hintText,
                style = hintTextStyle
            )
        },
        textStyle = inputTextStyle,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = backgroundLightGrey,
            unfocusedContainerColor = backgroundLightGrey,
            cursorColor = colorText,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(8.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Next
        )
    )
}



@Preview
@Composable
fun PreviewSimpleTextField() {
    SimpleTextField(
        hintText = "Name",
        value = "",
        onValueChange = {}
    )
}