package eu.indiewalkabout.fridgemanager.reminder

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.Worker
import androidx.work.WorkerParameters
import eu.indiewalkabout.fridgemanager.SingletonProvider
import eu.indiewalkabout.fridgemanager.data.FoodEntry
import eu.indiewalkabout.fridgemanager.util.DateUtility
import eu.indiewalkabout.fridgemanager.util.PreferenceUtility
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

        val foodEntriesNextDays: LiveData<MutableList<FoodEntry>>

        val dataNormalizedAtMidnight = DateUtility.getLocalMidnightFromNormalizedUtcDate(DateUtility.normalizedUtcMsForToday)
        val expiringDateToBeNotified = dataNormalizedAtMidnight + DAYS_BEFORE
        // 1549926000000 - 172800000 = 1549753200000

        // TODO : make this retrieving data from db with coroutine
        // get repository
        val repository = (SingletonProvider.getsContext() as SingletonProvider).repository
        foodEntriesNextDays = repository!!.loadAllFoodExpiring(expiringDateToBeNotified)

        foodEntriesNextDays.observeForever(object : Observer<MutableList<FoodEntry>> {
            override fun onChanged(foodEntries: MutableList<FoodEntry>?) {
                if (foodEntries!!.size > 0) {
                    ReminderOps.executeTask(context, ReminderOps.ACTION_REMIND_NEXT_DAYS_EXPIRING_FOOD, foodEntries)
                }
                foodEntriesNextDays.removeObserver(this)
            }
        })


        // -----------------------------------------------------------------------------------------
        // 2 - check for food expiring today, and show notification in case

        val foodEntriesToDay: LiveData<MutableList<FoodEntry>>

        val previousDayDate = dataNormalizedAtMidnight - DateUtility.DAY_IN_MILLIS
        val nextDayDate = dataNormalizedAtMidnight + DateUtility.DAY_IN_MILLIS

        // TODO : make this retrieving data from db in a different way
        foodEntriesToDay = repository.loadFoodExpiringToday(previousDayDate, nextDayDate)

        foodEntriesToDay.observeForever(object : Observer<MutableList<FoodEntry>> {
            override fun onChanged(foodEntries: MutableList<FoodEntry>?) {
                if (foodEntries!!.size > 0) {
                    ReminderOps.executeTask(context, ReminderOps.ACTION_REMIND_TODAY_EXPIRING_FOOD, foodEntries)
                }
                foodEntriesToDay.removeObserver(this)
            }
        })

        return Result.success()
    }

}

