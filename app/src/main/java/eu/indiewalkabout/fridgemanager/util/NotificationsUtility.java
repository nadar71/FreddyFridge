package eu.indiewalkabout.fridgemanager.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.util.List;

import eu.indiewalkabout.fridgemanager.R;
import eu.indiewalkabout.fridgemanager.data.FoodDatabase;
import eu.indiewalkabout.fridgemanager.data.FoodEntry;
import eu.indiewalkabout.fridgemanager.reminder.FoodReminderIntentService;
import eu.indiewalkabout.fridgemanager.reminder.ReminderOps;
import eu.indiewalkabout.fridgemanager.ui.FoodListActivity;
import eu.indiewalkabout.fridgemanager.ui.MainActivity;

public class NotificationsUtility {

    private static final String TAG = NotificationsUtility.class.getSimpleName();

    // Unique Id to Refer to notification when displayed
    private static final int FOOD_DEADLINE_NOTIFICATION_ID         = 1000;

    // Reference to notification pendingitent
    private static final int FOOD_DEADLINE_PENDING_INTENT_ID       = 1100;

    // Unique Id to Refer to notification when displayed
    private static final int FOOD_TODAY_DEADLINE_NOTIFICATION_ID   = 2000;

    // Reference to notification pendingitent
    private static final int FOOD_TODAY_DEADLINE_PENDING_INTENT_ID = 2100;

    // This notification channel and its action
    private static final String FOOD_DEADLINE_NOTIFICATION_CHANNEL_ID = "food_deadline_notification_channel";
    private static final String FOOD_TODAY_DEADLINE_NOTIFICATION_CHANNEL_ID = "food_today_deadline_notification_channel";

    private static final int ACTION_SHOW_PENDING_INTENT_ID         = 10;
    private static final int ACTION_SHOW_TODAY_PENDING_INTENT_ID   = 10;
    private static final int ACTION_IGNORE_PENDING_INTENT_ID       = 20;



    /**
     * ---------------------------------------------------------------------------------------------
     * Clear all notifications
     * @param context
     * ---------------------------------------------------------------------------------------------
     */
    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Food deadline reminder notification
     * @param context
     * ---------------------------------------------------------------------------------------------
     */
    public static void remindNextDaysExpiringFood(Context context, List<FoodEntry> foodEntries) {

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    FOOD_DEADLINE_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.nextdays_expiring_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        // create data visualization string
        String notificationsText = formatForNextdays(context, foodEntries);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context,FOOD_DEADLINE_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                // .setSmallIcon(R.drawable.ic_warning_green_24dp)
                .setSmallIcon(R.drawable.nextdays_notifications)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.nextdays_expiring_food_notification_title))
                // .setContentText(context.getString(R.string.nextdays_expiring_notification_body))
                .setContentText(notificationsText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.nextdays_expiring_notification_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))

                .addAction(showFoodExpiringNextDaysAction(context))
                .addAction(ignoreNotificationAction(context))

                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        notificationManager.notify(FOOD_DEADLINE_NOTIFICATION_ID, notificationBuilder.build());
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Formata Next days Expiring Food list for notification alert
     * ---------------------------------------------------------------------------------------------
     */
    private static String formatForNextdays(Context context, List<FoodEntry> todayList){
        String listString = "";
        if (todayList.size() > 0 ) {
            for(FoodEntry item: todayList){
                listString += item.getName() + ", ";
            }
        } else {
            listString = context.getString(R.string.nextdays_expiring_notification_body);
        }
        return listString;
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Food deadline reminder notification
     * @param context
     * ---------------------------------------------------------------------------------------------
     */
    public static void remindTodayExpiringFood(Context context, List<FoodEntry> foodEntries) {


        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    FOOD_TODAY_DEADLINE_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.today_expiring_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        // create data visualization string
        String notificationsText = formatForToday(context, foodEntries);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context,FOOD_TODAY_DEADLINE_NOTIFICATION_CHANNEL_ID)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        // .setSmallIcon(R.drawable.ic_warning_green_24dp)
                        .setSmallIcon(R.drawable.today_notifications)
                        .setLargeIcon(largeIcon(context))

                        .setContentTitle(context.getString(R.string.today_expiring_food_notification_title))
                        // .setContentText(context.getString(R.string.today_expiring_notification_body))
                        .setContentText(notificationsText)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(
                                context.getString(R.string.today_expiring_notification_body)))
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setContentIntent(contentIntent(context))

                        .addAction(showFoodExpiringTodayAction(context))
                        .addAction(ignoreNotificationAction(context))

                        .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        notificationManager.notify(FOOD_TODAY_DEADLINE_NOTIFICATION_ID, notificationBuilder.build());
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Formata Today Expiring Food list for todays notification alert
     * ---------------------------------------------------------------------------------------------
     */
    private static String formatForToday(Context context, List<FoodEntry> todayList){
        String listString = "";
        if (todayList.size() > 0 ) {
            for(FoodEntry item: todayList){
                listString += item.getName() + ", ";
            }
        } else {
            listString = context.getString(R.string.today_expiring_notification_body);
        }
        return listString;
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Action Ignore notification
     * @param context
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    private static NotificationCompat.Action ignoreNotificationAction(Context context) {
        Intent ignoreReminderIntent = new Intent(context, FoodReminderIntentService.class);
        ignoreReminderIntent.setAction(ReminderOps.ACTION_DISMISS_NOTIFICATION);

        PendingIntent ignoreReminderPendingIntent = PendingIntent.getService(
                context,
                ACTION_IGNORE_PENDING_INTENT_ID,
                ignoreReminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action ignoreReminderAction = new NotificationCompat.Action(R.drawable.ic_warning_green_24dp,
                context.getString(R.string.dismiss_notification_action_title),
                ignoreReminderPendingIntent);

        return ignoreReminderAction;
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Action Show list of expiring food in the next days opening app on expiring list activity
     * @param context
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    private static NotificationCompat.Action showFoodExpiringNextDaysAction(Context context) {
        // Intent foodReminderIntent = new Intent(context, FoodReminderIntentService.class);
        // foodReminderIntent.setAction(ReminderOps.ACTION_SHOW_EXPIRING_FOOD);
        Intent showExpiringFoodIntent = new Intent(context, FoodListActivity.class);
        showExpiringFoodIntent.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_EXPIRING);

        PendingIntent foodReminderPendingIntent = PendingIntent.getActivity(
                context,
                ACTION_SHOW_PENDING_INTENT_ID,
                showExpiringFoodIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Action showFoodAction = new NotificationCompat.Action(R.drawable.ic_warning_green_24dp,
                context.getString(R.string.show_food_expiring_action_title),
                foodReminderPendingIntent);

        return showFoodAction;
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Action Show list of TODAY expiring food opening app on main activity
     * @param context
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    private static NotificationCompat.Action showFoodExpiringTodayAction(Context context) {
        // Intent foodReminderIntent = new Intent(context, FoodReminderIntentService.class);
        // foodReminderIntent.setAction(ReminderOps.ACTION_SHOW_EXPIRING_FOOD);
        Intent startActivityIntent = new Intent(context, MainActivity.class);
        startActivityIntent.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_EXPIRING_TODAY);

        PendingIntent foodReminderPendingIntent = PendingIntent.getActivity(
                context,
                ACTION_SHOW_TODAY_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Action showFoodAction = new NotificationCompat.Action(R.drawable.ic_warning_green_24dp,
                context.getString(R.string.show_food_expiring_today_action_title),
                foodReminderPendingIntent);

        return showFoodAction;
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Pending item about reminder notification, call the MainActivity
     * @param context
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(
                context,
                FOOD_DEADLINE_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context) {
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.ic_warning_green_24dp);
        return largeIcon;
    }



}
