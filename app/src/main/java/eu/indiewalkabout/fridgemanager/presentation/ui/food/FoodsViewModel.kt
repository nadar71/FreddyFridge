package eu.indiewalkabout.fridgemanager.presentation.ui.food

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import eu.indiewalkabout.fridgemanager.FreddyFridgeApplication
import eu.indiewalkabout.fridgemanager.FreddyFridgeApplication.Companion.getsContext
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.DAY_IN_MILLIS
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.getLocalMidnightFromNormalizedUtcDate
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.normalizedUtcMsForToday
import eu.indiewalkabout.fridgemanager.core.util.extensions.TAG
import eu.indiewalkabout.fridgemanager.domain.repository.FridgeManagerRepositoryImpl
import eu.indiewalkabout.fridgemanager.domain.model.FoodEntry


class FoodsViewModel : ViewModel {

    // Livedata var on foodEntry List to populate through ViewModel
    var foodList: LiveData<MutableList<FoodEntry>>? = null
        private set

    // Livedata var on foodEntry obj to populate through ViewModel
    private var foodEntry: LiveData<FoodEntry>? = null

    // repository ref
    private var repository: FridgeManagerRepositoryImpl?

    constructor() {
        // init repository
        repository = (getsContext() as FreddyFridgeApplication?)!!.repository
    }

    // parameter from FoodsViewModelFactory foodlistType
    constructor(foodlistType: String) {
        Log.d(TAG, "Actively retrieving the collections from repository")

        // get repository instance
        repository = (getsContext() as FreddyFridgeApplication?)!!.repository

        // choose the type of food list to load from db
        if (foodlistType == FoodListActivity.FOOD_EXPIRING) {
            Log.d(TAG, "setupAdapter: FOOD_TYPE : $foodlistType")
            val dataNormalizedAtMidnight =
                getLocalMidnightFromNormalizedUtcDate(normalizedUtcMsForToday)
            foodList = repository!!.loadAllFoodExpiring(dataNormalizedAtMidnight)

        } else if (foodlistType == FoodListActivity.FOOD_SAVED) {
            Log.d(TAG, "setupAdapter: FOOD_TYPE : $foodlistType")
            foodList = repository!!.loadAllFoodSaved()

        } else if (foodlistType == FoodListActivity.FOOD_DEAD) {
            Log.d(TAG, "setupAdapter: FOOD_TYPE : $foodlistType")
            val dataNormalizedAtMidnight =
                getLocalMidnightFromNormalizedUtcDate(normalizedUtcMsForToday)
            foodList = repository!!.loadAllFoodDead(dataNormalizedAtMidnight)

        } else if (foodlistType == FoodListActivity.FOOD_EXPIRING_TODAY) {
            Log.d(TAG, "setupAdapter: FOOD_TYPE : $foodlistType")
            val dataNormalizedAtMidnight =
                getLocalMidnightFromNormalizedUtcDate(normalizedUtcMsForToday)
            val previousDayDate = dataNormalizedAtMidnight - DAY_IN_MILLIS
            val nextDayDate = dataNormalizedAtMidnight + DAY_IN_MILLIS
            foodList = repository!!.loadFoodExpiringToday(previousDayDate, nextDayDate)
        }
    }

    // Getter for LiveData<FoodEntry>
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
}