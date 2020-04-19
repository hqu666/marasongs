package com.hijiyam_koubou.marasongs;
/**
 *
 * 20200307:TITLE,DATA,COMPOSER が転記されていない
 * 202002;m3u書き出し
 * **/

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;

public class ZenkyokuList extends Activity implements plogTaskCallback{		// extends ProgressDialog implements  Runnable
	OrgUtil ORGUT;				//自作関数集
	Util UTIL;
	MusicPlaylist musicPlaylist ;
	MyPreferences myPreferences;


	MaraSonActivity MSA;
	public Context cContext ;
	public plogTaskCallback callback;
	public plogTask pTask;

	public ScrollView pdg_scroll;		//スクロール
	public TextView pgd_msg_tv ;
	public Handler handler1;
	public ProgressBar progBar1;		 //メインプログレスバー
	public TextView pgd_val_tv;
	public TextView pgd_max_tv;
	public TextView pgd_par_tv;
	public Handler handler2;
	public ProgressBar ProgBar2;		 //セカンドプログレスバー
	public TextView pgd_val2_tv;
	public TextView pgd_max2_tv;
	public TextView pgd_par2_tv;
	public Button pgd_finsh_bt;		//終了ボタン
	public LinearLayout pgdBar1_ll;		//メインプログレスバーエリア
	public LinearLayout pgdBar2_ll;		//セカンドプログレスバーエリア
	public String _numberFormat = "%d/%d";
	public  NumberFormat _percentFormat = NumberFormat.getPercentInstance();
	//プリファレンス
	public Editor pNFVeditor ;
	public SharedPreferences sharedPref;
	public Editor myEditor ;
	public String dataFN;						//再生中のファイル名
	public String ruikei_artist;				//アーティスト累計
	public String pref_compBunki = "40";		//コンピレーション設定[%]
	public boolean pref_cyakusinn_fukki=true;		//終話後に自動再生
	public boolean pref_bt_renkei =true;				//Bluetoothの接続に連携して一時停止/再開
	public boolean pref_list_simple =true;				//シンプルなリスト表示（サムネールなど省略）
	public boolean pref_lockscreen =true;				//ロックスクリーンプレイヤー</string>
	public boolean pref_notifplayer =true;				//ノティフィケーションプレイヤー</string>
	public String myPFN = "ma_pref";
	public String pref_commmn_music = "";				//音楽ファイルの格納先
	public String pref_data_url = "";

	long start;		// 開始時刻の取得
	long startPart;		// 開始時刻の取得
	public int reqCode=0;					//処理番号
	public int StartReqCode=0;					//処理番号
	public int reTry=0;					//再処理リミット

	public String pdTitol;
	public String pdMessage;
	public int pdCoundtVal;		//ProgressDialog表示値
	public int pdMaxVal;		//ProgressDialog表示値
	public String pdMes;
	public CharSequence pdMessage_stok;			//ProgressDialog のメッセージを設定
	public int pdStartVal=0;					//ProgressDialog の初期値を設定 (水平の時)
	public int pd2MaxVal = 10;					//ProgressDialog の最大値を設定 (水平の時)
	public int pd2CoundtVal;					//ProgressDialog表示値

	public int kyoku = 0 ;
	public Cursor cursor;
	public Uri cUri;		// scheme, for the content to retrieve
	public int souKyokuSuu =0;
	public int souMaiSuu=0;
	public int souNinzuuk=0;
	public int titolCount =0;
	public int artistBk=0;
	public int albumBk=0;
	public int titolBk=0;
	public int trackBk=0;
	public String saisinnbi;
	public String exDir ="";				//外部メモリ
	public String inDir ="";				//内蔵メモリ
	public String exDrive;
	public String inDrive;
	public List<String> creditArtistList;		//クレジットされているアーティスト名
	public List<String> albumList;		//アルバム名
	public List<String> titolList ;		//曲名
	public List<String> trackNoList;		//曲順
	public ArrayList<String> rArtistList;				//仮アルバムアーティスト
	public ArrayList<String> shortArtistList;		//最短アーティスト名
	public List<Map<String, Object>> artistList;				//アルバムアーティスト
	public List<Map<String, Object>> objList;				//アルバムアーティスト
	public Map<String, Object> objMap;				//汎用マップ
	public List<Map.Entry<String, String>> omitlist;
	public ArrayList<String> tList;
	public SQLiteDatabase kariArtist_db;
	public SQLiteDatabase artist_db;
	public ArtistHelper artistHelper;		//アーティスト名のリストの定義ファイル
	public String artistTName;			//アーティストリストのテーブル名

	public SQLiteDatabase Kari_db;									//全曲の仮ファイル	kari.db
	public ZenkyokuHelper zenkyokuHelper = null;				//全曲リストヘルパー
	public SQLiteDatabase Zenkyoku_db;		//全曲リストファイル
	public String zenkyokuTName;			//全曲リストのテーブル名
//	public String[] zenkyokuColom = null ;
	public SQLiteDatabase shyuusei_db;									//登録アーティスト修正DB
	public shyuuseiHelper shyuusei_Helper = null;				//登録アーティストヘルパー
	public String shyuuseiTName;

	public int shigot_bangou = 0;
	public static final int pt_start = 800;																//800;処理開始
	public static final int pt_preReadEnd=pt_start+1;												//801;メディアストア更新  ←preReadで欠けが見つかった場合のみ
	public static final int pt_mastKopusin=pt_preReadEnd+1;										//802;メディアストア更新  ←preReadで欠けが見つかった場合のみ
	public static final int pt_KaliArtistList=pt_mastKopusin+1;									//803;仮アーティスト作成
	public static final int pt_CreateKaliList=pt_KaliArtistList+1;									//804;仮リスト作成
	
	public static final int pt_jyuufukuSakujyo = pt_CreateKaliList+1;					//804;コンピレーション抽出；アルバムアーティスト名の重複
	public static final int pt_HenkouHanei= pt_jyuufukuSakujyo+1;						//805;ユーザーの変更を反映
	public static final int pt_CreateZenkyokuList = pt_HenkouHanei+1;					//806;全曲リスト作成
	public static final int pt_CompList = pt_CreateZenkyokuList+1;						//807;全曲リストにコンピレーション追加
//	public static final int pt_artistList_yomikomi = pt_CompList+1;								//808;アーティストリストを読み込む(db未作成時は-)
	public static final int pt_artistList_yomikomi = pt_CompList+1;								//808;アーティストリストを読み込む(db未作成時は-)
	public static final int pt_end = pt_artistList_yomikomi+1;										//809;最終処理
	public String bArtistN;
	public String aArtist ;
	public String b_AlbumMei ;
	public int albumCount;
	public String album_art;
	public String last_year ;
	public String compSelection ;			//+ comp ;		//MediaStore.Audio.Media.ARTIST +" <> " + comp;			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
	public String[] compList;				//全曲リスト末尾にまとめるもの
	public int compCount;
	public int comCount;					//コンピレーションなど末尾につける匿名
	public String all_songs_file_name ;		//全曲リストの汎用リスト
	public int allSongsID;
	public String album_artist_file_name ;			//アーティスト名の汎用リスト
	public String[] genleList = {"Pop","Jazz","Blues","Classic","Soundtrack","Compilations"};				//コンピレーション化するジャンル
	public List<String> compGenList;		//実際にあったコンピレーション化するジャンル


	public int sousalistID;		//操作対象リストID
	public String sousalistName;		//操作対象リスト名
	public String sousalist_data;		//操作対象リストのUrl
	public Uri sousalistUri;		//操作対象リストUri
	public int sousaRecordId;		//操作対象レコードのID
	public int sousaCount;			//操作カウント
	public Cursor playLists;

	public int artintCo = 0;
	public int albamCo = 0;
	public int titolCo = 0;
	public String albumMei = "";
	public String artURL = null;
	public List<String> aArtistList;		//アルバムアーティスト
	public String artistlist = "";
	String lastYear = null;

	public void readPref() {        //プリファレンスの読込み
		final String TAG = "readPref";
		String dbMsg = "[ZenkyokuList]";
		try {
			myPreferences = new MyPreferences();
			dbMsg += "MyPreferencesy読込み";
			myPreferences.readPrif(this);
			dbMsg += "完了";
			sharedPref = MyPreferences.sharedPref;
			myEditor =myPreferences.myEditor;

			pref_compBunki = myPreferences.pref_compBunki;			//コンピレーション設定[%]
			pref_list_simple =myPreferences.pref_list_simple;				//シンプルなリスト表示（サムネールなど省略）
			pref_lockscreen =myPreferences.pref_lockscreen;				//ロックスクリーンプレイヤー</string>
			pref_notifplayer =myPreferences.pref_notifplayer;				//ノティフィケーションプレイヤー</string>
			pref_cyakusinn_fukki=myPreferences.pref_cyakusinn_fukki;		//終話後に自動再生
			pref_bt_renkei =myPreferences.pref_bt_renkei;				//Bluetoothの接続に連携して一時停止/再開
//			play_order = Integer.parseInt(myPreferences.play_order);
			pref_commmn_music = myPreferences.pref_commmn_music;
			all_songs_file_name = pref_commmn_music + File.separator + cContext.getString(R.string.all_songs_file_name) + ".m3u8";
			dbMsg += ",全曲リストの汎用ファイル" + all_songs_file_name;album_artist_file_name = cContext.getString(R.string.album_artist_file_name);
			pref_data_url = myPreferences.pref_data_url;

			album_artist_file_name = pref_commmn_music + File.separator + cContext.getString(R.string.album_artist_file_name);
			dbMsg += ",アーティスト名の汎用ファイル" + album_artist_file_name;

					myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * Urlからアーティストを取得する
	 * **/
	public String getUrl2Artist(Cursor cursor) {
		final String TAG = "getUrl2Artist";
		String dbMsg = "[ZenkyokuList]";
		String retStr = "";
		try {
			String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
			String dataStr = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
			dataStr = dataStr.replace(pref_commmn_music + File.separator, "");
			dbMsg += ":" + dataStr ;
//			String extention = "";
//			int point = dataStr.lastIndexOf(".");
//			if (point != -1) {
//				extention = dataStr.substring(point + 1);
//			}
//			dbMsg += ",extention=" + extention ;
//			String titol = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
//			dbMsg += ",titol=" + titol ;
//			dataStr = dataStr.replace(titol , "");
//			dbMsg += ">>" + dataStr ;
//			String albumMei = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
//			dbMsg += ",albumMei=" + albumMei ;
//			dataStr = dataStr.replace(File.separator + albumMei, "");
//			dbMsg += ">>" + dataStr ;
			String[] datas = dataStr.split(File.separator );
			retStr = datas[0];
			retStr = retStr.replace("_","/");			//ファイルシステムに津川えなくて置き換えられている文字を戻す
			retStr = retStr.replace("%",".");
			if(! retStr.equals(artist) &&
					! retStr.equals("Compilations")){
				dbMsg += ":::artist=" + artist ;

				dbMsg += ">>" + retStr ;
//				myLog(TAG, dbMsg);
			}

		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return retStr;
	}

	public String setAlbumArtist(Cursor cursor) {
		final String TAG = "setAlbumArtist";
		String dbMsg = "[ZenkyokuList]";
		String retStr = "";
		try {
			String artistN = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
			String motoName = artistN;

//			String dataStr = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
//			dataStr = dataStr.replace(pref_commmn_music + File.separator, "");
//			dbMsg += ":" + dataStr ;
			String aArtintName = getUrl2Artist(cursor);
			dbMsg += ">>" + aArtintName ;
			String composer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER));
			dbMsg += ",composer=" + composer ;
			if( artistN == null ){
				if( composer != null ){
					artistN = composer;
				} else{
					artistN = aArtintName;
//					artistN = getApplicationContext().getResources().getString(R.string.comon_nuKnow_artist);
				}
			} else {
				String comp10 = getResources().getString(R.string.comon_compilation);			//コンピレーション
				String comp11 = comp10.toUpperCase();											//大文字化
				String comp12 = comp10.toLowerCase();											//小文字化
				String comp20 = getResources().getString(R.string.comon_compilation0);	//さまざまなアーティスト
				String comp21 = comp20.toUpperCase();											//大文字化
				String comp22 = comp20.toLowerCase();											//小文字化
				String comp30 = getResources().getString(R.string.comon_compilation2);	//Various Artists
				String comp31 = comp30.toUpperCase();											//大文字化
				String comp32 = comp30.toLowerCase();											//小文字化
				if(artistN.equals(comp10) || artistN.equals(comp11) || artistN.equals(comp12) ||
						artistN.equals(comp20) || artistN.equals(comp21) || artistN.equals(comp22) ||
						artistN.equals(comp30) || artistN.equals(comp31) || artistN.equals(comp32) ){
					artistN = comp10;
				}else{
					for(String Junl : genleList){
						if(aArtintName.equals(Junl)){
							artistN = Junl;
						}
					}
				}
			}

			if(retStr.equals("")){
				retStr = aArtintName;
			}
			if(! motoName.equals(artistN)
//					&& ! retStr.equals("Compilations")
			){
				retStr = artistN;
				dbMsg += ":::artist=" + artistN ;
				dbMsg += ">>" + retStr ;
//				myLog(TAG, dbMsg);
			}

		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return retStr;
	}


	//設定読込・旧バージョン設定の消去

	/**
 * 起動時に２系統プログレスのxlm読み込み
 * @Bundle savedInstanceState
 * onWindowFocusChangedを経てpreReadへ
 * */
	@Override
	public void onCreate(Bundle savedInstanceState) {	//WindowManagerの設定とアクティビティの読み込み
		super.onCreate(savedInstanceState);
		final String TAG = "onCreate";
		String dbMsg= "[ZenkyokuList]";
		try{
			startPart = System.currentTimeMillis();		// 開始時刻の取得
			ORGUT = new OrgUtil();				//自作関数集
			UTIL = new Util();
			musicPlaylist = new MusicPlaylist(ZenkyokuList.this);

			dbMsg+="cContext=" + this.cContext;/////////////////////////////////////
			if(this.cContext == null){
				this.cContext = ZenkyokuList.this;
			}
			dbMsg=">>" + this.cContext;/////////////////////////////////////
			Bundle extras = getIntent().getExtras();
			reqCode=extras.getInt("reqCode");		//何のリストか
			StartReqCode = reqCode;
			dbMsg= dbMsg +",reqCode="+reqCode;			//読み込み時の取得値
			if( reqCode == 0 ){
				reqCode = pt_start;
				dbMsg +=">> " +reqCode;/////////////////////////////////////
			}
			readPref();
			//		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.MATCH_PARENT);			//これを入れるとダイアログが最大化される。
			setContentView(R.layout.pd_log);
			pdg_scroll = findViewById(R.id.pdg_scroll);		//スクロール
			pgd_msg_tv = findViewById(R.id.pgd_msg_tv);
			progBar1 = findViewById(R.id.progBar1);		//メインプログレスバー
			pgd_val_tv = findViewById(R.id.pgd_val_tv);
			pgd_max_tv = findViewById(R.id.pgd_max);
			pgd_par_tv = findViewById(R.id.pgd_par_tv);
			dbMsg= dbMsg  + ",pgd_par_tv=" + pgd_par_tv.getId();
			_numberFormat = "%d/%d";
			_percentFormat = NumberFormat.getPercentInstance();
			_percentFormat.setMaximumFractionDigits(0);
			ProgBar2 = findViewById(R.id.ProgBar2);		 //セカンドプログレスバー
			pgd_val2_tv = findViewById(R.id.pgd_val2_tv);
			pgd_max2_tv = findViewById(R.id.pgd_max2);
			pgd_par2_tv = findViewById(R.id.pgd_par2_tv);
			dbMsg= dbMsg  + ",pgd_par2_tv=" + pgd_par2_tv.getId();
			pgd_finsh_bt = findViewById(R.id.pgd_finsh_bt);		//終了ボタン
			pgdBar1_ll = findViewById(R.id.pgdBar1_ll);		//メインプログレスバーエリア
			pgdBar2_ll = findViewById(R.id.pgdBar2_ll);		//セカンドプログレスバーエリア

			_percentFormat.setMaximumFractionDigits(0);
			artistTName = cContext.getString(R.string.artist_table);			//artist_table
			shigot_bangou = reqCode;
			dbMsg +=",reqCode=" + reqCode ;/////////////////////////////////////
			String comp1 = cContext.getString(R.string.artist_tuika01);				//コンピレーション
			String comp2 = cContext.getString(R.string.artist_tuika02);				//サウンドトラック
			String comp3  = cContext.getString(R.string.artist_tuika03);				//クラシック
			String comp4  = cContext.getString(R.string.comon_nuKnow_artist);		//アーティスト情報なし
			compList = new String[]{ comp1 , comp2 , comp3 , comp4};
			comCount = compList.length;
			compSelection = "SORT_NAME <> ? AND SORT_NAME <> ? AND SORT_NAME <> ? AND SORT_NAME <> ?";			//+ comp ;		//MediaStore.Audio.Media.ARTIST +" <> " + comp;			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
		//	myLog(TAG,dbMsg);
			switch(reqCode  ) {			//
			case MaraSonActivity.syoki_Yomikomi:				//128
//			case MuList.reTrySart:										//207
				pdTitol = this.cContext.getString(R.string.listmei_zemkyoku) +  this.cContext.getString(R.string.comon_sakuseicyuu);			//全曲リスト作成中
				pgd_finsh_bt.setVisibility( View.GONE );							//終了ボタンを非表示にしてスペースを詰める
				break;
			default:
				break;
			}
			setTitle(pdTitol);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}
//https://sites.google.com/site/shareandroid/ad/dg/framework/ui/style/applying
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {	 // ヘッドのイメージは実際にローディンされた時点で設定表示と同時にウィジェットの高さや幅を取得したいときは大抵ここで取る。
			if (hasFocus) {
				final String TAG = "onWindowFocusChanged[ZenkyokuList]";
				String dbMsg= "開始;";/////////////////////////////////////
				try{
					dbMsg= "shigot_bangou;" + shigot_bangou;/////////////////////////////////////
		//			myLog(TAG,dbMsg);
					switch(shigot_bangou) {
					case MaraSonActivity.syoki_Yomikomi:
						preRead( );			//dataURIを読み込みながら欠けデータ確認
						break;
					default:
						break;
					}
					shigot_bangou = 0;
				}catch (Exception e) {
					myErrorLog(TAG,dbMsg + "で"+e.toString());
				}
		 }
		 super.onWindowFocusChanged(hasFocus);
	 }

	@Override
	public void onResume(){
		super.onResume();
		final String TAG = "onResume[ZenkyokuList]";							//long seleID  ,, int hennkou, String seleItem
		String dbMsg= "プログレスダイアログの表示開始;";/////////////////////////////////////
		try{
			if(musicPlaylist == null){
				musicPlaylist = new MusicPlaylist(ZenkyokuList.this);
			}

//			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	@Override
	public void onPause() {
	super.onPause();
		final String TAG = "onPause[ZenkyokuList]";							//long seleID  ,, int hennkou, String seleItem
		String dbMsg= "プログレスダイアログの表示終了;";/////////////////////////////////////
		try{
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	/**
	 * MediaStore.Audio.Media.EXTERNAL_CONTENT_URIで端末内の音楽データ読み込み
     *  MediaStore.Audio.Media.IS_MUSIC  <> "0"
	 * c_orderBy = MediaStore.Audio.Media.DATA でアーティスフォルダの降順
	 * @ 無し
	 * preReadBodyへ
	 * reqCode = pt_start
     *
	 * */
	public void preRead( ) {			//dataURIを読み込みながら欠けデータ確認	, int reqCode
		final String TAG = "preRead";			//, AlertDialog pDialog
		String dbMsg= "[ZenkyokuList]";
		try{
			startPart = System.currentTimeMillis();		// 開始時刻の取得
			start = startPart;
			pgd_finsh_bt.setVisibility( View.GONE );							//終了ボタンを非表示にしてスペースを詰める
			progBar1.setVisibility( View.VISIBLE );										//メインプログレスバー
			pgdBar1_ll.setVisibility( View.VISIBLE );										//メインプログレスバーエリア
			ProgBar2.setVisibility( View.VISIBLE );										//セカンドプログレスバー
			pgdBar2_ll.setVisibility( View.VISIBLE );										//セカンドプログレスバーエリア
			ORGUT = new OrgUtil();				//自作関数集
			reqCode = pt_start;
			dbMsg=dbMsg +",reqCode="+ reqCode ;

			artistBk=0;
			albumBk=0;
			titolBk=0;
			trackBk=0;
			String saveDir = this.cContext.getExternalFilesDir(null).toString();						//メモリカードに保存フォルダ
			String[] rDir = saveDir.split(File.separator);
			dbMsg=dbMsg +"、rDir="+ rDir;
			exDrive = rDir[0] + File.separator+ rDir[1] + File.separator+ rDir[2];
			saveDir = this.cContext.getFilesDir().toString();						//内蔵メモリ
			rDir = saveDir.split(File.separator);
			inDrive = rDir[0] + File.separator+ rDir[1] + File.separator+ rDir[2];
			dbMsg=dbMsg +"、(do前)内蔵メモリ="+ inDrive + ",メモリカード="+ exDrive;
//			sharedPref = this.cContext.getSharedPreferences( this.cContext.getResources().getString(R.string.pref_main_file) , this.cContext.MODE_PRIVATE);		//	getSharedPreferences(prefFname,MODE_PRIVATE);
			pNFVeditor = sharedPref.edit();
			Map<String, ?> keys = sharedPref.getAll();
			dbMsg=dbMsg +",keys="+ keys.size() +"件" ;
			saisinnbi = "0";
			if(keys.size() > 0 ){
				saisinnbi = String.valueOf(keys.get("pref_file_saisinn"));
				dbMsg= dbMsg +",最新=" + saisinnbi ;/////////////////////////////////////
				pref_compBunki = String.valueOf(keys.get("pref_compBunki"));			//コンピレーション分岐点 曲数
				dbMsg= dbMsg +",pref_compBunki=" + pref_compBunki ;/////////////////////////////////////
			}
			if(pref_compBunki == null ){
				pref_compBunki = "40";
			}else if(pref_compBunki.equals("null")){
				pref_compBunki = "40";
				pNFVeditor.putString( "pref_compBunki", pref_compBunki);
				//			myLog(TAG,dbMsg);
				pNFVeditor.commit();	// データの保存
			}
			musicPlaylist.deletPlayList(ZenkyokuList.this.getResources().getString(R.string.all_songs_file_name));
			pd2CoundtVal=0;
			pd2MaxVal = pt_end - pt_start;									//このクラスのステップ数
			dbMsg +="[" +pd2CoundtVal +"/"+ pd2MaxVal +"]";
			ProgBar2.setMax(pd2MaxVal);	 //セカンドプログレスバー
			dbMsg=dbMsg +",最新="+ saisinnbi ;
			ContentResolver resolver = this.cContext.getContentResolver();	//c.getContentResolver();
			Uri cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
			String c_selection =  MediaStore.Audio.Media.IS_MUSIC +" <> ? "  ;
			String[] c_selectionArgs= {"0"  };   			//, null , null , null
			String c_orderBy=MediaStore.Audio.Media.DATE_MODIFIED  + " DESC"; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
			cursor = resolver.query( cUri , null , null , null, c_orderBy);
			kyoku = cursor.getCount();									//redrowIDList.size();
			if(cursor.moveToFirst()){
				ZenkyokuList.this.saisinnbi =  cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));
				dbMsg += "/更新日="+ ZenkyokuList.this.saisinnbi;/////////////////////////////////////////////////////////////////////////////////////////////
				if( ZenkyokuList.this.saisinnbi == null ){
					ZenkyokuList.this.saisinnbi =  cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));
					dbMsg += ">追加日>"+ ZenkyokuList.this.saisinnbi;/////////////////////////////////////////////////////////////////////////////////////////////
				}
				String dataFPN = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
				dbMsg += ",dataFPN="+ dataFPN;/////////////////////////////////////////////////////////////////////////////////////////////
				String artistN = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
				dbMsg +="、MediaStore..ARTIST=" + artistN;/////////////////////////////////////////////////////////////////////////////////////////////
				if( dataFPN != null && artistN != null ){
					if( dataFPN.contains(artistN) ){
						String dirName = dataFPN.substring(0, dataFPN.indexOf(artistN));
						if(dirName.contains( "external" ) || dirName.contains( "mnt/sdcard" )){					//メモリカード		exDrive
							ZenkyokuList.this.exDir = dirName;				//外部メモリ + "\n"
							if(ZenkyokuList.this.exDir.length() < dirName.length() || dirName.contains("Music") || dirName.contains("MUSIC") || dirName.contains("music")){
								ZenkyokuList.this.exDir = dirName;				//外部メモリ + "\n"
							}else{
								ZenkyokuList.this.exDir = exDrive;				//外部メモリ + "\n"
							}
							dbMsg +=">>exDir="+ ZenkyokuList.this.exDir;/////////////////////////////////////////////////////////////////////////////////////////////
						}else{				//								dbMsg=dbMsg +"、内蔵メモリ="+ inDrive + ",="+ exDrive;
							ZenkyokuList.this.inDir = dirName;				//内蔵メモリ + "\n"
							if(ZenkyokuList.this.inDir.length() < dirName.length() || dirName.contains("Music") || dirName.contains("MUSIC") || dirName.contains("music")){
								ZenkyokuList.this.inDir = dirName;				//内蔵メモリ + "\n"
							}else{
								ZenkyokuList.this.inDir = inDrive;				//内蔵メモリ + "\n"
							}
							dbMsg +=">>inDir="+ ZenkyokuList.this.inDir;/////////////////////////////////////////////////////////////////////////////////////////////
						}
					}
				}
			}
			String[] c_columns = null;		 		//③引数columnsには、検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
			c_selection =  MediaStore.Audio.Media.IS_MUSIC +" <> ? AND " +
								MediaStore.Audio.Media.ARTIST  + " = ? OR " +
								MediaStore.Audio.Media.ALBUM  + " = ? OR " +			//ARTIST?
								MediaStore.Audio.Media.TRACK  + " = ? OR " +
								MediaStore.Audio.Media.TITLE  + " = ? " ;
			String[] c_selectionArgs2= {"0" , "" , "" , "" , "" };   			//, null , null , null
			c_orderBy = MediaStore.Audio.Media.DATA; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
			cursor = resolver.query( cUri , c_columns , c_selection , c_selectionArgs2, c_orderBy);
			int keturaku = cursor.getCount();									//redrowIDList.size();
			dbMsg=dbMsg +";欠落="+ keturaku + "件";
			pdMessage = this.cContext.getString(R.string.medst_kakunin)+keturaku +"/" + kyoku + this.cContext.getString(R.string.pp_kyoku);			//"メディアストアの欠落データn/N name="">曲</string>;
			this.pdCoundtVal=0;		//ProgressDialog表示値
			if(cursor.moveToFirst()){
				pdMessage = pdMessage+this.cContext.getString(R.string.common_kakunincyuu);			//"メディアストアの欠落データ確認中";
				pdMessage_stok = pdMessage;
				pdMaxVal=cursor.getCount();		//ProgressDialog表示値
				dbMsg +="]Titol" + pdTitol + ",Msg=" + pdMessage + ",Max=" + pdMaxVal;/////////////////////////////////////
				pgd_msg_tv.setText(pdMessage_stok);
				pgd_msg_tv.scrollTo(0, pgd_msg_tv.getBottom());
		//		pdg_scroll.fullScroll(ScrollView.FOCUS_DOWN);
				String where = MediaStore.Audio.Media._ID + "= ?";
//				audio_id = Integer.valueOf(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));

				pTask = (plogTask) new plogTask(this ,  this).execute(reqCode,  pdMessage , cursor ,null , cUri , where );		//,jikkouStep,totalStep,calumnInfo
			}else{
				c_selection =  MediaStore.Audio.Media.IS_MUSIC +" <> ? AND " +
						MediaStore.Audio.Media.ARTIST  + " LIKE ? OR " +			//ARTIST?
						MediaStore.Audio.Media.ALBUM  + " LIKE ? OR " +
						MediaStore.Audio.Media.TRACK  + " LIKE ? " ;
				String[] c_selectionArgs3= {"0" , "%" +"unknow" + "%" , "%" +"unknow" + "%" , "%" +"unknow" + "%" };   			//, null , null , null
				cursor = resolver.query( cUri , c_columns , c_selection , c_selectionArgs3, c_orderBy);
				keturaku = cursor.getCount();									//redrowIDList.size();
				dbMsg=dbMsg +";unknow="+ keturaku + "件";
				pdMessage = this.cContext.getString(R.string.medst_kakunin)+keturaku +"/" + kyoku + this.cContext.getString(R.string.pp_kyoku);			//"メディアストアの欠落データn/N name="">曲</string>;
				this.pdCoundtVal=0;		//ProgressDialog表示値
				if(cursor.moveToFirst()){
					pdMessage = pdMessage+this.cContext.getString(R.string.common_kakunincyuu);			//"メディアストアの欠落データ確認中";
					pdMessage_stok = pdMessage;
					pdMaxVal=cursor.getCount();		//ProgressDialog表示値
					dbMsg +="]Titol" + pdTitol + ",Msg=" + pdMessage + ",Max=" + pdMaxVal;/////////////////////////////////////
					pgd_msg_tv.setText(pdMessage_stok);
					pgd_msg_tv.scrollTo(0, pgd_msg_tv.getBottom());
			//		pdg_scroll.fullScroll(ScrollView.FOCUS_DOWN);
					String where = MediaStore.Audio.Media._ID + "= ?";
					pTask = (plogTask) new plogTask(this ,  this).execute(reqCode,  pdMessage , cursor ,null , cUri , where );		//,jikkouStep,totalStep,calumnInfo
				}else{
					preReadEnd(cursor);			//データ確認欠け確認結果
					cursor = resolver.query( cUri , null , null , null, c_orderBy);
			//		kyoku = cursor.getCount();									//redrowIDList.size();
				}
			}
			long partEnd=System.currentTimeMillis();		// 終了時刻の取得
			dbMsg=dbMsg +";"+ (int)((partEnd - startPart)) + "m秒で終了";
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}

	/**
	 * MediaStore.Audio.Mediaの欠けデータ確認
	 * @ Cursor cursor MediaStore.Audio.Media.EXTERNAL_CONTENT_URIで読み込んだ端末内の音楽データ
	 * @ Uri cUri
	 * @ String where
	 * 欠落したデータはファイル名から補完
	 * アーティスト名が欠落している場合はR.string.comon_nuKnow_artist(アーティスト情報なし)
	 * アルバム名が欠落している場合はR.string.comon_nuKnow_album(アルバム情報なし)
	 * kousinnbに更新日、exDir で外部メモリ、inDiに内蔵メモリを記録
	 * preReadEndへ
	 * */
	public void preReadBody(Cursor cursor , Uri cUri , String where) throws IOException {			//MediaStore.Audio.Mediaの欠けデータ確認
		final String TAG = "preReadBody";
		String dbMsg= "[ZenkyokuList]";
		int pdCoundt = cursor.getPosition()+1;
		dbMsg = pdCoundt +"/"+ cursor.getCount();
		try{
			String dataFPN = null;
			String val = null;
			Map<String, String> map = null;

			String ｒID = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
			dbMsg = dbMsg + "[_ID" + ｒID +"]" ;/////////////////////////////////////////////////////////////////////////////////////////////
			String[] selectionArgs = {ｒID};
			dataFPN = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
			dbMsg = dbMsg+ dataFPN;/////////////////////////////////////////////////////////////////////////////////////////////
			if(dataFPN != null){
				String[] rStrs = dataFPN.split(File.separator);
				dbMsg = dbMsg + "；" + rStrs.length + "階層";/////////////////////////////////////////////////////////////////////////////////////////////
				map = ORGUT.data2msick(dataFPN , getApplicationContext());			//URLからアーティスト～拡張子を分離させて返す
				boolean kakikae = false;
				String mPass = map.get( "mPass" );
				dbMsg = dbMsg + ",mPass=" + mPass;/////////////////////////////////////////////////////////////////////////////////////////////

				String artistN = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
				dbMsg = dbMsg +"、MediaStore..ARTIST=" + artistN;/////////////////////////////////////////////////////////////////////////////////////////////
				String hikaku = getApplicationContext().getResources().getString(R.string.comon_nuKnow_artist);
		//		dbMsg = dbMsg +" ,hikaku="+ hikaku;/////////////////////////////////////////////////////////////////////////////////////////////
				val = map.get( "cArtistName" ); // 指定したキーに対応する値を取得. キャスト不要
				dbMsg = dbMsg +" ,map="+ val;/////////////////////////////////////////////////////////////////////////////////////////////
				if( artistN == null){
					kakikae = true ;
				}else if( artistN.equals("") || artistN.contains("unknown") || artistN.contains("Unknown") || artistN.contains("UNKNOW")){
					kakikae = true ;
				}else if(-1 < mPass.indexOf(artistN)){
					dbMsg = dbMsg +"、アーティスト名と関連無いフォルダ";/////////////////////////////////////////////////////////////////////////////////////////////
					if( val.equals(hikaku) ){
						artistN = val;
						kakikae = true ;
						dbMsg = dbMsg + " ,artistN= " + artistN;/////////////////////////////////////////////////////////////////////////////////////////////
					}
				}else if(artistN.equals(hikaku)){
					if(! val.equals(hikaku)){
						artistN = val;
						kakikae = true ;
						dbMsg = dbMsg + " ,artistN= " + artistN;/////////////////////////////////////////////////////////////////////////////////////////////
					}
				}
				if(kakikae){
					artistBk ++;
					if( val != null){
						artistN = val;	//アーティスト名などを読み取る
					}else{
						artistN = hikaku;	//アーティスト情報なし
					}
					dbMsg = dbMsg + ">>" + artistN;/////////////////////////////////////////////////////////////////////////////////////////////
					ContentValues cv = new ContentValues();
					cv.put(MediaStore.Audio.Media.ARTIST, artistN);
					int rows = cContext.getContentResolver().update(cUri, cv , where, selectionArgs);
					dbMsg = dbMsg + "処理" + rows +"件";/////////////////////////////////////////////////////////////////////////////////////////////
				}

				kakikae = false;
				String albumT = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
				dbMsg = dbMsg + " ,MediaStore...ALBUM= " + albumT;/////////////////////////////////////////////////////////////////////////////////////////////
				val = map.get( "Alnbum" );		//選択中アルバム名
				dbMsg = dbMsg +" ,map="+ val;/////////////////////////////////////////////////////////////////////////////////////////////
				hikaku = getApplicationContext().getResources().getString(R.string.comon_nuKnow_album);
		//		dbMsg = dbMsg +" ,hikaku="+ hikaku;/////////////////////////////////////////////////////////////////////////////////////////////
				if( albumT == null){
					kakikae = true ;
				}else if( albumT.equals("") || albumT.equals("<unknown>")){
					kakikae = true ;
				}else if( -1 < mPass.indexOf(albumT)){
					dbMsg = dbMsg +"、アルバム名と関連無いフォルダ";/////////////////////////////////////////////////////////////////////////////////////////////
					if( val.equals(hikaku) ){
						albumT = val;
						kakikae = true ;
						dbMsg = dbMsg + " ,albumT= " + albumT;/////////////////////////////////////////////////////////////////////////////////////////////
					}
				}else if(albumT.equals(hikaku)){
					if(! val.equals(hikaku)){
						albumT = val;
						kakikae = true ;
						dbMsg = dbMsg + " ,artistN= " + artistN;/////////////////////////////////////////////////////////////////////////////////////////////
					}
				}
				if(kakikae){
					albumBk ++;
					if(albumT != null){
						albumT = val;	//アーティスト名などを読み取る
						dbMsg +=",アルバム名= " + albumT;
					}else{
						albumT = hikaku;	//アーティスト情報なし
					}
					dbMsg = dbMsg + ">>" + albumT;/////////////////////////////////////////////////////////////////////////////////////////////
					ContentValues cv = new ContentValues();
					cv.put(MediaStore.Audio.Media.ALBUM, albumT);
					int rows = cContext.getContentResolver().update(cUri, cv , where, selectionArgs);
					dbMsg = dbMsg + "処理" + rows +"件";/////////////////////////////////////////////////////////////////////////////////////////////
					myLog(TAG,dbMsg);
				}
				kakikae = false;
				String rStr = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK));
				rStr = UTIL.checKTrack( rStr);
				dbMsg = dbMsg + " ,[MediaStoreで " + rStr ;
				String trackNo = map.get( "trackNo" );
				dbMsg = dbMsg + ",MediaStoreから" + rStr + "/ファイル名から" + trackNo;
				if( trackNo != null ){
					if (trackNo.contains("/")){
						String[] tStrs = trackNo.split("/");
						trackNo = tStrs[0];
					}
					if( ! trackNo.equals(rStr) ){				//if(kakikae && trackNo != null ){
						dbMsg = dbMsg + ">>" + trackNo;/////////////////////////////////////////////////////////////////////////////////////////////
						ContentValues cv = new ContentValues();
						cv.put(MediaStore.Audio.Media.TRACK, trackNo);
						int rows = cContext.getContentResolver().update(cUri, cv , where, selectionArgs);
						dbMsg = dbMsg + "処理" + rows +"件";/////////////////////////////////////////////////////////////////////////////////////////////
//									myLog(TAG,dbMsg);
						kakikae = true ;
					}
				}
				kakikae = false;
				String titleT = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
				dbMsg = dbMsg + "]MediaStoreから ; " + titleT;/////////////////////////////////////////////////////////////////////////////////////////////
				if( titleT == null){
					kakikae = true ;
				}else kakikae = titleT.equals("") || titleT.equals("<unknown>");
				if(kakikae){
					titolBk ++;
					val = map.get( "titolName" );
					if(val != null){
						titleT = val;		//曲名
					}
					dbMsg = dbMsg + ">>" + titleT;/////////////////////////////////////////////////////////////////////////////////////////////
					ContentValues cv = new ContentValues();
					cv.put(MediaStore.Audio.Media.TITLE, titleT);
					int rows = cContext.getContentResolver().update(cUri, cv , where, selectionArgs);
					dbMsg = dbMsg + "処理" + rows +"件";/////////////////////////////////////////////////////////////////////////////////////////////
				}
			}
//			String kousinnbi = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));	//The time the file was last modified
//			dbMsg = dbMsg +"更新日="+ kousinnbi;/////////////////////////////////////////////////////////////////////////////////////////////
//			if( kousinnbi == null ){
//				kousinnbi =  cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));
//				dbMsg += ">追加日>"+ kousinnbi;/////////////////////////////////////////////////////////////////////////////////////////////
//			}
//			
//			if( ZenkyokuList.this.saisinnbi == null){
//				ZenkyokuList.this.saisinnbi = kousinnbi;
//			}else if( ZenkyokuList.this.saisinnbi.equals("null")){
//				ZenkyokuList.this.saisinnbi = kousinnbi;
//			}
//			dbMsg = dbMsg + "/更新日="+ ZenkyokuList.this.saisinnbi;/////////////////////////////////////////////////////////////////////////////////////////////
//			if(Integer.valueOf(ZenkyokuList.this.saisinnbi) < Integer.valueOf(kousinnbi)){
//				ZenkyokuList.this.saisinnbi = kousinnbi;
//			}else if(Integer.valueOf(ZenkyokuList.this.saisinnbi) == 0){
//				ZenkyokuList.this.saisinnbi = kousinnbi;
//			}
//			dbMsg = dbMsg +">>"+ ZenkyokuList.this.saisinnbi;/////////////////////////////////////////////////////////////////////////////////////////////
//			String dirName = "";
//			val = map.get( "mPass" );
//			if(val != null){
//				dirName = val;
//			}
//			dbMsg = dbMsg +" , dirName="+ dirName;	// + ";;" + ORGUT.isInListString(mDir, dirName);//////////////////////////////////////////
//			if(dirName.contains( "external" ) || dirName.contains( "mnt/sdcard" )){					//メモリカード		exDrive
//				ZenkyokuList.this.exDir = dirName;				//外部メモリ + "\n"
//				if(ZenkyokuList.this.exDir.length() < dirName.length() || dirName.contains("Music") || dirName.contains("MUSIC") || dirName.contains("music")){
//					ZenkyokuList.this.exDir = dirName;				//外部メモリ + "\n"
//				}else{
//					ZenkyokuList.this.exDir = exDrive;				//外部メモリ + "\n"
//				}
//				dbMsg = dbMsg +">>exDir="+ ZenkyokuList.this.exDir;/////////////////////////////////////////////////////////////////////////////////////////////
//			}else{				//								dbMsg=dbMsg +"、内蔵メモリ="+ inDrive + ",="+ exDrive;
//				ZenkyokuList.this.inDir = dirName;				//内蔵メモリ + "\n"
//				if(ZenkyokuList.this.inDir.length() < dirName.length() || dirName.contains("Music") || dirName.contains("MUSIC") || dirName.contains("music")){
//					ZenkyokuList.this.inDir = dirName;				//内蔵メモリ + "\n"
//				}else{
//					ZenkyokuList.this.inDir = inDrive;				//内蔵メモリ + "\n"
//				}
//				dbMsg = dbMsg +">>inDir="+ ZenkyokuList.this.inDir;/////////////////////////////////////////////////////////////////////////////////////////////
//			}
//			myLog(TAG,dbMsg);
		}catch(IllegalArgumentException e){
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	//	return cursor;
	}

	/**
	 * データ確認欠け確認結果
	 * @ Cursor cursor クローズ
	 * pref_file_kyoku=総曲数,pref_file_saisinn=最新更新日,pref_file_ex=メモリーカード,pref_file_in=内蔵メモリをプリファレンスに書き込む
	 * kaliAartistListへ
	 * */
	public void preReadEnd(Cursor cursor) {			//データ確認欠け確認結果
		final String TAG = "preReadEnd";
		String dbMsg= "[ZenkyokuList]";
		try{
			pNFVeditor = sharedPref.edit();
			dbMsg=kyoku+"曲";
			pNFVeditor.putString( "pref_file_kyoku", String.valueOf(kyoku));		//総曲数
			dbMsg +="、更新日="+ ZenkyokuList.this.saisinnbi;
			pNFVeditor.putString( "pref_file_saisinn", ZenkyokuList.this.saisinnbi);					//最新更新日
			dbMsg= dbMsg +",メモリーカード="+ZenkyokuList.this.exDir;
			if(! ZenkyokuList.this.exDir.equals("")){
				pNFVeditor.putString( "pref_file_ex", File.separator + ZenkyokuList.this.exDir);								//メモリーカード
			}
			dbMsg= dbMsg +",内蔵メモリ="+ ZenkyokuList.this.inDir;		//+"（合計；"+mDir.size();
			if(! ZenkyokuList.this.inDir.equals("")){
				pNFVeditor.putString( "pref_file_in", File.separator + ZenkyokuList.this.inDir);								//内蔵メモリ
			}
			String file_wr;
			if(Integer.parseInt(Build.VERSION.SDK) < 19){								//kitcut以前なら
				file_wr = File.separator + exDir + this.cContext.getString(R.string.app_name) +File.separator ;
			}else{
				file_wr = File.separator + this.cContext.getFilesDir().toString();
			}
			dbMsg= dbMsg +",設定保存フォルダ?"+file_wr;
			pNFVeditor.putString( "pref_file_wr", file_wr);
			boolean kakikomi = pNFVeditor.commit();	// データの保存
			dbMsg= dbMsg+ "、書き込み" + kakikomi;

			int nextCount = cursor.getCount();			//3.次のステップ数
			cursor.close();
			pd2CoundtVal = ProgBar2.getProgress();
			pdMessage_stok = pd2CoundtVal + ";" + this.cContext.getString(R.string.medst_kakunin)+ this.cContext.getString(R.string.comon_kakuninn)
					+ nextCount + this.cContext.getString(R.string.comon_kencyuu);			//"メディアストアの欠落データ確認";;
			long end=System.currentTimeMillis();						// 終了時刻の取得
			String dousaJikann = ORGUT.sdf_mss.format( (int)((end - startPart)));
			dbMsg= dbMsg +",artistBk="+albumBk +",trackBk="+trackBk +",titolBk="+titolBk;
//			if( artistBk > 0 || albumBk > 0 || trackBk > 0 || titolBk > 0){		//欠けたフィールドが有れば
//				pdMessage_stok = pdMessage_stok + ">>"+ artistBk  + this.cContext.getString(R.string.comon_nin)+"/"+
//							albumBk + this.cContext.getString(R.string.pp_mai)+"/"+ trackBk + this.cContext.getString(R.string.pp_kyoku)+"/"+
//							titolBk + this.cContext.getString(R.string.comon_ken) + this.cContext.getString(R.string.comon_keturaku);		//件欠落
//				pdMes = pdMessage_stok +"["+dousaJikann + "mS]";		//	<string name="">所要時間</string>
//	//			myLog(TAG,dbMsg);
//				mastKopusin( (ContextWrapper) this.cContext); 				//メディアストア更新
//			}else{
				pdMessage_stok = pdMessage_stok + ">>"+ this.cContext.getString(R.string.comon_keturaku_nasi);		//	<string name="">欠落無し</string>
				pdMessage_stok = pdMessage_stok +"["+ dousaJikann + "mS]";		//	<string name="">所要時間</string>
				pd2CoundtVal++;
				pdMessage_stok = pdMessage_stok + "\n\n" + pd2CoundtVal + ";" +
							this.cContext.getString(R.string.medst_syuusei)+ "[" + this.cContext.getString(R.string.common_syouryaku)+ "]";	//省略
				pdMes = pdMessage_stok +"["+dousaJikann + "mS]";		//	<string name="">所要時間</string>
				kaliAartistList();				//アルバムアーティストリストアップ
	//		}
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}

/**
 * preReadで欠けが見つかった場合のメディアストア更新
 * preReadEndから呼ばれていたが現在未使用
 * */
	protected void mastKopusin(ContextWrapper context) {				//メディアストア更新  ←preReadで欠けが見つかった場合のみ
		final String TAG = "mastKopusin";
		String dbMsg= "[ZenkyokuList]";
		try{
			startPart = System.currentTimeMillis();		// 開始時刻の取得
			ORGUT = new OrgUtil();		//自作関数集
			dbMsg +=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			reqCode =  pt_mastKopusin;			//802;メディアストア更新  ←preReadで欠けが見つかった場合のみ
			start = System.currentTimeMillis();		// 開始時刻の取得
			cContext.getString(R.string.medst_start);
			creditArtistList = new ArrayList<String>();		//クレジットされているアーティスト名
			albumList = new ArrayList<String>();		//アルバム名
			titolList = new ArrayList<String>();		//曲名
			trackNoList = new ArrayList<String>();		//曲順
			String where = MediaStore.Audio.Media._ID + "= ?";
			ContentResolver resolver = context.getContentResolver();	//this.getApplication().
			Uri cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
			String[] c_columns = null;		 		//③引数columnsには、検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
			String c_selection =  MediaStore.Audio.Media.IS_MUSIC +" <> ? ";			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
			String[] c_selectionArgs= {"0"};   			//⑥引数groupByには、groupBy句を指定します。
			String c_orderBy=MediaStore.Audio.Media.DATA; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
			cursor = resolver.query( cUri , c_columns , c_selection , c_selectionArgs, c_orderBy);
			dbMsg=dbMsg +";"+ cursor.getCount() + "件×"+ cursor.getColumnCount() + "項目";
			if(cursor.moveToFirst()){
				pd2CoundtVal++;
				dbMsg="ループ前" + pd2CoundtVal +"/"+ pd2MaxVal ;
				pdTitol = cContext.getString(R.string.medst_start);			//データ確認中
				pdMessage = pdMessage_stok + "\n" +cContext.getString(R.string.medst_syuusei);		// メディアストアの欠落データ修正中
				pdMaxVal=cursor.getCount();		//ProgressDialog表示値
				dbMsg +="Titol" + pdTitol + ",Msg=" + pdMessage + ",Max=" + pdMaxVal;/////////////////////////////////////
				myLog(TAG,dbMsg);
	//			redrowProg ( pdMaxVal);			//progBar1の最大値と初期化
				pTask = (plogTask) new plogTask(this ,  this).execute(reqCode,  pdMessage , cursor , null, cUri , where );		//,jikkouStep,totalStep,calumnInfo
			}
		}catch(IllegalArgumentException e){
			myErrorLog(TAG,dbMsg  +"で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}

	protected int mastKopusinBody(Context context , Cursor cursor , Uri uri , String where ) {				//メディアストア更新のレコード処理
		final String TAG = "mastKopusinBody";
		pdCoundtVal=cursor.getPosition()+1;		//プログレスカウンタ
		String dbMsg= "[ZenkyokuList]";
		dbMsg += pdCoundtVal +"/"+ pdMaxVal + ";" ;/////+ cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC))///////////////////////////
		try{
			String trackNo = null;			//曲順
			String titolName =null;		//曲名
			dbMsg = cursor.getPosition() +"/"+ cursor.getCount() + "曲[";/////////////////////////////////////////////////////////////////////////////////////////////
			String ｒID = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
			dbMsg = dbMsg+ ｒID + "]";/////////////////////////////////////////////////////////////////////////////////////////////
			String dataFPN = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));				//The data stream for the file ;Type: DATA STREAM
			dbMsg = dbMsg +" , "+ dataFPN;/////////////////////////////////////////////////////////////////////////////////////////////
			String val;
			Map<String, String> map = ORGUT.data2msick(dataFPN, getApplicationContext());
			String creditArtistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));	//クレジットされているアーティスト名
			dbMsg = dbMsg +"アーティスト="+ creditArtistName;/////////////////////////////////////////////////////////////////////////////////////////////
			boolean ololae = false;
			if(creditArtistName == null){
				ololae = true;
			}else if(creditArtistName.equals("<unknown>") || creditArtistName.equals("")){
				ololae = true;
			}
			if(ololae){
				val = map.get( "cArtistName" ); // 指定したキーに対応する値を取得. キャスト不要
				if( val != null){
					creditArtistName = val;
					if(creditArtistName.length() < val.length()){
						creditArtistName = val;
					}
				}
				dbMsg = dbMsg + ">>" + creditArtistName;/////////////////////////////////////////////////////////////////////////////////////////////
				ContentValues cv = new ContentValues();
					cv.put(MediaStore.Audio.Media.ARTIST, creditArtistName);
				String[] selectionArgs = {ｒID};
				int rows = context.getContentResolver().update(uri, cv , where, selectionArgs);
				creditArtistList.add(creditArtistName);
				dbMsg = dbMsg + "処理" + rows +"件";/////////////////////////////////////////////////////////////////////////////////////////////
			}

			String albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));		//アルバム名
			dbMsg = dbMsg +" ,アルバム="+ albumName;/////////////////////////////////////////////////////////////////////////////////////////////
			if(albumName == null || albumName.equals("<unknown>")){
				ololae = true;
			}else ololae = albumName.equals("<unknown>") || albumName.equals("");
			if(ololae){
				//		String[] rStr = dataFPN.split(File.separator);
				val = map.get( "Alnbum" );
				if(val != null){
					if(albumName == null){		//アルバム名
						albumName = val;
					}
				}
				dbMsg= dbMsg + ",アルバム名= " + albumName;
				dbMsg = dbMsg + ">>" + albumName;/////////////////////////////////////////////////////////////////////////////////////////////
				albumList.add(albumName);
				ContentValues cv = new ContentValues();
				cv.put(MediaStore.Audio.Media.ALBUM, albumName);
				String[] selectionArgs = {ｒID};
				int rows = context.getContentResolver().update(uri, cv , where, selectionArgs);
				dbMsg = dbMsg + "処理" + rows +"件";/////////////////////////////////////////////////////////////////////////////////////////////
//				myLog(TAG,dbMsg);
			}

			trackNo = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK));			//曲順
			dbMsg = dbMsg +" ,曲名="+ trackNo;/////////////////////////////////////////////////////////////////////////////////////////////
			if(trackNo == null || trackNo.equals("<unknown>")){
				val = map.get( "trackNo" );
				if(trackNo == null){
					if(val != null){
						trackNo = val;
					}
				}
			}
			trackNo = UTIL.checKTrack( trackNo);
			dbMsg=dbMsg + "[" + trackNo + "]";
			trackNoList.add(trackNo);
			ContentValues cv = new ContentValues();
			cv.put(MediaStore.Audio.Media.TRACK, trackNo);
			String[] selectionArgs = {ｒID};
			int rows = context.getContentResolver().update(uri, cv , where, selectionArgs);
			dbMsg = dbMsg + "処理" + rows +"件";/////////////////////////////////////////////////////////////////////////////////////////////

			titolName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
			dbMsg = dbMsg +" ,曲名="+ titolName;/////////////////////////////////////////////////////////////////////////////////////////////
			if(titolName == null || titolName.equals("<unknown>")){
				val = map.get( "titolName" );
				if(titolName == null){
					if(val != null){
						titolName = val;		//曲名
					}
				}
				titolList.add(titolName);
				cv = new ContentValues();
					cv.put(MediaStore.Audio.Media.TITLE, titolName);
				String[] selectionArgs5 = {ｒID};
				rows = context.getContentResolver().update(uri, cv , where, selectionArgs5);
				dbMsg = dbMsg + "処理" + rows +"件";/////////////////////////////////////////////////////////////////////////////////////////////
//				myLog(TAG,dbMsg);
			}
		}catch(IllegalArgumentException e){
			myErrorLog(TAG,dbMsg  +"(while)で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"(while)で"+e.toString());
		}
		return pdCoundtVal;
	}

	protected void mastKopusinEnd() {				//メディアストア更新の終了
		final String TAG = "mastKopusinEnd";
		String dbMsg= "[ZenkyokuList]";
		try{
			this.pdCoundtVal=cursor.getCount();		//プログレスカウンタ
			dbMsg="pdCoundtVal="+pdCoundtVal;
			cursor.close();		//android.database.StaleDataException: Attempted to access a cursor after it has been closed.が発生するのでonDestroy()に任せる
			dbMsg +=",creditArtist="+creditArtistList.size();
			String pdMes = cContext.getString(R.string.medST_kekkka) + ";" + creditArtistList.size() + "," + cContext.getString(R.string.medST_kekkka);
			dbMsg +=",album="+albumList.size();
			pdMes = pdMes+"," + albumList.size() + cContext.getString(R.string.pp_mai) + titolList.size() + "," + cContext.getString(R.string.pp_kyoku);
			dbMsg +=",titol="+titolList.size();
			pdMes = pdMes+titolList.size() + "," + cContext.getString(R.string.pp_kyoku);
			dbMsg +=",trackNo="+trackNoList.size()+ cContext.getString(R.string.comon_ken);
			pdMes = pdMes+"/" + cursor.getCount() + cContext.getString(R.string.comon_ken);
			dbMsg=dbMsg +";"+pdMes+",到達"+cursor.getPosition();
	//		Toast.makeText(context, pdMes, Toast.LENGTH_SHORT).show();
			myLog(TAG,dbMsg);
			albumList.size();
		//	nextStep = MaraSonActivity.syoki_Yomi_Album_All;			//131MediaStore.Audio.Albumsの全レコード読み込み
	//		reqCode = pt_albumReadAll ;								//MediaStore.Audio.Albumsの全レコード読み込み
			pdMessage_stok = pdMessage_stok +"\n\n" + pd2CoundtVal + ";" +
					cContext.getString(R.string.medst_syuusei) + pdCoundtVal + cContext.getString(R.string.comon_ken);		// メディアストアの欠落データ修正
			long end=System.currentTimeMillis();		// 終了時刻の取得
			String dousaJikann = ORGUT.sdf_mss.format( (int)((end - startPart)));
			pdMes = pdMessage_stok +"["+cContext.getString(R.string.comon_syoyoujikan)+";"+ dousaJikann + "mS]";		//	<string name="">所要時間</string>
			myLog(TAG,dbMsg);
			kaliAartistList();				//アルバムアーティストリストアップ
	//		albumReadAll();					//MediaStore.Audio.Albumsの全レコード読み込み
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg);
		}
	}

	/**
	 * 仮アーティストリスト作成
	 * @ 無し
	 * MediaStore.Audio.Media.EXTERNAL_CONTENT_URIをMediaStore.Audio.Media.ARTISTでソート
	 * kaliAartistListBodyへ
	 * reqCode = pt_KaliArtistList
	 * */
	public void kaliAartistList(){				//803;仮アーティストリスト作成
		final String TAG = "kaliAartistList";
		String dbMsg= "[ZenkyokuList]";
		try{
			startPart = System.currentTimeMillis();		// 開始時刻の取得
			dbMsg=ORGUT.nowTime(true,true,true) + dbMsg;/////////////////////////////////////
			System.currentTimeMillis();

			cContext.getContentResolver();
			Uri cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
			String[] c_columns = null;		 		//③引数columnsには、検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
			String c_selection =  MediaStore.Audio.Media.IS_MUSIC +" <> ? ";	//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
			String[] c_selectionArgs= {"0"};   			//音楽と分類されるファイルだけを抽出する
//			String c_groupBy = MediaStore.Audio.Media.ALBUM;
//			String having =null;								//EXTERNAL_CONTENT_URIには使えない
			String c_orderBy= MediaStore.Audio.Media.DATA ; 				// + MediaStore.Audio.Media.YEAR  + " DESC , "	降順はDESC	☆"album_artist"は拾えない
			cursor = this.cContext.getContentResolver().query( cUri ,c_columns, c_selection, c_selectionArgs, c_orderBy) ;
			dbMsg=dbMsg +";"+ kyoku + "件×"+ cursor.getColumnCount() + "項目";
			if(cursor.moveToFirst()){
				aArtist = "";
				artistList = new ArrayList<Map<String, Object>>();
				compGenList = new ArrayList<String>();		//実際にあったコンピレーション化するジャンル
				shortArtistList = new ArrayList<String>();			//最短アーティスト名
				reqCode = pt_KaliArtistList;									//803;仮アーティストリスト作成
				pd2CoundtVal++;
				pdMessage = pdMessage_stok + "\n\n" + pd2CoundtVal + ";" +
						kyoku + this.cContext.getString(R.string.comon_ken)+ this.cContext.getString(R.string.comon_kara)+ ";" +			//件から
						this.cContext.getString(R.string.pp_artist)+ this.cContext.getString(R.string.comon_kakuninn);	//アーティスト確認
				dbMsg=reqCode + "ループ前" + pd2CoundtVal +"/"+ pd2MaxVal + ";" + pdMessage  ;
				pdCoundtVal = 0;
				pdMaxVal = cursor.getCount();
				if(kariArtist_db != null){
					if(kariArtist_db.isOpen()){
						kariArtist_db.close();
					}
				}
				String fn = this.cContext.getString(R.string.kari_artist_file);
				dbMsg += "db=" + fn;
				myLog(TAG,dbMsg);
				pTask = (plogTask) new plogTask(this ,  this).execute(reqCode,  pdMessage , cursor ,null , null , fn );
			}
		}catch(IllegalArgumentException e){
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}

	/**
	 * 仮アーティストリスト作成
	 * @ Cursor cursor MediaStore.Audio.Media.EXTERNAL_CONTENT_URIで読み込んだ端末内の音楽データ
	 * アーティスト名がcomon_compilation=コンピレーション,comon_compilation0=さまざまなアーティスト,comon_compilation2=Various Artistsはcomon_compilation=コンピレーションに統一
	 * アーティスト名を最短化して大文字化しソートキーになる文字列作成
	 * ArrayList<String> artistListに格納
	 * kaliAartistListEndへ
	 * */
	public Cursor kaliAartistListBody(Cursor cursor , SQLiteStatement stmt) throws IOException {			//803;仮アーティストリスト作成
		final String TAG = "kaliAartistListBody";
		String dbMsg= "[ZenkyokuList]";
		String dbMsg2= "";
		boolean kakikomi = false;
		try{
			pdCoundtVal = cursor.getPosition()+1;
			progBar1.setProgress(pdCoundtVal);
			dbMsg += "[" + pdCoundtVal +"/"+ progBar1.getMax() + "]";

			String motoName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
			dbMsg += motoName ;
			String artistN = setAlbumArtist(cursor);
			dbMsg += "）" + artistN ;
			String sort_name = ORGUT.ArtistPreFix(artistN.toUpperCase());	//大文字化してアーティスト名のTheを取る
			dbMsg += ",sort_name=" + sort_name ;
			boolean isNotComp = true;
			for(String Junl : genleList){
				if(artistN.equals(Junl)){
					isNotComp = false;
				}
			}

			int bCount = ZenkyokuList.this.artistList.size();

			ZenkyokuList.this.objMap = new HashMap<String, Object>();
			dbMsg += "<<aArtist=" + aArtist ;
			if(ZenkyokuList.this.artistList == null) {                //一人目
				dbMsg += ",artistList=null";
				shortArtistList = new ArrayList<String>();			//最短アーティスト名
				kakikomi = true;
			}else if(ZenkyokuList.this.artistList.size() == 0){				//一人目
				kakikomi = true;
			}else if(!isNotComp){				//コンピレーション
				kakikomi = false;
				String rStr = ORGUT.sarchiInListString( ZenkyokuList.this.compGenList , artistN) ;			//渡された文字が既にリストに登録されていれば該当する文字を返す
				if(rStr == null){
					compGenList.add(artistN);
				}
			}else if(! artistN.equals(aArtist) && isNotComp){					//名前が変わったら
				String rStr = ORGUT.sarchiInListString( ZenkyokuList.this.shortArtistList , sort_name) ;			//渡された文字が既にリストに登録されていれば該当する文字を返す
				dbMsg += ">sarchiInListString>" + rStr ;
				int rIndex = ORGUT.mapEqualIndex(artistList , "credit_artist" ,artistN);//渡された文字を含む名前が既にリストに登録されていればインデックスを返す
				dbMsg += ",rIndex=" + rIndex ;
				if( rStr == null ){
					kakikomi = true;
				} else if(rIndex < 0){			//未登録なら書き込む
//					kakikomi = true;
//					int rIndex2 = ORGUT.mapIndex(artistList , "sort_name" ,sort_name);//渡された文字を含む名前が既にリストに登録されていればインデックスを返す
//					dbMsg += ",rIndex=" + rIndex ;
//					if( -1 < rIndex2 ){			//渡された文字が既にリストに登録されていればインデックスを返す
//						sort_name = String.valueOf(artistList.get(rIndex2).get("sort_name"));
//						dbMsg += ">>" + artistN ;
//						artistN = String.valueOf(artistList.get(rIndex2).get("album_artist"));
//						dbMsg += ">>" + artistN ;
//					}else{
//						kakikomi = true;
//					}
				}
			}
			aArtist = sort_name;

//			bArtistN = artistN;
			dbMsg += ">ALBUM_ARTIST>>" + artistN;
			dbMsg += ",書込み" + kakikomi;
			if( kakikomi ){
				aArtist = artistN;
				ZenkyokuList.this.objMap.put("sort_name" ,sort_name );						//リストアップ順にソートできる冠詞抜きの大文字変換名
				ZenkyokuList.this.objMap.put("album_artist" ,artistN );						//ゲストやグループなどを除いた最少名
				ZenkyokuList.this.objMap.put("album" ,albumMei );							//対象アルバム
				ZenkyokuList.this.objMap.put("credit_artist" ,motoName );					//ゲストやグループなどを含む元の名称
				ZenkyokuList.this.artistList.add( objMap);
				ZenkyokuList.this.shortArtistList.add(sort_name);		//The抜き大文字アーティスト名
				//dbへの書込み///////////////////////////
				String artistID = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
				stmt = stmtWrite2(artistID ,  stmt , 1);
				stmt = stmtWrite2(sort_name ,  stmt , 2);
				stmt = stmtWrite2(motoName ,  stmt , 3);							//1;ARTIST
				stmt = stmtWrite2(artistN ,  stmt , 4);				//2;ALBUM_ARTIST
				stmt = stmtWrite2(albumMei ,  stmt ,  5);

				String album_art = null;
				int first_year = 0;
				int last_year = 0;
				int rInt = 0;
				String rStr = null;			// = cursor.getString(cursor.getColumnIndex("ALBUM"));
				cContext.getContentResolver();
				Uri cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
				String[] c_columns = null;		 		//③引数columnsには、検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
				String c_selection =  MediaStore.Audio.Media.ARTIST + " LIKE ?";
				String[] c_selectionArgs= {motoName};   			//音楽と分類されるファイルだけを抽出する
				String c_orderBy = MediaStore.Audio.Media.YEAR;
				Cursor cursor_p2 = this.cContext.getContentResolver().query( cUri ,c_columns, c_selection, c_selectionArgs,c_orderBy) ;
				int maiSuu = cursor_p2.getCount();
				dbMsg += ":"+ maiSuu + "曲";
				if( cursor_p2.moveToFirst() ){
					dbMsg2 = "";
					String rAlbum = null;
					do{
						rStr = cursor_p2.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
						if(album_art == null ){
							rAlbum = rStr;
							dbMsg2 +=", ALBUM="+ rAlbum;
							cUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
//						String[] c_columns = null;		 		//③引数columnsには、検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
							c_selection =  MediaStore.Audio.Albums.ARTIST +" LIKE ?  AND " + MediaStore.Audio.Albums.ALBUM +" = ?";			//
							String[] c_selectionArgs2= { "%" + artistN + "%"  , rAlbum };   			//
							c_orderBy = MediaStore.Audio.Albums.FIRST_YEAR  ; 			//LAST_YEAR	降順はDESC
							Cursor cursor_3 = getContentResolver().query( cUri , c_columns , c_selection , c_selectionArgs2, c_orderBy);
							if( cursor_3.moveToFirst() ){
								if(album_art == null){
									album_art = cursor_3.getString(cursor_3.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
								}
								rStr = cursor_3.getString(cursor_3.getColumnIndex(MediaStore.Audio.Albums.FIRST_YEAR));
								dbMsg2 += ",FIRST_YEAR="+ rStr;
								if( rStr != null ){
									rInt  = Integer.parseInt(rStr);
									if(first_year == 0){
										first_year =rInt;
									}else if(rInt < first_year ){
										first_year =rInt;
									}
									dbMsg2 += ">>"+ first_year;
								}
								rStr = cursor_3.getString(cursor_3.getColumnIndex(MediaStore.Audio.Albums.LAST_YEAR));
								dbMsg2 += ",LAST_YEAR="+ rInt;
								if( rStr != null ){
									rInt  = Integer.parseInt(rStr);
									if(last_year < rInt){
										last_year = rInt;
									}else if(last_year < first_year){
										last_year = 0;
									}
									dbMsg2 += ">>"+ last_year;
								}
							}
							cursor_3.close();

						}
					}while( cursor_p2.moveToNext() );
				}				//if( cursor_p2.moveToFirst() ){
				cursor_p2.close();
				dbMsg += ", ALBUM_ART="+ album_art;
				stmt = stmtWrite2( album_art  , stmt , 6);
				String sub_text = "";
				if(first_year > 0){
					sub_text = String.valueOf(first_year);
					if(last_year> first_year){
						sub_text = sub_text +" . . . ";
					}else{
						sub_text = "";
					}
				}
				if(last_year > 0){
					sub_text = sub_text + last_year;
				}
				sub_text = sub_text  +"  ";
				sub_text = sub_text + maiSuu +this.cContext.getString(R.string.pp_mai)+ "/" + kyoku  +this.cContext.getString(R.string.pp_kyoku); 		//○枚/○曲;
				dbMsg += ",sub_text="+ sub_text;
				stmt = stmtWrite2( sub_text  , stmt , 7);
				albamCo = albamCo + maiSuu;
				dbMsg += ",合計："+ albamCo +"枚";
				titolCo = titolCo + kyoku;
				dbMsg += + titolCo +"曲";
				artistlist += aArtist + "\n";

				long wId = stmt.executeInsert();					//書込み
				dbMsg += "[" + wId +"]に追加";///////////////////		ZenkyokuList.this.
			}
			int aCount = ZenkyokuList.this.artistList.size();
			dbMsg += aCount + "件目=" + ZenkyokuList.this.artistList.get(ZenkyokuList.this.artistList.size()-1 );
			do{
				artistN = setAlbumArtist(cursor);
				dbMsg += "）" + artistN ;
				String nSortName = ORGUT.ArtistPreFix(artistN.toUpperCase());	//大文字化してアーティスト名のTheを取る
				dbMsg += ",sort_name=" + nSortName ;
				if(sort_name.equals(nSortName)){
					break;
				}
			}while( cursor.moveToNext() );
//			cursor.moveToPrevious();
			dbMsg += ",sort_name=" + cursor.getPosition() + "件まで";
			String datsFN = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
			dbMsg2 = "";
			if(kakikomi){
				dbMsg = ">>" + aCount + "件目に追加>>" + artistN + ":" + sort_name + ":" + datsFN + "\n" + dbMsg;
				myLog(TAG,dbMsg);
			}else if(! isNotComp){
				dbMsg = motoName + ">コンピレーションに>" +artistN ;
//				myLog(TAG,dbMsg);
			}else if(sort_name.startsWith("THE")){
				String dataUrl = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
				dbMsg = ">The抜けず>" + sort_name + ":::" +dataUrl ;
//				myLog(TAG,dbMsg);
			}

		}catch(IllegalArgumentException e){
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + ":" + dbMsg2 +"で"+e.toString());
		}
		return cursor;
	}

	/**
	 * 仮アーティストリストの終了
	 * @ 無し
	 * CreateKaliListへ
	 * */
	protected void kaliAartistListEnd() {				//803;仮アーティストリストの終了
		final String TAG = "kaliAartistListEnd";
		String dbMsg= "[ZenkyokuList]";
		try{
			cursor.close();		//android.database.StaleDataException: Attempted to access a cursor after it has been closed.が発生するのでonDestroy()に任せる
			int endpoint = shortArtistList.size();
			dbMsg +=",artistList="+artistList.get(0) +"～（"+artistList.size() +"）"+artistList.get(artistList.size()-1);
			dbMsg +=",shortArtistList="+shortArtistList.get(0) +"～（"+shortArtistList.size() +"）"+shortArtistList.get(shortArtistList.size()-1);

//			Collections.sort(artistList, new Comparator<Map<String, Object>>(){
//				@Override
//				public int compare(Map<String, Object> rec1, Map<String, Object> rec2) {
//					String colName1 = (String)rec1.get("sort_name");
//					String colName2 = (String)rec2.get("sort_name");
//					return colName1.compareTo(colName2);
//				}
//			});
			Collections.sort(shortArtistList);

			if(0 < compGenList.size()){
				for(String Junl : genleList){
					for(String comp : compGenList){
						if(comp.equals(Junl)){
							String sort_name = ORGUT.ArtistPreFix(comp.toUpperCase());	//大文字化してアーティスト名のTheを取る
							ZenkyokuList.this.objMap.put("sort_name" ,sort_name );						//リストアップ順にソートできる冠詞抜きの大文字変換名
							ZenkyokuList.this.objMap.put("album_artist" ,comp );						//ゲストやグループなどを除いた最少名
							ZenkyokuList.this.objMap.put("album" ,comp );							//対象アルバム
							ZenkyokuList.this.objMap.put("credit_artist" ,comp );					//ゲストやグループなどを含む元の名称
							ZenkyokuList.this.artistList.add( objMap);
							dbMsg += "（"+artistList.size() +"）"+ comp;
							ZenkyokuList.this.shortArtistList.add( sort_name);
							dbMsg += "（"+shortArtistList.size() +"）"+ sort_name;
//							SQLiteStatement stmt = kariArtist_db.compileStatement("insert into " + artistTName +
//									"(ARTIST,ALBUM_ARTIST,ALBUM,ALBUM_ART,SUB_TEXT) values (?, ?, ?, ?, ?);");
//							stmt = stmtWrite2(comp ,  stmt , 1);							//1;ARTIST
//							stmt = stmtWrite2(comp ,  stmt , 2);				//2;ALBUM_ARTIST
//							stmt = stmtWrite2(comp ,  stmt ,  3);
//							stmt = stmtWrite2("" ,  stmt , 4);
//							stmt = stmtWrite2("" ,  stmt ,  5);
//							long id = stmt.executeInsert();
//							dbMsg += "文字[" + id +"]に追加";///////////////////		ZenkyokuList.this.
						}
					}
				}
			}
			kariArtist_db = artistHelper.getReadableDatabase();			// データベースをオープン
			cursor = kariArtist_db.query(artistTName, null , null , null , null, null , null);	//( table, columns, selection, selectionArgs, groupBy, having, orderBy)
			int artistSize = cursor.getCount();
			dbMsg += ",kariArtist_db=" + artistSize + "件";
			if(cursor.moveToFirst()){
				do{
					String _id = String.valueOf(cursor.getString(cursor.getColumnIndex("_id")));
					dbMsg += "\n" + _id + ")";
					String ｃArtist = String.valueOf(cursor.getString(cursor.getColumnIndex("ARTIST")));
					dbMsg += " " + ｃArtist;
					String sortName = String.valueOf(cursor.getString(cursor.getColumnIndex("SORT_NAME")));		//
					dbMsg += " : " + sortName;
					String aArtist = String.valueOf(cursor.getString(cursor.getColumnIndex("ALBUM_ARTIST")));		//SORT_NAME
					dbMsg += " : " + aArtist;

				}while(cursor.moveToNext());
			}
			cursor.close();
			pdMessage_stok = pdMessage_stok +"\n\n" + pd2CoundtVal + ";" +
					cContext.getString(R.string.pp_artist) + artistSize + "," + cContext.getString(R.string.comon_nin);// アーティスト  人</string>
			for(String rStr : shortArtistList) {
				Integer alubumArtistListID = ZenkyokuList.this.shortArtistList.indexOf(rStr);
				dbMsg +="\n" + alubumArtistListID + ")" + rStr;
			}
			long end=System.currentTimeMillis();		// 終了時刻の取得
			String dousaJikann = ORGUT.sdf_mss.format( (int)((end - startPart)));
			pdMes = pdMessage_stok +"["+cContext.getString(R.string.comon_syoyoujikan)+";"+ dousaJikann + "mS]";		//	<string name="">所要時間</string>
			myLog(TAG,dbMsg);
			CreateArtistList();
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg);
		}
	}

	/**
	 * アーティストリスト作成
	 * @ 無し
	 * Zenkyoku_dbをSORT_NAMEで group化して
	 * （変更前	からString[] compListに記載された名称を除いて全曲抽出）
	 * SORT_NAMEでgroup化してartist_dbへ
	 * CreateArtistListBodyへ
	 * reqCode = pt_artistList_yomikomi;
	 * */
	public int CreateArtistList(){									//アーティストリストを読み込む(db未作成時は-)
		int retInt = -1;
		final String TAG = "CreateArtistList";
		String dbMsg= "[ZenkyokuList]";
		try{
			reqCode = pt_artistList_yomikomi;
			pd2CoundtVal++;
			dbMsg = pd2CoundtVal + ")" + reqCode;
			String table =artistTName = getString(R.string.artist_table);//テーブル名を指定します。
			kariArtist_db = artistHelper.getReadableDatabase();			// データベースをオープン
			String[] columns =null;			//{  "ALBUM_ARTIST" , "ARTIST"};				//検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
			String selections = null;	//"ALBUM_ARTIST = ? ";			//+ comp ;		//MediaStore.Audio.Media.ARTIST +" <> " + comp;			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
			String[] selectionArgs = null;	//new String[]{ comp };
			String groupBy =null;	//"ALBUM_ARTIST";					//groupBy句を指定します。
			String having =null;					//having句を指定します。
			String orderBy = "SORT_NAME";
			String limit = null;					//検索結果の上限レコードを数を指定します。
			Cursor cursor = kariArtist_db.query( table ,columns, selections,  selectionArgs,  groupBy,  having,  orderBy,  limit) ;
			int artistSize = cursor.getCount();
			if(cursor.moveToFirst()){
//				do{
					aArtistList =  new ArrayList<String>();		//アルバムアーティスト
					artintCo = 0;
					albamCo = 0;
					titolCo = 0;
					compCount = 0;
					aArtist = "";
					albumMei = "";
					artURL = null;
					if(artist_db != null){
						if(artist_db.isOpen()){
							artist_db.close();
						}
					}
					String fn = this.cContext.getString(R.string.artist_file);			//アーティストリスト	artist_db.getPath();
					dbMsg += "db=" + fn;
					String pdMessage = pdMessage_stok + "\n\n" + getResources().getString(R.string.medst_artist_make) +//アーティストリスト作成中
							getResources().getString(R.string.zl_create_artist_list);							//..リストアップするアーティスト名毎の集計\n..コンピレーションなどはリストの末尾に
					myLog(TAG,dbMsg);
					pTask = (plogTask) new plogTask(this ,  this).execute(reqCode,  pdMessage , cursor ,null , null , fn );		//,jikkouStep,totalStep,calumnInfo
//				}while(cursor.moveToNext());
			}


		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return retInt;
	}

	/**
	 * ALBUM_ARTISTで付随するアルバム情報を取得
	 * @ Cursor cursor Zenkyoku_dbからString[] compListに記載された名称を除いて全曲抽出
	 * @SQLiteStatement stmt
	 * doInBackgroundでartist_dbに1レコードづつ書き込み
	 * CreateArtistListEndへ
	 * */
	public Cursor CreateArtistListBody(Cursor cursor , SQLiteStatement stmt) throws IOException{		//ALBUM_ARTISTで付随するアルバム情報を取得
		int retInt = -1;					//	 ,SQLiteStatement stmt
		final String TAG = "CreateArtistListBody";
		String dbMsg= "[ZenkyokuList]";
		try{
			String dbMsg2 = null;
			String album_art = null;
			int first_year = 0;
			int last_year = 0;
			int rInt = 0;
			int maiSuu = 0;		//					albumBk ++;
			int kyoku = 0;
			boolean tEnd = false;
			this.pdCoundtVal = cursor.getPosition()+1;
			dbMsg = "[" + cursor.getPosition() +"/"+ cursor.getCount() + "]";				//progBar1.getMax()
			String rStr = null;			// = cursor.getString(cursor.getColumnIndex("ALBUM"));

			int cCount = 1;
			String[] columnNames = cursor.getColumnNames();
			dbMsg +=columnNames.length + "項目";
			for(String cName:columnNames){
				dbMsg += "," + cCount+")" + cName;
				String cVal = String.valueOf(cursor.getString(cursor.getColumnIndex(cName)));
				dbMsg += " = "+ cVal;
				if( cName.equals("_id")) {
					dbMsg += " スキップ ";
				} else {
					stmt.bindString(cCount, cVal);
					cCount++;
				}
			}

			dbMsg += "、リスト" + ZenkyokuList.this.aArtistList.size() +"件";
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return cursor;
	}

	/**
	 * アーティストリスト作成終了
	 * @ 無し
	 * addCompArtistListで除外した名称を書き込み
	 * totalEndへ
	 * */
	public void CreateArtistListEnd() {		//アーティストリスト作成終了
		final String TAG = "CreateArtistListEnd";
		String dbMsg= "[ZenkyokuList]";
		try{
			dbMsg += "；アーティストリストテーブル=" + artistTName;
			artist_db = artistHelper.getReadableDatabase();			// データベースをオープン
			dbMsg += " , artist_db = " + artist_db;//////
			if(0 < compGenList.size()){
				for(String Junl : genleList){
					for(String comp : compGenList){
						if(comp.equals(Junl)){
							Integer alubumArtistListID = ZenkyokuList.this.shortArtistList.indexOf(comp)+1;
							SQLiteStatement stmt = artist_db.compileStatement("insert into " + artistTName +
									"(ARTIST_ID,SORT_NAME,ARTIST,ALBUM_ARTIST,ALBUM,ALBUM_ART,SUB_TEXT) values (?, ?, ?, ?, ?, ?, ?);");
							stmt = stmtWrite2(String.valueOf(alubumArtistListID) ,  stmt , 1);				//1.MediaStore.Audio.Media.ARTIST_ID
							stmt = stmtWrite2(comp ,  stmt , 2);				//2.the抜き大文字
							stmt = stmtWrite2(comp ,  stmt ,  3);				//3,MediaStore.Audio.Albums.ARTIST
							stmt = stmtWrite2(comp,  stmt , 4);			//4,ALBUM_ARTIST
							stmt = stmtWrite2("" ,  stmt ,  5);		//5,MediaStore.Audio.Albums.ALBUM
							stmt = stmtWrite2("" ,  stmt ,  6);		//6,MediaStore.Audio.Albums.ALBUM_ART
							stmt = stmtWrite2("" ,  stmt ,  7);		//7.アーティストリスト生成用の集約情報
							long id = stmt.executeInsert();
							dbMsg += "文字[" + id +"]に追加";///////////////////		ZenkyokuList.this.
						}
					}
				}
			}

			cursor = artist_db.query(artistTName, null , null , null , null, null , null);	//( table, columns, selection, selectionArgs, groupBy, having, orderBy)
			pdMaxVal = cursor.getCount();
			if(cursor.moveToFirst()){
				do{
					String _id = String.valueOf(cursor.getString(cursor.getColumnIndex("_id")));
					dbMsg += "\n" + _id + ")";
					String ｃArtist = String.valueOf(cursor.getString(cursor.getColumnIndex("ARTIST")));
					dbMsg += " " + ｃArtist;
					String sortName = String.valueOf(cursor.getString(cursor.getColumnIndex("SORT_NAME")));		//
					dbMsg += " : " + sortName;
					String aArtist = String.valueOf(cursor.getString(cursor.getColumnIndex("ALBUM_ARTIST")));		//SORT_NAME
					dbMsg += " : " + aArtist;

				}while(cursor.moveToNext());
			}
			cursor.close();

			dbMsg +=";; " + pdMaxVal + "人 ; " + cursor.getColumnCount() + "項目";//////
			long end=System.currentTimeMillis();						// 終了時刻の取得
			String dousaJikann = ORGUT.sdf_mss.format( (int)((end - startPart)));
			pdMessage_stok = pdMessage_stok + "\n\n" + pd2CoundtVal + ":" +
					getResources().getString(R.string.common_artist_list)  + pdMaxVal + this.cContext.getString(R.string.comon_nin);		//追加</string>
			pdMessage_stok = pdMessage_stok +"[" +dousaJikann + "mS]";		//所要時間
			dbMsg +=  pdMessage_stok ;
			reqCode = pt_end;
			cursor.close();

			try {
				dbMsg += " , アーティストリストファイル書き込み= " + album_artist_file_name;
				FileOutputStream fos = new FileOutputStream(new File(album_artist_file_name));
				OutputStreamWriter osw = new OutputStreamWriter(fos);
				BufferedWriter writer = new BufferedWriter(osw);
				writer.write(artistlist);
				writer.close();
				dbMsg += " >>成功";
			} catch (IOException e) {
				myErrorLog(TAG,dbMsg +"で"+e.toString());
			}


			myLog(TAG,dbMsg );
			CreateKaliList();			//仮リスト作成
//			totalEnd( pdMessage_stok );									//データベースを閉じて終了処理
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}


	/**
	 * 仮リスト作成
	 * @ 無し
	 * MediaStore.Audio.Media.EXTERNAL_CONTENT_URIをMediaStore.Audio.Media.ARTIST,ALBUM,TRACKでソート
	 * kaliListBodyへ
	 * reqCode = pt_CreateKaliList
	 * */
	public void CreateKaliList(){
		final String TAG = "CreateKaliList";
		String dbMsg= "[ZenkyokuList]";
		try{
			startPart = System.currentTimeMillis();		// 開始時刻の取得
			dbMsg=ORGUT.nowTime(true,true,true) + dbMsg;/////////////////////////////////////
			System.currentTimeMillis();
			cContext.getContentResolver();
			if(Kari_db != null){									//全曲の仮ファイル	kari.db
				if(Kari_db.isOpen()){
					Kari_db.close();
				}
			}
			String fn = cContext.getString(R.string.kari_file);			//kari.db
			ContentResolver resolver = this.cContext.getContentResolver();	//c.getContentResolver();
			Uri cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
			String[] c_columns = null;		 		//③引数columnsには、検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
			String c_selection =  MediaStore.Audio.Media.IS_MUSIC +" <> ? ";			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
			String[] c_selectionArgs= {"0"};   			//音楽と分類されるファイルだけを抽出する
			String c_orderBy=MediaStore.Audio.Media.ARTIST  + " , " + MediaStore.Audio.Media.ALBUM+ " , " + MediaStore.Audio.Media.TRACK ; 				// + MediaStore.Audio.Media.YEAR  + " DESC , "	降順はDESC
			cursor = resolver.query( cUri , c_columns , c_selection , c_selectionArgs  , c_orderBy);
			dbMsg=dbMsg +";"+ kyoku + "件×"+ cursor.getColumnCount() + "項目";
	//		myLog(TAG,dbMsg);
			if(cursor.moveToFirst()){
				aArtist = "";
				b_AlbumMei = "";
				bArtistN = "";
				reqCode = pt_CreateKaliList;								//803;仮リスト作成
				pd2CoundtVal++;
				pdMessage = pdMessage_stok + "\n\n" + pd2CoundtVal + ";" +
					this.cContext.getString(R.string.comon_album)+ this.cContext.getString(R.string.comon_matome)+ ";" +	//">アルバム"">まとめ</string>
					this.cContext.getString(R.string.comon_karifile)+ this.cContext.getString(R.string.comon_sakuseicyuu) +"\n"+	//仮ファイル作成中
					getResources().getString(R.string.zl_coment_create_kali_list);		//..アーティスト名からゲスト参加などの付属情報を削除\n..The抜き、大文字でソート
				dbMsg=reqCode + "ループ前" + pd2CoundtVal +"/"+ pd2MaxVal + ";" + pdMessage  ;
				pdCoundtVal = 0;
				pdMaxVal = cursor.getCount();
				albumCount = 0;
				album_art = null;
				last_year = null;
//				myLog(TAG,dbMsg);
				pTask = (plogTask) new plogTask(this ,  this).execute(reqCode,  pdMessage , cursor ,null , null , fn );		//,jikkouStep,totalStep,calumnInfo
			}
		}catch(IllegalArgumentException e){
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}

	/**
	 * 仮リスト作成
	 * @ Cursor cursor  MediaStore.Audio.Media.EXTERNAL_CONTENT_URIをMediaStore.Audio.Media.ARTIST,ALBUM,TRACKでソートして読み込んだ端末内の音楽データ
	 * @ SQLiteStatement stmt
	 * stmtはplogTask.endTSでKari_dbに書き込まれる
	 * CreateKaliListEndへ
	 * */
	@SuppressLint("DefaultLocale")
	public Cursor kaliListBody(Cursor cursor ,SQLiteStatement stmt ) throws IOException {			//仮リスト作成		 , SQLiteStatement stmt			5041曲 [01:17 349mS]		//2016：03；Cursor finalized without prior close()７回発生?
		final String TAG = "kaliListBody";
		String dbMsg= "[ZenkyokuList]";
		try{
			String val = null;
			Map<String, String> map = null;

			pdCoundtVal = cursor.getPosition()+1;
			progBar1.setProgress(pdCoundtVal);
			dbMsg += pdCoundtVal +"/"+ progBar1.getMax();
			String dataFPN = null;
			dataFPN = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
			dbMsg += dataFPN ;
			map = ORGUT.data2msick(dataFPN , getApplicationContext());			//URLからアーティスト～拡張子を分離させて返す
			boolean kakikae = false;
			String mPass = map.get( "mPass" );
//			dbMsg += ",mPass=" + mPass;/////////////////////////////////////////////////////////////////////////////////////////////
			
			String ｒID = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
			dbMsg += "[_ID:" + ｒID + "]";
			stmt.bindString(1, String.valueOf(ｒID));								//1.元々のID
			String motoName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
			dbMsg += motoName ;
			String artistN = setAlbumArtist(cursor);
			dbMsg += ":" + artistN ;
			String sort_name = ORGUT.ArtistPreFix(artistN.toUpperCase());	//大文字化してアーティスト名のTheを取る
			dbMsg += ",sort_name=" + sort_name ;
			boolean isNotComp = true;
			for(String Junl : genleList){
				if(artistN.equals(Junl)){
					isNotComp = false;
				}
			}
			dbMsg += ",isNotComp=" + isNotComp ;
			dbMsg += ")sort_name=" + sort_name + "、album_artist=" + artistN + "、credit_artist=" + motoName;
			stmt.bindString(2, String.valueOf(sort_name));		//2.SORT_NAME;ALBUM_ARTISTを最短化して大文字化
			stmt.bindString(3, motoName);						//3.artist;クレジットアーティスト名;cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
			stmt.bindString(4, artistN);						//4.album_artist
			if( ! bArtistN.equals(artistN) ){									//アーティストの変わり目		aArtist？
				bArtistN = artistN;
			}
			String albumMei = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
			dbMsg += "、" + albumMei ;
			dbMsg +=" , albumMei =" +  albumMei ;/////////////////////////////////////
			if(albumMei== null){
				val = map.get( "Alnbum" ); // 指定したキーに対応する値を取得. キャスト不要
				if( val != null ){
					albumMei = val;
				}else {
					albumMei = getApplicationContext().getResources().getString(R.string.comon_nuKnow_album);
				}
				dbMsg +=">>" +  albumMei ;/////////////////////////////////////
			}
			
			stmt.bindString(5, albumMei);										//5.album
			if( ! b_AlbumMei.equals(albumMei) ){								//アルバムの変わり目
				b_AlbumMei = albumMei;
				trackCount=0;
				lastYear = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR));
			}
			trackCount++;
			String readStr = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK));
			dbMsg += " ,[MediaStoreのTRACK=" + readStr + "]" ;
			if(readStr == null){						//拾えなければ
				dbMsg += ">>" + readStr + "]" ;
				readStr = String.valueOf(trackCount);
			}
			readStr = UTIL.checKTrack( readStr);
			if (readStr.contains("/")){									//トラックNo/曲数　の場合がある
				String[] tStrs = readStr.split("/");
				readStr = tStrs[0];
			}
			if(readStr.length() == 1){				//一桁なら
				readStr="0"+readStr;				//先頭に0を付加
			}
			dbMsg +=">>" + readStr + "]" ;/////////////////////////////////////
			stmt.bindString(6, readStr);																	//6.track;トラックNo,
			String nTitol = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
			dbMsg +=nTitol ;/////////////////////////////////////
			if(nTitol== null){
				val = map.get( "titolName" ); // 指定したキーに対応する値を取得. キャスト不要
				if( val != null ){
					nTitol = val;
				}else {
					nTitol = getApplicationContext().getResources().getString(R.string.comon_nuKnow_album);
				}
				dbMsg +=">>" +  nTitol ;/////////////////////////////////////
			}
			stmt.bindString(7, nTitol);																		//7.title;タイトル
			stmt.bindString(8, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));	//8.duration;再生時間
			readStr = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR));
			dbMsg +=",YEAR=" +readStr ;/////////////////////////////////////
			if(readStr != null){
				stmt.bindString(9, cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR)));		//9.MediaStore.Audio.Media.YEAR
			}else{
				stmt.bindString(9, "");			// = cur.getColumnIndex(MediaStore.Audio.Media.YEAR);
			}
			dataFPN = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
			stmt.bindString(10, dataFPN);
			String tuikabi = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));
			dbMsg +=" , 追加日="+ tuikabi;/////////////////////////////////////////////////////////////////////////////////////////////
			String kousinnbi = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));	//The time the file was last modified
			dbMsg +=" , 更新日="+ kousinnbi;/////////////////////////////////////////////////////////////////////////////////////////////
			if( kousinnbi == null ){
				kousinnbi = tuikabi;
				dbMsg +=">>"+ kousinnbi;/////////////////////////////////////////////////////////////////////////////////////////////
			}
			stmt.bindString(11, kousinnbi);			//11.idkousinnbi = cur.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED);
			String composer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER));
			dbMsg +=",composer=" + composer ;
			if( composer != null){
				stmt.bindString(12, composer);			//12.idcomposer = cur.getColumnIndex(MediaStore.Audio.Media.COMPOSER);
			}else{
				stmt.bindString(12, "");				//COMPOSER
			}
			//????
			if( lastYear != null){
				stmt.bindString(13, lastYear);			//LAST_YEAR
			}else{
				stmt.bindString(13, "");				//13.MediaStore.Audio.Albums.LAST_YEAR
			}

			String aSelection =  "SORT_NAME = ? ";			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
			String[] aSelectionArgs= {sort_name};   			//音楽と分類されるファイルだけを抽出する
			Cursor aCursor = artist_db.query(artistTName, null , aSelection , aSelectionArgs , null, null , null);	//( table, columns, selection, selectionArgs, groupBy, having, orderBy)
			Integer alubumArtistListID = aCursor.getInt(aCursor.getColumnIndex("_id"));
//			Integer alubumArtistListID = ZenkyokuList.this.shortArtistList.indexOf(sort_name)+1;
			dbMsg += "、ALBUM_ARTIST_LIST_ID="+ alubumArtistListID;
			if(-1 < alubumArtistListID){
				dbMsg += ">>書込み";
				stmt.bindString(14, String.valueOf(alubumArtistListID));			//ALBUM_ARTIST_LIST_IDのID
			}else{
				dbMsg += ">最後に>"+ ZenkyokuList.this.shortArtistList.size() + 1;
				stmt.bindString(14, String.valueOf(aCursor.getCount() + 1));			//ALBUM_ARTIST_LIST_IDのID
//				stmt.bindString(14, String.valueOf(ZenkyokuList.this.shortArtistList.size() + 1));			//ALBUM_ARTIST_LIST_IDのID
			}
			aCursor.close();
			if( ZenkyokuList.this.saisinnbi == null){					//最新更新日付が拾えていなければ
				ZenkyokuList.this.saisinnbi = kousinnbi;
//				dbMsg += ">>"+ ZenkyokuList.this.saisinnbi;/////////////////////////////////////////////////////////////////////////////////////////////
			}else if( ZenkyokuList.this.saisinnbi.equals("null")){
				ZenkyokuList.this.saisinnbi = kousinnbi;
//				dbMsg += ">>"+ ZenkyokuList.this.saisinnbi;/////////////////////////////////////////////////////////////////////////////////////////////
			}else if(Integer.valueOf(ZenkyokuList.this.saisinnbi) < Integer.valueOf(kousinnbi)){
				ZenkyokuList.this.saisinnbi = kousinnbi;
//				dbMsg += ">>"+ ZenkyokuList.this.saisinnbi;/////////////////////////////////////////////////////////////////////////////////////////////
			}
			if( dataFPN != null && artistN != null ){
				if( dataFPN.contains(artistN) ){
					String dirName = dataFPN.substring(0, dataFPN.indexOf(artistN));
					if(dirName.contains( "external" ) || dirName.contains( "mnt/sdcard" )){					//メモリカード		exDrive
						ZenkyokuList.this.exDir = dirName;				//外部メモリ + "\n"
						if(ZenkyokuList.this.exDir.length() < dirName.length() || dirName.contains("Music") || dirName.contains("MUSIC") || dirName.contains("music")){
							ZenkyokuList.this.exDir = dirName;				//外部メモリ + "\n"
						}else{
							ZenkyokuList.this.exDir = exDrive;				//外部メモリ + "\n"
						}
//						dbMsg +=">>exDir="+ ZenkyokuList.this.exDir;/////////////////////////////////////////////////////////////////////////////////////////////
					}else{				//								dbMsg=dbMsg +"、内蔵メモリ="+ inDrive + ",="+ exDrive;
						ZenkyokuList.this.inDir = dirName;				//内蔵メモリ + "\n"
						if(ZenkyokuList.this.inDir.length() < dirName.length() || dirName.contains("Music") || dirName.contains("MUSIC") || dirName.contains("music")){
							ZenkyokuList.this.inDir = dirName;				//内蔵メモリ + "\n"
						}else{
							ZenkyokuList.this.inDir = inDrive;				//内蔵メモリ + "\n"
						}
						dbMsg +=">>inDir="+ ZenkyokuList.this.inDir;/////////////////////////////////////////////////////////////////////////////////////////////
					}
				}
			}

//			if( (! artistN.equals(motoName) && (
//					! artistN.equals("Compilations" )||
//					! artistN.equals("Soundtrack"))
//			)){
//				dbMsg = motoName +">フォルダ名に>" + artistN + ":" + dataFPN ;
//				myLog(TAG,dbMsg);
//			}else if(sort_name.contains("The")){
//				dbMsg = ">The抜けず>" ;
//				myLog(TAG,dbMsg);
//			}

		}catch(IllegalArgumentException e){
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return cursor;
	}

	//http://team-redbean.blogspot.jp/2011/07/concurrentmodificationexception.html
	//http://wada811.blogspot.com/2013/11/java-synchronized-example.html
	/**
	 * 仮リストの終了
	 * @ 無し
	 * kari.dbを新規作成して書き込み
	 * jyuufukuSakujyoへ
	 * */
	public void CreateKaliListEnd(){														
		final String TAG = "CreateKaliListEnd";
		String dbMsg= "[ZenkyokuList]";
		try{
			cursor.close();
			if(Kari_db != null){									//全曲の仮ファイル	kari.db
				if(Kari_db.isOpen()){
					Kari_db.close();
				}
			}
			pNFVeditor = sharedPref.edit();
			dbMsg +=kyoku+"曲";
			pNFVeditor.putString( "pref_file_kyoku", String.valueOf(kyoku));		//総曲数
			dbMsg +="、更新日="+ ZenkyokuList.this.saisinnbi;
			pNFVeditor.putString( "pref_file_saisinn", ZenkyokuList.this.saisinnbi);					//最新更新日
			dbMsg= dbMsg +",メモリーカード="+ZenkyokuList.this.exDir;
			if(! ZenkyokuList.this.exDir.equals("")){
				pNFVeditor.putString( "pref_file_ex", File.separator + ZenkyokuList.this.exDir);								//メモリーカード
			}
			dbMsg= dbMsg +",内蔵メモリ="+ ZenkyokuList.this.inDir;		//+"（合計；"+mDir.size();
			if(! ZenkyokuList.this.inDir.equals("")){
				pNFVeditor.putString( "pref_file_in", File.separator + ZenkyokuList.this.inDir);								//内蔵メモリ
			}
			boolean kakikomi = pNFVeditor.commit();	// データの保存
			dbMsg= dbMsg+ "、書き込み" + kakikomi;
			
//			dbMsg += " , Kari_db = " + Kari_db;//////
			String fn = cContext.getString(R.string.kari_file);			//kari.db
			dbMsg += ",db=" + fn;
			zenkyokuHelper = new ZenkyokuHelper(getApplicationContext() , fn);		//全曲リストの定義ファイル		.this.cContext.
//			dbMsg += ">作り直し>" + cContext.getDatabasePath(fn).getPath();	///data/data/com.hijiyam_koubou.marasongs/databases/artist.db
			zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
//			dbMsg += "；全曲リストテーブル=" + zenkyokuTName;
			Kari_db = zenkyokuHelper.getReadableDatabase();			// データベースをオープン
			String c_groupBy=  " SORT_NAME";	//ALBUM , ALBUM_ARTISTで587、ALBUMで124,ALBUM_ARTISTで124,ARTISTで123
			String c_having=null;										// "COUNT(*) > 1"	降順はDESC
			cursor = Kari_db.query(zenkyokuTName, null , null , null , c_groupBy, c_having , null);	//( table, columns, selection, selectionArgs, groupBy, having, orderBy)
			souNinzuuk = cursor.getCount();
			c_groupBy= "ALBUM , SORT_NAME";		//で587、ALBUMで124,ALBUM_ARTISTで124,ARTISTで123
			cursor = Kari_db.query(zenkyokuTName, null , null , null , c_groupBy, c_having , null);	//( table, columns, selection, selectionArgs, groupBy, having, orderBy)
			souMaiSuu = cursor.getCount();
			c_groupBy= null;	//ALBUM , ALBUM_ARTISTで587、ALBUMで124,ALBUM_ARTISTで124,ARTISTで123
			cursor = Kari_db.query(zenkyokuTName, null , null , null , c_groupBy, c_having , null);	//( table, columns, selection, selectionArgs, groupBy, having, orderBy)
			souKyokuSuu = cursor.getCount();
			dbMsg +=";; " + pdMaxVal + "件 ; " + cursor.getColumnCount() + "項目";//////
			cursor.close();
			long end=System.currentTimeMillis();						// 終了時刻の取得
			String dousaJikann = ORGUT.sdf_mss.format( (int)((end - startPart)));
			pdMessage_stok = pdMessage_stok + "\n\n" + pd2CoundtVal + ";" +getString(R.string.comon_karifile) + 	//仮ファイル
					souNinzuuk +this.cContext.getString(R.string.comon_nin) + souMaiSuu +this.cContext.getString(R.string.pp_mai) + souKyokuSuu +this.cContext.getString(R.string.pp_kyoku) + "\n" +		//仮ファイル○曲;
					getResources().getString(R.string.zl_coment_create_kali_list);		//..アーティスト名からゲスト参加などの付属情報を削除\n..The抜き、大文字でソート
	//				 +	";"+ this.cContext.getString(R.string.comon_album)+ albumCount +this.cContext.getString(R.string.pp_mai); 			//アルバム○">枚</string>
			pdMessage_stok = pdMessage_stok + "\n" +"[" +dousaJikann + "mS]";		//所要時間
			dbMsg +=";; " +pdMessage_stok;//////
			myLog(TAG,dbMsg);
//			CreateZenkyokuList();				//全曲リスト作成
			jyuufukuSakujyo();				//コンピレーション抽出；アルバムアーティスト名の重複
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}

	/**
	 * コンピレーション抽出；アルバムアーティスト名の重複
	 * @ 無し
	 * Kari_dbをALBUM でグルーピング。
	 * 	ALBUM , SORT_NAMEでSort
	 * jyuufukuSakujyoBodyへ
	 * reqCode = pt_jyuufukuSakujyo
	 * */
	public void jyuufukuSakujyo(){		//コンピレーション抽出；アルバムアーティスト名の重複
		final String TAG = "jyuufukuSakujyo";
		String dbMsg= "[ZenkyokuList]";
		try{
			startPart = System.currentTimeMillis();		// 開始時刻の取得
			String comp = cContext.getString(R.string.comon_compilation);			//コンピレーション
			new ArrayList<String>();
			cContext.getContentResolver();

			String fn = cContext.getString(R.string.kari_file);			//仮ファイル
			dbMsg += " , fn = " + fn;		//03-28java.lang.IllegalArgumentException:  contains a path separator
			Kari_db = zenkyokuHelper.getWritableDatabase();			//アーティスト名のえリストファイルを書き込み可能モードで開く
			dbMsg += ">isOpen>" + Kari_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
			zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
			String[] c_columns = null;	////{ "ARTIST", "ALBUM ", "YEAR " };			//+ comp ;		//MediaStore.Audio.Media.ARTIST +" <> " + comp;			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
			String c_selection = null;					//"ALBUM = ? ";			//+ comp ;		//MediaStore.Audio.Media.ARTIST +" <> " + comp;			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
			String[] c_selectionArgs= null;   			//	 {"%" + artistMei + "%" , albumMei };
			String c_groupBy=  "ALBUM";			// , SORT_NAME　ALBUM , ALBUM_ARTISTで587、ALBUMで124,ALBUM_ARTISTで124,ARTISTで123
			String c_having= null;			//2で318,1で340、nullで587	降順はDESC
			String c_orderBy= "ALBUM , SORT_NAME";	//ALBUM_ARTIST,YEAR	。	降順はDESC
			cursor = Kari_db.query(zenkyokuTName, c_columns, c_selection, c_selectionArgs, c_groupBy, c_having, c_orderBy, null);				//(rSQL ,null);
			int souMai = cursor.getCount();
			Kari_db.close();
			dbMsg = "アルバムは" + souMai +"枚";
			if(cursor.moveToFirst()){
				reqCode = pt_jyuufukuSakujyo;			//コンピレーション抽出；アルバムアーティスト名の重複
				pd2CoundtVal++;
				pdMessage_stok = pdMessage_stok + "\n\n" + pd2CoundtVal + ";" +comp + getString(R.string.comon_syori)+"\n"+		//コンピレーション処理</string>
						getString(R.string.zl_coment_comp_matome)+"\n"+				//.条件1.トラック番号の重複\nn...条件2:設定した比率より別のアーティスト曲が多い</string>
						getString(R.string.zl_genzai_lisi)+ getString(R.string.comon_album) +souMai + this.cContext.getString(R.string.pp_mai);	 	//">リストアップされる"">アルバム</string></string>アルバム ○○枚確認
				pdMessage = pdMessage_stok + "\n\n" +
						getString(R.string.common_kakunincyuu) ;				//."">確認中</string>
			//	dbMsg +=reqCode + "ループ前" + pd2CoundtVal +"/"+ pd2MaxVal + ";" + pdMessage  ;
				pdCoundtVal = 0;
				myLog(TAG,dbMsg);
				pTask = (plogTask) new plogTask(this ,  this).execute(reqCode,  pdMessage , cursor ,null , null , fn );		//,jikkouStep,totalStep,calumnInfo
			}				//if( cursor.moveToFirst()){
	//		myLog(TAG,dbMsg);
		}catch(IllegalArgumentException e){
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}

	public String compIndex;
	public ContentValues cv;
	public String udIdName;							//アップデートする名前
    public String allSonglist = "";

	/**
	 * コンピレーションの抽出とアルバムアーティスト名の重複処理
	 * @ Cursor cursor  Kari_dbをALBUM でグルーピングしたレコード
	 * 1.Kari_dbからアルバムごとの曲データレコードを読み出し、
	 * 
	 * アップデートするIDをArrayList<String> udIdListに収集
	 * jyuufukuSakujyoEndへ
	 * */
	public Cursor jyuufukuSakujyoBody(Cursor cursor) throws IOException {		//アルバムアーティスト名の重複   ,  ,SQLiteStatement stmt
		final String TAG = "jyuufukuSakujyoBody";
		String dbMsg= "[ZenkyokuList]";
		try{
			boolean jikkou =true;
			int onajiTrack = 0;
			dbMsg += "[" + (cursor.getPosition() + 1 ) +"/"+ cursor.getCount() + "枚目]";
			String zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
			dbMsg = cursor.getPosition()+ "/;" + cursor.getCount();		// +"；" +"[" + id1 +"]" + aName1+";" + song1+"曲";
			String albumName = cursor.getString(cursor.getColumnIndex("ALBUM"));
			dbMsg += "アルバム：" + albumName;
			String c_selection ="ALBUM = ? ";			//+ comp ;		//MediaStore.Audio.Media.ARTIST +" <> " + comp;			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
			String[] c_selectionArgs=  {albumName};   			//	 {"%" + artistMei + "%" , albumMei };
			String c_groupBy= null;		//ALBUM_ARTIST "ALBUM";	//,YEAR	。	降順はDESC
			String c_having= null;			//"COUNT(*) > 1";	//ALBUM_ARTIST,YEAR	。	降順はDESC
			String c_orderBy= "ALBUM_ARTIST";	//	降順はDESC
			Cursor tCursor = ZenkyokuList.this.Kari_db.query(zenkyokuTName, null, c_selection, c_selectionArgs, c_groupBy, c_having, c_orderBy);				//(rSQL ,null);
			int kyokuSuu = tCursor.getCount();
			dbMsg=dbMsg +"は"+ kyokuSuu + "曲、";
			bArtistN = "";
			int onajiCount = 0;
			if( tCursor.moveToFirst() && 1 < tCursor.getCount()){
				bArtistN  = tCursor.getString(tCursor.getColumnIndex("ALBUM_ARTIST"));		//最短アーティスト名
				Map<String, Integer> intMap = new HashMap<String, Integer>();
				String artistName;
				do{
					artistName =String.valueOf( tCursor.getString(tCursor.getColumnIndex("ALBUM_ARTIST")));		//最短アーティスト名		ALBUM_ARTIST	SORT_NAME"));	
					String track = String.valueOf( tCursor.getString(tCursor.getColumnIndex("TRACK")));			//6.track;トラックNo,
					track = UTIL.checKTrack( track);
					if(bArtistN.equals(artistName)){
						onajiCount++;
					}else{
						dbMsg += "、" + artistName + ";" + onajiCount  + "曲" ;
						intMap.put(bArtistN ,onajiCount );
						onajiCount = 1;
						bArtistN = artistName;
					}
					if(track.equals("01")){
						onajiTrack++;
					}
				}while( tCursor.moveToNext() );
				if(1 < onajiCount){							//最多アーティストが最後の場合
					dbMsg += "、" + artistName + ";" + onajiCount  + "曲" ;
					intMap.put(bArtistN ,onajiCount );
				}
				dbMsg =  albumName + "は" + kyokuSuu + "曲中";
				dbMsg += "、参加アーティスト=" + intMap.size() +"人、コンプレーション判定値=" + pref_compBunki + "%に対し";
				if(1 < intMap.size() && onajiTrack < 2){			//2曲以上のアルバムで同じトラック番号が無ければ
					List<Map.Entry<String,Integer>> entries =  new ArrayList<Map.Entry<String,Integer>>(intMap.entrySet());						// List 生成 (ソート用)
					Collections.sort(entries, new Comparator<Map.Entry<String,Integer>>() {
						@Override
						public int compare( Entry<String,Integer> entry1, Entry<String,Integer> entry2) {
							return entry2.getValue().compareTo(entry1.getValue());
						}
					});
					String rArtistName = entries.get(0).getKey();				//.get("ALBUM_ARTIST"):
					int saidai = entries.get(0).getValue();
					dbMsg +=",最多=" + rArtistName +"=" + saidai +"曲";
					String SORT_NAME = rArtistName;
					float compBunk = ( saidai * 1.0f / kyokuSuu) *100;					//コンピレーションの分離のみなら	( objMap.size()*1.0f / kyokuSuu) *100;
					dbMsg +="、全曲中=" + compBunk;
					int compInt =  (int) (compBunk +0.5f);		//	Integer.valueOf(  String.valueOf
					dbMsg += ">>" + compInt +"%";
					if( compInt < Integer.valueOf(pref_compBunki)  ){						//parseIntではjava.lang.NumberFormatException: Invalid int: "16.666668"
						rArtistName =  cContext.getString(R.string.comon_compilation);			//コンピレーション	tCursor.getString(tCursor.getColumnIndex("ARTIST"));
						SORT_NAME = rArtistName;
					} else{
						int jitenn = entries.get(1).getValue();
						if(2 < jitenn){					//同名アルバム対策（仮）
							jikkou  = false;
							dbMsg +="、同名アルバムで処理中断 ";
						}else{
							SORT_NAME = ORGUT.ArtistPreFix(rArtistName.toUpperCase());	//大文字化してアーティスト名のTheを取る
						}
					}
					dbMsg +="、rArtistName=" + rArtistName +"、SORT_NAME=" + SORT_NAME;
					if( tCursor.moveToFirst() && jikkou ){
			//			sortNameReWright(tCursor ,rArtistName , SORT_NAME);		//アルバム一枚分のアルバムアーティスト名修正//						try {
						try {
					//		dbMsg += ">Kari_db.isOpen>" + Kari_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
							if(Kari_db.isOpen()){
								Kari_db.close();
				//				dbMsg += ">>" + Kari_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
							}
					//		dbMsg += ">isOpen>" + Kari_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
							if( ! Kari_db.isOpen()){
								Kari_db = zenkyokuHelper.getWritableDatabase();			//アーティスト名のえリストファイルを書き込み可能モードで開く
				//				dbMsg += ">>" + Kari_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
							}
							
							do{
				//				dbMsg =dbMsg+"書込み" +  tCursor.getPosition()+ "/" + tCursor.getCount() +"人目(id=";		// +"；" +"[" + id1 +"]" + aName1+";" + song1+"曲";
								String rID = tCursor.getString(tCursor.getColumnIndex("_id"));
								ContentValues cv = new ContentValues();
									cv.put("ALBUM_ARTIST", rArtistName);
									cv.put("SORT_NAME", SORT_NAME);
									String where = "_id = ?";
									String[] selectionArgs = {rID};
									int rRow = Kari_db.update(zenkyokuTName, cv , where, selectionArgs);
						//			dbMsg +=">書込み>" + rRow;
							}while( tCursor.moveToNext() );
						} finally {
							if (Kari_db != null) {
								Kari_db.close();
								Kari_db = zenkyokuHelper.getReadableDatabase();			//アーティスト名のえリストファイルを書き込み可能モードで開く
							}
						}
					}
		//			myLog(TAG,dbMsg);
				}else{
					dbMsg =dbMsg+"1曲しかない";
					dbMsg =dbMsg+"トラック番号01が" + onajiTrack + "曲で書換えしない";
		//			myLog(TAG,dbMsg);
				}
			}		//	( tCursor.moveToFirst() ){
			tCursor.close();
		}catch(IllegalArgumentException e){
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return cursor;
	}
		

	/**
	 * アルバムアーティスト名の重複処理終了
	 * @ 無し
	 * kari.dbを開き直して
	 * henkouHaneiへ
	 * */
	public void jyuufukuSakujyoEnd(){		//アルバムアーティスト名の重複
		final String TAG = "jyuufukuSakujyoEnd";
		String dbMsg= "[ZenkyokuList]";
		try{
			cursor.close();
			dbMsg += ">>" + Kari_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
			if(Kari_db.isOpen()){
				Kari_db.close();
				dbMsg += ">>" + Kari_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
			}
			dbMsg += ">isOpen>" + Kari_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
			if( ! Kari_db.isOpen()){
				Kari_db = zenkyokuHelper.getReadableDatabase();			//アーティスト名のえリストファイルを書き込み可能モードで開く
				dbMsg += ">>" + Kari_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
			}
			String fn = cContext.getString(R.string.kari_file);			//仮ファイル
			dbMsg += " , fn = " + fn;		//03-28java.lang.IllegalArgumentException:  contains a path separator
			Kari_db = zenkyokuHelper.getWritableDatabase();			//アーティスト名のえリストファイルを書き込み可能モードで開く
			dbMsg += ">isOpen>" + Kari_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
			zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
			String[] c_columns = null;	////{ "ARTIST", "ALBUM ", "YEAR " };			//+ comp ;		//MediaStore.Audio.Media.ARTIST +" <> " + comp;			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
			String c_selection = null;					//"ALBUM = ? ";			//+ comp ;		//MediaStore.Audio.Media.ARTIST +" <> " + comp;			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
			String[] c_selectionArgs= null;   			//	 {"%" + artistMei + "%" , albumMei };
			String c_groupBy=  "ALBUM , SORT_NAME";	//ALBUM , ALBUM_ARTISTで587、ALBUMで124,ALBUM_ARTISTで124,ARTISTで123
			String c_having=null;										// "COUNT(*) > 1"	降順はDESC
			String c_orderBy= " SORT_NAME , ALBUM";	//ALBUM_ARTIST,YEAR	。	降順はDESC
			Cursor cursor = Kari_db.query(zenkyokuTName, c_columns, c_selection, c_selectionArgs, c_groupBy, c_having, c_orderBy, null);				//(rSQL ,null);
			int souMai = cursor.getCount();
			pdMes =souMai + "件";		//	<string name="">所要時間</string>
			cursor.close();
			Kari_db.close();
			long end=System.currentTimeMillis();		// 終了時刻の取得
			String dousaJikann = ORGUT.sdf_mss.format( (int)((end - startPart)));
			pdMessage_stok = pdMessage_stok + ">>" +souMai  + this.cContext.getString(R.string.pp_mai) + "\n" +
					this.cContext.getString(R.string.comon_kakuninn) +"[" + dousaJikann + "mS]";//複数のアーティストが収録されたアルバム ○○枚確認
			pdMes = (String) pdMessage_stok;		//	<string name="">所要時間</string>
			myLog(TAG,dbMsg);
			henkouHanei();		//805;ユーザーの変更を反映
//			CreateZenkyokuList();				//全曲リスト作成
		}catch(IllegalArgumentException e){
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}

	/**
	 * ユーザーのアルバムアーティスト名変更を反映
	 * @ 無し
	 * shyuusei.dbを読み込み
	 * henkouHaneiBodyへ
	 * reqCode = pt_HenkouHanei
	 * */
	public void henkouHanei(){		//805;ユーザーの変更を反映
		final String TAG = "henkouHanei";
		String dbMsg= "[ZenkyokuList]ユーザーの変更(shyuusei_db)" + "を反映";/////////////////////////////////////
		try{
			startPart = System.currentTimeMillis();		// 開始時刻の取得
			reqCode = pt_HenkouHanei;					//805;ユーザーの変更を反映
			pd2CoundtVal++;

			String fn = getApplicationContext().getString(R.string.shyuusei_file);			//	shyuusei.db</string>
			dbMsg += ",db=" + fn;
			File shyuFile = new File(fn);
			dbMsg += ",exists=" + shyuFile.exists();
//			if( shyuFile.exists()){
			shyuusei_Helper = new shyuuseiHelper(getApplicationContext() , fn);		//全曲リストの定義ファイル		.this.cContext.
//			dbMsg += ">作り直し>" + cContext.getDatabasePath(fn).getPath();	///data/data/com.hijiyam_koubou.marasongs/databases/artist.db
			shyuuseiTName = getResources().getString(R.string.shyuusei_table);			//	<string name="">shyuusei_table</string>
//			dbMsg += "；全曲リストテーブル=" + zenkyokuTName;
			if(shyuusei_db != null){
				dbMsg += "," + shyuusei_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
	//			myLog(TAG,dbMsg);
				if(shyuusei_db.isOpen()){
					shyuusei_db = shyuusei_Helper.getReadableDatabase();			// データベースをオープン
					dbMsg += ">isOpen>" + shyuusei_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
				}
			}else{
				shyuusei_db = shyuusei_Helper.getReadableDatabase();			// データベースをオープン
				dbMsg += ">isOpen>" + shyuusei_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
			}

			cursor = shyuusei_db.query(shyuuseiTName, null, null, null , null, null, null);	// table, columns,new String[] {MotoN, albamN}
			int syuuseMae = cursor.getCount();					//修正前のレコードの合計
			dbMsg += syuuseMae+"件";
			if( cursor.moveToFirst() ){
				pdMessage_stok = pdMessage_stok + "\n\n" + pd2CoundtVal + ";" +syuuseMae + getString(R.string.muqu_dlog_kenwo)+		//	件を</string>
						getString(R.string.menu_funk_artistmei2);				//他のアーティストに統合する
				pdMessage = pdMessage_stok + "\n\n" +
						getString(R.string.common_kakunincyuu) ;				//."">確認中</string>
				dbMsg +=reqCode + ";ループ前" + pd2CoundtVal +"/"+ pd2MaxVal ;					//+ ";" + pdMessage  ;
				pdCoundtVal = 0;
		//		myLog(TAG,dbMsg);
				pTask = (plogTask) new plogTask(this ,  this).execute(reqCode,  pdMessage , cursor ,null , null , fn );		//,jikkouStep,totalStep,calumnInfo
			}else{
				cursor.close();
				shyuusei_db.close();
				pdMessage_stok = pdMessage_stok + "\n\n" + pd2CoundtVal + ";" + getString(R.string.list_contex_listup)+		//リストアップ編集</string>
				getString(R.string.common_syouryaku);				//	省略</string>
				CreateZenkyokuList();				//全曲リスト作成
			}
		}catch(IllegalArgumentException e){
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}

	/**
	 * ユーザーのアルバムアーティスト名変更反映処理
	 * @ Cursor cursor  shyuusei.db
	 * Kari_dbとDATAで照合してSORT_NAMEとALBUM_ARTISTを書換え
	 * CreateZenkyokuListへ
	 * */
	public Cursor henkouHaneiBody( Cursor cursor ) throws IOException {		//805;ユーザーの変更を反映
		final String TAG = "henkouHaneiBody";
		String dbMsg= "[ZenkyokuList]";
		try{
			dbMsg = "[" + ( cursor.getPosition() +1 )+ "/" + cursor.getCount() +"]";
			String dataUri = cursor.getString(cursor.getColumnIndex("DATA"));
			dbMsg += dataUri;
			String udIdName = cursor.getString(cursor.getColumnIndex("ALBUM_ARTIST"));
			String syuuseiArtist = cursor.getString(cursor.getColumnIndex("ARTIST"));
			dbMsg +="をALBUM_ARTIST「"+ udIdName + "」に変更";
			cContext.getContentResolver();
			String fn = cContext.getString(R.string.kari_file);			//仮ファイル
	//		dbMsg += " , fn = " + fn;		//03-28java.lang.IllegalArgumentException:  contains a path separator
			Kari_db = zenkyokuHelper.getWritableDatabase();			//アーティスト名のえリストファイルを書き込み可能モードで開く
	//		dbMsg += ">isOpen>" + Kari_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
			zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
			String[] c_columns = null;	////{ "ARTIST", "ALBUM ", "YEAR " };			//+ comp ;		//MediaStore.Audio.Media.ARTIST +" <> " + comp;			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
			String c_selection ="DATA = ? ";			//+ comp ;		//MediaStore.Audio.Media.ARTIST +" <> " + comp;			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
			String[] c_selectionArgs=  {dataUri};   			//	 {"%" + artistMei + "%" , albumMei };
			String c_groupBy=  null;	//ALBUM , ALBUM_ARTISTで587、ALBUMで124,ALBUM_ARTISTで124,ARTISTで123
			String c_having= null;			//2で318,1で340、nullで587	降順はDESC
			String c_orderBy= "ALBUM , ALBUM_ARTIST";	//ALBUM_ARTIST,YEAR	。	降順はDESC
			Cursor cCursor = Kari_db.query(zenkyokuTName, c_columns, c_selection, c_selectionArgs, c_groupBy, c_having, c_orderBy, null);				//(rSQL ,null);
			if( cCursor.moveToFirst() ){
				String rID = cCursor.getString(cCursor.getColumnIndex("_id"));
				dbMsg +=",_id=" + rID +")";		//03-28java.lang.IllegalArgumentException:  contains a path separator
				String cARTIST = cCursor.getString(cCursor.getColumnIndex("ARTIST"));
				String aARTIST = cCursor.getString(cCursor.getColumnIndex("ALBUM_ARTIST"));
				dbMsg += cARTIST +"["+ aARTIST+"]に一致 ";		//03-28java.lang.IllegalArgumentException:  contains a path separator
				ContentValues cv = new ContentValues();
				try {
					int aLengs= aARTIST.length();
					dbMsg += "(" + aLengs +"文字)";
					aLengs = aLengs+4;
					if(aLengs < cARTIST.length()){
						if( aARTIST.startsWith("The")  || aARTIST.startsWith("THE")  ||aARTIST.startsWith("the") ){
							cARTIST = cARTIST.substring(0, aLengs);
						}else{
							cARTIST = cARTIST.substring(0, aARTIST.length());
						}
						dbMsg += ">クレジット変更>" + cARTIST;
						udIdName = udIdName;
	//					myLog(TAG,dbMsg);
					}
					cv.put("SORT_NAME", udIdName);
					cv.put("ALBUM_ARTIST", udIdName);						//ALBUM_ARTIST
					String where = "_id = ?";
					String[] selectionArgs = {rID};
					int rRow = Kari_db.update(zenkyokuTName, cv , where, selectionArgs);
					dbMsg +=">>" + rRow;
//						if( ! cARTIST.equals(syuuseiArtist)){				//クレジットされているアーティスト名も
//							dbMsg +=",ARTIST=" + cARTIST  + ">>" + syuuseiArtist;
//							cv.put("ARTIST", syuuseiArtist);				//設定されたアーティスト名に書き換える
////							 where = "_id = ?";
////							String[] selectionArgs2 = {rID};
//						}
					rRow = Kari_db.update(zenkyokuTName, cv , where, selectionArgs);
					dbMsg +=">>" + rRow;
				} finally {
					if (Kari_db != null) {
						Kari_db.close();
					}
				}
			}
			cCursor.close();
			myLog(TAG,dbMsg);
		}catch(IllegalArgumentException e){
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return cursor;
	}

//	Cursor artistCursor;
	/**
	 * 全曲リスト作成
	 * ユーザーのアルバムアーティスト名変更反映処理終了
	 * @ 無し
	 * Kari_dbからコンピレーション、サウンドトラック、クラシック以外を抽出
	 * CreateZenkyokuBodyへ
	 * reqCode = pt_CreateZenkyokuList
	 * */
	public void CreateZenkyokuList(){				//全曲リスト作成
		final String TAG = "CreateZenkyokuList";
		String dbMsg= "[ZenkyokuList]";
		int retInt = 0;
		try{
			startPart = System.currentTimeMillis();		// 開始時刻の取得
			dbMsg +=ORGUT.nowTime(true,true,true) + dbMsg;/////////////////////////////////////
			System.currentTimeMillis();
			all_songs_file_name = pref_commmn_music + File.separator + cContext.getString(R.string.all_songs_file_name) + ".m3u8";
			ZenkyokuList.this.allSongsID = musicPlaylist.getPlaylistId(cContext.getString(R.string.all_songs_file_name) );
			dbMsg += "all_songs_file_name=" + all_songs_file_name;
			dbMsg +="[" + allSongsID + "]";

			cursor.close();
			dbMsg +=",shyuusei_db.isOpen()=" + shyuusei_db.isOpen();/////////////////////////////////////
			if( shyuusei_db.isOpen() ){
				shyuusei_db.close();
				dbMsg += ">>" + shyuusei_db.isOpen();/////////////////////////////////////
			}
			dbMsg += " , comCount = " + comCount;		//03-28java.lang.IllegalArgumentException:  contains a path separator
			String fn = cContext.getString(R.string.kari_file);			//仮ファイル
			dbMsg += " , fn = " + fn;		//03-28java.lang.IllegalArgumentException:  contains a path separator
			Kari_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
			dbMsg += ">isOpen=" + Kari_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
		//	String[] c_selectionArgs = new String[]{ comp };  			//	 {"%" + artistMei + "%" , albumMei };
			String c_orderBy= "ALBUM_ARTIST_LIST_ID,LAST_YEAR,ALBUM,TRACK";	//降順はDESC		YEAR	ALBUM_ARTIST,LAST_YEAR,TRACK SORT_NAME,
			cursor = Kari_db.query(zenkyokuTName, null, compSelection, compList , null, null, c_orderBy, null);	//リString table, String[] columns,new String[] {MotoN, albamN}
			pdMaxVal = cursor.getCount();
			dbMsg += "；" + pdMaxVal + "件";
			if(cursor.moveToFirst()){
				reqCode = pt_CreateZenkyokuList;
				pd2CoundtVal++;
				pdMessage_stok = pdMessage_stok + "\n\n" + pd2CoundtVal + ";" + this.cContext.getString(R.string.listmei_zemkyoku)+ this.cContext.getString(R.string.comon_sakuseicyuu) + "\n" +	//全曲リスト作成中
					getResources().getString(R.string.zl_create_zenkyoku_list) + "\n" + pdMaxVal + this.cContext.getString(R.string.pp_kyoku);		//コンピレーション	以外○○曲				dbMsg=reqCode + "ループ前" + pd2CoundtVal +"/"+ pd2MaxVal + ";" + pdMessage  ;
				pdCoundtVal = 0;
				cContext.getContentResolver();
				cContext.getString(R.string.comon_compilation);
				if(Zenkyoku_db != null){
					if(Zenkyoku_db.isOpen()){
						Zenkyoku_db.close();
					}
				}
				fn = cContext.getString(R.string.zenkyoku_file);			//全曲リスト+ File.separator +cContext.getString(R.string.zenkyoku_file)
				dbMsg += ",db=" + fn;
//				if(musicPlaylist == null){
					musicPlaylist = new MusicPlaylist(ZenkyokuList.this);	//cContext=com.hijiyam_koubou.marasongs.ZenkyokuList@6013445,
//				}
				dbMsg += ",pref_data_url=" + pref_data_url;
				if(pref_data_url.equals("") || pref_data_url == null){
					pref_data_url = cursor.getString(cursor.getColumnIndex("DATA"));
					dbMsg += ">>" + pref_data_url;
					setPrefStr("pref_data_url",pref_data_url,ZenkyokuList.this);
				}

				myLog(TAG,dbMsg);
				pTask = (plogTask) new plogTask(this ,  this).execute(reqCode,  pdMessage_stok , cursor ,null , null , fn );		//,jikkouStep,totalStep,calumnInfo
			}
	// 3176/4110曲目12項目,_id;2856,ARTIST;Stevie Ray Vaughan & Double Trouble,ALBUM_ARTIST;STEVIE RAY VAUGHAN,ALBUM;Live Alive,TRACK;01,TITLE;Say What,DURATION;291796,YEAR;1986,DATA;/storage/sdcard1/Music/Stevie Ray Vaughan & Double Trouble/Live Alive/01 Say What.wma,MODIFIED;1388666096,COMPOSER;Stevie Ray Vaughan,LAST_YEAR;1986

		}catch(IllegalArgumentException e){
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}

	/**
	 * 全曲リスト作成処理
	 * @ Cursor cursor
	 * 　　CreateZenkyokuListからKari_dbからコンピレーション、サウンドトラック、クラシック以外を抽出
	 * 　　addCompListからKari_dbから残りを抽出
	 * @SQLiteStatement stmt
	 * doInBackgroundで1レコードづつ書き込み
	 * CreateZenkyokuListEndへ
	 * */
	public Cursor CreateZenkyokuBody(  Cursor ｃursor  ,SQLiteStatement stmt) throws IOException {		//全曲リスト作成		 ,SQLiteStatement stmt		このパートが長い
		final String TAG = "CreateZenkyokuBody";
		String dbMsg= "[ZenkyokuList]";
		try{
			int audio_id = -1;
			pdCoundtVal = ｃursor.getPosition();
			dbMsg += reqCode + "：仮リスト: "+  pdCoundtVal + "/" + pdMaxVal + "曲目";
			String cAUDIO_ID = String.valueOf(ｃursor.getString(cursor.getColumnIndex("AUDIO_ID")));
			dbMsg += "[AUDIO_ID= "+ cAUDIO_ID;
			audio_id= Integer.parseInt(cAUDIO_ID);
			dbMsg += ",audio_id=" + audio_id;

			String dataUri = String.valueOf(ｃursor.getString(cursor.getColumnIndex("DATA")));
			dbMsg += "]= "+ dataUri;
			String titolName  = String.valueOf(ｃursor.getString(cursor.getColumnIndex("TITLE")));
			dbMsg += ",titolName= "+ titolName;
			String album_artist = String.valueOf(ｃursor.getString(cursor.getColumnIndex("ALBUM_ARTIST")));
			dbMsg += ",album_artist= "+ album_artist;
			String albumName = String.valueOf(ｃursor.getString(cursor.getColumnIndex("ALBUM_ARTIST")));
			dbMsg += ",albumName= "+ albumName;

			allSonglist += titolName + "," + dataUri + "#" + album_artist + "," + albumName +"\n";
			Uri result_uri = null;
			result_uri = musicPlaylist.addMusicToPlaylist(ZenkyokuList.this.allSongsID, audio_id, dataUri);    //プレイリストへ曲を追加する
			dbMsg += ">>result_uri=" + result_uri;

            int cCount = 1;
            String[] columnNames = cursor.getColumnNames();
            dbMsg +=columnNames.length + "項目";
            for(String cName:columnNames){
                dbMsg += "," + cCount+")" + cName;
                String cVal = String.valueOf(ｃursor.getString(cursor.getColumnIndex(cName)));
                dbMsg += " = "+ cVal;
				if( cName.equals("_id")) {
                    dbMsg += " スキップ ";
				} else {
                    stmt.bindString(cCount, cVal);
                    cCount++;
				}
            }
            myLog(TAG,dbMsg);
		}catch(IllegalArgumentException e){
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return ｃursor;
	}

	/**

	/**
	 * 全曲リスト作成処理終了
	 * @ 無し
	 * addCompListへ
	 * */
	public void CreateZenkyokuListEnd() {		//全曲リストにコンピレーション追加
		final String TAG = "CreateZenkyokuListEnd";
		String dbMsg= "[ZenkyokuList]";
		try{
			cursor.close();
			Kari_db.close();
			String fn = cContext.getString(R.string.zenkyoku_file);			//全曲リスト+ File.separator +cContext.getString(R.string.zenkyoku_file)
//			dbMsg += ",db=" + fn;
			zenkyokuHelper = new ZenkyokuHelper(getApplicationContext() , fn);		//全曲リストの定義ファイル		.this.cContext.
//			dbMsg += ">作り直し>" + cContext.getDatabasePath(fn).getPath();	///data/data/com.hijiyam_koubou.marasongs/databases/artist.db
			zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
//			dbMsg += "；全曲リストテーブル=" + zenkyokuTName;
			Zenkyoku_db = zenkyokuHelper.getReadableDatabase();			// データベースをオープン
			dbMsg += " , Zenkyoku_db = " + Zenkyoku_db;//////
			cursor = Zenkyoku_db.query(zenkyokuTName, null , null , null , null, null , null);	//( table, columns, selection, selectionArgs, groupBy, having, orderBy)
			pdMaxVal = cursor.getCount();
			dbMsg +=";; " + pdMaxVal + "件 ; " + cursor.getColumnCount() + "項目";//////
			long end=System.currentTimeMillis();						// 終了時刻の取得
			String dousaJikann = ORGUT.sdf_mss.format( (int)((end - startPart)));
			pdMessage_stok = pdMessage_stok + "\n\n" + pd2CoundtVal + ":" +
				getResources().getString(R.string.listmei_zemkyoku) + "\n"+		//"">全曲リスト
				getResources().getString(R.string.zl_create_zenkyoku_list)+ "\n"+		//"">..成形したアーティスト名でソート\n..アルバムは年代順に</string>
				this.cContext.getString(R.string.comon_compilation)+ this.cContext.getString(R.string.common_tuika)+ pdMaxVal +
				this.cContext.getString(R.string.pp_kyoku);		//コンピレーション	以外○○曲
			pdMessage_stok = pdMessage_stok +"[" +dousaJikann + "mS]";		//所要時間
			cursor.close();
			Kari_db.close();

			myLog(TAG,dbMsg );
			addCompList();		//全曲リストにコンピレーション追加
		}catch(IllegalArgumentException e){
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}

	/**
	 * 全曲リストにコンピレーション追加
	 * @ 無し
	 * addCompListBodyへ
	 * reqCode = pt_CompList
	 * */
	public void addCompList() {		//全曲リストにコンピレーション追加
		final String TAG = "addCompList";
		String dbMsg= "[ZenkyokuList]";
		try{
			reqCode = pt_CompList;						//806;全曲リストにコンピレーション追加
			pd2CoundtVal++;
			pdMessage_stok = pdMessage + "\n\n" + pd2CoundtVal + ";" +getResources().getString(R.string.common_tuika);	//"">追加</string>

			//			String fn = cContext.getString(R.string.zenkyoku_file);			//全曲リスト+ File.separator +cContext.getString(R.string.zenkyoku_file)
////		dbMsg += ",db=" + fn;
//		zenkyokuHelper = new ZenkyokuHelper(getApplicationContext() , fn);		//全曲リストの定義ファイル		.this.cContext.
////		dbMsg += ">作り直し>" + cContext.getDatabasePath(fn).getPath();	///data/data/com.hijiyam_koubou.marasongs/databases/artist.db
//		zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
////		dbMsg += "；全曲リストテーブル=" + zenkyokuTName;
//		Zenkyoku_db = zenkyokuHelper.getReadableDatabase();			// データベースをオープン
//			dbMsg += " , Zenkyoku_db = " + Zenkyoku_db;//////


//			String c_selection = "ALBUM_ARTIST = ? ";			//+ comp ;		//MediaStore.Audio.Media.ARTIST +" <> " + comp;			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
//			String c_orderBy= "ALBUM_ARTIST,LAST_YEAR,ALBUM,TRACK";	//降順はDESC		YEAR
//			for( String comp : compList ){
//				pdMessage = pdMessage + "\n\n" +comp;	//"">追加</string>
//				String[] c_selectionArgs = new String[]{ comp };  			//	 {"%" + artistMei + "%" , albumMei };
//				cursor = Zenkyoku_db.query(zenkyokuTName, null, c_selection, c_selectionArgs , null, null, c_orderBy, null);	//リString table, String[] columns,new String[] {MotoN, albamN}
//				int retInr = cursor.getCount();
//				pdMessage = pdMessage + String.valueOf(retInr) + ";" +getResources().getString(R.string.pp_kyoku);	//="">曲</string>
//
//			}

//				Zenkyoku_db.close();
				long end=System.currentTimeMillis();						// 終了時刻の取得
//				String dousaJikann = ORGUT.sdf_mss.format( (int)((end - startPart)));
//				pdMessage_stok = pdMessage_stok + "\n\n" + pd2CoundtVal + ":"+
//						comp +String.valueOf(pdMaxVal) +this.cContext.getString(R.string.pp_kyoku) + ";" + 		//コンピレーション追加
//						this.cContext.getString(R.string.pref_file_kyoku)+ String.valueOf(souKyokuSuu) +	this.cContext.getString(R.string.pp_kyoku);		//"">総曲数</string>○○曲追加
//				pdMessage_stok = pdMessage_stok +"\n[" +dousaJikann + "mS]";		//所要時間
				comCount = 0;
				addCompListBody();		//コンピレーション追加ループの中身		}catch(IllegalArgumentException e){
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}

	/**
	 * 全曲リストにコンピレーションなどを追加
	 * @ 無し
	 * Kari_dbから残りを抽出
	 * 初回はaddCompListでreqCode = pt_CompList;
	 * 		compListの読出しindexを初期化
	 * CreateZenkyokuBodyでZenkyoku_dbに書き込み
	 * onSuccessplogTaskからインデックスを加算して再起
	 * 該当レコードが無ければカウントアップして再起
	 * */
	public void addCompListBody() {		//コンピレーション追加ループの中身
		final String TAG = "addCompListBody";
		String dbMsg= "[ZenkyokuList]";
		try{
			dbMsg += ",Zenkyoku_db.isOpen()=" + Zenkyoku_db.isOpen();/////////////////////////////////////
			if( Zenkyoku_db.isOpen() ){
				Zenkyoku_db.close();
				dbMsg +=">>" + Zenkyoku_db.isOpen();/////////////////////////////////////
			}
			String fn = cContext.getString(R.string.kari_file);
//			dbMsg += ",db=" + fn;
//			dbMsg += ">isOpen>" + Kari_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
			if(Kari_db != null){									//全曲の仮ファイル	kari.db
				if(! Kari_db.isOpen()){
					zenkyokuHelper = new ZenkyokuHelper(getApplicationContext() , fn);		//全曲リストの定義ファイル		.this.cContext.
					Kari_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
	//				dbMsg += ">>" + Kari_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
				}
			}
			String comp = compList[comCount];
			dbMsg += ",comp=" + comp;
			pdMessage_stok = pdMessage_stok + "\n(" + (comCount+1) + ")" + comp;	//"">追加</string>

			String c_selection = "SORT_NAME = ? ";			//+ comp ;		//MediaStore.Audio.Media.ARTIST +" <> " + comp;			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
			String c_orderBy= "SORT_NAME,LAST_YEAR,ALBUM,TRACK";	//降順はDESC		YEAR	ALBUM_ARTIST,LAST_YEAR,TRACK
			String[] c_selectionArgs = new String[]{ comp };  			//	 {"%" + artistMei + "%" , albumMei };
			cursor = Kari_db.query(zenkyokuTName, null, c_selection, c_selectionArgs ,null , null,  c_orderBy ,null);	//リString table, String[] columns,new String[] {MotoN, albamN}
			pdMaxVal = cursor.getCount();
			dbMsg += "；" + pdMaxVal + "件";
			pdMessage_stok = pdMessage_stok + String.valueOf(pdMaxVal) + ";" +getResources().getString(R.string.pp_kyoku);	//="">曲</string>
			pdMessage = String.valueOf(pdMessage_stok) ;
	//		dbMsg += "；" + pdMessage;
			if(cursor.moveToFirst()){
//				stmt = Zenkyoku_db.compileStatement("insert into " + zenkyokuTName +
//						"(AUDIO_ID,SORT_NAME,ARTIST,ALBUM_ARTIST,ALBUM,TRACK,TITLE,DURATION,YEAR,DATA,MODIFIED,COMPOSER,LAST_YEAR) values (?,?,?,?,?,?,?,?,?,?,?,?,?);");
//
//				cursor = CreateZenkyokuBody( cursor  , stmt );					//最終リスト作成
////				int id = stmt.executeInsert();
//				dbMsg += "文字[" + id +"]に追加";///////////////////		ZenkyokuList.this.

//				pdMessage = pdMessage_stok + "\n\n" + pd2CoundtVal + ":"+  comp +String.valueOf(pdMaxVal) +this.cContext.getString(R.string.pp_kyoku) + ""+
//						this.cContext.getString(R.string.common_tuika);		///コンピレーション追加
				fn = cContext.getString(R.string.zenkyoku_file);			//全曲リスト+ File.separator +cContext.getString(R.string.zenkyoku_file)
				myLog(TAG,dbMsg );
				pTask = (plogTask) new plogTask(this ,  this).execute(reqCode,  pdMessage_stok , cursor ,null , null , fn );		//,jikkouStep,totalStep,calumnInfo
			}else if(comCount < compList.length-1){
				comCount++;
				addCompListBody();		//再帰でループさせる
			}else{
				addCompListEnd();		//コンピレーション追加終了
			}
		}catch(IllegalArgumentException e){
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}

	/**
	 * コンピレーション追加終了
	 * @ 無し
	 * reNewPLへ
	 * */
	public void addCompListEnd() {		//コンピレーション追加終了	//03-10 09zenkyoku.db' was leaked!  Please fix your application to end transactions in progress properly and to close the database when it is no longer needed.
		final String TAG = "addCompListEnd";
		String dbMsg= "[ZenkyokuList]";
		try{
			cursor.close();
			dbMsg= ",Zenkyoku_db.isOpen()=" + Zenkyoku_db.isOpen();/////////////////////////////////////
			if( Zenkyoku_db.isOpen() ){
				Zenkyoku_db.close();
				dbMsg +=">>" + Zenkyoku_db.isOpen();/////////////////////////////////////
			}
			try {
				dbMsg += " , 全曲リストファイル書き込み= " + all_songs_file_name;
				FileOutputStream fos = new FileOutputStream(new File(all_songs_file_name));
				//openFileOutputパス指定できず だと　/data/data/com.hijiyam_koubou.marasongs/files　にしか書き込めない
				OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
				BufferedWriter writer = new BufferedWriter(osw);
				writer.write(allSonglist);
				writer.close();
				dbMsg += " >>成功";
			} catch (IOException e) {
				e.printStackTrace();
			}

			myLog(TAG,dbMsg );
			reTry=0;					//再処理リミット
			totalEnd( pdMessage_stok );									//データベースを閉じて終了処理

//			CreateArtistList();								//アーティストリストを読み込む(db未作成時は-)
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}
	public SQLiteStatement stmtWrite(Cursor cursor  , SQLiteStatement stmt , String fName , int rfNo) throws IOException{
		int retInt = -1;					//	 ,SQLiteStatement stmt
		final String TAG = "stmtWrite";
		String dbMsg= "[ZenkyokuList]";
		try{
			dbMsg += cursor.getPosition()+ "/" +cursor.getCount() +";" ;
			int tClo = cursor.getColumnIndex(fName);
			dbMsg +=tClo+ "項目目(" +rfNo+ ")" +fName ;
			String rStr = "";
			if( tClo > 0 ){				//getColumnIndexで該当する名称のフィールドが無ければ-1
				rStr = cursor.getString(tClo);
				dbMsg += ";" + rStr ;
				if(rStr != null ){
					if(! rStr.equals("") ){
						stmt.bindString(rfNo , rStr);
					}else {
						stmt.bindString(rfNo ,  "");
					}
				}else{
					stmt.bindString(rfNo ,  "");
				}
			}else{
				stmt.bindString(rfNo ,  "");
			}
//			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return stmt;
	}

	public SQLiteStatement stmtWrite2(String rStr  , SQLiteStatement stmt , int rfNo) throws IOException{
		int retInt = -1;					//	 ,SQLiteStatement stmt
		final String TAG = "stmtWrite2";
		String dbMsg= "[ZenkyokuList]";
		try{
			dbMsg += rStr+ "を" +rfNo +"番目に" ;
			if(rStr != null ){
				if(! rStr.equals("") ){
					stmt.bindString(rfNo , rStr);
					dbMsg += "書き込み" ;
				}else {
					stmt.bindString(rfNo ,  "");
				}
			}else{
				stmt.bindString(rfNo ,  "");
			}
//			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return stmt;
	}

//	/**
//	 * アーティストリスト作成
//	 * @ 無し
//	 * Zenkyoku_dbをSORT_NAMEで group化して
//	 * （変更前	からString[] compListに記載された名称を除いて全曲抽出）
//	 * SORT_NAMEでgroup化してartist_dbへ
//	 * CreateArtistListBodyへ
//	 * reqCode = pt_artistList_yomikomi;
//	 * */
//	public int CreateArtistList(){									//アーティストリストを読み込む(db未作成時は-)
//		int retInt = -1;
//		final String TAG = "CreateArtistList";
//		String dbMsg= "[ZenkyokuList]";
//		try{
//			reqCode = pt_artistList_yomikomi;
//			pd2CoundtVal++;
//			dbMsg = pd2CoundtVal + ")" + reqCode;
//	//		String comp = cContext.getString(R.string.comon_compilation);			//コンピレーション
//			Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
//			zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
////			boolean distinct = true;					//trueを指定すると検索結果から重複する行を削除します。
//			String table =zenkyokuTName;				//テーブル名を指定します。
//			String[] columns = null;						//{  "ALBUM_ARTIST" , "ARTIST"};				// , "ARTIST" 検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
//			String c_selectio =compSelection;			//☆20151228	何故かコンピレーションが追記されないので後で追加
//			String[] c_selectionArgs = compList;		//new String[]{ comp };
//			String groupBy ="SORT_NAME";				//,ALBUM , ALBUM_ARTIST	SORT_NAME
//			String having =null;					//having句を指定します。
//			String orderBy ="_id";					//,LAST_YEAR DESC		"SORT_NAME"
//	//		String limit = null;					//検索結果の上限レコードを数を指定します。
//			cursor = Zenkyoku_db.query( table ,columns, c_selectio,  c_selectionArgs,  groupBy,  having,  orderBy) ;
//
//			retInt = cursor.getCount();
//			dbMsg += "ループ前" + retInt + "件";
//	//		myLog(TAG,dbMsg);
//			if(cursor.moveToFirst()){
//				aArtistList =  new ArrayList<String>();		//アルバムアーティスト
//				artintCo = 0;
//				albamCo = 0;
//				titolCo = 0;
//				compCount = 0;
//				aArtist = "";
//				albumMei = "";
//				artURL = null;
//				if(artist_db != null){
//					if(artist_db.isOpen()){
//						artist_db.close();
//					}
//				}
//				String fn = this.cContext.getString(R.string.artist_file);			//アーティストリスト	artist_db.getPath();
//				dbMsg += "db=" + fn;
//				String pdMessage = pdMessage_stok + "\n\n" + getResources().getString(R.string.medst_artist_make) +//アーティストリスト作成中
//						getResources().getString(R.string.zl_create_artist_list);							//..リストアップするアーティスト名毎の集計\n..コンピレーションなどはリストの末尾に
//				myLog(TAG,dbMsg);
//				pTask = (plogTask) new plogTask(this ,  this).execute(reqCode,  pdMessage , cursor ,null , null , fn );		//,jikkouStep,totalStep,calumnInfo
//			}
//		}catch (Exception e) {
//			myErrorLog(TAG,dbMsg +"で"+e.toString());
//		}
//		return retInt;
//	}
//
//	/**
//	 * ALBUM_ARTISTで付随するアルバム情報を取得
//	 * @ Cursor cursor Zenkyoku_dbからString[] compListに記載された名称を除いて全曲抽出
//	 * @SQLiteStatement stmt
//	 * doInBackgroundでartist_dbに1レコードづつ書き込み
//	 * CreateArtistListEndへ
//	 * */
//	public Cursor CreateArtistListBody(Cursor cursor , SQLiteStatement stmt) throws IOException{		//ALBUM_ARTISTで付随するアルバム情報を取得
//		int retInt = -1;					//	 ,SQLiteStatement stmt
//		final String TAG = "CreateArtistListBody";
//		String dbMsg= "[ZenkyokuList]";
//		try{
//			String dbMsg2 = null;
//			String album_art = null;
//			int first_year = 0;
//			int last_year = 0;
//			int rInt = 0;
//			int maiSuu = 0;		//					albumBk ++;
//			int kyoku = 0;
//			boolean tEnd = false;
//			this.pdCoundtVal = cursor.getPosition()+1;
//		//	progBar1.setProgress(pdCoundtVal);
//		//	dbMsg = pdCoundtVal +"/"+ cursor.getCount() + ")";				//progBar1.getMax()
//			dbMsg = "[" + cursor.getPosition() +"/"+ cursor.getCount() + "]";				//progBar1.getMax()
//			String rStr = null;			// = cursor.getString(cursor.getColumnIndex("ALBUM"));
/////id,1;ARTIST,2;ALBUM_ARTIST,3;ALBUM,4;TRACK,5;TITLE,6;DURATION,7;YEAR,8;DATA,9;MODIFIED,COMPOSER,BOOKMARK,ALBUM_ART,LAST_YEAR) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
//			String ｃArtist = String.valueOf(cursor.getString(cursor.getColumnIndex("ARTIST")));
//			dbMsg += ",ARTIST=" + ｃArtist;
//			String sort = String.valueOf(cursor.getString(cursor.getColumnIndex("SORT_NAME")));		//
//			dbMsg += ",SORT_NAME=" + sort;
//			String aArtist = String.valueOf(cursor.getString(cursor.getColumnIndex("ALBUM_ARTIST")));		//SORT_NAME
//			dbMsg += ",ALBUM_ARTIST=" + aArtist;
//			String kensakuStr = aArtist.toUpperCase();									//大文字化
//			kensakuStr = ORGUT.ArtistPreFix(kensakuStr);					//20140211アーティスト名のTheを取る
//			dbMsg += ">>" + kensakuStr ;
//			String comp = getResources().getString(R.string.comon_compilation).toUpperCase();			//コンピレーション
//			String comp0 = getResources().getString(R.string.comon_compilation0).toUpperCase();		//さまざまなアーティスト
//			String comp1 = getResources().getString(R.string.comon_compilation2).toUpperCase();		//Various Artists
//			String comp2 = getResources().getString(R.string.artist_tuika01).toUpperCase();			//>コンピレーション</string>
//			String comp3 = getResources().getString(R.string.artist_tuika02).toUpperCase();			//サウンドトラック</string>
//			String comp4 = getResources().getString(R.string.artist_tuika03).toUpperCase();			//クラシック</string>
//			String comp5 = getResources().getString(R.string.comon_nuKnow_artist).toUpperCase();		//アーティスト情報なし</string>
//			String comp6 = getResources().getString(R.string.comon_nuKnow);		//unknown</string>
//			String comp7 = comp6.toUpperCase();
//			if( aArtist.equals(comp) || aArtist.equals(comp0) || aArtist.equals(comp1) || aArtist.equals(comp2) || aArtist.equals(comp3) ||
//					 aArtist.equals(comp4) || aArtist.equals(comp5)){																			//
//				rStr = comp;
//			}else{
//				int sIndex =ORGUT.mapIndex(artistList , "sort_name" ,kensakuStr);		//渡された文字が既にリストに登録されていればインデックスを返す
//				dbMsg += ",artistList(" + sIndex + ")" ;
//				if(-1 < sIndex){
//					rStr = String.valueOf(ZenkyokuList.this.artistList.get(sIndex).get("album_artist"));		//最短アーティスト名
//				} else {
//					rStr = ｃArtist;
////BOB MARLEY ;;; 絢香,retINt=-1でjava.lang.IndexOutOfBoundsException: Invalid index 322, size is 322
//				}
//			}
//			dbMsg += "album_artist=" + rStr ;
//			for(String tStr : compList){
//				if( tStr.equals(aArtist) ){
//					aArtist = tStr;
//					dbMsg += ">>" + ｃArtist;
//				}
//			}
//			stmt = stmtWrite2(ｃArtist ,  stmt , 1);							//1;ARTIST
//			stmt = stmtWrite2(aArtist ,  stmt , 2);				//2;ALBUM_ARTIST
////ALBUM_ARTIST=STEVIE RAY VAUGHAN(18文字)、書込み済みかfalse(74人目)STEVIE RAY VAUGHAN(Stevie Ray Vaughan & Double Trouble(109文字)>クレジット変更>Stevie Ray Vaughan(109文字)
//			String rArtist = aArtist;
//			String rAlbum = cursor.getString(cursor.getColumnIndex("ALBUM"));
//			dbMsg +=", ALBUM="+ rAlbum;
//			stmt = stmtWrite(cursor ,  stmt , "ALBUM" , 3);
//	//			myLog(TAG,dbMsg);
//				dbMsg2 = dbMsg;
//	//			while( aArtist.equals(rArtist) ){
//					dbMsg = rArtist;
//					zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
//					if(! Zenkyoku_db.isOpen()){
//						Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
//					}
////					boolean distinct = true;					//trueを指定すると検索結果から重複する行を削除します。
//					String table =zenkyokuTName;				//テーブル名を指定します。
//					String[] columns = null;						//{  "ALBUM_ARTIST" , "ARTIST"};				// , "ARTIST" 検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
//					String c_selectio ="SORT_NAME = ? ";			//☆20151228	何故かコンピレーションが追記されないので後で追加
//					String[] c_selectionArgs = { sort };		//new String[]{ comp };
//					String groupBy =null;				//,ALBUM , ALBUM_ARTIST	SORT_NAME
//					String having =null;					//having句を指定します。
//					String orderBy ="YEAR";					//,LAST_YEAR DESC		"SORT_NAME"
//			//		String limit = null;					//検索結果の上限レコードを数を指定します。
//					Cursor cursor_p2 = Zenkyoku_db.query( table ,columns, c_selectio,  c_selectionArgs,  groupBy,  having,  orderBy) ;
//					kyoku = kyoku + cursor_p2.getCount();
//					dbMsg += ",NUMBER_OF_SONGS="+ kyoku;
//		//			first_year =  Integer.valueOf(cursor_p2.getString(cursor_p2.getColumnIndex("YEAR")));
//					groupBy ="ALBUM";				//, , ALBUM_ARTIST	SORT_NAME
//					cursor_p2 = Zenkyoku_db.query( table ,columns, c_selectio,  c_selectionArgs,  groupBy,  having,  orderBy) ;
//					maiSuu = maiSuu + cursor_p2.getCount();
//					dbMsg += ":"+ maiSuu + "枚";
//					if( cursor_p2.moveToFirst() ){
//						do{
//							rAlbum = cursor_p2.getString(cursor.getColumnIndex("ALBUM"));
//							dbMsg +=", ALBUM="+ rAlbum;
//							Uri cUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
//							String[] c_columns = null;		 		//③引数columnsには、検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
//							String c_selection =  MediaStore.Audio.Albums.ARTIST +" LIKE ?  AND " + MediaStore.Audio.Albums.ALBUM +" = ?";			//
//							String[] c_selectionArgs2= { "%" + aArtist + "%"  , rAlbum };   			//
//							String c_orderBy=MediaStore.Audio.Albums.FIRST_YEAR  ; 			//LAST_YEAR	降順はDESC
//							Cursor cursor_3 = getContentResolver().query( cUri , c_columns , c_selection , c_selectionArgs2, c_orderBy);
//							if( cursor_3.moveToFirst() ){
//								if(album_art == null){
//									album_art = cursor_3.getString(cursor_3.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
//								}
//								rStr = cursor_3.getString(cursor_3.getColumnIndex(MediaStore.Audio.Albums.FIRST_YEAR));
//								dbMsg += ",FIRST_YEAR="+ rStr;
//								if( rStr != null ){
//									rInt  = Integer.parseInt(rStr);
//									if(first_year == 0){
//										first_year =rInt;
//									}else if(rInt < first_year ){
//										first_year =rInt;
//									}
//									dbMsg += ">>"+ first_year;
//								}
//								rStr = cursor_3.getString(cursor_3.getColumnIndex(MediaStore.Audio.Albums.LAST_YEAR));
//								dbMsg += ",LAST_YEAR="+ rInt;
//								if( rStr != null ){
//									rInt  = Integer.parseInt(rStr);
//									if(last_year < rInt){
//										last_year = rInt;
//									}else if(last_year < first_year){
//										last_year = 0;
//									}
//									dbMsg += ">>"+ last_year;
//								}
//							}
//							cursor_3.close();
//						}while( cursor_p2.moveToNext() );
//					}				//if( cursor_p2.moveToFirst() ){
//					cursor_p2.close();
//					Zenkyoku_db.close();
//
//					if(cursor.moveToNext()){
//						dbMsg =cursor.getPosition()+ "/"+ cursor.getCount();
//						rArtist = cursor.getString(cursor.getColumnIndex("ALBUM_ARTIST"));
//						dbMsg +=")"+ rArtist;
//	//					myLog(TAG,dbMsg);
//					}else{
//						rArtist = "";
//					}
//		//		}					//while( aArtist.equals(rArtist) ){
//				if( ! aArtist.equals(rArtist) ){
//					if( cursor.getCount()>( cursor.getPosition()+2)){
//						cursor.moveToPrevious();
//					}
//				}
//
//				dbMsg =dbMsg2+ ", ALBUM_ART="+ album_art;
//				stmt = stmtWrite2( album_art  , stmt , 4);
//				String sub_text = "";
//				if(first_year > 0){
//					sub_text = String.valueOf(first_year);
//					if(last_year> first_year){
//						sub_text = sub_text +" . . . ";
//					}else{
//						sub_text = "";
//					}
//				}
//				if(last_year > 0){
//					sub_text = sub_text + last_year;
//				}
//				sub_text = sub_text  +"  ";
//				sub_text = sub_text + maiSuu +this.cContext.getString(R.string.pp_mai)+ "/" + kyoku  +this.cContext.getString(R.string.pp_kyoku); 		//○枚/○曲;
//				dbMsg += ",sub_text="+ sub_text;
//				stmt = stmtWrite2( sub_text  , stmt , 5);
//		//		stmt.bindString(5, sub_text);					//5.アーティストリスト生成用の集約情報
//								//SUB_TEXT	//ARTIST,ALBUM_ARTIST,ALBUM,ALBUM_ART,YEAR,NOS,SORT_NAME,SUB_TEXT
//				albamCo = albamCo + maiSuu;
//				dbMsg += ",合計："+ albamCo +"枚";
//				titolCo = titolCo + kyoku;
//				dbMsg += + titolCo +"曲";
//	//		}
//			artistlist += aArtist + "\n";
//			dbMsg += "、リスト" + ZenkyokuList.this.aArtistList.size() +"件";
//			myLog(TAG,dbMsg);
//		}catch (Exception e) {
//			myErrorLog(TAG,dbMsg +"で"+e.toString());
//		}
//		return cursor;
//	}
//
//	public String toCredit( String aArtist){			//同じアルバムアーティストから最適な表記名を返す
//		final String TAG = "toCredit";
//		String dbMsg= "[ZenkyokuList]";
//		dbMsg += "最適な表記名を返す";/////////////////////////////////////
//		String  retStr = null;			//nullで初期化すると先頭にnullが残る
//		try{
//			dbMsg= aArtist+"は";
//			Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
//			zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
////			boolean distinct = true;					//trueを指定すると検索結果から重複する行を削除します。
//			String table =zenkyokuTName;				//テーブル名を指定します。
//			String[] columns = null;						//{  "ALBUM_ARTIST" , "ARTIST"};				// , "ARTIST" 検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
//			String c_selection = "SORT_NAME = ?";			//+ comp ;		//MediaStore.Audio.Media.ARTIST +" <> " + comp;			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
//			String[] c_selectionArgs = new String[]{ aArtist };  			//{ comp }; 		 {"%" + artistMei + "%" , albumMei };
//			String groupBy =null;			//"ALBUM_ARTIST,ALBUM";
//			String having =null;					//having句を指定します。
//			String orderBy ="SORT_NAME";					//,LAST_YEAR DESC
//	//		String limit = null;					//検索結果の上限レコードを数を指定します。
//			Cursor cursor = Zenkyoku_db.query( table ,columns, c_selection,  c_selectionArgs,  groupBy,  having,  orderBy) ;
//			dbMsg= dbMsg +cursor.getCount()+"件";
//			if(cursor.moveToFirst()){
//				do{
//					String ｃArtist = String.valueOf(cursor.getString(cursor.getColumnIndex("ARTIST")));
//					String ｃArtistUp = ｃArtist.toUpperCase();
//					//	dbMsg += ">表記名を大文字化して>" + ｃArtistUp;
//						ｃArtistUp =ORGUT.ArtistPreFix(ｃArtistUp);	//TheとFeat以下をカット
//						dbMsg += ">ArtistPreFix>" + ｃArtistUp ;
//					if(ｃArtistUp.equals(aArtist)){
//						retStr = ｃArtist;
//						cursor.moveToLast();
////					}else if(retStr == null){
////						retStr = ｃArtist;
////					}else if(retStr.length() > ｃArtist.length()){
////						retStr = ｃArtist;
//					}
//				}while(cursor.moveToNext());
//			}
//			dbMsg +="\n[" +cursor.getPosition()+"/" + cursor.getCount() +"]" + retStr;
//			cursor.close();
//			Zenkyoku_db.close();
//			myLog(TAG,dbMsg);
//		} catch (Exception e) {
//			myErrorLog(TAG,dbMsg+"で"+e);
//		}
//	return retStr;
//	}
//
//	/**
//	 * アーティストリスト作成終了
//	 * @ 無し
//	 * addCompArtistListで除外した名称を書き込み
//	 * totalEndへ
//	 * */
//	public void CreateArtistListEnd() {		//アーティストリスト作成終了
//		final String TAG = "CreateArtistListEnd";
//		String dbMsg= "[ZenkyokuList]";
//		try{
//			int compKyokusuuTotal = 0;
//			String fn = cContext.getString(R.string.zenkyoku_file);			//全曲リスト+ File.separator +cContext.getString(R.string.zenkyoku_file)
////			dbMsg += ",db=" + fn;
//			zenkyokuHelper = new ZenkyokuHelper(getApplicationContext() , fn);		//全曲リストの定義ファイル		.this.cContext.
//			Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
//			zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
////			boolean distinct = true;					//trueを指定すると検索結果から重複する行を削除します。
//			String table =zenkyokuTName;				//テーブル名を指定します。
//			String[] columns = null;						//{  "ALBUM_ARTIST" , "ARTIST"};				// , "ARTIST" 検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
//			String c_selection = "ALBUM_ARTIST = ? ";			//+ comp ;		//MediaStore.Audio.Media.ARTIST +" <> " + comp;			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
//			for(String comp : compList ){
//				if(! Zenkyoku_db.isOpen()){
//					Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
//				}
//				dbMsg += "；comp=" + comp;
//				String[] c_selectionArgs = new String[]{ comp };  			//	 {"%" + artistMei + "%" , albumMei };
//				String groupBy ="ALBUM_ARTIST,ALBUM";
//				String having =null;					//having句を指定します。
//				String orderBy ="ALBUM_ARTIST,LAST_YEAR";
//		//		String limit = null;					//検索結果の上限レコードを数を指定します。
//				cursor = Zenkyoku_db.query( table ,columns, c_selection,  c_selectionArgs,  groupBy,  having,  orderBy) ;
//				int compKyokusuu = cursor.getCount();
//				dbMsg += "," + comp + ":" + compKyokusuu + "人";
//				if(cursor.moveToFirst()){
//					compKyokusuuTotal = compKyokusuuTotal + compKyokusuu;
//		//			myLog(TAG,dbMsg );
//					addCompArtistList(comp , cursor);		//アーティストリストコンピレーション追加
//				}
//				cursor.close();
//			}
//			Zenkyoku_db.close();
////			File dbF = cContext.getDatabasePath(fn);			//new File(fn);		// Environment.getExternalStorageDirectory().getPath();
////			artistHelper = new ArtistHelper(cContext , fn);		//アーティスト名のリストの定義ファイル		.
////			dbMsg += " , artistHelper =" + artistHelper;
////			artist_db.close();
////			artist_db = cContext.openOrCreateDatabase(fn, cContext.MODE_PRIVATE, null);	//String path, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler				//アーティスト名のえリストファイルを読み書きモードで開く
//			dbMsg += " , artist_db =" + artist_db;				//SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/artist.db；
//			dbMsg += "；アーティストリストテーブル=" + artistTName;
//			artist_db = artistHelper.getReadableDatabase();			// データベースをオープン
//			dbMsg += " , artist_db = " + artist_db;//////
//			cursor = artist_db.query(artistTName, null , null , null , null, null , null);	//( table, columns, selection, selectionArgs, groupBy, having, orderBy)
//			pdMaxVal = cursor.getCount();
//			dbMsg +=";; " + pdMaxVal + "人 ; " + cursor.getColumnCount() + "項目";//////
//			long end=System.currentTimeMillis();						// 終了時刻の取得
//			String dousaJikann = ORGUT.sdf_mss.format( (int)((end - startPart)));
//			pdMessage_stok = pdMessage_stok + "\n\n" + pd2CoundtVal + ":" +
//					getResources().getString(R.string.common_artist_list)  + pdMaxVal + this.cContext.getString(R.string.comon_nin);		//追加</string>
////					this.cContext.getString(R.string.pp_artist)+	//アーティスト○○人追加
////					albamCo + this.cContext.getString(R.string.pp_mai) +souKyokuSuu + this.cContext.getString(R.string.pp_kyoku) ;			//○枚○曲
//			pdMessage_stok = pdMessage_stok +"[" +dousaJikann + "mS]";		//所要時間
//			dbMsg +=  pdMessage_stok ;
//			reqCode = pt_end;
//			cursor.close();
//
//			try {
//				dbMsg += " , アーティストリストファイル書き込み= " + album_artist_file_name;
//                FileOutputStream fos = new FileOutputStream(new File(album_artist_file_name));
//				OutputStreamWriter osw = new OutputStreamWriter(fos);
//				BufferedWriter writer = new BufferedWriter(osw);
//				writer.write(artistlist);
//				writer.close();
//				dbMsg += " >>成功";
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//			myLog(TAG,dbMsg );
//
//
//			totalEnd( pdMessage_stok );									//データベースを閉じて終了処理
//		}catch (Exception e) {
//			myErrorLog(TAG,dbMsg +"で"+e.toString());
//		}
//	}

	/**
	 * アーティストリストコンピレーション追加
	 * CreateArtistListEndから呼ばれる
	 * */
	public void addCompArtistList(String comp , Cursor cursor) {		//アーティストリストコンピレーション追加
		final String TAG = "addCompArtistList";
		String dbMsg= "[ZenkyokuList]";
		try{
			artistTName = cContext.getString(R.string.artist_table);			//artist_table
			dbMsg += "；アーティストリストテーブル=" + artistTName;
			String fn = cContext.getString(R.string.artist_file);			//アーティストリスト	artist_db.getPath();
			dbMsg += "db=" + fn;
			artistHelper = new ArtistHelper(cContext , fn);		//アーティスト名のリストの定義ファイル		.
			artist_db = artistHelper.getWritableDatabase();			// データベースをオープン
			artist_db.beginTransaction();
			SQLiteStatement stmt = null;
			stmt = artist_db.compileStatement("insert into " + artistTName +
					"(ARTIST,ALBUM_ARTIST,ALBUM,ALBUM_ART,SUB_TEXT) values (?, ?, ?, ?, ?);");
			if( cursor.moveToFirst() ){
				cursor = CreateArtistListBody(cursor , stmt ) ;				//ALBUM_ARTISTで付随するアルバム情報を取得
				long id = 0;
				id = stmt.executeInsert();
				dbMsg += "文字[" + id +"]に追加";///////////////////		ZenkyokuList.this.
				try{
					dbMsg= "sql_db = " + artist_db;//////
					artist_db.setTransactionSuccessful();
				} finally {
					artist_db.endTransaction();
				}
			}
		//	cursor.close();
			artist_db.close();
			myLog(TAG,dbMsg );
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}

	public SQLiteStatement dataAlbumWright( String artist_ID , String artistMei , String albumMei , List<String> tList , SQLiteStatement stmt) throws IOException {		//アルバム１枚分の抽出
		final String TAG = "dataAlbumWright";
		String dbMsg= "[ZenkyokuList]";
		try{
			dbMsg += "[" + artist_ID + "]" + artistMei +" ; " + albumMei;/////////////////////////////////////
			String comp_sezu = cContext.getString(R.string.comon_comp_sezu);			//などなど
			ContentValues cv_sezu = new ContentValues();				//
			String selection_al = MediaStore.Audio.Media.IS_MUSIC + " = 1" ;
			String kensakuStr = artistMei;
			if( artistMei != null ){
				if(artistMei.endsWith(comp_sezu)){													//アルバムアーティスト不参加曲を含むアルバムは
					artistMei = artistMei.substring(0, artistMei.length()-comp_sezu.length());		//などなど除去
					dbMsg= dbMsg +">>" + artistMei;/////////////////////////////////////
					cv_sezu.put("ARTIST", artistMei);
					int uID = artist_db.update("artist_table", cv_sezu, "_id = " + artist_ID, null);
					dbMsg= dbMsg +"[" + uID +"]";/////////////////////////////////////
//							myLog(TAG,dbMsg );
				}else{
					kensakuStr = ORGUT.checkRepChr( kensakuStr, "'", "%");	//第一引数の文字列に第二引数で指定した文字があればを第三引数の文字列に置き換えて返す
					kensakuStr = ORGUT.checkRepChr( kensakuStr, ".", "%");
					kensakuStr = ORGUT.checkRepChr( kensakuStr, "(", "%");
					kensakuStr = ORGUT.checkRepChr( kensakuStr, ")", "%");
					if(artistMei.equals(kensakuStr)){
						selection_al =  selection_al + " AND " + MediaStore.Audio.Media.ARTIST + " LIKE '%"+ artistMei + "%'" ;
					}else{
						selection_al =  selection_al + " AND " + MediaStore.Audio.Media.ARTIST + " LIKE '%" + kensakuStr + "%'" ;
					}
				}
			}
			kensakuStr = albumMei;
			if( kensakuStr != null ){
				kensakuStr = ORGUT.checkRepChr( kensakuStr, "'", "%");	//第一引数の文字列に第二引数で指定した文字があればを第三引数の文字列に置き換えて返す
				kensakuStr = ORGUT.checkRepChr( kensakuStr, ".", "%");
				kensakuStr = ORGUT.checkRepChr( kensakuStr, "(", "%");
				kensakuStr = ORGUT.checkRepChr( kensakuStr, ")", "%");
			}
			if(albumMei.equals(kensakuStr)){
				selection_al = selection_al + " AND " + MediaStore.Audio.Media.ALBUM + " = '" + kensakuStr + "'";
			}else{
				selection_al = selection_al + " AND " + MediaStore.Audio.Media.ALBUM + " LIKE '%" + kensakuStr + "%'";
			}
			dbMsg= dbMsg +"," + selection_al;/////////////////////////////////////
			String sortOrdre = MediaStore.Audio.Media.TRACK;	//MediaStore.Audio.Media.ALBUM +" , " + MediaStore.Audio.Media.YEAR +" , " +
			Cursor cur = cContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null , selection_al , null, sortOrdre);		// 外部ストレージから音楽を検索
			dbMsg +=" , " + cur.getCount() + "件";/////////////////////////////////////
		//		myLog(TAG,dbMsg );
			ArrayList<String> albumList = new ArrayList<String>();
			albumList.clear();
			if (cur.moveToFirst()) {
				String comp = cContext.getString(R.string.comon_compilation);			//コンピレーション
				String nData = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
				dbMsg +=" , " + nData;/////////////////////////////////////
				if(!nData.equals("") || nData != null){
					int albumColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
					String nAlbum = cur.getString(albumColumn);
					if(! ORGUT.isInListString(albumList, nAlbum)){					//アルバムに重複がなければ追記
						albumList.add(nAlbum);
						tList.clear();
						bAlbum = "";
						trackCount =1;

						if( artistMei == null ){
							artistMei = comp;			//コンピレーション
						}
						do {					// リストに追加
							String tNmae = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
							if(artistMei.equals(comp)){
								stmt = dataRecordWright( stmt , cur , artistMei);					//データリストのレコード書き込み
								long kyokusuu = stmt.executeInsert();
								dbMsg += ">>" + kyokusuu +"行目";/////////////////////////////////////////////////////////////////////
	//							myLog(TAG,dbMsg );
							}else{
								if(! ORGUT.isInListString(tList, tNmae)){					//タイトルに重複がなければ追記
									tList.add(tNmae);
									stmt = dataRecordWright( stmt , cur , artistMei);					//データリストのレコード書き込み
									long kyokusuu = stmt.executeInsert();
									dbMsg += ">>" + kyokusuu +"行目";/////////////////////////////////////////////////////////////////////
		//							myLog(TAG,dbMsg );
								}
							}

						} while (cur.moveToNext());
						ContentValues cv_ac = new ContentValues();
						cv_ac.put("NOS", String.valueOf(tList.size()));
						artist_db.update("artist_table", cv_ac, "_id = " + artist_ID, null);		//アルバム内の曲数更新
					}
				}		//		if(! ORGUT.isInListString(albumList, nAlbum)){					//アルバムに重複がなければ追記
			}			//if (cur.moveToFirst()) {
			cur.close();
		}catch(IllegalArgumentException e){
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return stmt;
	}

	public String bAlbum;
	public int trackCount;
	public SQLiteStatement dataRecordWright( SQLiteStatement stmt , Cursor cur , String artist_r) {					//データリストのレコード書き込み
		final String TAG = "dataRecordWright";
		String dbMsg= "[ZenkyokuList]";
		try{
			dbMsg = "[" +cur.getPosition()  +"/" + cur.getCount() +"枚目]" ;/////////////////////////////////////////////////////////////////////////////////////////////
			int readint = cur.getInt(cur.getColumnIndex(MediaStore.Audio.Media._ID));
			dbMsg= "_ID=" + readint;/////////////////////////////////////
			ContentResolver resolver = cContext.getContentResolver();	//c.getContentResolver();
			Uri cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
			String where = MediaStore.Audio.Media._ID + "= ?";
			String[] selectionArgs = {String.valueOf(readint)};
			String rData =cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
			Map<String, String> map = ORGUT.data2msick(rData, getApplicationContext());		//URLからアーティスト～拡張子を分離させて返す

			String readStr = String.valueOf(readint);
			dbMsg +=">>" + readStr;/////////////////////////////////////
			stmt.bindString(1, readStr);		//1;id
			readStr = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ARTIST));
			dbMsg +="]" + readStr;/////////////////////////////////////
			stmt.bindString(2, readStr);						//2;ARTIST
			dbMsg +="(" + artist_r +")";/////////////////////////////////////
			stmt.bindString(3, artist_r);						//3;ALBUM_ARTIST
			readStr = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM));
			dbMsg +=readStr ;/////////////////////////////////////
			stmt.bindString(4, readStr);							//ALBUM
			if(bAlbum.equals(readStr)){
				trackCount++;
			}else{
	//			myLog(TAG,bAlbum +"("+trackCount +") >>"+ readStr );
				trackCount=1;
				bAlbum = readStr;
			}
			boolean kakikae = false;
			String rStr = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TRACK));
			dbMsg += " ,[MediaStoreで " + rStr ;
			String rTRACK = map.get( "trackNo" );
			dbMsg += ",ファイル名から" + rTRACK;
			if(rTRACK == null){
				rTRACK = String.valueOf(trackCount);
				if(rTRACK.length() == 1){
					rTRACK="0"+rTRACK;
				}
				dbMsg += ">>ら" + rTRACK;
			} else{
				rTRACK = UTIL.checKTrack( rTRACK);
			}
			if( rStr == null){
				kakikae = true ;
			}else if( rStr.equals("") || rStr.equals("<unknown>") || rStr.equals("0")){
				kakikae = true ;
			}else{
				if(rStr.length() == 1){
					rStr="0"+rStr;
				}
				if(! rStr.equals(rTRACK)){
					kakikae = true ;
				}
			}
			if(kakikae){
				dbMsg += ">>" + rTRACK;/////////////////////////////////////////////////////////////////////////////////////////////
				ContentValues cv = new ContentValues();
				cv.put(MediaStore.Audio.Media.TRACK, rTRACK);
				int rows = resolver.update(cUri, cv , where, selectionArgs);
				dbMsg += "]処理" + rows +"件";/////////////////////////////////////////////////////////////////////////////////////////////
	//			myLog(TAG,dbMsg);
			}
			stmt.bindString(5, rTRACK);			//TRACK
			String nTitol = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
			dbMsg +=nTitol ;/////////////////////////////////////
			stmt.bindString(6, nTitol);							//TITOL
			stmt.bindString(7, cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DURATION)));	//DURATION
			rStr = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.YEAR));
			dbMsg +=",YEAR=" +rStr ;/////////////////////////////////////
			if(rStr != null){
				stmt.bindString(8, cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.YEAR)));			// = cur.getColumnIndex(MediaStore.Audio.Media.YEAR);
			}else{
				stmt.bindString(8, "");			// = cur.getColumnIndex(MediaStore.Audio.Media.YEAR);
			}
			stmt.bindString(9, cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA)));			//DATA
//			stmt.bindString(9, rData);			//DATA
			stmt.bindString(10, cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)));			//MODIFIED

			readStr = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.COMPOSER));
			dbMsg +=",composer=" + readStr ;/////////////////////////////////////
			if( readStr != null){
				stmt.bindString(11, readStr);			//COMPOSER
			}else{
				stmt.bindString(11, "");			//COMPOSER
			}
			readStr = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.BOOKMARK));
			dbMsg +=",bOOKMARK=" + readStr ;/////////////////////////////////////
//			readStr = cur.getString(cur.getColumnIndex(MediaStore.MediaColumns.ALBUM_ARTIST));
//			dbMsg +=",ALBUM_ARTIST=" + readStr ;

			if( readStr != null){
				stmt.bindString(12, readStr);			//BOOKMARK
			}else{
				stmt.bindString(12, "");
			}

			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return stmt;
	}

	public boolean compNameCheck(String tStr) {	// コンピレーションなどアーティストリストの末尾に並べる名前か
		boolean retBool =false;
		final String TAG = "compNameCheck";							//long seleID  ,, int hennkou, String seleItem
		String dbMsg= "[ZenkyokuList]";
		try{
			long end=System.currentTimeMillis();		// 終了時刻の取得
			String dousaJikann = ORGUT.sdf_mss.format( (int)((end - startPart)));
			long start = System.currentTimeMillis();
			dbMsg = tStr;
			String comp = cContext.getString(R.string.comon_compilation);			//コンピレーション
			dbMsg +="("+ comp;
			String comp0 = cContext.getString(R.string.comon_compilation0);	//さまざまなアーティスト
			dbMsg +=","+ comp0;
			String comp1 = cContext.getString(R.string.comon_compilation1);			//VARIOUS ARTISTS
			dbMsg +=","+ comp1 +")";
			String comp2 = cContext.getString(R.string.comon_compilation2);			//Various Artists
			dbMsg +=","+ comp1 +")";
			if(tStr.equals(comp) ||
				tStr.equals(comp0) ||
				tStr.equals(comp1) ||
				tStr.equals(comp2)){
				retBool = true;
			}
			dbMsg +=">>"+ retBool;
			dbMsg=dbMsg +";"+dousaJikann + "m秒で終了";
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return retBool;
	}

	public SQLiteStatement compNameAdd(Uri cUri , SQLiteStatement stmt ,
								int titolCount , List<String> albumOfArtist) {	// コンピレーションなどを指定されたdbの末尾に追記する
		final String TAG = "compNameAdd";							//long seleID  ,, int hennkou, String seleItem
		String dbMsg= "[ZenkyokuList]";
		try{
			long end=System.currentTimeMillis();		// 終了時刻の取得
			long start = System.currentTimeMillis();
			String stdComp = "";

			stdComp = cContext.getString(R.string.comon_compilation);			//コンピレーション
			ArrayList<String> compNameList = new ArrayList<String>();
			compNameList.clear();

			if(0 < compGenList.size()){
				for(String Junl : genleList){
					for(String comp : compGenList){
						if(comp.equals(Junl)){
							compNameList.add(comp);
							//							dbMsg += "（"+artistList.size() +"）"+ comp;
						}
					}
				}
			}


			compNameList.add(cContext.getString(R.string.comon_compilation2));	//Various Artists
			compNameList.add(cContext.getString(R.string.comon_compilation1));	//VARIOUS ARTISTS
			compNameList.add(cContext.getString(R.string.comon_compilation0));	//さまざまなアーティスト
			compNameList.add(stdComp);													//コンピレーション
			for(String cName : compNameList){
				dbMsg=compNameList + "を追加";
				String c_selection =  MediaStore.Audio.Media.ARTIST +" LIKE '%" + cName + "%'";			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
				String c_orderBy = MediaStore.Audio.Albums.ALBUM; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
				Cursor cursor = cContext.getContentResolver().query( cUri , null , c_selection , null, c_orderBy);
				int retInt = cursor.getCount() ;
				dbMsg= cName + "は" + retInt + "枚分";
		//		myLog(TAG,dbMsg);
				if(cursor.moveToFirst()){
					do{
						dbMsg = "[" + cursor.getPosition() + "/" + cursor.getCount() +"]";/////////////////////////////////////////////////////////////////////
						String artistN = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
						String albumN = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
						dbMsg += artistN+"/" + albumN;/////////////////////////////////////////////////////////////////////
						String aOa = albumN +" of "+artistN;
						if(! ORGUT.isInListString(albumOfArtist, aOa)){				//既に書き込まれていなければ
							albumOfArtist.add(aOa);		//アルバムの参加アーティスト名
							stmt = albumRecordWright( stmt , cursor , stdComp , titolCount );	//アルバムリストのレコード書き込み//								stmt.bindString(1, aName);	//MediaStore.Audio.Albums.ARTIST		cv.put("ARTIST", aName);
							dbMsg = "stmt =" +stmt.toString().length();/////////////////////////////////////////////////////////////////////
	//						long id =stmtExecuteInsert();			//handlerを使ったSQLiteStatement書き込み
			//				dbMsg += "文字[" + id +"]に追加";/////////////////////////////////////////////////////////////////////
//								myLog(TAG,dbMsg);
							long id = stmt.executeInsert();
							dbMsg += "[" + id +"]に追加";/////////////////////////////////////////////////////////////////////
						}
		//				myLog(TAG,dbMsg);
					}while(cursor.moveToNext());
					cursor.close();
				}
			}
			dbMsg=dbMsg +";"+ (int)((end - start)) + "m秒で終了";
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return stmt;
	}

	/**	アルバムリストのレコード書き込み
	*	呼ばれ元 dbSakuseiBody , compNameAdd
	**/
		public SQLiteStatement albumRecordWright( SQLiteStatement stmt , Cursor cursor , String aName , int titolCount) {
			final String TAG = "albumRecordWright[ZenkyokuList]";
			String dbMsg= "[ZenkyokuList]";
			try{
				dbMsg += "[" +cursor.getPosition()  +"/" + cursor.getCount() +"枚目]" ;/////////////////////////////////////////////////////////////////////////////////////////////
				String  cArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
				dbMsg +=cArtist ;/////////////////////////////////////////////////////////////////////////////////////////////
				if(cArtist == null){
					cArtist = "";
				}else{
					cArtist = cArtist.substring(0, 1).toUpperCase() + cArtist.substring(1);
				}
				String lArtist = aName;
//				if(cArtist.startsWith("The")){				//Pattern.compile("The").matcher(aName).find() && cArtist.startsWith("The")では大文字小文字判定がされない
//					lArtist = "The " + aName;
//				}else if(cArtist.startsWith("the")){		//Pattern.compile("the").matcher(aName).find() &&
//					lArtist = "the " + aName;
//				}else if(cArtist.startsWith("THE")){			//cArtist.startsWith("THE") &&
//					lArtist = "THE " + aName;
//				}
				stmt.bindString(1, lArtist);	//MediaStore.Audio.Albums.ARTIST		cv.put("ARTIST", aName);
				stmt.bindString(2, cArtist);	//クレジットアーティスト;
				String albumMei = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
				dbMsg += " , " +albumMei ;/////////////////////////////////////////////////////////////////////////////////////////////
				if(albumMei == null){
					albumMei ="";
				}
				stmt.bindString(3, albumMei);	//MediaStore.Audio.Albums.ALBUM
				String wStr = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
				dbMsg += " , " +wStr ;/////////////////////////////////////////////////////////////////////////////////////////////
				if(wStr == null){
					wStr ="";
				}
				stmt.bindString(4, wStr);
				wStr = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.FIRST_YEAR));
				if( wStr == null ){
					wStr = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.LAST_YEAR));
				}
				if( wStr == null ){
					wStr = "";
				}
				stmt.bindString(5, wStr);
				wStr = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS));				//NOS
				if( wStr != null){
					int kyokusuu =  Integer.valueOf(wStr);
					if(kyokusuu<10){
						wStr = "0" + wStr;
					}
					dbMsg += ";"  + kyokusuu + "曲";	/////////////////////////////////////////////////////////////////////
					titolCount = titolCount + kyokusuu;
				}
				stmt.bindString(6, wStr);
				aName = ORGUT.ArtistPreFix( aName );						//Ｔｈｅ抜き
				aName = aName.toUpperCase();
				dbMsg += ">"  + aName;	/////////////////////////////////////////////////////////////////////
				stmt.bindString(7, aName);
				myLog(TAG,dbMsg );
			}catch (Exception e) {
				myErrorLog(TAG,dbMsg +"で"+e.toString());
			}
			return stmt;
		}

//A SQLiteConnection object for database '/data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db' was leaked!  Please fix your application to end transactions in progress properly and to close the database when it is no longer needed.
//A SQLiteConnection object for database '/data/data/com.hijiyam_koubou.marasongs/databases/shyuusei.db' was leaked!  Please fix your application to end transactions in progress properly and to close the database when it is no longer needed.
		/**	終了表示とデータベースをクローズ
		*	このクラスを終了
		**/
	public void totalEnd( CharSequence pdMessage_stok ) {		//データベースを閉じて終了処理
		final String TAG = "totalEnd";
		String dbMsg= "[ZenkyokuList]";
		try{
			long end=System.currentTimeMillis();		// 終了時刻の取得
			String dousaJikann = ORGUT.sdf_mss.format( (int)((end - start)));
			dbMsg += ",Zenkyoku_db.isOpen()=" + Zenkyoku_db.isOpen();/////////////////////////////////////
			if( Zenkyoku_db.isOpen() ){
				Zenkyoku_db.close();
				dbMsg +=">>" + Zenkyoku_db.isOpen();/////////////////////////////////////
			}
			dbMsg +=",artist_db.isOpen()=" + artist_db.isOpen();/////////////////////////////////////
			if( artist_db.isOpen() ){
				artist_db.close();
				dbMsg +=">>" + artist_db.isOpen();/////////////////////////////////////
			}
			dbMsg +=",Kari_db.isOpen()=" + Kari_db.isOpen();/////////////////////////////////////
			if( Kari_db.isOpen() ){
				Kari_db.close();
				dbMsg += ">>" + Kari_db.isOpen();/////////////////////////////////////
			}
			dbMsg +=",shyuusei_db.isOpen()=" + shyuusei_db.isOpen();/////////////////////////////////////
			if( shyuusei_db.isOpen() ){
				shyuusei_db.close();
				dbMsg += ">>" + shyuusei_db.isOpen();/////////////////////////////////////
			}
			pdMessage_stok = this.cContext.getString(R.string.zenkyoku_end_msg) ;			//お待たせしました。</string>
			pdMessage_stok = pdMessage_stok + "\n\n"  + kyoku +  this.cContext.getString(R.string.pp_kyoku);		//ame="">曲</string>
			pdMessage_stok = pdMessage_stok +"\n["+this.cContext.getString(R.string.comon_syoyoujikan)+";"+dousaJikann + "mS]";		//	<string name="">所要時間</string>
			Intent data = new Intent();			// 返すデータ(Intent&Bundle)の作成
			Bundle bundle = new Bundle();
//			dbMsg= "pdMaxVal=;"+ pdMaxVal;/////////////////////////////////////
//			bundle.putString("key.retStr", String.valueOf(pdMessage_stok));				//最終メッセージ
			bundle.putString("dMessege",String.valueOf(pdMessage_stok));
			data.putExtras(bundle);
			setResult(RESULT_CANCELED, data);		// setResult() で bundle を載せた送るIntent dataをセットする	// 第一引数は…Activity.RESULT_OK, Activity.RESULT_CANCELED など
			ZenkyokuList.this.finish();

				dbMsg= String.valueOf(pdMessage_stok);/////////////////////////////////////
			dbMsg +=","+ pgd_msg_tv.getText();/////////////////////////////////////
			myLog(TAG,dbMsg );
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}
	//@Override
	public void onSuccessplogTask(int reqCode) {			//プログレスが終わった時に発生	(AsyncTaskResult<Object>) Object... myResult
		final String TAG = "onSuccessplogTask";
		String dbMsg= "[ZenkyokuList]";
		try{
			dbMsg= "reqCode=" + reqCode;			/////////////////////////////////////
			switch(reqCode) {
			case MaraSonActivity.syoki_start_up:		//100;初回起動とプリファレンスリセット後
			case MaraSonActivity.syoki_start_up_sai:	//再起動
			case MaraSonActivity.syoki_Yomikomi:		//126;preRead
			case pt_start:
				preReadEnd(cursor);								//dataURIを読み込みながら欠けデータ確認
				break;
			case pt_mastKopusin:						//802;メディアストア更新  ←preReadで欠けが見つかった場合のみ
				mastKopusinEnd();							//メディアストア更新のレコード処理
				break;
			case pt_KaliArtistList:						//803;仮アーティスト作成
				kaliAartistListEnd();				//803;仮アーティストリストの終了
				break;
			case pt_CreateKaliList:						//804;仮リスト作成
				CreateKaliListEnd();
				break;
			case pt_HenkouHanei:							//805;ユーザーの変更を反映
				CreateZenkyokuList();			//全曲リスト作成						break;
				break;
//			case pt_albumReadAll:					//803;MediaStore.Audio.Albumsの全レコード読み込み
//				albumRead2( );										//MediaStore.Audio.Albumsを仮リストへ転記
//				break;
//			case pt_albumRead2:						//804;MediaStore.Audio.Albumsを仮リストへ転記
//				albumReadAllEnd();							//albumReadAllのループ内
//				break;
//			case pt_albumReadAllEnd:				//805;MediaStore.Audio.Albumsの全レコード仮読み込み終わり
//				break;
//			case pt_dbSakusei:						//806;artist_dbデータベース作成
//				dbSakuseiEnd( );							//MediaStoreからデータベースへ読み込み
//				break;
			case pt_jyuufukuSakujyo:					//807:アルバムアーティスト名の重複
				jyuufukuSakujyoEnd();					//アルバムアーティスト名の重複
				break;
			case pt_CreateZenkyokuList:			//809;全曲リスト作成
				CreateZenkyokuListEnd();
				break;
			case pt_CompList:		//807;全曲リストにコンピレーション追加
				dbMsg= dbMsg+",comCount=" + comCount;			/////////////////////////////////////
	//			myLog(TAG,dbMsg );
				comCount++;
				if(comCount < compList.length){
					addCompListBody();		//コンピレーション追加ループの中身
				}else{
					addCompListEnd();		//コンピレーション追加終了
				}
				break;
//			case pt_delPList:			//pt_CompList+1;プレイリスト更新中
//				int nokori = playLists.getCount() - playLists.getPosition();
//				dbMsg= dbMsg+",nokori=" + nokori + "レコード(" + reTry + "回目)";			/////////////////////////////////////
//				myLog(TAG,dbMsg );
//				playLists.close();
//				if(0 < nokori && reTry < 5 ){
//					reTry++;					//再処理リミット
//					reNewPL();
//				}else{
//					reNewPLEnd();
//				}
//				break;
//			case pt_makePList:			//809;汎用プレイリスト作成
//				makePLEnd();		//プレイリスト作成終了
//				break;
//			case pt_albam_syuusei:			//アルバムテーブルのアーティスト名変更
//				albamKoushinEnd();								//MediaStore.Audio.Albumsのアーティスト名更新
//				break;
			case pt_artistList_yomikomi:					//811;アーティストリストを読み込む(db未作成時は-)
				CreateArtistListEnd();		//アーティストリスト作成終了
				break;
				//			default:
//				break;
			}
			pTask = null;
//				myLog(TAG,dbMsg );
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}
//http://www.mori-soft.com/2008-08-15-01-36-37/smartphone/111-android-toast-ui
//http://www.java2s.com/Code/Android/UI/UsingThreadandProgressbar.htm
	public int bCount =0;
	public void setProgressValue(Integer progress) {
		final String TAG = "setProgressValue";							//long seleID  ,, int hennkou, String seleItem
		String dbMsg= "[ZenkyokuList]";
		dbMsg+=reqCode + ")progress;" + progress;
		try{
			if(progress> bCount ){
				ZenkyokuList.this.progBar1.setProgress(progress);
				dbMsg +=">>;" + progBar1.getProgress() + "/" + progBar1.getMax();///////////////////////////////////
	//				myLog(TAG,dbMsg);
				if(handler == null){
					handler = new Handler();
				}
				handler.post(new Runnable() {
					public void run() {
						final String TAG = "setProgressValue.run[ZenkyokuList]";
						String dbMsg="";
						try {
							int progress = progBar1.getProgress();
							int max   = progBar1.getMax();
							dbMsg= progress + "/" +max ;
							double parcent = (double) progress / (double) max;
							ZenkyokuList.this.pgd_val_tv.setText(String.valueOf(progress));
							SpannableString tmp = new SpannableString(_percentFormat.format(parcent));
							dbMsg +=";" +tmp +  "; " + handler ;
							ZenkyokuList.this.pgd_par_tv.setText(tmp);
							if(progress == 0){
								ZenkyokuList.this.pgd_max_tv.setText(String.valueOf(max));
							}
		//					myLog(TAG,dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG, dbMsg + "でエラー発生；"+e.toString());
						}
					}		//run
				});			//handler.post(new Runnable() {
			}
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	public void redrowProg ( int maxXal1 ,  int progress2) {			//progBar1の最大値と初期化
		final String TAG = "set1stProg";
		String dbMsg= "[ZenkyokuList]";
		try {
			bCount = 0;
			dbMsg +="maxXal= " + maxXal1;
			ZenkyokuList.this.progBar1.setMax(maxXal1);
			dbMsg +=">>" + progBar1.getMax();
			pdCoundtVal = 0;
			if(handler == null){
				handler = new Handler();
			}
	//		myLog(TAG,dbMsg);
			handler.post(new Runnable() {
				public void run() {
					final String TAG = "set1stProg[ZenkyokuList]";
					String dbMsg="";
					try {
						ZenkyokuList.this.pgd_max_tv.setText(String.valueOf(progBar1.getMax()));
						myLog(TAG,dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG, dbMsg + "でエラー発生；"+e.toString());
					}
				}
			});
			setProgressValue( 0 );
			change2ndText (progress2) ;
		} catch (Exception e) {
			myErrorLog(TAG, dbMsg + "でエラー発生；"+e.toString());
		}
	}

	 public void change2ndText (int progress2) {			//ProgBar2の表示値設定
		String dbMsg="";
		int progress = ProgBar2.getProgress() ;
		dbMsg +="progress2= " + progress2;
		ProgBar2.setProgress(progress2);
		int max  = ProgBar2.getMax();
		dbMsg +=" / " + max;
		if(handler == null){
			handler = new Handler();
		}
		handler.post(new Runnable() {
			public void run() {
				final String TAG = "change2ndText[ZenkyokuList]";
				String dbMsg="";
				try {
					int progress = ProgBar2.getProgress();
					dbMsg= "[2nd;" + progress;
					int max   = ProgBar2.getMax();
					dbMsg +="/" + max;
					pgd_val2_tv.setText(String.valueOf( progress));
					pgd_max2_tv.setText(String.valueOf( max));
					double parcent = (double) progress / (double) max;
					SpannableString tmp = new SpannableString(_percentFormat.format(parcent));
					dbMsg +=";" + tmp + "]";
					pgd_par2_tv.setText(tmp);
					dbMsg +=pdMessage;
					pgd_msg_tv.setText(pdMessage);		//		pDialog.setMessage(pdMessage);
					pgd_msg_tv.scrollTo(0, pgd_msg_tv.getBottom());				//スクロール？
					pdg_scroll.fullScroll(ScrollView.FOCUS_DOWN);
					//			myLog(TAG,dbMsg);
				} catch (Exception e) {
					myErrorLog(TAG, dbMsg + "でエラー発生；"+e.toString());
				}
			}
		});
	}

	/**
	 * 第一引数;タスク開始時:doInBackground()に渡す引数の型,
	 * 第二引数;進捗率を表示させるとき:onProgressUpdate()に使う型,
	 * 第三引数;タスク終了時のdoInBackground()の返り値の型			AsyncTaskResult<Object>
	 * 		http://d.hatena.ne.jp/tomorrowkey/20100824/1282655538
	 * 		http://pentan.info/android/app/multi_thread.html**/
	public class plogTask extends AsyncTask<Object, Integer , AsyncTaskResult<Integer>> {		//myResult	元は<Object, Integer, Boolean>
		private Context cContext = null;
		private plogTaskCallback callback;
//		private ZenkyokuList ZKL;
	//http://uguisu.skr.jp/Windows/android_asynctask.html
		OrgUtil ORGUT;					//自作関数集
		public long start = 0;				// 開始時刻の取得
		public Boolean isShowProgress;
		public Dialog pDialog = null;	// 処理中ダイアログ	ProgressDialog	AlertDialog
		public int reqCode = 0;						//処理番号
		public CharSequence pdTitol;			//ProgressDialog のタイトルを設定
		public CharSequence pdMessage;			//ProgressDialog のメッセージを設定
		public CharSequence pdMessage_stok;			//ProgressDialog のメッセージを設定
		public int pdMaxVal = 0;					//ProgressDialog の最大値を設定 (水平の時)
		public int pdStartVal=0;					//ProgressDialog の初期値を設定 (水平の時)
		public int pdCoundtVal=0;					//ProgressDialog表示値
		public int pd2MaxVal;					//ProgressDialog の最大値を設定 (水平の時)
		public int pd2CoundtVal;					//ProgressDialog表示値
		public String _numberFormat = "%d/%d";
		public  NumberFormat _percentFormat = NumberFormat.getPercentInstance();

		public Boolean preExecuteFiniSh=false;	//ProgressDialog生成終了
		public Bundle extras;

		long stepKaisi = System.currentTimeMillis();		//この処理の開始時刻の取得
		long stepSyuuryou;		//この処理の終了時刻の取得

		public plogTask(Context cContext , plogTaskCallback callback ){
			super();
				//,int reqCode , String pdTitol , String pdMessage ,int pdMaxVal ,int pd2CoundtVal,int pd2MaxVal){
			final String TAG = "plogTask[plogTask]";
			try{
				ORGUT = new OrgUtil();				//自作関数集
				String dbMsg = "cContext="+cContext;///////////////////////////
				if( cContext != null ){
					this.cContext = cContext;
					this.callback = callback;
					dbMsg += ",callback="+callback;///////////////////////////
				}
		//		myLog(TAG, dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,"でエラー発生；"+e.toString());
			}
		}
//			http://greety.sakura.ne.jp/redo/2011/02/asynctask.html
		@Override
	/***
     * 最初にUIスレッドで呼び出されます。 , UIに関わる処理をします。
	 * doInBackgroundメソッドの実行前にメインスレッドで実行されます。
	 * 非同期処理前に何か処理を行いたい時などに使うことができます。 */
		protected void onPreExecute() {			// onPreExecuteダイアログ表示
			//int reqCode , String pdTitol , String pdMessage ,int pdMaxVal ,int pd2CoundtVal,int pd2MaxVal
			super.onPreExecute();
			final String TAG = "onPreExecute[plogTask]";
			String dbMsg="";
			try {
				dbMsg = ":；reqCode="+reqCode;///////////////////////////
				dbMsg +=  ",Message="+pdMessage;///////////////////////////
				dbMsg += ",pdMaxVal="+pdMaxVal;///////////////////////////
			} catch (Exception e) {
				myErrorLog(TAG,"でエラー発生；"+e.toString());
			}
		}

		public  Uri cUri = null  ;							//4]
		public  String where = null;
		public  String fn = null;			//kari.db
		public  SQLiteStatement stmt = null ;			//6；SQLiteStatement
		public SQLiteDatabase tdb;
		public  ContentValues cv = null ;						//7；SQLiteStatement

		@SuppressWarnings({ "resource", "unchecked" })
		@Override
	/**
	 * doInBackground
	 * ワーカースレッド上で実行されます。 このメソッドに渡されるパラメータの型はAsyncTaskの一つ目のパラメータです。
	 * このメソッドの戻り値は AsyncTaskの三つ目のパラメータです。
	 * メインスレッドとは別のスレッドで実行されます。
	 * 非同期で処理したい内容を記述します。 このメソッドだけは必ず実装する必要があります。
	 *0 ;reqCode, 1; pdMessage , 2;pdMaxVal ,3:cursor , 4;cUri , 5;where , 6;stmt , 7;cv ,  8;omitlist , 9;tList );
	 * */
		public AsyncTaskResult<Integer> doInBackground(Object... params) {//InParams続けて呼ばれる処理；第一引数が反映されるのはここなのでここからダイアログ更新 バックスレッドで実行する処理;getProgress=0で呼ばれている
			final String TAG = "doInBackground[plogTask]";
			String dbMsg="";
			try {
				long id = 0;
				Cursor cursor = null  ;					//3]
				SQLiteDatabase wrDB;
//				String artistName = null;		//	ALBUM_ARTIST text,//album_artist
//				String creditName = null;		//ARTIST text not null," +	//artist;				//クレジットアーティスト名
//				String albumName = null;		//ALBUM text, " +					//album
//				String yearTitole = null;		//YEAR text, " +			//MediaStore.Audio.Media.YEAR
//				String artist_before = "";		//	ALBUM_ARTIST text,//album_artist
//				String album_before = "";		//ALBUM text, " +					//album

				pdCoundtVal = 0;
				this.reqCode = (Integer) params[0] ;			//0.処理
				dbMsg="reqCode = " + reqCode;
				dbMsg +="[" + ProgBar2.getProgress() + "/" + ProgBar2.getMax() + "]";
				this.pd2CoundtVal= reqCode - pt_start + 1 ;							//ProgBar2.getProgress() + 1 ;			//4.処理カウント
				dbMsg +=">>[" + this.pd2CoundtVal + "/" + this.pd2MaxVal + "]";
				ZenkyokuList.this.ProgBar2.setProgress(this.pd2CoundtVal);

				CharSequence setStr=(CharSequence) params[1];				//2.次の処理に渡すメッセージ
				if(setStr !=null ){
					if(! setStr.equals(pdMessage)){
						ZenkyokuList.this.pdMessage = (String) setStr;
						this.pdMessage = setStr;
						dbMsg +=",Message = " + ZenkyokuList.this.pdMessage;
					}
				}
				cursor = (Cursor) params[2] ;			//2
				dbMsg +=", cursor = " + cursor.getCount() + "件"  ;
				pdMaxVal = cursor.getCount();
				ZenkyokuList.this.progBar1.setMax(pdMaxVal);
				dbMsg +=">getMax>" + progBar1.getMax();
				setProgressValue( pdCoundtVal );
				long vtWidth = 500;		// 更新間隔
				if(pdMaxVal<500){
					vtWidth = 100;
					if(pdMaxVal<100){
						vtWidth = 20;
					}
				}

				dbMsg +=", 更新間隔 = " + vtWidth  ;
				long vTime = System.currentTimeMillis() + vtWidth;		// 更新タイミング
				wrDB=(SQLiteDatabase) params[3] ;			//3:
				File dbF;
				dbMsg +=", wrDB = " + wrDB ;
					switch(reqCode) {
						case MaraSonActivity.syoki_start_up:			//100;初回起動とプリファレンスリセット後
						case MaraSonActivity.syoki_start_up_sai:		//再起動
						case MaraSonActivity.syoki_Yomikomi:		//126;preRead
						case pt_start:
						case pt_mastKopusin:						//メディアストア更新  ←preReadで欠けが見つかった場合のみ
							this.cUri =  (Uri) params[4] ;			//4
							dbMsg +=", cUri = " + cUri.getPath()  ;
							this.where =  (String) params[5] ;			//5
							dbMsg +=", where = " + where  ;
							break;
						case pt_KaliArtistList:						//803;仮アーティスト作成
							this.fn =  (String) params[5] ;			//5
							dbMsg += ",db=" + fn;
							del_DB(fn);		//SQLiteDatabaseを消去
							kariArtist_db = cContext.openOrCreateDatabase(fn, MODE_PRIVATE, null);	//String path, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler				//アーティスト名のえリストファイルを読み書きモードで開く
							kariArtist_db.close();
							dbMsg += " , kariArtist_db =" + kariArtist_db;				//SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/artist.db；
							artistTName = cContext.getString(R.string.artist_table);			//artist_table
							dbMsg += "；アーティストリストテーブル=" + artistTName;
							artistHelper = new ArtistHelper(cContext , fn);		//アーティスト名のリストの定義ファイル		.
							kariArtist_db = artistHelper.getWritableDatabase();			// データベースをオープン
							kariArtist_db.beginTransaction();
							stmt = null;
							stmt = kariArtist_db.compileStatement("insert into " + artistTName +
									"(ARTIST_ID,SORT_NAME,ARTIST,ALBUM_ARTIST,ALBUM,ALBUM_ART,SUB_TEXT) values (?, ?, ?, ?, ?, ?, ?);");
							break;
						case pt_artistList_yomikomi:					//807;アーティストリストを読み込む(db未作成時は-)
							this.fn =  (String) params[5] ;			//5
							dbMsg += ",db=" + fn;
							del_DB(fn);		//SQLiteDatabaseを消去
							artist_db = cContext.openOrCreateDatabase(fn, MODE_PRIVATE, null);	//String path, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler				//アーティスト名のえリストファイルを読み書きモードで開く
							artist_db.close();
							dbMsg += " , artist_db =" + artist_db;				//SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/artist.db；
							artistTName = cContext.getString(R.string.artist_table);			//artist_table
							dbMsg += "；アーティストリストテーブル=" + artistTName;
							artistHelper = new ArtistHelper(cContext , fn);		//アーティスト名のリストの定義ファイル		.
							artist_db = artistHelper.getWritableDatabase();			// データベースをオープン
							artist_db.beginTransaction();
							stmt = null;
							stmt = artist_db.compileStatement("insert into " + artistTName +
									"(ARTIST_ID,SORT_NAME,ARTIST,ALBUM_ARTIST,ALBUM,ALBUM_ART,SUB_TEXT) values (?, ?, ?, ?, ?, ?, ?);");
							break;
						case pt_CreateKaliList:						//803;仮リスト作成
							this.fn =  (String) params[5] ;			//5
							dbMsg += ",db=" + fn;
							del_DB(fn);		//SQLiteDatabaseを消去
							zenkyokuHelper = new ZenkyokuHelper(getApplicationContext() , fn);		//全曲リストの定義ファイル		.this.cContext.
							Kari_db = this.cContext.openOrCreateDatabase(fn, SQLiteDatabase.OPEN_READWRITE, null);	//String path, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler				//アーティスト名のえリストファイルを読み書きモードで開く
							Kari_db.close();
							dbMsg += ">作り直し>" + cContext.getDatabasePath(fn).getPath();	///data/data/com.hijiyam_koubou.marasongs/databases/artist.db
							zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
							dbMsg += "；全曲リストテーブル=" + zenkyokuTName;
							Kari_db = zenkyokuHelper.getWritableDatabase();			// データベースをオープン
							Kari_db.beginTransaction();
							stmt = Kari_db.compileStatement("insert into " + zenkyokuTName +
									"(AUDIO_ID,SORT_NAME,ARTIST,ALBUM_ARTIST,ALBUM,TRACK,TITLE,DURATION,YEAR,DATA,MODIFIED,COMPOSER,LAST_YEAR,ALBUM_ARTIST_LIST_ID) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
							new ContentValues();
							albumCount = 0;
							album_art = null;
							last_year = null;
							break;
						case pt_jyuufukuSakujyo:						//804;コンピレーション抽出；アルバムアーティスト名の重複
							this.fn =  (String) params[5] ;			//5
							dbMsg += ",db=" + fn;
							zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
							dbMsg += "；全曲リストテーブル=" + zenkyokuTName;
							Kari_db = zenkyokuHelper.getWritableDatabase();			// データベースをオープン
//							Kari_db.beginTransaction();
//							stmt = Kari_db.compileStatement("UPDATE " + zenkyokuTName + " SET ALBUM_ARTIST=? WHERE _id=?");
//final SQLiteStatement statement1=writableDb.compileStatement("UPDATE " + TABLE_NAME + " SET "+ Field.NATIVECONTACTID+ "=? WHERE "+ Field.LOCALID+ "=?");
//http://stackoverflow.com/questions/13482091/update-syntax-in-sqlite?lq=1
//return readableDb.compileStatement("SELECT " + Field.LOCALID + " FROM "+ TABLE_NAME+ " WHERE "+ Field.SERVERID+ "=?");
							break;
						case pt_HenkouHanei:							//805;ユーザーの変更を反映
//							this.fn =  (String) params[5] ;			//5
//							dbMsg += ",db=" + fn;
//							shyuuseiTName = getResources().getString(R.string.shyuusei_table);			//登録アーティスト修正
//							dbMsg += "；全曲リストテーブル=" + shyuusei_Helper;
//							shyuusei_db = shyuusei_Helper.getWritableDatabase();			// データベースをオープン
							break;
						case pt_CreateZenkyokuList:		//807;全曲リスト作成
						case pt_CompList:						//806;全曲リストにコンピレーション追加
							this.fn =  (String) params[5] ;			//5
							dbMsg += ",db=" + fn;
							zenkyokuHelper = new ZenkyokuHelper(cContext , fn);		//全曲リストの定義ファイル		.
							if(reqCode == pt_CreateZenkyokuList ){
								del_DB(fn);		//SQLiteDatabaseを消去
								Zenkyoku_db = cContext.openOrCreateDatabase(fn, MODE_PRIVATE, null);	//String path, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler				//アーティスト名のえリストファイルを読み書きモードで開く
								Zenkyoku_db.close();
								dbMsg += ">作り直し>" + cContext.getDatabasePath(fn).getPath();	///data/data/com.hijiyam_koubou.marasongs/databases/artist.db
							}
							zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
							dbMsg += "；全曲リストテーブル=" + zenkyokuTName;
							Zenkyoku_db = zenkyokuHelper.getWritableDatabase();			// データベースをオープン
							Zenkyoku_db.beginTransaction();
							stmt = null;
							stmt = Zenkyoku_db.compileStatement("insert into " + zenkyokuTName +
									"(AUDIO_ID,SORT_NAME,ARTIST,ALBUM_ARTIST,ALBUM,TRACK,TITLE,DURATION,YEAR,DATA,MODIFIED,COMPOSER,LAST_YEAR,ALBUM_ARTIST_LIST_ID) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
							new ContentValues();
							break;
					}
					redrowProg ( pdMaxVal , this.pd2CoundtVal);			//progBar1の最大値と初期化
					if(cursor.moveToFirst()){
						dbMsg= dbMsg +"；ループ前；"+ cursor.getPosition() +"/ " + pdMaxVal;	/////////////////////////////////////////////////////////////
			//			myLog(TAG,dbMsg);
						bAlbum = null;
						do{
							dbMsg= reqCode+";" + cursor.getPosition() +"/"+ cursor.getCount() + ")" ;
							switch(reqCode) {
							case MaraSonActivity.syoki_start_up:			//100;初回起動とプリファレンスリセット後
							case MaraSonActivity.syoki_start_up_sai:		//再起動
							case MaraSonActivity.syoki_Yomikomi:		//126;preRead
							case pt_start:
								preReadBody(cursor , cUri , where);			//MediaStore.Audio.Mediaの欠けデータ確認
								break;
							case pt_mastKopusin:						//メディアストア更新  ←preReadで欠けが見つかった場合のみ
								pdCoundtVal = mastKopusinBody(cContext , cursor , cUri , where);				//メディアストア更新のレコード処理
								break;
							case pt_KaliArtistList:						//803;仮アーティスト作成
								cursor = kaliAartistListBody(cursor , stmt ) ;
//								id = 0;
//								id = stmt.executeInsert();
//								dbMsg += "文字[" + id +"]に追加";///////////////////		ZenkyokuList.this.
								pdCoundtVal = cursor.getPosition();
								break;
							case pt_artistList_yomikomi:									//809;アーティストリストを読み込む(db未作成時は-)
								cursor = CreateArtistListBody(cursor , stmt ) ;				//ALBUM_ARTISTで付随するアルバム情報を取得
								id = 0;
								id = stmt.executeInsert();
								dbMsg += "文字[" + id +"]に追加";///////////////////		ZenkyokuList.this.
								break;
							case pt_CreateKaliList:						//;								//804;仮リスト作成
								String albumMei = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
								String kyokuYear = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR));
								String testStr =albumMei;
								if(kyokuYear != null){
									testStr = testStr + kyokuYear;
								}
								cursor = kaliListBody( cursor , stmt  );		//仮リスト作成
								id = 0;
								id = stmt.executeInsert();
								dbMsg += "文字[" + id +"]に追加";///////////////////		ZenkyokuList.this.
								break;
							case pt_jyuufukuSakujyo:								//805;コンピレーション抽出；アルバムアーティスト名の重複
								cursor = jyuufukuSakujyoBody(cursor);		//アルバムアーティスト名の重複			//
								break;
							case pt_HenkouHanei:							//806;ユーザーの変更を反映
								cursor = henkouHaneiBody( cursor );
								break;
							case pt_CreateZenkyokuList:									//807;全曲リスト作成
							case pt_CompList:													//808;全曲リストにコンピレーション追加
								cursor = CreateZenkyokuBody( cursor  , stmt );					//最終リスト作成
								id = 0;
								id = stmt.executeInsert();
								dbMsg += "文字[" + id +"]に追加";///////////////////		ZenkyokuList.this.
								break;
//							case pt_delPList:			//pt_CompList+1;プレイリスト更新中
//								cursor = reNewPLbody(cursor);
//								break;
//							case pt_makePList:			//809;汎用プレイリスト作成
//								cursor = makePLBody( cursor );		//プレイリスト作成
//								break;
//							case pt_albam_syuusei:			//アルバムテーブルのアーティスト名変更
//								cursor = albamKoushinBody(cursor);								//MediaStore.Audio.Albumsのアーティスト名更新
//								break;
							default:
								pdCoundtVal =  ZenkyokuList.this.pdCoundtVal ;
								dbMsg = reqCode+")"+pdCoundtVal + "/" + pdMaxVal ;
			//					myLog(TAG, dbMsg);
								break;
							}
							pdCoundtVal =  cursor.getPosition()+1  ;
							dbMsg = reqCode+")"+pdCoundtVal + "/" + pdMaxVal ;
							long nTime = System.currentTimeMillis() ;
							if(nTime  > vTime ){
								publishProgress( pdCoundtVal );		//progressDialog.progBar1.setProgress(step1);
								if( vtWidth > 1 ){
									vtWidth = vtWidth/2;
								}
								vTime = System.currentTimeMillis() + vtWidth;		// 更新タイミング
								dbMsg +=">progBar1>;" + progBar1.getProgress() + "/" + progBar1.getMax() + "["+ vtWidth+ "]"+ vTime ;///////////////////////////////////
							}
						}while( cursor.moveToNext() ) ;				//pdCoundtVal <  pdMaxVal
					}
					switch(reqCode) {
						case pt_KaliArtistList:						//803;仮アーティスト作成
							endTS(kariArtist_db);			//setTransactionSuccessful
							break;
						case pt_artistList_yomikomi:					//807;アーティストリストを読み込む(db未作成時は-)
							endTS(artist_db);			//setTransactionSuccessful
							break;
						case pt_CreateKaliList:							//803;仮リスト作成
		//				case pt_jyuufukuSakujyo:							//804;コンピレーション抽出；アルバムアーティスト名の重複
							endTS(Kari_db);									//setTransactionSuccessful
							break;

						case pt_CreateZenkyokuList:					//805;全曲リスト作成
						case pt_CompList:									//806;全曲リストにコンピレーション追加
				//		case pt_albam_syuusei:			//アルバムテーブルのアーティスト名変更
				//		case pt_makePList:			//809;汎用プレイリスト作成
							endTS(Zenkyoku_db);			//setTransactionSuccessful
							break;
					}
				Thread.sleep(300);			//書ききる為の時間（100msでは不足）
				publishProgress( pdCoundtVal );		//progressDialog.progBar1.setProgress(step1);
				stepSyuuryou = System.currentTimeMillis();		//この処理の終了時刻の取得
				dbMsg = this.reqCode +";経過時間"+(int)((stepSyuuryou - stepKaisi)) + "[mS]";				//各処理の所要時間
		//		myLog(TAG,dbMsg);
				return AsyncTaskResult.createNormalResult( reqCode );
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"；"+e.toString());
				return AsyncTaskResult.createNormalResult(reqCode) ;				//onPostExecuteへ
			}
		}

		public void del_DB(String fn) {			//SQLiteDatabaseを消去
			final String TAG = "del_DB[plogTask]";
			String dbMsg="";
			try{
				File dbF = new File(fn);		//cContext.getDatabasePath(fn);			//Environment.getExternalStorageDirectory().getPath();
				dbMsg += ",dbF=" + dbF;
				dbMsg += ">>DB消去=" + cContext.deleteDatabase(fn);			//消去してdbF.delete();		deleteDatabase(dbF.getPath());
				dbMsg += " , exists=" + dbF.exists() +" , canWrite=" + dbF.canWrite();
				if(dbF.exists()){
					dbMsg += ">>delF=" + dbF.getPath();
					dbMsg += ">>ファイル消去=" + dbF.delete();
				}
	//			myLog(TAG,dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"；"+e.toString());
			}
		}

		public void up_DB(String udIdName , List<String> udIdList , SQLiteDatabase sql_db) {			//SQLite  update
			final String TAG = "up_DB[plogTask]";
			String dbMsg="";
			try{
				dbMsg=udIdName + "で" + udIdList.size() +"件更新;";
				dbMsg= dbMsg+ udIdName;
				ContentValues cv = new ContentValues();
				cv.put("ALBUM_ARTIST", udIdName);
				for(String rID : udIdList){
					dbMsg +="、" + rID;
					try {
//						if ( ! sql_db.isOpen()) {
//							sql_db= zenkyokuHelper.getWritableDatabase();			// データベースをオープン
//							sql_db.beginTransaction();
//						}
//						dbMsg= dbMsg +"(" +  rID + ")" ;				// ZenkyokuList.this.compIndex;
//						dbMsg +=">cv=" + cv.toString();
//						stmt.bindString(1, String.valueOf(udIdName));
//						stmt.bindString(2, String.valueOf(rID));
//						stmt.execute();
//						ContentValues cv = new ContentValues();
//						cv.put("ALBUM_ARTIST", rArtistName);
						int rRow = sql_db.update(zenkyokuTName, cv, "_id = " + rID , null);
						dbMsg +=">>" + rRow;
//	cContext.getContentResolver()db.update(DB_TABLE, val, "productid=?", new String[] { editId.getText().toString() });//		// データ更新
	//			dbMsg += "を" +rRow +  ">>"+ rArtistName;
					} finally {
//						sql_db.endTransaction();		 //DBクローズ
//						if (sql_db != null) {
//							sql_db.close();
//						}
					}
				}										//	for(String rID : udIdList){
	//			myLog(TAG,dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"；"+e.toString());
			}
		}

		//java.lang.IllegalStateException: attempt to re-open an already-closed object: SQLiteDatabase: ///.//kari.db


		public void endTS(SQLiteDatabase sql_db) {			//setTransactionSuccessful
			final String TAG = "endTS[plogTask]";
			String dbMsg="";
			try{
				try{
					dbMsg= "sql_db = " + sql_db;//////
					sql_db.setTransactionSuccessful();
				} finally {
					sql_db.endTransaction();
				}
				sql_db.close();
	//			myLog(TAG,dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"；"+e.toString());
			}
		}


		@Override
	/**
	 * onProgressUpdate
	 * プログレスバー更新処理： UIスレッドで実行される doInBackground内でpublishProgressメソッドが呼ばれると、
	 * UIスレッド上でこのメソッドが呼ばれます。   このメソッドの引数の型はAsyncTaskの二つ目のパラメータです。
	 * メインスレッドで実行されます。非同期処理の進行状況をプログレスバーで 表示したい時などに使うことができます。*/
		public void onProgressUpdate(Integer... values) {			//
			final String TAG = "onProgressUpdate[plogTask]";
			String dbMsg="";
			int progress = values[0];
			try{
				dbMsg= this.reqCode +")progress= " + progress;
				setProgressValue( progress );
				dbMsg +=">> " + progBar1.getProgress();
				dbMsg +="/" + progBar1.getMax();///////////////////////////////////
		//		myLog(TAG,dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"；"+e.toString());
			}
		}

		@Override
	/**
	 * onPostExecute
	 * doInBackground が終わるとそのメソッドの戻り値をパラメータとして渡して onPostExecute が呼ばれます。
	 * このパラメータの型は AsyncTask を extends するときの三つめのパラメータです。
	 *  バックグラウンド処理が終了し、メインスレッドに反映させる処理をここに書きます。
	 *  doInBackgroundメソッドの実行後にメインスレッドで実行されます。
	 *  doInBackgroundメソッドの戻り値をこのメソッドの引数として受け取り、その結果を画面に反映させることができます。*/
		public void onPostExecute(AsyncTaskResult<Integer> ret){	// タスク終了後処理：UIスレッドで実行される AsyncTaskResult<Object>
			super.onPostExecute(ret);
					final String TAG = "onPostExecute[plogTask]";
					String dbMsg="開始";
					try{
						dbMsg= pd2CoundtVal + " / " + pd2MaxVal ;
						reqCode = ret.getReqCode();
						dbMsg +="終了；reqCode=" + reqCode +"(終端"+ pdCoundtVal +")";
						dbMsg +=",callback = " + callback;	/////http://techbooster.org/android/ui/1282/
		//				myLog(TAG, dbMsg);
						callback.onSuccessplogTask(reqCode );		//1.次の処理;2.次の処理に渡すメッセージ
					} catch (Exception e) {
						myErrorLog(TAG,dbMsg + "でエラー発生；"+e.toString());
					}
				}

	}

	/** Runnable のプログラム */
	//http://techbooster.jpn.org/andriod/ui/9564/
	Thread thread;
	Handler handler = new Handler();
	Message msg ;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		final String TAG = "onDestroy[ZenkyokuList]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			Thread thread = this.thread;
			dbMsg= "thread = ; "+ thread;/////////////////////////////////////
			thread = null;
			dbMsg= ">>" + thread;/////////////////////////////////////
			dbMsg +=",Zenkyoku_db.isOpen()=" + Zenkyoku_db.isOpen();/////////////////////////////////////
			if( Zenkyoku_db.isOpen() ){
				Zenkyoku_db.close();
				dbMsg +=">>" + Zenkyoku_db.isOpen();/////////////////////////////////////
			}
			dbMsg +=",artist_db.isOpen()=" + artist_db.isOpen();/////////////////////////////////////
			if( artist_db.isOpen() ){
				artist_db.close();
				dbMsg +=">>" + artist_db.isOpen();/////////////////////////////////////
			}
			dbMsg +=",Kari_db.isOpen()=" + Kari_db.isOpen();/////////////////////////////////////
			if( Kari_db.isOpen() ){
				Kari_db.close();
				dbMsg +=">>" + Kari_db.isOpen();/////////////////////////////////////
			}
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}
	///////////////////////////////////////////////////////////////////////////////////
	public static void myLog(String TAG , String dbMsg) {
		Util UTIL = new Util();
		Util.myLog(TAG , dbMsg);
	}

	public static void myErrorLog(String TAG , String dbMsg) {
		Util UTIL = new Util();
		Util.myErrorLog(TAG , dbMsg);
	}

	public static boolean setPrefStr(String keyNmae , String wrightVal , Context context) {        //プリファレンスの読込み
		boolean retBool = false;
		final String TAG = "setPrefStr";
		String dbMsg="[MusicPlayerService]keyNmae=" + keyNmae;
		Util UTIL = new Util();
		retBool = Util.setPreStr(keyNmae , wrightVal,context);
		return retBool;
	}

}
//http://greety.sakura.ne.jp/redo/2011/02/asynctask.html



//How to extract ID3 tag from an mp3 file with android		http://www.anddev.org/viewtopic.php?p=12404
//Androidでプレイリストを使う	http://doloopwhile.hatenablog.com/entry/20110912/1315788396

