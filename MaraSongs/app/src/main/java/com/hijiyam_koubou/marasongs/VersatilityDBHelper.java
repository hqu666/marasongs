package com.hijiyam_koubou.marasongs;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
  *  汎用データベースヘルパー
 * **/
public class VersatilityDBHelper extends SQLiteOpenHelper {

	private  String DB_NAME = "test.db";
	private  String colNames = "";

	private int VERSION = 1;


	public VersatilityDBHelper(@Nullable Context context , @Nullable String name , @Nullable SQLiteDatabase.CursorFactory factory , int version , Cursor carsor) {
		super(context , name , factory , version);
		final String TAG = "VersatilityDBHelper";
		String dbMsg = "[VersatilityDBHelper]";
		try {
			dbMsg += "name = " + name;
			DB_NAME = name;
			VERSION = version;

			dbMsg += "carsor = " + carsor.getCount() + "件";
			if(carsor.moveToFirst()){
				int colCount = carsor.getColumnCount();
				dbMsg = "," + colCount + "列";
				colNames =  "id INTEGER PRIMARY KEY AUTOINCREMENT,";
				for (int i = 0; i < colCount; i++) {
					dbMsg += "(" + i + ")";
					String colName = carsor.getColumnName(i);
					dbMsg += colName + ":" + carsor.getString(i) + "\n";
					colNames += colName  + " STRING";
					if(i < (colCount  - 1)){
						colNames += ",";
//					}else{
//						colNames += ")";
					}
				}
			}

			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		final String TAG = "onCreate";
		String dbMsg = "[VersatilityDBHelper]";
		try {
			dbMsg += "colNames = " + colNames;
				db.execSQL(new StringBuilder().append("CREATE TABLE goods (").append(colNames).append(')').toString());
// 				db.execSQL("CREATE TABLE goods ("
//								   + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
//								   + "name STRING,"
//								   + "price INTEGER)");

			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		final String TAG = "onUpgrade";
		String dbMsg = "[VersatilityDBHelper]";
		try {

			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

//	public ArrayList makseFieldArray(Cursor carsor) {
//
//		final String TAG = "makseFieldArray";
//		String dbMsg = "[VersatilityDBHelper]";
//		ArrayList retAyyay = new ArrayList<String>();
//		try {
//
//			myLog(TAG, dbMsg);
//		} catch (Exception e) {
//			myErrorLog(TAG ,  dbMsg + "で" + e);
//		}
//
//	}

	public static void myLog(String TAG , String dbMsg) {
		MyUtil MyUtil = new MyUtil();
		MyUtil.myLog(TAG , dbMsg);
	}

	public static void myErrorLog(String TAG , String dbMsg) {
		MyUtil MyUtil = new MyUtil();
		MyUtil.myErrorLog(TAG , dbMsg);
	}

}