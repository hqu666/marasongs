package com.hijiyam_koubou.marasongs;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;

import java.util.ArrayList;
import java.util.List;

public class MusicService extends MediaBrowserServiceCompat {
    private static final String MY_MEDIA_ROOT_ID = "media_root_id";
    private static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";

    public ExoPlayer exoPlayer;				//音楽プレイヤーの実体
    private MediaSession mediaSession;            //MediaSessionCompat
    private PlaybackStateCompat.Builder stateBuilder;
    private List<MediaBrowserCompat.MediaItem> mediaItems;
    private String channelId = "default";
    final int NOTIFICATION_ID = 1;						//☆生成されないので任意の番号を設定する	 The ID we use for the notification (the onscreen alert that appears at the notification area at the top of the screen as an icon -- and as text as well if the user expands the notification area).

    public MusicService() {
        final String TAG = "MusicService";
        String dbMsg="";
        try{
            myLog(TAG,dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG,dbMsg+"で"+e);
        }
    }

    /////////////////////プレイヤーの状態をクライアントに通知する//
    ///通知の作成////////////////////
    //通知を作成、サービスをForegroundにする
    private void CreateNotification() {
        final String TAG = "CreateNotification";
        String dbMsg="";
        try{
//            dbMsg +=" ,mSession=" + mSession.toString();
//            MediaControllerCompat controller = mSession.getController();
//            MediaMetadataCompat mediaMetadata = controller.getMetadata();
//            dbMsg +=" ,mediaMetadataのsize=" + mediaMetadata.size();
//
//            if (mediaMetadata == null && !mSession.isActive()) return;
//
//            MediaDescriptionCompat description = mediaMetadata.getDescription();
//            dbMsg +=" ,description=" + description.toString();
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
//            String mediaID = description.getMediaId();
//            dbMsg +=" ,[" + mediaID + "]";
//            CharSequence nowTitle = description.getTitle();
//            dbMsg +=" ,nowTitle=" + nowTitle;
//            CharSequence nowSubtitle = description.getSubtitle();
//            dbMsg +=" ,nowSubtitle=" + nowSubtitle;
//            CharSequence nowDescription = description.getDescription();
//            dbMsg +=" ,nowDescription=" + nowDescription;
//            Bitmap iconBitmap = description.getIconBitmap();
//            if(iconBitmap == null){
//                dbMsg +=" ,iconBitmap=null";
//                iconBitmap=mLibrary.getAlbumBitmap(this.rContext,mediaID);
//                dbMsg +=">>";
//            }
//            dbMsg +=" ,iconBitmap(" + iconBitmap.getWidth() + " × " + iconBitmap.getHeight() + ")";
            // Get the session's metadata
            List<MediaSession.ControllerInfo> controller = mediaSession.getConnectedControllers();

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
//            dbMsg +=" ,builder=" + builder.toString();
//            startForeground(NOTIFICATION_ID, builder.build());
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
//                // Returns a root ID that clients can use with onLoadChildren() to retrieve
//                // the content hierarchy.
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
    public void onLoadChildren(@NonNull String parentMediaId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        final String TAG = "onLoadChildren";
        String dbMsg="";
        try{
            //  Browsing not allowed
            if (TextUtils.equals(MY_EMPTY_MEDIA_ROOT_ID, parentMediaId)) {
                result.sendResult(null);
                return;
            }

            // Assume for example that the music catalog is already loaded/cached.
            mediaItems = new ArrayList<>();

            // Check if this is the root menu:
            if (MY_MEDIA_ROOT_ID.equals(parentMediaId)) {
                // Build the MediaItem objects for the top level,
                // and put them in the mediaItems list...
            } else {
                // Examine the passed parentMediaId to see which submenu we're at,
                // and put the children of that menu in the mediaItems list...
            }
            result.sendResult(mediaItems);
            myLog(TAG,dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG,dbMsg+"で"+e);
        }
    }


    /**サービスが最初に作成されたときに 1 回限りのセットアップ処理を行う
     *
     * extends MediaBrowserServiceCompat だとActiviteyと同様のあつかいになる？
     *
     * */
    @Override
    public void onCreate() {											//①ⅸ
        super.onCreate();
        final String TAG = "onCreate";
        String dbMsg="";
        try{

            // EXOPLAYER
            exoPlayer = new ExoPlayer.Builder(getApplicationContext())
                    .setHandleAudioBecomingNoisy(true)
                    .build();

            mediaSession = new MediaSession.Builder(getApplicationContext(), exoPlayer).build();
            // Create a MediaSessionCompat

/****MediaSessionCompat
       //     mediaSession = new MediaSessionCompat(getApplicationContext(),  getResources().getString(R.string.app_name));

            // Enable callbacks from MediaButtons and TransportControls
            mediaSession.setFlags(
                    MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

            // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
            stateBuilder = new PlaybackStateCompat.Builder()
                    .setActions(
                            PlaybackStateCompat.ACTION_PLAY |
                                    PlaybackStateCompat.ACTION_PLAY_PAUSE);
            mediaSession.setPlaybackState(stateBuilder.build());

            // MySessionCallback() has methods that handle callbacks from a media controller
            mediaSession.setCallback(new MySessionCallback());

            // Set the session's token so that client activities can communicate with it.
            setSessionToken(mediaSession.getSessionToken());
 **/
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
       //     mediaSession?.run {
                exoPlayer.release();
            mediaSession.release();
                mediaSession = null;
     //       }
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