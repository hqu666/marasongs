package com.hijiyam_koubou.marasongs;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * サービス経由で音楽をコントロールするプレイヤー。
 * このプレイヤーは、ロック画面と Notification で状態の同期が取られる。
 */
public class MusicPlayerRemoteControlActivity extends Activity implements OnClickListener {

	public static final String ACTION_STATE_CHANGED = "com.example.android.remotecontrol.ACTION_STATE_CHANGED";			//sendPlayerState()
	public static final String ACTION_PLAYPAUSE = "com.example.android.remotecontrol.ACTION_PLAYPAUSE";
	public static final String ACTION_PLAY = "com.example.android.remotecontrol.ACTION_PLAY";
	public static final String ACTION_PAUSE = "com.example.android.remotecontrol.ACTION_PAUSE";
	public static final String ACTION_SKIP = "com.example.android.remotecontrol.ACTION_SKIP";
	public static final String ACTION_REWIND = "com.example.android.remotecontrol.ACTION_REWIND";
	public static final String ACTION_STOP = "com.example.android.remotecontrol.ACTION_STOP";
	public static final String ACTION_REQUEST_STATE = "com.example.android.remotecontrol.ACTION_REQUEST_STATE";
	public static final String ACTION_SYUURYOU = "SYUURYOU";					//追加１	；
	public static final String ACTION_PLAY_READ = "PLAY_READ";
	public static final String ACTION_PLAY_PAUSE = "com.example.android.notification.action.PLAY_PAUSE";
	public static final String ACTION_INIT = "com.example.android.notification.action.INIT";

	private static final String TAG = "MusicPlayerRemoteControlActivity";
	public TextView artist_tv;				//アルバムアーティスト	private TextView mTextViewArtist;
	public TextView creditArtistName_tf;	//クレジットされているアーティスト名
	public TextView artistTotalPTF;		//アルバムアーティス合計
	public TextView artistIDPTF;		//アルバムアーティスカウント
	public TextView alubum_tv;			//アルバム			private TextView mTextViewAlbum;
	public TextView alubumTotalPTF;		//アルバム合計
	public TextView albumIDPTF;			//アルバムカウント
	public TextView titol_tv;			//タイトル					private TextView mTextViewTitle;
	public TextView tIDPTF;				//タイトル合計
	public TextView nIDPTF;				//タイトルカウント
	public TextView albumAllPTF;		//全アルバムカウント
	public TextView titolAllPTF;		//全タイトルカウント
	public TextView infoPTF;			//URL
	public TextView saiseiPositionPTF;	//再生ポイント
	public TextView totalTimePTF;		//再生時間
	public TextView ruikei_jikan_tv;	//累積時間
	public TextView ruikei_kyoku_tv;	//累積曲数
	public TextView zenkai_ruikei_tv;	//前回の累積
	public ImageButton ppPBT;			//再生ボタン			private ImageButton mButtonPlayPause;
	public ImageButton stopPBT;				//終了ボタン
	public ImageButton ffPBT;			//送りボタン			private ImageButton mButtonSkip;
	public ImageButton rewPBT;			//戻しボタン			private ImageButton mButtonRewind;
	public ImageView mpJakeImg;			//ジャケット
	public SeekBar saiseiSeekMP;		//シークバー
//該当なし	private ImageButton mButtonStop;
	private Chronometer mChronometer;
	private long mCurrentPosition;
	private IntentFilter mFilter;

	private Handler mHandler = new Handler();
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			mHandler.post(new Runnable() {
				public void run() {
					final String TAG = "onReceive[MPRCActivity]";
					String dbMsg= "開始;";/////////////////////////////////////
					try{
					String action = intent.getAction();
					dbMsg= "action=" + action;////stop=com.example.android.remotecontrol.ACTION_STATE_CHANGED

					Notification notification = intent.getParcelableExtra("notification");		// 【1】IntentからNotificationを取り出します。
					dbMsg +=".notification=" + notification.toString();/////////////////////////////////////
					boolean isPlaying = intent.getBooleanExtra("isPlaying", false);			// 【2】Serviceであれば、状態を保持できるため、現在再生中かどうかをIntentから取得します。
					dbMsg +=".isPlaying=" + isPlaying;/////////////////////////////////////
					long baseTime = intent.getLongExtra("baseTime", 0);
					dbMsg +=".baseTime=" + baseTime;/////////////////////////////////////
					NotificationManager manager =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
					// 【3】android.app.NotificationManagerを取得

//					if (ACTION_INIT.equals(action)) {
//					intent.setAction(ACTION_PLAY_PAUSE);
//					} else if (ACTION_PLAY_PAUSE.equals(intent.getAction())) {
//					if (isPlaying) {
//					long current = System.currentTimeMillis() - baseTime;
//					// 【4】
//					notification.contentView.setImageViewResource(
//					R.id.playpause, R.drawable.media_play_s);
//					notification.contentView.setChronometer(
//					R.id.chronometer, SystemClock.elapsedRealtime() - current, null, false);
//					intent.putExtra("current", current);
//					} else {
//					long current = intent.getLongExtra("current", 0);
//					notification.contentView.setImageViewResource(
//					R.id.playpause, R.drawable.media_pause_s);
//					notification.contentView.setChronometer(
//					R.id.chronometer, SystemClock.elapsedRealtime() - current, null, true);
//					intent.putExtra("baseTime", System.currentTimeMillis() - current);
//					}
//					intent.putExtra("isPlaying", !isPlaying);
//					intent.putExtra("notification", notification);
//					}
//					// 【5】
//					PendingIntent pendingIntent = PendingIntent.getBroadcast(
//					context, R.id.playpause, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//					// 【6】
//					notification.contentView.setOnClickPendingIntent(R.id.playpause, pendingIntent);
//					// 【7】
//					manager.notify(R.id.button3, notification);

					//						dbMsg= "artist=" + intent.getStringExtra("artist");/////////////////////////////////////
//						artist_tv.setText(intent.getStringExtra("artist"));
//						alubum_tv.setText(intent.getStringExtra("album"));
//						titol_tv.setText(intent.getStringExtra("title"));
						String state = intent.getStringExtra("state");
						dbMsg +="state=" + state;/////////////////////////////////////
						mCurrentPosition = intent.getIntExtra("mcPosition", 0);
						dbMsg +=",mCurrentPosition=" + mCurrentPosition ;/////////////////////////////////////
						myLog(TAG,dbMsg);
//						mChronometer.setBase(SystemClock.elapsedRealtime() - mCurrentPosition);
						if( state != null ){
							if (state.equals(MusicPlayerService.State.Playing.toString())) {
								playing();
							} else if (state.equals(MusicPlayerService.State.Paused.toString())) {
								paused();
							} else if (state.equals(MusicPlayerService.State.Stopped.toString())) {
								stopped();
							}
						}
						myLog(TAG,dbMsg);
					}catch (Exception e) {
						myErrorLog(TAG,dbMsg + "で"+e.toString());
					}
				}
			});
		}
	};

//		http://www.atmarkit.co.jp/ait/articles/1202/16/news130_2.html

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final String TAG = "onCreate[MPRCActivity]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
//			setContentView(R.layout.remo_control);										//R.layout.music_player
//			artist_tv = (TextView) findViewById(R.id.artist);								//アルバムアーティスト
//			alubum_tv = (TextView) findViewById(R.id.album);							//アルバム
//			titol_tv = (TextView) findViewById(R.id.title);									//タイトル
//			mChronometer = (Chronometer) findViewById(R.id.chronometer);		//経過時間表示
//			ppPBT = (ImageButton) findViewById(R.id.ppPButton);						//再生ボタン
//			stopPBT = (ImageButton) findViewById(R.id.stopPButton);				//終了ボタン
////			ffPBT = (ImageButton) findViewById(R.id.ffPButton);			//送りボタン
////			rewPBT = (ImageButton) findViewById(R.id.rewPButton);		//戻しボタン
//
//			ppPBT.setOnClickListener(this);
//			stopPBT.setOnClickListener(this);
////			ffPBT.setOnClickListener(this);
////			rewPBT.setOnClickListener(this);
//
//			mFilter = new IntentFilter();
//			mFilter.addAction(MusicPlayerService.ACTION_STATE_CHANGED);
//			setContentView(R.layout.activity_mara_son);								//R.layout.music_player
//			artist_tv = (TextView) findViewById(R.id.artist_tv);						//アルバムアーティスト
//			creditArtistName_tf = (TextView) findViewById(R.id.creditArtistName_tf);	//クレジットされているアーティスト名
//			alubum_tv = (TextView) findViewById(R.id.alubum_tv);						//アルバムー
//			titol_tv = (TextView) findViewById(R.id.titol_tv);						//タイトルスピナー
//			artistTotalPTF = (TextView) findViewById(R.id.artistTotalPTF);			//アルバムアーティス合計
//			artistIDPTF = (TextView) findViewById(R.id.artistIDPTF);				//アルバムアーティスカウント
//			alubumTotalPTF = (TextView) findViewById(R.id.alubumTotalPTF);			//アルバム合計
//			albumIDPTF = (TextView) findViewById(R.id.albumIDPTF);					//アルバムカウント
////			albumAllPTF = (TextView) findViewById(R.id.albumAllPTF);					//全アルバムカウント
//			titolAllPTF = (TextView) findViewById(R.id.titolAllPTF);					//全タイトルカウント
//			tIDPTF = (TextView) findViewById(R.id.tIDPTF);							//タイトル合計
//			nIDPTF = (TextView) findViewById(R.id.nIDPTF);							//タイトルカウント
//			infoPTF = (TextView) findViewById(R.id.infoPTF);						//URL
//			saiseiPositionPTF = (TextView) findViewById(R.id.saiseiPositionPTF);	//再生ポイント
//
//			totalTimePTF = (TextView) findViewById(R.id.totalTimePTF);				//再生時間
//			mpJakeImg = (ImageView) findViewById(R.id.mpJakeImg);					//ジャケット
//			saiseiSeekMP = (SeekBar) findViewById(R.id.saiseiSeekMP);					//シークバー
//			ruikei_jikan_tv = (TextView) findViewById(R.id.ruikei_jikan_tv);	//累積時間
//			ruikei_kyoku_tv = (TextView) findViewById(R.id.ruikei_kyoku_tv);	//累積曲数
//			zenkai_ruikei_tv = (TextView) findViewById(R.id.zenkai_ruikei_tv);	//前回の累積
//			ppPBT = (ImageButton) findViewById(R.id.ppPButton);			//再生ボタン
//			ffPBT = (ImageButton) findViewById(R.id.ffPButton);			//送りボタン
//			rewPBT = (ImageButton) findViewById(R.id.rewPButton);		//戻しボタン
//
//			ppPBT.setOnClickListener(this);
//			ffPBT.setOnClickListener(this);
//			rewPBT.setOnClickListener(this);

			mFilter = new IntentFilter();
			mFilter.addAction(MusicPlayerService.ACTION_STATE_CHANGED);
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		final String TAG = "onResume[MPRCActivity]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			registerReceiver(mReceiver, mFilter);
			startService(new Intent(MusicPlayerService.ACTION_REQUEST_STATE));
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		final String TAG = "onPause[MPRCActivity]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			unregisterReceiver(mReceiver);
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		final String TAG = "onDestroy[MPRCActivity]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	@Override
	public void onClick(View v) {
		final String TAG = "onClick[MPRCActivity]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			if (v == ppPBT) {
				if (ppPBT.getContentDescription().equals(getResources().getText(R.string.play))) {
		//		if (mButtonPlayPause.getContentDescription().equals(getResources().getText(R.string.play))) {
					startService(new Intent(MusicPlayerService.ACTION_PLAY));
					playing();
				} else {
					startService(new Intent(MusicPlayerService.ACTION_PAUSE));
					paused();
				}
			} else if (v == ffPBT) {
				startService(new Intent(MusicPlayerService.ACTION_SKIP));
				mChronometer.stop();
				mChronometer.setBase(SystemClock.elapsedRealtime());
			} else if (v == rewPBT) {
				startService(new Intent(MusicPlayerService.ACTION_REWIND));
				mChronometer.setBase(SystemClock.elapsedRealtime());
			} else if (v == stopPBT) {
				Intent intent = new Intent(MusicPlayerService.ACTION_STOP);
				intent.putExtra("cancel", true);
				startService(intent);
				stopped();
			}
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		final String TAG = "onKeyDown[MPRCActivity]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			switch (keyCode) {
			case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
			case KeyEvent.KEYCODE_HEADSETHOOK:
				onClick(ppPBT);
				return true;
			}
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return super.onKeyDown(keyCode, event);
	}

	private void paused() {
		final String TAG = "paused[MPRCActivity]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			mCurrentPosition = SystemClock.elapsedRealtime() - mChronometer.getBase();
			mChronometer.stop();
			ppPBT.setImageResource(R.drawable.pl_r_btn);				//media_play
			ppPBT.setContentDescription(getResources().getText(R.string.play));
			myLog(TAG,dbMsg);
//			MaraSonActivity MUP = new MaraSonActivity();								//音楽プレイヤー
//			MUP.paused();
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	private void playing() {
		final String TAG = "playing[MPRCActivity]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			mChronometer.setBase(SystemClock.elapsedRealtime() - mCurrentPosition);
			mChronometer.start();
			ppPBT.setImageResource(R.drawable.pousebtn);								//media_pause
			ppPBT.setContentDescription(getResources().getText(R.string.pause));
			myLog(TAG,dbMsg);
//			MaraSonActivity MUP = new MaraSonActivity();								//音楽プレイヤー
//			MUP.playing();
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	private void stopped() {
		final String TAG = "stopped[MPRCActivity]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			mChronometer.stop();
			ppPBT.setImageResource(R.drawable.pl_r_btn);								//media_play
			ppPBT.setContentDescription(getResources().getText(R.string.play));
			mChronometer.setBase(SystemClock.elapsedRealtime());
			myLog(TAG,dbMsg);
//			MaraSonActivity MUP = new MaraSonActivity();								//音楽プレイヤー
//			MUP.stopped();
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}
	///////////////////////////////////////////////////////////////////////////////////
	public static void myLog(String TAG , String dbMsg) {
		MyUtil MyUtil = new MyUtil();
		MyUtil.myLog(TAG , dbMsg);
	}

	public static void myErrorLog(String TAG , String dbMsg) {
		MyUtil MyUtil = new MyUtil();
		MyUtil.myErrorLog(TAG , dbMsg);
	}
}
