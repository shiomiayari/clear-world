package com.clearworld.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * YouTube Shortsの1回の視聴セッション。
 * AccessibilityServiceが「表示開始」「離脱」を検知するたびに1行追加される。
 */
@Entity(tableName = "viewing_sessions")
data class ViewingSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** セッション開始時刻（UnixTime ミリ秒） */
    val startTime: Long,

    /**
     * セッション終了時刻（UnixTime ミリ秒）。
     * まだ視聴中の場合はnull。
     */
    val endTime: Long? = null,

    /** 時間帯ブロック: "morning" / "afternoon" / "evening" / "midnight" */
    val blockType: String,

    /** 日付文字列（例: "2026-03-25"）。6:00リセットに対応するため手動で設定する。 */
    val date: String
) {
    /** 視聴秒数。endTimeがnull（視聴中）の場合は0を返す。 */
    val durationSeconds: Long
        get() = if (endTime != null) (endTime - startTime) / 1000L else 0L
}
