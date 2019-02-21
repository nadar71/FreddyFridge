package eu.indiewalkabout.fridgemanager.ui;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdView;
import com.hlab.fabrevealmenu.enums.Direction;
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener;
import com.hlab.fabrevealmenu.view.FABRevealMenu;

import eu.indiewalkabout.fridgemanager.R;
import eu.indiewalkabout.fridgemanager.reminder.ReminderScheduler;
import eu.indiewalkabout.fridgemanager.util.ConsentSDK;
import eu.indiewalkabout.fridgemanager.util.PreferenceUtility;

public class MainSettingsActivity extends AppCompatActivity
        implements OnFABMenuSelectedListener {


    public static final String TAG = MainSettingsActivity.class.getName();

    private Toolbar foodInsertToolbar;
    private FABRevealMenu  fabMenu;

    private static String dayBeforeKey,hoursFreqKey;

    // admob banner ref
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_settings);

        mAdView = findViewById(R.id.adView);

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.getAdRequest(MainSettingsActivity.this));

        // init toolbar
        toolBarInit();

        // navigation fab
        addRevealFabBtn();
    }


    public static class MainPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{

        private ConsentSDK consentSDK = null;
        private Context context;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            dayBeforeKey = getString(R.string.days_before_deadline_count);
            hoursFreqKey = getString(R.string.hours_freq_today_deadline_count);

            // show and keep update the preferences
            addPreferencesFromResource(R.xml.main_settings);

            // get preference Screen reference
            PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(getActivity());

            // bind prefs on changes
            Preference dayBeforePref = findPreference(dayBeforeKey);
            bindPreferenceSummaryToValue(dayBeforePref);

            Preference hoursFreq     = findPreference(hoursFreqKey);
            bindPreferenceSummaryToValue(hoursFreq);

            Preference gdprConsentBtn = findPreference(getString(R.string.gdpr_btn_key));

            // Initialize ConsentSDK
            initConsentSDK(getActivity());

            // Checking the status of the user
            if(ConsentSDK.isUserLocationWithinEea(getActivity())) {
                String choice = ConsentSDK.isConsentPersonalized(getActivity())? "Personalize": "Non-Personalize";
                Log.i(TAG, "onCreate: consent choice : "+choice);

                gdprConsentBtn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        // Check Consent SDK
                        // Request the consent without callback
                        // consentSDK.requestConsent(null);
                        //To get the result of the consent
                        consentSDK.requestConsent(new ConsentSDK.ConsentStatusCallback() {
                            @Override
                            public void onResult(boolean isRequestLocationInEeaOrUnknown, int isConsentPersonalized) {
                                String choice = "";
                                switch (isConsentPersonalized) {
                                    case 0:
                                        choice = "Non-Personalize";
                                        break;
                                    case 1:
                                        choice = "Personalized";
                                        break;
                                    case -1:
                                        choice = "Error occurred";
                                }
                                Log.i(TAG, "onCreate: consent choice : "+choice);
                            }

                        });

                        return true;
                    }
                });

            } else {
                preferenceScreen.removePreference(gdprConsentBtn);;
            }



        }


        /**
         * Bind prefs text shown below label on prefs changes
         * @param preference
         */
        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);  // bind

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());

            // get new value to use for replacing old
            String sPreference = sharedPreferences.getString(preference.getKey(),"");

            // callback invokation on preference param
            onPreferenceChange(preference,sPreference);


        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String sValue = newValue.toString();

            if (preference instanceof ListPreference){
                ListPreference listPreference = (ListPreference) preference;
                int prefindex = ((ListPreference) preference).findIndexOfValue(sValue);
                if(prefindex >= 0){
                    CharSequence[] labels = listPreference.getEntries();

                    if (preference.getKey() == hoursFreqKey ) {
                        preference.setSummary("Notify every "+ labels[prefindex] + " hours");
                    } else {
                        preference.setSummary(labels[prefindex]);
                    }

                }

            }else{

                preference.setSummary(sValue);

            }

            // Relaunch job scheduler in case
            if ( (preference.getKey() == dayBeforeKey ) || (preference.getKey() == hoursFreqKey )){
                context = getContext();
                ReminderScheduler.scheduleChargingReminder(context);
            }

            return true;
        }



        /**
         * -----------------------------------------------------------------------------------------
         * Initialize consent
         * @param context
         * -----------------------------------------------------------------------------------------
         */
        private void initConsentSDK(Context context) {
            // Initialize ConsentSDK
            consentSDK = new ConsentSDK.Builder(context)
                    .addTestDeviceId("7DC1A1E8AEAD7908E42271D4B68FB270") // Add your test device id "Remove addTestDeviceId on production!"
                    .addCustomLogTag("gdpr_TAG") // Add custom tag default: ID_LOG
                    .addPrivacyPolicy("http://www.indie-walkabout.eu/privacy-policy-app") // Add your privacy policy url
                    .addPublisherId("pub-8846176967909254") // Add your admob publisher id
                    .build();
        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Toolbar init
     * ---------------------------------------------------------------------------------------------
     */
    private void toolBarInit(){
        // get the toolbar
        foodInsertToolbar = (Toolbar) findViewById(R.id.main_settings_toolbar);
        foodInsertToolbar.setTitle(R.string.mainSettingsActivity_title);

        // place toolbar in place of action bar
        setSupportActionBar(foodInsertToolbar);

        // get a support action bar
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * ---------------------------------------------------------------------------------------------
     *                                          MENU STUFF
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // When the home button is pressed, take the user back to Home
        if (id == android.R.id.home) {

            // go home
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        int hoursFrequency  = PreferenceUtility.getHoursCount(MainSettingsActivity.this);
    }


    // ---------------------------------------------------------------------------------------------
    //                                  REVEALING FAB BTN STUFF
    // ---------------------------------------------------------------------------------------------
    public void onBackPressed() {
        super.onBackPressed();
        if (fabMenu != null) {
            if (fabMenu.isShowing()) {
                fabMenu.closeMenu();

            }
        }
    }

    public FABRevealMenu getFabMenu() {
        return fabMenu;
    }

    public void setFabMenu(FABRevealMenu fabMenu) {
        this.fabMenu = fabMenu;
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Adding revealing main_fab button
     * ---------------------------------------------------------------------------------------------
     */
    private void addRevealFabBtn(){
        final FloatingActionButton fab = findViewById(R.id.settings_fab);
        final FABRevealMenu fabMenu    = findViewById(R.id.settings_fabMenu);

        // remove insert menu item
        fabMenu.removeItem(R.id.menu_insert);

        try {
            if (fab != null && fabMenu != null) {
                setFabMenu(fabMenu);

                //attach menu to main_fab
                fabMenu.bindAnchorView(fab);

                //set menu selection listener
                fabMenu.setOnFABMenuSelectedListener(MainSettingsActivity.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        fabMenu.setMenuDirection(Direction.LEFT);

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Revealing main_fab button menu management
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    public void onMenuItemSelected(View view, int id) {

        if (id == R.id.menu_insert) {
            Intent toInsertFood = new Intent(MainSettingsActivity.this, InsertFoodActivity.class);
            startActivity(toInsertFood);

        } else if (id == R.id.menu_expiring_food) {
            Intent showExpiringFood = new Intent(MainSettingsActivity.this, FoodListActivity.class);
            showExpiringFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_EXPIRING);
            startActivity(showExpiringFood);

        } else if (id == R.id.menu_consumed_food) {
            Intent showSavedFood = new Intent(MainSettingsActivity.this, FoodListActivity.class);
            showSavedFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_SAVED);
            startActivity(showSavedFood);

        } else if (id == R.id.menu_dead_food) {
            Intent showDeadFood = new Intent(MainSettingsActivity.this, FoodListActivity.class);
            showDeadFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_DEAD);
            startActivity(showDeadFood);

        } else if (id == R.id.menu_home) {
            Intent returnHome = new Intent(MainSettingsActivity.this, MainActivity.class);
            startActivity(returnHome);
        }
    }
}
