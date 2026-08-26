package eu.indiewalkabout.fridgemanager.feat_starting.presentation.ui.intromain

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.widget.ImageView
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import dagger.hilt.android.AndroidEntryPoint
import eu.indiewalkabout.fridgemanager.FreddyFridgeApp
import eu.indiewalkabout.fridgemanager.FreddyFridgeApp.Companion.alarmReminderScheduler
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.data.locals.AppPreferences
import eu.indiewalkabout.fridgemanager.core.data.locals.Constants.NUM_MAX_OPENINGS
import eu.indiewalkabout.fridgemanager.core.presentation.navigation.AppDestination
import eu.indiewalkabout.fridgemanager.core.presentation.navigation.AppNavDisplay
import eu.indiewalkabout.fridgemanager.core.presentation.navigation.NotificationNavigationContract
import eu.indiewalkabout.fridgemanager.core.presentation.navigation.rememberAppNavigationState
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.util.ReviewManagerUtil
import eu.indiewalkabout.fridgemanager.feat_ads.util.ConsentManager
import eu.indiewalkabout.fridgemanager.feat_ads.util.MobileAdsInitializer
import eu.indiewalkabout.fridgemanager.feat_ads.util.RequestConfigurationUtils
import eu.indiewalkabout.fridgemanager.feat_notifications.domain.reminder.AlarmReminderScheduler
import eu.indiewalkabout.fridgemanager.feat_notifications.presentation.components.NotificationPermissionDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity: AppCompatActivity()  {
    val TAG = "MainActivity"
    private var isAppReady by mutableStateOf(false)
    private var pendingNavigationDestination by mutableStateOf<AppDestination?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Main_activity created")

        // start scheduler for notifications reminder
        alarmReminderScheduler = AlarmReminderScheduler(this)

        // Handle deep link from notification
        handleIntent(intent)

        // Set your test devices.
        RequestConfigurationUtils.setTestDeviceIds(
            isDebug = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        )

        // Request review
        ReviewManagerUtil.requestReviewIfEligible(this)

        setContent {
            CompositionLocalProvider(LocalNavigationEventDispatcherOwner provides this@MainActivity) {
                FreddyFridgeTheme {
                    MainActivityContent(
                        isAppReady = isAppReady,
                        pendingNavigationDestination = pendingNavigationDestination,
                        onPendingNavigationConsumed = { pendingNavigationDestination = null }
                    )
                }
            }
        }

        // Check consent
        ConsentManager.requestConsent(this) { canRequestAds ->
            FreddyFridgeApp.canRequestAdsFlag = canRequestAds
            MobileAdsInitializer.initializeIfAllowed(applicationContext, canRequestAds)
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
        intent.getStringExtra(NotificationNavigationContract.DESTINATION_EXTRA)?.let { route ->
            Log.d(TAG, "handleIntent: queue navigation to $route")
            pendingNavigationDestination = NotificationNavigationContract.destinationFromRoute(route)
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
    pendingNavigationDestination: AppDestination?,
    onPendingNavigationConsumed: () -> Unit,
) {
    var launchAnimationCompleted by remember { mutableStateOf(false) }
    val appContentAlpha = remember { Animatable(0f) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isAppReady) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = appContentAlpha.value
                    }
            ) {
                MainAppContent(
                    pendingNavigationDestination = pendingNavigationDestination,
                    onPendingNavigationConsumed = onPendingNavigationConsumed,
                    allowTransientDialogs = launchAnimationCompleted
                )
            }
        }

        if (!launchAnimationCompleted) {
            BrandedLaunchFrame(
                startReveal = isAppReady,
                onUnderlyingContentReveal = {
                    appContentAlpha.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(
                            durationMillis = 260,
                            easing = LinearOutSlowInEasing
                        )
                    )
                },
                onAnimationCompleted = { launchAnimationCompleted = true }
            )
        }
    }
}

@Composable
private fun MainAppContent(
    pendingNavigationDestination: AppDestination?,
    onPendingNavigationConsumed: () -> Unit,
    allowTransientDialogs: Boolean,
) {
    val navigationState = rememberAppNavigationState()
    var showNotificationPermissionDialog by remember { mutableStateOf(true) }

    if (allowTransientDialogs &&
        showNotificationPermissionDialog &&
        AppPreferences.app_opening_counter < NUM_MAX_OPENINGS &&
        !AppPreferences.dontask_again_notification_permissions) {
        NotificationPermissionDialog(
            onDismiss = {
                showNotificationPermissionDialog = false
            },
            onPermissionGranted = {
                showNotificationPermissionDialog = false
            }
        )
    }

    AppNavDisplay(navigationState = navigationState)

    LaunchedEffect(pendingNavigationDestination) {
        val destination = pendingNavigationDestination ?: return@LaunchedEffect
        navigationState.navigate(destination)
        onPendingNavigationConsumed()
    }
}

@Composable
private fun BrandedLaunchFrame(
    startReveal: Boolean,
    onUnderlyingContentReveal: suspend () -> Unit,
    onAnimationCompleted: () -> Unit,
) {
    val logoScale = remember { Animatable(0.84f) }
    val overlayAlpha = remember { Animatable(1f) }
    val badgeAlpha = remember { Animatable(0f) }

    LaunchedEffect(startReveal) {
        if (!startReveal) return@LaunchedEffect

        val badgeJob = launch {
            badgeAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 220,
                    easing = LinearOutSlowInEasing
                )
            )
        }
        val logoJob = launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.86f,
                    stiffness = 210f
                )
            )
        }

        badgeJob.join()
        logoJob.join()

        delay(220)

        val revealJob = launch {
            onUnderlyingContentReveal()
        }
        val fadeJob = launch {
            overlayAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 340,
                    easing = LinearOutSlowInEasing
                )
            )
        }
        val shrinkJob = launch {
            logoScale.animateTo(
                targetValue = 0.72f,
                animationSpec = tween(
                    durationMillis = 340,
                    easing = LinearOutSlowInEasing
                )
            )
        }

        revealJob.join()
        fadeJob.join()
        shrinkJob.join()
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
                    alpha = overlayAlpha.value
                }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x22000000))
                .graphicsLayer {
                    alpha = overlayAlpha.value
                }
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(148.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = CircleShape,
                    ambientColor = Color(0x33FFFFFF),
                    spotColor = Color(0x22000000)
                )
                .graphicsLayer {
                    alpha = overlayAlpha.value * badgeAlpha.value
                    scaleX = logoScale.value
                    scaleY = logoScale.value
                }
                .clip(CircleShape)
                .background(Color(0xFFF7F4EE))
        )
        Image(
            painter = painterResource(id = R.drawable.fridge_foreground),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(88.dp)
                .graphicsLayer {
                    alpha = overlayAlpha.value * badgeAlpha.value
                    scaleX = logoScale.value
                    scaleY = logoScale.value
                }
        )
    }
}
