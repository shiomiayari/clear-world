# 画面遷移図 — Clear World

**作成日:** 2026-03-22

---

## 画面一覧

| カテゴリ | 画面名 |
|---|---|
| 初回起動 | スプラッシュ、オンボーディング（3枚）、権限リクエスト（Accessibility / ポップアップ）、セットアップ完了 |
| メイン | ホーム（水槽）、魚詳細、統計・履歴 |
| 設定 | 設定トップ、ウィジェット設定、データリセット確認 |
| オーバーレイ | 濁り警告ポップアップ |
| ウィジェット | 小・中・大（OSレイヤー） |

---

## 画面遷移図（Mermaid）

```mermaid
flowchart TD
    SPLASH([スプラッシュ])
    SPLASH --> ONBOARD1

    subgraph ONBOARDING["オンボーディング（初回のみ）"]
        ONBOARD1[コンセプト説明①\n水槽と透明度]
        ONBOARD2[コンセプト説明②\n魚の生成]
        ONBOARD3[コンセプト説明③\n昨日の自分と比較]
        ONBOARD1 --> ONBOARD2 --> ONBOARD3
    end

    ONBOARD3 --> PERM_ACCESS

    subgraph PERMISSIONS["権限リクエスト"]
        PERM_ACCESS[Accessibility Service\n許可リクエスト\n「なぜ必要か」説明付き]
        PERM_NOTIF[ポップアップ\n許可リクエスト]
        PERM_ACCESS -->|許可した| PERM_NOTIF
        PERM_ACCESS -->|拒否| PERM_DENY[権限なし案内\nAndroid設定への直リンク]
        PERM_DENY -->|設定から許可して戻る| PERM_ACCESS
    end

    PERM_NOTIF --> SETUP_DONE[セットアップ完了\n初期透明度60%で水槽が出現]
    SETUP_DONE --> HOME

    subgraph MAIN["メイン画面"]
        HOME[ホーム\n水槽 + 透明度 + 魚一覧]
        FISH_DETAIL[魚の詳細\n生まれた日時・ブロック・色の理由]
        STATS[統計・履歴\n過去7日のブロック別グラフ]
        SETTINGS[設定]

        HOME -->|魚をタップ| FISH_DETAIL
        HOME -->|統計ボタン| STATS
        HOME -->|設定ボタン| SETTINGS
        FISH_DETAIL -->|戻る| HOME
        STATS -->|戻る| HOME
        SETTINGS -->|戻る| HOME
    end

    subgraph SETTINGS_DETAIL["設定画面内"]
        SETTINGS --> SET_WIDGET[ウィジェット設定\nサイズ選択]
        SETTINGS --> SET_RESET[データリセット]
        SET_RESET --> CONFIRM[リセット確認ダイアログ]
        CONFIRM -->|キャンセル| SETTINGS
        CONFIRM -->|実行| HOME
    end

    subgraph OVERLAYS["オーバーレイ（YouTube視聴中に出現）"]
        POPUP_MUDDY[濁り警告ポップアップ\n「水槽が濁り始めています」]
    end

    HOME -.->|バックグラウンドで監視中| POPUP_MUDDY
    POPUP_MUDDY -->|閉じる| HOME

    subgraph WIDGET["ホーム画面ウィジェット（OSレイヤー）"]
        W_SMALL[小ウィジェット\n透明度のみ]
        W_MID[中ウィジェット\n透明度 + 魚数]
        W_LARGE[大ウィジェット\n水槽全体]
    end

    W_SMALL -->|タップ| HOME
    W_MID -->|タップ| HOME
    W_LARGE -->|タップ| HOME
```

---

## 補足事項

- **魚誕生の演出:** アニメーション専用画面なし。ブロック終了時にウィジェットが静かに更新される。アプリを開いたとき、すでに魚が増えている体験を重視。
- **権限拒否時:** ハードゲート。Android設定画面への直リンクを表示し、許可するまでアプリを使用不可とする。
- **魚の墓地:** 実装しない（v1スコープ外）。
