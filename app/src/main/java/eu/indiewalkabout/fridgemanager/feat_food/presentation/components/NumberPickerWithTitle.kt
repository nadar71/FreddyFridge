package eu.indiewalkabout.fridgemanager.feat_food.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.core.presentation.components.NumberPicker
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.backgroundLightGrey
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_24

@Composable
fun NumberPickerWithTitle(
    title: String,
    max: Int,
    onItemSelected: (String) -> Unit,
) {
    var selectedInteger by remember { mutableStateOf(1) }

    val integers = (1..max).toList()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(primaryColor, shape = RoundedCornerShape(8.dp))
    ) {
        
        Row(
            modifier = Modifier
                .padding(16.dp, 16.dp, 16.dp, 0.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundLightGrey, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp, 8.dp, 16.dp, 8.dp),
                text = title + " ${selectedInteger}",
                style = text_24(colorText, false),
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Wheel for the integer part (1 to 1000)
            NumberPicker(
                items = integers,
                selectedItem = selectedInteger,
                onItemSelected = {
                    selectedInteger = it
                    onItemSelected("${selectedInteger}")},
                modifier = Modifier.weight(1f)
            )

        }
    }

}


@Preview(showBackground = true)
@Composable
fun PreviewNumberPickerWithTitle() {
    var selectedItem by remember { mutableStateOf(1) }
    NumberPickerWithTitle(
        title = "Quantità",
        max = 50,
        onItemSelected = { selectedItem = it.toInt() },
    )
}




