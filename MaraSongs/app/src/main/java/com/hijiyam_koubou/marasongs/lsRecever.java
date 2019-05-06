package com.hijiyam_koubou.marasongs;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

	
public class lsRecever extends DeviceAdminReceiver {
	//ロックスクリーン用のレシーバ
	
    @Override
    public void onEnabled(Context context, Intent intent) {
		final String TAG = "onEnabled[lsRecever]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
		final String TAG = "onDisabled[lsRecever]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
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
