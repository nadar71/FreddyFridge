package eu.indiewalkabout.fridgemanager.data.local.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import eu.indiewalkabout.fridgemanager.core.util.extensions.TAG
import eu.indiewalkabout.fridgemanager.domain.model.FoodEntry

@Database(entities = [FoodEntry::class], version = 2, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class FoodDatabase : RoomDatabase() {

    // get for the dao
    abstract fun foodDbDao(): FoodDbDao

    companion object {
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
                    sDbInstance = Room.databaseBuilder(context.applicationContext, FoodDatabase::class.java, DBNAME)
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