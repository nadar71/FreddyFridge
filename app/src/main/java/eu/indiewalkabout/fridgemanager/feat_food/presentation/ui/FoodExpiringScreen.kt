package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.components.AdBannerPlaceholder
import eu.indiewalkabout.fridgemanager.core.presentation.components.BackgroundPattern
import eu.indiewalkabout.fridgemanager.core.presentation.components.ProductListCard
import eu.indiewalkabout.fridgemanager.core.presentation.components.TopBar
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.secondaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.getPreviousDayEndOfDayDate
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodListUiState
import eu.indiewalkabout.fridgemanager.feat_navigation.presentation.components.BottomNavigationBar

@Composable
fun FoodExpiringScreen(
    foodExpiringViewModel: FoodExpiringViewModel = hiltViewModel(),
) {
    val TAG = "FoodExpiringScreen"
    val colors = LocalAppColors.current

    var expiringFoodList by remember { mutableStateOf<List<FoodEntry>>(emptyList()) }
    var foodListLoaded by remember { mutableStateOf(false) }
    var showProgressBar by remember { mutableStateOf(false) }



    // ----------------------------- LOGIC ---------------------------------------------------------
    val foodListUiState by foodExpiringViewModel.foodListUiState.collectAsState()


    LaunchedEffect(Unit) {
        foodExpiringViewModel.getExpiringFood(getPreviousDayEndOfDayDate())
    }

    // handling loading food list from db
    LaunchedEffect(foodListUiState) {
        when (foodListUiState) {
            is FoodListUiState.Success -> {
                showProgressBar = false
                expiringFoodList = (foodListUiState
                        as FoodListUiState.Success<List<FoodEntry>>).data
                foodListLoaded = true
                Log.d(TAG, "foodExpiringListLoaded : $expiringFoodList")
            }
            is FoodListUiState.Error -> {
                showProgressBar = false
                Log.e(TAG, "Error recovering foodList from db")
                foodListLoaded = true
            }
            FoodListUiState.Idle -> {
                showProgressBar = false
            }
            FoodListUiState.Loading -> {
                showProgressBar = true
            }
        }
    }

    // ----------------------------- UI ---------------------------------------------------------

    Scaffold(
        bottomBar = {
            BottomNavigationBar(stringResource(R.string.menu_expiring_label_item))
        },
        containerColor = colors.primaryColor
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            BackgroundPattern()
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                TopBar(
                    title = stringResource(R.string.foodExpiring_title),
                    paddingTop = 16.dp,
                    paddingBottom = 16.dp,
                    backgroundColor = colors.primaryColor,
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (foodListLoaded) {
                    ProductListCard(
                        foods = expiringFoodList,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .weight(1f),
                        message = stringResource(R.string.foodExpiring_message)
                    )
                }

                // Show Progress Bar
                if (showProgressBar) {
                    Box(
                        modifier = Modifier,
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = secondaryColor)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp)) // Space between card and ad

                // Ad Banner Placeholder
                AdBannerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp) // change fixed height to test
                )

            }
        }
    }

}


@Preview
@Composable
fun PreviewFoodExpiringScreen() {
    FreddyFridgeTheme {
        FoodExpiringScreen()
    }
}