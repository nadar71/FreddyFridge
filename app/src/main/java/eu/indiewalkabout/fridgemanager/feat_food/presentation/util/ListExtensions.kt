package eu.indiewalkabout.fridgemanager.feat_food.presentation.util

import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import kotlin.collections.partition


// Add this extension function to sort the list with open products first
fun List<FoodEntry>.sortedByOpenStatus(): List<FoodEntry> {
    return this.partition { it.isProductOpen }
        .toList()
        .let { (openProducts, otherProducts) ->
            openProducts + otherProducts
        }
}