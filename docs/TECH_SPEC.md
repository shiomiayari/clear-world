# 技術仕様書 — Clear World

**作成日:** 2026-03-22

---

## セクション1 — 技術スタック

### 言語・基盤

| 項目 | 採用技術 | 理由 |
|---|---|---|
| 開発言語 | **Kotlin** | Android公式推奨・モダンな文法 |
| 最小SDKバージョン | **API 26（Android 8.0）** | Accessibility Serviceの安定動作に必要 |
| ターゲットSDK | **API 35（Android 15）** | 最新のWidgetKit対応のため |
| ビルドツール | **Gradle（Kotlin DSL）** | Android Studio標準 |

### ライブラリ一覧

| カテゴリ | ライブラリ | 用途 |
|---|---|---|
| **DB** | Room | ローカルDB（視聴記録・魚データ） |
| **非同期処理** | Kotlin Coroutines | バックグラウンド計測・DB操作 |
| **魚生成** | Magenta.js（WebView経由） | 魚のグラフィック・音の生成 |
| **ウィジェット** | AppWidget + Jetpack Glance | ホーム画面ウィジェット |
| **テスト** | JUnit4 + MockK | ユニットテスト |
| **CI/CD** | GitHub Actions | 自動テスト・自動ビルド |

---

## セクション2 — Accessibility Service実装方針

### プロセス設計

```
YouTubeアプリ操作
  ↓ AccessibilityEvent 発火
ShortsWatchService（常駐サービス）
  ↓ Shorts判定
ViewingTimerManager（計測）
  ↓ 一定間隔でDB保存
Room Database
  ↓
透明度計算エンジン / ポップアップ判定
```

### 検知するAccessibilityEvent

| Event種別 | 用途 |
|---|---|
| `TYPE_WINDOW_STATE_CHANGED` | YouTubeアプリの画面切り替えを検知 |
| `TYPE_WINDOW_CONTENT_CHANGED` | Shorts内のコンテンツ変化を検知 |

### Shorts判定の実装方針

```kotlin
const val YOUTUBE_PACKAGE = "com.google.android.youtube"
const val SHORTS_CLASS = "com.google.android.apps.youtube.app.shorts.standalone.ShortsStandaloneActivity"

override fun onAccessibilityEvent(event: AccessibilityEvent) {
    if (event.packageName != YOUTUBE_PACKAGE) return
    val isShorts = event.className?.contains("Shorts") == true
        || rootInActiveWindow?.findShortsComponent() == true
    if (isShorts) timerManager.start() else timerManager.pauseWithBuffer()
}
```

### 3分バッファの実装

```kotlin
fun pauseWithBuffer() {
    bufferJob = scope.launch {
        delay(3 * 60 * 1000L) // 3分待機
        timerManager.stop()   // 3分経過したら本当に停止
    }
}

fun start() {
    bufferJob?.cancel() // 戻ってきたらバッファキャンセル
    // 計測再開
}
```

### 注意事項
- Accessibility Serviceはユーザーの明示的な許可が必要
- なぜこの権限が必要か・プライバシー上安全な理由をオンボーディングで説明する
- YouTubeのアップデートで検知対象クラス名が変わる可能性があるため、クラス名は設定ファイルで管理する

---

## セクション3 — Magenta.js実装方針

### AndroidでMagenta.jsを動かす方法

Magenta.jsはJavaScriptライブラリのため、**WebViewの中で実行**する。

```
Kotlin（メインアプリ）
  ↓ パラメータをJSON形式で渡す
WebView（非表示・バックグラウンド）
  ↓ Magenta.jsが魚を生成
  ↓ 結果（色パラメータ・音データ）をKotlinに返す
Kotlin（DBに保存・ウィジェット更新）
```

### パラメータの受け渡し

```kotlin
// Kotlinから渡す
val params = """
    {
      "saved_seconds": 1800,
      "block_type": "evening"
    }
""".trimIndent()

webView.evaluateJavascript(
    "generateFish($params)",
    { result -> saveFishData(result) }  // 結果を受け取る
)
```

```javascript
// Magenta.js側（WebView内）
async function generateFish(params) {
    const hue = mapToHue(params.saved_seconds)
    const size = blockToSize(params.block_type)
    const melody = await generateMelody(params)
    return JSON.stringify({ hue, size, melody_seed: melody.seed })
}

// saved_seconds → 色相のマッピング
// 0秒（節約なし）→ 淡い色（hue: 180付近）
// 10800秒（3時間節約）→ 鮮やか（saturation: 1.0）
function mapToHue(savedSeconds) {
    return Math.min(savedSeconds / 10800, 1.0)
}

// ブロック → サイズのマッピング
function blockToSize(blockType) {
    const sizeMap = {
        midnight: "large",
        evening: "medium",
        morning: "small",
        afternoon: "small"
    }
    return sizeMap[blockType] || "small"
}
```

### フォールバック
Magenta.jsの生成に失敗した場合は、デフォルトパラメータ（hue: 180, saturation: 0.5, size: small）の魚を使用する。

---

## セクション4 — ウィジェット実装方針

### 使用するAPI

| API | 用途 |
|---|---|
| **AppWidget** | ウィジェットの基盤（Android標準） |
| **Jetpack Glance** | KotlinでウィジェットUIを宣言的に書くためのライブラリ |
| **AlarmManager** | 15分ごとの更新をスケジューリング |

### 更新の実装

```kotlin
// 15分ごとのアラーム設定
alarmManager.setRepeating(
    AlarmManager.RTC,
    System.currentTimeMillis(),
    15 * 60 * 1000L,  // 15分
    pendingIntent      // WidgetUpdateReceiverを呼び出す
)

// バッテリーセーバー検知時は30分に切替
if (powerManager.isPowerSaveMode) {
    interval = 30 * 60 * 1000L
}
```

---

## セクション5 — ローカルDB実装方針（Room）

### Roomの基本構成

```
Entity（データクラス）
  ↓ アノテーションで定義
DAO（Data Access Object）
  ↓ クエリをインターフェースとして定義
Database（RoomDatabase）
  ↓ シングルトンで管理
Repository（ビジネスロジック）
  ↓
ViewModel / UseCase
```

### Entity定義

```kotlin
@Entity(tableName = "viewing_records")
data class ViewingRecord(
    @PrimaryKey val id: String,        // "2026-03-22_evening"
    val date: String,
    val block: String,
    val totalSeconds: Int,
    val sessions: String               // JSON文字列として保存
)

@Entity(tableName = "fish")
data class Fish(
    @PrimaryKey val id: String,        // "fish_20260322_evening"
    val bornAt: String,
    val block: String,
    val savedSeconds: Int,
    val size: String,                  // "small" / "medium" / "large"
    val colorHue: Int,
    val colorSat: Float,
    val melodySeed: Int,
    val isAlive: Boolean
)

@Entity(tableName = "transparency",
    primaryKeys = ["date", "block"])
data class TransparencyRecord(
    val date: String,
    val block: String,
    val value: Int                     // 0〜100
)
```

---

## セクション6 — CI/CD設計

### 全体像

```
git push（main または develop ブランチ）
  ↓
GitHub Actions 起動
  ↓
┌─────────────────────┐
│      CI（テスト）    │
│ ① Lintチェック      │
│ ② ユニットテスト実行 │
└──────────┬──────────┘
           ↓ 通過したら
┌─────────────────────┐
│      CD（ビルド）    │
│ ③ APKビルド         │
│ ④ artifactに保存    │
└─────────────────────┘
```

### GitHub Actions 設定ファイル

```yaml
# .github/workflows/android.yml
name: Android CI/CD

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: JDKのセットアップ
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Gradleのキャッシュ
        uses: actions/cache@v3
        with:
          path: ~/.gradle/caches
          key: gradle-${{ hashFiles('**/*.gradle*') }}

      - name: Lintチェック
        run: ./gradlew lint

      - name: ユニットテスト実行
        run: ./gradlew test

  build:
    needs: test   # testジョブが通ったときだけ実行
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: JDKのセットアップ
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: APKビルド
        run: ./gradlew assembleDebug

      - name: ビルド成果物を保存
        uses: actions/upload-artifact@v3
        with:
          name: clear-world-debug
          path: app/build/outputs/apk/debug/
```

### ブランチ戦略

```
main        本番・安定版（直接pushしない）
  ↑ merge
develop     開発の主軸
  ↑ merge
feature/○○  機能ごとのブランチ
```

### 日々の作業フロー

```bash
# 機能を作り始めるとき
git checkout develop
git checkout -b feature/accessibility-service

# 作業・コミットを繰り返す（Conventional Commits形式）
git add .
git commit -m "feat: Shorts視聴の開始・終了検知を実装"
git commit -m "fix: 3分バッファ後にタイマーがリセットされないバグを修正"
git commit -m "test: ViewingTimerManagerのユニットテストを追加"

# 完成したらdevelopにマージ
git checkout develop
git merge feature/accessibility-service
git push origin develop
# ↑ ここでGitHub ActionsのCIが自動実行

# CIが通ったらmainにマージ
git checkout main
git merge develop
git push origin main
```

### コミットメッセージのprefixルール

| prefix | 使う場面 |
|---|---|
| `feat:` | 新機能を追加した |
| `fix:` | バグを直した |
| `docs:` | ドキュメントだけ変えた |
| `refactor:` | 動作は変わらないがコードを整理した |
| `test:` | テストを追加・修正した |
| `chore:` | ライブラリ追加・設定変更など |
