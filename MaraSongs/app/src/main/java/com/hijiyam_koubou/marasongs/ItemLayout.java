package com.hijiyam_koubou.marasongs;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.transition.Visibility;

import java.io.File;

public class ItemLayout extends LinearLayout {							//Custom View
	public OrgUtil ORGUT;						//自作関数集
	public ImageView mIconView;			// アイコン
	public Button playBt;
	public Button pouseBt;
	public TextView mTitleView;			// タイトル
	public TextView mDescriptionView;		// 概要
	public TextView noView;							//番号表示
	public int imHight = 48;					//イメージビューの高さ
	public int imWidth = 48;					//イメージビューの幅

	Context context;
	AttributeSet attrs;
	String sucssesPass;

	public ItemLayout(Context rContext, AttributeSet attrs) {					// , int reqCode
		super(rContext, attrs);													// , reqCode
		final String TAG = "ItemLayout";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			this.context =rContext;
	//		ORGUT = new OrgUtil();						//自作関数集
	//	myLog(TAG,dbMsg);
		} catch (Exception e) {		//汎用
			myErrorLog(TAG,dbMsg+"で"+e.toString());
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		final String TAG = "onFinishInflate";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			mIconView = findViewById(R.id.iconView);						//mIconView = (ImageView) findViewById(R.id.lrow_image);
			pouseBt = findViewById(R.id.pouseBt);
			playBt = findViewById(R.id.playBt);
			mIconView = findViewById(R.id.iconView);						//mIconView = (ImageView) findViewById(R.id.lrow_image);
			dbMsg= "mIconView=" + mIconView;
			mTitleView = findViewById(R.id.titleView);					//mTitleView = (TextView) findViewById(R.id.row_main_tv);
			mDescriptionView = findViewById(R.id.descriptionView);		//mDescriptionView = (TextView) findViewById(R.id.row_sub_tv);
			noView = findViewById(R.id.noView);							//番号表示
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {		//汎用
			myErrorLog(TAG,dbMsg+"で"+e.toString());
		}
	}

	public void bindView(CustomData item , int reqCode,Context context) {							//Item item
		final String TAG = "bindView";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			mIconView.setImageResource(R.drawable.no_image);					//eListItem.icon		android.R.drawable.ic_dialog_alert
			pouseBt.setVisibility(View.GONE);
			playBt.setVisibility(View.GONE);

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
				String dataUri = item.getDataUri();
				dbMsg +=",dataUri =" + dataUri;
				String albumId = item.getAlbum_id();
				dbMsg +=",albumId =" + albumId;
				switch(reqCode) {
					case MyConstants.v_play_list:
					case MyConstants.v_alubum:
						String firstUri = item.getFirstUri();
						dbMsg +=",firstUri =" + firstUri;
						OrgUtil.setArt(context,mIconView,firstUri,albumId);
						break;
					default:
						OrgUtil.setArt(context,mIconView,dataUri,albumId);
						break;
				}
				break;
			}
			myLog(TAG,dbMsg);
		} catch (Exception e) {		//汎用
			myErrorLog(TAG,"[ItemLayout]"+ dbMsg+"で"+e.toString());
		}
	}

	///////////////////////////////////////////////////////////////////////////////////
	public static void myLog(String TAG , String dbMsg) {
		MyUtil MyUtil = new MyUtil();
		MyUtil.myLog(TAG , "[ItemLayout]"+ dbMsg);
	}

	public static void myErrorLog(String TAG , String dbMsg) {
		MyUtil MyUtil = new MyUtil();
		MyUtil.myErrorLog(TAG , "[ItemLayout]"+ dbMsg);
	}

}


/**
 * ViewHolderを使わないでListViewを高速化する		http://qiita.com/mofumofu3n/items/28f8be64d39b20e69552
 * **/
