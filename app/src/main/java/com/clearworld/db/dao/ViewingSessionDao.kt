package com.clearworld.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.clearworld.db.entity.ViewingSession

@Dao
interface ViewingSessionDao {

    /** セッション開始時に呼ぶ。endTime=nullで挿入。 */
    @Insert
    suspend fun insert(session: ViewingSession): Long

    /** セッション終了時にendTimeを更新する。 */
    @Update
    suspend fun update(session: ViewingSession)

    /** 指定日・ブロックの合計視聴秒数を返す。 */
    @Query("""
        SELECT COALESCE(SUM((endTime - startTime) / 1000), 0)
        FROM viewing_sessions
        WHERE date = :date AND blockType = :blockType AND endTime IS NOT NULL
    """)
    suspend fun getTotalSeconds(date: String, blockType: String): Long

    /** セッションIDで取得（endTime更新用）。 */
    @Query("SELECT * FROM viewing_sessions WHERE id = :id")
    suspend fun getById(id: Long): ViewingSession?
}
