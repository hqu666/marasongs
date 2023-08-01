package com.hijiyam_koubou.marasongs;

public final class MyConstants {
// class?
    //MusicPlayerServiceから設定
//    /**リスト中のインデックス*/
//    public static int mIndex;						//play_order
//    /**リストの総登録曲数*/
//    public static int listEnd;

    //プリファレンス
    public static String PREFS_NAME = "main";				//プリファレンスファイル名


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
    /**仮リストからアーティストリストへ転記*/
    public static final int make_kari_list = v_play_list+1;
    /**仮リストからアーティストリストへ転記*/
    public static final int kari2artist = make_kari_list+1;

    /**アーティストリスト表示*/
    public static final int v_artist = kari2artist+1;
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
    public static final int SELECT_SONG=PUPRPOSE_SONG+100;
    /**多階層リスト操作中*/
    public static final int SELECT_TREE = SELECT_SONG+1;

    /**ノティフィケーションの曲戻し*/
    public static final int ACTION_CODE_REWINDE = SELECT_TREE + 101;
    /**ノティフィケーションの再生停止*/
    public static final int ACTION_CODE_PLAYPAUSE = ACTION_CODE_REWINDE + 1;
    /**ノティフィケーションの曲送り*/
    public static final int ACTION_CODE_SKIP = ACTION_CODE_PLAYPAUSE + 1;
    /**ノティフィケーションから終了*/
    public static final int ACTION_CODE_quit = ACTION_CODE_SKIP + 1;

    /**プレイリスト用簡易リスト*/
//    public List<String> plSL;//MyConstants.plSLは参照できない

    private MyConstants (){}
}
