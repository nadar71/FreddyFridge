package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class FoodEntrySubmissionBuilderTest {

    @Test
    fun `buildInsertEntries returns one entry when quantity is one or lower`() {
        val entries = FoodEntrySubmissionBuilder.buildInsertEntries(
            name = "Milk",
            expiringAt = LocalDate.of(2026, 5, 5),
            timezoneId = "Europe/Rome",
            quantity = 0,
        )

        assertEquals(1, entries.size)
        assertEquals("Milk", entries.single().name)
    }

    @Test
    fun `buildInsertEntries returns as many entries as requested quantity`() {
        val entries = FoodEntrySubmissionBuilder.buildInsertEntries(
            name = "Yogurt",
            expiringAt = LocalDate.of(2026, 5, 5),
            timezoneId = "Europe/Rome",
            quantity = 3,
        )

        assertEquals(3, entries.size)
        assertEquals(listOf("Yogurt", "Yogurt", "Yogurt"), entries.map { it.name })
    }

    @Test
    fun `buildUpdateSubmission updates first entry and creates remaining inserts`() {
        val submission = FoodEntrySubmissionBuilder.buildUpdateSubmission(
            originalEntry = FoodEntry(
                id = 7,
                name = "Old milk",
                consumedAt = LocalDate.of(2026, 5, 1),
            ),
            updatedName = "New milk",
            updatedExpiringAt = LocalDate.of(2026, 5, 10),
            timezoneId = "Europe/Rome",
            quantity = 3,
        )

        assertEquals(7, submission.entryToUpdate.id)
        assertEquals("New milk", submission.entryToUpdate.name)
        assertEquals(2, submission.additionalEntries.size)
        assertEquals(listOf("New milk", "New milk"), submission.additionalEntries.map { it.name })
        assertEquals(
            listOf(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1)),
            submission.additionalEntries.map { it.consumedAt }
        )
    }

    @Test
    fun `buildUpdateSubmission with single quantity creates no additional entries`() {
        val submission = FoodEntrySubmissionBuilder.buildUpdateSubmission(
            originalEntry = FoodEntry(id = 1, name = "Milk"),
            updatedName = "Milk",
            updatedExpiringAt = null,
            timezoneId = "Europe/Rome",
            quantity = 1,
        )

        assertEquals(1, submission.entryToUpdate.id)
        assertTrue(submission.additionalEntries.isEmpty())
        assertNull(submission.entryToUpdate.expiringAt)
    }
}
