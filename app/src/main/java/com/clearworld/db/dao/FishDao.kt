package com.clearworld.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.clearworld.db.entity.Fish
import kotlinx.coroutines.flow.Flow

@Dao
interface FishDao {

    /** 新しい魚を追加する。 */
    @Insert
    suspend fun insert(fish: Fish): Long

    /** 水槽にいる生きている魚を全件取得（ウィジェット表示用）。 */
    @Query("SELECT * FROM fish WHERE isAlive = 1 ORDER BY createdAt DESC")
    fun observeAliveFish(): Flow<List<Fish>>

    /** 視聴時間が増えたとき、魚をすべて泡にする。 */
    @Query("UPDATE fish SET isAlive = 0 WHERE isAlive = 1")
    suspend fun killAllFish()
}
