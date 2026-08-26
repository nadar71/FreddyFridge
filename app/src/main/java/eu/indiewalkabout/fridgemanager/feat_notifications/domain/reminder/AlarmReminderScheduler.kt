package eu.indiewalkabout.fridgemanager.feat_notifications.domain.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.indiewalkabout.fridgemanager.core.data.locals.AppPreferences
import eu.indiewalkabout.fridgemanager.core.data.locals.Constants.NUM_MAX_DAILY_NOTIFICATIONS_NUMBER
import eu.indiewalkabout.fridgemanager.core.util.extensions.TAG
import eu.indiewalkabout.fridgemanager.feat_notifications.util.extensions.companion.alarmManager
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

class AlarmReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    init {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    fun setRepeatingAlarm() {
        cancelAllAlarms()
        val alarmTimes = ReminderScheduleCalculator.calculate(
            now = Instant.now(),
            zoneId = ZoneId.systemDefault(),
            notificationsPerDay = AppPreferences.daily_notifications_number,
        )

        alarmTimes.forEachIndexed { requestCode, alarmTime ->
            val pendingIntent = reminderPendingIntent(requestCode)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                alarmManager.canScheduleExactAlarms()
            ) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    alarmTime.toEpochMilli(),
                    pendingIntent,
                )
                Log.d(TAG, "Scheduled EXACT alarm at $alarmTime")
            } else {
                alarmManager.setWindow(
                    AlarmManager.RTC_WAKEUP,
                    alarmTime.toEpochMilli(),
                    ALARM_WINDOW_MILLIS,
                    pendingIntent,
                )
                Log.d(TAG, "Scheduled WINDOW alarm at $alarmTime")
            }
        }
    }

    fun cancelAllAlarms() {
        for (requestCode in 0 until NUM_MAX_DAILY_NOTIFICATIONS_NUMBER) {
            alarmManager.cancel(reminderPendingIntent(requestCode))
        }
    }

    private fun reminderPendingIntent(requestCode: Int): PendingIntent = PendingIntent.getBroadcast(
        context,
        requestCode,
        Intent(context, AlarmReceiver::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    private companion object {
        const val ALARM_WINDOW_MILLIS = 60 * 60 * 1000L
    }
}
