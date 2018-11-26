package eu.indiewalkabout.fridgemanager.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import eu.indiewalkabout.fridgemanager.R;

public class ExpiringFoodListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expiring_food_list);
    }
}
