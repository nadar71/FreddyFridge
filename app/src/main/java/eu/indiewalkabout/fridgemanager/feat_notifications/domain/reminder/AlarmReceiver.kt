package eu.indiewalkabout.fridgemanager.feat_notifications.domain.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import eu.indiewalkabout.fridgemanager.core.data.locals.AppPreferences
import eu.indiewalkabout.fridgemanager.core.util.DateUtility
import eu.indiewalkabout.fridgemanager.feat_notifications.util.NotificationsUtility
import eu.indiewalkabout.fridgemanager.core.util.extensions.TAG
import eu.indiewalkabout.fridgemanager.feat_food.domain.repository.FridgeManagerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver @Inject constructor() : BroadcastReceiver() {

    @Inject
    lateinit var repository: FridgeManagerRepository

    // for real :
    private val days = AppPreferences.days_before_deadline // PreferenceUtility.getDaysCount(context)
    private val DAYS_BEFORE_IN_MILLIS = TimeUnit.DAYS.toMillis(days.toLong()) // to be used with timestamp which is in millisec

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, " AlarmReceiver : alarm RECEIVED, executing ...")

        // -----------------------------------------------------------------------------------------
        // 1 - check for food expiring in the next x days (DAYS_BEFORE), and show notification in case
        val dateNormalizedAtMidnight = DateUtility
            .getLocalMidnightFromNormalizedUtcDate(DateUtility.normalizedUtcMsForToday)
        val expiringDateToBeNotified = dateNormalizedAtMidnight + DAYS_BEFORE_IN_MILLIS
        CoroutineScope(Dispatchers.IO).launch {
            Log.i(TAG, "AlarmReceiver : check food expiring in the next days")
            val foodEntriesNextDays = repository.loadAllFoodExpiring(expiringDateToBeNotified)
            foodEntriesNextDays.let {
                if (foodEntriesNextDays.size > 0) {
                    NotificationsUtility.remindNextDaysExpiringFood(context, it)
                    Log.i(TAG, "AlarmReceiver: food expiring in the NEXT DAYS, notification sent")
                }
            }
        }



        // -----------------------------------------------------------------------------------------
        // 2 - check for food expiring today, and show notification in case
        val previousDayDate = dateNormalizedAtMidnight - DateUtility.DAY_IN_MILLIS
        val nextDayDate = dateNormalizedAtMidnight + DateUtility.DAY_IN_MILLIS
        CoroutineScope(Dispatchers.IO).launch {
            Log.i(TAG, "AlarmReceiver : check food expiring in today")
            val foodEntriesToDay = repository.loadFoodExpiringToday(previousDayDate, nextDayDate)

            foodEntriesToDay.let {
                if (foodEntriesToDay.isNotEmpty()) {
                    NotificationsUtility.remindTodayExpiringFood(context, it)
                    Log.i(TAG, "AlarmReceiver : check food expiring  TODAY, notification sent")
                }
            }
        }

    }

}
