package com.hijiyam_koubou.marasongs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ArtistHelper extends SQLiteOpenHelper {
	final static private int DB_VERSION = 1;
	private Context rContext;	//読出し元
	public String dbName;		//第２引数は、データベースファイルの名前です。この引数にnullを指定すると、データベースはメモリー上に作られます。

	public ArtistHelper(Context context , String dFn) {					//アーティスト名の置き換えリスト
		super(context, dFn, null, DB_VERSION);
		final String TAG = "ArtistHelper[ArtistHelper]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			dbMsg="getPackageCodePath="+context.getPackageCodePath();/////////////////////////////////////
			rContext = context;						//第１引数; context ;読出し元;データベースを所有するコンテキストオブジェクトを指定します。
			dbName = dFn;							//第２引数; fileName ;データベースファイルの名前です。この引数にnullを指定すると、データベースはメモリー上に作られます。
			dbMsg += " , db=" + dbName;
			dbMsg=dbMsg + ",バージョン="+DB_VERSION;	//第4引数; version ;データベースのバージョンを指定します。
	//		myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {		// table create
		final String TAG = "onCreate[ArtistHelper]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			String tName = rContext.getResources().getString(R.string.artist_table);
			dbMsg="テーブル名= "+ tName;/////////////////////////////////////
			String tSet = "create table " + tName +" (" +			//テーブル名；artist_rw_table
					"_id  integer primary key autoincrement not null, "+ 
					"	ARTIST text," +				//1,MediaStore.Audio.Albums.ARTIST
					"	ALBUM_ARTIST text," +		//2
					"	ALBUM text, " +				//3,MediaStore.Audio.Albums.ALBUM_ART
					"	ALBUM_ART text, " +		//4,MediaStore.Audio.Albums.ALBUM_ART
					"	SUB_TEXT text" +				//5.アーティストリスト生成用の集約情報
					");"; 
			/*	
					"	FIRST_YEAR text, " +		//5.MediaStore.Audio.Albums.FIRST_YEAR
					"	LAST_YEAR text, " +			//6.MediaStore.Audio.Albums.LAST_YEAR
			 					"	YEAR text, " +					//5,FIRST_YEAR-LAST_YEAR:NUMBER_OF_ALBUM/NUMBER_OF_SONGS
					"	NUMBER_OF_ALBUM text, " +	
					"	NUMBER_OF_SONGS text" +		//MediaStore.Audio.Albums.NUMBER_OF_SONGS
//					"	NOS integer, " +		//6,NUMBER_OF_SONGS
//					"	SORT_NAME text, " +		//7.the抜き大文字
*/
			dbMsg="tSet= "+tSet;/////////////////////////////////////
			db.execSQL(tSet);
	//		myLog(TAG, dbMsg);
	//		db.execSQL("insert into " + tName +"(motName,albumName , sakiName) values ('Beck', 'Beck, Bogert & Appice', 'Beck, Bogert & Appice');");
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		final String TAG = "onUpgrade[ArtistHelper]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
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