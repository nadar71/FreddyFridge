package eu.indiewalkabout.fridgemanager.core.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.reminder.FoodReminderIntentService
import eu.indiewalkabout.fridgemanager.core.reminder.ReminderOps
import eu.indiewalkabout.fridgemanager.core.reminder.withworkmanager.ReminderScheduler
import eu.indiewalkabout.fridgemanager.core.util.extensions.TAG
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_starting.presentation.ui.intromain.MainActivity


// TODO : All the part commented here is to be refactored to use workmanager
object NotificationsUtility {

    // Unique Id to Refer to notification when displayed
    private const val FOOD_NEXTDAYS_DEADLINE_NOTIFICATION_ID = 1000

    // Reference to notification pendingitent
    private const val FOOD_NEXTDAYS_DEADLINE_PENDING_INTENT_ID = 1100

    // Unique Id to Refer to notification when displayed
    private const val FOOD_TODAY_DEADLINE_NOTIFICATION_ID = 2000

    // Reference to notification pendingitent
    private val FOOD_TODAY_DEADLINE_PENDING_INTENT_ID = 2100

    // This notification channel and its action for sdk >= 26 (Oreo)
    private const val FOOD_NEXTDAYS_DEADLINE_NOTIFICATION_CHANNEL_ID = "food_deadline_notification_channel"
    private const val FOOD_TODAY_DEADLINE_NOTIFICATION_CHANNEL_ID = "food_today_deadline_notification_channel"

    private const val ACTION_SHOW_NEXTDAYS_PENDING_INTENT_ID = 10
    private const val ACTION_SHOW_TODAY_PENDING_INTENT_ID = 10
    private const val ACTION_IGNORE_PENDING_INTENT_ID = 20


    fun clearAllNotifications(context: Context) {
        val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }


    // Food deadline reminder notification
    fun remindNextDaysExpiringFood(context: Context, foodEntries: List<FoodEntry>) {

        val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        createNotificationsChannel(
            FOOD_NEXTDAYS_DEADLINE_NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.notification_nextdays_expiring_channel_name),
                context.getString(R.string.notification_nextdays_expiring_channel_name),
                notificationManager)

        // create data visualization string
        val notificationsText = formatForNextdays(context, foodEntries)

        val notificationBuilder = NotificationCompat.Builder(context, FOOD_NEXTDAYS_DEADLINE_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.nextdays_notifications)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.notification_nextdays_expiring_food_title))
                .setContentText(notificationsText)
                .setStyle(NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.notification_nextdays_expiring_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context)) // link a pending intent to notification
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // .addAction(showFoodExpiringNextDaysAction(context))
                .addAction(ignoreNotificationAction(context))
                .setAutoCancel(true)

        // Show notification
        Log.i(ReminderScheduler.TAG, "Create notification for remindNextDaysExpiringFood")
        notificationManager.notify(FOOD_NEXTDAYS_DEADLINE_NOTIFICATION_ID, notificationBuilder.build())
    }


    // Format Next days Expiring Food list for notification alert
    private fun formatForNextdays(context: Context, todayList: List<FoodEntry>): String {
        var listString = ""
        if (todayList.size > 0) {
            for (item in todayList) {
                listString += item.name + ", "
            }
        } else {
            listString = context.getString(R.string.notification_nextdays_expiring_body)
        }
        return listString
    }


    // Food deadline reminder notification
    fun remindTodayExpiringFood(context: Context, foodEntries: List<FoodEntry>) {

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationsChannel(
            FOOD_TODAY_DEADLINE_NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.notification_today_expiring_channel_name),
                context.getString(R.string.notification_today_expiring_channel_name),
                notificationManager)

        // create data visualization string
        val notificationsText = formatForToday(context, foodEntries)

        val notificationBuilder = NotificationCompat.Builder(context, FOOD_TODAY_DEADLINE_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.today_notifications)
                .setLargeIcon(largeIcon(context))

                .setContentTitle(context.getString(R.string.notification_today_expiring_food_title))
                .setContentText(notificationsText)
                .setStyle(NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.notification_today_expiring_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))  // link a pending intent to notification
                // .addAction(showFoodExpiringTodayAction(context))
                .addAction(ignoreNotificationAction(context))
                .setAutoCancel(true)

        // Show notification
        Log.i(ReminderScheduler.TAG, "Create notification for remindTodayExpiringFood")
        notificationManager.notify(FOOD_TODAY_DEADLINE_NOTIFICATION_ID, notificationBuilder.build())
    }


    private fun createNotificationsChannel(id: String, name: String, channelDescription: String,
                                           notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance: Int = NotificationManager.IMPORTANCE_HIGH
            val channel: NotificationChannel = NotificationChannel(id, name, importance).apply {
                description = channelDescription
            }
            notificationManager.createNotificationChannel(channel)
        }
    }


    // Format Today Expiring Food list for todays notification alert
    private fun formatForToday(context: Context, todayList: List<FoodEntry>): String {
        var listString = ""
        if (todayList.size > 0) {
            for (item in todayList) {
                listString += item.name + ", "
            }
        } else {
            listString = context.getString(R.string.notification_today_expiring_body)
        }
        return listString
    }


    // User Action Ignore notification
    private fun ignoreNotificationAction(context: Context): NotificationCompat.Action {
        val ignoreReminderIntent = Intent(context, FoodReminderIntentService::class.java)
        ignoreReminderIntent.action = ReminderOps.ACTION_DISMISS_NOTIFICATION

        val ignoreReminderPendingIntent = PendingIntent.getService(
                context,
                ACTION_IGNORE_PENDING_INTENT_ID,
                ignoreReminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Action(R.drawable.ic_warning_green_24dp,
                context.getString(R.string.notification_dismiss_action_title),
                ignoreReminderPendingIntent)
    }


    // User Action Show list of expiring food in the next days opening app on expiring list activity
    private fun showFoodExpiringNextDaysAction(context: Context)/*: NotificationCompat.Action*/ {


        /*val showExpiringFoodIntent = Intent(context, FoodListActivity::class.java)
        showExpiringFoodIntent.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_EXPIRING)

        val foodReminderPendingIntent = PendingIntent.getActivity(
                context,
                ACTION_SHOW_NEXTDAYS_PENDING_INTENT_ID,
                showExpiringFoodIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        Log.i(TAG, "SHOW notification for showFoodExpiringNextDaysAction")
        return NotificationCompat.Action(R.drawable.ic_warning_green_24dp,
                context.getString(R.string.show_food_expiring_action_title),
                foodReminderPendingIntent)

         */
    }


    // User Action Show list of TODAY expiring food opening app on main activity
    private fun showFoodExpiringTodayAction(context: Context)/*: NotificationCompat.Action*/ {
        /*val startActivityIntent = Intent(context, MainActivity::class.java)
        startActivityIntent.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_EXPIRING_TODAY)

        val foodReminderPendingIntent = PendingIntent.getActivity(
                context,
                ACTION_SHOW_TODAY_PENDING_INTENT_ID,
                startActivityIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        Log.i(TAG, "SHOW notification for showFoodExpiringTodayAction")
        return NotificationCompat.Action(R.drawable.ic_warning_green_24dp,
                context.getString(R.string.show_food_expiring_today_action_title),
                foodReminderPendingIntent)*/
    }


    // Pending item about reminder notification, call the MainActivity
    private fun contentIntent(context: Context): PendingIntent {
        val startActivityIntent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(
                context,
                FOOD_NEXTDAYS_DEADLINE_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun largeIcon(context: Context): Bitmap {
        val res = context.resources
        // return BitmapFactory.decodeResource(res, R.drawable.ic_warning_green_24dp)
        return (ResourcesCompat.getDrawable(res, R.drawable.ic_warning_green_24dp, null)
                as VectorDrawable).toBitmap()
    }


}
