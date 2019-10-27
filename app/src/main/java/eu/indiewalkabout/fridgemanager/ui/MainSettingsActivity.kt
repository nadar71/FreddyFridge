package eu.indiewalkabout.fridgemanager.ui


import android.content.Context
import android.content.Intent
import android.os.Build
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView

import com.google.android.gms.ads.AdView
import com.hlab.fabrevealmenu.enums.Direction
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener
import com.hlab.fabrevealmenu.view.FABRevealMenu

import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.reminder.ReminderScheduler
import eu.indiewalkabout.fridgemanager.util.ConsentSDK
import eu.indiewalkabout.fridgemanager.util.PreferenceUtility

class MainSettingsActivity : AppCompatActivity(), OnFABMenuSelectedListener {

    private var SettingsToolbar: Toolbar? = null
    private var toolbarTitle: TextView? = null

    var fabMenu: FABRevealMenu? = null

    // admob banner ref
    private var mAdView: AdView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_settings)

        mAdView = findViewById(R.id.mAdView)

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView!!.loadAd(ConsentSDK.getAdRequest(this@MainSettingsActivity))

        // init toolbar
        toolBarInit()

        // navigation fab
        addRevealFabBtn()

        // make bottom navigation bar and status bar hide
        hideStatusNavBars()

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Make bottom navigation bar and status bar hide, without resize when reappearing
     * ---------------------------------------------------------------------------------------------
     */
    private fun hideStatusNavBars() {
        // minsdk version is 19, no need code for lower api
        val decorView = window.decorView

        // hide status bar
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else if (Build.VERSION.SDK_INT >= 16) {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        // hide navigation bar
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            val v = this.window.decorView
            v.systemUiVisibility = View.GONE
        } else if (Build.VERSION.SDK_INT >= 19) {
            val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            decorView.systemUiVisibility = uiOptions
        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Preferences screen manager class
     * ---------------------------------------------------------------------------------------------
     */
    class MainPreferenceFragment : PreferenceFragment(), Preference.OnPreferenceChangeListener {

        private var consentSDK: ConsentSDK? = null
        private var myContext: Context? = null

        override fun onCreate(savedInstanceState: Bundle?) {

            super.onCreate(savedInstanceState)

            dayBeforeKey = getString(R.string.days_before_deadline_count)
            hoursFreqKey = getString(R.string.hours_freq_today_deadline_count)

            // show and keep update the preferences
            addPreferencesFromResource(R.xml.main_settings)

            // get preference Screen reference
            val preferenceScreen = preferenceManager.createPreferenceScreen(activity)

            // bind prefs on changes
            val dayBeforePref = findPreference(dayBeforeKey)
            bindPreferenceSummaryToValue(dayBeforePref)

            val hoursFreq = findPreference(hoursFreqKey)
            bindPreferenceSummaryToValue(hoursFreq)

            val gdprConsentBtn = findPreference(getString(R.string.gdpr_btn_key))
            val creditsBtn = findPreference(getString(R.string.credits_btn_key))

            // Initialize ConsentSDK
            initConsentSDK(activity)

            // Checking the status of the user
            if (ConsentSDK.isUserLocationWithinEea(activity)) {
                val choice = if (ConsentSDK.isConsentPersonalized(activity)) "Personalize" else "Non-Personalize"
                Log.i(TAG, "onCreate: consent choice : $choice")

                gdprConsentBtn.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    // Check Consent SDK
                    // Request the consent without callback
                    // consentSDK.requestConsent(null);
                    //To get the result of the consent
                    consentSDK!!.requestConsent(object : ConsentSDK.ConsentStatusCallback() {
                        override fun onResult(isRequestLocationInEeaOrUnknown: Boolean, isConsentPersonalized: Int) {
                            var choice = ""
                            when (isConsentPersonalized) {
                                0 -> choice = "Non-Personalize"
                                1 -> choice = "Personalized"
                                -1 -> choice = "Error occurred"
                            }
                            Log.i(TAG, "onCreate: consent choice : $choice")
                        }

                    })

                    true
                }

            } else {
                preferenceScreen.removePreference(gdprConsentBtn)
            }


            // Faq button
            creditsBtn.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val showEqOnMap = Intent(activity, CreditsActivity::class.java)
                startActivity(showEqOnMap)
                true
            }


        }


        /**
         * Bind prefs text shown below label on prefs changes
         * @param preference
         */
        private fun bindPreferenceSummaryToValue(preference: Preference) {
            preference.onPreferenceChangeListener = this  // bind

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.context)

            // get new value to use for replacing old
            val sPreference = sharedPreferences.getString(preference.key, "")

            // callback invokation on preference param
            onPreferenceChange(preference, sPreference!!)


        }

        override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
            val sValue = newValue.toString()

            if (preference is ListPreference) {
                val prefindex = preference.findIndexOfValue(sValue)
                if (prefindex >= 0) {
                    val labels = preference.entries

                    if (preference.getKey() == hoursFreqKey) {
                        val tmp = (resources.getString(R.string.settings_notify_every_01)
                                + labels[prefindex]
                                + resources.getString(R.string.settings_notify_every_02))
                        preference.setSummary(Html.fromHtml(tmp))

                    } else {
                        preference.setSummary(labels[prefindex])
                    }

                }

            } else {

                preference.summary = sValue

            }

            // Relaunch job scheduler in case
            if (preference.key == dayBeforeKey || preference.key == hoursFreqKey) {
                myContext = preference.context
                ReminderScheduler.scheduleChargingReminder(myContext!!)
            }

            return true
        }


        /**
         * -----------------------------------------------------------------------------------------
         * Initialize consent
         * @param context
         * -----------------------------------------------------------------------------------------
         */
        private fun initConsentSDK(context: Context) {
            // Initialize ConsentSDK
            consentSDK = ConsentSDK.Builder(context)
                    // .addTestDeviceId("7DC1A1E8AEAD7908E42271D4B68FB270") // Add your test device id "Remove addTestDeviceId on production!"
                    .addCustomLogTag("gdpr_TAG") // Add custom tag default: ID_LOG
                    .addPrivacyPolicy("http://www.indie-walkabout.eu/privacy-policy-app") // Add your privacy policy url
                    .addPublisherId("pub-8846176967909254") // Add your admob publisher id
                    .build()
        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Toolbar init
     * ---------------------------------------------------------------------------------------------
     */
    private fun toolBarInit() {
        // get the toolbar
        SettingsToolbar = findViewById(R.id.main_settings_toolbar)
        toolbarTitle = SettingsToolbar!!.findViewById(R.id.settings_toolbar_title_tv)
        toolbarTitle!!.setText(R.string.mainSettingsActivity_title)

        // place toolbar in place of action bar
        setSupportActionBar(SettingsToolbar)

        // get a support action bar
        val actionBar = supportActionBar

        actionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * MENU STUFF
     * ---------------------------------------------------------------------------------------------
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        // When the home button is pressed, take the user back to Home
        if (id == android.R.id.home) {

            // go home
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        val hoursFrequency = PreferenceUtility.getHoursCount(this@MainSettingsActivity)
    }


    // ---------------------------------------------------------------------------------------------
    //                                  REVEALING FAB BTN STUFF
    // ---------------------------------------------------------------------------------------------
    override fun onBackPressed() {
        super.onBackPressed()
        if (fabMenu != null) {
            if (fabMenu!!.isShowing) {
                fabMenu!!.closeMenu()

            }
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Adding revealing main_fab button
     * ---------------------------------------------------------------------------------------------
     */
    private fun addRevealFabBtn() {
        val fab = findViewById<FloatingActionButton>(R.id.settings_fab)
        var fabMenu = findViewById<FABRevealMenu>(R.id.settings_fabMenu)

        // remove insert menu item
        fabMenu!!.removeItem(R.id.menu_insert)

        try {
            if (fab != null && fabMenu != null) {
                fabMenu = fabMenu

                //attach menu to main_fab
                fabMenu.bindAnchorView(fab)

                //set menu selection listener
                fabMenu.setOnFABMenuSelectedListener(this@MainSettingsActivity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        fabMenu!!.menuDirection = Direction.LEFT

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Revealing main_fab button menu management
     * ---------------------------------------------------------------------------------------------
     */
    override fun onMenuItemSelected(view: View, id: Int) {

        if (id == R.id.menu_insert) {
            val toInsertFood = Intent(this@MainSettingsActivity, InsertFoodActivity::class.java)
            startActivity(toInsertFood)

        } else if (id == R.id.menu_expiring_food) {
            val showExpiringFood = Intent(this@MainSettingsActivity, FoodListActivity::class.java)
            showExpiringFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_EXPIRING)
            startActivity(showExpiringFood)

        } else if (id == R.id.menu_consumed_food) {
            val showSavedFood = Intent(this@MainSettingsActivity, FoodListActivity::class.java)
            showSavedFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_SAVED)
            startActivity(showSavedFood)

        } else if (id == R.id.menu_dead_food) {
            val showDeadFood = Intent(this@MainSettingsActivity, FoodListActivity::class.java)
            showDeadFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_DEAD)
            startActivity(showDeadFood)

        } else if (id == R.id.menu_home) {
            val returnHome = Intent(this@MainSettingsActivity, MainActivity::class.java)
            startActivity(returnHome)
        }
    }

    companion object {


        val TAG = MainSettingsActivity::class.java.name

        private var dayBeforeKey: String? = null
        private var hoursFreqKey: String? = null
    }
}
