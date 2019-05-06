package com.hijiyam_koubou.marasongs;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

/**
 * A {@link android.preference.Preference} that displays a number picker as a dialog.
 * http://stackoverflow.com/questions/20758986/android-preferenceactivity-dialog-with-number-picker
 */
public class NumberPickerPreference extends DialogPreference {

	private static final int DEFAULT_MAX = 100;
	private static final int DEFAULT_MIN = 0;
	// enable or disable the 'circular behavior'
	public static final boolean WRAP_SELECTOR_WHEEL = true;

	private NumberPicker picker;
	public int  step=5;
	public int  maxValue = -1;
	public int  minValue = -1;
	public int  defaultValue = 50;
	public int value = 0;
	public String dMessege = "";
	public String[] valueSet;

	public NumberPickerPreference(Context context, AttributeSet attrs) {
	super(context, attrs);
		final String TAG = "NumberPickerPreference[NumberPickerPreference]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg="context=" + context;
			dbMsg= dbMsg + ",attrs=" + attrs;
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
	super(context, attrs, defStyleAttr);
		final String TAG = "NumberPickerPreference2[NumberPickerPreference]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg="context=" + context;
			dbMsg= dbMsg + ",attrs=" + attrs;
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	@Override
	protected View onCreateDialogView() {
		FrameLayout dialogView = null;
		final String TAG = "onCreateDialogView[NumberPickerPreference]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					layoutParams.gravity = Gravity.CENTER;
					picker = new NumberPicker(getContext());
					picker.setLayoutParams(layoutParams);
			dialogView = new FrameLayout(getContext());
			dialogView.addView(picker);
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return dialogView;
	}

	@Override
	protected void onBindDialogView(View view) {
	super.onBindDialogView(view);
		final String TAG = "onBindDialogView[NumberPickerPreference]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg= minValue + "～" + maxValue;
			if( minValue == -1 ){
				minValue = DEFAULT_MIN;
			}
			if( maxValue == -1 ){
				maxValue = DEFAULT_MAX;
			}
			dbMsg= dbMsg + ">>" + minValue + "～" + maxValue;
			picker.setMinValue(minValue);
			picker.setWrapSelectorWheel(WRAP_SELECTOR_WHEEL);
			dbMsg= dbMsg +";defaultValue=" + defaultValue;									// ";getValue=" + getValue();
	//		picker.setValue(defaultValue);
			dbMsg= dbMsg + ";step=" + step;
			valueSet = new String[step + 1];
			int haba = (maxValue - minValue) / step;
			dbMsg= dbMsg +";haba=" + haba;									// ";getValue=" + getValue();
			valueSet[0] = String.valueOf(minValue);
			int selCount= 0;
			for (int i = 1; i <= step; i ++) {
				dbMsg= dbMsg + "," + i;
				int iVar = haba * i;
				valueSet[i] = String.valueOf(iVar);			//getString(format, i);
				dbMsg= dbMsg + "=" + valueSet[i];
				if(iVar <= defaultValue){
					selCount = i;
				}
			}
	//		valueSet[valueSet.length] = String.valueOf(maxValue);
			dbMsg= dbMsg + ">>valueSet=" + valueSet.length + "個";
			picker.setMaxValue(valueSet.length - 1);
			picker.setDisplayedValues(valueSet);
			picker.setValue(selCount);									//☆値ではなくインデックスでセレクトされる
			picker.setSelected(false);
			//picker.setRange(1, 7);
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		final String TAG = "onDialogClosed[NumberPickerPreference]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg="positiveResult=" + positiveResult;
			if (positiveResult) {
				picker.clearFocus();
				int newValue = picker.getValue();
				dbMsg= dbMsg + ",newValue=" + newValue;
				if (callChangeListener(newValue)) {
					setValue(retValue(newValue));
				}
			}
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		final String TAG = "onGetDefaultValue[NumberPickerPreference]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg="TypedArray=" + a;
			dbMsg= dbMsg + ",index=" + index;
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return a.getInt(index, DEFAULT_MIN);
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
		final String TAG = "onSetInitialValue[NumberPickerPreference]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg="restorePersistedValue=" + restorePersistedValue;
			dbMsg= dbMsg + ",defaultValue=" + defaultValue;
			if( defaultValue == null ){
				defaultValue = String.valueOf(this.defaultValue);
			} else{
				if( minValue == -1 ){
					minValue = DEFAULT_MIN;
				}
				setValue(restorePersistedValue ? getPersistedInt(minValue) : (Integer) defaultValue);
			}
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	public void setParamea(int min ,int max,int step,int defaultV ) {			//, String msg
		final String TAG = "setParamea[NumberPickerPreference]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg=min + "～" + max + "、step=" + step;
			this.minValue = min;
	//		persistInt(this.DEFAULT_MIN);
			this.maxValue = max;
			this.step = step;
			this.defaultValue = defaultV;
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	public void setValue(int value) {
		final String TAG = "setValue[NumberPickerPreference]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg="value=" + value;
			this.value = value;
		//	persistInt(this.value);
			dbMsg=">>" + this.value;
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	public int getValue() {
		final String TAG = "getValue[NumberPickerPreference]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg="value=" + this.value;
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return this.value;
	}

 	public int retValue(int index) {
 		int retInt =0;
		final String TAG = "retValue[NumberPickerPreference]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg="index=" + index;
			retInt = Integer.valueOf(valueSet[index]);
			dbMsg= dbMsg + "=" + index;
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return retInt;
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