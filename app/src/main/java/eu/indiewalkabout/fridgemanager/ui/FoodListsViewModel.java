package eu.indiewalkabout.fridgemanager.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import java.util.List;

import eu.indiewalkabout.fridgemanager.data.FoodDatabase;
import eu.indiewalkabout.fridgemanager.data.FoodEntry;
import eu.indiewalkabout.fridgemanager.util.DateUtility;

/**
 * -------------------------------------------------------------------------------------------------
 * ViewModel Class for retrieving all items in db, based on kind
 * -------------------------------------------------------------------------------------------------
 */
public class FoodListsViewModel extends ViewModel {

    // tag for logging
    private static final String TAG = FoodListsViewModel.class.getSimpleName();

    // Livedata var on foodEntry List to populate through ViewModel
    private LiveData<List<FoodEntry>> foodEntries;

    // Date utility class instance
    private DateUtility dateUtility;


    /**
     * ---------------------------------------------------------------------------------------------
     * Viewmodel class Constructor : init the attributes with dao
     * @param foodDb
     * @param foodlistType
     * ---------------------------------------------------------------------------------------------
     */
    public FoodListsViewModel(FoodDatabase foodDb, String foodlistType) {
        Log.d(TAG, "Actively retrieving the collections from db");

        // choose the type of food list to load from db
        if (foodlistType.equals(FoodListActivity.FOOD_EXPIRING)) {
            Log.d(TAG, "setupAdapter: FOOD_TYPE : " + foodlistType);
            foodEntries = foodDb.foodDbDao().loadAllFoodExpiring(dateUtility.getNormalizedUtcMsForToday());

        }else if (foodlistType.equals(FoodListActivity.FOOD_SAVED)){
            Log.d(TAG, "setupAdapter: FOOD_TYPE : " + foodlistType);
            foodEntries = foodDb.foodDbDao().loadAllFoodSaved();

        }else if (foodlistType.equals(FoodListActivity.FOOD_DEAD)){
            Log.d(TAG, "setupAdapter: FOOD_TYPE : " + foodlistType);
            foodEntries = foodDb.foodDbDao().loadAllFoodDead(dateUtility.getNormalizedUtcMsForToday());

        }

    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Public method to retrieve the selected foodList through ViewModel
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    public LiveData<List<FoodEntry>> getFoodList() {
        return foodEntries;
    }

}