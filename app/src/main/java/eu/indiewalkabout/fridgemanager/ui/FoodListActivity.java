package eu.indiewalkabout.fridgemanager.ui;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import eu.indiewalkabout.fridgemanager.R;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;


/**
* ---------------------------------------------------------------------------------------------
* FoodListActivity
* Show list of food depending on FOOD_TYPE
* ---------------------------------------------------------------------------------------------
*/
public class FoodListActivity extends AppCompatActivity implements FoodListAdapter.ItemClickListener  {

    public static final String TAG = FoodListActivity.class.getSimpleName();

    // Views ref
    Toolbar         foodsListToolbar;
    RecyclerView    foodList;
    FoodListAdapter foodListAdapter;


    // get the type of list to show from intent content extra
    public static final String FOOD_TYPE     = "food_type";

    // expiring food flag
    public static final String FOOD_EXPIRING = "ExpiringFood";

    // saved food flag
    public static final String FOOD_SAVED    = "SavedFood";

    // dead food flag
    public static final String FOOD_DEAD     = "DeadFood";


    /**
    * ---------------------------------------------------------------------------------------------
    * onCreate
    * @param savedInstanceState
    * ---------------------------------------------------------------------------------------------
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        // init toolbar
        toolBarInit();


        // TODO : use this in another way
        // get intent extra for configuring list type
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(FOOD_TYPE)) {
            String foodType = intent.getStringExtra(FOOD_TYPE);
            Log.d(TAG, "onCreate: FOOD_TYPE : " + foodType);

            if (foodType.equals(FOOD_EXPIRING)) {
                foodsListToolbar.setTitle(R.string.foodExpiring_activity_title);
                Log.d(TAG, "onCreate: FOOD_TYPE : " + R.string.foodExpiring_activity_title);

            }else if (foodType.equals(FOOD_SAVED)){
                foodsListToolbar.setTitle(R.string.foodSaved_activity_title);
                Log.d(TAG, "onCreate: FOOD_TYPE : " + R.string.foodSaved_activity_title);

            }else if (foodType.equals(FOOD_DEAD)){
                foodsListToolbar.setTitle(R.string.foodDead_activity_title);
                Log.d(TAG, "onCreate: FOOD_TYPE : " + R.string.foodDead_activity_title);

            }
        }

        // init recycle view list
        // TODO : accepts a parameter for the type of list
        initList();
        
    }


    // Recycle touch an item callback to update/modify task
    @Override
    public void onItemClickListener(int itemId) {
        Log.d(TAG, "onItemClickListener: Item" + itemId + "touched.");

    }


    /**
    * ---------------------------------------------------------------------------------------------
    * Toolbar init
    * TODO : MAKE AN EXTERNAL UTIL FUNCTIONS
    * ---------------------------------------------------------------------------------------------
    */
    private void toolBarInit(){

        // get the toolbar
        foodsListToolbar = (Toolbar) findViewById(R.id.food_list_toolbar);

        // place toolbar in place of action bar
        setSupportActionBar(foodsListToolbar);

        // get a support action bar
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
    * ---------------------------------------------------------------------------------------------
    * Recycle view list init
    * ---------------------------------------------------------------------------------------------
    */
    private void initList(){
        // Set the RecyclerView's view
        foodList = (RecyclerView) findViewById(R.id.food_list_recycleView);

        if (foodList == null) {
            Log.d(TAG, "onCreate: foodList == null ");
        }

        // Set LinearLayout
        foodList.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        foodListAdapter = new FoodListAdapter(this, this);
        foodList.setAdapter(foodListAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        foodList.addItemDecoration(decoration);
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
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
