package eu.indiewalkabout.fridgemanager.feat_food.domain.model


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.getLocalDateFormat
import eu.indiewalkabout.fridgemanager.feat_food.data.local.db.DateConverter
import java.time.LocalDate

@Entity(tableName = "FOODLIST")
data class FoodEntry (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String? = null,
    @ColumnInfo(name = "EXPIRING_AT") @TypeConverters(DateConverter::class)
    var expiringAt: LocalDate? = null, // localDate -> Long/milliseconds to db by typeConverter
    @ColumnInfo(name = "CONSUMED_AT") @TypeConverters(DateConverter::class)
    var consumedAt: LocalDate? = null, // localDate -> Long/milliseconds to db by typeConverter
    var timezoneId: String? = null,    // store timezone id string like "Europe/Rome"
    var order_number: Int = 1,
    var done: Int = 0,                 // 1: food consumed, 0: food not consumed yet
    )


fun FoodEntry.toFoodEntryUI(): FoodEntryUI {
    return FoodEntryUI (
        id = id,
        name = name,
        expiringAtLocalDate = expiringAt,
        expiringAtUI = expiringAt?.format(getLocalDateFormat()) ?: "",
        consumedAtLocalDate = consumedAt,
        consumedAtUI = consumedAt?.format(getLocalDateFormat()) ?: "",
        order_number = this@toFoodEntryUI.order_number,
        timezoneId = this@toFoodEntryUI.timezoneId,
        done = done,
    )
}


