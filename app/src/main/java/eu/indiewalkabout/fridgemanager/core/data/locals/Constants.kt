package eu.indiewalkabout.fridgemanager.core.data.locals


object Constants {
    const val APP_OPENING_COUNTER = "app-opening-counter"

    // numbers constants
    const val DEFAULT_COUNT = 0
    const val NUM_MAX_OPENINGS = 2
    const val NUM_MAX_DAYS_BEFORE_DEADLINE = 30
    const val NUM_MAX_DAILY_NOTIFICATIONS_NUMBER = 12

    const val FOOD_TYPE = "food_type"
    const val FOOD_EXPIRING = "ExpiringFood"
    const val FOOD_EXPIRING_TODAY = "ExpiringFoodToday"
    const val FOOD_CONSUMED = "SavedFood"
    const val FOOD_DEAD = "DeadFood"


    // DATE
    // val LOCAL_DATE_FORMAT = DateFormat.getDateInstance(DateFormat.DEFAULT).toString()
    var LOCAL_DATE_FORMAT = ""
    const val DEFAULT_DATE_FORMAT =  "dd/MM/yyyy" // "d MMMM yyyy"

    // strings constants
    const val googleplaystore = "https://play.google.com/store/apps/details?id=eu.indiewalkabout.fridgemanager"
    const val support_email = "indie.walkabout.71@gmail.com"
    const val my_website = "http://www.indie-walkabout.eu"
}