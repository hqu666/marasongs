package com.hijiyam_koubou.marasongs;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;

import android.R.drawable;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.bluetooth.*;
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
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.MediaStore;
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.app.NotificationManagerCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Visualizer;
import android.media.session.MediaSession;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.media.session.MediaController.TransportControls;


@SuppressWarnings("deprecation")
@SuppressLint("InlinedApi")
public class MusicPlayerService  extends Service implements  MusicFocusable,PrepareMusicRetrieverTask.MusicRetrieverPreparedListener  , OnCompletionListener, OnPreparedListener{
//	, OnErrorListener,
public static final String ACTION_BLUETOOTH_INFO= "com.hijiyam_koubou.action.BLUETOOTH_INFO";
//public static final String ACTION_BLUETOOTH_INFO= "com.hijiyam_koubou.intent.action.BLUETOOTH_INFO";
public static final String ACTION_STATE_CHANGED = "com.example.android.remotecontrol.ACTION_STATE_CHANGED";
public static final String ACTION_PLAYPAUSE = "com.example.android.remotecontrol.ACTION_PLAYPAUSE";
public static final String ACTION_PLAY = "com.example.android.remotecontrol.ACTION_PLAY";
public static final String ACTION_PAUSE = "com.example.android.remotecontrol.ACTION_PAUSE";
public static final String ACTION_SKIP = "com.example.android.remotecontrol.ACTION_SKIP";
public static final String ACTION_REWIND = "com.example.android.remotecontrol.ACTION_REWIND";
public static final String ACTION_STOP = "com.example.android.remotecontrol.ACTION_STOP";
public static final String ACTION_REQUEST_STATE = "com.example.android.remotecontrol.ACTION_REQUEST_STATE";
public static final String ACTION_LISTSEL = "LISTSEL";					//追加3	；リストで選択された曲の処理
public static final String ACTION_SYUURYOU = "SYUURYOU";					//追加１	；
public static final String ACTION_SYUURYOU_NOTIF = "SYUURYOU_NOTIF";					//追加3	；
public static final String ACTION_ACT_CLOSE = "ACT_CLOSE";					//追加4	；
public static final String ACTION_KEIZOKU = "KEIZOKU";					//追加2	；
public static final String ACTION_REQUEST = "REQUEST";					//次はリクエスト開始
public static final String ACTION_PLAY_READ = "PLAY_READ";
public static final String ACTION_EQUALIZER = "EQUALIZER";
public static final String ACTION_BASS_BOOST = "BASS_BOOST";
public static final String ACTION_REVERB = "REVERB";
public static final String ACTION_DATA_OKURI = "DATA_OKURI";			//データ送りのみ
public static final String ACTION_UKETORI = "UKETORI";					//データ受け取りのみ
//public static final String ACTION_ITEMSET = "ITEMSET";						//追加２	；dataReflesh()でアルバム一枚分のタイトル読み込み

public static final float DUCK_VOLUME = 0.1f;	// The volume we set the media player to when we lose audio focus, but are allowed to reduce the volume instead of stopping playback.
private AudioFocusHelper mAudioFocusHelper = null;		// our AudioFocusHelper object, if it's available (it's available on SDK level >= 8) If not available, this will be null. Always check for null before using!

enum State {	// indicates the state our service:
	Retrieving,	// the MediaRetriever is retrieving music
	Stopped,	// media player is stopped and not prepared to play
	Preparing,	// media player is preparing...
	Playing, 	// playback active (media player ready!). (but the media player may actually be paused in this state if we don't have audio focus. But we stay in this state so that we know we have to resume playback once we get focus back) playback paused (media player ready!)
	Paused		// playback paused (media player ready!)
};
private State mState = State.Retrieving;
boolean mStartPlayingAfterRetrieve = false;// if in Retrieving mode, this flag indicates whether we should start playing immediately when we are ready or not.
enum AudioFocus {	// do we have audio focus?
	NoFocusNoDuck,	// we don't have audio focus, and can't duck
	NoFocusCanDuck,	// we don't have focus, but can play at a low volume ("ducking")
	Focused			// we have full audio focus
}
private AudioFocus mAudioFocus = AudioFocus.NoFocusNoDuck;						// do we have audio focus?
	//			private List<String> artistList ;		//アルバムアーティスト
				//private List<String> albumList ;		//アルバム名
private List<Item> mItems;
private int mIndex;						//play_order
private int tugiNoKyoku ;
private AudioManager mAudioManager;
public TimerTask timertask;
private long mRelaxTime = System.currentTimeMillis();

OrgUtil ORGUT;				//自作関数集
//RemoteController RC;		//Androidバージョンごとのリモートコントロール
//プリファレンス
public SharedPreferences main_pref;
public Editor mainEditor ;
public Editor myEditor ;
public String nowList;				//再生中のプレイリスト名
public int nowList_id = 0;			//再生中のプレイリストID
public String nowList_data = null;		//再生中のプレイリストの保存場所
public String dataFN;						//再生中のファイル名
public String siseizumiDataFN;				//前回再生済みのファイル名
public String ruikei_artist;				//アーティスト累計
public String pref_compBunki = "40";		//コンピレーション分岐点 曲数
public boolean pref_cyakusinn_fukki=true;		//終話後に自動再生
public boolean pref_bt_renkei =true;				//Bluetoothの接続に連携して一時停止/再開
public boolean pref_list_simple =true;				//シンプルなリスト表示（サムネールなど省略）
public boolean pref_lockscreen =true;				//ロックスクリーンプレイヤー</string>
public boolean pref_notifplayer =true;				//ノティフィケーションプレイヤー</string>
public String myPFN = "ma_pref";
public String pref_artist_bunnri = "1000";		//アーティストリストを分離する曲数
public String pref_saikin_tuika = "5";			//最近追加リストのデフォルト枚数
public String pref_saikin_sisei = "100";		//最近再生加リストのデフォルト枚数
public String play_order;
public String artistID;						//アーティストごとの情報
public String albumID;							//アルバムごとの情報
public String audioID;							//曲ごとの情報
public String dataURL = null;
public String b_tagudata = null;
public int repeatType;			//リピート再生の種類
public String repeatArtist;		//リピートさせるアーティスト名
public boolean rp_pp;			//2点間リピート中
public int pp_start = 0;			//リピート区間開始点
public int pp_end;				//リピート区間終了点
public int tugiList_id;		//次に再生するリストID;
public String tugiList;		//次に再生するリスト名;リクエストリスト
public String tone_name;						//トーン名称
public String reverbMei;						//リバーブ効果名称
public short reverbBangou;						//リバーブ効果番号

public MediaPlayer mPlayer;
public MediaPlayer mPlayer2;
public TelephonyManager mTelephonyManager;
int musicVol ;							//音楽再生音量
int imanoJyoutai;

MaraSonActivity MUP;								//音楽プレイヤー
static Context rContext;
public Item playingItem;
public String album_artist =null;		//リストアップしたアルバムアーティスト名
public String creditArtistName ;	//クレジットアーティスト名
public String albumName =null;		//アルバム名
public String titolName =null;		//曲名
public String album_art ;
public int crossFeadTime;		//再生終了時、何ms前に次の曲に切り替えるか
public int mcPosition;			//現在の再生ポジション☆生成時は最初から
public int saiseiJikan;				//DURATION;継続;The duration of the audio file, in ms;Type: INTEGER (long)
public long ruikeiSTTime;			//累積時間
public long ruikeikasannTime;			//累積加算時間
public int ruikeikyoku;			//累積曲数
public boolean IsPlaying ;			//再生中か
public boolean IsSeisei ;			//生成中
public boolean syuuryou = true;	//一曲分の再生が終了
//public String dataURI;
public boolean kaisiZumi = false;
public String maenoArtist =null;	//前に再生していたアルバムアーティスト名
public boolean isOkuri = true;		//送り方向
public int frCount=0;										//送り戻し待ち曲数
public boolean sentakuCyuu = false;					//送り戻しリスト選択中；sendPlayerStateで解除
public String nowAction =null;							//現在のアクション
public Uri uriNext ;						//次のUri

public int mcZencyou = 0;			//再生ファイルの長さ
public int b_mIndex = 0;			//前のインデックス
public String b_dataFN =null;			//すでに再生している再生ファイル
public String b_Album ="";			//前のアルバム
public Intent muquIntent;			//ミュージックキューDB
//private static final String CONTENT = "content://com.hijiyama_koubou.medialinkplayer.MQLIST";		 //アプリAのパッケージ名　＋　データベース名	// private static final String CONTENT ="content://com.sen.muqu/";
//managedQuery() の指定値
public Uri c_uri=null;		//setMPFinfo(16172): c_uri=content://com.sen.MuQu/171
public String[] c_projection=null; 		//取得するカラム名を配列で指定します。null を指定すると、全カラムを取得することになります。Android が提供する全ての Provider は、カラム名を定数として持っています。
public String c_selection =null;			//取得する列を絞り込むときに使います。具体的には "AGE > 30" のように、SQL文の WHERE 句を指定します。null を指定すると、全行を取得することになります。
public String[] c_selectionArgs =null;		//selection でバインドを使用したとき、バインドの値をここで指定します。例えば selection で "AGE > ?" としたとき、ここで [ 30 ] と指定することができます。
public String c_sortOrder =null;			//ソート順を指定します。"NAME ASC" のように、SQL文の ORDER BY 句を指定します。null を指定すると、順番は不特定になります(通常のデータベースと同じです)。
public static final String ACTION = "Player Service";
public String action;									//ボタンなどで指定されたアクション

public Handler btHandler = null;		//new Handler();
//public IntentFilter btFilter = new IntentFilter();						//BluetoothA2dpのACTIONを保持する
public BluetoothAdapter mBluetoothAdapter = null;
public BuletoohtReceiver btReceiver;							//public BuletoohtReceiver btReceiver;		BroadcastReceiver
public String dviceStyteStr;									//デバイスの接続情報
public String dviceNamer;										//デバイス名
public String dviceStytus;										//デバイスの状態
public String stateBaseStr = null;
public String  b_stateStr;

static final int REQUEST_ENABLE_BT = 0;
public boolean selfStop = false;
private Thread mSelfStopThread = new Thread() {						//停止後 30 分再生がなかったらサービスを止めるstopSelf	createBodyでスタート
	public void run() {
		final String TAG = "mSelfStopThread[MusicPlayerService]";
		String dbMsg="開始";			//ORGUT.nowTime(true,true,true);/////////////////////////////////////
//	try {
			while (selfStop) {
				// 停止後 30 分再生がなかったらサービスを止める
				boolean needSleep = false;
				dbMsg="mState=" + mState;/////
				myLog(TAG,dbMsg);
				if (mState == MusicPlayerService.State.Preparing || mState == MusicPlayerService.State.Playing || mState == MusicPlayerService.State.Paused) {
					needSleep = true;
				} else if (mRelaxTime + 1 * 1000 * 60 > System.currentTimeMillis()) {			//relaxResources(mPlayerの破棄) でカウントスタート
					needSleep = true;
				}
				dbMsg=dbMsg+"needSleep=" + needSleep;/////
				if (!needSleep) {
					break;
				}
				try {
					Thread.sleep(1 * 1000 * 60); // 停止中でない、または 10 分経過してない場合は 1 分休む
				} catch (InterruptedException e) {
					myErrorLog(TAG,dbMsg +"で"+e.toString());
				}
			}				//while (true) {
			myLog(TAG,dbMsg);
			MusicPlayerService.this.stopSelf();
//		} catch (Exception e) {
//			myErrorLog(TAG,dbMsg +"で"+e.toString());
//		}
	}
};

/**
 *  操作対応②ⅱ各画面の操作ボタンから呼ばれ動作分岐。 ☆これが無ければonStart
*Called when we receive an Intent. When we receive an intent sent to us via startService(), this is the method that gets called. So here we react
* appropriately depending on the Intent's action, which specifies what is being requested of us.
*/
public int nowSartId = 0;
public boolean actClose= false;
@Override
public int onStartCommand(Intent intent, int flags, int startId) {			//
	final String TAG = "onStartCommand[MusicPlayerService]";
	String dbMsg="開始";/////次はonCreate
	try{
		onCompletNow = false;			//曲間処理中
		action = intent.getAction();					//ボタンなどで指定されたアクション
		nowAction =action;	//現在のアクション
		dbMsg= dbMsg + ",action=" + action;/////////////////////////////////////
		if( action == null ){						//
			action = ACTION_LISTSEL;
			dbMsg= dbMsg + ">>" + action;/////////////////////////////////////
		}
	//	dbMsg= dbMsg + ",intent=" + intent;/////////////////////////////////////
		dbMsg= dbMsg + " , flags=" + flags;/////////////////////////////////////
		dbMsg= dbMsg + " , startId=" + startId;/////////////////////////////////////
		nowSartId = startId;
	//	myLog(TAG,dbMsg);
/*アーティストリピート
 * 01-22 15:14:38.784: I/onStartCommand[MusicPlayerService](11241): 開始,action=null , flags=0 , startId=2
 * 二点間初期
 *  01-22 15:55:03.780: I/onStartCommand[MusicPlayerService](11241): 開始,action=null , flags=0 , startId=15
* */
		if (action.equals(ACTION_PLAYPAUSE)) {
			dataUketori(intent);										//クライアントからデータを受け取りグローバル変数にセット
			processTogglePlaybackRequest();														//②ⅲ?Play/Pauseの反転
		} else if (action.equals(ACTION_PLAY_READ)) {							//"readPlaying[MaraSonActivity]
			dataUketori(intent);			//クライアントからデータを受け取りグローバル変数にセット
			if( mPlayer !=null ){
				dbMsg= dbMsg + " ,isPlaying=" + mPlayer.isPlaying() ;/////////////////////////////////////
				dbMsg= dbMsg + " ," + mcPosition + "から,";/////////////////////////////////////
				mPlayer.seekTo(mcPosition);
				if(!  mPlayer.isPlaying()){
					mPlayer.start();
					mState = State.Playing;
				}
			}
			sendPlayerState(mPlayer);																		//②ⅲStop?//一曲分のデータ抽出して他のActvteyに渡す。
	//		changeCount(mPlayer);						//タイマーオブジェクトを使ったカウンタ更新を追加
		} else if (action.equals(ACTION_PLAY)) {
			dbMsg= dbMsg + " ," + mcPosition + "から,";/////////////////////////////////////
			processPlayRequest();																	//②ⅲPlay?StoppedならplayNextSong/PausedならconfigAndStartMediaPlayer
		} else if (action.equals(ACTION_LISTSEL)) {					//	リストで選曲後に再生中だった場合
			dataUketori(intent);
			playNextSong(false);			// If we're stopped, just go ahead to the next song and start playing
		} else if (action.equals(ACTION_PAUSE)) {
			processPauseRequest();																	//②ⅲPause?
		} else if (action.equals(ACTION_SKIP)) {
			if( mPlayer !=null ){
				mPlayer.pause();
			}
			processSkipRequest();																	//②ⅲFF?次の曲に順送り
		} else if (action.equals(ACTION_REWIND)) {
			if( mPlayer !=null ){
				mPlayer.pause();
			}
			processRewindRequest();																//②ⅲRew?
		} else if (action.equals(ACTION_STOP)) {
			processStopRequest(false);															//②ⅲStop?eタイマーを破棄してmPlayerの破棄へ
//			if (intent.getBooleanExtra("cancel", false)) {
//				mNotificationManager.cancel(NOTIFICATION_ID);
//			}
		} else if (action.equals(ACTION_REQUEST_STATE)) {
			//起動時の表示；dataUketoriでクライアントからデータを受け取りグローバル変数にセット、sendPlayerStateで一曲分のデータ抽出して他のActvteyに渡す。
			dataUketori(intent);			//クライアントからデータを受け取りグローバル変数にセット
			mState = State.Stopped;						//Stopped	プレイヤー生成のトリガーに使用？					Paused
			new PrepareMusicRetrieverTask(this).execute(getApplicationContext());		// Create the retriever and start an asynchronous task that will prepare it.
			sendPlayerState(mPlayer);																		//②ⅲStop?//一曲分のデータ抽出して他のActvteyに渡す。
		} else if (action.equals(ACTION_KEIZOKU)) {				//終了準備
			dataUketori(intent);			//クライアントからデータを受け取りグローバル変数にセット
			sendPlayerState(mPlayer);
		} else if (action.equals(ACTION_SYUURYOU)) {				//終了準備
			quitMe( startId );			//このサービスを閉じる
		} else if (action.equals(ACTION_SYUURYOU_NOTIF)) {				//ノティフィケーションから終了
			dbMsg= dbMsg + " ,actClose=" + actClose ;/////////////////////////////////////
			if(! actClose ){
				imanoJyoutai = MaraSonActivity.quit_all;
				sendPlayerState(mPlayer);
			}
			dbMsg= dbMsg + " ,startId=" + startId ;/////////////////////////////////////
			quitMe( startId );			//このサービスを閉じる
		} else if (action.equals(ACTION_ACT_CLOSE)) {				//アクティビティは閉じられている
			dbMsg= dbMsg + " ,actClose=" + actClose ;/////////////////////////////////////
			actClose = true;
			dbMsg= dbMsg + ">>" + actClose ;/////////////////////////////////////
		} else if (action.equals(ACTION_REQUEST)) {				//次はリクエスト開始
			Bundle extras = intent.getExtras();
			tugiList_id = extras.getInt("tugiList_id");
			dbMsg= dbMsg+"次に再生するリスト["+ tugiList_id ;
			tugiList = extras.getString("tugiList");
			dbMsg= dbMsg+"]"+ tugiList ;		//次に再生するリスト名;リクエストリスト
//			boolean requestSugu = false;
//			requestSugu = extras.getBoolean("requestSugu");
//			if(requestSugu){
//				nowList_id = tugiList_id;
//				nowList = tugiList;
//			}
		} else if (action.equals(ACTION_EQUALIZER)) {
			equalizerPartKousin( intent );					//Equalizerの部分更新
		} else if (action.equals(ACTION_BASS_BOOST)) {
			setupBassBoost(intent);			//ベースブーストOn/Off
		} else if (action.equals(ACTION_REVERB)) {
			dbMsg= dbMsg+",リバーブ効果["+ reverbBangou + "]" + reverbMei;		//次に再生するリスト名;リクエストリスト
			setupPresetReverb(intent);					//リバーブ設定
		} else if (action.equals(ACTION_DATA_OKURI)) {				//データ送りのみ
			sendPlayerState(mPlayer);
		} else if (action.equals(ACTION_UKETORI)) {				//データ受け取りのみ
			dataUketori(intent);
		}

	//	myLog(TAG,dbMsg);
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
//	return START_REDELIVER_INTENT;				//	再起動前と同じ順番で再起動してくれる。Intentも渡ってきています。
	return START_NOT_STICKY;				//APIL5;サービスが強制終了した場合、サービスは再起動しない
/*START_STICKY	サービスが強制終了した場合、サービスは再起動するonStartCommand()が再度呼び出され、Intentにnullが渡される
*/
}
@Override
public void onCreate() {											//①ⅸ
	super.onCreate();
	final String TAG = "onCreate[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		dbMsg="nowAction=" + nowAction;/////////onStartCommandで更新
		mPlayer = null;
		mPlayer2 = null;
		if(nowAction != null || nowAction != ACTION_SYUURYOU){
			createBody();
		}
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

/**
 * 設定を読み込み各サービスを起動、mItemsを読み込む
 * 呼出し元 ①nowActionで動作指定されている場合と、終了指定以外でonCreateから
 *  		②dataReflesh
 * */
public void createBody()  throws NullPointerException{										//①ⅹ		リモートコントロール
	final String TAG = "ｃreateBody[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		dbMsg= "起動済み=" + kaisiZumi;/////////////////////////////////////
		if(! kaisiZumi){  //重複読出し防止
			ORGUT = new OrgUtil();	//自作関数集

			ruikeiSTTime = 0;			//累積時間
			ruikeikyoku = 0;			//累積曲数
			///ここからオリジナル////////////////////////////////////////////////////////////////////////////
	//		dbMsg="PlayerServiceで"+getApplicationContext();/////////////////////////////////////
			dbMsg=dbMsg +"myPid:"+ android.os.Process.myPid() + " , myTid:" + android.os.Process.myTid();/////////////////////////////////////
			dbMsg= dbMsg+ ",Telephonys設定=" + mTelephonyManager;///////////////java.lang.NullPointerException
			if(mTelephonyManager == null ){
				mTelephonyManager= (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
				dbMsg= "Telephonys=" + mTelephonyManager;/////////////////////////////////////
				if(mTelephonyManager != null){
					mTelephonyManager.listen(mPhoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);
				}
			}
			setteriYomikomi();		//<onCreate	プリファレンスに記録されているデータ読み込み
			if (21 <= android.os.Build.VERSION.SDK_INT  ) {
		//		RC = new RemoteController(getApplicationContext());
			}else	{
				dbMsg= dbMsg +",ロックスクリーンプレイヤー=" + pref_lockscreen;/////////////////////////////////////
				if (android.os.Build.VERSION.SDK_INT < 14 ) {						//ロックスクリーンプレイヤー registerRemoteControlClient
					pref_lockscreen = false;
				} else {
		//			pref_lockscreen = Boolean.valueOf( (String) keys.get("pref_lockscreen"));

				}
				dbMsg= dbMsg +">>" + pref_lockscreen;/////////////////////////////////////
				dbMsg= dbMsg +",ノティフィケーションプレイヤー=" + pref_notifplayer;/////////////////////////////////////
				if (android.os.Build.VERSION.SDK_INT <11 ) {
					pref_notifplayer = false;
				}else if ( 14 <= android.os.Build.VERSION.SDK_INT  &&  android.os.Build.VERSION.SDK_INT < 21) {													//
//					pref_notifplayer = Boolean.valueOf( (String) keys.get("pref_notifplayer"));
					if( pref_notifplayer ){
						makeNotification();				//ノティフィケーション作成
					}
				}
			}
			dbMsg +=  "," + dataFN ;////////////////////////////////////////////////////////////////////////////
			if( mItems == null){
			// dbMsg + ",mItems =" + mItems;/////////////////////////////////////
				mItems = new LinkedList<Item>();	//id"、ARTIST、ALBUM_ARTIST、ALBUM、TITLE、DURATION、DATAを読み込む
//				MaraSonActivity MSA = new MaraSonActivity();
				mItems = Item.getItems( getApplicationContext());
				dbMsg=dbMsg + ">>" + mItems.size() + "件";/////////////////////////////////////
			}
			dbMsg +=  " , " + mItems ;////////////////////////////////////////////////////////////////////////////
			if( mItems != null ){
				if( nowList.equals(getResources().getString(R.string.listmei_zemkyoku))){		// ;		// 全曲リスト</string>
					mIndex = Item.getMPItem(  dataFN);			//インデックスの逆検索	 ,mItems , getApplicationContext()
				}
				dbMsg= dbMsg +"[" + mIndex + "/" + mItems.size() + "]";///////////////////////////////////
			}
			mAudioManager = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);
//			int ringVol = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
//			dbMsg=dbMsg + ",着信音量は" + ringVol;/////////////////////////////////////
			musicVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			dbMsg=dbMsg + ",musicVolは" + musicVol;/////////////////////////////////////
			selfStop = true;
	//		mSelfStopThread.start();
			kaisiZumi = true;
			//Bluetooth///////////////////////////////////////////ロックスクリーン//
//			receiverSeisei();		//レシーバーを生成 <onResume , playing , mData2Service	onClick
			pref_toneList =  new ArrayList<String>();				//トーンリストの初期化
			receiverSeisei();		//レシーバーを生成 <onResume , playing , mData2Service	onClick
		}			//if(! kaisiZumi){  //重複読出し防止
		//			myLog(TAG,dbMsg);
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

public void setteriYomikomi(){		//<onCreate	プリファレンスに記録されているデータ読み込み
	//http://d.hatena.ne.jp/Kazzz/20100715/p1
	final String TAG = "setteriYomikomi[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	long start = System.currentTimeMillis();		// 開始時刻の取得
	try{
		String fName =  "/data/data/" +getPackageName()+"/shared_prefs/" + getString(R.string.pref_main_file) +".xml";
		dbMsg="fName = " + fName;/////////////////////////////////////
		File tFile = new File(fName);
		dbMsg= dbMsg +">>有無 = " + tFile.exists();/////////////////////////////////////
		main_pref = getSharedPreferences( getResources().getString(R.string.pref_main_file) ,MODE_PRIVATE);		//	getSharedPreferences(prefFname,MODE_PRIVATE);
		mainEditor = main_pref.edit();
		dbMsg= dbMsg +">>" + tFile.exists();/////////////////////////////////////
		if( ! tFile.exists()){
			dbMsg="shared_prefs無し";/////////////////////////////////////
		}
		Map<String, ?> keys = main_pref.getAll();
		if( keys.size() > 0 ){			//プリファレンスが出来ている
			int i=0;
			i=0;
			for (String key : keys.keySet()) {
				i++;
				dbMsg =  i+"/"+keys.size()+")　"+key;///////////////+","+(keys.get(key) instanceof String)+",instanceof Boolean="+(keys.get(key) instanceof Boolean);////////////////////////////////////////////////////////////////////////////
				if( keys.get(key) != null){			//if( readStr != null || ! readStr.equals("")){
					dbMsg +="は "+String.valueOf(keys.get(key));///////////////+","+(keys.get(key) instanceof String)+",instanceof Boolean="+(keys.get(key) instanceof Boolean);////////////////////////////////////////////////////////////////////////////
					try{
						if(key.equals("pref_gyapless")){					//クロスフェード時間
							dbMsg +=  "クロスフェード時間"+crossFeadTime ;////////////////////////////////////////////////////////////////////////////
							crossFeadTime = Integer.valueOf(keys.get(key).toString());
						}else if(key.equals("pref_list_simple")){
							dbMsg += "は"+(keys.get(key)).toString()+ ">>シンプルなリスト表示（サムネールなど省略）"+pref_list_simple;////////////////////////////////////////////////////////////////////////////
							pref_list_simple = Boolean.valueOf((String.valueOf( keys.get(key)))) ;
							dbMsg +=  ">>"+pref_list_simple;////////////////////////////////////////////////////////////////////////////
						}else if(key.equals("pref_saisei_fname")){
							dbMsg +=  ">>再生中のファイル名" ;////////////////////////////////////////////////////////////////////////////
							dataFN = String.valueOf(keys.get(key));							//再生中のファイル名//DATA;The data stream for the file ;Type: DATA STREAM
							dbMsg =">>再生中のファイル名" + dataFN ;//// pref_saisei_fname //////
							File chFile = new File(dataFN);
							dbMsg += " , " + dataFN +"="+chFile.exists();//////////////////
							if(! chFile.exists() ){
								mainEditor.remove("dataFN");
								dataFN = null;
								String  pdMes = getResources().getString(R.string.setteriYomikomi_data_meg) ;		//前回再生していた音楽ファイルが見つかりません。\n再生する曲を選択して下さい。</string>
								dbMsg=dbMsg  + pdMes;
								Toast.makeText(this, (CharSequence) pdMes, Toast.LENGTH_SHORT).show();
							}
						}else if(key.equals("pref_saisei_jikan")){
							dbMsg += ">>再生中音楽ファイルの再開時間" ;//////////////////
							mcPosition = Integer.valueOf(String.valueOf(keys.get(key)));				//選択中選択ポジション
							dbMsg += "["+ORGUT.sdf_mss.format(mcPosition) + "/";////////////////////////////////////////////////////////////////////////////
						}else if(key.equals("pref_saisei_nagasa")){
							dbMsg += ">>再生中音楽ファイルの長さ";//////////////////
							saiseiJikan = Integer.valueOf(String.valueOf(keys.get(key)));				//再生時間
						}else if(key.equals("pref_cyakusinn_fukki")){			//着信後の復帰
							pref_cyakusinn_fukki = Boolean.valueOf(String.valueOf(keys.get(key)));
								dbMsg +=  "着信後の復帰=" + pref_cyakusinn_fukki;////////////////////////////////////////////////////////////////////////////
						}else if(key.equals("pref_compBunki")){
							dbMsg += ">>コンピレーション分岐点" ;//////////////////
							pref_compBunki = String.valueOf(keys.get(key));			//コンピレーション分岐点
						}else if(key.equals("nowList")){			//");
							dbMsg +=  ">再生中のプレイリスト名=" ;
							nowList = String.valueOf(keys.get(key).toString());
							dbMsg +=   String.valueOf(nowList)  ;
						}else if(key.equals("nowList_id")){			//");
							dbMsg +=  ">再生中のプレイリストID=" ;
							nowList_id = Integer.valueOf(keys.get(key).toString());	//
							dbMsg +=   String.valueOf(nowList_id)  ;
						}else if(key.equals("repeatType")){			//");;			//
							dbMsg +=  ">リピート再生の種類=" ;
							repeatType = Integer.valueOf(String.valueOf(keys.get(key)));	//
							if(repeatType != MaraSonActivity.rp_point){
								pp_start = 0;
							}
							dbMsg +=   String.valueOf(repeatType)  ;
						}else if(key.equals("repeatArtist")){			//");;			//
							dbMsg +=  ">リピートさせるアーティスト名=" ;
							repeatArtist = String.valueOf(keys.get(key).toString());	//
							dbMsg +=   String.valueOf(repeatType)  ;
						}else if(key.equals("pref_nitenkan")){			//");
							rp_pp = Boolean.valueOf(String.valueOf(keys.get(key)));
							dbMsg +=  "二点間再生中=" + rp_pp;	//
						}else if(key.equals("pref_nitenkan_start")){			//");
							pp_start = Integer.valueOf(keys.get(key).toString());	//
							dbMsg +=  "二点間再生開始点=" + pp_start ;////////pref_nitenkan_start//////////
						}else if(key.equals("pref_nitenkan_end")){			//");
							pp_end = Integer.valueOf(keys.get(key).toString());	//
							dbMsg +=  "二点間再生終了点=" + pp_end ;/////pref_nitenkan_end////////////////////////////
						}else if(key.equals("mIndex")){			//");
							dbMsg +=  ",play_order=ID=" ;
							mIndex = Integer.valueOf(keys.get(key).toString());	//
							dbMsg +=   String.valueOf(mIndex)  ;
						}else if(key.equals("nowList_data")){			//");
							dbMsg +=  ",プレイリストの保存場所=" ;
							nowList_data = String.valueOf(keys.get(key).toString());
							dbMsg +=   String.valueOf(nowList_data)  ;
						}else if(key.equals("pref_bt_renkei")){			//");
							dbMsg +=  ">>Bluetoothの接続に連携=" ;
							pref_bt_renkei = Boolean.valueOf(String.valueOf(keys.get(key)));
							dbMsg +=   String.valueOf(pref_bt_renkei)  ;////////////////	 Bluetoothの接続に連携して一時停止/再開////////////////////////////////////////////////////////////

						}else if(key.equals("tone_name")){
							tone_name = String.valueOf(keys.get(key).toString());	//
							dbMsg +=  "トーン名称=" + tone_name ;
			//				myLog(TAG,dbMsg);
						}else if(key.equals("pref_toneList")){				//http://qiita.com/tomoima525/items/f8cf688ad9571d17df41
							String stringList = String.valueOf(keys.get(key));											//bundle.getString("list");  //key名が"list"のものを取り出す
							dbMsg +=  ",stringList= " + stringList;
							try {
								JSONArray array = new JSONArray(stringList);
								dbMsg +=  ",array= " + array;
								int length = array.length();
								dbMsg +=  "= " + length +"件";
								pref_toneList =  new ArrayList<String>();				//トーンリストの初期化
								for(int j = 0; j < length; j++){
									dbMsg +=  "(" + j + "/" + length  + ")" + array.optString(j);
									pref_toneList.add(array.optString(j));
								}
							} catch (JSONException e1) {
								e1.printStackTrace();
							}
							dbMsg +=  ",トーン配列=" + pref_toneList ;
			//				myLog(TAG,dbMsg);
						}else if(key.equals("bBoot")){
							bBoot = Boolean.valueOf(String.valueOf(keys.get(key)));	//
							dbMsg +=  "バスブート=" + bBoot ;
						}else if(key.equals("reverbBangou")){
							reverbBangou = Short.valueOf(keys.get(key).toString());	//
							dbMsg +=  "リバーブ効果番号=" + reverbBangou ;

						}
	//					myLog(TAG,dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG,dbMsg+"；"+e);
					}
				}
			}			//for (String key : keys.keySet())
			//読み込み/////////////////////////////////////////////////////
			long end=System.currentTimeMillis();		// 終了時刻の取得
			dbMsg=(int)((end - start)) + "mS";		//	<string name="">所要時間</string>
	//		myLog(TAG,dbMsg);
		}
		long end=System.currentTimeMillis();		// 終了時刻の取得
		dbMsg=dbMsg +";"+ (int)((end - start)) + "m秒で終了";
//		myLog(TAG,dbMsg);
	}catch (Exception e) {
		myErrorLog(TAG,dbMsg +"で"+e.toString());
	}
}

//		https://sites.google.com/site/androidappzz/home/dev/volumesample				//着信音量を取得、設定する方法

private void processTogglePlaybackRequest() {												//②ⅲ?Play/Pauseの反転		<ACTION_PLAYPAUSE
	final String TAG = "processTogglePlaybackRequest[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		dbMsg="mState= " + mState;/////////////////////////////////////
		if (mState == State.Paused || mState == State.Stopped) {
			processPlayRequest();
		} else {
			processPauseRequest();
		}
	//	myLog(TAG,dbMsg);
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

public void processPlayRequest() {																//②ⅲPlay?StoppedならplayNextSong/PausedならconfigAndStartMediaPlayer
	final String TAG = "processPlayRequest[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		dbMsg="mState= " + mState;/////////////////////////////////////
		dbMsg= dbMsg + " ,mcPosition= " + mcPosition;/////////////////////////////////////
		if (mState == State.Retrieving) {
			mStartPlayingAfterRetrieve = true;			// If we are still retrieving media, just set the flag to start playing when we're ready
//			myLog(TAG,dbMsg);
			return;
		}
		tryToGetAudioFocus();
	//	myLog(TAG,dbMsg);
		if (mState == State.Stopped) {		// actually play the song
			playNextSong(false);			// If we're stopped, just go ahead to the next song and start playing
		} else if (mState == State.Paused ) {			//0531元SoucsはPausedだけ
			mState = State.Playing;			// If we're paused, just continue playback and restore the 'foreground service' state.
			configAndStartMediaPlayer();					//ポーズを解除
		} else {
			playNextSong(false);			// If we're stopped, just go ahead to the next song and start playing
		}
		if ( 21 <= android.os.Build.VERSION.SDK_INT) {
			lpNotificationMake(playingItem.artist , playingItem.album , playingItem.title , album_art);
		}else if ( 14 <= android.os.Build.VERSION.SDK_INT ) {													// &&  android.os.Build.VERSION.SDK_INT < 21
			dbMsg= dbMsg +" , mRemoteControlClient= " + mRemoteControlClient;/////////////////////////////////////
			if (mRemoteControlClient != null) {		// Tell any remote controls that our playback state is 'playing'.
				mRemoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
			}
			if(pref_notifplayer){
				updateNotification(mPlayer);				//Updates the notification
			}
		}
	//	sendPlayerState();					//一曲分のデータ抽出して他のActvteyに渡す。
//		myLog(TAG,dbMsg);
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

public void processPauseRequest() {															//②ⅲPause?
	final String TAG = "processPauseRequest[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		dbMsg="mState = " + mState;/////////////////////////////////////
		if (mState == State.Retrieving) {
			mStartPlayingAfterRetrieve = false;			// If we are still retrieving media, clear the flag that indicates we should start playing when we're ready
			return;
		}
//		if(g_timer != null){
//			g_timer.cancel();		//cancelメソッド実行後は再利用できない
//			g_timer=null;
//		}
		mState = State.Paused;			// Pause media player and cancel the 'foreground service' state.
		dbMsg= dbMsg + ">> " + mState;/////////////////////////////////////
		dbMsg= dbMsg + ",mPlayer " + mPlayer;/////////////////////////////////////
		if( mPlayer != null){
			setPref();								//プリファレンス記載
			dbMsg= dbMsg + ",isPlaying " + mPlayer.isPlaying();/////////////////////////////////////
			mPlayer.pause();
			dbMsg= dbMsg + ">isPlaying>" + mPlayer.isPlaying();/////////////////////////////////////
	//		myLog(TAG,dbMsg);
			sendPlayerState(mPlayer);					//一曲分のデータ抽出して他のActvteyに渡す。
			if(pref_notifplayer){
				if ( 21 <= android.os.Build.VERSION.SDK_INT) {
					lpNotificationMake(playingItem.artist , playingItem.album , playingItem.title , album_art);
				}else if(  14 <= android.os.Build.VERSION.SDK_INT){														// &&  android.os.Build.VERSION.SDK_INT < 21
					if (mRemoteControlClient != null) {		// Tell any remote controls that our playback state is 'paused'.
						mRemoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
					}
				}
			}
		}
	//	myLog(TAG,dbMsg);
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

private void processSkipRequest() {													//②ⅲFF?次の曲に順送り		<onCompletion , run[changeCount
	final String TAG = "processSkipRequest[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		dbMsg= "mPlayer="+ mPlayer;/////////////////////////////////////
		if(mPlayer != null){
			if( mPlayer.isPlaying() ){
				processStopRequest(false);					//タイマーを破棄して/mPlayerの破棄へ
			}
		}
		frCount++;
		dbMsg= dbMsg+ "," + mIndex +"+"+ frCount+";選択中="+ sentakuCyuu;/////////////////////////////////////
		if( mPlayer == null){
		//	Thread.sleep(1000);			//書ききる為の時間
			okuriMpdosi(frCount);
			frCount = 0;
		}
	//	myLog(TAG,dbMsg);
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

private void processRewindRequest() {															//②ⅲRew?
	final String TAG = "processRewindRequest[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		dbMsg= "mPlayer="+ mPlayer;/////////////////////////////////////
		if( mPlayer != null){
			mPlayer.pause();
			mcPosition = mPlayer.getCurrentPosition();
			dbMsg=dbMsg + ",mcPosition=" + mcPosition;/////////////////////////////////////
	//		myLog(TAG,dbMsg);
			if( mcPosition > 3000 ){					//3秒以上なら
				if(rp_pp){						//2点間リピート中で//リピート区間終了点
					mcPosition = pp_start;		//リピート区間開始点
				}else {
					mcPosition = 0;
				}
				mPlayer.seekTo(mcPosition);		//先頭に戻す
				action = MusicPlayerService.ACTION_PLAY;
				mPlayer.start();
			}else{											//1秒未満なら
				if(mPlayer != null){
					if( mPlayer.isPlaying() ){
						processStopRequest(false);					//タイマーを破棄して/mPlayerの破棄へ
					}
				}
			}
		}
		frCount--;
		dbMsg=dbMsg+ ","+  mIndex +"-"+ frCount;/////////////////////////////////////
		if( mPlayer == null){
			okuriMpdosi(frCount);
			frCount = 0;
		}
	//	myLog(TAG,dbMsg);
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

public void okuriMpdosi(int tIDCo) {		//送り戻しの実行;加算数を渡す <run[changeCount.MusicPlayerService
	final String TAG = "okuriMpdosi[MusicPlayerService]";
	String dbMsg= "開始";/////////////////////////////////////
	try{
		dbMsg= "現在" + mIndex + "+ " + tIDCo;/////////////////////////////////////
		stopCount();		//タイマーオブジェクトを破棄
		mIndex = mIndex + tIDCo;
		int startCount = 0;
		int endCount = mItems.size()-1;
		if(endCount< mIndex){				//最後の曲を超えたら
			mIndex = startCount;									//最初の曲に戻す
			dbMsg=dbMsg + ">>" + mIndex;/////////////////////////////////////
		} else if(mIndex < startCount){							//最初の曲まで戻っていたら
			mIndex =endCount;				//死後の曲へ
			dbMsg=dbMsg + ">>" + mIndex;/////////////////////////////////////
		}
		dbMsg= dbMsg + ">> " + mIndex;/////////////////////////////////////
		dbMsg= dbMsg + ",処理後に追加されたfrCount= " + frCount;/////////////////////////////////////
		if( frCount == 0 ||  mPlayer == null){					//カウントが追加されていなければ
			dbMsg= dbMsg + "[" +( mIndex ) +"/" + mItems.size() + "曲目へ]";///////////
			dbMsg=dbMsg + ",playingItem=" + playingItem;/////////////////////////////////////
			playingItem = mItems.get(mIndex);	//0始まりでリスト上のインデックス指定
			dbMsg=dbMsg + ">>" + playingItem;/////////////////////////////////////
			if(rp_pp){						//2点間リピート中で//リピート区間終了点
				mcPosition = pp_start;		//リピート区間開始点
			}else {
				mcPosition = 0;
			}
			action = MusicPlayerService.ACTION_PLAY;
	//		myLog(TAG,dbMsg);
			playNextSong(false);			// If we're stopped, just go ahead to the next song and start playing
		}else {									//カウントの変動が続いていたら判定ループに戻ってもう一度送り操作
	//		myLog(TAG,dbMsg);
			changeCount(mPlayer);						//タイマーオブジェクトを使ったカウンタ更新を追加
		}
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

/**
 * g_timer破棄
 * mPlayerの破棄（mPlayerの破棄）へ
 * */
@SuppressLint("NewApi")
private void processStopRequest(boolean force) {						//g_timerを止めてAudioFocusをNoFocusNoDuck、Notificationの書き換え <processStopRequest	boolean force
	final String TAG = "processStopRequest[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		if(mPlayer !=null){
			dbMsg= dbMsg +",レジュームするファイル=" + dataFN;/////////////////////////////////////
			if(dataFN !=null){
//				mainEditor.putString( "saisei_fname", String.valueOf(dataFN));				//レジュームするファイル
				dbMsg= dbMsg +"[mcPosition=" + mcPosition;/////////////////////////////////////
//				if(mcPosition >0 ){
//					mainEditor.putString( "pref_saisei_jikan", String.valueOf(mcPosition));		//再生ポジション
//				}
////				dbMsg= dbMsg +",累積；"+ ruikeikyoku+ "曲" + ruikeiSTTime ;/////////////////////////////////////
////				mainEditor.putString( "pref_zenkai_saiseKyoku", String.valueOf(ruikeikyoku));			//前回の連続再生曲数
////				mainEditor.putString( "pref_zenkai_saiseijikann", String.valueOf(ruikeiSTTime));		//前回の連続再生時間
//				dbMsg= dbMsg +"/" + saiseiJikan +"]";/////////////////////////////////////
//				mainEditor.commit();	// データの保存
				setPref();		//プリファレンス記載
				}
			}
	//		myLog(TAG,dbMsg);
		dbMsg="mState=" + mState;/////////////////////////////////////
		if (mState == State.Playing || mState == State.Paused ) {				//	 force////
			mState = State.Stopped;
			stopCount();		//タイマーオブジェクトを破棄
//			dbMsg += " , mPlayer=" + mPlayer;/////////////////////////////////////
//			relaxResources(true);										//mPlayerの破棄// let go of all resources...
//			dbMsg += " , mAudioFocus=" + mAudioFocus;/////////////////////////////////////
//			giveUpAudioFocus();											//AudioFocus.NoFocusNoDuckへ設定
//			dbMsg += " , mRemoteControlClient=" + mRemoteControlClient;/////////////////////////////////////
//			if (mRemoteControlClient != null) {
//				mRemoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_STOPPED);			// Tell any remote controls that our playback state is 'paused'.
//			}
		}
//		myLog(TAG,dbMsg);
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

/**
 * stopForegroundでサービスの消去して mPlayerの破棄
* Releases resources used by the service for playback. This includes the "foreground service" status and notification, the wake locks and possibly the MediaPlayer.
* @param releaseMediaPlayer Indicates whether the Media Player should also be released or not
*/
private void relaxResources(boolean releaseMediaPlayer) {				//mPlayerの破棄		<processStopRequest , playNextSong , onError , onDestroy , processPauseRequest
	final String TAG = "relaxResources[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		dbMsg="releaseMediaPlayer=" + releaseMediaPlayer;/////////////////////////////////////
		if(mBassBoost != null){
			mBassBoost.release();
		}
		if(mEqualizer != null){
			mEqualizer.release();
		}
//		stopForeground(true);		//APIL5 このサービスの消去 stop being a foreground service
		if (releaseMediaPlayer  ) {		// stop and release the Media Player, if it's available
			if ( mPlayer != null) {		// stop and release the Media Player, if it's available
				if( mPlayer.isPlaying()  ){
					mPlayer.pause();
					/* public void prepare()  Call this after setDataSource() or stop(), and before any other method that might throw IllegalStateException in this class
					 */
				}
//				musicVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//				dbMsg= dbMsg + ",musicVol=" + musicVol;/////////////////////////////////////
//				mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
//				dbMsg=dbMsg + ">>" + musicVol;/////////////////////////////////////
//  				myLog(TAG,dbMsg);
  				mPlayer.reset();
  				mPlayer.release();
			}
			mPlayer = null;
		}
		stopForeground(true);		//APIL5 このサービスの消去 stop being a foreground service
//		if (btHandler != null){
//			btHandler.removeMessages(1);
//			btHandler = null;
//		}
		mRelaxTime = System.currentTimeMillis();				//停止後 10 分再生がなかったらサービスを止める為のカウントスタート
//		myLog(TAG,dbMsg);
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

private void giveUpAudioFocus() {			//AudioFocus.NoFocusNoDuckへ設定
	final String TAG = "giveUpAudioFocus[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		if (mAudioFocus == AudioFocus.Focused && mAudioFocusHelper != null && mAudioFocusHelper.abandonFocus()) {
			mAudioFocus = AudioFocus.NoFocusNoDuck;
		}
		dbMsg="mAudioFocus=" + mAudioFocus;/////////////////////////////////////
	//	myLog(TAG,dbMsg);
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

/**
* Reconfigures MediaPlayer according to audio focus settings and starts/restarts it. This method starts/restarts the MediaPlayer respecting the current audio focus state. So if we have focus, it will
* play normally; if we don't have focus, it will either leave the MediaPlayer paused or set it to a low volume, depending on what is allowed by the current focus settings. This method assumes mPlayer !=null, so if you are calling it, you have to do so from a context where you are sure this is the case.
*/
private void configAndStartMediaPlayer() {			//既にMediaPlayerが生成されている	pause,start,setVolume 		<processPlayRequest
	final String TAG = "configAndStartMediaPlayer[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		if(mPlayer != null){				//レジューム対応を追加
			dbMsg=dbMsg +" , mcPosition= " + mPlayer.getCurrentPosition() +">>" + mcPosition;/////////////////////////////////////
			if (!mPlayer.isPlaying()) {
				if( mcPosition > 0 ){
					mPlayer.seekTo(mcPosition);		// Attempt to perform seekTo in wrong state
					dbMsg=dbMsg +" >>" + mPlayer.getCurrentPosition();/////////////////////////////////////
				}
		//		myLog(TAG,dbMsg);
				mPlayer.start();
				mState = State.Playing;
	//			sendPlayerState();
				changeCount( mPlayer );						//タイマーオブジェクトを使ったカウンタ更新を追加
			}
//		} else {
//			dbMsg="mAudioFocus=" + mAudioFocus +"(" + AudioFocus.NoFocusNoDuck+ "/" + AudioFocus.NoFocusCanDuck+ ")";/////////////////////////////////////
//			dbMsg=dbMsg +" , mPlayer= " + mPlayer;/////////////////////////////////////
//			if (mAudioFocus == AudioFocus.NoFocusNoDuck) {
//				if (mPlayer.isPlaying()) {			// If we don't have audio focus and can't duck, we have to pause, even if mState is State.Playing. But we stay in the Playing state so that we know we have to resume playback once we get the focus back.
//					mPlayer.pause();
//					mState = State.Paused;
//					myLog(TAG,dbMsg);
//	//				sendPlayerState();
//				}
//				return;
//			} else if (mAudioFocus == AudioFocus.NoFocusCanDuck) {
//				mPlayer.setVolume(DUCK_VOLUME, DUCK_VOLUME); // we'll be relatively quiet
//			} else {
//				mPlayer.setVolume(1.0f, 1.0f); // we can be loud
//			}
		}
		dbMsg= dbMsg + ";;" +dataFN;/////////////////////////////////////
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

private void tryToGetAudioFocus() {				//AudioFocus.Focusedと設定する;←onMusicRetrieverPrepared,processPlayRequest,
	final String TAG = "tryToGetAudioFocus[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		dbMsg="mAudioFocus =" +mAudioFocus;/////////////////////////////////////
		dbMsg= dbMsg +"mAudioFocusHelper =" +mAudioFocusHelper;/////////////////////////////////////
		if (mAudioFocus != AudioFocus.Focused && mAudioFocusHelper != null && mAudioFocusHelper.requestFocus()) {
			mAudioFocus = AudioFocus.Focused;
			dbMsg= dbMsg + ">>" +mAudioFocus;/////////////////////////////////////
		}
//		myLog(TAG,dbMsg);
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

public boolean yomiKomiCheck(String checkFN) throws IOException {		//setDataSourceを行う				 throws
	//←onCompletion,onMusicRetrieverPreparedからFalse , processSkipRequestでstopの時はtrue <processPlayRequest
	final String TAG = "yomiKomiCheck[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	boolean retBool = false;
	try{
		File file = new File(checkFN);
		retBool =  file.exists();
		dbMsg = checkFN + "の有無＝" +  retBool ;
		if(! retBool){
			Toast.makeText(this, checkFN + getResources().getString(R.string.comon_saisei_ijyou) , Toast.LENGTH_LONG).show();
		}
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
	return retBool;
	//08-02 16:37:29.802: I/Choreographer(25436): Skipped 91 frames!  The application may be doing too much work on its main thread.
	//UIのスレッド上の処理時間が長いと、途中で処理が中断されてしまう

}

	/**
	 * mItemsを読み込み指定データを確認。MediaPlerを生成して指定されたデータをセット(mPlayer.setDataSource)
	 * 読み込めないデータが指定されたら次の曲を読み込む
	 * Starts playing the next song. If manualUrl is null, the next song will be randomly selected from our Media Retriever
	 * (that is, it will be a random song in the user's device). If manualUrl is non-null, then it specifies the URL or path to the song that will be played next.
	 * 呼出し元onCompletion、onMusicRetrieverPrepared、
	 * 		onStartCommandでACTION_LISTSEL,processPlayRequestでmState == State.Paused 以外、okuriMpdosiで最終的な送り状態になった場合
	 * */
	public void playNextSong(boolean isOnlyPrepare) {		//setDataSourceを行う				 throws IOException　 throws IllegalArgumentException
		//←onCompletion,onMusicRetrieverPreparedからFalse , processSkipRequestでstopの時はtrue <processPlayRequest
		final String TAG = "playNextSong[MusicPlayerService]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			boolean yominaosi = false;
			dbMsg= "isOnlyPrepareは" + isOnlyPrepare;/////////////////////////////////////
			try {
				dbMsg= dbMsg + ",現在nowList[" + nowList_id + "]" + nowList + ";" +dataFN + "[" + mcPosition + "ms]";/////////////////////////////////////
				String b_dataFN = dataFN;
				int listEnd =  mItems.size();
				dbMsg= dbMsg + ",リスト中" + mIndex + "/" + listEnd;/////////////////////////////////////
				dbMsg= dbMsg + ",次のリスト[" + tugiList_id + "]" + tugiList;/////////////////////////////////////
				if(nowList.equals(getResources().getString(R.string.playlist_namae_request))){			//既にリクエストリスト実行中で
					int nokori = 0;
					Cursor cursor = null;
					siseizumiDataFN = String.valueOf(siseizumiDataFN);
					dbMsg= dbMsg + "削除する曲Uri= " + siseizumiDataFN;
					boolean sakujyoHantei = true;				//削除判定
					if( siseizumiDataFN == null){																	//2曲目以降は
						sakujyoHantei = false;
					}else if( siseizumiDataFN.equals("null") ){
						sakujyoHantei = false;
					}
					dbMsg= dbMsg + "削除判定= " + sakujyoHantei;
					if(sakujyoHantei){
						Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", nowList_id);
						String[] columns = null;			//{ idKey, nameKey };
						String selection =  MediaStore.Audio.Playlists.Members.DATA  + " = ? ";
						String[] selectionArgs = { siseizumiDataFN };
						String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
						cursor = this.getContentResolver().query(uri, columns, selection, selectionArgs, c_orderBy );
						nokori = cursor.getCount();
						dbMsg=dbMsg + ",該当"+cursor.getCount() +"件";
						if( cursor.moveToFirst() ){
							int delID = cursor.getInt(cursor.getColumnIndex( MediaStore.Audio.Playlists.Members._ID));
							dbMsg= dbMsg +"[" + delID +"]" ;/////////////////////////////////////
							String rStr = cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Playlists.Members.TITLE));
							dbMsg= dbMsg +";" + rStr;/////////////////////////////////////
							dbMsg= dbMsg+ "削除レコードのプレイリストUri= " + uri.toString();
							dbMsg= dbMsg+ ",削除するレコードID= " + delID;
							String where = "_id=" + Integer.valueOf(delID);
							int renint = getContentResolver().delete(uri, where, null);				//削除		getApplicationContext()
							dbMsg= dbMsg+ ",削除= " + renint + "レコード";
						}
						cursor = this.getContentResolver().query(uri, columns, null, null, c_orderBy );
						if(cursor.moveToFirst()){					//残りが有れば
							dataFN = cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Playlists.Members.DATA));
							dbMsg= dbMsg +"、次は" + dataFN;/////////////////////////////////////
						}else{
							main_pref = getSharedPreferences( getResources().getString(R.string.pref_main_file) ,MODE_PRIVATE);		//	getSharedPreferences(prefFname,MODE_PRIVATE);
							Map<String, ?> keys = main_pref.getAll();
							nowList_id = Integer.valueOf(keys.get("maeList_id").toString());	//
							dbMsg +="[" + nowList_id +"]" ;
							nowList = String.valueOf(keys.get("maeList").toString());
							dbMsg +="]" + nowList +"" ;
							dataFN = String.valueOf(keys.get("maeDatFN"));							//再生中のファイル名//DATA;The data stream for the file ;Type: DATA STREAM
							dbMsg +=" の　" + dataFN +"に戻す。" ;
							mainEditor.putString( "nowList", String.valueOf(nowList));
							mainEditor.putString( "nowList_id", String.valueOf(nowList_id));		//☆intで書き込むとcannot be cast
							mainEditor.putString( "pref_saisei_fname", String.valueOf(dataFN));		//再生中のファイル名
							mainEditor.putString( "maeList", null);
							mainEditor.putString( "maeList_id", String.valueOf(-1));		//☆intで書き込むとcannot be cast
							mainEditor.putString( "maeDatFN", null);		//再生中のファイル名
							boolean kakikomi = mainEditor.commit();
							dbMsg= dbMsg+",書き込み=" + kakikomi;	////////////////
						}
						yominaosi = true;
						if(! cursor.isClosed()){
							cursor.close();
						}
					}
				}else if(tugiList != null){ //リクエストリスト実行されていなくて　次のリストが設定されていたら…	 -1 < tugiList_id ||
					if(tugiList.equals(getResources().getString(R.string.playlist_namae_request))){ //リクエストリスト実行されていなくて　次のリストが設定されていたら…	 -1 < tugiList_id ||
						nowList_id = tugiList_id;
						nowList = tugiList;
						dbMsg=dbMsg + ">>リスト切替["+ nowList_id + "]" + tugiList;
						Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", nowList_id);
						String[] columns = null;			//{ idKey, nameKey };
						String selection =  null;			//MediaStore.Audio.Playlists.Members.DATA  + " = ? ";
						String[] selectionArgs = null;		//{ siseizumiDataFN };
						String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
						Cursor cursor = this.getContentResolver().query(uri, columns, selection, selectionArgs, c_orderBy );
						dbMsg=dbMsg + ",現在"+cursor.getCount() +"件登録";
						if( cursor.moveToFirst() ){
							dataFN = cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Playlists.Members.DATA));/////////////////////////////////////
							dbMsg= dbMsg +";" + dataFN;/////////////////////////////////////
							mcPosition = 0;
						}
						cursor.close();
						mainEditor.putString( "nowList_id", String.valueOf(nowList_id));		//☆intで書き込むとcannot be cast
						mainEditor.putString( "nowList", String.valueOf(nowList));
						mainEditor.putString( "pref_saisei_fname", String.valueOf(dataFN));		//再生中のファイル名
						boolean kakikomi = mainEditor.commit();
						dbMsg= dbMsg+",書き込み=" + kakikomi;	////////////////
						tugiList_id = -1;
						tugiList =null;
						yominaosi = true;
						cursor.close();
					}
				}
				if( yominaosi ){
					Item.itemsClear();
					mItems = new LinkedList<Item>();			//読み直し
					mItems = Item.getItems( getApplicationContext() );
					mIndex = Item.getMPItem(  dataFN);			//インデックスの逆検索	 ,mItems , getApplicationContext()
					listEnd =  mItems.size();
					dbMsg=dbMsg+"変更後のリスト中" + mIndex + "/" + listEnd;/////////////////////////////////////
				}
				int startCount = 0;
				int endCount = mItems.size()-1;
				if(listEnd <= mIndex ){					//mItems.size() <= mIndex
					mIndex = startCount;
					dbMsg= dbMsg + ">>" +mIndex;/////////////////////////////////////
				}else if( mIndex < startCount){
					mIndex = endCount;
					dbMsg= dbMsg + ">>" +mIndex;/////////////////////////////////////
				}
				Item playingItem = mItems.get(mIndex);
				if (playingItem == null) {
					Toast.makeText(this, "No available music to play. Place some music on your external storage device (e.g. your SD card) and try again.", Toast.LENGTH_LONG).show();
					myLog(TAG,dbMsg);
					processStopRequest(true); // stop everything!
					return;
				}
				Uri dataUri = playingItem.getURI( getApplicationContext() , mIndex );
				dataFN = String.valueOf(dataUri);
				dbMsg= dbMsg + ",読込確認；dataFN=" +dataFN;/////////////////////////////////////
				if( ! yomiKomiCheck(dataFN)){
					mIndex++;
					playNextSong(false);					// If we're stopped, just go ahead to the next song and start playing
				}
				dbMsg= dbMsg +"、mState=" +mState;/////////////////////////////////////
	//			dbMsg= dbMsg +"、mPlayer2=" +mPlayer2;/////////////////////////////////////
				mState = State.Stopped;
				dbMsg= dbMsg +">>" +mState;/////////////////////////////////////
		//		if( ! b_dataFN.equals(dataFN) || mPlayer == null ){			//読み込むデータが変わっていたら
					relaxResources(true);				//trueで再生しているmPlayerの破棄 // release everything except MediaPlayer
					createMediaPlayerIfNeeded();					//MediaPlerを生成←playNextSong// set the source of the media player a a content URI
		//		}
				dbMsg= dbMsg +"、mPlayer=" +mPlayer;/////////////////////////////////////
				dbMsg= dbMsg +",mcPosition=" + mcPosition + "mS";/////////////////////////////////////
				try {
					mPlayer.setLooping(true);						//☆以下二つより先に行わないとIllegalStateExceptionが発生
					mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
					mPlayer.setDataSource(getApplicationContext(), dataUri );				//☆MediaPlayer.createではjava.lang.IllegalStateException		http://umezo.hatenablog.jp/entry/20100531/1275319329

					dbMsg= dbMsg +">>" +mPlayer;			/////////////////////////////////////
		//			myLog(TAG,dbMsg);
					mPlayer.prepareAsync();							//データを非同期で読み込み、読み込み完了と同時にonCompletionを実行
//java.lang.IllegalStateException	既に.createされている？

					siseizumiDataFN = dataFN;				//前回再生済みのファイル名
					/*ユーザーの操作が速過ぎて「Prepareing」中に再度「MediaPlayer#prepareAsync()」を呼び出してしまうということが起こりがち*/
					/*prepare() は同期は「MediaPlayer#reset()」を呼び出せない「Preparing」に移行しないので簡単ですが、UIスレッドをブロックしてユーザーを待たせてしまったり、
					 * 最悪の場合は「ANR（Application Not Responding、アプリ無反応）」が発生してアプリが落ちてしまう可能性があります。*/
	//06-28 20:22:59.336: E/MediaPlayer(9526): prepareAsync called in state 1
//08-02 20:11:00.950: E/MediaPlayer-JNI(4324): QCMediaPlayer mediaplayer NOT present//unBindしてないサービスを再びbindしようとしていたのが原因
//	08-04 21:51:47.377: E/MediaPlayer-JNI(527): Not Using QCMediaPlayer,setting qc_post_event to NULL...
//	Skipped 259 frames!  The application may be doing too much work on its main thread.UIのスレッド上の処理時間が長いと、途中で処理が中断されてしまう
					//				contSong(mIndex);		//次の曲を準備する;2曲目
//							mPlayer.setNextMediaPlayer(mPlayer2);										//次の曲をセットする
				//			songInfoSett( mPlayer);
//							contSong(mIndex+1);		//更に次の曲を準備する;3曲目
//							dbMsg= dbMsg + ";;mcPosition=" + mcPosition;/////////////////////////////////////
		//			myLog(TAG,dbMsg);
				} catch (IllegalArgumentException e) {
					//ダイアログならdismissメソッドを呼び出す時に、ダイアログを表示したアクティビティが破棄されているのが原因
					myErrorLog(TAG,dbMsg+"で"+e);
					onPrepared( mPlayer);
				} catch (IllegalStateException e) {
					myErrorLog(TAG,dbMsg+"で"+e);
				}
			} catch (IOException e) {
					myErrorLog(TAG,dbMsg+"で"+e);
			}
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
		//MediaPlayer　 Uri is
		//http://init0.net/wp/archives/405
	}

	public MediaPlayer contSong(int nowCount , MediaPlayer mp) {		//次の曲を準備する
	final String TAG = "contSong[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		dbMsg= "再生中は"+ nowCount + "/" + mItems.size();///////
	//		dbMsg= dbMsg +"、VERSIONは" + Build.VERSION.SDK_INT;/////////////////////////////////////
			tugiNoKyoku = nowCount+1;
			if( mItems.size() < tugiNoKyoku){
				tugiNoKyoku= 1;
			}
			dbMsg= dbMsg +"、次は" + tugiNoKyoku;/////////////////////////////////////
	//		MaraSonActivity MSA = new MaraSonActivity();
			uriNext =playingItem.getURI( getApplicationContext() , tugiNoKyoku);						//次のUri
			dbMsg= dbMsg +"）" + uriNext;/////////////////////////////////////
			if( mp != null ){
				dbMsg= dbMsg +",mp=" + mp.getAudioSessionId();/////////////////////////////////////
				mp.reset();									//MediaPlayerを抹消
				mp = null;
			}
			mp = getMediaPlayer( this.getApplicationContext() );			//			?rContext
		//	mp = new MediaPlayer();				//MediaPlayerを生成
			dbMsg= dbMsg +">>" + mp;////////////////////////////////

			mp.setDataSource(getApplicationContext(), uriNext );		//☆MediaPlayer.createdではjava.lang.IllegalStateException
			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
//			if(16 <= Build.VERSION.SDK_INT){
//				mp.setLooping(true);					//この二つで
//				mp.pause();
////				mPlayer.setNextMediaPlayer(mPlayer2);										//次の曲をセットする
//				mp.prepareAsync();		//データを非同期で読み込み、読み込み完了と同時にonCompletionを実行
//				//☆ここでsetOnPreparedListener、setOnCompletionListenerを設定するとmPlayeｒのイベントに干渉する
////			} else {
////				mPlayer.setOnCompletionListener(this);
//			}
//			myLog(TAG,dbMsg);
			mState = State.Playing;			//	20150504	mState = State.Preparing;
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
		return mp;
	}

	/**
	 * Should have subtitle controller already set対策
	 * http://stackoverflow.com/questions/20087804/should-have-subtitle-controller-already-set-mediaplayer-error-android
	 * */
	static MediaPlayer getMediaPlayer(Context context){
		final String TAG = "getMediaPlayer[MusicPlayerService]";
		String dbMsg="開始";/////////////////////////////////////
		MediaPlayer mediaplayer = null;
		try{
			dbMsg= "context="  + context;//////////////////////////////
			mediaplayer = new MediaPlayer();
			dbMsg= dbMsg + ",mediaplayer"  + mediaplayer;//////////////////////////////
			if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
				return mediaplayer;
			}
			if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
				try {
					Class<?> cMediaTimeProvider = Class.forName( "android.media.MediaTimeProvider" );
					Class<?> cSubtitleController = Class.forName( "android.media.SubtitleController" );
					Class<?> iSubtitleControllerAnchor = Class.forName( "android.media.SubtitleController$Anchor" );
					Class<?> iSubtitleControllerListener = Class.forName( "android.media.SubtitleController$Listener" );
					Constructor constructor = cSubtitleController.getConstructor(new Class[]{Context.class, cMediaTimeProvider, iSubtitleControllerListener});
					Object subtitleInstance = constructor.newInstance(context, null, null);
					Field f = cSubtitleController.getDeclaredField("mHandler");
					f.setAccessible(true);
					try {
						f.set(subtitleInstance, new Handler());
					}catch (IllegalAccessException e) {return mediaplayer;}
					finally {
						f.setAccessible(false);
					}
					Method setsubtitleanchor = mediaplayer.getClass().getMethod("setSubtitleAnchor", cSubtitleController, iSubtitleControllerAnchor);
					setsubtitleanchor.invoke(mediaplayer, subtitleInstance, null);
					dbMsg= dbMsg +"subtitle is setted :p";//////////////////////////////
				} catch (Exception e) {
					myErrorLog(TAG,dbMsg+"で"+e);
				}
			}
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
		return mediaplayer;
	}

	public void songInfoSett(MediaPlayer player) {
		final String TAG = "songInfoSett[MusicPlayerService]";
		String dbMsg="開始";/////////////////////////////////////
		try{
//20160109			makeLockScreenP( player );					//ロックスクリーン作成
			dbMsg= dbMsg +"、g_timer=" + g_timer;/////////////////////////////////////
			dbMsg= dbMsg +"、g_handler=" + g_handler;/////////////////////////////////////
			player.setLooping(false);		//ループ再生はしない
			changeCount( player );						//タイマーオブジェクトを使ったカウンタ更新を追加
//			dbMsg=dbMsg+"mIsOnlyPrepare=" + mIsOnlyPrepare;/////////////////////////////////////
//			if (mIsOnlyPrepare) {		// The media player is done preparing. That means we can start playing!
//				mState = State.Stopped;
//			} else {
//				mState = State.Playing;
//			}
//			dbMsg=dbMsg+",mState=" + mState;/////////////////////////////////////
			dbMsg= dbMsg + ";;" +dataFN;/////////////////////////////////////
	//		myLog(TAG,dbMsg);
			sendPlayerState(mPlayer);					//ここまでの設定結果をBroad
//			dbMsg= dbMsg +">>" + mIsOnlyPrepare;/////////////////////////////////////
//			if (!mIsOnlyPrepare) {
//				configAndStartMediaPlayer();
//			}
//			mainEditor.putString( "saisei_fname", String.valueOf(dataFN));				//レジュームするファイル
//			boolean kakikae = mainEditor.commit();	// データの保存
//			dbMsg= dbMsg +"、書き換え=" + kakikae;/////////////////////////////////////
			setPref();		//プリファレンス記載
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	//06-24 22:11:45.218: E/MediaPlayer(26842): error (-38, 0)
	//06-24 22:11:45.218: E/MediaPlayer(26842): pause called in state 2
	//06-24 22:11:44.907: E/MediaPlayer(26842): Attempt to call getDuration without a valid mediaplayer
	//	http://seesaawiki.jp/w/moonlight_aska/d/%BA%C6%C0%B8%A4%AC%BD%AA%CE%BB%A4%B9%A4%EB%A4%C8....
	}

	/**
	* MediaPlayerを生成し、PowerManagerへロック画面を利用宣言を送る
	* MediaPlayerが生成されていたら、一旦破棄して廃棄(生成し直し)
	* 開始点を設定する
	* Makes sure the media player exists and has been reset. This will create the media player if needed, or reset the existing media player if one already exists.
	* 呼出し元はplayNextSong
	*/
	public void createMediaPlayerIfNeeded() {				//MediaPlayerを生成←playNextSong
		final String TAG = "createMediaPlayerIfNeeded[MusicPlayerService]";
		String dbMsg=ORGUT.nowTime(true,true,true);/////////////////////////////////////
		try {
			dbMsg= "mPlayer = " + mPlayer;/////////////////////////////////////
			if (mPlayer != null) {								//MediaPlayerが生成されていれば
				if(mPlayer.isPlaying()){						//再生中なら
					mPlayer.pause();								//止める☆stopだとprepareAsync called in state 1が発生する？
				}
				mPlayer.reset();									//MediaPlayerを抹消
				/*状態が「Idle」に移行。 「Preparing」「End」以外の状態で呼び出すことが可能です。
				 * 次のデータを再生したい場合は、 とにかく「Idle」に戻って 、データの読み込みからやり直す*/
				mPlayer = null;
//		  		if(g_timer != null){
//		  			g_timer.cancel();  							// タイマも消去
//		  		}
//				if( rp_pp ){						//2点間リピート中で//リピート区間終了点
//					mcPosition =pp_start;									//前に再生していた曲の再生ポジションを消去
//				} else {
//					mcPosition =0;									//前に再生していた曲の再生ポジションを消去
//				}
				createMediaPlayerIfNeeded();				//再起して作り直し
			} else {
				mPlayer = getMediaPlayer( this.getApplicationContext() );			//			?rContext
			//	mPlayer = new MediaPlayer();				//MediaPlayerを生成
//				dbMsg=dbMsg + ",musicVol=" + musicVol;/////////////////////////////////////
//				mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
//				dbMsg=dbMsg +",>>" + musicVol;/////////////////////////////////////
			//	mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);			//ロック画面を利用；makeLockScreenPで行われるのでここは省略
				if (21 <= android.os.Build.VERSION.SDK_INT  ) {
				}else if (14 <= android.os.Build.VERSION.SDK_INT  ){
					makeLockScreenP( mPlayer );					//ロックスクリーン作成 < songInfoSett
				}
				// Make sure the media player will acquire a wake-lock while playing. If we don't do that, the CPU might go to sleep while the song is playing, causing playback to stop.
				// Remember that to use this, we have to declare the android.permission.WAKE_LOCK permission in AndroidManifest.xml.
				mPlayer.setOnPreparedListener(this);	// we want the media player to notify us when it's ready preparing, and when it's done playing:
				mPlayer.setOnCompletionListener(this);
//				mPlayer.setOnErrorListener(this);
//				mPlayer.setLooping(true);						//20150321
				dbMsg= dbMsg + ">> " + mPlayer;//////////////////////////////////
		//		mPlayer.setAuxEffectSendLevel(1.0f);					//効果のレベル；デフォルトでは、センドレベルはは0.0fなので必要
				dbMsg=dbMsg + ",equalizerSet=" + equalizerSet;/////////////////////////////////////
				if(! equalizerSet){
					dbMsg=dbMsg + ",pref_toneList=" + pref_toneList;/////////////////////////////////////
					if( pref_toneList == null ){
						getEqualizer( );					//初期Equalizer情報の取得
					}
					dbMsg=dbMsg + ">equalizerSet>" + equalizerSet;/////////////////////////////////////
				}
				dbMsg=dbMsg + ",mBassBoost=" + mBassBoost;/////////////////////////////////////
				if(mBassBoost == null){
					bassBoostBody( bBoot );		//ベースブーストOn/Off本体
					dbMsg=dbMsg + ">>" + mBassBoost;/////////////////////////////////////
				}
				dbMsg=dbMsg + ",reverbBangou=" + reverbBangou;/////////////////////////////////////
				if(0< reverbBangou){
					dbMsg=dbMsg + ",mPresetReverb=" + mPresetReverb;/////////////////////////////////////
					if( mPresetReverb == null){
						presetReverbBody(reverbBangou);					//リバーブ設定本体
						dbMsg=dbMsg + ">>" + mPresetReverb;/////////////////////////////////////
					}
				}
				dbMsg=dbMsg + ",mVisualizer=" + mVisualizer;/////////////////////////////////////
				mVisualizer = setupVisualizer( mVisualizer);
				dbMsg=dbMsg + ">>" + mVisualizer;/////////////////////////////////////
			}
			if( rp_pp ){						//2点間リピート中で//リピート区間終了点
				mcPosition =pp_start;									//前に再生していた曲の再生ポジションを消去
//			} else {
//				mcPosition = 0;									//前に再生していた曲の再生ポジションを消去
			}
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		//Not Using QCMediaPlayer,setting qc_post_event to NULL...あなたのプラットフォームがQCMediaPlayerをサポートしていない
	}

	public Timer g_timer = null;
	public Handler g_handler = new Handler();
	public int kankaku;
//	int okuriCount ;
	public long stTime;
	MediaPlayer rPlayer;

	public void stopCount(){		//タイマーオブジェクトを破棄
	final String TAG = "stopCount[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg= dbMsg + ",タイマー設定；g_timer="+g_timer;/////////////////////////////////////
	  		if(g_timer != null){
	  			g_timer.cancel(); 		// タイマの設定
	  			g_timer = null;
				dbMsg= dbMsg + ">>"+g_timer;/////////////////////////////////////
	  		}
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}

	/**
	 * タイマーオブジェクトを使ったカウンタ更新/曲の変わり目検出
	 * playNextSong  , configAndStartMediaPlayer
	 * */
	public void changeCount(MediaPlayer player){		//タイマーオブジェクトを使ったカウンタ更新←playNextSong  , configAndStartMediaPlayer
	final String TAG = "changeCount[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
		try{
			kankaku = 100;
			stTime = System.currentTimeMillis()  ;
			dbMsg= dbMsg +","+stTime + "mS ";/////////////////////////////////////
			rPlayer= player;
//			if( mSelfStopThread.isAlive() ){
//				selfStop = false;
//			}
			dbMsg= dbMsg + ",タイマー設定；g_timer="+g_timer;/////////////////////////////////////
	  		if(g_timer == null){
	  			g_timer = new Timer(true);  		// タイマの設定
				dbMsg= dbMsg + ">>"+g_timer;/////////////////////////////////////
	  		}
	  		if( player != null ){
				saiseiJikan = player.getDuration();
				dbMsg= dbMsg + "[" +  saiseiJikan + "]";/////////////////////////////////////
				dbMsg= dbMsg + dataFN;/////////////////////////////////////
//20150601;java.lang.IllegalStateException: Timer was canceled

			//	myLog(TAG,dbMsg);
//				g_timer.schedule( new TimerTask(){
			timertask = new TimerTask() {
				@Override
				public void run() {
					g_handler.post( new Runnable() {
					public void run() {
						final String TAG = "run[changeCount.MusicPlayerService]";
						String dbMsg="g_handler="+g_handler+",g_timer="+g_timer+"/"+ saiseiJikan +"mS,";/////////////////////////////////////
						try {
							Intent intent = new Intent(ACTION_STATE_CHANGED);
							dbMsg= dbMsg + ",action=" + action ;/////////////////////////////////////
							if( action.equals(MusicPlayerService.ACTION_SKIP) || action.equals(MusicPlayerService.ACTION_REWIND)){
								long sTime = System.currentTimeMillis() - stTime ;
								dbMsg= dbMsg +",sTime="+ sTime + "mS後[ "+ stTime+"]";/////////////////////////////////////
								if( 1000 < sTime){
									dbMsg= dbMsg + "のfrCount= " + MusicPlayerService.this.frCount;/////////////////////////////////////
					//				myLog(TAG,dbMsg);
									okuriMpdosi(MusicPlayerService.this.frCount);		//送り戻しの実行
									MusicPlayerService.this.frCount = 0;
								}
							} else {
								if( mPlayer == null ){
									if(rp_pp){						//2点間リピート中で//リピート区間終了点
										mcPosition = pp_start;		//リピート区間開始点
									}else {
										mcPosition = 0;
									}

									IsPlaying  = false ;			//再生中か
									IsSeisei  = false ;				//生成中
								} else {
									mcPosition = mPlayer.getCurrentPosition();
									IsPlaying  = mPlayer.isPlaying() ;			//再生中か
									IsSeisei  = true ;				//生成中
								}
								dbMsg= "[" + mcPosition + "/"+saiseiJikan + "]";/////////////////////////////////////
								intent.putExtra("mcPosition", mcPosition);
								ruikeikasannTime = mcPosition;			//累積加算時間
								intent.putExtra("saiseiJikan", saiseiJikan);
								intent.putExtra("data", dataFN);
								dbMsg= dbMsg + ",生成中= " + IsSeisei;//////////////////////////////////
								intent.putExtra("IsSeisei", IsSeisei);
								dbMsg= dbMsg + ",再生中か= " + IsPlaying;//////////////////////////////////
								intent.putExtra("IsPlaying", IsPlaying);
								int nokori = (int) (saiseiJikan - mcPosition-kankaku);
					  			dbMsg= dbMsg + "," + titolName +"再生中の残" + nokori + "/" + crossFeadTime + "mSで次曲へ";/////////////////////////////////////
//								dbMsg= dbMsg + "mBluetoothAdapter=" +mBluetoothAdapter;
//								if (mBluetoothAdapter == null) {					//スマフォのBluetiithをOffにしたら
//								} else {
									if(stateBaseStr != null && b_stateStr != null){
										if( ! b_stateStr.equals(stateBaseStr)){
											dbMsg= dbMsg + "stateBaseStr=" +stateBaseStr;
											intent.putExtra("stateBaseStr", stateBaseStr);
											b_stateStr = stateBaseStr;
										}
									}
//								}
		//											myLog(TAG , dbMsg);
					  			if( ( nokori <= crossFeadTime ||
					  					(rp_pp && pp_end < mcPosition)					//2点間リピート中で//リピート区間終了点
					  					)){			//	&&  (Build.VERSION.SDK_INT <16)
									dbMsg= dbMsg + "[ " + mIndex +",再生時間="+ saiseiJikan;/////////////////////////////////////
									dbMsg= dbMsg +",mState = " +  mState ;/////////////////////////////////////
				//					myLog(TAG,dbMsg);
									onCompletion( mPlayer);		/** 再生中にデータファイルのENDが現れた場合にコールCalled when media player is done playing current song. */
						//			if( (Build.VERSION.SDK_INT <16)){	//Android4.2以前は
					//				completionRegasy();				// 再生中にデータファイルのENDが現れた場合にsetNextMediaPlayerを使わず曲送り. */
//										if( mPlayer.isPlaying() ){				//ギャップレス設定で既に送られている場合がある
//											dbMsg="mIndex = " + mIndex;/////////////////////////////////////
//											processSkipRequest();
//										}else{				//再生されていないのに
											dbMsg= dbMsg + ">残り時間> " + nokori + "( crossFead=" + crossFeadTime +")"  ;/////////////////////////////////////
//											myLog(TAG,dbMsg);
//											if(nokori<= (kankaku*2)){
//												playNextSong(false);
//												processSkipRequest();
//											}
//										}
					//				}
								} else {							// if( nokori <= crossFeadTime-100  )
									imanoJyoutai =  MuList.sonomama ;
									onCompletNow = false;			//曲間処理中
									if(saiseiJikan-mcPosition > 75){
							//			sendPlayerState(mPlayer);
										sendBroadcast(intent);					//APIL1
									}
									if(nokori > kankaku){
										if((kankaku*3 > nokori)  &&  (1 < kankaku)){						//
											dbMsg=  ",間隔= " +kankaku  ;/////////////////////////////////////
											kankaku = kankaku/2;
											dbMsg= dbMsg + ">>" +kankaku  ;/////////////////////////////////////
				//							myLog(TAG,dbMsg);
										}
									}else{
										onCompletion( mPlayer);		/** 再生中にデータファイルのENDが現れた場合にコールCalled when media player is done playing current song. */
									}
								}
							}
						} catch (Exception e) {
							myErrorLog(TAG,dbMsg +"で"+e.toString());
						}
						} //run
					}); //g_handler.post
				} //run
//				}, 100, kankaku);		//最初に呼ばれるまでの待ち時間と、その後のインターバル
			};		//最初に呼ばれるまでの待ち時間と、その後のインターバル
			g_timer = new Timer(); //This is new
			g_timer.schedule(timertask, 0, kankaku); // execute in every 15sec
	  		}
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}

///独自イベント///////////////////////////////////////////////////////////////////////////////////
long tunagiJikan;		//測定用
boolean onCompletNow = false;			//曲間処理中
	public void onCompletion(MediaPlayer player) {			/** 再生中にデータファイルのENDが現れた場合にコールCalled when media player is done playing current song. */
//☆曲の終了でも発生する
	final String TAG = "onCompletion[MusicPlayerService]";
	String dbMsg="ENDマーク検出から";/////////////////////////////////////
	try{
//		dbMsg= dbMsg + ",uriNext=" +uriNext;/////////////////////////////////////
//		if( uriNext != null ){
			tunagiJikan = System.currentTimeMillis();		// 開始時刻の取得
			if(g_timer != null){
				g_timer.cancel();
			}
			g_timer =null;			//☆毎回破棄しないとChoreographer: Skipped ○○」 frames!  The application may be doing too much work on its main thread.
			dbMsg= dbMsg +"、mIndex=" + mIndex;/////////////////////////////////////
			mIndex ++;
			if( mItems.size() < mIndex){
				mIndex= 1;
			}
			dbMsg= dbMsg +"、次は" + mIndex;/////////////////////////////////////
			ruikeikyoku++;			//累積曲数
			dbMsg= dbMsg +"累積曲数" + ruikeikyoku +"曲";/////////////////////////////////////
			if(rp_pp){						//2点間リピート中で//リピート区間終了点
				mcPosition = pp_start;		//リピート区間開始点
			}else {
				mcPosition = 0;
			}
			playNextSong(false);					// If we're stopped, just go ahead to the next song and start playing
//		}
//		onCompletNow = true;			//曲間処理中
//		completionRegasy();				// 再生中にデータファイルのENDが現れた場合にsetNextMediaPlayerを使わず曲送り. */
////		dbMsg="読込終了=" + prepareConm ;/////////////////////////////////////
////		if ( prepareConm ){				//読込終了
////			prepareConm = false ;
//		dbMsg= dbMsg +"、mIndex=" + mIndex;/////////////////////////////////////
//		dbMsg= dbMsg +">>" + mIndex;	/////////////////////////////////////
//
//		makeLockScreenP(player);					//ロックスクリーン作成
////			dbMsg= dbMsg +"、g_timer=" + g_timer;/////////////////////////////////////
////			dbMsg= dbMsg +"、g_handler=" + g_handler;/////////////////////////////////////
////			changeCount();						//タイマーオブジェクトを使ったカウンタ更新を追加
////			sendPlayerState();					//ここまでの設定結果をBroad
////			ruikeikyoku++;			//累積曲数
////			dbMsg= dbMsg +">>" + ruikeikyoku +"曲";/////////////////////////////////////
//////		} else {
////			dbMsg= dbMsg + ",SDK_INT= " + Build.VERSION.SDK_INT   ;/////////////////////////////////////
////			if(Build.VERSION.SDK_INT < 16 ){
//////				if( player.isPlaying() ){				//ギャップレス設定で既に送られている場合がある
//////				dbMsg="mIndex = " + mIndex;/////////////////////////////////////
////				myLog(TAG,dbMsg);
////				processSkipRequest();
//////			}else{				//再生されていないのに
//////				dbMsg= dbMsg + ">残り時間> " + nokori + "( crossFead=" + crossFeadTime +")"  ;/////////////////////////////////////
//////				myLog(TAG,dbMsg);
//////				if(nokori<= (kankaku*2)){
//////					playNextSong(false);
//////					processSkipRequest();
//////				}
//////			}
////			} else {
////				dbMsg= dbMsg + ",mPlayer2= " + mPlayer2 ;
////				mPlayer2.setLooping(true);					//この二つで
//////				mPlayer2.pause();
////				mPlayer.setNextMediaPlayer(mPlayer2);					//次の曲をセットすると前にセットした曲が再生される
////				dbMsg= dbMsg + ">id> " + mPlayer.getAudioSessionId()   ;/////////////////////////////////////
////				mPlayer.setLooping(false);					//ループを止めて
////				mState = State.Playing;
////				dbMsg= dbMsg + ",mIndex =" + mIndex   ;/////////////////////////////////////
////				mIndex++;
////				dbMsg= dbMsg + ">>" + mIndex ;/////////////////////////////////////
////				dataFN = String.valueOf(uriNext);				//☆操作対象ファイルのURLを更新ないと曲が送られない
////				dbMsg= dbMsg + ",dataFN= " + dataFN ;/////////////////////////////////////
////				songInfoSett( mPlayer);
////				mPlayer2 = contSong(mIndex , mPlayer2);		//次の曲を準備する
////			}
////			imanoJyoutai =  MuList.sonomama ;
			if( tunagiJikan > 0 ){
				tunagiJikan = System.currentTimeMillis()-tunagiJikan;		// 開始時刻の取得
				dbMsg= dbMsg + ",前曲から" +tunagiJikan +"mS経過、," ;/////////////////////////////////////
				dbMsg= dbMsg + mPlayer.toString()  ;/////////////////////////////////////
			}
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	//	http://seesaawiki.jp/w/moonlight_aska/d/%BA%C6%C0%B8%A4%AC%BD%AA%CE%BB%A4%B9%A4%EB%A4%C8....
	}

public void onPrepared(MediaPlayer player) {					//playNextSongに続いて データ詠み込み後の処理	Called when media player is done preparing
	final String TAG = "onPrepared[MusicPlayerService]";
	String dbMsg="これから再生、";/////////////////////////////////////
	try{
		dbMsg= "渡されたplayer=" +player ;/////////////////////////////////////
		dbMsg= dbMsg + ",既存のplayer=" +player ;/////////////////////////////////////
		if( player == mPlayer ){
			imanoJyoutai =  MuList.sonomama ;
			dbMsg=dbMsg+ "、isPlaying=" + player.isPlaying() ;/////////////////////////////////////
			if( !player.isPlaying() ){
				player.setVolume(DUCK_VOLUME, DUCK_VOLUME); 	//0.1f; we'll be relatively quiet
				dbMsg= dbMsg +",2点間リピート中=" + rp_pp ;/////////////////////////////////////
				if( rp_pp ){						//2点間リピート中の時だけ//リピート区間終了点
					mcPosition =pp_start;			//前に再生していた曲の再生ポジションを消去
				}
				dbMsg= dbMsg +",mcPosition=" + mcPosition ;//////////////////////////////////
				if( 0 < mcPosition ){
					player.seekTo(mcPosition);
				}
				kankaku = 100;
				player.start();				//java.lang.SecurityException: Neither user 10859 nor current process has android.permission.WAKE_LOCK.
				player.setVolume(1.0f, 1.0f); // we can be loud
				mState = State.Playing;			//	20150504	mState = State.Preparing;
				dbMsg= dbMsg +">>" + player.isPlaying() ;/////////////////////////////////////
			}
			songInfoSett( player);
//			main_pref = getSharedPreferences( getResources().getString(R.string.pref_main_file) ,MODE_PRIVATE);		//MODE_WORLD_WRITEABLE 	getSharedPreferences(prefFname,MODE_PRIVATE);
//			mainEditor = main_pref.edit();
//			mainEditor.putString("pref_saisei_fname",dataFN);				//再生していた曲	.commit()
//			Boolean kakikomi = mainEditor.commit();	// データの保存
//			dbMsg +=",kakikomi="+kakikomi;////////////////////////////////////////////////////////////////////////////
			setPref();		//プリファレンス記載
//			if( mPlayer2 != null ){
//				dbMsg= dbMsg +",mPlayer2=" + mPlayer2.getAudioSessionId();/////////////////////////////////////
//				mPlayer2.reset();									//MediaPlayerを抹消
//				mPlayer2 = null;
//			}
//			mPlayer2 = new MediaPlayer();				//MediaPlayerを生成
//			mPlayer2 =contSong(mIndex , mPlayer2);		//次の曲を準備する;2曲目  ;20150628uriまで
////			if(16 <= Build.VERSION.SDK_INT ){
////				mPlayer2.setOnPreparedListener(MusicPlayerService.this);	// we want the media player to notify us when it's ready preparing, and when it's done playing:
////				mPlayer2.prepareAsync();		//データを非同期で読み込み、読み込み完了と同時にonCompletionを実行
////			}
//		} else if( player == mPlayer2 ){
//			dbMsg= dbMsg + "次曲の登録mPlayer2=" + mPlayer2.getAudioSessionId() ;/////////////////////////////////////
//			player.setLooping(true);					//この二つで
//			player.pause();								//次曲も再生が始まるがmPlayer2が再生され制御不能になる
//	//		mPlayer.setNextMediaPlayer(player);										//次の曲をセットする  セットすると前にセットして曲を再生
//			/*Set the MediaPlayer to start when this MediaPlayer finishes playback (i.e. reaches the end of the stream).
//				MediaPlayerの再生を終了した(ストリームの終わりに達した）ときに開始する次のMediaPlayerを設定します。
//			 * The media framework will attempt to transition from this player to the next as seamlessly as possible.
//				メディアフレームワークをシームレスに可能な限り次にこのプレーヤーからの移行しようとします。
//			 * The next player can be set at any time before completion.
//				次のプレイヤーを終了させる時間を任意に設定することができます。
//			 * The next player must be prepared by the app, and the application should not call start() on it.
//				次のプレーヤーは、アプリによって準備する必要があり、アプリケーショ更にスタートを呼び出すべきではありません。
//			 * The next MediaPlayer must be different from 'this'. An exception will be thrown if next == this.
//				次のMediaPlayerのは、現在再生しているファイルと異なっている必要があります。==この次の場合は例外がスローされます。
//			 * The application may call setNextMediaPlayer(null) to indicate no next player should be started at the end of playback.
//				アプリケーションには次のプレーヤーが再生終了時に開始されるべきではない示すためにsetNextMediaPlayer（null）を呼び出すことができます。
//			 *  If the current player is looping, it will keep looping and the next player will not be started.
//				現在のプレイヤーがループしている場合は、ループを維持し、次のプレイヤーは開始されません。*/
		}
		dbMsg=dbMsg+ "現在再生中、mPlayer=" + mPlayer.getAudioSessionId() ;/////////////////////////////////////
		dbMsg= dbMsg + ",状態=" + imanoJyoutai ;/////////////////////////////////////
	//	myLog(TAG,dbMsg);
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

///APIL21対応////////////////////////////////////////////////////////////////////////////////////////////////
//private static final String ACTION_TOGGLE_PLAYBACK = "com.your.package.name.TOGGLE_PLAYBACK";
//private static final String ACTION_PREV = "com.your.package.name.PREV";
//private static final String ACTION_NEXT = "com.your.package.name.NEXT";
public  Notification lpNotification;
public TransportControls lpNControls;
public  MediaSession mediaSession;		//final
/**
 * sendPlayerStateから呼出しボタンをアップする度に呼び出される
 * ？ロックスクリーンでは呼び出されない？
 * http://stackoverflow.com/questions/27209596/media-style-notification-not-working-after-update-to-android-5-0
 * 	retreivePlaybackActionはnoti作成時に動作してしまうので
 * 		のprivate Notification.Action generateAction( int icon, String title, String intentAction ) を使用
 * 	Using Android Media Style notifications with Media Session controls										https://www.binpress.com/tutorial/using-android-media-style-notifications-with-media-session-controls/165
 * Android Wear に Notification で出来ること									http://y-anz-m.blogspot.jp/2014/07/android-wear-notification.html
 * ロック画面でのメディア再生をコントロールする					http://developer.android.com/intl/ja/guide/topics/ui/notifiers/notifications.html#controllingMedia
 * */
@SuppressLint("NewApi")
public void lpNotificationMake(String keyArtist  , String keyAlbum , String keyTitle , String albumArt) {
	final String TAG = "lpNotificationMake[MusicPlayerService]";
	String dbMsg="開始";
	try{
		dbMsg="KeyArtist =" + keyArtist;
		dbMsg= dbMsg + ",keyAlbum =" + keyAlbum;
		dbMsg= dbMsg + ",keyTitle =" + keyTitle;
//		OrgUtil ORGUT = new OrgUtil();	//自作関数集
		Drawable drawable =  getApplicationContext().getResources().getDrawable(R.drawable.no_image);;
		Bitmap artwork  = BitmapFactory.decodeResource( getResources() , R.drawable.no_image);
		dbMsg= dbMsg + ",albumArt =" + albumArt;
		if(albumArt != null){
			drawable = new BitmapDrawable(getResources(), albumArt);
			Bitmap orgBitmap = ((BitmapDrawable)drawable).getBitmap();					//DrawableからBitmapインスタンスを取得//				http://android-note.open-memo.net/sub/image__resize_drawable.html
			dbMsg +=",orgBitmap="+orgBitmap;
			Bitmap resizedBitmap = Bitmap.createScaledBitmap(orgBitmap, 96, 96, false);										//100x100の大きさにリサイズ
			dbMsg= dbMsg +",resizedBitmap="+resizedBitmap;
			drawable = new BitmapDrawable(getResources(), resizedBitmap);
			artwork = ORGUT.retBitMap( albumArt  , 144 , 144 ,  getResources() );		//指定したURiのBitmapを返す	 , dHighet , dWith ,
			dbMsg= dbMsg + ">>" + albumArt;
		}
		Intent intent = new Intent( getApplicationContext(), MaraSonActivity.class );						//タップで表示する画面		http://qiita.com/roga7zl/items/4c9e1b62db1b427a9226
		intent.putExtra("notification_ID", NOTIFICATION_ID); 
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_CANCEL_CURRENT);		//http://y-anz-m.blogspot.jp/2011/07/androidappwidget-pendingintent-putextra.html
//		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0 , intent, PendingIntent.FLAG_ONE_SHOT);			//	;二重に開く	FLAG_CANCEL_CURRENT	FLAG_NO_CREATE	FLAG_UPDATE_CURRENT

		if(mediaSession != null){
			mediaSession.release();
			mediaSession = null;
		}
		mediaSession = new MediaSession(getApplicationContext(), "lpNotificationMake");			// Create a new MediaSession		this			debug tag
//https://sites.google.com/site/buildingappsfortv/displaying-a-now-playing-card
		MediaMetadata.Builder metadataBuilder = new MediaMetadata.Builder();		// To provide most control over how an item is displayed set the display fields in the metadata
		metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, keyTitle);
		metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, keyAlbum + " / " + keyArtist);
		metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI, albumArt);
		metadataBuilder.putString(MediaMetadata.METADATA_KEY_TITLE, keyTitle);		// And at minimum the title and artist for legacy support
		metadataBuilder.putString(MediaMetadata.METADATA_KEY_ALBUM, keyAlbum);
		metadataBuilder.putString(MediaMetadata.METADATA_KEY_ARTIST, keyArtist);
		metadataBuilder.putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, albumArt);		// A small bitmap for the artwork is also recommended
		metadataBuilder.putBitmap(MediaMetadata.METADATA_KEY_ART, artwork);		//これが無いとロックスクリーンの背景にならない？ A small bitmap for the artwork is also recommended
		mediaSession.setMetadata(metadataBuilder.build());		// Add any other fields you have for your data as well
		mediaSession.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);				// このフラグが有るカードだけが表示される	Indicate you want to receive transport controls via your Callback
		
		mediaSession.setCallback(new MediaSession.Callback() {									// Attach a new Callback to receive MediaSession updates
			@Override
			public void onPlay() {
				final String TAG = "onPlay[MusicPlayerService.MediaSession.Callback]";
				String dbMsg="開始";
				try{
					myLog(TAG,dbMsg);
				} catch (Exception e) {
					myErrorLog(TAG,dbMsg+"で"+e);
				}
			}

			@Override
			public void onPlayFromMediaId(String mediaId, Bundle extras) {			// リスト選択時にコールされる
				final String TAG = "onPlay[MusicPlayerService.MediaSession.Callback]";
				String dbMsg="開始";
				try{
					// MediaIDを元に曲のインデックス番号を検索し、設定する。
					myLog(TAG,dbMsg);
				} catch (Exception e) {
					myErrorLog(TAG,dbMsg+"で"+e);
				}
			}

			@Override
			public void onSkipToNext() {				// 再生キューの位置を次へ
				final String TAG = "onSkipToNext[MusicPlayerService.MediaSession.Callback]";
				String dbMsg="開始";
				try{
					myLog(TAG,dbMsg);
				} catch (Exception e) {
					myErrorLog(TAG,dbMsg+"で"+e);
				}
			}

			@Override
			public void onSkipToPrevious() {				// 再生キューの位置を前へ
				final String TAG = "onSkipToPrevious[MusicPlayerService.MediaSession.Callback]";
				String dbMsg="開始";
				try{
					myLog(TAG,dbMsg);
				} catch (Exception e) {
					myErrorLog(TAG,dbMsg+"で"+e);
				}
			}
		});
		tryToGetAudioFocus();
		dbMsg= dbMsg + ",isActive = " + mediaSession.isActive() ;
		if (!mediaSession.isActive()) {
			mediaSession.setActive(true);															//再生中カードとして表示; Indicate you're ready to receive media commands
		}
		dbMsg= dbMsg + ",mState = " + mState ;
		int ppIcon = android.R.drawable.ic_media_pause;				//getApplicationContext().getResources().getInteger(android.R.drawable.ic_media_pause);
		String ppTitol = "pause";
		if(mPlayer != null){
			dbMsg= dbMsg + ",isPlaying = " + mPlayer.isPlaying() ;
			if (! mPlayer.isPlaying()) {		//(mState == State.Paused || mState == State.Stopped
				ppIcon = android.R.drawable.ic_media_play;
				ppTitol = "play";
			}
		}
		dbMsg= dbMsg + ",ppTitol = " + ppTitol  ;
		dbMsg= dbMsg + ",ppIcon = " + ppIcon  ;
		if( lpNotification != null ){
			lpNotification = null;
		}
		lpNotification = new Notification.Builder(this)								// Create a new Notification				final Notification
			.setVisibility(Notification.VISIBILITY_PUBLIC)														//2016050:通知にメディア再生コントロールを表示	http://developer.android.com/intl/ja/about/versions/android-5.0.html アプリで RemoteControlClient を使用する場合
			.setShowWhen(false)																	// Hide the timestamp
			.setStyle(new Notification.MediaStyle()												// Set the Notification style☆RemoteViews.RemoteViewから変更					new Notification.MediaStyle()	
				.setMediaSession(mediaSession.getSessionToken())									// Attach our MediaSession token
				.setShowActionsInCompactView(0, 1, 2))												// Show our playback controls in the compat view
			.setColor(getResources().getColor(R.color.dark_gray))								// Set the Notification color		//
			.setLargeIcon(artwork)																// Set the large and small icons
			.setSmallIcon(R.drawable.no_image)													// Set Notification content information			R.drawable.no_image
			.setContentText(keyArtist)															//ここが実際に書き込まれる文字
			.setContentInfo(keyAlbum)
			.setContentTitle(keyTitle)
			.addAction( generateAction( android.R.drawable.ic_media_rew, "Rewind", ACTION_REWIND ))				//.addAction(android.R.drawable.ic_media_rew, "prev", retreivePlaybackAction(3))			// Add some playback controls		//retreivePlaybackAction(3)
			.addAction(generateAction(ppIcon , ppTitol , ACTION_PLAYPAUSE ))			//.addAction(android.R.drawable.ic_media_pause, "pause", retreivePlaybackAction(1))			//retreivePlaybackAction(1)
			.addAction( generateAction( android.R.drawable.ic_media_ff, "Next", ACTION_SKIP ))					//.addAction(android.R.drawable.ic_media_ff, "next", retreivePlaybackAction(2))				//retreivePlaybackAction()
			.addAction( generateAction( android.R.drawable.ic_lock_power_off, "Qite", ACTION_SYUURYOU_NOTIF ))		//ノティフィケーションから終了	ロックスクリーンでは4つ目のアイコンでsetLargeIconが表示されなくなる	☆終了不能に陥る?
			.setContentIntent(contentIntent)																		//タップで起動する画面
	//		.setAutoCancel(true)																					//タップで通知領域から削除する＞＞setContentIntentと合わせて削除せずにノティフィケーションだけを閉じさせる　＝　ノティフィケーションを閉じてアクテイビティを表示
			.build();
	//	lpNotification.flags = Notification.FLAG_ONGOING_EVENT;			//フリックで削除させない（削除されることを防ぐ）			//http://mashilo-blog.blogspot.jp/2015/11/notificationbuildernotificationsetlights.html
		lpNControls = mediaSession.getController().getTransportControls();			// Do something with your TransportControls			final TransportControls
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, lpNotification);
	//	myLog(TAG,dbMsg);
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}
//05-07 21:43:02.811: E/MediaPlayer(5999): Should have subtitle controller already set


private Notification.Action generateAction( int icon, String title, String intentAction ) {
	final String TAG = "generateAction[MusicPlayerService]";
	String dbMsg="開始";
//	PendingIntent pendingInten;
//	try{
	dbMsg="icon = " + icon;
	dbMsg= dbMsg + ",title = " + title;
	dbMsg= dbMsg + ",intentAction = " + intentAction;
	dbMsg= dbMsg + ",mState = " + mState ;
		Intent intent = new Intent( getApplicationContext(), MusicPlayerService.class );
		intent.setAction( intentAction );
	//	myLog(TAG,dbMsg);
		PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
//	} catch (Exception e) {
//		myErrorLog(TAG,dbMsg+"で"+e);
//	}
	return new Notification.Action.Builder( icon, title, pendingIntent ).build();
}

private PendingIntent retreivePlaybackAction(int which) {
	final String TAG = "retreivePlaybackAction[MusicPlayerService]";
	String dbMsg="開始";
	try{
//		Intent action;
//		PendingIntent pendingIntent = null;
		dbMsg="which = " + which;
		myLog(TAG,dbMsg);
//		final ComponentName serviceName = new ComponentName(this, MusicPlayerService.class);
		switch (which) {
		case 1:
//			action = new Intent(ACTION_TOGGLE_PLAYBACK);		// Play and pause
//			action.setComponent(serviceName);
//			pendingIntent = PendingIntent.getService(this, 1, action, 0);
//			return pendingIntent;
			break;
		case 2:
//			action = new Intent(ACTION_NEXT);		// Skip tracks
//			action.setComponent(serviceName);
//			pendingIntent = PendingIntent.getService(this, 2, action, 0);
//			return pendingIntent;
			break;
		case 3:
//			action = new Intent(ACTION_PREV);		// Previous tracks
//			action.setComponent(serviceName);
//			pendingIntent = PendingIntent.getService(this, 3, action, 0);
//			return pendingIntent;
			break;
		default:
			break;
		}
		myLog(TAG,dbMsg);
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
	return null;
}


/**	http://techbooster.jpn.org/andriod/ui/8843/																			Notificationを使ってステータスバーに情報を表示する / Getting Started
		http://techbooster.org/android/application/421/																	Notificationを使ってステータス通知する
		http://techbooster.org/android/ui/3208/																				ステータス通知(Notification)を変化させる
		http://ichitcltk.hustle.ne.jp/gudon2/index.php?pageType=file&id=Android025_service		サービスとNotification
		http://amyu.hatenadiary.com/entry/2014/09/24/101534				NotificationのRemoteViewsを使ったカスタマイズ (MediaPlayer)
		http://connvoi.hatenablog.com/entry/2013/05/26/030250
		 Android5;;  http://nextdeveloper.hatenablog.com/entry/2014/07/25/193748
		//http://dev.classmethod.jp/smartphone/android/android-tips-23-android4-1-notification-style/
		http://www.atmarkit.co.jp/ait/articles/1203/28/news128_3.html
		☆
ここは表示の作成と更新
*/
//	Intent intentN;													// Notification	から作成するIntent
//Intent intentNR ;								//ノティフィケーションレシーバ
//private PendingIntent pendingIntent;
private NotificationManager mNotificationManager;			//			NotificationManagerCompat		NotificationManager
private Notification mNotification = null;
final int NOTIFICATION_ID = 1;						//☆生成されないので任意の番号を設定する	 The ID we use for the notification (the onscreen alert that appears at the notification area at the top of the screen as an icon -- and as text as well if the user expands the notification area).
//	private NotificationCompat.Builder builder;
private RemoteViews ntfViews;						//ノティフィケーションのレイアウト
public void makeNotification() {					//ノティフィケーション作成			<createBody , updateNotification
	final String TAG = "makeNotification[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	//	http://android-note.open-memo.net/sub/system__custom_notification.html
	//	http://yoshihikomuto.hatenablog.jp/entry/20111124/1322106813
	//	http://triware.blogspot.jp/2012/10/notification-6.html
	//	https://github.com/mixi-inc/AndroidTraining/wiki/2.04.-%E3%83%A1%E3%83%83%E3%82%BB%E3%83%BC%E3%82%B8%E3%83%B3%E3%82%B0%E3%81%A8%E9%80%9A%E7%9F%A5//
//	http://qiita.com/Helmos/items/5260601711560d9bc862
//☆表示されない時は	設定￥アプリケーションでこのアプリを選び「通知を表示」にチェック
	try{
		dbMsg= ",SDK_INT=" + android.os.Build.VERSION.SDK_INT;/////////////////////////////////////
		dbMsg= dbMsg +",pref_notifplayer=" + pref_notifplayer;/////////////////////////////////////
		main_pref = getSharedPreferences( getResources().getString(R.string.pref_main_file) ,MODE_PRIVATE);		//	getSharedPreferences(prefFname,MODE_PRIVATE);
		Map<String, ?> keys = main_pref.getAll();
		pref_notifplayer = Boolean.valueOf( String.valueOf(keys.get("pref_notifplayer")));
		dbMsg= dbMsg +">>" + pref_notifplayer;/////////////////////////////////////
		if ( 21 <= android.os.Build.VERSION.SDK_INT && pref_notifplayer) {
		}else if ( 11 <= android.os.Build.VERSION.SDK_INT && pref_notifplayer) {
			dbMsg= dbMsg +",mNotification=" + mNotification;/////////////////////////////////////
			if( mNotification != null ){
				mNotificationManager.cancelAll();
			}
			mNotificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);	// NotificationManagerを取得		NotificationManagerCompat
			dbMsg= dbMsg +";" + mNotificationManager;/////////////////////////////////////
			ntfViews = new RemoteViews(this.getPackageName(), R.layout.remote_controll);		//ノティフィケーションのレイアウトをファイルからRemoteViewsを生成
			PendingIntent pendingIntent = PendingIntent.getActivity( getApplicationContext() , 0
					,new Intent(getApplicationContext() , MaraSonActivity.class), PendingIntent.FLAG_UPDATE_CURRENT );
					//既にPendingIntentオブジェクトを作成している場合、そのオブジェクトが持つ`Intent`オブジェクト自体は置き換えず、`
					//Intent`オブジェクトが持つ`Extras`のみを置き換えます。
			mNotification = new Notification();		// Notificationを生成・初期化
			mNotification.icon = R.drawable.play_notif;
			mNotification.contentView = ntfViews;
			mNotification.flags = Notification.FLAG_ONGOING_EVENT			////「通知」ではなく，「実行中 (OnGoing)」として通知する。
																									//これによって「通知を消去」ボタンから notification を消去できなくなる。。
	//								| Intent.FLAG_ACTIVITY_CLEAR_TOP
									| Notification.FLAG_NO_CLEAR;						// 音楽再生中は常駐するようにする
//加えてManifest ファイルの activity に android:launchMode="singleTask" を付与すると，
//ステータスバーの通知をタップした際に onPause → onResume と状態が遷移する。即ち，onCreate へ遷移しない。

			mNotification.contentIntent = pendingIntent;
	//		myLog(TAG,dbMsg);

				////////ボタンの処理	☆	extends Service 	のonStartCommandで処理							NotifRecever
				Intent intentNR = new Intent(MusicPlayerService.this, NotifRecever.class);
				ntfViews.setImageViewResource(R.id.stop, drawable.ic_lock_power_off);
				intentNR.setAction(ACTION_SYUURYOU_NOTIF);				//intentNR = new Intent(ACTION_STOP);
				PendingIntent piStop = PendingIntent.getService(this, 0, intentNR, PendingIntent.FLAG_UPDATE_CURRENT);
				ntfViews.setOnClickPendingIntent(R.id.stop, piStop);						//piStop

				ntfViews.setImageViewResource(R.id.ppPButton  , R.drawable.play_notif);
				intentNR.setAction(ACTION_PLAYPAUSE);					//intentNR = new Intent(ACTION_PLAY);
				PendingIntent piPlay = PendingIntent.getService(this, 0, intentNR, PendingIntent.FLAG_UPDATE_CURRENT);
				ntfViews.setOnClickPendingIntent(R.id.ppPButton, piPlay);
		///////////////////////////////////////////ボタンの処理///////////////////////////
			}
	//		myLog(TAG,dbMsg);
//			if( mPlayer != null ){
//				updateNotification(mPlayer);				//Updates the notification
//			}
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

public long currentTime = 0;
public boolean playing ;
//public String path;
public Bitmap notifAlbumArt;	// Dummy album art we will pass to the remote control (if the APIs are available).
public int playPauseRes;		//ノティフィケーションプレイヤーの再生/ポーズボタン
/**
 * Updates the notification.
 * 呼出し元	sendPlayerState ,
 * */
public void updateNotification( MediaPlayer player ) throws IllegalStateException{		//
	final String TAG = "updateNotification[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		dbMsg= ",SDK_INT=" + android.os.Build.VERSION.SDK_INT;/////////////////////////////////////
		dbMsg= dbMsg +",pref_notifplayer=" + pref_notifplayer;/////////////////////////////////////
		if( player != null ){
			if (android.os.Build.VERSION.SDK_INT >= 11 && pref_notifplayer) {
				dbMsg= dbMsg +",mNotification=" + mNotification;/////////////////////////////////////
		//		if( mNotification!= null ){
					makeNotification();					//ノティフィケーション作成			<createBody
		//		}
				dbMsg= "[" + mIndex +"]";/////////////////////////////////////
//				Item playingItem = MusicPlayerService.this.mItems.get(mIndex);
				dbMsg= dbMsg + ",isPlaying=" + player.isPlaying();/////////////////////////////////////
				if(player.isPlaying() ){
					mState = State.Playing;
				}
				dbMsg= dbMsg + ",mState=" + mState;/////////////////////////////////////
				playing = mState == State.Playing;
				dbMsg= dbMsg + ",playing=" + playing;/////////////////////////////////////
	//操作ボタンの切り替え/////////////////////////////////////////////////////////
				playPauseRes = playing ? R.drawable.pouse_notif : R.drawable.play_notif;			//操作ボタン	...509 / ...510
				dbMsg= dbMsg +",playPauseRes=" + playPauseRes;/////////////////////////////////////
				ntfViews.setImageViewResource(R.id.ppPButton  ,playPauseRes);
				mNotification.icon = playPauseRes;
	////////////////////////////////////////////////////////操作ボタンの切り替え///
				dbMsg= dbMsg +",mState=" + mState;/////////////////////////////////////
				dbMsg= dbMsg +",currentTime=" + currentTime;/////////////////////////////////////
				if (mState == State.Stopped) {
					currentTime = 0;
				} else {
					if( player != null ){
						dbMsg= dbMsg +";getCurrentPosition=" + player.getCurrentPosition();			// mPlayer.getCurrentPosition();/////////////////////////////////////
						currentTime = SystemClock.elapsedRealtime() - player.getCurrentPosition();			//mPlayer.getCurrentPosition();
						dbMsg= dbMsg +";current=" + currentTime;			// mPlayer.getCurrentPosition();/////////////////////////////////////
					}
				}
				dbMsg= dbMsg +">>" + currentTime;/////////////////////////////////////
				Resources r = getApplicationContext().getResources();
				String albumArt =MusicPlayerService.this.album_art;
				dbMsg +=",albumArt="+ albumArt;
				notifAlbumArt = ORGUT.retBitMap( albumArt  , 112 , 112 ,  getResources() );		//指定したURiのBitmapを返す	 , dHighet , dWith ,
				dbMsg= dbMsg + ",art=" + album_art ;/////////////////////////////////////リストの状態	起動直後；veiwPlayer / 再選択chyangeSong
				dbMsg=dbMsg + " , AlbumArt(ビットマップ) = " + notifAlbumArt;/////////////////////////////////////
		//		myLog(TAG,dbMsg);
				new Thread(new Runnable() {				//ワーカースレッドの生成☆ここで書換え
					public void run() {
						final String TAG = "Thread[updateNotification]";
						String dbMsg= "開始";/////////////////////////////////////
						try{
							try {
								Thread.sleep(10); // 1ms秒待つ
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							dbMsg= "thread id = " + Thread.currentThread().getId();
							dbMsg= dbMsg +";" +  MusicPlayerService.this.playingItem.artist;				//creditArtistName;/////////////////////////////////////
							ntfViews.setTextViewText(R.id.artist,  MusicPlayerService.this.playingItem.artist);;	//クレジットアーティスト名
							dbMsg= dbMsg +"(" + b_Album+ ">>"+ MusicPlayerService.this.playingItem.album;				// albumName;/////////////////////////////////////
							ntfViews.setTextViewText(R.id.album,  MusicPlayerService.this.playingItem.album);	//アルバム名
							dbMsg= dbMsg +";" +  MusicPlayerService.this.playingItem.title;				//titolName;/////////////////////////////////////
							ntfViews.setTextViewText(R.id.title,   MusicPlayerService.this.playingItem.title);		//曲名
							dbMsg= dbMsg +",currentTime=" + MusicPlayerService.this.currentTime +"mS";/////////////////////////////////////
							ntfViews.setChronometer(R.id.chronometer,  MusicPlayerService.this.currentTime , null, playing);
//操作ボタンの切り替え/////////////////////////////////////////////////////////
//							dbMsg= dbMsg +",playPauseRes=" + playPauseRes;/////////////////////////////////////
//							ntfViews.setImageViewResource(R.id.ppPButton  ,playPauseRes);
//							mNotification.icon = playPauseRes;
////////////////////////////////////////////////////////操作ボタンの切り替え///
							dbMsg= dbMsg +",notifAlbumArt=" + MusicPlayerService.this.notifAlbumArt;/////////////////////////////////////
							try{
								ntfViews.setImageViewBitmap(R.id.rc_Img  , MusicPlayerService.this.notifAlbumArt);
							} catch (IllegalStateException e) {
								myErrorLog(TAG,dbMsg+"で"+e);
							} catch (Exception e) {
								myErrorLog(TAG,dbMsg+"で"+e);
								throw new RuntimeException(e);
							}
							mNotificationManager.notify(NOTIFICATION_ID, mNotification);
			//				myLog(TAG,dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG,dbMsg+"で"+e);
						}
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
					}
				}).start();
			}
		}
	} catch (IllegalStateException e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

public RemoteControlClient mRemoteControlClient;	// our RemoteControlClient object, which will use remote control APIs available in SDK level >= 14, if they're available.
public Bitmap mDummyAlbumArt;	// Dummy album art we will pass to the remote control (if the APIs are available).
public Intent intentRS;					//ロックスクリーン用
public ComponentName mMediaButtonReceiverComponent;// The component name of MusicIntentReceiver, for use with media button and remote control APIs
/**
 * ロックスクリーン作成(mRemoteControlClientにボタンを設定する)
 * 呼出し元はcreateMediaPlayerIfNeededでMediaPlayer生成直後	( 元々はsongInfoSett)
 * */
@SuppressWarnings("deprecation")
public void makeLockScreenP( MediaPlayer player ) {					//ロックスクリーン作成 < songInfoSett
	final String TAG = "makeLockScreenP[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		dbMsg= ",SDK_INT=" + android.os.Build.VERSION.SDK_INT;/////////////////////////////////////
		main_pref = getSharedPreferences( getResources().getString(R.string.pref_main_file) ,MODE_PRIVATE);		//	getSharedPreferences(prefFname,MODE_PRIVATE);
		Map<String, ?> keys = main_pref.getAll();
		pref_lockscreen = Boolean.valueOf( String.valueOf(keys.get("pref_lockscreen")));
		dbMsg= dbMsg +",pref_lockscreen=" + pref_lockscreen;/////////////////////////////////////
	//	myLog(TAG,dbMsg);
		if (pref_lockscreen) {										// registerRemoteControlClient
			if (21 <= android.os.Build.VERSION.SDK_INT) {										// registerRemoteControlClient		 && android.os.Build.VERSION.SDK_INT <21
				MediaSession mSession =  new MediaSession(getApplicationContext(),getApplicationContext().getPackageName());
				intentRS = new Intent(Intent.ACTION_MEDIA_BUTTON);	//②③ RemoteControlClientの為にPendingIntentを作成する	Intent.ACTION_MEDIA_BUTTONをアクションに持つIntentを生成
				intentRS.setComponent(mMediaButtonReceiverComponent);	//②③ ComponentNameをIntentに設定
				PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intentRS, 0);	//	②③ create and register the remote control client
			}else if (14 <= android.os.Build.VERSION.SDK_INT) {										// registerRemoteControlClient		 && android.os.Build.VERSION.SDK_INT <21
				dbMsg= dbMsg +",player=" +player;/////////////////////////////////////
				if( player != null ){
					dbMsg= dbMsg +",mRemoteControlClient=" +mRemoteControlClient;/////////////////////////////////////
					if( mRemoteControlClient == null ){
						player.setWakeMode(getApplicationContext(),PowerManager.PARTIAL_WAKE_LOCK);					//①	 Intent.ACTION_MEDIA_BUTTONのブロードキャストを受け取る
///Org	http://www.atmarkit.co.jp/ait/articles/1203/28/news128_3.html///////////////////////////////////////////////////////
						intentRS = new Intent(Intent.ACTION_MEDIA_BUTTON);
///http://techbooster.org/android/ui/10298////////////////////////////////////////////////////////
						ComponentName myEventReceiver = new ComponentName(getPackageName(), MusicPlayerReceiver.class.getName());						// BroadcastReceiverのコンポーネント名を取得する
						mAudioManager.registerMediaButtonEventReceiver(myEventReceiver);			// BroadcastReceiverをシステムに登録する
						intentRS.setComponent(myEventReceiver);						///http://techbooster.org/android/ui/10298//
////////////////////////////////////////////////////http://techbooster.org/android/ui/10298///////

						PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intentRS, 0);	//	②③ create and register the remote control client
						mRemoteControlClient = new RemoteControlClient(mediaPendingIntent);				//②RemoteControlClientを生成し、PendingIntentを設定する
						if( mRemoteControlClient != null ){
							dbMsg= dbMsg + ">>" + mRemoteControlClient;/////////////////////////////////////
							mAudioManager.registerRemoteControlClient(mRemoteControlClient);										//② AudioManagerにRemoteControlClientを登録

							dbMsg= dbMsg + ",mAudioManager=" + mAudioManager.toString();/////////////////////////////////////
							mRemoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);					//ロックスクリーンの状態を再生に設定
							mRemoteControlClient.setTransportControlFlags(RemoteControlClient.FLAG_KEY_MEDIA_PLAY		// リモコン上で扱える操作を設定
								| RemoteControlClient.FLAG_KEY_MEDIA_PAUSE
								| RemoteControlClient.FLAG_KEY_MEDIA_NEXT
								| RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS
								| RemoteControlClient.FLAG_KEY_MEDIA_STOP);
						}
						////////////////////////////////////////////////////////Org///
					}
				}					//if( mPlayer != null ){
			}				//if (android.os.Build.VERSION.SDK_INT >= 14) {

			OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
				public void onAudioFocusChange(int focusChange) {
					final String TAG = "onAudioFocusChange[MusicPlayerService]";
					String dbMsg="開始";/////////////////////////////////////
					try{
						switch (focusChange) {
						case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
							dbMsg="AUDIOFOCUS_LOSS_TRANSIENT；再生を一時停止します。";
							break;
						case AudioManager.AUDIOFOCUS_GAIN:
							dbMsg="AUDIOFOCUS_GAIN；再生を再開します。";
							break;
						case AudioManager.AUDIOFOCUS_LOSS:
							dbMsg="AUDIOFOCUS_LOSS；再生を停止します。";
//							mAudioManager.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
//							mAudioManager.abandonAudioFocus(afChangeListener);
							break;
				  		}
		//				myLog(TAG,dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG,dbMsg+"で"+e);
					}
			    }
			};
			int result = mAudioManager.requestAudioFocus(afChangeListener,
					AudioManager.STREAM_MUSIC,								// Use the music stream.
					AudioManager.AUDIOFOCUS_GAIN);							// // 永続的なフォーカスを要求します		AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
			dbMsg= dbMsg + ",result=" + result;/////////////////////////////////////
			if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {				//1
//				AudioManager.unregisterMediaButtonEventReceiver(RemoteControlReceiver);		// Start playback.
				dbMsg= dbMsg+ ">>" + mRemoteControlClient;/////////////////////////////////////
				if( mRemoteControlClient != null ){
		//			myLog(TAG,dbMsg);
					updateLockScreenP();					//ロックスクリーン更新
				}
			}

		}
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
	/*ロック画面にオーディオリモコンを表示させる方法
	 * ①	http://www.atmarkit.co.jp/ait/articles/1203/28/news128_3.html		【2】ロック画面クライアントからの制御方法
		②	http://techbooster.org/android/ui/10298/
		③	http://wp.developapp.net/?p=3253
		④	http://stackoverflow.com/questions/22281616/set-lock-screen-background-in-android-like-spotify-do/22281858#22281858
	*/
}

/**
 * ロックスクリーン更新
 * 呼び出し元はmakeLockScreenP , sendPlayerStatem
 * */
public void updateLockScreenP() throws IllegalStateException {					//ロックスクリーン更新
	final String TAG = "updateLockScreenP[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		dbMsg= ",SDK_INT=" + android.os.Build.VERSION.SDK_INT;/////////////////////////////////////
		dbMsg= dbMsg +",pref_lockscreen=" + pref_lockscreen;/////////////////////////////////////
		dbMsg= dbMsg +",playingItem=" +playingItem;/////////////////////////////////////
		if( playingItem != null && pref_lockscreen ){
			dbMsg= dbMsg + ",mRemoteControlClient=" + mRemoteControlClient;/////////////////////////////////////
			if( mRemoteControlClient != null ){
				dbMsg= dbMsg + ",アーティスト=" + playingItem.artist;/////////////////////////////////////
				RemoteControlClient.MetadataEditor rcEditer = mRemoteControlClient.editMetadata(true)	;																		// リモコン上の曲情報を更新
				rcEditer.putString(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST, playingItem.artist);					//☆METADATA_KEY_ARTISTでは表示されなかった
				rcEditer.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, playingItem.album);
				rcEditer.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, playingItem.title);
				rcEditer.putLong(MediaMetadataRetriever.METADATA_KEY_DURATION, playingItem.duration);
				dbMsg= dbMsg + ",Art=" + mDummyAlbumArt;/////////////////////////////////////
				if(mDummyAlbumArt == null){
					dbMsg= dbMsg + ",dataFN=" + dataFN;/////////////////////////////////////
					MediaMetadataRetriever mmr = new MediaMetadataRetriever();
					mmr.setDataSource(dataFN);
					byte[] data = mmr.getEmbeddedPicture();
					if (data != null) {
						mDummyAlbumArt = BitmapFactory.decodeByteArray(data, 0, data.length);
					}
//				} else {
//					rcEditer.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, mDummyAlbumArt);
				}
				rcEditer.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, mDummyAlbumArt);
				rcEditer.apply();
				dbMsg= dbMsg + ",mRemoteControlClient=" + mRemoteControlClient;/////////////////////////////////////
				mAudioManager.registerRemoteControlClient(mRemoteControlClient);										//② AudioManagerにRemoteControlClientを登録
				dbMsg= dbMsg + ",mAudioManager=" + mAudioManager.toString();/////////////////////////////////////
			}
		}
	//	myLog(TAG,dbMsg);
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

/**
* Called when there's an error playing media. When this happens, the media player goes to the Error state. We warn the user about the error and reset the media player.
*/
public boolean onError(MediaPlayer mp, int what, int extra) {
	final String TAG = "onError[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		Toast.makeText(getApplicationContext(), "Media player error! Resetting.", Toast.LENGTH_SHORT).show();
		dbMsg="Media player error! Resetting.";/////////////////////////////////////
//		myErrorLog(TAG, "Error: what=" + String.valueOf(what) + ", extra=" + String.valueOf(extra));
		mState = State.Stopped;
		myLog(TAG,dbMsg);
		relaxResources(true);				//mPlayerの破棄
		giveUpAudioFocus();
		myLog(TAG,dbMsg);
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
	return true; // true indicates we handled the error
}

public void onGainedAudioFocus() {
	final String TAG = "onGainedAudioFocus[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		Toast.makeText(getApplicationContext(), "gained audio focus.", Toast.LENGTH_SHORT).show();
		mAudioFocus = AudioFocus.Focused;
		dbMsg="mState=" + mState;/////////////////////////////////////
		myLog(TAG,dbMsg);
		if (mState == State.Playing) {		// restart media player with new focus settings
			configAndStartMediaPlayer();
		}
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

public void onLostAudioFocus(boolean canDuck) {
	final String TAG = "onLostAudioFocus[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		dbMsg="canDuck= " +canDuck;/////////////////////////////////////
		Toast.makeText(getApplicationContext(), "lost audio focus." + (canDuck ? "can duck" : "no duck"), Toast.LENGTH_SHORT).show();
		mAudioFocus = canDuck ? AudioFocus.NoFocusCanDuck : AudioFocus.NoFocusNoDuck;
		dbMsg="mAudioFocus= " + mAudioFocus;/////////////////////////////////////
		myLog(TAG,dbMsg);
		if (mPlayer != null ) {		// start/restart/pause media player with new focus settings
			if (mPlayer.isPlaying()) {
				configAndStartMediaPlayer();
			}
		}
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

//////////////////////////////////////////////////////////////////////////////////////////////
AsyncTask<Context, Void, List<Item>> PMRTask;

@Override
public void onMusicRetrieverPrepared(List<Item> items) {													//出来上がっている曲リストの読み込み
	final String TAG = "onMusicRetrieverPrepared[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		//11-28 23:22:33.422: E/onMusicRetrieverPrepared[MusicPlayerService](7868): 開始でjava.lang.IndexOutOfBoundsException
		dbMsg=items.get(mIndex).artist + " ; " + items.get(mIndex).album + " ; " + items.get(mIndex).title;/////////////////////////////////////
		mState = State.Stopped;		// Done retrieving!
		mItems = items;
		dbMsg=dbMsg+ " ; mStartPlayingAfterRetrieve=" + mStartPlayingAfterRetrieve;/////////////////////////////////////
		if (mStartPlayingAfterRetrieve) {		// If the flag indicates we should start playing after retrieving, let's do that now.
			tryToGetAudioFocus();
			playNextSong(false);
		} else {
	//			myLog(TAG,dbMsg);
			sendPlayerState(mPlayer);
		}
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////
public int mItemNoIndex(String sNmae){					//アイテムリストからタイトルと照合
	final String TAG = "mItemNoIndex[MusicPlayerService]";
	int retInt = -1;
	String dbMsg= "開始";/////////////////////////////////////
	try{
		int i=0;
		for( Item rVal : mItems ){
			if(rVal.title.equals(sNmae)){
				return i;
			}
			i++;
		}
//		myLog(TAG,dbMsg);
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
	return retInt;
}

protected List<Item> dataReflesh(Context context) {					//リストの全タイトル読み込み
	final String TAG = "dataReflesh[MusicPlayerService]";
	String dbMsg= "開始;";/////////////////////////////////////
	try{
		createBody();
		rContext = context;
	//	MaraSonActivity MSA = new MaraSonActivity();
		mItems = Item.getItems( context);
		dbMsg= dbMsg + mItems.size() + "件";///////////////////////////////////
//		myLog(TAG,dbMsg);
	}catch (Exception e) {
		myErrorLog(TAG,dbMsg + "で"+e.toString());
	}
	return mItems;
}

//データ受信と送信/////////////////////////////////////////////////////////////////////
/**
 * 一曲分のデータ抽出して他のActvteyに渡す。
 *  呼出し元は	onStartCommand ACTION_PLAY_READ/ACTION_REQUEST_STATE/ACTION_KEIZOKU/ACTION_SYUURYOU_NOTIF
 * 				 , processPlayRequest , processPauseRequest , songInfoSett . onMusicRetrieverPrepared
 * */
@SuppressLint("InlinedApi")
private void sendPlayerState( MediaPlayer player ) {			//①?、②ⅲStop?	,	onStartCommand(ACTION_PLAY_READ,ACTION_REQUEST_STATE),
	//	<processPlayRequest,configAndStartMediaPlayer,playNextSong,onMusicRetrieverPrepared , processPauseRequest
	final String TAG = "sendPlayerState[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		dbMsg= "player=" +  player ;/////////////
	//	if( player != null){
			Intent intent = null;
			if (mItems != null) {
		//		dbMsg="[" + mItems + "]";/////////////////////////////////////
				dbMsg= dbMsg + ",操作指定=" +  action.toString() ;/////////////
				dbMsg= dbMsg +",送り戻し待ち曲数=" + frCount ;/////////////////////////
				dbMsg= dbMsg +",player=" + player ;/////////////////////////
//				if( (action.equals(ACTION_SKIP) || action.equals(ACTION_REWIND)) && frCount !=0){
//					myLog(TAG,dbMsg);
//					okuriMpdosi(frCount);		//送り戻しの実行
//				} else {
				intent = new Intent(ACTION_STATE_CHANGED);
				dbMsg= dbMsg + "[List_id=" +  nowList_id + "]";/////////////////////////////////////
				intent.putExtra("nowList_id",nowList_id);
				dbMsg= dbMsg + nowList;/////////////////////////////////////
				intent.putExtra("nowList",nowList);
				dbMsg= dbMsg + "[mIndex=" + mIndex +"/"+ mItems.size() +"]";/////////////////////////////////////
				if(mItems.size() == 0){
					mItems = new LinkedList<Item>();	//id"、ARTIST、ALBUM_ARTIST、ALBUM、TITLE、DURATION、DATAを読み込む
					mItems = Item.getItems(getApplicationContext());
					dbMsg= dbMsg + ">>"+ mItems.size() +"]";/////////////////////////////////////
				}
				intent.putExtra("mIndex", mIndex);
				dbMsg= dbMsg + ",URi=" +dataFN ;/////////////////////////////////////
				if( dataFN != null ){
					intent.putExtra("data", dataFN);
					int rInt = Item.getMPItem(dataFN);
					dbMsg= dbMsg + ",rInt=" +rInt ;////☆ここから参照できない？/////////////////////////////////
//					String bLyric = songLyric;
//					if( bLyric == null){
//						bLyric ="";
//					}
//					if(songLyric == null){
//						readLyric( dataFN );					//歌詞の読出し
//					}else{
//						boolean samelyric = songLyric.equals(bLyric);
//						dbMsg= dbMsg +",samelyric=" + samelyric;
//						if(! samelyric){
			//				readLyric( dataFN );					//歌詞の読出し
//						}
//					}
				}

				if(saiseiJikan > 0){
					dbMsg= dbMsg + "[" +saiseiJikan + "mS]";/////////////////////////////////////
					intent.putExtra("saiseiJikan", saiseiJikan);
				}
				dbMsg= dbMsg + " , mState=" + mState.toString();////////////////////////////ノティフィケーション送る
				intent.putExtra("state", mState.toString());
		//		IsPlaying  = false ;								//再生中か
				if( player != null){
					saiseiJikan = player.getDuration();
					if( player.getCurrentPosition() > 0){
						mcPosition = player.getCurrentPosition();
					}
					IsPlaying  = player.isPlaying() ;			//再生中か
					IsSeisei = true;
				} else {
					IsSeisei = false ;
					IsPlaying  = false ;								//再生中か
				}
				dbMsg= dbMsg + "[再生ポジション=" +  mcPosition + ",2点間リピート中" + rp_pp ;/////////////////////////////////////
				if( rp_pp ){			//2点間リピート中
					mcPosition = pp_start;			//リピート区間開始点
					dbMsg= dbMsg + ">>"+ mcPosition;/////////////////////////////////////
	//				player.seekTo(mcPosition);
				}
				intent.putExtra("mcPosition", mcPosition);
				intent.putExtra("currentPosition", mcPosition);
			//	saiseiJikan = (int) playingItem.duration;			//DURATION;継続;The duration of the audio file, in ms;Type: INTEGER (long)
				dbMsg= dbMsg  + "/" +  saiseiJikan + "mS]";/////////////////////////////////////
				dbMsg= dbMsg + ",生成中= " + IsSeisei;//////////////////////////////////
				intent.putExtra("IsSeisei", IsSeisei);
				dbMsg= dbMsg + ",再生中か= " + IsPlaying;//////////////////////////////////
				intent.putExtra("IsPlaying", IsPlaying);
				dbMsg= dbMsg +"、今の状態=" + imanoJyoutai ;/////////////////////////////////////リストの状態	起動直後；veiwPlayer / 再選択chyangeSong
				intent.putExtra("imanoJyoutai", imanoJyoutai);
				dbMsg= dbMsg + ",Bluetooth= " + stateBaseStr;//////////////////////////////////
				dbMsg= dbMsg + " ,今日は " + ruikeikyoku +"曲";/////////////////////////////////////
				intent.putExtra("ruikeikyoku", ruikeikyoku);
				ruikeiSTTime = ruikeiSTTime + ruikeikasannTime;				//	累積加算時間
				dbMsg= dbMsg  + ruikeiSTTime +"mS(追加分" + ruikeikasannTime +"mS)";/////////////////////////////////////
				intent.putExtra("ruikeiSTTime", ruikeiSTTime);
				intent.putExtra("stateBaseStr", stateBaseStr);
		//		myLog(TAG,dbMsg);
				sentakuCyuu = false;						//送り戻しリスト選択解除
				dbMsg= dbMsg + ",選択中=" + sentakuCyuu;/////////////////////////////////////
				dbMsg= dbMsg + ", album_art = " + album_art;/////////////////////////////////////
				Cursor cursor=null;
				dbMsg= dbMsg + ",creditArtistNameは " + creditArtistName;/////////////////////////////////////
				dbMsg= dbMsg + ",アルバムは " + b_Album +">>>"+ albumName;/////////////////////////////////////
				if( b_Album == null ){
					if( albumName != null ){
						b_Album = albumName;
					}
				}
				if(b_Album != null && albumName != null ){
					if(! b_Album.equals(albumName) ){		//前のアルバム
						album_art =null;
						Uri cUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
						String[] c_columns = null;		 		//③引数columnsには、検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
						String c_selection =  MediaStore.Audio.Albums.ARTIST +" LIKE ?  AND " + MediaStore.Audio.Albums.ALBUM +" = ?";
						String[] c_selectionArgs= { "%" + creditArtistName + "%" , albumName };   			//⑥引数groupByには、groupBy句を指定します。
						String c_orderBy= null;											//MediaStore.Audio.Albums.LAST_YEAR  ; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
						cursor = getContentResolver().query( cUri , c_columns , c_selection , c_selectionArgs, c_orderBy);
						dbMsg= dbMsg + "、 " +  cursor.getCount() +"件";/////////////////////////////////////
						if( cursor.moveToFirst() ){
							album_art = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
						}
						cursor.close();

						OrgUtil ORGUT = new OrgUtil();				//自作関数集
						WindowManager wm = (WindowManager)this.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
						Display disp = wm.getDefaultDisplay();
						int width = disp.getWidth();
						width = width*9/10;
						mDummyAlbumArt = ORGUT.retBitMap( album_art  , width , width ,  getResources() );		//指定したURiのBitmapを返す	 , dHighet , dWith ,
						b_Album = albumName;
						dbMsg= dbMsg + ",art=" + album_art ;/////////////////////////////////////リストの状態	起動直後；veiwPlayer / 再選択chyangeSong
						intent.putExtra("albumArt", album_art);
						dbMsg=dbMsg + " , AlbumArt(ビットマップ) = " + mDummyAlbumArt;/////////////////////////////////////
					}
				}
				if(mItems == null){
					mItems = new LinkedList<Item>();	//id"、ARTIST、ALBUM_ARTIST、ALBUM、TITLE、DURATION、DATAを読み込む
					mItems = Item.getItems( getApplicationContext() );
				}
				playingItem = mItems.get(mIndex);
				dbMsg= dbMsg + ",playingItem=" +playingItem ;/////////////////////////////////////
				if(playingItem != null){
					//		dataFN=playingItem.data;			//DATA;The data stream for the file ;Type: DATA STREAM
					//		dbMsg= dbMsg + ",URi=" +dataFN ;/////////////////////////////////////
					dbMsg= dbMsg + "[id=" + playingItem._id +"]";/////////////////////////////////////
					intent.putExtra("_id", playingItem._id);
					albumName = playingItem.album;
					titolName =  playingItem.title;
					intent.putExtra("titolName", titolName);
					saiseiJikan = (int) playingItem.duration;			//DURATION;継続;The duration of the audio file, in ms;Type: INTEGER (long)
					dbMsg= dbMsg  +  saiseiJikan + "mS]";/////////////////////////////////////
				}
				intent.putExtra("songLyric", songLyric);
				sendBroadcast(intent);					//APIL1
		//		}						//if( (action.equals(ACTION_SKIP) || action.equals(ACTION_REWIND)) && frCount >0){
			}							//if (mItems != null) {
		///	myLog(TAG,dbMsg);
	/*アーティストリピート	>playNextSong	>songInfoSett
 * onCompletNow=false,操作指定=LISTSEL,送り戻し待ち曲数=0,player=android.media.MediaPlayer@41f4ccd0[List_id=43408]
 * リピート再生[mIndex=0/238],URi=/storage/sdcard0/Music/The Beatles/Past Masters, Vol. 1 [2009 Stereo Remaster]/1-01 Love Me Do [Original Single Ver.wma,rInt=0[145960mS] , mState=Playing
 * [再生ポジション=92,2点間リピート中false/145960mS],生成中= true,再生中か= true、今の状態=203,Bluetooth= null ,今日は 0曲0mS(追加分0mS),選択中=false,
 * album_art = /storage/sdcard0/Android/data/com.android.providers.media/albumthumbs/1373795418804creditArtistNameは The Beatlesアルバムは Let It Be... Naked>>>Past Masters, Vol. 1 [2009 Stereo Remaster]、
 * 1件,art=/storage/sdcard0/Android/data/com.android.providers.media/albumthumbs/1440782793143 , AlbumArt(ビットマップ) = android.graphics.Bitmap@43e533a8,playingItem=com.hijiyam_koubou.marasongs.Item@43edc618[id=0]145960mS]
 *
 * 二点間初期
 * onCompletNow=false,操作指定=LISTSEL,送り戻し待ち曲数=0,player=android.media.MediaPlayer@43500cd8[List_id=43408]
 * リピート再生[mIndex=0/1],URi=/storage/sdcard0/Music/The Beatles/Let It Be... Naked/1-08 Don't Let Me Down.wma,rInt=0[198824mS] , mState=Playing
 * [再生ポジション=85,2点間リピート中true>>75884/198824mS],生成中= true,再生中か= true、今の状態=203,Bluetooth= null ,今日は 0曲612320mS(追加分0mS),選択中=false,
 *  album_art = /storage/sdcard0/Android/data/com.android.providers.media/albumthumbs/1373795418804creditArtistNameは The Beatlesアルバムは Let It Be... Naked>>>Let It Be... Naked,art=/storage/sdcard0/Android/data/com.android.providers.media/albumthumbs/1373795418804 ,
 *   AlbumArt(ビットマップ) = android.graphics.Bitmap@4411b550,playingItem=com.hijiyam_koubou.marasongs.Item@43cc4730[id=0]198824mS]

 * */
//			if(  b_tagudata == null){
//				readLyric( dataFN );
//				b_tagudata = dataFN;
//			}else if( ! b_tagudata.equals(dataFN)){
//				readLyric( dataFN );
//				b_tagudata = dataFN;
//			}
			if ( 21 <= android.os.Build.VERSION.SDK_INT) {
				lpNotificationMake(playingItem.artist , playingItem.album , playingItem.title , album_art);
			}else if ( 14 <= android.os.Build.VERSION.SDK_INT  && pref_notifplayer) {								//&&  android.os.Build.VERSION.SDK_INT < 21
				dbMsg= dbMsg + ",action=" + action ;///////////////////////////////////
				if(! action.equals(ACTION_SYUURYOU) && ! action.equals(ACTION_SYUURYOU_NOTIF)){
					updateNotification(player);				//Updates the notification
					updateLockScreenP();					//ロックスクリーン更新
				}
			}
			dbMsg= dbMsg + ",imanoJyoutai=" + imanoJyoutai ;///////////////////////////////////
	//	}
			wrightSaseiList( dataFN );
	//		myLog(TAG,dbMsg);
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

/**
 * 再生した曲をリストに追記する
 * @ String data 再生する曲のUrl
 * */
private void wrightSaseiList( String data ) {			//最近再生リストへの追記
	final String TAG = "wrightSaseiList[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		String audio_id = null;
		String listMei =String.valueOf(getResources().getString(R.string.playlist_namae_saikinsisei));			//最近再生
//		MuList ML = new MuList();
		int playlist_id =siteiListSakusi( listMei);				//指定された名称のリストを作成する
		dbMsg="[" + playlist_id + "]" + listMei ;
//		Cursor playList = ML.listUMU(listMei);
		Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
		String[] columns = null;			//{ idKey, nameKey };
		String c_selection0 = MediaStore.Audio.Playlists.NAME +" = ? ";
		String[] c_selectionArg= { String.valueOf(listMei) };		//⑥引数groupByには、groupBy句を指定します。
		String c_orderBy = null;			//MediaStore.Audio.Playlists.Members.PLAY_ORDER;
		Cursor playList = getApplicationContext().getContentResolver().query(uri, columns, c_selection0, c_selectionArg, c_orderBy );

		int poSetteiti = playList.getCount();
		dbMsg=dbMsg + ",現在" + poSetteiti + "件" ;
		playList.close();

		dbMsg= dbMsg+ ",追加する曲=" + data;
		Uri cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
		String[] c_columns = null;		 		//③引数columnsには、検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
		String c_selection =  MediaStore.Audio.Media.DATA +" = ? ";			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
		String[] c_selectionArgs= {String.valueOf(data)};   			//音楽と分類されるファイルだけを抽出する
		Cursor cursor = getContentResolver().query( cUri , c_columns , c_selection , c_selectionArgs  , c_orderBy);
		if(cursor.moveToFirst()){
			audio_id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
		}

		ContentValues contentvalues = new ContentValues();
		Uri kakikomiUri = null;
		contentvalues.put("play_order", poSetteiti);
			kakikomiUri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist_id);
			dbMsg= dbMsg+ ",kakikomiUri=" + kakikomiUri;
			dbMsg= dbMsg+ ",audio_id=" + audio_id;
			contentvalues.put("audio_id", Integer.valueOf(audio_id));
		Uri result_uri = getContentResolver().insert(kakikomiUri, contentvalues);				//追加
		dbMsg= dbMsg+ ",result_uri=" + result_uri;
		if(result_uri == null){					//NG
			dbMsg= dbMsg+ "失敗 add music : " + playlist_id + ", " + audio_id + ", is null";
		}else if(((int)ContentUris.parseId(result_uri)) == -1){					//NG
			dbMsg= dbMsg+ "失敗 add music : " + playlist_id + ", " + audio_id + ", " + result_uri.toString();
		}else{					//OK
			dbMsg= dbMsg+  ">>成功list_id=" + playlist_id + ", audio_id=" + audio_id + ",result_uri= " + result_uri.toString();
		}
	//	myLog(TAG,dbMsg);
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

public int siteiListSakusi( String listName){				//指定された名称のリストを作成する
	int listID = 0;
	final String TAG = "siteiListSakusi[MusicPlayerService]";
	String dbMsg= "指定された名称のリストを作成する" ;/////////////////////////////////////
	try{
		 dbMsg= "指定された名称=" + listName;
		Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
		String[] columns = null;			//{ idKey, nameKey };
		String c_selection = MediaStore.Audio.Playlists.NAME +" = ? ";
		String[] c_selectionArg= { String.valueOf(listName) };		//⑥引数groupByには、groupBy句を指定します。
		String c_orderBy = null;			//MediaStore.Audio.Playlists.Members.PLAY_ORDER;
		Cursor cursor = getApplicationContext().getContentResolver().query(uri, columns, c_selection, c_selectionArg, c_orderBy );
		dbMsg=dbMsg +  "," + cursor.getCount() + "件既存";
		if(cursor.moveToFirst()){
			listID = Integer.valueOf(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID)));
			dbMsg= dbMsg + ",[" + listID + "]";			//[42529]content://media/external/audio/playlists/42529
		} else{
			Uri result_uri = addPlaylist(listName, null, null);		//プレイリストを新規作成する
			dbMsg= dbMsg + ",result_uri=" + result_uri;			//fastItemeFn=/storage/sdcard0/Music/Jimmy Cliff/Follow My Mind/07 Remake The World.wma
			listID = (int)ContentUris.parseId(result_uri);
			dbMsg= dbMsg + ","  + listID +"を作成";			//[42529]content://media/external/audio/playlists/42529
		}
		cursor.close();
	//	myLog(TAG,dbMsg);
	}catch (Exception e) {
		myErrorLog(TAG,dbMsg +"で"+e.toString());
	}
	return listID;
}

public Uri addPlaylist(String listName, Uri images_uri, String thumb){			//プレイリストを新規作成する
	Uri result_uri = null;
	final String TAG = "addPlaylist[MusicPlayerService]";
	String dbMsg= "プレイリストを新規作成する" ;/////////////////////////////////////
	try{
		dbMsg= "新規リスト名=" + listName;
		ContentValues contentvalues = null;
		ContentResolver contentResolver = getContentResolver();
		Uri playlist_uri = null;
		int playlist_id = -1;

		contentvalues = new ContentValues();
		contentvalues.put("name", listName);
		playlist_uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;			//URI
		result_uri = contentResolver.insert(playlist_uri, contentvalues);		//追加
		dbMsg= dbMsg + ",result_uri=" + result_uri;	//result_uri=content://media/external/audio/playlists/42529
		if(result_uri == null){			//NG
			dbMsg= dbMsg+">>失敗 add playlist : " + listName + ", is null";
		}else if((playlist_id = (int)ContentUris.parseId(result_uri)) == -1){			//NG
			dbMsg= dbMsg+ ">>失敗 add playlist : " + listName + ", " + result_uri.toString();
		}else{			//OK
			dbMsg= dbMsg+ ">>成功 listName＝ " + listName + ",playlist_id=" + playlist_id;
			//add playlist : プレイリスト2015-12-03 14:16:37,42529
		}
		myLog(TAG,dbMsg);
	}catch (Exception e) {
		myErrorLog(TAG,dbMsg +"で"+e.toString());
	}
	return result_uri;
}

//////
/**
 *クライアントからデータを受け取りグローバル変数にセット
 * 独自メソッド
 * 	呼出し元	onStartCommandで
 * */
public void dataUketori(Intent intent) {	//クライアントからデータを受け取りグローバル変数にセット
	final String TAG = "dataUketori[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		b_dataFN="";
		String b_list = nowList;
		int rInt = 0;
		dbMsg= "intent="+ intent;						//Intent { cmp=com.sen/.PlayerService } ,startId:1
		dbMsg= dbMsg +"getActio=:"+ intent.getAction();						//Intent { cmp=com.sen/.PlayerService } ,startId:1
		if( intent != null ){
			Bundle extras = intent.getExtras();
			dbMsg= dbMsg +".extras="+ extras;						//Intent { cmp=com.sen/.PlayerService } ,startId:1
			if (extras == null) {
				dbMsg= dbMsg +"他にデータ無し; ";
	//			myLog(TAG,dbMsg);
			}else{
				if (mPlayer != null) {
					b_dataFN =dataFN;			//すでに再生している再生ファイル
				}
				dbMsg= "現在[" + nowList_id+"}"+ b_list ;
			//	if( ! nowList.equals(getResources().getString(R.string.playlist_namae_request))){			//リクエスト実行中でなければ
					int b_List_id = nowList_id;
					rInt = extras.getInt("nowList_id");			//再生中のプレイリストID
					if(0 != rInt){
						nowList_id = rInt;
					}
					dbMsg= dbMsg+"→"+ nowList_id ;
					if(b_List_id != nowList_id){
						mItems = new LinkedList<Item>();	//id"、ARTIST、ALBUM_ARTIST、ALBUM、TITLE、DURATION、DATAを読み込む
						mItems = Item.getItems( getApplicationContext() );
					}
					String rStr = extras.getString("nowList");			//再生中のプレイリスト
					dbMsg= dbMsg+",rStr="+ rStr ;
					if( rStr.equals("null") ){
						rStr = null;
					}
					if(rStr == null){
						setteriYomikomi();
					}else {
						nowList = rStr;
					}
					dbMsg= dbMsg+"]"+ nowList ;			// = extras.getInt("mIndex");		//現リスト中の順番;
					dataFN = extras.getString("dataFN");			//音楽ファイルのurl
					dbMsg= dbMsg+",dataFN="+ dataFN ;			// = extras.getInt("mIndex");		//現リスト中の順番;
					int index  =itemUmu(nowList_id , dataFN);	//指定されたリストの中に指定した曲が有るか
					dbMsg= dbMsg+">itemUmu>index="+ index ;			// = extras.getInt("mIndex");		//現リスト中の順番;
					rInt = extras.getInt("mIndex");
					dbMsg= dbMsg+"[mIndex;"+ rInt ;
//					if(rInt != index){
						mItems = new LinkedList<Item>();	//id"、ARTIST、ALBUM_ARTIST、ALBUM、TITLE、DURATION、DATAを読み込む
						mItems = Item.getItems( getApplicationContext() );
						if(index == -1){
							mIndex = 0;
							dataFN = mItems.get(0).data;
							dbMsg= dbMsg+",dataFN="+ dataFN ;
							mainEditor.putString( "pref_saisei_fname", String.valueOf(dataFN));		//再生中のファイル名
						}else{
							mIndex = index;
						}
////						nowList = getResources().getString(R.string.listmei_zemkyoku);
////						mainEditor.putString( "nowList", String.valueOf(nowList));
////						mainEditor.putString( "nowList_id", String.valueOf(0));		//☆intで書き込むとcannot be cast
////						mainEditor.putString( "nowList_data", null);	//再生中のプレイリストの保存場所
////						boolean kakikomi = mainEditor.commit();
////						dbMsg= dbMsg+",書き込み=" + kakikomi;	////////////////
//					}else{
//						mIndex = rInt;
//					}
//						mIndex =itemUmu(nowList_id ,nowList , dataFN);	//指定されたリストの中に指定した曲が有るか
//						dbMsg= dbMsg +">itemUmu>[" + mIndex + "/" + mItems.size() + "]";///////////////////////////////////
						dbMsg= dbMsg +"["+ mIndex + "/" + mItems.size() +"]";			// = extras.getInt("mIndex");		//現リスト中の順番;
						playingItem = mItems.get(mIndex);							//☆1始まりのIdを0始まりのインデックスに	再生中の楽曲レコード
//							dbMsg=items.get(mIndex).artist + " ; " + items.get(mIndex).album + " ; " + items.get(mIndex).title;/////////////////////////////////////
						creditArtistName = playingItem.artist;					//extras.getString("artist");		//アーティスト名
						dbMsg= dbMsg +"creditArtistName = "+ creditArtistName;
						album_artist = playingItem.album_artist;						//extras.getString("album_artist");		//リストアップしたアルバムアーティスト名
						dbMsg= dbMsg +"("+ album_artist +")";
						albumName = playingItem.album;					//extras.getString("albumName");		//アルバム名
						dbMsg= dbMsg +" / "+ albumName;
						titolName = playingItem.title;								//extras.getString("titolName");		//曲名
						dbMsg= dbMsg +" / "+ titolName;
			//	}
				if( ! b_list.equals(getResources().getString(R.string.playlist_namae_request)) &&
						nowList.equals(getResources().getString(R.string.playlist_namae_request))
						){			//リクエストに切り替わった直後
							siseizumiDataFN = null;
				}

				rp_pp = extras.getBoolean("rp_pp");						//2点間リピート中
				dbMsg= dbMsg+",rp_pp="+ rp_pp ;			// = extras.getInt("mIndex");		//現リスト中の順番;
				pp_start = extras.getInt("pp_start");				//リピート区間開始点
				dbMsg= dbMsg+";"+ pp_start ;			// = extras.getInt("mIndex");		//現リスト中の順番;
				pp_end = extras.getInt("pp_end");					//リピート区間終了点
				dbMsg= dbMsg+"～"+ pp_end ;			// = extras.getInt("mIndex");		//現リスト中の順番;

	////		}
//				pref_lockscreen = extras.getBoolean("pref_lockscreen") ;				//ロックスクリーンプレイヤー
//				dbMsg= dbMsg +"、ロックスクリーンプレイヤー＝ "+ pref_lockscreen;
//				pref_notifplayer = extras.getBoolean("pref_notifplayer") ;					//ノティフィケーションプレイヤー</string>
//				dbMsg= dbMsg +"、ノティフィケーションプレイヤー＝ "+ pref_notifplayer;
				mcPosition = extras.getInt("mcPosition");		//現在の再生ポジション☆生成時は最初から ☆seekバー操作のために情事受け取り
				dbMsg= dbMsg +" [Position="+ mcPosition;
				saiseiJikan = extras.getInt("saiseiJikan");		//DURATION;継続;The duration of the audio file, in ms;Type: INTEGER (long)
				dbMsg= dbMsg +"/"+ saiseiJikan +"mS]";
				if(mcPosition > saiseiJikan){
					mcPosition = saiseiJikan;
					dbMsg= dbMsg +">>"+ mcPosition;
				}
			}
		}
//		myLog(TAG,dbMsg);
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg +"で"+e.toString());
	}
}

	/**
	 * 指定されたリストの中に指定した曲が有ればPLAY_ORDERを返す
	 * 　無ければ-1
	 * @param playlistId プレイリストのID
	 * @param listName レイリストの名前
	 * @param dataURL 検索する音楽データのフルパス名
	 */

	public int itemUmu(int playlistId , String dataURL) {	//指定されたリストの中に指定した曲が有るか		 ,String listName
		int retInt = -1;
		final String TAG = "itemUmu[MusicPlayerService]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			Cursor cursor = null;
			dbMsg=dbMsg + "[listId="+playlistId +"の中に" + dataURL +"を確認";
			if(0 < playlistId){
				ContentResolver resolver = this.getContentResolver();
				Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
				String[] columns = null;				//{ idKey, nameKey };
				String c_selection = MediaStore.Audio.Playlists._ID + " = ?";
				String[] c_selectionArgs= { String.valueOf(playlistId) };   			//String.valueOf(dataURL)
				Cursor playLists = this.getContentResolver().query(uri, columns, c_selection, c_selectionArgs, null);
				if(playLists.moveToFirst()){
					dbMsg=dbMsg + ">>指定リスト有り";
					String ieKubunn = "internal";
	////			File vFile = new File(volumeName);
					//	String extV = Environment.getExternalStorageDirectory().toString() ;
					//	dbMsg +=  ",extV=" + extV ;
					//	if(volumeName.contains(extV)){
							ieKubunn = "external";
					//	}
				//	dbMsg=dbMsg + ",内外区分="+ieKubunn;
					uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
					dbMsg=dbMsg + ",uri="+uri.toString();
					c_selection = MediaStore.Audio.Playlists.Members.DATA + " = ?";
					String[] c_selectionArgs2= { dataURL };   			//String.valueOf(dataURL)
					String c_orderBy = MediaStore.Audio.Playlists.Members._ID;	//MediaStore.Audio.Playlists.Members.PLAY_ORDER;
					cursor = this.getContentResolver().query(uri, columns, c_selection, c_selectionArgs2, c_orderBy );
					dbMsg=dbMsg + "に"+cursor.getCount() +"件";
					if( cursor.moveToFirst() ){
						retInt = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.PLAY_ORDER));
					}
				}else {
					dbMsg=dbMsg + "指定リスト無し";
					nowList = getResources().getString(R.string.listmei_zemkyoku);		// 全曲リスト
					mainEditor.putString( "nowList", String.valueOf(nowList));
					nowList_id = -1;
					mainEditor.putString( "nowList_id", String.valueOf(nowList_id));
					nowList_data = null;
					mainEditor.putString( "nowList_data", nowList_data);
					boolean kakikae = mainEditor.commit();	// データの保存
					dbMsg= dbMsg +"、書き換え=" + kakikae;/////////////////////////////////////
					retInt =itemUmuZenkyoku( dataURL);
				}
				playLists.close();
			}else{
				retInt =itemUmuZenkyoku( dataURL);
			}
			dbMsg=dbMsg + ",retInt="+retInt;
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return retInt;
	}

	public int itemUmuZenkyoku( String dataURL) {	//全曲リストの中に指定した曲が有るか		 ,String listName
		int retInt = -1;
		final String TAG = "itemUmuZenkyoku[MusicPlayerService]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			String fn = getString(R.string.zenkyoku_file);			//全曲リスト名
			ZenkyokuHelper zenkyokuHelper = null;				//全曲リストヘルパー
			SQLiteDatabase Zenkyoku_db;		//全曲リストファイル
			//		dbMsg += ",fn=" + fn;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
			zenkyokuHelper = new ZenkyokuHelper(getApplicationContext() , fn);		//全曲リストの定義ファイル		.MuList.this
			Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
			if( ! Zenkyoku_db.isOpen()){
				Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
		//		dbMsg += ">isOpen>" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
			}
			dbMsg += ",getPageSize=" + Zenkyoku_db.getPageSize() + "件中、" + dataURL + "は";			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
			String zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
			String[] c_columns =null;					//②引数tableには、テーブル名を指定します。
			String c_selection = "DATA = ?";			//= "ALBUM_ARTIST LIKE ? AND ALBUM = ?";
			String[] c_selectionArgs= { dataURL };   			// {"%" + artistMei + "%" , albumMei };
			String c_orderBy= null; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC		MediaStore.Audio.Media.TRACK
			Cursor cursor = Zenkyoku_db.query(zenkyokuTName, c_columns, c_selection, c_selectionArgs , null, null, c_orderBy);	// table, columns,new String[] {MotoN, albamN}
			dbMsg += ",getCount=" + cursor.getCount() + "件、" ;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
			if( cursor.moveToFirst() ){
				retInt = cursor.getInt(cursor.getColumnIndex("_id"))-1;
			}
			cursor.close();
			dbMsg=dbMsg + ",retInt="+retInt;
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return retInt;
	}

/**
 * プリファレンス記載
 * 呼出し元は		processPauseRequest/processStopRequest/songInfoSett/phoneCallEventでCALL_STATE_RINGING
 * */
public void setPref() {			//プリファレンス記載
	final String TAG = "setPref[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		new Thread(new Runnable() {				//ワーカースレッドの生成
			public void run() {
				String dbMsg= "thread id = " + Thread.currentThread().getId();/////////////////////////////////////
				try {
					dbMsg += ",dataFN="+dataFN;
					if(dataFN != null){
						main_pref = getSharedPreferences( getResources().getString(R.string.pref_main_file) ,MODE_PRIVATE);		//MODE_WORLD_WRITEABLE 	getSharedPreferences(prefFname,MODE_PRIVATE);
						mainEditor = main_pref.edit();
						mainEditor.putString("nowList_id",String.valueOf(nowList_id));		//再生中のプレイリストID
						mainEditor.putString("nowList",nowList);							//再生中のプレイリスト名
						mainEditor.putString("mIndex",String.valueOf( mIndex ));		//play_order
						mainEditor.putString("pref_saisei_fname",dataFN);				//再生していた曲	.commit()
//						if(mPlayer !=  null){
//							mcPosition = mPlayer.getCurrentPosition();
							dbMsg += "["+mcPosition;////////////////////////////////////////////////////////////////////////////
							if(mcPosition > 0){
								mainEditor.putString( "pref_saisei_jikan", String.valueOf(mcPosition));		//再生ポジション
							}
							dbMsg += "["+ORGUT.sdf_mss.format(mcPosition) + "/";////////////////////////////////////////////////////////////////////////////
							dbMsg +=";"+ORGUT.sdf_mss.format(saiseiJikan) + "]";////////////////////////////////////////////////////////////////////////////
//						}
						mainEditor.putString( "pref_saisei_nagasa", String.valueOf(saiseiJikan));					//再生時間
						dbMsg +=";"+ruikeikyoku + "曲"+ruikeiSTTime+"mS" ;////////////////////////////////////////////////////////////////////////////
						if( ruikeikyoku > 0 ){
							mainEditor.putString( "pref_zenkai_saiseKyoku", String.valueOf(ruikeikyoku));			//連続再生曲数
							mainEditor.putString( "pref_zenkai_saiseijikann", String.valueOf(ruikeiSTTime));		//連続再生時間
						}
						Boolean kakikomi = mainEditor.commit();	// データの保存
						dbMsg +=",書き込み成功="+kakikomi;////////////////////////////////////////////////////////////////////////////
					}
	//				myLog(TAG,dbMsg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}
//通信//////////////////////////////////////////////////////////////////////////
static class btHandler extends Handler{
	public void handleMessage(Message msg) {
		final String TAG = "handleMessage[MusicPlayerService]";
		String dbMsg="開始";/////////////////////////////////////
		try {
			Intent broadcastIntent = new Intent();
			dbMsg="message" + msg.toString() ;/////////////////////////////////////
			broadcastIntent.putExtra("message", msg);
			String actionName = ACTION_BLUETOOTH_INFO;
			broadcastIntent.setAction(actionName);
	//		rContext.sendBroadcast(broadcastIntent);
		//	sendEmptyMessageDelayed(1, 1000);
			myLog(TAG , dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}

		}
	}

public void setBTinfo( String stateBaseStr){					//Bluettoth情報更新
	final String TAG = "setBTinfo[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		dbMsg= dbMsg + ",Buletooth=" +stateBaseStr;
		this.stateBaseStr = stateBaseStr;
		Intent intent = new Intent(ACTION_STATE_CHANGED);
		intent.putExtra("stateBaseStr", stateBaseStr);
//		sendBroadcast(intent);					//APIL1
//		} else {
//			shigot_bangou = btInfo_kousin;
//		}
		myLog(TAG , dbMsg);
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

////////////////////////////////http://techbooster.jpn.org/tag/mediaplayer/////////////////////////
public boolean equalizerSet = false;					//バスブート
//public AudioManager am;
public Equalizer mEqualizer;
public BassBoost mBassBoost = null;
public PresetReverb mPresetReverb = null;
public Visualizer mVisualizer;
public List<String> pref_toneList;		//プリファレンス保存用トーンリスト
public boolean bBoot = false;					//バスブート
public String toneSeparata = "L";
public Map<String, Object> objMap;				//汎用マップ
public List<Map<String, Object>> toneList;		//プレイリスト名用リスト
/**
 * 初期Equalizer情報の取得
 * */
public void getEqualizer( ){					//初期Equalizer情報の取得
	final String TAG = "getEqualizer[MusicPlayerService]";
	String dbMsg="開始";		//http://www110.kir.jp/Android/ch0709.html
	try{
		dbMsg= dbMsg + ",pref_toneList=" + pref_toneList;
		if( pref_toneList == null ){
			if( 0 == pref_toneList.size() ){
				pref_toneList =  new ArrayList<String>();
				pref_toneList.add(14000 + toneSeparata+ 0);
				pref_toneList.add(3600 + toneSeparata+ 0);
				pref_toneList.add(910 + toneSeparata+ 0);
				pref_toneList.add(230 + toneSeparata+ 0);
				pref_toneList.add(60 + toneSeparata+ 0);
			}
		}
		int bands = pref_toneList.size();
		dbMsg= dbMsg + ",bands=" + bands + "件";
		toneList = new ArrayList<Map<String, Object>>();
		for (int i = bands; 0 < i ; i--) {
			dbMsg= dbMsg + "(" + i + "/" + bands + ")";
			String rStr = pref_toneList.get(i);
			dbMsg= dbMsg + ",rStr=" + rStr;
			String[] rAttay = rStr.split(toneSeparata);
			int freq = Integer.valueOf(rAttay[0]);						//mEqualizer.getCenterFreq((short) i) / 1000;					// イコライザの周波数帯の値を取得
			dbMsg= dbMsg + ",freq=" + String.format("%6dHz", freq);
			short band =  Short.valueOf(rAttay[1]);						//mEqualizer.getBandLevel((short) i);				// 現在のイコライザのバンドの値を取得
			dbMsg= dbMsg + ",band=" + String.format("%6d", band);
			String eStr = freq + toneSeparata+ band;
			objMap = new HashMap<String, Object>();				//汎用マップ
			objMap.put("tone_Hz" ,freq );													//高域調整周波数
			objMap.put("tone_Lev" ,band );													//高域調整レベル
			toneList.add( objMap);
		}			//for (int i = 0; i < bands; i++) {
		dbMsg= dbMsg + ",toneList=" + toneList;
		if( 0 < pref_toneList.size() ){
			main_pref = getSharedPreferences( getResources().getString(R.string.pref_main_file) ,MODE_WORLD_WRITEABLE);		//MODE_WORLD_WRITEABLE 	getSharedPreferences(prefFname,MODE_PRIVATE);
			mainEditor = main_pref.edit();
			mainEditor.putString("tone_name",getResources().getString(R.string.tone_name_puri));			//現在の設定
			mainEditor.putString("pref_toneList", pref_toneList.toString());							//再生中のプレイリストID
			Boolean kakikomi = mainEditor.commit();	// データの保存
			dbMsg +=",書き込み成功="+kakikomi;////////////////////////////////////////////////////////////////////////////
		}
		myLog(TAG,dbMsg);
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
	//return mEqualizer;
}

/**
 * Equalizer情報の更新
 * createMediaPlayerIfNeedでmPlayer = new MediaPlayer();の後に呼出し
 * */
public void setEqualizer( ){					//Equalizer情報の更新
	final String TAG = "setEqualizer[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		dbMsg= dbMsg + ",mPlayer=" + mPlayer;
	//	pref_toneList =  new ArrayList<String>();
		if( pref_toneList != null ){
			dbMsg= dbMsg + ",pref_toneList=" + pref_toneList.size() + "件";
			if( 0 == pref_toneList.size() ){
//				String tone_name = getResources().getString(R.string.comon_genzai);						//name="">現在の設定</string>
				dbMsg= dbMsg + ",getAudioSessionId=" + mPlayer.getAudioSessionId();
				if( mEqualizer != null ){
					mEqualizer.release();
					mEqualizer = null;
				}
				mEqualizer = new Equalizer(0, mPlayer.getAudioSessionId());		// Eaulizerを生成
				dbMsg= dbMsg + ",mEqualizer=" + mEqualizer;
		//		dbMsg= dbMsg + ",getId=" + mEqualizer.getId();
				short bands = mEqualizer.getNumberOfBands();			// イコライザのバンド数
				short minEQLevel = mEqualizer.getBandLevelRange()[0];			 // イコライザのバンドの最小値
				dbMsg= dbMsg + ",EQLevel=" + minEQLevel;
				short maxEQLevel = mEqualizer.getBandLevelRange()[1];
				dbMsg= dbMsg + "～" + maxEQLevel;
				toneList = new ArrayList<Map<String, Object>>();
				for (int i = bands-1; -1 < i ; i--) {
					dbMsg= dbMsg + "(" + i + "/" + bands + ")";
					int freq = mEqualizer.getCenterFreq((short) i) / 1000;					// イコライザの周波数帯の値を取得
					dbMsg= dbMsg + ",freq=" + String.format("%6dHz", freq);
					short band = mEqualizer.getBandLevel((short) i);				// 現在のイコライザのバンドの値を取得
					dbMsg= dbMsg + ",band=" + band;
					equalizerPartKousinBody( i , freq , band );					//Equalizerの部分更新本体
				}			//for (int i = 0; i < bands; i++) {
			}
		}
		Thread.sleep(500);
		equalizerSet = true;
		myLog(TAG,dbMsg);
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

	public void equalizerPartKousin(Intent intent ){					//Equalizerの部分更新
		final String TAG = "equalizerPartKousin[MusicPlayerService]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			Bundle extras = intent.getExtras();
			int rdIndex = extras.getInt("rdIndex");				//更新するインデックス
			dbMsg="(" + rdIndex ;
			int freq = extras.getInt("tone_Hz");				//更新する周波数
			dbMsg= dbMsg + ";" + freq + "Hz)";
			int band = extras.getInt("tone_Lev");				//更新する調整レベル
			dbMsg= dbMsg  + band;
			equalizerPartKousinBody( rdIndex , freq , band );					//Equalizerの部分更新本体
	//		Thread.sleep(500);
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	public void equalizerPartKousinBody(int rdIndex , int freq ,int band ) throws UnsupportedOperationException{					//Equalizerの部分更新本体
		final String TAG = "equalizerPartKousinBody[MusicPlayerService]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg="(" + rdIndex ;
			dbMsg= dbMsg + ";" + freq + "Hz)";
			dbMsg= dbMsg  + band;
	//02-16 17:22:12.578: E/equalizerPartKousinBody[MusicPlayerService](15613): (0;60Hz)2でjava.lang.NullPointerException
			dbMsg= dbMsg + ",mPlayer=" + mPlayer;
			if(mPlayer != null){
				dbMsg= dbMsg + ",mEqualizer=" + mEqualizer;
				if(mEqualizer != null){
					mEqualizer.release();
					mEqualizer =null;
					dbMsg= dbMsg + ">>" + mEqualizer;
				}
				mEqualizer = new Equalizer(0, mPlayer.getAudioSessionId());		// Eaulizerを生成
				dbMsg= dbMsg + ",mEqualizer=" + mEqualizer.getCenterFreq((short) rdIndex) / 1000 + "Hz";
				dbMsg= dbMsg + "," + mEqualizer.getBandLevel((short) rdIndex) + "db" ;
				 try {
					mEqualizer.setBandLevel(Short.valueOf(String.valueOf(rdIndex)), Short.valueOf(String.valueOf(band)) );							// イコライザにバンドの値を設定
					mEqualizer.setEnabled(true);
				} catch(Exception e) {
					myErrorLog(TAG, "Create second effect: wait was interrupted.");
//				} finally {
					mEqualizer.release();
			//		terminateListenerLooper();
				}
				dbMsg= dbMsg + ">>" + mEqualizer.getBandLevel((short) rdIndex) + "db" ;
			}
	//		myLog(TAG,dbMsg);
		} catch (UnsupportedOperationException e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
			mEqualizer.release();
			mEqualizer =null;
		}
		//https://osdn.jp/projects/gb-231r1-is01/scm/git/Gingerbread_2.3.3_r1_IS01/blobs/master/frameworks/base/media/tests/MediaFrameworkTest/src/com/android/mediaframeworktest/functional/MediaEqualizerTest.java
	}

	private void setupBassBoost(Intent intent ) {			//ベースブーストOn/Off
		final String TAG = "setupBassBoost[MusicPlayerService]";
		String dbMsg="開始";/////////////////////////////////////
		try {
			Bundle extras = intent.getExtras();
			bBoot = extras.getBoolean("bBoot");
			dbMsg=  "bBoot=" + bBoot;
			if(mPlayer != null){
				bassBoostBody( bBoot );		//ベースブーストOn/Off本体
			}
		//	Thread.sleep(500);
	//		myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	private void bassBoostBody(boolean  bBoot )  throws UnsupportedOperationException{			//ベースブーストOn/Off本体
		// Create the BassBoost object and attach it to our media player.
	//http://greety.sakura.ne.jp/redo/2011/01/abc2011w-gingerbread-sipaudiofx.html
	//
		final String TAG = "bassBoostBody[MusicPlayerService]";
		String dbMsg="開始";/////////////////////////////////////
		try {
			dbMsg=  "bBoot=" + bBoot;
			if(mBassBoost != null){
				mBassBoost.release();
				mBassBoost = null;
			}
			mBassBoost = new BassBoost(0, mPlayer.getAudioSessionId());
			dbMsg= dbMsg + ",supported=" + mBassBoost.getStrengthSupported();
			if(  mBassBoost.getStrengthSupported() ){
		//		dbMsg=dbMsg + "mBassBoost=" + mBassBoost;
				short roundedStrength = mBassBoost.getRoundedStrength();
				dbMsg= dbMsg + " roundedStrength = " + Short.toString(roundedStrength);
				if (bBoot) {					//roundedStrength > 0
					mBassBoost.setStrength((short) 1000); //effect and 1000 per mille designates the strongest
				} else {
					mBassBoost.setStrength((short) 0); // off
				}
		//		mPlayer.attachAuxEffect(mBassBoost.getId());				//☆曲を送ってしまう
				mBassBoost.setEnabled(true);
		//		mPlayer.setAuxEffectSendLevel(1.0f);
		//		mPlayer.prepare();
				roundedStrength = mBassBoost.getRoundedStrength();
				dbMsg= dbMsg + ">>" + Short.toString(roundedStrength);
			}
	//		myLog(TAG, dbMsg);
		} catch (UnsupportedOperationException e) {
	//		Toast.makeText(this, "BaseBoost: " + e.getMessage(), Toast.LENGTH_LONG).show();
			myErrorLog(TAG,dbMsg+"で"+e);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
			if(mBassBoost != null){
				mBassBoost.release();
				mBassBoost = null;
			}
		}
	}

	private void setupPresetReverb(Intent intent ) {					//リバーブ設定
		// Create the PresetReverb object and attach it to our media player.
	//http://greety.sakura.ne.jp/redo/2011/01/abc2011w-gingerbread-sipaudiofx.html
		final String TAG = "setupPresetReverb[MusicPlayerService]";
		String dbMsg="開始";/////////////////////////////////////
		try {
			Bundle extras = intent.getExtras();
			reverbBangou = extras.getShort("reverbBangou");					//リバーブ効果番号
			dbMsg=  "reverbBangou=" + reverbBangou;
			if(mPlayer != null){
				presetReverbBody(reverbBangou);					//リバーブ設定本体
			}
			final CharSequence[] items =getResources().getStringArray(R.array.effect_rev);
			reverbMei =( String.valueOf( items[reverbBangou]) );						//リバーブ効果名称;0解除				plNameSL.get(which) )
			dbMsg= dbMsg + ",reverbMei=" + reverbMei;
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {
	//		Toast.makeText(this, "PresetReverb: " + e.getMessage(), Toast.LENGTH_LONG).show();
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	private void presetReverbBody(Short reverbBangou) throws UnsupportedOperationException{					//リバーブ設定本体
		// Create the PresetReverb object and attach it to our media player.
//http://greety.sakura.ne.jp/redo/2011/01/abc2011w-gingerbread-sipaudiofx.html
//https://osdn.jp/users/tagoh/pf/android_so01c/scm/blobs/master/frameworks/base/media/tests/MediaFrameworkTest/src/com/android/mediaframeworktest/functional/MediaPresetReverbTest.java
		final String TAG = "presetReverbBody[MusicPlayerService]";
		String dbMsg="開始";/////////////////////////////////////
		try {
			dbMsg=  "reverbBangou=" + reverbBangou;
			dbMsg= dbMsg + ",mPresetReverb=" + mPresetReverb;
			if(mPresetReverb != null){
				mPresetReverb.release();
				mPresetReverb = null;
			}
			dbMsg= dbMsg + ",mPlayer=" + mPlayer;
//			dbMsg= dbMsg + ",AudioManager=" + am;
//			am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//			dbMsg= dbMsg + ">>" + am;
//			am.loadSoundEffects();
			mPlayer.setAuxEffectSendLevel(1.0f);											//効果のセンドレベル；デフォルトは0.0fなので必要
			mPresetReverb = new PresetReverb(0, mPlayer.getAudioSessionId());
			mPresetReverb.setPreset((short)reverbBangou);
			mPresetReverb.setEnabled(true);
	//		dbMsg= dbMsg + ",getId=" + mPresetReverb.getId();
	//		mPlayer.attachAuxEffect(mPresetReverb.getId());					//☆次の曲に送ってしまう
			Thread.sleep(500);
			dbMsg= dbMsg + ",Properties=" + mPresetReverb.getProperties().toString();
	//		myLog(TAG,dbMsg);
		} catch (UnsupportedOperationException e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		} catch (Exception e) {
			if(mPresetReverb != null){
				mPresetReverb.release();
				mPresetReverb = null;
			}
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}
/**
 * setupVisualizer ビジュアライザーの使用準備
 *java.lang.RuntimeException: Cannot initialize Visualizer engine, error: -1が発生するのはマニュフェストに以下が無い
*		<uses-permission android:name="android.permission.RECORD_AUDIO"/>
*		<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS"/>
*		呼出し元	createMediaPlayerIfNeeded
* 参考	http://tools.oesf.biz/android-4.1.2_r1.0/xref/development/samples/ApiDemos/src/com/example/android/apis/media/AudioFxDemo.java#eqTextView
*/
	public Visualizer setupVisualizer(Visualizer mVisualizer) {
		//
		// Create a VisualizerView (defined below), which will render the simplified audio wave form to a Canvas.
		final String TAG = "setupVisualizer[MusicPlayerService]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			if( mVisualizer != null ){
				mVisualizer.release();
			}
			mVisualizer = null;
			dbMsg= dbMsg + ",mPlayer=" + MusicPlayerService.this.mPlayer;
			if(  MusicPlayerService.this.mPlayer != null ){
				mVisualizer = new Visualizer( MusicPlayerService.this.mPlayer.getAudioSessionId());					//セッションIDで紐付ける Create the Visualizer object and attach it to our media player.
				mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
				dbMsg= dbMsg + ",mVisualizer=" +mVisualizer;
				ObjectResults objectResults = new ObjectResults( mVisualizer);					//Serializableを使ったオブジェクトの受け渡し
			} else {
				mVisualizer = null;
			}
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return mVisualizer;
	}

	private String songLyric;
	private boolean lyricAri;			//歌詞を取得できた

//以下、旧記載////////////////////////////////////////////////////////////////////////////////////////
	int okuriIti =0;
  	public void onAudioFocusChange(int focusChange) {
		final String TAG = "onAudioFocusChange[MusicPlayerService]";
		String dbMsg=ORGUT.nowTime(true,true,true);/////////////////////////////////////
try {
			dbMsg="focusChange="+focusChange+",isPlaying="+mPlayer.isPlaying();/////////////////////////////////////
			myLog(TAG,dbMsg);
			switch (focusChange) {
	  		case AudioManager.AUDIOFOCUS_GAIN:			// resume playback
	  			if (mPlayer == null){
	  				//initMediaPlayer();
	  			} else if (!mPlayer.isPlaying()){
	  				dbMsg=dbMsg+",mcPosition=" + mcPosition;/////////////////////////////////////
	  				mPlayer.seekTo(mcPosition);
	  				mPlayer.start();
					mState = State.Playing;
					mPlayer.setVolume(1.0f, 1.0f);
	  				myLog(TAG,dbMsg);
	  			}
	  			break;
	  		case AudioManager.AUDIOFOCUS_LOSS:			// Lost focus for an unbounded amount of time: stop playback and release media player
	  			if (mPlayer.isPlaying()){
	  				myLog(TAG,dbMsg);
	  				mPlayer.pause();			//mPlayer.stop();  kitcut以降はQCMediaPlayer mediaplayer NOT presentが発生する
	  				mPlayer.release();
	  				mPlayer = null;
	  			}
	  			break;

	  		case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:			// Lost focus for a short time, but we have to stop playback.
	  																// We don't release the media player because playback is likely to resume
	  			if (mPlayer.isPlaying()){
	  				mPlayer.pause();
	  			}
	  			break;

	  		case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:			// Lost focus for a short time, but it's ok to keep playing at an attenuated level
	  			if (mPlayer.isPlaying()){
	  				mPlayer.setVolume(0.1f, 0.1f);
	  			}
	  			break;
	  		}	//switch (focusChange) {
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}

  	}

  	public boolean retbool_GB=false;
	public boolean alartTowBt(String setumonn,String tMsg,String fMsg){		//2ボタンアラート；第一引数に設問文、第二引数に戻り値true、第三引数に戻り値False側の表記を指定する
		//		myLog("alartTowBt","alartTowBt；1;setumonn;"+setumonn+",tMsg;"+tMsg+",fMsg;"+fMsg);
		AlertDialog.Builder tbaBuilder = new AlertDialog.Builder(this);

		tbaBuilder.setMessage(setumonn)
			.setCancelable(false)
			.setPositiveButton(tMsg, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					retbool_GB= true;
					dialog.dismiss();			//Dialog;L1;このダイアログを放棄し, 表示をやめる. このメソッドは同時進行する事象の中で安全に行使する事が出来る。
				}
			})
			.setNegativeButton(fMsg, new DialogInterface.OnClickListener() {
				//Set a listener to be invoked when the negative button of the dialog is pressed.
				public void onClick(DialogInterface dialog, int id) {
					retbool_GB=false;
					dialog.dismiss();
				}
			})
			.show();
	/*	AlertDialog tbAlert = tbaBuilder.create();
		tbAlert.show();*/
		return retbool_GB;
	}	//2ボタンアラート；///////////////////////////////////////////////////////////////////////////////
/////Bluetooth,着信のイベント///////////////////////////////////////////////////////////////////////////////////////////////////
	PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, String number) {
			final String TAG = "onCallStateChanged[MusicPlayerService]";
			String dbMsg="開始";/////////////////////////////////////
			try{
				dbMsg="state = " + state;/////////////////////////////////////
				dbMsg=dbMsg + " , number = " + number;/////////////////////////////////////
				phoneCallEvent(state, number);
	//			myLog(TAG,dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"で"+e);
			}
		}
	};

	private void phoneCallEvent(int state, String number){
		final String TAG = "phoneCallEvent[MusicPlayerService]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			main_pref = getSharedPreferences( getResources().getString(R.string.pref_main_file) ,MODE_PRIVATE);		//	getSharedPreferences(prefFname,MODE_PRIVATE);
			mainEditor = main_pref.edit();
			dbMsg="state = " + state;/////////////////////////////////////
			dbMsg=dbMsg + " , number = " + number;/////////////////////////////////////
			switch(state) {	  								  /* 各状態でTextViewを追加する */
			case TelephonyManager.CALL_STATE_RINGING:	/* 着信 */
				dbMsg=dbMsg + ">>着信;mPlayer=" + mPlayer;/////////////////////////////////////
				if(mPlayer != null){
					dbMsg=dbMsg + ">>isPlaying=" + mPlayer.isPlaying();/////////////////////////////////////
					if(mPlayer.isPlaying()){
						setPref();		//プリファレンス記載
						mPlayer.pause();
					}
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:	/* 1；通話 */
				dbMsg=dbMsg + ">>通話";/////////////////////////////////////
				break;
			case TelephonyManager.CALL_STATE_IDLE:	/* 0；待ち受け */
				dbMsg=dbMsg + ">>待ち受け;mPlayer=" + mPlayer;/////////////////////////////////////
				Map<String, ?> keys = main_pref.getAll();
				dbMsg=dbMsg + ",keys=" +keys.size() +"項目" ;/////////////////////////////////////
				pref_cyakusinn_fukki= Boolean.valueOf(String.valueOf(keys.get("pref_cyakusinn_fukki")));			//終話後に自動再生
				dbMsg=dbMsg + ",終話後に自動再生=" + pref_cyakusinn_fukki ;/////////////////////////////////////
				if(pref_cyakusinn_fukki){
					dataFN = String.valueOf(keys.get("pref_saisei_fname"));				//再生中のファイル名  Editor に値を代入
					dbMsg=dbMsg + "," +dataFN ;/////////////////////////////////////
					mcPosition = Integer.valueOf(String.valueOf(keys.get("pref_saisei_jikan")));				//選択中選択ポジション
					dbMsg=dbMsg + "[" + mcPosition + "]";/////////////////////////////////////
					if(mPlayer != null){
	//					myLog(TAG,dbMsg);
						if(! mPlayer.isPlaying()){
							dbMsg=dbMsg + ">>isPlaying=" + mPlayer.isPlaying();/////////////////////////////////////
				//			mPlayer.pause();			//pauseから復帰せず
							dbMsg=dbMsg + ">mcPosition=" + mcPosition;/////////////////////////////////////
							myLog(TAG,dbMsg);
							mPlayer.seekTo(mcPosition);
							mPlayer.start();
							mState = State.Playing;
				//			playMuFile(dataFN, mcPosition);	//再生開始
						}
					}else{
				//		playMuFile(dataFN, mcPosition);	//再生開始
					}
				}
				break;
			}
			if(mPlayer != null){
				dbMsg=dbMsg + ">>isPlaying=" + mPlayer.isPlaying();/////////////////////////////////////
			}
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	public void receiverSeisei(){		//レシーバーを生成 <onResume , playing , mData2Service	onClick
		final String TAG = "receiverSeisei[MusicPlayerService]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
//			if( btReceiver== null && pref_bt_renkei ){
//				dbMsg=  "Bluetooth;";/////////////////////////////////////
//				if(mBluetoothAdapter == null){
//					mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();			// Get local Bluetooth adapter
//					dbMsg=dbMsg + ",BluetoothAdapterは" + mBluetoothAdapter;/////////////////////////////////////
//				}
//				IntentFilter btFilter = new IntentFilter();									//BluetoothA2dpのACTIONを保持する
//				btFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);			//20160414
//				btFilter.addAction(BluetoothDevice.ACTION_FOUND);							//20160414
//				btFilter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);					//20160414
//				btFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);			//20160414
//				btFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
//				btFilter.addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED);
//				btFilter.addAction(Intent.ACTION_MEDIA_BUTTON);							//操作ボタン対応？
//				dbMsg= dbMsg +  ",btReceiver=" + btReceiver;////////////////////////
//			if( btReceiver == null ){
//				btReceiver = new BuletoohtReceiver();				//BuletoohtReceiver();
//				btReceiver.service = this;		//this
//		//		btHandler = new Handler();
//			}
//				registerReceiver(btReceiver, btFilter);
//				dbMsg= dbMsg + ">生成>=" + btReceiver;///////////////////////
//			}
		//	myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	public void receiverHaki(){		//レシーバーを破棄
		final String TAG = "receiverHaki[MusicPlayerService]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			dbMsg= ",btReceiver=" + btReceiver;////////////////////////
			if( btReceiver != null ){
//				if(mBluetoothAdapter == null){
//					mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();			// Get local Bluetooth adapter
//				}
//				mBluetoothAdapter.cancelDiscovery(); //検索キャンセル
//				unregisterReceiver(btReceiver);
				btReceiver = null;
				dbMsg= ">>" + btReceiver;////////////////////////
			}
//			dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

private void quitMe( int startId ) {			//このサービスを閉じる
	final String TAG = "quitMe[MusicPlayerService]";
	String dbMsg="開始";/////////////////////////////////////
	try{
		dbMsg= mIndex +"+"+ frCount+";選択中="+ sentakuCyuu;/////////////////////////////////////
		processStopRequest(false);															//②ⅲStop?eタイマーを破棄してmPlayerの破棄へ
		mState = State.Stopped;		// Service is being killed, so make sure we release our resources
		dbMsg= dbMsg +",g_timer="+ g_timer ;/////////////////////////////////////
		if(g_timer != null){
			g_timer.cancel();		//cancelメソッド実行後は再利用できない
			g_timer.purge();
			g_timer=null;
		}
//		dbMsg= dbMsg +",g_handler="+ g_handler ;/////////////////////////////////////
//		if(g_handler != null){
//			g_handler = null;
//		}
//		dbMsg= dbMsg +",timertask="+ timertask ;/////////////////////////////////////
//		if(timertask != null){
//			timertask.cancel();
//			timertask = null;
//		}

		giveUpAudioFocus();			//AudioFocus.NoFocusNoDuckへ設定
//		myLog(TAG,dbMsg);
		receiverHaki();							//レシーバーを破棄
		dbMsg= dbMsg+",lpNotification=" + lpNotification;//,lpNotification=Notification(pri=0 contentView=com.hijiyam_koubou.marasongs/0x1090080 vibrate=null sound=null defaults=0x0 flags=0x10 color=0xff333333 category=transport actions=3 vis=PRIVATE),
		if(lpNotification != null){
		//	dbMsg= dbMsg+",lpNControls=" + lpNControls;//lpNControls=android.media.session.MediaController$TransportControls@16c85e3a
			NotificationManager notifManager= (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
			notifManager.cancel(NOTIFICATION_ID);
			dbMsg= dbMsg+">>" + lpNotification;
		}
		dbMsg= dbMsg +",mNotificationManager=" + mNotificationManager;//mNotificationManager=null,
		if( mNotificationManager != null ){
			mNotificationManager.cancel(NOTIFICATION_ID);
	//		mNotificationManager.cancelAll();
			mNotificationManager = null;
			dbMsg= dbMsg +">>" + mNotificationManager;/////////////////////////////////////
		}
		if( mRemoteControlClient != null ){
			mAudioManager.unregisterRemoteControlClient(mRemoteControlClient);
			mRemoteControlClient = null;
		}
		if( mEqualizer != null ){
			mEqualizer.release();
			mEqualizer = null;
		}
		if( mBassBoost != null ){
			mBassBoost.release();
			mBassBoost = null;
		}
		if( mPresetReverb != null ){
			mPresetReverb.release();
			mPresetReverb = null;
		}
//		if ( mPlayer2 != null) {		// stop and release the Media Player, if it's available
//			mPlayer2.reset();
//			mPlayer2.release();
//			mPlayer2 = null;
//		}
//Service com.hijiyam_koubou.marasongs.MusicPlayerService has leaked IntentReceiver com.hijiyam_koubou.marasongs.BuletoohtReceiver@42f0d470 that was originally registered here. Are you missing a call to unregisterReceiver()?
		//unregisterReceiverされていない
		dbMsg= dbMsg+",startId=" + startId;////////////////////////
		boolean storR = MusicPlayerService.this.stopSelfResult(startId);
		dbMsg= dbMsg+",storR=" + storR;////////////////////////
		if( storR ){						//サービスが消去できれば
			nowSartId = 0;			//idも消去
		}
		dbMsg= dbMsg+",nowSartId=" + nowSartId;////////////////////////
		stopSelf();
		dbMsg= dbMsg+",mBinder=" + mBinder;////////////////////////
		MusicPlayerService.this.stopSelf();
	//	myLog(TAG,dbMsg);
/*startIdを指定せずにサービスを終了しようとして、同じタイミングでstartService()を呼び出してしまったら、
* startService()が処理できなくなってしまいます。そうした事態を避けるためにstopSelf()に最新のstartIdを渡すことで、
* 同時にstartService()が呼び出された場合にそのstartIdを比較して、サービスの終了処理を行わないようにします。
* また、同時に呼び出されたstartService()を処理できます。stopSelfResult()は、サービスを終了できたかどうかの戻り値を受け取れます。*/
/* 0+0;選択中=false,mNotificationManager=null,mNotificationManager=null,
*/
	} catch (Exception e) {
		myErrorLog(TAG,dbMsg+"で"+e);
	}
}

/////ライフサイクル////////////////////////////////////////////////////////////////////////Bluetooth,着信のイベント/////
	//@Override
	protected void onHandleIntent(Intent intent) {
		final String TAG = "onHandleIntent[MusicPlayerService]";
		String dbMsg="開始";/////////////////////////////////////
		try {
			Thread.sleep(10000);
			Intent broadcastIntent = new Intent();
			//	  public Item playingItem;
			dbMsg="artist=" +creditArtistName ;//////////////////////////
			broadcastIntent.putExtra("artist", creditArtistName);						//クレジットアーティスト名
			dbMsg="album_artist=" + album_artist;/////////////////////////////////////
			broadcastIntent.putExtra("album_artist", album_artist);		//リストアップしたアルバムアーティスト名
			dbMsg= dbMsg + ",album=" + albumName;
			broadcastIntent.putExtra("albumName", albumName);			//アルバム名
			dbMsg= dbMsg + ",titol=" + titolName;
			broadcastIntent.putExtra("titolName", titolName);			//曲名
			dbMsg= dbMsg + ",dataFN=" + dataFN;
			broadcastIntent.putExtra("dataFN", dataFN);				//DATA;The data stream for the file ;Type: DATA STREAM
			dbMsg= dbMsg + "[" + mcPosition;
			broadcastIntent.putExtra("mcPosition", mcPosition);		//現在の再生ポジション☆生成時は最初から
			dbMsg= dbMsg + "/" + saiseiJikan +"]";
			broadcastIntent.putExtra("saiseiJikan", saiseiJikan);		//DURATION;継続;The duration of the audio file, in ms;Type: INTEGER (long)
			broadcastIntent.setAction("mFilter");
			myLog(TAG,dbMsg);
			getBaseContext().sendBroadcast(broadcastIntent);
		} catch (InterruptedException e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	private final IBinder mBinder = new MusicPlayBinder();
	public class MusicPlayBinder extends Binder {
		public MusicPlayerService getService() {
			final String TAG = "MusicPlayBinder[MusicPlayerService]";
			String dbMsg="bindServicから開始";/////////「プロセス間通信」（IPC：Inter Process Communication）によるリモートメソッドコールを行う
			try{
//				Intent notificationIntent = new Intent(this, MusicPlayerService.class);									// 通知押下時に、MusicPlayServiceのonStartCommandを呼び出すためのintent
//				PendingIntent pendingIntent = PendingIntent.getService(this, 0, notificationIntent, 0);
//				NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());				// サービスを永続化するために、通知を作成する
//				builder.setContentIntent(pendingIntent);
//				builder.setTicker("準備中");
//				builder.setContentTitle("title");
//				builder.setContentText("text");
//				builder.setSmallIcon(android.R.drawable.ic_dialog_info);
//				mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//				mNotificationManager.notify(R.string.psSarviceUri, builder.build());
//				startForeground(R.string.psSarviceUri, builder.build());				// サービス永続化
				dbMsg= "MusicPlayerService=" + MusicPlayerService.this;
				myLog(TAG,dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"で"+e);
			}
			return MusicPlayerService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		final String TAG = "onBind[MusicPlayerService]";
		String dbMsg="bindServicから開始";/////////「プロセス間通信」（IPC：Inter Process Communication）によるリモートメソッドコールを行う
		try{
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
		return null;				//サービスの実体を返します
	}

	@Override
	public boolean onUnbind(Intent arg0) {
		final String TAG = "onUnbind[MusicPlayerService]";
		String dbMsg="bindServicから開始";/////////////////////////////////////
		try{
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
		dbMsg="onDestroyへ";/////////////////////////////////////
	return false;
	}

	@Override
	public void onDestroy() {
		final String TAG = "onDestroy[MusicPlayerService]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg= dbMsg +",mPlayer=" + mPlayer;/////////////////////////////////////
			relaxResources(true);		//mPlayerの破棄
	//		receiverHaki();							//レシーバーを破棄
			dbMsg= dbMsg +",nowSartId=" + nowSartId;/////////////////////////////////////
			if(0 < nowSartId ){
				quitMe( nowSartId );			//このサービスを閉じる
			}
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	super.onDestroy();
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
}

/*
//mMediaPlayer2の使い方	http://www110.kir.jp/Android/ch0707.html

//		http://dev.classmethod.jp/smartphone/android/android-tips-23-android4-1-notification-style/		いまさら聞けない Notification の使いかた
//		http://into.cocolog-nifty.com/pulog/2011/10/android030-7026.html				Androidアプリ開発メモ030:サービス その2
//		http://techbooster.org/android/application/267/										MediaPlayerで音楽を再生する
//		http://www110.kir.jp/Android/ch0707.html												音楽の情報をロックスクリーンに表示する
//		http://dogear11.hatenablog.com/entry/2014/07/30/112602							RemoteControlClientの使い方(RemoteControllerとの連携)
//		http://molehill.mole-kingdom.com/opencms/export/sites/default/translate/Android/APIGuideDoc/media/mediaplayer/index.html		メディアの再生
//		http://d.hatena.ne.jp/minghai/20080728/p2										//Service
//		http://y-anz-m.blogspot.jp/2011/07/androidappwidget-pendingintent-putextra.html
 * 04-13 10:50:14.975: E/MediaPlayer-JNI(17615): QCMediaPlayer mediaplayer NOT present
 * 04-13 10:50:15.255: E/MediaPlayer(17615): Should have subtitle controller already set

 * */

