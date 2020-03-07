package com.hijiyam_koubou.marasongs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.widget.Toast;

public class CreditNameHensyuu extends Activity {
	OrgUtil ORGUT;		//自作関数集
	MaraSonActivity MSG;			//読出し元のプレイヤーアクティビテイ

	public Context rContext;
	public long start = 0;								// 開始時刻の取得
	public SharedPreferences sharedPref;
	public Editor myEditor ;
	public SharedPreferences artist_l_Pref;				//アーティストリストファイル
	public Editor alEditor;
	public String alPFN = "al_pref";
	public ArtistRwHelper arhelper;		//アーティスト名の置き換えリストの定義ファイル
	public SQLiteDatabase ar_db;	//アーティスト名の置き換えリストファイル
	public String awTname;	//置換えアーティストリスト
	public String myFolder ;
	public String file_ex;	//メモリーカードの音楽ファイルフォルダ
	public Cursor awCursor;
	public List<String> reNameList = null;	//アルバム←クレジットアーティスト強制変換リスト

	public int reqCode = 0;		//何の処理か
	String creditArtistName = null;		//クレジットされているアーティスト名
	String artistName = null;		//リストアップしたアルバムアーティスト名
	String albumName = null;		//アルバム名

	public List<String> artistList = null;		//アルバムアーティスト

	public void readPref () {        //プリファレンスの読込み
		final String TAG = "readPref";
		String dbMsg = "[MuList]";
		try {
			MyPreferences myPreferences = new MyPreferences();
			dbMsg += "MyPreferencesy読込み";
			myPreferences.readPrif(this);
			sharedPref = MyPreferences.sharedPref;
			myEditor =myPreferences.myEditor;
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}																	//設定読込・旧バージョン設定の消去

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final String TAG = "onCreate[CreditNameHensyuu]";
		String dbMsg = "開始";
		try{
			long start = System.currentTimeMillis();		// 開始時刻の取得
			ORGUT = new OrgUtil();				//自作関数集

			rContext = getApplicationContext();
			Bundle extras = getIntent().getExtras();
			reqCode = extras.getInt("reqCode");		//何の処理か
			dbMsg += ";reqCode=" + reqCode +";";
			String MotoN = extras.getString("MotoN");
			String albamN = extras.getString("albamN");
			String sakiN = extras.getString("sakiN");
			myFolder = extras.getString("myFolder");
			readPref();
//			sharedPref = getSharedPreferences(getResources().getString(R.string.pref_main_file),MODE_PRIVATE);		//	getSharedPreferences(prefFname,MODE_PRIVATE);
			file_ex = sharedPref.getString("pref_file_ex", getApplicationContext().getPackageResourcePath());	//メモリーカードの音楽ファイルフォルダ
			if(file_ex.endsWith("\n")){
				file_ex = file_ex.substring(0, file_ex.length()-3);
			}
			dbMsg = "保存先=" + file_ex;
			artist_l_Pref = getSharedPreferences(getResources().getString(R.string.pref_artist_file),MODE_PRIVATE);				//アーティストリストファイル
			alEditor = artist_l_Pref.edit();

			String fn = file_ex + getResources().getString(R.string.artist_reW_file);		//+File.separator
			dbMsg += ",db=" + fn;
			arhelper = new ArtistRwHelper(getApplicationContext() , fn);		//アーティスト名の置き換えリストの定義ファイル
			dbMsg += ">>" + getApplicationContext().getDatabasePath(fn);
			ar_db = arhelper.getWritableDatabase();	//アーティスト名の置き換えリストファイルを読み書きモードで開く
			awTname = getResources().getString(R.string.artist_reW_table);	//置換えアーティストリストのテーブル名
			dbMsg += "；書き換えテーブル=" + awTname;
			awCursor = ar_db.query(awTname, null, null, null , null, null, null);	//リString table, String[] columns,new String[] {MotoN, albamN}

			dbMsg +=")"+ MotoN + "[" + albamN  + "]>>" + sakiN ;/////////////////////////////////////
			String delL = extras.getString("delL");
//			creditArtistName = extras.getString("sakiName");		//クレジットされているアーティスト名
//			artistName = extras.getString("motName");		//リストアップしたアルバムアーティスト名
//			albumName = extras.getString("albumName");		//アルバム名
			switch(reqCode){
//				case 111:
//					dbMsg = MotoN + "[" + albamN  + "]>>" + sakiN ;/////////////////////////////////////
//					reigaiListKakikomi( MotoN , albamN, sakiN);			//クレジットアーティスト名のリスト表示名反映リストの書き込み
//					break;
//				case 222:
//					dbMsg = MotoN + ">>" + sakiN ;/////////////////////////////////////
//					//( MotoN , sakiN );								//クレジットアーティスト名の変更書き込み
//					break;
				case R.string.menu_funk_artistmei1:					//2131165263
				case R.string.menu_funk_artistmei2:
				case R.string.menu_funk_artistmei3:
					dbMsg=dbMsg +reqCode;	//のまま
					reigaiListKakikomi( MotoN , albamN , sakiN);	//クレジットアーティスト名のリスト表示名反映リストの書き込み	//リスト表示する名称 = 元々クレジットされているアーティスト表記
					break;
				case R.string.menu_funk_artistmeiE:
					dbMsg +=getResources().getString(R.string.menu_funk_artistmeiE) + "を選択";	//指定変更
					creditNLHensyuu( );								//クレジットアーティスト名変更リストの表示
					break;
				default:
					break;
				}
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	String[] listItems =null;


	public void reigaiListKakikomi( String MotoN , String albamN, String sakiN){			//クレジットアーティスト名のリスト表示名反映リストの書き込み	creditNameKakikomi、creditNameTouroku
		final String TAG = "reigaiListKakikomi[CreditNameHensyuu]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			dbMsg = MotoN + "[" + albamN  + "]>>" + sakiN ;/////////////////////////////////////
			dbMsg += "；書き換えテーブル=" + awTname;
			awCursor = ar_db.query(awTname, null, "motName = '" + MotoN + "' AND albumName = '"+ albamN +"'", null , null, null, null);//リString table, String[] columns,new String[] {MotoN, albamN}
			//db.query("emp", new String[] { "name", "age" }, null, null, null, null, null);//
			if(awCursor.moveToFirst()){
				dbMsg += "は書き換え除外リストに" + awCursor.getCount() +"件あり";/////////////////////////////////////////////////////////////	motName,albumName , sakiName
//				String artistM = awCursor.getString(awCursor.getColumnIndex("artistName"));
//				dbMsg += ",artistM=" + artistM;/////////////////////////////////////////////////////////////
//				String albumN = awCursor.getString(awCursor.getColumnIndex("albumName"));
//				dbMsg += ",albumN=" + albumN;/////////////////////////////////////////////////////////////
//				aName = awCursor.getString(awCursor.getColumnIndex("creditArtistName"));
//				dbMsg += ",credit⇒aName=" + dbMsg;/////////////////////////////////////////////////////////////
			}else{
				dbMsg += "を書き換え除外リストに追記";/////////////////////////////////////////////////////////////
				ar_db.execSQL("insert into "+getResources().getString(R.string.artist_reW_table)+
						"(motName,albumName ,sakiName) values ('" + MotoN +"', '" +albamN+"', '" + sakiN +"');");
			}
			awCursor.close();
			myLog(TAG, dbMsg);
			creditNameKakikomi( MotoN , albamN , sakiN );								//クレジットアーティスト名の変更書き込み
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void creditNameKakikomi( String MotoN , String albamN , String sakiN ){								//クレジットアーティスト名の変更書き込み
		final String TAG = "creditNameKakikomi[CreditNameHensyuu]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
//			String[] tStr = MotoN.split(getResources().getString(R.string.aaWSepalater));
//			MotoN = tStr[0];
			dbMsg = MotoN;/////////////////////////////////////
	//		String albamN =tStr[1];
			dbMsg +="[" + albamN +"]";/////////////////////////////////////
			dbMsg += ">>" + sakiN;/////////////////////////////////////
			myLog(TAG, dbMsg);
			ContentResolver resolver = getApplicationContext().getContentResolver();	//c.getContentResolver();
			Uri cUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
			String[] c_columns = null;		 		//③引数columnsには、検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
			String c_selection =  MediaStore.Audio.Albums.ARTIST +" = ? AND " + MediaStore.Audio.Albums.ALBUM +" = ? ";			//2.projection  A list of which columns to return. Passing null will return all columns, which is inefficient.
			String[] c_selectionArgs= {MotoN , albamN};   							//⑥引数groupByには、groupBy句を指定します。
			String c_orderBy=MediaStore.Audio.Albums.DEFAULT_SORT_ORDER; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
			Cursor cursor = resolver.query( cUri , c_columns , c_selection , c_selectionArgs, c_orderBy);
			dbMsg=dbMsg +";"+ cursor.getCount() + "件×"+ cursor.getColumnCount() + "項目";
			if(cursor.moveToFirst()){
				String wId = MediaStore.Audio.Media._ID;
				dbMsg = wId + ")" + MediaStore.Audio.Media.ARTIST + "　の　" + MediaStore.Audio.Media.ALBUM ;/////////////////////////////////////////////////////////////////////////////////////////////
				ContentValues cv = new ContentValues();
				cv.put(MediaStore.Audio.Media.ARTIST, sakiN);
				String where = MediaStore.Audio.Albums._ID + "= ?";
				String[] selectionArgs = {wId};
				int rows = resolver.update(cUri, cv , where, selectionArgs);
				myLog(TAG, dbMsg);
				artistName = sakiN;
				String toastStr = getResources().getString(R.string.medst_artist_make);
				Toast.makeText(getApplicationContext(), toastStr, Toast.LENGTH_LONG).show();
				CreateArtistList(R.string.menu_funk_artistmei , sakiN);		//このアーティストの指定
			}
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	String fSep ="[";
	String aSep ="]>>";
	public void creditNLHensyuu( ){								//クレジットアーティスト名変更リストの表示
		final String TAG = "creditNLHensyuu[CreditNameHensyuu]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			int cCount = awCursor.getCount();
			dbMsg= cCount + "件登録済み";/////////////////////////////////////
			if(awCursor.moveToFirst()){
				listItems = new String[cCount];
				int i = 0;
				do{
					dbMsg +=i + "/" + cCount + ")" ;/////////////////////////////////////
					String motName = awCursor.getString(awCursor.getColumnIndex("motName"));			//リストアップしたアルバムアーティスト名
					String albumName = awCursor.getString(awCursor.getColumnIndex("albumName"));		//アルバム名
					String sakiName = awCursor.getString(awCursor.getColumnIndex("sakiName"));	//クレジットされているアーティスト名
					String wStr = motName + fSep + albumName + aSep + sakiName;
					listItems[i] = wStr;
					dbMsg +=listItems[i];/////////////////////////////////////
					i++;
				}while(awCursor.moveToNext());
	//			listItems = reNameList.toArray(new String[0]);
				String dTitol = getResources().getString(R.string.menu_funk_artistmeiE);		//指定変更
				dbMsg=dTitol+">>" + listItems.length + "人を登録";////////////////////////////////////////////////////////
				AlertDialog.Builder adgBuilder = new AlertDialog.Builder( CreditNameHensyuu.this );		// アラートダイアログのタイトルを設定します ☆getApplicationContext()などではなく、このActiviteyを指定
				adgBuilder.setTitle(dTitol);
				adgBuilder.setItems(listItems, new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which) {
							final String TAG = "list[CreditNameHensyuu]";
							String dbMsg= "開始;";/////////////////////////////////////
							try{
					        	dbMsg="[" +which +"]";/////////////////////////////////////////////////////////////////////////////////////
					        	awCursor.moveToPosition(which);
					        	String idStr = String.valueOf(awCursor.getInt(awCursor.getColumnIndex("_id")));
								dbMsg +=idStr;/////////////////////////////////////////////////////////////////////////////////////
								int syoukyoGyou = ar_db.delete(getResources().getString(R.string.artist_reW_table),"_id = '" + idStr +"'", null);		//			"_id = ?", new String[]{ idStr }
								dbMsg +=" , 消去したのは" +  syoukyoGyou + " 行目";/////////////////////////////////////////////////////////////////////////////////////
								myLog(TAG, dbMsg);
//								creditNLSakujyo(  motS , albS , sakiN);								//クレジットアーティスト名変更リストから削除
								dialog.dismiss();
								Intent data = new Intent();			// 返すデータ(Intent&Bundle)の作成
								setResult(RESULT_OK, data);
								quitMe();				//ダイアログとこのクラスを破棄
								awCursor.close();
							}catch (Exception e) {
								myErrorLog(TAG ,  dbMsg + "で" + e);
							}
						}
					});
				adgBuilder.setNegativeButton(getResources().getString(R.string.comon_cyusi),new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
				//			bundle.putInt("key.retInt", 3);			//切り替え先
							dialog.dismiss();
							Intent data = new Intent();			// 返すデータ(Intent&Bundle)の作成
							setResult(RESULT_CANCELED, data);		//RESULT_CANCELED=0 (0x00000000)
							quitMe();				//ダイアログとこのクラスを破棄
							awCursor.close();
						}
					});
				dbMsg=dbMsg+">>show";////////////////////////////////////////////////////////
				adgBuilder.show();
			}
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void CreateArtistList(int tugiNoSyori , String artistMei){		//アーティストリスト作成 ,表示指定が無ければアーティストIDは0指定
		final String TAG = "CreateArtistList[CreditNameHensyuu]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			dbMsg=ORGUT.nowTime(true,true,true) + dbMsg;/////////////////////////////////////
			dbMsg= dbMsg +";tugiNoSyori="+tugiNoSyori +",artistMei="+artistMei;/////////////////////////////////////
			int reqCode= tugiNoSyori;					// syoki_Yomi1 = syoki_Yomikomi+1;CreateArtistListの初回作成
			Intent intentML = new Intent(getApplication(),MuList.class);						//parsonalPBook.thisではメモリーリークが起こる
			intentML.putExtra("reqCode",reqCode);		//何のリストか
			intentML.putExtra("artistName",artistName);		//アルバムアーティスト
			startActivityForResult(intentML , reqCode);
	//		myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public void quitMe(){			///終了処理
		final String TAG = "quitMe[CreditNameHensyuu]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			dbMsg="スタート";///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//		Log.i(TAG,"quitMeが発生");
	//		dialog.dismiss();
			CreditNameHensyuu.this.finish();
		} catch (Exception e) {		//汎用
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) { // startActivityForResult で起動させたアクティビティがfinish() により破棄されたときにコールされる
	// requestCode : startActivityForResult の第二引数で指定した値が渡される
	// resultCode : 起動先のActivity.setResult の第一引数が渡される
	// Intent data : 起動先Activityから送られてくる Intent
		super.onActivityResult(requestCode, resultCode, intent);
		final String TAG = "onActivityResult";
		String dbMsg="[CreditNameHensyuu]";
		try{
			dbMsg += ",intent="+intent;////////////////////////////////////////////////////////////////////////////
			if(intent != null){
				dbMsg += ",resultCode="+resultCode;////////////////////////////////////////////////////////////////////////////
				dbMsg += "requestCode="+requestCode;////////////////////////////////////////////////////////////////////////////
				Bundle bundle = null ;
				int retInt = 0;
				boolean retBool ;
				String retString = null;
				bundle = intent.getExtras();
				List<String> mainList = new ArrayList<String>();		//メインデータリスト
				List<String> imageList = new ArrayList<String>();		//イメージURLリスト
				List<String> subList = new ArrayList<String>();		//付加情報リスト

				Intent data = new Intent();			// 返すデータ(Intent&Bundle)の作成

				if(intent != null){
					mainList = bundle.getStringArrayList("key.mainList");		//メインデータリスト
					if(mainList != null){
						dbMsg +=",main="+ mainList.size() +"件";		/////////////////////////////////////////////////////////////
					}
					subList = bundle.getStringArrayList("key.subList");	//付加情報リスト
					if(subList != null){
						dbMsg +=",sub="+ subList.size() +"件";		/////////////////////////////////////////////////////////////
					}
					imageList = bundle.getStringArrayList("key.imageList");		//イメージURLリスト
					if(imageList != null){
						dbMsg +=",image="+ imageList.size() +"件";		/////////////////////////////////////////////////////////////
					}
				}
			//		myLog(TAG, dbMsg);
//					break;
//				}
				if(resultCode == RESULT_OK){			//-1
					switch(requestCode) {
					case R.string.menu_funk_artistmei:		//← CreateArtistList ← reigaiListKakikomi ← 	//クレジットのまま
						if(mainList != null){
							dbMsg= "mainList=" + mainList.size() + "件、";/////////////////////////////////////
							bundle.putStringArrayList("key.mainList", (ArrayList<String>) mainList);			//メインデータリスト
							bundle.putString("key.kekka", artistName);			//メインデータリスト
						}
						if(subList != null){
							dbMsg +=" , subList=" + subList.size() + "件、";/////////////////////////////////////
							bundle.putStringArrayList("key.subList", (ArrayList<String>) subList);				//付加情報リスト
						}
						if(imageList != null){
							dbMsg +=" , imageList=" + imageList.size() + "件、";/////////////////////////////////////
							bundle.putStringArrayList("key.imageList", (ArrayList<String>) imageList);			//("key.");		//イメージURLリスト
						}
						dbMsg +=" , artist=" + artistName;/////////////////////////////////////
						bundle.putString("key.kekka", artistName);
						data.putExtras(bundle);
						myLog(TAG, dbMsg);
						setResult(RESULT_OK, data);		// setResult() で bundle を載せた送るIntent dataをセットする	// 第一引数は…Activity.RESULT_OK, Activity.RESULT_CANCELED など
						break;
//					default:
//						break;
					}
				}else if(resultCode == RESULT_CANCELED){				//リストから何も選択されずに戻された時
				}
			}
			quitMe();			//
			long end=System.currentTimeMillis();		// 終了時刻の取得
			dbMsg=dbMsg +";"+ (int)((end - start)) + "m秒で終了";
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}		//http://fernweh.jp/b/startactivityforresult/

	@Override
	protected void onRestart() {
		super.onRestart();
		final String TAG = "onRestart[CreditNameHensyuu]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		final String TAG = "onResume[CreditNameHensyuu]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
	//		myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		final String TAG = "onStart[CreditNameHensyuu]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		final String TAG = "[CreditNameHensyuu]onPause";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		final String TAG = "onStop[CreditNameHensyuu]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		final String TAG = "onDestroy[CreditNameHensyuu]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

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
