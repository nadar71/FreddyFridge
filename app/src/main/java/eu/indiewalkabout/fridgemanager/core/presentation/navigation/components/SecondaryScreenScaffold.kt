package eu.indiewalkabout.fridgemanager.core.presentation.navigation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import eu.indiewalkabout.fridgemanager.core.presentation.components.BackgroundPattern
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors

@Composable
fun SecondaryScreenScaffold(
    bottomBar: @Composable () -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = LocalAppColors.current

    Scaffold(
        bottomBar = bottomBar,
        containerColor = colors.primaryColor,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            BackgroundPattern()
            content()
        }
    }
}

@Preview
@Composable
private fun SecondaryScreenScaffoldPreview() {
    FreddyFridgeTheme {
        SecondaryScreenScaffold {
            Text(text = "Secondary screen content")
        }
    }
}
