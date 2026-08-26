package eu.indiewalkabout.fridgemanager.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation3.runtime.NavBackStack

@Stable
class AppNavigationState internal constructor(
    initialTab: TopLevelDestination = TopLevelDestination.MAIN,
    initialBackStacks: Map<TopLevelDestination, List<AppDestination>> = defaultBackStacks(),
) {
    var selectedTab by mutableStateOf(initialTab)
        private set

    private val tabBackStacks = mutableStateMapOf<TopLevelDestination, NavBackStack<AppDestination>>()

    init {
        TopLevelDestination.entries.forEach { destination ->
            tabBackStacks[destination] = navBackStackOf(
                initialBackStacks[destination].orEmpty().ifEmpty { listOf(destination.root) }
            )
        }
    }

    val currentBackStack: NavBackStack<AppDestination>
        get() = tabBackStacks.getValue(selectedTab)

    fun selectTab(tab: TopLevelDestination) {
        selectedTab = tab
    }

    fun navigate(destination: AppDestination) {
        destination.topLevelDestination?.let { topLevelDestination ->
            selectTab(topLevelDestination)
            return
        }

        val backStack = currentBackStack
        if (backStack.lastOrNull() != destination) {
            backStack.add(destination)
        }
    }

    fun goBack() {
        val backStack = currentBackStack
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    fun openSettings() = navigate(AppDestination.Settings)

    fun openCredits() = navigate(AppDestination.Credits)

    fun snapshot(): List<String> {
        return buildList {
            add(selectedTab.name)
            TopLevelDestination.entries.forEach { destination ->
                add(
                    tabBackStacks
                        .getValue(destination)
                        .joinToString(separator = SAVED_DESTINATION_SEPARATOR) {
                            AppDestination.toLegacyRoute(it)
                        }
                )
            }
        }
    }

    companion object {
        val Saver = listSaver<AppNavigationState, String>(
            save = { it.snapshot() },
            restore = ::restore,
        )

        fun restore(savedState: List<String>): AppNavigationState {
            val selectedTab = savedState
                .firstOrNull()
                ?.let(TopLevelDestination::valueOf)
                ?: TopLevelDestination.MAIN

            val restoredBackStacks = TopLevelDestination.entries
                .mapIndexed { index, destination ->
                    destination to decodeBackStack(
                        savedState.getOrNull(index + 1),
                        fallback = destination.root,
                    )
                }
                .toMap()

            return AppNavigationState(
                initialTab = selectedTab,
                initialBackStacks = restoredBackStacks,
            )
        }

        private const val SAVED_DESTINATION_SEPARATOR = "|"

        private fun decodeBackStack(
            encodedStack: String?,
            fallback: AppDestination,
        ): List<AppDestination> {
            return encodedStack
                ?.split(SAVED_DESTINATION_SEPARATOR)
                ?.mapNotNull(AppDestination::fromLegacyRoute)
                ?.ifEmpty { listOf(fallback) }
                ?: listOf(fallback)
        }
    }
}

@Composable
fun rememberAppNavigationState(): AppNavigationState {
    return rememberSaveable(saver = AppNavigationState.Saver) { AppNavigationState() }
}

private fun defaultBackStacks(): Map<TopLevelDestination, List<AppDestination>> {
    return TopLevelDestination.entries.associateWith { listOf(it.root) }
}

private fun navBackStackOf(destinations: List<AppDestination>): NavBackStack<AppDestination> {
    val backStack = NavBackStack<AppDestination>()
    backStack.addAll(destinations)
    return backStack
}
