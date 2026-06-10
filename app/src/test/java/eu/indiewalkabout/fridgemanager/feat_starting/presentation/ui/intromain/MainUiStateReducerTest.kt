package eu.indiewalkabout.fridgemanager.feat_starting.presentation.ui.intromain

import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodListUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MainUiStateReducerTest {

    @Test
    fun `food list loading turns on list loading flag without marking data as loaded`() {
        val initialState = MainUiState()

        val result = MainUiStateReducer.reduceFoodListState(
            initialState,
            FoodListUiState.Loading
        )

        assertTrue(result.isFoodListLoading)
        assertFalse(result.hasLoadedFood)
    }

    @Test
    fun `food list success updates rendered list and clears loading`() {
        val initialState = MainUiState(isFoodListLoading = true)
        val foods = listOf(FoodEntry(id = 1, name = "Milk"))

        val result = MainUiStateReducer.reduceFoodListState(
            initialState,
            FoodListUiState.Success(foods)
        )

        assertEquals(foods, result.foods)
        assertTrue(result.hasLoadedFood)
        assertFalse(result.isLoading)
    }

    @Test
    fun `food list error clears list loading and preserves existing foods`() {
        val existingFoods = listOf(FoodEntry(id = 1, name = "Milk"))
        val initialState = MainUiState(
            foods = existingFoods,
            isFoodListLoading = true
        )

        val result = MainUiStateReducer.reduceFoodListState(
            initialState,
            FoodListUiState.Error(ErrorResponse(0, emptyList(), "boom"))
        )

        assertEquals(existingFoods, result.foods)
        assertTrue(result.hasLoadedFood)
        assertFalse(result.isFoodListLoading)
    }

    @Test
    fun `insert success closes bottom sheet and emits toast plus reminder refresh`() {
        val initialState = MainUiState(
            isOperationLoading = true,
            isBottomSheetVisible = true
        )

        val transition = MainUiStateReducer.reduceInsertResult(
            initialState,
            isSuccess = true
        )

        assertFalse(transition.state.isLoading)
        assertFalse(transition.state.isBottomSheetVisible)
        assertEquals(
            listOf(
                MainUiEvent.ShowToast(R.string.insert_food_successfully),
                MainUiEvent.RefreshExpiringNotifications
            ),
            transition.events
        )
    }

    @Test
    fun `update success keeps ui stable and emits success toast`() {
        val initialState = MainUiState(isOperationLoading = true)

        val transition = MainUiStateReducer.reduceUpdateResult(
            initialState,
            isSuccess = true
        )

        assertFalse(transition.state.isLoading)
        assertEquals(
            listOf(MainUiEvent.ShowToast(R.string.update_food_successfully)),
            transition.events
        )
    }

    @Test
    fun `errors stop loading and emit no events`() {
        val initialState = MainUiState(isOperationLoading = true, isBottomSheetVisible = true)

        val insertTransition = MainUiStateReducer.reduceInsertResult(
            initialState,
            isSuccess = false
        )
        val updateTransition = MainUiStateReducer.reduceUpdateResult(
            initialState,
            isSuccess = false
        )

        assertFalse(insertTransition.state.isLoading)
        assertTrue(insertTransition.state.isBottomSheetVisible)
        assertTrue(insertTransition.events.isEmpty())
        assertFalse(updateTransition.state.isLoading)
        assertTrue(updateTransition.events.isEmpty())
    }

    @Test
    fun `loading operation states only toggle loading and emit no events`() {
        val initialState = MainUiState(isBottomSheetVisible = true)

        val insertTransition = MainUiStateReducer.reduceInsertLoading(
            initialState,
            isLoading = true
        )
        val updateTransition = MainUiStateReducer.reduceUpdateLoading(
            initialState,
            isLoading = true
        )

        assertTrue(insertTransition.state.isOperationLoading)
        assertTrue(insertTransition.state.isBottomSheetVisible)
        assertTrue(insertTransition.events.isEmpty())
        assertTrue(updateTransition.state.isOperationLoading)
        assertTrue(updateTransition.events.isEmpty())
    }
}
