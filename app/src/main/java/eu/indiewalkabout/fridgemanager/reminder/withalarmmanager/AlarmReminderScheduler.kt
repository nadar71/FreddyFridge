package eu.indiewalkabout.fridgemanager.reminder.withalarmmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import eu.indiewalkabout.fridgemanager.App
import java.util.*

class AlarmReminderScheduler() {

    companion object {
        val TAG = AlarmReminderScheduler::class.java.simpleName
    }

    private var alarmMgr: AlarmManager? = null
    private var alarmIntent: PendingIntent
    private val calendar: Calendar
    private val context: Context



    init {
        context = App.getsContext()!!
        alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            Log.i(TAG, " AlarmReminderScheduler : repeating alarm activated ")
            // PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        // Set the alarm to start at 8:30 a.m.
        calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 30)
        }
    }



    fun setRepeatingAlarm(minutes: Int) {
        alarmMgr?.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                1000 * 60 * minutes.toLong(),
                alarmIntent
        )
        Log.i(TAG, " AlarmReminderScheduler : repeating alarm set. ")
    }
}