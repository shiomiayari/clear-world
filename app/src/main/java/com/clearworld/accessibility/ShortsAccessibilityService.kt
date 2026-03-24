package com.clearworld.accessibility

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import com.clearworld.db.ClearWorldDatabase
import com.clearworld.db.dao.DailyBlockDao
import com.clearworld.db.dao.ViewingSessionDao
import com.clearworld.db.entity.ViewingSession
import com.clearworld.util.DateUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * YouTube ShortsのUI表示を検知し、視聴時間を計測するAccessibility Service。
 *
 * 計測ルール:
 * - Shortsが画面に表示されている時間のみを計測（再生中かどうかは問わない）
 * - 離脱後3分以内に戻ったら連続とみなす（バッファタイマー）
 * - 連続20分でポップアップ警告
 */
class ShortsAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var viewingSessionDao: ViewingSessionDao
    private lateinit var dailyBlockDao: DailyBlockDao

    /** 現在計測中のセッションID。nullなら計測していない。 */
    private var activeSessionId: Long? = null

    /** 計測開始時刻（UnixTimeミリ秒） */
    private var sessionStartTime: Long = 0L

    /** 連続視聴の合計秒数（20分警告用） */
    private var continuousSeconds: Long = 0L

    /** 3分バッファタイマー：離脱後3分経過したら計測終了とみなす */
    private val bufferRunnable = Runnable {
        endSession()
        continuousSeconds = 0L
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        val db = ClearWorldDatabase.getInstance(applicationContext)
        viewingSessionDao = db.viewingSessionDao()
        dailyBlockDao = db.dailyBlockDao()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        if (event.packageName != YOUTUBE_PACKAGE) return

        val isShortsVisible = isShortsOnScreen()

        if (isShortsVisible) {
            // Shortsが表示されている → バッファタイマーをキャンセルして計測継続・開始
            handler.removeCallbacks(bufferRunnable)
            if (activeSessionId == null) {
                startSession()
            }
        } else {
            // Shortsが消えた → 3分バッファタイマーを開始
            if (activeSessionId != null) {
                handler.removeCallbacks(bufferRunnable)
                handler.postDelayed(bufferRunnable, BUFFER_MILLIS)
            }
        }
    }

    override fun onInterrupt() {
        endSession()
    }

    override fun onDestroy() {
        super.onDestroy()
        endSession()
        handler.removeCallbacksAndMessages(null)
        serviceScope.cancel()
    }

    // ── private ────────────────────────────────────────────────────────────

    private fun startSession() {
        val now = System.currentTimeMillis()
        sessionStartTime = now

        val blockType = DateUtil.currentBlockType()
        val date = DateUtil.todayString()

        val session = ViewingSession(
            startTime = now,
            blockType = blockType,
            date = date
        )

        serviceScope.launch {
            val id = viewingSessionDao.insert(session)
            activeSessionId = id
        }
    }

    private fun endSession() {
        val id = activeSessionId ?: return
        val endTime = System.currentTimeMillis()
        activeSessionId = null

        serviceScope.launch {
            val session = viewingSessionDao.getById(id) ?: return@launch
            viewingSessionDao.update(session.copy(endTime = endTime))

            // ブロックの合計秒数を更新
            val date = session.date
            val blockType = session.blockType
            val total = viewingSessionDao.getTotalSeconds(date, blockType)
            dailyBlockDao.upsert(
                com.clearworld.db.entity.DailyBlock(
                    date = date,
                    blockType = blockType,
                    totalSeconds = total
                )
            )

            // 連続視聴時間を更新
            continuousSeconds += session.durationSeconds
            if (continuousSeconds >= WARNING_SECONDS) {
                showContinuousViewingWarning()
                continuousSeconds = 0L
            }
        }
    }

    /**
     * 現在の画面にShortsのコンポーネントが存在するかを確認する。
     * YouTubeのShorts視聴画面はReelsコンポーネントで識別できる。
     */
    private fun isShortsOnScreen(): Boolean {
        val root = rootInActiveWindow ?: return false
        return root.findAccessibilityNodeInfosByViewId(SHORTS_VIEW_ID).isNotEmpty()
            || root.findAccessibilityNodeInfosByViewId(SHORTS_VIEW_ID_ALT).isNotEmpty()
    }

    private fun showContinuousViewingWarning() {
        // TODO: オーバーレイポップアップの表示（機能④で実装）
    }

    companion object {
        private const val YOUTUBE_PACKAGE = "com.google.android.youtube"

        /** YouTubeアプリ内のShortsプレーヤーのViewID */
        private const val SHORTS_VIEW_ID =
            "com.google.android.youtube:id/reel_player_page_container"
        private const val SHORTS_VIEW_ID_ALT =
            "com.google.android.youtube:id/shorts_container"

        /** 離脱バッファ: 3分 */
        private const val BUFFER_MILLIS = 3 * 60 * 1000L

        /** 連続視聴警告: 20分 */
        private const val WARNING_SECONDS = 20 * 60L
    }
}
