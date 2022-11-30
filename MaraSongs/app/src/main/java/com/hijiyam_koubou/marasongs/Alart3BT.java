package com.hijiyam_koubou.marasongs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class Alart3BT extends AlertDialog implements DialogInterface {
	private AlertDialog alertDialog;
	private Context ｒContext;		//呼出し元のコンテキスト
	public OrgUtil ORGUT;						//自作関数集

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
	private int retInt;

//	@Override protected void onCreate(Bundle icicle) {
//		super.onCreate(icicle);
//		final String TAG = "onCreate";
//		String dbMsg = "[Alart3BT]";
//		try{
//			dbMsg += ORGUT.nowTime(true,true,true)+dbMsg;
//			Bundle extras = ｒContext.getIntent().getExtras();
//
//
//
//			myLog(TAG, dbMsg);
//		}catch (Exception e) {
//			myErrorLog(TAG ,  dbMsg + "で" + e);
//		}
//	}

	/****
	 * パラメータを受け取ってダイアログ表示
	 * @param context	クラス名.this
	 * @param dTitol　ダイアログタイトル
	 * @param dMessage	アラート文
	 * @param PosiBTT	PositiveButtonボタンのキーフェイス: OK
	 * @param NeutBTT	NeutralButtonボタン2のキーフェイス: オプション
	 * @param NegaBTT	NegativeButtonボタン3のキーフェイス: キャンセル
	 * @param isInput	文字入力か
	 * @param InType	入力の型制限
	 * @param isLists	リストか
	 * @param multiChoiceList	複数選択リストか
	 * @param listItem	リストアイテム
	 * @param listID	複数選択リストの戻し用ID
	 */
	protected Alart3BT(Context context ,String dTitol ,String dMessage ,
					   String PosiBTT ,String NeutBTT ,String NegaBTT ,
					   Boolean isInput ,int InType ,
					   Boolean isLists ,Boolean multiChoiceList ,
					   CharSequence[] listItem , String[] listID ) {
		super(context);
		final String TAG = "Alart3BT";
		String dbMsg = "[Alart3BT]";
		try{
			start=System.currentTimeMillis();
			Intent rData;
			ｒContext = context;		//呼出し元のコンテキスト
			dTitol = dTitol ;		//extras.getString("dTitol");				//ダイアログタイトル
			dbMsg += "dTitol=" + dTitol;/////////////////////////////////////////////////////////////////////////////////////
			dMessage = dMessage ;		//extras.getString("dMessage");			//アラート文
			Msg1= PosiBTT ;		//extras.getString("Msg1");					//PositiveButtonボタンのキーフェイス
			Msg2= NeutBTT ;		//extras.getString("Msg2");					//NeutralButtonボタン2のキーフェイス
			Msg3= NegaBTT ;		//extras.getString("Msg3");					//NegativeButtonボタン3のキーフェイス
			dbMsg += ",dMessage="+dMessage+",Msg1="+Msg1+",Msg2="+Msg2+",Msg3="+Msg3;///////////////////////////////////////
			isIn = isInput ;		//extras.getBoolean("isIn");				//文字入力か
			InType = InType ;	//extras.getInt("InType");				//入力制限
			isList = isLists ;	//extras.getBoolean("isList");				//リストか
			
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( ｒContext );		// アラートダイアログのタイトルを設定します 	getApplicationContext()	
			final EditText editView = new EditText(ｒContext);
			alertDialogBuilder.setTitle(dTitol);
			if(isIn){
				dbMsg += ",isIn="+isIn;
				alertDialogBuilder.setView(editView);// アラートダイアログのメッセージを設定します
		//		editView.setHint(dMessage);;
				editView.setText(dMessage);
		//		editView.setInputType( InType);
			}else if(isList){
				dbMsg += ",isList="+isList;
				listItems = listItem.clone();		//extras.getStringArray("listItems");				//リストアイテム
				dbMsg +=  ":" + listItems[0]  + "～" +listItems[listItems.length-1];
				if(listID != null){
					listIDs = listID.clone() ; 		//extras.getStringArray("listIDs");					//リストアイテムのID
					dbMsg += "["+listIDs[0]+"]"+listItems[0]+"～["+listIDs[listIDs.length-1]+"]"+listItems[listItems.length-1] + "件";
				}
				multiChoice = multiChoiceList ;		//extras.getBoolean("multiChoice");		//複数選択リスト
				dbMsg += ",multiChoice="+multiChoice;
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
							String TAG = "onClick";
							String dbMsg = "onClick[Alart3BT.リスト]" + motoN + "を";
//							if(listIDs != null){
//								dbMsg +="["+listIDs[which]+"]";/////////////////////////////////////////////////////////////////////////////////////
//							}
//							dbMsg +=listItems[which];/////////////////////////////////////////////////////////////////////////////////////
//							rData.putExtra("retInt", which);
//							if(motoN != null){
//								rData.putExtra("key.motoN", motoN);				//元データなど
//							}
							dbMsg += "[" +which +"]";
							String sentakuItem = (String) listItems[which];
							dbMsg += sentakuItem +"を選択";/////////////////////////////////////////////////////////////////////////////////////
//							rData.putExtra("key.kekka", sentakuItem);	//選択されたアイテム
//							setResult(RESULT_OK, rData);		//RESULT_OK=1 (0xffffffff)
//							Alart3BT.this.finish();
							myLog(TAG, dbMsg);
							modori( which , sentakuItem, null, null, null);		// 戻し
						}
					});
				}
			}else{
				dbMsg += ",else";
				alertDialogBuilder.setMessage(dMessage);// アラートダイアログのメッセージを設定します
			}
			if(null != Msg1){		//PositiveButtonボタン
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
						}
					});
			}
			if(null != Msg2){      // アラートダイアログの中立ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
				alertDialogBuilder.setNeutralButton(Msg2,new DialogInterface.OnClickListener() {
					//	@Override
						public void onClick(DialogInterface dialog, int which) {
							modori( 9 , null, null, null, null);		// 戻し
						}
					});// アラートダイアログの否定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
			}
			if(null != Msg3){			//NegativeButtonボタン
				alertDialogBuilder.setNegativeButton(Msg3,new DialogInterface.OnClickListener() {
				//	@Override
					public void onClick(DialogInterface dialog, int which) {
						modori( 3 , null, null, null, null);	// 戻し
					}
				});
			}
			dbMsg += ",setCancelable";/////////////////////////////////////////////////////////////////////////////////////
			alertDialogBuilder.setCancelable(true);// アラートダイアログのキャンセルが可能かどうかを設定します
			dbMsg += ",show";/////////////////////////////////////////////////////////////////////////////////////
			alertDialog = alertDialogBuilder.create();	// アラートダイアログを表示します
			alertDialog.setCanceledOnTouchOutside(false);	//背景をタップしてもダイアログを閉じない
			alertDialog.show();
			long end=System.currentTimeMillis();		// 終了時刻の取得
			dbMsg += ";"+ (int)((end - start)) + "m秒で終了";
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * このクラスからの戻り
	 * @param retInt	選択されたボタン（リストならインデックス）
	 * @param retStr　入力結果（選択されたアイテム）
	 * @param listItems	リストアイテム
	 * @param listIDs	リストアイテムのID
	 * @param checkedItems	選択リストの配列
	 */
	public void modori(int retInt , String retStr , CharSequence[] listItems ,String[] listIDs ,boolean[] checkedItems) {	// 戻し
		//、,,,

		final String TAG = "modori[Alart3BT]";							//long seleID  ,, int hennkou, String seleItem
		String dbMsg = "[Alart3BT]";
		try{
			Intent data = new Intent();			// 返すデータ(Intent&Bundle)の作成
			Bundle bundle = new Bundle();
			
			if(retInt > 0){
				dbMsg += "retInt=" + retInt + "件" ;/////////////////////////////////////
				bundle.putInt("key.retInt", retInt);			//切り替え先
			}
			if(retStr != null){
				dbMsg += "retStr=" + retStr  ;/////////////////////////////////////
				bundle.putString("key.retStr", retStr);			//切り替え先
			}
			data.putExtras(bundle);
			long end = System.currentTimeMillis();		// 終了時刻の取得
			start = System.currentTimeMillis();
			dbMsg += ";"+ (int)((end - start)) + "m秒で終了";
			myLog(TAG, dbMsg);
			quitMe();
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	/**
	 * ダイアログを閉じてこのクラスを破棄
	 * */
	public void quitMe() {
		final String TAG = "quitMe";
		String dbMsg = "[Alart3BT]";
		try{
			//		if(alertDialog.isShowing()){
			alertDialog.dismiss();
			//		}
			//		Alart3BT.this.finish();
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		final String TAG = "onStart";
		String dbMsg = "[Alart3BT]";
		try{
			dbMsg += ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}


	@Override
	protected void onStop() {
		super.onStop();
		final String TAG = "onStop";
		String dbMsg = "[Alart3BT]";
		try{
			dbMsg += ORGUT.nowTime(true,true,true)+dbMsg;/////////////////////////////////////
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}
//////////////////////////////////////////////////////////////////////////
	public static void myLog(String TAG , String dbMsg) {
	MyUtil MyUtil = new MyUtil();
	MyUtil.myLog(TAG , dbMsg);
}

	public static void myErrorLog(String TAG , String dbMsg) {
		MyUtil MyUtil = new MyUtil();
		MyUtil.myErrorLog(TAG , dbMsg);
	}

}
