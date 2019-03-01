package eu.indiewalkabout.fridgemanager.reminder;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import eu.indiewalkabout.fridgemanager.data.FoodEntry;


/**
 * -------------------------------------------------------------------------------------------------
 * Handle requested action to background service
 * -------------------------------------------------------------------------------------------------
 */
public class FoodReminderIntentService extends IntentService {

 public FoodReminderIntentService() {
     super("FoodReminderIntentService");
 }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        ReminderOps.executeTask(this, action,null);
    }
}
