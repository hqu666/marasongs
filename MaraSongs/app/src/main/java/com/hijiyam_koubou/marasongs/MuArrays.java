package com.hijiyam_koubou.marasongs;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import android.util.Log;

public class MuArrays  implements Comparable<Object>{
	public Map<String, Object> artistMap;				//アーティストリスト用
	public static  List<Map<String, Object>> artistAL;		//アーティストリスト用ArrayList
	public Map<String, Object> albumMap;			//アルバムトリスト用
	public List<Map<String, Object>> albumAL;		//アルバムリスト用ArrayList
	public Map<String, Object> titolMap;				//タイトルリスト用
	public List<Map<String, Object>> titolAL;			//タイトルムリスト用ArrayList


	public MuArrays(List<Map<String, Object>> artistAL) {
		final String TAG = "MuArrays[MuArrays]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg= "artistAL=" + artistAL.size() +"件書き込み";/////////////////////////////////////
			MuArrays.artistAL = artistAL;
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}


	public void setArtistList( List<Map<String, Object>> artistAL ) throws IOException {			//dataURIを読み込みながら欠けデータ確認
		final String TAG = "setArtistList[MuArrays]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			long start = System.currentTimeMillis();		// 開始時刻の取得
			dbMsg= "artistAL=" + artistAL.size() +"件書き込み";/////////////////////////////////////
			MuArrays.artistAL = artistAL;
			long end=System.currentTimeMillis();		// 終了時刻の取得
			dbMsg=dbMsg +";"+ (int)((end - start)) + "m秒で終了";
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}

	public static List<Map<String, Object>>  getArtistList(  ) throws IOException {			//dataURIを読み込みながら欠けデータ確認
		final String TAG = "getArtistList[MuArrays]";
		String dbMsg="開始";/////////////////////////////////////
		List<Map<String, Object>> retList = null;
		try{
			long start = System.currentTimeMillis();		// 開始時刻の取得
			retList = MuArrays.artistAL;
			if( retList != null ){
				dbMsg= "retList=" + retList.size() +"件読出し";/////////////////////////////////////
			}
			long end=System.currentTimeMillis();		// 終了時刻の取得
			dbMsg=dbMsg +";"+ (int)((end - start)) + "m秒で終了";
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return retList;
	}

	@Override
	public int compareTo(Object another) {
		final String TAG = "compareTo[MuArrays]";
		String dbMsg= "開始;";/////////////////////////////////////
		int result = 0;
		try{
			dbMsg= "another=" + another;/////////////////////////////////////
			if (another == null) {
				return 1;
			}
			if (result != 0) {
				return result;
			}
			myLog(TAG,dbMsg );
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return result;
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
