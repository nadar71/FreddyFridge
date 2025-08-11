package eu.indiewalkabout.fridgemanager.feat_notifications.presentation.components

import android.Manifest
import android.R.attr.onClick
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.data.locals.AppPreferences
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColor
import eu.indiewalkabout.fridgemanager.feat_notifications.util.extensions.hasNotificationPermission
import eu.indiewalkabout.fridgemanager.feat_notifications.util.extensions.openAppSettings


@Composable
fun NotificationPermissionDialog(
    onDismiss: () -> Unit,
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    var showRationale by remember { mutableStateOf(false) }
    var dontAskAgain by remember { mutableStateOf(AppPreferences.dontask_again_notification_permissions) }

    // Check if we already have permission
    val hasPermission = remember {
        context.hasNotificationPermission()
    }

    // If we already have permission, call onPermissionGranted and dismiss
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            onPermissionGranted()
            onDismiss()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
            onDismiss()
        } else {
            // Check if we should show rationale
            // NB :
            // - Use Build.VERSION_CODES.TIRAMISU (API 33) instead of M (API 23) for the notification permission check
            // - For devices running Android 12L (API 32) and below, we assume the permission is granted since it's not required
            val activity = context as? androidx.activity.ComponentActivity
            val shouldShowRationale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && activity != null
            ) {
                activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                false
            }

            if (shouldShowRationale) {
                showRationale = true
            } else {
                dontAskAgain = true
            }
        }
    }

    if (!hasPermission) {
        AlertDialog(
            onDismissRequest = onDismiss,

            title = {
                Text(
                    text = stringResource(R.string.notification_permission_title),
                    style = MaterialTheme.typography.headlineSmall
                )
            },

            text = {
                Text(
                    text = if (dontAskAgain) {
                        stringResource(R.string.notification_permission_go_to_settings)
                    } else if (showRationale) {
                        stringResource(R.string.notification_permission_rationale)
                    } else {
                        stringResource(R.string.notification_permission_message)
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            },

            confirmButton = {
                Button(
                    onClick = {
                        if (dontAskAgain) {
                            context.openAppSettings()
                            onDismiss()
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                ) {
                    Text(
                        stringResource(
                            id = if (dontAskAgain) R.string.notification_permission_go_to_settings
                            else R.string.allow
                        )
                    )
                }
            },

            dismissButton = {
                if (!dontAskAgain) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(id = R.string.not_now))
                    }
                    TextButton(
                        onClick = {
                            AppPreferences.dontask_again_notification_permissions = true
                            onDismiss()
                        }
                    ) {
                        Text(stringResource(id = R.string.dont_ask_again))
                    }
                } else {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(id = android.R.string.cancel))
                    }
                }
            }
        )
    }
}


@Composable
fun CheckNotificationPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit = {}
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    val checkAndRequestPermission = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionStatus = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )

            if (permissionStatus == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted()
            } else {
                showDialog = true
            }
        } else {
            // On older versions, notifications are enabled by default
            onPermissionGranted()
        }
    }

    LaunchedEffect(Unit) {
        checkAndRequestPermission()
    }

    if (showDialog) {
        NotificationPermissionDialog(
            onDismiss = {
                showDialog = false
                onPermissionDenied()
            },
            onPermissionGranted = {
                showDialog = false
                onPermissionGranted()
            }
        )
    }
}