package com.hijiyam_koubou.marasongs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaDescription;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.PresetReverb;
import android.media.audiofx.Visualizer;
import android.media.browse.MediaBrowser;
import android.media.session.MediaController.TransportControls;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.service.media.MediaBrowserService;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;
import androidx.media.utils.MediaConstants;
import androidx.preference.PreferenceManager;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("deprecation")
@SuppressLint("InlinedApi")
public class MusicPlayerService  extends MediaBrowserServiceCompat{
	// implements MusicFocusable,PrepareMusicRetrieverTask.MusicRetrieverPreparedListener  , OnCompletionListener, OnPreparedListener
	//	, OnErrorListener,
	public Context rContext;			//static
	public MusicLibrary mLibrary;

	MediaSessionCompat.QueueItem queueItem;
	/**プレイヤー*/
	public Class<? extends MaraSonActivity> playerClass;
	public OrgUtil ORGUT;				//自作関数集

	public MyApp myApp;
//	public MaraSonActivity MUP;								//音楽プレイヤー
	public MediaPlayer mPlayer = null;
	public MediaPlayer mPlayer2 = null;

	private List<MediaSession.QueueItem> mPlayingQueue; // 再生キューリスト
	private int mCurrentQueueIndex;                     // 再生キューのインデックス
	private static final String MEDIA_ID_ROOT = "__ROOT__";
	public MediaSessionCompat mSession;			//Androidシリーズでメディア再生処理を共通化するサーバの様なsもの
	public MediaSessionCompat.Token mSessionToken;	// Media Sessionの固有のID
	public MediaBrowserCompat mBrowser;
	public MediaControllerCompat mController;

	/**Urlだけのプレイリスト用簡易リスト*/
	public ArrayList<String> plSL;
	public int nextIndex;				//リスト中で次のインデックス
	/**リスト中のインデックス*/
	public int mIndex;
	/**リストの総登録曲数*/
	public int listEnd;
	public String creditArtistName ;	//クレジットアーティスト名
	public String albumArtistName =null;		//アルバムの代表 アーティスト名
	public String albumName =null;		//アルバム名
	public String titolName =null;		//曲名
	public int audioId;
	public long albumId;
	public String album_art ;
	public Uri albumArtUri ;
	public String NextDataFN;
	public MusicPlaylist musicPlaylist ;

	public static final String ACTION_START_SERVICE= "ACTION_START_SERVICE";
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
	private MediaBrowserCompat.MediaItem mediaItem;

	enum State {	// indicates the state our service:
		Retrieving,	// the MediaRetriever is retrieving music
		Stopped,	// media player is stopped and not prepared to play
		Preparing,	// media player is preparing...
		Playing, 	// playback active (media player ready!). (but the media player may actually be paused in this state if we don't have audio focus. But we stay in this state so that we know we have to resume playback once we get focus back) playback paused (media player ready!)
		Paused		// playback paused (media player ready!)
	}

	private State mState = State.Retrieving;
	boolean mStartPlayingAfterRetrieve = false;// if in Retrieving mode, this flag indicates whether we should start playing immediately when we are ready or not.
	enum AudioFocus {	// do we have audio focus?
		NoFocusNoDuck,	// we don't have audio focus, and can't duck
		NoFocusCanDuck,	// we don't have focus, but can play at a low volume ("ducking")
		Focused			// we have full audio focus
	}
	private AudioFocus mAudioFocus = AudioFocus.NoFocusNoDuck;						// do we have audio focus?
	private int tugiNoKyoku ;
	private AudioManager mAudioManager;
	public TimerTask timertask;
	private long mRelaxTime = System.currentTimeMillis();

	//プリファレンス
	public MyPreferences myPreferences;
	public SharedPreferences sharedPref;
	public Editor mainEditor ;
	public Editor myEditor ;

	public String siseizumiDataFN;				//前回再生済みのファイル名
	public String ruikei_artist;				//アーティスト累計
//	public boolean pref_cyakusinn_fukki=true;		//終話後に自動再生
//	public boolean myPreferences.nowList_data =true;				//Bluetoothの接続に連携して一時停止/再開
	public String myPFN = "ma_pref";
	public String pref_artist_bunnri = "1000";		//アーティストリストを分離する曲数
	public String pref_saikin_tuika = "5";			//最近追加リストのデフォルト枚数
	public String pref_saikin_sisei = "100";		//最近再生加リストのデフォルト枚数

	public String play_order;
	public int repeatType;			//リピート再生の種類
	public String repeatArtist;		//リピートさせるアーティスト名
	public boolean rp_pp = false;			//2点間リピート中
	public int pp_start = 0;			//リピート区間開始点
	public int pp_end;				//リピート区間終了点
	public int tugiList_id;		//次に再生するリストID;
	public String tugiList;		//次に再生するリスト名;リクエストリスト
	public String tone_name;						//トーン名称
	public String reverbMei;						//リバーブ効果名称
	public short reverbBangou;						//リバーブ効果番号

//	public TelephonyManager mTelephonyManager;		//Android 12 でPhoneStateListener が Deprecated になった
	int musicVol ;							//音楽再生音量
	int imanoJyoutai;

	public Item playingItem;
	public String album_artist =null;		//リストアップしたアルバムアーティスト名
	public int crossFeadTime = 500;		//再生終了時、何ms前に次の曲に切り替えるか
	public int mcPosition;			//現在の再生ポジション☆生成時は最初から
	public int saiseiJikan;				//DURATION;継続;The duration of the audio file, in ms;Type: INTEGER (long)
	public long ruikeiSTTime;			//累積時間
	public long ruikeikasannTime;			//累積加算時間
	public int ruikeikyoku;			//累積曲数
	public boolean IsPlaying ;			//再生中か
	public boolean IsSeisei ;			//生成中
	public boolean kaisiZumi = false;
	public int frCount=0;										//送り戻し待ち曲数
	public boolean sentakuCyuu = false;					//送り戻しリスト選択中；sendPlayerStateで解除
	public String nowAction =null;							//現在のアクション

	public String b_dataFN = "";			//すでに再生している再生ファイル
	public String b_Album ="";			//前のアルバム
	public String c_selection =null;			//取得する列を絞り込むときに使います。具体的には "AGE > 30" のように、SQL文の WHERE 句を指定します。null を指定すると、全行を取得することになります。
	public String[] c_selectionArgs =null;		//selection でバインドを使用したとき、バインドの値をここで指定します。例えば selection で "AGE > ?" としたとき、ここで [ 30 ] と指定することができます。
	public static final String ACTION = "Player Service";
	public String action;									//ボタンなどで指定されたアクション

	public Handler btHandler = null;		//new Handler();
	public BluetoothAdapter mBluetoothAdapter = null;
	public BuletoohtReceiver btReceiver;							//public BuletoohtReceiver btReceiver;		BroadcastReceiver
	public String dviceStyteStr;									//デバイスの接続情報
	public String dviceNamer;										//デバイス名
	public String dviceStytus;										//デバイスの状態
	public String stateBaseStr = null;
	public String b_stateStr;
	public String b_state ="";

	static final int REQUEST_ENABLE_BT = 0;
	public boolean selfStop = false;

//	public MusicPlayerService(Context context ) {
//		final String TAG = "MusicPlayerService";
//		String dbMsg= "";
//		try{
//			this.rContext = context;
//			playerClass = context.getClass();
//			dbMsg= ",playerClass=" + playerClass.getName();
//
//		//	readPref();
//			myLog(TAG,dbMsg);
//		}catch (Exception e) {
//			myErrorLog(TAG,dbMsg + "で"+e.toString());
//		}
//	}


	public void readPref() {        //プリファレンスの読込み
		final String TAG = "readPref";
		String dbMsg="";
		try {
			if(myPreferences == null){
			 	myPreferences = new MyPreferences(this);
			}
			dbMsg += "MyPreferencesy読込み";
			myPreferences.readPref();
			dbMsg += "[" + myPreferences.nowList_id+"]" + myPreferences.nowList;
			String	retString = myPreferences.pref_gyapless;	//クロスフェード時間
			dbMsg += ",retString="+ retString;
			if(retString != null){
				crossFeadTime = Integer.parseInt(retString);	//クロスフェード時間
			}else{
				crossFeadTime=1000;
			}
			dbMsg += ",crossFeadTime="+ crossFeadTime;
			pref_saikin_tuika = myPreferences.pref_saikin_tuika;			//最近追加リストのデフォルト枚数
			pref_saikin_sisei = myPreferences.pref_saikin_sisei;		//最近再生加リストのデフォルト枚数
			repeatType = myPreferences.repeatType;							//リピート再生の種類
			dbMsg += ",ロックスクリーンプレイヤー="+ myPreferences.pref_lockscreen;
			dbMsg += ",ノティフィケーションプレイヤー="+ myPreferences.pref_notifplayer;
			play_order =myPreferences.play_order;

			dbMsg += ",全曲リスト="+ myPreferences.pref_zenkyoku_list_id;
			dbMsg += ",最近追加="+ myPreferences.saikintuika_list_id;
			dbMsg += ",最近再生="+ myPreferences.saikinsisei_list_id;

			pref_toneList = myPreferences.pref_toneList;		//プリファレンス保存用トーンリスト
			toneSeparata = myPreferences.toneSeparata;
			tone_name = myPreferences.tone_name;				//トーン名称
			bBoot = myPreferences.bBoot;					//バスブート
			reverbBangou = myPreferences.reverbBangou;				//リバーブ効果番号

			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}																	//設定読込・旧バージョン設定の消去

	/**
	 * プリファレンス記載
	 * 呼出し元は		processPauseRequest/processStopRequest/songInfoSett/phoneCallEventでCALL_STATE_RINGING
	 * */
	public void setPref() {			//プリファレンス記載
		final String TAG = "setPref";
		String dbMsg="";
		try{
			new Thread(new Runnable() {				//ワーカースレッドの生成
				public void run() {
					final String TAG = "setPref";
					String dbMsg="";
					try {
						String dataFN = getPrefStr( "pref_data_url" ,"" , MusicPlayerService.this);
						dbMsg += ",dataFN="+dataFN;
						if(dataFN != null){
							dbMsg += ",mIndex="+ mIndex;
							int mcPosition = 0;
							int saiseiJikan = (int)playingItem.duration;
//							sharedPref = getSharedPreferences( getResources().getString(R.string.pref_main_file) ,MODE_PRIVATE);		//MODE_WORLD_WRITEABLE 	getSharedPreferences(prefFname,MODE_PRIVATE);
//							mainEditor = sharedPref.edit();
//							mainEditor.putString("nowList_id",String.valueOf(myPreferences.nowList_id));		//再生中のプレイリストID
//							mainEditor.putString("nowList",myPreferences.nowList);							//再生中のプレイリスト名
			//				mainEditor.putInt("pref_mIndex", mIndex );
							mainEditor.putString("pref_data_url",dataFN);				//再生していた曲	.commit()
							if(exoPlayer !=  null){
								mcPosition = (int) exoPlayer.getCurrentPosition();
								saiseiJikan = (int) exoPlayer.getDuration();
							}
							mainEditor.putInt("pref_position", mcPosition);		//再生ポジション
							mainEditor.putInt("pref_duration", saiseiJikan);	//再生時間
							dbMsg += "[mcPosition="+ORGUT.sdf_mss.format(mcPosition) + "/" + ";"+ORGUT.sdf_mss.format(saiseiJikan) + "]";
							dbMsg +=";"+ruikeikyoku + "曲"+ruikeiSTTime+"mS" ;////////////////////////////////////////////////////////////////////////////
							if( ruikeikyoku > 0 ){
								mainEditor.putString( "pref_zenkai_saiseKyoku", String.valueOf(ruikeikyoku));			//連続再生曲数
								mainEditor.putString( "pref_zenkai_saiseijikann", String.valueOf(ruikeiSTTime));		//連続再生時間
							}
							Boolean kakikomi = mainEditor.commit();	// データの保存
							dbMsg +=",書き込み成功="+kakikomi;////////////////////////////////////////////////////////////////////////////
						}
						myLog(TAG,dbMsg);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	private Thread mSelfStopThread = new Thread() {						//停止後 30 分再生がなかったらサービスを止めるstopSelf	createBodyでスタート
		public void run() {
			final String TAG = "mSelfStopThread";
			String dbMsg="";
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
		}
	};

//		https://sites.google.com/site/androidappzz/home/dev/volumesample				//着信音量を取得、設定する方法
	/**
	 *  mStateで再生中なら停止(State.Paused)、停止してたら再生*/
	private void processTogglePlaybackRequest() {
		final String TAG = "processToggle";
		String dbMsg="";
		try{
			dbMsg +="mState= " + mState;/////////////////////////////////////
			if (mState == State.Paused || mState == State.Stopped) {
				processPlayRequest();
				ppAction.icon =android.R.drawable.ic_media_play;
			} else {
				processPauseRequest();
				ppAction.icon =android.R.drawable.ic_media_pause;
			}
		//	ppIconChaange();
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	/**
	 *  ポーズ中、もしくは再生ポジションが以上なら再生、それ以外は曲を読み出し*/
	public void processPlayRequest() {																//②ⅲPlay?StoppedならplayNextSong/PausedならconfigAndStartMediaPlayer
		final String TAG = "processPlayRequest";
		String dbMsg ="";
		try{
//			int mcPosition = getPrefInt("pref_position" , 0, MusicPlayerService.this);		//sharedPref.getInt("pref_position" , 0);
			int saiseiJikan = getPrefInt("pref_duration" , 0, MusicPlayerService.this);
			dbMsg +="mState= " + mState + " ,mcPosition= " + mcPosition + "/" +  saiseiJikan + "[ms]";
			if (mState == State.Retrieving) {
				mStartPlayingAfterRetrieve = true;			// If we are still retrieving media, just set the flag to start playing when we're ready
				myLog(TAG,dbMsg);
				return;
			}

//			dbMsg +=",mPlayer " + mPlayer;/////////////////////////////////////
//			if( mPlayer != null){
//				dbMsg +=",isPlaying " + mPlayer.isPlaying();/////////////////////////////////////
//				mPlayer.seekTo(mcPosition);		//先頭に戻す
//				action = MusicPlayerService.ACTION_PLAY;
//				mPlayer.start();
//				dbMsg +=">isPlaying>" + mPlayer.isPlaying();/////////////////////////////////////
//				mState = State.Playing;			// Pause media player and cancel the 'foreground service' state.
//				dbMsg +=",mState=" + mState;/////////////////////////////////////
//			}

			tryToGetAudioFocus();
			dbMsg +="mPlayer= " + exoPlayer ;
			if(exoPlayer == null){
	//			playNextSong(mIndex,true);
			} else{
				if (mState == State.Stopped) {		// actually play the song
					dbMsg += " , Stopped>>次曲へ " ;
	//				playNextSong(mIndex,true);
				} else if (mState == State.Paused
				|| 0< exoPlayer.getCurrentPosition()
				) {			//0531元SoucsはPausedだけ   || 0 < mcPosition
					configAndStartMediaPlayer();					//ポーズを解除
				} else {
					dbMsg += " , 次曲へ " ;
	//				playNextSong(mIndex,true);
				}
			}
			sendPlayerState( exoPlayer );					//一曲分のデータ抽出して他のActvteyに渡す。
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	/**
	 * 再生中ならポーズに**/
	public void processPauseRequest() {															//②ⅲPause?
		final String TAG = "processPauseRequest";
		String dbMsg="";/////////////////////////////////////
		try{
			dbMsg="mState = " + mState;/////////////////////////////////////
			if (mState == State.Retrieving) {
				mStartPlayingAfterRetrieve = false;			// If we are still retrieving media, clear the flag that indicates we should start playing when we're ready
				return;
			}
			dbMsg +=">> " + mState;/////////////////////////////////////
			dbMsg +=",mPlayer " + exoPlayer;/////////////////////////////////////
			if( exoPlayer != null){
				dbMsg +=",isPlaying=" + exoPlayer.isPlaying();/////////////////////////////////////
				exoPlayer.setPlayWhenReady(false);//.pause();
				mState = State.Paused;			// Pause media player and cancel the 'foreground service' state.
				dbMsg +=">isPlaying>" + exoPlayer.isPlaying();/////////////////////////////////////
				mcPosition= (int) exoPlayer.getCurrentPosition();
				dbMsg += " , mcPosition= " + mcPosition + "[ms]";
				setPrefInt("pref_position", mcPosition, MusicPlayerService.this);        //sharedPref.getInt("pref_position" , 0);
				sendPlayerState(exoPlayer);					//一曲分のデータ抽出して他のActvteyに渡す。
			}
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	/**
	 *  次曲に送る*/
	private void processSkipRequest() {
		final String TAG = "processSkipRequest[MusicPlayerService]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg +=  "[" + mIndex;
			nextIndex = mIndex + 1;
			if(listEnd <= nextIndex){
				dbMsg +=  "/" + listEnd;
				nextIndex = 0;
			}
			dbMsg +=  ">>" + nextIndex;
			dbMsg += "mPlayer="+ exoPlayer;/////////////////////////////////////
			if(exoPlayer != null){
				if( exoPlayer.isPlaying() ){
					processStopRequest(false);					//タイマーを破棄して/mPlayerの破棄へ
				}
			}
//			frCount++;
//			dbMsg +=  "," + mIndex +"+"+ frCount+";選択中="+ sentakuCyuu;/////////////////////////////////////
//			if( exoPlayer == null){
//			//	Thread.sleep(1000);			//書ききる為の時間
//				okuriMpdosi(frCount);
//				frCount = 0;
//			}
			playNextSong(nextIndex,true);
			sendPlayerState(exoPlayer);					//一曲分のデータ抽出して他のActvteyに渡す。
			changeCount( exoPlayer );
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	/**
	 *  前の曲に戻す**/
	private void processRewindRequest() {															//②ⅲRew?
		final String TAG = "processRewindRequest[MusicPlayerService]";
		String dbMsg="開始";/////////////////////////////////////
		try{
//			dbMsg += "mPlayer="+ exoPlayer.getTrackInfo();
			if(exoPlayer != null){
				if( exoPlayer.isPlaying() ){
					processStopRequest(false);					//タイマーを破棄して/mPlayerの破棄へ
				}
			}
			dbMsg +=",[" + mIndex + "]";
			dbMsg += "/" + listEnd+ "]";
			nextIndex = mIndex - 1;
			if(nextIndex < 0){
				nextIndex = listEnd-1;
			}
			dbMsg +=">>[" + nextIndex + "]";
			mPlayer2 = null;
			playNextSong(nextIndex,true);
			sendPlayerState(exoPlayer);					//一曲分のデータ抽出して他のActvteyに渡す。
			changeCount( exoPlayer );
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	@SuppressLint("Range")
	public void okuriMpdosi(int tIDCo) {		//送り戻しの実行;加算数を渡す
		final String TAG = "okuriMpdosi[MusicPlayerService]";
		String dbMsg="";
		try{
			dbMsg += "[nowList_id=" + myPreferences.nowList_id + "]";/////////////////////////////////////
			dbMsg += "現在" + mIndex + "+ " + tIDCo;/////////////////////////////////////
			stopCount();		//タイマーオブジェクトを破棄
			mIndex = mIndex + tIDCo;
			int startCount = 0;
	//		listEnd = musicPlaylist.getUserListMaxPlayOrder(myPreferences.nowList_id);
			dbMsg += "/" + listEnd + "]";/////////////////////////////////////
			if(listEnd< mIndex){				//最後の曲を超えたら
				mIndex = startCount;									//最初の曲に戻す
				dbMsg +=">>" + mIndex;/////////////////////////////////////
			} else if(mIndex < startCount){							//最初の曲まで戻っていたら
				mIndex =listEnd;				//死後の曲へ
				dbMsg +=">>" + mIndex;/////////////////////////////////////
			}
			dbMsg +=">> " + mIndex;/////////////////////////////////////
			dbMsg +=",処理後に追加されたfrCount= " + frCount;/////////////////////////////////////
			if( frCount == 0 ||  exoPlayer == null) {                    //カウントが追加されていなければ
				dbMsg += "[" + (mIndex) + "/" + listEnd + "曲目へ]";///////////
				Cursor playingItem = musicPlaylist.getPlaylistItems(Integer.parseInt(myPreferences.nowList_id), mIndex);
				if (playingItem.moveToFirst()) {
					creditArtistName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST));        //playingItem.artist;	//クレジットされているアーティスト名
					dbMsg += " ,クレジット⁼ " + creditArtistName;
					albumName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM));        //playingItem.album;			//アルバム名
					dbMsg += " , アルバム⁼" + albumName;/////////////////////////////////////	this.album = album;
					titolName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE));        //playingItem.title;		//曲名
					dbMsg += " ,タイトル= " + titolName;/////////////////////////////////////		this.title = title;
					@SuppressLint("Range") int audioId = Integer.parseInt(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)));
					dbMsg += " ,audioId= " + audioId;
//				albumArtist = musicPlaylist.getAlbumArtist(audioId , MaraSonActivity.this);	//playingItem.album_artist;	//アルバムアーティスト名
//				dbMsg +=" ,アルバムアーティスト= " + albumArtist;
					Uri dataUri = Uri.parse(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA)));
					String dataFN = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA));
					//String.valueOf(dataUri);
					dbMsg += ",読込確認；dataFN=" + dataFN;/////////////////////////////////////
					int duration = Integer.parseInt(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.DURATION)));
					dbMsg += ">>" + playingItem;/////////////////////////////////////
					int mcPosition = 0;
					if (rp_pp) {                        //2点間リピート中で//リピート区間終了点
						mcPosition = pp_start;        //リピート区間開始点
					}
//					int duration = (int) playingItem.duration;
//					dbMsg += ">>" + playingItem.data + "の" + mcPosition + "[ms]" + duration + "[ms]";
//					setPrefInt("pref_duration", duration, MusicPlayerService.this);
//					setPrefInt("pref_position", mcPosition, MusicPlayerService.this);        //sharedPref.getInt("pref_position" , 0);
					action = MusicPlayerService.ACTION_PLAY;
					playNextSong(mIndex,true);            // If we're stopped, just go ahead to the next song and start playing
				} else {                                    //カウントの変動が続いていたら判定ループに戻ってもう一度送り操作
					//		myLog(TAG,dbMsg);
					changeCount(exoPlayer);                        //タイマーオブジェクトを使ったカウンタ更新を追加
				}
				playingItem.close();

			}
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	/**
	 * g_timer破棄
	 * mPlayerの破棄（mPlayerの破棄）へ
	 * */
	@SuppressLint("NewApi")
	private void processStopRequest(boolean force ) {						// <processStopRequest	boolean force
		final String TAG = "processStopRequest";
		String dbMsg="";
		try{
			dbMsg +="mState=" + mState;
			if(exoPlayer != null){
				setPref();		//プリファレンス記載
				dbMsg +=",isPlaying=" + exoPlayer.isPlaying();
				if ( exoPlayer.isPlaying()) {				//	mState == State.Playing || mState == State.Paused
					exoPlayer.stop();
					mState = State.Stopped;
					stopCount();		//タイマーオブジェクトを破棄
				}
			}
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	/**
	 * stopForegroundでサービスの消去して mPlayerの破棄
	* Releases resources used by the service for playback. This includes the "foreground service" status and notification, the wake locks and possibly the MediaPlayer.
	* @param releaseMediaPlayer Indicates whether the Media Player should also be released or not
	*/
	private void relaxResources(boolean releaseMediaPlayer) {				//mPlayerの破棄
		final String TAG = "relaxResources";
		String dbMsg="";/////////////////////////////////////
		try{
			dbMsg +="releaseMediaPlayer=" + releaseMediaPlayer;/////////////////////////////////////
			if(mBassBoost != null){
				mBassBoost.release();
			}
			if(mEqualizer != null){
				mEqualizer.release();
			}
	//		stopForeground(true);		//APIL5 このサービスの消去 stop being a foreground service
			if (releaseMediaPlayer  ) {		// stop and release the Media Player, if it's available
				if ( exoPlayer != null) {		// stop and release the Media Player, if it's available
					dbMsg +=",isPlaying=" + exoPlayer.isPlaying() ;/////////////////////////////////////
					if( exoPlayer.isPlaying()  ){
						exoPlayer.setPlayWhenReady(false);
					}
	//				musicVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	//				dbMsg +=",musicVol=" + musicVol;/////////////////////////////////////
	//				mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
	//				dbMsg +=">>" + musicVol;/////////////////////////////////////
	//  				myLog(TAG,dbMsg);
//					exoPlayer.reset();
					exoPlayer.release();
				}
				exoPlayer = null;

			}
			stopForeground(true);		//APIL5 このサービスの消去 stop being a foreground service
	//		if (btHandler != null){
	//			btHandler.removeMessages(1);
	//			btHandler = null;
	//		}
			mRelaxTime = System.currentTimeMillis();				//停止後 10 分再生がなかったらサービスを止める為のカウントスタート
			myLog(TAG,dbMsg);
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
	 *  指定された再生ポジションから再生を再開する
	 *  rocessPlayRequest から　ポーズ/再開の場合に呼ばれる
	 *  */
	private void configAndStartMediaPlayer() {			//既にMediaPlayerが生成されている
		final String TAG = "configAndStartMediaPlayer";
		String dbMsg="";
		try{
			if(exoPlayer != null){				//レジューム対応を追加
				int mcPosition = getPrefInt("pref_position" , 0, MusicPlayerService.this);  //sharedPref.getInt("pref_position" , 0);
				dbMsg += " , mcPosition= " + exoPlayer.getCurrentPosition() +">Pref>" + mcPosition + "[ms]";
				if (!exoPlayer.isPlaying()) {
					if(0 < mcPosition ){
						exoPlayer.seekTo(mcPosition);		// Attempt to perform seekTo in wrong state
						dbMsg += " >>" + exoPlayer.getCurrentPosition();/////////////////////////////////////
					}
					dbMsg += ", isPlaying=" + exoPlayer.isPlaying();
					exoPlayer.setPlayWhenReady(true);
					dbMsg += " >>" + exoPlayer.isPlaying();
					mState = State.Playing;
					changeCount( exoPlayer );						//タイマーオブジェクトを使ったカウンタ更新を追加
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
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	/**AudioFocus.Focusedと設定する*/
	private void tryToGetAudioFocus() {
		final String TAG = "tryToGetAudioFocus[MusicPlayerService]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg="mAudioFocus =" +mAudioFocus;/////////////////////////////////////
			dbMsg += "mAudioFocusHelper =" +mAudioFocusHelper;/////////////////////////////////////
			if (mAudioFocus != AudioFocus.Focused && mAudioFocusHelper != null && mAudioFocusHelper.requestFocus()) {
				mAudioFocus = AudioFocus.Focused;
				dbMsg +=">>" +mAudioFocus;/////////////////////////////////////
			}
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	public boolean yomiKomiCheck(String checkFN) throws IOException {		//setDataSourceを行う				 throws
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
	 * MediaPlerを生成して指定されたデータをセット(mPlayer.setDataSource)
	 * m指定データを確認。読み込めないデータが指定されたら次の曲を読み込む
	 * */
	@SuppressLint("Range")
	public void playNextSong(int itemIndex ,boolean toPlay) {		// throws IOException　 throws IllegalArgumentException
		final String TAG = "playNextSong";
		String dbMsg="";
		try{
			boolean yominaosi = false;
			dbMsg +=",nowList[" + myPreferences.nowList_id + "]" + myPreferences.nowList ;
//				if(mPlayer2 != null){
//					mPlayer.pause();
//		//			mPlayer = getMediaPlayer(this);
//					dbMsg +=",mPlayer2" + mPlayer2.getTrackInfo().toString();
//					dbMsg +=",送るのは[" + nextIndex;
//					dbMsg +="]" + NextDataFN;
//					boolean retBool = setPrefStr( "myPreferences.pref_data_url", String.valueOf(NextDataFN),MusicPlayerService.this);   			//再生中のファイル名
//					dbMsg += "を書込み" + retBool;
//					retBool = setPrefInt( "pref_position", 0,MusicPlayerService.this);   			//再生中のファイル名
//					dbMsg += "を書込み" + retBool;
//					getDataInfo(NextDataFN);
//
//					mPlayer = mPlayer2;
//					dbMsg +="\nmPlayer" + mPlayer.getTrackInfo().toString()  ;
//					myPreferences.pref_data_url = NextDataFN;
//					mIndex = nextIndex;
//
//					mPlayer.start();
//				}else{
			dbMsg +="の["  + itemIndex + "/"+ listEnd + "件目]toPlay=" + toPlay;
			myPreferences.pref_data_url = plSL.get(itemIndex);
			dbMsg += myPreferences.pref_data_url;
			getDataInfo(myPreferences.pref_data_url);
//						if(mPlayer == null){
			if(exoPlayer != null){
				if (exoPlayer.isPlaying()) {
					exoPlayer.stop();
					stopCount();
				}
				try {
//					exoPlayer.reset();
					exoPlayer.release();
					exoPlayer = null;
				} catch (IllegalStateException e) {
					myErrorLog(TAG,dbMsg+"で"+e);
				}
			}
			exoPlayer = (ExoPlayer) getMediaPlayer(getApplicationContext(),myPreferences.pref_data_url);  //createMediaPlayerIfNeeded()?
	//		exoPlayer.setLooping(true);						//☆以下二つより先に行わないとIllegalStateExceptionが発生
		//	setVolumeControlStream(AudioManager.STREAM_MUSIC);
		//	exoPlayer.setVolumeControlStream(AudioManager.STREAM_MUSIC);
			stopCount();
			getDataInfo(myPreferences.pref_data_url);
			if(toPlay){
				exoPlayer.setPlayWhenReady(true);
				mState = State.Playing;
//				changeCount( exoPlayer );						//タイマーオブジェクトを使ったカウンタ更新を追加
			}
			changeCount(exoPlayer);                        //タイマーオブジェクトを使ったカウンタ更新を追加
			mIndex = itemIndex;
			boolean retBool = setPrefStr( "pref_data_url", String.valueOf(myPreferences.pref_data_url),MusicPlayerService.this);   			//再生中のファイル名
			dbMsg += "を書込み" + retBool;
//			retBool = setPrefInt( "pref_mIndex", itemIndex,MusicPlayerService.this);   			//再生中のファイル名
//			dbMsg += "を書込み" + retBool;
			int mcPosition = 0;
			retBool = setPrefInt( "pref_position", mcPosition,MusicPlayerService.this);   			//再生中のファイル名
			dbMsg += "を書込み" + retBool;
			retBool = setPrefInt( "pref_duration", (int) exoPlayer.getDuration(),MusicPlayerService.this);   			//再生中のファイル名
			dbMsg += "を書込み" + retBool;
//			lpNotificationMake( myPreferences.pref_data_url);
			//	}



//				}
//				try {
//					nextIndex = mIndex + 1;
//					if(listEnd < nextIndex){
//						nextIndex = 0;
//					}
//					dbMsg +="\n次は["  + nextIndex + "/"+ listEnd + "件]";
//					Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", myPreferences.nowList_id);
//					String[] columns = null;			//{ idKey, nameKey };
//					String selection = MediaStore.Audio.Playlists.Members.PLAY_ORDER  + " = ? ";
//					String[] selectionArgs = { String.valueOf(nextIndex) };
//					String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
//					Cursor cursor = this.getContentResolver().query(uri, columns, selection, selectionArgs, c_orderBy );
//					dbMsg += ";" + cursor.getCount() + "件";
//					if(cursor.moveToFirst()){
//						NextDataFN = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA));/////////////////////////////////////
//						dbMsg += ";" + NextDataFN;
//						mPlayer2 = getMediaPlayer(this);
//						mPlayer2.setLooping(true);						//☆以下二つより先に行わないとIllegalStateExceptionが発生
//						mPlayer2.setAudioStreamType(AudioManager.STREAM_MUSIC);
//						mPlayer2.setDataSource(getApplicationContext(), Uri.parse(NextDataFN));				//☆MediaPlayer.createではjava.lang.IllegalStateException		http://umezo.hatenablog.jp/entry/20100531/1275319329
//					}
//				} catch (IllegalArgumentException e) {
//					myErrorLog(TAG,dbMsg+"で"+e);
//				} catch (IllegalStateException e) {
//					myErrorLog(TAG,dbMsg+"で"+e);
//				}

/*
				String dataFN = getPrefStr("myPreferences.pref_data_url" ,"" , MusicPlayerService.this);
				dbMsg +=",現在myPreferences.nowList[" + myPreferences.nowList_id + "]" + myPreferences.nowList + ";" +dataFN ;
				int mcPosition = getPrefInt("pref_position" ,0 , MusicPlayerService.this);
				int Duration = getPrefInt( "pref_duration" ,0 , MusicPlayerService.this);
				dbMsg += "[" + mIndex + "]" + dataFN + "." + mcPosition + "/" + Duration +"[ms]" ;
				dbMsg += ",dataFN="+dataFN;
				dbMsg +=",リスト中" + mIndex + "/" + listEnd;/////////////////////////////////////
				if(myPreferences.nowList.equals(getResources().getString(R.string.playlist_namae_request))){			//既にリクエストリスト実行中で
					int nokori = 0;
					Cursor cursor = null;
					siseizumiDataFN = String.valueOf(siseizumiDataFN);
					dbMsg +="削除する曲Uri= " + siseizumiDataFN;
					boolean sakujyoHantei = true;				//削除判定
					if( siseizumiDataFN == null){																	//2曲目以降は
						sakujyoHantei = false;
					}else if( siseizumiDataFN.equals("null") ){
						sakujyoHantei = false;
					}
					dbMsg +="削除判定= " + sakujyoHantei;
					if(sakujyoHantei){
						Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", myPreferences.nowList_id);
						String[] columns = null;			//{ idKey, nameKey };
						String selection =  MediaStore.Audio.Playlists.Members.DATA  + " = ? ";
						String[] selectionArgs = { siseizumiDataFN };
						String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
						cursor = this.getContentResolver().query(uri, columns, selection, selectionArgs, c_orderBy );
						nokori = cursor.getCount();
						dbMsg +=",該当"+cursor.getCount() +"件";
						if( cursor.moveToFirst() ){
							int delID = cursor.getInt(cursor.getColumnIndex( MediaStore.Audio.Playlists.Members._ID));
							dbMsg += "[" + delID +"]" ;/////////////////////////////////////
							String rStr = cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Playlists.Members.TITLE));
							dbMsg += ";" + rStr;/////////////////////////////////////
							dbMsg +=  "削除レコードのプレイリストUri= " + uri.toString();
							dbMsg +=  ",削除するレコードID= " + delID;
							String where = "_id=" + Integer.valueOf(delID);
							int renint = getContentResolver().delete(uri, where, null);				//削除		getApplicationContext()
							dbMsg +=  ",削除= " + renint + "レコード";
						}
						cursor = this.getContentResolver().query(uri, columns, null, null, c_orderBy );
						if(cursor.moveToFirst()){					//残りが有れば
							dataFN = cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Playlists.Members.DATA));
							dbMsg += "、次は" + dataFN;/////////////////////////////////////
						}else{
							sharedPref = getSharedPreferences( getResources().getString(R.string.pref_main_file) ,MODE_PRIVATE);		//	getSharedPreferences(prefFname,MODE_PRIVATE);
							Map<String, ?> keys = sharedPref.getAll();
							dataFN = String.valueOf(keys.get("maeDatFN"));							//再生中のファイル名//DATA;The data stream for the file ;Type: DATA STREAM
							dbMsg +=" の　" + dataFN +"に戻す。" ;
							mainEditor.putString( "myPreferences.nowList", String.valueOf(myPreferences.nowList));
							mainEditor.putString( "myPreferences.nowList_id", String.valueOf(myPreferences.nowList_id));		//☆intで書き込むとcannot be cast
							mainEditor.putString( "myPreferences.pref_data_url", dataFN);		//再生中のファイル名
							mainEditor.putString( "maeList", null);
							mainEditor.putString( "maeList_id", String.valueOf(myPreferences.pref_zenkyoku_list_id));		//☆intで書き込むとcannot be cast
							mainEditor.putString( "maeDatFN", null);		//再生中のファイル名
							boolean kakikomi = mainEditor.commit();
							dbMsg += ",書き込み=" + kakikomi;	////////////////
						}
						yominaosi = true;
						if(! cursor.isClosed()){
							cursor.close();
						}
					}
				}else if(tugiList != null){ //リクエストリスト実行されていなくて　次のリストが設定されていたら…	 -1 < tugiList_id ||
					if(tugiList.equals(getResources().getString(R.string.playlist_namae_request))){ //リクエストリスト実行されていなくて　次のリストが設定されていたら…	 -1 < tugiList_id ||
						myPreferences.nowList_id = tugiList_id;
						myPreferences.nowList = tugiList;
						dbMsg +=">>リスト切替["+ myPreferences.nowList_id + "]" + tugiList;
						Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", myPreferences.nowList_id);
						String[] columns = null;			//{ idKey, nameKey };
						String selection =  null;			//MediaStore.Audio.Playlists.Members.DATA  + " = ? ";
						String[] selectionArgs = null;		//{ siseizumiDataFN };
						String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
						Cursor cursor = this.getContentResolver().query(uri, columns, selection, selectionArgs, c_orderBy );
						dbMsg +=",現在"+cursor.getCount() +"件登録";
						if( cursor.moveToFirst() ){
							dataFN = cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Playlists.Members.DATA));/////////////////////////////////////
							dbMsg += ";" + dataFN;/////////////////////////////////////
							setPrefInt("pref_position" , 0, MusicPlayerService.this);  //sharedPref.getInt("pref_position" , 0);
						}
						cursor.close();
						setPrefStr( "myPreferences.pref_data_url", String.valueOf(dataFN),MusicPlayerService.this);   			//再生中のファイル名
						boolean kakikomi = mainEditor.commit();
						dbMsg += ",書き込み=" + kakikomi;	////////////////
						tugiList_id = -1;
						tugiList =null;
						yominaosi = true;
						cursor.close();
					}
				}
				int startCount = 0;
				int endCount = listEnd - 1;
				if(listEnd <= mIndex ){
					mIndex = startCount;
					dbMsg +=">>" +mIndex;/////////////////////////////////////
				}else if( mIndex < startCount){
					mIndex = endCount;
					dbMsg +=">>" +mIndex;/////////////////////////////////////
				}
//				Cursor playingItem = musicPlaylist.getPlaylistItems(myPreferences.nowList_id,mIndex);
//				if(playingItem.moveToFirst()){
//					creditArtistName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST));		//playingItem.artist;	//クレジットされているアーティスト名
					dbMsg +=" ,クレジット⁼ " + creditArtistName;
//					albumName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM));		//playingItem.album;			//アルバム名
					dbMsg +=" , アルバム⁼" + albumName;/////////////////////////////////////	this.album = album;
//					titolName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE));		//playingItem.title;		//曲名
					dbMsg +=" ,タイトル= " + titolName;/////////////////////////////////////		this.title = title;
//					int audioId = Integer.parseInt(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)));
					dbMsg +=" ,audioId= " + audioId;
//				albumArtist = musicPlaylist.getAlbumArtist(audioId , MaraSonActivity.this);	//playingItem.album_artist;	//アルバムアーティスト名
//				dbMsg +=" ,アルバムアーティスト= " + albumArtist;
//					Uri dataUri = Uri.parse(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA)));
//					dataFN = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA));
					dbMsg +=",読込確認；dataFN=" +dataFN;/////////////////////////////////////
					if( ! yomiKomiCheck(dataFN)){
						dbMsg +=">>次の曲へ" ;/////////////////////////////////////
						mIndex++;
						playNextSong(false);					// If we're stopped, just go ahead to the next song and start playing
					}

					dbMsg += "、mState=" +mState;/////////////////////////////////////
					relaxResources(true);				//trueで再生しているmPlayerの破棄 // release everything except MediaPlayer
					createMediaPlayerIfNeeded();
					dbMsg += "、mPlayer=" +mPlayer;/////////////////////////////////////
					try {
						mPlayer.setLooping(true);						//☆以下二つより先に行わないとIllegalStateExceptionが発生
						mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
						mPlayer.setDataSource(getApplicationContext(), Uri.parse(dataFN));				//☆MediaPlayer.createではjava.lang.IllegalStateException		http://umezo.hatenablog.jp/entry/20100531/1275319329
						dbMsg += ";データセット；";						// +mPlayer.getDuration() + "[ms]";			/////////////////////////////////////
						mPlayer.prepareAsync();							//データを非同期で読み込み、読み込み完了と同時にonCompletionを実行
						siseizumiDataFN = dataFN;				//前回再生済みのファイル名
					} catch (IllegalArgumentException e) {
						//ダイアログならdismissメソッドを呼び出す時に、ダイアログを表示したアクティビティが破棄されているのが原因
						myErrorLog(TAG,dbMsg+"で"+e);
						onPrepared( mPlayer);
					} catch (IllegalStateException e) {
						myErrorLog(TAG,dbMsg+"で"+e);
					}

//				}else{
//					Toast.makeText(this, "No available music to play. Place some music on your external storage device (e.g. your SD card) and try again.", Toast.LENGTH_LONG).show();
//					myLog(TAG,dbMsg);
//					processStopRequest(true); // stop everything!
//					return;
//				}
//				playingItem.close();
				*/


			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	/**
	 * MediaPlayer生成
	 * Should have subtitle controller already set対策
	 * http://stackoverflow.com/questions/20087804/should-have-subtitle-controller-already-set-mediaplayer-error-android
	 * */
	static MediaPlayer getMediaPlayer(Context context , String pref_data_url){
		final String TAG = "getMediaPlayer[MusicPlayerService]";
		String dbMsg="開始";/////////////////////////////////////
		MediaPlayer mediaplayer = null;
		try{
			dbMsg += "context="  + context;//////////////////////////////
			mediaplayer = MediaPlayer.create(context, Uri.parse(pref_data_url));
			//	mediaplayer = new MediaPlayer();
		//	mediaplayer.prepare();
//			mediaplayer.setOnCompletionListener((OnCompletionListener) context);
			dbMsg +=",mediaplayer="  + mediaplayer.getTrackInfo();
//			if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
//				return mediaplayer;
//			}
			if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
				dbMsg +=",SDK_INT="  + android.os.Build.VERSION_CODES.KITKAT;//////////////////////////////
				try {
					Class<?> cMediaTimeProvider = Class.forName( "android.media.MediaTimeProvider" );
					Class<?> cSubtitleController = Class.forName( "android.media.SubtitleController" );
					Class<?> iSubtitleControllerAnchor = Class.forName( "android.media.SubtitleController$Anchor" );
					Class<?> iSubtitleControllerListener = Class.forName( "android.media.SubtitleController$Listener" );
					Constructor constructor = cSubtitleController.getConstructor(Context.class, cMediaTimeProvider, iSubtitleControllerListener);
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
					dbMsg += "subtitle is setted :p";//////////////////////////////
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
			dbMsg += "、g_timer=" + g_timer;/////////////////////////////////////
			dbMsg += "、g_handler=" + g_handler;/////////////////////////////////////
			player.setLooping(false);		//ループ再生はしない
			changeCount((ExoPlayer) player);						//シークバーの逐次更新；タイマーオブジェクトを使ったカウンタ更新を追加
			myLog(TAG,dbMsg);
			sendPlayerState((ExoPlayer) mPlayer);					//ここまでの設定結果をBroad
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
		final String TAG = "createMediaPlayerIfNeeded";
		String dbMsg = "[MusicPlayerService]";
		dbMsg += ORGUT.nowTime(true,true,true);/////////////////////////////////////
		try {
			dbMsg += "mPlayer = " + exoPlayer;
			int mcPosition = 0;
			if (exoPlayer != null) {								//MediaPlayerが生成されていれば
				if(exoPlayer.isPlaying()){						//再生中なら
					exoPlayer.setPlayWhenReady(false);							//止める☆stopだとprepareAsync called in state 1が発生する？
					mcPosition = (int) exoPlayer.getCurrentPosition();
					setPrefInt("pref_position" , mcPosition, MusicPlayerService.this);  //sharedPref.getInt("pref_position" , 0);
				}
		//		exoPlayer.reset();									//MediaPlayerを抹消
				/*状態が「Idle」に移行。 「Preparing」「End」以外の状態で呼び出すことが可能です。
				 * 次のデータを再生したい場合は、 とにかく「Idle」に戻って 、データの読み込みからやり直す*/
				exoPlayer = null;
				createMediaPlayerIfNeeded();				//再起して作り直し
			} else {
				exoPlayer = (ExoPlayer) getMediaPlayer( this.getApplicationContext() , myPreferences.pref_data_url);			//			?rContext
				if (21 <= android.os.Build.VERSION.SDK_INT  ) {
				}else if (14 <= android.os.Build.VERSION.SDK_INT  ){
//					makeLockScreenP( exoPlayer );					//ロックスクリーン作成 < songInfoSett
				}
//				// Make sure the media player will acquire a wake-lock while playing. If we don't do that, the CPU might go to sleep while the song is playing, causing playback to stop.
//				// Remember that to use this, we have to declare the android.permission.WAKE_LOCK permission in AndroidManifest.xml.
//				mPlayer.setOnPreparedListener(MusicPlayerService.this);	// we want the media player to notify us when it's ready preparing, and when it's done playing:
//				mPlayer.setOnCompletionListener(MusicPlayerService.this);   //曲の終了を感知する
////				mPlayer.setOnErrorListener(this);
////				mPlayer.setLooping(true);						//20150321
				dbMsg +=">> " + exoPlayer;//////////////////////////////////
		//		mPlayer.setAuxEffectSendLevel(1.0f);					//効果のレベル；デフォルトでは、センドレベルはは0.0fなので必要
				dbMsg +=",equalizerSet=" + equalizerSet;/////////////////////////////////////
				if(! equalizerSet){
					dbMsg +=",pref_toneList=" + pref_toneList;/////////////////////////////////////
					if( pref_toneList == null ){
						getEqualizer( );					//初期Equalizer情報の取得
					}
					dbMsg +=">equalizerSet>" + equalizerSet;/////////////////////////////////////
				}
				dbMsg +=",mBassBoost=" + mBassBoost;/////////////////////////////////////
				if(mBassBoost == null){
					bassBoostBody( bBoot );		//ベースブーストOn/Off本体
					dbMsg +=">>" + mBassBoost;/////////////////////////////////////
				}
				dbMsg +=",reverbBangou=" + reverbBangou;/////////////////////////////////////
				if(0< reverbBangou){
					dbMsg +=",mPresetReverb=" + mPresetReverb;/////////////////////////////////////
					if( mPresetReverb == null){
						presetReverbBody(reverbBangou);					//リバーブ設定本体
						dbMsg +=">>" + mPresetReverb;/////////////////////////////////////
					}
				}
				dbMsg +=",mVisualizer=" + mVisualizer;/////////////////////////////////////
				mVisualizer = setupVisualizer( mVisualizer);
				dbMsg +=">>" + mVisualizer;/////////////////////////////////////
			}
			if( rp_pp ){						//2点間リピート中で//リピート区間終了点
				mcPosition =pp_start;									//前に再生していた曲の再生ポジションを消去
			}
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		//Not Using QCMediaPlayer,setting qc_post_event to NULL...あなたのプラットフォームがQCMediaPlayerをサポートしていない
	}

	public Timer g_timer = null;
	public Handler g_handler = new Handler();
	public int kankaku;
	public long stTime;
	MediaPlayer rPlayer;

	/**タイマーオブジェクトを破棄*/
	public void stopCount(){
	final String TAG = "stopCount";
	String dbMsg="";
		try{
			dbMsg +=",タイマー設定；g_timer="+g_timer;/////////////////////////////////////
	  		if(g_timer != null){
	  			g_timer.cancel(); 		// タイマの設定
	  			g_timer = null;
				dbMsg +=">>"+g_timer;/////////////////////////////////////
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
	public void changeCount(ExoPlayer player){		//タイマーオブジェクトを使ったカウンタ更新←playNextSong  , configAndStartMediaPlayer
	final String TAG = "changeCount";
	String dbMsg="開始";/////////////////////////////////////
		try{
			kankaku = 100;
			stTime = System.currentTimeMillis()  ;
			dbMsg += ","+ stTime + "mS ";/////////////////////////////////////
//			rPlayer= player;
			dbMsg +=",タイマー設定；g_timer="+g_timer;/////////////////////////////////////
	  		if(g_timer == null){
	  			g_timer = new Timer(true);  		// タイマの設定
				dbMsg +=">>"+g_timer;/////////////////////////////////////
	  		}
	  		if( player != null ){
				timertask = new TimerTask() {
				@Override
				public void run() {
					g_handler.post( new Runnable() {
						public void run() {
							final String TAG = "changeCount.run";
							String dbMsg="[MusicPlayerService.changeCount.MusicPlayerService]";
							try {
								Intent intent = new Intent(ACTION_STATE_CHANGED);
								dbMsg +=",action=" + action ;
								int mcPosition = 0;
								if( exoPlayer == null ){
									mcPosition = getPrefInt("pref_position" , 0 , MusicPlayerService.this);
									saiseiJikan = getPrefInt("pref_duration" , 0 , MusicPlayerService.this);
									if(rp_pp){						//2点間リピート中で//リピート区間終了点
										mcPosition = pp_start;		//リピート区間開始点
									}
									IsPlaying  = false ;			//再生中か
									IsSeisei  = false ;				//生成中
								} else {
									mcPosition = (int) exoPlayer.getCurrentPosition();
									saiseiJikan = (int) exoPlayer.getDuration();
									IsPlaying  = exoPlayer.isPlaying() ;			//再生中か
									IsSeisei  = true ;				//生成中
								}
								dbMsg += "[" + mcPosition + "/"+saiseiJikan + "]";

								dbMsg += " ,SDK_INT="+android.os.Build.VERSION.SDK_INT;
								if(31<= android.os.Build.VERSION.SDK_INT) {
//									if(mNotificationManager != null){
//										int ppIcon = android.R.drawable.ic_media_pause;                //getApplicationContext().getResources().getInteger(android.R.drawable.ic_media_pause);
//										String ppTitol = "pause";
//										if (exoPlayer.isPlaying()) {        //(mState == State.Paused || mState == State.Stopped
//											ppIcon = android.R.drawable.ic_media_pause;
//											ppTitol = "pause";
//										}else{
//											ppIcon = android.R.drawable.ic_media_play;
//											ppTitol = "play";
//										}
//										dbMsg += ",ppTitol = " + ppTitol;
//
////										ppAction = new NotificationCompat.Action(ppIcon,ppTitol,ppIntent);
////										builder.addAction(ppAction);
////										notificationManager.notify(NOTIFICATION_ID, builder.build());
//									}
								}

								int nokori = saiseiJikan - mcPosition-kankaku;



								if( ( nokori <= crossFeadTime
//											|| (rp_pp && pp_end < mcPosition)					//2点間リピート中で//リピート区間終了点
								)){			//	&&  (Build.VERSION.SDK_INT <16)
									if(exoPlayer.isPlaying()){
										dbMsg +="," + titolName +"再生中の残" + nokori + "/" + crossFeadTime + "mSで次曲へ";/////////////////////////////////////
										myLog(TAG,dbMsg);
										onCompletion((MediaPlayer) exoPlayer);		/** 再生中にデータファイルのENDが現れた場合にコールCalled when media player is done playing current song. */
										return;
									}
								} else {							// if( nokori <= crossFeadTime-100  )
									imanoJyoutai =  MuList.sonomama ;
									onCompletNow = false;			//曲間処理中
									if((kankaku * 3 > nokori)  &&  (1 < kankaku)){						//
										dbMsg +=  ",間隔= " +kankaku  ;/////////////////////////////////////
										kankaku = kankaku/2;
										dbMsg +=">>" +kankaku  ;/////////////////////////////////////
									}
								}
//								if( action.equals(MusicPlayerService.ACTION_SKIP) || action.equals(MusicPlayerService.ACTION_REWIND)){
//									long sTime = System.currentTimeMillis() - stTime ;
//									dbMsg += ",sTime="+ sTime + "mS後[ "+ stTime+"]";/////////////////////////////////////
//									if( 1000 < sTime){
//										dbMsg +="のfrCount= " + MusicPlayerService.this.frCount;/////////////////////////////////////
//							//			okuriMpdosi(MusicPlayerService.this.frCount);		//送り戻しの実行
//										MusicPlayerService.this.frCount = 0;
//									}
//								} else {
									intent.putExtra("mcPosition", mcPosition);
									ruikeikasannTime = mcPosition;			//累積加算時間
									intent.putExtra("saiseiJikan", saiseiJikan);
									intent.putExtra("mIndex", mIndex);
									intent.putExtra("listEnd", listEnd);
									dbMsg +=",生成中= " + IsSeisei;//////////////////////////////////
									intent.putExtra("IsSeisei", IsSeisei);
									dbMsg +=",再生中か= " + IsPlaying;//////////////////////////////////
									intent.putExtra("IsPlaying", IsPlaying);
									intent.putExtra("data", myPreferences.pref_data_url.toString());
									sendBroadcast(intent);					//APIL1
									dbMsg +=";Broadcast送信";
	//								dbMsg +="mBluetoothAdapter=" +mBluetoothAdapter;
									if(stateBaseStr != null && b_stateStr != null){
										if( ! b_stateStr.equals(stateBaseStr)){
											dbMsg +="stateBaseStr=" +stateBaseStr;
											intent.putExtra("stateBaseStr", stateBaseStr);
											b_stateStr = stateBaseStr;
										}
									}
//								}
//								myLog(TAG,dbMsg);
							} catch (Exception e) {
								myErrorLog(TAG,dbMsg +"で"+e.toString());
							}
						} //run
					});
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
	@SuppressLint("Range")
	/**再生中にデータファイルのENDが現れた場合にコールされる*/
	public void onCompletion(MediaPlayer player) {
	//☆曲の終了でも発生する
		final String TAG = "onCompletion";
		String dbMsg = "ENDマーク検出から";/////////////////////////////////////
		try{
			tunagiJikan = System.currentTimeMillis();		// 開始時刻の取得

			dbMsg += "[nowList_id=" + myPreferences.nowList_id;
			dbMsg += "]" + myPreferences.nowList;
			dbMsg += "[" + mIndex;
			listEnd =  plSL.size();
			dbMsg += "/" + listEnd;
			dbMsg += ":" + myPreferences.pref_data_url;
			dbMsg += ">>" + NextDataFN;
			nextIndex = mIndex + 1;
			if(listEnd < nextIndex){
				nextIndex = 0;
			}
			playNextSong(nextIndex,true);
////			Cursor playingItem = musicPlaylist.getPlaylistItems(myPreferences.nowList_id,mIndex);
////			if(playingItem.moveToFirst()){
////				creditArtistName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST));		//playingItem.artist;	//クレジットされているアーティスト名
////				dbMsg +=" ,クレジット⁼ " + creditArtistName;
////				albumName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM));		//playingItem.album;			//アルバム名
////				dbMsg +=" , アルバム⁼" + albumName;/////////////////////////////////////	this.album = album;
////				titolName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE));		//playingItem.title;		//曲名
////				dbMsg +=" ,タイトル= " + titolName;/////////////////////////////////////		this.title = title;
////				int audioId = Integer.parseInt(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)));
////				dbMsg +=" ,audioId= " + audioId;
//////				albumArtist = musicPlaylist.getAlbumArtist(audioId , MaraSonActivity.this);	//playingItem.album_artist;	//アルバムアーティスト名
//////				dbMsg +=" ,アルバムアーティスト= " + albumArtist;
////				Uri dataUri = Uri.parse(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA)));
////				String myPreferences.pref_data_url = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA));
////				//String.valueOf(dataUri);
////				dbMsg +=",読込確認=" + myPreferences.pref_data_url;/////////////////////////////////////
////				int duration = Integer.parseInt(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.DURATION)));
//////						(int)playingItem.duration;
////				dbMsg += ",duration = " + duration + "[ms]";
//
////				if(g_timer != null){
////					g_timer.cancel();
////				}
////				g_timer =null;			//☆毎回破棄しないとChoreographer: Skipped ○○」 frames!  The application may be doing too much work on its main thread.
////				dbMsg += "、mIndex=" + mIndex;/////////////////////////////////////
////				mIndex ++;
////				if( listEnd < mIndex){
////					mIndex= 1;
////				}
////				dbMsg += "、次は" + mIndex;
////				ruikeikyoku++;			//累積曲数
////				dbMsg += ",累積曲数" + ruikeikyoku +"曲";
////				int mcPosition = 0;
////				if(rp_pp){						//2点間リピート中で//リピート区間終了点
////					mcPosition = pp_start;		//リピート区間開始点
////					mIndex--;
////				}
////				dbMsg += ">>[" + mIndex +"]";
////				playingItem = musicPlaylist.getPlaylistItems(myPreferences.nowList_id,mIndex);
////				if(playingItem.moveToFirst()) {
////					creditArtistName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST));        //playingItem.artist;	//クレジットされているアーティスト名
////					dbMsg += " ,クレジット⁼ " + creditArtistName;
////					albumName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM));        //playingItem.album;			//アルバム名
////					dbMsg += " , アルバム⁼" + albumName;/////////////////////////////////////	this.album = album;
////					titolName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE));        //playingItem.title;		//曲名
////					dbMsg += " ,タイトル= " + titolName;/////////////////////////////////////		this.title = title;
////					audioId = Integer.parseInt(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)));
////					dbMsg += " ,audioId= " + audioId;
////					dataUri = Uri.parse(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA)));
////					myPreferences.pref_data_url = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA));
////					dbMsg +=",読込確認=" + myPreferences.pref_data_url;/////////////////////////////////////
////					duration = Integer.parseInt(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.DURATION)));
////					dbMsg += ",duration = " + duration + "[ms]";
////					setPrefStr("myPreferences.pref_data_url" , myPreferences.pref_data_url, MusicPlayerService.this);  //sharedPref.getInt("pref_position" , 0);
////					setPrefInt("pref_duration" , duration, MusicPlayerService.this);  //sharedPref.getInt("pref_position" , 0);
////					setPrefInt("pref_position" , mcPosition, MusicPlayerService.this);  //sharedPref.getInt("pref_position" , 0);
////
////					if( tunagiJikan > 0 ){
////						tunagiJikan = System.currentTimeMillis()-tunagiJikan;		// 開始時刻の取得
////						dbMsg +=",前曲から" +tunagiJikan +"mS経過、," ;/////////////////////////////////////
////						dbMsg +=mPlayer.toString()  ;/////////////////////////////////////
////					}
////				}
////			}
////			playingItem.close();
			dbMsg += ">自動送り>" + myPreferences.pref_data_url;
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	//	http://seesaawiki.jp/w/moonlight_aska/d/%BA%C6%C0%B8%A4%AC%BD%AA%CE%BB%A4%B9%A4%EB%A4%C8....
	}

	/**playNextSongに続いて データ詠み込み後の処理**/
	public void onPrepared(MediaPlayer player) {					//	Called when media player is done preparing
		final String TAG = "onPrepared[MusicPlayerService]";
		String dbMsg="これから再生、";/////////////////////////////////////
		try{
			dbMsg += "渡されたplayer=" +player ;/////////////////////////////////////
			dbMsg +=",既存のplayer=" +player ;/////////////////////////////////////
			if( player == exoPlayer ){
				imanoJyoutai =  MuList.sonomama ;
				dbMsg=dbMsg+ "、isPlaying=" + player.isPlaying() ;/////////////////////////////////////
				if( !player.isPlaying() ){
					player.setVolume(DUCK_VOLUME, DUCK_VOLUME); 	//0.1f; we'll be relatively quiet
					dbMsg += ",2点間リピート中=" + rp_pp ;/////////////////////////////////////
					int mcPosition = getPrefInt("pref_position" , 0, MusicPlayerService.this);  //sharedPref.getInt("pref_position" , 0);
					if( rp_pp ){						//2点間リピート中の時だけ//リピート区間終了点
						mcPosition =pp_start;			//前に再生していた曲の再生ポジションを消去
					}
					dbMsg += ",mcPosition=" + mcPosition ;//////////////////////////////////
					if( 0 < mcPosition ){
						player.seekTo(mcPosition);
					}
					kankaku = 100;
					player.start();				//java.lang.SecurityException: Neither user 10859 nor current process has android.permission.WAKE_LOCK.
					player.setVolume(1.0f, 1.0f); // we can be loud
					mState = State.Playing;			//	20150504	mState = State.Preparing;
					dbMsg += ">>" + player.isPlaying() ;/////////////////////////////////////
				}
				songInfoSett( player);
				setPref();		//プリファレンス記載
			}
			dbMsg +=",状態=" + imanoJyoutai ;/////////////////////////////////////
		//	myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	///APIL21対応////////////////////////////////////////////////////////////////////////////////////////////////
	//private static final String ACTION_TOGGLE_PLAYBACK = "com.your.package.name.TOGGLE_PLAYBACK";
	//private static final String ACTION_PREV = "com.your.package.name.PREV";
	//private static final String ACTION_NEXT = "com.your.package.name.NEXT";
	/**SDK_INT31以降のノティフィケーションビルダ*/
	private NotificationCompat.Builder builder;
	/**ノテフィケーションから起動するアクティビティ*/
	public Intent notifyIntent;
	public PendingIntent pendingIntent;
	/**play/pouseボタン*/
	private PendingIntent ppIntent;
	private NotificationCompat.Action ppAction;
	private NotificationCompat.Action pp2Play;
	private NotificationCompat.Action pp2Pouse;
	private NotificationManagerCompat notificationManager;
	private String channelId = "default";
	private androidx.media.app.NotificationCompat.MediaStyle NotifStyle;

	/**ノティフィケーションインスタンス*/
//	public Notification lpNotification;
	public TransportControls lpNControls;
	public Intent intentNR;


	public MediaMetadata.Builder metadataBuilder;
//	private PlaybackStateCompat.Builder stateBuilder;
	/**ノティフィケーションマネージャー*/
//	private NotificationManager mNotificationManager;			//
//	private NotificationChannel mNotificationChannel;
//	private Notification.Builder nBuilder;
	/**ノティフィケーションインスタンス*/
//	private Notification mNotification = null;
	final int NOTIFICATION_ID = 1;						//☆生成されないので任意の番号を設定する	 The ID we use for the notification (the onscreen alert that appears at the notification area at the top of the screen as an icon -- and as text as well if the user expands the notification area).
	private RemoteViews ntfViews;						//ノティフィケーションのレイアウト

//	//MediaSessionCompat
//	MediaSession.Callback nfCallback = new MediaSession.Callback() {
//				@Override
//				public void onPlay() {
//					final String TAG = "onPlay";
//					String dbMsg = "[nfCallback]";
//					try {
//						if(mPlayer != null){
//							dbMsg += ",isPlaying=" + mPlayer.isPlaying();
//							if(! mPlayer.isPlaying()){
////					AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
////					// Request audio focus for playback, this registers the afChangeListener
////					AudioAttributes attrs = new AudioAttributes.Builder()
////							.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
////							.build();
////					audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
////							.setOnAudioFocusChangeListener(afChangeListener)
////							.setAudioAttributes(attrs)
////							.build();
////					int result = am.requestAudioFocus(audioFocusRequest);
////
////					if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
////						// Start the service
////						startService(new Intent(getApplication(), MediaBrowserService.class));
////						// Set the session active  (and update metadata and state)
////						mSession.setActive(true);
////						// start the player (custom call)
//								mPlayer.start();
////						// Register BECOME_NOISY BroadcastReceiver
////						registerReceiver(myNoisyAudioStreamReceiver, intentFilter);
////						// Put the service in the foreground, post notification
////						service.startForeground(id, myPlayerNotification);
//							}
//						}else{
//							mPlayer.pause();
//						}
//						dbMsg += ">>" + mPlayer.isPlaying();
//						myLog(TAG, dbMsg);
//					} catch (Exception e) {
//						myErrorLog(TAG, dbMsg + "で" + e);
//					}
////						player.start();
////					}
//				}
//
//				@Override
//				public void onStop() {
//					final String TAG = "onStop";
//					String dbMsg = "[nfCallback]";
//					try {
//						if(mPlayer != null){
//							dbMsg += ",isPlaying=" + mPlayer.isPlaying();
//							if( mPlayer.isPlaying()){
//								//					AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
////					// Abandon audio focus
////					am.abandonAudioFocusRequest(audioFocusRequest);
////					unregisterReceiver(myNoisyAudioStreamReceiver);
////					// Stop the service
////					service.stopSelf();
////					// Set the session inactive  (and update metadata and state)
////					mSession.setActive(false);
////					// stop the player (custom call)
//								mPlayer.stop();
//								dbMsg += ">>" + mPlayer.isPlaying();
//								//					// Take the service out of the foreground
////					service.stopForeground(false);
//							}
//						}
//						myLog(TAG, dbMsg);
//					} catch (Exception e) {
//						myErrorLog(TAG, dbMsg + "で" + e);
//					}
//				}
//
//				@Override
//				public void onPause() {
//					final String TAG = "onPause";
//					String dbMsg = "[nfCallback]";
//					try {
//						if(mPlayer != null){
//							dbMsg += ",isPlaying=" + mPlayer.isPlaying();
//							if( mPlayer.isPlaying()){
//								//					AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
////					// Update metadata and state
////					// pause the player (custom call)
//								mPlayer.pause();
////					// unregister BECOME_NOISY BroadcastReceiver
////					unregisterReceiver(myNoisyAudioStreamReceiver);
////					// Take the service out of the foreground, retain the notification
////					service.stopForeground(false);
//								dbMsg += ">>" + mPlayer.isPlaying();
//							}
//						}
//						myLog(TAG, dbMsg);
//					} catch (Exception e) {
//						myErrorLog(TAG, dbMsg + "で" + e);
//					}
//				}
//			};

	/**play/pauseアイコン切り替え*/
	public NotificationCompat.Action ppIconChaange() {
		final String TAG = "ppIconChaange";
		String dbMsg = "";
		NotificationCompat.Action retAction = null;
		try {
			intentNR.setAction(ACTION_PLAYPAUSE);
			intentNR.putExtra("RequestCode",MyConstants.ACTION_CODE_PLAYPAUSE);
			ppIntent = PendingIntent.getBroadcast(getApplicationContext(), MyConstants.ACTION_CODE_PLAYPAUSE, intentNR,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
			int ppIcon = android.R.drawable.ic_media_pause;
			if(exoPlayer != null){
				dbMsg += ",isPlaying=" + exoPlayer.isPlaying();
				if(! exoPlayer.isPlaying()){
					ppIcon = android.R.drawable.ic_media_play;
					//exoPlayer.start();
				}else {
					//	exoPlayer.pause();
				}
				dbMsg += ">>" + exoPlayer.isPlaying();
			}else{
				dbMsg += "exoPlayer = null";
				ppIcon = android.R.drawable.ic_media_play;
			}
			retAction = new NotificationCompat.Action(ppIcon,ACTION_PLAYPAUSE,ppIntent);
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG, dbMsg + "で" + e);
		}
		return retAction;
	}

	/**
	 * sendPlayerStateから呼出しボタンをアップする度に呼び出される
	 * ？ロックスクリーンでは呼び出されない？
	 * http://stackoverflow.com/questions/27209596/media-style-notification-not-working-after-update-to-android-5-0
	 * 	retreivePlaybackActionはnoti作成時に動作してしまうので
	 * 		のprivate Notification.Action generateAction( int icon, String title, String intentAction ) を使用
	 * 	Using Android Media Style notifications with Media Session controls										https://www.binpress.com/tutorial/using-android-media-style-notifications-with-media-session-controls/165
	 * Android Wear に Notification で出来ること									http://y-anz-m.blogspot.jp/2014/07/android-wear-notification.html
	 * ロック画面でのメディア再生をコントロールする					http://developer.android.com/intl/ja/guide/topics/ui/notifiers/notifications.html#controllingMedia
	 * 呼出し元    sendPlayerState
	  */
	@SuppressLint("NewApi")
//	public void lpNotificationMake(String dataFN) {
//		final String TAG = "lpNotificationMake";
//		String dbMsg="";
//		try{
//			dbMsg += "dataFN="+dataFN;
//			String title = getApplicationContext().getString(R.string.app_name);
//			notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//			dbMsg += " ,notifyIntent="+notifyIntent.getClass().getName();		//android.content.Intent
//			if(pendingIntent == null){
//				pendingIntent = PendingIntent.getActivity(
//						getApplicationContext(), 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
//								| PendingIntent.FLAG_IMMUTABLE
//				);
//			}
//			dbMsg += " ,pendingIntent="+pendingIntent.getClass().getName();				//android.app.PendingIntent
//			notificationManager = NotificationManagerCompat.from(this);
//			dbMsg += " ,Notification　Channel 設定";
//			mNotificationChannel = new NotificationChannel(String.valueOf(NOTIFICATION_ID), title, NotificationManager.IMPORTANCE_DEFAULT);
//			dbMsg += " ,通知音を消す";
//			mNotificationChannel.setSound(null,null);			//setSound(Uri.parse("android.resource://" + packageName + "/" + R.raw.sample), null)
//			notificationManager.createNotificationChannel(mNotificationChannel);
//			if (notificationManager != null) {
//				notificationManager.createNotificationChannel(mNotificationChannel);
//			}
//			dbMsg += " ,SDK_INT="+android.os.Build.VERSION.SDK_INT;
//			if(31<= android.os.Build.VERSION.SDK_INT){
//				if(metadataBuilder == null){
//					dbMsg += " , metadataBuilder == null";
//					getDataInfo(dataFN);
//				}
//				if(mSession ==null){
//					dbMsg += " , mSession == null";
//					mSession = new MediaSessionCompat(getApplicationContext(),  getResources().getString(R.string.app_name));
//				}
//
//				dbMsg += " , DURATION=" + metadataBuilder.build().getLong(MediaMetadata.METADATA_KEY_DURATION);
//				// Get the session's metadata
//			//	MediaController controller = MediaSessionCompat.getController();
//				NotificationCompat.Builder builder = new NotificationCompat.Builder(this, String.valueOf(NOTIFICATION_ID));
//				// タイトルなどの表示
//				mSession.setMetadata(MediaMetadataCompat.fromMediaMetadata(metadataBuilder.build()));
//			///	mSession.setCallback(nfCallback);
//				dbMsg += " Notification そのものをタップしたとき="+pendingIntent.getCreatorUid();
//				builder.setContentIntent(pendingIntent);
//				builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
//
//	//			NotifStyle = new androidx.media.app.NotificationCompat.MediaStyle();
//				intentNR = new Intent(getApplicationContext(), NotifRecever.class);			//MusicPlayerService.this
//
//				intentNR.setAction(ACTION_REWIND);
//				intentNR.putExtra("RequestCode",MyConstants.ACTION_CODE_REWINDE);
//				PendingIntent rewIntent = PendingIntent.getBroadcast(getApplicationContext(), MyConstants.ACTION_CODE_REWINDE, intentNR,PendingIntent.FLAG_UPDATE_CURRENT |  PendingIntent.FLAG_IMMUTABLE);
//				NotificationCompat.Action rewAction = new NotificationCompat.Action(android.R.drawable.ic_media_rew,ACTION_REWIND,rewIntent);
//
//				intentNR.setAction(ACTION_PLAYPAUSE);
//				intentNR.putExtra("RequestCode",MyConstants.ACTION_CODE_PLAYPAUSE);
//				ppIntent = PendingIntent.getBroadcast(getApplicationContext(), MyConstants.ACTION_CODE_PLAYPAUSE, intentNR,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//				playing=false;
//				if(exoPlayer != null){
//					if(exoPlayer.isPlaying()) {
//						playing=true;
//					}
//				}
//				int ppIcon = playing ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play;
//				ppAction = new NotificationCompat.Action(ppIcon,ACTION_PLAYPAUSE,ppIntent);
//				//getApplicationContext().getResources().getInteger(android.R.drawable.ic_media_pause);
////				String ppTitol = "pause";
//
//				pp2Pouse = new NotificationCompat.Action(android.R.drawable.ic_media_pause, ACTION_PLAYPAUSE, ppIntent);
//				pp2Play = new NotificationCompat.Action(android.R.drawable.ic_media_play, ACTION_PLAYPAUSE, ppIntent);
//
//				intentNR.setAction(ACTION_SKIP);
//				intentNR.putExtra("RequestCode",MyConstants.ACTION_CODE_SKIP);
//				PendingIntent ffIntent = PendingIntent.getBroadcast(getApplicationContext(), MyConstants.ACTION_CODE_SKIP, intentNR,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//				NotificationCompat.Action ffAction = new NotificationCompat.Action(android.R.drawable.ic_media_ff,ACTION_SKIP,ffIntent);
//
//				intentNR.setAction(ACTION_SYUURYOU_NOTIF);
//				intentNR.putExtra("RequestCode",MyConstants.ACTION_CODE_quit);
//				PendingIntent quitIntent = PendingIntent.getBroadcast(getApplicationContext(), MyConstants.ACTION_CODE_quit, intentNR,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//				NotificationCompat.Action quitAction = new NotificationCompat.Action(android.R.drawable.ic_lock_power_off,ACTION_SYUURYOU_NOTIF,quitIntent);
//
//				// 左から順に並びます
//				builder.addAction(rewAction);
//				builder.addAction(ppAction);
////				builder.addAction(ppIconChaange());
////				builder.addAction(pp2Pouse);
////				builder.addAction(pp2Play);
//				builder.addAction(ffAction);
//				builder.addAction(quitAction);
////				if (mPlayer != null) {
////					dbMsg += ",isPlaying = " + mPlayer.isPlaying();
////					if (! mPlayer.isPlaying()) {        //(mState == State.Paused || mState == State.Stopped
////						NotifStyle.setShowActionsInCompactView(2);
////					}else{
////						NotifStyle.setShowActionsInCompactView(1);
////					}
////				}else{
////					NotifStyle.setShowActionsInCompactView(1);
////				}
////				builder.setStyle(NotifStyle);
//
//				builder.setSmallIcon(R.drawable.ic_launcher_notif);
//				dbMsg += ",setStyle";
//				builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
//						//		.setMediaSession(MediaSessionCompat.Token.fromToken(mSession.getSessionToken()))
//								.setShowActionsInCompactView(1)			//0,1,3,4
//				);
//
//				dbMsg += " Notification型のインスタンス生成";
//				lpNotification = builder.build();
//				notificationManager.notify(NOTIFICATION_ID, lpNotification);
//				startForeground(NOTIFICATION_ID, lpNotification);
//			}else {
////				Drawable drawable = getApplicationContext().getResources().getDrawable(R.drawable.no_image);
////				Bitmap artwork = BitmapFactory.decodeResource(getResources(), R.drawable.no_image);
////				dbMsg += ",albumArt =" + album_art;
////					nBuilder = new Notification.Builder(getApplicationContext(), channelId);
////					PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_MUTABLE);        //http://y-anz-m.blogspot.jp/2011/07/androidappwidget-pendingintent-putextra.html
////					if (mSession != null) {
////						mSession.release();
////						mSession = null;
////					}
////					//https://sites.google.com/site/buildingappsfortv/displaying-a-now-playing-card
////					MediaMetadata.Builder metadataBuilder = new MediaMetadata.Builder();        // To provide most control over how an item is displayed set the display fields in the metadata
////					metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, titolName);
////					metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, albumName + " / " + creditArtistName);
////					metadataBuilder.putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI, album_art);
////					metadataBuilder.putString(MediaMetadata.METADATA_KEY_TITLE, titolName);        // And at minimum the title and artist for legacy support
////					metadataBuilder.putString(MediaMetadata.METADATA_KEY_ALBUM, albumName);
////					metadataBuilder.putString(MediaMetadata.METADATA_KEY_ARTIST, creditArtistName);
////					metadataBuilder.putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, album_art);        // A small bitmap for the artwork is also recommended
////					metadataBuilder.putBitmap(MediaMetadata.METADATA_KEY_ART, artwork);        //これが無いとロックスクリーンの背景にならない？ A small bitmap for the artwork is also recommended
////					mSession.setMetadata(metadataBuilder.build());        // Add any other fields you have for your data as well
////					mSession.setFlags(mSession.FLAG_HANDLES_TRANSPORT_CONTROLS);                // このフラグが有るカードだけが表示される	Indicate you want to receive transport controls via your Callback
////					mSession.setCallback(new mSession.Callback() {                                    // Attach a new Callback to receive mSession updates
////						@Override
////						public void onPlay() {
////							final String TAG = "onPlay";
////							String dbMsg = "[mSession.Callback]";
////							try {
////								processTogglePlaybackRequest();
////								myLog(TAG, dbMsg);
////							} catch (Exception e) {
////								myErrorLog(TAG, dbMsg + "で" + e);
////							}
////						}
////
////						@Override
////						public void onPlayFromMediaId(String mediaId, Bundle extras) {            // リスト選択時にコールされる
////							final String TAG = "onPlay";
////							String dbMsg = "[mSession.Callback]";
////							try {
////								// MediaIDを元に曲のインデックス番号を検索し、設定する。
////								myLog(TAG, dbMsg);
////							} catch (Exception e) {
////								myErrorLog(TAG, dbMsg + "で" + e);
////							}
////						}
////
////						@Override
////						public void onSkipToNext() {                // 再生キューの位置を次へ
////							final String TAG = "onSkipToNext";
////							String dbMsg = "[mSession.Callback]";
////							try {
////								myLog(TAG, dbMsg);
////							} catch (Exception e) {
////								myErrorLog(TAG, dbMsg + "で" + e);
////							}
////						}
////
////						@Override
////						public void onSkipToPrevious() {                // 再生キューの位置を前へ
////							final String TAG = "onSkipToPrevious";
////							String dbMsg = "[mSession.Callback]";
////							try {
////								myLog(TAG, dbMsg);
////							} catch (Exception e) {
////								myErrorLog(TAG, dbMsg + "で" + e);
////							}
////						}
////					});
////
////					nBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);                                                        //2016050:通知にメディア再生コントロールを表示	http://developer.android.com/intl/ja/about/versions/android-5.0.html アプリで RemoteControlClient を使用する場合
////					nBuilder.setShowWhen(false);                                                                    // Hide the timestamp
////					nBuilder.setStyle(new Notification.MediaStyle()                                                // Set the Notification style☆RemoteViews.RemoteViewから変更					new Notification.MediaStyle()
////							.setMediaSession(mSession.getSessionToken())                                    // Attach our MediaSession token
////							.setShowActionsInCompactView(0, 1, 2));                                                // Show our playback controls in the compat view
////					nBuilder.setColor(getResources().getColor(R.color.dark_gray));                                // Set the Notification color		//
////					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////						nBuilder.setChannelId(getResources().getString(R.string.notifi_id));                    // Build.VERSION_CODES.O の追加分
////					}
////					Notification.Action cAction = generateAction(android.R.drawable.ic_media_rew, "Rewind", ACTION_REWIND, PendingIntent.FLAG_MUTABLE);
////					//				・一発を確実に出す場合=FLAG_ONE_SHOT
////					//・情報を更新しつつ何度も出す場合=FLAG_CANCEL_CURRENT（丸っと更新） or FLAG_UPDATE_CURRENT(一部更新）
////					//・直前のやつを再利用（リピート）する場合=FLAG_NO_CREATE
////					dbMsg += ",ACTION_REWIND 生成";
////					if (cAction != null) {
////						nBuilder.addAction(cAction);                //.addAction(android.R.drawable.ic_media_rew, "prev", retreivePlaybackAction(3))			// Add some playback controls		//retreivePlaybackAction(3)
////					} else {
////						dbMsg += "できず";
////					}
////					int ppIcon = android.R.drawable.ic_media_pause;                //getApplicationContext().getResources().getInteger(android.R.drawable.ic_media_pause);
////					String ppTitol = "pause";
////					if (mPlayer != null) {
////						dbMsg += ",isPlaying = " + mPlayer.isPlaying();
////						if (!mPlayer.isPlaying()) {        //(mState == State.Paused || mState == State.Stopped
////							ppIcon = android.R.drawable.ic_media_play;
////							ppTitol = "play";
////						}
////					}
////					cAction = generateAction(ppIcon, ppTitol, ACTION_PLAYPAUSE, PendingIntent.FLAG_MUTABLE);
////					dbMsg += ",ACTION_PLAYPAUSE 生成";
////					if (cAction != null) {
////						nBuilder.addAction(cAction);
////					} else {
////						dbMsg += "できず";
////					}
////					cAction = generateAction(android.R.drawable.ic_media_ff, "Next", ACTION_SKIP, PendingIntent.FLAG_MUTABLE);
////					dbMsg += ",ACTION_SKIP 生成";
////					if (cAction != null) {
////						nBuilder.addAction(cAction);    //.addAction(android.R.drawable.ic_media_ff, "next", retreivePlaybackAction(2))				//retreivePlaybackAction()
////					} else {
////						dbMsg += "できず";
////					}
////					cAction = generateAction(android.R.drawable.ic_lock_power_off, "Qite", ACTION_SYUURYOU_NOTIF, PendingIntent.FLAG_MUTABLE);
////					dbMsg += ",ACTION_SYUURYOU_NOTIF 生成";
////					if (cAction != null) {
////						nBuilder.addAction(cAction);    //ノティフィケーションから終了	ロックスクリーンでは4つ目のアイコンでsetLargeIconが表示されなくなる	☆終了不能に陥る?
////					} else {
////						dbMsg += "できず";
////					}
////					nBuilder.setLargeIcon(artwork);                                                                // Set the large and small icons
////					nBuilder.setSmallIcon(R.drawable.no_image);                                                    // Set Notification content information			R.drawable.no_image
////					nBuilder.setContentText(creditArtistName);                                                            //ここが実際に書き込まれる文字
////					nBuilder.setContentInfo(albumName);
////					nBuilder.setContentTitle(titolName);
////					nBuilder.setContentIntent(pendingIntent);                                                                        //タップで起動する画面
////					//		.setAutoCancel(true)																					//タップで通知領域から削除する＞＞setContentIntentと合わせて削除せずにノティフィケーションだけを閉じさせる　＝　ノティフィケーションを閉じてアクテイビティを表示
////					lpNotification = nBuilder.build();                                                                                        //生成
////					lpNotification.flags = Notification.FLAG_ONGOING_EVENT;                                                        //フリックで削除させない（削除されることを防ぐ）
////					lpNControls = mSession.getController().getTransportControls();            // Do something with your TransportControls			final TransportControls
////					mNotificationManager.notify(NOTIFICATION_ID, lpNotification);
////
////					startForeground(1, lpNotification);
//			}
//			myLog(TAG,dbMsg);
//		} catch (Exception e) {
//			myErrorLog(TAG,dbMsg+"で"+e);
//		}
//	}
	//05-07 21:43:02.811: E/MediaPlayer(5999): Should have subtitle controller already set

//
//	private Notification.Action generateAction( int icon, String title, String intentAction ,int flag) {
//		final String TAG = "generateAction";
//		String dbMsg="";
//		Notification.Action action = null;
//		dbMsg="icon = " + icon;
//		dbMsg +=",title = " + title;
//		dbMsg +=",intentAction = " + intentAction;
//		dbMsg +=",mState = " + mState ;
//		try{
//			Intent intent = new Intent( getApplicationContext(), MusicPlayerService.class );
//			intent.setAction( intentAction );
//			PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, flag);
//			action = new Notification.Action.Builder( icon, title, pendingIntent ).build();
//			myLog(TAG,dbMsg);
//		} catch (Exception e) {
//			myErrorLog(TAG,dbMsg+"で"+e);
//		}
//		return action;
//	}

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

	/**ノティフィケーション作成*/
//	public void makeNotification() {
//		final String TAG = "makeNotification";
//		String dbMsg="";/////////////////////////////////////
//		//	http://android-note.open-memo.net/sub/system__custom_notification.html
//		//	http://yoshihikomuto.hatenablog.jp/entry/20111124/1322106813
//		//	http://triware.blogspot.jp/2012/10/notification-6.html
//		//	https://github.com/mixi-inc/AndroidTraining/wiki/2.04.-%E3%83%A1%E3%83%83%E3%82%BB%E3%83%BC%E3%82%B8%E3%83%B3%E3%82%B0%E3%81%A8%E9%80%9A%E7%9F%A5//
//	//	http://qiita.com/Helmos/items/5260601711560d9bc862
//	//☆表示されない時は	設定￥アプリケーションでこのアプリを選び「通知を表示」にチェック
//		try{
//			dbMsg += ",SDK_INT=" + android.os.Build.VERSION.SDK_INT;/////////////////////////////////////
//			dbMsg += ",pref_notifplayer=" + myPreferences.pref_notifplayer;/////////////////////////////////////
////			sharedPref = getSharedPreferences( getResources().getString(R.string.pref_main_file) ,MODE_PRIVATE);		//	getSharedPreferences(prefFname,MODE_PRIVATE);
//			Map<String, ?> keys = sharedPref.getAll();
//			myPreferences.pref_notifplayer = Boolean.valueOf( String.valueOf(keys.get("myPreferences.pref_notifplayer")));
//			dbMsg += ">>" + myPreferences.pref_notifplayer;
//			if ( 21 <= android.os.Build.VERSION.SDK_INT && myPreferences.pref_notifplayer) {
//			}else if ( 11 <= android.os.Build.VERSION.SDK_INT && myPreferences.pref_notifplayer) {
//				dbMsg += ",mNotification=" + mNotification;/////////////////////////////////////
//				if( mNotification != null ){
//					mNotificationManager.cancelAll();
//				}
//				mNotificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);	// NotificationManagerを取得
//				dbMsg += ";" + mNotificationManager;/////////////////////////////////////
//				ntfViews = new RemoteViews(this.getPackageName(), R.layout.remote_controll);		//ノティフィケーションのレイアウトをファイルからRemoteViewsを生成
//				PendingIntent pendingIntent = PendingIntent.getActivity( getApplicationContext() , 0
//						,new Intent(getApplicationContext() ,playerClass), PendingIntent.FLAG_UPDATE_CURRENT );
//						//既にPendingIntentオブジェクトを作成している場合、そのオブジェクトが持つ`Intent`オブジェクト自体は置き換えず、`
//						//Intent`オブジェクトが持つ`Extras`のみを置き換えます。
//				mNotification = new Notification();		// Notificationを生成・初期化
//				mNotification.icon = R.drawable.play_notif;
//				mNotification.contentView = ntfViews;
//				mNotification.flags = Notification.FLAG_ONGOING_EVENT			////「通知」ではなく，「実行中 (OnGoing)」として通知する。
//																										//これによって「通知を消去」ボタンから notification を消去できなくなる。。
//		//								| Intent.FLAG_ACTIVITY_CLEAR_TOP
//										| Notification.FLAG_NO_CLEAR;						// 音楽再生中は常駐するようにする
//	//加えてManifest ファイルの activity に android:launchMode="singleTask" を付与すると，
//	//ステータスバーの通知をタップした際に onPause → onResume と状態が遷移する。即ち，onCreate へ遷移しない。
//
//				mNotification.contentIntent = pendingIntent;
//		//		myLog(TAG,dbMsg);
//
//				////////ボタンの処理	☆	extends Service 	のonStartCommandで処理							NotifRecever
//				Intent intentNR = new Intent(getApplication(), NotifRecever.class);
//				ntfViews.setImageViewResource(R.id.stop, android.R.drawable.ic_lock_power_off);
//				intentNR.setAction(ACTION_SYUURYOU_NOTIF);
//				PendingIntent piStop = PendingIntent.getService(this, 0, intentNR, PendingIntent.FLAG_UPDATE_CURRENT);
//				ntfViews.setOnClickPendingIntent(R.id.stop, piStop);						//piStop
//
//				ntfViews.setImageViewResource(R.id.ppPButton  , R.drawable.play_notif);
//				intentNR.setAction(ACTION_PLAYPAUSE);
//				PendingIntent piPlay = PendingIntent.getService(this, 0, intentNR, PendingIntent.FLAG_UPDATE_CURRENT);
//				ntfViews.setOnClickPendingIntent(R.id.ppPButton, piPlay);
//		///////////////////////////////////////////ボタンの処理///////////////////////////
//			}
//			myLog(TAG,dbMsg);
//		} catch (Exception e) {
//			myErrorLog(TAG,dbMsg+"で"+e);
//		}
//	}

	public long currentTime = 0;
	public boolean playing ;
	public Bitmap notifAlbumArt;	// Dummy album art we will pass to the remote control (if the APIs are available).
	public int playPauseRes;		//ノティフィケーションプレイヤーの再生/ポーズボタン
	/**
	 * Updates the notification.
	 * 呼出し元	sendPlayerState ,
	 * */
//	public void updateNotification( MediaPlayer player ) throws IllegalStateException{		//
//		final String TAG = "updateNotification";
//		String dbMsg="";
//		try{
//			dbMsg += ",SDK_INT=" + android.os.Build.VERSION.SDK_INT;/////////////////////////////////////
//			dbMsg += ",pref_notifplayer=" + myPreferences.pref_notifplayer;/////////////////////////////////////
//			if( player != null ){
//				if (android.os.Build.VERSION.SDK_INT >= 11 && myPreferences.pref_notifplayer) {
//					dbMsg += ",mNotification=" + mNotification;/////////////////////////////////////
//					dbMsg += "[" + mIndex +"]";/////////////////////////////////////
//					dbMsg +=",isPlaying=" + player.isPlaying();/////////////////////////////////////
//					if(player.isPlaying() ){
//						mState = State.Playing;
//					}
//					dbMsg +=",mState=" + mState;
//					playing = mState == State.Playing;
//					dbMsg +=",playing=" + playing;
//					//操作ボタンの切り替え/////////////////////////////////////////////////////////
//					playPauseRes = playing ? R.drawable.pouse_notif : R.drawable.play_notif;			//操作ボタン	...509 / ...510
//					dbMsg += ",playPauseRes=" + playPauseRes;/////////////////////////////////////
//					ntfViews.setImageViewResource(R.id.ppPButton  ,playPauseRes);
//					mNotification.icon = playPauseRes;
//					////////////////////////////////////////////////////////操作ボタンの切り替え///
//					dbMsg += ",mState=" + mState;
//					dbMsg += ",currentTime=" + currentTime;
//					if (mState == State.Stopped) {
//						currentTime = 0;
//					} else {
//						if( player != null ){
//							dbMsg += ";getCurrentPosition=" + player.getCurrentPosition();			// mPlayer.getCurrentPosition();/////////////////////////////////////
//							currentTime = SystemClock.elapsedRealtime() - player.getCurrentPosition();			//mPlayer.getCurrentPosition();
//							dbMsg += ";current=" + currentTime;			// mPlayer.getCurrentPosition();/////////////////////////////////////
//						}
//					}
//					dbMsg += ">>" + currentTime;/////////////////////////////////////
//					Resources r = getApplicationContext().getResources();
//					String albumArt =MusicPlayerService.this.album_art;
//					dbMsg +=",albumArt="+ albumArt;
//					notifAlbumArt = ORGUT.retBitMap( albumArt  , 112 , 112 ,  getResources() );		//指定したURiのBitmapを返す	 , dHighet , dWith ,
//					dbMsg +=",art=" + album_art ;/////////////////////////////////////リストの状態	起動直後；veiwPlayer / 再選択chyangeSong
//					dbMsg +=" , AlbumArt(ビットマップ) = " + notifAlbumArt;/////////////////////////////////////
//					myLog(TAG,dbMsg);
//					new Thread(new Runnable() {				//ワーカースレッドの生成☆ここで書換え
//						public void run() {
//							final String TAG = "Thread";
//							String dbMsg="";
//							try{
//								try {
//									Thread.sleep(10); // 1ms秒待つ
//								} catch (InterruptedException e) {
//									e.printStackTrace();
//								}
//								dbMsg += "thread id = " + Thread.currentThread().getId();
//								dbMsg += ";" +  MusicPlayerService.this.playingItem.artist;				//creditArtistName;/////////////////////////////////////
//								ntfViews.setTextViewText(R.id.artist,  MusicPlayerService.this.playingItem.artist);//クレジットアーティスト名
//								dbMsg += "(" + b_Album+ ">>"+ MusicPlayerService.this.playingItem.album;				// albumName;/////////////////////////////////////
//								ntfViews.setTextViewText(R.id.album,  MusicPlayerService.this.playingItem.album);	//アルバム名
//								dbMsg += ";" +  MusicPlayerService.this.playingItem.title;				//titolName;/////////////////////////////////////
//								ntfViews.setTextViewText(R.id.title,   MusicPlayerService.this.playingItem.title);		//曲名
//								dbMsg += ",currentTime=" + MusicPlayerService.this.currentTime +"mS";/////////////////////////////////////
//								ntfViews.setChronometer(R.id.chronometer,  MusicPlayerService.this.currentTime , null, playing);
//								dbMsg += ",notifAlbumArt=" + MusicPlayerService.this.notifAlbumArt;/////////////////////////////////////
//								try{
//									ntfViews.setImageViewBitmap(R.id.rc_Img  , MusicPlayerService.this.notifAlbumArt);
//								} catch (IllegalStateException e) {
//									myErrorLog(TAG,dbMsg+"で"+e);
//								} catch (Exception e) {
//									myErrorLog(TAG,dbMsg+"で"+e);
//									throw new RuntimeException(e);
//								}
//								mNotificationManager.notify(NOTIFICATION_ID, mNotification);
//								myLog(TAG,dbMsg);
//							} catch (Exception e) {
//								myErrorLog(TAG,dbMsg+"で"+e);
//							}
//						}
//					}).start();
//				}
//			}
//		} catch (IllegalStateException e) {
//			myErrorLog(TAG,dbMsg+"で"+e);
//		} catch (Exception e) {
//			myErrorLog(TAG,dbMsg+"で"+e);
//		}
//	}

	public RemoteControlClient mRemoteControlClient;	// our RemoteControlClient object, which will use remote control APIs available in SDK level >= 14, if they're available.
	public Bitmap mDummyAlbumArt;	// Dummy album art we will pass to the remote control (if the APIs are available).
	public Intent intentRS;					//ロックスクリーン用
	public ComponentName mMediaButtonReceiverComponent;// The component name of MusicIntentReceiver, for use with media button and remote control APIs
	/**
	 * ロックスクリーン作成(mRemoteControlClientにボタンを設定する)
	 * 呼出し元はcreateMediaPlayerIfNeededでMediaPlayer生成直後	( 元々はsongInfoSett)
	 * */
	@SuppressWarnings("deprecation")
//	public void makeLockScreenP( MediaPlayer player ) {					//ロックスクリーン作成 < songInfoSett
//		final String TAG = "makeLockScreenP";
//		String dbMsg="";
//		try{
//			dbMsg += ",SDK_INT=" + android.os.Build.VERSION.SDK_INT;/////////////////////////////////////
////			sharedPref = getSharedPreferences( getResources().getString(R.string.pref_main_file) ,MODE_PRIVATE);		//	getSharedPreferences(prefFname,MODE_PRIVATE);
//			Map<String, ?> keys = sharedPref.getAll();
//			myPreferences.pref_lockscreen = Boolean.valueOf( String.valueOf(keys.get("pref_lockscreen")));
//			dbMsg += ",pref_lockscreen=" + myPreferences.pref_lockscreen;/////////////////////////////////////
//			myLog(TAG,dbMsg);
//			if (myPreferences.pref_lockscreen) {										// registerRemoteControlClient
//				if (21 <= android.os.Build.VERSION.SDK_INT) {										// registerRemoteControlClient		 && android.os.Build.VERSION.SDK_INT <21
//					MediaSession mSession =  new MediaSession(getApplicationContext(),getApplicationContext().getPackageName());
//					intentRS = new Intent(Intent.ACTION_MEDIA_BUTTON);	//②③ RemoteControlClientの為にPendingIntentを作成する	Intent.ACTION_MEDIA_BUTTONをアクションに持つIntentを生成
//					intentRS.setComponent(mMediaButtonReceiverComponent);	//②③ ComponentNameをIntentに設定
//					PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intentRS, 0);	//	②③ create and register the remote control client
//				}else if (14 <= android.os.Build.VERSION.SDK_INT) {										// registerRemoteControlClient		 && android.os.Build.VERSION.SDK_INT <21
//					dbMsg += ",player=" +player;/////////////////////////////////////
//					if( player != null ){
//						dbMsg += ",mRemoteControlClient=" +mRemoteControlClient;/////////////////////////////////////
//						if( mRemoteControlClient == null ){
//							player.setWakeMode(getApplicationContext(),PowerManager.PARTIAL_WAKE_LOCK);					//①	 Intent.ACTION_MEDIA_BUTTONのブロードキャストを受け取る
//						///Org	http://www.atmarkit.co.jp/ait/articles/1203/28/news128_3.html///////////////////////////////////////////////////////
//							intentRS = new Intent(Intent.ACTION_MEDIA_BUTTON);
//						///http://techbooster.org/android/ui/10298////////////////////////////////////////////////////////
//							ComponentName myEventReceiver = new ComponentName(getPackageName(), MusicPlayerReceiver.class.getName());						// BroadcastReceiverのコンポーネント名を取得する
//							mAudioManager.registerMediaButtonEventReceiver(myEventReceiver);			// BroadcastReceiverをシステムに登録する
//							intentRS.setComponent(myEventReceiver);						///http://techbooster.org/android/ui/10298//
//						////////////////////////////////////////////////////http://techbooster.org/android/ui/10298///////
//							PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intentRS, 0);	//	②③ create and register the remote control client
//							mRemoteControlClient = new RemoteControlClient(mediaPendingIntent);				//②RemoteControlClientを生成し、PendingIntentを設定する
//							if( mRemoteControlClient != null ){
//								dbMsg +=">>" + mRemoteControlClient;/////////////////////////////////////
//								mAudioManager.registerRemoteControlClient(mRemoteControlClient);										//② AudioManagerにRemoteControlClientを登録
//
//								dbMsg +=",mAudioManager=" + mAudioManager.toString();/////////////////////////////////////
//								mRemoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);					//ロックスクリーンの状態を再生に設定
//								mRemoteControlClient.setTransportControlFlags(RemoteControlClient.FLAG_KEY_MEDIA_PLAY		// リモコン上で扱える操作を設定
//									| RemoteControlClient.FLAG_KEY_MEDIA_PAUSE
//									| RemoteControlClient.FLAG_KEY_MEDIA_NEXT
//									| RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS
//									| RemoteControlClient.FLAG_KEY_MEDIA_STOP);
//							}
//							////////////////////////////////////////////////////////Org///
//						}
//					}					//if( mPlayer != null ){
//				}				//if (android.os.Build.VERSION.SDK_INT >= 14) {
//
//				OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
//					public void onAudioFocusChange(int focusChange) {
//						final String TAG = "onAudioFocusChange[MusicPlayerService]";
//						String dbMsg="";
//						try{
//							switch (focusChange) {
//							case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
//								dbMsg="AUDIOFOCUS_LOSS_TRANSIENT；再生を一時停止します。";
//								break;
//							case AudioManager.AUDIOFOCUS_GAIN:
//								dbMsg="AUDIOFOCUS_GAIN；再生を再開します。";
//								break;
//							case AudioManager.AUDIOFOCUS_LOSS:
//								dbMsg="AUDIOFOCUS_LOSS；再生を停止します。";
//	//							mAudioManager.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
//	//							mAudioManager.abandonAudioFocus(afChangeListener);
//								break;
//							}
//			//				myLog(TAG,dbMsg);
//						} catch (Exception e) {
//							myErrorLog(TAG,dbMsg+"で"+e);
//						}
//					}
//				};
//				int result = mAudioManager.requestAudioFocus(afChangeListener,
//						AudioManager.STREAM_MUSIC,								// Use the music stream.
//						AudioManager.AUDIOFOCUS_GAIN);							// // 永続的なフォーカスを要求します		AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
//				dbMsg +=",result=" + result;/////////////////////////////////////
//				if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {				//1
//	//				AudioManager.unregisterMediaButtonEventReceiver(RemoteControlReceiver);		// Start playback.
//					dbMsg +=  ">>" + mRemoteControlClient;/////////////////////////////////////
//					if( mRemoteControlClient != null ){
//			//			myLog(TAG,dbMsg);
//						updateLockScreenP();					//ロックスクリーン更新
//					}
//				}
//
//			}
//		} catch (Exception e) {
//			myErrorLog(TAG,dbMsg+"で"+e);
//		}
//		/*ロック画面にオーディオリモコンを表示させる方法
//		 * ①	http://www.atmarkit.co.jp/ait/articles/1203/28/news128_3.html		【2】ロック画面クライアントからの制御方法
//			②	http://techbooster.org/android/ui/10298/
//			③	http://wp.developapp.net/?p=3253
//			④	http://stackoverflow.com/questions/22281616/set-lock-screen-background-in-android-like-spotify-do/22281858#22281858
//		*/
//	}

	/**
	 * ロックスクリーン更新
	 * 呼び出し元はmakeLockScreenP , sendPlayerStatem
	 * */
	public void updateLockScreenP() throws IllegalStateException {					//ロックスクリーン更新
		final String TAG = "updateLockScreenP";
		String dbMsg="";
		try{
			dbMsg += ",SDK_INT=" + android.os.Build.VERSION.SDK_INT;/////////////////////////////////////
			dbMsg += ",pref_lockscreen=" + myPreferences.pref_lockscreen;/////////////////////////////////////
			dbMsg += ",playingItem=" +playingItem;/////////////////////////////////////
			if( playingItem != null && myPreferences.pref_lockscreen ){
				dbMsg +=",mRemoteControlClient=" + mRemoteControlClient;/////////////////////////////////////
				if( mRemoteControlClient != null ){
					dbMsg +=",アーティスト=" + playingItem.artist;/////////////////////////////////////
					RemoteControlClient.MetadataEditor rcEditer = mRemoteControlClient.editMetadata(true)	;																		// リモコン上の曲情報を更新
					rcEditer.putString(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST, playingItem.artist);					//☆METADATA_KEY_ARTISTでは表示されなかった
					rcEditer.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, playingItem.album);
					rcEditer.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, playingItem.title);
					rcEditer.putLong(MediaMetadataRetriever.METADATA_KEY_DURATION, playingItem.duration);
					dbMsg +=",Art=" + mDummyAlbumArt;/////////////////////////////////////
					if(mDummyAlbumArt == null){
						String dataFN = getPrefStr( "pref_data_url" ,"" , MusicPlayerService.this);
						dbMsg +=",dataFN=" + dataFN;/////////////////////////////////////
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
					dbMsg +=",mRemoteControlClient=" + mRemoteControlClient;/////////////////////////////////////
					mAudioManager.registerRemoteControlClient(mRemoteControlClient);										//② AudioManagerにRemoteControlClientを登録
					dbMsg +=",mAudioManager=" + mAudioManager.toString();/////////////////////////////////////
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
		String dbMsg="";
		try{
			Toast.makeText(getApplicationContext(), "Media player error! Resetting.", Toast.LENGTH_SHORT).show();
			dbMsg+="Media player error! Resetting.";/////////////////////////////////////
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
		final String TAG = "onGainedAudioFocus";
		String dbMsg="";
		try{
			Toast.makeText(getApplicationContext(), "gained audio focus.", Toast.LENGTH_SHORT).show();
			mAudioFocus = AudioFocus.Focused;
			dbMsg +="mState=" + mState;/////////////////////////////////////
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
		String dbMsg="";
		try{
			dbMsg +="canDuck= " +canDuck;/////////////////////////////////////
			Toast.makeText(getApplicationContext(), "lost audio focus." + (canDuck ? "can duck" : "no duck"), Toast.LENGTH_SHORT).show();
			mAudioFocus = canDuck ? AudioFocus.NoFocusCanDuck : AudioFocus.NoFocusNoDuck;
			dbMsg="mAudioFocus= " + mAudioFocus;/////////////////////////////////////
			myLog(TAG,dbMsg);
			if (exoPlayer != null ) {		// start/restart/pause media player with new focus settings
				if (exoPlayer.isPlaying()) {
					configAndStartMediaPlayer();
				}
			}
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	AsyncTask<Context, Void, List<Item>> PMRTask;

	/**
	 * 出来上がっている曲リストの読み込み
	 * **/
//	@Override
	public void onMusicRetrieverPrepared(List<Item> items) {													//
		final String TAG = "onMusicRetrieverPrepared[MusicPlayerService]";
		String dbMsg="";
		try{
			mState = State.Stopped;		// Done retrieving!
			dbMsg += " ; mStartPlayingAfterRetrieve=" + mStartPlayingAfterRetrieve;
			if (mStartPlayingAfterRetrieve) {		// If the flag indicates we should start playing after retrieving, let's do that now.
				tryToGetAudioFocus();
				playNextSong(mIndex,true);
			} else {
				sendPlayerState(exoPlayer);
			}
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	/**
	 * 一曲分のデータ抽出して他のActvteyに渡す。
	 *  呼出し元は	onStartCommand ACTION_PLAY_READ/ACTION_REQUEST_STATE/ACTION_KEIZOKU/ACTION_SYUURYOU_NOTIF
	 * 				 , processPlayRequest , processPauseRequest , songInfoSett . onMusicRetrieverPrepared
	 * */
	@SuppressLint({"InlinedApi", "Range"})
	private void sendPlayerState( ExoPlayer player ) {			//①?、②ⅲStop?	,	onStartCommand(ACTION_PLAY_READ,ACTION_REQUEST_STATE),
		final String TAG = "sendPlayerState";
		String dbMsg="";
		try {
			dbMsg += ",操作指定=" + action + "<<b_state=" + b_state;
			dbMsg += ",再生中のプレイリスト[" + myPreferences.nowList_id + "]";
			dbMsg += myPreferences.nowList;
			dbMsg += " ,mIndex " + mIndex;
//			Cursor playingItem = musicPlaylist.getPlaylistItems(myPreferences.nowList_id, mIndex);
//			if (playingItem.moveToFirst()) {
//				creditArtistName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST));        //playingItem.artist;	//クレジットされているアーティスト名
//				Uri dataUri = Uri.parse(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA)));
//				String myPreferences.pref_data_url = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA));
			dbMsg += ",読込確認=" + myPreferences.pref_data_url;/////////////////////////////////////
			dbMsg += "[id=" + audioId + "]";
			dbMsg += " ,アルバムの代表 アーティスト名=⁼ " + albumArtistName;
			dbMsg += " ,クレジット=" + creditArtistName;
//				albumName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM));        //playingItem.album;			//アルバム名
			dbMsg += " , アルバム=" + albumName;/////////////////////////////////////	this.album = album;
//				titolName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE));        //playingItem.title;		//曲名
			dbMsg += " ,タイトル=" + titolName;/////////////////////////////////////		this.title = title;
//				@SuppressLint("Range") int audioId = Integer.parseInt(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)));
//				dbMsg += " ,audioId= " + audioId;
//				int duration = Integer.parseInt(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.DURATION)));
			int mcPosition =0;
			IsPlaying =false;
			if (exoPlayer != null){
				mcPosition = (int) exoPlayer.getCurrentPosition();
				IsPlaying = exoPlayer.isPlaying();
			}
			dbMsg += ">>mcPosition=" + mcPosition + "/" + saiseiJikan + "mS]";
			dbMsg += ",再生中か= " + IsPlaying;

			Intent intent = new Intent(ACTION_STATE_CHANGED);
			intent.putExtra("nowList_id", myPreferences.nowList_id);
			intent.putExtra("nowList", myPreferences.nowList);
			intent.putExtra("mIndex", mIndex);
			intent.putExtra("data", myPreferences.pref_data_url.toString());
			intent.putExtra("mcPosition", mcPosition);
			intent.putExtra("saiseiJikan", saiseiJikan);
			intent.putExtra("albumArtist", albumArtistName);
			intent.putExtra("creditArtistName", creditArtistName);
			intent.putExtra("albumName", albumName);
			intent.putExtra("titolName", titolName);
			intent.putExtra("IsPlaying", IsPlaying);
			sendBroadcast(intent);                    //APIL1
			dbMsg += ";Broadcast送信>>mcPosition=" + mcPosition + "/" + saiseiJikan + "mS]";
//				saiseiJikan = duration;
//				dbMsg += saiseiJikan + "mS]";
//				String dataFN = getPrefStr("myPreferences.pref_data_url", "", MusicPlayerService.this);
//				dbMsg += "以前のファイル=" + dataFN + ">>再生中のファイル名=" + dataFN;
//				int mcPosition = getPrefInt("pref_position", 0, context);        //sharedPref.getInt("pref_position" , 0);
//				int saiseiJikan = getPrefInt("pref_duration", 0, context);        //sharedPref.getInt("pref_duration" , 0);
//				dbMsg += ">>mcPosition=" + mcPosition + "/" + saiseiJikan + "mS]";
//				if (mPlayer == null) {
//					dbMsg += ",mPlayer=null";
//					if (dataFN == null || dataFN.equals("")) {
//						dataFN = getPrefStr("myPreferences.pref_data_url", "", context);    //sharedPref.getString("dataFN" , "");
//						dbMsg += ">pref1>" + dataFN;////////////////////////////////////////////////////////////////////////////
//						if (dataFN.equals("")) {
//							dataFN = getPrefStr("myPreferences.pref_data_url", "", context);
//							dbMsg += ">pref2>" + dataFN;////////////////////////////////////////////////////////////////////////////
//						}
//					}
//				} else {
//	//				dataFN = playingItem.data;                 // mPlayerから直接読めない
//					dbMsg += ">playingItem>" + dataFN;
//				}
//				if (saiseiJikan < mcPosition) {
//					saiseiJikan = duration;
//	//				(int) playingItem.duration;
//					dbMsg += ">saiseiJikan>" + saiseiJikan;
//				}
//				if (b_dataFN != dataFN) {                                    //0706戻す            	b_state != action)
//					dbMsg += ">>action変更>>";
//					intent.putExtra("action", action);
//					dbMsg += ",送り戻し待ち曲数=" + frCount;
//					dbMsg += ",player=" + player;
//					dbMsg += "[List_id=" + myPreferences.nowList_id + "]";
//					dbMsg += "[mIndex=" + mIndex + "/" + listEnd + "]";
//
//					dbMsg += ",dataFN=" + dataFN;
//					if (dataFN == null || dataFN.equals("")) {
//						int rInt = Item.getMPItem(dataFN);
//						dbMsg += ",rInt=" + rInt;////☆ここから参照できない？/////////////////////////////////
//						saiseiJikan = duration;            //DURATION;継続;The duration of the audio file, in ms;Type: INTEGER (long)
//						dbMsg += ">>mcPosition=" + mcPosition + "/" + saiseiJikan + "mS]";//pauseから復帰した時0になっている
//						dbMsg += ",art=" + album_art;/////////////////////////////////////リストの状態	起動直後；veiwPlayer / 再選択chyangeSong
//						intent.putExtra("albumArt", album_art);
//					}
//
//					dbMsg += " , mState=" + mState.toString();////////////////////////////ノティフィケーション送る
//					intent.putExtra("state", mState.toString());
//					dbMsg += ",2点間リピート中" + rp_pp;/////////////////////////////////////
//					if (rp_pp) {            //2点間リピート中
//						mcPosition = pp_start;            //リピート区間開始点
//						dbMsg += ">>" + mcPosition;/////////////////////////////////////
//						player.seekTo(mcPosition);
//					}
//					b_state = action;
//				}
//
//				dbMsg += ",生成中= " + IsSeisei;//////////////////////////////////
//				intent.putExtra("IsSeisei", IsSeisei);
//				dbMsg += "、今の状態=" + imanoJyoutai;/////////////////////////////////////リストの状態	起動直後；veiwPlayer / 再選択chyangeSong
//				intent.putExtra("imanoJyoutai", imanoJyoutai);
//				dbMsg += ",Bluetooth= " + stateBaseStr;//////////////////////////////////
//				dbMsg += " ,今日は " + ruikeikyoku + "曲";/////////////////////////////////////
//				intent.putExtra("ruikeikyoku", ruikeikyoku);
//				ruikeiSTTime = ruikeiSTTime + ruikeikasannTime;                //	累積加算時間
//				dbMsg += ruikeiSTTime + "mS(追加分" + ruikeikasannTime + "mS)";/////////////////////////////////////
//				intent.putExtra("ruikeiSTTime", ruikeiSTTime);
//				intent.putExtra("stateBaseStr", stateBaseStr);
//				sentakuCyuu = false;                        //送り戻しリスト選択解除
//				dbMsg += ",選択中=" + sentakuCyuu;/////////////////////////////////////
//				dbMsg += ", album_art = " + album_art;/////////////////////////////////////
//				Cursor cursor = null;
//				dbMsg += ",creditArtistNameは " + creditArtistName;/////////////////////////////////////
//				dbMsg += ",アルバムは " + b_Album + ">albumName>" + albumName;/////////////////////////////////////
//				if (b_Album == null) {
//					if (albumName != null) {
//						b_Album = albumName;
//					}
//				}
//				if (b_Album != null && albumName != null) {
//					if (!b_Album.equals(albumName)) {        //前のアルバム
//						album_art = null;
//						Uri cUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
//						String[] c_columns = null;                //③引数columnsには、検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
//						String c_selection = MediaStore.Audio.Albums.ARTIST + " LIKE ?  AND " + MediaStore.Audio.Albums.ALBUM + " = ?";
//						String[] c_selectionArgs = {"%" + creditArtistName + "%", albumName};            //⑥引数groupByには、groupBy句を指定します。
//						String c_orderBy = null;                                            //MediaStore.Audio.Albums.LAST_YEAR  ; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
//						cursor = getContentResolver().query(cUri, c_columns, c_selection, c_selectionArgs, c_orderBy);
//						dbMsg += "、 " + cursor.getCount() + "件";/////////////////////////////////////
//						if (cursor.moveToFirst()) {
//							album_art = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
//						}
//						cursor.close();
//						intent.putExtra("album_art", album_art);
//
//						OrgUtil ORGUT = new OrgUtil();                //自作関数集
//						WindowManager wm = (WindowManager) this.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
//						Display disp = wm.getDefaultDisplay();
//						int width = disp.getWidth();
//						width = width * 9 / 10;
//						mDummyAlbumArt = ORGUT.retBitMap(album_art, width, width, getResources());        //指定したURiのBitmapを返す	 , dHighet , dWith ,
//						b_Album = albumName;
//						dbMsg += ",art=" + album_art;/////////////////////////////////////リストの状態	起動直後；veiwPlayer / 再選択chyangeSong
//						dbMsg += " , AlbumArt(ビットマップ) = " + mDummyAlbumArt;/////////////////////////////////////
//					}
//				}
//				dbMsg += ">>>> " + b_Album + ">albumName>" + albumName;/////////////////////////////////////
//				intent.putExtra("songLyric", songLyric);
//				b_state = action;
//					/*アーティストリピート	>	>songInfoSett
//					 * onCompletNow=false,操作指定=LISTSEL,送り戻し待ち曲数=0,player=android.media.MediaPlayer@41f4ccd0[List_id=43408]
//					 * リピート再生[mIndex=0/238],URi=/storage/sdcard0/Music/The Beatles/Past Masters, Vol. 1 [2009 Stereo Remaster]/1-01 Love Me Do [Original Single Ver.wma,rInt=0[145960mS] , mState=Playing
//Ω					 * album_art = /storage/sdcard0/Android/data/com.android.providers.media/albumthumbs/1373795418804creditArtistNameは The Beatlesアルバムは Let It Be... Naked>>>Past Masters, Vol. 1 [2009 Stereo Remaster]、
//					 * 1件,art=/storage/sdcard0/Android/data/com.android.providers.media/albumthumbs/1440782793143 , AlbumArt(ビットマップ) = android.graphics.Bitmap@43e533a8,playingItem=com.hijiyam_koubou.marasongs.Item@43edc618[id=0]145960mS]
//					 *
//					 * 二点間初期
//					 * onCompletNow=false,操作指定=LISTSEL,送り戻し待ち曲数=0,player=android.media.MediaPlayer@43500cd8[List_id=43408]
//					 * リピート再生[mIndex=0/1],URi=/storage/sdcard0/Music/The Beatles/Let It Be... Naked/1-08 Don't Let Me Down.wma,rInt=0[198824mS] , mState=Playing
//					 * [再生ポジション=85,2点間リピート中true>>75884/198824mS],生成中= true,再生中か= true、今の状態=203,Bluetooth= null ,今日は 0曲612320mS(追加分0mS),選択中=false,
//					 *  album_art = /storage/sdcard0/Android/data/com.android.providers.media/albumthumbs/1373795418804creditArtistNameは The Beatlesアルバムは Let It Be... Naked>>>Let It Be... Naked,art=/storage/sdcard0/Android/data/com.android.providers.media/albumthumbs/1373795418804 ,
//					 *   AlbumArt(ビットマップ) = android.graphics.Bitmap@4411b550,playingItem=com.hijiyam_koubou.marasongs.Item@43cc4730[id=0]198824mS]
//					 * */
//
//				dbMsg += ",imanoJyoutai=" + imanoJyoutai;///////////////////////////////////
//				if (!dataFN.equals("")) {
//					wrightSaseiList(dataFN);
//				}
//			}else{
//				dbMsg += ",読み出せず";
//			}
//			playingItem.close();
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	/**
	 * 再生した曲を最近再生リストに追記する
	 * @ String data 再生する曲のUrl
	 * */
	private void wrightSaseiList( String data ) {			//最近再生リストへの追記
		final String TAG = "wrightSaseiList";
		String dbMsg="";
		try{
			dbMsg +=",現在" + data ;

			int playOrder = 0;
			String listMei = getResources().getString(R.string.playlist_namae_saikinsisei);			//最近再生
			int playlist_id = musicPlaylist.getPlaylistId(listMei);	//指定された名称のリストを作成する
			dbMsg +="[" + playlist_id + "]" + listMei ;
			Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
			String[] columns = null;			//{ idKey, nameKey };
			String c_selection0 = MediaStore.Audio.Playlists.NAME +" = ? ";
			String[] c_selectionArg= {listMei};		//⑥引数groupByには、groupBy句を指定します。
			String c_orderBy = null;			//MediaStore.Audio.Playlists.Members.PLAY_ORDER;
			Cursor playList = getApplicationContext().getContentResolver().query(uri, columns, c_selection0, c_selectionArg, c_orderBy );
			int poSetteiti = playList.getCount();
			dbMsg +=",現在" + poSetteiti + "件" ;
			if(playList.moveToFirst()){
				Uri plUri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist_id);
				String c_selection = MediaStore.Audio.Playlists.Members.DATA +" = ? ";			//MediaStore.Audio.Media.IS_MUSIC + " = 1"
				String[] c_selectionArgs2= { String.valueOf(data) };   			//⑥引数groupByには、groupBy句を指定します。
				Cursor cursor = getContentResolver().query(plUri, null, c_selection, c_selectionArgs2, null);
				if(cursor.moveToFirst()){
					dbMsg +=",削除候補＝" + cursor.getCount() + "件" ;
					do{
						@SuppressLint("Range") int delId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members._ID));
						dbMsg +="[" + delId + "]" ;
						String where = "_id=" + Integer.valueOf(delId);
						int delID = getApplicationContext().getContentResolver().delete(plUri, where, null);				//削除		getApplicationContext()
						dbMsg += ",削除= " + delID + "レコード";

//						int delID = musicPlaylist.delOneLineBody(uri,menberId);
						dbMsg +=">>" + delID ;
					}while(cursor.moveToNext());
				}else{
					dbMsg +=",重複無し" ;

				}
				cursor.close();
				c_orderBy = "play_order";//
				c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
				cursor = getApplicationContext().getContentResolver().query(plUri, null, null, null, c_orderBy );
				if(cursor.moveToLast()){
					playOrder = cursor.getCount();
					dbMsg +=",書換候補＝" + playOrder + "件" ;
					if(100 < playOrder){
						if(cursor.moveToPosition(99)){
							playOrder = 100;
						}
					}
					do{
						@SuppressLint("Range") int menberId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members._ID));
						dbMsg +="(" + playOrder + ")"  ;
//						String menberData = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA));
						String where = "_id=" + Integer.valueOf(menberId);
//						String[] c_selectionArg3= {String.valueOf(menberId)};
						ContentValues contentvalues = new ContentValues();
						contentvalues.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, String.valueOf(playOrder));
						dbMsg += "[" + menberId + "]" ;
						int result_Id = getContentResolver().update(plUri,contentvalues,where,null);				//更新
						dbMsg +=  ">result>" + result_Id;
						playOrder--;

					}while(cursor.moveToPrevious());
				}
				cursor.close();

				dbMsg +=  ",追加する曲=" + data;
				Uri cUri;
				if ( Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT) {
					cUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
				} else {
					cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				String[] c_columns = null;		 		//③引数columnsには、検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
				c_selection =  MediaStore.Audio.Media.DATA +" = ? ";			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
				String[] c_selectionArgs3= {String.valueOf(data)};   			//音楽と分類されるファイルだけを抽出する
				cursor = getContentResolver().query( cUri , c_columns , c_selection , c_selectionArgs3  , null);
				if(cursor.moveToFirst()){
					dbMsg +=",追加候補＝" + cursor.getCount() + "件" ;
					dbMsg +=  ",kakikomiUri=" + plUri;

					ContentValues contentvalues = new ContentValues();
					@SuppressLint("Range") int audio_id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));;
					dbMsg +=  ",audio_id=" + audio_id;
					contentvalues.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, Integer.valueOf(audio_id));
					contentvalues.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, String.valueOf(playOrder));
					Uri result_uri = getContentResolver().insert(plUri, contentvalues);				//追加
					dbMsg +=  ",result_uri=" + result_uri;
					if(result_uri == null){					//NG
						dbMsg +=  "失敗 add music : " + playlist_id + ", " + audio_id + ", is null";
					}else if(((int)ContentUris.parseId(result_uri)) == -1){					//NG
						dbMsg +=  "失敗 add music : " + playlist_id + ", " + audio_id + ", " + result_uri.toString();
					}else{					//OK
						dbMsg +=   ">>成功list_id=" + playlist_id + ", audio_id=" + audio_id + ",result_uri= " + result_uri.toString();
					}
				}
				cursor.close();
			}
			playList.close();
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	@SuppressLint("Range")
	public int siteiListSakusi(String listName){				//指定された名称のリストを作成する
		int listID = 0;
		final String TAG = "siteiListSakusi[MusicPlayerService]";
		String dbMsg="";
		dbMsg += "指定された名称のリストを作成する" ;/////////////////////////////////////
		try{
			 dbMsg += "指定された名称=" + listName;
			Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
			String[] columns = null;			//{ idKey, nameKey };
			String c_selection = MediaStore.Audio.Playlists.NAME +" = ? ";
			String[] c_selectionArg= { String.valueOf(listName) };		//⑥引数groupByには、groupBy句を指定します。
			String c_orderBy = null;			//MediaStore.Audio.Playlists.Members.PLAY_ORDER;
			Cursor cursor = getApplicationContext().getContentResolver().query(uri, columns, c_selection, c_selectionArg, c_orderBy );
			dbMsg += "," + cursor.getCount() + "件既存";
			if(cursor.moveToFirst()){
				listID = Integer.valueOf(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID)));
				dbMsg +=",[" + listID + "]";			//[42529]content://media/external/audio/playlists/42529
			} else{
				Uri result_uri = addPlaylist(listName, null, null);		//プレイリストを新規作成する
				dbMsg +=",result_uri=" + result_uri;			//fastItemeFn=/storage/sdcard0/Music/Jimmy Cliff/Follow My Mind/07 Remake The World.wma
				listID = (int)ContentUris.parseId(result_uri);
				dbMsg +=","  + listID +"を作成";			//[42529]content://media/external/audio/playlists/42529
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
		String dbMsg="";
		 dbMsg += "プレイリストを新規作成する" ;
		try{
			dbMsg += "新規リスト名=" + listName;
			ContentValues contentvalues = null;
			ContentResolver contentResolver = getContentResolver();
			Uri playlist_uri = null;
			int playlist_id = -1;

			contentvalues = new ContentValues();
			contentvalues.put("name", listName);
			playlist_uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;			//URI
			result_uri = contentResolver.insert(playlist_uri, contentvalues);		//追加
			dbMsg +=",result_uri=" + result_uri;	//result_uri=content://media/external/audio/playlists/42529
			if(result_uri == null){			//NG
				dbMsg += ">>失敗 add playlist : " + listName + ", is null";
			}else if((playlist_id = (int)ContentUris.parseId(result_uri)) == -1){			//NG
				dbMsg +=  ">>失敗 add playlist : " + listName + ", " + result_uri.toString();
			}else{			//OK
				dbMsg +=  ">>成功 listName＝ " + listName + ",playlist_id=" + playlist_id;
				//add playlist : プレイリスト2015-12-03 14:16:37,42529
			}
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return result_uri;
	}

	/**
	 *MediaStore.Audio.Playlists.Membersからプリファレンスに保存されている再生曲から情報を取り出しグローバル変数にセット
	 * 独自メソッド
	 * 	呼出し元	onStartCommandで
	 * */
	@SuppressLint("Range")
	public void dataUketori(Intent intent) {	//クライアントからデータを受け取りグローバル変数にセット
		final String TAG = "dataUketori";
		String dbMsg="";/////////////////////////////////////
		try{
			String b_list = myPreferences.nowList;
			dbMsg += "intent/extrasから現在[" + myPreferences.nowList_id + "]" + b_list;
			int rInt = 0;
			dbMsg += "[" + mIndex + "]";
			String dataFN = getPrefStr( "pref_data_url" ,"" , MusicPlayerService.this);
			dbMsg += "pref:dataFN="+ dataFN ;			// = extras.getInt("mIndex");		//現リスト中の順番;
//			if(mIndex < 0 ){
//				mIndex = getCarentIndex(dataFN,plSL);
//				dbMsg += ">>mIndex>" + mIndex;
//				setPrefInt("pref_mIndex",mIndex,MusicPlayerService.this);
//			}
			int mcPosition = getPrefInt("pref_position" , 0, MusicPlayerService.this);  //sharedPref.getInt("pref_position" , 0);
			int saiseiJikan = getPrefInt("pref_duration" , 0, MusicPlayerService.this);  //sharedPref.getInt("pref_position" , 0);
			dbMsg +=" ,mcPosition=" + mcPosition + "/" + saiseiJikan + "[ms]IsPlaying=" + IsPlaying;
			dbMsg += ",getActio=:"+ intent.getAction();						//Intent { cmp=com.sen/.PlayerService } ,startId:1
			if( intent != null ) {
				Bundle extras = intent.getExtras();
				if (extras == null) {
					dbMsg += "他にデータ無し; ";
				} else {
					int b_List_id = Integer.parseInt(myPreferences.nowList_id);
					Cursor playingItem = musicPlaylist.getPlaylistItems(Integer.parseInt(myPreferences.nowList_id), mIndex);
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
			//			String myPreferences.pref_data_url = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA));
						dbMsg += ",読込確認=" + myPreferences.pref_data_url;/////////////////////////////////////
						int duration = Integer.parseInt(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.DURATION)));
						dbMsg += ",duration = " + duration + "[ms]";
						int index =Integer.parseInt(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.PLAY_ORDER)));
						dbMsg += ">itemUmu>index=" + index;            // = extras.getInt("mIndex");		//現リスト中の順番;
						rInt = extras.getInt("mIndex");
						dbMsg += "[mIndex;" + rInt;
						if (rInt != index) {
							if (index == -1) {
								mIndex = 0;
								dataFN = myPreferences.pref_data_url;
								dbMsg += ",dataFN=" + dataFN;
								mainEditor.putString("pref_data_url", String.valueOf(dataFN));        //再生中のファイル名
							} else {
								mIndex = index;
							}
						} else {
							mIndex = rInt;
						}
						mIndex = index;
						dbMsg += ">itemUmu>[" + mIndex + "/" + listEnd + "]";///////////////////////////////////
						if (!b_list.equals(getResources().getString(R.string.playlist_namae_request)) && myPreferences.nowList.equals(getResources().getString(R.string.playlist_namae_request))) {            //リクエストに切り替わった直後
							siseizumiDataFN = null;
						}
						rp_pp = extras.getBoolean("rp_pp");                        //2点間リピート中
						dbMsg += ",rp_pp=" + rp_pp;            // = extras.getInt("mIndex");		//現リスト中の順番;
						pp_start = extras.getInt("pp_start");                //リピート区間開始点
						dbMsg += ";" + pp_start;            // = extras.getInt("mIndex");		//現リスト中の順番;
						pp_end = extras.getInt("pp_end");                    //リピート区間終了点
						dbMsg += "～" + pp_end;            // = extras.getInt("mIndex");		//現リスト中の順番;
						if (saiseiJikan < mcPosition) {       //カウントアップ超過
							mcPosition = saiseiJikan;
							dbMsg += ">>" + mcPosition;
						}
						Boolean continuStatus = extras.getBoolean("continu_status");
						dbMsg += ",continuStatus=" + continuStatus;
						if (continuStatus) {
							dbMsg += "から再生,";
							processPlayRequest();                                                                    //②ⅲPlay?/PausedならconfigAndStartMediaPlayer
						}
					}
					playingItem.close();
				}
			}
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}

	/**
	 * 指定されたリストの中に指定した曲が有ればPLAY_ORDERを返す
	 * 　無ければ-1 */
// 	public int itemUmu(int playlistId , String dataURL) {	//指定されたリストの中に指定した曲が有るか		 ,String listName
//		int retInt = -1;
//		final String TAG = "itemUmu";
//		String dbMsg="";/////////////////////////////////////
//		try{
//			Cursor cursor = null;
//			dbMsg +="[listId="+playlistId +"の中に" + dataURL +"を確認";
//			if(0 < playlistId){
//				ContentResolver resolver = this.getContentResolver();
//				Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
//				String[] columns = null;				//{ idKey, nameKey };
//				String c_selection = MediaStore.Audio.Playlists._ID + " = ?";
//				String[] c_selectionArgs= { String.valueOf(playlistId) };
//				Cursor playLists = this.getContentResolver().query(uri, columns, c_selection, c_selectionArgs, null);
//				if(playLists.moveToFirst()){
//					dbMsg +=">>指定リスト有り";
//					String ieKubunn = "internal";
//					uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
//					dbMsg +=",uri="+uri.toString();
//					c_selection = MediaStore.Audio.Playlists.Members.DATA + " = ?";
//					String[] c_selectionArgs2= { dataURL };   			//String.valueOf(dataURL)
//					String c_orderBy = MediaStore.Audio.Playlists.Members._ID;	//MediaStore.Audio.Playlists.Members.PLAY_ORDER;
//					cursor = this.getContentResolver().query(uri, columns, c_selection, c_selectionArgs2, c_orderBy );
//					dbMsg +="に"+cursor.getCount() +"件";
//					if( cursor.moveToFirst() ){
//						retInt = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.PLAY_ORDER));
//					}
//				}else {
//					dbMsg +="指定リスト無し";
//					myPreferences.nowList = getResources().getString(R.string.listmei_zemkyoku);		// 全曲リスト
//					mainEditor.putString( "myPreferences.nowList", myPreferences.nowList);
//					myPreferences.nowList_id = myPreferences.pref_zenkyoku_list_id;
//					mainEditor.putString( "myPreferences.nowList_id", String.valueOf(myPreferences.nowList_id));
//					boolean kakikae = mainEditor.commit();	// データの保存
//					dbMsg += "、書き換え=" + kakikae;/////////////////////////////////////
//					retInt =itemUmuZenkyoku( dataURL);
//				}
//				playLists.close();
//			}else{
//				retInt =itemUmuZenkyoku( dataURL);
//			}
//			dbMsg +=",retInt="+retInt;
//			myLog(TAG,dbMsg);
//		} catch (Exception e) {
//			myErrorLog(TAG,dbMsg +"で"+e.toString());
//		}
//		return retInt;
//	}

//	public int itemUmuZenkyoku( String dataURL) {	//全曲リストの中に指定した曲が有るか		 ,String listName
//		int retInt = -1;
//		final String TAG = "itemUmuZenkyoku";
//		String dbMsg="";/////////////////////////////////////
//		try{
//			String fn = getString(R.string.zenkyoku_file);			//全曲リスト名
//			ZenkyokuHelper zenkyokuHelper = null;				//全曲リストヘルパー
//			SQLiteDatabase Zenkyoku_db;		//全曲リストファイル
//			//		dbMsg += ",fn=" + fn;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
//			zenkyokuHelper = new ZenkyokuHelper(getApplicationContext() , fn);		//全曲リストの定義ファイル		.MuList.this
//			Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
//			if( ! Zenkyoku_db.isOpen()){
//				Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
//		//		dbMsg += ">isOpen>" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
//			}
//			dbMsg += ",getPageSize=" + Zenkyoku_db.getPageSize() + "件中、" + dataURL + "は";			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
//			String zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
//			String[] c_columns =null;					//②引数tableには、テーブル名を指定します。
//			String c_selection = "DATA = ?";			//= "ALBUM_ARTIST LIKE ? AND ALBUM = ?";
//			String[] c_selectionArgs= { dataURL };   			// {"%" + artistMei + "%" , albumMei };
//			String c_orderBy= null; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
//			Cursor cursor = Zenkyoku_db.query(zenkyokuTName, c_columns, c_selection, c_selectionArgs , null, null, c_orderBy);	// table, columns,new String[] {MotoN, albamN}
//			dbMsg += ",getCount=" + cursor.getCount() + "件、" ;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
//			if( cursor.moveToFirst() ){
//				retInt = cursor.getInt(cursor.getColumnIndex("_id"))-1;
//			}
//			cursor.close();
//			dbMsg +=",retInt="+retInt;
//			myLog(TAG,dbMsg);
//		} catch (Exception e) {
//			myErrorLog(TAG,dbMsg +"で"+e.toString());
//		}
//		return retInt;
//	}
	//BlueTooth通信//////////////////////////////////////////////////////////////////////////
	static class btHandler extends Handler{
		public void handleMessage(Message msg) {
			final String TAG = "handleMessage";
			String dbMsg="";
			try {
				Intent broadcastIntent = new Intent();
				dbMsg +="message" + msg.toString() ;/////////////////////////////////////
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
		String dbMsg="";
		try{
			dbMsg +=",Buletooth=" +stateBaseStr;
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
		String dbMsg="";
		try{
			dbMsg +=",pref_toneList=" + pref_toneList;
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
			dbMsg +=",bands=" + bands + "件";
			toneList = new ArrayList<Map<String, Object>>();
			for (int i = bands; 0 < i ; i--) {
				dbMsg +="(" + i + "/" + bands + ")";
				String rStr = pref_toneList.get(i);
				dbMsg +=",rStr=" + rStr;
				String[] rAttay = rStr.split(toneSeparata);
				int freq = Integer.valueOf(rAttay[0]);						//mEqualizer.getCenterFreq((short) i) / 1000;					// イコライザの周波数帯の値を取得
				dbMsg +=",freq=" + String.format("%6dHz", freq);
				short band =  Short.valueOf(rAttay[1]);						//mEqualizer.getBandLevel((short) i);				// 現在のイコライザのバンドの値を取得
				dbMsg +=",band=" + String.format("%6d", band);
				String eStr = freq + toneSeparata+ band;
				objMap = new HashMap<String, Object>();				//汎用マップ
				objMap.put("tone_Hz" ,freq );													//高域調整周波数
				objMap.put("tone_Lev" ,band );													//高域調整レベル
				toneList.add( objMap);
			}			//for (int i = 0; i < bands; i++) {
			dbMsg +=",toneList=" + toneList;
			if( 0 < pref_toneList.size() ){
//				sharedPref = getSharedPreferences( getResources().getString(R.string.pref_main_file) ,MODE_WORLD_WRITEABLE);		//MODE_WORLD_WRITEABLE 	getSharedPreferences(prefFname,MODE_PRIVATE);
//				mainEditor = sharedPref.edit();
				mainEditor.putString("tone_name",getResources().getString(R.string.tone_name_puri));			//現在の設定
				mainEditor.putString("pref_toneList", pref_toneList.toString());							//再生中のプレイリストID
				Boolean kakikomi = mainEditor.commit();	// データの保存
				dbMsg +=",書き込み成功="+kakikomi;////////////////////////////////////////////////////////////////////////////
			}
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	/**
	 * Equalizer情報の更新
	 * createMediaPlayerIfNeedでmPlayer = MediaPlayer生成の後に呼出し
	 * */
	public void setEqualizer( ){					//Equalizer情報の更新
		final String TAG = "setEqualizer[MusicPlayerService]";
		String dbMsg="";
		try{
//			dbMsg +=",mPlayer=" + exoPlayer;
//			if( pref_toneList != null ){
//				dbMsg +=",pref_toneList=" + pref_toneList.size() + "件";
//				if( 0 == pref_toneList.size() ){
//					dbMsg +=",getAudioSessionId=" + exoPlayer.getAudioSessionId();
//					if( mEqualizer != null ){
//						mEqualizer.release();
//						mEqualizer = null;
//					}
//					mEqualizer = new Equalizer(0, exoPlayer.getAudioSessionId());		// Eaulizerを生成
//					dbMsg +=",mEqualizer=" + mEqualizer;
//					short bands = mEqualizer.getNumberOfBands();			// イコライザのバンド数
//					short minEQLevel = mEqualizer.getBandLevelRange()[0];			 // イコライザのバンドの最小値
//					dbMsg +=",EQLevel=" + minEQLevel;
//					short maxEQLevel = mEqualizer.getBandLevelRange()[1];
//					dbMsg +="～" + maxEQLevel;
//					toneList = new ArrayList<Map<String, Object>>();
//					for (int i = bands-1; -1 < i ; i--) {
//						dbMsg +="(" + i + "/" + bands + ")";
//						int freq = mEqualizer.getCenterFreq((short) i) / 1000;					// イコライザの周波数帯の値を取得
//						dbMsg +=",freq=" + String.format("%6dHz", freq);
//						short band = mEqualizer.getBandLevel((short) i);				// 現在のイコライザのバンドの値を取得
//						dbMsg +=",band=" + band;
//						equalizerPartKousinBody( i , freq , band );					//Equalizerの部分更新本体
//					}			//for (int i = 0; i < bands; i++) {
//				}
//			}
//			Thread.sleep(500);
//			equalizerSet = true;
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	/**
	 * Equalizerの部分更新
	 * **/
	public void equalizerPartKousin(Intent intent ){
		final String TAG = "equalizerPartKousin[MusicPlayerService]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			Bundle extras = intent.getExtras();
			int rdIndex = extras.getInt("rdIndex");				//更新するインデックス
			dbMsg="(" + rdIndex ;
			int freq = extras.getInt("tone_Hz");				//更新する周波数
			dbMsg +=";" + freq + "Hz)";
			int band = extras.getInt("tone_Lev");				//更新する調整レベル
			dbMsg += band;
			equalizerPartKousinBody( rdIndex , freq , band );					//Equalizerの部分更新本体
	//		Thread.sleep(500);
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	public void equalizerPartKousinBody(int rdIndex , int freq ,int band ) throws UnsupportedOperationException{					//Equalizerの部分更新本体
		final String TAG = "equalizerPartKousinBody[MusicPlayerService]";
		String dbMsg="";
		try{
			dbMsg +="(" + rdIndex ;
			dbMsg +=";" + freq + "Hz)";
			dbMsg += band;
	//02-16 17:22:12.578: E/equalizerPartKousinBody[MusicPlayerService](15613): (0;60Hz)2でjava.lang.NullPointerException
			dbMsg +=",exoPlayer=" + exoPlayer;
//			if(exoPlayer != null){
//				dbMsg +=",mEqualizer=" + mEqualizer;
//				if(mEqualizer != null){
//					mEqualizer.release();
//					mEqualizer =null;
//					dbMsg +=">>" + mEqualizer;
//				}
//				mEqualizer = new Equalizer(0, exoPlayer.getAudioSessionId());		// Eaulizerを生成
//				dbMsg +=",mEqualizer=" + mEqualizer.getCenterFreq((short) rdIndex) / 1000 + "Hz";
//				dbMsg +="," + mEqualizer.getBandLevel((short) rdIndex) + "db" ;
//				 try {
//					mEqualizer.setBandLevel(Short.valueOf(String.valueOf(rdIndex)), Short.valueOf(String.valueOf(band)) );							// イコライザにバンドの値を設定
//					mEqualizer.setEnabled(true);
//				} catch(Exception e) {
//					myErrorLog(TAG, "Create second effect: wait was interrupted.");
//					mEqualizer.release();
//				}
//				dbMsg +=">>" + mEqualizer.getBandLevel((short) rdIndex) + "db" ;
//			}
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

	/**
	 * ベースブーストOn/Off
	 * Create the BassBoost object and attach it to our media player.
	 * 	http://greety.sakura.ne.jp/redo/2011/01/abc2011w-gingerbread-sipaudiofx.html
	 * **/
	private void setupBassBoost(Intent intent ) {
		final String TAG = "setupBassBoost[MusicPlayerService]";
		String dbMsg="";
		try {
			Bundle extras = intent.getExtras();
			bBoot = extras.getBoolean("bBoot");
			dbMsg +=  "bBoot=" + bBoot;
			if(exoPlayer != null){
				bassBoostBody( bBoot );		//ベースブーストOn/Off本体
			}
		//	Thread.sleep(500);
	//		myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	/**
	 * ベースブーストOn/Off本体
	 * Create the BassBoost object and attach it to our media player.
	 * 	http://greety.sakura.ne.jp/redo/2011/01/abc2011w-gingerbread-sipaudiofx.html
	 * **/
	private void bassBoostBody(boolean  bBoot )  throws UnsupportedOperationException{
		final String TAG = "bassBoostBody[MusicPlayerService]";
		String dbMsg="";
		try {
//			dbMsg +=  "bBoot=" + bBoot;
//			if(mBassBoost != null){
//				mBassBoost.release();
//				mBassBoost = null;
//			}
//			mBassBoost = new BassBoost(0, exoPlayer.getAudioSessionId());
//			dbMsg +=",supported=" + mBassBoost.getStrengthSupported();
//			if(  mBassBoost.getStrengthSupported() ){
//				short roundedStrength = mBassBoost.getRoundedStrength();
//				dbMsg +=" roundedStrength = " + roundedStrength;
//				if (bBoot) {					//roundedStrength > 0
//					mBassBoost.setStrength((short) 1000); //effect and 1000 per mille designates the strongest
//				} else {
//					mBassBoost.setStrength((short) 0); // off
//				}
//				mBassBoost.setEnabled(true);
//				roundedStrength = mBassBoost.getRoundedStrength();
//				dbMsg +=">>" + roundedStrength;
//			}
//	//		myLog(TAG, dbMsg);
		} catch (UnsupportedOperationException e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
			if(mBassBoost != null){
				mBassBoost.release();
				mBassBoost = null;
			}
		}
	}

	/**
	 * リバーブ設定
	 * 	 Create the PresetReverb object and attach it to our media player.
	 * 	http://greety.sakura.ne.jp/redo/2011/01/abc2011w-gingerbread-sipaudiofx.html
	 * **/
	private void setupPresetReverb(Intent intent ) {
		final String TAG = "setupPresetReverb[MusicPlayerService]";
		String dbMsg="開始";/////////////////////////////////////
		try {
			Bundle extras = intent.getExtras();
			reverbBangou = extras.getShort("reverbBangou");					//リバーブ効果番号
			dbMsg +=  "reverbBangou=" + reverbBangou;
			if(exoPlayer != null){
				presetReverbBody(reverbBangou);					//リバーブ設定本体
			}
			final CharSequence[] items =getResources().getStringArray(R.array.effect_rev);
			reverbMei =( String.valueOf( items[reverbBangou]) );						//リバーブ効果名称;0解除				plNameSL.get(which) )
			dbMsg +=",reverbMei=" + reverbMei;
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {
	//		Toast.makeText(this, "PresetReverb: " + e.getMessage(), Toast.LENGTH_LONG).show();
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	/**
	 * リバーブ設定本体
	 * 	Create the PresetReverb object and attach it to our media player.
	 * //http://greety.sakura.ne.jp/redo/2011/01/abc2011w-gingerbread-sipaudiofx.html
	 * //https://osdn.jp/users/tagoh/pf/android_so01c/scm/blobs/master/frameworks/base/media/tests/MediaFrameworkTest/src/com/android/mediaframeworktest/functional/MediaPresetReverbTest.java	 * **/
	private void presetReverbBody(Short reverbBangou) throws UnsupportedOperationException{
		final String TAG = "presetReverbBody[MusicPlayerService]";
		String dbMsg="";
		try {
//			dbMsg +=  "reverbBangou=" + reverbBangou;
//			dbMsg +=",mPresetReverb=" + mPresetReverb;
//			if(mPresetReverb != null){
//				mPresetReverb.release();
//				mPresetReverb = null;
//			}
//			dbMsg +=",exoPlayer=" + exoPlayer;
//			exoPlayer.setAuxEffectSendLevel(1.0f);											//効果のセンドレベル；デフォルトは0.0fなので必要
//			mPresetReverb = new PresetReverb(0, exoPlayer.getAudioSessionId());
//			mPresetReverb.setPreset(reverbBangou);
//			mPresetReverb.setEnabled(true);
//			Thread.sleep(500);
//			dbMsg +=",Properties=" + mPresetReverb.getProperties().toString();
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
		final String TAG = "setupVisualizer[MusicPlayerService]";
		String dbMsg="";
		try{
			if( mVisualizer != null ){
				mVisualizer.release();
			}
			mVisualizer = null;
			if(  MusicPlayerService.this.exoPlayer != null ){
//				mVisualizer = new Visualizer( MusicPlayerService.this.exoPlayer.getAudioSessionId());					//セッションIDで紐付ける Create the Visualizer object and attach it to our media player.
//				mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
//				dbMsg +=",mVisualizer=" +mVisualizer;
//				ObjectResults objectResults = new ObjectResults( mVisualizer);					//Serializableを使ったオブジェクトの受け渡し
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

	/**アルバムIDからアルバムアートのUriを返す*/
	private Uri getAlbumArtUri(long albumId) {
		final String TAG = "getAlbumArtUri";
		String dbMsg= "";
		Uri retUri = null;
		try{
			dbMsg += ",albumId=" + albumId;
			Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
			retUri = ContentUris.withAppendedId(albumArtUri, albumId);
			dbMsg += ",retUri=" + retUri.toString();
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return retUri;
	}

	/**
	 * urlからタイトルなどの情報読み出し
	 * globalにセット
	 * */
	@SuppressLint("Range")
	public void getDataInfo(String urlStr){
		final String TAG = "getDataInfo";
		String dbMsg= "";
		try{
			dbMsg += "検索対象；" + urlStr;
			ContentResolver resolver = getApplicationContext().getContentResolver();	//c.getContentResolver();
			Uri cUri;
			if ( Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT) {
				cUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
			} else {
				cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			}
			String[] c_columns =null;					//②引数tableには、テーブル名を指定します。
			String c_selection =  MediaStore.Audio.Media.DATA +" = ?";			//MediaStore.Audio.Albums.ARTIST +" LIKE ?"
			String c_orderBy=MediaStore.Audio.Media.ARTIST; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
			String[] c_selectionArgs= { String.valueOf(urlStr) };		//"%" + albumArtist + "%"
			Cursor cursor = resolver.query( cUri , c_columns , c_selection , c_selectionArgs, c_orderBy);
			dbMsg += " <" + cursor.getCount() +"曲";
			//		myLog(TAG,dbMsg);
			if(cursor.moveToFirst()){
				@SuppressLint("Range") int tInt = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
				dbMsg += "[" + tInt;
				audioId=tInt;
				@SuppressLint("Range") String tStr = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
				dbMsg += "]" + tStr;
				creditArtistName=tStr;
				tStr = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
				dbMsg += "," + tStr;
				albumName=tStr;
				tStr = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
				dbMsg += "," + tStr;
				titolName=tStr;
				tStr = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
				dbMsg += "," + ORGUT.sdf_mss.format(Integer.parseInt(tStr)) ;
				saiseiJikan= Integer.parseInt(tStr);
				tStr = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
				dbMsg += " ," + tStr;
				albumArtUri = getAlbumArtUri(Long.parseLong(tStr));
				albumId= Long.parseLong(tStr);
				dbMsg += "[" + albumId + "]" + albumArtUri;
			}
			cursor.close();
			String[] items = urlStr.split(File.separator);
			albumArtistName = items[items.length-3];
			dbMsg += ",albumArtistName=" + albumArtistName;
			if(metadataBuilder == null){
				metadataBuilder = new MediaMetadata.Builder();
			}
			// To provide most control over how an item is displayed set the
			// display fields in the metadata
			metadataBuilder.putString(MediaMetadata.METADATA_KEY_MEDIA_ID, String.valueOf(audioId));
			metadataBuilder.putString(MediaMetadata.METADATA_KEY_MEDIA_URI, urlStr);
			metadataBuilder.putString(MediaMetadata.METADATA_KEY_TITLE, titolName);
			metadataBuilder.putString(MediaMetadata.METADATA_KEY_ARTIST, creditArtistName);
			metadataBuilder.putString(MediaMetadata.METADATA_KEY_ALBUM, albumName);
			metadataBuilder.putLong(MediaMetadata.METADATA_KEY_DURATION,Long.valueOf(saiseiJikan));
			metadataBuilder.putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI,albumArtUri.toString());
//			c_columns = null;                //③引数columnsには、検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
//			c_selection = MediaStore.Audio.Albums.ARTIST + " LIKE ?  AND " + MediaStore.Audio.Albums.ALBUM + " = ?";
//			String[] c_selectionArgs2 = {"%" + creditArtistName + "%", albumName};            //⑥引数groupByには、groupBy句を指定します。
//			c_orderBy = null;                                            //MediaStore.Audio.Albums.LAST_YEAR  ; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
//			Cursor cursor2 = getContentResolver().query(cUri, c_columns, c_selection, c_selectionArgs2, c_orderBy);
//			dbMsg += "\nAlbums=" + cursor2.getCount() + "件";/////////////////////////////////////
//			if (cursor2.moveToFirst()) {
//				int cols = cursor2.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
//				if(-1 < cols) {
//					album_art = cursor2.getString(cols);
//				}
//			}
//			cursor2.close();
//			dbMsg +="、ジャケット写真=" + album_art ;/////////////////////////////////////
//			if ( album_art ==null ) {
//				album_art =ORGUT.retAlbumArtUri( getApplicationContext() , creditArtistName , albumName );			//アルバムアートUriだけを返すalbumArtist		MaraSonActivity.this  ,
//				dbMsg +=">>" + album_art ;/////////////////////////////////////
//			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**指定されたリストを内部配列に読み込む*/
	@SuppressLint("Range")
	public ArrayList<String> readCalentList(int playlistId , String listName){
		final String TAG = "readCalentList";
		String dbMsg= "";
		ArrayList<String> pSL =  new ArrayList<String>();				//Urlだけのプレイリスト用簡易リスト
		try{
			dbMsg += "選択されたプレイリスト[ID="+playlistId + "]" + listName;
			pSL.clear();
			if(listName.equals(MusicPlayerService.this.getResources().getString(R.string.all_songs_file_name))){
				ZenkyokuHelper zenkyokuHelper = null;				//全曲リストヘルパー
				SQLiteDatabase Zenkyoku_db;		//全曲リストファイル
				zenkyokuHelper = new ZenkyokuHelper(this ,  getString(R.string.zenkyoku_file));		//全曲リストの定義ファイル		.
				Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
				dbMsg =  ">isOpen>" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
				dbMsg =  ",getPageSize=" + Zenkyoku_db.getPageSize() + "件、" ;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
				String table = getResources().getString(R.string.zenkyoku_table);
				Zenkyoku_db = zenkyokuHelper.getReadableDatabase();			// データベースをオープン
				String[] columns =null;			//{  "ALBUM_ARTIST" , "ARTIST"};				//検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
				String selections = null;	//"ALBUM_ARTIST = ? ";			//+ comp ;		//MediaStore.Audio.Media.ARTIST +" <> " + comp;			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
				String[] selectionArgs = null;	//new String[]{ comp };
				String groupBy = null;					//groupBy句を指定します。
				String having =null;					//having句を指定します。
				String orderBy = null;  //"ALBUM_ARTIST_INDEX";
				String limit = null;					//検索結果の上限レコードを数を指定します。
				Cursor zCursor = Zenkyoku_db.query( table ,columns, selections,  selectionArgs,  groupBy,  having,  orderBy,  limit) ;
				dbMsg = ",全曲=" + zCursor.getCount() + "件";
				if(zCursor.moveToFirst()){
					do{
						@SuppressLint("Range")  String cVal = zCursor.getString(zCursor.getColumnIndex("DATA"));
						pSL.add(cVal);
					}while( zCursor.moveToNext() ) ;				//pdCoundtVal <  pdMaxVal
				}
				zCursor.close();

			}else{
				final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
				String[] columns = null;				//{ idKey, nameKey };
				String c_selection = null;				//MediaStore.Audio.Playlists.NAME +" = ? ";
				String[] c_selectionArgs = null;			//{listName};        //⑥引数groupByには、groupBy句を指定します。
				String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
				Cursor rCursor= this.getContentResolver().query(uri, columns, c_selection, c_selectionArgs, c_orderBy );
				if(rCursor.moveToFirst()){
					do{
						String cVal = rCursor.getString(rCursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA));
						pSL.add(cVal);
					}while( rCursor.moveToNext() ) ;				//pdCoundtVal <  pdMaxVal
				}
				rCursor.close();

			}
			dbMsg += "、"+pSL.size() + "件";
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return pSL;
	}


	private MediaBrowserCompat.MediaItem createMediaItem(String mediaId, String dataUri, String artistName,String albumName, String title, String duration) {
		MediaMetadataRetriever retriever;
		MediaDescriptionCompat mediaDescriptionCompat;
		final String TAG = "createMediaItem";
		String dbMsg= "";
		MediaBrowserCompat.MediaItem retItem = null;
		try{
			dbMsg+= "[" + mediaId ;
			dbMsg+= "]" + dataUri + ":artist=" +  artistName ;
			dbMsg+= ",album="+ albumName ;
			dbMsg+= "," + title ;
			dbMsg+= "," + duration +"[mS]";

			MediaDescription.Builder mediaDescriptionBuilder = new MediaDescription.Builder();
			mediaDescriptionBuilder.setMediaId(mediaId);			//"sample.mp3"
			mediaDescriptionBuilder.setMediaUri(Uri.parse(dataUri));
			mediaDescriptionBuilder.setTitle(title);
			mediaDescriptionBuilder.setSubtitle(artistName);
			//	mediaDescriptionBuilder.setIconUri(createArt(retriever));
			Bundle extras = new Bundle();
			extras.putInt(
					MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_SINGLE_ITEM,
					MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_LIST_ITEM);
			extras.putInt(
					MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_BROWSABLE,
					MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_LIST_ITEM);
			extras.putInt(
					MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_PLAYABLE,
					MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM);
			mediaDescriptionBuilder.setExtras(extras);
			retItem = MediaBrowserCompat.MediaItem.fromMediaItem(new MediaBrowser.MediaItem(
					mediaDescriptionBuilder.build(), MediaBrowser.MediaItem.FLAG_BROWSABLE));

//			mediaDescriptionCompat = MediaDescriptionCompat.fromMediaDescription(new MediaMetadataCompat.Builder()
//				.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID,mediaId )			//"sample.mp3"
//				.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI,dataUri)
//				.putString(MediaMetadataCompat.METADATA_KEY_TITLE,title)
//				.putString(MediaMetadataCompat.METADATA_KEY_ARTIST,artistName)
//				.putString(MediaMetadataCompat.METADATA_KEY_ALBUM,albumName)
//				.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, Long.parseLong(duration)*1000)
//	//				.putBitmap(MediaMetadataCompat.METADATA_KEY_ART,createArt(retriever))
//				.build());
//			retItem = new MediaBrowserCompat.MediaItem(mediaDescriptionCompat, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
//
			dbMsg+= ",retItem.id=" + retItem.getMediaId() ;
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return retItem;
	}

	private MediaSessionCompat.QueueItem createQueueItem(int index , String mediaId, String dataUri, String artistName,String albumName, String title, String duration) {
		MediaMetadataRetriever retriever;
		MediaDescriptionCompat mediaDescriptionCompat;
		final String TAG = "createQueueItem";
		String dbMsg= "";
		MediaSessionCompat.QueueItem retItem = null;
		try{
			dbMsg+= "[" + mediaId ;
			dbMsg+= "]" + dataUri + ":artist=" +  artistName ;
			dbMsg+= ",album="+ albumName ;
			dbMsg+= "," + title ;
			dbMsg+= "," + duration +"[mS]";
			dbMsg+= "," + index +"件目";


//			MediaDescription.Builder mediaDescriptionBuilder = new MediaDescription.Builder();
//			mediaDescriptionBuilder.setMediaId(mediaId);			//"sample.mp3"
//			mediaDescriptionBuilder.setMediaUri(Uri.parse(dataUri));
//			mediaDescriptionBuilder.setTitle(title);
//			mediaDescriptionBuilder.setSubtitle(artistName);
//			//	mediaDescriptionBuilder.setIconUri(createArt(retriever));
//			Bundle extras = new Bundle();
//			extras.putInt(
//					MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_SINGLE_ITEM,
//					MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_CATEGORY_LIST_ITEM);
//			extras.putInt(
//					MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_BROWSABLE,
//					MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_LIST_ITEM);
//			extras.putInt(
//					MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_PLAYABLE,
//					MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM);
//			mediaDescriptionBuilder.setExtras(extras);
			// 再生データ情報の設定
			MediaDescription.Builder mdb1 = new MediaDescription.Builder();

			// 再生データ1
			final Bundle mediaBundle1 = new Bundle();
			mediaBundle1.putString("Path", dataUri); // データパス
			mediaBundle1.putInt("Index", index);                      // 曲のインデックス
			mdb1.setMediaId(mediaId);                         // メディアID
			mdb1.setTitle(title);                             // タイトル
			mdb1.setSubtitle(artistName);                       // サブタイトル
			mdb1.setExtras(mediaBundle1);
			retItem = MediaSessionCompat.QueueItem.fromQueueItem(new MediaSession.QueueItem(mdb1.build(), 0));
//			JSONArray mPlayingQueue = null;
//			retItem = (MediaSessionCompat.QueueItem) mPlayingQueue.get(Integer.parseInt(mediaId));

//			mediaDescriptionCompat = MediaDescriptionCompat.fromMediaDescription(new MediaMetadataCompat.Builder()
//				.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID,mediaId )			//"sample.mp3"
//				.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI,dataUri)
//				.putString(MediaMetadataCompat.METADATA_KEY_TITLE,title)
//				.putString(MediaMetadataCompat.METADATA_KEY_ARTIST,artistName)
//				.putString(MediaMetadataCompat.METADATA_KEY_ALBUM,albumName)
//				.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, Long.parseLong(duration)*1000)
//	//				.putBitmap(MediaMetadataCompat.METADATA_KEY_ART,createArt(retriever))
//				.build());
//			retItem = new MediaBrowserCompat.MediaItem(mediaDescriptionCompat, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
//
			dbMsg+= ",retItem.id=" + retItem.getQueueItem().toString() ;			//
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return retItem;
	}

	/**指定されたリストを内部配列に読み込む*/
	@SuppressLint("Range")
	public List<MediaSessionCompat.QueueItem> setMediaItemList(int playlistId , String listName){
		final String TAG = "setMediaItemList";
		String dbMsg= "";
		List<MediaSessionCompat.QueueItem> pSL = new ArrayList<>();
		try{
			dbMsg += "選択されたプレイリスト[ID="+playlistId + "]" + listName;
			int i =0 ;
			pSL.clear();
			if(listName.equals(MusicPlayerService.this.getResources().getString(R.string.all_songs_file_name))){
				ZenkyokuHelper zenkyokuHelper = null;				//全曲リストヘルパー
				SQLiteDatabase Zenkyoku_db;		//全曲リストファイル
				zenkyokuHelper = new ZenkyokuHelper(this ,  getString(R.string.zenkyoku_file));		//全曲リストの定義ファイル		.
				Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
				dbMsg +=  ">isOpen>" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
				dbMsg +=  ",getPageSize=" + Zenkyoku_db.getPageSize() + "件、" ;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
				String table = getResources().getString(R.string.zenkyoku_table);
				Zenkyoku_db = zenkyokuHelper.getReadableDatabase();			// データベースをオープン
				String[] columns =null;			//{  "ALBUM_ARTIST" , "ARTIST"};				//検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
				String selections = null;	//"ALBUM_ARTIST = ? ";			//+ comp ;		//MediaStore.Audio.Media.ARTIST +" <> " + comp;			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
				String[] selectionArgs = null;	//new String[]{ comp };
				String groupBy = null;					//groupBy句を指定します。
				String having =null;					//having句を指定します。
				String orderBy = null;  //"ALBUM_ARTIST_INDEX";
				String limit = null;					//検索結果の上限レコードを数を指定します。
				Cursor zCursor = Zenkyoku_db.query( table ,columns, selections,  selectionArgs,  groupBy,  having,  orderBy,  limit) ;
				dbMsg += ",全曲=" + zCursor.getCount() + "件";

				if(zCursor.moveToFirst()){
					do{
						Long audioID = Long.valueOf(zCursor.getString(zCursor.getColumnIndex("AUDIO_ID")));
//						MediaSessionCompat.QueueItem media;
//						media.

						//					for (MediaBrowserCompat.MediaItem media : MusicLibrary.getMediaItems()) {
//						queueItems.add(new MediaSessionCompat.QueueItem(media.getDescription(), i));
//						i++;
//					}
				//		@SuppressLint("Range")  String cVal = zCursor.getString(zCursor.getColumnIndex("DATA"));
						i++;
						//	pSL.size(),
						MediaSessionCompat.QueueItem queueItem = createQueueItem(
							i,
							zCursor.getString(zCursor.getColumnIndex("AUDIO_ID")),
							zCursor.getString(zCursor.getColumnIndex("DATA")),
							zCursor.getString(zCursor.getColumnIndex("ARTIST")),
							zCursor.getString(zCursor.getColumnIndex("ALBUM")),
							zCursor.getString(zCursor.getColumnIndex("TITLE")),
							zCursor.getString(zCursor.getColumnIndex("DURATION"))
						);
						String path = queueItem.getDescription().getExtras().getString("Path");
						pSL.add(queueItem);
					//	dbMsg += "[" + pSL.size() + "]" + path;
					}while( zCursor.moveToNext() ) ;				//pdCoundtVal <  pdMaxVal
				}
				zCursor.close();

			}else{
				final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
				String[] columns = null;				//{ idKey, nameKey };
				String c_selection = null;				//MediaStore.Audio.Playlists.NAME +" = ? ";
				String[] c_selectionArgs = null;			//{listName};        //⑥引数groupByには、groupBy句を指定します。
				String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
				Cursor rCursor= this.getContentResolver().query(uri, columns, c_selection, c_selectionArgs, c_orderBy );
				if(rCursor.moveToFirst()){
					do{
						i++;
						MediaSessionCompat.QueueItem queueItem = createQueueItem(
								i,
								rCursor.getString(rCursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)),
								rCursor.getString(rCursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA)),
								rCursor.getString(rCursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST)),
								rCursor.getString(rCursor.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM)),
								rCursor.getString(rCursor.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE)),
								rCursor.getString(rCursor.getColumnIndex(MediaStore.Audio.Playlists.Members.DURATION))
						);
						pSL.add(queueItem);
			//			pSL.add(MediaSessionCompat.QueueItem.fromQueueItem(cVal));
					}while( rCursor.moveToNext() ) ;				//pdCoundtVal <  pdMaxVal
				}
				rCursor.close();
			}
			dbMsg += "、"+pSL.size() + "件";
			dbMsg += "[" + i + "]" + queueItem.getDescription().getExtras().getString("Path");;
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return pSL;
	}



	/**リスト中のインデックスを返す*/
	public int getCarentIndex(String urlStr,ArrayList<String> pSL){
		final String TAG = "getCarentIndex";
		String dbMsg= "";
		int retInt=-1;
		try{
			dbMsg += "検索対象；" + urlStr;
			retInt = pSL.indexOf(urlStr);
			dbMsg += "," + retInt + " / " + pSL.size() + "件";
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return retInt;
	}

	//以下、旧記載////////////////////////////////////////////////////////////////////////////////////////
	int okuriIti =0;
  	public void onAudioFocusChange(int focusChange) {
		final String TAG = "onAudioFocusChange[MusicPlayerService]";
		String dbMsg=ORGUT.nowTime(true,true,true);/////////////////////////////////////
		try {
			dbMsg="focusChange="+focusChange+",isPlaying="+exoPlayer.isPlaying();/////////////////////////////////////
			myLog(TAG,dbMsg);
			switch (focusChange) {
				case AudioManager.AUDIOFOCUS_GAIN:            // resume playback
					if ( exoPlayer == null ) {
						int mcPosition = (int) exoPlayer.getCurrentPosition();
						//initMediaPlayer();
						if ( !exoPlayer.isPlaying() ) {
							dbMsg = dbMsg + ",mcPosition=" + mcPosition;/////////////////////////////////////
							exoPlayer.seekTo(mcPosition);
							exoPlayer.setPlayWhenReady(true);
							mState = State.Playing;
				//			exoPlayer.setVolume(1 , 1);
							myLog(TAG , dbMsg);
						}
					}
	  			break;
	  		case AudioManager.AUDIOFOCUS_LOSS:			// Lost focus for an unbounded amount of time: stop playback and release media player
	  			if (exoPlayer.isPlaying()){
	  				myLog(TAG,dbMsg);
					exoPlayer.setPlayWhenReady(false);
	  				exoPlayer.release();
	  				exoPlayer = null;
	  			}
	  			break;

	  		case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:			// Lost focus for a short time, but we have to stop playback.
	  																// We don't release the media player because playback is likely to resume
	  			if (exoPlayer.isPlaying()){
					exoPlayer.setPlayWhenReady(false);
	  			}
	  			break;

	  		case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:			// Lost focus for a short time, but it's ok to keep playing at an attenuated level
	  			if (exoPlayer.isPlaying()){
	  	//			exoPlayer.setVolume(0.1f, 0.1f);
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
		final String TAG = "onCallStateChanged";
		String dbMsg="";
		try{
			dbMsg +="state = " + state;/////////////////////////////////////
			dbMsg +=" , number = " + number;/////////////////////////////////////
			phoneCallEvent(state, number);
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
		}
	};

	private void phoneCallEvent(int state, String number){
		final String TAG = "phoneCallEvent[MusicPlayerService]";
		String dbMsg="";
		try{
//			sharedPref = getSharedPreferences( getResources().getString(R.string.pref_main_file) ,MODE_PRIVATE);		//	getSharedPreferences(prefFname,MODE_PRIVATE);
//			mainEditor = sharedPref.edit();
			dbMsg="state = " + state;/////////////////////////////////////
			dbMsg +=" , number = " + number;/////////////////////////////////////
			switch(state) {	  								  /* 各状態でTextViewを追加する */
			case TelephonyManager.CALL_STATE_RINGING:	/* 着信 */
				dbMsg +=">>着信;exoPlayer=" + exoPlayer;/////////////////////////////////////
				if(exoPlayer != null){
					dbMsg +=">>isPlaying=" + exoPlayer.isPlaying();/////////////////////////////////////
					if(exoPlayer.isPlaying()){
						setPref();		//プリファレンス記載
						exoPlayer.setPlayWhenReady(false);
					}
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:	/* 1；通話 */
				dbMsg +=">>通話";/////////////////////////////////////
				break;
			case TelephonyManager.CALL_STATE_IDLE:	/* 0；待ち受け */
//				dbMsg +=">>待ち受け;mPlayer=" + mPlayer;/////////////////////////////////////
//				Map<String, ?> keys = sharedPref.getAll();
//				dbMsg +=",keys=" +keys.size() +"項目" ;/////////////////////////////////////
//				pref_cyakusinn_fukki= Boolean.valueOf(String.valueOf(keys.get("pref_cyakusinn_fukki")));			//終話後に自動再生
//				dbMsg +=",終話後に自動再生=" + pref_cyakusinn_fukki ;/////////////////////////////////////
//				if(pref_cyakusinn_fukki){
////					String dataFN = String.valueOf(keys.get("pref_myPreferences.pref_data_url"));				//再生中のファイル名  Editor に値を代入
////					dbMsg +="," +dataFN;
////					mcPosition = Integer.valueOf(String.valueOf(keys.get("pref_position")));				//選択中選択ポジション
////					dbMsg +="[mcPosition =" + mcPosition + "ms]";/////////////////////////////////////
//					if(mPlayer != null){
//						dbMsg +=">>isPlaying=" + mPlayer.isPlaying();/////////////////////////////////////
//						if(! mPlayer.isPlaying()){
//							int mcPosition = getPrefInt("pref_position" , 0, MusicPlayerService.this);  //sharedPref.getInt("pref_position" , 0);
////							mcPosition = Integer.valueOf(String.valueOf(keys.get("pref_position")));				//選択中選択ポジション
//							dbMsg +="[mcPosition =" + mcPosition + "ms]";/////////////////////////////////////
//				//			mPlayer.pause();			//pauseから復帰せず
//							dbMsg +=">mcPosition=" + mcPosition;/////////////////////////////////////
//							mPlayer.seekTo(mcPosition);
//							mPlayer.start();
//							mState = State.Playing;
//				//			playMuFile(dataFN, mcPosition);	//再生開始
//						}
//					}else{
//				//		playMuFile(dataFN, mcPosition);	//再生開始
//					}
//				}
				break;
			}
			if(exoPlayer != null){
				dbMsg +=">>isPlaying=" + exoPlayer.isPlaying();/////////////////////////////////////
			}
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	/**レシーバーを生成*/
	public void receiverSeisei(){		// <onResume , playing , mData2Service	onClick
		final String TAG = "receiverSeisei[MusicPlayerService]";
		String dbMsg="";
		try{
//			if( btReceiver== null && myPreferences.nowList_data ){
//				dbMsg +=  "Bluetooth;";/////////////////////////////////////
//				if(mBluetoothAdapter == null){
//					mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();			// Get local Bluetooth adapter
//					dbMsg +=",BluetoothAdapterは" + mBluetoothAdapter;/////////////////////////////////////
//				}
//				IntentFilter btFilter = new IntentFilter();									//BluetoothA2dpのACTIONを保持する
//				btFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);			//20160414
//				btFilter.addAction(BluetoothDevice.ACTION_FOUND);							//20160414
//				btFilter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);					//20160414
//				btFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);			//20160414
//				btFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
//				btFilter.addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED);
//				btFilter.addAction(Intent.ACTION_MEDIA_BUTTON);							//操作ボタン対応？
//				dbMsg += ",btReceiver=" + btReceiver;////////////////////////
//				if( btReceiver == null ){
//					btReceiver = new BuletoohtReceiver();				//BuletoohtReceiver();
//	//				btReceiver.service = this;		//this
//	//		//		btHandler = new Handler();
//				}
//				registerReceiver(btReceiver, btFilter);
//				dbMsg +=">生成>=" + btReceiver;///////////////////////
//			}
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	/**レシーバーを破棄*/
	public void receiverHaki(){
		final String TAG = "receiverHaki[MusicPlayerService]";
		String dbMsg="";
		try{
			dbMsg += ",btReceiver=" + btReceiver;////////////////////////
			if( btReceiver != null ){
//				if(mBluetoothAdapter == null){
//					mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();			// Get local Bluetooth adapter
//				}
//				mBluetoothAdapter.cancelDiscovery(); //検索キャンセル
//				unregisterReceiver(btReceiver);
				btReceiver = null;
				dbMsg += ">>" + btReceiver;////////////////////////
			}
//			dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	private void quitMe( int startId ) {			//このサービスを閉じる
		final String TAG = "quitMe";
		String dbMsg="";/////////////////////////////////////
		try{
			dbMsg += "mIndex=" + mIndex +",frCount="+ frCount+";選択中="+ sentakuCyuu;/////////////////////////////////////
			processStopRequest(false);															//②ⅲStop?eタイマーを破棄してmPlayerの破棄へ
			mState = State.Stopped;		// Service is being killed, so make sure we release our resources
			dbMsg += ",g_timer="+ g_timer ;/////////////////////////////////////
			if(g_timer != null){
				g_timer.cancel();		//cancelメソッド実行後は再利用できない
				g_timer.purge();
				g_timer=null;
			}

			giveUpAudioFocus();			//AudioFocus.NoFocusNoDuckへ設定
	//		myLog(TAG,dbMsg);
			receiverHaki();							//レシーバーを破棄
//			dbMsg += ",lpNotification=" + lpNotification;//,lpNotification=Notification(pri=0 contentView=com.hijiyam_koubou.marasongs/0x1090080 vibrate=null sound=null defaults=0x0 flags=0x10 color=0xff333333 category=transport actions=3 vis=PRIVATE),
//			if(lpNotification != null){
//			//	dbMsg += ",lpNControls=" + lpNControls;//lpNControls=android.media.session.MediaController$TransportControls@16c85e3a
//				NotificationManager notifManager= (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//				notifManager.cancel(NOTIFICATION_ID);
//				dbMsg += ">>" + lpNotification;
//			}
//			dbMsg += ",mNotificationManager=" + mNotificationManager;//mNotificationManager=null,
//			if( mNotificationManager != null ){
//				mNotificationManager.cancel(NOTIFICATION_ID);
//				mNotificationManager = null;
//				dbMsg += ">>" + mNotificationManager;/////////////////////////////////////
//			}
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
	//Service com.hijiyam_koubou.marasongs.MusicPlayerService has leaked IntentReceiver com.hijiyam_koubou.marasongs.BuletoohtReceiver@42f0d470 that was originally registered here. Are you missing a call to unregisterReceiver()?
			//unregisterReceiverされていない
			dbMsg += ",startId=" + startId;////////////////////////
			boolean storR = MusicPlayerService.this.stopSelfResult(startId);
			dbMsg += ",storR=" + storR;////////////////////////
			if( storR ){						//サービスが消去できれば
				nowSartId = 0;			//idも消去
			}
			dbMsg += ",nowSartId=" + nowSartId;////////////////////////
			stopSelf();
			dbMsg += ",mBinder=" + mBinder;////////////////////////

			MusicPlayerService.this.stopSelf();
			myLog(TAG,dbMsg);
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
//	//@Override
//	protected void onHandleIntent(Intent intent) {
//		final String TAG = "onHandleIntent[MusicPlayerService]";
//		String dbMsg="";
//		try {
//			Thread.sleep(10000);
//			Intent broadcastIntent = new Intent();
//			//	  public Item playingItem;
//			dbMsg +="artist=" +creditArtistName ;//////////////////////////
//			broadcastIntent.putExtra("artist", creditArtistName);						//クレジットアーティスト名
//			dbMsg +="album_artist=" + album_artist;/////////////////////////////////////
//			broadcastIntent.putExtra("album_artist", album_artist);		//リストアップしたアルバムアーティスト名
//			dbMsg +=",album=" + albumName;
//			broadcastIntent.putExtra("albumName", albumName);			//アルバム名
//			dbMsg +=",titol=" + titolName;
//			broadcastIntent.putExtra("titolName", titolName);			//曲名
//			dbMsg +=",dataFN=" + dataFN;
//			broadcastIntent.putExtra("dataFN", dataFN);				//DATA;The data stream for the file ;Type: DATA STREAM
//			dbMsg +="[" + mcPosition;
//			broadcastIntent.putExtra("mcPosition", mcPosition);		//現在の再生ポジション☆生成時は最初から
//			dbMsg +="/" + saiseiJikan +"]";
//			broadcastIntent.putExtra("saiseiJikan", saiseiJikan);		//DURATION;継続;The duration of the audio file, in ms;Type: INTEGER (long)
//			broadcastIntent.setAction("mFilter");
//			dbMsg +=";Broadcast送信";
//			myLog(TAG,dbMsg);
//			getBaseContext().sendBroadcast(broadcastIntent);
//		} catch (InterruptedException e) {
//			myErrorLog(TAG,dbMsg+"で"+e);
//		}
//	}

	private final IBinder mBinder = new MusicPlayBinder();
	public class MusicPlayBinder extends Binder {
		public MusicPlayerService getService() {
			final String TAG = "MusicPlayBinder[]";
			String dbMsg="[MusicPlayerService]bindServicから開始";/////////「プロセス間通信」（IPC：Inter Process Communication）によるリモートメソッドコールを行う
			try{
//				Intent notificationIntent = new Intent(getApplication(), MusicPlayerService.class);									// 通知押下時に、MusicPlayServiceのonStartCommandを呼び出すためのintent
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
				dbMsg += "MusicPlayerService=" + MusicPlayerService.this;
				myLog(TAG,dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"で"+e);
			}
			return MusicPlayerService.this;
		}
	}

	/**他のコンポーネントがサービスにバインドするとき呼び出される*/
	@Override
	public IBinder onBind(Intent arg0) {
		final String TAG = "onBind";
		String dbMsg="";
		dbMsg +="bindServicから開始";/////////「プロセス間通信」（IPC：Inter Process Communication）によるリモートメソッドコールを行う
		try{
			if(musicPlaylist == null){
				musicPlaylist = new MusicPlaylist(MusicPlayerService.this);
			}
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
		return null;				//サービスの実体を返します
	}

	@Override
	public boolean onUnbind(Intent arg0) {
		final String TAG = "onUnbind[MusicPlayerService]";
		String dbMsg="";
		try{
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
		dbMsg="onDestroyへ";/////////////////////////////////////
		return false;
	}

	///MediaBrowserServiceCompat////////////////////
	private static final String MY_MEDIA_ROOT_ID = "media_root_id";
	private static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";
	private PlaybackStateCompat.Builder stateBuilder;
	public MediaSource mSource = null;

	/** [onLoadChildren]でparentIdに入ってくる。Android 11のメディアの再開の場合はこの値 */
	private String ROOT_RECENT = "root_recent";
	/** [onLoadChildren]でparentIdに入ってくる。[ROOT_RECENT]以外の場合 */
	private String ROOT = "root";
	private List<MediaBrowserCompat.MediaItem> mediaItems;

	////イマドキなAndroid音楽プレーヤーの作り方  https://qiita.com/siy1121/items/f01167186a6677c22435 ///////
	final String lTAG = MusicPlayerService.class.getSimpleName();//ログ用タグ
	final String ROOT_ID = "root";//クライアントに返すID onGetRoot / onLoadChildrenで使用

	Handler handler;//定期的に処理を回すためのHandler
	int index = 0;//再生中のインデックス

	ExoPlayer exoPlayer;//音楽プレイヤーの実体

	List<MediaSessionCompat.QueueItem> queueItems = new ArrayList<>();//キューに使用するリスト
	/**MediaBrowserService に接続しようとした時*/
	@Nullable
	@Override
	public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
		final String TAG = "onGetRoot";
		String dbMsg="";
		BrowserRoot br =null;
		try{
			// 最後の曲をリクエストしている場合はtrue
			boolean isRequestRecentMusic = rootHints.getBoolean(BrowserRoot.EXTRA_RECENT);	// false;					;
			dbMsg +=",最後の曲をリクエストしている=" + isRequestRecentMusic;
			// BrowserRootに入れる値を変える
			String rootPath = ROOT;
			if (isRequestRecentMusic){
				rootPath = ROOT_RECENT;
			}
			dbMsg +=",rootPath=" + rootPath;
			myLog(TAG,dbMsg);
			br =  new BrowserRoot(rootPath, null);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
		return br;
	}

	/**Activityや他デバイス（Wear OSとか車とか？）に返す曲を読み込み*/
	@Override
	public void onLoadChildren(@NonNull String parentMediaId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
		final String TAG = "onLoadChildren";
		String dbMsg="";
		try{
			dbMsg += ",parentMediaId=" + parentMediaId;
			//  Browsing not allowed
			if (TextUtils.equals(MY_EMPTY_MEDIA_ROOT_ID, parentMediaId)) {
				result.sendResult(null);
				return;
			}

			// Assume for example that the music catalog is already loaded/cached.
			dbMsg += "[" + myPreferences.nowList_id +  "]" + myPreferences.nowList;
			mediaItems = new ArrayList<>();
//			mediaItems = setMediaItemList(Integer.parseInt(myPreferences.nowList_id), myPreferences.nowList);
			dbMsg += ">>mediaItems=" + mediaItems.size() + "件";
			// Check if this is the root menu:
			if (MY_MEDIA_ROOT_ID.equals(parentMediaId)) {
				// Build the MediaItem objects for the top level,
				// and put them in the mediaItems list...
			} else {
				// Examine the passed parentMediaId to see which submenu we're at,
				// and put the children of that menu in the mediaItems list...
			}
			dbMsg += ",mediaItems=" + mediaItems.size() + "件";
			result.sendResult(mediaItems);
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}
	////////////イマドキなAndroid音楽プレーヤーの作り方   ///////
	private IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

	// Defined elsewhere...
//	private AudioManager.OnAudioFocusChangeListener afChangeListener;
//	private BecomingNoisyReceiver myNoisyAudioStreamReceiver = new BecomingNoisyReceiver();
//	private MediaStyleNotification myPlayerNotification;
	private MediaBrowserService service;
//	private SomeKindOfPlayer player;

	private AudioFocusRequest audioFocusRequest;


//	/** フォアグラウンドサービスを起動する */
//	private void startThisService() {
////		Intent playlistPlayServiceIntent = Intent(this, BackgroundPlaylistCachePlayService::; class.java)
////		// 起動
////		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////			startForegroundService(playlistPlayServiceIntent)
////		} else {
////			startService(playlistPlayServiceIntent)
////		}
//	}

	///MediaSession用コールバック////////////////////
	private MediaSessionCompat.Callback MySessionCallback = new MediaSessionCompat.Callback() {
		//曲のIDから再生する
		//WearやAutoのブラウジング画面から曲が選択された場合もここが呼ばれる
		@Override
		public void onPlayFromMediaId(String mediaId, Bundle extras) {
			final String TAG = "onPlayFromMediaId";
			String dbMsg="[MySessionCallback]";
			try{
				dbMsg +=",mediaId=" + mediaId;
				//今回はAssetsフォルダに含まれる音声ファイルを再生
				//Uriから再生する
				DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), Util.getUserAgent(getApplicationContext(), "AppName"));
				MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse("file:///android_asset/" + mLibrary.getMusicFilename(mediaId)));

				//今回は簡易的にmediaIdからインデックスを割り出す。
				for (MediaSessionCompat.QueueItem item : queueItems)
					if (item.getDescription().getMediaId().equals(mediaId))
						index = (int) item.getQueueId();

				exoPlayer.prepare(mediaSource);
				mSession.setActive(true);
				onPlay();

				//MediaSessionが配信する、再生中の曲の情報を設定
				mSession.setMetadata(mLibrary.getMetadata(getApplicationContext(), mediaId));
				myLog(TAG,dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"で"+e);
			}
		}

		@Override
		public void onPlay() {
			final String TAG = "onPlay";
			String dbMsg="[MySessionCallback]";
			try{
				//オーディオフォーカスを要求
				if (mAudioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
					//取得できたら再生を始める
					mSession.setActive(true);
					exoPlayer.setPlayWhenReady(true);
				}

////				startThisService();
//			//	isActive = true;
//
//				AudioManager am = (AudioManager) rContext.getSystemService(Context.AUDIO_SERVICE);
//				// Request audio focus for playback, this registers the afChangeListener
//				AudioAttributes attrs = new AudioAttributes.Builder()
//						.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//						.build();
//				audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
//						.setOnAudioFocusChangeListener(afChangeListener)
//						.setAudioAttributes(attrs)
//						.build();
//				int result = am.requestAudioFocus(audioFocusRequest);
//				dbMsg +=",isPlaying=" + exoPlayer.isPlaying();
//				if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//					// Start the service
//					startService(new Intent(rContext, MediaBrowserService.class));
//					// Set the session active  (and update metadata and state)
//					mSession.setActive(true);
//					// start the player (custom call)
////						exoPlayer.start(); もしくは
//					exoPlayer.setPlayWhenReady(true);
//					// Register BECOME_NOISY BroadcastReceiver
//	//				registerReceiver(myNoisyAudioStreamReceiver, intentFilter);
//					// Put the service in the foreground, post notification
//	//			service.startForeground(id, myPlayerNotification);
//				}
				dbMsg +=">>" + exoPlayer.isPlaying();
				myLog(TAG,dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"で"+e);
			}
		}

		@Override
		public void onPause() {
			final String TAG = "onPause";
			String dbMsg="[MySessionCallback]";
			try{
//				AudioManager am = (AudioManager) rContext.getSystemService(Context.AUDIO_SERVICE);
//				// Update metadata and state
//				// pause the player (custom call)
////						dbMsg +=",isPlaying=" + mPlayer.isPlaying();
////						mPlayer.pause();
////						dbMsg +=">>" + mPlayer.isPlaying();
//				// unregister BECOME_NOISY BroadcastReceiver
//				//			unregisterReceiver(myNoisyAudioStreamReceiver);
//				// Take the service out of the foreground, retain the notification
				exoPlayer.setPlayWhenReady(false);
				mAudioManager.abandonAudioFocus(afChangeListener);
				myLog(TAG,dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"で"+e);
			}
		}

		@Override
		public void onStop() {
			final String TAG = "onStop";
			String dbMsg="[MySessionCallback]";
			try{
				onPause();
				mSession.setActive(false);
				//オーディオフォーカスを開放
				mAudioManager.abandonAudioFocus(afChangeListener);
//				///MediaBrowserServiceCompat////////////////////
//				AudioManager am = (AudioManager) rContext.getSystemService(Context.AUDIO_SERVICE);
//				// Abandon audio focus
//				am.abandonAudioFocusRequest(audioFocusRequest);
////				unregisterReceiver(myNoisyAudioStreamReceiver);
//				// Stop the service
//				service.stopSelf();
//				// Set the session inactive  (and update metadata and state)
//				mSession.setActive(false);
//				// stop the player (custom call)
//		//		dbMsg +=",isPlaying=" + exoPlayer.isPlaying();
//			//	mPlayer.stop();もしくは
//				exoPlayer.setPlayWhenReady(false);
//			//	dbMsg +=">>" + mPlayer.isPlaying();
//				// Take the service out of the foreground
//				service.stopForeground(false);
				myLog(TAG,dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"で"+e);
			}
		}

		//シークをリクエストされたとき
		@Override
		public void onSeekTo(long pos) {
			final String TAG = "onSeekTo";
			String dbMsg="[MySessionCallback]";
			try{
				dbMsg +=",pos=" + pos;
				exoPlayer.seekTo(pos);
				myLog(TAG,dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"で"+e);
			}
		}
		//次の曲をリクエストされたとき
		@Override
		public void onSkipToNext() {
			final String TAG = "onSkipToNext";
			String dbMsg="[MySessionCallback]";
			try{
				dbMsg +=",index=" + index;
				index++;
				if (index >= mLibrary.getMediaItems().size())//ライブラリの最後まで再生したら
					index = 0;//最初に戻す
				dbMsg +=">>" + index;
				onPlayFromMediaId(queueItems.get(index).getDescription().getMediaId(), null);
				myLog(TAG,dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"で"+e);
			}
		}

		//前の曲をリクエストされたとき
		@Override
		public void onSkipToPrevious() {
			final String TAG = "onSkipToPrevious";
			String dbMsg="[MySessionCallback]";
			try{
				dbMsg +=",index=" + index;
				index--;
				if (index < 0)//インデックスが0以下になったら
					index = queueItems.size() - 1;//最後の曲に移動する
				dbMsg +=">>" + index;

				onPlayFromMediaId(queueItems.get(index).getDescription().getMediaId(), null);
				myLog(TAG,dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"で"+e);
			}
		}

		//WearやAutoでキュー内のアイテムを選択された際にも呼び出される
		@Override
		public void onSkipToQueueItem(long i) {
			final String TAG = "onSkipToQueueItem";
			String dbMsg="[MySessionCallback]";
			try{
				dbMsg +=",i=" + i;
				onPlayFromMediaId(queueItems.get((int)i).getDescription().getMediaId(), null);
				myLog(TAG,dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"で"+e);
			}
		}

		//Media Button Intentが飛んできた時に呼び出される
		//オーバーライド不要（今回はログを吐くだけ）
		//MediaSessionのplaybackStateのActionフラグに応じてできる操作が変わる
		@Override
		public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
			final String TAG = "onMediaButtonEvent";
			String dbMsg="[MySessionCallback]";
			try{
				KeyEvent key = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
				dbMsg +=",key=" + String.valueOf(key.getKeyCode());
				myLog(TAG,dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"で"+e);
			}
			return super.onMediaButtonEvent(mediaButtonEvent);
		}
	};
	/////////////////////MediaSession用コールバック//

	///プレイヤーの状態をクライアントに通知する////////////////////
	//プレイヤーのコールバック
	private Player.EventListener eventListener = new Player.DefaultEventListener() {
		//プレイヤーのステータスが変化した時に呼ばれる
		@Override
		public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
			final String TAG = "onPlayerStateChanged";
			String dbMsg="[Player.DefaultEventListener]";
			try{
				dbMsg +=",playWhenReady=" + playWhenReady;
				dbMsg +=",playbackState=" + playbackState;
				UpdatePlaybackState();
				myLog(TAG,dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"で"+e);
			}
		}
	};

	/**
	 * MediaSessionが配信する、現在のプレイヤーの状態を設定する。
	 * ここには再生位置の情報も含まれるので定期的に更新する
	 */
	private void UpdatePlaybackState() {
		final String TAG = "UpdatePlaybackState";
		String dbMsg="[Player.DefaultEventListener]";
		try{
			int state = PlaybackStateCompat.STATE_NONE;
			dbMsg +=",state=" + state;
			//プレイヤーの状態からふさわしいMediaSessionのステータスを設定する
			switch (exoPlayer.getPlaybackState()) {
				case Player.STATE_IDLE:					//1
					dbMsg +="=STATE_IDLE";
					state = PlaybackStateCompat.STATE_NONE;
					break;
				case Player.STATE_BUFFERING:			//2
					dbMsg +="=STATE_BUFFERING";
					state = PlaybackStateCompat.STATE_BUFFERING;
					break;
				case Player.STATE_READY:				//3
					dbMsg +="=STATE_READY";
					if (exoPlayer.getPlayWhenReady()) {
						dbMsg +="=STATE_PLAYING";
						state = PlaybackStateCompat.STATE_PLAYING;
					}else {
						dbMsg +="=STATE_PAUSED";
						state = PlaybackStateCompat.STATE_PAUSED;
					}
					break;
				case Player.STATE_ENDED:
					dbMsg +="=STATE_ENDED";
					state = PlaybackStateCompat.STATE_STOPPED;
					break;
			}
			//プレイヤーの情報、現在の再生位置などを設定する
			//また、MeidaButtonIntentでできる操作を設定する
			mSession.setPlaybackState(new PlaybackStateCompat.Builder()
					.setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_STOP)
					.setState(state, exoPlayer.getCurrentPosition(), exoPlayer.getPlaybackParameters().speed)
					.build());
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}

	}
	/////////////////////プレイヤーの状態をクライアントに通知する//
	///通知の作成////////////////////
	//通知を作成、サービスをForegroundにする
	private void CreateNotification() {
		final String TAG = "CreateNotification";
		String dbMsg="";
		try{
			dbMsg +=" ,mSession=" + mSession.toString();
			MediaControllerCompat controller = mSession.getController();
			MediaMetadataCompat mediaMetadata = controller.getMetadata();
			dbMsg +=" ,mediaMetadataのsize=" + mediaMetadata.size();

			if (mediaMetadata == null && !mSession.isActive()) return;

			MediaDescriptionCompat description = mediaMetadata.getDescription();
			dbMsg +=" ,description=" + description.toString();
			NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
			String mediaID = description.getMediaId();
			dbMsg +=" ,[" + mediaID + "]";
			CharSequence nowTitle = description.getTitle();
			dbMsg +=" ,nowTitle=" + nowTitle;
			CharSequence nowSubtitle = description.getSubtitle();
			dbMsg +=" ,nowSubtitle=" + nowSubtitle;
			CharSequence nowDescription = description.getDescription();
			dbMsg +=" ,nowDescription=" + nowDescription;
			Bitmap iconBitmap = description.getIconBitmap();
			if(iconBitmap == null){
				dbMsg +=" ,iconBitmap=null";
				iconBitmap=mLibrary.getAlbumBitmap(this.rContext,mediaID);
				dbMsg +=">>";
			}
			dbMsg +=" ,iconBitmap(" + iconBitmap.getWidth() + " × " + iconBitmap.getHeight() + ")";

			builder
					//現在の曲の情報を設定
					.setContentTitle(nowTitle)
					.setContentText(nowSubtitle)
					.setSubText(nowDescription)
					.setLargeIcon(iconBitmap)

					// 通知をクリックしたときのインテントを設定
					.setContentIntent(pendingIntent)

					// 通知がスワイプして消された際のインテントを設定
					.setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,
							PlaybackStateCompat.ACTION_STOP))

					// 通知の範囲をpublicにしてロック画面に表示されるようにする
					.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
					//exo_controls_play
					.setSmallIcon(R.drawable.play_notif)
					//通知の領域に使う色を設定
					//Androidのバージョンによってスタイルが変わり、色が適用されない場合も多い
					.setColor(ContextCompat.getColor(this, R.color.colorAccent))

					// Media Styleを利用する
					.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mSession.getSessionToken())
							//.setMediaSession(MediaSessionCompat.Token.fromToken(mSession.getSessionToken()))
					.setShowActionsInCompactView(1)			//0,1,3,4
			);

//				.setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
//						.setMediaSession(mSession.getSessionToken())
//						//通知を小さくたたんだ時に表示されるコントロールのインデックスを設定
//						.setShowActionsInCompactView(1));

			// Android4.4以前は通知をスワイプで消せないので
			//キャンセルボタンを表示することで対処
			//今回はminSDKが21なので必要ない
			//.setShowCancelButton(true)
			//.setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,
			//        PlaybackStateCompat.ACTION_STOP)));

			//通知のコントロールの設定
			//exo_controls_previous ?
			builder.addAction(new NotificationCompat.Action(
					R.drawable.rewbtn, "prev",
					MediaButtonReceiver.buildMediaButtonPendingIntent(this,
							PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)));

			//プレイヤーの状態で再生、一時停止のボタンを設定
			PlaybackStateCompat stateObj = controller.getPlaybackState();
			final int state = stateObj == null ? PlaybackStateCompat.STATE_NONE : stateObj.getState();
			dbMsg +=" ,state=" + state;
			if (state == PlaybackStateCompat.STATE_PLAYING) {
				builder.addAction(new NotificationCompat.Action(
						R.drawable.pouse_notif, "pause",
						MediaButtonReceiver.buildMediaButtonPendingIntent(this,
								PlaybackStateCompat.ACTION_PAUSE)));
			} else {
				builder.addAction(new NotificationCompat.Action(
						R.drawable.play_notif, "play",
						MediaButtonReceiver.buildMediaButtonPendingIntent(this,
								PlaybackStateCompat.ACTION_PLAY)));
			}
			builder.addAction(new NotificationCompat.Action(
					R.drawable.ffbtn, "next",
					MediaButtonReceiver.buildMediaButtonPendingIntent(this,
							PlaybackStateCompat.ACTION_SKIP_TO_NEXT)));
			dbMsg +=" ,builder=" + builder.toString();
			startForeground(NOTIFICATION_ID, builder.build());
//android.app.RemoteServiceException$CannotPostForegroundServiceNotificationException: Bad notification for startForeground
			//再生中以外ではスワイプで通知を消せるようにする
			if (state != PlaybackStateCompat.STATE_PLAYING) {
				stopForeground(false);
			}
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}
	/////////////////////通知の作成//

	///Audio Focusを扱う////////////////////
	/////////////////////Audio Focusを扱う//
	//オーディオフォーカスのコールバック
	AudioManager.OnAudioFocusChangeListener afChangeListener =
			new AudioManager.OnAudioFocusChangeListener() {
				public void onAudioFocusChange(int focusChange) {
					//フォーカスを完全に失ったら
					if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
						//止める
						mSession.getController().getTransportControls().pause();
					} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {//一時的なフォーカスロスト
						//止める
						mSession.getController().getTransportControls().pause();
					} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {//通知音とかによるフォーカスロスト（ボリュームを下げて再生し続けるべき）
						//本来なら音量を一時的に下げるべきだが何もしない
					} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {//フォーカスを再度得た場合
						//再生
						mSession.getController().getTransportControls().play();
					}
				}
			};
	/////////////////////MediaBrowserServiceCompat//
	/**
	 *  操作対応②ⅱ各画面の操作ボタンから呼ばれ動作分岐。 ☆これが無ければonStart
	 *Called when we receive an Intent. When we receive an intent sent to us via startService(), this is the method that gets called. So here we react
	 * appropriately depending on the Intent's action, which specifies what is being requested of us.
	 */
	public int nowSartId = 0;
	public boolean actClose= false;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		final String TAG = "onStartCommand";
		String dbMsg="";
		try{
			Class<?> callClass = intent.getClass();
			dbMsg +="、callClass=" + callClass.getName();
			rContext = this.getApplicationContext();			//com.hijiyam_koubou.marasongs.MyApp
			readPref();
			if(sharedPref ==null){
				MyConstants.PREFS_NAME = this.getResources().getString(R.string.pref_main_file);
				dbMsg +="、PREFS_NAME=" + MyConstants.PREFS_NAME;
				if (31 <= android.os.Build.VERSION.SDK_INT ) {
					sharedPref = getSharedPreferences(MyConstants.PREFS_NAME, MODE_PRIVATE);
					myEditor = sharedPref.edit();
				}else{
					sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());			//	this.getSharedPreferences(this, MODE_PRIVATE);		//
					myEditor = sharedPref.edit();
				}
			}

			onCompletNow = false;			//曲間処理中
			action = intent.getAction();					//ボタンなどで指定されたアクション
			nowAction =action;	//現在のアクション
			dbMsg +=",action=" + action;
			if( action == null ){
				action = ACTION_LISTSEL;
				dbMsg +=">>" + action;
			}
			int mcPosition = getPrefInt("mcPosition" , 0, MusicPlayerService.this);  //sharedPref.getInt("pref_position" , 0);
			dbMsg +=" , mcPosition=" + mcPosition;
			if( exoPlayer !=null ){
				dbMsg +=" ,isPlaying=" + exoPlayer.isPlaying() ;/////////////////////////////////////
				exoPlayer.seekTo(mcPosition);
			}		//	Bundle extras = intent.getExtras();
			if(mSession ==null){
				dbMsg += " , mSession == null";
				mSession = new MediaSessionCompat(getApplicationContext(),  getResources().getString(R.string.app_name));
			}
			if (action.equals(ACTION_START_SERVICE)) {
				dbMsg += ".読み込み前[ " + myPreferences.nowList_id+ "] " + myPreferences.nowList + " の "+ myPreferences.pref_data_url;		// + "の" + saiseiJikan + "から";
//				playerClass = (Class<? extends MaraSonActivity>) intent.getSerializableExtra("callClass");
//				dbMsg += ",playerClass=" + playerClass.getName();
				notifyIntent = new Intent(MusicPlayerService.this, MaraSonActivity.class);		//getApplication()
				String readID = String.valueOf(intent.getIntExtra("nowList_id", 0));				//extras.getInt("nowList_id");
				dbMsg += ",渡されたのは[ " + readID+ "] ";
				if(myPreferences.nowList_id != readID){
					myPreferences.nowList_id = readID;
					dbMsg += ">>[ " + myPreferences.nowList_id+ "] "  ;		// + "の" + saiseiJikan + "から";
				}
				String readList = intent.getStringExtra("nowList");				//extras.getString("nowList");
				dbMsg += readList;
				if(!myPreferences.nowList.equals(readList)){
					myPreferences.nowList = readList;
					dbMsg += ">>" + myPreferences.nowList ;		// + "の" + saiseiJikan + "から";
				}
				dbMsg += ",読み込み前= "+ myPreferences.pref_data_url;		// + "の" + saiseiJikan + "から";
				String readUrl= intent.getStringExtra("pref_data_url");		//extras.getString("pref_data_url");
				dbMsg += ",渡されたのは= " + readUrl;
				if(myPreferences.pref_data_url != null || !readUrl.equals(myPreferences.pref_data_url)){
					myPreferences.pref_data_url = readUrl;
					dbMsg += ">>"+ myPreferences.pref_data_url;
				}
				if(plSL == null || plSL.size()<1){
					plSL =  new ArrayList<String>();				//プレイリスト用簡易リスト
					plSL.clear();
					plSL = readCalentList(Integer.parseInt(myPreferences.nowList_id),myPreferences.nowList);
					listEnd = plSL.size();
					mPlayer2 = null;
				}
				int rInt = getCarentIndex(myPreferences.pref_data_url,plSL);
				if(-1 < rInt){
					mIndex = rInt;
				}
//				dbMsg += "で[mIndex：" + mIndex + "/"+ listEnd + "件]";
//				boolean retBool = setPrefInt("pref_mIndex", mIndex, this);        //プリファレンスの読込み
//				dbMsg += "を書込み" + retBool;
				if(musicPlaylist == null){
					musicPlaylist = new MusicPlaylist(MusicPlayerService.this);
				}
		//		lpNotificationMake(myPreferences.pref_data_url);
				IsPlaying=intent.getBooleanExtra("IsPlaying",false);
				dbMsg += ",IsPlaying=" + IsPlaying;
////一旦停止				playNextSong(mIndex,IsPlaying);
////				if(IsPlaying){
////					configAndStartMediaPlayer();
////				}else{
////					mState = State.Paused;
////				}
//				//画面遷移のやり方でプレイヤー画面を開く
////一旦停止					startActivity(notifyIntent);
				dbMsg += "＞＞＞＞＞リストから選曲";
			} else if (action.equals(ACTION_PLAYPAUSE)) {
				dbMsg +="でPLAY/PAUSE";
				processTogglePlaybackRequest();
			} else if (action.equals(ACTION_PLAY_READ)) {							//"readPlaying[MaraSonActivity]
			//	dataUketori(intent);			//クライアントからデータを受け取りグローバル変数にセット
				processPlayRequest();
				if( exoPlayer !=null ){
					dbMsg +=" ,isPlaying=" + exoPlayer.isPlaying() ;/////////////////////////////////////
					exoPlayer.seekTo(mcPosition);
					if(! exoPlayer.isPlaying()){
						exoPlayer.setPlayWhenReady(true);
						mState = State.Playing;
					}
				}
//				sendPlayerState(exoPlayer);																		//②ⅲStop?//一曲分のデータ抽出して他のActvteyに渡す。
				changeCount(exoPlayer);						//シークバーの初期更新：タイマーオブジェクトを使ったカウンタ更新を追加
			} else if (action.equals(ACTION_PLAY)) {
				mState = State.Playing;
				dbMsg +="から再生,";
				processPlayRequest();																	//②ⅲPlay?StoppedならplayNextSong/PausedならconfigAndStartMediaPlayer
			} else if (action.equals(ACTION_LISTSEL)) {					//	リストで選曲後に再生中だった場合
////				dataUketori(intent);
//				processPlayRequest();
//				playNextSong(mIndex,true);
			} else if (action.equals(ACTION_PAUSE)) {
				dbMsg +="でポーズ";
				processPauseRequest();																	//②ⅲPause?
			} else if (action.equals(ACTION_SKIP)) {
				dbMsg +="で送り";
				if( exoPlayer !=null ){
					exoPlayer.setPlayWhenReady(false);
				}
				processSkipRequest();																	//②ⅲFF?次の曲に順送り
			} else if (action.equals(ACTION_REWIND)) {
				dbMsg +="から戻し,";
				if( exoPlayer !=null ){
					exoPlayer.setPlayWhenReady(false);
				}
				processRewindRequest();																//②ⅲRew?
			} else if (action.equals(ACTION_STOP)) {
				dbMsg +="で停止,";
				processStopRequest(false);															//②ⅲStop?eタイマーを破棄してmPlayerの破棄へ
				//			if (intent.getBooleanExtra("cancel", false)) {
				//				mNotificationManager.cancel(NOTIFICATION_ID);
				//			}
			} else if (action.equals(ACTION_REQUEST_STATE)) {
				dbMsg +="でリストから戻り,";
				//起動時の表示；dataUketoriでクライアントからデータを受け取りグローバル変数にセット、sendPlayerStateで一曲分のデータ抽出して他のActvteyに渡す。
		//		dataUketori(intent);			//クライアントからデータを受け取りグローバル変数にセット
				processPlayRequest();
//				mState = State.Stopped;						//Stopped	プレイヤー生成のトリガーに使用？					Paused
				if(! IsPlaying){
					dbMsg +="起動直後？";
//					new PrepareMusicRetrieverTask(this).execute(getApplicationContext());		// Create the retriever and start an asynchronous task that will prepare it.

				}
				sendPlayerState(exoPlayer);																		//②ⅲStop?//一曲分のデータ抽出して他のActvteyに渡す。
			} else if (action.equals(ACTION_KEIZOKU)) {
				dbMsg +="で終了準備,";
	//			dataUketori(intent);			//クライアントからデータを受け取りグローバル変数にセット
				processPlayRequest();
				sendPlayerState(exoPlayer);
			} else if (action.equals(ACTION_SYUURYOU)) {				//終了準備
				dbMsg +="で終了処理,";
				quitMe( startId );			//このサービスを閉じる
			} else if (action.equals(ACTION_SYUURYOU_NOTIF)) {				//
				dbMsg +="でノティフィケーションから終了,";
				dbMsg +=" ,actClose=" + actClose ;/////////////////////////////////////
				if(! actClose ){
					imanoJyoutai = MaraSonActivity.quit_all;
					sendPlayerState(exoPlayer);
				}
				dbMsg +=" ,startId=" + startId ;/////////////////////////////////////
				quitMe( startId );			//このサービスを閉じる
			} else if (action.equals(ACTION_ACT_CLOSE)) {				//アクティビティは閉じられている
				dbMsg +=" ,actClose=" + actClose ;/////////////////////////////////////
				actClose = true;
				dbMsg +=">>" + actClose ;/////////////////////////////////////
			} else if (action.equals(ACTION_REQUEST)) {				//次はリクエスト開始
//				Bundle extras = intent.getExtras();
//				tugiList_id = extras.getInt("tugiList_id");
//				dbMsg += "次に再生するリスト["+ tugiList_id ;
//				tugiList = extras.getString("tugiList");
//				dbMsg += "]"+ tugiList ;		//次に再生するリスト名;リクエストリスト
				//			boolean requestSugu = false;
				//			requestSugu = extras.getBoolean("requestSugu");
				//			if(requestSugu){
				//				myPreferences.nowList_id = tugiList_id;
				//				myPreferences.nowList = tugiList;
				//			}
			} else if (action.equals(ACTION_EQUALIZER)) {
				equalizerPartKousin( intent );					//Equalizerの部分更新
			} else if (action.equals(ACTION_BASS_BOOST)) {
				setupBassBoost(intent);			//ベースブーストOn/Off
			} else if (action.equals(ACTION_REVERB)) {
				dbMsg += ",リバーブ効果["+ reverbBangou + "]" + reverbMei;		//次に再生するリスト名;リクエストリスト
				setupPresetReverb(intent);					//リバーブ設定
			} else if (action.equals(ACTION_DATA_OKURI)) {				//データ送りのみ
				sendPlayerState(exoPlayer);
			} else if (action.equals(ACTION_UKETORI)) {				//データ受け取りのみ
//				dataUketori(intent);
				processPlayRequest();
			}
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
		//	return START_REDELIVER_INTENT;				//	再起動前と同じ順番で再起動してくれる。Intentも渡ってきています。
		return START_NOT_STICKY;				//APIL5;サービスが強制終了した場合、サービスは再起動しない
		/*START_STICKY	サービスが強制終了した場合、サービスは再起動するonStartCommand()が再度呼び出され、Intentにnullが渡される
		 */
	}

	/**
	 * 再生状態とメタデータを設定する。今回はメタデータはハードコートする
	 *
	 * MediaSessionのsetCallBackで扱う操作([MediaSessionCompat.Callback.onPlay]など)も[PlaybackStateCompat.Builder.setState]に書かないと何も起きない
	 * */
	private void updateState() {
		final String TAG = "updateState";
		String dbMsg="";
		MediaSource retSource = null;
		try{
			// 取り扱う操作。とりあえず 再生準備 再生 一時停止 シーク を扱うようにする。書き忘れると何も起きない
			stateBuilder = new PlaybackStateCompat.Builder();
			//	stateBuilder.apply{
			stateBuilder.setActions(PlaybackStateCompat.ACTION_PREPARE);
			stateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY);
			stateBuilder.setActions(PlaybackStateCompat.ACTION_PAUSE);
			stateBuilder.setActions(PlaybackStateCompat.ACTION_STOP);
			stateBuilder.setActions(PlaybackStateCompat.ACTION_SEEK_TO);

			dbMsg += ",再生してるか;exoPlayer=" + exoPlayer.isPlaying();
			int state = PlaybackStateCompat.STATE_PAUSED;
			if(exoPlayer.isPlaying()){
				state = PlaybackStateCompat.STATE_PLAYING;
			}
			long position = exoPlayer.getCurrentPosition();
			long duration = exoPlayer.getDuration();
			dbMsg += ",position=" + position + "/" + duration +"ms";
			// 再生状態を更新
			stateBuilder.setState(state, position, 1.0f); // 最後は再生速度
			stateBuilder.build();

			mSession.setPlaybackState(stateBuilder.build());

			// メタデータの設定
			MediaMetadataCompat.Builder mediaMetadataCompat = new MediaMetadataCompat.Builder();
			dbMsg += "で[mIndex：" + myPreferences.pref_mIndex + " / " + queueItems.size() + "曲目";			//mediaItems.size()
			queueItem = queueItems.get(mCurrentQueueIndex);
			dbMsg += ",METADATA_KEY_TITLE=" + queueItem.getDescription().getTitle();
			dbMsg += ",METADATA_KEY_ARTIST=" + queueItem.getDescription().getSubtitle();
			dbMsg += ",art=" + queueItem.getDescription().getIconBitmap().getRowBytes()+"バイト";
			mediaMetadataCompat.putString(MediaMetadataCompat.METADATA_KEY_TITLE, (String) queueItem.getDescription().getTitle());
			mediaMetadataCompat.putString(MediaMetadataCompat.METADATA_KEY_ARTIST,(String) queueItem.getDescription().getSubtitle());
			mediaMetadataCompat.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration * 1000); // これあるとAndroid 10でシーク使えます
			mediaMetadataCompat.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, queueItem.getDescription().getIconBitmap()); // これあるとAndroid 10でシーク使えます
			mediaMetadataCompat.build();
			//media sessionにデータセット
			mSession.setMetadata(mediaMetadataCompat.build());
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	/** 通知を表示する */
//	private void showNotification() {
//		final String TAG = "showNotification";
//		String dbMsg="";
//		MediaSource retSource = null;
//		try{
//			dbMsg += ",exoPlayer=" + exoPlayer.isPlaying();
//			dbMsg += "[" + myPreferences.nowList_id + "]";
//			dbMsg += ">>"+ myPreferences.nowList;
//			dbMsg += "[" + myPreferences.pref_mIndex + "]";
//			dbMsg += ">>"+ myPreferences.pref_data_url;
////				String dataFN = getPrefStr( "pref_data_url" ,"" , MusicPlayerService.this);
////				dbMsg +=  "," + dataFN ;////////////////////////////////////////////////////////////////////////////
//			dbMsg += "(" + myPreferences.pref_saisei_jikan ;
//			dbMsg += " / " + myPreferences.pref_saisei_nagasa + ")";
//			String title = getApplicationContext().getString(R.string.app_name);
//			if(notifyIntent == null){
//				notifyIntent = new Intent(MusicPlayerService.this, MaraSonActivity.class);		//getApplication()
//			}
//			notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//			dbMsg += " ,notifyIntent="+notifyIntent.getClass().getName();		//android.content.Intent
//			if(pendingIntent == null){
//				pendingIntent = PendingIntent.getActivity(
//						getApplicationContext(), 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
//								| PendingIntent.FLAG_IMMUTABLE
//				);
//			}
//			dbMsg += " ,pendingIntent="+pendingIntent.getClass().getName();				//android.app.PendingIntent
//			notificationManager = NotificationManagerCompat.from(this);
//			dbMsg += " ,Notification　Channel 設定";
//			mNotificationChannel = new NotificationChannel(String.valueOf(NOTIFICATION_ID), title, NotificationManager.IMPORTANCE_DEFAULT);
//			dbMsg += " ,通知音を消す";
//			mNotificationChannel.setSound(null,null);			//setSound(Uri.parse("android.resource://" + packageName + "/" + R.raw.sample), null)
//			notificationManager.createNotificationChannel(mNotificationChannel);
//			if (notificationManager != null) {
//				notificationManager.createNotificationChannel(mNotificationChannel);
//			}
//
//			if(metadataBuilder == null){
//				dbMsg += " , metadataBuilder == null";
//				getDataInfo(myPreferences.pref_data_url);
//			}
//			if(mSession ==null){
//				dbMsg += " , mSession == null";
//				mSession = new MediaSessionCompat(getApplicationContext(),  getResources().getString(R.string.app_name));
//			}
//
//			dbMsg += " , DURATION=" + metadataBuilder.build().getLong(MediaMetadata.METADATA_KEY_DURATION);
//			// Get the session's metadata
//			//	MediaController controller = MediaSessionCompat.getController();
//			NotificationCompat.Builder builder = new NotificationCompat.Builder(this, String.valueOf(NOTIFICATION_ID));
//			// タイトルなどの表示
//			mSession.setMetadata(MediaMetadataCompat.fromMediaMetadata(metadataBuilder.build()));
//			///	mSession.setCallback(nfCallback);
//			dbMsg += " Notification そのものをタップしたとき="+pendingIntent.getCreatorUid();
//			builder.setContentIntent(pendingIntent);
//			builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
//
//			//			NotifStyle = new androidx.media.app.NotificationCompat.MediaStyle();
//			intentNR = new Intent(getApplicationContext(), NotifRecever.class);			//MusicPlayerService.this
//
//			intentNR.setAction(ACTION_REWIND);
//			intentNR.putExtra("RequestCode",MyConstants.ACTION_CODE_REWINDE);
//			PendingIntent rewIntent = PendingIntent.getBroadcast(getApplicationContext(), MyConstants.ACTION_CODE_REWINDE, intentNR,PendingIntent.FLAG_UPDATE_CURRENT |  PendingIntent.FLAG_IMMUTABLE);
//			NotificationCompat.Action rewAction = new NotificationCompat.Action(android.R.drawable.ic_media_rew,ACTION_REWIND,rewIntent);
//
//			intentNR.setAction(ACTION_PLAYPAUSE);
//			intentNR.putExtra("RequestCode",MyConstants.ACTION_CODE_PLAYPAUSE);
//			ppIntent = PendingIntent.getBroadcast(getApplicationContext(), MyConstants.ACTION_CODE_PLAYPAUSE, intentNR,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//			playing=false;
//			if(exoPlayer != null){
//				if(exoPlayer.isPlaying()) {
//					playing=true;
//				}
//			}
//			int ppIcon = playing ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play;
//			ppAction = new NotificationCompat.Action(ppIcon,ACTION_PLAYPAUSE,ppIntent);
//			//getApplicationContext().getResources().getInteger(android.R.drawable.ic_media_pause);
////				String ppTitol = "pause";
//
//			pp2Pouse = new NotificationCompat.Action(android.R.drawable.ic_media_pause, ACTION_PLAYPAUSE, ppIntent);
//			pp2Play = new NotificationCompat.Action(android.R.drawable.ic_media_play, ACTION_PLAYPAUSE, ppIntent);
//
//			intentNR.setAction(ACTION_SKIP);
//			intentNR.putExtra("RequestCode",MyConstants.ACTION_CODE_SKIP);
//			PendingIntent ffIntent = PendingIntent.getBroadcast(getApplicationContext(), MyConstants.ACTION_CODE_SKIP, intentNR,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//			NotificationCompat.Action ffAction = new NotificationCompat.Action(android.R.drawable.ic_media_ff,ACTION_SKIP,ffIntent);
//
//			intentNR.setAction(ACTION_SYUURYOU_NOTIF);
//			intentNR.putExtra("RequestCode",MyConstants.ACTION_CODE_quit);
//			PendingIntent quitIntent = PendingIntent.getBroadcast(getApplicationContext(), MyConstants.ACTION_CODE_quit, intentNR,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//			NotificationCompat.Action quitAction = new NotificationCompat.Action(android.R.drawable.ic_lock_power_off,ACTION_SYUURYOU_NOTIF,quitIntent);
//
//			// 左から順に並びます
//			builder.addAction(rewAction);
//			builder.addAction(ppAction);
////				builder.addAction(ppIconChaange());
////				builder.addAction(pp2Pouse);
////				builder.addAction(pp2Play);
//			builder.addAction(ffAction);
//			builder.addAction(quitAction);
////				if (mPlayer != null) {
////					dbMsg += ",isPlaying = " + mPlayer.isPlaying();
////					if (! mPlayer.isPlaying()) {        //(mState == State.Paused || mState == State.Stopped
////						NotifStyle.setShowActionsInCompactView(2);
////					}else{
////						NotifStyle.setShowActionsInCompactView(1);
////					}
////				}else{
////					NotifStyle.setShowActionsInCompactView(1);
////				}
////				builder.setStyle(NotifStyle);
//
//			builder.setSmallIcon(R.drawable.ic_launcher_notif);
//			dbMsg += ",setStyle";
//			builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
//					//		.setMediaSession(MediaSessionCompat.Token.fromToken(mSession.getSessionToken()))
//					.setShowActionsInCompactView(1)			//0,1,3,4
//			);
//
//			dbMsg += " Notification型のインスタンス生成";
//			lpNotification = builder.build();
//			notificationManager.notify(NOTIFICATION_ID, lpNotification);
//	/*
//			NotificationCompat notification;
//			// 通知を作成。通知チャンネルのせいで長い
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//				notification = new NotificationCompat();
//						// 通知チャンネル
//				channelId = String.valueOf(NOTIFICATION_ID);
//				NotificationChannel notificationChannel = new NotificationChannel(channelId, getApplicationContext().getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW);			//IMPORTANCE_DEFAULT
//				if (notificationManager.getNotificationChannel(channelId) == null) {
//					// 登録
//					notificationManager.createNotificationChannel(notificationChannel);
//				}
//				new NotificationCompat.Builder(this, channelId);
//			} else {
//				new NotificationCompat.Builder(this);
//			}
//				*/
////			lpNotification.apply {
////				lpNotification.setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mSessionCompat.sessionToken).setShowActionsInCompactView(0));
////				lpNotification.setSmallIcon(R.drawable.ic_background_icon);
////				// 通知領域に置くボタン
////				if (exoPlayer.isPlaying()) {
////					addAction(NotificationCompat.Action(R.drawable.ic_pause_black_24dp, "一時停止", MediaButtonReceiver.buildMediaButtonPendingIntent(this@BackgroundPlaylistCachePlayService, PlaybackStateCompat.ACTION_PAUSE)));
////				} else {
////					addAction(NotificationCompat.Action(R.drawable.ic_play_arrow_24px, "再生", MediaButtonReceiver.buildMediaButtonPendingIntent(this@BackgroundPlaylistCachePlayService, PlaybackStateCompat.ACTION_PLAY)));
////				}
////				addAction(NotificationCompat.Action(R.drawable.ic_clear_black, "停止", MediaButtonReceiver.buildMediaButtonPendingIntent(this@BackgroundPlaylistCachePlayService, PlaybackStateCompat.ACTION_STOP)));
////			}
//			// 通知表示
//			//startForeground(84, notification.build());
//			startForeground(NOTIFICATION_ID, lpNotification);
//			myLog(TAG,dbMsg);
//		} catch (Exception e) {
//			myErrorLog(TAG,dbMsg+"で"+e);
//		}
//	}

	//接続時に呼び出されるコールバック
	private MediaBrowserCompat.ConnectionCallback connectionCallback = new MediaBrowserCompat.ConnectionCallback() {
		@Override
		public void onConnected() {
			final String TAG = "onConnected";
			String dbMsg="";
//			try {
				//接続が完了するとSessionTokenが取得できるので
				//それを利用してMediaControllerを作成
				mController = new MediaControllerCompat(MusicPlayerService.this, mBrowser.getSessionToken());
				//サービスから送られてくるプレイヤーの状態や曲の情報が変更された際のコールバックを設定
				mController.registerCallback(controllerCallback);

				//既に再生中だった場合コールバックを自ら呼び出してUIを更新
				if (mController.getPlaybackState() != null && mController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
					controllerCallback.onMetadataChanged(mController.getMetadata());
					controllerCallback.onPlaybackStateChanged(mController.getPlaybackState());
				}
				myLog(TAG,dbMsg);
//			} catch (RemoteException ex) {
//				ex.printStackTrace();
//			//	myErrorLog(TAG,dbMsg+"で"+ ex);
//				Toast.makeText(MusicPlayerService.this, ex.getMessage(), Toast.LENGTH_LONG).show();
//			}
			//サービスから再生可能な曲のリストを取得
			mBrowser.subscribe(mBrowser.getRoot(), subscriptionCallback);
		}
	};

	//MediaControllerのコールバック
	private MediaControllerCompat.Callback controllerCallback = new MediaControllerCompat.Callback() {
		//再生中の曲の情報が変更された際に呼び出される
		@Override
		public void onMetadataChanged(MediaMetadataCompat metadata) {
			final String TAG = "onMetadataChanged";
			String dbMsg="";
			try {
				dbMsg += ",title =　" + metadata.getDescription().getTitle();
			//	textView_title.setText(metadata.getDescription().getTitle());
//				imageView.setImageBitmap(metadata.getDescription().getIconBitmap());
				dbMsg += ",duration =　" + metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
		//		textView_duration.setText(Long2TimeString(metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)));
		//		seekBar.setMax((int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
				myLog(TAG,dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"で"+e);
			}
		}

		//プレイヤーの状態が変更された時に呼び出される
		@Override
		public void onPlaybackStateChanged(PlaybackStateCompat state) {
			final String TAG = "onPlaybackStateChanged";
			String dbMsg="";
			try {

				//プレイヤーの状態によってボタンの挙動とアイコンを変更する
				if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
					dbMsg += "state =　STATE_PLAYING";
					//再生ボタン
//					button_play.setOnClickListener(new View.OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							final String TAG = "onClick";
//							String dbMsg="";
//							try {
//								mController.getTransportControls().pause();
//								myLog(TAG,dbMsg);
//							} catch (Exception e) {
//								myErrorLog(TAG,dbMsg+"で"+e);
//							}
//						}
//					});
//					button_play.setImageResource(R.drawable.exo_controls_pause);
				} else {
						dbMsg += "state ≠ STATE_PLAYING";
//					button_play.setOnClickListener(new View.OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							final String TAG = "onClick";
//							String dbMsg="";
//							try {
//								mController.getTransportControls().play();
//								myLog(TAG,dbMsg);
//							} catch (Exception e) {
//								myErrorLog(TAG,dbMsg+"で"+e);
//							}
//						}
//					});
//					button_play.setImageResource(R.drawable.exo_controls_play);
				}
				dbMsg += "、再生ポジション=" + state.getPosition();
		//		textView_position.setText(Long2TimeString(state.getPosition()));
				//シーク
		//		seekBar.setProgress((int) state.getPosition());
				myLog(TAG,dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"で"+e);
			}
		}
	};

	//Subscribeした際に呼び出されるコールバック
	private MediaBrowserCompat.SubscriptionCallback subscriptionCallback = new MediaBrowserCompat.SubscriptionCallback() {
		@Override
		public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
			final String TAG = "onChildrenLoaded";
			String dbMsg="";
			try {
				String id = children.get(0).getMediaId();
				dbMsg += "、id=" + id;
				//既に再生中でなければ初めの曲を再生をリクエスト
				if (mController.getPlaybackState() == null){
					//playFromMediaIdを呼び出すと、サービス側のMediaSessionのコールバック内のonPlayFromMediaIdが呼ばれる
					mController.getTransportControls().playFromMediaId(id, null);				}
				myLog(TAG,dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"で"+e);
			}
		}
	};

	/**
	 * 設定を読み込み各サービスを起動
	 * 呼出し元 ①nowActionで動作指定されている場合と、終了指定以外でonCreateから
	 *  		②dataReflesh
	 * */
	public void createBody()  throws NullPointerException{										//①ⅹ		リモートコントロール
		final String TAG = "createBody";
		String dbMsg="";
		try{
			dbMsg += "起動済み=" + kaisiZumi;/////////////////////////////////////
			if(! kaisiZumi){  //重複読出し防止
				ORGUT = new OrgUtil();	//自作関数集
				rContext = getApplicationContext();
				ruikeiSTTime = 0;			//累積時間
				ruikeikyoku = 0;			//累積曲数
				myApp = (MyApp) this.getApplication();

				/* Android 12 でPhoneStateListener が Deprecated になった  https://note.com/koh_49/n/n94a5c2ae3aa2
				dbMsg=dbMsg +"myPid:"+ android.os.Process.myPid() + " , myTid:" + android.os.Process.myTid();/////////////////////////////////////
				dbMsg +=  ",Telephonys設定=" + mTelephonyManager;///////////////java.lang.NullPointerException
				if(mTelephonyManager == null ){
					mTelephonyManager= (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
					dbMsg += "Telephonys=" + mTelephonyManager;/////////////////////////////////////
					if(mTelephonyManager != null){
						mTelephonyManager.listen(mPhoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);
					}
				}
				*/
				readPref();
				dbMsg += ",List[" + myPreferences.nowList_id + "]";
				dbMsg += ">>"+ myPreferences.nowList;
//				mIndex = getPrefInt("pref_mIndex" , 0 , MusicPlayerService.this);
				dbMsg += "[" + myPreferences.pref_mIndex + "]";
				dbMsg += myPreferences.pref_data_url;
//				String dataFN = getPrefStr( "pref_data_url" ,"" , MusicPlayerService.this);
//				dbMsg +=  "," + dataFN ;////////////////////////////////////////////////////////////////////////////
				dbMsg += "(" + myPreferences.pref_saisei_jikan ;
				dbMsg += " / " + myPreferences.pref_saisei_nagasa + ")";
				//AudioManagerを取得
				mAudioManager = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);
				musicVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
				dbMsg +=",musicVolは" + musicVol;/////////////////////////////////////
				selfStop = true;
				kaisiZumi = true;
				//Bluetooth///////////////////////////////////////////ロックスクリーン//
				pref_toneList =  new ArrayList<String>();				//トーンリストの初期化
				receiverSeisei();		//レシーバーを生成 <onResume , playing , mData2Service	onClick
				dbMsg += ",SDK_INT=" + android.os.Build.VERSION.SDK_INT;
//				myPreferences.pref_lockscreen = false;
//				dbMsg += ">>" + myPreferences.pref_lockscreen;
				dbMsg += ",ノティフィケーションプレイヤーを使う=" + myPreferences.pref_notifplayer;
				if ( 31 <= android.os.Build.VERSION.SDK_INT) {
					// 再生キューの位置を初期化
					mCurrentQueueIndex = myPreferences.pref_mIndex;
					dbMsg += ",.再生キューの位置[" + mCurrentQueueIndex + "]";
					////イマドキなAndroid音楽プレーヤーの作り方  https://qiita.com/siy1121/items/f01167186a6677c22435 ///////
					mLibrary = new MusicLibrary();
					int totalCount = mLibrary.makeList(this, Long.parseLong(myPreferences.nowList_id),myPreferences.nowList);
					dbMsg += ",totalCount=" + totalCount;
					OrgUtil oUtil = new OrgUtil();
					String[] infos = myPreferences.pref_data_url.split(File.separator);
					String albumID=oUtil.retAlbumID(this,infos[infos.length-3],infos[infos.length-2]);
					dbMsg += ",albumID=" + albumID;
					int mIndex = mLibrary.getIndex(myPreferences.pref_data_url);
					dbMsg += ",mIndex=" + mIndex + "/" + totalCount;
					dbMsg += ",mSession作成" ;
					if(mSession ==null){
						dbMsg += " , mSession == null";
						mSession = new MediaSessionCompat(getApplicationContext(),  getResources().getString(R.string.app_name));
						dbMsg += " >>" + mSession.toString();
					}
					//このMediaSessionが提供する機能を設定
					mSession.setFlags(
											MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | 		//ヘッドフォン等のボタンを扱う
											MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS | //キュー系のコマンドの使用をサポート
											MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS); //再生、停止、スキップ等のコントロールを提供
					//クライアントからの操作に応じるコールバックを設定
					mSession.setCallback(MySessionCallback);

					// Set the session's token so that client activities can communicate with it.
					mSessionToken = mSession.getSessionToken();
					setSessionToken(mSessionToken);
					dbMsg += ",getSessionToken=" + mSession.getSessionToken().toString();
					//Media Sessionのメタデータや、プレイヤーのステータスが更新されたタイミングで
					//通知の作成/更新をする
					mSession.getController().registerCallback(new MediaControllerCompat.Callback() {
						@Override
						public void onPlaybackStateChanged(PlaybackStateCompat state) {
							final String TAG = "onPlaybackStateChanged";
							String dbMsg="";
							try{
								dbMsg += ",state=" + state ;
						//		lpNotificationMake(myPreferences.pref_data_url);
								CreateNotification();
								myLog(TAG,dbMsg);
							} catch (Exception e) {
								myErrorLog(TAG,dbMsg+"で"+e);
							}
						}

						@Override
						public void onMetadataChanged(MediaMetadataCompat metadata) {
							final String TAG = "onMetadataChanged";
							String dbMsg="";
							try{
								dbMsg += ",metadata=" + metadata ;
							//	lpNotificationMake(myPreferences.pref_data_url);
								CreateNotification();
								myLog(TAG,dbMsg);
							} catch (Exception e) {
								myErrorLog(TAG,dbMsg+"で"+e);
							}
						}
					});

					//キューにアイテムを追加
					int i = 0;
					String mediaId = null;
					for (MediaBrowserCompat.MediaItem media : mLibrary.getMediaItems()) {			//MusicLibrary.getMediaItems()
						queueItems.add(new MediaSessionCompat.QueueItem(media.getDescription(), i));
						dbMsg += "\n[" + i +  "]" + media.toString();
						i++;
						if(mIndex== i){
							mediaId=media.getMediaId();
							dbMsg += ",mediaId=" + mediaId;
						}
					}
					dbMsg += "," + queueItems.size() + "件";
					//add;ここでデータセット
					mSession.setMetadata(mLibrary.getMetadata(getApplicationContext(), mediaId));
					// 再生リストのオブジェクトを作る
					mPlayingQueue = new ArrayList<MediaSession.QueueItem>();
					//キューにアイテムを追加
					mSession.setQueue(queueItems);//WearやAutoにキューが表示される
					//exoPlayerの初期化
					exoPlayer = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), new DefaultTrackSelector());
					//プレイヤーのイベントリスナーを設定
					exoPlayer.addListener(eventListener);
					handler = new Handler();
					//500msごとに再生情報を更新
//					handler.postDelayed(new Runnable() {
//						@Override
//						public void run() {
//							//再生中にアップデート
//							if (exoPlayer.getPlaybackState() == Player.STATE_READY && exoPlayer.getPlayWhenReady())
//								UpdatePlaybackState();
//
//							//再度実行
//							handler.postDelayed(this, 500);
//						}
//					}, 500);
					//java.lang.NullPointerException: Attempt to invoke interface method 'int com.google.android.exoplayer2.ExoPlayer.getPlaybackState()' on a null object reference

//					DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//					exoPlayer = ExoPlayerFactory.newSimpleInstance(rContext, new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter)));
//					//new MediaBrowserCompat(this, new ComponentName(getApplicationContext(), this.class), connectionCallback, null);
//		//			mBrowser = new MediaBrowserCompat(this, new ComponentName(this, MusicPlayerService.class), connectionCallback, null);
////					mController =  new MediaControllerCompat(this, mBrowser.getSessionToken());
//					//12/18	java.lang.IllegalStateException: getSessionToken() called while not connected (state=1)
////					//	exoPlayer = ExoPlayerFactory.newSimpleInstance(rContext, new DefaultTrackSelector());
////				//	exoPlayer = SimpleExoPlayer.Builder(this).build(),
//					//接続(サービスをバインド)
//		//			mBrowser.connect();
//
//					// ExoPlayerの再生状態が更新されたときも通知を更新する
//					exoPlayer.addListener(new Player.EventListener()  {
//						private boolean mReady;
//						@Override
//						public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//							final String TAG = "onPlayerStateChanged";
//							String dbMsg="";
//							try{
//								dbMsg += ",playWhenReady=" + playWhenReady ;
//								dbMsg += ",playbackState=" + playbackState ;
//								updateState();
//								showNotification();
//								myLog(TAG,dbMsg);
//							} catch (Exception e) {
//								myErrorLog(TAG,dbMsg+"で"+e);
//							}
//						}
//
//						@Override
//						public void onIsPlayingChanged(boolean isPlaying) {
//							final String TAG = "onIsPlayingChanged";
//							String dbMsg="";
//							try{
//								dbMsg += ",isPlaying=" + isPlaying ;
////							updateState()
//							showNotification();
//								myLog(TAG,dbMsg);
//							} catch (Exception e) {
//								myErrorLog(TAG,dbMsg+"で"+e);
//							}
//						}
//					});
//
//					/*
//				DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//				exoPlayer = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter)));
//				* */
//					//Uriから再生する
//					DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), Util.getUserAgent(getApplicationContext(), getResources().getString(R.string.app_name)));
//					mSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(myPreferences.pref_data_url)); // TODO これから説明します
////					mSource = createMediaSource(Uri.parse(myPreferences.pref_data_url)); // TODO これから説明します
//					exoPlayer.prepare(mSource);
					///////////イマドキなAndroid音楽プレーヤーの作り方 ///////
				}else if (android.os.Build.VERSION.SDK_INT <11 ) {
					myPreferences.pref_notifplayer = false;
				}else if ( 14 <= android.os.Build.VERSION.SDK_INT  &&  android.os.Build.VERSION.SDK_INT < 21) {													//
				//					dbMsg += ",ロックスクリーンプレイヤー=" + myPreferences.pref_lockscreen;/////////////////////////////////////
				//					myPreferences.pref_lockscreen = true;
				//					dbMsg += ">>" + myPreferences.pref_lockscreen;/////////////////////////////////////
					//					myPreferences.pref_notifplayer = Boolean.valueOf( (String) keys.get("myPreferences.pref_notifplayer"));
					if( myPreferences.pref_notifplayer ){
						//	makeNotification();				//ノティフィケーション作成
					}
				}else if (21 <= android.os.Build.VERSION.SDK_INT && android.os.Build.VERSION.SDK_INT <31) {
					//		RC = new RemoteController(getApplicationContext());
				}

			}
//			updateState();
//			showNotification();
//////一旦停止				playNextSong(mIndex,IsPlaying);
//////				if(IsPlaying){
//////					configAndStartMediaPlayer();
//////				}else{
//////					mState = State.Paused;
//////				}
//			//画面遷移のやり方でプレイヤー画面を開く
//////一旦停止			startActivity(notifyIntent);
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	private MediaSource createMediaSource(Uri sorceUri) {
		final String TAG = "createMediaSource";
		String dbMsg="";
		MediaSource retSource = null;
		try{
			dbMsg += ",exoPlayer=" + exoPlayer.isPlaying();
//			dataSourceFactory = DefaultDataSourceFactory(rContext, Util.getUserAgent(rContext, rContext.getPackageName()));
//			retSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(myPreferences.pref_data_url));
			retSource =new ExtractorMediaSource(sorceUri,null,null,null,null);
			LoopingMediaSource loopingSource = new LoopingMediaSource(retSource);   //setLooping(true);の代わり
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
		return retSource;
	}

	/**サービスが最初に作成されたときに 1 回限りのセットアップ処理を行う
	 *
	 * extends MediaBrowserServiceCompat だとActiviteyと同様のあつかいになる？
	 *
	 * */
	@Override
	public void onCreate() {											//①ⅸ
		super.onCreate();
		final String TAG = "onCreate";
		String dbMsg="";
		try{
		//	Intent intent = getIntent();は通らないのでreadPref

			dbMsg="nowAction=" + nowAction;/////////onStartCommandで更新
			exoPlayer = null;
			mPlayer2 = null;
			if(nowAction != null || nowAction != ACTION_SYUURYOU){
				createBody();
			}
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	/**サービスが使用されなくなり、破棄される*/
	@Override
	public void onDestroy() {
		final String TAG = "onDestroy";
		String dbMsg="";
		try{
			dbMsg += ",mPlayer=" + exoPlayer;/////////////////////////////////////
			relaxResources(true);		//mPlayerの破棄
			dbMsg += ",nowSartId=" + nowSartId;/////////////////////////////////////
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	super.onDestroy();
	}
	///////////////////////////////////////////////////////////////////////////////////

	public static String getPrefStr(String keyNmae , String defaultVal,Context context) {        //プリファレンスの読込み
		String retStr = "";
		final String TAG = "getPrefStr";
		String dbMsg="[MusicPlayerService]keyNmae=" + keyNmae;
		MyUtil MyUtil = new MyUtil();
		retStr = MyUtil.getPrefStr(keyNmae , defaultVal,context);
		return retStr;
	}

	public static int getPrefInt(String keyNmae , int defaultVal,Context context) {        //プリファレンスの読込み
		int retInt = -99;
		final String TAG = "getPrefStr";
		String dbMsg="[MusicPlayerService]keyNmae=" + keyNmae;
		MyUtil MyUtil = new MyUtil();
		retInt = MyUtil.getPrefInt(keyNmae , defaultVal,context);
		return retInt;
	}

	public static boolean setPrefStr(String keyNmae , String wrightVal , Context context) {        //プリファレンスの読込み
		boolean retBool = false;
		final String TAG = "setPrefStr";
		String dbMsg="[MusicPlayerService]keyNmae=" + keyNmae;
		MyUtil MyUtil = new MyUtil();
		retBool = MyUtil.setPreStr(keyNmae , wrightVal,context);
		return retBool;
	}

	public static boolean setPrefInt(String keyNmae , int wrightVal , Context context) {        //プリファレンスの読込み
		boolean retBool = false;
		final String TAG = "getPrefStr";
		String dbMsg="[MusicPlayerService]keyNmae=" + keyNmae;
		MyUtil MyUtil = new MyUtil();
		retBool = MyUtil.setPrefInt(keyNmae , wrightVal,context);
		return retBool;
	}

	public static void myLog(String TAG , String dbMsg) {
		MyUtil MyUtil = new MyUtil();
		MyUtil.myLog(TAG , "[MusicPlayerService]" + dbMsg);
	}

	public static void myErrorLog(String TAG , String dbMsg) {
		MyUtil MyUtil = new MyUtil();
		MyUtil.myErrorLog(TAG , "[MusicPlayerService]" + dbMsg);
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

