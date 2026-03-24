package com.clearworld.calculation

/**
 * 水槽の透明度を計算する純粋関数の集まり。
 * DBやContextに依存しないので単体テストしやすい。
 *
 * 計算式:
 *   Δ = -(diff_seconds / 1800) × 10   // 30分差で±10%
 *   新透明度 = 現在の透明度 + Δ
 *   新透明度 = max(0, min(100, 新透明度))
 */
object TransparencyCalculator {

    const val INITIAL_TRANSPARENCY = 60f  // 初日スタート
    const val RESET_TRANSPARENCY = 50f    // 6:00リセット時のスタート

    /** 1ブロックでこれ以上見ると完全に濁る（3時間） */
    private const val MAX_BLOCK_SECONDS = 10800L

    /** 昨日比この割合以上節約したとみなす閾値（-80%） */
    private const val GOOD_BLOCK_RATIO = 0.2f  // 昨日の20%以下 = 80%節約

    /** この回数連続で良いブロックが続くと完全透明 */
    private const val FULL_CLEAR_STREAK = 3

    /**
     * 1ブロック分の新しい透明度を計算する。
     *
     * @param currentTransparency 現在の透明度 (0.0〜100.0)
     * @param todaySeconds 今日のブロックの視聴秒数
     * @param yesterdaySeconds 昨日の同ブロックの視聴秒数
     * @return 新しい透明度 (0.0〜100.0)
     */
    fun calculate(
        currentTransparency: Float,
        todaySeconds: Long,
        yesterdaySeconds: Long
    ): Float {
        // 1ブロック3時間以上 → 完全に濁る
        if (todaySeconds >= MAX_BLOCK_SECONDS) return 0f

        val diffSeconds = todaySeconds - yesterdaySeconds
        val delta = -(diffSeconds / 1800f) * 10f
        return (currentTransparency + delta).coerceIn(0f, 100f)
    }

    /**
     * そのブロックが「良いブロック」かどうか判定する。
     * 昨日比-80%（昨日の20%以下）を達成していれば良いブロック。
     * 昨日が0秒の場合は今日も0秒なら良いブロックとみなす。
     *
     * @param todaySeconds 今日の視聴秒数
     * @param yesterdaySeconds 昨日の視聴秒数
     */
    fun isGoodBlock(todaySeconds: Long, yesterdaySeconds: Long): Boolean {
        if (yesterdaySeconds == 0L) return todaySeconds == 0L
        return todaySeconds <= yesterdaySeconds * GOOD_BLOCK_RATIO
    }

    /**
     * 完全透明（100%）になるかどうか判定する。
     * 直近3ブロック連続で良いブロックなら完全透明。
     *
     * @param recentBlockResults 直近のブロック結果（true=良い）。新しい順。
     */
    fun isFullyClear(recentBlockResults: List<Boolean>): Boolean {
        if (recentBlockResults.size < FULL_CLEAR_STREAK) return false
        return recentBlockResults.take(FULL_CLEAR_STREAK).all { it }
    }
}
