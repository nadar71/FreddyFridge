package eu.indiewalkabout.fridgemanager.reminder;

import android.content.Context;
import android.content.Intent;

import eu.indiewalkabout.fridgemanager.ui.MainActivity;
import eu.indiewalkabout.fridgemanager.util.NotificationsUtility;


public class ReminderOps {

    public static final String ACTION_SHOW_EXPIRING_FOOD   = "show-expiring-food";
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";

    public static void executeTask(Context context, String action) {
        if (ACTION_DISMISS_NOTIFICATION.equals(action)) {
            NotificationsUtility.clearAllNotifications(context);
        } else if (ACTION_SHOW_EXPIRING_FOOD.equals(action)){
            // TODO : delete if no needed
            /*
            NotificationsUtility.clearAllNotifications(context);
            Intent startActivityIntent = new Intent(context, MainActivity.class);
            context.startActivity(startActivityIntent);
            */
        }
    }

    // TODO : update preferences for notifications  here
    private static void updatePreferences(Context context) {
        // PreferenceUtilities.incrementWaterCount(context);
    }


}
