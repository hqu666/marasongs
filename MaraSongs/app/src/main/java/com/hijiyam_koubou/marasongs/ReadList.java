package com.hijiyam_koubou.marasongs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReadList extends Activity {
//AlertDialog implements DialogInterface
    public OrgUtil ORGUT;						//自作関数集
    public Util UTIL;
    private ReadList.ploglessTask plTask;

    public AlertDialog plogDialogView;
    public LinearLayout progress_dlog_ll;
    public ProgressBar progress_pb;
    public TextView progress_message_tv ;
    public TextView progress_titol_tv ;

    public Context gContext;
    public String pdTitol;
    public String pdMessage;
    public int pdMaxVal;
    private int pdCoundtVal=0;					//ProgressDialog表示値
    public int leadlistId;
    public String leadlistName;


    public int  reqCode;
    public Cursor rCursor;
    public List<String> plSL;					//プレイリスト用簡易リスト
    public List<Map<String, Object>> plAL;		//プレイリスト用ArrayList
    public Map<String, Object> objMap;				//汎用マップ

    @Override
    public void onCreate(Bundle savedInstanceState) {                                    //①起動
        super.onCreate(savedInstanceState);
        final String TAG = "onCreate";
        String dbMsg = "";
        try{
            ORGUT = new OrgUtil();		//自作関数集
            UTIL = new Util();

            Intent intent = getIntent();
            reqCode=intent.getIntExtra("reqCode",0);
            dbMsg += ",reqCode=" + reqCode;
            leadlistId = intent.getIntExtra("leadlistId", 0);
            leadlistName=intent.getStringExtra("leadlistName");
            dbMsg += "[" + leadlistId+ "]" + leadlistName;
            pdTitol=intent.getStringExtra("pdTitol");
            pdMessage= leadlistName;                    //intent.getStringExtra("pdMessage");
            pdMaxVal=intent.getIntExtra("pdMaxVal",100);
            dbMsg += ",pdTitol=" + pdTitol+ ",pdMessage=" + pdMessage+ ",pdMaxVal=" + pdMaxVal;

            setContentView(R.layout.dialog_progress);				//			setContentView(R.layout.main);
            Window gWindow = this.getWindow();
            gWindow.setTitle(pdTitol);
            progress_dlog_ll = findViewById(R.id.progress_dlog_ll);
            progress_pb = findViewById(R.id.progress);
            progress_titol_tv = findViewById(R.id.progress_titol);
            progress_titol_tv.setText(pdTitol);
            progress_message_tv = findViewById(R.id.progress_message);
            progress_message_tv.setText(pdMessage);
            progress_pb.setMax(pdMaxVal);
            pdCoundtVal=0;

            plTask  = new ReadList.ploglessTask(this);

            if(leadlistName.equals(getResources().getString(R.string.listmei_zemkyoku))){

            }else{
                CreatePLList( leadlistId , pdMessage);
            }
            myLog(TAG,dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG,dbMsg + "で"+e.toString());
        }
    }

//    protected ReadList(Context context,String dTitol ,String dMessage ,int maxVal) {
//        super(context);
//        final String TAG = "ReadList";
//        String dbMsg = "";
//        try{
//            ORGUT = new OrgUtil();		//自作関数集
//            UTIL = new Util();
//
//            gContext = context;
//            pdTitol = dTitol;
//            pdMessage = dMessage;
//            pdMaxVal = maxVal;
//            dbMsg += ",pdTitol=" + pdTitol+ ",pdMessage=" + pdMessage+ ",pdMaxVal=" + pdMaxVal;
//            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( gContext );		// アラートダイアログのタイトルを設定します 	getApplicationContext()
//            alertDialogBuilder.setTitle(pdTitol);
//            progress_message_tv = new TextView(gContext);
//            alertDialogBuilder.setView(progress_message_tv);// アラートダイアログのメッセージを設定します
//            progress_message_tv.setText(dMessage);
//            progress_pb = new ProgressBar(gContext);
//            alertDialogBuilder.setView(progress_pb);// アラートダイアログのメッセージを設定します
//            progress_pb.setMax(pdMaxVal-1);
//            progress_pb.setProgress(0);
////
////            plogDialog = new Builder(this)
////                    .setView(plogDialogView)
////                    .create();
////            plogDialog.show();
//            alertDialogBuilder.setCancelable(true);// アラートダイアログのキャンセルが可能かどうかを設定します
//            plogDialogView = alertDialogBuilder.create();	// アラートダイアログを表示します
//            plogDialogView.setCanceledOnTouchOutside(false);	//背景をタップしてもダイアログを閉じない
//            plogDialogView.show();
//            //	plTask.execute(reqCode,playLists,pdTitol,pdMessage,maxVal);
//            myLog(TAG,dbMsg);
//        }catch (Exception e) {
//            myErrorLog(TAG,dbMsg + "で"+e.toString());
//        }
//    }

    public void setMax(int maxInt) {
        progress_pb.setMax(maxInt);
    }

    public int getMax() {
        return progress_pb.getMax();
    }

    ////////////////////////////////////////////
    /**汎用プレイリスト(.ｍ3ｐ)をCursorに読み込む*/
    public void CreatePLList( long playlistId , String pdMessage){		//playlistIdで指定したMediaStore.Audio.Playlists.Membersの内容取得		String volumeName,
        final String TAG = "CreatePLList";
        String dbMsg = "";
        try{
            dbMsg += ORGUT.nowTime(true,true,true) + dbMsg;/////////////////////////////////////
            long start = System.currentTimeMillis();		// 開始時刻の取得
            dbMsg += "選択されたプレイリスト[ID="+playlistId + "]" + this.leadlistName ;
            final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
            final String[] columns = null;			//{ idKey, nameKey };
            String c_orderBy = MediaStore.Audio.Playlists.Members.PLAY_ORDER;
            rCursor= this.getContentResolver().query(uri, columns, null, null, c_orderBy );
            dbMsg += ",rCursor=" + rCursor.getCount() + "件";
            if( rCursor.moveToFirst() ){
                Util UTIL = new Util();
                UTIL.dBaceColumnCheck(rCursor ,0);
                this.plAL = new ArrayList<Map<String, Object>>();
                this.plAL.clear();
                this.plSL =  new ArrayList<String>();				//プレイリスト用簡易リスト
                this.plSL.clear();
                reqCode = MyConstants.PUPRPOSE_lIST;	//MENU_TAKAISOU ;			//多階層リスト選択選択中
                dbMsg += ",reqCode="+reqCode;
                int koumoku = rCursor.getColumnCount();
                String pdTitol = getResources().getString(R.string.pref_playlist) +"" + getResources().getString(R.string.common_yomitori);				//読み込み
                pdMaxVal = rCursor.getCount();
                plTask.execute(reqCode,rCursor,pdTitol,pdMessage,pdMaxVal);
            }else{
                dbMsg += "MediaStore.Audio.Playlists以外";
                AlertDialog.Builder Dlg = new AlertDialog.Builder(this);			// リスト表示用のアラートダイアログ
                Dlg.setTitle(getResources().getString(R.string.pref_playlist));		// プレイリスト</string>
                Dlg.setMessage(getResources().getString(R.string.pl_nasi_meg));		//ご指定のプレイリストは曲が登録されていないか、読み込めませんでした。
                Dlg.setNegativeButton(getResources().getString(R.string.comon_kakuninn),	//確認
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                Dlg.setCancelable(false);
                Dlg.create().show();			// 表示
            }
            myLog(TAG, dbMsg);
        }catch(IllegalArgumentException e){
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
    }

    /**
     *  Cursorで渡されたプレイリストレコードから内容を取得し、plALに書き込む */
    public Cursor CreatePLListBody(Cursor playLists) throws IOException {		//プレイリストの内容取得
        final String TAG = "CreatePLListBody";
        String dbMsg = "";
        try{
            int rPosi = playLists.getPosition();
            dbMsg= "[" + rPosi +"/" + playLists.getCount() +"曲]";		//this.rCount
            String subText = null;
            String ArtistName = null;
            String AlbumArtistName = null;
            String AlbumName = null;
            String songTitol = null;
            String Dur = null;
            this.objMap = new HashMap<String, Object>();
            for(int i = 0 ; i < playLists.getColumnCount() ; i++ ){				//this.koumoku
                String cName = playLists.getColumnName(i);
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
                    dbMsg += "は読み込めない";
                }else{
                    if( cName.equals("album_artist")){		//[26/37]
                        String cVal = playLists.getString(i);
                        if(cVal != null){
                            cVal = cVal;
                        }
                        dbMsg +=  "="+cVal;
                        AlbumArtistName = cVal;
                        this.objMap.put(cName ,cVal );
                    }else if( cName.equals(MediaStore.Audio.Playlists.Members.ARTIST)){		//[33/37]artist=Santana
                        String cVal = playLists.getString(i);
                        if(cVal != null){
                            cVal = cVal;
                        }else{
                            cVal = getResources().getString(R.string.bt_unknown);			//不明
                        }
                        dbMsg +=  "="+cVal;
                        ArtistName = cVal;
                        this.objMap.put(cName ,cVal );
                    }else if( cName.equals(MediaStore.Audio.Playlists.Members.ALBUM)){		//[33/37]artist=Santana
                        String cVal = playLists.getString(i);
                        if(cVal != null){
                            cVal = cVal;
                        }else{
                            cVal = getResources().getString(R.string.bt_unknown);			//不明
                        }
                        dbMsg +=  "="+cVal;
                        AlbumName = cVal;
                        this.objMap.put(cName ,cVal );
                    }else if( cName.equals(MediaStore.Audio.Playlists.Members.TITLE)){		//[12/37]title=Just Feel Better
                        String cVal = playLists.getString(i);
                        if(cVal != null){
                            cVal = cVal;
                        }else{
                            cVal = getResources().getString(R.string.bt_unknown);			//不明
                        }
                        this.objMap.put(cName ,cVal );
                        this.objMap.put("main" ,cVal );
                        dbMsg +=  "="+cVal;
                        this.plSL.add(cVal);
                        songTitol = cVal;
                    }else if( cName.equals(MediaStore.Audio.Playlists.Members.DURATION)){	//[14/37]duration=252799>>04:12 799
                        String cVal = playLists.getString(i);
                        dbMsg +=  "="+cVal;
                        if(cVal != null){
                            cVal = cVal;
                        }
                        this.objMap.put(cName ,cVal );
                        Dur = "["+ ORGUT.sdf_mss.format(Long.valueOf(cVal)) + "]";
                        dbMsg +=  ">>"+Dur;
                    }else if( cName.equals(MediaStore.Audio.Playlists.Members.DATA)){	//[5/37]_data=/storage/sdcard0/external_sd/Music/Santana/All That I Am/05 Just Feel Better.wma
                        String cVal = playLists.getString(i);
                        if(cVal != null){
                            cVal = cVal;
                        }
                        this.objMap.put("DATA" ,cVal );
//                        this.saisei_fnameList.add(cVal);
                        dbMsg +=  "="+cVal;
                    }else if( cName.equals(MediaStore.Audio.Playlists.Members.TRACK)){
                        String cVal = playLists.getString(i);
                     //   cVal = UTIL.checKTrack( cVal);
                        this.objMap.put(cName ,cVal );
                    }else if( cName.equals(MediaStore.Audio.Playlists.Members.ALBUM_ID)){
                        String cVal = String.valueOf(playLists.getInt(i));
                        this.objMap.put(cName ,cVal );
                        dbMsg +=  "="+cVal;
                    }else{
                        int cPosition = playLists.getColumnIndex(cName);
                        dbMsg += "『" + cPosition+"』";
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
                                    dbMsg += cInt+"【int】";
                                    cVal=String.valueOf(cInt);
                                    break;
                                case Cursor.FIELD_TYPE_FLOAT:         //2
                                    @SuppressLint("Range") float cFlo = playLists.getFloat(cPosition);
                                    dbMsg += cFlo+"【float】";
                                    cVal=String.valueOf(cFlo);
                                    break;
                                case Cursor.FIELD_TYPE_STRING:          //3
                                    cVal = playLists.getString(cPosition);
                                    dbMsg +=  cVal+"【String】";
                                    break;
                                case Cursor.FIELD_TYPE_BLOB:         //4
                                    //@SuppressLint("Range") String cBlob = String.valueOf(cursor.getBlob(cPosition));
                                    cVal ="【Blob】";
                                    break;
                                default:
                                    cVal = String.valueOf(playLists.getString(cPosition));
                                    dbMsg +=  cVal;
                                    break;
                            }
                        }
                        dbMsg += "="+cVal;
                        this.objMap.put(cName ,cVal );
                    }
                }
            }
            dbMsg +=  ",Dur="+Dur;
            subText = ArtistName + " " +Dur;
            dbMsg +=  ",subText="+subText;
            if(subText != null){
                this.objMap.put("sub" ,subText );
            }
            if(AlbumArtistName != null){
                ArtistName = AlbumArtistName;
            }
            this.objMap.put("img" , ORGUT.retAlbumArtUri( this, ArtistName , AlbumName ) );			//アルバムアートUriだけを返す
            this.plAL.add( objMap);
            myLog(TAG, dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg  + "で" + e);
        }
        return playLists;
    }

    /**プログレスを更新して、maxに達すればダイアログを閉じる*/
    public void setProgVal(int progInt) {
        final String TAG = "setProgVal";
        String dbMsg = "";
        try{
            pdCoundtVal = progInt;
            dbMsg += "pdCoundtVal=" + progress_pb.getProgress();
            progress_pb.setProgress(pdCoundtVal);
            dbMsg += ">>" + progress_pb.getProgress()+ "/" + progress_pb.getProgress();
            if(progress_pb.getMax()<=progress_pb.getProgress()){
                plogDialogView.dismiss();
                dbMsg += ">>dismiss" ;

            }
            myLog(TAG,dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG,dbMsg + "で"+e.toString());
        }
    }

    public int getProgress() {
        return progress_pb.getProgress();
    }

    public void setTitle(String titleStr) {
        progress_titol_tv.setText(titleStr);
    }

    public void setMessage(String msgStr) {
        progress_message_tv.setText(msgStr);
    }

    public void dismiss() {
        plogDialogView.dismiss();
    }
    long startPart;		// 開始時刻の取得
    public static final String EXTRA_MESSAGE = "0";

    /**
     * public class plogTask extends AsyncTask<Object, Integer , AsyncTaskResult<Integer>>の置き換え
     * Android 11でAsyncTaskがdeprecated
     * サンプルは　 MyTask　https://akira-watson.com/android/asynctask.html
     * */
    public class ploglessTask {
        ExecutorService executorService;
        private Context cContext = null;
        Intent intentDP;
        SQLiteDatabase writeDB;
        SQLiteDatabase readDB;

        public int reqCode = 0;						//処理番号
        public Cursor cCursor;
        public String pdTitol;			//ProgressDialog のタイトルを設定
        public String pdMessage;			//ProgressDialog のメッセージを設定

        public String pdMessage_stok;			//ProgressDialog のメッセージを設定
        public int pdMaxVal = 0;					//ProgressDialog の最大値を設定 (水平の時)
        public int pdStartVal=0;					//ProgressDialog の初期値を設定 (水平の時)
        public int pdCoundtVal=0;					//ProgressDialog表示値
        public String _numberFormat = "%d/%d";
        public NumberFormat _percentFormat = NumberFormat.getPercentInstance();
        double num;

        /**
         * 指定したデータベースをWritableで開くか作成する
         * */
        public SQLiteDatabase MakeOrOpenDataBase(String fn , SQLiteDatabase db){
            final String TAG = "MakeOrOpenDataBase";
            String dbMsg= "[AllSongs]";
            try{
                startPart = System.currentTimeMillis();		// 開始時刻の取得
                dbMsg=ORGUT.nowTime(true,true,true) + dbMsg;/////////////////////////////////////
                System.currentTimeMillis();
                cContext.getContentResolver();
                dbMsg += ",db=" + fn;
//                if(db != null){									//全曲の仮ファイル	kari.db
//                    dbMsg += ",isOpen=" + db.isOpen()+",isReadOnly=" + db.isReadOnly();
//                    if(db.isOpen()){
//                        db.close();
//                    }
//                    db = zenkyokuHelper.getWritableDatabase();			// データベースをオープン
//                }else{
                ZenkyokuHelper zenkyokuHelper = new ZenkyokuHelper(cContext.getApplicationContext() , fn);		//全曲リストの定義ファイル		.this.cContext.
                db = zenkyokuHelper.getWritableDatabase();			// データベースをオープン
//                    db = cContext.openOrCreateDatabase(fn, MODE_PRIVATE , null);	// | Context.MODE_ENABLE_WRITE_AHEAD_LOGGING Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE, String path, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler				//アーティスト名のえリストファイルを読み書きモードで開く
                dbMsg += ">作り直し>" + cContext.getDatabasePath(fn).getPath();	///data/data/com.hijiyam_koubou.marasongs/databases/artist.db
//                }
                dbMsg += ">>isOpen=" + db.isOpen()+",isReadOnly=" + db.isReadOnly();
                myLog(TAG,dbMsg);
            }catch(IllegalArgumentException e){
                myErrorLog(TAG,dbMsg +"で"+e.toString());
            }catch (Exception e) {
                myErrorLog(TAG,dbMsg +"で"+e.toString());
            }
            return db;
        }

        /**
         * Transactionの終端
         * */
        public void endTS(SQLiteDatabase sql_db) {
            final String TAG = "endTS";
            String dbMsg="";
            try{
                try{
                    dbMsg= "sql_db = " + sql_db;//////
                    sql_db.setTransactionSuccessful();
                } finally {
                    sql_db.endTransaction();
                }
                sql_db.close();
                myLog(TAG,dbMsg);
            } catch (Exception e) {
                myErrorLog(TAG,dbMsg+"；"+e.toString());
            }
        }

        /**
         * プログレス表示クラスのコンテキスト
         * */
        public ploglessTask(Context context) {
            super();
            final String TAG = "ploglessTask[ploglessTask]";
            String dbMsg="";
            try {
                executorService  = Executors.newSingleThreadExecutor();
                this.cContext = context;
                //		this.cCallback = callback;
                myLog(TAG,dbMsg );
            } catch (Exception e) {
                myErrorLog(TAG,dbMsg+" でエラー発生；"+e.toString());
            }
        }

        public class TaskRun implements Runnable {

            @Override
            public void run() {
                final String TAG = "run[ploglessTask]";
                String dbMsg="";
                try {
                    dbMsg +="["+ reqCode + "]";
                    String fn = cContext.getString(R.string.kari_file);			//仮ファイル
                    String zenkyokuTName = getResources().getString(R.string.zenkyoku_table);			//全曲リストのテーブル名
                    SQLiteStatement stmt = null ;			//6；SQLiteStatement
                    dbMsg += "；全曲リストテーブル=" + zenkyokuTName;
                    //		if(cCursor.moveToFirst()) {
                    switch(reqCode) {
//							case pt_CreateAllSongs:		//807;全曲リスト作成
//								fn = cContext.getString(R.string.zenkyoku_file);
//								dbMsg += ",db=" + fn;
//								Zenkyoku_db=MakeOrOpenDataBase( fn ,  Zenkyoku_db);
//								dbMsg +=">>isOpen=" + Zenkyoku_db.isOpen()+",isReadOnly=" + Zenkyoku_db.isReadOnly();
//								Zenkyoku_db.beginTransaction();
//								stmt = null;
//								stmt = Zenkyoku_db.compileStatement("insert into " + zenkyokuTName +
//										"(AUDIO_ID,SORT_NAME,ARTIST,ALBUM_ARTIST,ALBUM,TRACK,TITLE,DURATION,YEAR,DATA,MODIFIED,COMPOSER,LAST_YEAR,ALBUM_ARTIST_INDEX) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
//								new ContentValues();
//								break;
                        case MyConstants.PUPRPOSE_SONG:
//								pdMaxVal = (int) params[2];		//プレイリスト用ArrayList
                            dbMsg += ", pdMaxVal = " + pdMaxVal + "件"  ;
                            break;
                        default:
                            if(cCursor != null) {
                                pdMaxVal = cCursor.getCount();
                            }
                            dbMsg += ", cCursor = " + pdMaxVal + "件"  ;
                            break;
                    }
                    long id = 0;
                    List<String> testList = null;	//最近再生された楽曲のID
                    testList =  new ArrayList<String>();	//最近再生された楽曲のID
                    switch(reqCode) {
                        case MyConstants.PUPRPOSE_SONG:
                            dbMsg += ", pdCoundtVal = " + pdCoundtVal ;
                            for(pdCoundtVal =0 ; pdCoundtVal < pdMaxVal;pdCoundtVal ++){
                                switch(reqCode) {
                                    case MyConstants.PUPRPOSE_SONG:
                                 //       plInfoSinglBody(pdCoundtVal);
                                        break;
                                }
//									publishProgress( pdCoundtVal );		//progressDialog.progBar1.setProgress(step1);
                            }
                            dbMsg += ">>" + pdCoundtVal ;
                            break;
                        default:
                            dbMsg +=";"+ cCursor.getCount() + "件×"+ cCursor.getColumnCount() + "項目";
                            if(cCursor.moveToFirst()){
                                dbMsg +=  "；ループ前；"+ cCursor.getPosition() +"/ " + pdMaxVal;	/////////////////////////////////////////////////////////////
                                @SuppressLint("Range") String delID =null;
                                Uri pUri =null;
                                int delC = 0;
                                int rCol = 0;
                                do{
                                    //				dbMsg +=  "[cursor="+  cCursor.getPosition() +  "/" + pdMaxVal + ";progressDialog=" + progressDialog.getProgress() +"]" ;
                                    dbMsg += ",isClosed=" + cCursor.isClosed() ;
                                    switch(reqCode) {
                                        case MyConstants.PUPRPOSE_lIST:
                                            cCursor = CreatePLListBody(cCursor);		//プレイリストの内容取得
                                            break;
                                    }
                                    pdCoundtVal =  cCursor.getPosition() +1;
//										intentDP.putExtra(EXTRA_MESSAGE, String.valueOf(pdCoundtVal));
                                    //	progressDialog.setProgVal(pdCoundtVal);
                                    dbMsg += "(reqCode=" +reqCode+")pdCoundtVal="+pdCoundtVal + "/" + pdMaxVal ;
                                    long nTime = System.currentTimeMillis() ;
//										if(nTime  > vTime ){
//											publishProgress( pdCoundtVal );		//progressDialog.progBar1.setProgress(step1);
//											if( vtWidth > 1 ){
//												vtWidth = vtWidth/2;
//											}
//											vTime = System.currentTimeMillis() + vtWidth;		// 更新タイミング
//										}
                                }while( cCursor.moveToNext() ) ;				//pdCoundtVal <  pdMaxVal
                            }
                            break;
                    }
                    //	Thrd.sleep(200);			//書ききる為の時間（100msでは不足）

                    dbMsg += ",最終[" + id +"]に追加";///////////////////		AllSongs.this.
//						switch(reqCode) {
//							case pt_CreateKaliList:							//803;仮リスト作成
//								endTS(Kari_db);
//								break;
//							case pt_CreateAllSongs:					//805;全曲リスト作成
//							case pt_CompList:									//806;全曲リストにコンピレーション追加
//								endTS(Zenkyoku_db);
//								break;
//							case pt_artistList:
//								endTS(artist_db);
//								break;
//						}
//						Thread.sleep(300);			//書ききる為の時間（100msでは不足）
                    //	}
                    new Handler(Looper.getMainLooper())
                            .post(() -> onPostExecute());
                    myLog(TAG,dbMsg );
                } catch (Exception e) {
                    myErrorLog(TAG,dbMsg+" でエラー発生；"+e.toString());
                }
            }
        }

        /**
         * ploglessTaskの入口
         * */
        void execute(int req_code , Cursor cursor, String plog_title ,String plog_message,int maxVal) {
            final String TAG = "execute[ploglessTask]";
            String dbMsg="";
            try {
                onPreExecute(req_code , cursor, plog_title ,plog_message,maxVal);
                executorService.submit(new ploglessTask.TaskRun());
                myLog(TAG,dbMsg );
            } catch (Exception e) {
                myErrorLog(TAG,dbMsg+" でエラー発生；"+e.toString());
            }
        }

        /**
         * ploglessTaskの前処理
         * Backgroundメソッドの実行前にメインスレッドで実行
         * */
        void onPreExecute(int req_code , Cursor cursor, String plog_title ,String plog_message,int maxVal) {           //,SQLiteDatabase write_db
            final String TAG = "onPreExecute";
            String dbMsg="[ploglessTask]";
            try {
                this.reqCode = req_code;
                dbMsg += "[" + this.reqCode + "]";
                this.pdTitol = plog_title;
                this.pdMessage = plog_message;
                dbMsg += this.pdTitol + ":" + this.pdMessage ;
                this.cCursor = cursor;
                if(this.cCursor != null){
                    dbMsg += " , " + this.cCursor.getCount() + "件";
                    pdMaxVal = this.cCursor.getCount();
                }else{
                    pdMaxVal = maxVal;
                }
                dbMsg +="getMax=" + pdMaxVal;
                myLog(TAG,dbMsg );
            } catch (Exception e) {
                myErrorLog(TAG,dbMsg+" でエラー発生；"+e.toString());
            }
        }

        /**
         * ploglessTaskの終端処理
         * Backgroundメソッドの実行前にメインスレッドで実行
         * */
        void onPostExecute() {
            final String TAG = "onPostExecute";
            String dbMsg = "";
            try{
                dbMsg +=  "reqCode=" + reqCode;/////////////////////////////////////
                quitMe();
//                switch(reqCode) {
//                    case MyConstants.PUPRPOSE_lIST:
//                        CreatePLListEnd(cCursor);				//プレイリストの内容取得
//                        break;
//                    case MyConstants.PUPRPOSE_SONG:
//                        plWrightEnd();					//プレイリストの描画
//                        break;
////			default:
////				break;
//                }
                myLog(TAG, dbMsg);
            }catch (Exception e) {
                myErrorLog(TAG ,  dbMsg + "で" + e);
            }
        }
    }

    void quitMe() {
        final String TAG = "quitMe";
        String dbMsg = "";
        try{
            Intent intentSub = new Intent();
            dbMsg +=  ",reqCode=" + reqCode;
            intentSub.putExtra("reqCode",reqCode);
            dbMsg += ",plSL=" + plSL.size() + "件";
          //  intentSub.putStringArrayListExtra("plSL", plSL);
            intentSub.putExtra("plSL", (Serializable) plSL);   //  cannot be cast to android.os.Parcelable
            dbMsg += "plAL=" + plAL.size() + "件";
     //       intentSub.putParcelableArrayListExtra("plAL", plAL);
            intentSub.putExtra("plAL", (Serializable) plAL);
            setResult(RESULT_OK, intentSub);
            finish();
            myLog(TAG, dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
    }



    /////////////////////////////////////////////////////////////////////
    public static void myLog(String TAG , String dbMsg) {
        Util UTIL = new Util();
        Util.myLog(TAG , "[ReadList]"+ dbMsg);
    }

    public static void myErrorLog(String TAG , String dbMsg) {
        Util UTIL = new Util();
        Util.myErrorLog(TAG , dbMsg);
    }

}