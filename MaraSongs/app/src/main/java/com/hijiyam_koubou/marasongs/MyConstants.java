package com.hijiyam_koubou.marasongs;

public final class MyConstants {

    //MusicPlayerServiceから設定
    /**リスト中のインデックス*/
    public static int mIndex;						//play_order
    /**リストの総登録曲数*/
    public static int listEnd;

    //プリファレンス
    public String ruikei_artist;					//アーティスト累計
    public String pref_compBunki = "40";			//コンピレーション分岐点 曲数
    public int pref_sonota_vercord ;				//////////////このアプリのバージョンコード/ 2015年08月//9341081 //////////////////////
    public boolean pref_cyakusinn_fukki=true;		//終話後に自動再生
    public boolean pref_bt_renkei =true;				//Bluetoothの接続に連携して一時停止/再開
    public boolean pref_list_simple =false;				//シンプルなリスト表示（サムネールなど省略）
    public boolean pref_lockscreen =true;				//ロックスクリーンプレイヤー</string>
    public boolean pref_notifplayer =true;				//ノティフィケーションプレイヤー</string>
    public String myPFN = "ma_pref";
    public String pref_rundam_list_size = "100";				//ランダム再生リストアップ曲数
    public int repeatType;							//リピート再生の種類
    public String repeatArtist;					//リピートさせるアーティスト名
    public boolean rp_pp = false;							//2点間リピート中
    public int pp_start = 0;						//リピート区間開始点
    public int pp_end;								//リピート区間終了点
    public boolean zenkyokuAri;					//全曲リスト有り
    public String b_action ="";
    public boolean IsPlayingNow = false;		//既に再生中
    public String pref_artist_bunnri = "50";			//アーティストリストを分離する曲数
    public String pref_saikin_tuika = "30";				//最近追加リストのデフォルト日数
    public String pref_saikin_sisei = "100";			//最近再生加リストのデフォルト曲数

    public long crossFeadTime=0;		//再生終了時、何ms前に次の曲に切り替えるか
    public boolean pref_pb_bgc;		//プレイヤーの背景は白
    public int saiseiJikan;					//DURATION;継続;The duration of the audio file, in ms;Type: INTEGER (long)
    public int pref_zenkai_saiseKyoku = 0;		//前回の連続再生曲数
    public long pref_zenkai_saiseijikann = 0;		//前回の連続再生時間
    public int pref_file_kyoku;					//曲累計
    public String pref_file_saisinn;					//最新更新日
    public String pref_file_in ="";		//内蔵メモリ
    public String pref_file_ex ;						//メモリーカードの音楽ファイルフォルダ
    public String pref_file_wr;						//設定保存フォルダ
    public String pref_commmn_music="";		//共通音楽フォルダ
    public boolean prTT_dpad = false;			//ダイヤルキー有り
    public int pref_zenkyoku_list_id;			// 全曲リスト
    public int saikintuika_list_id;			//最近追加
    public int saikinsisei_list_id;			//最近再生

    public String pref_gyapless = null;			//クロスフェード時間
    public boolean pref_reset = false;					//設定を初期化
    public boolean pref_listup_reset = false;			//調整リストを初期化
    public String pref_saisei_nagasa  ="0";		//再生時間

    //	MaraSonActivityの動作経路
    static final int RESULT_ENABLE = 1;
    public static final int kidou_std = 100;										//通常のリストから起動
    public static final int kidou_notif = kidou_std + 1;						//ノティフィケーションからの起動
    public static final int syoki_start_up = kidou_notif +1;						//終了後、onCreateまで戻る
    public static final int syoki_start_up1 = syoki_start_up+1;			//終了後、retSturtUp_kakuninnまで戻る
    public static final int syoki_start_up2 = syoki_start_up1+1;			//終了後、resoceYomikomiでアクティビティを読み込んでリソースIDを取得
    public static final int syoki_start_up_sai = syoki_start_up2+1;		//再起動
    public static final int syoki_start_upe = syoki_start_up_sai+1;			//MediaStore.Audio.Albumsの全レコードからアーティストリスト作成
    public static final int dataReflesh_end = syoki_start_upe+5;					//アルバム一枚分のタイトル読み込み後、そのアーティストのアルバムリストを作る
    public static final int syoki_activty_set = dataReflesh_end+5;		//プリファレンスの読み込み
    public static final int syoki_1stsentaku = syoki_activty_set+5;	//リスト選択されなかった・一人目のアーティストからアルバムリストへ
    public static final int syoki_1stsentaku1 = syoki_1stsentaku+1;	//リスト選択されなかった・一枚目のアルバムリスト～タイトルリストへ
    public static final int syoki_1stsentaku2 = syoki_1stsentaku1+1;	//リスト選択されなかった・タイトルリストから一曲目を選択して再生準備
    /**全曲リスト読み込み*/
    public static final int syoki_Yomikomi = syoki_1stsentaku2+5;				//
    public static final int syoki_Yomi1 = syoki_Yomikomi+1;				//CreateArtistListの初回作成
    public static final int syoki_Yomi2 = syoki_Yomi1+1;				//リストから重複削除
    public static final int syoki_Yomi3 = syoki_Yomi2+1;				//全曲リスト作成へ

    public static final int btInfo_kousin = syoki_Yomi3+1;			//Bluetooth情報の更新

    public static final int syoki_Yomi_syuusei = btInfo_kousin+1;			//MediaStore修正へ
    public static final int syoki_Yomi_Album_All = syoki_Yomi_syuusei+1;			//MediaStore.Audio.Albumsの全レコード読み込み
    public static final int syoki_Yomi_Album_All2 = syoki_Yomi_Album_All+1;			//MediaStore.Audio.Albumsの全レコード読み込み
    public static final int syoki_Yomi_Album_All3 = syoki_Yomi_Album_All2+1;			//MediaStore.Audio.Albumsの全レコード読み込み
    public static final int syoki_Yomi_Album_All4 = syoki_Yomi_Album_All3+1;			//MediaStore.Audio.Albumsの全レコード読み込み
    public static final int syoki_Yomi_Album_AllE = syoki_Yomi_Album_All4+1;			//MediaStore.Audio.Albumsの全レコード読み込み
    public static final int syoki_alist2redium = syoki_Yomi_Album_AllE+5;			//artistListを作った後でレジューム機能に戻す
    public static final int listSentaku_artist = syoki_Yomi_Album_All2+5;	//リスト作成でCreateArtistListに続く処理
    public static final int listSentaku_alubum = listSentaku_artist+1;	//リスト作成でCreateAlbumListに続く処理
    public static final int listSentaku_titol = listSentaku_alubum+1;	//リスト作成でCreateTitleListに続く処理
    public static final int url2_titol = listSentaku_titol+5;				//起動時のプリファレンスからurlからTitleList作成　
    public static final int url2_end = url2_titol+1;					//各リスト設定とフィールドの書き込み　
    public static final int okuri_artist = url2_end+5;			//送り処理のアルバム抽出；アルバムのみ送る
    public static final int list_2alubum = okuri_artist+5;			//アーティスト→アルバムリストへ
    public static final int list_2titol = list_2alubum+1;			//アルバム→曲選択リストへ
    public static final int call_artistV = list_2titol+5;			//アーティストの呼び直し
    public static final int pref_haikesyoku = call_artistV+5;		//プリファレンスの読み込み後、背景色変更
    public static final int Seek_kick = pref_haikesyoku+5;		//シークバーが動作していない時の再起動
    public static final int CONTEXT_runum_sisei = Seek_kick+10;						//ランダム再生
    public static final int Visualizer_type_none = CONTEXT_runum_sisei + 5;		//191;Visualizerを使わない
    public static final int Visualizer_type_wave = Visualizer_type_none + 1;		//189;Visualizerはwave表示
    public static final int Visualizer_type_FFT = Visualizer_type_wave+1;		//190;VisualizerはFFT
    public static final int LyricCheck = Visualizer_type_FFT + 1;					//歌詞の有無確認
    public static final int LyricEnc= LyricCheck+1;					//歌詞の再エンコード
    public static final int LyricWeb = LyricEnc+1;					//192;歌詞のweb表示
    /**プレイリスト表示?*/
    public static final int v_play_list = LyricWeb+1;
    /**アーティストリスト表示*/
    public static final int v_artist = v_play_list+1;
    /**アルバムリスト表示*/
    public static final int v_alubum = v_artist+1;
    /**タイトルリスト表示*/
    public static final int v_titol = v_alubum+1;
    public static final int rp_artist = v_titol +1;					//アーティストリピート指定ボタン
    /**アルバムリピート指定ボタン*/
    public static final int rp_album = rp_artist +1;
    /**タイトルリピート指定ボタン*/
    public static final int rp_titol = rp_album +1;
    /**二点間リピート*/
    public static final int rp_point = rp_titol +1;
    public static final int settei_hyouji = rp_point+5;					//設定表示			startActivityForResult(intent , reqCode );でjava.lang.IllegalArgumentException: Can only use lower 16 bits for requestCode
    public static final int quit_all = settei_hyouji+1;					//すべて終了
    /**汎用プレイリスト一覧*/
    public static final int PUPRPOSE_lIST=rp_point+50;
    /**汎用プレイリストの曲リスト*/
    public static final int PUPRPOSE_SONG=PUPRPOSE_lIST+1;
    /**楽曲選択*/
    public static final int SELECT_SONG=PUPRPOSE_SONG+1000;
    /**多階層リスト操作中*/
    public static final int SELECT_TREE = SELECT_SONG+1;

    /**プレイリスト用簡易リスト*/
//    public List<String> plSL;//MyConstants.plSLは参照できない

    private MyConstants (){}
}
