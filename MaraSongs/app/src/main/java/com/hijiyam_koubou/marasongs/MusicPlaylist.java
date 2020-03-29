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
    public MyPreferences myPreferences;

    public String tuikaSakiListName;		//操作対象リスト名
    public int tuikaSakiListID;		//操作対象リスト名
    public Uri tuikaSakilistUri;		//編集中リストUri
    public Cursor playLists;
    public String pref_commmn_music="";		//共通音楽フォルダ
    public String nowAlbumArtist;
    public String nowAlbum;

    public int reqCode = -1;
    static final int CONTEXT_del_playlist = 100;			//このリストを削除


    /***
     * Androidのlaylistを使用する
     * ****/
    public MusicPlaylist(Context context) {
        final String TAG = "MusicPlaylist";
        String dbMsg= "[MusicPlaylist]";
        try{
            cContext = context;
            myPreferences = new MyPreferences();
            pref_commmn_music= myPreferences.pref_commmn_music;		//共通音楽フォルダ

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
//            myLog(TAG, dbMsg);
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
        int listId = 0;
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
                listId = Integer.valueOf(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID)));
            } else{
                dbMsg +=  ">>新規作成" ;
                result_uri = addPlaylist(listName, null, null);		//プレイリストを新規作成する
                dbMsg += ",result_uri=" + result_uri;			//fastItemeFn=/storage/sdcard0/Music/Jimmy Cliff/Follow My Mind/07 Remake The World.wma
                listId = (int)ContentUris.parseId(result_uri);
            }
            if(listName.equals(cContext.getResources().getString(R.string.listmei_zemkyoku))){
//                if(listId != pref_zenkyoku_list_id){			// 全曲リスト
                    setPrefInt("pref_zenkyoku_list_id", listId ,cContext);
//                }
            }else if(listName.equals(cContext.getResources().getString(R.string.playlist_namae_saikintuika))){
//                if(listId != saikintuika_list_id){			// 最近追加
                    setPrefInt("saikintuika_list_id", listId ,cContext);
//                }
            }else if(listName.equals(cContext.getResources().getString(R.string.playlist_namae_saikinsisei))){
//                if(listId != saikinsisei_list_id){			// 最近再生
                    setPrefInt("saikinsisei_list_id", listId ,cContext);
//                }
            }

            dbMsg += ",[" + listId + "]" + result_uri;			//[42529]content://media/external/audio/playlists/42529
            cursor.close();
            myLog(TAG, dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return listId;
    }

    
    /**
     * プレイリストへ曲を追加する	 */
    public Uri addMusicToPlaylist(int playlist_id , int audio_id, String data){		//プレイリストへ曲を追加する
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
            }else {
                int poSetteiti = getUserListMaxPlayOrder(playlist_id);            //プレイリストの最大のplay_orderを取得する
                dbMsg += "、次は" + poSetteiti + "曲目";
                contentvalues.put(MediaStore.Audio.Playlists.Members._ID, poSetteiti + 1);
                contentvalues.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, poSetteiti + 1);
                contentvalues.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, Integer.valueOf(audio_id));
//                contentvalues.put(MediaStore.Audio.Playlists.Members.DATA, data);
                if (isGalaxy()) {
                    int data_hash = 0;              //内容不明
                    kakikomiUri = Uri.parse("content://media/external/audio/music_playlists/" + playlist_id + "/members");
                    contentvalues.put("audio_data", data);
                    dbMsg += ",data_hash=" + data_hash;
                    contentvalues.put("audio_data_hashcode", data_hash);
                } else {
                    dbMsg += ",SDK_INT= " + Build.VERSION.SDK_INT;
                    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ) { //Andrid10以降
                        dbMsg += "=Pai" ;
                        //						//  https://codechacha.com/ja/android-mediastore-insert-media-files/
                        kakikomiUri = MediaStore.Audio.Playlists.Members.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY,playlist_id);
// MuList は.VOLUME_EXTERNAL　でもOK　：content://media/external_primary/audio/playlists/18140/members,
                    }else{
                        kakikomiUri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist_id);
                    }
                }
                dbMsg += ",uri= " + kakikomiUri;
                dbMsg += ",contentvalues( " + contentvalues.toString() + " )";
                result_uri = contentResolver.insert(kakikomiUri, contentvalues);                //追加
                dbMsg += ",result_uri=" + result_uri;
                if(result_uri == null){					//NG
                    dbMsg += "失敗 add music : " + playlist_id + ", " + audio_id + ", is null";
                }else if(((int)ContentUris.parseId(result_uri)) == -1){					//NG
                    dbMsg += "失敗 add music : " + playlist_id + ", " + audio_id + ", " + result_uri.toString();
                }else{					//OK
                    dbMsg +=">>成功list_id=" + playlist_id + ", audio_id=" + audio_id + ",result_uri= " + result_uri.toString();
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

    /**
     * uriから指定された名称を返す
     *
     * reqItem は 0:アーティスト、1:アルバム2、:タイトル
     * **/
    public String uri2Item(String uriStr , int reqItem) {
        final String TAG = "uri2Item";
        String dbMsg= "[MusicPlaylist]";
        String retStr = "";
        try{
            String albumArtist = "";
            String album = "";
            String titol = "";

            dbMsg += "uriStr" + uriStr;
            dbMsg += "、pref_commmn_music=" + pref_commmn_music;
            String removed = uriStr.replace(pref_commmn_music + File.separator, "");
            String rStrs[] = removed.split(File.separator);
            if(rStrs.length == 3){
                albumArtist = rStrs[0];
                album = rStrs[1];
                titol = rStrs[2];
            }
            dbMsg += "、reqItem=" + reqItem;
            switch(reqItem) {
                case 0:
                    dbMsg += ":アーティスト";
                    retStr = albumArtist;
                    break;
                case 1:
                    dbMsg += ":アルバム";
                    retStr = album;
                    break;
                case 2:
                    dbMsg += ":タイトル";
                    retStr = titol;
                    break;
            }

            dbMsg += ",retStr" + retStr;
            myLog(TAG,dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG,dbMsg + "で"+e.toString());
        }
        return retStr;
    }

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

    public static boolean setPrefInt(String keyNmae , int wrightVal , Context context) {        //プリファレンスの読込み
        boolean retBool = false;
        final String TAG = "setPrefInt";
        String dbMsg="[MusicPlayerService]keyNmae=" + keyNmae;
        Util UTIL = new Util();
        retBool = Util.setPrefInt(keyNmae , wrightVal,context);
        return retBool;
    }

}
