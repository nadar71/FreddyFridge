package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntryUI
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class FoodEditorUiStateTest {

    @Test
    fun `empty state starts with default quantity and disabled submit`() {
        val state = FoodEditorUiState.empty()

        assertEquals("1", state.quantityText)
        assertEquals("", state.description)
        assertEquals("", state.expiringAtText)
        assertFalse(state.canSubmit)
    }

    @Test
    fun `from entry ui preloads editable values and keeps submit enabled`() {
        val state = FoodEditorUiState.fromFoodEntryUI(
            FoodEntryUI(
                id = 12,
                name = "Yogurt",
                expiringAtLocalDate = LocalDate.of(2026, 5, 6),
                expiringAtUI = "06/05/2026",
            )
        )

        assertEquals("Yogurt", state.description)
        assertEquals(LocalDate.of(2026, 5, 6), state.expiringAt)
        assertEquals("06/05/2026", state.expiringAtText)
        assertEquals("1", state.quantityText)
        assertTrue(state.canSubmit)
    }

    @Test
    fun `quantity falls back to one when text is blank or invalid`() {
        assertEquals(1, FoodEditorUiState(quantityText = "").quantity)
        assertEquals(1, FoodEditorUiState(quantityText = "abc").quantity)
        assertEquals(4, FoodEditorUiState(quantityText = "4").quantity)
    }
}
