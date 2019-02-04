package eu.indiewalkabout.fridgemanager.ui;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.ads.AdView;

import eu.indiewalkabout.fridgemanager.R;
import eu.indiewalkabout.fridgemanager.util.ConsentSDK;

public class MainSettingsActivity extends AppCompatActivity {


    public static final String TAG = MainSettingsActivity.class.getName();

    private Toolbar foodInsertToolbar;

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
    }


    public static class MainPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{

        private ConsentSDK consentSDK = null;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            // show and keep update the preferences
            addPreferencesFromResource(R.xml.main_settings);

            // get preference Screen reference
            PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(getActivity());

            // bind prefs on changes
            Preference someValue = findPreference(getString(R.string.settings_some_value_key));
            bindPreferenceSummaryToValue(someValue);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

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
                    preference.setSummary(labels[prefindex]);
                }

            }else{

                preference.setSummary(sValue);

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

}
