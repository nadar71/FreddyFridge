package eu.indiewalkabout.fridgemanager.feat_starting.presentation.ui.intromain

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import eu.indiewalkabout.fridgemanager.FreddyFridgeApp.Companion.alarmReminderScheduler
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.components.TopBar
import eu.indiewalkabout.fridgemanager.core.presentation.navigation.AppDestination
import eu.indiewalkabout.fridgemanager.core.presentation.navigation.components.TopLevelScreenScaffold
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.secondaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.presentation.theme.Fredoka
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_16
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.getEndOfTodayEpochMillis
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.getPreviousDayEndOfDayDate
import eu.indiewalkabout.fridgemanager.feat_food.presentation.components.ProductListCard
import eu.indiewalkabout.fridgemanager.feat_food.presentation.ui.FoodMutationEvent
import eu.indiewalkabout.fridgemanager.feat_food.presentation.ui.FoodMutationViewModel
import eu.indiewalkabout.fridgemanager.feat_food.presentation.ui.InsertFoodBottomSheetContent
import eu.indiewalkabout.fridgemanager.feat_food.presentation.ui.InsertFoodEvent
import eu.indiewalkabout.fridgemanager.feat_food.presentation.ui.InsertFoodViewModel
import eu.indiewalkabout.fridgemanager.feat_starting.presentation.components.AnimatedFoodBox
import eu.indiewalkabout.fridgemanager.feat_starting.presentation.ui.tutorials.OnBoardingScreenOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    insertFoodViewModel: InsertFoodViewModel = hiltViewModel(),
    foodViewModel: FoodMutationViewModel = hiltViewModel(),
    onOpenSettings: () -> Unit = {},
    selectedDestination: AppDestination = AppDestination.Main,
    onNavigateToDestination: (AppDestination) -> Unit = {},
) {
    val tag = "MainScreen"
    val context = LocalContext.current

    val uiState by mainViewModel.uiState.collectAsState()
    val isInserting by insertFoodViewModel.isInserting.collectAsState()
    val isMutating by foodViewModel.isMutating.collectAsState()

    LaunchedEffect(Unit) {
        mainViewModel.getFoodExpiringToday(getPreviousDayEndOfDayDate(), getEndOfTodayEpochMillis())
    }

    LaunchedEffect(isMutating) {
        mainViewModel.handleUpdateLoading(isMutating)
    }

    LaunchedEffect(Unit) {
        foodViewModel.events.collect { event ->
            when (event) {
                FoodMutationEvent.Success -> mainViewModel.handleUpdateResult(true)
                is FoodMutationEvent.Error -> mainViewModel.handleUpdateResult(false)
            }
        }
    }

    LaunchedEffect(Unit) {
        insertFoodViewModel.events.collect { event ->
            when (event) {
                InsertFoodEvent.Success -> mainViewModel.handleInsertResult(true)
                is InsertFoodEvent.Error -> mainViewModel.handleInsertResult(false)
            }
        }
    }

    LaunchedEffect(isInserting) {
        mainViewModel.handleInsertLoading(isInserting)
    }

    LaunchedEffect(Unit) {
        mainViewModel.events.collect { event ->
            when (event) {
                is MainUiEvent.ShowToast -> {
                    Toast.makeText(
                        context,
                        context.getString(event.messageResId),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                MainUiEvent.RefreshExpiringNotifications -> {
                    alarmReminderScheduler.setRepeatingAlarm()
                }
            }
        }
    }

    if (uiState.showOnBoarding) {
        OnBoardingScreenOverlay()
    }

    TopLevelScreenScaffold(
        selectedDestination = selectedDestination,
        onDestinationSelected = onNavigateToDestination,
        onNewItemClicked = { mainViewModel.setBottomSheetVisible(true) },
        isBottomSheetVisible = uiState.isBottomSheetVisible,
        onBottomSheetDismiss = { mainViewModel.setBottomSheetVisible(false) },
        bottomSheetContent = { InsertFoodBottomSheetContent() },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_app_title),
                contentDescription = "FreddyFridge Logo",
                modifier = Modifier
                    .height(80.dp)
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .clickable {
                        Log.d(tag, "MainScreen: settings icon pressed")
                        onOpenSettings()
                    }
            )

            Spacer(modifier = Modifier.height(8.dp))

            TopBar(
                title = stringResource(R.string.main_subtitle),
                drawableRightIcon = R.drawable.ic_flower_white,
                paddingStart = 16.dp,
                paddingEnd = 16.dp,
                onLeftIconClick = {
                    Log.d(tag, "MainScreen: help icon pressed")
                    mainViewModel.setOnBoardingVisible(true)
                },
                onRightIconClick = {
                    Log.d(tag, "MainScreen: settings icon pressed")
                    onOpenSettings()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedFoodBox()

            Text(
                text = stringResource(R.string.main_list_title),
                fontFamily = Fredoka,
                fontWeight = FontWeight.SemiBold,
                style = text_16(colorText, true),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 4.dp)
            )

            if (uiState.hasLoadedFood) {
                ProductListCard(
                    foods = uiState.foods,
                    sharingTitle = stringResource(R.string.settings_share_today_title),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .weight(1f),
                    isUpdatable = true,
                    isDeletable = true,
                    isOpenable = true,
                    onCheckChanged = {},
                    message = stringResource(R.string.foodExpiring_message)
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
fun PreviewMainScreen() {
    FreddyFridgeTheme {
        MainScreen()
    }
}
