package com.clearworld.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
    version = 1,
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
         * データベースのシングルトンインスタンスを取得する。
         * アプリ全体で1つだけ生成される。
         */
        fun getInstance(context: Context): ClearWorldDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    ClearWorldDatabase::class.java,
                    "clear_world.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
