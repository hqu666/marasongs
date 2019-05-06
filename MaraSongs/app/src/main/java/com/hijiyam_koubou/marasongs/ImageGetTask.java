package com.hijiyam_koubou.marasongs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ImageGetTask extends AsyncTask<String,Void,Bitmap> {		// Image取得用スレッドクラス
	private ImageView image;

	/**  ImageGetTask*/
	public ImageGetTask(ImageView _image) {
		final String TAG = "ImageGetTask[ImageGetTask]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			dbMsg= "_image=" + _image;
			image = _image;
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {		//汎用
			myErrorLog(TAG,dbMsg+"で"+e.toString());
		}
	}

	/**
	 * doInBackground */
	@Override
	protected Bitmap doInBackground(String... params) {
		final String TAG = "doInBackground[ImageGetTask]";
		String dbMsg= "開始";/////////////////////////////////////
		Bitmap image = null;
		try {
			dbMsg= "imageUrl=" + params[0];
			File srcFile = new File(params[0]);
			if(srcFile.exists()){
				InputStream imageIs = new FileInputStream(srcFile);
//				URL imageUrl = new URL( "file:/" + params[0]);				//"https:/" +でエラーは出ないが動作もしない
//				//java.net.MalformedURLException: Protocol not found: /storage/sdcard0/Android/data/com.android.providers.media/albumthumbs/1440782709833
//				dbMsg= dbMsg+"＞>>" + imageUrl;
//				InputStream imageIs;
//				imageIs = imageUrl.openStream();
				dbMsg= dbMsg+",imageUrl=" + imageIs;
				image = BitmapFactory.decodeStream(imageIs);
				dbMsg= dbMsg+",image=" + image;
			}
		//	myLog(TAG,dbMsg);
			return image;
		} catch (FileNotFoundException e) {			//URLなら		} catch (MalformedURLException e) {
			myErrorLog(TAG,dbMsg+"で"+e.toString());
			return null;
//		} catch (IOException e) {
//			myErrorLog(TAG,dbMsg+"で"+e.toString());
//			return null;
		}
	}

/**
 * onPostExecu @param  */
	@Override
	protected void onPostExecute(Bitmap result) {
		final String TAG = "onPostExecute[ImageGetTask]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			dbMsg= "Bitmap=" + result;
			image.setImageBitmap(result);					// 取得した画像をImageViewに設定します。
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {		//汎用
			myErrorLog(TAG,dbMsg+"で"+e.toString());
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

/**
 * 参考
 * http://kiwamunet.hateblo.jp/entry/2014/09/24/112005
 *
 */

