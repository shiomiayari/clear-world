package com.clearworld.calculation

/**
 * 水槽の透明度を計算するエンジン。
 *
 * 計算式:
 *   Δ = -(diff_seconds / 1800) × 10   // 30分差で±10%
 *   新透明度 = 現在の透明度 + Δ
 *   新透明度 = max(0, min(100, 新透明度))
 */
object TransparencyCalculator {

    const val INITIAL_TRANSPARENCY = 60f
    const val RESET_TRANSPARENCY = 50f

    /**
     * 新しい透明度を計算する。
     *
     * @param currentTransparency 現在の透明度 (0.0〜100.0)
     * @param diffSeconds 昨日比の秒数差（正 = 昨日より多く見た、負 = 昨日より少なく見た）
     * @return 新しい透明度 (0.0〜100.0)
     */
    fun calculate(currentTransparency: Float, diffSeconds: Long): Float {
        val delta = -(diffSeconds / 1800f) * 10f
        return (currentTransparency + delta).coerceIn(0f, 100f)
    }
}
