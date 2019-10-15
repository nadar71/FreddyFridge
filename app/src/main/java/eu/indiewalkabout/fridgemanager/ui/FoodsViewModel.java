package eu.indiewalkabout.fridgemanager.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import java.util.List;

import eu.indiewalkabout.fridgemanager.FridgeManagerRepository;
import eu.indiewalkabout.fridgemanager.SingletonProvider;
import eu.indiewalkabout.fridgemanager.data.FoodEntry;
import eu.indiewalkabout.fridgemanager.util.DateUtility;

/**
 * -------------------------------------------------------------------------------------------------
 * ViewModel Class for retrieving all items in db, based on kind
 * -------------------------------------------------------------------------------------------------
 */
public class FoodsViewModel extends ViewModel {

    // tag for logging
    private static final String TAG = FoodsViewModel.class.getSimpleName();

    // Livedata var on foodEntry List to populate through ViewModel
    private LiveData<List<FoodEntry>> foodEntries;

    // Livedata var on foodEntry obj to populate through ViewModel
    private LiveData<FoodEntry> foodEntry;

    // repository ref
    private FridgeManagerRepository repository;

    /**
     * Standard FoodsViewModel constructor; init repository
     */

    public FoodsViewModel() {
        // init repository
        repository = ((SingletonProvider) SingletonProvider.Companion.getsContext()).getRepository();
    }




    /**
     * ---------------------------------------------------------------------------------------------
     * Constructor with parameter used by {@link FoodsViewModelFactory}
     * : init the attributes with LiveData<List<FoodEntry>>
     * @param foodlistType
     * ---------------------------------------------------------------------------------------------
     */
    public FoodsViewModel(String foodlistType) {
        Log.d(TAG, "Actively retrieving the collections from repository");

        // get repository instance
        repository = ((SingletonProvider) SingletonProvider.Companion.getsContext()).getRepository();

        // choose the type of food list to load from db
        if (foodlistType.equals(FoodListActivity.FOOD_EXPIRING)) {
            Log.d(TAG, "setupAdapter: FOOD_TYPE : " + foodlistType);
            long dataNormalizedAtMidnight  =
                    DateUtility.INSTANCE.getLocalMidnightFromNormalizedUtcDate(DateUtility.INSTANCE.getNormalizedUtcMsForToday());
            foodEntries = repository.loadAllFoodExpiring(dataNormalizedAtMidnight);

        }else if (foodlistType.equals(FoodListActivity.FOOD_SAVED)){
            Log.d(TAG, "setupAdapter: FOOD_TYPE : " + foodlistType);
            foodEntries = repository.loadAllFoodSaved();

        }else if (foodlistType.equals(FoodListActivity.FOOD_DEAD)){
            Log.d(TAG, "setupAdapter: FOOD_TYPE : " + foodlistType);
            long dataNormalizedAtMidnight  =
                    DateUtility.INSTANCE.getLocalMidnightFromNormalizedUtcDate(DateUtility.INSTANCE.getNormalizedUtcMsForToday());
            foodEntries = repository.loadAllFoodDead(dataNormalizedAtMidnight);

        }else if (foodlistType.equals(FoodListActivity.FOOD_EXPIRING_TODAY)){
            Log.d(TAG, "setupAdapter: FOOD_TYPE : " + foodlistType);
            long dataNormalizedAtMidnight  =
                    DateUtility.INSTANCE.getLocalMidnightFromNormalizedUtcDate(DateUtility.INSTANCE.getNormalizedUtcMsForToday());
            long previousDayDate = dataNormalizedAtMidnight - DateUtility.INSTANCE.getDAY_IN_MILLIS();
            long nextDayDate     = dataNormalizedAtMidnight + DateUtility.INSTANCE.getDAY_IN_MILLIS();
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

    /**
     * ---------------------------------------------------------------------------------------------
     * Getter for LiveData<FoodEntry>
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    public LiveData<FoodEntry> getFoodEntry(int foodId) {
        foodEntry = repository.loadFoodById(foodId);
        return foodEntry;
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