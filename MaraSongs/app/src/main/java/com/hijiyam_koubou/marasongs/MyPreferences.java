package com.hijiyam_koubou.marasongs;
/*
  MODE_PRIVATE	自アプリのみ読み書き可能
  MODE_WORLD_READABLE	他アプリから読み取り可能	API Level17で非推奨
  MODE_WORLD_WRITEABLE	他アプリから書き込み可能	API Level17で非推奨
  MODE_MULTI_PROCESS	複数のプロセスで読み書き可能	API Level23で非推奨
  **/

import static android.os.Environment.DIRECTORY_MUSIC;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * プリファレンス読み込み専用クラス
 * 未設定の必須項目は読み込み時に取得
 * **/
public class MyPreferences{
	public OrgUtil ORGUT;		//自作関数集                                                 .putString( "nowData",   putString( MediaStore.Audio.Playlists.Members.DATA
//	public String PREFS_NAME = "defaults";
//	public PreferenceManager mPreferenceManager;

	public Locale locale;	// アプリで使用されているロケール情報を取得
	public Context rContext ;							//Context
	public static SharedPreferences sharedPref;
	public SharedPreferences.Editor myEditor;

//	public String pref_compBunki = null;	 //"40";			//コンピレーション設定[%]
	/**再生制御**/
	/**クロスフェード時間*/
	public String pref_gyapless = null;
	/**プレイヤーの背景	true＝Black"*/
	public boolean pref_pb_bgc = true;				//	http://techbooster.jpn.org/andriod/ui/10152/

	/**リスト設定**********/
	/**詳細無しのシンプルな表示にする*/
	public boolean pref_list_simple = false;				//シンプルなリスト表示（サムネールなど省略）
//	public String pref_artist_bunnri = null;			//"100";		//アーティストリストを分離する曲数
	/**最近追加リストのデフォルト日数*/
	public String pref_saikin_tuika = null;			//"7";
	/**最近再生加リストのデフォルト枚数*/
	public String pref_saikin_sisei = null;
	/**ランダム再生リストアップ曲数*/
	public String pref_rundam_list_size =null;

	/**付属機能**/
	/**ロックスクリーンプレイヤー*/
	public boolean pref_lockscreen =true;
	/**ノティフィケーションプレイヤー*/
	public boolean pref_notifplayer =true;
	/**Bluetoothの接続に連携して一時停止/再開*/
	public boolean pref_bt_renkei =true;
//	public boolean pref_cyakusinn_fukki=true;		//終話後に自動再生

	/**プリファレンス機能*/
	public boolean pref_reset = false;					//設定を初期化
	public boolean pref_listup_reset = false;			//調整リストを初期化

	/**状況記録**/
	/**APIレベル*/
	public String pref_apiLv = "33";
	/**このアプリのバージョンコード*/
	public int pref_sonota_vercord;
	/**内蔵メモリ*/
	public String pref_file_in ="";		//
	/**メモリーカード*/
	public String pref_file_ex="";		//
	/**設定保存フォルダ*/
	public String pref_file_wr="";		//
	/**共通音楽フォルダ*/
	public String pref_commmn_music="";		//

	/**全曲リスト作成時に取得***/
	/**総曲数*/
	public String pref_file_kyoku="0";
	/**総アルバム数*/
	public String pref_file_album="0";
	/**最新更新日*/
	public String pref_file_saisinn="";	//

	/**レジュームに使用**/
	/**
	 * 再生中のプレイリストID
	 * <ul>書込みは
	 * 	<li>リスト選択時：MuList.listOfListClick
	 * 	<li>アプリ終了時：MusicService.destructionPlayer
	 * */
	public String nowList_id;				//	playListID
	/**
	 *  再生中のプレイリスト名
	 * <ul>書込みは
	 * 	<li>リスト選択時：MuList.listOfListClick
	 * 	<li>アプリ終了時：MusicService.destructionPlayer
	 *  */
	public String nowList;					//	playlistNAME
	/**
	 * リスト中のインデックス
	 * <ul>書込みは
	 * 	<li>選曲時：MuList.sousinnMaeSyori
	 * 	<li>アプリ終了時：MusicService.destructionPlayer
	 * */
	public int nowIndex;
	/**
	 * 再生中のファイル名
	 * <ul>書込みは
	 * 	<li>選曲時：MuList.sousinnMaeSyori
	 * 	<li>再生曲変更時：MusicService.onMediaItemTransition
	 * 	<li>アプリ終了時：MusicService.destructionPlayer
	 * */
	public String nowData = "";
	public String saisei_fname =null;
	/**
	 * 再開時間
	 * <ul>書込みは
	 * 	<li>選曲時に0：MuList.sousinnMaeSyori
	 * 	<li>アプリ終了時：MusicService.destructionPlayer
	 * */
	public String pref_position ="0";			//		Integer
	public String pref_saisei_jikan ="0";			//		Integer
	/**再生時間*/
	public String pref_saisei_nagasa  ="0";		//
	/**前回の連続再生曲数*/
	public String pref_zenkai_saiseKyoku ="0";
	/**前回の連続再生時間*/
	public String pref_zenkai_saiseijikann ="0";

	/**再生中のプレイリストのアイテム数*/
	public int list_max = -1;						//リスト選択時に更新
	/**全曲リスト のID*/
	public long pref_zenkyoku_list_id = -1;			//
	/**最近追加 のID*/
	public long saikintuika_list_id = -1;			//
	/**最近再生 のID*/
	public long saikinsisei_list_id = -1;
	/**歌詞のフォントサイズ*/
	public float lylicFontSize=24;


	public int repeatType;							//リピート再生の種類
	public boolean rp_pp;							//2点間リピート中
	public String pp_start;							//リピート区間開始点
	public String pp_end;								//リピート区間終了点

	public String play_order;
	//アーティストごとの情報
	public String artistID;
	//アルバムごとの情報
	public String albumID;
	//曲ごとの情報
	public String audioID;
	public String dataURL = null;
	public String b_List =null;			//前に再生していたプレイリスト
	public int b_List_id = 0;			//前のプレイリストID
	public int modori_List_id = 0;			//リピート前のプレイリストID
	public int b_index;				//前の曲順

	public String prBTname = "";			//前回接続していたBluetooth機器名
	public String prBTAdress = "";			//MACアドレス（機器固有番号）
	public String pMusic_dir = "";			//再生する音楽ファイルがあるフォルダ
	public String pplist_usb = "";			//再生する音楽ファイルがあるUSBメモリーのフォルダ
	public String pplist_rquest = "";		//リクエストリスト
	public String pplist_a_new = "";		//新規プレイリスト

	public List<String> pref_toneList;		//プリファレンス保存用トーンリスト
	public String toneSeparata = "L";
	public String tone_name;				//トーン名称
	public boolean bBoot = false;					//バスブート
	public short reverbBangou = 0;				//リバーブ効果番号
	public int visualizerType;		// = Visualizer_type_FFT;		//VisualizerはFFT
	public int Visualizer_type_none;		//191;Visualizerを使わない
	public int Visualizer_type_wave;		//189;Visualizerはwave表示
	public int Visualizer_type_FFT;		//190;VisualizerはFFT

	public Boolean prTT_dpad = false;		//ダイヤルキーの有無
	public String Siseiothers ="";				//レジューム再生の情報
	public String others ="";				//その他の情報

	public Map<String, ?> keys;
//	public EditTextPreference pEdit;

///外部から呼ばれる時の動作//////////////////////////////
//	public int reqCode = 0;
//	public int backCode = 0;

	public MyPreferences(Context context ) {
		final String TAG = "MyPreferences";
		String dbMsg= "";
		try{
			this.rContext = context;
			readPref();
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	/**端末に保存されている音楽ファイルの確認**/
	@SuppressLint({"DefaultLocale", "Range"})
	public void checkMusicFile(){
		final String TAG = "checkMusicFile";
		String dbMsg="";
		try {
			int kyokuSuu = 0;
			Cursor cursor = null;
			String modifiedDate="";
			dbMsg += "," + pref_file_kyoku + "曲";
			dbMsg += ",最新更新日=" + pref_file_saisinn;
			if(pref_file_kyoku.equals("0") || pref_file_saisinn.equals("") || pref_file_saisinn.equals("0")){
				ContentResolver resolver = rContext.getContentResolver();
				Uri cUri;
				if ( Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT) {
					cUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
					//content://media/external/audio/media
				} else {
					cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
					//     cUri =MediaStore.Audio.Media.INTERNAL_CONTENT_URI はビルドできない
				}
				dbMsg += ",cUri=" + cUri.toString();
				String[] c_columns = null;
				String c_selection = MediaStore.Audio.Media.IS_MUSIC + " <> ? ";			//2.projection   " = ?";
				String[] c_selectionArgs= {"0"};   			//音楽と分類されるファイルだけを抽出する
				String c_orderBy= MediaStore.Audio.Media.DATE_MODIFIED; 				// + MediaStore.Audio.Media.YEAR  + " DESC , "	降順はDESC
				//全音楽ファイル抽出
				cursor = resolver.query(
						cUri,             // Uri of the table
						c_columns,      // The columns to return for each row
						c_selection,       // Selection criteria
						c_selectionArgs,   // Selection criteria
						c_orderBy);
				kyokuSuu = cursor.getCount();
				dbMsg += ">>" + kyokuSuu + "曲";
				pref_file_kyoku = String.valueOf(kyokuSuu);
				myEditor.putString("pref_file_kyoku",pref_file_kyoku).apply();
				if(cursor.moveToLast()){
					modifiedDate = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));
				}
				cursor.close();
			}else{
				kyokuSuu = Integer.parseInt(pref_file_kyoku);
			}
			if(! modifiedDate.equals("")){
				dbMsg += ",最新更新日=" + modifiedDate;
				if(! modifiedDate.equals(pref_file_saisinn)){
					myEditor.putString("pref_file_saisinn",modifiedDate).apply();
					dbMsg += "に更新";
				}
			}
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	/**渡されたIDに該当するプレイリスト名を返す*/
	@SuppressLint("Range")
	public String getPlayListName(Context sContext,Long serchId){				//プリファレンスに文字を書き込む	this.rContext
		final String TAG = "prefItemuKakikomi";
		String dbMsg="";
		String retStr="";
			try{
				dbMsg= "serchId="+ serchId;
				;
				final Uri cUri = MediaStore.Audio.Playlists.Members.getContentUri("external", serchId);
				String[] columns = null;        //{ MediaStore.Audio.Playlists.Members.DATA };
				String c_selection = null;        //MediaStore.Audio.Playlists.Members.DATA +" = ? ";
				String[] c_selectionArgs = null;        //{dataStr};        //⑥引数groupByには、groupBy句を指定します。
				String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
				;
				Cursor playLists= sContext.getContentResolver().query(cUri, columns, c_selection, c_selectionArgs, c_orderBy );
				dbMsg += ",該当" + playLists.getCount() +"件、";
				if(playLists.moveToFirst()){
					retStr = playLists.getString(playLists.getColumnIndex(MediaStore.Audio.Playlists.DISPLAY_NAME));
					dbMsg +=  retStr;
				}
				myLog(TAG,dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"で"+e);
			}
		return retStr;
	}

	/**
	 * プリファレンスの読込み
	 * */
	@SuppressLint("SimpleDateFormat")
	public void readPref(){
		final String TAG = "readPref";
		String dbMsg="";
		try {
			ORGUT = new OrgUtil();        //自作関数集
//			Visualizer_type_wave = MyConstants.Visualizer_type_wave;        //189;Visualizerはwave表示
//			Visualizer_type_FFT = MyConstants.Visualizer_type_wave;        //190;VisualizerはFFT
//			Visualizer_type_none = MyConstants.Visualizer_type_wave;
//			//191;Visualizerを使わない
	//		MyConstants.PREFS_NAME = context.getResources().getString(R.string.pref_main_file);
			dbMsg += ",PREFS_NAME=" + MyConstants.PREFS_NAME;
			pref_apiLv = Build.VERSION.SDK_INT+"";
			dbMsg += ",SDK_INT=" + android.os.Build.VERSION.SDK_INT;
			if (31 <= android.os.Build.VERSION.SDK_INT ) {
				sharedPref = rContext.getSharedPreferences(MyConstants.PREFS_NAME, android.content.Context.MODE_PRIVATE);
				myEditor = sharedPref.edit();
			}else{
				sharedPref = PreferenceManager.getDefaultSharedPreferences(rContext);			//	this.getSharedPreferences(this, MODE_PRIVATE);		//getApplicationContext()
				myEditor = sharedPref.edit();
			}
			String wrStr;
			String selectStr = null;
			String stringList = "";                                            //bundle.getString("list");  //key名が"list"のものを取り出す
			Siseiothers = "";                    //レジューム再生の情報
			others = "";                //その他の情報
			Map< String, ? > keys = sharedPref.getAll();
			dbMsg += ",読み込み開始" + keys.size() + "項目;mySharedPref=" + sharedPref;
//				//まだ作成できていないパラメータの作成
			pref_apiLv = String.valueOf(Build.VERSION.SDK);                                    //APIレベル
			dbMsg += ",pref_apiLv=" + pref_apiLv;
			int now_vercord = 1;
			pref_file_saisinn = "0";    //最新更新日
			PackageManager pm = rContext.getPackageManager();
			try {
				PackageManager packageManager = rContext.getPackageManager();
				PackageInfo packageInfo = packageManager.getPackageInfo(rContext.getPackageName(), PackageManager.GET_ACTIVITIES);
				now_vercord = packageInfo.versionCode;
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
			dbMsg += ",アプリのバージョンコード＝" + now_vercord;

			dbMsg += ",選択中のリスト[" ;
			if(keys.get("nowList_id") == null ){
				nowList_id = "-1";
				nowList = String.valueOf(rContext.getResources().getText(R.string.listmei_zemkyoku));
				dbMsg += ">>" + nowList_id + "]" ;
				myEditor.putString("nowList_id", nowList_id);
				myEditor.putString("nowList", nowList);
				myEditor.apply();
			}else{
				nowList_id = String.valueOf(keys.get("nowList_id"));
				dbMsg +=nowList_id + "]" ;
			}

			if(keys.get("nowList") == null ){
				nowList = String.valueOf(rContext.getResources().getText(R.string.listmei_zemkyoku));
				dbMsg += ">>" + nowList;
				myEditor.putString("nowList", nowList);
				myEditor.apply();
			}else{
				nowList = String.valueOf(keys.get("nowList"));
				dbMsg +=  nowList;
			}
			dbMsg +=nowList ;
			if(nowList_id.equals("-1")){
				if(!nowList.equals(String.valueOf(rContext.getResources().getText(R.string.listmei_zemkyoku)))){
					nowList=getPlayListName(this.rContext, Long.valueOf(nowList_id));
					dbMsg += ">>>"+ nowList;
				}
			}

			if(keys.get("nowIndex") == null ){
				nowIndex =0;
				dbMsg += "[>>" + nowIndex + "]";
				myEditor.putInt("nowIndex", nowIndex);
				myEditor.apply();
			}else{
				nowIndex =  Integer.parseInt(keys.get("nowIndex").toString());
				dbMsg += "[" + nowIndex + "]";
			}

			dbMsg +=lylicFontSize ;
			if(keys.get("lylicFontSize") == null ){
				lylicFontSize = 24;
				dbMsg += "," + lylicFontSize + "sp";
				myEditor.putFloat("lylicFontSize", lylicFontSize);
				myEditor.apply();
			}else{
				lylicFontSize =  Float.parseFloat(keys.get("lylicFontSize").toString());
				dbMsg += "," + lylicFontSize + "sp";
			}

			if(keys.get("pref_file_in") == null ){
				pref_file_in = rContext.getFilesDir().getPath();    //内部データ領域
				dbMsg += ">内蔵メモリ>" + pref_file_in;
				myEditor.putString("pref_file_in", pref_file_in);
				myEditor.apply();
			}else{
				pref_file_in =  String.valueOf(keys.get("pref_file_in"));
				dbMsg += ",内蔵メモリ＝" + pref_file_in;
			}

			if(keys.get("pref_file_ex") == null ){
				pref_file_ex =Environment.getExternalStorageDirectory().getPath();
				dbMsg += ">メモリーカード>" + pref_file_ex;
				myEditor.putString("pref_file_ex", pref_file_ex);
				myEditor.apply();
			}else{
				pref_file_ex = String.valueOf(keys.get("pref_file_ex"));
				dbMsg += ",メモリーカード＝" + pref_file_ex;
			}

			if(keys.get("pref_file_wr") == null ){
				pref_file_wr = rContext.getFilesDir().getPath();
				dbMsg += ">設定保存フォルダ>" + pref_file_wr;
				myEditor.putString("pref_file_wr", pref_file_wr);
				myEditor.apply();
			}else{
				pref_file_wr =  String.valueOf(keys.get("pref_file_wr"));
				dbMsg += ",設定保存フォルダ＝" + pref_file_wr;
			}

			if(keys.get("pref_commmn_music") == null ){
				pref_commmn_music = Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).getPath();
				dbMsg += ">共通音楽フォルダ>" + pref_commmn_music;
				myEditor.putString("pref_commmn_music", pref_commmn_music);
				myEditor.apply();
			}else{
				pref_commmn_music =  String.valueOf(keys.get("pref_commmn_music"));
				dbMsg += ",共通音楽フォルダ＝" + pref_commmn_music;
			}

			if(keys.get("pref_gyapless") == null ){
				pref_gyapless = "1000";
				dbMsg += ">クロスフェード時間>" + pref_gyapless;
				myEditor.putString("pref_gyapless", pref_gyapless);
				myEditor.apply();
			}else{
				pref_gyapless = String.valueOf(keys.get("pref_gyapless"));
				dbMsg += ",クロスフェード時間" + pref_gyapless;
			}

			////				dbMsg += "コンピレーション分岐点" + pref_compBunki;
////				if(pref_compBunki == null){
////				}
			if(keys.get("pref_list_simple") == null ){
				pref_list_simple = false;
				dbMsg += ">シンプルなリスト表示（サムネールなど省略）>" + pref_list_simple;
				myEditor.putBoolean("pref_list_simple", pref_list_simple);
				myEditor.apply();
			}else{
				pref_list_simple = Boolean.valueOf(keys.get("pref_list_simple")+"");
				dbMsg += "シンプルなリスト表示（サムネールなど省略）=" + pref_list_simple;
			}

			if(keys.get("pref_pb_bgc") == null ){
				pref_pb_bgc = false;
				dbMsg += ">プレイヤーの背景は白>" + pref_pb_bgc;
				myEditor.putBoolean("pref_pb_bgc", pref_pb_bgc);
				myEditor.apply();
			}else{
				pref_pb_bgc = Boolean.valueOf(keys.get("pref_pb_bgc")+"");
				dbMsg += ",プレイヤーの背景は白=" + pref_pb_bgc;
			}

////				dbMsg += ",アーティストリストを分離する曲数=" + pref_artist_bunnri;
////				if(pref_artist_bunnri == null){
////					pref_artist_bunnri = "100";
////					dbMsg += ">>" + pref_artist_bunnri;
////					myEditor.putString("pref_artist_bunnri", pref_artist_bunnri);
////				}

			if(keys.get("pref_saikin_tuika") == null ){
				pref_saikin_tuika = "7";
				dbMsg += ">最近追加リストのデフォルト日数>" + pref_saikin_tuika;
				myEditor.putString("pref_saikin_tuika", pref_saikin_tuika);
				myEditor.apply();
			}else{
				pref_saikin_tuika = String.valueOf(keys.get("pref_saikin_tuika"));
				dbMsg += ",最近追加リストのデフォルト日数=" + pref_saikin_tuika;
			}

			if(keys.get("pref_saikin_sisei") == null ){
				pref_saikin_sisei = "100";
				dbMsg += ">最近再生加リストのデフォルト枚数>" + pref_saikin_sisei;
				myEditor.putString("pref_saikin_sisei", pref_saikin_sisei);
				myEditor.apply();
			}else{
				pref_saikin_sisei = String.valueOf(keys.get("pref_saikin_sisei"));
				dbMsg += ",最近再生加リストのデフォルト枚数=" + pref_saikin_sisei;
			}

			if(keys.get("pref_rundam_list_size") == null ){
				pref_rundam_list_size = "100";
				dbMsg += ">ランダム再生リストアップ曲数>" + pref_rundam_list_size;
				myEditor.putString("pref_rundam_list_size", pref_rundam_list_size);
				myEditor.apply();
			}else{
				pref_rundam_list_size = String.valueOf(keys.get("pref_rundam_list_size"));
				dbMsg += ",ランダム再生リストアップ曲数=" + pref_rundam_list_size;
			}

			if(keys.get("pref_lockscreen") == null ){
				pref_lockscreen = true;
				dbMsg += ">ロックスクリーンプレイヤー>" + pref_lockscreen;
				myEditor.putBoolean("pref_lockscreen", pref_lockscreen);
				myEditor.apply();
			}else{
				pref_lockscreen = Boolean.valueOf(keys.get("pref_lockscreen")+"");
				dbMsg += ",ロックスクリーンプレイヤー=" + pref_lockscreen;
			}

			if(keys.get("pref_notifplayer") == null ){
				pref_notifplayer = true;
				dbMsg += ">ノティフィケーションプレイヤー>" + pref_notifplayer;
				myEditor.putBoolean("pref_notifplayer", pref_notifplayer);
				myEditor.apply();
			}else{
				pref_notifplayer = Boolean.valueOf(keys.get("pref_notifplayer")+"");
				dbMsg += ",ノティフィケーションプレイヤー=" + pref_notifplayer;
			}

			if(keys.get("pref_bt_renkei") == null ){
				pref_bt_renkei = false;
				dbMsg += ">Bluetoothの接続に連携して一時停止>" + pref_bt_renkei;
				myEditor.putBoolean("pref_bt_renkei", pref_bt_renkei);
				myEditor.apply();
			}else{
				pref_bt_renkei = Boolean.valueOf(keys.get("pref_bt_renkei")+"");
				dbMsg += ",Bluetoothの接続に連携して一時停止=" + pref_bt_renkei;
			}

//				dbMsg += ",終話後に自動再生=" + pref_cyakusinn_fukki;
//				if(!pref_cyakusinn_fukkiIsIn){
//					pref_cyakusinn_fukki = true;
//					dbMsg += ">>" + pref_cyakusinn_fukki;
//					myEditor.putBoolean("pref_cyakusinn_fukki", pref_cyakusinn_fukki);
//				}

			if ( keys.size() <= 0 ) {         //最初から6項目ある？
				dbMsg += ",初期設定へ" ;
//				prefItialize();
		//		setdPrif(context);
			}else {
				int i = 0;
				for (String key : keys.keySet()) {
					i++;
					dbMsg += "\n" + i + "/" + keys.size() + ")" + key + " は " + keys.get(key);
					try {
						if (String.valueOf(keys.get(key)) != null) {
							if (key.startsWith("myPreferences")) {
								dbMsg += "　は誤記";
								myEditor.remove(key).commit();
							}else if (key.equals("nowData")) {
								saisei_fname = String.valueOf(keys.get(key));
								dbMsg += "　は再生中のファイル";
								nowData = saisei_fname;
							} else if (key.equals("pref_position")) {
								pref_position = String.valueOf(keys.get(key));
								dbMsg += " = ";//////////////////
								wrStr = ORGUT.sdf_mss.format(Long.valueOf(pref_position));
								dbMsg += "；再生ポジション= " + pref_position + " =" + wrStr;                //+";"+pTF_saisei_jikan.getText();//////////////////
							} else if (key.equals("pref_saisei_jikan")) {
								pref_saisei_jikan = String.valueOf(keys.get(key));        //再開時間		Integer.valueOf(keys.get(key).toString());
								dbMsg += " = ";//////////////////
								wrStr = ORGUT.sdf_mss.format(Long.valueOf(pref_saisei_jikan));
								dbMsg += "；再生ポジション= " + pref_saisei_jikan + " =" + wrStr;                //+";"+pTF_saisei_jikan.getText();//////////////////
							} else if (key.equals("pref_duration")) {                    //
								pref_saisei_nagasa = String.valueOf(keys.get(key));        //pTF_pref_saisei_nagasa;		//再生中音楽ファイルの再生時間
								dbMsg += " ,再生時間＝ " + pref_saisei_nagasa;//////////////////
								wrStr = ORGUT.sdf_mss.format(Long.valueOf(pref_saisei_nagasa));
								dbMsg += "；" + pref_saisei_nagasa + "=" + wrStr;                //+";"+pTF_saisei_jikan.getText();//////////////////
							} else if (key.equals("pref_zenkyoku_list_id")) {
								pref_zenkyoku_list_id = Integer.parseInt(keys.get(key).toString());
								dbMsg += ",全曲リスト[" + pref_zenkyoku_list_id + "]";
							} else if (key.equals("saikintuika_list_id")) {
								saikintuika_list_id = Integer.parseInt(keys.get(key).toString());
								dbMsg += ",最近追加[" + saikintuika_list_id + "]";
							} else if (key.equals("saikinsisei_list_id")) {
								saikinsisei_list_id = Integer.parseInt(keys.get(key).toString());
								dbMsg += ",最近再生[" + saikinsisei_list_id + "]";
							} else if (key.equals("play_order")) {
								play_order = keys.get(key).toString();
								dbMsg += "(play_order=" + play_order + ")";
							} else if (key.equals("list_max")) {
								list_max = Integer.parseInt(keys.get(key).toString());
								dbMsg += "(list_max=" + list_max + "件";		///再生中のプレイリストのアイテム数
							} else if (key.equals("artistID")) {
								artistID = keys.get(key).toString();
								dbMsg += ",アーティスト=" + artistID;////////////////////////////////////////////////////////////////////////////
							} else if (key.equals("albumID")) {
								albumID = keys.get(key).toString();
								dbMsg += ",アルバム=" + albumID;////////////////////////////////////////////////////////////////////////////
							} else if (key.equals("audioID")) {
								audioID = keys.get(key).toString();
								dbMsg += ",曲=" + audioID;////////////////////////////////////////////////////////////////////////////
							} else if (key.equals("dataFN")) {
								saisei_fname = String.valueOf(keys.get(key));
								dbMsg += "saisei_fname=" + saisei_fname;
//							} else if (key.equals("pref_compBunki")) {
//								pref_compBunki = keys.get(key).toString();
//								dbMsg += "コンピレーション分岐 = " + pref_compBunki;
//							} else if (key.equals("pref_artist_bunnri")) {
//								pref_artist_bunnri = keys.get(key).toString();
//								dbMsg += "アーティストリストを分離する曲数=" + pref_artist_bunnri;
							} else if (key.equals("repeatType")) {            //");;			//
								repeatType = Integer.valueOf(keys.get(key).toString());    //
								dbMsg += ">リピート再生の種類=" + repeatType;
							} else if (key.equals("pref_nitenkan")) {            //");		//ダイヤルキー
								rp_pp = Boolean.valueOf(keys.get(key).toString());
								dbMsg += "二点間再生中=" + rp_pp;    //pref_nitenkan
							} else if (key.equals("pref_nitenkan_start")) {
								pp_start = keys.get(key).toString();
								if (pp_start == null) {
									pp_start = "0";
								}
								dbMsg += ";二点間再生開始点=" + pp_start;////////pref_nitenkan_start//////////
							} else if (key.equals("pref_nitenkan_end")) {
								pp_end = keys.get(key).toString();
								if (pp_end == null) {
									pp_end = "1";
								}
								dbMsg += ";二点間再生終了点=" + pp_end;/////pref_nitenkan_end////////////////////////////
							} else if (key.equals("b_List")) {            //");
								dbMsg += ",前に再生していたプレイリスト=";
								b_List = keys.get(key).toString();
								dbMsg += b_List;
							} else if (key.equals("b_List_id")) {            //");
								dbMsg += ",前のプレイリストID=";
								b_List_id = Integer.valueOf(keys.get(key).toString());
								dbMsg += String.valueOf(b_List_id);
							} else if (key.equals("b_index")) {            //");
								dbMsg += ",前に再生していたプレイリスト中のID=";
								b_index = Integer.valueOf(keys.get(key).toString());
								dbMsg += String.valueOf(b_index);
							} else if (key.equals("modori_List_id")) {            //");
								dbMsg += ",リピート前に再生していたプレイリスト中のID=";
								modori_List_id = Integer.valueOf(keys.get(key).toString());
								dbMsg += String.valueOf(modori_List_id);
							} else if (key.equals("tone_name")) {
								tone_name = keys.get(key).toString();    //
								dbMsg += "トーン名称=" + tone_name;
							} else if (key.equals("pref_toneList")) {                //http://qiita.com/tomoima525/items/f8cf688ad9571d17df41
								stringList = String.valueOf(keys.get(key));                                            //bundle.getString("list");  //key名が"list"のものを取り出す
								dbMsg += ",stringList= " + stringList;
								try {
									JSONArray array = new JSONArray(stringList);
									dbMsg += ",array= " + array;
									int length = array.length();
									dbMsg += "= " + length + "件";
									pref_toneList = new ArrayList<String>();                //トーンリストの初期化
									for (int j = 0; j < length; j++) {
										dbMsg += "(" + j + "/" + length + ")" + array.optString(j);
										pref_toneList.add(array.optString(j));
									}
								} catch (JSONException e1) {
									e1.printStackTrace();
								}
//									dbMsg +=  ",トーン配列=" + pref_toneList ;
							} else if (key.equals("bBoot")) {
								bBoot = Boolean.valueOf(keys.get(key).toString());    //
								dbMsg += "バスブート=" + bBoot;
							} else if (key.equals("reverbBangou")) {
								reverbBangou = Short.valueOf(keys.get(key).toString());    //
								dbMsg += ",リバーブ効果番号=" + reverbBangou;
							} else if (key.equals("visualizerType")) {
								visualizerType = Integer.valueOf(keys.get(key).toString());    //
								dbMsg += "visualizerType=" + visualizerType;
							} else if (key.equals("pref_zenkai_saiseKyoku")) {
								pref_zenkai_saiseKyoku = String.valueOf(keys.get(key));        //pTF_pref_saisei_nagasa;		// != null ){		//
								dbMsg += "前回の連続再生曲数＝" + pref_zenkai_saiseKyoku;//////////////////
							} else if (key.equals("pref_zenkai_saiseijikann")) {
								pref_zenkai_saiseijikann = String.valueOf(keys.get(key));
								wrStr = ORGUT.sdf_mss.format(Long.valueOf(pref_zenkai_saiseijikann));
								dbMsg += "；前回の連続再生時間= " + pref_zenkai_saiseijikann + " =" + wrStr;                //+";"+pTF_saisei_jikan.getText();//////////////////
							} else if (key.equals("pref_file_kyoku")) {
								pref_file_kyoku = String.valueOf(keys.get(key));
								dbMsg += "総曲数＝" + pref_file_kyoku;////////////////
							} else if (key.equals("pref_file_album")) {
								pref_file_album = String.valueOf(keys.get(key));
								dbMsg += "総アルバム数＝" + pref_file_album;//////////////////
							} else if (key.equals("pref_file_saisinn")) {
								pref_file_saisinn = String.valueOf(keys.get(key));
								if (!pref_file_saisinn.equals("")) {
									wrStr = ORGUT.sdf_yyyyMMddHHmm.format(Long.valueOf(pref_file_saisinn) * 1000);
									dbMsg += "；最近の追加日= " + pref_file_saisinn + " =" + wrStr;                //+";"+pTF_saisei_jikan.getText();//////////////////
								}
							} else if (key.equals("pref_sonota_dpad")) {            //");		//ダイヤルキー
								prTT_dpad = Boolean.valueOf(keys.get(key).toString());
								dbMsg += "、ダイヤルキー=" + prTT_dpad;////////////////////////////////////////////////////////////////////////////
							} else if (key.equals("pref_sonota_vercord")) {
								pref_sonota_vercord = Integer.parseInt(String.valueOf(keys.get(key)));
								dbMsg += "、このアプリのバージョンコード＝" + pref_sonota_vercord;
								if (pref_sonota_vercord != now_vercord) {
									pref_sonota_vercord = now_vercord;
									myEditor.putInt("pref_sonota_vercord", pref_sonota_vercord);
								}
							}
						}
					} catch (Exception e) {
						myErrorLog(TAG, dbMsg + "；" + e);
					}
				}

			}
			checkMusicFile();
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}


//	public void prefItemuKakikomi(String key ,String vStr){				//プリファレンスに文字を書き込む
//		final String TAG = "prefItemuKakikomi";
//			String dbMsg="";
//		try{
//			dbMsg= key+";"+ vStr;
//			key = String.valueOf( key );
//			vStr = String.valueOf( vStr );
//			dbMsg= ">>" + key+";"+ vStr;
//			myEditor.putString( key, vStr);						//再生中のファイル名  Editor に値を代入
//			boolean wrb = myEditor.commit();
//			dbMsg +=">>書込み成功="+ wrb;
//			myLog(TAG,dbMsg);
//		} catch (Exception e) {
//			myErrorLog(TAG,dbMsg+"で"+e);
//		}
//	}		//http://ichitcltk.hustle.ne.jp/gudon/modules/pico_rd/index.php?content_id=98
//

	/**プリファレンスにbool値を書き込む*/
	public void prefBoolWrite(String key ,boolean vBool){
		final String TAG = "prefBoolWrite";
		String dbMsg="";
		try{
			dbMsg= key+";"+ vBool;
			key = String.valueOf( key );
			vBool = Boolean.valueOf( vBool );
			dbMsg= ">>" + key+";"+ vBool;
			myEditor.putBoolean( key + "", vBool);						//再生中のファイル名  Editor に値を代入
			//20190506;java.lang.NullPointerException: Attempt to invoke interface method 'android.content.SharedPreferences$Editor android.content.SharedPreferences$Editor.putBoolean(java.lang.String, boolean)' on a null object reference
			myEditor.apply();    //非同期書込み
//			boolean wrb = myEditor.commit();	//は同期書込み
//			dbMsg= ">>書込み成功="+ wrb;
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}		//http://ichitcltk.hustle.ne.jp/gudon/modules/pico_rd/index.php?content_id=98
//	boolean kariBool =false;
//
//	/**
//	 * NumberPickerの変更反映
//	 * 変更値をObjectからbooleanにキャストしてプリファレンスに書き込む
//	 * 項目追加時は個々の変数にここで設定
//	 * */
//	private OnPreferenceChangeListener numberPickerListener =new OnPreferenceChangeListener(){			//NumberPickerの PreferenceChangeリスナー
//		@Override
//		public boolean onPreferenceChange(Preference preference, Object newValue) {
//			final String TAG = "numberPickerListener";
//			String dbMsg="[MyPreferences]onPreferenceChange:";
//			try{
//				String keyName =  preference.getKey();
//				dbMsg= keyName + ";" + newValue;
//				String atai = String.valueOf(newValue) ;
//				dbMsg +=";" + atai;
//				if( keyName.equals("pref_gyapless") ){											//クロスフェード時間
//					pref_gyapless = String.valueOf(pTF_pref_gyapless.getText());
////					pref_gyapless = String.valueOf(pTF_pref_gyapless.retValue(Integer.valueOf(atai)));
//					dbMsg +=",クロスフェード時間=" + pref_gyapless;
//					pTF_pref_gyapless.setText(pref_gyapless +  getResources().getString(R.string.pp_msec));
//					atai = pref_gyapless;
////				}else if(keyName.equals("pref_compBunki")){								//コンピレーション
////					pref_compBunki = String.valueOf(pTF_pref_compBunki.getText());
//////					pref_compBunki = String.valueOf(pTF_pref_compBunki.retValue(Integer.valueOf(atai)));
////					dbMsg +=",コンピレーション=" + pref_compBunki;
////					pTF_pref_compBunki.setSummary(pref_compBunki +  "%");
////					atai = pref_compBunki;
////				}else if(keyName.equals("pref_artist_bunnri")){						//アーティストリストを分離する曲数
////					pref_artist_bunnri = String.valueOf(pTF_pref_artist_bunnri.getText());
//////					pref_artist_bunnri = String.valueOf(pTF_pref_artist_bunnri.retValue(Integer.valueOf(atai)));
////					dbMsg +=",アーティストリストを分離する曲数=" + pref_artist_bunnri;
////					pTF_pref_artist_bunnri.setSummary(pref_artist_bunnri + getResources().getString(R.string.comon_kyoku));
////					atai = pref_artist_bunnri;
//				}else if(keyName.equals("pref_saikin_tuika")){							//最近追加リストのデフォルト枚数
//					pref_saikin_tuika = String.valueOf(pTF_prefsaikin_tuika.getText());
////					pref_saikin_tuika = String.valueOf(pTF_prefsaikin_tuika.retValue(Integer.valueOf(atai)));
//					dbMsg +=",最近追加リストのデフォルト日数=" + pref_saikin_tuika;
////					pTF_prefsaikin_tuika.setSummary(pref_saikin_tuika +  getResources().getString(R.string.common_nitibun));
//					atai = pref_saikin_tuika;
//				}else if(keyName.equals("pref_saikin_sisei")){							//最近再生リストのデフォルト曲数
//					pref_saikin_sisei = String.valueOf(pTF_prefsaikin_sisei.getText());
////					pref_saikin_sisei = String.valueOf(pTF_prefsaikin_sisei.retValue(Integer.valueOf(atai)));
//					dbMsg +=",最近再生リストのデフォルト曲数=" + pref_saikin_sisei;
////					pTF_prefsaikin_sisei.setSummary(pref_saikin_sisei + getResources().getString(R.string.common_nitibun));
//					atai = pref_saikin_sisei;
//				}else if(keyName.equals("pref_rundam_list_size")){							//ランダム再生の設定曲数
//					pref_rundam_list_size = String.valueOf(pTF_rundam_list_size.getText());
//					dbMsg +=",ランダム再生の設定曲数=" + pref_rundam_list_size;
////					pTF_rundam_list_size.setSummary(pref_rundam_list_size +  getResources().getString(R.string.pp_msec));
//					atai = pref_rundam_list_size;
//				}
//				prefItemuKakikomi(keyName ,atai);
//				myLog(TAG,dbMsg);
//			} catch (Exception e) {
//				myErrorLog(TAG,dbMsg+"で"+e);
//			}
//			return true;
//		}
//	};
//
//	/**
// * SwitchPreferenceCompatの変更反映
// * 変更値をObjectからbooleanにキャストしてプリファレンスに書き込む
// * 項目追加時は個々の変数にここで設定
// * */
//	private OnPreferenceChangeListener switchListener =new OnPreferenceChangeListener(){	// SwitchPreferenceCompatの PreferenceChangeリスナー
//		@Override
//		public boolean onPreferenceChange(Preference preference, Object newValue) {
//			final String TAG = "switchListener";
//			String dbMsg="[MyPreferences]onPreferenceChange:";
//			try{
//				String keyName =  preference.getKey();
//				dbMsg= keyName + ";" + newValue;
//				boolean atai = (boolean) newValue;
//				dbMsg +=";" + atai;
//				if( keyName.equals("pref_list_simple") ){
//					pref_list_simple = atai;
//					dbMsg +=",シンプルなリスト表示=" + pref_list_simple;
//				}else if( keyName.equals("pref_pb_bgc") ){
//					pref_pb_bgc = atai;
//					dbMsg +=",プレイヤーの背景=" + pref_pb_bgc;
//				}else if( keyName.equals("pref_notifplayer") ){
//					pref_notifplayer = atai;
//					dbMsg +=",ノティフィケーションプレイヤー=" + pref_notifplayer;
//				}else if( keyName.equals("pref_lockscreen") ){
//					pref_lockscreen = atai;
//					dbMsg +=",ロックスクリーンプレイヤー=" + pref_lockscreen;
////				}else if( keyName.equals("pref_cyakusinn_fukki") ){
////					pref_cyakusinn_fukki = atai;
////					dbMsg +=",通話連携=" + pref_cyakusinn_fukki;
//				}else if( keyName.equals("pref_bt_renkei") ){
//					pref_bt_renkei = atai;
//					dbMsg +=",Bluetooth連携=" + pref_bt_renkei;
//				}
//				prefBoolKakikomi(keyName ,atai);
//				myLog(TAG,dbMsg);
//			} catch (Exception e) {
//				myErrorLog(TAG,dbMsg+"で"+e);
//			}
//			return true;
//		}
//	};
//
//	private OnPreferenceChangeListener listPreference_OnPreferenceChangeListener =new OnPreferenceChangeListener(){	// リストPreferenceの　PreferenceChangeリスナー
//		@Override
//		public boolean onPreferenceChange(Preference preference, Object newValue) {
//			return listPreference_OnPreferenceChange(preference,newValue);
//		}
//	};
//
//	private boolean listPreference_OnPreferenceChange(Preference preference, Object newValue){
//		final String TAG = "listPreference_OnPreferenceChange[MyPreferences]";
//		String dbMsg="";
//		try{
//			dbMsg="newValue="+ newValue;
//			ListPreference listpref =(ListPreference)preference;
//			dbMsg +=",listpref="+ listpref;
////			String summary = String.format("entry=%s , value=%s", listpref.getEntry(),listpref.getValue());
////			dbMsg +=",summary="+ summary;
//			preference.setSummary((CharSequence) newValue);
//			if(newValue.equals(getString(R.string.pref_effect_vi_fft))){				//スペクトラムアナライザ風
//				visualizerType = MyConstants.Visualizer_type_FFT;
//			} else if(newValue.equals(getString(R.string.pref_effect_vi_wave))){		//オシロスコープ風
//				visualizerType = MyConstants.Visualizer_type_wave;
//			} else if(newValue.equals(getString(R.string.comon_tukawanai))){			//使わない
//				visualizerType = MyConstants.Visualizer_type_none;
//			}
//			dbMsg +=",visualizerType="+ visualizerType;
////			String effectMsg = (String) pPS_pref_effect.getSummary();				//サウンドエフェクト
////			effectMsg = effectMsg.substring(0, effectMsg.indexOf(getString(R.string.pref_effect_vi))) + "\n" +
////					getString(R.string.pref_effect_vi) + ";" + newValue ;
////			pPS_pref_effect.setSummary(effectMsg);
//			myLog(TAG,dbMsg);
//		} catch (Exception e) {
//			myErrorLog(TAG,dbMsg+"で"+e);
//		}
//		return true;
//	}
//
//	//メニューボタンで表示するメニュー///////////////////////////////////////////////////////////////////////////////
//	@Override
//	public boolean onCreateOptionsMenu(Menu flMenu) {
//	//	//Log.d("onCreateOptionsMenu","NakedFileVeiwActivity;mlMenu="+flMenu);
//		getMenuInflater().inflate(R.menu.pref_menu , flMenu);		//メニューリソースの使用
//		return super.onCreateOptionsMenu(flMenu);
//	}
//
//	public boolean makeOptionsMenu(Menu flMenu) {	//ボタンで表示するメニューの内容
//		return true;
//	}
//
//	@Override
//	public boolean onPrepareOptionsMenu(Menu flMenu) {			//表示直前に行う非表示や非選択設定
//		if(locale.equals( Locale.JAPAN)){										//日本語の場合のみconstant for ja_JP.
//			flMenu.findItem(R.id.menu_item_sonota_help).setEnabled(true);		//ヘルプ表示	MENU_HELP
//		}else{
//			flMenu.findItem(R.id.menu_item_sonota_help).setEnabled(false);		//ヘルプ表示	MENU_HELP
//		}
//		flMenu.findItem(R.id.menu_item_sonota_end).setEnabled(true);	//終了	MENU_END
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		final String TAG = "onOptionsItemSelected";
//		String dbMsg="";
//		dbMsg+=ORGUT.nowTime(true,true,true);/////////////////////////////////////
//		try{
//			Intent intentPRF;
//			dbMsg += "MenuItem"+item.getItemId()+"="+item.toString();////////////////////////////////////////////////////////////////////////////
//			String helpURL;
//			int nowSelectMenu = item.getItemId();
//			myLog(TAG,dbMsg);
//			switch (nowSelectMenu) {
//				case R.id.menu_item_sonota_help:						//ヘルプ表示	MENU_HELP
//					Intent intentWV = new Intent(MyPreferences.this,wKit.class);			//webでヘルプ表示
//					if(locale.equals( Locale.JAPAN)){										//日本語の場合のみconstant for ja_JP.
//						helpURL = "file:///android_asset/pref.html";		//日本語ヘルプ
//						intentWV.putExtra("dataURI",helpURL);		//"file:///android_asset/index.html"
//					}else {
//						helpURL = "file:///android_asset/en/pref.html";	//英語ヘルプ
//					}
//					startActivity(intentWV);
//					return true;
//				case android.R.id.home:
//				case R.id.menu_item_sonota_end:					//終了	MENU_END
//					quitMe();		//このアプリを終了する
//					return true;
//			}
//			return false;
//		} catch (Exception e) {
//			myErrorLog(TAG,dbMsg +"で"+e.toString());
//			return false;
//		}
//	}
//
//	@Override
//	public void onOptionsMenuClosed(Menu flMenu) {
//		//Log.d("onOptionsMenuClosed","NakedFileVeiwActivity;mlMenu="+flMenu);
//	}
//	//このアプリを終了する/////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////メニューボタンで表示するメニュー//
//	@Override
//	public void onWindowFocusChanged(boolean hasFocus) {	 // ヘッドのイメージは実際にローディンされた時点で設定表示と同時にウィジェットの高さや幅を取得したいときは大抵ここで取る。
//			if (hasFocus) {
//				final String TAG = "onWindowFocusChanged";
//				String dbMsg="";
//				try{
//					myLog(TAG,dbMsg);
//		//			prefHyouji();				//プリファレンス画面表示
//				}catch (Exception e) {
//					myErrorLog(TAG,dbMsg + "で"+e.toString());
//				}
//		 }
//		 super.onWindowFocusChanged(hasFocus);
//	 }
////
////	@Override
////	protected void onActivityResult(int requestCode, int resultCode, Intent rData) { // startActivityForResult で起動させたアクティビティがfinish() により破棄されたときにコールされる
////	// requestCode : startActivityForResult の第二引数で指定した値が渡される
////	// resultCode : 起動先のActivity.setResult の第一引数が渡される
////	// Intent data : 起動先Activityから送られてくる Intent
////		super.onActivityResult(requestCode, resultCode, rData);
////		final String TAG = "setHeadImgList";
////		String dbMsg="";
////		try{
////			dbMsg += "requestCode="+requestCode+",resultCode="+resultCode+",rData="+rData;//////////////////////////////////////////////////////////////////////////////
////			Bundle bundle = null ;
////			if(rData != null){
////				bundle = rData.getExtras();
////				if(resultCode == RESULT_OK){			//-1
//////					switch(requestCode) {
//////					case R.string.pref_reset:						//));				//ダイアログタイトル ;設定を初期化
//////						pref_reset= true;
//////						dbMsg +=pref_reset;//////////////////
//////						myLog(TAG,dbMsg);
//////						prefBoolKakikomi("pref_reset" ,pref_reset);
//////						break;
//////					}
////				}
////			}
////			myLog(TAG,dbMsg);
////		} catch (Exception e) {		//汎用
////			myErrorLog(TAG,dbMsg+"で"+e.toString());
////		}
////	}		//http://fernweh.jp/b/startactivityforresult/
//
//	@SuppressWarnings("deprecation")
//	@Override
//	protected void onResume() {
//		super.onResume();
//		final String TAG = "onResume";
//		String dbMsg="";
//		try{
//			myLog(TAG,dbMsg);
//		}catch (Exception e) {
//			myErrorLog(TAG,dbMsg + "で"+e.toString());
//		}
//	}
//
//	@SuppressWarnings("deprecation")
//	@Override
//	protected void onPause() {
//		super.onPause();
//		final String TAG = "onResume";
//		String dbMsg="";
//		try{
//	//		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
//			myLog(TAG,dbMsg);
//		}catch (Exception e) {
//			myErrorLog(TAG,dbMsg + "で"+e.toString());
//		}
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		quitMe();
//		myLog("onDestroy","onDestroyが発生");
//	}
//
	///////////////////////////////////////////////////////////////////////////////////
	public static void myLog(String TAG , String dbMsg) {
		MyUtil MyUtil = new MyUtil();
		MyUtil.myLog(TAG , "[MyPreferences]" + dbMsg);
	}

	public static void myErrorLog(String TAG , String dbMsg) {
		MyUtil MyUtil = new MyUtil();
		MyUtil.myErrorLog(TAG , "[MyPreferences]"+ dbMsg);
	}

}

//http://yan-note.blogspot.jp/2010/09/android_12.html
//http://android.roof-balcony.com/shori/strage/localfile-2/