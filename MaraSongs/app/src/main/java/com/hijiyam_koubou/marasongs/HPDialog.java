package com.hijiyam_koubou.marasongs;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.NumberFormat;

public class HPDialog extends DialogFragment {
	public Context cContext;
	private ProgressDialog progressDialog;
	public  Dialog pDialog;
	public HPDialog dialogFragment;
	public boolean dlogShow;
	public static View view;
	public static Builder prg ;
	public TextView pgd_msg_tv ;
	public Handler handler1;
	public  ProgressBar progBar1;		 //メインプログレスバー
	public  TextView pgd_val_tv;
	public  TextView pgd_max_tv;
	public  TextView pgd_par_tv;
//	public Handler handler2;
	public  ProgressBar ProgBar2;		 //セカンドプログレスバー
	public  TextView pgd_val2_tv;
	public  TextView pgd_max2_tv;
	public  TextView pgd_par2_tv;
	public int dlogID = 0;
	public  String _numberFormat = "%d/%d";
	public   NumberFormat _percentFormat = NumberFormat.getPercentInstance();

	public void  onAttach(Context contextg) {
		super.onAttach(contextg);
		final String TAG = "onAttach";
		String dbMsg = "[HPDialog]";
		try {
			cContext = contextg;
			dbMsg = "context = " + this.cContext;
			if ( pDialog == null ) {
				pDialog = DualProgressDialog(this.cContext);
			}
			dbMsg = dbMsg + ", pDialog = " + pDialog;
			myLog(TAG , dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG , dbMsg + "で" + e.toString());
		}
	}

//	public HPDialog( Context cContext ){
//		final String TAG = "HPDialog[HPDialog]";
//		String dbMsg="起動";
//		try{
//			this.cContext = cContext ;
//			dbMsg="context = " + this.cContext;
//			if( pDialog ==null ){
//				pDialog = DualProgressDialog(this.cContext);
//			}
//			dbMsg= dbMsg +  ", pDialog = " + pDialog;
//			myLog(TAG,dbMsg );
//		}catch (Exception e) {
//			myErrorLog(TAG,dbMsg +"で"+e.toString());
//		}
//	}

	public static HPDialog newInstance(String title, String message, int max , int max2 , Context cContext) {			//static
		final String TAG = "newInstance";
		String dbMsg="[HPDialog]";
		HPDialog fragment = new HPDialog();
//		HPDialog fragment = HPDialog.this;			//  new HPDialog(cContext);
		try{
//			HPDialog.this.cContext = cContext ;
			dbMsg="context= " + cContext;
//			LayoutInflater factory = LayoutInflater.from(cContext);
//			dbMsg= dbMsg + ",factory=" + factory;
//			view = factory.inflate(R.layout.pd_log, null);
//			dbMsg= dbMsg  + ",view=" + view.getId();

			Bundle args = new Bundle();
			dbMsg="title=" + title;
			args.putString("title", title);
			dbMsg= dbMsg +",message=" + message;
			args.putString("message", message);
			dbMsg= dbMsg +",max=" + max;
			args.putInt("max", max);
			dbMsg= dbMsg +",max2=" + max2;
			args.putInt("max2", max2);
			fragment.setArguments(args);
//			fragment.show(activity.getFragmentManager(), "dialog_fragment");
//			dbMsg= dbMsg +",isVisible="+ fragment.isVisible();
			myLog(TAG,dbMsg );
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return fragment;
	}


	public Dialog DualProgressDialog (Context cContext) {
		final String TAG = "DualProgressDialog[HPDialog]";
		String dbMsg="開始";
		try {
			dbMsg="context=" + cContext;
			LayoutInflater factory = LayoutInflater.from(cContext);
			dbMsg= dbMsg + ",factory=" + factory;
			view = factory.inflate(R.layout.pd_log, null);
			dbMsg= dbMsg  + ",view=" + view.getId();
			pgd_msg_tv = (TextView) view.findViewById(R.id.pgd_msg_tv);
			progBar1 = (ProgressBar) view.findViewById(R.id.progBar1);		 //メインプログレスバー
			pgd_val_tv = (TextView) view.findViewById(R.id.pgd_val_tv);
			pgd_max_tv = (TextView) view.findViewById(R.id.pgd_max);
			pgd_par_tv = (TextView) view.findViewById(R.id.pgd_par_tv);
			dbMsg= dbMsg  + ",pgd_par_tv=" + pgd_par_tv.getId();
			_numberFormat = "%d/%d";
			_percentFormat = NumberFormat.getPercentInstance();
			_percentFormat.setMaximumFractionDigits(0);
			ProgBar2 = (ProgressBar) view.findViewById(R.id.ProgBar2);		 //セカンドプログレスバー
			pgd_val2_tv = (TextView) view.findViewById(R.id.pgd_val2_tv);
			pgd_max2_tv = (TextView) view.findViewById(R.id.pgd_max2);
			pgd_par2_tv = (TextView) view.findViewById(R.id.pgd_par2_tv);
			dbMsg= dbMsg  + ",pgd_par2_tv=" + pgd_par2_tv.getId();
			_percentFormat.setMaximumFractionDigits(0);
			prg = new AlertDialog.Builder(cContext);				//AlertDialog
			dbMsg= dbMsg + " , prg=" + prg;
			prg.setView(view);
			dbMsg= dbMsg + " >>" + prg;
			pDialog =  prg.create();			//(ProgressDialog)
			dbMsg= dbMsg + " pDialog=" + pDialog;
	//		pd2CoundtVal = 0;
//			myLog(TAG, dbMsg);
			pDialog.show();				//plogTaskから呼ぶと通る
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg + "でエラー発生；"+e.toString());
		}
		return pDialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle safedInstanceState) {
		final String TAG = "onCreateDialog[HPDialog]";
		String dbMsg="起動";
		try{
			dbMsg="Bundle=" + safedInstanceState;
			String title = getArguments().getString("title");
			dbMsg= dbMsg + "title=" + title;
			String message = getArguments().getString("message");
			dbMsg= dbMsg + ",message=" + message;
			int max = getArguments().getInt("max");
			dbMsg= dbMsg + ",max=" + max;
			int max2 = getArguments().getInt("max2");
			dbMsg= dbMsg + ",max2=" + max2;
//			pgd_title_tv.setText(title);
			pgd_msg_tv.setText(message);
			progBar1.setMax(max);
			pgd_max_tv.setText(String.valueOf(progBar1.getMax()));
			ProgBar2.setMax(max2);
			pgd_max2_tv.setText(String.valueOf(ProgBar2.getMax()));

			pDialog.show();				//plogTaskから呼ぶと通る

			myLog(TAG,dbMsg );
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return this.pDialog;
	}

	/**
	 * プログレス表示
	 */
	public Boolean isPShow() {
		final String TAG = "isPShow[HPDialog]";
		String dbMsg="起動";
		Boolean retBool=false ;
		try{
			dbMsg="pDialog=" + pDialog;
			if( pDialog != null ){
	//			retBool = progressDialog.isShowing();
				retBool = pDialog.isShowing();
				dbMsg= dbMsg + ",retBool=" + retBool;
			}
	//		myLog(TAG,dbMsg );
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return retBool;
	}

	/**
	 * プログレス値の取得
	 */
	public int getProgress() {
		final String TAG = "getProgress[HPDialog]";
		String dbMsg="起動";
		int np=0 ;
		try{
			np = progBar1.getProgress();					//progressDialog.getProgress();
			dbMsg="progBar1=" + np;
	//		myLog(TAG,dbMsg );
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return np;
	}

	public int getPMax() {
		final String TAG = "getProgress[HPDialog]";
		String dbMsg="起動";
		int mp=0 ;
		try{
			mp = progBar1.getMax();
			dbMsg="progBar1Max=" + mp;
	//		myLog(TAG,dbMsg );
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
		}
		return mp;
	}

	/**
	 * プログレス値のセット
	 */
	private final Handler handler = new Handler();
	public void setProgress(int value) {
		final String TAG = "setProgress[HPDialog]";
		String dbMsg="起動";
//		HPDialog fragment = new HPDialog();
		try{
			dbMsg= value + ")" ;
			progBar1.setProgress(value);
			myLog(TAG,dbMsg);

			new Thread(new Runnable(){
				@Override
				public void run() {
					handler.post(new Runnable() {
						public void run() {
							final String TAG = "run[HPDialog.setProgress]";
							String dbMsg="";
							try{
								int progress = progBar1.getProgress();
								dbMsg= dbMsg + ";" + progress;
								int max   = progBar1.getMax();
								dbMsg= dbMsg + "/" + max;
								double parcent = (double) progress / (double) max;
								dbMsg= dbMsg + "=" + parcent;
								pgd_val_tv.setText(String.valueOf(progress));
								SpannableString tmp = new SpannableString(_percentFormat.format(parcent));
								dbMsg= dbMsg + ";" + tmp;
								pgd_par_tv.setText(tmp);
//								if(progress == 0){
//										pgd_max_tv.setText(String.valueOf(max));
//								}
								if(progress >= max){
									return;
								}
			//					myLog(TAG,dbMsg);
							} catch (Exception e) {
								myErrorLog(TAG, dbMsg + "でエラー発生；"+e.toString());
							}
						}
					});
				}
			}).start();

//			int progress = progBar1.getProgress();
//			dbMsg= dbMsg + ";" + progress;
//			int max   = progBar1.getMax();
//			dbMsg= dbMsg + "/" + max;
//			double parcent = (double) progress / (double) max;
//			dbMsg= dbMsg + "=" + parcent;
//			pgd_val_tv.setText(String.valueOf(progress));
//			SpannableString tmp = new SpannableString(_percentFormat.format(parcent));
//			dbMsg= dbMsg + ";" + tmp;
//			pgd_par_tv.setText(tmp);

		}catch (Exception e) {
			myErrorLog(TAG,dbMsg +"で"+e.toString());
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
//http://dangoya.jp/?p=277