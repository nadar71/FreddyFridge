package eu.indiewalkabout.fridgemanager.feat_food.data.local.db

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

import java.util.Date

object DateConverter {

    private val zoneId = ZoneId.systemDefault()

    @TypeConverter
    @JvmStatic
    fun fromLocalDateToLong(date: LocalDate?): Long? {
        return date?.atStartOfDay(zoneId)
            ?.toInstant()
            ?.toEpochMilli()
    }

    @TypeConverter
    @JvmStatic
    fun toLocalDateFromLong(timestamp: Long?): LocalDate? {
        return timestamp?.let {
            Instant.ofEpochMilli(it).atZone(zoneId).toLocalDate()
        }
    }

    @TypeConverter
    @JvmStatic
    fun toDateFromLong(timestamp: Long?): Date? {
        // Log.d(TAG, "from milllisec to Date : " + timestamp + " is " + new Date(timestamp));
        return if (timestamp == null) null else Date(timestamp)

    }

    @TypeConverter
    @JvmStatic
    fun fromDateToLong(date: Date?): Long? {
        // Log.d(TAG, "from date to millisecfromepoch: " + date + " is " + date.getTime());
        return date?.time

    }
}
