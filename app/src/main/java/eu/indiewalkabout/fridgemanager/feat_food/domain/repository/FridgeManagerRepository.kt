package eu.indiewalkabout.fridgemanager.feat_food.domain.repository

import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import kotlinx.coroutines.flow.Flow

interface FridgeManagerRepository {
    //----------------------------------- QUERY ----------------------------------------------------
    fun observeAllFood(): Flow<List<FoodEntry>>
    fun observeAllFoodExpiring(date: Long?): Flow<List<FoodEntry>>
    fun observeFoodExpiringToday(daybefore: Long?, dayafter: Long?): Flow<List<FoodEntry>>
    fun observeAllFoodExpired(date: Long?): Flow<List<FoodEntry>>
    fun observeAllFoodConsumed(): Flow<List<FoodEntry>>
    suspend fun loadAllFood(): MutableList<FoodEntry>
    suspend fun loadAllFoodExpiring(date: Long?): MutableList<FoodEntry>
    suspend fun loadFoodExpiringToday(daybefore: Long?, dayafter: Long?): MutableList<FoodEntry>
    suspend fun loadAllFoodDead(date: Long?): MutableList<FoodEntry>
    suspend fun loadAllFoodSaved(): MutableList<FoodEntry>
    suspend fun loadFoodById(id: Int): FoodEntry
    //----------------------------------------- INSERT ---------------------------------------------
    suspend fun insertFoodEntry(foodEntry: FoodEntry)
    //------------------------------------------ UPDATE---------------------------------------------
    suspend fun updateFoodEntry(foodEntry: FoodEntry)
    suspend fun updateDoneField(done: Int, id: Int)
    suspend fun deleteFoodEntry(foodEntry: FoodEntry)
    //------------------------------------------- DROP TABLE ---------------------------------------
    suspend fun dropTable()


}
