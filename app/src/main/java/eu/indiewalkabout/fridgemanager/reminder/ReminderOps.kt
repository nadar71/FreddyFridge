package eu.indiewalkabout.fridgemanager.reminder

import android.content.Context

import eu.indiewalkabout.fridgemanager.data.FoodEntry
import eu.indiewalkabout.fridgemanager.util.NotificationsUtility

/*
Static class for executing different type of actions about notifications
*/

// TODO : can be removed calling directly the notifications method
object ReminderOps {

    val ACTION_REMIND_NEXT_DAYS_EXPIRING_FOOD = "notify-next-days-expiring-food"
    val ACTION_REMIND_TODAY_EXPIRING_FOOD = "notify-today-expiring-food"
    val ACTION_DISMISS_NOTIFICATION = "dismiss-notification"

    fun executeTask(context: Context, action: String, foodEntries: List<FoodEntry>?) {
        if (ACTION_DISMISS_NOTIFICATION == action) {
            NotificationsUtility.clearAllNotifications(context)

        } else if (ACTION_REMIND_NEXT_DAYS_EXPIRING_FOOD == action) {
            foodEntries?.let { NotificationsUtility.remindNextDaysExpiringFood(context, it) }

        } else if (ACTION_REMIND_TODAY_EXPIRING_FOOD == action) {
            foodEntries?.let { NotificationsUtility.remindTodayExpiringFood(context, it) }
        }
    }

    // update preferences if needed here
    private fun updatePreferences(context: Context) {}


}
