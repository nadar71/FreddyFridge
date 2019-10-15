package eu.indiewalkabout.fridgemanager.util

import android.os.Build

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

}
