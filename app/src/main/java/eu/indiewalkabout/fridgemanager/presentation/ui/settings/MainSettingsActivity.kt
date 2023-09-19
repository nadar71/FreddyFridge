package eu.indiewalkabout.fridgemanager.presentation.ui.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.text.InputType
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent.*
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hlab.fabrevealmenu.enums.Direction
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener
import com.hlab.fabrevealmenu.view.FABRevealMenu
import eu.indiewalkabout.fridgemanager.App
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.reminder.withworkmanager.ReminderScheduler.scheduleChargingReminder
import eu.indiewalkabout.fridgemanager.presentation.ui.credits.CreditsActivity
import eu.indiewalkabout.fridgemanager.presentation.ui.food.FoodListActivity
import eu.indiewalkabout.fridgemanager.presentation.ui.food.InsertFoodActivity
import eu.indiewalkabout.fridgemanager.presentation.ui.intromain.MainActivity
import eu.indiewalkabout.fridgemanager.core.util.ConsentSDK
import eu.indiewalkabout.fridgemanager.core.util.ConsentSDK.Companion.getAdRequest
import eu.indiewalkabout.fridgemanager.core.util.ConsentSDK.Companion.isConsentPersonalized
import eu.indiewalkabout.fridgemanager.core.util.ConsentSDK.Companion.isUserLocationWithinEea
import eu.indiewalkabout.fridgemanager.core.util.ConsentSDK.ConsentStatusCallback
import eu.indiewalkabout.fridgemanager.core.util.GenericUtility
import eu.indiewalkabout.fridgemanager.core.util.GenericUtility.hideStatusNavBars
import eu.indiewalkabout.fridgemanager.core.util.GenericUtility.showRandomizedInterstAds
import eu.indiewalkabout.fridgemanager.core.util.PreferenceUtility.getHoursCount
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_settings.*
import kotlinx.android.synthetic.main.activity_main_settings.adView


// Settings configuration class; uses activity_main_settings layout and include settingsFrag
class MainSettingsActivity : AppCompatActivity(),
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback,
        OnFABMenuSelectedListener {

    companion object {
        val TAG = MainSettingsActivity::class.java.name
        private var dayBeforeKey: String? = null
        private var hoursFreqKey: String? = null
    }

    lateinit var SettingsToolbar: Toolbar
    lateinit var toolbarTitle: TextView
    var fabMenu: FABRevealMenu? = null


    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat, pref: Preference): Boolean {
        // Instantiate the new Fragment
        val args = pref.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
                classLoader,
                pref.fragment)
        fragment.arguments = args
        fragment.setTargetFragment(caller, 0)
        // Replace the existing Fragment with the new Fragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.settingsFrag, fragment)
                .addToBackStack(null)
                .commit()
        return true
    }

    // ---------------------------------------------------------------------------------------------
    // onCreate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_settings)

        // admob banner ref
        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        adView.loadAd(getAdRequest(this@MainSettingsActivity))

        // init toolbar
        toolBarInit()

        // navigation fab
        addRevealFabBtn()

        hideStatusNavBars(this)
    }


     // ---------------------------------------------------------------------------------------------
     // Preferences screen manager class which is used by  activity_main_settings.xml

    class MainPreferenceFragment : PreferenceFragmentCompat(),
            Preference.OnPreferenceChangeListener,
            SharedPreferences.OnSharedPreferenceChangeListener{

        lateinit var consentSDK: ConsentSDK
        val appPackageName: String = App.getsContext()?.packageName ?: "eu.indiewalkabout.fridgemanager"

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.main_settings, rootKey)
        }


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            dayBeforeKey = getString(R.string.days_before_deadline_count)
            hoursFreqKey = getString(R.string.hours_freq_today_deadline_count)

            // get preference Screen reference
            val preferenceScreen = preferenceManager.createPreferenceScreen(activity)

            preferenceScreen.sharedPreferences
                    .registerOnSharedPreferenceChangeListener(this)

            // bind prefs on changes
            val dayBeforePref: EditTextPreference? = findPreference(dayBeforeKey!!)
            // NB : forced to avoid error :
            // https://stackoverflow.com/questions/41123715/dialog-view-must-contain-an-edittext-with-id-androidid-edit
            dayBeforePref?.dialogLayoutResource = R.layout.dialog_preference_edittext
            dayBeforePref?.setOnBindEditTextListener { editText ->
                        editText.inputType = InputType.TYPE_CLASS_NUMBER
            }

            if (dayBeforePref != null) {
                bindPreferenceSummaryToValue(dayBeforePref)
            }

            val hoursFreq: Preference? = findPreference(hoursFreqKey!!)
            if (hoursFreq != null) {
                bindPreferenceSummaryToValue(hoursFreq)
            }

            val gdprConsentBtn: Preference? = findPreference(getString(R.string.gdpr_btn_key))
            val creditsBtn: Preference? = findPreference(getString(R.string.credits_btn_key))
            val notificationBtn: Preference? = findPreference(getString(R.string.app_settings_btn_key))
            val feedbackBtn: Preference? = findPreference(getString(R.string.review_btn_key))
            val myAppsBtn: Preference? = findPreference(getString(R.string.myapps_btn_key))
            val supportBtn: Preference? = findPreference(getString(R.string.support_btn_key))

            // Initialize ConsentSDK
            initConsentSDK(requireActivity())

            // Checking the status of the user
            if (isUserLocationWithinEea(requireActivity())) {
                val choice = if (isConsentPersonalized(requireActivity())) "Personalize" else "Non-Personalize"
                Log.i(TAG, "onCreate: consent choice : $choice")
                gdprConsentBtn?.onPreferenceClickListener = Preference.OnPreferenceClickListener { // Check Consent SDK
                    // Request the consent without callback
                    // consentSDK.requestConsent(null);
                    //To get the result of the consent
                    consentSDK.requestConsent(object : ConsentStatusCallback() {
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
            creditsBtn?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val showEqOnMap = Intent(activity, CreditsActivity::class.java)
                startActivity(showEqOnMap)
                true
            }

            // Go to notifications settings button
            notificationBtn?.onPreferenceClickListener = Preference.OnPreferenceClickListener{
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.setData(Uri.parse("package:" + getString(R.string.app_package)));
                startActivity(intent);
                true
            }

            // Goto app review in store
            feedbackBtn?.onPreferenceClickListener = Preference.OnPreferenceClickListener{
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                    Log.i(TAG, "feedback pressed, open google play store app ")
                } catch (anfe: ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                    Log.i(TAG, "feedback pressed, open google play browser page app ")
                }
                true
            }

            // Goto my apps in store
            myAppsBtn?.onPreferenceClickListener = Preference.OnPreferenceClickListener{
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://dev?id=9031358010900352978")))
                    Log.i(TAG, "my app pressed, open google play store app ")
                } catch (anfe: ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=9031358010900352978")))
                    Log.i(TAG, "my app  pressed, open google play browser page app ")
                }
                true
            }

            // Goto support, open mail
            supportBtn?.onPreferenceClickListener = Preference.OnPreferenceClickListener{
                try {
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.type = "message/rfc822"
                    intent.data = Uri.parse("mailto:") // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("indie.walkabout.1971@gmail.com"))
                    intent.putExtra(Intent.EXTRA_SUBJECT, "App feedback")
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    Toast.makeText(activity, "There are no email client installed on your device.", Toast.LENGTH_SHORT).show()
                }
                true
            }




        }

        /**
         * Bind prefs text shown below label on prefs changes
         * @param preference
         */
        private fun bindPreferenceSummaryToValue(preference: Preference) {
            preference.onPreferenceChangeListener = this // bind
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.context)

            // get new value to use for replacing old
            val sPreference = sharedPreferences.getString(preference.key, "")

            // callback invokation on preference param
            onPreferenceChange(preference, sPreference as Any)
        }

        // Update summary before writing on shared preferences
        override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
            val sValue = newValue.toString()
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.context)

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

            return true
        }


        // Update summary after writing on shared preferences
        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
            val preference: Preference? = findPreference(key!!)
            if (preference != null) {
                if (preference.key == dayBeforeKey) {
                    val value = sharedPreferences.getString(preference.key, "")
                    // days before timeline can't be null
                    if (value.equals("")){
                        preference.summary = "1"
                        with(sharedPreferences.edit()) {
                            putString(dayBeforeKey, "1")
                            apply()
                        }

                        GenericUtility.showGenericBlockingAlert(
                                getString(R.string.days_alert_dialog_title),
                                getString(R.string.days_alert_dialog_msg),
                                requireActivity())
                    }
                }

                // Relaunch job scheduler in case
                if (preference.key == dayBeforeKey || preference.key == hoursFreqKey) {
                    scheduleChargingReminder(preference.context)
                }
            }
        }

        // -----------------------------------------------------------------------------------------
        // Initialize consent
        // @param context

        private fun initConsentSDK(context: Context) {
            // Initialize ConsentSDK
            consentSDK = ConsentSDK.Builder(context) // .addTestDeviceId("7DC1A1E8AEAD7908E42271D4B68FB270") // Add your test device id "Remove addTestDeviceId on production!"
                    .addCustomLogTag("gdpr_TAG") // Add custom tag default: ID_LOG
                    .addPrivacyPolicy("http://www.indie-walkabout.eu/privacy-policy-app") // Add your privacy policy url
                    .addPublisherId("pub-8846176967909254") // Add your admob publisher id
                    .build()
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Toolbar init

    private fun toolBarInit() {
        // get the toolbar
        SettingsToolbar = findViewById(R.id.main_settings_toolbar)
        toolbarTitle = SettingsToolbar.findViewById(R.id.settings_toolbar_title_tv)
        toolbarTitle.setText(R.string.mainSettingsActivity_title)

        // place toolbar in place of action bar
        setSupportActionBar(SettingsToolbar)

        // get a support action bar
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
    }


    // ---------------------------------------------------------------------------------------------
    // MENU STUFF

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        // When the home button is pressed, take the user back to Home
        if (id == android.R.id.home) {
            showRandomizedInterstAds(4, this)
            // go home
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        // TODO : what this is for ?
        val hoursFrequency = getHoursCount(this@MainSettingsActivity)
    }

    // ---------------------------------------------------------------------------------------------
    //                                  REVEALING FAB BTN STUFF
    // ---------------------------------------------------------------------------------------------
    override fun onBackPressed() {
        super.onBackPressed()
        showRandomizedInterstAds(4,this)
        if (fabMenu != null) {
            if (fabMenu!!.isShowing) {
                fabMenu!!.closeMenu()
            }
        }
    }


    // ---------------------------------------------------------------------------------------------
    // Adding revealing main_fab button

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


     // ---------------------------------------------------------------------------------------------
     // Revealing main_fab button menu management

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




}