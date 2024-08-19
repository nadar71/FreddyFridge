package eu.indiewalkabout.fridgemanager.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import eu.indiewalkabout.fridgemanager.domain.model.FoodEntry

@Database(
    entities = [FoodEntry::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateConverter::class)

abstract class FoodDatabase : RoomDatabase() {
    abstract fun foodDbDao(): FoodDbDao

    companion object {
        private const val DBNAME = "FoodDB"

        internal val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE FOODLIST " +
                        "ADD COLUMN done INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDbInstance(context: Context): FoodDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                FoodDatabase::class.java,
                DBNAME
            )
                //.allowMainThreadQueries() // TODO : temporary for debugging, delete this
                .addMigrations(MIGRATION_1_2)
                .build()
        }
    }


}