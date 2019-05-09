package com.hijiyam_koubou.marasongs;

import java.io.File;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ItemLayout extends LinearLayout {							//Custom View
	OrgUtil ORGUT;						//自作関数集
	ImageView mIconView;			// アイコン
	TextView mTitleView;			// タイトル
	TextView mDescriptionView;		// 概要
	TextView noView;							//番号表示
	int imHight = 48;					//イメージビューの高さ
	int imWidth = 48;					//イメージビューの幅

	Context context;
	AttributeSet attrs;
	String sucssesPass;

	public ItemLayout(Context context, AttributeSet attrs) {					// , int reqCode
		super(context, attrs);													// , reqCode
		final String TAG = "ItemLayout[ItemLayout]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
	//		ORGUT = new OrgUtil();						//自作関数集
	//	myLog(TAG,dbMsg);
		} catch (Exception e) {		//汎用
			myErrorLog(TAG,dbMsg+"で"+e.toString());
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		final String TAG = "onFinishInflate[ItemLayout]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			mIconView = (ImageView) findViewById(R.id.iconView);						//mIconView = (ImageView) findViewById(R.id.lrow_image);
			dbMsg= "mIconView=" + mIconView;
			mTitleView = (TextView) findViewById(R.id.titleView);					//mTitleView = (TextView) findViewById(R.id.row_main_tv);
			mDescriptionView = (TextView) findViewById(R.id.descriptionView);		//mDescriptionView = (TextView) findViewById(R.id.row_sub_tv);
			noView = (TextView) findViewById(R.id.noView);							//番号表示
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {		//汎用
			myErrorLog(TAG,dbMsg+"で"+e.toString());
		}
	}

	public void bindView(CustomData item , int reqCode) {							//Item item
		final String TAG = "bindView[ItemLayout]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			mIconView.setImageResource(R.drawable.no_image);					//eListItem.icon		android.R.drawable.ic_dialog_alert
			dbMsg +=",title =" + item.textData_;
			mTitleView.setText(item.textData_);
			dbMsg +=",description =" + item.subData_;
			mDescriptionView.setText(item.subData_);
			dbMsg +=",reqCode =" + reqCode;
			switch(reqCode) {
			case MaraSonActivity.v_titol:
				mIconView.setVisibility(View.GONE);
				noView.setVisibility(View.VISIBLE);
				noView.setText(item.no_);
				break;
			default:
//			case MaraSonActivity.v_artist:													//...334
//			case MaraSonActivity.v_alubum:												//...340
				noView.setVisibility(View.GONE);
				mIconView.setVisibility(View.VISIBLE);
				dbMsg +=",imageUrl_ =" + item.imageUrl_;
				if( item.imageUrl_ != null ){
					ImageGetTask task = new ImageGetTask(mIconView);				//画像取得スレッド起動
					task.execute(item.imageUrl_ );
				}
				break;
			}
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
 * ViewHolderを使わないでListViewを高速化する		http://qiita.com/mofumofu3n/items/28f8be64d39b20e69552
 * **/
