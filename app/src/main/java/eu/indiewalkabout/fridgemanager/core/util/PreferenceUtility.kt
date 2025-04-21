package eu.indiewalkabout.fridgemanager.core.util

import android.content.Context
import androidx.preference.PreferenceManager
import eu.indiewalkabout.fridgemanager.R

object PreferenceUtility {

    // Get the count of days in advance for alerting for expiring foods
    fun getDaysCount(context: Context): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val key = context.getString(R.string.settings_days_before_deadline_count)
        val default_value = context.getString(R.string.settings_days_before_default)    // the default num days values
        val daysBefore_s = prefs.getString(key, default_value)
        return Integer.parseInt(daysBefore_s!!)
    }


    // Get the hours interval for alerting during the day
    fun getHoursCount(context: Context): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        /*
        // DON'T KNOW WHY THIS NOT WORKING....the type seems correct
        // here : https://stackoverflow.com/questions/7257232/sharedpreferences-getint-results-in-classcastexception-why
        String key   = context.getString(R.string.hours_freq_today_deadline_count);
        Log.d(TAG, "getHoursCount: key : "+key+" key type : "+key.getClass().getSimpleName());
        int  default_value = Integer.parseInt(context.getString(R.string.today_hours_repeat_default));
        Log.d(TAG, "getHoursCount: default_value : "+default_value+" default_value type : "+GenericUtility.getPrimitiveType(default_value));
        Log.d(TAG, "getHoursCount: context.getString(R.string.today_hours_repeat_default) : "+context.getString(R.string.today_hours_repeat_default));
        int hoursFreq = prefs.getInt(key, default_value);
        */
        val key = context.getString(R.string.settings_hours_freq_today_deadline_count)
        val default_value = context.getString(R.string.settings_today_hours_repeat_default)   // the default num days values
        val hoursFreq_s = prefs.getString(key, default_value)
        return Integer.parseInt(hoursFreq_s!!)
    }


}