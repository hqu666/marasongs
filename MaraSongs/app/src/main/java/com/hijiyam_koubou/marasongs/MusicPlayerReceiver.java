package com.hijiyam_koubou.marasongs;

import static com.hijiyam_koubou.marasongs.MusicService.ACTION_STATE_CHANGED;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Toast;

/*リモートコントロール（ロックスクリーンやBluethootアンプ）からブロードキャストされるマルチメディアイベントを受け取るレシーバ。 */
public class MusicPlayerReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		final String TAG = "onReceive";
		String dbMsg="[MusicPlayerReceiver]";/////////////////////////////////////
		try{
			dbMsg="context="+context;				
//				//ロックスクリーン；	android.app.ReceiverRestrictedContext@419e37f8
//				//プレイヤー；			android.app.ReceiverRestrictedContext@419e37f8,
			dbMsg +=",intent= " + intent;
				//ロックスクリーン； 	{ act=android.intent.action.MEDIA_BUTTON flg=0x10 cmp=com.hijiyam_koubou.marasongs/.MusicPlayerReceiver (has extras) }:
				//プレイヤー； 			{ act=android.intent.action.MEDIA_BUTTON flg=0x10 cmp=com.hijiyam_koubou.marasongs/.MusicPlayerReceiver }:
			String rAction = intent.getAction();
			dbMsg= dbMsg +",getAction=" + rAction;
			if(rAction == null){
				rAction = ACTION_STATE_CHANGED;
			}
			Intent MPSIntent = new Intent(context, MusicService.class);
			dbMsg +=":" + (
					rAction == android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY? "ACTION_AUDIO_BECOMING_NOISY" :
							rAction== Intent.ACTION_MEDIA_BUTTON ? "ボタン" : "不明");
				if (rAction.equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {			//外部スピーカー経由の出力イベントが発生
					dbMsg +=",Headphonesが外された" ;															//offしか発生しない
					Toast.makeText(context, "Headphones disconnected.", Toast.LENGTH_SHORT).show();
//					MPSIntent.setAction(MusicService.ACTION_PAUSE);
//					dbMsg +=">指定するAction>" + MPSIntent.getAction();/////////////////////////////////////
//					context.startService(MPSIntent);	//startService(new Intent(MusicService.ACTION_PAUSE));
				}else if (rAction.equals(android.media.AudioManager.ACTION_HEADSET_PLUG)) {
					dbMsg +=",Headphonessoucy装着" ;
//					MPSIntent.setAction(MusicService.ACTION_PAUSE);
//					dbMsg +=">指定するAction>" + MPSIntent.getAction();/////////////////////////////////////
//					context.startService(MPSIntent);	//startService(new Intent(MusicService.ACTION_PAUSE));
				} else if (rAction.equals(Intent.ACTION_MEDIA_BUTTON)) {
					dbMsg= dbMsg +",getExtras=" + intent.getExtras();
					if(  intent.getExtras() != null ){
						KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
						if (keyEvent.getAction() != KeyEvent.ACTION_DOWN) {
							return;
						}
					dbMsg +=":" + (
						keyEvent.getKeyCode() == KeyEvent.KEYCODE_HEADSETHOOK ? "PLAY/PAUSE" :
						keyEvent.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE ? "PLAY/PAUSE" :
						keyEvent.getKeyCode() == KeyEvent.KEYCODE_MEDIA_STOP ? "STOP" :
						keyEvent.getKeyCode() == KeyEvent.KEYCODE_MEDIA_NEXT ? "FF" :
						keyEvent.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PREVIOUS ? "Rew" : "Rew");
					if( keyEvent != null ){
						dbMsg= dbMsg +"を"  + (
						keyEvent.getAction() == KeyEvent.ACTION_DOWN ? "押した" :
						keyEvent.getAction() == KeyEvent.ACTION_UP ? "UP" :
						keyEvent.getAction() == KeyEvent.ACTION_MULTIPLE ? "MULTIPLE" : "Unknown");
						//ロックスクリーン以外はここでエラーで止まる
					}
					dbMsg +=",getKeyCode=" + keyEvent.getKeyCode();
					myLog(TAG,dbMsg);
					switch (keyEvent.getKeyCode()) {
					case KeyEvent.KEYCODE_HEADSETHOOK:
					case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
//						MPSIntent.putExtra("mIndex",mIndex);	//現リスト中の順番;
//						registerReceiver(mReceiver, mFilter);
						MPSIntent.setAction(MusicService.ACTION_PLAYPAUSE);
						break;
					case KeyEvent.KEYCODE_MEDIA_PLAY:
						MPSIntent.setAction(MusicService.ACTION_PLAY);
						break;
					case KeyEvent.KEYCODE_MEDIA_PAUSE:
						MPSIntent.setAction(MusicService.ACTION_PAUSE);
						break;
					case KeyEvent.KEYCODE_MEDIA_STOP:
						MPSIntent.setAction(MusicService.ACTION_STOP);
						break;
					case KeyEvent.KEYCODE_MEDIA_NEXT:
						MPSIntent.setAction(MusicService.ACTION_SKIP);;
						break;
					case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
						MPSIntent.setAction(MusicService.ACTION_REWIND);
						break;
					}
					dbMsg +=" ,ロックスクリーンから" + MPSIntent.getAction();/////////////////////////////////////
					ComponentName MPSName = context.startService(MPSIntent);
					dbMsg +=" ,ComponentName=" + MPSName;/////////////////////////////////////
				} else {
						dbMsg=dbMsg+ ",ロックスクリーンではない";/////////////////////////////////////
				}						//if(  intent.getExtras() != null ){
			}							//} else if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
			dbMsg=dbMsg+ ",終了";/////////////////////////////////////
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
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

/*
 * 	http://www.atmarkit.co.jp/ait/articles/1203/28/news128_3.html	BroadcastReceiverの使い方
 * ソースはRemoteControlExample
 * http://techbooster.jpn.org/andriod/ui/10298/
 * */
