package eu.indiewalkabout.fridgemanager.feat_navigation.presentation.components

import android.R.attr.label
import android.R.attr.onClick
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_12
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_14
import eu.indiewalkabout.fridgemanager.feat_navigation.domain.navigation.AppDestinationRoutes
import eu.indiewalkabout.fridgemanager.feat_navigation.domain.navigation.AppNavigation.navigate


@Composable
fun BottomNavigationBar(itemLabelSelected: String) {
    val colors = LocalAppColors.current
    var activeIndex = 3

    NavigationBar(containerColor = colors.primaryColor) {
        when(itemLabelSelected) {
            stringResource(R.string.menu_expired_label_item) -> {
                activeIndex = 1
            }
            stringResource(R.string.menu_consumed_label_item) -> {
                activeIndex = 2
            }
            stringResource(R.string.menu_home_item) -> {
                activeIndex = 3
            }
            stringResource(R.string.menu_expiring_label_item) -> {
                activeIndex = 4
            }
            stringResource(R.string.menu_add_label_item) -> {
                activeIndex = 5
            }
        }

        NavigationBarItem(
            selected = false,
            onClick = { navigate(AppDestinationRoutes.FoodExpiredScreen.route) },
            icon = {
                Icon(
                painter = painterResource(id = R.drawable.ic_ghost),
                contentDescription = stringResource(R.string.content_menu_expired_label_item),
                tint = if (activeIndex == 1) colors.iconColor else colors.lightGreyVeryTransparent
                )
                   },
            label = { Text(
                text = stringResource(R.string.menu_expired_label_item),
                style = text_12(
                    if (activeIndex == 1) colors.colorText else colors.lightGreyVeryTransparent
                )
            ) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navigate(AppDestinationRoutes.FoodConsumedScreen.route) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_done_all_white),
                    contentDescription = stringResource(R.string.content_menu_consumed_label_item),
                    tint = if (activeIndex == 2) colors.iconColor else colors.lightGreyVeryTransparent
                )
            },
            label = { Text(
                text = stringResource(R.string.menu_consumed_label_item),
                style = text_12(
                    if (activeIndex == 2) colors.colorText else colors.lightGreyVeryTransparent
                )
            ) }
        )


        NavigationBarItem(
            selected = false,
            onClick = { navigate(AppDestinationRoutes.MainScreen.route) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home_white_24dp),
                    contentDescription = stringResource(R.string.menu_home_item),
                    tint = if (activeIndex == 3) colors.iconColor else colors.lightGreyVeryTransparent
                )
            },
            label = { Text(
                text = stringResource(R.string.menu_home_item),
                style = text_12(
                    if (activeIndex == 3) colors.colorText else colors.lightGreyVeryTransparent
                )
            ) }
        )


        NavigationBarItem(
            selected = false,
            onClick = { navigate(AppDestinationRoutes.FoodExpiringScreen.route) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_hourglass_empty_white),
                    contentDescription = stringResource(R.string.content_menu_expiring_label_item),
                    tint = if (activeIndex == 4) colors.iconColor else colors.lightGreyVeryTransparent
                )
            },
            label = { Text(
                text = stringResource(R.string.menu_expiring_label_item),
                style = text_12(
                    if (activeIndex == 4) colors.colorText else colors.lightGreyVeryTransparent
                )
            ) }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navigate(AppDestinationRoutes.InsertFoodScreen.route) },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_box_white),
                    contentDescription = stringResource(R.string.content_menu_add_label_item),
                    tint = if (activeIndex == 5) colors.iconColor else colors.lightGreyVeryTransparent
                )
            },
            label = { Text(
                text = stringResource(R.string.menu_add_label_item),
                style = text_12(
                    if (activeIndex == 5) colors.colorText else colors.lightGreyVeryTransparent
                )
            ) }
        )
    }
}



/*@Composable
fun BottomNavigationBar(navController: NavController) {
    val colors = LocalAppColors.current
    val selectedColor = colors.brown
    val unselectedColor = colors.colorText

    // current back stack entry state to determine the current destination
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = colors.primaryColor) {
        bottomNavItems.forEach { item -> // Iterate through BottomNavItem objects
            // Check this item is currently selected by comparing routes
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected, // Set selected based on the current route
                onClick = {
                    // Navigate only if the current route is not the item's route
                    // This prevents unnecessary navigation and recomposition
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Configure navigation options for Bottom Navigation
                            // Pop up to the start destination to avoid building up a large back stack
                            // when switching between bottom nav tabs.
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true // Save the state of the screens being popped
                            }
                            // Avoid creating multiple copies of the same destination on the stack
                            // when reselecting an already selected item.
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            // e.g., scroll position in a list
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = stringResource(id = item.contentDescription)
                    )
                },
                label = {
                    Text(text = stringResource(id = item.label))
                },
                colors = NavigationBarItemDefaults.colors(
                    // Customize colors based on selection state using the defined colors
                    selectedIconColor = selectedColor,
                    selectedTextColor = selectedColor,
                    unselectedIconColor = unselectedColor,
                    unselectedTextColor = unselectedColor,
                    indicatorColor = colors.primaryColor // Set indicator color, e.g., to match container or transparent
                )
            )
        }
    }
}*/


// --- Preview Composable ---
/*@Preview(showBackground = true)
@Composable
fun PreviewBottomNavigationBar() {
    // Create a TestNavHostController for the preview
    val testNavController = remember { androidx.navigation.testing.TestNavHostController(LocalContext.current) }

    // You need to set up a basic graph for the TestNavHostController
    // to simulate having destinations.
    testNavController.graph = testNavController.createGraph(
        startDestination = bottomNavItems[0].route // Set a starting destination for the preview
    ) {
        // Add a destination for each item in your bottom navigation
        bottomNavItems.forEach { item ->
            // In a real graph, you'd have a full screen composable here,
            // but for the bottom nav preview, just defining the route is enough
            // for the NavController to know about the destination.
            composable(item.route) { *//* Actual Screen Composable goes here in the real graph *//* }
        }
        // Add other top-level destinations if needed for popUpTo
        // For example, if your startDestinationId for popUpTo is different
        composable(BottomNavItem.MainScreen.route) { *//* ... *//* }
        composable(BottomNavItem.SettingsScreen.route) { *//* ... *//* }
    }

    // Wrap your BottomNavigationBar in your app's Theme
    FreddyFridgeTheme { // Replace FreddyFridgeTheme with your actual app theme
        BottomNavigationBar(navController = testNavController)
    }
}*/

// You might need a dummy composable for the theme and LocalAppColors in the preview
// if they are not available by default.
// @Composable
// fun MyDummyTheme(content: @Composable () -> Unit) {
//     CompositionLocalProvider(LocalAppColors provides MyMockAppColors) {
//         content()
//     }
// }
// object MyMockAppColors : AppColors {
//     override val primaryColor: Color = Color.Blue
//     override val brown: Color = Color.Brown
//     override val colorText: Color = Color.White
//     // Define other colors needed for the preview
// }