package eu.indiewalkabout.fridgemanager.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import android.util.Log
import eu.indiewalkabout.fridgemanager.FridgeManagerRepository
import eu.indiewalkabout.fridgemanager.SingletonProvider
import eu.indiewalkabout.fridgemanager.SingletonProvider.Companion.getsContext
import eu.indiewalkabout.fridgemanager.data.FoodEntry
import eu.indiewalkabout.fridgemanager.util.DateUtility.DAY_IN_MILLIS
import eu.indiewalkabout.fridgemanager.util.DateUtility.getLocalMidnightFromNormalizedUtcDate
import eu.indiewalkabout.fridgemanager.util.DateUtility.normalizedUtcMsForToday

/**
 * -------------------------------------------------------------------------------------------------
 * ViewModel Class for retrieving all items in db, based on kind
 * -------------------------------------------------------------------------------------------------
 */
class FoodsViewModel : ViewModel {
    /**
     * ---------------------------------------------------------------------------------------------
     * Getter for LiveData<List></List><FoodEntry>> list
     * @return
     * ---------------------------------------------------------------------------------------------
    </FoodEntry> */
    // Livedata var on foodEntry List to populate through ViewModel
    var foodList: LiveData<MutableList<FoodEntry>>? = null
        private set

    // Livedata var on foodEntry obj to populate through ViewModel
    var foodEntry: LiveData<FoodEntry>? = null

    // repository ref
    var repository: FridgeManagerRepository?

    /**
     * Standard FoodsViewModel constructor; init repository
     */
    constructor() {
        // init repository
        repository = (getsContext() as SingletonProvider?)!!.repository
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
        repository = (getsContext() as SingletonProvider?)!!.repository

        // choose the type of food list to load from db
        if (foodlistType == FoodListActivity.FOOD_EXPIRING) {
            Log.d(TAG, "setupAdapter: FOOD_TYPE : $foodlistType")
            val dataNormalizedAtMidnight = getLocalMidnightFromNormalizedUtcDate(normalizedUtcMsForToday)
            foodList = repository!!.loadAllFoodExpiring(dataNormalizedAtMidnight)

        } else if (foodlistType == FoodListActivity.FOOD_SAVED) {
            Log.d(TAG, "setupAdapter: FOOD_TYPE : $foodlistType")
            foodList = repository!!.loadAllFoodSaved()

        } else if (foodlistType == FoodListActivity.FOOD_DEAD) {
            Log.d(TAG, "setupAdapter: FOOD_TYPE : $foodlistType")
            val dataNormalizedAtMidnight = getLocalMidnightFromNormalizedUtcDate(normalizedUtcMsForToday)
            foodList = repository!!.loadAllFoodDead(dataNormalizedAtMidnight)

        } else if (foodlistType == FoodListActivity.FOOD_EXPIRING_TODAY) {
            Log.d(TAG, "setupAdapter: FOOD_TYPE : $foodlistType")
            val dataNormalizedAtMidnight = getLocalMidnightFromNormalizedUtcDate(normalizedUtcMsForToday)
            val previousDayDate = dataNormalizedAtMidnight - DAY_IN_MILLIS
            val nextDayDate = dataNormalizedAtMidnight + DAY_IN_MILLIS
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
        return foodEntry!!
    } /*
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

    companion object {
        private val TAG = FoodsViewModel::class.java.simpleName
    }
}