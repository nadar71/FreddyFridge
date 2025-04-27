package eu.indiewalkabout.fridgemanager.presentation.ui.intromain

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.domain.navigation.AppDestinationRoutes
import eu.indiewalkabout.fridgemanager.core.domain.navigation.AppNavigation.navigate
import eu.indiewalkabout.fridgemanager.core.presentation.components.BackgroundPattern
import eu.indiewalkabout.fridgemanager.core.presentation.components.BottomNavigationBar
import eu.indiewalkabout.fridgemanager.core.presentation.components.ProductListCard
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.presentation.theme.Fredoka
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_16
import eu.indiewalkabout.fridgemanager.presentation.components.TopBar
import eu.indiewalkabout.fridgemanager.presentation.ui.tutorials.OnBoardingScreenOverlay
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    val colors = LocalAppColors.current

    var showOnBoarding by remember { mutableStateOf(false) }

    if (showOnBoarding) {
        OnBoardingScreenOverlay()
    }


    Scaffold(
        bottomBar = {
            BottomNavigationBar()
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
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {



                // title
                Image(
                    painter = painterResource(id = R.drawable.img_app_title), // Replace with actual image
                    contentDescription = "FreddyFridge Logo",
                    modifier = Modifier
                        .height(80.dp)
                        .padding(vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                TopBar(
                    title = stringResource(R.string.main_subtitle),
                    onLeftIconClick = {
                        showOnBoarding = true
                    },
                    onRightIconClick = {
                        navigate(AppDestinationRoutes.SettingsScreen.route)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedFoodBox()

                /*Box(
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
                            .offset(x = (-5).dp) // Adjust this value to control the offset
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
                            .offset(x = 20.dp)
                            .align(Alignment.CenterEnd),
                        contentScale = ContentScale.FillHeight
                    )

                    // Fridge Background (Middle)
                    Image(
                        painter = painterResource(id = R.drawable.fridge_background_white),
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
                }*/

                // List Section Title
                Text(
                    text = stringResource(R.string.main_list_title),
                    fontFamily = Fredoka,
                    fontWeight = FontWeight.SemiBold,
                    style = text_16(colors.colorText, true),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 4.dp)
                )

                ProductListCard()

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
            painter = painterResource(id = R.drawable.fridge_background_white),
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