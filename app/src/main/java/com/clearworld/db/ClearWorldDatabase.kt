package com.clearworld.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.clearworld.db.dao.AquariumStateDao
import com.clearworld.db.dao.DailyBlockDao
import com.clearworld.db.dao.FishDao
import com.clearworld.db.dao.ViewingSessionDao
import com.clearworld.db.entity.AquariumState
import com.clearworld.db.entity.DailyBlock
import com.clearworld.db.entity.Fish
import com.clearworld.db.entity.ViewingSession

@Database(
    entities = [
        ViewingSession::class,
        DailyBlock::class,
        AquariumState::class,
        Fish::class,
    ],
    version = 2,
    exportSchema = false
)
abstract class ClearWorldDatabase : RoomDatabase() {

    abstract fun viewingSessionDao(): ViewingSessionDao
    abstract fun dailyBlockDao(): DailyBlockDao
    abstract fun aquariumStateDao(): AquariumStateDao
    abstract fun fishDao(): FishDao

    companion object {
        @Volatile
        private var INSTANCE: ClearWorldDatabase? = null

        /**
         * v1→v2: Fishテーブルにsize・colorHue・colorSaturationカラムを追加。
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE fish ADD COLUMN size TEXT NOT NULL DEFAULT 'small'")
                db.execSQL("ALTER TABLE fish ADD COLUMN colorHue INTEGER NOT NULL DEFAULT 180")
                db.execSQL("ALTER TABLE fish ADD COLUMN colorSaturation REAL NOT NULL DEFAULT 0.5")
            }
        }

        fun getInstance(context: Context): ClearWorldDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    ClearWorldDatabase::class.java,
                    "clear_world.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
