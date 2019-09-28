package com.hijiyam_koubou.marasongs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Util {
	public SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	public SimpleDateFormat sdffiles = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public SimpleDateFormat stf = new SimpleDateFormat("mm:ss SSS");		//02;03 414=123414

	//ファイル操作///////////////////
	/**
	 * 端末内でファイル保存できる場所
	 */
	public String getSavePath(Context context , String evironmentType , String savePassName) {
		final String TAG = "getSavePath[MPF]";
		String dbMsg = "開始";
		String write_folder = "";
		try {
			dbMsg += ",evironmentType=" + evironmentType;
			File photDir = Environment.getExternalStoragePublicDirectory(evironmentType);
			//              //自分のアプリ用の内部ディレクトリ    context.getFilesDir();
			dbMsg += ",photDir=" + photDir.getPath() + File.separator;      //pathSeparatorは：
			write_folder = photDir.getPath() + File.separator + savePassName;
			dbMsg += ",端末内の保存先=" + write_folder;
//			String local_dir_size = userDir.getFreeSpace() + "";// "5000000";
//			dbMsg +=+ ",保存先の空き容量=" + local_dir_size;
//			if ( local_dir_size.isEmpty() ) {
//				local_dir_size = "5000000";
//				dbMsg +=+ ">>" + local_dir_size;
//			}
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
		return write_folder;
	}            //端末内にファイル保存する為のパラメータ調整

	/**
	 * 指定されたパスが無ければ新規作成する
	 */
	public void maikOrgPass(String writeFolder) {
		final String TAG = "maikOrgPass[util]";
		String dbMsg = "開始";
		String local_dir = "";
		try {
			dbMsg += "writeFolder=" + writeFolder;
			File saveFolder = new File(writeFolder);
			if ( !saveFolder.exists() ) {
				saveFolder.mkdir();
				dbMsg += "作成";
			} else {
				dbMsg += "既存";
			}
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			Log.e(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	/**
	 * アンドロイドのデータベースへ登録
	 * // (登録しないとギャラリーなどにすぐに反映されないため)
	 * @param mimeType No.	Format	MimeType
	 *                 1	MP3	audio/mpeg
	 *                 2	M4A	audio/mp4
	 *                 3	WAV	audio/x-wav
	 *                 4	AMR	audio/amr
	 *                 5	AWB	audio/amr-wb
	 *                 6	WMA(※1）	audio/x-ms-wma
	 *                 7	OGG	application/ogg
	 *                 8	OGA	application/ogg
	 *                 9	AAC	audio/aac
	 *                 10	MK	audio/x-matroska
	 *                 11	MID	audio/midi
	 *                 12	MDI	audio/midi
	 *                 13	XMF	audio/midi
	 *                 14	RTTTL	audio/midi
	 *                 15	SMF	audio/sp-midi
	 *                 16	IMY	audio/imelody
	 *                 17	RTX	audio/midi
	 *                 18	OTA	audio/midi
	 *                 19	M3U	audio/x-mpegurl
	 *                 20	PLS	audio/x-scpls
	 *                 21	WPL	application/vnd.ms-wpl
	 *                 22	MPEG	video/mpeg
	 *                 23	MP4	video/mp4
	 *                 24	M4V	video/mp4
	 *                 25	3GP	video/3gpp
	 *                 26	3GPP	video/3gpp
	 *                 27	3G2		video/3gpp2
	 *                 28	3GPP2	video/3gpp2
	 *                 29	MKV	video/x-matroska
	 *                 30	WEBM	video/x-matroska
	 *                 31	TS	video/mp2ts
	 *                 32	WMV(※1）	video/x-ms-wmv
	 *                 33	ASF(※1）	video/x-ms-asf
	 *                 34	JPG	image/jpeg
	 *                 35	JPEG	image/jpeg
	 *                 36	GIF	image/gif
	 *                 37	PNG	image/png
	 *                 38	BMP	image/x-ms-bmp
	 *                 39	WBMP	image/vnd.wap.wbmp
	 */
	public void setContentValues(Context activity , String mimeType , String saveFileName) {
		final String TAG = "setContentValues[util]";
		String dbMsg = "開始";
		String local_dir = "";
		try {
			dbMsg += saveFileName + "を" + mimeType + "に";
			ContentValues values = new ContentValues();
			ContentResolver contentResolver = activity.getContentResolver();
			values.put(MediaStore.Images.Media.MIME_TYPE , mimeType);
			values.put("_data" , saveFileName);
			Uri rUrl = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI , values);
			dbMsg += ">>" + rUrl;
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			Log.e(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	/**
	 * このアプリケーション用のデータフォルダーを返す
	 */
	public String getAplPathName(Context context) {
		final String TAG = "getAplPathName[util]";
		String dbMsg = "開始";
		String local_dir = "";
		try {
			java.io.File wrDir = context.getFilesDir();//自分のアプリ用の内部ディレクトリ
			String wrDirName = wrDir.getPath();
			dbMsg += ",wrDir=" + wrDirName;            //wrDir=/data/user/0/com.example.hkuwayama.nuloger/files
			java.io.File file = new java.io.File(wrDir , wrDirName);
			local_dir = wrDir.getAbsolutePath();
			dbMsg += ",local_dir=" + local_dir;
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			Log.e(TAG , dbMsg + ";でエラー発生；" + er);
		}
		return local_dir;
	}

	public int getNowFileCount(Context context) {
		final String TAG = "getNowFileCount[util]";
		String dbMsg = "開始";
		int nowCount = 0;
		try {
			java.io.File[] files;
			String local_dir = getAplPathName(context);
			files = new java.io.File(local_dir).listFiles();
			if ( files != null ) {
				int fCount = files.length;
				for ( int i = 0 ; i < fCount ; i++ ) {
					dbMsg += "," + i + "/" + fCount;
					java.io.File rFile = files[i];
					String fName = rFile.getName();
					dbMsg += ")" + fName;
					if ( fName.endsWith(".csv") ) {
						nowCount++;
						dbMsg += ">>" + nowCount;
					}
				}
//							long wSize = file.length();
//							long maxFileSize = Long.parseLong(max_file_size);
//							dbMsg += ",wSize=" + wSize + "/max_file_size=" + maxFileSize;
//							if ( maxFileSize < wSize ) {
//								max_file_size = wSize + "";
//								dbMsg += ">>max_file_size=" + max_file_size;
//								//            setSaveParameter();                //端末内にファイル保存する為のパラメータ調整
//								myEditor.putString("max_file_size_key", max_file_size);
//								boolean kakikomi = myEditor.commit();
//								dbMsg += ",プリファレンス更新=" + kakikomi;
//							}
			}
			dbMsg += ",now_count=" + nowCount + "件";
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			Log.e(TAG , dbMsg + ";でエラー発生；" + er);
		}
		return nowCount;
	}


	/**
	 * 保存フォルダーを検索して最新の保存ファイルをサムネイルに表示する
	 */
	public String getSaveFiles(String findFileName) {
		final String TAG = "getSaveFiles[util]";
		String dbMsg = "";
		String retStr = "0";
		try {
			dbMsg += "findFileName=" + findFileName;
			File[] files = new File(findFileName).listFiles();
			String extentionStr = "";
			for ( File fObj : files ) {
				dbMsg += "\nretStr=" + retStr;
				String fName = fObj.getName();
				dbMsg += ",fName=" + fName;
				if ( fObj.isDirectory() ) {
					if ( Integer.parseInt(retStr) < Integer.parseInt(fName) ) {
						retStr = fName;
					}
				} else if ( fObj.isFile() && !fName.equals("pre.jpg") ) {
					int point = fName.lastIndexOf(".");  //	String[] fNames = fName.split(".");が効かない
					String cName = "";
					if ( point != -1 ) {
						cName = fName.substring(0 , point);
						extentionStr = fName.substring(point + 1);
					}
					dbMsg += ",cName=" + cName + ",extentionStr=" + extentionStr;
					if ( Long.parseLong(retStr) < Long.parseLong(cName) ) {
						retStr = cName;
						dbMsg += ">>retStr=" + retStr;
					}
				}
			}
			retStr = findFileName + File.separator + retStr;
			dbMsg += ">>retStr=" + retStr;
			if ( extentionStr.equals("") ) {
				dbMsg += ";フォルダ";
				files = new File(findFileName).listFiles();
				if ( 0 < files.length ) {
					retStr = getSaveFiles(retStr);
				}
			} else {
				dbMsg += ";最終ファイル";
				retStr += "." + extentionStr;
			}
			dbMsg += "=" + retStr;
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
		return retStr;
	}
	//画像操作//////////////ファイル操作///

	public int getRotatedPhotFile(File file) {
		final String TAG = "getRotatedPhotFile[util]";
		String dbMsg = "";
		int orientation = 0;
		try {
			ExifInterface exifInterface = null;

			try {
				exifInterface = new ExifInterface(file.getPath());
			} catch (IOException e) {
				e.printStackTrace();
				return orientation;
			}

			orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION , ExifInterface.ORIENTATION_UNDEFINED);        // 画像の向きを取得
			// 画像を回転させる処理をマトリクスに追加
			dbMsg += ",orientation=" + orientation;
			switch ( orientation ) {
				case ExifInterface.ORIENTATION_UNDEFINED:            //0;
					break;
				case ExifInterface.ORIENTATION_NORMAL:        //1:
					break;
				case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:    //2; 水平方向にリフレクト
//					matrix.postScale(-1f , 1f);
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:        //3; 180度回転
//					matrix.postRotate(180f);
					break;
				case ExifInterface.ORIENTATION_FLIP_VERTICAL:    //4; 垂直方向にリフレクト
//					matrix.postScale(1f , -1f);
					break;
				case ExifInterface.ORIENTATION_ROTATE_90:        //6; 反時計回り90度回転
//					matrix.postRotate(90f);
					break;
				case ExifInterface.ORIENTATION_TRANSVERSE:        //7; 時計回り90度回転し、垂直方向にリフレクト
//					matrix.postRotate(-90f);
//					matrix.postScale(1f , -1f);
					break;
				case ExifInterface.ORIENTATION_TRANSPOSE:        //5; 反時計回り90度回転し、垂直方向にリフレクト
//					matrix.postRotate(90f);
//					matrix.postScale(1f , -1f);
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:        //8; 反時計回りに270度回転（時計回りに90度回転）
//					matrix.postRotate(-90f);
					break;
			}
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
		return orientation;
	}

	/**
	 * 画像の回転後のマトリクスを取得
	 * @param file   入力画像
	 * @param matrix 元のマトリクス
	 * @return matrix 回転後のマトリクス
	 * https://qiita.com/sutchan/items/6ef7216cb8221bbf3894
	 */
	public Matrix getRotatedMatrix(File file , Matrix matrix) {
		final String TAG = "getRotatedMatrix[util]";
		String dbMsg = "";
		try {
			ExifInterface exifInterface = null;

			try {
				exifInterface = new ExifInterface(file.getPath());
			} catch (IOException e) {
				e.printStackTrace();
				return matrix;
			}

			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION , ExifInterface.ORIENTATION_UNDEFINED);        // 画像の向きを取得
			// 画像を回転させる処理をマトリクスに追加
			dbMsg += ",orientation=" + orientation;
			switch ( orientation ) {
				case ExifInterface.ORIENTATION_UNDEFINED:                //0;
					break;
				case ExifInterface.ORIENTATION_NORMAL:                    //1;
					break;
				case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:                //2; 水平方向にリフレクト
					matrix.postScale(-1f , 1f);
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:                    //3; 180度回転
					matrix.postRotate(180f);
					break;
				case ExifInterface.ORIENTATION_FLIP_VERTICAL:                //4; 垂直方向にリフレクト
					matrix.postScale(1f , -1f);
					break;
				case ExifInterface.ORIENTATION_ROTATE_90:                    //6; 反時計回り90度回転
					matrix.postRotate(90f);
					break;
				case ExifInterface.ORIENTATION_TRANSVERSE:                    //7; 時計回り90度回転し、垂直方向にリフレクト
					matrix.postRotate(-90f);
					matrix.postScale(1f , -1f);
					break;
				case ExifInterface.ORIENTATION_TRANSPOSE:                    //5; 反時計回り90度回転し、垂直方向にリフレクト
					matrix.postRotate(90f);
					matrix.postScale(1f , -1f);
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:                    //8; 反時計回りに270度回転（時計回りに90度回転）
					matrix.postRotate(-90f);
					break;
			}
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
		return matrix;
	}

	public int getDisplayOrientation(Activity activity) {
		final String TAG = "getDisplayOrientation[util}";
		String dbMsg = "";
		int degrees = 0;
		try {
			int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();   //helperからは((Activity)getContext()).
			switch ( rotation ) {
				case Surface.ROTATION_0:
					degrees = 0;
					break;
				case Surface.ROTATION_90:
					degrees = 90;
					break;
				case Surface.ROTATION_180:
					degrees = 180;
					break;
				case Surface.ROTATION_270:
					degrees = 270;
					break;
			}
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
		return degrees;
	}

	/**
	 * 反対の色を返す
	 * Illustrator の計算方法		https://q-az.net/complementary-color-javascript/
	 */
	public int complementaryColor(int orgColor) {
		final String TAG = "complementaryColor[util}";
		String dbMsg = "";
		int comColor = 0;
		try {
			dbMsg += "orgColor=" + orgColor;
			int orgAlfa = Color.alpha(orgColor);
			int orgRed = Color.red(orgColor);
			int orgGreen = Color.green(orgColor);
			int orgBlie = Color.blue(orgColor);
			dbMsg += "=a=" + orgAlfa + " r=" + orgRed + " g=" + orgGreen + " b=" + orgBlie;
//			 int rgbColor = Color.rgb(orgRed, orgGreen, orgBlie);
			float[] hsv = new float[3];
			Color.colorToHSV(orgColor , hsv);
			dbMsg += ",Hue=" + hsv[0] + "[dig],Saturation=" + hsv[1] + ",Value of Brightness=" + hsv[2];
			hsv[0] = (hsv[0] + 120) % 360.0f;
			dbMsg += ">Hue>" + hsv[0];
			comColor = Color.HSVToColor(orgAlfa , hsv);
			dbMsg += ",comColor=" + comColor + "=a=" + Color.alpha(comColor) + " r=" + Color.red(comColor) + " g=" + Color.green(comColor) + " b=" + Color.blue(comColor);
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
		return comColor;
	}


	////汎用関数///////////////////////////////////////////////画像操作/////
	public String retDateStr(long dateTimeVar , String patten) {
		final String TAG = "retDateStr[util}";
		String dbMsg = "";
		String retStr = "";
		try {
			dbMsg = "dateTimeVar=" + dateTimeVar;
			dbMsg += ",patten=" + patten;
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patten);
			retStr = simpleDateFormat.format(dateTimeVar);
			dbMsg += ">>" + retStr;
			myLog(TAG , dbMsg);
		} catch (Exception er) {
			myErrorLog(TAG , dbMsg + ";でエラー発生；" + er);
		}
		return retStr;
	}


	public boolean isIntVar(String val) {
		try {
			Integer.parseInt(val);
			return true;
		} catch (NumberFormatException nfex) {
			return false;
//			myErrorLog(TAG, dbMsg + ";でエラー発生；" + er);
		}
	}

	public boolean isLongVal(String val) {
		try {
			Long.parseLong(val);
			return true;
		} catch (NumberFormatException nfex) {
			return false;
		}
	}

	public boolean isFloatVal(String val) {
		try {
			Float.parseFloat(val);
			return true;
		} catch (NumberFormatException nfex) {
			return false;
		}
	}


	public boolean isDoubleVal(String val) {
		try {
			Double.parseDouble(val);
			return true;
		} catch (NumberFormatException nfex) {
			return false;
		}
	}

	public void dbColumnCheck( Cursor carsor , int index) {
		final String TAG = "dbColumnCheck";
		String dbMsg = "[util]";
		try {
			dbMsg += index +  "/" + carsor.getCount() + "件";
			if(carsor.moveToPosition(index)) {
				int colCount = carsor.getColumnCount();
				dbMsg += "," + colCount + "列";
	//				colNames =  "id INTEGER PRIMARY KEY AUTOINCREMENT,";
				for ( int i = 0 ; i < colCount ; i++ ) {
					dbMsg += "\n(" + i + ")";
					String colName = carsor.getColumnName(i);
					dbMsg += colName;
					String data = carsor.getString(i);
					int iType = carsor.getType(i);	;  //1は数字、3は文字
					dbMsg += ",type=" + iType;  //1は数字、3は文字
					dbMsg += ":" + data;  //1は数字、3は文字
				}
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			  myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}


	public void dBaceColumnCheck( Cursor carsor , int index) {
		final String TAG = "dBaceColumnCheck";
		String dbMsg = "[util]";
		try {
			dbMsg += index +  "/" + carsor.getCount() + "件";
			if(carsor.moveToPosition(index)){
				int colCount = carsor.getColumnCount();
				dbMsg += "," + colCount + "列";
//				colNames =  "id INTEGER PRIMARY KEY AUTOINCREMENT,";
				for (int i = 0; i < colCount; i++) {
					dbMsg += "(" + i + ")";
					String colName = carsor.getColumnName(i);
					dbMsg += colName;
					String data = "";
					int iType = carsor.getType(i);  //1は数字、3は文字
					dbMsg += ",type=" + iType;  //1は数字、3は文字
					if ( carsor.FIELD_TYPE_STRING == iType ) {
						data = carsor.getString(i);
					} else if ( carsor.FIELD_TYPE_INTEGER == iType ) {
						data = carsor.getInt(i) + "";
					} else if ( carsor.FIELD_TYPE_FLOAT == iType ) {
						data = carsor.getFloat(i) + "";
					}
			/*
					if(colName.equals("datetaken")) {  //	null
						dbMsg += ";datetaken";
					}else if(colName.equals("_size")) {  //			MediaStore.Audio.Media.SIZE
						dbMsg += ";SIZE";
					}else if(colName.equals("mime_type")) {//				MediaStore.Audio.Media.MIME_TYPE
						dbMsg += ";MIME_TYPE";
					}else if(colName.equals("is_drm")) {
					}else if(colName.equals("artist_key")) { 	//	MediaStore.Audio.Media.ARTIST_KEY
						dbMsg += ";ARTIST_KEY";
					}else if(colName.equals("album_key")) {      //						MediaStore.Audio.Media.ALBUM_KEY
						dbMsg += ";ALBUM_KEY";
					}else if(colName.equals("is_audiobook")) {
						dbMsg += ";is_audiobook";
					}else if(colName.equals("volume_name")) {
						dbMsg += ";volume_name";
					}else if(i == carsor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)) {
						dbMsg += ";IS_MUSIC";
					}else if(i == carsor.getColumnIndex(MediaStore.Audio.Media.IS_ALARM)) {
						dbMsg += ";IS_ALARM";
					}else if(i == carsor.getColumnIndex(MediaStore.Audio.Media.IS_NOTIFICATION)) {
						dbMsg += ";IS_NOTIFICATION";
					}else if(i == carsor.getColumnIndex(MediaStore.Audio.Media.IS_PODCAST)) {
						dbMsg += ";IS_PODCAST";
					}else if(i == carsor.getColumnIndex(MediaStore.Audio.Media.IS_RINGTONE)) {
						dbMsg += ";IS_RINGTONE";
					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)))){
						dbMsg += ";ARTIST_ID";
					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.ARTIST)))){
						dbMsg += ";ARTIST";
					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)))){
						dbMsg += ";ALBUM_ID";
					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.ALBUM)))){
						dbMsg += ";ALBUM";
					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.TRACK)))){
						dbMsg += ";TRACK";
//					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.TITLE_KEY)))){
//						dbMsg += ";TITLE_KEY";
					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.TITLE)))){
						dbMsg += ";TITLE";
					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media._ID)))){
						dbMsg += ";_ID";
					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.DATA)))){
						dbMsg += ";DATA";
					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.YEAR)))){
						dbMsg += ";YEAR";
					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.DURATION)))){
						dbMsg += ";DURATION";
					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)))){
						dbMsg += ";DISPLAY_NAME";
					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)))){         			//リッピングした日
						dbMsg += ";DATE_MODIFIED";
					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)))){						//更新した日
						dbMsg += ";DATE_ADDED";
//					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.CONTENT_TYPE)))){    //Couldn't read row 0, col -1 from CursorWindow.  Make sure the Cursor is initialized correctly before accessing data from it.
//						dbMsg += ";CONTENT_TYPE";
					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.COMPOSER)))){
						dbMsg += ";COMPOSER";
					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.WIDTH)))){
						dbMsg += ";WIDTH";
//					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.RECORD_SOUND_ACTION)))){
//						dbMsg += ";RECORD_SOUND_ACTION";
					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.WIDTH)))){
						dbMsg += ";WIDTH";
					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.SIZE)))){
						dbMsg += ";SIZE";
					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.HEIGHT)))){
						dbMsg += ";HEIGHT";
//					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.EXTRA_MAX_BYTES)))){
//						dbMsg += ";EXTRA_MAX_BYTES";
//					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.ENTRY_CONTENT_TYPE)))){
//						dbMsg += ";ENTRY_CONTENT_TYPE";
//					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.DEFAULT_SORT_ORDER)))){
//						dbMsg += ";DEFAULT_SORT_ORDER";
					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.BOOKMARK)))){
						dbMsg += ";BOOKMARK";
					}else if(data.equals(carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media._COUNT)))){
						dbMsg += ";_COUNT";
					}else{
						dbMsg += ";該当無し";
					}
			 */
					dbMsg += " = " + data ;
					if ( carsor.FIELD_TYPE_INTEGER == iType ) {
						int rInt = carsor.getInt(i);
						if ( 24 * 60 * 60 * 1000 < rInt ) {	//	"date_added") || colName.equals("date_modified"         //86,400,000‬         ,      1,569,599,212>>2019-09-28 00:46:52
							String mod = sdffiles.format(new Date(Long.valueOf(data) * 1000));
							dbMsg += ">>" + mod;
						}else if ( 1000 < rInt && 1000 < (12 * 60*60*1000)) {
							String moh = stf.format(new Date(Long.valueOf(data)));
							dbMsg += ">>" + moh;
						}
					}
					dbMsg += "\n";
				}
			}
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	public String getAlbumArtist( Cursor carsor ) {
		final String TAG = "getAlbumArtist";
		String dbMsg = "[util]";
		String albumArtist = "";
		String b_Artist = "";
		try {
			if(carsor.moveToFirst()){
				do{
					dbMsg += "(" + carsor.getPosition() + "/" + carsor.getCount() + ")";
					String artistName = carsor.getString(carsor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
					dbMsg += artistName;
					if ( 0 == carsor.getPosition() ) {
						albumArtist = artistName;
					} else {
						if ( !artistName.equals(albumArtist) ) {
							if ( artistName.startsWith(albumArtist) ) {
								dbMsg += ",初めが同じ";
							} else if ( artistName.endsWith(albumArtist) ) {
								dbMsg += ",後ろが同じ";
							} else if ( b_Artist.equals(artistName) ) {
								dbMsg += "連続して違っても前と同じ";
								albumArtist = artistName;
							}
						}
					}
					b_Artist = artistName;
				}while(carsor.moveToNext());
			}
			dbMsg += ">>albumArtist=" + albumArtist;
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return albumArtist;
	}

	/**文字プリファレンスの読込み*/
	public static String getPrefStr(String keyNmae , String defaultVal ,Context context) {
		String retStr = "";
		final String TAG = "getPrefStr";
		String dbMsg="[util]keyNmae=" + keyNmae + ",defaultVal=" + defaultVal;
		try {
			dbMsg +=",context=" + context;
			String pefName = context.getResources().getString(R.string.pref_main_file);
			SharedPreferences sharedPref = context.getSharedPreferences(pefName,context.MODE_PRIVATE);		//	getSharedPreferences(prefFname,MODE_PRIVATE);
			retStr = (String)sharedPref.getString(keyNmae , defaultVal);
			dbMsg +=  ",retStr="  + retStr;
//			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return retStr;
	}

	/**整数プリファレンスの読込み*/
	public static int getPrefInt(String keyNmae , int defaultVal,Context context) {
		int retInt = -99;
		final String TAG = "getPrefInt";
		String dbMsg="[util]keyNmae=" + keyNmae+ ",defaultVal=" + defaultVal;
		try {
			String pefName = context.getResources().getString(R.string.pref_main_file);
			SharedPreferences sharedPref = context.getSharedPreferences(pefName,context.MODE_PRIVATE);		//	getSharedPreferences(prefFname,MODE_PRIVATE);
			try {
				retInt = sharedPref.getInt(keyNmae , defaultVal);
				dbMsg +=  ",retIn="  + retInt;
			} catch (Exception e) {
				myErrorLog(TAG ,  dbMsg + "で" + e);
				String sStr = sharedPref.getString(keyNmae, String.valueOf(defaultVal));
				dbMsg +=  ",sStr="  + sStr;
				retInt = Integer.parseInt(sStr);
			}
			dbMsg +=  ",retInt="  + retInt;
//			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return retInt;
	}

	public static boolean setPreStr(String keyNmae , String wrightVal , Context context) {
		boolean retBool = false;
		final String TAG = "setPreStr";
		String dbMsg="[util]keyNmae=" + keyNmae+ ",defaultVal=" + wrightVal;
		try {
			String pefName = context.getResources().getString(R.string.pref_main_file);
			SharedPreferences sharedPref = context.getSharedPreferences(pefName,context.MODE_PRIVATE);		//	getSharedPreferences(prefFname,MODE_PRIVATE);
			SharedPreferences.Editor myEditor = sharedPref.edit();
			myEditor.putString( keyNmae, wrightVal);						//再生中のファイル名  Editor に値を代入
			retBool = myEditor.commit();
			dbMsg +=">>書込み成功="+ retBool;
//			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return retBool;
	}

	/** 整数プリファレンスの書込み*/
	public static boolean setPrefInt(String keyNmae , int wrightVal , Context context) {
		boolean retBool = false;
		final String TAG = "setPrefInt";
		String dbMsg="[util]keyNmae=" + keyNmae+ ",defaultVal=" + wrightVal;
		try {
			String pefName = context.getResources().getString(R.string.pref_main_file);
			SharedPreferences sharedPref = context.getSharedPreferences(pefName,context.MODE_PRIVATE);		//	getSharedPreferences(prefFname,MODE_PRIVATE);
			SharedPreferences.Editor myEditor = sharedPref.edit();
			myEditor.putInt( keyNmae, wrightVal);						//再生中のファイル名  Editor に値を代入
			retBool = myEditor.commit();
			dbMsg +=">>書込み成功="+ retBool;
//			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return retBool;
	}
	/** 整数プリファレンスの書込み*/
	public static boolean setPrefBool(String keyNmae , boolean wrightVal , Context context) {
		boolean retBool = false;
		final String TAG = "setPrefInt";
		String dbMsg="[util]keyNmae=" + keyNmae+ ",defaultVal=" + wrightVal;
		try {
			String pefName = context.getResources().getString(R.string.pref_main_file);
			SharedPreferences sharedPref = context.getSharedPreferences(pefName,context.MODE_PRIVATE);		//	getSharedPreferences(prefFname,MODE_PRIVATE);
			SharedPreferences.Editor myEditor = sharedPref.edit();
//			dbMsg= keyNmae+";"+ keyNmae;
//			key = String.valueOf( keyNmae );
//			vStr = String.valueOf( vStr );
//			dbMsg= ">>" + key+";"+ vStr;
			myEditor.putBoolean( keyNmae, wrightVal);						//再生中のファイル名  Editor に値を代入
			retBool = myEditor.commit();
			dbMsg +=">>書込み成功="+ retBool;
//			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return retBool;
	}

//保留；入力ダイアログ
//    public String retStr = "";
//     public void inputShow(String titolStr, String mggStr, String defaultStr) {
//         retStr = defaultStr;
//         LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
//         final View layout = inflater.inflate( R.layout.step_setting,(ViewGroup) findViewById(R.id.ss_root));
//         EditText ss_stet_et = (EditText) layout.findViewById(R.id.ss_stet_et);
//         TextView ss_msg_tv = (TextView) layout.findViewById(R.id.ss_msg_tv);
//         // アラーとダイアログ を生成
//         AlertDialog.Builder builder = new AlertDialog.Builder(this);
//         builder.setTitle(titolStr);
//         builder.setMessage(mggStr);
//         builder.setView(layout);
//         ss_stet_et.setText(defaultStr);
//
//         builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//             public void onClick(DialogInterface dialog, int which) {
//                 // OK ボタンクリック処理
//                 EditText text = (EditText) layout.findViewById(R.id.ss_stet_et);
//                 retStr = text.getText().toString();
//             }
//         });
//         builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//             public void onClick(DialogInterface dialog, int which) {
//               }
//         });
//
//         // 表示
//         builder.create().show();
//    }

//	public static boolean messageShowRet= false;
	public static void messageShow(String titolStr , String mggStr , Context context) {
		new AlertDialog.Builder(context).setTitle(titolStr).setMessage(mggStr).setPositiveButton(android.R.string.ok , new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog , int which) {
				if(which == 1){
//					messageShowRet = true;
				}
			}
		}).create().show();
//		return messageShowRet;
	}

	public static boolean debugNow = true;

	public static void myLog(String TAG , String dbMsg) {
		try {
			if ( debugNow ) {
				if(23 < TAG.length()){     				//APIL24以上（Android 7.0 Nougat 以上)
					TAG = TAG.substring(0,22);
				}
				Log.w(TAG , dbMsg + "");
			}
		} catch (Exception er) {
			Log.e(TAG , dbMsg + ";でエラー発生；" + er);
		}
	}

	public static boolean errorCheckNow = true;

	public static void myErrorLog(String TAG , String dbMsg) {
		if ( errorCheckNow ) {
			if(23 < TAG.length()){
				TAG = TAG.substring(0,22);
			}
			Log.e(TAG , dbMsg + "");
		}
	}

}
