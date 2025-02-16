package eu.indiewalkabout.fridgemanager.presentation.ui.food

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.DAY_IN_MILLIS
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.getLocalMidnightFromNormalizedUtcDate
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.normalizedUtcMsForToday
import eu.indiewalkabout.fridgemanager.data.repository.FridgeManagerRepository
import eu.indiewalkabout.fridgemanager.domain.model.FoodEntry
import javax.inject.Inject


@HiltViewModel
class ExpiringFoodViewModel @Inject constructor(
    val context: Application,
    private val repository: FridgeManagerRepository
): ViewModel() {

    // Livedata var on foodEntry List to populate through ViewModel
    var foodList: LiveData<MutableList<FoodEntry>>? = null
        private set

    // Livedata var on foodEntry obj to populate through ViewModel
    private var foodEntry: LiveData<FoodEntry>? = null


    fun getFoodList(): LiveData<MutableList<FoodEntry>> {
        val dataNormalizedAtMidnight =
            getLocalMidnightFromNormalizedUtcDate(normalizedUtcMsForToday)
        val previousDayDate = dataNormalizedAtMidnight - DAY_IN_MILLIS
        val nextDayDate = dataNormalizedAtMidnight + DAY_IN_MILLIS
        return repository.loadFoodExpiringToday(previousDayDate, nextDayDate)
    }

    fun getFoodEntry(foodId: Int): LiveData<FoodEntry> {
        foodEntry = repository.loadFoodById(foodId)
        return foodEntry!!
    }

}