package com.hijiyam_koubou.marasongs;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

public class Alart3BT extends AlertDialog implements DialogInterface {
//http://d.hatena.ne.jp/abachibi/20100508/1273355363
	private AlertDialog alertDialog;
	private Context ｒContext;		//呼出し元のコンテキスト
	private long start=0;
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

	protected Alart3BT(Context context ,String dTitol ,String dMessage ,String PosiBTT ,String NeutBTT ,String NegaBTT
			, Boolean isInput ,int InType , Boolean isLists ,Boolean multiChoiceList , CharSequence[] listItem , String[] listID ) {
		//ダイアログタイトル,アラート文,PositiveButton,NeutralButton,NegativeButton,文字入力か,入力制限,リストか,複数選択リスト,リストアイテム,複数選択リストのアイテムID
		super(context);
		final String TAG = "Alart3BT[Alart3BT]";
		String dbMsg="";
		try{
			start=System.currentTimeMillis();

			
			Intent rData;
			ｒContext = context;		//呼出し元のコンテキスト
//			final Intent rData = new Intent();
//			Bundle extras = getIntent().getExtras();
//			rData.putExtras(extras);
			dTitol= dTitol ;		//extras.getString("dTitol");				//ダイアログタイトル
			dbMsg="dTitol=" + dTitol;/////////////////////////////////////////////////////////////////////////////////////
			dMessage = dMessage ;		//extras.getString("dMessage");			//アラート文
			Msg1= PosiBTT ;		//extras.getString("Msg1");					//PositiveButtonボタンのキーフェイス
			Msg2= NeutBTT ;		//extras.getString("Msg2");					//NeutralButtonボタン2のキーフェイス
			Msg3= NegaBTT ;		//extras.getString("Msg3");					//NegativeButtonボタン3のキーフェイス
			dbMsg=dbMsg+",dMessage="+dMessage+",Msg1="+Msg1+",Msg2="+Msg2+",Msg3="+Msg3;///////////////////////////////////////
			isIn = isInput ;		//extras.getBoolean("isIn");				//文字入力か
			InType = InType ;	//extras.getInt("InType");				//入力制限
			isList = isLists ;	//extras.getBoolean("isList");				//リストか
	//		motoN = extras.getString("motoN");		//元データなど
//			accName = extras.getString("accName");	//登録先名
//			accType = extras.getString("accType");	//登録先タイプ
//			gID = extras.getString("gID");			//書き込み先のグループID
			
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( ｒContext );		// アラートダイアログのタイトルを設定します 	getApplicationContext()	
			final EditText editView = new EditText(ｒContext);
			alertDialogBuilder.setTitle(dTitol);
			if(isIn){
				dbMsg=dbMsg+",isIn="+isIn;/////////////////////////////////////////////////////////////////////////////////////
				alertDialogBuilder.setView(editView);// アラートダイアログのメッセージを設定します
		//		editView.setHint(dMessage);;
				editView.setText(dMessage);
		//		editView.setInputType( InType);
			}else if(isList){
				dbMsg=dbMsg+",isList="+isList;/////////////////////////////////////////////////////////////////////////////////////
				listItems = listItem.clone();		//extras.getStringArray("listItems");				//リストアイテム
				dbMsg=dbMsg+ ";;" + listItems[0]  + "～" +listItems[listItems.length-1];/////////////////////////////////////////////////////////////////////////////////////
				if(listID != null){
					listIDs = listID.clone() ; 		//extras.getStringArray("listIDs");					//リストアイテムのID
					dbMsg="["+listIDs[0]+"]"+listItems[0]+"～["+listIDs[listIDs.length-1]+"]"+listItems[listItems.length-1];/////////////////////////////////////////////////////////////////////////////////////
				}
				multiChoice = multiChoiceList ;		//extras.getBoolean("multiChoice");		//複数選択リスト
				dbMsg=dbMsg+",multiChoice="+multiChoice;/////////////////////////////////////////////////////////////////////////////////////
				if(multiChoice){	//複数選択リスト
					List<String> retList = new ArrayList<String>();
					checkedItems = new boolean[listItems.length];
					alertDialogBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
						public void onClick(DialogInterface dialog, int which,boolean isChecked) {
//								if(isChecked){
//									String dbMsg="["+which+"]"+ listItems[which];/////////////////////////////////////////////////////////////////////////////////////
//									Log.i("multiChoice",dbMsg);
//								}
							checkedItems[which]=isChecked;
							}
						});
				}else{
					alertDialogBuilder.setItems(listItems, new OnClickListener() {			//DialogInterface.OnClicｋListener()
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String TAG ="onClick[リスト]";
							String dbMsg=motoN + "を";/////////////////////////////////////////////////////////////////////////////////////
//							if(listIDs != null){
//								dbMsg +="["+listIDs[which]+"]";/////////////////////////////////////////////////////////////////////////////////////
//							}
//							dbMsg +=listItems[which];/////////////////////////////////////////////////////////////////////////////////////
							Log.i(TAG,dbMsg);
//							rData.putExtra("retInt", which);
//							if(motoN != null){
//								rData.putExtra("key.motoN", motoN);				//元データなど
//							}
							dbMsg="[" +which +"]";/////////////////////////////////////////////////////////////////////////////////////
							String sentakuItem = (String) listItems[which];
							dbMsg +=sentakuItem +"を選択";/////////////////////////////////////////////////////////////////////////////////////
//							rData.putExtra("key.kekka", sentakuItem);	//選択されたアイテム
//							setResult(RESULT_OK, rData);		//RESULT_OK=1 (0xffffffff)
//							Alart3BT.this.finish();
							modori( which , sentakuItem, null, null, null);		// 戻し
				//			dialog.cancel();
				//			alertDialog.dismiss();
		//					closeMe();				//ダイアログとこのクラスを破棄
						}
					});
				}
			}else{
				dbMsg=dbMsg+",else";/////////////////////////////////////////////////////////////////////////////////////
				alertDialogBuilder.setMessage(dMessage);// アラートダイアログのメッセージを設定します
			}
			if(null != Msg1){
				alertDialogBuilder.setPositiveButton(Msg1,new DialogInterface.OnClickListener() {	// アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
					//	@Override
						public void onClick(DialogInterface dialog, int which) {
							if(isIn){
								modori( android.app.Activity.RESULT_OK ,  editView.getText().toString(), null, null, null);		// 戻し
//								rData.putExtra("key.motoN", motoN);							//元データなど
//								rData.putExtra("key.kekka", editView.getText().toString());	//編集結果
							}else if(isList){
								if(multiChoice){	//複数選択リスト
//									modori( android.app.Activity.RESULT_OK ,  null, listItems, listIDs, checkedItems);		// 戻し
//									rData.putExtra("retArray", checkedItems);
//									rData.putExtra("listItems", listItems);			//リストアイテム
//									rData.putExtra("listIDs", listIDs);				//リストアイテムのID
////									rData.putExtra("accName", accName);				//登録先名
////									rData.putExtra("accType", accType);				//登録先タイプ
////									rData.putExtra("grID", gID);						//書き込み先のグループID
//									setResult(RESULT_OK, rData);		//RESULT_OK=1 (0xffffffff)
								}else{
									retInt = 1;
									String retStr = editView.getText().toString();
//									if(motoN != null){
//										rData.putExtra("key.motoN", motoN);				//元データなど
//									}
//									rData.putExtra("key.retStr", retStr);
									modori( android.app.Activity.RESULT_OK ,  editView.getText().toString(), null, null, null);		// 戻し
								}
							}
//							setResult(RESULT_OK, rData);		//RESULT_OK=1 (0xffffffff)
//							closeMe();				//ダイアログとこのクラスを破棄
//							return;
						}
					});       // アラートダイアログの中立ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
			}
			if(null != Msg2){
				alertDialogBuilder.setNeutralButton(Msg2,new DialogInterface.OnClickListener() {
					//	@Override
						public void onClick(DialogInterface dialog, int which) {
							modori( 9 , null, null, null, null);		// 戻し
//							retInt = 9;
//							setResult(9, rData);
//							closeMe();				//ダイアログとこのクラスを破棄
//							return;
						}
					});// アラートダイアログの否定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
			}
			if(null != Msg3){
				alertDialogBuilder.setNegativeButton(Msg3,new DialogInterface.OnClickListener() {
				//	@Override
					public void onClick(DialogInterface dialog, int which) {
						modori( 3 , null, null, null, null);	// 戻し
//						retInt = 3;
//						setResult(RESULT_CANCELED, rData);		//RESULT_CANCELED=0 (0x00000000)
//						closeMe();				//ダイアログとこのクラスを破棄
//						return;
					}
				});
			}
			dbMsg=dbMsg+",setCancelable";/////////////////////////////////////////////////////////////////////////////////////
			alertDialogBuilder.setCancelable(true);// アラートダイアログのキャンセルが可能かどうかを設定します
			dbMsg=dbMsg+",show";/////////////////////////////////////////////////////////////////////////////////////
		//	alertDialogBuilder.show();
		//	dbMsg=dbMsg+",create";/////////////////////////////////////////////////////////////////////////////////////
			alertDialog = alertDialogBuilder.create();	// アラートダイアログを表示します
			alertDialog.setCanceledOnTouchOutside(false);	//背景をタップしてもダイアログを閉じない
			alertDialog.show();
			Log.i(TAG,dbMsg);
		} catch (Exception e) {
			Log.e(TAG, dbMsg + "で"+e);
		}
	}

	public void quitMe() {				//ダイアログとこのクラスを破棄
		final String TAG = "closeMe[Alart3BT]";
		String dbMsg="発生";
		try{
	//		if(alertDialog.isShowing()){
				alertDialog.dismiss();
	//		}
	//		Alart3BT.this.finish();
		} catch (Exception e) {
			Log.e(TAG,dbMsg + e);
		}

	}

	public void modori(int retInt , String retStr , CharSequence[] listItems ,String[] listIDs ,boolean[] checkedItems) {	// 戻し
		//選択されたボタン（リストならインデックス）、入力結果（選択されたアイテム）,リストアイテム,リストアイテムのID,選択リストの配列

		final String TAG = "modori[Alart3BT]";							//long seleID  ,, int hennkou, String seleItem
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			Intent data = new Intent();			// 返すデータ(Intent&Bundle)の作成
			Bundle bundle = new Bundle();
			
			if(retInt > 0){
				dbMsg +="retInt=" + retInt + "件" ;/////////////////////////////////////
				bundle.putInt("key.retInt", retInt);			//切り替え先
			}
			if(retStr != null){
				dbMsg +="retStr=" + retStr  ;/////////////////////////////////////
				bundle.putString("key.retStr", retStr);			//切り替え先
			}
			data.putExtras(bundle);
			quitMe();			//
			long end=System.currentTimeMillis();		// 終了時刻の取得
			start=System.currentTimeMillis();
			dbMsg=dbMsg +";"+ (int)((end - start)) + "m秒で終了";
			Log.i(TAG,dbMsg);
		}catch (Exception e) {
			Log.e(TAG,dbMsg + "で"+e.toString());
		}
	}

	
//	protected void onRestart() {
//		super.onRestart();
//		Log.i("onRestart","onRestartが[Alart3BT]で発生");
//	}

//	@Override
//	protected void onResume() {
//		super.onResume();
//		Log.i("onResume","onResumeが[Alart3BT]で発生");
//	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i("onStart","onStartが[Alart3BT]で発生");
	}

//	@Override
//	protected void onPause() {
//		super.onPause();
//		Log.i("onPause","onPauseが[Alart3BT]で発生");
//	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i("onStop","onStopが[Alart3BT]で発生");
	}

//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		final String TAG = "onDestroy[Alart3BT]";
//		String dbMsg="発生";
//		try{
//			Log.i(TAG,dbMsg);
//		} catch (Exception e) {
//			Log.e(TAG, dbMsg + "で"+e);
//		}
//	}


}
