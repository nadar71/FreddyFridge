package eu.indiewalkabout.fridgemanager.ui;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;


import java.util.Date;

import eu.indiewalkabout.fridgemanager.R;
import eu.indiewalkabout.fridgemanager.data.DateConverter;
import eu.indiewalkabout.fridgemanager.data.FoodDatabase;
import eu.indiewalkabout.fridgemanager.data.FoodEntry;


/**
 * ---------------------------------------------------------------------------------------------
 * InsertFoodActivity
 * Get a new food to keep monitored
 * ---------------------------------------------------------------------------------------------
 */
public class InsertFoodActivity extends AppCompatActivity {

    private final static String TAG = InsertFoodActivity.class.getSimpleName();

    // Views ref
    Button       save_btn;
    EditText     foodName_et;
    CalendarView dateExpir_cv;
    Toolbar      foodInsertToolbar;

    // Db reference
    FoodDatabase foodDb;

    /**
    * ---------------------------------------------------------------------------------------------
    * onCreate
    * @param savedInstanceState
    * ---------------------------------------------------------------------------------------------
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_food);

        // init db instance
        foodDb = FoodDatabase.getsDbInstance(getApplicationContext());

        // init views
        initViews();

        // init toolbar
        toolBarInit();



    }



    /**
    * ---------------------------------------------------------------------------------------------
    *  Views init
    * ---------------------------------------------------------------------------------------------
    */
    private void initViews(){
        save_btn     = findViewById(R.id.save_btn);
        foodName_et  = findViewById(R.id.foodName_et);
        dateExpir_cv = findViewById(R.id.calendar_cv);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveBtnClicked();
            }
        });


    }


    /**
    * ---------------------------------------------------------------------------------------------
    * Toolbar init
    * ---------------------------------------------------------------------------------------------
    */
    private void toolBarInit(){
        // get the toolbar
        foodInsertToolbar = (Toolbar) findViewById(R.id.insert_food_toolbar);
        foodInsertToolbar.setTitle(R.string.insertFoodActivity_title);

        // place toolbar in place of action bar
        setSupportActionBar(foodInsertToolbar);

        // get a support action bar
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    /**
    * ---------------------------------------------------------------------------------------------
    * Save/Update Button
    * ---------------------------------------------------------------------------------------------
    */
    public void onSaveBtnClicked(){
        Log.d(TAG, "onSaveBtnClicked");
        String foodName     = foodName_et.getText().toString();
        Long   expiringDateL = dateExpir_cv.getDate();
        // convert for FoodEntry constructor
        Date   expiringDate  = DateConverter.toDate(expiringDateL);


        Log.d(TAG, "foodName : " + foodName);
        Log.d(TAG, "expiringDate : " + expiringDate);


        // create a new food obj and init with data inserted by user
        final FoodEntry foodEntry = new FoodEntry(foodName,expiringDate);

        // ----------------------------------------
        // Update db using executor
        // ----------------------------------------
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                foodDb.foodDbDao().insertFoodEntry(foodEntry);
                finish();

                /* TODO when update is implemented
                if(mTaskId == DEFAULT_TASK_ID) {     // save a new task
                    mDb.taskDao().insertTask(taskEntry);
                    finish();
                }else{                               // update a previous task
                    // set id to the task to update
                    taskEntry.setId(mTaskId);
                    // update task on db
                    mDb.taskDao().updateTask(taskEntry);
                }
                */
            }
        });
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
