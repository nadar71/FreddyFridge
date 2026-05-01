package eu.indiewalkabout.fridgemanager.feat_food.data.repository

import eu.indiewalkabout.fridgemanager.feat_food.data.local.db.FoodDbDao
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.domain.repository.FridgeManagerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FridgeManagerRepositoryImpl @Inject constructor(
    private val foodDbDao: FoodDbDao
): FridgeManagerRepository {


    //----------------------------------- QUERY ----------------------------------------------------

    // detect ALL KIND OF FOOD changes  without regarding expiring date
    override fun observeAllFood(): Flow<List<FoodEntry>> {
        return foodDbDao.observeAllFood()
    }

    // detect EXPIRING FOOD changes
    override fun observeAllFoodExpiring(date: Long?): Flow<List<FoodEntry>> {
        return foodDbDao.observeAllFoodExpiring(date)
    }

    // detect EXPIRING FOOD TODAY changes
    override fun observeFoodExpiringToday(daybefore: Long?, dayafter: Long?): Flow<List<FoodEntry>> {
        return foodDbDao.observeFoodExpiringToday(daybefore, dayafter)
    }

    // detect DEAD/EXPIRED FOOD changes
    override fun observeAllFoodExpired(date: Long?): Flow<List<FoodEntry>> {
        return foodDbDao.observeAllFoodExpired(date)
    }

    // detect DONE/CONSUMED FOOD changes
    override fun observeAllFoodConsumed(): Flow<List<FoodEntry>> {
        return foodDbDao.observeAllFoodConsumed()
    }

    // retrieve ALL KIND OF FOOD  without regarding expiring date
    override suspend fun loadAllFood(): MutableList<FoodEntry> {
        return foodDbDao.loadAllFood()
    }

    // retrieve EXPIRING FOOD
    override suspend fun loadAllFoodExpiring(date: Long?): MutableList<FoodEntry> {
        return foodDbDao.loadAllFoodExpiring(date)
    }

    // retrieve EXPIRING FOOD TODAY
    override suspend fun loadFoodExpiringToday(daybefore: Long?, dayafter: Long?): MutableList<FoodEntry> {
        return foodDbDao.loadFoodExpiringToday(daybefore, dayafter)
    }

    // retrieve DEAD/EXPIRED FOOD
    override suspend fun loadAllFoodDead(date: Long?): MutableList<FoodEntry> {
        return foodDbDao.loadAllFoodDead(date)
    }

    // retrieve DONE/CONSUMED FOOD
    override suspend fun loadAllFoodSaved(): MutableList<FoodEntry> {
        return foodDbDao.loadAllFoodSaved()
    }

    override suspend fun loadFoodById(id: Int): FoodEntry {
        return foodDbDao.loadFoodById(id)
    }


    //----------------------------------------- INSERT ---------------------------------------------
    override suspend fun insertFoodEntry(foodEntry: FoodEntry) {
        foodDbDao.insertFoodEntry(foodEntry)
    }


    //------------------------------------------ UPDATE---------------------------------------------
    override suspend fun updateFoodEntry(foodEntry: FoodEntry) {
        foodDbDao.updateFoodEntry(foodEntry)
    }

    override suspend fun updateDoneField(done: Int, id: Int) {
        foodDbDao.updateDoneField(done, id)
    }

    // delete single record
    override suspend fun deleteFoodEntry(foodEntry: FoodEntry) {
        foodDbDao.deleteFoodEntry(foodEntry)
    }

    //------------------------------------------- DROP TABLE ---------------------------------------
    override suspend fun dropTable() {
        foodDbDao.dropTable()
    }


}
