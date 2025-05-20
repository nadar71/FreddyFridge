package eu.indiewalkabout.fridgemanager.feat_food.domain.model

import java.util.Date

data class FoodEntryUI(
    var name: String? = null,
    var expiringAt: String? = null,
    var quantity: Int = 1,
    var done: Int = 0,
)