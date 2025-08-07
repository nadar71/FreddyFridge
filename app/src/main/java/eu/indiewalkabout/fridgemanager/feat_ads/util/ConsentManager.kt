package eu.indiewalkabout.fridgemanager.feat_ads.util

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

object ConsentManager {
    private var consentInformation: ConsentInformation? = null

    fun requestConsent(
        context: Context,
        activity: Activity?,
        onConsentReady: (Boolean) -> Unit
    ) {
        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        consentInformation = UserMessagingPlatform.getConsentInformation(context)
        consentInformation?.requestConsentInfoUpdate(
            activity,
            params,
            {
                if (consentInformation?.isConsentFormAvailable == true) {
                    loadAndShowForm(context, onConsentReady)
                } else {
                    onConsentReady(consentInformation?.canRequestAds() == true)
                }
            },
            {
                onConsentReady(false) // Consent failed
            }
        )
    }

    private fun loadAndShowForm(context: Context, onConsentReady: (Boolean) -> Unit) {
        UserMessagingPlatform.loadConsentForm(
            context,
            { form ->
                val status = consentInformation?.consentStatus
                if (status == ConsentInformation.ConsentStatus.REQUIRED) {
                    form.show(context as Activity) {
                        onConsentReady(consentInformation?.canRequestAds() == true)
                    }
                } else {
                    onConsentReady(consentInformation?.canRequestAds() == true)
                }
            },
            {
                onConsentReady(false)
            }
        )
    }
}
