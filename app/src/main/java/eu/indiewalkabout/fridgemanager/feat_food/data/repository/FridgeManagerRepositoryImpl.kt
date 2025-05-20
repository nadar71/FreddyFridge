package eu.indiewalkabout.fridgemanager.feat_food.data.repository

import eu.indiewalkabout.fridgemanager.feat_food.data.local.db.FoodDbDao
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.domain.repository.FridgeManagerRepository
import javax.inject.Inject

class FridgeManagerRepositoryImpl @Inject constructor(
    private val foodDbDao: FoodDbDao
): FridgeManagerRepository {


    //----------------------------------- QUERY ----------------------------------------------------
    // retrieve ALL KIND OF FOOD  without regarding expiring date
    override suspend fun loadAllFood_no_livedata(): MutableList<FoodEntry> {
        return foodDbDao.loadAllFood_no_livedata()
    }

    // retrieve EXPIRING FOOD no livedata
    override suspend fun loadAllFoodExpiring_no_livedata(date: Long?): MutableList<FoodEntry> {
        return foodDbDao.loadAllFoodExpiring_no_livedata(date)
    }

    // retrieve EXPIRING FOOD TODAY no LiveData
    override suspend fun loadFoodExpiringToday_no_livedata(daybefore: Long?, dayafter: Long?): MutableList<FoodEntry> {
        return foodDbDao.loadFoodExpiringToday_no_livedata(daybefore, dayafter)
    }

    // retrieve DEAD/EXPIRED FOOD
    override suspend fun loadAllFoodDead_no_livedata(date: Long?): MutableList<FoodEntry> {
        return foodDbDao.loadAllFoodDead_no_livedata(date)
    }

    // retrieve DONE/CONSUMED FOOD
    override suspend fun loadAllFoodSaved_no_livedata(): MutableList<FoodEntry> {
        return foodDbDao.loadAllFoodSaved_no_livedata()
    }

    override suspend fun loadFoodById_no_livedata(id: Int): FoodEntry {
        return foodDbDao.loadFoodById_no_livedata(id)
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