package com.hijiyam_koubou.marasongs;

import android.app.Application;

import java.util.List;

/**
 * グローバル変数
 *
 *https://akira-watson.com/android/global-val.html
 * **/
public class MyApp extends Application {

    private List<Item> mItems;

    @Override
    public void onCreate() {
        super.onCreate();
        final String TAG = "onCreate";
        String dbMsg = "[MyApp]";
        try{
            myLog(TAG, dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
    }

    public List<Item> getItemList() {
        final String TAG = "getItemList";
        String dbMsg = "[MyApp]";
        try{
            dbMsg += "、mItems=" + mItems.size() + "件";
//            myLog(TAG, dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
        return mItems;
    }

    public void setItemList(List<Item> rtems) {
        final String TAG = "setList";
        String dbMsg = "[MyApp]";
        try{
            dbMsg += "、rtems=" + rtems.size() + "件";
            mItems = rtems;
            myLog(TAG, dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG ,  dbMsg + "で" + e);
        }
    }
    //////////////////////
    public static void myLog(String TAG , String dbMsg) {
        MyUtil MyUtil = new MyUtil();
        MyUtil.myLog(TAG , dbMsg);
    }

    public static void myErrorLog(String TAG , String dbMsg) {
        MyUtil MyUtil = new MyUtil();
        MyUtil.myErrorLog(TAG , dbMsg);
    }

}
