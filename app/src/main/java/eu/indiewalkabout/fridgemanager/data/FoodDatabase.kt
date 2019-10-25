package eu.indiewalkabout.fridgemanager.data

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.arch.persistence.room.migration.Migration
import android.content.Context
import android.util.Log

@Database(entities = [FoodEntry::class], version = 2, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class FoodDatabase : RoomDatabase() {

    // get for the dao
    abstract fun foodDbDao(): FoodDbDao

    companion object {
        private val TAG = FoodDatabase::class.java.simpleName

        // lock for synchro
        private val LOCK = Any()
        private val DBNAME = "FoodDB"
        private var sDbInstance: FoodDatabase? = null

        internal val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE FOODLIST " + "ADD COLUMN done INTEGER NOT NULL DEFAULT 0")
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
        }

        fun getsDbInstance(context: Context): FoodDatabase? {
            if (sDbInstance == null) {
                synchronized(LOCK) {
                    Log.d(TAG, "Creating App db singleton instance...")
                    sDbInstance = Room.databaseBuilder(context.applicationContext, FoodDatabase::class.java, FoodDatabase.DBNAME)
                            //.allowMainThreadQueries() // TODO : temporary for debugging, delete this
                            .addMigrations(MIGRATION_1_2)
                            .build()
                }

            }
            Log.d(TAG, "Db created")
            return sDbInstance
        }
    }


}