package com.hijiyam_koubou.marasongs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import net.nend.android.NendAdInformationListener;
import net.nend.android.NendAdView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MaraSonActivity extends AppCompatActivity
	implements View.OnClickListener  , View.OnLongClickListener , View.OnKeyListener ,OnSeekBarChangeListener , android.view.GestureDetector.OnGestureListener{

	public static final String ACTION_PLAY_PAUSE = "com.example.android.notification.action.PLAY_PAUSE";
	public static final String ACTION_INIT = "com.example.android.notification.action.INIT";
	public Context rContext ;
	OrgUtil ORGUT;		//自作関数集
	OrgUtilFile OGFil;	//ファイル関連関数
	MusicPlaylist musicPlaylist ;

	MyPreferences myPreferences;
	ZenkyokuList ZKL;
	MuList MLIST;
	public SimpleDateFormat convertFormat;						//時差抜き時分秒表示フォーマット
	public WindowManager wm;			// ウィンドウマネージャのインスタンス取得
	public int dWidth;								//ディスプレイ幅
	public int dHeigh;							//ディスプレイ高

	public long start = 0;				// 開始時刻の取得
	public String pref_apiLv;
	public int pref_sonota_vercord ;//////////////このアプリのバージョンコード///////////////////////
	public boolean prTT_dpad = false;				//ダイヤルキー有り
	//プリファレンス
	public SharedPreferences sharedPref;
	public Editor myEditor ;

	public String myPFN = "ma_pref";
	public String pref_compBunki = "40";				//コンピレーション分岐点 %
	public boolean pref_cyakusinn_fukki=true;			//終話後に自動再生
	public boolean pref_bt_renkei =true;				//Bluetoothの接続に連携して一時停止/再開
	public boolean pref_list_simple =false;			//シンプルなリスト表示（サムネールなど省略）
	public boolean pref_lockscreen =true;				//ロックスクリーンプレイヤー</string>
	public boolean pref_notifplayer =true;				//ノティフィケーションプレイヤー</string>
	public String alPFN = "al_pref";
	public int mcPosition;
	public String pref_artist_bunnri = "100";		//アーティストリストを分離する曲数
	public String pref_saikin_tuika = "1";			//最近追加リストのデフォルト枚数
	public String pref_saikin_sisei = "100";		//最近再生加リストのデフォルト枚数

	public int pref_zenkyoku_list_id;			// 全曲リスト
	public int saikintuika_list_id;			//最近追加
	public int saikinsisei_list_id;			//最近再生

	//曲ごとの情報
	public String audioID;
	public String dataURL = null;

	public String aWPFN = "aW_pref";
	public String myFolder ;
//	動作経路
	static final int RESULT_ENABLE = 1;
	public int shigot_bangou = 0;
	public int kidou_jyoukyou= 100;
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
	public static final int v_play_list = LyricWeb+1;					//アーティストリスト表示
	public static final int v_artist = v_play_list+1;					//アーティストリスト表示
	public static final int v_alubum = v_artist+1;					//アルバムリスト表示
	public static final int v_titol = v_alubum+1;						//タイトルリスト表示
	public static final int rp_artist = v_titol +1;					//アーティストリピート指定ボタン
	public static final int rp_album = rp_artist +1;					//アルバムリピート指定ボタン
	public static final int rp_titol = rp_album +1;					//タイトルリピート指定ボタン
	public static final int rp_point = rp_titol +1;					//二点間リピート
	public static final int settei_hyouji = rp_point+5;					//設定表示
	public static final int quit_all = settei_hyouji+1;					//すべて終了

	Toolbar toolbar;						//このアクティビティのtoolBar
//	public TextView plistDPTF;					//プレイリスト表示
	public TextView songIDPTF;					//リスト中の何曲目か
	public TextView titolAllPTF;				//全タイトルカウント

	public TextView artist_tv;					//アルバムアーティスト
	public TextView artistTotalPTF;			//アルバムアーティス合計
	public TextView artistIDPTF;				//アルバムアーティスカウント
	public TextView alubum_tv;					//アルバム
	public TextView alubumTotalPTF;		//アルバム合計
	public TextView albumIDPTF;				//アルバムカウント
	public TextView titol_tv;						//タイトル
	public TextView tIDPTF;						//タイトル合計
	public TextView nIDPTF;						//タイトルカウント
	public TextView saiseiPositionPTF;		//再生ポイント
	public TextView totalTimePTF;			//再生時間
	public TextView ruikei_jikan_tv;			//累積時間
	public TextView ruikei_kyoku_tv;			//累積曲数
	public TextView zenkai_ruikei_suu_tv;	//前回の累積曲数
	public TextView zenkai_ruikei_tv;			//前回の累積
	public TextView vol_tf;												//音量表示
	public TextView dviceStyte;			//デバイスの接続情報
	public HorizontalScrollView dviceStyteHSV;					//デバイスの接続情報のスクロール
	public ImageButton ppPBT;			//再生ボタン
	public ImageButton ffPBT;			//送りボタン
	public ImageButton rewPBT;			//戻しボタン
	public ImageButton vol_btn;								//ボリュームボタン
	public ViewFlipper pp_vf;									//中心部の表示枠
	public ImageView mpJakeImg;			//ジャケット
	public TextView lyric_tv;					//歌詞表示
	public SeekBar saiseiSeekMP;		//シークバー
	public LinearLayout pp_pp_ll;						//二点間再生レイアウト
	public TextView pp_pp_start_tf;					//二点間再生開始点
	public TextView pp_pp_end_tf;						//二点間再生終了点
	public LinearLayout artistCountLL;					//アーティストカウント
	public LinearLayout albumCountLL;					//アルバムカウント
	public LinearLayout titolCountLL;					//タイトルカウント
	public LinearLayout pp_zenkai_ll;								//前回の累積レイアウト
	public Drawable dofoltSBDrawable;					//シークバーの元設定
	public LinearLayout pp_bt_ll;						//buletooth情報
	//	public LinearLayout pp_koukoku;					//広告表示枠		include
	private LinearLayout advertisement_ll;
	private LinearLayout ad_layout;
	private AdView adView;
	private LinearLayout nend_layout;
	private NendAdView nendAdView;

	public List<String> dataFNList = null;				//uri配列
	public List<String> artistList = null;				//クレジットされているアーティスト名
	public List<String> albumList = null;				//アルバム名
	public List<String> titolList = null;								//曲名
	public Map<String, Object> artistMap;				//アーティストリスト用
	public  List<Map<String, Object>> artistAL;		//アーティストリスト用ArrayList
	public Map<String, Object> albumMap;				//アルバムトリスト用
	public List<Map<String, Object>> albumAL;			//アルバムリスト用ArrayList
	public Map<String, Object> titolMap;				//タイトルリスト用
	public List<Map<String, Object>> titolAL;			//タイトルムリスト用ArrayList

	public ZenkyokuHelper zenkyokuHelper = null;				//全曲リストヘルパー
	public SQLiteDatabase Zenkyoku_db;							//全曲リストファイル
	public String zenkyokuTName;									//全曲リストのテーブル名
	public SQLiteDatabase artist_db;
	public ArtistHelper artistHelper;		//アーティスト名のリストの定義ファイル
	public String artistTName;			//アーティストリストのテーブル名
	public Cursor cursor;
	public int songIDTotal;			//全曲数
	public int titolTotal;				//曲合計
	public int titolID;					//曲カウント
	public int alubumTotal;			//アルバム合計
	public int albumID;					//アルバムカウント
	public int artistID;					//再生中のアーティスト
	public int artistIDTotal;			//全アーティスト数
	public String nowList;				//再生中のプレイリスト名
	public int nowList_id = 0;			//再生中のプレイリストID
	/**リスト中のインデックス*/
	public int mIndex;
	/**リストの総登録曲数*/
	public int listEnd;

	public String nowList_data = null;		//再生中のプレイリストの保存場所
	public String creditArtistName;		//クレジットされているアーティスト名
	public String albumArtist ="";		//リストアップしたアルバムアーティスト名
	public String albumName ="";		//アルバム名
	public String titolName ="";			//曲名
	public boolean IsPlaying ;			//再生中か
	public boolean toPlaying ;			//曲をセットしたら再生
	public boolean b_Playing = false;			//状況変化
	public boolean IsSeisei ;			//生成中
	public int saiseiJikan;					//DURATION;継続;The duration of the audio file, in ms;Type: INTEGER (long)
	public int releaceYear = 0;			//録音年
	public String albumArt =null;		//アルバムアートのURI
	public String b_albumArt =null;		//前のアルバムアートのURI

	public int trackInAlbum = 0;				//アルバム内の曲数
	public String maenoArtist =null;		//前に再生していたアルバムアーティスト名
	public String b_artist = "";				//それまで参照していたアーティスト名
	public String b_album = "";				//それまで参照していたアルバム名
	public String b_dataFN ="";				//すでに再生している再生ファイル
	public String n_dataFN =null;				//次に再生する再生ファイル
	public String b_List =null;			//前に再生していたプレイリスト
	public int b_List_id = 0;			//前のプレイリストID
	public int modori_List_id = 0;			//リピート前のプレイリストID
	private int b_index;				//前の曲順
	private int t_index;				//そのリストの曲数
	public String b_albumName =null;			//前アルバム名
	public String b_titolName =null;			//前曲名

	public String stateBaseStr;
	static final int REQUEST_ENABLE_BT = 0;

	public String b_state ="";
	public int zenkai_saiseKyoku = 0;		//前回の連続再生曲数
	public long zenkai_saiseijikann = 0L;		//前回の連続再生時間

	//プレイヤー
	public float saitaiFont;				//最大フォントサイズ
	public long crossFeadTime=0;		//再生終了時、何ms前に次の曲に切り替えるか
	public long ruikeiSTTime = 0;			//累積開始時間
	public int ruikeikyoku = 0;				//累積曲数
	public String ruikei_titol;			//曲累計
	public String ruikei_album;			//アルバム累計
	public String ruikei_artist;		//アーティスト累計
	public String file_saisinn;			//最新更新日
	public boolean playerBGColor_w;		//プレイヤーの背景は白

	public int imanoJyoutai;		//リストの状態	起動直後；veiwPlayer / 再選択chyangeSong

	public String saveDir= null;
	public static final String artisListFile= "artistList.txt";
	public String file_ex;		//メモリーカードの音楽ファイルフォルダ
	public String file_in;		//内蔵メモリの音楽ファイルフォルダ
	public String file_wr;		//設定保存フォルダ
	//サービス
	public Intent MPSIntent;
	public String psSarviceUri;
	public ComponentName MPSName;
	public MusicPlayerService MPS;
	public MusicReceiver mReceiver;
	public Handler mHandler = new Handler();
	public IntentFilter mFilter;
	public String b_stateStr;
	public  BuletoohtReceiver btReceiver;
	public BluetoothAdapter mBluetoothAdapter = null;
	public Handler btHandler = new Handler();
	public Animation slideInFromLeft;			//左フリック
	public Animation slideInFromRight;			//右フリック
	private GestureDetector gestureDetector;

	public DevicePolicyManager mDevicePolicyManager;
	public ComponentName mDarSample;
	//Bluetooth
//	public String setuzokuBT_Address="";

	/**
	 * 終了処理
	 * 戻り値の設定
	 * */
	public void quitMe(){		//このアプリを終了するonClick(View v)
		final String TAG = "quitMe";
		String dbMsg = "[MaraSonActivity]";
		try{
			dbMsg +="、mVisualizer="+ mVisualizer;//////////////////
			if( mVisualizer != null ){
				mVisualizer.release();
				mVisualizer = null;
			}
			dbMsg += ",adView="+ mAdView;//////////////////
			if( mAdView != null ){
				mAdView.destroy();
				dbMsg += ">>"+ mAdView;//////////////////
			}
			dbMsg +=",startVol=" + startVol;/////////////////////////////////////
			reSetVol();			//設定されたミュートを解除
			audioManage.setStreamMute(AudioManager.STREAM_MUSIC, false);					// ミュート設定をONにする
			if ( startVol == 0) {	//起動時の音楽音量
				audioManage.setStreamVolume(AudioManager.STREAM_MUSIC, 10, 0);
			}else{
				audioManage.setStreamVolume(AudioManager.STREAM_MUSIC, startVol, 0);			//起動時の音楽音量に戻す
			}
			receiverHaki();							//unregisterReceiverでレシーバーを破棄
			ArrayList<String> serviceList = ORGUT.getMyService( this , getApplicationContext().getPackageName());			//起動しているサービスを取得
			dbMsg += "破棄前serviceList="+ serviceList ;/////////////////////////////////////
			if( 0 < serviceList.size() ){
				dbMsg += ",MPSIntent="+ MPSIntent;//////////////////
				for(String serviceName : serviceList){
					if(serviceName.contains("MusicPlayerService")){
						dbMsg +=",MPSIntent=" + MPSIntent;/////////////////////////////////////
						if( MPSIntent == null){
							MPSIntent = new Intent(MaraSonActivity.this, MusicPlayerService.class);
							dbMsg +=">>" + MPSIntent;/////////////////////////////////////
						}
						MPSIntent.setAction(MusicPlayerService.ACTION_SYUURYOU);					//service内のquitMe
						startService(MPSIntent);
						stopService(MPSIntent);			//こっちで消える？
					}else if(serviceName.contains("NotifRecever")){
						MPSIntent = new Intent(MaraSonActivity.this, NotifRecever.class);
						dbMsg += ",NotifRecever="+ MPSIntent;//////////////////
						stopService(MPSIntent);	//		MPSName = startService(MPSIntent);
						dbMsg +=">>" + MPSIntent;/////////////////////////////////////
					}else if(serviceName.contains("BuletoohtReceiver")){
						MPSIntent = new Intent(MaraSonActivity.this, BuletoohtReceiver.class);
						dbMsg += ",BuletoohtReceiver="+ MPSIntent;//////////////////
						stopService(MPSIntent);	//		MPSName = startService(MPSIntent);
						dbMsg +=">>" + MPSIntent;/////////////////////////////////////
					}
				}
				serviceList = ORGUT.getMyService( this , getApplicationContext().getPackageName());			//起動しているサービスを取得
				dbMsg +=">>破棄後serviceList="+ serviceList ;/////////////////////////////////////
			}
			dbMsg += ",IsPlaying="+ IsPlaying ;///

			Intent intentSub = new Intent();
			dbMsg +=  ",reqCode=" + reqCode;
			if(reqCode != MyConstants.v_artist && reqCode != MyConstants.v_alubum && reqCode != MyConstants.v_titol) {
				reqCode = MyConstants.v_play_list;
				dbMsg +=  ">>" + reqCode;
			}
			intentSub.putExtra("reqCode",reqCode);
			dbMsg += "[" + nowList_id+ "]" + nowList;
			intentSub.putExtra("nowList_id",nowList_id);
			intentSub.putExtra("nowList",nowList);
			dbMsg += ",mIndex=" + mIndex;
			intentSub.putExtra("mIndex",mIndex);
			dbMsg += ",dataURL=" + dataURL;
			intentSub.putExtra("pref_data_url",dataURL);
			setResult(RESULT_OK, intentSub);

			MaraSonActivity.this.finish ();
			serviceList = ORGUT.getMyService( this , getApplicationContext().getPackageName());			//起動しているサービスを取得
			dbMsg +=",serviceList="+ serviceList ;/////////////////////////////////////
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}	//このアプリを終了する/////////////////////////////////////////////////////////////////////////////////

	//設定変更反映//////////////////////////////////////////////////////////////////////////////////////////////////////////////起動項目////
	public void readPref () {        //プリファレンスの読込み
		final String TAG = "readPref";
		String dbMsg = "[MaraSonActivity]";
		try {
			myPreferences = new MyPreferences();
			dbMsg += "MyPreferencesy読込み";
			myPreferences.readPrif(this);
			dbMsg += "完了";

			sharedPref = MyPreferences.sharedPref;
			myEditor = myPreferences.myEditor;
			pref_apiLv=myPreferences.pref_apiLv;							//APIレベル
			pref_sonota_vercord =myPreferences.pref_sonota_vercord;				//このアプリのバージョンコード
//			dbMsg += "、このアプリのバージョンコード=" + pref_sonota_vercord;
			pref_compBunki = myPreferences.pref_compBunki;			//コンピレーション設定[%]
			pref_list_simple =myPreferences.pref_list_simple;				//シンプルなリスト表示（サムネールなど省略）
			pref_artist_bunnri = myPreferences.pref_artist_bunnri;		//アーティストリストを分離する曲数
			pref_saikin_tuika = myPreferences.pref_saikin_tuika;			//最近追加リストのデフォルト枚数
			pref_saikin_sisei = myPreferences.pref_saikin_sisei;		//最近再生加リストのデフォルト枚数
			repeatType = myPreferences.repeatType;							//リピート再生の種類
			pref_lockscreen =myPreferences.pref_lockscreen;				//ロックスクリーンプレイヤー</string>
			pref_notifplayer =myPreferences.pref_notifplayer;				//ノティフィケーションプレイヤー</string>
			pref_cyakusinn_fukki=myPreferences.pref_cyakusinn_fukki;		//終話後に自動再生
			pref_bt_renkei =myPreferences.pref_bt_renkei;				//Bluetoothの接続に連携して一時停止/再開

			file_in =myPreferences.pref_file_in;		//内蔵メモリ
			file_ex=myPreferences.pref_file_ex;		//メモリーカード
			file_wr= myPreferences.pref_file_wr;		//設定保存フォルダ
			file_saisinn= myPreferences.pref_file_saisinn;	//最新更新日
			nowList_id = Integer.parseInt(myPreferences.nowList_id);
			b_List =myPreferences.b_List;			//前に再生していたプレイリスト
			b_List_id = myPreferences.b_List_id;			//前のプレイリストID
			modori_List_id = myPreferences.modori_List_id;			//リピート前のプレイリストID
			b_index= myPreferences.b_index;				//前の曲順
			pref_toneList = myPreferences.pref_toneList;		//プリファレンス保存用トーンリスト
			toneSeparata = myPreferences.toneSeparata;
			tone_name = myPreferences.tone_name;				//トーン名称
			bBoot = myPreferences.bBoot;					//バスブート
			reverbBangou = myPreferences.reverbBangou;				//リバーブ効果番号
			visualizerType =myPreferences.visualizerType;		//VisualizerはFFT
			dbMsg += "、visualizerType=" + visualizerType;
			pref_zenkyoku_list_id = myPreferences.pref_zenkyoku_list_id;			// 全曲リスト
			saikintuika_list_id = myPreferences.saikintuika_list_id;			//最近追加
			saikinsisei_list_id = myPreferences.saikinsisei_list_id;			//最近再生
			prTT_dpad = myPreferences.prTT_dpad;		//ダイヤルキーの有無
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}																	//設定読込・旧バージョン設定の消去

	//広告////////////////////////////////////////////////////////////////////////
	private AdView mAdView = null;							//Google広告表示エリア
	public int adWidth = 0;
	public int adHight = 0;

	/**ADSens生成*/
	public void setADSens() {
		final String TAG = "setADSens";
		String dbMsg="";
		try {
			if(adView == null){
				adView = new AdView(this);
				String AdUnitID = getString (R.string.banner_ad_unit_id);
//				String AdUnitID = getString (R.string.banner_test_id);//配信停止中のデバッグ用
				dbMsg += ",AdUnitID=" + AdUnitID ;
				adView.setAdUnitId(AdUnitID);
				dbMsg += ",adView=" + adView;
				adView = new AdView(this);
				adView.setAdUnitId(AdUnitID);

				float scale = getResources().getDisplayMetrics().density;
////			int baceWidth =(int)(advertisement_ll.getWidth());
////			dbMsg += ",baceWidth=" + baceWidth;
				dbMsg += ",scale=" + scale;
				int layoutWidth =320;			//(int)(ad_layout.getWidth()/scale);
				int layoutHeight =50;			//(int)(ad_layout.getHeight()/scale);
				dbMsg += "[" + layoutWidth + "×" + layoutHeight + "]";
//			layoutWidth =(int)(320*scale)/2;
//			layoutHeight =(int)(layoutWidth /320 * 50);
////			layoutHeight =(int)(50*scale)/2;
//			dbMsg += ">>[" + layoutWidth + "×" + layoutHeight + "]";
				AdSize customAdSize = new AdSize(layoutWidth, layoutHeight);
				adView.setAdSize(customAdSize);
				ad_layout.addView(adView);

				AdRequest adRequest = new AdRequest.Builder().build();
				dbMsg += ",adRequest=" + adRequest;
				adView.loadAd(adRequest);

				adView.setAdListener(new AdListener() {
					@Override
					public void onAdLoaded() {
						final String TAG = "onAdLoaded";
						String dbMsg = "[adView]";
						try {
							ad_layout.setVisibility (View.VISIBLE);
							myLog(TAG, dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG ,  dbMsg + "で" + e);
						}
					}

//					@Override
					public void onAdFailedToLoad(int errorCode) {
						final String TAG = "onAdFailedToLoad";
						String dbMsg = "[adView]";
						try {
							if(errorCode == 0 ) {
								dbMsg += "ERROR_CODE_INTERNAL_ERROR";
							}else if(errorCode == 1 ){
								dbMsg += "ERROR_CODE_INVALID_REQUEST";
							}else if(errorCode == 2 ){
								dbMsg += "ERROR_CODE_NETWORK_ERROR";
							}else if(errorCode == 3 ){
								dbMsg += "ERROR_CODE_NO_FILL";
							}
							ad_layout.setVisibility (View.GONE);
							myLog(TAG, dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG ,  dbMsg + "で" + e);
						}
					}

					@Override
					public void onAdOpened() {
						final String TAG = "onAdOpened";
						String dbMsg = "[adView]";
						try {
							myLog(TAG, dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG ,  dbMsg + "で" + e);
						}
					}

					@Override
					public void onAdClicked() {
						final String TAG = "onAdClicked";
						String dbMsg = "[adView]";
						try {
							myLog(TAG, dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG ,  dbMsg + "で" + e);
						}
					}

//					@Override
					public void onAdLeftApplication() {
						final String TAG = "onAdLeftApplication";
						String dbMsg = "[adView]";
						try {
							myLog(TAG, dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG ,  dbMsg + "で" + e);
						}
					}

					@Override
					public void onAdClosed() {
						final String TAG = "onAdClosed";
						String dbMsg = "[adView]";
						try {
							myLog(TAG, dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG ,  dbMsg + "で" + e);
						}
					}
				});
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**Nend生成*/
	public void setNend() {               		//https://github.com/fan-ADN/nendSDK-Android/wiki/%E3%83%90%E3%83%8A%E3%83%BC%E5%9E%8B%E5%BA%83%E5%91%8A_%E5%AE%9F%E8%A3%85%E6%89%8B%E9%A0%86
		final String TAG = "setNend";
		String dbMsg="";
		try {
			if(nendAdView == null){
				int nend_spotID = Integer.parseInt(getString (R.string.nend_spotID));
				dbMsg += ",nend_spotID=" + nend_spotID ;
				String nend_apiKey = getString (R.string.nend_apiKey);
				dbMsg += ",nend_apiKey=" + nend_apiKey ;
				float scale = getResources().getDisplayMetrics().density;
				dbMsg += ",scale=" + scale;
				int layoutWidth = (int)(320*scale)/2;		//(nend_layout.getWidth());                  //advertisement_ll ;ll object reference
				int layoutHeight = (int)(50*scale)/4;		//(nend_layout.getHeight());	//50;		//(int)(layoutWidth* 50 / 320);	//
				dbMsg += "[" + layoutWidth + "×" + layoutHeight + "]";
				float ratio = layoutWidth / (320.0f * scale);
				dbMsg += ",ratio=" + ratio;
//				layoutHeight =(int)(50*scale)/2;
				layoutHeight =(int)(50*ratio);
				dbMsg += ">>[" + layoutWidth + "×" + layoutHeight + "]";
				nendAdView = new NendAdView(this,nend_spotID, nend_apiKey);    							// 1 NendAdView をインスタンス化   ,第3引数:nendsdk:NendAdjustSize="true"相当  画面幅いっぱいに広がる
				nend_layout.addView(nendAdView, new LinearLayout.LayoutParams( layoutWidth, layoutHeight));	 	// 2 NendAdView をレイアウトに追加
	//			nend_layout.addView(nendAdView);

				nendAdView.loadAd();     		// 3 広告の取得を開始

				nendAdView.setListener(new NendAdInformationListener() {
					@Override
					public void onFailedToReceiveAd(NendAdView adView) {
						final String TAG = "onFailedToReceiveAd";
						String dbMsg="";
						try {
							dbMsg += "広告取得失敗";
							NendAdView.NendError nendError = adView.getNendError();
							switch (nendError) {
								case INVALID_RESPONSE_TYPE:
									dbMsg += "不明な広告ビュータイプ";
									break;
								case FAILED_AD_DOWNLOAD:
									dbMsg += "広告画像の取得失敗";
									break;
								case FAILED_AD_REQUEST:
									dbMsg += "広告取得失敗";
									break;
								case AD_SIZE_TOO_LARGE:
									dbMsg += "広告サイズがディスプレイサイズよりも大きい";
									break;
								case AD_SIZE_DIFFERENCES:
									dbMsg += "リクエストしたサイズと取得したサイズが異なる";
									break;
							}
							nend_layout.setVisibility (View.GONE);
							myLog(TAG, dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG ,  dbMsg + "で" + e);
						}
					}

					@Override
					public void onReceiveAd(NendAdView adView) {
						final String TAG = "[nendAdView]";
						String dbMsg = "onReceiveAd;";
						try {
							dbMsg += "広告取得成功";
							nend_layout.setVisibility (View.VISIBLE);
							myLog(TAG, dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG ,  dbMsg + "で" + e);
						}
					}

					@Override
					public void onClick(NendAdView adView) {
						final String TAG = "[nendAdView]";
						String dbMsg = "onClick;";
						try {
							dbMsg += "クリック成功";
							myLog(TAG, dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG ,  dbMsg + "で" + e);
						}
					}

					@Override
					public void onDismissScreen(NendAdView adView) {
						final String TAG = "[nendAdView]";
						String dbMsg = "onDismissScreen;";
						try {
							dbMsg += "復帰成功";
							nend_layout.setVisibility (View.VISIBLE);
							myLog(TAG, dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG ,  dbMsg + "で" + e);
						}
					}

					@Override
					public void onInformationButtonClick(NendAdView adView) {
						final String TAG = "onInformationButtonClick";
						String dbMsg="";
						try {
							dbMsg += "Informationボタンクリック成功";
							myLog(TAG, dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG ,  dbMsg + "で" + e);
						}
					}
				});
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

///Suviceから値を受け取る////////////////////////////////////////////////

//①起動動作///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 起動直後にonWindowFocusChangedが発生した時(フォーカスが当たった場合)
	 * ノティフィケーションからの起動された場合はkeizoku2ServiceでMusicPlayerServiceのプロパティ取得
	 * 通常起動時はmData2Serviceからサービスを起動
	 * */
	@SuppressLint("InlinedApi")
	public void aSetei(){													//①ⅲフォーカス取得後のActibty設定   ;再生ポジション操作
		final String TAG = "aSetei";
		String dbMsg="";
		try{
			saitaiFont = artist_tv.getTextSize();
	//		plistDPTF.setText( nowList );		//プレイリスト	全曲リスト</string>
			if(ruikei_titol != null){
				dbMsg +=  "全タイトルカウント=" + ruikei_titol;/////////////////////////////////////////////////////////////////////////////////////////////////////////
				titolAllPTF.setText( ruikei_titol );													//全タイトルカウント
			}
			dbMsg += "前回の累積=" + zenkai_saiseKyoku +"曲";/////////////////////////////////////////////////////////////////////////////////////////////////////////
			if(zenkai_saiseKyoku >0){
	//			zennkaiLL.setVisibility(View.VISIBLE);							//前回の累積レイアウト
				zenkai_ruikei_suu_tv.setText(String.valueOf(zenkai_saiseKyoku));								//前回の累積曲数
				dbMsg += "/" + zenkai_saiseijikann + "[ms]";/////////////////////////////////////////////////////////////////////////////////////////////////////////
		//		zenkai_ruikei_tv.setText(ORGUT.sdf_hms.format(zenkai_saiseijikann));						//前回の再生時間
				Date wrDate = new Date(zenkai_saiseijikann);
				dbMsg += ">Date>" + wrDate;/////////////////////////////////////////////////////////////////////////////////////////////////////////
				String convertedDateStr = convertFormat.format(wrDate);
				dbMsg += ">format>" + convertedDateStr +"S";/////////////////////////////////////////////////////////////////////////////////////////////////////////
				zenkai_ruikei_tv.setText(convertedDateStr);						//前回の再生時間
//			} else {
//				zennkaiLL.setVisibility(View.GONE);							//前回の累積レイアウト
			}
			ruikei_kyoku_tv.setText(String.valueOf(0));	//累積曲数
			Date wrDate = new Date(0);
			dbMsg += ">Date>" + wrDate;/////////////////////////////////////////////////////////////////////////////////////////////////////////
			String convertedDateStr = convertFormat.format(wrDate);
			dbMsg += ">format>" + convertedDateStr;/////////////////////////////////////////////////////////////////////////////////////////////////////////
			ruikei_jikan_tv.setText(convertedDateStr);						//累積時間
	//		myLog(TAG,dbMsg);
			long end=System.currentTimeMillis();		// 終了時刻の取得
			dbMsg=(int)((end - start)) + "mS";		//	<string name="">所要時間</string>
			rContext = MaraSonActivity.this;
			CharSequence btStre = ppPBT.getContentDescription();
			dbMsg +=",ppPBT="+btStre;/////////////////////////////////////
			if( btStre == null ){
				ppPBT.setContentDescription(getResources().getText(R.string.pause));			//処理後はポーズ何か設定しておかないと
			}
			dbMsg +=",起動="+kidou_jyoukyou;/////////////////////////////////////
			if( kidou_jyoukyou == kidou_notif  ){				//ノティフィケーションからの起動
				keizoku2Service();										//MusicPlayerServiceのプロパティ取得
			}else {
//				dbMsg +=",reqCode="+reqCode;/////////////////////////////////////
		//		int mIndex = getPrefInt("pref_mIndex" , 0 , MaraSonActivity.this);
				dbMsg +=",mIndex="+ mIndex + ">>mData2Service";/////////////////////////////////////
//				mData2Service(mIndex);										//サービスにプレイヤー画面のデータを送る
			}
			switch(visualizerType) {
			case Visualizer_type_wave:			//Visualizerはwave表示
			case Visualizer_type_FFT:			//VisualizerはFFT
				makeVisualizer(  );					//VisualizerのView作成
				break;
			case Visualizer_type_none:			//Visualizerを使わない
				break;
//				default:
//					break;
			}
//			pp_vf.setOnTouchListener(new OnTouchListener() {			//http://google-os.blog.jp/archives/50663546.html
//				@Override
//				public boolean onTouch(View v, MotionEvent event) {
//					final String TAG = "aSetei[MaraSonActivity]";
//					String dbMsg="開始";/////////////////////////////////////
//					try{
//		//				MyImageView view = (MyImageView)viewFlipper.getCurrentView();						// MyImageViewのScaleGestureDetectorにイベントを渡す.
////						if(view.onTouchEvent(event) == true) {
////							return true;
////						}
//					} catch (Exception e) {
//						myErrorLog(TAG,dbMsg+"で"+e);
//					}
//					return gesDetect.onTouchEvent(event);			// 表示されているMyImageViewを取得 GestureDetectorにイベントを渡す.
//				}
//			});

			pp_bt_ll.setVisibility(View.GONE);			//フィールドを非表示
			btHandler = new Handler();
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	private void keizoku2Service(){						//MusicPlayerServiceのプロパティ取得
		final String TAG = "keizoku2Service";
		String dbMsg= "";/////////////////////////////////////
		try{
//			b_dataFN ="";
//			dbMsg +=dataFN;////////////////////////////////////this.data = data;
//			dbMsg +="[" + ORGUT.sdf_mss.format(mcPosition) + "/" + ORGUT.sdf_mss.format(saiseiJikan) + "]"  ;/////////////////////////////////////
			dbMsg +=",MPSIntent=" + MPSIntent;/////////////////////////////////////
			MPSIntent = new Intent(getApplication(),MusicPlayerService.class);	//parsonalPBook.thisではメモリーリークが起こる
			dbMsg +=">>" + MPSIntent;/////////////////////////////////////
			MPSIntent.setAction(MusicPlayerService.ACTION_KEIZOKU);	//追加2	；
			dbMsg +=" ,getAction=" + MPSIntent.getAction();/////////////////////////////////////
			dbMsg +=",プレイリストID=" + nowList_id;/////////////////////////////////////
			MPSIntent.putExtra("nowList_id",nowList_id);
			dbMsg +=",再生中のプレイリスト名=" + nowList;/////////////////////////////////////
			MPSIntent.putExtra("nowList",nowList);
//			MPSIntent.putExtra("dataFN",dataFN);
			int mcPosition = saiseiSeekMP.getProgress();
			int saiseiJikan = saiseiSeekMP.getMax();
			dbMsg +=","+ mcPosition + "/" +saiseiJikan;/////////////////////////////////////
			MPSIntent.putExtra("mcPosition",mcPosition);	//再生ポジション
			MPSIntent.putExtra("saiseiJikan",saiseiJikan);
			MPSName = startService(MPSIntent);
			dbMsg +=" ,ComponentName=" + MPSName;/////////////////////////////////////
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

/**
 * サービスにプレイヤー画面のデータを送る
 *呼出し元	起動直後にaSetei 、onActivityResultでリストから戻ってきた時
 * */
//	private void mData2Service(int mIndex){										//①ⅳサービスにプレイヤー画面のデータを送る	＜aSetei, , onStopTrackingTouch , preparBody[MaraSonActivity]
//		final String TAG = "mData2Service";
//		String dbMsg= "";
//		try{
//			dbMsg += "MPSIntent=" + MPSIntent;/////////////////////////////////////
//			if(MPSIntent == null){
//				MPSIntent = new Intent(getApplication(),MusicPlayerService.class);	//parsonalPBook.thisではメモリーリークが起こる
//				dbMsg +=">>" + MPSIntent;
//			}
//			String dataFN = getPrefStr( "pref_data_url" ,"" , MaraSonActivity.this);
//			dbMsg += "[" + nowList_id + "]=" + nowList;/////////////////////////////////////
//			MPSIntent.putExtra("nowList_id",nowList_id);
//			if(nowList == null){
//				nowList = getPLName( nowList_id);			//プレイリスト名を返す
//				dbMsg += ">>" + nowList;/////////////////////////////////////
//			}
//			MPSIntent.putExtra("nowList",nowList);
//			dbMsg +="[play_order=ID=mIndex=" + mIndex;/////////////////////////////////////
//			MPSIntent.putExtra("mIndex",mIndex);
//			dbMsg +=",toPlaying=" + toPlaying;/////////////////////////////////////
//			MPSIntent.putExtra("continu_status",toPlaying);
//			dbMsg += ","+ n_dataFN;/////////////////////////////////////
//			if( n_dataFN != null ){				//n_dataFN
//				MPSIntent.putExtra("dataFN",n_dataFN);	//n_dataFN
//			} else {
//				if(dataFN != null){          //        &&  pref_data_url.equals("")
//					MPSIntent.putExtra("dataFN",dataFN);
//				}
//				MPSIntent.setAction(MusicPlayerService.ACTION_REQUEST_STATE);	//起動時の表示
//			}
//
////			dbMsg += "[再生ポジション=" + mcPosition;/////////////////////////////////////
////			//	MPSIntent.setAction(MusicPlayerService.ACTION_LISTSEL);
//////			if( rp_pp ){
//////				MPSIntent.setAction(MusicPlayerService.ACTION_STOP);		//音が出てしまう
//////			}
//////			dbMsg +="[再生ポジション=" + mcPosition;/////////////////////////////////////
////			MPSIntent.putExtra("mcPosition",mcPosition);	//再生ポジション
//			dbMsg +="/" + saiseiJikan +"mS]";/////////////////////////////////////
//			if(0 < saiseiJikan){
//				MPSIntent.putExtra("saiseiJikan",saiseiJikan);	//DURATION;
//			}
//
//			MPSIntent.putExtra("rp_pp",rp_pp);					//2点間リピート中
//			MPSIntent.putExtra("pp_start",pp_start);			//リピート区間開始点
//			MPSIntent.putExtra("pp_end",pp_end);				//リピート区間終了点
//			MPSIntent.putExtra("tugiList_id",pref_zenkyoku_list_id );
//			MPSIntent.putExtra("tugiList","");
//
//			dbMsg +=" ,getAction=" + MPSIntent.getAction();/////////////////////////////////////
//			MPSName = startService(MPSIntent);
//			dbMsg +=" ,ComponentName=" + MPSName;/////////////////////////////////////
//			myLog(TAG, dbMsg);
//		} catch (Exception e) {
//			myErrorLog(TAG ,  dbMsg + "で" + e);
//		}
//	}

	/**プレイリスト名を返す*/
	@SuppressLint("Range")
	public String getPLName(int nowList_id){
		String retStr = null;
		final String TAG = "getPLName[MaraSonActivity]";
		String dbMsg = "[MaraSonActivity];プレイリスト名を返す;";
		try{
			dbMsg += "nowList_id =" + nowList_id ;
			if(0 < nowList_id  ){
				Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
				String[] columns = null;				//{ idKey, nameKey };
				String c_selection = MediaStore.Audio.Playlists._ID;
				String[] c_selectionArgs = {String.valueOf(nowList_id)};
				Cursor playLists = getApplicationContext().getContentResolver().query(uri, columns, c_selection  , c_selectionArgs , null);
				dbMsg +="," + playLists +"件" ;
				if(playLists.moveToFirst()){
					retStr = playLists.getString(playLists.getColumnIndex("name"));
					dbMsg +=",name=" + retStr ;
					nowList_data = playLists.getString(playLists.getColumnIndex("_data"));
					dbMsg +=",_data=" + nowList_data ;
				}else{
					retStr = getString(R.string.listmei_zemkyoku);			//全曲リスト名
				}
				playLists.close();
			}else{
				retStr = getString(R.string.listmei_zemkyoku);			//全曲リスト名
			}
			dbMsg +=",name=" + retStr ;
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return retStr;
	}

///②サービスで稼働している情報をActivtyに書き込む/////////////////////////////////////////////////①起動動作
	/**
	 * 送信元はMusicPlayerService.sendPlayerStateと	changeCount	および	onHandleIntent
	 *
	 * ここでBuletoothなど他のブロードキャストは受け付けない
	 * */
	public class MusicReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(final Context context, final Intent intent) {
			mHandler.post(new Runnable() {
				public void run() {
					final String TAG = "MusicReceiver";
					String dbMsg="";
					try{
						String rStr = null;
						boolean thisCont = true;
						boolean gamenKakikae = false;
						//逐次更新*/
						IsPlaying = intent.getBooleanExtra("IsPlaying", false);			//再生中か
						dbMsg +=  ",再生中= "+ IsPlaying  + "(b_Playing= "+ b_Playing + ")";/////////////////////////////////////
					//	int mcPosition = getPrefInt("pref_position" , 0, context);		//sharedPref.getInt("pref_position" , 0);
						int rInt = intent.getIntExtra("mcPosition" , 0);        //DURATION;継続;The duration of the audio file, in ms;Type: INTEGER (long)
						if(0 < rInt){
							mcPosition = rInt;
						}
						dbMsg += ",mcPosition=" + mcPosition + "/" + saiseiJikan + "[ms]";
						if(IsPlaying){
							pointKousin(mcPosition);	//再生ポイント更新							//////////http://www.atmarkit.co.jp/ait/articles/1202/16/news130.html	①～　　はクリックした順番
							if (! ppPBT.getContentDescription().equals(getResources().getText(R.string.play))) {
								dbMsg += ",Description修正＞＞Play";
								ppPBT.setImageResource(R.drawable.pousebtn);
								ppPBT.setContentDescription(getResources().getText(R.string.play));
								myLog(TAG, dbMsg);
							}
						}else{
							if (! ppPBT.getContentDescription().equals(getResources().getText(R.string.pause))) {
								dbMsg += ",Description修正＞＞Pouse";
								ppPBT.setImageResource(R.drawable.pl_r_btn);
								ppPBT.setContentDescription(getResources().getText(R.string.pause));
								myLog(TAG, dbMsg);
							}
						}
//						IsSeisei = intent.getBooleanExtra("IsSeisei", false);			//生成中
//						dbMsg +=  ",生成中= "+ IsSeisei;/////////////////////////////////////
//						String state = intent.getStringExtra("state") + "";
//						if(! state.equals("") ){
//							dbMsg +=",state=" + state;
//							if( ! b_state.equals(state) ){
//								dbMsg +=",b_state=" + b_state;
//								b_state = state;
//								if(state.equals( MusicPlayerService.State.Playing )){
//									ppPBT.setImageResource(R.drawable.pl_r_btn);
//									ppPBT.setContentDescription(getResources().getText(R.string.pause));			//play
//									IsPlaying = false;
//								}else if(state.equals( MusicPlayerService.State.Paused ) || state.equals( MusicPlayerService.State.Stopped )){
//									ppPBT.setImageResource(R.drawable.pousebtn);
//									ppPBT.setContentDescription(getResources().getText(R.string.play));			//pause
//									IsPlaying = true;
//								}
//								dbMsg += " 、Description="+ ppPBT.getContentDescription();/////////////////////////////////////
//								rInt = intent.getIntExtra("imanoJyoutai",0 );		//DURATION;継続;The duration of the audio file, in ms;Type: INTEGER (long)
//								dbMsg +=  ",今の状態="+rInt ;/////////////////////////////////////リストの状態	起動直後；veiwPlayer / 再選択chyangeSong
//								if( rInt != 0 ){									//MuList.sonomama
//									imanoJyoutai = rInt;
//									if(rInt == quit_all){
//										quitMe();		//このアプリを終了する
//										thisCont = false;
//									}
//								}
//								dbMsg +=  ">>"+imanoJyoutai ;/////////////////////////////////////リストの状態	起動直後；veiwPlayer / 再選択chyangeSong
//							}
//						}
						String dataFN =intent.getStringExtra("data") + "";
						dbMsg +=  "以前のファイル=" + b_dataFN +  ">>再生中のファイル名=" + dataFN;
						if( dataFN.equals("") || dataFN==null) {
						}else{
							mIndex = intent.getIntExtra("mIndex", 0);
							dbMsg += "[mIndex=" + mIndex;
							listEnd = intent.getIntExtra("listEnd", 9999);
							dbMsg += "/" + listEnd + "]";
							if(! dataFN.equals(b_dataFN) ){			//曲が変わったら || nowList_id != b_List_id           && ! dataFN.equals("") )         || ! b_titolName.equals(titolName)
								b_dataFN = dataFN;
								dbMsg += ">曲ごとの更新";
								saiseiJikan = getPrefInt("pref_duration" , 0, context);		//sharedPref.getInt("pref_duration" , 0);
								dbMsg += ",mcPosition=" + mcPosition + "/" + saiseiJikan + "[ms]";
								setSeekMax( saiseiJikan );
								b_index = Integer.valueOf( String.valueOf(songIDPTF.getText() ));				//前の曲順
								t_index = Integer.valueOf( String.valueOf(titolAllPTF.getText() ));				//そのリストの曲数
								dbMsg += "[b_index=" + b_index + "/" + t_index  + "]";
								if( b_index == t_index && 0< t_index && nowList.equals( getResources().getString(R.string.playlist_namae_randam) )){
									randumPlay();			//ランダム再生
									songIDPTF.setText("1");
								}else{
									albumArt=intent.getStringExtra("albumArt");
									dbMsg +=",albumArt=" + albumArt;
									gamenKakikae = true;
								}
								b_List_id = nowList_id;			//前のプレイリストID
								nowList_id = intent.getIntExtra("nowList_id", b_List_id);
								dbMsg += "[List_id=" + b_List_id + "→" + nowList_id;
								rStr = intent.getStringExtra("nowList");
								dbMsg +="]" + rStr;
								if( rStr != null ){					//|| plHenkou
									nowList = rStr;
									dbMsg +=">>" + nowList;
									if( ! toolbar.getTitle().equals(rStr) ){					//					getActionBar().getTitle().equals(rStr)					getActionBar().setTitle(nowList);
										nowList = rStr;
										dbMsg += ">>" + nowList;/////////////////////////////////////
										toolbar.setTitle(nowList);
										gamenKakikae = true;
									}
								}
//								b_titolName =String.valueOf(MaraSonActivity.this.titol_tv.getText());			//前曲名
//								dbMsg += "、前の曲名；"+ b_titolName + " を　";
//								rStr =intent.getStringExtra("titolName");
//								dbMsg +=",titolName=" + rStr;
//								if( rStr != null ){
//									titolName = rStr;
//								}else{
//									titolName = b_titolName;
//								}
//
//								b_albumName =String.valueOf(MaraSonActivity.this.alubum_tv.getText());
//								dbMsg += "、前のアルバム；"+ b_albumName + " を　";
//								rStr =intent.getStringExtra("albumName");
//								dbMsg +=",albumName=" + rStr;
//								if( rStr != null ){
//									albumName = rStr;
//								}else{
//									albumName = b_albumName;
//								}
//								albumArtist =intent.getStringExtra("albumArtist");
//								creditArtistName =intent.getStringExtra("creditArtistName");
//								dbMsg +=" ,クレジット⁼ " + creditArtistName;
								url2FSet(dataFN);		//urlからプレイヤーの書き込みを行う		albumArtist

								if( mVisualizer != null ){
									mVisualizer.release();
									mVisualizer = null;
								}
								dbMsg +=",isShown=" + lyric_tv.isShown();		//常に0
								if(lyric_tv.isShown()){
									readLyric( dataFN );					//歌詞の読出し
								}
//								saiseiJikan = intent.getIntExtra("saiseiJikan", 0);		//DURATION;継続;The duration of the audio file, in ms;Type: INTEGER (long)
								dbMsg += ",IsPlaying=" + IsPlaying;
								if( IsPlaying ){											//起動時
									dbMsg += ",mVisualizer=" + mVisualizer;
									if( mVisualizer == null){
										mVisualizer = (Visualizer) intent.getExtras().get("mVisualizer");
										switch(visualizerType) {
											case Visualizer_type_wave:			//Visualizerはwave表示
											case Visualizer_type_FFT:			//VisualizerはFFT
												setupVisualizerFxAndUI();				//Visualizerの設定
												break;
											case Visualizer_type_none:			//Visualizerを使わない
												break;
											//												default:
											//													break;
										}
									}
								}
////								gamenKakikae = true;
//								if(gamenKakikae){
//									url2FSet(dataFN);		//urlからプレイヤーの書き込みを行う		albumArtist
//								}
								if (thisCont) {
									int retInt = intent.getIntExtra("ruikeikyoku" , 0);
									dbMsg +=",累積曲数=" + retInt;
									if( retInt >0 ){
										ruikeikyoku = retInt;
										dbMsg +=">>" + ruikeikyoku;
										ruikeiSTTime = intent.getLongExtra("ruikeiSTTime", 0);		//累積時間
										dbMsg +=",累積時間=" +ruikeiSTTime;
										rusekiKousin();														//再生ポイント更新
									}
								}
								myLog(TAG, dbMsg);
							}
						}

						String bLyric = songLyric;
						if( bLyric == null){
							bLyric ="";
						}
						songLyric = intent.getStringExtra("songLyric");
						if(songLyric != null){
							boolean samelyric = songLyric.equals(bLyric);
							dbMsg += ",samelyric=" + samelyric;
							if(! samelyric){
								lyric_tv.setText(songLyric);					//歌詞表示
							}
						}
					} catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}
			});
		}
	}

//http://blog.justoneplanet.info/2011/12/14/android%E3%81%A7notification%E3%82%92%E4%BD%BF%E3%81%86/
	public void setSeekMax(int saiseiJikan ){		//     ,
		final String TAG = "setSeekMax";
		String dbMsg= "";
		try{
			dbMsg +="saiseiJikan =" + saiseiJikan;
			totalTimePTF.setText(ORGUT.sdf_mss.format(saiseiJikan));
			saiseiSeekMP.setMax(saiseiJikan);		//DURATION;
			nowVol = audioManage.getStreamVolume(AudioManager.STREAM_MUSIC);			//現在の音楽音量
			dbMsg +=">>" + nowVol;//0～15/////////////////////////////
			vol_tf.setText(String.valueOf(nowVol));//音量表示
	//							myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}
	/**
	 * 再生ポイント更新
	 * 呼出し元  MusicReceiver
	 * */
	public void pointKousin(int mcPosition ){		//     , int saiseiJikan
		final String TAG = "pointKousin";
		String dbMsg= "";
//			new Thread(new Runnable() {				//ワーカースレッドの生成
//				public void run() {
//					String dbMsg= "thread id = " + Thread.currentThread().getId();/////////////////////////////////////
//					try {
//						Thread.sleep(1); // 1ms秒待つ
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					runOnUiThread(new Runnable() {			//メインスレッド以外からのUI更新
//						/*☆メインスレッドにポストする-
//						 *メインスレッドから呼ばれた場合、渡されたアクションをすぐに実行
//						 *メインスレッド以外から呼ばれた場合、渡されたアクションをメインスレッドのイベントキューにポスト*/
//						public void run() {
//							final String TAG = "runOnUiThread[pointKousin]";
//							String dbMsg= "開始";/////////////////////////////////////
		try{
			dbMsg +="mcPosition =" + mcPosition;
			int saiseiJikan = saiseiSeekMP.getMax();
			dbMsg +="/ " + saiseiJikan;
			if( saiseiJikan < mcPosition ){
				saiseiJikan = getPrefInt("pref_duration" , 0, MaraSonActivity.this);		//sharedPref.getInt("pref_duration" , 0);
				dbMsg +=">再生時間>" +saiseiJikan;
				if( saiseiJikan < mcPosition ){
					return;
				}else{
					setSeekMax(saiseiJikan);		//DURATION;
				}
			}
			dbMsg +="[再生ポジション=" + mcPosition + "/";
			saiseiSeekMP.setProgress(mcPosition);
			saiseiPositionPTF.setText(ORGUT.sdf_mss.format(mcPosition));	   		//再生画面の経過時間枠
			nowVol = audioManage.getStreamVolume(AudioManager.STREAM_MUSIC);			//現在の音楽音量
			dbMsg +=">>" + nowVol;//0～15/////////////////////////////
			vol_tf.setText(String.valueOf(nowVol));//音量表示

//							myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
//						}
//					});
//				}
//			}).start();
	}

	/**レシーバを作り直す*/
	public void rusekiKousin(){		//累積更新;累積曲数が発生する一瞬で書き換える
		receiverHaki();		//レシーバーを破棄	//☆毎回破棄しないとChoreographer: Skipped ○○」 frames!  The application may be doing too much work on its main thread.
		receiverSeisei();		//レシーバーを生成

		new Thread(new Runnable() {				//ワーカースレッドの生成
			public void run() {
				try {
					Thread.sleep(50); // 50ms秒待つ
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				/*Viewのpostメソッドを利用*/
				ruikei_kyoku_tv.post(new Runnable() {
					public void run() {
						final String TAG = "post";
						String dbMsg= "[ruikei_kyoku_tv]";
						try{
							dbMsg= "post thread id = " + Thread.currentThread().getId();
							dbMsg +=",累積曲数=" + ruikeikyoku;
							ruikei_kyoku_tv.setText(String.valueOf(ruikeikyoku));	//累積曲数
							myLog(TAG, dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG ,  dbMsg + "で" + e);
						}
					}
				});
				ruikei_jikan_tv.post(new Runnable() {
					@SuppressWarnings("deprecation")
					public void run() {
						final String TAG = "post[ruikei_jikan_tv]";
						String dbMsg= "開始";/////////////////////////////////////
						try{
							dbMsg= "post thread id = " + Thread.currentThread().getId();
							dbMsg += "累計" + ruikeiSTTime;/////////////////////////////////////////////////////////////////////////////////////////////////////////
							Date wrDate = new Date(ruikeiSTTime);
							dbMsg += ">Date>" + wrDate;/////////////////////////////////////////////////////////////////////////////////////////////////////////
							String convertedDateStr = convertFormat.format(wrDate);
							dbMsg += ">format>" + convertedDateStr + "S";/////////////////////////////////////////////////////////////////////////////////////////////////////////
							ruikei_jikan_tv.setText(convertedDateStr);						//前回の再生時間

////								int jisa = TimeZone.getDefault().getRawOffset();
////								dbMsg +="(時差" +jisa  ;
////								Date jisaDate = new Date(jisa);
////								dbMsg +="＞" +jisaDate  ;
////								long jisaLomg = jisaDate.getTime();
////								dbMsg +="＞" +jisaLomg  ;
//								dbMsg +=",累積時間=" +ruikeiSTTime ;
////								Date ruikeiDate = new Date(ruikeiSTTime);
////								dbMsg +=",ruikeiDate=" +ruikeiDate.getTime() ;
////								int jiInt =ruikeiDate.getHours();
////								dbMsg +="、" +jiInt +"時" ;
////								dbMsg +="）" +jiInt +"時" ;
////								int minInt = ruikeiDate.getMinutes();
////								dbMsg += minInt +"分" ;
////								int secInt = ruikeiDate.getSeconds();
////								dbMsg += minInt +"秒" ;
//			//					ruikei_jikan_tv.setText(diffTimeStr);						//累積時間	-(9*60*60*1000)
//							ruikei_jikan_tv.setText(String.valueOf(ORGUT.sdf_hms.format(ruikeiSTTime)));						//累積時間	-(9*60*60*1000)
							myLog(TAG, dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG ,  dbMsg + "で" + e);
						}
					}
				});
			}
		}).start();
	}

	/**
	 * レシーバーを生成
	 * 呼出し元は	onCreate , mData2Service	rusekiKousin	//onResume , playing 			keizoku2Service	onStopTrackingTouch	onKey
	 * ☆サービスからサービスは呼び出せないのでこのアクティビティから呼び出す。
	 * */
	public void receiverSeisei(){		//レシーバーを生成 <onResume , playing , mData2Service
		final String TAG = "receiverSeisei";
		String dbMsg= "";
		try{
			dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			dbMsg += ",mReceiver=" + mReceiver;////////////////////////
			if( mReceiver== null ){
				mFilter = new IntentFilter();
				mFilter.addAction(MusicPlayerService.ACTION_STATE_CHANGED);
				mReceiver = new MusicReceiver();
				registerReceiver(mReceiver, mFilter);
				dbMsg +=">生成>=" + mReceiver;////////////////////////
			}
//			dbMsg +="、btReceiver=" + btReceiver;///////////////////////
//			dbMsg +="、pref_bt_renkei=" + pref_bt_renkei;///////////////////////
//			if( btReceiver== null && pref_bt_renkei){			//& pref_bt_renkei ////
//				dbMsg +=",BluetoothAdapterは" + mBluetoothAdapter;/////////////////////////////////////
//				if(mBluetoothAdapter == null){
//					mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();			// Get local Bluetooth adapter
//					dbMsg +=">>" + mBluetoothAdapter;/////////////////////////////////////
//				}
//				IntentFilter btFilter = new IntentFilter();									//BluetoothA2dpのACTIONを保持する
//				btFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);			//20160414
//				btFilter.addAction(BluetoothDevice.ACTION_FOUND);							//20160414
//				btFilter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);					//20160414
//				btFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);			//20160414
//				btFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
//				btFilter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
//				btFilter.addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED);
//				btFilter.addAction(Intent.ACTION_MEDIA_BUTTON);							//操作ボタン対応？
//				dbMsg += ",btReceiver=" + btReceiver;////////////////////////
//				btReceiver = new BuletoohtReceiver();				//BuletoohtReceiver();
//		//		btReceiver.service = new MusicPlayerService();		//this
//				btReceiver.rContext = this;
//				registerReceiver(btReceiver, btFilter);
//				dbMsg +=">生成>=" + btReceiver;///////////////////////
//				btHandler = new Handler();
//			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * mReceivert,btReceiverが生成されていたら破棄する
	 * 呼出し元は	onCreate ,quitMe	 rusekiKousin		//onActivityResult , onStartTrackingTouch
	 * */
	@SuppressLint("MissingPermission")
	public void receiverHaki(){		//レシーバーを破棄
		final String TAG = "receiverHaki";
		String dbMsg= "";/////////////////////////////////////
		try{
			dbMsg += "mReceivert=" + mReceiver;
			if( mReceiver != null ){
				unregisterReceiver(mReceiver);
				mReceiver = null;
				dbMsg += ">>" + mReceiver;
			}
			dbMsg += ",btReceiver=" + btReceiver;////////////////////////
			if( btReceiver != null ){
				if(mBluetoothAdapter == null){
					mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();			// Get local Bluetooth adapter
				}
				mBluetoothAdapter.cancelDiscovery(); //検索キャンセル
				unregisterReceiver(btReceiver);
				btReceiver = null;
				dbMsg += ">>" + btReceiver;////////////////////////
			}
//			dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}


	public Handler handler = new Handler();
	public int audioId;
//	public List<String> plSL;					//プレイリスト用簡易リスト
//	/** 指定したプレイリストの内容取得 */
//	public void CreatePLList( long playlistId ){		//playlistIdで指定したMediaStore.Audio.Playlists.Membersの内容取得		String volumeName,
//		final String TAG = "CreatePLList";
//		String dbMsg = "";
//		try{
//			dbMsg += ORGUT.nowTime(true,true,true) + dbMsg;/////////////////////////////////////
//			long start = System.currentTimeMillis();		// 開始時刻の取得
//			dbMsg += "選択されたプレイリスト[ID="+playlistId + "]";
//			dbMsg += "[" + nowList_id+ "]" + nowList;
//
//			final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
//			final String[] columns = null;			//{ idKey, nameKey };
//			String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
//			Cursor rCursor= this.getContentResolver().query(uri, columns, null, null, c_orderBy );
////				}else{
////					treeAdapter = null;
////					Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
////					String[] columns =null;
////					String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
////					playLists = this.getContentResolver().query(uri, columns, null, null, c_orderBy );
////				}
//			dbMsg += ",rCursor=" + rCursor.getCount() + "件";
//			if( rCursor.moveToFirst() ){
//				reqCode = MyConstants.PUPRPOSE_lIST;
//				String pdTitol = getResources().getString(R.string.pref_playlist) +"" + getResources().getString(R.string.common_yomitori);				//読み込み
//				Util UTIL = new Util();
//				UTIL.dBaceColumnCheck(rCursor ,0);
//				plSL =  new ArrayList<String>();				//プレイリスト用簡易リスト
//				plSL.clear();
//
////20220826 読み込みループ　から
//
//			}
//			myLog(TAG, dbMsg);
//		}catch(IllegalArgumentException e){
//			myErrorLog(TAG ,  dbMsg + "で" + e);
//		}catch (Exception e) {
//			myErrorLog(TAG ,  dbMsg + "で" + e);
//		}
//	}

	/**
	 * urlからプレイヤーの書き込みを行う
	 * onReceive、onWindowFocusChanged後の処理
	 * */
	@SuppressLint("Range")
	public void url2FSet(String urlStr){		//②ⅱ；　起動時のプリファレンスから
		final String TAG = "url2FSet";
		String dbMsg= "";
		try{
			dbMsg += "検索対象；" + urlStr;
			ContentResolver resolver = getApplicationContext().getContentResolver();	//c.getContentResolver();
			Uri cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
			String[] c_columns =null;					//②引数tableには、テーブル名を指定します。
			String c_selection =  MediaStore.Audio.Media.DATA +" = ?";			//MediaStore.Audio.Albums.ARTIST +" LIKE ?"
			String c_orderBy=MediaStore.Audio.Media.ARTIST; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
			String[] c_selectionArgs= { String.valueOf(urlStr) };		//"%" + albumArtist + "%"
			Cursor cursor = resolver.query( cUri , c_columns , c_selection , c_selectionArgs, c_orderBy);
			dbMsg +=cursor.getCount() +"曲";/////////////////////////////////////
			//		myLog(TAG,dbMsg);
			if(cursor.moveToFirst()){
				@SuppressLint("Range") String tStr = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
				dbMsg +=tStr;/////////////////////////////////////
				creditArtistName=tStr;
				tStr = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
				dbMsg +=tStr;/////////////////////////////////////
				albumName=tStr;
				tStr = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
				dbMsg +=tStr;/////////////////////////////////////
				titolName=tStr;
				@SuppressLint("Range") int tInt = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
				dbMsg +=tInt;/////////////////////////////////////
				audioId=tInt;
				dbMsg +="、ジャケット写真=" + albumArt ;/////////////////////////////////////
				if ( albumArt ==null ) {
					albumArt =ORGUT.retAlbumArtUri( getApplicationContext() , creditArtistName , albumName );			//アルバムアートUriだけを返すalbumArtist		MaraSonActivity.this  ,
					dbMsg +=">>" + albumArt ;/////////////////////////////////////
				}
				if(handler == null){
					handler = new Handler();
				}
				handler.post(new Runnable() {
					public void run() {
						final String TAG = "url2FSet.run";
						String dbMsg="";
						try {
							dbMsg += "[nowList_id=" + nowList_id +"]F" + nowList ;
							toolbar.setTitle(nowList);
							dbMsg += "(mIndex=" + mIndex+")" ;
							songIDPTF.setText(String.valueOf((mIndex+1)));			//リスト中の何曲目か
							songIDTotal=listEnd;		//plSL.size();
							dbMsg +="/" + songIDTotal +"曲";
							titolAllPTF.setText(String.valueOf(songIDTotal));		//全タイトルカウント
							dbMsg +=" ,クレジット⁼ " + creditArtistName;
							artist_tv.setText(creditArtistName);
							dbMsg +=" , アルバム⁼" + albumName;/////////////////////////////////////	this.album = album;
							alubum_tv.setText(albumName);											//アルバム
							dbMsg +=" ,タイトル= " + titolName;/////////////////////////////////////		this.title = title;
							titol_tv.setText(titolName);					//タイトル

							dbMsg += "(mIndex=" + mIndex+")" ;
							songIDPTF.setText(String.valueOf((mIndex+1)));			//リスト中の何曲目か
							songIDTotal = listEnd;						//musicPlaylist.getUserListMaxPlayOrder(nowList_id);
							dbMsg +="/" + songIDTotal +"曲";
							titolAllPTF.setText(String.valueOf(songIDTotal));		//全タイトルカウント
							String[] items = urlStr.split(File.separator);
							albumArtist = items[items.length-3];
							dbMsg +=",albumArtist=" + albumArtist;
							artistID = artisIndex(albumArtist);								//アーティストリストの中でアルバムアーティスト名が何番目にあるか
							albumID = albumIndex(albumArtist , albumName);					//アルバムアーティスト名のアルバムリストの中でアルバムが何番目にあるか
							int rInt = titolIndex(albumArtist ,  albumName , titolName) + 1;				//タイトルリストの中でそのタイトルが何番目にあるか
							dbMsg +=",rInt=" + rInt + "曲目";
							nIDPTF.setText(String.valueOf(rInt));										//タイトルカウント

							dbMsg +=" ,audioId= " + audioId;
							if( b_albumArt == null ){
								b_albumArt = "";
							}
							if( ! b_albumArt.equals(albumArt)){
								MaraSonActivity.this.jakeSya( albumArt ,  mpJakeImg);		//相当するジャケット写真
								b_albumArt = albumArt;
								dbMsg +=">b_albumArt>" + b_albumArt ;/////////////////////////////////////
							}
							listBetuSettei( nowList );					//リスト毎のヘッダー部変更
							if( rp_pp ){			//2点間リピート中
								String pp_startStr = ORGUT.sdf_mss.format(MaraSonActivity.this.pp_start);				//二点間再生開始点(mmss000)
								dbMsg +=", " + pp_startStr;
								String pp_endStr = ORGUT.sdf_mss.format(MaraSonActivity.this.pp_end);			//二点間再生終了点(mmss000)
								dbMsg +="～" + pp_endStr + "のリピート";
								pp_pp_ll.setVisibility(View.VISIBLE);
								pp_pp_start_tf.setText(pp_startStr);				//二点間再生開始点
								pp_pp_end_tf.setText(pp_endStr);							//二点間再生終了点
								saiseiSeekMP.setSecondaryProgress(MaraSonActivity.this.pp_end);
								Drawable drawable = getResources().getDrawable(R.drawable.pp_progress);
								saiseiSeekMP.setProgressDrawable(drawable);
								saiseiSeekMP.setProgress(pp_start);
								//				mcPosition = pp_start;
							}else{
								pp_pp_ll.setVisibility(View.GONE);
								saiseiSeekMP.setSecondaryProgress(0);
								saiseiSeekMP.setProgressDrawable(dofoltSBDrawable);
							}
							lyric_tv.setText(songLyric);					//歌詞表示
						} catch (Exception e) {
							myErrorLog(TAG, dbMsg + "でエラー発生；"+e.toString());
						}
					}		//run
				});			//handler.post(new Runnable() {
			}
			cursor.close();

/*
			dbMsg += "、mIndex；" + mIndex;
			if(urlStr == null || urlStr.equals("") ||  urlStr.equals("null")) {
				urlStr = null;
				urlStr = getPrefStr( "pref_data_url" ,"" , MaraSonActivity.this);
				dbMsg += ">pref_data_url>" + urlStr;
				if(urlStr == null || urlStr.equals("")) {
					urlStr = getPrefStr( "dataURL" ,"" , MaraSonActivity.this);
					dbMsg += ">dataURL>" + urlStr;
					if(urlStr == null || urlStr.equals("")) {
						if( -1 < mIndex){
							dbMsg += ">mIndex>" + mIndex;
							urlStr = Item.getURI(MaraSonActivity.this , mIndex).toString();
						}
						dbMsg += ">dataURL>" + urlStr;
							if(urlStr == null || urlStr.equals("")) {
//							if(playingItem != null) {
//								urlStr = playingItem.data;
//								dbMsg += ">>" + urlStr;
//							}
						}
					}
				}
			}
			*/
//			if(urlStr == null || urlStr.equals("")) {
//			}else{
//				if( -1 < mIndex){
//				start = System.currentTimeMillis();		// 開始時刻の取得
//				dbMsg += "[nowList_id=" + nowList_id +"]F" + nowList ;
//				toolbar.setTitle(nowList);
//				mIndex = musicPlaylist.getPlaylistItemOrder(nowList_id,urlStr);
//				dbMsg += "(mIndex=" + mIndex+")" ;
//				songIDPTF.setText(String.valueOf((mIndex+1)));			//リスト中の何曲目か
//				songIDTotal = musicPlaylist.getUserListMaxPlayOrder(nowList_id);
//				dbMsg +="/" + songIDTotal +"曲";
//				titolAllPTF.setText(String.valueOf(songIDTotal));		//全タイトルカウント
//				Cursor playingItem = musicPlaylist.getPlaylistItems(nowList_id,mIndex);
//				creditArtistName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST));		//playingItem.artist;	//クレジットされているアーティスト名
//				dbMsg +=" ,クレジット⁼ " + creditArtistName;
//				artist_tv.setText(creditArtistName);
//				albumName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM));		//playingItem.album;			//アルバム名
//				dbMsg +=" , アルバム⁼" + albumName;/////////////////////////////////////	this.album = album;
//				alubum_tv.setText(albumName);											//アルバム
//				titolName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE));		//playingItem.title;		//曲名
//				dbMsg +=" ,タイトル= " + titolName;/////////////////////////////////////		this.title = title;
//				titol_tv.setText(titolName);					//タイトル
//				int audioId = Integer.parseInt(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)));
//				playingItem.close();
//				dbMsg +=" ,audioId= " + audioId;
//				albumArtist = musicPlaylist.getAlbumArtist(audioId , MaraSonActivity.this);	//アルバムアーティスト名
//				dbMsg +=" ,アルバムアーティスト= " + albumArtist;
//				dbMsg +="、ジャケット写真=" + albumArt ;/////////////////////////////////////
//				if ( albumArt ==null ) {
//					albumArt =ORGUT.retAlbumArtUri( getApplicationContext() , creditArtistName , albumName );			//アルバムアートUriだけを返すalbumArtist		MaraSonActivity.this  ,
//					dbMsg +=">>" + albumArt ;/////////////////////////////////////
//				}
//				if( b_albumArt == null ){
//					b_albumArt = "";
//				}
//				if( ! b_albumArt.equals(albumArt)){
//					MaraSonActivity.this.jakeSya( albumArt ,  mpJakeImg);		//相当するジャケット写真
//					b_albumArt = albumArt;
//					dbMsg +=">b_albumArt>" + b_albumArt ;/////////////////////////////////////
//				}
//				listBetuSettei( nowList );					//リスト毎のヘッダー部変更
//				if( rp_pp ){			//2点間リピート中
//					String pp_startStr = ORGUT.sdf_mss.format(MaraSonActivity.this.pp_start);				//二点間再生開始点(mmss000)
//					dbMsg +=", " + pp_startStr;
//					String pp_endStr = ORGUT.sdf_mss.format(MaraSonActivity.this.pp_end);			//二点間再生終了点(mmss000)
//					dbMsg +="～" + pp_endStr + "のリピート";
//					pp_pp_ll.setVisibility(View.VISIBLE);
//					pp_pp_start_tf.setText(pp_startStr);				//二点間再生開始点
//					pp_pp_end_tf.setText(pp_endStr);							//二点間再生終了点
//					saiseiSeekMP.setSecondaryProgress(MaraSonActivity.this.pp_end);
//					Drawable drawable = getResources().getDrawable(R.drawable.pp_progress);
//					saiseiSeekMP.setProgressDrawable(drawable);
//					saiseiSeekMP.setProgress(pp_start);
//	//				mcPosition = pp_start;
//				}else{
//					pp_pp_ll.setVisibility(View.GONE);
//					saiseiSeekMP.setSecondaryProgress(0);
//					saiseiSeekMP.setProgressDrawable(dofoltSBDrawable);
//				}
//				lyric_tv.setText(songLyric);					//歌詞表示
//			}
//			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void listBetuSettei(String listName){					//リスト毎のヘッダー部変更
		final String TAG = "listBetuSettei[MaraSonActivity]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			dbMsg= "listName=" + listName;
			if( ! listName.equals(getResources().getString(R.string.listmei_zemkyoku))){		// ;		// 全曲リスト</string>
				artistCountLL.setVisibility(View.GONE);				//アーティストカウント
				albumCountLL.setVisibility(View.GONE);					//アルバムカウント
				titolCountLL.setVisibility(View.GONE);					//タイトルカウント
			}else{
				artistCountLL.setVisibility(View.VISIBLE);				//アーティストカウント
				albumCountLL.setVisibility(View.VISIBLE);					//アルバムカウント
				titolCountLL.setVisibility(View.VISIBLE);					//タイトルカウント
				dbMsg +=",albumArtist=" + albumArtist + ",albumName=" + albumName + ",titolName=" + titolName;
				if(albumArtist == null){
					albumArtist = creditArtistName;
				}
				int rInt = titolIndex(albumArtist ,  albumName , titolName) + 1;				//タイトルリストの中でそのタイトルが何番目にあるか
				dbMsg +=",rInt=" + rInt + "曲目";
				nIDPTF.setText(String.valueOf(rInt));										//タイトルカウント
				dbMsg +=",b_album=" + b_album;
				if( b_album == null ){
					dbMsg +="<<⁼" + b_album +">";
					b_album = "";
				}else if( ! albumName.equals(b_album)){
					b_album = albumName;
					artistID = artisIndex(albumArtist);								//アーティストリストの中でアルバムアーティスト名が何番目にあるか
					dbMsg +=artistID + "人目" ;/////////////////////////////////////
					albumList =null;
					albumID = albumIndex(albumArtist , albumName);					//アルバムアーティスト名のアルバムリストの中でアルバムが何番目にあるか
					dbMsg +=albumID + "枚目" ;/////////////////////////////////////
					titolList = null;
				}
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public SQLiteDatabase retDB() {			//全曲dbを返す
		final String TAG = "retDB[MaraSonActivity]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			String fn = getString(R.string.zenkyoku_file);			//全曲リスト名
			dbMsg = "fn=" + fn;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
			zenkyokuHelper = new ZenkyokuHelper(MaraSonActivity.this , fn);		//全曲リストの定義ファイル		.
			File dbF = getDatabasePath(fn);			//Environment.getExternalStorageDirectory().getPath();		new File(fn);		//cContext.
			dbMsg += ",dbF=" + dbF;
			dbMsg += " , exists=" + dbF.exists() +" , canWrite=" + dbF.canWrite();
			if(dbF.exists() ){
				if(Zenkyoku_db != null){
					dbMsg += ">isOpen>" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
					if(! Zenkyoku_db.isOpen()){
						if(dbF.exists()){
							dbMsg += ">>delF=" + dbF.getPath();
							Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
							dbMsg += ">isOpen>" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
						}
					}
				} else {
					if(dbF.exists()){
						Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
						dbMsg += ">isOpen>" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
					}
				}
			}														//if(dbF.exists() ){
//			dbMsg = "検索するのは" + dataFN;
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return Zenkyoku_db;
	}

	public int rInt;
//	public int listYomikomi() throws IOException{			//①ⅵ２；Zenkyoku_dbを読み込む(db未作成時は-)	；	<jyoukyouBunki	zenFealdKaikomi
//		final String TAG = "listYomikomi[MaraSonActivity]";
//		String dbMsg="開始";/////////////////////////////////////
//		try{
//			if(dataFNList !=null){
//				dbMsg = dataFNList.size()+ "件から";
//				if(0 == dataFNList.size()){
//					if( dataFN != null){
//						dbMsg =dataFN +  "を検索";
//						if(dataFN.equals("")){
//							dbMsg += "空白";
//							preRead(syoki_Yomikomi , null);				//dataURIを読み込みながら欠けデータ確認		//		mediaSTkousinn();						//メディアストアの更新呼出し
//						}else{				//if(dataFN.equals("")){
//							dataFNList.indexOf(dataFN);
//
//							if( rInt !=0 ){
//							}
//						}					//if(dataFN.equals("")){
//					}						//if( dataFN != null){
//				}
//			}
//			myLog(TAG, dbMsg);
//		} catch (Exception e) {
//			myErrorLog(TAG ,  dbMsg + "で" + e);
//		}
//		return songIDTotal;
//	}

	public int artisIndex(String albumArtist){									//アーティストリストの中でアルバムアーティスト名が何番目にあるか
		final String TAG = "artisIndex";
		String dbMsg="";
		int artistID = 0;
		try{
			dbMsg +="artistList=" + artistList;
			if(artistList == null){
				artistIDTotal = artistList_yomikomi();									//アーティストリストを読み込む(db未作成時は-)
			}else{
				artistIDTotal = artistList.size();
			}
			dbMsg += "artist_db="+ artistIDTotal +"人";
			ruikei_artist = String.valueOf(artistIDTotal );    ///全アーティスト数
			dbMsg += ">"+ ruikei_artist ;
			artistTotalPTF.setText(ruikei_artist);							//アルバムアーティス合計
			dbMsg += "人中、"+ albumArtist +"は" ;
			if(artistIDTotal >0){
				artistID = artistList.indexOf(albumArtist) +1;				//再生中のアーティスト
				dbMsg +=">>" +artistID +"＞" ;
				String artistIDStr = String.valueOf(artistID );    ///全アーティスト数
				dbMsg +=">>" +artistIDStr +"番目" ;
				artistIDPTF.setText(String.valueOf(artistID ));	//アルバムアーティスカウント
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return artistID;
	}

	/**アーティストリストを読み込む(db未作成時は-)*/
	public int artistList_yomikomi() throws IOException{
		int retInt = -1;
		final String TAG = "artistList_yomikomi";
		String dbMsg="";/////////////////////////////////////
		try{
			artistTName = getString(R.string.artist_table);			//artist_table
			dbMsg += "；アーティストリストテーブル=" + artistTName;
			if( artist_db != null){
				if( artist_db.isOpen()){
					artist_db.close();
				}
			}
			String fn = getString(R.string.artist_file);			//アーティストリスト	artist_db.getPath();
			dbMsg = "db=" + fn;
			artistHelper = new ArtistHelper(this , fn);		//アーティスト名のリストの定義ファイル		.
			artist_db = artistHelper.getReadableDatabase();			// データベースをオープン
			boolean distinct = true;					//trueを指定すると検索結果から重複する行を削除します。
			String table =artistTName;			//テーブル名を指定します。
			String[] columns = null;			//{  "ALBUM_ARTIST" };				//検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
			String selections =null;				//検索条件を指定します。
			String[] selectionArgs =null;			//検索条件のパラメータ（？で指定）に置き換わる値を指定します。
			String groupBy = "ALBUM_ARTIST";						//groupBy句を指定します。
			String having =null;					//having句を指定します。
			String limit = null;					//検索結果の上限レコードを数を指定します。
			cursor = artist_db.query( distinct, table ,columns, selections,  selectionArgs,  groupBy,  having,  null,  limit) ;
			retInt = cursor.getCount();
			dbMsg += artistTName + "人";
			if(cursor.moveToFirst()){
				artistList = new ArrayList<String>();
				artistList.clear();
				ArrayList<String> compList = new ArrayList<String>();
				compList.clear();
				String comp = getResources().getString(R.string.comon_compilation);			//コンピレーション
				dbMsg += ",comp=" + comp;
				do{
					dbMsg = cursor.getPosition() + "/" + retInt + "件";
					@SuppressLint("Range") String rStr1 = cursor.getString(cursor.getColumnIndex("ALBUM_ARTIST"));
					dbMsg += rStr1;
					if( rStr1 != null ){
						if( rStr1.equals(comp) ){
							compList.add(rStr1);
						} else {
							artistList.add(rStr1);
						}
					}
				}while(cursor.moveToNext());
				dbMsg += compList;
				if( compList != null ){
					for( String cName : compList){
						dbMsg += "::" +cName;
						artistList.add(cName);
						dbMsg += ">>"+ artistList.size() + ")" + artistList.get(artistList.size()-1);
					}
					retInt = artistList.size();
					ruikei_artist = String.valueOf(artistList.size());
				}
				dbMsg += "://全アーティスト数"+ ruikei_artist +"人";
			}else{
				dbMsg ="dbは有るが読み出すデータが無い";/////////////////////////////////////
			}
			cursor.close();
			artist_db.close();
			myLog(TAG, dbMsg);
//		}catch (IOException e) {						//データベースの定義が古ければ
//			myErrorLog(TAG ,  dbMsg + "で" + e);
//			preRead(syoki_Yomikomi , null);				//dataURIを読み込みながら欠けデータ確認		//		mediaSTkousinn();						//メディアストアの更新呼出し
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return retInt;
	}

	/**アルバムアーティスト名のアルバムリストの中でアルバムが何番目にあるか*/
	public int albumIndex(String albumArtist , String albumMei){
		int albumID = -1;
		final String TAG = "albumIndex[MaraSonActivity]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg="albumArtist=" + albumArtist + ";albumMei=" + albumMei;
			dbMsg +="(前は" + b_album + "）";/////////////////////////////////////
			if(albumList== null ){								//起動時　if(albumList != null){
				albumList = new ArrayList<String>();
				albumList.clear();
				b_album = albumMei;
				alubumTotal = albumList_yomikomi(albumArtist , albumMei);									//アーティストリストを読み込む(db未作成時は-)
				albumID =albumIndex(albumArtist , albumMei);
			}else if(! albumMei.equals(b_album)){		//アルバムが変わっていたら
				dbMsg += ">albumList=" + albumList.size() + "件";
				dbMsg +="albumName=" + b_album + ">>" + albumMei;/////////////////////////////////////
		//		myLog(TAG,dbMsg);
				albumList = new ArrayList<String>();
				albumList.clear();
				b_album = albumMei;
				alubumTotal = albumList_yomikomi(albumArtist , albumMei);									//アーティストリストを読み込む(db未作成時は-)
				albumID =albumIndex(albumArtist , albumMei);
				titolList = null;
			}
			dbMsg += "アルバム" + albumID +"/" +albumList.size();
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return albumID;
	}

	public String artistMei ;
	public String albumMei;
	public int retInt = -1;

	/**アルバムトリストを読み込む(db未作成時は-)*/
	public int albumList_yomikomi(String artistMei  , String albumMei){
		this.retInt = -1;
		this.artistMei = artistMei;
		this.albumMei = albumMei;
		final String TAG = "albumList_yomikomi[MaraSonActivity]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			new Thread(new Runnable() {				//ワーカースレッドの生成
				public void run() {
		//			myLog(TAG, "in Thread button1 thread id = " + Thread.currentThread().getId());
					try {
						Thread.sleep(1); // 1ms秒待つ
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					runOnUiThread(new Runnable() {			//メインスレッド以外からのUI更新
						/*☆メインスレッドにポストする-
						 *メインスレッドから呼ばれた場合、渡されたアクションをすぐに実行
						 *メインスレッド以外から呼ばれた場合、渡されたアクションをメインスレッドのイベントキューにポスト*/
						public void run() {
							final String TAG = "runOnUiThread[albumList_yomikomi]";
							String dbMsg= "開始";/////////////////////////////////////
							try{
								dbMsg= "thread id = " + Thread.currentThread().getId();/////////////////////////////////////
					//			myLog(TAG, dbMsg);
				//				Toast.makeText(getApplicationContext(), "Click button1", Toast.LENGTH_SHORT).show();
								dbMsg=dbMsg+"artistMei=" +MaraSonActivity.this.artistMei ;/////////////////////////////////////
								getResources().getString(R.string.comon_compilation);
								Zenkyoku_db = retDB();			//全曲dbを返す
								String zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
								boolean distinct = false;					//trueを指定すると検索結果から重複する行を削除します。
								String c_selection = "ALBUM_ARTIST = ?";			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
								String[] c_selectionArgs0={MaraSonActivity.this.artistMei};			//⑥引数groupByには、groupBy句を指定します。 "'" + artistMei + "'"
								String groupBy =  "ALBUM_ARTIST , ALBUM";		//groupBy句を指定します。
								String having =null;					//having句を指定します。
								String c_orderBy= "LAST_YEAR"; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
								Cursor cursor = Zenkyoku_db.query(distinct,zenkyokuTName, null, c_selection, c_selectionArgs0 , groupBy, having, c_orderBy, null);	//1;table, 2; columns,new String[] {MotoN, albamN}
								alubumTotal = cursor.getCount();		//アルバム合計
								dbMsg +="で"+alubumTotal +"件";		//リストアップしたアルバムアーティスト名
								if(cursor.moveToFirst()){
									do{
										dbMsg= "[" + cursor.getPosition() +"/"+alubumTotal +"]";		//リストアップしたアルバムアーティスト名
										@SuppressLint("Range") String rAlbum = cursor.getString(cursor.getColumnIndex("ALBUM"));
										albumList.add( rAlbum );														//MediaStore.Audio.Albums.ARTIST
										dbMsg += albumList.get(albumList.size()-1);
						//				myLog(TAG,dbMsg);
									}while(cursor.moveToNext());
									alubumTotalPTF.setText(String.valueOf( alubumTotal ));		//アルバム合計
									albumID = albumList.indexOf(MaraSonActivity.this.albumMei) + 1;
									dbMsg +=albumID +"番目に" + MaraSonActivity.this.albumMei + "("+ albumArtist +")";
									albumIDPTF.setText(String.valueOf(albumID));			//アルバムカウント
									dbMsg += "アルバム" + albumID +"/" +albumList.size();
									MaraSonActivity.this.retInt = albumList.size();
									dbMsg = MaraSonActivity.this.artistMei + "のアルバム" + alubumTotal+ "(リスト；" + MaraSonActivity.this.retInt+ "件）";
								}
								cursor.close();
								Zenkyoku_db.close();
								myLog(TAG, dbMsg);
							} catch (Exception e) {
								myErrorLog(TAG ,  dbMsg + "で" + e);
							}
						}
					});
				}
			}).start();
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return MaraSonActivity.this.retInt;
	}

	public int titolIndex(String albumArtist , String albumMei , String titolMei){					//タイトルリストの中でそのタイトルが何番目にあるか
		titolID = 0;					//曲カウント
		final String TAG = "titolIndex[MaraSonActivity]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg="albumArtist=" + albumArtist;
			dbMsg=dbMsg+",albumMei=" + albumMei;
			dbMsg=dbMsg+",titolMei=" + titolMei;
			dbMsg=dbMsg+",titolList=" + titolList;
			if( titolList == null ){
				titolList = new ArrayList<String>();
				titolTotal = titolList_yomikomi(albumArtist , albumMei);									//アーティストリストを読み込む(db未作成時は-)
				titolID =titolIndex(albumArtist , albumMei , titolMei);
			}
			titolID =  titolList.size();
			dbMsg += ">" + titolID + "件";
			tIDPTF.setText(String.valueOf(titolID));			//タイトル合計
			titolID = titolList.indexOf(titolMei) ;
			dbMsg +=titolID +"番目に" + titolMei + "("+ albumMei +")";
			nIDPTF.setText(String.valueOf(titolID));
			dbMsg +=titolMei +"は" + titolID +"/" +titolList.size();
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return titolID;
	}

	/**
	 * 曲名リストを読み込み何曲目かを返す
	 * (db未作成時は-1)
	 * @param artistMei アーティスト名
	 * @param albumMei アルバム名
	 * 使用メソッド retDB();			//全曲dbを返す
	 */
	public int titolList_yomikomi(String artistMei , String albumMei)throws IOException{					//曲名リストを読み込む(db未作成時は-)
		int titolTotal = -1;				//曲合計
		final String TAG = "titolList_yomikomi[MaraSonActivity]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			this.artistMei = artistMei;
			dbMsg = artistMei ;
			this.albumMei = albumMei;
			dbMsg += "の" + albumMei;
//			new Thread(new Runnable() {				//ワーカースレッドの生成
//				public void run() {
//		//			myLog(TAG, "in Thread button1 thread id = " + Thread.currentThread().getId());
//					try {
//						Thread.sleep(1); // 1ms秒待つ
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					runOnUiThread(new Runnable() {			//メインスレッド以外からのUI更新
//						/*☆メインスレッドにポストする-
//						 *メインスレッドから呼ばれた場合、渡されたアクションをすぐに実行
//						 *メインスレッド以外から呼ばれた場合、渡されたアクションをメインスレッドのイベントキューにポスト*/
//						public void run() {
//							final String TAG = "runOnUiThread[titolList_yomikomi]";
//							String dbMsg= "開始";/////////////////////////////////////
//							try{
						//		dbMsg= "runOnUiThread thread id = " + Thread.currentThread().getId();/////////////////////////////////////
					//			myLog(TAG, dbMsg);
				//				Toast.makeText(getApplicationContext(), "Click button1", Toast.LENGTH_SHORT).show();
								getResources().getString(R.string.comon_compilation0);
								String comp = getResources().getString(R.string.comon_compilation);			//コンピレーション
								if(MaraSonActivity.this.artistMei.equals(comp)){
									MaraSonActivity.this.artistMei = comp;
									dbMsg += "→artist=" + artistMei;
								}
								Zenkyoku_db = retDB();			//全曲dbを返す
								dbMsg += ",Zenkyoku_db=" + Zenkyoku_db.getPageSize() +"件";
								String zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
								dbMsg += "；全曲リストテーブル=" + zenkyokuTName;
								dbMsg=dbMsg+",album="+MaraSonActivity.this.albumMei;		//アルバム名
								String c_selection = "ALBUM_ARTIST = ? AND ALBUM = ?";			// LIKE
								String[] c_selectionArgs= { MaraSonActivity.this.artistMei , MaraSonActivity.this.albumMei };   			//	 {"%" + artistMei + "%" , albumMei };
								String c_orderBy= null;					//⑧引数orderByには、orderBy句を指定します。	降順はDESC
								Cursor cursor = Zenkyoku_db.query(zenkyokuTName, null, c_selection, c_selectionArgs , null, null, c_orderBy);	//( table, columns, selection, selectionArgs, groupBy, having, orderBy)
								titolTotal = cursor.getCount();
								dbMsg += "；" + MaraSonActivity.this.titolTotal + "件" + titolName +"を検索";
								if(cursor.moveToFirst()){
									titolList = new ArrayList<String>();
					//				titoSubList = new ArrayList<String>();
									do{
										try{
											dbMsg = (cursor.getPosition() +1 )+ "/" + MaraSonActivity.this.titolTotal + "曲目";
											@SuppressLint("Range") String rStr = cursor.getString(cursor.getColumnIndex("TITLE"));		//タイトル
											dbMsg += rStr;
											if( rStr != null){					//アルバムに重複がなければ追記
												titolList.add(rStr);	//MediaStore.Audio.Albums.ARTIST
												dbMsg += titolList.get(titolList.size()-1);
											}
											dbMsg += rStr;
							//				myLog(TAG,dbMsg);
										}catch (IllegalArgumentException e) {
											myErrorLog(TAG,dbMsg +"で"+e.toString());
										}
									}while(cursor.moveToNext());
									tIDPTF.setText(String.valueOf(MaraSonActivity.this.titolTotal));			//タイトル合計
									dbMsg += "、titolList=" + titolList;
									if(titolName != null){
										int rInt = titolList.lastIndexOf(titolName) + 1;
										nIDPTF.setText(String.valueOf(rInt));		//;				//タイトルカウント
									}
								}else{
//									cursor = Zenkyoku_db.query(zenkyokuTName, null, null, null , null, null, c_orderBy);
//									retInt = cursor.getCount();
								}
								cursor.close();
								Zenkyoku_db.close();
					//		}
							dbMsg += MaraSonActivity.this.titolTotal + "件";
			//				myLog(TAG,dbMsg);
//							} catch (Exception e) {
//								myErrorLog(TAG,dbMsg+"で"+e);
//							}
//						}
//					});
//				}
//			}).start();

			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return titolTotal;
	}
	//////////////////////////////////////////////////②サービスで稼働している情報をActivtyに書き込む
////各リストの作成///////////////////////////////////////////////////////////////////////////////////////////
	public int pdCoundtVal;
	public String pdMessage ;			//プログレスに渡すメッセージ
	public int pdMaxVal ;															//プログレスに渡す最大値
	public String pTask_Sepa =null;		//インターフェイス文字列のセパレーター
	public HPDialog dialogFragment;
	public boolean dlogShow;
	public Thread thread;
	public ZenkyokuList ZLPD;

//	public void preRead(int reqCode , String msg) throws IOException {							//dataURIを読み込みながら欠けデータ確認
//		final String TAG = "preRead[MaraSonActivity]";
//		String dbMsg="開始";/////////////////////////////////////
//		try{
//			dbMsg= "[" + reqCode +"]";/////////////////////////////////////
//			long start = System.currentTimeMillis();		// 開始時刻の取得
//			Intent intentZL = new Intent(getApplication(),ZenkyokuList.class);						//parsonalPBook.thisではメモリーリークが起こる
//			intentZL.putExtra("reqCode",reqCode);														//処理コード
//			startActivityForResult(intentZL , reqCode);
//			long end=System.currentTimeMillis();		// 終了時刻の取得
//			dbMsg=dbMsg +";"+ (int)((end - start)) + "m秒で終了";
//			myLog(TAG, dbMsg);
//		} catch (Exception e) {
//			myErrorLog(TAG ,  dbMsg + "で" + e);
//		}
//	}

	String sucssesPass;
	public String jakeSya(String albumArt  ,ImageView mpJakeImg)  throws IOException {		//①ⅸジャケット写真を表示	<zenFealdKaikomi
		final String TAG = "jakeSya[MaraSonActivity]";
		String dbMsg=ORGUT.nowTime(true,true,true);/////////////////////////////////////
		try{
			dbMsg= "albumArt=" + albumArt ;/////////////////////////////////////
			int jH=mpJakeImg.getHeight();			//ジャケット
			dbMsg += ",jH=" +jH;
			if( jH ==0){
				jH = 287;
				dbMsg += ">>" +jH;
			}
			int jW=mpJakeImg.getWidth();			//ジャケット
			dbMsg += ",jW=" +jW;
			if( jW ==0){
				jW = 273;
				dbMsg += ">>" +jW;
			}
			if(albumArt != null){
//				http://android-note.open-memo.net/sub/image__resize_drawable.html
				Drawable drawable = new BitmapDrawable(getResources(), albumArt);
				Bitmap orgBitmap = ((BitmapDrawable)drawable).getBitmap();					//DrawableからBitmapインスタンスを取得//				http://android-note.open-memo.net/sub/image__resize_drawable.html
				dbMsg += ",orgBitmap="+orgBitmap;
				Bitmap resizedBitmap = Bitmap.createScaledBitmap(orgBitmap, jW, jH, false);										//100x100の大きさにリサイズ
				dbMsg += ",resizedBitmap="+resizedBitmap;
				drawable = new BitmapDrawable(getResources(), resizedBitmap);
				mpJakeImg.setImageDrawable(drawable);
				////				defoltIcon = getResources().getDrawable( R.drawable.no_image );		//	defoltIcon = Drawable.createFromPath("/data/data/com.hijiyam_koubou.marasongs/res/drawable/no_image.png");	//画像のあるパスからdrawableを生成
////				dbMsg += ",defoltIcon="+defoltIcon;
//				Bitmap orgBitmap = ((BitmapDrawable)defoltIcon).getBitmap();														//DrawableからBitmapインスタンスを取得
//				dbMsg += ",orgBitmap="+orgBitmap;
//				Bitmap resizedBitmap = Bitmap.createScaledBitmap(orgBitmap, 144, 144, false);										//100x100の大きさにリサイズ
//				dbMsg += ",resizedBitmap="+resizedBitmap;
//				defoltIcon = new BitmapDrawable(getResources(), resizedBitmap);
//				dbMsg += ">defoltIcon>"+defoltIcon;
//	        final ImageView imageView = (ImageView)findViewById(R.id.imageView);
//	        imageView.setImageDrawable(drawable);
//				File AAF = new File(albumArt);
//				dbMsg += ",exists=" + AAF.exists();
//				if( AAF.exists() ){									//読み込めるものまでfalseになる
//				}else{
//					dbMsg += "ファイル無し";
//		//			albumArt = null;
//				}
			}else{
				mpJakeImg.setImageResource(R.drawable.no_image);
				dbMsg += "Uri無し";
				albumArt = null;
			}
//			String rPass = ORGUT.setReSizeArt( albumArt ,  mpJakeImg ,  jH , jW , MaraSonActivity.this , sucssesPass);		//指定したイメージビューに指定したURiのファイルを表示させる
//			if( rPass != null ){
//				File SPF = new File(rPass);
//				sucssesPass = SPF.getPath();			//実際に読み出せたアルバムアートのパス
//				dbMsg += ">>sucssesPass=" + sucssesPass;
//			}
			jH=mpJakeImg.getHeight();			//ジャケット
			dbMsg += ",設定後[" +jH;
			jW=mpJakeImg.getWidth();			//ジャケット
			dbMsg += "×" +jW +"]";
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return albumArt;
	}

	FileInfoEdit fieDialog;
	int fie_cBtn;

//	@SuppressLint("Range")
//	public void creditNHanei(List<String> mainList  , String artistMei){			//クレジットアーティスト名のリスト表示名反映後の再表示処理
//		final String TAG = "creditNHanei[MaraSonActivity]";
//		String dbMsg= "開始;";/////////////////////////////////////
//		try{
//			int retInt = artistList_yomikomi();									//アーティストリストを読み込む(db未作成時は-)
//			if(retInt > 0){
//				//		dbMsg = artistList.size() + "人" ;/////////////////////////////////////
//				ruikei_artist = String.valueOf(artistList.size());				//アーティスト累計
//				dbMsg += ">>" +ruikei_artist + "人" ;/////////////////////////////////////
//	//			artistTotalPTF.setText(ruikei_artist);		//アルバムアーティス合計
//				String fn = file_ex +File.separator  + getResources().getString(R.string.app_name) +File.separator + getResources().getString(R.string.artist_reW_file);			//書き換えdb
//
//				ArtistRwHelper arhelper = new ArtistRwHelper(getApplicationContext() , fn);		//アーティスト名の置き換えリストの定義ファイル
//				//		dbMsg += " >> " + getApplicationContext().getDatabasePath(fn);
//				SQLiteDatabase ar_db = arhelper.getReadableDatabase();	//アーティスト名の置き換えリストファイル
//				String awTname = getResources().getString(R.string.artist_reW_table);	//置換えアーティストリスト
//				Cursor awCursor = ar_db.query(awTname, null, null, null , null, null, null);//リString table, String[] columns,new String[] {MotoN, albamN}
//				dbMsg="書き換え" + awCursor.getCount() +"件";/////////////////////////////////////
//				String rDate = null ;
//				if( awCursor.moveToFirst()){
//					String dataFN = getPrefStr( "pref_data_url" ,"" , MaraSonActivity.this);
//					do{
//						int rId = awCursor.getInt(awCursor.getColumnIndex("_id"));				//データURL
//						dbMsg="書き換えid=" + rId +"）";/////////////////////////////////////
//						rDate = awCursor.getString(awCursor.getColumnIndex("rDate"));				//データURL
//					}while( awCursor.moveToNext() || ! dataFN.contains(rDate) );
//				}
//				@SuppressLint("Range") String rARName = awCursor.getString(awCursor.getColumnIndex("albumArtist"));		//リストアップしたアルバムアーティスト名
//				if( rARName != null ){
//					albumArtist = rARName;
//					dbMsg += ">>" + albumArtist;//////////////////
//					myLog(TAG,dbMsg);
//					dbMsg += ">>" + artistList.indexOf(albumArtist) + "番目";//////////////////
//					artistIDPTF.setText(String.valueOf(artistList.indexOf(albumArtist)));
//				}
//				String rALName = awCursor.getString(awCursor.getColumnIndex("albumName"));		//アルバム名
//				awCursor.close();
//				ar_db.close();
//				if( rALName != null ){
//					albumName = rALName;
//					alubum_tv.setText(albumName);
//					ContentResolver resolver = getApplicationContext().getContentResolver();	//c.getContentResolver();
//					Uri cUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
//					String[] c_columns =null;					//②引数tableには、テーブル名を指定します。
//					String c_selection =  MediaStore.Audio.Albums.ARTIST +" LIKE ?";			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
//					String c_orderBy=MediaStore.Audio.Albums.FIRST_YEAR; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
//					String[] c_selectionArgs= {"%" + albumArtist + "%"};   			//⑥引数groupByには、groupBy句を指定します。
//					Cursor cursor = resolver.query( cUri , c_columns , c_selection , c_selectionArgs, c_orderBy);
//					dbMsg +=cursor.getCount() +"件";/////////////////////////////////////
//					if(cursor.moveToFirst()){
//						albumList = new ArrayList<String>();
//						albumList.clear();		//アルバム名
//						do{
//							@SuppressLint("Range") String rStr = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
//							albumList.add(rStr);
//						}while( cursor.moveToNext() );
//						albumIDPTF.setText(String.valueOf(albumList.indexOf(albumName)+1));		//アルバムカウント
//						alubumTotalPTF.setText(String.valueOf(albumList.size()));				//アーティスト合計
//					}
//					cursor.close();
//				}
////				String rRYear = awCursor.getString(awCursor.getColumnIndex("releaceYear"));		//制作年
////				String rNo = awCursor.getString(awCursor.getColumnIndex("trackNo"));			//trackNo,
//			}
//			myLog(TAG, dbMsg);
//		} catch (Exception e) {
//			myErrorLog(TAG ,  dbMsg + "で" + e);
//		}
//	}

	/**
	 * クレジットアーティスト名からアルバムアーティスト名を返す
	 * */
	public String credit2AArtist(String artistMei , String albumMei){			//クレジットアーティスト名からアルバムアーティスト名を返す
		final String TAG = "credit2AArtist[MaraSonActivity]";
		String dbMsg= "開始;";/////////////////////////////////////
	//	String rStr = null;
		try{
			dbMsg=albumMei +"は" ;/////////////////////////////////////
			ContentResolver resolver = getApplicationContext().getContentResolver();	//c.getContentResolver();
			Uri cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
			String[] c_columns =null;					//②引数tableには、テーブル名を指定します。
			String c_selection =  MediaStore.Audio.Media.ALBUM +" = ?";			//MediaStore.Audio.Albums.ARTIST +" LIKE ?"
			String c_orderBy=MediaStore.Audio.Media.ARTIST; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
			String[] c_selectionArgs= { String.valueOf(albumMei) };		//"%" + albumArtist + "%"
			Cursor cursor = resolver.query( cUri , c_columns , c_selection , c_selectionArgs, c_orderBy);
			dbMsg +=cursor.getCount() +"曲";/////////////////////////////////////
	//		myLog(TAG,dbMsg);
			if(cursor.moveToFirst()){
				do{
					dbMsg= "[" + cursor.getPosition() + "/" + cursor.getCount() +"]";/////////////////////////////////////
					@SuppressLint("Range") String tStr = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
					dbMsg +=tStr;/////////////////////////////////////
					String coment = null;
					if( tStr.equals(artistMei) ){
		//				coment = "equals";
					}else if(artistMei.startsWith(tStr)){
						coment = "startsWith";
					}else if(artistMei.endsWith(tStr)){
						coment = "endsWith";
					}else if(artistMei.contains(tStr)){
						coment = "contains";
					}
					if(coment != null && tStr.length() < artistMei.length()){
						artistMei = tStr;
						dbMsg +=">>" + artistMei + ";" + coment;
					}
		//			myLog(TAG,dbMsg);
				}while( cursor.moveToNext() );
			}
			cursor.close();
			dbMsg +=">>" + artistMei;
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return artistMei;
	}

	DialogInterface.OnClickListener mItemListener = new DialogInterface.OnClickListener() {	// アイテムのリスナー //
		public void onClick(DialogInterface dialog, int which) {
	//		Toast.makeText(MaraSonActivity.this, "Item " + which + " clicked.", Toast.LENGTH_SHORT).show();
		}
	};

//再生動作///////////////////////////////////////////////////////////////////////////////////////////////////////各リストの作成//
	public void prefHyouji(int reqCode   ){												//プリファレンス表示
		final String TAG = "prefHyouji[MaraSonActivity]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			int mcPosition = saiseiSeekMP.getProgress();
			int saiseiJikan = saiseiSeekMP.getMax();
			dbMsg="再生中の状況保存[" + mcPosition +"/" + saiseiJikan +"]から" + albumArtist;	//dataFN +
			Intent intentPRF = new Intent(MaraSonActivity.this,MyPreferences.class);			//プリファレンス
			intentPRF.putExtra("reqCode",reqCode);					//設定;
			intentPRF.putExtra("pref_apiLv",pref_apiLv);				//APIL
			intentPRF.putExtra("pref_bt_renkei",pref_bt_renkei);	//Bluetoothの接続に連携して一時停止/再開
			intentPRF.putExtra("pref_sonota_dpad",prTT_dpad);					//ダイアルキー有り
			if(mcPosition != 0){
				intentPRF.putExtra("pref_position",mcPosition);			//再生ポジション
			}
			if( saiseiJikan != 0){
				intentPRF.putExtra("pref_duration",saiseiJikan);		//DURATION 再生時間
			}
			if (android.os.Build.VERSION.SDK_INT < 14 ) {										// registerRemoteControlClient
				pref_lockscreen = false;
			} else{
				intentPRF.putExtra("pref_lockscreen", pref_lockscreen);			//ロックスクリーンプレイヤー</string>
			}
			if (android.os.Build.VERSION.SDK_INT <11 ) {
				pref_notifplayer = false;
			} else{
				intentPRF.putExtra("pref_notifplayer", pref_notifplayer);			//ノティフィケーションプレイヤー</string>
			}
			intentPRF.putExtra("pref_pb_bgc", playerBGColor_w);					//プレイヤーの背景	true＝Black"	http://techbooster.jpn.org/andriod/ui/10152/
			intentPRF.putExtra("pref_compBunki",pref_compBunki);		//コンピレーション分岐点
			intentPRF.putExtra("pref_cyakusinn_fukki",pref_cyakusinn_fukki);		// = true;		//終話後に自動再生
			dbMsg +="終話後に自動再生=" +pref_cyakusinn_fukki;/////////////////////////////////////
			dbMsg +=",このアプリのバージョンコード="+pref_sonota_vercord;/////////////////////////////////////
			intentPRF.putExtra("pref_sonota_vercord",pref_sonota_vercord);
			startActivityForResult(intentPRF , reqCode);
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}	//このアプリを終了する/////////////////////////////////////////////////////////////////////////////////

	//メニューボタンで表示するメニュー///////////////////////////////////////////////////////////////////////////////
	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu flMenu) {
	//	//Log.d("onCreateOptionsMenu","NakedFileVeiwActivity;mlMenu="+flMenu);
//		flMenu.add("Normal item");		 // メニューの要素を追加
//		MenuItem actionItem = flMenu.add(0,0,0,"メニュー");	// メニューの要素を追加して取得
//		actionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);	// SHOW_AS_ACTION_IF_ROOM:余裕があれば表示
//		actionItem.setIcon(android.R.drawable.ic_menu_share);	// アイコンを設定

		getMenuInflater().inflate(R.menu.mara_son , flMenu);		//メニューリソースの使用
		return super.onCreateOptionsMenu(flMenu);
	// メニューアイテムの追加
//public abstract MenuItem add (int groupId, int itemId, int order, CharSequence title)
//int groupId	グループの識別子。メニューをグループ分けする必要がない時は通常NONEを指定する。
//		int itemId	メニューアイテムを識別するためにユニークなIDを指定する。ユニークなIDが不要な場合はNONEを指定する。
//		int order	メニューアイテムの表示する順番。NONEを指定すれば、追加した順番で表示される。
//		CharSequence title	メニューに表示する文字列
	}

	public boolean makeOptionsMenu(Menu flMenu) {	//ボタンで表示するメニューの内容
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu flMenu) {			//表示直前に行う非表示や非選択設定	ToolBarでは自動反映されないのでトリガーになるメソッド内から強制実行する
		final String TAG = "onPrepareOptionsMenu[MaraSonActivity]";
		String dbMsg=ORGUT.nowTime(true,true,true);/////////////////////////////////////
		try{
			flMenu.findItem(R.id.menu_item_randumplay).setEnabled(true);			//ランダム再生</string>
			dbMsg = "rp_pp = " + rp_pp;
			if( rp_pp ){														//pp_pp_ll.isShown()
				flMenu.findItem(R.id.menu_item_repeatplay).setEnabled(false);			//リピート再生
				flMenu.findItem(R.id.menu_item_repeatplay).setVisible(false);			//リピート再生
				flMenu.findItem(R.id.menu_item_repeatplay_kaijo).setVisible(true);		//リピート再生解除
				flMenu.findItem(R.id.menu_item_repeatplay_kaijo).setEnabled(true);		//リピート再生解除
				flMenu.findItem(R.id.pref_nitenkan_start_dt).setVisible(true);		//二点間再生開始点
				flMenu.findItem(R.id.pref_nitenkan_start_dt).setEnabled(true);		//二点間再生開始点
				flMenu.findItem(R.id.pref_nitenkan_end_dt).setVisible(true);		//二点間再生終了点
				flMenu.findItem(R.id.pref_nitenkan_end_dt).setEnabled(true);		//二点間再生終了点
			}else{
				flMenu.findItem(R.id.menu_item_repeatplay).setEnabled(true);			//リピート再生
				flMenu.findItem(R.id.menu_item_repeatplay).setVisible(true);			//リピート再生
				flMenu.findItem(R.id.menu_item_repeatplay_kaijo).setVisible(false);		//リピート再生解除
				flMenu.findItem(R.id.menu_item_repeatplay_kaijo).setEnabled(false);		//リピート再生解除
				flMenu.findItem(R.id.pref_nitenkan_start_dt).setVisible(false);		//二点間再生開始点
				flMenu.findItem(R.id.pref_nitenkan_start_dt).setEnabled(false);		//二点間再生開始点
				flMenu.findItem(R.id.pref_nitenkan_end_dt).setVisible(false);		//二点間再生終了点
				flMenu.findItem(R.id.pref_nitenkan_end_dt).setEnabled(false);		//二点間再生終了点
			}
			flMenu.findItem(R.id.menu_item_tone).setEnabled(true);			//音質調整
			flMenu.findItem(R.id.effect_reverb).setEnabled(true);				//リバーブ
	//		flMenu.findItem(R.id.menu_funk_artistmei).setEnabled(false);			//このアーティストの指定</string>
	//		flMenu.findItem(R.id.comon_sakuseiyotei).setEnabled(false);				//作成予定

			flMenu.findItem(R.id.menu_item_sonota_settei).setEnabled(true);		//設定	MENU_SETTEI
			flMenu.findItem(R.id.menu_item_sonota_help).setEnabled(true);		//ヘルプ表示	MENU_HELP
			flMenu.findItem(R.id.menu_item_sonota_end).setEnabled(true);	//終了	MENU_END
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
			return false;
		}
		return true;
	}

@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final String TAG = "onOptionsItemSelected[MaraSonActivity]";
		String dbMsg=ORGUT.nowTime(true,true,true);/////////////////////////////////////
		try{
			dbMsg = "MenuItem"+item.getItemId()+"="+item.toString();////////////////////////////////////////////////////////////////////////////
	//		Log.d("onOptionsItemSelected",dbMsg);
			Locale locale = Locale.getDefault();		// アプリで使用されているロケール情報を取得
			String helpURL;

			int nowSelectMenu = item.getItemId();
			switch (nowSelectMenu) {
			case R.id.menu_item_randumplay:					//ランダム再生</string>
				randumPlay();									//ランダム再生
				break;
			case R.id.menu_item_repeatplay:					//リピート再生
				repeatePlay();
				break;
			case R.id.menu_item_repeatplay_kaijo:			//リピート再生解除
				repeatePlaykijyo();
				break;
			case R.id.pref_nitenkan_start_dt:				//二点間再生開始点
				ppSet(R.id.pp_start_rd);								//二点間リピートの調整
				break;
			case R.id.pref_nitenkan_end_dt:					//二点間再生終了点
				ppSet(R.id.pp_end_rd);								//二点間リピートの調整
				break;
			case R.id.menu_item_tone:					//音質調整
				toneSettie();								//音質調整
				break;
			case R.id.effect_reverb:						//リバーブ
				reverbSettie();								//リバーブ設定のIF表示
				break;
//			case R.id.menu_funk_artistmei:					//このアーティストの指定</string>
//				creditNameTouroku( );							//クレジットされているアーティスト名のリスト登録の扱い
//				return true;
			case R.id.menu_item_sonota_settei:				//設定
				prefHyouji(settei_hyouji);					//設定表示☆toolBarのメニューでメニューＩＤは反映されない
				return true;
			case R.id.menu_item_sonota_help:						//ヘルプ表示	MENU_HELP
//				Intent intentWV = new Intent(MaraSonActivity.this,wKit.class);			//webでヘルプ表示
//				if(locale.equals( Locale.JAPAN)){										//日本語の場合のみconstant for ja_JP.
//					helpURL = "http://www.geocities.jp/hqu666/maramongs/player.html";		//日本語ヘルプ				//	helpURL = "file:///android_asset/list.html";		//日本語ヘルプ
//				}else {
//					helpURL = "http://www.geocities.jp/hqu666/maramongs/en/player.html";	//英語ヘルプ				//	helpURL = "file:///android_asset/en/list.html";	//英語ヘルプ
//				}
//				intentWV.putExtra("dataURI",helpURL);		//"file:///android_asset/index.html"
//				intentWV.putExtra("fType","");		//"file:///android_asset/index.html"
//				startActivity(intentWV);
				return true;
			case R.id.menu_item_sonota_end:					//終了	MENU_END
				quitMe();		//このアプリを終了する
				return true;
			}
			//通常のメニュー処理を、それをここで消費するためにtrueで、継続する場合はfalse。
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
			return false;
		}
		return false;	//Return false to allow normal menu processing to proceed, true to consume it here.
	}

	@Override
	public void onOptionsMenuClosed(Menu flMenu) {
		final String TAG = "onOptionsMenuClosed[MaraSonActivity]";
		String dbMsg=ORGUT.nowTime(true,true,true);/////////////////////////////////////
		try{
			dbMsg= "flMenu=" + flMenu;/////////////////////////////////////
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

//コンテキストメニュ///////////////////////////////////////////////////////////////////////////////
	static final int CONTEXT_Lylic2Web = quit_all + 1000;								//歌詞をwebに表示
	static final int CONTEXT_Lylic_Encord = CONTEXT_Lylic2Web + 11;					//歌詞の再エンコード
	static final int CONTEXT_Lylic_Reload = CONTEXT_Lylic_Encord + 1;				//歌詞の再読み込み

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
		final String TAG = "onCreateContextMenu";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg="menu=" + menu;/////////////////////////////////////
			int viewId = view.getId();
				String contextTitile = getResources().getString(R.string.lylic_contex_title);								//歌詞表示の操作
				menu.setHeaderTitle(contextTitile);		//APIL1;リスト操作;コンテキストメニューの設定
			switch(viewId) {
			case R.id.lyric_tv:						//2131558448 タイトル
				menu.add(0, CONTEXT_Lylic2Web, 0, getResources().getString(R.string.lylic_contex_lylic2web));				//webに表示
	//			menu.add(0, CONTEXT_Lylic_Encord, 0, getResources().getString(R.string.lylic_contex_encord));				//再エンコード
				menu.add(0, CONTEXT_Lylic_Reload, 0, getResources().getString(R.string.lylic_contex_reload));				//再読み込み
//				if( ! lyricAri){							//歌詞を取得できていなければ
//					menu.getItem(1).setEnabled(false);		//グレーアウト
//				}
				break;
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public boolean onContextItemSelected(MenuItem item) {
		item.getMenuInfo();
		final String TAG = "onContextItemSelected";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg="item" + item.getItemId() + ")";/////////////////////////////////////
			myLog(TAG, dbMsg);
			switch (item.getItemId()) {
			case CONTEXT_Lylic2Web:					//歌詞をwebに表示
				lyric2web( lylicHTM );					//歌詞をwebKitに送る
				return true;
			case CONTEXT_Lylic_Encord:				//歌詞の再エンコード
				saiEncord();									//再エンコード要求
				return true;
			case CONTEXT_Lylic_Reload:				//歌詞の再読み込み
				b_filePath = null;
				String dataFN = getPrefStr( "pref_data_url" ,"" , MaraSonActivity.this);
				readLyric( dataFN );					//歌詞の読出し
				return true;
			default:
				return super.onContextItemSelected(item);
			}
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return true;
	}

//プレイヤーから呼び出す機能////////////////////////////////////////////////////////////メニューボタンで表示するメニュー//
	/**
	 * ランダム再生
	 *
	 * */
	public void randumPlay(){			//ランダム再生
		final String TAG = "randumPlay[MaraSonActivity]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			if( rp_pp ){
				MaraSonActivity.this.rp_pp = false;
				MaraSonActivity.this.pp_start = 0;
				MaraSonActivity.this.pp_end = 0;
				myEditor.putBoolean("pref_nitenkan", MaraSonActivity.this.rp_pp);
				myEditor.putString( "pref_nitenkan_start", String.valueOf(MaraSonActivity.this.pp_start));
				myEditor.putString( "pref_nitenkan_end", String.valueOf(MaraSonActivity.this.pp_end));
				Boolean kakikomi = myEditor.commit();	// データの保存
				dbMsg +=",kakikomi="+kakikomi;////////////////////////////////////////////////////////////////////////////
			}
			Intent intent = new Intent(MaraSonActivity.this, MuList.class);			//
			int reqCode = CONTEXT_runum_sisei ;			//ランダム再生
			dbMsg +="reqCode="+ reqCode;/////////////////////////////////////
			intent.putExtra("reqCode",reqCode);
			int mcPosition = saiseiSeekMP.getProgress();
			String dataFN = getPrefStr( "pref_data_url" ,"" , MaraSonActivity.this);
			callListView(reqCode ,dataFN , mcPosition );								//リストビューを読出し
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public int repeatSyurui;		//リピート区分
	public String repeatArtist;		//リピートさせるアーティスト名
	public int seekLTPosition;		//シークの現在点
	public int repeatPPSyurui;		//区間開始/終了区分
	public int repeatType;			//リピート再生の種類
	public boolean rp_pp = false;			//2点間リピート中
	public boolean rp_pp_bicyousei =false;			//2点間リピート設定変更
	public int pp_start = 0;			//リピート区間開始点
	public int pp_end = pp_start+1;				//リピート区間終了点
	public int b_pp_start = 0;			//リピート区間開始点
	public boolean is_pp_set_start =false;			//2点間リピートの開始側を設定中

	public int b_pp_end = pp_start+1;				//リピート区間終了点
	public LinearLayout rd_pp_ll;	//リピート区間IF
	public RadioGroup rd_pp_gr;				//リピートの種類グループ
	public RadioButton pp_start_rd ;		//アーティストリピート指定ボタン
	public RadioButton pp_end_rd ;			//アーティストリピート指定ボタン
	public TextView rd_pp_start_tf;		//リピート区間開始点
	public TextView rd_pp_end_tf;			//リピート区間終了点
	public SeekBar rd_pp_seekBar;
	public TextView rd_start_tf;
	public TextView rd_end_tf;

	/**
	 *  開始/終了反転確認
	 *  反転していたらtrue*/
	public boolean ppParamCehck(int stInt , int endInt){
		final String TAG = "ppParamSet";
		String dbMsg="[MaraSonActivity]" + stInt + "～" + endInt ;
		boolean retBool = false;
		try{
			if(endInt < stInt){
				String msgStr = getResources().getString(R.string.repeate_dt_invert_msg) + "\n";
				msgStr += ORGUT.sdf_mss.format(MaraSonActivity.this.b_pp_start) + " " + getResources().getString(R.string.comon_kara_mark)  + " " + ORGUT.sdf_mss.format(MaraSonActivity.this.pp_end)  + ">>\n";
				msgStr += ORGUT.sdf_mss.format(stInt) + " " + getResources().getString(R.string.comon_kara_mark)  + " " + ORGUT.sdf_mss.format(endInt)  + "\n";
				new AlertDialog.Builder(MaraSonActivity.this)
						.setTitle(getResources().getString(R.string.repeate_dt_nitenkan))
						.setMessage(msgStr)
						.setPositiveButton(getResources().getString(R.string.modosu_msg) , new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog , int which) {
								final String TAG = getResources().getString(R.string.modosu_msg);
								String dbMsg="[MaraSonActivity.ppParamSet]" + stInt + "～" + endInt ;
								if(which == 1){
									if(is_pp_set_start){       	//2点間リピートの開始側を設定中
										pp_start = b_pp_start;			//リピート区間開始点の置き数
									}else{
										pp_end = b_pp_end;				//リピート区間終了点の置き数
									}
								}
								dbMsg += ">>" + ORGUT.sdf_mss.format(pp_start) + "～" + ORGUT.sdf_mss.format(pp_end);
								myLog(TAG, dbMsg);
							}
						})
						.setNegativeButton(getResources().getString(R.string.sonomsmst_msg) , new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog , int which) {
								final String TAG = getResources().getString(R.string.sonomsmst_msg);
								String dbMsg="[MaraSonActivity.ppParamSet]" + stInt + "～" + endInt ;
								myLog(TAG, dbMsg);
								if(which == 1){
									if(is_pp_set_start){       	//2点間リピートの開始側を設定中
										pp_start = stInt;			//リピート区間開始点の置き数
										b_pp_start = pp_start;			//リピート区間開始点の置き数
									}else{
										pp_end = endInt;				//リピート区間終了点の置き数
										b_pp_end = pp_end;				//リピート区間終了点の置き数
									}
									dbMsg += ">>" + ORGUT.sdf_mss.format(pp_start) + "～" + ORGUT.sdf_mss.format(pp_end);
								}
							}
						})
						.create().show();
//				pp_start = endInt;
//				pp_end = stInt;
//				dbMsg += ">>" + pp_start + "～" + pp_end ;
				retBool = true;
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return  retBool;
	}

	public void ppParamSet(int sPoint){			//二点間リピートの調整
		final String TAG = "ppParamSet";
		String dbMsg="";
		try{
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * リピート再生の設定
	 *  四つの中から選択	 *
	 * */
	public void repeatePlay(){			//リピート再生
		final String TAG = "repeatePlay";
		String dbMsg="";
		try{
			seekLTPosition = saiseiSeekMP.getProgress();					//シークの現在点
			dbMsg +=",seekLTPosition=" + seekLTPosition;
			pp_start = seekLTPosition;				//リピート区間開始点
			pp_end = saiseiJikan;			//リピート区間終了点
			b_pp_start = pp_start;			//リピート区間開始点の置き数
			b_pp_end = pp_end;				//リピート区間終了点の置き数
			ORGUT.sdf_mss.format(pp_start);
			ORGUT.sdf_mss.format(pp_end);
			b_List = nowList;				//前に再生していたプレイリスト
			modori_List_id =  nowList_id;			//リピート前のプレイリストID
			sharedPref = getSharedPreferences( getResources().getString(R.string.pref_main_file) ,MODE_PRIVATE);		//	getSharedPreferences(prefFname,MODE_PRIVATE);
			myEditor = sharedPref.edit();
			myEditor.putString( "b_List" ,String.valueOf(nowList) );						//再生中のファイル名  Editor に値を代入
			myEditor.putString( "modori_List_id" , String.valueOf(nowList_id));
			boolean wrb = myEditor.commit();
			dbMsg +=">>書込み成功="+ wrb;

			LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);		// カスタムビューを設定
			final View layout = inflater.inflate(R.layout.repeate_dlg, findViewById(R.id.rd_root_ll));
			AlertDialog.Builder builder = new AlertDialog.Builder(this);			// アラーとダイアログ を生成
			builder.setTitle( getResources().getString(R.string.repeate_dt));
			builder.setView(layout);
			RadioGroup rd_repeat_gr = layout.findViewById(R.id.rd_repeat_gr);		//リピートの種類グループ
			RadioButton rd_artist_rd = layout.findViewById(R.id.rd_artist_rd);	//アーティストリピート指定ボタン
			RadioButton rd_album_rd = layout.findViewById(R.id.rd_album_rd);		//アーティストリピート指定ボタン
			RadioButton titol_rd_rd = layout.findViewById(R.id.titol_rd_rd);		//タイトルリピート指定ボタン
			layout.findViewById(R.id.rd_point_rb);
			rd_artist_rd.setChecked(true);
			MaraSonActivity.this.repeatSyurui = rp_artist;

			rd_pp_ll = layout.findViewById(R.id.pp_pp_gll);						//リピート区間
			rd_pp_gr = layout.findViewById(R.id.rd_pp_gr);				//リピートの種類グループ
			pp_start_rd = layout.findViewById(R.id.pp_start_rd);		//区間リピート開始ボタン
			pp_end_rd = layout.findViewById(R.id.pp_end_rd);			///区間リピート終了ボタン
			rd_pp_start_tf = layout.findViewById(R.id.pp_pp_set_tf);		//リピート区間開始点
			rd_pp_end_tf = layout.findViewById(R.id.rd_pp_end_tf);			//リピート区間終了点
			rd_pp_seekBar = layout.findViewById(R.id.pp_pp_seekBar);
//			Drawable drawable = getResources().getDrawable(R.drawable.pp_progress);
//			rd_pp_seekBar.setProgressDrawable(drawable);
			rd_start_tf = layout.findViewById(R.id.rd_start_tf);
			rd_end_tf = layout.findViewById(R.id.pp_end_tf);
			rd_pp_ll.setVisibility(View.GONE);

			rd_repeat_gr.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {				//ラジオボタンが押された、状態が変わった時の挙動を記述する
				 public void onCheckedChanged(RadioGroup group, int checkedId) {
						final String TAG = "onCheckedChanged";
					 String dbMsg="[MaraSonActivity.repeatePlay.rd_repeat_gr]";
						try{
							dbMsg="group = " + ",checkedId=" + checkedId;
							switch(checkedId) {
							case R.id.alubum_tv:				//2131558442アルバム
								checkedId = rp_artist;
								break;
							case R.id.rd_album_rd:			//アルバムリピート指定ボタン
								checkedId = rp_album;
								break;
							case R.id.titol_rd_rd:			//タイトルリピート指定ボタン
								checkedId = rp_titol;
								break;
							case R.id.rd_point_rb:			//2131558550 二点間リピート指定ボタン
								checkedId = rp_point;
								break;
							}

							switch(checkedId) {
							case rp_point:
								dbMsg +=";タイトル = " + titolName + "のリピート";
								String pp_startStr = ORGUT.sdf_mss.format(MaraSonActivity.this.pp_start);				//二点間再生開始点(mmss000)
								dbMsg +=", " + pp_startStr;
								String pp_endStr = ORGUT.sdf_mss.format(MaraSonActivity.this.pp_end);			//二点間再生終了点(mmss000)
								dbMsg +="～" + pp_endStr + "のリピート";
								String endStr = ORGUT.sdf_mss.format(MaraSonActivity.this.saiseiJikan);			//再生終了点(mmss000)
								dbMsg +=";;" + endStr + ";;";
								rd_pp_ll.setVisibility(View.VISIBLE);
								pp_zenkai_ll.setVisibility(View.GONE);						//前回の累積レイアウト

					//			pp_start_rd.setChecked(true);
								rd_pp_gr.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {				//ラジオボタンが押された、状態が変わった時の挙動を記述する
									 public void onCheckedChanged(RadioGroup group, int checkedId) {
									 	final String TAG = "rd_pp_gr";
									 	String dbMsg="[MaraSonActivity.repeatePlay.rd_pp_gr]";
										try{
											dbMsg="group = " + ",checkedId=" + checkedId;
											switch(checkedId) {
											case R.id.pp_start_rd:		//区間リピート開始ボタン
												dbMsg +=";区間リピート開始";
												MaraSonActivity.this.pp_start = seekLTPosition = saiseiSeekMP.getProgress();					//シークの現在点
												String pp_startStr = ORGUT.sdf_mss.format(MaraSonActivity.this.pp_start);				//二点間再生開始点(mmss000)
												dbMsg +=", " + pp_startStr;
												rd_pp_start_tf.setText(pp_startStr);			//二点間再生開始点(mmss000)
												break;
											case R.id.pp_end_rd:		//区間リピート終了ボタン
												dbMsg +=";区間リピート終了";
												MaraSonActivity.this.pp_end = seekLTPosition = saiseiSeekMP.getProgress();					//シークの現在点
												String pp_endStr = ORGUT.sdf_mss.format(MaraSonActivity.this.pp_end);			//二点間再生終了点(mmss000)
												dbMsg +="～" + pp_endStr + "のリピート";
												rd_pp_end_tf.setText(pp_endStr);			//二点間再生終了点(mmss000)
												rd_pp_seekBar.setSecondaryProgress(MaraSonActivity.this.pp_end);
												break;
//												default:
//													break;
											}
											repeatPPSyurui = checkedId;		//区間開始/終了区分
											myLog(TAG, dbMsg);
										} catch (Exception e) {
											myErrorLog(TAG ,  dbMsg + "で" + e);
										}
									}
								});

								rd_pp_seekBar.setMax(saiseiJikan);
								rd_pp_seekBar.setProgress(seekLTPosition);
								rd_pp_seekBar.setSecondaryProgress(seekLTPosition);

								rd_start_tf.setText(pp_startStr);			//二点間再生開始点(mmss000)
								rd_end_tf.setText(endStr);			//二点間再生終了点(mmss000)
								rd_pp_start_tf.setText(pp_startStr);			//二点間再生開始点(mmss000)
								rd_pp_end_tf.setText(pp_endStr);			//二点間再生終了点(mmss000)
								break;
							default:
								MaraSonActivity.this.pp_start = 0;
								rd_pp_ll.setVisibility(View.GONE);
								pp_zenkai_ll.setVisibility(View.VISIBLE);						//前回の累積レイアウト
								break;
							}
							MaraSonActivity.this.repeatSyurui = checkedId;
							dbMsg +=";repeatSyurui = " + MaraSonActivity.this.repeatSyurui;
							myLog(TAG, dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG ,  dbMsg + "で" + e);
						}
					}
				});
			rd_pp_seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				public void onStopTrackingTouch(SeekBar seekBar) {
					if(ppParamCehck(MaraSonActivity.this.pp_start,MaraSonActivity.this.pp_end )){
//						if(is_pp_set_start){       	//2点間リピートの開始側を設定中
							String pp_startStr = ORGUT.sdf_mss.format(MaraSonActivity.this.pp_start);		//二点間再生開始点(mmss000)
							rd_pp_start_tf.setText(pp_startStr);
//						}else{
							String pp_endStr = ORGUT.sdf_mss.format(MaraSonActivity.this.pp_end);			//二点間再生終了点(mmss000)
							rd_pp_end_tf.setText(pp_endStr);
//						}
					}
				}
				public void onStartTrackingTouch(SeekBar seekBar) {
					final String TAG = "onStartTrackingTouch";
					String dbMsg="[MaraSonActivity.rd_pp_seekBar]";
					try{
						myLog(TAG, dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}

				public void onProgressChanged(SeekBar seekBar,int progress, boolean fromUser) {
					final String TAG = "onProgressChanged";
					String dbMsg="[MaraSonActivity.rd_pp_seekBar]";
					try{
						dbMsg= "progress=" + progress + ",fromUser=" + fromUser;
						dbMsg +=";区間開始/終了区分" + MaraSonActivity.this.repeatPPSyurui;
						switch(MaraSonActivity.this.repeatPPSyurui) {		//区間開始/終了区分
						case R.id.pp_start_rd:		//区間リピート開始ボタン
							dbMsg +=";区間リピート開始";
							MaraSonActivity.this.pp_start = progress;													//シークの現在点
							String pp_startStr = ORGUT.sdf_mss.format(MaraSonActivity.this.pp_start);		//二点間再生開始点(mmss000)
							dbMsg +=", " + pp_startStr;
							rd_pp_start_tf.setText(pp_startStr);			//二点間再生開始点(mmss000)
							is_pp_set_start =true;			//2点間リピートの開始側を設定中
							break;
						case R.id.pp_end_rd:		//区間リピート終了ボタン
							dbMsg +=";区間リピート終了";
							MaraSonActivity.this.pp_end = progress;														//シークの現在点
							String pp_endStr = ORGUT.sdf_mss.format(MaraSonActivity.this.pp_end);			//二点間再生終了点(mmss000)
							dbMsg +="～" + pp_endStr + "のリピート";
							rd_pp_end_tf.setText(pp_endStr);			//二点間再生終了点(mmss000)
							rd_pp_seekBar.setSecondaryProgress(MaraSonActivity.this.pp_end);
							is_pp_set_start =false;			//2点間リピートの開始側を設定中
							break;
//						default:
//							break;
						}
						myLog(TAG, dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
			    }
			});

			dbMsg +="、アーティスト = " + albumArtist;

			if(albumArtist == null){
				albumArtist = creditArtistName;
				dbMsg +=">>" + albumArtist;
			}else if(albumArtist.equals("")){
				albumArtist = creditArtistName;
				dbMsg +=">>" + albumArtist;
			}
			String aArtist = credit2AArtist(albumArtist , albumName);			//クレジットアーティスト名からアルバムアーティスト名を返す
			dbMsg +=",aArtist=" + aArtist;
			if(albumArtist.contains(aArtist)){
				albumArtist = aArtist;
				dbMsg +=">>" + albumArtist;
			}
			rd_artist_rd.setText(albumArtist);
			dbMsg +="、アルバム = " + albumName;
			rd_album_rd.setText(albumName);
			dbMsg +=";タイトル = " + titolName ;
			titol_rd_rd.setText(titolName);

			builder.setNegativeButton(getResources().getString(R.string.comon_cyusi), new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					String dbMsg ="[MaraSonActivity.repeatePlay]";/////////////////////////////////////
					MaraSonActivity.this.repeatArtist = "";		//リピートさせるアーティスト名
					MaraSonActivity.this.rp_pp = false;			//2点間リピート中
					rp_pp_bicyousei =false;			//2点間リピート設定変更
				}
			});
			builder.setPositiveButton(getResources().getString(R.string.comon_kakutei), new OnClickListener () {
				public void onClick(DialogInterface dialog, int which) {
					final String TAG = getResources().getString(R.string.comon_kakutei)+".";
					String dbMsg ="[MaraSonActivity.repeatePlay]";/////////////////////////////////////
					try{
						dbMsg +=";repeatSyurui = " + MaraSonActivity.this.repeatSyurui;
						String selName = MaraSonActivity.this.titolName;
						int mcPosition = saiseiSeekMP.getProgress();
						int saiseiJikan = saiseiSeekMP.getMax();

						switch(MaraSonActivity.this.repeatSyurui) {
						case rp_artist:													//2131558547
							selName = MaraSonActivity.this.albumArtist;
							dbMsg +=";アーティスト = " + selName + "のリピート";
							MaraSonActivity.this.repeatArtist = MaraSonActivity.this.albumArtist;		//リピートさせるアーティスト名
							myEditor.putString("repeatArtist", String.valueOf(MaraSonActivity.this.repeatArtist));			//リピートさせるアーティスト名
							repeatePlaykijyo();			//リピート再生解除
							break;
						case rp_album:													//2131558548
							selName = MaraSonActivity.this.albumName;
							dbMsg +=";アルバム = " + selName + "のリピート";
							repeatePlaykijyo();			//リピート再生解除
							break;
						case rp_titol:													//2131558549
							dbMsg +=";タイトル = " + titolName + "のリピート";
							repeatePlaykijyo();			//リピート再生解除
							break;
						case rp_point:													//2131558550
							dbMsg +=";二点間=" + MaraSonActivity.this.pp_start + "～" + MaraSonActivity.this.pp_end ;
							if(MaraSonActivity.this.pp_end < MaraSonActivity.this.pp_start){
								int sPoint = MaraSonActivity.this.pp_end;
								MaraSonActivity.this.pp_end = MaraSonActivity.this.pp_start;
								MaraSonActivity.this.pp_start = sPoint;
								dbMsg +=">>" + MaraSonActivity.this.pp_start + "～" + MaraSonActivity.this.pp_end ;
							}

							String pp_startStr = ORGUT.sdf_mss.format(MaraSonActivity.this.pp_start);				//二点間再生開始点(mmss000)
							dbMsg +=", " + pp_startStr;
							String pp_endStr = ORGUT.sdf_mss.format(MaraSonActivity.this.pp_end);			//二点間再生終了点(mmss000)
							dbMsg +="～" + pp_endStr + "のリピート";
							pp_pp_ll.setVisibility(View.VISIBLE);
							rp_pp_bicyousei =false;			//2点間リピート設定変更
							ppSetEnd( MaraSonActivity.this.pp_start , MaraSonActivity.this.pp_end);		//二点間リピートの調整結果
							break;
						default:
							repeatePlaykijyo();			//リピート再生解除
							break;
						}
						if( MaraSonActivity.this.repeatSyurui != rp_point ){
							MaraSonActivity.this.rp_pp = false;			//2点間リピート中
							mcPosition = 0;
							myEditor.putBoolean("pref_nitenkan", MaraSonActivity.this.rp_pp);
						}
						myEditor.putString("repeatType", String.valueOf(MaraSonActivity.this.repeatSyurui));					//リピート再生の種類
						Boolean kakikomi = myEditor.commit();											// データの保存
						dbMsg +=",kakikomi="+kakikomi;////////////////////////////////////////////////////////////////////////////
						int reqCode = MaraSonActivity.this.repeatSyurui ;
						dbMsg +=",reqCode="+ reqCode;/////////////////////////////////////
						myLog(TAG, dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}
			});
			builder.create().show();	// 表示
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public TextView pp_mms_tv;
	public EditText pp_s_et;
	public EditText pp_pp_et;
	public SeekBar pp_seek;
	public SubMenu kaijyoMenu;

	/**
	 * 二点間リピートの調整
	 *  ダイアログ表示
	 * */
	public void ppSet(int se){			//二点間リピートの調整
		final String TAG = "ppSet";
		String dbMsg="";
		try{
			this.repeatPPSyurui = se;
			int editInt  = saiseiSeekMP.getProgress();					//シークの現在点	seekLTPosition
			dbMsg +=",シークの現在点=" + editInt;
//			MaraSonActivity.this.mcPosition = MaraSonActivity.this.pp_start;
////			pp_start = seekLTPosition;				//リピート区間開始点
////			pp_end = saiseiJikan;			//リピート区間終了点
////			String pp_startStr = ORGUT.sdf_mss.format(pp_start).toString();				//二点間再生開始点(mmss000)
////			String pp_endStr = ORGUT.sdf_mss.format(pp_end).toString();					//二点間再生終了点(mmss000)
			String saiseiJikanStr = ORGUT.sdf_mss.format(saiseiJikan);		//この曲の長さ
			String dTitle =  getResources().getString(R.string.pref_nitenkan_start_dt);			//二点間再生開始点
			String nowStr = ORGUT.sdf_mss.format( MaraSonActivity.this.pp_start);
			is_pp_set_start = true;      	//2点間リピートの開始側を設定中
			if( se == R.id.pp_end_rd ){
				dTitle =  getResources().getString(R.string.pref_nitenkan_end_dt);				//二点間再生終了点</string>
				nowStr = ORGUT.sdf_mss.format( MaraSonActivity.this.pp_end);
				editInt =  MaraSonActivity.this.pp_end;
				is_pp_set_start = false;      	//2点間リピートの開始側を設定中
			}

			LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);		// カスタムビューを設定
			final View layout = inflater.inflate(R.layout.repeate_pp_dlg, findViewById(R.id.pp_root_ll));
			AlertDialog.Builder builder = new AlertDialog.Builder(this);			// アラーとダイアログ を生成
			builder.setTitle( dTitle );
			builder.setView(layout);
			pp_mms_tv = layout.findViewById(R.id.pp_mms_tv);
			pp_s_et = layout.findViewById(R.id.pp_s_et);
			pp_pp_et = layout.findViewById(R.id.pp_pp_et);
			TextView pp_now_tf = layout.findViewById(R.id.pp_now_tf);
			pp_now_tf.setText(nowStr);
			TextView pp_end_tf = layout.findViewById(R.id.pp_end_tf);
			pp_end_tf.setText(saiseiJikanStr);
			pp_seek = layout.findViewById(R.id.pp_seek);
			pp_seek.setMax(saiseiJikan);
			pp_seek.setProgress(editInt);
			pp_seek.setSecondaryProgress(pp_end);
//			Drawable drawable = getResources().getDrawable(R.drawable.pp_progress);
//			pp_seek.setProgressDrawable(drawable);
			dbMsg +=",editInt=" + editInt;
			String edidStr = ORGUT.sdf_mss.format(editInt);
			dbMsg +=",edidStr=" + edidStr;
			String mmsStr = edidStr.substring(0, edidStr.length()-5);
			dbMsg +=",mmsStr=" + mmsStr;
			pp_mms_tv.setText(mmsStr);
			String secondStr = edidStr.substring(edidStr.length()-5, edidStr.length()-4);
			dbMsg +=",secondStr=" + secondStr;
			pp_s_et.setText(secondStr);
			String miliStr = edidStr.substring(edidStr.length()-3);
			dbMsg +=",miliStr=" + miliStr;
			pp_pp_et.setText(miliStr);
			pp_pp_et.addTextChangedListener(new TextWatcher(){
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count){
					final String TAG = "onTextChanged]";
					String dbMsg ="[MaraSonActivity.ppSet.pp_pp_et]";
					try{
						dbMsg="s=" + s + ",start=" + start + ",before=" + before + ",count=" + count;
						myLog(TAG, dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					final String TAG = "beforeTextChanged";
					String dbMsg ="[MaraSonActivity.ppSet.pp_pp_et]";
					try{
						dbMsg="s=" + s + ",start=" + start + ",after=" + after + ",count=" + count;
//						String retStr = String.valueOf(s);
//						switch(count) {
//						case 0:
//							retStr = "000";
//							break;
//						case 1:
//							retStr = "00" + s;
//							break;
//						case 2:
//							retStr = "0" + s;
//							break;
////							default:
////								break;
//						}
//						if(! retStr.equals(String.valueOf(s))){
//							pp_pp_et.setText(retStr);
//						}
						myLog(TAG, dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}

				@Override
				public void afterTextChanged(Editable s) {
					final String TAG = "afterTextChanged]";
					String dbMsg ="[MaraSonActivity.ppSet.pp_pp_et]";
					try{
						dbMsg="s=" + s ;
						int len = s.length();
						dbMsg +=",len=" + len +"文字" ;
//						String retStr = String.valueOf(s);
//						switch(len) {
//						case 0:
//							retStr = "000";
//							break;
//						case 1:
//							retStr = "00" + retStr;
//							break;
//						case 2:
//							retStr = "0" + retStr;
//							break;
////							default:
////								break;
//						}
//						if(! retStr.equals(String.valueOf(s))){
//							pp_pp_et.setText(retStr);
//						}
						myLog(TAG, dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}
			});

			pp_seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				public void onStopTrackingTouch(SeekBar seekBar) {
					final String TAG = "onStopTrackingTouch";
					String dbMsg ="[MaraSonActivity.ppSet.pp_seek]";

					if(ppParamCehck(MaraSonActivity.this.pp_start,MaraSonActivity.this.pp_end )){
						String edidStr = ORGUT.sdf_mss.format(MaraSonActivity.this.pp_end);
						dbMsg +=",edidStr=" + edidStr;
						if(is_pp_set_start){       	//2点間リピートの開始側を設定中
							edidStr = ORGUT.sdf_mss.format(MaraSonActivity.this.pp_start);		//二点間再生開始点(mmss000)
						}
						String mmsStr = edidStr.substring(0, edidStr.length()-5);
						dbMsg +=",mmsStr=" + mmsStr;
						pp_mms_tv.setText(mmsStr);
						String secondStr = edidStr.substring(edidStr.length()-5, edidStr.length()-4);
						dbMsg +=",secondStr=" + secondStr;
						pp_s_et.setText(secondStr);
						String miliStr = edidStr.substring(edidStr.length()-3);
						dbMsg +=",miliStr=" + miliStr;
						pp_pp_et.setText(miliStr);
					}
					myLog(TAG, dbMsg);
				}

				public void onStartTrackingTouch(SeekBar seekBar) {

				}

				public void onProgressChanged(SeekBar seekBar,int progress, boolean fromUser) {
					final String TAG = "onProgressChanged";
					String dbMsg ="[MaraSonActivity.ppSet.pp_seek]";
					try{
						dbMsg= "progress=" + progress + ",fromUser=" + fromUser;
						String edidStr = ORGUT.sdf_mss.format(progress);
						dbMsg +=",edidStr=" + edidStr;
						String mmsStr = edidStr.substring(0, edidStr.length()-5);
						dbMsg +=",mmsStr=" + mmsStr;
						pp_mms_tv.setText(mmsStr);
						String secondStr = edidStr.substring(edidStr.length()-5, edidStr.length()-4);
						dbMsg +=",secondStr=" + secondStr;
						pp_s_et.setText(secondStr);
						String miliStr = edidStr.substring(edidStr.length()-3);
						dbMsg +=",miliStr=" + miliStr;
						pp_pp_et.setText(miliStr);

						dbMsg +=";区間開始/終了区分" + MaraSonActivity.this.repeatPPSyurui;
						switch(MaraSonActivity.this.repeatPPSyurui) {		//区間開始/終了区分
						case R.id.pp_start_rd:		//区間リピート開始ボタン
							dbMsg +=";区間リピート開始";
							MaraSonActivity.this.pp_start = progress;
//							String pp_startStr = ORGUT.sdf_mss.format(MaraSonActivity.this.pp_start).toString();				//二点間再生開始点(mmss000)
//							dbMsg +=", " + pp_startStr;
//							rd_pp_start_tf.setText(pp_startStr);			//二点間再生開始点(mmss000)
							break;
						case R.id.pp_end_rd:		//区間リピート終了ボタン
							dbMsg +=";区間リピート終了";
							MaraSonActivity.this.pp_end = progress;
//							String pp_endStr = ORGUT.sdf_mss.format(MaraSonActivity.this.pp_end).toString();			//二点間再生終了点(mmss000)
//							dbMsg +="～" + pp_endStr + "のリピート";
					//		rd_pp_end_tf.setText(pp_endStr);			//二点間再生終了点(mmss000)
							seekBar.setSecondaryProgress(progress);
							break;
//						default:
//							break;
						}
						myLog(TAG, dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
			    }
			});

			builder.setPositiveButton(getResources().getString(R.string.comon_kakutei), new OnClickListener () {
				public void onClick(DialogInterface dialog, int which) {
					final String TAG = getResources().getString(R.string.comon_kakutei);
					String dbMsg ="[MaraSonActivity.ppSet.builder]";
					try{
						dbMsg +=";二点間=" + MaraSonActivity.this.pp_start + "～" + MaraSonActivity.this.pp_end ;
						if(MaraSonActivity.this.pp_end < MaraSonActivity.this.pp_start){
							int sPoint = MaraSonActivity.this.pp_end;
							MaraSonActivity.this.pp_end = MaraSonActivity.this.pp_start;
							MaraSonActivity.this.pp_start = sPoint;
							dbMsg +=">>" + MaraSonActivity.this.pp_start + "～" + MaraSonActivity.this.pp_end ;
						}
						String mmsStr =  String.valueOf(pp_mms_tv.getText());
						dbMsg="pp_mms_tv = " + pp_mms_tv.getText();
						String secondStr =  String.valueOf(pp_s_et.getText());
						dbMsg +="pp_s_et = " + secondStr;
						String miliStr =  String.valueOf(pp_pp_et.getText());
						int len = miliStr.length();
						dbMsg +=",len=" + len +"文字" ;
						switch(len) {
							case 0:
								miliStr = "000";
								break;
							case 1:
								miliStr = "00" + miliStr;
								break;
							case 2:
								miliStr = "0" + miliStr;
								break;
//							default:
//								break;
						}
						dbMsg +="pp_pp_et = " +miliStr;
						String edidStr = mmsStr + secondStr + " " + miliStr;
						dbMsg +=">>" + edidStr;
						int editInt =ORGUT.reFormartMSS(edidStr);
						dbMsg +="=" + editInt;
						dbMsg +="；epeatPPSyurui=" + MaraSonActivity.this.repeatPPSyurui;
//						if(MaraSonActivity.this.repeatPPSyurui == R.id.pp_start_rd){
//							pp_start = editInt;
////							MaraSonActivity.this.pp_start = editInt;
////							pp_pp_start_tf.setText(edidStr);				//二点間再生開始点
////							MaraSonActivity.this.mcPosition = editInt;
////							myEditor.putString( "pref_nitenkan_start", String.valueOf(editInt));
//						}else if(MaraSonActivity.this.repeatPPSyurui == R.id.pp_end_rd){
//							pp_end = editInt;
//						}
						rp_pp_bicyousei =true;			//2点間リピート設定変更
						ppSetEnd( pp_start , pp_end);		//二点間リピートの調整結果
						myLog(TAG, dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}
			});
			builder.setNegativeButton(getResources().getString(R.string.comon_cyusi), new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					pp_start = 0;
//					MaraSonActivity.this.repeatPPSyurui = false;
				}
			});
			builder.create().show();	// 表示

			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void ppSetEnd(int pp_start , int pp_end){			//二点間リピートの調整結果
		final String TAG = "ppSetEnd";
		String dbMsg="";
		try{
			dbMsg += pp_start +"～"+pp_end;
			if( pp_end < pp_start ){
				int oki= pp_end;
				pp_start = pp_end;
				pp_end = oki;
				dbMsg += ">>" + pp_start+"～"+pp_end;
			}
			MaraSonActivity.this.rp_pp = true;			//2点間リピート中
			myEditor.putBoolean("pref_nitenkan", MaraSonActivity.this.rp_pp);
			myEditor.putString("repeatType", String.valueOf( rp_point));					//リピート再生の種類

			String edidStr = ORGUT.sdf_mss.format(pp_start);
			pp_pp_start_tf.setText(edidStr);				//二点間再生開始点
//			MaraSonActivity.this.mcPosition = pp_start;
			myEditor.putString( "pref_nitenkan_start", String.valueOf(pp_start));
			if(MaraSonActivity.this.saiseiJikan < pp_end){
				pp_end = MaraSonActivity.this.saiseiJikan;
				dbMsg +=">>"+pp_end;////////////////////////////////////////////////////////////////////////////
			}
			MaraSonActivity.this.pp_end = pp_end;
			myEditor.putString( "pref_nitenkan_end", String.valueOf(pp_end));
//			myEditor.putString( "pref_pref_data_url", String.valueOf(dataFN));
			myEditor.putInt( "pref_position", pp_start);
			Boolean kakikomi = myEditor.commit();	// データの保存
			dbMsg +=",kakikomi="+kakikomi;////////////////////////////////////////////////////////////////////////////
			dbMsg +=";"+edidStr;////////////////////////////////////////////////////////////////////////////
			edidStr = ORGUT.sdf_mss.format(pp_end);
			dbMsg +="～"+edidStr;
			pp_pp_end_tf.setText(edidStr);							//二点間再生終了点
			saiseiSeekMP.setSecondaryProgress(pp_end);
			Drawable drawable = getResources().getDrawable(R.drawable.pp_progress);
			saiseiSeekMP.setProgressDrawable(drawable);
			dbMsg +=",getProgress="+saiseiSeekMP.getProgress();////////////////////////////////////////////////////////////////////////////
			MaraSonActivity.this.pp_start = pp_start;
			songIDPTF.setText(String.valueOf((1)));			//リスト中の何曲目か
			dbMsg +=",rp_pp_bicyousei="+ rp_pp_bicyousei;
			int mIndex = getPrefInt("pref_mIndex" , 0 , MaraSonActivity.this);
	//		mData2Service(mIndex);										//サービスにプレイヤー画面のデータを送る
			rp_pp_bicyousei = false;
			onPrepareOptionsMenu(toolbar.getMenu());			//表示直前に行う非表示や非選択設定
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * リピート再生解除
	 *
	 * */
	public void repeatePlaykijyo(){			//リピート再生解除
		final String TAG = "repeatePlaykijyo[MaraSonActivity]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			pp_start = 0;			//リピート区間開始点
			pp_end = 0;				//リピート区間終了点
			rp_pp = false;			//2点間リピート中
			nowList = b_List;				//前に再生していたプレイリスト
			nowList_id = modori_List_id;			//前のプレイリストID
			dbMsg = "戻り["+ nowList_id + "]" + nowList;		/////////////////////////////////////////////////////////////////////////
			myEditor.putString( "nowList", String.valueOf(b_List));
			myEditor.putString( "nowList_id", String.valueOf(modori_List_id));
			myEditor.putBoolean("pref_nitenkan", rp_pp);
			myEditor.putString( "pref_nitenkan_start", String.valueOf(pp_start));
			myEditor.putString( "pref_nitenkan_end", String.valueOf(pp_end));
			Boolean kakikomi = myEditor.commit();	// データの保存
			dbMsg +=",kakikomi="+kakikomi;////////////////////////////////////////////////////////////////////////////
			pp_pp_ll.setVisibility(View.GONE);
			pp_zenkai_ll.setVisibility(View.VISIBLE);						//前回の累積レイアウト
			saiseiSeekMP.setSecondaryProgress(0);
			saiseiSeekMP.setProgressDrawable(dofoltSBDrawable);
			int mIndex = getPrefInt("pref_mIndex" , 0 , MaraSonActivity.this);
		//	mData2Service( mIndex);
			onPrepareOptionsMenu(toolbar.getMenu());			//表示直前に行う非表示や非選択設定
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void kakiutusiArray( List<String> sakiList , List<String> motoList) {	//リストの書き写し
		final String TAG = "kakiutusiArray[MaraSonActivity]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			dbMsg=ORGUT.nowTime(true,true,true) + dbMsg;/////////////////////////////////////
	//		dbMsg +=",motoList=" + motoList.size() +"件";/////////////////////////////////////
			sakiList = new ArrayList<String>();
	//		sakiList.clear();
			sakiList.addAll(motoList);
			dbMsg +=">>sakiList=" + sakiList.size() +"件";/////////////////////////////////////
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public Visualizer mVisualizer;
	public int visualizerType = Visualizer_type_FFT;		//VisualizerはFFT
	public boolean useWaveFormDataCapture = false;
	public boolean useFftDataCapture = false;

	public View toneLayout;
	public TextView td_vol_sum;					//音量合計
	private ArrayList<View> mSeekBars = new ArrayList<View>();
	private VisualizerView mVisualizerView;
	private ViewGroup visualizerVG ;				//Visualizerの割付け先
	private FftView fftView;
	public List<String> pref_toneList;		//プリファレンス保存用トーンリスト
	public String toneSeparata = "L";
	public String tone_name;						//トーン名称
	public boolean bBoot = false;				//バスブート
	public String reverbMei;						//リバーブ効果名称
	public short reverbBangou;						//リバーブ効果番号
	public short maxEQLevel = 1500;
	public short minEQLevel = -1500;
	public int bandSum = 0;					//レベル合計
	public boolean seekFromUser = false;
	public boolean eqHwnkou = false;			//プリセットから変更したか
	public boolean td_level_nomal = true;			//自動音量調整
	public int tone_vol_sum;
	public int ePosition;											//plNameSL.indexOf(tone_name);
	public boolean visualizerAri = false;	//Visualizerを実装できた
	public boolean lyricAri = false;			//歌詞を取得できた
	public boolean lyricSendWeb = false;		//歌詞をwebKitで表示中
	public  String songLyric;					//この曲jの歌詞データ
	public  String lyricEncord;				//歌詞の再エンコード
	public  String lylicHTM = null;				//html変換した歌詞のフルパス名

//	public List<String> plNameSL=null;				//プレイリスト名用簡易リスト
	public Map<String, Object> objMap;				//汎用マップ
	public List<Map<String, Object>> toneList;		//プレイリスト名用リスト
/**
* 音質調整
*/
	public void toneSettie() {	//音質調整
		final String TAG = "toneSettie[MaraSonActivity]";
		String dbMsg= "開始";//音楽のイコライザーを設定する	http://www110.kir.jp/Android/ch0709.html
		try{
			dbMsg=ORGUT.nowTime(true,true,true) + dbMsg;/////////////////////////////////////
			dbMsg= "tone_name=" + tone_name;
			if(tone_name == null){						// || toneList == null
				tone_name = getResources().getString(R.string.tone_name_flat) ;		//フラット
				dbMsg +=">>" + tone_name;
			}
			if( toneList == null ){
				toneList = new ArrayList<Map<String, Object>>();
			}else{
				toneList.clear();
			}
			toneList = tonePriset(MaraSonActivity.this.tone_name);										//プリセット作成
			dbMsg +=" ,toneList=" + toneList;
			LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);		// カスタムビューを設定
			toneLayout = inflater.inflate(R.layout.tone_set, findViewById(R.id.tr_root_ll));			//final View  ?
			AlertDialog.Builder builder = new AlertDialog.Builder(this);			// アラーとダイアログ を生成
			findViewById(R.id.tr_root_ll);
			getLayoutInflater();
			String dTitle = getResources().getString(R.string.menu_item_tone);	//音質調整
			builder.setTitle( dTitle  );
			builder.setView(toneLayout);
			dbMsg +=",toneList=" + toneList.toString();
		//	myLog(TAG,dbMsg);
			mSeekBars.clear();
			for (int i = 0; i < toneList.size(); i++) {
				dbMsg= "(" + i + "/" + toneList.size() + ")";
				int freq = Integer.valueOf(String.valueOf(toneList.get(i).get("tone_Hz")));		//mEqualizer.getCenterFreq((short) i) / 1000;					// イコライザの周波数帯の値を取得
				dbMsg +=",freq=" + String.format("%6dHz", freq);
				short band = Short.valueOf(String.valueOf(toneList.get(i).get("tone_Lev")));		//mEqualizer.getBandLevel((short) i);				// 現在のイコライザのバンドの値を取得
				dbMsg +=",band=" + String.format("%6d", band);

				View layoutView = getLayoutInflater().inflate(R.layout.tone_row, null);				//null
				((ViewGroup) toneLayout).addView(layoutView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				mSeekBars.add(layoutView);
				TextView tr_hz_tv = layoutView.findViewById(R.id.tr_hz_tv);
				tr_hz_tv.setText(String.format("%6dHz", freq));
				TextView tr_lev_tv = layoutView.findViewById(R.id.tr_lev_tv);
				tr_lev_tv.setText(String.format("%6d", band/100));
				//				mSeekBars.add(layoutView);
				SeekBar seekbar = layoutView.findViewById(R.id.tr_seek);
				dbMsg +=",maxEQLevel=" + maxEQLevel + "～" + minEQLevel;
				seekbar.setMax(maxEQLevel- minEQLevel);
				seekbar.setProgress(band + Math.abs( MaraSonActivity.this.minEQLevel));
				seekbar.setTag(i);				// リスナーの中でどのバンドのSeekBarであるか判断するためにタグに値を設定しておく
				tr_lev_tv.setTag(i);
				dbMsg +=",tr_lev_tv=" + tr_lev_tv.getTag(i);
				seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					public void onStopTrackingTouch(SeekBar seekBar) {
						final String TAG = ".onStopTrackingTouch";
						String dbMsg="[MaraSonActivity.seekbar]";
						try{
							MaraSonActivity.this.eqHwnkou = true;			//プリセットから変更したか
							int progress = seekBar.getProgress();
							dbMsg= "progress=" + progress  + ",fromUser=" + MaraSonActivity.this.seekFromUser;				// + ",seekBar=" + seekBar;
							int band = progress- Math.abs( MaraSonActivity.this.minEQLevel);
							dbMsg +=",band=" + band;
							int index = (Integer) seekBar.getTag();
							dbMsg +="(" + index + ")";
							View layout = mSeekBars.get(index);
							TextView textFreq = layout.findViewById(R.id.tr_lev_tv);							// 変更されたSeekBarの値を元に表示とイコライザのバンドの値を更新
							textFreq.setText(String.format("%6d", band/100));
							int freq = Integer.valueOf(String.valueOf(MaraSonActivity.this.toneList.get(index).get("tone_Hz")));		//mEqualizer.getCenterFreq((short) i) / 1000;					// イコライザの周波数帯の値を取得
							dbMsg +=",freq=" + String.format("%6dHz", freq);
							short motband = Short.valueOf(String.valueOf(MaraSonActivity.this.toneList.get(index).get("tone_Lev")));		//mEqualizer.getBandLevel((short) i);				// 現在のイコライザのバンドの値を取得
							dbMsg +=",band=" + String.format("%6d", motband);
						//	setEqBandPart(index , freq , Short.valueOf(String.valueOf(band)) , false);			//イコライザー各バンドの設定変更
							MaraSonActivity.this.toneList.remove(index);
							objMap = new HashMap<String, Object>();				//汎用マップ
							objMap.put("tone_Hz" ,freq );													//高域調整周波数
							objMap.put("tone_Lev" ,band );													//高域調整レベル
							MaraSonActivity.this.toneList.add(index, objMap);
							dbMsg +=">>" + MaraSonActivity.this.toneList.get(index).get("tone_Lev");
							int index2 = index - (MaraSonActivity.this.toneList.size() - 1);
							if(index2 < 0){
								index2 = index2 * (-1);
							}
							dbMsg +=",index2=" + index2;
							MPSIntent = new Intent(getApplication(),MusicPlayerService.class);	//parsonalPBook.thisではメモリーリークが起こる
							MPSIntent.setAction(MusicPlayerService.ACTION_EQUALIZER);
							MPSIntent.putExtra("rdIndex",index2 );				//更新するインデックス
							MPSIntent.putExtra("tone_Hz",freq );				//更新する周波数
							MPSIntent.putExtra("tone_Lev",band );				//更新する調整レベル
							MPSName = startService(MPSIntent);
							dbMsg +=" ,ComponentName=" + MPSName;/////////////////////////////////////
							if( MaraSonActivity.this.seekFromUser ){
								sumEqLevel();									//各バンドのレベル合計
							}
							myLog(TAG, dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG ,  dbMsg + "で" + e);
						}
					}
					public void onStartTrackingTouch(SeekBar seekBar) {			}

					public void onProgressChanged(SeekBar seekBar,int progress, boolean fromUser) {
						final String TAG = ".onProgressChanged";
						String dbMsg="[MaraSonActivity.seekbar]";
						try{
							MaraSonActivity.this.seekFromUser = fromUser;
							myLog(TAG, dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG ,  dbMsg + "で" + e);
						}
				    }
				});
		//		myLog(TAG,dbMsg);
			}			//for (int i = 0; i < bands; i++) {

			Spinner tr_sp = toneLayout.findViewById(R.id.tr_sp);					//プリセットスピナー
			String[] spinnerItems = getResources().getStringArray(R.array.tone_names);											//plNameSL.toArray(new String[plNameSL.size()]);
			for(int i = 0 ; i < spinnerItems.length ; i++){
				String rStr = spinnerItems[i];
				if(rStr.equals(tone_name)){
					ePosition = i;
					dbMsg +=",ePosition=" + ePosition;
				}
			}
	//		myLog(TAG,dbMsg);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerItems );
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			tr_sp.setAdapter(adapter);	// spinner に adapter をセット
			tr_sp.setOnItemSelectedListener(new OnItemSelectedListener() {	// リスナーを登録
				public void onItemSelected(AdapterView<?> parent, View viw, int arg2, long arg3) {	//　アイテムが選択された時
					final String TAG = "tdth_sp.onItemSelected[MaraSonActivity]";
					String dbMsg="開始";/////////////////////////////////////
					try{
						MaraSonActivity.this.ePosition = arg2;
						dbMsg= "position = " + MaraSonActivity.this.ePosition;
						String[] spinnerItems = getResources().getStringArray(R.array.tone_names);											//plNameSL.toArray(new String[plNameSL.size()]);
						MaraSonActivity.this.tone_name = spinnerItems[MaraSonActivity.this.ePosition];			//String.valueOf(plNameSL.get(position));
						if( MaraSonActivity.this.toneList == null ){
							MaraSonActivity.this.toneList = new ArrayList<Map<String, Object>>();
						}else{
							MaraSonActivity.this.toneList.clear();
						}
						toneList = tonePriset(MaraSonActivity.this.tone_name);										//プリセット作成
						dbMsg +="toneList=" + MaraSonActivity.this.toneList.size() +"件" ;
						toneRowsMake( (ViewGroup) toneLayout ,  MaraSonActivity.this.toneList);							//各バンドのレコード記述
						sumEqLevel();									//各バンドのレベル合計
						MaraSonActivity.this.eqHwnkou = false;			//プリセットから変更したか
						myLog(TAG, dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}
				public void onNothingSelected(AdapterView<?> parent) {	//　アイテムが選択されなかった
				}
			});
			dbMsg +=",ePosition=" + ePosition;
			tr_sp.setSelection(ePosition , true);								//☆falseで勝手に動作させない

			toneRowsMake( (ViewGroup) toneLayout ,  toneList);							//各バンドのレコード記述
			View layoutBottm = getLayoutInflater().inflate(R.layout.tone_bottm, null);				//null
			((ViewGroup) toneLayout).addView(layoutBottm, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			ToggleButton td_bb_tb = toneLayout.findViewById(R.id.td_bb_tb);		//BassBoost
			td_bb_tb.setChecked(MaraSonActivity.this.bBoot);
			td_bb_tb.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {					//トグルキーが変更された際に呼び出される
					final String TAG = getResources().getString(R.string.effect_bassbost)+".toneSettie[MaraSonActivity]";
					String dbMsg="開始";/////////////////////////////////////
					try{
						dbMsg="バスブート" + MaraSonActivity.this.bBoot;
						MaraSonActivity.this.bBoot = isChecked;
						dbMsg +=">>" + MaraSonActivity.this.bBoot;
						sharedPref = getSharedPreferences( getResources().getString(R.string.pref_main_file) ,MODE_WORLD_WRITEABLE);		//MODE_WORLD_WRITEABLE 	getSharedPreferences(prefFname,MODE_PRIVATE);
						myEditor = sharedPref.edit();
						myEditor.putBoolean("bBoot",MaraSonActivity.this.bBoot);			//現在の設定
						Boolean kakikomi = myEditor.commit();	// データの保存
						dbMsg +=",書き込み成功="+kakikomi;////////////////////////////////////////////////////////////////////////////
						MPSIntent = new Intent(getApplication(),MusicPlayerService.class);	//parsonalPBook.thisではメモリーリークが起こる
						MPSIntent.setAction(MusicPlayerService.ACTION_BASS_BOOST);
						MPSIntent.putExtra("bBoot",MaraSonActivity.this.bBoot );				//更新するインデックス
						MPSName = startService(MPSIntent);
						dbMsg +=" ,ComponentName=" + MPSName;/////////////////////////////////////
						myLog(TAG, dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}
			});
			td_vol_sum = toneLayout.findViewById(R.id.td_vol_sum);					//音量合計
			Button td_lev_nom_bt = toneLayout.findViewById(R.id.td_lev_nom_bt);		//音量調整
			td_lev_nom_bt.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					final String TAG = getResources().getString(R.string.tone_level_nom)+".toneSettie[MaraSonActivity]";
					String dbMsg="開始";/////////////////////////////////////
					try{
						sumEqLevel();									//各バンドのレベル合計
				//		dbMsg= "toneList" + toneList + ",レベル合計=" + MaraSonActivity.this.bandSum;					//
						dbMsg= "レベル合計=" + MaraSonActivity.this.bandSum;
						int bandCount = toneList.size();
						dbMsg +="bandCount=" + bandCount;
						int avLevel = (MaraSonActivity.this.bandSum) / bandCount;
						dbMsg +=",平均=" + avLevel;					//
						int bandSum = 0;
						for (int i = 0; i < bandCount; i++) {
							dbMsg += "\n(" + i + "/" + toneList.size() + ")";
							int freq = Integer.valueOf(String.valueOf(toneList.get(i).get("tone_Hz")));		//mEqualizer.getCenterFreq((short) i) / 1000;					// イコライザの周波数帯の値を取得
							dbMsg +=",freq=" + String.format("%6dHz", freq);
							int band = Short.valueOf(String.valueOf(toneList.get(i).get("tone_Lev")));		//mEqualizer.getBandLevel((short) i);				// 現在のイコライザのバンドの値を取得
							dbMsg +=",band=" + band;
							band = band - avLevel;
							dbMsg +=">>" + band;
							View layout = mSeekBars.get(i);
							TextView textFreq = layout.findViewById(R.id.tr_lev_tv);							// 変更されたSeekBarの値を元に表示とイコライザのバンドの値を更新
							textFreq.setText(String.format("%6d", band/100));
							SeekBar seekBar = layout.findViewById(R.id.tr_seek);
							seekBar.setProgress(band - minEQLevel);
							MaraSonActivity.this.toneList.remove(i);
							objMap = new HashMap<String, Object>();				//汎用マップ
							objMap.put("tone_Hz" ,freq );													//高域調整周波数
							objMap.put("tone_Lev" ,band );													//高域調整レベル
							MaraSonActivity.this.toneList.add(i, objMap);
							dbMsg +=">>" + MaraSonActivity.this.toneList.get(i).get("tone_Lev");
							int index2 = i - (MaraSonActivity.this.toneList.size() - 1);
							if(index2 < 0){
								index2 = index2 * (-1);
							}
							dbMsg +=",index2=" + index2;
							MPSIntent = new Intent(getApplication(),MusicPlayerService.class);	//parsonalPBook.thisではメモリーリークが起こる
							MPSIntent.setAction(MusicPlayerService.ACTION_EQUALIZER);
							MPSIntent.putExtra("rdIndex",index2 );				//更新するインデックス
							MPSIntent.putExtra("tone_Hz",freq );				//更新する周波数
							MPSIntent.putExtra("tone_Lev",band );				//更新する調整レベル
							MPSName = startService(MPSIntent);
					//		dbMsg +=" ,ComponentName=" + MPSName;/////////////////////////////////////
							dbMsg +=">toneList>" + toneList.get(i).get("tone_Lev");
							bandSum = bandSum+ band;
							dbMsg +=">bandSum>" + bandSum;
						}			//for (int i = 0; i < bands; i++) {
						td_vol_sum.setText(String.format("%6d", bandSum/100));
						myLog(TAG, dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}
			});

			builder.setPositiveButton(getResources().getString(R.string.comon_kakutei), new OnClickListener () {
				public void onClick(DialogInterface dialog, int which) {
					final String TAG = getResources().getString(R.string.comon_kakutei)+".toneSettie[MaraSonActivity]";
					String dbMsg="開始";/////////////////////////////////////
					try{
						dbMsg +=MaraSonActivity.this.tone_name;
						if( MaraSonActivity.this.eqHwnkou ){			//プリセットから変更したか
							MaraSonActivity.this.tone_name = getResources().getString(R.string.tone_name_puri);			//現在の設定
							dbMsg +=">>" + MaraSonActivity.this.tone_name;
						}
						dbMsg +=",toneList=" + toneList;
						int endInt = toneList.size() -1 ;
						MaraSonActivity.this.pref_toneList.clear();
						for (int i = 0; i <= endInt ; i++) {
//						for (int i = endInt; 0 <= i ; i--) {
							dbMsg +="(" + i + "/" + endInt + ")";
							int freq = Integer.valueOf(String.valueOf(toneList.get(i).get("tone_Hz")));						//mEqualizer.getCenterFreq((short) i) / 1000;					// イコライザの周波数帯の値を取得
							dbMsg +=",freq=" + String.format("%6dHz", freq);
							short band =  Short.valueOf(String.valueOf(toneList.get(i).get("tone_Lev")));						//mEqualizer.getBandLevel((short) i);				// 現在のイコライザのバンドの値を取得
							dbMsg +=",band=" + String.format("%6d", band);
							String eStr = freq + toneSeparata+ band;
							MaraSonActivity.this.pref_toneList.add( eStr);
						}			//for (int i = 0; i < bands; i++) {
						dbMsg +=",pref_toneList=" + MaraSonActivity.this.pref_toneList;
						sharedPref = getSharedPreferences( getResources().getString(R.string.pref_main_file) ,MODE_WORLD_WRITEABLE);		//MODE_WORLD_WRITEABLE 	getSharedPreferences(prefFname,MODE_PRIVATE);
						myEditor = sharedPref.edit();
						myEditor.putString("tone_name",MaraSonActivity.this.tone_name);			//現在の設定
						myEditor.putString("pref_toneList", MaraSonActivity.this.pref_toneList.toString());							//再生中のプレイリストID
						myEditor.putBoolean("bBoot",MaraSonActivity.this.bBoot);														//バスブート；現在の設定
						Boolean kakikomi = myEditor.commit();	// データの保存
						dbMsg +=",書き込み成功="+kakikomi;////////////////////////////////////////////////////////////////////////////
						myLog(TAG, dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}
			});
			builder.setNegativeButton(getResources().getString(R.string.comon_cyusi), new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// Cancel ボタンクリック処理
				}
			});
			builder.create().show();	// 表示

			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void setEqBandPart(int index , int freq , short band , boolean seekRed) {			//イコライザー各バンドの設定変更
		final String TAG = "setEqBandPart[MaraSonActivity]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			View layout = mSeekBars.get(index);
			TextView textFreq = layout.findViewById(R.id.tr_lev_tv);							// 変更されたSeekBarの値を元に表示とイコライザのバンドの値を更新
			textFreq.setText(String.format("%6d", band/100));
			if(seekRed){
				SeekBar seekBar = layout.findViewById(R.id.tr_seek);
				seekBar.setProgress(band);
			}

			MaraSonActivity.this.toneList.remove(index);
			objMap = new HashMap<String, Object>();				//汎用マップ
			objMap.put("tone_Hz" ,freq );													//高域調整周波数
			objMap.put("tone_Lev" ,band );													//高域調整レベル
			MaraSonActivity.this.toneList.add(index, objMap);
			dbMsg +=">>" + MaraSonActivity.this.toneList.get(index).get("tone_Lev");
			int index2 = index - (MaraSonActivity.this.toneList.size() - 1);
			if(index2 < 0){
				index2 = index2 * (-1);
			}
			dbMsg +=",index2=" + index2;
			MPSIntent = new Intent(getApplication(),MusicPlayerService.class);	//parsonalPBook.thisではメモリーリークが起こる
			MPSIntent.setAction(MusicPlayerService.ACTION_EQUALIZER);
			MPSIntent.putExtra("rdIndex",index2 );				//更新するインデックス
			MPSIntent.putExtra("tone_Hz",freq );				//更新する周波数
			MPSIntent.putExtra("tone_Lev",band );				//更新する調整レベル
			MPSName = startService(MPSIntent);
			dbMsg +=" ,ComponentName=" + MPSName;/////////////////////////////////////
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void toneRowsMake(ViewGroup layout , List<Map<String, Object>> toneList) {									//各バンドのレコード記述
		final String TAG = "toneRowsMake[MaraSonActivity]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			dbMsg= "toneList" + toneList;
			for (int i = 0; i < toneList.size(); i++) {
				dbMsg= "(" + i + "/" + toneList.size() + ")";
				int freq = Integer.valueOf(String.valueOf(toneList.get(i).get("tone_Hz")));		//mEqualizer.getCenterFreq((short) i) / 1000;					// イコライザの周波数帯の値を取得
				dbMsg +=",freq=" + String.format("%6dHz", freq);
				short band = Short.valueOf(String.valueOf(toneList.get(i).get("tone_Lev")));		//mEqualizer.getBandLevel((short) i);				// 現在のイコライザのバンドの値を取得
				dbMsg +=",band=" + String.format("%6d", band);
				View tView = mSeekBars.get(i);
				SeekBar seekbar = tView.findViewById(R.id.tr_seek);
				seekbar.setMax(maxEQLevel- minEQLevel);
				seekbar.setProgress(band + Math.abs( MaraSonActivity.this.minEQLevel));
				TextView tr_hz_tv = tView.findViewById(R.id.tr_hz_tv);
				tr_hz_tv.setText(String.format("%6dHz", freq));
				TextView textFreq = tView.findViewById(R.id.tr_lev_tv);							// 変更されたSeekBarの値を元に表示とイコライザのバンドの値を更新
				textFreq.setText(String.format("%6d", band/100));
			}			//for (int i = 0; i < bands; i++) {
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void sumEqLevel() {									//各バンドのレベル合計
		final String TAG = "toneRowsMake[MaraSonActivity]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			dbMsg= "toneList" + toneList;
			bandSum = 0;					//レベル合計
			for (int i = 0; i < toneList.size(); i++) {
				dbMsg= "(" + i + "/" + toneList.size() + ")";
				int freq = Integer.valueOf(String.valueOf(toneList.get(i).get("tone_Hz")));		//mEqualizer.getCenterFreq((short) i) / 1000;					// イコライザの周波数帯の値を取得
				dbMsg +=",freq=" + String.format("%6dHz", freq);
				short band = Short.valueOf(String.valueOf(toneList.get(i).get("tone_Lev")));		//mEqualizer.getBandLevel((short) i);				// 現在のイコライザのバンドの値を取得
				dbMsg +=",band=" + String.format("%6d", band);
				bandSum = bandSum+ band;
			}			//for (int i = 0; i < bands; i++) {
			td_vol_sum.setText(String.format("%6d", bandSum/100));
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public List<Map<String, Object>> tonePriset(String tone_name) {										//プリセット作成
		final String TAG = "tonePriset[MaraSonActivity]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			new ArrayList();
			dbMsg= "tone_name= " + tone_name;
			if( pref_toneList == null ){
				pref_toneList =  new ArrayList<String>();				//トーンリストの初期化
			} else {
				pref_toneList.clear();
			}
			if( tone_name.equals(getResources().getString(R.string.tone_name_flat) ) ){	//フラット
				pref_toneList.clear();
				pref_toneList.add("14000L3");
				pref_toneList.add("3600L0");
				pref_toneList.add("910L0");
				pref_toneList.add("230L0");
				pref_toneList.add("60L3");
			}else if( tone_name.equals(getResources().getString(R.string.tone_name_hi_up) ) ){	//高域強調
				pref_toneList.clear();
				pref_toneList.add("14000L800");
				pref_toneList.add("3600L500");
				pref_toneList.add("910L0");
				pref_toneList.add("230L-500");
				pref_toneList.add("60L-800");
			}else if( tone_name.equals(getResources().getString(R.string.tone_name_mid_up) ) ){		//中域強調
				pref_toneList.clear();
				pref_toneList.add("14000L-750");
				pref_toneList.add("3600L500");
				pref_toneList.add("910L500");
				pref_toneList.add("230L500");
				pref_toneList.add("60L-750");
			}else if( tone_name.equals(getResources().getString(R.string.tone_name_low_up) ) ){		//低域強調
				pref_toneList.clear();
				pref_toneList.add("14000L-1000");
				pref_toneList.add("3600L-500");
				pref_toneList.add("910L0");
				pref_toneList.add("230L500");
				pref_toneList.add("60L1000");
			}else if( tone_name.equals(getResources().getString(R.string.tone_name_hilow_up) ) ){		//高低域強調
				pref_toneList.clear();
				pref_toneList.add("14000L600");
				pref_toneList.add("3600L-300");
				pref_toneList.add("910L-500");
				pref_toneList.add("230L-300");
				pref_toneList.add("60L500");
			}else if( tone_name.equals(getResources().getString(R.string.tone_name_puri) ) ){			//現在の設定
				sharedPref = getSharedPreferences( getResources().getString(R.string.pref_main_file) ,MODE_PRIVATE);		//	getSharedPreferences(prefFname,MODE_PRIVATE);
				Map<String, ?> keys = sharedPref.getAll();
				if( sharedPref.contains("pref_toneList") ){
					String stringList = String.valueOf(keys.get("pref_toneList"));											//bundle.getString("list");  //key名が"list"のものを取り出す
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
				}
				dbMsg +=  ",トーン配列=" + pref_toneList ;
				if( pref_toneList == null ){
					pref_toneList.add("14000L300");
					pref_toneList.add("3600L0");
					pref_toneList.add("910L0");
					pref_toneList.add("230L0");
					pref_toneList.add("60L300");
				}
			}
			dbMsg +=",pref_toneList=" + pref_toneList;
			int endInt = pref_toneList.size();
			for (int i = 0 ; i < endInt ; i++) {
				dbMsg +="(" + i + "/" + endInt + ")";
				String rStr = pref_toneList.get(i);
				dbMsg +=",rStr=" + rStr;
				String[] rAttay = rStr.split(toneSeparata);
				int freq = Integer.valueOf(rAttay[0]);						//mEqualizer.getCenterFreq((short) i) / 1000;					// イコライザの周波数帯の値を取得
				dbMsg +=",freq=" + String.format("%6dHz", freq);
				short band =  Short.valueOf(rAttay[1]);						//mEqualizer.getBandLevel((short) i);				// 現在のイコライザのバンドの値を取得
				dbMsg +=",band=" + String.format("%6d", band);
				objMap = new HashMap<String, Object>();				//汎用マップ
				objMap.put("tone_Hz" ,freq );													//高域調整周波数
				objMap.put("tone_Lev" ,band );													//高域調整レベル
				toneList.add( objMap);
			}			//for (int i = 0; i < bands; i++) {
			dbMsg +=",toneList=" + toneList;
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return toneList;
	}

 	public void reverbSettie() {				//リバーブ設定のIF表示
		final String TAG = "reverbSettie[MaraSonActivity]";
		String dbMsg= "開始";//音楽のイコライザーを設定する	http://www110.kir.jp/Android/ch0709.html
		try{
			final CharSequence[] items =getResources().getStringArray(R.array.effect_rev);											//plNameSL.toArray(new CharSequence[plNameSL.size()]);
			dbMsg +=">>"+items.length +"件";/////////////////////////////////////
			AlertDialog.Builder listDlg = new AlertDialog.Builder(this);			// リスト表示用のアラートダイアログ
			String dTitile = getResources().getString(R.string.effect_reverb);			//リバーブ</string>
			listDlg.setTitle(dTitile);
			listDlg.setSingleChoiceItems(items, MaraSonActivity.this.reverbBangou, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {			// ここで選択状態を一時保存しておく
			//		System.out.println("selected theme => " + radioItems[which]);
					final String TAG = "setSingleChoiceItems[reverbSettie.MaraSonActivity]";
					String dbMsg="開始";/////////////////////////////////////
					try{
						dbMsg= "リスト上のインデックス=" + which ;
						final CharSequence[] items =getResources().getStringArray(R.array.effect_rev);
						MaraSonActivity.this.reverbMei =( String.valueOf( items[which]) );						//リバーブ効果名称;0解除				plNameSL.get(which) )
						dbMsg +="、reverbMei=" + MaraSonActivity.this.reverbMei;
						MaraSonActivity.this.reverbBangou = (short) which ;													//リバーブ効果番号
						dbMsg +="、reverbBangou=" + MaraSonActivity.this.reverbBangou;

						MPSIntent = new Intent(getApplication(),MusicPlayerService.class);	//parsonalPBook.thisではメモリーリークが起こる
						MPSIntent.setAction(MusicPlayerService.ACTION_REVERB);
						MPSIntent.putExtra("reverbBangou",MaraSonActivity.this.reverbBangou );				//リバーブ効果番号
						MPSName = startService(MPSIntent);
						dbMsg +=" ,ComponentName=" + MPSName;/////////////////////////////////////
						myLog(TAG, dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}
			});
//☆onClickではクリックしたらダイアログが閉じる
			listDlg.setPositiveButton(getResources().getString(R.string.comon_kakutei), new OnClickListener () {
				public void onClick(DialogInterface dialog, int which) {
					final String TAG = getResources().getString(R.string.comon_kakutei)+".reverbSettie[MaraSonActivity]";
					String dbMsg="開始";/////////////////////////////////////
					try{
						dbMsg +="、reverbBangou=" + MaraSonActivity.this.reverbBangou;
						myEditor = sharedPref.edit();
						myEditor.putString("reverbBangou",String.valueOf(MaraSonActivity.this.reverbBangou));			//リバーブ効果番号
						Boolean kakikomi = myEditor.commit();	// データの保存
						dbMsg +=",書き込み成功="+kakikomi;////////////////////////////////////////////////////////////////////////////
						myLog(TAG, dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}
			});

			listDlg.setNegativeButton(getResources().getString(R.string.comon_cyusi),  new DialogInterface.OnClickListener() {	//中止</string>
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			listDlg.setCancelable(true);
			listDlg.create().show();			// 表示
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}
/**
 * VisualizerのView作成
 * アナログはVisualizerView Class , デジタルはFftView Classで作成
 * 呼出し元	aSetei	reMakeVisualizer	onActivityResult
 * */
	private void makeVisualizer() {					//VisualizerのView作成
		final String TAG = "makeVisualizer";
		String dbMsg= "";
		try{
			LinearLayout visualizer_ll = findViewById(R.id.visualizer_ll);
			float VISUALIZER_HEIGHT_DIP =200.0f;
			switch(visualizerType) {
			case Visualizer_type_wave:			//Visualizerはwave表示
				mVisualizerView = new VisualizerView(this);
				mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,(int) (VISUALIZER_HEIGHT_DIP  * getResources().getDisplayMetrics().density)));
				visualizerVG = visualizer_ll.findViewById(R.id.pp_visualizer);		//Visualizerの割付け先
				dbMsg +=",mVisualizerView=" + mVisualizerView;
				visualizerVG.addView(mVisualizerView);
				dbMsg +=",mLinearLayout=" + visualizerVG;
				break;
			case Visualizer_type_FFT:			//VisualizerはFFT
				fftView = new FftView(this);
				fftView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,(int) (VISUALIZER_HEIGHT_DIP  * getResources().getDisplayMetrics().density)));
				visualizerVG = visualizer_ll.findViewById(R.id.pp_visualizer);
				dbMsg +=",fftView_=" + fftView;
				visualizerVG.addView(fftView);
				dbMsg +=",mLinearLayout=" + visualizerVG;
				break;
			case Visualizer_type_none:			//Visualizerを使わない
				break;
//				default:
//					break;
				}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

 /**
  * MusicPlayerService ClassのMediaPlayerと紐づけられたVisualizerオブジェクトををSerializable経由で受け取り
  * makeVisualizerで作成したVisualizerのViewに渡す
  * 呼出し元	MusicReceiverで
  * */
	private void setupVisualizerFxAndUI(  ) throws IllegalStateException{					//Visualizerの設定
		final String TAG = "setupVisualizerFxAndUI";
		String dbMsg= "";
		try{
			mVisualizer = (Visualizer) ObjectResults.getObject();					//Serializableを使ったオブジェクトの受け渡し
			dbMsg= "mVisualizer=" + mVisualizer;
			if( mVisualizer != null ){
				dbMsg +=",visualizerType=" + visualizerType;
				switch(visualizerType) {
				case Visualizer_type_wave:			//Visualizerはwave表示
					useWaveFormDataCapture = true;
					useFftDataCapture = false;
					break;
				case Visualizer_type_FFT:			//VisualizerはFFT
					useWaveFormDataCapture = false;
					useFftDataCapture = true;
					break;
				case Visualizer_type_none:			//Visualizerを使わない
					useWaveFormDataCapture = false;
					useFftDataCapture = false;
					break;
//					default:
//						break;
				}
				dbMsg +=",Wave=" + useWaveFormDataCapture + ",Fft=" + useFftDataCapture;
				//java.lang.IllegalStateException: getEnabled() called in wrong state: 0

				if(  mVisualizer.getEnabled()){
					mVisualizer.setEnabled(false);					//☆EnabledではsetCaptureSizeでcalled in wrong state: 2
				}
					mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
					dbMsg +=",CaptureSize=" + mVisualizer.getCaptureSize();
					mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {							//キャプチャしたデータを定期的に取得するリスナーを設定
						public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {	//Wave形式のキャプチャーデータ
							final String TAG = "onWaveFormDataCapture[setupVisualizerFxAndUI.MaraSonActivity]";
							String dbMsg= "開始";/////////////////////////////////////
							try{
								dbMsg= "bytes" + bytes +",samplingRate=" + samplingRate;
								mVisualizerView.updateVisualizer(bytes);
								myLog(TAG, dbMsg);
							} catch (Exception e) {
								myErrorLog(TAG ,  dbMsg + "で" + e);
							}
						}

						public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {			//高速フーリエ変換のキャプチャーデータ
							final String TAG = "onFftDataCapture";
							String dbMsg= "[setupVisualizerFxAndUI.MaraSonActivity]";		//		http://asumism.hatenablog.com/entry/2014/02/08/043624
							try{
								dbMsg += "bytes=" + bytes + ",samplingRate=" + samplingRate;
								fftView.update(bytes);
//								myLog(TAG, dbMsg);
							} catch (Exception e) {
								myErrorLog(TAG ,  dbMsg + "で" + e);
							}
						}
					}, Visualizer.getMaxCaptureRate() / 2, 		//キャプチャーデータの取得レート（ミリヘルツ）
						useWaveFormDataCapture,									//onWaveFormDataCaptureを使う
						useFftDataCapture);									//onFftDataCaptureを使う

					mVisualizer.setEnabled(true);
					switch(visualizerType) {
					case Visualizer_type_FFT:			//VisualizerはFFT
						fftView.setSamplingRate(mVisualizer.getSamplingRate());			// サンプリング周波数を事前に教えてあげる
						break;
//					case Visualizer_type_wave:			//Visualizerはwave表示
//					case Visualizer_type_none:			//Visualizerを使わない
//						break;
					default:
						break;
					}
			}
			myLog(TAG, dbMsg);
		}catch (IllegalStateException e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	private void reMakeVisualizer(  ) {					//Visualizerの作り直し
		final String TAG = "reMakeVisualizer[MaraSonActivity]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			dbMsg= "visualizerType=" + visualizerType;
			visualizerVG.removeAllViews();							//設定されているViewGrupを削除
			if(visualizerType != Visualizer_type_none){				//元が使わないでなければ
				makeVisualizer(  );						//VisualizerのView作成
				setupVisualizerFxAndUI();				//Visualizerの設定
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * 歌詞の読出し
	 * 		//http://www.nilab.info/z3/20120806_02.html
	 * */
	public String b_filePath = null;					//読み込み済みのファイル
	private void readLyric( String filepath ) {					//歌詞の読出し
		final String TAG = "readLyric";
		String dbMsg= "";/////////////////////////////////////
		try{
			if(filepath != null){
				if(! filepath.equals(b_filePath)){
					songLyric = getResources().getString(R.string.yomikomi_hunou);		//e="">この曲はタグ情報を読み込めませんでした。</string>
					lyricAri = false;			//歌詞を取得できた
					dbMsg= "filepath=" + filepath;
					Intent intentTB = new Intent(MaraSonActivity.this,TagBrows.class);
					intentTB.putExtra("reqCode",TagBrows.read_USLT);								// 歌詞読み込み
					intentTB.putExtra("filePath",filepath);
					lyricAri = false;			//歌詞を取得できた
					startActivityForResult(intentTB , LyricCheck );								//歌詞の有無確認
					/*		クラスとしての読出し
					//					File sdFile = new File(Environment.getExternalStorageDirectory(), dataFN);
										TagBrows mp3file = new TagBrows(dataFN ,this);
										String wrStr = mp3file.getSongLyric();
										if( wrStr != null){
											songLyric = wrStr;
										}
										lyric_tv.setText(songLyric);
					*/
				}
				b_filePath = filepath;
			}
			songLyric = songLyric + "\n\n" + filepath;
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	private void lyric2web( String hozonnsaki ) {					//歌詞をwebKitに送る
		//http://www.nilab.info/z3/20120806_02.html
		final String TAG = "lyric2web[MaraSonActivity]";
		String dbMsg= "";
		try{
//			String fName = "lyric.htm";
//			String hozonnsaki = "/data/data/" + this.getPackageName() + "/files/" + fName;
//	//		lyric2webSouce( songLyric );					//歌詞をhtmlに書き出す	//		String lyricStr = null;
			dbMsg +=",hozonnsaki=" + hozonnsaki ;/////////////////////////////////////
			Intent intentWV = new Intent(MaraSonActivity.this,wKit.class);			//webでヘルプ表示
			File wFile = new File(hozonnsaki);
			dbMsg +=",exists=" + wFile.exists() ;/////////////////////////////////////

			intentWV.putExtra("dataURI","file://"+ hozonnsaki);		//"file:///android_asset/index.html"
	//		intentWV.putExtra("loadStr",lyricStr);
			intentWV.putExtra("motoFName",hozonnsaki);
			intentWV.putExtra("fType","lyric");
			startActivityForResult(intentWV , LyricWeb );								//192;歌詞のweb表示
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void saiEncord() {										//再エンコード要求
		final String TAG = "saiEncord";
		String dbMsg= "";
		try{
			AlertDialog.Builder listDlg = new AlertDialog.Builder(this);			// リスト表示用のアラートダイアログ
			CharSequence contextTitile = lyricEncord;

			String defoltES = Charset.defaultCharset().name();
			dbMsg= "デフォルトエンコーディングセット= " + defoltES;
			List<String> encList = new ArrayList<String>();									//表示リスト
			encList.add("UTF-16");				//01h: UTF-16 / BOMあり
			encList.add("UTF-16BE");			//(id3v2.4) 02h: UTF-16BE / BOMなし
			encList.add("ISO-8859-1");			//00h: ISO-8859-1
			encList.add("UTF-8");				//id3v2.4) 03h: UTF-8
			final CharSequence[] items = encList.toArray(new CharSequence[encList.size()]);
			listDlg.setTitle(contextTitile );
			listDlg.setItems(items,
				new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {			// リスト選択時の処理, which は、選択されたアイテムのインデックス
					final String TAG = "onClick[saiEncord]";
					String dbMsg="開始";/////////////////////////////////////
					try{
						dbMsg= "現在"+ MaraSonActivity.this.lyricEncord  ;/////////////////////////////////////
						dbMsg +="を(リストの"+ which +"番目の)" ;/////////////////////////////////////
						String saiEncrod = items[which].toString();				//追加先のリスト名
						dbMsg +=saiEncrod + "に" ;/////////////////////////////////////
						dbMsg +=",songLyric=" + MaraSonActivity.this.songLyric.substring(0, 40)  + "～" + MaraSonActivity.this.songLyric.substring(MaraSonActivity.this.songLyric.length()-24) ;/////////////////////////////////////
//						String eucjpStr = new String(MaraSonActivity.this.songLyric.getBytes(MaraSonActivity.this.lyricEncord), saiEncrod);
//						dbMsg +=",eucjpStr=" + eucjpStr.substring(0, 40)  + "～" + eucjpStr.substring(eucjpStr.length()-40, eucjpStr.length()) ;/////////////////////////////////////
						byte[] dataBuffer = songLyric.getBytes(MaraSonActivity.this.lyricEncord);			//songLyric.substring(0, songLyric.length()).getBytes(motoEncrod);									//データ部分を抜出			ISO-8859-1		"EUC_JP"
						if( dataBuffer != null ){
							dbMsg +=",dataBuffer= "+ dataBuffer.length + "バイト";
							songLyric = new String(dataBuffer, saiEncrod);
							lyric_tv.setText(songLyric);
							MaraSonActivity.this.lyricEncord = saiEncrod;
						}
						myLog(TAG, dbMsg);
					} catch (Exception e) {
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
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

///////////////////////////////////////////////////////////////////////////
//ロック画面クライアントからの制御方法	http://www.atmarkit.co.jp/ait/articles/1203/28/news128_3.html	MediaPlayerのリモート操作
//Androidで各種プレイヤーの再生情報を取得する方法		http://mamor-blog.tumblr.com/post/2525335625/android

	//操作系/////////////////////////////////////////////////////////////////////////////////////////////////
	//08-28 22:32:33.626: I/[MaraSonActivity]onPause(8041): mReceiver;isInitialStickyBroadcast=false

	public void paused() {
		final String TAG = "paused";
		String dbMsg= "";
		try{
//			dbMsg +=","+ mcPosition + "/" +saiseiJikan + "[ms]";
			dbMsg +=  "MPSIntent=" + MPSIntent;/////////////////////////////////////
			dbMsg +=".getAction=" + MPSIntent.getAction();/////////////////////////////////////
			MPSIntent.setAction(MusicPlayerService.ACTION_PAUSE);
			dbMsg +=">>" + MPSIntent.getAction();/////////////////////////////////////
			MPSName = startService(MPSIntent);	//startService(new Intent(MusicPlayerService.ACTION_PAUSE));
			dbMsg +=" ,ComponentName=" + MPSName;
			//Timerも停止するので書き換えが起きない
			ppPBT.setImageResource(R.drawable.pl_r_btn);
			ppPBT.setContentDescription(getResources().getText(R.string.pause));
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void playing(int mcPosition) {
		final String TAG = "playing";
		String dbMsg= "";
		try{
			dbMsg +=  "mFilter=" + mFilter ;
			dbMsg +=","+ mcPosition + "/" +saiseiJikan + "[ms]";
			if(mFilter == null){
				psSarviceUri = getPackageName() + getResources().getString(R.string.psSarviceUri);		//プレイヤーサービス	"com.hijiyam_koubou.marasongs.PlayerService";
				dbMsg= ">>psSarviceUri=" + psSarviceUri;/////////////////////////////////////
				mFilter = new IntentFilter();
				mFilter.addAction(MusicPlayerService.ACTION_STATE_CHANGED);
				dbMsg += ">>" + psSarviceUri;/////////////////////////////////////
			}
			dbMsg+= ",MPSIntent=" + MPSIntent;/////////////////////////////////////
			if( MPSIntent == null){
				MPSIntent = new Intent(MaraSonActivity.this, MusicPlayerService.class);
				dbMsg+= ">>" + MPSIntent;/////////////////////////////////////
			}
			MPSIntent.setAction(MusicPlayerService.ACTION_PLAY);
//			dbMsg +=",2点間リピート中="+ rp_pp + ";" + pp_start + "～" + pp_end + "(mcPosition = " + mcPosition +")";/////////////////////////////////////
//			if( rp_pp ){				// || MaraSonActivity.this.requestCode ==rp_point          pp_start != 0 ||
//				mcPosition = pp_start;
//				MPSIntent.putExtra("mcPosition",mcPosition);	//再生ポジション
//				MPSIntent.putExtra("rp_pp",rp_pp);					//2点間リピート中
//				MPSIntent.putExtra("pp_start",pp_start);			//リピート区間開始点
//				MPSIntent.putExtra("pp_end",pp_end);				//リピート区間終了点
//			}
//			String dataFN = getPrefStr( "pref_data_url" ,"" , MaraSonActivity.this);
//			dbMsg +=">> " + mcPosition +"ms)";
//		//	int mIndex = mIndex;			//getPrefInt("mIndex" , 0 , MaraSonActivity.this);
//			sendPlaying( MPSIntent , dataFN , mcPosition , mIndex);						//setされたActionを受け取って再生		<onStopTrackingTouch [aSetei]
			MPSName = startService(MPSIntent);	//startService(new Intent(MusicPlayerService.ACTION_PAUSE));
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	private void readPlaying(int mcPosition) {						//このアクティビティで設定されたデータを渡して再生		<onStopTrackingTouch [aSetei]
		final String TAG = "readPlaying";
		String dbMsg= "";
		try{
			dbMsg += ",MPSIntent=" + MPSIntent;/////////////////////////////////////
			if( MPSIntent == null){
				MPSIntent = new Intent(MaraSonActivity.this, MusicPlayerService.class);
				dbMsg= ">>" + MPSIntent;/////////////////////////////////////
			}
			MPSIntent.setAction(MusicPlayerService.ACTION_PLAY_READ);
			String dataFN = getPrefStr( "pref_data_url" ,"" , MaraSonActivity.this);
//			int mIndex = getPrefInt("pref_mIndex" , 0, MaraSonActivity.this);
			sendPlaying( MPSIntent , dataFN , mcPosition , mIndex);						//setされたActionを受け取って再生		<onStopTrackingTouch [aSetei]
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**リストで選択された曲でプレーヤーを開く*/
	public void sendPlaying( Intent intent ,String dataFN ,int mcPosition ,int mIndex ) {						//setされたActionを受け取って再生		<onStopTrackingTouch [aSetei]
		final String TAG = "sendPlaying";
		String dbMsg= "";
		try{
			dbMsg += dataFN;
			dbMsg +=  "(再生ポジション="+ mcPosition + "/" + saiseiJikan +"[ms]";
			if(saiseiJikan == 0){
				saiseiJikan = getPrefInt("pref_duration" , 0 , MaraSonActivity.this);
				dbMsg +=  ">>" + saiseiJikan +"[ms]";
			}
			intent.putExtra("dataFN",dataFN);
			dbMsg +=","+ mcPosition + "/" +saiseiJikan + "[ms]mIndex=" + mIndex;
			intent.putExtra("mcPosition",mcPosition);	//再生ポジション
			intent.putExtra("saiseiJikan",saiseiJikan);
			intent.putExtra("mIndex",mIndex);
			intent.putExtra("pref_lockscreen",pref_lockscreen);
			intent.putExtra("pref_notifplayer",pref_notifplayer);
			MPSName = startService(intent);
			dbMsg +=" ,ComponentName=" + MPSName;/////////////////////////////////////
			ppPBT.setImageResource(R.drawable.pousebtn);
			ppPBT.setContentDescription(getResources().getText(R.string.play));
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void stopped() {															//stop2reset
		final String TAG = "stopped";
		String dbMsg= "";
		try{
			dbMsg=  "MPSIntent=" + MPSIntent;/////////////////////////////////////
			MPSIntent.setAction(MusicPlayerService.ACTION_STOP);
			MPSName = startService(MPSIntent);	//Intent intent = new Intent(MusicPlayerService.ACTION_STOP);
			ppPBT.setImageResource(R.drawable.pl_r_btn);
			ppPBT.setContentDescription(getResources().getText(R.string.pause));
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

/*Implicit intents with startService are not safe: Intent { act=com.example.android.remotecontrol.ACTION_PAUSE flg=0x10 (has extras) } android.content.ContextWrapper.startService:494 com.hijiyam_koubou.marasongs.MaraSonActivity.paused:2200 com.hijiyam_koubou.marasongs.MaraSonActivity$1$1.run:611
*/
	/**
	 *  リストに戻る
	 * @param reqCode	選択されたもの
	 * */
	@SuppressLint("Range")
	public void callListView(int reqCode , String selName , int mcPosition ){								//リストビューを読出し
		final String TAG = "callListView";
		String dbMsg= "";
		try{
			dbMsg +=ORGUT.nowTime(true,true,true);
//			Intent intent = new Intent(MaraSonActivity.this, MuList.class);			//
//			intent.putExtra("requestCode",imanoJyoutai);
////			intent.putExtra("resultCode",imanoJyoutai);
//			dbMsg +="reqCode="+ reqCode;//;アルバム=195
//			intent.putExtra("reqCode",reqCode);
//			dbMsg +="[プレイリストID= " + nowList_id;
//			intent.putExtra("nowList_id", nowList_id);
//			dbMsg +="]" + nowList;//			// 全曲リスト
//			intent.putExtra("nowList", nowList);
//			if( nowList_data != null ){
//				dbMsg +=",保存場所= " + nowList_data;//////////////////////////////////
//				intent.putExtra("nowList_data", nowList_data);
//			}
//			int mIndex = getPrefInt("pref_mIndex" , 0, MaraSonActivity.this);
//			dbMsg +="[mIndex="+ mIndex;			// リストの一曲目=0
//			int rINdex = Item.getMPItem(selName);
//			if(mIndex != rINdex ){
//				mIndex = rINdex;
//				dbMsg +=">>"+ mIndex;			// リストの一曲目=0
//			}
		//	if(mIndex < 0){			//20200419:全曲リストの作成失敗対策
//				mIndex = 0;
//				dbMsg +=">>"+ mIndex;			// リストの一曲目=0
				quitMe();
		//	}
//			intent.putExtra("mIndex",mIndex);
//			dbMsg +="]選択アイテム="+selName;	//			// ]選択アイテム=10cc
//			if(selName ==null){
//				selName="";
//			}
//			if( reqCode == MuList.MENU_MUSCK_PLIST ){
//				selName = titolName;
//				dbMsg +=">>="+selName;
//			}
//			intent.putExtra("senntakuItem",selName);
//			if(-1 < mIndex){
//				Cursor playingItem = musicPlaylist.getPlaylistItems(nowList_id, mIndex);
//				creditArtistName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST));        //playingItem.artist;	//クレジットされているアーティスト名
//				dbMsg += " ,クレジット⁼ " + creditArtistName;
//				albumName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.ALBUM));        //playingItem.album;			//アルバム名
//				dbMsg += " , アルバム⁼" + albumName;/////////////////////////////////////	this.album = album;
//				titolName = playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE));        //playingItem.title;		//曲名
//				dbMsg += " ,タイトル= " + titolName;/////////////////////////////////////		this.title = title;
//				@SuppressLint("Range") int audioId = Integer.parseInt(playingItem.getString(playingItem.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)));
//				dbMsg += " ,audioId= " + audioId;
//				playingItem.close();
//				albumArtist = musicPlaylist.getAlbumArtist(audioId , MaraSonActivity.this);
//				dbMsg +=" ,アルバムアーティスト= " + albumArtist;
//			}
//
//			intent.putExtra("creditArtistName",creditArtistName);					//クレジットされているアーティスト名
//			intent.putExtra("albumArtist",albumArtist);					//アルバムアーティスト	albumArtist
//			intent.putExtra("albumName",albumName);									//アルバム名
//			intent.putExtra("titolName",titolName);									//曲名
////			dbMsg += ",アルバムアートのURI=" + albumArt;								// アルバムアートのURI=null,
////			intent.putExtra("albumArt",albumArt);
//			dbMsg +=",mcPosition= " + mcPosition;											// ,mcPosition= 0
//			intent.putExtra("mcPosition", mcPosition);
//			dbMsg +=",再生中か= " + IsPlaying;												// ,再生中か= false,
//			intent.putExtra("IsPlaying", IsPlaying);
//			dbMsg +=",生成中= " + IsSeisei;			// 生成中= false
//			intent.putExtra("IsSeisei", IsSeisei);
//			setResult(RESULT_OK, intent);                //20190518
//			if(IsPlaying){
//				MaraSonActivity.this.finish();     //			startActivityForResult(intent , reqCode );										//200;プレイヤーを表示
//			} else{
//				quitMe();
//			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void callListTitolList(){
		final String TAG = "callListTitolList";
		String dbMsg= "";
		try{
			titolName =titol_tv.getText().toString();		//曲名
			dbMsg += ".titolName="+ titolName;
			String dataFN = getPrefStr( "pref_data_url" ,"" , MaraSonActivity.this);
			dbMsg += ".dataFN="+ dataFN;
			int mcPosition = saiseiSeekMP.getProgress();
			dbMsg += ".mcPosition="+ mcPosition;
			callListView(v_titol ,dataFN , mcPosition );								//リストビューを読出し
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		boolean retBool= false;
		final String TAG = "dispatchKeyEvent";
		String dbMsg= "";
		dbMsg += ORGUT.nowTime(true,true,true);/////////////////////////////////////
		try{
			int keyCode =  event.getKeyCode();
			dbMsg += " , keyCode=" + keyCode + " , Action=" +event.getAction();
			switch (keyCode) {
				case KeyEvent.KEYCODE_BACK:	//4；KEYCODE_BACK :keyCode；09SH: keyCode；4,event=KeyEvent{action=0 code=4 repeat=0 meta=0 scancode=158 mFlags=72}
					if(event.getAction() == KeyEvent.ACTION_DOWN){					//0;二重発生の防止
						callListTitolList();
					}
					break;
				case KeyEvent.KEYCODE_DPAD_UP:		//マルチガイド上；19
				case KeyEvent.KEYCODE_DPAD_DOWN:	//マルチガイド下；20
				case KeyEvent.KEYCODE_DPAD_LEFT:	//マルチガイド左；21
				case KeyEvent.KEYCODE_DPAD_RIGHT:	//マルチガイド右；22
				case KeyEvent.KEYCODE_DPAD_CENTER:	//決定ボタン；23  ★ここではなくonCkickで処理される
					if(! prTT_dpad){					//d-pad有りか
						setKeyAri();									//d-pad対応
					}
					break;
				default:
					 retBool= false;			//指定したキー以外はデフォルト動作
					break;
			}
			dbMsg += "retBool=" + retBool;///////////////////////////////////////////////////////////////////
			myLog(TAG, dbMsg);
		} catch (NullPointerException e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return retBool;
	}

	//	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final String TAG = "dispatchTouchEvent";
		String dbMsg= "";
		try{
			dbMsg += ORGUT.nowTime(true,true,true);
			dbMsg += " , Action=" +event.getAction();
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return gestureDetector.onTouchEvent(event)
				|| super.dispatchTouchEvent(event);
	}

	@Override
	public void onClick(View v) {																		//操作対応②ⅰ
		final String TAG = "onClick";
		String dbMsg= "";
		try{
			dbMsg +=ORGUT.nowTime(true,true,true);/////////////////////////////////////
//			String dataFN = getPrefStr( "pref_data_url" ,"" , MaraSonActivity.this);
//			b_dataFN = dataFN;
			b_album = albumName;			//それまで参照していたアルバム名
	//		int mcPosition = saiseiSeekMP.getProgress();
			int saiseiJikan = saiseiSeekMP.getMax();
			dbMsg += ",現在="+mcPosition + "/" + saiseiJikan + "[ms]";/////saiseiJikan　⁼0？
			dbMsg += " , クリックされたのは" + v.getId();/////////////////////////////////////

			if (v == ppPBT) {
				dbMsg += " 、ppPBT;Description="+ ppPBT.getContentDescription();/////////////////////////////////////
//				dbMsg +=  ",生成中= "+ IsSeisei;
				dbMsg += " 、IsPlaying="+ IsPlaying;
				if (ppPBT.getContentDescription().equals(getResources().getText(R.string.play)) || IsPlaying) {      //getContentDescription だけでは誤動作？20191112
					dbMsg += " 、pausedへ";
					paused();
					ppPBT.setContentDescription(getResources().getText(R.string.pause));
				} else {
					dbMsg += " 、mcPosition=" + mcPosition;
					dbMsg += " 、playingへ";
					playing(mcPosition);
					ppPBT.setContentDescription(getResources().getText(R.string.play));
				}
			} else if (v == ffPBT) {
				if( pp_start != 0 ){				// || MaraSonActivity.this.requestCode ==rp_point
					mcPosition = pp_start;
				}else{
					mcPosition =0;									//前に再生していた曲の再生ポジションを消去
				}
				 saiseiSeekMP.setProgress(mcPosition);
				 saiseiSeekMP.setMax(saiseiJikan);
			//	ppPBT.setContentDescription(getResources().getText(R.string.play));			//処理後は再生
				imanoJyoutai =  MuList.chyangeSong;
				MPSIntent.setAction(MusicPlayerService.ACTION_SKIP);
				MPSName = startService(MPSIntent);		//onStartCommandへ	//startService(new Intent(MusicPlayerService.ACTION_SKIP));
				dbMsg +=" ,ComponentName=" + MPSName.toString();/////////////////////////////////////
			} else if (v == rewPBT) {				//mButtonRewind
//				dbMsg += ",rewPBT(" + mIndex +")";
				if( pp_start != 0 ){				// || MaraSonActivity.this.requestCode ==rp_point
					mcPosition = pp_start;
				}else{
					mcPosition =0;									//前に再生していた曲の再生ポジションを消去
				}
				saiseiSeekMP.setProgress(mcPosition);
				saiseiSeekMP.setMax(saiseiJikan);
		//		ppPBT.setContentDescription(getResources().getText(R.string.play));			//処理後は再生
				imanoJyoutai =  MuList.chyangeSong;
				MPSIntent.setAction(MusicPlayerService.ACTION_REWIND);
				MPSName = startService(MPSIntent);	//startService(new Intent(MusicPlayerService.ACTION_REWIND));
				dbMsg +=" ,ComponentName=" + MPSName.toString();/////////////////////////////////////
			} else if (v == vol_btn) {								//ボリュームボタン
				dbMsg += ",vol_btn";
				nowVol = audioManage.getStreamVolume(AudioManager.STREAM_MUSIC);			//現在の音楽音量
				dbMsg +=",nowVol=" + nowVol;//0～15/////////////////////////////
				if(0< nowVol){
					audioManage.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);			//起動時の音楽音量に戻す
					vol_btn.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
				}else{
					if( nowVol == 0 ){
						if( startVol == 0 ){
							nowVol = 10;
						}else{
							nowVol = startVol;
						}
						audioManage.setStreamVolume(AudioManager.STREAM_MUSIC, nowVol, 0);			//起動時の音楽音量に戻す
					}
					audioManage.setStreamMute(AudioManager.STREAM_MUSIC, false);
					vol_btn.setImageResource(android.R.drawable.ic_lock_silent_mode);			//消音中
				}
				nowVol = audioManage.getStreamVolume(AudioManager.STREAM_MUSIC);			//現在の音楽音量
				dbMsg +=">>" + nowVol;//0～15/////////////////////////////
				vol_tf.setText(String.valueOf(nowVol));//音量表示
			} else if (v == artist_tv) {					//mButtonStop
				maenoArtist =albumArtist;	//前に再生していたアルバムアーティスト名
				dbMsg+=albumArtist + "を選択してアーティスト選択リストを表示";
				reqCode = MyConstants.v_artist;
				callListView(MyConstants.v_artist ,albumArtist ,mcPosition);							//リストビュー読出し
			} else if (v == alubum_tv) {					//mButtonStop
				dbMsg+=  "alubum_tv設定";
				albumName =alubum_tv.getText().toString();		//アルバム名
				reqCode = MyConstants.v_alubum;
//				if(albumName != null){
//					if(albumName.equals("")){
//						albumName = playingItem.album;					//選択しておくアイテムアイテム
//					}
//				}else{
//					albumName = playingItem.album;					//選択しておくアイテムアイテム
//				}
				callListView(v_alubum ,albumName  ,mcPosition);						//リストビュー読出し
			} else if (v == titol_tv) {					//mButtonStop
				dbMsg+=  "titol_tv設定";
				reqCode = MyConstants.v_titol;
				callListTitolList();
			}
			dbMsg +=" ,mcPosition=" + mcPosition + "/" + saiseiJikan + "[ms]";
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	@Override
	public boolean onLongClick(View v) {
		final String TAG = "onLongClick";
		String dbMsg= "";/////////////////////////////////////
		try{
			dbMsg= "View=" + v.getId();
			if (v == vol_btn) {
//				nowVol = audioManage.getStreamVolume(AudioManager.STREAM_MUSIC);			//現在の音楽音量
//				dbMsg= "nowVol=" + nowVol;//0～15/////////////////////////////
////				vol_btn.setSelected(false);					//TextView,ImageButton,Spinnerは.setSelected(true);
//////			vol_btn.setFocusable(false);					//TextView,ImageButton,Spinnerは.setSelected(true);
//////			vol_btn.setFocusableInTouchMode(false);
////			vol_btn.requestFocus();
//		//		vol_btn.clearFocus();			//これだけではフォーカスが飛ぶ
				setVol();				//音量設定
//				audioManage.setStreamVolume(AudioManager.STREAM_MUSIC, nowVol, AudioManager.FLAG_SHOW_UI);
//				Thread.sleep(4000); // 4秒待つ
//				nowVol = audioManage.getStreamVolume(AudioManager.STREAM_MUSIC);			//現在の音楽音量
//				dbMsg +=">>" + nowVol;//0～15/////////////////////////////
//				myLog(TAG,dbMsg);
//				vol_tf.setText(String.valueOf(nowVol));;												//音量表示
				return true;
//			} else if (v == lyric_tv) {										//歌詞表示
//				lyric2web( songLyric );				//歌詞をwebKitに送る
//				return true;
			}else if(v == pp_pp_start_tf){				//二点間再生開始点
				ppSet(R.id.pp_start_rd);								//二点間リピートの調整
				return true;
			}else if(v == pp_pp_end_tf){				//二点間再生終了点
				ppSet(R.id.pp_end_rd);								//二点間リピートの調整
				return true;
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return false;
	}

	/**
	 * ViewFlipperを左右動作させる
	 * */
	public int flipper = 0;					//ジャケットを0として右にフリックで++,左にフリックで減算
	@Override
	public final boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX, final float velocityY) {
		final String TAG = "onFling";
		String dbMsg= "";
		try{
//			dbMsg += ",velocityX=" + velocityX + ",velocityY=" + velocityY;			//velocityX=10339.762,velocityY=-1579.5083
			float dx = Math.abs(e1.getX() - e2.getX());
			float dy = Math.abs(e1.getY() - e2.getY());
//			dbMsg += ",dx=" + dx + ",dy=" + dy;										//,dx=372.7354,dy=37.109497,
			if (dx > dy) {
				String dataFN = getPrefStr( "pref_data_url" ,"" , MaraSonActivity.this);
				dbMsg += ",velocityX=" + velocityX;									//velocityX=10339.762,
				if (velocityX > 0) {
					pp_vf.setInAnimation(slideInFromLeft);
					pp_vf.showPrevious();
				} else {
					pp_vf.setInAnimation(slideInFromRight);
					pp_vf.showNext();
				}
				int nextVF = pp_vf.getDisplayedChild();
				dbMsg +=",表示" + nextVF + "/" + pp_vf.getChildCount()+ "枚目";		//2		0始まりで何枚目か
				switch( nextVF ) {		//
				case 0:		//"ジャケット	mpJakeImg.getId();						//2131624054
					dbMsg +="ジャケット=" + mpJakeImg.getId();		//2131624129
					break;
				case 1:		//"ビジュアライザー						//2131624129
					int checkResalt = checkSelfPermission(Manifest.permission.RECORD_AUDIO);	//許可されていなければ -1 いれば 0
					dbMsg += "=" + checkResalt;
					if ( checkResalt != PackageManager.PERMISSION_GRANTED ) {  //許可されていなければ
						if (velocityX > 0) {
							pp_vf.showPrevious();
						} else {
							pp_vf.showNext();
						}
					}
					dbMsg +=",visualizerType=" + visualizerType;		//2131624129
					if(visualizerType < Visualizer_type_none){
						visualizerType = Visualizer_type_FFT;
						myEditor.putInt ("visualizerType", visualizerType);
					}
					if( (dataFN != null && ! dataFN.equals("")) ){                                //|| visualizerType == Visualizer_type_none
//						dbMsg +=",ビジュアライザーベース=" + visualizerVG.getId();		//2131624129
						dbMsg +=",アナログビジュアライザー=" + mVisualizerView;/////////////////////////////////////
						dbMsg +=",FFTビジュアライザー=" + fftView;/////////////////////////////////////
						if( mVisualizerView == null && fftView == null ){
							makeVisualizer();						//VisualizerのView作成
							dbMsg +=">>アナログ>>" + mVisualizerView;/////////////////////////////////////
							dbMsg +=">>FFT>>" + fftView;/////////////////////////////////////
						}
						dbMsg +=",mVisualizer=" + mVisualizer;		//2131624129
						setupVisualizerFxAndUI();				//Visualizerの設定
						if( mVisualizer != null ){
						} else {
							pp_vf.showNext();
						}
					} else {
						pp_vf.showNext();
					}
					break;
				case 2:		//"歌詞表示=" + lyric_tv.getId();							//2131624055
					dbMsg +=",歌詞表示=" + lyric_tv.getId();													//2131624055
					dbMsg +=",isShown=" + lyric_tv.isShown();		//常に0
					readLyric( dataFN );					//歌詞の読出し
					break;
				default:								//最初はここから
					break;
				}

				dbMsg +=",getChildAt.getId()=" + pp_vf.getChildAt(pp_vf.getDisplayedChild()).getId();		//2
			return true;
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return false;
	}

	@Override
	public boolean onDown(MotionEvent me) {
		final String TAG = "onDown";
		String dbMsg= "";
		try{
			dbMsg += "MotionEvent=" + me ;///////////////////////////////////
//MotionEvent { action=ACTION_DOWN, id[0]=0, x[0]=305.0, y[0]=805.0, toolType[0]=TOOL_TYPE_FINGER, buttonState=0, metaState=0, flags=0x0, edgeFlags=0x0, pointerCount=1, historySize=0,
//eventTime=21487376, downTime=21487376, deviceId=5, source=0x1002 }
//			int CFvIDthis = this.getCurrentFocus().getId();
//			dbMsg +=",getCurrentFocus=" + CFvIDthis;/////////////////////////////////////
//			dbMsg +="ViewFlipper=" + pp_vf.getId();///////////////////////////////////// = ()
//			dbMsg +="ジャケット=" + mpJakeImg.getId();/////////////////////////////////////
//			dbMsg +="歌詞表示=" + lyric_tv.getId();/////////////////////////////////////
//			dbMsg +="ビジュアライザーベース=" + visualizerVG.getId();/////////////////////////////////////
//
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	return false;
	}

	@Override
	public void onShowPress(MotionEvent me) {
		final String TAG = "onShowPress";
		String dbMsg= "";
		try{
			dbMsg="MotionEvent=" + me ;
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	@Override
	public boolean onSingleTapUp(MotionEvent me) {
		final String TAG = "onSingleTapUp";
		String dbMsg= "";
		try{
//			dbMsg +=" ,mcPosition=" + mcPosition + "/" + saiseiJikan + "[ms]";
			dbMsg +="MotionEvent=" + me ;
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,float distanceY) {
		final String TAG = "onScroll";
		String dbMsg= "";/////////////////////////////////////
		try{
			dbMsg +="MotionEvent1=" + e1 ;/////////////////////////////////////
			dbMsg +=",MotionEvent2=" + e2 ;/////////////////////////////////////
			dbMsg +=",distanceX=" + distanceX ;/////distanceX=-49.9375
///*MotionEvent1=MotionEvent { action=ACTION_DOWN, id[0]=0, x[0]=305.0, y[0]=805.0, toolType[0]=TOOL_TYPE_FINGER, buttonState=0, metaState=0, flags=0x0, edgeFlags=0x0,
// *  pointerCount=1, historySize=0, eventTime=21487376, downTime=21487376, deviceId=5, source=0x1002 },
// * MotionEvent2=MotionEvent { action=ACTION_MOVE, id[0]=0, x[0]=354.9375, y[0]=803.18884, toolType[0]=TOOL_TYPE_FINGER, buttonState=0, metaState=0, flags=0x0, edgeFlags=0x0,
// *  pointerCount=1, historySize=1, eventTime=21487435, downTime=21487376, deviceId=5, source=0x1002 },
//*/
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent me) {
		final String TAG = "onLongPress";
		String dbMsg= "";
		try{
			dbMsg="MotionEvent=" + me ;
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

//	dbMsg=  "saiseiSeekMP設定";///////	public void onSeekBarChangeListener(SeekBar seekBar) {
	//	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {		// トラッキング開始時に呼び出されます
		final String TAG = "onStartTrackingTouch[saiseiSeekMP]";
		String dbMsg= "";/////////////////////////////////////
		try{//戻し操作
//			dbMsg +=" ,mcPosition=" + mcPosition + "/" + saiseiJikan + "[ms]";
			dbMsg +=",seekBar=" + seekBar.getProgress()+"/"+saiseiJikan +"mS]";//////////////////////////////////////
			if (ppPBT.getContentDescription().equals(getResources().getText(R.string.play))) {			//isPlaying
				dbMsg += " 、Description="+ ppPBT.getContentDescription();/////////////////////////////////////
		//		receiverHaki();															//レシーバーを破棄
				paused();
			}
			receiverHaki();
			dbMsg += ">>"+ ppPBT.getContentDescription().toString();///////////////////////////////////
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {	// トラッキング中に呼び出されます☆ここが呼ばれる//☆キー操作為操作以外でも動いてしまえば発生
		final String TAG = "onProgressChanged[saiseiSeekMP]";
		String dbMsg= "";/////////////////////////////////////
		try{
			dbMsg +="再生ポジション=" + seekBar.getProgress()+"/"+saiseiJikan +"mS]";//////////////////////////////////////
			dbMsg +="progress"+ progress + ",fromTouch=" + fromTouch;/////////////////////////////////////
			saiseiPositionPTF.setText(ORGUT.sdf_mss.format(saiseiSeekMP.getProgress()));	   		//再生画面の経過時間枠
//			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * トラッキング終了時に呼び出されます
	 * レシーバ生成
	 * */
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		final String TAG = "onStopTrackingTouch";
		String dbMsg= "";/////////////////////////////////////
		try{
			receiverSeisei();		//レシーバーを生成☆onStopで破棄しないとleaked発生
//			dbMsg += "seekBar=" + seekBar.getId()+"("+saiseiSeekMP.getId() +")";//////////////////////////////////////
			dbMsg += "getKeyDispatcherState=" + seekBar.getKeyDispatcherState().toString();//////////////////////////////////////
			int mcPosition = saiseiSeekMP.getProgress();					///seekBar.getProgress();
			int saiseiJikan  = saiseiSeekMP.getMax();
			dbMsg +=" ,mcPosition=" + mcPosition + "/" + saiseiJikan + "[ms]";
			setPrefInt("pref_position" ,  mcPosition , MaraSonActivity.this);

			CharSequence btStre = ppPBT.getContentDescription();
			dbMsg +=",ppPBT=" + btStre;
			dbMsg += " 、Description="+ ppPBT.getContentDescription();/////////////////////////////////////
			if (ppPBT.getContentDescription().equals(getResources().getText(R.string.pause))) {	//isPlaying
	//			receiverSeisei();	//レシーバーを生成
				readPlaying( mcPosition );						//このアクティビティで設定されたデータを渡して再生
			}
			dbMsg += ">>"+ ppPBT.getContentDescription().toString();
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}			//onStopTrackingTouch(SeekBar seekBar)

	int b_focusItemID = 0;
	int b_Action = 9;
	public int renzoku;
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		final String TAG = "onKey";
		String dbMsg= "";
		try{
			dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;
			int mcPosition = saiseiSeekMP.getProgress();
			int saiseiJikan = saiseiSeekMP.getMax();
			dbMsg += ",現在="+mcPosition + "/" + saiseiJikan + "[ms]";////////////////////////////////////////////////////////////////////////////
			dbMsg="keyCode="+keyCode+",event="+event.toString()+"が"+ORGUT.nowTime(true,true,true)+"発生";
			dbMsg +=",getKeyCode="+event.getKeyCode();
			View currentFo = this.getCurrentFocus();
			int focusItemID= 0 ;
			if(currentFo != null){
				focusItemID=currentFo.getId();
				dbMsg +="focusItemID="+focusItemID;///////////////////////////////////////////////////////////////////
			}
			if (focusItemID== saiseiSeekMP.getId()){					//シークバー
				dbMsg +="；； "+b_focusItemID ;///////////////////////////
				dbMsg += ">>"+event.getKeyCode() +"を"+event.getAction() ;///////////////////////////
				CharSequence btStre = ppPBT.getContentDescription();
				dbMsg +=",ボタンの状態は" + btStre;
				dbMsg +="<< "+b_Action ;///////////////////////////
				dbMsg += ",isTracking="+event.isTracking();///////////////////////////
				if( event.getKeyCode() ==KeyEvent.KEYCODE_DPAD_LEFT ||  event.getKeyCode() ==KeyEvent.KEYCODE_DPAD_RIGHT ){
					if(event.getAction() == KeyEvent.ACTION_DOWN){					//ACTION_UP =0
//							mcPosition = saiseiSeekMP.getProgress();					///seekBar.getProgress();
//							dbMsg +="[再生ポジション=" + mcPosition;/////////////////////////////////////
							dbMsg += "/"+saiseiJikan +"mS]";/////////////////////////////////////
//							//第三引数fromTouchはタッチ操作でtrue それ以外はfalse
						if (btStre.equals(getResources().getText(R.string.play))) {	//再生中なら
//							onStartTrackingTouch(saiseiSeekMP); 	// トラッキング開始時に呼び出されます	//			myLog(TAG,dbMsg);
						}
					} else if(event.getAction() == KeyEvent.ACTION_UP){					//ACTION_UP =1
						if (btStre.equals(getResources().getText(R.string.pause))) {	//再生中なら
							onStopTrackingTouch(saiseiSeekMP);								// トラッキング終了時に呼び出されます//				if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT ){
						}
					}
				}
			} else if (focusItemID== ppPBT.getId()){
				dbMsg= "再生ボタン="+ppPBT.getId() ;///////////////////////////
				dbMsg += ", IsPlaying="+IsPlaying ;///////////////////////////
			} else if (focusItemID== vol_btn.getId()){
				dbMsg +="ボリュームボタン="+vol_btn.getId() ;
				dbMsg += ", event="+event ;///////////////////////////
				if( keyCode == KeyEvent.KEYCODE_DPAD_CENTER ){
					if( 1 ==event.getAction()){					//ACTION_UP,
						dbMsg += ", renzoku="+renzoku ;///////////////////////////
						myLog(TAG,dbMsg);
						if( 0 < renzoku ){
							onLongClick(vol_btn);
						} else {
							onClick(vol_btn);
						}
					} else {
						renzoku = event.getRepeatCount();
					}
					return true;					//イベントをここで止める
				}
//			} else if (focusItemID== adViewC.getId()){
//				dbMsg= "広告表示エリア="+adViewC.getId() ;
//				myLog(TAG,dbMsg);
//				switch (event.getKeyCode()) {
//				case KeyEvent.KEYCODE_DPAD_DOWN:
//					dbMsg= "でDPAD_DOWN" ;
//					if(event.getAction() == KeyEvent.ACTION_DOWN){					//ACTION_UP =0
//						dbMsg= "をDOWN" ;
//						//GADBannerView
//
//						plistDPTF.setSelected(true);
//						plistDPTF.setFocusable(true);
//						plistDPTF.setFocusableInTouchMode(true);
//						plistDPTF.requestFocus();
//					}
//					break;
//				case KeyEvent.KEYCODE_DPAD_CENTER:
//					dbMsg= "でDPADCENTER" ;
//					if(event.getAction() == KeyEvent.ACTION_DOWN){					//ACTION_UP =0
//						dbMsg= "をDOWN" ;
//						adViewC.setSelected(true);
//						adViewC.requestFocus();
//					}
//					break;
//				}
			} else {
				switch (event.getKeyCode()) {
				case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
				case KeyEvent.KEYCODE_HEADSETHOOK:
//					onClick(ppPBT);		//再生ボタン
					return true;
				case KeyEvent.KEYCODE_ENDCALL:	//4；KEYCODE_BACK :keyCode；09SH: keyCode；4,event=KeyEvent{action=0 code=4 repeat=0 meta=0 scancode=158 mFlags=72}
				case KeyEvent.KEYCODE_BACK:	//4；KEYCODE_BACK :keyCode；09SH: keyCode；4,event=KeyEvent{action=0 code=4 repeat=0 meta=0 scancode=158 mFlags=72}
		//			audioManage.setStreamMute(AudioManager.STREAM_MUSIC, false);
					dbMsg +=",MPSIntent=" + MPSIntent;/////////////////////////////////////
					if( MPSIntent == null){
						MPSIntent = new Intent(MaraSonActivity.this, MusicPlayerService.class);
						dbMsg +=">>" + MPSIntent;/////////////////////////////////////
					}
					MPSIntent.setAction(MusicPlayerService.ACTION_ACT_CLOSE);			//アクティビティがクローズされた事の通達
		//			myLog(TAG,dbMsg);
					if(event.getAction() == KeyEvent.ACTION_DOWN){					//リストから戻った時はUPになっている
						String dataFN = getPrefStr( "pref_data_url" ,"" , MaraSonActivity.this);
//						int mIndex = getPrefInt("pref_mIndex" , 0 , MaraSonActivity.this);
						dbMsg +=",mIndex=" + mIndex;/////////////////////////////////////
						sendPlaying( MPSIntent , dataFN , mcPosition , mIndex);						//setされたActionを受け取って再生		<onStopTrackingTouch [aSetei]
						quitMe();		//このアプリを終了する
					}
					return true;
				//	break;
				case KeyEvent.KEYCODE_DPAD_LEFT:	//マルチガイド左；21
				case KeyEvent.KEYCODE_DPAD_RIGHT:	//マルチガイド右；22
				case KeyEvent.KEYCODE_DPAD_UP:		//マルチガイド上；19
				case KeyEvent.KEYCODE_DPAD_DOWN:	//マルチガイド下；20
				case KeyEvent.KEYCODE_DPAD_CENTER:	//決定ボタン；23  ★ここではなくonCkickで処理される
				case KeyEvent.KEYCODE_STAR:	//スターキー；17
					if(! prTT_dpad){					//d-pad有りか
						setKeyAri();									//d-pad対応
					}
					break;
				}
			}
			b_focusItemID = focusItemID;
			b_Action = event.getAction();
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return false;
	}

	public int SelID;
	public void setKeyAri() {									//d-pad対応;送り先の設定
		final String TAG = "setKeyAri";
		String dbMsg= "";/////////////////////////////////////
		try{
			prTT_dpad=true;					//d-pad有りか
			dbMsg=",ppPBT=" + ppPBT.getId() ;
			if( ppPBT ==null ){
				setContentView(R.layout.activity_mara_son);
				ppPBT = findViewById(R.id.ppPButton);									//再生ボタン
			}
			ppPBT.setNextFocusUpId(saiseiSeekMP.getId());		//シークバー
			dbMsg=dbMsg+",artist_tv=" + artist_tv.getId() ;
			artist_tv.setNextFocusDownId(alubum_tv.getId());	//アルバム
			artist_tv.setNextFocusLeftId(rewPBT.getId());		//戻しボタン
			artist_tv.setNextFocusRightId(ffPBT.getId());		//送りボタン

			dbMsg=dbMsg+",alubum_tv=" + alubum_tv.getId() ;
			alubum_tv.setNextFocusUpId(artist_tv.getId());		//アルバムアーティスト
			alubum_tv.setNextFocusDownId(titol_tv.getId());		//タイトル
			alubum_tv.setNextFocusLeftId(rewPBT.getId());		//戻しボタン
			alubum_tv.setNextFocusRightId(ffPBT.getId());		//送りボタン

			dbMsg=dbMsg+",titol_tv=" + titol_tv.getId() ;
			titol_tv.setNextFocusUpId(alubum_tv.getId());		//アルバム
			titol_tv.setNextFocusDownId(saiseiSeekMP.getId());	//シークバー
			titol_tv.setNextFocusLeftId(rewPBT.getId());		//戻しボタン
			titol_tv.setNextFocusRightId(ffPBT.getId());		//送りボタン

			dbMsg=dbMsg+",rewPBT=" + rewPBT.getId() ;
			rewPBT.setNextFocusDownId(saiseiSeekMP.getId());	//シークバー
			rewPBT.setNextFocusLeftId(ffPBT.getId());			//送りボタン
			rewPBT.setNextFocusRightId(ffPBT.getId());			//送りボタン

			dbMsg=dbMsg+",ffPBT=" + ffPBT.getId() ;
			ffPBT.setNextFocusUpId(titol_tv.getId());			//タイトル
			ffPBT.setNextFocusDownId(vol_btn.getId());		//シークバー
			ffPBT.setNextFocusLeftId(rewPBT.getId());			//戻しボタン
			ffPBT.setNextFocusRightId(rewPBT.getId());			//戻しボタン

			dbMsg=dbMsg+",vol_btn=" + vol_btn.getId() ;								//ボリュームボタン
			vol_btn.setNextFocusUpId(ffPBT.getId());
			vol_btn.setNextFocusDownId(saiseiSeekMP.getId());		//シークバー
			vol_btn.setNextFocusLeftId(rewPBT.getId());			//戻しボタン
			vol_btn.setNextFocusRightId(rewPBT.getId());			//戻しボタン

			dbMsg=dbMsg+",saiseiSeekMP=" + saiseiSeekMP.getId() ;
			saiseiSeekMP.setNextFocusUpId(vol_btn.getId());
			saiseiSeekMP.setNextFocusDownId(ppPBT.getId());		//再生ボタン
			dbMsg=dbMsg+",adViewを読み込むLinearLayou=" + mAdView.getId() ;
			ppPBT.setFocusable(true);
			ppPBT.setSelected(true);					//TextView,ImageButton,Spinnerは.setSelected(true);
			ppPBT.setFocusable(true);					//TextView,ImageButton,Spinnerは.setSelected(true);
			ppPBT.setFocusableInTouchMode(true);
			ppPBT.requestFocus();
			myEditor.putString("pref_sonota_dpad", String.valueOf(prTT_dpad));
			myEditor.commit();
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}
//////////////////////////////////////////////////////////キー対応//
	public AlertDialog.Builder volDog;		// アラーとダイアログ を生成
	private void setVol() {				//音量設定
		final String TAG = "setVol";
		String dbMsg= "";/////////////////////////////////////
		try{
			nowVol = audioManage.getStreamVolume(AudioManager.STREAM_MUSIC);			//現在の音楽音量
			dbMsg= "nowVol=" + nowVol;//0～15/////////////////////////////
			LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
			final View layout = inflater.inflate(R.layout.set_vol,
					findViewById(R.id.setvolroot));
			volDog = new AlertDialog.Builder(this);		// アラーとダイアログ を生成
			volDog.setTitle(getResources().getText(R.string.setvol_titol));
			volDog.setView(layout);
			volDog.setCancelable(true);
			SeekBar volSeek = layout.findViewById(R.id.vol_seek);
			volSeek.setMax(15);
			volSeek.setProgress(nowVol);
			volSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				public void onStopTrackingTouch(SeekBar seekBar) {

				}
				public void onStartTrackingTouch(SeekBar seekBar) {

				}

				public void onProgressChanged(SeekBar seekBar,int progress, boolean fromUser) {
					final String TAG = "onProgressChanged[MaraSonActivity]";
					String dbMsg= "開始;";/////////////////////////////////////
					try{
						dbMsg +="nowVol=" + MaraSonActivity.this.nowVol;//0～15/////////////////////////////
						MaraSonActivity.this.nowVol =progress;
						dbMsg +=",progress=" + progress;//0～15/////////////////////////////
						audioManage.setStreamVolume(AudioManager.STREAM_MUSIC, MaraSonActivity.this.nowVol, 0);				//FLAG_SHOW_UIでTost表示
						MaraSonActivity.this.nowVol = audioManage.getStreamVolume(AudioManager.STREAM_MUSIC);			//現在の音楽音量
						dbMsg +=">>" + MaraSonActivity.this.nowVol;//0～15/////////////////////////////
				//		myLog(TAG,dbMsg);
						MaraSonActivity.this.vol_tf.setText(String.valueOf(MaraSonActivity.this.nowVol));										//音量表示
						MaraSonActivity.this.volDog.create().dismiss();
						myLog(TAG, dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
			    }
			});
			volDog.create().show();			// 表示
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	private void reSetVol() {				//設定されたミュートを解除
		final String TAG = "reSetVol";
		String dbMsg= "";/////////////////////////////////////
		try{
			nowVol = audioManage.getStreamVolume(AudioManager.STREAM_MUSIC);			//現在の音楽音量
			dbMsg= "nowVol=" + nowVol;//0～15/////////////////////////////
			if( nowVol == 0 ){
				audioManage.setStreamMute(AudioManager.STREAM_MUSIC, false);			//解除して
				audioManage.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);				//最少音量に
				vol_btn.setImageResource(android.R.drawable.ic_lock_silent_mode);			//消音中
			}
			nowVol = audioManage.getStreamVolume(AudioManager.STREAM_MUSIC);			//現在の音楽音量
			dbMsg= ">>" + nowVol;//0～15/////////////////////////////
			MaraSonActivity.this.vol_tf.setText(String.valueOf(MaraSonActivity.this.nowVol));										//音量表示
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	//通信//////////////////////////////////////////////////////////////////////////
	static class btHandler extends Handler{
		public void handleMessage(Message msg) {
			final String TAG = "handleMessage";
			String dbMsg= "[MaraSonActivity.btHandler]";/////////////////////////////////////
			try {
				Intent broadcastIntent = new Intent();
				dbMsg="message" + msg.toString() ;/////////////////////////////////////
				broadcastIntent.putExtra("message", msg);
				String actionName = MusicPlayerService.ACTION_BLUETOOTH_INFO;
				broadcastIntent.setAction(actionName);
				myLog(TAG, dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
		}
	}

/**
 * Bluettoth情報表示
 * 再生/停止操作は class BuletoohtReceiver	で行う
 * */
	public void setBTinfo( String stateBaseStr){					//Bluettoth情報更新
		final String TAG = "setBTinfo";
		String dbMsg= "[MaraSonActivity.btHandler]";/////////////////////////////////////
		try{
			dbMsg +=",Buletooth=" +stateBaseStr;
			this.stateBaseStr = stateBaseStr;
			dbMsg +=",dviceStyte=" +MaraSonActivity.this.dviceStyte;
			dbMsg +=",Buletooth=" +stateBaseStr;
			this.stateBaseStr = stateBaseStr;
//			Intent intent = new Intent(MusicPlayerService.ACTION_STATE_CHANGED);
//			intent.putExtra("stateBaseStr", stateBaseStr);
//			sendBroadcast(intent);					//APIL1
//			if( dviceStyte != null ){
//				dviceStyte = (TextView) findViewById(R.id.dviceStyte);								//デバイスの接続情報
//				dbMsg +=">>" +dviceStyte;
				if(stateBaseStr != null){
			//		if( ! b_stateStr.equals(stateBaseStr)){
					MaraSonActivity.this.pp_bt_ll.setVisibility(View.VISIBLE);
					MaraSonActivity.this.dviceStyte.setText(stateBaseStr);			// メッセージ出力
			//		}
				}else{
					MaraSonActivity.this.pp_bt_ll.setVisibility(View.GONE);			//フィールドを非表示
				}
//			} else {
//				shigot_bangou = btInfo_kousin;
//			}
//			if(stateBaseStr.contains(getResources().getString(R.string.bt_playing))){					//再生中
//	//			playing();
//			}else if(stateBaseStr.contains(getResources().getString(R.string.bt_not_playing))){		//一時停止
//	//			paused();
//			}else if(stateBaseStr.contains(getResources().getString(R.string.bt_discnnected))){		//切断
//			}else if(stateBaseStr.contains(getResources().getString(R.string.bt_connected))){		//接続済み
//			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**サービスとレシーバに再生ファイルを指定*/
	public boolean send2Service(String dataFN , String listName ,boolean IsPlaying) {																		//操作対応②ⅰ
		final String TAG = "send2Service";
		String dbMsg = "";
		try{
			dbMsg += ORGUT.nowTime(true,true,true)+dbMsg;
			dbMsg +=" 、dataFN="+ dataFN;
			dbMsg +=" 、listName="+ listName;
			dbMsg +=" 、IsPlaying="+ IsPlaying;				// + ",MPSIntent=" + MPSIntent;
			//		dbMsg +=" 、ppPBT;IsPlaying="+ IsPlaying;				// + ",MPSIntent=" + MPSIntent;
			if(mFilter == null){
				psSarviceUri = getPackageName() + getResources().getString(R.string.psSarviceUri);		//プレイヤーサービス	"com.hijiyam_koubou.marasongs.PlayerService";
				dbMsg +=  ">>psSarviceUri=" + psSarviceUri;
				mFilter = new IntentFilter();
				mFilter.addAction(MusicPlayerService.ACTION_STATE_CHANGED);
				registerReceiver(mReceiver, mFilter);
			}
			if( MPSIntent == null){
				MPSIntent = new Intent(MaraSonActivity.this,MusicPlayerService.class);	//parsonalPBook.thisではメモリーリークが起こる		getApplication()
				dbMsg +=  ",MPSIntent=null";
			}else{
				stopService(MPSIntent);
			}
			dbMsg +=" 、[ " + nowList_id+ "] " + listName + " の " + mIndex + "番目で"+ dataFN + "の" + saiseiJikan + "から";
			MPSIntent.putExtra("nowList_id",nowList_id);
			MPSIntent.putExtra("nowList",listName);
			MPSIntent.putExtra("pref_data_url",dataFN);
			MPSIntent.putExtra("continu_status","toPlay");
			if(! IsPlaying){
				//		MPSIntent.setAction(MusicPlayerService.ACTION_PAUSE);
				IsPlaying = false;
			}else{
				//		MPSIntent.setAction(MusicPlayerService.ACTION_PLAY);
				IsPlaying = true;
			}
			MPSIntent.setAction(MusicPlayerService.ACTION_START_SERVICE);
			dbMsg += ">action>" + MPSIntent.getAction();
			dbMsg += " , IsPlaying=" + IsPlaying;
			MPSIntent.putExtra("IsPlaying",IsPlaying);
			MPSName = startService(MPSIntent);	//ボタンフェイスの変更はサービスからの戻りで更新
			dbMsg += " ,MPSName=" + MPSName + "でstartService";
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return IsPlaying;
	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//		super.onActivityResult(requestCode, resultCode, intent);
//		final String TAG = "onActivityResult";
//		String dbMsg="";
//		try{
//			Bundle bundle = null ;
//			int mIndex = getPrefInt("pref_mIndex" , 0, MaraSonActivity.this);
//			dbMsg += "mIndex=" + mIndex;
//			String dataFN = getPrefStr( "pref_data_url" ,"" , MaraSonActivity.this);
//			dbMsg += "intent="+intent;
//			if(intent != null){
//				dbMsg += ",resultCode="+resultCode;
//				dbMsg += ",requestCode="+requestCode;
//				boolean retBool ;
//				String retString = null;
//				bundle = intent.getExtras();
//				if(resultCode == RESULT_OK){
//					switch(requestCode) {
//						case syoki_start_up:			//100;onCreate、jyoukyouBunki→preRead→　ここ　→終了後、onCreateまで戻る
//							ruikei_titol = String.valueOf( bundle.getInt("key.retInt"));		//全タイトルカウント//MediaStore.Audio.Mediaのレコード数
//							dbMsg +="、ファイル数=" + ruikei_titol;
//							if(Integer.parseInt(ruikei_titol) >0 ){
//								MaraSonActivity.this.finish();											//reStart
//								startActivity(new Intent(MaraSonActivity.this,MaraSonActivity.class));
//							}else{
//								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//								alertDialogBuilder.setTitle(getResources().getString(R.string.saisinnFileKakuninn_f_titol));		//音楽ファイルを確認できません
//								alertDialogBuilder.setMessage(getResources().getString(R.string.saisinnFileKakuninn_f_meg));		//この端末に再生できる音楽ファイルが見つかりません。\nメモリーカードの装着や音楽ファイルの有無を確認してください
//								alertDialogBuilder.setPositiveButton(getResources().getString(R.string.saisinnFileKakuninn_f_btn),	//一旦終了
//										new DialogInterface.OnClickListener() {			// アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
//											@Override
//											public void onClick(DialogInterface dialog, int which) {
//												quitMe();		//このアプリを終了する
//											}
//										});
//								alertDialogBuilder.setCancelable(true);			// アラートダイアログのキャンセルが可能かどうかを設定します
//								AlertDialog alertDialog = alertDialogBuilder.create();
//								alertDialog.show();			// アラートダイアログを表示します
//							}
//							break;
//						case syoki_start_up2:			//100;初回起動とプリファレンスリセット後;全レコードチェック後、・アーティスト名などの補足
//							artistList_yomikomi();
//							shigot_bangou =  syoki_activty_set ;		//	onWindowFocusChangedを経てaSetei； ;ボタンなどへのイベント割付け
//							break;
//
//						case syoki_Yomikomi:
//							String retStr = String.valueOf(bundle.getString("key.retStr"));
//							dbMsg += ",retStr=" + retStr ;//////////////////
//							if( retStr != null ){
//								Intent intentZL = new Intent(getApplication(),ZenkyokuList.class);						//parsonalPBook.thisではメモリーリークが起こる
//								intentZL.putExtra("reqCode",syoki_Yomi_syuusei);														//処理コード
//								intentZL.putExtra("pdMessage_stok",retStr);														//処理コード
//								startActivityForResult(intentZL , syoki_Yomi_syuusei);
//							}
//							break;
//						case syoki_alist2redium:							//129;artistListを作った後でレジューム機能に戻す
//							artistList_yomikomi();		//アーティストリストを読み込む(db未作成時は-)
//							dbMsg +="レジュームに戻す前のアーティスト="+ artistList.size() +"人";		/////////////////////////////////////////////////////////////
//							if(dataFN != null){
//								url2FSet(dataFN ,mIndex);		//urlからプレイヤーの書き込みを行う		albumArtist
//							}
//							break;
//						case MuList.MENU_MUSCK_PLIST:			//プレイリスト選択中
//						case CONTEXT_runum_sisei:				//184 ランダム再生
//						case v_artist:							//アーティストリスト表示
//						case v_alubum:							//アルバムリスト表示
//						case v_titol:								//タイトルリスト表示
//						case rp_artist:							//アーティストリピート指定ボタン
//						case rp_album:							//アルバムリピート指定ボタン
//						case rp_titol:							//タイトルリピート指定ボタン
//						case rp_point:							//二点間リピート
//							MaraSonActivity.this.nowList = bundle.getString("nowList");		//プレイリス
//							dbMsg +=",nowList=" + MaraSonActivity.this.nowList;/////////////////////////////////////
//							toolbar.setTitle(MaraSonActivity.this.nowList);
//							int b_nowList_id = nowList_id;
//							dbMsg +="[プレイリストID=" + b_nowList_id;/////////////////////////////////////
//							MaraSonActivity.this.nowList_id = bundle.getInt("nowList_id");
//							dbMsg +="→" + MaraSonActivity.this.nowList_id +"]" + MaraSonActivity.this.nowList;/////////////////////////////////////
//							if( pref_zenkyoku_list_id != MaraSonActivity.this.nowList_id ){
//								artistCountLL.setVisibility(View.GONE);				//アーティストカウント
//								albumCountLL.setVisibility(View.GONE);					//アルバムカウント
//								titolCountLL.setVisibility(View.GONE);					//タイトルカウント
//							}else{
//								artistCountLL.setVisibility(View.VISIBLE);				//アーティストカウント
//								albumCountLL.setVisibility(View.VISIBLE);					//アルバムカウント
//								titolCountLL.setVisibility(View.VISIBLE);					//タイトルカウント
//							}
//							if( b_nowList_id != MaraSonActivity.this.nowList_id
////								|| requestCode ==rp_artist  ||
////							requestCode ==rp_album  ||
////							requestCode ==rp_titol  ||
////							requestCode ==rp_point
//							){
//							}
//							n_dataFN = bundle.getString("dataFN");		//	//次に再生する再生ファイル
//							dbMsg += ",n_dataFN= ;"+n_dataFN;
////						if(! nowList.equals(getResources().getString(R.string.listmei_zemkyoku)) ){
////							mIndex = bundle.getInt("mIndex");
////						}else{
//							 mIndex = Item.getMPItem(n_dataFN);
////						}
//							dbMsg +=  "[play_order="+mIndex ;/////////////////////////////////////リストの状態	起動直後；veiwPlayer / 再選択chyangeSong
//							songIDTotal = musicPlaylist.getUserListMaxPlayOrder(nowList_id);
//							dbMsg +="/" + songIDTotal +"曲";
//							titolAllPTF.setText(String.valueOf(songIDTotal));		//全タイトルカウント
//							imanoJyoutai = bundle.getInt("imanoJyoutai");	//DURATION;継続;The duration of the audio file, in ms;Type: INTEGER (long)
//							dbMsg +=  ",今の状態="+imanoJyoutai ;/////////////////////////////////////リストの状態	起動直後；veiwPlayer / 再選択chyangeSong
//							dataFN = getPrefStr( "pref_data_url" ,"" , MaraSonActivity.this);
//							if( ! n_dataFN.equals(dataFN)){
//								IsPlaying = bundle.getBoolean("IsPlaying");
//								dbMsg += ",再生中=" + IsPlaying;/////////////////////////////////////
////							if( IsPlaying ){
////								MPSIntent.setAction(MusicPlayerService.ACTION_LISTSEL);				//ACTION_STOP?
////								MPSName = startService(MPSIntent);		//onStartCommandへ	//startService(new Intent(MusicPlayerService.ACTION_SKIP));
////							}
////								dbMsg += ",mcPosition="+mcPosition;
////								saiseiJikan = bundle.getInt("saiseiJikan");
////								dbMsg += "/"+saiseiJikan +"mS";
//								pref_bt_renkei = bundle.getBoolean("pref_bt_renkei");
//								dbMsg += ",Bluetoothの接続に連携=" + pref_bt_renkei;/////////////////////////////////////
//								zenkai_saiseKyoku = bundle.getInt("zenkai_saiseKyoku");
//								dbMsg += ",前回=" + zenkai_saiseKyoku +"曲";/////////////////////////////////////
//								zenkai_saiseijikann = bundle.getLong("zenkai_saiseijikann");
//								dbMsg +=zenkai_saiseijikann;/////////////////////////////////////
//								dataFN = n_dataFN;
////							if( IsPlaying ){
////								MPSIntent.setAction(MusicPlayerService.ACTION_LISTSEL);				//ACTION_STOP?
////								MPSName = startService(MPSIntent);		//onStartCommandへ	//startService(new Intent(MusicPlayerService.ACTION_SKIP));
////							}
//							}
//							dbMsg +=",pp_start=" + pp_start;/////////////////////////////////////
//
//							dbMsg +=",点間リピート中="+rp_pp;/////////////////////////////////////
//							boolean mot_pp = rp_pp;
//							rp_pp =bundle.getBoolean("rp_pp");
//							dbMsg +=">>"+rp_pp;/////////////////////////////////////
//							if( ! rp_pp && mot_pp){					//二点間リピートから解除された
//								repeatePlaykijyo();
//							}
//
//							if( pp_start != 0 || requestCode ==rp_point  ){
////								mcPosition = pp_start;
////								dbMsg +=">mcPosition>" + mcPosition;/////////////////////////////////////
//							} else {
//								pp_pp_ll.setVisibility(View.GONE);
//								saiseiSeekMP.setSecondaryProgress(0);
//								saiseiSeekMP.setProgressDrawable(dofoltSBDrawable);
//							}
//
//							dataFNList = bundle.getStringArrayList("dataFNList");
//							//			dbMsg +=",dataFNList=" + dataFNList;/////////////////////////////////////
//							if( dataFNList != null ){
//								dbMsg +=",dataFNList=" + dataFNList.size() + "件";/////////////////////////////////////
//								n_dataFN = bundle.getString("dataFN");		//	//次に再生する再生ファイル
//								dbMsg += ",n_dataFN= ;"+n_dataFN;
//							}
//							mData2Service( mIndex );										//サービスにプレイヤー画面のデータを送る
////						isListSentaku = false;		//リスト選択終了
////						kyokuHenkou = true;			// 任意に曲変更
//							break;
//						case settei_hyouji:		//);		//プリファレンス表示	settei_hyouji				//設定表示
//							retString = bundle.getString("key.pref_gyapless");	//クロスフェード時間
//							dbMsg += ",クロスフェード="+ retString;				///////////////
//							if(retString != null){
//								MLIST.crossFeadTime = Long.valueOf(retString);	//クロスフェード時間
//							}
//							String retComp = String.valueOf(bundle.getString("key.pref_compBunki"));			//コンピレーション分岐点
//							dbMsg +="、コンピレーション分岐点"+ pref_compBunki;	////////////////
//							if( ! retComp.equals(pref_compBunki)){
//								dbMsg += ">>"+ retComp;	////////////////
//								pref_compBunki = retComp;
////								mcPosition = saiseiSeekMP.getProgress();
////								dbMsg=dbMsg +";;" + mcPosition;/////////////////////////////////////
//								preRead(syoki_Yomikomi , null);
//							}
//
//							retBool = Boolean.valueOf(bundle.getString("pref_list_simple"));
//							dbMsg +="、シンプルなリスト表示(今は"+ pref_list_simple +")";	////////////////
//							if( retBool != pref_list_simple){
//								dbMsg +="＞＞"+ retBool;	////////////////
//								pref_list_simple = retBool;
//								MLIST.pref_list_simple = pref_list_simple;
//							}
//							retBool = Boolean.valueOf(bundle.getString("key.pref_lockscreen"));
//							dbMsg +="、ロックスクリーンプレイヤー(今は"+ pref_lockscreen +")";	////////////////
//							if( retBool != pref_lockscreen){
//								dbMsg +="＞＞"+ retBool ;	////////////////
//								pref_lockscreen = retBool;
//								MLIST.pref_lockscreen = pref_lockscreen;
//							}
//							retBool = Boolean.valueOf(bundle.getString("key.pref_notifplayer"));
//							dbMsg +="ノティフィケーションプレイヤ(今は"+ pref_notifplayer +")";	////////////////
//							if( retBool != pref_lockscreen){
//								dbMsg +="＞＞"+ retBool ;	////////////////
//								pref_notifplayer = retBool;
//								MLIST.pref_notifplayer = pref_notifplayer;
//							}
//							retInt = Integer.valueOf(bundle.getString("visualizerType"));
//							dbMsg += ",visualizerType="+ visualizerType;				///////////////
//							dbMsg +="＞＞"+ retInt ;	////////////////
//							if( retInt != visualizerType){
//								if(visualizerType != Visualizer_type_none){				//元が使わないでなければ
//									visualizerVG.removeAllViews();							//設定されているViewGrupを削除
//								}
//								visualizerType = retInt ;
//								if(visualizerType != Visualizer_type_none){				//元が使わないでなければ
//									makeVisualizer(  );						//VisualizerのView作成
//									setupVisualizerFxAndUI();				//Visualizerの設定
//								}
//							}
//							shigot_bangou = 0;
//
//							retBool = Boolean.valueOf(bundle.getString("key.pref_pb_bgc"));						//プレイヤーの背景
//							dbMsg +=",プレイヤーの背景(w；今は"+ playerBGColor_w +")";	////////////////
//							if( retBool != playerBGColor_w){		//プレイヤーの背景
//								dbMsg +="＞＞"+ retBool ;	////////////////
//								playerBGColor_w = retBool;
//								MLIST.pref_pb_bgc = playerBGColor_w;				//プレイヤーの背景は白
//								dbMsg +=",mVisualizer="+ mVisualizer;//////////////////
//								if( mVisualizer != null ){
//									mVisualizer.release();
//									mVisualizer = null;
//									visualizerVG.removeAllViews();							//設定されているViewGrupを削除
//								}
//								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//								alertDialogBuilder.setTitle(getResources().getString(R.string.pref_henkou_titol));
//								String dMessege = getResources().getString(R.string.pref_pb_bgc_titol);								//プレイヤー色</string>
//								if(playerBGColor_w){
//									dMessege = dMessege + "\n" + getResources().getString(R.string.pref_pb_bgc_bk) +">>" + getResources().getString(R.string.pref_pb_bgc_wh);					//黒系>>白系
//								}else{
//									dMessege = dMessege + "\n" + getResources().getString(R.string.pref_pb_bgc_wh) +">>" + getResources().getString(R.string.pref_pb_bgc_bk);					//黒系>>白系
//								}
//								dMessege = dMessege + "\n\n" + getResources().getString(R.string.pref_henkou_msg);					//次に起動した時に変更が反映されます。</string>
//								alertDialogBuilder.setMessage(dMessege);
//								alertDialogBuilder.setPositiveButton(getResources().getString(R.string.comon_saikidou),				//再起動</string>
//										new DialogInterface.OnClickListener() {			// アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
//											@Override
//											public void onClick(DialogInterface dialog, int which) {
//												Intent intentM = getIntent();
//												overridePendingTransition(0, 0);
//												intentM.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//												quitMe();		//このアプリを終了する
//												overridePendingTransition(0, 0);
////									MaraSonActivity.this.finish();
////									Intent intentM = new Intent(MaraSonActivity.this, MaraSonActivity.class);
////								//	intent.putExtra("reqCode",syoki_start_up_sai);								//再起動
//												startActivity(intentM);		//再起動実行
//											}
//										});
//								alertDialogBuilder.setNegativeButton(getResources().getString(R.string.comon_atode),					//後で</string>
//										new DialogInterface.OnClickListener() {			// アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
//											@Override
//											public void onClick(DialogInterface dialog, int which) {
//											}
//										});
//								alertDialogBuilder.setCancelable(true);			// アラートダイアログのキャンセルが可能かどうかを設定します
//								AlertDialog alertDialog = alertDialogBuilder.create();
//								alertDialog.show();			// アラートダイアログを表示します
//
//							}
//							break;
//						case LyricWeb:												//192;歌詞のweb表示
//							MPSIntent.setAction(MusicPlayerService.ACTION_DATA_OKURI);				//データ送りのみ
//							MPSName = startService(MPSIntent);		//onStartCommandへ	//startService(new Intent(MusicPlayerService.ACTION_SKIP));
//							dbMsg +=" ,ComponentName=" + MPSName.toString();/////////////////////////////////////
//							break;
//						case RESULT_ENABLE:					//ロック画面からの復帰
//							if (resultCode == Activity.RESULT_OK) {
//								dbMsg += "Administration enabled!";/////////////////////////////////////
//							} else {
//								dbMsg += "Administration enable FAILED!";/////////////////////////////////////
//							}
//							//				myLog(TAG,dbMsg);
//							return;
//						case REQUEST_ENABLE_BT:
//							// When the request to enable Bluetooth returns
//							if (resultCode == Activity.RESULT_OK) {
//								dbMsg += "Bluetooth is now enabled, so set up a chat session";/////////////////////////////////////
////					  setupChat();
//							}
//							break;
//						case LyricCheck:							//歌詞の有無確認
//						case LyricEnc:							//歌詞の再エンコード
//							String wrStr =bundle.getString("songLyric");
//							if( wrStr != null){
//								songLyric = wrStr;
//							}
//							lyric_tv.setText(songLyric);
//							lyricAri = bundle.getBoolean("lyricAri");			//歌詞を取得できた
//							wrStr =bundle.getString("lyricEncord");
//							if( wrStr != null){
//								lyricEncord = wrStr;						//歌詞の再エンコード
//							}
//							wrStr =bundle.getString("lylicHTM");			//html変換した歌詞のフルパス名
//							if( wrStr != null){
//								lylicHTM = wrStr;
//							}
//							break;
////					default:
////						break;
//					}
//				}else if(resultCode == RESULT_CANCELED){				//リストから何も選択されずに戻された時
//					switch(requestCode) {		//
//						case settei_hyouji:		//:		//);		//プリファレンス表示	settei_hyouji				//設定表示
//							//			prefKakikomi(true);					//終了前にプリファレンスを書き込む
//							preRead(syoki_start_up_sai , null);		//再起動
//							break;
//						default:								//最初はここから
//							if(dataFN == null){					//前回の再生ファイルが無い
//							}else{
//								dbMsg += "前回使ったアーティストリスト=" + artistList.size()+ "件で" + dataFN;//////////////////
////								url2FSet(dataFN ,mIndex);		//urlからプレイヤーの書き込みを行う		albumArtist
//							}
//							break;
//					}
//				}
//			}
//			myLog(TAG, dbMsg);
//		} catch (Exception e) {
//			myErrorLog(TAG ,  dbMsg + "で" + e);
//		}
//	}

	/**ACTIVITY,AUDIO_SERVICEの取得*/
	public ActivityManager activityManager;
	public AudioManager audioManage;
	int startVol;			//起動時の音楽音量
	int nowVol;				//現在の音楽音量
	public void kidou_Kakuninn () {								//起動確認;音量設定/前回終了
		final String TAG = "kidou_Kakuninn";
		String dbMsg="";
		try{
			new ArrayList<String>();
			activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
			audioManage = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			startVol = audioManage.getStreamVolume(AudioManager.STREAM_MUSIC);			//起動時の音楽音量
			dbMsg += ",startVol=" +startVol ;
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**shigot_bangou振り分け、*/
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {		//①ⅱヘッドのイメージは実際にローディンされた時点で設定表示と同時にウィジェットの高さや幅を取得したいときは大抵ここで取る。
		if (hasFocus) {
			final String TAG = "onWindowFocusChanged";
			String dbMsg= "";
			try{
				if(hasFocus){
					dbMsg= "shigot_bangou" + shigot_bangou;/////////////////////////////////////
					if(shigot_bangou > 0){
						dbMsg= "shigot_bangou;" + shigot_bangou;/////////////////////////////////////
						switch(shigot_bangou) {
							case syoki_activty_set:		// 105;プリファレンスの読み込み
								//			nendLoad();	//nendの広告設定
								//			adMobLoad();													//Google AdMobの広告設定
								String rStr = String.valueOf(titol_tv.getText());
								if( rStr.equals("") ){
									aSetei();		//フォーカス取得後のActibty設定
								}
								break;
							case btInfo_kousin :			//Bluetooth情報の更新
								setBTinfo( stateBaseStr);					//Bluettoth情報更新
								break;
							//					case quit_all :		//すべて終了
							//		//				quitMe();		//このアプリを終了する.
							//						break;
							default:
								nowVol = audioManage.getStreamVolume(AudioManager.STREAM_MUSIC);			//現在の音楽音量
								dbMsg +=",nowVol=" + nowVol;/////////////////////////////////////
								vol_tf.setText(String.valueOf(nowVol));//音量表示
								if(0< nowVol){
									vol_btn.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
								}else{
									vol_btn.setImageResource(android.R.drawable.ic_lock_silent_mode);			//消音中
								}

								if(lyric_tv.isShown()){
								}

								break;
						}
						shigot_bangou = 0;
					}
					int[] location = new int[2];
					dbMsg +="ディスプレイ["+dWidth+" × "+ dHeigh +"]" ;								//[1080 × 1776]
					Rect rect = new Rect();
					Window window = getWindow();
					window.getDecorView().getWindowVisibleDisplayFrame(rect);			//getGlobalVisibleRect,getLocationOnScreenと変わらず
					int statusBarHeight = rect.top;									// ステータスバーの高さ=75 	http://sukohi.blogspot.jp/2013/11/android.html
					dbMsg += ",statusBarHeight=" + statusBarHeight;
					dbMsg +=",ppPBT["+ppPBT.getWidth()+" × "+ ppPBT.getHeight() +"]" ;				//ppPBT[276 × 276]
					// 20190506:  java.lang.NullPointerException: Attempt to invoke virtual method 'int android.widget.ImageButton.getWidth()' on a null object reference
					//広告表示//////////////////////////////////////////////////
					dbMsg += ",Locale=" + Locale.getDefault ();/////////////////////////////////////
					setADSens();	////////////////////////////////////////////////広告表示////
					setNend();
				}
				myLog(TAG, dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
			shigot_bangou = 0;
		}
		super.onWindowFocusChanged(hasFocus);
	}

	public int reqCode;

	//①起動	②プリファレンスが無くて再読み込み	③ベース色など変更でリスタート
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {				//①起動動作ⅰWindowManagerの設定とアクティビティの読み込み//
		super.onCreate(savedInstanceState);
		final String TAG = "onCreate";
		String dbMsg="";
		try{
			start = System.currentTimeMillis();		// 開始時刻の取得
			ORGUT = new OrgUtil();		//自作関数集
			OGFil = new OrgUtilFile();//ファイル関連関数

			musicPlaylist = new MusicPlaylist(MaraSonActivity.this);
//			NotificationManager mNotificationManager = (NotificationManager)this.getSystemService(Activity.NOTIFICATION_SERVICE);
//			dbMsg += ",NotificationManager="+ mNotificationManager.toString() ;
			//スレッド起動確認///////////////////////////////////
			if( activityManager == null ){
				kidou_Kakuninn ();							//起動確認;音量設定/前回終了
			}
			List<android.app.ActivityManager.RunningServiceInfo> listServiceInfo = activityManager.getRunningServices(Integer.MAX_VALUE);
			dbMsg += ",listServiceInfo="+ listServiceInfo.size() + "件" ;
			boolean found = false;
			for (android.app.ActivityManager.RunningServiceInfo curr : listServiceInfo) {	//起動中のサービス名を取得 クラス名を比較
				if (curr.service.getClassName().equals(MusicPlayerService.class.getName())) {		// 実行中のサービスと一致 = ノティフィケーションからの起動
					dbMsg +="起動中のサービス=" + curr.service.getClassName();///////////	dbMsg=dbMsg +">>"+ MusicPlayerService.class.getName();/////////////////////////////////////
					found = true;
					kidou_jyoukyou = kidou_notif ;						//ノティフィケーションからの起動
					imanoJyoutai = MuList.sonomama;
					break;
				}
			}
			///////////////////////////////////スレッド起動確認//
	//		readPref();       //					setteriYomikomi();		//<onCreate	プリファレンスに記録されているデータ読み込み

			Intent intent = getIntent();
			reqCode = intent.getIntExtra("reqCode", 0);
			dbMsg += ",reqCode=" + reqCode;
			nowList_id = intent.getIntExtra("nowList_id", 0);
			nowList=intent.getStringExtra("nowList");
			dbMsg += "[" + nowList_id+ "]" + nowList;
			dataURL=intent.getStringExtra("pref_data_url");
			dbMsg += ",dataURL=" + dataURL;
			toPlaying=intent.getBooleanExtra("IsPlaying",true);
			dbMsg += ",toPlaying=" + toPlaying;
//			dbMsg += ",found="+ found ;/////////////////////////////////////
////			Bundle extras = getIntent().getExtras();				//起動されたアクティビティからデータを受け取る
////			dbMsg += ",extras="+ extras ;/////////////////////////////////////

			receiverHaki();		//レシーバーを破棄

//			String dataFN = myPreferences.saisei_fname;
//			dbMsg +=  ",dataFN=" + dataFN;////////////////////////////////////////////////////////////////////////////
//			dbMsg += ",IsPlaying=" + IsPlaying;/////////////////////////////////////
//			toPlaying = extras.getBoolean("toPlaying");
//			dbMsg += ",曲をセットしたら再生=" + toPlaying;
//			dbMsg +=",起動="+ kidou_jyoukyou + ",状態="+ imanoJyoutai ;/////////////////////////////////////
//			dbMsg +=",プレイリストの保存場所= " + nowList_data;//////////////////////////////////
//			pref_bt_renkei = extras.getBoolean("pref_bt_renkei");			//Bluetoothの接続に連携して一時停止/再開
//			dbMsg += ",Bluetoothの接続に連携=" + pref_bt_renkei;/////////////////////////////////////
//			pref_list_simple = extras.getBoolean("pref_list_simple");
//			dbMsg += ",シンプルなリスト表示=" + pref_list_simple;/////////////////////////////////////
////				zenkai_saiseKyoku = extras.getInt("zenkai_saiseKyoku");					//前回の連続再生曲数
//			dbMsg += ",前回=" + zenkai_saiseKyoku +"曲";/////////////////////////////////////
////				zenkai_saiseijikann = extras.getLong("zenkai_saiseijikann");						//前回の連続再生時間
//			dbMsg +=zenkai_saiseijikann;/////////////////////////////////////
////				pref_lockscreen = extras.getBoolean("pref_lockscreen") ;
//			dbMsg +=",ロックスクリーンプレイヤー="+pref_lockscreen;/////////////////////////////////////
////				pref_notifplayer = extras.getBoolean("pref_notifplayer") ;
//			dbMsg +=",ノティフィケーションプレイヤー="+pref_notifplayer;/////////////////////////////////////
////				pref_sonota_vercord = extras.getInt("pref_sonota_vercord") ;
//			dbMsg +=",このアプリのバージョンコード="+pref_sonota_vercord;/////////////////////////////////////
//			imanoJyoutai = extras.getInt("imanoJyoutai");		//リストの状態	起動直後；veiwPlayer / 再選択chyangeSong
//			dbMsg +=",リストの状態="+imanoJyoutai;/////////////////////////////////////
			dbMsg += ",kidou_jyoukyou=" + kidou_jyoukyou;//100:
			wm = (WindowManager)getSystemService(WINDOW_SERVICE);			// ウィンドウマネージャのインスタンス取得
			Display disp = wm.getDefaultDisplay();										// ディスプレイのインスタンス生成
			dWidth = disp.getWidth();								//ディスプレイ幅
			dHeigh = disp.getHeight();							//ディスプレイ高
			dbMsg +="ディスプレイ["+dWidth+" × "+ dHeigh +"]" ;/////////////////////////////////////////////////////////////////////////////////////////////////////////
			//ロックスクリーン/////////////////////////////////////////////		//http://d.hatena.ne.jp/compound/20110813/1313245962
			mDevicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);				// DevicePolicyManagerを取得する。
			if(Build.VERSION.SDK_INT > 8){
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}
			mDarSample = new ComponentName(getApplication(), lsRecever.class);
			dbMsg +=".mDarSample="+mDarSample.toString() ;/////////////////////////////////////////////////////////////////////////////////////////////////////////
			/////Bluetooth///////////////////////////////////////////ロックスクリーン//
			b_stateStr = "";
			/////////////////////////////////////////////Bluetooth//
			//				if(Integer.valueOf(pref_apiLv) >10){
			//					this.getWindow().requestFeature(Window.FEATURE_ACTION_MODE_OVERLAY);			//、コンテンツの上に、ActionBarをオーバーレイ表示することを許可	APIL11;☆setContentView でコンテントビューを設定する前に行います。
			//				}
			dbMsg +=",playerBGColor_w=" + playerBGColor_w;/////////////////////////////////////
			if(playerBGColor_w){
				setTheme(R.style.MyLightTheme);		//Theme_Light 16973836 	白　タイトルなし   //タイトルバー(アクションバー)なし	Theme_Light_NoTitleBar
				//☆ContextにViewがインスタンス化される前(setContentViewとかinflateとかされる前)み有効
			}else{
				setTheme(android.R.style.Theme_Black);		//Theme_Black6973832；；	黒　タイトルなし   //タイトルバー(アクションバー)なし	Theme_Black_NoTitleBar
			}
//			}
			//		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

			setContentView(R.layout.activity_mara_son);
			toolbar = findViewById(R.id.pp_tool_bar);							//このアクティビティのtoolBar
			toolbar.setContentInsetsAbsolute(0,0);								//左と上の余白を無くす
			//☆このアクティビティだけメニューの文字色が黒くなる
			if(playerBGColor_w){															//白系なら
				toolbar.setBackgroundColor(getResources().getColor(R.color.white_pere));
				toolbar.setTitleTextColor(getResources().getColor(R.color.black_pere));
				toolbar.setPopupTheme(R.style.PopupMenu_w); 			//inflateMenuでViewの更新が入ってるっぽいのでinflateMenuより前で
			}else{
				toolbar.setBackgroundColor(getResources().getColor(R.color.black_pere));
				toolbar.setTitleTextColor(getResources().getColor(R.color.white_pere));
				toolbar.setPopupTheme(R.style.PopupMenu_b); 			//inflateMenuでViewの更新が入ってるっぽいのでinflateMenuより前で
			}
			setSupportActionBar(toolbar);
			toolbar.setTitle(nowList);

			artist_tv = findViewById(R.id.artist_tv);									//アルバムアーティスト
			//		creditArtistName_tf = (TextView) findViewById(R.id.creditArtistName_tf);	//クレジットされているアーティスト名
			alubum_tv = findViewById(R.id.alubum_tv);								//アルバムー
			titol_tv = findViewById(R.id.titol_tv);										//タイトルスピナー
			songIDPTF = findViewById(R.id.songIDPTF);							//リスト中の何曲目か
			titolAllPTF = findViewById(R.id.titolAllPTF);							//全タイトルカウント
			if(ruikei_titol != null){
				titolAllPTF.setText( ruikei_titol );			//全タイトルカウント
			}
			saiseiPositionPTF = findViewById(R.id.saiseiPositionPTF);			//再生ポイント
			totalTimePTF = findViewById(R.id.totalTimePTF);						//再生時間
			saiseiSeekMP = findViewById(R.id.saiseiSeekMP);						//シークバー
			saiseiSeekMP.setMax(0);        													//起動フラグ
//			saiseiSeekMP.setProgress(mcPosition);
			dofoltSBDrawable = saiseiSeekMP.getProgressDrawable();					//シークバーの元設定
			ruikei_jikan_tv = findViewById(R.id.ruikei_jikan_tv);						//累積時間
			ruikei_kyoku_tv = findViewById(R.id.ruikei_kyoku_tv);					//累積曲数
			pp_zenkai_ll = findViewById(R.id.pp_zenkai_ll);								//前回の累積レイアウト
			zenkai_ruikei_suu_tv = findViewById(R.id.zenkai_ruikei_suu_tv);		//前回の累積曲数
			zenkai_ruikei_tv = findViewById(R.id.zenkai_ruikei_tv);					//前回の累積
			vol_tf = findViewById(R.id.vol_tf);												//音量表示
			dviceStyte = findViewById(R.id.dviceStyte);								//デバイスの接続情報
			dviceStyteHSV = findViewById(R.id.dviceStyteHSV);					//デバイスの接続情報のスクロール
			ppPBT = findViewById(R.id.ppPButton);									//再生ボタン
			ffPBT = findViewById(R.id.ffPButton);									//送りボタン
			rewPBT = findViewById(R.id.rewPButton);								//戻しボタン
			//	lockButton = (ImageButton) findViewById(R.id.lockButton);							//画面ロックボタン
//			stopPButton = (ImageButton) findViewById(R.id.stopPButton);						//終了ボタン
			vol_btn = findViewById(R.id.vol_btn);								//ボリュームボタン
			artistCountLL = findViewById(R.id.artistCountLL);					//アーティストカウント
			artistTotalPTF = findViewById(R.id.artistTotalPTF);					//アルバムアーティス合計
			artistIDPTF = findViewById(R.id.artistIDPTF);							//アルバムアーティスカウント
			albumCountLL = findViewById(R.id.albumCountLL);					//アーティストカウント
			alubumTotalPTF = findViewById(R.id.alubumTotalPTF);			//アルバム合計
			albumIDPTF = findViewById(R.id.albumIDPTF);						//アルバムカウント
			titolCountLL = findViewById(R.id.titolCountLL);					//アーティストカウント
			tIDPTF = findViewById(R.id.tIDPTF);										//タイトル合計
			nIDPTF = findViewById(R.id.nIDPTF);										//タイトルカウント
			pp_pp_ll = findViewById(R.id.pp_pp_ll);								//二点間再生レイアウト
			pp_pp_start_tf = findViewById(R.id.pp_pp_start_tf);					//二点間再生開始点
			pp_pp_end_tf = findViewById(R.id.pp_pp_end_tf);						//二点間再生終了点
			pp_pp_ll.setVisibility(View.GONE);
			pp_bt_ll = findViewById(R.id.pp_bt_ll);					//buletooth情報
			pp_bt_ll.setVisibility(View.GONE);			//フィールドを非表示

			pp_vf = findViewById(R.id.pp_vf);									//中心部の表示枠
			slideInFromLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_left);			//左フリック
			slideInFromRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_right);			//右フリック
			//		dbMsg +=",pp_vf=" + pp_vf;/////////////////////////////////////
			LinearLayout jaket_ll = findViewById(R.id.jaket_ll);
			mpJakeImg = jaket_ll.findViewById(R.id.mpJakeImg);							//ジャケット
			dbMsg +=",mpJakeImg=" + mpJakeImg;/////////////////////////////////////
			//	mpJakeImg.setScaleType(ImageView.ScaleType.FIT_CENTER);				//FIT_CENTER   エリアの縦幅まで画像を拡大し中央に表示
			LinearLayout lilic_ll = findViewById(R.id.lilic_ll);
			lyric_tv = lilic_ll.findViewById(R.id.lyric_tv);					//歌詞表示
			dbMsg +=",lilic_tv=" + lyric_tv;/////////////////////////////////////
			lyric_tv.setMovementMethod(ScrollingMovementMethod.getInstance());
			registerForContextMenu(lyric_tv);
			//Manifestのアプリケーションテーマで	@style/Base.Theme.AppCompat		@style/Theme.AppCompat

			gestureDetector = new GestureDetector(this, this);			// GestureDetectorの生成
			artist_tv.setMovementMethod(ScrollingMovementMethod.getInstance());			//ScrollView を使わないで TextView に スクロールバーを表示する	http://kokufu.blogspot.jp/2012/12/scrollview-textview.html
			alubum_tv.setMovementMethod(ScrollingMovementMethod.getInstance());
			titol_tv.setMovementMethod(ScrollingMovementMethod.getInstance());
			dviceStyte.setMovementMethod(ScrollingMovementMethod.getInstance());

			ppPBT.setOnClickListener(this);
			ffPBT.setOnClickListener(this);
			rewPBT.setOnClickListener(this);
			vol_btn.setOnClickListener(this);
			vol_btn.setOnLongClickListener(this);
			artist_tv.setOnClickListener(this);															//アルバムアーティスト
			alubum_tv.setOnClickListener(this);														//アルバムー
			titol_tv.setOnClickListener(this);															//タイトル
			pp_pp_start_tf.setOnLongClickListener(this);					//二点間再生開始点
			pp_pp_end_tf.setOnLongClickListener(this);						//二点間再生終了点

			artist_tv.setOnKeyListener( this);
			alubum_tv.setOnKeyListener( this);
			titol_tv.setOnKeyListener( this);
			vol_btn.setOnKeyListener( this);
			rewPBT.setOnKeyListener( this);
			ffPBT.setOnKeyListener( this);
			saiseiSeekMP.setOnKeyListener( this);
			saiseiSeekMP.setOnSeekBarChangeListener (this);
			ppPBT.setOnKeyListener( this);

			convertFormat = new SimpleDateFormat("HH:mm:ss");
			convertFormat.setTimeZone(TimeZone.getTimeZone("UTC"));								//時差を抜いた時分秒表示
			//広告表示//////////////////////////////////////////////////
			advertisement_ll = findViewById(R.id.advertisement_ll);
			ad_layout = findViewById(R.id.ad_layout);
			nend_layout = findViewById(R.id.nend_layout);
			//////////////////////////////////////
			shigot_bangou =  syoki_activty_set ;			//	onWindowFocusChangedを経てaSetei； ;ボタンなどへのイベント割付け
			receiverSeisei();		//レシーバーを生成☆onStopで破棄しないとleaked発生
			dbMsg += ",toPlaying=" + toPlaying;
			toPlaying = send2Service( dataURL,nowList,true);
			dbMsg += ">>" + toPlaying;
			long end=System.currentTimeMillis();		// 終了時刻の取得
			dbMsg +=" ,経過=" +(int)((end - start)) + "mS";		//	<string name="">所要時間</string>
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**未使用*/
	@Override
	protected void onStart() {
		super.onStart();
		final String TAG = "onStart[MaraSonActivity]";
		String dbMsg= "←(onRestart;アクティビティ再表示←onStop)";/////////////////////////////////////
		try{
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**アフェリエイト、レシーバを生成*/
	@Override
	protected void onResume() {										//MediaPlayerを生成
		super.onResume();
		final String TAG = "onResume";
		String dbMsg= "[MaraSonActivity]←(onPause;アクティビティ再表示)";/////////////////////////////////////
		try{
			if(musicPlaylist == null){
				musicPlaylist = new MusicPlaylist(MaraSonActivity.this);
			}

			dbMsg = "adView="+ mAdView;//////////////////
			if( mAdView != null ){
				dbMsg +=",adWidth["+ adWidth+"×"+ adHight +"]";//////////////////
				mAdView.resume();             //二重表示さ有れる
			}
			dbMsg +=">>"+ mAdView;//////////////////
			dbMsg +=",nendAdView="+ nendAdView;//////////////////
			if( nendAdView != null ){
				nendAdView.resume();
			}
//			dbMsg +=">>"+ nendAdView;//////////////////
			//	myLog(TAG,dbMsg);
//			if( kidou_jyoukyou == kidou_notif  ){				//ノティフィケーションからの起動
//			}else {
//			}
			if( MPSIntent == null ){
				psSarviceUri = getPackageName() + getResources().getString(R.string.psSarviceUri);		//プレイヤーサービス	"com.hijiyam_koubou.marasongs.PlayerService";
				dbMsg += ">>psSarviceUri=" + psSarviceUri;/////////////////////////////////////
				MPSIntent = new Intent(MaraSonActivity.this, MusicPlayerService.class);
			}
//			if(toPlaying){
//				int mcPosition = 0;
//				dbMsg +=">>mcPosition="+ mcPosition + "[ms]";
//				sendPlaying( MPSIntent , dataFN , mcPosition);						//setされたActionを受け取って再生		<onStopTrackingTouch [aSetei]
//				toPlaying = false;
//			}

			int sMax = saiseiSeekMP.getMax();
			dbMsg += ",sMax=" + sMax + "[ms]";
			if(sMax < 1000){            //起動時
				int saiseiJikan = getPrefInt("pref_duration" , 0, MaraSonActivity.this);		//sharedPref.getInt("pref_duration" , 0);
				setSeekMax( saiseiJikan );
				int mcPosition = getPrefInt("pref_position" , 0, MaraSonActivity.this);		//sharedPref.getInt("pref_position" , 0);
				dbMsg += ">>mcPosition=" +  mcPosition + "/" +  saiseiJikan + "mS]";
				pointKousin(mcPosition);	//再生ポイント更新							//////////http://www.atmarkit.co.jp/ait/articles/1202/16/news130.html	①～　　はクリックした順番
			}

			dbMsg +="shigot_bangou;" + shigot_bangou;/////////////////////////////////////
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**アフェリエイトにpause*/
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressLint("NewApi")
	@Override
	protected void onPause() {										//レシーバーを破棄
		super.onPause();
		final String TAG = "onPause[MaraSonActivity]";
		String dbMsg= "←(別の;アクティビティをスタート)";/////////////////////////////////////
		try{
			dbMsg= "shigot_bangou;" + shigot_bangou;/////////////////////////////////////
			//		myLog(TAG,dbMsg);
			switch(shigot_bangou) {
//				case syoki_activty_set:		// 105;プリファレンスの読み込み
//					aSetei();		//フォーカス取得後のActibty設定
//					break;
				default:
					break;
			}
			dbMsg +=",ディスプレイ["+dWidth+" × "+ dHeigh +"]" ;/////////////////////////////////////////////////////////////////////////////////////////////////////////
			dbMsg +="adView="+ mAdView;//////////////////
			if( mAdView != null ){
				dbMsg +=",adWidth["+ adWidth+"×"+ adHight +"]";//////////////////
				mAdView.pause();
				//		dbMsg +=">>layout_ad["+ layout_ad.getWidth() +"×"+ layout_ad.getHeight() +"]";//////////////////
			}
			dbMsg +=">>"+ mAdView;//////////////////
			if( nendAdView != null ){
				nendAdView.pause();
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**未使用*/
	@Override
	protected void onRestart() {
		super.onRestart();
		final String TAG = "onRestart[MaraSonActivity]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**未使用*/
	@Override
	protected void onStop() {
		super.onStop();
		final String TAG = "onStop[MaraSonActivity]";
		String dbMsg= "←(onRestart;アクティビティ非表示←onPause)";/////////////////////////////////////
		try{
			dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			dbMsg += "mReceiver=" + mReceiver;////////////////////////
			dbMsg += ">>" + mReceiver;////////////////////////
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**アフェリエイト、レシーバ解除*/
	@Override
	protected void onDestroy() {
		super.onDestroy();
		final String TAG = "onDestro";
		String dbMsg= "";
		try{
//			dbMsg = "mVisualizer="+ mVisualizer;//////////////////
//			if( mVisualizer != null ){
//				mVisualizer.release();
//				mVisualizer = null;
//			}
			dbMsg += ",adView="+ mAdView;
			if( mAdView != null ){
				mAdView.destroy();
				dbMsg = ">>"+ mAdView;//////////////////
			}
//			reSetVol();			//設定されたミュートを解除
			//		audioManage.setStreamMute(AudioManager.STREAM_MUSIC, false);					// ミュート設定をONにする
			dbMsg += ",btReceiver="+ btReceiver;
			if(btReceiver != null){
				unregisterReceiver(btReceiver); // レシーバー解除
			}
			dbMsg += ",mReceiver="+ mReceiver;
			if( mReceiver != null ){
				unregisterReceiver(mReceiver); // レシーバー解除
			}
			dbMsg += ",IsPlaying="+ IsPlaying;
//			if(! IsPlaying){
//				Intent intent = new Intent( MaraSonActivity.this, MusicPlayerService.class);
//				intent.setAction(MusicPlayerService.ACTION_SYUURYOU_NOTIF);
//				startService(intent) ;
//			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	///////////////////////////////////////////////////////////////////////////////////
	public void pendeingMessege() {
		String titolStr = "制作中です";
		String mggStr = "最終リリースをお待ちください";
		messageShow(titolStr , mggStr);
	}

	public static String getPrefStr(String keyNmae , String defaultVal,Context context) {        //プリファレンスの読込み
		String retStr = "";
		final String TAG = "getPrefStr";
		String dbMsg="[MaraSonActivity]keyNmae=" + keyNmae + ",defaultVal=" + defaultVal;
		try {
//			Util UTIL = new Util();
//			retStr = Util.getPrefStr(keyNmae , defaultVal,context);
//
//			context = MaraSonActivity.this;
			dbMsg +=",context=" + context;
			String pefName = context.getResources().getString(R.string.pref_main_file);
			dbMsg +=",pefName=" + pefName;
			Util UTIL = new Util();
			retStr = Util.getPrefStr(keyNmae , defaultVal,context);
//			SharedPreferences shPref = context.getSharedPreferences(pefName , context.MODE_PRIVATE);        //	getSharedPreferences(prefFname,MODE_PRIVATE);
//			retStr = shPref.getString(keyNmae , defaultVal);
			dbMsg +=  ",retStr="  + retStr;
//			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return retStr;
	}

	public static int getPrefInt(String keyNmae , int defaultVal,Context context) {        //プリファレンスの読込み
		int retInt = -99;
		final String TAG = "getPrefStr";
		String dbMsg="[MaraSonActivity]keyNmae=" + keyNmae;
		Util UTIL = new Util();
		retInt = Util.getPrefInt(keyNmae , defaultVal,context);

//		try {
//			Context context = getApplicationContext();
//			dbMsg +=",context";
//			String pefName = context.getResources().getString(R.string.pref_main_file);
//			sharedPref = context.getSharedPreferences(pefName,context.MODE_PRIVATE);		//	getSharedPreferences(prefFname,MODE_PRIVATE);
//			myEditor = sharedPref.edit();
//			retInt = sharedPref.getInt(keyNmae , defaultVal);
//			dbMsg +=  ",retIn="  + retInt;
//			myLog(TAG, dbMsg);
//		} catch (Exception e) {
//			myErrorLog(TAG ,  dbMsg + "で" + e);
//		}
		return retInt;
	}


	public static boolean setPrefInt(String keyNmae , int wrightVal , Context context) {        //プリファレンスの読込み
		boolean retBool = false;
		final String TAG = "setPrefInt";
		String dbMsg="[MusicPlayerService]keyNmae=" + keyNmae;
		Util UTIL = new Util();
		retBool = Util.setPrefInt(keyNmae , wrightVal,context);
		return retBool;
	}

	public void messageShow(String titolStr , String mggStr) {
		Util UTIL = new Util();
		Util.messageShow(titolStr , mggStr , MaraSonActivity.this);
	}

	public static void myLog(String TAG , String dbMsg) {
		Util UTIL = new Util();
		Util.myLog(TAG , "[MaraSonActivity];" + dbMsg);
	}

	public static void myErrorLog(String TAG , String dbMsg) {
		Util UTIL = new Util();
		Util.myErrorLog(TAG , "[MaraSonActivity];" + dbMsg);
	}
}
/**
 * 起動時の
 * 2016/03-24 14:15:58.939: W/ResourceType(13787): Skipping entry 0x7f090019 in package table 0 because it is not complex!
 * 		R.javaで	public static final int dark_gray=0x7f090019;
 * 03-24 14:55:52.084: W/SQLiteConnectionPool(21184): A SQLiteConnection object for database '/data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db' was leaked!  Please fix your application to end transactions in progress properly and to close the database when it is no longer needed.
 *  20191112:play/pause誤動作；ppPBT.setContentDescription                  .setContentDescription

 * */
