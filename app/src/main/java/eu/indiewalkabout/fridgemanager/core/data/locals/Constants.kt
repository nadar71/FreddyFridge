package eu.indiewalkabout.fridgemanager.core.data.locals

import java.text.DateFormat

object Constants {
    const val APP_OPENING_COUNTER = "app-opening-counter"
    const val DEFAULT_COUNT = 0
    const val NUM_MAX_OPENINGS = 2

    const val FOOD_TYPE = "food_type"
    const val FOOD_EXPIRING = "ExpiringFood"
    const val FOOD_EXPIRING_TODAY = "ExpiringFoodToday"
    const val FOOD_CONSUMED = "SavedFood"
    const val FOOD_DEAD = "DeadFood"

    // DATE
    val LOCAL_DATE_FORMAT = DateFormat.getDateInstance(DateFormat.DEFAULT)
    const val DEFAULT_DATE_FORMAT =  "dd/MM/yyyy" // "d MMMM yyyy"
}