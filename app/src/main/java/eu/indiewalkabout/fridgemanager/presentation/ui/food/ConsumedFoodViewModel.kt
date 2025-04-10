package eu.indiewalkabout.fridgemanager.presentation.ui.food

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.fridgemanager.domain.repository.FridgeManagerRepositoryImpl
import eu.indiewalkabout.fridgemanager.domain.model.FoodEntry
import javax.inject.Inject


@HiltViewModel
class ConsumedFoodViewModel @Inject constructor(
    val context: Application,
    private val repository: FridgeManagerRepositoryImpl
): ViewModel() {

    // Livedata var on foodEntry List to populate through ViewModel
    var foodList: LiveData<MutableList<FoodEntry>>? = null
        private set

    // Livedata var on foodEntry obj to populate through ViewModel
    private var foodEntry: LiveData<FoodEntry>? = null

    // repository ref
    /*private var repository: FridgeManagerRepository?

    constructor() {
        // init repository
        repository = (getsContext() as FreddyFridgeApplication?)!!.repository
    }*/

    // parameter from FoodsViewModelFactory foodlistType
    /*constructor(foodlistType: String) {
        Log.d(TAG, "Actively retrieving the collections from repository")

        // get repository instance
        // repository = (getsContext() as FreddyFridgeApplication?)!!.repository

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
    }*/


    fun getFoodList(): LiveData<MutableList<FoodEntry>> {
        return repository.loadAllFoodSaved()
    }

    fun getFoodEntry(foodId: Int): LiveData<FoodEntry> {
        foodEntry = repository.loadFoodById(foodId)
        return foodEntry!!
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