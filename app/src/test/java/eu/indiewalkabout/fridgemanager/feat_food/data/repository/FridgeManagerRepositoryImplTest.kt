package eu.indiewalkabout.fridgemanager.feat_food.data.repository

import eu.indiewalkabout.fridgemanager.feat_food.data.local.db.FoodDbDao
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class FridgeManagerRepositoryImplTest {

    @Test
    fun observeFoodExpiringToday_emitsUpdatedListsFromDaoFlow() = runBlocking {
        val initialItems = mutableListOf(
            FoodEntry(id = 1, name = "Milk", expiringAt = LocalDate.of(2026, 5, 1))
        )
        val updatedItems = mutableListOf(
            FoodEntry(id = 1, name = "Milk", expiringAt = LocalDate.of(2026, 5, 1)),
            FoodEntry(id = 2, name = "Yogurt", expiringAt = LocalDate.of(2026, 5, 1))
        )
        val dao = FakeFoodDbDao(
            expiringTodayFlow = MutableStateFlow(initialItems.toList())
        )
        val repository = FridgeManagerRepositoryImpl(dao)

        val emissions = mutableListOf<List<FoodEntry>>()
        val collectJob = launch {
            repository.observeFoodExpiringToday(daybefore = 100L, dayafter = 200L)
                .collect { emissions += it }
        }

        delay(10)
        dao.expiringTodayFlow.value = updatedItems.toList()
        delay(10)
        collectJob.cancel()

        assertEquals(listOf(initialItems, updatedItems), emissions)
    }

    private class FakeFoodDbDao(
        val expiringTodayFlow: MutableStateFlow<List<FoodEntry>>
    ) : FoodDbDao {

        override fun observeAllFood(): Flow<List<FoodEntry>> = MutableStateFlow(emptyList())

        override fun observeAllFoodExpiring(date: Long?): Flow<List<FoodEntry>> =
            MutableStateFlow(emptyList())

        override fun observeFoodExpiringToday(
            daybefore: Long?,
            dayafter: Long?
        ): Flow<List<FoodEntry>> = expiringTodayFlow

        override fun observeAllFoodExpired(date: Long?): Flow<List<FoodEntry>> =
            MutableStateFlow(emptyList())

        override fun observeAllFoodConsumed(): Flow<List<FoodEntry>> =
            MutableStateFlow(emptyList())

        override suspend fun loadAllFood(): MutableList<FoodEntry> = mutableListOf()

        override suspend fun loadAllFoodExpiring(date: Long?): MutableList<FoodEntry> = mutableListOf()

        override suspend fun loadFoodExpiringToday(
            daybefore: Long?,
            dayafter: Long?
        ): MutableList<FoodEntry> = mutableListOf()

        override suspend fun loadAllFoodDead(date: Long?): MutableList<FoodEntry> = mutableListOf()

        override suspend fun loadAllFoodSaved(): MutableList<FoodEntry> = mutableListOf()

        override suspend fun loadFoodById(id: Int): FoodEntry = FoodEntry(id = id)

        override suspend fun insertFoodEntry(foodEntry: FoodEntry) = Unit

        override suspend fun updateFoodEntry(foodEntry: FoodEntry) = Unit

        override suspend fun updateDoneField(done: Int, id: Int) = Unit

        override suspend fun deleteFoodEntry(foodEntry: FoodEntry) = Unit

        override suspend fun dropTable() = Unit
    }
}
