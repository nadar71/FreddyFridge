package eu.indiewalkabout.fridgemanager.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

@Database(entities = {FoodEntry.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class FoodDatabase extends RoomDatabase {
    private static final String TAG = FoodDatabase.class.getSimpleName();
    // lock for synchro
    private static final Object LOCK   = new Object();
    private static final String DBNAME = "FoodDB";
    private static FoodDatabase sDbInstance ;

    public static FoodDatabase getsDbInstance(Context context){
        if(sDbInstance == null){
            synchronized (LOCK){
                Log.d(TAG, "Creating App db singleton instance...");
                sDbInstance = Room.databaseBuilder(context.getApplicationContext(), FoodDatabase.class,FoodDatabase.DBNAME)
                        .allowMainThreadQueries() // TODO : temporary for debugging, delete this
                        .build();
            }

        }
        Log.d(TAG, "Db created");
        return sDbInstance;
    }

    public abstract FoodDbDao foodDbDao();

}