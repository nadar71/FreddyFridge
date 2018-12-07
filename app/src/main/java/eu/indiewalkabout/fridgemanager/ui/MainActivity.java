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


        temporarySettingsBtn = findViewById(R.id.settingsBtn);
        temporarySettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(getApplicationContext(), MainSettingsActivity.class);
                startActivity(settingsIntent);
            }
        });

        // -----------------------------------------------------------------------------------------
        // Init admob
        // Sample AdMob banner ID:         ca-app-pub-3940256099942544~3347511713
        // THIS APP REAL AdMob banner ID:  ca-app-pub-8846176967909254~3156345913
        // -----------------------------------------------------------------------------------------
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        // load ads banner
        mAdView = findViewById(R.id.adView);

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("7DC1A1E8AEAD7908E42271D4B68FB270")
                .build();
        mAdView.loadAd(adRequest);



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
