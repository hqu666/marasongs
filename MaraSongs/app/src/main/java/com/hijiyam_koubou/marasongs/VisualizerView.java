package com.hijiyam_koubou.marasongs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

public class VisualizerView  extends View {
	private byte[] mBytes;
	private float[] mPoints;
	private Rect mRect = new Rect();
	private Paint mForePaint = new Paint();
	
	public VisualizerView(Context context) {
		super(context);
		final String TAG = "VisualizerView";
		String dbMsg= "[VisualizerView]";
		try{
			dbMsg+= "context =" + context;
			init();
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}
	
	private void init() {
		final String TAG = "init[VisualizerView]";
		String dbMsg= "[VisualizerView]";
		try{
			mBytes = null;
			mForePaint.setStrokeWidth(1f);
			mForePaint.setAntiAlias(true);
			mForePaint.setColor(Color.rgb(0, 128, 255));
			dbMsg += "mForePaint =" + mForePaint;
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}
	
	public void updateVisualizer(byte[] bytes) {
		final String TAG = "updateVisualizer[VisualizerView]";
		String dbMsg= "[VisualizerView]";
		try{
			dbMsg += "mBytes = " + mBytes;
			mBytes = bytes;
			dbMsg +=">>" + mBytes;
			invalidate();
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		final String TAG = "onDraw[VisualizerView]";
		String dbMsg= "[VisualizerView]";
		try{
			dbMsg += "mBytes = " + mBytes;
			if (mBytes == null) {
				return;
			}
			if (mPoints == null || mPoints.length < mBytes.length * 4) {
				mPoints = new float[mBytes.length * 4];
			}
			dbMsg +=",mPoints = " + mPoints;
			mRect.set(0, 0, getWidth(), getHeight());
			for (int i = 0; i < mBytes.length - 1; i++) {
				mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
				mPoints[i * 4 + 1] = mRect.height() / 2 + ((byte) (mBytes[i] + 128)) * (mRect.height() / 2) / 128;
				mPoints[i * 4 + 2] = mRect.width() * (i + 1) / (mBytes.length - 1);
				mPoints[i * 4 + 3] = mRect.height() / 2 + ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2) / 128;
			}
			dbMsg +=">> " + mPoints;
			canvas.drawLines(mPoints, mForePaint);
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
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

//http://tools.oesf.biz/android-4.1.2_r1.0/xref/development/samples/ApiDemos/src/com/example/android/apis/media/AudioFxDemo.java#mVisualizerView