package eu.indiewalkabout.fridgemanager.domain.model


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import eu.indiewalkabout.fridgemanager.data.local.db.DateConverter

import java.util.Date

@Entity(tableName = "FOODLIST")
class FoodEntry {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var name: String? = null

    @ColumnInfo(name = "EXPIRING_AT")
    @TypeConverters(DateConverter::class)
    var expiringAt: Date? = null

    var done: Int = 0

    // new FoodEntry
    @Ignore
    constructor(done: Int, name: String, expiringAt: Date) {
        this.done = done
        this.name = name
        this.expiringAt = expiringAt
    }


    // new FoodEntry for db
    constructor(id: Int, done: Int, name: String, expiringAt: Date) {
        this.id = id
        this.done = done
        this.name = name
        this.expiringAt = expiringAt
    }
}
