package com.hijiyam_koubou.marasongs;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class MusicPlaylist {

    public Context cContext ;

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
//            dbMsg += ",reqCode=" + reqCode;
//            switch(reqCode) {
//                case CONTEXT_rumdam_saiseizumi:					//642  ランダム再生準備
//                case CONTEXT_REPET_CLEAN:						//リピート再生リストの既存レコード消去
//                case CONTEXT_add_request:						//リクエストリスト
//                    break;
//                default:
//                    int ePosition = 0;
//                    if( plNameSL != null){
//                        if(0 < plNameSL.size() ){
//                            dbMsg += ",plNameSL=" + plNameSL.size() + "件";
//                            ePosition = plNameSL.indexOf(listName);
//                        }
//                        dbMsg += ",ePosition=" + ePosition;
//                        pl_sp.setSelection(ePosition);
//                    }
//                    break;
//            }
//            if( plNameSL != null){
//                int sIndex = plNameSL.indexOf(listName);				//既存のリストを検索
//                dbMsg += ",sIndex=" + sIndex;
//                if(-1 < sIndex){										//見つかれば
//                    int motoID = Integer.valueOf(String.valueOf(plNameAL.get(sIndex).get("_id")) );
//                    dbMsg += ",sIndex=" + motoID;
//                    if( motoID != listID ){
//                        plNameAL.get(sIndex).put("_id", listID) ;
//                        dbMsg += ">>" + plNameAL.get(sIndex).get("_id");
//                        if(result_uri!= null  ){
//                            plNameAL.get(sIndex).put("_data", result_uri) ;
//                            dbMsg += "]" + plNameAL.get(sIndex).get("_data");
//                        }
//                    }
//                }
//            }
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
//            if(this.cContext == null){
//                this.cContext = context;
//            }

//            dbMsg +=  "reqCode=" + MusicPlaylist.this.reqCode;
            dbMsg +=  ",playlist_id=" + playlist_id;
            dbMsg += "[audio_id=" + audio_id;
            dbMsg += "]追加する曲=" + data;
            ContentResolver contentResolver = cContext.getContentResolver();
            ContentValues contentvalues = new ContentValues();
            Uri kakikomiUri = null;

            if(contentResolver == null){
            }else{
                int poSetteiti = getUserListMaxPlayOrder(playlist_id);			//プレイリストの最大のplay_orderを取得する
                dbMsg += "、現在の設定数=" + poSetteiti;
                contentvalues.put("play_order", poSetteiti);
                if(isGalaxy()){
                    kakikomiUri = Uri.parse("content://media/external/audio/music_playlists/" + playlist_id + "/members");
                    contentvalues.put("audio_data", data);
                    dbMsg += ",data_hash=" + data_hash;
                    contentvalues.put("audio_data_hashcode", data_hash);
                }else{
                    kakikomiUri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist_id);
                    dbMsg += ",kakikomiUri=" + kakikomiUri;
                    dbMsg += ",audio_id=" + audio_id;
                    contentvalues.put("audio_id", Integer.valueOf(audio_id));
                }
                dbMsg += ",uri= " + kakikomiUri;
                result_uri = contentResolver.insert(kakikomiUri, contentvalues);				//追加
                dbMsg += ",result_uri=" + result_uri;
                if(result_uri == null){					//NG
                    dbMsg += "失敗 add music : " + playlist_id + ", " + audio_id + ", is null";
                }else if(((int) ContentUris.parseId(result_uri)) == -1){					//NG
                    dbMsg += "失敗 add music : " + playlist_id + ", " + audio_id + ", " + result_uri.toString();
                }else{					//OK
                    dbMsg +=">>成功list_id=" + playlist_id + ", audio_id=" + audio_id + ",result_uri= " + result_uri.toString();
//                    switch(MusicPlaylist.this.reqCode) {
//                        case CONTEXT_rumdam_wr:
//                            dbMsg += ",ランダム再生リストの書込み";
//                            break;
//                        case CONTEXT_REPET_WR:
//                            dbMsg += ",リピート再生リストレコード書込み";
//                            break;
//                        case CONTEXT_add_request:
//                            dbMsg += ",リクエストリスト";
//                            break;
//                        default:
//                            dbMsg += ",書込み";
////						if(artistHTF != null){
////							artistHTF.setVisibility(View.GONE);
////						}
////			//			toolbar.setNavigationIcon(R.drawable.ic_launcher);
////						if(headImgIV != null){
////							headImgIV.setVisibility(View.GONE);
////						}
////						if(mainHTF != null){
////							mainHTF.setVisibility(View.GONE);
////						}
//                            dbMsg += ",スピナー表示";
////						pl_sp.setVisibility(View.VISIBLE);
//                            int ePosition = 0;
//                            if(plNameSL == null) {                                    //プレイリスト名用簡易リスト
//                            }
//                            if(plNameSL != null){									//プレイリスト名用簡易リスト
//                                dbMsg += ",plNameSL=" + plNameSL.size() + "件";
//                                dbMsg += "：=" + MusicPlaylist.this.tuikaSakiListName;
//                                ePosition = plNameSL.indexOf(MusicPlaylist.this.tuikaSakiListName);
//                            }else{
//                                dbMsg += ",plNameSL=null";
//                            }
//                            dbMsg += ",ePosition=" + ePosition;
//                            pl_sp.setSelection(ePosition , false);								//☆勝手に動作させない
//                            break;
//                    }
                }
            }
//			myLog(TAG, dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return result_uri;
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

}
