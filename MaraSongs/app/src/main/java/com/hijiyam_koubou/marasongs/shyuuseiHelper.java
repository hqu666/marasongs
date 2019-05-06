package com.hijiyam_koubou.marasongs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class shyuuseiHelper extends SQLiteOpenHelper {
	final static private int DB_VERSION = 1;
	private Context rContext;	//読出し元
	public String dbName;		//第２引数は、データベースファイルの名前です。この引数にnullを指定すると、データベースはメモリー上に作られます。

	public shyuuseiHelper(Context context , String dFn) {					//アーティスト名の置き換えリスト
		super(context, dFn, null, DB_VERSION);
		final String TAG = "shyuuseiHelper[shyuuseiHelper]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			dbMsg="getPackageCodePath="+context.getPackageCodePath();/////////////////////////////////////
			rContext = context;						//第１引数; context ;読出し元;データベースを所有するコンテキストオブジェクトを指定します。
			dbName = dFn;							//第２引数; fileName ;データベースファイルの名前です。この引数にnullを指定すると、データベースはメモリー上に作られます。
			dbMsg += " , db=" + dbName;
			dbMsg=dbMsg + ",バージョン="+DB_VERSION;	//第4引数; version ;データベースのバージョンを指定します。
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {		// table create
		final String TAG = "onCreate[shyuuseiHelper]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			String tName = rContext.getResources().getString(R.string.shyuusei_table);
			dbMsg="テーブル名= "+ tName;/////////////////////////////////////
			String tSet = "create table " + tName +" (" +							//テーブル名；artist_rw_table
					"_id integer primary key autoincrement not null, "+ 			//作成したリストの連番
					"	ARTIST text not null," +	//artist;				//クレジットアーティスト名
					"	ALBUM_ARTIST text, " +		//album_artist
					"	ALBUM text, " +					//album
					"	TRACK text, " +				//track;					//トラックNo,
					"	TITLE text, " +				//title;					//タイトル
					"	DURATION text, " +			//duration;				//再生時間
					"	YEAR text, " +			//MediaStore.Audio.Media.YEAR
					"	DATA text, " +				//data;					//URI
					"	MODIFIED text, " +				//idkousinnbi = cur.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED);
					"	COMPOSER text, " +				//idcomposer = cur.getColumnIndex(MediaStore.Audio.Media.COMPOSER);
					"	LAST_YEAR text " +				//MediaStore.Audio.Albums.LAST_YEAR
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