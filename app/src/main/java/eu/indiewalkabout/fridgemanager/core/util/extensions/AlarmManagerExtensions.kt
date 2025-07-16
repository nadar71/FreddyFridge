package eu.indiewalkabout.fridgemanager.core.util.extensions

import android.app.AlarmManager
import android.app.PendingIntent
import android.util.Log


fun AlarmManager.scheduleExactAlarm(type: Int, triggerAtMillis: Long, operation: PendingIntent) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S &&
        !canScheduleExactAlarms()) {
        // ase where we don't have permission
        Log.e(TAG, "Cannot schedule exact alarm - permission not granted")
        // TODO : show a dialog about this
        // for now fall back to inexact alarms (i.e. within a 1 minute window)
        setWindow(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            60 * 1000, // 1 minute time window setting: not exact, but enough for the app
            operation
        )
        return
    }
    setExactAndAllowWhileIdle(type, triggerAtMillis, operation)
}