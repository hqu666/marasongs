package com.hijiyam_koubou.marasongs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaDescription;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.browse.MediaBrowser;
import android.media.browse.MediaBrowser.MediaItem;
import android.media.session.MediaSession;
import android.os.BaseBundle;
import android.os.Bundle;
import android.service.media.MediaBrowserService;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("NewApi")
public class AndroidAutoMediaService extends MediaBrowserService implements OnPreparedListener, OnCompletionListener, OnErrorListener{
	OrgUtil ORGUT;				//自作関数集

	private MediaSession mMediaSession;							// MediaSessionクラス
	private MediaPlayer  mMediaPlayer;								// MediaPlayerクラス
	private List<MediaSession.QueueItem> mPlayingQueue;			// 再生キューリスト
	private int mCurrentQueueIndex;								// 再生キューのインデックス
	private static final String MEDIA_ID_ROOT = "__ROOT__";

	private List<Item> mItems;
	private int mIndex;
	private String dataFN = null;		// 再生データのデータパス
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		final String TAG = "onCreate[AndroidAutoMediaService]";
		String dbMsg="開始";/////次はonCreate
		try{
			if( ORGUT == null ) {
				ORGUT = new OrgUtil();	//自作関数集
			}
			readPref();
			mPlayingQueue = new ArrayList<MediaSession.QueueItem>();		// 再生リストのオブジェクトを作る
			mMediaSession = new MediaSession(this, "MyMediaSession");		// MediaSessionを生成
			setSessionToken(mMediaSession.getSessionToken());
			mMediaSession.setCallback(new MyMediaSessionCallback());		// コールバックを設定
			mCurrentQueueIndex = 0;		// 再生キューの位置を初期化
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}
	
//データ送受信/////////////////////////////////////////////////////////////////////////////////////////////////////////	
	//プリファレンス
	public SharedPreferences sharedPref;
	public Editor myEditor ;
	public String nowList;				//再生中のプレイリスト名
	public int nowList_id = 0;			//再生中のプレイリストID
	public String nowList_data = null;		//再生中のプレイリストの保存場所
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
	public int crossFeadTime;		//再生終了時、何ms前に次の曲に切り替えるか
	public int mcPosition;			//現在の再生ポジション☆生成時は最初から
	public int saiseiJikan;				//DURATION;継続;The duration of the audio file, in ms;Type: INTEGER (long)
	public List<String> pref_toneList;		//プリファレンス保存用トーンリスト
	public boolean bBoot = false;					//バスブート

	public void readPref() {        //プリファレンスの読込み
		final String TAG = "readPref";
		String dbMsg = "[MuList]";
		try {
			MyPreferences myPreferences = new MyPreferences();
			dbMsg += "MyPreferencesy読込み";
			myPreferences.readPrif(this);
			sharedPref = MyPreferences.sharedPref;
			myEditor =myPreferences.myEditor;

			pref_compBunki = myPreferences.pref_compBunki;			//コンピレーション設定[%]
			pref_list_simple =myPreferences.pref_list_simple;				//シンプルなリスト表示（サムネールなど省略）

			pref_artist_bunnri = myPreferences.pref_artist_bunnri;		//アーティストリストを分離する曲数
			pref_saikin_tuika = myPreferences.pref_saikin_tuika;			//最近追加リストのデフォルト枚数
			pref_saikin_sisei = myPreferences.pref_saikin_sisei;		//最近再生加リストのデフォルト枚数
			repeatType = myPreferences.repeatType;							//リピート再生の種類
			rp_pp = myPreferences.rp_pp;							//2点間リピート中
			pp_start = Integer.parseInt(myPreferences.pp_start);							//リピート区間開始点
			pp_end = Integer.parseInt(myPreferences.pp_end);								//リピート区間終了点

			pref_lockscreen =myPreferences.pref_lockscreen;				//ロックスクリーンプレイヤー</string>
			pref_notifplayer =myPreferences.pref_notifplayer;				//ノティフィケーションプレイヤー</string>
			pref_cyakusinn_fukki=myPreferences.pref_cyakusinn_fukki;		//終話後に自動再生
			pref_bt_renkei =myPreferences.pref_bt_renkei;				//Bluetoothの接続に連携して一時停止/再開
			nowList_id = Integer.parseInt(myPreferences.nowList_id);				//再生中のプレイリストID	playListID
			nowList = myPreferences.nowList;					//再生中のプレイリスト名	playlistNAME
			play_order = myPreferences.play_order;
//			//アーティストごとの情報
//			artistID = myPreferences.artistID;
//			//アルバムごとの情報
//			albumID = myPreferences.albumID;
//			//曲ごとの情報
//			audioID = myPreferences.audioID;
			dataURL = myPreferences.dataURL;
			pref_toneList = myPreferences.pref_toneList;		//プリファレンス保存用トーンリスト
			tone_name = myPreferences.tone_name;				//トーン名称
			bBoot = myPreferences.bBoot;					//バスブート
			reverbBangou = myPreferences.reverbBangou;				//リバーブ効果番号
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}																	//設定読込・旧バージョン設定の消去


	@SuppressWarnings("unchecked")
//	public void setteriYomikomi(){		//<onCreate	プリファレンスに記録されているデータ読み込み
//		final String TAG = "setteriYomikomi[AndroidAutoMediaService]";
//		String dbMsg="開始";/////////////////////////////////////
//		long start = System.currentTimeMillis();		// 開始時刻の取得
//		try{
//			if( ORGUT == null ) {
//				ORGUT = new OrgUtil();	//自作関数集
//			}
//		String fName =  "/data/data/" +getPackageName()+"/shared_prefs/" + getString(R.string.pref_main_file) +".xml";
//			dbMsg="fName = " + fName;/////////////////////////////////////
//			File tFile = new File(fName);
//			dbMsg= dbMsg +">>有無 = " + tFile.exists();/////////////////////////////////////
//			sharedPref = getSharedPreferences( getResources().getString(R.string.pref_main_file) ,MODE_PRIVATE);		//	getSharedPreferences(prefFname,MODE_PRIVATE);
//			mainEditor = sharedPref.edit();
//			dbMsg= dbMsg +">>" + tFile.exists();/////////////////////////////////////
//			if( ! tFile.exists()){
//				dbMsg="shared_prefs無し";/////////////////////////////////////
//			}
//			Map<String, ?> keys = sharedPref.getAll();
//			if( keys.size() > 0 ){			//プリファレンスが出来ている
//				int i=0;
//				i=0;
//				for (String key : keys.keySet()) {
//					i++;
//					dbMsg =  i+"/"+keys.size()+")　"+key;///////////////+","+(keys.get(key) instanceof String)+",instanceof Boolean="+(keys.get(key) instanceof Boolean);////////////////////////////////////////////////////////////////////////////
//					if( keys.get(key) != null){			//if( readStr != null || ! readStr.equals("")){
//						dbMsg +="は "+String.valueOf(keys.get(key));///////////////+","+(keys.get(key) instanceof String)+",instanceof Boolean="+(keys.get(key) instanceof Boolean);////////////////////////////////////////////////////////////////////////////
//						try{
//							if(key.equals("pref_gyapless")){					//クロスフェード時間
//								dbMsg +=  "クロスフェード時間"+crossFeadTime ;////////////////////////////////////////////////////////////////////////////
//								crossFeadTime = Integer.valueOf(keys.get(key).toString());
//							}else if(key.equals("pref_list_simple")){
//								dbMsg += "は"+(keys.get(key)).toString()+ ">>シンプルなリスト表示（サムネールなど省略）"+pref_list_simple;////////////////////////////////////////////////////////////////////////////
//								pref_list_simple = Boolean.valueOf((String) keys.get(key)) ;
//								dbMsg +=  ">>"+pref_list_simple;////////////////////////////////////////////////////////////////////////////
//							}else if(key.equals("pref_data_url")){
//								dbMsg +=  ">>再生中のファイル名" ;////////////////////////////////////////////////////////////////////////////
//								dataFN = String.valueOf(keys.get(key));							//再生中のファイル名//DATA;The data stream for the file ;Type: DATA STREAM
//								dbMsg =">>再生中のファイル名" + dataFN ;
//								File chFile = new File(dataFN);
//								dbMsg += " , " + dataFN +"="+chFile.exists();//////////////////
//								if(! chFile.exists() ){
//									mainEditor.remove("dataFN");
//									dataFN = null;
//									String  pdMes = getResources().getString(R.string.setteriYomikomi_data_meg) ;		//前回再生していた音楽ファイルが見つかりません。\n再生する曲を選択して下さい。</string>
//									dbMsg=dbMsg  + pdMes;
//									Toast.makeText(this, (CharSequence) pdMes, Toast.LENGTH_SHORT).show();
//								}
//							}else if(key.equals("pref_saisei_jikan")){
//								dbMsg += ">>再生中音楽ファイルの再開時間" ;//////////////////
//								mcPosition = Integer.valueOf(String.valueOf(keys.get(key)));				//選択中選択ポジション
//								dbMsg += "["+ORGUT.sdf_mss.format(mcPosition) + "/";////////////////////////////////////////////////////////////////////////////
//							}else if(key.equals("pref_duration")){
//								dbMsg += ">>再生中音楽ファイルの長さ";//////////////////
//								saiseiJikan = Integer.valueOf(String.valueOf(keys.get(key)));				//再生時間
//							}else if(key.equals("pref_cyakusinn_fukki")){			//着信後の復帰
//								pref_cyakusinn_fukki = Boolean.valueOf(keys.get(key).toString());
//									dbMsg +=  "着信後の復帰=" + pref_cyakusinn_fukki;////////////////////////////////////////////////////////////////////////////
//							}else if(key.equals("pref_compBunki")){
//								dbMsg += ">>コンピレーション分岐点" ;//////////////////
//								pref_compBunki = String.valueOf(keys.get(key));			//コンピレーション分岐点
//							}else if(key.equals("nowList")){			//");
//								dbMsg +=  ">再生中のプレイリスト名=" ;
//								nowList = String.valueOf(keys.get(key).toString());
//								dbMsg +=   String.valueOf(nowList)  ;
//							}else if(key.equals("nowList_id")){			//");
//								dbMsg +=  ">再生中のプレイリストID=" ;
//								nowList_id = Integer.valueOf(keys.get(key).toString());	//
//								dbMsg +=   String.valueOf(nowList_id)  ;
//							}else if(key.equals("repeatType")){			//");;			//
//								dbMsg +=  ">リピート再生の種類=" ;
//								repeatType = Integer.valueOf(String.valueOf(keys.get(key)));	//
//								if(repeatType != MaraSonActivity.rp_point){
//									pp_start = 0;
//								}
//								dbMsg +=   String.valueOf(repeatType)  ;
//							}else if(key.equals("repeatArtist")){			//");;			//
//								dbMsg +=  ">リピートさせるアーティスト名=" ;
//								repeatArtist = String.valueOf(keys.get(key).toString());	//
//								dbMsg +=   String.valueOf(repeatType)  ;
//							}else if(key.equals("pref_nitenkan")){			//");
//								rp_pp = Boolean.valueOf(keys.get(key).toString());	//
//								dbMsg +=  "二点間再生中=" + rp_pp;	//
//							}else if(key.equals("pref_nitenkan_start")){			//");
//								pp_start = Integer.valueOf(keys.get(key).toString());	//
//								dbMsg +=  "二点間再生開始点=" + pp_start ;////////pref_nitenkan_start//////////
//							}else if(key.equals("pref_nitenkan_end")){			//");
//								pp_end = Integer.valueOf(keys.get(key).toString());	//
//								dbMsg +=  "二点間再生終了点=" + pp_end ;/////pref_nitenkan_end////////////////////////////
//							}else if(key.equals("mIndex")){			//");
//								dbMsg +=  ",play_order=ID=" ;
//								mIndex = Integer.valueOf(keys.get(key).toString());	//
//								dbMsg +=   String.valueOf(mIndex)  ;
//							}else if(key.equals("nowList_data")){			//");
//								dbMsg +=  ",プレイリストの保存場所=" ;
//								nowList_data = String.valueOf(keys.get(key).toString());
//								dbMsg +=   String.valueOf(nowList_data)  ;
//							}else if(key.equals("pref_bt_renkei")){			//");
//								dbMsg +=  ">>Bluetoothの接続に連携=" ;
//								pref_bt_renkei = Boolean.valueOf(keys.get(key).toString());
//								dbMsg +=   String.valueOf(pref_bt_renkei)  ;////////////////	 Bluetoothの接続に連携して一時停止/再開////////////////////////////////////////////////////////////
//
//							}else if(key.equals("tone_name")){
//								tone_name = String.valueOf(keys.get(key).toString());	//
//								dbMsg +=  "トーン名称=" + tone_name ;
//				//				myLog(TAG, dbMsg);
//							}else if(key.equals("pref_toneList")){				//http://qiita.com/tomoima525/items/f8cf688ad9571d17df41
//								String stringList = String.valueOf(keys.get(key));											//bundle.getString("list");  //key名が"list"のものを取り出す
//								dbMsg +=  ",stringList= " + stringList;
//								try {
//									JSONArray array = new JSONArray(stringList);
//									dbMsg +=  ",array= " + array;
//									int length = array.length();
//									dbMsg +=  "= " + length +"件";
//									pref_toneList =  new ArrayList<String>();				//トーンリストの初期化
//									for(int j = 0; j < length; j++){
//										dbMsg +=  "(" + j + "/" + length  + ")" + array.optString(j);
//										pref_toneList.add(array.optString(j));
//									}
//								} catch (JSONException e1) {
//									e1.printStackTrace();
//								}
//								dbMsg +=  ",トーン配列=" + pref_toneList ;
//				//				myLog(TAG, dbMsg);
//							}else if(key.equals("bBoot")){
//								bBoot = Boolean.valueOf(keys.get(key).toString());	//
//								dbMsg +=  "バスブート=" + bBoot ;
//							}else if(key.equals("reverbBangou")){
//								reverbBangou = Short.valueOf(keys.get(key).toString());	//
//								dbMsg +=  "リバーブ効果番号=" + reverbBangou ;
//
//							}
//		//					myLog(TAG, dbMsg);
//						} catch (Exception e) {
//							Log.e(TAG,dbMsg+"；"+e);
//						}
//					}
//				}			//for (String key : keys.keySet())
//				//読み込み/////////////////////////////////////////////////////
//				long end=System.currentTimeMillis();		// 終了時刻の取得
//				dbMsg=(int)((end - start)) + "mS";		//	<string name="">所要時間</string>
//		//		myLog(TAG, dbMsg);
//			}
//			long end=System.currentTimeMillis();		// 終了時刻の取得
//			dbMsg=dbMsg +";"+ (int)((end - start)) + "m秒で終了";
////			myLog(TAG, dbMsg);
//		}catch (Exception e) {
//			Log.e(TAG,dbMsg +"で"+e.toString());
//		}
//	}

	
	
//リクエスト受信////////////////////////////////////////////////////////////////////////////////////////データ送受信///	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {			//
		final String TAG = "onStartCommand[AndroidAutoMediaService]";
		String dbMsg="開始";/////次はonCreate
		try{
			dbMsg = "intent = " + intent;
			dbMsg +=",flags = " + flags;
			dbMsg +=",startId = " + startId;
			readPref();
				myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
//		return START_REDELIVER_INTENT;				//	再起動前と同じ順番で再起動してくれる。Intentも渡ってきています。
		return START_NOT_STICKY;				//APIL5;サービスが強制終了した場合、サービスは再起動しない
	/*START_STICKY	サービスが強制終了した場合、サービスは再起動するonStartCommand()が再度呼び出され、Intentにnullが渡される
	*/
	}

	@Override
	public BrowserRoot onGetRoot(String clientPackageName, int clientUid, Bundle rootHints) {
		final String TAG = "onGetRoot[AndroidAutoMediaService]";
		String dbMsg="開始";/////次はonCreate
		try{
			dbMsg = "clientPackageName = " + clientPackageName;
			dbMsg +=",clientUid = " + clientUid;
			dbMsg +=",rootHints = " + rootHints;
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return new BrowserRoot(MEDIA_ID_ROOT, null);
	}

	@Override
	public void onLoadChildren(String parentId, Result <List<MediaItem>> result) {
		final String TAG = "onLoadChildren[AndroidAutoMediaService]";
		String dbMsg="開始";/////次はonCreate
		try{
			dbMsg = "parentId = " + parentId;
			dbMsg +=",result = " + result;

			List<MediaBrowser.MediaItem> mediaItems = new ArrayList<MediaBrowser.MediaItem>();
			MediaDescription.Builder mdb1 = new MediaDescription.Builder();		// 再生データ情報の設定
			final Bundle mediaBundle1 = new Bundle();					// 再生データ1
			mediaBundle1.putString("Path", dataFN); 		// 再生データのデータパス
			mediaBundle1.putInt("Index", mIndex);							// 再生データの曲のインデックス
			mdb1.setMediaId("MediaID01");								// 再生データのメディアID
			mdb1.setTitle("Title01");									// 再生データのタイトル
			mdb1.setSubtitle("SubTitle01");								// 再生データのサブタイトル
			mdb1.setExtras(mediaBundle1);

			MediaDescription.Builder mdb2 = new MediaDescription.Builder();		// 再生データ2
			final Bundle mediaBundle2 = new Bundle();
			mediaBundle2.putString("Path", "mnt/sdcard/M02.mp3");		//次曲のデータパス
			mediaBundle2.putInt("Index", 1);							// 次曲の曲のインデックス
			mdb2.setMediaId("MediaID02");								// 次曲のメディアID
			mdb2.setTitle("Title02");									// 次曲のタイトル
			mdb2.setSubtitle("SubTitle02");								// 次曲のサブタイトル
			mdb2.setExtras(mediaBundle2);

			mediaItems.add(new MediaBrowser.MediaItem(mdb1.build(),MediaBrowser.MediaItem.FLAG_PLAYABLE));
			mediaItems.add(new MediaBrowser.MediaItem(mdb2.build(), MediaBrowser.MediaItem.FLAG_PLAYABLE));

			result.detach();		// sendResult()をコールする前にdetach()のコールが必要
			result.sendResult(mediaItems);

			mPlayingQueue.add(new MediaSession.QueueItem(mdb1.build(), 0));		// 再生キューをセッションに設定
			mPlayingQueue.add(new MediaSession.QueueItem(mdb2.build(), 1));
			mMediaSession.setQueue(mPlayingQueue);
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}

	}

	private void playMusic() {
		final String TAG = "playMusic[AndroidAutoMediaService]";
		String dbMsg="開始";/////次はonCreate
		try{
			if (mMediaPlayer == null) {		// プレイヤー生成と準備(生成済みの場合はリセット)
				mMediaPlayer = new MediaPlayer();
				mMediaPlayer.setOnPreparedListener(this);
				mMediaPlayer.setOnCompletionListener(this);
				mMediaPlayer.setOnErrorListener(this);
			} else {
				mMediaPlayer.reset();
			}
			MediaSession.QueueItem queueItem = mPlayingQueue.get(mCurrentQueueIndex);		// 現在のキュー位置を元に再生データのパスを取得する
			String path = queueItem.getDescription().getExtras().getString("Path");
			try {
				mMediaPlayer.setDataSource(path);		// MediaPlayerのデータ設定と準備(非同期)
				mMediaPlayer.prepareAsync();
			} catch (IllegalArgumentException e) {
		        e.printStackTrace();
		    } catch (SecurityException e) {
		        e.printStackTrace();
		    } catch (IllegalStateException e) {
		        e.printStackTrace();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		final String TAG = "onPrepared[AndroidAutoMediaService]";
		String dbMsg="開始";/////次はonCreate
		try{
		    // 再生準備完了通知を受け、再生を行う
		    if(mMediaPlayer != null) {
		        mMediaPlayer.start();
		    }
			//	myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}

	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		final String TAG = "onCompletion[AndroidAutoMediaService]";
		String dbMsg="開始";/////次はonCreate
		try{
			//	myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}

	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		final String TAG = "onError[AndroidAutoMediaService]";
		String dbMsg="開始";/////次はonCreate
		try{
			dbMsg = "mp = " + mp;
			dbMsg +=",what = " + what;
			dbMsg +=",extra = " + extra;

			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return false;
	}


	private final class MyMediaSessionCallback extends MediaSession.Callback {

		MyMediaSessionCallback() {
			final String TAG = "MyMediaSessionCallback[AndroidAutoMediaService]";
			String dbMsg="開始";/////次はonCreate
			try{
				myLog(TAG, dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
		}

		@Override
		public void onPlay() {
			final String TAG = "onPlay[AndroidAutoMediaService]";
			String dbMsg="開始";/////次はonCreate
			try{
				playMusic();
				myLog(TAG, dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}

		}

		@Override
		public void onPlayFromMediaId(String mediaId, Bundle extras) {	// リスト選択時にコールされる
			final String TAG = "onPlayFromMediaId[AndroidAutoMediaService]";
			String dbMsg="開始";/////次はonCreate
			try{
				dbMsg = "mediaId = " + mediaId;
				dbMsg +=",extras = " + extras;

				for(int i = 0; i < mPlayingQueue.size(); i++) {		// MediaIDを元に曲のインデックス番号を検索し、設定する。
					MediaSession.QueueItem queueItem = mPlayingQueue.get(i);
					MediaDescription md = queueItem.getDescription();
					if(mediaId.equals(md.getMediaId())) {
						mCurrentQueueIndex = md.getExtras().getInt("Index");
					}
				}
				playMusic();
				myLog(TAG, dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}

		}

		@Override
		public void onSkipToNext() {
			final String TAG = "onSkipToNext[AndroidAutoMediaService]";
			String dbMsg="開始";/////次はonCreate
			try{
				dbMsg = "mCurrentQueueIndex = " + mCurrentQueueIndex;
				dbMsg +=",size = " + mPlayingQueue.size();

				mCurrentQueueIndex++;		// 再生キューの位置を次へ
				if (mCurrentQueueIndex >= mPlayingQueue.size()) {
					mCurrentQueueIndex = 0;
				}
				playMusic();
				myLog(TAG, dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}

		}

		@Override
		public void onSkipToPrevious() {
			final String TAG = "onSkipToPrevious[AndroidAutoMediaService]";
			String dbMsg="開始";
			try{
				dbMsg = "mCurrentQueueIndex = " + mCurrentQueueIndex;
				dbMsg +=",size = " + mPlayingQueue.size();

				mCurrentQueueIndex--;		// 再生キューの位置を前へ
				if (mCurrentQueueIndex < 0) {
					mCurrentQueueIndex = 0;
				}
				playMusic();
				myLog(TAG, dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}

		}
	}		//MyMediaSessionCallback

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

/**
 * Android Auto用音楽再生アプリの作り方			http://bril-tech.blogspot.jp/2014/12/android-auto.html
 * 						https://github.com/googlesamples/android-MediaBrowserService
 * */
