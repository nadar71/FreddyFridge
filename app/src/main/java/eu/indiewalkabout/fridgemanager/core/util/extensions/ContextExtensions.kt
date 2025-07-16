package eu.indiewalkabout.fridgemanager.core.util.extensions

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.indiewalkabout.fridgemanager.core.presentation.components.ExactAlarmPermissionDialog
import eu.indiewalkabout.fridgemanager.core.util.extensions.companion.alarmManager

/*
fun Context.checkAndRequestExactAlarmPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (!alarmManager.canScheduleExactAlarms()) {
            // Show rationale and request permission
            val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            startActivity(intent)
        }
    }
}*/
object companion {
    lateinit var alarmManager: AlarmManager
}



@Composable
fun RequestExactAlarmPermissionDialog(
    onDismiss: () -> Unit = {},
    onPermissionGranted: () -> Unit = {}
) {
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(needsExactAlarmPermission(context)) }

    if (showDialog.value) {
        ExactAlarmPermissionDialog(
            onDismiss = {
                showDialog.value = false
                onDismiss()
            },
            onConfirm = {
                openAlarmSettings(context)
                showDialog.value = false
                onPermissionGranted()
            }
        )
    }
}

fun Context.checkAndRequestExactAlarmPermission() {
    if (needsExactAlarmPermission(this)) {
        openAlarmSettings(this)
    }
}

fun needsExactAlarmPermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return false

    alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    return !alarmManager.canScheduleExactAlarms()
}


private fun openAlarmSettings(context: Context) {
    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}
