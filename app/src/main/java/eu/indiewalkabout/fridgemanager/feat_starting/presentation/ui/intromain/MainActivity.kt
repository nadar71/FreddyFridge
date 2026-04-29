package eu.indiewalkabout.fridgemanager.feat_starting.presentation.ui.intromain

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import eu.indiewalkabout.fridgemanager.FreddyFridgeApp
import eu.indiewalkabout.fridgemanager.FreddyFridgeApp.Companion.alarmReminderScheduler
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.data.locals.AppPreferences
import eu.indiewalkabout.fridgemanager.core.data.locals.Constants.NUM_MAX_OPENINGS
import eu.indiewalkabout.fridgemanager.core.presentation.navigation.AppDestinationRoutes
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.util.ReviewManagerUtil
import eu.indiewalkabout.fridgemanager.feat_ads.util.ConsentManager
import eu.indiewalkabout.fridgemanager.feat_ads.util.RequestConfigurationUtils
import eu.indiewalkabout.fridgemanager.core.presentation.navigation.AppNavigation
import eu.indiewalkabout.fridgemanager.core.presentation.navigation.NavigationGraph
import eu.indiewalkabout.fridgemanager.feat_notifications.domain.reminder.AlarmReminderScheduler
import eu.indiewalkabout.fridgemanager.feat_notifications.presentation.components.NotificationPermissionDialog
import eu.indiewalkabout.fridgemanager.feat_notifications.util.extensions.RequestExactAlarmPermissionDialog
import eu.indiewalkabout.fridgemanager.feat_notifications.util.extensions.canScheduleExactAlarms
import eu.indiewalkabout.fridgemanager.feat_notifications.util.extensions.needsExactAlarmPermissionCheck
import eu.indiewalkabout.fridgemanager.feat_notifications.util.extensions.openAlarmSettings
import kotlinx.coroutines.delay


@AndroidEntryPoint
class MainActivity: AppCompatActivity()  {
    val TAG = "MainActivity"
    private var canRequestAds by mutableStateOf(false)
    private var isAppReady by mutableStateOf(false)
    private var pendingNavigationRoute by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setOnExitAnimationListener { splashScreenViewProvider ->
            splashScreenViewProvider.view.animate()
                .alpha(0f)
                .setDuration(220L)
                .withEndAction {
                    splashScreenViewProvider.remove()
                }
                .start()
        }
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Main_activity created")

        // start scheduler for notifications reminder
        alarmReminderScheduler = AlarmReminderScheduler(this)

        // Handle deep link from notification
        handleIntent(intent)

        // Set your test devices.
        RequestConfigurationUtils.setTestDeviceIds()

        // Request review
        ReviewManagerUtil.requestReviewIfEligible(this)

        setContent {
            FreddyFridgeTheme {
                MainActivityContent(
                    isAppReady = isAppReady,
                    pendingNavigationRoute = pendingNavigationRoute,
                    onPendingNavigationConsumed = { pendingNavigationRoute = null }
                )
            }
        }

        // Check consent
        ConsentManager.requestConsent(this, this@MainActivity) { canRequestAds ->
            MobileAds.initialize(this)
            FreddyFridgeApp.canRequestAdsFlag = canRequestAds
            this.canRequestAds = canRequestAds
            isAppReady = true
        }
        
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.let { handleIntent(it) }
    }

    override fun onStart() {
        super.onStart()
    }

    private fun handleIntent(intent: Intent) {
        intent.getStringExtra("destination")?.let { route ->
            Log.d(TAG, "handleIntent: queue navigation to $route")
            pendingNavigationRoute = route
        }
    }

    override fun onResume() {
        super.onResume()
        alarmReminderScheduler.setRepeatingAlarm()
    }

}

@Composable
private fun MainActivityContent(
    isAppReady: Boolean,
    pendingNavigationRoute: String?,
    onPendingNavigationConsumed: () -> Unit,
) {
    var launchAnimationCompleted by remember { mutableStateOf(false) }

    if (!isAppReady || !launchAnimationCompleted) {
        BrandedLaunchFrame(
            onAnimationCompleted = { launchAnimationCompleted = true }
        )
        return
    }

    val context = LocalContext.current
    var showNotificationPermissionDialog by remember { mutableStateOf(true) }
    var askForExactAlarmPermission by remember { mutableStateOf(false) }
    var showExactAlarmPermissionDialog by remember {
        mutableStateOf(needsExactAlarmPermissionCheck() && !context.canScheduleExactAlarms())
    }

    if (showNotificationPermissionDialog &&
        AppPreferences.app_opening_counter < NUM_MAX_OPENINGS &&
        !AppPreferences.dontask_again_notification_permissions) {
        NotificationPermissionDialog(
            onDismiss = {
                showNotificationPermissionDialog = false
                askForExactAlarmPermission = true
            },
            onPermissionGranted = {
                showNotificationPermissionDialog = false
                askForExactAlarmPermission = true
            }
        )
    } else {
        askForExactAlarmPermission = true
    }

    if (askForExactAlarmPermission &&
        showExactAlarmPermissionDialog &&
        AppPreferences.app_opening_counter < NUM_MAX_OPENINGS) {
        RequestExactAlarmPermissionDialog(
            onDismiss = {
                showExactAlarmPermissionDialog = false
                alarmReminderScheduler.setRepeatingAlarm()
            },
            onPermissionGranted = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    context.openAlarmSettings()
                }
                alarmReminderScheduler.setRepeatingAlarm()
                showExactAlarmPermissionDialog = false
            }
        )
    } else {
        LaunchedEffect(Unit) {
            alarmReminderScheduler.setRepeatingAlarm()
        }
    }

    AppNavigation.NavigationInit()
    NavigationGraph(AppNavigation.appNavHostController)

    LaunchedEffect(pendingNavigationRoute) {
        val route = pendingNavigationRoute ?: return@LaunchedEffect
        AppNavigation.appNavHostController.navigate(route) {
            popUpTo(AppDestinationRoutes.MainScreen.route) {
                inclusive = false
            }
            launchSingleTop = true
        }
        onPendingNavigationConsumed()
    }
}

@Composable
private fun BrandedLaunchFrame(
    onAnimationCompleted: () -> Unit,
) {
    val logoScale = remember { Animatable(0.42f) }
    val frameAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        logoScale.animateTo(
            targetValue = 0.78f,
            animationSpec = tween(
                durationMillis = 650,
                easing = FastOutSlowInEasing
            )
        )
        delay(260)
        frameAlpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 320,
                easing = LinearOutSlowInEasing
            )
        )
        onAnimationCompleted()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                AppCompatImageView(context).apply {
                    setImageResource(R.drawable.intro_background)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = frameAlpha.value
                }
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(148.dp)
                .graphicsLayer {
                    alpha = frameAlpha.value
                    scaleX = logoScale.value
                    scaleY = logoScale.value
                }
                .clip(CircleShape)
                .background(Color.White)
        )
        Image(
            painter = painterResource(id = R.drawable.fridge_foreground),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(88.dp)
                .graphicsLayer {
                    alpha = frameAlpha.value
                    scaleX = logoScale.value
                    scaleY = logoScale.value
                }
        )
    }
}
