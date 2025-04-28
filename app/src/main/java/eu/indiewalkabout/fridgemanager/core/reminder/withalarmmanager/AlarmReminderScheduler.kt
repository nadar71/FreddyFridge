package eu.indiewalkabout.fridgemanager.core.reminder.withalarmmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import eu.indiewalkabout.fridgemanager.FreddyFridgeApp
import eu.indiewalkabout.fridgemanager.core.util.PreferenceUtility
import eu.indiewalkabout.fridgemanager.core.util.extensions.TAG
import java.util.Calendar

class AlarmReminderScheduler() {

    private var alarmMgr: AlarmManager? = null
    private var alarmIntent: PendingIntent
    private val calendar: Calendar
    private val context: Context = FreddyFridgeApp.getsContext()!!


    init {
        alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            Log.i(TAG, " AlarmReminderScheduler : repeating alarm activated ")
            // PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        // Set the alarm to start at 8:30 a.m.
        calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 30)
        }
    }


    fun setRepeatingAlarm() {
        val hoursFrequency = PreferenceUtility.getHoursCount(context)
        val minutesPeriodicity = hoursFrequency * 60

        // TODO: not working
        if (Calendar.HOUR_OF_DAY < 21 && Calendar.HOUR_OF_DAY > 8) {
            alarmMgr?.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                1000 * 60 * minutesPeriodicity.toLong(),
                alarmIntent
            )
        }
        Log.i(TAG, " AlarmReminderScheduler : repeating alarm set every ${hoursFrequency} hours. ")
    }


    fun setRepeatingAlarm(minutes: Int) {

        alarmMgr?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            1000 * 60 * minutes.toLong(),
            alarmIntent
        )
        Log.i(TAG, " AlarmReminderScheduler : repeating alarm set every ${minutes} minutes. ")
    }


}