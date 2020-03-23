package com.hijiyam_koubou.marasongs;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MusicPlaylist {

    public Context cContext ;
//    public plogTask pTask;

    public String tuikaSakiListName;		//操作対象リスト名
    public int tuikaSakiListID;		//操作対象リスト名
    public Uri tuikaSakilistUri;		//編集中リストUri
    public Cursor playLists;


    public int reqCode = -1;
    static final int CONTEXT_del_playlist = 100;			//このリストを削除


    /***
     * Androidのlaylistを使用する
     * ****/
    public MusicPlaylist(Context context) {					//アーティスト名の置き換えリスト
        final String TAG = "MusicPlaylist";
        String dbMsg= "[MusicPlaylist]";
        try{
            cContext = context;						//第１引数; context ;読出し元;データベースを所有するコンテキストオブジェクトを指定します。
//            myLog(TAG,dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG,dbMsg + "で"+e.toString());
        }
    }

    public boolean isGalaxy(){
        boolean retrbool =false;
        final String TAG = "isGalaxy";
        String dbMsg = "[MusicPlaylist]";
        try{
            //Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;			//206SH; uri=content://media/external/audio/playlists
            //String extV = Environment.getExternalStorageDirectory().toString() ;	//206SH;/storage/sdcard0
            //	String getDataDirectory = Environment.getDataDirectory().toString() ;	//206SH;getDataDirectory=/data,
            //	String getRootDirectory = Environment.getRootDirectory().toString() ;	//206SH;getRootDirectory=/system
            //		String DIRECTORY_MUSIC = Environment.DIRECTORY_MUSIC.toString() ;		//206SH;DIRECTORY_MUSIC=Music
//			boolean isExternalStorageRemovable = Environment.isExternalStorageRemovable(Environment.getExternalStorageDirectory()) ;
//			dbMsg +=  "isExternalStorageRemovable=" +  isExternalStorageRemovable ;/////////////////////////////////////

            Uri playlist_uri = Uri.parse("content://media/external/audio/music_playlists");			//URI
            dbMsg += ",retrbool=" + retrbool;
//			myLog(TAG, dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return retrbool;
    }

    /**
     * プレイリストを削除する */
    public void removePlaylists(int[] ids){				//プレイリストを削除する
        final String TAG = "removePlaylists";
        String dbMsg = "[MusicPlaylist]";
        try{
            dbMsg +=  "ids＝ " + ids;
            Uri uri = null;
            if(isGalaxy()){
                uri = Uri.parse("content://media/external/audio/music_playlists");
            }else{
                uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
            }
            dbMsg += ",uri= " + uri.toString();
            removeItems(uri, ids);				//登録されているアイテムを削除する

            myLog(TAG, dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
    }

    /**
     *  登録されているアイテムを削除する */
    protected void removeItems(Uri uri, int[] ids){				//登録されているアイテムを削除する
        final String TAG = "removeItems";
        String dbMsg = "[MusicPlaylist]";
        try{
            int renint = 0;
            dbMsg +=  "ids＝ " + ids;
            dbMsg += ",uri= " + uri.toString();
            ContentResolver contentResolver = cContext.getContentResolver();

            if(uri == null || ids == null){
            }else{
                String where = "_id IN(";
                for(int i=0; i<ids.length; i++){
                    where += Integer.valueOf(ids[i]);
                    if(i < (ids.length -1)){
                        where += ", ";
                    }
                }
                where += ")";
                renint = cContext.getContentResolver().delete(uri, where, null);				//削除
            }
            dbMsg += ",renint= " + renint;
            myLog(TAG, dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
    }


    /**
     *  指定したプレイリストを全削除
     *  全消去すればmedia/external/audio/playlists/リストIDが変わる/members/
     *  */
    public int deletPlayList(String listName){			//指定したプレイリストを削除する
        int retInt =0;
        final String TAG = "deletPlayList";
        String dbMsg = "[MusicPlaylist]";
        try{
            MusicPlaylist.this.tuikaSakiListName = listName;		//操作対象リスト名
            dbMsg +=  "listName=" + listName ;/////////////////////////////////////
            Uri playlist_uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;			//URI
            String[] columns = null;				//{ idKey, nameKey };
            String c_selection =   MediaStore.Audio.Playlists.NAME +" = ? ";
            String[] c_selectionArgs= { String.valueOf(listName) };   			//⑥引数groupByには、groupBy句を指定します。
            String c_orderBy = null;				//MediaStore.Audio.Playlists.Members.PLAY_ORDER;
            Cursor playLists = cContext.getContentResolver().query(playlist_uri, columns, c_selection, c_selectionArgs, c_orderBy);
            if(playLists.moveToFirst()){
                tuikaSakiListID =  playLists.getInt(playLists.getColumnIndex( MediaStore.Audio.Playlists._ID));
                dbMsg +="[" + tuikaSakiListID +"]" ;/////////////////////////////////////
                String dMessage = "[" + tuikaSakiListID +"]"+listName;
                dbMsg +=  "tuikaSakiListID=" + MusicPlaylist.this.tuikaSakiListID;
                Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", MusicPlaylist.this.tuikaSakiListID);
                columns = null;			//{ idKey, nameKey };
                c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
                playLists = cContext.getContentResolver().query(uri, columns, null, null, c_orderBy );
                dbMsg += ",該当"+playLists.getCount() +"件";
                if( playLists.moveToFirst() ){
                    MusicPlaylist.this.tuikaSakilistUri = null;
                    if(isGalaxy()){
                        MusicPlaylist.this.tuikaSakilistUri = Uri.parse("content://media/external/audio/music_playlists/" + tuikaSakiListID + "/members");
                    }else{
                        MusicPlaylist.this.tuikaSakilistUri = MediaStore.Audio.Playlists.Members.getContentUri("external", tuikaSakiListID);
                    }
                    dbMsg += ",url=" + tuikaSakilistUri;
                    int renint = cContext.getContentResolver().delete(MusicPlaylist.this.tuikaSakilistUri, null, null);
                    dbMsg += ">renint>"+ renint;
                }
                playLists = cContext.getContentResolver().query(uri, columns, null, null, c_orderBy );
                dbMsg += ">>"+playLists.getCount() +"件";
            }
            playLists.close();
            myLog(TAG, dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return retInt;
    }


    /**
     * 指定したプレイリストの内容削除(プログレス処理への引き継ぎ) */
    public void deletPlayListLoop(int reqCode , int listID){			//指定したプレイリストを削除する
        int retInt =0;
        final String TAG = "deletPlayListLoop";
        String dbMsg = "[MusicPlaylist]";
        MusicPlaylist.this.tuikaSakiListID = listID;
        dbMsg += ",reqCode="+reqCode;
        try{
            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", MusicPlaylist.this.tuikaSakiListID);
            String[] columns = null;			//{ idKey, nameKey };
            String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
            playLists = cContext.getContentResolver().query(uri, columns, null, null, c_orderBy );
            dbMsg += ",該当"+playLists.getCount() +"件";
            if( playLists.moveToFirst() ){
                MusicPlaylist.this.tuikaSakilistUri = null;
                if(isGalaxy()){
                    MusicPlaylist.this.tuikaSakilistUri = Uri.parse("content://media/external/audio/music_playlists/" + listID + "/members");
                }else{
                    MusicPlaylist.this.tuikaSakilistUri = MediaStore.Audio.Playlists.Members.getContentUri("external", listID);
                }
                dbMsg += ",url=" + tuikaSakilistUri;
                int renint = cContext.getContentResolver().delete(MusicPlaylist.this.tuikaSakilistUri, null, null);
                //where =null で全削除
////                MusicPlaylist.this.plAL = new ArrayList<Map<String, Object>>();
////                String pdTitol = cContext.getResources().getString(R.string.pref_playlist) +"" + cContext.getResources().getString(R.string.comon_sakujyo);		//削除
////                String pdMessage = MusicPlaylist.this.tuikaSakiListName + ";" + retInt + cContext.getResources().getString(R.string.comon_ken);						//件
////                retInt = playLists.getCount();
////                pTask = new plogTask(cContext , MusicPlaylist.this , pdTitol ,pdMessage,retInt ).execute(reqCode,  pdMessage , playLists ,pdTitol );		//,jikkouStep,totalStep,calumnInfo
//                do{
//                    dbMsg += "(" + playLists.getPosition() + "/" + playLists.getCount() +")";
//                    int delID = playLists.getInt(playLists.getColumnIndex( MediaStore.Audio.Playlists.Members._ID));
//                    dbMsg +="[" + delID +"]" ;/////////////////////////////////////
//                    dbMsg += MusicPlaylist.this.tuikaSakilistUri ;/////////////////////////////////////
//                    String rStr = playLists.getString(playLists.getColumnIndex( MediaStore.Audio.Playlists.Members.TITLE));/////////////////////////////////////
//                    dbMsg +=";" + rStr;
//
////                    int retint=delOneLineBody( MusicPlaylist.this.tuikaSakilistUri, delID);			//プレイリストから指定された行を削除する MuList.this.sousalistUri, MuList.this.sousaRecordId
////                    dbMsg += ",削除= " + retint + "レコード";
//                    dbMsg += ",削除するレコードID= " + delID;
//                    String where = "_id=" + Integer.valueOf(delID);
//                    int renint = cContext.getContentResolver().delete(MusicPlaylist.this.tuikaSakilistUri, where, null);				//削除		getApplicationContext()
//                    dbMsg += ",削除= " + renint + "レコード";
//                }while(playLists.moveToNext());
            }
            playLists = cContext.getContentResolver().query(uri, columns, null, null, c_orderBy );
            dbMsg += ">>"+playLists.getCount() +"件";

            myLog(TAG, dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
    }

    /**
     *  指定したプレイリストを削除(レコードごとの読出し) */
    public Cursor deletPlayListBody(Cursor cursor){			//指定したプレイリストを削除する
        int retInt =0;
        final String TAG = "deletPlayListBody[MuList]";
        String dbMsg = "[MuList]";
        try{
            dbMsg +=  "削除するプレイリスト=" + MusicPlaylist.this.tuikaSakilistUri ;
            dbMsg += "(" + cursor.getPosition() + "/" + cursor.getCount() +")";
            int delID = cursor.getInt(cursor.getColumnIndex( MediaStore.Audio.Playlists.Members._ID));
            dbMsg +="[" + delID +"]" ;/////////////////////////////////////
            dbMsg += MusicPlaylist.this.tuikaSakilistUri ;/////////////////////////////////////
            String rStr = cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Playlists.Members.TITLE));/////////////////////////////////////
            dbMsg +=";" + rStr;/////////////////////////////////////
            int retint=delOneLineBody( MusicPlaylist.this.tuikaSakilistUri, delID);			//プレイリストから指定された行を削除する MuList.this.sousalistUri, MuList.this.sousaRecordId
            dbMsg += ",削除= " + retint + "レコード";
//			myLog(TAG, dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return cursor;
    }

    /**
     *  指定したプレイリストを削除(終了処理) */
    public void deletPlayListEnd(){			//指定したプレイリストを削除する
        int retInt =0;
        final String TAG = "deletPlayListEnd";
        String dbMsg = "[MusicPlaylist]";
        try{
            playLists.close();
//            if(reqCode == CONTEXT_del_playlist){
//                Uri playlist_uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;			//URI
//                String where = MediaStore.Audio.Playlists._ID + " = ?";					//"_id = ?";
//                String[] selectionArgs = { String.valueOf(MusicPlaylist.this.tuikaSakiListID) };
//                retInt = getContentResolver().delete(playlist_uri, where, selectionArgs);
//                dbMsg += ",削除=" + retInt;		//listId=42533,retInt=1
//                myLog(TAG,dbMsg);
//                plNameSL = null;
//                makePlayListSPN(sousalistName);		//プレイリストスピナーを作成する
//                int ePosition = plNameSL.indexOf(MusicPlaylist.this.nowList);
//                dbMsg +=  "ePosition=" + ePosition;
//                pl_sp.setSelection(ePosition , false);								//☆falseで勝手に動作させない
//            }
            myLog(TAG, dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
    }

    /**
     * プレイリストから指定された行を削除する*/
    protected int delOneLineBody(Uri listUri, int delId){				//プレイリストから指定された行を削除する
        int renint =-1;
        final String TAG = "delOneLineBody";
        String dbMsg = "[MuList]";
        try{
            dbMsg +=   "削除レコードのプレイリストUri= " + listUri.toString();
            dbMsg += ",削除するレコードID= " + delId;
            String where = "_id=" + Integer.valueOf(delId);
            renint = cContext.getContentResolver().delete(listUri, where, null);				//削除		getApplicationContext()
            dbMsg += ",削除= " + renint + "レコード";
//			myLog(TAG, dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return renint;
    }

    /**
     *  プレイリストを新規作成する */
    public Uri addPlaylist(String listName, Uri images_uri, String thumb){			//プレイリストを新規作成する
        Uri result_uri = null;
        final String TAG = "addPlaylist";
        String dbMsg = "[MusicPlaylist]";
        try{
            dbMsg +=  "新規リスト名=" + listName;
            ContentValues contentvalues = null;
            ContentResolver contentResolver = cContext.getContentResolver();
            Uri playlist_uri = null;
            int playlist_id = -1;

            contentvalues = new ContentValues();
            contentvalues.put("name", listName);
            if(isGalaxy()){			//Galaxyの場合の必要データ作成
                playlist_uri = Uri.parse("content://media/external/audio/music_playlists");			//URI
                int image_index = -1;			//データ作成
                if(images_uri != null){
                    image_index = (int)ContentUris.parseId(images_uri);
                }
                contentvalues.put("images_id", image_index);
                contentvalues.put("thumbnail_uri", "");
            }else{			//標準的な端末の必要データ作成
                playlist_uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;			//URI
            }
            dbMsg += ",playlist_uri=" + playlist_uri;
            result_uri = contentResolver.insert(playlist_uri, contentvalues);		//追加
            dbMsg += ",result_uri=" + result_uri;	//result_uri=content://media/external/audio/playlists/42529
            if(result_uri == null){			//NG
                dbMsg +=">>失敗 add playlist : " + listName + ", is null";
            }else if((playlist_id = (int)ContentUris.parseId(result_uri)) == -1){			//NG
                dbMsg += ">>失敗 add playlist : " + listName + ", " + result_uri.toString();
            }else{			//OK
                dbMsg += ">>成功 listName＝ " + listName + ",playlist_id=" + playlist_id;
                //add playlist : プレイリスト2015-12-03 14:16:37,42529
            }
            myLog(TAG, dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return result_uri;
    }

    /**
     *  プレイリストの最大のplay_orderを取得する */
    public int getUserListMaxPlayOrder(int playlist_id){				//プレイリストの最大のplay_orderを取得する
        int ret = 0;				//org -1
        final String TAG = "getUserListMaxPlayOrder";
        String dbMsg = "[MusicPlaylist]";
        try{
            final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist_id);
            final String[] columns = null;			//{ idKey, nameKey };
            String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
            Cursor cursor = cContext.getContentResolver().query(uri, columns, null, null, c_orderBy );
            ret = cursor.getCount();
            dbMsg += ","+ret +"件";
            cursor.close();
//			myLog(TAG, dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return ret;
    }

    /**
     *  指定された名称のリストを作成する;既に有ればlistIDを返し、無ければ作成してIdを返す */
    public int getPlaylistId(String listName){				//指定された名称のリストを作成する
        int listID = 0;
        final String TAG = "getPlaylistId";
        String dbMsg = "[MusicPlaylist]";
        try{
            dbMsg+="cContext=" + this.cContext;/////////////////////////////////////
            dbMsg +=  "指定された名称=" + listName;
            Uri result_uri = null;
            Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
            String[] columns = null;			//{ idKey, nameKey };
            String c_selection = MediaStore.Audio.Playlists.NAME +" = ? ";
            String[] c_selectionArg= { String.valueOf(listName) };		//⑥引数groupByには、groupBy句を指定します。
            String c_orderBy = null;			//MediaStore.Audio.Playlists.Members.PLAY_ORDER;
            Cursor cursor = cContext.getContentResolver().query(uri, columns, c_selection, c_selectionArg, c_orderBy );
            dbMsg +=  ",既存Playlists;" + cursor.getCount() + "件";
            if(cursor.moveToFirst()){
                listID = Integer.valueOf(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID)));
            } else{
                dbMsg +=  ">>新規作成" ;
                result_uri = addPlaylist(listName, null, null);		//プレイリストを新規作成する
                dbMsg += ",result_uri=" + result_uri;			//fastItemeFn=/storage/sdcard0/Music/Jimmy Cliff/Follow My Mind/07 Remake The World.wma
                listID = (int)ContentUris.parseId(result_uri);
            }
            dbMsg += ",[" + listID + "]" + result_uri;			//[42529]content://media/external/audio/playlists/42529
            cursor.close();
            myLog(TAG, dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return listID;
    }

    
    /**
     * プレイリストへ曲を追加する	 */
    public Uri addMusicToPlaylist(int playlist_id , int audio_id, String data, int data_hash){		//プレイリストへ曲を追加する
        Uri result_uri = null;
        final String TAG = "addMusicToPlaylist";
        String dbMsg = "[MusicPlaylist]";
        try{
            dbMsg+="cContext=" + this.cContext;/////////////////////////////////////
            dbMsg +=  ",playlist_id=" + playlist_id;
            dbMsg += "[audio_id=" + audio_id;
            dbMsg += "]追加する曲=" + data;
            ContentResolver contentResolver = cContext.getContentResolver();
            ContentValues contentvalues = new ContentValues();
            Uri kakikomiUri = null;

            if(contentResolver == null){
            }else{
                int poSetteiti = getUserListMaxPlayOrder(playlist_id);			//プレイリストの最大のplay_orderを取得する
                dbMsg += "、次は" + poSetteiti + "曲目";
                contentvalues.put(MediaStore.Audio.Playlists.Members._ID, poSetteiti+ 1);
                contentvalues.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, poSetteiti + 1);
                dbMsg += ",audio_id=" + audio_id;
                contentvalues.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, Integer.valueOf(audio_id));

                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ) { //Andrid10以降
                    try {
                        //  https://codechacha.com/ja/android-mediastore-insert-media-files/
                        Uri collection = MediaStore.Audio.Playlists.Members.getContentUri(MediaStore.VOLUME_INTERNAL,playlist_id);
//                        Uri collection = MediaStore.Audio.Playlists.Members.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY,playlist_id);
                        dbMsg += ",書込みuri= " + collection;
                        result_uri = contentResolver.insert(collection, contentvalues);				//追加

//                    contentResolver.openFileDescriptor(result_uri, "w", null){
//                        // write something to OutputStream
//                        FileOutputStream(it!!.fileDescriptor).use { outputStream ->
//                                val imageInputStream = resources.openRawResource(R.raw.my_image)
//                            while (true) {
//                                val data = imageInputStream.read()
//                                if (data == -1) {
//                                    break
//                                }
//                                outputStream.write(data)
//                            }
//                            imageInputStream.close()
//                            outputStream.close()
//                        }
//                    }                        //members/シリアルIDが加算される
//                          contentvalues.clear();
//                          contentResolver.update(result_uri, contentvalues, null, null);
                    } catch (Exception e) {
                        myErrorLog(TAG , e + "");
//                    dbMsg = null;
                    }
                    if(result_uri == null){					//NG
                        dbMsg += "失敗 add music :list= " + playlist_id + ", audio_id=" + audio_id + ", is null";
                    }else if(((int) ContentUris.parseId(result_uri)) == -1){					//NG
                        dbMsg += "失敗 add music : " + playlist_id + ", " + audio_id + ", " + result_uri.toString();
                    }else{					//OK
                        dbMsg +=">>成功list_id=" + playlist_id + ", audio_id=" + audio_id + ",result_uri= " + result_uri.toString();
                    }
                }else{
                    dbMsg += ",書込みuri= " + kakikomiUri;

                    contentvalues.put("play_order", poSetteiti);
                    if(isGalaxy()){
                        dbMsg += "、isGalaxy";
                        kakikomiUri = Uri.parse("content://media/external/audio/music_playlists/" + playlist_id + "/members");
                        contentvalues.put("audio_data", data);
                        dbMsg += ",data_hash=" + data_hash;
                        contentvalues.put("audio_data_hashcode", data_hash);
                    }else{
                        kakikomiUri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist_id);

//                    kakikomiUri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist_id);
                        //これだけだと　java.lang.SecurityException:　発生
//                    dbMsg += ",Path=" + kakikomiUri.getEncodedPath();
//                    File kakikomiFN = new File(kakikomiUri.getEncodedPath());
//                    kakikomiUri = Uri.fromFile(kakikomiFN);
                    }
                    try {
                        //  https://codechacha.com/ja/android-mediastore-insert-media-files/
                        result_uri = contentResolver.insert(kakikomiUri, contentvalues);				//追加
                        dbMsg += ",result_uri=" + result_uri;
                        //members/シリアルIDが加算される
                    } catch (Exception e) {
                        myErrorLog(TAG , e + "");
//                    dbMsg = null;
                    }
                    if(result_uri == null){					//NG
                        dbMsg += "失敗 add music :list= " + playlist_id + ", audio_id=" + audio_id + ", is null";
                    }else if(((int) ContentUris.parseId(result_uri)) == -1){					//NG
                        dbMsg += "失敗 add music : " + playlist_id + ", " + audio_id + ", " + result_uri.toString();
                    }else{					//OK
                        dbMsg +=">>成功list_id=" + playlist_id + ", audio_id=" + audio_id + ",result_uri= " + result_uri.toString();
                    }

                }
            }
            if(dbMsg != null){
                myLog(TAG, dbMsg);
            }
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return result_uri;
    }


////////////////////////////////////////
//    public class plogTask extends AsyncTask<Object, Integer , AsyncTaskResult<Integer>> {		//myResult	元は<Object, Integer, Boolean>
//        private Context cContext = null;
//        private plogTaskCallback callback;
//        OrgUtil ORGUT;					//自作関数集
//        public long start = 0;				// 開始時刻の取得
//        public Boolean isShowProgress;
//        public ProgressDialog progressDialog = null;	// 処理中ダイアログ	ProgressDialog	AlertDialog
//        public int reqCode = 0;						//処理番号
//        public CharSequence pdTitol;			//ProgressDialog のタイトルを設定
//        public CharSequence pdMessage;			//ProgressDialog のメッセージを設定
//        public CharSequence pdMessage_stok;			//ProgressDialog のメッセージを設定
//        public int pdMaxVal = 0;					//ProgressDialog の最大値を設定 (水平の時)
//        public int pdStartVal=0;					//ProgressDialog の初期値を設定 (水平の時)
//        public int pdCoundtVal=0;					//ProgressDialog表示値
//        public int pd2MaxVal;					//ProgressDialog の最大値を設定 (水平の時)
//        public int pd2CoundtVal;					//ProgressDialog表示値
//        public String _numberFormat = "%d/%d";
//        public NumberFormat _percentFormat = NumberFormat.getPercentInstance();
//
//        public Boolean preExecuteFiniSh=false;	//ProgressDialog生成終了
//        public Bundle extras;
//
//        long stepKaisi = System.currentTimeMillis();		//この処理の開始時刻の取得
//        long stepSyuuryou;		//この処理の終了時刻の取得
//
//        public plogTask(Context cContext , plogTaskCallback callback,CharSequence pdTitol ,CharSequence pdMessage ,int pdMaxVal){
//            super();
//            // ,int reqCode , String pdTitol , String pdMessage ,int pdMaxVal ,int pd2CoundtVal,int pd2MaxVal){
//            final String TAG = "plogTask[plogTask]";
//            try{
//                ORGUT = new OrgUtil();				//自作関数集
//                boolean dCancel = false;
//                String dbMsg = "cContext="+cContext;///////////////////////////
//                if( cContext != null ){
//                    this.cContext = cContext;
////                    this.callback = callback;
////                    dbMsg += ",callback="+callback;///////////////////////////
//                    dbMsg += ",Titol=" + pdTitol;
//                    dbMsg += ",Message=" + pdMessage;
//                    this.pdMessage = pdMessage;
//                    dbMsg += ",Max=" + pdMaxVal;
//                    this.pdMaxVal = pdMaxVal;
//                    dbMsg += "reqCode=" + reqCode;
//                    progressDialog = new ProgressDialog(cContext);			//.getApplicationContext()
//                    progressDialog.setTitle(pdTitol);
//                    progressDialog.setMessage(pdMessage);
//                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);			// プログレスダイアログのスタイルを水平スタイルに設定します
//                    progressDialog.setMax(pdMaxVal);			// プログレスダイアログの最大値を設定します
////                    if( MuList.this.reqCode == MENU_TAKAISOU  ){
////                        dbMsg += ",多階層リスト";
////                        dCancel =true;
////                    }
//                    progressDialog.setCancelable(dCancel);			// プログレスダイアログのキャンセルが可能かどうかを設定します
//                    progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                        @Override
//                        public void onCancel(DialogInterface dialog) {
//                            final String TAG = "onCancel[plogTask]";
//                            String dbMsg = "開始";
//                            try {
//                                //							dbMsg = "reqCode=" + MuList.this.reqCode;
//                                //							reqCode = MENU_infoKaisou;								//537;情報付き曲名リスト書き込み中
//                                //							MuList.this.plef_tankaisou = true;									//階層リストを単階層に変更
//                                //							String pdMessage  = getResources().getString(R.string.playlist_namae_saikintuika) + plogTask.this.pdMaxVal + getResources().getString(R.string.comon_kyoku);		//最近追加	曲</string>
//                                //							CreatePLList( MuList.this.nowList_id , pdMessage);
//                                myLog(TAG, dbMsg);
//                            } catch (Exception e) {
//                                myErrorLog(TAG,"でエラー発生；"+e.toString());
//                            }
//                        }
//                    });
//                    progressDialog.show();
//                    dbMsg += ">isShowing>" + progressDialog.isShowing();
//                }
//                //		myLog(TAG, dbMsg);
//            } catch (Exception e) {
//                myErrorLog(TAG,"でエラー発生；"+e.toString());
//            }
//        }
//
//        //		http://greety.sakura.ne.jp/redo/2011/02/asynctask.html
//        /**
//         * 最初にUIスレッドで呼び出されます。 , UIに関わる処理をします。
//         * doInBackgroundメソッドの実行前にメインスレッドで実行されます。
//         * 非同期処理前に何か処理を行いたい時などに使うことができます。 */
//        @Override
//        protected void onPreExecute() {			// onPreExecuteダイアログ表示
//            //int reqCode , String pdTitol , String pdMessage ,int pdMaxVal ,int pd2CoundtVal,int pd2MaxVal
//            super.onPreExecute();
//            final String TAG = "onPreExecute";
//            String dbMsg = "[MusicPlaylist.plogTask]";
//            try {
//                dbMsg += ":reqCode="+reqCode;///////////////////////////
//                dbMsg +=  ",pdTitol="+pdTitol;///////////////////////////
//                dbMsg +=  ",Message="+pdMessage;///////////////////////////
//                dbMsg += ",pdMaxVal="+pdMaxVal;///////////////////////////
//                myLog(TAG, dbMsg);
//            }catch (Exception e) {
//                myErrorLog(TAG ,  dbMsg + "で" + e);
//            }
//        }
//
//        public  Uri cUri = null  ;							//4]
//        public  String where = null;
//        public SQLiteStatement stmt = null ;			//6；SQLiteStatement
//        public SQLiteDatabase tdb;
//        public  ContentValues cv = null ;						//7；SQLiteStatement
//        @SuppressWarnings("resource")
//        /**
//         * doInBackground
//         * ワーカースレッド上で実行されます。 このメソッドに渡されるパラメータの型はAsyncTaskの一つ目のパラメータです。
//         * このメソッドの戻り値は AsyncTaskの三つ目のパラメータです。
//         * メインスレッドとは別のスレッドで実行されます。
//         * 非同期で処理したい内容を記述します。 このメソッドだけは必ず実装する必要があります。
//         *0 ;reqCode, 1; pdMessage , 2;pdMaxVal ,3:cursor , 4;cUri , 5;where , 6;stmt , 7;cv ,  8;omitlist , 9;tList );
//         * */
//        @Override
//        public AsyncTaskResult<Integer> doInBackground(Object... params) {//InParams続けて呼ばれる処理；第一引数が反映されるのはここなのでここからダイアログ更新 バックスレッドで実行する処理;getProgress=0で呼ばれている
//            final String TAG = "doInBackground";
//            String dbMsg = "[MusicPlaylist.plogTask]";
//            try {
//                Cursor cursor = null  ;					//3]
//                List<String> testList = null;	//最近再生された楽曲のID
//                String comp = null;
//                SQLiteDatabase wrDB;
//                pdCoundtVal = 0;
//                this.reqCode = (Integer) params[0] ;			//0.処理
//                dbMsg += "reqCode = " + reqCode;
//
//                CharSequence setStr=(CharSequence) params[1];				//2.次の処理に渡すメッセージ
//                if(setStr !=null ){
//                    if(! setStr.equals(pdMessage)){
//                        pdMessage = setStr;
//                        this.pdMessage = setStr;
//                        dbMsg += ",Message = " + pdMessage;
//                        //				change2ndText () ;			//ProgBar2の表示値設定
//                    }
//                }
//                switch(reqCode) {
////                    case CONTEXT_listup_jyunbi2:	//リストアップ準備;2階層リスト化
////                    case MENU_hihyoujiArtist2:		//非表示になったアーティストの修正処理;2階層化
////                    case MENU_infoKaisou:			//情報付きリスト書き込み中
////                    case listType_2ly:				//アーティストの単階層リストとalbumとtitolの２階層
////                    case MENU_2KAISOU:				//2階層リスト選択選択中
////                    case MENU_TAKAISOU2:				//多階層リスト書き込み中
////                    case CONTEXT_rumdam_arrayi:		//ランダム再生;配列書込み
////                    case CONTEXT_saikintuika_end:	//最近追加リストのプレイリスト作成
////                    case CONTEXT_saikintuika_Wr:				//最近追加リストのファイル作成
////                    case CONTEXT_rumdam_wr:			//ランダム再生リストの書込み
////                        pdMaxVal = (int) params[2];		//プレイリスト用ArrayList
////                        dbMsg += ", pdMaxVal = " + pdMaxVal + "件"  ;
////                        break;
////                    case CONTEXT_M3U2_PL:		//汎用プレイリストをAndroidのプレイリストに転記
////                        break;
//                    default:
//                        cursor = (Cursor) params[2] ;			//2
//                        pdMaxVal = cursor.getCount();
//                        dbMsg += ", cursor = " + pdMaxVal + "件"  ;
//                        break;
//                }
//
////                if(reqCode == CONTEXT_saikin_sisei0  ){	//最近再生された楽曲(重複削除)
////                    testList =  new ArrayList<String>();	//最近再生された楽曲のID
////                }
//
//
//                long vtWidth = 200;		// 更新間隔
//                if(pdMaxVal<200){
//                    vtWidth = 100;
//                    if(pdMaxVal<100){
//                        vtWidth = 20;
//                    }
//                }
//                dbMsg += ", 更新間隔 = " + vtWidth  ;
//                long vTime = System.currentTimeMillis() + vtWidth;		// 更新タイミング
//                pdTitol=(CharSequence) params[3] ;			//3:
//                dbMsg += ", pdTitol = " + pdTitol ;
//                switch(reqCode) {
////                    case CONTEXT_listup_jyunbi2:	//リストアップ準備;2階層リスト化
////                    case MENU_hihyoujiArtist2:		//非表示になったアーティストの修正処理;2階層化
////                    case MENU_infoKaisou:			//情報付きリスト書き込み中
////                    case listType_2ly:				//アーティストの単階層リストとalbumとtitolの２階層
////                    case MENU_2KAISOU:				//2階層リスト選択選択中
////                    case MENU_TAKAISOU2:							//多階層リスト書き込み中
////                    case CONTEXT_saikintuika_end:	//最近追加リストのプレイリスト作成
////                    case CONTEXT_saikintuika_Wr:				//最近追加リストのファイル作成
//////				case CONTEXT_saikintuika_read:
////                    case CONTEXT_rumdam_arrayi:		//ランダム再生;配列書込み
////                    case CONTEXT_rumdam_wr:			//ランダム再生リストの書込み
////                    case CONTEXT_M3U2_PL:		//汎用プレイリストをAndroidのプレイリストに転記
////                        dbMsg += ", pdCoundtVal = " + pdCoundtVal ;
////                        for(pdCoundtVal =0 ; pdCoundtVal < pdMaxVal;pdCoundtVal ++){
////                            switch(reqCode) {
////                                case CONTEXT_listup_jyunbi2:	//リストアップ準備;2階層リスト化
////                                case MENU_hihyoujiArtist2:		//非表示になったアーティストの修正処理;2階層化
////                                    listSyuuseiJyunbi2Body( pdCoundtVal);								//リストアップ準備;2階層リスト化
////                                    break;
////                                case MENU_infoKaisou:			//537;情報付きリスト書き込み中
////                                    plInfoSinglBody(pdCoundtVal);		//情報付き単階層プレイリストの描画
////                                    break;
////                                case listType_2ly:				//2;アーティストの単階層リストとalbumとtitolの２階層
////                                    plOneBody(pdCoundtVal);
////                                    break;
////                                case MENU_2KAISOU:				//534;2階層リスト選択選択中
////                                    plTowBody(pdCoundtVal);		//単階層+2階層プレイリストの2階層作成							break;
////                                    break;
////                                case MENU_TAKAISOU2:			//536;多階層リスト書き込み中
////                                    plWrightBody(pdCoundtVal);	//3階層プレイリストの描画
////                                    break;
////                                case CONTEXT_saikintuika_end:	//654 最近追加リストのプレイリスト作成
////                                    saikin_tuika_yomi_body( pdCoundtVal );				//最近追加された楽曲の抽出(指定IF)
////                                    break;
////                                case CONTEXT_saikintuika_Wr:				//最近追加リストのファイル作成
////                                    playListWr_body(pdCoundtVal );
////                                    break;
////                                case CONTEXT_rumdam_arrayi:		//ランダム再生;配列書込み
////                                    pdCoundtVal = randumPlay_array_body(pdCoundtVal);			//ランダム再生;ランダム追加
////                                    break;
////                                case CONTEXT_rumdam_wr:			//ランダム再生リストの書込み
////                                    randumPlayListWr_body(pdCoundtVal);			//ランダム再生;リストに書き込む
////                                    break;
////                                case CONTEXT_M3U2_PL:		//汎用プレイリストをAndroidのプレイリストに転記
////                                    m3U2PlayList_body( pdCoundtVal );
////                                    break;
////                            }
////                            publishProgress( pdCoundtVal );		//progressDialog.progBar1.setProgress(step1);
////                        }
////                        dbMsg += ">>" + pdCoundtVal ;
////                        break;
//                    default:
//                        if(cursor.moveToFirst()){
//                            dbMsg +=  "；ループ前；"+ cursor.getPosition() +"/ " + pdMaxVal;	/////////////////////////////////////////////////////////////
//                            switch(reqCode) {
////						case MaraSonActivity.v_artist:							//アーティスト
////							comp = getResources().getString(R.string.comon_compilation);			//コンピレーション
////							break;
//                                case MaraSonActivity.v_alubum:							//アルバム
//                                    break;
//                                case MaraSonActivity.v_titol:						//タイトル
//                                    break;
//                                //					default:
//                                //						break;
//                            }
//                            String delID =null;
//                            Uri pUri =null;
//                            int delC = 0;
//                            do{
//                                dbMsg +=  "[cursor="+  cursor.getPosition() +  "/" + pdMaxVal + ";progressDialog=" + progressDialog.getProgress() +"]" ;//cursor.getCount()
//                                switch(reqCode) {
////                                    case CONTEXT_listup_jyunbi:					//リストアップ準備;アーティスト/作曲者名
////                                    case MENU_hihyoujiArtist:					//非表示になったアーティストの修正処理
////                                        cursor =  listSyuuseiJyunbiBody( cursor);								//リスト修正準備；アーティストと作曲者名リスト
////                                        break;
////                                    case MENU_TAKAISOU:				//537;多階層リスト選択選択中
////                                    case listType_2ly2:				//  listType_2ly + 1;albumとtitolの２階層
////                                    case MENU_MUSCK_PLIST:			//プレイリスト選択中
////                                        cursor = CreatePLListBody(cursor);		//プレイリストの内容取得
////                                        break;
//                                    case CONTEXT_del_playlist:					//624;このリストを削除
////                                    case CONTEXT_del_playlis_membert:			//627;リストの中身を削除
////                                    case CONTEXT_rumdam_redrow:					//634 ランダム再生リストの消去
////                                    case CONTEXT_REPET_CLEAN:					//リピート再生リストの既存レコード消去
////                                    case CONTEXT_add_request:					//リクエストリスト
////                                    case CONTEXT_dell_request:					//リクエスト解除
//                                        delID = cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Playlists.Members._ID));
//                                        dbMsg += "[delID=" + delID ;///////////////////////////////////
//                                        pUri = MediaStore.Audio.Playlists.Members.getContentUri("external",Long.valueOf(String.valueOf(MusicPlaylist.this.tuikaSakiListID)) );
//                                        dbMsg += "]pUri=" + pUri.toString() ;///////////////////////////////////
//                                        delC = delOneLineBody(pUri , Integer.valueOf(delID));			//プレイリストから指定された行を削除する
//                                        dbMsg += ">削除>" + delC + "件" ;///////////////////////////////////
//                                        break;
////							case CONTEXT_saikintuika_read:
////								dbMsg += ",最近追加リストをデータベースから読み出す;" ;
////								int playOrder = cursor.getPosition() +1;
////								readSaikinTuikaDB_body(cursor , playOrder);				//最近追加された楽曲fairuyomiro
////								break;
////                                    case MaraSonActivity.v_artist:							//195 アーティスト
////                                        cursor = artistList_yomikomiLoop( cursor );					//, comp
////                                        break;
////                                    case MaraSonActivity.v_alubum:							//アルバム
////                                        break;
////                                    case MaraSonActivity.v_titol:						//タイトル
////                                        break;
////                                    case CONTEXT_saikin_sisei0:		//655;最近再生された楽曲(重複削除)
////                                        dbMsg +=  ",最近追加リスト;CONTEXT_saikin_sisei0" ;
////                                        String tStr = cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Playlists.Members.AUDIO_ID));
////                                        dbMsg += ",AUDIO_ID=" + tStr ;///////////////////////////////////
////                                        dbMsg += "(" + testList.size() +")" ;///////////////////////////////////
////                                        if(ORGUT.isInListString(testList , tStr)){		//渡された文字が既にリストに登録されていればtrueを返す
////                                            delID = cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Playlists.Members._ID));
////                                            pUri = MediaStore.Audio.Playlists.Members.getContentUri("external",Long.valueOf(String.valueOf(MuList.this.tuikaSakiListID)) );
////                                            dbMsg += ",pUri=" + pUri.toString() ;///////////////////////////////////
////                                            delC = delOneLineBody(pUri , Integer.valueOf(delID));			//プレイリストから指定された行を削除する
////                                            dbMsg += ">削除>" + delC + "件" ;///////////////////////////////////
////                                        }else{
////                                            testList.add(tStr);
////                                            dbMsg += "→" + testList.size() +")" ;///////////////////////////////////
////                                        }
////                                        //				myLog(TAG,dbMsg);
////                                        break;
////                                    case CONTEXT_saikin_sisei:			//656;最近再生リストの準備
////                                        dbMsg +=  ",設定=" + MuList.this.pref_saikin_sisei ;///////////////////////////////////
////                                        dbMsg +=  ",登録=" + pdMaxVal ;//////////////////////////////cursor.getCount()/////
////                                        int amari = cursor.getCount()-Integer.valueOf(MuList.this.pref_saikin_sisei) - cursor.getPosition();
////                                        dbMsg +=  ",amari=" + amari ;///////////////////////////////////
////                                        if( 0 < amari){
////                                            delID = cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Playlists.Members._ID));
////                                            pUri = MediaStore.Audio.Playlists.Members.getContentUri("external",Long.valueOf(String.valueOf(MuList.this.tuikaSakiListID)) );
////                                            dbMsg += ",pUri=" + pUri.toString() ;///////////////////////////////////
////                                            delC = delOneLineBody(pUri , Integer.valueOf(delID));			//プレイリストから指定された行を削除する
////                                            dbMsg += ">削除>" + delC + "件" ;///////////////////////////////////
////                                        }else{
////                                            cursor.moveToLast();
////                                        }
////                                        break;
////                                    case CONTEXT_rumdam_saiseizumi:		//ランダム再生準備
////                                        String AUDIO_ID = cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Playlists.Members.AUDIO_ID));
////                                        dbMsg += ",AUDIO_ID=" + AUDIO_ID ;///////////////////////////////////
////                                        dbMsg += "(" + MuList.this.plSL.size() +")" ;///////////////////////////////////
////                                        if(ORGUT.isInListString(MuList.this.plSL , AUDIO_ID)){		//渡された文字が既にリストに登録されていればtrueを返す
////                                        }else{
////                                            MuList.this.plSL.add(AUDIO_ID);
////                                            dbMsg += "→" + MuList.this.plSL.size() +")" ;///////////////////////////////////
////                                        }
////                                        //				myLog(TAG,dbMsg);
////                                        break;
////                                    case CONTEXT_REPET_WR:					//リピート再生リストレコード書込み
////                                        cursor = repeatPlayMake_body(cursor);		//リピート再生
////                                        break;
//                                    //						default:
//                                    //							break;
//                                }
//                                pdCoundtVal =  cursor.getPosition() +1;
//                                dbMsg += "(reqCode=" +reqCode+")pdCoundtVal="+pdCoundtVal + "/" + pdMaxVal ;
//                                long nTime = System.currentTimeMillis() ;
//                                if(nTime  > vTime ){
//                                    publishProgress( pdCoundtVal );		//progressDialog.progBar1.setProgress(step1);
//                                    if( vtWidth > 1 ){
//                                        vtWidth = vtWidth/2;
//                                    }
//                                    vTime = System.currentTimeMillis() + vtWidth;		// 更新タイミング
//                                }
//                            }while( cursor.moveToNext() ) ;				//pdCoundtVal <  pdMaxVal
//                        }
//                        break;
//                }
//                //	Thrd.sleep(200);			//書ききる為の時間（100msでは不足）
//                publishProgress( pdCoundtVal );		//progressDialog.progBar1.setProgress(step1);
//                stepSyuuryou = System.currentTimeMillis();		//この処理の終了時刻の取得
//                dbMsg += this.reqCode +";経過時間"+(int)((stepSyuuryou - stepKaisi)) + "[mS]";				//各処理の所要時間
//                myLog(TAG, dbMsg);
//                return AsyncTaskResult.createNormalResult( reqCode );
//            } catch (Exception e) {
//                myErrorLog(TAG ,  dbMsg + "で" + e);
//                return AsyncTaskResult.createNormalResult(reqCode) ;				//onPostExecuteへ
//            }
//        }
//
//        /**
//         * onProgressUpdate
//         * プログレスバー更新処理： UIスレッドで実行される doInBackground内でpublishProgressメソッドが呼ばれると、
//         * UIスレッド上でこのメソッドが呼ばれます。   このメソッドの引数の型はAsyncTaskの二つ目のパラメータです。
//         * メインスレッドで実行されます。非同期処理の進行状況をプログレスバーで 表示したい時などに使うことができます。*/
//        @Override
//        public void onProgressUpdate(Integer... values) {
//            final String TAG = "onProgressUpdate";
//            String dbMsg = "[MusicPlaylist]";
//            int progress = values[0];
//            try{
//                dbMsg +=  this.reqCode +")progress= " + progress;
//                progressDialog.setProgress(progress);
//                dbMsg += ">> " + progressDialog.getProgress();
//                dbMsg += "/" + progressDialog.getMax();///////////////////////////////////
////				myLog(TAG, dbMsg);
//            }catch (Exception e) {
//                myErrorLog(TAG ,  dbMsg + "で" + e);
//            }
//        }
//
//        /**
//         * onPostExecute
//         * doInBackground が終わるとそのメソッドの戻り値をパラメータとして渡して onPostExecute が呼ばれます。
//         * このパラメータの型は AsyncTask を extends するときの三つめのパラメータです。
//         *  バックグラウンド処理が終了し、メインスレッドに反映させる処理をここに書きます。
//         *  doInBackgroundメソッドの実行後にメインスレッドで実行されます。
//         *  doInBackgroundメソッドの戻り値をこのメソッドの引数として受け取り、その結果を画面に反映させることができます。*/
//        @Override
//        public void onPostExecute(AsyncTaskResult<Integer> ret){	// タスク終了後処理：UIスレッドで実行される AsyncTaskResult<Object>
//            super.onPostExecute(ret);
//            final String TAG = "onPostExecute[plogTask]";
//            String dbMsg = "[MusicPlaylist]";
//            try{
//                Thread.sleep(100);			//書ききる為の時間（100msでは不足）
//                reqCode = ret.getReqCode();
//                dbMsg += "終了；reqCode=" + reqCode +"(終端"+ pdCoundtVal +")";
////                dbMsg += ",callback = " + callback;	/////http://techbooster.org/android/ui/1282/
//                dbMsg += "[ " + pdCoundtVal +  "/ " + pdMaxVal +"]";	/////http://techbooster.org/android/ui/1282/
//                progressDialog.dismiss();
//                onSuccessplogTask(reqCode );		//1.次の処理;2.次の処理に渡すメッセージ
//                myLog(TAG, dbMsg);
//            }catch (Exception e) {
//                myErrorLog(TAG ,  dbMsg + "で" + e);
//            }
//        }
//    }  //public class plogTask


//    @Override
//    public void onSuccessplogTask(int reqCode) {															//①ⅵ3；
//        final String TAG = "onSuccessplogTask";
//        String dbMsg = "[MusicPlaylist]";
//        try{
//            dbMsg +=  "reqCode=" + reqCode;/////////////////////////////////////
//            switch(reqCode) {
//                 case CONTEXT_del_playlist:			//648 このリストを削除
//                    deletPlayListEnd();					//指定したプレイリストを削除する
//                    break;
//            }
//            myLog(TAG, dbMsg);
//        }catch (Exception e) {
//            myErrorLog(TAG ,  dbMsg + "で" + e);
//        }
//    }

    //////////////////////////////////////
    public static void myLog(String TAG , String dbMsg) {
        Util UTIL = new Util();
        Util.myLog(TAG , dbMsg);
    }

    public static void myErrorLog(String TAG , String dbMsg) {
        Util UTIL = new Util();
        Util.myErrorLog(TAG , dbMsg);
    }

    public static String getPrefStr(String keyNmae , String defaultVal, Context context) {        //プリファレンスの読込み
        String retStr = "";
        final String TAG = "getPrefStr";
        String dbMsg="[MusicPlayerService]keyNmae=" + keyNmae;
        Util UTIL = new Util();
        retStr = Util.getPrefStr(keyNmae , defaultVal,context);
        return retStr;
    }

}
