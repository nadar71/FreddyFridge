package eu.indiewalkabout.fridgemanager;


import eu.indiewalkabout.fridgemanager.data.FoodDatabase;

/**
 * -------------------------------------------------------------------------------------------------
 * App repository
 * -------------------------------------------------------------------------------------------------
 */
public class FridgeManagerRepository {
    private static final String TAG = FridgeManagerRepository.class.getSimpleName();

    private static FridgeManagerRepository sInstance;

    private FoodDatabase foodDb;


    private FridgeManagerRepository(FoodDatabase foodDb){
        this.foodDb = foodDb;
    }


    public static FridgeManagerRepository getInstance(final FoodDatabase database) {
        if (sInstance == null) {
            synchronized (FridgeManagerRepository.class) {
                if (sInstance == null) {
                    sInstance = new FridgeManagerRepository(database);
                }
            }
        }
        return sInstance;
    }





}
