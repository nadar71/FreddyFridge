package eu.indiewalkabout.fridgemanager.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.CircleMenuRectMain;
import com.hitomi.cmlibrary.OnMenuSelectedListener;
import com.hitomi.cmlibrary.OnMenuStatusChangeListener;

import eu.indiewalkabout.fridgemanager.R;


public class MainActivity extends AppCompatActivity {

    private CircleMenu circleMenu;
    private CircleMenuRectMain circleMenuRect;
    // {Log.d("MainActivity", "After declaring instance of circleMenu");} // the constructors start soon after

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



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
}
