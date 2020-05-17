package eu.indiewalkabout.fridgemanager.data

import androidx.room.TypeConverter
import android.util.Log

import java.util.Date

object DateConverter {
    private val TAG = DateConverter::class.java.simpleName

    @TypeConverter
    @JvmStatic
    fun toDate(timestamp: Long?): Date? {
        // Log.d(TAG, "from milllisec to Date : " + timestamp + " is " + new Date(timestamp));
        return if (timestamp == null) null else Date(timestamp)

    }

    @TypeConverter
    @JvmStatic
    fun fromDate(date: Date?): Long? {
        // Log.d(TAG, "from date to millisecfromepoch: " + date + " is " + date.getTime());
        return date?.time

    }
}
