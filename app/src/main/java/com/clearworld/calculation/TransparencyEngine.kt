package com.clearworld.calculation

import com.clearworld.db.dao.AquariumStateDao
import com.clearworld.db.dao.DailyBlockDao
import com.clearworld.db.entity.AquariumState
import com.clearworld.util.DateUtil

/**
 * 透明度の計算をDBと繋ぐエンジン。
 * TransparencyCalculator（純粋計算）を呼び出し、結果をDBに保存する。
 *
 * AccessibilityServiceがブロック終了を検知したタイミングで呼ばれる。
 */
class TransparencyEngine(
    private val aquariumStateDao: AquariumStateDao,
    private val dailyBlockDao: DailyBlockDao
) {

    /**
     * 指定ブロックの視聴が終わったタイミングで透明度を更新する。
     *
     * @param blockType 時間帯ブロック ("morning" / "afternoon" / "evening" / "midnight")
     */
    suspend fun updateAfterBlock(blockType: String) {
        val today = DateUtil.todayString()
        val yesterday = DateUtil.yesterdayString()

        val todaySeconds = dailyBlockDao.get(today, blockType)?.totalSeconds ?: 0L
        val yesterdaySeconds = dailyBlockDao.get(yesterday, blockType)?.totalSeconds ?: 0L

        val current = aquariumStateDao.get()
            ?: AquariumState(transparency = TransparencyCalculator.INITIAL_TRANSPARENCY)

        // 完全透明チェック（直近3ブロックの結果を取得）
        val recentResults = getRecentBlockResults(today, yesterday, blockType)
        val newTransparency = when {
            TransparencyCalculator.isFullyClear(recentResults) -> 100f
            else -> TransparencyCalculator.calculate(
                currentTransparency = current.transparency,
                todaySeconds = todaySeconds,
                yesterdaySeconds = yesterdaySeconds
            )
        }

        aquariumStateDao.upsert(
            current.copy(
                transparency = newTransparency,
                lastUpdated = System.currentTimeMillis()
            )
        )
    }

    /**
     * 6:00リセット時に透明度を50%に戻す。
     */
    suspend fun resetAtSix() {
        val current = aquariumStateDao.get()
            ?: AquariumState(transparency = TransparencyCalculator.RESET_TRANSPARENCY)
        aquariumStateDao.upsert(
            current.copy(
                transparency = TransparencyCalculator.RESET_TRANSPARENCY,
                lastUpdated = System.currentTimeMillis()
            )
        )
    }

    /**
     * 直近3ブロックが「良いブロック」だったかどうかのリストを返す。
     * 新しい順（今のブロックが先頭）。
     */
    private suspend fun getRecentBlockResults(
        today: String,
        yesterday: String,
        currentBlockType: String
    ): List<Boolean> {
        val allBlocks = DateUtil.blockOrder  // ["morning", "afternoon", "evening", "midnight"]
        val currentIndex = allBlocks.indexOf(currentBlockType)

        // 直近3ブロックを新しい順に取得
        val recent = mutableListOf<Boolean>()
        var idx = currentIndex
        var date = today

        repeat(3) {
            val block = allBlocks[idx]
            val todaySec = dailyBlockDao.get(date, block)?.totalSeconds ?: 0L
            val prevDate = if (date == today) yesterday else DateUtil.previousDay(date)
            val prevSec = dailyBlockDao.get(prevDate, block)?.totalSeconds ?: 0L
            recent.add(TransparencyCalculator.isGoodBlock(todaySec, prevSec))

            idx--
            if (idx < 0) {
                idx = allBlocks.size - 1
                date = yesterday
            }
        }
        return recent
    }
}
