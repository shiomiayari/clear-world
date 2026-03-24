package com.clearworld.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 水槽にいる魚。節約時間が達成されるたびに生成される。
 * 視聴時間が増えたときに isAlive=false になり、泡になって消える。
 */
@Entity(tableName = "fish")
data class Fish(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 誕生時刻（UnixTime ミリ秒） */
    val createdAt: Long = System.currentTimeMillis(),

    /** 誕生したブロック: "morning" / "afternoon" / "evening" / "midnight" */
    val blockType: String,

    /**
     * このブロックで節約した秒数。
     * - 長いほど色が鮮やか
     * - midnight → 大きい魚
     * - evening  → 中くらいの魚
     * - morning / afternoon → 小さい魚
     */
    val savedSeconds: Long,

    /** falseになったら泡になって消える */
    val isAlive: Boolean = true
)
