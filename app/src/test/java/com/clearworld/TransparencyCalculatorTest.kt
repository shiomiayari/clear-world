package com.clearworld

import com.clearworld.calculation.TransparencyCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TransparencyCalculatorTest {

    // ── 透明度計算 ──────────────────────────────────────────

    @Test
    fun `30分多く見た場合に透明度が10%下がる`() {
        val result = TransparencyCalculator.calculate(
            currentTransparency = 60f,
            todaySeconds = 3600,   // 60分
            yesterdaySeconds = 1800 // 30分 → diff=+1800秒
        )
        assertEquals(50f, result, 0.01f)
    }

    @Test
    fun `30分節約した場合に透明度が10%上がる`() {
        val result = TransparencyCalculator.calculate(
            currentTransparency = 60f,
            todaySeconds = 0,
            yesterdaySeconds = 1800  // diff=-1800秒
        )
        assertEquals(70f, result, 0.01f)
    }

    @Test
    fun `透明度は0を下回らない`() {
        val result = TransparencyCalculator.calculate(
            currentTransparency = 5f,
            todaySeconds = 10800,
            yesterdaySeconds = 0
        )
        assertEquals(0f, result, 0.01f)
    }

    @Test
    fun `透明度は100を超えない`() {
        val result = TransparencyCalculator.calculate(
            currentTransparency = 95f,
            todaySeconds = 0,
            yesterdaySeconds = 10800
        )
        assertEquals(100f, result, 0.01f)
    }

    @Test
    fun `1ブロックで3時間以上視聴したら完全に濁る`() {
        val result = TransparencyCalculator.calculate(
            currentTransparency = 80f,
            todaySeconds = 10800,  // ちょうど3時間
            yesterdaySeconds = 0
        )
        assertEquals(0f, result, 0.01f)
    }

    // ── 良いブロック判定 ──────────────────────────────────────

    @Test
    fun `昨日の20%以下なら良いブロック`() {
        assertTrue(TransparencyCalculator.isGoodBlock(todaySeconds = 200, yesterdaySeconds = 1000))
    }

    @Test
    fun `昨日の21%なら良いブロックではない`() {
        assertFalse(TransparencyCalculator.isGoodBlock(todaySeconds = 210, yesterdaySeconds = 1000))
    }

    @Test
    fun `昨日も今日も0秒なら良いブロック`() {
        assertTrue(TransparencyCalculator.isGoodBlock(todaySeconds = 0, yesterdaySeconds = 0))
    }

    @Test
    fun `昨日0秒で今日1秒でも見たら良いブロックではない`() {
        assertFalse(TransparencyCalculator.isGoodBlock(todaySeconds = 1, yesterdaySeconds = 0))
    }

    // ── 完全透明判定 ──────────────────────────────────────────

    @Test
    fun `直近3ブロック全て良いブロックなら完全透明`() {
        assertTrue(TransparencyCalculator.isFullyClear(listOf(true, true, true)))
    }

    @Test
    fun `直近3ブロックのうち1つでも悪いブロックがあれば完全透明にならない`() {
        assertFalse(TransparencyCalculator.isFullyClear(listOf(true, false, true)))
    }

    @Test
    fun `ブロック数が3未満なら完全透明にならない`() {
        assertFalse(TransparencyCalculator.isFullyClear(listOf(true, true)))
    }
}
