package eu.indiewalkabout.fridgemanager.core.presentation.navigation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.components.BackgroundPattern
import eu.indiewalkabout.fridgemanager.core.presentation.navigation.AppDestination
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.feat_ads.presentation.AdMobBannerView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopLevelScreenScaffold(
    selectedDestination: AppDestination?,
    onDestinationSelected: (AppDestination) -> Unit,
    onNewItemClicked: (() -> Unit)? = null,
    isBottomSheetVisible: Boolean = false,
    onBottomSheetDismiss: () -> Unit = {},
    bottomSheetContent: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = LocalAppColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedDestination = selectedDestination,
                onDestinationSelected = onDestinationSelected,
                onNewItemClicked = onNewItemClicked,
            )
        },
        containerColor = colors.primaryColor,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            BackgroundPattern()
            Column(modifier = Modifier.fillMaxSize()) {
                content()
                Spacer(modifier = Modifier.height(16.dp))
                AdMobBannerView(adUnitId = stringResource(R.string.admob_key_bottom_banner))
            }
        }
    }

    if (isBottomSheetVisible && bottomSheetContent != null) {
        ModalBottomSheet(
            onDismissRequest = onBottomSheetDismiss,
            sheetState = sheetState,
            containerColor = colors.primaryColor,
        ) {
            bottomSheetContent()
        }
    }
}
