package eu.indiewalkabout.fridgemanager.util


import android.content.Context
import android.text.format.DateUtils

import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import java.util.concurrent.TimeUnit


/**
 * ---------------------------------------------------------------------------------------------
 * Class for handling a bunch of date conversions.
 * ---------------------------------------------------------------------------------------------
 */
object DateUtility {

    /* Milliseconds in a day */
    val DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(1)

    /**
     * ---------------------------------------------------------------------------------------------
     * This method returns the number of milliseconds (UTC time) for today's date at midnight in
     * the local time zone. For example, if you live in California and the day is September 20th,
     * 2016 and it is 6:30 PM, it will return 1474329600000. Now, if you plug this number into an
     * Epoch time converter, you may be confused that it tells you this time stamp represents 8:00
     * PM on September 19th local time, rather than September 20th. We're concerned with the GMT
     * date here though, which is correct, stating September 20th, 2016 at midnight.
     *
     *
     * As another example, if you are in Hong Kong and the day is September 20th, 2016 and it is
     * 6:30 PM, this method will return 1474329600000. Again, if you plug this number into an Epoch
     * time converter, you won't get midnight for your local time zone. Just keep in mind that we
     * are just looking at the GMT date here.
     *
     *
     * This method will ALWAYS return the date at midnight (in GMT time) for the time zone you
     * are currently in. In other words, the GMT date will always represent your date.
     *
     *
     * Since UTC / GMT time are the standard for all time zones in the world, we use it to
     * normalize our dates that are stored in the database. When we extract values from the
     * database, we adjust for the current time zone using time zone offsets.
     *
     * @return The number of milliseconds (UTC / GMT) for today's date at midnight in the local
     * time zone
     * ---------------------------------------------------------------------------------------------
     */
    /*
         * This number represents the number of milliseconds that have elapsed since January
         * 1st, 1970 at midnight in the GMT time zone.
         *//*
         * This TimeZone represents the device's current time zone. It provides us with a means
         * of acquiring the offset for local time from a UTC time stamp.
         *//*
         * The getOffset method returns the number of milliseconds to add to UTC time to get the
         * elapsed time since the epoch for our current time zone. We pass the current UTC time
         * into this method so it can determine changes to account for daylight savings time.
         *//*
         * UTC time is measured in milliseconds from January 1, 1970 at midnight from the GMT
         * time zone. Depending on your time zone, the time since January 1, 1970 at midnight (GMT)
         * will be greater or smaller. This variable represents the number of milliseconds since
         * January 1, 1970 (GMT) time.
         *//* This method simply converts milliseconds to days, disregarding any fractional days *//*
         * Finally, we convert back to milliseconds. This time stamp represents today's date at
         * midnight in GMT time. We will need to account for local time zone offsets when
         * extracting this information from the database.
         */
        val normalizedUtcMsForToday: Long
        get() {
            val utcNowMillis                  = System.currentTimeMillis()
            val currentTimeZone           = TimeZone.getDefault()
            val gmtOffsetMillis               = currentTimeZone.getOffset(utcNowMillis).toLong()
            val timeSinceEpochLocalTimeMillis = utcNowMillis + gmtOffsetMillis
            val daysSinceEpochLocal           = TimeUnit.MILLISECONDS.toDays(timeSinceEpochLocalTimeMillis)

            return TimeUnit.DAYS.toMillis(daysSinceEpochLocal)
        }


    val normalizedUtcDateForToday: Date
        get() {
            val normalizedMilli = normalizedUtcMsForToday
            return Date(normalizedMilli)
        }


    /**
     * ---------------------------------------------------------------------------------------------
     * This method returns the number of days since the epoch (January 01, 1970, 12:00 Midnight UTC)
     * in UTC time from the current date.
     *
     * @param utcDate A date in milliseconds in UTC time.
     * @return The number of days from the epoch to the date argument.
     * ---------------------------------------------------------------------------------------------
     */
    private fun elapsedDaysSinceEpoch(utcDate: Long): Long {
        return TimeUnit.MILLISECONDS.toDays(utcDate)
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * This method will return the local time midnight for the provided normalized UTC date.
     *
     * @param normalizedUtcDate UTC time at midnight for a given date. This number comes from the
     * database
     * @return The local date corresponding to the given normalized UTC date
     * ---------------------------------------------------------------------------------------------
     */
    fun getLocalMidnightFromNormalizedUtcDate(normalizedUtcDate: Long): Long {
        /* The timeZone object will provide us the current user's time zone offset */
        val timeZone = TimeZone.getDefault()
        /*
         * This offset, in milliseconds, when added to a UTC date time, will produce the local
         * time.
         */
        val gmtOffset = timeZone.getOffset(normalizedUtcDate).toLong()
        return normalizedUtcDate - gmtOffset
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Returns a date string in the format specified, which shows an abbreviated date without a
     * year.
     *
     * @param context      Used by DateUtils to format the date in the current locale
     * @param timeInMillis Time in milliseconds since the epoch (local time)
     * @return The formatted date string
     * ---------------------------------------------------------------------------------------------
     */
    private fun getReadableDateString(context: Context, timeInMillis: Long): String {
        val flags = (DateUtils.FORMAT_SHOW_DATE
                or DateUtils.FORMAT_NO_YEAR
                or DateUtils.FORMAT_SHOW_WEEKDAY)

        return DateUtils.formatDateTime(context, timeInMillis, flags)
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Convert from LocalDate to epoch (long)
     * NB : require minsdk 26
     * @param date
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    /*
    public static long fromLocalDate_to_Epoch(LocalDate date){
        ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Europe/Oslo");
        long epoch = date.atStartOfDay(zoneId).toEpochSecond();
        return epoch;
    }
     */


    /**
     * ---------------------------------------------------------------------------------------------
     * Add a day in ol Date format
     * @param date
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    fun addDays(date: Date, numDays: Int): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DATE, numDays)
        return cal.time
    }


}
