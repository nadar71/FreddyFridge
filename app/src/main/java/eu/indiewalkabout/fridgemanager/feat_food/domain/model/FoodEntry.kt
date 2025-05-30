package eu.indiewalkabout.fridgemanager.feat_food.domain.model


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.getLocalDateFormat
import eu.indiewalkabout.fridgemanager.feat_food.data.local.db.DateConverter
import eu.indiewalkabout.fridgemanager.feat_food.data.local.db.DateConverter.toLocalDateFromLong
import java.time.LocalDate
import java.util.Date

@Entity(tableName = "FOODLIST")
data class FoodEntry (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String? = null,
    @ColumnInfo(name = "EXPIRING_AT") @TypeConverters(DateConverter::class)
    var expiringAt: LocalDate? = null, // localDate converted Long/milliseconds by typeConverter
    var quantity: Int = 1,
    var done: Int = 0, // 1: food consumed, 0: food not consumed yet
    )


fun FoodEntry.toFoodEntryUI(): FoodEntryUI {
    return FoodEntryUI (
        name = name,
        expiringAtLocalDate = expiringAt,
        expiringAtUI = expiringAt?.format(getLocalDateFormat()) ?: "",
        quantity = quantity,
        done = done,
    )
}


