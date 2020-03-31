package com.hijiyam_koubou.marasongs;

import android.app.Application;

import java.util.List;

/**
 * グローバル変数
 * **/
public class MyApp extends Application {

    private List<Item> mItems;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public List<Item> getList() {
        return mItems;
    }

    public void setList(List<Item> rtems) {
        mItems = rtems;
    }

}
