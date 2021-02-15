package eu.indiewalkabout.fridgemanager.reminder

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import eu.indiewalkabout.fridgemanager.App
import eu.indiewalkabout.fridgemanager.util.DateUtility
import eu.indiewalkabout.fridgemanager.util.NotificationsUtility
import eu.indiewalkabout.fridgemanager.util.PreferenceUtility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


/*
Every time the scheduler ReminderScheduler activate this worker:
- check if a daily notification is needed for food expiring in x days
- check if a hourly notification is needed for food expiring today (every x hours)
*/
class FoodReminderWorker (appContext: Context, params: WorkerParameters) :
        Worker(appContext, params) {

    companion object {
        val TAG = FoodReminderWorker::class.java.simpleName
    }

    // Check the food entries in db and in case activate the specific reminder
    override fun doWork(): Result {

        val context = applicationContext

        // time scheduling
        Log.i(TAG, "Workmanager, doWork: FoodReminderWorker activated by scheduler!")
        // * hardcoded for debug, next in preferences key :
        // day before in millisec
        // 2 days = 172800000
        // final int DAYS_BEFORE = (int) (TimeUnit.DAYS.toSeconds(2))*1000;

        // for real :
        val days = PreferenceUtility.getDaysCount(context)
        val DAYS_BEFORE = TimeUnit.DAYS.toSeconds(days.toLong()).toInt()

        // -----------------------------------------------------------------------------------------
        // 1 - check for food expiring in the next x days (DAYS_BEFORE), and show notification in case
        // TODO : a user wants a notification for expiring food DAYS_BEFORE food expiring day,
        //        so the val dateBefore = dataNormalizedAtMidnight - DAYS_BEFORE couldn't be :
        //        val dateBefore = dataNormalizedAtMidnight + DAYS_BEFORE ? now fixed, must check


        val dataNormalizedAtMidnight = DateUtility.getLocalMidnightFromNormalizedUtcDate(DateUtility.normalizedUtcMsForToday)
        val expiringDateToBeNotified = dataNormalizedAtMidnight + DAYS_BEFORE
        // 1549926000000 - 172800000 = 1549753200000

        // get repository
        val repository = (App.getsContext() as App).repository


        CoroutineScope(IO).launch {
            Log.i(TAG, "Workmanager, doWork: check food expiring in the next days")
            val foodEntriesNextDays = repository!!.loadAllFoodExpiring_no_livedata(expiringDateToBeNotified)
            foodEntriesNextDays.let {
                if (foodEntriesNextDays.size > 0) {
                    NotificationsUtility.remindNextDaysExpiringFood(context, it)
                    Log.i(TAG, "Workmanager, doWork: food expiring in the NEXT DAYS, notification sent")
                }

            }
        }




        // -----------------------------------------------------------------------------------------
        // 2 - check for food expiring today, and show notification in case

        val previousDayDate = dataNormalizedAtMidnight - DateUtility.DAY_IN_MILLIS
        val nextDayDate = dataNormalizedAtMidnight + DateUtility.DAY_IN_MILLIS


        CoroutineScope(IO).launch {
            Log.i(TAG, "Workmanager, doWork: check food expiring in today")
            val foodEntriesToDay = repository!!.loadFoodExpiringToday_no_livedata(previousDayDate, nextDayDate)

            foodEntriesToDay.let {
                if (foodEntriesToDay.size > 0) {
                    NotificationsUtility.remindTodayExpiringFood(context, it)
                    Log.i(TAG, "Workmanager, doWork: check food expiring  TODAY, notification sent")
                }
            }
        }


        return Result.success()
    }

}

