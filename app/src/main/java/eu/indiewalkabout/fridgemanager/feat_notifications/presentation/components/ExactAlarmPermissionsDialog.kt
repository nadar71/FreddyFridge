package eu.indiewalkabout.fridgemanager.feat_notifications.presentation.components

import android.R.attr.text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import eu.indiewalkabout.fridgemanager.R

@Composable
fun ExactAlarmPermissionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.exact_alarm_permission_title),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            val appName = stringResource(R.string.app_name)
            Text(
                text = stringResource(R.string.exact_alarm_permission_message,appName),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.continue_text))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.not_now))
            }
        }
    )
}