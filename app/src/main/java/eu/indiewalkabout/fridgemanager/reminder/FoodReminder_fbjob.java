package eu.indiewalkabout.fridgemanager.reminder;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;


import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.indiewalkabout.fridgemanager.SingletonProvider;
import eu.indiewalkabout.fridgemanager.FridgeManagerRepository;
import eu.indiewalkabout.fridgemanager.data.FoodEntry;
import eu.indiewalkabout.fridgemanager.util.DateUtility;
import eu.indiewalkabout.fridgemanager.util.PreferenceUtility;

public class FoodReminder_fbjob extends JobService {
    // private AsyncTask bgReminderTask;

    public static final String TAG = FoodReminder_fbjob.class.getSimpleName();


    @Override
    public boolean onStartJob(final JobParameters params) {

        final Context context = FoodReminder_fbjob.this;

        // hardcoded for debug, next in preferences key
        // day before in millisec
        // 2 days = 172800000
        // final int DAYS_BEFORE = (int) (TimeUnit.DAYS.toSeconds(2))*1000;
        int days = PreferenceUtility.INSTANCE.getDaysCount(context);
        final int DAYS_BEFORE = (int) (TimeUnit.DAYS.toSeconds(days));

        // -----------------------------------------------------------------------------------------
        // 1 - check for food expiring in the next days
        // -----------------------------------------------------------------------------------------
        final LiveData<List<FoodEntry>> foodEntriesNextDays;

        long dataNormalizedAtMidnight  =
                DateUtility.INSTANCE.getLocalMidnightFromNormalizedUtcDate(DateUtility.INSTANCE.getNormalizedUtcMsForToday());
        long dateBefore = dataNormalizedAtMidnight - DAYS_BEFORE;
        // 1549926000000 - 172800000 = 1549753200000

        // get repository
        FridgeManagerRepository repository = ((SingletonProvider) SingletonProvider.Companion.getsContext()).getRepository();
        foodEntriesNextDays = repository.loadAllFoodExpiring(dateBefore);

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

        long previousDayDate = dataNormalizedAtMidnight - DateUtility.INSTANCE.getDAY_IN_MILLIS();
        long nextDayDate     = dataNormalizedAtMidnight + DateUtility.INSTANCE.getDAY_IN_MILLIS();


        foodEntriesToDay = repository.loadFoodExpiringToday(previousDayDate,nextDayDate);

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
        // if (bgReminderTask != null) bgReminderTask.cancel(true);
        return true;
    }



}
