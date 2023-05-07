package com.hijiyam_koubou.marasongs;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.service.media.MediaBrowserService;
import android.support.v4.media.session.MediaSessionCompat;
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
import androidx.media3.ui.PlayerView;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.util.ArrayList;
import java.util.List;
@SuppressLint("InlinedApi")
public class MusicService extends MediaBrowserService {
    public OrgUtil ORGUT;						//自作関数集
    private static final String MY_MEDIA_ROOT_ID = "media_root_id";
    private static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";
    public ExoPlayer exoPlayer;				//音楽プレイヤーの実体
    private MediaSession mediaSession;            //MediaSessionCompat ？　MediaSession
    private MediaSessionCompat.Token sessionToken;
//    private PlaybackStateCompat.Builder stateBuilder;
    public List<MediaItem> mediaItemList;
    public int mIndex = 0;

    public String pref_data_url;
    public String artistName;
    public String albumName;
    public String songTitol;
    public Bitmap albumArtBitmap;

    public long saiseiJikan = 0;
    /**ノティフィケーションインスタンス*/
	public Notification lpNotification;
    public MuList ML;

    private String channelId = "default";
    final int NOTIFICATION_ID = 1;						//☆生成されないので任意の番号を設定する	 The ID we use for the notification (the onscreen alert that appears at the notification area at the top of the screen as an icon -- and as text as well if the user expands the notification area).

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

    private static final int[] REQUEST_CODE = { 0, 1, 2 };
    /////////////////////プレイヤーの状態をクライアントに通知する//
    ///通知の作成////////////////////
    //通知を作成、サービスをForegroundにする
    /// https://developer.android.com/guide/topics/media-apps/audio-app/building-a-mediabrowserservice?hl=ja
    private void CreateNotification() {
        final String TAG = "CreateNotification";
        String dbMsg="";
        try{
/*            PendingIntent intent = WorkManager.getInstance(getApplicationContext()).createCancelPendingIntent(getId());
            lpNotification = new NotificationCompat.Builder(context, id)
                    .setContentTitle(title)
                    .setTicker(title)
                    .setSmallIcon(R.drawable.ic_work_notification)
                    .setOngoing(true)
                    // Add the cancel action to the notification which can
                    // be used to cancel the worker
                    .addAction(android.R.drawable.ic_delete, "cancel", intent)
                    .build();*/
            NotificationCompat.Builder builder  = new NotificationCompat.Builder(getApplicationContext(),channelId);
            builder.setSmallIcon(R.drawable.ic_launcher);
            builder.setContentTitle(songTitol);
            builder.setContentText(artistName + " - " + albumName);
   //         builder.setLargeIcon(albumArtBitmap);
            builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0));
    //        builder.setMediaSession(mediaSession);

//            lpNotification = new Notification.Builder()
//                    .setSmallIcon(R.drawable.ic_stat_player)
//                    .setContentTitle("Track title")
//                    .setContentText("Artist - Album")
//                    .setLargeIcon(albumArtBitmap))
//                    .setStyle(new Notification.MediaStyle()
//                    .setMediaSession(mediaSession))
//                    .build();

//            Context context = getApplicationContext();
//            MediaControllerCompat controller = mediaSession.getController();        //MediaControllerCompat.getMediaController(MuList.class);  //mediaSession.getController();
//            MediaMetadataCompat mediaMetadata = controller.getMetadata();
//            MediaDescriptionCompat description = mediaMetadata.getDescription();
//
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
//
//            builder
//                    // Add the metadata for the currently playing track
//                    .setContentTitle(description.getTitle())
//                    .setContentText(description.getSubtitle())
//                    .setSubText(description.getDescription())
//                    .setLargeIcon(description.getIconBitmap())
//
//                    // Enable launching the player by clicking the notification
//                    .setContentIntent(controller.getSessionActivity())
//
//                    // Stop the service when the notification is swiped away
//                    .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context,
//                            PlaybackStateCompat.ACTION_STOP))
//
//                    // Make the transport controls visible on the lockscreen
//                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//
//                    // Add an app icon and set its accent color
//                    // Be careful about the color
//                    .setSmallIcon(R.drawable.notification_icon)
//                    .setColor(ContextCompat.getColor(context, R.color.primaryDark))
//
//                    // Add a pause button
//                    .addAction(new NotificationCompat.Action(
//                            R.drawable.pause, getString(R.string.pause),
//                            MediaButtonReceiver.buildMediaButtonPendingIntent(context,
//                                    PlaybackStateCompat.ACTION_PLAY_PAUSE)))
//
//                    // Take advantage of MediaStyle features
//                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
//                            .setMediaSession(mediaSession.getSessionToken())
//                            .setShowActionsInCompactView(0)
//
//                            // Add a cancel button
//                            .setShowCancelButton(true)
//                            .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context,
//                                    PlaybackStateCompat.ACTION_STOP)));
//
//            // Display the notification and place the service in the foreground
//            startForeground(id, builder.build());
////            dbMsg +=" ,mSession=" + mSession.toString();
            /* V4
            MediaControllerCompat controller = mediaSession.getController();
            MediaMetadataCompat mediaMetadata = controller.getMetadata();
            dbMsg +=" ,mediaMetadataのsize=" + mediaMetadata.size();

            if (mediaMetadata == null && !mSession.isActive()) return;

            MediaDescriptionCompat description = mediaMetadata.getDescription();
            dbMsg +=" ,description=" + description.toString();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
            String mediaID = description.getMediaId();
            dbMsg +=" ,[" + mediaID + "]";
            CharSequence nowTitle = description.getTitle();
            dbMsg +=" ,nowTitle=" + nowTitle;
            CharSequence nowSubtitle = description.getSubtitle();
            dbMsg +=" ,nowSubtitle=" + nowSubtitle;
            CharSequence nowDescription = description.getDescription();
            dbMsg +=" ,nowDescription=" + nowDescription;
            Bitmap iconBitmap = description.getIconBitmap();
            if(iconBitmap == null){
                dbMsg +=" ,iconBitmap=null";
                iconBitmap=mLibrary.getAlbumBitmap(this.rContext,mediaID);
                dbMsg +=">>";
            }
            dbMsg +=" ,iconBitmap(" + iconBitmap.getWidth() + " × " + iconBitmap.getHeight() + ")";
            */

//            // Get the session's metadata
//            List<MediaSession.ControllerInfo> controller = mediaSession.getConnectedControllers();

//            MediaMetadataCompat mediaMetadata = controller.getMetadata();
//            MediaDescriptionCompat description = mediaMetadata.getDescription();
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
//
//            builder
//                    // Add the metadata for the currently playing track
//                    .setContentTitle(description.getTitle())
//                    .setContentText(description.getSubtitle())
//                    .setSubText(description.getDescription())
//                    .setLargeIcon(description.getIconBitmap())
//
//                    // Enable launching the player by clicking the notification
//                    .setContentIntent(controller.getSessionActivity())
//
//                    // Stop the service when the notification is swiped away
//                    .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(getApplicationContext(),
//                            PlaybackStateCompat.ACTION_STOP))
//
//                    // Make the transport controls visible on the lockscreen
//                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//
//                    // Add an app icon and set its accent color
//                    // Be careful about the color
//                    .setSmallIcon(R.drawable.ic_launcher_notif)
//                    .setColor(ContextCompat.getColor(getApplicationContext(), R.color.primaryDark))
//
//                    // Add a pause button
//                    .addAction(new NotificationCompat.Action(
//                            R.drawable.pouse_notif, getString(R.string.pause),
//                            MediaButtonReceiver.buildMediaButtonPendingIntent(getApplicationContext(),
//                                    PlaybackStateCompat.ACTION_PLAY_PAUSE)))
//
//                    // Take advantage of MediaStyle features
//                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
//                            .setMediaSession(mediaSession.getSessionToken())
//                            .setShowActionsInCompactView(0)
//
//                            // Add a cancel button
//                            .setShowCancelButton(true)
//                            .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(getApplicationContext(),
//                                    PlaybackStateCompat.ACTION_STOP)));
//
//            // Display the notification and place the service in the foreground
//            startForeground(NOTIFICATION_ID, builder.build());

//
//            builder
//                    //現在の曲の情報を設定
//                    .setContentTitle(nowTitle)
//                    .setContentText(nowSubtitle)
//                    .setSubText(nowDescription)
//                    .setLargeIcon(iconBitmap)
//
//                    // 通知をクリックしたときのインテントを設定
//                    .setContentIntent(pendingIntent)
//
//                    // 通知がスワイプして消された際のインテントを設定
//                    .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,
//                            PlaybackStateCompat.ACTION_STOP))
//
//                    // 通知の範囲をpublicにしてロック画面に表示されるようにする
//                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                    //exo_controls_play
//                    .setSmallIcon(R.drawable.play_notif)
//                    //通知の領域に使う色を設定
//                    //Androidのバージョンによってスタイルが変わり、色が適用されない場合も多い
//                    .setColor(ContextCompat.getColor(this, R.color.colorAccent))
//
//                    // Media Styleを利用する
//                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mSession.getSessionToken())
//                            //.setMediaSession(MediaSessionCompat.Token.fromToken(mSession.getSessionToken()))
//                            .setShowActionsInCompactView(1)			//0,1,3,4
//                    );
//
////				.setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
////						.setMediaSession(mSession.getSessionToken())
////						//通知を小さくたたんだ時に表示されるコントロールのインデックスを設定
////						.setShowActionsInCompactView(1));
//
//            // Android4.4以前は通知をスワイプで消せないので
//            //キャンセルボタンを表示することで対処
//            //今回はminSDKが21なので必要ない
//            //.setShowCancelButton(true)
//            //.setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,
//            //        PlaybackStateCompat.ACTION_STOP)));
//
//            //通知のコントロールの設定
//            //exo_controls_previous ?
//            builder.addAction(new NotificationCompat.Action(
//                    R.drawable.rewbtn, "prev",
//                    MediaButtonReceiver.buildMediaButtonPendingIntent(this,
//                            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)));
//
//            //プレイヤーの状態で再生、一時停止のボタンを設定
//            PlaybackStateCompat stateObj = controller.getPlaybackState();
//            final int state = stateObj == null ? PlaybackStateCompat.STATE_NONE : stateObj.getState();
//            dbMsg +=" ,state=" + state;
//            if (state == PlaybackStateCompat.STATE_PLAYING) {
//                builder.addAction(new NotificationCompat.Action(
//                        R.drawable.pouse_notif, "pause",
//                        MediaButtonReceiver.buildMediaButtonPendingIntent(this,
//                                PlaybackStateCompat.ACTION_PAUSE)));
//            } else {
//                builder.addAction(new NotificationCompat.Action(
//                        R.drawable.play_notif, "play",
//                        MediaButtonReceiver.buildMediaButtonPendingIntent(this,
//                                PlaybackStateCompat.ACTION_PLAY)));
//            }
//            builder.addAction(new NotificationCompat.Action(
//                    R.drawable.ffbtn, "next",
//                    MediaButtonReceiver.buildMediaButtonPendingIntent(this,
//                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT)));
            dbMsg +=" ,builder=" + builder.toString();
            startForeground(NOTIFICATION_ID, builder.build());
////android.app.RemoteServiceException$CannotPostForegroundServiceNotificationException: Bad notification for startForeground
//            //再生中以外ではスワイプで通知を消せるようにする
//            if (state != PlaybackStateCompat.STATE_PLAYING) {
//                stopForeground(false);
//            }
            myLog(TAG,dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG,dbMsg+"で"+e);
        }
    }


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

    /**指定されたリストを内部配列に読み込む*/
    @SuppressLint("Range")
    public List<MediaItem> readCalentList(int playlistId , String listName){
        final String TAG = "readCalentList";
        String dbMsg= "";
        List<MediaItem> mediaItemList = new ArrayList<MediaItem>();
        try{
            dbMsg += "選択されたプレイリスト[ID="+playlistId + "]" + listName;
            dbMsg += " の "+ pref_data_url;
//            if(listName.equals(MusicPlayerService.this.getResources().getString(R.string.all_songs_file_name))){
//                ZenkyokuHelper zenkyokuHelper = null;				//全曲リストヘルパー
//                SQLiteDatabase Zenkyoku_db;		//全曲リストファイル
//                zenkyokuHelper = new ZenkyokuHelper(this ,  getString(R.string.zenkyoku_file));		//全曲リストの定義ファイル		.
//                Zenkyoku_db = zenkyokuHelper.getReadableDatabase();		//アーティスト名のえリストファイルを読み書きモードで開く
//                dbMsg =  ">isOpen>" + Zenkyoku_db.isOpen();		//03-28java.lang.IllegalArgumentException:  contains a path separator
//                dbMsg =  ",getPageSize=" + Zenkyoku_db.getPageSize() + "件、" ;			//Kari_db = SQLiteDatabase: /data/data/com.hijiyam_koubou.marasongs/databases/zenkyoku.db
//                String table = getResources().getString(R.string.zenkyoku_table);
//                Zenkyoku_db = zenkyokuHelper.getReadableDatabase();			// データベースをオープン
//                String[] columns =null;			//{  "ALBUM_ARTIST" , "ARTIST"};				//検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
//                String selections = null;	//"ALBUM_ARTIST = ? ";			//+ comp ;		//MediaStore.Audio.Media.ARTIST +" <> " + comp;			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
//                String[] selectionArgs = null;	//new String[]{ comp };
//                String groupBy = null;					//groupBy句を指定します。
//                String having =null;					//having句を指定します。
//                String orderBy = null;  //"ALBUM_ARTIST_INDEX";
//                String limit = null;					//検索結果の上限レコードを数を指定します。
//                Cursor zCursor = Zenkyoku_db.query( table ,columns, selections,  selectionArgs,  groupBy,  having,  orderBy,  limit) ;
//                dbMsg = ",全曲=" + zCursor.getCount() + "件";
//                if(zCursor.moveToFirst()){
//                    do{
//                        @SuppressLint("Range")  String cVal = zCursor.getString(zCursor.getColumnIndex("DATA"));
//                        pSL.add(cVal);
//                    }while( zCursor.moveToNext() ) ;				//pdCoundtVal <  pdMaxVal
//                }
//                zCursor.close();
//
//            }else{
                final Uri cUri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
                String[] columns = null;				//{ idKey, nameKey };
                String c_selection = null;				//MediaStore.Audio.Playlists.NAME +" = ? ";
                String[] c_selectionArgs = null;			//{listName};        //⑥引数groupByには、groupBy句を指定します。
                String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
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
                        for(int i = 0 ; i < playLists.getColumnCount() ; i++ ){				//MuList.this.koumoku
                            String cName = playLists.getColumnName(i);
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
                                    String cVal = playLists.getString(i);
                                    if(cVal != null){
                                        cVal = cVal;
                                    }
//                                    dbMsg +=  "="+cVal;
                                    audioId = cVal;
                                }else if( cName.equals(MediaStore.Audio.Playlists.Members.DATA)){	//[5/37]_data=/storage/sdcard0/external_sd/Music/Santana/All That I Am/05 Just Feel Better.wma
                                    uriStr = playLists.getString(i);
                                    dbMsg += "[" + i +"/" + playLists.getColumnCount() +"項目]"+ cName;
                                    if(uriStr != null){
                                    }else{
                                       dbMsg +=  "Url取得できず";
                                    }
                                    dbMsg +=  "="+uriStr;
                                }else if( cName.equals("album_artist")){		//[26/37]
                                    String cVal = playLists.getString(i);
                                    if(cVal != null){
                                        cVal = cVal;
                                    }
//                                    dbMsg +=  "="+cVal;
                                    albumArtistStr = cVal;
                                }else if( cName.equals(MediaStore.Audio.Playlists.Members.ARTIST)){		//[33/37]artist=Santana
                                    String cVal = playLists.getString(i);
                                    if(cVal != null){
                                        cVal = cVal;
                                    }else{
                                        cVal = getResources().getString(R.string.bt_unknown);			//不明
                                    }
//                                    dbMsg +=  "="+cVal;
                                    artistStr = cVal;
                                }else if( cName.equals(MediaStore.Audio.Playlists.Members.ALBUM)){		//[33/37]artist=Santana
                                    String cVal = playLists.getString(i);
                                    if(cVal != null){
                                        cVal = cVal;
                                    }else{
                                        cVal = getResources().getString(R.string.bt_unknown);			//不明
                                    }
//                                    dbMsg +=  "="+cVal;
                                    albumTitleStr = cVal;
                                }else if( cName.equals(MediaStore.Audio.Playlists.Members.TITLE)){		//[12/37]title=Just Feel Better
                                    String cVal = playLists.getString(i);
                                    if(cVal != null){
                                        cVal = cVal;
                                    }else{
                                        cVal = getResources().getString(R.string.bt_unknown);			//不明
                                    }
                                    titleStr = cVal;
                                }else if( cName.equals(MediaStore.Audio.Playlists.Members.DURATION)){	//[14/37]duration=252799>>04:12 799
                                    String cVal = playLists.getString(i);
//                                    dbMsg +=  "="+cVal;
                                    if(cVal != null){
                                        cVal = cVal;
                                    }
                                    duranationStr = ",duranation=" + ORGUT.sdf_mss.format(Long.valueOf(cVal)) + "[s]";
                                    dbMsg +=  ">>"+duranationStr;
                                }else if( cName.equals(MediaStore.Audio.Playlists.Members.TRACK)){
                                    trackStr = playLists.getString(i);
                                }else if( cName.equals(MediaStore.Audio.Playlists.Members.ALBUM_ID)){
                                    String albunIdStr = String.valueOf(playLists.getInt(i));
                                }else{
                                    int cPosition = playLists.getColumnIndex(cName);
                                  //  dbMsg += "『" + cPosition+"』";
                                    String cVal ="";
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
                            }
                        }
                        dbMsg +=  ",\nuriStr="+ uriStr;
                        if(uriStr != null){
                            String imageUriStr = ORGUT.retAlbumArtUri(getApplicationContext(), (String) artistStr, (String) albumTitleStr);
                            dbMsg +=  ",imageUriStr="+ imageUriStr;
                            Uri imageUri = null;
                            if(imageUriStr != null){
                                imageUri = Uri.parse(imageUriStr);
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
                            mediaItemList.add(mItem);
                            dbMsg += "\n取得["+ mediaItemList.size() + "]";
                            dbMsg +=  ",albumArtist="+ mItem.mediaMetadata.albumArtist;
                            dbMsg +=  ",artist="+ mItem.mediaMetadata.artist;
                            dbMsg +=  ",albumTitle="+ mItem.mediaMetadata.albumTitle;
                            dbMsg +=  "["+ mItem.mediaMetadata.trackNumber + "]";
                            dbMsg +=  ",title="+ albumTitleStr;         //mItem.mediaMetadata.title;
                //            dbMsg +=  ",artworkUri="+ mItem.mediaMetadata.artworkUri;
                            dbMsg +=  ",genre="+ mItem.mediaMetadata.genre;
                            //		dbMsg +=  ",release="+ mItem.mediaMetadata.releaseYear + "/"+ mItem.mediaMetadata.releaseMonth + "/"+ mItem.mediaMetadata.releaseYear;
//                            if(uriStr.equals(pref_data_url)){
//                                artistName = (String) artistStr;
//                                albumName = (String) albumTitleStr;
//                                songTitol = (String) titleStr;
//                                //                   albumArtBitmap;
//
//                            }
                        }
                    }while( playLists.moveToNext() ) ;				//pdCoundtVal <  pdMaxVal
            }
            playLists.close();

//            }
            dbMsg += "、"+ mediaItemList.size() + "件";
            myLog(TAG, dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return mediaItemList;
    }

    /****/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String TAG = "onStartCommand";
        String dbMsg="";
        try{
            dbMsg +="、flags=" + flags;
            dbMsg +="、startId=" + startId;
            Class<?> callClass = intent.getClass();
            dbMsg +="、callClass=" + callClass.getName();
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
//            if (action.equals(ACTION_START_SERVICE)) {
//                dbMsg += ".読み込み前[ " + myPreferences.nowList_id+ "] " + myPreferences.nowList + " の "+ myPreferences.pref_data_url;		// + "の" + saiseiJikan + "から";
////				playerClass = (Class<? extends MaraSonActivity>) intent.getSerializableExtra("callClass");
////				dbMsg += ",playerClass=" + playerClass.getName();
//                notifyIntent = new Intent(MusicPlayerService.this, MaraSonActivity.class);		//getApplication()
            String nowList_id = intent.getStringExtra("nowList_id");
            //extras.getString("nowList");
             //   String nowList_id = String.valueOf(intent.getIntExtra("nowList_id", 0));				//extras.getInt("nowList_id");
                dbMsg += ",渡されたのは[ " + nowList_id+ "] ";
//                if(myPreferences.nowList_id != readID){
//                    myPreferences.nowList_id = readID;
//                    dbMsg += ">>[ " + myPreferences.nowList_id+ "] "  ;		// + "の" + saiseiJikan + "から";
//                }
                String nowList = intent.getStringExtra("nowList");				//extras.getString("nowList");
                dbMsg += nowList;
//                if(!myPreferences.nowList.equals(readList)){
//                    myPreferences.nowList = readList;
//                    dbMsg += ">>" + myPreferences.nowList ;		// + "の" + saiseiJikan + "から";
//                }
//                dbMsg += ",読み込み前= "+ myPreferences.pref_data_url;		// + "の" + saiseiJikan + "から";
            pref_data_url= intent.getStringExtra("pref_data_url");		//extras.getString("pref_data_url");
                dbMsg += ",渡されたのは= " + pref_data_url;
//                if(myPreferences.pref_data_url != null || !readUrl.equals(myPreferences.pref_data_url)){
//                    myPreferences.pref_data_url = readUrl;
//                    dbMsg += ">>"+ myPreferences.pref_data_url;
//                }
//            ArrayList<MediaItem> itemList = null;
//            String MediaItem;
//            mediaItemList = intent.putParcelableArrayListExtra("mediaItemList");        //List<MediaItem> mediaItemList
//                if(plSL == null || plSL.size()<1){
//                    plSL =  new ArrayList<String>();				//プレイリスト用簡易リスト
//                    plSL.clear();
            mediaItemList = readCalentList(Integer.parseInt(nowList_id),nowList);
//                    listEnd = plSL.size();
//                    //			mPlayer2 = null;
//                }
//                int rInt = getCarentIndex(myPreferences.pref_data_url,plSL);
//                if(-1 < rInt){
//                    mIndex = rInt;
//                }
            mIndex =intent.getIntExtra("mIndex", 0);
            dbMsg += "で[mIndex：" + mIndex + "/"+ mediaItemList.size() + "件]";
            saiseiJikan =intent.getLongExtra("saiseiJikan", 0);
            dbMsg += ",saiseiJikan=" + saiseiJikan;
////				boolean retBool = setPrefInt("pref_mIndex", mIndex, this);        //プリファレンスの読込み
////				dbMsg += "を書込み" + retBool;
//                if(musicPlaylist == null){
//                    musicPlaylist = new MusicPlaylist(MusicPlayerService.this);
//                }
//                //		lpNotificationMake(myPreferences.pref_data_url);
//                IsPlaying=intent.getBooleanExtra("IsPlaying",false);
//                dbMsg += ",IsPlaying=" + IsPlaying;
//////一旦停止				playNextSong(mIndex,IsPlaying);
//////				if(IsPlaying){
//////					configAndStartMediaPlayer();
//////				}else{
//////					mState = State.Paused;
//////				}
////				//画面遷移のやり方でプレイヤー画面を開く
//////一旦停止					startActivity(notifyIntent);
//                dbMsg += "＞＞＞＞＞リストから選曲";
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
//            } else if (action.equals(ACTION_PAUSE)) {
//                dbMsg +="でポーズ";
//                processPauseRequest();																	//②ⅲPause?
//            } else if (action.equals(ACTION_SKIP)) {
//                dbMsg +="で送り";
//                if( exoPlayer !=null ){
//                    exoPlayer.setPlayWhenReady(false);
//                }
//                processSkipRequest();																	//②ⅲFF?次の曲に順送り
//            } else if (action.equals(ACTION_REWIND)) {
//                dbMsg +="から戻し,";
//                if( exoPlayer !=null ){
//                    exoPlayer.setPlayWhenReady(false);
//                }
//                processRewindRequest();																//②ⅲRew?
//            } else if (action.equals(ACTION_STOP)) {
//                dbMsg +="で停止,";
//                processStopRequest(false);															//②ⅲStop?eタイマーを破棄してmPlayerの破棄へ
//                //			if (intent.getBooleanExtra("cancel", false)) {
//                //				mNotificationManager.cancel(NOTIFICATION_ID);
//                //			}
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
            initializePlayer(); // EXOPLAYER
            myLog(TAG,dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG,dbMsg+"で"+e);
        }
        //	return START_REDELIVER_INTENT;				//	再起動前と同じ順番で再起動してくれる。Intentも渡ってきています。
        return START_NOT_STICKY;				//APIL5;サービスが強制終了した場合、サービスは再起動しない
        /*START_STICKY	サービスが強制終了した場合、サービスは再起動するonStartCommand()が再度呼び出され、Intentにnullが渡される
         */
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

                if (playbackState == Player.STATE_ENDED) {
                    showControls();
                }
                updateButtonVisibility();
            } catch (Exception e) {
                myErrorLog(TAG ,  dbMsg + "で" + e);
            }
        }


        @Override
        public void onPlayerError(PlaybackException error) {
            final String TAG = "onPlayerError";
            String dbMsg="[PlayerEventListener]";
            try {

                if (error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
                    exoPlayer.seekToDefaultPosition();
                    exoPlayer.prepare();
                } else {
                    updateButtonVisibility();
                    showControls();
                }
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
                    return;
                }
                if (tracks.containsType(C.TRACK_TYPE_VIDEO)
                        && !tracks.isTypeSupported(C.TRACK_TYPE_VIDEO, /* allowExceedsCapabilities= */ true)) {
                    showToast("Media includes video tracks, but none are playable by this device");
                }
                if (tracks.containsType(C.TRACK_TYPE_AUDIO)
                        && !tracks.isTypeSupported(C.TRACK_TYPE_AUDIO, /* allowExceedsCapabilities= */ true)) {
                    showToast("Media includes audio tracks, but none are playable by this device");
                }
                lastSeenTracks = tracks;	} catch (Exception e) {
                myErrorLog(TAG ,  dbMsg + "で" + e);
            }

        }
    }

    @OptIn(markerClass = UnstableApi.class)
    private void configurePlayerWithServerSideAdsLoader() {
        final String TAG = "configurePlayerWithServerSideAdsLoader";
        String dbMsg="";
        try {

            serverSideAdsLoader.setPlayer(exoPlayer);
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
            return builder.build();
        } catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return null;
    }

    /**
     * 指定されたプレイリストの曲情報をList<MediaItem>に返す
     * **/
//    private List<MediaItem> createMediaItems(Intent intent) {
//        final String TAG = "createMediaItems";
//        String dbMsg="";
//        List<MediaItem> retList = new ArrayList<MediaItem>();
//        try {
//            dbMsg += "選択されたプレイリスト[ID=" + myPreferences.nowList_id + "]" + myPreferences.nowList ;
////			final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", Long.parseLong(myPreferences.nowList_id));
////			final String[] columns = null;			//{ idKey, nameKey };
////			String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
////			Cursor rCursor= this.getContentResolver().query(uri, columns, null, null, c_orderBy );
////			dbMsg += ",rCursor=" + rCursor.getCount() + "件";
////			if( rCursor.moveToFirst() ) {
////				int rPosi = rCursor.getPosition();
////				dbMsg= "[" + rPosi +"/" + rCursor.getCount() +"曲]";		//MuList.this.rCount
////				String subText = null;
////				String ArtistName = null;
////				String AlbumArtistName = null;
////				String AlbumName = null;
////				String songTitol = null;
////				String Dur = null;
////				objMap = new HashMap<String, Object>();
////				for(int i = 0 ; i < rCursor.getColumnCount() ; i++ ){				//MuList.this.koumoku
////					String cName = rCursor.getColumnName(i);
////					dbMsg += "[" + i +"/" + rCursor.getColumnCount() +"項目]"+ cName;
////					if(
//////					cName.equals(MediaStore.Audio.Playlists.Members.INSTANCE_ID) ||	//[1/66]instance_id
//////					cName.equals(MediaStore.Audio.Playlists.Members.TITLE_KEY) ||	//[13/37]title_key
//////					cName.equals(MediaStore.Audio.Playlists.Members.SIZE) ||			//[7/37]_size=4071748
//////					cName.equals(MediaStore.Audio.Playlists.Members.IS_RINGTONE) ||	//[20/37]is_ringtone=0
//////					cName.equals(MediaStore.Audio.Playlists.Members.IS_MUSIC) ||			//[21/37]is_music=1
//////					cName.equals(MediaStore.Audio.Playlists.Members.IS_ALARM) ||			//[22/37]is_alarm=0
//////					cName.equals(MediaStore.Audio.Playlists.Members.IS_NOTIFICATION) ||	//[23/37]is_notification=0
//////					cName.equals(MediaStore.Audio.Playlists.Members.IS_PODCAST) ||			//[24/37]is_podcast=
//////				 	cName.equals(MediaStore.Audio.Playlists.Members.ARTIST_KEY) ||		//
//////					cName.equals(MediaStore.Audio.Playlists.Members.ALBUM_KEY) ||		//[35/37]album_key
////							cName.equals(MediaStore.Audio.Playlists.Members.XMP)){		//[31/66項目]xmp=【Blob】[32/37]artist_key
////						dbMsg += "は読み込めない";
////					}else{
////						if( cName.equals("album_artist")){		//[26/37]
////							String cVal = rCursor.getString(i);
////							if(cVal != null){
////								cVal = cVal;
////							}
////							dbMsg +=  "="+cVal;
////							AlbumArtistName = cVal;
////							objMap.put(cName ,cVal );
////						}else if( cName.equals(MediaStore.Audio.Playlists.Members.ARTIST)){		//[33/37]artist=Santana
////							String cVal = rCursor.getString(i);
////							if(cVal != null){
////								cVal = cVal;
////							}else{
////								cVal = getResources().getString(R.string.bt_unknown);			//不明
////							}
////							dbMsg +=  "="+cVal;
////							ArtistName = cVal;
////							objMap.put(cName ,cVal );
////						}else if( cName.equals(MediaStore.Audio.Playlists.Members.ALBUM)){		//[33/37]artist=Santana
////							String cVal = rCursor.getString(i);
////							if(cVal != null){
////								cVal = cVal;
////							}else{
////								cVal = getResources().getString(R.string.bt_unknown);			//不明
////							}
////							dbMsg +=  "="+cVal;
////							AlbumName = cVal;
////							objMap.put(cName ,cVal );
////						}else if( cName.equals(MediaStore.Audio.Playlists.Members.TITLE)){		//[12/37]title=Just Feel Better
////							String cVal = rCursor.getString(i);
////							if(cVal != null){
////								cVal = cVal;
////							}else{
////								cVal = getResources().getString(R.string.bt_unknown);			//不明
////							}
////							objMap.put(cName ,cVal );
////							objMap.put("main" ,cVal );
////							dbMsg +=  "="+cVal;
////						//	MuList.this.plSL.add(cVal);
////							songTitol = cVal;
////						}else if( cName.equals(MediaStore.Audio.Playlists.Members.DURATION)){	//[14/37]duration=252799>>04:12 799
////							String cVal = rCursor.getString(i);
////							dbMsg +=  "="+cVal;
////							if(cVal != null){
////								cVal = cVal;
////							}
////							objMap.put(cName ,cVal );
////							Dur = "["+ ORGUT.sdf_mss.format(Long.valueOf(cVal)) + "]";
////							dbMsg +=  ">>"+Dur;
////						}else if( cName.equals(MediaStore.Audio.Playlists.Members.DATA)){	//[5/37]_data=/storage/sdcard0/external_sd/Music/Santana/All That I Am/05 Just Feel Better.wma
////							String cVal = rCursor.getString(i);
////							if(cVal != null){
////								cVal = cVal;
////							}
////							objMap.put(cName ,cVal );
////						//	MuList.this.saisei_fnameList.add(cVal);
////						//	MuList.this.plSL.add(cVal);
////						//	dbMsg +=  "="+cVal;
////						}else if( cName.equals(MediaStore.Audio.Playlists.Members.TRACK)){
////							String cVal = rCursor.getString(i);
////							//	cVal = MyUtil.checKTrack( cVal);
////							objMap.put(cName ,cVal );
////						}else if( cName.equals(MediaStore.Audio.Playlists.Members.ALBUM_ID)){
////							String cVal = String.valueOf(rCursor.getInt(i));
////							objMap.put(cName ,cVal );
////							dbMsg +=  "="+cVal;
////						}else{
////							int cPosition = rCursor.getColumnIndex(cName);
////							dbMsg += "『" + cPosition+"』";
////							String cVal ="";
////							if(0<cPosition){
////								int colType = rCursor.getType(cPosition);
////								//		dbMsg += ",Type=" + colType + ",";
////								switch (colType){
////									case Cursor.FIELD_TYPE_NULL:          //0
////										cVal ="【null】" ;
////										break;
////									case Cursor.FIELD_TYPE_INTEGER:         //1
////										@SuppressLint("Range") int cInt = rCursor.getInt(cPosition);
////										dbMsg += cInt+"【int】";
////										cVal=String.valueOf(cInt);
////										break;
////									case Cursor.FIELD_TYPE_FLOAT:         //2
////										@SuppressLint("Range") float cFlo = rCursor.getFloat(cPosition);
////										dbMsg += cFlo+"【float】";
////										cVal=String.valueOf(cFlo);
////										break;
////									case Cursor.FIELD_TYPE_STRING:          //3
////										cVal = rCursor.getString(cPosition);
////										dbMsg +=  cVal+"【String】";
////										break;
////									case Cursor.FIELD_TYPE_BLOB:         //4
////										//@SuppressLint("Range") String cBlob = String.valueOf(cursor.getBlob(cPosition));
////										cVal ="【Blob】";
////										break;
////									default:
////										cVal = String.valueOf(rCursor.getString(cPosition));
////										dbMsg +=  cVal;
////										break;
////								}
////							}
////							dbMsg += "="+cVal;
////							objMap.put(cName ,cVal );
////						}
////					}
////					MediaItem mItem =  new MediaItem.Builder().setUri((String) objMap.get(MediaStore.Audio.Playlists.Members.DATA)).build();
////				//20Aip
//
////				}
////			}else{
////				retList = Collections.emptyList();
////			}
//
//            String action = intent.getAction();
//            boolean actionIsListView = IntentUtil.ACTION_VIEW_LIST.equals(action);
//            if (!actionIsListView && !IntentUtil.ACTION_VIEW.equals(action)) {
//                showToast("action");			//R.string.unexpected_intent_action,
//                finish();
//                return Collections.emptyList();
//            }
//
//            List<MediaItem> mediaItems = mediaItemList;
////			List<MediaItem> mediaItems = createMediaItems(intent, DemoUtil.getDownloadTracker(this));
//            dbMsg += ",mediaItems=" + mediaItems.size() + "件";
//            for (int i = 0; i < mediaItems.size(); i++) {
//                MediaItem mediaItem = mediaItems.get(i);
//
//                if (!Util.checkCleartextTrafficPermitted(mediaItem)) {
//                    showToast("Cleartext HTTP traffic not permitted. See https://exoplayer.dev/issues/cleartext-not-permitted");			//R.string.error_cleartext_not_permitted
//                    finish();
//                    return Collections.emptyList();
//                }
//                if (Util.maybeRequestReadExternalStoragePermission(/* activity= */ this, mediaItem)) {
//                    // The player will be reinitialized if the permission is granted.
//                    return Collections.emptyList();
//                }
//
//                MediaItem.DrmConfiguration drmConfiguration = mediaItem.localConfiguration.drmConfiguration;
//                if (drmConfiguration != null) {
//                    if (Build.VERSION.SDK_INT < 18) {
//                        showToast("DRM content not supported on API levels below 18");		//R.string.error_drm_unsupported_before_api_18
//                        finish();
//                        return Collections.emptyList();
//                    } else if (!FrameworkMediaDrm.isCryptoSchemeSupported(drmConfiguration.scheme)) {
//                        showToast("This device does not support the required DRM scheme");			//R.string.error_drm_unsupported_scheme
//                        finish();
//                        return Collections.emptyList();
//                    }
//                }
//            }
//            dbMsg += ",最終" + retList.size() + "件";
//            return mediaItems;
//        } catch (Exception e) {
//            myErrorLog(TAG ,  dbMsg + "で" + e);
//        }
//        return retList;
//    }

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
    protected boolean initializePlayer() {
        final String TAG = "initializePlayer";
        String dbMsg="";
        try {
            if (exoPlayer == null) {
       //         Intent intent = getIntent();
                dbMsg="mediaItemList=" + mediaItemList.size() + "件";

                lastSeenTracks = Tracks.EMPTY;
                exoPlayer = new ExoPlayer.Builder( MusicService.this).build();
//                if(NowSavedInstanceState != null){
//                    trackSelectionParameters =
//                            TrackSelectionParameters.fromBundle(
//                                    NowSavedInstanceState.getBundle("track_selection_parameters"));
//                    exoPlayer.setTrackSelectionParameters(trackSelectionParameters);
//                }
            //    exoPlayer.addListener(new PlayerEventListener());
                ///https://www.jisei-firm.com/android_develop44/#toc2///////
                exoPlayer.addListener(new Player.Listener() {
                    @Override
                    public void onEvents(@NonNull Player player, @NonNull Player.Events events) {
                        List<Integer> eventList = new ArrayList<>();
                        for (int i = 0 ; i < events.size(); i++)
                            eventList.add(events.get(i));
                        if (eventList.size() > 2) {
                            if (eventList.get(0) == 4 && eventList.get(1) == 7 && eventList.get(2) == 11 && exoPlayer.getContentPosition() == 0) {
                                // SEEK_TO_PREVIOUS
                                Intent intent = new Intent("SEND_MESSAGE");
                    //            intent.putExtra("MUSIC", RWD);
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                            }
                        }
                        Player.Listener.super.onEvents(player, events);
                    }
                    @Override
                    public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                        if (exoPlayer.getCurrentMediaItemIndex() == 1) {
                            // SEEK_TO_NEXT
                            Intent intent = new Intent("SEND_MESSAGE");
                     //       intent.putExtra("MUSIC", FWD);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                        }
                        Player.Listener.super.onMediaItemTransition(mediaItem, reason);
                    }
                });
                ////////https://www.jisei-firm.com/android_develop44/#toc2//
                exoPlayer.addAnalyticsListener(new EventLogger());
                exoPlayer.setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true);
                exoPlayer.setPlayWhenReady(startAutoPlay);
                playerView.setPlayer(exoPlayer);
                configurePlayerWithServerSideAdsLoader();
                //		debugViewHelper = new DebugTextViewHelper(exoPlayer, lp_artist);			//debugTextView
                //		debugViewHelper.start();
            }
//			boolean haveStartPosition = startItemIndex != C.INDEX_UNSET;
//			if (haveStartPosition) {
//				exoPlayer.seekTo(startItemIndex, startPosition);
//			}
            mediaSession = new MediaSession.Builder(getApplicationContext(), exoPlayer).build();
            exoPlayer.setMediaItems(mediaItemList, true);
            //	exoPlayer.playWhenReady = true;
            dbMsg += "[" +mIndex + "/" + mediaItemList.size() + "件]" + saiseiJikan + "から";
            exoPlayer.seekTo(mIndex, saiseiJikan); //特定のアイテムの特定の位置から開始
            exoPlayer.prepare();
            updateButtonVisibility();
            CreateNotification();
            myLog(TAG,dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return true;
    }
    ///////// https://github.com/androidx/media/blob/release/demos/main/src/main/java/androidx/media3/demo/main/PlayerActivity.java
///ゆるプログラミング日記 〈kotlin〉ExoPlayer////// https://mtnmr.hatenablog.com/entry/2022/09/30/113118



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
     *　onStartCommandより先
     * extends MediaBrowserServiceCompat だとActiviteyと同様のあつかいになる？
     *
     * */
    @Override
    public void onCreate() {											//①ⅸ
        super.onCreate();
        final String TAG = "onCreate";
        String dbMsg="";
        try{
            ORGUT = new OrgUtil();		//自作関数集

    //        mediaSession = new MediaSession.Builder(getApplicationContext(), exoPlayer).build();
      //      sessionToken = mediaSession.getSessionCompatToken();
//            MediaSession.ControllerInfo controllerInfo;
//            mediaSession=onGetSession(controllerInfo: MediaSession.ControllerInfo);
       //       mediaSession.getSessionCompatToken();//  .getSessionToken()

/****MediaSessionCompat
            // Create a MediaSessionCompat
            String LOG_TAG = getResources().getString(R.string.app_name);
            mediaSession = new MediaSessionCompat(getApplicationContext(), LOG_TAG);
            // Enable callbacks from MediaButtons and TransportControls
            mediaSession.setFlags(
                    MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |					//ヘッドフォン等のボタンを扱う
                            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);	//再生、停止、スキップ等のコントロールを提供

            //このMediaSessionが提供する機能を設定
            stateBuilder = new PlaybackStateCompat.Builder()
                    .setActions(
                            PlaybackStateCompat.ACTION_PLAY |
                                    PlaybackStateCompat.ACTION_PLAY_PAUSE);
            mediaSession.setPlaybackState(stateBuilder.build());

            //クライアントからの操作に応じるコールバックを設定
            mediaSession.setCallback(new MySessionCallback());

            // Set the session's token so that client activities can communicate with it.
            setSessionToken(mediaSession.getSessionToken());
 */
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
            if(mediaSession != null){
                exoPlayer.release();
                mediaSession.release();
                mediaSession = null;
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