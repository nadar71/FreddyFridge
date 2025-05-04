package eu.indiewalkabout.fridgemanager.feat_navigation.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import eu.indiewalkabout.fridgemanager.R // Make sure this points to your resources

sealed class BottomNavItem(
    val route: String,
    @DrawableRes val icon: Int,
    @StringRes val label: Int,
    @StringRes val contentDescription: Int // Added for accessibility
) {
    object Expired : BottomNavItem(
        route = "food_expired",
        icon = R.drawable.ic_ghost, // Replace with your actual icon resources
        label = R.string.menu_expired_label_item,
        contentDescription = R.string.content_menu_expired_label_item
    )

    object Consumed : BottomNavItem(
        route = "food_consumed",
        icon = R.drawable.ic_done_all_white,
        label = R.string.menu_consumed_label_item,
        contentDescription = R.string.content_menu_consumed_label_item
    )

    object Expiring : BottomNavItem(
        route = "food_expiring",
        icon = R.drawable.ic_hourglass_empty_white,
        label = R.string.menu_expiring_label_item,
        contentDescription = R.string.content_menu_expiring_label_item
    )

    object Add : BottomNavItem(
        route = "insert_food",
        icon = R.drawable.ic_add_box_white,
        label = R.string.menu_add_label_item,
        contentDescription = R.string.content_menu_add_label_item
    )

    // You can also include other top-level destinations that might not be in the bottom nav
    object MainScreen : BottomNavItem(
        route = "main_screen",
        icon = 0, // Not applicable for this destination
        label = 0,
        contentDescription = 0
    )

    object SettingsScreen : BottomNavItem(
        route = "settings_screen",
        icon = 0, // Not applicable for this destination
        label = 0,
        contentDescription = 0
    )

    object CreditsScreen : BottomNavItem(
        route = "credits_screen",
        icon = 0,
        label = 0,
        contentDescription = 0
    )
}

// List of bottom navigation items
val bottomNavItems = listOf(
    BottomNavItem.Expired,
    BottomNavItem.Consumed,
    BottomNavItem.Expiring,
    BottomNavItem.Add
)