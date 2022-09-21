package com.hijiyam_koubou.marasongs;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrgUtil  extends Activity{				//
//	public Locale locale;	// アプリで使用されているロケール情報を取得
//	public SimpleDateFormat timeFormatte;

//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//	super.onCreate(savedInstanceState);
//		locale = Locale.getDefault();	// アプリで使用されているロケール情報を取得
//	//	SimpleDateFormat timeFormatter = new SimpleDateFormat ("HH:mm:ss");
//	//	timeFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
//	}

	public String dbBlock;

///日時関数///////////////////////////////////////////////////////////////////////////////////////////////////
	public SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy'年'MM'月'dd'日'HH'時'mm'分'ss'秒'");	///(1970年01月16日22時09分37秒＝1343377189
	public SimpleDateFormat sdf_ms = new SimpleDateFormat("mm:ss");
	public SimpleDateFormat sdf_hms = new SimpleDateFormat("kk:mm:ss");		//"HH:mm:ss"	kk	"'HH'時間'mm'分'ss'秒'"	"kk:mm:ss"

			//Use a literal 'H' (for compatibility with SimpleDateFormat and Unicode) or 'k' (for compatibility with Android releases up to and including Jelly Bean MR-1) instead. Note that the two are incompatible.
	public SimpleDateFormat sdf_mss = new SimpleDateFormat("mm:ss SSS");		//02;03 414=123414
	public SimpleDateFormat sdf_yyyyMMddHHmm = new SimpleDateFormat("yyyy/MM/dd HH:mm");		//02;03 414=123414
	public SimpleDateFormat sdf_ts_yyyyMMddHHmm = new SimpleDateFormat("yyyyMMddHHmm");		//02;03 414=123414


	public int reFormartMSS(String fromatSt) {
		int kekka=0;
		try{
			dbBlock =fromatSt+"⇒";////////////////////////////////////////////////////////////////////////////
			if(fromatSt != null){
				String[] mStrs = fromatSt.split(":");
				dbBlock = dbBlock+mStrs[0]+"[分]と"+mStrs[1]+"⇒";////////////////////////////////////////////////////////////////////////////
				String[] oStrs =mStrs[1].split(" ");
				dbBlock = dbBlock+ oStrs[0]+"[秒]と"+oStrs[1]+"[ms]を";////////////////////////////////////////////////////////////////////////////
				kekka=Integer.valueOf(oStrs[0])*1000+Integer.valueOf(oStrs[1]);
				dbBlock =dbBlock+ dbBlock+ kekka +"⇒";////////////////////////////////////////////////////////////////////////////
				kekka=Integer.valueOf(mStrs[0])*1000*60+kekka;
				dbBlock =dbBlock+ kekka;////////////////////////////////////////////////////////////////////////////
//				Log.d("reFormartMSS",dbBlock);
			}
		} catch (Exception e) {
 			myErrorLog("reFormartMSS",dbBlock+"；"+e);
	return 0;
		}
		return kekka;
	}

	public int reFormartSDF1(String fromatSt) {
		int kekka=0;
		try{
			dbBlock ="fromatSt="+fromatSt;/////////////////////yyyy'年'MM'月'dd'日'HH'時'mm'分'ss'秒'//////////////////
			if(fromatSt != null){
				String[] yStrs = fromatSt.split("年");
				dbBlock = yStrs[0]+"と"+yStrs[1];///////////"/////////////////////////////////////
				String[] mStrs = yStrs[1].split("月");
				dbBlock = mStrs[0]+"と"+mStrs[1];///////////"/////////////////////////////////////
				String[] dStrs = mStrs[1].split("日");
				dbBlock = dStrs[0]+"と"+dStrs[1];///////////"/////////////////////////////////////
				String[] hStrs = dStrs[1].split("時");
				dbBlock = hStrs[0]+"と"+hStrs[1];///////////"/////////////////////////////////////
				String[] mnStrs = hStrs[1].split("分");
				dbBlock = mnStrs[0]+"と"+mnStrs[1];///////////"/////////////////////////////////////
				int scInt = Integer.valueOf(mnStrs[1].substring(0, 2));
				dbBlock = String.valueOf(scInt);///////////"/////////////////////////////////////
				kekka=Integer.valueOf(mnStrs[0])*60+scInt;
				dbBlock = mnStrs[0]+"と"+scInt+"で"+ kekka;///////////"/////////////////////////////////////
				kekka=Integer.valueOf(hStrs[0])*60*60+kekka;
				dbBlock = hStrs[0]+"時を加えて"+Integer.valueOf(kekka);///////////"/////////////////////////////////////
				kekka=Integer.valueOf(dStrs[0])*60*60*24+kekka;
				dbBlock = dStrs[0]+"日を加えて"+Integer.valueOf(kekka);///////////"/////////////////////////////////////
	//			Log.d("reFormartSDF1",dbBlock);

				int monthVai=Integer.valueOf(mStrs[0]);
				int yearVai=Integer.valueOf(yStrs[0]);
				int mInDays;
				switch(monthVai) {
					case 2:
						if(Math.round(yearVai/4) == yearVai/4){
							mInDays=29;
						}else{
							mInDays=28;
						}
						break;
					case 1:
					case 3:
					case 5:
					case 7:
					case 8:
					case 10:
					case 12:
						mInDays=31;
						break;
					default:
						mInDays=30;
						break;
				}
				kekka=monthVai*60*60*24*mInDays+kekka;
				dbBlock = monthVai+"月を加えて"+Integer.valueOf(kekka);///////////"/////////////////////////////////////
	//			Log.d("reFormartSDF1",dbBlock);

				if(Math.round(yearVai/4) == yearVai/4){
					mInDays=29;
				}else{
					mInDays=28;
				}
				kekka=yearVai*60*60*24*mInDays*12+kekka;
				dbBlock =yearVai+"年を加えて"+Integer.valueOf(kekka);///////////"/////////////////////////////////////
				Log.d("reFormartSDF1",dbBlock);
			}
		} catch (Exception e) {
 			myErrorLog("reFormartSDF1",dbBlock+"；"+e);
	return 0;
		}
		return kekka;
	}

	public String nowTime(boolean apBool,boolean secBool,boolean mSecBool){		//現在時刻を文字列で返す　第一引数；trueで24時間制,第二引数；trueで秒追加,第三引数；trueでm秒追加
		String retString="";
		Calendar calendar = Calendar.getInstance();
		String am_pm;		// AM/PMの取得(0:AM,1:PM)
		if(calendar.get(Calendar.AM_PM) == 0) {
			am_pm = "午前"; // 0なら午前
		}else{
			am_pm = "午後"; // 1なら午後
		}
		int hour12 = calendar.get(Calendar.HOUR);					// 時間の取得(12時間単位)
		int hour24 = calendar.get(Calendar.HOUR_OF_DAY);			// 時間の取得(24時間単位)
		int minute = calendar.get(Calendar.MINUTE);					// 分の取得
		int second = calendar.get(Calendar.SECOND);					// 秒の取得
		int millisec = calendar.get(Calendar.MILLISECOND);			// ミリ秒の取得
		String timeFormat1 = am_pm + hour12 + "時" + minute + "分";	// 表示形式1(午前 XX時XX分)
		String timeFormat2 = hour24 + "時" + minute + "分";			// 表示形式2(XX時XX分)
		if(apBool){		//trueで24時間制
			retString=timeFormat2;
		}else{
			retString=timeFormat1;
		}
		if(secBool){		//trueで秒追加
			retString=retString + second + "秒";		//XX秒
		}
		if(mSecBool){		//trueでm秒追加
			retString=retString  + millisec;		//XX秒XXX
		}
		return retString;
	}
///////////////////////////////////////////////////////////////////////////////////////////////////日時関数///

///型確認///////////////////////////////////////////////////////////////////////////////////////////////////
	public boolean colominUMU(){
		boolean retBool =false;
		final String TAG = "colominUMU";
		String dbBlock="開始";///////////////////////////////////////////////////////////////////////////////
		try {
		} catch (Exception e) {
			myErrorLog(TAG,dbBlock+"でエラー発生；"+e);
		}
		return retBool;
	}

	public int orgCursorFeldType(Cursor cursor ,int position,int columnIndex)throws Exception{ //カーソルフィールドの型判定
		int retTyp = 0;
		final String TAG = "orgCursorFeldType";
		String dbBlock="開始";///////////////////////////////////////////////////////////////////////////////
		try {
			if(Build.VERSION.SDK_INT >= 11){
				retTyp =orgCursorFeldType11( cursor , position, columnIndex);
			}else{
				try{
			//		dbBlock=position +";"+ columnIndex + ";"+cursor.getColumnName(columnIndex);///////////////////////////////////////////////////////////////////////////////
					dbBlock= dbBlock + "getInt";///////////////////////////////////////////////////////////////////////////////
					if(isNum(cursor.getString(columnIndex))){			//数字ならtrue
						cursor.getInt(columnIndex);
						retTyp = Cursor.FIELD_TYPE_INTEGER;			//1
					}else{
						retTyp = Cursor.FIELD_TYPE_STRING;		//3
					}
				} catch (Exception e) {
					try{
						dbBlock= dbBlock + "getString";///////////////////////////////////////////////////////////////////////////////
						cursor.getString(columnIndex);
						retTyp = Cursor.FIELD_TYPE_STRING;		//3
					} catch (Exception e1) {
						try{
							dbBlock= dbBlock + "getFloat";///////////////////////////////////////////////////////////////////////////////
							cursor.getFloat(columnIndex);
							retTyp = Cursor.FIELD_TYPE_FLOAT;		//2
						} catch (Exception e2) {
							try{
								dbBlock= dbBlock + "getBlob";///////////////////////////////////////////////////////////////////////////////
								cursor.getBlob(columnIndex);
								retTyp = Cursor.FIELD_TYPE_BLOB;	//4
							} catch (Exception e3) {
								retTyp = Cursor.FIELD_TYPE_NULL;	//0
							}
						}
					}
				}
			}
		} catch (Exception e) {
			myErrorLog(TAG,dbBlock+"でエラー発生；"+e);
		}
		return retTyp;
	}

	public String getCursorStr(Cursor cursor ,int position,int columnIndex)throws Exception{ //カーソルフィールドのデータ読出し
		String retStr = null;
		final String TAG = "getCursorStr";
		String dbBlock="開始";///////////////////////////////////////////////////////////////////////////////
		try {
			if(columnIndex > 0){
				dbBlock=position +";"+ columnIndex + ";"+cursor.getColumnName(columnIndex);///////////////////////////////////////////////////////////////////////////////
				try{
					dbBlock= dbBlock + "getString";///////////////////////////////////////////////////////////////////////////////
					retStr = cursor.getString(columnIndex);
				} catch (Exception e) {
					try{
						dbBlock= dbBlock + "getInt";///////////////////////////////////////////////////////////////////////////////
						if(isNum(cursor.getString(columnIndex))){			//数字ならtrue
							retStr= String.valueOf(cursor.getInt(columnIndex));
						}
					} catch (Exception e1) {
						try{
							dbBlock= dbBlock + "getFloat";///////////////////////////////////////////////////////////////////////////////
							retStr = String.valueOf(cursor.getFloat(columnIndex));
						} catch (Exception e2) {
							try{
								dbBlock= dbBlock + "getBlob";///////////////////////////////////////////////////////////////////////////////
								retStr = String.valueOf(cursor.getBlob(columnIndex));
							} catch (Exception e3) {
								retStr = "error " + e3;	//0
							}
						}
					}
				}
			}
		} catch (Exception e) {
			myErrorLog(TAG,dbBlock+"でエラー発生；"+e);
		}
		return retStr;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public int orgCursorFeldType11(Cursor cursor ,int position,int columnIndex){
		int retTyp = 0;
		final String TAG = "orgCursorFeldType11";
		String dbBlock="開始";///////////////////////////////////////////////////////////////////////////////
		try {
			retTyp = cursor.getType(columnIndex);
		} catch (Exception e) {
			myErrorLog(TAG,dbBlock+"でエラー発生；"+e);
		}
		return retTyp;
	}

	public String retCusolVal(Cursor cursor ,int cType , int position,int columnIndex){
		String cVal = null ;
		final String TAG = "retCusolVal";
		String dbBlock="開始";///////////////////////////////////////////////////////////////////////////////
		try {
			switch(cType) {
			case Cursor.FIELD_TYPE_NULL:									//
				break;
			case Cursor.FIELD_TYPE_INTEGER:									//0
				cVal =String.valueOf(cursor.getInt(columnIndex));
				break;
			case Cursor.FIELD_TYPE_FLOAT:									//
				cVal =String.valueOf(cursor.getFloat(columnIndex));
				break;
			case Cursor.FIELD_TYPE_STRING:									//
				cVal =String.valueOf(cursor.getString(columnIndex));
				break;
			case Cursor.FIELD_TYPE_BLOB:									//
				cVal =String.valueOf(cursor.getBlob(columnIndex));
				break;
			}
		} catch (Exception e) {
			myErrorLog(TAG,dbBlock+"でエラー発生；"+e);
		}
		return cVal;
	}

	public String retTypeVal(int cType){
		String cTypeName = null ;
		final String TAG = "retCusolVal";
		String dbBlock="開始";///////////////////////////////////////////////////////////////////////////////
		try {
			switch(cType) {
			case Cursor.FIELD_TYPE_NULL:									//
				break;
			case Cursor.FIELD_TYPE_INTEGER:									//0
				cTypeName ="INTEGER";
				break;
			case Cursor.FIELD_TYPE_FLOAT:									//
				cTypeName ="FLOAT";
				break;
			case Cursor.FIELD_TYPE_STRING:									//
				cTypeName ="STRING";
				break;
			case Cursor.FIELD_TYPE_BLOB:									//
				cTypeName ="BLOB";
				break;
			}
		} catch (Exception e) {
			myErrorLog(TAG,dbBlock+"でエラー発生；"+e);
		}
		return cTypeName;
	}

	public boolean isNum(String motoStr){		//数字ならtrue
		final String TAG = "isNum";
		String dbBlock="motoStr="+ motoStr;
		boolean retBool = true ;
		try {
			for(int i = 0 ;i< motoStr.length() ;i++ ){
				Character tChar = motoStr.toCharArray()[i];										//先頭一文字を拾って
				int tCord = tChar;											//その文字コードを取得
				dbBlock=i + "/" + (motoStr.length()-1)+ "="+ tChar+ "="+ tCord+ "[数字なら"+ (int)('0')+ "～"+ (int)('9')+ "]";
				if((tCord>=('0') && tCord<=('9')) || String.valueOf(tChar).equals(".")){
				}else{
					retBool = false ;
					break;
				}
			}
	//		myLog(TAG,dbBlock);
		} catch (NumberFormatException e) {
			myErrorLog(TAG,dbBlock+"でエラー発生；"+e);
		}
		return retBool;
	}
///////////////////////////////////////////////////////////////////////////////////////////////////型確認///
	public String denwaBangouFormat(String pNumStr , Context context){	//電話番号の文字列に区切り文字を入れて返す
		String wStr = pNumStr ;
		final String TAG = "denwaBangouFormat";
		String dbBlock="開始";
		try {
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);			//	getSharedPreferences(prefFname,MODE_PRIVATE);
			boolean pBangou_kigiriAri = sharedPref.getBoolean( "bangou_kigirimoji" , true);			//電話番号は - を含んだ表示

			dbBlock="電話番号="+pNumStr + ",区切="+pBangou_kigiriAri + ",数値="+isNum(pNumStr);//////////////////////////////////
	//		myLog(TAG,dbBlock);
			if( pBangou_kigiriAri && isNum(pNumStr)){	//電話番号に区切り記号を入れる
				switch(pNumStr.length()) {
				case 11:				//携帯電話　090-4567-89ab
					if(pNumStr.substring(2, 3).equals("0")){
						wStr = pNumStr.substring(0, 3) +"-"+ pNumStr.substring(3, pNumStr.length()-4) +"-"+ pNumStr.substring(pNumStr.length()-4);
					}
					break;
				case 10:				//一般電話　0824-20-1558 1234-56-7890
					if(pNumStr.substring(0, 4).equals("0120")){			//0120-567-890
						wStr = pNumStr.substring(0, 4) +"-"+ pNumStr.substring(4, pNumStr.length()-3) +"-"+ pNumStr.substring(pNumStr.length()-3);
					}else if(pNumStr.substring(0, 2).equals("03") || pNumStr.substring(0, 2).equals("06")){
						wStr = pNumStr.substring(0, 2) +"-"+ pNumStr.substring(2, pNumStr.length()-4) +"-"+ pNumStr.substring(pNumStr.length()-4);
					}else {
						wStr = pNumStr.substring(0, 3) +"-"+ pNumStr.substring(3, pNumStr.length()-4 ) +"-"+ pNumStr.substring(pNumStr.length()-4);
					}
					break;
				default:
					wStr = pNumStr.substring(0, pNumStr.length()-4 ) +"-"+ pNumStr.substring(pNumStr.length()-4);
					break;
				}
			}
		} catch (NumberFormatException e) {
			myErrorLog(TAG,dbBlock+"でエラー発生；"+e);
		}
		dbBlock= dbBlock + "⇒"+wStr;//////////////////////////////////
//		myLog(TAG,dbBlock);
		return wStr;
	}

///文字関数///////////////////////////////////////////////////////////////////////////////////////////////////
	public String repChr(String Org,String sep,String rep){	//第一引数の文字列から第二引数で指定した文字列を第三引数の文字列に置き換えて返す
		String[] oStrs = Org.split(sep);		//先頭の”/”が[0]に入っている？
		String retStrs = "";
			int i;
		try{
		//	Log.d("repChr","repChr1:元；" + oStrs.length + "分割,"+sep+"を"+rep+"に置き換えます。");
			for (i = 1; i <(oStrs.length); i++) {
			dbBlock = i+"/"+oStrs.length+"="+retStrs+"("+Org+"から"+sep+"を"+rep+"に)";////////////////////////////////////////////////////////////////////////////
				if(i ==(oStrs.length-1)){
					retStrs=retStrs+oStrs[i];		//最後はrepを付けない
				}else{
					retStrs=retStrs+oStrs[i]+rep;
				}
			}
	//		Log.d("repChr","repChr3("+i+")"+retStrs);
		} catch (Exception e) {
			myErrorLog("repChr",dbBlock+"；"+e);
		}
		return retStrs;
	}

	public String checkRepChr(String Org,String sep,String rep){	//第一引数の文字列に第二引数で指定した文字があればを第三引数の文字列に置き換えて返す
		final String TAG = "checkRepChr[OrgUtil]";
		String dbMsg="開始;";/////////////////////////////////////
		String retStrs = Org;
		try{
			dbMsg=Org +" は "+ sep;/////////////////////////////////////
			while(Org.contains(sep)){
				dbMsg=dbMsg +" を含む ";/////////////////////////////////////
				retStrs = Org.substring(0, Org.indexOf(sep)) + rep;	//
		//		if(retStrs.length()<3){
					retStrs = retStrs  + Org.substring( Org.indexOf(sep)+1);
		//		}
				dbMsg=dbMsg +" >> " + retStrs;/////////////////////////////////////
				Log.d(TAG,dbMsg);
				Org = retStrs;
				dbMsg=Org +" は "+ sep;/////////////////////////////////////
			}
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"；"+e);
		}
		return retStrs;
	}

	public boolean isInListString(List<String> groups , String tStr){		//渡された文字が既にリストに登録されていればtrueを返す
		boolean retBool =false;
		final String TAG = "isInListString[OrgUtil]";
		String dbBlock= groups.size() + "件目で" + tStr;//////////////////////////////////////////
		try{
			int i=1;
			for(String tName:groups){		//
//			for(i=0;i< groups.size()-1 ;i++){
//				String tName = groups.get(i);
				dbBlock= i +"/" +groups.size()+";" + tStr +"/" +tName ;////////////////////////////////////////////////////////
				if(tStr.equals(tName)){							//tStr.equals(tName) || tName.equals(tStr)
					retBool = true;
					break;
				} else if( tStr.startsWith(tName) ){			//先に出てきた名前のみを優先
					retBool = true;
					break;
				} else if( tStr.contains(tName) ){
					retBool = true;
					break;
				}
			}
			dbBlock= dbBlock + ",retBool=" + retBool  ;//////////////////////////////////////////
//			dbBlock="tStr=" + tStr +"が"+ groups.get(0) + "～" + groups.get(groups.size()-1) + "に有るか；件数="+ groups.size() ;//////////////////////////////////////////
		//	myLog(TAG,dbBlock);
		} catch (Exception e) {
			myErrorLog(TAG, dbBlock +"で"+e.toString());
		}
		return retBool;
	}

	public List<String> add2List(List<String> groups , String tStr) {		//一致しない文字だけをリスト登録
		final String TAG = "add2List[OrgUtil]";
		String dbBlock="開始";
		try{
			if(tStr == null){
			}else if(tStr.equals("null")){
			}else if(tStr.equals("")){
			}else{
				boolean kakikomi = true;
				for(String tName:groups){		//
					if(tStr.equals(tName)){
						kakikomi = false;
					}
				}
				if(kakikomi){
					groups.add(tStr);
				}
			}
			//		myLog(TAG,dbBlock);
		} catch (Exception e) {
			myErrorLog(TAG, dbBlock +"で"+e.toString());
		}
		return groups;
	}


	public boolean isInArrayString(String[] groups , String tStr){		//渡された文字が既にリストに登録されていればtrueを返す
		boolean retBool =false;
		final String TAG = "isInArrayString[OrgUtil]";
		String dbBlock= groups.length+ "件目で" + tStr;//////////////////////////////////////////
		try{
			int i=1;
			for(String tName:groups){		//for(i=0;i<pCount;i++){
				dbBlock= i +"/" +groups.length +";" + tStr +"/" +tName ;////////////////////////////////////////////////////////
				if(tStr.equals(tName)){							//tStr.equals(tName) || tName.equals(tStr)
					retBool = true;
					break;
				} else if( tStr.startsWith(tName) ){			//先に出てきた名前のみを優先
					retBool = true;
					break;
				} else if( tStr.contains(tName) ){
					retBool = true;
					break;
				}
			}
			dbBlock= dbBlock + ",retBool=" + retBool  ;//////////////////////////////////////////
		//	myLog(TAG,dbBlock);
//			dbBlock="tStr=" + tStr +"が"+ groups.get(0) + "～" + groups.get(groups.size()-1) + "に有るか；件数="+ groups.size() ;//////////////////////////////////////////
		} catch (Exception e) {
			myErrorLog(TAG, dbBlock +"で"+e.toString());
		}
		return retBool;
	}

/**
 * 渡された文字が既にリストに登録されていればインデックスを返す
 * */
	public int mapIndex(List<Map<String, Object>> groups , String key , String tStr){		//渡された文字が既にリストに登録されていればインデックスを返す
		int retINt =-1;
		final String TAG = "mapIndex[OrgUtil]";
		String dbBlock= groups.size() + "件目で" + tStr;//////////////////////////////////////////
		try{
			int i=1;
		//	for(String tName:groups){		//
			for(i=0;i< groups.size() ;i++){
				String tName = String.valueOf(groups.get(i).get(key));
				dbBlock= i +"/" +groups.size()+";" + tStr +" ;;; " +tName ;////////////////////////////////////////////////////////
				if(tStr.equals(tName)){							//tStr.equals(tName) || tName.equals(tStr)
					retINt = i;
					break;
				} else if( tStr.startsWith(tName) ){			//先に出てきた名前のみを優先
					retINt = i;
					break;
				} else if( tStr.contains(tName) ){
					retINt = i;
					break;
				} else if( tName.contains(tStr) ){
					retINt = i;
					break;
				}
			}
			dbBlock= dbBlock + ",retINt=" + retINt  ;//////////////////////////////////////////
			if(-1 < retINt){
				dbBlock="tStr=" + tStr +"が"+ groups.get(0).get(key) + "～" + groups.get(i).get(key) + "に有るか；件数="+ groups.size() ;//////////////////////////////////////////
			}
	//		myLog(TAG,dbBlock);
		} catch (Exception e) {
			myErrorLog(TAG, dbBlock +"で"+e.toString());
		}
		return retINt;
	}

/**
 * 渡された文字が既にリストに登録されていればインデックスを返す
 * */
	public int mapEqualIndex(List<Map<String, Object>> groups , String key , String tStr){		//渡された文字が既にリストに登録されていればインデックスを返す
		int retINt =-1;
		final String TAG = "mapIndex[OrgUtil]";
		String dbBlock= groups.size() + "件目で" + tStr;//////////////////////////////////////////
		try{
			int i=1;
		//	for(String tName:groups){		//
			for(i=0;i< groups.size() ;i++){
				String tName = String.valueOf(groups.get(i).get(key));
				dbBlock= i +"/" +groups.size()+";" + tStr +" ;;; " +tName ;////////////////////////////////////////////////////////
				if(tStr.equals(tName)){							//tStr.equals(tName) || tName.equals(tStr)
					retINt = i;
					break;
				}
			}
			dbBlock= dbBlock + ",retINt=" + retINt  ;//////////////////////////////////////////
			if(-1 < retINt){
				dbBlock="tStr=" + tStr +"が"+ groups.get(0).get(key) + "～" + groups.get(i).get(key) + "に有るか；件数="+ groups.size() ;//////////////////////////////////////////
			}
	//		myLog(TAG,dbBlock);
		} catch (Exception e) {
			myErrorLog(TAG, dbBlock +"で"+e.toString());
		}
		return retINt;
	}

	/**
	 * 渡された文字が既にリストに登録されていれば追加、短い一致名が有れば置換えた配列を返す
	 * @ ArrayList<String> groups
	 * @ String tStr
	 * */
	public ArrayList<String> retInListString(ArrayList<String> groups , String tStr){		//渡された文字が既にリストに登録されていれば追加、短い一致名が有れば置換え
		boolean retBool =false;
		final String TAG = "retInListString[OrgUtil]";
		String dbBlock= "開始";//////////////////////////////////////////
		try{
			dbBlock= groups.size() + "件から" + tStr + "を照合";//////////////////////////////////////////
			int i=1;
			boolean tuiki =true;
			String dbBlock2 = null;
			for(String tName:groups){		//for(i=0;i<pCount;i++){
				dbBlock2= tName +" と" ;////////////////////////////////////////////////////////
				if(tStr.equals(tName)){							//tStr.equals(tName) || tName.equals(tStr)
					dbBlock2= dbBlock + "；equals,"  ;//////////////////////////////////////////
					tuiki =false;
					break;
				} else if( tStr.startsWith(tName) ){			//先に出てきた名前のみを優先
					dbBlock2= dbBlock + "；startsWith,"  ;//////////////////////////////////////////
					tuiki =false;
					break;
				} else if( tStr.endsWith(tName) ){			//後に出てきた名前のみを優先
					dbBlock2= dbBlock + "；endsWith,"  ;//////////////////////////////////////////
					tuiki =false;
					break;
				} else if( tStr.contains(tName) || tName.contains(tStr)){
					dbBlock2= dbBlock + "contains,"  ;//////////////////////////////////////////
					if(tStr.length() < tName.length()){
						groups.remove(tName);
						groups.add(tStr);
						dbBlock2= dbBlock + ">>" + tName +"を" + tStr +"に置換え";//////////////////////////////////////////
					}
					tuiki =false;
	//				myLog(TAG,dbBlock2);
					break;
				}
			}
			if(tuiki){
				groups.add(tStr);
				dbBlock= groups.size() + "件目に" + groups.get(groups.size()-1) +"を追加"  ;//////////////////////////////////////////
			}else{
				dbBlock = dbBlock + dbBlock2 + "で追加しない";
			}
//			dbBlock="tStr=" + tStr +"が"+ groups.get(0) + "～" + groups.get(groups.size()-1) + "に有るか；件数="+ groups.size() ;//////////////////////////////////////////
		} catch (Exception e) {
			myErrorLog(TAG, dbBlock +"で"+e.toString());
		}
		return groups;
	}

	/**
	 * 渡された文字全体一致もしくは渡された文字を含むメンバーが有れば短い一致名を返す
	 * @ ArrayList<String> groups
	 * @ String tStr
	 * */
	public String retShortName(ArrayList<String> groups , String tStr){		//渡された文字が既にリストに登録されていれば追加、短い一致名が有れば置換え
		String retStr = null;
		final String TAG = "retInListString[OrgUtil]";
		String dbBlock= "開始";//////////////////////////////////////////
		try{
			dbBlock= groups.size() + "件から" + tStr + "を照合";//////////////////////////////////////////
			int i=1;
	//		boolean tuiki =true;
			String dbBlock2 = null;
			for(String tName:groups){		//for(i=0;i<pCount;i++){
				dbBlock2= tName +" と" ;////////////////////////////////////////////////////////
				if(tStr.equals(tName)){							//tStr.equals(tName) || tName.equals(tStr)
					dbBlock2= dbBlock + "；equals,"  ;//////////////////////////////////////////
					retStr = tName;
		//			tuiki =false;
					break;
				} else if( tStr.startsWith(tName) ){			//先に出てきた名前のみを優先
					dbBlock2= dbBlock + "；startsWith,"  ;//////////////////////////////////////////
					retStr = tName;
		//			tuiki =false;
					break;
				} else if( tStr.endsWith(tName) ){			//後に出てきた名前のみを優先
					dbBlock2= dbBlock + "；endsWith,"  ;//////////////////////////////////////////
					retStr = tName;
		//			tuiki =false;
					break;
				} else if( tStr.contains(tName) ){
					dbBlock2= dbBlock + "contains,"  ;//////////////////////////////////////////
					if(tStr.length() < tName.length()){
						groups.remove(tName);
						groups.add(tStr);
						dbBlock2= dbBlock + ">>" + tName +"を" + tStr +"に置換え";//////////////////////////////////////////
						retShortName(groups , tStr);
					}
					retStr = tName;
		//			tuiki =false;
					break;
				}
			}
//			if(tuiki){
//				groups.add(tStr);
//				dbBlock= groups.size() + "件目に" + groups.get(groups.size()-1) +"を追加"  ;//////////////////////////////////////////
//			}else{
//				dbBlock = dbBlock + dbBlock2 + "で追加しない";
//			}
			dbBlock= dbBlock + dbBlock2 + ">retStr>" + retStr;//////////////////////////////////////////
	//		myLog(TAG,dbBlock);
//			dbBlock="tStr=" + tStr +"が"+ groups.get(0) + "～" + groups.get(groups.size()-1) + "に有るか；件数="+ groups.size() ;//////////////////////////////////////////
		} catch (Exception e) {
			myErrorLog(TAG, dbBlock +"で"+e.toString());
		}
		return retStr;
	}

	/**
	 * リストに該当する名称が有ればそれを返す
	 * ***/
	public String sarchiInListString(List<String> groups , String tStr){		//渡された文字が既にリストに登録されていれば該当する文字を返す
		String retStr = null ;
		final String TAG = "sarchiInListString";
		String dbMsg ="[OrgUtil]";
		dbMsg += groups.size() + "件中に " + tStr;
		try{
			int i=1;
			for(String tName:groups){		//for(i=0;i<pCount;i++){
//				dbMsg += " tName=" + tName;
				if(tStr.equals(tName)){							//tStr.equals(tName) || tName.equals(tStr)
					retStr = tName;
					break;
				} else if( tStr.startsWith(tName) ){
					retStr = tName;
					break;
//				} else if( tStr.endsWith(tName) ){
//					retStr = tName;
//					break;
				}
			}
			if (retStr != null) {
				dbMsg += " >>" + i + "/" + groups.size() + "件目に有り";
			}else{
				dbMsg +=  " >>該当無し" ;//////////////////////////////////////////
			}
//			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG, dbBlock +"で"+e.toString());
		}
		return retStr;
	}

	public List<String> howInListString(List<String> groups , String tStr){		//渡された複数の文字列のうちいくつがリストに登録されているか
		List<String> retStrs = null;
		final String TAG = "howInListString[OrgUtil]";
		String dbBlock= groups.size() + "件中、" + tStr;//////////////////////////////////////////
		try{
			int ｊ=1;
			String[] testS= tStr.split(" ");
			for(String checkName:testS){		//for(i=0;i<pCount;i++){
				int i=1;
				for(String tName:groups){		//for(i=0;i<pCount;i++){
					if(checkName.equals(tName)){
						dbBlock= ｊ +"-"+i +"/" +groups.size()+";" + tStr +"/" +tName +"と"+checkName;////////////////////////////////////////////////////////
						retStrs.add(tName);
						myLog(TAG,dbBlock);
					}
				}
			}

//			dbBlock="tStr=" + tStr +"が"+ groups.get(0) + "～" + groups.get(groups.size()-1) + "に有るか；件数="+ groups.size() ;//////////////////////////////////////////
		} catch (Exception e) {
			myErrorLog(TAG,"[OrgUtil]" +dbBlock+e.toString());
		}
		return retStrs;
	}

	public String mojiretuKasan(String moto , String tuika){			//文字列加算
		final String TAG = "sonotaSTr";
		String dbBlock=moto + "）に加えて（" + tuika ;
		try{
			if(tuika==null || tuika.equals("") || tuika.equals("null")){
				return moto;
			}else{
				if(moto==null || moto.equals("") || moto.equals("null")){
					return tuika;
				}else{
					moto = moto +","+ tuika;
				}
			}
		} catch (Exception e) {
			myErrorLog(TAG,dbBlock+"でエラー発生；"+e);
		}
		return moto;
	}

	public int strIndexInList(String  str , List<String> blockList ,int startB){			//指定された文字列を含むList<String>の要素を指定されたインデックスから検索する
		final String TAG = "strIndexInList[OrgUtil]";
		String dbBlock= blockList.toString() + "の" + startB + "番目から「" + str + "」が最初に入っているインデックスを返す";/////////////////////////////////////
		int retInt = -1;
		try{
			for(int i =startB  ; i < blockList.size() ; i++){
				String ckStr = blockList.get(i);
				if(ckStr.indexOf(str) >= 0 ){
					retInt = i;
					break;
				}
			}
			dbBlock= dbBlock + ">>" + retInt;/////////////////////////////////////
//			myLog(TAG,dbBlock);
		} catch (Exception e) {
			myErrorLog(TAG,dbBlock+"で"+e);
		}
	return retInt;
	}

	public int strLastIndexInList(String  str , List<String> blockList ,int startB){			//指定された文字列を含むList<String>の要素を終わりから検索する
		final String TAG = "strLastIndexInList[OrgUtil]";
		String dbBlock= blockList.toString() + "から「" + str + "」が最後に入っているインデックスを返す";/////////////////////////////////////
		int retInt = -1;
		try{
			for(int i = blockList.size()-1-startB ; i >= 0 ; i--){
				String ckStr = blockList.get(i);
				if(ckStr.indexOf(str) >= 0 ){
					retInt = i;
					break;
				}
			}
		} catch (Exception e) {
			myErrorLog(TAG,dbBlock+"で"+e);
		}
	return retInt;
	}

	public String lArray2String( List<String> blockList){			//List<String>の要素を文字列にして戻す
		final String TAG = "strLastIndexInList[OrgUtil]";
		String dbBlock= blockList.toString() + "を文字列に変換";/////////////////////////////////////
		String  retStr = "";			//nullで初期化すると先頭にnullが残る
		try{
			List<String>chList= new ArrayList<String>();
			for(String rStr:blockList){
				if(isNum(rStr) && isNum(retStr) && ! retStr.equals("")){
					chList.add("*");
				}
				chList.add(rStr);
				retStr = rStr;
			}
			dbBlock= "chList=" + chList.toString() + ",blockList＝" + blockList.toString() +"["+ chList.size() + "/"+ blockList.size() +"]";/////////////////////////////////////
	//		myLog(TAG,dbBlock);
			if(chList.size() > blockList.size()){
				blockList.clear();
				for(String rStr:chList){
					blockList.add(rStr);
				}
			}
			retStr ="";
			for(String elStr :blockList){
				if( elStr != null){
					retStr = retStr + elStr;
				}
			}
		} catch (Exception e) {
			myErrorLog(TAG,dbBlock+"で"+e);
		}
	return retStr;
	}

	public String toCaps( String tStr){			//単語の先頭だけを大文字にする
		final String TAG = "toCaps";
		String dbBlock= "単語の先頭だけを大文字にする";/////////////////////////////////////
		String  retStr = "";			//nullで初期化すると先頭にnullが残る
		try{
			dbBlock = tStr;
			tStr = tStr.toLowerCase();				//Locale.JAPANESE
			int ｊ=1;
			String[] testS= tStr.split(" ");
			for(String checkName:testS){		//for(i=0;i<pCount;i++){
				if(0 < retStr.length()){
					retStr =retStr + " ";
				}
				retStr = retStr + checkName.substring(0, 1).toUpperCase() + checkName.substring(1);
			}
			dbBlock = dbBlock+ ">>"+ retStr;
			myLog(TAG,dbBlock);
		} catch (Exception e) {
			myErrorLog(TAG,dbBlock+"で"+e);
		}
	return retStr;
	}


////////////////////////////////////////////////////////////////////////////////////////////////////文字関数//
	public void saveMyText(String fileName, String str,boolean tuiki){ // throws IOException	//第一引数のファイル名で第二引数のテキストファイルを作成する
		//マニフェストファイルの設定；ファイルの入出力先が/data/data/パッケージ名/filesディレクトリ以下なら何も必要ありませんが、/sdcardディレクトリ以下などの場合は下の一行が必要になります。
		//<uses-permission android:name=”android.permission.WRITE_EXTERNAL_STORAGE” />

	//	Log.d("saveMyText",fileName+"へ"+str);
		try {
			File directory = Environment.getExternalStorageDirectory();	//「/sdcard」を取得
			if (directory.exists()){		//L1;ファイルシステム下にdirectoryが見つかった;Returns a boolean indicating whether this file can be found on the underlying file system.
				if(directory.canWrite()){	//L1;書き込み可能以外の状態ではFalse⇒書き込める状態の場合
					/*SampleDirフォルダを作成します。*/
					File wFileFull=new File(fileName);
					File wDir = new File(wFileFull.getParent());		// File file = new File(directory.getAbsolutePath() +dirName);
					if (!wDir.exists()){								//書込み先のデレクトリが無ければ
						wDir.mkdirs();									//作る	//mkdirが作成できないパス名でも作成する
					}
		//			Log.d("saveMyText",wFile.toString()+"を"+wDir.toString()+"に書き出しexists="+wDir.exists());
/*			FileOutputStream output = this.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
			output.write(str.getBytes());
			output.close(); 		*/			 //BufferedWriterを利用してテキスト書き込み
					BufferedWriter bw = new BufferedWriter(
							new OutputStreamWriter(
							new FileOutputStream(fileName,tuiki), StandardCharsets.UTF_8));	//※FileOutputStreamの第二引数をTrueにすると追加書き込みし、Falseにすると上書き作成します。
					bw.write(str);
					bw.close();					//BufferedWriterをクローズするのを忘れないようにする。
				}else{
		//			Log.d("saveMyText","saveMyText;2;directory.canWrite()="+directory.canWrite()+"なのでパッケージ内のfilesへの書き出し");
					FileOutputStream output = this.openFileOutput(fileName, MODE_WORLD_READABLE); // ストリームを開く
					//L1;Open a private file associated with this Context's application package for writing. Creates the file if it doesn't already exist.
					//MODE_PRIVATE ;作成したアプリでしか読み書きでない: the default mode, where the created file can only be accessed by the calling application (or all applications sharing the same user ID).
					//MODE_WORLD_READABLE；作成したファイルをすべてのアプリで読めるFile creation mode: allow all other applications to have read access to the created file.
					//MODE_WORLD_WRITEABLE；作成したファイルにすべてのアプリから書き込みできるFile creation mode: allow all other applications to have write access to the created file.
					output.write(str.getBytes()); // 書き込み
					output.close();// ストリームを閉じる
				}
			}else{
	//			Log.d("saveMyText","saveMyText;3;directory.exists()="+directory.exists());
			}
		} catch (UnsupportedEncodingException e) {
			myErrorLog("saveMyText","エラー;UnsupportedEncodingException;"+e.toString());
		} catch (FileNotFoundException e) {
		myErrorLog("saveMyText","エラー;FileNotFoundException;"+e.toString());
		} catch (IOException e) {
		myErrorLog("saveMyText","エラー;IOException;"+e.toString());
		} catch (Exception e) {
			myErrorLog("saveMyText","エラー発生；"+e);
		}
	}

	public List<String> fealdNameRead(Class target , List<String> names , List<String> jyogai ,boolean nomi) {		//URIの無いクラスは指定したオブジェクトのフィールド名をStringArrayオブジェクトで返す
		final String TAG = "fealdNameRead";
		String dbBlock="開始";
		try{
			String fName=null;
			String cvName=null;
			String cvVal=null;
			Field[] box = null;
			boolean jogaiSuru = false;
			String tName= target.getCanonicalName();				//getNameではセパレータ―が＄になる
			dbBlock="tName=" + tName + ",nomi=" + nomi;
	//		myLog(TAG,dbBlock);	//////////////////////////////////
			if(nomi){
				box =target.getDeclaredFields();	//targetクラスのフィールドのみ抽出
			}else{
				box =target.getFields();			//targetクラスに連携するものを含む
			}
			dbBlock="box=" + box.length + "個";
			tName=tName.replaceAll("android.provider.", "");
			dbBlock=target.getName() +"に"+ dbBlock;
			if(jyogai != null){
				dbBlock=dbBlock +",除外" +jyogai.size();
				jogaiSuru =true;
			}else{
				dbBlock=dbBlock +",除外指定無し;";
//				jogaiSuru =false;
			}
			for(Field fObj :box){
				try{
					boolean readOK = true;
					fName = fObj.toGenericString();	// fObj.getAnnotation(target);						//(tName + "." + fObj.getName());
					dbBlock="fName=" + fName;
					String[] tStrs =fName.split("android.provider.");
					fName = tStrs[1];
//					fName= fName.replaceAll("public static final java.lang.String android.provider.", "");			//"public static final java.lang.String android.provider.
//					fName= fName.replaceAll("public static final int android.provider.", "");			//ContactsContract$StatusColumns.AWAYfalse
					fName= fName.replace("$", ".");			//ontactsContract$StatusColumns.AVAILABLE..false
					if(fName.endsWith(".")){
						fName=fName.substring(0, fName.length()-1);
					}
					dbBlock=dbBlock + ">>" + fName;
					if(jogaiSuru){
						String chNAme = fObj.getName();
						if( isInListString(jyogai, chNAme)){							//以下はprojectionに通らない物
							dbBlock=chNAme+"を除外"+dbBlock;			//+ORGUT.isInListString(jyogai, chNAme) +";"+ dbBlock;
	//						myLog(TAG,dbBlock);
							readOK = false;
						}else{
							cvName=String.valueOf(fObj.get(target));			//Constant Valueに
						}
					}else{
						cvName=String.valueOf(fObj.get(target));			//Constant Valueに
					}
					dbBlock=fName + " : " + cvName + " : ";
					if(cvName == null){
						readOK = false;
						cvVal = "null";
					}else {
						if(isInListString(names, cvName)){			//重複
							readOK = false;
						}
						//フィールド名以外
			//			myLog(TAG,dbBlock);
						if(cvName.startsWith("content://")){			//URI
							readOK = false;
							cvVal = cvName;
						} else if( isNum(cvName)){			//データ　数字など
							readOK = false;
							cvVal = cvName;
						}else if(cvName.contains(".android.")){			//2)com.android.contacts.ACTION_ALL_REMOVE_MISSEDCALL_NOTIFICATION
							readOK = false;
							cvVal = cvName;
						}else if(cvName.contains(" ")){			//CallLog.Calls.EXPAND_SORT_ORDER(date DESC, _id DESC)
							readOK = false;
							cvVal = cvName;
						}else if(cvName.contains("type=")){			//02-19 16:41:10.915: I/fealdNameRead(562): 30) type=2
							readOK = false;
							cvVal = cvName;
						}
	//android.provider.CallLog.Calls.TAG;58)sc_call_stateでjava.lang.IllegalAccessException: access to field not allowed
					}
					if(readOK ){
						names.add(cvName);
						dbBlock=names.size()+":"+names.get(names.size()-1);
					}
					dbBlock=dbBlock +" ; "+ fName+ " : " + cvName + " : ";			//dbBlock +";"+ fName+ ":" + cvName + ":";
	//				myLog(TAG,dbBlock);
					cvName = null;			//のフィールド(Constant Value)
					cvVal = "";			//のフィールド(Constant Value)
				} catch (Exception e) {
					myErrorLog(TAG, dbBlock+"で"+e);
				}
			}
		} catch (Exception e) {
			myErrorLog(TAG,dbBlock+"で"+e);
		}
		return names;
	}

	public String array2Str(String Moto ,String checkStr ,String sepaStr){ //文字列になっている配列をsepaStrで分離してcheckStrを含む部分を返す
		String retStr = null;
		final String TAG = "array2Str";
		String dbBlock="Moto="+ Moto + "　から　"+ checkStr + "　を　"+ sepaStr + "　で分離";
		try {
			Moto = Moto.substring(2, Moto.length()-2);
			dbBlock=dbBlock + "\n>substring>"+Moto;
	//		myLog(TAG,dbBlock);
			String[] items = Moto.split(", ");			//ArrayList<Map<String,String>>();					// クリックされたアイテムを取得します
			for(String tStr : items){
				if(tStr.startsWith(checkStr)){
					retStr = tStr.substring(checkStr.length());
				}
			}
			dbBlock=dbBlock + "retStr="+retStr;
	//		myLog(TAG,dbBlock);
		} catch (Exception e) {
			myErrorLog(TAG,dbBlock+"で"+e);
		}
		return retStr;
	}

	///色関数///////////////////////////////////////////////////////////////////////////////////////////////////
	public float limitNum(float jyougen , float hsv , float kasan){		//上限値を超えた加算値は上限を引いて上限までの範囲内に収めて返す
		float retInt=hsv;
		final String TAG = "limitNum";
		String dbBlock="上限="+ jyougen + "で"+ hsv + "+"+ kasan;
		try {
			retInt=hsv + kasan;

			if(jyougen >1.00000001f){
				if(retInt > jyougen){
					retInt = retInt- jyougen;
				}
			}else{								//1以下の上限値で
				if(jyougen > 0){				//上限値1を超えたら
					if(retInt > jyougen){		//
						retInt = jyougen;		//上限値に
					}
				}
			}
			if(retInt<0){//負数になったら
				retInt = 0;
			}

			dbBlock=dbBlock+"="+retInt;
	//		myLog(TAG,dbBlock);
		} catch (Exception e) {
			myErrorLog(TAG,dbBlock+"；"+e);
		}
		return retInt;
	}

	public int sikicyouHenkou(int moto , float saido , float meido){		//元の色から彩度と明度を変更したカラー値を返す。
		int retInt=moto;
		final String TAG = "fezerColor";
		String dbBlock="元色="+ moto + "から彩度"+ saido + ",明度"+ meido;
		try {
			float[] hsv = new float[3];
			dbBlock=dbBlock + ">>元色のR="+Color.red(moto) + ",G="+ Color.green(moto) + ",B="+ Color.blue(moto);
			Color.RGBToHSV(Color.red(moto), Color.green(moto), Color.blue(moto), hsv);
			dbBlock=dbBlock + ">>元色の色相"+ hsv[0] + ",彩度"+ hsv[1] + ",明度"+ hsv[2];
//			float sikisouKasnn =240+(360/11)-5;
			hsv[1]=hsv[1] + saido;		//彩度
			if(hsv[1] > 1){
				hsv[1] = 0.999f;
			}else if(hsv[1] < 0){
				hsv[1] = 0.001f;
			}
			hsv[2]=hsv[2] + meido;		//明度
			if(hsv[2] > 1){
				hsv[2] = 0.999f;
			}else if(hsv[2] < 0){
				hsv[2] = 0.001f;
			}
			retInt = Color.HSVToColor( hsv );			//new float[] { 360, 1, 0.63 }
			dbBlock=dbBlock + "\n変更されたR="+Color.red(retInt) + ",G="+ Color.green(retInt) + ",B="+ Color.blue(retInt);
	//		myLog(TAG,dbBlock);
		} catch (Exception e) {
			myErrorLog(TAG,dbBlock+"；"+e);
		}
		return retInt;
	}
	//http://www.minc.ne.jp/~konda/web_resoce/js/colors/rgb2hsv.html

	///固有関数///////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * mapを使ったリストからインデックスを返す
	 * */
	public int retArreyIndex(List<Map<String, Object>> fList , String key , String fStr){		//mapを使ったリストからインデックスを返す
		int retInt =-1;
		final String TAG = "retArreyIndex";
		String dbMsg="開始;";/////////////////////////////////////
		try{
			dbMsg="fList=" + fList.size() + "件";
			dbMsg= dbMsg +"key=" + key;
			dbMsg= dbMsg +"fStr=" + fStr;
			for( int i =0 ;i < fList.size() ; i++ ){
				String tStr = String.valueOf(fList.get( i ).get(key));
				if(fStr.equals(tStr)){
					return i;
				}
			}
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return retInt;
	}

	/**
	 * アーティスト名のThe や 動作不備につながる文字を削除
	 * ゲスト参加も除去
	 * */
	public String ArtistPreFix(String aName ){
		final String TAG = "ArtistPreFix";
		String dbMsg="";
		try{
			dbMsg += nowTime(true,true,true) + aName;/////////////////////////////////////
			aName = String.valueOf(aName);
			aName = aName.toUpperCase();									//大文字化
			if(aName.startsWith("THE")){
				aName = aName.replaceAll("THE ", "");	//aName = aName.substring(3, aName.length());
			}else if(aName.startsWith("the ")){
				aName = aName.replaceAll("the ", "");	//aName = aName.substring(3, aName.length());
			}else if(aName.startsWith("The ")){
				aName = aName.replaceAll("The ", "");	//aName = aName.substring(3, aName.length());
			}
			if(aName.contains("FEAT")){
				dbMsg +=">" + aName;/////////////////////////////////////
				aName = aName.substring(0, aName.indexOf("FEAT")-1);	//aName = aName.substring(3, aName.length());
				dbMsg +=">" + aName;/////////////////////////////////////
//				 myLog(TAG,dbMsg);
			}
			aName = aName.replace( "'", "%");				//	kensakuStr = ORGUT.checkRepChr( kensakuStr, "'", "%");			//誤動作する文字
			aName = aName.replace(  ".", "%");
			aName = aName.replace(  "(", "%");
			aName = aName.replace(  ")", "%");
			dbMsg += ">" + aName;
			if(aName.startsWith("THE")){
				dbMsg += ">The抜けず" ;
				myLog(TAG,dbMsg);
			}

		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return aName;
	}

	public String surfixFN(String fName ){					//ファイル名に使えないものが置き換えられている場合
		final String TAG = "surfixFN[OrgUtil]";
		String dbMsg="開始;";/////////////////////////////////////
		try{
			String wCard = "?";
			dbMsg=nowTime(true,true,true) + dbMsg + fName;/////////////////////////////////////
			if(fName.endsWith("_")){
				fName = fName.substring(0, fName.length()-1)+ wCard;
			}
			if(fName.contains(".")){
				dbMsg +="; . 有り;" + fName;/////////////////////////////////////
				int po  = fName.indexOf(".");
			//	fName = fName.substring(0, po)+ wCard;
				fName = fName.replace(".", " ");
//Stevie Ray Vaughan/The Real Deal  Greatest Hits Vol  2 //// The Real Deal: Greatest Hits Vol. 2
			}
//			if(fName.contains("\\")){
//				fName = fName.replaceAll("\\", wCard);
//			}
			dbMsg +=">" + fName;/////////////////////////////////////
	//		 myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return fName;
	}

	public String mijikaiName(String testName , List<String> testList){					//リストにある短い名前に
		final String TAG = "mijikaiName[OrgUtil]";
		String dbMsg="開始;";/////////////////////////////////////
		try{
			dbMsg=nowTime(true,true,true) + dbMsg + testName;/////////////////////////////////////
			boolean mituketa = false;
			if(testList.size() > 0){
				int fId = 0;
				for(String kesiStr : testList){				//アーティスト単単位名に消込み
					fId++;
					kesiStr = String.valueOf(kesiStr);
					testName = String.valueOf(testName);
					if(testName.contains(kesiStr)){			// 渡された配列に確認する文字列を含むものが既に有れば
						testName = kesiStr;						//その文字列を記録
						dbMsg +="("+ fId + ")" + testName;/////////////////////////////////////
	//					myLog(TAG,dbMsg );
						mituketa = true;
					}
				}
			}
			if(! mituketa){						//見つからなければ
				testName = "";					//空白文字列を返す
			}
	//		dbMsg +=">" + testName;/////////////////////////////////////
	//		 myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return testName;
	}

	public Map<String, String> data2msick(String data ,Context context ){					//音楽ファイルのUrlからアーティスト名、アルバム名、曲順、曲名、拡張子を返す
		final String TAG = "data2msick";
		String dbMsg="開始;";/////////////////////////////////////
		Map<String, String> map = new HashMap<String, String>();
		try{
			String mPass = "";
			String ESD = Environment.getExternalStorageDirectory().getPath();		///storage/sdcard0/ か　/mnt/sdcard/
			dbMsg="ESD="+ ESD;
			String[] sdStrs = ESD.split(File.separator);
			int discVal = (sdStrs.length);
			dbMsg +="(" + discVal + "デレクトリ)";/////////////////////////////////////

			dbMsg= dbMsg+"data=" + data;/////////////////////////////////////
			String rStr = null;
			String[] rStrs = data.split(File.separator);
			int sVal = (rStrs.length-1);
			dbMsg +="(" + sVal + "デレクトリ)";/////////////////////////////////////
			int musciPass = sVal - discVal;
			dbMsg +=",音楽情報>" + musciPass + "デレクトリ,";/////////////////////////////////////

			int endInt = sVal-2;
			int i=0;
			if( sVal <= discVal ){				//
				endInt = discVal-1;
			}
			dbMsg +=",endInt=" + endInt ;
			for(i=0;i<endInt ;i++){
				mPass = mPass+ rStrs[i]+ File.separator ;
			}
			map.put( "mPass", mPass );
			dbMsg +=",mPass=" + map.get( "mPass" ) ;

			for(i = sVal ; i >= endInt ;i--){
				 dbMsg +="[" +i + "/" + sVal +"]";/////////////////////////////////////
				rStr = rStrs[i];
				dbMsg= dbMsg  +rStr;/////////////////////////////////////
				if(mPass.contains(rStr)){
					break;
				}
				if( i == sVal){									//一番下はファイル名
					String[] tStrs = rStr.split("\\.");
					if(tStrs.length >0){
						map.put( "kakucyousi", tStrs[1]);
						dbMsg +=",拡張子⁼" +map.get( "kakucyousi" );/////////////////////////////////////
						rStr = rStr.substring(0, rStr.length()-map.get( "kakucyousi" ).length());
						tStrs = rStr.split(" ");
						String trackNo = tStrs[0];		//アルバム内の曲順
						dbMsg +=",trackNo= " + trackNo;
						if(isNum(trackNo)){		//数字ならtrue
							map.put( "trackNo", trackNo);	//曲順
							map.put( "titolName", rStr.substring(map.get( "trackNo" ).length()));	//曲名
							dbMsg +=">>>[" + map.get( "trackNo" ) + "]";
						}else{
							map.put( "trackNo", null);	//曲順
							map.put( "titolName", rStr);	//曲名
						}
					}else{
						map.put( "trackNo", null);	//曲順
						map.put( "titolName", rStr);	//曲名
					}
					dbMsg +=",タイトル=" + map.get( "titolName" );
				}else if( i == (sVal-1)){
					dbMsg +=",rStr" + rStr;/////////////////////////////////////
					if(! ESD.contains(rStr)){
						map.put( "Alnbum", rStr);
					}
					dbMsg +=",Alnbum=" + map.get( "Alnbum" ) ;
				}else if( i == (sVal-2)){
					if(! ESD.contains(rStr)){
						map.put( "cArtistName", rStr);
					}
					dbMsg +=",Alnbum=" + map.get( "cArtistName" ) ;
				}
			}
			dbMsg +=",Alnbum=" + map.get( "Alnbum" ) ;
			if(map.get( "Alnbum" ) == null ){
				rStr = context.getResources().getString(R.string.comon_nuKnow_album) ;
				dbMsg +=">>" + rStr ;
				map.put( "Alnbum", rStr );		//アルバム情報なし
				dbMsg +=">>" + map.get( "Alnbum" ) ;
			}
			dbMsg +=",cArtistName=" + map.get( "cArtistName" ) ;
			if(map.get( "cArtistName" ) == null ){
				rStr = context.getResources().getString(R.string.comon_nuKnow_artist) ;
				dbMsg +=">>" + rStr ;
				map.put( "cArtistName", rStr);		// アーティスト情報なし</string>
				dbMsg +=">>" + map.get( "cArtistName" ) ;
			}
			if( map.get( "cArtistName" ).equals(context.getResources().getString(R.string.comon_nuKnow_artist)) ||
				 map.get( "Alnbum" ).equals(context.getResources().getString(R.string.comon_nuKnow_album)) ){
		//		myLog(TAG,dbMsg);
			}
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return map;
	}

	public String searchDir2Pass(String path , String searchStr) {			//指定されたパス以下で、指定されたフォルダを探してフルパスで返す
		String retStr = null;
		final String TAG = "searchDir2Pass[OrgUtil]";
		String dbBlock="path="+path;///////////////////////////////////////////
		try{
			File file = new File(path);
			String[] listFiles = file.list();
			dbBlock= dbBlock + "のアイテム数="+listFiles.length+ "で検索するのは"+searchStr;///////////////////////////////////////////
			for (int i = 0 ; i < listFiles.length ; i++){
				String pStr = listFiles[i];
				File pFile = new File(path + File.separator + pStr);
				dbBlock= i + ")" + pStr +",isDirectory="+pFile.isDirectory();///////////////////////////////////////////
				if (pFile.isDirectory()) {		//デレクトリーなら
					if(pStr.contains(".") || pStr.startsWith("SD_")){			//jp.co.mti.android.musicapp.sharpなど
					}else{
						if(pStr.contains(searchStr)){
							retStr = path + File.separator + pStr;
							return retStr;
						}else{
							searchStr = searchStr.toLowerCase(Locale.getDefault());	//すべて小文字	Locale.getAvailableLocales()
							dbBlock= dbBlock + ">>"+searchStr;///////////////////////////////////////////
							if(pStr.contains(searchStr)){
								retStr = path + File.separator + pStr;
								return retStr;
							}else{
								searchStr = searchStr.toUpperCase(Locale.getDefault());			//すべて大文字		Locale.getDefault()
								dbBlock= dbBlock + ">>>"+searchStr;///////////////////////////////////////////
								if(pStr.contains(searchStr)){
									retStr = path + File.separator + pStr;
									return retStr;
								}
							}
						}
					}
				}
			}
	//		myLog(TAG,dbBlock);
		} catch (Exception e) {
			myErrorLog(TAG,dbBlock+"で"+e.toString());
		}
		return retStr;
	}

	public boolean containsInListString(List<String> groups , String tStr){		//渡された文字が既にリストに登録されているいずれかの文字列に含まれていたらtrueを返す
		boolean retBool =false;
		final String TAG = "containsInListString[OrgUtil]";
		String dbBlock= groups.size() + "件目で" + tStr;//////////////////////////////////////////
		try{
			int i=1;
			for(String tName:groups){		//for(i=0;i<pCount;i++){
				if(tStr.contains(tName)){							//tStr.equals(tName) || tName.equals(tStr)
					dbBlock= i +"/" +groups.size()+";" + tStr +"/" +tName ;////////////////////////////////////////////////////////
			//		retBool =true;
					return true;
				}else if(tName.contains(tStr)){
					dbBlock= i +"/" +groups.size()+";" + tStr +"/" +tName ;////////////////////////////////////////////////////////
//					int kesuIndex = groups.indexOf(tName);
//					dbBlock= dbBlock +"で" +kesuIndex+"番目を消去" ;////////////////////////////////////////////////////////
					boolean seikou = groups.remove(tName);
					dbBlock= dbBlock +">>" +seikou ;////////////////////////////////////////////////////////
	//				myLog(TAG,dbBlock);
					return true;
				}
			}
//			myLog(TAG,dbBlock);
//			dbBlock="tStr=" + tStr +"が"+ groups.get(0) + "～" + groups.get(groups.size()-1) + "に有るか；件数="+ groups.size() ;//////////////////////////////////////////
		} catch (Exception e) {
			myErrorLog(TAG, dbBlock+e.toString());
		}
		return retBool;
	}

	/**
	 *　アルバムアートのURLを返す
	 * android - ALBUM_ART列はAPI 29などから非推奨
	 * **/
	@SuppressLint("Range")
	public String retAlbumArtUri(Context context , String artistMei , String albumMei) throws IOException {			//アルバムアートUriだけを返す		ContextWrapper context ,
		String retStr = null;
		final String TAG = "retAlbumArtUri";
		String dbMsg= "" ;
		try{
			dbMsg += "artistMei=" + artistMei + ",albumMei=" +albumMei ;/////////////////////////////////////
			Uri cUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;//1.uri  The URI, using the content:// scheme, for the content to retrieve
			String[] c_columns = null;		 		//③引数columnsには、検索結果に含める列名を指定します。nullを指定すると全列の値が含まれます。
			String c_selection =  MediaStore.Audio.Albums.ARTIST +" LIKE ?  AND " + MediaStore.Audio.Albums.ALBUM +" = ?";
			String[] c_selectionArgs= { "%" + artistMei + "%" , albumMei };   			//⑥引数groupByには、groupBy句を指定します。
			String c_orderBy= null;											//MediaStore.Audio.Albums.LAST_YEAR  ; 			//⑧引数orderByには、orderBy句を指定します。	降順はDESC
			Cursor cursor = context.getContentResolver().query( cUri , c_columns , c_selection , c_selectionArgs, c_orderBy);			//getApplicationContext()
			dbMsg += "," +cursor.getCount() + "件";
			if( cursor.moveToFirst() ){
				int colCount = cursor.getColumnCount();
				do{
					int targetIndex = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
					dbMsg += "[" +targetIndex + "/" + colCount + "]";
					retStr = cursor.getString(targetIndex);
					if(retStr != null){
						break;
					}
					targetIndex = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID);
					dbMsg += "[" +targetIndex + "/" + colCount + "]";
					int alibumId = cursor.getInt(targetIndex);
					Uri albumUri = getAlbumArtUri(alibumId);
					dbMsg += ",albumUri=" +albumUri;
					retStr = albumUri.toString();
//					for(int i = 0; i < colCount; ++i){
//						dbMsg += "[" +i + "/" + colCount + "]";
//						dbMsg += cursor.getColumnName(i);
//						int colType = cursor.getType(i);
//						switch (colType){
//							case Cursor.FIELD_TYPE_NULL:          //0
//								dbMsg += "null" ;
//								break;
//							case Cursor.FIELD_TYPE_INTEGER:         //1
//								dbMsg += "=" + cursor.getInt(i);
//								break;
//							case Cursor.FIELD_TYPE_FLOAT:         //2
//								dbMsg += "=" + cursor.getFloat(i);
//								break;
//							case Cursor.FIELD_TYPE_STRING:          //3
//								dbMsg += "=" + cursor.getString(i);
//								break;
//							case Cursor.FIELD_TYPE_BLOB:         //4
//								//@SuppressLint("Range") String cBlob = String.valueOf(cursor.getBlob(cPosition));
//								dbMsg +=  "【Blob】";
//								break;
//							default:
//								break;
//						}
//
//					}

				}while(cursor.moveToNext());
			}
			dbMsg += ",album_art=" +retStr;/////////////////////////////////////
			cursor.close();
			myLog(TAG,dbMsg);
		}catch(IllegalArgumentException e){
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return retStr;
	}

	private Uri getAlbumArtUri(long albumId) {
		final String TAG = "getAlbumArtUri";
		String dbMsg= "" ;
		Uri retUri = null;
		try{
			dbMsg += ",albumId=" + albumId ;
			Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
			retUri=ContentUris.withAppendedId(albumArtUri, albumId);
			dbMsg += ",retUri=" + retUri ;
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return retUri;
	}


	public ImageView IV;
	public Bitmap mDummyAlbumArt = null;

	public void seNoImg( int dHighet , int dWith ,Context context ){		//イメージが無い場合の初期設定
		final String TAG = "seNoImg[OrgUtil]";		//<<"jakeSya[MaraSonActivity]
		String dbMsg = "開始" ;//////////////////////////////////////////
		try{
			dbMsg= "[" + dHighet  ;//////////////////////////////////////////
			dbMsg= "×" + dWith +"]"  ;//////////////////////////////////////////
			mDummyAlbumArt = BitmapFactory.decodeResource( context.getResources() , R.drawable.no_image);
			dbMsg = ",ビットマップ=" + mDummyAlbumArt ;
				//imageView.setImageBitmap(mDummyAlbumArt);
		} catch (Exception e) {
			myErrorLog(TAG, dbMsg +"で"+e.toString());
		}
	}

	public String setAlbumArt(String album_art , ImageView imageView , int dHighet , int dWith ,Context context , String sucssesPass){		//指定したイメージビューに指定したURiのファイルを表示させる
		final String TAG = "setAlbumArt";		//<<"jakeSya[MaraSonActivity]
		String dbMsg = "開始" ;//////////////////////////////////////////
		try{
			dbMsg= "album_art=" + album_art  ;//////////////////////////////////////////
			dHighet = imageView.getWidth();
			dbMsg += "(" + dHighet ;
			dWith = imageView.getWidth();
			dbMsg += "×" + dWith ;
			seNoImg( dHighet , dWith ,context );		//イメージが無い場合の初期設定
			if( album_art != null ){
				File file = new File(album_art);
				String fName = file.getName();
//			}else{
//				seNoImg( dHighet , dWith ,context );		//イメージが無い場合の初期設定
			}
			sucssesPass = setReSizeArt(album_art , imageView , dHighet , dWith ,context , sucssesPass);	//指定したイメージビューに指定したURiのファイルを表示させる
			dbMsg += ",指定サイズで登録できた画像のUri=" + sucssesPass ;
//			if( sucssesPass == null ){
//				seNoImg( dHighet , dWith ,context );		//イメージが無い場合の初期設定
//			}
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG, dbMsg +"で"+e.toString());
		}
		return sucssesPass;
	}

	public Bitmap retBitMap(String album_art  , int dHighet , int dWith ,Resources res ){		//指定したURiのBitmapを返す
		final String TAG = "retBitMap[OrgUtil]";
		String dbMsg = "開始" ;//////////////////////////////////////////
		Bitmap retBM = null;
		float scale = 1;
		try{
			dbMsg = "album_art=" + album_art ;////		http://dorodoro.info/tip/bitmap%E3%81%AB%E8%AA%AD%E3%81%BF%E8%BE%BC%E3%82%80%E6%99%82%E3%81%AEoptions%E3%81%AE%E8%A8%AD%E5%AE%9A/
			dbMsg +=",引数[表示枠；"+ dWith+ "×"+ dHighet+ "]";///////////////////////////////////////////////////////////////////////////////////////////
			BitmapFactory.Options options = new BitmapFactory.Options();			// デコード時のオプション
			options.inJustDecodeBounds = true;				// 画像のサイズだけを取得するようにする
			if( album_art == null ){
				BitmapFactory.decodeResource( res, R.drawable.no_image , options);
			} else {
				File file = new File(album_art);
				dbMsg += ",exists=" +  file.exists() ;
				if(  ! file.exists()){
					BitmapFactory.decodeResource( res, R.drawable.no_image , options);
					album_art = null;
					dbMsg = ">>" + album_art ;//////////////////////////////////////////
				}else {
					BitmapFactory.decodeFile(album_art, options);
				}
			}
			int _oDpi = options.inDensity;
			dbMsg +=",_oDpi="+ _oDpi;//////////////////////////////
			int orgH = options.outHeight;
			int orgW = options.outWidth;
			float hHiritu = 1;
			float wHiritu = 1;
			if( dHighet !=0 && dWith !=0 ){
				dbMsg +=",options.out[ファイルのサイズ；"+ orgW+ "×"+ orgH + "]";///////////////////////////////////////////////////////////////////////////////////////////
				if( orgW < dWith ){				//	dWith < orgW 			orgW < dWith
					wHiritu = orgW / dWith;
				} else {
					wHiritu = dWith/orgW;
				}
				dbMsg +="→[幅"+ wHiritu;///////////////////////////////////////////////////////////////////////////////////////////
				if( orgH < dHighet ){						// dHighet < orgH 		 orgH < dHighet
					hHiritu = orgH / dHighet;
				} else {
					hHiritu = dHighet/orgH;
				}
				dbMsg +="×高さ"+ hHiritu + "]";///////////////////////////////////////////////////////////////////////////////////////////
				float max = Math.max(hHiritu, wHiritu);
				scale = (float) Math.floor(max);

//				if (hHiritu < wHiritu ){
//					scale = (int) wHiritu ;		// 縮小する比率
//				} else {
//					scale = (int)  hHiritu ;
//				}
				dbMsg +="⇒"+ scale ;///////////////////////////////////////////////////////////////////////////////////////////
			}
			dbMsg +=",scale="+ scale;//////////////////////////////
			if(1 < scale ){												//縮小する場合だけ
				options.inSampleSize = (int)scale;				//分母を指定して画像の縮小をしてくれます。
			}else if(scale <= 0){
				options.inSampleSize = 1;
				dbMsg +=",low Size";//////////////////////////////
//				myErrorLog(TAG,dbMsg);
			}
			options.inJustDecodeBounds = false;			// 画像の中身もデコードできるようにする
			options.inPurgeable=true;				//再利用の可否で、trueにすると、再利用はないとの判断で割とメモリをキレイにしてくれる
			if( album_art == null ){
				retBM = BitmapFactory.decodeResource( res, R.drawable.no_image , options);
			} else{
				retBM = BitmapFactory.decodeFile(album_art,options);
			}
			dbMsg +=",retBM="+ retBM;///////////////////////////////////////////////////////////////////////////////////////////
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG, dbMsg +"で"+e.toString());
		}
		return retBM;
	}

	public String jaName;
	public int ih;
	public int iw;
	public Context iC;
	public String setReSizeArt(String album_art , ImageView imageView , int dHighet , int dWith ,Context context , String sucssesPass){		//指定したイメージビューに指定したURiのファイルを表示させる
		final String TAG = "setReSizeArt[OrgUtil]";
		String dbMsg = "開始" ;//////////////////////////////////////////
		try{
			dbMsg = "album_art=" + album_art ;//////////////////////////////////////////
			IV = imageView;
			jaName = album_art;
			ih = dHighet;
			iw = dWith ;
			iC = context;
			mDummyAlbumArt =  retBitMap( album_art  , dHighet , dWith , context.getResources() );		//指定したURiのBitmapを返す
			dbMsg +=",mDummyAlbumArt=" + mDummyAlbumArt ;//////////////////////////////////////////
	//		myLog(TAG,dbMsg);
			new Thread(new Runnable() {				//ワーカースレッドの生成
			public void run() {
				String dbMsg= "thread id = " + Thread.currentThread().getId();/////////////////////////////////////
				try {
					Thread.sleep(1); // 1ms秒待つ
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				runOnUiThread(new Runnable() {			//メインスレッド以外からのUI更新
					/*☆メインスレッドにポストする-
					 *メインスレッドから呼ばれた場合、渡されたアクションをすぐに実行
					 *メインスレッド以外から呼ばれた場合、渡されたアクションをメインスレッドのイベントキューにポスト*/
						public void run() {
							final String TAG = "runOnUiThread[setReSizeArt.setReSizeArt.OrgUtil]";
							String dbMsg= "開始";/////////////////////////////////////
							try{
								dbMsg= "thread id = " + Thread.currentThread().getId();/////////////////////////////////////
								dbMsg=  ",IV=" + OrgUtil.this.IV + ",bMap=" + mDummyAlbumArt ;//////////////////////////////////////////
			//					myLog(TAG,dbMsg);
								if(mDummyAlbumArt != null ){
									OrgUtil.this.IV.setImageBitmap(mDummyAlbumArt);
								}
		//						myLog(TAG,dbMsg);
							} catch (Exception e) {
								myErrorLog(TAG,dbMsg+"で"+e);
							}
						}
					});
				}
			}).start();
		}catch(Exception e) {	//ストリームが開けなかったときの処理
			myErrorLog(TAG, dbMsg +"で"+e.toString());
			sucssesPass = null;
			if( jaName != null){
				OrgUtil.this.jaName = null;
				setReSizeArt(OrgUtil.this.jaName ,  OrgUtil.this.IV , OrgUtil.this.ih , OrgUtil.this.iw ,OrgUtil.this.iC , null);
			}
//			Drawable img = Drawable.createFromPath(album_art);
//			imageView.setImageDrawable(img);							//http://stackoverflow.com/questions/11031250/display-album-art-from-mediastore-audio-albums-album-art
		}
		return sucssesPass;
	}

///service////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 起動しているサービスを取得
	 * {@link <a href="http://seesaawiki.jp/w/moonlight_aska/d/%B5%AF%C6%B0%C3%E6%A4%CE%A5%B5%A1%BC%A5%D3%A5%B9%B0%EC%CD%F7%A4%F2%BC%E8%C6%C0%A4%B9%A4%EB">...</a>}
	 * */
	public ArrayList<String> getMyService(Activity fromAct , String myPackageName) {				//起動しているサービスを取得
		final String TAG = "getNowServicet[OrgUtil]";
		String dbMsg = "開始";
		ArrayList<String> serviceList = new ArrayList<String>();
		try{
			ActivityManager activityManager = (ActivityManager)fromAct.getSystemService(ACTIVITY_SERVICE);
			List<RunningServiceInfo> runningService = activityManager.getRunningServices(100);		// 起動中のサービス情報を取得
			dbMsg = "RunningServiceInfo" + runningService.size() + "件";
			dbMsg += "(this;" + myPackageName + ")";
			if(runningService != null) {
				for(RunningServiceInfo srvInfo : runningService) {
			//		dbMsg += "(" + srvInfo.service.getPackageName();
					String serviceClassName = srvInfo.service.getClassName();
					dbMsg += ")service;" + serviceClassName;
					if(serviceClassName.startsWith(myPackageName)){
						dbMsg += ")Shor;" + srvInfo.service.getShortClassName()  + "\n";
						serviceList.add(serviceClassName);
					}
				}
			}
		//	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, serviceList);		// リスト表示
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"；"+e);
		}
		return serviceList;
	}
	///////////////////////////////////////////////////////////////////////////////////
	public static void myLog(String TAG , String dbMsg) {
		Util UTIL = new Util();
		Util.myLog(TAG , "[OrgUtil]" + dbMsg);
	}

	public static void myErrorLog(String TAG , String dbMsg) {
		Util UTIL = new Util();
		Util.myErrorLog(TAG , "[OrgUtil]" + dbMsg);
	}
}
