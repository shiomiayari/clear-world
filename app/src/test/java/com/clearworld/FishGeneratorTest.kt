package com.clearworld

import com.clearworld.fish.FishGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FishGeneratorTest {

    // ── 生成判定 ──────────────────────────────────────────────

    @Test
    fun `今日が昨日より少なければ魚を生成できる`() {
        assertTrue(FishGenerator.canGenerate(todaySeconds = 600, yesterdaySeconds = 1800))
    }

    @Test
    fun `今日が昨日と同じなら魚は生成できない`() {
        assertFalse(FishGenerator.canGenerate(todaySeconds = 1800, yesterdaySeconds = 1800))
    }

    @Test
    fun `今日が昨日より多ければ魚は生成できない`() {
        assertFalse(FishGenerator.canGenerate(todaySeconds = 3600, yesterdaySeconds = 1800))
    }

    // ── サイズ ────────────────────────────────────────────────

    @Test
    fun `midnightブロックは大きい魚`() {
        assertEquals("large", FishGenerator.sizeFor("midnight"))
    }

    @Test
    fun `eveningブロックは中くらいの魚`() {
        assertEquals("medium", FishGenerator.sizeFor("evening"))
    }

    @Test
    fun `morningブロックは小さい魚`() {
        assertEquals("small", FishGenerator.sizeFor("morning"))
    }

    @Test
    fun `afternoonブロックは小さい魚`() {
        assertEquals("small", FishGenerator.sizeFor("afternoon"))
    }

    // ── 色 ───────────────────────────────────────────────────

    @Test
    fun `節約0秒のときhueは180（青緑）`() {
        assertEquals(180, FishGenerator.hueFor(0L))
    }

    @Test
    fun `節約3時間でhueは60（黄色）`() {
        assertEquals(60, FishGenerator.hueFor(10800L))
    }

    @Test
    fun `節約0秒のとき彩度は最小`() {
        assertEquals(0.3f, FishGenerator.saturationFor(0L), 0.01f)
    }

    @Test
    fun `節約3時間で彩度は最大`() {
        assertEquals(1.0f, FishGenerator.saturationFor(10800L), 0.01f)
    }
}
