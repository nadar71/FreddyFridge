package eu.indiewalkabout.fridgemanager.util;

import android.os.Build;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GenericUtility {


    // check primitive type (expand with all the others....)
    public static String getPrimitiveType(int var){
        return "int";
    }

    public static String getPrimitiveType(String var){
        return "String";
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Return a pseudo-random int based on api level, for retrocompat;
     * I return random value in the interval, margins included.
     * @param min
     * @param max
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    public static int randRange_ApiCheck(int min, int max){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return ThreadLocalRandom.current().nextInt(min, max + 1);
        else {
            Random rand = new Random(System.currentTimeMillis());
            return rand.nextInt((max - min +1 ) + min);
        }
    }

}
