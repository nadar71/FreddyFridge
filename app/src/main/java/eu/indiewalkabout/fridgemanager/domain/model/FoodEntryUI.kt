package eu.indiewalkabout.fridgemanager.domain.model

import java.util.Date

data class FoodEntryUI(
    var name: String? = null,
    var expiringAt: Date? = null,
    var done: Int = 0,
)