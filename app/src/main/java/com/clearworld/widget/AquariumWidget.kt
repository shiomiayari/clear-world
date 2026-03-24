package com.clearworld.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import com.clearworld.db.ClearWorldDatabase

/**
 * 水槽ウィジェット。
 * Glanceを使ってホーム画面に透明度・魚の数を表示する。
 */
class AquariumWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db = ClearWorldDatabase.getInstance(context)
        val state = db.aquariumStateDao().get()
        val fishCount = db.fishDao().getAliveFishCount()

        val transparency = state?.transparency ?: 60f

        provideContent {
            AquariumWidgetContent(
                transparency = transparency,
                fishCount = fishCount
            )
        }
    }
}

/**
 * ウィジェットのブロードキャストレシーバー。
 * OSからの更新イベントを受け取りウィジェットを更新する。
 */
class AquariumWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = AquariumWidget()
}
