package com.clearworld.ui

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView
import com.clearworld.R

/**
 * YouTube視聴中に画面上に表示する濁り警告オーバーレイ。
 * AccessibilityServiceから呼ばれる。
 *
 * 表示条件:
 * - 連続視聴が20分を超えたとき
 * - 現ブロックの累計が前日同ブロックを超えたとき
 */
class WarningOverlay(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: android.view.View? = null

    /**
     * 警告オーバーレイを表示する。
     * @param message 表示するメッセージ
     */
    fun show(message: String) {
        if (overlayView != null) return  // 既に表示中なら重ねない

        val view = LayoutInflater.from(context).inflate(R.layout.overlay_warning, null)
        view.findViewById<TextView>(R.id.tv_warning_message).text = message
        view.findViewById<android.view.View>(R.id.btn_close_warning).setOnClickListener {
            dismiss()
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            y = 200  // 下から200pxの位置
        }

        windowManager.addView(view, params)
        overlayView = view
    }

    /** オーバーレイを非表示にする。 */
    fun dismiss() {
        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
    }
}
