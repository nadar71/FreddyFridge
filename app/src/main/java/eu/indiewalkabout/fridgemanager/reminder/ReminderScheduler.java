package eu.indiewalkabout.fridgemanager.reminder;

import android.content.Context;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

import eu.indiewalkabout.fridgemanager.util.PreferenceUtility;

public class ReminderScheduler {

    // Hours interval at which remind the user about food expiring
    // private static final int periodicity         = (int) (TimeUnit.MINUTES.toSeconds(3600));
    // private static final int toleranceInterval   = (int) (TimeUnit.MINUTES.toSeconds(60));
    private static int periodicity         ;
    private static int toleranceInterval   ;

    private static final String REMINDER_JOB_TAG = "food_reminder_tag";

    private static boolean sInitialized;

    synchronized public static void scheduleChargingReminder(@NonNull final Context context) {

        if (sInitialized) return;

        // get frequency of daily reminder from preferences
        int hoursFrequency  = PreferenceUtility.getHoursCount(context);
        periodicity         = (int) (TimeUnit.HOURS.toSeconds(hoursFrequency))/60;
        toleranceInterval   = periodicity;

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);


        Job launcheReminderNotificationJob = dispatcher.newJobBuilder()
                // todo : change service method name
                .setService(FoodReminder_fbjob.class)
                .setTag(REMINDER_JOB_TAG)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        periodicity,
                        periodicity + toleranceInterval))
                .setReplaceCurrent(true)
                .build();

        /* Schedule the Job with the dispatcher */
        dispatcher.schedule(launcheReminderNotificationJob);

        /* The job has been initialized */
        sInitialized = true;
    }
}
