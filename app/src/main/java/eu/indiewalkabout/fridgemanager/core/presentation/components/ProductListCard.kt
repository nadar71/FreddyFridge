package eu.indiewalkabout.fridgemanager.core.presentation.components

import android.R.attr.name
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntryUI
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.toFoodEntryUI
import eu.indiewalkabout.fridgemanager.feat_food.presentation.components.FoodCard
import java.time.LocalDate

@Composable
fun ProductListCard(
    modifier: Modifier = Modifier,
    foods: List<FoodEntry> = emptyList(),
    onCheckChanged: (FoodEntryUI, Boolean) -> Unit = { _, _ -> },
    onDelete: (FoodEntryUI) -> Unit = {},
    message: String = stringResource(R.string.no_food_todays)
) {
    val colors = LocalAppColors.current

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = colors.lightGreyVeryTransparent)
    ) {
        if (foods.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message,
                    color = colors.lightWhiteSemitransparent,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                items(foods) { foodEntry ->
                    val food = foodEntry.toFoodEntryUI()
                    FoodCard(
                        food = food,
                        onCheckChanged = { checked -> onCheckChanged(food, checked) },
                        onDelete = { onDelete(food) }
                    )
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewProductListCardWithFoods() {
    val mockFoods = listOf(
        FoodEntry(name = "Milk", quantity = 2,      expiringAt = LocalDate.now()),
        FoodEntry(name = "Bread", quantity = 1,     expiringAt = LocalDate.now().plusDays(1)),
        FoodEntry(name = "Eggs", quantity = 6,      expiringAt = LocalDate.now().plusDays(1)),
        FoodEntry(name = "Butter", quantity = 3,    expiringAt = LocalDate.now().plusDays(2)),
        FoodEntry(name = "Tomatoes", quantity = 4,  expiringAt = LocalDate.now().plusDays(3)),
        FoodEntry(name = "Cheese", quantity = 1,    expiringAt = LocalDate.now().plusDays(4)),
    )

    FreddyFridgeTheme {
        ProductListCard(
            foods = mockFoods,
            onCheckChanged = { _, _ -> },
            onDelete = {}
        )
    }
}