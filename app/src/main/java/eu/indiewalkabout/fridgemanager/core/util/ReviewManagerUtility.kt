package eu.indiewalkabout.fridgemanager.core.util

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.multidex.BuildConfig
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.google.android.play.core.review.testing.FakeReviewManager
import eu.indiewalkabout.fridgemanager.FreddyFridgeApp
import eu.indiewalkabout.fridgemanager.core.data.locals.AppPreferences
import kotlinx.coroutines.launch


object ReviewManagerUtil {
    private const val LAUNCH_THRESHOLD = 20
    private const val TAG = "ReviewManagerUtil"

    fun incrementLaunchCount() {
        AppPreferences.app_opening_counter += 1
    }

    private fun shouldRequestReview(): Boolean {
        return AppPreferences.app_opening_counter >= LAUNCH_THRESHOLD
    }

    private fun resetLaunchCount() {
        AppPreferences.app_opening_counter = 0
    }

    fun requestReviewIfEligible(activity: ComponentActivity) {
        if (!shouldRequestReview()) return

        val manager = ReviewManagerFactory.create(activity)
        // val manager = FakeReviewManager(FreddyFridgeApp.appContext)


        activity.lifecycleScope.launch {
            try {
                val request = manager.requestReviewFlow()
                request.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val reviewInfo = task.result
                        val flow = manager.launchReviewFlow(activity, reviewInfo)
                        flow.addOnCompleteListener { _ ->
                            // The flow has finished. The API does not indicate whether the user
                            // reviewed or not, or even whether the review dialog was shown. Thus, no
                            // matter the result, we continue our app flow.
                            Log.d(TAG, "Review flow completed")
                        }
                    } else {
                        // There was some problem, log or handle the error code.
                        @ReviewErrorCode val reviewErrorCode =
                            (task.getException() as ReviewException).errorCode
                        Log.d(TAG, "Review flow ERROR: $reviewErrorCode")
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "Review flow ERROR: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
