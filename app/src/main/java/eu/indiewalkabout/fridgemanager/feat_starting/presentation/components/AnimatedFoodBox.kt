package eu.indiewalkabout.fridgemanager.feat_starting.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.secondaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.feat_starting.presentation.ui.intromain.MainScreen
import kotlinx.coroutines.launch

/*@Composable
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
}*/


@Composable
fun AnimatedFoodBox() {
    var boxWidth by remember { mutableStateOf(0) }
    var boxSize by remember { mutableStateOf(IntSize.Zero) }
    var targetLeft by remember { mutableStateOf(0f) }
    var targetRight by remember { mutableStateOf(0f) }
    val foodLeftOffsetX = remember { Animatable(-boxWidth.toFloat()) }
    val foodRightOffsetX = remember { Animatable(boxWidth.toFloat()) }

    LaunchedEffect(boxWidth) {
        if (boxWidth > 0) {
            targetLeft = -boxWidth / 4.0f // tune the factor as needed
            targetRight = boxWidth / 3.15f

            launch {
                foodLeftOffsetX.animateTo(
                    targetValue = targetLeft,
                    animationSpec = tween(
                        durationMillis = boxWidth, //(1500 * (1.0f/boxWidth)).toInt(),
                        easing = FastOutSlowInEasing
                    )
                )
            }

            launch {
                foodRightOffsetX.animateTo(
                    targetValue = targetRight,
                    animationSpec = tween(
                        durationMillis = boxWidth, //(1500 * (1.0f/boxWidth)).toInt(),
                        easing = FastOutSlowInEasing
                    )
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(vertical = 12.dp)
            .onGloballyPositioned { coordinates ->
                boxWidth = coordinates.size.width
                boxSize = coordinates.size
            }
    ) {

        // Food Left
        Image(
            painter = painterResource(id = R.drawable.food_left),
            contentDescription = "Food Left",
            modifier = Modifier
                .padding(end = 30.dp)
                .height(80.dp)
                .offset { IntOffset(foodLeftOffsetX.value.toInt(), 0) }
                .align(Alignment.Center),
            contentScale = ContentScale.FillHeight
        )

        // Food Right
        Image(
            painter = painterResource(id = R.drawable.food_right),
            contentDescription = "Food Right",
            modifier = Modifier
                .padding(end = 20.dp)
                .height(80.dp)
                .offset { IntOffset(foodRightOffsetX.value.toInt(), 0) }
                .align(Alignment.Center),
            contentScale = ContentScale.FillHeight
        )

        // Fridge Background
        Image(
            painter = painterResource(id = R.drawable.fridge_background),
            contentDescription = "Fridge Background",
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        )

        // Fridge Foreground
        Image(
            painter = painterResource(id = R.drawable.fridge_foreground),
            contentDescription = "Fridge Foreground",
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        )

        /*Text(
            // text = "Box size: ${boxSize.width} x ${boxSize.height} px",
            text = "Box size: ${boxSize.width} x ${boxSize.height} px \n " +
                    "Target Left: $targetLeft \n Target Right: $targetRight",

            color = secondaryColor,
            modifier = Modifier
                .align(Alignment.BottomStart),

            )*/
    }


}

/*@Composable
fun AnimatedFoodBox() {
    val foodLeftOffsetX = remember { Animatable(-350f) } // Start off-screen to the left
    val foodRightOffsetX = remember { Animatable(350f) } // Start off-screen to the right

    var boxWidth by remember { mutableStateOf(0) }
    var boxSize by remember { mutableStateOf(IntSize.Zero) }
    var targetLeft by remember { mutableStateOf(0f) }
    var targetRight by remember { mutableStateOf(0f) }

    LaunchedEffect(boxWidth) {
        if (boxWidth > 0) {
            targetLeft = -boxWidth / 40.0f // tune the factor as needed
            targetRight = boxWidth / 40.0f


            launch {
                foodLeftOffsetX.animateTo(
                    targetValue = targetLeft, //-5f,
                    animationSpec = tween(
                        durationMillis = 1500,
                        easing = FastOutSlowInEasing
                    )
                )
            }

            launch {
                foodRightOffsetX.animateTo(
                    targetValue = targetRight, //20f,
                    animationSpec = tween(
                        durationMillis = 1500,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        }
    }


    Box(
        modifier = Modifier
            .height(120.dp)
            .padding(vertical = 12.dp)
            .onGloballyPositioned { coordinates ->
                boxWidth = coordinates.size.width
                boxSize = coordinates.size
            }
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
                .padding(end = 0.dp)
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


        Text(
            // text = "Box size: ${boxSize.width} x ${boxSize.height} px",
            text = "Box size: ${boxSize.width} x ${boxSize.height} px \n " +
                    "Target Left: $targetLeft \n Target Right: $targetRight",

            color = secondaryColor,
            modifier = Modifier
                .align(Alignment.BottomStart),

        )
    }
}*/




@Preview
@Composable
fun PreviewMainScreen() {
    FreddyFridgeTheme {
        AnimatedFoodBox()
    }
}