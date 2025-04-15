package eu.indiewalkabout.fridgemanager.domain.model


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import eu.indiewalkabout.fridgemanager.data.local.db.DateConverter
import java.util.Date

@Entity(tableName = "FOODLIST")
data class FoodEntry (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String? = null,
    @ColumnInfo(name = "EXPIRING_AT") @TypeConverters(DateConverter::class)
    var expiringAt: Date? = null,
    var done: Int = 0,
    )


fun FoodEntry.toFoodEntryUI(): FoodEntryUI {
    return FoodEntryUI (
        name = name,
        expiringAt = expiringAt,
        done = done,
    )
}


