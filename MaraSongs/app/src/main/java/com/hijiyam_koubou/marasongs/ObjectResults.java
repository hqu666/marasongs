package com.hijiyam_koubou.marasongs;

import java.io.Serializable;

import android.util.Log;

public class ObjectResults  implements Serializable {
	static Object object;

	public  ObjectResults(Object object) {
		final String TAG = "ObjectResults[ObjectResults]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			dbMsg= "object=" + object;
			ObjectResults.this.object = object;
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}

	}

	public static  Object getObject( ) {
		final String TAG = "getObject[ObjectResults]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			dbMsg= "object=" +object;
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return object;

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

/*http://techbooster.jpn.org/andriod/application/7190/
 * 
 * オブジェクトをIntentで受け渡しする際には、入出力ストリームにて入出力が可能となるように、オブジェクトをシリアライズ(直列化)によって連続的なバイト列にする必要があります。
 * オブジェクトはメモリ上に連続して配置されているとは限らないからです。
よって、前述した通り、受け渡し対象となるオブジェクトのクラスは、java.io.Serializableインタフェースをimplementsしている必要があります。*/
}
