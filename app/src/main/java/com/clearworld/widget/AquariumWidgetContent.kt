package com.clearworld.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.clearworld.ui.MainActivity

/**
 * ウィジェットのUI。透明度に応じて水の色を変える。
 * タップするとMainActivityを開く。
 */
@Composable
fun AquariumWidgetContent(
    transparency: Float,
    fishCount: Int
) {
    val waterColor = waterColorFor(transparency)

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(waterColor)
            .cornerRadius(16.dp)
            .clickable(actionStartActivity<MainActivity>()),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // 透明度の数値
            Text(
                text = "${transparency.toInt()}%",
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            // 魚の数（1匹以上いるときだけ表示）
            if (fishCount > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🐟",
                        style = TextStyle(fontSize = 14.sp)
                    )
                    Spacer(modifier = GlanceModifier.width(4.dp))
                    Text(
                        text = "×$fishCount",
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = 14.sp
                        )
                    )
                }
            }

            // 状態ラベル
            Text(
                text = statusLabelFor(transparency),
                style = TextStyle(
                    color = ColorProvider(Color.White.copy(alpha = 0.8f)),
                    fontSize = 11.sp
                ),
                modifier = GlanceModifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * 透明度から水の色を返す。
 * 仕様書の対応表に基づく。
 */
fun waterColorFor(transparency: Float): Color = when {
    transparency >= 100f -> Color(0xFF29B6F6)  // 透き通った青
    transparency >= 75f  -> Color(0xFF4DB6AC)  // 薄い青緑
    transparency >= 50f  -> Color(0xFF78909C)  // くすんだ緑
    transparency >= 25f  -> Color(0xFF8D6E63)  // 茶色がかった緑
    else                 -> Color(0xFF4E342E)  // 暗い茶色
}

/**
 * 透明度から状態ラベルを返す。
 */
fun statusLabelFor(transparency: Float): String = when {
    transparency >= 100f -> "澄み渡っている"
    transparency >= 75f  -> "きれい"
    transparency >= 50f  -> "少し濁っている"
    transparency >= 25f  -> "濁っている"
    else                 -> "ひどく濁っている"
}
