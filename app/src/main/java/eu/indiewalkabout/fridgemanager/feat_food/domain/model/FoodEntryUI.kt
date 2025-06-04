package eu.indiewalkabout.fridgemanager.feat_food.domain.model

import java.time.LocalDate
import java.util.Date

    data class FoodEntryUI(
        var id: Int,
        var name: String? = null,
        var expiringAtLocalDate: LocalDate? = null,
        var expiringAtUI: String? = null,
        var consumedAtLocalDate: LocalDate? = null,
        var consumedAtUI: String? = null,
        var quantity: Int = 1,
        var timezone: String? = null,
        var done: Int = 0,
    )


fun FoodEntryUI.toFoodEntry(): FoodEntry {
    return FoodEntry (
        id = id,
        name = name,
        expiringAt = expiringAtLocalDate,
        consumedAt = consumedAtLocalDate,
        timezone = timezone,
        quantity = quantity,
        done = done,
    )
}
