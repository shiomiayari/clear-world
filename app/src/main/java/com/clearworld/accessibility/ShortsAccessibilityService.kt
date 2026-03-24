package com.clearworld.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

/**
 * YouTube ShortsのUI表示を検知し、視聴時間を計測するAccessibility Service。
 * Shortsが画面に表示されている時間のみを対象とする（再生状態は問わない）。
 */
class ShortsAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // TODO: YouTube Shortsの表示検知ロジックを実装
    }

    override fun onInterrupt() {
        // TODO: サービス中断時の処理
    }
}
