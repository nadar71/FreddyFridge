package eu.indiewalkabout.fridgemanager.data;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import java.util.Date;

public class DateConverter {
    private final static String TAG = DateConverter.class.getSimpleName();

    @TypeConverter
    public static Date toDate(Long timestamp){
        // Log.d(TAG, "from milllisec to Date : " + timestamp + " is " + new Date(timestamp));
        return timestamp == null ? null : new Date(timestamp);

    }
    @TypeConverter
    public static Long fromDate(Date date){
        // Log.d(TAG, "from date to millisecfromepoch: " + date + " is " + date.getTime());
        return date == null ? null : date.getTime();

    }
}
