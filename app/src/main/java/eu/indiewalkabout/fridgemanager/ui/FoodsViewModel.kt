package eu.indiewalkabout.fridgemanager.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.util.Log

import eu.indiewalkabout.fridgemanager.FridgeManagerRepository
import eu.indiewalkabout.fridgemanager.SingletonProvider
import eu.indiewalkabout.fridgemanager.data.FoodEntry
import eu.indiewalkabout.fridgemanager.util.DateUtility

/**
 * -------------------------------------------------------------------------------------------------
 * ViewModel Class for retrieving all items in db, based on kind
 * -------------------------------------------------------------------------------------------------
 */
class FoodsViewModel : ViewModel {

    // Livedata var on foodEntry List to populate through ViewModel
    /**
     * ---------------------------------------------------------------------------------------------
     * Getter for LiveData<List></List><FoodEntry>> list
     * @return
     * ---------------------------------------------------------------------------------------------
    </FoodEntry> */
    internal lateinit var foodList: LiveData<List<FoodEntry>>
        private set

    // Livedata var on foodEntry obj to populate through ViewModel
    private var foodEntry: LiveData<FoodEntry>? = null

    // repository ref
    private var repository: FridgeManagerRepository? = null

    /**
     * Standard FoodsViewModel constructor; init repository
     */

    constructor() {
        // init repository
        repository = (SingletonProvider.getsContext() as SingletonProvider).repository
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Constructor with parameter used by [FoodsViewModelFactory]
     * : init the attributes with LiveData<List></List><FoodEntry>>
     * @param foodlistType
     * ---------------------------------------------------------------------------------------------
    </FoodEntry> */
    constructor(foodlistType: String) {
        Log.d(TAG, "Actively retrieving the collections from repository")

        // get repository instance
        repository = (SingletonProvider.getsContext() as SingletonProvider).repository

        // choose the type of food list to load from db
        if (foodlistType == FoodListActivity.FOOD_EXPIRING) {
            Log.d(TAG, "setupAdapter: FOOD_TYPE : $foodlistType")
            val dataNormalizedAtMidnight = DateUtility.getLocalMidnightFromNormalizedUtcDate(DateUtility.normalizedUtcMsForToday)
            foodList = repository!!.loadAllFoodExpiring(dataNormalizedAtMidnight)

        } else if (foodlistType == FoodListActivity.FOOD_SAVED) {
            Log.d(TAG, "setupAdapter: FOOD_TYPE : $foodlistType")
            foodList = repository!!.loadAllFoodSaved()

        } else if (foodlistType == FoodListActivity.FOOD_DEAD) {
            Log.d(TAG, "setupAdapter: FOOD_TYPE : $foodlistType")
            val dataNormalizedAtMidnight = DateUtility.getLocalMidnightFromNormalizedUtcDate(DateUtility.normalizedUtcMsForToday)
            foodList = repository!!.loadAllFoodDead(dataNormalizedAtMidnight)

        } else if (foodlistType == FoodListActivity.FOOD_EXPIRING_TODAY) {
            Log.d(TAG, "setupAdapter: FOOD_TYPE : $foodlistType")
            val dataNormalizedAtMidnight = DateUtility.getLocalMidnightFromNormalizedUtcDate(DateUtility.normalizedUtcMsForToday)
            val previousDayDate = dataNormalizedAtMidnight - DateUtility.DAY_IN_MILLIS
            val nextDayDate = dataNormalizedAtMidnight + DateUtility.DAY_IN_MILLIS
            foodList = repository!!.loadFoodExpiringToday(previousDayDate, nextDayDate)
        }

    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Getter for LiveData<FoodEntry>
     * @return
     * ---------------------------------------------------------------------------------------------
    </FoodEntry> */
    fun getFoodEntry(foodId: Int): LiveData<FoodEntry> {
        foodEntry = repository!!.loadFoodById(foodId)
        return foodEntry as LiveData<FoodEntry>
    }

    companion object {

        // tag for logging
        private val TAG = FoodsViewModel::class.java.simpleName
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