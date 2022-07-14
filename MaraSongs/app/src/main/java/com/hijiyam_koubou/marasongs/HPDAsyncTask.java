package com.hijiyam_koubou.marasongs;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.widget.Toast;

import java.text.NumberFormat;

/**
 * AsyncTaskの引数は、デフォルトでは AsyncTask<Params, Progress, Result>です。これはそれぞれ <入力パラメータ、進行度、結果のデータ型> を表しています。
 * 入力パラメータはActivityのexecute()で渡した引数の型です。
 * 今回は、入力パラメータをStringで渡し、進行度をIntegerで指定してUIを更新し、最終結果をLongで返すようにしています。
 */
public class HPDAsyncTask extends AsyncTask<Object, Integer, Long> {

	Context cContext;
	HPDialog progressDialog = null;// ロード中画面のプログレスダイアログ作成
	public Boolean isShowProgress;
//	public AlertDialog pDialog = null;	// 処理中ダイアログ	ProgressDialog	AlertDialog
	public int reqCode = 0;						//処理番号
	public CharSequence pdTitol;			//ProgressDialog のタイトルを設定
	public CharSequence pdMessage;			//ProgressDialog のメッセージを設定
	public CharSequence pdMessage_stok;			//ProgressDialog のメッセージを設定
	public int pdMaxVal = 0;					//ProgressDialog の最大値を設定 (水平の時)
	public int pdStartVal=0;					//ProgressDialog の初期値を設定 (水平の時)
	public int pdCoundtVal=0;					//ProgressDialog表示値
	public int pd2MaxVal;					//ProgressDialog の最大値を設定 (水平の時)
	public int pd2CoundtVal;					//ProgressDialog表示値
	public boolean dlogShow;

	/**
	 * コンストラクタ
	 */
	public HPDAsyncTask(Context cContext){
		final String TAG = "HPDAsyncTask[HPDAsyncTask]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			this.cContext = cContext;
			dbMsg="cContext=" + this.cContext ;/////////////////////////////////////
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}

	/**
	 * バッググラウンド処理の前処理（準備）
	 * UI Thread処理
	 */
	@Override
	protected void onPreExecute(){
		final String TAG = "onPreExecute";
		String dbMsg="[HPDAsyncTask]";
		try{
//			@SuppressWarnings({ "serial" })
//			Serializable Cancel_Listener = new HPDAsyncTask.CancelListener() {
//			@Override
//			public void canceled(DialogInterface _interface) {
//			cancel(true); // これをTrueにすることでキャンセルされ、onCancelledが呼び出される。
//			}
//			};
			progressDialog = HPDialog.newInstance("処理中", "しばらくお待ちください",100,10, cContext  );					//true, Cancel_Listener
			dbMsg="progressDialog=" + progressDialog ;/////////////////////////////////////
//			progressDialog.show((( Activity ) cContext).getFragmentManager(), "progress");                          //参照できなかった
			dbMsg +=",isPShow=" + progressDialog.isPShow() ;/////////////////////////////////////
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}


	/**
	 * バックグラウンド処理
	 */
	@Override
	protected Long doInBackground(Object... params) {		//0;reqCode, 1;pdTitol, 2;pdMessage,3;pdMaxVal  , 4;pd2MaxVal
		final String TAG = "doInBackground[HPDAsyncTask]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			Integer reqCode = (Integer) params[0] ;			//0;reqCode;0.次の処理
			dbMsg="ループ前：reqCode = " + reqCode;
	//		this.pd2CoundtVal=(Integer) params[4] ;			//4.処理カウント
			this.pd2MaxVal=(Integer) params[4] ;			// 4;pd2MaxVal
			dbMsg +=">>" + this.pd2CoundtVal + "/" + this.pd2MaxVal;
			progressDialog.ProgBar2.setMax(this.pd2MaxVal);
	//		progressDialog.ProgBar2.setProgress(this.pd2CoundtVal);
			dbMsg +=">>" + progressDialog.ProgBar2.getProgress() + "/" + progressDialog.ProgBar2.getMax();
			Integer setInt=(Integer) params[3] ;			//3;pdMaxVal;3.次のステップ数
			if( setInt != pdMaxVal ){
				pdMaxVal = setInt;
				progressDialog.progBar1.setMax(pdMaxVal);
				dbMsg +=")" +progressDialog. progBar1.getMax();
			}
			CharSequence setStr =(CharSequence) params[1];		//1;pdTitol;1.次の処理のタイトル
			dbMsg +=" , setStr = " + setStr;
			if(setStr != null ){
				if(! setStr.equals(pdTitol)){
					pdTitol = setStr;
					pdCoundtVal = 0;
				}
			}
			dbMsg +=" , Titol = " + pdTitol;
			setStr=(CharSequence) params[2];				//2;pdMessage;2.次の処理に渡すメッセージ
			if(setStr !=null ){
				if(! setStr.equals(pdMessage)){
					pdMessage = setStr;
					dbMsg +=",Message = " + pdMessage;
				}
			}
			this.pd2CoundtVal = 0;
			final int interval = 100;
			dlogShow = dlogMati( 10 , 10000 );					//ダイアログを表示するまで待つ  50ｍｓ 間隔で10,000回まで
//			msg = new Message();
//			msg.arg1=1;								//空ではsendMessageに入れない
//			handler.sendMessage(msg );
			change2ndText ();			//ProgBar2の表示値設定
			
			do{
				this.pd2CoundtVal++;
				dbMsg= "[ " + pd2CoundtVal + "/ " + pd2MaxVal +"]" ;		// + "/lStep = " + lStep + "=lEnd = " + lEnd + ",StepWidth = " + StepWidth;
				progressDialog.ProgBar2.setProgress(this.pd2CoundtVal);
				int step1 = pdMaxVal/10;
				if( step1 < 1 ){
					step1 =1;
				}
				dbMsg=  dbMsg + ">>"+ step1  ;		// + "/lStep = " + lStep + "=lEnd = " + lEnd + ",StepWidth = " + StepWidth;
				myLog(TAG,dbMsg);
				do{
					step1++;
					publishProgress( step1 );		//progressDialog.progBar1.setProgress(step1);
//					try{
//						Thread.sleep(1000);
//						if (isCancelled()){			 // Cancelされたとき
//							return 0L;
//						}
//						myLog(TAG,dbMsg);
//					} catch (InterruptedException e) {
//						Log.d("test", "Error");
//					}
					dbMsg=  "[" + progressDialog.progBar1.getProgress() + "/ " +  progressDialog.progBar1.getMax() +"]" ;		// + "/lStep = " + lStep + "=lEnd = " + lEnd + ",StepWidth = " + StepWidth;
				}while( progressDialog.progBar1.getProgress() < progressDialog.progBar1.getMax() ) ;
			}while( progressDialog.ProgBar2.getProgress() <  pd2MaxVal ) ;
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return 123L;
	}

	public boolean dlogMati( long wTime , int eStrp )  {										//ダイアログを表示するまで待つ
		boolean  retBool = false ;
		final String TAG = "dlogMati[HPDAsyncTask]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg += ",isPShow=" + progressDialog.isPShow() ;
			int eCount =0;
			do{
				eCount++;
				Thread.sleep(wTime);
				retBool = progressDialog.isPShow();
			}while(! retBool && eCount< eStrp);
			dbMsg +="eCount =  " + eCount + "/" + eStrp + ";"+ retBool ;	/////////////////////////////////////////////////////////////
			myLog(TAG,dbMsg);
			dbMsg=dbMsg +",progressDialog=" + progressDialog;/////////////////////////////////////
			if( progressDialog !=null){
				dbMsg=dbMsg +", isShowing="+ progressDialog.isPShow() ;
				myLog(TAG,dbMsg);
				if( ! progressDialog.isPShow() ){
					progressDialog.dismiss();
				}
			}
			myLog(TAG,dbMsg);
//ここから別クラスを呼ぶとjava.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare()
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return retBool;
	}
//初回/２回目=1220/80ｍｓ , 1210/80ｍｓ
	private Handler handler = new Handler() {

		public void handleMessage(Message msg ) {					//, Context cContext
			final String TAG = "handleMessage[HPDAsyncTask]";
			String dbMsg="開始";/////////////////////////////////////
			try{
				dbMsg="msg=" + msg ;/////////////////////////////////////
				dbMsg +=",cContext=" + HPDAsyncTask.this.cContext ;/////////////////////////////////////
		//		myLog(TAG,dbMsg);
		//		preReadLoop( HPDAsyncTask.this.cContext ) ;		//dataURIを読み込みながら欠けデータ確認		 getApplicationContext()
			}catch (Exception e) {
				myErrorLog(TAG,dbMsg +"で"+e.toString());
			}
		}
	};


	
	@Override
/** 
 * onProgressUpdate 
 * プログレスバー更新処理： UIスレッドで実行される doInBackground内でpublishProgressメソッドが呼ばれると、 
 * UIスレッド上でこのメソッドが呼ばれます。   このメソッドの引数の型はAsyncTaskの二つ目のパラメータです。  
 * メインスレッドで実行されます。非同期処理の進行状況をプログレスバーで 表示したい時などに使うことができます。*/  
	public void onProgressUpdate(Integer... values) {			//
		final String TAG = "onProgressUpdate[HPDAsyncTask]";
		String dbMsg="";
		try{
			dbMsg= "values="+values[0];///////////////////////////
			if(progressDialog != null){
		//		change1stProg ( (int)values[0] );		//		publishProgress( pdCoundtVal );		//MDST.pdCoundtVal
				int progress = values[0];
				dbMsg= "progress= " + progress;
				progressDialog.progBar1.setProgress(progress);
				dbMsg +=">> " +progressDialog. progBar1.getProgress();
				change1stText ();			//外部スレッドからprogBar1のUI操作
	//			myLog(TAG,dbMsg);
			}
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"；"+e.toString());
		}
	}

	public String _numberFormat = "%d/%d";
	public  NumberFormat _percentFormat = NumberFormat.getPercentInstance();
	 private void change1stText () {			//progBar1の値と％
		if(handler == null){
			handler = new Handler();
		}
		handler.post(new Runnable() {
			public void run() {
				final String TAG = "change1stText[HPDAsyncTask]";
				String dbMsg="";
				try {
				int progress = progressDialog.progBar1.getProgress();
				int max   = progressDialog.progBar1.getMax();
					dbMsg= progress + "/" + max;
				double parcent = (double) progress / (double) max;
				progressDialog.pgd_val_tv.setText(String.valueOf(progress));
				//	pgd_val_tv.setText(String.format(_numberFormat, progress, max));
				SpannableString tmp = new SpannableString(_percentFormat.format(parcent));
				dbMsg +=">>" + tmp;
		//		myLog(TAG,dbMsg);
				progressDialog.pgd_par_tv.setText(tmp);
				if(progress == 0){
					progressDialog.pgd_max_tv.setText(String.valueOf(max));
				}
				} catch (Exception e) {
					myErrorLog(TAG, dbMsg + "でエラー発生；"+e.toString());
				}
			}
		});
	}
	
	 private void change2ndText () {			//ProgBar2の表示値設定
		final String TAG = "change2ndText[HPDAsyncTask]";
		String dbMsg="";
		int progress = progressDialog.ProgBar2.getProgress()  + 1 ;
		dbMsg +="progress= " + progress;
		progressDialog.ProgBar2.setProgress(progress);
		int max   = progressDialog.ProgBar2.getMax();
		dbMsg +=" / " + max;
		
		int pEnd = HPDAsyncTask.this.pdMaxVal ;
		progressDialog.progBar1.setMax(pEnd);
		progressDialog.progBar1.setProgress(0);
		dbMsg += "[" + progressDialog.progBar1.getProgress() + "/" + progressDialog.progBar1.getMax() + "]" ;
		myLog(TAG,dbMsg);
		if(handler == null){
			handler = new Handler();
		}
		handler.post(new Runnable() {
			public void run() {
				final String TAG = "change2ndText[ZenkyokuList]";
				String dbMsg="";
				try {
					int progress = progressDialog.ProgBar2.getProgress();
					dbMsg= "[" + progress;
					int max   = progressDialog.ProgBar2.getMax();
					dbMsg +="/" + max + "]";
					progressDialog.pgd_val2_tv.setText(String.valueOf( progress));
					progressDialog.pgd_max2_tv.setText(String.valueOf( max));
					double parcent = (double) progress / (double) max;
					SpannableString tmp = new SpannableString(_percentFormat.format(parcent));
					progressDialog.pgd_par2_tv.setText(tmp);
					progressDialog.pgd_msg_tv.setText(pdMessage_stok);		//		pDialog.setMessage(pdMessage);
					myLog(TAG,dbMsg);
				} catch (Exception e) {
					myErrorLog(TAG, dbMsg + "でエラー発生；"+e.toString());
				}
			}
		});
	}

	 
	/**
	 * バックグラウンド処理が終わった後の処理（表示の更新）
	 */
	@Override
	protected void onPostExecute(Long result){
		final String TAG = "onPostExecute[HPDAsyncTask]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg=  "isPShow=" + progressDialog.isPShow() ;/////////////////////////////////////
			if (progressDialog.getShowsDialog())
				progressDialog.dismiss();
			dbMsg +=">>" + progressDialog.isPShow() ;/////////////////////////////////////

			if (result != null){
				Toast.makeText(cContext, result.toString(), Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(cContext, "NG", Toast.LENGTH_SHORT).show();
			}
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	}

	/**
	 * 中止された際の処理
	 */
	@Override
	protected void onCancelled(){
		final String TAG = "onCancelled[HPDAsyncTask]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg=  "isPShow=" + progressDialog.isPShow() ;/////////////////////////////////////
			if (progressDialog.getShowsDialog()){
				progressDialog.dismiss();
			}
			dbMsg +=">>" + progressDialog.isPShow() ;/////////////////////////////////////
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
	Toast.makeText(cContext, "Canceled", Toast.LENGTH_SHORT).show();
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