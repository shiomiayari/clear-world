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

    /** このブロックで節約した秒数。長いほど色が鮮やか。 */
    val savedSeconds: Long,

    /** 魚のサイズ: "small" / "medium" / "large" */
    val size: String,

    /** 色相 (0〜360)。節約秒数が長いほど鮮やかな色になる。 */
    val colorHue: Int,

    /** 彩度 (0.0〜1.0)。節約秒数が長いほど高い。 */
    val colorSaturation: Float,

    /** falseになったら泡になって消える */
    val isAlive: Boolean = true
)
