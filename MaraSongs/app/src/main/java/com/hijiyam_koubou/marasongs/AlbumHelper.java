package com.hijiyam_koubou.marasongs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AlbumHelper extends SQLiteOpenHelper {
	final static private int DB_VERSION = 1;
	private Context rContext;	//読出し元
	public String dbName;		//第２引数は、データベースファイルの名前です。この引数にnullを指定すると、データベースはメモリー上に作られます。

	public AlbumHelper(Context context , String dFn) {					//アーティスト名の置き換えリスト
		super(context, dFn, null, DB_VERSION);
		final String TAG = "AlbumHelper[AlbumHelper]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			dbMsg="getPackageCodePath="+context.getPackageCodePath();/////////////////////////////////////
			rContext = context;						//第１引数; context ;読出し元;データベースを所有するコンテキストオブジェクトを指定します。
			dbName = dFn;							//第２引数; fileName ;データベースファイルの名前です。この引数にnullを指定すると、データベースはメモリー上に作られます。
			dbMsg += " , db=" + dbName;
			dbMsg +=",バージョン="+DB_VERSION;	//第4引数; version ;データベースのバージョンを指定します。
	//		myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {		// table create
		final String TAG = "onCreate[AlbumHelper]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			String tName = "album_table";
			dbMsg="テーブル名= "+ tName;/////////////////////////////////////
			String tSet = "create table " + tName +" (" +			//テーブル名；artist_rw_table
					"_id  integer primary key autoincrement not null, "+	//[3/12]_id(1 : INTEGER)1940210128
					"ALBUM_ID text," +				//[11/12]album_id(1)>>[>>6295010392320328144]BBC Sessions [Disc 1] [Live] >> 15項目
					"ALBUM text," +				//5,[4/12]album(3 : STRING)BBC Sessions [Disc 1] [Live]
					"NUM_SONGS text," +									//[0/12]numsongs(1 : INTEGER)28
					"ARTIST text," +										//[1/12]artist(3 : STRING)Led Zeppelin
					"FIRST_YEAR text," +				//[9/12]maxyear(1 : INTEGER)1997[10/12]
					"LAST_YEAR text" +				//[10/12]minyear(1 : INTEGER)1997				//integer
//					"ARTIST_ID integer," +	//[7/12]artist_id(1 : INTEGER)-138840664
//					"ARTIST_KEY text," +	//[8/12]artist_key(3 : STRING)403230045c32484832403a44
//					"SORT_NAME text," +	//2.the抜き大文字
//					"ALBUM_ARTIST text," +			//4,ALBUM_ARTIST
//					"ALBUM_KEY text, " +			//[6/12]album_key(3 : STRING)2c2c2e044e324e4e3a46444e04303a4e2e041504403a5432
//					"ALBUM_ART text, " +			//6,[5/12]album_art(0 : NULL)
					");";
			dbMsg="tSet= "+tSet;/////////////////////////////////////
			db.execSQL(tSet);
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		final String TAG = "onUpgrade[AlbumHelper]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}
	///////////////////////////////////////////////////////////////////////////////////
	public static void myLog(String TAG , String dbMsg) {
		MyUtil MyUtil = new MyUtil();
		MyUtil.myLog(TAG , dbMsg);
	}

	public static void myErrorLog(String TAG , String dbMsg) {
		MyUtil MyUtil = new MyUtil();
		MyUtil.myErrorLog(TAG , dbMsg);
	}
}

//http://ichitcltk.hustle.ne.jp/gudon/modules/pico_rd/index.php?content_id=74