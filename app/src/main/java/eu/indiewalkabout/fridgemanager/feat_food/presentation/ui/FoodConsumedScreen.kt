package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui


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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.components.BackgroundPattern
import eu.indiewalkabout.fridgemanager.core.presentation.navigation.components.BottomNavigationBar
import eu.indiewalkabout.fridgemanager.feat_food.presentation.components.ProductListCard
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.core.presentation.components.TopBar
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.secondaryColor
import eu.indiewalkabout.fridgemanager.feat_ads.presentation.AdMobBannerView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodConsumedScreen(
    foodConsumedViewModel: FoodConsumedViewModel = hiltViewModel(),
    insertFoodViewModel: InsertFoodViewModel = hiltViewModel(),
    foodViewModel: FoodMutationViewModel = hiltViewModel()
)  {
    val colors = LocalAppColors.current

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    // ----------------------------- LOGIC ---------------------------------------------------------
    val uiState by foodConsumedViewModel.uiState.collectAsState()
    val isInserting by insertFoodViewModel.isInserting.collectAsState()
    val isMutating by foodViewModel.isMutating.collectAsState()

    LaunchedEffect(Unit) {
        foodConsumedViewModel.getConsumedFood()
    }

    FoodListScreenEffects(
        isInserting = isInserting,
        isMutating = isMutating,
        insertEvents = insertFoodViewModel.events,
        mutationEvents = foodViewModel.events,
        screenEvents = foodConsumedViewModel.events,
        onInsertLoading = foodConsumedViewModel::handleInsertLoading,
        onInsertResult = foodConsumedViewModel::handleInsertResult,
        onUpdateLoading = foodConsumedViewModel::handleUpdateLoading,
        onUpdateResult = foodConsumedViewModel::handleUpdateResult,
    )


    // ----------------------------- UI ---------------------------------------------------------
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                stringResource(R.string.menu_consumed_label_item),
                onNewItemClicked = {
                    foodConsumedViewModel.setBottomSheetVisible(true)
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
                    title = stringResource(R.string.foodConsumed_title),
                    paddingTop = 16.dp,
                    paddingBottom = 16.dp,
                    backgroundColor = colors.primaryColor,
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (uiState.hasLoadedFood) {
                    ProductListCard(
                        foods = uiState.foods,
                        isUpdatable = true,
                        isDeletable = true,
                        isOpenable = false,
                        sharingTitle = stringResource(R.string.settings_saved_food_list_subject),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .weight(1f),
                        message = stringResource(R.string.foodConsumed_message),
                        onCheckChanged = {},
                    )
                }

                // Show Progress Bar
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier,
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = secondaryColor)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp)) // Space between card and ad

                // Ad Banner
                AdMobBannerView(adUnitId = stringResource(R.string.admob_key_bottom_banner))
            }
        }


        if (uiState.isBottomSheetVisible) {
            ModalBottomSheet(
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                modifier = Modifier,
                onDismissRequest = { foodConsumedViewModel.setBottomSheetVisible(false) },
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
fun PreviewFoodConsumedScreen() {
    FreddyFridgeTheme {
        FoodConsumedScreen()
    }
}
