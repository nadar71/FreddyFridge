package eu.indiewalkabout.fridgemanager.util

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View

import com.google.ads.consent.ConsentForm
import com.google.ads.consent.ConsentFormListener
import com.google.ads.consent.ConsentInfoUpdateListener
import com.google.ads.consent.ConsentInformation
import com.google.ads.consent.ConsentStatus
import com.google.ads.consent.DebugGeography
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

import java.net.MalformedURLException
import java.net.URL

import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.util.ConsentSDK.Companion.nonPersonalizedAdsBundle

class ConsentSDK {

    private var context: Context? = null
    private var form: ConsentForm? = null
    private var LOG_TAG = "ID_LOG"
    private var DEVICE_ID = ""
    private var DEBUG = false
    private var privacyURL: String? = null
    private var publisherId: String? = null
    var consentSDK: ConsentSDK

    private var settings: SharedPreferences? = null

    abstract class ConsentCallback {
        abstract fun onResult(isRequestLocationInEeaOrUnknown: Boolean)
    }

    abstract class ConsentStatusCallback {
        abstract fun onResult(isRequestLocationInEeaOrUnknown: Boolean, isConsentPersonalized: Int)
    }

    abstract class ConsentInformationCallback {
        abstract fun onResult(consentInformation: ConsentInformation, consentStatus: ConsentStatus)
        abstract fun onFailed(consentInformation: ConsentInformation, reason: String)
    }

    abstract class LocationIsEeaOrUnknownCallback {
        abstract fun onResult(isRequestLocationInEeaOrUnknown: Boolean)
    }


    // Builder class
    class Builder// Initialize Builder
    (private val context: Context) {
        private var LOG_TAG = "ID_LOG"
        private var DEVICE_ID = ""
        private var DEBUG = false
        private var privacyURL: String? = null
        private var publisherId: String? = null

        // Add test device id
        fun addTestDeviceId(device_id: String): Builder {
            this.DEVICE_ID = device_id
            this.DEBUG = true
            return this
        }

        // Add privacy policy
        fun addPrivacyPolicy(privacyURL: String): Builder {
            this.privacyURL = privacyURL
            return this
        }

        // Add Publisher Id
        fun addPublisherId(publisherId: String): Builder {
            this.publisherId = publisherId
            return this
        }

        // Add Logcat id
        fun addCustomLogTag(LOG_TAG: String): Builder {
            this.LOG_TAG = LOG_TAG
            return this
        }

        // Build
        fun build(): ConsentSDK {
            if (this.DEBUG) {
                val consentSDK = ConsentSDK(context, publisherId, privacyURL, true)
                consentSDK.LOG_TAG = this.LOG_TAG
                consentSDK.DEVICE_ID = this.DEVICE_ID
                return consentSDK
            } else {
                return ConsentSDK(context, publisherId, privacyURL)
            }
        }
    }

    // Initialize debug
    constructor(context: Context, publisherId: String?, privacyURL: String?, DEBUG: Boolean) {
        this.context = context
        this.settings = initPreferences(context)
        this.publisherId = publisherId
        this.privacyURL = privacyURL
        this.DEBUG = DEBUG
        this.consentSDK = this
    }

    // Initialize production
    constructor(context: Context, publisherId: String?, privacyURL: String?) {
        this.context = context
        this.settings = context.getSharedPreferences(preferences_name, Context.MODE_PRIVATE)
        this.publisherId = publisherId
        this.privacyURL = privacyURL
        this.consentSDK = this
    }

    // Consent is personalized
    private fun consentIsPersonalized() {
        settings!!.edit().putBoolean(ads_preference, PERSONALIZED).apply()
    }

    // Consent is non personalized
    fun consentIsNonPersonalized() {
        settings!!.edit().putBoolean(ads_preference, NON_PERSONALIZED).apply()
    }

    // Consent is within
    fun updateUserStatus(status: Boolean) {
        settings!!.edit().putBoolean(user_status, status).apply()
    }

    // Consent information
    private fun initConsentInformation(callback: ConsentInformationCallback?) {
        val consentInformation = ConsentInformation.getInstance(context)
        if (DEBUG) {
            if (!DEVICE_ID.isEmpty()) {
                consentInformation.addTestDevice(DEVICE_ID)
            }
            consentInformation.debugGeography = DebugGeography.DEBUG_GEOGRAPHY_EEA
        }
        val publisherIds = arrayOf<String?>(publisherId)
        consentInformation.requestConsentInfoUpdate(publisherIds, object : ConsentInfoUpdateListener {
            override fun onConsentInfoUpdated(consentStatus: ConsentStatus) {
                callback?.onResult(consentInformation, consentStatus)
            }

            override fun onFailedToUpdateConsentInfo(reason: String) {
                callback!!.onFailed(consentInformation, reason)
            }
        })
    }

    // Check if the location is EEA
    fun isRequestLocationIsEeaOrUnknown(callback: LocationIsEeaOrUnknownCallback) {
        // Get Consent information
        initConsentInformation(object : ConsentInformationCallback() {
            override fun onResult(consentInformation: ConsentInformation, consentStatus: ConsentStatus) {
                callback.onResult(consentInformation.isRequestLocationInEeaOrUnknown)
            }

            override fun onFailed(consentInformation: ConsentInformation, reason: String) {
                callback.onResult(false)
            }
        })
    }

    // Initialize Consent SDK
    fun checkConsent(callback: ConsentCallback) {
        // Initialize consent information
        initConsentInformation(object : ConsentInformationCallback() {
            override fun onResult(consentInformation: ConsentInformation, consentStatus: ConsentStatus) {
                // Switch consent
                when (consentStatus) {
                    ConsentStatus.UNKNOWN -> {
                        // Debugging
                        if (DEBUG) {
                            Log.d(LOG_TAG, "Unknown Consent")
                            Log.d(LOG_TAG, "User location within EEA: " + consentInformation.isRequestLocationInEeaOrUnknown)
                        }
                        // Check the user status
                        if (consentInformation.isRequestLocationInEeaOrUnknown) {
                            requestConsent(object : ConsentStatusCallback() {
                                override fun onResult(isRequestLocationInEeaOrUnknown: Boolean, isConsentPersonalized: Int) {
                                    callback.onResult(isRequestLocationInEeaOrUnknown)
                                }
                            })
                        } else {
                            consentIsPersonalized()
                            // Callback
                            callback.onResult(consentInformation.isRequestLocationInEeaOrUnknown)
                        }
                    }
                    ConsentStatus.NON_PERSONALIZED -> {
                        consentIsNonPersonalized()
                        // Callback
                        callback.onResult(consentInformation.isRequestLocationInEeaOrUnknown)
                    }
                    else -> {
                        consentIsPersonalized()
                        // Callback
                        callback.onResult(consentInformation.isRequestLocationInEeaOrUnknown)
                    }
                }
                // Update user status
                updateUserStatus(consentInformation.isRequestLocationInEeaOrUnknown)
            }

            override fun onFailed(consentInformation: ConsentInformation, reason: String) {
                if (DEBUG) {
                    Log.d(LOG_TAG, "Failed to update: \$reason")
                }
                // Update user status
                updateUserStatus(consentInformation.isRequestLocationInEeaOrUnknown)
                // Callback
                callback.onResult(consentInformation.isRequestLocationInEeaOrUnknown)
            }
        })
    }

    // Request Consent
    fun requestConsent(callback: ConsentStatusCallback?) {
        var privacyUrl: URL? = null
        try {
            privacyUrl = URL(privacyURL)
        } catch (e: MalformedURLException) {
            //            e.printStackTrace();
        }

        form = ConsentForm.Builder(context, privacyUrl)
                .withListener(object : ConsentFormListener() {
                    override fun onConsentFormLoaded() {
                        if (DEBUG) {
                            Log.d(LOG_TAG, "Consent Form is loaded!")
                        }
                        form!!.show()
                    }

                    override fun onConsentFormError(reason: String?) {
                        if (DEBUG) {
                            Log.d(LOG_TAG, "Consent Form ERROR: \$reason")
                        }
                        // Callback on Error
                        if (callback != null) {
                            consentSDK.isRequestLocationIsEeaOrUnknown(object : LocationIsEeaOrUnknownCallback() {
                                override fun onResult(isRequestLocationInEeaOrUnknown: Boolean) {
                                    callback.onResult(isRequestLocationInEeaOrUnknown, -1)
                                }
                            })
                        }
                    }

                    override fun onConsentFormOpened() {
                        if (DEBUG) {
                            Log.d(LOG_TAG, "Consent Form is opened!")
                        }
                    }

                    override fun onConsentFormClosed(consentStatus: ConsentStatus?, userPrefersAdFree: Boolean?) {
                        if (DEBUG) {
                            Log.d(LOG_TAG, "Consent Form Closed!")
                        }
                        val isConsentPersonalized: Int
                        // Check the consent status and save it
                        when (consentStatus) {
                            ConsentStatus.NON_PERSONALIZED -> {
                                consentIsNonPersonalized()
                                isConsentPersonalized = 0
                            }
                            else -> {
                                consentIsPersonalized()
                                isConsentPersonalized = 1
                            }
                        }
                        // Callback
                        if (callback != null) {
                            consentSDK.isRequestLocationIsEeaOrUnknown(object : LocationIsEeaOrUnknownCallback() {
                                override fun onResult(isRequestLocationInEeaOrUnknown: Boolean) {
                                    callback.onResult(isRequestLocationInEeaOrUnknown, isConsentPersonalized)
                                }
                            })
                        }
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .build()
        form!!.load()
    }

    companion object {

        private val ads_preference = "ads_preference"
        private val user_status = "user_status"
        private val PERSONALIZED = true
        private val NON_PERSONALIZED = false
        private val preferences_name = "com.ayoubfletcher.consentsdk"

        // Admob banner test id
        private val DUMMY_BANNER = "ca-app-pub-3940256099942544/6300978111"

        // Initialize dummy banner
        fun initDummyBanner(context: Context) {
            val adView = AdView(context)
            adView.adSize = AdSize.BANNER
            adView.adUnitId = DUMMY_BANNER
            adView.loadAd(AdRequest.Builder().build())
        }

        // Initialize SharedPreferences
        private fun initPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(preferences_name, Context.MODE_PRIVATE)
        }

        // Consent status
        fun isConsentPersonalized(context: Context): Boolean {
            val settings = initPreferences(context)
            return settings.getBoolean(ads_preference, PERSONALIZED)
        }

        // -----------------------------------------------------------------------------------------
        // Init admob
        // Sample AdMob app ID:         ca-app-pub-3940256099942544~3347511713
        // THIS APP REAL AdMob app ID:  ca-app-pub-8846176967909254~3156345913
        // -----------------------------------------------------------------------------------------
        // Get AdRequest
        fun getAdRequest(context: Context): AdRequest {
            if (isConsentPersonalized(context)) {
                MobileAds.initialize(context, context.getString(R.string.admob_key_app_id))
                return AdRequest.Builder()
                        // .addTestDevice("7DC1A1E8AEAD7908E42271D4B68FB270")
                        .build()
            } else {
                MobileAds.initialize(context, context.getString(R.string.admob_key_app_id))
                return AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter::class.java, nonPersonalizedAdsBundle)
                        // .addTestDevice("7DC1A1E8AEAD7908E42271D4B68FB270")
                        .build()
            }
        }

        // Get Non Personalized Ads Bundle
        private val nonPersonalizedAdsBundle: Bundle
            get() {
                val extras = Bundle()
                extras.putString("npa", "1")
                return extras
            }

        // Check the user location
        fun isUserLocationWithinEea(context: Context): Boolean {
            return initPreferences(context).getBoolean(user_status, false)
        }
    }

}

