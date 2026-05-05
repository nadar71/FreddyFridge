package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import java.time.LocalDate

data class FoodUpdateSubmission(
    val entryToUpdate: FoodEntry,
    val additionalEntries: List<FoodEntry>,
)

object FoodEntrySubmissionBuilder {

    fun buildInsertEntries(
        name: String,
        expiringAt: LocalDate?,
        timezoneId: String,
        quantity: Int,
    ): List<FoodEntry> {
        val normalizedQuantity = quantity.coerceAtLeast(1)
        return List(normalizedQuantity) {
            FoodEntry(
                name = name,
                expiringAt = expiringAt,
                timezoneId = timezoneId,
            )
        }
    }

    fun buildUpdateSubmission(
        originalEntry: FoodEntry,
        updatedName: String,
        updatedExpiringAt: LocalDate?,
        timezoneId: String,
        quantity: Int,
    ): FoodUpdateSubmission {
        val normalizedQuantity = quantity.coerceAtLeast(1)
        val updatedEntry = originalEntry.copy(
            name = updatedName,
            expiringAt = updatedExpiringAt,
            timezoneId = timezoneId,
        )

        val additionalEntries = if (normalizedQuantity == 1) {
            emptyList()
        } else {
            List(normalizedQuantity - 1) {
                FoodEntry(
                    name = updatedName,
                    expiringAt = updatedExpiringAt,
                    consumedAt = originalEntry.consumedAt,
                    timezoneId = timezoneId,
                )
            }
        }

        return FoodUpdateSubmission(
            entryToUpdate = updatedEntry,
            additionalEntries = additionalEntries,
        )
    }
}
