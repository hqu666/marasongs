package com.hijiyam_koubou.marasongs;
//Androidアプリでマルチメディアを扱うための基礎知識	http://www.atmarkit.co.jp/ait/articles/1203/28/news128.html
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class Item implements Comparable<Object> {	// 外部ストレージ上の音楽をあらわすクラス。
	//プリファレンス
	public static SharedPreferences sharedPref;
	public Editor myEditor ;
	private static List<Item> items;
	private static final String TAG = "Item";
	final int listid;						//作成したリストの連番
	final int _id;						//MediaStore.Audio.Media._ID
	final String artist;				//クレジットアーティスト名
	final String album_artist;			//アルバムアーティスト
	final String album;					//アルバム
	final int track;					//トラックNo,
	final String title;					//タイトル
	final long duration;				//再生時間
	final String data;
	public SimpleDateFormat stf = new SimpleDateFormat("mm:ss SSS");		//02;03 414=123414

	public Item(int listid, int _id, String artist, String album_artist, String album, int track, String title, long duration, String data) {			//int lid,
		this.listid = listid;
		this._id = _id;
		this.artist = artist;
		this.album_artist = album_artist;
		this.album = album;
		this.track = track;
		this.title = title;
		this.duration = duration;
		this.data = data;
	}

	public static Uri getURI(Context context , int id) {		//playNextSong[MusicPlayerService]でsetDataSource
		final String TAG = "getURI[Item]";
		String dbMsg= "開始;";/////////////////////////////////////
		Uri retUri = null;
		try{
			Cursor cursor;
	//		id++;						//20140629暫定
			dbMsg= "id= " + id;/////////////////////////////////////
			retUri = Uri.parse(items.get(id).data);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		dbMsg += ">>" + retUri;
	//	myLog(TAG,dbMsg );
		return retUri;
	//	return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
	}

	public Uri getUriAAT(Context context , String albumArtist , String albumMei , String titol) {		//アルバムアーティスト、アルバム、タイトルからUriを戻す
		final String TAG = "getUriAAT[Item]";
		String dbMsg= "開始;";/////////////////////////////////////
		Uri retUri = null;
		try{
			dbMsg= "albumArtist= " + albumArtist + "albumMei= " + albumMei + "titol= " + titol;/////////////////////////////////////
			String fn = context.getResources().getString(R.string.zenkyoku_file);			//全曲リスト
			File dbF = context.getDatabasePath(fn);			//☆初回時は未だDBが作られていないのでgetApplicationContext()
			fn = dbF.getPath();
	//		dbMsg = "db=" + fn ;
			ZenkyokuHelper zenkyokuHelper = new ZenkyokuHelper(context , fn);		//全曲リストの定義ファイル		.
			SQLiteDatabase Zenkyoku_db = zenkyokuHelper.getReadableDatabase();						//全曲リストファイルを読み書きモードで開く
	//		dbMsg += ">>" + zenkyokuHelper.toString();		//03-28java.lang.IllegalArgumentException:  contains a path separator
			String zenkyokuTName = context.getResources().getString(R.string.zenkyoku_table);			//全曲のテーブル名
	//		dbMsg += "；アーティストリストテーブル=" + zenkyokuTName;
			String[] c_columns =null;					//②引数tableには、テーブル名を指定します。
			String c_selection = "ALBUM_ARTIST = ? AND ALBUM = ? AND TRACK = ? ";			//MediaStore.Audio.Media.ARTIST +" LIKE ? AND " + MediaStore.Audio.Media.ALBUM +" = ?";
			String[] c_selectionArgs= { albumArtist , albumMei , titol };   			//⑥引数groupByには、groupBy句を指定します。
			String c_orderBy= null; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC		MediaStore.Audio.Media.TRACK
			Cursor cursor = Zenkyoku_db.query(zenkyokuTName, c_columns, c_selection, c_selectionArgs , null, null, c_orderBy);	// table, columns,new String[] {MotoN, albamN}
			dbMsg += "；" + cursor.getCount() + "件";
			if(cursor.moveToFirst()){
				retUri = Uri.parse(cursor.getString(cursor.getColumnIndex( "DATA" )));			//MediaStore.Audio.Media.TRACK
			}
			cursor.close();
			Zenkyoku_db.close();
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		dbMsg += ">>" + retUri;
	//	myLog(TAG,dbMsg );
		return retUri;
	//	return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
	}

	public static List<Item> itemsClear( ) {
		final String TAG = "itemsClear[Item]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			long start = System.currentTimeMillis();		// 開始時刻の取得
			if( items  != null){
				dbMsg +=","+items.size();
				items.clear();
				dbMsg +=">>"+items.size();				/// +getResources().getString(R.string.comon_ken);
				long end=System.currentTimeMillis();		// 終了時刻の取得
//				dbMsg +="["+context.getResources().getString(R.string.comon_syoyoujikan)+";"+ (int)((end - start)) + "mS]";		//	<string name="">所要時間</string>
			}
	//		myLog(TAG,dbMsg );
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return items;
	}

	public static List<Item> dbRecordAddItems(Cursor cursor , int nowList_id ,int idCount , List<Item> items) {
		final String TAG = "dbRecordAddItems";
		String dbMsg = "[Item];";
		try{
			int cPosition = cursor.getPosition();
			dbMsg += "," + cPosition + "/" + cursor.getCount() + "曲目,idCount=" + idCount;
			Util UTIL = new Util();
//			UTIL.dBaceColumnCheck( cursor ,  cPosition);


			String ArtistName = cursor.getString(cursor.getColumnIndex("ARTIST"));
			dbMsg += ",ARTIST=" + ArtistName;
			String albumArtist = cursor.getString(cursor.getColumnIndex("ALBUM_ARTIST"));
			dbMsg += " : " + albumArtist;
			String albumName = cursor.getString(cursor.getColumnIndex("ALBUM"));
			dbMsg += ",ALBUM=" + albumName;
			String trackVar = cursor.getString(cursor.getColumnIndex("TRACK"));
			dbMsg += ",TRACK=" + trackVar;
			trackVar = UTIL.checKTrack(trackVar);
//			if (trackVar.contains("/")){
//				String[] tStrs = trackVar.split("/");
//				trackVar = tStrs[0];
//			}
			String titleNeme = cursor.getString(cursor.getColumnIndex("TITLE"));
			dbMsg += ",TITLE=" + titleNeme;
			String duration = cursor.getString(cursor.getColumnIndex("DURATION"));
			dbMsg += ",DURATION=" + duration;
			String moh = UTIL.stf.format(new Date(Long.valueOf(duration)));
			dbMsg += "=" + moh;
				//I'll Be Alright
			String dataUrl = cursor.getString(cursor.getColumnIndex("DATA"));
//			dbMsg += ",DATA=" + dataUrl;

			items.add(new Item(
					nowList_id,
					idCount,				//id	Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")))
					ArtistName,						//cursor.getString(cursor.getColumnIndex("ARTIST")),								//artist
					albumArtist,				//cursor.getString(cursor.getColumnIndex("ALBUM_ARTIST")),					//album_artist
					albumName,					//cursor.getString(cursor.getColumnIndex("ALBUM")),								//album
					Integer.parseInt(trackVar),		//track
					titleNeme,				//cursor.getString(cursor.getColumnIndex("TITLE")),								//title
					Long.valueOf(duration),					//Long.valueOf(cursor.getString(cursor.getColumnIndex("DURATION"))),		//duration
					dataUrl								//cursor.getString(cursor.getColumnIndex("DATA"))								//data
			));
				//ALBUM_ARTIST text, MODIFIED text, COMPOSER text, BOOKMARK text " +				//idbOOKMARK = cur.getColumnIndex(MediaStore.Audio.Media.BOOKMARK);			//APIL8
//								dbMsg2 += items.get(items.size()-1)._id +"]" + items.get(items.size()-1).artist +"(" + items.get(items.size()-1).album_artist +")"+
//											   items.get(items.size()-1).album +"[" + items.get(items.size()-1).track +"]"+items.get(items.size()-1).title+" >> "+items.get(items.size()-1).data;/////////////////////////////////////
//								dbMsg2 += ",YEAR=" + cursor.getString(cursor.getColumnIndex("YEAR"));
//								dbMsg2 += ",LAST_YEAR=" + cursor.getString(cursor.getColumnIndex("LAST_YEAR"));

//			}
			dbMsg += ",DATA=" + items.get(0).data;
			if(!ArtistName.equals(albumArtist) && !albumArtist.equals("コンピレーション")){
//				myLog(TAG,dbMsg );
			}
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
			Util UTIL = new Util();
			UTIL.dBaceColumnCheck(  cursor ,  idCount);
		}
		return items;
	}

	public SQLiteDatabase retDB(Context context) {			//全曲dbを返す
		final String TAG = "retDB";
		String dbMsg = "[Item];";
		SQLiteDatabase Zenkyoku_db = null;
		try{
			String fn = context.getString(R.string.zenkyoku_file);			//全曲リスト名
			dbMsg = "fn=" + fn;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
			ZenkyokuHelper zenkyokuHelper = new ZenkyokuHelper(context , fn);		//全曲リストの定義ファイル		.
			File dbF = context.getDatabasePath(fn);			//Environment.getExternalStorageDirectory().getPath();		new File(fn);		//cContext.
			dbMsg += ",dbF=" + dbF;
			dbMsg += " , exists=" + dbF.exists() +" , canWrite=" + dbF.canWrite();
			if(dbF.exists() ){
				if(Zenkyoku_db != null){
					dbMsg += ">isOpen>" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
					if(! Zenkyoku_db.isOpen()){
						if(dbF.exists()){
							dbMsg += ">>delF=" + dbF.getPath();
							Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
							dbMsg += ">isOpen>" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
						}
					}
				} else {
					if(dbF.exists()){
						Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
						dbMsg += ">isOpen>" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
					}
				}
			}														//if(dbF.exists() ){
//			dbMsg = "検索するのは" + dataFN;
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return Zenkyoku_db;
	}

	/**
	 * *ストレージ上から音楽を探してリストを返す。
	 *  @param context コンテキスト
	 *  @return 見つかった音楽のリスト
	 *  プレイリストからデータを読み込む時に再生順に齟齬が有れば修正する
	 *  */
	public static List<Item> getItems(Context context) {
		final String TAG = "getItems";
		String dbMsg = "[Item];";
		String dbMsg2 = "";
		try{
			long start = System.currentTimeMillis();		// 開始時刻の取得
			Cursor cursor = null;
			int nowList_id = -1;
			sharedPref = context.getSharedPreferences( context.getResources().getString(R.string.pref_main_file) , Context.MODE_PRIVATE);		//	getSharedPreferences(prefFname,MODE_PRIVATE);
			Map<String, ?> keys = sharedPref.getAll();
			dbMsg += ",keys=" + keys.size() + "件" ;				//in16.pla=29236

			boolean kAri = sharedPref.contains("nowList_id");
			if(kAri){
				String rVal = sharedPref.getString("nowList_id", "-1");
//				nowList_id =sharedPref.getInt("nowList_id", -1);		//☆getIntでは java.lang.String cannot be cast to java.lang.Integer
				nowList_id = Integer.valueOf(rVal);			//☆getIntでは java.lang.String cannot be cast to java.lang.Integer
			} else{
				dbMsg += ",nowList_id有り=" + kAri  ;
			}
			dbMsg += ",prefのプレイリスト[" + nowList_id  + "]";				//in16.pla=29236

			String nowList = String.valueOf(keys.get("nowList"));                  //20190506;[-1でjava.lang.NullPointerException:
			dbMsg += "、nowList=" + nowList  ;
			if(nowList == null || nowList.equals("null")){
				nowList_id = -1;
				dbMsg += ">>" + nowList_id  ;
				nowList = context.getResources().getString(R.string.listmei_zemkyoku);					//全曲リストでなければ
				dbMsg +=  ">>全曲リストに補正" ;
			}
//			else {
				if (items != null) {
					dbMsg += ",items=" + items.size() + "件";
					if (0 < items.size()) {                                        //既に読み込んでいたら
						int nlID = items.get(0).listid;
						dbMsg += "、保持しているのは[" + nlID + "]";
						if (nlID != nowList_id ||
								nowList.equals(context.getResources().getString(R.string.playlist_namae_randam)) ||            //ランダム再生
								nowList.equals(context.getResources().getString(R.string.playlist_namae_repeat))            //リピート再生
						) {
							itemsClear();
//					}else if( nlID != nowList_id ){								//リストが変わっていたら
//						itemsClear( );
//					}else{
//						itemsClear( );
						}
					}
					dbMsg += ",items=" + items.size() + "件に";
				} else {
					items = new LinkedList<Item>();
				}
				//		myLog(TAG,dbMsg );
				if (items.size() == 0) {
					if (nowList.equals(context.getResources().getString(R.string.listmei_zemkyoku))) {
						sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.pref_main_file), android.content.Context.MODE_PRIVATE);        //	getSharedPreferences(prefFname,MODE_PRIVATE);
						dbMsg += ",全曲リストを読み込み";

						String fn = context.getResources().getString(R.string.zenkyoku_file);            				//全曲リスト
						File databasePath = context.getDatabasePath(fn);
						dbMsg += ",databasePath=" + databasePath;		//		/data/user/0/com.hijiyam_koubou.marasongs/databases/zenkyoku.db


//						File f = validateFilePath(fn, false);
						ZenkyokuHelper zenkyokuHelper = new ZenkyokuHelper(context, fn);        //全曲リストの定義ファイル		.
						dbMsg += ">>" + zenkyokuHelper.toString();        //03-28java.lang.IllegalArgumentException:  contains a path separator
//						File dbF = context.getDatabasePath(zenkyokuHelper.getDatabaseName());
//						String saveDir = context.getExternalFilesDir(null).toString();						//メモリカードに保存フォルダ
//						dbMsg += "、saveDir="+ saveDir;
////						String[] rDir = saveDir.split(File.separator);
////						dbMsg += "、rDir="+ rDir;
//						File dbF = new File(saveDir + "/databases/" + fn);
////						File dbF = new File("/data/data/com.hijiyam_koubou.marasongs/databases/" + fn);
						File dbF = context.getDatabasePath(fn);				//dbF=/data/user/0/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
						//☆初回時は未だDBが作られていないのでgetApplicationContext()
						dbMsg += ",dbF=" + dbF;
						dbMsg += " , exists=" + dbF.exists() +" , canWrite=" + dbF.canWrite();
//						fn = dbF.getPath();
//						dbMsg += "db= " + fn;
//						File chFile = new File(dbF);
//						dbMsg += " ,exists=" + chFile.exists();
						if (dbF.exists()) {
//						if (chFile.exists()) {
							SQLiteDatabase Zenkyoku_db = zenkyokuHelper.getReadableDatabase();                        //全曲リストファイルを読み書きモードで開く
							dbMsg += ",Zenkyoku_db:PageSiz=" + Zenkyoku_db.getPageSize();
							String zenkyokuTName = context.getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
							dbMsg += "；全曲リストテーブル名=" + zenkyokuTName;
							String c_selection = null;
							String[] c_selectionArgs= null;
							String c_orderBy= null;
							cursor = Zenkyoku_db.query(zenkyokuTName, null, c_selection, c_selectionArgs , null, null, c_orderBy);	//( table, columns, selection, selectionArgs, groupBy, having, orderBy)

//							String c_orderBy = null;            //⑧引数orderByには、orderBy句を指定します。	降順はDESC		MediaStore.Audio.Media.TRACK
//							cursor = Zenkyoku_db.query(zenkyokuTName, null, null, null, null, null, c_orderBy);    //リString table, String[] columns,new String[] {MotoN, albamN}
							dbMsg += "；" + cursor.getCount() + "件";
							int idCount = 0;
							if (cursor.moveToFirst()) {
								do {
									items = dbRecordAddItems(cursor, nowList_id, idCount, items);
									idCount++;
								} while (cursor.moveToNext());
							}
							Zenkyoku_db.close();
							dbMsg +=","+ items.get(0) + "〜"+ items.get(items.size() -1).data;
						} else {
							dbMsg += ",全曲リスト未作成";
						}
					} else if (nowList.equals(context.getResources().getString(R.string.playlist_namae_saikintuika))) {
						String fn = context.getResources().getString(R.string.playlist_saikintuika_filename);
						dbMsg += "、ファイル=" + fn;
						File chFile = new File(fn);
						dbMsg += ",exists=" + chFile.exists();
						if (chFile.exists()) {
							ZenkyokuHelper newSongHelper = new ZenkyokuHelper(context, fn);        //周防会作成時はここでonCreatが走る
							dbMsg += ">>" + newSongHelper.toString();        //03-28java.lang.IllegalArgumentException:  contains a path separator
							SQLiteDatabase newSongDb = newSongHelper.getReadableDatabase();
							String saikintuikaTablename = fn.replace(".db", "");    //最近追加のリストテーブル名
							dbMsg += "、テーブル=" + saikintuikaTablename;
							String c_orderBy = null;            //⑧引数orderByには、orderBy句を指定します。	降順はDESC		MediaStore.Audio.Media.TRACK
							cursor = newSongDb.query(saikintuikaTablename, null, null, null, null, null, c_orderBy);    //リString table, String[] columns,new String[] {MotoN, albamN}
							dbMsg += "；" + cursor.getCount() + "件";
							int idCount = 0;
							if (cursor.moveToFirst()) {
								do {
									dbMsg2 = cursor.getPosition() + "/" + cursor.getCount() + "曲目";
									items = dbRecordAddItems(cursor, nowList_id, idCount, items);
									idCount++;
								} while (cursor.moveToNext());
							}
							newSongDb.close();
						} else {
							dbMsg += ",最近追加リスト未作成";
						}
					} else {
						String ieKubunn = "internal";
						String extV = Environment.getExternalStorageDirectory().toString();
						String nowList_data = String.valueOf(sharedPref.getString("nowList_data", ""));
						dbMsg += ">保存場所=" + nowList_data;
						if (nowList_data.contains(extV)) {
							ieKubunn = "external";
						}
						dbMsg += ",内外区分＝" + ieKubunn;
						Uri uri = MediaStore.Audio.Playlists.Members.getContentUri(ieKubunn, nowList_id);
						String[] columns = null;            //{ idKey, nameKey };
						String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
						cursor = context.getContentResolver().query(uri, columns, null, null, c_orderBy);
						dbMsg += "," + cursor.getCount() + "件";
						if (cursor.moveToFirst()) {
							do {
								int play_order = cursor.getPosition();
								dbMsg2 = play_order + "/" + cursor.getCount() + "曲目";
								int rIndex = cursor.getColumnIndex("play_order");
								if (-1 < rIndex) {                //読み出すカラムが有れば
									play_order = Integer.parseInt(cursor.getString(cursor.getColumnIndex("play_order")));
								}
								dbMsg2 += ",play_order=" + play_order;
								if (play_order != cursor.getPosition()) {
									play_order = cursor.getPosition();
									int sousaID = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members._ID));                //_idか？
									dbMsg2 += ",id=" + sousaID + ",PLAY_ORDER= " + play_order;
									ContentValues contentvalues = new ContentValues();
									Uri playlist_uri = MediaStore.Audio.Playlists.Members.getContentUri("external", nowList_id);
									contentvalues.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, play_order);
									String where = MediaStore.Audio.Playlists.Members._ID + " = ?";
									String[] selectionArgs = {String.valueOf(sousaID)};
									int result = context.getContentResolver().update(playlist_uri, contentvalues, where, selectionArgs);
									dbMsg2 += ",result=" + result;
									//						myLog(TAG,dbMsg );
								}
								items.add(new Item(
										nowList_id,
										play_order,
										cursor.getString(cursor.getColumnIndex("artist")),                                //
										cursor.getString(cursor.getColumnIndex("album_artist")),                    //
										cursor.getString(cursor.getColumnIndex("album")),                                //
										cursor.getInt(cursor.getColumnIndex("track")),        //
										cursor.getString(cursor.getColumnIndex("title")),                                //
										Long.valueOf(cursor.getString(cursor.getColumnIndex("duration"))),        //
										cursor.getString(cursor.getColumnIndex("_data"))                                //data
								));
								//ALBUM_ARTIST text, MODIFIED text, COMPOSER text, BOOKMARK text " +				//idbOOKMARK = cur.getColumnIndex(MediaStore.Audio.Media.BOOKMARK);			//APIL8
								dbMsg2 += "[" + items.get(items.size() - 1)._id + "]" + items.get(items.size() - 1).artist + "(" + items.get(items.size() - 1).album_artist + ")" +
										items.get(items.size() - 1).album + "[" + items.get(items.size() - 1).track + "]" + items.get(items.size() - 1).title + " >> " + items.get(items.size() - 1).data;/////////////////////////////////////
								//				myLog(TAG,dbMsg );
							} while (cursor.moveToNext());
						}
					}
					if(cursor != null){
						if (!cursor.isClosed()) {
							cursor.close();
						}
					}
					dbMsg += ",書換え結果";


				}
//			}
			long end=System.currentTimeMillis();		// 終了時刻の取得
			dbMsg +="["+context.getResources().getString(R.string.comon_syoyoujikan)+";"+ (int)((end - start)) + "mS]"+items.size() +context.getResources().getString(R.string.comon_ken);		//	<string name="">所要時間</string>

			myLog(TAG,dbMsg );
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + dbMsg2 + "で"+e.toString());
		}
		return items;
	}

	public static ArrayList<String> artistlist_yomikomi(Context context){				//アーティストリストを読み込む
		ArrayList<String> retList = null;
		final String TAG = "artistlist_yomikomi[MusicPlayerService]";
		String dbMsg="開始";/////////////////////////////////////
		int retInt;
		try{
//			file_saisinn = null;	//最新更新日
			String fn = context.getResources().getString(R.string.artist_file);			//アーティストリスト
			File dbF = context.getDatabasePath(fn);			//☆初回時は未だDBが作られていないのでgetApplicationContext()
			fn = dbF.getPath();
			dbMsg = "db=" + fn ;
			File chFile = new File(fn);
			dbMsg += ",exists=" + chFile.exists();
			if(chFile.exists()){
				ArtistHelper artistHelper = new ArtistHelper( context , fn);		//アーティスト名のリストの定義ファイル		getApplicationContext()
//				dbMsg += ">>" + getApplicationContext().getDatabasePath(fn);		//03-28java.lang.IllegalArgumentException:  contains a path separator
				dbMsg += ">>" + artistHelper.toString();		//03-28java.lang.IllegalArgumentException:  contains a path separator
				SQLiteDatabase artist_db = artistHelper.getWritableDatabase();					//アーティスト名のえリストファイルを読み書きモードで開く
				String artistTName = context.getResources().getString(R.string.artist_table);			//アーティストリストのテーブル名
				dbMsg += "；アーティストリストテーブル=" + artistTName;
				Cursor artistCursor = artist_db.query(artistTName, null, null, null , null, null, null);	//リString table, String[] columns,new String[] {MotoN, albamN}
				retInt = artistCursor.getCount();
				dbMsg += "；" + retInt + "件";
				if( retInt >0 ){
					if(artistCursor.moveToFirst()){
						retList = new ArrayList<String>();
						retList.clear();
						int artintCo = 0;
						int albamCo = 0;
						int titolCo = 0;
						do{
							dbMsg = artistCursor.getPosition() + "/" + retInt + "件";
							retList.add(artistCursor.getString(artistCursor.getColumnIndex("ARTIST")));	//MediaStore.Audio.Albums.ARTIST
							dbMsg += retList.get(retList.size()-1);
							artintCo++;
						}while(artistCursor.moveToNext());
						dbMsg += ">>artin=" + artintCo +"/albam=" + albamCo + "/titol=" + titolCo;
			//			ruikei_artist = String.valueOf(artintCo);	//artistTotalPTF.setText(String.valueOf(artintCo));		//アルバムアーティス合計
					}else{
						dbMsg += ">>読み直し";
					}
				}
				artistCursor.close();
				artist_db.close();
			}
			dbMsg = "artistList=" + retList.size() + "件";
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return retList;
	}

	/*
	 * インデックスをデータURLで逆検索
	 * */
	public static int getMPItem(String data) {			//    , List<Item> muItems , Context context0213,第二引数の String albumArtist を削除
		int retInt = -1;
		final String TAG = "getMPItem[Item]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			dbMsg=  "data=" + data ;/////////////////////////////////////
			dbMsg +="を" + items.size() +"件から検索";/////////////////////////////////////
			if(data != null){
				for( int i = 0 ;i< items.size() ; i++){
					//	イテレータ	for( Item i : items){はjava.util.ConcurrentModificationException発生
					String rStr = items.get(i).data;
					if(rStr.equals(data)){
						retInt = items.get(i)._id;
						dbMsg +="[" + retInt + "]に" + rStr + "有り";
						break;
					}
				}
			}
			dbMsg +=",retInt=" + retInt ;/////////////////////////////////////
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return retInt;
	}

	@Override
	public int compareTo(Object another) {
		final String TAG = "compareTo[Item]";
		String dbMsg= "開始;";/////////////////////////////////////
		Item item = null;
		try{
			dbMsg= "another=" + another;/////////////////////////////////////
			if (another == null) {
				return 1;
			}
			item = (Item) another;
			int result = album.compareTo(item.album);
			if (result != 0) {
				return result;
			}
	//		myLog(TAG,dbMsg );
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return track - item.track;
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
