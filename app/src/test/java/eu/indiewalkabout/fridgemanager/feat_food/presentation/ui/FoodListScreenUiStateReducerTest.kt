package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.domain.model.ErrorResponse
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodListUiState
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodUiState
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodUpdateUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FoodListScreenUiStateReducerTest {

    @Test
    fun `food list success updates foods and clears list loading`() {
        val initialState = FoodListScreenUiState(isFoodListLoading = true)
        val foods = listOf(FoodEntry(id = 1, name = "Milk"))

        val result = FoodListScreenUiStateReducer.reduceFoodListState(
            initialState,
            FoodListUiState.Success(foods)
        )

        assertEquals(foods, result.foods)
        assertTrue(result.hasLoadedFood)
        assertFalse(result.isLoading)
    }

    @Test
    fun `food list error keeps existing data and clears list loading`() {
        val foods = listOf(FoodEntry(id = 1, name = "Milk"))
        val initialState = FoodListScreenUiState(
            foods = foods,
            isFoodListLoading = true
        )

        val result = FoodListScreenUiStateReducer.reduceFoodListState(
            initialState,
            FoodListUiState.Error(ErrorResponse(0, emptyList(), "boom"))
        )

        assertEquals(foods, result.foods)
        assertTrue(result.hasLoadedFood)
        assertFalse(result.isFoodListLoading)
    }

    @Test
    fun `insert success closes bottom sheet and emits expected events`() {
        val initialState = FoodListScreenUiState(
            isOperationLoading = true,
            isBottomSheetVisible = true
        )

        val transition = FoodListScreenUiStateReducer.reduceInsertState(
            initialState,
            FoodUiState.Success(Unit)
        )

        assertFalse(transition.state.isLoading)
        assertFalse(transition.state.isBottomSheetVisible)
        assertEquals(
            listOf(
                FoodListScreenUiEvent.ShowToast(R.string.insert_food_successfully),
                FoodListScreenUiEvent.RefreshExpiringNotifications
            ),
            transition.events
        )
    }

    @Test
    fun `update success emits success toast and clears operation loading`() {
        val initialState = FoodListScreenUiState(isOperationLoading = true)

        val transition = FoodListScreenUiStateReducer.reduceUpdateState(
            initialState,
            FoodUpdateUiState.Success(Unit)
        )

        assertFalse(transition.state.isLoading)
        assertEquals(
            listOf(FoodListScreenUiEvent.ShowToast(R.string.update_food_successfully)),
            transition.events
        )
    }

    @Test
    fun `loading states set only operation loading`() {
        val initialState = FoodListScreenUiState(isBottomSheetVisible = true)

        val insertTransition = FoodListScreenUiStateReducer.reduceInsertState(
            initialState,
            FoodUiState.Loading
        )
        val updateTransition = FoodListScreenUiStateReducer.reduceUpdateState(
            initialState,
            FoodUpdateUiState.Loading
        )

        assertTrue(insertTransition.state.isOperationLoading)
        assertTrue(insertTransition.state.isBottomSheetVisible)
        assertTrue(insertTransition.events.isEmpty())
        assertTrue(updateTransition.state.isOperationLoading)
        assertTrue(updateTransition.events.isEmpty())
    }
}
