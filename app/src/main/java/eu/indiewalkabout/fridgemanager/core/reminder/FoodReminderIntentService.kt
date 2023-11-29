package eu.indiewalkabout.fridgemanager.core.reminder

import android.app.IntentService
import android.content.Intent


// Handle requested action to background service
// Used by NotificationsUtility for ignore notification action by user,in the 2 types of notifications:
// - remindNextDaysExpiringFood
// - remindTodayExpiringFood
class FoodReminderIntentService : IntentService("FoodReminderIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        val action = intent!!.action
        ReminderOps.executeTask(this, action!!, null)
    }
}
