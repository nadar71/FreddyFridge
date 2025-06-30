package eu.indiewalkabout.fridgemanager.core.util

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import java.util.concurrent.ThreadLocalRandom
import android.provider.Settings
import android.widget.Toast


object GenericUtility {

    // check primitive type (expand with all the others....)
    fun getPrimitiveType(`var`: Int): String {
        return "int"
    }

    fun getPrimitiveType(`var`: String): String {
        return "String"
    }


    // Return a pseudo- random int based on api level, for retrocompat;
    // I return random value in the interval, margins included.
    // @param min
    // @param max
    // @return
    fun randRange_ApiCheck(min: Int, max: Int): Int {
        return ThreadLocalRandom.current().nextInt(min, max + 1)
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


    // TODO : must recreate with new procedure
    fun showRandomizedInterstAds(upperLimit: Int, activity: Activity) {
        val guess = randRange_ApiCheck(1, 10)
        if (guess <= upperLimit) {
            // admob interstitial ads
            // showInterstitialAd()

            // unity interstitial
            //NO MORE PRESENT
            // App.displayUnityInterstitialAd(activity, "interstitial")

        }
    }


    fun getScreenWidthDp(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        return (displayMetrics.widthPixels / displayMetrics.density).toInt()
    }

    fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    fun openAppStore(context: Context,appPackageName: String) {
        val context = context
        // val appPackageName = context.packageName
        val marketUri_01 = Uri.parse("market://details?id=$appPackageName")
        val marketUri_02 = Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")

        try {
            Log.d("openAppStore", "store uri: $marketUri_01")
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    marketUri_01
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            try {
                Log.d("openAppStore", "store uri: $marketUri_02")
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        marketUri_02
                    )
                )
            } catch (e: ActivityNotFoundException){
                Log.e("OpenAppStore", "Error opening app store", e)
            }
        }
    }


    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Could not open app settings.", Toast.LENGTH_SHORT).show()
        }
    }



}
