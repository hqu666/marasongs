package com.hijiyam_koubou.marasongs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MediaScannerBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final String TAG = "onReceive[MediaScannerBroadcastReceiver]";
		String dbMsg="開始";/////次はonCreate
		try{
			String action = intent.getAction();
			dbMsg="ACTION_MEDIA_SCANNER_FINISHED=" + Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action);
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}


	} // END onReceive()

/*
 *			
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uriToSD));
			//			Intent intent = new Intent();
//			intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);			// actionコード
			/*http://tsukaayapontan.web.fc2.com/doc/media/media.html
			 * Androidが自動的にデータベースを更新するタイミングは、以下の2通りです。
端末起動時；暗黙Intent(action=android.intent.action.BOOT_COMPLETED)を受信した場合
SDカードがマウントされた時；暗黙Intent(action=android.intent.action.MEDIA_MOUNTED/data:scheme=file)を受信した場合
受信IntentはAndroid Open Source Projectからダウンロードしたソースコード（Android 4.0.3)で確認しています。
　GalaxySⅢでは、USB経由でファイルをPC⇒Android端末へ転送したタイミングでもMediaScannerServiceが起動されていました
　（暗黙Intent(action=android.intent.action.MTP_FILE_SCAN)がブロードキャストされています）。
 * */


	///////////////////////////////////////////////////////////////////////////////////
	public static void myLog(String TAG , String dbMsg) {
		MyUtil MyUtil = new MyUtil();
		MyUtil.myLog(TAG , dbMsg);
	}

	public static void myErrorLog(String TAG , String dbMsg) {
		MyUtil MyUtil = new MyUtil();
		MyUtil.myErrorLog(TAG , dbMsg);
	}




} // END class