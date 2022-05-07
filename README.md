﻿## 概要  
PC版フォールガイズ(Fall Guys)向けのフォールボールのギミック可視化ツールです。  
自分用に作ったものですが、一応公開しておきます。  
1920×1080でプレイする前提のサイズで作りました(リサイズ不可)。  
初期設定完了後、フォールガイズと一緒に"FallBallGimmick.jar"を起動しておくだけで使えます。  
アプリを終了する際は、タイマーを右クリックして『Close』を選択してください。  
※使用は自己責任でお願いします。  
サンプル動画はこちら: https://youtu.be/0oeTSgS1Ss8  
  
## 表示内容  
  
### 【壁パンチャーの起動タイミング】  
左上に表示されているカウントダウンが壁パンチャーの起動タイミングです。  
試合開始のログを検知したら3秒カウントが始まります。以降は12.33秒をカウントし続けます。  
  
### 【ジャンパーの1回目の方向】  
右に表示されている黄青がジャンパーの1回目の方向です。  
例えば、『青→黄』と表示された場合は、青チーム側のジャンパーが黄チーム側へ飛びます。  
2回目以降の方向は分かりません。  
  
### 【接続しているサーバへping】  
下に表示されているIPと秒数が接続しているサーバのIPとpingの値です。  
現在接続しているサーバをログから取得し、10秒毎にpingを送り結果を表示します。  
(高頻度でpingを送ると荒らし扱いされる可能性があるので、10秒毎にしています。)  
ping実行に権限が必要らしいので、アプリを権限実行しないとうまく機能しないかもです(自分の環境ではうまく動いています)。  
  
## ダウンロード方法  
(1) https://github.com/circumark994/fallballgimmick を開きます。  
(2) Codeボタンを押した後に出る『Download ZIP』からダウンロードできます。  
  
## 初期設定  
(1) Java環境をインストールする必要があります。  
※ v0.912から"path.ini"はなくなりました。  
  
## その他  
メインショー/スクワッド/カスタムなど問わず、フォールボールであれば全てに機能します。  
壁パンチャータイマーを手動で開始する機能もあります(タイマーを右クリック→『Start』『Reset』)。  
FallBallGimmick.javaがソースなので、手直ししたい方はご自由にどうぞ。  
ログがうまく読み込めない場合は、ソースのplayer.logのパスの部分を直してコンパイルし直してください。  
バグを見つけたら直しておきます。  
  
## 履歴  
2022/05/07 v0.912 path.iniを使わない仕様へ変更  
2022/05/05 v0.911 ログ読み込み部分を微修正  
2022/05/04 v0.910 ログ読み込み部分の改良  
2022/05/04 v0.900 公開  
