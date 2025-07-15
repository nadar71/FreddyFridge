package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
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
import eu.indiewalkabout.fridgemanager.feat_food.presentation.components.ProductListCard
import eu.indiewalkabout.fridgemanager.core.presentation.components.TopBar
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.secondaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.getPreviousDayEndOfDayDate
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodListUiState
import eu.indiewalkabout.fridgemanager.feat_food.presentation.util.sortedByOpenStatus
import eu.indiewalkabout.fridgemanager.feat_navigation.presentation.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodExpiringScreen(
    foodExpiringViewModel: FoodExpiringViewModel = hiltViewModel(),
) {
    val TAG = "FoodExpiringScreen"
    val colors = LocalAppColors.current

    var loadDataFromDdb by remember { mutableStateOf(true) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    var descriptionText by remember { mutableStateOf("") }
    var expiringFoodList by remember { mutableStateOf<List<FoodEntry>>(emptyList()) }
    var foodListLoaded by remember { mutableStateOf(false) }
    var showProgressBar by remember { mutableStateOf(false) }


    // ----------------------------- LOGIC ---------------------------------------------------------
    val foodListUiState by foodExpiringViewModel.foodListUiState.collectAsState()

    LaunchedEffect(loadDataFromDdb) {
        if (loadDataFromDdb) {
            loadDataFromDdb = false
            foodListLoaded = false
            foodExpiringViewModel.getExpiringFood(getPreviousDayEndOfDayDate())
        }
    }

    // handling loading food list from db
    LaunchedEffect(foodListUiState) {
        when (foodListUiState) {
            is FoodListUiState.Success -> {
                showProgressBar = false
                expiringFoodList = (foodListUiState as FoodListUiState.Success<List<FoodEntry>>)
                    .data
                    .sortedByOpenStatus()
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
            BottomNavigationBar(
                stringResource(R.string.menu_expiring_label_item),
                onNewItemClicked = {
                    showBottomSheet = true
                }
            )
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
                        isUpdatable = true,
                        isDeletable = true,
                        isOpenable = true,
                        sharingTitle = stringResource(R.string.settings_expiring_food_list_subject),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .weight(1f),
                        message = stringResource(R.string.foodExpiring_message),
                        onDelete = {
                            loadDataFromDdb = true
                        },
                        onUpdate = {
                            loadDataFromDdb = true
                        },
                        onCheckChanged = {
                            loadDataFromDdb = true
                        }
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

        if (showBottomSheet) {
            ModalBottomSheet(
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                modifier = Modifier,
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = primaryColor,
            ) {
                InsertFoodBottomSheetContent(
                    descriptionText = descriptionText,
                    onDescriptionChange = { descriptionText = it },
                    onSave = {
                        showBottomSheet = false
                        loadDataFromDdb = true // force food list refresh
                    },
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