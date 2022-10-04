package com.hijiyam_koubou.marasongs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import java.io.File;
import java.util.List;
import java.util.Map;

public class FileInfoEdit extends Dialog implements DialogInterface, OnCheckedChangeListener {
//http://d.hatena.ne.jp/abachibi/20100508/1273355363
	OrgUtil ORGUT;		//自作関数集
	private AlertDialog alertDialog;
	private Context ｒContext;		//呼出し元のコンテキスト
	private long start=0;
	public SharedPreferences sharedPref;
	public Editor myEditor ;
//	public String alPFN = "al_pref";
	public ArtistRwHelper arhelper;		//アーティスト名の置き換えリストの定義ファイル
	public SQLiteDatabase ar_db;	//アーティスト名の置き換えリストファイル
	public String awTname;	//置換えアーティストリスト
	public String myFolder ;
	public String file_ex;	//メモリーカードの音楽ファイルフォルダ
	public Cursor awCursor;
	public List<String> reNameList = null;	//アルバム←クレジットアーティスト強制変換リスト

//	private Spinner fie_albam_artist_name_sp;		//（アルバムをまとめる）アーティスト名
	private ImageButton fie_albam_artist_list_bt;		//アーティスト名リスト表示
	private RadioGroup fe_sousa_taisyou;			//操作対象選択
	private RadioButton fie_sousa_taisyou_artist;			//操作対象アーティスト
	private RadioButton fie_sousa_taisyou_album;			//操作対象アルバム
	private RadioButton fie_sousa_taisyou_kyoku;			//操作対象タイトル
	private EditText fie_albam_artist_name_et;		//（アルバムをまとめる）アーティスト名
	private EditText fie_credit_artist_name_et;		//（アルバムに表記されている）アーティスト名
	private TextView fie_albam_tv;					//アルバム
	private TextView fie_year_tv;					//制作もしくは録音年
	private TextView fie_nen_tv;						//年
	private TextView fie_titol_tv;					//タイトル
	private EditText fie_albam_name_et;				//アルバム名
	private EditText fie_year_et;					//アルバムの(制作もしくは録音)年
	private EditText fie_taisyou_track;				//トラック番号
	private EditText fie_taisyou_titol;				//タイトル
	private TextView fie_taisyou_folder;			//アルバムの(制作もしくは録音)年
	public Button fie_nega_btn;					//中止ボタン
	public Button fie_posi_btn ;					//確定ボタン
	public Button fie_kakuninn_btn ;				//確認ボタン

	public int shigot_bangou = 0;
	public static final int dlogKinu = 200;		//ダイアログに機能割付け

	public String rDate;				//データURL
	public int sousa_taisyou;			//操作対象
	public String creditArtistName;		//クレジットされているアーティスト名
	public String artistName =null;		//リストアップしたアルバムアーティスト名
	public String albumName =null;		//アルバム名
	public String releaceYear = null;			//制作年
	public String trackNo =null;		//アルバムアートのURI
	public String titolName =null;		//曲名

	private static CharSequence dTitol = null;		//ダイアログタイトル
	private static CharSequence dMessage = null;		//アラート文
	private static CharSequence Msg1 = null;			//ボタン1のキーフェイス
	private static CharSequence Msg2 = null;			//ボタン2のキーフェイス
	private static CharSequence Msg3 = null;			//ボタン3のキーフェイス
	private static boolean isIn = false;				//入力
	private static boolean isList = false;				//リスト
	private static boolean multiChoice = false;		//複数選択リスト
	private static int InType =0;						//入力制限
	private static CharSequence[] listItems= null;			//リストアイテム
	private static String[] listIDs = null;				//リストアイテムのID
	boolean[] checkedItems = null;						//選択リストの配列
	private static String motoN = null;	//元データなど
//	private static String accName = null;				//登録先名
//	private static String accType = null;				//登録先タイプ
//	private static String gID = null;				//書き込み先のグループID
	private int retInt;


	public void readPref() {        //プリファレンスの読込み
		final String TAG = "readPref";
		String dbMsg = "[MuList]";
		try {
			MyPreferences myPreferences = new MyPreferences();
			dbMsg += "MyPreferencesy読込み";
			myPreferences.readPrif(ｒContext);
			sharedPref = MyPreferences.sharedPref;
			myEditor =myPreferences.myEditor;
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}																	//設定読込・旧バージョン設定の消去


	protected FileInfoEdit(Context context , int taisyou , String data , String cAName , String AName , String album , String rYear, String titol) {
		//呼出し元のコンテキスト、クレジットされているアーティスト名,クレジットされているアーティスト名,
		super(context);
		final String TAG = "FileInfoEdit[FileInfoEdit]";
		String dbMsg="";
		try{
			OrgUtil ORGUT = new OrgUtil();		//自作関数集

			start=System.currentTimeMillis();
			ｒContext = context;		//呼出し元のコンテキスト
			sousa_taisyou = taisyou;			//操作対象
			if(cAName != null){
				creditArtistName = cAName;		//クレジットされているアーティスト名
			}
			dbMsg="クレジット= " + creditArtistName;
			if(AName != null){
				artistName =AName;		//リストアップしたアルバムアーティスト名
			}
			dbMsg +=";;" + artistName;
			if(album != null){
				albumName =album;		//アルバム名
			}
			if(rYear != null && ! rYear.equals("0")){
				if(! rYear.equals("0")){
					releaceYear = rYear;			//制作年
				}else{
					releaceYear = null;
				}
			}
			dbMsg +=" , 制作年= " + releaceYear + ",アルバム= " + albumName;
			if(titol != null){
				titolName =titol;		//"制作年= " + rYear
			}
			if( data != null ){
				rDate = data;				//データURL
				Map<String, String> map = ORGUT.data2msick(data , context);
				String val = map.get( "cArtistName" ); // 指定したキーに対応する値を取得. キャスト不要
				if(creditArtistName == null){
					if( val != null){
						creditArtistName = val;
					}
				}else if( val != null){
					if(creditArtistName.length() < val.length()){
						creditArtistName = val;
					}
				}
				if(artistName == null){
					artistName = creditArtistName;
				}else if( val != null){
					if(creditArtistName.length() < artistName.length()){
						creditArtistName = artistName;
						artistName = val;
					}				}
				dbMsg= "クレジット= " + creditArtistName+ ";;" + artistName;
				val = map.get( "Alnbum" );
				if(val != null){
					if(albumName == null){		//アルバム名
						albumName = val;
					}
				}
				dbMsg +=",アルバム名= " + albumName;
				val = map.get( "trackNo" );
				if(trackNo == null){
					if(val != null){
						trackNo = val;
					}
				}
				dbMsg +="[" + trackNo + "]";
				val = map.get( "titolName" );
				if(titolName == null){
					if(val != null){
						titolName = val;		//曲名
					}
				}
				dbMsg=dbMsg  + titolName;
				val = map.get( "kakucyousi" );
				if(val != null){
					dbMsg +=" , 拡張子="  + val;
				}
				val = map.get( "mPass" );
				if(val != null){
					dbMsg +=" , 残りパス="  + val;
				}
			}
			myLog(TAG,dbMsg);
			url2FSet(data , AName);	//urlからプレイヤーの書き込みを行う　起動時のプリファレンスから
		//	Intent rData;
		} catch (Exception e) {
			myErrorLog(TAG, dbMsg + "で"+e);
		}
	}

	@SuppressLint("Range")
	public void url2FSet(String urlStr , String artistMei){		//urlからプレイヤーの書き込みを行う　起動時のプリファレンスから
		final String TAG = "url2FSet[FileInfoEdit]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			boolean senntakusinaosi = true;			//曲選択から
	//		dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			dbMsg=urlStr;/////////////////////////////////////
			if(urlStr == null){
				senntakusinaosi = true;			//曲選択から
			}else{
				File findF = new File(urlStr);
				dbMsg= dbMsg +";exists="+ findF.exists();/////////////////////////////////////
				String[] nameSet = urlStr.split(File.separator);	//URLから
				creditArtistName = nameSet[nameSet.length-3];	//アーティスト名などを読み取る
				dbMsg= dbMsg +";credit;"+ creditArtistName;/////////////////////////////////////
		//		String artistListName = creditArtistLisName;		//選択中アルバムアーティスト名
				String albumListName = nameSet[nameSet.length-2];		//選択中アルバム名
				String titolListName = nameSet[nameSet.length-1];		//選択中曲名

				if(findF.exists()){					//そのファイルが実在すれば
					ContentResolver resolver = ｒContext.getContentResolver();	//c.getContentResolver();
					Uri cUri;
					if ( Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT) {
						cUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
					} else {
						cUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
					}
					String[] c_columns = null;		 		//③引数columnsには、検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
					String c_selection =  MediaStore.Audio.Media.DATA +" = ? ";			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
					String[] c_selectionArgs= {urlStr};   			//⑥引数groupByには、groupBy句を指定します。
					String c_orderBy=MediaStore.Audio.Media.DATA; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
					Cursor cursor = resolver.query( cUri , c_columns , c_selection , c_selectionArgs, c_orderBy);
					dbMsg= dbMsg +">"+ cursor.getCount() + "件";/////////////////////////////////////
					if(cursor.moveToFirst()){
						String rStr = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
						if(creditArtistName.length() < rStr.length()){
							artistName = creditArtistName;
							creditArtistName = rStr;		//クレジットされているアーティスト名
						}else{
							artistName =rStr;
						}
						dbMsg= dbMsg +", artist = " + artistName + "[" + creditArtistName + "]";/////////////////////////////////////
						if(albumName == null){
							albumName =cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));		//アルバム名
						}
						dbMsg= dbMsg +", album = " + albumName ;/////////////////////////////////////
						if(releaceYear == null){
							releaceYear =cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR));		//制作年
						}
						dbMsg= dbMsg +", Year = " + releaceYear ;/////////////////////////////////////
						if(trackNo == null){
							trackNo =cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK));
							if(trackNo == null || trackNo.equals("0") ){
								trackNo = "00";
							}
						}
						dbMsg= dbMsg +"(" + trackNo +")";/////////////////////////////////////
						if(titolName == null){
							titolName =cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
						}
						dbMsg= dbMsg +titolName;/////////////////////////////////////
					}
					if(releaceYear == null || releaceYear.equals("0")){
						cUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
						c_selection =  MediaStore.Audio.Albums.ALBUM +" = ? ";
						String[] c_selectionArgs2= { albumName };   			//⑥引数groupByには、groupBy句を指定します。
						c_orderBy=MediaStore.Audio.Albums.FIRST_YEAR; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
						cursor = resolver.query( cUri , c_columns , c_selection , c_selectionArgs2, c_orderBy);
						dbMsg= dbMsg +">"+ cursor.getCount() + "件";/////////////////////////////////////
						if(cursor.moveToFirst()){
							String rStr = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.FIRST_YEAR));
							if(rStr != null){
								releaceYear = rStr;
							}else{
								releaceYear = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.LAST_YEAR));
							}
							dbMsg= dbMsg +">>"+ releaceYear;/////////////////////////////////////
						}
					}
				}
			}
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final String TAG = "FileInfoEdit[FileInfoEdit]";
		String dbMsg="";
		try{

//			//		Bundle extras = getIntent().getExtras();
//			//		rData.putExtras(extras);
//					dTitol= dTitol ;		//extras.getString("dTitol");				//ダイアログタイトル
//					dbMsg="dTitol=" + dTitol;/////////////////////////////////////////////////////////////////////////////////////
//					dMessage = dMessage ;		//extras.getString("dMessage");			//アラート文
//					Msg1= PosiBTT ;		//extras.getString("Msg1");					//PositiveButtonボタンのキーフェイス
//					Msg2= NeutBTT ;		//extras.getString("Msg2");					//NeutralButtonボタン2のキーフェイス
//					Msg3= NegaBTT ;		//extras.getString("Msg3");					//NegativeButtonボタン3のキーフェイス
//					dbMsg=dbMsg+",dMessage="+dMessage+",Msg1="+Msg1+",Msg2="+Msg2+",Msg3="+Msg3;///////////////////////////////////////
//					isIn = isInput ;		//extras.getBoolean("isIn");				//文字入力か
//					InType = InType ;	//extras.getInt("InType");				//入力制限
//					isList = isLists ;	//extras.getBoolean("isList");				//リストか
//			//		motoN = extras.getString("motoN");		//元データなど
//			//		accName = extras.getString("accName");	//登録先名
//			//		accType = extras.getString("accType");	//登録先タイプ
//			//		gID = extras.getString("gID");			//書き込み先のグループID
			readPref();
//			String pefName = ｒContext.getResources().getString(R.string.pref_main_file);
//			sharedPref = ｒContext.getSharedPreferences(pefName,ｒContext.MODE_PRIVATE);		//	getSharedPreferences(prefFname,MODE_PRIVATE);
			file_ex = sharedPref.getString("pref_file_ex", ｒContext.getPackageResourcePath());	//メモリーカードの音楽ファイルフォルダ
			Map<String, ?> keys = sharedPref.getAll();
			file_ex = String.valueOf(keys.get("pref_file_ex"));	//メモリーカードの音楽ファイルフォルダ
//			String fName = null;
//			file_ex = "";
//			if(Integer.valueOf(Build.VERSION.SDK) < 19){			//kitcutのSD書き込み非対応対策
//				String fN = (ｒContext.getApplicationContext().getFilesDir()).toString();
//				String[] wStrs = fN.split(File.separator);
//				file_ex = fN.substring(0, (fN.length() - wStrs[ wStrs.length-1 ].length()));				//.substring(0, );
//				dbMsg +=" >> " + file_ex;			//"/data/data/" +  getResources().getString(R.string.content_str)
//		//		myLog(TAG,dbMsg);
//				fName = file_ex + "/shared_prefs/" + ｒContext.getResources().getString(R.string.pref_main_file) +".xml";
//				dbMsg="プリファレンス= " + fName;
//			}
			dbMsg = "保存先=" + file_ex;
			String fn = file_ex + ｒContext.getResources().getString(R.string.app_name) +File.separator + ｒContext.getResources().getString(R.string.artist_reW_file);			//書き換えdb
			dbMsg += ",書き換えファイル=" + fn;
			arhelper = new ArtistRwHelper(ｒContext , fn);		//アーティスト名の置き換えリストの定義ファイル
			dbMsg += ">>" + ｒContext.getDatabasePath(fn);
			ar_db = arhelper.getWritableDatabase();	//アーティスト名の置き換えリストファイルを読み書きモードで開く
			awTname = ｒContext.getResources().getString(R.string.artist_reW_table);	//置換えアーティストリストのテーブル名
			dbMsg += "；書き換えテーブル=" + awTname;
	//		myLog(TAG, dbMsg);
			awCursor = ar_db.query(awTname, null, null, null , null, null, null);	//リString table, String[] columns,new String[] {MotoN, albamN}

	//		requestWindowFeature(Window.FEATURE_NO_TITLE);	        // (これしないとグレーのタイトルが付く)
			setContentView(R.layout.file_info_edit);	        // layout.xml を利用する
	//		fie_albam_artist_name_sp = (Spinner) findViewById(R.id.fie_albam_artist_name_sp);		//（アルバムをまとめる）アーティスト名
			fe_sousa_taisyou = findViewById(R.id.fe_sousa_taisyou);			//操作対象選択
			fie_sousa_taisyou_artist = findViewById(R.id.fie_sousa_taisyou_artist);			//操作対象アーティスト
			fie_sousa_taisyou_album = findViewById(R.id.fie_sousa_taisyou_album);			//操作対象アルバム
			fie_sousa_taisyou_kyoku = findViewById(R.id.fie_sousa_taisyou_kyoku);			//操作対象タイトル
			fie_albam_artist_list_bt = findViewById(R.id.fie_albam_artist_list_bt);	//アーティスト名リスト表示
			fie_albam_artist_name_et = findViewById(R.id.fie_albam_artist_name_et);		//（アルバムをまとめる）アーティスト名
			fie_credit_artist_name_et = findViewById(R.id.fie_credit_artist_name_et);	//（アルバムに表記されている）アーティスト名
			fie_albam_name_et = findViewById(R.id.fie_albam_name_et);					//アルバム名
			fie_year_et = findViewById(R.id.fie_year_et);								//アルバムの(制作もしくは録音)年
			fie_taisyou_track = findViewById(R.id.fie_taisyou_track);					//トラック番号
			fie_taisyou_titol = findViewById(R.id.fie_taisyou_titol);					//タイトル
			fie_taisyou_folder = findViewById(R.id.fie_taisyou_folder);					//データUri
			fie_nega_btn = findViewById(R.id.fie_nega_btn);								//中止ボタン
			fie_posi_btn = findViewById(R.id.fie_posi_btn);								//確定ボタン
			fie_kakuninn_btn = findViewById(R.id.fie_kakuninn_btn);							//確認ボタン

			fie_albam_tv = findViewById(R.id.fie_albam_tv);					//アルバム
			fie_year_tv = findViewById(R.id.fie_year_tv);					//制作もしくは録音年
			fie_nen_tv = findViewById(R.id.fie_nen_tv);						//年
			fie_titol_tv = findViewById(R.id.fie_titol_tv);					//タイトル

			fie_albam_artist_name_et.setText(artistName);		//（アルバムをまとめる）アーティスト名
			fie_credit_artist_name_et.setText(creditArtistName);	//（アルバムに表記されている）アーティスト名
			fie_albam_name_et.setText(albumName);					//アルバム名
			fie_year_et.setText(releaceYear);								//アルバムの(制作もしくは録音)年
			fie_taisyou_track.setText(trackNo);			//トラック番号
			fie_taisyou_titol.setText(titolName);					//タイトル
			fie_taisyou_folder.setText(rDate);						//データURL

			LayoutParams lp = getWindow().getAttributes();	        // 画面の大きさに合わせる(これしないと場合によっては極小表示になる)
			lp.width = LayoutParams.FILL_PARENT;
			getWindow().setAttributes((android.view.WindowManager.LayoutParams) lp);
			shigot_bangou = dlogKinu;		//ダイアログに機能割付け
		} catch (Exception e) {
			myErrorLog(TAG, dbMsg + "で"+e);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {	 // ヘッドのイメージは実際にローディンされた時点で設定表示と同時にウィジェットの高さや幅を取得したいときは大抵ここで取る。
		final String TAG = "onWindowFocusChanged[FileInfoEdit]";
		String dbMsg = "shigot_bangou;" + shigot_bangou;/////////////////////////////////////
		try{
			if (hasFocus) {
				if(shigot_bangou > 0){
					switch(shigot_bangou) {
					case dlogKinu:		//ダイアログに機能割付け
						dLogKinu();	 	//ダイアログに機能割付け
						break;
					default:
						break;
					}
	//				myLog(TAG,dbMsg);
					shigot_bangou = 0;
				}
			}
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	 super.onWindowFocusChanged(hasFocus);
	 }

	public void dLogKinu() {	 //ダイアログに機能割付け
			final String TAG = "dLogKinu[FileInfoEdit]";
			String dbMsg= "開始;";/////////////////////////////////////
			try{
				//http://techbooster.jpn.org/andriod/ui/9640/
				switch(sousa_taisyou) {
				case 0:
					fie_sousa_taisyou_artist.setChecked(true);			//操作対象アーティスト
					fie_sousa_taisyou_album.setChecked(false);			//操作対象アルバム
					fie_sousa_taisyou_kyoku.setChecked(false);				//操作対象タイトル
					break;
				case 1:
					fie_sousa_taisyou_album.setChecked(true);			//操作対象アルバム
					fie_sousa_taisyou_kyoku.setChecked(false);				//操作対象タイトル
					break;
				case 2:
					fie_sousa_taisyou_kyoku.setChecked(true);				//操作対象タイトル
					break;
//				default:
//					break;
				}

				fe_sousa_taisyou.setOnCheckedChangeListener( this);
	//			fie_albam_artist_name_et.setOnEditorActionListener(l);;		//（アルバムをまとめる）アーティスト名
				fie_albam_artist_list_bt = findViewById(R.id.fie_albam_artist_list_bt);

				 fie_albam_artist_list_bt.setOnClickListener( new View.OnClickListener() {	//アーティスト名リスト表示
			            @Override
			            public void onClick(View arg0) {
							 aListSentaku( creditArtistName , albumName );			//クレジットアーティスト名を他の名称に統合;MotoN = creditArtistName , albumN = albumName
			            }
			        });

				fie_posi_btn.setOnClickListener( new View.OnClickListener() {	        // 「OK」ボタン
		            @Override
		            public void onClick(View arg0) {
		        		final String TAG = "fie_posi_btn[FileInfoEdit]";
		        		String dbMsg="発生";
		        		try{
		        			dbMsg="操作対象" + sousa_taisyou;
							creditArtistName = fie_credit_artist_name_et.getText().toString();		//クレジットされているアーティスト名
							artistName = fie_albam_artist_name_et.getText().toString();		//リストアップしたアルバムアーティスト名
							if(sousa_taisyou > 0){
								albumName = fie_albam_name_et.getText().toString();		//アルバム名
								releaceYear = fie_year_et.getText().toString();		//制作年
							}
							if(sousa_taisyou > 1){
								titolName = fie_taisyou_titol.getText().toString();		//曲名
								trackNo = fie_taisyou_track.getText().toString();		//トラック番号
							}

							switch(sousa_taisyou) {							//操作対象
							case 0:			//アーティスト
								dbMsg +=",アーティスト=" + creditArtistName+";;" + artistName;
								reigaiListKakikomi( rDate , creditArtistName, artistName ,null , null , null , null);			//クレジットアーティスト名のリスト表示名反映リストの書き込み	creditNameKakikomi、creditNameTouroku
								break;
							case 1:			//アルバム
								dbMsg= dbMsg +",アルバム[" + releaceYear+"]" + albumName;
								reigaiListKakikomi( rDate , creditArtistName, artistName ,albumName , releaceYear , null , null);			//クレジットアーティスト名のリスト表示名反映リストの書き込み	creditNameKakikomi、creditNameTouroku
								break;
							case 2:			//タイトル
								dbMsg +=",タイトル（" + trackNo+"）" + titolName+"、、" ;
								reigaiListKakikomi( rDate , creditArtistName, artistName ,albumName , releaceYear , trackNo , titolName);			//クレジットアーティスト名のリスト表示名反映リストの書き込み	creditNameKakikomi、creditNameTouroku
								break;
//							default:
//								break;
							}
							dbMsg +=">>" +rDate ;
							myLog(TAG,dbMsg );
			                dismiss();
			        //        quitMe();				//ダイアログとこのクラスを破棄
		        		} catch (Exception e) {
		        			myErrorLog(TAG,dbMsg + e);
		        		}
		            }
		        });

				fie_nega_btn.setOnClickListener( new View.OnClickListener() {	        // 「Cancel」ボタン
		            @Override
		            public void onClick(View arg0) {

		               cancel();
		           //     quitMe();				//ダイアログとこのクラスを破棄
		            }

		        });

				fie_kakuninn_btn.setOnClickListener( new View.OnClickListener() {				//確認ボタン
					public void onClick(View arg0) {
						tourokuZumi( );								//登録してあるレコードの表示
					}
		        });
				if( awCursor.getCount() >0 ){
					fie_kakuninn_btn.setVisibility(View.VISIBLE);
				}else{
					fie_kakuninn_btn.setVisibility(View.GONE);
				}
			}catch (Exception e) {
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
	 }

	 @Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		final String TAG = "onCheckedChanged[FileInfoEdit]";
		String dbMsg="発生";
		try{
			dbMsg= checkedId + ")";
			if (-1 == checkedId) {        // どれも選択されていなければidには-1が入ってくる
				dbMsg= "クリアされました";
			} else {
				dbMsg +=((RadioButton)findViewById(checkedId)).getText() + "が選択されました";
				fie_albam_tv.setVisibility(View.GONE);						//アルバム
				fie_year_tv.setVisibility(View.GONE);						//制作もしくは録音年
				fie_nen_tv.setVisibility(View.GONE);							//年
				fie_albam_name_et.setVisibility(View.GONE);				//アルバム名
				fie_year_et.setVisibility(View.GONE);					//アルバムの(制作もしくは録音)年
				fie_titol_tv.setVisibility(View.GONE);						//タイトル
				fie_taisyou_track.setVisibility(View.GONE);				//トラック番号
				fie_taisyou_titol.setVisibility(View.GONE);				//タイトル
				switch(checkedId) {							//操作対象
				case R.id.fie_sousa_taisyou_artist:			//操作対象アーティスト
					sousa_taisyou = 0;			//操作対象
					break;
				case R.id.fie_sousa_taisyou_album:				//操作対象アルバム
					sousa_taisyou = 1;			//操作対象
					fie_albam_tv.setVisibility(View.VISIBLE);						//アルバム
					fie_year_tv.setVisibility(View.VISIBLE);						//制作もしくは録音年
					fie_nen_tv.setVisibility(View.VISIBLE);							//年
					fie_albam_name_et.setVisibility(View.VISIBLE);				//アルバム名
					fie_year_et.setVisibility(View.VISIBLE);					//アルバムの(制作もしくは録音)年
					break;
				case R.id.fie_sousa_taisyou_kyoku:								//操作対象タイトル
					sousa_taisyou = 2;			//操作対象
					fie_albam_tv.setVisibility(View.VISIBLE);						//アルバム
					fie_year_tv.setVisibility(View.VISIBLE);						//制作もしくは録音年
					fie_nen_tv.setVisibility(View.VISIBLE);							//年
					fie_albam_name_et.setVisibility(View.VISIBLE);				//アルバム名
					fie_year_et.setVisibility(View.VISIBLE);					//アルバムの(制作もしくは録音)年
					fie_titol_tv.setVisibility(View.VISIBLE);						//タイトル
					fie_taisyou_track.setVisibility(View.VISIBLE);				//トラック番号
					fie_taisyou_titol.setVisibility(View.VISIBLE);				//タイトル
					break;
//				default:
//					break;
				}
			}
	//		myLog(TAG,dbMsg );
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg + e);
		}
   }

	public int cCount = 0;
	public void tourokuZumi( ){								//登録してあるレコードの表示
		final String TAG = "tourokuZumi[FileInfoEdit]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			awCursor = ar_db.query(awTname, null, null, null , null, null, null);	//リString table, String[] columns,new String[] {MotoN, albamN}
			cCount = awCursor.getCount();
			dbMsg= cCount + "件登録済み";/////////////////////////////////////
			if(awCursor.moveToFirst()){
				listItems = null;
				listItems = new String[cCount];
				int i = 0;
				do{
					dbMsg +=i + "/" + cCount + ")" ;/////////////////////////////////////
					@SuppressLint("Range") String rDate = awCursor.getString(awCursor.getColumnIndex("rDate"));		//データURL
					dbMsg +=rDate+">>";/////////////////////////////////////
					listItems[i] = rDate;
					dbMsg +=listItems[i];/////////////////////////////////////
					i++;
				}while(awCursor.moveToNext());
	//			listItems = reNameList.toArray(new String[0]);
				String dTitol = ｒContext.getResources().getString(R.string.menu_funk_artistmeiE);		//指定変更
				dbMsg=dTitol+">>" + listItems.length + "人を登録";////////////////////////////////////////////////////////
				AlertDialog.Builder adgBuilder = new AlertDialog.Builder( ｒContext );		// アラートダイアログのタイトルを設定します ☆getApplicationContext()などではなく、このActiviteyを指定
				adgBuilder.setTitle(dTitol);
				adgBuilder.setItems(listItems, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which) {
							final String TAG = "list[CreditNameHensyuu]";
							String dbMsg= "開始;";/////////////////////////////////////
							try{
					        	dbMsg="[" +which +"]";/////////////////////////////////////////////////////////////////////////////////////
					        	awCursor.moveToPosition(which);
					        	@SuppressLint("Range") String idStr = String.valueOf(awCursor.getInt(awCursor.getColumnIndex("_id")));
								dbMsg +=idStr;/////////////////////////////////////////////////////////////////////////////////////
								int syoukyoGyou = ar_db.delete(ｒContext.getResources().getString(R.string.artist_reW_table),"_id = '" + idStr +"'", null);		//			"_id = ?", new String[]{ idStr }
								dbMsg +=" , 消去したのは" +  syoukyoGyou + " 行目";/////////////////////////////////////////////////////////////////////////////////////
								myLog(TAG,dbMsg);
//									creditNLSakujyo(  motS , albS , sakiN);								//クレジットアーティスト名変更リストから削除
								dialog.dismiss();
								listItems = null;
								awCursor = ar_db.query(awTname, null, null, null , null, null, null);	//リString table, String[] columns,new String[] {MotoN, albamN}
								cCount = awCursor.getCount();
								if(cCount >0 ){
									fie_kakuninn_btn.setVisibility(View.VISIBLE);
								}else{
									fie_kakuninn_btn.setVisibility(View.GONE);
								}
							}catch (Exception e) {
								myErrorLog(TAG ,  dbMsg + "で" + e);
							}
						}
					});
				adgBuilder.setNegativeButton(ｒContext.getResources().getString(R.string.comon_cyusi),new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
			//				awCursor.close();
						}
					});
				dbMsg=dbMsg+">>show";////////////////////////////////////////////////////////
				adgBuilder.show();
			}
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void reigaiListKakikomi( String rDate , String creditArtistName, String artistName ,String albumName , String releaceYear ,
			String trackNo , String titolName){			//クレジットアーティスト名のリスト表示名反映リストの書き込み	creditNameKakikomi、creditNameTouroku
		final String TAG = "reigaiListKakikomi[FileInfoEdit]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			dbMsg = creditArtistName + "[" + artistName  + "]>>" + albumName + " ;;" + titolName ;/////////////////////////////////////
			dbMsg += "；書き換えテーブル=" + awTname;
			awCursor = ar_db.query(awTname, null, "rDate LIKE ? ", new String[] {"'" + rDate + "%'"} ,  null ,  null ,  null);//リString table, String[] columns,new String[] {MotoN, albamN}
//			awCursor = ar_db.query(awTname, null, "creditArtistName = '" + creditArtistName + "' AND albumName = '"+ albumName +"'", null , null, null, null);//リString table, String[] columns,new String[] {MotoN, albamN}
			if(awCursor.moveToFirst()){
				dbMsg += "は書き換え除外リストに" + awCursor.getCount() +"件あり";/////////////////////////////////////////////////////////////	motName,albumName , sakiName
				@SuppressLint("Range") String reId = String.valueOf(awCursor.getInt(awCursor.getColumnIndex("_ID")));
				dbMsg +="更新先=" + reId +")";/////////////////////////////////////
				ar_db.beginTransaction();
				try {
					SQLiteStatement stmt = ar_db.compileStatement("update " + awTname + " set rDate = ? , creditArtistName = ?, artistName = ? ,albumName = ? , releaceYear = ? , trackNo = ? , titolName = ? where _id = ?;");
			//		ContentValues cv = new ContentValues();
					String dStr = null ;
					if( creditArtistName != null ){
						dStr =hairetuElement( rDate , File.separator ,2, true);	//渡された文字をセパレータで区切って前もしくは後ろの指定されたエレメント数で結合して返す
						stmt.bindString(2, creditArtistName);	//cv.put("creditArtistName", creditArtistName);		//クレジットされているアーティスト名
					}
					if( artistName != null ){
						stmt.bindString(3, artistName);	//cv.put("artistName", artistName);		//リストアップしたアルバムアーティスト名
					}
					if( albumName != null ){
						dStr =hairetuElement( rDate , File.separator ,1, true);	//渡された文字をセパレータで区切って前もしくは後ろの指定されたエレメント数で結合して返す
						stmt.bindString(4, albumName);	//cv.put("albumName", albumName);			//アルバム名
						stmt.bindString(5, releaceYear);	//cv.put("releaceYear", releaceYear);		//制作年
					}
					if( titolName != null ){
						stmt.bindString(6, trackNo);	//cv.put("trackNo", trackNo);				//trackNo,
						stmt.bindString(7, titolName);	//cv.put("titolName", titolName);			//曲名
						dStr = rDate;		//cv.put("rDate", rDate);					//データURL
					}
					stmt.bindString(1, dStr);		//cv.put("rDate", rDate);					//データURL
					stmt.bindString(8 , reId);

					long id = stmt.executeInsert();
					dbMsg +=">>" + id+")";/////////////////////////////////////
					ar_db.setTransactionSuccessful();
				} finally {
					ar_db.endTransaction();
				}
			}else{
				dbMsg += "を書き換え除外リストに追記";/////////////////////////////////////////////////////////////
//				ar_db.execSQL("insert into "+ｒContext.getResources().getString(R.string.artist_reW_table)+
//						"(motName,albumName ,sakiName) values ('" + MotoN +"', '" +albamN+"', '" + sakiN +"');");
				ar_db.beginTransaction();
				try {
					SQLiteStatement stmt = ar_db.compileStatement("insert into " + awTname + "(rDate , creditArtistName, artistName ,albumName , releaceYear , trackNo , titolName) values (? , ?, ? ,? , ? , ? , ?);");
			//		ContentValues cv = new ContentValues();
					String dStr = null ;
					if( creditArtistName != null ){
						dStr =hairetuElement( rDate , File.separator ,2, true);	//渡された文字をセパレータで区切って前もしくは後ろの指定されたエレメント数で結合して返す
						stmt.bindString(2, creditArtistName);	//cv.put("creditArtistName", creditArtistName);		//クレジットされているアーティスト名
					}
					if( artistName != null ){
						stmt.bindString(3, artistName);	//cv.put("artistName", artistName);		//リストアップしたアルバムアーティスト名
					}
					if( albumName != null ){
						dStr =hairetuElement( rDate , File.separator ,1, true);	//渡された文字をセパレータで区切って前もしくは後ろの指定されたエレメント数で結合して返す
						stmt.bindString(4, albumName);	//cv.put("albumName", albumName);			//アルバム名
						stmt.bindString(5, releaceYear);	//cv.put("releaceYear", releaceYear);		//制作年
					}
					if( titolName != null ){
						stmt.bindString(6, trackNo);	//cv.put("trackNo", trackNo);				//trackNo,
						stmt.bindString(7, titolName);	//cv.put("titolName", titolName);			//曲名
						dStr = rDate;		//cv.put("rDate", rDate);					//データURL
					}
					stmt.bindString(1, dStr);		//cv.put("rDate", rDate);					//データURL
					long id = stmt.executeInsert();
					dbMsg +=">>" +id+")";/////////////////////////////////////
					ar_db.setTransactionSuccessful();
				} finally {
					ar_db.endTransaction();
				}
			}
			myLog(TAG,dbMsg);
		//	creditNameKakikomi( MotoN , albamN , sakiN );			//アーティストリスト更新
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public String hairetuElement( String rDate , String sepa ,int keta, boolean mae){	//渡された文字をセパレータで区切って前もしくは後ろの指定されたエレメント数で結合して返す
		String ｒetStr = "";
		final String TAG = "hairetuElement[FileInfoEdit]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			String[] strS = rDate.split(sepa);
			int Count =0;
			if(mae){
				for(String rStr : strS){
					if((strS.length-keta) > Count){
						ｒetStr = ｒetStr + rStr + sepa;
					}
					Count++;
				}
			}else{

			}

		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return ｒetStr;
	}

	//ArrayList<String> lItems;
	@SuppressLint("Range")
	public void aListSentaku(String MotoN , String albumN ){			//クレジットアーティスト名を他の名称に統合;アーティストリスト表示
		String sakiN = null;
		final String TAG = "aListSentaku[FileInfoEdit]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			dbMsg =  MotoN  + "[" + albumN  + "]";/////////////////////////////////////
			final String MotoNa = MotoN;
			final String albamNa = albumN;
			String fn = file_ex + ｒContext.getResources().getString(R.string.app_name) +File.separator + ｒContext.getResources().getString(R.string.artist_file);			//アーティストリスト

			dbMsg += ",db=" + fn;
			ArtistHelper artistHelper = new ArtistHelper(ｒContext , fn);		//アーティスト名のリストの定義ファイル
			dbMsg += ">>" + ｒContext.getDatabasePath(fn);
			SQLiteDatabase artist_db = artistHelper.getWritableDatabase();					//アーティスト名のえリストファイルを読み書きモードで開く
			String artistTName = ｒContext.getResources().getString(R.string.artist_table);			//アーティストリストのテーブル名
			dbMsg += "；アーティストリストテーブル=" + artistTName;
			Cursor artistCursor = artist_db.query(artistTName, null, null, null , null, null, null);	//リString table, String[] columns,new String[] {MotoN, albamN}
			int cCount = artistCursor.getCount();
			dbMsg += "；" + cCount + "件";
			if(artistCursor.moveToFirst()){
				String[] aTuika = ｒContext.getResources().getStringArray(R.array.artist_tuika);
				dbMsg += "；追加=" + aTuika.length + "件";
				myLog(TAG,dbMsg);
				listItems = new String[cCount + aTuika.length];
				int i = 0 ;
				do{
					dbMsg = artistCursor.getPosition() + "/" + cCount + "件";
					listItems[i] = artistCursor.getString(artistCursor.getColumnIndex("ARTIST"));	//MediaStore.Audio.Albums.ARTIST				listItems = artistList.toArray(new String[0]);
		//			dbMsg += lItems.get(lItems.size()-1);
					i++;
				}while(artistCursor.moveToNext());

				for(String wStr : aTuika){
					dbMsg = i + ")" + wStr;
		//			myLog(TAG,dbMsg);
					listItems[i] = wStr;
					i++;
				}

				String dTitol = ｒContext.getResources().getString(R.string.menu_funk_artistmei2)  + ";" + MotoN;		//統合先を選択して下さい。
				dbMsg=dTitol+">>" + listItems.length + "人から選択";////////////////////////////////////////////////////////
			}
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( ｒContext );		// アラートダイアログのタイトルを設定します 	getApplicationContext()
			alertDialogBuilder.setTitle(dTitol);
			alertDialogBuilder.setItems(listItems, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					final String TAG = "aListSentaku[FileInfoEdit]";
					String dbMsg="[" +which +"]";/////////////////////////////////////////////////////////////////////////////////////
					try{
						dbMsg=dbMsg +listItems[which];/////////////////////////////////////////////////////////////////////////////////////
						artistName = (String) listItems[which];
						dbMsg +=artistName +"を選択";/////////////////////////////////////////////////////////////////////////////////////
						fie_albam_artist_name_et.setText(artistName);
						dialog.dismiss();
		//				creditNameTouroku_kakikomi( R.string.menu_funk_artistmei2 , MotoNa , albamNa ,sakiN);		//変更したアーティスト名をリスト登録 ；；元のアーティスト名 , 対象アルバム, 書き換え
		//				myLog(TAG,dbMsg);
					}catch (Exception e) {
						myErrorLog(TAG ,  dbMsg + "で" + e);
					}
				}
			});
			alertDialogBuilder.setNegativeButton(ｒContext.getResources().getString(R.string.comon_cyusi),new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
			alertDialogBuilder.show();
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}


	public void quitMe() {				//ダイアログとこのクラスを破棄
		final String TAG = "closeMe[FileInfoEdit]";
		String dbMsg="発生";
		try{
	//		if(alertDialog.isShowing()){
				alertDialog.dismiss();
	//		}
	//		FileInfoEdit.this.finish();
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg + e);
		}

	}

//	public void modori(int retInt , String retStr , CharSequence[] listItems ,String[] listIDs ,boolean[] checkedItems) {	// 戻し
//		//選択されたボタン（リストならインデックス）、入力結果（選択されたアイテム）,リストアイテム,リストアイテムのID,選択リストの配列
//
//		final String TAG = "modori[FileInfoEdit]";							//long seleID  ,, int hennkou, String seleItem
//		String dbMsg= "開始;";/////////////////////////////////////
//		try{
//			Intent data = new Intent();			// 返すデータ(Intent&Bundle)の作成
//			Bundle bundle = new Bundle();
//
//			if(retInt > 0){
//				dbMsg +="retInt=" + retInt + "件" ;/////////////////////////////////////
//				bundle.putInt("key.retInt", retInt);			//切り替え先
//			}
//			if(retStr != null){
//				dbMsg +="retStr=" + retStr  ;/////////////////////////////////////
//				bundle.putString("key.retStr", retStr);			//切り替え先
//			}
//			data.putExtras(bundle);
//			quitMe();			//
//			long end=System.currentTimeMillis();		// 終了時刻の取得
//			start=System.currentTimeMillis();
//			dbMsg=dbMsg +";"+ (int)((end - start)) + "m秒で終了";
//			myLog(TAG,dbMsg);
//		}catch (Exception e) {
//			myErrorLog(TAG ,  dbMsg + "で" + e);
//		}
//	}

	@Override
	protected void onStart() {
		super.onStart();
		myLog("onStart","onStartが[FileInfoEdit]で発生");
	}

//	@Override
//	protected void onPause() {
//		super.onPause();
//		myLog("onPause","onPauseが[FileInfoEdit]で発生");
//	}

	@Override
	protected void onStop() {
		super.onStop();
		myLog("onStop","onStopが[FileInfoEdit]で発生");
	}


//	public void setPositiveButton(String string, android.view.View.OnClickListener
//			onClickListener) {
//		final String TAG = "setPositiveButton[FileInfoEdit]";
//    		String dbMsg="発生";
//    		try{
//    			dbMsg="操作対象" + sousa_taisyou;
//				creditArtistName = fie_credit_artist_name_et.getText().toString();		//クレジットされているアーティスト名
//				artistName = fie_albam_artist_name_et.getText().toString();		//リストアップしたアルバムアーティスト名
//				if(sousa_taisyou > 0){
//					albumName = fie_albam_name_et.getText().toString();		//アルバム名
//					releaceYear = fie_year_et.getText().toString();		//制作年
//				}
//				if(sousa_taisyou > 1){
//					titolName = fie_taisyou_titol.getText().toString();		//曲名
//					trackNo = fie_taisyou_track.getText().toString();		//トラック番号
//				}
//
//				switch(sousa_taisyou) {							//操作対象
//				case 0:			//アーティスト
//					dbMsg +=",アーティスト=" + creditArtistName+";;" + artistName;
//					reigaiListKakikomi( rDate , creditArtistName, artistName ,null , null , null , null);			//クレジットアーティスト名のリスト表示名反映リストの書き込み	creditNameKakikomi、creditNameTouroku
//					break;
//				case 1:			//アルバム
//					dbMsg= dbMsg +",アルバム[" + releaceYear+"]" + albumName;
//					reigaiListKakikomi( rDate , creditArtistName, artistName ,albumName , releaceYear , null , null);			//クレジットアーティスト名のリスト表示名反映リストの書き込み	creditNameKakikomi、creditNameTouroku
//					break;
//				case 2:			//タイトル
//					dbMsg +=",タイトル（" + trackNo+"）" + titolName+"、、" ;
//					reigaiListKakikomi( rDate , creditArtistName, artistName ,albumName , releaceYear , trackNo , titolName);			//クレジットアーティスト名のリスト表示名反映リストの書き込み	creditNameKakikomi、creditNameTouroku
//					break;
////				default:
////					break;
//				}
//				dbMsg +=">>" +rDate ;
//				myLog(TAG,dbMsg );
//                dismiss();
//        //        quitMe();				//ダイアログとこのクラスを破棄
//    		} catch (Exception e) {
//    			myErrorLog(TAG,dbMsg + e);
//    		}
//        }
//
//	public void setNegativeButton(String string,android.view.View.OnClickListener
//			onClickListener) {
//		final String TAG = "setNegativeButton[FileInfoEdit]";
//		String dbMsg="発生";
//		try{
//			cancel();
////	      //     quitMe();				//ダイアログとこのクラスを破棄
//			myLog(TAG,dbMsg);
//		} catch (Exception e) {
//			myErrorLog(TAG, dbMsg + "で"+e);
//		}
//    }
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		final String TAG = "onDestroy[FileInfoEdit]";
//		String dbMsg="発生";
//		try{
//			myLog(TAG,dbMsg);
//		} catch (Exception e) {
//			myErrorLog(TAG, dbMsg + "で"+e);
//		}
//	}
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
