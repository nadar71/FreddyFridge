package eu.indiewalkabout.fridgemanager.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import eu.indiewalkabout.fridgemanager.R;

public class PreferenceUtility {

    private static final String TAG = PreferenceUtility.class.getSimpleName();

    public static int getDaysCount(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String key   = context.getString(R.string.days_before_deadline_count);
        String  default_value_s = context.getString(R.string.days_before_default);
        int default_value = Integer.parseInt(default_value_s);
        int daysBefore = prefs.getInt(key, default_value);
        return daysBefore;
    }


    public static int getHoursCount(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
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
        String key   = context.getString(R.string.hours_freq_today_deadline_count);
        String default_value = context.getString(R.string.today_hours_repeat_default);
        String hoursFreq_s = prefs.getString(key,default_value);
        int hoursFreq = Integer.parseInt(hoursFreq_s);
        return hoursFreq;
    }



}