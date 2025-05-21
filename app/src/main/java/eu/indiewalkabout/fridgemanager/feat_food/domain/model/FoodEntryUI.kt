package eu.indiewalkabout.fridgemanager.feat_food.domain.model

import java.time.LocalDate
import java.util.Date

    data class FoodEntryUI(
        var name: String? = null,
        var expiringAtLocalDate: LocalDate? = null,
        var expiringAtUI: String? = null,
        var quantity: Int = 1,
        var done: Int = 0,
    )