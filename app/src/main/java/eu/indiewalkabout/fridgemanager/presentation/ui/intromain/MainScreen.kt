package eu.indiewalkabout.fridgemanager.presentation.ui.intromain

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.components.BackgroundPattern
import eu.indiewalkabout.fridgemanager.core.presentation.components.BottomNavigationBar
import eu.indiewalkabout.fridgemanager.core.presentation.components.ProductListCard
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_16
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_18
import eu.indiewalkabout.fridgemanager.presentation.components.TopBar

@Composable
fun MainScreen() {
    val colors = LocalAppColors.current

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
                TopBar()
                Spacer(modifier = Modifier.height(8.dp))

                // title
                Image(
                    painter = painterResource(id = R.drawable.img_app_title), // Replace with actual image
                    contentDescription = "FreddyFridge Logo",
                    modifier = Modifier
                        .height(80.dp)
                        .padding(vertical = 8.dp)
                )

                // subtitle
                Text(
                    text = "Zero Sprechi!",
                    style = text_18(colors.colorText, true),
                )


                Box(
                    modifier = Modifier
                        .height(160.dp)
                        .padding(vertical = 12.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.fridge_background_white),
                        contentDescription = "Fridge Background",
                        modifier = Modifier.fillMaxSize()
                    )

                    Image(
                        painter = painterResource(id = R.drawable.fridge_foreground),
                        contentDescription = "Fridge Foreground",
                        modifier = Modifier.fillMaxSize()
                    )
                }


                // List Section Title
                Text(
                    text = "Prodotti in scadenza oggi :",
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

@Preview
@Composable
fun PreviewMainScreen() {
    FreddyFridgeTheme {
        MainScreen()
    }
}