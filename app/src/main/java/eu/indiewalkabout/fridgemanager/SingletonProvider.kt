package eu.indiewalkabout.fridgemanager

import android.app.Application
import android.content.Context

import eu.indiewalkabout.fridgemanager.data.FoodDatabase


/**
 * -------------------------------------------------------------------------------------------------
 * Class used for access singletons and application context wherever in the app
 * NB : register in manifest in <Application android:name=".SingletonProvider">... </Application>
 * -------------------------------------------------------------------------------------------------
 */
class SingletonProvider : Application() {

    private val mAppExecutors: AppExecutors? = null

    /**
     * ---------------------------------------------------------------------------------------------
     * Return singleton db instance
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    val database: FoodDatabase
        get() = FoodDatabase.getsDbInstance(this)


    /**
     * ---------------------------------------------------------------------------------------------
     * Return depository singleton instance
     * @return
     * ---------------------------------------------------------------------------------------------
     */
    val repository: FridgeManagerRepository
        get() = FridgeManagerRepository.getInstance(database)

    override fun onCreate() {
        super.onCreate()
        // mAppExecutors = new AppExecutors.getInstance();

        sContext = applicationContext


    }

    companion object {

        private var sContext: Context? = null

        /**
         * ---------------------------------------------------------------------------------------------
         * Return application context wherever we are in the app
         * @return
         * ---------------------------------------------------------------------------------------------
         */
        fun getsContext(): Context? {
            return sContext
        }
    }

}
