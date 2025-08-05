package eu.indiewalkabout.fridgemanager.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

// Placeholder Composable for the Ad Banner
@Composable
fun AdBannerPlaceholder(modifier: Modifier = Modifier.Companion) {
    Box(
        modifier = modifier
            .background(Color.Companion.Gray), // Use a gray background as a placeholder
        contentAlignment = Alignment.Companion.Center
    ) {
        Text(text = "Ad Banner Placeholder", color = Color.Companion.White)
    }
}