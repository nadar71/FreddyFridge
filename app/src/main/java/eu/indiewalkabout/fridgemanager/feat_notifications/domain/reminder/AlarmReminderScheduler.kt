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
import java.util.Calendar
import javax.inject.Inject

// Started in FreddyFridgeApp at app open
class AlarmReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // private var alarmManager: AlarmManager? = null
    private var alarmIntent: PendingIntent
    private val firstAllowedAlarmTime: Calendar
    private var ALARM_REQUEST_CODE_BASE = 0


    init {
        // Retrieves the AlarmManager system service
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Intent that will be broadcast when the alarm fires, targetting AlarmReceiver as BroadcastReceiver
        alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            Log.i(TAG, " AlarmReminderScheduler : repeating alarm activated ")
            // PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        // Set the alarm to start at 8:30 a.m.
        firstAllowedAlarmTime = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 30)
        }
    }


    /*fun setRepeatingAlarm() {
        val hoursFrequency = AppPreferences.daily_notifications_number
        // TODO : now is the number of notifications: must divide and calculate  frequency
        val minutesPeriodicity = hoursFrequency * 60

        // TODO: not working
        val currentDayHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (currentDayHour < 21 && currentDayHour > 8) {
            alarmMgr?.setRepeating(
                AlarmManager.RTC_WAKEUP,
                firstAllowedAlarmTime.timeInMillis,
                1000 * 60 * minutesPeriodicity.toLong(),
                alarmIntent
            )
        }
        Log.i(TAG, " AlarmReminderScheduler : repeating alarm set every ${hoursFrequency} hours. ")
    }*/

    /*fun setRepeatingAlarm() {
        // Delete any existing alarms
        cancelAllAlarms()

        val dailyNotificationsNumber = AppPreferences.daily_notifications_number
        if (dailyNotificationsNumber <= 0) return

        // Get current time and normalize to 8:00 AM today
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 8)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Calculate the interval between alarms
        val totalHours = 13
        val intervalHours = totalHours / dailyNotificationsNumber.toDouble()

        // Get current time
        val now = Calendar.getInstance()

        // Create alarms only for the remaining part of the day
        for (i in 0 until dailyNotificationsNumber) {
            val alarmCalendar = Calendar.getInstance()
            alarmCalendar.timeInMillis = calendar.timeInMillis

            // Calculate the hour for this alarm
            val hourOffset = (i * intervalHours).toInt()
            alarmCalendar.add(Calendar.HOUR_OF_DAY, hourOffset)

            // Only schedule if the alarm time is in the future
            if (alarmCalendar.after(now)) {
                // Create unique request code for each alarm
                val requestCode = ALARM_REQUEST_CODE_BASE + i

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    Intent(context, AlarmReceiver::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    alarmCalendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )

                Log.d(TAG, "Scheduled alarm at ${alarmCalendar.time}")
            }
        }
    }*/

    fun setRepeatingAlarm() {
        cancelAllAlarms()
        val dailyNotificationsNumber = AppPreferences.daily_notifications_number
        if (dailyNotificationsNumber <= 0) return

        // current time
        val now = Calendar.getInstance()

        // Create a calendar for the start of today's notification window (8:00 AM)
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If it's already past 9 PM, start from tomorrow
            if (get(Calendar.HOUR_OF_DAY) >= 21) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val totalHours = 13 // 8:00 to 21:00 is 13 hours
        val intervalHours = totalHours / dailyNotificationsNumber.toDouble()

        for (i in 0 until dailyNotificationsNumber) {
            val alarmCalendar = Calendar.getInstance().apply {
                timeInMillis = calendar.timeInMillis
                add(Calendar.MINUTE, (i * intervalHours * 60).toInt())
            }

            // Only schedule if the alarm time is in the future or within a 30' window in the past
            val timeDiff = alarmCalendar.timeInMillis - now.timeInMillis
            if (timeDiff > -30 * 60 * 1000) { // withou window : alarmCalendar.after(now)
                val requestCode = ALARM_REQUEST_CODE_BASE + i
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    Intent(context, AlarmReceiver::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        alarmCalendar.timeInMillis,
                        pendingIntent
                    )
                    Log.d(TAG, "Scheduled EXACT alarm at ${alarmCalendar.time}")
                } else {
                    alarmManager.setWindow(
                        AlarmManager.RTC_WAKEUP,
                        alarmCalendar.timeInMillis,
                        60 * 60 * 1000, // 1 hour window
                        pendingIntent
                    )
                    Log.d(TAG, "Scheduled WINDOW alarm at ${alarmCalendar.time}")
                }
            } else {
                Log.d(TAG, "Skipping alarm at ${alarmCalendar.time} as it's too far in the past")
            }
        }
    }


    fun cancelAllAlarms() {
        for (i in 0 until NUM_MAX_DAILY_NOTIFICATIONS_NUMBER) {
            val requestCode = ALARM_REQUEST_CODE_BASE + i
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }


}