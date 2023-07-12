package com.hijiyam_koubou.marasongs;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.service.media.MediaBrowserService;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.TrackSelectionParameters;
import androidx.media3.common.Tracks;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.RenderersFactory;
import androidx.media3.exoplayer.ima.ImaAdsLoader;
import androidx.media3.exoplayer.ima.ImaServerSideAdInsertionMediaSource;
import androidx.media3.exoplayer.offline.DownloadRequest;
import androidx.media3.exoplayer.source.ads.AdsLoader;
import androidx.media3.exoplayer.util.DebugTextViewHelper;
import androidx.media3.exoplayer.util.EventLogger;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaStyleNotificationHelper;
import androidx.media3.ui.PlayerView;
import androidx.preference.PreferenceManager;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicService extends MediaBrowserService {
    public Context context;
    public OrgUtil ORGUT;						//自作関数集
    public MyUtil MyUtil;
    public MyPreferences myPreferences;
    private static final String MY_MEDIA_ROOT_ID = "media_root_id";
    private static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";
    public ExoPlayer exoPlayer;				//音楽プレイヤーの実体
    public MediaSession mediaSession;            //MediaSessionCompat ？　MediaSession
//    public MediaStyleNotificationHelper.MediaStyle mediaStyle;           //androidx.media3.session.MediaStyleNotificationHelper.
    //    private MediaSessionCompat.Token sessionToken;
//    private PlaybackStateCompat.Builder stateBuilder;

    public String currentListId = "";
    public String currentListName = "";
    public String currentAlbumName = "";

    /**プレイヤーに保持させるMediaItemのリスト*/
    public List<MediaItem> mediaItemList;
    /**conntentProviderを読み込んだプレイリスト用ArrayList*/
    public List<Map<String, Object>> plAL;

    /**操作中のプレイリストのMediaItemのリスト
     * <ul>
     *     <li>
     *        ListViewのレコードがクリックされた時点で切り替える
     *     </li>
     *  </ul>
     * */
    public List<MediaItem> mediaItemList2;
    /**conntentProviderを読み込んだプレイリスト用ArrayList*/
    public List<Map<String, Object>> plAL2;

    public Map<String, Object> objMap;				//汎用マップ
    public int mIndex = 0;
    /**
     * 状況変化前の再生状況
     * <ul>
     *     <li>設定時点は
     *     <ul>
     *        <li> onStartCommandの先頭と
     *        <li> ACTION_PLAY/ACTION_PAUSE
     * **/
    public boolean nowPlay;
    public String pref_data_url;
    public String artistName;
    public String albumName;
    public String songTitol;
    public Bitmap albumArtBitmap;
    public int repeatMode = Player.REPEAT_MODE_ALL;                    //2:プレイリスト内繰り返し  /  Player.REPEAT_MODE_ONE: 現在の項目が無限ループで繰り返されます。
    public long saiseiJikan = 0;
    //プリファレンス
    public SharedPreferences sharedPref;
    public SharedPreferences.Editor myEditor;


    public Notification notification;
    public NotificationCompat.Builder notificationBuilder;
    private String channelId = "default";
    public final int NOTIFICATION_ID = 1;						//☆生成されないので任意の番号を設定する	 The ID we use for the notification (the onscreen alert that appears at the notification area at the top of the screen as an icon -- and as text as well if the user expands the notification area).
    public PendingIntent prevPendingIntent = null;
    public PendingIntent pausePendingIntent = null;
    public PendingIntent nextPendingIntent = null;
    public PendingIntent quitPendingIntent = null;
    public PendingIntent repatPendingIntent = null;

    /**MusicServiceの開始
     * List<MediaItem>の初期化
     * */
    public static final int MS_START_SERVICE= 1001;
    public static final String ACTION_START_SERVICE= "ACTION_START_SERVICE";
    /** ListにMediaItemを呼び込む */
    public static final int MS_MAKE_LIST = MS_START_SERVICE + 1;
    public static final String ACTION_MAKE_LIST= "MAKE_LIST";
    /** 選曲された楽曲を読み込ませたプレイヤーを作製 */
    public static final String ACTION_SET_SONG= "SET_SONG";
    public static final int MS_SET_SONG = MS_MAKE_LIST + 1;
    /**再生状況変化*/
    public static final String ACTION_STATE_CHANGED = "com.example.android.remotecontrol.ACTION_STATE_CHANGED";
    public static final int MS_STATE_CHANGED = MS_SET_SONG + 1;
    /**再生停止トグル*/
    public static final String ACTION_PLAYPAUSE = "com.example.android.remotecontrol.ACTION_PLAYPAUSE";
    public static final int MS_PLAYPAUSE = MS_STATE_CHANGED + 1;
    /**Play*/
    public static final String ACTION_PLAY = "com.example.android.remotecontrol.ACTION_PLAY";
    public static final int MS_PLAY = MS_PLAYPAUSE + 1;
    /**PAUSE*/
    public static final String ACTION_PAUSE = "com.example.android.remotecontrol.ACTION_PAUSE";
    public static final int MS_PAUSE = MS_PLAY + 1;
    /**再生位置変更*/
    public static final String ACTION_SEEK = "ACTION_SEEK";
    public static final int MS_SEEK = MS_PAUSE + 1;
    /**FF*/
    public static final String ACTION_SKIP = "com.example.android.remotecontrol.ACTION_SKIP";
    public static final int MS_SKIP = MS_SEEK + 1;
    /**Rew*/
    public static final String ACTION_REWIND = "com.example.android.remotecontrol.ACTION_REWIND";
    public static final int MS_REWIND = MS_SKIP + 1;
    /**REPEAT**/
    public static final String ACTION_REPEAT_MODE = "REPEAT_MODE";
    public static final int MS_REPEAT_MODE = MS_REWIND + 1;
    /**Quit**/
    public static final int MS_QUIT = MS_REPEAT_MODE + 1;
    public static final String ACTION_QUIT = "QUIT";

    public static final String ACTION_BLUETOOTH_INFO= "com.hijiyam_koubou.action.BLUETOOTH_INFO";
    //public static final String ACTION_BLUETOOTH_INFO= "com.hijiyam_koubou.intent.action.BLUETOOTH_INFO";
    public static final String ACTION_STOP = "com.example.android.remotecontrol.ACTION_STOP";
    public static final String ACTION_REQUEST_STATE = "com.example.android.remotecontrol.ACTION_REQUEST_STATE";
    public static final String ACTION_LISTSEL = "LISTSEL";					//追加3	；リストで選択された曲の処理
    public static final String ACTION_SYUURYOU = "SYUURYOU";					//追加１	；
    public static final String ACTION_SYUURYOU_NOTIF = "SYUURYOU_NOTIF";					//追加3	；
    public static final String ACTION_ACT_CLOSE = "ACT_CLOSE";					//追加4	；
    public static final String ACTION_KEIZOKU = "KEIZOKU";					//追加2	；
    public static final String ACTION_REQUEST = "REQUEST";					//次はリクエスト開始
    public static final String ACTION_PLAY_READ = "PLAY_READ";
    public static final String ACTION_EQUALIZER = "EQUALIZER";
    public static final String ACTION_BASS_BOOST = "BASS_BOOST";
    public static final String ACTION_REVERB = "REVERB";
    public static final String ACTION_DATA_OKURI = "DATA_OKURI";			//データ送りのみ
    public static final String ACTION_UKETORI = "UKETORI";					//データ受け取りのみ
    enum State {	// indicates the state our service:
        Retrieving,	// the MediaRetriever is retrieving music
        Stopped,	// media player is stopped and not prepared to play
        Preparing,	// media player is preparing...
        Playing, 	// playback active (media player ready!). (but the media player may actually be paused in this state if we don't have audio focus. But we stay in this state so that we know we have to resume playback once we get focus back) playback paused (media player ready!)
        Paused		// playback paused (media player ready!)
    }   //public static final String ACTION_ITEMSET = "ITEMSET";						//追加２	；dataReflesh()でアルバム一枚分のタイトル読み込み
    public SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    public SimpleDateFormat sdffiles = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//    public MusicService(Context callContext) {
//        final String TAG = "MusicService";
//        String dbMsg="";
//        try{
//            ML =callContext.getClass();
//            myLog(TAG,dbMsg);
//        } catch (Exception e) {
//            myErrorLog(TAG,dbMsg+"で"+e);
//        }
//    }

    //設定変更反映//////////////////////////////////////////////////////////////////////////////////////////////////////////////起動項目////

    public static boolean setPrefbool(String keyNmae , boolean wrightVal , Context context) {        //プリファレンスの読込み
        boolean retBool = false;
        final String TAG = "setPrefbool";
        String dbMsg="[MusicService]keyNmae=" + keyNmae;
        MyUtil MyUtil = new MyUtil();
        retBool = MyUtil.setPrefBool(keyNmae , wrightVal,context);
        return retBool;
    }

    public static boolean setPrefInt(String keyNmae , int wrightVal , Context context) {        //プリファレンスの読込み
        boolean retBool = false;
        final String TAG = "setPrefInt";
        String dbMsg="[MusicService]keyNmae=" + keyNmae;
        MyUtil MyUtil = new MyUtil();
        retBool = MyUtil.setPrefInt(keyNmae , wrightVal,context);
        return retBool;
    }

    public static boolean setPrefStr(String keyNmae , String wrightVal , Context context) {        //プリファレンスの読込み
        boolean retBool = false;
        final String TAG = "setPrefStr";
        String dbMsg="[MusicService]keyNmae=" + keyNmae;
        MyUtil MyUtil = new MyUtil();
        retBool = MyUtil.setPreStr(keyNmae , wrightVal,context);
        return retBool;
    }

    /**プリファレンスの読込み*/
    public void readPref() {
        final String TAG = "readPref";
        String dbMsg = "";
        try {
            myPreferences.readPref();
            dbMsg += "MyPreferencesy読込み";
////			myPreferences.sharedPref = myPreferences.sharedPref;
////			myEditor =myPreferences.myEditor;
//
//            dbMsg += "、このアプリのバージョンコード=" + myPreferences.pref_sonota_vercord;
//            dbMsg += "、クロスフェード時間=" + myPreferences.pref_gyapless;
//            dbMsg += "、シンプルなリスト表示=" + myPreferences.pref_list_simple + "、プレイヤーの背景:黒=" + myPreferences.pref_pb_bgc;

            dbMsg += "、再生中のプレイリスト[" + myPreferences.nowList_id;
            dbMsg += "]" + myPreferences.nowList;
            dbMsg += "の" + myPreferences.pref_mIndex +"曲目";
//            sousalistID=Integer.parseInt(myPreferences.nowList_id);
            dbMsg += "、再生中のファイル名=" + myPreferences.saisei_fname;
//            pl_file_name = myPreferences.pref_commmn_music + File.separator;//汎用プレイリストのファイル名のパスまで
            dbMsg += "、汎用プレイリストのファイル名=" + myPreferences.pref_commmn_music + File.separator;
            //            pref_zenkai_saiseKyoku = Integer.parseInt(myPreferences.pref_zenkai_saiseKyoku);			//前回の連続再生曲数
//            pref_zenkai_saiseijikann =Integer.parseInt(myPreferences.pref_zenkai_saiseijikann);			//前回の連続再生時間
            dbMsg += "、前回=" + myPreferences.pref_zenkai_saiseKyoku + "曲、" + myPreferences.pref_zenkai_saiseijikann + "時間";
            dbMsg += "、最近追加リストのデフォルト枚数=" + myPreferences.pref_saikin_tuika;
            dbMsg += "、最近再生加リストのデフォルト枚数=" + myPreferences.pref_saikin_sisei;
            dbMsg += "、ランダム再生リストアップ曲数=" + myPreferences.pref_rundam_list_size;
//            repeatType = myPreferences.repeatType;							//リピート再生の種類
//			rp_pp = myPreferences.rp_pp;							//2点間リピート中
            //		String pref_data_url =myPreferences.saisei_fname;				//

            dbMsg += "、共通音楽フォルダ=" + myPreferences.pref_commmn_music;
            dbMsg += "、内蔵メモリ=" + myPreferences.pref_file_in;
            dbMsg += "、メモリーカード=" + myPreferences.pref_file_ex;
            dbMsg += "、設定保存フォルダ=" + myPreferences.pref_file_wr;
//            pref_file_kyoku= Integer.parseInt(myPreferences.pref_file_kyoku );		//総曲数
            dbMsg += "、総曲数=" + myPreferences.pref_file_kyoku;
            dbMsg += "、設定保存フォルダ=" + myPreferences.pref_file_wr;
//			dbMsg += "、総アルバム数=" + myPreferences.pref_file_album);
//			pref_file_album= Integer.parseInt(myPreferences.pref_file_album);		//
            dbMsg += "、記録している最新更新日=" + myPreferences.pref_file_saisinn;
            if(!myPreferences.pref_file_saisinn.equals("")){
                String mod = sdffiles.format(new Date(Long.valueOf(myPreferences.pref_file_saisinn) * 1000));
                dbMsg += ">>" + mod;
            }
            dbMsg += "、全曲リスト=" + myPreferences.pref_zenkyoku_list_id;
            dbMsg += "、最近追加=" + myPreferences.saikintuika_list_id;
            dbMsg += "、最近再生=" + myPreferences.saikinsisei_list_id;

            myLog(TAG, dbMsg);
    //        setteriYomikomi();            //状況に応じた分岐を行う
        } catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
    }																	//設定読込・旧バージョン設定の消去


    private static final int[] REQUEST_CODE = { 0, 1, 2 };
    /////////////////////プレイヤーの状態をクライアントに通知する//
    ///通知の作成////////////////////
    //通知を作成、サービスをForegroundにする
    /// https://developer.android.com/guide/topics/media-apps/audio-app/building-a-mediabrowserservice?hl=ja
    private void redrowNotification(MediaItem mediaItem) {
        final String TAG = "redrowNotification";
        String dbMsg="";
        try{
            dbMsg += "[" + exoPlayer.getCurrentMediaItemIndex()  + "/" + mediaItemList.size() + "]";
            dbMsg += ",artist=" + artistName;
            dbMsg += ",albumTitle=" + albumName;
            dbMsg += ",title=" + songTitol;
       //     notificationBuilder.setStyle(mediaStyle);
            notificationBuilder .setContentTitle(songTitol);
            notificationBuilder.setContentText(artistName+" - "+ albumName);
            notificationBuilder.setLargeIcon(albumArtBitmap);
     //       notificationBuilder.setSound(null);
            notificationBuilder.setSilent(true);
            notificationBuilder.build();
            startForeground(NOTIFICATION_ID, notification);
            myLog(TAG,dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG,dbMsg+"で"+e);
        }
    }

    /**リストのインデックスで指定した曲を指定した再生ポジションに切り替える**/
    private void songChange(int sIndex,long playPosition) {
        final String TAG = "songChange";
        String dbMsg="";
        try {
            dbMsg += ",mIndex=" + mIndex;
            dbMsg += "(CurrentMediaItemIndex=" + exoPlayer.getCurrentMediaItemIndex() +")";
            dbMsg += ">>" + sIndex;
            dbMsg += ",ContentPosition=" + exoPlayer.getContentPosition();
            dbMsg += ">>" + playPosition;
//            boolean nowPlaying = exoPlayer.isPlaying();
//            dbMsg += ",nowPlaying=" + nowPlaying;
//            if(nowPlaying){
//                exoPlayer.pause();
//            }
            exoPlayer.seekTo(sIndex, playPosition); //特定のアイテムの特定の位置から開始
//            if(nowPlaying){
//                exoPlayer.play();
//            }
       //  曲を変えれば onMediaItemTransition が発生するので pause/play　切り替えや　sendSongInfo(sIndex);は不要
            dbMsg += "(CurrentMediaItemIndex=" + exoPlayer.getCurrentMediaItemIndex() +")";
            mIndex= sIndex;
            myLog(TAG,dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
    }

    /**指定されたプレイリストの楽曲を内部配列に読み込む*/
    @SuppressLint("Range")
    public List<MediaItem> add2List(int playlistId ,String dataStr,List<MediaItem> targetMediaItemList){
        final String TAG = "add2List";
        String dbMsg= "";
        try{
            dbMsg += "、現在"+targetMediaItemList.size() + "件＞＞";
            dbMsg += "選択されたプレイリスト[ID="+playlistId + "]の" + dataStr;
            final Uri cUri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
            String[] columns = null;        //{ MediaStore.Audio.Playlists.Members.DATA };
            String c_selection = MediaStore.Audio.Playlists.Members.DATA +" = ? ";
            String[] c_selectionArgs = {dataStr};        //⑥引数groupByには、groupBy句を指定します。
            String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
            Cursor playLists= this.getContentResolver().query(cUri, columns, c_selection, c_selectionArgs, c_orderBy );
            dbMsg += ",該当" + playLists.getCount() +"件、";
            if(playLists.moveToFirst()){
                do{
                    String uriStr =null;    // (String) objMap.get(MediaStore.Audio.Playlists.Members.DATA);
                    String audioId = null;
                    CharSequence albumArtistStr = null;
                    CharSequence albumTitleStr = null;
                    CharSequence titleStr = null;
                    CharSequence artistStr = null;
                    CharSequence genreStr = null;
                    String duranationStr = null;
                    String trackStr = null;
                    objMap = new HashMap<String, Object>();
                    for(int i = 0 ; i < playLists.getColumnCount() ; i++ ){				//MuList.this.koumoku
                        String cName = playLists.getColumnName(i);
                        String cVal = null;
                        dbMsg += "[" + i +"/" + playLists.getColumnCount() +"項目]"+ cName;
                        if(
//					cName.equals(MediaStore.Audio.Playlists.Members.INSTANCE_ID) ||	//[1/66]instance_id
//					cName.equals(MediaStore.Audio.Playlists.Members.TITLE_KEY) ||	//[13/37]title_key
//					cName.equals(MediaStore.Audio.Playlists.Members.SIZE) ||			//[7/37]_size=4071748
//					cName.equals(MediaStore.Audio.Playlists.Members.IS_RINGTONE) ||	//[20/37]is_ringtone=0
//					cName.equals(MediaStore.Audio.Playlists.Members.IS_MUSIC) ||			//[21/37]is_music=1
//					cName.equals(MediaStore.Audio.Playlists.Members.IS_ALARM) ||			//[22/37]is_alarm=0
//					cName.equals(MediaStore.Audio.Playlists.Members.IS_NOTIFICATION) ||	//[23/37]is_notification=0
//					cName.equals(MediaStore.Audio.Playlists.Members.IS_PODCAST) ||			//[24/37]is_podcast=
//				 	cName.equals(MediaStore.Audio.Playlists.Members.ARTIST_KEY) ||		//
//					cName.equals(MediaStore.Audio.Playlists.Members.ALBUM_KEY) ||		//[35/37]album_key
                                cName.equals(MediaStore.Audio.Playlists.Members.XMP)){		//[31/66項目]xmp=【Blob】[32/37]artist_key
                       //     dbMsg += "は読み込めない";
                        }else{
                            if( cName.equals(MediaStore.Audio.Playlists.Members.AUDIO_ID)){
                                audioId = playLists.getString(i);
                                cVal = playLists.getString(i);
                            }else if( cName.equals(MediaStore.Audio.Playlists.Members.DATA)){	//[5/37]_data=/storage/sdcard0/external_sd/Music/Santana/All That I Am/05 Just Feel Better.wma
                                uriStr = playLists.getString(i);
                                cVal = playLists.getString(i);
                            }else if( cName.equals("album_artist")){		//[26/37]
                                albumArtistStr = playLists.getString(i);
                                cVal = playLists.getString(i);
                            }else if( cName.equals(MediaStore.Audio.Playlists.Members.ARTIST)){		//[33/37]artist=Santana
                                artistStr = playLists.getString(i);
                                cVal = playLists.getString(i);
                            }else if( cName.equals(MediaStore.Audio.Playlists.Members.ALBUM)){		//[33/37]artist=Santana
                                cVal = playLists.getString(i);
                                albumTitleStr = playLists.getString(i);
                            }else if( cName.equals(MediaStore.Audio.Playlists.Members.TITLE)){		//[12/37]title=Just Feel Better
                                cVal = playLists.getString(i);
                                titleStr = playLists.getString(i);
                            }else if( cName.equals(MediaStore.Audio.Playlists.Members.DURATION)){	//[14/37]duration=252799>>04:12 799
                                cVal = playLists.getString(i);
                                duranationStr = ",duranation=" + ORGUT.sdf_mss.format(Long.valueOf(cVal)) + "[s]";
                                dbMsg +=  ">>"+duranationStr;
                            }else if( cName.equals(MediaStore.Audio.Playlists.Members.TRACK)){
                                cVal = playLists.getString(i);
                                trackStr = playLists.getString(i);
                            }else if( cName.equals(MediaStore.Audio.Playlists.Members.ALBUM_ID)){
                                cVal = playLists.getString(i);
                                String albunIdStr = String.valueOf(playLists.getInt(i));
                            }else{
                                int cPosition = playLists.getColumnIndex(cName);
                              //  dbMsg += "『" + cPosition+"』";
                                if(0<cPosition){
                                    int colType = playLists.getType(cPosition);
                                    //		dbMsg += ",Type=" + colType + ",";
                                    switch (colType){
                                        case Cursor.FIELD_TYPE_NULL:          //0
                                            cVal ="【null】" ;
                                            break;
                                        case Cursor.FIELD_TYPE_INTEGER:         //1
                                            @SuppressLint("Range") int cInt = playLists.getInt(cPosition);
//                                                dbMsg += cInt+"【int】";
                                            cVal=String.valueOf(cInt);
                                            break;
                                        case Cursor.FIELD_TYPE_FLOAT:         //2
                                            @SuppressLint("Range") float cFlo = playLists.getFloat(cPosition);
                                            dbMsg += cFlo+"【float】";
                                            cVal=String.valueOf(cFlo);
                                            break;
                                        case Cursor.FIELD_TYPE_STRING:          //3
                                            cVal = playLists.getString(cPosition);
//                                                dbMsg +=  cVal+"【String】";
                                            break;
                                        case Cursor.FIELD_TYPE_BLOB:         //4
                                            //@SuppressLint("Range") String cBlob = String.valueOf(cursor.getBlob(cPosition));
                                            cVal ="【Blob】";
                                            break;
                                        default:
                                            cVal = String.valueOf(playLists.getString(cPosition));
//                                                dbMsg +=  cVal;
                                            break;
                                    }
                                }
                            }
                            dbMsg += "="+cVal;
                            objMap.put(cName ,cVal );
                        }
                    }
                    plAL.add( objMap);
                    dbMsg +=  ",\nuriStr="+ uriStr;
                    if(uriStr != null){
                        String imageUriStr = null;
                        Uri imageUri = null;
                        if( artistStr != null && albumTitleStr != null){
          //                  if(! artistStr.equals(getResources().getString(R.string.bt_unknown)) && !albumTitleStr.equals(getResources().getString(R.string.bt_unknown))){
                             imageUriStr = ORGUT.retAlbumArtUri(getApplicationContext(), (String) artistStr, (String) albumTitleStr);
//                            dbMsg +=  ",imageUriStr="+ imageUriStr;
//                            if(imageUriStr != null){
//                                imageUri = Uri.parse(imageUriStr);
//                            }
                        }
                        MediaMetadata metadata = new MediaMetadata.Builder()
                                .setAlbumTitle(albumTitleStr)
                                .setTitle(titleStr)
                                .setArtist(artistStr)
                                .setGenre(genreStr)
                                //		.setIsBrowsable(true)					//isBrowsable
                                .setIsPlayable(true)
                                .setArtworkUri(imageUri)
                                // 	.setMediaType(mediaType)			//int objMap.get(MediaStore.Audio.Playlists.Members.MIME_TYPE) ではない
                                .setAlbumArtist(albumArtistStr)
                                .build();
                        Uri rUri = Uri.parse(uriStr);
                        MediaItem mItem = new MediaItem.Builder()
                                .setMediaId((String) audioId)
                                //		.setSubtitleConfigurations(subtitleConfigurations)
                                .setMediaMetadata(metadata)
                                .setUri(rUri)
                                .build();
                        targetMediaItemList.add(mItem);
                        dbMsg += "\n取得["+ targetMediaItemList.size() + "件]";
                        dbMsg +=  ",albumArtist="+ mItem.mediaMetadata.albumArtist;
                        dbMsg +=  ",artist="+ mItem.mediaMetadata.artist;
                        dbMsg +=  ",albumTitle="+ mItem.mediaMetadata.albumTitle;
                        dbMsg +=  "["+ mItem.mediaMetadata.trackNumber + "]";
                        dbMsg +=  ",title="+ albumTitleStr;         //mItem.mediaMetadata.title;
            //            dbMsg +=  ",artworkUri="+ mItem.mediaMetadata.artworkUri;
                        dbMsg +=  ",genre="+ mItem.mediaMetadata.genre;
                    }
                }while( playLists.moveToNext() ) ;				//pdCoundtVal <  pdMaxVal
            }
            playLists.close();

//            }
            dbMsg += "、"+ targetMediaItemList.size() + "件";
            myLog(TAG, dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return targetMediaItemList;
    }

    /**全曲からアルバム内の一曲をMediaItemのリストに加える*/
    @SuppressLint("Range")
    public List<MediaItem> add2TitolList(int playlistId ,String dataStr,List<MediaItem> targetMediaItemList){
        final String TAG = "add2TitolList";
        String dbMsg= "";
        try{
            dbMsg += "選択されたプレイリスト[ID="+playlistId + "]の" + dataStr;
            dbMsg += "現在のプレイリスト[ID="+playlistId + "]";
            Uri cUri;
            if ( Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT) {
                cUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            } else {
                cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }
            dbMsg += ",cUri=" + cUri.toString();            String[] columns = null;        //{ MediaStore.Audio.Playlists.Members.DATA };
            String c_selection = MediaStore.Audio.Media.DATA +" = ? ";
            String[] c_selectionArgs = {dataStr};        //⑥引数groupByには、groupBy句を指定します。
            String c_orderBy = MediaStore.Audio.Media.TRACK;
            Cursor playLists= this.getContentResolver().query(cUri, columns, c_selection, c_selectionArgs, c_orderBy );
            dbMsg += "," + playLists.getCount() +"件";
            if(playLists.moveToFirst()){
                do{
                    String uriStr =null;    // (String) objMap.get(MediaStore.Audio.Playlists.Members.DATA);
                    String audioId = null;
                    CharSequence albumArtistStr = null;
                    CharSequence albumTitleStr = null;
                    CharSequence titleStr = null;
                    CharSequence artistStr = null;
                    CharSequence genreStr = null;
                    String duranationStr = null;
                    String trackStr = null;
                    objMap = new HashMap<String, Object>();
                    for(int i = 0 ; i < playLists.getColumnCount() ; i++ ){				//MuList.this.koumoku
                        String cName = playLists.getColumnName(i);
                        String cVal = null;
                        dbMsg += "[" + i +"/" + playLists.getColumnCount() +"項目]"+ cName;
                        if(
//					cName.equals(MediaStore.Audio.Playlists.Members.INSTANCE_ID) ||	//[1/66]instance_id
//					cName.equals(MediaStore.Audio.Playlists.Members.TITLE_KEY) ||	//[13/37]title_key
//					cName.equals(MediaStore.Audio.Playlists.Members.SIZE) ||			//[7/37]_size=4071748
//					cName.equals(MediaStore.Audio.Playlists.Members.IS_RINGTONE) ||	//[20/37]is_ringtone=0
//					cName.equals(MediaStore.Audio.Playlists.Members.IS_MUSIC) ||			//[21/37]is_music=1
//					cName.equals(MediaStore.Audio.Playlists.Members.IS_ALARM) ||			//[22/37]is_alarm=0
//					cName.equals(MediaStore.Audio.Playlists.Members.IS_NOTIFICATION) ||	//[23/37]is_notification=0
//					cName.equals(MediaStore.Audio.Playlists.Members.IS_PODCAST) ||			//[24/37]is_podcast=
//				 	cName.equals(MediaStore.Audio.Playlists.Members.ARTIST_KEY) ||		//
//					cName.equals(MediaStore.Audio.Playlists.Members.ALBUM_KEY) ||		//[35/37]album_key
                                cName.equals(MediaStore.Audio.Playlists.Members.XMP)){		//[31/66項目]xmp=【Blob】[32/37]artist_key
                            //     dbMsg += "は読み込めない";
                        }else{
                            if( cName.equals(MediaStore.Audio.Media._ID)){
                                audioId = playLists.getString(i);
                                cVal = playLists.getString(i);
                            }else if( cName.equals(MediaStore.Audio.Media.DATA)){	//[5/37]_data=/storage/sdcard0/external_sd/Music/Santana/All That I Am/05 Just Feel Better.wma
                                uriStr = playLists.getString(i);
                                cVal = playLists.getString(i);
                            }else if( cName.equals("album_artist")){		//[26/37]
                                albumArtistStr = playLists.getString(i);
                                cVal = playLists.getString(i);
                            }else if( cName.equals(MediaStore.Audio.Media.ARTIST)){		//[33/37]artist=Santana
                                artistStr = playLists.getString(i);
                                cVal = playLists.getString(i);
                            }else if( cName.equals(MediaStore.Audio.Media.ALBUM)){		//[33/37]artist=Santana
                                cVal = playLists.getString(i);
                                albumTitleStr = playLists.getString(i);
                            }else if( cName.equals(MediaStore.Audio.Media.TITLE)){		//[12/37]title=Just Feel Better
                                cVal = playLists.getString(i);
                                titleStr = playLists.getString(i);
                            }else if( cName.equals(MediaStore.Audio.Media.DURATION)){	//[14/37]duration=252799>>04:12 799
                                cVal = playLists.getString(i);
                                duranationStr = ",duranation=" + ORGUT.sdf_mss.format(Long.valueOf(cVal)) + "[s]";
                                dbMsg +=  ">>"+duranationStr;
                            }else if( cName.equals(MediaStore.Audio.Media.TRACK)){
                                cVal = playLists.getString(i);
                                trackStr = playLists.getString(i);
                            }else if( cName.equals(MediaStore.Audio.Media.ALBUM_ID)){
                                cVal = playLists.getString(i);
                                String albunIdStr = String.valueOf(playLists.getInt(i));
                            }else{
                                int cPosition = playLists.getColumnIndex(cName);
                                //  dbMsg += "『" + cPosition+"』";
                                if(0<cPosition){
                                    int colType = playLists.getType(cPosition);
                                    //		dbMsg += ",Type=" + colType + ",";
                                    switch (colType){
                                        case Cursor.FIELD_TYPE_NULL:          //0
                                            cVal ="【null】" ;
                                            break;
                                        case Cursor.FIELD_TYPE_INTEGER:         //1
                                            @SuppressLint("Range") int cInt = playLists.getInt(cPosition);
//                                                dbMsg += cInt+"【int】";
                                            cVal=String.valueOf(cInt);
                                            break;
                                        case Cursor.FIELD_TYPE_FLOAT:         //2
                                            @SuppressLint("Range") float cFlo = playLists.getFloat(cPosition);
                                            dbMsg += cFlo+"【float】";
                                            cVal=String.valueOf(cFlo);
                                            break;
                                        case Cursor.FIELD_TYPE_STRING:          //3
                                            cVal = playLists.getString(cPosition);
//                                                dbMsg +=  cVal+"【String】";
                                            break;
                                        case Cursor.FIELD_TYPE_BLOB:         //4
                                            //@SuppressLint("Range") String cBlob = String.valueOf(cursor.getBlob(cPosition));
                                            cVal ="【Blob】";
                                            break;
                                        default:
                                            cVal = String.valueOf(playLists.getString(cPosition));
//                                                dbMsg +=  cVal;
                                            break;
                                    }
                                }
//                                    dbMsg += "="+cVal;
                            }
                            objMap.put(cName ,cVal );
                        }
                    }
                    plAL.add( objMap);
                    dbMsg +=  ",\nuriStr="+ uriStr;
                    if(uriStr != null){
                        String imageUriStr = null;
                        Uri imageUri = null;
                        if( artistStr != null && albumTitleStr != null){
                            //                  if(! artistStr.equals(getResources().getString(R.string.bt_unknown)) && !albumTitleStr.equals(getResources().getString(R.string.bt_unknown))){
                            imageUriStr = ORGUT.retAlbumArtUri(getApplicationContext(), (String) artistStr, (String) albumTitleStr);
                            dbMsg +=  ",imageUriStr="+ imageUriStr;
                            if(imageUriStr != null){
                                imageUri = Uri.parse(imageUriStr);
                            }
                        }
                        MediaMetadata metadata = new MediaMetadata.Builder()
                                .setAlbumTitle(albumTitleStr)
                                .setTitle(titleStr)
                                .setArtist(artistStr)
                                .setGenre(genreStr)
                                //		.setIsBrowsable(true)					//isBrowsable
                                .setIsPlayable(true)
                                .setArtworkUri(imageUri)
                                // 	.setMediaType(mediaType)			//int objMap.get(MediaStore.Audio.Playlists.Members.MIME_TYPE) ではない
                                .setAlbumArtist(albumArtistStr)
                                .build();
                        Uri rUri = Uri.parse(uriStr);
                        MediaItem mItem = new MediaItem.Builder()
                                .setMediaId((String) audioId)
                                //		.setSubtitleConfigurations(subtitleConfigurations)
                                .setMediaMetadata(metadata)
                                .setUri(rUri)
                                .build();
                        targetMediaItemList.add(mItem);
                        dbMsg += "\n取得["+ targetMediaItemList.size() + "]";
                        dbMsg +=  ",albumArtist="+ mItem.mediaMetadata.albumArtist;
                        dbMsg +=  ",artist="+ mItem.mediaMetadata.artist;
                        dbMsg +=  ",albumTitle="+ mItem.mediaMetadata.albumTitle;
                        dbMsg +=  "["+ mItem.mediaMetadata.trackNumber + "]";
                        dbMsg +=  ",title="+ albumTitleStr;         //mItem.mediaMetadata.title;
                        //            dbMsg +=  ",artworkUri="+ mItem.mediaMetadata.artworkUri;
                        dbMsg +=  ",genre="+ mItem.mediaMetadata.genre;
                    }
                }while( playLists.moveToNext() ) ;				//pdCoundtVal <  pdMaxVal
            }
            playLists.close();

//            }
            dbMsg += "、"+ targetMediaItemList.size() + "件";
            myLog(TAG, dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return targetMediaItemList;
    }

    /**
     * コンポーネントにサービスの開始を許可する**/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String TAG = "onStartCommand";
        String dbMsg="";
        try{
            dbMsg +="、flags=" + flags;
            dbMsg +="、startId=" + startId;
            Class<?> callClass = intent.getClass();
            dbMsg +="、callClass=" + callClass.getName();
            String action = intent.getAction();                    //ボタンなどで指定されたアクション
            if(exoPlayer != null){
                nowPlay = exoPlayer.isPlaying();
            }else{
                nowPlay = false;
            }
            dbMsg +="、nowPlay=" + nowPlay;
            dbMsg +=",action=" + action;
            dbMsg += ",現在[" + currentListId + "]" + currentListName;
            if(action.equals(ACTION_START_SERVICE)) {
                dbMsg += "、MusicServiceの開始,List<MediaItem>の初期化";
                String nowList_id = intent.getStringExtra("nowList_id");
                dbMsg += ",渡されたのは[ " + nowList_id+ "] ";
                String setListName = intent.getStringExtra("nowList");
                dbMsg += setListName;
                if(currentListName.equals("")){
                    currentListId = nowList_id;
                    currentListName = setListName;
                }
                if(mediaItemList == null){
                    dbMsg += ",プライマリーmediaItemList作成 ";
                    mediaItemList = new ArrayList<MediaItem>();
                    plAL = new ArrayList<Map<String, Object>>();
                    plAL.clear();
                }else{              // if(nowPlay && mediaItemList != null)
                    dbMsg += ",プライマリー" + mediaItemList.size() + "件";
                    dbMsg += ",予備リスト： mediaItemList2へ";
                    mediaItemList2 = new ArrayList<MediaItem>();
                    plAL2 = new ArrayList<Map<String, Object>>();
                    plAL2.clear();
                }
            }else if(action.equals(ACTION_MAKE_LIST)){
                dbMsg +="、ListにMediaItemを呼び込む";
                String nowList_id = intent.getStringExtra("nowList_id");
                int playlistId = Integer.parseInt(nowList_id);
                String setListName = intent.getStringExtra("nowList");
                String uriStr = intent.getStringExtra("uriStr");
                dbMsg +="[" + playlistId + "]" + setListName + "の" + uriStr;
                String setAlbumName = intent.getStringExtra("nowAlbum");
                boolean isListChange = false;
                if(0<mediaItemList.size()) {
                    if(setListName.equals(getResources().getString(R.string.listmei_zemkyoku))){
                        dbMsg += ",setAlbumName=" + setAlbumName;
                        if(! setAlbumName.equals(currentAlbumName)){
                            isListChange = true;
                        }
                    }else{
                        if(! currentListName.equals(setListName)){
                            isListChange = true;
                        }
                    }
                }
                dbMsg += ",isListChange=" + isListChange;
                if(isListChange){
                    dbMsg += ",予備リスト ";
                    if(! setListName.equals(getResources().getString(R.string.listmei_zemkyoku))){
                        mediaItemList2 = add2List( playlistId ,uriStr,mediaItemList2);
                    }else{
                        mediaItemList2 = add2TitolList( playlistId ,uriStr,mediaItemList2);
                    }
                    dbMsg +=">>" + mediaItemList2.size() + "件:名称リスト" + plAL2.size() + "件";
                }else{
                    dbMsg +=",プライマリー" ;
                    if(! setListName.equals(getResources().getString(R.string.listmei_zemkyoku))){
                        mediaItemList = add2List( playlistId ,uriStr,mediaItemList);
                    }else{
                        mediaItemList = add2TitolList( playlistId ,uriStr,mediaItemList);
                    }
                    dbMsg +=">>" + mediaItemList.size() + "件"+ "件:名称リスト" + plAL.size() + "件";
                }
            }else if(action.equals(ACTION_SET_SONG)){
                dbMsg +="、選曲された楽曲を読み込ませたプレイヤーを作製";
                String setListId = intent.getStringExtra("nowList_id");
                String setListName = intent.getStringExtra("nowList");
                dbMsg += ",渡されたのは[" + setListId + "]" + setListName;
                String setAlbumName = intent.getStringExtra("nowAlbum");
                boolean isListChange = false;
                if(setListName.equals(getResources().getString(R.string.listmei_zemkyoku))){
                    dbMsg += ",setAlbumName=" + setAlbumName;
                    if(! setAlbumName.equals(currentAlbumName)){
                        isListChange = true;
                        currentAlbumName = setAlbumName;
                    }
                }else{
                    if(! currentListName.equals(setListName)){
                        isListChange = true;
                    }
                }
                dbMsg += ",isListChange=" + isListChange;
                if(isListChange){
                    dbMsg += ">>プレイリスト変更";
                    currentListId = setListId;
                    currentListName = setListName;
                    if(mediaItemList2 != null){
                        dbMsg += ">>予備リストへ（" + mediaItemList.size() + "件";
                        mediaItemList = new ArrayList<MediaItem>();
                        plAL = new ArrayList<Map<String, Object>>();
                        int lCount =0;
                        for(lCount =0;lCount<mediaItemList2.size();lCount++){
                            MediaItem mItem = mediaItemList2.get(lCount);
                            mediaItemList.add(mItem);
                            Map<String, Object> pItem = plAL2.get(lCount);
                            plAL.add(pItem);
                        }
                        dbMsg += ">>" + mediaItemList.size() + "件）";
                        dbMsg += "、plAL（" + plAL.size() + "件）";
                        mediaItemList2 = null;
                        plAL2=null;
                    }
                    dbMsg += ",exoPlayer=" + exoPlayer;
                    if(exoPlayer != null){
                        exoPlayer.pause();
                        exoPlayer.release();
                        exoPlayer = null;
                        dbMsg += ">>" + exoPlayer;
                    }
                    dbMsg += ",mediaSession=" + mediaSession;
                    if(mediaSession != null){
                        mediaSession.release();
                        mediaSession = null;
                        dbMsg += ">>" + mediaSession;
                    }
                }
                pref_data_url= intent.getStringExtra("pref_data_url");		//extras.getString("pref_data_url");
                dbMsg += "で " + pref_data_url;
                mIndex =intent.getIntExtra("mIndex", 0);
                dbMsg += "で[mIndex：" + mIndex + "/"+ mediaItemList.size() + "件]";
                saiseiJikan =intent.getLongExtra("saiseiJikan", 0L);
                dbMsg += ",saiseiJikan=" + saiseiJikan;
         //       plAL=intent.getParcelableExtra("plAL");
                dbMsg += ",plAL= " + plAL.size();
                if(exoPlayer == null){
                    initializePlayer(); // EXOPLAYER
                }else{
                    dbMsg += ",player生成済み";
                    MediaItem currentMediaItem = exoPlayer.getCurrentMediaItem();
                    String mediaId = currentMediaItem.mediaId;
                    dbMsg += ",現在:" + mediaId;
                    MediaItem sarchItem = mediaItemList.get(mIndex);
                    String sarchId = sarchItem.mediaId;
                    dbMsg += ",指定:" + sarchId;
                    if(! mediaId.equals(sarchId)){
                        songChange(mIndex, saiseiJikan);
                    }else{
                        dbMsg += ",同じ曲を指定指定:";
                    }
                }
            }else if(action.equals(ACTION_PLAYPAUSE)){
                dbMsg +="、プレイ/ポーズのトグル";
            }else if(action.equals(ACTION_PLAY)){
                dbMsg +="、プレイ";
                if(exoPlayer != null){
                    exoPlayer.play();
                    nowPlay = true;
                    sendStateChasng();
                }else{
                    dbMsg +="、exoPlayer== null";
                }
            }else if(action.equals(ACTION_PAUSE)){
                dbMsg +="、ポーズ";
                if(exoPlayer != null){
                    exoPlayer.pause();
                    nowPlay = false;
                    sendStateChasng();
                }else{
                    dbMsg +="、exoPlayer== null";
                }
            }else if(action.equals(ACTION_SEEK)){
                dbMsg +="、再生ポジション変更";
                dbMsg += ",saiseiJikan=" + saiseiJikan;
                if(exoPlayer != null){
                    saiseiJikan =intent.getLongExtra("seekProgress", 0L);
                    dbMsg += ">>" + saiseiJikan;
                    exoPlayer.seekTo(saiseiJikan);
                    sendStateChasng();
                }else{
                    dbMsg +="、exoPlayer== null";
                }
            }else if(action.equals(ACTION_SKIP)){
                dbMsg +="、送り";                  //onMediaItemTransition
                if(exoPlayer != null){
                    dbMsg +="[" + mIndex + " / " + mediaItemList.size() + "]";
              //      exoPlayer.seekToNextMediaItem();
                    mIndex++;
                    if(mediaItemList.size()<mIndex){
                        mIndex=0;
                    }
                    mIndex=exoPlayer.getCurrentMediaItemIndex();
                    dbMsg +=">>" + mIndex ;
                    songChange(mIndex, 0L);
                }else{
                    dbMsg +="、exoPlayer== null";
                }
            }else if(action.equals(ACTION_REWIND)){
                dbMsg +="、戻し";
                if(exoPlayer != null){
                    long nowPosiotion = exoPlayer.getContentPosition();
                    dbMsg +="、nowPosiotion= " + nowPosiotion;
                    mIndex=exoPlayer.getCurrentMediaItemIndex();
                    dbMsg +="[" + mIndex + " / " + mediaItemList.size() + "]";
                    if(1000 < nowPosiotion){

                    }else{
                        mIndex--;
                        if(mIndex<0){
                            mIndex=mediaItemList.size();
                        }
//                    exoPlayer.seekBack();
                    }
                    dbMsg +=">>" + mIndex ;
                    songChange(mIndex, 0L);
                }else{
                    dbMsg +="、exoPlayer== null";
                }
            }else if(action.equals(ACTION_REPEAT_MODE)){
                dbMsg +="、リピート、シャフル切り替え";
            }else if(action.equals(ACTION_QUIT)){
                dbMsg +="、終了";
            }
//            rContext = this.getApplicationContext();			//com.hijiyam_koubou.marasongs.MyApp
//            readPref();
//            if(sharedPref ==null){
//                MyConstants.PREFS_NAME = this.getResources().getString(R.string.pref_main_file);
//                dbMsg +="、PREFS_NAME=" + MyConstants.PREFS_NAME;
//                if (31 <= android.os.Build.VERSION.SDK_INT ) {
//                    sharedPref = getSharedPreferences(MyConstants.PREFS_NAME, MODE_PRIVATE);
//                    myEditor = sharedPref.edit();
//                }else{
//                    sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());			//	this.getSharedPreferences(this, MODE_PRIVATE);		//
//                    myEditor = sharedPref.edit();
//                }
//            }

//            onCompletNow = false;			//曲間処理中
//            action = intent.getAction();					//ボタンなどで指定されたアクション
//            nowAction =action;	//現在のアクション
//            dbMsg +=",action=" + action;
//            if( action == null ){
//                action = ACTION_LISTSEL;
//                dbMsg +=">>" + action;
//            }
//            int mcPosition = getPrefInt("mcPosition" , 0, MusicPlayerService.this);  //sharedPref.getInt("pref_position" , 0);
//            dbMsg +=" , mcPosition=" + mcPosition;
//            if( exoPlayer !=null ){
//                dbMsg +=" ,isPlaying=" + exoPlayer.isPlaying() ;/////////////////////////////////////
//                exoPlayer.seekTo(mcPosition);
//            }		//	Bundle extras = intent.getExtras();
////			if(mSession ==null){
////				dbMsg += " , mSession == null";
////				mSession = new MediaSessionCompat(getApplicationContext(),  getResources().getString(R.string.app_name));
////			}
//            } else if (action.equals(ACTION_PLAYPAUSE)) {
//                dbMsg +="でPLAY/PAUSE";
//                processTogglePlaybackRequest();
//            } else if (action.equals(ACTION_PLAY_READ)) {							//"readPlaying[MaraSonActivity]
//                //	dataUketori(intent);			//クライアントからデータを受け取りグローバル変数にセット
//                processPlayRequest();
//                if( exoPlayer !=null ){
//                    dbMsg +=" ,isPlaying=" + exoPlayer.isPlaying() ;/////////////////////////////////////
//                    exoPlayer.seekTo(mcPosition);
//                    if(! exoPlayer.isPlaying()){
//                        exoPlayer.setPlayWhenReady(true);
//                        mState = MusicPlayerService.State.Playing;
//                    }
//                }
////				sendPlayerState(exoPlayer);																		//②ⅲStop?//一曲分のデータ抽出して他のActvteyに渡す。
//                changeCount(exoPlayer);						//シークバーの初期更新：タイマーオブジェクトを使ったカウンタ更新を追加
//            } else if (action.equals(ACTION_PLAY)) {
//                mState = MusicPlayerService.State.Playing;
//                dbMsg +="から再生,";
//                processPlayRequest();																	//②ⅲPlay?StoppedならplayNextSong/PausedならconfigAndStartMediaPlayer
//            } else if (action.equals(ACTION_LISTSEL)) {					//	リストで選曲後に再生中だった場合
//////				dataUketori(intent);
////				processPlayRequest();
////				playNextSong(mIndex,true);
//            } else if (action.equals(ACTION_REQUEST_STATE)) {
//                dbMsg +="でリストから戻り,";
//                //起動時の表示；dataUketoriでクライアントからデータを受け取りグローバル変数にセット、sendPlayerStateで一曲分のデータ抽出して他のActvteyに渡す。
//                //		dataUketori(intent);			//クライアントからデータを受け取りグローバル変数にセット
//                processPlayRequest();
////				mState = State.Stopped;						//Stopped	プレイヤー生成のトリガーに使用？					Paused
//                if(! IsPlaying){
//                    dbMsg +="起動直後？";
////					new PrepareMusicRetrieverTask(this).execute(getApplicationContext());		// Create the retriever and start an asynchronous task that will prepare it.
//
//                }
//                sendPlayerState(exoPlayer);																		//②ⅲStop?//一曲分のデータ抽出して他のActvteyに渡す。
//            } else if (action.equals(ACTION_KEIZOKU)) {
//                dbMsg +="で終了準備,";
//                //			dataUketori(intent);			//クライアントからデータを受け取りグローバル変数にセット
//                processPlayRequest();
//                sendPlayerState(exoPlayer);
//            } else if (action.equals(ACTION_SYUURYOU)) {				//終了準備
//                dbMsg +="で終了処理,";
//                quitMe( startId );			//このサービスを閉じる
//            } else if (action.equals(ACTION_SYUURYOU_NOTIF)) {				//
//                dbMsg +="でノティフィケーションから終了,";
//                dbMsg +=" ,actClose=" + actClose ;/////////////////////////////////////
//                if(! actClose ){
//                    imanoJyoutai = MaraSonActivity.quit_all;
//                    sendPlayerState(exoPlayer);
//                }
//                dbMsg +=" ,startId=" + startId ;/////////////////////////////////////
//                quitMe( startId );			//このサービスを閉じる
//            } else if (action.equals(ACTION_ACT_CLOSE)) {				//アクティビティは閉じられている
//                dbMsg +=" ,actClose=" + actClose ;/////////////////////////////////////
//                actClose = true;
//                dbMsg +=">>" + actClose ;/////////////////////////////////////
//            } else if (action.equals(ACTION_REQUEST)) {				//次はリクエスト開始
////				Bundle extras = intent.getExtras();
////				tugiList_id = extras.getInt("tugiList_id");
////				dbMsg += "次に再生するリスト["+ tugiList_id ;
////				tugiList = extras.getString("tugiList");
////				dbMsg += "]"+ tugiList ;		//次に再生するリスト名;リクエストリスト
//                //			boolean requestSugu = false;
//                //			requestSugu = extras.getBoolean("requestSugu");
//                //			if(requestSugu){
//                //				myPreferences.nowList_id = tugiList_id;
//                //				myPreferences.nowList = tugiList;
//                //			}
//            } else if (action.equals(ACTION_EQUALIZER)) {
//                equalizerPartKousin( intent );					//Equalizerの部分更新
//            } else if (action.equals(ACTION_BASS_BOOST)) {
//                setupBassBoost(intent);			//ベースブーストOn/Off
//            } else if (action.equals(ACTION_REVERB)) {
//                dbMsg += ",リバーブ効果["+ reverbBangou + "]" + reverbMei;		//次に再生するリスト名;リクエストリスト
//                setupPresetReverb(intent);					//リバーブ設定
//            } else if (action.equals(ACTION_DATA_OKURI)) {				//データ送りのみ
//                sendPlayerState(exoPlayer);
//            } else if (action.equals(ACTION_UKETORI)) {				//データ受け取りのみ
////				dataUketori(intent);
//                processPlayRequest();
//            }
            myLog(TAG,dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG,dbMsg+"で"+e);
        }
        //	return START_REDELIVER_INTENT;				//	再起動前と同じ順番で再起動してくれる。Intentも渡ってきています。
        return START_NOT_STICKY;				//APIL5;サービスが強制終了した場合、サービスは再起動しない
        /*START_STICKY	サービスが強制終了した場合、サービスは再起動するonStartCommand()が再度呼び出され、Intentにnullが渡される
         */
    }

    /**バインドを許可する;現在未使用*/
    @Override
    public IBinder onBind(Intent intent) {
        final String TAG = "onBind";
        String dbMsg="";
        try{
            myLog(TAG,dbMsg);
            // TODO: Return the communication channel to the service.
            throw new UnsupportedOperationException("Not yet implemented");
        } catch (Exception e) {
            myErrorLog(TAG,dbMsg+"で"+e);
        }
        return null;				//サービスの実体を返します
    }

    //   @Override
    public MediaSession onGetSession(MediaSession.ControllerInfo info) {
        final String TAG = "onGetSession";
        String dbMsg="";
        try{
            dbMsg += ",info=" + info.toString();
            myLog(TAG,dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG,dbMsg+"で"+e);
        }
        return mediaSession;
    }

    /**
     * https://github.com/androidx/media/blob/release/demos/main/src/main/java/androidx/media3/demo/main/PlayerActivity.java
     * */
    protected PlayerView playerView;					//project.PlayerView
    private Tracks lastSeenTracks;
    //  private List<MediaItem> mediaItems = mediaItemList;

    public static final String ACTION_PLAY_PAUSE = "com.example.android.notification.action.PLAY_PAUSE";
    private boolean startAutoPlay;
    private int startItemIndex;
    private long startPosition;
    private DebugTextViewHelper debugViewHelper;
    private TrackSelectionParameters trackSelectionParameters;
    private DataSource.Factory dataSourceFactory;
    private ImaServerSideAdInsertionMediaSource.AdsLoader.@MonotonicNonNull State
            serverSideAdsLoaderState;
    @Nullable
    private AdsLoader clientSideAdsLoader;

    private void showToast(int messageId) {
        final String TAG = "showToast";
        String dbMsg="";
        try {
            showToast(getString(messageId));
        } catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
    }

    private void showToast(String message) {
        final String TAG = "showToast";
        String dbMsg="";
        try {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
    }

    @Nullable
    private ImaServerSideAdInsertionMediaSource.AdsLoader serverSideAdsLoader;

    private void showControls() {
        final String TAG = "showControls";
        String dbMsg="";
        try {

            //	debugRootView.setVisibility(View.VISIBLE);
            myLog(TAG,dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
    }

    private class PlayerEventListener implements Player.Listener {

        @Override
        public void onPlaybackStateChanged(@Player.State int playbackState) {
            final String TAG = "onPlaybackStateChanged";
            String dbMsg="[PlayerEventListener]";
            try {
                dbMsg += ",playbackState=" + playbackState;
                if (playbackState == Player.PLAYBACK_SUPPRESSION_REASON_NONE) {          //0
                    dbMsg += "=PLAYBACK_SUPPRESSION_REASON_NONE";
                }else if (playbackState == Player.STATE_IDLE) {          //1
                    dbMsg += "=STATE_IDLE";
                }else if (playbackState == Player.PLAYBACK_SUPPRESSION_REASON_TRANSIENT_AUDIO_FOCUS_LOSS) {          //1
                    dbMsg += "=PLAYBACK_SUPPRESSION_REASON_TRANSIENT_AUDIO_FOCUS_LOSS";
                }else if (playbackState == Player.STATE_BUFFERING) {          //2
                    dbMsg += "=STATE_BUFFERING";
                }else if (playbackState == Player.STATE_READY) {          //3
                    dbMsg += "=STATE_READY";
                }else if (playbackState == Player.STATE_ENDED) {          //4
                    dbMsg += "=STATE_ENDED";
                    showControls();
                }else if (playbackState == Player.EVENT_PLAY_WHEN_READY_CHANGED) {          //5
                    dbMsg += "=EVENT_PLAY_WHEN_READY_CHANGED";
                }else if (playbackState == Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM) {          //6
                    dbMsg += "=COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM";
                }else if (playbackState == Player.EVENT_PLAYBACK_SUPPRESSION_REASON_CHANGED) {          //6
                    dbMsg += "=EVENT_PLAYBACK_SUPPRESSION_REASON_CHANGED";
                }   //2:選曲時、3
                updateButtonVisibility();
                myLog(TAG,dbMsg);
            } catch (Exception e) {
                myErrorLog(TAG ,  dbMsg + "で" + e);
            }
        }


        @Override
        public void onPlayerError(PlaybackException error) {
            final String TAG = "onPlayerError";
            String dbMsg="[PlayerEventListener]";
            try {
                dbMsg += ",errorCode=" + error.errorCode;
                if (error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {           //1002
                    exoPlayer.seekToDefaultPosition();
                    exoPlayer.prepare();
                } else {
                    updateButtonVisibility();
                    showControls();
                }
                myLog(TAG,dbMsg);
            } catch (Exception e) {
                myErrorLog(TAG ,  dbMsg + "で" + e);
            }
        }

        @Override
        @SuppressWarnings("ReferenceEquality")
        public void onTracksChanged(Tracks tracks) {
            final String TAG = "onTracksChanged";
            String dbMsg="[PlayerEventListener]";
            try {
                updateButtonVisibility();
                if (tracks == lastSeenTracks) {
                    dbMsg += ",lastSeenTracks=" + lastSeenTracks;
                    myLog(TAG,dbMsg);
                    return;
                }
                dbMsg += ",TRACK_TYPE_VIDEO=" + tracks.containsType(C.TRACK_TYPE_VIDEO);
                if (tracks.containsType(C.TRACK_TYPE_VIDEO)
                        && !tracks.isTypeSupported(C.TRACK_TYPE_VIDEO, /* allowExceedsCapabilities= */ true)) {         //TRACK_TYPE_VIDEO:2
                    showToast("Media includes video tracks, but none are playable by this device");
                }
                dbMsg += ",TRACK_TYPE_AUDIO=" + tracks.containsType(C.TRACK_TYPE_AUDIO);
                if (tracks.containsType(C.TRACK_TYPE_AUDIO)
                        && !tracks.isTypeSupported(C.TRACK_TYPE_AUDIO, /* allowExceedsCapabilities= */ true)) {        //TRACK_TYPE_VIDEO:1
                }
                lastSeenTracks = tracks;
//                mIndex=exoPlayer.getCurrentMediaItemIndex();
//                int endIndex = exoPlayer.getMediaItemCount();
//                objMap=plAL.get(mIndex);
//                String dataFN = (String) objMap.get(MediaStore.Audio.Playlists.Members.DATA);
//                dbMsg += ",dataFN=" + dataFN;
//                String duranation = (String) objMap.get(MediaStore.Audio.Playlists.Members.DURATION);
//                dbMsg += ",duranation=" + duranation;
//                MediaItem mediaItem = mediaItemList.get(mIndex);
//                sendSongInfo(myPreferences.nowList_id,mIndex,dataFN,mediaItem,duranation);
                myLog(TAG,dbMsg);
            } catch (Exception e) {
                myErrorLog(TAG ,  dbMsg + "で" + e);
            }

        }
    }

    @OptIn(markerClass = UnstableApi.class)
    private void configurePlayerWithServerSideAdsLoader() {
        final String TAG = "configurePlayerWithServerSideAdsLoader";
        String dbMsg="";
        try {
            if(exoPlayer != null){
                serverSideAdsLoader.setPlayer(exoPlayer);
            }
            myLog(TAG,dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
    }

    private static List<MediaItem> createMediaItems(Intent intent, DownloadTracker downloadTracker) {
        final String TAG = "createMediaItems";
        String dbMsg="";
        try {

            List<MediaItem> mediaItems = new ArrayList<>();
            for (MediaItem item : IntentUtil.createMediaItemsFromIntent(intent)) {
                mediaItems.add(
                        maybeSetDownloadProperties(
                                item, downloadTracker.getDownloadRequest(item.localConfiguration.uri)));
            }
            return mediaItems;
        } catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return null;
    }

    @OptIn(markerClass = androidx.media3.common.util.UnstableApi.class)
    private static MediaItem maybeSetDownloadProperties(MediaItem item, @Nullable DownloadRequest downloadRequest) {
        final String TAG = "maybeSetDownloadProperties";
        String dbMsg="";
        try {

            if (downloadRequest == null) {
                return item;
            }
            MediaItem.Builder builder = item.buildUpon();
            builder
                    .setMediaId(downloadRequest.id)
                    .setUri(downloadRequest.uri)
                    .setCustomCacheKey(downloadRequest.customCacheKey)
                    .setMimeType(downloadRequest.mimeType)
                    .setStreamKeys(downloadRequest.streamKeys);
            @Nullable
            MediaItem.DrmConfiguration drmConfiguration = item.localConfiguration.drmConfiguration;
            if (drmConfiguration != null) {
                builder.setDrmConfiguration(
                        drmConfiguration.buildUpon().setKeySetId(downloadRequest.keySetId).build());
            }
            myLog(TAG,dbMsg);
            return builder.build();
        } catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return null;
    }


    private AdsLoader getClientSideAdsLoader(MediaItem.AdsConfiguration adsConfiguration) {
        final String TAG = "getClientSideAdsLoader";
        String dbMsg="";
        try {
            // The ads loader is reused for multiple playbacks, so that ad playback can resume.
            if (clientSideAdsLoader == null) {
                clientSideAdsLoader = new ImaAdsLoader.Builder(/* context= */ this).build();
            }
            clientSideAdsLoader.setPlayer(exoPlayer);
            return clientSideAdsLoader;
        } catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return null;
    }


    @OptIn(markerClass = UnstableApi.class)
    private void setRenderersFactory(ExoPlayer.Builder playerBuilder, boolean preferExtensionDecoders) {
        final String TAG = "setRenderersFactory";
        String dbMsg="";
        try {

            RenderersFactory renderersFactory =
                    DemoUtil.buildRenderersFactory(/* context= */ this, preferExtensionDecoders);
            playerBuilder.setRenderersFactory(renderersFactory);
        } catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
    }

    private void updateButtonVisibility() {
        final String TAG = "updateButtonVisibility";
        String dbMsg="";
        try {
//			lp_ppPButton.setEnabled(exoPlayer != null && TrackSelectionDialog.willHaveContent(exoPlayer));
            //	selectTracksButton.setEnabled(exoPlayer != null && TrackSelectionDialog.willHaveContent(exoPlayer));
        } catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
    }

    //	@Override
    public void onVisibilityChanged(int visibility) {
        final String TAG = "onVisibilityChanged";
        String dbMsg="";
        try {
        } catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
    }

    /**
     * exoPlayerを生成する
     * @return Whether initialization was successful.
     * https://github.com/androidx/media/blob/release/demos/main/src/main/java/androidx/media3/demo/main/PlayerActivity.java
     */
    @OptIn(markerClass = UnstableApi.class)
    protected boolean initializePlayer() {
        final String TAG = "initializePlayer";
        String dbMsg="";
        try {
            if (exoPlayer == null) {
                dbMsg="mediaItemList=" + mediaItemList.size() + "件";
                lastSeenTracks = Tracks.EMPTY;
                exoPlayer = new ExoPlayer.Builder(context)                     //MusicService.this
                        .setHandleAudioBecomingNoisy(true)
                        .build();
                dbMsg += "[" +mIndex + "/" + mediaItemList.size() + "件]" + saiseiJikan + "から";
                exoPlayer.setMediaItems(mediaItemList, true);
                exoPlayer.seekTo(mIndex, saiseiJikan); //特定のアイテムの特定の位置から開始
                exoPlayer.setRepeatMode(repeatMode);                    //2:プレイリスト内繰り返し  /  Player.REPEAT_MODE_ONE: 現在の項目が無限ループで繰り返されます。
//                if(NowSavedInstanceState != null){
//                    trackSelectionParameters =
//                            TrackSelectionParameters.fromBundle(
//                                    NowSavedInstanceState.getBundle("track_selection_parameters"));
//                    exoPlayer.setTrackSelectionParameters(trackSelectionParameters);
//                }
                exoPlayer.addListener(new PlayerEventListener());
                exoPlayer.addListener(new Player.Listener() {
                    /**曲変更などのイベント*/
                    @Override
                    public void onEvents(@NonNull Player player, @NonNull Player.Events events) {
                        final String TAG = "onEvents";
                        String dbMsg="[Player.Listener]";
                        try {
                            List<Integer> eventList = new ArrayList<>();
                            dbMsg += "eventList=" + eventList.size() +"件";
                            for (int i = 0 ; i < events.size(); i++)
                                eventList.add(events.get(i));
                            if (eventList.size() > 2) {
                                dbMsg += "、(0)=" + eventList.get(0);
                                dbMsg += "、(1)=" + eventList.get(1);
                                dbMsg += "、(2)=" + eventList.get(2);
                                dbMsg += "、ContentPosition=" + exoPlayer.getContentPosition();
                                if (eventList.get(0) == 4 && eventList.get(1) == 7 && eventList.get(2) == 11 && exoPlayer.getContentPosition() == 0) {
//                                    // SEEK_TO_PREVIOUS
//                                    Intent intent = new Intent("SEND_MESSAGE");
//                                    //            intent.putExtra("MUSIC", RWD);
//                                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                                }
                                myLog(TAG,dbMsg);
                            }
                        } catch (Exception e) {
                            myErrorLog(TAG ,  dbMsg + "で" + e);
                        }
                        Player.Listener.super.onEvents(player, events);
                    }

                    /**再生が別のメディア項目に移行するときの検出:https://developer.android.com/guide/topics/media/exoplayer/playlists#detecting-when-playback-transitions-to-another-media-item*/
                    @Override
                    public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                        final String TAG = "onMediaItemTransition";
                        String dbMsg="[Player.Listener]";
                        try {
                            mIndex=exoPlayer.getCurrentMediaItemIndex();
                            int endIndex = exoPlayer.getMediaItemCount();
                            setPrefInt("pref_mIndex" , mIndex ,  context);
                            dbMsg += "[" + mIndex + "/" + endIndex + "]";               //mediaItemList.size()
//                            boolean nowPlaying = exoPlayer.isPlaying();
//                            dbMsg += ",nowPlaying=" + nowPlaying;
                            dbMsg += ",reason=" + reason;
                            dbMsg += ",artist=" + mediaItem.mediaMetadata.artist;
                            dbMsg += ",albumTitle=" + mediaItem.mediaMetadata.albumTitle;
                            dbMsg += ",title=" + mediaItem.mediaMetadata.title;
                            artistName= (String) mediaItem.mediaMetadata.artist;
                            albumName= (String) mediaItem.mediaMetadata.albumTitle;
                            songTitol= (String) mediaItem.mediaMetadata.title;
//                            if (exoPlayer.getCurrentMediaItemIndex() == 1) {
//                                // SEEK_TO_NEXT
////                                Intent intent = new Intent("SEND_MESSAGE");
////                                //       intent.putExtra("MUSIC", FWD);
////                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
//                            }
                            redrowNotification(mediaItem);

                            objMap=plAL.get(mIndex);
                            String dataFN = (String) objMap.get(MediaStore.Audio.Playlists.Members.DATA);
                            dbMsg += ",dataFN=" + dataFN;
                            String duranation = (String) objMap.get(MediaStore.Audio.Playlists.Members.DURATION);
                            dbMsg += ",duranation=" + duranation;
                            myLog(TAG,dbMsg);
                            sendSongInfo(mIndex);
                            Player.Listener.super.onMediaItemTransition(mediaItem, reason);
                        } catch (Exception e) {
                            myErrorLog(TAG ,  dbMsg + "で" + e);
                        }
                    }
                });
                ////////https://www.jisei-firm.com/android_develop44/#toc2//
                exoPlayer.addAnalyticsListener(new EventLogger());
                exoPlayer.setAudioAttributes(AudioAttributes.DEFAULT,  true);
                exoPlayer.setPlayWhenReady(startAutoPlay);
                configurePlayerWithServerSideAdsLoader();
            }else{
                dbMsg += ",player生成済み";
            }

            exoPlayer.prepare();
            updateButtonVisibility();
      //      	exoPlayer.playWhenReady = true;
            mediaSession = new MediaSession.Builder(context,exoPlayer).build();           // MusicService.this
            // Notification作成//////////////////////////////////////////////////////////////
            MediaStyleNotificationHelper.MediaStyle mediaStyle = new MediaStyleNotificationHelper.MediaStyle(mediaSession);         //
//            mediaStyle.setShowActionsInCompactView(0,1,2);              //,3,4
            //  https://developer.android.com/training/notify-user/expanded?hl=ja

            notificationBuilder = new NotificationCompat.Builder(context, channelId);
            notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            notificationBuilder.setSmallIcon(R.drawable.media_session_service_notification_ic_music_note);
////                    // setShowActionsInCompactView に該当するボタンが必要。アイコンは指定のリソースは無視される
            //addActionは無視される
//            notificationBuilder.addAction(R.drawable.media3_notification_seek_back, "Previous", prevPendingIntent); // #0
//            notificationBuilder.addAction(R.drawable.media3_notification_pause, "Pause", pausePendingIntent);  // #1
//            notificationBuilder.addAction(R.drawable.media3_notification_seek_forward, "Next", nextPendingIntent);     // #2
////                    .addAction(android.R.drawable.stat_notify_sync_noanim, "Repeat", repatPendingIntent)     // #3
////                    .addAction(android.R.drawable.ic_lock_power_off, "Quity", quitPendingIntent)     // #4
////                    // Apply the media style template

            notificationBuilder.setStyle(mediaStyle);                   // NotificationCompat.Style
            notificationBuilder .setContentTitle(songTitol);
            notificationBuilder.setContentText(artistName+" - "+ albumName);
            notificationBuilder.setSound(null);         //通知音を消す
            notificationBuilder.setSilent(true);
            //        notificationBuilder.setLargeIcon(albumArtBitmap);

            notification = notificationBuilder.build();
            startForeground(NOTIFICATION_ID, notification);
            ////////////////////////////////////////////////////////////// Notification作成 //

//            objMap=plAL.get(mIndex);
//            String dataFN = (String) objMap.get(MediaStore.Audio.Playlists.Members.DATA);
//            dbMsg += ",dataFN=" + dataFN;
//            String duranation = (String) objMap.get(MediaStore.Audio.Playlists.Members.DURATION);
//            dbMsg += ",duranation=" + duranation;
//            MediaItem mediaItem = mediaItemList.get(mIndex);
            sendSongInfo(mIndex);
            myLog(TAG,dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return true;
    }

    ///////// https://github.com/androidx/media/blob/release/demos/main/src/main/java/androidx/media3/demo/main/PlayerActivity.java
///ゆるプログラミング日記 〈kotlin〉ExoPlayer////// https://mtnmr.hatenablog.com/entry/2022/09/30/113118

    /**再生曲情報をBroadcastする*/
    public void sendSongInfo(int currentIndex) {       // String dataFN,,MediaItem mediaItem,String duranatione
        final String TAG = "sendSongInfo";
        String dbMsg="";
        try {
            Intent MRIintent = new Intent();
            //      Intent MRIintent = new Intent(getApplicationContext(), MusicPlayerReceiver.class);
            MRIintent.setAction(ACTION_SET_SONG);
            String list_id = myPreferences.nowList_id;
            dbMsg += ",送信するのは" + list_id;
            MRIintent.putExtra("nowList_id",list_id);
            dbMsg += "の[" + currentIndex +"]";
            MRIintent.putExtra("currentIndex",currentIndex);
            objMap=plAL.get(currentIndex);            //mIndex?
            String dataFN = (String) objMap.get(MediaStore.Audio.Playlists.Members.DATA);
            dbMsg += ",dataFN=" + dataFN;
            String duranation = (String) objMap.get(MediaStore.Audio.Playlists.Members.DURATION);
            dbMsg += ",duranation=" + duranation;
            MediaItem mediaItem = mediaItemList.get(currentIndex);
            MRIintent.putExtra("pref_data_url",dataFN);
            MRIintent.putExtra("artist",mediaItem.mediaMetadata.artist);
            MRIintent.putExtra("albumTitle",mediaItem.mediaMetadata.albumTitle);
            MRIintent.putExtra("title",mediaItem.mediaMetadata.title);
            MRIintent.putExtra("duranation",duranation);
            dbMsg += ",exoPlayer=" + exoPlayer;
            long contentPosition = 0l;
            MRIintent.putExtra("isPlaying",  nowPlay);
//            MRIintent.putExtra("contentPosition",contentPosition);
            dbMsg += ",isPlaying=" +  exoPlayer.isPlaying() + ",contentPosition=" +  contentPosition;
            getBaseContext().sendBroadcast(MRIintent);
            myLog(TAG,dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
    }

    /**exoPlayerのContentPositionとisPlayingを送る*/
    public void sendStateChasng() {
        final String TAG = "sendStateChasng";
        String dbMsg="";
        try {
            Intent MRIintent = new Intent();
            dbMsg += ",exoPlayer=" + exoPlayer;
            MRIintent.setAction(ACTION_STATE_CHANGED);
            long contentPosition = 0l;
            if(exoPlayer != null){
                contentPosition = exoPlayer.getContentPosition();
            }
            dbMsg += ",isPlaying=" +  exoPlayer.isPlaying() + ",isPlaying=" +  contentPosition;
            MRIintent.putExtra("isPlaying",  nowPlay);
            MRIintent.putExtra("contentPosition",contentPosition);
            getBaseContext().sendBroadcast(MRIintent);
            myLog(TAG,dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
    }


   /**
    *  プレイヤー破棄
    *  <ul>破棄する前に
    *   <li> 再生ポジションとリスト上の再生順などをプリファレンスに書き込む
    *   <li> mediaSessionも破棄する
    *  */
    protected void destructionPlayer() {
        final String TAG = "destructionPlayer";
        String dbMsg="";
        try {
            dbMsg += "[" + myPreferences.nowList_id + "]" + myPreferences.nowList;
//            myEditor.putString( "nowList_id", String.valueOf(myPreferences.nowList_id));
//            myEditor.putString( "nowList", String.valueOf(myPreferences.nowList));
            mIndex = exoPlayer.getCurrentMediaItemIndex();
            String data_url = (String) plAL.get(mIndex).get(MediaStore.Audio.Playlists.Members.DATA);
            dbMsg += "[" + mIndex + "]" + data_url;
            myEditor.putString( "pref_mIndex", String.valueOf(mIndex));
            myEditor.putString( "pref_data_url", data_url);                 // myPreferences.pref_data_url

            dbMsg += "exoPlayer=" + exoPlayer;
            long contentPosition = 0l;
            if(exoPlayer != null){
                contentPosition = exoPlayer.getContentPosition();
                exoPlayer.release();
                dbMsg += ">>" + exoPlayer;
            }
            dbMsg += "," + contentPosition ;
            myEditor.putString( "pref_position", String.valueOf(contentPosition));
            String duration = (String) plAL.get(mIndex).get(MediaStore.Audio.Playlists.Members.DURATION);
            dbMsg += "/" + duration + "[ms]";
            myEditor.putString( "pref_duration", String.valueOf(duration));
            boolean kakikomi = myEditor.commit();
            dbMsg +=",Pref書き込み=" + kakikomi;

            dbMsg += ",mediaSession=" + mediaSession;
            if(mediaSession != null){
                mediaSession.release();
                mediaSession = null;
                dbMsg += ">>" + mediaSession;
            }
            myLog(TAG,dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        final String TAG = "onGetRoot";
        String dbMsg="";
        BrowserRoot br =null;
        try{
//            // (Optional) Control the level of access for the specified package name.
//            // You'll need to write your own logic to do this.
//            if (allowBrowsing(clientPackageName, clientUid)) {
//                // Returns a root ID that clients can use with onLoadChildren() to retrieve the content hierarchy.
                 br = new BrowserRoot(MY_MEDIA_ROOT_ID, null);
//            } else {
//                // Clients can connect, but this BrowserRoot is an empty hierachy
//                // so onLoadChildren returns nothing. This disables the ability to browse for content.
//                br = new BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null);
//            }
            myLog(TAG,dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG,dbMsg+"で"+e);
        }
        return br;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowser.MediaItem>> result) {
        final String TAG = "onLoadChildren";
        String dbMsg="";
        try{
            dbMsg += "parentId=" + parentId;
            //  Browsing not allowed
            if (TextUtils.equals(MY_EMPTY_MEDIA_ROOT_ID, parentId)) {
                result.sendResult(null);
                return;
            }

            // Assume for example that the music catalog is already loaded/cached.
            List<MediaBrowser.MediaItem> mediaItems = new ArrayList<>();

            // Check if this is the root menu:
            if (MY_MEDIA_ROOT_ID.equals(parentId)) {
                // Build the MediaItem objects for the top level,
                // and put them in the mediaItems list...
            } else {
                // Examine the passed parentMediaId to see which submenu we're at,
                // and put the children of that menu in the mediaItems list...
            }
            dbMsg += ",mediaItems=" + mediaItems.size() + "件";
            result.sendResult(mediaItems);
            myLog(TAG,dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG,dbMsg+"で"+e);
        }

    }

    /**サービスが最初に作成されたときに 1 回限りのセットアップ処理を行う
     *　onStartCommandより先なのでパラメータは渡っていない
     * extends MediaBrowserServiceCompat だとActiviteyと同様のあつかいになる？
     *
     * */
    @Override
    public void onCreate() {											//①ⅸ
        super.onCreate();
        final String TAG = "onCreate";
        String dbMsg="";
        try{
            context = this;                       //getBaseContext();          //getApplicationContext();
            ORGUT = new OrgUtil();		//自作関数集
            MyUtil = new MyUtil();
            myPreferences = new MyPreferences(this);
            dbMsg +="、PREFS_NAME=" + MyConstants.PREFS_NAME;
            if (31 <= android.os.Build.VERSION.SDK_INT ) {
                sharedPref = getSharedPreferences(MyConstants.PREFS_NAME, MODE_PRIVATE);
                myEditor = sharedPref.edit();
            }else{
                sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());			//	this.getSharedPreferences(this, MODE_PRIVATE);		//
                myEditor = sharedPref.edit();
            }
            readPref();
            myLog(TAG,dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG,dbMsg+"で"+e);
        }
    }

    /**サービスが使用されなくなり、破棄される*/
    @Override
    public void onDestroy() {
        final String TAG = "onDestroy";
        String dbMsg="";
        try{
            destructionPlayer();
            if(mediaItemList != null){
                dbMsg += ",mediaItemList=" + mediaItemList.size() + "件";
                mediaItemList.clear();
                dbMsg += ">>" + mediaItemList.size() + "件";
                mediaItemList=null;
            }
            myLog(TAG,dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG,dbMsg+"で"+e);
        }
        super.onDestroy();
    }

  //  @Override
    public void onStart() {
        final String TAG = "onStart";
        String dbMsg="";
        try{
            //    sessionToken = SessionToken(this, ComponentName(this, PlaybackService::; class.java))
            myLog(TAG,dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG,dbMsg+"で"+e);
        }
        super.onDestroy();
    }


    //////////////////////////////////////////////////////////////////////////
    public static void myLog(String TAG , String dbMsg) {
        MyUtil MyUtil = new MyUtil();
        MyUtil.myLog(TAG , "[MusicService]" + dbMsg);
    }

    public static void myErrorLog(String TAG , String dbMsg) {
        MyUtil MyUtil = new MyUtil();
        MyUtil.myErrorLog(TAG , "[MusicService]" + dbMsg);
    }

}