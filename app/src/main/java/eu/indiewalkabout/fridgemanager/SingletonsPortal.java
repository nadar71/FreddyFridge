package eu.indiewalkabout.fridgemanager;

import android.app.Application;
import eu.indiewalkabout.fridgemanager.data.FoodDatabase;

public class SingletonsPortal extends Application {

    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();
        // mAppExecutors = new AppExecutors.getInstance();
    }

    public FoodDatabase getDatabase() {
        return FoodDatabase.getsDbInstance(this);
    }

    public FridgeManagerRepository getRepository() {
        return FridgeManagerRepository.getInstance(getDatabase());
    }


}
