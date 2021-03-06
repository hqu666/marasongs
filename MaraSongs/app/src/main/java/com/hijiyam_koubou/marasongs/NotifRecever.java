package com.hijiyam_koubou.marasongs;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;

public class NotifRecever extends Service {
	///////////////////////////////////////////////
//	public static final String ACTION_PLAY_PAUSE = "com.example.android.notification.action.PLAY_PAUSE";
//	public static final String ACTION_INIT = "com.example.android.notification.action.INIT";

	//@Override
	public void onReceive(Context context, Intent intent) {
		final String TAG = "onReceive[NotifRecever]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg="intent=" + intent ;/////////////////////////////////////
			String action = intent.getAction();
			dbMsg= dbMsg + ",action= " + action;
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		final String TAG = "onStartCommand[NotifRecever]";
		String dbMsg="Startで呼び出された";/////////////////////////////////////
		try{
			dbMsg= dbMsg +";intent=" + intent ;/////////////////////////////////////
			if( intent != null ){
				dbMsg=dbMsg + ",flags=" + flags ;/////////////////////////////////////
				dbMsg=dbMsg + ",startId=" + startId ;/////////////////////////////////////
				String action = intent.getAction();
				dbMsg=dbMsg + ",action=" + action ;/////////////////////////////////////
				if( action.equals( MusicPlayerService.ACTION_SYUURYOU ) ){
		//			stopSelf();																	//これが最後だと他のActiviｙから操作が完了できない
					MaraSonActivity MUP = new MaraSonActivity();								//音楽プレイヤー
					MUP.quitMe();		//
					//.faQuite();フォーカスが当たってからquitMeへfaQuite();	04-14 16:54:42.746: E/ActivityThread(4671): Service com.hijiyam_koubou.marasongs.MusicPlayerService has leaked IntentReceiver com.hijiyam_koubou.marasongs.BuletoohtReceiver@42fbcc40 that was originally registered here. Are you missing a call to unregisterReceiver()?
				}else{
					Intent MPSIntent = new Intent(getApplicationContext(),MusicPlayerService.class);	//parsonalPBook.thisではメモリーリークが起こる
					MPSIntent.setAction(action);				//	context.startService(new Intent(MusicPlayerService.ACTION_STOP));
					dbMsg= dbMsg + " ,ノティフィケーションから" + MPSIntent.getAction();/////////////////////////////////////
					ComponentName MPSName = getApplicationContext().startService(MPSIntent);
					dbMsg= dbMsg + " ,ComponentName=" + MPSName;/////////////////////////////////////
				}
			}
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		final String TAG = "onBind[NotifRecever]";
		String dbMsg="Bindで呼び出された場合のみ発生";/////////////////////////////////////
		try{
			dbMsg= dbMsg +";intent=" + intent ;/////////////////////////////////////
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
		return null;
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