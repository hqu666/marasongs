package com.hijiyam_koubou.marasongs;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.audiofx.PresetReverb;
import android.os.Build;
import android.os.Bundle;

public class MyPreferences extends PreferenceActivity {
	public OrgUtil ORGUT;		//自作関数集
	public MaraSonActivity MSA;
	public Locale locale;	// アプリで使用されているロケール情報を取得
	public Context rContext ;							//Context
	public SharedPreferences main_pref;
	public Editor myEditor;//=LAO.myEditor;
	public String pref_apiLv  = "14";							//APIレベル
	public int pref_sonota_vercord =0;				//このアプリのバージョンコード
	public String pref_compBunki = null;			//コンピレーション設定[%]
	public String pref_gyapless = null;			//クロスフェード時間
	public boolean pref_list_simple =true;				//シンプルなリスト表示（サムネールなど省略）
	public boolean pref_pb_bgc = true;				//プレイヤーの背景	true＝Black"	http://techbooster.jpn.org/andriod/ui/10152/

	public String pref_artist_bunnri = null;		//アーティストリストを分離する曲数
	public String pref_saikin_tuika = null;			//最近追加リストのデフォルト枚数
	public String pref_saikin_sisei = null;		//最近再生加リストのデフォルト枚数
	public String pref_rundam_list_size =null;	//ランダム再生リストアップ曲数
	public int repeatType;							//リピート再生の種類
	public boolean rp_pp;							//2点間リピート中
	public String pp_start;							//リピート区間開始点
	public String pp_end;								//リピート区間終了点

	public boolean pref_lockscreen =true;				//ロックスクリーンプレイヤー</string>
	public boolean pref_notifplayer =true;				//ノティフィケーションプレイヤー</string>
	public boolean pref_cyakusinn_fukki=true;		//終話後に自動再生
	public boolean pref_bt_renkei =true;				//Bluetoothの接続に連携して一時停止/再開
	public boolean pref_reset = false;					//設定を初期化
	public boolean pref_listup_reset = false;			//調整リストを初期化

	public String saisei_fname =null;				//再生中のファイル名
	public String pref_saisei_jikan ="0";			//再開時間		Integer
	public String pref_saisei_nagasa  ="0";		//再生時間
	public String pref_zenkai_saiseKyoku ="0";			//前回の連続再生曲数
	public String pref_zenkai_saiseijikann ="0";			//前回の連続再生時間

	public String pref_file_in ="";		//内蔵メモリ
	public String pref_file_ex="";		//メモリーカード
	public String pref_file_wr="";		//設定保存フォルダ
	public String pref_file_kyoku="";		//総曲数
	public String pref_file_album="";		//総アルバム数
	public String pref_file_saisinn="";	//最新更新日

	public String nowList_id;				//再生中のプレイリストID	playListID
	public String nowList;					//再生中のプレイリスト名	playlistNAME
	public String play_order;
	//アーティストごとの情報
	public String artistID;
	//アルバムごとの情報
	public String albumID;
	//曲ごとの情報
	public String audioID;
	public String dataURL = null;
	public String b_List =null;			//前に再生していたプレイリスト
	public int b_List_id = 0;			//前のプレイリストID
	public int modori_List_id = 0;			//リピート前のプレイリストID
	private int b_index;				//前の曲順

	public String prBTname = "";			//前回接続していたBluetooth機器名
	public String prBTAdress = "";			//MACアドレス（機器固有番号）
	public String pMusic_dir = "";			//再生する音楽ファイルがあるフォルダ
	public String pplist_usb = "";			//再生する音楽ファイルがあるUSBメモリーのフォルダ
	public String pplist_rquest = "";		//リクエストリスト
	public String pplist_a_new = "";		//新規プレイリスト

	public List<String> pref_toneList;		//プリファレンス保存用トーンリスト
	public String toneSeparata = "L";
	public String tone_name;				//トーン名称
	public boolean bBoot = false;					//バスブート
	public short reverbBangou = 0;				//リバーブ効果番号
	public int visualizerType = MaraSonActivity.Visualizer_type_FFT;		//VisualizerはFFT

	public Boolean prTT_dpad = false;		//ダイヤルキーの有無
	public String Siseiothers ="";				//レジューム再生の情報
	public String others ="";				//その他の情報

	public ListPreference sumList;
	public EditTextPreference sumEdit;

	public PreferenceScreen pPS_pref_player;			//プレイヤー設定
	public NumberPickerPreference pTF_pref_gyapless;			//クロスフェード時間
	public NumberPickerPreference pTF_pref_compBunki;			//コンピレーション分岐点
	public SwitchPreference pcb_list_simple;				//シンプルなリスト表示（サムネールなど省略）
	public SwitchPreference pTF_pref_pb_bgc;				//プレイヤーの背景は白

	public PreferenceScreen pPS_pref_plist;			//プレイリスト設定
	public NumberPickerPreference pTF_pref_artist_bunnri;		//アーティストリストを分離する曲数
	public NumberPickerPreference pTF_prefsaikin_tuika;		//最近追加リストのデフォルト枚数
	public NumberPickerPreference pTF_prefsaikin_sisei;		//最近再生リストのデフォルト曲数
	public NumberPickerPreference pTF_rundam_list_size;				//ランダム再生の設定曲数
	public EditTextPreference  pref_nitenka_memo;								//二点間再生状況

	public PreferenceScreen pPS_pref_effect;										//サウンドエフェクト
	public ListPreference pLi_pref_effect_vi;								//ビジュアライザー
	public EditTextPreference pref_eff_memo;								//サウンドエフェクトの設定確認

	public PreferenceScreen pPS_pref_kisyubetu;		//機種別調整
	public SwitchPreference pcb_pref_lockscreen;			//ロックスクリーンプレイヤー</string>
	public SwitchPreference pcb_pref_notifplayer;		//ノティフィケーションプレイヤー</string>
	public SwitchPreference pcb_bt_renkei;				//Bluetoothの接続に連携して一時停止/再開
	public SwitchPreference pCB_pref_cyakusinn_fukki;	//終話後に自動再生

	public EditTextPreference pTFBN_name ;					//前回接続していたBluetooth機器名
	public EditTextPreference pTFMac_address;		//MACアドレス（機器固有番号）
	public EditTextPreference pTF_music_dir;		//再生する音楽ファイルがあるフォルダ	public PreferenceScreen pPS_sonota;			//その他　のプリファレンススクリーン
	public EditTextPreference pTF_plist_usb;		//再生する音楽ファイルがあるUSBメモリーのフォルダ
	public EditTextPreference pTF_plist_rquest;	//リクエストリスト
	public EditTextPreference pTF_plist_a_new;		//新規プレイリスト
	public PreferenceScreen pPS_taisyou_type;		//再生する音楽ファイルの種類（拡張子指定）のプリファレンススクリーン
	public SwitchPreference pCB_pref_reset;				//設定消去
	public SwitchPreference pCB_pref_listup_reset;		//調整リストのリセット
	public Preference pPS_sonota;		//その他　のプリファレンススクリーン
	public EditTextPreference pref_memo;							//その他の項目列記

	public Map<String, ?> keys;
	public EditTextPreference pEdit;
	public CheckBoxPreference pCB;			//汎用

///外部から呼ばれる時の動作//////////////////////////////
	int reqCode = 0;		//何のリストか

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		final String TAG = "onKeyDown[MyPreferences]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg = "keyCode="+keyCode+",getDisplayLabel="+event.getDisplayLabel()+",getAction="+event.getAction();////////////////////////////////
	//		myLog(TAG,dbMsg);
				switch (keyCode) {
				case KeyEvent.KEYCODE_BACK:	//バック；4
					modori();	// 呼出し元への戻し処理
					break;
				case KeyEvent.KEYCODE_DPAD_UP:		//マルチガイド上；19
				case KeyEvent.KEYCODE_DPAD_DOWN:	//マルチガイド下；20
				case KeyEvent.KEYCODE_DPAD_LEFT:	//マルチガイド左；21
				case KeyEvent.KEYCODE_DPAD_RIGHT:	//マルチガイド右；22
					prTT_dpad=true;
					break;
				default:
					prTT_dpad=false;			//ダイヤルキーは使えない
					break;
				}
				prefItemuKakikomi("pref_sonota_dpad" ,String.valueOf(prTT_dpad));									//プリファレンス全項目読出し
	//		myLog("onKeyDown","ダイヤルキーは"+prTT_dpad);
		} catch (NullPointerException e) {
			myErrorLog(TAG,"エラー発生；"+e.toString());
	//		return false;
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
		final String TAG = "onCreate[MyPreferences]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			long start = System.currentTimeMillis();		// 開始時刻の取得
			ORGUT = new OrgUtil();		//自作関数集
			main_pref = getSharedPreferences(getResources().getString(R.string.pref_main_file),MODE_PRIVATE);		//	getSharedPreferences(prefFname,MODE_PRIVATE);
			myEditor = main_pref.edit();
			locale = Locale.getDefault();		// アプリで使用されているロケール情報を取得
			dbMsg= "locale="+locale;/////////////////////////////////////

			addPreferencesFromResource(R.xml.preferences);
	//ava.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.String
			//☆再インストールで解決
	//		myLog(TAG,dbMsg);
			pPS_pref_player = (PreferenceScreen)findPreference("pref_player");										//プレイヤー設定
				pTF_pref_gyapless= (NumberPickerPreference) findPreference("pref_gyapless");							//クロスフェード時間
				pTF_pref_pb_bgc= (SwitchPreference) findPreference("pref_pb_bgc");									//プレイヤーの背景は白
				pTF_pref_compBunki = (NumberPickerPreference) findPreference("pref_compBunki");					//コンピレーション分岐点
				pcb_list_simple = (SwitchPreference) findPreference("pref_list_simple");						//シンプルなリスト表示（サムネールなど省略）
//				pcb_list_simple.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
//					@Override
//					public boolean onPreferenceChange(Preference preference, Object newValue) {
//						final String TAG = "pcb_list_simple[MyPreferences]";
//						String dbMsg="開始";/////////////////////////////////////
//						try{
//							dbMsg= preference.getKey() + ";" + String.valueOf(newValue);
//							pref_list_simple = (boolean) newValue;		//Boolean.valueOf(newValue);				//シンプルなリスト表示（サムネールなど省略）
//							dbMsg= dbMsg + ",pref_list_simple=" + pref_list_simple;
//							myLog(TAG,dbMsg);
//						}catch (Exception e) {
//							myErrorLog(TAG,dbMsg + "で"+e.toString());
//						}
//						return bBoot;
//					}
//				});
			pPS_pref_plist = (PreferenceScreen)findPreference("pref_plist");										//プレイリスト設定
			pTF_pref_artist_bunnri = (NumberPickerPreference) findPreference("pref_artist_bunnri");					//アーティストリストを分離する曲数
			pTF_prefsaikin_tuika = (NumberPickerPreference) findPreference("pref_saikin_tuika");					//最近追加リストのデフォルト枚数
			pTF_prefsaikin_sisei = (NumberPickerPreference) findPreference("pref_saikin_sisei");					//最近再生リストのデフォルト曲数
			pTF_rundam_list_size = (NumberPickerPreference) findPreference("pref_rundam_list_size");				//ランダム再生の設定曲数
			pref_nitenka_memo = (EditTextPreference) findPreference("pref_nitenka_memo");								//二点間再生状況
//				pref_nitenkan = (CheckBoxPreference) findPreference("pref_nitenkan");								//二点間再生中
//				pref_nitenkan_start = (EditTextPreference) findPreference("pref_nitenkan_start");					//二点間再生開始点
//				pref_nitenkan_end = (EditTextPreference) findPreference("pref_nitenkan_end");						//二点間再生終了点

			pPS_pref_effect = (PreferenceScreen)findPreference("pref_effect");										//サウンドエフェクト
			pLi_pref_effect_vi = (ListPreference) findPreference("visualizerType");								//ビジュアライザー
			pLi_pref_effect_vi.setOnPreferenceChangeListener(listPreference_OnPreferenceChangeListener);
			pref_eff_memo = (EditTextPreference) findPreference("pref_eff_memo");								//サウンドエフェクトの設定確認

			pPS_pref_kisyubetu = (PreferenceScreen)findPreference("pref_kisyubetu");							//機種別調整
			pcb_pref_notifplayer = (SwitchPreference) findPreference("pref_notifplayer");				//ノティフィケーションプレイヤー</string>
			dbMsg= dbMsg + "SDK_INT="+String.valueOf(android.os.Build.VERSION.SDK_INT);/////////////////////////////////////
			if (android.os.Build.VERSION.SDK_INT <11 ) {
				pcb_pref_notifplayer.setEnabled(false);
			}
			pcb_pref_lockscreen = (SwitchPreference) findPreference("pref_lockscreen");				//ロックスクリーンプレイヤー</string>
			if (android.os.Build.VERSION.SDK_INT < 14 ) {										// registerRemoteControlClient
				pcb_pref_lockscreen.setEnabled(false);
			}
			pcb_bt_renkei =(SwitchPreference) findPreference("pref_bt_renkei");							//Bluetoothの接続に連携して一時停止/再開
			pCB_pref_cyakusinn_fukki = (SwitchPreference) findPreference("pref_cyakusinn_fukki");		//終話後に自動再生

			pPS_sonota =  (PreferenceScreen)findPreference("pref_sonota");				//その他　のプリファレンススクリーン
				pCB_pref_reset = (SwitchPreference) findPreference("pref_reset");		//設定消去
				pCB_pref_listup_reset = (SwitchPreference) findPreference("pref_listup_reset");		//調整リストのリセット
			pref_memo= (EditTextPreference) findPreference("pref_memo");							//その他の項目列記

			pTF_pref_gyapless.setOnPreferenceChangeListener(numberPickerListener);
			pTF_pref_compBunki.setOnPreferenceChangeListener(numberPickerListener);
			pTF_pref_artist_bunnri.setOnPreferenceChangeListener(numberPickerListener);				//アーティストリストを分離する曲数
			pTF_prefsaikin_tuika.setOnPreferenceChangeListener(numberPickerListener);					//最近追加リストのデフォルト枚数
			pTF_prefsaikin_sisei.setOnPreferenceChangeListener(numberPickerListener);					//最近再生リストのデフォルト曲数
			pTF_rundam_list_size.setOnPreferenceChangeListener(numberPickerListener);				//ランダム再生の設定曲数

			pcb_list_simple.setOnPreferenceChangeListener(switchListener);
			pTF_pref_pb_bgc.setOnPreferenceChangeListener(switchListener);
			pcb_pref_notifplayer.setOnPreferenceChangeListener(switchListener);
			pcb_pref_lockscreen.setOnPreferenceChangeListener(switchListener);
			pCB_pref_cyakusinn_fukki.setOnPreferenceChangeListener(switchListener);
			pcb_bt_renkei.setOnPreferenceChangeListener(switchListener);
			pCB_pref_reset.setOnPreferenceChangeListener(switchListener);
			pCB_pref_listup_reset.setOnPreferenceChangeListener(switchListener);

			Bundle extras = getIntent().getExtras();
			reqCode=extras.getInt("reqCode");				//何のリストか
			dbMsg= dbMsg + "reqCode="+String.valueOf(reqCode);/////////////////////////////////////
			pref_apiLv =extras.getString("pref_apiLv");		//APIL
			dbMsg= dbMsg + "pref_apiLv="+String.valueOf(pref_apiLv);/////////////////////////////////////

			pref_sonota_vercord = extras.getInt("pref_sonota_vercord") ;
			dbMsg= dbMsg + ",このアプリのバージョンコード="+String.valueOf(pref_sonota_vercord);/////////////////////////////////////
			prTT_dpad = extras.getBoolean("prTT_dpad");		//ダイアルキー有り
			pref_gyapless =extras.getString("pref_gyapless");			//クロスフェード時間
			pref_compBunki =extras.getString("pref_compBunki");		//コンピレーション分岐点
			pref_pb_bgc = extras.getBoolean("pref_pb_bgc");		//プレイヤーの背景	true＝Black"	http://techbooster.jpn.org/andriod/ui/10152/

			pref_list_simple = extras.getBoolean("pref_list_simple");		//シンプルなリスト表示（サムネールなど省略）
			saisei_fname =extras.getString("saisei_fname");
//			pref_artist_name =extras.getString("pref_artist_name");			//リスト表示するアーティスト名
			pref_saisei_jikan =extras.getString("pref_saisei_jikan");		//再開時間		Integer
			pref_saisei_nagasa =extras.getString("pref_saisei_nagasa");			//再生時間
			pref_bt_renkei =extras.getBoolean("pref_bt_renkei");			//Bluetoothの接続に
			pref_zenkai_saiseKyoku = extras.getString("pref_zenkai_saiseKyoku");		//前回の連続再生曲数
			pref_zenkai_saiseijikann = extras.getString("pref_zenkai_saiseijikann");		//前回の連続再生時間

			pref_cyakusinn_fukki = extras.getBoolean("pref_cyakusinn_fukki");		//終話後に自動再生

			dbMsg = "reqCode=" + reqCode;//////////////////
			switch(reqCode) {
			case R.id.menu_item_sonota_settei:			//);						//設定;
				prefHyouji();				//プリファレンス画面表示
				break;
//			case R.id.menu_sonota_settei_syoukyo:			//設定消去";
//				delPrif();		//プリファレンスの内容削除
//				break;
			default:
				prefHyouji();				//プリファレンス画面表示
				break;
			}
			long end=System.currentTimeMillis();		// 終了時刻の取得
			dbMsg=dbMsg+(int)((end - start)) + "m秒で表示終了";
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	public void prefHyouji() {				//プリファレンス画面表示
		final String TAG = "prefHyouji[MyPreferences]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			long start = System.currentTimeMillis();		// 開始時刻の取得
			readPrif();		//プリファレンスの読込み
			viewSakusei();				//プリファレンスの表示処理
			long end=System.currentTimeMillis();		// 終了時刻の取得
			dbMsg=dbMsg+(int)((end - start)) + "m秒で表示終了";
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	public void viewSakusei( ) {				//プリファレンスの表示処理
		//<<onSharedPreferenceChanged , prefItemuKakikomi , onWindowFocusChanged , viewSakusei
		final String TAG = "viewSakusei[MyPreferences]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			MSA = new MaraSonActivity();
			String wrStr= null;
			String playerMsg =null;	//プレイヤー設定//////////////////////////////////////////////////////////
			if(pref_gyapless != null){
				dbMsg = "クロスフェード時間" + pref_gyapless;//////////////////pTF_pref_gyapless
				playerMsg =  getResources().getString(R.string.pref_gyapless) + pref_gyapless ;
			}else{
				playerMsg = "0" ;
			}
			playerMsg =  playerMsg +  getResources().getString(R.string.pp_msec)  + "\n" ;
			if(pref_compBunki != null){
				dbMsg += "コンピレーション分岐点" + pref_compBunki;//////////////////pTF_pref_compBunki
				playerMsg = playerMsg + getResources().getString(R.string.pref_compBunki);
			}else{
				playerMsg = playerMsg + "0" ;
			}
			playerMsg = playerMsg + pref_compBunki + "[%}"  + "\n" ;
			dbMsg += ",シンプルなリスト表示=" + pref_list_simple;/////////////////
			pcb_list_simple.setChecked(pref_list_simple);			//シンプルなリスト表示（サムネールなど省略）
			playerMsg=playerMsg+getString(R.string.pref_list_simple_title)+"=" ;			//+ pcb_list_simple.getSummary() +"\n";			//="">リスト表示</string>
			if(pref_list_simple){
				playerMsg=playerMsg+getString(R.string.pref_list_simple_summaryOn) +"\n";			//	<string name="">アイテムだけ</string>
			}else{
				playerMsg=playerMsg+getString(R.string.pref_list_simple_summaryOff) +"\n";			//	="">詳細表示</string>
			}
//			dbMsg +=  "最近追加リストのデフォルト枚数=" + pref_saikin_tuika ;////////////////////////////////////////////////////////////////////////////
//			playerMsg=playerMsg+getString(R.string.playlist_namae_saikintuika) + pTF_prefsaikin_tuika.getSummary()+getString(R.string.pp_mai) ;
//			dbMsg +=  "最近再生リストのデフォルト曲数=" + pref_saikin_sisei ;////////////////////////////////////////////////////////////////////////////
//			playerMsg=playerMsg+getString(R.string.playlist_namae_saikintuika) + pTF_prefsaikin_sisei.getSummary()+getString(R.string.pp_kyoku) + "\n" ;
			dbMsg += ",プレイヤーの背景は白=" + pref_pb_bgc;/////////////////
			pTF_pref_pb_bgc.setChecked(pref_pb_bgc);	//プレイヤーの背景は白
			playerMsg=playerMsg +getString(R.string.pref_pb_bgc_titol) + "=" + pTF_pref_pb_bgc.getSummary() +"\n";
			dbMsg +="\n"+playerMsg;				//+";"+pTF_saisei_jikan.getText();//////////////////
			if(playerMsg != null){
				pPS_pref_player.setSummary("");					//☆一旦消して書き直す
				pPS_pref_player.setSummary(playerMsg);		//プレイヤー設定	☆xmlでandroid:enabled=trueにしないと書き換わらない
			}

			String effectMsg = viewSakusei_eff( );				//エフェクト部のプリファレンス表示処理
			pPS_pref_effect.setSummary(effectMsg);															//サウンドエフェクト

			String kisyubetu =null;	//機種別調整//////////////////////////////////////////////////////////
			dbMsg += ",ノティフィケーションプレイヤー＝" + pref_notifplayer;//////////////
			pcb_pref_notifplayer.setChecked(pref_notifplayer);
			kisyubetu=getString(R.string.pref_notifplayer) + "=" + pcb_pref_notifplayer.getSummary() +"\n";
			dbMsg += ",ロックスクリーンプレイヤー＝" + pref_lockscreen;//////////////
			pcb_pref_lockscreen.setChecked(pref_lockscreen);
			kisyubetu=kisyubetu+getString(R.string.pref_lockscreen) + "=" + pcb_pref_lockscreen.getSummary() +"\n";
			dbMsg += ",Bluetoothの接続に連携して一時停止/再開＝" + pref_bt_renkei;//////////////////pcb_bt_renkei
			pcb_bt_renkei.setChecked(pref_bt_renkei);
			kisyubetu=kisyubetu+getString(R.string.pref_bt_renkei_titol) + "=" + pcb_bt_renkei.getSummary() +"\n";
			dbMsg +="終話後に自動再生＝" + pref_cyakusinn_fukki;//////////////////
			dbMsg += " , pCB_pref_cyakusinn_fukki＝" + pCB_pref_cyakusinn_fukki;//////////////////
			pCB_pref_cyakusinn_fukki.setChecked(pref_cyakusinn_fukki);				//;		//終話後に自動再生
			kisyubetu=kisyubetu +getString(R.string.pref_cyakusinn_fukki)+"=" + pCB_pref_cyakusinn_fukki.getSummary() +"\n";
			dbMsg += "\n" + kisyubetu;				//機種別調整////////////////////
			if(kisyubetu != null){
				pPS_pref_kisyubetu.setSummary("");
				pPS_pref_kisyubetu.setSummary(kisyubetu);
			}

			String saisei ="";	//レジューム再生//////////////////////////////////////////////////////////
			if(saisei_fname != null){
				dbMsg +="再生中のファイル名" + saisei_fname;//////////////////
				saisei = saisei_fname +"\n" ;
			}
			if(pref_saisei_jikan != null ){
				wrStr=ORGUT.sdf_mss.format(Long.valueOf(pref_saisei_jikan));
				dbMsg +="再生ポジション；" + pref_saisei_jikan +">>" +wrStr;				//+";"+pTF_saisei_jikan.getText();//////////////////
				saisei = saisei  + getResources().getString(R.string.pref_saisei_come1) +" " + wrStr;
			}
			if(pref_saisei_nagasa != null ){
				wrStr=ORGUT.sdf_mss.format(Long.valueOf(pref_saisei_nagasa));
				dbMsg +="再生時間；" +pref_saisei_nagasa +">>" +wrStr;				//+";"+pTF_saisei_jikan.getText();//////////////////
				saisei = saisei + "/"+wrStr +"]" + "\n";
				dbMsg += saisei;				//+";"+pTF_saisei_jikan.getText();//////////////////
			}
			if( pref_zenkai_saiseKyoku != null ){		//前回の連続再生曲数		pTF_pref_zenkai_saiseKyoku
				dbMsg +="前回の連続再生曲数；" +pref_zenkai_saiseKyoku;				//+";"+pTF_saisei_jikan.getText();//////////////////
				saisei = saisei+getResources().getString(R.string.comon_zennkai) +pref_zenkai_saiseKyoku + getResources().getString(R.string.pp_kyoku) ;
				dbMsg += saisei;				//+";"+pTF_saisei_jikan.getText();//////////////////
			}
			if(pref_zenkai_saiseijikann != null ){
				dbMsg +="前回の連続再生時間；"+ORGUT.sdf_mss.format(Long.valueOf(pref_zenkai_saiseijikann));			///////////////////
				saisei = saisei +ORGUT.sdf_mss.format(Long.valueOf(pref_zenkai_saiseijikann));
				dbMsg +=saisei;				//+";"+pTF_saisei_jikan.getText();//////////////////
			}else{
				pref_zenkai_saiseijikann = "0";
			}
			dbMsg +=saisei;				//+";"+pTF_saisei_jikan.getText();//////////////////
			Siseiothers = Siseiothers + saisei;
			String summelyStr = "";				//その他//////////////////////////////////////////////////
			dbMsg +="pref_apiLv＝" + pref_apiLv;//////////////////
			summelyStr=getString(R.string.pref_sonota_apil)+"="+pref_apiLv;

			dbMsg +="pref_sonota_vercord＝" + pref_sonota_vercord;//////////////////
			summelyStr=summelyStr+" , "+getString(R.string.pref_sonota_vercord)+"="+pref_sonota_vercord;

			dbMsg += ",prTT_dpad＝" + prTT_dpad;//////////////////
			pCB_pref_reset.setChecked(false);		//設定消去
			pCB_pref_reset.setSummary(getString(R.string.comon_sinai));
			others = others+summelyStr;
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	public String viewSakusei_eff( ) {				//エフェクト部のプリファレンス表示処理
		String effectMsg =null;	//サウンドエフェクト設定//////////////////////////////////////////////////////////
		final String TAG = "viewSakusei_eff[MyPreferences]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			String effectMemo =null;
			//		public List<String> pref_toneList;		//プリファレンス保存用トーンリスト
		//	public String toneSeparata = "L";
			dbMsg = "トーン名称=" + tone_name;/////////////////
			if( tone_name == null ){
				tone_name =  getString(R.string.tone_name_puri);				//="">現在の設定</string>
			}
			effectMsg= getString(R.string.comon_genzai)+ ";" +tone_name;					//"">現在の設定</string>
			effectMemo= effectMsg;
			dbMsg += ",pref_toneList=" + pref_toneList;/////////////////
			if(pref_toneList != null){
				for(String wS : pref_toneList){
					dbMsg += ",wS=" + wS;/////////////////
					String wSs[] = wS.split(toneSeparata);
					String frq = wSs[0];
					frq = String.format("%6dHz", Integer.valueOf(frq));	//☆型を確定しないとjava.util.IllegalFormatConversionException: %d can't format java.lang.String arguments
					dbMsg += ",frq=" + frq;/////////////////
					String band = wSs[1];
					int wInt = Integer.valueOf(band);
					if(0 != wInt){
						wInt = wInt/100;
					}
					band = String.format("%6ddb", wInt);
					dbMsg += ",band=" + band;/////////////////
					effectMemo= effectMemo + "\n" + frq + band;
				}
			}
			dbMsg += ",バスブート=" + bBoot;/////////////////
			String wrStr = getString(R.string.comon_tukawanai);			//使わない
			if( bBoot ){
				wrStr = getString(R.string.comon_tukau);			//使う
			}
			effectMsg= effectMsg + "\n" + getString(R.string.effect_bassbost)+ ";" +bBoot;
			effectMemo= effectMemo + "\n" + getString(R.string.effect_bassbost)+ ";" +bBoot;
			dbMsg += ",リバーブ効果番号=" + reverbBangou;/////////////////
			switch(reverbBangou) {
			case PresetReverb.PRESET_NONE:
				wrStr = getString(R.string.reverb_none);
				break;
			case PresetReverb.PRESET_SMALLROOM:
				wrStr = getString(R.string.reverb_small_room);
				break;
			case PresetReverb.PRESET_MEDIUMROOM:
				wrStr = getString(R.string.reverb_medium_room);
				break;
			case PresetReverb.PRESET_LARGEROOM:
				wrStr = getString(R.string.reverb_large_room);
				break;
			case PresetReverb.PRESET_MEDIUMHALL:
				wrStr = getString(R.string.reverb_medium_hall);
			case PresetReverb.PRESET_LARGEHALL:
				wrStr = getString(R.string.reverb_large_hall);
				break;
			case PresetReverb.PRESET_PLATE:
				wrStr = getString(R.string.reverb_plate);
				break;
			default:
				wrStr = getString(R.string.reverb_none);
				break;
			}
			effectMsg= effectMsg + "\n" + getString(R.string.effect_reverb)+ ";" +wrStr;					//me="">リバーブ</string>
			effectMemo= effectMemo + "\n" + getString(R.string.effect_reverb)+ ";" +wrStr;
			pref_eff_memo.setSummary(effectMemo);								//サウンドエフェクトの設定確認
			dbMsg += ",Visualizer=" + visualizerType;/////////////////
			wrStr = getString(R.string.comon_tukawanai);				//"">使わない</string>
			if(-1 < visualizerType){
				switch(visualizerType) {
				case MaraSonActivity.Visualizer_type_wave:						//Visualizerはwave表示
					wrStr = getString(R.string.pref_effect_vi_wave);				//オシロスコープ風
					break;
				case MaraSonActivity.Visualizer_type_FFT:
					wrStr = getString(R.string.pref_effect_vi_fft);				//スペクトラムアナライザ風
					break;
				case MaraSonActivity.Visualizer_type_none:						//Visualizerを使わない
					wrStr = getString(R.string.comon_tukawanai);				//"">使わない</string>
					break;
//				default:
//					break;
				}
			}else{
				wrStr = getString(R.string.comon_tukawanai);				//"">使わない</string>
			}
			dbMsg += "=" + wrStr;/////////////////
			pLi_pref_effect_vi.setSummary(wrStr);;								//ビジュアライザー
			pLi_pref_effect_vi.setDefaultValue(wrStr);						//初期設定　xlmで	android:summary="%s"	Summaryを選択する	が利かなかった

			effectMsg= effectMsg + "\n" + getString(R.string.pref_effect_vi)+ ";" +wrStr;					//"">ビジュアライザー</string>
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
		return effectMsg;
	}

	public String taisyouTypeSmally() {
		String retStr="";
		final String TAG = "taisyouTypeSmally[MyPreferences]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			pCB=(CheckBoxPreference) findPreference("pref_taisyou_type_mp3");
			if(pCB.isChecked()){
				retStr="mp3";
			}
			pCB=(CheckBoxPreference) findPreference("pref_taisyou_type_m4a");		//m4a(AAC;MPEG-4)
			if(pCB.isChecked()){
				retStr=retStr+",m4a";
			}
			pCB=(CheckBoxPreference) findPreference("pref_taisyou_type_wma");
			if(pCB.isChecked()){
				retStr=retStr+",wma";
			}
			pCB=(CheckBoxPreference) findPreference("pref_taisyou_type_wav");
			if(pCB.isChecked()){
				retStr=retStr+",wav";
			}
			pCB=(CheckBoxPreference) findPreference("pref_taisyou_type_ogg");		//ogg(Ogg vorbis )
			if(pCB.isChecked()){
				retStr=retStr+",ogg";
			}
			pCB=(CheckBoxPreference) findPreference("pref_taisyou_type_amr");		//amr
			if(pCB.isChecked()){
				retStr=retStr+",amr";
			}
			pCB=(CheckBoxPreference) findPreference("pref_taisyou_type_3gp");		//3gp(voice recorder,AMR-WB)"
			if(pCB.isChecked()){
				retStr=retStr+",3gp";
			}
			pCB=(CheckBoxPreference) findPreference("pref_taisyou_type_mid");
			if(pCB.isChecked()){
				retStr=retStr+",mid";
			}
			pCB=(CheckBoxPreference) findPreference("pref_taisyou_type_xmf");		//xmf(ringer?)
			if(pCB.isChecked()){
				retStr=retStr+",xmf";
			}
			pCB=(CheckBoxPreference) findPreference("pref_taisyou_type_mxmf");		//mxmf(ringer?)
			if(pCB.isChecked()){
				retStr=retStr+",mid";
			}
			pCB=(CheckBoxPreference) findPreference("pref_taisyou_type_rtttl");		//rtttl(ringer?)
			if(pCB.isChecked()){
				retStr=retStr+",rtttl";
			}
			pCB=(CheckBoxPreference) findPreference("pref_taisyou_type_rlx");		//rlx(ringer?)
			if(pCB.isChecked()){
				retStr=retStr+",rlx";
			}
			pCB=(CheckBoxPreference) findPreference("pref_taisyou_type_ota");		//ota(Over The Air (OTA) image used for sending pictures on Nokia and Siemens mobile phones)
			if(pCB.isChecked()){
				retStr=retStr+",ota";
			}
			pCB=(CheckBoxPreference) findPreference("pref_taisyou_type_imy");		//imy(Monophonic ringtone format developed by the irDa (infrared communications))
			if(pCB.isChecked()){
				retStr=retStr+",imy";
			}
			pCB=(CheckBoxPreference) findPreference("pref_taisyou_type_SMF");		//imy(Monophonic ringtone format developed by the irDa (infrared communications))
			if(pCB.isChecked()){
				retStr=retStr+",smf";
			}
			myLog(TAG,"retStr="+retStr);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
		return retStr;
	}

	@SuppressLint("SimpleDateFormat")
	public void readPrif(){		//プリファレンスの読込み
		final String TAG = "readPrif[MyPreferences]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			String summaryStr = null;				//☆setSummaryする前に完全な文字列にする
			String wrStr;
			String selectStr = null;
			Siseiothers = "";					//レジューム再生の情報
			others = "";				//その他の情報
			Map<String, ?> keys = main_pref.getAll();
			dbMsg = "読み込み開始"+keys.size()+"項目;mySharedPref="+main_pref;////////////////////////////////////////////////////////////////////////////
			syokiPrif( keys);		//プリファレンス未作成時の初期作成
			pref_apiLv=String.valueOf(Build.VERSION.SDK);									//APIレベル
			if (keys.size() > 0) {
	//			myLog(TAG,dbMsg);
				int i=0;
				for (String key : keys.keySet()) {
					summaryStr = null;				//☆setSummaryする前に完全な文字列にする
					i++;
					dbMsg =  i+"/"+keys.size()+")　"+key+"は "+String.valueOf(keys.get(key));///////////////+","+(keys.get(key) instanceof String)+",instanceof Boolean="+(keys.get(key) instanceof Boolean);////////////////////////////////////////////////////////////////////////////
					try{
						if(String.valueOf(keys.get(key)) != null){
							if( ! String.valueOf(keys.get(key)).equals("null")){
								if(key.equals("pref_pb_bgc")){			//");		//プレイヤーの背景は白
									pref_pb_bgc = Boolean.valueOf(keys.get(key).toString());
									dbMsg +=  "プレイヤーの背景は白=" + pref_pb_bgc;////////////////////////////////////////////////////////////////////////////
									pTF_pref_pb_bgc.setChecked(pref_pb_bgc);
									if(pref_pb_bgc){
										pTF_pref_pb_bgc.setSummary(getString(R.string.pref_pb_bgc_wh));
									}else{
										pTF_pref_pb_bgc.setSummary(getString(R.string.pref_pb_bgc_bk));
									}
								}else if(key.equals("pref_gyapless")){					//クロスフェード時間
									pref_gyapless = String.valueOf(keys.get(key).toString());
									dbMsg +=  "=クロスフェード時間" ;////////////////////////////////////////////////////////////////////////////
									summaryStr = String.valueOf(pref_gyapless) +  getResources().getString(R.string.pp_msec);
									pTF_pref_gyapless.setSummary( summaryStr);
									pTF_pref_gyapless.setParamea(0, 1000, 10, Integer.valueOf(pref_gyapless));
								}else if(key.equals("pref_compBunki")){
									pref_compBunki = String.valueOf(keys.get(key).toString());
									dbMsg +=  "コンピレーション分岐 = " + pref_compBunki ;////////////////////////////////////////////////////////////////////////////
									summaryStr = String.valueOf(pref_compBunki) + "%";
									pTF_pref_compBunki.setSummary(String.valueOf(pref_compBunki) + "%");
									pTF_pref_compBunki.setParamea(0, 100, 10, Integer.valueOf(pref_compBunki));
								}else if(key.equals("pref_list_simple")){
									pref_list_simple = Boolean.valueOf(keys.get(key).toString());
									dbMsg +=  "シンプルなリスト表示（サムネールなど省略）=" + pref_list_simple;////////////////////////////////////////////////////////////////////////////
									pcb_list_simple.setChecked(pref_list_simple);
									if(pref_list_simple){
										pcb_list_simple.setSummary(getString(R.string.comon_suru));				//する
									}else{
										pcb_list_simple.setSummary(getString(R.string.comon_sinai));				//しない
									}
								}else if(key.equals("pref_artist_bunnri")){
									pref_artist_bunnri = String.valueOf(keys.get(key).toString());
									dbMsg +=  "アーティストリストを分離する曲数=" + pref_artist_bunnri ;
									summaryStr = String.valueOf(pref_artist_bunnri) + getResources().getString(R.string.comon_kyoku);
									dbMsg +=  "、summaryStr=" + summaryStr ;
									pTF_pref_artist_bunnri.setSummary(summaryStr);
									pTF_pref_artist_bunnri.setParamea(0, 1000, 10, Integer.valueOf(pref_artist_bunnri));
								}else if(key.equals("pref_saikin_tuika")){
									pref_saikin_tuika = String.valueOf(keys.get(key).toString());
									dbMsg +=  "最近追加リストのデフォルト枚数=" + pref_saikin_tuika ;////////////////////////////////////////////////////////////////////////////
									summaryStr = String.valueOf(pref_saikin_tuika) + getResources().getString(R.string.pp_mai);
									dbMsg +=  "、summaryStr=" + summaryStr ;
									pTF_prefsaikin_tuika.setSummary(summaryStr);
									pTF_prefsaikin_tuika.setParamea(0, 10, 10, Integer.valueOf(pref_saikin_tuika));
								}else if(key.equals("pref_saikin_sisei")){
									pref_saikin_sisei = String.valueOf(keys.get(key).toString());
									dbMsg +=  "最近再生リストのデフォルト曲数=" + pref_saikin_sisei ;////////////////////////////////////////////////////////////////////////////
									summaryStr = String.valueOf(pref_saikin_sisei) + getResources().getString(R.string.comon_kyoku);
									dbMsg +=  "、summaryStr=" + summaryStr ;
									pTF_prefsaikin_sisei.setSummary(	 summaryStr );
									pTF_prefsaikin_sisei.setParamea(0, 200, 10, Integer.valueOf(pref_saikin_sisei));
								}else if(key.equals("pref_rundam_list_size")){
									pref_rundam_list_size = String.valueOf(keys.get(key).toString());
									dbMsg +=  "ランダム再生の設定曲数=" + pref_rundam_list_size ;////////////////////////////////////////////////////////////////////////////
									summaryStr = String.valueOf(pref_rundam_list_size) + getResources().getString(R.string.comon_kyoku);
									dbMsg +=  "、summaryStr=" + summaryStr ;
									pTF_rundam_list_size.setSummary(summaryStr);
									pTF_rundam_list_size.setParamea(0, 200, 10, Integer.valueOf(pref_rundam_list_size));
								}else if(key.equals("repeatType")){			//");;			//
									dbMsg +=  ">リピート再生の種類=" ;
									repeatType = Integer.valueOf(keys.get(key).toString());	//
									dbMsg +=   String.valueOf(repeatType)  ;
				//					myLog(TAG,dbMsg);
								}else if(key.equals("pref_nitenkan")){			//");		//ダイヤルキー
									rp_pp = Boolean.valueOf(keys.get(key).toString());
										dbMsg +=  "二点間再生中=" + rp_pp;	//pref_nitenkan
								}else if(key.equals("pref_nitenkan_start")){
									pp_start = String.valueOf(keys.get(key).toString());
									if( pp_start == null ){
										pp_start = "0";
									}
									dbMsg +=  ";二点間再生開始点=" + pp_start ;////////pref_nitenkan_start//////////
								}else if(key.equals("pref_nitenkan_end")){
									pp_end = String.valueOf(keys.get(key).toString());
									if( pp_end == null ){
										pp_end = "1";
									}
									dbMsg +=  ";二点間再生終了点=" + pp_end ;/////pref_nitenkan_end////////////////////////////
								}else if(key.equals("pref_notifplayer")){
									pref_notifplayer = Boolean.valueOf(keys.get(key).toString());
									dbMsg += ",ノティフィケーションプレイヤー＝" + pref_notifplayer;//////////////
									pcb_pref_notifplayer.setChecked(pref_notifplayer);
									if(pref_notifplayer){
										pcb_pref_notifplayer.setSummary(getString(R.string.comon_tukau));				//使う
									}else{
										pcb_pref_notifplayer.setSummary(getString(R.string.comon_tukawanai));		//使わない
									}

								}else if(key.equals("b_List")){			//");
									dbMsg +=  ",前に再生していたプレイリスト=" ;
									b_List = String.valueOf(keys.get(key).toString());
									dbMsg +=   String.valueOf(b_List)  ;
								}else if(key.equals("b_List_id")){			//");
									dbMsg +=  ",前のプレイリストID=" ;
									b_List_id = Integer.valueOf(String.valueOf(keys.get(key).toString()));
									dbMsg +=   String.valueOf(b_List_id)  ;
								}else if(key.equals("b_index")){			//");
									dbMsg +=  ",前に再生していたプレイリスト中のID=" ;
									b_index = Integer.valueOf(String.valueOf(keys.get(key).toString()));
									dbMsg +=   String.valueOf(b_index)  ;
								}else if(key.equals("modori_List_id")){			//");
									dbMsg +=  ",リピート前に再生していたプレイリスト中のID=" ;
									modori_List_id = Integer.valueOf(String.valueOf(keys.get(key).toString()));
									dbMsg +=   String.valueOf(modori_List_id)  ;
									
								}else if(key.equals("pref_lockscreen")){
									pref_lockscreen = Boolean.valueOf(keys.get(key).toString());
									dbMsg += ",ロックスクリーンプレイヤー＝" + pref_lockscreen;//////////////
									pcb_pref_lockscreen.setChecked(pref_lockscreen);
									if(pref_lockscreen){
										pcb_pref_lockscreen.setSummary(getString(R.string.comon_tukau));				//使う
									}else{
										pcb_pref_lockscreen.setSummary(getString(R.string.comon_tukawanai));		//使わない
									}
								}else if(key.equals("pref_bt_renkei")){
									pref_bt_renkei = Boolean.valueOf(keys.get(key).toString());
									dbMsg +=  "Bluetoothの接続に連携=" + pref_bt_renkei;////////////////////////////////////////////////////////////////////////////
										pcb_bt_renkei.setChecked(pref_bt_renkei);
										if(pref_bt_renkei){
											pcb_bt_renkei.setSummary(getString(R.string.comon_suru));				//する
										}else{
											pcb_bt_renkei.setSummary(getString(R.string.comon_sinai));				//しない
										}
								}else if(key.equals("pref_cyakusinn_fukki")){			//着信後の復帰
									pref_cyakusinn_fukki = Boolean.valueOf(keys.get(key).toString());
									dbMsg +=  "着信後の復帰=" + pref_cyakusinn_fukki;////////////////////////////////////////////////////////////////////////////
									pCB_pref_cyakusinn_fukki.setChecked(pref_cyakusinn_fukki);
									if(pref_cyakusinn_fukki){
										pCB_pref_cyakusinn_fukki.setSummary(getString(R.string.comon_suru));				//する
									}else{
										pCB_pref_cyakusinn_fukki.setSummary(getString(R.string.comon_sinai));				//しない
									}

								}else if(key.equals("nowList_id")){
									nowList_id = String.valueOf(keys.get(key).toString());
									dbMsg +=  "再生中のプレイリスト["  + nowList_id +"]";	//再生中のプレイリストID	playListID
								}else if(key.equals("nowList")){
									nowList = String.valueOf(keys.get(key).toString());
									dbMsg +=  "プレイリスト名="  + nowList;////////////////////////////////////////////////////////////////////////////
								}else if(key.equals("play_order")){
									play_order = String.valueOf(keys.get(key).toString());
									dbMsg +=  "(play_order=" + play_order +")";////////////////////////////////////////////////////////////////////////////
								}else if(key.equals("artistID")){
									artistID = String.valueOf(keys.get(key).toString());
									dbMsg +=  ",アーティスト=" + artistID ;////////////////////////////////////////////////////////////////////////////
								}else if(key.equals("albumID")){
									albumID = String.valueOf(keys.get(key).toString());
									dbMsg +=  ",アルバム=" + albumID ;////////////////////////////////////////////////////////////////////////////
								}else if(key.equals("audioID")){
									audioID = String.valueOf(keys.get(key).toString());
									dbMsg +=  ",曲=" + audioID ;////////////////////////////////////////////////////////////////////////////
								}else if(key.equals("pref_saisei_fname")){
									saisei_fname = String.valueOf(keys.get(key));						//再生中のファイル名
									dbMsg +=  "再生中のファイル名" ;////////////////////////////////////////////////////////////////////////////
								}else if(key.equals("pref_saisei_jikan")){
									pref_saisei_jikan = String.valueOf(keys.get(key));		//再開時間		Integer.valueOf(keys.get(key).toString());
									dbMsg += "再生中音楽ファイルの再開時間" ;//////////////////
									wrStr = ORGUT.sdf_mss.format(Long.valueOf(pref_saisei_jikan));
									dbMsg = "再生ポジション；" + pref_saisei_jikan +">>" +wrStr;				//+";"+pTF_saisei_jikan.getText();//////////////////
								}else if(key.equals("pref_saisei_nagasa")){
									pref_saisei_nagasa = String.valueOf(keys.get(key));		//pTF_pref_saisei_nagasa;		//再生中音楽ファイルの再生時間
									dbMsg += "再生中音楽ファイルの長さ＝" + pref_saisei_nagasa;//////////////////
									wrStr=ORGUT.sdf_mss.format(Long.valueOf(pref_saisei_nagasa));
									dbMsg = "再生時間；" +pref_saisei_nagasa +">>" +wrStr;				//+";"+pTF_saisei_jikan.getText();//////////////////
								}else if(key.equals("nowList")){
									nowList = String.valueOf(keys.get(key));

								}else if(key.equals("tone_name")){
									tone_name = String.valueOf(keys.get(key).toString());	//
									dbMsg +=  "トーン名称=" + tone_name ;
								}else if(key.equals("pref_toneList")){				//http://qiita.com/tomoima525/items/f8cf688ad9571d17df41
									String stringList = String.valueOf(keys.get(key));											//bundle.getString("list");  //key名が"list"のものを取り出す
									dbMsg +=  ",stringList= " + stringList;
									try {
										JSONArray array = new JSONArray(stringList);
										dbMsg +=  ",array= " + array;
										int length = array.length();
										dbMsg +=  "= " + length +"件";
										pref_toneList =  new ArrayList<String>();				//トーンリストの初期化
										for(int j = 0; j < length; j++){
											dbMsg +=  "(" + j + "/" + length  + ")" + array.optString(j);
											pref_toneList.add(array.optString(j));
										}
									} catch (JSONException e1) {
										e1.printStackTrace();
									}
									dbMsg +=  ",トーン配列=" + pref_toneList ;
								}else if(key.equals("bBoot")){
									bBoot = Boolean.valueOf(keys.get(key).toString());	//
									dbMsg +=  "バスブート=" + bBoot ;
								}else if(key.equals("reverbBangou")){
									reverbBangou = Short.valueOf(keys.get(key).toString());	//
									dbMsg +=  ",リバーブ効果番号=" + reverbBangou ;
								}else if(key.equals("visualizerType")){
									visualizerType = Integer.valueOf(keys.get(key).toString());	//
									dbMsg +=  "visualizerType=" + visualizerType ;
								}else if(key.equals("pref_zenkai_saiseKyoku")){
									pref_zenkai_saiseKyoku = String.valueOf(keys.get(key));		//pTF_pref_saisei_nagasa;		// != null ){		//
									dbMsg += "前回の連続再生曲数＝" + pref_zenkai_saiseKyoku;//////////////////
									others = others +	getString(R.string.pref_zenkai_saiseikyokusuu) + " : " + pref_zenkai_saiseKyoku +	getString(R.string.pp_kyoku) + "\n";			// ame="">前回の再生曲数</string>
								}else if(key.equals("pref_zenkai_saiseijikann")){
									pref_zenkai_saiseijikann = String.valueOf(keys.get(key));		//;		//前回の連続再生時間
									dbMsg += "前回の連続再生時間＝" + pref_zenkai_saiseijikann;//////////////////
									String saisei = ORGUT.sdf_mss.format(Long.valueOf(pref_zenkai_saiseijikann));
									others = others +	getString(R.string.pref_zenkai_saiseijikann) + " : " + saisei + "\n";			//me="">前回の連続再生時間</string>
								}else if(key.equals("pref_file_in")){
									pref_file_in = String.valueOf(keys.get(key));
									dbMsg += "内蔵メモリ＝" + pref_file_in;////////////////
//05-01 15:11:29.756: E/readPrif[MyPreferences](2990): 23/23)　pref_file_inは //storage/emulated/0/Music/内蔵メモリ＝//storage/emulated/0/Music/でjava.lang.NumberFormatException: Invalid long: "null"
									others =others + getString(R.string.pref_file_in) + " : " +  pref_file_in+"\n";				//内蔵メモリ</string>
								}else if(key.equals("pref_file_ex")){
									pref_file_ex = String.valueOf(keys.get(key));
									dbMsg += "メモリーカード＝" + pref_file_ex;//////////////////
									others =others + getString(R.string.pref_file_ex) + " : " + pref_file_ex +"\n";				//me="">メモリーカード</string>
								}else if(key.equals("pref_file_wr")){
									pref_file_wr = String.valueOf(keys.get(key));
									dbMsg += "設定保存フォルダ＝" + pref_file_wr;//////////////////
									others =others + getString(R.string.pref_file_wr) + " : " + pref_file_wr +"\n";				//name="">設定保存フォルダ</string>
								}else if(key.equals("pref_file_kyoku")){
									pref_file_kyoku = String.valueOf(keys.get(key));
									dbMsg += "総曲数＝" + pref_file_kyoku;////////////////
//05-01 15:11:29.571: E/readPrif[MyPreferences](2990): 11/11)　pref_file_kyokuは 5950総曲数＝5950でjava.lang.NumberFormatException: Invalid long: "null"
									others =others + getString(R.string.pref_file_kyoku) + "=" + pref_file_kyoku + getString(R.string.pp_kyoku) +"\n";		//総曲数
								}else if(key.equals("pref_file_album")){
									pref_file_album = String.valueOf(keys.get(key));
									dbMsg += "総アルバム数＝" + pref_file_album;//////////////////
									others =others + getString(R.string.pref_file_album) + " : " + pref_file_album + getString(R.string.pp_mai)+"\n";		//name="">総アルバム数</string>
								}else if(key.equals("pref_file_saisinn")){
									pref_file_saisinn = String.valueOf(keys.get(key));
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
									String modified = sdf.format(new Date(Long.valueOf(pref_file_saisinn)*1000));
									dbMsg += "最新更新日＝" + modified;//////////////////
									//☆☆1970/1/1が登録されてい☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆
									others =others + getString(R.string.pref_file_saisinn) + " : " +  modified +"\n";		//ame="">最新更新日</string>
								}else if(key.equals("pref_sonota_dpad")){			//");		//ダイヤルキー
									prTT_dpad = Boolean.valueOf(keys.get(key).toString());
										dbMsg +=  "ダイヤルキー=" + prTT_dpad;////////////////////////////////////////////////////////////////////////////
							//			myLog(TAG,dbMsg);
										if(prTT_dpad){
											others =others + getString(R.string.pref_sonota_dpad) + " : " +  getString(R.string.comon_ari) +"\n";		// ダイヤルキー有
										}else{
											others =others + getString(R.string.pref_sonota_dpad) + " : " +  getString(R.string.comon_nasi) +"\n";		// ダイヤルキー無
										}
								}else if(key.equals("pref_sonota_vercord")){
									pref_file_album = String.valueOf(keys.get(key));
									dbMsg += "このアプリのバージョンコード＝" + pref_sonota_vercord;//////////////////
									others =others + getString(R.string.pref_sonota_vercord) + " : " +  String.valueOf(keys.get(key))+"\n";		//バージョンコード
								}else if(key.equals("pref_reset")){		//">このダイアログを閉じたら設定を初期化します。</string>
									pref_reset = Boolean.valueOf(keys.get(key).toString());			//設定を初期化
									dbMsg +=  "このダイアログを閉じたら設定を初期化" ;////////////////////////////////////////////////////////////////////////////
									pCB_pref_reset.setChecked(pref_reset);
									if(pref_reset){
										pref_reset = false;
										pCB_pref_reset.setSummary(getString(R.string.comon_sinai));		//しない</string>
										prefBoolKakikomi("pref_reset" ,pref_reset);
									}
								}else if(key.equals("pref_listup_reset")){		//">このダイアログを閉じたら設定を初期化します。</string>
									pref_listup_reset = Boolean.valueOf(keys.get(key).toString());			//調整リスト消去
									dbMsg +=  "このダイアログを閉じたら調整リスト消去" ;////////////////////////////////////////////////////////////////////////////
									pCB_pref_listup_reset.setChecked(pref_listup_reset);
									if(pref_listup_reset){
										pref_listup_reset = false;
										pCB_pref_listup_reset.setSummary(getString(R.string.comon_sinai));		//しない</string>
										prefBoolKakikomi("pref_listup_reset" ,pref_listup_reset);
									}
								}else if(key.equals("dataFN")){		//">このダイアログを閉じたら設定を初期化します。
									Editor mainEditor = main_pref.edit();
									mainEditor.remove("dataFN");
								}else{
									others =others + key + " : " +  String.valueOf(keys.get(key))+"\n";				//その他の情報
						//			myEditor.remove(key);
								}
							}
						}
		//				myLog(TAG,dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG,dbMsg+"；"+e);
					}
				}			//for (String key : keys.keySet()) {
				String plSum = getString(R.string.pref_titol_plist_artist_bunnri) + ":" + pref_artist_bunnri + getString(R.string.pp_kyoku) + "\n" +
								getString(R.string.playlist_namae_saikintuika)  + ":" +  pref_saikin_tuika + getString(R.string.pp_mai) + "\n" +
								getString(R.string.playlist_namae_saikinsisei) + ":" + pref_saikin_sisei + getString(R.string.pp_kyoku) + "\n" +
								getString(R.string.pref_rundam_list_size) + ":" + pref_rundam_list_size + getString(R.string.pp_kyoku);
				if( rp_pp ){		//二点間再生中
					if( Integer.valueOf(pp_end) <  Integer.valueOf(pp_end)  ){
						String oki= pp_end;
						pp_end = pp_start;
						pp_end = oki;
					}
					SimpleDateFormat ssdh = new SimpleDateFormat("mm:ss 000");
					plSum = plSum  + "\n" + getString(R.string.pref_nitenkan) + ":" +
							ssdh.format(new Date(Long.valueOf(pp_start))) +">>" + ssdh.format(new Date(Long.valueOf(pp_end)));
				}
				String nitenka_memo = getString(R.string.pref_nitenkan) ;		//二点間再生中
				if( rp_pp ){
					nitenka_memo =nitenka_memo + " : " +  getString(R.string.comon_suru) +"\n";		//する
					nitenka_memo =nitenka_memo + saisei_fname +"\n";
					String pp_startStr = ORGUT.sdf_mss.format(Long.valueOf(String.valueOf(pp_start)));				//二点間再生開始点(mmss000)	ORGUT
					dbMsg +=  ">>" + pp_startStr ;////////pref_nitenkan_start//////////
					nitenka_memo =nitenka_memo + " : " +  pp_startStr;
					String pp_endStr = ORGUT.sdf_mss.format(Long.valueOf(String.valueOf(pp_end)));				//二点間再生開始点(mmss000)
					dbMsg +=  ">>" + pp_endStr ;////////pref_nitenkan_start//////////
					nitenka_memo =nitenka_memo + " >>> " +  pp_endStr;
					nitenka_memo =nitenka_memo + "\n" +  getString(R.string.pref_repeet_hukki) +"\n";					//解除後の復帰先</string>
					nitenka_memo =nitenka_memo + "[" + b_List_id + "]" + b_List + "[" + b_index + "]" ;					//前の[プレイリストID]プレイリスト[前の曲順]
				}else{
					nitenka_memo =nitenka_memo  + " : " +  getString(R.string.comon_sinai) +"\n";		//しない
				}
				pref_nitenka_memo.setSummary(nitenka_memo);								//二点間再生状況


				pPS_pref_plist.setSummary(plSum);			//プレイリスト設定
				Siseiothers = getString(R.string.pref_saisei) + ";" + getString(R.string.pref_playlist) + "[" + nowList_id + "]" + nowList + "(" + play_order + ")\n";									//プレイリスト
				Siseiothers = Siseiothers +	getString(R.string.pref_saisei_fname) + "[id;" + artistID + "/" + albumID + "/" + audioID + "]" + saisei_fname;		// 再生中のファイル名</string>
				pPS_sonota.setSummary(Siseiothers);
				wrStr = ORGUT.sdf_mss.format(Long.valueOf(pref_saisei_jikan));
				String others2 = "["+ getString(R.string.pref_saisei_jikan) + " : " +wrStr + "/";			// 再開ポジション</string>
				wrStr=ORGUT.sdf_mss.format(Long.valueOf(pref_saisei_nagasa));
				others2 = others2 +	getString(R.string.pref_saisei_nagasa) + " : " +wrStr + "]\n";			// 再生時間</string>
				others = others2 + others;
				pref_memo.setSummary(others);							//その他の項目列記
				pref_reset = false;
				pCB_pref_reset.setSummary(getString(R.string.comon_sinai));		//しない</string>
				prefBoolKakikomi("pref_reset" ,pref_reset);
			}
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	public void syokiPrif(Map<String, ?> keys){		//プリファレンス未作成時の初期作成
		final String TAG = "syokiPrif[MyPreferences]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			if( ! keys.containsKey("pref_pb_bgc") ){
				pref_pb_bgc = false;
				dbMsg +=  "プレイヤーの背景は白=" + pref_pb_bgc;////////////////////////////////////////////////////////////////////////////
				prefBoolKakikomi("pref_pb_bgc" ,pref_pb_bgc);
			}
			if( ! keys.containsKey("pref_list_simple") ){
				pref_list_simple = false;
				dbMsg +=  ",シンプルなリスト表示（サムネールなど省略）=" + pref_list_simple;////////////////////////////////////////////////////////////////////////////
				prefBoolKakikomi("pref_list_simple" ,pref_list_simple);
			}

			if( ! keys.containsKey("pref_compBunki") ){
				pref_compBunki = "40";
				dbMsg +=  ",コンピレーション分岐点" ;////////////////////////////////////////////////////////////////////////////
				prefItemuKakikomi("pref_compBunki" ,String.valueOf(pref_compBunki));
			}
			if( ! keys.containsKey("pref_gyapless") ){
				pref_gyapless = "100";
				dbMsg +=  ",クロスフェード時間=" + pref_gyapless ;
				prefItemuKakikomi("pref_gyapless" ,String.valueOf(pref_gyapless));
			}

			if( ! keys.containsKey("pref_artist_bunnri") ){
				pref_artist_bunnri = "1000";
				dbMsg +=  ",アーティストリストを分離する曲数=" + pref_artist_bunnri ;
				prefItemuKakikomi("pref_artist_bunnri" ,String.valueOf(pref_artist_bunnri));
			}
			if( ! keys.containsKey("pref_saikin_tuika") ){
				pref_saikin_tuika = "1";
				dbMsg +=  ",最近追加リストのデフォルト枚数=" + pref_saikin_tuika ;////////////////////////////////////////////////////////////////////////////
				prefItemuKakikomi("pref_saikin_tuika" ,String.valueOf(pref_saikin_tuika));
			}
			if( ! keys.containsKey("pref_saikin_sisei") ){
				pref_saikin_sisei = "100";
				dbMsg +=  ",最近再生リストのデフォルト曲数=" + pref_saikin_sisei ;////////////////////////////////////////////////////////////////////////////
				prefItemuKakikomi("pref_saikin_sisei" ,String.valueOf(pref_saikin_sisei));
			}
			if( ! keys.containsKey("pref_rundam_list_size") ){
				pref_rundam_list_size = "100";
				dbMsg +=  ",ランダム再生の設定曲数=" + pref_rundam_list_size ;////////////////////////////////////////////////////////////////////////////
				prefItemuKakikomi("pref_rundam_list_size" ,String.valueOf(pref_rundam_list_size));
			}

			if( ! keys.containsKey("pref_notifplayer") ){
				pref_notifplayer = true;
				dbMsg += ",ノティフィケーションプレイヤー＝" + pref_notifplayer;//////////////
				prefBoolKakikomi("pref_notifplayer" ,pref_notifplayer);
			}
			if( ! keys.containsKey("pref_lockscreen") ){
				pref_lockscreen = true;
				dbMsg += ",ロックスクリーンプレイヤー＝" + pref_lockscreen;//////////////
				prefBoolKakikomi("pref_lockscreen" ,pref_lockscreen);
			}
			if( ! keys.containsKey("pref_bt_renkei") ){
				pref_bt_renkei = true;
				dbMsg +=  ",Bluetoothの接続に連携=" + pref_bt_renkei;////////////////////////////////////////////////////////////////////////////
				prefBoolKakikomi("pref_bt_renkei" ,pref_bt_renkei);
			}
			if( ! keys.containsKey("pref_cyakusinn_fukki") ){
				pref_cyakusinn_fukki = true;
				dbMsg +=  ",着信後の復帰=" + pref_cyakusinn_fukki;////////////////////////////////////////////////////////////////////////////
				prefBoolKakikomi("pref_cyakusinn_fukki" ,pref_cyakusinn_fukki);
			}

			if( ! keys.containsKey("prTT_dpad") ){
				prTT_dpad = false;
				dbMsg +=  ",ダイヤルキー=" + prTT_dpad;////////////////////////////////////////////////////////////////////////////
				prefBoolKakikomi("prTT_dpad" ,prTT_dpad);
			}
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

//プリファレンス全項目読出し//http://ichitcltk.hustle.ne.jp/gudon/modules/pico_rd/index.php?content_id=98

	protected boolean isEditOBJ(String testStr){			//EditTextPreferenceで設定された文字項目☆項目追加時はここに記載
		final String TAG = "isEditOBJ[MyPreferences]";
		String dbMsg="開始";/////////////////////////////////////
		boolean retB=false;
		if(testStr .equals("shyougouURL")					//照合サイトURL
		){
			return true;
		}
		return retB;
	}

	protected boolean isIntOBJ(String testStr){			//EditTextPreferenceで設定された項目☆項目追加時はここに記載
		final String TAG = "isIntOBJ[MyPreferences]";
		String dbMsg="開始";/////////////////////////////////////
		boolean retB=false;
		if(testStr .equals("randumStart_val")				//乱数の開始値
				|| testStr .equals("randumEnd_val")			//乱数の終了値
				|| testStr .equals("val_val")				//乱数の個数
				|| testStr .equals("kurikaesi_val")			//繰り返し判定数
		){
			return true;
		}
		return retB;
	}

	protected boolean isListOBJ(String testStr){			//ListPreferenceで設定された項目☆項目追加時はここに記載
		final String TAG = "isListOBJ[MyPreferences]";
		String dbMsg="開始";/////////////////////////////////////
		boolean retB=false;
		if(testStr .equals("kujiSyurui")				//くじの種類
//			|| testStr .equals("prefsSortOder")				//ソーﾄ
		){
			return true;
		}
		return retB;
	}

	protected boolean isCheckOBJ(String testStr){			//CheckBoxtPreferenceで設定された項目☆項目追加時はここに記載
		final String TAG = "isCheckOBJ[MyPreferences]";
		String dbMsg="開始";/////////////////////////////////////
		boolean retB=false;
		if(testStr .equals("prefUseDlog_ch")			//ダイアログの使用/未使用
//			|| testStr .equals("prefsSortOder")				//ソーﾄ
		){
			return true;
		}
		return retB;
	}


	// ここで summary を動的に変更
//	private OnPreferenceChangeListener cBPOnPreferenceChangeListener =
//		new OnPreferenceChangeListener(){
//		@Override
//		public boolean onPreferenceChange(Preference preference, Object newValue) {
	/**
	 * リスナー未設定の場合はここで汎用的に書き込み
	 * */
		private SharedPreferences.OnSharedPreferenceChangeListener listener =
			new SharedPreferences.OnSharedPreferenceChangeListener() {
				public void onSharedPreferenceChanged(SharedPreferences onSharedPreferenceChanged, String key) {
				final String TAG = "onSharedPreferenceChanged[MyPreferences]";
				String dbMsg="開始";/////////////////////////////////////
				try{
					boolean vBool;
					String vStr;
					dbMsg = "key="+key +"=" ;//////////////////
					if(key.equals("pref_pb_bgc")){			//");		//プレイヤーの背景は白
//						vStr = (String) pTF_pref_pb_bgc.getSummary();
//						dbMsg += ",pref_pb_bgc=" + vStr;//////////////////
//						pref_pb_bgc=  vStr.equals(getString(R.string.pref_pb_bgc_wh));	//白								//onSharedPreferenceChanged.getBoolean(key, true);		//☆ここで操作直後のモニター
//						dbMsg += ">>" + pref_pb_bgc;//////////////////
//						myLog(TAG,dbMsg);
//						prefBoolKakikomi(key ,pref_pb_bgc);									//プリファレンス全項目読出し
//			//			prefItemuKakikomi(key ,String.valueOf(pref_pb_bgc));									//プリファレンス全項目読出し
					}else if(key.equals("pref_list_simple")){														//シンプルなリスト表示（サムネールなど省略）
//						vStr = (String) pcb_list_simple.getSummary();
//						dbMsg += ",pref_list_simple=" + vStr;//////////////////
//						pref_list_simple= vStr.equals(getString(R.string.pref_list_simple_summaryOn));		//アイテムだけ
//						//				pref_list_simple= onSharedPreferenceChanged.getBoolean(key, true);
//						dbMsg += ">>" + pref_list_simple;//////////////////
//						prefItemuKakikomi(key ,String.valueOf(pref_list_simple));									//プリファレンス全項目読出し
					}else if(key.equals("pref_gyapless")){					//クロスフェード時間
//						vStr = onSharedPreferenceChanged.getString(key, "0");
//						dbMsg +=vStr;//////////////////
//			//			myLog(TAG,dbMsg);
//						if(Integer.parseInt(vStr) > 999){		//曲と曲の間が長いと感じたら小さな値に、曲の終わりが次の曲に重なるようなら最大1000m秒まで調整できます。
//							pref_gyapless = String.valueOf(999);
//						}else if(Integer.parseInt(vStr) < 0 || vStr ==null){		//曲と曲の間が長いと感じたら小さな値に、曲の終わりが次の曲に重なるようなら最大1000m秒まで調整できます。
//							pref_gyapless = String.valueOf(0);
//						}else{
//							pref_gyapless = vStr;
//						}
//			//			pTF_pref_gyapless.setText(String.valueOf(pref_gyapless));
//						pTF_pref_gyapless.setSummary(String.valueOf(pref_gyapless));
//						prefItemuKakikomi(key , String.valueOf(pref_gyapless));
					}else if(key.equals("pref_compBunki")){								//コンピレーション
//						vStr = onSharedPreferenceChanged.getString(key, "40");
//						dbMsg +=vStr;//////////////////
//						pTF_pref_compBunki.setSummary(String.valueOf(vStr) + "%");
//						prefItemuKakikomi(key , String.valueOf(vStr));
					}else if(key.equals("pref_artist_bunnri")){						//アーティストリストを分離する曲数
//						vStr = onSharedPreferenceChanged.getString(key, "1000");
//						dbMsg +=vStr;//////////////////
//						pTF_pref_artist_bunnri.setText(String.valueOf(vStr));
//						pTF_pref_artist_bunnri.setSummary(String.valueOf(vStr));
//						prefItemuKakikomi(key , String.valueOf(vStr));
					}else if(key.equals("pref_saikin_tuika")){							//最近追加リストのデフォルト枚数
//						vStr = onSharedPreferenceChanged.getString(key, "1");
//						dbMsg +=vStr;//////////////////
//						pTF_prefsaikin_tuika.setText(String.valueOf(vStr));
//						pTF_prefsaikin_tuika.setSummary(String.valueOf(vStr));
//						prefItemuKakikomi(key , String.valueOf(vStr));
					}else if(key.equals("pref_saikin_sisei")){							//最近再生リストのデフォルト曲数
//						vStr = onSharedPreferenceChanged.getString(key, "100");
//						dbMsg +=vStr;//////////////////
//						pTF_prefsaikin_sisei.setText(String.valueOf(vStr));
//						pTF_prefsaikin_sisei.setSummary(String.valueOf(vStr));
//						prefItemuKakikomi(key , String.valueOf(vStr));
					}else if(key.equals("pref_rundam_list_size")){							//ランダム再生の設定曲数
//						vStr = onSharedPreferenceChanged.getString(key, "100");
//						dbMsg +=vStr;//////////////////
//						pTF_rundam_list_size.setText(String.valueOf(vStr));
//						pTF_rundam_list_size.setSummary(String.valueOf(vStr));
//						prefItemuKakikomi(key , String.valueOf(vStr));
					}else if(key.equals("pref_nitenkan")){
//						rp_pp= onSharedPreferenceChanged.getBoolean(key, rp_pp);
//						dbMsg += ",ノティフィケーションプレイヤー＝" + rp_pp;//////////////
//						prefItemuKakikomi(key ,String.valueOf(rp_pp));
					}else if(key.equals("pref_nitenkan_start")){
//						vStr = onSharedPreferenceChanged.getString(key, "0");
//						dbMsg +=",リピート区間開始点 = " + vStr;//////////////////
//						String pp_startStr = ORGUT.sdf_mss.format(vStr).toString();				//二点間再生開始点(mmss000)
//						pref_nitenkan_start.setText(String.valueOf(pp_startStr));
//						pref_nitenkan_start.setSummary(String.valueOf(pp_startStr));
//						prefItemuKakikomi(key , String.valueOf(vStr));
					}else if(key.equals("pref_nitenkan_end")){
//						vStr = onSharedPreferenceChanged.getString(key, "1");
//						dbMsg +=",リピート区間終了点 = " + vStr;//////////////////
//						String pp_endStr = ORGUT.sdf_mss.format(vStr).toString();				//二点間再生開始点(mmss000)
//						pref_nitenkan_end.setText(String.valueOf(pp_endStr));
//						pref_nitenkan_end.setSummary(String.valueOf(pp_endStr));
//				//		prefItemuKakikomi(key , String.valueOf(vStr));
					}else if(key.equals("pref_lockscreen")){
//						pref_lockscreen= onSharedPreferenceChanged.getBoolean(key, pref_lockscreen);
//						dbMsg += ",ロックスクリーンプレイヤー＝" + pref_lockscreen;//////////////
//						prefItemuKakikomi(key ,String.valueOf(pref_lockscreen));
					}else if(key.equals("pref_notifplayer")){
//						pref_notifplayer= onSharedPreferenceChanged.getBoolean(key, pref_notifplayer);
//						dbMsg += ",ノティフィケーションプレイヤー＝" + pref_notifplayer;//////////////
//						prefItemuKakikomi(key ,String.valueOf(pref_notifplayer));
					}else if(key.equals("pref_cyakusinn_fukki")){
//						pref_cyakusinn_fukki= onSharedPreferenceChanged.getBoolean(key, true);
//						dbMsg +="着信後の復帰は" + pref_cyakusinn_fukki;//////////////////
//						prefItemuKakikomi(key ,String.valueOf(pref_cyakusinn_fukki));									//プリファレンス全項目読出し
					}else if(key.equals("pref_sonota_dpad")){									//ダイヤルキー
						prTT_dpad= onSharedPreferenceChanged.getBoolean(key, true);
							dbMsg +=prTT_dpad;//////////////////
				//			myLog(TAG,dbMsg);
							prefItemuKakikomi(key ,String.valueOf(prTT_dpad));									//プリファレンス全項目読出し
					}else if(key.equals("pref_bt_renkei")){															//,Bluetoothの接続に連携
//						pref_bt_renkei= onSharedPreferenceChanged.getBoolean(key, true);
//							dbMsg +=pref_bt_renkei;//////////////////
//				//			myLog(TAG,dbMsg);
//							prefItemuKakikomi(key ,String.valueOf(pref_bt_renkei));									//プリファレンス全項目読出し
					}else if(key.equals("pref_reset")){											//設定を初期化
//						kariBool = onSharedPreferenceChanged.getBoolean(key, true);
//						if(kariBool){
//							Intent intentA3B = new Intent(MyPreferences.this,Alart3BT.class);
//							intentA3B.putExtra("dTitol",getResources().getString(R.string.pref_reset));				//ダイアログタイトル ;設定を初期化
//							intentA3B.putExtra("dMessage",getResources().getString(R.string.pref_reset_msg));	//アラート文 ;このダイアログを閉じた時、設定を初期化します。\n(異常動作が収まらない時の処置です。本当に消去して構いませんか？)</string>
//							intentA3B.putExtra("Msg1",getResources().getString(R.string.comon_suru));		//ボタン1のキーフェイス ;する
//							intentA3B.putExtra("Msg3",getResources().getString(R.string.comon_sinai));		//NGのキーフェイス ; しない
//							startActivityForResult(intentA3B , R.string.pref_reset);
//						}
					}else if(key.equals("visualizerType")){
						dbMsg +=  "visualizerType=" + visualizerType ;
						prefItemuKakikomi(key , String.valueOf(visualizerType));
						//				}else{
	//					prefItemuKakikomi(key , String.valueOf(keys.get(key)));									//プリファレンス全項目読出し
					}
	//				myLog(TAG,dbMsg);
		//			viewSakusei();				//プリファレンスの表示処理
				} catch (Exception e) {
					myErrorLog(TAG,dbMsg+"で"+e);
				}
			}	//onSharedPreferenceChanged(SharedPreferences onSharedPreferenceChanged, String key) {

		};

		public void prefItemuKakikomi(String key ,String vStr){				//プリファレンスに文字を書き込む
		final String TAG = "prefItemuKakikomi[MyPreferences]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg= key+";"+ vStr;
			myEditor.putString( key, vStr);						//再生中のファイル名  Editor に値を代入
			boolean wrb = myEditor.commit();
			dbMsg= dbMsg + ">>書込み成功="+ wrb;
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}		//http://ichitcltk.hustle.ne.jp/gudon/modules/pico_rd/index.php?content_id=98

	public void prefBoolKakikomi(String key ,boolean vBool){			//プリファレンスにbool値を書き込む
		final String TAG = "prefBoolKakikom";
		String dbMsg="i[MyPreferences]";
		try{
			dbMsg= key+";"+ vBool;
			myEditor.putBoolean( key, vBool);						//再生中のファイル名  Editor に値を代入
			boolean wrb = myEditor.commit();
			dbMsg= ">>書込み成功="+ wrb;
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}		//http://ichitcltk.hustle.ne.jp/gudon/modules/pico_rd/index.php?content_id=98

	public void modori() {	// 呼出し元への戻し処理
		final String TAG = "modori[MyPreferences]";							//long seleID  ,, int hennkou, String seleItem
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			Intent data = new Intent();			// 返すデータ(Intent&Bundle)の作成
			Bundle bundle = new Bundle();
			data.putExtras(bundle);
			if(pref_reset){				//設定を初期化
				myEditor.clear();		//プリファレンスの内容削除
				myEditor.commit();
				data.putExtras(bundle);
				setResult(RESULT_CANCELED, data);		// setResult() で bundle を載せた送るIntent dataをセットする	// 第一引数は…Activity.RESULT_OK, Activity.RESULT_CANCELED など
			}else{
				dbMsg= "シンプルなリスト表示="+ pref_list_simple;	////////////////
				bundle.putString("key.pref_list_simple", String.valueOf(pref_list_simple));				//シンプルなリスト表示（サムネールなど省略）
				dbMsg= dbMsg + ",プレイヤーの背景(w)="+ pref_pb_bgc;	////////////////
				bundle.putString("key.pref_pb_bgc", String.valueOf(pref_pb_bgc));			//プレイヤーの背景
				dbMsg= dbMsg +",クロスフェード="+ pref_gyapless;				///////////////
				bundle.putString("key.pref_gyapless", pref_gyapless);		//クロスフェード時間
				dbMsg= dbMsg +",コンピレーション分岐点="+ pref_compBunki;				///////////////
				bundle.putString("key.pref_compBunki", String.valueOf(pref_compBunki));			//pref_compBunki;		//コンピレーション分岐点
				dbMsg += ",ロックスクリーンプレイヤー＝" + pref_lockscreen;//////////////
				bundle.putString("key.pref_lockscreen", String.valueOf(pref_lockscreen));
				dbMsg += ",ノティフィケーションプレイヤー＝" + pref_notifplayer;//////////////
				bundle.putString("key.pref_notifplayer", String.valueOf(pref_notifplayer));
				dbMsg= dbMsg +",Bluetoothの接続に連携="+ pref_bt_renkei;				///////////////
				bundle.putString("key.pref_bt_renkei", String.valueOf(pref_bt_renkei));
				dbMsg= dbMsg +",調整リスト消去="+ pref_listup_reset;				///////////////
				bundle.putString("pref_listup_reset", String.valueOf(pref_listup_reset) );
				dbMsg= dbMsg +",visualizerType="+ visualizerType;				///////////////
				bundle.putString("visualizerType", String.valueOf(visualizerType) );
		//		myLog(TAG,dbMsg);
				data.putExtras(bundle);
				setResult(RESULT_OK, data);		// setResult() で bundle を載せた送るIntent dataをセットする	// 第一引数は…Activity.RESULT_OK, Activity.RESULT_CANCELED など
			}
			quitMe();			//
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	public void quitMe(){			///終了処理
		final String TAG = "quitMe[MyPreferences]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			dbMsg="スタート";///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//			myLog(TAG,"quitMeが発生");
			MyPreferences.this.finish();
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
	private OnPreferenceChangeListener numberPickerListener =new OnPreferenceChangeListener(){			//NumberPickerの PreferenceChangeリスナー
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			final String TAG = "numberPickerListener[MyPreferences]";
			String dbMsg="開始";/////////////////////////////////////
			try{
				String keyName =  preference.getKey();
				dbMsg= keyName + ";" + String.valueOf(newValue);
				String atai = String.valueOf(newValue) ;
				dbMsg= dbMsg + ";" + atai;
				if( keyName.equals("pref_gyapless") ){											//クロスフェード時間
					pref_gyapless = String.valueOf(pTF_pref_gyapless.retValue(Integer.valueOf(atai)));
					dbMsg= dbMsg + ",クロスフェード時間=" + pref_gyapless;
					pTF_pref_gyapless.setSummary(pref_gyapless +  getResources().getString(R.string.pp_msec));
					atai = pref_gyapless;
				}else if(keyName.equals("pref_compBunki")){								//コンピレーション
					pref_compBunki = String.valueOf(pTF_pref_compBunki.retValue(Integer.valueOf(atai)));
					dbMsg= dbMsg + ",コンピレーション=" + pref_compBunki;
					pTF_pref_compBunki.setSummary(pref_compBunki +  "%");
					atai = pref_compBunki;
				}else if(keyName.equals("pref_artist_bunnri")){						//アーティストリストを分離する曲数
					pref_artist_bunnri = String.valueOf(pTF_pref_artist_bunnri.retValue(Integer.valueOf(atai)));
					dbMsg= dbMsg + ",アーティストリストを分離する曲数=" + pref_artist_bunnri;
					pTF_pref_artist_bunnri.setSummary(pref_artist_bunnri + getResources().getString(R.string.comon_kyoku));
					atai = pref_artist_bunnri;
				}else if(keyName.equals("pref_saikin_tuika")){							//最近追加リストのデフォルト枚数
					pref_saikin_tuika = String.valueOf(pTF_prefsaikin_tuika.retValue(Integer.valueOf(atai)));
					dbMsg= dbMsg + ",最近追加リストのデフォルト枚数=" + pref_saikin_tuika;
					pTF_prefsaikin_tuika.setSummary(pref_saikin_tuika +  getResources().getString(R.string.pp_mai));
					atai = pref_saikin_tuika;
				}else if(keyName.equals("pref_saikin_sisei")){							//最近再生リストのデフォルト曲数
					pref_saikin_sisei = String.valueOf(pTF_prefsaikin_sisei.retValue(Integer.valueOf(atai)));
					dbMsg= dbMsg + ",最近再生リストのデフォルト曲数=" + pref_saikin_sisei;
					pTF_prefsaikin_sisei.setSummary(pref_saikin_sisei + getResources().getString(R.string.comon_kyoku));
					atai = pref_saikin_sisei;
				}else if(keyName.equals("pref_rundam_list_size")){							//ランダム再生の設定曲数
					pref_rundam_list_size = String.valueOf(pTF_rundam_list_size.retValue(Integer.valueOf(atai)));
					dbMsg= dbMsg + ",ランダム再生の設定曲数=" + pref_rundam_list_size;
					pTF_rundam_list_size.setSummary(pref_rundam_list_size +  getResources().getString(R.string.pp_msec));
					atai = pref_rundam_list_size;
				}
				prefItemuKakikomi(keyName ,atai);
				myLog(TAG,dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"で"+e);
			}
			return true;
		}
	};

/**
 * SwitchPreferenceの変更反映
 * 変更値をObjectからbooleanにキャストしてプリファレンスに書き込む
 * 項目追加時は個々の変数にここで設定
 * */
	private OnPreferenceChangeListener switchListener =new OnPreferenceChangeListener(){	// SwitchPreferenceの PreferenceChangeリスナー
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			final String TAG = "switchListener[MyPreferences]";
			String dbMsg="開始";/////////////////////////////////////
			try{
				String keyName =  preference.getKey();
				dbMsg= keyName + ";" + String.valueOf(newValue);
				boolean atai = (boolean) newValue;
				dbMsg= dbMsg + ";" + atai;
				if( keyName.equals("pref_list_simple") ){
					pref_list_simple = atai;
					dbMsg= dbMsg + ",シンプルなリスト表示=" + pref_list_simple;
				}else if( keyName.equals("pref_pb_bgc") ){
					pref_pb_bgc = atai;
					dbMsg= dbMsg + ",プレイヤーの背景=" + pref_pb_bgc;
				}else if( keyName.equals("pref_notifplayer") ){
					pref_notifplayer = atai;
					dbMsg= dbMsg + ",ノティフィケーションプレイヤー=" + pref_notifplayer;
				}else if( keyName.equals("pref_lockscreen") ){
					pref_lockscreen = atai;
					dbMsg= dbMsg + ",ロックスクリーンプレイヤー=" + pref_lockscreen;
				}else if( keyName.equals("pref_cyakusinn_fukki") ){
					pref_cyakusinn_fukki = atai;
					dbMsg= dbMsg + ",通話連携=" + pref_cyakusinn_fukki;
				}else if( keyName.equals("pref_bt_renkei") ){
					pref_bt_renkei = atai;
					dbMsg= dbMsg + ",Bluetooth連携=" + pref_bt_renkei;
				}else if( keyName.equals("pref_reset") ){
					pref_reset = atai;
					dbMsg= dbMsg + ",設定を初期化=" + pref_reset;
				}else if( keyName.equals("pref_listup_reset") ){
					pref_listup_reset = atai;
					dbMsg= dbMsg + ",調整リストを初期化=" + pref_listup_reset;
				}
				prefBoolKakikomi(keyName ,atai);
				myLog(TAG,dbMsg);
			} catch (Exception e) {
				myErrorLog(TAG,dbMsg+"で"+e);
			}
			return true;
		}
	};

	private OnPreferenceChangeListener listPreference_OnPreferenceChangeListener =new OnPreferenceChangeListener(){	// リストPreferenceの　PreferenceChangeリスナー
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			return listPreference_OnPreferenceChange(preference,newValue);
		}
	};

	private boolean listPreference_OnPreferenceChange(Preference preference, Object newValue){
		final String TAG = "listPreference_OnPreferenceChange[MyPreferences]";
		String dbMsg="開始";/////////////////////////////////////
		try{
			dbMsg="newValue="+ newValue;
			ListPreference listpref =(ListPreference)preference;
			dbMsg= dbMsg + ",listpref="+ listpref;
//			String summary = String.format("entry=%s , value=%s", listpref.getEntry(),listpref.getValue());
//			dbMsg= dbMsg + ",summary="+ summary;
			preference.setSummary((CharSequence) newValue);
			if(newValue.equals(getString(R.string.pref_effect_vi_fft))){				//スペクトラムアナライザ風
				visualizerType = MaraSonActivity.Visualizer_type_FFT;
			} else if(newValue.equals(getString(R.string.pref_effect_vi_wave))){		//オシロスコープ風
				visualizerType = MaraSonActivity.Visualizer_type_wave;
			} else if(newValue.equals(getString(R.string.comon_tukawanai))){			//使わない
				visualizerType = MaraSonActivity.Visualizer_type_none;
			}
			dbMsg= dbMsg + ",visualizerType="+ visualizerType;
			String effectMsg = (String) pPS_pref_effect.getSummary();				//サウンドエフェクト
			effectMsg = effectMsg.substring(0, effectMsg.indexOf(getString(R.string.pref_effect_vi))) + "\n" +
					getString(R.string.pref_effect_vi) + ";" + newValue ;
			pPS_pref_effect.setSummary(effectMsg);
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
		return true;
	}

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

@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final String TAG = "onOptionsItemSelected[MaraSonActivity]";
		String dbMsg=ORGUT.nowTime(true,true,true);/////////////////////////////////////
		try{
			Intent intentPRF;
			dbMsg = "MenuItem"+item.getItemId()+"="+item.toString();////////////////////////////////////////////////////////////////////////////
	//		Log.d("onOptionsItemSelected",dbMsg);
			String helpURL;

			int nowSelectMenu = item.getItemId();
			switch (nowSelectMenu) {
			case R.id.menu_item_sonota_help:						//ヘルプ表示	MENU_HELP
				Intent intentWV = new Intent(MyPreferences.this,wKit.class);			//webでヘルプ表示
				if(locale.equals( Locale.JAPAN)){										//日本語の場合のみconstant for ja_JP.
					helpURL = "file:///android_asset/pref.html";		//日本語ヘルプ
					intentWV.putExtra("dataURI",helpURL);		//"file:///android_asset/index.html"
				}else {
					helpURL = "file:///android_asset/en/pref.html";	//英語ヘルプ
				}
				startActivity(intentWV);
				return true;
			case R.id.menu_item_sonota_end:					//終了	MENU_END
				quitMe();		//このアプリを終了する
				return true;
			}
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
				final String TAG = "onWindowFocusChanged[MyPreferences]";
				String dbMsg= "開始;";/////////////////////////////////////
				try{
		//			myLog(TAG,dbMsg);
					prefHyouji();				//プリファレンス画面表示
				}catch (Exception e) {
					myErrorLog(TAG,dbMsg + "で"+e.toString());
				}
		 }
		 super.onWindowFocusChanged(hasFocus);
	 }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent rData) { // startActivityForResult で起動させたアクティビティがfinish() により破棄されたときにコールされる
	// requestCode : startActivityForResult の第二引数で指定した値が渡される
	// resultCode : 起動先のActivity.setResult の第一引数が渡される
	// Intent data : 起動先Activityから送られてくる Intent
		super.onActivityResult(requestCode, resultCode, rData);
		final String TAG = "setHeadImgList[MuList]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			dbMsg = "requestCode="+requestCode+",resultCode="+resultCode+",rData="+rData;//////////////////////////////////////////////////////////////////////////////
			Bundle bundle = null ;
			if(rData != null){
				bundle = rData.getExtras();
				if(resultCode == RESULT_OK){			//-1
					switch(requestCode) {
					case R.string.pref_reset:						//));				//ダイアログタイトル ;設定を初期化
						pref_reset= true;
						dbMsg +=pref_reset;//////////////////
						myLog(TAG,dbMsg);
						prefBoolKakikomi("pref_reset" ,pref_reset);
						break;
					}
				}
			}
			myLog(TAG,dbMsg);
		} catch (Exception e) {		//汎用
			myErrorLog(TAG,dbMsg+"で"+e.toString());
		}
	}		//http://fernweh.jp/b/startactivityforresult/

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		final String TAG = "onResume[MyPreferences]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			if(listener != null){
				getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
			}
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
		super.onPause();
		final String TAG = "onResume[MyPreferences]";
		String dbMsg= "開始;";/////////////////////////////////////
		try{
			getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
	//		myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e.toString());
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		modori();	// 呼出し元への戻し処理
	//	//myLog("onDestroy","onDestroyが発生");
	//	LAO.readPrif();		//プリファレンスの読込み
	//	clPref();	//プリファレンス設定状況読み込み
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

//http://yan-note.blogspot.jp/2010/09/android_12.html
//http://android.roof-balcony.com/shori/strage/localfile-2/