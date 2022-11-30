package com.hijiyam_koubou.marasongs;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.File;
import java.util.List;

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
            myPreferences = new MyPreferences(context);
            pref_commmn_music= myPreferences.pref_commmn_music;		//共通音楽フォルダ

            myLog(TAG,dbMsg);
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
    @SuppressLint("Range")
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
            @SuppressLint("Range") int delID = cursor.getInt(cursor.getColumnIndex( MediaStore.Audio.Playlists.Members._ID));
            dbMsg +="[" + delID +"]" ;/////////////////////////////////////
            dbMsg += MusicPlaylist.this.tuikaSakilistUri ;/////////////////////////////////////
            @SuppressLint("Range") String rStr = cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Playlists.Members.TITLE));/////////////////////////////////////
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
     * アルバムのアーティスト名を取得する
     * 当面は全曲DBから取得
     * */
    @SuppressLint("Range")
    public String getAlbumArtist(int audioId , Context context){
        String album_artist = null;
        final String TAG = "getAlbumArtist";
        String dbMsg = "[MusicPlaylist]";
        try{
            dbMsg += ",[" + audioId + "]";
            dbMsg += ",全曲リストを読み込み";
            String zenkyokuTName = context.getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
            String fn = context.getResources().getString(R.string.zenkyoku_file);            				//全曲リスト
            File databasePath = context.getDatabasePath(fn);
            dbMsg += ",databasePath=" + databasePath;		//		/data/user/0/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
            ZenkyokuHelper zenkyokuHelper = new ZenkyokuHelper(context, fn);        //全曲リストの定義ファイル		.
            dbMsg += ">>" + zenkyokuHelper.toString();        //03-28java.lang.IllegalArgumentException:  contains a path separator
            File dbF = context.getDatabasePath(fn);				//dbF=/data/user/0/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
            //☆初回時は未だDBが作られていないのでgetApplicationContext()
            dbMsg += ",dbF=" + dbF;
            dbMsg += " , exists=" + dbF.exists() +" , canWrite=" + dbF.canWrite();

            if (dbF.exists()) {
                SQLiteDatabase Zenkyoku_db = zenkyokuHelper.getReadableDatabase();                        //全曲リストファイルを読み書きモードで開く
                String c_selection = "AUDIO_ID = ? ";			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
                String[] c_selectionArgs= {String.valueOf(audioId)};   			//音楽と分類されるファイルだけを抽出する
                Cursor cursor = Zenkyoku_db.query(zenkyokuTName, null, c_selection, c_selectionArgs , null, null, null);
                if (cursor.moveToFirst()) {
                    album_artist = cursor.getString(cursor.getColumnIndex("ALBUM_ARTIST"));
                    dbMsg += album_artist ;
                }
                cursor.close();
                Zenkyoku_db.close();
            } else {
                dbMsg += ",全曲リスト未作成";
            }
            myLog(TAG, dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return album_artist;
    }

   /**
    * アルバム　アーティストのリストアップ
    * **/
    public List<String>  listUpArtist(Context context){
        List<String> artistSL =null;
        final String TAG = "listUpArtist";
        String dbMsg = "[MusicPlaylist]";
        try{
//            dbMsg += ",[" + audioId + "]";
            dbMsg += ",全曲リストを読み込み";
            String zenkyokuTName = context.getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
            String fn = context.getResources().getString(R.string.zenkyoku_file);            				//全曲リスト
            File databasePath = context.getDatabasePath(fn);
            dbMsg += ",databasePath=" + databasePath;		//		/data/user/0/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
            ZenkyokuHelper zenkyokuHelper = new ZenkyokuHelper(context, fn);        //全曲リストの定義ファイル		.
            dbMsg += ">>" + zenkyokuHelper.toString();        //03-28java.lang.IllegalArgumentException:  contains a path separator
            File dbF = context.getDatabasePath(fn);				//dbF=/data/user/0/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
            //☆初回時は未だDBが作られていないのでgetApplicationContext()
            dbMsg += ",dbF=" + dbF;
            dbMsg += " , exists=" + dbF.exists() +" , canWrite=" + dbF.canWrite();

            if (dbF.exists()) {
                SQLiteDatabase Zenkyoku_db = zenkyokuHelper.getReadableDatabase();                        //全曲リストファイルを読み書きモードで開く
                boolean distinct = true;
                String[] columns= {"DISTINCT " + String.valueOf("ALBUM_ARTIST")};
                String c_selection = null;          //"AUDIO_ID = ? ";			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
                String[] c_selectionArgs= null; //{String.valueOf(audioId)};   			//音楽と分類されるファイルだけを抽出する

                Cursor cursor = Zenkyoku_db.query(zenkyokuTName, columns, null, null , null, null, null);
//                Cursor cursor = Zenkyoku_db.query(distinct,zenkyokuTName, columns, null, null , null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do{
                        @SuppressLint("Range") String albumArtist = cursor.getString(cursor.getColumnIndex("ALBUM_ARTIST"));
                        dbMsg += albumArtist ;
                        artistSL.add(albumArtist);
                    }while(cursor.moveToNext());
                }
                cursor.close();
                Zenkyoku_db.close();
                dbMsg += "、" + artistSL.size() + "件" ;
            } else {
                dbMsg += ",全曲リスト未作成";
            }
            myLog(TAG, dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return artistSL;
    }

    /**
     * 指定されたアーティストのアルバム名リストを返す
     * **/
    public List<String>  listUpAlubm(String albumArtist,Context context){
        List<String> albumSL =null;
        final String TAG = "listUpArtist";
        String dbMsg = "[MusicPlaylist]";
        String dbMsg2 = "";

        try{
//            dbMsg += ",[" + audioId + "]";
            dbMsg += ",albumArtist=" + albumArtist;
            String zenkyokuTName = context.getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
            String fn = context.getResources().getString(R.string.zenkyoku_file);            				//全曲リスト
            File databasePath = context.getDatabasePath(fn);
            dbMsg += ",databasePath=" + databasePath;		//		/data/user/0/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
            ZenkyokuHelper zenkyokuHelper = new ZenkyokuHelper(context, fn);        //全曲リストの定義ファイル		.
            dbMsg += ">>" + zenkyokuHelper.toString();        //03-28java.lang.IllegalArgumentException:  contains a path separator
            File dbF = context.getDatabasePath(fn);				//dbF=/data/user/0/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
            //☆初回時は未だDBが作られていないのでgetApplicationContext()
            dbMsg += ",dbF=" + dbF;
            dbMsg += " , exists=" + dbF.exists() +" , canWrite=" + dbF.canWrite();

            if (dbF.exists()) {
                SQLiteDatabase Zenkyoku_db = zenkyokuHelper.getReadableDatabase();                        //全曲リストファイルを読み書きモードで開く
                boolean distinct = true;
                String[] columns= null;     //{"DISTINCT " + String.valueOf("ALBUM_ARTIST")};
                String c_selection = "ALBUM_ARTIST = ? ";          //"AUDIO_ID = ? ";			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
                String[] c_selectionArgs= {String.valueOf(albumArtist)};   			//音楽と分類されるファイルだけを抽出する

                Cursor cursor = Zenkyoku_db.query(zenkyokuTName, columns, c_selection, c_selectionArgs , null, null, null);
                if (cursor.moveToFirst()) {
                    do{
                        @SuppressLint("Range") String albumName = cursor.getString(cursor.getColumnIndex("ALBUM"));
                        dbMsg2 += "," + albumName ;
                        albumSL.add(albumName);
                    }while(cursor.moveToNext());
                }
                cursor.close();
                Zenkyoku_db.close();
                dbMsg += "、" + albumSL.size() + "件" ;
            } else {
                dbMsg += ",全曲リスト未作成";
            }
            myLog(TAG, dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return albumSL;
    }

    /**
     * 指定されたアーティストの指定されたアルバムからタイトル名リストを返す
     * **/
    public List<String>  listUpTitol(String albumArtist,String albumName,Context context){
        List<String> titolSL =null;
        final String TAG = "listUpTitol";
        String dbMsg = "[MusicPlaylist]";
        String dbMsg2 = "";

        try{
            dbMsg += ",albumArtist=" + albumArtist;
            dbMsg += ",albumName=" + albumName;
            String zenkyokuTName = context.getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
            String fn = context.getResources().getString(R.string.zenkyoku_file);            				//全曲リスト
            File databasePath = context.getDatabasePath(fn);
            dbMsg += ",databasePath=" + databasePath;		//		/data/user/0/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
            ZenkyokuHelper zenkyokuHelper = new ZenkyokuHelper(context, fn);        //全曲リストの定義ファイル		.
            dbMsg += ">>" + zenkyokuHelper.toString();        //03-28java.lang.IllegalArgumentException:  contains a path separator
            File dbF = context.getDatabasePath(fn);				//dbF=/data/user/0/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
            //☆初回時は未だDBが作られていないのでgetApplicationContext()
            dbMsg += ",dbF=" + dbF;
            dbMsg += " , exists=" + dbF.exists() +" , canWrite=" + dbF.canWrite();

            if (dbF.exists()) {
                SQLiteDatabase Zenkyoku_db = zenkyokuHelper.getReadableDatabase();                        //全曲リストファイルを読み書きモードで開く
                boolean distinct = true;
                String[] columns= null;     //{"DISTINCT " + String.valueOf("ALBUM_ARTIST")};
                String c_selection = "ALBUM_ARTIST = ? + ALBUM = ?";          //"AUDIO_ID = ? ";			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
                String[] c_selectionArgs= {String.valueOf(albumArtist) , String.valueOf(albumName)};   			//音楽と分類されるファイルだけを抽出する
                Cursor cursor = Zenkyoku_db.query(zenkyokuTName, columns, c_selection, c_selectionArgs , null, null, null);
                if (cursor.moveToFirst()) {
                    do{
                        @SuppressLint("Range") String titolName = cursor.getString(cursor.getColumnIndex("ALBUM"));
                        dbMsg2 += "," + titolName ;
                        titolSL.add(titolName);
                    }while(cursor.moveToNext());
                }
                cursor.close();
                Zenkyoku_db.close();
                dbMsg += "、" + titolSL.size() + "件" ;
            } else {
                dbMsg += ",全曲リスト未作成";
            }
            myLog(TAG, dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return titolSL;
    }


    /**
     * 指定されたplayListのプレイオーダーにある曲のUrlを返す
     * ***/
    @SuppressLint("Range")
    public String getPlaylistItemData(int listId , int playOrder){				//指定された名称のリストを作成する
        String data = null;
        final String TAG = "getPlaylistItemData";
        String dbMsg = "[MusicPlaylist]";
        try{
            dbMsg += ",[" + listId + "]" + playOrder;
            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", listId);
            String[] columns = null;    //{String.valueOf(playOrder)};			//{ idKey, nameKey };
            String c_selection =  MediaStore.Audio.Playlists.Members.PLAY_ORDER +" = ? ";
            String[] selectionArgs = { String.valueOf(playOrder) };
            Cursor playList = cContext.getContentResolver().query(uri, columns, c_selection, selectionArgs, null );
//            dbMsg += ",該当"+playList.getCount() +"件";
            if( playList.moveToFirst() ){
                data = playList.getString(playList.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA));
                dbMsg += ",url=" + data;
            }else{
                dbMsg += ",該当無し";
            }
            myLog(TAG, dbMsg);
            playList.close();
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return data;
    }

    /**
     * 指定されたplayListからデータを検索し、プレイオーダーを返す
     * ***/
    @SuppressLint("Range")
    public int getPlaylistItemOrder(int listId , String data){				//指定された名称のリストを作成する
        int playOrder = -1;
        final String TAG = "getPlaylistItemOrder";
        String dbMsg = "[MusicPlaylist]";
        try{
            dbMsg += ",[" + listId + "]" + data;
            if(data != null){
                Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", listId);
                String[] columns = null;			//{ idKey, nameKey };
                String selection =  MediaStore.Audio.Playlists.Members.DATA  + " = ? ";
                String[] selectionArgs =  { data };
                String c_orderBy = MediaStore.Audio.Playlists.Members.DATA;
                Cursor playList = cContext.getContentResolver().query(uri, columns, selection, selectionArgs, c_orderBy );
//            dbMsg += ",該当"+playLists.getCount() +"件";
                if( playList.moveToFirst() ){
                    playOrder = Integer.parseInt(playList.getString(playList.getColumnIndex(MediaStore.Audio.Playlists.Members.PLAY_ORDER)));
                    dbMsg += ",playOrder=" + playOrder;
                }else{
                    dbMsg += ",該当無し";
                }
                playList.close();
            }else{
                playOrder = 0;
            }
            myLog(TAG, dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return playOrder;
    }

    /**
     * 指定されたplayListのプレイオーダーにある曲のUrlを返す
     * ***/
    public Cursor getPlaylistItems(int listId , int playOrder){				//指定された名称のリストを作成する
        Cursor playList = null;
        final String TAG = "getPlaylistItems";
        String dbMsg = "[MusicPlaylist]";
        try{
            dbMsg += ",[" + listId + "]" + playOrder;
            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", listId);
            String[] columns = null;            //{String.valueOf(playOrder)};			//{ idKey, nameKey };
            String c_selection =  MediaStore.Audio.Playlists.Members.PLAY_ORDER +" = ? ";
            String[] selectionArgs = { String.valueOf(playOrder) };
            playList = cContext.getContentResolver().query(uri, columns, c_selection, selectionArgs, null );
//            dbMsg += ",該当"+playList.getCount() +"件";
            if( playList.moveToFirst() ){
                @SuppressLint("Range") String data = playList.getString(playList.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA));
                dbMsg += ",url=" + data;
                dbMsg += "," + playList.getColumnCount() + "項目";

            }else{
                dbMsg += ",該当無し";
            }
            myLog(TAG, dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return playList;
    }


    /**
     *  指定された名称のリストを作成する;既に有ればlistIDを返し、無ければ作成してIdを返す */
    @SuppressLint("Range")
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
//                myLog(TAG, dbMsg);
            }
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return result_uri;
    }

    /**
     * uriから指定された名称を返す
     *
     * reqItem は 0:アーティスト、1:アルバム、2:タイトル
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
        MyUtil MyUtil = new MyUtil();
        MyUtil.myLog(TAG , dbMsg);
    }

    public static void myErrorLog(String TAG , String dbMsg) {
        MyUtil MyUtil = new MyUtil();
        MyUtil.myErrorLog(TAG , dbMsg);
    }

    public static String getPrefStr(String keyNmae , String defaultVal, Context context) {        //プリファレンスの読込み
        String retStr = "";
        final String TAG = "getPrefStr";
        String dbMsg="[MusicPlayerService]keyNmae=" + keyNmae;
        MyUtil MyUtil = new MyUtil();
        retStr = MyUtil.getPrefStr(keyNmae , defaultVal,context);
        return retStr;
    }

    public static boolean setPrefInt(String keyNmae , int wrightVal , Context context) {        //プリファレンスの読込み
        boolean retBool = false;
        final String TAG = "setPrefInt";
        String dbMsg="[MusicPlayerService]keyNmae=" + keyNmae;
        MyUtil MyUtil = new MyUtil();
        retBool = MyUtil.setPrefInt(keyNmae , wrightVal,context);
        return retBool;
    }

}
