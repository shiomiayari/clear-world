# Clear World（クリア・ワールド）

> ～昨日の自分を超えて、世界を澄ませていく～

## 概要

YouTube Shortsの視聴時間を「世界の濁り」に見立てた、情緒的な行動変容Androidアプリ。

- **水槽の透明度**で「昨日の自分との比較」を視覚化
- **魚の生成・消滅**で節約時間を報酬として表現
- **ホーム画面ウィジェット**でアプリを開かずに現状を把握
- **ポップアップ警告**で連続視聴・前日比悪化を即座に通知

## スクリーンショット

> （実装後に追加）

## 技術スタック

| カテゴリ | 技術 |
|---|---|
| 言語 | Kotlin |
| 最小SDK | API 26（Android 8.0） |
| 視聴検知 | Android Accessibility Service |
| 魚生成 | Magenta.js（WebView経由） |
| ウィジェット | AppWidget + Jetpack Glance |
| DB | Room |
| 非同期 | Kotlin Coroutines |
| CI/CD | GitHub Actions |

## セットアップ

### 必要な環境

- Android Studio Hedgehog 以降
- JDK 17
- Android SDK API 35

### 手順

```bash
git clone https://github.com/your-name/clear-world.git
cd clear-world
```

Android Studioで `open project` からcloneしたフォルダを開く。

### 必要な権限

アプリの動作には以下の権限が必要です：

- **Accessibility Service** — YouTube Shorts視聴時間の計測に使用
- **ポップアップ表示** — 濁り警告の表示に使用

## ドキュメント

| ファイル | 内容 |
|---|---|
| [docs/PRD.md](docs/PRD.md) | 製品要件定義書 |
| [docs/SCREEN_FLOW.md](docs/SCREEN_FLOW.md) | 画面遷移図 |
| [docs/SPEC.md](docs/SPEC.md) | 機能仕様書 |
| [docs/TECH_SPEC.md](docs/TECH_SPEC.md) | 技術仕様書 |

## ブランチ戦略

```
main        本番・安定版
develop     開発の主軸
feature/○○  機能ごとのブランチ
```

## ライセンス

MIT
