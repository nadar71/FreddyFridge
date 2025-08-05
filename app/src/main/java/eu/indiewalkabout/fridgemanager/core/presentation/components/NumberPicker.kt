package eu.indiewalkabout.fridgemanager.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorAccent
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.backgroundLightGrey
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_20

@Composable
fun NumberPicker(
    items: List<Int>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .background(backgroundLightGrey, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .padding(0.dp, 8.dp, 0.dp, 10.dp)
            .height(150.dp),
        verticalArrangement = Arrangement.Center,
        contentPadding = PaddingValues(vertical = 0.dp)
    ) {
        items(items) { item ->
            // Each item in the list
            Text(
                text = item.toString(),
                style = text_20(if (item == selectedItem) colorAccent else colorText, false),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null, // Disable ripple effect
                    ) { onItemSelected(item) }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewNumberPicker() {
    var selectedInteger by remember { mutableStateOf(1) } // For the 1-1000 section
    val integers = (1..1000).toList()
    NumberPicker(
        items = integers,
        selectedItem = selectedInteger,
        onItemSelected = { selectedInteger = it },
    )

}