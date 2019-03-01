package eu.indiewalkabout.fridgemanager.reminder;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.indiewalkabout.fridgemanager.data.FoodDatabase;
import eu.indiewalkabout.fridgemanager.data.FoodEntry;
import eu.indiewalkabout.fridgemanager.util.DateUtility;
import eu.indiewalkabout.fridgemanager.util.PreferenceUtility;

public class FoodReminder_fbjob extends JobService {
    private AsyncTask bgReminderTask;

    public static final String TAG = FoodReminder_fbjob.class.getSimpleName();



    @Override
    public boolean onStartJob(final JobParameters params) {

        /*
        // debug
        bgReminderTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Context context = FoodReminder_fbjob.this;

                ReminderOps.executeTask(context, ReminderOps.ACTION_REMIND_NEXT_DAYS_EXPIRING_FOOD);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(params, false);
            }
        };
        bgReminderTask.execute();
        */


        final Context context = FoodReminder_fbjob.this;

        // hardcoded for debug, next in preferences key
        // day before in millisec
        // 2 days = 172800000
        // final int DAYS_BEFORE = (int) (TimeUnit.DAYS.toSeconds(2))*1000;
        int days = PreferenceUtility.getDaysCount(context);
        final int DAYS_BEFORE = (int) (TimeUnit.DAYS.toSeconds(days));

        // Db reference
        // TODO : delete when depository active
        FoodDatabase foodDb;
        foodDb = FoodDatabase.getsDbInstance(getApplicationContext());

        // TODO : inject and use the repository when created, now query directly db, we are in background.
        // follow here https://stackoverflow.com/questions/45932995/observe-livedata-from-jobservice
        // get the getLiveDataFromSomewhere() from repository injected, instead of
        // foodEntriesNextDays = foodDb.foodDbDao().loadAllFoodExpiring(dataNormalizedAtMidnight);

        // -----------------------------------------------------------------------------------------
        // 1 - check for food expiring in the next days
        // -----------------------------------------------------------------------------------------
        final LiveData<List<FoodEntry>> foodEntriesNextDays;

        long dataNormalizedAtMidnight  =
                DateUtility.getLocalMidnightFromNormalizedUtcDate(DateUtility.getNormalizedUtcMsForToday());
        long dateBefore = dataNormalizedAtMidnight - DAYS_BEFORE;
        // 1549926000000 - 172800000 = 1549753200000

        // TODO : MAKE WITH LIVEDATA/VIEWMODEL
        foodEntriesNextDays = foodDb.foodDbDao().loadAllFoodExpiring(dateBefore);

        foodEntriesNextDays.observeForever(new Observer<List<FoodEntry>>() {
            @Override
            public void onChanged(@Nullable List<FoodEntry> foodEntries) {
                if (foodEntries.size() > 0 ){
                    ReminderOps.executeTask(context, ReminderOps.ACTION_REMIND_NEXT_DAYS_EXPIRING_FOOD,foodEntries);
                }
                foodEntriesNextDays.removeObserver(this);
            }
        });


        // -----------------------------------------------------------------------------------------
        // 2 - check for food expiring today
        // -----------------------------------------------------------------------------------------
        final LiveData<List<FoodEntry>> foodEntriesToDay;

        long previousDayDate = dataNormalizedAtMidnight - DateUtility.DAY_IN_MILLIS;
        long nextDayDate     = dataNormalizedAtMidnight + DateUtility.DAY_IN_MILLIS;

        // TODO : MAKE WITH LIVEDATA/VIEWMODEL
        foodEntriesToDay = foodDb.foodDbDao().loadFoodExpiringToday(previousDayDate,nextDayDate);

        foodEntriesToDay.observeForever(new Observer<List<FoodEntry>>() {
            @Override
            public void onChanged(@Nullable List<FoodEntry> foodEntries) {
                if (foodEntries.size() > 0 ){
                    ReminderOps.executeTask(context, ReminderOps.ACTION_REMIND_TODAY_EXPIRING_FOOD,foodEntries);
                }
                foodEntriesToDay.removeObserver(this);
            }
        });



        return true;
    }



    @Override
    public boolean onStopJob(JobParameters params) {
        if (bgReminderTask != null) bgReminderTask.cancel(true);
        return true;
    }



}
