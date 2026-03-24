package com.clearworld.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 日付・時間帯ブロック関連のユーティリティ。
 *
 * 「今日」は6:00リセットのため、6:00未満は前日扱いになる。
 */
object DateUtil {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    /** ブロックの順序（インデックスで前後を辿るために使う） */
    val blockOrder = listOf("morning", "afternoon", "evening", "midnight")

    /**
     * 現在時刻から「今日」の日付文字列を返す。
     * 6:00未満の場合は前日扱い（6:00リセット対応）。
     */
    fun todayString(): String {
        val now = LocalDateTime.now()
        val date = if (now.hour < 6) now.toLocalDate().minusDays(1) else now.toLocalDate()
        return date.format(formatter)
    }

    /** 「今日」の前日の日付文字列を返す。 */
    fun yesterdayString(): String {
        return previousDay(todayString())
    }

    /** 指定日付の前日を返す。 */
    fun previousDay(dateString: String): String {
        return LocalDate.parse(dateString, formatter).minusDays(1).format(formatter)
    }

    /**
     * 現在時刻から時間帯ブロックを返す。
     * 6:00リセットに対応するため、0:00〜5:59は"midnight"扱い。
     */
    fun currentBlockType(): String {
        return when (LocalDateTime.now().hour) {
            in 6..11  -> "morning"
            in 12..17 -> "afternoon"
            in 18..23 -> "evening"
            else      -> "midnight"  // 0〜5時
        }
    }
}
