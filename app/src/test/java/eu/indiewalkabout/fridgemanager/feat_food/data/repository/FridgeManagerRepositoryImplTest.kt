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
    fun observeAllFood_emitsUpdatedListsFromDaoFlow() = runBlocking {
        val initialItems = mutableListOf(
            FoodEntry(id = 1, name = "Milk", expiringAt = LocalDate.of(2026, 5, 1))
        )
        val updatedItems = mutableListOf(
            FoodEntry(id = 1, name = "Milk", expiringAt = LocalDate.of(2026, 5, 1)),
            FoodEntry(id = 2, name = "Eggs", expiringAt = LocalDate.of(2026, 5, 2))
        )
        val dao = FakeFoodDbDao(allFoodFlow = MutableStateFlow(initialItems.toList()))
        val repository = FridgeManagerRepositoryImpl(dao)

        val emissions = mutableListOf<List<FoodEntry>>()
        val collectJob = launch {
            repository.observeAllFood().collect { emissions += it }
        }

        delay(10)
        dao.allFoodFlow.value = updatedItems.toList()
        delay(10)
        collectJob.cancel()

        assertEquals(listOf(initialItems, updatedItems), emissions)
    }

    @Test
    fun observeAllFoodExpiring_emitsUpdatedListsFromDaoFlow() = runBlocking {
        val initialItems = mutableListOf(
            FoodEntry(id = 1, name = "Cheese", expiringAt = LocalDate.of(2026, 5, 3))
        )
        val updatedItems = mutableListOf(
            FoodEntry(id = 1, name = "Cheese", expiringAt = LocalDate.of(2026, 5, 3)),
            FoodEntry(id = 2, name = "Yogurt", expiringAt = LocalDate.of(2026, 5, 4))
        )
        val dao = FakeFoodDbDao(expiringFlow = MutableStateFlow(initialItems.toList()))
        val repository = FridgeManagerRepositoryImpl(dao)

        val emissions = mutableListOf<List<FoodEntry>>()
        val collectJob = launch {
            repository.observeAllFoodExpiring(date = 300L).collect { emissions += it }
        }

        delay(10)
        dao.expiringFlow.value = updatedItems.toList()
        delay(10)
        collectJob.cancel()

        assertEquals(listOf(initialItems, updatedItems), emissions)
    }

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

    @Test
    fun observeAllFoodExpired_emitsUpdatedListsFromDaoFlow() = runBlocking {
        val initialItems = mutableListOf(
            FoodEntry(id = 1, name = "Salad", expiringAt = LocalDate.of(2026, 4, 30))
        )
        val updatedItems = mutableListOf(
            FoodEntry(id = 1, name = "Salad", expiringAt = LocalDate.of(2026, 4, 30)),
            FoodEntry(id = 2, name = "Ham", expiringAt = LocalDate.of(2026, 4, 29))
        )
        val dao = FakeFoodDbDao(expiredFlow = MutableStateFlow(initialItems.toList()))
        val repository = FridgeManagerRepositoryImpl(dao)

        val emissions = mutableListOf<List<FoodEntry>>()
        val collectJob = launch {
            repository.observeAllFoodExpired(date = 400L).collect { emissions += it }
        }

        delay(10)
        dao.expiredFlow.value = updatedItems.toList()
        delay(10)
        collectJob.cancel()

        assertEquals(listOf(initialItems, updatedItems), emissions)
    }

    @Test
    fun observeAllFoodConsumed_emitsUpdatedListsFromDaoFlow() = runBlocking {
        val initialItems = mutableListOf(
            FoodEntry(id = 1, name = "Bread", consumedAt = LocalDate.of(2026, 5, 1))
        )
        val updatedItems = mutableListOf(
            FoodEntry(id = 1, name = "Bread", consumedAt = LocalDate.of(2026, 5, 1)),
            FoodEntry(id = 2, name = "Butter", consumedAt = LocalDate.of(2026, 5, 2))
        )
        val dao = FakeFoodDbDao(consumedFlow = MutableStateFlow(initialItems.toList()))
        val repository = FridgeManagerRepositoryImpl(dao)

        val emissions = mutableListOf<List<FoodEntry>>()
        val collectJob = launch {
            repository.observeAllFoodConsumed().collect { emissions += it }
        }

        delay(10)
        dao.consumedFlow.value = updatedItems.toList()
        delay(10)
        collectJob.cancel()

        assertEquals(listOf(initialItems, updatedItems), emissions)
    }

    @Test
    fun loadAllFoodExpiring_returnsDaoSnapshot() = runBlocking {
        val expected = listOf(FoodEntry(id = 1, name = "Milk"))
        val repository = FridgeManagerRepositoryImpl(
            FakeFoodDbDao(expiringSnapshot = expected)
        )

        val result: List<FoodEntry> = repository.loadAllFoodExpiring(date = 300L)

        assertEquals(expected, result)
    }

    @Test
    fun loadFoodExpiringToday_returnsDaoSnapshot() = runBlocking {
        val expected = listOf(FoodEntry(id = 2, name = "Yogurt"))
        val repository = FridgeManagerRepositoryImpl(
            FakeFoodDbDao(expiringTodaySnapshot = expected)
        )

        val result: List<FoodEntry> = repository.loadFoodExpiringToday(
            daybefore = 100L,
            dayafter = 200L,
        )

        assertEquals(expected, result)
    }

    private class FakeFoodDbDao(
        val allFoodFlow: MutableStateFlow<List<FoodEntry>> = MutableStateFlow(emptyList()),
        val expiringFlow: MutableStateFlow<List<FoodEntry>> = MutableStateFlow(emptyList()),
        val expiringTodayFlow: MutableStateFlow<List<FoodEntry>> = MutableStateFlow(emptyList()),
        val expiredFlow: MutableStateFlow<List<FoodEntry>> = MutableStateFlow(emptyList()),
        val consumedFlow: MutableStateFlow<List<FoodEntry>> = MutableStateFlow(emptyList()),
        private val expiringSnapshot: List<FoodEntry> = emptyList(),
        private val expiringTodaySnapshot: List<FoodEntry> = emptyList(),
    ) : FoodDbDao {

        override fun observeAllFood(): Flow<List<FoodEntry>> = allFoodFlow

        override fun observeAllFoodExpiring(date: Long?): Flow<List<FoodEntry>> =
            expiringFlow

        override fun observeFoodExpiringToday(
            daybefore: Long?,
            dayafter: Long?
        ): Flow<List<FoodEntry>> = expiringTodayFlow

        override fun observeAllFoodExpired(date: Long?): Flow<List<FoodEntry>> =
            expiredFlow

        override fun observeAllFoodConsumed(): Flow<List<FoodEntry>> =
            consumedFlow

        override suspend fun loadAllFoodExpiring(date: Long?): List<FoodEntry> = expiringSnapshot

        override suspend fun loadFoodExpiringToday(
            daybefore: Long?,
            dayafter: Long?
        ): List<FoodEntry> = expiringTodaySnapshot

        override suspend fun insertFoodEntry(foodEntry: FoodEntry) = Unit

        override suspend fun updateFoodEntry(foodEntry: FoodEntry) = Unit

        override suspend fun deleteFoodEntry(foodEntry: FoodEntry) = Unit
    }
}
