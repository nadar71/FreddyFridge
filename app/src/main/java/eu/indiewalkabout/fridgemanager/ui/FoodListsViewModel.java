package eu.indiewalkabout.fridgemanager.ui;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;

import java.util.List;

import eu.indiewalkabout.fridgemanager.FridgeManagerRepository;
import eu.indiewalkabout.fridgemanager.ApplicationProvider;
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

    // repository ref
    private final FridgeManagerRepository repository;

    /**
     * Standard FoodListsViewModel constructor; init repository
     */
    public FoodListsViewModel() {

        // get repository instance
        repository = ((ApplicationProvider) ApplicationProvider.getsContext()).getRepository();
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Constructor with parameter used by {@link FoodListsViewModelFactory}
     * : init the attributes with LiveData<List<FoodEntry>>
     * @param foodlistType
     * ---------------------------------------------------------------------------------------------
     */
    public FoodListsViewModel(String foodlistType) {
        Log.d(TAG, "Actively retrieving the collections from repository");

        // get repository instance
        repository = ((ApplicationProvider) ApplicationProvider.getsContext()).getRepository();

        // choose the type of food list to load from db
        if (foodlistType.equals(FoodListActivity.FOOD_EXPIRING)) {
            Log.d(TAG, "setupAdapter: FOOD_TYPE : " + foodlistType);
            long dataNormalizedAtMidnight  =
                    DateUtility.getLocalMidnightFromNormalizedUtcDate(DateUtility.getNormalizedUtcMsForToday());
            foodEntries = repository.loadAllFoodExpiring(dataNormalizedAtMidnight);

        }else if (foodlistType.equals(FoodListActivity.FOOD_SAVED)){
            Log.d(TAG, "setupAdapter: FOOD_TYPE : " + foodlistType);
            foodEntries = repository.loadAllFoodSaved();

        }else if (foodlistType.equals(FoodListActivity.FOOD_DEAD)){
            Log.d(TAG, "setupAdapter: FOOD_TYPE : " + foodlistType);
            long dataNormalizedAtMidnight  =
                    DateUtility.getLocalMidnightFromNormalizedUtcDate(DateUtility.getNormalizedUtcMsForToday());
            foodEntries = repository.loadAllFoodDead(dataNormalizedAtMidnight);

        }else if (foodlistType.equals(FoodListActivity.FOOD_EXPIRING_TODAY)){
            Log.d(TAG, "setupAdapter: FOOD_TYPE : " + foodlistType);
            long dataNormalizedAtMidnight  =
                    DateUtility.getLocalMidnightFromNormalizedUtcDate(DateUtility.getNormalizedUtcMsForToday());
            long previousDayDate = dataNormalizedAtMidnight - DateUtility.DAY_IN_MILLIS;
            long nextDayDate     = dataNormalizedAtMidnight + DateUtility.DAY_IN_MILLIS;
            foodEntries = repository.loadFoodExpiringToday(previousDayDate,nextDayDate);
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


    /*
    // ---------------------------------------------------------------------------------------------
    // Update for db FoodEntry
    // ---------------------------------------------------------------------------------------------
    public void updateFoodEntry(FoodEntry foodEntry){
        repository.updateFoodEntry(foodEntry);
    }


    public void updateDoneField(int done, int id){
        repository.updateDoneField(done, id);
    }

    // ---------------------------------------------------------------------------------------------
    // Delete single FoodEntry
    // ---------------------------------------------------------------------------------------------
    public void deleteFoodEntry(FoodEntry foodEntry){
        repository.deleteFoodEntry(foodEntry);
    }
    */
}