package eu.indiewalkabout.fridgemanager.data


import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters

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

    /**
     * ---------------------------------------------------------------------------------------------
     * Create a new FoodEntry
     * ---------------------------------------------------------------------------------------------
     * @param done
     * @param name
     * @param expiringAt
     */
    @Ignore
    constructor(done: Int, name: String, expiringAt: Date) {
        this.done = done
        this.name = name
        this.expiringAt = expiringAt
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Create a new FoodEntry for db
     * ---------------------------------------------------------------------------------------------
     * @param id
     * @param done
     * @param name
     * @param expiringAt
     */
    constructor(id: Int, done: Int, name: String, expiringAt: Date) {
        this.id = id
        this.done = done
        this.name = name
        this.expiringAt = expiringAt
    }
}
