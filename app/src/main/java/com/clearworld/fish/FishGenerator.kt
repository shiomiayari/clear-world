package com.clearworld.fish

import com.clearworld.db.entity.Fish

/**
 * 節約秒数・ブロック種別から魚のパラメータを決定する純粋関数の集まり。
 * DBやContextに依存しないので単体テストしやすい。
 */
object FishGenerator {

    /** 節約秒数の最大値（3時間）。これ以上は色・サイズ計算の上限として使う。 */
    private const val MAX_SAVED_SECONDS = 10800L

    /** 彩度の最小値・最大値 */
    private const val MIN_SATURATION = 0.3f
    private const val MAX_SATURATION = 1.0f

    /**
     * 魚を生成できるかどうか判定する。
     * 今日の視聴秒数が昨日より少なければ生成可能。
     */
    fun canGenerate(todaySeconds: Long, yesterdaySeconds: Long): Boolean {
        return todaySeconds < yesterdaySeconds
    }

    /**
     * 魚を生成する。
     *
     * @param blockType 誕生したブロック
     * @param savedSeconds 節約した秒数（昨日 - 今日）
     */
    fun generate(blockType: String, savedSeconds: Long): Fish {
        return Fish(
            blockType = blockType,
            savedSeconds = savedSeconds,
            size = sizeFor(blockType),
            colorHue = hueFor(savedSeconds),
            colorSaturation = saturationFor(savedSeconds)
        )
    }

    /**
     * ブロック種別からサイズを決定する。
     * midnight=大、evening=中、morning/afternoon=小
     */
    fun sizeFor(blockType: String): String = when (blockType) {
        "midnight" -> "large"
        "evening"  -> "medium"
        else       -> "small"  // morning / afternoon
    }

    /**
     * 節約秒数から色相を決定する。
     * 節約が多いほど青緑（180°）→ 黄色（60°）へ変化し、鮮やかになる。
     */
    fun hueFor(savedSeconds: Long): Int {
        val ratio = (savedSeconds.coerceIn(0L, MAX_SAVED_SECONDS) / MAX_SAVED_SECONDS.toFloat())
        // 180（青緑）→ 60（黄色）に線形補間
        return (180 - (ratio * 120)).toInt()
    }

    /**
     * 節約秒数から彩度を決定する。
     * 節約が多いほど彩度が高く（鮮やか）なる。
     */
    fun saturationFor(savedSeconds: Long): Float {
        val ratio = (savedSeconds.coerceIn(0L, MAX_SAVED_SECONDS) / MAX_SAVED_SECONDS.toFloat())
        return MIN_SATURATION + ratio * (MAX_SATURATION - MIN_SATURATION)
    }
}
