package eu.indiewalkabout.fridgemanager.presentation.ui.food

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.getLocalMidnightFromNormalizedUtcDate
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.normalizedUtcMsForToday
import eu.indiewalkabout.fridgemanager.data.repository.FridgeManagerRepositoryImpl
import eu.indiewalkabout.fridgemanager.domain.model.FoodEntry
import javax.inject.Inject


@HiltViewModel
class DeadFoodViewModel @Inject constructor(
    val context: Application,
    private val repository: FridgeManagerRepositoryImpl
): ViewModel() {

    var foodList: LiveData<MutableList<FoodEntry>>? = null
        private set

    private var foodEntry: LiveData<FoodEntry>? = null

    fun getFoodList(): LiveData<MutableList<FoodEntry>> {
        val dataNormalizedAtMidnight =
            getLocalMidnightFromNormalizedUtcDate(normalizedUtcMsForToday)
        return repository.loadAllFoodDead(dataNormalizedAtMidnight)
    }

    fun getFoodEntry(foodId: Int): LiveData<FoodEntry> {
        foodEntry = repository.loadFoodById(foodId)
        return foodEntry!!
    }

}