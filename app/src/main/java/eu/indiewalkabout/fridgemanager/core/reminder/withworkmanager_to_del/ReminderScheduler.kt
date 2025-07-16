package eu.indiewalkabout.fridgemanager.core.reminder.withworkmanager_to_del

import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import eu.indiewalkabout.fridgemanager.core.data.locals.AppPreferences
import eu.indiewalkabout.fridgemanager.core.util.extensions.TAG
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    // Hours interval at which remind the user about food expiring
    // private static final int periodicity         = (int) (TimeUnit.MINUTES.toSeconds(3600));
    // private static final int toleranceInterval   = (int) (TimeUnit.MINUTES.toSeconds(60));
    private var periodicity: Long = 0
    private val REMINDER_JOB_TAG = "food_reminder_tag"


    @Synchronized
    fun scheduleChargingReminder(context: Context) {

        // get frequency of daily reminder from preferences
        val hoursFrequency = AppPreferences.daily_notifications_number // PreferenceUtility.getHoursCount(context)
        // TODO : now is the number of notitifcations: must divide and calculate  frequency
        periodicity = TimeUnit.HOURS.toSeconds(hoursFrequency.toLong())

        // TODO : comment debug frequency BEFORE RELEASE!!
        // periodicity = 60*15


        val request = PeriodicWorkRequestBuilder<FoodReminderWorker>(
                periodicity,TimeUnit.SECONDS,
                15, TimeUnit.MINUTES)
                .build()

        WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork(REMINDER_JOB_TAG, ExistingPeriodicWorkPolicy.KEEP, request)

        Log.i(TAG, "Workmanager, scheduling every seconds : $periodicity")

    }
}
