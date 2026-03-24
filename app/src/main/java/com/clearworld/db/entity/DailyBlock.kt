package com.clearworld.db.entity

import androidx.room.Entity

/**
 * 日付×時間帯ブロックごとの視聴合計。
 * viewing_sessionsを集計した結果をキャッシュする。
 *
 * 主キーは (date, blockType) の複合キー。
 * 例: date="2026-03-25", blockType="morning" → その日の朝ブロックの合計
 */
@Entity(
    tableName = "daily_blocks",
    primaryKeys = ["date", "blockType"]
)
data class DailyBlock(
    /** 日付文字列（例: "2026-03-25"） */
    val date: String,

    /** 時間帯ブロック: "morning" / "afternoon" / "evening" / "midnight" */
    val blockType: String,

    /** そのブロックの合計視聴秒数 */
    val totalSeconds: Long = 0L
)
