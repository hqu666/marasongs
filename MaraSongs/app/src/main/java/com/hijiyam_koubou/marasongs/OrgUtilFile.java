package com.hijiyam_koubou.marasongs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;

public class OrgUtilFile extends Activity {
//	public String dbBlock="";
//	public plogTask pTask;				//プログレス表示
	public MuList MUL;					//リストアクティビティ
	public String kuigiriStr=",";		//"."は使えない


	@Override
	public void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
		final String TAG = "onCreate";
		String dbBlock="";
		try{
			MuList MUL = new MuList();						//リストアクティビティ
		} catch (Exception e) {
			myErrorLog(TAG,"OrgUtilFile[]"+dbBlock+e.toString());
		}
	}

	public static String fn2peareatDirName(String chekFN){		//引数で渡されたパス名の親デレクトリを返す
		String retFN="";
		try{
			String[] passName = chekFN.split(File.separator);		//システムのパスセパレータでパス名を分離
			//	Log.d("fn2peareatDirName","1;chekFN ="+chekFN+"を"+passName.length+"分割");
				for(int i=0;i<passName.length-1;i++)
				{
					retFN=retFN+passName[i]+File.separator;
			//		Log.d("fn2peareatDirName","1["+i+"]は"+passName[i]+"で"+retFN);
				}
				if(retFN.length()>1){
					retFN=retFN.substring(0, retFN.length()-1);		//最後のパスセパレータを除去
				}
				return retFN;
		} catch (Exception e) {
			myErrorLog("fn2peareatDirName","エラー発生；"+e.toString());
			return retFN;
		}
	}

	public String kakucyousiJyogai(String filename) {		//拡張子を除外する
		final String TAG = "[OrgUtilFile]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			int lastDotPos = filename.lastIndexOf('.');
			if (lastDotPos == -1) {
				return filename;
			} else if (lastDotPos == 0) {
				return filename;
			} else {
				return filename.substring(0, lastDotPos);
			}
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return filename;
	 }
	
	public static String fn2lastNameNFO(String fName){	//ファイルオブジェクトと確定できない名称の最終カラムを返す
		String lFn="";
		try{
			String[] passName = fName.split(File.separator);		//システムのパスセパレータでパス名を分離
	//		Log.d("fn2lastNameNFO","1;chekFN ="+fName+"を"+passName.length+"分割");
			return passName[passName.length-1];
		} catch (Exception e) {
			myErrorLog("fn2lastNameNFO","エラー発生；"+e.toString()+";"+fName);
		}
		return lFn;
	}

	public String[] kakucyousi = null;	//抽出対象ファイルの拡張子
	public String muExtPrefR(Context context){					//拡張子で指定されたファイル種類の読み取り
		String retStr = "";
		String extStr="";
		final String TAG = "muExtPrefR";
		String dbBlock = "";
		try{
	//		Context context=this.getParent().getApplicationContext();
			dbBlock="context="+context.getPackageName();/////////////////////////////////////////////////////////////////
			Log.d(TAG,dbBlock);
			String hikakuStr="pref_taisyou_type_";

			SharedPreferences sharedPref=PreferenceManager.getDefaultSharedPreferences(context);
			dbBlock="sharedPref;"+sharedPref.getAll().size()+"項目";/////////////////////////////////////////////////////////////////
	//		Log.d(TAG,dbBlock);
			if(sharedPref.getAll().size() == 0){
				extStr="mp3"+kuigiriStr+"m4a"+kuigiriStr+"wma"+kuigiriStr+"wav";
			}else{
				Map<String,?> map=sharedPref.getAll();
				dbBlock=map.size()+"項目";
				for(Entry<String, ?> entry : map.entrySet()){
					String key=entry.getKey();
					if(key.startsWith(hikakuStr)){
						String value = entry.getValue().toString();
						if(Boolean.valueOf(value)){
							extStr=extStr+key.substring(hikakuStr.length())+kuigiriStr;
							dbBlock=extStr;/////////////////////////////////////////////////////////////////
						}
					}
				}
				extStr=extStr.substring(0, (extStr.length()-kuigiriStr.length()));
			}

		} catch (Exception e) {
			myErrorLog(TAG,dbBlock+e.toString());
//			return retStr;
		}
		Log.d(TAG,extStr);
		return extStr;
	}

	public boolean isMusic(String exStr){		//拡張子からAndoroidで再生できる音楽ファイルならtrueを返す
		boolean retB=false;
		final String TAG = "isMusic";
		String dbBlock = "kakucyousi="+kakucyousi.length;
		try{
			for(String extStr : kakucyousi){
				exStr=exStr.toLowerCase();
				if(exStr .endsWith(extStr)){	//リングトーンフォーマットは RTTTL/RTX、 OTA、および iMelody
					return true;
				}
			}
		} catch (Exception e) {
			myErrorLog(TAG,dbBlock+e.toString());
		}
		return retB;
	}

	public boolean isMove(String exStr){		//拡張子からAndoroidで再生できる動画ファイルならtrueを返す
		boolean retB=false;
		exStr=exStr.toLowerCase();
		if(exStr .equals(".mp4")
				|| exStr .equals(".3gp")) {		//3GPP (.3gp) と MPEG-4 (.mp4). MPEG-TS (.ts, AAC オーディオのみ, シーク不可, Android 3.0+)
			return true;
//			|| exStr .equals(".3gp")) {		//3GPP (.3gp) と MPEG-4 (.mp4). MPEG-TS (.ts, AAC オーディオのみ, シーク不可, Android 3.0+)
		}
		return retB;
	}

	public boolean isImg(String exStr){		//拡張子からAndoroidで表示できる静止画ファイルならtrueを返す
		boolean retB=false;
		exStr=exStr.toLowerCase();
		if(exStr .equals(".jpg") || exStr .equals(".gif")|| exStr .equals(".png")|| exStr .equals(".bmp")) {
			return true;
		}
		return retB;
	}

	public String findMuAll(String dirName ,Context context){			//指定された階層以降で指定された種類のファイルをリスト化する
		String retStr="";
		final String TAG = "findMuAll";
		String dbBlock="dirName="+dirName;
		int i;
		try{
			if(kakucyousi==null){
				dbBlock="kakucyousi="+kakucyousi;
				String kakucyousiStr= muExtPrefR(context);					//拡張子で指定されたファイル種類の読み取り
				dbBlock="kakucyousiStr="+kakucyousiStr+"で"+kakucyousiStr.length();
				Log.d(TAG,dbBlock);
				if(kakucyousiStr.length() >0){
					kakucyousi= kakucyousiStr.split(kuigiriStr);
				}
			}
			dbBlock="kakucyousi="+kakucyousi[0]+"～"+kakucyousi[(kakucyousi.length-1)];
			Log.d(TAG,dbBlock);
			File findDir = new File(dirName);
			dbBlock=dbBlock+","+findDir.listFiles().length+"\n";
			kekkaCount=0;
			kekkaStr=new String[10000];
			String pdTitol="音楽ファイルの検索";
			String pdMessage=dirName;
			int pdMaxVal=0;
			dbBlock="pdTitol="+pdTitol+",pdMessage="+pdMessage;

			searchDir(dirName);
			String[]kekka=kekkaDirs.split(kuigiriStr);
			for(String k : kekka){
				Log.d(TAG,k);
			}
//			pDialog.dismiss();
			findMuAll_a();			//リスト配列の処理
		} catch (Exception e) {
			myErrorLog(TAG,dbBlock+e.toString());
		}
		return retStr;
	}

	public void findMuAll_a(){			//リスト配列の処理
		final String TAG = "findMuAll_a";
		String dbBlock = "";
		try{
			String muArria="";
			String muArrias="";
			int i = 1;
			for (String fName : kekkaStr) {
				if(fName != null){
					dbBlock=fName.startsWith(muArria)+"("+i+")"+fName+" / "+muArria;///////////////////////////////////////////
					if(fName.startsWith(muArria) || fName==null){
					}else{
						Log.d("findMuAll_a",dbBlock);
						muArria=setOyaDir(fName , 4);		//渡されたフルパスから指定されたパスを差し引いたパス名を返す
						muArrias=muArrias+i+";"+muArria+"\n";
					}
					i++;
				} else {
					break;
				}
			}
			myLog("findMuAll_a",muArrias+"\n"+i+"ファイル");
		} catch (Exception e) {
			myErrorLog("findMuAll_a",dbBlock+e.toString());
		}
	}

	public String setOyaDir(String fFname , int omitPCount){		//渡されたフルパスから指定されたパスを差し引いたパス名を返す
		String[] muA=null;
		String muArria = "";
		String dbBlock = "";
		try{
			muA=fFname.split(File.separator);
			dbBlock=muA.length+"カラム"+muA[0]+"～"+muA[muA.length];///////////////////////////////////////////
			Log.d("setOyaDir",dbBlock);
			int lEnd=muA.length-omitPCount;
			for (int j = 1 ; j < lEnd ; j++) {
				muArria=muArria+File.separator+muA[j];
	//			dbBlock=muArria;///////////////////////////////////////////
			}
			myLog("setOyaDir",muArria);
		} catch (Exception e) {
			myErrorLog("setOyaDir",dbBlock+e.toString());
		}
		return muArria;
	}

	public static String[] kekkaStr=null;		//音楽ファイルの配列
	public static int kekkaCount=0;				//そのカウント
	public String readBeforDir="";
	public String kekkaDirs="";		//音楽デレクトリの配列
	public static int dirCount=0;				//そのカウント

	boolean searchDir(String path) {
		final String TAG = "searchDir";
		String dbBlock="path="+path;///////////////////////////////////////////
		try{
			File file = new File(path);
			File[] listFiles = file.listFiles(new FilenameFilter() {
			//	@Override
				public boolean accept(File dir, String name) {
					if (name.startsWith(".")) {		// ドットで始まるファイルは対象外
						return false;
					}
					if (name.endsWith(".class")) {// クラスファイルは対象外
						return false;
					}
					String absolutePath = dir.getAbsolutePath()+ File.separator + name;		// 対象要素の絶対パスを取得
					if (new File(absolutePath).isFile()) {
						return true;
					}else {
						return searchDir(absolutePath);	// ディレクトリの場合、再び同一メソッドを呼出す。
					}
				}
			});
			for (File f : listFiles) {
				if (f.isFile()) {
					String ffName=f.getAbsolutePath();
					dbBlock="("+kekkaCount+")"+ffName;///////////////////////////////////////////
					if(isMusic(ffName)){		//拡張子からAndoroidで再生できる音楽ファイルならtrueを返す)
						String[] pDirs=ffName.split(File.separator);
						String readNowFN=pDirs[(pDirs.length-3)]+File.separator+pDirs[(pDirs.length-2)]+File.separator+pDirs[pDirs.length-1];
						dbBlock=ffName+">>"+readNowFN;///////////////////////////////////////////
						kekkaStr[kekkaCount]=readNowFN;			//ファイル名から拡張子を抜き出し
						dbBlock=kekkaCount+")"+ffName+"を\n  "+kekkaStr[kekkaCount];///////////////////////////////////////////
						kekkaCount++;
						String readNowDir=ffName.substring(0, (ffName.length()-readNowFN.length()));
						dbBlock=dbBlock+"と"+readNowDir+"\n  "+dirCount+"))"+readBeforDir;///////////////////////////////////////////
		//				Log.d(TAG,dbBlock);
						if(readNowDir.equals(readBeforDir)){
						}else{
							readBeforDir=readNowDir;
							kekkaDirs=kekkaDirs+readNowDir+kuigiriStr;
							dbBlock=kekkaDirs;///////////////////////////////////////////ffName+">>"+kekkaDirs[dirCount]+" / "+readNowFN+"("+readBeforDir+")"
							dirCount++;
						}
					}
				}
			}
		//	Log.d(TAG,dbBlock);
		} catch (Exception e) {
			myErrorLog(TAG,dbBlock+"で"+e.toString());
		}
		return true;
	}

	public static FilenameFilter getFileExtensionFilter(String extension) {		//指定した拡張子のファイルを返す
		final String _extension = extension;
		return new FilenameFilter() {
			public boolean accept(File file, String name) {
				boolean ret = name.endsWith(_extension);
				return ret;
			}
		};
	}

	public static FilenameFilter getFileRegexFilter(String regex) {		//"[0-9]{8}\\.html"の様な正規表現で検索
		final String regex_ = regex;
		return new FilenameFilter() {
			public boolean accept(File file, String name) {
				boolean ret = name.matches(regex_);
				return ret;
			}
		};
	}

	 public static FileFilter getNotDirFileFilter() {
		return new FileFilter() {
			public boolean accept(File file) {
				return file.isFile();
			}
		};
	}

	public boolean acceesDekiru(String exStr){		//アクセスできなかった実例文字ならfalseを返す
		if(exStr.equals("/")){
			return false;
		}else if(exStr.equals("/calllog/")){
			return false;
		}else if(exStr.equals("/ldb/")){
			return false;
		}else if(exStr.equals("/battlog/")){
			return false;
		}else if(exStr.equals("/persist/")){
			return false;
		}else if(exStr.equals("/data/")){
			return false;
		}else if(exStr.equals("/root/")){
			return false;
		}else if(exStr.equals("/sbin/")){
			return false;
		}else if(exStr.equals("/config/")){
			return false;
		}else if(exStr.equals("/cache/")){
			return false;
		}else if(exStr.equals("/mnt/secure/")){
			return false;
		}else if(exStr.equals("/mnt/sdcard/secure/")){
			return false;
		}else return !exStr.equals("/mnt/sdcard/.android_secure/");
	}

	protected String sizeFormat(double chedItem) {		//サイズ取得と、小数第三位で四捨五入して単位変換
		double retNum;
		String retStr = "";

		try{
	  if ( chedItem >= (1024*1024*1024)) {
	  	retNum =chedItem / (1024*1024*1024);
	BigDecimal bi = new BigDecimal(String.valueOf(retNum));
	retNum = bi.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
	retStr= retNum + " GB";
	  } else if ( (1024*1024*1024) >chedItem && chedItem> (1024*1024)) {	//&amp;&ampでは通らないので&&に置換え
	  	retNum = chedItem /  (1024*1024);
	BigDecimal bi = new BigDecimal(String.valueOf(retNum));
	retNum = bi.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
	retStr= retNum + " MB";
	  } else if (( (1024*1024) > chedItem )&&( chedItem >= 1024)) {
	  	retNum = chedItem / 1024;
	BigDecimal bi = new BigDecimal(String.valueOf(retNum));
	retNum = bi.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
	retStr= retNum + " KB";
	  } else if ( 1024 > chedItem ) {
	  	retStr= (long) chedItem + " B";
	  }
		} catch (Exception e) {
			myErrorLog("sizeFormat","エラー発生；"+e);
		}
		return retStr;
	}

	public Bitmap reSizePict(String orgURL,int targrtW,int targetH){			//画像ファイルのURLと表示枠のサイズを渡して収まるサイズのビットマップ情報を得る
		Bitmap image =null;
		final String TAG = "reSizePict[OrgUtilFile]";
		String dbBlock = "";
		try{
			dbBlock =orgURL+ "を"+ targrtW+"×"+ targetH+"に";///////////////////////////////////
	//		myLog(TAG,dbBlock);
			BitmapFactory.Options options = new BitmapFactory.Options();		//読み込み用のオプションオブジェクトを生成
			options.inJustDecodeBounds = true;								//この値をtrueにすると実際には画像を読み込まず、画像のサイズ情報だけを取得することができます。
			dbBlock =dbBlock+"＞"+ options.inTempStorage+"[バイト]";///////////////////////////////////
			BitmapFactory.decodeFile(orgURL, options);		//画像ファイル読み込みここでは上記のオプションがtrueのため実際の画像は読み込まれないです。
			int scaleW = options.outWidth / targrtW + 1;				//読み込んだサイズはoptions.outWidthとoptions.outHeightに格納されるので、その値から読み込む際の縮尺を計算します。
			int scaleH = options.outHeight / targetH + 1;
			dbBlock =dbBlock+"＞"+ "元サイズ="+ options.outWidth+"×"+ options.outHeight+"を幅なら"+ scaleW+"、高さなら"+ scaleH+"に";///////////////////////////////////
			int scale = Math.max(scaleW, scaleH);							//縮尺は整数値で、2なら画像の縦横のピクセル数を1/2にしたサイズ。//3なら1/3にしたサイズで読み込まれます。
			options.inJustDecodeBounds = false;								//今度は画像を読み込みたいのでfalseを指定
			options.inSampleSize = scale;									//先程計算した縮尺値を指定
			image = BitmapFactory.decodeFile(orgURL, options);							//これで指定した縮尺で画像を読み込めます。もちろん容量も小さくなるので扱いやすいです。
	//		myLog(TAG,dbBlock);
		} catch (Exception e) {
			myErrorLog(TAG,dbBlock+"+；"+e);
			return null;
		}
		return image;

	}//http://lablog.lanche.jp/archives/192

	public String GetFileMei(String fullName){			//フルパス名からファイル名の抜き出し
		String retStr="";
  String dbBlock = "fullName="+fullName;///////////////////////////////////
		try{
			if(fullName.contains(File.separator)){
				String[] ubFns = fullName.split(File.separator);		//\だけでエスケープできないので\\	"\\/"
				retStr = ubFns[ubFns.length-1];		//+"/*"
		  dbBlock ="retStr"+retStr+";"+dbBlock;///////////////////////////////////
	//			Log.d("GetFileMei",dbBlock);
			}else{
				return fullName;
			}
		} catch (Exception e) {
			myErrorLog("GetFileMei",dbBlock+"；"+e);
			return null;
		}
		return retStr;
	}

	public static String GetKakucousi(String fName){			//ファイル名から拡張子を抜き出し
		String retStr="";
		String dbBlock ="fName="+fName;///////////////////////////////////
		try{
			if(fName.contains("\\.")){
				String[] ubFns = fName.split("\\.");
		  dbBlock ="length="+ubFns.length+";"+dbBlock;///////////////////////////////////
				retStr = ubFns[ubFns.length];		//+"/*"
		  dbBlock ="retStr"+retStr+";"+dbBlock;///////////////////////////////////
	//			Log.d("GetKakucousi",dbBlock);
			}else{
				return fName;
			}
		} catch (Exception e) {
			myErrorLog("GetKakucousi",dbBlock+"；"+e);
			return null;
		}
		return retStr;
	}

	public String GetMIMEType(String Path){	//ファイル名からMIMEタイプ取得
		String ext="";
		String MIMEStr="";
		String fname="";
		String dbBlock = "";
		try{
	  dbBlock ="1;Path="+Path;///////////////////////////////////
	//		Log.d("GetMIMEType",dbBlock);
			ext=MimeTypeMap.getFileExtensionFromUrl(Path);				 //拡張子を取得
	  dbBlock ="2;ext"+ext+";"+dbBlock;///////////////////////////////////
	//		Log.d("GetMIMEType",dbBlock);
			if(ext==null || ext.equals("")){
				fname=GetFileMei(Path);			//フルパス名からファイル名の抜き出し
				ext=GetKakucousi(fname);			//ファイル名から拡張子を抜き出し
			}
			ext=ext.toLowerCase();												 //小文字に変換
	  dbBlock ="ext="+ext+",fname="+fname+","+dbBlock;///////////////////////////////////
		//	Log.d("GetMIMEType",dbBlock);
			MIMEStr= MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);	 //MIME Typeを返す
	  dbBlock ="ext="+ext+",MIME="+MIMEStr;///////////////////////////////////
		//	Log.d("GetMIMEType",dbBlock);
			if(MIMEStr == null || MIMEStr.equals("")){
				if(ext.contains("jpg") || ext.contains("jpeg")){		//画像
					MIMEStr="image/jpeg";
				}else if(ext.contains("gif")){				//gif 画像
					MIMEStr="image/gif";
				}else if(ext.contains("png")){				//png 画像
					MIMEStr="image/png";
				}else if(ext.contains("pdf")){					//pdf ファイル
					MIMEStr="application/pdf";
				}else if(ext.contains("swf")){					//フラッシュファイル
					MIMEStr="application/x-shockwave-flash";
				}else if(ext.contains("rm")){					//RealPlay ビデオファイル
					MIMEStr="application/vnd.rn-realmedia";
				}else if(ext.contains("mpg")){					//mpg ビデオファイル
					MIMEStr="video/mpg";
				}else if(ext.contains("mov")){					//		mov ビデオファイル
					MIMEStr="video/quicktime";
				}else if(ext.contains("avi")){					//avi ビデオファイル
					MIMEStr="video/avi";
				}else if(ext.contains("wmv")){					//Windows ビデオファイル
					MIMEStr="video/x-ms-wmv";
				}else if(ext.contains("mp3")){					//		MP3 音声ファイル
					MIMEStr="audio/mp3";
				}else if(ext.contains("wav")){					//	 wave	wave 音声ファイル
					MIMEStr="audio/wav";
				}else if(ext.contains("ra")){					//	 ram	RealPlay 音声ファイル
					MIMEStr="audio/vnd.rn-realaudio";
				}else if(ext.contains("mid")){					//	 midi	midi オーディオファイル
					MIMEStr="audio/midi";
				}else if(ext.contains("txt") || ext.contains("text") || ext.contains("dat") || ext.contains("csv")){																		//プレーンテキスト
					MIMEStr="text/plain";
				}else if(ext.contains("jis") || ext.contains("utf8") || ext.contains("euc") || ext.contains("sjis") || ext.contains("sjs") || ext.contains("asc") || ext.contains("utf")){	//プレーンテキスト
					MIMEStr="text/plain";
				}else if(ext.contains("htm")){					//	html 	html文書
					MIMEStr="text/html";
				}else if(ext.contains("svg")){					//	svg(Scalable Vector Graphics)
					MIMEStr="image/svg-xml";
				}else if(ext.contains("zip")){					//		zip 圧縮ファイル
					MIMEStr="application/x-zip-compressed";
				}else if(ext.contains("lzh")){					//lzh 圧縮ファイル
					MIMEStr="application/x-lzh-compressed";
				}else if(ext.contains("gz")){					//g-zip 圧縮ファイル
					MIMEStr="application/x-gzip";
				}else if(ext.contains("bz2")){					//bz2 圧縮ファイル
					MIMEStr="application/x-bz2-compressed";
				}else if(ext.contains("tgz") || ext.equals("z")){			//compress 圧縮ファイル
					MIMEStr="application/x-compress";
				}else if(ext.contains("cab")){					//cab 圧縮ファイル
					MIMEStr="application/x-cab-compressed";
				}else if(ext.contains("sit") || ext.endsWith("stuff") || ext.endsWith("z")){					//圧縮ファイル
					MIMEStr="application/x-stuffit";
				}else if(ext.contains("tar")){					//tar 書庫ファイル
					MIMEStr="application/x-tar";
				}else if(ext.contains("exe")){					//Windows 実行ファイル
					MIMEStr="application/ms-download";
				}else if(ext.contains("doc")){					//MS-Word 文書ファイル
					MIMEStr="application/ms-word";
				}else if(ext.contains("xls")){					//MS-Excel 文書ファイル
					MIMEStr="application/ms-excel";
				}else if(ext.contains("mdb") || ext.endsWith("mde")){	//MS-Access データベース
					MIMEStr="application/ms-access";
				}else if(ext.contains("ppt") || ext.contains("pps")){	//application/	 	MS-パワーポイントプレゼンテーション*/
					MIMEStr="application/ms-powerpoint";
				}else{
					MIMEStr="*/*";
				}
			}
			dbBlock =MIMEStr+";ext="+ext;///////////////////////////////////
		//	Log.d("GetMIMEType",dbBlock);
			return MIMEStr;
		} catch (Exception e) {
			myErrorLog("GetMIMEType",dbBlock+"；"+e);
			return null;
		}	//http://ac-mopp.blogspot.jp/2011/12/android-mime-type.html
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent rData) { // startActivityForResult で起動させたアクティビティがfinish() により破棄されたときにコールされる
	 // requestCode : startActivityForResult の第二引数で指定した値が渡される
	 // resultCode : 起動先のActivity.setResult の第一引数が渡される
	 // Intent data : 起動先Activityから送られてくる Intent
		super.onActivityResult(requestCode, resultCode, rData);
		final String TAG = "onActivityResult";
		String dbMsg = "[OrgUtilFile]";
	//	Log.d("onActivityResult","requestCode="+requestCode+",resultCode="+resultCode+",rData="+rData);
		try{
			switch(requestCode) {
//			case MENU_SETTEI:				//エラー発生時
//				reWriteAllVal();//変数全設定
//				wriAllPrif();		//プリファレンス援項目の読込み
//				break;
			case MuList.MENU_FILTER_item0:			//511;//1.MediaStoreをSQLiteに読み込む
				findMuAll_a();									//リスト配列の処理();
				break;
			}
			myLog( TAG ,  dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e.toString());
		}
	}		//http://fernweh.jp/b/startactivityforresult/
	///////////////////////////////////////////////////////////////////////////////////
	public static void myLog(String TAG , String dbMsg) {
		MyUtil MyUtil = new MyUtil();
		MyUtil.myLog(TAG , dbMsg);
	}

	public static void myErrorLog(String TAG , String dbMsg) {
		MyUtil MyUtil = new MyUtil();
		MyUtil.myErrorLog(TAG , dbMsg);
	}
}
