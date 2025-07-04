package eu.indiewalkabout.fridgemanager.feat_food.presentation.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.secondaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.toFoodEntryUI
import java.time.format.DateTimeFormatter



@Composable
fun ProductListCard(
    modifier: Modifier = Modifier,
    foods: List<FoodEntry> = emptyList(),
    sharingTitle: String = "",
    onCheckChanged: () -> Unit = {},
    onDelete: () -> Unit = {},
    onUpdate: () -> Unit = {},
    isUpdatable: Boolean = false,
    message: String = stringResource(R.string.no_food_todays)
) {
    val context = LocalContext.current
    val colors = LocalAppColors.current

    Box(modifier = modifier) {
        Card(
            modifier = Modifier.fillMaxSize(),
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
                            onCheckChanged = { onCheckChanged() },
                            onDelete = { onDelete() },
                            onUpdate = { onUpdate() },
                            isUpdatable = isUpdatable
                        )
                    }
                }
            }
        }

        if (foods.isNotEmpty()) {
            FloatingActionButton(
                onClick = {
                    val shareText = formatFoodListForSharing(
                        context,
                        sharingTitle,
                        foods
                    )
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                },
                modifier = Modifier
                    //.padding(16.dp)
                    .align(Alignment.BottomEnd)
                    .size(40.dp),
                shape = CircleShape,
                containerColor = primaryColor,
                contentColor = secondaryColor
            ) {
                Icon(
                    // imageVector = Icons.Default.Share,
                    painter = painterResource(id = R.drawable.ic_share),
                    contentDescription = sharingTitle
                )
            }
        }
    }
}


private fun formatFoodListForSharing(
    context: Context,
    sharingTitle: String,
    foods: List<FoodEntry>
): String {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return buildString {
        append("🍽️" + " $sharingTitle\n\n")
        foods.forEach { food ->
            append("• ${food.name}")
            // if (food.quantity > 1) append(" (${food.quantity})")
            food.expiringAt?.let { expiringAt ->
                append(" - " + context.getString(R.string.food_expiring_date_label)
                        +" ${expiringAt.format(dateFormatter)}" + ": ")
            }
            if (food.isProductOpen) append(" - " + context.getString(R.string.food_open_label))
            // if (food.brand.isNotBlank()) append(" - Brand: ${food.brand}")
            append("\n")
        }
        append("\nShared by FreddyFridge App")
    }
}



/*
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
}*/
