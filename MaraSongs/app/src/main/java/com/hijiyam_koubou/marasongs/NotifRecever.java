package com.hijiyam_koubou.marasongs;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class NotifRecever extends BroadcastReceiver {
	/////////////////////////////////////////////// Service
	public static final String ACTION_PLAYPAUSE = "com.example.android.remotecontrol.ACTION_PLAYPAUSE";
//	public static final String ACTION_INIT = "com.example.android.notification.action.INIT";

	//@Override
	public void onReceive(Context context, Intent intent) {
		final String TAG = "onReceive";
		String dbMsg="";
		try{
			dbMsg +="intent=" + intent ;/////////////////////////////////////
			String action = intent.getAction();
			dbMsg +=",action= " + action;
			dbMsg += " ,SDK_INT="+android.os.Build.VERSION.SDK_INT;
			if( action.equals( MusicService.ACTION_SYUURYOU ) ){
				//			stopSelf();																	//これが最後だと他のActiviｙから操作が完了できない
				MaraSonActivity MUP = new MaraSonActivity();								//音楽プレイヤー
				MUP.quitMe();		//
				MuList ML =new MuList();
//				ML.quitBody();
				ML.receiverHaki();
					ML.finish();
			}else {
				Intent MPSIntent = new Intent(context, MusicService.class);    //parsonalPBook.thisではメモリーリークが起こる
				MPSIntent.setAction(action);                //	context.startService(new Intent(MusicPlayerService.ACTION_STOP));
				dbMsg += " ,ノティフィケーションから" + MPSIntent.getAction();/////////////////////////////////////
				ComponentName MPSName = context.startService(MPSIntent);
				dbMsg += " ,ComponentName=" + MPSName;
			}
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}
	///////////////////////////////////////////////////////////////////////////////////
	public static void myLog(String TAG , String dbMsg) {
		MyUtil MyUtil = new MyUtil();
		MyUtil.myLog(TAG , "[NotifRecever]"+dbMsg);
	}

	public static void myErrorLog(String TAG , String dbMsg) {
		MyUtil MyUtil = new MyUtil();
		MyUtil.myErrorLog(TAG , "[NotifRecever]"+dbMsg);
	}
}