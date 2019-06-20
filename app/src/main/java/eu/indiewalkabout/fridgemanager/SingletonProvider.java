package eu.indiewalkabout.fridgemanager;

import android.app.Application;
import android.content.Context;

import eu.indiewalkabout.fridgemanager.data.FoodDatabase;


/**
 * -------------------------------------------------------------------------------------------------
 * Class used for access singletons and application context wherever in the app
 * NB : register in manifest in <Application  android:name=".SingletonProvider" >... </Application>
 * -------------------------------------------------------------------------------------------------
 */
public class SingletonProvider extends Application {

    private AppExecutors mAppExecutors;

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        // mAppExecutors = new AppExecutors.getInstance();

        sContext = getApplicationContext();


    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Return singleton db instance
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    public FoodDatabase getDatabase() {
        return FoodDatabase.getsDbInstance(this);
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Return depository singleton instance
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    public FridgeManagerRepository getRepository() {
        return FridgeManagerRepository.getInstance(getDatabase());
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Return application context wherever we are in the app
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    public static Context getsContext(){
        return sContext;
    }

}
