package eu.indiewalkabout.fridgemanager.feat_notifications.util.extensions

import android.app.AlarmManager
import android.app.PendingIntent
import android.os.Build
import android.util.Log
import eu.indiewalkabout.fridgemanager.core.util.extensions.TAG


fun AlarmManager.scheduleExactAlarm(type: Int, triggerAtMillis: Long, operation: PendingIntent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
        !canScheduleExactAlarms()) {
        // ase where we don't have permission
        Log.e(TAG, "Cannot schedule exact alarm - permission not granted")
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