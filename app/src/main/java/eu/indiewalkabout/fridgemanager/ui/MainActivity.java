package eu.indiewalkabout.fridgemanager.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.CircleMenuRectMain;
import com.hitomi.cmlibrary.OnMenuSelectedListener;
import com.hitomi.cmlibrary.OnMenuStatusChangeListener;

import eu.indiewalkabout.fridgemanager.R;
import eu.indiewalkabout.fridgemanager.data.FoodDatabase;
import eu.indiewalkabout.fridgemanager.util.ConsentSDK;


public class MainActivity extends AppCompatActivity {

    // admob banner ref
    private AdView mAdView;

    // UI item reference
    private CircleMenu circleMenu;
    private CircleMenuRectMain circleMenuRect;
    private Button temporarySettingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initialize ConsentSDK
        ConsentSDK consentSDK = new ConsentSDK.Builder(this)
                .addTestDeviceId("7DC1A1E8AEAD7908E42271D4B68FB270") // redminote 5 // Add your test device id "Remove addTestDeviceId on production!"
                // .addTestDeviceId("9978A5F791A259430A0156313ED9C6A2")
                .addCustomLogTag("gdpr_TAG") // Add custom tag default: ID_LOG
                .addPrivacyPolicy("http://www.indie-walkabout.eu/privacy-policy-app") // Add your privacy policy url
                .addPublisherId("pub-8846176967909254") // Add your admob publisher id
                .build();


        // To check the consent and load ads
        consentSDK.checkConsent(new ConsentSDK.ConsentCallback() {
            @Override
            public void onResult(boolean isRequestLocationInEeaOrUnknown) {
                Log.i("gdpr_TAG", "onResult: isRequestLocationInEeaOrUnknown : "+isRequestLocationInEeaOrUnknown);
                // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
                mAdView.loadAd(ConsentSDK.getAdRequest(MainActivity.this));
            }
        });


        temporarySettingsBtn = findViewById(R.id.settingsBtn);
        temporarySettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(getApplicationContext(), MainSettingsActivity.class);
                startActivity(settingsIntent);
            }
        });

        mAdView = findViewById(R.id.adView);

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.getAdRequest(MainActivity.this));



        // -----------------------------------------------------------------------------------------
        // Costum UI
        // -----------------------------------------------------------------------------------------
        // Log.d("MainActivity", "Before getting circleMenu from R");

        circleMenu = (CircleMenu) findViewById(R.id.circle_menu); //

        // Log.d("MainActivity", "Before circleMenu.setMainMenu");

        //circleMenu.setMainMenu(Color.parseColor("#CDCDCD"), R.mipmap.icon_menu, R.mipmap.icon_cancel)
        circleMenu.setMainMenu(Color.parseColor("#740001"), R.drawable.ic_main_menu_icon, R.mipmap.icon_cancel)
                  .addSubMenu(Color.parseColor("#258CFF"), R.mipmap.icon_home)
                  .addSubMenu(Color.parseColor("#30A400"), R.mipmap.icon_search)
                  .addSubMenu(Color.parseColor("#FF4B32"), R.mipmap.icon_notify)
                  .addSubMenu(Color.parseColor("#FF4B32"), R.mipmap.icon_notify)
                  //.addSubMenu(Color.parseColor("#8A39FF"), R.mipmap.icon_setting)
                  //.addSubMenu(Color.parseColor("#FF6A00"), R.mipmap.icon_gps)
                  .setOnMenuSelectedListener(new OnMenuSelectedListener() {


                    @Override
                    public void onMenuSelected(int index) { // btn numbered in clockwise direction from top
                        switch(index){
                            case 0 :
                                Log.i("Insert New Food ", String.valueOf(index));
                                Intent toInsertFood = new Intent(MainActivity.this, InsertFoodActivity.class);
                                startActivity(toInsertFood);
                                break;


                            case 1 :
                                Log.i("Show Expiring Food ", String.valueOf(index));
                                Intent showExpiringFood = new Intent(MainActivity.this, FoodListActivity.class);
                                showExpiringFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_EXPIRING);
                                startActivity(showExpiringFood);
                                break;

                            case 2 :
                                Log.i("Show SAVED Food", String.valueOf(index));
                                Intent showSavedFood = new Intent(MainActivity.this, FoodListActivity.class);
                                showSavedFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_SAVED);
                                startActivity(showSavedFood);
                                break;

                            case 3 :
                                Log.i("Show DEAD Food", String.valueOf(index));
                                Intent showDeadFood = new Intent(MainActivity.this, FoodListActivity.class);
                                showDeadFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_DEAD);
                                startActivity(showDeadFood);
                                break;


                            default:
                                break;
                        }
                    }

                }).setOnMenuStatusChangeListener(new OnMenuStatusChangeListener() {

            @Override
            public void onMenuOpened() {}

            @Override
            public void onMenuClosed() {}

        });

        /*
        // variante con menu rettangolare
        circleMenuRect = (CircleMenuRectMain) findViewById(R.id.circle_menu_rect); //

        // Log.d("MainActivity", "Before circleMenu.setMainMenu");

        //circleMenu.setMainMenu(Color.parseColor("#CDCDCD"), R.mipmap.icon_menu, R.mipmap.icon_cancel)
        circleMenuRect.setMainMenu(Color.parseColor("#740001"), R.drawable.ic_main_menu_icon, R.mipmap.icon_cancel)
                .addSubMenu(Color.parseColor("#258CFF"), R.mipmap.icon_home)
                .addSubMenu(Color.parseColor("#30A400"), R.mipmap.icon_search)
                .addSubMenu(Color.parseColor("#FF4B32"), R.mipmap.icon_notify)
                //.addSubMenu(Color.parseColor("#8A39FF"), R.mipmap.icon_setting)
                //.addSubMenu(Color.parseColor("#FF6A00"), R.mipmap.icon_gps)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {

                    @Override
                    public void onMenuSelected(int index) {}

                }).setOnMenuStatusChangeListener(new OnMenuStatusChangeListener() {

            @Override
            public void onMenuOpened() {}

            @Override
            public void onMenuClosed() {}

        });
        */
    }

    // ---------------------------------------------------------------------------------------------
    //                                          MENU STUFF
    // ---------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainactivity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            Intent settingsIntent = new Intent(this, MainSettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
