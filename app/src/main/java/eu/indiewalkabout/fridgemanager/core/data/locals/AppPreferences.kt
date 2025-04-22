package eu.indiewalkabout.fridgemanager.core.data.locals

import com.chibatching.kotpref.KotprefModel


object AppPreferences : KotprefModel() {
    var days_before_deadline by intPref(0)
    var todays_howmany_each_hours by intPref(0)
    var app_opening_counter by intPref(0)
}

/*
OLD ONES:

R.string.days_before_deadline_count -> days_before_deadline // : Stores the number of days before the deadline for displaying reminders/notifications.
R.string.hours_freq_today_deadline_count -> todays_howmany_each_hours //  Stores how many notifications to send each day

<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
<map>
    <boolean name="bg_startup_tracing" value="false" />
    <string name="days_before_deadline">2</string>
    <int name="app-opening-counter" value="2" />
    <string name="todays_howmany_each_hours">6</string>
</map>



*/