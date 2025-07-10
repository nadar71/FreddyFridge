package eu.indiewalkabout.fridgemanager.core.reminder.withalarmmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.indiewalkabout.fridgemanager.core.data.locals.AppPreferences
import eu.indiewalkabout.fridgemanager.core.util.extensions.TAG
import java.util.Calendar
import javax.inject.Inject

// Started in FreddyFridgeApp at app open
class AlarmReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var alarmMgr: AlarmManager? = null
    private var alarmIntent: PendingIntent
    private val firstAllowedAlarmTime: Calendar


    init {
        // Retrieves the AlarmManager system service
        alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
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


    fun setRepeatingAlarm() {
        val hoursFrequency = AppPreferences.daily_notifications_number // PreferenceUtility.getHoursCount(context)
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
    }


    fun setRepeatingAlarm(minutes: Int) {

        alarmMgr?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            firstAllowedAlarmTime.timeInMillis,
            1000 * 60 * minutes.toLong(),
            alarmIntent
        )
        Log.i(TAG, " AlarmReminderScheduler : repeating alarm set every ${minutes} minutes. ")
    }


}