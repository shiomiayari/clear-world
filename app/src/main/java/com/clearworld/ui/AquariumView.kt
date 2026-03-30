package com.clearworld.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import java.util.Random
import kotlin.math.*

class AquariumView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var transparency: Float = 60f
    private var statusLabel: String = ""
    private var wavePhase = 0f
    private val density = context.resources.displayMetrics.density
    private val sp = context.resources.displayMetrics.scaledDensity

    // ---- Bubble data ----
    private class Bubble(
        var x: Float, var y: Float,
        val radius: Float, val speed: Float,
        val driftAmp: Float, val driftFreq: Float, val driftOffset: Float
    )

    // ---- Caustic light data ----
    private class Caustic(
        var x: Float, var y: Float,
        var alpha: Float, var alphaDir: Float,
        var scale: Float, var scaleDir: Float
    )

    private val bubbles = ArrayList<Bubble>()
    private val caustics = ArrayList<Caustic>()
    private val rng = Random(42)

    // ---- Paints ----
    private val waterPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val wave1Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0x1AFFFFFF
    }
    private val wave2Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = 0x0FFFFFFF
    }
    private val bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = 0xBBFFFFFF.toInt()
        strokeWidth = 1.5f
    }
    private val causticPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
    }
    private val percentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        setShadowLayer(16f, 0f, 2f, 0x50000000)
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xCCFFFFFF.toInt()
        textAlign = Paint.Align.CENTER
    }

    private val clipPath = Path()
    private val rect = RectF()
    private val cornerRadius = 24f * density
    private var animator: ValueAnimator? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(0f, 0f, w.toFloat(), h.toFloat())
        clipPath.reset()
        clipPath.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW)

        percentPaint.textSize = 58f * sp
        labelPaint.textSize = 14f * sp
        bubblePaint.strokeWidth = 1.5f * density

        initBubbles(w, h)
        initCaustics(w, h)
        updateWaterShader()
    }

    private fun initBubbles(w: Int, h: Int) {
        bubbles.clear()
        repeat(20) {
            bubbles += Bubble(
                x = rng.nextFloat() * w,
                y = rng.nextFloat() * h,
                radius = (2f + rng.nextFloat() * 5f) * density,
                speed = (0.3f + rng.nextFloat() * 0.8f) * density,
                driftAmp = (8f + rng.nextFloat() * 16f) * density,
                driftFreq = 0.5f + rng.nextFloat() * 1.5f,
                driftOffset = rng.nextFloat() * (2f * PI.toFloat())
            )
        }
    }

    private fun initCaustics(w: Int, h: Int) {
        caustics.clear()
        repeat(10) {
            caustics += Caustic(
                x = rng.nextFloat() * w,
                y = rng.nextFloat() * h * 0.65f,
                alpha = 0.03f + rng.nextFloat() * 0.07f,
                alphaDir = (if (rng.nextBoolean()) 1f else -1f) * 0.0008f,
                scale = (12f + rng.nextFloat() * 38f) * density,
                scaleDir = (if (rng.nextBoolean()) 1f else -1f) * 0.15f * density
            )
        }
    }

    private fun updateWaterShader() {
        val t = transparency / 100f
        val top = waterColor(t, +0.08f)
        val bottom = waterColor(t, -0.08f)
        waterPaint.shader = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            top, bottom, Shader.TileMode.CLAMP
        )
    }

    /** 透明度(0〜1)に応じた水の色を生成。brightnessでグラデーション差をつける。 */
    private fun waterColor(t: Float, brightness: Float = 0f): Int {
        val c = when {
            t < 0.4f -> lerpColor(0xFF7B5040.toInt(), 0xFF00695C.toInt(), t / 0.4f)
            t < 0.7f -> lerpColor(0xFF00695C.toInt(), 0xFF1565C0.toInt(), (t - 0.4f) / 0.3f)
            else     -> lerpColor(0xFF1565C0.toInt(), 0xFF00ACC1.toInt(), (t - 0.7f) / 0.3f)
        }
        if (brightness == 0f) return c
        fun ch(v: Int) = (v + (255 * brightness).toInt()).coerceIn(0, 255)
        return Color.rgb(ch(Color.red(c)), ch(Color.green(c)), ch(Color.blue(c)))
    }

    private fun lerpColor(from: Int, to: Int, t: Float): Int {
        val s = t.coerceIn(0f, 1f)
        return Color.rgb(
            (Color.red(from)   + (Color.red(to)   - Color.red(from))   * s).toInt(),
            (Color.green(from) + (Color.green(to) - Color.green(from)) * s).toInt(),
            (Color.blue(from)  + (Color.blue(to)  - Color.blue(from))  * s).toInt()
        )
    }

    override fun onDraw(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()

        canvas.save()
        canvas.clipPath(clipPath)

        // 1. 水　背景
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, waterPaint)

        // 2. コースティックライト
        for (c in caustics) {
            causticPaint.alpha = (c.alpha * 255).toInt().coerceIn(0, 255)
            canvas.drawCircle(c.x, c.y, c.scale, causticPaint)
        }

        // 3. 波
        drawWaves(canvas, w, h)

        // 4. 泡
        for (b in bubbles) {
            val bx = b.x + b.driftAmp * sin(wavePhase * b.driftFreq + b.driftOffset)
            canvas.drawCircle(bx, b.y, b.radius, bubblePaint)
        }

        // 5. 中央テキスト
        drawCenterText(canvas, w, h)

        canvas.restore()
    }

    private fun drawWaves(canvas: Canvas, w: Float, h: Float) {
        val amp = 10f * density
        val y1 = h * 0.10f
        val y2 = h * 0.14f
        canvas.drawPath(buildWavePath(w, h, y1, amp,       wavePhase,        2.5f), wave1Paint)
        canvas.drawPath(buildWavePath(w, h, y2, amp * 0.6f, wavePhase + 1.2f, 3.2f), wave2Paint)
    }

    private fun buildWavePath(w: Float, h: Float, cy: Float, amp: Float, phase: Float, freq: Float): Path {
        val path = Path()
        val steps = 60
        for (i in 0..steps) {
            val x = w * i / steps
            val y = cy + sin(phase + x / w * freq * PI.toFloat() * 2).toFloat() * amp
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.lineTo(w, h); path.lineTo(0f, h); path.close()
        return path
    }

    private fun drawCenterText(canvas: Canvas, w: Float, h: Float) {
        val cx = w / 2f
        val cy = h / 2f

        val pctStr = "${transparency.toInt()}%"
        val pctBounds = Rect()
        percentPaint.getTextBounds(pctStr, 0, pctStr.length, pctBounds)
        val pctY = cy - pctBounds.exactCenterY()
        canvas.drawText(pctStr, cx, pctY, percentPaint)

        if (statusLabel.isNotEmpty()) {
            val lBounds = Rect()
            labelPaint.getTextBounds(statusLabel, 0, statusLabel.length, lBounds)
            val lY = pctY + pctBounds.height() / 2f + 10f * density - lBounds.exactCenterY()
            canvas.drawText(statusLabel, cx, lY, labelPaint)
        }
    }

    // ---- Public API ----

    fun setTransparency(value: Float) {
        transparency = value.coerceIn(0f, 100f)
        updateWaterShader()
        invalidate()
    }

    fun setStatusLabel(label: String) {
        statusLabel = label
        invalidate()
    }

    // ---- Lifecycle ----

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        animator = ValueAnimator.ofFloat(0f, (2 * PI).toFloat()).apply {
            duration = 5000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()
            addUpdateListener { a ->
                wavePhase = a.animatedValue as Float
                tickBubbles()
                tickCaustics()
                invalidate()
            }
        }
        animator?.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
        animator = null
    }

    private fun tickBubbles() {
        val h = height.toFloat()
        val w = width.toFloat()
        for (b in bubbles) {
            b.y -= b.speed
            if (b.y + b.radius < 0f) {
                b.y = h + b.radius
                b.x = rng.nextFloat() * w
            }
        }
    }

    private fun tickCaustics() {
        val maxScale = 55f * density
        val minScale = 8f * density
        for (c in caustics) {
            c.alpha += c.alphaDir
            if (c.alpha > 0.13f) c.alphaDir = -abs(c.alphaDir)
            if (c.alpha < 0.02f) c.alphaDir = abs(c.alphaDir)
            c.scale += c.scaleDir
            if (c.scale > maxScale || c.scale < minScale) c.scaleDir = -c.scaleDir
        }
    }
}
