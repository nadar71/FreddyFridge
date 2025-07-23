package eu.indiewalkabout.fridgemanager.feat_food.presentation.components

import android.content.Context
import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.secondaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.toFoodEntryUI
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter



@Composable
fun ProductListCard(
    modifier: Modifier = Modifier,
    foods: List<FoodEntry> = emptyList(),
    isUpdatable: Boolean = false,
    isDeletable: Boolean = false,
    isOpenable: Boolean = false,
    sharingTitle: String = "",
    onCheckChanged: () -> Unit = {},
    message: String = stringResource(R.string.no_food_todays)
) {
    val context = LocalContext.current
    val colors = LocalAppColors.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var isFabVisible by remember { mutableStateOf(true) }
    
    // Show FAB when scrolling stops, hide when scrolling
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { isScrolling ->
                isFabVisible = !isScrolling
            }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colors.lightGreyVeryTransparent)
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
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                items(foods) { foodEntry ->
                    val food = foodEntry.toFoodEntryUI()
                    FoodCard(
                        food = food,
                        isUpdatable = isUpdatable,
                        isDeletable = isDeletable,
                        isOpenable = isOpenable,
                        onCheckChanged = { onCheckChanged() },
                    )
                }
            }
        }

        // FAB positioned half outside the card
        AnimatedVisibility(
            visible = foods.isNotEmpty() && isFabVisible,
            enter = fadeIn(), // + slideIn(initialOffset = { IntOffset(0, 20) }),
            exit = fadeOut(), //+ slideOut(targetOffset = { IntOffset(0, 20) }),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                // .offset(x = 10.dp, y = (-10).dp)
        ) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
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
                    }
                },
                modifier = Modifier
                    .size(40.dp)
                    .padding(4.dp),
                shape = CircleShape,
                containerColor = primaryColor,
                contentColor = secondaryColor,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_share),
                    contentDescription = sharingTitle,
                    modifier = Modifier.size(20.dp)
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
