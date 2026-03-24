package com.clearworld

import com.clearworld.calculation.TransparencyCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class TransparencyCalculatorTest {

    @Test
    fun `30分多く見た場合に透明度が10%下がる`() {
        val result = TransparencyCalculator.calculate(60f, diffSeconds = 1800)
        assertEquals(50f, result, 0.01f)
    }

    @Test
    fun `30分節約した場合に透明度が10%上がる`() {
        val result = TransparencyCalculator.calculate(60f, diffSeconds = -1800)
        assertEquals(70f, result, 0.01f)
    }

    @Test
    fun `透明度は0を下回らない`() {
        val result = TransparencyCalculator.calculate(5f, diffSeconds = 10800)
        assertEquals(0f, result, 0.01f)
    }

    @Test
    fun `透明度は100を超えない`() {
        val result = TransparencyCalculator.calculate(95f, diffSeconds = -10800)
        assertEquals(100f, result, 0.01f)
    }
}
