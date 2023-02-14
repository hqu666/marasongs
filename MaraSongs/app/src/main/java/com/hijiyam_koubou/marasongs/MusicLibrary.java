package com.hijiyam_koubou.marasongs;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * 選択されたリストでMedia Sessionのリスト作成
 * Copyright 2017 The Android Open Source Project
 *　https://github.com/SIY1121/MediaSessionSample/blob/master/app/src/main/java/space/siy/mediasessionsample/MusicLibrary.java
 * **/
public class MusicLibrary {

    private static final TreeMap<String, MediaMetadataCompat> music = new TreeMap<>();
    private static final HashMap<String, Integer> albumRes = new HashMap<>();
    private static final HashMap<String, String> musicFileName = new HashMap<>();

//    static {
//        createMediaMetadataCompat(
//                "Jazz_In_Paris",
//                "Jazz in Paris",
//                "Media Right Productions",
//                "Jazz & Blues",
//                "Jazz",
//                103,
//                TimeUnit.SECONDS,
//                "jazz_in_paris.mp3",
//                R.drawable.no_image,
//                "album_jazz_blues");
//        createMediaMetadataCompat(
//                "The_Coldest_Shoulder",
//                "The Coldest Shoulder",
//                "The 126ers",
//                "Youtube Audio Library Rock 2",
//                "Rock",
//                160,
//                TimeUnit.SECONDS,
//                "the_coldest_shoulder.mp3",
//                R.drawable.no_image,
//                "album_youtube_audio_library_rock_2");
//    }

    /**
     * 指定されたプレイリストからmediasessionのリスト作成
     * **/
    public int makeList(Context con,long playlistId,String listName) {
        final String TAG = "makeList";
        String dbMsg = "";
        int totalSongs = -1;
        try {
            OrgUtil oUtil = new OrgUtil();
            dbMsg += "選択されたプレイリスト[ID="+playlistId + "]" + listName;
            if(listName.equals( con.getResources().getString(R.string.listmei_zemkyoku))) {
                String fn = con.getString(R.string.zenkyoku_file);			//全曲リスト名
                dbMsg +=  ",fn=" + fn;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
                ZenkyokuHelper zenkyokuHelper = new ZenkyokuHelper(con, fn);        //全曲リストの定義ファイル		.
                SQLiteDatabase Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
                dbMsg =  ">isOpen>" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
                dbMsg =  ",getPageSize=" + Zenkyoku_db.getPageSize() + "件、" ;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
                String zenkyokuTName = con.getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
                String[] c_columns =null;					//②引数tableには、テーブル名を指定します。
                String c_selection = null;
                String[] c_selectionArgs= null;
                String c_groupBy = null;        //"ARTIST";
                String c_having = null;
                String c_orderBy = null;        // "ARTIST"; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
                Cursor cursor = Zenkyoku_db.query(zenkyokuTName, c_columns, c_selection, c_selectionArgs, c_groupBy, c_having, c_orderBy);    // table, columns,new String[] {MotoN, albamN}
                totalSongs = cursor.getCount();
                dbMsg += ",取得" + totalSongs + "件";
                if(cursor.moveToFirst()){
                    do{
                        dbMsg +="\n["+ cursor.getPosition()  +"/"+ cursor.getCount() +"]";
                        @SuppressLint("Range") int audio_id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                        dbMsg += "[audio_id="+ audio_id + "]";
                        @SuppressLint("Range") String albumArtist = cursor.getString(cursor.getColumnIndex("ALBUM_ARTIST"));
                        @SuppressLint("Range") String ArtistName = cursor.getString(cursor.getColumnIndex("ARTIST"));
                        dbMsg += "," + ArtistName ;
                        @SuppressLint("Range") String AlbumName = cursor.getString(cursor.getColumnIndex("ALBUM"));
                        dbMsg += "," + AlbumName ;
                        @SuppressLint("Range") String titolName = cursor.getString(cursor.getColumnIndex("TITLE"));
                        dbMsg += "," + titolName ;
                        @SuppressLint("Range") String dataVal = cursor.getString(cursor.getColumnIndex("DATA"));
                        dbMsg += ",dataVal="+ dataVal;
                        @SuppressLint("Range") long duration = Long.getLong(cursor.getString(cursor.getColumnIndex("DURATION")));
//                        MuList.this.objMap.put("year" ,cursor.getString(cursor.getColumnIndex("YEAR")) );
//                        MuList.this.objMap.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER , playOrder );
                        int albumArtResId= 0;           //R.drawable.no_image,
                        String albumID=oUtil.retAlbumID(con,albumArtist,AlbumName);
                        albumArtResId = oUtil.getAlbumArtResId(Long.parseLong(albumID),con);
                        if(albumArtResId ==0){
                            albumArtResId =R.drawable.no_image;
                        }
                        dbMsg += ",albumArtResId="+ albumArtResId;
                        String genreName = oUtil.retGenre(con,albumArtist,AlbumName);

                        createMediaMetadataCompat(
                                audio_id + "",
                                titolName,
                                ArtistName,
                                AlbumName,
                                genreName,
                                duration,
                                TimeUnit.SECONDS,
                                dataVal,
                                albumArtResId,
                                albumArtist);

                    }while(cursor.moveToNext());
                    cursor.close();
                }else{
                    dbMsg += "全曲拾えず" ;
                }
            }else{
                final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
                final String[] columns = null;			//{ idKey, nameKey };
                String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
                Cursor rCursor= con.getContentResolver().query(uri, columns, null, null, c_orderBy );
                totalSongs = rCursor.getCount();
                dbMsg += ",取得" + totalSongs + "件";
                if( rCursor.moveToFirst() ){
                    do{
                        int rPosi = rCursor.getPosition();
                        dbMsg= "[" + rPosi +"/" + rCursor.getCount() +"曲]";		//MuList.this.rCount
                        String audio_id = null;
                        String dataVal = null;
                        String subText = null;
                        String ArtistName = null;
                        String albumArtist = null;
                        String AlbumName = null;
                        String titolName = null;
                        String genreName = null;
                        long duration = 0;
                        int albumArtResId= 0;           //R.drawable.no_image,
                        for(int i = 0 ; i < rCursor.getColumnCount() ; i++ ){				//MuList.this.koumoku
                            String cName = rCursor.getColumnName(i);
                            dbMsg += "[" + i +"/" + rCursor.getColumnCount() +"項目]"+ cName;
                            if(cName.equals(MediaStore.Audio.Playlists.Members.XMP)){		//[31/66項目]xmp=【Blob】[32/37]artist_key
                                dbMsg += "は読み込めない";
                            }else{
                                if( cName.equals("album_artist")){		//[26/37]
                                    String cVal = rCursor.getString(i);
                                    if(cVal != null){
                                        cVal = cVal;
                                    }
                                    dbMsg +=  "="+cVal;
                                    albumArtist = cVal;
                                }else if( cName.equals(MediaStore.Audio.Playlists.Members.AUDIO_ID)){		//[33/37]artist=Santana
                                    String cVal = rCursor.getString(i);
                                    if(cVal != null){
                                        cVal = cVal;
                                    }else{
                                        cVal = con.getResources().getString(R.string.bt_unknown);			//不明
                                    }
                                    dbMsg +=  "="+cVal;
                                    audio_id = cVal;
                                }else if( cName.equals(MediaStore.Audio.Playlists.Members.ARTIST)){		//[33/37]artist=Santana
                                    String cVal = rCursor.getString(i);
                                    if(cVal != null){
                                        cVal = cVal;
                                    }else{
                                        cVal = con.getResources().getString(R.string.bt_unknown);			//不明
                                    }
                                    dbMsg +=  "="+cVal;
                                    ArtistName = cVal;
                                }else if( cName.equals(MediaStore.Audio.Playlists.Members.ALBUM)){		//[33/37]artist=Santana
                                    String cVal = rCursor.getString(i);
                                    if(cVal != null){
                                        cVal = cVal;
                                    }else{
                                        cVal = con.getResources().getString(R.string.bt_unknown);			//不明
                                    }
                                    dbMsg +=  "="+cVal;
                                    AlbumName = cVal;
                                }else if( cName.equals(MediaStore.Audio.Playlists.Members.TITLE)){		//[12/37]title=Just Feel Better
                                    String cVal = rCursor.getString(i);
                                    if(cVal != null){
                                        cVal = cVal;
                                    }else{
                                        cVal = con.getResources().getString(R.string.bt_unknown);			//不明
                                    }
                                    titolName = cVal;
                                }else if( cName.equals(MediaStore.Audio.Playlists.Members.DURATION)){	//[14/37]duration=252799>>04:12 799
                                    String cVal = rCursor.getString(i);
                                    dbMsg +=  "="+cVal;
                                    if(cVal != null){
                                        cVal = cVal;
                                    }else{
                                        duration =Long.valueOf(cVal);
                                    }
                       //             Dur = "["+ ORGUT.sdf_mss.format(Long.valueOf(cVal)) + "]";
                                }else if( cName.equals(MediaStore.Audio.Playlists.Members.DATA)){	//[5/37]_data=/storage/sdcard0/external_sd/Music/Santana/All That I Am/05 Just Feel Better.wma
                                    String cVal = rCursor.getString(i);
                                    if(cVal != null){
                                        cVal = cVal;
                                    }
                                    dbMsg +=  "="+cVal;
                                    dataVal=cVal;
                                }else if( cName.equals(MediaStore.Audio.Playlists.Members.TRACK)){
                                    String cVal = rCursor.getString(i);
                                    //	cVal = MyUtil.checKTrack( cVal);
                                }else if( cName.equals(MediaStore.Audio.Playlists.Members.ALBUM_ID)){
                                    String cVal = rCursor.getString(i);
                                    dbMsg +=  "="+cVal;
                                    if(cVal != null){
//                                        cVal =  cVal.replace(";", "");
//                                        dbMsg +=  ">>"+cVal;
                                        Long cLing = Long.valueOf(cVal);
                                        albumArtResId = oUtil.getAlbumArtResId(cLing,con);
                                    }
                                    if(albumArtResId ==0){
                                        albumArtResId =R.drawable.no_image;
                                    }
                                    dbMsg += ",albumArtResId="+ albumArtResId;
                                }else if( cName.equals(MediaStore.Audio.Playlists.Members.GENRE)){
                                    String cVal = rCursor.getString(i);
                                    dbMsg +=  "="+cVal;
                                    if(cVal != null){
                                        cVal = cVal;
                                    }else{
                                        cVal = con.getResources().getString(R.string.bt_unknown);			//不明
                                    }
                                    genreName =cVal;
                                }else{
                                    int cPosition = rCursor.getColumnIndex(cName);
                                    dbMsg += "『" + cPosition+"』";
                                    String cVal ="";
                                    if(0<cPosition){
                                        int colType = rCursor.getType(cPosition);
                                        //		dbMsg += ",Type=" + colType + ",";
                                        switch (colType){
                                            case Cursor.FIELD_TYPE_NULL:          //0
                                                cVal ="【null】" ;
                                                break;
                                            case Cursor.FIELD_TYPE_INTEGER:         //1
                                                @SuppressLint("Range") int cInt = rCursor.getInt(cPosition);
                                                dbMsg += cInt+"【int】";
                                                cVal=String.valueOf(cInt);
                                                break;
                                            case Cursor.FIELD_TYPE_FLOAT:         //2
                                                @SuppressLint("Range") float cFlo = rCursor.getFloat(cPosition);
                                                dbMsg += cFlo+"【float】";
                                                cVal=String.valueOf(cFlo);
                                                break;
                                            case Cursor.FIELD_TYPE_STRING:          //3
                                                cVal = rCursor.getString(cPosition);
                                                dbMsg +=  cVal+"【String】";
                                                break;
                                            case Cursor.FIELD_TYPE_BLOB:         //4
                                                //@SuppressLint("Range") String cBlob = String.valueOf(cursor.getBlob(cPosition));
                                                cVal ="【Blob】";
                                                break;
                                            default:
                                                cVal = String.valueOf(rCursor.getString(cPosition));
                                                dbMsg +=  cVal;
                                                break;
                                        }
                                    }
                                    dbMsg += "="+cVal;
                                }
                                createMediaMetadataCompat(
                                        audio_id + "",
                                        titolName,
                                        ArtistName,
                                        AlbumName,
                                        genreName,
                                        duration,
                                        TimeUnit.SECONDS,
                                        dataVal,
                                        albumArtResId,
                                        albumArtist);
                            }
                        }
                    }while(rCursor.moveToNext());
                    rCursor.close();
                }
            }
            myLog(TAG , dbMsg);
        } catch (Exception er) {
            myErrorLog(TAG , dbMsg + " でエラー発生；" + er);
        }
        return totalSongs;
    }

    public int getIndex(String mediaId) {
        final String TAG = "getIndex";
        String dbMsg = "";
        int indexInt = 0;
        try {
            for (MediaMetadataCompat metadata : music.values()) {
                if( metadata.containsKey(mediaId)) {
                    break;
                }
                indexInt++;
            }
//                for (int i = 0; i < music.size(); i++) {
//                    MediaMetadataCompat rItem = music.get(i);
//                    rItem.getClass().
//                            System.out.println(music.get(i));
//                }
//            }
            dbMsg = "indexInt=" + indexInt;
            myLog(TAG , dbMsg);
        } catch (Exception er) {
            myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
        }
        return indexInt;
    }

    public static String getRoot() {
        return "root";
    }

    private static String getAlbumArtUri(String albumArtResName) {
        final String TAG = "getAlbumArtUri";
        String dbMsg = "";
        try {
            myLog(TAG , dbMsg);
        } catch (Exception er) {
            myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
        }

        return ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                BuildConfig.APPLICATION_ID + "/drawable/" + albumArtResName;
    }

    public static String getMusicFilename(String mediaId) {
        final String TAG = "getMusicFilename";
        String dbMsg = "";
        try {
            myLog(TAG , dbMsg);
        } catch (Exception er) {
            myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
        }

        return musicFileName.containsKey(mediaId) ? musicFileName.get(mediaId) : null;
    }

    private static int getAlbumRes(String mediaId) {
        final String TAG = "getAlbumRes";
        String dbMsg = "";
        try {
            myLog(TAG , dbMsg);
        } catch (Exception er) {
            myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
        }

        return albumRes.containsKey(mediaId) ? albumRes.get(mediaId) : 0;
    }

    public static Bitmap getAlbumBitmap(Context context, String mediaId) {
        final String TAG = "getAlbumBitmap";
        String dbMsg = "";
        try {
            myLog(TAG , dbMsg);
        } catch (Exception er) {
            myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
        }
        return BitmapFactory.decodeResource(context.getResources(),
                MusicLibrary.getAlbumRes(mediaId));
    }

    ///サービスのonCreatから呼ばれる
    public static List<MediaBrowserCompat.MediaItem> getMediaItems() {
        final String TAG = "getMediaItems";
        String dbMsg = "";
        List<MediaBrowserCompat.MediaItem> result = new ArrayList<>();
        try {
            for (MediaMetadataCompat metadata : music.values()) {
                result.add(
                        new MediaBrowserCompat.MediaItem(
                                metadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
            }
            myLog(TAG , dbMsg);
        } catch (Exception er) {
            myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
        }
        return result;
    }

    public static MediaMetadataCompat getMetadata(Context context, String mediaId) {
        final String TAG = "getMetadata";
        String dbMsg = "";
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        try {
            dbMsg +=  "mediaId=" + mediaId;
            MediaMetadataCompat metadataWithoutBitmap = music.get(mediaId);
            Bitmap albumArt = getAlbumBitmap(context, mediaId);

            // Since MediaMetadataCompat is immutable, we need to create a copy to set the album art.
            // We don't set it initially on all queueItems so that they don't take unnecessary memory.
            for (String key :
                    new String[]{
                            MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                            MediaMetadataCompat.METADATA_KEY_ALBUM,
                            MediaMetadataCompat.METADATA_KEY_ARTIST,
                            MediaMetadataCompat.METADATA_KEY_GENRE,
                            MediaMetadataCompat.METADATA_KEY_TITLE
                    }) {
                builder.putString(key, metadataWithoutBitmap.getString(key));
            }
            builder.putLong(
                    MediaMetadataCompat.METADATA_KEY_DURATION,
                    metadataWithoutBitmap.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt);
            myLog(TAG , dbMsg);
        } catch (Exception er) {
            myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
        }
        return builder.build();
    }

    private static void createMediaMetadataCompat(
            String mediaId,
            String title,
            String artist,
            String album,
            String genre,
            long duration,
            TimeUnit durationUnit,
            String musicFilename,
            int albumArtResId,
            String albumArtResName) {
        final String TAG = "createMediaMetadataCompat";
        String dbMsg = "";
        try {
            dbMsg +=  "mediaId=" + mediaId;

            music.put(
                    mediaId,
                    new MediaMetadataCompat.Builder()
                            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                                    TimeUnit.MILLISECONDS.convert(duration, durationUnit))
                            .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
                            .putString(
                                    MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                                    getAlbumArtUri(albumArtResName))
                            .putString(
                                    MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
                                    getAlbumArtUri(albumArtResName))
                            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                            .build());
            albumRes.put(mediaId, albumArtResId);
            musicFileName.put(mediaId, musicFilename);
            myLog(TAG , dbMsg);
        } catch (Exception er) {
            myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
        }
    }


    public static void myLog(String TAG , String dbMsg) {
        MyUtil MyUtil = new MyUtil();
        MyUtil.myLog(TAG ,  "[MusicLibrary]" + dbMsg);
    }

    public static void myErrorLog(String TAG , String dbMsg) {
        MyUtil MyUtil = new MyUtil();
        MyUtil.myErrorLog(TAG ,  "[MusicLibrary]" + dbMsg);
    }


}