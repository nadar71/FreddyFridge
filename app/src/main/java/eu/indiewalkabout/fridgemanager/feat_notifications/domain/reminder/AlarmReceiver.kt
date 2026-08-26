package eu.indiewalkabout.fridgemanager.feat_notifications.domain.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import eu.indiewalkabout.fridgemanager.core.data.locals.AppPreferences
import eu.indiewalkabout.fridgemanager.core.util.DateUtility
import eu.indiewalkabout.fridgemanager.core.util.extensions.TAG
import eu.indiewalkabout.fridgemanager.feat_notifications.util.NotificationsUtility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationCoordinator: ReminderNotificationCoordinator

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val localTodayMidnight = DateUtility.getLocalMidnightFromNormalizedUtcDate(
                    DateUtility.normalizedUtcMsForToday,
                )
                val food = notificationCoordinator.load(
                    localTodayMidnight = localTodayMidnight,
                    daysBeforeDeadline = AppPreferences.days_before_deadline,
                )

                if (food.expiringSoon.isNotEmpty()) {
                    NotificationsUtility.remindNextDaysExpiringFood(context, food.expiringSoon)
                }
                if (food.expiringToday.isNotEmpty()) {
                    NotificationsUtility.remindTodayExpiringFood(context, food.expiringToday)
                }
            } catch (error: Exception) {
                Log.e(TAG, "Unable to deliver food reminder notifications", error)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
