package eu.indiewalkabout.fridgemanager.feat_starting.presentation.ui.intromain

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import eu.indiewalkabout.fridgemanager.FreddyFridgeApp.Companion.alarmReminderScheduler
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.components.AdBannerPlaceholder
import eu.indiewalkabout.fridgemanager.core.presentation.components.BackgroundPattern
import eu.indiewalkabout.fridgemanager.feat_food.presentation.components.ProductListCard
import eu.indiewalkabout.fridgemanager.core.presentation.components.TopBar
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.secondaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.presentation.theme.Fredoka
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_16
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.getEndOfTodayEpochMillis
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.getPreviousDayEndOfDayDate
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodListUiState
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodUiState
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodUpdateUiState
import eu.indiewalkabout.fridgemanager.feat_food.presentation.ui.FoodViewModel
import eu.indiewalkabout.fridgemanager.feat_food.presentation.ui.InsertFoodBottomSheetContent
import eu.indiewalkabout.fridgemanager.feat_food.presentation.ui.InsertFoodViewModel
import eu.indiewalkabout.fridgemanager.feat_navigation.domain.navigation.AppDestinationRoutes
import eu.indiewalkabout.fridgemanager.feat_navigation.domain.navigation.AppNavigation
import eu.indiewalkabout.fridgemanager.feat_navigation.presentation.components.BottomNavigationBar
import eu.indiewalkabout.fridgemanager.feat_starting.presentation.ui.tutorials.OnBoardingScreenOverlay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    insertFoodViewModel: InsertFoodViewModel = hiltViewModel(),
    foodViewModel: FoodViewModel = hiltViewModel()
) {
    val TAG = "MainScreen"
    val context = LocalContext.current
    var loadDataFromDdb by remember { mutableStateOf(true) }
    var showOnBoarding by remember { mutableStateOf(false) }

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    var expiringTodayFoodList by remember { mutableStateOf<List<FoodEntry>>(emptyList()) }
    var foodListLoaded by remember { mutableStateOf(false) }
    var showProgressBar by remember { mutableStateOf(false) }

    // ----------------------------- LOGIC ---------------------------------------------------------
    val foodListUiState by mainViewModel.foodListUiState.collectAsState()
    val unitUiState by insertFoodViewModel.unitUiState.collectAsState()
    val updateUiState by foodViewModel.updateUiState.collectAsState()


    LaunchedEffect(loadDataFromDdb) {
        if (loadDataFromDdb) {
            loadDataFromDdb = false
            foodListLoaded = false
            mainViewModel.getFoodExpiringToday(getPreviousDayEndOfDayDate(),getEndOfTodayEpochMillis())
        }
    }

    // handling loading food list from db
    LaunchedEffect(foodListUiState) {
        when (foodListUiState) {
            is FoodListUiState.Success -> {
                showProgressBar = false
                expiringTodayFoodList = (foodListUiState
                        as FoodListUiState.Success<List<FoodEntry>>).data
                foodListLoaded = true
                Log.d(TAG, "foodExpiringListLoaded : $expiringTodayFoodList")
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
    if (showOnBoarding) {
        OnBoardingScreenOverlay()
    }



    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                stringResource(R.string.menu_home_item),
                onNewItemClicked = {
                    showBottomSheet = true
                }
            )
        },
        containerColor = primaryColor
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
                //.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                // title
                Image(
                    painter = painterResource(id = R.drawable.img_app_title), // Replace with actual image
                    contentDescription = "FreddyFridge Logo",
                    modifier = Modifier
                        .height(80.dp)
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                        .clickable(onClick = {
                            Log.d(TAG, "MainScreen: settings icon pressed")
                            AppNavigation.appNavHostController.navigate(AppDestinationRoutes.SettingsScreen.route)
                        })
                )
                Spacer(modifier = Modifier.height(8.dp))
                TopBar(
                    title = stringResource(R.string.main_subtitle),
                    drawableLeftIcon = R.drawable.ic_help_outline,
                    drawableRightIcon = R.drawable.ic_flower_white,
                    paddingStart = 16.dp,
                    paddingEnd = 16.dp,
                    onLeftIconClick = {
                        Log.d(TAG, "MainScreen: help icon pressed")
                        showOnBoarding = true
                    },
                    onRightIconClick = {
                        Log.d(TAG, "MainScreen: settings icon pressed")
                        // navigate(AppDestinationRoutes.SettingsScreen.route)
                        AppNavigation.appNavHostController.navigate(AppDestinationRoutes.SettingsScreen.route)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedFoodBox()

                // List Section
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

                /*ProductListCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .weight(1f), // card take all available vertical space
                )*/

                if (foodListLoaded) {
                    ProductListCard(
                        foods = expiringTodayFoodList,
                        sharingTitle = stringResource(R.string.settings_share_today_title),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .weight(1f),
                        isUpdatable = true,
                        isDeletable = true,
                        isOpenable = true,
                        onCheckChanged = {
                            loadDataFromDdb = true
                        },
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


@Composable
fun AnimatedFoodBox() {
    val foodLeftOffsetX = remember { Animatable(-350f) } // Start off-screen to the left
    val foodRightOffsetX = remember { Animatable(350f) } // Start off-screen to the right

    LaunchedEffect(key1 = true) {
        // Launch both animations in parallel
        launch {
            foodLeftOffsetX.animateTo(
                targetValue = -5f,
                animationSpec = tween(
                    durationMillis = 1500,
                    easing = FastOutSlowInEasing
                )
            )
        }

        launch {
            foodRightOffsetX.animateTo(
                targetValue = 20f,
                animationSpec = tween(
                    durationMillis = 1500,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }


    Box(
        modifier = Modifier
            .height(120.dp)
            .padding(vertical = 12.dp)
    ) {

        // Food Left (Behind)
        Image(
            painter = painterResource(id = R.drawable.food_left),
            contentDescription = "Food Left",
            modifier = Modifier
                .padding(end = 30.dp)
                .height(80.dp)
                .offset { IntOffset(foodLeftOffsetX.value.toInt(), 0) }
                .align(Alignment.CenterStart),
            contentScale = ContentScale.FillHeight
        )

        // Food Right (Behind)
        Image(
            painter = painterResource(id = R.drawable.food_right),
            contentDescription = "Food Right",
            modifier = Modifier
                .padding(end = 20.dp)
                .height(80.dp)
                .offset { IntOffset(foodRightOffsetX.value.toInt(), 0) }
                .align(Alignment.CenterEnd),
            contentScale = ContentScale.FillHeight
        )

        // Fridge Background (Middle)
        Image(
            painter = painterResource(id = R.drawable.fridge_background),
            contentDescription = "Fridge Background",
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        )

        // Fridge Foreground (Middle)
        Image(
            painter = painterResource(id = R.drawable.fridge_foreground),
            contentDescription = "Fridge Foreground",
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        )
    }
}

@Preview
@Composable
fun PreviewMainScreen() {
    FreddyFridgeTheme {
        MainScreen()
    }
}