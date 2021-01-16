package eu.indiewalkabout.fridgemanager.util

import android.app.Activity
import android.app.AlertDialog
import android.os.Build
import android.util.Log
import android.view.View
import eu.indiewalkabout.fridgemanager.ui.FoodListActivity

import java.util.Random
import java.util.concurrent.ThreadLocalRandom

object GenericUtility {


    // check primitive type (expand with all the others....)
    fun getPrimitiveType(`var`: Int): String {
        return "int"
    }

    fun getPrimitiveType(`var`: String): String {
        return "String"
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Return a pseudo- random int based on api level, for retrocompat;
     * I return random value in the interval, margins included.
     * @param min
     * @param max
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    fun randRange_ApiCheck(min: Int, max: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return ThreadLocalRandom.current().nextInt(min, max + 1)
        else {
            val rand = Random(System.currentTimeMillis())
            return rand.nextInt(max - min + 1 + min)
        }
    }


    // Generic alert
    fun showGenericBlockingAlert(title: String, msg: String, activity: Activity) {
        val dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK") { dialog, id ->
                }

        val alert = dialogBuilder.create()
        alert.setCancelable(false)
        alert.setTitle(title)
        alert.show()
    }


    // Make bottom navigation bar and status bar hide, without resize when reappearing
    fun hideStatusNavBars(activity: Activity) {
        // minsdk version is 19, no need code for lower api
        val decorView = activity.window.decorView

        // hide status bar
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        // hide navigation bar
        val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = uiOptions
    }



}
