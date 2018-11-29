package eu.indiewalkabout.fridgemanager.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

@Database(entities = {FoodEntry.class}, version = 2, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class FoodDatabase extends RoomDatabase {
    private static final String TAG = FoodDatabase.class.getSimpleName();
    // lock for synchro
    private static final Object LOCK   = new Object();
    private static final String DBNAME = "FoodDB";
    private static FoodDatabase sDbInstance ;

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE FOODLIST "+"ADD COLUMN done INTEGER NOT NULL DEFAULT 0");
            // Create the new table
            /*
            database.execSQL(
                    "CREATE TABLE new_FOODLIST (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                                                "done INTEGER, " +
                                                "name TEXT, " +
                                                "EXPIRING_AT INTEGER)");

            // Copy the data
            database.execSQL(
                    "INSERT INTO new_FOODLIST (id, name, EXPIRING_AT) SELECT id, name, EXPIRING_AT FROM FOODLIST");

            // Remove the old table
            database.execSQL("DROP TABLE FOODLIST");

            // Change the table name to the correct one
            database.execSQL("ALTER TABLE new_FOODLIST RENAME TO FOODLIST");
            */

        }
    };

    public static FoodDatabase getsDbInstance(Context context){
        if(sDbInstance == null){
            synchronized (LOCK){
                Log.d(TAG, "Creating App db singleton instance...");
                sDbInstance = Room.databaseBuilder(context.getApplicationContext(), FoodDatabase.class,FoodDatabase.DBNAME)
                        .allowMainThreadQueries() // TODO : temporary for debugging, delete this
                        .addMigrations(MIGRATION_1_2)
                        .build();
            }

        }
        Log.d(TAG, "Db created");
        return sDbInstance;
    }

    public abstract FoodDbDao foodDbDao();

}