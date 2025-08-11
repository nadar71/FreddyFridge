package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import eu.indiewalkabout.fridgemanager.FreddyFridgeApp.Companion.alarmReminderScheduler
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.components.AdBannerPlaceholder
import eu.indiewalkabout.fridgemanager.core.presentation.components.BackgroundPattern
import eu.indiewalkabout.fridgemanager.feat_navigation.presentation.components.BottomNavigationBar
import eu.indiewalkabout.fridgemanager.feat_food.presentation.components.ProductListCard
import eu.indiewalkabout.fridgemanager.core.presentation.components.TopBar
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.secondaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.getPreviousDayEndOfDayDate
import eu.indiewalkabout.fridgemanager.feat_ads.presentation.AdMobBannerView
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodListUiState
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodUiState
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodUpdateUiState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodExpiredScreen(
    foodExpiredViewModel: FoodExpiredViewModel = hiltViewModel(),
    foodViewModel: FoodViewModel = hiltViewModel(),
    insertFoodViewModel: InsertFoodViewModel = hiltViewModel()
)  {
    val TAG = "FoodExpiredScreen"
    val context = LocalContext.current
    val colors = LocalAppColors.current

    var loadDataFromDdb by remember { mutableStateOf(true) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    var expiredFoodList by remember { mutableStateOf<List<FoodEntry>>(emptyList()) }
    var foodListLoaded by remember { mutableStateOf(false) }
    var showProgressBar by remember { mutableStateOf(false) }


    // ----------------------------- LOGIC ---------------------------------------------------------
    val foodListUiState by foodExpiredViewModel.foodListUiState.collectAsState()
    val unitUiState by insertFoodViewModel.unitUiState.collectAsState()
    val updateUiState by foodViewModel.updateUiState.collectAsState()


    LaunchedEffect(loadDataFromDdb) {
        if (loadDataFromDdb) {
            loadDataFromDdb = false
            foodListLoaded = false
            foodExpiredViewModel.getExpiredFood(getPreviousDayEndOfDayDate())
        }
    }

    // handling loading food list from db
    LaunchedEffect(foodListUiState) {
        when (foodListUiState) {
            is FoodListUiState.Success -> {
                showProgressBar = false
                expiredFoodList = (foodListUiState
                        as FoodListUiState.Success<List<FoodEntry>>).data
                foodListLoaded = true
                Log.d(TAG, "foodExpiredListLoaded : $expiredFoodList")
            }
            is FoodListUiState.Error -> {
                showProgressBar = false
                Log.e(TAG, "Error recovering foodList from db")
                foodListLoaded = true
            }
            is FoodListUiState.Idle -> {
                showProgressBar = false
            }
            is FoodListUiState.Loading -> {
                showProgressBar = true
            }
        }
    }

    // Handle update food response
    LaunchedEffect(updateUiState) {
        when (updateUiState) {
            is FoodUpdateUiState.Success -> {
                showProgressBar = false
                Toast.makeText(context,
                    context.getString(R.string.update_food_successfully),
                    Toast.LENGTH_SHORT).show()
                // After Success/Error, reset updateUiState to Idle doesn't re-trigger dialog re-opening
                foodViewModel.resetUpdateUiStateToIdle()
                loadDataFromDdb = true // force food list refresh
            }
            is FoodUpdateUiState.Error -> {
                showProgressBar = false
                Log.e(TAG, "Error updating food in db")
                foodViewModel.resetUpdateUiStateToIdle()
            }
            is FoodUpdateUiState.Loading -> {
                showProgressBar = true
            }
            is FoodUpdateUiState.Idle -> {
                showProgressBar = false
            }
        }
    }

    // Handle insert food response
    LaunchedEffect(unitUiState) {
        when (unitUiState) {
            is FoodUiState.Success -> {
                showProgressBar = false
                Toast.makeText(
                    context,
                    context.getString(R.string.insert_food_successfully),
                    Toast.LENGTH_SHORT
                ).show()
                // refresh scheduler for expiring notifications on new product inserted
                alarmReminderScheduler.setRepeatingAlarm()
                // After Success/Error, reset updateUiState to Idle doesn't re-trigger dialog re-opening
                insertFoodViewModel.resetUpdateUiStateToIdle()
                showBottomSheet = false
                loadDataFromDdb = true // force food list refresh
            }

            is FoodUiState.Error -> {
                showProgressBar = false
                Log.e(TAG, "Error inserting food in db")
                insertFoodViewModel.resetUpdateUiStateToIdle()
            }

            is FoodUiState.Loading -> {
                showProgressBar = true
            }

            is FoodUiState.Idle -> {
                showProgressBar = false
            }
        }
    }

    // ----------------------------- UI ---------------------------------------------------------

    Scaffold(
        bottomBar = {
            // BottomNavigationBar(AppNavigation.getNavController())
            BottomNavigationBar(
                stringResource(R.string.menu_expired_label_item),
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
                    title = stringResource(R.string.foodExpired_title),
                    paddingTop = 16.dp,
                    paddingBottom = 16.dp,
                    backgroundColor = colors.primaryColor,
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (foodListLoaded) {
                    ProductListCard(
                        foods = expiredFoodList,
                        isUpdatable = false,
                        isDeletable = true,
                        isOpenable = false,
                        sharingTitle = stringResource(R.string.settings_wasted_food_list_subject),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .weight(1f),
                        message = stringResource(R.string.foodExpired_message),
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

                // Ad Banner
                AdMobBannerView()
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
                InsertFoodBottomSheetContent()
            }
        }
    }

}


@Preview
@Composable
fun PreviewFoodExpiredScreen() {
    FreddyFridgeTheme {
        FoodExpiredScreen()
    }
}
