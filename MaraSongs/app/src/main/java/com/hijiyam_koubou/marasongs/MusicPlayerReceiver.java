package com.hijiyam_koubou.marasongs;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

/*リモートコントロール（ロックスクリーンやBluethootアンプ）からブロードキャストされるマルチメディアイベントを受け取るレシーバ。 */
public class MusicPlayerReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		final String TAG = "onReceive[MusicPlayerReceiver]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg="context="+context;				
//				//ロックスクリーン；	android.app.ReceiverRestrictedContext@419e37f8
//				//プレイヤー；			android.app.ReceiverRestrictedContext@419e37f8,
			dbMsg +=",intent= " + intent;
				//ロックスクリーン； 	{ act=android.intent.action.MEDIA_BUTTON flg=0x10 cmp=com.hijiyam_koubou.marasongs/.MusicPlayerReceiver (has extras) }:
				//プレイヤー； 			{ act=android.intent.action.MEDIA_BUTTON flg=0x10 cmp=com.hijiyam_koubou.marasongs/.MusicPlayerReceiver }:
			dbMsg= dbMsg +",getAction=" + intent.getAction();
			Intent MPSIntent = new Intent(context, MusicPlayerService.class);
			dbMsg +=":" + (
				intent.getAction() == android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY? "ACTION_AUDIO_BECOMING_NOISY" :
				intent.getAction() == Intent.ACTION_MEDIA_BUTTON ? "ボタン" : "不明");
				if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {			//外部スピーカー経由の出力イベントが発生
					dbMsg +=",Headphonesが外された" ;															//offしか発生しない
					Toast.makeText(context, "Headphones disconnected.", Toast.LENGTH_SHORT).show();
//					MPSIntent.setAction(MusicPlayerService.ACTION_PAUSE);						//		ACTION_PLAYPAUSE
//					dbMsg +=">指定するAction>" + MPSIntent.getAction();/////////////////////////////////////
//					context.startService(MPSIntent);	//startService(new Intent(MusicPlayerService.ACTION_PAUSE));
				}else if (intent.getAction().equals(android.media.AudioManager.ACTION_HEADSET_PLUG)) {		
					dbMsg +=",Headphonessoucy装着" ;
//					MPSIntent.setAction(MusicPlayerService.ACTION_PAUSE);						//		ACTION_PLAYPAUSE
//					dbMsg +=">指定するAction>" + MPSIntent.getAction();/////////////////////////////////////
//					context.startService(MPSIntent);	//startService(new Intent(MusicPlayerService.ACTION_PAUSE));
				} else if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
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
						keyEvent.getAction() == KeyEvent.ACTION_MULTIPLE ? "MULTIPLE" : "Unknown");			//ロックスクリーン以外はここでエラーで止まる
					}
					dbMsg +=",getKeyCode=" + keyEvent.getKeyCode();
					myLog(TAG,dbMsg);
					switch (keyEvent.getKeyCode()) {
					case KeyEvent.KEYCODE_HEADSETHOOK:
					case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
//						MPSIntent.putExtra("mIndex",mIndex);	//現リスト中の順番;
//						registerReceiver(mReceiver, mFilter);
						MPSIntent.setAction(MusicPlayerService.ACTION_PLAYPAUSE);	//	ontext.startService(new Intent(MusicPlayerService.ACTION_PLAYPAUSE));
						break;
					case KeyEvent.KEYCODE_MEDIA_PLAY:
						MPSIntent.setAction(MusicPlayerService.ACTION_PLAY);				//context.startService(new Intent(MusicPlayerService.ACTION_PLAY));
						break;
					case KeyEvent.KEYCODE_MEDIA_PAUSE:
						MPSIntent.setAction(MusicPlayerService.ACTION_PAUSE);			//	context.startService(new Intent(MusicPlayerService.ACTION_PAUSE));
						break;
					case KeyEvent.KEYCODE_MEDIA_STOP:
						MPSIntent.setAction(MusicPlayerService.ACTION_STOP);				//	context.startService(new Intent(MusicPlayerService.ACTION_STOP));
						break;
					case KeyEvent.KEYCODE_MEDIA_NEXT:
						MPSIntent.setAction(MusicPlayerService.ACTION_SKIP);				//	context.startService(new Intent(MusicPlayerService.ACTION_SKIP));
						break;
					case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
						MPSIntent.setAction(MusicPlayerService.ACTION_REWIND);			//	context.startService(new Intent(MusicPlayerService.ACTION_REWIND));					// plays the previous song
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
		Util UTIL = new Util();
		Util.myLog(TAG , dbMsg);
	}

	public static void myErrorLog(String TAG , String dbMsg) {
		Util UTIL = new Util();
		Util.myErrorLog(TAG , dbMsg);
	}

}

/*
 * 	http://www.atmarkit.co.jp/ait/articles/1203/28/news128_3.html	BroadcastReceiverの使い方
 * ソースはRemoteControlExample
 * http://techbooster.jpn.org/andriod/ui/10298/
 * */
