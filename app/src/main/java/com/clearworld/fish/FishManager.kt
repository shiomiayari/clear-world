package com.clearworld.fish

import com.clearworld.db.dao.DailyBlockDao
import com.clearworld.db.dao.FishDao
import com.clearworld.util.DateUtil

/**
 * ブロック終了時に魚の生成・消滅を管理するクラス。
 * AccessibilityServiceまたはAlarmManagerから呼ばれる。
 */
class FishManager(
    private val fishDao: FishDao,
    private val dailyBlockDao: DailyBlockDao
) {

    /**
     * ブロック終了時に呼ぶ。
     * 昨日より節約できていれば魚を生成、増えていれば魚を泡にする。
     *
     * @param blockType 終了したブロック
     */
    suspend fun onBlockEnd(blockType: String) {
        val today = DateUtil.todayString()
        val yesterday = DateUtil.yesterdayString()

        val todaySeconds = dailyBlockDao.get(today, blockType)?.totalSeconds ?: 0L
        val yesterdaySeconds = dailyBlockDao.get(yesterday, blockType)?.totalSeconds ?: 0L

        if (FishGenerator.canGenerate(todaySeconds, yesterdaySeconds)) {
            val savedSeconds = yesterdaySeconds - todaySeconds
            val fish = FishGenerator.generate(blockType, savedSeconds)
            fishDao.insert(fish)
        } else {
            // 視聴時間が増えた → 生きている魚を泡にする
            fishDao.killAllFish()
        }
    }
}
