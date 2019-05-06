package com.hijiyam_koubou.marasongs;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
 

import java.util.ArrayList;
 
/**
 * Created by systemkd on 2015/02/28.
 * ToolbarでSpinnerを利用する	http://systemkd.blogspot.jp/2015/03/androidtoolbarspinner.html
 */
public class ExploreSpinnerAdapter extends BaseAdapter {
	 
	private Activity mActivity;
	 
	public ExploreSpinnerAdapter(Activity activity) {
		final String TAG = "ExploreSpinnerAdapter[ExploreSpinnerAdapter]";
		String dbMsg= "";/////////////////////////////////////
		try{
			dbMsg= " activity=" +  activity;
			mActivity = activity;
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}
	 
	private ArrayList<String> mItems = new ArrayList<>();
	 
	public void addItem(String msg) {
		final String TAG = "addItem[ExploreSpinnerAdapter]";
		String dbMsg= "";/////////////////////////////////////
		try{
			dbMsg= " msg=" +  msg;
			mItems.add(msg);
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}
	 
	@Override
	public int getCount() {
		final String TAG = "getCount[ExploreSpinnerAdapter]";
		String dbMsg= "";/////////////////////////////////////
		try{
			dbMsg= "size=" + mItems.size();
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return mItems.size();
	}
	 
	@Override
	public Object getItem(int position) {
		final String TAG = "getItem[ExploreSpinnerAdapter]";
		String dbMsg= "";/////////////////////////////////////
		try{
			dbMsg= "position=" + position;
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return mItems.get(position);
	}
	 
	@Override
	public long getItemId(int position) {
		final String TAG = "getItemId[ExploreSpinnerAdapter]";
		String dbMsg= "";/////////////////////////////////////
		try{
			dbMsg= "position=" + position;
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return position;
	}
	 
	@Override
	public View getDropDownView(int position, View view, ViewGroup parent) {
		final String TAG = "getDropDownView[ExploreSpinnerAdapter]";
		String dbMsg= "";/////////////////////////////////////
		try{
			dbMsg= "position=" + position;
			if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
				view = mActivity.getLayoutInflater().inflate(R.layout.explore_spinner_item_dropdown,
				parent, false);
				view.setTag("DROPDOWN");
				}
				 
				TextView textView = (TextView)view.findViewById(android.R.id.text1);
				textView.setText(getTitle(position));
				myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return view;
	}
	 
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final String TAG = "getView[ExploreSpinnerAdapter]";
		String dbMsg= "";/////////////////////////////////////
		try{
			dbMsg= "position=" + position;
			if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
				view = mActivity.getLayoutInflater().inflate(
				R.layout.explore_spinner_item_actionbar,
				parent, false);
				view.setTag("NON_DROPDOWN");
				}
				TextView textView = (TextView) view.findViewById(android.R.id.text1);
				textView.setText(getTitle(position));
				myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return view;
	}
	 
	private String getTitle(int position) {
		final String TAG = "getTitle[ExploreSpinnerAdapter]";
		String dbMsg= "";/////////////////////////////////////
		try{
			dbMsg= "position=" + position;
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return position >= 0 && position < mItems.size() ? mItems.get(position) : "";
	}
	 
	@Override
	public int getItemViewType(int position) {
		final String TAG = "getViewTypeCount[ExploreSpinnerAdapter]";
		String dbMsg= "";/////////////////////////////////////
		try{
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return 0;
	}
	 
	@Override
	public int getViewTypeCount() {
		final String TAG = "getViewTypeCount[ExploreSpinnerAdapter]";
		String dbMsg= "";/////////////////////////////////////
		try{
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return 1;
	}
	 
	@Override
	public boolean areAllItemsEnabled() {
		final String TAG = "areAllItemsEnabled[ExploreSpinnerAdapter]";
		String dbMsg= "";/////////////////////////////////////
		try{
			myLog(TAG, dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
		return false;
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