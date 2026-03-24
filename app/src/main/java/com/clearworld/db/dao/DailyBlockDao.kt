package com.clearworld.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.clearworld.db.entity.DailyBlock

@Dao
interface DailyBlockDao {

    /**
     * 集計結果を保存する。
     * 既に同じ (date, blockType) があれば上書き（REPLACE）。
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(dailyBlock: DailyBlock)

    /** 指定日・ブロックの記録を取得。なければnull。 */
    @Query("SELECT * FROM daily_blocks WHERE date = :date AND blockType = :blockType")
    suspend fun get(date: String, blockType: String): DailyBlock?
}
