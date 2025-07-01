package eu.indiewalkabout.fridgemanager.feat_food.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry

@Database(
    entities = [FoodEntry::class],
    version = 7,
    exportSchema = false
)
@TypeConverters(DateConverter::class)

abstract class FoodDatabase : RoomDatabase() {
    abstract fun foodDbDao(): FoodDbDao

    companion object {
        private const val DBNAME = "FoodDB"

        internal val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE FOODLIST " +
                            "ADD COLUMN done INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

        internal val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // migration from Date -> LocalDate field
            }
        }

        // added quantity column
        internal val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE FOODLIST " +
                            "ADD COLUMN quantity INTEGER NOT NULL DEFAULT 1"
                )
            }
        }

        // added consumedAt and timezone columns
        internal val MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE FOODLIST ADD COLUMN CONSUMED_AT INTEGER DEFAULT 1")
                database.execSQL("ALTER TABLE FOODLIST ADD COLUMN timezone TEXT DEFAULT 'UTC'")
            }
        }

        // change column name from timezone to timezoneId
        internal val MIGRATION_5_6: Migration = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE FOODLIST RENAME COLUMN timezone TO timezoneId")
            }
        }

        // change column name from quantity to num_ord
        internal val MIGRATION_6_7: Migration = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE FOODLIST RENAME COLUMN quantity TO num_ord")
            }
        }

        fun getDbInstance(context: Context): FoodDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                FoodDatabase::class.java,
                DBNAME
            )
                .addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3,
                    MIGRATION_3_4,
                    MIGRATION_4_5,
                    MIGRATION_5_6,
                    MIGRATION_6_7
                )
                .build()
        }
    }


}