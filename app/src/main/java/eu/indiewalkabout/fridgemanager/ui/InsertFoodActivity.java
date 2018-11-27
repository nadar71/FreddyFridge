package eu.indiewalkabout.fridgemanager.ui;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import eu.indiewalkabout.fridgemanager.R;

public class InsertFoodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_food);

        // get the toolbar
        Toolbar foodInsertToolbar = (Toolbar) findViewById(R.id.insert_food_toolbar);
        foodInsertToolbar.setTitle(R.string.insertFoodActivity_title);

        // place toolbar in place of action bar
        setSupportActionBar(foodInsertToolbar);


        // get a support action bar
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }



    // ---------------------------------------------------------------------------------------------
    //                                          MENU STUFF
    // ---------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // When the home button is pressed, take the user back to Home
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
