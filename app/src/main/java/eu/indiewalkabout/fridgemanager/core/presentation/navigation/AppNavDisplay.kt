package eu.indiewalkabout.fridgemanager.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import eu.indiewalkabout.fridgemanager.feat_food.presentation.ui.FoodConsumedScreen
import eu.indiewalkabout.fridgemanager.feat_food.presentation.ui.FoodExpiredScreen
import eu.indiewalkabout.fridgemanager.feat_food.presentation.ui.FoodExpiringScreen
import eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.credits.CreditsScreen
import eu.indiewalkabout.fridgemanager.feat_settings.presentation.ui.settings.SettingsScreen
import eu.indiewalkabout.fridgemanager.feat_starting.presentation.ui.intromain.MainScreen

@Composable
fun AppNavDisplay(
    navigationState: AppNavigationState,
) {
    val saveableStateHolder = rememberSaveableStateHolder()
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current)

    NavDisplay(
        backStack = navigationState.currentBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(saveableStateHolder),
            rememberViewModelStoreNavEntryDecorator(viewModelStoreOwner) { true },
        ),
        sceneStrategies = listOf(SinglePaneSceneStrategy()),
        onBack = navigationState::goBack,
        entryProvider = entryProvider<AppDestination>(
            { destination ->
                NavEntry(destination) {
                    when (destination) {
                        AppDestination.Main -> MainScreen(
                            onOpenSettings = navigationState::openSettings,
                            selectedDestination = AppDestination.Main,
                            onNavigateToDestination = navigationState::navigate,
                        )

                        AppDestination.Expiring -> FoodExpiringScreen(
                            selectedDestination = AppDestination.Expiring,
                            onNavigateToDestination = navigationState::navigate,
                        )

                        AppDestination.Expired -> FoodExpiredScreen(
                            selectedDestination = AppDestination.Expired,
                            onNavigateToDestination = navigationState::navigate,
                        )

                        AppDestination.Consumed -> FoodConsumedScreen(
                            selectedDestination = AppDestination.Consumed,
                            onNavigateToDestination = navigationState::navigate,
                        )

                        AppDestination.Settings -> SettingsScreen(
                            onOpenCredits = navigationState::openCredits,
                        )

                        AppDestination.Credits -> CreditsScreen(
                            onBack = navigationState::goBack,
                        )
                    }
                }
            },
            builder = {}
        ),
    )
}
