package eu.indiewalkabout.fridgemanager.reminder

import android.content.Context

import com.firebase.jobdispatcher.Constraint
import com.firebase.jobdispatcher.Driver
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.Job
import com.firebase.jobdispatcher.Lifetime
import com.firebase.jobdispatcher.Trigger

import java.util.concurrent.TimeUnit

import eu.indiewalkabout.fridgemanager.util.PreferenceUtility

object ReminderScheduler {

    // Hours interval at which remind the user about food expiring
    // private static final int periodicity         = (int) (TimeUnit.MINUTES.toSeconds(3600));
    // private static final int toleranceInterval   = (int) (TimeUnit.MINUTES.toSeconds(60));
    private var periodicity: Int = 0
    private var toleranceInterval: Int = 0

    private val REMINDER_JOB_TAG = "food_reminder_tag"

    private var sInitialized: Boolean = false

    @Synchronized
    fun scheduleChargingReminder(context: Context) {

        if (sInitialized) return

        // get frequency of daily reminder from preferences
        val hoursFrequency = PreferenceUtility.getHoursCount(context)
        periodicity = TimeUnit.HOURS.toSeconds(hoursFrequency.toLong()).toInt()
        toleranceInterval = periodicity

        val driver = GooglePlayDriver(context)
        val dispatcher = FirebaseJobDispatcher(driver)


        val launcheReminderNotificationJob = dispatcher.newJobBuilder()
                .setService(FoodReminder_fbjob::class.java)
                .setTag(REMINDER_JOB_TAG)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        periodicity,
                        periodicity + toleranceInterval))
                .setReplaceCurrent(true)
                .build()

        /* Schedule the Job with the dispatcher */
        dispatcher.schedule(launcheReminderNotificationJob)

        /* The job has been initialized */
        sInitialized = true
    }
}
