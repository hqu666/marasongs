package com.hijiyam_koubou.marasongs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
			dbMsg +=",バージョン="+DB_VERSION;	//第4引数; version ;データベースのバージョンを指定します。
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
					"_id integer primary key autoincrement not null, "+
					"ARTIST_ID text," +			//1.MediaStore.Audio.Media.ARTIST_ID
					"SORT_NAME text," +			//2.the抜き大文字
					"ARTIST text," +				//3,MediaStore.Audio.Albums.ARTIST
					"ALBUM_ARTIST text," +			//4,ALBUM_ARTIST
					"ALBUM_ID text," +			//5,artistMap.put(MediaStore.Audio.Media.ALBUM_ID, albumId);
					"GENRE text," +			//6,MediaStore.Audio.Media.GENRE, cGenre);
					"COMPILATION text," +			//7,MediaStore.Audio.Media.COMPILATION, cCompilation);
					"NUMBER_OF_ALBUMS text," +				//8,MNUMBER_OF_ALBUMS
					"NUMBER_OF_TRACKS text" +			//9,NUMBER_OF_TRACKS
//					"ALBUM text, " +				//5,MediaStore.Audio.Albums.ALBUM
//					"ALBUM_ART text, " +			//6,MediaStore.Audio.Albums.ALBUM_ART
//					"SUB_TEXT text" +				//7.アーティストリスト生成用の集約情報:100001足して文字列でもソート可能にする5項目[0/5]number_of_tracks(1 : INTEGER)113[1/5]artist(3 : STRING)Led Zeppelin[2/5]_id(1 : INTEGER)-138840664[3/5]artist_key(3 : STRING)403230045c32484832403a44[4/5]number_of_albums(1 : INTEGER)12 >> 6項目
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
		MyUtil MyUtil = new MyUtil();
		MyUtil.myLog(TAG , dbMsg);
	}

	public static void myErrorLog(String TAG , String dbMsg) {
		MyUtil MyUtil = new MyUtil();
		MyUtil.myErrorLog(TAG , dbMsg);
	}
}

//http://ichitcltk.hustle.ne.jp/gudon/modules/pico_rd/index.php?content_id=74