package eu.indiewalkabout.fridgemanager.reminder

import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import java.util.concurrent.TimeUnit

import eu.indiewalkabout.fridgemanager.SingletonProvider
import eu.indiewalkabout.fridgemanager.data.FoodEntry
import eu.indiewalkabout.fridgemanager.util.DateUtility
import eu.indiewalkabout.fridgemanager.util.PreferenceUtility

class FoodReminder_fbjob : JobService() {


    override fun onStartJob(params: JobParameters): Boolean {

        val context = this@FoodReminder_fbjob

        // hardcoded for debug, next in preferences key
        // day before in millisec
        // 2 days = 172800000
        // final int DAYS_BEFORE = (int) (TimeUnit.DAYS.toSeconds(2))*1000;
        val days = PreferenceUtility.getDaysCount(context)
        val DAYS_BEFORE = TimeUnit.DAYS.toSeconds(days.toLong()).toInt()

        // -----------------------------------------------------------------------------------------
        // 1 - check for food expiring in the next days
        // -----------------------------------------------------------------------------------------
        val foodEntriesNextDays: LiveData<List<FoodEntry>>

        val dataNormalizedAtMidnight = DateUtility.getLocalMidnightFromNormalizedUtcDate(DateUtility.normalizedUtcMsForToday)
        val dateBefore = dataNormalizedAtMidnight - DAYS_BEFORE
        // 1549926000000 - 172800000 = 1549753200000

        // get repository
        val repository = (SingletonProvider.getsContext() as SingletonProvider).repository
        foodEntriesNextDays = repository!!.loadAllFoodExpiring(dateBefore)

        foodEntriesNextDays.observeForever(object : Observer<List<FoodEntry>> {
            override fun onChanged(foodEntries: List<FoodEntry>?) {
                if (foodEntries!!.size > 0) {
                    ReminderOps.executeTask(context, ReminderOps.ACTION_REMIND_NEXT_DAYS_EXPIRING_FOOD, foodEntries)
                }
                foodEntriesNextDays.removeObserver(this)
            }
        })


        // -----------------------------------------------------------------------------------------
        // 2 - check for food expiring today
        // -----------------------------------------------------------------------------------------
        val foodEntriesToDay: LiveData<List<FoodEntry>>

        val previousDayDate = dataNormalizedAtMidnight - DateUtility.DAY_IN_MILLIS
        val nextDayDate = dataNormalizedAtMidnight + DateUtility.DAY_IN_MILLIS


        foodEntriesToDay = repository.loadFoodExpiringToday(previousDayDate, nextDayDate)

        foodEntriesToDay.observeForever(object : Observer<List<FoodEntry>> {
            override fun onChanged(foodEntries: List<FoodEntry>?) {
                if (foodEntries!!.size > 0) {
                    ReminderOps.executeTask(context, ReminderOps.ACTION_REMIND_TODAY_EXPIRING_FOOD, foodEntries)
                }
                foodEntriesToDay.removeObserver(this)
            }
        })



        return true
    }


    override fun onStopJob(params: JobParameters): Boolean {
        // if (bgReminderTask != null) bgReminderTask.cancel(true);
        return true
    }

    companion object {
        // private AsyncTask bgReminderTask;

        val TAG = FoodReminder_fbjob::class.java.simpleName
    }


}
