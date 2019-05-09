package com.hijiyam_koubou.marasongs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;
import android.util.Log;

public class ZenkyokuHelper extends SQLiteOpenHelper {
	final static private int DB_VERSION = 1;
	private Context rContext;	//読出し元
	public String dbName;		//第２引数は、データベースファイルの名前です。この引数にnullを指定すると、データベースはメモリー上に作られます。

	public ZenkyokuHelper(Context context , String dFn) {					//アーティスト名の置き換えリスト
		super(context, dFn, null, DB_VERSION);
		final String TAG = "ZenkyokuHelper[ZenkyokuHelper]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			dbMsg="getPackageCodePath="+context.getPackageCodePath();/////////////////////////////////////
			rContext = context;						//第１引数; context ;読出し元;データベースを所有するコンテキストオブジェクトを指定します。
			dbName = dFn;							//第２引数; fileName ;データベースファイルの名前です。この引数にnullを指定すると、データベースはメモリー上に作られます。
			dbMsg += " , db=" + dbName;
			dbMsg +=",バージョン="+DB_VERSION;	//第4引数; version ;データベースのバージョンを指定します。
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {		// table create
		final String TAG = "onCreate[ZenkyokuHelper]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			String tName = rContext.getResources().getString(R.string.zenkyoku_table);
			dbMsg="テーブル名= "+ tName;/////////////////////////////////////
			String tSet = "create table " + tName +" (" +							//テーブル名；artist_rw_table
					"_id integer primary key autoincrement not null, "+ 			//作成したリストの連番
					"	AUDIO_ID text not null," +		//1.元々のID
					"	SORT_NAME text not null," +		//2.ALBUM_ARTISTを最短化して大文字化
					"	ARTIST text not null," +		//3.artist;クレジットアーティスト名
					"	ALBUM_ARTIST text, " +			//4.album_artist
					"	ALBUM text, " +					//5.album
					"	TRACK text, " +					//6.track;					//トラックNo,
					"	TITLE text, " +					//7.title;					//タイトル
					"	DURATION text, " +				//8.duration;				//再生時間
					"	YEAR text, " +					//9.MediaStore.Audio.Media.YEAR
					"	DATA text, " +					//10.data;					//URI
					"	MODIFIED text, " +				//11.idkousinnbi = cur.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED);
					"	COMPOSER text, " +				//12.idcomposer = cur.getColumnIndex(MediaStore.Audio.Media.COMPOSER);
					"	LAST_YEAR text " +				//13.MediaStore.Audio.Albums.LAST_YEAR
						");";
/*
	 				"	id text, "+ 										//MediaStore.Audio.Media._ID	updateに必要
					"	BOOKMARK text, " +				//idbOOKMARK = cur.getColumnIndex(MediaStore.Audio.Media.BOOKMARK);			//APIL8
					"	ALBUM_ART text, " +				//MediaStore.Audio.Albums.ALBUM_ART
*/
			dbMsg="tSet= "+tSet;/////////////////////////////////////
			db.execSQL(tSet);
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		final String TAG = "onUpgrade[ZenkyokuHelper]";
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

//http://ichitcltk.hustle.ne.jp/gudon/modules/pico_rd/index.php?content_id=74