package com.hijiyam_koubou.marasongs;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * 外部ストレージから音楽ファイルを探すための非同期タスク。
 */
public class PrepareMusicRetrieverTask extends AsyncTask<Context, Void, List<Item>> {
	private MusicRetrieverPreparedListener mListener;

	public PrepareMusicRetrieverTask(MusicRetrieverPreparedListener listener) {
		final String TAG = "PrepareMusicRetrieverTask";
		String dbMsg= "[PrepareMusicRetrieverTask];";
		try{
			mListener = listener;
			dbMsg= "mListener=" + mListener.getClass().getName();/////////////////////////////////////
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	@Override
	protected List<Item> doInBackground(Context... arg) {
		final String TAG = "doInBackground[PrepareMusicRetrieverTask]";
		String dbMsg= "開始;";/////////////////////////////////////
		List<Item> retList = null;
		try{
//			dbMsg= "arg[0]=" + arg[0];/////////////////////////////////////
//			retList = new LinkedList<Item>();
//			if(arg[0] != null){
//				retList = Item.getItems(arg[0]);
//			}
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return retList;
	}

	@Override
	protected void onPostExecute(List<Item> result) {
		final String TAG = "onPostExecute[PrepareMusicRetrieverTask]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			if( result == null ){
				dbMsg= "result=" + result;
			} else {
				dbMsg= "result=" + result.size() + "件";
			}
	//		myLog(TAG,dbMsg);
			mListener.onMusicRetrieverPrepared(result);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	public interface MusicRetrieverPreparedListener {
		void onMusicRetrieverPrepared(List<Item> items);
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
