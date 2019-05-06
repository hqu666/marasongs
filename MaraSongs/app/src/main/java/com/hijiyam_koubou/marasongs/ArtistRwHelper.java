package com.hijiyam_koubou.marasongs;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ArtistRwHelper extends SQLiteOpenHelper {
	final static private int DB_VERSION = 1;
	private Context rContext;	//読出し元
	public String dbName;		//第２引数は、データベースファイルの名前です。この引数にnullを指定すると、データベースはメモリー上に作られます。
	public String rDate;				//データURL
	public String creditArtistName;		//クレジットされているアーティスト名
	public String artistName =null;		//リストアップしたアルバムアーティスト名
	public String albumName =null;		//アルバム名
	public String releaceYear = null;			//制作年
	public String trackNo =null;		//trackNo,
	public String titolName =null;		//曲名
	

	public ArtistRwHelper(Context context , String dFn) {					//アーティスト名の置き換えリスト
		super(context, dFn, null, DB_VERSION);
		final String TAG = "ArtistRwHelper[ArtistRwHelper]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			dbMsg="Context="+context;/////////////////////////////////////
			rContext = context;						//第１引数; context ;読出し元;データベースを所有するコンテキストオブジェクトを指定します。
			dbName = dFn;							//第２引数; fileName ;データベースファイルの名前です。この引数にnullを指定すると、データベースはメモリー上に作られます。
			dbMsg += " , db=" + dbName;
			File file = new File(dbName);
			dbMsg += " , exists=" + file.exists();
//			if(! file.exists()){
//			}else{
//				file.delete();
//			}
			dbMsg=dbMsg + ",バージョン="+DB_VERSION;	//第4引数; version ;データベースのバージョンを指定します。
	//		myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {		// table create
		final String TAG = "onCreate[ArtistRwHelper]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			String tName = rContext.getResources().getString(R.string.artist_reW_table);
			dbMsg="テーブル名= "+ tName;/////////////////////////////////////
			String tSet = "create table " + tName +" (" +			//テーブル名；artist_rw_table
					"_id  integer primary key autoincrement not null, "+ 
					"	rDate text not null," +			//データURL
					"	creditArtistName text ," +		//クレジットされているアーティスト名
					"	artistName text ," +			//リストアップしたアルバムアーティスト名
					"	albumName text ," +				//アルバム名
					"	releaceYear text ," +			//制作年
					"	trackNo text ," +				//trackNo,
					"	titolName text" +				//曲名
					");";      
			dbMsg="tSet= "+tSet;/////////////////////////////////////
			db.execSQL(tSet);
			db.execSQL("insert into " + tName +"(rDate , creditArtistName , artistName , albumName , releaceYear) "
						+ "values ('aa', 'bb', 'cc', 'dd', '19730200');");
			int syoukyoGyou = db.delete(tName,"_id = '" + 1 +"'", null);		//			"_id = ?", new String[]{ idStr }
			dbMsg= dbMsg + " , 消去したのは" +  syoukyoGyou + " 行目";/////////////////////////////////////////////////////////////////////////////////////
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		final String TAG = "onUpgrade[ArtistRwHelper]";
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