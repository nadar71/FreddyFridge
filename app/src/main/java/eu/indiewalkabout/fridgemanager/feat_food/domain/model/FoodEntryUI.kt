package eu.indiewalkabout.fridgemanager.feat_food.domain.model

import java.util.Date

data class FoodEntryUI(
    var name: String? = null,
    var expiringAt: Date? = null,
    var done: Int = 0,
)