package eu.indiewalkabout.fridgemanager.data.repository

import androidx.lifecycle.LiveData
import eu.indiewalkabout.fridgemanager.data.local.db.FoodDbDao
import eu.indiewalkabout.fridgemanager.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.domain.repository.FridgeManagerRepository

class FridgeManagerRepositoryImpl(
    private val foodDbDao: FoodDbDao
): FridgeManagerRepository {


    //----------------------------------- QUERY ----------------------------------------------------
    // --> with NO live data <--
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




    // --> with live data ( temporary retrocompatibility) <--   TODO : to be deleted after no more using
    // retrieve ALL KIND OF FOOD  without regarding expiring date
    fun loadAllFood(): LiveData<MutableList<FoodEntry>> {
        return foodDbDao.loadAllFood()
    }

    // retrieve EXPIRING FOOD
    fun loadAllFoodExpiring(date: Long?): LiveData<MutableList<FoodEntry>> {
        return foodDbDao.loadAllFoodExpiring(date)
    }

    // retrieve EXPIRING FOOD TODAY with LiveData
    fun loadFoodExpiringToday(daybefore: Long?, dayafter: Long?): LiveData<MutableList<FoodEntry>> {
        return foodDbDao.loadFoodExpiringToday(daybefore, dayafter)
    }

    // retrieve DEAD/EXPIRED FOOD
    fun loadAllFoodDead(date: Long?): LiveData<MutableList<FoodEntry>> {
        return foodDbDao.loadAllFoodDead(date)
    }

    // retrieve DONE/CONSUMED FOOD
    fun loadAllFoodSaved(): LiveData<MutableList<FoodEntry>> {
        return foodDbDao.loadAllFoodSaved()
    }

    fun loadFoodById(id: Int): LiveData<FoodEntry> {
        return foodDbDao.loadFoodById(id)
    }


    //----------------------------------------- INSERT ---------------------------------------------
    override fun insertFoodEntry(foodEntry: FoodEntry) {
        foodDbDao.insertFoodEntry(foodEntry)
    }


    //------------------------------------------ UPDATE---------------------------------------------
    override fun updateFoodEntry(foodEntry: FoodEntry) {
        foodDbDao.updateFoodEntry(foodEntry)
    }

    override fun updateDoneField(done: Int, id: Int) {
        foodDbDao.updateDoneField(done, id)
    }

    // delete single record
    override fun deleteFoodEntry(foodEntry: FoodEntry) {
        foodDbDao.deleteFoodEntry(foodEntry)
    }

    //------------------------------------------- DROP TABLE ---------------------------------------
    override fun dropTable() {
        foodDbDao.dropTable()
    }


}