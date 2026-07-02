package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.components.TopBar
import eu.indiewalkabout.fridgemanager.core.presentation.navigation.AppDestination
import eu.indiewalkabout.fridgemanager.core.presentation.navigation.components.TopLevelScreenScaffold
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.secondaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.getPreviousDayEndOfDayDate
import eu.indiewalkabout.fridgemanager.feat_food.presentation.components.ProductListCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodExpiringScreen(
    foodExpiringViewModel: FoodExpiringViewModel = hiltViewModel(),
    foodViewModel: FoodMutationViewModel = hiltViewModel(),
    insertFoodViewModel: InsertFoodViewModel = hiltViewModel(),
    selectedDestination: AppDestination = AppDestination.Expiring,
    onNavigateToDestination: (AppDestination) -> Unit = {},
) {
    val colors = LocalAppColors.current

    val uiState by foodExpiringViewModel.uiState.collectAsState()
    val isInserting by insertFoodViewModel.isInserting.collectAsState()
    val isMutating by foodViewModel.isMutating.collectAsState()

    LaunchedEffect(Unit) {
        foodExpiringViewModel.getExpiringFood(getPreviousDayEndOfDayDate())
    }

    FoodListScreenEffects(
        isInserting = isInserting,
        isMutating = isMutating,
        insertEvents = insertFoodViewModel.events,
        mutationEvents = foodViewModel.events,
        screenEvents = foodExpiringViewModel.events,
        onInsertLoading = foodExpiringViewModel::handleInsertLoading,
        onInsertResult = foodExpiringViewModel::handleInsertResult,
        onUpdateLoading = foodExpiringViewModel::handleUpdateLoading,
        onUpdateResult = foodExpiringViewModel::handleUpdateResult,
    )

    TopLevelScreenScaffold(
        selectedDestination = selectedDestination,
        onDestinationSelected = onNavigateToDestination,
        onNewItemClicked = { foodExpiringViewModel.setBottomSheetVisible(true) },
        isBottomSheetVisible = uiState.isBottomSheetVisible,
        onBottomSheetDismiss = { foodExpiringViewModel.setBottomSheetVisible(false) },
        bottomSheetContent = { InsertFoodBottomSheetContent() },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(
                title = stringResource(R.string.foodExpiring_title),
                paddingTop = 16.dp,
                paddingBottom = 16.dp,
                backgroundColor = colors.primaryColor,
            )

            if (uiState.hasLoadedFood) {
                ProductListCard(
                    foods = uiState.foods,
                    isUpdatable = true,
                    isDeletable = true,
                    isOpenable = true,
                    sharingTitle = stringResource(R.string.settings_expiring_food_list_subject),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .weight(1f),
                    message = stringResource(R.string.foodExpiring_message),
                    onCheckChanged = {}
                )
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier,
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = secondaryColor)
                }
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
