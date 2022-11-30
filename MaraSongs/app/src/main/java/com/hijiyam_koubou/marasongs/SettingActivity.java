package com.hijiyam_koubou.marasongs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.Locale;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {
    public OrgUtil ORGUT;		//自作関数集 
    public String PREFS_NAME = "defaults";
    MyPreferences myPreferences;
//	public PreferenceManager mPreferenceManager;

    public Locale locale;	// アプリで使用されているロケール情報を取得
    public Context rContext ;							//Context
    public SharedPreferences sharedPref;
    public SharedPreferences.Editor myEditor;
//    public String myPreferences.pref_apiLv = "33";							//APIレベル
//    public int myPreferences.pref_sonota_vercord;				//このアプリのバージョンコード
//
//    //	public String pref_compBunki = null;	 //"40";			//コンピレーション設定[%]
//    public String myPreferences.pref_gyapless = null;			//クロスフェード時間
//    public boolean myPreferences.pref_list_simple = false;				//シンプルなリスト表示（サムネールなど省略）
//    public boolean myPreferences.pref_pb_bgc = true;				//プレイヤーの背景	true＝Black"	http://techbooster.jpn.org/andriod/ui/10152/
//
//    //	public String pref_artist_bunnri = null;			//"100";		//アーティストリストを分離する曲数
//    public String myPreferences.pref_saikin_tuika = null;			//"7";			//最近追加リストのデフォルト日数
//    public String myPreferences.pref_saikin_sisei = null;		//最近再生加リストのデフォルト枚数
//    public String myPreferences.pref_rundam_list_size =null;	//ランダム再生リストアップ曲数
//
//    public boolean myPreferences.pref_lockscreen =true;				//ロックスクリーンプレイヤー</string>
//    public boolean myPreferences.pref_notifplayer =true;				//ノティフィケーションプレイヤー</string>
//    public boolean pref_cyakusinn_fukki=true;		//終話後に自動再生
//    public boolean myPreferences.pref_bt_renkei =true;				//Bluetoothの接続に連携して一時停止/再開
//
//    public boolean pref_reset = false;					//設定を初期化
//    public boolean pref_listup_reset = false;			//調整リストを初期化
//
//    public String saisei_fname =null;				//再生中のファイル名
//    public String myPreferences.pref_saisei_jikan ="0";			//再開時間		Integer
//    public String myPreferences.pref_saisei_nagasa  ="0";		//再生時間
//    public String myPreferences.pref_zenkai_saiseKyoku ="0";			//前回の連続再生曲数
//    public String myPreferences.pref_zenkai_saiseijikann ="0";			//前回の連続再生時間
//
//    public int repeatType;							//リピート再生の種類
//    public boolean rp_pp;							//2点間リピート中
//    public String pp_start;							//リピート区間開始点
//    public String pp_end;								//リピート区間終了点
//    public String myPreferences.pref_file_in ="";		//内蔵メモリ
//    public String myPreferences.pref_file_ex="";		//メモリーカード
//    public String myPreferences.pref_file_wr="";		//設定保存フォルダ
//    public String myPreferences.pref_commmn_music="";		//共通音楽フォルダ
//    public String myPreferences.pref_file_kyoku="0";		//総曲数
//    public String pref_file_album="0";		//総アルバム数
//    public String myPreferences.pref_file_saisinn="";	//最新更新日
//    public int pref_mIndex;
//    public String myPreferences.pref_data_url = "";
//    public String myPreferences.myPreferences.nowList_id;				//再生中のプレイリストID	playListID
//    public String myPreferences.nowList;					//再生中のプレイリスト名	playlistNAME
//    public int list_max = -1;			///再生中のプレイリストのアイテム数
//    public int pref_zenkyoku_list_id = -1;			// 全曲リスト
//    public int saikintuika_list_id = -1;			//最近追加
//    public int saikinsisei_list_id = -1;			//最近再生
//
//    public String play_order;
//    //アーティストごとの情報
//    public String artistID;
//    //アルバムごとの情報
//    public String albumID;
//    //曲ごとの情報
//    public String audioID;
//    public String dataURL = null;
//    public String b_List =null;			//前に再生していたプレイリスト
//    public int b_List_id = 0;			//前のプレイリストID
//    public int modori_List_id = 0;			//リピート前のプレイリストID
//    public int b_index;				//前の曲順
//
//    public String prBTname = "";			//前回接続していたBluetooth機器名
//    public String prBTAdress = "";			//MACアドレス（機器固有番号）
//    public String pMusic_dir = "";			//再生する音楽ファイルがあるフォルダ
//    public String pplist_usb = "";			//再生する音楽ファイルがあるUSBメモリーのフォルダ
//    public String pplist_rquest = "";		//リクエストリスト
//    public String pplist_a_new = "";		//新規プレイリスト

//    public List<String> pref_toneList;		//プリファレンス保存用トーンリスト
//    public String toneSeparata = "L";
//    public String tone_name;				//トーン名称
//    public boolean bBoot = false;					//バスブート
//    public short reverbBangou = 0;				//リバーブ効果番号
//    public int visualizerType;		// = Visualizer_type_FFT;		//VisualizerはFFT
//    public int Visualizer_type_none;		//191;Visualizerを使わない
//    public int Visualizer_type_wave;		//189;Visualizerはwave表示
//    public int Visualizer_type_FFT;		//190;VisualizerはFFT
//
//    public Boolean myPreferences.prTT_dpad = false;		//ダイヤルキーの有無
//    public String Siseiothers ="";				//レジューム再生の情報
    public String others ="";				//その他の情報

    //	public PreferenceFragmentCompat settingsFragment;
//    public ListPreference sumList;
//    public EditTextPreference sumEdit;

    //	public PreferenceCategory pPS_pref_player;			//プレイヤー設定
    public EditText pTF_pref_gyapless;			//クロスフェード時間
    //	public EditTextPreference pTF_pref_compBunki;			//コンピレーション分岐点
    public Switch pTF_pref_pb_bgc;				//プレイヤーの背景は白

    //	public PreferenceCategory pPS_pref_plist;			//プレイリスト設定
//	public EditText pTF_pref_artist_bunnri;		//アーティストリストを分離する曲数
    public Switch pcb_list_simple;				//シンプルなリスト表示（サムネールなど省略）
    public EditText pTF_prefsaikin_tuika;		//最近追加リストのデフォルト日数
    public EditText pTF_prefsaikin_sisei;		//最近再生リストのデフォルト曲数
    public EditText pTF_rundam_list_size;				//ランダム再生の設定曲数
    public EditText  pref_nitenka_memo;								//二点間再生状況

//	public PreferenceCategory pPS_pref_effect;										//サウンドエフェクト
//	public ListPreference pLi_pref_effect_vi;								//ビジュアライザー
//	public EditTextPreference pref_eff_memo;								//サウンドエフェクトの設定確認

    //	public PreferenceCategory pPS_pref_kisyubetu;		//機種別調整
    public Switch pcb_pref_notifplayer;		//ノティフィケーションプレイヤー</string>
    public Switch pcb_pref_lockscreen;			//ロックスクリーンプレイヤー</string>
    public Switch pcb_bt_renkei;				//Bluetoothの接続に連携して一時停止/再開
//	public SwitchPreferenceCompat pCB_pref_cyakusinn_fukki;	//終話後に自動再生

//    public EditTextPreference pTFBN_name ;					//前回接続していたBluetooth機器名
//    public EditTextPreference pTFMac_address;		//MACアドレス（機器固有番号）
//    public EditTextPreference pTF_music_dir;		//再生する音楽ファイルがあるフォルダ	public PreferenceScreen pPS_sonota;			//その他　のプリファレンススクリーン
//    public EditTextPreference pTF_plist_usb;		//再生する音楽ファイルがあるUSBメモリーのフォルダ
//    public EditTextPreference pTF_plist_rquest;	//リクエストリスト
//    public EditTextPreference pTF_plist_a_new;		//新規プレイリスト
    //	public PreferenceCategory pPS_taisyou_type;		//再生する音楽ファイルの種類（拡張子指定）のプリファレンススクリーン
    public Button pCB_pref_reset;				//設定消去
    public Button pCB_pref_listup_reset;		//調整リストのリセット
    public TextView pref_memo;							//その他の項目列記

    public Map<String, ?> keys;
//	public EditTextPreference pEdit;

    ///外部から呼ばれる時の動作//////////////////////////////
    public int reqCode = 0;
    public int backCode = 0;
    public boolean isAllSave=false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        final String TAG = "onKeyDown";
        String dbMsg="";
        try{
            dbMsg = "keyCode="+keyCode+",getDisplayLabel="+event.getDisplayLabel()+",getAction="+event.getAction();////////////////////////////////
            //		myLog(TAG,dbMsg);
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:	//バック；4
                    quitMe();
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:		//マルチガイド上；19
                case KeyEvent.KEYCODE_DPAD_DOWN:	//マルチガイド下；20
                case KeyEvent.KEYCODE_DPAD_LEFT:	//マルチガイド左；21
                case KeyEvent.KEYCODE_DPAD_RIGHT:	//マルチガイド右；22
                    myPreferences.prTT_dpad=true;
                    break;
                default:
                    myPreferences.prTT_dpad=false;			//ダイヤルキーは使えない
                    break;
            }
            prefItemuKakikomi("pref_sonota_dpad" ,String.valueOf(myPreferences.prTT_dpad));									//プリファレンス全項目読出し
            //		myLog("onKeyDown","ダイヤルキーは"+myPreferences.prTT_dpad);
//		} catch (NullPointerException e) {
//			myErrorLog(TAG,"エラー発生；"+e.toString());
        } catch (Exception e) {
            myErrorLog(TAG,"エラー発生；"+e.toString());
            //		return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String TAG = "onCreate";
        String dbMsg="";
        try{
            long start = System.currentTimeMillis();		// 開始時刻の取得
            ORGUT = new OrgUtil();		//自作関数集
            Bundle extras = getIntent().getExtras();
            reqCode = extras.getInt("reqCode");
            backCode = extras.getInt("backCode");
            dbMsg +="、reqCode=" + reqCode + "、backCode=" + backCode;
            myPreferences = new MyPreferences(this);
            dbMsg +="、PREFS_NAME=" + MyConstants.PREFS_NAME;
			if (31 <= android.os.Build.VERSION.SDK_INT ) {
                sharedPref = getSharedPreferences(MyConstants.PREFS_NAME, MODE_PRIVATE);
                myEditor = sharedPref.edit();
			}else{
                sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());			//	this.getSharedPreferences(this, MODE_PRIVATE);		//
                myEditor = sharedPref.edit();
			}

            setContentView(R.layout.settings_activity);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }

            locale = Locale.getDefault();		// アプリで使用されているロケール情報を取得
            dbMsg += ",locale="+locale;/////////////////////////////////////
            // プレイヤー設定
            //クロスフェード時間
            pTF_pref_gyapless= (EditText)findViewById(R.id.pref_gyapless);	//(EditTextPreference) settingsFragment..findPreference(pref_gyapless);
            pTF_pref_gyapless.setOnClickListener( v -> {
                myPreferences.pref_gyapless = pTF_pref_gyapless.getText().toString();
                myEditor.putString ("myPreferences.pref_gyapless", myPreferences.pref_gyapless);
                myEditor.apply();
                myLog(TAG,"pref_gyaplessを"+myPreferences.pref_gyapless+"に");
                isAllSave = true;
            });

            pTF_pref_pb_bgc= (Switch) findViewById(R.id.pref_pb_bgc);

            //リスト設定
            //シンプルなリスト表示（サムネールなど省略）
            pcb_list_simple = (Switch)findViewById(R.id.pref_list_simple);
            //最近追加リストのデフォルト枚数
            pTF_prefsaikin_tuika = (EditText)findViewById(R.id.pref_saikin_tuika);
            pTF_prefsaikin_tuika.setOnClickListener( v -> {
                myPreferences.pref_saikin_tuika = pTF_prefsaikin_tuika.getText().toString();
                myEditor.putString ("prefsaikin_tuika", myPreferences.pref_saikin_tuika);
                myEditor.apply();
                myLog(TAG,"prefsaikin_tuika"+myPreferences.pref_saikin_tuika+"に");
                isAllSave = true;
            });
            //最近再生リストのデフォルト曲数
            pTF_prefsaikin_sisei = (EditText)findViewById(R.id.pref_saikin_sisei);
            pTF_prefsaikin_sisei.setOnClickListener( v -> {
                myPreferences.pref_saikin_sisei = pTF_prefsaikin_sisei.getText().toString();
                myEditor.putString ("pref_saikin_sisei", myPreferences.pref_saikin_sisei);
                myEditor.apply();
                myLog(TAG,"pref_saikin_sisei"+myPreferences.pref_saikin_sisei+"に");
                isAllSave = true;
            });
            //ランダム再生の設定曲数
            pTF_rundam_list_size = (EditText)findViewById(R.id.pref_rundam_list_size);
            pTF_rundam_list_size.setOnClickListener( v -> {
                myPreferences.pref_rundam_list_size = pTF_rundam_list_size.getText().toString();
                myEditor.putString ("pref_rundam_list_size", myPreferences.pref_rundam_list_size);
                myEditor.apply();
                myLog(TAG,"pref_rundam_list_size"+myPreferences.pref_rundam_list_size+"に");
                isAllSave = true;
            });
            // 機種別調整
            //ノティフィケーションプレイヤー
            pcb_pref_notifplayer = (Switch)findViewById(R.id.pref_notifplayer);
            //ロックスクリーンプレイヤー
            pcb_pref_lockscreen = (Switch)findViewById(R.id.pref_lockscreen);
            //Bluetoothの接続に連携して一時停止/再開
            pcb_bt_renkei = (Switch)findViewById(R.id.pref_bt_renkei);
            // その他　のプリファレンススクリーン
            pref_memo= (TextView)findViewById(R.id.pref_memo);	//(EditTextPreference) settingsFragment.findPreference("(EditText)findViewById(R.id.pref_gyapless);	//");							//その他の項目列記

            pCB_pref_reset = (Button) findViewById(R.id.pref_reset);	//(SwitchPreferenceCompat) settingsFragment.findPreference("pref_reset");		//設定消去
            pCB_pref_reset.setOnClickListener( v -> {
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle(getResources().getString(R.string.pref_reset))
                        .setMessage(getResources().getString(R.string.pref_reset_msg))
                        .setPositiveButton(getResources().getString(R.string.comon_suru) , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog , int which) {
                                prefItialize();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.comon_sinai) , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog , int which) {
                            }
                        })
                        .create().show();
            });
            pCB_pref_listup_reset = (Button)findViewById(R.id.pref_listup_reset);	//(SwitchPreferenceCompat) settingsFragment.findPreference("pref_listup_reset");		//調整リストのリセット
            pCB_pref_listup_reset.setOnClickListener( v -> {
                setdPrif();
            });
//			pref_nitenka_memo = (EditTextPreference) settingsFragment.findPreference("pref_nitenka_memo");								//二点間再生状況
////				pref_nitenkan = (CheckBoxPreference) findPreference("pref_nitenkan");								//二点間再生中
////				pref_nitenkan_start = (EditTextPreference) findPreference("pref_nitenkan_start");					//二点間再生開始点
////				pref_nitenkan_end = (EditTextPreference) findPreference("pref_nitenkan_end");						//二点間再生終了点
////			pPS_pref_effect = (PreferenceScreen) settingsFragment.findPreference("pref_effect");										//サウンドエフェクト
//			pLi_pref_effect_vi = (ListPreference) settingsFragment.findPreference("visualizerType");								//ビジュアライザー
//	//		pLi_pref_effect_vi.setOnPreferenceChangeListener(listPreference_OnPreferenceChangeListener);
//			pref_eff_memo = (EditTextPreference) settingsFragment.findPreference("pref_eff_memo");								//サウンドエフェクトの設定確認
////			pPS_pref_kisyubetu = (PreferenceScreen)findPreference("pref_kisyubetu");

            reDrowView();				//プリファレンス画面表示

            long end=System.currentTimeMillis();		// 終了時刻の取得
            dbMsg=dbMsg+(int)((end - start)) + "m秒で表示終了";
            myLog(TAG,dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG,dbMsg + "で"+e.toString());
        }
    }

    /**チェックボックスの動作*/
    public void onCheckboxClicked(View view) {
        final String TAG = "onCheckboxClicked";
        String dbMsg="";
        try{
            boolean isApply =true;
            boolean checked = ((CheckBox) view).isChecked();
            dbMsg+=",checked=" + checked;
            switch(view.getId()) {
                case R.id.pref_pb_bgc:
                    dbMsg+=",プレイヤーの背景は黒=" + myPreferences.pref_pb_bgc;
                    myPreferences.pref_pb_bgc=checked;
                    dbMsg+=">>" + myPreferences.pref_pb_bgc;
                    if(myPreferences.pref_pb_bgc){
                        pTF_pref_pb_bgc.setText(getString(R.string.pref_pb_bgc_titol) + " : " + getString(R.string.pref_pb_bgc_bk));
                        myEditor.putBoolean ("myPreferences.pref_pb_bgc", true);
                    }else{
                        pTF_pref_pb_bgc.setText(getString(R.string.pref_pb_bgc_titol) + " : " + getString(R.string.pref_pb_bgc_wh));
                        myEditor.putBoolean ("myPreferences.pref_pb_bgc", false);
                    }
                    break;
                case R.id.pref_list_simple:
                    dbMsg+=",シンプルなリスト表示（サムネールなど省略）=" + myPreferences.pref_list_simple;
                    myPreferences.pref_list_simple=checked;
                    dbMsg+=">>" + myPreferences.pref_list_simple;
                    if(myPreferences.pref_list_simple){
                        pcb_list_simple.setText(getString(R.string.pref_list_simple_title) +  " : " + getString(R.string.pref_list_simple_summaryOn));
                        myEditor.putBoolean ("pref_list_simple", true);
                    }else{
                        pcb_list_simple.setText(getString(R.string.pref_list_simple_title) +  " : " + getString(R.string.pref_list_simple_summaryOff));
                        myEditor.putBoolean ("pref_list_simple", false);
                    }
                    break;
                case R.id.pref_notifplayer:
                    dbMsg+=",pref_notifplayer=" + myPreferences.pref_notifplayer;
                    myPreferences.pref_notifplayer= checked;
                    dbMsg+=">>" + myPreferences.pref_notifplayer;
                    if(myPreferences.pref_notifplayer){
                        pcb_pref_notifplayer.setText(getString(R.string.pref_notifplayer) + " : " + getString(R.string.comon_tukau));
                        myEditor.putBoolean ("pref_notifplayer", true);
                    }else{
                        pcb_pref_notifplayer.setText(getString(R.string.pref_notifplayer) + " : " + getString(R.string.comon_tukawanai));
                        myEditor.putBoolean ("pref_notifplayer", false);
                    }
                    break;
                case R.id.pref_lockscreen:
                    dbMsg+=",pref_lockscreen=" + myPreferences.pref_lockscreen;
                    myPreferences.pref_lockscreen= checked;
                    dbMsg+=">>" + myPreferences.pref_lockscreen;
                    if(myPreferences.pref_lockscreen){
                        pcb_pref_lockscreen.setText(getString(R.string.pref_lockscreen) + " : " + getString(R.string.comon_tukau));
                        myEditor.putBoolean ("pref_lockscreen", true);
                    }else{
                        pcb_pref_lockscreen.setText(getString(R.string.pref_lockscreen) + " : " + getString(R.string.comon_tukawanai));
                        myEditor.putBoolean ("pref_lockscreen", false);
                    }
                    break;
                case R.id.pref_bt_renkei:
                    dbMsg+=",.pref_bt_renkei=" + myPreferences.pref_bt_renkei;
                    myPreferences.pref_bt_renkei= checked;
                    dbMsg+=">>" + myPreferences.pref_bt_renkei;
                    if(myPreferences.pref_bt_renkei){
                        pcb_bt_renkei.setText(getString(R.string.pref_bt_renkei_titol) + " : " + getString(R.string.comon_sinai));
                        myEditor.putBoolean ("pref_bt_renkei", true);
                    }else{
                        pcb_bt_renkei.setText(getString(R.string.pref_bt_renkei_titol) + " : " + getString(R.string.comon_suru));
                        myEditor.putBoolean ("pref_bt_renkei", false);
                    }
                    break;
                default:
                    dbMsg+=",不明なチェックボックス";
                    isApply =false;
                    break;
            }
            if(isApply){
                myEditor.apply();
                dbMsg+=">>設定";
                isAllSave = true;
            }
            myLog(TAG,dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG,dbMsg + "で"+e.toString());
        }
    }

    public void onSwitchClicked(View view) {
        final String TAG = "onSwitchClicked";
        String dbMsg="";
        try{
            boolean isApply =true;
            boolean checked = ((Switch) view).isChecked();
            dbMsg+=",checked=" + checked;
            switch(view.getId()) {
                case R.id.pref_pb_bgc:
                    dbMsg+=",プレイヤーの背景は白=" + myPreferences.pref_pb_bgc;
                    myPreferences.pref_pb_bgc=checked;
                    if(myPreferences.pref_pb_bgc){
                        pTF_pref_pb_bgc.setText(getString(R.string.pref_pb_bgc_titol) + " : " + getString(R.string.pref_pb_bgc_bk));
                        myEditor.putBoolean ("pref_pb_bgc", true);
                    }else{
                        pTF_pref_pb_bgc.setText(getString(R.string.pref_pb_bgc_titol) + " : " + getString(R.string.pref_pb_bgc_wh));
                        myEditor.putBoolean ("pref_pb_bgc", false);
                    }
                    dbMsg+=">>" + myPreferences.pref_pb_bgc;
                    break;
                case R.id.pref_list_simple:
                    dbMsg+=",シンプルなリスト表示（サムネールなど省略）=" + myPreferences.pref_list_simple;
                    myPreferences.pref_list_simple=checked;
                    if(myPreferences.pref_list_simple){
                        pcb_list_simple.setText(getString(R.string.pref_list_simple_title) +  " : " + getString(R.string.pref_list_simple_summaryOn));
                        myEditor.putBoolean ("pref_list_simple", true);
                    }else{
                        pcb_list_simple.setText(getString(R.string.pref_list_simple_title) +  " : " + getString(R.string.pref_list_simple_summaryOff));
                        myEditor.putBoolean ("pref_list_simple", false);
                    }
                    dbMsg+=">>" + myPreferences.pref_list_simple;
                    break;
                case R.id.pref_notifplayer:
                    dbMsg+=",myPreferences.pref_notifplayer=" + myPreferences.pref_notifplayer;
                    myPreferences.pref_notifplayer= checked;
                    if(myPreferences.pref_notifplayer){
                        pcb_pref_notifplayer.setText(getString(R.string.pref_notifplayer) + " : " + getString(R.string.comon_tukau));
                        myEditor.putBoolean ("pref_notifplayer", true);
                    }else{
                        pcb_pref_notifplayer.setText(getString(R.string.pref_notifplayer) + " : " + getString(R.string.comon_tukawanai));
                        myEditor.putBoolean ("pref_notifplayer", false);
                    }
                    dbMsg+=">>" + myPreferences.pref_notifplayer;
                    break;
                case R.id.pref_lockscreen:
                    dbMsg+=",pref_lockscreen=" + myPreferences.pref_lockscreen;
                    myPreferences.pref_lockscreen= checked;
                    if(myPreferences.pref_lockscreen){
                        pcb_pref_lockscreen.setText(getString(R.string.pref_lockscreen) + " : " + getString(R.string.comon_tukau));
                        myEditor.putBoolean ("pref_lockscreen", true);
                    }else{
                        pcb_pref_lockscreen.setText(getString(R.string.pref_lockscreen) + " : " + getString(R.string.comon_tukawanai));
                        myEditor.putBoolean ("pref_lockscreen", false);
                    }
                    dbMsg+=">>" + myPreferences.pref_lockscreen;
                    break;
                case R.id.pref_bt_renkei:
                    dbMsg+=",myPreferences.pref_bt_renkei=" + myPreferences.pref_bt_renkei;
                    myPreferences.pref_bt_renkei= checked;
                    if(myPreferences.pref_bt_renkei){
                        pcb_bt_renkei.setText(getString(R.string.pref_bt_renkei_titol) + " : " + getString(R.string.comon_suru));
                        myEditor.putBoolean ("pref_bt_renkei", true);
                    }else{
                        pcb_bt_renkei.setText(getString(R.string.pref_bt_renkei_titol) + " : " + getString(R.string.comon_sinai));
                        myEditor.putBoolean ("pref_bt_renkei", false);
                    }
                    dbMsg+=">>" + myPreferences.pref_bt_renkei;
                    break;
                default:
                    dbMsg+=",不明なチェックボックス";
                    isApply =false;
                    break;
            }
            if(isApply){
                myEditor.apply();
                dbMsg+=">>設定";
                isAllSave = true;
            }
            myLog(TAG,dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG,dbMsg + "で"+e.toString());
        }
    }


    /**
     * 設定内容の書込み
     * **/
    public void reDrowView() {				//プリファレンス画面表示
        final String TAG = "reDrowView";
        String dbMsg="";
        try{
            long start = System.currentTimeMillis();		// 開始時刻の取得
            myPreferences.readPref();		//プリファレンスの読込み
            String wrStr= null;
			dbMsg += "クロスフェード時間" + myPreferences.pref_gyapless;
            pTF_pref_gyapless.setText(myPreferences.pref_gyapless);
            dbMsg += ",プレイヤーの背景は白=" + myPreferences.pref_pb_bgc;/////////////////
            pTF_pref_pb_bgc.setChecked(myPreferences.pref_pb_bgc);	//プレイヤーの背景は白
            if(myPreferences.pref_pb_bgc){
                pTF_pref_pb_bgc.setText(getString(R.string.pref_pb_bgc_titol) + " : " + getString(R.string.pref_pb_bgc_bk));
            }else{
                pTF_pref_pb_bgc.setText(getString(R.string.pref_pb_bgc_titol) + " : " + getString(R.string.pref_pb_bgc_wh));
            }
            dbMsg += ",シンプルなリスト表示=" + myPreferences.pref_list_simple;
            pcb_list_simple.setChecked(myPreferences.pref_list_simple);			//シンプルなリスト表示（サムネールなど省略）
            if(myPreferences.pref_list_simple){
                pcb_list_simple.setText(getString(R.string.pref_list_simple_title) +  " : "  + getString(R.string.pref_list_simple_summaryOn));
            }else{
                pcb_list_simple.setText(getString(R.string.pref_list_simple_title) +  " : "  + getString(R.string.pref_list_simple_summaryOff));
            }

            pTF_prefsaikin_tuika.setText(myPreferences.pref_saikin_tuika);
            pTF_prefsaikin_sisei.setText(myPreferences.pref_saikin_sisei);
            pTF_rundam_list_size.setText(myPreferences.pref_rundam_list_size);

           	//機種別調整//////////////////////////////////////////////////////////
            dbMsg += ",ノティフィケーションプレイヤー＝" + myPreferences.pref_notifplayer;
            pcb_pref_notifplayer.setChecked(myPreferences.pref_notifplayer);
            if(myPreferences.pref_notifplayer){
                pcb_pref_notifplayer.setText(getString(R.string.pref_notifplayer) + " : " + getString(R.string.comon_tukau));
            }else{
                pcb_pref_notifplayer.setText(getString(R.string.pref_notifplayer) + " : " + getString(R.string.comon_tukawanai));
            }
            dbMsg += ",ロックスクリーンプレイヤー＝" + myPreferences.pref_lockscreen;
            pcb_pref_lockscreen.setChecked(myPreferences.pref_lockscreen);
            if(myPreferences.pref_lockscreen){
                pcb_pref_lockscreen.setText(getString(R.string.pref_lockscreen) + " : " + getString(R.string.comon_tukau));
            }else{
                pcb_pref_lockscreen.setText(getString(R.string.pref_lockscreen) + " : " + getString(R.string.comon_tukawanai));
            }
            dbMsg += ",Bluetoothの接続に連携して一時停止/再開＝" + myPreferences.pref_bt_renkei;
            pcb_bt_renkei.setChecked(myPreferences.pref_bt_renkei);
            if(myPreferences.pref_bt_renkei){
                pcb_bt_renkei.setText(getString(R.string.pref_bt_renkei_titol) + " : " + getString(R.string.comon_suru));
            }else{
                pcb_bt_renkei.setText(getString(R.string.pref_bt_renkei_titol) + " : " + getString(R.string.comon_sinai));
            }
//			dbMsg +="終話後に自動再生＝" + pref_cyakusinn_fukki;//////////////////
//			dbMsg += " , pCB_pref_cyakusinn_fukki＝" + pCB_pref_cyakusinn_fukki;//////////////////
//            kisyubetu +=getString(R.string.pref_cyakusinn_fukki)+"=" + pref_cyakusinn_fukki +"\n";		//	pCB_pref_cyakusinn_fukki.getSummary() +"\n";

            String summelyStr = "";				//その他//////////////////////////////////////////////////
            summelyStr = getString(R.string.pref_data_url)+ "\n[";
            dbMsg +="再生中のリスト" + myPreferences.nowList_id;//////////////////
            if(myPreferences.nowList_id != null){
                summelyStr += myPreferences.nowList_id ;
            }
            summelyStr += "]" ;
            if(myPreferences.nowList != null){
                summelyStr += myPreferences.nowList ;
            }
            summelyStr += "\n" ;
            if(0 <= myPreferences.pref_mIndex){
                summelyStr += "[" + myPreferences.pref_mIndex + "]" ;
            }
            if(myPreferences.pref_data_url != null){
                summelyStr += myPreferences.pref_data_url +"\n" ;
            }

//            if(saisei_fname != null){
//                dbMsg +="再生中のファイル名" + saisei_fname;//////////////////
//                saisei = saisei_fname +"\n" ;
//            }
            if(myPreferences.pref_saisei_jikan != null ){
                wrStr=ORGUT.sdf_mss.format(Long.valueOf(myPreferences.pref_saisei_jikan));
                dbMsg +="再生ポジション；" + myPreferences.pref_saisei_jikan +">>" +wrStr;				//+";"+pTF_saisei_jikan.getText();//////////////////
                summelyStr += "\n" +getResources().getString(R.string.pref_saisei_come1) +" [" + wrStr;
            }
            if(myPreferences.pref_saisei_nagasa != null ){
                wrStr=ORGUT.sdf_mss.format(Long.valueOf(myPreferences.pref_saisei_nagasa));
                dbMsg +="再生時間；" +myPreferences.pref_saisei_nagasa +">>" +wrStr;				//+";"+pTF_saisei_jikan.getText();//////////////////
                summelyStr +=  "/"+wrStr +"]" + "\n";
            }
            if( myPreferences.pref_zenkai_saiseKyoku != null ){		//前回の連続再生曲数
                dbMsg +="前回の連続再生曲数；" +myPreferences.pref_zenkai_saiseKyoku;				//+";"+pTF_saisei_jikan.getText();//////////////////
                summelyStr += "\n" + summelyStr+getResources().getString(R.string.comon_zennkai) +myPreferences.pref_zenkai_saiseKyoku + getResources().getString(R.string.pp_kyoku) ;
            }
            if(myPreferences.pref_zenkai_saiseijikann != null ){
                dbMsg +="前回の連続再生時間；"+ORGUT.sdf_mss.format(Long.valueOf(myPreferences.pref_zenkai_saiseijikann));			///////////////////
                summelyStr += ORGUT.sdf_mss.format(Long.valueOf(myPreferences.pref_zenkai_saiseijikann));
            }else{
                myPreferences.pref_zenkai_saiseijikann = "0";
            }
            if(! myPreferences.pref_file_in.equals("")){
                summelyStr += "\n" + getString(R.string.pref_file_in)+"="+ myPreferences.pref_file_in + "\n";
            }
            if(! myPreferences.pref_file_ex.equals("")){
                summelyStr += getString(R.string.pref_file_ex)+"="+myPreferences.pref_file_ex + "\n";
            }else{
                summelyStr += getString(R.string.pref_file_ex)+getString(R.string.comon_nasi) + "\n";
            }
            if(! myPreferences.pref_file_wr.equals("")){
                summelyStr += getString(R.string.pref_file_wr)+"="+myPreferences.pref_file_wr + "\n";
            }
            if(! myPreferences.pref_commmn_music.equals("")){
                summelyStr +=  getString(R.string.pref_commmn_music)+"="+myPreferences.pref_commmn_music+ "\n";
            }
            //		pref_filse.setSummary(summelyStr);

            if(! myPreferences.pref_file_kyoku.equals("")){
                summelyStr = getString(R.string.pref_file_kyoku)+"="+myPreferences.pref_file_kyoku + "\n";
            }
//            if(! pref_file_album.equals("")){
//                summelyStr += getString(R.string.pref_file_album)+"="+pref_file_album + "\n";
//            }
            if(! myPreferences.pref_file_saisinn.equals("")){
                summelyStr += getString(R.string.pref_file_saisinn)+"="+myPreferences.pref_file_saisinn + "\n";
            }
            //		pPS_sonota.setSummary(summelyStr);

            dbMsg +="myPreferences.pref_apiLv＝" + myPreferences.pref_apiLv;//////////////////
            summelyStr += getString(R.string.pref_sonota_apil)+"="+myPreferences.pref_apiLv + "\n";
            dbMsg +="pref_sonota_vercord＝" + myPreferences.pref_sonota_vercord;//////////////////
            summelyStr += getString(R.string.pref_sonota_vercord)+"="+myPreferences.pref_sonota_vercord + "\n";

            dbMsg +=summelyStr;				//+";"+pTF_saisei_jikan.getText();//////////////////
            pref_memo.setText(summelyStr);
            long end=System.currentTimeMillis();		// 終了時刻の取得
            dbMsg +=(int)((end - start)) + "m秒で表示終了";
            myLog(TAG,dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG,dbMsg + "で"+e.toString());
        }
    }

    /**
     * プリファレンス初期化
     * */
    public void prefItialize( ) {				//プリファレンスの表示処理
        final String TAG = "prefItialize";
        String dbMsg="";
        try{
            myEditor.clear();		//プリファレンスの内容削除
            myEditor.commit();
            myPreferences.pref_gyapless="100";
            dbMsg += "クロスフェード時間" + myPreferences.pref_gyapless+getResources().getString(R.string.pp_msec) ;
            myEditor.putString ("pref_gyapless", myPreferences.pref_gyapless);

            myPreferences.pref_pb_bgc = true;
            dbMsg += ",プレイヤーの背景は黒=" + myPreferences.pref_pb_bgc;
            myEditor.putBoolean ("pref_pb_bgc", myPreferences.pref_pb_bgc);

            myPreferences.pref_list_simple = false;
            dbMsg += ",シンプルなリスト表示=" + myPreferences.pref_list_simple;
            myEditor.putBoolean ("pref_list_simple", myPreferences.pref_list_simple);

            myPreferences.pref_saikin_tuika="7";
            dbMsg = ",最近追加リスト" + myPreferences.pref_saikin_tuika + "日" ;
            myEditor.putString ("pref_saikin_tuika", myPreferences.pref_saikin_tuika);

            myPreferences.pref_saikin_sisei="100";
            dbMsg = ",最近再生加リスト" + myPreferences.pref_saikin_sisei + "曲" ;
            myEditor.putString ("pref_saikin_sisei", myPreferences.pref_saikin_sisei);

            myPreferences.pref_rundam_list_size="100";
            dbMsg = ",ランダム再生リストアップ曲数" + myPreferences.pref_rundam_list_size + "曲" ;
            myEditor.putString ("pref_rundam_list_size", myPreferences.pref_rundam_list_size);

            //機種別調整////////////////////
            myPreferences.pref_notifplayer =true;
            dbMsg += ",ノティフィケーションプレイヤー＝" + myPreferences.pref_notifplayer;//////////////
            myEditor.putBoolean ("pref_notifplayer", myPreferences.pref_notifplayer);

            myPreferences.pref_lockscreen = true;
            dbMsg += ",ロックスクリーンプレイヤー＝" + myPreferences.pref_lockscreen;//////////////
            myEditor.putBoolean ("pref_lockscreen", myPreferences.pref_lockscreen);

            myPreferences.pref_bt_renkei = true;
            dbMsg += ",Bluetoothの接続に連携して一時停止/再開＝" + myPreferences.pref_bt_renkei;//////////////////pcb_bt_renkei
            myEditor.putBoolean ("pref_bt_renkei", myPreferences.pref_bt_renkei);

            String status = Environment.getExternalStorageState();
            if (!status.equals(Environment.MEDIA_MOUNTED)) {
                myPreferences.pref_file_ex =Environment.getExternalStorageDirectory().getPath();
                dbMsg += ",メモリーカード＝" + myPreferences.pref_file_ex;//////////////////
            } else{
                dbMsg += ",メモリーカード＝無し" ;//////////////////
            }
            myEditor.putString ("pref_file_ex", myPreferences.pref_file_ex);

            myPreferences.pref_file_wr = this.getFilesDir().getPath();
            dbMsg += ",設定保存フォルダ＝" + myPreferences.pref_file_wr;
            myEditor.putString ("pref_file_wr", myPreferences.pref_file_wr);

            myPreferences.prTT_dpad =false;
            dbMsg += ",prTT_dpad＝" + myPreferences.prTT_dpad;
            myEditor.putBoolean ("prTT_dpad", myPreferences.prTT_dpad);

//            myPreferences.pref_apiLv = String.valueOf(Build.VERSION.SDK);                                    //APIレベル
//            dbMsg += ",myPreferences.pref_apiLv=" + myPreferences.pref_apiLv;
//            myEditor.putString ("myPreferences.pref_apiLv", myPreferences.pref_apiLv);
//
//            dbMsg += ",このアプリのバージョンコード="+ myPreferences.pref_sonota_vercord;
//            myEditor.putInt ("pref_sonota_vercord", myPreferences.pref_sonota_vercord);

            //更新
            myEditor.apply();
            myLog(TAG,dbMsg);
            reDrowView();
        }catch (Exception e) {
            myErrorLog(TAG,dbMsg + "で"+e.toString());
        }
    }

    /**
     * プリファレンスの読込み
     * */
    @SuppressLint("SimpleDateFormat")
    public void readPref(Context context){
        final String TAG = "readPref";
        String dbMsg="";
        try {
            dbMsg += "MyPreferencesy読込み";
            myPreferences.readPref();

            dbMsg += "、このアプリのバージョンコード=" + myPreferences.pref_sonota_vercord;
            dbMsg += ",クロスフェード時間=" + myPreferences.pref_gyapless;
            dbMsg += ",プレイヤーの背景は黒=" + myPreferences.pref_pb_bgc;
            dbMsg += "、シンプルなリスト表示=" + myPreferences.pref_list_simple;
//            repeatType = myPreferences.repeatType;							//リピート再生の種類
//			rp_pp = myPreferences.rp_pp;							//2点間リピート中
            dbMsg += "、Bluetoothの接続に連携して一時停止/再開=" + myPreferences.pref_bt_renkei;
            dbMsg += "、再生中のプレイリスト[" + myPreferences.nowList_id;
            dbMsg += "]" + myPreferences.nowList;
            dbMsg += "、再生中のファイル名=" + myPreferences.pref_data_url;
//            saisei_fname = myPreferences.saisei_fname;
//            myPreferences.pref_data_url =myPreferences.saisei_fname;				//
//            dbMsg += "、汎用プレイリストのファイル名=" + saisei_fname;
            dbMsg += "、前回=" + myPreferences.pref_zenkai_saiseKyoku + "曲、" + myPreferences.pref_zenkai_saiseijikann + "時間";
            dbMsg += "、内蔵メモリ=" + myPreferences.pref_file_in;
            dbMsg += "、メモリーカード=" + myPreferences.pref_file_ex;
            dbMsg += "、設定保存フォルダ=" + myPreferences.pref_file_wr;
            dbMsg += "、共通音楽フォルダ=" + myPreferences.pref_commmn_music;
            dbMsg += "、総曲数=" + myPreferences.pref_file_kyoku;
//			dbMsg += "、総アルバム数=" + myPreferences.pref_file_album);
//			pref_file_album= Integer.parseInt(myPreferences.pref_file_album);		//
            dbMsg += "、記録している最新更新日=" + myPreferences.pref_file_saisinn;
//            if(!myPreferences.pref_file_saisinn.equals("")){
//                String mod = sdffiles.format(new Date(Long.valueOf(myPreferences.pref_file_saisinn) * 1000));
//                dbMsg += ">>" + mod;
//            }
            dbMsg += "、全曲リスト=" + myPreferences.pref_zenkyoku_list_id;
            dbMsg += "、最近追加=" + myPreferences.saikintuika_list_id;
            dbMsg += "、最近再生=" + myPreferences.saikinsisei_list_id;
            myLog(TAG,dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG,dbMsg+"で"+e);
        }
    }

    /**
     * 現在の状態で更新
     * */
    @SuppressLint("SimpleDateFormat")
    public void setdPrif(){
        final String TAG = "setdPrif";
        String dbMsg="";
        try{
            ORGUT = new OrgUtil();		//自作関数集
            dbMsg += ",PREFS_NAME=" + MyConstants.PREFS_NAME;

            myPreferences.pref_gyapless = String.valueOf(pTF_pref_gyapless.getText());
            dbMsg += "クロスフェード時間" + myPreferences.pref_gyapless;
            myEditor.putString ("pref_gyapless", myPreferences.pref_gyapless);            //クロスフェード時間
            myPreferences.pref_pb_bgc = pTF_pref_pb_bgc.isChecked();
            dbMsg +=  ",プレイヤーの背景は黒=" + myPreferences.pref_pb_bgc;
            myEditor.putBoolean ("pref_pb_bgc", myPreferences.pref_pb_bgc);

            //リスト設定
            myPreferences.pref_list_simple =pcb_list_simple.isChecked();
            dbMsg +=  "シンプルなリスト表示（サムネールなど省略）=" + myPreferences.pref_list_simple;
            myEditor.putBoolean ("pref_list_simple", myPreferences.pref_list_simple);

            myPreferences.pref_saikin_tuika  = String.valueOf(pTF_prefsaikin_tuika.getText());
            dbMsg +=  "最近追加リストのデフォルト日数";
            myEditor.putString ("pref_saikin_tuika", myPreferences.pref_saikin_tuika);

            myPreferences.pref_saikin_sisei = String.valueOf(pTF_prefsaikin_sisei.getText());
            dbMsg +=  "最近再生リストのデフォルト曲数=" + myPreferences.pref_saikin_sisei ;
            myEditor.putString ("pref_saikin_sisei", myPreferences.pref_saikin_sisei);

            myPreferences.pref_rundam_list_size = String.valueOf(pTF_rundam_list_size.getText());
            dbMsg +=  "ランダム再生の設定曲数=" + myPreferences.pref_rundam_list_size ;
            myEditor.putString ("pref_rundam_list_size", myPreferences.pref_rundam_list_size);

            myPreferences.pref_notifplayer = pcb_pref_notifplayer.isChecked();
            dbMsg += ",ノティフィケーションプレイヤー＝" + myPreferences.pref_notifplayer;
            myEditor.putBoolean ("pref_notifplayer", myPreferences.pref_notifplayer);

            myPreferences.pref_lockscreen = pcb_pref_lockscreen.isChecked();
            dbMsg += ",ロックスクリーンプレイヤー＝" + myPreferences.pref_lockscreen;
            myEditor.putBoolean ("pref_lockscreen", myPreferences.pref_lockscreen);

            myPreferences.pref_bt_renkei = pcb_bt_renkei.isChecked();
            dbMsg +=  "Bluetoothの接続に連携=" + myPreferences.pref_bt_renkei;////////////////////////////////////////////////////////////////////////////
            myEditor.putBoolean ("pref_bt_renkei", myPreferences.pref_bt_renkei);
//			20200223:前回に再生したファイル、プレイリストが無いことでインストール直後であることを判定：
//			saisei_fname = "";						//再生中のファイル名
//			dbMsg +=  "再生中のファイル名" + saisei_fname;////////////////////////////////////////////////////////////////////////////
//			myEditor.putString ("myPreferences.pref_data_url", saisei_fname);
//			myPreferences.pref_saisei_jikan = "0";		//再開時間		Integer.valueOf(keys.get(key).toString());
//			dbMsg += "再生中音楽ファイルの再開時間" ;//////////////////
//			wrStr = ORGUT.sdf_mss.format(Long.valueOf(myPreferences.pref_saisei_jikan));
//			dbMsg += "再生ポジション；" + myPreferences.pref_saisei_jikan +">>" +wrStr;				//+";"+pTF_saisei_jikan.getText();//////////////////
//			myEditor.putString ("pref_position", myPreferences.pref_saisei_jikan);
//			myPreferences.pref_saisei_nagasa = "0";		//pTF_myPreferences.pref_saisei_nagasa;		//再生中音楽ファイルの再生時間
//			dbMsg += "再生中音楽ファイルの長さ＝" + myPreferences.pref_saisei_nagasa;//////////////////
//			wrStr=ORGUT.sdf_mss.format(Long.valueOf(myPreferences.pref_saisei_nagasa));
//			dbMsg += "再生時間；" +myPreferences.pref_saisei_nagasa +">>" +wrStr;				//+";"+pTF_saisei_jikan.getText();//////////////////
//			myEditor.putString ("pref_duration", myPreferences.pref_saisei_nagasa);
//			myPreferences.myPreferences.nowList_id = "-1";
//			dbMsg +=  "、myPreferences.nowList["  + myPreferences.myPreferences.nowList_id +"]";	//再生中のプレイリストID	playListID
//			myEditor.putString ("myPreferences.myPreferences.nowList_id", myPreferences.myPreferences.nowList_id);
//			myPreferences.nowList = String.valueOf(context.getResources().getText(R.string.listmei_zemkyoku));
//			dbMsg +=  "プレイリスト名="  + myPreferences.nowList;////////////////////////////////////////////////////////////////////////////
//			myEditor.putString ("myPreferences.nowList", myPreferences.nowList);
//            play_order ="0";
//            dbMsg +=  "(play_order=" + play_order +")";////////////////////////////////////////////////////////////////////////////
//            myEditor.putString ("play_order", play_order);
//            artistID = "1";
//			dbMsg +=  ",アーティスト=" + artistID ;////////////////////////////////////////////////////////////////////////////
//			myEditor.putString ("artistID", artistID);
//			albumID = "1";
//			dbMsg +=  ",アルバム=" + albumID ;////////////////////////////////////////////////////////////////////////////
//			myEditor.putString ("albumID", albumID);
//			audioID = "1";
//			dbMsg +=  ",曲=" + audioID ;////////////////////////////////////////////////////////////////////////////
//			myEditor.putString ("audioID", audioID);

//            dbMsg += ",内蔵メモリ＝" + myPreferences.pref_file_in;////////////////    //storage/emulated/0/Music
//            myEditor.putString ("pref_file_in", myPreferences.pref_file_in);
//            myPreferences.pref_file_ex = "";
//            String status = Environment.getExternalStorageState();
//            if (!status.equals(Environment.MEDIA_MOUNTED)) {
//                myPreferences.pref_file_ex =Environment.getExternalStorageDirectory().getPath();
//                dbMsg += ",メモリーカード＝" + myPreferences.pref_file_ex;//////////////////
//            } else{
//                dbMsg += ",メモリーカード＝無し" ;//////////////////
//            }
//            myEditor.putString ("pref_file_ex", myPreferences.pref_file_ex);
////            myPreferences.pref_file_wr =	context.getFilesDir().getPath();
//            dbMsg += ",設定保存フォルダ＝" + myPreferences.pref_file_wr;//////////////////
//            myEditor.putString ("pref_file_wr", myPreferences.pref_file_wr);
//
//            myPreferences.pref_commmn_music = Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).getPath();
//            dbMsg += ",共通音楽フォルダ＝" + myPreferences.pref_commmn_music;//////////////////
//            myEditor.putString ("pref_commmn_music", myPreferences.pref_commmn_music);
//            dbMsg += ",最新更新日＝" + myPreferences.pref_file_saisinn;//////////////
//            myEditor.putString ("myPreferences.pref_file_saisinn", myPreferences.pref_file_saisinn);
//			pref_compBunki ="40";
//			dbMsg +=  "コンピレーション分岐 = " + pref_compBunki ;////////////////////////////////////////////////////////////////////////////
//			myEditor.putString ("pref_compBunki", pref_compBunki);
//            repeatType = -1;
//            dbMsg +=  ">リピート再生の種類="+repeatType ;
//            myEditor.putInt("repeatType",repeatType);
//            rp_pp = false;
//            dbMsg +=  "二点間再生中=" + rp_pp;	//pref_nitenkan
//            myEditor.putBoolean ("pref_nitenkan", rp_pp);
//            pp_start ="0";
//            dbMsg +=  ";二点間再生開始点=" + pp_start ;
//            myEditor.putString ("pref_nitenkan_start", pp_start);
//            pp_end ="1";
//            dbMsg +=  ";二点間再生終了点=" + pp_end ;
//            myEditor.putString ("pref_nitenkan_end", pp_end);
//            b_List = "";
//            dbMsg += ",ノティフィケーションプレイヤー＝" + myPreferences.pref_notifplayer;//////////////
//            myEditor.putString ("myPreferences.pref_file_saisinn", myPreferences.pref_file_saisinn);
//            dbMsg +=  ",前に再生していたプレイリスト=" +b_List ;
//            myEditor.putString ("b_List", b_List);
//            b_index = 1;
//            dbMsg += ",前の再生曲ID=" +b_List_id ;
//            myEditor.putInt("b_index",b_index);
//            modori_List_id = 1;
//            dbMsg +=  ",リピート前に再生していたプレイリスト中のID=" + modori_List_id ;
//            myEditor.putInt("modori_List_id",modori_List_id);
//            tone_name = "";
//            dbMsg +=  "トーン名称=" + tone_name ;
//            myEditor.putString ("tone_name", tone_name);
//            try {
//                String[] toneNames = context.getResources().getStringArray(R.array.tone_names);											//plNameSL.toArray(new String[plNameSL.size()]);
//                dbMsg +=  ",toneNames= " + toneNames.length + "件";
//                JSONArray array = new JSONArray(toneNames);
//                int length = array.length();
//                dbMsg +=  "= " + length +"件";
//                pref_toneList =  new ArrayList<String>();				//トーンリストの初期化
//                for(int j = 0; j < length; j++){
//                    dbMsg +=  "(" + j + "/" + length  + ")" + array.optString(j);
//                    pref_toneList.add(array.optString(j));
//                }
//                myEditor.putString ("pref_toneList", array.toString());
//            } catch (JSONException e1) {
//                e1.printStackTrace();
//            }
//            bBoot = false;
//            dbMsg +=  "バスブート=" + bBoot ;
//            myEditor.putBoolean ("bBoot", bBoot);
//            reverbBangou = 0;
//            dbMsg +=  ",リバーブ効果番号=" + reverbBangou ;
//            myEditor.putInt ("reverbBangou", reverbBangou);
//            visualizerType = Visualizer_type_FFT;
//            dbMsg +=  ",visualizerType=" + visualizerType ;
//            myEditor.putInt ("visualizerType", visualizerType);
//            myPreferences.pref_zenkai_saiseKyoku = "0";		//pTF_myPreferences.pref_saisei_nagasa;		// != null ){		//
//            myEditor.putString ("pref_zenkai_saiseKyoku", myPreferences.pref_zenkai_saiseKyoku);
//            dbMsg += "前回の連続再生曲数＝" + myPreferences.pref_zenkai_saiseKyoku;
//            myPreferences.pref_zenkai_saiseijikann = "0";		//;		//前回の連続再生時間
//            myEditor.putString ("pref_zenkai_saiseijikann", myPreferences.pref_zenkai_saiseijikann);
//            dbMsg += "前回の連続再生時間＝" + myPreferences.pref_zenkai_saiseijikann;//////////////////
//            myPreferences.pref_file_kyoku = "0";
//            dbMsg += "総曲数＝" + myPreferences.pref_file_kyoku;////////////////
//            myEditor.putString ("pref_file_kyoku", myPreferences.pref_file_kyoku);
//            pref_file_album = "0";
//            dbMsg += "総アルバム数＝" + pref_file_album;//////////////////
//            myEditor.putString ("pref_file_album", pref_file_album);
//            myPreferences.pref_file_saisinn = "";
//            myEditor.putString ("myPreferences.pref_file_saisinn", myPreferences.pref_file_saisinn);
            dbMsg +=  "ダイヤルキー=" + myPreferences.prTT_dpad;////////////////////////////////////////////////////////////////////////////
            myEditor.putBoolean ("pref_sonota_dpad", myPreferences.prTT_dpad);

            myEditor.apply();    //非同期書込み
//			boolean wrb = myEditor.commit();	//は同期書込み
//			dbMsg= ">>書込み成功="+ wrb;
            myLog(TAG,dbMsg);
            reDrowView();
            isAllSave = false;
        } catch (Exception e) {
            myErrorLog(TAG,dbMsg+"で"+e);
        }
    }

    public void prefItemuKakikomi(String key ,String vStr){				//プリファレンスに文字を書き込む
        final String TAG = "prefItemuKakikomi";
        String dbMsg="";
        try{
            dbMsg= key+";"+ vStr;
            key = String.valueOf( key );
            vStr = String.valueOf( vStr );
            dbMsg= ">>" + key+";"+ vStr;
            myEditor.putString( key, vStr);						//再生中のファイル名  Editor に値を代入
            boolean wrb = myEditor.commit();
            dbMsg +=">>書込み成功="+ wrb;
            myLog(TAG,dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG,dbMsg+"で"+e);
        }
    }		//http://ichitcltk.hustle.ne.jp/gudon/modules/pico_rd/index.php?content_id=98

    public void prefBoolKakikomi(String key ,boolean vBool){			//プリファレンスにbool値を書き込む
        final String TAG = "prefBoolKakikomi";
        String dbMsg="";
        try{
            dbMsg= key+";"+ vBool;
            key = String.valueOf( key );
            vBool = Boolean.valueOf( vBool );
            dbMsg= ">>" + key+";"+ vBool;
            myEditor.putBoolean( key + "", vBool);						//再生中のファイル名  Editor に値を代入
            //20190506;java.lang.NullPointerException: Attempt to invoke interface method 'android.content.SharedPreferences$Editor android.content.SharedPreferences$Editor.putBoolean(java.lang.String, boolean)' on a null object reference
            boolean wrb = myEditor.commit();
            dbMsg= ">>書込み成功="+ wrb;
            myLog(TAG,dbMsg);
        } catch (Exception e) {
            myErrorLog(TAG,dbMsg+"で"+e);
        }
    }		//http://ichitcltk.hustle.ne.jp/gudon/modules/pico_rd/index.php?content_id=98

    public void quitMe(){			///終了処理
        final String TAG = "quitMe";
        String dbMsg="";
        try{
            dbMsg="スタート";///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if(isAllSave){
                setdPrif();
            }
            Intent data = new Intent();			// 返すデータ(Intent&Bundle)の作成
            dbMsg +="、reqCode=" + reqCode + "、backCode=" + backCode;
            data.putExtra("reqCode", reqCode);
            data.putExtra("backCode", backCode);
//            Bundle bundle = new Bundle();
//            data.putExtras(bundle);
////			if(pref_reset){				//設定を初期化
////				myEditor.clear();		//プリファレンスの内容削除
////				myEditor.commit();
////				data.putExtras(bundle);
////				setResult(RESULT_CANCELED, data);		// setResult() で bundle を載せた送るIntent dataをセットする	// 第一引数は…Activity.RESULT_OK, Activity.RESULT_CANCELED など
////			}else{
//            dbMsg += "シンプルなリスト表示="+ myPreferences.pref_list_simple;	////////////////
//            bundle.putString("key.pref_list_simple", String.valueOf(myPreferences.pref_list_simple));				//シンプルなリスト表示（サムネールなど省略）
//            dbMsg +=",プレイヤーの背景(w)="+ myPreferences.pref_pb_bgc;	////////////////
//            bundle.putString("key.myPreferences.pref_pb_bgc", String.valueOf(myPreferences.pref_pb_bgc));			//プレイヤーの背景
//            dbMsg += dbMsg +",クロスフェード="+ myPreferences.pref_gyapless;				///////////////
//            bundle.putString("key.pref_gyapless", myPreferences.pref_gyapless);		//クロスフェード時間
////				dbMsg= dbMsg +",コンピレーション分岐点="+ pref_compBunki;				///////////////
////				bundle.putString("key.pref_compBunki", String.valueOf(pref_compBunki));			//pref_compBunki;		//コンピレーション分岐点
//            dbMsg += ",ロックスクリーンプレイヤー＝" + myPreferences.pref_lockscreen;//////////////
//            bundle.putString("key.pref_lockscreen", String.valueOf(myPreferences.pref_lockscreen));
//            dbMsg += ",ノティフィケーションプレイヤー＝" + myPreferences.pref_notifplayer;//////////////
//            bundle.putString("key.pref_notifplayer", String.valueOf(myPreferences.pref_notifplayer));
//            dbMsg += dbMsg +",Bluetoothの接続に連携="+ myPreferences.pref_bt_renkei;				///////////////
//            bundle.putString("key.pref_bt_renkei", String.valueOf(myPreferences.pref_bt_renkei));
////            dbMsg= dbMsg +",visualizerType="+ visualizerType;				///////////////
////            bundle.putString("visualizerType", String.valueOf(visualizerType) );
//            data.putExtras(bundle);
            setResult(RESULT_OK, data);		// setResult() で bundle を載せた送るIntent dataをセットする	// 第一引数は…Activity.RESULT_OK, Activity.RESULT_CANCELED など
            myLog(TAG,dbMsg);
            SettingActivity.this.finish();
        } catch (Exception e) {		//汎用
            myErrorLog(TAG,dbMsg+"で"+e.toString());
        }
    }

    boolean kariBool =false;

    /**
     * NumberPickerの変更反映
     * 変更値をObjectからbooleanにキャストしてプリファレンスに書き込む
     * 項目追加時は個々の変数にここで設定
     * */
//    private Preference.OnPreferenceChangeListener numberPickerListener =new Preference.OnPreferenceChangeListener(){			//NumberPickerの PreferenceChangeリスナー
//        @Override
//        public boolean onPreferenceChange(Preference preference, Object newValue) {
//            final String TAG = "numberPickerListener";
//            String dbMsg="[SettingActivity]onPreferenceChange:";
//            try{
//                String keyName =  preference.getKey();
//                dbMsg= keyName + ";" + newValue;
//                String atai = String.valueOf(newValue) ;
//                dbMsg +=";" + atai;
//                if( keyName.equals("myPreferences.pref_gyapless") ){											//クロスフェード時間
//                    myPreferences.pref_gyapless = String.valueOf(pTF_myPreferences.pref_gyapless.getText());
////					myPreferences.pref_gyapless = String.valueOf(pTF_myPreferences.pref_gyapless.retValue(Integer.valueOf(atai)));
//                    dbMsg +=",クロスフェード時間=" + myPreferences.pref_gyapless;
//                    pTF_myPreferences.pref_gyapless.setText(myPreferences.pref_gyapless +  getResources().getString(R.string.pp_msec));
//                    atai = myPreferences.pref_gyapless;
////				}else if(keyName.equals("pref_compBunki")){								//コンピレーション
////					pref_compBunki = String.valueOf(pTF_pref_compBunki.getText());
//////					pref_compBunki = String.valueOf(pTF_pref_compBunki.retValue(Integer.valueOf(atai)));
////					dbMsg +=",コンピレーション=" + pref_compBunki;
////					pTF_pref_compBunki.setSummary(pref_compBunki +  "%");
////					atai = pref_compBunki;
////				}else if(keyName.equals("pref_artist_bunnri")){						//アーティストリストを分離する曲数
////					pref_artist_bunnri = String.valueOf(pTF_pref_artist_bunnri.getText());
//////					pref_artist_bunnri = String.valueOf(pTF_pref_artist_bunnri.retValue(Integer.valueOf(atai)));
////					dbMsg +=",アーティストリストを分離する曲数=" + pref_artist_bunnri;
////					pTF_pref_artist_bunnri.setSummary(pref_artist_bunnri + getResources().getString(R.string.comon_kyoku));
////					atai = pref_artist_bunnri;
//                }else if(keyName.equals("myPreferences.pref_saikin_tuika")){							//最近追加リストのデフォルト枚数
//                    myPreferences.pref_saikin_tuika = String.valueOf(pTF_prefsaikin_tuika.getText());
////					myPreferences.pref_saikin_tuika = String.valueOf(pTF_prefsaikin_tuika.retValue(Integer.valueOf(atai)));
//                    dbMsg +=",最近追加リストのデフォルト日数=" + myPreferences.pref_saikin_tuika;
////					pTF_prefsaikin_tuika.setSummary(myPreferences.pref_saikin_tuika +  getResources().getString(R.string.common_nitibun));
//                    atai = myPreferences.pref_saikin_tuika;
//                }else if(keyName.equals("myPreferences.pref_saikin_sisei")){							//最近再生リストのデフォルト曲数
//                    myPreferences.pref_saikin_sisei = String.valueOf(pTF_prefsaikin_sisei.getText());
////					myPreferences.pref_saikin_sisei = String.valueOf(pTF_prefsaikin_sisei.retValue(Integer.valueOf(atai)));
//                    dbMsg +=",最近再生リストのデフォルト曲数=" + myPreferences.pref_saikin_sisei;
////					pTF_prefsaikin_sisei.setSummary(myPreferences.pref_saikin_sisei + getResources().getString(R.string.common_nitibun));
//                    atai = myPreferences.pref_saikin_sisei;
//                }else if(keyName.equals("myPreferences.pref_rundam_list_size")){							//ランダム再生の設定曲数
//                    myPreferences.pref_rundam_list_size = String.valueOf(pTF_rundam_list_size.getText());
//                    dbMsg +=",ランダム再生の設定曲数=" + myPreferences.pref_rundam_list_size;
////					pTF_rundam_list_size.setSummary(myPreferences.pref_rundam_list_size +  getResources().getString(R.string.pp_msec));
//                    atai = myPreferences.pref_rundam_list_size;
//                }
//                prefItemuKakikomi(keyName ,atai);
//                myLog(TAG,dbMsg);
//            } catch (Exception e) {
//                myErrorLog(TAG,dbMsg+"で"+e);
//            }
//            return true;
//        }
//    };
//
//    /**
//     * SwitchPreferenceCompatの変更反映
//     * 変更値をObjectからbooleanにキャストしてプリファレンスに書き込む
//     * 項目追加時は個々の変数にここで設定
//     * */
//    private Preference.OnPreferenceChangeListener switchListener =new Preference.OnPreferenceChangeListener(){	// SwitchPreferenceCompatの PreferenceChangeリスナー
//        @Override
//        public boolean onPreferenceChange(Preference preference, Object newValue) {
//            final String TAG = "switchListener";
//            String dbMsg="[SettingActivity]onPreferenceChange:";
//            try{
//                String keyName =  preference.getKey();
//                dbMsg= keyName + ";" + newValue;
//                boolean atai = (boolean) newValue;
//                dbMsg +=";" + atai;
//                if( keyName.equals("myPreferences.pref_list_simple") ){
//                    myPreferences.pref_list_simple = atai;
//                    dbMsg +=",シンプルなリスト表示=" + myPreferences.pref_list_simple;
//                }else if( keyName.equals("myPreferences.pref_pb_bgc") ){
//                    myPreferences.pref_pb_bgc = atai;
//                    dbMsg +=",プレイヤーの背景=" + myPreferences.pref_pb_bgc;
//                }else if( keyName.equals("myPreferences.pref_notifplayer") ){
//                    myPreferences.pref_notifplayer = atai;
//                    dbMsg +=",ノティフィケーションプレイヤー=" + myPreferences.pref_notifplayer;
//                }else if( keyName.equals("myPreferences.pref_lockscreen") ){
//                    myPreferences.pref_lockscreen = atai;
//                    dbMsg +=",ロックスクリーンプレイヤー=" + myPreferences.pref_lockscreen;
////				}else if( keyName.equals("pref_cyakusinn_fukki") ){
////					pref_cyakusinn_fukki = atai;
////					dbMsg +=",通話連携=" + pref_cyakusinn_fukki;
//                }else if( keyName.equals("myPreferences.pref_bt_renkei") ){
//                    myPreferences.pref_bt_renkei = atai;
//                    dbMsg +=",Bluetooth連携=" + myPreferences.pref_bt_renkei;
//                }
//                prefBoolKakikomi(keyName ,atai);
//                myLog(TAG,dbMsg);
//            } catch (Exception e) {
//                myErrorLog(TAG,dbMsg+"で"+e);
//            }
//            return true;
//        }
//    };
//
//    private Preference.OnPreferenceChangeListener listPreference_OnPreferenceChangeListener =new Preference.OnPreferenceChangeListener(){	// リストPreferenceの　PreferenceChangeリスナー
//        @Override
//        public boolean onPreferenceChange(Preference preference, Object newValue) {
//            return listPreference_OnPreferenceChange(preference,newValue);
//        }
//    };
//
//    private boolean listPreference_OnPreferenceChange(Preference preference, Object newValue){
//        final String TAG = "listPreference_OnPreferenceChange[SettingActivity]";
//        String dbMsg="";
//        try{
//            dbMsg="newValue="+ newValue;
//            ListPreference listpref =(ListPreference)preference;
//            dbMsg +=",listpref="+ listpref;
////			String summary = String.format("entry=%s , value=%s", listpref.getEntry(),listpref.getValue());
////			dbMsg +=",summary="+ summary;
//            preference.setSummary((CharSequence) newValue);
//            if(newValue.equals(getString(R.string.pref_effect_vi_fft))){				//スペクトラムアナライザ風
//                visualizerType = MyConstants.Visualizer_type_FFT;
//            } else if(newValue.equals(getString(R.string.pref_effect_vi_wave))){		//オシロスコープ風
//                visualizerType = MyConstants.Visualizer_type_wave;
//            } else if(newValue.equals(getString(R.string.comon_tukawanai))){			//使わない
//                visualizerType = MyConstants.Visualizer_type_none;
//            }
//            dbMsg +=",visualizerType="+ visualizerType;
////			String effectMsg = (String) pPS_pref_effect.getSummary();				//サウンドエフェクト
////			effectMsg = effectMsg.substring(0, effectMsg.indexOf(getString(R.string.pref_effect_vi))) + "\n" +
////					getString(R.string.pref_effect_vi) + ";" + newValue ;
////			pPS_pref_effect.setSummary(effectMsg);
//            myLog(TAG,dbMsg);
//        } catch (Exception e) {
//            myErrorLog(TAG,dbMsg+"で"+e);
//        }
//        return true;
//    }
//
    //メニューボタンで表示するメニュー///////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu flMenu) {
        //	//Log.d("onCreateOptionsMenu","NakedFileVeiwActivity;mlMenu="+flMenu);
        getMenuInflater().inflate(R.menu.pref_menu , flMenu);		//メニューリソースの使用
        return super.onCreateOptionsMenu(flMenu);
    }

    public boolean makeOptionsMenu(Menu flMenu) {	//ボタンで表示するメニューの内容
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu flMenu) {			//表示直前に行う非表示や非選択設定
        if(locale.equals( Locale.JAPAN)){										//日本語の場合のみconstant for ja_JP.
            flMenu.findItem(R.id.menu_item_sonota_help).setEnabled(true);		//ヘルプ表示	MENU_HELP
        }else{
            flMenu.findItem(R.id.menu_item_sonota_help).setEnabled(false);		//ヘルプ表示	MENU_HELP
        }
        flMenu.findItem(R.id.menu_item_sonota_end).setEnabled(true);	//終了	MENU_END
        return true;
    }

    /**actionBar.setDisplayHomeAsUpEnabled(true);で表示したボタンのクリック*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final String TAG = "onOptionsItemSelected";
        String dbMsg="";
        dbMsg+=ORGUT.nowTime(true,true,true);/////////////////////////////////////
        try{
            Intent intentPRF;
            dbMsg += "MenuItem"+item.getItemId()+"="+item.toString();////////////////////////////////////////////////////////////////////////////
            String helpURL;
            int nowSelectMenu = item.getItemId();
            myLog(TAG,dbMsg);
//            switch (nowSelectMenu) {
//                case R.id.menu_item_sonota_help:						//ヘルプ表示	MENU_HELP
//                    Intent intentWV = new Intent(SettingActivity.this,wKit.class);			//webでヘルプ表示
//                    if(locale.equals( Locale.JAPAN)){										//日本語の場合のみconstant for ja_JP.
//                        helpURL = "file:///android_asset/pref.html";		//日本語ヘルプ
//                        intentWV.putExtra("dataURI",helpURL);		//"file:///android_asset/index.html"
//                    }else {
//                        helpURL = "file:///android_asset/en/pref.html";	//英語ヘルプ
//                    }
//                    startActivity(intentWV);
//                    return true;
//                case android.R.id.home:
//                case R.id.menu_item_sonota_end:					//終了	MENU_END
                    quitMe();		//このアプリを終了する
//                    return true;
//            }
            return false;
        } catch (Exception e) {
            myErrorLog(TAG,dbMsg +"で"+e.toString());
            return false;
        }
    }

    @Override
    public void onOptionsMenuClosed(Menu flMenu) {
        //Log.d("onOptionsMenuClosed","NakedFileVeiwActivity;mlMenu="+flMenu);
    }
    //このアプリを終了する/////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////メニューボタンで表示するメニュー//
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {	 // ヘッドのイメージは実際にローディンされた時点で設定表示と同時にウィジェットの高さや幅を取得したいときは大抵ここで取る。
        if (hasFocus) {
            final String TAG = "onWindowFocusChanged";
            String dbMsg="";
            try{
                myLog(TAG,dbMsg);
                //			reDrowView();				//プリファレンス画面表示
            }catch (Exception e) {
                myErrorLog(TAG,dbMsg + "で"+e.toString());
            }
        }
        super.onWindowFocusChanged(hasFocus);
    }
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent rData) { // startActivityForResult で起動させたアクティビティがfinish() により破棄されたときにコールされる
//	// requestCode : startActivityForResult の第二引数で指定した値が渡される
//	// resultCode : 起動先のActivity.setResult の第一引数が渡される
//	// Intent data : 起動先Activityから送られてくる Intent
//		super.onActivityResult(requestCode, resultCode, rData);
//		final String TAG = "setHeadImgList";
//		String dbMsg="";
//		try{
//			dbMsg += "requestCode="+requestCode+",resultCode="+resultCode+",rData="+rData;//////////////////////////////////////////////////////////////////////////////
//			Bundle bundle = null ;
//			if(rData != null){
//				bundle = rData.getExtras();
//				if(resultCode == RESULT_OK){			//-1
////					switch(requestCode) {
////					case R.string.pref_reset:						//));				//ダイアログタイトル ;設定を初期化
////						pref_reset= true;
////						dbMsg +=pref_reset;//////////////////
////						myLog(TAG,dbMsg);
////						prefBoolKakikomi("pref_reset" ,pref_reset);
////						break;
////					}
//				}
//			}
//			myLog(TAG,dbMsg);
//		} catch (Exception e) {		//汎用
//			myErrorLog(TAG,dbMsg+"で"+e.toString());
//		}
//	}		//http://fernweh.jp/b/startactivityforresult/

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        final String TAG = "onResume";
        String dbMsg="";
        try{
            myLog(TAG,dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG,dbMsg + "で"+e.toString());
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onPause() {
        super.onPause();
        final String TAG = "onResume";
        String dbMsg="";
        try{
            //		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
            myLog(TAG,dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG,dbMsg + "で"+e.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        quitMe();
        myLog("onDestroy","onDestroyが発生");
    }

    ///////////////////////////////////////////////////////////////////////////////////
    public static void myLog(String TAG , String dbMsg) {
        MyUtil MyUtil = new MyUtil();
        MyUtil.myLog(TAG , "[SettingActivity]" + dbMsg);
    }

    public static void myErrorLog(String TAG , String dbMsg) {
        MyUtil MyUtil = new MyUtil();
        MyUtil.myErrorLog(TAG , "[SettingActivity]"+ dbMsg);
    }

}

//http://yan-note.blogspot.jp/2010/09/android_12.html
//http://android.roof-balcony.com/shori/strage/localfile-2/}