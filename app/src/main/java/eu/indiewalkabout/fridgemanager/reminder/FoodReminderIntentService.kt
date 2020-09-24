package eu.indiewalkabout.fridgemanager.reminder

import android.app.IntentService
import android.content.Intent


/**
 * -------------------------------------------------------------------------------------------------
 * Handle requested action to background service
 * -------------------------------------------------------------------------------------------------
 */
class FoodReminderIntentService : IntentService("FoodReminderIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        val action = intent!!.action
        ReminderOps.executeTask(this, action!!, null)
    }
}
