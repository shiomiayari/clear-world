package com.clearworld.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 水槽の現在状態。常にid=1の1行だけ存在する。
 * 透明度が変化するたびにこの行をUPDATEする。
 */
@Entity(tableName = "aquarium_state")
data class AquariumState(
    @PrimaryKey
    val id: Int = 1,

    /** 現在の透明度（0.0〜100.0）。初日は60.0からスタート。 */
    val transparency: Float = 60f,

    /** 最終更新時刻（UnixTime ミリ秒） */
    val lastUpdated: Long = System.currentTimeMillis()
)
