package eu.indiewalkabout.fridgemanager.ui;

import android.support.annotation.NonNull;
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
import android.widget.Toast;


import java.util.Date;

import eu.indiewalkabout.fridgemanager.R;
import eu.indiewalkabout.fridgemanager.data.DateConverter;
import eu.indiewalkabout.fridgemanager.data.FoodDatabase;
import eu.indiewalkabout.fridgemanager.data.FoodEntry;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;


/**
 * ---------------------------------------------------------------------------------------------
 * InsertFoodActivity
 * Get a new food to keep monitored
 * ---------------------------------------------------------------------------------------------
 */
public class InsertFoodActivity extends AppCompatActivity implements CalendarView.OnDateChangeListener {

    private final static String TAG = InsertFoodActivity.class.getSimpleName();

    // Views ref
    private Button       save_btn;
    private EditText     foodName_et;
    private CalendarView dateExpir_cv;
    private Toolbar      foodInsertToolbar;

    // Db reference
    private FoodDatabase foodDb;

    // Date picked up reference
    private Calendar datePicked;
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
        dateExpir_cv.setOnDateChangeListener(this);

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
     * Get the date picked up by the user from calendar
     * @param view
     * @param year
     * @param month
     * @param dayOfMonth
     */
    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
        Log.d(TAG, "onSelectedDayChange: done");
        datePicked = new GregorianCalendar(year, month, dayOfMonth);
    }


    /**
    * ---------------------------------------------------------------------------------------------
    * Save/Update Button
    * ---------------------------------------------------------------------------------------------
    */
    public void onSaveBtnClicked(){
        Log.d(TAG, "onSaveBtnClicked");
        String foodName     = foodName_et.getText().toString();
        //Long   expiringDateL = dateExpir_cv.getDate();
        // convert for FoodEntry constructor
        // Date   expiringDate  = DateConverter.toDate(expiringDateL);
        Date   expiringDate  = datePicked.getTime();


        Log.d(TAG, "foodName : "             + foodName);
        Log.d(TAG, "expiringDate : "         + expiringDate);
        Log.d(TAG, "datePicked.getTime() : " + datePicked.getTime());


        // create a new food obj and init with data inserted by user
        final FoodEntry foodEntry = new FoodEntry(0,foodName,expiringDate);

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
