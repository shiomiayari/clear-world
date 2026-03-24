# CLAUDE.md — Clear World プロジェクト指示書

このファイルはClaude Codeがプロジェクトを理解するための指示書です。

## プロジェクト概要

YouTube Shortsの視聴時間を「水槽の透明度」と「魚の生成・消滅」で表現するAndroid行動変容アプリ。
ユーザーが「昨日の自分より少し良くなった」ことを積み重ねられるよう設計されている。

## 技術スタック

- **言語:** Kotlin
- **視聴検知:** Android Accessibility Service（YouTubeアプリのみ対象）
- **魚生成:** Magenta.js（WebView経由でKotlinと連携）
- **ウィジェット:** AppWidget + Jetpack Glance
- **DB:** Room（SQLite ORM）
- **非同期:** Kotlin Coroutines
- **テスト:** JUnit4 + MockK
- **CI/CD:** GitHub Actions

## 重要な仕様・決定事項

### 計測ロジック
- Shortsが**画面に表示されている時間のみ**を計測（再生中かどうかは問わない）
- 対象はYouTubeアプリのみ（ブラウザ版は対象外）
- **3分間の中断バッファ:** 離脱後3分以内に戻ったら連続とみなす
- **連続視聴警告:** 連続20分でポップアップ表示

### 時間帯ブロック（6:00リセット）
| ブロック名 | 時間帯 |
|---|---|
| 深夜（midnight） | 24:00〜翌6:00 |
| 朝（morning） | 6:00〜12:00 |
| 昼（afternoon） | 12:00〜18:00 |
| 夕方（evening） | 18:00〜24:00 |

### 透明度の計算式
```
Δ = -(diff_seconds / 1800) × 10   // 30分差で±10%
新透明度 = 現在の透明度 + Δ
新透明度 = max(0, min(100, 新透明度))
```
- 初日ユーザーは**60%からスタート**
- 1ブロックで3時間以上の視聴で**完全に濁る（0%）**
- 昨日比-80%を3ブロック連続で達成したとき**完全に透明（100%）**
- 6:00リセット時は**50%からスタート**

### 魚の仕様
- 節約時間が長いほど**色が鮮やか**
- 深夜ブロックの節約 → **大きい魚**
- 夕方ブロックの節約 → **中くらいの魚**
- 朝・昼ブロックの節約 → **小さい魚**
- 視聴時間が増えたら魚は**泡になって消える**
- 魚誕生の演出は通知ではなく**ウィジェットの変化**で表現

### データ
- **ローカルのみ**（クラウド同期なし、MVPスコープ）
- アプリ削除時に全データ削除

### スコープ外（v1）
- iOSサポート（将来対応）
- ブラウザ版YouTube Shorts
- クラウド同期
- 魚の墓地（消えた魚の表示）
- 「浄化の成功」通知

## コーディング規約

- コミットメッセージは**Conventional Commits**形式を使う
  - `feat:` 新機能、`fix:` バグ修正、`docs:` ドキュメント、`refactor:` リファクタ
- ブランチは `feature/機能名` の形式で作る
- mainへの直接pushは禁止（develop経由でマージ）

## ディレクトリ構成（予定）

```
clear-world/
├── app/
│   ├── src/main/
│   │   ├── java/com/clearworld/
│   │   │   ├── accessibility/   # Accessibility Service
│   │   │   ├── calculation/     # 透明度計算エンジン
│   │   │   ├── fish/            # 魚の生成・消滅ロジック
│   │   │   ├── widget/          # AppWidget
│   │   │   ├── db/              # Room DB
│   │   │   └── ui/              # 画面
│   │   └── assets/
│   │       └── magenta/         # Magenta.js関連ファイル
├── docs/
│   ├── PRD.md
│   ├── SCREEN_FLOW.md
│   ├── SPEC.md
│   └── TECH_SPEC.md
├── .github/workflows/
│   └── android.yml
├── CLAUDE.md
└── README.md
```
