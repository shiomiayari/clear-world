package com.clearworld.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.clearworld.db.entity.AquariumState
import kotlinx.coroutines.flow.Flow

@Dao
interface AquariumStateDao {

    /**
     * 水槽の状態を保存する。
     * id=1固定なので、既存行があれば上書き（REPLACE）。
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(state: AquariumState)

    /** 現在の水槽状態を取得。初回はnull。 */
    @Query("SELECT * FROM aquarium_state WHERE id = 1")
    suspend fun get(): AquariumState?

    /**
     * 透明度をリアルタイムで監視するFlow。
     * ウィジェットやUIがこれを購読して自動更新する。
     */
    @Query("SELECT * FROM aquarium_state WHERE id = 1")
    fun observe(): Flow<AquariumState?>
}
