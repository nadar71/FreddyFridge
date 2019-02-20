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

    /**
     * ---------------------------------------------------------------------------------------------
     * Constructor for {@link FoodListsViewModelFactory} : init the attributes with
     * LiveData<List<FoodEntry>>
     * @param foodDb
     * @param foodlistType
     * ---------------------------------------------------------------------------------------------
     */
    public FoodListsViewModel(FoodDatabase foodDb, String foodlistType) {
        Log.d(TAG, "Actively retrieving the collections from db");

        // choose the type of food list to load from db
        if (foodlistType.equals(FoodListActivity.FOOD_EXPIRING)) {
            Log.d(TAG, "setupAdapter: FOOD_TYPE : " + foodlistType);
            long dataNormalizedAtMidnight  =
                    DateUtility.getLocalMidnightFromNormalizedUtcDate(DateUtility.getNormalizedUtcMsForToday());
            foodEntries = foodDb.foodDbDao().loadAllFoodExpiring(dataNormalizedAtMidnight);

        }else if (foodlistType.equals(FoodListActivity.FOOD_SAVED)){
            Log.d(TAG, "setupAdapter: FOOD_TYPE : " + foodlistType);
            foodEntries = foodDb.foodDbDao().loadAllFoodSaved();

        }else if (foodlistType.equals(FoodListActivity.FOOD_DEAD)){
            Log.d(TAG, "setupAdapter: FOOD_TYPE : " + foodlistType);
            long dataNormalizedAtMidnight  =
                    DateUtility.getLocalMidnightFromNormalizedUtcDate(DateUtility.getNormalizedUtcMsForToday());
            foodEntries = foodDb.foodDbDao().loadAllFoodDead(dataNormalizedAtMidnight);

        }else if (foodlistType.equals(FoodListActivity.FOOD_EXPIRING_TODAY)){
            Log.d(TAG, "setupAdapter: FOOD_TYPE : " + foodlistType);
            long dataNormalizedAtMidnight  =
                    DateUtility.getLocalMidnightFromNormalizedUtcDate(DateUtility.getNormalizedUtcMsForToday());
            long previousDayDate = dataNormalizedAtMidnight - DateUtility.DAY_IN_MILLIS;
            long nextDayDate     = dataNormalizedAtMidnight + DateUtility.DAY_IN_MILLIS;
            foodEntries = foodDb.foodDbDao().loadFoodExpiringToday(previousDayDate,nextDayDate);
        }

    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Getter for LiveData<List<FoodEntry>> list
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    public LiveData<List<FoodEntry>> getFoodList() {
        return foodEntries;
    }

}