package eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases

import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.domain.repository.FridgeManagerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class LoadFoodForNotificationUseCasesTest {

    @Test
    fun `next-days query delegates cutoff date and returns repository result`() = runBlocking {
        val expected = listOf(FoodEntry(id = 1, name = "Milk"))
        val repository = FakeRepository(expiringResult = expected)
        val useCase = LoadFoodExpiringForNotificationUseCase(repository)

        val result = useCase(cutoffDate = 300L)

        assertEquals(300L, repository.requestedCutoffDate)
        assertEquals(expected, result)
    }

    @Test
    fun `next-days query preserves empty result`() = runBlocking {
        val useCase = LoadFoodExpiringForNotificationUseCase(FakeRepository())

        assertEquals(emptyList<FoodEntry>(), useCase(cutoffDate = 300L))
    }

    @Test
    fun `today query delegates date bounds and returns repository result`() = runBlocking {
        val expected = listOf(FoodEntry(id = 2, name = "Yogurt"))
        val repository = FakeRepository(expiringTodayResult = expected)
        val useCase = LoadFoodExpiringTodayForNotificationUseCase(repository)

        val result = useCase(dayBefore = 100L, dayAfter = 200L)

        assertEquals(100L, repository.requestedDayBefore)
        assertEquals(200L, repository.requestedDayAfter)
        assertEquals(expected, result)
    }

    @Test
    fun `today query preserves empty result`() = runBlocking {
        val useCase = LoadFoodExpiringTodayForNotificationUseCase(FakeRepository())

        assertEquals(emptyList<FoodEntry>(), useCase(dayBefore = 100L, dayAfter = 200L))
    }

    private class FakeRepository(
        private val expiringResult: List<FoodEntry> = emptyList(),
        private val expiringTodayResult: List<FoodEntry> = emptyList(),
    ) : FridgeManagerRepository {
        var requestedCutoffDate: Long? = null
        var requestedDayBefore: Long? = null
        var requestedDayAfter: Long? = null

        override fun observeAllFood(): Flow<List<FoodEntry>> = emptyFlow()
        override fun observeAllFoodExpiring(date: Long?): Flow<List<FoodEntry>> = emptyFlow()
        override fun observeFoodExpiringToday(
            daybefore: Long?,
            dayafter: Long?,
        ): Flow<List<FoodEntry>> = emptyFlow()

        override fun observeAllFoodExpired(date: Long?): Flow<List<FoodEntry>> = emptyFlow()
        override fun observeAllFoodConsumed(): Flow<List<FoodEntry>> = emptyFlow()

        override suspend fun loadAllFoodExpiring(date: Long?): List<FoodEntry> {
            requestedCutoffDate = date
            return expiringResult
        }

        override suspend fun loadFoodExpiringToday(
            daybefore: Long?,
            dayafter: Long?,
        ): List<FoodEntry> {
            requestedDayBefore = daybefore
            requestedDayAfter = dayafter
            return expiringTodayResult
        }

        override suspend fun insertFoodEntry(foodEntry: FoodEntry) = Unit
        override suspend fun updateFoodEntry(foodEntry: FoodEntry) = Unit
        override suspend fun deleteFoodEntry(foodEntry: FoodEntry) = Unit
    }
}
