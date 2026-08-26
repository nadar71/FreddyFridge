package eu.indiewalkabout.fridgemanager.feat_ads.util

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

object ConsentManager {
    private var consentInformation: ConsentInformation? = null
    var isPrivacyOptionsRequired by mutableStateOf(false)
        private set

    fun requestConsent(
        activity: Activity,
        onConsentReady: (Boolean) -> Unit
    ) {
        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        consentInformation = UserMessagingPlatform.getConsentInformation(activity)
        consentInformation?.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                    updatePrivacyOptionsRequirement()
                    onConsentReady(consentInformation?.canRequestAds() == true)
                }
            },
            {
                updatePrivacyOptionsRequirement()
                onConsentReady(consentInformation?.canRequestAds() == true)
            }
        )
    }

    fun showPrivacyOptions(activity: Activity, onComplete: () -> Unit = {}) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) {
            updatePrivacyOptionsRequirement()
            onComplete()
        }
    }

    private fun updatePrivacyOptionsRequirement() {
        isPrivacyOptionsRequired = consentInformation?.privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }
}
