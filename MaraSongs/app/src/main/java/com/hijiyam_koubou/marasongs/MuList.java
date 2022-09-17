package com.hijiyam_koubou.marasongs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.ProgressBar;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hijiyam_koubou.marasongs.BaseTreeAdapter.TreeEntry;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 2.0
 * ①Marasongで作った全曲リストや最近追加などのプレイリストを他の音楽再生アプリからもご利用いただけます。
 * ・AndroidOSの管理するプレイリスト(MediaStore.Audio.Playlists)に登録します。
 * 　YouTube MusicのデバイスのファイルやGoogle Play Musicのすべてのプレイリストに表示されます。
 * ・汎用形式のプレイリスト(エンコードUTF-8の拡張子.m3u8)を作成します。
 * 　AndroidOSの管理するプレイリストに対応していない音楽再生アプリからもMarasongで作成したプレイリストをご利用いただける方法を提供します。
 * ②　m3u8のプレイリストを読み込んでAndroidOSの管理するプレイリストに登録できます
 * ③全曲リストもAndroidOSの管理するプレイリストを利用します。
 * 　全曲リストを作成する時にMediaStore.Audio.Media.ARTIST
 *
 * ※読み込めるプレイリストの上限曲数は音楽再生アプリにより異なります。
 * 　YouTube Musicで4000曲、Google Play Musicで1000曲です。
 * ※ Google Play Musicの様に.m3u8形式のプレイリストに対応していない音楽再生アプリも有ります。
 * ※　m3u8形式のファイルは厳密な規定が無いため、読み込めないことも有ります。
 *
 * **/
public class MuList extends AppCompatActivity implements  View.OnClickListener , View.OnKeyListener {
	                                                                 //plogTaskCallback,
	public static final String ACTION_INIT = "com.example.android.notification.action.INIT";
	public OrgUtil ORGUT;						//自作関数集
	public Util UTIL;
	public MyApp myApp;
	public MyPreferences myPreferences;
	public MusicPlaylist musicPlaylist ;
	private MuList.ploglessTask plTask;

	/**定数*/
	public Locale locale;							// アプリで使用されているロケール情報を取得

	//プリファレンス
	public Editor myEditor ;
	public SharedPreferences sharedPref;

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
	private int mIndex;
	private Item playingItem;						//再生中の楽曲レコード
	public long backTime=0;						//このActivtyに戻った時間

	public ZenkyokuHelper zenkyokuHelper = null;				//全曲リストヘルパー
	public SQLiteDatabase Zenkyoku_db;		//全曲リストファイル
	public String zenkyokuTName;			//全曲リストのテーブル名
	public SQLiteDatabase artist_db;
	public ArtistHelper artistHelper;		//アーティスト名のリストの定義ファイル
	public String artistTName;			//アーティストリストのテーブル名
	public String alPFN = "al_pref";
	public String myFolder ;
	public ZenkyokuHelper newSongHelper = null;				//最近追加リストヘルパー
	public SQLiteDatabase newSongDb;		//最近追加のデータベースファイル

	public Toolbar toolbar;						//このアクティビティのtoolBar
	public Drawable defoltIcon;	//R.drawable.no_image
	public Spinner pl_sp;	//プレイリスト選択
	private ExploreSpinnerAdapter spinnerAdapter;

	public ListView lvID;				//リスト
	public TextView artistHTF;			//ヘッダーのアーティスト名表示枠
	public TextView mainHTF;			//ヘッダーのメインテキスト表示枠
	public TextView subHTF;			//ヘッダーのサブテキスト表示枠
	public ImageView headImgIV;		//ヘッダーのアイコン表示枠
	public int headImgH = 52;			//ヘッダーのアイコン高さ
	public int headImgW = 52;			//ヘッダーのアイコン高さ
	public LinearLayout list_player;		//プレイヤーのインクルード
	public ImageButton lp_ppPButton;			//プレイヤーの再生/停止ボタン
	public ImageButton lp_stop;					//プレイヤーの終了ボタン
	public TextView lp_artist ;					//プレイヤーのアーティスト表示
	public TextView lp_album ;					//プレイヤーのアルバム表示
	public TextView lp_title ;						//プレイヤーのタイトル表示
	public Chronometer lp_chronometer;		//プレイヤーの再生ポジション表示
	public LinearLayout rc_fbace;			//プレイヤーフィールド部の土台;クリックの反応部

	public List<String> plNameSL=null;					//プレイリスト名用簡易リスト
	public List<Map<String, Object>> plNameAL;			//プレイリスト名用リスト
	public List<String> plSL;					//プレイリスト用簡易リスト
	public List<Map<String, Object>> plAL;		//プレイリスト用ArrayList
	public Map<String, Object> objMap;				//汎用マップ
	public List<String> artistSL =null;					//アーティストリスト用簡易リスト
	public List<String> albumSL =null;					//アルバムリスト用簡易リスト
	public List<String> titolSL =null;					//タイトルリスト用簡易リスト

	public Map<String, Object> artistMap;				//アーティストリスト用
	public List<Map<String, Object>> artistAL;		//アーティストリスト用ArrayList
	public Map<String, Object> albumMap;			//アルバムトリスト用
	public List<Map<String, Object>> albumAL;		//アルバムリスト用ArrayList
	public Map<String, Object> titolMap;				//タイトルリスト用
	public List<Map<String, Object>> titolAL;			//タイトルムリスト用ArrayList
	public List<String> listItems=null;					//メインリストアイテム
	public List<String> imgItems=null;					//サムネール
	public List<String> subItems=null;					//付加情報
	public List<String> saisei_fnameList = null;		//uri配列
	public List<CustomData> isList = null;			//ヘッドイメージとサブテキストを持ったリストアイテム
	public String senntakuItem=null;					//選択しておくアイテムアイテム
	public int reqCode=0;								//何の処理か
	public int b_reqCode=0;							//処理コードの保留
	public int backCode=0;								//上のリスト
	public int reqKariCode=0;
	public String listTopFname;							//リストの先頭ファイル名（プレイリスト作成時など）

	public String subTex = null;		//ヘッド部の付加情報

	public List<String> albumArtistList = null;		//クレジットされているアーティスト名
	public List<String> artistList = null;		//クレジットされているアーティスト名
	public List<String> albumList = null;		//アルバム名
	public List<String> titolList = null;		//曲名

	public List<String> mainList = null;		//アルバムアーティスト
	public List<String> imgList = null;		//アルバムアート
	public List<String> subList = null;		//アルバム付加情報
	public List<String> titoSubList = null;		//曲名付加情報
	public int nowList_id = -1;				//再生中のプレイリストID	(受取り時/戻し時)
	public String nowList = null;				//再生中のプレイリスト名
	public String nowListSub;				//再生中のプレイリストの詳細
	public String nowList_data;		//再生中のプレイリストの保存場所
	public int reqestList_id = 0;			//アクティビテイ方戻されたのプレイリストID

	public int play_order = 0;
	public String creditArtistName;		//クレジットされているアーティスト名
	public String albumArtist;				//リストアップしたアルバムアーティスト名
	public String albumName;			//アルバム名
	public String titolName ;				//曲名
	public String albumArt ;				//アルバムアートのURI
	public int trackInAlbum = 0;			//アルバム内の曲数
	public int releaceYear = 0;			//制作年
	public String b_saisei_fname ="";				//すでに再生している再生ファイル
	public String b_artist = "";				//それまで参照していたアーティスト名
	public String b_album = "";				//それまで参照していたアルバム名
	public boolean IsPlaying ;			//再生中か
	public boolean IsSeisei;			//再生中か
	public String sucssesPass;			//実際に読み出せたアルバムアートのパス
	public int yobidashiItem = -1;		//プレイヤー画面でタップされたアイテム

//	public List<Item> mItems;
//	public Cursor allSongCursor = null;

	public LinearLayout headLayout;			//ツールバーのカスタムレイアウト
	public String mainTStr = null;
	public String subTStr;
	public ploglessTask pTask;
	public int artintCo = 0;
	public int albamCo = 0;
	public int titolCo = 0;
	public int compCount;
	public String aArtist = "";
	public String albumMei = "";
	public String artURL = null;
	public String[] compList;
	public int comCount;
	public String compSelection ;			//+ comp ;		//MediaStore.Audio.Media.ARTIST +" <> " + comp;			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
	public String pl_file_name = ""; 		//汎用プレイリストのファイル名
	public String pStrlist = "";			//汎用プレイリストの内容
	//サービス
	/**MusicPlayerService*/
	public Intent MPSIntent;
	public String psSarviceUri;
	////メニューインデックス*******************************************************************/
	int nowSelectMMenu;		//選択したメニューID
	////////getItemIdで拾える定数//////////////////////////////////////////////////////////////
	public int shigot_bangou;
	public int imanoJyoutai = 0;
	public int yobidashiMoto = -1;													//起動直後=veiwPlayer;プレイヤーからの呼出し = chyangeSong
	public static final int settei_hyouji =99;					//設定表示
	public static final int quite_me = settei_hyouji+1;													//終了
	public static final int veiwPlayer = quite_me+100;												//プレイヤーを表示;起動直後
	public static final int chyangeSong= veiwPlayer+1;								//プレイヤーから戻って曲変更
	public static final int SkipRew= chyangeSong+1;								//送り戻し
	public static final int sonomama = SkipRew+1;								//そのまま継続（）
	public static final int jyoukyou_bunki = sonomama+1;								//現在の状態に見合った分岐を行う
	public static final int shyou_Main = jyoukyou_bunki+1;						//アーティストリスト作成後、作成するリストの振り分けに
	public static final int list_wright = shyou_Main+1;						//リスト描画
	public static final int reTrySart = list_wright+1;								//全曲リストを作って再起動
	public static final int reTryMse = reTrySart+1;							//全曲リスト作成から戻ってメッセージ表示
	public static final int quite_list = reTryMse+1;							//リストの終了
//	public static final int m3u_list = quite_list+1;							//m3uファイル選択
	public static final int make_list_head = 500;		//ヘッド作成
	public static final int MENU_MU_OPTION = make_list_head+10;
	public static final int MENU_FILTER_MUSCK = MENU_MU_OPTION+1;			//プレイリストなどの選択処理
	public static final int MENU_FILTER_item0 = MENU_FILTER_MUSCK+1;			//"内部メモリーとメモりーカード"
	public static final int MENU_FILTER_item1 = MENU_FILTER_item0+1;			//""USBメモリー"
	public static final int MENU_FILTER_item2 = MENU_FILTER_item1+1;			//"リクエストリスト"
	public static final int MENU_FILTER_itemEnd = MENU_FILTER_item2+10;			//"新規プレイリスト"
	public static final int MENU_hihyoujiArtist = MENU_FILTER_itemEnd+1;			//非表示になったアーティストの修正処理
	public static final int MENU_hihyoujiArtist2 = MENU_hihyoujiArtist+1;			//非表示になったアーティストの修正処理;2階層化
	public static final int MENU_2KAISOU = MENU_hihyoujiArtist2+10;			//アルバム-タイトル2階層リスト選択選択中
	public static final int MENU_TAKAISOU = MENU_2KAISOU+1;			//537多階層リスト選択選択中
	public static final int MENU_TAKAISOU2 = MENU_TAKAISOU+1;			//多階層リスト書き込み中
	public static final int MENU_infoKaisou = MENU_TAKAISOU2+1;			//情報付きリスト書き込み中
	public static final int MENU_INFO_KAISOU = MENU_infoKaisou;
	public static final int MENU_MUSCK_PLIST = MENU_infoKaisou+1;			//プレイリスト選択中
	public static final int MENU_MUSCK_Artist = MENU_MUSCK_PLIST+10;	//アーティスト選択
	public static final int MENU_MUSCK_ALBUM = MENU_MUSCK_Artist+10;		//アルバム選択
	public static final int MENU_MUSCK_TITOL = MENU_MUSCK_ALBUM+10;		//曲選択
	public static final int MENU_MUSCK_PLAY = MENU_MUSCK_TITOL+10;		//再生
	public static final int MENU_PLAY2STOP = MENU_MUSCK_PLAY+10;				//停止

	public static final int MENU_SONOTA_SETTEI = MENU_PLAY2STOP+10;	//プリファレンス操作Integer.valueOf(R.id.menu_sonota_settei)
	public static final int MENU_ALLSONGDB = MENU_SONOTA_SETTEI+10;				//全曲リストの更新"

	static final int CONTEXT_listup = MENU_ALLSONGDB + 10;								//リストアップ編集
	static final int CONTEXT_listup_jyunbi = CONTEXT_listup + 10;				//リストアップ準備;アーティスト/作曲者名
	static final int CONTEXT_listup_jyunbi2 = CONTEXT_listup_jyunbi + 10;		//リストアップ準備;2階層リスト化
	static final int CONTEXT_add_request = CONTEXT_listup_jyunbi2+1;			//リクエスト
	static final int CONTEXT_dell_request = CONTEXT_add_request+1;				//リクエスト解除
	static final int CONTEXT_add_playlist = CONTEXT_dell_request+1;			//プレイリストに追加
	static final int CONTEXT_make_playlist = CONTEXT_add_playlist+1;			//新規プレイリストを作成して追加
	static final int CONTEXT_rem_playlist = CONTEXT_make_playlist+1;			//このリストから削除
	static final int CONTEXT_oc_playlist = CONTEXT_rem_playlist+1;				//次にタップする位置に移動
	static final int CONTEXT_rename_playlist = CONTEXT_oc_playlist+1;			//リスト名変更
	static final int CONTEXT_del_playlist = CONTEXT_rename_playlist+1;			//このリストを削除
	static final int CONTEXT_del_playlis_membert = CONTEXT_del_playlist+1;	//リストの中身を削除
	static final int CONTEXT_delete = CONTEXT_del_playlis_membert+1;			//削除
	static final int CONTEXT_saikintuika_Wr = CONTEXT_delete+1;				//最近追加リストのファイル作成
	static final int CONTEXT_saikintuika0 = CONTEXT_saikintuika_Wr+1;			//最近追加リストの記述
	static final int CONTEXT_saikintuika = CONTEXT_saikintuika0+1;				//最近追加リストの更新
	static final int CONTEXT_saikintuika_end = CONTEXT_saikintuika+1;			//最近追加リストのプレイリスト作成
	static final int CONTEXT_saikin_sisei0 = CONTEXT_saikintuika_end+1;		//最近再生リストの重複削除
	static final int CONTEXT_saikin_sisei = CONTEXT_saikin_sisei0+1;			//最近再生リストの規定曲に削減
	static final int CONTEXT_rumdam_saiseizumi = CONTEXT_saikin_sisei+10;		//ランダム再生準備
	static final int CONTEXT_rumdam_redrow = CONTEXT_rumdam_saiseizumi+1;		//ランダム再生リストの消去
	static final int CONTEXT_rumdam_arrayi = CONTEXT_rumdam_redrow+1;			//ランダム再生準備
	static final int CONTEXT_rumdam_wr = CONTEXT_rumdam_arrayi+1;				//ランダム再生リストの書込み
	static final int CONTEXT_REPET_CLEAN= CONTEXT_rumdam_wr+1;					//リピート再生リストの既存レコード消去
	static final int CONTEXT_REPET_WR= CONTEXT_REPET_CLEAN+1;					//リピート再生リストレコード書込み
	static final int CONTEXT_M3U2_PL= CONTEXT_REPET_WR+1;					//汎用プレイリストを１Androidのプレイリストに転記


//	static final int CONTEXT_saikintuika_read = CONTEXT_REPET_WR + 1;			//最近追加リストをデータベースから読み出す

	static final int REQUEST_PREF = 100;                          //Prefarensからの戻り
	private Object selItem;
	///////////////////////////////////////////////////////////////////////////////////
//	public static void  messageShow(String titolStr , String mggStr) {
//		final String TAG = "messageShow";
//		String dbMsg = "[MuList]"+titolStr + "\n" + mggStr;
//		Util UTIL = new Util();
//		 UTIL.messageShow(titolStr , mggStr , MuList.this);
//	}

	public static void myLog(String TAG , String dbMsg) {
		Util UTIL = new Util();
		Util.myLog(TAG ,  "[MuList]" + dbMsg);
	}

	public static void myErrorLog(String TAG , String dbMsg) {
		Util UTIL = new Util();
		Util.myErrorLog(TAG ,  "[MuList]" + dbMsg);
	}

	public static String getPrefStr(String keyNmae , String defaultVal,Context context) {        //プリファレンスの読込み
		String retStr = "";
		final String TAG = "getPrefStr";
		String dbMsg="[MusicPlayerService]keyNmae=" + keyNmae;
		Util UTIL = new Util();
		retStr = Util.getPrefStr(keyNmae , defaultVal,context);
		return retStr;
	}

	public static boolean setPrefbool(String keyNmae , boolean wrightVal , Context context) {        //プリファレンスの読込み
		boolean retBool = false;
		final String TAG = "setPrefbool";
		String dbMsg="[MusicPlayerService]keyNmae=" + keyNmae;
		Util UTIL = new Util();
		retBool = Util.setPrefBool(keyNmae , wrightVal,context);
		return retBool;
	}

	public static boolean setPrefInt(String keyNmae , int wrightVal , Context context) {        //プリファレンスの読込み
		boolean retBool = false;
		final String TAG = "setPrefInt";
		String dbMsg="[MusicPlayerService]keyNmae=" + keyNmae;
		Util UTIL = new Util();
		retBool = Util.setPrefInt(keyNmae , wrightVal,context);
		return retBool;
	}

	public static boolean setPrefStr(String keyNmae , String wrightVal , Context context) {        //プリファレンスの読込み
		boolean retBool = false;
		final String TAG = "setPrefStr";
		String dbMsg="[MusicPlayerService]keyNmae=" + keyNmae;
		Util UTIL = new Util();
		retBool = Util.setPreStr(keyNmae , wrightVal,context);
		return retBool;
	}

	/**
	 * 強制的に全曲リストを使用する
	 * 有ればIDを返す
	 * 無ければ作成する
	 * **/
	@SuppressLint("Range")
	public int getAllSongItems() {
		final String TAG = "getAllSongItems";
		String dbMsg = "";
		Cursor allSongCursor = null;
		try {
			sousalistID = pref_zenkyoku_list_id;		//操作対象リストID
			sousalist_data = null;		//操作対象リストのUrl
			sousalistName =getResources().getString(R.string.listmei_zemkyoku);
			dbMsg += "[" + sousalistID + "]" + sousalistName ;
			setPrefStr( "nowList", sousalistName,  MuList.this);         //			myEditor.putString( "nowList", String.valueOf(sousalistName));
			setPrefStr( "nowList_id", String.valueOf(sousalistID),  MuList.this);         //	myEditor.putString( "nowList_id", String.valueOf(sousalistID));		//☆intで書き込むとcannot be cast

			Uri result_uri = null;
			Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
			String[] columns = null;			//{ idKey, nameKey };
			String c_selection = MediaStore.Audio.Playlists.NAME +" = ? ";
			String[] c_selectionArg= { String.valueOf(sousalistName) };		//⑥引数groupByには、groupBy句を指定します。
			String c_orderBy = null;			//MediaStore.Audio.Playlists.Members.PLAY_ORDER;
			Cursor cursor = MuList.this.getContentResolver().query(uri, columns, c_selection, c_selectionArg, c_orderBy );
			if(cursor.moveToFirst()){
				dbMsg +=  "," + cursor.getCount() + "件";
				pref_zenkyoku_list_id = Integer.valueOf(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID)));
			}else{
				dbMsg += "取得できず";
				preRead(MyConstants.syoki_Yomikomi , null);				//dataURIを読み込みながら欠けデータ確認		//		mediaSTkousinn();						//メディアストアの更新呼出し
			}
			cursor.close();
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
		return pref_zenkyoku_list_id;
	}


	//起動　/終了処理//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void checkMyPermission() {
		final String TAG = "checkMyPermission";
		String dbMsg = "";
		try {
			if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {                //(初回起動で)全パーミッションの許諾を取る
				dbMsg += "許諾確認";
				List<String> permissionArray = new ArrayList<String>();
				permissionArray.add(Manifest.permission.READ_EXTERNAL_STORAGE);
				permissionArray.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
				//Android 10(Q)でMediaStoreを介してデータを読み取るときは、 READ_EXTERNAL_STORAGE権限を要求し、書くとき何の権限を必要としません。
				permissionArray.add(Manifest.permission.INTERNET);
				permissionArray.add(Manifest.permission.ACCESS_NETWORK_STATE);
				permissionArray.add(Manifest.permission.BLUETOOTH);
				permissionArray.add(Manifest.permission.BLUETOOTH_ADMIN);
				permissionArray.add(Manifest.permission.RECORD_AUDIO);			//プライバシー ポリシーの設定が必要;ビジュアライザー
				permissionArray.add(Manifest.permission.MODIFY_AUDIO_SETTINGS);
				permissionArray.add(Manifest.permission.WAKE_LOCK);
//				if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {                //(初回起動で)全パーミッションの許諾を取る
//					permissionArray.add(Manifest.permission.MEDIA_CONTENT_CONTROL);   //フラグが変わらない；再生中メディアへのアクセス許可
//				}
				if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ) {                //(初回起動で)全パーミッションの許諾を取る
					permissionArray.add(Manifest.permission.READ_CONTACTS);
					permissionArray.add(Manifest.permission.WRITE_CONTACTS);
				}else{
				}
				dbMsg += "," + permissionArray.size() + "件";
				final String[] PERMISSIONS = permissionArray.toArray(new String[permissionArray.size()]);
				dbMsg += ">>" + PERMISSIONS.length + "件";

				boolean isNeedParmissionReqest = false;
				for ( String permissionName : PERMISSIONS ) {
					dbMsg += "," + permissionName;
					int checkResalt = checkSelfPermission(permissionName);	//許可されていなければ -1 いれば 0
					dbMsg += "=" + checkResalt;
					if ( checkResalt != PackageManager.PERMISSION_GRANTED ) {
						isNeedParmissionReqest = true;
					}
				}
				dbMsg += "、isNeedParmissionReqest=" + isNeedParmissionReqest;
				if ( isNeedParmissionReqest ) {
					dbMsg += "::許諾処理へ";
					myPreferences = new MyPreferences();
					myPreferences.setdPrif(MuList.this);
					new AlertDialog.Builder(MuList.this)
							.setTitle( getResources().getString(R.string.permission_titol) )
							.setMessage( getResources().getString(R.string.permission_msg))
							.setPositiveButton(android.R.string.ok , new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog , int which) {
									requestPermissions(PERMISSIONS , REQUEST_PREF);
//									return;
								}
							})
							.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog , int which) {
									quitMe();
								}
							})
							.create().show();
				}else{
					dbMsg += "::許諾済み";
					dbMsg += "::readPrefへ";
					readPref();
				}
			}
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	/**
	  * パーミッションが通った時点でstartLocalStream 	 */
	@SuppressLint("MissingSuperCall")
	@Override
	public void onRequestPermissionsResult(int requestCode , String[] permissions , int[] grantResults) {
		final String TAG = "onRequestPermissionsResult";
		String dbMsg = "";
		try {
			dbMsg = "requestCode=" + requestCode;
			switch ( requestCode ) {
				case REQUEST_PREF:
					readPref();
					break;
			}
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	public void quitBody() {                //このクラスを破棄
		final String TAG = "quitBody";
		String dbMsg = "";
		try {
//			NendAdInterstitial.dismissAd ();
			//		NendAdInterstitial.showFinishAd(this);			// アプリ内で使用するインタースティシャル広告の枠が一つの場合はこちらをお使いください
			//		NendAdInterstitial.showFinishAd (this,458687);		// 広告枠指定ありの場合
			receiverHaki();							//unregisterReceiverで
			dbMsg += ",レシーバーを破棄";
			MuList.this.finish ();
			this.finish();
			if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
				finishAndRemoveTask();                      //アプリケーションのタスクを消去する事でデバッガーも停止する。
			} else {
				moveTaskToBack(true);                       //ホームボタン相当でアプリケーション全体が中断状態
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void quitMe() {                //このクラスを破棄
		final String TAG = "quitMe";
		String dbMsg = "";
		try {
			dbMsg += "IsPlaying=" + IsPlaying;
			if(IsPlaying){
				new AlertDialog.Builder(MuList.this)
						.setTitle( getResources().getString(R.string.quit_titol))
						.setMessage( getResources().getString(R.string.quit_msg))
						.setPositiveButton(getResources().getString(R.string.quit_posi_bt) , new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog , int which) {
							//	Intent intent = new Intent( MuList.this, MusicPlayerService.class);
								MPSIntent.setAction(MusicPlayerService.ACTION_SYUURYOU_NOTIF);
								startService(MPSIntent) ;
								quitBody();
							}
						})
						.setNegativeButton(getResources().getString(R.string.quit_nega_bt) , new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog , int which) {
								quitBody();
							}
						})
						.create().show();
			} else{
				quitBody();
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	//設定変更反映//////////////////////////////////////////////////////////////////////////////////////////////////////////////起動項目////

	public void readPref() {        //プリファレンスの読込み
		final String TAG = "readPref";
		String dbMsg = "";
		try {
			MyPreferences myPreferences = new MyPreferences();
			dbMsg += "MyPreferencesy読込み";
			myPreferences.readPrif(this);
			sharedPref = MyPreferences.sharedPref;
			myEditor =myPreferences.myEditor;

			pref_sonota_vercord =myPreferences.pref_sonota_vercord;				//このアプリのバージョンコード
			dbMsg += "、このアプリのバージョンコード=" + pref_sonota_vercord;
			pref_compBunki = myPreferences.pref_compBunki;			//コンピレーション設定[%]
//			pref_gyapless = myPreferences.pref_gyapless;			//クロスフェード時間
			pref_list_simple =myPreferences.pref_list_simple;				//シンプルなリスト表示（サムネールなど省略）
			pref_pb_bgc = myPreferences.pref_pb_bgc;				//プレイヤーの背景	true＝Black"	http://techbooster.jpn.org/andriod/ui/10152/

			pref_artist_bunnri = myPreferences.pref_artist_bunnri;		//アーティストリストを分離する曲数
			pref_saikin_tuika = myPreferences.pref_saikin_tuika;			//最近追加リストのデフォルト枚数
			pref_saikin_sisei = myPreferences.pref_saikin_sisei;		//最近再生加リストのデフォルト枚数
			pref_rundam_list_size =myPreferences.pref_rundam_list_size;	//ランダム再生リストアップ曲数
			repeatType = myPreferences.repeatType;							//リピート再生の種類
//			rp_pp = myPreferences.rp_pp;							//2点間リピート中
			pref_lockscreen =myPreferences.pref_lockscreen;				//ロックスクリーンプレイヤー</string>
			pref_notifplayer =myPreferences.pref_notifplayer;				//ノティフィケーションプレイヤー</string>
			pref_cyakusinn_fukki=myPreferences.pref_cyakusinn_fukki;		//終話後に自動再生
			pref_bt_renkei =myPreferences.pref_bt_renkei;				//Bluetoothの接続に連携して一時停止/再開

			String pref_data_url =myPreferences.saisei_fname;				//
			pref_zenkai_saiseKyoku = Integer.parseInt(myPreferences.pref_zenkai_saiseKyoku);			//前回の連続再生曲数
			pref_zenkai_saiseijikann =Integer.parseInt(myPreferences.pref_zenkai_saiseijikann);			//前回の連続再生時間
			dbMsg += "、前回=" + pref_zenkai_saiseKyoku + "曲、" + pref_zenkai_saiseijikann + "時間";

			pref_commmn_music=myPreferences.pref_commmn_music;		//共通音楽フォルダ
			pref_file_in =myPreferences.pref_file_in;		//内蔵メモリ
			pref_file_ex=myPreferences.pref_file_ex;		//メモリーカード
			pref_file_wr= myPreferences.pref_file_wr;		//設定保存フォルダ
			pref_file_kyoku= Integer.parseInt(myPreferences.pref_file_kyoku );		//総曲数
			dbMsg += "、総曲数=" + pref_file_kyoku;
			nowList_data = myPreferences.pref_file_wr;		//設定保存フォルダ
			dbMsg += "、設定保存フォルダ=" + nowList_data;
//			dbMsg += "、総アルバム数=" + myPreferences.pref_file_album);
//			pref_file_album= Integer.parseInt(myPreferences.pref_file_album);		//
			pref_file_saisinn= myPreferences.pref_file_saisinn;	//最新更新日
			dbMsg += "、記録している最新更新日=" + pref_file_saisinn;
			if(!pref_file_saisinn.equals("")){
				String mod = sdffiles.format(new Date(Long.valueOf(pref_file_saisinn) * 1000));
				dbMsg += ">>" + mod;
			}
			dbMsg += "、再生中のプレイリストID=" + myPreferences.nowList_id;
			dbMsg += "、再生中のプレイリスト名=" + myPreferences.nowList;
			nowList = myPreferences.nowList;					//	playlistNAME
			dbMsg += "、再生中のファイル名=" + pref_data_url;
			pl_file_name = myPreferences.pref_commmn_music +File.separator;//汎用プレイリストのファイル名のパスまで
			dbMsg += "、汎用プレイリストのファイル名=" + pl_file_name;
			pref_zenkyoku_list_id = myPreferences.pref_zenkyoku_list_id;			// 全曲リスト
			saikintuika_list_id = myPreferences.saikintuika_list_id;			//最近追加
			saikinsisei_list_id = myPreferences.saikinsisei_list_id;			//最近再生

			myLog(TAG, dbMsg);
			if(pref_data_url == null || pref_data_url.equals("") ||
					! saisinnFileKakuninn( pref_file_kyoku + "" , pref_file_saisinn )
			){
				//	nowList == null || nowList.equals("") ||
				//
				preRead(MyConstants.syoki_Yomikomi , null);            //syoki_start_up
			}else {
				setteriYomikomi();            //状況に応じた分岐を行う
			}
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}																	//設定読込・旧バージョン設定の消去


	@SuppressLint("Range")
	public void setteriYomikomi(){		//<onCreate	プリファレンスに記録されているデータ読み込み、状況に応じた分岐を行う
		final String TAG = "setteriYomikomi";
		String dbMsg = "";
		long start = System.currentTimeMillis();		// 開始時刻の取得
		try{
			String maeList_id = null;		//リクエスト前のリスト
			String maeList = null;			//リクエスト前のリスト
			String maeDatFN = null;			//リクエスト前の再生ファイル
			String fName =  "/data/data/" +getPackageName()+"/shared_prefs/" + getString(R.string.pref_main_file) +".xml";
			dbMsg += "fName = " + fName;/////////////////////////////////////
			File tFile = new File(fName);
			dbMsg += ">>有無 = " + tFile.exists();/////////////////////////////////////
			if( ! tFile.exists()){
				dbMsg += "shared_prefs無し";/////////////////////////////////////
			}
			//1.06ユーザーが居なくなったら消去
			String dName = "/data/data/" +getApplicationContext().getPackageName() + "/shared_prefs/" + getResources().getString(R.string.pref_artist_file) +".xml";
			dbMsg += ",artistList= " + dName;
			File chFile3 = new File(dName);
			dbMsg += " = " +chFile3.exists();
			if(chFile3.exists()){
				chFile3.delete();
			}
			dbMsg += ">再生中のリストは[" + nowList_id + "]" + nowList ;
			String dataFN = getPrefStr( "pref_data_url" ,"" , MuList.this);
			dbMsg += ",dataFN = " + dataFN;
			if(dataFN.equals("")){
				dataFN = null;
			}

			if(null == nowList ){																	//初期の未設定時など
				nowList=(String) getResources().getText(R.string.listmei_zemkyoku);				//再生中のプレイリスト名
			}else if( nowList.equals("null") ){
				nowList=(String) getResources().getText(R.string.listmei_zemkyoku);				//再生中のプレイリスト名
			}
			dbMsg += ">nowList>" + nowList;
			if( ! nowList.equals(getResources().getText(R.string.listmei_zemkyoku)) ){
				String colName = "DATA";                   //MediaStore.Audio.Playlists.Members.DATA
				nowList_id = pref_zenkyoku_list_id;
				Cursor cursor = list_dataUMU( nowList_id ,colName, dataFN);				//指定した名前のリスト指定した項目のデータが有ればレコード情報をカーソルを返す
				if(cursor != null){
					if( cursor.moveToFirst() ){
						dbMsg += "、" + nowList + "に"+ cursor.getCount() + "件";
						mIndex = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.PLAY_ORDER));
						dbMsg += ",mIndex=" + mIndex + "曲目";
					} else {    	//見つからなければ
						dbMsg += ";指定リストに無し" + nowList;
						nowList = String.valueOf(getResources().getText(R.string.listmei_zemkyoku));
					}
				} else{
					nowList = String.valueOf(getResources().getText(R.string.listmei_zemkyoku));
				}
			}

			dbMsg += ">最終；nowList>" + nowList;
			if( nowList.equals(getResources().getText(R.string.listmei_zemkyoku))){                               	  // && -1 < Integer.valueOf(nowList_id)
				nowList_id = pref_zenkyoku_list_id;
				nowList_data = null;
				dbMsg +=  ">修正結果[" + nowList_id + "]" + nowList + "" + dataFN;
				mIndex = musicPlaylist.getPlaylistItemOrder(nowList_id,dataFN);			//Item.getMPItem( senntakuItem );
//				mIndex = Item.getMPItem( dataFN);
				dbMsg += ",mIndex=" + mIndex + "曲目";
			}
			sousalistName = nowList;		//操作対象リスト名
			sousalistID = nowList_id;		//操作対象リストID
			sousalist_data = nowList_data;		//sousalistName
			dbMsg +=  ">操作対象リスト[" + sousalistID + "]" + nowList + "；" + sousalist_data;
			dbMsg +=  ",読み込まれたバージョンコード" + pref_sonota_vercord;//////////////このアプリのバージョンコード///////////////////////
			PackageManager packageManager = this.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
			pref_sonota_vercord = packageInfo.versionCode;					//このアプリのバージョンコード
			dbMsg += ",このアプリのバージョンコード="+ pref_sonota_vercord;
			if( MuList.this.nowList.equals(getResources().getString(R.string.playlist_namae_request))){				// );		//リクエストリスト
				Cursor cursor = listUMU(getResources().getString(R.string.playlist_namae_request));
				dbMsg +=  "全件="+cursor.getCount() + "件";
				if(cursor.moveToFirst()){
					String listID  = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID));
					dbMsg +=  "listID["+ listID + "]";
					String listName  = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME));
					dbMsg +=  listName;
					Uri uri2 = MediaStore.Audio.Playlists.Members.getContentUri("external", Long.valueOf(listID));
					String[] columns = null;			//{ idKey, nameKey };
					String c_selection = null;				//MediaStore.Audio.Playlists.NAME +" = ? ";
					String[] c_selectionArg= null;				//{ String.valueOf(listName) };		//⑥引数groupByには、groupBy句を指定します。
					String c_orderBy = null;			//MediaStore.Audio.Playlists.Members.PLAY_ORDER;
					Cursor cursol2 = this.getContentResolver().query(uri2, columns, c_selection, c_selectionArg, c_orderBy);
					int count  = cursol2.getCount();
					dbMsg +=  ";count=" +cursol2 +"件";
					if(cursol2.moveToFirst()){
					}else{
						if( maeList != null ){
							nowList = maeList;
							nowList_id = Integer.valueOf(maeList_id);				//再生中のプレイリストID
							dbMsg += ">>[" + nowList_id + "]" + nowList +";;" + maeDatFN;
						}
					}
					cursol2.close();
				}
				cursor.close();
			}
			if( nowList.equals(getResources().getString(R.string.listmei_zemkyoku)) ){
				dbMsg += "、" + nowList +"に" + maeDatFN +"は";
				nowList_id =-1;
			}
			shigot_bangou = jyoukyou_bunki ;			//ファイルに変更が有れば全曲リスト更新の警告/無ければURiリストの読み込みに

			long end=System.currentTimeMillis();		// 終了時刻の取得
			dbMsg += ";"+ (int)((end - start)) + "m秒で終了";
			myLog(TAG, dbMsg);
			shigot_bangou= jyoukyou_bunki;		//204；現在の状態に見合った分岐を行う

		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	//メニューボタンで表示するメニュー///////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCreateOptionsMenu(Menu flMenu) {
		getMenuInflater().inflate(R.menu.list_menu , flMenu);		//メニューリソースの使用
		return super.onCreateOptionsMenu(flMenu);
	}

	public boolean makeOptionsMenu(Menu flMenu) {	//ボタンで表示するメニューの内容
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu flMenu) {			//表示直前に行う非表示や非選択設定
		final String TAG = "onPrepareOptionsMenu";
		String dbMsg = "";
		dbMsg +=ORGUT.nowTime(true,true,true);/////////////////////////////////////
		try{
			dbMsg = "pref_list_simple=" + pref_list_simple;
			if( pref_list_simple ){				//シンプルなリスト表示（サムネールなど省略）
				flMenu.findItem(R.id.menu_list_simple).setVisible(false);				//シンプルリスト
				flMenu.findItem(R.id.menu_list_simple).setEnabled(false);				//シンプルリスト
				flMenu.findItem(R.id.menu_list_syousai).setVisible(true);					//詳細表示に切替</string>
				flMenu.findItem(R.id.menu_list_syousai).setEnabled(true);					//詳細表示に切替</string>
			}else{
				flMenu.findItem(R.id.menu_list_simple).setVisible(true);				//シンプルリスト
				flMenu.findItem(R.id.menu_list_simple).setEnabled(true);				//シンプルリスト
				flMenu.findItem(R.id.menu_list_syousai).setVisible(false);					//詳細表示に切替</string>
				flMenu.findItem(R.id.menu_list_syousai).setEnabled(false);					//詳細表示に切替</string>
			}
			flMenu.findItem(R.id.menu_item_plist_saikin_tuika).setEnabled(true);		//最近追加リスト作成/編集
			dbMsg +=",sousalistName=" + sousalistName;
			flMenu.findItem(R.id.list_contex_rename_playlist).setEnabled(true);		//リスト名変更
			flMenu.findItem(R.id.list_contex_del_playlist).setEnabled(true);				// このリストを削除</string>
			if( requestJikkoucyuu ){		//リクエスト実行中
				flMenu.findItem(R.id.menu_list_request_reset).setVisible(true);
				flMenu.findItem(R.id.menu_list_request_reset).setEnabled(true);
			}else {
				flMenu.findItem(R.id.menu_list_request_reset).setVisible(false);
				flMenu.findItem(R.id.menu_list_request_reset).setEnabled(false);
			}
			flMenu.findItem(R.id.menu_hihyoujiArtist).setEnabled(true);						// 表示されていないアーティスト</string>
			flMenu.findItem(R.id.menu_item_plist_zenkyoku_kousin).setEnabled(true);		//全曲リスト更新

			flMenu.findItem(R.id.menu_item_sonota_settei).setEnabled(true);			//設定
			flMenu.findItem(R.id.menu_syuusei_lisuto_hyouji).setEnabled(true);		//修正リスト表示</string>
//			if(locale.equals( Locale.JAPAN)){											//日本語の場合のみconstant for ja_JP.
				flMenu.findItem(R.id.menu_item_sonota_help).setEnabled(true);		//ヘルプ表示	MENU_HELP
//			}else{
//				flMenu.findItem(R.id.menu_item_sonota_help).setEnabled(false);		//ヘルプ表示	MENU_HELP
//			}
			flMenu.findItem(R.id.menu_item_sonota_end).setEnabled(true);				//終了	MENU_END
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final String TAG = "onOptionsItemSelected";
		String dbMsg = "";
		dbMsg += ORGUT.nowTime(true,true,true);/////////////////////////////////////
		try{
			dbMsg += "MenuItem="+item.getItemId()+"="+item.toString();
			String helpURL;
			boolean kakikomi = false;
			int nowSelectMenu = item.getItemId();
			myLog(TAG, dbMsg);
			switch (nowSelectMenu) {
			case R.id.menu_list_simple:						//シンプルリスト
				pref_list_simple =  true;
				dbMsg += "指定(false詳細)=" + pref_list_simple;
				setPrefStr("pref_list_simple", String.valueOf(pref_list_simple) ,  MuList.this);
				simpleSyousai( pref_list_simple );
				return true;
			case R.id.menu_list_syousai:						//詳細表示に切替
				pref_list_simple = false;
				dbMsg += "指定(false詳細)=" + pref_list_simple;
				setPrefStr("pref_list_simple", String.valueOf(pref_list_simple) ,  MuList.this);
				simpleSyousai( pref_list_simple );
				return true;
			case R.id.menu_item_plist_saikin_tuika:				//最近追加リスト作成/編集
				saikin_tuika();				//最近追加された楽曲の抽出(指定IF)
				return true;
			case R.id.list_contex_rename_playlist:					//リスト名変更
				playListHnkou( getResources().getString(R.string.list_contex_rename_playlist));		//指定したプレイリストの操作
				return true;
			case R.id.list_contex_del_playlist:						// このリストを削除</string>
				playListHnkou( getResources().getString(R.string.list_contex_del_playlist));		//指定したプレイリストの操作
				return true;
			case R.id.menu_list_request_reset:				//リクエストリセット
				requestListReset();								//リクエスト撤回
				return true;
			case R.id.menu_hihyoujiArtist:				//	// 表示されていないアーティスト</string>
				hihyoujiArtist();							//非表示アーティスト対策
				return true;
			case R.id.menu_m3uRead:						//汎用プレイリスト読み込み
				readm3U2();
				return true;
			case R.id.menu_item_plist_zenkyoku_kousin:						//全曲リスト更新
				shigot_bangou = 0;
				musicPlaylist.deletPlayList(MuList.this.getResources().getString(R.string.all_songs_file_name));
				preRead(MyConstants.syoki_Yomikomi , null);
				return true;
				//http://d.hatena.ne.jp/ksk_kbys/20110822/1314028750
			case R.id.menu_item_sonota_settei:		//設定
				prefHyouji(settei_hyouji);			//設定表示
				return true;
			case R.id.menu_syuusei_lisuto_hyouji:			//修正リスト表示
				shyuuseiuListHyouji();			//作成されている修正リストの表示
				return true;
			case R.id.menu_item_sonota_help:						//ヘルプ表示	MENU_HELP
				Intent intent = new Intent(MuList.this,wKit.class);			//webでヘルプ表示
				if(locale.equals( Locale.JAPAN)){										//日本語の場合のみconstant for ja_JP.
					helpURL = "http://www.geocities.jp/hqu666/maramongs/list.html";		//日本語ヘルプ				//	helpURL = "file:///android_asset/list.html";		//日本語ヘルプ
				}else {
					helpURL = "http://www.geocities.jp/hqu666/maramongs/en/list.html";	//英語ヘルプ				//	helpURL = "file:///android_asset/en/list.html";	//英語ヘルプ
				}
				intent.putExtra("dataURI",helpURL);		//"file:///android_asset/index.html"
				intent.putExtra("fType","");		//"file:///android_asset/index.html"
				resultLauncher.launch(intent);
//			startActivity(intentWV);
				return true;
			case R.id.menu_item_sonota_end:					//終了	MENU_END
				quitMe();		//このアプリを終了する
				return true;
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}

	return false;
	}

	/**
	 * シンプルメニューと詳細メニューの切り替え
	 * @ boolean flag trueでシンプルメニュー
	 * */
	public void simpleSyousai( boolean flag ) {
		final String TAG = "simpleSyousa";
		String dbMsg = "";
		dbMsg += ORGUT.nowTime(true,true,true);/////////////////////////////////////
		try{
			pref_list_simple =  flag;
			dbMsg += "[" + sousalistID+ "]" + sousalistName+",reqCode=" + reqCode;
			if( sousalistName.equals(getResources().getString(R.string.listmei_zemkyoku)) ){		// 全曲リストのアーティスト選択
				dbMsg += ",Artist=" + sousa_artist + ",alubm=" + sousa_alubm + ",titol=" + sousa_titol;	////////////////sousa_alubmArtist
				sigotoFuriwake(reqCode, sousa_artist , sousa_alubm  , sousa_titol , null);		//表示するリストの振り分け		sousa_artist ,
			}else{
				switch(reqCode) {
					case MENU_2KAISOU:				//2階層リスト選択選択中
						plAlbumTitol( MuList.this.sousalistID ,  b_artist);		//指定したプレイリストから特定アーティストのアルバムとタイトル内容取得
						break;
					default:
						CreatePLList( MuList.this.sousalistID ,  MuList.this.sousalistName);		//プレイリストの内容取得	 MuList.this.sousalist_data,
						break;
				}
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	@Override
	public void onOptionsMenuClosed(Menu flMenu) {
		//Log.d("onOptionsMenuClosed","NakedFileVeiwActivity;mlMenu="+flMenu);
	}

///ContextMenu///http://techbooster.jpn.org/andriod/ui/7490///
	public String contextTitile = null;
	public String sousa_artist = null;
	public String sousa_alubmArtist = null;
	public String sousa_alubm = null;
	public String sousa_titol = null;
	public String tuikaSakiArtist = null;
	public int SelectLocation = 0;
	public SQLiteDatabase shyuusei_db;									//登録アーティスト修正DB
	public shyuuseiHelper shyuusei_Helper = null;				//登録アーティストヘルパー
	public String shyuuseiTName;
	public SQLiteStatement stmt = null ;			//6；SQLiteStatement
	public boolean henkou = false;
	public boolean sakiniSoroelu = false;			//書込み先に揃える
	public String mtoArtist;
	public String mtoAlubmArtist;
	public String mtoAlubm;
	public String mtoTitol;

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		final String TAG = "onCreateContextMenu";
		String dbMsg = "";
		try{
			dbMsg += "menu=" + menu +",v=" + v+ ",menuInfo=" + v;
			String selItem = itemStr;
			if(locale.equals( Locale.JAPAN)){											//日本語の場合のみconstant for ja_JP.
				contextTitile = itemStr +  getResources().getString(R.string.list_contex_title);						//の操作
			}else{
				contextTitile = getResources().getString(R.string.list_contex_title) + " of " + itemStr ;						//の操作
			}
			dbMsg += ",contextTitile = " + contextTitile+";";/////////////////////////////////////
			menu.setHeaderTitle(contextTitile);		//APIL1;リスト操作;コンテキストメニューの設定
			//Menu.add(int groupId, int itemId, int order, CharSequence title)
			dbMsg += ",reqCode=" + reqCode;/////////////////////////////////////
			if( sousalistName.equals(getResources().getString(R.string.listmei_zemkyoku)) ){		// 全曲リストのアーティスト選択
				switch(reqCode) {
				case MyConstants.v_titol:						//2131558448 タイトル
					menu.add(0, CONTEXT_add_request, 0, getResources().getString(R.string.list_contex_add_request));			//リクエスト
					menu.add(0, CONTEXT_add_playlist, 0, getResources().getString(R.string.list_contex_add_playlist));		//プレイリストに追加
					menu.add(0, CONTEXT_make_playlist, 0, getResources().getString(R.string.list_contex_make_playlist));		//新規プレイリストを作成して追加
					break;
				case MyConstants.v_artist:							//195アーティスト
				case MyConstants.v_alubum:							//196アルバム
					 albumCMCreate( menu);
					break;
				}
			}else{
				dbMsg += ",layerCode=" + layerCode;/////////////////////////////////////
				switch(layerCode) {
				case lyer_artist:				//アーティスト
				case lyer_album:				//アルバム
					albumCMCreate( menu);
					break;
				case lyer_titol:				//タイトル
					titolCMCreate( menu);
					break;
				}
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/** リストロングタップ時に表示するコンテキストメニューをリソースから読み込む*/
	public void albumCMCreate(ContextMenu menu) {
		final String TAG = "albumCMCreate";
		String dbMsg = "";
		try{
			menu.add(0, CONTEXT_listup, 0, getResources().getString(R.string.list_contex_listup));							//リストアップ編集
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/** リスト選択時に表示するコンテキストメニュー */
	public void titolCMCreate(ContextMenu menu) {					//, View v, ContextMenuInfo menuInfo
		final String TAG = "titolCMCreate";
		String dbMsg = "";
		try{
			dbMsg += "menu=" + menu;/////////////////////////////////////
			menu.add(0, CONTEXT_add_request, 0, getResources().getString(R.string.list_contex_add_request));		//リクエス
			menu.add(0, CONTEXT_add_playlist, 0, getResources().getString(R.string.list_contex_add_playlist));		//プレイリストに追加
			menu.add(0, CONTEXT_make_playlist, 0, getResources().getString(R.string.list_contex_make_playlist));		//新規プレイリストを作成して追加
			menu.add(0, CONTEXT_rem_playlist, 0, getResources().getString(R.string.list_contex_rem_playlist));		//このリストから削除</string>
			menu.add(0, CONTEXT_oc_playlist, 0, getResources().getString(R.string.list_contex_oc_playlist));		//次にタップする位置に移動</string>
			dbMsg +=",listKyokuSuu=" + listKyokuSuu;
			dbMsg +=",SelectLocation" + MuList.this.SelectLocation;
			if(1 ==listKyokuSuu){		//リストにある曲数
				menu.getItem(3).setEnabled(false);
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * リストロングタップ時に表示するコンテキストメニューの動作設定 */
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final String TAG = "onContextItemSelec";
		String dbMsg = "";
		try{
			artistPosition = -1;		//選択させるアーティスト
			alubmPosition = -1;		//選択させるアルバム
			titolePosition = -1;		//選択させるタイトル
			dbMsg += "item" + item.getItemId() + ")"+",SelectLocation" + MuList.this.SelectLocation;
			myLog(TAG, dbMsg);
			switch (item.getItemId()) {
			case CONTEXT_add_request:						//リクエスト
				requestList(MuList.this.itemStr);							//リクエストリスト作成
				return true;
			case CONTEXT_add_playlist:						//プレイリストに追加
				kizonListTuika(MuList.this.itemStr);
				return true;
			case CONTEXT_make_playlist:						//新規プレイリストを作成して追加
				makePlaylist(MuList.this.itemStr);			//選択した楽曲からプレイリストを新規作成する
				return true;
			case CONTEXT_rem_playlist:						//このリストから削除
				delOneLine(sousalistID, sousaRecordPlayOrder , MuList.this.itemStr);				//プレイリストから指定された行を削除する
				return true;
			case CONTEXT_oc_playlist:						//次にタップする位置に移動
				plJyouge( selPosition , MuList.this.itemStr);			//プレイリストの曲を上下移動させる
				return true;
			case CONTEXT_listup:									//リストアップ編集
				listSyuuseiJyunbi();								//リスト修正準備；アーティストと作曲者名リスト
				return true;
			case CONTEXT_delete:								//削除；デバッグ用；修正DBを消去
				String fn = getApplicationContext().getString(R.string.shyuusei_file);			//	shyuusei.db</string>
				File shyuFile = new File(fn);
				dbMsg += ",exists=" + shyuFile.exists();
				this.deleteDatabase(getApplicationContext().getString(R.string.shyuusei_file));		//デバッグ用		shyuusei_Helper.getDatabaseName()	getApplicationContext()
				dbMsg += ">作り直し>" + getApplicationContext().getDatabasePath(fn).getPath();	///data/data/com.hijiyam_koubou.marasongs/databases/artist.db
				preRead( MyConstants.syoki_Yomikomi , null);
				return true;
			default:
				return super.onContextItemSelected(item);
			}
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return true;
	}

	public List<String> artistHenkouSL=null;					//アーティストリスト用簡易リスト
	public List<Map<String, String>> parentList = null;
	public List<List<Map<String, String>>> allChildList = new ArrayList<List<Map<String, String>>>();
	public List<Map<String, String>> childList = new ArrayList<Map<String, String>>();
	public String b_sentouMoji = null;
	private final String KEY1 = "sentouMoji";
	private final String KEY2 = "ARTIST";

	public void listSyuuseiJyunbi() {								//リスト修正準備；アーティストと作曲者名リスト
		final String TAG = "listSyuuseiJyunbi";
		String dbMsg = "";
		try{
			dbMsg += ",SelectLocation" + MuList.this.SelectLocation;
			if(parentList != null){
				listSyuuseiStart();							//選択したアイテムの位置修正
			}else{
				artistHenkouSL =  new ArrayList<String>();				//アーティストリスト用簡易リスト
				artistHenkouSL.clear();
				if(Zenkyoku_db != null){
					if( Zenkyoku_db.isOpen()){
						Zenkyoku_db.close();
						dbMsg =  ">isOpen>" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
					}
				}
				String fn = getString(R.string.zenkyoku_file);			//全曲リスト名
				dbMsg +=  ",fn=" + fn;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
				zenkyokuHelper = new ZenkyokuHelper(MuList.this , fn);		//全曲リストの定義ファイル		.
				Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
				dbMsg =  ">isOpen>" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
				dbMsg =  ",getPageSize=" + Zenkyoku_db.getPageSize() + "件、" ;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
				String zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
				String[] c_columns =null;					//②引数tableには、テーブル名を指定します。
				String c_selection = null;
				String[] c_selectionArgs= null;
				String c_groupBy = "ARTIST";
				String c_having = null;
				String c_orderBy = "ARTIST"; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
				cursor = Zenkyoku_db.query(zenkyokuTName, c_columns, c_selection, c_selectionArgs , c_groupBy, c_having, c_orderBy);	// table, columns,new String[] {MotoN, albamN}
				dbMsg += ",getCount=" + cursor.getCount() + "件、" +",reqCode="+reqCode;
				reqCode = CONTEXT_listup_jyunbi ;					//リストアップ準備;アーティスト/作曲者名
				dbMsg += ">>"+reqCode;
				int koumoku = cursor.getColumnCount();
				String pdTitol = getResources().getString(R.string.list_contex_listup) +"" + getResources().getString(R.string.comon_jyunnbi);				//リストアップ編集	準備
				String pdMessage = getResources().getString(R.string.func_listup_jyunbi_msg);																// アーティスト名と作曲者名をリストアップします
				int retInt = cursor.getCount();
				dbMsg +=  "," + retInt + "件";
				plTask.execute(reqCode,cursor,pdTitol,pdMessage,cursor.getCount());
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	@SuppressLint("Range")
	public Cursor listSyuuseiJyunbiBody(Cursor cursor) {								//リスト修正準備；アーティストと作曲者名リスト
		final String TAG = "listSyuuseiJyunbiBody";
		String dbMsg = "";
		try{
			dbMsg += "[" + cursor.getPosition() + "/"+ cursor.getCount() + "]";
			@SuppressLint("Range") String comp = cursor.getString(cursor.getColumnIndex("ARTIST"));
		//	String comp = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
			dbMsg += "ARTIST = " + comp;
			artistHenkouSL = ORGUT.add2List(artistHenkouSL , comp);		//一致しない文字だけをリスト登録
			comp = cursor.getString(cursor.getColumnIndex("COMPOSER"));
			dbMsg +=  ",COMPOSER = " + comp;
			artistHenkouSL = ORGUT.add2List(artistHenkouSL , comp);		//一致しない文字だけをリスト登録
			dbMsg +=  ",artistHenkouSL = " + artistHenkouSL.size() + "件";
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return cursor;
	}

	public void listSyuuseiJyunbi2() {								//リスト修正準備；アーティストと作曲者名リスト
		final String TAG = "listSyuuseiJyunbi2";
		String dbMsg = "";
		try{
			cursor.close();
			Zenkyoku_db.close();
			Collections.sort(artistHenkouSL);																//ソートする
			int retInt = artistHenkouSL.size();
			dbMsg += retInt + "件";
			dbMsg += "(1)"+ artistHenkouSL.get(0) + "～(" + retInt + ")" + artistHenkouSL.get(retInt-1) ;

			parentList = new ArrayList<Map<String, String>>();
			allChildList = new ArrayList<List<Map<String, String>>>();
			b_sentouMoji = null;
			reqCode = CONTEXT_listup_jyunbi2 ;					//リストアップ準備;2階層リスト化
			String pdTitol = getResources().getString(R.string.list_contex_listup) +"" + getResources().getString(R.string.comon_jyunnbi);		//リストアップ編集	準備
			String pdMessage = getResources().getString(R.string.comon_kaisouka);																	//階層化
	//		myLog(TAG,dbMsg);
			plTask.execute(reqCode,null,pdTitol,pdMessage,retInt);
//			pTask = (plogTask) new plogTask(this ,  this ,pdTitol ,pdMessage ,retInt ).execute(reqCode,  pdMessage , retInt ,pdTitol );		//,jikkouStep,totalStep,calumnInfo
//			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void listSyuuseiJyunbi2Body(int i) {								//リストアップ準備;2階層リスト化
		final String TAG = "listSyuuseiJyunbi2Body";
		String dbMsg = "";
		try{
			dbMsg += "[" + i + "/"+ artistHenkouSL.size() + "]";
			String comp = String.valueOf(artistHenkouSL.get(i));
			dbMsg +=comp;
			String sentouMoji = comp.substring(0, 1);
			dbMsg +="[" + sentouMoji + "]";
			if(b_sentouMoji == null){															//一文字目
				dbMsg +="先頭データ";
				Map<String, String> parentData = new HashMap<String, String>();
				parentData.put("sentouMoji", sentouMoji);
				parentList.add(parentData);		// グループの親項目用のリストに内容を格納
				childList = new ArrayList<Map<String, String>>();
			}else if(! sentouMoji.equals(b_sentouMoji)){										//二文字目以降
				dbMsg +="切替わり";
				allChildList.add(childList);							//前の子リストを格納
				childList = new ArrayList<Map<String, String>>();		//次の子リストを作成
				Map<String, String> parentData = new HashMap<String, String>();
				parentData.put("sentouMoji", sentouMoji);
				parentList.add(parentData);								// グループの親項目用のリストに内容を格納
			}
			b_sentouMoji = sentouMoji;
			Map<String, String> childData = new HashMap<String, String>();
	//		childData.put("sentouMoji", sentouMoji);
			childData.put("ARTIST", comp);
			childList.add(childData);			// リストに文字を格納
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public AlertDialog lss_dlog;
	public void listSyuuseiStart() {								//選択したアイテムの位置修正;選択先のリスト表示～選択
		final String TAG = "listSyuuseiStart";
		String dbMsg = "";
		try{
			dbMsg += "artist" + sousa_artist + "(" + sousa_alubmArtist+")" + sousa_alubm + "/" + sousa_titol ;/////////////////////////////////////
			dbMsg += ",SelectLocation" + MuList.this.SelectLocation;
			contextTitile = sousa_artist;
			if( sousa_alubm != null){
				contextTitile = contextTitile + " / " +sousa_alubm;
			}
			if( sousa_titol != null){
				contextTitile = contextTitile + " / " +sousa_titol;
			}
			contextTitile = contextTitile + getResources().getString(R.string.func_listup_titol);				//	を選択したアーティストに統合します
			allChildList.add(childList);							//最後の子リストを格納
			String lastPearent = parentList.get(parentList.size()-1).get(KEY1);
			dbMsg += ",lastPearent=" + lastPearent;/////////////////////////////////////
			String lastPearenStrt = getResources().getString(R.string.comon_compilation)+ getResources().getString(R.string.comon_comp_sezu);
			if(! lastPearent.equals(lastPearenStrt) ){
				childList = new ArrayList<Map<String, String>>();		//次の子リストを作成
				Map<String, String> parentData = new HashMap<String, String>();
				parentData.put("sentouMoji", lastPearenStrt);			//コンピレーション	など
				parentList.add(parentData);								// グループの親項目用のリストに内容を格納
				Map<String, String> childData = new HashMap<String, String>();
				childData.put("ARTIST", getResources().getString(R.string.comon_compilation));
				childList.add(childData);			// リストに文字を格納
				allChildList.add(childList);							//前の子リストを格納
				childData = new HashMap<String, String>();
				childData.put("ARTIST", getResources().getString(R.string.artist_tuika02));				//サウンドトラック</string>
				childList.add(childData);			// リストに文字を格納
				allChildList.add(childList);							//前の子リストを格納
				childData = new HashMap<String, String>();
				childData.put("ARTIST", getResources().getString(R.string.artist_tuika03));				//クラシック</string>
				childList.add(childData);			// リストに文字を格納
				allChildList.add(childList);							//前の子リストを格納
				childData = new HashMap<String, String>();
				childData.put("ARTIST", getResources().getString(R.string.comon_nuKnow_artist));			//アーティスト情報なし</string>
				childList.add(childData);			// リストに文字を格納
				allChildList.add(childList);							//前の子リストを格納
			}
			SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter( this,
				parentList,
				android.R.layout.simple_expandable_list_item_1,
				new String[] { KEY1 },
				new int[] { android.R.id.text1, android.R.id.text2 },
				allChildList,
				android.R.layout.simple_expandable_list_item_2,
				new String[] {  KEY1, KEY2  },											// { KEY1, KEY2 },
				new int[] { android.R.id.text1, android.R.id.text1 }					//☆text1は大文字で折り返し有/text2は小文字で折り返し無し
				);
			myLog(TAG,dbMsg);
			LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);		// カスタムビューを設定
			final View layout = inflater.inflate(R.layout.list_dlog, findViewById(R.id.list_d_root_ll));
			AlertDialog.Builder lss_builder = new AlertDialog.Builder(this);			// アラーとダイアログ を生成
			lss_builder.setTitle( contextTitile );
			lss_builder.setView(layout);
			ExpandableListView list_d_elv = layout.findViewById(R.id.list_d_elv);
			list_d_elv.setAdapter(adapter);
			list_d_elv.setOnGroupClickListener(new OnGroupClickListener() {		// グループの親項目がクリックされた時の処理
				@Override
				public boolean onGroupClick(ExpandableListView parent, View view,int groupPosition, long id) {
					final String TAG = "onGroupClick.listSyuuseiStart";
					String dbMsg = "";
					try{
						ExpandableListAdapter adapter = parent.getExpandableListAdapter();
						@SuppressWarnings("unchecked")
						Map<String, String> item = (Map<String, String>)adapter.getGroup(groupPosition);		// クリックされた場所の内容情報を取得
						dbMsg += "クリック>"+ item.get(KEY1);
						myLog(TAG, dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
					return false;
				}
			});

			list_d_elv.setOnChildClickListener(new OnChildClickListener() {		// リスト項目がクリックされた時の処理
				@Override
				public boolean onChildClick(ExpandableListView parent, View view,int groupPosition, int childPosition, long id) {
					final String TAG = "onChildClick.listSyu";
					String dbMsg = "";
					try{
						ExpandableListAdapter adapter = parent.getExpandableListAdapter();
						@SuppressWarnings("unchecked")
						Map<String, String> item = (Map<String, String>)adapter.getChild(groupPosition, childPosition);		// クリックされた場所の内容情報を取得
						dbMsg += "KEY1=" + item.get(KEY1) + ",KEY2=" + item.get(KEY2) ;/////////////////////////////////////
						tuikaSakiArtist = item.get(KEY2);
						creditArtistName = tuikaSakiArtist;
						dbMsg +=  "を("+ item.get(KEY1) +")" +tuikaSakiArtist+",SelectLocation=" + MuList.this.SelectLocation;
						shyuuseiKakunit( tuikaSakiArtist , sousa_artist ,  sousa_alubmArtist ,  sousa_alubm , sousa_titol  );			//修正するかどうかの確認
						MuList.this.lss_dlog.dismiss();						//☆意図的に閉じなければ表示されたままになる
						myLog(TAG, dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
					return true;
				}
			});

			lss_builder.setNegativeButton(getResources().getString(R.string.comon_cyusi),  new DialogInterface.OnClickListener() {	//中止</string>
				@Override
				public void onClick(DialogInterface dialog, int which) {
					listReWrite( false );			//修正リスト操作後の再描画
//					//	☆再描画させないとThe content of the adapter has changed but ListView did not receive a notification
				}
			});
			lss_dlog = lss_builder.create();
			lss_dlog.setCancelable(true);		//戻るボタンでダイアログを閉じる
			lss_dlog.show();			// 表示
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public EditText le_saki_et;
	public void shyuuseiKakunit(final String tuikaSakiArtist , String mtoArtist , String mtoAlubmArtist , String mtoAlubm , String mtoTitol  ) {
		//選択されたアーティスト/アルバムの全曲を抽出修正するかどうかの確認
		 CharSequence[] items = null;
		final String TAG = "shyuuseiKakunit";
		String dbMsg = "";
		try{
			dbMsg += tuikaSakiArtist +"に" + mtoArtist + "(" + mtoAlubmArtist+")" + mtoAlubm + "/" + mtoTitol ;/////////////////////////////////////
			dbMsg +=",SelectLocation" + MuList.this.SelectLocation;
			contextTitile = mtoArtist;				//tuikaSakiArtist+ getResources().getString(R.string.comon_ni) + mtoArtist   ;
			if( sousa_alubm != null){
				contextTitile = contextTitile + " / " +sousa_alubm;
			}
			if( sousa_titol != null){
				contextTitile = contextTitile + " / " +sousa_titol;
			}
			contextTitile = contextTitile+ " ; " + getResources().getString(R.string.shyuuseiKakunit_dt);				//リストアップ先変更</string>
			AlertDialog.Builder builder = new AlertDialog.Builder(this);		//AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			builder.setTitle( contextTitile );				//リストアップ編集	getResources().getString(R.string.list_contex_listup)
			LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.list_shyuusei,null);
			final View lEditDlog = inflater.inflate(R.layout.list_shyuusei,null);				//(ViewGroup)findViewById(R.id.alertdialog_layout)
			le_saki_et = lEditDlog.findViewById(R.id.le_saki_et);						//final EditText
			le_saki_et.setText(tuikaSakiArtist);
			TextView le_msg_tv = lEditDlog.findViewById(R.id.le_msg_tv);
			final CheckBox le_cb = lEditDlog.findViewById(R.id.le_cb);
			le_cb.setChecked(sakiniSoroelu);
			builder.setView(lEditDlog);
			le_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {			// チェック状態が変更された時のハンドラ
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				String dbMsg = "[MuList]isChecked="+ isChecked;/////////////////////////////////////
				dbMsg += ",le_cb="+ le_cb.isChecked();/////////////////////////////////////
				sakiniSoroelu = le_cb.isChecked();			//選択したアーティスト名で表示
				dbMsg +=  ",sakiniSoroelu="+ sakiniSoroelu;/////////////////////////////////////
				}
			});
			builder.setPositiveButton(getResources().getString(R.string.comon_kakutei), new  DialogInterface.OnClickListener(){	//確定</string>
				@Override
				public void onClick(DialogInterface dialog, int idx) {
					final String TAG = getResources().getString(R.string.comon_kakutei);
					String dbMsg = "";
					try{
						dbMsg +=  MuList.this.sousa_artist +"/" + MuList.this.sousa_alubm;
						MuList.this.tuikaSakiArtist = MuList.this.le_saki_et.getText().toString();
						dbMsg +=">tuikaSakiArtist=" + MuList.this.tuikaSakiArtist;
						dbMsg += ",SelectLocation=" + MuList.this.SelectLocation;
		//				myLog(TAG,dbMsg);
						shyuuseiuList( MuList.this.tuikaSakiArtist,  sousa_artist ,  sousa_alubmArtist ,  sousa_alubm , sousa_titol  );			//修正リスト作成
						myLog(TAG, dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}
			});
			builder.setNegativeButton(getResources().getString(R.string.comon_cyusi),  new DialogInterface.OnClickListener() {	//中止</string>
				@Override
				public void onClick(DialogInterface dialog, int which) {
					listReWrite( false );			//修正リスト操作後の再描画
				}
			});
			builder.setCancelable(false);
			builder.show();
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * 確定；選択されたアーティスト/アルバムの全曲を抽出修正するかどうかの確認*/
	@SuppressLint("NewApi")
	@SuppressWarnings("resource")
	public void shyuuseiuList(String tuikaSakiArtist , String mtoArtist , String mtoAlubmArtist , String mtoAlubm , String mtoTitol) {				//修正リスト作成
		 CharSequence[] items = null;
		final String TAG = "shyuuseiuList";
		String dbMsg = "";
		try{
			this.mtoArtist = mtoArtist;
			this.mtoAlubmArtist = mtoAlubmArtist;
			this.mtoAlubm = mtoAlubm;
			this.mtoTitol = mtoTitol;
			int syuuseiGoukei = 0 ;				//修正するレコードの合計
			int syuuseMae = 0;					//修正前のレコードの合計
			int syuuseKensuu = 0;			//修正レコード数
			int henkouKensuu = 0;			//変更レコード数
			long id = 0;
			dbMsg = "reqCode=" + reqCode ;
			dbMsg += ",SelectLocation" + MuList.this.SelectLocation + ",追加先は" + tuikaSakiArtist ;
			tuikaSakiArtist = Artist2albumAetist(tuikaSakiArtist );
			dbMsg +=  "(" + tuikaSakiArtist +")" +"へ~" + mtoArtist  ;
			mtoAlubmArtist = Artist2albumAetist(mtoArtist );
			dbMsg += "(" + mtoAlubmArtist+"）のアルバム=" + mtoAlubm + ",タイトル=" + mtoTitol +"をリストアップ先修正" + ",Zenkyoku_db=" + Zenkyoku_db;
			String fn = getString(R.string.zenkyoku_file);			//全曲リスト名
			if(Zenkyoku_db == null){
				zenkyokuHelper = new ZenkyokuHelper(getApplicationContext() , fn);		//全曲リストの定義ファイル		.MuList.this
				Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
			}
			if( ! Zenkyoku_db.isOpen()){
				Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
			}
			String zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
			String[] c_columns =null;					//②引数tableには、テーブル名を指定します。
			String c_selection = "ARTIST = ? ";
			String c_orderBy= "TRACK"; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
			Cursor cursor;
			if( mtoAlubm == null ){
				dbMsg += mtoArtist + "は" ;
				String[] c_selectionArgs= { mtoArtist };
				cursor = Zenkyoku_db.query(zenkyokuTName, c_columns, c_selection, c_selectionArgs , null, null, c_orderBy);	// table, columns,new String[] {MotoN, albamN}
			}else{
				dbMsg += mtoAlubmArtist +"の" + mtoAlubm  + "は" ;
				c_selection = "ALBUM_ARTIST = ? AND ALBUM = ?";			//= "ALBUM_ARTIST LIKE ? AND ALBUM = ?";			SORT_NAME
				String[] c_selectionArgs2= { mtoAlubmArtist , mtoAlubm };
				cursor = Zenkyoku_db.query(zenkyokuTName, c_columns, c_selection, c_selectionArgs2 , null, null, c_orderBy);	// table, columns,new String[] {MotoN, albamN}
			}
			syuuseKensuu = cursor.getCount();			//修正レコード数
			dbMsg += ",Zenkyoku_db中,該当=" + syuuseKensuu+ "件、" ;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
			if(cursor.moveToFirst()){
				fn = getApplicationContext().getString(R.string.shyuusei_file);			//	shyuusei.db</string>
				dbMsg += ",db=" + fn;
				File shyuFile = new File(fn);
				shyuusei_Helper = new shyuuseiHelper(getApplicationContext() , fn);		//全曲リストの定義ファイル		.this.cContext.
				dbMsg += ">作り直し>" + getApplicationContext().getDatabasePath(fn).getPath();	///data/data/com.hijiyam_koubou.marasongs/databases/artist.db
				dbMsg += ",exists=" + shyuFile.exists();
				dbMsg += ",exists=" + shyuFile.exists();
				if( ! shyuFile.exists()){
					shyuusei_db =  SQLiteDatabase.openOrCreateDatabase(fn, null);	//String path, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler				//アーティスト名のえリストファイルを読み書きモードで開く
//					shyuusei_db =  getApplicationContext().openOrCreateDatabase(fn, SQLiteDatabase.OPEN_READWRITE, null);	//String path, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler				//アーティスト名のえリストファイルを読み書きモードで開く
					shyuusei_db.close();
				}
				shyuuseiTName = getResources().getString(R.string.shyuusei_table);			//	<string name="">shyuusei_table</string>
				shyuusei_db = shyuusei_Helper.getWritableDatabase();			// データベースをオープン
				dbMsg +=  " , shyuusei_db = " + shyuusei_db.getPageSize() +"件";//////
				Cursor cursorS = shyuusei_db.query(shyuuseiTName, null, null, null , null, null, null);	// table, columns,new String[] {MotoN, albamN}
				syuuseMae=cursorS.getCount();					//修正前のレコードの合計

				henkouKensuu =shyuuseiuListRDelLoop(mtoArtist , mtoAlubm);			//作成されている修正リストから選択されたレコードを消去する

				if( mtoAlubm == null ){
					c_selection = "ARTIST = ? ";
					String[] c_selectionArgs= { mtoArtist };
					cursorS = shyuusei_db.query(shyuuseiTName, c_columns, c_selection, c_selectionArgs , null, null, c_orderBy);	// table, columns,new String[] {MotoN, albamN}
				}else{
					c_selection = "ARTIST = ? AND ALBUM = ?";
					String[] c_selectionArgs2= { mtoArtist , mtoAlubm };
					cursorS = shyuusei_db.query(shyuuseiTName, c_columns, c_selection, c_selectionArgs2 , null, null, c_orderBy);	// table, columns,new String[] {MotoN, albamN}
				}
				henkouKensuu = cursor.getCount();			//変更レコード数
				dbMsg += henkouKensuu +"件登録済み;";
				id = shyuuseiuListTuika( cursor , tuikaSakiArtist );		//修正リスト作成のレコード追加
				dbMsg += ",追加[" + id + "]";///////////////////
				cursorS.close();
				shyuusei_db.close();
			}
			cursor.close();
			contextTitile = null;
			if( henkouKensuu >0  ){						//変更レコード数
				contextTitile = henkouKensuu + getResources().getString(R.string.comon_ken) + getResources().getString(R.string.comon_henkou);	//件を変更</string>
			} else {
				contextTitile = henkouKensuu + getResources().getString(R.string.comon_ken) + getResources().getString(R.string.comon_wo_tuika);	//件を追加\n\n戻るキーで中止</string>
			}
			dbMsg +=  ",contextTitile=" + contextTitile;///////////////////
			if( contextTitile != null){
				String dlTitol = getResources().getString(R.string.menu_item_plist_zenkyoku_kousin);				//">全曲リスト更新</string>
			//	String dlTitol = getResources().getString(R.string.saisinnVerKakuninn_titol);			//ご利用ありがとうございます。</string>
		//		preReadJunbi(dlTitol , contextTitile);														//全曲再生リスト作成を促すダイアログ表示
				new AlertDialog.Builder(this)				//getActivity()
				.setTitle(getResources().getString(R.string.list_contex_listup))				//リストアップ編集
				.setMessage(contextTitile)
				.setPositiveButton(getResources().getString(R.string.menu_item_plist_zenkyoku_kousin), new DialogInterface.OnClickListener() {	//全曲リスト更新
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String TAG = "shyuuseiuList";
						String dbMsg = "";
						try {
							preRead( MyConstants.syoki_Yomikomi , null);
							myLog(TAG, dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG ,  dbMsg + "で" + e);
						}
					}
				})
				.setNeutralButton(getResources().getString(R.string.sentaku_wo_tudukeru),  new DialogInterface.OnClickListener() {	//選択を続ける</string>
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String TAG = "shyuuseiuList";
						String dbMsg = "";
						try {
							dbMsg += "SelectLocation=" + MuList.this.SelectLocation;
							dbMsg += ",選択していたリスト=" + b_reqCode;///////////////////
							reqCode = b_reqCode;										//元の選択リストに戻す
							switch(b_reqCode) {
							case MyConstants.v_artist:							//195アーティスト
								dbMsg += ",sousa_artist=" + sousa_artist ;				//それまで参照していたアーティスト名	b_artist
								dbMsg += ",artistSL="+artistSL.size() ;
								artistSL.remove(MuList.this.SelectLocation);		//アルバム名
								dbMsg += ">>"+artistSL.size() + "件";
								dbMsg += ",有無="+artistSL.indexOf(sousa_artist);
								dbMsg += ",artistAL="+artistAL.size() ;
								artistAL.remove(MuList.this.SelectLocation);		//アルバム名
								dbMsg += ">>"+artistAL.size() + "件";
								senntakuItem = artistSL.get(MuList.this.SelectLocation);
								dbMsg += ",次に選択されるのは="+ senntakuItem;
								if( pref_list_simple ){					//シンプルなリスト表示（サムネールなど省略）
									makePlainList( artistSL);			//階層化しないシンプルなリスト
								} else {
									setHeadImgList(artistAL );				//イメージとサブテキストを持ったリストを構成
								}
								break;
							case MyConstants.v_alubum:							//196アルバム
								dbMsg += ",sousa_alubm=" + sousa_alubm ;
								dbMsg += ",albumList="+albumList.size() ;
								albumList.remove(MuList.this.SelectLocation);		//アルバム名
								dbMsg += ">>"+albumList.size() + "件";
								dbMsg += ",有無="+albumList.indexOf(sousa_alubm);
								dbMsg += ",albumAL="+albumAL.size() ;
								albumAL.remove(MuList.this.SelectLocation);		//アルバム名
								dbMsg += ">>"+albumAL.size() + "件";
								senntakuItem = albumList.get(MuList.this.SelectLocation);
								dbMsg += ",次に選択されるのは="+ senntakuItem;
								if( pref_list_simple ){					//シンプルなリスト表示（サムネールなど省略）
									makePlainList( albumList);			//階層化しないシンプルなリスト
								} else {
									setHeadImgList(albumAL );				//イメージとサブテキストを持ったリストを構成
								}
								break;
							}
							myLog(TAG, dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG ,  dbMsg + "で" + e);
						}
					}
				})

				.setNegativeButton(getResources().getString(R.string.comon_cyusi),  new DialogInterface.OnClickListener() {	//name="">中止</string>
					@Override
					public void onClick(DialogInterface dialog, int which) {
						shyuuseiCyuusi( MuList.this.mtoArtist , MuList.this.mtoAlubmArtist , MuList.this.mtoAlubm , MuList.this.mtoTitol);			//修正リストから除外
					}
				})
				.setCancelable(false)
				.show();
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void shyuuseiCyuusi( String mtoArtist , String mtoAlubmArtist , String mtoAlubm , String mtoTitol) {			//修正リストから除外
		final String TAG = "shyuuseiCyuusi";
		String dbMsg = "";
		try{
			int syuuseiGoukei = 0 ;				//修正するレコードの合計
			int syuuseMae = 0;					//修正前のレコードの合計
			int henkouKensuu = 0;			//変更レコード数
			long id = 0;
			dbMsg += "Artist=" + mtoArtist  ;/////////////////////////////////////
			dbMsg += ",AlubmArtist=" + mtoAlubmArtist +",Alubm=" + mtoAlubm +",Titol=" + mtoTitol  ;/////////////////////////////////////
			String fn = getApplicationContext().getString(R.string.shyuusei_file);			//	shyuusei.db</string>
//			dbMsg += ",db=" + fn;
			File shyuFile = new File(fn);
			shyuusei_Helper = new shyuuseiHelper(getApplicationContext() , fn);		//全曲リストの定義ファイル		.this.cContext.
			if( ! shyuFile.exists()){
				shyuusei_db =  getApplicationContext().openOrCreateDatabase(fn, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE, null);	//String path, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler				//アーティスト名のえリストファイルを読み書きモードで開く
//				modeを SQLiteDatabase.OPEN_READWRITEから変更
// アーティスト名のえリストファイルを読み書きモードで開く
				shyuusei_db.close();
			}
			shyuuseiTName = getResources().getString(R.string.shyuusei_table);			//	<string name="">shyuusei_table</string>
//				dbMsg += "；全曲リストテーブル=" + zenkyokuTName;
			shyuusei_db = shyuusei_Helper.getWritableDatabase();			// データベースをオープン
	//		dbMsg +=  " , shyuusei_db = " + shyuusei_db;//////
			Cursor cursorS = shyuusei_db.query(shyuuseiTName, null, null, null , null, null, null);	// table, columns,new String[] {MotoN, albamN}
			syuuseMae=cursorS.getCount();					//修正前のレコードの合計
			dbMsg += " 修正前 = " + syuuseMae;//////
			if(cursorS.moveToFirst()){				//既に登録されているレコードのアップデート
				String where = "_id = ?";
				int dRows = 0;
				do{
					dbMsg += "\n(" +  cursorS.getPosition()+ "/" + cursorS.getCount() +"人目(id=";		// +"；" +"[" + id1 +"]" + aName1+";" + song1+"曲";
					@SuppressLint("Range") String compIndex = cursorS.getString(cursorS.getColumnIndex("_id"));
					@SuppressLint("Range") String tStr = cursorS.getString(cursorS.getColumnIndex("ARTIST"));
					dbMsg +=  compIndex +")" + tStr;
					if(mtoArtist.equals(tStr)){
						String[] selectionArgs = {compIndex};
						int rRow = shyuusei_db.delete(shyuuseiTName, where, selectionArgs);
						dRows += dRows+rRow;
						dbMsg +=  ""+dRows +"削除";		// +"；" +"[" + id1 +"]" + aName1+";" + song1+"曲";
						myLog(TAG, dbMsg);
					}
				}while( cursorS.moveToNext() );
			}
			cursorS = shyuusei_db.query(shyuuseiTName, null, null, null , null, null, null);	// table, columns,new String[] {MotoN, albamN}
			syuuseiGoukei = cursorS.getCount();				//修正するレコードの合計
			dbMsg += ",修正後レコード" + syuuseiGoukei + "件";///////////////////
			cursorS.close();
			shyuusei_db.close();
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/** 修正リストのレコード修正 shyuusei_dbから変更するアーティスト名、アルバム名で抽出したレコード*/
	@SuppressLint("Range")
	public void shyuuseiuListSyuusei(Cursor cursorS , String rArtistName) {			//修正リストのレコード修正
		 CharSequence[] items = null;
		final String TAG = "shyuuseiuListSyuusei";
		String dbMsg = "";
		try{
			dbMsg = "ALBUM_ARTISTを" +  rArtistName +"に変更";		// +"；" +"[" + id1 +"]" + aName1+";" + song1+"曲";
			ContentValues cv = new ContentValues();
			ArrayList<String> udIdList = new ArrayList<String>();	//アップデートするID

			do{
				dbMsg += "\n書込み" + ( cursorS.getPosition() + 1 ) + "/" + cursorS.getCount() +"人目(id=";		// +"；" +"[" + id1 +"]" + aName1+";" + song1+"曲";
				@SuppressLint("Range") String compIndex = cursorS.getString(cursorS.getColumnIndex("_id"));
				dbMsg +=compIndex +")";		// +"；" +"[" + id1 +"]" + aName1+";" + song1+"曲";
				dbMsg += cursorS.getString(cursorS.getColumnIndex("DATA"));
				udIdList.add(compIndex);							//アップデートするID
				dbMsg +="(" + udIdList.size()+"件目)";
			}while( cursorS.moveToNext() );
			String where = "_id = ?";
			for(String rID : udIdList){
				dbMsg +="(" +  rID + ")" ;				// ZenkyokuList.this.compIndex;
				cv.put("ALBUM_ARTIST", rArtistName);
					String[] selectionArgs = {rID};
				if( sakiniSoroelu ){			//書込み先に揃える
					dbMsg += ">creditArtistName>" + creditArtistName;
					cv.put("ARTIST", creditArtistName);
				}
				int rRow = shyuusei_db.update(shyuuseiTName, cv , where, selectionArgs);
				dbMsg += ">>" + rRow;
				dbMsg +="[" + rRow +"レコード更新]";/////////////////////////////////////
			}			//	for(String rID : udIdList){
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/** 修正リスト作成のレコード追加;  */
	public long shyuuseiuListTuika(Cursor cursor ,String tuikaSakiArtist) {			//修正リスト作成のレコード追加
		CharSequence[] items = null;
		long id = 0;
		final String TAG = "shyuuseiuListTuika";
		String dbMsg = "";
		try{
			shyuusei_db.beginTransaction();
			stmt = null;
			stmt = shyuusei_db.compileStatement("insert into " + shyuuseiTName +
					"(ARTIST,ALBUM_ARTIST,ALBUM,TRACK,TITLE,DURATION,YEAR,DATA,MODIFIED,COMPOSER,LAST_YEAR) values (?,?,?,?,?,?,?,?,?,?,?);");
			do{
				int pdCoundtVal = cursor.getPosition();
				dbMsg =  (pdCoundtVal + 1 ) + "/" + cursor.getCount()  + "曲目";				//reqCode + ": "+
				int cCount = 0;
				String[] columnNames = cursor.getColumnNames();
				dbMsg += columnNames.length + "項目";
				for(String cName:columnNames){
					cCount = cursor.getColumnIndex(cName) ;
					int rColIndex = cCount -2;
					if(-1 < rColIndex){
						dbMsg += "[" + rColIndex + "]" + cName;
						@SuppressLint("Range") String cVal = String.valueOf(cursor.getString(cursor.getColumnIndex(cName)));
						if( cName.equals("_id") ){
						} else if( cName.equals("id") ){
							stmt.bindString(cCount+2, String.valueOf(pdCoundtVal));		//1;id
						} else if( cName.equals("AUDIO_ID") ){
						} else if( cName.equals("SORT_NAME") ){
						} else if( cName.equals("ARTIST") ){
							dbMsg += ",追加先の名称に変更=" + sakiniSoroelu ;
							if( sakiniSoroelu ){							//（選択したアーティスト名で表示）書込み先に揃える場合は
								dbMsg += ">creditArtistName>" + creditArtistName ;
								stmt.bindString(rColIndex, String.valueOf(creditArtistName));			//選択したアーティスト名に
							} else{																						//揃える必要が無ければ
					//			dbMsg += ">creditArtistName>" + String.valueOf(cVal) ;
								stmt.bindString(rColIndex, cVal);							//そのまま書き写し
							}
						} else if( cName.equals("ALBUM_ARTIST") ){
							stmt.bindString(rColIndex, String.valueOf(tuikaSakiArtist));
							dbMsg += ">ALBUM_ARTISTを" + tuikaSakiArtist +"に変更";
						} else {
							if(cVal == null){
								stmt.bindString(rColIndex, "");
				//				dbMsg += ">空白文字"  ;
							}else{
								stmt.bindString(rColIndex, cVal);
								if( cName.equals("DATA") ){
									dbMsg += ";" + cVal;
								}
							}
						}
					}
				}
				id = stmt.executeInsert();
				dbMsg +=  "文字[" + id +"]に追加";///////////////////		ZenkyokuList.this.
			}while( cursor.moveToNext() );
			try{
				shyuusei_db.setTransactionSuccessful();
			} finally {
				shyuusei_db.endTransaction();
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return id;
	}

	public List<String> hyojiSL = null;									//表示リスト
	public List<Map<String, String>> hyojiAL;
	/**作成されている修正リストの表示 */
	public void shyuuseiuListHyouji() {			//作成されている修正リストの表示
		long id = 0;
		final String TAG = "shyuuseiuLis";
		String dbMsg = "";
		try{
			String fn = getApplicationContext().getString(R.string.shyuusei_file);			//	shyuusei.db</string>
			File shyuFile = new File(fn);
			shyuusei_Helper = new shyuuseiHelper(getApplicationContext() , fn);		//全曲リストの定義ファイル		.this.cContext.
			if( ! shyuFile.exists()){
				shyuusei_db =  getApplicationContext().openOrCreateDatabase(fn, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE, null);	//String path, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler				//アーティスト名のえリストファイルを読み書きモードで開く
				//String path, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler				//アーティスト名のえリストファイルを読み書きモードで開く
				   //  2019	SQLiteDatabase.OPEN_READWRITE
				shyuusei_db.close();
			}
			shyuuseiTName = getResources().getString(R.string.shyuusei_table);			//	<string name="">shyuusei_table</string>
			shyuusei_db = shyuusei_Helper.getReadableDatabase();			// データベースをオープン
			String[] c_columns =null;					//②引数tableには、テーブル名を指定します。
			String c_selection = null;
			String[] c_selectionArgs= null;
			String c_groupBy = "ARTIST";
			String c_having = null;
			String c_orderBy = "ARTIST"; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
			Cursor cursorS = shyuusei_db.query(shyuuseiTName, c_columns, c_selection, c_selectionArgs , c_groupBy, c_having, c_orderBy);
			int syuuse = cursorS.getCount();					//修正前のレコードの合計
			dbMsg += " 登録数 = " + syuuse + "件";//////
			if(cursorS.moveToFirst()){
				hyojiSL =  new ArrayList<String>();									//表示リスト
				hyojiAL = new ArrayList<Map<String, String>>();

				do{
					dbMsg += "\n(" +  cursorS.getPosition()+ "/" + cursorS.getCount() +"件目；";		// +"；" +"[" + id1 +"]" + aName1+";" + song1+"曲";
					@SuppressLint("Range") String compIndex = cursorS.getString(cursorS.getColumnIndex("_id"));
					Map<String, String> hyoujiData = new HashMap<String, String>();
					for(int i = 0 ; i < cursorS.getColumnCount() ; i++){
						dbMsg +=  i + ")";
						String cName = cursorS.getColumnName(i);
						dbMsg += cName;
						String cVal = cursorS.getString(i);
						dbMsg +=  ";" + cVal;
						if( cVal != null ){
							hyoujiData.put(cName , cVal);
						}
					}
					hyojiAL.add(hyoujiData);		// グループの親項目用のリストに内容を格納
					String lowData = hyojiAL.get(hyojiAL.size()-1).get("ARTIST");				//クレジットアーティスト名
					lowData = lowData + "\n(" + hyojiAL.get(hyojiAL.size()-1).get("ALBUM") +")";
					lowData = lowData + "\n>>" + hyojiAL.get(hyojiAL.size()-1).get("ALBUM_ARTIST") + "\n";
					hyojiSL.add(lowData);
				}while( cursorS.moveToNext() );
			}
			cursorS.close();
			shyuusei_db.close();
			final CharSequence[] items =hyojiSL.toArray(new CharSequence[hyojiSL.size()]);					//
			dbMsg += ",読取り=" + items.length;
			AlertDialog.Builder listDlg = new AlertDialog.Builder(this);			// リスト表示用のアラートダイアログ

			Builder li = listDlg.setItems(items,
				new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {			// リスト選択時の処理, which は、選択されたアイテムのインデックス
					final String TAG = "onClick";
					String dbMsg = "[MuList.listSyuuseiStart]";
					try{
			//			dbMsg += "artist=" + sousa_artist + "(" + sousa_alubmArtist+")" + sousa_alubm + "/" + sousa_titol ;/////////////////////////////////////
						dbMsg += "選択" +items[which].toString()+"を("+ which +")" +tuikaSakiArtist ;
						shyuuseiuListStuusei( which);		//作成されている修正リストの表示
						myLog(TAG, dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}
			});
			AlertDialog aDlog = listDlg.create();
			aDlog.setCancelable(true);
			aDlog.show();			// 表示
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public int syuuseiIndex;
	public int syuuseiID;
	public void shyuuseiuListStuusei(int which) {			//作成されている修正リストの表示
		long id = 0;
		final String TAG = "shyuuseiuListStuusei";
		String dbMsg = "";
		try{
			syuuseiIndex = which;
			dbMsg += "which=" + which;
			String dtitol = hyojiAL.get(which).get("ARTIST");				//クレジットアーティスト名
			String ALBUM = hyojiAL.get(which).get("ALBUM");
			if(ALBUM != null){
				dtitol = dtitol + "\n(" + ALBUM +")";
			}
			String ALBUM_ARTIST = hyojiAL.get(which).get("ALBUM_ARTIST");
			String dMessege = getResources().getString(R.string.shyuuseiuListStuusei_dm) + "\n" + ALBUM_ARTIST;					// name="">リストアップ先</string></string>
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			alertDialogBuilder.setTitle(dtitol);
			alertDialogBuilder.setMessage(dMessege);
			alertDialogBuilder.setPositiveButton(getResources().getString(R.string.comon_henkou),			//"">変更</string>
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String TAG = "shyuuseiuListStuusei";
						String dbMsg = "";
						try {
							MuList.this.syuuseiIndex = Integer.valueOf(MuList.this.hyojiAL.get(MuList.this.syuuseiIndex).get("_id"));				//クレジットアーティスト名
							MuList.this.sousa_artist = MuList.this.hyojiAL.get(MuList.this.syuuseiIndex).get("ARTIST");				//クレジットアーティスト名
							MuList.this.sousa_alubm = MuList.this.hyojiAL.get(MuList.this.syuuseiIndex).get("ALBUM");				//クレジットアーティスト名
							dbMsg += "[" + MuList.this.syuuseiIndex + "]" + sousa_artist + "/" + MuList.this.sousa_alubm ;/////////////////////////////////////
							listSyuuseiJyunbi();								//リスト修正準備；アーティストと作曲者名リスト
							myLog(TAG, dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG ,  dbMsg + "で" + e);
						}
					}
				});
			alertDialogBuilder.setNeutralButton(getResources().getString(R.string.comon_sakujyo),			// ng name="">削除</string>
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							final String TAG = "shyuuseiuListStuusei";
							String dbMsg = "";
							try {
								MuList.this.syuuseiID = Integer.valueOf(MuList.this.hyojiAL.get(MuList.this.syuuseiIndex).get("_id"));				//クレジットアーティスト名
								String mtoArtistt = MuList.this.hyojiAL.get(MuList.this.syuuseiIndex).get("ARTIST");				//クレジットアーティスト名
								String mtoAlubm = MuList.this.hyojiAL.get(MuList.this.syuuseiIndex).get("ALBUM");				//クレジットアーティスト名
								dbMsg += "[" + MuList.this.syuuseiIndex + "]" + mtoArtistt + "/" + MuList.this.mtoAlubm ;/////////////////////////////////////
								int kekka = shyuuseiuListRDelLoop(mtoArtistt , mtoAlubm);		//作成されている修正リストから選択されたレコードを消去する
								dbMsg += ",削除" + kekka + "件" ;/////////////////////////////////////
								myLog(TAG, dbMsg);
							} catch (Exception e) {
								myErrorLog(TAG ,  dbMsg + "で" + e);
							}
						}
					});
			alertDialogBuilder.setNegativeButton(getResources().getString(R.string.comon_cyusi),			// name="">中止</string>
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String TAG = "shyuuseiuListStuusei";
						String dbMsg = "";
						try {
							myLog(TAG, dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG ,  dbMsg + "で" + e);
						}
					}
				});
			alertDialogBuilder.setCancelable(true);				// アラートダイアログのキャンセルが可能かどうかを設定します
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();				// アラートダイアログを表示します
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/** shyuusei_dbから指定されたアーティストを削除 */
	@SuppressWarnings("resource")	//カーソルが閉じられない？
	public int shyuuseiuListRDelLoop(String mtoArtist , String mtoAlubm) {			//作成されている修正リストから選択されたレコードを消去する
		int henkouKensuu = 0;
		final String TAG = "shyuuseiuListRDelLoop";
		String dbMsg = "";
		try{
			Cursor cursorS;
			String fn = getApplicationContext().getString(R.string.shyuusei_file);			//	shyuusei.db</string>
			dbMsg += "db=" + fn;
			File shyuFile = new File(fn);
			shyuusei_Helper = new shyuuseiHelper(getApplicationContext() , fn);		//全曲リストの定義ファイル		.this.cContext.
			dbMsg += ">>" + getApplicationContext().getDatabasePath(fn).getPath() + ",exists=" + shyuFile.exists()+ ",exists=" + shyuFile.exists();
			if( ! shyuFile.exists()){
				shyuusei_db =  getApplicationContext().openOrCreateDatabase(fn, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE, null);	//String path, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler				//アーティスト名のえリストファイルを読み書きモードで開く
				shyuusei_db.close();
			}
			shyuuseiTName = getResources().getString(R.string.shyuusei_table);			//	<string name="">shyuusei_table</string>
			shyuusei_db = shyuusei_Helper.getWritableDatabase();			// データベースをオープン
			dbMsg +=  " , getPageSize = " + shyuusei_db.getPageSize();//////
			cursorS = shyuusei_db.query(shyuuseiTName, null, null, null , null, null, null);	// table, columns,new String[] {MotoN, albamN}
			int syuuseMae = cursorS.getCount();					//修正前のレコードの合計
			dbMsg += "修正前" + syuuseMae +"件;";
			cursorS.close();
			String c_selection = null;
			String[] c_columns = null;
			String c_orderBy = null;
			dbMsg += " , ARTIST = " + mtoArtist +  " , ALBUM = " + mtoAlubm;//////
			if( mtoAlubm == null ){
				c_selection = "ARTIST = ? ";
				String[] c_selectionArgs= { mtoArtist };
				cursorS = shyuusei_db.query(shyuuseiTName, c_columns, c_selection, c_selectionArgs , null, null, c_orderBy);	// table, columns,new String[] {MotoN, albamN}
			}else{
				c_selection = "ARTIST = ? AND ALBUM = ?";
				String[] c_selectionArgs2= { mtoArtist , mtoAlubm };
				cursorS = shyuusei_db.query(shyuuseiTName, c_columns, c_selection, c_selectionArgs2 , null, null, c_orderBy);	// table, columns,new String[] {MotoN, albamN}
			}
			henkouKensuu = cursorS.getCount();			//変更レコード数
			dbMsg += henkouKensuu +"件登録済み;";
			if(cursorS.moveToFirst()){				//既に登録されているレコードの削除
				do{
					@SuppressLint("Range") int compIndex = Integer.valueOf(cursorS.getString(cursorS.getColumnIndex("_id")));
					dbMsg += "[" + compIndex +"],";
					shyuuseiuListRDel(compIndex);			//作成されている修正リストから選択されたレコードを消去する
				}while(cursorS.moveToNext());
			}
			cursorS.close();
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return henkouKensuu;
	}

	/** shyuusei_dbから指定されたレコードを削除 */
	public void shyuuseiuListRDel(int compIndex) {			//作成されている修正リストから選択されたレコードを消去する
		long id = 0;
		final String TAG = "shyuuseiuListRDel";
		String dbMsg = "";
		try{
			shyuuseiTName = getResources().getString(R.string.shyuusei_table);			//	<string name="">shyuusei_table</string>
			shyuusei_db = shyuusei_Helper.getWritableDatabase();			// データベースをオープン
			Cursor cursorS = shyuusei_db.query(shyuuseiTName, null, null, null , null, null, null);	// table, columns,new String[] {MotoN, albamN}
			int syuuseMae = cursorS.getCount();					//修正前のレコードの合計
			dbMsg +=" 修正前 = " + syuuseMae;//////
			if(cursorS.moveToFirst()){				//既に登録されているレコードのアップデート
				String where = "_id = ?";
				int dRows = 0;
				String[] selectionArgs = {String.valueOf(compIndex)};
				int rRow = shyuusei_db.delete(shyuuseiTName, where, selectionArgs);
				dRows = dRows+rRow;
				dbMsg += ""+dRows +"削除";		// +"；" +"[" + id1 +"]" + aName1+";" + song1+"曲";
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/** 非表示アーティスト対策 */
	public void hihyoujiArtist() {
		final String TAG = "hihyoujiArtist";
		String dbMsg = "";
		try{
			if(parentList != null){
				hihyoujiArtistStart();							//選択したアイテムの位置修正
			}else{
				artistHenkouSL =  new ArrayList<String>();				//アーティストリスト用簡易リスト
				artistHenkouSL.clear();
				if(Zenkyoku_db != null){
					if( Zenkyoku_db.isOpen()){
						Zenkyoku_db.close();
						dbMsg += ">isOpen>" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
					}
				}
				String fn = getString(R.string.zenkyoku_file);			//全曲リスト名
				dbMsg += ",fn=" + fn;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
				zenkyokuHelper = new ZenkyokuHelper(MuList.this , fn);		//全曲リストの定義ファイル		.
				Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
				dbMsg += ">isOpen>" + Zenkyoku_db.isOpen()+ ",getPageSize=" + Zenkyoku_db.getPageSize() + "件、" ;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
				String zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
				String[] c_columns =null;					//②引数tableには、テーブル名を指定します。
				String c_selection = null;
				String[] c_selectionArgs= null;
				String c_groupBy = "ARTIST";
				String c_having = null;
				String c_orderBy = "ARTIST"; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
				cursor = Zenkyoku_db.query(zenkyokuTName, c_columns, c_selection, c_selectionArgs , c_groupBy, c_having, c_orderBy);	// table, columns,new String[] {MotoN, albamN}
				dbMsg += ",getCount=" + cursor.getCount() + "件、" ;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
				reqCode = MENU_hihyoujiArtist ;							//非表示になったアーティストの修正処理
				dbMsg += ",reqCode="+reqCode;
				int koumoku = cursor.getColumnCount();
				String pdTitol = getResources().getString(R.string.list_contex_listup) +"" + getResources().getString(R.string.comon_jyunnbi);				//リストアップ編集	準備
				String pdMessage = getResources().getString(R.string.func_listup_jyunbi_msg);																// アーティスト名と作曲者名をリストアップします
				int retInt = cursor.getCount();
				dbMsg +=  "," + retInt + "件";
				plTask.execute(reqCode,cursor,pdTitol,pdMessage,cursor.getCount());
//				pTask = (plogTask) new plogTask(this ,  this ,pdTitol ,pdMessage,retInt ).execute(reqCode,  pdMessage , cursor ,pdTitol );		//,jikkouStep,totalStep,calumnInfo
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/** 非表示アーティスト対策；アーティストと作曲者名リスト */
	public void hihyoujiArtist2() {
		final String TAG = "hihyoujiArtist2";
		String dbMsg = "";
		try{
			if(! cursor.isClosed()){
				cursor.close();
			}
			Collections.sort(artistHenkouSL);																//ソートする
			int retInt = artistHenkouSL.size();
			dbMsg += retInt + "件(1)"+ artistHenkouSL.get(0) + "～(" + retInt + ")" + artistHenkouSL.get(retInt-1) ;

			parentList = new ArrayList<Map<String, String>>();
			allChildList = new ArrayList<List<Map<String, String>>>();
			b_sentouMoji = null;
			reqCode = MENU_hihyoujiArtist2 ;							//非表示になったアーティストの修正処理;2階層化
			String pdTitol = getResources().getString(R.string.list_contex_listup) +"" + getResources().getString(R.string.comon_jyunnbi);		//リストアップ編集	準備
			String pdMessage = getResources().getString(R.string.comon_kaisouka);																	//階層化
			plTask.execute(reqCode,null,pdTitol,pdMessage,retInt);
//			pTask = (plogTask) new plogTask(this ,  this ,pdTitol ,pdMessage ,retInt ).execute(reqCode,  pdMessage , retInt ,pdTitol );		//,jikkouStep,totalStep,calumnInfo
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void hihyoujiArtistStart() {								//非表示アーティスト対策
		final String TAG = "hihyoujiArtistStart";
		String dbMsg = "";
		try{
			dbMsg += "artist" + sousa_artist + "(" + sousa_alubmArtist+")" + sousa_alubm + "/" + sousa_titol ;/////////////////////////////////////
			allChildList.add(childList);							//最後の子リストを格納
			SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter( this,
					parentList,
					android.R.layout.simple_expandable_list_item_1,
					new String[] { KEY1 },
					new int[] { android.R.id.text1, android.R.id.text2 },
					allChildList,
					android.R.layout.simple_expandable_list_item_2,
					new String[] {  KEY1, KEY2  },											// { KEY1, KEY2 },
					new int[] { android.R.id.text1, android.R.id.text1 }					//☆text1は大文字で折り返し有/text2は小文字で折り返し無し
					);
			LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);		// カスタムビューを設定
			final View layout = inflater.inflate(R.layout.list_dlog, findViewById(R.id.list_d_root_ll));
			AlertDialog.Builder lss_builder = new AlertDialog.Builder(this);			// アラーとダイアログ を生成
			String dTitile = getResources().getString(R.string.menu_hihyoujiArtist_dt);		//アーティスト名をそのままリストアップします</string>
			lss_builder.setTitle( dTitile );
			lss_builder.setView(layout);
			ExpandableListView list_d_elv = layout.findViewById(R.id.list_d_elv);
			list_d_elv.setAdapter(adapter);
			list_d_elv.setOnGroupClickListener(new OnGroupClickListener() {		// グループの親項目がクリックされた時の処理
				@Override
				public boolean onGroupClick(ExpandableListView parent, View view,int groupPosition, long id) {
					final String TAG = "onGroupClick.listSyuuseiStart[MuList]";
					String dbMsg = "";
					try{
						ExpandableListAdapter adapter = parent.getExpandableListAdapter();
						@SuppressWarnings("unchecked")
						Map<String, String> item = (Map<String, String>)adapter.getGroup(groupPosition);		// クリックされた場所の内容情報を取得
						dbMsg += "クリック>"+ item.get(KEY1);
						myLog(TAG, dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
					return false;
				}
			});

			list_d_elv.setOnChildClickListener(new OnChildClickListener() {		// リスト項目がクリックされた時の処理
				@Override
				public boolean onChildClick(ExpandableListView parent, View view,int groupPosition, int childPosition, long id) {
					final String TAG = "onChildClick.hihyoujiArtistStart[MuList]";
					String dbMsg = "";
					try{
						ExpandableListAdapter adapter = parent.getExpandableListAdapter();
						@SuppressWarnings("unchecked")
						Map<String, String> item = (Map<String, String>)adapter.getChild(groupPosition, childPosition);		// クリックされた場所の内容情報を取得
						dbMsg += "KEY1=" + item.get(KEY1) + ",KEY2=" + item.get(KEY2) ;/////////////////////////////////////
						tuikaSakiArtist = item.get(KEY2);
						creditArtistName = tuikaSakiArtist;
						dbMsg += "を("+ item.get(KEY1) +")" +tuikaSakiArtist ;/////////////////////////////////////
						hihyoujiArtistKakunit( tuikaSakiArtist , tuikaSakiArtist ,  tuikaSakiArtist , null , null  );			//修正するかどうかの確認		,  sousa_alubm , sousa_titol
						MuList.this.lss_dlog.dismiss();						//☆意図的に閉じなければ表示されたままになる
						myLog(TAG, dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
					return true;
				}
			});

			lss_builder.setNegativeButton(getResources().getString(R.string.comon_cyusi),  new DialogInterface.OnClickListener() {	//中止</string>
				@Override
				public void onClick(DialogInterface dialog, int which) {
					reqCode = MyConstants.v_artist;
					listReWrite( false );			//修正リスト操作後の再描画
				}
			});
			lss_dlog = lss_builder.create();
			lss_dlog.setCancelable(true);		//戻るボタンでダイアログを閉じる
			lss_dlog.show();			// 表示
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**選択されたアーティスト/アルバムの全曲を抽出修正するかどうかの確認*/
	public void hihyoujiArtistKakunit(final String tuikaSakiArtist , String mtoArtist , String mtoAlubmArtist , String mtoAlubm , String mtoTitol  ) {
		 CharSequence[] items = null;
		final String TAG = "hihyoujiArtistKakunit";
		String dbMsg = "";
		try{
			dbMsg += tuikaSakiArtist +"に" + mtoArtist + "(" + mtoAlubmArtist+")" + mtoAlubm + "/" + mtoTitol ;/////////////////////////////////////
			String dTitile = getResources().getString(R.string.menu_hihyoujiArtist_dt);		//アーティスト名をそのままリストアップします</string>
			AlertDialog.Builder builder = new AlertDialog.Builder(this);		//AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			builder.setTitle( dTitile );				//リストアップ編集	getResources().getString(R.string.list_contex_listup)
			builder.setMessage(tuikaSakiArtist);
			builder.setPositiveButton(getResources().getString(R.string.comon_kakutei), new  DialogInterface.OnClickListener(){	//確定</string>
				@Override
				public void onClick(DialogInterface dialog, int idx) {
					final String TAG = getResources().getString(R.string.comon_kakutei)+".hihyoujiArtistKakunit[MuList]";
					String dbMsg = "";
					try{
						dbMsg +=  "tuikaSakiArtist=" + MuList.this.tuikaSakiArtist;
						shyuuseiuList( MuList.this.tuikaSakiArtist,   MuList.this.tuikaSakiArtist ,   MuList.this.tuikaSakiArtist ,  null , null  );			//修正リスト作成
						myLog(TAG, dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}
			});
			builder.setNegativeButton(getResources().getString(R.string.comon_cyusi),  new DialogInterface.OnClickListener() {	//中止</string>
				@Override
				public void onClick(DialogInterface dialog, int which) {
					listReWrite( false );			//修正リスト操作後の再描画
				}
			});
			builder.setCancelable(false);
			builder.show();
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**  修正リスト操作後の再描画 	 * */
	public void listReWrite( boolean reMove ) {
		final String TAG = "listReWrite[MuList]";
		String dbMsg = "";
		try{
			dbMsg =  "元リスト中=" + SelectLocation;///////////////////
			//	☆再描画させないとThe content of the adapter has changed but ListView did not receive a notification
			dbMsg += ",reqCode=" + reqCode;///////////////////
			switch(reqCode) {						//7340?
			case MyConstants.v_artist:				//...7334アーティスト
				if( pref_list_simple ){					//シンプルなリスト表示（サムネールなど省略）
			//		dbMsg += "artistSL=" + artistSL;///////////////////
					if( artistSL== null ){
						myLog(TAG,dbMsg);				//
						artistList_yomikomi();
					}
					if(reMove){													//リスト編集後、済んだ項目を
						artistSL.remove(SelectLocation);				//削除
					}
					dbMsg += "=" + artistSL.get(SelectLocation);///////////////////
					makePlainList( artistSL);			//階層化しないシンプルなリスト
				} else {
					if(reMove){													//リスト編集後、済んだ項目を
						artistAL.remove(SelectLocation);
					}
					dbMsg += "=" + artistAL.get(SelectLocation);///////////////////
					setHeadImgList(artistAL );				//イメージとサブテキストを持ったリストを構成
				}
				break;
			case MyConstants.v_alubum:				//2131361804アルバム
				if( pref_list_simple ){					//シンプルなリスト表示（サムネールなど省略）
					if(reMove){													//リスト編集後、済んだ項目を
						albumList.remove(SelectLocation);
					}
					dbMsg += "=" + albumList.get(SelectLocation);///////////////////
		//			myLog(TAG,dbMsg);
					makePlainList( albumList);			//階層化しないシンプルなリスト
				} else {
					if(reMove){													//リスト編集後、済んだ項目を
						albumAL.remove(SelectLocation);
					}
					dbMsg += "=" + albumAL.get(SelectLocation);///////////////////
					setHeadImgList(albumAL );				//イメージとサブテキストを持ったリストを構成
				}
				break;
			case MyConstants.v_titol:						//2131361809	タイトル
				break;
			default:
				break;
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

//////////////////////////////////////////////////////////ContextMenu///
	  /**dataURIを読み込みながら欠けデータ確認、ZenkyokuListを読み込む*/
	public void preRead(int reqCode , String msg) throws IOException {
		final String TAG = "preRead";
		String dbMsg = "";
		try{
			long start = System.currentTimeMillis();		// 開始時刻の取得
			dbMsg += "[" + reqCode +"]";/////////////////////////////////////
			sousalistName = getResources().getString(R.string.listmei_zemkyoku);			//全曲リスト
			sousalistID = -1;		//操作対象リストID
			sousalist_data = null;		//操作対象リストのUrl
			Intent intent = new Intent(getApplication(),AllSongs.class);						//parsonalPBook.thisではメモリーリークが起こる
			intent.putExtra("reqCode",reqCode);														//処理コード
			resultLauncher.launch(intent);
//			startActivityForResult(intentZL , reqCode);
			long end=System.currentTimeMillis();		// 終了時刻の取得
			dbMsg += ";"+ (int)((end - start)) + "m秒で終了";
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**プリファレンス表示*/
	public void prefHyouji(int reqCode){
		final String TAG = "prefHyouji";
		String dbMsg = "";
		try{
			Intent intent = new Intent(MuList.this,MyPreferences.class);			//プリファレンス
			intent.putExtra("reqCode",reqCode);					//設定;
			if (android.os.Build.VERSION.SDK_INT < 14 ) {										// registerRemoteControlClient
				pref_lockscreen = false;
			}else{
				intent.putExtra("pref_lockscreen", pref_lockscreen);			//ロックスクリーンプレイヤー</string>
			}
			if (android.os.Build.VERSION.SDK_INT <11 ) {
				pref_notifplayer = false;
			}else{
				intent.putExtra("pref_notifplayer", pref_notifplayer);			//ノティフィケーションプレイヤー</string>
			}
			intent.putExtra("pref_pb_bgc", pref_pb_bgc);					//プレイヤーの背景	true＝Black"	http://techbooster.jpn.org/andriod/ui/10152/
			intent.putExtra("pref_compBunki",pref_compBunki);		//コンピレーション分岐点
			intent.putExtra("pref_cyakusinn_fukki",pref_cyakusinn_fukki);		// = true;		//終話後に自動再生
			dbMsg += "終話後に自動再生=" +pref_cyakusinn_fukki;/////////////////////////////////////
			resultLauncher.launch(intent);
//			startActivityForResult(intentPRF , reqCode);
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}	//このアプリを終了する/////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////メニューボタンで表示するメニュー//
	public void headKusei() {								//ヘッドエリアの構成物調整
		final String TAG = "headKusei[MuList]";
		String dbMsg = "";
		try{
			dbMsg +=  "nowList = " + nowList;
			dbMsg +=",reqCode = " + reqCode;
			switch(reqCode) {
			case listType_2ly2:				// = listType_2ly + 1;albumとtitolの２階層
			case MyConstants.v_alubum:							//2131427340アルバム
			case MyConstants.v_titol:
				headImgIV.setVisibility(View.GONE);
				mainHTF.setVisibility(View.VISIBLE);
				artistHTF.setVisibility(View.VISIBLE);			//ヘッダーのアーティスト名表示枠
				pl_sp.setVisibility(View.GONE);
				break;
			default:
				headKuseiDefault();								//ヘッドエリアの構成物調整
				break;
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void headKuseiDefault() {								//ヘッドエリアの構成物調整
		final String TAG = "headKuseiDefault";
		String dbMsg = "";
		try{
			dbMsg +=",reqCode = " + reqCode;

				headImgIV.setVisibility(View.GONE);
				mainHTF.setVisibility(View.GONE);
				pl_sp.setVisibility(View.VISIBLE);
				dbMsg +=",plNameSL = " + plNameSL;
				if( plNameSL == null ){
					makePlayListSPN(sousalistName);		//プレイリストスピナーを作成する
				}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

//リストスクロールで: W/ResourceType(18024): No package identifier when getting name for resource number 0x00000064

	public String dtitol = "" ;
	public String dMessege = "" ;
	public Cursor listUMUCursor1;
	Cursor list_dataUMUCursor;
	/**
	 * ①ⅱファイルに変更が有れば全曲リスト更新の警告
	 * */
	public void jyoukyouBunki(){
		final String TAG = "jyoukyouBunki";
		String dbMsg = "";
		try{
			String dataFN = getPrefStr( "pref_data_url" ,"" , MuList.this);
			dbMsg ="プリファレンス；端末内にあるファイル数=" + pref_file_kyoku + ",更新日=" + pref_file_saisinn;

			if( 0< pref_file_kyoku && ! pref_file_saisinn.equals("")){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				String modified = sdf.format(new Date(Long.valueOf(pref_file_saisinn)*1000));
				dbMsg += "=" + modified;//////////////////
				zenkyokuAri = true;			//全曲リスト有り
				if(saisinnFileKakuninn( pref_file_kyoku + "" , pref_file_saisinn )){			//端末内にあるファイル数と更新日の照合>>音楽ファイルが無ければ終了
					dbMsg += ">ファイル変更なし；再生中のファイル名=" + dataFN ;
					dbMsg += ",nowList[" + nowList_id  + "]" + nowList ;
					shigot_bangou = 0;
					if( nowList.equals(getResources().getString(R.string.listmei_zemkyoku)) ){		//全曲リストで	|| -1 == nowList_id
						if( dataFN != null && ! dataFN.equals("null")){																				//前回再生曲があれば
							dbMsg += ">>プレイヤーへ";
		//22020810一時停止					send2Player(dataFN,false);																			//プレイヤーにuriを送る
//						}else{																								//前回再生曲が無ければ
//							dtitol = getResources().getString(R.string.jyoukyouBunki_titol_t);							//選曲して下さい。
//							dMessege = getResources().getString(R.string.jyoukyouBunki_titol_m);							//（初めてのご利用か）前回再生していた曲が読み込めませんでした。</string>
//							readDB();										//全曲リストの読み込み
						}
					}else{																				//その他のリストを
						dbMsg += ">>全曲以外";
						if( MuList.this.nowList != null){												//使用した経過が有れば
							if( MuList.this.nowList.equals(getResources().getString(R.string.playlist_namae_saikintuika)) ){
								listUMUCursor1 = listUMU_addNew(MuList.this.nowList);								//そのプレイリストの情報を取得
							}else{
								listUMUCursor1 = listUMU(MuList.this.nowList);								//そのプレイリストの情報を取得
							}
							dbMsg +=  ",再生中のリスト="+ MuList.this.nowList + "には" + listUMUCursor1.getCount() + "件登録";
							if( listUMUCursor1.moveToFirst() ){
								if( dataFN != null && ! dataFN.equals("")){
									String colName =  MediaStore.Audio.Playlists.Members.DATA ;                      // MediaStore.Audio.Media.DATA = "_data"
									list_dataUMUCursor = list_dataUMU(nowList_id , colName , dataFN);			//指定した名前のリスト指定した項目のデータが有ればカーソルを返す	MediaStore.Audio.Playlists.Members.DATA
									dbMsg +=  ","+ dataFN + "を" + list_dataUMUCursor.getCount() + "件検出";
									if( list_dataUMUCursor.moveToFirst() ){
										if ( imanoJyoutai == veiwPlayer  ){											//200;プレイヤーを表示;起動直後
											//22020810一時停止								send2Player(dataFN,false );														//プレイヤーにuriを送る
										}
//									}else {																			//指定されたリストが無ければ
//										dtitol = getResources().getString(R.string.jyoukyouBunki_titol_t);		//選曲して下さい。
//										dMessege = getResources().getString(R.string.jyoukyouBunki_titol_m);		//（初めてのご利用か）前回再生していた曲が読み込めませんでした。</string>
									}
									list_dataUMUCursor.close();
//								}else{																			//再生する曲が無ければ
//									dtitol = getResources().getString(R.string.jyoukyouBunki_titol_t);		//選曲して下さい。
//									dMessege = getResources().getString(R.string.jyoukyouBunki_titol_m2);	//プレイリストから再生するファイルを選択して下さい。</string>
//									readDB();										//全曲リストの読み込み
								}
//							}else{																			//指定されたリストが無ければ
//								dtitol = getResources().getString(R.string.jyoukyouBunki_list_t);		//プレイリストを選択して下さい。
//								dMessege = getResources().getString(R.string.jyoukyouBunki_list_m);		//（初めてのご利用か）前回利用したリストが読み込めませんでした。既存のリストから選曲しますか?
							}
							listUMUCursor1.close();
						}
					}
				}else{		//ここで誤動作
					dbMsg +=  "最新状況確認でfalse";
					dtitol = getResources().getString(R.string.jyoukyouBunki_file_t);		//ファイルリストを更新します// アラートダイアログのタイトルを設定します
					dMessege = getResources().getString(R.string.jyoukyouBunki_file_m);		//音楽ファイルに変更がある様です。全曲リストを作り直してもよろしいですか？\n１～２分かかります。今お時間が無ければ"しない"でアプリを一旦終了します。
				}
			} else if(0 == pref_file_kyoku  || pref_file_saisinn.equals("")) {
				dbMsg +=  ",全曲リストは未作成でdataFN="+ dataFN;
				zenkyokuAri = false;			//全曲リスト有り
				if( dataFN == null ||  dataFN.equals("")){															//前回再生曲があれば
					dtitol = getResources().getString(R.string.syokairiyou_dt);				//全曲リストを作成作成させ下さい。
					dMessege = getResources().getString(R.string.syokairiyou_dm);				//１～２分かかりますが、ゲスト参加などで分離されたアルバムを統合して自然な連続再生を可能にします。\n
				} else {
//22020810一時停止					send2Player(dataFN,false );														//プレイヤーにuriを送る
				}
			} else {
				dbMsg +=  "；pref_file_kyoku == null || pref_file_saisinn == null";
				if( plNameSL == null){
					plNameSL = getPList();		//プレイリストを取得する
				}
				dbMsg +="plNameSL = " + plNameSL.size() +"件";
				if(0 < plNameSL.size()){
					dtitol = getResources().getString(R.string.jyoukyouBunki_list_t);		//プレイリストを選択して下さい。
					dMessege = getResources().getString(R.string.jyoukyouBunki_list_m);		//（初めてのご利用か）前回利用したリストが読み込めませんでした。既存のリストから選曲しますか?
				}else{
					dbMsg +=  "：,0 < plNameSL.size())";
					dtitol = getResources().getString(R.string.jyoukyouBunki_file_t);		//全曲リストを更新します// アラートダイアログのタイトルを設定します
					dMessege = getResources().getString(R.string.jyoukyouBunki_file_m2);		//再生できるプレイリストが見つかりません。全曲リストを作成させて下さい。</string>
				}
			}
			dbMsg +=",dtitol = " + dtitol;
			myLog(TAG,dbMsg);
			if(! dtitol.equals("") ) {                            //dtitol != null ||
				makePlayListSPN(sousalistName);        //プレイリストスピナーを作成する
				headKusei();                            //ヘッドエリアの構成物調整
				if ( dtitol.equals(getResources().getString(R.string.jyoukyouBunki_file_t)) ||        //全曲リストを更新します
							 dtitol.equals(getResources().getString(R.string.syokairiyou_dt))                    //全曲リストを作成作成させ下さい。
				) {
					preReadJunbi(dtitol , dMessege);                                        //全曲再生リスト作成を促すダイアログ表示
				} else {
					imanoJyoutai = 0;
					int ePosition = plNameSL.indexOf(MuList.this.nowList);
					dbMsg += ",ePosition=" + ePosition;
					pl_sp.setSelection(ePosition , false);                                //☆勝手に動作させない
					String subText = creditArtistName + " / " + albumName + " / " + titolName;
					subHTF.setText(subText);                //	toolbar.setSubtitle(subText);
					artistPosition = -1;        //選択させるアーティスト
					alubmPosition = -1;        //選択させるアルバム
					titolePosition = -1;        //選択させるタイトル
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
					alertDialogBuilder.setTitle(dtitol);
					alertDialogBuilder.setMessage(dMessege);
					alertDialogBuilder.setPositiveButton(getResources().getString(R.string.comon_suru) ,            //する
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog , int which) {
									final String TAG = "jyoukyouBunki";
									String dbMsg = "";
									try {
										if ( dtitol.equals(getResources().getString(R.string.jyoukyouBunki_file_t)) ||        //全曲リストを更新します
													 dtitol.equals(getResources().getString(R.string.syokairiyou_dt))                    //全曲リストを作成作成させ下さい。
										) {
											//			dismissDialog(0);//基本はこっち。showDialogを再度すると、前のダイアログが使いまわされる
											//			MyConstants.this.removeDialog(0);//手動でダイアログの消去と、ダイアログ使い回しを破棄をしたい場合。
											dbMsg += "ファイルに変更あり";/////////////////////////////////////
											myLog(TAG , dbMsg);
											preRead(MyConstants.syoki_Yomikomi , null);            //syoki_start_up
										} else if ( dtitol.equals(getResources().getString(R.string.jyoukyouBunki_list_t)) ) {            //プレイリストを選択して下さい。
//										} else if ( dtitol.equals(getResources().getString(R.string.jyoukyouBunki_titol_t)) ) {        //選曲して下さい。
//											nowList = getResources().getString(R.string.listmei_zemkyoku);                //全曲リスト
//											artistList_yomikomi();
										}
										myLog(TAG , dbMsg);
									} catch (Exception e) {
										myErrorLog(TAG , dbMsg + "で" + e);
									}
								}
							});
					alertDialogBuilder.setNegativeButton(getResources().getString(R.string.comon_sinai) ,            //しない
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog , int which) {
									final String TAG = "onClick";
									String dbMsg = "[MuList.jyoukyouBunki]";
									if ( dtitol.equals(getResources().getString(R.string.jyoukyouBunki_file_t)) ||        //全曲リストを更新します
												 dtitol.equals(getResources().getString(R.string.syokairiyou_dt))                    //全曲リストを作成作成させ下さい。
									) {
										if ( zenkyokuAri ) {
											nowList = getResources().getString(R.string.listmei_zemkyoku);                //全曲リスト
											artistList_yomikomi();
										} else {
											quitMe();        //このアプリを終了する
										}
									} else if ( dtitol.equals(getResources().getString(R.string.jyoukyouBunki_list_t)) ) {            //プレイリストを選択して下さい。
//									} else if ( dtitol.equals(getResources().getString(R.string.jyoukyouBunki_titol_t)) ) {        //選曲して下さい。
//										nowList = getResources().getString(R.string.listmei_zemkyoku);                //全曲リスト
//										artistList_yomikomi();
									}
									myLog(TAG , dbMsg);
								}
							});
					alertDialogBuilder.setCancelable(true);                // アラートダイアログのキャンセルが可能かどうかを設定します
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();                // アラートダイアログを表示します
					//		}
				}
			}else if( MuList.this.nowList.equals(getResources().getString(R.string.playlist_namae_saikintuika)) ){
				readDB_AddNew();							//最近追加リストの読み込み
			}else{
				readDB();										//全曲リストの読み込み
			}
			 if(dataFN == null){
				 dataFN = musicPlaylist.getPlaylistItemData(nowList_id , 1);
				 dbMsg += "、dataFN= " + dataFN ;
			 }

			 myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/** 音楽ファイルの登録状況　；無ければここで終了*/
	@SuppressLint("Range")
	public boolean saisinnFileKakuninn(String titolCo , String saisinn ){		//＜jyoukyouBunki；端末内にあるファイル数と更新日の照合>>音楽ファイルが無ければ終了
		boolean retBool = true;		//読み直す
		final String TAG = "saisinnFileKakuninn";
		String dbMsg = "";
		dbMsg += ORGUT.nowTime(true,true,true);/////////////////////////////////////
		try{
			Uri uriTo=Uri.parse("file://" + Environment.getExternalStorageDirectory()  );				//+"/Music/"	getExternalStorageDirectory API29で廃止
			audioScanFile(uriTo);		//MediaStore.Audio.Mediaの更新	API29で廃止

			dbMsg +=   "プリファレンス；ファイル数="+titolCo + ",最新="+saisinn;	//
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			String modified = sdf.format(new Date(Long.valueOf(saisinn)*1000));
			dbMsg += "=" + modified;//////////////////
			ContentResolver resolver = getApplicationContext().getContentResolver();	//c.getContentResolver();
			Uri cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
			String[] c_columns = null;		 		//③引数columnsには、検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
			String c_selection =  MediaStore.Audio.Media.IS_MUSIC +" <> ? "  ;
			String[] c_selectionArgs= {"0"  };   			//, null , null , null
			String c_orderBy=MediaStore.Audio.Media.DATE_MODIFIED  + " DESC"; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
			cursor = resolver.query( cUri , null , null , null, c_orderBy);
			int tCount = cursor.getCount();
			dbMsg +=">読取り="+ tCount + "件";			//+ cursor.getColumnCount() + "項目";
			if( cursor.moveToFirst() ){
				if (Integer.valueOf(titolCo) != tCount){
					dbMsg +=">>件数="+ tCount + ">>"+tCount + "件";
					setPrefStr( "pref_file_kyoku", String.valueOf(tCount),  MuList.this);         //	myEditor.putString( "pref_file_kyoku", String.valueOf(tCount));		//総曲数
				}
				int sisinV =0;
				if(saisinn != null && ! saisinn.equals("null")){
					sisinV = Integer.valueOf(saisinn);
				}
				@SuppressLint("Range") String kousinnbi = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));	//The time the file was last modified
				dbMsg +=";読み取った更新日="+ kousinnbi ;
				if( kousinnbi == null ){
					kousinnbi =  cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));
					dbMsg += ">追加日>"+ kousinnbi;/////////////////////////////////////////////////////////////////////////////////////////////
				}
				modified = sdf.format(new Date(Long.valueOf(kousinnbi)*1000));
				dbMsg += "=" + modified;//////////////////
				if( kousinnbi != null ){
		//			dbMsg += ">>"+kousinnbi;
					if (sisinV != Integer.valueOf(kousinnbi)){
//						myEditor.putString( "pref_pref_file_saisinn", kousinnbi);					//最新更新日
//						myEditor.commit();	// データの保存
						retBool = false;
						dbMsg +=">>読直さない"+ retBool;
					}
				}
			}else{
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
				alertDialogBuilder.setTitle(getResources().getString(R.string.saisinnFileKakuninn_f_titol));		//音楽ファイルを確認できません
				alertDialogBuilder.setMessage(getResources().getString(R.string.saisinnFileKakuninn_f_meg));		//この端末に再生できる音楽ファイルが見つかりません。\nメモリーカードの装着や音楽ファイルの有無を確認してください
				alertDialogBuilder.setPositiveButton(getResources().getString(R.string.saisinnFileKakuninn_f_btn),	//一旦終了
					new DialogInterface.OnClickListener() {			// アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
					@Override
					public void onClick(DialogInterface dialog, int which) {
						quitMe();		//このアプリを終了する
						}
					});
				alertDialogBuilder.setCancelable(true);			// アラートダイアログのキャンセルが可能かどうかを設定します
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();			// アラートダイアログを表示します
			}
			cursor.close();
			dbMsg +=">>読直さない"+ retBool;
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return retBool;
	}

	public void audioScanFile(Uri uriTo)  {			//MediaStore.Audio.Mediaの更新
		final String TAG = "audioScanFile[MuList]";
		String dbMsg = "";
		try{
			dbMsg +=",uriToSD="+ uriTo.toString() ;
			File _file = new File(uriTo.toString() );
			dbMsg +=",file="+ _file.getName() ;
			String[] mimeTypes  = new String[] { "audio/mp3" , "audio/aac" , "audio/x-ms-wma" };
			dbMsg +=",mimeTypes="+ mimeTypes.length+"項目" ;
			MediaScannerConnection.scanFile(getApplicationContext(),
				new String[] { _file.getPath() }, mimeTypes ,
				new OnScanCompletedListener() {
					@Override
					public void onScanCompleted(String path, Uri uri) {
						final String TAG = "onScanCompleted[MuList]";
						String dbMsg = ",path="+ path;
					//	dbMsg += ",uri="+ uri.toString() ;
						myLog(TAG, dbMsg);
					}
				});
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void readDB(){													//①ⅲ全曲リストの読み込み		<onCreate , jyoukyouBunki ,
		final String TAG = "readDB";
		String dbMsg = "";
		try{
			String fn = getResources().getString(R.string.zenkyoku_file);			//全曲リスト名
			dbMsg += "fn=" + fn;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
			zenkyokuHelper = new ZenkyokuHelper(MuList.this , fn);		//全曲リストの定義ファイル		.
			File dbF = getDatabasePath(fn);
			dbMsg += ",dbF=" + dbF;
			dbMsg += " , exists=" + dbF.exists() +" , canWrite=" + dbF.canWrite();
			if(dbF.exists() ){
				if( Zenkyoku_db == null){
					Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
				}
				dbMsg += ">isOpen>" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
		//		myLog(TAG,dbMsg);
				if(! Zenkyoku_db.isOpen()){
					dbMsg += ">>delF=" + dbF.getPath();
					Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
					dbMsg += ">isOpen>" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
					zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
					dbMsg +=  ",全曲リストのテーブル名=" + zenkyokuTName;
				}
				if ( imanoJyoutai != veiwPlayer ){
					dbMsg += ",reqCode= " + reqCode;
					reqKariCode = reqCode;
				}
				reqCode = MyConstants.v_artist;							//アーティスト
				dbMsg += ",artistAL= " + artistAL;
				if(artistAL != null){
					dbMsg += "L= " + artistAL.size();
					if( artistAL.size() >0  ){
						shyouMain(reqCode);		//onCleateの続きで仕掛けの仕込み
					} else {
						myLog(TAG,dbMsg);				//
						artistList_yomikomi();								//アーティストリストを読み込む(db未作成時は-) ☆フォーカスはダイアログに移る
						shigot_bangou = shyou_Main ;					//アーティストリスト作成後、作成するリストの振り分けに
					}
				} else {
					myLog(TAG,dbMsg);				//
					artistList_yomikomi();								//アーティストリストを読み込む(db未作成時は-) ☆フォーカスはダイアログに移る
					shigot_bangou = shyou_Main ;					//アーティストリスト作成後、作成するリストの振り分けに
				}
				zenkyokuAri = true;			//全曲リスト有り
			} else{
				zenkyokuAri = false;			//全曲リスト有り
				String dMeseege = getResources().getString(R.string.jyoukyouBunki_file_m);		//">音楽ファイルに変更がある様です。
				dbMsg += "全曲リストが無い";//////////////////

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
				alertDialogBuilder.setTitle(getResources().getString(R.string.jyoukyouBunki_file_t));		//"">ファイルリストを更新します// アラートダイアログのタイトルを設定します
				alertDialogBuilder.setMessage(dMeseege);		//">音楽ファイルに変更がある様です。全曲リストを作り直してもよろしいですか？\n１～２分かかります。今お時間が無ければ"しない"でアプリを一旦終了します。</string>// アラートダイアログのメッセージを設定します
				alertDialogBuilder.setPositiveButton(getResources().getString(R.string.comon_suru),			//"">する</string>//肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							final String TAG = "readDB";
							String dbMsg = "";
							try {
								preRead(reTrySart , null);				//全曲リストを作って再起動
								myLog(TAG, dbMsg);
							} catch (Exception e) {
								myErrorLog(TAG ,  dbMsg + "で" + e);
							}
						}
					});
				alertDialogBuilder.setNegativeButton(getResources().getString(R.string.comon_sinai),			//">しない// アラートダイアログの否定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String dbMsg = "readDB";
							myLog(TAG, dbMsg);
	//						quitMe();		//このアプリを終了する
						}
					});
				alertDialogBuilder.setCancelable(true);				// アラートダイアログのキャンセルが可能かどうかを設定します
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();				// アラートダイアログを表示します
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 *  最近追加のデータベース読出し
	 * ***/
	public void readDB_AddNew(){
		final String TAG = "readDB_AddNew";
		String dbMsg = "";
		try{
			String fn = getResources().getString(R.string.playlist_saikintuika_filename);
			dbMsg += "fn=" + fn;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
			if(newSongHelper == null){
				newSongHelper = new ZenkyokuHelper(MuList.this , saikintuikaDBname);		//周防会作成時はここでonCreatが走る
			}
			File dbF = getDatabasePath(fn);
			dbMsg += ",dbF=" + dbF;
			dbMsg += " , exists=" + dbF.exists() +" , canWrite=" + dbF.canWrite();
			if(dbF.exists() ){
				if(newSongDb != null){
					dbMsg += " , isOpen=" + newSongDb.isOpen();
				}
				if(! newSongDb.isOpen()){
					dbMsg += ">>delF=" + dbF.getPath();
					newSongDb = newSongHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
					dbMsg += ">isOpen>" + newSongDb.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
					MuList.this.saikintuikaTablename = saikintuikaDBname.replace(".db" , "");	//最近追加のリストテーブル名
					dbMsg +=  "、テーブル=" + MuList.this.saikintuikaTablename ;
				}
				if ( imanoJyoutai != veiwPlayer ){
					dbMsg += ",reqCode= " + reqCode;
					reqKariCode = reqCode;
				}
//				reqCode = MyConstants.v_artist;							//アーティスト
				dbMsg += ",plAL= " + MuList.this.plAL;
				if(MuList.this.plAL != null){
					int listSize =  MuList.this.plAL.size();
					dbMsg += ",listSize== " + listSize;
					if( 0 < listSize ){
						shyouMain(reqCode);		//onCleateの続きで仕掛けの仕込み
					} else {
						myLog(TAG,dbMsg);				//
						readSaikinTuikaDB();								//アーティストリストを読み込む(db未作成時は-) ☆フォーカスはダイアログに移る
						shigot_bangou = shyou_Main ;					//アーティストリスト作成後、作成するリストの振り分けに
					}
				} else {
//					myLog(TAG,dbMsg);				//
//					artistList_yomikomi();								//アーティストリストを読み込む(db未作成時は-) ☆フォーカスはダイアログに移る
//					shigot_bangou = shyou_Main ;					//アーティストリスト作成後、作成するリストの振り分けに
				}
//				zenkyokuAri = true;			//全曲リスト有り
			} else{
//				zenkyokuAri = false;			//全曲リスト有り
//				String dMeseege = getResources().getString(R.string.jyoukyouBunki_file_m);		//">音楽ファイルに変更がある様です。
//				dbMsg += "全曲リストが無い";//////////////////
//
//				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//				alertDialogBuilder.setTitle(getResources().getString(R.string.jyoukyouBunki_file_t));		//"">ファイルリストを更新します// アラートダイアログのタイトルを設定します
//				alertDialogBuilder.setMessage(dMeseege);		//">音楽ファイルに変更がある様です。全曲リストを作り直してもよろしいですか？\n１～２分かかります。今お時間が無ければ"しない"でアプリを一旦終了します。</string>// アラートダイアログのメッセージを設定します
//				alertDialogBuilder.setPositiveButton(getResources().getString(R.string.comon_suru),			//"">する</string>//肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//								final String TAG = "readDB";
//								String dbMsg = "";
//								try {
//									preRead(reTrySart , null);				//全曲リストを作って再起動
//									myLog(TAG, dbMsg);
//								} catch (Exception e) {
//									myErrorLog(TAG ,  dbMsg + "で" + e);
//								}
//							}
//						});
//				alertDialogBuilder.setNegativeButton(getResources().getString(R.string.comon_sinai),			//">しない// アラートダイアログの否定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//								String dbMsg = "readDB";
//								myLog(TAG, dbMsg);
//								//						quitMe();		//このアプリを終了する
//							}
//						});
//				alertDialogBuilder.setCancelable(true);				// アラートダイアログのキャンセルが可能かどうかを設定します
//				AlertDialog alertDialog = alertDialogBuilder.create();
//				alertDialog.show();				// アラートダイアログを表示します
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}													//最近追加のデータベース読出し


	/** 全曲リストの場合、リクエストコードをsigotoFuriwakeに送る */
	public void shyouMain(int reqC){		//①ⅳonCleateの続きで仕掛けの仕込み		ヘッド部のクリックN
		final String TAG = "shyouMain[MuList]";
		String dbMsg = "";
		try{
			dbMsg += ORGUT.nowTime(true,true,true) + dbMsg;/////////////////////////////////////
			dbMsg += ";nowList=" + nowList  ;/////////////////////////////////////
			dbMsg += ";reqC=" + reqC  ;/////////////////////////////////////
			if( nowList.equals(getResources().getString(R.string.listmei_zemkyoku))){
				sigotoFuriwake(reqC , sousa_artist , sousa_alubm  , sousa_titol , null);		//表示するリストの振り分け		, albumAL
			}else if( nowList.equals(getResources().getString(R.string.listmei_list_all))){
				sigotoFuriwake(reqC , sousa_artist , sousa_alubm  , sousa_titol , null);		//表示するリストの振り分け		, albumAL
			}else{
				String pdMessage = nowList + getResources().getString(R.string.data_lad);			//データ読み込み中
				CreatePLList( Long.valueOf(MuList.this.sousalistID) , pdMessage);		//プレイリストの内容取得		MuList.this.sousalist_data,
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void artistDBOpen(){										//artist_dbを開く
		final String TAG = "artistDBOpen";
		String dbMsg = "";
		try{
			if(artist_db != null){
				if(artist_db.isOpen()){
					artist_db.close();
				}
			}
			String fn = getString(R.string.artist_file);			//アーティストリスト	artist_db.getPath();
			dbMsg += "db=" + fn;
			artistHelper = new ArtistHelper(MuList.this , fn);		//アーティスト名のリストの定義ファイル		.
			dbMsg += " , artistHelper =" + artistHelper+ " , artist_db =" + artist_db;				//SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/artist.db；
			artistTName = getString(R.string.artist_table);			//artist_table
			dbMsg += "；アーティストリストテーブル=" + artistTName;
			artist_db = artistHelper.getReadableDatabase();			// データベースをオープン
			dbMsg += "；artist_db isOpen=" + artist_db.isOpen()+"；getPageSize=" + artist_db.getPageSize();
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

//	Cursor cursorA ;
	/**
	 *  全曲再生リスト作成を促すダイアログ表示
	 * 呼出し元　jyoukyouBunkiで全曲リストの作成形跡なし/artistList_yomikomiでレコード0件
	 * */
	public void preReadJunbi(String dlTitol , String dlMessege){										//全曲再生リスト作成を促すダイアログ表示
		final String TAG = "preReadJunbi[MuList]";
		String dbMsg = "";
		try{
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			alertDialogBuilder.setTitle(dlTitol);
			alertDialogBuilder.setMessage(dlMessege);
			alertDialogBuilder.setPositiveButton(getResources().getString(R.string.comon_suru),			//"">する</string>//肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String TAG = "preReadJunbi[MuList]";
						String dbMsg = "";
						try {
							preRead( MyConstants.syoki_Yomikomi , null);			//syoki_start_up
							myLog(TAG, dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG ,  dbMsg + "で" + e);
						}
					}
				});
			alertDialogBuilder.setNegativeButton(getResources().getString(R.string.comon_sinai),			//">しない// アラートダイアログの否定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final String TAG = "preReadJunbi[MuList]";
						String dbMsg = "[MuList]plNameSL" + plNameSL;
						if( plNameSL == null ){
							 dbMsg += "=" + plNameSL.size() +"件中";
							 dbMsg += "sousalistNamen=" + sousalistName +"は";
							int ePosition = plNameSL.indexOf(sousalistName);
							dbMsg += ",ePosition=" + ePosition;
							pl_sp.setSelection(ePosition , false);								//☆falseで勝手に動作させない
						}
						myLog(TAG, dbMsg);
						if(zenkyokuAri){
							nowList = getResources().getString(R.string.listmei_zemkyoku);				//全曲リスト
							artistList_yomikomi();
						}else{
							quitMe();		//このアプリを終了する
						}
					}
				});
			alertDialogBuilder.setCancelable(true);				// アラートダイアログのキャンセルが可能かどうかを設定します
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();				// アラートダイアログを表示します
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * プレイリスト一覧のクリック
	 * 再生対象リストの確定
	 * */
	public void listOfListClick(AdapterView<?> parent, View view, int position, long id) {
		final String TAG = "listOfListClick";
		String dbMsg = "";
		try{
			dbMsg += ",position=" + position;
			long readID = Long.valueOf((String) plNameAL.get(position).get(MediaStore.Audio.Playlists._ID));
			sousalistID = Math.toIntExact(readID);
			nowList_id = sousalistID;
			sousalistName = (String) plNameAL.get(position).get(MediaStore.Audio.Playlists.TITLE);			//"title"
			nowList = sousalistName;
			dbMsg +=",["+sousalistID + "]" + nowList;
			if( nowList.equals(getResources().getString(R.string.listmei_zemkyoku)) ){
				nowList_id =-1;
				dbMsg += ">>" + nowList_id;
			}
			dbMsg +=">>["+sousalistID + "]" + nowList;
			boolean retBool = setPrefInt("nowList_id", nowList_id, MuList.this);        //プリファレンスの読込み
			dbMsg += "を書込み" + retBool;
			dbMsg += "]" + sousalistName;/////////////////////////////////////
			retBool = setPrefStr("nowList" , sousalistName , MuList.this);        //プリファレンスの読込み
			dbMsg += "を書込み" + retBool;

			if(sousalistName.equals(getString(R.string.listmei_zemkyoku))){
				artistList_yomikomi();
				reqCode = MyConstants.v_artist;
			}else {
				CreatePLList(readID , sousalistName);		//nowList
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * 端末内のプレイリスト一覧表示
	 * */
	public int listOfList_yomikomi(String selectListName){																				//①ⅵ；アーティストリストを読み込む(db未作成時は-)
		int retInt = -1;
		final String TAG = "listOfList_yomikomi";
		String dbMsg = "";
		try{

			dbMsg += "、plNameAL=" + plNameAL.size() + "件";
			dbMsg += "、選択リスト名=" + selectListName;
			dbMsg += "、pref_list_simple=" + pref_list_simple;
			sousalistName = getString(R.string.listmei_list_all);
			reqCode = MyConstants.v_play_list;
			if( pref_list_simple ) {                    //シンプルなリスト表示（サムネールなど省略）
				dbMsg += "、シンプルリスト作成";
				//	makePlainList( plNameSL);			//階層化しないシンプルなリスト
				ArrayAdapter<String> arrayAdapter =new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, plNameSL);
				lvID.setAdapter(arrayAdapter);
				lvID.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						final String TAG = "onItemClick";
						String dbMsg = "[listOfList_yomikomi]";
//						if(sousalistName.equals(getString(R.string.listmei_zemkyoku))){
//							artistList_yomikomi();
//						}else {
							listOfListClick(parent, view, position, id);
//						}
					}
				});
			}else{
				setHeadImgList(plNameAL);
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return retInt;
	}

			/**
             * artist_dbの読み込み(	Ver1.1.1からZenkyokuListで作成したアイテムを無条件で読み込む)
             * 読出し元
             * sigotoFuriwakeで、	reqCode = MyConstants.v_artist;	　/
             * listReWriteで、		artistSL== null
             * readDBで				artistALが読み込まれていない
             * */
	@SuppressLint("Range")
	public int artistList_yomikomi(){																				//①ⅵ；アーティストリストを読み込む(db未作成時は-)
		int retInt = -1;
		final String TAG = "artistList_yomikomi[MuList]";
		String dbMsg = "";
		try{
			reqCode = MyConstants.v_artist;							//アーティスト
			artistDBOpen();									//artist_dbを開く
//			if(cursorA != null){
//				if(! cursorA.isClosed()){
//					cursorA.close();
//				}
//			}
			String table =artistTName;			//テーブル名を指定します。
			String[] columns =null;
			String c_selection = null;					// compSelection;
			String[] c_selectionArgs= null;				//compList;
			String groupBy = null;
			String having =null;					//having句を指定します。
			String orderBy  = "ARTIST_ID";
			String limit = null;					//検索結果の上限レコードを数を指定します。
			Cursor cCursor = artist_db.query( table ,columns, c_selection,  c_selectionArgs,  groupBy,  having,  orderBy,  limit) ;
			retInt = cCursor.getCount();
			dbMsg = "；アーティスト=" + retInt + "人";
			if(cCursor.moveToFirst()){
				zenkyokuAri = true;
				dbMsg += "；" +  cCursor.getString(cCursor.getColumnIndex("ARTIST"));
				mainTStr = getResources().getString(R.string.listmei_zemkyoku);			//全曲リスト
				subTStr =  getResources().getString(R.string.pp_artist)+retInt +  getResources().getString(R.string.comon_nin);			//アーティスト
				mainHTF.setText(mainTStr);					//ヘッダーのメインテキスト表示枠
				subHTF.setText(subTStr);					//ヘッダーのサブテキスト表示枠
		//		dbMsg += ",artistSL=" +  artistSL;
				if( artistSL == null){
					artistSL =  new ArrayList<String>();				//アーティストリスト用簡易リスト
				}else {
					artistSL.clear();
				}
				dbMsg += ">>" +  artistSL;
//				if( aArtistList == null ){
//					aArtistList =  new ArrayList<String>();					//アルバムアーティスト
//				} else {
//					aArtistList.clear();
//				}
//				dbMsg += ",aArtistList=" +  aArtistList;
				if( artistAL == null ){
					artistAL = new ArrayList<Map<String, Object>>();
				} else {
					artistAL.clear();
				}
				dbMsg += ",artistAL=" +  artistAL;
				artintCo = 0;
				albamCo = 0;
				titolCo = 0;
				compCount = 0;
				aArtist = "";
				albumMei = "";
				artURL = null;
				reqCode = MyConstants.v_artist ;
				String pdTitol = getResources().getString(R.string.jyunbicyuu);					//準備中
				String pdMessage = getResources().getString(R.string.data_lad);				//データ読み込み中</string>
		//		myLog(TAG,dbMsg);
			//	plTask.execute(reqCode,cCursor,pdTitol,pdMessage,cCursor.getCount());
				do{
					cCursor=artistList_yomikomiLoop(cCursor);
				}while( cCursor.moveToNext() ) ;
				dbMsg += ",artistAL=" +  artistAL.size() + "件";
				artistList_yomikomiEnd();
			} else {
				zenkyokuAri = false;
				String dlTitol = getResources().getString(R.string.syokairiyou_dt);			//全曲リストを作成作成させ下さい。
				String dlMessege = getResources().getString(R.string.syokairiyou_dm);					// 			//１～２分かかりますが、ゲスト参加などで分離されたアルバムを統合して自然な連続再生を可能にします。\n
				preReadJunbi(dlTitol , dlMessege);										//全曲再生リスト作成を促すダイアログ表示
			}
			dbMsg += ">レコード>" + artistAL.size() + "件";
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return retInt;
	}

	/**
	 *  cursorで渡されたartist_dbのレコードから"ARTIST"をartistSLに "ALBUM_ARTIST"をaArtistListに格納 詳細表示の場合はartistALを使用 */
	@SuppressLint("Range")
	public Cursor artistList_yomikomiLoop(Cursor cursor ) throws IOException{		//①ⅵ2；			, String comp
		final String TAG = "artistList_yomikomiLoop";
		String dbMsg = "";
		try{
			String artURL = null;
			dbMsg += cursor.getPosition() + "件目;";					// "/" + cursor.getCount() +
			@SuppressLint("Range") String aArtist = cursor.getString(cursor.getColumnIndex("ALBUM_ARTIST"));
			dbMsg += "ALBUM_ARTIST=" + aArtist;
			@SuppressLint("Range") String cArtist = cursor.getString(cursor.getColumnIndex("ARTIST"));		//置換え前に戻す
			dbMsg += ",ARTIST=" + cArtist;
			String wrArtist =cArtist;
			String ｃArtistUp = cArtist.toUpperCase();
			ｃArtistUp =ORGUT.ArtistPreFix(ｃArtistUp);	//TheとFeat以下をカット
			dbMsg += ">ArtistPreFix>" + ｃArtistUp ;
			if(ｃArtistUp.length() !=aArtist.length()){
	//			myLog(TAG,dbMsg);
			}
					MuList.this.artistSL.add(aArtist);					//クレジットされたアーティストト	wrArtist
					MuList.this.artistMap = new HashMap<String, Object>();		//アーティストリスト用
					MuList.this.artistMap.put("index" ,aArtist );
					MuList.this.artistMap.put("main" ,aArtist );					///wrArtist
					if( pref_list_simple ){					//シンプルなリスト表示（サムネールなど省略）
					} else {
						dbMsg +=MuList.this.artistSL.size() + ")" + aArtist ;
						int tClo = cursor.getColumnIndex("ALBUM_ART");
						dbMsg += ";" + tClo +"項目";
						if( tClo > 0 ){
							artURL = cursor.getString(cursor.getColumnIndex("ALBUM_ART"));			//
						}
						dbMsg += ",artURL=;" + artURL ;
						if( artURL == null ){
							Uri cUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
							String[] c_columns = null;		 		//③引数columnsには、検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
							String c_selection =  MediaStore.Audio.Albums.ARTIST +" LIKE ? ";
							String[] c_selectionArgs= { "%" + cArtist+ "%" , };   			//⑥引数groupByには、groupBy句を指定します。
							String c_orderBy= null;											//MediaStore.Audio.Albums.LAST_YEAR  ; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
							Cursor cursor2 =MuList.this .getContentResolver().query( cUri , c_columns , c_selection , c_selectionArgs, c_orderBy);			//getApplicationContext()
							dbMsg += "[" +cursor2.getCount() + "件]";///////////////////////////////////////////////////////////////////////////////////////////
							if( cursor2.moveToFirst() ){
								do{
									artURL = cursor2.getString(cursor2.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
									if( artURL != null ){
										File AAF = new File(artURL);
										dbMsg += ",exists=" + AAF.exists();
										if( AAF.exists() ){										//そのファイルが実際にあれば
											break;												//検索終了
										}
									}
								}while(cursor2.moveToNext());
							}
							cursor2.close();
							dbMsg += ">>;" + artURL ;
						}
						MuList.this.artistMap.put("img" ,artURL );
						tClo = cursor.getColumnIndex("SUB_TEXT");
						dbMsg += ";,SUB_TEXT=" + tClo +"項目";
						String rStr = null;
						if( tClo > 0 ){
							rStr = cursor.getString(tClo);
						}else{
							rStr = "";
				//			MuList.this.artistMap.put("sub" ,"" );
						}
						dbMsg +=">>" + rStr;
						MuList.this.artistMap.put("sub" ,rStr );
					}
					MuList.this.artistAL.add(artistMap);		//アーティストリスト用ArrayList
					dbMsg +="["+ cursor.getPosition()   +">>" +MuList.this.artistAL.size() +"]";			//+ "/" + cursor.getCount()
	//		dbMsg +=">>" + MuList.this.aArtistList.size() + "件";
//			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return cursor;
	}

	/**
 	* artistSLで階層化しないシンプルなリスト  	 artistALで詳細リストを表示する  * */
	@SuppressLint("Range")
	public int artistList_yomikomiEnd(){							//起動直後はCreateAlbumListへ//①ⅵ4；アーティストリストの終了処理	＜onSuccessplogTaskからの戻り
		int retInt = -1;
		final String TAG = "artistList_yomikomiEnd";
		String dbMsg = "";
		try{
	//		cursorA.close();
			dbMsg += "；artist_db isOpen=" + artist_db.isOpen();
			artist_db.close();
			dbMsg += ">>" + artist_db.isOpen();
			if(Zenkyoku_db != null){
				if(Zenkyoku_db.isOpen()){
					Zenkyoku_db.close();
				}
			}
			nowList_id = pref_zenkyoku_list_id;
			sousalistID =  nowList_id;
			nowList = getResources().getString(R.string.listmei_zemkyoku);
			sousalistName = nowList;
			dbMsg +=  "nowList[" + nowList_id + "]" + nowList +"→操作中のリスト[" + sousalistID + "]" + sousalistName ;	////////////////
			dbMsg += "シンプル表示か" + pref_list_simple;/////////////////////////////////////
			pref_zenkyoku_list_id = getAllSongItems();
			dbMsg += " [全曲リスト； " + pref_zenkyoku_list_id + "]";/////////////////////////////////////
			if( pref_list_simple ){					//シンプルなリスト表示（サムネールなど省略）
				dbMsg += " artistSL= " + artistSL.size() + "件" +artistSL.get(0) + "～"  +artistSL.get(artistSL.size()-1) ;/////////////////////////////////////
				artintCo = artistSL.size();
				dbMsg += " , imanoJyoutai= " + imanoJyoutai;/////////////////////////////////////
				dbMsg +=",1)"+artistSL.get(0);
				dbMsg +="～" + artintCo +")";
				dbMsg +=artistSL.get(artintCo-1);
				dbMsg += ",artist=" + artistSL.size() +"件";
				makePlainList( artistSL);			//階層化しないシンプルなリスト
			}else{
				dbMsg += " artistAL= " + artistAL.size() + "件";/////////////////////////////////////
				artintCo = artistAL.size();
				dbMsg +=artistAL.get(0);
				dbMsg +="～" + artintCo +")";
				dbMsg +=artistAL.get(artintCo-1);
				setHeadImgList(artistAL );				//イメージとサブテキストを持ったリストを構成
			}
			String subText =getResources().getString(R.string.pp_artist) + " ; "  + artintCo + getResources().getString(R.string.comon_nin) ;			//アーティスト 人
			subHTF.setText(subText );
	//		reqCode = MyConstants.v_alubum;							//アーティスト
				if( 0< mIndex ){
					dbMsg += " ,mIndex= " + mIndex;/////////////////////////////////////
					Cursor playingItem = musicPlaylist.getPlaylistItems(nowList_id,mIndex);
					if(playingItem.moveToFirst()){
						int albumId = Integer.parseInt(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM_ID)));
						dbMsg +=" [" + albumId + "]";
						albumArtist = musicPlaylist.getAlbumArtist(albumId, MuList.this);
//							playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST));		//playingItem.album_artist;	//アルバムアーティスト名
						dbMsg +=" ,アルバムアーティスト= " + albumArtist;
						creditArtistName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST));		//playingItem.artist;	//クレジットされているアーティスト名
						dbMsg +=" ,クレジット⁼ " + creditArtistName;
						albumName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM));		//playingItem.album;			//アルバム名
						dbMsg +=" , アルバム⁼" + albumName;/////////////////////////////////////	this.album = album;
						titolName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE));		//playingItem.title;		//曲名
						dbMsg +=" ,タイトル= " + titolName;/////////////////////////////////////		this.title = title;
						sousalistName =getResources().getString(R.string.listmei_zemkyoku);
					}
					playingItem.close();
				}
			dbMsg += " , retInt=⁼" + retInt;
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return retInt;
	}

	public int artistList_yomikomiAdd(String comp){															//①ⅵ5；アーティストリストの末尾に追加するコンピレーションなど
		int retInt = -1;
		final String TAG = "artistList_yomikomiAdd[MuList]";
		String dbMsg = "";
		try{
			String table =artistTName = getString(R.string.artist_table);//テーブル名を指定します。
			String[] columns =null;			//{  "ALBUM_ARTIST" , "ARTIST"};				//検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
			String selections = "ALBUM_ARTIST = ? ";			//+ comp ;		//MediaStore.Audio.Media.ARTIST +" <> " + comp;			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
			String[] selectionArgs = new String[]{ comp };
			String groupBy ="ALBUM_ARTIST";					//groupBy句を指定します。
			String having =null;					//having句を指定します。
			String orderBy = null;					//"ALBUM_ARTIST,ALBUM";				//
			String limit = null;					//検索結果の上限レコードを数を指定します。
			Cursor cursor = artist_db.query( table ,columns, selections,  selectionArgs,  groupBy,  having,  orderBy,  limit) ;
			retInt = cursor.getCount();
			dbMsg += "；"+comp + "；" + retInt + "人";
		//	myLog(TAG,dbMsg);
			if( cursor.moveToFirst() ){
				artistList_yomikomiLoop( cursor);		// , comp
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return retInt;
	}

	@SuppressLint("Range")
	public List<Map<String, Object>> CreateAlbumList(String albumArtist , String albumMei) throws IOException {		//起動直後はCreateTitleListへ	//アルバムリスト作成
		final String TAG = "CreateAlbumList";
		String dbMsg = "";
		try{
			dbMsg += ORGUT.nowTime(true,true,true) + dbMsg;/////////////////////////////////////
			long start = System.currentTimeMillis();		// 開始時刻の取得
			String comp = getResources().getString(R.string.comon_compilation);			//コンピレーション
			dbMsg += "artistMei="+albumArtist+",albumMei="+albumMei+",albumAL="+albumAL;
			if(albumAL == null){
				albumAL = new ArrayList<Map<String, Object>>();		//アルバムリスト用ArrayList
			}else {
				albumAL.clear();
			}
			dbMsg += ",albumList="+albumList;///////////////////////
			if(albumList == null){
				albumList = new ArrayList<String>();		//アルバム名
			}else {
				albumList.clear();
			}
			dbMsg += ">>"+albumList;///////////////////////
			int retInt = 0 ;
			if(Zenkyoku_db != null){
				dbMsg += ",isOpen="+ Zenkyoku_db.isOpen();///////////////////////
				if(Zenkyoku_db.isOpen()){
					Zenkyoku_db.close();
				}
			}
			String fn = getString(R.string.zenkyoku_file);			//全曲リスト名
			dbMsg += ",fn=" + fn;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
			zenkyokuHelper = new ZenkyokuHelper(MuList.this , fn);		//全曲リストの定義ファイル		.
			Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
			dbMsg += ",isOpen=" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
			String zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
			boolean distinct = true;					//trueを指定すると検索結果から重複する行を削除します。
			String c_selection = "ALBUM_ARTIST = ?";			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
			String[] c_selectionArgs0={albumArtist};			//⑥引数groupByには、groupBy句を指定します。 "'" + artistMei + "'"
			String c_groupBy= "ALBUM";
			String having =null;					//having句を指定します。
			String c_orderBy="LAST_YEAR ,YEAR"; 	 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
			if( Arrays.asList( compList ).contains(albumArtist)){				//コンピュレーションなどの末尾へのなら
				c_orderBy = "ALBUM";
			}
			Cursor cursor = Zenkyoku_db.query(distinct,zenkyokuTName, null, c_selection, c_selectionArgs0 , c_groupBy, having, c_orderBy, null);	//1;table, 2; columns,new String[] {MotoN, albamN}
			dbMsg +=" 、"+ cursor.getCount() +"件";/////////////////////////////////////////////////////////////////////////////////////////////
			if(! cursor.moveToFirst() ){				//抽出されるものが無ければ
				cursor.close();
				String kensakuStr = albumAetist2Aetist( albumArtist) ;
				String[] c_selectionArgs1={kensakuStr};			//⑥引数groupByには、groupBy句を指定します。 "'" + artistMei + "'"
				cursor = Zenkyoku_db.query(distinct,zenkyokuTName, null, c_selection, c_selectionArgs1 , null, null, c_orderBy, null);	//1;table, 2; columns,new String[] {MotoN, albamN}
				if(! cursor.moveToFirst() ){
					cursor.close();
					c_selection = "ARTIST LIKE ?";			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
					cursor = Zenkyoku_db.query(distinct,zenkyokuTName, null, c_selection, c_selectionArgs0 , null, null, c_orderBy, null);	//1;table, 2; columns,new String[] {MotoN, albamN}
					cursor.moveToFirst();
				}
				@SuppressLint("Range") String sortName = cursor.getString(cursor.getColumnIndex("SORT_NAME"));			//MediaStore.Audio.Albums.ARTIST
				dbMsg +=" ( "+ sortName +")";/////////////////////////////////////////////////////////////////////////////////////////////
				cursor.close();
				distinct = true;					//trueを指定すると検索結果から重複する行を削除します。
				c_selection = "SORT_NAME = ?";			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
				String[] c_selectionArgs={sortName};			//⑥引数groupByには、groupBy句を指定します。 "'" + artistMei + "'"
				cursor = Zenkyoku_db.query(distinct,zenkyokuTName, null, c_selection, c_selectionArgs , c_groupBy, having, c_orderBy, null);	//1;table, 2; columns,new String[] {MotoN, albamN}
				if(! cursor.moveToFirst() ){
					c_selection = "SORT_NAME LIKE ?";			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
					cursor = Zenkyoku_db.query(distinct,zenkyokuTName, null, c_selection, c_selectionArgs , c_groupBy, having, c_orderBy, null);	//1;table, 2; columns,new String[] {MotoN, albamN}
					cursor.moveToFirst();
				}
			}				//			if(! cursor.moveToFirst() ){				//抽出されるものが無ければ
			retInt = cursor.getCount();
			dbMsg += ";" + retInt +"件";		//03-28java.lang.IllegalArgumentException:  contains a path separator
			if(cursor.moveToFirst()){
				do{
					dbMsg += cursor.getPosition() +"/"+ cursor.getCount() + "枚目は";/////////////////////////////////////////////////////////////////////////////////////////////
					try{
						@SuppressLint("Range") String artName = cursor.getString(cursor.getColumnIndex("ARTIST"));
						dbMsg += artName + " の ";/////////////////////////////////////////////////////////////////////////////////////////////
						@SuppressLint("Range") String aName = cursor.getString(cursor.getColumnIndex("ALBUM"));			//3；	MediaStore.Audio.Albums.ALBUM)//
						dbMsg +=" ; "+ aName;/////////////////////////////////////////////////////////////////////////////////////////////
						albumList.add(aName);
						if( pref_list_simple ){					//シンプルなリスト表示（サムネールなど省略）
						} else {
							MuList.this.albumMap = new HashMap<String, Object>();		//アルバムトリスト用
							MuList.this.albumMap.put("main" ,aName );
							artURL =ORGUT.retAlbumArtUri(getApplicationContext() , artName , aName );			//アルバムアートUriだけを返す		MuList.this ,
							dbMsg += ">>" + artURL;
							MuList.this.albumMap.put("img" ,artURL );
							String fYear = null;
							fYear = cursor.getString(cursor.getColumnIndex("LAST_YEAR"));			//MediaStore.Audio.Albums.FIRST_YEAR
							dbMsg += "。最終=" + fYear + "年";
							if( fYear == null || fYear.equals("")){
								fYear = cursor.getString(cursor.getColumnIndex("YEAR"));			//MediaStore.Audio.Albums.FIRST_YEAR
								dbMsg += ",YEAR=" + fYear + "年";
							}
							c_selection = "ALBUM_ARTIST = ? AND ALBUM = ?";			//SORT_NAME	2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
							String[] c_selectionArgs2={albumArtist , aName};			//⑥引数groupByには、groupBy句を指定します。 "'" + artistMei + "'"
							c_groupBy = null;
							Cursor cursor2 = Zenkyoku_db.query(zenkyokuTName, null, c_selection, c_selectionArgs2 , null, null, null);	//1;table, 2; columns,new String[] {MotoN, albamN}
							String kyokusuu = String.valueOf(cursor2.getCount());			//MediaStore.Audio.Albums.NUMBER_OF_SONGS
							dbMsg +=" ; "+ kyokusuu + "曲";/////////////////////////////////////////////////////////////////////////////////////////////
							cursor2.close();
							if(kyokusuu != null){
								fYear = fYear +" ; " + kyokusuu + getResources().getString(R.string.pp_kyoku);
							}
							dbMsg += " , " + fYear;
							MuList.this.albumMap.put("sub" ,fYear );		//アルバム付加情報
							MuList.this.albumAL.add( albumMap);			//アルバムリスト用ArrayList
						}
						dbMsg += " , " + MuList.this.albumAL.size() + "件";
//						myLog(TAG, dbMsg);
					}catch(IllegalArgumentException e){
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}while(cursor.moveToNext());
				dbMsg += " , imanoJyoutai= " + imanoJyoutai;/////////////////////////////////////
			}													//if(cursor.moveToFirst()){
			cursor.close();
			Zenkyoku_db.close();
			long end=System.currentTimeMillis();		// 終了時刻の取得
			String toastStr = albumList.size() +getResources().getString(R.string.pp_mai)+"["+getResources().getString(R.string.comon_syoyoujikan)+";"+ (int)((end - start)) + "mS]";		//	<string name="">所要時間</string>
			dbMsg += "::" + toastStr ;
			myLog(TAG, dbMsg);
		}catch(IllegalArgumentException e){
			myErrorLog(TAG ,  dbMsg + "で" + e);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return MuList.this.albumAL;
	}

	@SuppressLint("Range")
	public List<Map<String, Object>> CreateTitleList(String artistMei , String albumMei , String titolMei) throws IOException {		//起動時はここからプレイヤー起動/曲リスト作成
		final String TAG = "CreateTitleList";
		String dbMsg = "[MuList.List<Map<String, Object>>]";
		dbMsg +=ORGUT.nowTime(true,true,true);/////////////////////////////////////
		try{
			long start = System.currentTimeMillis();		// 開始時刻の取得
			dbMsg += ",artistMei="+artistMei+",albumMei="+albumMei;///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			int releaceYear = 0;			//制作年
			titolAL = new ArrayList<Map<String, Object>>();		//タイトルムリスト用ArrayList
			titolList = null;
			titolList = new ArrayList<String>();		//曲名
			if(Zenkyoku_db != null){
				if( Zenkyoku_db.isOpen()){
					Zenkyoku_db.close();
					dbMsg += ">isOpen>" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
				}
			}
			String fn = getString(R.string.zenkyoku_file);			//全曲リスト名
			dbMsg += ",fn=" + fn;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
			zenkyokuHelper = new ZenkyokuHelper(MuList.this , fn);		//全曲リストの定義ファイル		.
			Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
			dbMsg += ">isOpen>" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
			dbMsg += ",getPageSize=" + Zenkyoku_db.getPageSize() + "件、" ;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
			String zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
			String[] c_columns =null;					//②引数tableには、テーブル名を指定します。
			String c_selection = "ALBUM_ARTIST LIKE ? AND ALBUM = ?";		//	= "SORT_NAME = ? AND ALBUM = ?";
			String[] c_selectionArgs= {"%" + artistMei + "%" , albumMei };		//	artistMei , albumMei };  ////
			String c_orderBy= "TRACK"; 			//⑧引数orderByには、orderBy句を指定します。	降順はD
			Cursor cursor = Zenkyoku_db.query(zenkyokuTName, c_columns, c_selection, c_selectionArgs , null, null, c_orderBy);	// table, columns,new String[] {MotoN, albamN}
			dbMsg += ",getCount=" + cursor.getCount() + "件、" ;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
			String comp = getResources().getString(R.string.comon_compilation);			//
			if(artistMei.equals(comp)){
				dbMsg += "、コンピレーションでアルバム名のみ";
				c_selection = "ALBUM = ?";			//MediaStore.Audio.Media.ARTIST +" LIKE ? AND " + MediaStore.Audio.Media.ALBUM +" = ?";
				String[] c_selectionArgs2= {albumMei };   			//⑥引数groupByには、groupBy句を指定します。
				cursor = Zenkyoku_db.query(zenkyokuTName, c_columns, c_selection, c_selectionArgs2 , null, null, c_orderBy);	// table, columns,new String[] {MotoN, albamN}
			}else{
				cursor = Zenkyoku_db.query(zenkyokuTName, c_columns, c_selection, c_selectionArgs , null, null, c_orderBy);	// table, columns,new String[] {MotoN, albamN}
			}
			trackInAlbum = cursor.getCount();
			dbMsg +="、収録曲" +  trackInAlbum +"曲";///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if( cursor.moveToFirst()){
				creditArtistName = cursor.getString(cursor.getColumnIndex( "SORT_NAME" ));				//SORT_NAME
			}
			dbMsg += "、creditArtistName="+creditArtistName;//////
// 20190519;二回処理している利用不明///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//			cursor.close();
//			if(artistMei.equals(comp)){
//				dbMsg += "、コンピレーションでアルバムのみ=";
//				c_selection = "ALBUM = ?";			//MediaStore.Audio.Media.ARTIST +" LIKE ? AND " + MediaStore.Audio.Media.ALBUM +" = ?";
//				String[] c_selectionArgs2= {albumMei };   			//⑥引数groupByには、groupBy句を指定します。
//				cursor = Zenkyoku_db.query(zenkyokuTName, c_columns, c_selection, c_selectionArgs2 , null, null, c_orderBy);	// table, columns,new String[] {MotoN, albamN}
//			}else{
//				c_selection = "SORT_NAME = ? AND ALBUM = ?";			//= "ALBUM_ARTIST LIKE ? AND ALBUM = ?";
//				String[] c_selectionArgs3= { creditArtistName , albumMei };   			// {"%" + artistMei + "%" , albumMei };
//				cursor = Zenkyoku_db.query(zenkyokuTName, c_columns, c_selection, c_selectionArgs3 , null, null, c_orderBy);	// table, columns,new String[] {MotoN, albamN}
//			}
//			trackInAlbum = cursor.getCount();
//			dbMsg += ";" + trackInAlbum + "曲;";
// 明///////////////////////////////////////////////////////////////////////////////////20190519;二回処理している利用不//////
			if(cursor.moveToFirst()){
				do{
					String dMsg = cursor.getPosition() +"/"+ cursor.getCount() + "曲;";/////////////////////////////////////////////////////////////////////////////////////////////
					try{
						MuList.this.titolMap = new HashMap<String, Object>();			//タイトルリスト用
						String aName = null;
						String track = cursor.getString(cursor.getColumnIndex( "TRACK" ));
						dbMsg +=" [ "+ track + " ] ";
						Util UTIL = new Util();
						track = UTIL.checKTrack( track);

						aName = cursor.getString(cursor.getColumnIndex( "TITLE" ));			//MediaStore.Audio.Media.TITLE
						dbMsg +=aName;/////////////////////////////////////////////////////////////////////////////////////////////
						titolList.add(aName);
						String tTime = cursor.getString(cursor.getColumnIndex( "DURATION" ));		//MediaStore.Audio.Media.DURATION
						if(tTime != null){
							tTime = String.valueOf(ORGUT.sdf_mss.format(Long.valueOf(tTime)) );
						}
						String cArtist = cursor.getString(cursor.getColumnIndex( "ARTIST" ));			//クレジットアーティスト名
						if( ! cArtist.equals(artistMei) ){
							tTime = cArtist +" ; "+ tTime;
						}
						dMsg = dMsg + ";" + tTime;/////////////////////////////////////////////////////////////////////////////////////////////
						int rYear = cursor.getInt(cursor.getColumnIndex( "YEAR" ));		//制作年	MediaStore.Audio.Media.YEAR
						dMsg = dMsg +" , "+ rYear;/////////////////////////////////////////////////////////////////////////////////////////////
						MuList.this.titolMap.put("main" ,aName );
						if(releaceYear < rYear){
							releaceYear = rYear;
						}
						if(releaceYear > 0){
							tTime = tTime +";"+ releaceYear;
						}
						MuList.this.titolMap.put("sub" ,tTime );		//アルバム付加情報
						MuList.this.titolMap.put("DATA" ,cursor.getString(cursor.getColumnIndex("DATA")));		//アルバム付加情報
						MuList.this.titolAL.add( titolMap);			//アルバムリスト用ArrayList
					}catch(IllegalArgumentException e){
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}while(cursor.moveToNext());
			  //ファイル名から無理矢理検索/////////////////////////////////////////////////////////////////////////////
				dbMsg += " , imanoJyoutai= " + imanoJyoutai;/////////////////////////////////////
			}				//if(cursor.moveToFirst()){
			cursor.close();
			Zenkyoku_db.close();
			long end=System.currentTimeMillis();		// 終了時刻の取得
			dbMsg += (int)((end - start)) + "m秒で表示終了";
			String toastStr = titolAL.size() +getResources().getString(R.string.pp_kyoku)+"["+getResources().getString(R.string.comon_syoyoujikan)+";"+ (int)((end - start)) + "mS]";		//	<string name="">所要時間</string>
			dbMsg += toastStr ;
			myLog(TAG, dbMsg);
		}catch(IllegalArgumentException e){
			myErrorLog(TAG ,  dbMsg + "で" + e);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return titolAL;
	}

	@SuppressLint("Range")
	public String albumAetist2Aetist(String aName) {						//アーティスト名からアルバムアーティスト名を返す
		final String TAG = "albumAetist2Aetist";
		String dbMsg = "";
		String artist = null;
		try{
			dbMsg +=   ",aName=" + aName;/////////////////////////////////////
			String table =artistTName= getString(R.string.artist_table);				//テーブル名を指定します。
			artistDBOpen();									//artist_dbを開く
			String[] columns =null;
			String selections = "ALBUM_ARTIST = ? ";			//+ comp ;		//MediaStore.Audio.Media.ARTIST +" <> " + comp;			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
			String[] selectionArgs = new String[]{ aName };  			//	 {"%" + artistMei + "%" , albumMei };
			String groupBy ="ALBUM_ARTIST";					//groupBy句を指定します。
			String having =null;					//having句を指定します。
			String orderBy  =null;
			String limit = null;					//検索結果の上限レコードを数を指定します。
			Cursor cursor = artist_db.query( table ,columns, selections,  selectionArgs,  groupBy,  having,  orderBy,  limit) ;
			if(cursor.moveToFirst()){
				artist =cursor.getString(cursor.getColumnIndex("ARTIST"));		//リストアップしたアルバムアーティスト名
			}else{
				artist = aName;
	//			albumArtist =  ORGUT.ArtistPreFix(aName);					//20140211アーティスト名のTheを取る
			}
			dbMsg += ",albumArtist=" + artist;/////////////////////////////////////
			cursor.close();
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return artist;
	}

	@SuppressLint("Range")
	public String Artist2albumAetist(String aName) {						//アーティスト名からアルバムアーティスト名を返す
		final String TAG = "Artist2albumAetist";
		String dbMsg = "";
		String artistName = null;
		try{
			dbMsg +=   ",aName=" + aName;/////////////////////////////////////
			dbMsg += ",table=" + artistTName;/////////////////////////////////////
			String table =artistTName= getString(R.string.artist_table);				//テーブル名を指定します。
			dbMsg += ">>" + artistTName;/////////////////////////////////////
			artistDBOpen();									//artist_dbを開く
			String[] columns =null;
			String selections = "ARTIST = ? ";			//+ comp ;		//MediaStore.Audio.Media.ARTIST +" <> " + comp;			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
			String[] selectionArgs = new String[]{ aName };  			//	 {"%" + artistMei + "%" , albumMei };
			if(aName == null){
				selections = null;			//+ comp ;		//MediaStore.Audio.Media.ARTIST +" <> " + comp;			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
				selectionArgs = null;  			//	 {"%" + artistMei + "%" , albumMei };
			}
			String groupBy ="ALBUM_ARTIST";					//groupBy句を指定します。
			String having =null;					//having句を指定します。
			String orderBy  =null;
			String limit = null;					//検索結果の上限レコードを数を指定します。
			Cursor cursor = artist_db.query( table ,columns, selections,  selectionArgs,  groupBy,  having,  orderBy,  limit) ;
			dbMsg += "," + cursor.getCount() + "件";/////////////////////////////////////
			if(cursor.moveToFirst()){
				artistName =cursor.getString(cursor.getColumnIndex("ALBUM_ARTIST"));		//アーティスト名
			}else{
				artistName = aName;
	//			albumArtist =  ORGUT.ArtistPreFix(aName);					//20140211アーティスト名のTheを取る
			}
			dbMsg += ",artistName=" + artistName;/////////////////////////////////////
			cursor.close();
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return artistName;
	}

	/** 送信前のプリファレンス処理など onCreateで受け取った再生リスト(nowList)と異なる操作結果リストが渡されたら更新 */
	@SuppressLint("Range")
	public void sousinnMaeSyori(String pref_data_url ) {										//送信前のプリファレンス処理など
		final String TAG = "sousinnMaeSyori";
		String dbMsg = "";
		try{
			dbMsg +=  "nowList[" + nowList_id + "]" + nowList +"→操作中のリスト[" + sousalistID + "]" + sousalistName ;	////////////////
			boolean listHenkou = false;
			if( ! nowList.equals(sousalistName)){
				nowList = sousalistName;
				myEditor.putString( "nowList", String.valueOf(nowList));
				listHenkou = true;
			}
			if( nowList_id != sousalistID){
				nowList_id = sousalistID;		//操作対象リストID
				nowList_data = sousalist_data;		//操作対象リストのUrl
				myEditor.putString( "nowList_id", String.valueOf(nowList_id));		//☆intで書き込むとcannot be cast
				myEditor.putString( "nowList_data", String.valueOf(nowList_data));	//再生中のプレイリストの保存場所
				listHenkou = true;
			}
			dbMsg += ">書込み>[" + nowList_id +"]"+ nowList;	////////////////
			dbMsg += ",rp_pp=" + rp_pp;	////////////////
			dbMsg += ",pref_data_url= "+ pref_data_url ;////////
			myEditor.putString( "pref_data_url", String.valueOf(pref_data_url));		//再生中のファイル名
			pref_zenkyoku_list_id = getAllSongItems();
			dbMsg += " [全曲リスト； " + pref_zenkyoku_list_id + "]";/////////////////////////////////////
			mIndex = musicPlaylist.getPlaylistItemOrder(sousalistID,senntakuItem);			//Item.getMPItem( senntakuItem );
//			mIndex = Item.getMPItem(pref_data_url);
			dbMsg += "[mIndex=" + mIndex +"]";	//+ "/" +mItems.size()+"]";/////////////////////////////////////
			Cursor playingItem = musicPlaylist.getPlaylistItems(nowList_id, mIndex);
			if (playingItem.moveToFirst()) {
				creditArtistName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST));        //playingItem.artist;	//クレジットされているアーティスト名
				dbMsg += " ,クレジット⁼ " + creditArtistName;
				albumName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM));        //playingItem.album;			//アルバム名
				dbMsg += " , アルバム⁼" + albumName;/////////////////////////////////////	this.album = album;
				titolName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE));        //playingItem.title;		//曲名
				dbMsg += " ,タイトル= " + titolName;/////////////////////////////////////		this.title = title;
				int audioId = Integer.parseInt(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)));
				dbMsg += " ,audioId= " + audioId;
//				albumArtist = musicPlaylist.getAlbumArtist(audioId , MaraSonActivity.this);	//playingItem.album_artist;	//アルバムアーティスト名
//				dbMsg +=" ,アルバムアーティスト= " + albumArtist;
				Uri dataUri = Uri.parse(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA)));
				String dataFN = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA));
				//String.valueOf(dataUri);
				dbMsg += ",読込確認；dataFN=" + dataFN;/////////////////////////////////////
				myEditor.putInt("pref_mIndex", mIndex);
				int duration = Integer.parseInt(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.DURATION)));
				dbMsg += ".再生時間=" + duration + "[ms]";/////////////////////////////////////
				myEditor.putInt( "pref_duration", duration);
				boolean kakikomi = myEditor.commit();
				dbMsg +=",書き込み=" + kakikomi+",リスト変更=" + listHenkou;	////////////////

			}
			playingItem.close();
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}


	/**
	 *  プレイヤーにuriを送る onClickが//プレイヤーフィールド部の土台
	 *  listClickのタイトルリストクリック3通り
	 *  onClickでプレイヤーのクリック
	 *  jyoukyouBunki
	 *  */
	public void send2Player(String pref_data_url , String listName ,boolean toPlaying) {																//プレイヤーにuriを送る   , String aName
		final String TAG = "send2Player";
		String dbMsg = "";
		try{
			dbMsg += ",pref_data_url=" + pref_data_url;/////////////////////////////////////
			boolean retBool = setPrefStr("pref_data_url" , pref_data_url , MuList.this);        //プリファレンスの読込み
			dbMsg += "を書込み" + retBool;
			dbMsg += ",setPref=" + toPlaying;/////////////////////////////////////
			if(toPlaying){
				imanoJyoutai = chyangeSong;
				IsPlaying = false;
//				sousinnMaeSyori( pref_data_url );
			}
			dbMsg += ",プレイリスト[ID=" + nowList_id + "]" + listName;
			dbMsg += "]pref_data_url=" + pref_data_url;
			dbMsg += " の" +  saiseiJikan +"から再生";

//			toPlaying = send2Service( pref_data_url,listName,toPlaying);
//			dbMsg += ",toPlaying=" + toPlaying;

			Intent intent = new Intent(MuList.this, MaraSonActivity.class);

			intent.putExtra("reqCode",imanoJyoutai);
			intent.putExtra("nowList_id",nowList_id);
			intent.putExtra("nowList",nowList);
			intent.putExtra("pref_data_url",pref_data_url);
//		intent.putExtra("mIndex" , mIndex);
			dbMsg +=",再生中=" + IsPlaying;/////////////////////////////////////
			intent.putExtra( "IsPlaying",IsPlaying);		// ;			//再生中か
//			if(toPlaying) {
//				intent.putExtra("to_play",true);
//			}else{
//				intent.putExtra("to_play",false);
//			}
			intent.putExtra("toPlaying",toPlaying);
//			intent.putExtra("nowList_data",nowList_data);
//			dbMsg +="/"+saiseiJikan +"mS";
//			intent.putExtra("saiseiJikan",saiseiJikan);
//				dbMsg +=",imanoJyoutai="+imanoJyoutai;
//				intent.putExtra("imanoJyoutai",imanoJyoutai);
////				dbMsg +=",Bluetoothの接続に連携=" + pref_bt_renkei;/////////////////////////////////////
//				intent.putExtra( "pref_bt_renkei",pref_bt_renkei);
////				dbMsg +=",前回=" + pref_zenkai_saiseKyoku +"曲";/////////////////////////////////////
//				intent.putExtra( "pref_zenkai_saiseKyoku",pref_zenkai_saiseKyoku);
////				dbMsg += pref_zenkai_saiseijikann +"mS";/////////////////////////////////////
//				intent.putExtra( "pref_zenkai_saiseijikann",pref_zenkai_saiseijikann);
//				intent.putExtra("kidou_jyoukyou",MyConstants.kidou_std);
////				dbMsg += ",pref_lockscreen=" + pref_lockscreen;/////////////////////////////////////
//				intent.putExtra("pref_lockscreen",pref_lockscreen);
////				dbMsg += ",pref_notifplayer=" + pref_notifplayer;/////////////////////////////////////
//				intent.putExtra("pref_notifplayer",pref_notifplayer);
////				dbMsg += ",点間リピート中="+rp_pp;/////////////////////////////////////
//				intent.putExtra("rp_pp",rp_pp);
////				dbMsg += ",リピート区間開始点="+pp_start;/////////////////////////////////////
//				intent.putExtra("pp_start",pp_start);
////				dbMsg += ",リピート区間終了点="+pp_end;/////////////////////////////////////
//				intent.putExtra("pp_end",pp_end);
//				intent.putExtra("set_pref",toPlaying);
			resultLauncher.launch(intent);

			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}

	/**リストからの戻り処理 */
	public void back2Player(String saisei_fname ){			///リストからの戻り処理
		final String TAG = "back2Player[MuList]";
		String dbMsg = "";
		try{
			dbMsg +=  "[" + nowList_id +"]"+nowList+"("+nowList_data+")";	////////////////
			sousinnMaeSyori( saisei_fname );										//送信前のプリファレンス処理など//
			dbMsg += ">>[" + nowList_id +"]"+nowList+"("+nowList_data+")";	////////////////
			Intent data = new Intent();			// 返すデータ(Intent&Bundle)の作成
			Bundle bundle = new Bundle();
			dbMsg +=",reqCode="+reqCode;
			bundle.putInt("reqCode",reqCode);
			bundle.putInt("imanoJyoutai",MuList.chyangeSong); 			//201；リストで曲選択
			dbMsg +="/"+saiseiJikan +"mS";
			bundle.putInt("saiseiJikan",saiseiJikan);
			dbMsg +=",シンプルなリスト表示=" + pref_list_simple;/////////////////////////////////////
			bundle.putBoolean( "pref_list_simple",pref_list_simple);
			dbMsg +=",再生中=" + IsPlaying;/////////////////////////////////////
			bundle.putBoolean( "IsPlaying",IsPlaying);
			dbMsg +=",Bluetoothの接続に連携=" + pref_bt_renkei;/////////////////////////////////////
			bundle.putBoolean( "pref_bt_renkei",pref_bt_renkei);
			dbMsg +=",前回=" + pref_zenkai_saiseKyoku +"曲";/////////////////////////////////////
			bundle.putInt( "pref_zenkai_saiseKyoku",pref_zenkai_saiseKyoku);
			dbMsg += pref_zenkai_saiseijikann;/////////////////////////////////////
			bundle.putLong( "pref_zenkai_saiseijikann",pref_zenkai_saiseijikann);
			dbMsg += ",再生中のプレイリスト名=" + nowList;/////////////////////////////////////
			bundle.putString( "nowList",nowList);
			dbMsg += ",プレイリストID=" + nowList_id;/////////////////////////////////////
			bundle.putString( "nowList_id", String.valueOf(nowList_id) );
			dbMsg += ",プレイリストの保存場所= " + nowList_data;//////////////////////////////////
			bundle.putString("nowList_data",nowList_data);
			dbMsg += ",mIndex=" + mIndex;/////////////////////////////////////
			bundle.putInt("pref_mIndex",mIndex);
			dbMsg += ",点間リピート中="+rp_pp;/////////////////////////////////////
			bundle.putBoolean( "rp_pp",rp_pp);
			dbMsg += ",リピート区間="+pp_start;/////////////////////////////////////
			bundle.putInt( "pp_start",pp_start);
			dbMsg += "～"+pp_end;/////////////////////////////////////
			bundle.putInt( "pp_end",pp_end);
			myLog(TAG,dbMsg);
			data.putExtras(bundle);
			setResult(RESULT_OK, data);		// setResult() で bundle を載せた送るIntent dataをセットする
														// 第一引数は…Activity.RESULT_OK, Activity.RESULT_CANCELED など
		//	MuList.this.finish();
		} catch (Exception e) {		//汎用
			myErrorLog(TAG,"で"+e.toString());
		}
	}

	//サービス
	public ComponentName MPSName;
	public MusicPlayerService MPS;
	public MusicReceiver mReceiver;
	Handler mHandler = new Handler();
	public IntentFilter mFilter;

	public boolean isActionChange(String action ){			///リストからの戻り処理
		final String TAG = "isActionChange";
		String dbMsg = "";
		boolean retBool = true;
		try{
			dbMsg +=",action= "+ action;
			if(action != null){
				String btDescription = lp_ppPButton.getContentDescription() + "";			//.toString();
				dbMsg +=",btDescription= "+ btDescription;
				if (btDescription == null) {           //再生中か
					retBool = false;
				}else if (btDescription.equals(getResources().getText(R.string.play)) && action.equals( "com.example.android.remotecontrol.ACTION_PLAY")) {           //再生中か
					retBool = false;
				}else if (btDescription.equals(getResources().getText(R.string.pause)) && action.equals( "com.example.android.remotecontrol.ACTION_PAUSE")) {
					retBool = false;
				} else{
					dbMsg +=",retBool= "+ retBool;
					myLog(TAG, dbMsg);
				}
			} else{
				retBool = false;
			}
			dbMsg += ",既に再生中= "+ IsPlayingNow +  ",IsPlaying= "+ IsPlaying;
			if(IsPlayingNow){
				if(IsPlaying){
					retBool = false;
				}else{
					myLog(TAG, dbMsg);
				}
			}else{
				if(! IsPlaying){
					retBool = false;
				}else{
					myLog(TAG, dbMsg);
				}
			}
		} catch (Exception e) {		//汎用
			myErrorLog(TAG,"で"+e.toString());
		}
		return retBool;
	}

	/**
	 *  Chronometerの値を更新し、、再生中はカウントアップ
	 *  Play/Pause が切り替わった時と、プレイヤーから戻って曲変更された時
	* **/
	public void setChronometer(int mcPosition , boolean IsPlaying ){			///リストからの戻り処理
		final String TAG = "setChronometer";
		String dbMsg = "";
		try{
			dbMsg += " , isFocusNow=" + isFocusNow;
			backTime= backTime - System.currentTimeMillis();						//このActivtyに戻ってからの時間
			long current = SystemClock.elapsedRealtime() - mcPosition + backTime;																					//再生ポイントを取得
			dbMsg += " , current:" + current;
			dbMsg += " , mcPosition:" + mcPosition;
			lp_chronometer.setBase(current);             //long
			SimpleDateFormat dataFormat = new SimpleDateFormat("mm:ss", Locale.JAPAN);
			String  mcPositionStr = dataFormat.format(mcPosition);
			dbMsg += " , mcPosition:" + mcPositionStr;

			dbMsg +=",IsPlaying= "+ IsPlaying;//ボタンフェイスの変更はクリック時
			if(IsPlaying){                                  				//再生状態で戻ってきた時点で表示
				lp_chronometer.start();
			}else{
				lp_chronometer.stop();
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {		//汎用
			myErrorLog(TAG,"で"+e.toString());
		}
	}

	//②サービスで稼働している情報をActivtyに書き込む/////////////////////////////////////////////////①起動動作
	public class MusicReceiver extends BroadcastReceiver{
			@Override
			public void onReceive(Context context, final Intent intent) {
				mHandler.post(new Runnable() {
					@SuppressLint("Range")
					public void run() {
						final String TAG = "onReceive";
						String dbMsg = ".[MuList]";
						try{
							dbMsg += " , isFocusNow=" + isFocusNow;
							String state = intent.getStringExtra("state");
							dbMsg += ",state=" + state;
							String action = intent.getStringExtra("action");
							int mcPosition = intent.getIntExtra("mcPosition", 0);		////run[changeCount.MusicPlayerService]で取得、sendPlayerState[MusicPlayerService]で初期値取得
							dbMsg += "[再生ポジション=" + mcPosition + "/";
							IsPlaying = intent.getBooleanExtra("IsPlaying", false);			//再生中か
							dbMsg +=",IsPlaying= "+ IsPlaying;//ボタンフェイスの変更はクリック時
							if(IsPlaying){
								action =  "com.example.android.remotecontrol.ACTION_PLAY";
								IsPlayingNow = true;		//既に再生中
								MuList.this.lp_ppPButton.setContentDescription(getResources().getText(R.string.play));
							}else{
								action =  "com.example.android.remotecontrol.ACTION_PAUSE";
								IsPlayingNow = false;
								MuList.this.lp_ppPButton.setContentDescription(getResources().getText(R.string.pause));
							}
							if(isActionChange(action)){
								SimpleDateFormat dataFormat = new SimpleDateFormat("mm:ss", Locale.JAPAN);
								String  mcPositionStr = dataFormat.format(mcPosition);
								dbMsg += " , mcPosition:" + mcPositionStr;            //この文字では設定できない
								MuList.this.setChronometer(mcPosition ,  IsPlaying );
								b_action = action;
							}
							dbMsg += ",b_action=" + action + ">>" + action;
							String saisei_fname =intent.getStringExtra("pref_data_url");
							dbMsg += ",受信ファイル；" + saisei_fname + " を　";
							if(saisei_fname == null || saisei_fname.equals("")) {
							}else{
								String pefName = context.getResources().getString(R.string.pref_main_file);
								dbMsg += ",前のファイル；" + b_saisei_fname + " を　";
								if(! saisei_fname.equals(b_saisei_fname)){
									dbMsg += saisei_fname + "に変更";
									b_saisei_fname = saisei_fname;
									mIndex = intent.getIntExtra("mIndex", 0);
									dbMsg +="[mIndex=" + mIndex + "]";
									Cursor playingItem = musicPlaylist.getPlaylistItems(nowList_id, mIndex);
									if (playingItem.moveToFirst()) {
										creditArtistName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST));        //playingItem.artist;	//クレジットされているアーティスト名
										dbMsg += " ,クレジット⁼ " + creditArtistName;
										albumName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM));        //playingItem.album;			//アルバム名
										dbMsg += " , アルバム⁼" + albumName;/////////////////////////////////////	this.album = album;
										titolName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE));        //playingItem.title;		//曲名
										dbMsg += " ,タイトル= " + titolName;/////////////////////////////////////		this.title = title;
										int audioId = Integer.parseInt(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)));
										dbMsg += " ,audioId= " + audioId;
										Uri dataUri = Uri.parse(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA)));
										String dataFN = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA));
//										dbMsg += ".再生時間=" + duration + "[ms]";/////////////////////////////////////
										lp_artist.setText( creditArtistName);
										lp_album.setText( albumName);
										lp_title.setText( titolName);
										String album_art =intent.getStringExtra("album_art") +"";
										dbMsg +=",album_art=" + album_art;
										if(! album_art.equals("")){
											OrgUtil ORGUT = new OrgUtil();				//自作関数集
											WindowManager wm = (WindowManager)MuList.this.getSystemService(Context.WINDOW_SERVICE);
											Display disp = wm.getDefaultDisplay();
											ImageView rc_Img = findViewById(R.id.rc_Img);			//ヘッダーのアイコン表示枠				headImgIV = (ImageView)findViewById(R.id.headImg);		//ヘッダーのアイコン表示枠
											int width = rc_Img.getWidth();
											width = width*9/10;
											Bitmap mDummyAlbumArt = ORGUT.retBitMap(album_art , width , width , getResources());        //指定したURiのBitmapを返す	 , dHighet , dWith ,
											rc_Img.setImageBitmap(mDummyAlbumArt);
										}
										setListPlayer( creditArtistName, albumName, titolName, album_art);

									}
									playingItem.close();
									myLog(TAG, dbMsg);
								}
							}
						} catch (Exception e) {
							myErrorLog(TAG,"で"+e);
						}
					}
				});
			}
		}            //MusicPlayerReceiver

	/**
	 * レシーバーを生成
	 * 呼出し元は	onCreate , mData2Service	rusekiKousin	//onResume , playing 	onClick		keizoku2Service	onStopTrackingTouch	onKey
	 * ☆サービスからサービスは呼び出せないのでこのアクティビティから呼び出す。
	 * */
	public void receiverSeisei(){		//レシーバーを生成 <onResume , playing , mData2Service	onClick
		final String TAG = "receiverSeisei";
		String dbMsg= "";
		try{
			dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			dbMsg= dbMsg +",mReceiver=" + mReceiver;////////////////////////
			if( mReceiver== null ){
				mFilter = new IntentFilter();
				mFilter.addAction(MusicPlayerService.ACTION_STATE_CHANGED);
				mReceiver = new MusicReceiver();
				registerReceiver(mReceiver, mFilter);                        //レシーバーを指定する旨を記述すれば、Android 8.0端末でもOK?
				//that was originally registered here. Are you missing a call to unregisterReceiver()?
				dbMsg +=">生成>=" + mReceiver;////////////////////////
			} else{
				dbMsg += "mReceiver = null";
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void receiverHaki(){		//レシーバーを破棄
		final String TAG = "receiverHaki";
		String dbMsg= "[MaraSonActivity]";/////////////////////////////////////
		try{
			dbMsg += "mReceivert=" + mReceiver;
			if( mReceiver != null ){
				unregisterReceiver(mReceiver);
				mReceiver = null;
				dbMsg += ">>" + mReceiver;
				list_player.setVisibility(View.GONE);
			} else{
				dbMsg += "mReceiver = null";
			}
//			dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	//設定値管理////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void reWriteAllVal(){ //変数全設定	★wriAllPrifに続けてreadPrifを呼び出しても更新がされていない
		final String TAG = "reWriteAllVal";
		String dbMsg = "";
		try{
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////設定値管理///
	/** 情報表示しないシンプルなリスト */
	public void makePlainList(List<String> listItems) {			//情報表示しないシンプルなリスト
		System.currentTimeMillis();
		final String TAG = "makePlainList";
		String dbMsg = "";
		try{
			dbMsg += "listItems=" + listItems.size() + "件";
			ArrayAdapter<String> adapter = null;
			adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listItems);	//☆getApplicationContext()だと文字が小さくなった
																															//☆thisだと;Activityはメモリの逼迫等に引きずられて、簡単に死にます(インスタンスがnull)。	http://qiita.com/yu_eguchi/items/65311af1c9fc0bff0cb0
		//	dbMsg += "," + adapter.getCount() +"項目の" + senntakuItem +"を選択";///あ;12354～65437,;8,は;Shift;13
			dbMsg += "," + senntakuItem +"の" + adapter.getItem(0) + "～"+ adapter.getItem(adapter.getCount()-1) +";"+ adapter.getCount() +"項目の" + senntakuItem +"を選択";///あ;12354～65437,;8,は;Shift;13
			lvID.setAdapter(adapter);
			//リストアイテムのイベント処理/////////////////////////////////////////////////////////////////////////////////////////////////////////
			lvID.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					final String TAG = "onItemClick";
					String dbMsg = "[MuList.makePlainList]";
					try{
						dbMsg += ",position=" + position + ",id=" + id;
						myLog(TAG, dbMsg);
						listClick( parent, view, position, id);			//共有：クリックの処理
					}catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}
			});
			if(reqCode == MyConstants.v_play_list){
				return;
			}

			lvID.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){	// 項目が長押しクリックされた時のハンドラ
				// リスナーを登録する thisを引数にできない //☆キャストの方法　(OnItemLongClickListener) this
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {	// 長押しクリックされた時の処理を記述
					final String TAG = "onItemLongClick";
					String dbMsg = "[MuList.makePlainList]"+ORGUT.nowTime(true,true,true);/////////////////////////////////////
					try{
						dbMsg += ",position=" + position + ",id=" + id;
						myLog(TAG, dbMsg);
						listLongClick( parent, view, position, id);		//共有：ロングクリックの処理
					}catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
					return false;
				}
			});		//onItemLongClick

			lvID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {				//端末の十字キー等でリストの項目がフォーカスされた
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
			/*リストの項目が選択された（端末の十字キー等でリストの項目がフォーカスされた）時と、選択されていない時の処理
			 *http://techbooster.org/android/ui/9039/ */
			dbMsg += ",mIndex= " + mIndex;
			dbMsg +=",nowList=" + nowList;///あ;12354～65437,;8,は;Shift;13
			int sPosition = mIndex;
			if( nowList.equals(getResources().getString(R.string.listmei_zemkyoku))){		// ;		// 全曲リスト</string>
				dbMsg +=",itemStr=" + itemStr;
				if(itemStr != null){
					sPosition = listItems.indexOf(itemStr);
				}else{
					dbMsg +=">>senntakuItem=" + senntakuItem;
					sPosition = listItems.indexOf(senntakuItem);
				}
			}
			dbMsg +="(" + sPosition + ")";
			if(-1 < sPosition){
				lvID.setSelection(sPosition);
		//		lvID.getAdapter().getItem(sPosition).setBackgroundColor(getResources().getColor(R.color.blue_fezer));
		//		lvID.getSelectedView().setBackgroundColor(Color.BLUE);				//getResources().getColor(R.color.blue_dark)
				lvID.setFocusable(true);
				lvID.setFocusableInTouchMode(true);
//				lvID.requestFocus();
			}
			lvID.requestFocus();
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	public SimpleDateFormat sdffiles = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	int listId = 0;
	public int selPosition;

	/**リストクリック時の処理分岐*/
	public void listClick(AdapterView<?> parent, View view, int position, long id) {			//共有：クリックの処理
		System.currentTimeMillis();
		final String TAG = "listClick";
		String dbMsg = "";
		try{
			dbMsg +="reqCode="+reqCode;
			selPosition = position;
			CharSequence itemCS;
			TreeEntry treeEntry;
			String rStr;
			dbMsg += ",シンプルなリスト表示="+pref_list_simple+ ":position="+position+",[id="+id+"]";////////"リスト；parent="+parent+",view="+view+
			dbMsg += ",artist="+sousa_artist+ ":album="+sousa_alubm + ",titol=" + sousa_titol;
			dbMsg += ",プレイリスト:sousalistName="+sousalistName + ",nowList:"+nowList;
			if( sousalistName.equals(getResources().getString(R.string.listmei_zemkyoku)) && reqCode <=MyConstants.v_titol) {        // 全曲リストのアーティスト選択
				dbMsg += ",全曲リストでreqCode=" + reqCode;
				switch (reqCode) {
					case MyConstants.v_artist:                            //195 ;2131558436 アーティスト
						dbMsg += "=アーティストから";
						itemStr = String.valueOf(artistSL.get(position));        //クレジットされているアーティスト名
						sousa_artist = itemStr;
						sousa_alubm = null;
						sousa_titol = null;
						reqCode = MyConstants.v_alubum;
						dbMsg += ",reqCode=" + reqCode;/////////////////////////////////////
						dbMsg += ",sousa_artist=" + sousa_artist + "のアルバムリスト作成";////////////////////////////////////
						sigotoFuriwake(reqCode, sousa_artist, sousa_alubm, sousa_titol, null);        //表示するリストの振り分け	, albumAL
						break;
					case MyConstants.v_alubum:                            //196;2131558442 アルバム
						dbMsg += ",アルバムから";
						dbMsg += ",albumList=" + albumList.size() + "件";
						itemStr = albumList.get(position);        //アルバム名
						dbMsg += ",itemStr=" + itemStr;
						sousa_alubm = itemStr;
						sousa_titol = null;
						reqCode = MyConstants.v_titol;
						dbMsg += ",sousa_artist=" + sousa_artist + ",sousa_alubm=" + sousa_alubm + "のタイトルリスト作成";////////////////////////////////////
						sigotoFuriwake(reqCode, sousa_artist, sousa_alubm, sousa_titol, null);        //表示するリストの振り分け		, albumAL
						break;
					case MyConstants.v_titol:                        // ; 2131558448 タイトル
						dbMsg += ",タイトルから";
						dbMsg += ",titolList=" + titolList.size() + "件";
						itemStr = titolList.get(position);        //曲名
						dbMsg += ",itemStr=" + itemStr;/////////////////////////////////////
//						setPrefStr("nowList", sousalistName, MuList.this);                //他のプレイリストに該当曲が無ければ全曲に戻されるのでプリファレンスも修正
//						setPrefStr("nowList_id", String.valueOf(sousalistID), MuList.this);
						sousa_titol = itemStr;
						contextTitile = sousa_artist + "/" + sousa_alubm + "/" + sousa_titol;
						b_artist = albumArtist;                //それまで参照していたアーティスト名
						b_album = albumMei;                //それまで参照していたアルバム名
						titolName = itemStr;        //曲名	titolList.get(position)
						String dataFN = String.valueOf(titolAL.get(position).get("DATA"));
						dbMsg += ",再生するのは=" + dataFN;
						setPrefInt("pref_position", 0, MuList.this);
						dbMsg += ",呼出し元=" + yobidashiMoto;    //yobidashiMoto = imanoJyoutai;//起動直後=veiwPlayer;プレイヤーからの呼出し = chyangeSong
						dbMsg += ">>プレイヤーへ";
						send2Player(dataFN, sousalistName,true);                                                //プレイヤーにuriを送る
						break;
				}
			}else if( sousalistName.equals(getString(R.string.listmei_list_all)) || reqCode == MyConstants.v_play_list) {
				dbMsg += ">>プレイリスト一覧、v_play_list";
				listOfListClick(parent, view, position, id);
			} else if( reqCode == MENU_infoKaisou || reqCode == MyConstants.SELECT_SONG){					//539,1253
				dbMsg +="【MENU_infoKaisouかSELECT_SONG】";
				dbMsg += ",plAL=" + plAL.get(position).toString();
				titolName = String.valueOf(plAL.get(position).get("main"));				//= (String) plAL.get(position).get("main");
				dbMsg += ",titolName=" + titolName;
				mIndex = position;
				dbMsg += ",再生するのは[" + mIndex;
				String dataFN = String.valueOf(plAL.get(position).get(MediaStore.Audio.Playlists.Members.DATA));
				dbMsg += "]" + dataFN;
				dbMsg += ",yobidashiMoto=" + yobidashiMoto;	//yobidashiMoto = imanoJyoutai;//起動直後=veiwPlayer;プレイヤーからの呼出し = chyangeSong
				setPrefInt("pref_position", 0, MuList.this);
				send2Player(dataFN , sousalistName,true)  ;
			}else if( reqCode == MyConstants.SELECT_TREE ||
					nowList.equals(getResources().getString(R.string.playlist_namae_saikintuika)) ||
					sousalistName.equals(getResources().getString(R.string.playlist_namae_saikintuika))
			){
				dbMsg += ">>最近追加";
				int depth;
				int artistID;
				int albumID;
				int audioID;
				String duration;
				String Modified;
				boolean kakikomi;
				treeEntry = (TreeEntry)parent.getItemAtPosition(position);
				depth = treeEntry.getDepth();
				dbMsg += ",depth=" + depth;
				if(treeEntry.isExpanded()) {
					expandMap.remove(depth);
					treeEntry.collapse();											//閉じる
				} else {
					if(treeEntry.hasChild()) {
						if(expandMap.containsKey(depth)) {
							expandMap.get(depth).collapse();			//expandMap.get(depth).collapse();ではメソッド collapse() は型 Object で未定義
						}
						treeEntry.expand();											//開く
						dbMsg += ",treeEntry=" + treeEntry;
						expandMap.put(depth, treeEntry);
					}else{
//						dbMsg +=",LayerName=タイトル" ;
//						play_order = treeEntry.getPlayOrder();
//						dbMsg +="(play_order= " + play_order ;
//						artistID = treeEntry.getArtistID();
//						dbMsg +=" ,artistID= " + artistID ;
//						albumID = treeEntry.getAlbumID();
//						dbMsg +=" ,r_albumID= " + albumID ;
//						audioID = treeEntry.getAudioID();
//						dbMsg +=" ,audioID= " + audioID ;
						String dataFN = treeEntry.getDataURL();      //null?
						dbMsg +=")" + dataFN ;
						setPrefStr( "pref_data_url" ,  dataFN , MuList.this);
						setPrefInt("pref_position", 0, MuList.this);
//						duration = treeEntry.getDuration();
//						dbMsg +=",再生時間=" + duration ;
//						Modified = treeEntry.getModified();
//						setPrefInt("pref_position" ,  0 , MuList.this);
//						dbMsg +=" ,Modified= " + Modified ;
//						mIndex = play_order;
//						dbMsg += ",yobidashiMoto=" + yobidashiMoto;	//yobidashiMoto = imanoJyoutai;//起動直後=veiwPlayer;プレイヤーからの呼出し = chyangeSong
//						if ( yobidashiMoto == veiwPlayer  ){											//200;プレイヤーを表示;起動直後
							send2Player(dataFN,getResources().getString(R.string.playlist_namae_saikintuika),true );												//プレイヤーにuriを送る
//						}
					}
				}
//				itemStr = treeEntry.getData().toString();
//				dbMsg += ",表記=" + itemStr;

//				int rLayerName = treeEntry.getLayerName();
//				switch(rLayerName) {
//					case lyer_artist:				// 10;アーティスト
//						dbMsg +=",LayerName=アーティスト" ;
//						break;
//					case lyer_album:				// lyer_artist + 10;アルバム
//						dbMsg +=",LayerName=アルバム" ;
//						break;
//					case lyer_titol:				// lyer_album + 10;タイトル
//						dbMsg +=",LayerName=タイトル" ;
////						play_order = treeEntry.getPlayOrder();
////						dbMsg +="(play_order= " + play_order ;
//////						dbMsg +=")nowList_data= " + nowList_data ;
////						artistID = treeEntry.getArtistID();
////						dbMsg +=" ,artistID= " + artistID ;
////						albumID = treeEntry.getAlbumID();
////						dbMsg +=" ,r_albumID= " + albumID ;
////						audioID = treeEntry.getAudioID();
////						dbMsg +=" ,audioID= " + audioID ;
////						String dataFN = treeEntry.getDataURL();      //null?
////						dbMsg +=")" + dataFN ;
////						duration = treeEntry.getDuration();
////						dbMsg +=",再生時間=" + duration ;
////						Modified = treeEntry.getModified();
////						setPrefStr( "pref_data_url" ,  dataFN , MuList.this);
////						setPrefInt("pref_position" ,  0 , MuList.this);
////						dbMsg +=" ,Modified= " + Modified ;
////						mIndex = play_order;
////						dbMsg += ",yobidashiMoto=" + yobidashiMoto;	//yobidashiMoto = imanoJyoutai;//起動直後=veiwPlayer;プレイヤーからの呼出し = chyangeSong
////						if ( yobidashiMoto == veiwPlayer  ){											//200;プレイヤーを表示;起動直後
////							send2Player(dataFN,true );												//プレイヤーにuriを送る
////						}
//						break;
//					case MENU_TAKAISOU:				//535 多階層リスト選択選択中
//						dbMsg += ",parent=" + parent;
//						dbMsg += ",view=" + view;
//						treeEntry = (TreeEntry)parent.getItemAtPosition(position);
//						depth = treeEntry.getDepth();
//						dbMsg += ",depth=" + depth;
//						if(treeEntry.isExpanded()) {
//							expandMap.remove(depth);
//							treeEntry.collapse();											//閉じる
//						} else {
//							if(treeEntry.hasChild()) {
//								if(expandMap.containsKey(depth)) {
//									expandMap.get(depth).collapse();			//expandMap.get(depth).collapse();ではメソッド collapse() は型 Object で未定義
//								}
//								treeEntry.expand();											//開く
//								dbMsg += ",treeEntry=" + treeEntry;
//								expandMap.put(depth, treeEntry);		//型の安全性: メソッド put(Object, Object) は raw 型 Map に属しています。総称型 Map<K,V> への参照はパラメーター化される必要があります
//							}
//						}
//						itemStr = treeEntry.getData().toString();
//						dbMsg += ",表記=" + itemStr;
//						switch(depth) {
//							case 0:													//アーティスト
//								break;
//							case 1:													//アルバム
//								break;
//							case 2:													//曲
//								String dataFN = treeEntry.getDataURL();
//								dbMsg +=")" + dataFN ;
//								if ( yobidashiMoto == veiwPlayer  ){											//200;プレイヤーを表示;起動直後
//									send2Player(dataFN ,true);												//プレイヤーにuriを送る
//								}
//								break;
//						}
//						break;
//				}
			}else if( sousalistName.equals(plNameSL.get(position)) &&
						!sousalistName.equals(getResources().getString(R.string.playlist_namae_saikintuika))
				){
				dbMsg += ">>最近追加以外";
				if(reqCode != MyConstants.SELECT_SONG){
					int listID = musicPlaylist.getPlaylistId(MuList.this.sousalistName);
					String pdMessage = MuList.this.sousalistName;
					CreatePLList( listID , pdMessage);		//プレイリストの内容取得			MuList.this.sousalist_data,
				}
			}else{		//操作対象リストID		 if( 0 < sousalistID   )
				dbMsg +=",sousalist["+sousalistID + "]" + sousalistName;
				if( reqCode == listType_2ly ){					//3
					dbMsg +=",listType_2ly";
					if( sousalistName.equals(getResources().getString(R.string.listmei_zemkyoku)) ){		// 全曲リストのアーティスト選択
						itemStr=artistSL.get(Integer.valueOf(String.valueOf(id)) );
					}else{
						itemStr=String.valueOf( parent.getItemAtPosition(position));
					}
					sousaAlbumArtist = itemStr;	//操作対象のアルバムアーティスト名
					dbMsg += sousaAlbumArtist + "の2階層化へ";
					plAlbumTitol( sousalistID , sousaAlbumArtist);		//指定したプレイリストから特定アーティストのアルバムとタイトル内容取得
				}
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/** リストアイテムのロングクリック処理*/
	public void listLongClick(AdapterView<?> parent, View view, int position, long id) {			//共有：ロングクリックの処理
		System.currentTimeMillis();
		final String TAG = "listLongClick";
		String dbMsg = "";
		try{
			selPosition = position;
			dbMsg += "pref_list_simple="+pref_list_simple;////////"リスト；parent="+parent+",view="+view+
			dbMsg +=  ",reqCode="+ reqCode;////////"リスト；parent="+parent+",view="+view+
			dbMsg += ",[id;"+id+"]"+  ",[position;"+position+ "]";////////"リスト；parent="+parent+",view="+view+
			SelectLocation = position;
			dbMsg +=",SelectLocation=" + SelectLocation;
			sousaRecordId = -1;		//操作対象レコードのID
			sousaRecordUrl = null;		//操作対象レコードのUrl
			sousaRecordPlayOrder = position;
			if( sousalistName.equals(getResources().getString(R.string.listmei_zemkyoku)) ){		// 全曲リストのアーティスト選択
				b_reqCode=reqCode;							//処理コードの保留;選択していたリスト
				switch(reqCode) {
				case MyConstants.v_artist:							//2131427334アーティスト
					itemStr = String.valueOf(artistSL.get(position));		//クレジットされているアーティスト名
					sousa_artist = itemStr;
					sousa_alubm = null;
					sousa_titol = null;
					layerCode = lyer_artist ;							//アーティスト
					break;
				case MyConstants.v_alubum:							//2131558442 アルバム
					dbMsg +=",アルバムから";
					itemStr = albumList.get(position);		//アルバム名
					sousa_alubm = itemStr;
					sousa_titol = null;
					layerCode = lyer_album ;			//アルバム
					break;
				case MyConstants.v_titol:						//2131558448 タイトル
					dbMsg +=",タイトルから";
					itemStr = titolList.get(position);		//曲名
					dbMsg += "artist=" + sousa_artist+ "・・・" + sousa_alubmArtist;/////////////////////////////////////
					dbMsg += ",alubm=" + sousa_alubm;/////////////////////////////////////
					sousa_titol = itemStr;
					dbMsg += ",操作しようとするtitol=" + sousa_titol;/////////////////////////////////////
					layerCode = lyer_titol ;				//タイトル
					contextTitile = sousa_artist +"\n "+ sousa_alubm +"\n "+ sousa_titol;
					break;
				}
			}else{
				int depth;
				TreeEntry treeEntry;
				dbMsg +=",sousalist["+sousalistID + "]" + sousalistName;

				if( reqCode == listType_2ly ){					//2;アーティストだけの単階層リスト
					if( sousalistName.equals(getResources().getString(R.string.listmei_zemkyoku)) ){		// 全曲リストのアーティスト選択
						itemStr=artistSL.get(Integer.valueOf(String.valueOf(id)) );
					}else{
						itemStr=String.valueOf( parent.getItemAtPosition(position));
					}
					sousaAlbumArtist = itemStr;	//操作対象のアルバムアーティスト名
					layerCode = lyer_artist ;							//アーティスト
					dbMsg += sousaAlbumArtist + "の2階層化へ";
				}else if( reqCode == MENU_infoKaisou ){					//539;情報付きリスト書き込み中
					itemStr = String.valueOf(plSL.get(position));		//クレジットされているアーティスト名
					layerCode = lyer_titol;				//:				//タイトル
				} else {
					treeEntry = (TreeEntry)parent.getItemAtPosition(position);
					depth = treeEntry.getDepth();
					dbMsg += ",depth=" + depth;
					itemStr = treeEntry.getData().toString();
					dbMsg += ",表記=" + itemStr;

					int rLayerName = treeEntry.getLayerName();
					String rStr;
					switch(rLayerName) {
					case lyer_artist:				// 10;アーティスト
						dbMsg +=",LayerName=アーティスト" ;
						layerCode = lyer_artist ;							//アーティスト
						break;
					case lyer_album:				// lyer_artist + 10;アルバム
						dbMsg +=",LayerName=アルバム" ;
						layerCode = lyer_album ;			//アルバム
						break;
					case lyer_titol:				// lyer_album + 10;タイトル
						dbMsg +=",LayerName=タイトル" ;
						layerCode = lyer_titol ;				//タイトル
						break;
					case MENU_TAKAISOU:				//537 多階層リスト選択選択中
						dbMsg += ",rLayerName=MENU_TAKAISOU";
						dbMsg += ",parent=" + parent;
						dbMsg += ",view=" + view;
						treeEntry = (TreeEntry)parent.getItemAtPosition(position);
						depth = treeEntry.getDepth();
						dbMsg += ",depth=" + depth;
						itemStr = treeEntry.getData().toString();
						dbMsg += ",表記=" + itemStr;
						treeEntry = (TreeEntry)parent.getItemAtPosition(position);
						if( parent == null ){
							parent = lvID;
							dbMsg += ">>" + parent;
						}
						switch(depth) {
						case 0:													//アーティスト
							layerCode = lyer_artist ;							//アーティスト
							break;
						case 1:													//アルバム
							layerCode = lyer_album ;			//アルバム
							break;
						case 2:													//曲
							layerCode = lyer_titol ;				//タイトル
							contextTitile = treeEntry.getData().toString();
							dbMsg += ",表記=" + contextTitile;
							sousaRecordId = treeEntry.getAudioID();		//操作対象レコードのID
							dbMsg += ",操作対象レコードのAudioID=" + sousaRecordId;
							sousaRecordPlayOrder= treeEntry.getPlayOrder();
							dbMsg += ",PlayOrder=" + sousaRecordPlayOrder;
							sousaRecordUrl = treeEntry.getDataURL();		//操作対象レコードのUrl
							dbMsg += ",Url=" + sousaRecordUrl;
							dbMsg += ",parent=" + parent;			// pref_list_simple=true,reqCode=534,[id;0],[position;6]null
							break;
						}
						break;
					}
				}
			}
			dbMsg += ",itemStr=" + itemStr;			// pref_list_simple=true,reqCode=534,[id;0],[position;6]null
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/** ヘッドが クリックされた時の処理*/
	public void headClickAction() {
		final String TAG = "headClickAction";
		String dbMsg = "";
		try{
			dbMsg = "操作中のリスト="+sousalistName;////////"リスト；parent="+parent+",view="+view+
			if( sousalistName.equals(getResources().getString(R.string.listmei_zemkyoku)) ){		// 全曲リストのアーティスト選択
				dbMsg +=",現在;reqCode=" + reqCode + ",albumArtist=" + MuList.this.albumArtist;////////////////////////////////////
				if( reqCode != MyConstants.v_artist && MuList.this.albumArtist == null){
					albumArtist = Artist2albumAetist( creditArtistName) ;
					dbMsg +=">>" + albumArtist ;////////////////////////////////////
				}
				dbMsg += ",album=" + MuList.this.albumName;////////////////////////////////////
				switch(reqCode) {			//backCode
				case MyConstants.v_artist:				//<<quitMe();<<MuList.this.finish();
					dbMsg +=",クリックしたのはartistリストのヘッド" ;
					reqCode = MyConstants.v_play_list;
					dbMsg +=",現在[" + sousalistID + "]" + sousalistName +"次はプレイリスト一覧：" + reqCode ;
					senntakuItem = sousalistName;
					sigotoFuriwake(reqCode , sousalistName , null  , null , null);		//表示するリストの振り分け		, albumAL
					break;
				case MyConstants.v_alubum:				//2131558442  アルバム
					dbMsg +=",クリックしたのはalbumリストのヘッド" ;////////////////////////////////////
					reqCode = MyConstants.v_artist;
					dbMsg +=",sousa_artist="+ sousa_artist  ;////////////////////////////////////
					senntakuItem = sousa_artist;
					sigotoFuriwake(reqCode , sousa_artist , null  , null , null);		//表示するリストの振り分け		, albumAL
					break;
				case MyConstants.v_titol:						//2131558448 	タイトル
					dbMsg +=",クリックしたのはtitolリストのヘッド" ;////////////////////////////////////
					reqCode = MyConstants.v_alubum;
					dbMsg +=",sousa_artist="+ sousa_artist +",sousa_alubm=" + sousa_alubm ;////////////////////////////////////
					senntakuItem = sousa_alubm ;
					sigotoFuriwake(reqCode , sousa_artist , sousa_alubm  , null , null);		//表示するリストの振り分け		, albumAL
					break;
				default:
					dbMsg +=",reqCode = MyConstants.v_play_listに変更" ;////////////////////////////////////
					reqCode = MyConstants.v_play_list;
					sigotoFuriwake(reqCode , null , null  , null , null);		//表示するリストの振り分け		, albumAL
					//		MuList.this.finish();				//プレイヤーからquitMeを呼ばれても仕事が残っていたら終わらない
					break;
				}
				dbMsg +=",次は;reqCode=" + reqCode ;////////////////////////////////////
			}else{
				dbMsg +=",imanoJyoutai=" + imanoJyoutai ;////////////////////////////////////
				switch(reqCode) {			//backCode
					case listType_2ly:				// = listType_info + 1;アーティストの単階層リストとalbumとtitolの２階層
						reqCode = MyConstants.v_play_list;
						sigotoFuriwake(reqCode , null , null  , null , null);		//表示するリストの振り分け		, albumAL
						//		MuList.this.finish();				//プレイヤーからquitMeを呼ばれても仕事が残っていたら終わらない
						break;
					case MENU_TAKAISOU:						//535多階層リスト選択選択中
						headImgIV.setVisibility(View.GONE);								 // 表示枠を消す
						mainHTF.setVisibility(View.GONE);
						pl_sp.setVisibility(View.VISIBLE);
						reqCode = listType_2ly;								//2階層リスト選択選択中
						if( sousalistName.equals(getResources().getString(R.string.listmei_zemkyoku))){
							if(artistSL == null){
								artistList_yomikomi();								//アーティストリストを読み込む(db未作成時は-) ☆フォーカスはダイアログに移る
							}
							makePlainList( artistSL);			//階層化しないシンプルなリスト
						}else if(artistSL != null){
							String subTStr = getResources().getString(R.string.pp_artist)+artistSL.size() +  getResources().getString(R.string.comon_nin);			//アーティスト
							subHTF.setText(subTStr);					//ヘッダーのサブテキスト表示枠
							makePlainList( artistSL);			//階層化しないシンプルなリスト
						}else{
							dbMsg +="[選択="+ sousalistID + "(nowList=" +nowList_id+")]"+ MuList.this.sousalistName;
							dbMsg =nowList ;
							dbMsg +=" ,保存場所= " + sousalist_data ;
							String pdMessage = MuList.this.sousalistName;
							CreatePLList(  Long.valueOf(MuList.this.sousalistID) , pdMessage);		//プレイリストの内容取得			MuList.this.sousalist_data,
						}
						break;
					default:
						reqCode = MyConstants.v_play_list;
						sigotoFuriwake(reqCode , null , null  , null , null);		//表示するリストの振り分け		, albumAL
						//		MuList.this.finish();				//プレイヤーからquitMeを呼ばれても仕事が残っていたら終わらない
						break;
				}
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

//拡張リスト//////////////////////////////////////////////////////////////////////////////////////////////////////
	/** イメージとサブテキストを持ったリスト */
	public void setHeadImgList(List<Map<String, Object>> ItemAL ) {		////①ⅵ6；イメージとサブテキストを持ったリストを構成/☆リスト選択対応
		final String TAG = "setHeadImgList";
		String dbMsg = "";
		try{
			dbMsg += ORGUT.nowTime(true,true,true);
			if( cursor != null){
				if(! cursor.isClosed()){
					cursor.close();
				}
			}
			if(Zenkyoku_db != null){
				if(Zenkyoku_db.isOpen()){
					Zenkyoku_db.close();
				}
			}
			dbMsg += "reqCode" + reqCode + "：" + ItemAL.size() +"件";
			if(0<ItemAL.size()){
				dbMsg +=ItemAL.get(0) +"～" +ItemAL.get(ItemAL.size()-1);
			}
			String album_art = null;			//アルバムアートUriだけを返す
			int sPosition = 0;
			isList = null;
			isList = new ArrayList<CustomData>();		//ヘッドイメージとサブテキストを持ったリストアイテム
			for(int i=0;i<ItemAL.size();i++){
				dbMsg +=  "\n" + i +"/" + ItemAL.size() ;///////////////////////////////////////////////////////////////////////////////////////////
				CustomData item1 = new CustomData();
				String rStr = null;
				switch(reqCode) {
					case MENU_infoKaisou:							//539				//537;情報付き曲名リスト書き込み中
						rStr = (String) ItemAL.get(i).get("img");
						dbMsg +=",img=" +rStr ;///////////////////////////////////////////////////////////////////////////////////////////
						item1.setimageUrl(rStr);
						rStr =  (String) ItemAL.get(i).get("main");
						dbMsg +=",main=" + rStr ;///////////////////////////////////////////////////////////////////////////////////////////
						item1.setTextData(rStr);
						rStr =  (String) ItemAL.get(i).get("sub");
						dbMsg +=",sub=" +rStr ;///////////////////////////////////////////////////////////////////////////////////////////
						if(rStr != null){
							if(! rStr.equals("") ){
								item1.setSubData(rStr);
							}
						}
						rStr =  (String) ItemAL.get(i).get("DATA");
						dbMsg +=",DATA=" +rStr ;///////////////////////////////////////////////////////////////////////////////////////////
						if(rStr != null){
							if(! rStr.equals("") ){
								item1.setDataUri(rStr);
							}
						}
						break;
					case MyConstants.v_play_list:			//198
						String title = (String) ItemAL.get(i).get("title");
						dbMsg +=",title=" + title ;///////////////////////////////////////////////////////////////////////////////////////////
						item1.setTextData(title);
////						rStr = (String) ItemAL.get(i).get("img");
////						dbMsg +=",img=" +rStr ;///////////////////////////////////////////////////////////////////////////////////////////
////						item1.setimageUrl(rStr);
//						rStr =  "[" + (String) ItemAL.get(i).get("_id") + "]";
//						rStr +=  (String) ItemAL.get(i).get("date_modified") + getResources().getString(R.string.comon_kousinn);
						rStr = "[" + (String) objMap.get("_id") + "]";
						rStr += sdf.format(new Date(Long.valueOf((String) ItemAL.get(i).get("date_modified"))*1000))  + getResources().getString(R.string.comon_kousinn);
						dbMsg +=",_data=" +(String) ItemAL.get(i).get("_data") ;
						long lineCount =0;
						if( title.equals(getResources().getString(R.string.listmei_zemkyoku))) {        // ;		// 全曲リスト</string>
							if(Zenkyoku_db != null){
								if( Zenkyoku_db.isOpen()){
									Zenkyoku_db.close();
									dbMsg +=  ">isOpen>" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
								}
							}
							String fn = getString(R.string.zenkyoku_file);			//全曲リスト名
							dbMsg +=  ",fn=" + fn;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
							zenkyokuHelper = new ZenkyokuHelper(MuList.this , fn);		//全曲リストの定義ファイル		.
							Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
							dbMsg +=  ">isOpen>" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
							dbMsg +=  ",getPageSize=" + Zenkyoku_db.getPageSize() + "件、" ;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
							lineCount = Zenkyoku_db.getPageSize();
						}else{
							// ファイルパス
							Path path = Paths.get((String) ItemAL.get(i).get("_data"));
							// ファイルの行数
							lineCount = Files.lines(path).count()-1;
						}
						rStr += " " +lineCount+ getResources().getString(R.string.comon_kyoku);
						dbMsg +=",sub=" +rStr ;///////////////////////////////////////////////////////////////////////////////////////////
						if(rStr != null){
							if(! rStr.equals("") ){
								item1.setSubData(rStr);
							}
						}
						rStr =  (String) ItemAL.get(i).get("_data");
						dbMsg +=",_data=" +rStr ;///////////////////////////////////////////////////////////////////////////////////////////
						if(rStr != null){
							if(! rStr.equals("") ){
								item1.setDataUri(rStr);
							}
						}
						nowList = getResources().getString(R.string.listmei_list_all);
						break;
					default:
						rStr = (String) ItemAL.get(i).get("img");
						dbMsg +=",img=" +rStr ;
						item1.setimageUrl(rStr);
						String titleStr =  (String) ItemAL.get(i).get("main");
						if( titleStr.equals(senntakuItem) ){
							sPosition = i;
						}
						item1.setTextData((String) ItemAL.get(i).get("main"));
						dbMsg +=")" + ItemAL.get(i).get("main") ;///////////////////////////////////////////////////////////////////////////////////////////

						switch(reqCode) {
						case MyConstants.v_artist:					//195	...334
								break;
						case MyConstants.v_alubum:					//196	...340
							MuList.this.albumName = titleStr;
							break;
						case MyConstants.v_titol:					//197
							if(i<9){
								rStr = "0"+ ( i + 1 ) + ";" ;
							}else{
								rStr = ( i + 1 ) + ";";
							}
							item1.setNom(rStr);
							default:
								break;
						}
						rStr =  (String) ItemAL.get(i).get("sub");
						dbMsg +=",sub=" +rStr ;///////////////////////////////////////////////////////////////////////////////////////////
						if(rStr != null){
							if(! rStr.equals("") ){
								item1.setSubData(rStr);
							}
						}
						rStr =  (String) ItemAL.get(i).get("DATA");
						dbMsg +=",sub=" +rStr ;///////////////////////////////////////////////////////////////////////////////////////////
						if(rStr != null){
							if(! rStr.equals("") ){
								item1.setDataUri(rStr);
							}
						}
					break;
				}
				isList.add(item1);
//				dbMsg +=">>" +isList.size() ;
			}      // for(int i=0;i<ItemAL.size();i++){
			dbMsg += "最終：" + isList.size() +"件"+isList.get(isList.size()-1);

			lvID.setAdapter(new CustomAdapter(this, R.layout.custom_item_layout, isList));			//getApplicationContext()	/this ?	R.layout.custom_item_layout

			lvID.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent,View view, int position, long id) {
					final String TAG = "onItemClick";
					String dbMsg =  "[MuList・setHeadImgList]";
					try{
						dbMsg +=  ",position="+position + ",id="+id;
						myLog(TAG, dbMsg);
						listClick( parent, view, position, id);			//共有：クリックの処理
					}catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}
			});

			lvID.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){	// 項目が長押しクリックされた時のハンドラ
				// リスナーを登録する thisを引数にできない //☆キャストの方法　(OnItemLongClickListener) this
			//	@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {	// 長押しクリックされた時の処理を記述
					final String TAG = "onItemLongClick[setHeadImgList]";
					String dbMsg =  "[MuList・setHeadImgList]";
					dbMsg += ORGUT.nowTime(true,true,true);
					try{
						dbMsg += ",position=" + position + ",id=" + id;
						myLog(TAG, dbMsg);
						listLongClick( parent, view, position, id);		//共有：ロングクリックの処理
					}catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
					return false;
				}
				//このブロックでToast.makeTextは使えない
			});		//onItemLongClick

			dbMsg +=",nowList=" + nowList;///あ;12354～65437,;8,は;Shift;13
			if( nowList.equals(getResources().getString(R.string.listmei_zemkyoku))){		// ;		// 全曲リスト</string>
				dbMsg +=",itemStr=" + itemStr;
				dbMsg +="(" + sPosition + ")";
				lvID.setSelection(sPosition);
				lvID.setFocusable(true);
				lvID.setFocusableInTouchMode(true);
				lvID.requestFocus();
			}else if( nowList.equals(getResources().getString(R.string.listmei_list_all))){
				mainHTF.setText(nowList);					//ヘッダーのメインテキスト表示枠
				subHTF.setText(ItemAL.size() + getResources().getString(R.string.comon_ken));					//ヘッダーのサブテキスト表示枠
			}else{
				mainHTF.setText(nowList);					//ヘッダーのメインテキスト表示枠
				subHTF.setText(ItemAL.size() + getResources().getString(R.string.comon_kyoku));					//ヘッダーのサブテキスト表示枠

				dbMsg += ",mIndex= " + mIndex;
				lvID.setSelection(mIndex);
			}

			if(reqCode == MENU_MUSCK_PLIST ||
					sousalistName.equals(getResources().getString(R.string.playlist_namae_saikinsisei)) ||			//最近再生
					sousalistName.equals(getResources().getString(R.string.playlist_namae_randam)) 			//="">ランダム再生</string>
					){			//R.id.plistDPTF
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public class CustomAdapter extends ArrayAdapter<CustomData>{				//ArrayAdapter<CustomData>			eListItem
		 private LayoutInflater mFactory;
		 private int mItemLayoutResource;

		 public CustomAdapter(Context context, int textViewResourceId, List<CustomData> objects) {		//(Context context, int textViewResourceId, List<CustomData> objects)
			super(context, textViewResourceId, objects);
			final String TAG = "CustomAdapter";
			 String dbMsg = "";
			try{
				mFactory = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				dbMsg +=  "mFactory=" + mFactory;
				mItemLayoutResource = textViewResourceId;											//Custom View
				dbMsg += ",mItemLayoutResource=" + mItemLayoutResource;
				myLog(TAG, dbMsg);
			}catch (Exception e) {
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {						//各行の書き込み
			final ItemLayout view;
			final String TAG = "getView";
			String dbMsg = "[MuList.CustomAdapter]";
//			try{
				dbMsg += "reqCode="+  reqCode + ";"+ position + ")";///////////////////////////////////////////////////////////////////////////////////////////
				dbMsg += ",convertView= "+ convertView;///////////////////////////////////////////////////////////////////////////////////////////
				if (convertView == null) {			// Viewがなかったら生成
					dbMsg += ",mFactory= "+ mFactory;///////////////////////////////////////////////////////////////////////////////////////////
					dbMsg += ",mItemLayoutResource= "+ mItemLayoutResource;/////////////////////////////////////////////////////////////////////////////////////////
					view = (ItemLayout) mFactory.inflate(mItemLayoutResource, null);					//mFactory.inflate(mItemLayoutResource, null);
				} else {
					view = (ItemLayout) convertView;
				}
				dbMsg += ">view> "+ view ;///////////////////////////////////////////////////////////////////////////////////////////
				view.bindView(getItem(position) , reqCode);			//view.bindView(getItem(position));		 bindView(MuList.eListItem) は引数 (CustomData) に適用できません
//			myLog(TAG, dbMsg);
//		}catch (Exception e) {
//			myErrorLog(TAG ,  dbMsg + "で" + e);
//		}
			return view;				//convertView
		 }

/**
 *
 * http://qiita.com/yu_eguchi/items/65311af1c9fc0bff0cb0
 * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
 * */
		private  class ThumbnailTask extends AsyncTask {					//static?
			private int mPosition;
			private ViewHolder mHolder;
			private String album_art;

			public ThumbnailTask(int position, ViewHolder holder ) {
				mPosition = position;
				mHolder = holder;
			}

			@Override
			protected String doInBackground(Object... params) {
				final String TAG = "doInBackground";
				String dbMsg = "[MuList.CustomAdapter]";
				try{
					String album_art = (String) params[0] ;
					dbMsg +=  "album_art="+ album_art;///////////////////////////////////////////////////////////////////////////////////////////
					String rPass = MuList.this.ORGUT.setAlbumArt( album_art ,  mHolder.thumbnail ,  45 , 45 , MuList.this , sucssesPass);		//指定したイメージビューに指定したURiのファイルを表示させる
					dbMsg += ",rPass="+ rPass;///////////////////////////////////////////////////////////////////////////////////////////
					if( rPass != null ){
						File SPF = new File(rPass);
						 MuList.this.sucssesPass = SPF.getPath();			//実際に読み出せたアルバムアートのパス
						dbMsg += ">>sucssesPass=" + MuList.this.sucssesPass;
					}
//					myLog(TAG, dbMsg);
				}catch (Exception e) {
					myErrorLog(TAG ,  dbMsg + "で" + e);
				}
				return sucssesPass;
			}

//			@Override
			protected void onPostExecute() {
				final String TAG = "onPostExecute";
				String dbMsg = "[MuList.CustomAdapter]";
				try{
					dbMsg +=  "mHolder.position="+ mHolder.position +  ",mPosition="+ mPosition;///////////////////////////////////////////////////////////////////////////////////////////
					if (mHolder.position == mPosition) {
//						dbMsg +=  "album_art="+ this.album_art;///////////////////////////////////////////////////////////////////////////////////////////
//						String rPass = MuList.this.ORGUT.setAlbumArt( this.album_art ,  mHolder.thumbnail ,  45 , 45 , MuList.this , sucssesPass);		//指定したイメージビューに指定したURiのファイルを表示させる
//						dbMsg += ",rPass="+ rPass;///////////////////////////////////////////////////////////////////////////////////////////
//						if( rPass != null ){
//							File SPF = new File(rPass);
//							 MuList.this.sucssesPass = SPF.getPath();			//実際に読み出せたアルバムアートのパス
//							dbMsg += ">>sucssesPass=" + MuList.this.sucssesPass;
//						}
					}
					myLog(TAG, dbMsg);
				}catch (Exception e) {
					myErrorLog(TAG ,  dbMsg + "で" + e);
				}
			}
		}
	}

	private static class ViewHolder {				//？
		public ImageView thumbnail;
		public int position;
	}

//プレイリスト////////////////////////////////////////////////////////////////////////////////////////////////////拡張リスト//
	public Map<Integer, TreeEntry> expandMap = new HashMap();			//expandMap.get(depth).collapse();ではメソッド collapse() は型 Object で未定義	>>	<Integer, TreeEntry> を追加
	public TreeEntry artistLayer;
	public TreeEntry albumLayer;
	public TreeEntry titolLayer;
	public MyTreeAdapter treeAdapter;

	/** 多階層リストの作成テスト */
	public void makeKaisouList() {
		final String TAG = "makeKaisouList[MuList]";
		String dbMsg = "";
		try{
			MuList.this.plAL = new ArrayList<Map<String, Object>>();
			MuList.this.objMap = new HashMap<String, Object>();
			MuList.this.objMap.put("album_artist" ,"アーティスト1" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.ARTIST ,"1-1" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.ALBUM ,"一枚目" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.TITLE ,"1曲目(01)" );
			MuList.this.plAL.add( objMap);
			MuList.this.objMap = new HashMap<String, Object>();
			MuList.this.objMap.put("album_artist" ,"アーティスト1" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.ARTIST ,"1-1" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.ALBUM ,"一枚目" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.TITLE ,"2曲目(02)" );
			MuList.this.plAL.add( objMap);
			MuList.this.objMap = new HashMap<String, Object>();
			MuList.this.objMap.put("album_artist" ,"アーティスト1" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.ARTIST ,"1-1" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.ALBUM ,"２枚目" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.TITLE ,"1曲目(03)" );
			MuList.this.plAL.add( objMap);
			MuList.this.objMap = new HashMap<String, Object>();
			MuList.this.objMap.put("album_artist" ,"アーティスト2" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.ARTIST ,"2-1" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.ALBUM ,"一枚目" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.TITLE ,"1曲目(04)" );
			MuList.this.plAL.add( objMap);
			MuList.this.objMap = new HashMap<String, Object>();
			MuList.this.objMap.put("album_artist" ,"アーティスト2" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.ARTIST ,"2-1" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.ALBUM ,"一枚目" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.TITLE ,"2曲目(05)" );
			MuList.this.plAL.add( objMap);
			MuList.this.objMap = new HashMap<String, Object>();
			MuList.this.objMap.put("album_artist" ,"アーティスト2" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.ARTIST ,"2-1" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.ALBUM ,"一枚目" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.TITLE ,"3曲目(06)" );
			MuList.this.plAL.add( objMap);
			MuList.this.objMap = new HashMap<String, Object>();
			MuList.this.objMap.put("album_artist" ,"アーティスト2" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.ARTIST ,"2-1" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.ALBUM ,"２枚目" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.TITLE ,"1曲目(07)" );
			MuList.this.plAL.add( objMap);
			MuList.this.objMap = new HashMap<String, Object>();
			MuList.this.objMap.put("album_artist" ,"アーティスト2" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.ARTIST ,"2-1" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.ALBUM ,"２枚目" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.TITLE ,"2曲目(08)" );
			MuList.this.plAL.add( objMap);
			MuList.this.objMap = new HashMap<String, Object>();
			MuList.this.objMap.put("album_artist" ,"アーティスト2" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.ARTIST ,"2-1" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.ALBUM ,"２枚目" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.TITLE ,"3曲目(09)" );
			MuList.this.plAL.add( objMap);
			MuList.this.objMap = new HashMap<String, Object>();
			MuList.this.objMap.put("album_artist" ,"アーティスト2" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.ARTIST ,"2-1" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.ALBUM ,"２枚目" );
			MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.TITLE ,"4曲目(10)" );
			MuList.this.plAL.add( objMap);
			dbMsg +=  "plAL=" + plAL.size() +"件";
	//		CreatePLListEnd();	//プレイリストの内容取得			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}

//プレイリスト操作///////////////////////////////////////////////////////////////////////////////////////////////////プレイリスト//
	public int sousalistID = nowList_id;				//操作対象リストID
	public String sousalistName = null;		//操作対象リスト名 ;セレクタの設定に使用
	public String sousalist_data = null;		//操作対象リストのUrl
	public Uri sousalistUri;		//操作対象リストUri
	public int sousaRecordId;		//操作対象レコードのID
	public int sousaRecordPlayOrder;		//操作対象レコードのID
	public String sousaRecordUrl;		//操作対象レコードのUrl
	public String sousaRecordName;	//操作対象レコード名
	public String sousaAlbumArtist;	//操作対象のアルバムアーティスト名
	public String tuikaItemName;	//操作対象レコードのID
	public int listKyokuSuu;		//リストにある曲数
	public int idousaki;			//リスト上の移動先
	public String tuiukaItemeFn;
	public int audio_id;
	public String tuikaSakiListName;	//編集中のリスト名
	public int tuikaSakiListID;		//編集中のリストID
	public Uri tuikaSakilistUri;		//編集中リストUri
	public String saikintuikaDBname;	//最近追加のリストファイル名
	public String saikintuikaTablename;	//最近追加のリストテーブル名

	public int sousaSuu = 0;				//指定数値
	public Cursor playLists;
	public String itemStr;
	public int spSyokiPosition = 0;		//プレイリスト選択の初期位置
	public int artistPosition = -1;		//選択させるアーティスト
	public int alubmPosition = -1;		//選択させるアルバム
	public int titolePosition = -1;		//選択させるタイトル
	public int artistCount = 0;		//アーティストのリスト上のポジション
	public int alubmCount = 0;			//アルバムのリスト上のポジション
	public int titoleCount = 0;		//タイトルのリスト上のポジション
	public int artistID;
	public int albumID;
	public int layerCode;
	public int listType = listType_plane;
	public boolean plef_tankaisou = false;									//階層リストを単階層に変更
	static final int listType_plane = 0;							//情報なし
	static final int listType_sub = listType_plane + 1;			//sub情報付き
	static final int listType_info = listType_sub + 1;			//sub+先頭画像情報付き
	static final int listType_2ly = listType_info + 1;			//アーティストの単階層リストとalbumとtitolの２階層
	static final int listType_2ly2 = listType_2ly + 1;			//albumとtitolの２階層
	static final int listType_3ly = listType_2ly2 + 1;			//albumとtitolの２階層リスト
	static final int lyer_artist = 10;							//アーティスト
	static final int lyer_album = lyer_artist + 10;			//アルバム
	static final int lyer_titol = lyer_album + 10;				//タイトル

/**
 * プレイリスト一覧とリスナーをスピナーに登録
 * 指定されたプレイリスト名を選択
 * 変更されたプレイリストの情報にグローバル変数を更新する
 * 	int sousalistID = -1;				//操作対象リストID
 *	String sousalistName = null;		//操作対象リスト名
 *	String sousalist_data = null;		//操作対象リストのUrl
 *	Uri sousalistUri;		//操作対象リストUri
 * */
	@SuppressWarnings("deprecation")
	public void makePlayListSPN(String sousalistName) {		//プレイリストスピナーを作成する
		final String TAG = "makePlayListSPN";
		String dbMsg = "";
		try{
			if( plNameSL == null ){
				plNameSL = getPList();		//プレイリストを取得する
			}
			dbMsg +=  "plNameSL = " + plNameSL.size() +"件";
			dbMsg ="[nowList_id="+nowList_id+"]" + nowList;
			dbMsg ="("+spSyokiPosition+")";
			if(nowList == null){
				spSyokiPosition = 0;		//プレイリスト選択の初期位置
			}else{
				spSyokiPosition = ORGUT.retArreyIndex(MuList.this.plNameAL , MediaStore.Audio.Playlists.NAME , nowList);
			}
			dbMsg +=">>"+spSyokiPosition+"番目)";
			String[] spinnerItems = plNameSL.toArray(new String[plNameSL.size()]);
			ArrayAdapter<String> sAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerItems );			//☆getApplicationContext()だと文字が白く抜ける
			sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			pl_sp.setAdapter(sAdapter);	// spinner に adapter をセット
			if(0< plNameSL.size()){
				dbMsg += ",選択させるのは；sousalistName=" + sousalistName;

				int ePosition = plNameSL.indexOf(sousalistName);
				dbMsg += ",zenkyokuAri=" + zenkyokuAri;
				dbMsg += ",ePosition=" + ePosition;
				if(sousalistName.equals(getResources().getString(R.string.listmei_zemkyoku)) ||
						nowList.equals(getResources().getString(R.string.playlist_namae_request))		//リクエストリスト
						){					//全曲リスト有り	 ,	// 全曲リスト
					if( zenkyokuAri){					//全曲リスト有り
						pl_sp.setSelection(0 , false);								//☆falseで勝手に動作させない
					}else{
						pl_sp.setSelection(plNameSL.size()-2 , false);				//☆falseで勝手に動作させない	onItemSelectedを呼ぶanimationを回避	☆setOnItemSelectedListenerの前に書く事
					}
				}else{
					pl_sp.setSelection(ePosition , false);								//☆falseで勝手に動作させない
				}
			}

			pl_sp.setOnItemSelectedListener(new OnItemSelectedListener() {	// リスナーを登録
				public void onItemSelected(AdapterView<?> parent, View viw, int arg2, long arg3) {	//　アイテムが選択された時
					final String TAG = "onItemSelected";
					String dbMsg = "[MuList.makePlayListSPN]";
					try{
						int position = arg2;
						dbMsg +=  "position = " + position;
						dbMsg +=",plNameAL=" + MuList.this.plNameAL.size() +"件" ;
						dbMsg +=",positionの_idは=" + MuList.this.plNameAL.get(position).get("_id") ;
						MuList.this.spSyokiPosition = position;
						dbMsg += ",arg3 = " + arg3;
						String maeList = MuList.this.sousalistName;
						Spinner spinner = (Spinner) parent;
						MuList.this.sousalistName = (String) spinner.getSelectedItem();
						dbMsg += ",nowList[" + MuList.this.nowList_id + "]" + MuList.this.nowList + ",maeList = " + maeList;
						MuList.this.sousalistID = Integer.parseInt( String.valueOf( MuList.this.plNameAL.get(position).get("_id")));
						dbMsg +="sousalist[=" + MuList.this.sousalistID +"]" + MuList.this.sousalistName;
						String pdMessage = MuList.this.sousalistName;
						if( ! MuList.this.sousalistName.equals(getResources().getString(R.string.playlist_namae_repeat)) ){				// リピート再生でなければ
							rp_pp = false;
							pp_start = 0;			//リピート区間開始点
							pp_end = 0;				//リピート区間終了点
							rp_pp = false;			//2点間リピート中
							myEditor.putBoolean("pref_nitenkan", rp_pp);
							myEditor.putString( "pref_nitenkan_start", String.valueOf(pp_start));
							myEditor.putString( "pref_nitenkan_end", String.valueOf(pp_end));
							Boolean kakikomi = myEditor.commit();	// データの保存
						}
						MuList.this.sousalistID = Integer.parseInt( (String) MuList.this.plNameAL.get(position).get("_id"));
						dbMsg +="[=" + MuList.this.sousalistID;
						MuList.this.sousalistUri = Uri.parse(String.valueOf(MuList.this.plNameAL.get(position).get("_data")) ) ;
						dbMsg += "]" + MuList.this.sousalistUri.toString() ;
						if( MuList.this.sousalistName.equals(getResources().getString(R.string.listmei_zemkyoku)) ){				// 全曲リスト
							dbMsg +=" ,保存場所= " + sousalist_data ;
							dbMsg +=" ,yobidashiItem= " + yobidashiItem ;
							dbMsg +=" ,maeList= " + maeList ;         						//20190506
							if( yobidashiItem == MyConstants.v_artist  || ! maeList.equals(MuList.this.sousalistName)){		//プレイヤー画面でタップされたアイテム
//								dbMsg +=" ,cursorA= " + cursorA;
//								if(cursorA != null){							//既に読み込み中に誤動作したら読み込み処理に入らない
//									dbMsg +=" ,isClosed= " +  cursorA.isClosed() ;
//									if( cursorA.isClosed()){
//										artistList_yomikomi();
//									}
//								}else{
									artistList_yomikomi();
//								}
							}
//						}else if( MuList.this.sousalistName.equals(getResources().getString(R.string.playlist_namae_saikintuika)) ){		//最近追加

						}else if( MuList.this.sousalistName.equals(getResources().getString(R.string.playlist_namae_saikinsisei)) ){		//最近再生
//							MuList.this.sousalistID = Integer.parseInt( (String) MuList.this.plNameAL.get(position).get("_id"));
//							dbMsg +="[=" + MuList.this.sousalistID;
//							MuList.this.sousalistUri = Uri.parse(String.valueOf(MuList.this.plNameAL.get(position).get("_data")) ) ;
//							dbMsg += "]" + MuList.this.sousalistUri.toString() ;
							saikin_sisei_jyunbi( MuList.this.sousalistID );				//最近再生された楽曲
						} else if( sousalistName.equals(getResources().getString(R.string.playlist_namae_repeat)) ){					//リピート再生
							dbMsg += ",repeatType="+repeatType;
							if( repeatType == MyConstants.rp_artist ){				//リピート再生の種類	2131558548
								dbMsg += ",repeatArtist="+repeatArtist ;
								plAlbumTitol( MuList.this.sousalistID , repeatArtist);		//指定したプレイリストから特定アーティストのアルバムとタイトル内容取得
							}else{
								CreatePLList(Long.valueOf(MuList.this.sousalistID) , pdMessage);		//プレイリストの内容取得		 MuList.this.sousalist_data,
							}
							dbMsg += ">listType="+listType;
						}else{
							headImgIV.setVisibility(View.GONE);								 // 表示枠を消す
							MuList.this.sousalist_data = String.valueOf( MuList.this.plNameAL.get(position).get("_data"));	//[1/5] /storage/sdcard0/Playlists/★お気に入りプレイリスト
							dbMsg +=" ,保存場所= " + sousalist_data ;
							String added = String.valueOf( MuList.this.plNameAL.get(position).get("date_added"));			//[3/5]=1376892219
							dbMsg +=",added=" + added ;
							String modified = String.valueOf( MuList.this.plNameAL.get(position).get("date_modified"));		//再生中のプレイリストの詳細//[4/5]=null
							dbMsg +=" ,modified= " + modified ;
							if (modified == null){
							}else if (! modified.equals("null")){	// modified= null；java.lang.NumberFormatException: Invalid long: "null"
								modified =  sdf.format(new Date(Long.valueOf(modified)*1000));
								dbMsg +=">>" + modified ;
								MuList.this.nowListSub = getResources().getString(R.string.pref_file_saisinn) + " " + modified;	//="">最新更新日</string>
							}else if(added == null){
							}else if(! added.equals("null")){
								added =  sdf.format(new Date(Long.valueOf(added)*1000));
								dbMsg +=">>" + added ;
								nowListSub = getResources().getString(R.string.comon_sakusei) + " "+ added;			//作成</string>
							}
							dbMsg +=" ; " + nowListSub ;
							pdMessage = pdMessage + "\n\n"+ nowListSub;
							CreatePLList(Long.valueOf(MuList.this.sousalistID) , pdMessage);									//プレイリストの内容取得
						}
//						pref_zenkyoku_list_id = getAllSongItems();
//						dbMsg += " [全曲リスト； " + pref_zenkyoku_list_id + "]";/////////////////////////////////////
						if(! MuList.this.sousalistName.equals(getResources().getString(R.string.playlist_namae_saikintuika)) ) {        //最近追加
							MuList.this.treeAdapter = null;
						}
						myLog(TAG, dbMsg);
					}catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}
				public void onNothingSelected(AdapterView<?> parent) {	//　アイテムが選択されなかった
				}
			});

			dbMsg += ",reqCode="+ reqCode ;////////
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		/*tools:listitem="@android:layout/simple_selectable_list_item" */
	}

	/**
	 * 書き損ねリストの削除
	 * */
	public void delCheckList() {
		final String TAG = "delCheckList";
		String dbMsg = "";
		try{
			dbMsg +=  "プレイリスト用簡易リスト="+plNameSL;
			Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
			String[] columns = null;				//{ idKey, nameKey };
			String c_selection = null;				//MediaStore.Audio.Playlists.NAME +" = ? ";
			String[] c_selectionArgs= null;			//{ String.valueOf(String.valueOf(getResources().getString(R.string.listmei_zemkyoku))) };		// 全曲リスト
			playLists = this.getContentResolver().query(uri, columns, c_selection, c_selectionArgs, null);
			dbMsg +=  "全件="+playLists.getCount() + "件";
			if(playLists.moveToFirst()){
				do{
					@SuppressLint("Range") String listID  = playLists.getString(playLists.getColumnIndex(MediaStore.Audio.Playlists._ID));
					dbMsg += "\n["+ listID + "]";
					@SuppressLint("Range") String listName  = playLists.getString(playLists.getColumnIndex(MediaStore.Audio.Playlists.NAME));
					dbMsg += listName;
					Uri uri2 = MediaStore.Audio.Playlists.Members.getContentUri("external", Long.valueOf(listID));
					Cursor cursol = this.getContentResolver().query(uri2, columns, c_selection, c_selectionArgs, null);
					int count  = cursol.getCount();
					dbMsg += ";count=" +count +"件";
					cursol.close();
				}while(playLists.moveToNext());
			}
			playLists.close();
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * content providers に登録してある playlist をリストアップする
	 * plNameSL にデータを格納して
	 * nameを　pl_sp スピナーに表示する
	 * プリファレンスの定例リストIdを更新する
	 * */
	public List<String> getPList() throws IOException {		//プレイリストを取得する
		final String TAG = "getPList";
		String dbMsg = "";
		try{
			int koumoku = 0;
			///定例名称の実在確認//////////////////////////////////////////////////
			String[] kennsakuMei = {
					getResources().getString(R.string.listmei_zemkyoku),	// 全曲リスト
					getResources().getString(R.string.playlist_namae_saikintuika),			//最近追加
					getResources().getString(R.string.playlist_namae_saikinsisei)            //最近再生
			};
			dbMsg +=  "プレイリスト用簡易リスト="+plNameSL;
			Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;			//content providers に登録してある playlist
			dbMsg +=  ",uri = " + uri;
			String[] columns = null;				//{ idKey, nameKey };
			Cursor playLists  = null;
			if(plNameSL == null){
				plNameSL =  new ArrayList<String>();
				dbMsg +=">plNameSL>"+plNameSL;
				plNameAL = new ArrayList<Map<String, Object>>();
			} else {
				plNameSL.clear();
				plNameAL.clear();
			}
			objMap = new HashMap<String, Object>();
			String cVal = getResources().getString(R.string.listmei_zemkyoku);		// 全曲リスト</string>
			int listId = pref_zenkyoku_list_id;
			// 全playlist 取得
			for(String listName : kennsakuMei){											//定例リストが有る場合だけ追記
				dbMsg += "\n読み出すリスト=" +  listName;
//				if ( listName.equals(getResources().getString(R.string.listmei_zemkyoku)) ) {
//					objMap.put("_id" , 0);
//					objMap.put("name" , listName);
//					objMap.put("_data" , null);
//					objMap.put("date_added" , 0);
//					objMap.put("date_modified" , null);
//					plNameAL.add(objMap);
//					plNameSL.add(listName);
////				}else if(listName.equals(String.valueOf(getResources().getString(R.string.playlist_namae_saikintuika)))){
////					if(newSongHelper == null){
////						newSongHelper = new ZenkyokuHelper(MuList.this , saikintuikaDBname);		//周防会作成時はここでonCreatが走る
////					}
////					if(newSongDb == null){
////						newSongDb = newSongHelper.getReadableDatabase();
////					}
//				}else {
					String c_selection = MediaStore.Audio.Playlists.NAME + " = ? ";
					String[] c_selectionArgs = {listName};        //⑥引数groupByには、groupBy句を指定します。
					playLists = this.getContentResolver().query(uri , columns , c_selection , c_selectionArgs , null);
					koumoku = playLists.getColumnCount();
					if ( playLists.moveToFirst() ) {
						objMap = new HashMap< String, Object >();

						// 全データ取得　//////////////////////////////
						for ( int i = 0 ; i < koumoku ; i++ ) {
//							dbMsg += "\n[" + i + "/" + koumoku + "]";
							String cName = playLists.getColumnName(i);
//							dbMsg += cName;
							cVal = playLists.getString(i);
							if ( cVal != null ) {
								cVal = cVal;
							}
//							dbMsg += "=" + cVal;
							objMap.put(cName , cVal);
							if ( cName.equals(MediaStore.Audio.Playlists.NAME) ) {			//"name"
								plNameSL.add(cVal);
							}else if( cName.equals(MediaStore.Audio.Playlists._ID) ) {				//"_id"
								listId = Integer.parseInt(cVal);
							}
						}
						plNameAL.add(objMap);
						// プリファレンス　////////////////////////////全データ取得//
						if(listName.equals(getResources().getString(R.string.listmei_zemkyoku))){
							dbMsg += ",pref_zenkyoku_list_id=" + pref_zenkyoku_list_id;
							if(listId != pref_zenkyoku_list_id){			// 全曲リスト
								setPrefInt("pref_zenkyoku_list_id", listId ,MuList.this);
							}
						}else if(listName.equals(getResources().getString(R.string.playlist_namae_saikintuika))){
							dbMsg += ",saikintuika_list_id=" + saikintuika_list_id;
							if(listId != saikintuika_list_id){			// 最近追加
								setPrefInt("saikintuika_list_id", listId ,MuList.this);
							}
						}else if(listName.equals(getResources().getString(R.string.playlist_namae_saikinsisei))){
							dbMsg += ",saikinsisei_list_id=" + saikinsisei_list_id;
							if(listId != saikinsisei_list_id){			// 最近再生
								setPrefInt("saikinsisei_list_id", listId ,MuList.this);
							}
						}
//					}else if ( listName.equals(getResources().getString(R.string.listmei_zemkyoku)) ) {
//						objMap.put("_id" , 0);
//						objMap.put("name" , listName);
//						objMap.put("_data" , null);
//						objMap.put("date_added" , 0);
//						objMap.put("date_modified" , null);
//						plNameAL.add(objMap);
//						plNameSL.add(listName);
					}

				}
//			}
				//定例以外に作成されたリストの検索////////////////////////////////////////////////定例名称の実在確認///
			String c_selection =null;
			String[] c_selectionArgs=null;
			playLists = this.getContentResolver().query(uri, columns, c_selection, c_selectionArgs, null);
			dbMsg += "\nplayLists=" + playLists.getCount()+"件";
			if (playLists.moveToFirst()) {
				int rCount = playLists.getCount();
				dbMsg +=  rCount +"件" + koumoku +"項目";
				if( playLists.moveToFirst() ){
					do{
						boolean kakikomu = false;
						int rPosi = playLists.getPosition();
						dbMsg +=  "[" + rPosi +"/" + rCount +"件目]";
						@SuppressLint("Range") String lName = playLists.getString(playLists.getColumnIndex( MediaStore.Audio.Playlists.NAME));
						dbMsg += "：" + lName;
						if(lName.equals(getResources().getString(R.string.listmei_zemkyoku)) ||								//追加済みの定例リストは除く
							lName.equals(getResources().getString(R.string.playlist_namae_saikintuika)) ||					//
								   lName.equals(getResources().getString(R.string.playlist_namae_saikinsisei))
								){
						} else {
							Cursor cursor = listUMU(lName);
							if( cursor.moveToFirst() ){
								@SuppressLint("Range") long listID = Long.valueOf(String.valueOf(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID))));
								Uri kakuninntUri = MediaStore.Audio.Playlists.Members.getContentUri("external", listID );
								dbMsg += "(" + kakuninntUri+")";/////////////////////////////////////
//								String[] columns = null;			//{ idKey, nameKey };
								String c_orderBy = null;			//MediaStore.Audio.Playlists.Members.PLAY_ORDER;
								Cursor cursor2 = this.getContentResolver().query(kakuninntUri , columns, null, null, c_orderBy );
								int tCount = cursor2.getCount();
								dbMsg += tCount+ "件";
								if(0 < tCount){
									kakikomu = true;
								}
								cursor2.close();
							}
							cursor.close();
						}
						dbMsg += ",kakikomu=" + kakikomu;
						if(kakikomu){
							objMap = new HashMap<String, Object>();
							for(int i = 0 ; i < koumoku ; i++ ){
								dbMsg += "[" + i +"/" + koumoku +"]";
								String cName = playLists.getColumnName(i);
								dbMsg += cName;
								cVal = playLists.getString(i);
								if(cVal != null){
									cVal = cVal;
								}
								dbMsg += "="+cVal;
								objMap.put(cName ,cVal );
								if(cName.equals("name")){
									plNameSL.add(cVal);
									if(cVal.equals(MuList.this.nowList)){
										spSyokiPosition = plNameAL.size();		//プレイリスト選択の初期位置
									}
								}else if(cName.equals("_id")){
									if(Integer.valueOf(cVal) == MuList.this.nowList_id){
										spSyokiPosition = plNameAL.size();		//プレイリスト選択の初期位置
									}
								}
							}
							plNameAL.add( objMap);
						}
					}while(playLists.moveToNext());
				}
			}
			playLists.close();
			dbMsg += ">plNameAL>"+plNameAL.toString()+ ",reqCode="+ reqCode ;
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return plNameSL;
		//http://jp.androids.help/q17164
		//Android端末とMP3で苦労したので覚え書き	http://kato-h.cocolog-nifty.com/khweblog/2013/12/androidmp3-d572.html
		//..m3u8	ファイルの種類を「M3U8(Unicode)プレイリスト」
	}

	public Cursor rCursor;

	/** 指定したプレイリストの内容取得 */
	public void CreatePLList( long playlistId , String pdMessage){		//playlistIdで指定したMediaStore.Audio.Playlists.Membersの内容取得		String volumeName,
		final String TAG = "CreatePLList";
		String dbMsg = "";
		try{
			dbMsg += ORGUT.nowTime(true,true,true) + dbMsg;/////////////////////////////////////
			long start = System.currentTimeMillis();		// 開始時刻の取得
			dbMsg += "選択されたプレイリスト[ID="+playlistId + "]" + MuList.this.sousalistName ;
			final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
			final String[] columns = null;			//{ idKey, nameKey };
			String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
			Cursor rCursor= this.getContentResolver().query(uri, columns, null, null, c_orderBy );
//				}else{
//					treeAdapter = null;
//					Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
//					String[] columns =null;
//					String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
//					playLists = this.getContentResolver().query(uri, columns, null, null, c_orderBy );
//				}
			dbMsg += ",rCursor=" + rCursor.getCount() + "件";
			if( rCursor.moveToFirst() ){
				reqCode = MyConstants.PUPRPOSE_lIST;
				String pdTitol = getResources().getString(R.string.pref_playlist) +"" + getResources().getString(R.string.common_yomitori);				//読み込み
				Util UTIL = new Util();
				UTIL.dBaceColumnCheck(rCursor ,0);
				MuList.this.plAL = new ArrayList<Map<String, Object>>();
				MuList.this.plAL.clear();
				MuList.this.plSL =  new ArrayList<String>();				//プレイリスト用簡易リスト
				MuList.this.plSL.clear();
				MuList.this.saisei_fnameList = new ArrayList<String>();		//uri配列
				MuList.this.listKyokuSuu = rCursor.getCount();		//リストにある曲数
				dbMsg += ","+MuList.this.nowListSub;
				if( MuList.this.nowListSub != null ){
					MuList.this.nowListSub = MuList.this.nowListSub + "  " + MuList.this.listKyokuSuu + getResources().getString(R.string.pp_kyoku);
				} else {
					MuList.this.nowListSub = MuList.this.listKyokuSuu + getResources().getString(R.string.pp_kyoku);
				}
				dbMsg += "→"+MuList.this.nowListSub;
				dbMsg += ",reqCode="+reqCode;
				pdMessage= sousalistName;
				int maxVal = rCursor.getCount();
				plTask.execute(reqCode,rCursor,pdTitol,pdMessage,maxVal);
			}else{
				dbMsg += "MediaStore.Audio.Playlists以外";
				if(nowList.equals(getResources().getString(R.string.playlist_namae_request))){					// リクエスト</string>
					pl_sp.setSelection(0 , false);								//☆falseで勝手に動作させない
				}else if(playLists.getCount() < 1){
					AlertDialog.Builder Dlg = new AlertDialog.Builder(this);			// リスト表示用のアラートダイアログ
					Dlg.setTitle(getResources().getString(R.string.pref_playlist));		// プレイリスト</string>
					Dlg.setMessage(getResources().getString(R.string.pl_nasi_meg));		//ご指定のプレイリストは曲が登録されていないか、読み込めませんでした。
					Dlg.setNegativeButton(getResources().getString(R.string.comon_kakuninn),	//確認
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					Dlg.setCancelable(false);
					Dlg.create().show();			// 表示
				}
			}
			myLog(TAG, dbMsg);
		}catch(IllegalArgumentException e){
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 *  Cursorで渡されたプレイリストレコードから内容を取得し、plALに書き込む */
	public Cursor CreatePLListBody(Cursor playLists) throws IOException {		//プレイリストの内容取得
		final String TAG = "CreatePLListBody";
		String dbMsg = "";
		try{
			int rPosi = playLists.getPosition();
			dbMsg= "[" + rPosi +"/" + playLists.getCount() +"曲]";		//MuList.this.rCount
			String subText = null;
			String ArtistName = null;
			String AlbumArtistName = null;
			String AlbumName = null;
			String songTitol = null;
			String Dur = null;
			MuList.this.objMap = new HashMap<String, Object>();
			for(int i = 0 ; i < playLists.getColumnCount() ; i++ ){				//MuList.this.koumoku
				String cName = playLists.getColumnName(i);
				dbMsg += "[" + i +"/" + playLists.getColumnCount() +"項目]"+ cName;
				if(
//					cName.equals(MediaStore.Audio.Playlists.Members.INSTANCE_ID) ||	//[1/66]instance_id
//					cName.equals(MediaStore.Audio.Playlists.Members.TITLE_KEY) ||	//[13/37]title_key
//					cName.equals(MediaStore.Audio.Playlists.Members.SIZE) ||			//[7/37]_size=4071748
//					cName.equals(MediaStore.Audio.Playlists.Members.IS_RINGTONE) ||	//[20/37]is_ringtone=0
//					cName.equals(MediaStore.Audio.Playlists.Members.IS_MUSIC) ||			//[21/37]is_music=1
//					cName.equals(MediaStore.Audio.Playlists.Members.IS_ALARM) ||			//[22/37]is_alarm=0
//					cName.equals(MediaStore.Audio.Playlists.Members.IS_NOTIFICATION) ||	//[23/37]is_notification=0
//					cName.equals(MediaStore.Audio.Playlists.Members.IS_PODCAST) ||			//[24/37]is_podcast=
//				 	cName.equals(MediaStore.Audio.Playlists.Members.ARTIST_KEY) ||		//
//					cName.equals(MediaStore.Audio.Playlists.Members.ALBUM_KEY) ||		//[35/37]album_key
					cName.equals(MediaStore.Audio.Playlists.Members.XMP)){		//[31/66項目]xmp=【Blob】[32/37]artist_key
					dbMsg += "は読み込めない";
				}else{
					if( cName.equals("album_artist")){		//[26/37]
						String cVal = playLists.getString(i);
						if(cVal != null){
							cVal = cVal;
						}
						dbMsg +=  "="+cVal;
						AlbumArtistName = cVal;
						MuList.this.objMap.put(cName ,cVal );
					}else if( cName.equals(MediaStore.Audio.Playlists.Members.ARTIST)){		//[33/37]artist=Santana
						String cVal = playLists.getString(i);
						if(cVal != null){
							cVal = cVal;
						}else{
							cVal = getResources().getString(R.string.bt_unknown);			//不明
						}
						dbMsg +=  "="+cVal;
						ArtistName = cVal;
						MuList.this.objMap.put(cName ,cVal );
					}else if( cName.equals(MediaStore.Audio.Playlists.Members.ALBUM)){		//[33/37]artist=Santana
						String cVal = playLists.getString(i);
						if(cVal != null){
							cVal = cVal;
						}else{
							cVal = getResources().getString(R.string.bt_unknown);			//不明
						}
						dbMsg +=  "="+cVal;
						AlbumName = cVal;
						MuList.this.objMap.put(cName ,cVal );
					}else if( cName.equals(MediaStore.Audio.Playlists.Members.TITLE)){		//[12/37]title=Just Feel Better
						String cVal = playLists.getString(i);
						if(cVal != null){
							cVal = cVal;
						}else{
							cVal = getResources().getString(R.string.bt_unknown);			//不明
						}
						MuList.this.objMap.put(cName ,cVal );
						MuList.this.objMap.put("main" ,cVal );
						dbMsg +=  "="+cVal;
						MuList.this.plSL.add(cVal);
						songTitol = cVal;
					}else if( cName.equals(MediaStore.Audio.Playlists.Members.DURATION)){	//[14/37]duration=252799>>04:12 799
						String cVal = playLists.getString(i);
						dbMsg +=  "="+cVal;
						if(cVal != null){
							cVal = cVal;
						}
						MuList.this.objMap.put(cName ,cVal );
						Dur = "["+ ORGUT.sdf_mss.format(Long.valueOf(cVal)) + "]";
						dbMsg +=  ">>"+Dur;
					}else if( cName.equals(MediaStore.Audio.Playlists.Members.DATA)){	//[5/37]_data=/storage/sdcard0/external_sd/Music/Santana/All That I Am/05 Just Feel Better.wma
						String cVal = playLists.getString(i);
						if(cVal != null){
							cVal = cVal;
						}
						MuList.this.objMap.put(cName ,cVal );
						MuList.this.saisei_fnameList.add(cVal);
						MuList.this.plSL.add(cVal);
						dbMsg +=  "="+cVal;
					}else if( cName.equals(MediaStore.Audio.Playlists.Members.TRACK)){
						String cVal = playLists.getString(i);
					//	cVal = UTIL.checKTrack( cVal);
						MuList.this.objMap.put(cName ,cVal );
					}else if( cName.equals(MediaStore.Audio.Playlists.Members.ALBUM_ID)){
						String cVal = String.valueOf(playLists.getInt(i));
						MuList.this.objMap.put(cName ,cVal );
						dbMsg +=  "="+cVal;
					}else{
						int cPosition = playLists.getColumnIndex(cName);
						dbMsg += "『" + cPosition+"』";
						String cVal ="";
						if(0<cPosition){
							int colType = playLists.getType(cPosition);
					//		dbMsg += ",Type=" + colType + ",";
							switch (colType){
								case Cursor.FIELD_TYPE_NULL:          //0
									cVal ="【null】" ;
									break;
								case Cursor.FIELD_TYPE_INTEGER:         //1
									@SuppressLint("Range") int cInt = playLists.getInt(cPosition);
									dbMsg += cInt+"【int】";
									cVal=String.valueOf(cInt);
									break;
								case Cursor.FIELD_TYPE_FLOAT:         //2
									@SuppressLint("Range") float cFlo = playLists.getFloat(cPosition);
									dbMsg += cFlo+"【float】";
									cVal=String.valueOf(cFlo);
									break;
								case Cursor.FIELD_TYPE_STRING:          //3
									cVal = playLists.getString(cPosition);
									dbMsg +=  cVal+"【String】";
									break;
								case Cursor.FIELD_TYPE_BLOB:         //4
									//@SuppressLint("Range") String cBlob = String.valueOf(cursor.getBlob(cPosition));
									cVal ="【Blob】";
									break;
								default:
									cVal = String.valueOf(playLists.getString(cPosition));
									dbMsg +=  cVal;
									break;
							}
						}
						dbMsg += "="+cVal;
						MuList.this.objMap.put(cName ,cVal );
					}
				}
			}
			dbMsg +=  ",Dur="+Dur;
			subText = ArtistName + " " +Dur;
			dbMsg +=  ",subText="+subText;
			if(subText != null){
				MuList.this.objMap.put("sub" ,subText );
			}
			if(AlbumArtistName != null){
				ArtistName = AlbumArtistName;
			}
			MuList.this.objMap.put("img" , ORGUT.retAlbumArtUri( getApplicationContext() , ArtistName , AlbumName ) );			//アルバムアートUriだけを返す
			MuList.this.plAL.add( objMap);
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg  + "で" + e);
		}
		return playLists;
	}

	/**
	 * 取得されたPlayListのメンバーを条件に合わせてリストに書き込む準備*/
	public void CreatePLListEnd() {		//プレイリストの内容取得            指定
		final String TAG = "CreatePLListEnd";
		String dbMsg = "";
		try{
//			if(playLists != null){
//				dbMsg += ",isClosed=" + playLists.isClosed();
//				if(! playLists.isClosed()){
//					//リーク対策
//					playLists.close();
//					dbMsg += ">>" + playLists.isClosed();
//				}
//			}
			int sPosition = 0;
			ArrayList<MyTreeAdapter> tList = null;				//ArrayList<MyTreeAdapter> tList = null;
			tList = new ArrayList<MyTreeAdapter>();				//tList = new ArrayList<MyTreeAdapter>()
			MuList.this.artistLayer = null;
			MuList.this.albumLayer  = null;
			MuList.this.titolLayer  = null;
			MuList.this.b_artist = "";
			MuList.this.b_album = "";
			MuList.this.artistPosition = 0;		//選択させるアーティスト
			MuList.this.alubmPosition = 0;		//選択させるアルバム
			MuList.this.titolePosition = 0;		//選択させるタイトル
			MuList.this.artistCount = 0;		//アーティストのリスト上のポジション
			MuList.this.alubmCount = 0;			//アルバムのリスト上のポジション
			MuList.this.titoleCount = 0;		//タイトルのリスト上のポジション

			MuList.this.treeAdapter = new MyTreeAdapter(tList);
			dbMsg +=  "reqCode="+reqCode;
			reqCode = MENU_infoKaisou;								//539;情報付き曲名リスト書き込み中
			int retInt = plAL.size();
			String subText = retInt + getResources().getString(R.string.comon_ken);
			dbMsg += ",retInt="+ subText + ":artist_bunnri" + pref_artist_bunnri +"人";
			dbMsg += ",sousalistName="+sousalistName ;
			dbMsg += ",単階層指定="+plef_tankaisou ;

			if( sousalistName.equals(getResources().getString(R.string.playlist_namae_saikintuika)) ){
				dbMsg += ">reqCode="+reqCode;
				listType = listType_plane;									// 0;//情報なし
				if( retInt < Integer.valueOf(pref_artist_bunnri) ){
					dbMsg += ",リストが長すぎるので" ;
					dbMsg += ">reqCode="+reqCode;
					if(pref_list_simple){											//シンプルなリスト表示（サムネールなど省略）
						listType = listType_plane;									// 0;//情報なし
					}else {
						listType = listType_info;									//sub情報付き
					}
				}
				dbMsg += ">listType="+listType;
				pl_file_name = getPrefStr( "pref_commmn_music" ,  "" , MuList.this) +File.separator;
				//myPreferences.pref_commmn_music + File.separator + MuList.this.getString(R.string.playlist_namae_saikintuika) + ".m3u";
				pl_file_name +=  MuList.this.getString(R.string.playlist_namae_saikintuika) + ".m3u8";

			} else if( sousalistName.equals(getResources().getString(R.string.playlist_namae_saikinsisei)) ||			//最近再生
						sousalistName.equals(getResources().getString(R.string.playlist_namae_randam)) 			//="">ランダム再生</string>
					){
				if(pref_list_simple){											//シンプルなリスト表示（サムネールなど省略）
					listType = listType_plane;									//listType_sub 1;sub情報付き
				}else {
					listType = listType_info;									//sub情報付き
				}
			}else if(sousalistName.equals(getResources().getString(R.string.playlist_namae_repeat)) ){			//リピート再生
				subText = creditArtistName + " ; " + albumName + " ; " + MuList.this.listKyokuSuu + getResources().getString(R.string.pp_kyoku);
			} else {													//汎用プレイリストで
				if( retInt < Integer.valueOf(pref_artist_bunnri)){		//閾値内もしくは				 || plef_tankaisou	単階層指定なら
					if(pref_list_simple){											//シンプルなリスト表示（サムネールなど省略）
						listType = listType_plane;									// 0;//情報なし
					}else {
						listType = listType_info;									//sub情報付き
					}
				}else{														//閾値を超えたら
					reqCode = listType_2ly;
					listType = listType_2ly;								//3 listType_info + 1;アーティストの単階層リストとalbumとtitolの２階層
					dbMsg +=",artistAL="+artistAL;
					if(artistAL == null){
						artistAL = new ArrayList<Map<String, Object>>();
						dbMsg +=">>"+artistAL;
					}else{
						artistAL.clear();
					}
					dbMsg +=",artistSL="+artistSL;
					if(artistSL == null){
						artistSL =  new ArrayList<String>();				//アーティストリスト用簡易リスト
						dbMsg +=">>"+artistSL;
					}else{
						artistSL.clear();
					}
				}
			}
			if(!pStrlist.equals("")){				//汎用プレイリストの内容が書き込まれていたが
				try {
					dbMsg += " , " + pl_file_name +" を書き込み";
					FileOutputStream fos = new FileOutputStream(new File(pl_file_name));
					//openFileOutputパス指定できず だと　/data/data/com.hijiyam_koubou.marasongs/files　にしか書き込めない
					OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
					BufferedWriter writer = new BufferedWriter(osw);
					writer.write(pStrlist);
					writer.close();
					dbMsg += " >>成功";
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			dbMsg += ">reqCode>"+reqCode + ",listType=" + listType;
			String pdTitol = getResources().getString(R.string.pref_playlist) +"" + getResources().getString(R.string.comon_sakusei);				//プレイリスト 作成>
			dbMsg += ",subText="+subText ;
			subHTF.setText(subText );
			String pdMessage = MuList.this.sousalistName + " ; " + subText;
			dbMsg += ",pdMessage="+pdMessage;
			int maxVal = MuList.this.plAL.size();
			nowList = sousalistName;
//			plTask.execute(reqCode,null,pdTitol,pdMessage,maxVal);
			for(int pdCoundtVal =0 ; pdCoundtVal < maxVal ; pdCoundtVal ++){
				if( sousalistName.equals(getResources().getString(R.string.playlist_namae_saikintuika)) ) {        //最近追加
					plWrightBody(pdCoundtVal);
				}else{
					plInfoSinglBody(pdCoundtVal);
				}
			}
			if( sousalistName.equals(getResources().getString(R.string.playlist_namae_saikintuika)) ) {        //最近追加
				mainHTF.setText(nowList);					//ヘッダーのメインテキスト表示枠
				subHTF.setText(maxVal + getResources().getString(R.string.comon_kyoku));					//ヘッダーのサブテキスト表示枠
				plWrightEnd();
			}else{
				setHeadImgList(plAL );				//イメージとサブテキストを持ったリストを構成
			}
			if( sousalistName.equals(getResources().getString(R.string.playlist_namae_saikintuika)) ){


				reqCode = MyConstants.SELECT_TREE;
			}else{
				reqCode = MyConstants.SELECT_SONG;
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * 情報付き単階層プレイリストの作成 */
	public void plInfoSinglBody(int i) {			//情報付き単階層プレイリストの作成
		final String TAG = "plInfoSinglBody";
		String dbMsg = "";
		try{
			dbMsg +=  "[" +i +"/" + MuList.this.plAL.size() + "]";///////////////////////////////////////////////////////////////////////////////////////////
			String AlbumArtistName =  (String) MuList.this.plAL.get(i).get("album_artist");
			dbMsg +=  ",AlbumArtist="+AlbumArtistName;
			String ArtistName =  (String) MuList.this.plAL.get(i).get(MediaStore.Audio.Playlists.Members.ARTIST );
			dbMsg +=  ",Artist="+ArtistName;
			if(AlbumArtistName == null){
				if(ArtistName == null){
					AlbumArtistName = getResources().getString(R.string.bt_unknown);			//不明
					ArtistName = getResources().getString(R.string.bt_unknown);			//不明
				}else{
					AlbumArtistName = ArtistName;
				}
				dbMsg +=  ">>"+AlbumArtistName;
			}
			String AlbumName =  (String) MuList.this.plAL.get(i).get(MediaStore.Audio.Playlists.Members.ALBUM );
			dbMsg +=  ",Album="+AlbumName;
			if( AlbumName == null ){
				AlbumName = getResources().getString(R.string.bt_unknown);			//不明
				dbMsg +=  ">>"+AlbumName;
			}

			String songTitol =  (String) MuList.this.plAL.get(i).get(MediaStore.Audio.Playlists.Members.TITLE );
			dbMsg +=  ",Titol="+songTitol;
			MuList.this.titolLayer = MuList.this.treeAdapter.add(songTitol);
			if(songTitol.equals(MuList.this.titolName)){				//それまで参照していたアーティスト名
				MuList.this.titolePosition = MuList.this.titoleCount;		//選択させるタイトル
				dbMsg +=  ",titolePosition="+MuList.this.titolePosition;
			}
			MuList.this.titoleCount++;		//タイトルのリスト上のポジション
			int playListID = MuList.this.nowList_id;
			dbMsg +=  ",playListID="+playListID;
			String playlistNAME  = String.valueOf(MuList.this.plAL.get(i).get(MediaStore.Audio.Playlists.NAME ));
			dbMsg +=  ",playlistNAME="+playlistNAME;
			if(playlistNAME.equals("null")){
				playlistNAME = nowList;
				dbMsg +=  ">>"+playlistNAME;
			}
			int playOrder = Integer.valueOf((String)MuList.this.plAL.get(i).get(MediaStore.Audio.Playlists.Members.PLAY_ORDER ));
			dbMsg +=  ",playOrder="+playOrder;
			int audioID = Integer.valueOf((String)MuList.this.plAL.get(i).get(MediaStore.Audio.Playlists.Members.AUDIO_ID ));
			dbMsg +=  ",audioID="+audioID;
			String track = String.valueOf(MuList.this.plAL.get(i).get(MediaStore.Audio.Playlists.Members.TRACK ));
			Util UTIL = new Util();
			track = UTIL.checKTrack( track);
			dbMsg +=  ",track="+track;
			String dataURL = String.valueOf(MuList.this.plAL.get(i).get( "DATA"  ));				//MediaStore.Audio.Playlists.Members.DATA
			dbMsg +=  ",dataURL="+dataURL;
			String duration = String.valueOf(MuList.this.plAL.get(i).get(MediaStore.Audio.Playlists.Members.DURATION ));
			dbMsg += ",duration="+duration;
			MuList.this.titolLayer.addOther( playListID, playOrder , artistID, albumID , audioID , track , duration , dataURL , playlistNAME , AlbumArtistName , ArtistName , lyer_titol , MuList.this.listType );

			MuList.this.artistID = Integer.valueOf((String)MuList.this.plAL.get(i).get(MediaStore.Audio.Playlists.Members.ARTIST_ID ));
			MuList.this.titolLayer.addArtistOther(artistID , lyer_titol , MuList.this.listType );
			MuList.this.albumID = Integer.valueOf((String)MuList.this.plAL.get(i).get(MediaStore.Audio.Playlists.Members.ALBUM_ID ));
			dbMsg += ",albumID="+albumID;
			String date_modified = String.valueOf(MuList.this.plAL.get(i).get(MediaStore.Audio.Playlists.Members.DATE_MODIFIED ));
			dbMsg += ",date_modified="+date_modified;
			String albumYear = String.valueOf(MuList.this.plAL.get(i).get(MediaStore.Audio.Playlists.Members.YEAR ));
			dbMsg += ",albumYear="+albumYear;
			MuList.this.titolLayer.addAlbumOther(albumID, albumYear,date_modified, lyer_titol , MuList.this.listType  , MuList.this.sousalistName);
//			tList.add(MuList.this.treeAdapter);
//			dbMsg +=">tList>" +tList.size() ;	///////////////////////////////////////////////////////////////////////////////////////////
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * 単階層+2階層プレイリストのアーティスト単階層作成 */
	public void plOneBody(int i) {		//プレイリストの描画
		final String TAG = "plOneBody";
		String dbMsg = "";
		try{
			dbMsg +=  "[" +i +"/" + plAL.size() + "]";///////////////////////////////////////////////////////////////////////////////////////////
			String AlbumArtistName =  (String) plAL.get(i).get("album_artist");
			dbMsg +=  ",AlbumArtist="+AlbumArtistName;
			String ArtistName =  (String) plAL.get(i).get(MediaStore.Audio.Playlists.Members.ARTIST );
			dbMsg +=  ",Artist="+ArtistName;
			if(AlbumArtistName == null){
				if(ArtistName == null){
					AlbumArtistName = getResources().getString(R.string.bt_unknown);			//不明
					ArtistName = getResources().getString(R.string.bt_unknown);			//不明
				}else{
					AlbumArtistName = ArtistName;
				}
				dbMsg +=  ">>"+AlbumArtistName;
			}
			if(! AlbumArtistName.equals(MuList.this.b_artist)){				//それまで参照していたアーティスト名
				dbMsg +=  "(←"+MuList.this.b_artist + ")";
				int bSize = artistAL.size();
				dbMsg +=  ",artistAL="+bSize;
				MuList.this.artistSL= ORGUT.retInListString((ArrayList<String>) MuList.this.artistSL , AlbumArtistName);	//渡された文字が既にリストに登録されていれば追加、短い一致名が有れば置換え
				int aSize = artistAL.size();
				dbMsg +=  ">>"+aSize;
				if(bSize < aSize){
					MuList.this.objMap.put("album_artist" ,AlbumArtistName );
					MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.ARTIST  ,ArtistName );
					MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.ARTIST_ID  ,Integer.valueOf((String)plAL.get(i).get(MediaStore.Audio.Playlists.Members.ARTIST_ID )) );
					MuList.this.objMap.put("layerName" , lyer_artist );
					MuList.this.objMap.put("listType" ,MuList.this.listType );
					MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.PLAYLIST_ID  ,MuList.this.sousalistID );
					MuList.this.objMap.put("playlist_name"  ,MuList.this.sousalistName );
					MuList.this.objMap.put("playlist_data"  ,MuList.this.sousalist_data );
					MuList.this.artistAL.add(MuList.this.artistAL.size(), objMap);		//アーティストリスト用ArrayList
					dbMsg +=  "→"+artistAL.size() + "件";
				}

				if(! MuList.this.b_artist.equals("")){				//それまで参照していたアーティスト名
					dbMsg +=  ",artistCount="+MuList.this.artistCount;
					MuList.this.artistCount++;		//アーティストのリスト上のポジション
					MuList.this.alubmCount = 0;			//アルバムのリスト上のポジション
					MuList.this.b_album ="";
					dbMsg +=  ">>="+MuList.this.artistCount;
				}
				dbMsg +=  ">>="+MuList.this.albumArtist;
				if(AlbumArtistName.equals(MuList.this.albumArtist)){				//それまで参照していたアーティスト名
					MuList.this.artistPosition = MuList.this.artistCount;		//選択させるアーティスト
					dbMsg +=  ",artistPosition="+MuList.this.artistPosition;
				}
				MuList.this.b_artist = AlbumArtistName;
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 *  指定したプレイリストから特定アーティストのアルバムとタイトル内容取得 */
	public void plAlbumTitol(long playlistId , String artistName){		//指定したプレイリストから特定アーティストのアルバムとタイトル内容取得
		final String TAG = "plAlbumTitol";
		String dbMsg = "";
		try{
			dbMsg += ORGUT.nowTime(true,true,true) + dbMsg;/////////////////////////////////////
			long start = System.currentTimeMillis();		// 開始時刻の取得
			MuList.this.plAL = new ArrayList<Map<String, Object>>();
			dbMsg += "選択されたプレイリストID="+playlistId;///////////////////////
			dbMsg += "、artistName、="+artistName;///////////////////////
			b_artist = artistName;				//リストを戻す時の設定値
			sousaAlbumArtist = artistName;	//操作対象のアルバムアーティスト名
			if( 0 < playlistId ){
				Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
				String[] columns =null;
				playLists = this.getContentResolver().query(uri, columns, null, null, null );
				int zennkyoku = playLists.getCount();
				playLists.close();
				String c_selection = MediaStore.Audio.Playlists.Members.ARTIST  +" LIKE ? ";			//		"album_artist"
				if(ORGUT.isInArrayString(compList, artistName)){
					c_selection ="album_artist LIKE ? ";			//
				}

				String[] c_selectionArgs= {"%" + artistName + "%" };			//⑥引数groupByには、groupBy句を指定します。
				String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
				playLists = this.getContentResolver().query(uri, columns, c_selection, c_selectionArgs, c_orderBy );
				int retInt = playLists.getCount();
				MuList.this.nowListSub = retInt + "/" + zennkyoku + getResources().getString(R.string.pp_kyoku);
				dbMsg += ","+ MuList.this.nowListSub;
				if( playLists.moveToFirst() ){
					headImgIV.setVisibility(View.GONE);								 // 表示枠を消す
					if( sousalistName.equals(getResources().getString(R.string.playlist_namae_repeat)) ){			//リピート再生
						mainHTF.setVisibility(View.GONE);
						pl_sp.setVisibility(View.VISIBLE);
					}else{
						pl_sp.setVisibility(View.GONE);
						mainHTF.setVisibility(View.VISIBLE);
						mainHTF.setText(artistName);
					}
					MuList.this.plSL =  new ArrayList<String>();				//プレイリスト用簡易リスト
					MuList.this.saisei_fnameList = new ArrayList<String>();		//uri配列
					MuList.this.listKyokuSuu = playLists.getCount();		//リストにある曲数
					dbMsg += ","+MuList.this.nowListSub;
					if( MuList.this.nowListSub != null ||
							sousalistName.equals(getResources().getString(R.string.playlist_namae_repeat)) 			//リピート再生
							){
						MuList.this.nowListSub = artistName + " ; " + MuList.this.listKyokuSuu + getResources().getString(R.string.pp_kyoku);
					} else {
						MuList.this.nowListSub = MuList.this.listKyokuSuu + getResources().getString(R.string.pp_kyoku);
					}
					dbMsg += "→"+MuList.this.nowListSub;
					reqCode = listType_2ly2 ;			// = listType_2ly + 1;albumとtitolの２階層
					dbMsg += ",reqCode="+reqCode;
					String pdTitol = getResources().getString(R.string.pref_playlist) +"" + getResources().getString(R.string.common_yomitori);				//読み込み
					String pdMessage = artistName+ ";" + retInt + getResources().getString(R.string.pp_kyoku);
					myLog(TAG, dbMsg);
					plTask.execute(reqCode,playLists,pdTitol,pdMessage,playLists.getCount());
//					pTask = (plogTask) new plogTask(this ,  this ,pdTitol ,pdMessage,retInt ).execute(reqCode,  pdMessage , playLists ,pdTitol );		//,jikkouStep,totalStep,calumnInfo
				}else{
					myLog(TAG, dbMsg);
				}
			}else{
				myLog(TAG, dbMsg);
			}
			myLog(TAG, dbMsg);
		}catch(IllegalArgumentException e){
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 *  指定したプレイリストから特定アーティストのアルバムとタイトル内容取得 */
	public void plAlbumTitolEnd(){		//playlistIdで指定したMediaStore.Audio.Playlists.Membersの内容取得
		final String TAG = "plAlbumTitolEnd";
		String dbMsg = "";
		try{
			int sPosition = 0;
			ArrayList<MyTreeAdapter> tList = null;				//ArrayList<MyTreeAdapter> tList = null;
			tList = new ArrayList<MyTreeAdapter>();				//tList = new ArrayList<MyTreeAdapter>()
			MuList.this.albumLayer  = null;
			MuList.this.titolLayer  = null;
			MuList.this.b_artist = "";
			MuList.this.b_album = "";
			MuList.this.artistPosition = 0;		//選択させるアーティスト
			MuList.this.alubmPosition = 0;		//選択させるアルバム
			MuList.this.titolePosition = 0;		//選択させるタイトル
			MuList.this.artistCount = 0;		//アーティストのリスト上のポジション
			MuList.this.alubmCount = 0;			//アルバムのリスト上のポジション
			MuList.this.titoleCount = 0;		//タイトルのリスト上のポジション

			MuList.this.treeAdapter = new MyTreeAdapter(tList);
			dbMsg +=  "reqCode="+reqCode;
			reqCode = MENU_2KAISOU;								//2階層リスト選択選択中
			dbMsg += "→"+reqCode;
			int retInt = plAL.size();
			String subText = retInt + getResources().getString(R.string.pp_kyoku);
			dbMsg += ",retInt="+subText + ":pref_artist_bunnri=" + pref_artist_bunnri + "曲";
			subHTF.setText(MuList.this.nowListSub );
			if(pref_list_simple){											//シンプルなリスト表示（サムネールなど省略）
				listType = listType_plane;									// 0;//情報なし
			}else {
				listType = listType_info;									//sub情報付き
			}
			dbMsg += ">reqCode>"+reqCode + ",listType=" + listType;
		//	MuList.this.plAL = new ArrayList<Map<String, Object>>();
			String pdTitol = getResources().getString(R.string.pref_playlist) +"" + getResources().getString(R.string.comon_sakusei);				//プレイリスト 作成>
	//		dbMsg += ",sousalistName="+sousalistName ;
			String pdMessage = MuList.this.sousalistName + " ; " + subText;
			dbMsg += ",pdMessage="+pdMessage;
			plTask.execute(reqCode,null,pdTitol,pdMessage,retInt);
//			pTask = (plogTask) new plogTask(this ,  this ,pdTitol ,pdMessage ,retInt ).execute(reqCode,  pdMessage , retInt ,pdTitol );		//,jikkouStep,totalStep,calumnInfo
			myLog(TAG, dbMsg);
		}catch(IllegalArgumentException e){
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * 単階層+2階層プレイリストの2階層作成 */
	public void plTowBody(int i) {
		final String TAG = "plTowBody";
		String dbMsg = "";
		try{
			dbMsg +=  "[" +i +"/" + plAL.size() + "]";///////////////////////////////////////////////////////////////////////////////////////////
			String AlbumArtistName = sousaAlbumArtist;					// (String) plAL.get(i).get("album_artist");
			dbMsg +=  ",AlbumArtist="+AlbumArtistName;
			String ArtistName =  (String) plAL.get(i).get(MediaStore.Audio.Playlists.Members.ARTIST );
			dbMsg +=  ",Artist="+ArtistName;
			if(AlbumArtistName == null){
				if(ArtistName == null){
					AlbumArtistName = getResources().getString(R.string.bt_unknown);			//不明
					ArtistName = getResources().getString(R.string.bt_unknown);			//不明
				}else{
					AlbumArtistName = ArtistName;
				}
				dbMsg +=  ">>"+AlbumArtistName;
			}
			String AlbumName =  (String) plAL.get(i).get(MediaStore.Audio.Playlists.Members.ALBUM );
			dbMsg +=  ",Album="+AlbumName;
			if( AlbumName == null ){
				AlbumName = getResources().getString(R.string.bt_unknown);			//不明
				dbMsg +=  ">>"+AlbumName;
			}
			if(! AlbumName.equals(MuList.this.b_album)){		//それまで参照していたアルバム名
				dbMsg +=  "(←"+MuList.this.b_album + ")";
				MuList.this.albumLayer = MuList.this.treeAdapter.add(AlbumName);
				MuList.this.albumID = Integer.valueOf((String)plAL.get(i).get(MediaStore.Audio.Playlists.Members.ALBUM_ID ));
				dbMsg +=  ",albumID="+albumID;
				String date_modified = String.valueOf(plAL.get(i).get(MediaStore.Audio.Playlists.Members.DATE_MODIFIED ));
				dbMsg +=  ",date_modified="+date_modified;
				String albumYear = String.valueOf(plAL.get(i).get(MediaStore.Audio.Playlists.Members.YEAR ));
				dbMsg +=  ",albumYear="+albumYear;
				MuList.this.albumLayer.addAlbumOther(albumID, albumYear,date_modified , lyer_album , MuList.this.listType ,MuList.this.sousalistName);
				if(! MuList.this.b_album.equals("")){				//それまで参照していたアーティスト名
					MuList.this.alubmCount++;		//アルバムのリスト上のポジション
					MuList.this.titoleCount = 0;		//タイトルのリスト上のポジション
				}
				if(AlbumName.equals(MuList.this.albumName)){				//それまで参照していたアーティスト名
					MuList.this.alubmPosition = MuList.this.alubmCount;		//選択させるアルバム
					dbMsg +=  ",alubmPosition="+MuList.this.alubmPosition;
				}
				MuList.this.b_album = AlbumName;
			}
			String songTitol =  (String) plAL.get(i).get(MediaStore.Audio.Playlists.Members.TITLE );
			dbMsg +=  ",Titol="+songTitol;
			MuList.this.titolLayer  = MuList.this.albumLayer.add(songTitol);
			if(songTitol.equals(MuList.this.titolName)){				//それまで参照していたアーティスト名
				MuList.this.titolePosition = MuList.this.titoleCount;		//選択させるタイトル
				dbMsg +=  ",titolePosition="+MuList.this.titolePosition;
			}
			MuList.this.titoleCount++;		//タイトルのリスト上のポジション
			int playListID = MuList.this.nowList_id;
			dbMsg +=  ",playListID="+playListID;
			String playlistNAME  = String.valueOf(plAL.get(i).get(MediaStore.Audio.Playlists.NAME ));
			dbMsg +=  ",playlistNAME="+playlistNAME;
			if(playlistNAME.equals("null")){
				playlistNAME = nowList;
				dbMsg +=  ">>"+playlistNAME;
			}
			int playOrder = Integer.valueOf((String)plAL.get(i).get(MediaStore.Audio.Playlists.Members.PLAY_ORDER ));
			dbMsg +=  ",playOrder="+playOrder;
			int audioID = Integer.valueOf((String)plAL.get(i).get(MediaStore.Audio.Playlists.Members.AUDIO_ID ));
			dbMsg +=  ",audioID="+audioID;
			String track = String.valueOf(plAL.get(i).get(MediaStore.Audio.Playlists.Members.TRACK ));
			dbMsg +=  ",track="+track;
			track = UTIL.checKTrack( track);
			String dataURL = String.valueOf(plAL.get(i).get( "DATA"  ));				//MediaStore.Audio.Playlists.Members.DATA
			dbMsg +=  ",dataURL="+dataURL;
			String duration = String.valueOf(plAL.get(i).get(MediaStore.Audio.Playlists.Members.DURATION ));
			dbMsg +=  ",duration="+duration;
			MuList.this.titolLayer.addOther( playListID, playOrder , artistID, albumID , audioID , track , duration , dataURL , playlistNAME , AlbumArtistName , ArtistName , lyer_titol , listType_3ly );
//			tList.add(MuList.this.treeAdapter);
//			dbMsg +=">tList>" +tList.size() ;	///////////////////////////////////////////////////////////////////////////////////////////
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * 3階層プレイリストの作成 ; plALから読取り
	 * */
	public void plWrightBody(int i) {		//プレイリストの描画
		final String TAG = "plWrightBody";
		String dbMsg = "";
		try{
			dbMsg +=  "[" +i +"/" + plAL.size() + "]";///////////////////////////////////////////////////////////////////////////////////////////
//			if(i == 0){
//				MuList.this.b_artist =  "";
//			}
			String AlbumArtistName =  (String) plAL.get(i).get("album_artist");
			dbMsg +=  ",AlbumArtist="+AlbumArtistName;
			String ArtistName =  (String) plAL.get(i).get(MediaStore.Audio.Playlists.Members.ARTIST );
			dbMsg +=  ",Artist="+ArtistName;

			if(AlbumArtistName == null){
				if(ArtistName == null){
					AlbumArtistName = getResources().getString(R.string.bt_unknown);			//不明
					ArtistName = getResources().getString(R.string.bt_unknown);			//不明
				}else if(MuList.this.b_artist !=  null){
					dbMsg += ",b_artist=" + MuList.this.b_artist;
					if(i == 0) {
						AlbumArtistName = ArtistName;					//次のアーティスト
					}else if(-1 < ArtistName.indexOf(MuList.this.b_artist)) {	//クレジットされたアーティスト名に前曲のアルバムアーティスト名が含まれれば
						AlbumArtistName = MuList.this.b_artist;				//アルバム名義は同一
					} else{                                              //それでも無ければ
						AlbumArtistName = ArtistName;					//次のアーティスト
					}
				}else if(ArtistName !=  null){                                         //それでも無ければ
					AlbumArtistName = ArtistName;					//次のアーティスト
				}
				dbMsg += ">AlbumArtist>" + AlbumArtistName;
			}else if(ArtistName !=  null){                                         //それでも無ければ
				if(i == 0) {
					AlbumArtistName = ArtistName;					//次のアーティスト
				}else if(-1 < ArtistName.indexOf(MuList.this.b_artist)) {	//クレジットされたアーティスト名に前曲のアルバムアーティスト名が含まれれば
					AlbumArtistName = MuList.this.b_artist;				//アルバム名義は同一
				} else{                                              //それでも無ければ
					AlbumArtistName = ArtistName;					//次のアーティスト
				}
				dbMsg +=  ">>AlbumArtist="+AlbumArtistName;
			}

			String AlbumName =  (String) plAL.get(i).get(MediaStore.Audio.Playlists.Members.ALBUM );
			dbMsg +=  ",Album="+AlbumName;
			if( AlbumName == null ){
				AlbumName = getResources().getString(R.string.bt_unknown);			//不明
				dbMsg +=  ">>"+AlbumName;
			}
			if(! AlbumArtistName.equals(MuList.this.b_artist)){				//それまで参照していたアーティスト名
				dbMsg +=  "(←"+MuList.this.b_artist + ")";
				MuList.this.artistLayer = MuList.this.treeAdapter.add(AlbumArtistName);
				MuList.this.artistID = Integer.valueOf((String)plAL.get(i).get(MediaStore.Audio.Playlists.Members.ARTIST_ID ));
				dbMsg += ",artistID="+artistID;
				MuList.this.artistLayer.addArtistOther(artistID , lyer_artist , MuList.this.listType );
				if(! MuList.this.b_artist.equals("")){				//それまで参照していたアーティスト名
					dbMsg += ",artistCount="+MuList.this.artistCount;
					MuList.this.artistCount++;		//アーティストのリスト上のポジション
					MuList.this.alubmCount = 0;			//アルバムのリスト上のポジション
					MuList.this.b_album ="";
					dbMsg +=  ">>="+MuList.this.artistCount;
				}
				dbMsg += ">>="+MuList.this.albumArtist;
				if(AlbumArtistName.equals(MuList.this.albumArtist)){				//それまで参照していたアーティスト名
					MuList.this.artistPosition = MuList.this.artistCount;		//選択させるアーティスト
					dbMsg += ",artistPosition="+MuList.this.artistPosition;
				}
				MuList.this.b_artist = AlbumArtistName;
			}
			if(! AlbumName.equals(MuList.this.b_album)){		//それまで参照していたアルバム名
				dbMsg += "(←"+MuList.this.b_album + ")";
				MuList.this.albumLayer = MuList.this.artistLayer.add(AlbumName);
				String rVal = (String) plAL.get(i).get(MediaStore.Audio.Playlists.Members.ALBUM_ID);
				if(rVal != null){
					MuList.this.albumID = Integer.parseInt(rVal);
				}else{
					albumID=0;
				}
				dbMsg += ",albumID="+albumID;
				String date_modified = String.valueOf(plAL.get(i).get(MediaStore.Audio.Playlists.Members.DATE_MODIFIED ));
				dbMsg += ",date_modified="+date_modified;
				String albumYear = String.valueOf(plAL.get(i).get(MediaStore.Audio.Playlists.Members.YEAR ));
				dbMsg += ",albumYear="+albumYear;
				MuList.this.albumLayer.addAlbumOther(albumID, albumYear,date_modified , lyer_album , MuList.this.listType ,MuList.this.sousalistName);
				if(! MuList.this.b_album.equals("")){				//それまで参照していたアーティスト名
					MuList.this.alubmCount++;		//アルバムのリスト上のポジション
					MuList.this.titoleCount = 0;		//タイトルのリスト上のポジション
				}
				if(AlbumName.equals(MuList.this.albumName)){				//それまで参照していたアーティスト名
					MuList.this.alubmPosition = MuList.this.alubmCount;		//選択させるアルバム
					dbMsg += ",alubmPosition="+MuList.this.alubmPosition;
				}
				MuList.this.b_album = AlbumName;
			}
			String songTitol =  (String) plAL.get(i).get(MediaStore.Audio.Playlists.Members.TITLE );
			dbMsg += ",Titol="+songTitol;
			MuList.this.titolLayer  = MuList.this.albumLayer.add(songTitol);
			if(songTitol.equals(MuList.this.titolName) && reqCode == MyConstants.v_titol){				//それまで参照していたアーティスト名
				MuList.this.titolePosition = MuList.this.titoleCount;		//選択させるタイトル
				dbMsg += ",titolePosition="+MuList.this.titolePosition;
			}
			MuList.this.titoleCount++;		//タイトルのリスト上のポジション
			int playListID = MuList.this.nowList_id;
			dbMsg += ",playListID="+playListID;
			String playlistNAME  = String.valueOf(plAL.get(i).get(MediaStore.Audio.Playlists.NAME ));
			dbMsg += ",playlistNAME="+playlistNAME;
			if(playlistNAME.equals("null")){
				playlistNAME = nowList;
				dbMsg += ">>"+playlistNAME;
			}
			int playOrder = Integer.valueOf((String)plAL.get(i).get(MediaStore.Audio.Playlists.Members.PLAY_ORDER ));
			dbMsg +=  ",playOrder="+playOrder;
			int audioID = Integer.valueOf((String)plAL.get(i).get(MediaStore.Audio.Playlists.Members.AUDIO_ID ));
			dbMsg += ",audioID="+audioID;
			String track = String.valueOf(plAL.get(i).get(MediaStore.Audio.Playlists.Members.TRACK ));
			Util UTIL = new Util();
			track = UTIL.checKTrack( track);
			String dataURL = String.valueOf(plAL.get(i).get( MediaStore.Audio.Playlists.Members.DATA ));
			dbMsg +=  ",dataURL="+dataURL;
			String duration = String.valueOf(plAL.get(i).get(MediaStore.Audio.Playlists.Members.DURATION ));
//			dbMsg +=  ",duration="+duration;
			MuList.this.titolLayer.addOther( playListID, playOrder , artistID, albumID , audioID , track , duration , dataURL , playlistNAME , AlbumArtistName , ArtistName , lyer_titol , listType_3ly );
//			tList.add(MuList.this.treeAdapter);
//			dbMsg +=">tList>" +tList.size() ;	///////////////////////////////////////////////////////////////////////////////////////////
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * 多階層プレイリストをリストビューに書き込みイベント設定 */
	public void plWrightEnd() {									//プレイリストの描画	reqCode = MENU_TAKAISOU;//多階層リスト選択選択中にセット
		final String TAG = "plWrightEnd";
		String dbMsg = "";
		try{
			dbMsg += "treeAdapter=" + MuList.this.treeAdapter.getCount() +"件" ;
			MuList.this.lvID.setAdapter(MuList.this.treeAdapter);

			MuList.this.lvID.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					final String TAG = "onItemClick";
					String dbMsg = "[MuList.plWrightEnd]";
					try{
						dbMsg =  ",position="+position + ",id="+id;
						myLog(TAG, dbMsg);
						listClick( parent, view, position, id);			//共有：クリックの処理
					}catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}
			});

			MuList.this.lvID.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){	// 項目が長押しクリックされた時のハンドラ
				// リスナーを登録する thisを引数にできない //☆キャストの方法　(OnItemLongClickListener) this
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {	// 長押しクリックされた時の処理を記述
					final String TAG = "onItemLongClick";
					String dbMsg = "[MuList.plWrightEnd]";
					dbMsg += ORGUT.nowTime(true,true,true);/////////////////////////////////////
					try{
						dbMsg += ",position=" + position + ",id=" + id;
						myLog(TAG, dbMsg);
						listLongClick( parent, view, position, id);		//共有：ロングクリックの処理
					}catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
					return false;
				}
				//このブロックでToast.makeTextは使えない
			});		//onItemLongClick

			MuList.this.lvID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {				//端末の十字キー等でリストの項目がフォーカスされた
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});

			if( nowList_id == sousalistID ){
				selectTreePosition(mIndex);
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void selectTreeItem() {									//プレイリストの描画	reqCode = MENU_TAKAISOU;//多階層リスト選択選択中にセット
		final String TAG = "selectTreeItem";
		String dbMsg = "";
		try{
			dbMsg += "treeAdapter=" + MuList.this.treeAdapter.getCount() +"件" ;
			TreeEntry treeEntry;
			dbMsg += ",artistPosition=" + artistPosition;
			if( 0 <= artistPosition){		//選択させるアーティスト					 && yobidashiItem == MyConstants.v_artist
				treeEntry = (TreeEntry)lvID.getItemAtPosition(artistPosition);		//選択させるアーティスト
				//	int depth = treeEntry.getDepth();
				treeEntry.expand();			//指定したTreeEntryを開く
				dbMsg += ",treeEntry=" + treeEntry;
				alubmPosition= artistPosition+  1 + alubmPosition;
			}
			dbMsg += ",alubmPosition=" + alubmPosition;
			if( 0 <= alubmPosition ){		//選択させるアルバム	 && yobidashiItem == MyConstants.v_alubum
				treeEntry = (TreeEntry)lvID.getItemAtPosition(alubmPosition);		//選択させるアルバム
				treeEntry.expand();						//指定したTreeEntryを開く
				titolePosition = alubmPosition+ + 1 + titolePosition;
			}
			dbMsg += ",titolePosition=" + titolePosition;
			if( 0 <= titolePosition && yobidashiItem == MyConstants.v_titol){			//選択させるタイトル
				lvID.setSelection(titolePosition);		//選択させるタイトル
				lvID.setFocusable(true);
				lvID.setFocusableInTouchMode(true);
				lvID.requestFocus();
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	@SuppressLint("Range")
	public void selectTreePosition(int iPosition) {									//プレイリストの描画	reqCode = MENU_TAKAISOU;//多階層リスト選択選択中にセット
		final String TAG = "selectTreePosition";
		String dbMsg = "";
		try{
			dbMsg += "[mItems;" + "]";			//iPosition + "/" + mItems.size() + "件]";
			Cursor playingItem = musicPlaylist.getPlaylistItems(nowList_id, mIndex);
			if (playingItem.moveToFirst()) {
				creditArtistName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST));        //playingItem.artist;	//クレジットされているアーティスト名
				dbMsg += " ,クレジット⁼ " + creditArtistName;
				albumName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM));        //playingItem.album;			//アルバム名
				dbMsg += " , アルバム⁼" + albumName;/////////////////////////////////////	this.album = album;
				titolName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE));        //playingItem.title;		//曲名
				dbMsg += " ,タイトル= " + titolName;/////////////////////////////////////		this.title = title;
				int audioId = Integer.parseInt(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)));
				dbMsg += " ,audioId= " + audioId;
//				albumArtist = musicPlaylist.getAlbumArtist(audioId , MaraSonActivity.this);	//playingItem.album_artist;	//アルバムアーティスト名
//				dbMsg +=" ,アルバムアーティスト= " + albumArtist;
				Uri dataUri = Uri.parse(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA)));
				String dataFN = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA));
				lp_artist.setText( creditArtistName);
				lp_album.setText( albumName);
				lp_title.setText( titolName);
				if ( albumArt ==null ) {
					albumArt =ORGUT.retAlbumArtUri( getApplicationContext() , creditArtistName , albumName );			//アルバムアートUriだけを返すalbumArtist		MaraSonActivity.this  ,
					dbMsg +=">>" + albumArt ;/////////////////////////////////////
				}

//				String album_art = intent.getStringExtra("album_art") +"";
//				dbMsg +=",album_art=" + album_art;
				if(! albumArt.equals("")){
					OrgUtil ORGUT = new OrgUtil();				//自作関数集
					WindowManager wm = (WindowManager)MuList.this.getSystemService(Context.WINDOW_SERVICE);
					Display disp = wm.getDefaultDisplay();
					ImageView rc_Img = findViewById(R.id.rc_Img);			//ヘッダーのアイコン表示枠				headImgIV = (ImageView)findViewById(R.id.headImg);		//ヘッダーのアイコン表示枠
					int width = rc_Img.getWidth();
					width = width*9/10;
					Bitmap mDummyAlbumArt = ORGUT.retBitMap(albumArt , width , width , getResources());        //指定したURiのBitmapを返す	 , dHighet , dWith ,
					rc_Img.setImageBitmap(mDummyAlbumArt);
				}
				setListPlayer( creditArtistName, albumName, titolName, albumArt);

			}
			playingItem.close();

			if(artistSL == null){
				artistSL =musicPlaylist.listUpArtist(MuList.this);					//アーティストリスト用簡易リスト
			}
			String artistNmme = artistSL.get(iPosition);
//					mItems.get(iPosition).album_artist;
			dbMsg += ",artistNmme=" +  artistNmme;
			albumSL =musicPlaylist.listUpAlubm(artistNmme,MuList.this);					//アルバムリスト用簡易リスト
			String albumName = albumSL.get(0);
					//mItems.get(iPosition).album;
			dbMsg += ",albumName=" +  albumName;
			titolSL =musicPlaylist.listUpTitol(artistNmme,albumName,MuList.this);						//タイトルリスト用簡易リスト
			String titleName = titolSL.get(0);
//					mItems.get(iPosition).title;
			dbMsg += ",titleName=" +  titleName;
////			TreeEntry treeEntry;
////			int tCount = MuList.this.treeAdapter.getCount();
////			dbMsg += ",treeAdapter=" + tCount + "件";
////			dbMsg += "[lvID=" + lvID + "]";
			String bArtistNmme = "";
			String bAlbumName = "";
//			String bTitleName = "";
			artistPosition=0;
			alubmPosition=0;
			titolePosition=0;


			for(int i=0; i< iPosition ; i++) {
//				artistNmme = mItems.get(i).album_artist;
//				albumName = mItems.get(i).album;
				if(0 == i){
					dbMsg += "[" + i + "}";
					dbMsg += ",artistNmme=" +  artistNmme;
					bArtistNmme = artistNmme;
					bAlbumName = albumName;
//					bTitleName = titleName;
				}else{
					if(! artistNmme.contains(bArtistNmme)){
						dbMsg += "\n[" + i + "}";
						dbMsg += ",artistNmme=" +  artistNmme;
						artistPosition++;
						alubmPosition = 0;
						titolePosition = 0;
						bArtistNmme = artistNmme;
						bAlbumName = albumName;
					}else if(! albumName.equals(bAlbumName)){
						dbMsg += ",albumName=" +  albumName;
						alubmPosition++;
						titolePosition = 0;
						bAlbumName = albumName;
					}else{
						titolePosition++;
					}
				}
			}
			dbMsg += "\nartistPosition=" + artistPosition;
			dbMsg += ",alubmPosition=" + alubmPosition;
			dbMsg += ",titolePosition=" + titolePosition;
			myLog(TAG, dbMsg);
			selectTreeItem();
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * 選択した楽曲からプレイリストを新規作成する */
	public void makePlaylist(String fastItem){			//選択した楽曲からプレイリストを新規作成する
		final String TAG = "makePlaylist";
		String dbMsg = "";
		try{
			dbMsg +=  "読み取ったリスト[" + sousalistID +"]"+ sousalistName +"の=" + fastItem;
			tuikaItemName = getResources().getString(R.string.pref_playlist) + sdffiles.format(new Date());		//プレイリスト
			dbMsg +=">追加するリスト>" + tuikaItemName;
			itemStr = fastItem;
			String dMessage = getResources().getString(R.string.make_playlist_msg) + "\n\n"+ 	//新規リストを作成して追加
					getResources().getString(R.string.add_playlist_msg) + ";\n"+fastItem;			//追加する曲名</string>
			LayoutInflater inflater = LayoutInflater.from(MuList.this);
			View view = inflater.inflate(R.layout.input_dlog, null);
			final EditText editText = view.findViewById(R.id.input_dlog_et);
			editText.setText(tuikaItemName);
			AlertDialog.Builder Dlg = new AlertDialog.Builder(MuList.this);
			Dlg.setTitle(getResources().getString(R.string.list_contex_make_playlist));
			Dlg.setMessage( dMessage);				//リストの名称を設定してください。
			Dlg.setIcon(R.drawable.ic_launcher);
			Dlg.setView(view) .setPositiveButton( getResources().getString(R.string.comon_kakutei),	//確定
				new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) {
					final String TAG = "makePlaylist";
					String dbMsg =  "選択した楽曲からプレイリストを新規作成する" ;/////////////////////////////////////
					try{
						MuList.this.tuikaItemName =  editText.getText().toString();
						dbMsg +=  "listName=" + MuList.this.tuikaItemName;
						makePlaylistBody( MuList.this.tuikaItemName , MuList.this.itemStr);			//選択した楽曲からプレイリストを新規作成する
					}catch (Exception e) {
						myErrorLog(TAG,dbMsg +"で"+e.toString());
					}
					}
				});
			Dlg.setNegativeButton(getResources().getString(R.string.comon_cyusi),	//中止</string>
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			Dlg.show();
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * プレイリスト一つ分の内容取得 */
	public void makePlaylistBody(String listName , String fastItem){			//選択した楽曲からプレイリストを新規作成する
		final String TAG = "makePlaylistBody";
		String dbMsg = "";
		try{
			dbMsg +=  "作成するリスト名=" + listName;						//listName=プレイリスト2015-12-03 14:16:37
			dbMsg += ",読み取ったリスト=[" + sousalistID + "]" + sousalistName;			//fastIteme=Remake The World,reqCode=2131558448
			dbMsg += " の" + fastItem;			//fastIteme=Remake The World,reqCode=2131558448
			int audio_id =0;
			String fastItemeFn = null;
			Uri result_uri = null;
			dbMsg += ",sousaRecordUrl=" + sousaRecordUrl;			//
			if(sousaRecordUrl != null){
				fastItemeFn = sousaRecordUrl;
			}else{
				fastItemeFn = retDataUrl( fastItem , sousalistID , selPosition);		//曲名で指定された音楽ファイルのUrlを返す
			}
			dbMsg += ",fastItemeFn=" + fastItemeFn;			//fastItemeFn=/storage/sdcard0/Music/Jimmy Cliff/Follow My Mind/07 Remake The World.wma
			dbMsg += ",sousaRecordId=" + sousaRecordId;
			if(-1 < sousaRecordId){
				audio_id = sousaRecordId;
			}else{
				audio_id = retAudioId( fastItemeFn , sousalistID , selPosition);			//データURLで指定された音楽ファイルのaudio_idを返す
			}
			dbMsg += ",audio_id=" + audio_id;
			String resMseg = listName + "\n" + getResources().getString(R.string.make_playlist_msg_f);		//作成できませんでした。
			if(0 < audio_id){
				result_uri = musicPlaylist.addPlaylist(listName, null, null);		//プレイリストを新規作成する
				dbMsg += ",result_uri=" + result_uri;			//fastItemeFn=/storage/sdcard0/Music/Jimmy Cliff/Follow My Mind/07 Remake The World.wma
				MuList.this.tuikaSakiListID = (int)ContentUris.parseId(result_uri);
				dbMsg += ",追加先[" + MuList.this.tuikaSakiListID + "]" + result_uri;			//[42529]content://media/external/audio/playlists/42529
				int data_hash = 0;
				if(result_uri != null && 0 < MuList.this.tuikaSakiListID){		// musicPlaylist.
					result_uri = musicPlaylist.addMusicToPlaylist( MuList.this.tuikaSakiListID, audio_id, fastItemeFn);	//プレイリストへ曲を追加する
					MuList.this.sousalistID = MuList.this.tuikaSakiListID;
					MuList.this.sousalistName = MuList.this.tuikaItemName;
					MuList.this.sousalistUri = result_uri;
					dbMsg += ",作成したリスト=[" + sousalistID + "]" + sousalistName;			//fastIteme=Remake The World,reqCode=2131558448
				}else{
					Toast.makeText(this, resMseg, Toast.LENGTH_LONG).show();
				}
				dbMsg += ">追加結果>"  + result_uri;			//>>content://media/external/audio/playlists/42529
			}else{
				Toast.makeText(this, resMseg, Toast.LENGTH_LONG).show();
			}
			headImgIV.setVisibility(View.GONE);
			mainHTF.setVisibility(View.GONE);
			artistHTF.setVisibility(View.GONE);			//ヘッダーのアーティスト名表示枠
			pl_sp.setVisibility(View.VISIBLE);
			plNameSL = null;
			myLog(TAG,dbMsg);
			makePlayListSPN(sousalistName);		//プレイリストスピナーを作成する
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 *  曲名で指定された音楽ファイルのUrlを返す	 * @param listPosition プレイリスト上の位置；play_order*/
	@SuppressLint("Range")
	public String retDataUrl(String itemName , int List_id , int listPosition){			//曲名で指定された音楽ファイルのUrlを返す
		String itemeFn = null;
		final String TAG = "retAudioId";
		String dbMsg = "";
		dbMsg += itemName + "のUrを返す" ;/////////////////////////////////////
		try{
			if( sousalistName.equals(getResources().getString(R.string.listmei_zemkyoku)) ){		// 全曲リストのアーティスト選択
				dbMsg += ",アルバム=" + MuList.this.sousa_alubm ;/////////////////////////////////////
				Uri cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
				String c_selection =MediaStore.Audio.Media.ALBUM + " = ? AND " +MediaStore.Audio.Media.TITLE + " = ?";			//☆20151228	何故かコンピレーションが追記されないので後で追加
				String[] c_selectionArgs = new String[]{ MuList.this.sousa_alubm,itemName };
				String c_orderBy=MediaStore.Audio.Media._ID ;			// + " DESC"; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
				Cursor cursor = this.getContentResolver().query( cUri , null , c_selection , c_selectionArgs, c_orderBy);
				int kyoku = cursor.getCount();									//redrowIDList.size();
				dbMsg += "で" + kyoku + "件" ;/////////////////////////////////////
				if(cursor.moveToFirst()){
					itemeFn = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
				}
			} else {
				dbMsg += ",albumName=" + albumName;
				Uri cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
				String[] c_columns = null;		 		//③引数columnsには、検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
				String c_selection =  MediaStore.Audio.Media.ALBUM +" = ? AND " + MediaStore.Audio.Media.TITLE +" = ? ";			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
				String[] c_selectionArgs= {String.valueOf(albumName) , itemName};   			//⑥引数groupByには、groupBy句を指定します。
				String c_orderBy=MediaStore.Audio.Media.DATA; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
				Cursor cursor = getContentResolver().query( cUri , c_columns , c_selection , c_selectionArgs, c_orderBy);
				int kyoku = cursor.getCount();
				dbMsg +=";"+ kyoku + "件×"+ cursor.getColumnCount() + "項目";
				if(cursor.moveToFirst()){
					itemeFn = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
				}
				cursor.close();
			}
			dbMsg += ",itemeFn=" + itemeFn;			//fastItemeFn=/storage/sdcard0/Music/Jimmy Cliff/Follow My Mind/07 Remake The World.wma
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return itemeFn;
	}

	/**
	 *  データURLで指定された音楽ファイルのaudio_idを返す */
	@SuppressLint("Range")
	public int retAudioId(String itemeFn , int List_id , int listPosition){			//データURLで指定された音楽ファイルのaudio_idを返す
		int audio_id =-1;
		final String TAG = "retAudioId";
		String dbMsg = "";
		dbMsg +=  "データURL=" + itemeFn;/////////////////////////////////////
		try{
			if( sousalistName.equals(getResources().getString(R.string.listmei_zemkyoku)) ){		// 全曲リストのアーティスト選択
				Uri cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
				String c_selection =MediaStore.Audio.Media.DATA + " = ?";			//☆20151228	何故かコンピレーションが追記されないので後で追加
				String[] c_selectionArgs = new String[]{ itemeFn };
				String c_orderBy=MediaStore.Audio.Media._ID ;			// + " DESC"; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
				Cursor cursor = this.getContentResolver().query( cUri , null , c_selection , c_selectionArgs, c_orderBy);
				int kyoku = cursor.getCount();									//redrowIDList.size();
				if(cursor.moveToFirst()){
					audio_id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
				}
				cursor.close();
			} else {
				dbMsg += ",List_id=" + List_id;/////////////////////////////////////
				Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
				String[] columns = null;				//{ idKey, nameKey };
				Cursor cursor = this.getContentResolver().query(uri, columns, null, null, null);
				if(cursor.moveToFirst()){
					boolean keizoku = true;
					do{
						int plID = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID));
						dbMsg += "[" + plID +"]";
						String plNAme = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME));
						dbMsg += plNAme;
						Uri kensakuUri = MediaStore.Audio.Playlists.Members.getContentUri("external", plID);
						String c_selection = MediaStore.Audio.Playlists.Members.DATA +" = ? ";			//MediaStore.Audio.Media.IS_MUSIC + " = 1"
						String[] c_selectionArgs2= { String.valueOf(itemeFn) };   			//⑥引数groupByには、groupBy句を指定します。
						Cursor cursor2 = getContentResolver().query(kensakuUri, null, c_selection, c_selectionArgs2, null);
						dbMsg += "," + cursor2.getCount() +"件";
						if(cursor2.moveToFirst()){
							audio_id = cursor2.getInt(cursor2.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID));
						}
						cursor2.close();
						keizoku = cursor.moveToNext();
						if( 0 < audio_id){
							keizoku = false;
						}
					}while(keizoku);
				}
				cursor.close();
			}
			dbMsg += ">MediaStore.Audio.Media._ID>" + audio_id;
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return audio_id;
	}

	/**
	 *  既存のプレイリストに選択した曲を追加 */
	public void kizonListTuika(String tuikaItemName){
		final String TAG = "kizonListTuika";
		String dbMsg = "";
		try{
			this.tuikaItemName = tuikaItemName;
			this.tuiukaItemeFn = null;
			this.tuiukaItemeFn = retDataUrl( tuikaItemName , nowList_id , selPosition);		//曲名で指定された音楽ファイルのUrlを返す
			dbMsg += ",tuiukaItemeFn=" + tuiukaItemeFn;			//fastItemeFn=/storage/sdcard0/Music/Jimmy Cliff/Follow My Mind/07 Remake The World.wma
			this.audio_id =0;
			this.audio_id = retAudioId( tuiukaItemeFn , nowList_id , selPosition);			//データURLで指定された音楽ファイルのaudio_idを返す
			dbMsg += ",audio_id=" + audio_id;
			if(0 < audio_id){
				contextTitile = contextTitile + getResources().getString(R.string.add_playlist_titol);	//追加先のリスト
				List<String> addList= getPList();		//プレイリストを取得する
				dbMsg += ">>" + plNameSL.size() +">>";/////
				final CharSequence[] items =addList.toArray(new CharSequence[addList.size()]);
					dbMsg += items.length +"件";/////////////////////////////////////
					AlertDialog.Builder listDlg = new AlertDialog.Builder(this);			// リスト表示用のアラートダイアログ
					listDlg.setTitle(contextTitile);
					listDlg.setItems(items,
						new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {			// リスト選択時の処理, which は、選択されたアイテムのインデックス
							final String TAG = "onClick[kizonListTuika]";
							String dbMsg = "";
							try{
								dbMsg +=  "("+ which +")" ;/////////////////////////////////////
								tuikaSakiListName = items[which].toString();				//追加先のリスト名
								dbMsg += tuikaSakiListName;/////////////////////////////////////
								tuikaSakiListID = Integer.parseInt(String.valueOf(plNameAL.get(which).get("_id")));		//追加先のリストID	プレイリスト名用リスト
								dbMsg += "("+ tuikaSakiListID +")";/////////////////////////////////////
								myLog(TAG,dbMsg);
								kizonListTuikaBody(MuList.this.tuikaItemName ,MuList.this.tuiukaItemeFn , MuList.this.audio_id , tuikaSakiListID , tuikaSakiListName);
							}catch (Exception e) {
								myErrorLog(TAG,dbMsg +"で"+e.toString());
							}
						}
					});

					listDlg.setNegativeButton(getResources().getString(R.string.comon_cyusi),  new DialogInterface.OnClickListener() {	//中止</string>
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					listDlg.setCancelable(false);
					listDlg.create().show();			// 表示
			}else{
				String resMseg = tuikaItemName + "\n" + getResources().getString(R.string.make_playlist_tuika_msg_f);		//追加できませんでした。
				Toast.makeText(this, resMseg, Toast.LENGTH_LONG).show();
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 *  既存のプレイリストに選択した曲を追加* */
	public void kizonListTuikaBody(String tuikaItemName ,String tuiukaItemeFn , int audio_id , int sousalistID , String sousalistName){
		final String TAG = "kizonListTuikaBody";
		String dbMsg = "";
		try{
			dbMsg +=   "追加曲=" + tuikaItemName;
			dbMsg += "," + tuiukaItemeFn;
			dbMsg += ",audio_id=" + audio_id;
			dbMsg += "[" + sousalistID + "]" + sousalistName;			// + tuikasakiUri;		//[42529]content://media/external/audio/playlists/42529
			int data_hash = 0;
			Uri result_uri = null;
			if(0 < sousalistID){			//		musicPlaylist.
				 result_uri = musicPlaylist.addMusicToPlaylist( sousalistID, audio_id, tuiukaItemeFn);	//プレイリストへ曲を追加する
			}else{
				String resMseg = tuikaItemName + "\n" + getResources().getString(R.string.make_playlist_tuika_msg_f);		//追加できませんでした。
				Toast.makeText(this, resMseg, Toast.LENGTH_LONG).show();
			}
			dbMsg += ">addMusicToPlaylist>"  + result_uri;			//>>content://media/external/audio/playlists/42529

			MuList.this.plSL =  new ArrayList<String>();				//プレイリスト用簡易リスト
			MuList.this.saisei_fnameList = new ArrayList<String>();		//uri配列
			MuList.this.plAL = new ArrayList<Map<String, Object>>();
			MuList.this.plSL.clear();				//プレイリスト用簡易リスト
			MuList.this.plAL.clear();
			String pdMessage = sousalistName;
			titolName = itemStr;
			dbMsg +=">CreatePLList>追加した曲=" + tuikaItemName;
			dbMsg +="、plAL=" + plAL.size();
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public String menuName;

	/**
	 *  リスト一覧を表示してプレイリストを削除/リスト名変更 */
	public void playListHnkou(String menuName){			//指定したプレイリストを削除する
		int retInt =0;
		final String TAG = "playListHnkou[MuList]";
		String dbMsg = "";
		try{
			MuList.this.menuName = menuName;
			dbMsg +=  "操作=" + MuList.this.menuName ;/////////////////////////////////////
			if( plNameSL == null ){
				plNameSL = getPList();		//プレイリストを取得する
			}
			dbMsg += ",plNameSL = " + plNameSL.size() +"件";
			plNameSL.remove(getResources().getString(R.string.listmei_zemkyoku));					// ,	// 全曲リスト
			final CharSequence[] items =plNameSL.toArray(new CharSequence[plNameSL.size()]);
			dbMsg += ">>"+items.length +"件";/////////////////////////////////////
			String dTitile = getResources().getString(R.string.playlist_del_line_titol1);				//削除するリストを選択して下さい。
			if( menuName.equals(getResources().getString(R.string.list_contex_rename_playlist)) ){		//リスト名変更
				dTitile = getResources().getString(R.string.renameplaylist_titol);						//名称を変更するリストを選択して下さい。
			}
			AlertDialog.Builder listDlg = new AlertDialog.Builder(this);			// リスト表示用のアラートダイアログ
			listDlg.setTitle(dTitile);
			listDlg.setItems(items,
				new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {			// リスト選択時の処理, which は、選択されたアイテムのインデックス
					final String TAG = "onClick[playListHnkou]";
					String dbMsg = "";
					try{
						dbMsg +=  "リスト上のインデックス=" + which ;
						MuList.this.tuikaSakiListID =Integer.valueOf( String.valueOf( plNameAL.get(which+1).get("_id") )) ;
						dbMsg += "[操作対象リスト=" + MuList.this.tuikaSakiListID + "]";
						MuList.this.tuikaSakiListName =  String.valueOf( plNameAL.get(which+1).get("name") ) ;
						dbMsg += MuList.this.tuikaSakiListName;
			//			dbMsg += ",操作対象リスト名=" + plNameSL.get(which) ;
						myLog(TAG,dbMsg);
						if( MuList.this.menuName.equals(getResources().getString(R.string.list_contex_rename_playlist)) ){			//リスト名変更
							listmeiHenkou(MuList.this.tuikaSakiListID,  MuList.this.tuikaSakiListName);				//プレイリストの名前を変更する
						} else if( MuList.this.menuName.equals(getResources().getString(R.string.list_contex_del_playlist)) ){		// このリストを削除
							deletPlayList(MuList.this.tuikaSakiListName);			//指定したプレイリストを削除する
						}
						myLog(TAG, dbMsg);
					}catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}
			});

			listDlg.setNegativeButton(getResources().getString(R.string.comon_cyusi),  new DialogInterface.OnClickListener() {	//中止</string>
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			listDlg.setCancelable(false);
			listDlg.create().show();			// 表示
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 *  指定したプレイリストを削除*/
	@SuppressLint("Range")
	public int deletPlayList(String listName){			//指定したプレイリストを削除する
		int retInt =0;
		final String TAG = "deletPlayList";
		String dbMsg = "";
		try{
			MuList.this.tuikaSakiListName = listName;		//操作対象リスト名
			dbMsg +=  "listName=" + listName ;/////////////////////////////////////
			Uri playlist_uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;			//URI
			String[] columns = null;				//{ idKey, nameKey };
			String c_selection =   MediaStore.Audio.Playlists.NAME +" = ? ";
			String[] c_selectionArgs= { String.valueOf(listName) };   			//⑥引数groupByには、groupBy句を指定します。
			String c_orderBy = null;				//MediaStore.Audio.Playlists.Members.PLAY_ORDER;
			Cursor playLists = getContentResolver().query(playlist_uri, columns, c_selection, c_selectionArgs, c_orderBy);
			if(playLists.moveToFirst()){
				tuikaSakiListID =  playLists.getInt(playLists.getColumnIndex( MediaStore.Audio.Playlists._ID));
				dbMsg +="[" + tuikaSakiListID +"]" ;/////////////////////////////////////
				String dMessage = "[" + tuikaSakiListID +"]"+listName;
				AlertDialog.Builder Dlg = new AlertDialog.Builder(MuList.this);
				Dlg.setTitle(getResources().getString(R.string.list_contex_del_playlist));			//このリストを削除
				Dlg.setMessage( dMessage);				//リストの名称を設定してください。
				Dlg.setIcon(R.drawable.ic_launcher);
				Dlg .setPositiveButton( getResources().getString(R.string.comon_sakujyo),	//削除
					new DialogInterface.OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which) {
						final String TAG = "onClick";
						String dbMsg = "[MuList.deletPlayList]";
						try{
							dbMsg +=  "tuikaSakiListID=" + MuList.this.tuikaSakiListID;
							reqCode = CONTEXT_del_playlist;
							deletPlayListLoop(reqCode ,  MuList.this.tuikaSakiListID);			//指定したプレイリストを削除する
							myLog(TAG, dbMsg);
						}catch (Exception e) {
							myErrorLog(TAG ,  dbMsg + "で" + e);
						}
						}
					});
				Dlg.setNegativeButton(getResources().getString(R.string.comon_cyusi),	//中止</string>
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				Dlg.show();
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return retInt;
	}

	/**
	 * 指定したプレイリストの内容削除(プログレス処理への引き継ぎ) */
	public void deletPlayListLoop(int reqCode , int listID){			//指定したプレイリストを削除する
		int retInt =0;
		final String TAG = "deletPlayListLoop";
		String dbMsg = "";
		MuList.this.tuikaSakiListID = listID;
		dbMsg += ",reqCode="+reqCode;
		try{
			Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", MuList.this.tuikaSakiListID);
			String[] columns = null;			//{ idKey, nameKey };
			String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
			playLists = this.getContentResolver().query(uri, columns, null, null, c_orderBy );
			dbMsg += ",該当"+playLists.getCount() +"件";
			if( playLists.moveToFirst() ){
				MuList.this.tuikaSakilistUri = null;
				if(isGalaxy()){
					MuList.this.tuikaSakilistUri = Uri.parse("content://media/external/audio/music_playlists/" + listID + "/members");
				}else{
					MuList.this.tuikaSakilistUri = MediaStore.Audio.Playlists.Members.getContentUri("external", listID);
				}
				MuList.this.plAL = new ArrayList<Map<String, Object>>();
				String pdTitol = getResources().getString(R.string.pref_playlist) +"" + getResources().getString(R.string.comon_sakujyo);		//削除
				String pdMessage = MuList.this.tuikaSakiListName + ";" + retInt + getResources().getString(R.string.comon_ken);						//件
				retInt = playLists.getCount();
				plTask.execute(reqCode,playLists,pdTitol,pdMessage,playLists.getCount());
//				pTask = (plogTask) new plogTask(this ,  this ,pdTitol ,pdMessage,retInt ).execute(reqCode,  pdMessage , playLists ,pdTitol );		//,jikkouStep,totalStep,calumnInfo
			}else{							//曲が登録されていなければ
				switch(reqCode) {
				case CONTEXT_rumdam_redrow:			//643 ランダム再生リストの消去
					randumPlay2();
					break;
				case CONTEXT_REPET_CLEAN:			//646 リピート再生リストの既存レコード消去
					repeatPlayMake();
					break;
				case CONTEXT_add_request:			//リクエストリスト
					requestListTuika();								//リクエストリスト作成・楽曲追加
					break;
				case CONTEXT_dell_request:					//リクエスト解除
					requestListResetEnd();
					break;
				default:
					deletPlayListEnd();			//指定したプレイリストを削除する
					break;
				}
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 *  指定したプレイリストを削除(レコードごとの読出し) */
	public Cursor deletPlayListBody(Cursor cursor){			//指定したプレイリストを削除する
		int retInt =0;
		final String TAG = "deletPlayListBody[MuList]";
		String dbMsg = "";
		try{
			dbMsg +=  "削除するプレイリスト=" + MuList.this.tuikaSakilistUri ;
			dbMsg += "(" + cursor.getPosition() + "/" + cursor.getCount() +")";
			@SuppressLint("Range") int delID = cursor.getInt(cursor.getColumnIndex( MediaStore.Audio.Playlists.Members._ID));
			dbMsg +="[" + delID +"]" ;/////////////////////////////////////
			dbMsg += MuList.this.tuikaSakilistUri ;/////////////////////////////////////
			@SuppressLint("Range") String rStr = cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Playlists.Members.TITLE));/////////////////////////////////////
			dbMsg +=";" + rStr;/////////////////////////////////////
			int retint=delOneLineBody( MuList.this.tuikaSakilistUri, delID);			//プレイリストから指定された行を削除する MuList.this.sousalistUri, MuList.this.sousaRecordId
			 dbMsg += ",削除= " + retint + "レコード";
//			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return cursor;
	}

	/**
	 *  指定したプレイリストを削除(終了処理) */
	public void deletPlayListEnd(){			//指定したプレイリストを削除する
		int retInt =0;
		final String TAG = "deletPlayListEnd";
		String dbMsg = "";
		try{
			playLists.close();
			if(reqCode == CONTEXT_del_playlist){
				Uri playlist_uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;			//URI
				String where = MediaStore.Audio.Playlists._ID + " = ?";					//"_id = ?";
				String[] selectionArgs = { String.valueOf(MuList.this.tuikaSakiListID) };
				retInt = getContentResolver().delete(playlist_uri, where, selectionArgs);
				dbMsg += ",削除=" + retInt;		//listId=42533,retInt=1
				myLog(TAG,dbMsg);
				plNameSL = null;
				makePlayListSPN(sousalistName);		//プレイリストスピナーを作成する
				int ePosition = plNameSL.indexOf(MuList.this.nowList);
				dbMsg +=  "ePosition=" + ePosition;
				pl_sp.setSelection(ePosition , false);								//☆falseで勝手に動作させない
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public boolean isGalaxy(){
		boolean retrbool =false;
		final String TAG = "isGalaxy";
		String dbMsg = "";
		try{
			Uri playlist_uri = Uri.parse("content://media/external/audio/music_playlists");			//URI
			dbMsg += ",retrbool=" + retrbool;
//			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return retrbool;
	}

	/**  プレイリストを新規作成する */

	/**
	 *  プレイリストの名前を変更（名前変更ダイアログ表示） */
	public void listmeiHenkou(int listID, String motolistName){				//プレイリストの名前を変更する
		final String TAG = "listmeiHenkou";
		String dbMsg = "";
		try{
//			dbMsg +=  "リスト上のインデックス=" + listPosition ;
//			MuList.this.sousalistID =Integer.valueOf( String.valueOf( plNameAL.get(listPosition).get("_id") )) ;
			dbMsg += "[" + listID +"]" ;
//			MuList.this.sousalistName = motolistName;		//操作対象リスト名
			dbMsg += ",操作対象リスト名=" + motolistName ;
//			String rStr =  String.valueOf( plNameAL.get(listPosition).get("name") ) ;
//			dbMsg += ",rStr=" + rStr ;
			myLog(TAG,dbMsg);
	//		String dMessage = motolistName;			//追加する曲名</string>
			LayoutInflater inflater = LayoutInflater.from(MuList.this);
			View view = inflater.inflate(R.layout.input_dlog, null);
			final EditText editText = view.findViewById(R.id.input_dlog_et);
			editText.setText(motolistName);
			AlertDialog.Builder Dlg = new AlertDialog.Builder(MuList.this);
			Dlg.setTitle(getResources().getString(R.string.playlist_namehennkou_dt));		//プレイリストの名前を変更
	//		Dlg.setMessage( dMessage);				//リストの名称を設定してください。
			Dlg.setIcon(R.drawable.ic_launcher);
			Dlg.setView(view);
			Dlg.setPositiveButton( getResources().getString(R.string.comon_kakutei),	//確定
				new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) {
					final String TAG = "PositiveButton";
					String dbMsg =  "プレイリストの名前を変更する" ;/////////////////////////////////////
					try{
						dbMsg +=   "[操作対象リスト名=" + MuList.this.tuikaSakiListID + "]" + MuList.this.tuikaSakiListName;
						MuList.this.tuikaSakiListName =  editText.getText().toString();
						dbMsg +=">>"+ MuList.this.tuikaSakiListName;
						updatePlaylist(MuList.this.tuikaSakiListID, MuList.this.tuikaSakiListName);			//プレイリストの名前を変更する
						myLog(TAG, dbMsg);
					}catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}
			});
			Dlg.setNegativeButton(getResources().getString(R.string.comon_cyusi),	//中止</string>
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			Dlg.show();
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 *  プレイリストの名前を変更する（update実行） */
	public void updatePlaylist(int tuikaSakiListID, String tuikaSakiListName){				//プレイリストの名前を変更する
		final String TAG = "updatePlaylist";
		String dbMsg = "";
		try{
			dbMsg +=  "操作対象リストID=" + tuikaSakiListID ;
			dbMsg += ",操作対象リスト名=" + tuikaSakiListName ;
			ContentValues contentvalues = null;
			Uri uri = null;
			String[] as;
			int result = -1;
			if(isGalaxy()){
				uri = Uri.parse("content://media/external/audio/music_playlists");
			}else{
				uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;			//URIの作成
				dbMsg += ",uri=" + uri ;
			}
			contentvalues = new ContentValues(1);			//登録データの作成
			contentvalues.put("name", tuikaSakiListName);
			as = new String[1];
			as[0] = Integer.toString(tuikaSakiListID);
			result = getContentResolver().update(uri, contentvalues, "_id = ?", as);			//変更
			if(result != 1){
				dbMsg += ">失敗 update playlist : " + tuikaSakiListName + ", " + result ;
			}else{
				MuList.this.sousalistID = tuikaSakiListID;
				MuList.this.sousalistName = tuikaSakiListName;
				dbMsg += ">成功  , " + result;
				dbMsg +=   "[操作対象リスト名=" + MuList.this.tuikaSakiListID + "]" + MuList.this.tuikaSakiListName;
				myLog(TAG,dbMsg);
				plNameSL = null;
				makePlayListSPN(tuikaSakiListName);		//プレイリストスピナーを作成する
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 *  プレイリストの曲を上下移動させる(指定IF) */
	public int listEnd = 0;
	public void plJyouge(int motoPosition , String sousaRecordName){				//プレイリストの曲を上下移動させる
		final String TAG = "plJyouge[MuList]";
		String dbMsg = "";
		try{
			dbMsg += "["+ nowList_id +"]" + nowList;
			String pdTitile = "("+ motoPosition +")" + sousaRecordName + " ; " + getResources().getString(R.string.playlist_idou_titol);	// 選択する曲の前、もしくは次に移動します。</string>
			//	List<String> addList= getPList();		//プレイリストを取得する
			dbMsg += ">>" + plSL.size() +">>";/////
			final CharSequence[] idouItems =plSL.toArray(new CharSequence[plSL.size()]);
			listEnd = idouItems.length-1;
			dbMsg += idouItems.length +"件";/////////////////////////////////////
			myLog(TAG,dbMsg);
			AlertDialog.Builder listDlg = new AlertDialog.Builder(this);			// リスト表示用のアラートダイアログ
			listDlg.setTitle(pdTitile);
			listDlg.setSingleChoiceItems(idouItems, motoPosition,					//null);
				new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {			// リスト選択時の処理, which は、選択されたアイテムのインデックス
					final String TAG = "onClick";
					String dbMsg = "[MuList.plJyouge]";
					try{
						dbMsg +=  "("+ which +")" ;/////////////////////////////////////
						MuList.this.idousaki = which;			//リスト上の移動先
						myLog(TAG,dbMsg);
					}catch (Exception e) {
						myErrorLog(TAG,dbMsg +"で"+e.toString());
					}
				}
			});
			if(0 < motoPosition){
				listDlg.setPositiveButton(getResources().getString(R.string.playlist_idou_ue), new DialogInterface.OnClickListener() {		//前に</string>
					public void onClick(DialogInterface dialog, int which) {
						String dbMsg =  MuList.this.idousaki +"番目の前に";/////////////////////////////////////
						MuList.this.idousaki--;
						if(MuList.this.idousaki < 0 ){
							MuList.this.idousaki = 0;
							dbMsg += "→" + MuList.this.idousaki +"番目の前に";/////////////////////////////////////
						}
						myLog(TAG, dbMsg);
						plJyougeBody( MuList.this.idousaki);				//プレイリストの曲を上下移動させる
					}
				});
			}
			if(motoPosition < listEnd){
				listDlg.setNeutralButton(getResources().getString(R.string.playlist_idou_sita), new DialogInterface.OnClickListener() {		//次に</string>
					public void onClick(DialogInterface dialog, int which) {
						String dbMsg =  MuList.this.idousaki +"番目の前に";/////////////////////////////////////
						MuList.this.idousaki++;
						if(listEnd < MuList.this.idousaki){
							MuList.this.idousaki = listEnd;
							dbMsg += "→" + MuList.this.idousaki +"番目の前に";/////////////////////////////////////
						}
						myLog(TAG, dbMsg);
						plJyougeBody( MuList.this.idousaki);				//プレイリストの曲を上下移動させる
					}
				});
			}
			listDlg.setNegativeButton(getResources().getString(R.string.comon_cyusi),  new DialogInterface.OnClickListener() {	//中止</string>
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			listDlg.setCancelable(false);
			listDlg.create().show();			// 表示
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 *  プレイリストの曲を上下移動させる(本体)*/
	public void plJyougeBody( int to){				//プレイリストの曲を上下移動させる
		final String TAG = "plJyougeBody[MuList]";
		String dbMsg = "";
		try{
			dbMsg += "["+ sousalistID +"]" + sousalistName;
			dbMsg += ","+ MuList.this.selPosition +"→" + to;
			if( selPosition != to ){
				boolean moved = MediaStore.Audio.Playlists.Members.moveItem(getContentResolver(), MuList.this.sousalistID, MuList.this.selPosition, to);
				//APIL8☆追加先リストID、元の位置、書き換え先でPlay_order変更
				dbMsg += ",書換え="+ moved;
				if(moved ){
					MuList.this.plSL =  new ArrayList<String>();				//プレイリスト用簡易リスト
					MuList.this.saisei_fnameList = new ArrayList<String>();		//uri配列
					MuList.this.plAL = new ArrayList<Map<String, Object>>();
					String pdMessage = MuList.this.sousalistName;
					MuList.this.nowListSub = null;
					titolName = itemStr;
		//			reqCode = R.id.plistDPTF;				//2131558424 プレイリスト
					CreatePLList(  Long.valueOf(MuList.this.sousalistID) , pdMessage);		//プレイリストの内容取得			sousalist_data,
				}
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 *  プレイリストから指定された行を削除する */
	protected void delOneLine(int playlist_id, int delOrder , String delName){				//プレイリストから指定された行を削除する
		final String TAG = "delOneLine[MuList]";
		String dbMsg = "";
		try{
			dbMsg +=   "削除するレコードが有るプレイリストのID= " + playlist_id;
			sousalistID = playlist_id;
			Uri playlist_uri = null;
			if(isGalaxy()){
				playlist_uri = Uri.parse("content://media/external/audio/music_playlists/" + playlist_id + "/members");
			}else{
				playlist_uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist_id);
			}
			dbMsg += "→uri= " + playlist_uri;
			dbMsg += "[削除する曲順= " + delOrder + "]";
			dbMsg += delName;
			String[] columns = null;				//{ idKey, nameKey };
			String c_selection =   MediaStore.Audio.Playlists.Members.PLAY_ORDER +" = ? AND " +
									MediaStore.Audio.Playlists.Members.TITLE +" = ? ";
			String[] c_selectionArgs= { String.valueOf(delOrder) , String.valueOf(delName) };   			//⑥引数groupByには、groupBy句を指定します。
			Cursor playLists = getContentResolver().query(playlist_uri, columns, c_selection, c_selectionArgs, null);
			dbMsg += ","+playLists.getCount() +"件";
			if( playLists.moveToFirst() ){
				@SuppressLint("Range") int delID =  playLists.getInt(playLists.getColumnIndex( MediaStore.Audio.Playlists.Members._ID));
				dbMsg +="[" + delID +"]" ;/////////////////////////////////////
				String dMessage = "(" + (delID+1) + getResources().getString(R.string.comon_kyokume) +")"+delName + "[" + delID +"]";	//曲目
				MuList.this.sousalistUri = playlist_uri;		//操作対象リスト
				MuList.this.sousaRecordId = delID;		//操作対象レコードのID
				AlertDialog.Builder Dlg = new AlertDialog.Builder(this);			// リスト表示用のアラートダイアログ
				Dlg.setTitle(getResources().getString(R.string.playlist_del_line_titol));		//削除しますか？
				Dlg.setMessage(dMessage);
				Dlg.setPositiveButton(getResources().getString(R.string.comon_jikkou),	//実行</string>
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int retint=delOneLineBody( MuList.this.sousalistUri, MuList.this.sousaRecordId);			//プレイリストから指定された行を削除する
						String dbMsg = "[MuList]renint= " + retint;
						if(-1 < retint ){
							playOrderSyuusei( MuList.this.sousalistID, 0);				//再生順を振りなおす
							MuList.this.nowListSub = null;
							String pdMessage = MuList.this.nowList;
							CreatePLList( Long.valueOf(MuList.this.sousalistID) , pdMessage);		//プレイリストの内容取得			MuList.this.nowList_data,
							reqCode= MENU_MUSCK_PLIST;			//プレイリスト選択中
							dbMsg +=  "追加した曲=" + tuikaItemName;
							sigotoFuriwake(reqCode , null , null  , tuikaItemName , null);		//表示するリストの振り分け			, plAL
						}
						myLog(TAG, dbMsg);
					}
				});
				Dlg.setCancelable(true);
				Dlg.create().show();			// 表示
				Dlg.setNegativeButton(getResources().getString(R.string.comon_cyusi),	//中止</string>
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
			}else{
				AlertDialog.Builder Dlg = new AlertDialog.Builder(this);			// リスト表示用のアラートダイアログ
				Dlg.setTitle(getResources().getString(R.string.pref_playlist));		// プレイリスト</string>
				Dlg.setMessage(getResources().getString(R.string.pl_nasi_meg));		//ご指定のプレイリストは曲が登録されていないか、読み込めませんでした。
				Dlg.setNegativeButton(getResources().getString(R.string.comon_kakuninn),	//確認
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				Dlg.setCancelable(false);
				Dlg.create().show();			// 表示
			}
			playLists.close();
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * プレイリストから指定された行を削除する*/
	protected int delOneLineBody(Uri listUri, int delId){				//プレイリストから指定された行を削除する
		int renint =-1;
		final String TAG = "delOneLineBody";
		String dbMsg = "";
		try{
			dbMsg +=   "削除レコードのプレイリストUri= " + listUri.toString();
			dbMsg += ",削除するレコードID= " + delId;
			String where = "_id=" + Integer.valueOf(delId);
			renint = getContentResolver().delete(listUri, where, null);				//削除		getApplicationContext()
			dbMsg += ",削除= " + renint + "レコード";
//			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return renint;
	}

	/**
	 * * 再生順を振りなおす */
	protected void playOrderSyuusei(int playlist_id, int startId){				//再生順を振りなおす
		final String TAG = "removeItems[MuList]";
		String dbMsg = "";
		try{
			dbMsg +=   "操作対象プレイリストのID= " + playlist_id;
			dbMsg += ",操作開始レコード= " + startId;
			Uri playlist_uri = null;
			if(isGalaxy()){
				playlist_uri = Uri.parse("content://media/external/audio/music_playlists/" + playlist_id + "/members");
			}else{
				playlist_uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist_id);
			}
			dbMsg += ",uri= " + playlist_uri;
			String[] columns = null;				//{ idKey, nameKey };
			String c_selection = null;		//  MediaStore.Audio.Playlists.Members.PLAY_ORDER +" = ? AND " MediaStore.Audio.Playlists.Members.TITLE +" = ? ";
			String[] c_selectionArgs= null;			//{ String.valueOf(delOrder) , String.valueOf(delName) };   			//⑥引数groupByには、groupBy句を指定します。
			Cursor playLists = getContentResolver().query(playlist_uri, columns, c_selection, c_selectionArgs, null);
			dbMsg += ","+playLists.getCount() +"件";
			if( playLists.moveToFirst() ){
				if(startId != 0){
					playLists.moveToPosition(startId);
				}
				do{
					dbMsg = "[" + playLists.getPosition() + "/" + playLists.getCount() +"]";
			//		if(playLists.getPosition() != startId){
						@SuppressLint("Range") int sousaID = playLists.getInt(playLists.getColumnIndex(MediaStore.Audio.Playlists.Members._ID));
						dbMsg += ",id=" + sousaID +",PLAY_ORDER= " + startId;
						ContentValues contentvalues = new ContentValues();
						contentvalues.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, startId);
						String where = MediaStore.Audio.Playlists.Members._ID +" = ?";
						String[] selectionArgs = {String.valueOf(sousaID)};
						int result = getContentResolver().update(playlist_uri, contentvalues, where, selectionArgs);
						dbMsg += ",result=" + result;
			//		}
					startId++;
					myLog(TAG,dbMsg);
				}while(playLists.moveToNext());
			}
			myLog(TAG,dbMsg);
			playLists.close();
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}

	public ArrayList<String> modifiedList;
	public String alabumName;
	public String modifide;
	public int listUpDates;
	public int listUpCount;
	public NumberPicker npd_np;			//ナンバーピッカー
	public ArrayList<String> urls;		//プレイリストのURLのみの配列
	private static CharSequence[] m3Item= null;			//リストアイテム
	private AlertDialog alertDialog;
	public List<Map<String, String>> albumArtistLis = new ArrayList<Map<String, String>>();


	/**
	 * 汎用プレイリスト選択
	 * ***/
	public void readm3U2(){
		final String TAG = "readm3U2";
		String dbMsg = "";
		try{
			dbMsg += "pref_commmn_music=" + pref_commmn_music;

			File fileDir = new File(pref_commmn_music);
			File [] file_lists = null;
			try {
				file_lists = fileDir.listFiles();
			}catch(Exception e){
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
			if(file_lists!= null){
				dbMsg += ",file_lists=" + file_lists.length + "件";
				dbMsg += ":" + file_lists[0] + "〜" + file_lists[file_lists.length - 1];

				ArrayList<CharSequence> m3Files = new ArrayList<CharSequence>();
				for (int i = 0; i < file_lists.length ; i++){
					if (file_lists[i].isDirectory()) {	//ディレクトリの場合
//						file_lists2.add("/" + file.getName());
					}else{	//通常のファイル
						String fNmes = file_lists[i].getName();
						if(fNmes.contains(".m3u")){
							m3Files.add(fNmes);
						}
					}
				}
				int gSize = m3Files.size();
				dbMsg += ">>" + gSize + "件";
				if(0 < gSize) {
					dbMsg += ":" + m3Files.get(0) + "〜" + m3Files.get(gSize - 1);
					String dTitol = getResources().getString(R.string.m3uRead_mgs);
					 //読み込むプレイリストファイルを選択して下さい
					String NegaBTT = getResources().getString(R.string.comon_cyusi);
					m3Item = new CharSequence[gSize];
					for (int i = 0; i < gSize ; i++){
						m3Item[i] = m3Files.get(i);
					}
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( MuList.this );
					alertDialogBuilder.setTitle(dTitol);	// アラートダイアログのタイトルを設定します 	getApplicationContext()
					alertDialogBuilder.setItems(m3Item, new DialogInterface.OnClickListener() {			//DialogInterface.OnClicｋListener()
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String TAG = "onClick";
							String dbMsg = "onClick[MuList.リスト]" + which +"]";
							String sentakuItem = (String) m3Item[which];
							dbMsg += sentakuItem +"を選択";/////////////////////////////////////////////////////////////////////////////////////
							alertDialog.dismiss();
							m3U2PlayList(sentakuItem);		//test20200311;全曲リスト作成後　pref_commmn_music + File.separator +
							myLog(TAG, dbMsg);
						}
					});
					alertDialogBuilder.setNegativeButton(NegaBTT,new DialogInterface.OnClickListener() {
						//	@Override
						public void onClick(DialogInterface dialog, int which) {
							alertDialog.dismiss();
						}
					});
					alertDialog = alertDialogBuilder.create();	// アラートダイアログを表示します
					alertDialog.setCanceledOnTouchOutside(false);	//背景をタップしてもダイアログを閉じない
					alertDialog.show();
				}
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * m3UからAndroidのPlayListに転記
	 * **/
	public void m3U2PlayList(String listFileName){
		final String TAG = "m3U2PlayList";
		String dbMsg = "";
		try{
			String listName ="";
			if(listFileName.contains(".")){
				dbMsg += ">>";
				String[] rStrs = listFileName.split("\\.",0);
				dbMsg += rStrs.length + "分割>>";
				listName = rStrs[0];
			}else{
				listName = listFileName;
			}
			listFileName = pref_commmn_music + File.separator + listFileName;	// + ".m3u";
			dbMsg += "listFileName=" + listFileName;
			dbMsg += ">>" + listName;
			MuList.this.tuikaSakiListName = listName;
			File rFile = new File(listFileName);
			String pdTitol = listName ;
			String pdMessage =   "該当するファイルは有りません。" ;						//確認	曲

			if(rFile.exists()){
				albumArtistLis = new ArrayList<Map<String, String>>();
				if( plNameSL == null ){
					plNameSL = getPList();		//プレイリストを取得する
				}
				MuList.this.tuikaSakiListID = siteiListSakusi( MuList.this.tuikaSakiListName );
				MuList.this.nowList_id = MuList.this.tuikaSakiListID;
				dbMsg +=  "[" + tuikaSakiListID + "]" +MuList.this.tuikaSakiListName + "へ";
				final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", tuikaSakiListID);
				final String[] columns = null;			//{ idKey, nameKey };
				String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
				Cursor cursor0 = this.getContentResolver().query(uri, columns, null, null, c_orderBy );
				dbMsg += ",変更前"+ cursor0.getCount() +"件";
				int renint = getContentResolver().delete(uri , null , null);                //where = nullで全件削除
				dbMsg += ",renint"+ renint;
				cursor0 = this.getContentResolver().query(uri, columns, null, null, c_orderBy );
				dbMsg += ">>"+ cursor0.getCount() +"件";
				cursor0.close();
				try{
					FileInputStream in = new FileInputStream( rFile );
					BufferedReader reader = new BufferedReader( new InputStreamReader( in , StandardCharsets.UTF_8) );
					dbMsg += "：m3uファイル読み込み:" ;
					String tmp;
					urls = new ArrayList<String>();
					while( (tmp = reader.readLine()) != null ){
						urls.add(tmp);
	//					str = str + tmp + "\n";
					}
					reader.close();
				}catch( IOException e ){
					myErrorLog(TAG ,  dbMsg + "で" + e);
				}
				dbMsg += ", " + urls.get(0);
				int pdMaxVal = urls.size();
				dbMsg += " 〜 " + urls.get(pdMaxVal - 1);
				pdMessage =  listName + "(" + pdMaxVal + getResources().getString(R.string.pp_kyoku)
						+ ")" + getResources().getString(R.string.play_list_wr_msgi);						//確認	曲
				reqCode = CONTEXT_M3U2_PL;					//汎用プレイリストをAndroidのプレイリストに転記
				dbMsg +=",reqCode="+ reqCode;
				plTask.execute(reqCode,null,pdTitol,pdMessage,pdMaxVal);
//				pTask = (plogTask) new plogTask(this ,  this ,pdTitol ,pdMessage, pdMaxVal ).execute(reqCode,  pdMessage , urls ,pdTitol );		//,jikkouStep,totalStep,calumnInfo
			}else{
				dbMsg += ">>" + pdMessage;
			}
//			dbMsg += "：" + pdMessage;
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 *指定されたインデックスのURLから
	 * **/
	public void m3U2PlayList_body(int index){
		final String TAG = "";
		String dbMsg = "";
		try{
			dbMsg +=  MuList.this.tuikaSakiListID + "に("+ ( index + 1 ) + "曲目)";/////////////////////////////////////
			String rURL = urls.get(index);
			dbMsg +=",rURL="+ rURL;
			String extntionStr = "";
			if(rURL.contains(".mp3" )){
				extntionStr = ".mp3";
			}else if(rURL.contains(".aac" )){
				extntionStr = ".aac";
			}else if(rURL.contains(".m4a" )){
				extntionStr = ".m4a";
			}else if(rURL.contains(".mp4" )){
				extntionStr = ".mp4";
			}else if(rURL.contains(".flac" )){
				extntionStr = ".flac";
			}else if(rURL.contains(".wav" )){
				extntionStr = ".wav";
			}else if(rURL.contains(".aif" )){
				extntionStr = ".aif";
			}else if(rURL.contains(".ogg" )){
				extntionStr = ".ogg";
			}else if(rURL.contains(".wma" )){
				extntionStr = ".wma";
			}else if(rURL.contains(".mp3" )){
				extntionStr = ".mp3";
			}
			dbMsg +=",extntionStr="+ extntionStr;
			String[] rStrs = rURL.split(extntionStr,0);
			dbMsg += rStrs.length + " で分割>>";
			rURL = rStrs[0];
			String commentStr = rStrs[1];
			rStrs = rURL.split(pref_commmn_music,0);
			rURL = pref_commmn_music + rStrs[1] + extntionStr;
			//m3uのセパレータ　
//			String recexStr = ",";
//			if(rURL.contains("," )){
//				String[] rStrs = rURL.split("\\,",0);
//
			//m3uのコメント ＃ もファイル名に書き込めてしまうので分離条件に使えない
			dbMsg += ":::rURL= " + rURL;
			File rFile = new File(rURL);
			if(rFile.exists()) {
				ContentResolver resolver = MuList.this.getContentResolver();	//c.getContentResolver();
				Uri cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
				String c_selection =  MediaStore.Audio.Media.DATA +" = ? "  ;
				String[] c_selectionArgs= {rURL};   			//, null , null , null
				String c_orderBy = null; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
				cursor = resolver.query( cUri , null , c_selection , c_selectionArgs, c_orderBy);
				if(cursor.moveToFirst()) {
					@SuppressLint("Range") int audio_id = Integer.valueOf(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
					dbMsg += ",audio_id=" + audio_id;
					Uri result_uri = musicPlaylist.addMusicToPlaylist(MuList.this.tuikaSakiListID, audio_id, rURL);    //プレイリストへ曲を追加する
					dbMsg += ">>result_uri=" + result_uri;/////////////////////////////////////
				}else{
					dbMsg += " 該当無し" ;
				}
			}else{
				dbMsg += " 該当ファイル無し" ;
			}
			if(commentStr.contains("#" )){
				dbMsg += ">remark>";
				rStrs = commentStr.split("\\#",0);
				dbMsg += rStrs.length + "分割>>";
				commentStr = rStrs[1];
				dbMsg += ",commentStr=" + commentStr;
				if(MuList.this.tuikaSakiListName.equals(MuList.this.getResources().getString(R.string.all_songs_file_name))){
					String[] aas = commentStr.split("\\,",0);
					dbMsg += aas.length + "分割>>";
					String album_artist = aas[0];
					String album = aas[1];
					boolean isAdd = false;
					if( 0 < albumArtistLis.size()){
						Map<String, String> rtMap= albumArtistLis.get(albumArtistLis.size() - 1);
						String b_album_artist = rtMap.get("album_artist");
						String b_album = rtMap.get("album");
						if(!album.equals(b_album)){		//!album_artist.equals(b_album_artist) &&
							isAdd = true;
						}
					}else{
						isAdd = true;
					}
					if(isAdd){
						dbMsg += "[" + albumArtistLis.size() + "枚目]";
						dbMsg += album_artist;
						dbMsg += ",album=" + album;
						Map<String, String> albumArtistMap= new HashMap<>();
						albumArtistMap.put("album_artist" , album_artist);
						albumArtistMap.put("album" , album);
						albumArtistLis.add(albumArtistMap);
					}

				}
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void allSongArtistList(){
		final String TAG = "allSongArtistList";
		String dbMsg = "";
		try{
			if(0 < albumArtistLis.size()){
				dbMsg +=  dbMsg  +",albumArtistLis = " + albumArtistLis.size() + "件";


			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	//最近追加されたアルバム///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	String[] settingDays;	// = {"1","2", "3" ,"7","15", "30","60", "180", "365","730", "1800"};          //日分
	long saikinTuikaLimt = 0;
	/**
	 * 最近追加の抽出(指定IF)
	 *
	 * */
	public void saikin_tuika(){				//最近追加された楽曲の抽出(指定IF)
		final String TAG = "saikin_tuika]";
		String dbMsg = "";
		try{
			dbMsg +=  "pref_saikin_tuika=" + pref_saikin_tuika + ";" ;
			MuList.this.sousaSuu = Integer.valueOf(pref_saikin_tuika);
			dbMsg +=  ";現在" + MuList.this.sousaSuu + "日" ;
			settingDays = getResources().getStringArray(R.array.settingDays);											//plNameSL.toArray(new String[plNameSL.size()]);

			LayoutInflater inflater = LayoutInflater.from(MuList.this);
			View view = inflater.inflate(R.layout.number_picker_dlog, null, false);
			NumberPicker npd_np = view.findViewById(R.id.npd_np);			//ナンバーピッカー
			npd_np.setMaxValue(settingDays.length-1);         //最大値
			npd_np.setMinValue(1);			//最小
			npd_np.setValue( Integer.valueOf(pref_saikin_tuika));
			npd_np.setDisplayedValues(settingDays);
			npd_np.setOnValueChangedListener(new OnValueChangeListener() {			// 値が変化した時に通知を受け取るリスナーを登録する
				@Override
				public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
					final String TAG = "onValueChange";
					String dbMsg = "";
					try{
						dbMsg +=  "oldVal=" + oldVal + ">newVal>=" + newVal;
						MuList.this.sousaSuu = Integer.parseInt(MuList.this.settingDays[newVal - 1]);
						dbMsg +=  "=" + MuList.this.sousaSuu + "日";
						myLog(TAG, dbMsg);
					}catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}
			});
			AlertDialog.Builder Dlg = new AlertDialog.Builder(MuList.this);
			Dlg.setTitle(getResources().getString(R.string.menu_item_plist_saikin_tuika));			//最近追加リスト作成/編集
			Dlg.setMessage( getResources().getString(R.string.menu_item_plist_saikin_tuika_dm));	//最近追加されたアルバムを何枚抽出するか選択した下さい</string>
			Dlg.setIcon(R.drawable.ic_launcher);
			Dlg.setView(view) .setPositiveButton( getResources().getString(R.string.comon_kakutei),new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) {
					final String TAG = "onClick[saikin_tuika.MuList]";
					String dbMsg = "[MuList]which=" + which;
					try{
						dbMsg += "," + MuList.this.pref_saikin_tuika + "日の設定" ;
						MuList.this.pref_saikin_tuika = String.valueOf(MuList.this.sousaSuu);
						dbMsg +=  ">>" + MuList.this.sousaSuu + "日を指定";
						if( MuList.this.sousaSuu != Integer.parseInt( MuList.this.pref_saikin_tuika) ){
							MuList.this.pref_saikin_tuika = String.valueOf(MuList.this.sousaSuu);
							dbMsg +=  ">>" + MuList.this.pref_saikin_tuika + "日に変更";
							myEditor.putString( "pref_saikin_tuika", MuList.this.pref_saikin_tuika);
							boolean kakikomi = myEditor.commit();
							dbMsg +=",書き込み=" + kakikomi;	////////////////
						}
						myLog(TAG, dbMsg);
						saikin_tuika_Junbi( MuList.this.sousaSuu );				//最近追加された楽曲の抽出(指定IF)
					}catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}
			});

			Dlg.setNegativeButton(getResources().getString(R.string.comon_cyusi),	//中止</string>
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			Dlg.show();
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * 最近追加された楽曲の抽出 */
	public void saikin_tuika_Junbi( int listUpDates){				//最近追加された楽曲の抽出(指定IF)
		final String TAG = "saikin_tuika_Junbi";
		String dbMsg = "";
		try{
			mIndex = 1;								//リストの一件目に仮設定
			dbMsg +=  "、リストアップは=" + listUpDates + "日前から" ;
			if( plNameSL == null ){
				plNameSL = getPList();		//プレイリストを取得する
			}
			MuList.this.tuikaSakiListName = getResources().getString(R.string.playlist_namae_saikintuika);		//最近追加
			MuList.this.tuikaSakiListID = siteiListSakusi( MuList.this.tuikaSakiListName );
			dbMsg +=  "[" + tuikaSakiListID + "]" +MuList.this.tuikaSakiListName + "へ";
			final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", tuikaSakiListID);
			final String[] columns = null;			//{ idKey, nameKey };
			String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
			Cursor cursor0 = this.getContentResolver().query(uri, columns, null, null, c_orderBy );
			dbMsg += ",変更前"+ cursor0.getCount() +"件";
			int renint = getContentResolver().delete(uri , null , null);                //where = nullで全件削除
			dbMsg += ",renint"+ renint;
			cursor0 = this.getContentResolver().query(uri, columns, null, null, c_orderBy );
			dbMsg += ">>"+ cursor0.getCount() +"件";
			cursor0.close();

//			headKuseiDefault();								//ヘッドエリアの構成物調整

			saikintuikaDBname = getResources().getString(R.string.playlist_saikintuika_filename);
			dbMsg +=  "、ファイル=" + saikintuikaDBname ;
			File fileObj = new File(MuList.this.saikintuikaDBname);
			MuList.this.saikintuikaTablename = saikintuikaDBname.replace(".db" , "");	//最近追加のリストテーブル名
			dbMsg +=  "、テーブル=" + MuList.this.saikintuikaTablename ;

			MuList.this.tuikaSakiListName = getResources().getString(R.string.playlist_namae_saikintuika);		//最近追加
			MuList.this.nowList = MuList.this.tuikaSakiListName;
			//データベース作成/////////////////////////////////////////////////////////
			if(newSongHelper == null){
				newSongHelper = new ZenkyokuHelper(MuList.this , saikintuikaDBname);		//周防会作成時はここでonCreatが走る
			}
			if(newSongDb != null){
				dbMsg += " , isOpen=" + newSongDb.isOpen();
			}
			newSongDb = newSongHelper.getWritableDatabase();
			dbMsg += ">isOpen>" + newSongDb.isOpen();
			dbMsg += " , isReadOnly=" + newSongDb.isReadOnly();
			dbMsg += " , isWriteAheadLoggingEnabled=" + newSongDb.isWriteAheadLoggingEnabled();
			dbMsg += " , getPageSize=" + newSongDb.getPageSize();
			dbMsg += " , getMaximumSize=" + newSongDb.getMaximumSize();
			//>isOpen>true , isReadOnly=false , isWriteAheadLoggingEnabled=false , getPageSize=4096 , getMaximumSize=4398046507008 新規作成時
			//>isOpen>true , isReadOnly=false , isWriteAheadLoggingEnabled=false , getPageSize=4096 , getMaximumSize=4398046507008 , 既存=170レコード
			Cursor cursor = newSongDb.query(
					saikintuikaTablename,
					null,
					null,
					null,
					null,
					null,
					null
			);
		   if(cursor.moveToFirst()){
		   		int preexistCount =  cursor.getCount();
			   dbMsg += " , 既存=" + preexistCount + "レコード";
			   if(0 < preexistCount){
				   dbMsg += ">>作り直し";
				   newSongDb.execSQL("DROP TABLE IF EXISTS " + MuList.this.saikintuikaTablename);    			//既存のテーブルを削除して
				   newSongHelper.onCreate(newSongDb);
			   }
		   }else{
			   dbMsg += " ,初回作成=";
		   }
			cursor.close();
			myLog(TAG, dbMsg);
			saikin_tuika_mod( );		//MuList.this.sousaSuu
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 *  最近追加された楽曲の抽出 MediaStore.Audio.Media.EXTERNAL_CONTENT_URIからDATE_ADDEDが指定日以降の楽曲を抽出 */
	@SuppressLint("Range")
	public void saikin_tuika_mod(){				//最近追加された楽曲の抽出(指定IF)
		final String TAG = "saikin_tuika_mod";
		String dbMsg = "";
		try{
//	java.lang.NullPointerException: Attempt to invoke interface method 'boolean android.database.Cursor.isClosed()' on a null object reference

			if( cursor != null && ! cursor.isClosed() ){
				cursor.close();
			}
			this.listUpDates = Integer.valueOf(pref_saikin_tuika);
			dbMsg +=  "最低=" + listUpDates + "日前まで抽出させる" ;
			Date tDay = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(tDay);
			calendar.add(Calendar.DATE, -listUpDates);
			Date targetDate = calendar.getTime();
			dbMsg +=",targetDate="+ targetDate + "=" + sdffiles.format(targetDate);
			saikinTuikaLimt = targetDate.getTime() /1000;// / (24 * 60 * 60);  //1,558,969,836
			dbMsg +=",saikinTuikaLimt="+ saikinTuikaLimt ;

			MuList.this.sousalistName = getResources().getString(R.string.playlist_namae_saikintuika);
			sousalistID = siteiListSakusi( MuList.this.sousalistName );		//最近追加
			dbMsg += "、作成list[ID=" + tuikaSakiListID + "]"+ MuList.this.sousalistName;
			modifiedList = new ArrayList<String>();		//更新日→アルバム
			int listUpCount =0;
			Uri cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
			String[] c_columns = null;		 		//③引数columnsには、検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
			String c_selection = MediaStore.Audio.Media.DATE_ADDED +">=?" ;			//ファイル更新日
			String[] c_selectionArgs= {String.valueOf(saikinTuikaLimt)};   			//⑥引数groupByには、groupBy句を指定します。
			String c_orderBy =MediaStore.Audio.Media.DATE_ADDED   + " DESC " ;
			c_orderBy += " , " + MediaStore.Audio.Media.ALBUM+ " , " + MediaStore.Audio.Media.TRACK ;
//			c_orderBy += "," + MediaStore.Audio.Media.ARTIST;
			playLists = getContentResolver().query( cUri , c_columns , c_selection , c_selectionArgs, c_orderBy);
			int rCount = playLists.getCount();
			dbMsg +=";抽出"+ rCount + "曲";
			if(playLists.moveToFirst()){
				if(ORGUT == null){
					ORGUT = new OrgUtil();		//自作関数集
				}
				Util UTIL = new Util();
				String b_albumName = "";
				if(playLists.moveToFirst()){
					for (int i = 0; i < rCount; i++) {
						String albumName = playLists.getString(playLists.getColumnIndex(MediaStore.Audio.Media.ALBUM));
						if(!b_albumName.equals(albumName)){
							if(!ORGUT.isInListString(modifiedList ,albumName )){
								dbMsg += "\n(" + i + ")" + playLists.getString(playLists.getColumnIndex(MediaStore.Audio.Media.ARTIST));
								modifiedList.add(String.valueOf(albumName));
								dbMsg += "[" + modifiedList.size() + "]" + albumName;
//							   if(i == 0){
								UTIL.dBaceColumnCheck(playLists ,i);
//							   }
							}
							b_albumName = albumName;
						}
						playLists.moveToNext();
					}
				}
				MuList.this.plAL = new ArrayList<Map<String, Object>>();
				String pdTitol = getResources().getString(R.string.playlist_namae_saikintuika) +"" + getResources().getString(R.string.comon_sakusei);		//作成</string>
				int pdMaxVal = playLists.getCount();
				String pdMessage =  getResources().getString(R.string.comon_kakuninn) + ";" + pdMaxVal + getResources().getString(R.string.pp_kyoku);						//確認	曲
				dbMsg +=",reqCode="+ reqCode;
				reqCode = CONTEXT_saikintuika0;					//652;最近追加リストの記述
				dbMsg +=">>="+ reqCode;
				myLog(TAG,dbMsg);
				MuList.this.alabumName = "";
				MuList.this.modifide = "";
				plTask.execute(reqCode,playLists,pdTitol,pdMessage,playLists.getCount());
//				pTask = (plogTask) new plogTask(this ,  this ,pdTitol ,pdMessage, pdMaxVal ).execute(reqCode,  pdMessage , playLists ,pdTitol );		//,jikkouStep,totalStep,calumnInfo
			} else{
				String titolStr = getResources().getString(R.string.playlist_namae_saikintuika);		//最近追加
				String mggStr = listUpDates + "日前から追加された楽曲は有りません";
				Util.messageShow(titolStr , mggStr ,  MuList.this);
				myLog(TAG, dbMsg);
			}
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 *  最近追加されたアルバムの抽出  " DESC "で最新からソート */
	public Cursor saikin_tuika_mod_body(Cursor cursor){				//最近追加された楽曲の抽出
		final String TAG = "saikin_tuika_mod_body";
		String dbMsg = "";
		try{
			dbMsg += "["+ cursor.getPosition()  +"/"+ cursor.getCount() +"]";
			@SuppressLint("Range") String alabumName2 = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
			dbMsg +="alabumName="+ MuList.this.alabumName + ">>" + alabumName2;
			if(MuList.this.alabumName.equals(alabumName2)){
			}else{																										//アルバムが変わったら
				@SuppressLint("Range") String modifide2 = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));			//追加日を確認				変更日	DATE_ADDED
				dbMsg +=",次の更新日="+ modifide2;
				String mod2 = sdffiles.format(new Date(Long.valueOf(modifide2)*1000));
				MuList.this.alabumName = alabumName2;
				modifide2 = modifide2.substring(0, 7);
				dbMsg += "、日付比較"+ MuList.this.modifide + "と"+ modifide2;
				modifide2 = sdf.format(new Date(Long.valueOf(modifide2)*1000*1000));
				dbMsg += "="+modifide2 + "(equals=" + MuList.this.modifide.equals(modifide2) + ")";
				if( MuList.this.modifide.equals(modifide2)){												//日付が変わらなければ
					MuList.this.modifiedList.add(String.valueOf(alabumName2));
				} else {												//日付が変わったら
					MuList.this.listUpCount = MuList.this.modifiedList.size();
					dbMsg += "[カウント"+ MuList.this.listUpCount + "/" + MuList.this.sousaSuu +"]";
					if( MuList.this.sousaSuu < MuList.this.listUpCount){										//閾の枚数を超えたら
						cursor.moveToLast();																		//終端にジャンプ
						dbMsg += ">>cursor="+ cursor.getPosition();
					}else{
						MuList.this.modifide = modifide2;													//比較日付を変更
						MuList.this.modifiedList.add(String.valueOf(alabumName2));
					}
				}
			}
//			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return cursor;
	}

	public void saikin_tuika_mod_end(){				//最近追加された楽曲の抽出(継続処理)
		final String TAG = "saikin_tuika_mod_end";
		String dbMsg = "";
		try{
			if(playLists != null){
				playLists.close();
			}
			dbMsg += ",抽出結果=" + modifiedList;
			saikin_tuika_yomi( modifiedList);				//最近追加された楽曲の抽出(指定IF)
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 *  最近追加された楽曲の抽出(指定IF) */
	public void saikin_tuika_yomi( ArrayList<String> modifiedList){				//最近追加された楽曲の抽出(指定IF)
		final String TAG = "saikin_tuika_yomi";
		String dbMsg = "";
		try{
			dbMsg = "[" + MuList.this.sousalistID + "]" + MuList.this.sousalistName;
			int retInt = modifiedList.size();
			String pdTitol = getResources().getString(R.string.playlist_namae_saikintuika) +"" + getResources().getString(R.string.comon_sakuseicyuu);		//">作成中</string>
			String pdMessage =  getResources().getString(R.string.comon_kakuninn) + ";" + retInt + getResources().getString(R.string.common_nitibun);						//"">枚</string>
			MuList.this.plAL = new ArrayList<Map<String, Object>>();
			reqCode = CONTEXT_saikintuika_end;						//最近追加リストのプレイリスト作成   654
			int pdMaxVal = retInt;
			dbMsg += "," + pdMessage;
			plTask.execute(reqCode,null,pdTitol,pdMessage,pdMaxVal);
//			pTask = (plogTask) new plogTask(this ,  this ,pdTitol ,pdMessage,retInt ).execute(reqCode,  pdMessage , pdMaxVal  ,pdTitol );		//,jikkouStep,totalStep,calumnInfo
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で");
		}
	}

	/**
	 *  抽出されたアルバムごとの楽曲の書込み	20190908修正*/
	@SuppressLint("Range")
	public void saikin_tuika_yomi_body(int rIndex){				//最近追加された楽曲の抽出(指定IF)
		final String TAG = "saikin_tuika_yomi_body";
		String dbMsg = "";
		try{
			dbMsg +=  "抽出開始" + rIndex + "/" + MuList.this.modifiedList.size() + ")";
			String workTable = "work";
			int pOrder = MuList.this.plAL.size() + 1;
			String alabumName = MuList.this.modifiedList.get(rIndex);
			dbMsg += alabumName ;
			Uri cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
			String[] c_columns = null;		 		//③引数columnsには、検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
			String c_selection =  MediaStore.Audio.Media.ALBUM +" = ? ";			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
			String[] c_selectionArgs= {String.valueOf( alabumName ) };
//			String c_orderBy= MediaStore.Audio.Media._ID;			//MediaStore.Audio.Media.ALBUM + " , " + MediaStore.Audio.Media.TRACK ;		//降順はDESC
			String c_orderBy= MediaStore.Audio.Media.TRACK;	//では1,10,11,12...2,3　となる。リッピングの順番が収録順と一致する事に期待する 、
			Cursor cursor = getContentResolver().query( cUri , c_columns , c_selection , c_selectionArgs, c_orderBy);
			int rCount = cursor.getCount();
			dbMsg +=";"+ rCount + "件";
			Util UTIL = new Util();
			String albumArtist = UTIL.getAlbumArtist(cursor);
			dbMsg +=",albumArtist="+ albumArtist;
			String dataFN = getPrefStr( "pref_data_url" ,"" , MuList.this);
			ZenkyokuHelper workHelper = new ZenkyokuHelper(MuList.this , workTable);
			SQLiteDatabase workDb = workHelper.getWritableDatabase();
			workHelper.onCreate(workDb);
			String artistID = "";
			String alubmID = "";

			if(cursor.moveToFirst()){
				do{
					dbMsg +="\n["+ cursor.getPosition()  +"/"+ cursor.getCount() +"]";
					@SuppressLint("Range") String audioID = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
					dbMsg += "[audio_id="+ audioID + "]";
					int playOrder =cursor.getPosition() ;
					dbMsg += "を"+ playOrder + "曲目に";
					@SuppressLint("Range") String track = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK));
					dbMsg += "["+ track + "]";
//					track = UTIL.checKTrack( track);
//					dbMsg += ">>"+ track + "]";
					@SuppressLint("Range") String dataVal = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
					dbMsg += ",dataVal="+ dataVal;
					@SuppressLint("Range") String artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
					if(!artistName.equals(albumArtist)){
						dbMsg += "\n,artistName="+ artistName;
					}
					@SuppressLint("Range") String albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
					@SuppressLint("Range") String titolName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
					@SuppressLint("Range") String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
					@SuppressLint("Range") String year = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR));
					@SuppressLint("Range") String modified = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));
					@SuppressLint("Range") String composer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER));
					if(composer == null){
						composer = albumArtist;
					}
					@SuppressLint("Range") String lastYear = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR));				//13.MediaStore.Audio.Albums.LAST_YEAR
					if(0 == cursor.getPosition()){
						artistID = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
						alubmID = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
					}
					workDb.insert(workTable, null, UTIL.writetOneDBRecord( pOrder, audioID,albumArtist, artistName, albumName, titolName, dataVal, track, duration, year, modified, composer, lastYear) );////データベース書込み//
//					pStrlist += titolName + "," + dataVal +"\n";		//汎用プレイリストの内容

				}while(cursor.moveToNext());		// && listUpCount < listUpDates
			}
			cursor.close();
			cursor = workDb.query(workTable , null , null , null , null , null , c_orderBy);
			rCount = cursor.getCount();
			dbMsg +="\n;" + workTable + "に" + rCount + "件;trackで再ソート";

			if(cursor.moveToFirst()){
				do{
					dbMsg +="\n["+ cursor.getPosition()  +"/"+ cursor.getCount() +"]";
					@SuppressLint("Range") String audioID = cursor.getString(cursor.getColumnIndex("AUDIO_ID"));
					dbMsg += "[audio_id="+ audioID + "]";
					int playOrder = (pOrder + cursor.getPosition());
					dbMsg += "を"+ playOrder + "曲目に";
					String track = cursor.getString(cursor.getColumnIndex("TRACK"));
					dbMsg += "["+ track + "]";
					String dataVal = cursor.getString(cursor.getColumnIndex("DATA"));
					dbMsg += ",dataVal="+ dataVal;
					String artistName = cursor.getString(cursor.getColumnIndex("ARTIST"));
					if(!artistName.equals(albumArtist)){
						dbMsg += "\n,artistName="+ artistName;
					}
					String albumName = cursor.getString(cursor.getColumnIndex("ALBUM"));
					String titolName = cursor.getString(cursor.getColumnIndex("TITLE"));
					//リストの源泉を作る;CreatePLList；同様/////////////////////////////////////////////////////
					if(0 == playOrder){
						MuList.this.listTopFname = dataVal;							//リストの先頭ファイル名（プレイリスト作成時など）
					}
					String duration = cursor.getString(cursor.getColumnIndex("DURATION"));
					String year = cursor.getString(cursor.getColumnIndex("YEAR"));
					String modified = cursor.getString(cursor.getColumnIndex("MODIFIED"));
					String composer = cursor.getString(cursor.getColumnIndex("COMPOSER"));
					String lastYear = cursor.getString(cursor.getColumnIndex("LAST_YEAR"));

					MuList.this.plAL.add( UTIL.writetOneListRecord( playOrder, audioID, albumArtist,artistName, albumName, titolName, dataVal, track, duration, year, modified,artistID,alubmID) );
					//データベース書込み/////////////////////////////////////////////////
					newSongDb.insert(MuList.this.saikintuikaTablename, null,  UTIL.writetOneDBRecord( pOrder, audioID,albumArtist, artistName, albumName, titolName, dataVal, track, duration, year, modified, composer, lastYear) );
					/////////////////////////////////////////////////データベース書込み//
					if(dataVal.equals(dataFN)){ 				//再生中の曲が検出されたら
						MuList.this.mIndex = playOrder;
						dbMsg += "[mIndex="+ mIndex + "]";
					}
				}while(cursor.moveToNext());		// && listUpCount < listUpDates
			}
			cursor.close();
			workDb.execSQL("DROP TABLE IF EXISTS " + workTable);    			//作業のテーブルを削除

			dbMsg +=">現在>"+ MuList.this.plAL.size() + "件";
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * 最近追加された楽曲の書込み結果の表示 */
	public void saikin_tuika_yomi_end( ){				//最近追加された楽曲の書込み結果の表示
		final String TAG = "saikin_tuika_yomi_end";
		String dbMsg = "";
		try{
			boolean retBool = setPrefStr("nowList_id" , String.valueOf(nowList_id) , MuList.this);        //プリファレンスの読込み
			dbMsg += "," + nowList + " の　" + mIndex + " 番目　";
//			retBool = setPrefStr("nowList" , nowList , MuList.this);        //プリファレンスの読込み
			retBool = setPrefInt("pref_mIndex" , mIndex , MuList.this);        //プリファレンスの読込み
			dbMsg += "を書込み" + retBool + "]";
			if( 0 < mIndex){
				dbMsg += "listTopFname=" + MuList.this.listTopFname;							//リストの先頭ファイル名（プレイリスト作成時など）
				retBool = setPrefStr( "pref_data_url" , listTopFname , MuList.this);        //プリファレンスの読込み
				dbMsg += "を書込み" + retBool;
			}

			playListWr( CONTEXT_saikintuika_Wr);				//最近追加リストのファイル作成
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}


	/**
	 * *プレイリストのファイル書込み */
	public void playListWr(int reqCode){
		final String TAG = "playListWr";
		String dbMsg = "[MuList]reqCode=" + reqCode;                      //最近追加で651？
		try{
			dbMsg += ",現在のプレイリスト" + nowList_id + ":" + nowList;
			int retInt = 0;
			pStrlist ="";		//汎用プレイリストの内容を初期化
			if(plAL != null){
			}else{
				CreatePLList( nowList_id , nowList);
			}
			retInt = plAL.size();
			dbMsg += ",プログレスバーの最大値=" + retInt  + "件";					//[42529]content://media/external/audio/playlists/42529
			String pdTitol = MuList.this.sousalistName + getResources().getString(R.string.play_list_wr_msgi);	//ランダム再生準備</string>
//			if( reqCode == CONTEXT_saikintuika_Wr ){               								// 最近追加
//				if(MuList.this.sousalistName.equals(getResources().getString(R.string.all_songs_file_name))){				//全曲リスト
//					pdTitol = MuList.this.sousalistName + "update...";
////					allSongEnd();
//				} else if(MuList.this.sousalistName.equals(getResources().getString(R.string.playlist_namae_saikintuika))){	//最近追加
//					pdTitol = getResources().getString(R.string.menu_item_plist_saikin_tuika);
//				}
//			}
			String pdMessage =  getResources().getString(R.string.common_kakikomi) + ";" + retInt + getResources().getString(R.string.pp_kyoku);						// 書込み	曲
			//	reqCode = CONTEXT_rumdam_wr;									//ランダム再生リストの書込み
			int pdMaxVal = retInt;
			plTask.execute(reqCode,null,pdTitol,pdMessage,pdMaxVal);
//			pTask = (plogTask) new plogTask(this ,  this ,pdTitol ,pdMessage,retInt ).execute(reqCode,  pdMessage , pdMaxVal  ,pdTitol );		//,jikkouStep,totalStep,calumnInfo
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void playListWr_body(int i){
		final String TAG = "playListWr_body";
		String dbMsg = "";
		try{
			dbMsg +=  MuList.this.tuikaSakiListID + "に("+ ( i + 1 ) + "曲目)";/////////////////////////////////////
			int audio_id = Integer.valueOf( String.valueOf(MuList.this.plAL.get(i).get(MediaStore.Audio.Playlists.Members.AUDIO_ID )) );
			dbMsg += "audio_id=" + audio_id ;/////////////////////////////////////
			String dataVal = String.valueOf(MuList.this.plAL.get(i).get(MediaStore.Audio.Media.DATA));			//cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
			dbMsg += ",DATA="+ dataVal;
			Uri result_uri = musicPlaylist.addMusicToPlaylist( MuList.this.tuikaSakiListID, audio_id, dataVal);	//プレイリストへ曲を追加する
			dbMsg += ">>result_uri=" + result_uri ;/////////////////////////////////////
			String titolName = String.valueOf(MuList.this.plAL.get(i).get(MediaStore.Audio.Media.TITLE));
			dbMsg += ",titolName="+ titolName;
			pStrlist += titolName + "," + dataVal +"\n";		//汎用プレイリストの内容に追加
//			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * 最近追加された楽曲の書込み結果の表示 */
	public void saikin_tuika_end( ){				//最近追加された楽曲の書込み結果の表示
		final String TAG = "saikin_tuika_end";
		String dbMsg = "";
		try{
			int ePosition = 0;
			if(plNameSL != null){
				ePosition = plNameSL.indexOf(sousalistName);
			}
			dbMsg += ",ePosition=" + ePosition;
			pl_sp.setSelection(ePosition , false);								//☆falseで勝手に動作させない
			sousalistName = getResources().getString(R.string.playlist_namae_saikintuika);
			MuList.this.b_artist = "";
			MuList.this.b_album = "";
			pref_zenkyoku_list_id = getAllSongItems();
			dbMsg += " [全曲リスト； " + pref_zenkyoku_list_id + "]";/////////////////////////////////////
			CreatePLListEnd();				//プレイリストの内容取得
			dbMsg +=">CreatePLList>追加した曲=" + tuikaItemName;
			dbMsg +="、plAL=" + plAL.size();
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	@SuppressLint("Range")
	public void readSaikinTuikaDB(){				//最近追加された楽曲fairuyomiro
		final String TAG = "readSaikinTuikaDB";
		String dbMsg = "";
		try{
			if(playLists != null){
				playLists.close();
			}
			dbMsg += ",抽出結果=" + modifiedList;
			saikintuikaDBname = getResources().getString(R.string.playlist_saikintuika_filename);
			dbMsg +=  "、ファイル=" + saikintuikaDBname ;
			File fileObj = new File(MuList.this.saikintuikaDBname);
			MuList.this.saikintuikaTablename = saikintuikaDBname.replace(".db" , "");	//最近追加のリストテーブル名
			dbMsg +=  "、テーブル=" + MuList.this.saikintuikaTablename ;

			MuList.this.tuikaSakiListName = getResources().getString(R.string.playlist_namae_saikintuika);		//最近追加
			MuList.this.nowList = MuList.this.tuikaSakiListName;
			//データベース作成/////////////////////////////////////////////////////////
			if(newSongHelper == null){
				newSongHelper = new ZenkyokuHelper(MuList.this , saikintuikaDBname);		//周防会作成時はここでonCreatが走る
			}
			if(newSongDb != null){
				dbMsg += " , isOpen=" + newSongDb.isOpen();
			}
			newSongDb = newSongHelper.getReadableDatabase();
			dbMsg += ">isOpen>" + newSongDb.isOpen();
			dbMsg += " , isReadOnly=" + newSongDb.isReadOnly();
			dbMsg += " , isWriteAheadLoggingEnabled=" + newSongDb.isWriteAheadLoggingEnabled();
			dbMsg += " , getPageSize=" + newSongDb.getPageSize();
			dbMsg += " , getMaximumSize=" + newSongDb.getMaximumSize();
			//>isOpen>true , isReadOnly=false , isWriteAheadLoggingEnabled=false , getPageSize=4096 , getMaximumSize=4398046507008 新規作成時
			//>isOpen>true , isReadOnly=false , isWriteAheadLoggingEnabled=false , getPageSize=4096 , getMaximumSize=4398046507008 , 既存=170レコード
			Cursor cursor = newSongDb.query(saikintuikaTablename , null , null , null , null , null , null);
			if(cursor.moveToFirst()){
				Util UTIL = new Util();
				UTIL.dbColumnCheck(cursor ,0);

				int preexistCount =  cursor.getCount();
				dbMsg += " , 既存=" + preexistCount + "レコード";
				MuList.this.plAL = new ArrayList<Map<String, Object>>();
				int pOrder = MuList.this.plAL.size() + 1;
				do{
					MuList.this.objMap = new HashMap<String, Object>();
					dbMsg +="\n["+ cursor.getPosition()  +"/"+ cursor.getCount() +"]";
					@SuppressLint("Range") int audio_id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
					dbMsg += "[audio_id="+ audio_id + "]";
					int playOrder = (pOrder + cursor.getPosition());
					dbMsg += "を"+ playOrder + "曲目に";
//					String tuiukaItemeFn = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
					//リストの源泉を作る;CreatePLList；同様/////////////////////////////////////////////////////
					@SuppressLint("Range") String albumArtist = cursor.getString(cursor.getColumnIndex("ALBUM_ARTIST"));
					MuList.this.objMap.put("album_artist" ,albumArtist );
					@SuppressLint("Range") String ArtistName = cursor.getString(cursor.getColumnIndex("ARTIST"));
					dbMsg += ArtistName ;
					MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.ARTIST ,  ArtistName );
					@SuppressLint("Range") String AlbumName = cursor.getString(cursor.getColumnIndex("ALBUM"));
					MuList.this.objMap.put("album" ,AlbumName ); //					MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.ALBUM , AlbumName);
					MuList.this.objMap.put("title" ,cursor.getString(cursor.getColumnIndex("TITLE")) ); //					MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.TITLE , cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)) );
					@SuppressLint("Range") String dataVal = cursor.getString(cursor.getColumnIndex("DATA"));
					dbMsg += ",dataVal="+ dataVal;
					MuList.this.objMap.put("_data" ,dataVal );
					if(0 == playOrder){
						MuList.this.listTopFname = dataVal;							//リストの先頭ファイル名（プレイリスト作成時など）
					}
					String track = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK));
					track = UTIL.checKTrack( track);
					MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.TRACK , track );
					MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.DURATION ,  cursor.getString(cursor.getColumnIndex("DURATION")) );
					MuList.this.objMap.put("year" ,cursor.getString(cursor.getColumnIndex("YEAR")) );
					MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER , playOrder );
					MuList.this.plAL.add( objMap);
				}while(cursor.moveToNext());

				pref_zenkyoku_list_id = getAllSongItems();
				dbMsg += " [全曲リスト； " + pref_zenkyoku_list_id + "]";/////////////////////////////////////
				saikin_tuika_yomi_end( );
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG , dbMsg + "で" + e);
		}
	}

	public int saikin_sisei_kyokusuu;
//最近再生された楽曲////////////////////////////////////////////////////////////////////////////////////////////////////////////////////最近追加されたアルバム///
	/** 最近再生された楽曲(重複削除)*/
	public void saikin_sisei_jyunbi( int list_id ){				//最近再生された楽曲(重複削除)
		final String TAG = "saikin_sisei_jyunbi";
		String dbMsg = "";
		try{
			tuikaSakiListID = list_id;		//操作対象リストID
			tuikaSakiListName= getResources().getString(R.string.playlist_namae_saikinsisei);			//最近再生
			dbMsg += "[" + tuikaSakiListID + "]" + tuikaSakiListName ;
			if( playLists != null ){
				if(! playLists.isClosed()){
					playLists.close();
				}
			}
			Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", list_id);
			String[] columns =null;
			String c_selection = null;			//MediaStore.Audio.Playlists.NAME +" = ? ";
			String[] c_selectionArgs= null;			//{ String.valueOf(listMei) };		//⑥引数groupByには、groupBy句を指定します。
			String c_order = MediaStore.Audio.Playlists.Members.PLAY_ORDER  + " DESC";
			playLists = this.getContentResolver().query(uri, columns, c_selection, c_selectionArgs, c_order );
			int koumoku = playLists.getCount();
			dbMsg += "最近再生リストの曲数=" + koumoku ;////////////////////////////////////////////////////////////////////////////
				if( playLists.moveToFirst() ){
					reqCode = CONTEXT_saikin_sisei0 ;				//最近再生リストの重複削除
					dbMsg += ",reqCode="+reqCode;
					String pdTitol = getResources().getString(R.string.menu_item_plist_saikin_saisei_dt);				//最近再生された楽曲
					int retInt = koumoku;
					String pdMessage = getResources().getString(R.string.menu_item_plist_saikin_saisei_dm0);		//     <string name="">重複削除</string>
					myLog(TAG,dbMsg);
					plTask.execute(reqCode,playLists,pdTitol,pdMessage,playLists.getCount());
//					pTask = (plogTask) new plogTask(this ,  this ,pdTitol ,pdMessage,retInt ).execute(reqCode,  pdMessage , playLists ,pdTitol );		//,jikkouStep,totalStep,calumnInfo
				} else {
					saikin_sisei_jyunbi_end();
				}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void saikin_sisei_jyunbi_end(){				//最近再生された楽曲(重複削除)
		final String TAG = "saikin_sisei";
		String dbMsg = "";
		try{
			 dbMsg +=  playLists.getCount()+"件" ;
			playLists.close();
			saikin_sisei( MuList.this.sousalistID );				//最近再生された楽曲
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/** 最近再生された楽曲 */
	public void saikin_sisei( int list_id ){				//最近再生された楽曲
		final String TAG = "saikin_sisei";
		String dbMsg = "";
		try{
			String listMei = getResources().getString(R.string.playlist_namae_saikinsisei);			//最近再生
			dbMsg += "[" + list_id + "]" + listMei ;
			if( playLists != null ){
				if(! playLists.isClosed()){
					playLists.close();
				}
			}
			Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", list_id);
			String[] columns =null;
			String c_selection = null;			//MediaStore.Audio.Playlists.NAME +" = ? ";
			String[] c_selectionArgs= null;			//{ String.valueOf(listMei) };		//⑥引数groupByには、groupBy句を指定します。
			String c_order = MediaStore.Audio.Playlists.Members.PLAY_ORDER ;
			playLists = this.getContentResolver().query(uri, columns, c_selection, c_selectionArgs, c_order);
			int koumoku = playLists.getCount();
			dbMsg += "最近再生リストの曲数=" + koumoku ;////////////////////////////////////////////////////////////////////////////
			int saikin_sisei_kyokusuu = Integer.valueOf(pref_saikin_sisei);
			dbMsg +=  "/デフォルト曲数=" + saikin_sisei_kyokusuu ;////////////////////////////////////////////////////////////////////////////
			if( saikin_sisei_kyokusuu < koumoku ){
				if( playLists.moveToFirst() ){
					reqCode = CONTEXT_saikin_sisei ;			//最近再生リストの準備
					dbMsg += ",reqCode="+reqCode;
					String pdTitol = getResources().getString(R.string.menu_item_plist_saikin_saisei_dt);				//最近再生された楽曲
					int retInt = koumoku;
					String pdMessage = getResources().getString(R.string.menu_item_plist_saikin_saisei_dm) +"\n" +
											koumoku + ">>" + saikin_sisei_kyokusuu + getResources().getString(R.string.common_yomitori);		// 指定曲数に調整しています。</string>
					plTask.execute(reqCode,playLists,pdTitol,pdMessage,playLists.getCount());
//					pTask = (plogTask) new plogTask(this ,  this ,pdTitol ,pdMessage,retInt ).execute(reqCode,  pdMessage , playLists ,pdTitol );		//,jikkouStep,totalStep,calumnInfo
				} else {
					 saikin_sisei_end();				//最近再生された楽曲の抽出(継続処理)
				}
			}else {
				 saikin_sisei_end();				//最近再生された楽曲の抽出(継続処理)
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void saikin_sisei_end(){				//最近再生された楽曲の抽出(継続処理)
		final String TAG = "saikin_sisei_end";
		String dbMsg = "";
		try{
			if( playLists != null ){
				playLists.close();
				playOrderSyuusei(MuList.this.sousalistID , 0);				//再生順を振りなおす
			}
			String pdMessage = getResources().getString(R.string.menu_item_plist_saikin_saisei_dt);		// 最近再生された楽曲
			CreatePLList(Long.valueOf(MuList.this.sousalistID) , pdMessage);		//プレイリストの内容取得		 MuList.this.sousalist_data,
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}
//ランダム再生////////////////////////////////////////////////////////////////////////////////////////////////////////////////////最近追加されたアルバム///
	/** ランダム再生  最近再生した曲のaudio_idをplSLに書き込む。  次段処理でrandumPlay2で乱数で引き当てたaudio_idを追記する */
	@SuppressLint("Range")
	public void randumPlay(){			//ランダム再生
		final String TAG = "randumPlay";
		String dbMsg = "";
		try{
			MuList.this.plSL =  new ArrayList<String>();				//プレイリスト用簡易リスト
			MuList.this.plSL.clear();
			int tuikaSakiListID = -1;
			tuikaSakiListName = getResources().getString(R.string.playlist_namae_saikinsisei);			//最近再生
			Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
			String[] columns = null;				//{ idKey, nameKey };
			String c_selection = MediaStore.Audio.Playlists.NAME +" = ? ";
			String[] c_selectionArgs= {tuikaSakiListName};		//⑥引数groupByには、groupBy句を指定します。
			playLists = this.getContentResolver().query(uri, columns, c_selection, c_selectionArgs, null);
			if( playLists.moveToFirst() ){
				tuikaSakiListID = Integer.valueOf(playLists.getString(playLists.getColumnIndex(MediaStore.Audio.Playlists._ID)));		//操作対象リストID
				dbMsg += "読み込むリスト[" + tuikaSakiListID + "]" + tuikaSakiListName ;
			}
			playLists.close();
			if( 0 < tuikaSakiListID ){
				uri = MediaStore.Audio.Playlists.Members.getContentUri("external", tuikaSakiListID);
				c_selection = null;			//MediaStore.Audio.Playlists.NAME +" = ? ";
				String c_order = MediaStore.Audio.Playlists.Members.PLAY_ORDER  + " DESC";
				playLists = this.getContentResolver().query(uri, columns, c_selection, null, c_order );
				int koumoku = playLists.getCount();
				dbMsg += ";曲数=" + koumoku ;////////////////////////////////////////////////////////////////////////////
				if( playLists.moveToFirst() ){
					dbMsg += "["+ playLists.getPosition() +"/" + playLists.getCount() + "]";
					reqCode = CONTEXT_rumdam_saiseizumi ;				//642  ランダム再生準備
					dbMsg += ",reqCode="+reqCode;
					String pdTitol = getResources().getString(R.string.menu_item_plist_randam_dm);			//ランダム再生準備
					int retInt = koumoku;
					String pdMessage = getResources().getString(R.string.menu_item_plist_randam_dm0);		//最近再生した曲を確認しています。
					plTask.execute(reqCode,playLists,pdTitol,pdMessage,playLists.getCount());
//					plogTask pTask = (plogTask) new plogTask(this ,  this ,pdTitol ,pdMessage,retInt ).execute(reqCode,  pdMessage , playLists ,pdTitol );		//,jikkouStep,totalStep,calumnInfo
				} else {
					randumPlay_make();	//ランダム再生;リスト作成
				}
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/** ランダム再生;リスト作成:* 既存が有れば内容消去: ランダム再生リストの情報をsousalistName、sousalistIDに書き込む*/
	public void randumPlay_make(){			//ランダム再生;リスト作成
		final String TAG = "randumPlay_make";
		String dbMsg = "";
		try{
			tuikaSakiListName = getResources().getString(R.string.playlist_namae_randam);		//操作対象リスト名	ランダム再生</string>
			MuList.this.tuikaSakiListName = tuikaSakiListName;
			tuikaSakiListID = siteiListSakusi( tuikaSakiListName);				//指定された名称のリストを作成する
			dbMsg +=  "[" + tuikaSakiListID + "] "+tuikaSakiListName ;
			Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", tuikaSakiListID);
			String[] columns =null;
			String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
			Cursor cursor = this.getContentResolver().query(uri, columns, null, null, c_orderBy );
			dbMsg += ","+cursor.getCount() +"件";
			if( cursor.moveToFirst() ){
				reqCode = CONTEXT_rumdam_redrow;		//ランダム再生リストの消去
				deletPlayListLoop(reqCode ,  MuList.this.tuikaSakiListID);			//指定したプレイリストを削除する
			}else{
				randumPlay2();
			}
			cursor.close();
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public Cursor cursor;
	public int saiseiZumiSuu = 0;					//最近再生した曲数
	public int  jyougenn;
	/**ランダム再生;ランダム追加: 全曲リストの曲数を最大rundam_list_sizeで設定した曲数を上限に乱数でレコードを指定してリストデータ生成:randumPlay_array_bodyで加算 */
	public void randumPlay2(){			//ランダム再生;ランダム追加
		final String TAG = "randumPlay2";
		String dbMsg = "";
		try{
			playLists.close();
			dbMsg +=  "最近再生";	//+MuList.this.plSL;
			saiseiZumiSuu =MuList.this.plSL.size();
			dbMsg +=  "="+saiseiZumiSuu + "件";
			Uri cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
			String c_selection =  MediaStore.Audio.Media.IS_MUSIC +" <> ?" ;
			String[] c_selectionArgs= {"0" };   			//, null , null , null
			String c_orderBy=MediaStore.Audio.Media._ID; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
			Cursor cursor = this.getContentResolver().query( cUri , null , c_selection , c_selectionArgs, c_orderBy);
			int kyoku = cursor.getCount();									//redrowIDList.size();
			dbMsg += ",全曲数="+kyoku +"曲で";
			int kagen = 0;
			jyougenn = kyoku;
			dbMsg +="、範囲" + kagen  +"～"+ jyougenn ;//////////////////
			int adEnd = saiseiZumiSuu;
			int rundam_list_size = Integer.valueOf(pref_rundam_list_size) + 1;
			if(kyoku <rundam_list_size){					//ランダム再生リストアップ曲数
				adEnd = adEnd+ kyoku;
			}else {
				adEnd = adEnd+ rundam_list_size;
			}
			cursor.close();
			MuList.this.plAL = new ArrayList<Map<String, Object>>();
			plAL.clear();
			int retInt = rundam_list_size;
			dbMsg += ",ランダム再生リストアップ曲数設定=" + retInt  + "件";			//[42529]content://media/external/audio/playlists/42529
			String pdTitol = getResources().getString(R.string.menu_item_plist_randam_dm);					//ランダム再生準備</string>
			String pdMessage =  getResources().getString(R.string.menu_item_plist_randam_dm1) + ";" + (retInt - 1 ) + getResources().getString(R.string.pp_kyoku);			//乱数生成		曲
			reqCode = CONTEXT_rumdam_arrayi;									//ランダム再生;配列書込み
			int pdMaxVal = retInt;
			plTask.execute(reqCode,null,pdTitol,pdMessage,pdMaxVal);
//			pTask = (plogTask) new plogTask(this ,  this ,pdTitol ,pdMessage,retInt ).execute(reqCode,  pdMessage , pdMaxVal  ,pdTitol );		//,jikkouStep,totalStep,calumnInfo
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	@SuppressLint("Range")
	public int randumPlay_array_body(int i){			//ランダム再生;ランダム追加
		final String TAG = "randumPlay_array_body";
		String dbMsg = "";
		try{
			MuList.this.objMap = new HashMap<String, Object>();
			int kagen = 0;
			float retFl =(float) (Math.floor(Math.random() * (jyougenn - kagen  )) + kagen);				//(jyougenn - kagen + 1)) + kagen);
			dbMsg +=">>>"+ retFl ;//////////////////
			int gyou = (int) (retFl+0.5);
			dbMsg += ",行=" + gyou ;///////////////////////////////////
			Uri cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
			String c_selection =  MediaStore.Audio.Media.IS_MUSIC +" <> ?" ;
			String[] c_selectionArgs= {"0" };   			//, null , null , null
			String c_orderBy=MediaStore.Audio.Media._ID; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
			Cursor cursor2 = this.getContentResolver().query( cUri , null , c_selection , c_selectionArgs, c_orderBy);
			if(cursor2.moveToPosition(gyou)){
				@SuppressLint("Range") String tStr = cursor2.getString(cursor2.getColumnIndex( MediaStore.Audio.Media._ID));
				dbMsg += "(" + MuList.this.plSL.size() +")" + tStr  ;///////////////////////////////////
				if(! ORGUT.isInListString(MuList.this.plSL , String.valueOf(tStr))){		//渡された文字が既にリストに登録されていればtrueを返す
					MuList.this.plSL.add(String.valueOf(tStr));
					MuList.this.objMap.put(MediaStore.Audio.Media._ID ,String.valueOf(tStr) );
					tStr = cursor2.getString(cursor2.getColumnIndex( MediaStore.Audio.Media.DATA));
					MuList.this.objMap.put(MediaStore.Audio.Media.DATA ,String.valueOf(tStr) );
					MuList.this.plAL.add( objMap);
//				}else{
//					i--;
				}
				dbMsg += "→" + i +"曲目)" ;///////////////////////////////////
			}
			i = plAL.size();
			cursor2.close();
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return i;
	}

	public void randumPlayListWr(){			//ランダム再生;リストに書き込む
		final String TAG = "randumPlayListWr";
		String dbMsg = "";
		try{
			int retInt = plAL.size();
			dbMsg += ",plAL=" + retInt  + "件";			//[42529]content://media/external/audio/playlists/42529
			String pdTitol = getResources().getString(R.string.menu_item_plist_randam_dm);					//ランダム再生準備</string>
			String pdMessage =  getResources().getString(R.string.common_kakikomi) + ";" + retInt + getResources().getString(R.string.pp_kyoku);						// 書込み	曲
			reqCode = CONTEXT_rumdam_wr;									//ランダム再生リストの書込み
			int pdMaxVal = retInt;
			plTask.execute(reqCode,null,pdTitol,pdMessage,pdMaxVal);
//			pTask = (plogTask) new plogTask(this ,  this ,pdTitol ,pdMessage,retInt ).execute(reqCode,  pdMessage , pdMaxVal  ,pdTitol );		//,jikkouStep,totalStep,calumnInfo
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void randumPlayListWr_body(int i){			//ランダム再生;リストに書き込む
		final String TAG = "randumPlayListWr_body";
		String dbMsg = "";
		try{
			dbMsg +=  MuList.this.tuikaSakiListID + "に("+ ( i + 1 ) + "曲目)";/////////////////////////////////////
			int audio_id = Integer.valueOf( String.valueOf(MuList.this.plAL.get(i).get( MediaStore.Audio.Media._ID)) );
			dbMsg += "audio_id=" + audio_id ;/////////////////////////////////////
			String DATA = String.valueOf(MuList.this.plAL.get(i).get( MediaStore.Audio.Media.DATA));			//cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
			dbMsg += ",DATA="+ DATA;
			Uri result_uri = musicPlaylist.addMusicToPlaylist(MuList.this.tuikaSakiListID, audio_id, DATA );	//プレイリストへ曲を追加する
			dbMsg += ">>result_uri=" + result_uri ;/////////////////////////////////////
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/** ランダム再生/リピート再生;プレイヤーに戻る	*/
	public void makedListBack(){			//ランダム再生;プレイヤーに戻る
		final String TAG = "makedListBack";
		String dbMsg = "";
		try{
			dbMsg +=  "リスト[" + MuList.this.tuikaSakiListID + "]" + MuList.this.tuikaSakiListName;			//[42529]content://media/external/audio/playlists/42529
			Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", MuList.this.tuikaSakiListID);
			String[] columns =null;
			String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
			cursor = this.getContentResolver().query(uri, columns, null, null, c_orderBy );
			if( cursor.moveToFirst() ){
				@SuppressLint("Range") String dataFN = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.DATA));
				dbMsg += ",一曲目;dataFN=" + dataFN;			//[42529]content://media/external/audio/playlists/42529
				sousalistID = tuikaSakiListID;		//最終操作設定値へ
				sousalistName = tuikaSakiListName;
				setPrefStr( "pref_data_url" ,  dataFN , MuList.this);
				setPrefInt("pref_position" ,  0 , MuList.this);
				back2Player( dataFN);												//プレイヤーにuriを送る
			}

			cursor.close();
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

//リピート再生////////////////////////////////////////////////////////////////////////////////////////////////////////////////////ランダム再生///
	/** リピート再生: Cursor に対象データ格納:既にプレイリスト化されていたら既存の内容消去: 無ければ新規作成*/
	public void repeatPlay(){			//リピート再生
		final String TAG = "repeatPlay";
		String dbMsg = "";
		try{
			dbMsg +=  "開始前のreqCode = " + reqCode;						//リスト消去に入る前に保留
			b_reqCode= reqCode;							//処理コードの保留
			tuikaSakiListName = getResources().getString(R.string.playlist_namae_repeat);		//リピート再生</string>
			MuList.this.tuikaSakiListName = tuikaSakiListName;
			repeatType = reqCode;
			reqCode = CONTEXT_REPET_CLEAN;						//リピート再生リストの既存レコード消去
			tuikaSakiListID = siteiListSakusi( tuikaSakiListName);				//指定された名称のリストを作成する
			dbMsg +=  "[" + tuikaSakiListID + "] "+tuikaSakiListName ;
			Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", tuikaSakiListID);
			String[] columns =null;
			String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
			Cursor cursor = this.getContentResolver().query(uri, columns, null, null, c_orderBy );
			dbMsg += ","+cursor.getCount() +"件";
			if( cursor.moveToFirst() ){
				reqCode = CONTEXT_REPET_CLEAN;						//リピート再生リストの既存レコード消去
				deletPlayListLoop(reqCode ,  MuList.this.tuikaSakiListID);			//指定したプレイリストを削除する
			}else{
				repeatPlayMake();
			}
			cursor.close();
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/** リピート再生 */
	public void repeatPlayMake(){			//リピート再生
		final String TAG = "repeatPlayMake";
		String dbMsg = "";
		try{
			reqCode = b_reqCode;							//処理コードの保留
			dbMsg +=  "reqCode="+ reqCode;
			String fn = getString(R.string.zenkyoku_file);			//全曲リスト名
			dbMsg += "(Zenkyoku_db;fn=" + fn;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
			zenkyokuHelper = new ZenkyokuHelper(MuList.this , fn);		//全曲リストの定義ファイル		.
			Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
			dbMsg += ">isOpen>" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
			dbMsg += ",getPageSize=" + Zenkyoku_db.getPageSize() + "件)" ;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
			String zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
			String[] c_columns =null;					//②引数tableには、テーブル名を指定します。
			String c_selection = "ALBUM_ARTIST LIKE ? ";
			String[] c_selectionArgs= {"%" + albumArtist + "%"  };
			String c_orderBy= "_id , ALBUM , TRACK";			// ""; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
			cursor = Zenkyoku_db.query(zenkyokuTName, c_columns, c_selection, c_selectionArgs , null, null, c_orderBy);	// table, columns,new String[] {MotoN, albamN}
			dbMsg +=albumArtist+ "は"+ cursor.getCount() + "件";
			if( cursor.moveToFirst() ){
				zenkyokuAri = true;			//全曲リスト有り
				dbMsg += ",アルバム="+ albumName;
				switch(reqCode) {
				case MyConstants.rp_album:			//2131558548 アーティストリピート指定ボタン
					c_selection = "ALBUM_ARTIST LIKE ? AND ALBUM = ?";
					String[] c_selectionArgs3= {"%" + albumArtist + "%" , albumName };
					c_orderBy= "TRACK"; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
					cursor = Zenkyoku_db.query(zenkyokuTName, c_columns, c_selection, c_selectionArgs3 , null, null, c_orderBy);	// table, columns,new String[] {MotoN, albamN}
					dbMsg += "は"+ cursor.getCount() + "件";
					break;
				case MyConstants.rp_titol:			//2131558549 タイトルリピート指定ボタン
				case MyConstants.rp_point:			//2131558550 二点間リピート指定ボタン
					dbMsg += ",タイトル="+ titolName;
					c_selection = "ALBUM_ARTIST LIKE ? AND ALBUM = ? AND TITLE = ?";
					String[] c_selectionArgs4= {"%" + albumArtist + "%" , albumName , titolName };
					c_orderBy=null;
					cursor = Zenkyoku_db.query(zenkyokuTName, c_columns, c_selection, c_selectionArgs4 , null, null, c_orderBy);	// table, columns,new String[] {MotoN, albamN}
					dbMsg += "は"+ cursor.getCount() + "件";
					break;
//				default:
//					break;
				}
			}else{
				zenkyokuAri = false;			//全曲リスト有り
				dbMsg += " , " + albumArtist+ "は";
				Uri cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
				c_columns = null;		 		//③引数columnsには、検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
				c_selection = MediaStore.Audio.Media.ARTIST + " LIKE ?";			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
				String[] c_selectionArgs2= {"%" + albumArtist + "%" };   			//音楽と分類されるファイルだけを抽出する
				c_orderBy= MediaStore.Audio.Media.YEAR + " , " + MediaStore.Audio.Media.ALBUM  + " , " + MediaStore.Audio.Media.TRACK  ; 				// + MediaStore.Audio.Media.YEAR  + " DESC , "	降順はDESC	☆"album_artist"は拾えない
				cursor = getContentResolver().query( cUri ,c_columns, c_selection, c_selectionArgs2, c_orderBy) ;
				dbMsg += "、MediaStoreに"+ cursor.getCount() + "件";
				if( cursor.moveToFirst() ){
					dbMsg += ",アルバム="+ albumName;
					switch(reqCode) {
		//			case MyConstants.rp_artist:			//2131558547 アーティストリピート指定ボタン
					case MyConstants.rp_album:			//2131558548 アーティストリピート指定ボタン
						c_selection = MediaStore.Audio.Media.ARTIST + " LIKE ? AND " + MediaStore.Audio.Media.ALBUM + " = ?";
						String[] c_selectionArgs3= {"%" + albumArtist + "%" , albumName };
						c_orderBy=MediaStore.Audio.Media.TRACK;			// "TRACK"; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
						cursor = getContentResolver().query( cUri ,c_columns, c_selection, c_selectionArgs3, c_orderBy) ;
						dbMsg += "は"+ cursor.getCount() + "件";
						break;
					case MyConstants.rp_titol:			//2131558549 タイトルリピート指定ボタン
					case MyConstants.rp_point:			//2131558550 二点間リピート指定ボタン
						dbMsg += ",タイトル="+ titolName;
						c_selection = MediaStore.Audio.Media.ARTIST + " LIKE ? AND " + MediaStore.Audio.Media.ALBUM + " = ? AND " + MediaStore.Audio.Media.TITLE + " = ?";
						String[] c_selectionArgs4= {"%" + albumArtist + "%" , albumName , titolName };
						c_orderBy=null;
						cursor = getContentResolver().query( cUri ,c_columns, c_selection, c_selectionArgs4, c_orderBy) ;
						dbMsg += "は"+ cursor.getCount() + "件";
						break;
	//					default:
	//						break;
					}
				}
			}
			dbMsg += ";;結果="+ cursor.getCount() + "件";
			if( cursor.moveToFirst() ){
				dbMsg += ",tuikaSakiListName="+ tuikaSakiListName;
				tuikaSakiListName = getResources().getString(R.string.playlist_namae_repeat) ;		//リピート再生	makedListBackで全曲リストにされているので
				dbMsg += ">>"+ tuikaSakiListName;
				String pdTitol = getResources().getString(R.string.playlist_namae_repeat) +"" + getResources().getString(R.string.comon_sakusei);		//リピート再生	作成
				int retInt = cursor.getCount();
				String pdMessage =  getResources().getString(R.string.common_kakikomi) + ";" + retInt + getResources().getString(R.string.pp_kyoku);		//書込み　曲
				dbMsg += ",pdMessage=" + pdMessage;			//fastItemeFn=/storage/sdcard0/Music/Jimmy Cliff/Follow My Mind/07 Remake The World.wma
				retInt = cursor.getCount();
				reqCode = CONTEXT_REPET_WR;								//リピート再生リストレコード書込み
				plTask.execute(reqCode,cursor,pdTitol,pdMessage,cursor.getCount());
//				pTask = (plogTask) new plogTask(this ,  this ,pdTitol ,pdMessage,retInt ).execute(reqCode,  pdMessage , cursor ,pdTitol );		//,jikkouStep,totalStep,calumnInfo
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/** リピート再生:* 渡されたCursorからaudio_idと曲のURLを読み出し: addMusicToPlaylistでリピート再生に書き込む */
	@SuppressLint("Range")
	public Cursor repeatPlayMake_body(Cursor cursor){			//リピート再生
		final String TAG = "repeatPlayMake_body";
		String dbMsg = "";
		try{
			dbMsg +=  "[" + cursor.getPosition()  +"/"+ cursor.getCount() +"]";
			int audio_id = 0;
			String tuiukaItemeFn = null;
			dbMsg += "、zenkyokuAri="+ zenkyokuAri;
			if( zenkyokuAri ){			//				 = true;			//全曲リスト有り
				audio_id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("AUDIO_ID")));
				dbMsg += "[audio_id="+ audio_id + "]";
				tuiukaItemeFn = String.valueOf(cursor.getString(cursor.getColumnIndex("DATA")));
				dbMsg += tuiukaItemeFn;
			}else{
				audio_id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
				dbMsg += "[audio_id="+ audio_id + "]";
				if( -1 < cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)){
					tuiukaItemeFn = String.valueOf(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
					dbMsg += tuiukaItemeFn;
				}
			}
			Uri result_uri = musicPlaylist.addMusicToPlaylist( tuikaSakiListID, audio_id, tuiukaItemeFn);	//プレイリストへ曲を追加する
			dbMsg += "、書込み"+ result_uri;	// musicPlaylist.
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return cursor;
	}

	//リクエストリスト////////////////////////////////////////////////////////////////////////////////////////////////////////////////////リピート再生///
	boolean requestSugu = false;				//すぐに再生
	boolean requestJikkoucyuu = false;		//リクエスト実行中
	/** リクエストリスト作成*/
	@SuppressLint("Range")
	public void requestList(String itemStr){								//リクエストリスト作成
		final String TAG = "requestList";
		String dbMsg = "";
		try{
			dbMsg +=   "nowList["+ nowList_id + "]"+ nowList +  "sousalist["+ sousalistID + "]"+ sousalistName;/////////////////////////////////////
			dbMsg +="artist=" + sousa_artist+ ",sousa_alubmArtist=" + sousa_alubmArtist+ ",alubm=" + sousa_alubm;/////////////////////////////////////
			dbMsg +=  ",itemStr=" + itemStr;
			dbMsg +=  ",操作対象レコードのUrl=" + sousaRecordUrl;
			b_reqCode=reqCode;							//処理コードの保留
			if( MuList.this.sousalistName.equals(getResources().getString(R.string.listmei_zemkyoku)) ){				// 全曲リスト
				Uri cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
				String[] c_columns = null;		 		//③引数columnsには、検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
				String c_selection = MediaStore.Audio.Media.ARTIST + " LIKE ? AND " + MediaStore.Audio.Media.ALBUM + " = ? AND " + MediaStore.Audio.Media.TITLE + " = ? ";			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
				String[] c_selectionArgs= {"%" + sousa_artist + "%", sousa_alubm , itemStr };   			//音楽と分類されるファイルだけを抽出する
				String c_orderBy= null;			//MediaStore.Audio.Media.YEAR + " , " + MediaStore.Audio.Media.ALBUM  + " , " + MediaStore.Audio.Media.TRACK  ; 				// + MediaStore.Audio.Media.YEAR  + " DESC , "	降順はDESC	☆"album_artist"は拾えない
				Cursor cursor = getContentResolver().query( cUri ,c_columns, c_selection, c_selectionArgs, c_orderBy) ;
				dbMsg += "、MediaStoreに"+ cursor.getCount() + "件";
				if( cursor.moveToFirst() ){
					audio_id = Integer.valueOf(String.valueOf(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))));
					dbMsg += ">>[" + audio_id;
					sousaRecordUrl = String.valueOf(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
					dbMsg += "]" + sousaRecordUrl;
				}
				cursor.close();
			} else {
				Cursor cursor = list_dataUMU( sousalistID , MediaStore.Audio.Playlists.Members.TITLE , itemStr);				//指定した名前のリスト指定した項目のデータが有ればカーソルを返す
				dbMsg += "、" + sousalistName + "に"+ cursor.getCount() + "件";
				if( cursor.moveToFirst() ){
					audio_id = Integer.valueOf(String.valueOf(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID))));
					dbMsg += ">>[" + audio_id;
					sousaRecordUrl = String.valueOf(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA)));
					dbMsg += "]" + sousaRecordUrl;
					sousa_artist = String.valueOf(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST)));
					dbMsg += "," + sousa_artist;
					sousa_alubm = String.valueOf(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM)));
					dbMsg += "." + sousa_alubm;
				}
				cursor.close();
			}
			itemStr = sousa_artist +"\n" + sousa_alubm +"\n" + itemStr;
			AlertDialog.Builder builder = new AlertDialog.Builder(this);		//AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//	LayoutInflater inflater = getLayoutInflater();
			View view = inflater.inflate(R.layout.list_shyuusei,null);
			builder.setTitle(getResources().getString(R.string.list_contex_add_request));				//リクエスト
			builder.setMessage(getResources().getString(R.string.request_msg));				//リクエストした曲でプレイリストを作成します。
			final View lEditDlog = inflater.inflate(R.layout.list_shyuusei,null);				//(ViewGroup)findViewById(R.id.alertdialog_layout)
			builder.setView(lEditDlog);
			TextView le_msg_tv = lEditDlog.findViewById(R.id.le_msg_tv);
			le_saki_et = lEditDlog.findViewById(R.id.le_saki_et);						//final EditText
			le_saki_et.setVisibility(View.GONE);
			final CheckBox le_cb = lEditDlog.findViewById(R.id.le_cb);
			le_msg_tv.setText(itemStr);
			dbMsg += "、nowList="+ MuList.this.nowList + ",tuikaSakiListName=" +  MuList.this.tuikaSakiListName;
			if( MuList.this.nowList.equals(getResources().getString(R.string.playlist_namae_request))){				// );		//リクエストリスト
				requestJikkoucyuu = true;
			}else if(MuList.this.tuikaSakiListName != null){
				if(MuList.this.tuikaSakiListName.equals(getResources().getString(R.string.playlist_namae_request))){
					requestJikkoucyuu = true;
				}
			} else {
				requestJikkoucyuu = false;
			}
			dbMsg +="、すでにリクエスト実行中="+ requestJikkoucyuu;
			if( ! requestJikkoucyuu){				// = false;		//リクエスト実行中
				le_cb.setVisibility(View.VISIBLE);
				le_cb.setText(getResources().getString(R.string.cb_text_request_sugu));				//すぐ再生</string>
				le_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {			// チェック状態が変更された時のハンドラ
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					String dbMsg = "isChecked="+ isChecked;/////////////////////////////////////
					dbMsg += ",le_cb="+ le_cb.isChecked();/////////////////////////////////////
					MuList.this.requestSugu = le_cb.isChecked();			//すぐに再生
					dbMsg += "すぐに再生="+ MuList.this.requestSugu;/////////////////////////////////////
						myLog(TAG,dbMsg);
					}
				});
			} else {
				le_cb.setVisibility(View.GONE);
			}
			builder.setPositiveButton(getResources().getString(R.string.comon_kakutei), new  DialogInterface.OnClickListener(){	//確定</string>
				@Override
				public void onClick(DialogInterface dialog, int idx) {
					final String TAG = getResources().getString(R.string.comon_kakutei)+"requestList[MuList]";
					String dbMsg = "開始";
					try{
						dbMsg +=  "操作対象レコード[" + audio_id;
						dbMsg +="]" + sousaRecordUrl;
						dbMsg += "、すぐに再生="+ MuList.this.requestSugu;/////////////////////////////////////
						dbMsg += "渡されたリスト名="+ MuList.this.nowList;/////////////////////////////////////
						dbMsg += "、すでにリクエスト実行中="+ MuList.this.requestJikkoucyuu;/////////////////////////////////////
						if( ! MuList.this.requestJikkoucyuu){					// && ! nowList.equals(getResources().getString(R.string.playlist_namae_request))
							String dataFN = getPrefStr( "pref_data_url" ,  "" , MuList.this);
							dbMsg += ",保留["+ nowList_id + "]"+ nowList + ",dataFN = "+ dataFN ;////////
//							sharedPref = getSharedPreferences( getResources().getString(R.string.pref_main_file) ,MODE_PRIVATE);		//	getSharedPreferences(prefFname,MODE_PRIVATE);
//							myEditor = sharedPref.edit();
							myEditor.putString( "maeList", String.valueOf(MuList.this.nowList));
							myEditor.putString( "maeList_id", String.valueOf(MuList.this.nowList_id));		//☆intで書き込むとcannot be cast

							if(Zenkyoku_db != null){
								if( Zenkyoku_db.isOpen()){
									Zenkyoku_db.close();
									dbMsg += ">isOpen>" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
								}
							}
							String fn = getString(R.string.zenkyoku_file);			//全曲リスト名
							dbMsg += ",fn=" + fn;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
							zenkyokuHelper = new ZenkyokuHelper(MuList.this , fn);		//全曲リストの定義ファイル		.
							Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
							dbMsg += ">isOpen>" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
							dbMsg += ",getPageSize=" + Zenkyoku_db.getPageSize() + "件、" ;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
							String zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
							String[] c_columns =null;					//②引数tableには、テーブル名を指定します。
							String c_selection = "DATA = ?";
							String[] c_selectionArgs= {String.valueOf(dataFN)};
							String c_groupBy = null;
							String c_having = null;
							String c_orderBy = null; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
							cursor = Zenkyoku_db.query(zenkyokuTName, c_columns, c_selection, c_selectionArgs , c_groupBy, c_having, c_orderBy);	// table, columns,new String[] {MotoN, albamN}
							if( cursor.moveToFirst() ){
								int maeIndex = Integer.valueOf(String.valueOf(cursor.getString(cursor.getColumnIndex("_id"))));//インデックスの逆検索	 ,mItems , getApplicationContext()
								dbMsg += "("+ maeIndex  ;////////
								listEnd = Integer.valueOf(pref_file_kyoku);
								dbMsg += "/"+listEnd + ")" ;////////
								int tugiIndex = maeIndex+1;
								if(listEnd < tugiIndex){
									tugiIndex = 1 ;
								}
								dbMsg += ">>("+ tugiIndex + "/"+listEnd + ")" ;////////
								c_selection = "_id = ? ";
								String[] c_selectionArgs2= {String.valueOf(tugiIndex)};
								cursor = Zenkyoku_db.query(zenkyokuTName, c_columns, c_selection, c_selectionArgs , c_groupBy, c_having, c_orderBy);	// table, columns,new String[] {MotoN, albamN}
								if( cursor.moveToFirst() ){
									String tugiFN = String.valueOf(cursor.getString(cursor.getColumnIndex("DATA")));
									dbMsg += tugiFN ;////////
									myEditor.putString( "maeDatFN", tugiFN);		//再生中のファイル名
								}
							}
							cursor.close();
							Zenkyoku_db.close();
							boolean kakikomi = myEditor.commit();
							dbMsg +=",書き込み=" + kakikomi;	////////////////
						}
						MuList.this.requestJikkoucyuu = true;
						requestListJyunbi();								//リクエストリスト作成・既存リスト確認/削除
						myLog(TAG, dbMsg);
					}catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}
			});
			builder.setNegativeButton(getResources().getString(R.string.comon_cyusi),  new DialogInterface.OnClickListener() {	//中止</string>
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			builder.setCancelable(false);
			builder.show();
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/** リクエストリスト作成・既存リスト確認/削除 */
	public void requestListJyunbi(){								//リクエストリスト作成・既存リスト確認/削除
		final String TAG = "requestListJyunbi";
		String dbMsg = "";
		try{
			tuikaSakiListName = getResources().getString(R.string.playlist_namae_request);		//リクエストリスト
			MuList.this.tuikaSakiListName = tuikaSakiListName;
			reqCode = CONTEXT_add_request;						//リクエストリスト
			tuikaSakiListID = siteiListSakusi( tuikaSakiListName);				//指定された名称のリストを作成する
			dbMsg +=  "[" + tuikaSakiListID + "] "+tuikaSakiListName ;
			requestListTuika();								//リクエストリスト作成・楽曲追加
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**リクエストリスト作成・楽曲追加*/
	public void requestListTuika(){								//リクエストリスト作成・楽曲追加
		final String TAG = "requestListTuika";
		String dbMsg = "";
		try{
			dbMsg +=  "操作対象レコード[" + audio_id;
			dbMsg +="]" + sousaRecordUrl;
			dbMsg +=";書込みリスト[" + tuikaSakiListID + "]" + tuikaSakiListName;
			dbMsg += "すぐに再生="+ requestSugu;///	musicPlaylist.
			Uri result_uri = musicPlaylist.addMusicToPlaylist( tuikaSakiListID, audio_id, sousaRecordUrl);	//プレイリストへ曲を追加する
			dbMsg += "、書込み"+ result_uri;
			requestJikkoucyuu = true;
			if( requestSugu ){
				dbMsg += "すぐに再生="+ requestSugu;/////////////////////////////////////
				sousalistID = tuikaSakiListID;		//最終操作設定値へ
				sousalistName = tuikaSakiListName;
				nowList = tuikaSakiListName;
				nowList_id = tuikaSakiListID;
				myEditor.putString( "nowList", tuikaSakiListName);
				myEditor.putString( "nowList_id", String.valueOf(tuikaSakiListID));		//☆intで書き込むとcannot be cast
				boolean kakikomi = myEditor.commit();
				dbMsg +=",書き込み=" + kakikomi;	////////////////
				back2Player( sousaRecordUrl);												//プレイヤーにuriを送る
			} else {
				if(MPSIntent == null){
					MPSIntent = new Intent(getApplication(),MusicPlayerService.class);	//parsonalPBook.thisではメモリーリークが起こる
					dbMsg += ">>" + MPSIntent;/////////////////////////////////////
				}
				dbMsg += ".getAction=" + MPSIntent.getAction();/////////////////////////////////////
				MPSIntent.setAction(MusicPlayerService.ACTION_REQUEST);
				dbMsg += ">>" + MPSIntent.getAction();/////////////////////////////////////
				int tugiList_id = tuikaSakiListID;
				dbMsg += "[" + tuikaSakiListID;/////////////////////////////////////
				String tugiList = tuikaSakiListName;
				dbMsg += "]" + tuikaSakiListName;/////////////////////////////////////
				MPSIntent.putExtra("tugiList_id",tuikaSakiListID );
				MPSIntent.putExtra("tugiList",tuikaSakiListName);
				MPSName = startService(MPSIntent);	//startService(new Intent(MusicPlayerService.ACTION_PAUSE));
				dbMsg += ",送信先ComponentName=" + MPSName;/////////////////////////////////////
				dbMsg += " ,b_reqCode=" + b_reqCode;/////////////////////////////////////
				if( sousalistName.equals(getResources().getString(R.string.listmei_zemkyoku)) ){		// 全曲リストのアーティスト選択
					reqCode = MyConstants.v_titol;						//2131558439	タイトル
				}else{
					reqCode = b_reqCode;							//処理コードの保留
				}
				dbMsg += " ,reqCode=" + reqCode;/////////////////////////////////////
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/** リクエスト撤回 */
	public void requestListReset(){								//リクエスト撤回
		final String TAG = "requestListReset";
		String dbMsg = "";
		try{
			plNameSL = getPList();		//プレイリストを取得する
			dbMsg +=  "リスト総数 = " + plNameSL.size() +"件";
			tuikaSakiListName = getResources().getString(R.string.playlist_namae_request);		//リクエストリスト
			MuList.this.tuikaSakiListName = tuikaSakiListName;
			reqCode = CONTEXT_dell_request;						//リクエスト解除
			int rIndex = -1;
			rIndex = plNameSL.indexOf(tuikaSakiListName);
			dbMsg += ",該当= " + (rIndex + 1 ) + "件目";
			if(-1 < rIndex){
				tuikaSakiListID =Integer.valueOf(String.valueOf(plNameAL.get(rIndex).get("_id")));		//操作対象リストID
				dbMsg += "既存[" + tuikaSakiListID + "] "+tuikaSakiListName ;
				String cStr = String.valueOf(plNameAL.get(rIndex).get("name"));		//操作対象リスト名
				dbMsg += "(確認；" + cStr + ")" ;
				sousalist_data = String.valueOf(plNameAL.get(rIndex).get("_data"));		//
				dbMsg += ",操作対象リストのUrl=" + sousalist_data ;
				deletPlayListLoop(reqCode ,  MuList.this.tuikaSakiListID);			//指定したプレイリストを削除する
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void requestListResetEnd(){								//リクエスト撤回
		final String TAG = "requestListReset";
		String dbMsg = "";
		try{
			myEditor.putString( "maeList", null);
			myEditor.putString( "maeList_id", String.valueOf(-1));		//☆intで書き込むとcannot be cast
			myEditor.putString( "maeDatFN", null);		//再生中のファイル名
			boolean kakikomi = myEditor.commit();
			dbMsg +=",書き込み=" + kakikomi;	////////////////
			if(MPSIntent == null){
				MPSIntent = new Intent(getApplication(),MusicPlayerService.class);	//parsonalPBook.thisではメモリーリークが起こる
				dbMsg += ">>" + MPSIntent;/////////////////////////////////////
			}
			dbMsg += ".getAction=" + MPSIntent.getAction();/////////////////////////////////////
			MPSIntent.setAction(MusicPlayerService.ACTION_REQUEST);
			dbMsg += ">>" + MPSIntent.getAction();/////////////////////////////////////
			int tugiList_id = 0;
			dbMsg += "[" + tuikaSakiListID;/////////////////////////////////////
			String tugiList = null;
			dbMsg += "]" + tuikaSakiListName;/////////////////////////////////////
			MPSIntent.putExtra("tugiList_id",tuikaSakiListID );
			MPSIntent.putExtra("tugiList",tuikaSakiListName);
			MPSName = startService(MPSIntent);	//startService(new Intent(MusicPlayerService.ACTION_PAUSE));
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	//プレイリストユーティリティ////////////////////////////////////////////////////////////////////////////////////////////////リクエストリスト///
	/**
	 *  指定した名前のリストが有ればカーソルを返す; 無ければnullを返す*/
	public Cursor listUMU(String listName){	//
		Cursor cursor = null;
		final String TAG = "listUMU";
		String dbMsg = "";
		try{
			dbMsg +=  listName+"は";/////////////////////////////////////
			Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
			String[] columns = null;			//{ idKey, nameKey };
			String c_selection = MediaStore.Audio.Playlists.NAME +" = ? ";
			String[] c_selectionArg= { String.valueOf(listName) };		//⑥引数groupByには、groupBy句を指定します。
			String c_orderBy = null;			//MediaStore.Audio.Playlists.Members.PLAY_ORDER;
			cursor = getApplicationContext().getContentResolver().query(uri, columns, c_selection, c_selectionArg, c_orderBy );
			dbMsg += cursor.getCount()+"件あり";

			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return cursor;
	}

	/**
	 *  指定した名前のリスト指定した項目のデータが有ればカーソルを返す; 無ければnullを返す* */
	public Cursor list_dataUMU(long listID , String colName , String chName) {	//指定した名前のリスト指定した項目のデータが有ればカーソルを返す
		Cursor cursor = null;
		final String TAG = "list_dataUMU";
		String dbMsg = "";
		try{
			dbMsg +=  "[listID=" + listID+"]の";/////////////////////////////////////
			dbMsg += colName+"に";/////////////////////////////////////
			if(colName.equals("DATA")){
				colName =  MediaStore.Audio.Playlists.DATA;
			}else{
				colName = colName.toLowerCase();
			}
			dbMsg += chName+"は";/////////////////////////////////////
			if( 0< listID){
				Uri kakuninntUri = MediaStore.Audio.Playlists.Members.getContentUri("external", listID);
				dbMsg += "(" + kakuninntUri+")";/////////////////////////////////////
				String[] columns = null;			//{ idKey, nameKey };
				String c_orderBy = null;			//MediaStore.Audio.Playlists.Members.PLAY_ORDER;
				cursor = this.getContentResolver().query(kakuninntUri , columns, null, null, c_orderBy );
				if(cursor.moveToFirst()){
					int tCount = cursor.getCount();
					String c_selection2 = colName  +" = ? ";			//		"album_artist"
					String[] c_selectionArgs= { String.valueOf( chName ) };			//{"%" + String.valueOf( chName ) + "%" };
					cursor = this.getContentResolver().query(kakuninntUri , columns, c_selection2, c_selectionArgs, c_orderBy );
					dbMsg += cursor.getCount() + "/" + tCount + "件あり";/////////////////////////////////////
				}
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return cursor;
	}

	/**
	 *  指定された名称のリストを作成する;既に有ればlistIDを返し、無ければ作成してIdを返す
	 *  */
	public int siteiListSakusi( String listName){				//指定された名称のリストを作成する
		int listID = 0;
		final String TAG = "siteiListSakusi";
		String dbMsg = "";
		try{
			dbMsg +=  "指定された名称=" + listName;
			listID = musicPlaylist.getPlaylistId(listName);
//			Uri result_uri = null;
//			Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
//			String[] columns = null;			//{ idKey, nameKey };
//			String c_selection = MediaStore.Audio.Playlists.NAME +" = ? ";
//			String[] c_selectionArg= { String.valueOf(listName) };		//⑥引数groupByには、groupBy句を指定します。
//			String c_orderBy = null;			//MediaStore.Audio.Playlists.Members.PLAY_ORDER;
//			Cursor cursor = getApplicationContext().getContentResolver().query(uri, columns, c_selection, c_selectionArg, c_orderBy );
//			dbMsg +=  ",既存Playlists;" + cursor.getCount() + "件";
//			if(cursor.moveToFirst()){
//				listID = Integer.valueOf(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID)));
//			} else{
//				dbMsg +=  ">>新規作成" ;
//				result_uri = addPlaylist(listName, null, null);		//プレイリストを新規作成する
//				dbMsg += ",result_uri=" + result_uri;			//fastItemeFn=/storage/sdcard0/Music/Jimmy Cliff/Follow My Mind/07 Remake The World.wma
//				listID = (int)ContentUris.parseId(result_uri);
//			}
			dbMsg += ",[" + listID + "]" ;	//+ result_uri;			//[42529]content://media/external/audio/playlists/42529
			cursor.close();
			dbMsg += ",reqCode=" + reqCode;
			switch(reqCode) {
				case CONTEXT_rumdam_saiseizumi:					//642  ランダム再生準備
				case CONTEXT_REPET_CLEAN:						//リピート再生リストの既存レコード消去
				case CONTEXT_add_request:						//リクエストリスト
					break;
				default:
					int ePosition = 0;
					if( plNameSL != null){
						if(0 < plNameSL.size() ){
							dbMsg += ",plNameSL=" + plNameSL.size() + "件";
							ePosition = plNameSL.indexOf(listName);
						}
						dbMsg += ",ePosition=" + ePosition;
						pl_sp.setSelection(ePosition);
					}
					break;
			}
			if( plNameSL != null){
				int sIndex = plNameSL.indexOf(listName);				//既存のリストを検索
				dbMsg += ",sIndex=" + sIndex;
				if(-1 < sIndex){										//見つかれば
					int motoID = Integer.valueOf(String.valueOf(plNameAL.get(sIndex).get("_id")) );
					dbMsg += ",sIndex=" + motoID;
					if( motoID != listID ){
						plNameAL.get(sIndex).put("_id", listID) ;
						dbMsg += ">>" + plNameAL.get(sIndex).get("_id");
//						if(result_uri!= null  ){
//							plNameAL.get(sIndex).put("_data", result_uri) ;
//							dbMsg += "]" + plNameAL.get(sIndex).get("_data");
//						}
					}
				}
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return listID;
	}

	/**
	 *  最近追加リストのDBファイル読取りファイル読取り
	 *
	 * @param listName
	 * @return
	 */
	public Cursor listUMU_addNew(String listName){	// 最近追加リストのDBファイル読取りファイル読取り
		Cursor cursor = null;
		final String TAG = "listUMU_addNew";
		String dbMsg = "";
		try{
			dbMsg +=  listName+"は";/////////////////////////////////////
//			Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
//			String[] columns = null;			//{ idKey, nameKey };
//			String c_selection = MediaStore.Audio.Playlists.NAME +" = ? ";
//			String[] c_selectionArg= { String.valueOf(listName) };		//⑥引数groupByには、groupBy句を指定します。
//			String c_orderBy = null;			//MediaStore.Audio.Playlists.Members.PLAY_ORDER;
//			cursor = getApplicationContext().getContentResolver().query(uri, columns, c_selection, c_selectionArg, c_orderBy );
			saikintuikaDBname = getResources().getString(R.string.playlist_saikintuika_filename);
			dbMsg +=  "、ファイル=" + saikintuikaDBname ;
			File fileObj = new File(MuList.this.saikintuikaDBname);
			MuList.this.saikintuikaTablename = saikintuikaDBname.replace(".db" , "");	//最近追加のリストテーブル名
			dbMsg +=  "、テーブル=" + MuList.this.saikintuikaTablename ;

			MuList.this.tuikaSakiListName = getResources().getString(R.string.playlist_namae_saikintuika);		//最近追加
			MuList.this.nowList = MuList.this.tuikaSakiListName;

			if(newSongHelper == null){
				newSongHelper = new ZenkyokuHelper(MuList.this , saikintuikaDBname);		//周防会作成時はここでonCreatが走る
			}
			if(newSongDb != null){
				dbMsg += " , isOpen=" + newSongDb.isOpen();
			}
			newSongDb = newSongHelper.getReadableDatabase();
			dbMsg += ">isOpen>" + newSongDb.isOpen();
			dbMsg += " , isReadOnly=" + newSongDb.isReadOnly();
			dbMsg += " , isWriteAheadLoggingEnabled=" + newSongDb.isWriteAheadLoggingEnabled();
			dbMsg += " , getPageSize=" + newSongDb.getPageSize();
			dbMsg += " , getMaximumSize=" + newSongDb.getMaximumSize();
			//>isOpen>true , isReadOnly=false , isWriteAheadLoggingEnabled=false , getPageSize=4096 , getMaximumSize=4398046507008 新規作成時
			//>isOpen>true , isReadOnly=false , isWriteAheadLoggingEnabled=false , getPageSize=4096 , getMaximumSize=4398046507008 , 既存=170レコード
			cursor = newSongDb.query(saikintuikaTablename , null , null , null , null , null , null);

			dbMsg += cursor.getCount()+"件あり";

			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return cursor;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////プレイリストユーティリティ//

	//イベント処理////////////////////////////////////////////////////////////////////////////////////////////////////プレイリスト//
	public int SelID;
	View currentFo;					//選択されているアイテム

//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		final String TAG = "onKeyDown";
//		String dbMsg = "";
//		dbMsg += ORGUT.nowTime(true,true,true);/////////////////////////////////////
//		dbMsg += " , keyCode=" +keyCode;
//		myLog(TAG, dbMsg);
//		if(keyCode != KeyEvent.KEYCODE_BACK){
//				headClickAction();	//ヘッドが クリックされた時の処理
//
//				return super.onKeyDown(keyCode, event);
//			}else{
//				return false;
//			}
//	}

	/**
	 *  ハードウェアキーを取得
	 *  https://techbooster.org/android/device/5056/
	 * */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		boolean retBool= false;
		final String TAG = "dispatchKeyEvent";
		String dbMsg = "";
		dbMsg += ORGUT.nowTime(true,true,true);/////////////////////////////////////
		try{
			int focusItemID = 0;
			dbMsg += ORGUT.nowTime(true,true,true)+"発生";////////////",event="+event.toString()+/////////////////////////////////
			int keyCode =  event.getKeyCode();
			dbMsg += " , keyCode=" +keyCode + " , Action=" +event.getAction();
//		if (e.getAction() == KeyEvent.ACTION_DOWN) {
//			headClickAction();	//ヘッドが クリックされた時の処理
//		}
//		myLog(TAG, dbMsg);
//		return super.dispatchKeyEvent(e);
			currentFo = this.getCurrentFocus();				//選択されているアイテム
			if(currentFo != null){
				focusItemID=currentFo.getId();
				dbMsg +="が" + "Item="+focusItemID+"(";
//				if(headLayout.isShown()){
//					dbMsg +="head=" + mainHTF.getId() + "/";///////////////////////////////////////////////////////////////////
//				}
				dbMsg += "list=" + lvID.getId() + ")で発生";///////////////////////////////////////////////////////////////////
			}
			switch (keyCode) {
				case KeyEvent.KEYCODE_BACK:	//4；KEYCODE_BACK :keyCode；09SH: keyCode；4,event=KeyEvent{action=0 code=4 repeat=0 meta=0 scancode=158 mFlags=72}
					if(event.getAction() == KeyEvent.ACTION_DOWN){					//二重発生の防止
						headClickAction();	//ヘッドが クリックされた時の処理
					}
					return true;
				//	break;
				case KeyEvent.KEYCODE_DPAD_UP:		//マルチガイド上；19
				case KeyEvent.KEYCODE_DPAD_DOWN:	//マルチガイド下；20
				case KeyEvent.KEYCODE_DPAD_LEFT:	//マルチガイド左；21
				case KeyEvent.KEYCODE_DPAD_RIGHT:	//マルチガイド右；22
				case KeyEvent.KEYCODE_DPAD_CENTER:	//決定ボタン；23  ★ここではなくonCkickで処理される
					if(! prTT_dpad){					//d-pad有りか
						setKeyAri();									//d-pad対応
					}
					break;
//			default:
//				 retBool= false;			//指定したキー以外はデフォルト動作
//				break;
			}
			retBool= setKeyDousa( focusItemID , event.getKeyCode() ,  event.getAction());				//d-padの操作反応	event.getKeyCode,event.getAction()
			dbMsg += "retBool=" + retBool;///////////////////////////////////////////////////////////////////
			myLog(TAG, dbMsg);
		} catch (NullPointerException e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return retBool;
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		boolean retBool= false;
		final String TAG = "onKey";
		String dbMsg = "";
		dbMsg += ORGUT.nowTime(true,true,true);/////////////////////////////////////
		try{
//			int focusItemID = 0;
//			dbMsg += ORGUT.nowTime(true,true,true)+"発生";////////////",event="+event.toString()+/////////////////////////////////
//			dbMsg += " , keyCode=" +keyCode;
//			dbMsg += " , Action=" +event.getAction();
//			currentFo = this.getCurrentFocus();				//選択されているアイテム
//			if(currentFo != null){
//				focusItemID=currentFo.getId();
//				dbMsg +="が" + "Item="+focusItemID+"(";
////				if(headLayout.isShown()){
////					dbMsg +="head=" + mainHTF.getId() + "/";///////////////////////////////////////////////////////////////////
////				}
//				dbMsg += "list=" + lvID.getId() + ")で発生";;///////////////////////////////////////////////////////////////////
//			}
//			switch (keyCode) {
//			case KeyEvent.KEYCODE_BACK:	//4；KEYCODE_BACK :keyCode；09SH: keyCode；4,event=KeyEvent{action=0 code=4 repeat=0 meta=0 scancode=158 mFlags=72}
//				if(event.getAction() == KeyEvent.ACTION_DOWN){					//二重発生の防止
//					headClickAction();	//ヘッドが クリックされた時の処理
//				}
//				return true;
//			//	break;
//			case KeyEvent.KEYCODE_DPAD_UP:		//マルチガイド上；19
//			case KeyEvent.KEYCODE_DPAD_DOWN:	//マルチガイド下；20
//			case KeyEvent.KEYCODE_DPAD_LEFT:	//マルチガイド左；21
//			case KeyEvent.KEYCODE_DPAD_RIGHT:	//マルチガイド右；22
//			case KeyEvent.KEYCODE_DPAD_CENTER:	//決定ボタン；23  ★ここではなくonCkickで処理される
//				if(! prTT_dpad){					//d-pad有りか
//					setKeyAri();									//d-pad対応
//				}
//				break;
////			default:
////				 retBool= false;			//指定したキー以外はデフォルト動作
////				break;
//			}
//			retBool= setKeyDousa( focusItemID , event.getKeyCode() ,  event.getAction());				//d-padの操作反応	event.getKeyCode,event.getAction()
			dbMsg += "retBool=" + retBool;///////////////////////////////////////////////////////////////////
			myLog(TAG, dbMsg);
		} catch (NullPointerException e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return retBool;
	}

	@SuppressLint("ResourceAsColor")
	public boolean setKeyDousa( int focusItemID ,int keyCode , int kAction) {									//d-padの操作反応	event.getKeyCode,event.getAction()
		final String TAG = "setKeyDousa";
		String dbMsg = "";
		try{
			dbMsg +=  focusItemID+"で"+keyCode +"を" + kAction ;///////////////////////////
			if (focusItemID== mainHTF.getId()){																		//リストの操作中
				dbMsg +=  "ヘッダーのメインテキスト表示枠("+mainHTF.getId() +")で" ;///////////////////////////
				int position = lvID.getFirstVisiblePosition();
				dbMsg += ",getFirstVisiblePosition="+position ;///////////////////////////
				int y = lvID.getChildAt(0).getTop();
				dbMsg += ",getTop="+y ;///////////////////////////
				switch (keyCode) {
				case KeyEvent.KEYCODE_DPAD_UP:				//0
					dbMsg +=", 19;DPAD_UP";///////////////////////////
					switch (kAction) {
					case KeyEvent.ACTION_UP:				//0
						dbMsg +="で;ACTION_UP";///////////////////////////
						mainHTF.setSelected(true);
						mainHTF.setFocusable(true);
						mainHTF.setFocusableInTouchMode(true);
						mainHTF.requestFocus();
						mainHTF.setBackgroundColor(Color.rgb(0, 0, 0));
						mainHTF.setTextColor( Color.rgb(255, 255, 255));
						mainHTF.setSelected(false);
						mainHTF.requestFocus();
						lvID.setSelection(lvID.getCount()-1);					//最終行を選択
						lvID.setFocusable(true);
						lvID.setFocusableInTouchMode(true);
						lvID.requestFocus();
						return true;													//イベントをこれ以上送らない
					case KeyEvent.ACTION_DOWN:
						dbMsg +="で;ACTION_DOWN";///////////////////////////
						break;
					}
				case KeyEvent.KEYCODE_DPAD_DOWN:
					dbMsg +=", 20;DPAD_DOWN";///////////////////////////
					switch (kAction) {
					case KeyEvent.ACTION_UP:				//0
						dbMsg +="で;ACTION_UP";///////////////////////////
						dbMsg += lvID.getSelectedItemPosition()+"/" + lvID.getCount();///////////////////////////
						myLog(TAG,dbMsg);
						break;
					case KeyEvent.ACTION_DOWN:
						dbMsg +="で;ACTION_DOWN";///////////////////////////
						dbMsg += lvID.getSelectedItemPosition()+"/" + lvID.getCount();///////////////////////////
						mainHTF.setSelected(true);
						mainHTF.setFocusable(true);
						mainHTF.setFocusableInTouchMode(true);
						mainHTF.requestFocus();
						mainHTF.setBackgroundColor(Color.rgb(0, 0, 0));
						mainHTF.setTextColor( Color.rgb(255, 255, 255));
						mainHTF.setSelected(false);
						mainHTF.requestFocus();
						lvID.setSelection(0);
						lvID.setFocusable(true);
						lvID.setFocusableInTouchMode(true);
						lvID.requestFocus();
						return true;													//イベントをこれ以上送らない
					}
					break;
				case KeyEvent.KEYCODE_DPAD_CENTER:
					dbMsg +=", 23:DPAD_CENTER";///////////////////////////
					switch (kAction) {
					case KeyEvent.ACTION_UP:				//0
						dbMsg +="で;ACTION_UP";///////////////////////////
						headClickAction();//ヘッドが クリックされた時の処理
						return true;													//イベントをこれ以上送らない
					case KeyEvent.ACTION_DOWN:
						dbMsg +="で;ACTION_DOWN";///////////////////////////
						break;
					}
				break;
				}
			}else if (focusItemID== lvID.getId()){																		//リストの操作中
				dbMsg +=  "リスト="+lvID.getId() +")" ;///////////////////////////
				int SentakuRow = lvID.getSelectedItemPosition();
				dbMsg +="[SentakuRow="+SentakuRow ;///////////////////////////
				int totalRow = lvID.getCount();
				dbMsg +="/totalRow="+ totalRow + "]" ;///////////////////////////
				switch (keyCode) {
				case KeyEvent.KEYCODE_DPAD_UP:				//0
					dbMsg +=", 19;DPAD_UP";///////////////////////////
					switch (kAction) {
					case KeyEvent.ACTION_UP:				//0
						dbMsg +="で;ACTION_UP";///////////////////////////
						if(SentakuRow == 0){
							lvID.setSelection(lvID.getCount()-1);					//最終行を選択
							lvID.setSelected(false);
							lvID.requestFocus();
							lvID.clearFocus();
							mainHTF.setSelected(true);
							mainHTF.setFocusable(true);
							mainHTF.setFocusableInTouchMode(true);
							mainHTF.requestFocus();
							mainHTF.setBackgroundColor(Color.rgb(246, 152, 32));
							mainHTF.setTextColor( Color.rgb(0, 0, 0));
							return true;			//イベントをこれ以上送らない
//						} else {
//							return false;			//イベントを送る
						}
					case KeyEvent.ACTION_DOWN:
						dbMsg +="で;ACTION_DOWN";///////////////////////////
						break;
					}
				case KeyEvent.KEYCODE_DPAD_DOWN:
					dbMsg +=", 20;DPAD_DOWN";///////////////////////////
					switch (kAction) {
					case KeyEvent.ACTION_UP:				//0
						dbMsg +="で;ACTION_UP";///////////////////////////
						myLog(TAG,dbMsg);
						if( SentakuRow == (totalRow -1)) {									//最終行に達したら
							lvID.setSelection(0);											//☆選択を先頭に戻してから
							lvID.setSelected(false);										//選択を外す
							lvID.requestFocus();
							lvID.clearFocus();
							mainHTF.setFocusable(true);
							mainHTF.setFocusableInTouchMode(true);
							mainHTF.requestFocus();
							mainHTF.setSelected(true);
							mainHTF.setBackgroundColor(Color.rgb(246, 152, 32));
							mainHTF.setTextColor( Color.rgb(0, 0, 0));
							return true;			//イベントをこれ以上送らない
//						} else {
//							return false;			//イベントを送る
						}
					case KeyEvent.ACTION_DOWN:
						dbMsg +="で;ACTION_DOWN";///////////////////////////
						break;
					}
					break;
				case KeyEvent.KEYCODE_DPAD_CENTER:
					dbMsg +=", 23:DPAD_CENTER";///////////////////////////
					switch (kAction) {
					case KeyEvent.ACTION_UP:				//0
						dbMsg +="で;ACTION_UP";///////////////////////////
						break;
					case KeyEvent.ACTION_DOWN:
						dbMsg +="で;ACTION_DOWN";///////////////////////////
						break;
					}
				break;
				}
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return false;													//イベントを後段に送る
	}

	//属性設定　http://techbooster.org/android/ui/14993/
	public void setKeyAri() {									//d-pad対応
		final String TAG = "setKeyAri";
		String dbMsg = "";
		try{
			prTT_dpad=true;					//d-pad有りか
//			dbBlock=TAG+",mainHTF=" + mainHTF.getId() ;
//			mainHTF.setNextFocusUpId(lvID.getId());		//アルバムアーティスト
//			mainHTF.setNextFocusDownId(lvID.getId());		//タイトル
//			dbBlock=dbBlock+",lvID=" + lvID.getId() ;
//			lvID.setNextFocusUpId(mainHTF.getId());			//再生ボタン
//			lvID.setNextFocusDownId(mainHTF.getId());	//アルバム
//			ppBtnID.setBackgroundColor(0xff00ffff);		//@colors/blueGreen	#00ffff
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}
//////////////////////////////////////////////////////////キー対応//
	@Override
	public void onClick(View v) {																		//操作対応②ⅰ
		final String TAG = "onClick";
		String dbMsg = "";
		try{
			dbMsg += ORGUT.nowTime(true,true,true)+dbMsg;
			String dataFN = getPrefStr( "pref_data_url" ,  "" , MuList.this);
			b_saisei_fname = dataFN;
			b_album = albumName;			//それまで参照していたアルバム名
			dbMsg +=" , クリックされたのは" + v.getId();
			if (v == lp_ppPButton) {
				if (lp_ppPButton.getContentDescription().equals(getResources().getText(R.string.play)) ) {
					IsPlaying = false;
				}else{
					IsPlaying = true;
				}
		//20220831		IsPlaying =	send2Service(dataFN , nowList , IsPlaying);
//				dbMsg +=" 、ppPBT;IsPlaying="+ IsPlaying;				// + ",MPSIntent=" + MPSIntent;
//				if(mFilter == null){
//					psSarviceUri = getPackageName() + getResources().getString(R.string.psSarviceUri);		//プレイヤーサービス	"com.hijiyam_koubou.marasongs.PlayerService";
//					dbMsg +=  ">>psSarviceUri=" + psSarviceUri;
//					mFilter = new IntentFilter();
//					mFilter.addAction(MusicPlayerService.ACTION_STATE_CHANGED);
//					registerReceiver(mReceiver, mFilter);
////					dbMsg +=">>" + psSarviceUri.toString();
//				}
//				if( MPSIntent == null){
//					MPSIntent = new Intent(getApplication(),MusicPlayerService.class);	//parsonalPBook.thisではメモリーリークが起こる
//					dbMsg +=  ",MPSIntent=null";
//					MPSIntent.putExtra("saiseiJikan",saiseiJikan);
//					MPSIntent.putExtra("mIndex",mIndex);
//					MPSIntent.putExtra("pref_lockscreen",pref_lockscreen);
//					MPSIntent.putExtra("pref_notifplayer",pref_notifplayer);
//				}
//				dbMsg +=")" +dataFN;
//				MPSIntent.putExtra("dataFN",dataFN);
//				dbMsg +=",nowList_id" +nowList_id;
//				MPSIntent.putExtra("nowList_id",nowList_id);
//				dbMsg +=",mIndex" +mIndex;
//				MPSIntent.putExtra("mIndex",mIndex);
//				MPSIntent.putExtra("continu_status","toPlay");
				if (lp_ppPButton.getContentDescription().equals(getResources().getText(R.string.play)) ) {			//再生中か
					dbMsg += ".getAction=" + MPSIntent.getAction();/////////////////////////////////////
					MPSIntent.setAction(MusicPlayerService.ACTION_PAUSE);
					dbMsg += ">>" + MPSIntent.getAction();/////////////////////////////////////
					dbMsg +=  ">>startService";
					MPSName = startService(MPSIntent);	//ボタンフェイスの変更はサービスからの戻りで更新
					dbMsg += " ,ComponentName=" + MPSName;/////////////////////////////////////
					lp_ppPButton.setImageResource(R.drawable.play_notif);
					lp_ppPButton.setContentDescription(getResources().getText(R.string.pause));
					lp_chronometer.stop();
				} else {
					MPSIntent.setAction(MusicPlayerService.ACTION_PLAY);
					dbMsg += ">>" + MPSIntent.getAction();/////////////////////////////////////
					dbMsg +=  ">>startService";
					MPSName = startService(MPSIntent);	//
//					MPSName = startService(new Intent(MusicPlayerService.ACTION_PLAY));             //startService(MPSIntent);	//
					dbMsg += " ,MPSName=" + MPSName;/////////////////////////////////////
					lp_ppPButton.setImageResource(R.drawable.pouse_notif);
					lp_ppPButton.setContentDescription(getResources().getText(R.string.play));
					lp_chronometer.start();
				}
			} else if (v == rc_fbace) {	//プレイヤーフィールド部の土台
				dbMsg +=  "クリックされたのはrc_fbace";
				send2Player(dataFN ,nowList,false );
			} else if (v == lp_stop) {
				dbMsg +=  "クリックされたのはlp_stop";
			//	Intent intent = new Intent( MuList.this, MusicPlayerService.class);
				MPSIntent.setAction(MusicPlayerService.ACTION_SYUURYOU);
//				MPSIntent.setAction(MusicPlayerService.ACTION_SYUURYOU_NOTIF);
				dbMsg +=  ">>startService";
				startService(MPSIntent) ;
				quitBody();
			}
			dbMsg +=  "(mIndex=" + mIndex  +")";			//+ sentakuCyuu ;/////////////////////////////////////
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	long startPart;		// 開始時刻の取得
	public static final String EXTRA_MESSAGE = "0";
	/**
	 * public class plogTask extends AsyncTask<Object, Integer , AsyncTaskResult<Integer>>の置き換え
	 * Android 11でAsyncTaskがdeprecated
	 * サンプルは　 MyTask　https://akira-watson.com/android/asynctask.html
	 * */
	public class ploglessTask {
		ExecutorService executorService;
		private Context cContext = null;
//		public DialogProgress progressDialog;	// 処理中ダイアログ	ProgressDialog	AlertDialog
		Intent intentDP;
		SQLiteDatabase writeDB;
		SQLiteDatabase readDB;
//        public  SQLiteStatement stmt = null ;			//6；SQLiteStatement

		public int reqCode = 0;						//処理番号
		public Cursor cCursor;
		public String pdTitol;			//ProgressDialog のタイトルを設定
		public String pdMessage;			//ProgressDialog のメッセージを設定

		public String pdMessage_stok;			//ProgressDialog のメッセージを設定
		public int pdMaxVal = 0;					//ProgressDialog の最大値を設定 (水平の時)
		public int pdStartVal=0;					//ProgressDialog の初期値を設定 (水平の時)
		public int pdCoundtVal=0;					//ProgressDialog表示値
//		public int pd2MaxVal;					//ProgressDialog の最大値を設定 (水平の時)
//		public int pd2CoundtVal;					//ProgressDialog表示値
		public String _numberFormat = "%d/%d";
		public  NumberFormat _percentFormat = NumberFormat.getPercentInstance();
		double num;

		/**
		 * 指定したデータベースをWritableで開くか作成する
		 * */
		public SQLiteDatabase MakeOrOpenDataBase(String fn , SQLiteDatabase db){
			final String TAG = "MakeOrOpenDataBase";
			String dbMsg= "[AllSongs]";
			try{
				startPart = System.currentTimeMillis();		// 開始時刻の取得
				dbMsg=ORGUT.nowTime(true,true,true) + dbMsg;/////////////////////////////////////
				System.currentTimeMillis();
				cContext.getContentResolver();
				dbMsg += ",db=" + fn;
//                if(db != null){									//全曲の仮ファイル	kari.db
//                    dbMsg += ",isOpen=" + db.isOpen()+",isReadOnly=" + db.isReadOnly();
//                    if(db.isOpen()){
//                        db.close();
//                    }
//                    db = zenkyokuHelper.getWritableDatabase();			// データベースをオープン
//                }else{
				zenkyokuHelper = new ZenkyokuHelper(getApplicationContext() , fn);		//全曲リストの定義ファイル		.this.cContext.
				db = zenkyokuHelper.getWritableDatabase();			// データベースをオープン
//                    db = cContext.openOrCreateDatabase(fn, MODE_PRIVATE , null);	// | Context.MODE_ENABLE_WRITE_AHEAD_LOGGING Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE, String path, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler				//アーティスト名のえリストファイルを読み書きモードで開く
				dbMsg += ">作り直し>" + cContext.getDatabasePath(fn).getPath();	///data/data/com.hijiyam_koubou.marasongs/databases/artist.db
//                }
				dbMsg += ">>isOpen=" + db.isOpen()+",isReadOnly=" + db.isReadOnly();
				myLog(TAG,dbMsg);
			}catch(IllegalArgumentException e){
				myErrorLog(TAG,dbMsg +"で"+e.toString());
			}catch (Exception e) {
				myErrorLog(TAG,dbMsg +"で"+e.toString());
			}
			return db;
		}

		/**
		 * Transactionの終端
		 * */
		public void endTS(SQLiteDatabase sql_db) {
			final String TAG = "endTS";
			String dbMsg="";
			try{
				try{
					dbMsg= "sql_db = " + sql_db;//////
					sql_db.setTransactionSuccessful();
				} finally {
					sql_db.endTransaction();
				}
				sql_db.close();
				myLog(TAG,dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"；"+e.toString());
			}
		}

		/**
		 * プログレス表示クラスのコンテキスト
		 * */
		public ploglessTask(Context context) {
			super();
			final String TAG = "ploglessTask[ploglessTask]";
			String dbMsg="";
			try {
				executorService  = Executors.newSingleThreadExecutor();
				this.cContext = context;
		//		this.cCallback = callback;
				myLog(TAG,dbMsg );
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+" でエラー発生；"+e.toString());
			}
		}

		public class TaskRun implements Runnable {

			@Override
			public void run() {
				final String TAG = "run[ploglessTask]";
				String dbMsg="";
				try {
					dbMsg +="["+ reqCode + "]";
					String fn = cContext.getString(R.string.kari_file);			//仮ファイル
					String zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
					SQLiteStatement stmt = null ;			//6；SQLiteStatement
		//			dbMsg += "；全曲リストテーブル=" + zenkyokuTName;
			//		if(cCursor.moveToFirst()) {
						switch(reqCode) {
//							case pt_CreateAllSongs:		//807;全曲リスト作成
//								fn = cContext.getString(R.string.zenkyoku_file);
//								dbMsg += ",db=" + fn;
//								Zenkyoku_db=MakeOrOpenDataBase( fn ,  Zenkyoku_db);
//								dbMsg +=">>isOpen=" + Zenkyoku_db.isOpen()+",isReadOnly=" + Zenkyoku_db.isReadOnly();
//								Zenkyoku_db.beginTransaction();
//								stmt = null;
//								stmt = Zenkyoku_db.compileStatement("insert into " + zenkyokuTName +
//										"(AUDIO_ID,SORT_NAME,ARTIST,ALBUM_ARTIST,ALBUM,TRACK,TITLE,DURATION,YEAR,DATA,MODIFIED,COMPOSER,LAST_YEAR,ALBUM_ARTIST_INDEX) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
//								new ContentValues();
//								break;
							case CONTEXT_listup_jyunbi2:	//リストアップ準備;2階層リスト化
							case MENU_hihyoujiArtist2:		//非表示になったアーティストの修正処理;2階層化
							case MENU_infoKaisou:			//539:情報付きリスト書き込み中:CreatePLListEnd
							case listType_2ly:				//アーティストの単階層リストとalbumとtitolの２階層
							case MENU_2KAISOU:				//2階層リスト選択選択中
							case MENU_TAKAISOU2:				//多階層リスト書き込み中
							case CONTEXT_rumdam_arrayi:		//ランダム再生;配列書込み
							case CONTEXT_saikintuika_end:	//最近追加リストのプレイリスト作成
							case CONTEXT_saikintuika_Wr:				//最近追加リストのファイル作成
							case CONTEXT_rumdam_wr:			//ランダム再生リストの書込み
							case MyConstants.PUPRPOSE_SONG:
//								pdMaxVal = (int) params[2];		//プレイリスト用ArrayList
								dbMsg += ", pdMaxVal = " + pdMaxVal + "件"  ;
								break;
							case CONTEXT_M3U2_PL:		//汎用プレイリストをAndroidのプレイリストに転記
								break;
							default:
								if(cCursor != null) {
									pdMaxVal = cCursor.getCount();
								}
								dbMsg += ", cCursor = " + pdMaxVal + "件"  ;
								break;
						}
						long id = 0;
						List<String> testList = null;	//最近再生された楽曲のID
						testList =  new ArrayList<String>();	//最近再生された楽曲のID
						switch(reqCode) {
							case CONTEXT_listup_jyunbi2:	//リストアップ準備;2階層リスト化
							case MENU_hihyoujiArtist2:		//非表示になったアーティストの修正処理;2階層化
							case MENU_infoKaisou:			//情報付きリスト書き込み中
							case listType_2ly:				//アーティストの単階層リストとalbumとtitolの２階層
							case MENU_2KAISOU:				//2階層リスト選択選択中
							case MENU_TAKAISOU2:							//多階層リスト書き込み中
							case CONTEXT_saikintuika_end:	//最近追加リストのプレイリスト作成
							case CONTEXT_saikintuika_Wr:				//最近追加リストのファイル作成
								//				case CONTEXT_saikintuika_read:
							case CONTEXT_rumdam_arrayi:		//ランダム再生;配列書込み
							case CONTEXT_rumdam_wr:			//ランダム再生リストの書込み
							case CONTEXT_M3U2_PL:		//汎用プレイリストをAndroidのプレイリストに転記
							case MyConstants.PUPRPOSE_SONG:
								dbMsg += ", pdCoundtVal = " + pdCoundtVal ;
								for(pdCoundtVal =0 ; pdCoundtVal < pdMaxVal;pdCoundtVal ++){
									switch(reqCode) {
										case CONTEXT_listup_jyunbi2:	//リストアップ準備;2階層リスト化
										case MENU_hihyoujiArtist2:		//非表示になったアーティストの修正処理;2階層化
											listSyuuseiJyunbi2Body( pdCoundtVal);								//リストアップ準備;2階層リスト化
											break;
										case MENU_infoKaisou:			//537;情報付きリスト書き込み中;CreatePLListEnd
											plInfoSinglBody(pdCoundtVal);		//情報付き単階層プレイリストの描画
											break;
										case listType_2ly:				//2;アーティストの単階層リストとalbumとtitolの２階層
											plOneBody(pdCoundtVal);
											break;
										case MENU_2KAISOU:				//534;2階層リスト選択選択中
											plTowBody(pdCoundtVal);		//単階層+2階層プレイリストの2階層作成							break;
											break;
										case MENU_TAKAISOU2:			//536;多階層リスト書き込み中
										case MyConstants.PUPRPOSE_SONG:
											plWrightBody(pdCoundtVal);	//3階層プレイリストの描画
											break;
										case CONTEXT_saikintuika_end:	//654 最近追加リストのプレイリスト作成
											saikin_tuika_yomi_body( pdCoundtVal );				//最近追加された楽曲の抽出(指定IF)
											break;
										case CONTEXT_saikintuika_Wr:				//最近追加リストのファイル作成
											playListWr_body(pdCoundtVal );
											break;
										case CONTEXT_rumdam_arrayi:		//ランダム再生;配列書込み
											pdCoundtVal = randumPlay_array_body(pdCoundtVal);			//ランダム再生;ランダム追加
											break;
										case CONTEXT_rumdam_wr:			//ランダム再生リストの書込み
											randumPlayListWr_body(pdCoundtVal);			//ランダム再生;リストに書き込む
											break;
										case CONTEXT_M3U2_PL:		//汎用プレイリストをAndroidのプレイリストに転記
											m3U2PlayList_body( pdCoundtVal );
											break;
									}
//									publishProgress( pdCoundtVal );		//progressDialog.progBar1.setProgress(step1);
								}
								dbMsg += ">>" + pdCoundtVal ;
								break;
							default:
								dbMsg +=";"+ cCursor.getCount() + "件";				//+ cCursor.getColumnCount() + "項目";
								if(cCursor.moveToFirst()){
							//		dbMsg +=  "；ループ前；"+ cCursor.getPosition() +"/ " + pdMaxVal;	/////////////////////////////////////////////////////////////
									switch(reqCode) {
										case MyConstants.v_artist:							//アーティスト
//																	comp = getResources().getString(R.string.comon_compilation);			//コンピレーション
											break;
										case MyConstants.v_alubum:							//アルバム
											break;
										case MyConstants.v_titol:						//タイトル
											break;
										//					default:
										//						break;
									}
									@SuppressLint("Range") String delID =null;
									Uri pUri =null;
									int delC = 0;
									int rCol = 0;
									int prgInterval= cCursor.getCount()/10;
									int progStep=prgInterval;
									do{
						//				dbMsg +=  "[cursor="+  cCursor.getPosition() +  "/" + pdMaxVal + ";progressDialog=" + progressDialog.getProgress() +"]" ;
										dbMsg += ",isClosed=" + cCursor.isClosed() ;
										switch(reqCode) {
											case CONTEXT_listup_jyunbi:					//リストアップ準備;アーティスト/作曲者名
											case MENU_hihyoujiArtist:					//非表示になったアーティストの修正処理
												cCursor =  listSyuuseiJyunbiBody( cCursor);								//リスト修正準備；アーティストと作曲者名リスト
												break;
											case MENU_TAKAISOU:				//537;多階層リスト選択選択中:CreatePLList
											case listType_2ly2:				//  listType_2ly + 1;albumとtitolの２階層
											case MENU_MUSCK_PLIST:			//プレイリスト選択中
											case MyConstants.PUPRPOSE_lIST:
												cCursor = CreatePLListBody(cCursor);		//プレイリストの内容取得
												break;
											case CONTEXT_del_playlist:					//624;このリストを削除
											case CONTEXT_del_playlis_membert:			//627;リストの中身を削除
											case CONTEXT_rumdam_redrow:					//634 ランダム再生リストの消去
											case CONTEXT_REPET_CLEAN:					//リピート再生リストの既存レコード消去
											case CONTEXT_add_request:					//リクエストリスト
											case CONTEXT_dell_request:					//リクエスト解除
												rCol = cCursor.getColumnIndex(MediaStore.Audio.Playlists.Members._ID);
												delID = cCursor.getString(rCol);
												dbMsg += "[delID=" + delID ;///////////////////////////////////
												pUri = MediaStore.Audio.Playlists.Members.getContentUri("external",Long.valueOf(String.valueOf(MuList.this.tuikaSakiListID)) );
												dbMsg += "]pUri=" + pUri.toString() ;///////////////////////////////////
												delC = delOneLineBody(pUri , Integer.valueOf(delID));			//プレイリストから指定された行を削除する
												dbMsg += ">削除>" + delC + "件" ;///////////////////////////////////
												break;
											case MyConstants.v_artist:							//196 アーティスト
												cCursor = artistList_yomikomiLoop( cCursor );					//, comp
												break;
											case MyConstants.v_alubum:							//アルバム
												break;
											case MyConstants.v_titol:						//タイトル
												break;
											case CONTEXT_saikin_sisei0:		//655;最近再生された楽曲(重複削除)
												dbMsg +=  ",最近追加リスト;CONTEXT_saikin_sisei0" ;
												@SuppressLint("Range") String tStr = cCursor.getString(cCursor.getColumnIndex( MediaStore.Audio.Playlists.Members.AUDIO_ID));
												dbMsg += ",AUDIO_ID=" + tStr ;///////////////////////////////////
												dbMsg += "(" + testList.size() +")" ;///////////////////////////////////
												if(ORGUT.isInListString(testList , tStr)){		//渡された文字が既にリストに登録されていればtrueを返す
													rCol = cCursor.getColumnIndex(MediaStore.Audio.Playlists.Members._ID);
													delID = cCursor.getString(rCol);
													pUri = MediaStore.Audio.Playlists.Members.getContentUri("external",Long.valueOf(String.valueOf(MuList.this.tuikaSakiListID)) );
													dbMsg += ",pUri=" + pUri.toString() ;///////////////////////////////////
													delC = delOneLineBody(pUri , Integer.valueOf(delID));			//プレイリストから指定された行を削除する
													dbMsg += ">削除>" + delC + "件" ;///////////////////////////////////
												}else{
													testList.add(tStr);
													dbMsg += "→" + testList.size() +")" ;///////////////////////////////////
												}
												//				myLog(TAG,dbMsg);
												break;
											case CONTEXT_saikin_sisei:			//656;最近再生リストの準備
												dbMsg +=  ",設定=" + MuList.this.pref_saikin_sisei ;///////////////////////////////////
												dbMsg +=  ",登録=" + pdMaxVal ;//////////////////////////////cursor.getCount()/////
												int amari = cCursor.getCount()-Integer.valueOf(MuList.this.pref_saikin_sisei) - cCursor.getPosition();
												dbMsg +=  ",amari=" + amari ;///////////////////////////////////
												if( 0 < amari){
													rCol = cCursor.getColumnIndex(MediaStore.Audio.Playlists.Members._ID);
													delID = cCursor.getString(rCol);
													pUri = MediaStore.Audio.Playlists.Members.getContentUri("external",Long.valueOf(String.valueOf(MuList.this.tuikaSakiListID)) );
													dbMsg += ",pUri=" + pUri.toString() ;///////////////////////////////////
													delC = delOneLineBody(pUri , Integer.valueOf(delID));			//プレイリストから指定された行を削除する
													dbMsg += ">削除>" + delC + "件" ;///////////////////////////////////
												}else{
													cCursor.moveToLast();
												}
												break;
											case CONTEXT_rumdam_saiseizumi:		//ランダム再生準備
												@SuppressLint("Range") String AUDIO_ID = cCursor.getString(cCursor.getColumnIndex( MediaStore.Audio.Playlists.Members.AUDIO_ID));
												dbMsg += ",AUDIO_ID=" + AUDIO_ID ;///////////////////////////////////
												dbMsg += "(" + MuList.this.plSL.size() +")" ;///////////////////////////////////
												if(ORGUT.isInListString(MuList.this.plSL , AUDIO_ID)){		//渡された文字が既にリストに登録されていればtrueを返す
												}else{
													MuList.this.plSL.add(AUDIO_ID);
													dbMsg += "→" + MuList.this.plSL.size() +")" ;///////////////////////////////////
												}
												//				myLog(TAG,dbMsg);
												break;
											case CONTEXT_REPET_WR:					//リピート再生リストレコード書込み
												cCursor = repeatPlayMake_body(cCursor);		//リピート再生
												break;
											//						default:
											//							break;
										}
										pdCoundtVal =  cCursor.getPosition() +1;
										if(progStep<=pdCoundtVal){
											MuList.this.setProgVal(pdCoundtVal);
											progStep += prgInterval;
										}
										dbMsg += "(reqCode=" +reqCode+")pdCoundtVal="+pdCoundtVal + "/" + pdMaxVal ;
										long nTime = System.currentTimeMillis() ;
									}while( cCursor.moveToNext() ) ;				//pdCoundtVal <  pdMaxVal
									MuList.this.setProgVal(pdCoundtVal);
								}
								break;
						}
						//	Thrd.sleep(200);			//書ききる為の時間（100msでは不足）

						dbMsg += ",最終[" + id +"]に追加";///////////////////		AllSongs.this.
//						switch(reqCode) {
//							case pt_CreateKaliList:							//803;仮リスト作成
//								endTS(Kari_db);
//								break;
//							case pt_CreateAllSongs:					//805;全曲リスト作成
//							case pt_CompList:									//806;全曲リストにコンピレーション追加
//								endTS(Zenkyoku_db);
//								break;
//							case pt_artistList:
//								endTS(artist_db);
//								break;
//						}
//						Thread.sleep(300);			//書ききる為の時間（100msでは不足）
				//	}
					new Handler(Looper.getMainLooper())
							.post(() -> onPostExecute());
					myLog(TAG,dbMsg );
				} catch (Exception e) {
					myErrorLog(TAG,dbMsg+" でエラー発生；"+e.toString());
				}
			}
		}

		/**
		 * ploglessTaskの入口
		 * */
		void execute(int req_code , Cursor cursor, String plog_title ,String plog_message,int maxVal) {
			final String TAG = "execute[ploglessTask]";
			String dbMsg="";
			try {
				onPreExecute(req_code , cursor, plog_title ,plog_message,maxVal);
				executorService.submit(new MuList.ploglessTask.TaskRun());
				myLog(TAG,dbMsg );
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+" でエラー発生；"+e.toString());
			}
		}

		/**
		 * ploglessTaskの前処理
		 * Backgroundメソッドの実行前にメインスレッドで実行
		 * */
		void onPreExecute(int req_code , Cursor cursor, String plog_title ,String plog_message,int maxVal) {           //,SQLiteDatabase write_db
			final String TAG = "onPreExecute";
			String dbMsg="[ploglessTask]";
			try {
				this.reqCode = req_code;
				dbMsg += "[" + this.reqCode + "]";
				this.pdTitol = plog_title;
				this.pdMessage = plog_message;
				dbMsg += this.pdTitol + ":" + this.pdMessage ;
				this.cCursor = cursor;
				if(this.cCursor != null){
					dbMsg += " , " + this.cCursor.getCount() + "件";
					pdMaxVal = this.cCursor.getCount();
				}else{
					pdMaxVal = maxVal;
				}
				dbMsg +=",getMax=" + pdMaxVal;
				createPrgressDlog(pdTitol,pdMessage , pdMaxVal );
				myLog(TAG,dbMsg );
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+" でエラー発生；"+e.toString());
			}
		}

		/**
		 * ploglessTaskの終端処理
		 * Backgroundメソッドの実行前にメインスレッドで実行
		 * */
		void onPostExecute() {
			final String TAG = "onPostExecute";
			String dbMsg = "[MuList・lvID]";
			try{
				if(plogDialogView != null){
					plogDialogView.dismiss();
				}
				dbMsg +=  "reqCode=" + reqCode;/////////////////////////////////////
				switch(reqCode) {
					case CONTEXT_listup_jyunbi:					//リストアップ準備;アーティスト/作曲者名
						listSyuuseiJyunbi2();								//リスト修正準備；アーティストと作曲者名リスト
						break;
					case CONTEXT_listup_jyunbi2:				//リストアップ準備;2階層リスト化
						listSyuuseiStart();							//選択したアイテムの位置修正
						break;
					case MENU_hihyoujiArtist:					//非表示になったアーティストの修正処理
//						cursor.close();
						hihyoujiArtist2();								//非表示アーティスト対策；アーティストと作曲者名リスト
						break;
					case MENU_hihyoujiArtist2:		//非表示になったアーティストの修正処理;2階層化
						hihyoujiArtistStart();								//非表示アーティスト対策
						break;
					case MENU_TAKAISOU:				//537 多階層リスト選択選択中
					case MENU_MUSCK_PLIST:			//540	プレイリスト選択中
					case MyConstants.PUPRPOSE_lIST:
						CreatePLListEnd();				//プレイリストの内容取得
						break;
					case CONTEXT_del_playlist:			//648 このリストを削除
						deletPlayListEnd();					//指定したプレイリストを削除する
						break;
					case CONTEXT_del_playlis_membert:			//649 	リストの中身を削除
//						playLists.close();
						break;
					case CONTEXT_saikintuika0:
						dbMsg +=  ",最近追加リストの記述" ;
						saikin_tuika_mod_end();				//最近追加された楽曲の抽出(継続処理)
						break;
					case MENU_2KAISOU:				//536;2階層リスト選択選択中
					case MENU_TAKAISOU2:				//538多階層リスト書き込み中
					case MyConstants.PUPRPOSE_SONG:
						plWrightEnd();					//プレイリストの描画
						break;
					case MENU_infoKaisou:			//539 ;情報付きリスト書き込み中:CreatePLListEnd
						setHeadImgList(plAL );				//イメージとサブテキストを持ったリストを構成
						break;
					case CONTEXT_saikintuika_end:
						dbMsg +=  ",最近追加リストのプレイリスト作成" ;
						saikin_tuika_yomi_end( );		//最近追加された楽曲の書込み結果の表示
						break;
					case CONTEXT_saikintuika_Wr:				//最近追加リストのファイル作成
						saikin_tuika_end();
						break;
//			case CONTEXT_saikintuika_read:
//				dbMsg +=  ",最近追加リストをデータベースから読み出す;";
//				readSaikinTuikaDB_end();				//最近追加された楽曲fairuyomiro
//				break;
					case listType_2ly:				//アーティストの単階層リストとalbumとtitolの２階層
						String subTStr = getResources().getString(R.string.pp_artist)+artistSL.size() +  getResources().getString(R.string.comon_nin);			//アーティスト
						subHTF.setText(subTStr);					//ヘッダーのサブテキスト表示枠
						makePlainList( artistSL);			//階層化しないシンプルなリスト
						break;
					case listType_2ly2:				//  listType_2ly + 1;albumとtitolの２階層
//						playLists.close();
						plAlbumTitolEnd();		//playlistIdで指定したMediaStore.Audio.Playlists.Membersの内容取得
						break;
					case CONTEXT_saikin_sisei0:			//655 最近再生リストの重複削除
						saikin_sisei_jyunbi_end();				//最近再生された楽曲(重複削除)
						break;
					case CONTEXT_saikin_sisei:			//656最近再生リストの準備
						saikin_sisei_end();				//最近再生された楽曲の抽出(継続処理)
						break;
					case CONTEXT_rumdam_saiseizumi:		//642 ランダム再生準備
						randumPlay_make();					//ランダム再生;リスト作成
						break;
					case CONTEXT_rumdam_redrow:			//643  ランダム再生リストの消去
//						playLists.close();
						randumPlay2();
						break;
					case CONTEXT_rumdam_arrayi:			//644 ランダム再生;配列書込み
						randumPlayListWr();					//ランダム再生;リストに書き込む
						break;
					case CONTEXT_rumdam_wr:				//645 ランダム再生リストの書込み
					case CONTEXT_REPET_WR:				//647 リピート再生リストレコード書込み
						makedListBack();						//ランダム再生;プレイヤーに戻る
						break;
					case CONTEXT_REPET_CLEAN:			//67 リピート再生リストの既存レコード消去
						repeatPlayMake();			//リピート再生
						break;
					case CONTEXT_add_request:					//リクエストリスト
						requestListTuika();								//リクエストリスト作成・楽曲追加
						break;
					case CONTEXT_dell_request:					//リクエスト解除
						requestListResetEnd();
						break;
					case MyConstants.v_artist:			//アーティスト
						artistList_yomikomiEnd();					//アーティストリストの終了処理
						break;
					case MyConstants.v_alubum:							//アルバム
						break;
					case MyConstants.v_titol:						//タイトル
						break;
					case CONTEXT_M3U2_PL:		//汎用プレイリストをAndroidのプレイリストに転記
						allSongArtistList();
//				allSongEnd( );
						break;

//			default:
//				break;
				}
				myLog(TAG, dbMsg);
			}catch (Exception e) {
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
		}
	}


	/**
	 * 第一引数;タスク開始時:doInBackground()に渡す引数の型,
	 * 第二引数;進捗率を表示させるとき:onProgressUpdate()に使う型,
	 * 第三引数;タスク終了時のdoInBackground()の返り値の型			AsyncTaskResult<Object>
	 * 		http://d.hatena.ne.jp/tomorrowkey/20100824/1282655538
	 * 		http://pentan.info/android/app/multi_thread.html**/
//	public class plogTask extends AsyncTask<Object, Integer , AsyncTaskResult<Integer>> {		//myResult	元は<Object, Integer, Boolean>
//		private Context cContext = null;
//		private plogTaskCallback callback;
//		OrgUtil ORGUT;					//自作関数集
//		public long start = 0;				// 開始時刻の取得
//		public Boolean isShowProgress;
//		public ProgressDialog progressDialog = null;	// 処理中ダイアログ	ProgressDialog	AlertDialog
//		public int reqCode = 0;						//処理番号
//		public CharSequence pdTitol;			//ProgressDialog のタイトルを設定
//		public CharSequence pdMessage;			//ProgressDialog のメッセージを設定
//		public CharSequence pdMessage_stok;			//ProgressDialog のメッセージを設定
//		public int pdMaxVal = 0;					//ProgressDialog の最大値を設定 (水平の時)
//		public int pdStartVal=0;					//ProgressDialog の初期値を設定 (水平の時)
//		public int pdCoundtVal=0;					//ProgressDialog表示値
//		public int pd2MaxVal;					//ProgressDialog の最大値を設定 (水平の時)
//		public int pd2CoundtVal;					//ProgressDialog表示値
//		public String _numberFormat = "%d/%d";
//		public  NumberFormat _percentFormat = NumberFormat.getPercentInstance();
//
//		public Boolean preExecuteFiniSh=false;	//ProgressDialog生成終了
//		public Bundle extras;
//
//		long stepKaisi = System.currentTimeMillis();		//この処理の開始時刻の取得
//		long stepSyuuryou;		//この処理の終了時刻の取得
//
//		public plogTask(Context cContext , plogTaskCallback callback ,CharSequence pdTitol ,CharSequence pdMessage ,int pdMaxVal){
//			super();
//			//,int reqCode , String pdTitol , String pdMessage ,int pdMaxVal ,int pd2CoundtVal,int pd2MaxVal){
//			final String TAG = "plogTask[plogTask]";
//			try{
//				ORGUT = new OrgUtil();				//自作関数集
//				boolean dCancel = false;
//				String dbMsg = "cContext="+cContext;///////////////////////////
//				if( cContext != null ){
//					this.cContext = cContext;
//					this.callback = callback;
//					dbMsg += ",callback="+callback;///////////////////////////
//					dbMsg += ",callback="+callback;///////////////////////////
//					dbMsg += ",Titol=" + pdTitol;
//					dbMsg += ",Message=" + pdMessage;
//					this.pdMessage = pdMessage;
//					dbMsg += ",Max=" + pdMaxVal;
//					this.pdMaxVal = pdMaxVal;
//					dbMsg += "reqCode=" + MuList.this.reqCode;
//					progressDialog = new ProgressDialog(cContext);			//.getApplicationContext()
//					progressDialog.setTitle(pdTitol);
//					progressDialog.setMessage(pdMessage);
//					progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);			// プログレスダイアログのスタイルを水平スタイルに設定します
//					progressDialog.setMax(pdMaxVal);			// プログレスダイアログの最大値を設定します
//					if( MuList.this.reqCode == MENU_TAKAISOU  ){
//						dbMsg += ",多階層リスト";
//						dCancel =true;
//					}
//					progressDialog.setCancelable(dCancel);			// プログレスダイアログのキャンセルが可能かどうかを設定します
//					progressDialog.setOnCancelListener(new OnCancelListener() {
//						@Override
//						public void onCancel(DialogInterface dialog) {
//							final String TAG = "onCancel[plogTask]";
//							String dbMsg = "開始";
//							try {
//	//							dbMsg = "reqCode=" + MuList.this.reqCode;
//	//							reqCode = MENU_infoKaisou;								//537;情報付き曲名リスト書き込み中
//	//							MuList.this.plef_tankaisou = true;									//階層リストを単階層に変更
//	//							String pdMessage  = getResources().getString(R.string.playlist_namae_saikintuika) + plogTask.this.pdMaxVal + getResources().getString(R.string.comon_kyoku);		//最近追加	曲</string>
//	//							CreatePLList( MuList.this.nowList_id , pdMessage);
//								myLog(TAG, dbMsg);
//							} catch (Exception e) {
//								myErrorLog(TAG,"でエラー発生；"+e.toString());
//							}
//						}
//					});
//					progressDialog.show();
//					dbMsg += ">isShowing>" + progressDialog.isShowing();
//				}
//		//		myLog(TAG, dbMsg);
//			} catch (Exception e) {
//				myErrorLog(TAG,"でエラー発生；"+e.toString());
//			}
//		}
//
//		//		http://greety.sakura.ne.jp/redo/2011/02/asynctask.html
//		/**
//		 * 最初にUIスレッドで呼び出されます。 , UIに関わる処理をします。
//		 * doInBackgroundメソッドの実行前にメインスレッドで実行されます。
//		 * 非同期処理前に何か処理を行いたい時などに使うことができます。 */
//		@Override
//		protected void onPreExecute() {			// onPreExecuteダイアログ表示
//			super.onPreExecute();
//			final String TAG = "onPreExecute";
//			String dbMsg = "[MuList.plogTask]";
//			try {
//				dbMsg += ":reqCode="+reqCode;///////////////////////////
//				dbMsg +=  ",pdTitol="+pdTitol;///////////////////////////
//				dbMsg +=  ",Message="+pdMessage;///////////////////////////
//				dbMsg += ",pdMaxVal="+pdMaxVal;///////////////////////////
//				myLog(TAG, dbMsg);
//			}catch (Exception e) {
//				myErrorLog(TAG ,  dbMsg + "で" + e);
//			}
//		}
//
//		public  Uri cUri = null  ;							//4]
//		public  String where = null;
//		public  SQLiteStatement stmt = null ;			//6；SQLiteStatement
//		public SQLiteDatabase tdb;
//		public  ContentValues cv = null ;						//7；SQLiteStatement
//		@SuppressLint("Range")
//		@SuppressWarnings("resource")
//		/**
//		 * doInBackground
//		 * ワーカースレッド上で実行されます。 このメソッドに渡されるパラメータの型はAsyncTaskの一つ目のパラメータです。
//		 * このメソッドの戻り値は AsyncTaskの三つ目のパラメータです。
//		 * メインスレッドとは別のスレッドで実行されます。
//		 * 非同期で処理したい内容を記述します。 このメソッドだけは必ず実装する必要があります。
//		 *0 ;reqCode, 1; pdMessage , 2;pdMaxVal ,3:cursor , 4;cUri , 5;where , 6;stmt , 7;cv ,  8;omitlist , 9;tList );
//		 * */
//		@Override
//		public AsyncTaskResult<Integer> doInBackground(Object... params) {//InParams続けて呼ばれる処理；第一引数が反映されるのはここなのでここからダイアログ更新 バックスレッドで実行する処理;getProgress=0で呼ばれている
//			final String TAG = "doInBackground";
//			String dbMsg = "[MuList.plogTask]";
//			try {
//				Cursor cursor = null  ;					//3]
//				List<String> testList = null;	//最近再生された楽曲のID
//				String comp = null;
//				SQLiteDatabase wrDB;
//				pdCoundtVal = 0;
//				this.reqCode = (Integer) params[0] ;			//0.処理
//				dbMsg += "reqCode = " + reqCode;
//
//				CharSequence setStr=(CharSequence) params[1];				//2.次の処理に渡すメッセージ
//				if(setStr !=null ){
//					if(! setStr.equals(pdMessage)){
//						pdMessage = setStr;
//						this.pdMessage = setStr;
//						dbMsg += ",Message = " + pdMessage;
//		//				change2ndText () ;			//ProgBar2の表示値設定
//					}
//				}
//				switch(reqCode) {
//					case CONTEXT_listup_jyunbi2:	//リストアップ準備;2階層リスト化
//					case MENU_hihyoujiArtist2:		//非表示になったアーティストの修正処理;2階層化
//					case MENU_infoKaisou:			//情報付きリスト書き込み中
//					case listType_2ly:				//アーティストの単階層リストとalbumとtitolの２階層
//					case MENU_2KAISOU:				//2階層リスト選択選択中
//					case MENU_TAKAISOU2:				//多階層リスト書き込み中
//					case CONTEXT_rumdam_arrayi:		//ランダム再生;配列書込み
//					case CONTEXT_saikintuika_end:	//最近追加リストのプレイリスト作成
//					case CONTEXT_saikintuika_Wr:				//最近追加リストのファイル作成
//					case CONTEXT_rumdam_wr:			//ランダム再生リストの書込み
//						pdMaxVal = (int) params[2];		//プレイリスト用ArrayList
//						dbMsg += ", pdMaxVal = " + pdMaxVal + "件"  ;
//						break;
//					case CONTEXT_M3U2_PL:		//汎用プレイリストをAndroidのプレイリストに転記
//						break;
//					default:
//						cursor = (Cursor) params[2] ;			//2
//						pdMaxVal = cursor.getCount();
//						dbMsg += ", cursor = " + pdMaxVal + "件"  ;
//						break;
//				}
//
//				if(reqCode == CONTEXT_saikin_sisei0  ){	//最近再生された楽曲(重複削除)
//					testList =  new ArrayList<String>();	//最近再生された楽曲のID
//				}
//
//
//				long vtWidth = 200;		// 更新間隔
//				if(pdMaxVal<200){
//					vtWidth = 100;
//					if(pdMaxVal<100){
//						vtWidth = 20;
//					}
//				}
//				dbMsg += ", 更新間隔 = " + vtWidth  ;
//				long vTime = System.currentTimeMillis() + vtWidth;		// 更新タイミング
//				pdTitol=(CharSequence) params[3] ;			//3:
//				dbMsg += ", pdTitol = " + pdTitol ;
//				switch(reqCode) {
//					case CONTEXT_listup_jyunbi2:	//リストアップ準備;2階層リスト化
//					case MENU_hihyoujiArtist2:		//非表示になったアーティストの修正処理;2階層化
//					case MENU_infoKaisou:			//情報付きリスト書き込み中
//					case listType_2ly:				//アーティストの単階層リストとalbumとtitolの２階層
//					case MENU_2KAISOU:				//2階層リスト選択選択中
//					case MENU_TAKAISOU2:							//多階層リスト書き込み中
//					case CONTEXT_saikintuika_end:	//最近追加リストのプレイリスト作成
//					case CONTEXT_saikintuika_Wr:				//最近追加リストのファイル作成
//	//				case CONTEXT_saikintuika_read:
//					case CONTEXT_rumdam_arrayi:		//ランダム再生;配列書込み
//					case CONTEXT_rumdam_wr:			//ランダム再生リストの書込み
//					case CONTEXT_M3U2_PL:		//汎用プレイリストをAndroidのプレイリストに転記
//						dbMsg += ", pdCoundtVal = " + pdCoundtVal ;
//						for(pdCoundtVal =0 ; pdCoundtVal < pdMaxVal;pdCoundtVal ++){
//							switch(reqCode) {
//							case CONTEXT_listup_jyunbi2:	//リストアップ準備;2階層リスト化
//							case MENU_hihyoujiArtist2:		//非表示になったアーティストの修正処理;2階層化
//								listSyuuseiJyunbi2Body( pdCoundtVal);								//リストアップ準備;2階層リスト化
//								break;
//							case MENU_infoKaisou:			//537;情報付きリスト書き込み中
//								plInfoSinglBody(pdCoundtVal);		//情報付き単階層プレイリストの描画
//								break;
//							case listType_2ly:				//2;アーティストの単階層リストとalbumとtitolの２階層
//								plOneBody(pdCoundtVal);
//								break;
//							case MENU_2KAISOU:				//534;2階層リスト選択選択中
//								plTowBody(pdCoundtVal);		//単階層+2階層プレイリストの2階層作成							break;
//								break;
//							case MENU_TAKAISOU2:			//536;多階層リスト書き込み中
//								plWrightBody(pdCoundtVal);	//3階層プレイリストの描画
//								break;
//							case CONTEXT_saikintuika_end:	//654 最近追加リストのプレイリスト作成
//								saikin_tuika_yomi_body( pdCoundtVal );				//最近追加された楽曲の抽出(指定IF)
//								break;
//							case CONTEXT_saikintuika_Wr:				//最近追加リストのファイル作成
//								playListWr_body(pdCoundtVal );
//								break;
//							case CONTEXT_rumdam_arrayi:		//ランダム再生;配列書込み
//								pdCoundtVal = randumPlay_array_body(pdCoundtVal);			//ランダム再生;ランダム追加
//								break;
//							case CONTEXT_rumdam_wr:			//ランダム再生リストの書込み
//								randumPlayListWr_body(pdCoundtVal);			//ランダム再生;リストに書き込む
//								break;
//							case CONTEXT_M3U2_PL:		//汎用プレイリストをAndroidのプレイリストに転記
//								m3U2PlayList_body( pdCoundtVal );
//								break;
//							}
//							publishProgress( pdCoundtVal );		//progressDialog.progBar1.setProgress(step1);
//						}
//						dbMsg += ">>" + pdCoundtVal ;
//						break;
//					default:
//						if(cursor.moveToFirst()){
//							dbMsg +=  "；ループ前；"+ cursor.getPosition() +"/ " + pdMaxVal;	/////////////////////////////////////////////////////////////
//							switch(reqCode) {
//		//						case MyConstants.v_artist:							//アーティスト
//		//							comp = getResources().getString(R.string.comon_compilation);			//コンピレーション
//		//							break;
//								case MyConstants.v_alubum:							//アルバム
//									break;
//								case MyConstants.v_titol:						//タイトル
//									break;
//			//					default:
//			//						break;
//							}
//							String delID =null;
//							Uri pUri =null;
//							int delC = 0;
//							do{
//								dbMsg +=  "[cursor="+  cursor.getPosition() +  "/" + pdMaxVal + ";progressDialog=" + progressDialog.getProgress() +"]" ;//cursor.getCount()
//								switch(reqCode) {
//								case CONTEXT_listup_jyunbi:					//リストアップ準備;アーティスト/作曲者名
//								case MENU_hihyoujiArtist:					//非表示になったアーティストの修正処理
//									cursor =  listSyuuseiJyunbiBody( cursor);								//リスト修正準備；アーティストと作曲者名リスト
//									break;
////								case MENU_TAKAISOU:				//537;多階層リスト選択選択中
////								case listType_2ly2:				//  listType_2ly + 1;albumとtitolの２階層
////								case MENU_MUSCK_PLIST:			//プレイリスト選択中
////									cursor = CreatePLListBody(cursor);		//プレイリストの内容取得
////									break;
//								case CONTEXT_del_playlist:					//624;このリストを削除
//								case CONTEXT_del_playlis_membert:			//627;リストの中身を削除
//								case CONTEXT_rumdam_redrow:					//634 ランダム再生リストの消去
//								case CONTEXT_REPET_CLEAN:					//リピート再生リストの既存レコード消去
//								case CONTEXT_add_request:					//リクエストリスト
//								case CONTEXT_dell_request:					//リクエスト解除
//									delID = cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Playlists.Members._ID));
//									dbMsg += "[delID=" + delID ;///////////////////////////////////
//									pUri = MediaStore.Audio.Playlists.Members.getContentUri("external",Long.valueOf(String.valueOf(MuList.this.tuikaSakiListID)) );
//									dbMsg += "]pUri=" + pUri.toString() ;///////////////////////////////////
//									delC = delOneLineBody(pUri , Integer.valueOf(delID));			//プレイリストから指定された行を削除する
//									dbMsg += ">削除>" + delC + "件" ;///////////////////////////////////
//									break;
//		//							case CONTEXT_saikintuika_read:
//		//								dbMsg += ",最近追加リストをデータベースから読み出す;" ;
//		//								int playOrder = cursor.getPosition() +1;
//		//								readSaikinTuikaDB_body(cursor , playOrder);				//最近追加された楽曲fairuyomiro
//		//								break;
//								case MyConstants.v_artist:							//195 アーティスト
//									cursor = artistList_yomikomiLoop( cursor );					//, comp
//									break;
//								case MyConstants.v_alubum:							//アルバム
//									break;
//								case MyConstants.v_titol:						//タイトル
//									break;
//								case CONTEXT_saikin_sisei0:		//655;最近再生された楽曲(重複削除)
//									dbMsg +=  ",最近追加リスト;CONTEXT_saikin_sisei0" ;
//									@SuppressLint("Range") String tStr = cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Playlists.Members.AUDIO_ID));
//									dbMsg += ",AUDIO_ID=" + tStr ;///////////////////////////////////
//									dbMsg += "(" + testList.size() +")" ;///////////////////////////////////
//									if(ORGUT.isInListString(testList , tStr)){		//渡された文字が既にリストに登録されていればtrueを返す
//										delID = cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Playlists.Members._ID));
//										pUri = MediaStore.Audio.Playlists.Members.getContentUri("external",Long.valueOf(String.valueOf(MuList.this.tuikaSakiListID)) );
//										dbMsg += ",pUri=" + pUri.toString() ;///////////////////////////////////
//										delC = delOneLineBody(pUri , Integer.valueOf(delID));			//プレイリストから指定された行を削除する
//										dbMsg += ">削除>" + delC + "件" ;///////////////////////////////////
//									}else{
//										testList.add(tStr);
//										dbMsg += "→" + testList.size() +")" ;///////////////////////////////////
//									}
//					//				myLog(TAG,dbMsg);
//									break;
//								case CONTEXT_saikin_sisei:			//656;最近再生リストの準備
//									dbMsg +=  ",設定=" + MuList.this.pref_saikin_sisei ;///////////////////////////////////
//									dbMsg +=  ",登録=" + pdMaxVal ;//////////////////////////////cursor.getCount()/////
//									int amari = cursor.getCount()-Integer.valueOf(MuList.this.pref_saikin_sisei) - cursor.getPosition();
//									dbMsg +=  ",amari=" + amari ;///////////////////////////////////
//									if( 0 < amari){
//										delID = cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Playlists.Members._ID));
//										pUri = MediaStore.Audio.Playlists.Members.getContentUri("external",Long.valueOf(String.valueOf(MuList.this.tuikaSakiListID)) );
//										dbMsg += ",pUri=" + pUri.toString() ;///////////////////////////////////
//										delC = delOneLineBody(pUri , Integer.valueOf(delID));			//プレイリストから指定された行を削除する
//										dbMsg += ">削除>" + delC + "件" ;///////////////////////////////////
//									}else{
//										cursor.moveToLast();
//									}
//									break;
//								case CONTEXT_rumdam_saiseizumi:		//ランダム再生準備
//									@SuppressLint("Range") String AUDIO_ID = cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Playlists.Members.AUDIO_ID));
//									dbMsg += ",AUDIO_ID=" + AUDIO_ID ;///////////////////////////////////
//									dbMsg += "(" + MuList.this.plSL.size() +")" ;///////////////////////////////////
//									if(ORGUT.isInListString(MuList.this.plSL , AUDIO_ID)){		//渡された文字が既にリストに登録されていればtrueを返す
//									}else{
//										MuList.this.plSL.add(AUDIO_ID);
//										dbMsg += "→" + MuList.this.plSL.size() +")" ;///////////////////////////////////
//									}
//					//				myLog(TAG,dbMsg);
//									break;
//								case CONTEXT_REPET_WR:					//リピート再生リストレコード書込み
//									cursor = repeatPlayMake_body(cursor);		//リピート再生
//									break;
//			//						default:
//			//							break;
//								}
//								pdCoundtVal =  cursor.getPosition() +1;
//								dbMsg += "(reqCode=" +reqCode+")pdCoundtVal="+pdCoundtVal + "/" + pdMaxVal ;
//								long nTime = System.currentTimeMillis() ;
//								if(nTime  > vTime ){
//									publishProgress( pdCoundtVal );		//progressDialog.progBar1.setProgress(step1);
//									if( vtWidth > 1 ){
//										vtWidth = vtWidth/2;
//									}
//									vTime = System.currentTimeMillis() + vtWidth;		// 更新タイミング
//								}
//							}while( cursor.moveToNext() ) ;				//pdCoundtVal <  pdMaxVal
//						}
//						break;
//			}
//		//	Thrd.sleep(200);			//書ききる為の時間（100msでは不足）
//			publishProgress( pdCoundtVal );		//progressDialog.progBar1.setProgress(step1);
//			stepSyuuryou = System.currentTimeMillis();		//この処理の終了時刻の取得
//			dbMsg += this.reqCode +";経過時間"+(int)((stepSyuuryou - stepKaisi)) + "[mS]";				//各処理の所要時間
//			myLog(TAG, dbMsg);
//			return AsyncTaskResult.createNormalResult( reqCode );
//		} catch (Exception e) {
//			myErrorLog(TAG ,  dbMsg + "で" + e);
//			return AsyncTaskResult.createNormalResult(reqCode) ;				//onPostExecuteへ
//		}
//	}
//
//	/**
//	 * onProgressUpdate
//	 * プログレスバー更新処理： UIスレッドで実行される doInBackground内でpublishProgressメソッドが呼ばれると、
//	 * UIスレッド上でこのメソッドが呼ばれます。   このメソッドの引数の型はAsyncTaskの二つ目のパラメータです。
//	 * メインスレッドで実行されます。非同期処理の進行状況をプログレスバーで 表示したい時などに使うことができます。*/
//		@Override
//		public void onProgressUpdate(Integer... values) {
//			final String TAG = "onProgressUpdate";
//			String dbMsg = "";
//			int progress = values[0];
//			try{
//				dbMsg +=  this.reqCode +")progress= " + progress;
//				progressDialog.setProgress(progress);
//				dbMsg += ">> " + progressDialog.getProgress();
//				dbMsg += "/" + progressDialog.getMax();///////////////////////////////////
////				myLog(TAG, dbMsg);
//			}catch (Exception e) {
//				myErrorLog(TAG ,  dbMsg + "で" + e);
//			}
//		}
//
//	/**
//	 * onPostExecute
//	 * doInBackground が終わるとそのメソッドの戻り値をパラメータとして渡して onPostExecute が呼ばれます。
//	 * このパラメータの型は AsyncTask を extends するときの三つめのパラメータです。
//	 *  バックグラウンド処理が終了し、メインスレッドに反映させる処理をここに書きます。
//	 *  doInBackgroundメソッドの実行後にメインスレッドで実行されます。
//	 *  doInBackgroundメソッドの戻り値をこのメソッドの引数として受け取り、その結果を画面に反映させることができます。*/
//	@Override
//	public void onPostExecute(AsyncTaskResult<Integer> ret){	// タスク終了後処理：UIスレッドで実行される AsyncTaskResult<Object>
//		super.onPostExecute(ret);
//			final String TAG = "onPostExecute[plogTask]";
//			String dbMsg = "";
//			try{
//				Thread.sleep(100);			//書ききる為の時間（100msでは不足）
//				reqCode = ret.getReqCode();
//				dbMsg += "終了；reqCode=" + reqCode +"(終端"+ pdCoundtVal +")";
//				dbMsg += ",callback = " + callback;	/////http://techbooster.org/android/ui/1282/
//				dbMsg += "[ " + pdCoundtVal +  "/ " + pdMaxVal +"]";	/////http://techbooster.org/android/ui/1282/
//				progressDialog.dismiss();
//				callback.onSuccessplogTask(reqCode );		//1.次の処理;2.次の処理に渡すメッセージ
//				myLog(TAG, dbMsg);
//			}catch (Exception e) {
//				myErrorLog(TAG ,  dbMsg + "で" + e);
//			}
//		}
//	}  //public class plogTask
//
//	@Override
//	public void onSuccessplogTask(int reqCode) {															//①ⅵ3；
//		final String TAG = "onSuccessplogTask";
//		String dbMsg = "[MuList・lvID]";
//		try{
//			dbMsg +=  "reqCode=" + reqCode;/////////////////////////////////////
//			switch(reqCode) {
//			case CONTEXT_listup_jyunbi:					//リストアップ準備;アーティスト/作曲者名
//				listSyuuseiJyunbi2();								//リスト修正準備；アーティストと作曲者名リスト
//				break;
//			case CONTEXT_listup_jyunbi2:				//リストアップ準備;2階層リスト化
//				listSyuuseiStart();							//選択したアイテムの位置修正
//				break;
//			case MENU_hihyoujiArtist:					//非表示になったアーティストの修正処理
//				cursor.close();
//				hihyoujiArtist2();								//非表示アーティスト対策；アーティストと作曲者名リスト
//				break;
//			case MENU_hihyoujiArtist2:		//非表示になったアーティストの修正処理;2階層化
//				hihyoujiArtistStart();								//非表示アーティスト対策
//				break;
////			case MENU_TAKAISOU:				//537 多階層リスト選択選択中
////			case MENU_MUSCK_PLIST:			//540	プレイリスト選択中
////				CreatePLListEnd(playLists);				//プレイリストの内容取得
////				break;
//			case CONTEXT_del_playlist:			//648 このリストを削除
//				deletPlayListEnd();					//指定したプレイリストを削除する
//				break;
//			case CONTEXT_del_playlis_membert:			//649 	リストの中身を削除
//				playLists.close();
//				break;
//			case CONTEXT_saikintuika0:
//				dbMsg +=  ",最近追加リストの記述" ;
//				saikin_tuika_mod_end();				//最近追加された楽曲の抽出(継続処理)
//				break;
//			case MENU_2KAISOU:				//536;2階層リスト選択選択中
//			case MENU_TAKAISOU2:				//538多階層リスト書き込み中
//				plWrightEnd();					//プレイリストの描画
//				break;
//			case MENU_infoKaisou:			//539 ;情報付きリスト書き込み中
//				setHeadImgList(plAL );				//イメージとサブテキストを持ったリストを構成
//				break;
//			case CONTEXT_saikintuika_end:
//				dbMsg +=  ",最近追加リストのプレイリスト作成" ;
//				saikin_tuika_yomi_end( );		//最近追加された楽曲の書込み結果の表示
//				break;
//			case CONTEXT_saikintuika_Wr:				//最近追加リストのファイル作成
//				saikin_tuika_end();
//				break;
////			case CONTEXT_saikintuika_read:
////				dbMsg +=  ",最近追加リストをデータベースから読み出す;";
////				readSaikinTuikaDB_end();				//最近追加された楽曲fairuyomiro
////				break;
//			case listType_2ly:				//アーティストの単階層リストとalbumとtitolの２階層
//				String subTStr = getResources().getString(R.string.pp_artist)+artistSL.size() +  getResources().getString(R.string.comon_nin);			//アーティスト
//				subHTF.setText(subTStr);					//ヘッダーのサブテキスト表示枠
//				makePlainList( artistSL);			//階層化しないシンプルなリスト
//				break;
//			case listType_2ly2:				//  listType_2ly + 1;albumとtitolの２階層
//				playLists.close();
//				plAlbumTitolEnd();		//playlistIdで指定したMediaStore.Audio.Playlists.Membersの内容取得
//				break;
//			case CONTEXT_saikin_sisei0:			//655 最近再生リストの重複削除
//				saikin_sisei_jyunbi_end();				//最近再生された楽曲(重複削除)
//				break;
//			case CONTEXT_saikin_sisei:			//656最近再生リストの準備
//				 saikin_sisei_end();				//最近再生された楽曲の抽出(継続処理)
//				break;
//			case CONTEXT_rumdam_saiseizumi:		//642 ランダム再生準備
//				randumPlay_make();					//ランダム再生;リスト作成
//				break;
//			case CONTEXT_rumdam_redrow:			//643  ランダム再生リストの消去
//				playLists.close();
//				randumPlay2();
//				break;
//			case CONTEXT_rumdam_arrayi:			//644 ランダム再生;配列書込み
//				randumPlayListWr();					//ランダム再生;リストに書き込む
//				break;
//			case CONTEXT_rumdam_wr:				//645 ランダム再生リストの書込み
//			case CONTEXT_REPET_WR:				//647 リピート再生リストレコード書込み
//				makedListBack();						//ランダム再生;プレイヤーに戻る
//				break;
//			case CONTEXT_REPET_CLEAN:			//67 リピート再生リストの既存レコード消去
//				repeatPlayMake();			//リピート再生
//				break;
//			case CONTEXT_add_request:					//リクエストリスト
//				requestListTuika();								//リクエストリスト作成・楽曲追加
//				break;
//			case CONTEXT_dell_request:					//リクエスト解除
//				requestListResetEnd();
//				break;
//			case MyConstants.v_artist:			//アーティスト
//				artistList_yomikomiEnd();					//アーティストリストの終了処理
//				break;
//			case MyConstants.v_alubum:							//アルバム
//				break;
//			case MyConstants.v_titol:						//タイトル
//				break;
//			case CONTEXT_M3U2_PL:		//汎用プレイリストをAndroidのプレイリストに転記
//				allSongArtistList();
////				allSongEnd( );
//				break;
//
////			default:
////				break;
//			}
//			myLog(TAG, dbMsg);
//		}catch (Exception e) {
//			myErrorLog(TAG ,  dbMsg + "で" + e);
//		}
//	}

	public void oFR() {	 // onWindowFocusChanged , onResume の共通操作
		final String TAG = "oFR";
		String dbMsg = "";
		try{
			dbMsg += ",shigot_bangou;" + shigot_bangou;/////////////////////////////////////
			dbMsg += ",reqCode;" + reqCode;/////////////////////////////////////
			myLog(TAG,dbMsg);
//			if(0 < shigot_bangou ){
				switch(shigot_bangou) {
					case 0:		//インストール時
						break;
					case jyoukyou_bunki:		//204；現在の状態に見合った分岐を行う
						dbMsg +=",jyoukyouBunkiへ" ;
						jyoukyouBunki();			//①ⅱ現在の状態に見合った分岐を行う；全曲リストが出来ているか
						break;
					case make_list_head:		//500；ヘッド作成
					case shyou_Main:			//205 アーティストリスト作成後、作成するリストの振り分けに
//					if(reqCode== MENU_MUSCK_PLIST || ! nowList.equals(getResources().getString(R.string.listmei_zemkyoku))){			//536;プレイリスト選択中
//						String pdMessage = nowList;
//						if(plAL != null){
//							dbMsg += ",読み込まれているリスト= " + plAL.size() + "件";///////////////アクティビテイ方戻されたプレイリストID
//							dbMsg += ",戻されたプレイリストID= " + reqestList_id;///////////////アクティビテイ方戻されたプレイリストID
//							dbMsg += ",プリファレンスで詠み込んだプレイリストID= " + nowList_id;///////////////アクティビテイ方戻されたプレイリストID
//							if(reqestList_id != nowList_id){
//								nowList_id = reqestList_id;
//								CreatePLList( Long.valueOf(nowList_id) , pdMessage);		//プレイリストの内容取得			nowList_data,
//							}
//						}else{
//							CreatePLList(  Long.valueOf(nowList_id) , pdMessage);		//プレイリストの内容取得				nowList_data,
//						}
//					}else{
						shyouMain( yobidashiItem );		//onCleateの続きで仕掛けの仕込み		reqCode
//					}
						break;
					case MyConstants.CONTEXT_runum_sisei:			//184 ランダム再生
						randumPlay();			//ランダム再生
						break;
					case MyConstants.rp_artist:			//2131558547 アーティストリピート指定ボタン
					case MyConstants.rp_album:			//2131558548 アーティストリピート指定ボタン
					case MyConstants.rp_titol:			//2131558549 タイトルリピート指定ボタン
					case MyConstants.rp_point:			//2131558550 二点間リピート指定ボタン
						repeatPlay();			//リピート再生
						break;
					case quite_me:										//99;終了
						MuList.this.finish();
						break;
				}
//			}
			shigot_bangou = 0;
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}
	Handler handler = new Handler();
	public AlertDialog plogDialogView;
	public LinearLayout progress_dlog_ll;
	public ProgressBar progress_pb;
	public TextView progress_message_tv ;
	public TextView progress_titol_tv ;
	public TextView progress_val_tv ;
	public TextView progress_max_tv ;
	/**プログレスダイアログ表示*/
	public void createPrgressDlog(String pdTitol,String pdMessage , int pdMaxVal) {																		//操作対応②ⅰ
		final String TAG = "createPrgressDlog";
		String dbMsg = "";
		try{
			dbMsg += ORGUT.nowTime(true,true,true);
			dbMsg += ",pdTitol=" + pdTitol + ",pdMessage=" + pdMessage;
			LayoutInflater factory = LayoutInflater.from(MuList.this);
			final View inputView = factory.inflate(R.layout.dialog_progress, null);

			AlertDialog.Builder builder = new AlertDialog.Builder(MuList.this);
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.setTitle(pdTitol);
			builder.setView(inputView);
			progress_titol_tv = (TextView)inputView.findViewById(R.id.progress_titol);
			progress_titol_tv.setVisibility(View.GONE);
			progress_message_tv = (TextView)inputView.findViewById(R.id.progress_message);
			progress_pb = (ProgressBar)inputView.findViewById(R.id.progress);
			progress_max_tv =  (TextView)inputView.findViewById(R.id.progress_max);
			progress_val_tv =  (TextView)inputView.findViewById(R.id.progress_val);

			MuList.this.progress_titol_tv.setText(pdTitol);
			MuList.this.progress_message_tv.setText(pdMessage);
			if(0 < pdMaxVal){
				MuList.this.progress_pb.setMax(pdMaxVal);
				MuList.this.progress_max_tv.setText(String.valueOf(pdMaxVal));
				int pdCoundtVal=0;
				MuList.this.progress_pb.setProgress(pdCoundtVal);
				MuList.this.progress_val_tv.setText(String.valueOf(pdCoundtVal));
			}
//			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int whichButton) {
//
//				}
//			});
//
//			builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//				}
//			});
			MuList.this.plogDialogView = builder.create();
			MuList.this.plogDialogView.show();

//			if(handler == null){
//				handler = new Handler();
//			}
//			handler.post(new Runnable() {
//				public void run() {
//					final String TAG = "createPrgressDlog.run";
//					String dbMsg="";
//					try {
//
//					} catch (Exception e) {
//						myErrorLog(TAG, dbMsg + "でエラー発生；"+e.toString());
//					}
//				}		//run
//			});			//handler.post(new Runnable() {

			dbMsg += ORGUT.nowTime(true,true,true)+dbMsg;
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	//	return plogDialogView;
	}

	/**プログレスを更新して、maxに達すればダイアログを閉じる*/
	public void setProgVal(int progInt) {
		final String TAG = "setProgVal";
		String dbMsg = "";
		try{
			if(handler == null){
				handler = new Handler();
			}
			handler.post(new Runnable() {
				public void run() {
					final String TAG = "setProgressValue.run";
					String dbMsg="";
					try {
					//	pdCoundtVal = progInt;
						if(MuList.this.progress_pb.getProgress() <= MuList.this.progress_pb.getMax()){
							dbMsg += "pdCoundtVal=" + progress_pb.getProgress();
							MuList.this.progress_pb.setProgress(progInt);
							MuList.this.progress_val_tv.setText(String.valueOf(progInt));
							dbMsg += ">>" + MuList.this.progress_pb.getProgress()+ "/" + MuList.this.progress_pb.getMax();
						}else {
//                plogDialogView.dismiss();
//                dbMsg += ">>dismiss" ;
						}
					} catch (Exception e) {
						myErrorLog(TAG, dbMsg + "でエラー発生；"+e.toString());
					}
				}		//run
			});			//handler.post(new Runnable() {
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	/**
	 *  全曲リストの場合はプレイヤーでタップされたフィールドに応じてたリストを表示する */
	public void sigotoFuriwake(int reqC , String artistMei , String albumMei , String titolMei,String albumArt){		//表示するリストの振り分け		, List<Map<String, Object>> ItemAL
		final String TAG = "sigotoFuriwake";
		String dbMsg = "";
		try{
			dbMsg += ORGUT.nowTime(true,true,true);		/////////////////////////////////////
			String subTStr = "";
			String saisei_fname = getPrefStr( "pref_data_url" ,  "" , MuList.this);
			dbMsg +=  ",reqC=" + reqC +") artist;" + ";album=" + albumMei + ",titolMei=" + titolMei ;
//			if(nowList.equals(getResources().getString(R.string.listmei_zemkyoku))){
//				if(MyConstants.v_titol <= reqC){
//					reqC = MyConstants.v_artist;
//					dbMsg +=  ">>" + reqC ;
//				}
//			}
			switch(reqC) {
				case MyConstants.v_play_list:
					dbMsg +=",アーティストリストのヘッダータップ後、backCode=" + backCode;	//////////// 0始まりでposition= id ///////////////////////////////////////////////////////////
					headImgIV.setVisibility(View.GONE);								 // 表示枠を消す
					artistHTF.setVisibility(View.GONE);			//ヘッダーのアーティスト名表示枠
					mainHTF.setVisibility(View.VISIBLE);
					pl_sp.setVisibility(View.GONE);
					mainTStr = getResources().getString(R.string.listmei_list_all);
					mainHTF.setText(mainTStr);					//ヘッダーのメインテキスト表示枠
					subTStr =  plNameSL.size() +  getResources().getString(R.string.comon_ken);
					subHTF.setText(subTStr);					//ヘッダーのサブテキスト表示枠
					dbMsg += "、選択リスト名=" + sousalistName;
					listOfList_yomikomi( sousalistName);
					break;
				case MyConstants.v_artist:							//195;	2131558436 :アーティスト
					backCode = MyConstants.v_play_list;		//上のリスト
					dbMsg +=",アーティストリストタップ後、backCode=" + backCode;	//////////// 0始まりでposition= id ///////////////////////////////////////////////////////////
//					headImgIV.setVisibility(View.GONE);								 // 表示枠を消す
//					artistHTF.setVisibility(View.GONE);			//ヘッダーのアーティスト名表示枠
//					mainHTF.setVisibility(View.GONE);
//					pl_sp.setVisibility(View.VISIBLE);
//					makePlayListSPN(sousalistName);		//プレイリストスピナーを作成する
					artistList_yomikomi();
					dbMsg +=",pref_list_simple= " + pref_list_simple;	//////////// 0始まりでposition= id ///////////////////////////////////////////////////////////
					int selPosition = artistSL.indexOf(artistMei);
					int artistCount =  artistSL.size();
					if( pref_list_simple ){					//シンプルなリスト表示（サムネールなど省略）
					} else {
						selPosition = artistAL.indexOf(artistMei);
						artistCount =  artistAL.size();
					}
					dbMsg +=",artistSL= " + artistCount+ "件";	//////////// 0始まりでposition= id ///////////////////////////////////////////////////////////
					subTStr = getResources().getString(R.string.pp_artist)+ selPosition + "/"+ artistCount + getResources().getString(R.string.comon_nin);			//アーティスト
					subHTF.setText(subTStr);					//ヘッダーのサブテキスト表示枠
					myLog(TAG,dbMsg);
					lvID.setSelection(selPosition);
					lvID.setFocusable(true);
					lvID.setFocusableInTouchMode(true);
					lvID.requestFocus();
					break;
				case MyConstants.v_alubum:			//197 ; 2131558442	アルバム
					backCode = MyConstants.v_artist;		//上のリスト
					dbMsg +=",アルバムをタップ；alubum_tv <backCode=" + backCode;	//////////// 0始まりでposition= id ///////////////////////////////////////////////////////////
					pl_sp.setVisibility(View.GONE);	//プレイリスト選択
					headImgIV.setVisibility(View.GONE);								 // 表示枠を消す
					artistHTF.setVisibility(View.GONE);			//ヘッダーのアーティスト名表示枠

					dbMsg +="alubum_tv:artistMei= " + artistMei +",albumMei= " + albumMei;		//////////// 0始まりでposition= id ///////////////////////////////////////////////////////////
					mainHTF.setVisibility(View.VISIBLE);
					mainHTF.setText( artistMei);					//ヘッダーのメインテキスト表示枠		albumArtist		//		getSupportActionBar().setTitle(artistMei);
					MuList.this.albumAL = CreateAlbumList( artistMei , albumMei );		//リスト表示せずに曲リスト作成
					dbMsg += ",抽出できたアルバム=" + MuList.this.albumAL.size() + "枚 ";
					if( artistMei == null){
						artistMei = sousa_artist;
					}
					saisei_fname = saisei_fname + "";
					dbMsg += ",imanoJyoutai= " + imanoJyoutai ;		//+ ",saisei_fname= " + saisei_fname;         //200

					if ( imanoJyoutai == veiwPlayer && saisei_fname.equals("")){ 		//
						if( playingItem != null ){
							titolName =playingItem.title;		//曲名
							dbMsg += " ,タイトル= " + titolName;/////////////////////////////////////		this.title = title;
						}
						dbMsg += "(" + albumName;///////////////////////
//					if ( position  >= 0) {
						if( albumMei != null){
							MuList.this.albumArt =ORGUT.retAlbumArtUri(getApplicationContext() ,artistMei , albumMei );			//アルバムアートUriだけを返す	this ,
							dbMsg += ",albumArt=" + MuList.this.albumArt;
						}
//					}
						myLog(TAG, dbMsg);
						sigotoFuriwake(reqCode, artistMei , MuList.this.albumName  , MuList.this.titolName , MuList.this.albumArt);		//表示するリストの振り分け		titolAL ,
					} else {
						if( pref_list_simple ){					//シンプルなリスト表示（サムネールなど省略）
							makePlainList( albumList);			//階層化しないシンプルなリスト
						} else {
							setHeadImgList(albumAL );				//イメージとサブテキストを持ったリストを構成
						}
					}
					dbMsg +=",albumList= " + MuList.this.albumList.size() + "件";	//////////// 0始まりでposition= id ///////////////////////////////////////////////////////////
					if( 0 < MuList.this.albumList.size()){
						subTStr = MuList.this.albumList.size() + getResources().getString(R.string.pp_mai);
					}
					dbMsg +=",subTStr= " + subTStr;	//////////// 0始まりでposition= id ///////////////////////////////////////////////////////////
					subHTF.setText(subTStr);					//ヘッダーのサブテキスト表示枠
					break;
				case MyConstants.v_titol:						//198；2131558448 ;タイトル
					backCode = MyConstants.v_alubum;		//上のリスト
					dbMsg +=",アルバムをタップ；backCode=" + backCode;	//////////// 0始まりでposition= id ///////////////////////////////////////////////////////////
					dbMsg +=",titol_tv;albumMei; = " +creditArtistName +"の"+ albumMei;		//////////// 0始まりでposition= id ///////////////////////////////////////////////////////////
					dbMsg +=",titol_tv <backCode< " + backCode;	//////////// 0始まりでposition= id ///////////////////////////////////////////////////////////
					pl_sp.setVisibility(View.GONE);	//プレイリスト選択
					headImgIV.setVisibility(View.VISIBLE);								 // 表示枠を消す
					headImgIV.setImageResource(R.drawable.no_image);									//リサイズしたR.drawable.no_image
					titolAL = CreateTitleList(artistMei , albumMei , titolMei);
					subTStr =  titolAL.size() + getResources().getString(R.string.pp_kyoku);
					dbMsg +=",titol_tv;titol; = " + subTStr;		//////////// 0始まりでposition= id ///////////////////////////////////////////////////////////
					Bitmap mDummyAlbumArt = BitmapFactory.decodeResource(getResources(), R.drawable.no_image);
					albumArt =ORGUT.retAlbumArtUri(getApplicationContext() ,MuList.this.creditArtistName , albumMei );			//アルバムアートUriだけを返す			MuList.this ,
					dbMsg +=",albumArt= " + albumArt;		//////////// 0始まりでposition= id ///////////////////////////////////////////////////////////
					if( albumArt != null  ){
						Drawable drawable = new BitmapDrawable(getResources(), albumArt);
						Bitmap orgBitmap = ((BitmapDrawable)drawable).getBitmap();					//DrawableからBitmapインスタンスを取得//				http://android-note.open-memo.net/sub/image__resize_drawable.html
						dbMsg +=",orgBitmap="+orgBitmap;
						Bitmap resizedBitmap = Bitmap.createScaledBitmap(orgBitmap, 144, 144, false);										//100x100の大きさにリサイズ
						dbMsg +=",resizedBitmap="+resizedBitmap;
						drawable = new BitmapDrawable(getResources(), resizedBitmap);
						headImgIV.setImageDrawable(drawable);
					}
					dbMsg +=",headImgIV[" + headImgIV.getX() +", " + headImgIV.getY() +"] " + headImgIV.getHeight();		//////////// 0始まりでposition= id ///////////////////////////////////////////////////////////
					mainHTF.setVisibility(View.VISIBLE);		//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
					dbMsg +=",albumMei=" + albumMei;
					mainHTF.setText(albumMei);					//ヘッダーのメインテキスト表示枠		//		getSupportActionBar().setTitle(albumMei);
					artistHTF.setVisibility(View.VISIBLE);			//ヘッダーのアーティスト名表示枠
					artistHTF.setText( artistMei);					//ヘッダーのメインテキスト表示枠		albumArtist
					subHTF.setVisibility(View.VISIBLE);			//ヘッダーのアーティスト名表示枠
					subHTF.setText(subTStr);	//ヘッダーのサブテキスト表示枠
					imgItems=null;				//アルバムアート
					if( pref_list_simple ){					//シンプルなリスト表示（サムネールなど省略）
						dbMsg +=",階層化しないシンプルなタイトルリスト";
						makePlainList( titolList);
					} else {
						dbMsg +=",イメージとサブテキストを持ったタイトルリストを構成";
						setHeadImgList(titolAL );
					}
					break;
				default:
					break;
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	//プレーヤーの表示設定
	public void setListPlayer(String artistMei , String albumMei , String titolMei,String albumArt){
		final String TAG = "setListPlayer";
		String dbMsg = "";
		try{
			dbMsg +=",artistMei= " + artistMei;
			dbMsg +=",albumMei= " + albumMei;
			dbMsg +=",titolMei= " + titolMei;
			lp_artist.setText(creditArtistName);									//プレイヤーのアーティスト表示
			lp_album.setText(albumName);									//プレイヤーのアルバム表示
			lp_title.setText(titolName);										//プレイヤーのタイトル表示
			dbMsg +=",albumArt= " + albumArt;
			if( albumArt != null  ){
				ImageView rc_Img = findViewById(R.id.rc_Img);			//ヘッダーのアイコン表示枠				headImgIV = (ImageView)findViewById(R.id.headImg);		//ヘッダーのアイコン表示枠
				Drawable drawable = new BitmapDrawable(getResources(), albumArt);
				Bitmap orgBitmap = ((BitmapDrawable)drawable).getBitmap();					//DrawableからBitmapインスタンスを取得//				http://android-note.open-memo.net/sub/image__resize_drawable.html
				dbMsg +=",orgBitmap="+orgBitmap;
				rc_Img.setImageBitmap(orgBitmap);
//				int width = rc_Img.getWidth();
////				width = width*9/10;
//				dbMsg +=",width= " + width;
////				Bitmap resizedBitmap = Bitmap.createScaledBitmap(orgBitmap, width, width, false);										//100x100の大きさにリサイズ
////				dbMsg +=",resizedBitmap="+resizedBitmap;
////				drawable = new BitmapDrawable(getResources(), resizedBitmap);
////				rc_Img.setImageDrawable(drawable);
			}
			dbMsg +=",IsPlaying= " + IsPlaying;
			if(!IsPlaying) {			//停止していれば
				MPSIntent.setAction(MusicPlayerService.ACTION_PAUSE);
				MPSName = startService(MPSIntent);	//startService(new Intent(MusicPlayerService.ACTION_PAUSE));
				dbMsg += " ,ComponentName=" + MPSName;/////////////////////////////////////
				lp_ppPButton.setImageResource(R.drawable.play_notif);
				lp_ppPButton.setContentDescription(getResources().getText(R.string.pause));			//play
			} else {
//							MPSIntent.setAction(MusicPlayerService.ACTION_PLAY);
//							MPSName = startService(MPSIntent);	//startService(new Intent(MusicPlayerService.ACTION_PLAY));
//							dbMsg += " ,MPSName=" + MPSName;/////////////////////////////////////
				lp_ppPButton.setImageResource(R.drawable.pouse_notif);
				lp_ppPButton.setContentDescription(getResources().getText(R.string.play));			//pause
			}

			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**遷移先からの戻り*/
	ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
			new ActivityResultContracts.StartActivityForResult(),
			result -> {
//				//new ActivityResultCallback() {
//				//    @Override
//				//    public void onActivityResult(ActivityResult result) {
//				// }
				final String TAG = "resultLauncher";
				String dbMsg = "";
				try {
					dbMsg += ",reqCode="+reqCode;
					int resultCode = result.getResultCode();
					dbMsg += ",resultCode=" + resultCode;
					if (result.getResultCode() == Activity.RESULT_OK) {
						Intent intent = result.getData();
						if (intent != null) {
							backTime= System.currentTimeMillis();						//このActivtyに戻った時間
							Bundle bundle = null;
							boolean kakikomi  = false;
						//		bundle = intent.getExtras();
								int reqCode = intent.getIntExtra("reqCode" , 0);
								dbMsg += ",reqCode="+reqCode;
								Boolean retBool;
								switch(reqCode) {
									case MyConstants.PUPRPOSE_lIST:
										dbMsg += ",汎用プレイリスト読み込み";
										plSL= intent.getStringArrayListExtra("plSL");				//(List<String>)	プレイリスト用簡易リスト
//										plSL = new ArrayList<>();
//										String plSLStr = intent.getStringExtra("plSLJson");
//										try {
//											JSONArray jarray = new JSONArray(plSLStr);
//											for (int i = 0; i < jarray.length(); ++ i) {
//												JSONObject json = jarray.getJSONObject(i);
//												Iterator it = json.keys();
//												while (it.hasNext()) {
//													objMap = new HashMap<String, Object>();
//													String cName = (String) it.next();
//													String cVal = json.getString(cName);
//													objMap.put(cName ,cVal );
//													objMap.put("main" ,cVal );
//													dbMsg +=  "="+cVal;
//													this.plSL.add(cVal);
//												}
//											}
//										}
//										catch (org.json.JSONException e) {
//											myErrorLog(TAG ,  dbMsg + "で" + e);
//										}
										dbMsg += ",plSL=" + plSL.size() + "件";
										plAL= (List<Map<String, Object>>) intent.getSerializableExtra("plAL");				//プレイリスト用ArrayList
//										plAL = new ArrayList<Map<String, Object>>();
//										String plALStr = intent.getStringExtra("plALJson");
//										try {
//											JSONArray j2array = new JSONArray(plALStr);
//											for (int i = 0; i < j2array.length(); ++ i) {
//												JSONObject json = j2array.getJSONObject(i);
//												Iterator it2 = json.keys();
//												while (it2.hasNext()) {
//													objMap = new HashMap<String, Object>();
//													String cName = (String) it2.next();
//													dbMsg +=  "," + cName;
//													String cVal = json.getString(cName);
//													dbMsg +=  "="+cVal;
//													if(cVal != null){
//														objMap.put(cName ,cVal );
//													}
//												}
//												plAL.add(objMap);
//											}
//										}
//										catch (org.json.JSONException e) {
//											myErrorLog(TAG ,  dbMsg + "で" + e);
//										}
										dbMsg += ",plAL=" + plAL.size() + "件";
									//	objMap= intent.getParcelableExtra("objMap");			//汎用マップ
										CreatePLListEnd();
										break;
									case MyConstants.syoki_Yomikomi:		//128；全曲リスト更新
										dbMsg += "全曲リスト更新後";
										String pref_data_url = getPrefStr("pref_data_url" , "",MuList.this);
										String albumArtist = musicPlaylist.uri2Item(pref_data_url,0);
										sigotoFuriwake(MyConstants.v_artist,albumArtist,"","", "");
										break;
									case settei_hyouji:
										dbMsg += "設定表示";
										retBool = Boolean.valueOf(bundle.getString("key.pref_list_simple"));						//プレイヤーの背景
										dbMsg +="シンプルなリスト表示="+ retBool +"(今は"+ pref_list_simple +")";	////////////////
										if( retBool != pref_list_simple){
											pref_list_simple = retBool;
											simpleSyousai( pref_list_simple );
										}
										pref_lockscreen = Boolean.valueOf(bundle.getBoolean("key.pref_lockscreen")) ;				//ロックスクリーンプレイヤー</string>
										pref_notifplayer = Boolean.valueOf( bundle.getBoolean("key.pref_notifplayer")) ;					//ノティフィケーションプレイヤー</string>
										retBool = false;
										retBool = Boolean.valueOf(bundle.getString("key.pref_listup_reset"));						//調整リスト消去
										if( retBool ){
											dbMsg +=",調整リスト消去="+ retBool ;	////////////////
											String fn = getApplicationContext().getString(R.string.shyuusei_file);			//	shyuusei.db</string>
											File shyuFile = new File(fn);
											dbMsg += ",exists=" + shyuFile.exists();
											this.deleteDatabase(getApplicationContext().getString(R.string.shyuusei_file));		//デバッグ用		shyuusei_Helper.getDatabaseName()	getApplicationContext()
											dbMsg += ">作り直し>" + getApplicationContext().getDatabasePath(fn).getPath();	///data/data/com.hijiyam_koubou.marasongs/databases/artist.db
											preRead( MyConstants.syoki_Yomikomi , null);
										}
										break;
									case quite_me:
										dbMsg += "設定表示";
										MuList.this.finish();
										break;
									default:
									case chyangeSong:
									case MyConstants.v_play_list:
									case MyConstants.v_artist:
									case MyConstants.v_alubum:
									case MyConstants.v_titol:
										//			if(reqCode !=  && reqCode != MyConstants. && reqCode != MyConstants.) {
										dbMsg += ",プレイヤーから戻って曲変更";
					//					reqCode =bundle.getInt("reqCode");
										dbMsg +="[プレイリストID= " + nowList_id+ "]" + sousalistName;

								//		psSarviceUri = bundle.getString("psSarviceUri");
										nowList_id = bundle.getInt("nowList_id");
		//				nowList_id = Integer.valueOf(bundle.getString("nowList_id"));
										nowList = bundle.getString("nowList");
										dbMsg +="]" + nowList;//			// 全曲リスト
										mIndex = bundle.getInt("mIndex");
										dbMsg += "[mIndex=" + mIndex;/////////////////////////////////////
										pref_data_url = bundle.getString("pref_data_url");
										dbMsg += "]再生しているファイル="+pref_data_url;
										int mcPosition = bundle.getInt("mcPosition");
										dbMsg += " の "+mcPosition + "ms";
										String[] names = pref_data_url.split(File.pathSeparator);

										albumArtist = names[names.length-2];
										dbMsg += ",albumArtist = "+albumArtist;
										albumName = names[names.length-1];
										dbMsg += ",albumName = "+albumName;
										titolName = names[names.length];
										dbMsg += ",titolName = "+titolName;

//						saisei_fname = bundle.getString("dataFN");
//						dbMsg +=",dataFN=" + saisei_fname;/////////////////////////////////////
//										albumArtist = bundle.getString("albumArtist");
//										dbMsg += ",アルバムアーティスト="+albumArtist;           //b_artist +  ">"+
//										creditArtistName = bundle.getString("creditArtistName");
//										sousa_artist = creditArtistName;
//										dbMsg += ",クレジットされているアーティスト名=" + creditArtistName;////////////////////////////////
//										albumName = bundle.getString("albumName");
//										sousa_alubm = albumName;
//										dbMsg += ",アルバム名=" +b_album + ">" + albumName;/////////////////////////////////////
//										titolName = bundle.getString("titolName");
//										sousa_titol = titolName;
//										dbMsg +=",曲名=" + titolName;/////////////////////////////////////
//										albumArt = bundle.getString("albumArt");
//										dbMsg +=",albumArt;アルバムアートのURI=" + albumArt;/////////////////////////////////////
//										IsPlaying = bundle.getBoolean("IsPlaying");			//再生中か
//										dbMsg +=",IsPlaying=" + IsPlaying;/////////////////////////////////////
//										dbMsg +=  ",MPSIntent=" + MPSIntent;
//										if( MPSIntent == null){
//											MPSIntent = new Intent(getApplication(),MusicPlayerService.class);	//parsonalPBook.thisではメモリーリークが起こる
//											dbMsg +=  ">>" + MPSIntent;
//										}
										setChronometer(mcPosition , IsPlaying ) ;
										IsSeisei = bundle.getBoolean("IsSeisei");			//再生中か
										dbMsg += ",生成中= " + IsSeisei;//////////////////////////////////
//										pref_list_simple = bundle.getBoolean("pref_list_simple");			//再生中か
										dbMsg += ",/シンプルなリスト表示= " + pref_list_simple;
//										setListPlayer( creditArtistName , albumMei , titolName, albumArt);
//										b_artist = b_artist + "";
//										b_album = b_album + "";
//								//		makePlayListSPN(sousalistName);		//プレイリストスピナーを作成する
										dbMsg += "＞makePlayListSPN後＞";
										if(nowList.equals(getResources().getString(R.string.listmei_zemkyoku))){
											dbMsg += ",全曲リスト";
											switch(reqCode) {
												case MyConstants.v_play_list:                                                    //169...334
													dbMsg += ",plNameSL = " + plNameSL.size() + "件";
													//				myLog(TAG,dbMsg);
													sigotoFuriwake(reqCode , nowList , null , null , null);        //表示するリストの振り分け		artistAL ,
													break;
												case MyConstants.v_artist:                                                    //169...334
//													dbMsg += ",artistAL = " + artistAL.size() + "件";
													//				myLog(TAG,dbMsg);
													sigotoFuriwake(reqCode , albumArtist , null , null , null);        //表示するリストの振り分け		artistAL ,
													break;
												case MyConstants.v_alubum:                                                //196...340
//													dbMsg += ",albumAL = " + albumAL.size() + "件";
//
//													if ( b_artist.equals(albumArtist) && b_album.equals(albumName) ) {
//													} else {    //それまで参照していたアーティスト名かそれまで参照していたアルバム名が変わっていたら
//									albumAL = CreateAlbumList( albumArtist ,  "");		//アルバムリスト作成
//									titolAL = CreateTitleList( albumArtist , albumName , titolName);		//曲リスト作成
														sigotoFuriwake(reqCode , albumArtist , albumName , null , null);        //表示するリストの振り分け	albumAL ,
//													}
													break;
												case MyConstants.v_titol:                                                    //197
//													dbMsg += ",曲リスト = " + titolAL.size() + "件";
//								//							if( b_artist.equals(albumArtist) && b_album.equals(albumName) ){
//								//							} else {	//それまで参照していたアーティスト名かそれまで参照していたアルバム名が変わっていたら
//									albumAL = CreateAlbumList( albumArtist ,  "");		//アルバムリスト作成
//									titolAL = CreateTitleList( albumArtist , albumName , titolName);		//曲リスト作成
//									dbMsg +=">>曲リスト = " + titolAL.size() + "件";
//															}
													sigotoFuriwake(reqCode , albumArtist , albumName , null , null);        //表示するリストの振り分けtitolAL ,
													break;
												default:
													break;
											}
											if( IsSeisei ){					//		IsPlaying
//							showMyPlayer();					//プレイヤー表示
											} else{
//							list_player.setVisibility(View.GONE);
											}
										}else if(nowList.equals(getResources().getString(R.string.playlist_namae_saikintuika)) ){
											dbMsg += ",最近追加";
//											sousalistID = nowList_id;
//											sousalistName = nowList;
//											dbMsg += "[" + sousalistID + "]" + sousalistName;
//											pref_zenkyoku_list_id = getAllSongItems();
//											dbMsg += " [全曲リスト； " + pref_zenkyoku_list_id + "]";/////////////////////////////////////
//											mIndex = musicPlaylist.getPlaylistItemOrder(sousalistID,senntakuItem);			//Item.getMPItem( senntakuItem );
//											dbMsg += "[" + mIndex + "]";
											if(treeAdapter != null ){
												dbMsg += ">>選択のみ";
												selectTreePosition(mIndex);
											}else{
												CreatePLList(Long.valueOf(nowList_id) , nowList);									//プレイリストの内容取得
											}

										}else if(nowList.equals(getResources().getString(R.string.playlist_namae_saikinsisei))){
											dbMsg += ",最近再生";
										}

										break;
								}
						}else{
							dbMsg += ",intent == null";
						}
					}
					myLog(TAG, dbMsg);
				} catch (Exception e) {
					myErrorLog(TAG ,  dbMsg + "で" + e);
				}
			});

	public boolean isFocusNow = false;
	//グローバル変数にセットされたタイトルとメッセージでダイアログを表示する
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {	 //<onCreate ヘッドのイメージは実際にローディンされた時点で設定表示と同時にウィジェットの高さや幅を取得したいときは大抵ここで取る。
		final String TAG = "onWindowFocusChanged";
		String dbMsg = "";
		try{
			dbMsg +=  "hasFocus=;" + hasFocus;/////////////////////////////////////
			isFocusNow = hasFocus;
			if(hasFocus){
				dbMsg +=",shigot_bangou;" + shigot_bangou;/////////////////////////////////////
				dbMsg +=",reqCode;" + reqCode;
				switch(shigot_bangou) {
					case reTryMse:				//208　全曲リスト作成から戻ってメッセージ表示
						dbMsg +=",reTryMse;";
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
						alertDialogBuilder.setTitle(dtitol);
						alertDialogBuilder.setMessage(dMessege);
						alertDialogBuilder.setPositiveButton(getResources().getString(R.string.comon_kakuninn),			//確認</string>
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										final String TAG = "onClick";
										String dbMsg = "[MuList,onWindowFocusChanged]";
										try {
											myLog(TAG, dbMsg);
										} catch (Exception e) {
											myErrorLog(TAG ,  dbMsg + "で" + e);
										}
									}
								});
						alertDialogBuilder.setCancelable(true);				// アラートダイアログのキャンセルが可能かどうかを設定します
						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();				// アラートダイアログを表示します
						break;
					default:
//						dbMsg +=",oFRへ" ;
//						oFR();												 // onWindowFocusChanged , onResumeじの共通操作
						break;
				}
				shigot_bangou = 0;
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		super.onWindowFocusChanged(hasFocus);
	}

	private Object[] activities = {
			"MusicPlayer (Normal)", MaraSonActivity.class,
			"MusicPlayer (RemoteControl)", MusicPlayerRemoteControlActivity.class,
	};

//Permission確認、レシーバー破棄、起動中のサービス確認、スレッド起動確認、リスト画面の構成物読み込み、getPList()	へ
	@Override
	public void onCreate(Bundle savedInstanceState) {									//①起動
		super.onCreate(savedInstanceState);
		final String TAG = "onCreate";
		String dbMsg = "";
		try{
			long start = System.currentTimeMillis();	// 開始時刻の取得
			dbMsg +=  ",start="+ start ;/////////////////////////////////////
			shigot_bangou = 0;
			IsPlaying = false;
		//	CONST = new MyConstants();
			ORGUT = new OrgUtil();		//自作関数集
			UTIL = new Util();
			myApp = (MyApp) MuList.this.getApplication();

			musicPlaylist = new MusicPlaylist(MuList.this);
			//Permission確認
			checkMyPermission();      //初回起動はパーミッション後にプリファレンス読込み
			setPrefbool( "rp_pp" ,  false , MuList.this);   							//2点間リピート中
			setPrefInt( "repeatType" ,  0 , MuList.this);   							//リピート再生の種類
			setPrefStr( "pp_start" ,  "0" , MuList.this);   							//リピート区間開始点
			setPrefStr( "pp_end" ,  "0" , MuList.this);   							//リピート区間開始点
			//レシーバー破棄、
			receiverHaki();		//レシーバーを破棄

			dbMsg += "=" + ORGUT.nowTime(true,true,true) ;/////////////////////////////////////
			//起動中のサービス確認
			Bundle extras = getIntent().getExtras();
			dbMsg +=  ",extras="+ extras ;/////////////////////////////////////
			if( extras== null ){
				ArrayList<String> serviceList = ORGUT.getMyService( this , getApplicationContext().getPackageName());			//起動しているサービスを取得
				dbMsg +=  "起動時serviceList="+ serviceList ;/////////////////////////////////////
				if( 0 < serviceList.size() ){
					dbMsg += ",MPSIntent="+ MPSIntent;//////////////////
					for(String serviceName : serviceList){
						if(serviceName.contains("MusicPlayerService")){
							dbMsg +=  ",MPSIntent=" + MPSIntent;/////////////////////////////////////
							if( MPSIntent == null){
								MPSIntent = new Intent(MuList.this, MusicPlayerService.class);
								dbMsg +=  ">>" + MPSIntent;/////////////////////////////////////
							}
							MPSIntent.setAction(MusicPlayerService.ACTION_SYUURYOU);					//service内のquitMe
							startService(MPSIntent);
							stopService(MPSIntent);
						}else if(serviceName.contains("NotifRecever")){
							MPSIntent = new Intent(MuList.this, NotifRecever.class);
							dbMsg += ",NotifRecever="+ MPSIntent;//////////////////
							stopService(MPSIntent);	//		MPSName = startService(MPSIntent);
							dbMsg +=  ">>" + MPSIntent;/////////////////////////////////////
						}else if(serviceName.contains("BuletoohtReceiver")){
							MPSIntent = new Intent(MuList.this, BuletoohtReceiver.class);
							dbMsg = ",BuletoohtReceiver="+ MPSIntent;//////////////////
							stopService(MPSIntent);	//		MPSName = startService(MPSIntent);
							dbMsg +=  ">>" + MPSIntent;/////////////////////////////////////
						}
					}
					serviceList = ORGUT.getMyService( this , getApplicationContext().getPackageName());			//起動しているサービスを取得
					dbMsg +=  "破棄後serviceList="+ serviceList ;/////////////////////////////////////
				}
				imanoJyoutai = veiwPlayer ;												//プレイヤーを表示;起動直後
//				shigot_bangou = jyoukyou_bunki ;			//ファイルに変更が有れば全曲リスト更新の警告/無ければURiリストの読み込みに
			}else{
				imanoJyoutai = chyangeSong ;												//プレイヤーから戻って曲変更
				shigot_bangou = make_list_head ;			//500；ヘッド作成
			}
			dbMsg += ",imanoJyoutai="+ imanoJyoutai ;/////////////////////////////////////
			yobidashiMoto = imanoJyoutai;													//起動直後=veiwPlayer;プレイヤーからの呼出し = chyangeSong
			//スレッド起動確認///////////////////////////////////
//			ActivityManager am = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
//			dbMsg +=  ",ActivityManager="+ am ;/////////////////////////////////////
//			List<android.app.ActivityManager.RunningServiceInfo> listServiceInfo = am.getRunningServices(Integer.MAX_VALUE);
//			for (android.app.ActivityManager.RunningServiceInfo curr : listServiceInfo) {	// クラス名を比較
//				dbMsg += curr.getClass().getName();/////////////////////////////////////
//				if (curr.service.getClassName().equals(MusicPlayerService.class.getName())) {
//					dbMsg += ">>実行中のサービス="+ MusicPlayerService.class.getName() +"と一致";/////////////////////////////////////
//					if( extras== null ){
////						MuList.this.finish();
//						Intent intent = new Intent(MuList.this, MaraSonActivity.class);
//						intent.putExtra("kidou_jyoukyou",MaraSonActivity.kidou_notif);
//						resultLauncher.launch(intent);
////						startActivity(intent);		//プレイやのみ起動
//					}
//					break;
//				}
//			}
			///////////////////////////////////スレッド起動確認//
			CharSequence[] list = new CharSequence[activities.length / 2];
			for (int i = 0; i < list.length; i++) {
				list[i] = (String) activities[i * 2];
			}
			//リスト画面の構成物読み込み
			locale = Locale.getDefault();		// アプリで使用されているロケール情報を取得
			setContentView(R.layout.mu_list);				//			setContentView(R.layout.main);
			toolbar = findViewById(R.id.list_tool_bar);						//このアクティビティのtoolBar
			toolbar.setTitle("");				//何かを設定しなければアプリ名が表示される		☆.setDisplayShowTitleEnabled(false);　はtoolbarに無い？
			toolbar.setContentInsetsAbsolute(0,0);								//左と上の余白を無くす
			View tbContainer = LayoutInflater.from(this).inflate(R.layout.list_toolbar,  toolbar, false);
			ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			toolbar.addView(tbContainer, lp);
			pl_sp = tbContainer.findViewById(R.id.list_pl_sp);		//プレイリスト選択	pl_sp = (Spinner) findViewById(R.id.pl_sp);	から置換え
			headImgIV = tbContainer.findViewById(R.id.headImg);			//ヘッダーのアイコン表示枠				headImgIV = (ImageView)findViewById(R.id.headImg);		//ヘッダーのアイコン表示枠
			dbMsg +=",headImgIV="+headImgIV;
			headImgIV.setVisibility(View.VISIBLE);
			mainHTF = tbContainer.findViewById(R.id.mainHTF);			//				mainHTF = (TextView)findViewById(R.id.mainHTF);			//ヘッダーのメインテキスト表示枠
			subHTF = tbContainer.findViewById(R.id.subHTF);				//ヘッダーのサブテキスト表示枠			subHTF = (TextView)findViewById(R.id.subHTF);				//ヘッダーのサブテキスト表示枠
			artistHTF = tbContainer.findViewById(R.id.artistHTF);			//ヘッダーのアーティスト名表示枠			artistHTF = (TextView)findViewById(R.id.artistHTF);			//ヘッダーのアーティスト名表示枠
			headLayout = tbContainer.findViewById(R.id.headLayout);			//ツールバーのカスタムレイアウト
			setSupportActionBar(toolbar);
			lvID = findViewById(R.id.pllist);	//　作成したリストアダプターをリストビューにセットする	 Id	@id/android:list
			lvID.setTextFilterEnabled(true);						//ListViewにフォーカスを移して「a」を入力すると、以下の図のように先頭に「a」の文字がある項目だけが表示されます。
			lvID.setFocusableInTouchMode(true);
			registerForContextMenu(lvID);							//コンテキストメニュー
			list_player = findViewById(R.id.list_player);		//プレイヤーのインクルード

			lp_ppPButton = list_player.findViewById(R.id.ppPButton);			//プレイヤーの再生/停止ボタン
			rc_fbace = list_player.findViewById(R.id.rc_fbace);		//プレイヤーフィールド部の土台
			lp_stop = list_player.findViewById(R.id.stop);								//プレイヤーの終了ボタン
			lp_artist = list_player.findViewById(R.id.artist);									//プレイヤーのアーティスト表示
			lp_album = list_player.findViewById(R.id.album);									//プレイヤーのアルバム表示
			lp_title = list_player.findViewById(R.id.title);										//プレイヤーのタイトル表示
			lp_chronometer = list_player.findViewById(R.id.chronometer);		//プレイヤーの再生ポジション表示

//			lp_chronometer.setFormat("mm:ss");
			SimpleDateFormat dataFormat = new SimpleDateFormat("mm:ss", Locale.JAPAN);
			lp_chronometer.setText(dataFormat.format(0));

			lp_ppPButton.setOnClickListener(this);
			lp_stop.setOnClickListener(this);
			rc_fbace.setOnClickListener(this);

//			list_player.setVisibility(View.GONE);  //再生したまま戻って来るまで非表示
			mainHTF.setOnKeyListener( this);
			//ヘッダー部分のロングタップ/////////////////////////////////////////////////////////////////////////////////////////////////////////
			mainHTF.setOnLongClickListener(new View.OnLongClickListener() {	// ボタンが長押しクリックされた時のハンドラ
				//	@Override
				public boolean onLongClick(View v) {	// 長押しクリックされた時の処理を記述
					final String TAG = "mainHTF.onLongClick";
					String dbMsg = "";
					try{
						dbMsg += "reqCode;" + reqCode;/////////////////////////////////////
						myLog(TAG, dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
					return false;
				}

				//////////////////////////////デレクトリ名表示フィールドのロングタッチ//
			});
			mainHTF.setOnClickListener(new View.OnClickListener() {	// ヘッダー部分がクリックされた時のハンドラ
				public void onClick(View v) {	// クリックされた時の処理を記述
					headClickAction();//ヘッドが クリックされた時の処理
				}
			});

			lvID.setOnKeyListener( this);
			artistAL = new ArrayList<Map<String, Object>>();
			albumAL = new ArrayList<Map<String, Object>>();
			albumList = new ArrayList<String>();		//アルバム名
			titolAL = new ArrayList<Map<String, Object>>();

			dbMsg +=",imanoJyoutai=" + imanoJyoutai ;
			String comp1 = getString(R.string.artist_tuika01);			//コンピレーション</string>
			String comp2 = getString(R.string.artist_tuika02);				//サウンドトラック</string>
			String comp3  = getString(R.string.artist_tuika03);				//">クラシック</string>
			String comp4  = getString(R.string.comon_nuKnow_artist);				//"">アーティスト情報なし</string>
			compList = new String[]{ comp1 , comp2 , comp3 , comp4};
			comCount = compList.length;
			compSelection = "ALBUM_ARTIST <> ? AND ALBUM_ARTIST <> ? AND ALBUM_ARTIST <> ? AND ALBUM_ARTIST <> ?";			//+ comp ;		//MediaStore.Audio.Media.ARTIST +" <> " + comp;			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
			dbMsg += " , compList = " + compList+"の" + comCount + "件";		//03-28java.lang.IllegalArgumentException:  contains a path separator
			dbMsg +=  ">>ダイヤルキー=" + prTT_dpad;
			if(prTT_dpad){
				setKeyAri();									//d-pad対応
			}
			plTask  = new MuList.ploglessTask(this);
			getPList();
			long end=System.currentTimeMillis();		// 終了時刻の取得
			dbMsg += ":"+ ORGUT.sdf_mss.format(end - start) +"で起動終了"+ ",reqCode="+ reqCode + ",shigot_bangou="+ shigot_bangou ;
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	//現在未使用
	@Override
	protected void onRestart() {
		super.onRestart();
		final String TAG = "onRestart";
		String dbMsg = "";
		try{
			dbMsg += ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	//現在未使用
	@Override
	protected void onStart() {
		super.onStart();
		final String TAG = "onStart";
		String dbMsg = "";
		try{
			dbMsg += ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	// MusicPlaylistクラスとレシーバーを生成
	@Override
	protected void onResume() {
		super.onResume();
		final String TAG = "onResume";
		String dbMsg = "";
		try{
			dbMsg += ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			if(musicPlaylist == null){
				musicPlaylist = new MusicPlaylist(MuList.this);
			}
			dbMsg +="shigot_bangou="+shigot_bangou;
//			receiverSeisei();		//
//			dbMsg +=":レシーバーを生成";
////			dbMsg +="[" + nowList_id + "]" + nowList;
////			if(nowList.equals(getString(R.string.listmei_zemkyoku))){
////				artistList_yomikomi();
////			}else{
////				CreatePLList(Long.valueOf(nowList_id) , nowList);
////			}
			myLog(TAG, dbMsg);
			oFR();
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	//現在未使用
	@Override
	protected void onPause() {
		super.onPause();
		final String TAG = "onPause";
		String dbMsg = "";
		try{
			dbMsg += ORGUT.nowTime(true,true,true);/////////////////////////////////////
			dbMsg +="shigot_bangou="+shigot_bangou;/////////////////////////////////////
//			if( shigot_bangou == quite_me ){					//リスト選択してプレイヤーで終了ボタンをタップするとここに来る
//				MuList.this.finish();
//			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	//現在未使用
	@Override
	protected void onStop() {
		super.onStop();
		final String TAG = "onStop";
		String dbMsg = "";
		try{
			dbMsg += ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	//現在未使用
	@Override
	protected void onDestroy() {
		super.onDestroy();
		final String TAG = "onDestroy";
		String dbMsg = "";
		try{
			dbMsg += ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

}

/**2016/03/24	リストが重い
 * 03-24 13:55:04.125: W/ResourceType(11340): No package identifier when getting name for resource number 0x00000064
 * 	int com.hijiyam_koubou.marasongs.MuList.lv_id = 100 [0x64]　<<				lvID.setId(lv_id); 				//リストビューID;100としていた
 * 	マスクして警告は止まった
 * 03-24 14:51:53.421: I/Choreographer(20421): Skipped 73 frames!  The application may be doing too much work on its main thread.
 * 		UIのスレッド上の処理時間が長いと、途中で処理が中断されてしまう？
 * 	MediaStore.Audio.Playlists.Membersへのトランザクション書き込みは？	
 * */
