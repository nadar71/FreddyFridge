package eu.indiewalkabout.fridgemanager.core.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import eu.indiewalkabout.fridgemanager.R

@Composable
fun BackgroundPattern() {
    Image(
        painter = painterResource(id = R.drawable.food_background), // Your patterned green BG
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
        alpha = 0.25f
    )
}