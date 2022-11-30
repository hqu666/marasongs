package com.hijiyam_koubou.marasongs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class FftView extends View {
	// ----------------------------
	// 定数
	// ----------------------------
	private static float FFT_PEAK_VALUE = (float) (128 * Math.sqrt(2));							// ピーク値
	private static float DISPLAY_MINIMUM_DB = -30;												// 表示デシベル数の下限		 -30
	private static float DISPLAY_MINIMUM_HZ = 35;												// 表示する最小周波数		35
	private static float DISPLAY_MAXIMUM_HZ = 30000;												// 表示する最大周波数
	private static float BAND_MINIMUM_HZ = 40;													// バンド表示の最小周波数	40
	private static float BAND_MAXIMUM_HZ = 28000;												// バンド表示の最大周波数
	private static int BAND_DEFAULT_NUMBER = 16;													// バンドのデフォルト数		16
	private static float BAND_INNER_OFFSET = 4;													// バンドの内側の表示オフセット
	private static int FFT_DATA_SHADER_START_COLOR_ID = android.R.color.holo_blue_dark;		// FFTデータの描画色ID
	private static int FFT_DATA_SHADER_END_COLOR_ID = android.R.color.white;
	private static int LOG_GRID_COLOR_ID = android.R.color.secondary_text_light;				// 対数グリッドの色ID
	// ----------------------------
	// 変数
	// ----------------------------
	private int currentWidth_;								// Viewのサイズ
	private int currentHeight_;
	private byte[] fftData_;								// FFTデータ
	private Paint fftDataPaint_;							// FFTデータの色
	private float[] logGridDataX_;						// 対数グリッドの座標データ
	private float[] logGridDataY_;
	private Paint logGridPaint_;							// 対数グリッドの色
	private Paint textPaint;								// 文字設定
	private int samplingRate_;								// サンプリングレート
	private int bandNumber_;								// 表示するバンドの数
	private RectF[] bandRects_;							// バンドの矩形
	private boolean isBandEnabled_;						// バンドを表示するか(非表示でパルスを描画)
	private int minLogarithm_;								// 対数の範囲 (10^xでいうxの数)
	private int maxLogarithm_;
	private float logBlockWidth_;							// 対数の区間あたりの幅 (e.g. 10^1から10^2と，10^2から10^3の描画幅は一緒)
	private float logOffsetX_;							// X方向の表示オフセット
	private int bandRegionMinX_;							// バンド全体のX方向の表示域
	private int bandRegionMaxX_;
	private int bandWidth_;								// 個々のバンドの幅
	private float[] bandFftData_;							// バンドのデータ
	private LinearGradient fftDataShader_;					// データ表示用のシェーダ
	private float fontSize = 1;							// X方向の表示オフセット

	public FftView(Context context) {	// コンストラクタ
		super(context);		
		final String TAG = "FftView[FftView]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
//				dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			dbMsg="Context=" + context;/////////////////////////////////////
			initialize();
	//		myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}
	
	public FftView(Context context, AttributeSet attrs) {
		super(context, attrs);
		final String TAG = "FftView[FftView]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
//				dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			dbMsg="Context=" + context + ",AttributeSet=" + attrs;/////////////////////////////////////
			initialize();
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}
	
	public FftView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		final String TAG = "FftView[FftView]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
//				dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			dbMsg="Context=" + context + ",AttributeSet=" + attrs + ",defStyleAttr=" + defStyleAttr;/////////////////////////////////////
			initialize();
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	private void initialize() {	// 初期化
		final String TAG = "initialize[FftView]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
//				dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			bandNumber_ = BAND_DEFAULT_NUMBER;		// 変数設定
			isBandEnabled_ = true;			// バーの領域確保
			bandRects_ = new RectF[bandNumber_];
			for(int i = 0; i < bandNumber_; ++i){
				bandRects_[i] = new RectF();
			}
			dbMsg="bandRects_=" + bandRects_.length;/////////////////////////////////////
			bandFftData_ = new float[bandNumber_];		// データを格納する配列
			fftDataPaint_ = new Paint();		// ペイントの設定
			fftDataPaint_.setStrokeWidth(1f);
			fftDataPaint_.setAntiAlias(true);
			
			logGridPaint_ = new Paint();
			logGridPaint_.setStrokeWidth(1f);
			logGridPaint_.setAntiAlias(true);
			logGridPaint_.setColor(getResources().getColor(LOG_GRID_COLOR_ID));
			textPaint = new Paint();								// 文字設定
			textPaint.setTextSize(fontSize);
			textPaint.setColor( Color.WHITE);
	//		myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		final String TAG = "onWindowFocusChanged[FftView]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
//				dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			currentHeight_ = getHeight();		// Viewの高さ，幅が取れるのでそれらに依存した計算を行う
			dbMsg="currentHeight_=" + currentHeight_;/////////////////////////////////////
			if(0< currentHeight_){
				currentWidth_ = getWidth();
				dbMsg +="/" + currentWidth_;/////////////////////////////////////
				if(0< currentWidth_){
					calculateViewSizeDependedData();
				}
			}
	//		myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}
	
	private void calculateViewSizeDependedData() {												// Viewのサイズを基に対数グリッドとバーの座標を計算
		final String TAG = "calculateViewSizeDependedData[FftView]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
//				dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			dbMsg= "[DISPLAY;" + DISPLAY_MINIMUM_HZ +"/" + DISPLAY_MAXIMUM_HZ + " HZ]";/////////////////////////////////////
			minLogarithm_ = (int) Math.floor(Math.log10(DISPLAY_MINIMUM_HZ));		// 対数の範囲を計算
			maxLogarithm_ = (int) Math.ceil(Math.log10(DISPLAY_MAXIMUM_HZ));		
			dbMsg +="[" + minLogarithm_ +"/" + maxLogarithm_ + "]";/////////////////////////////////////
			logBlockWidth_ = (float) (getWidth() / (Math.log10(DISPLAY_MAXIMUM_HZ) - Math.log10(DISPLAY_MINIMUM_HZ)));		// 対数の区間あたりの幅
			dbMsg +=",logBlockWidth_" + logBlockWidth_;/////////////////////////////////////
			logOffsetX_ = (float) (Math.log10(DISPLAY_MINIMUM_HZ) * logBlockWidth_);		// X方向の表示オフセット
			dbMsg +=",logOffsetX_" + logOffsetX_;/////////////////////////////////////
			// グリッドの線の数を数えて領域を確保，座標を計算して格納
			// 縦
			int lineNumberX = 10 - (int) (DISPLAY_MINIMUM_HZ / Math.pow(10, minLogarithm_));
			lineNumberX += 9 * (maxLogarithm_ - minLogarithm_ - 2);
			lineNumberX += (int) (DISPLAY_MAXIMUM_HZ / Math.pow(10, maxLogarithm_ - 1));
			logGridDataX_ = new float[lineNumberX];
			int logGridDataCounterX = 0;
			int left = getLeft();
			int right = getRight();
			for(int i = minLogarithm_; i < maxLogarithm_; ++i){
				dbMsg=  dbMsg+"[" + i +"/" + maxLogarithm_ + " ]";/////////////////////////////////////
				for(int j = 1; j < 10; ++j){
					dbMsg=  dbMsg+"[" + j +"/" + 10 + " ]";/////////////////////////////////////
					float x = (float) Math.log10(Math.pow(10, i)*j) * logBlockWidth_ - logOffsetX_;
					dbMsg= dbMsg+",x" + x;/////////////////////////////////////
					if(x >= left && x <= right){
						logGridDataX_[logGridDataCounterX] = x;
						logGridDataCounterX++;
					}
				}
			}
			// 横
			int lineNumberY = (int) (Math.ceil(-DISPLAY_MINIMUM_DB / 10));
			float deltaY = getHeight() / -DISPLAY_MINIMUM_DB * 10;
			logGridDataY_ = new float[lineNumberY];
			int top = getTop();
			for(int i = 0; i < lineNumberY; ++i){
				logGridDataY_[i] = top + deltaY * i;
			}		
			// 各々のバンドの座標を計算
			bandRegionMinX_ = (int) (Math.log10(BAND_MINIMUM_HZ) * logBlockWidth_ - logOffsetX_);
			bandRegionMaxX_ = (int) (Math.log10(BAND_MAXIMUM_HZ) * logBlockWidth_ - logOffsetX_);
			bandWidth_ = (bandRegionMaxX_ - bandRegionMinX_) / bandNumber_;
			int bottom = getBottom();
			for(int i = 0; i < bandNumber_; ++i){
				bandRects_[i].bottom = bottom-fontSize;
				bandRects_[i].top = bottom;	// バーが表示されないように
				bandRects_[i].left = bandRegionMinX_ + (bandWidth_ * i) + BAND_INNER_OFFSET;
				bandRects_[i].right = bandRects_[i].left + bandWidth_ - BAND_INNER_OFFSET;
			}
			dbMsg +=",bandRects_=" + bandRects_.length +"件";/////////////////////////////////////
			
			// シェーダーを設定
			int color0 = getResources().getColor(FFT_DATA_SHADER_START_COLOR_ID);
			int color1 = getResources().getColor(FFT_DATA_SHADER_END_COLOR_ID);
			fftDataShader_ = new LinearGradient(0, bottom, 0, top, color0, color1, Shader.TileMode.CLAMP);
			fftDataPaint_.setShader(fftDataShader_);
	//		myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}
	
	public void setSamplingRate(int samplingRateInMilliHz) {	// サンプリングレート
		final String TAG = "setSamplingRate[FftView]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
//				dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			dbMsg= "[samplingRateInMilliHz;" + samplingRateInMilliHz + " HZ]";/////////////////////////////////////
			samplingRate_ = samplingRateInMilliHz / 1000;
	//		myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}
	
	public int getSamplingRate() {
		final String TAG = "getSamplingRate[FftView]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
//				dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return samplingRate_;
	}

	public void update(byte[] bytes) {	// 更新
		final String TAG = "update[FftView]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
//				dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			dbMsg= "bytes=" + bytes ;/////////////////////////////////////
			fftData_ = bytes;
			invalidate();
	//		myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {	// 描画
		super.onDraw(canvas);
		final String TAG = "onDraw[FftView]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
//				dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			dbMsg= "currentWidth_=" + currentWidth_ + "×Height_=" + currentHeight_ ;/////////////////////////////////////
			if(currentWidth_ != getWidth() || currentHeight_ != getHeight()){		// Viewのサイズ変更があった場合，再計算
				calculateViewSizeDependedData();
			}
			drawLogGrid(canvas);		// グリッド描画
			dbMsg +="、fftData_=" + fftData_ ;/////////////////////////////////////
			if (fftData_ == null){		// 波形データがない場合には処理を行わない
				return;
			}
			drawFft(canvas);		// FFTデータの描画
	//		myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}
	
	private void drawFft(Canvas canvas) {	// FFTの内容を描画
		final String TAG = "drawFft[FftView]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
//				dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			int top = getTop();		// Viewのサイズ情報
			int height = (int) (getHeight()-fontSize);
			int fftNum = fftData_.length / 2;					// データの個数
			dbMsg= "isBandEnabled_=" + isBandEnabled_ ;/////////////////////////////////////
			if(isBandEnabled_){									// データをバンドに加工して表示
				for(int i = 0; i < bandNumber_; ++i){			// データの初期化
					bandFftData_[i] = 0;
				}
				dbMsg +=",bandFftData_=" + bandFftData_.length + "件" ;/////////////////////////////////////
				for(int i = 1; i < fftNum; ++i){												// データを順に見ていく
					float frequency = (float) (i * samplingRate_ / 2) / fftNum;				// 注目しているデータの周波数
					float x = (float) (Math.log10(frequency) * logBlockWidth_) - logOffsetX_;				// 表示位置から対応するバンドのインデックスを計算
					int index = (int) (x - bandRegionMinX_) / bandWidth_;
					if(index >= 0 && index < bandNumber_){
						float amplitude = (float) Math.sqrt(Math.pow((float)fftData_[i * 2], 2) + Math.pow((float)fftData_[i * 2 + 1], 2));					// 振幅スペクトルを計算
						if(amplitude > 0 ){
							if(bandFftData_[index] < amplitude) {						// 対応する区間で一番大きい値を取っておく
								bandFftData_[index] = amplitude;
							}
						}
					}
				}
				dbMsg +=",bandFftData_=" + bandFftData_.length + "件" ;/////////////////////////////////////
				for(int i = 0; i < bandNumber_; ++i){												// バーの高さを計算して描画
					float db = (float) (20.0f * Math.log10(bandFftData_[i]/FFT_PEAK_VALUE));
					float y = top - db / -DISPLAY_MINIMUM_DB * height;
					bandRects_[i].top = y;
					canvas.drawRect(bandRects_[i], fftDataPaint_);
				}
				dbMsg +=",bandRects_=" + bandRects_.length + "件" ;/////////////////////////////////////
			}else{																				// データをそのまま線分で表示
				int bottom = getBottom();
				int right = getRight();
				int left = getLeft();
				dbMsg +=",bottom=" + bottom + ",right=" + right + ",left=" + left ;/////////////////////////////////////
				float frequency = 0;
				float x = 0;
				for(int i = 1; i < fftNum; ++i){												// 直流成分(0番目)は計算しない
					dbMsg +="[" + i + "/" + fftNum + "]";/////////////////////////////////////
					frequency = (float) (i * samplingRate_ / 2) / fftNum;				// 注目しているデータの周波数
					dbMsg +=frequency + "Hz";/////////////////////////////////////
					float amplitude = (float) Math.sqrt(Math.pow((float)fftData_[i * 2], 2) + Math.pow((float)fftData_[i * 2 + 1], 2));	// 振幅スペクトルからデシベル数を計算	fftData_はupdateで渡されるbyte[] bytes
					float db = (float) (20.0f * Math.log10(amplitude / FFT_PEAK_VALUE));													//ピーク値；FFT_PEAK_VALUEは初期設定で(float) (128 * Math.sqrt(2));
					dbMsg +=db + "db";/////////////////////////////////////
					x = (float) (Math.log10(frequency) * logBlockWidth_) - logOffsetX_;				// 描画
					if(x >= left && x <= right){
						float y = top - db / -DISPLAY_MINIMUM_DB * height;
						canvas.drawLine(x, bottom, x, y, fftDataPaint_);					//fftDataPaint_はinitializeで設定
					}
				}
			}
	//		myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	private void drawLogGrid(Canvas canvas) {	// グリッド描画
		final String TAG = "drawLogGrid[FftView]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
//				dbMsg=ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			int bottom = (int) (getBottom()-fontSize);				// 横方向
			dbMsg= "bottom" + bottom;/////////////////////////////////////
			int top = getTop();
			dbMsg= dbMsg+ "～top" + top;/////////////////////////////////////
//			float frequency = 0;
//			String b_frequency = "1";
//			int fftNum = fftData_.length / 2;					// データの個数
//			float xPoint = 0;
//			int i = 0;
			for(float x: logGridDataX_) {
				dbMsg= dbMsg+ "[x=" + x + "/" + logGridDataX_.length + "]";///calculateViewSizeDependedDataで設定
//				xPoint = x;
				canvas.drawLine(x, bottom, x, top, logGridPaint_);
				//数値記入
//				i = i+1;
//				frequency = (float) (i * samplingRate_ / 2) / fftNum;				// 注目しているデータの周波数
//				dbMsg= dbMsg+ "frequency" + frequency;/////////////////////////////////////
//				if((Float.valueOf(b_frequency) * 10) < frequency){
//					dbMsg= "frequency" + frequency+ "(b_frequency=" + b_frequency + ")";/////////////////////////////////////
//					b_frequency = String.valueOf(Integer.valueOf(b_frequency)*10);
//					canvas.drawText(String.valueOf(b_frequency), xPoint, bottom + fontSize, textPaint);
//				}
			}
			int width = getWidth();		// 縦方向
			dbMsg= dbMsg+ "width" + width;/////////////////////////////////////
//			canvas.drawText(String.valueOf("Hz"), width- fontSize, bottom + fontSize, textPaint);
//			canvas.drawText(String.valueOf("db"), width- fontSize*2,fontSize, textPaint);
//			int lineNumberY = (int) (Math.ceil(-DISPLAY_MINIMUM_DB / 10));
//			dbMsg= dbMsg+ ",lineNumberY=" + lineNumberY;/////////////////////////////////////
//			int haba = (int) (DISPLAY_MINIMUM_DB/lineNumberY);
//			dbMsg= dbMsg+ ",haba=" + haba;/////////////////////////////////////
//			i = 0;
			for(float y: logGridDataY_) {
				dbMsg= dbMsg+ "[y=" + y + "/" + logGridDataY_.length + "]";/////////////////////////////////////
				canvas.drawLine(0, y, width, y, logGridPaint_);
				//数値記入
//				String dbS = String.valueOf(DISPLAY_MINIMUM_DB  - (lineNumberY-i)* haba) ;
//				dbMsg= dbMsg+ dbS;/////////////////////////////////////
//				canvas.drawText(String.valueOf(dbS ), width- fontSize*3,y, textPaint);
//				i = i+1;
			}
	//		myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

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