package eu.indiewalkabout.fridgemanager.reminder

import android.content.Context
import android.util.Log
import androidx.work.*

import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.Lifetime
import com.firebase.jobdispatcher.Trigger

import java.util.concurrent.TimeUnit

import eu.indiewalkabout.fridgemanager.util.PreferenceUtility

object ReminderScheduler {

    // Hours interval at which remind the user about food expiring
    // private static final int periodicity         = (int) (TimeUnit.MINUTES.toSeconds(3600));
    // private static final int toleranceInterval   = (int) (TimeUnit.MINUTES.toSeconds(60));
    private var periodicity: Long = 0
    private val REMINDER_JOB_TAG = "food_reminder_tag"
    private var sInitialized: Boolean = false

    val TAG = ReminderScheduler::class.java.simpleName



    @Synchronized
    fun scheduleChargingReminder(context: Context) {
        // if (sInitialized) return

        // get frequency of daily reminder from preferences
        val hoursFrequency = PreferenceUtility.getHoursCount(context)
        // periodicity = TimeUnit.HOURS.toSeconds(hoursFrequency.toLong())

        // debug frequency
        periodicity = 60*16

        val request :PeriodicWorkRequest = PeriodicWorkRequestBuilder<FoodReminderWorker>(periodicity, TimeUnit.SECONDS)
                        .build()

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(REMINDER_JOB_TAG, ExistingPeriodicWorkPolicy.KEEP, request)

        Log.i(TAG, "Scheduling every seconds : ${periodicity}")

        // request initialized
        // sInitialized = true
    }
}
