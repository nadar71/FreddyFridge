package eu.indiewalkabout.fridgemanager.reminder;

import android.content.Context;

import java.util.List;

import eu.indiewalkabout.fridgemanager.data.FoodEntry;
import eu.indiewalkabout.fridgemanager.util.NotificationsUtility;


public class ReminderOps {

    public static final String ACTION_REMIND_NEXT_DAYS_EXPIRING_FOOD   = "notify-next-days-expiring-food";
    public static final String ACTION_REMIND_TODAY_EXPIRING_FOOD       = "notify-today-expiring-food";
    public static final String ACTION_DISMISS_NOTIFICATION             = "dismiss-notification";

    public static void executeTask(Context context, String action, List<FoodEntry> foodEntries) {
        if (ACTION_DISMISS_NOTIFICATION.equals(action)) {
            NotificationsUtility.clearAllNotifications(context);

        } else if (ACTION_REMIND_NEXT_DAYS_EXPIRING_FOOD.equals(action)){
            NotificationsUtility.remindNextDaysExpiringFood(context, foodEntries);

        } else if (ACTION_REMIND_TODAY_EXPIRING_FOOD.equals(action)){
            NotificationsUtility.remindTodayExpiringFood(context, foodEntries);
        }
    }

    // update preferences if needed here
    private static void updatePreferences(Context context) {
    }


}
