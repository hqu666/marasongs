package com.hijiyam_koubou.marasongs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothHealth;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class BuletoohtReceiver extends BroadcastReceiver{
//	MusicPlayerService MPS = new MusicPlayerService();
	public Context rContext;
	MaraSonActivity activity;// onCreate の　receiver.activity = this;
//	MusicPlayerService  service;

	public Handler btHandler = new Handler();
	public boolean pref_bt_renkei =true;							//Bluetoothの接続に連携して一時停止/再開
	public IntentFilter btFilter = new IntentFilter();						//BluetoothA2dpのACTIONを保持する
	public BluetoothAdapter mBluetoothAdapter = null;				//getPairedDevicesで生成
	public BluetoothServerSocket serverSocket;
//	BuletoohtReceiver btReceiver;
	public String dviceStyteStr;		//デバイスの接続情報
	public String dviceNamer;						//デバイス名
	public String dviceStytus;						//デバイスの状態
	public String stateBaseStr = null;
	public String uuidStr = "00001108-0000-1000-8000-00805F9B34FB";			//接続先のサービス
	static final int REQUEST_ENABLE_BT = 0;


//	public BuletoohtReceiver(){
//		final String TAG = "BuletoohtReceiver[BuletoohtReceiver]";
//		String dbMsg="開始";/////////////////////////////////////
//		try {
//			if( dviceStyteStr == null ){
//				dviceStyteStr = getPairedDevices( context , intent);								//ペアリング機器の検索※当面は上記未満の暫定対応
//			}
//			dbMsg +="デバイスの接続情報=" +dviceStyteStr;
//			myLog(TAG, dbMsg);
//		} catch (Exception e) {		//汎用
//			myErrorLog(TAG ,  dbMsg + "で" + e);
//		}
//	}

	public void BluetoothAdapter(){
		final String TAG = "BluetoothAdapter[BuletoohtReceiver]";
		String dbMsg="開始";/////////////////////////////////////
		try {
			myLog(TAG, dbMsg);
		} catch (Exception e) {		//汎用
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}

/**
 * 接続時（接続先PowerOn）.a2dp.profile.action.CONNECTION_STATE_CHANGED,state=1	>>.a2dp.profile.action.CONNECTION_STATE_CHANGED,state=2	>>	.a2dp.profile.action.PLAYING_STATE_CHANGED,state=11
 * 				>>>onStartCommand[MusicPlayerService](16543): 開始,action=com.example.android.remotecontrol.ACTION_PLAY>>remotecontrol.ACTION_PAUSE が連続発生
 * */
	//	BroadcastReceiver btReceiver = new BroadcastReceiver() {		//②ⅰpublic BroadcastReceive
	@SuppressLint("MissingPermission")
	@Override
	public void onReceive(final Context context, final Intent intent) {
		this.rContext = context;
//		btHandler.post(new Runnable() {
//			public void run() {
				final String TAG = "onReceive[BuletoohtReceiver.onReceive]";
				String dbMsg="開始";/////////////////////////////////////
				try {
					BuletoohtReceiver.this.rContext = context;
//					dbMsg= "rContext=" + rContext;	///////rContext=com.hijiyam_koubou.marasongs.MusicPlayerService@428a7c60
//					dbMsg +=",intent=" + intent;	//,intent=Intent { act=android.bluetooth.a2dp.profile.action.PLAYING_STATE_CHANGED flg=0x8000010 (has extras)
//					defaulthAdapter = BluetoothAdapter.getDefaultAdapter();
//					dbMsg +=",defaulthAdapter=" + defaulthAdapter;	////////////////
//					dbMsg +=",接続連携指定=" + pref_bt_renkei;	//////////////
					String action = intent.getAction();			//		String action = String.valueOf( intent.getAction() );
					dbMsg +=",action=" + action;	///////////
//て一時停止/再開=true,action=android.bluetooth.adapter.action.DISCOVERY_STARTEDでjava.lang.NullPointerException: Attempt to invoke virtual method 'int android.os.Bundle.getInt(java.lang.String)' on a null object reference
					Bundle extras  = intent.getExtras();
					dbMsg +=",extras=" + extras;	////////////////
					if( extras != null ){
						int state = Integer.valueOf( extras.getInt(BluetoothProfile.EXTRA_STATE) );
						dbMsg= "state=" + state;	////////////////
						int prevState = extras.getInt(BluetoothProfile.EXTRA_PREVIOUS_STATE);
						dbMsg +=",prevState=" + prevState;	////////////////
//						dbMsg +=",service=" + service;	////////////////
//						if(service == null){
//							service = MPS;
//							dbMsg +=">>" + service;	////////////////
//						}
						if( dviceStyteStr == null ){
							dviceStyteStr = getPairedDevices( context , intent);								//ペアリング機器の検索※当面は上記未満の暫定対応
						}
						dbMsg +="デバイスの接続情報=" +dviceStyteStr;
							stateBaseStr = stateBaseStr +dviceStyteStr;
						BluetoothDevice device = extras.getParcelable(BluetoothDevice.EXTRA_DEVICE);
						if( device != null ){
							dbMsg +=","+ "Device名=" + device.getName()+";";	////////////////
							stateBaseStr  =  device.getName() +":";
						}
						Intent MPSIntent = new Intent(context, MusicPlayerService.class);
						if (action.equals(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)) {				//API level 11
							dbMsg += "、action =BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED:state=" +state ;
							switch(state) {
							case BluetoothProfile.STATE_DISCONNECTED:																//0			getApplicationContext().
								dbMsg += "、STATE_DISCONNECTED;切断された" ;
								stateBaseStr = stateBaseStr+ context.getResources().getString(R.string.bt_discnnected);				//切断
								MPSIntent.setAction(MusicPlayerService.ACTION_PAUSE);						//		ACTION_PLAYPAUSE
								dbMsg +=">指定するAction>" + MPSIntent.getAction();/////////////////////////////////////
								context.startService(MPSIntent);	//startService(new Intent(MusicPlayerService.ACTION_PAUSE));
								break;
							case BluetoothProfile.STATE_CONNECTING:																	//1
								stateBaseStr = stateBaseStr+  context.getResources().getString(R.string.bt_connecting);				//接続処理中
								break;
							case BluetoothProfile.STATE_CONNECTED:																	//2
								dbMsg += "、接続された"  ;
								stateBaseStr = stateBaseStr+  context.getResources().getString(R.string.bt_connected);				//接続済み
								MPSIntent.setAction(MusicPlayerService.ACTION_PLAY);						//		ACTION_PLAYPAUSE
								dbMsg +=">指定するAction>" + MPSIntent.getAction();/////////////////////////////////////
								context.startService(MPSIntent);	//startService(new Intent(MusicPlayerService.ACTION_PAUSE));
								break;
							case BluetoothProfile.STATE_DISCONNECTING:																//3
								stateBaseStr =  stateBaseStr+ context.getResources().getString(R.string.bt_discnnecting);				//切断処理中
								break;
							default:
								stateBaseStr =  stateBaseStr+ context.getResources().getString(R.string.bt_unknown);					//不明
								break;
							}
						} else if (action.equals(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED)) {
							dbMsg += "、action =BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED:state =" +state ;
							switch(state) {
							case BluetoothA2dp.STATE_PLAYING:																			//10
								dbMsg += "、aSTATE_PLAYING:";
								stateBaseStr =  stateBaseStr+ context.getResources().getString(R.string.bt_playing);					//再生中
//								MPSIntent.setAction(MusicPlayerService.ACTION_PLAY);		
//								dbMsg +=">指定するAction>" + MPSIntent.getAction();
//								context.startService(MPSIntent);	//startService(new Intent(MusicPlayerService.ACTION_PAUSE));
//			//					service.processPlayRequest();
								break;
							case BluetoothA2dp.STATE_NOT_PLAYING:																	//11
								dbMsg += "、STATE_NOT_PLAYING:";				//接続が切れた時に発生した
								stateBaseStr = stateBaseStr+  context.getResources().getString(R.string.bt_not_playing);				//一時停止
//								MPSIntent.setAction(MusicPlayerService.ACTION_PAUSE);						//		ACTION_PLAYPAUSE
//								dbMsg +=">指定するAction>" + MPSIntent.getAction();/////////////////////////////////////
//								context.startService(MPSIntent);	//startService(new Intent(MusicPlayerService.ACTION_PAUSE));
////								if( service.pref_bt_renkei ){	//終話後に自動再生
////									service.processPauseRequest();
////								}
								break;
							default:
								stateBaseStr =  stateBaseStr+ context.getResources().getString(R.string.bt_unknown);				//不明
								break;
							}
//									stateStr = prevState == BluetoothA2dp.STATE_NOT_PLAYING ?  getResources().getString(R.string.bt_not_playing)
//								: prevState == BluetoothA2dp.STATE_PLAYING ?  getResources().getString(R.string.bt_unknown)
//								: getResources().getString(R.string.bt_unknown);
						} else if (action.equals(Intent.ACTION_MEDIA_BUTTON)) {
							dbMsg += "、ACTION_MEDIA_BUTTON:";
							AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
							KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
							if (audioManager.isBluetoothA2dpOn()) {								// Bluetoothで再生中
								dbMsg += "、Bluetoothで再生中";
								int keyCord = event.getKeyCode();
								dbMsg += "(" + keyCord;
								switch(state) {
								case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE :																//85
									dbMsg += ")PLAY_PAUSEボタン";
									MPSIntent.setAction(MusicPlayerService.ACTION_PLAYPAUSE);		
									break;
								case KeyEvent.KEYCODE_MEDIA_PLAY :																		//126
									dbMsg += ")再生ボタン";
									MPSIntent.setAction(MusicPlayerService.ACTION_PLAY);
									break;
								case KeyEvent.KEYCODE_MEDIA_PAUSE :																		//127
									dbMsg += ")PAUSEボタン";
									MPSIntent.setAction(MusicPlayerService.ACTION_PAUSE);
									break;
								case KeyEvent.KEYCODE_MEDIA_NEXT :																		//87
									dbMsg += ")NEXTボタン";
									MPSIntent.setAction(MusicPlayerService.ACTION_SKIP);	
									break;
								case KeyEvent.KEYCODE_MEDIA_PREVIOUS :																		//88
									dbMsg += ")PREVIOUSボタン";
									MPSIntent.setAction(MusicPlayerService.ACTION_REWIND);	
									break;
								default:
									break;
								}
								dbMsg +=">指定するAction>" + MPSIntent.getAction();/////////////////////////////////////
								context.startService(MPSIntent);	//startService(new Intent(MusicPlayerService.ACTION_PAUSE));
							}
						}
						dbMsg += "、stateBaseStr=" + stateBaseStr;
						retBTinfo( stateBaseStr ,  context);							//情報を呼出し元に返す
					}
// Need BLUETOOTH permission: Neither user 10840 nor current process has android.permission.BLUETOOTH.
// Dead object in registerRemoteControlClientandroid.os.DeadObjectException

//					AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//					dbMsg += "、STATE_NOT_PLAYING:";
//					if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
//						KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
//						if (KeyEvent.KEYCODE_MEDIA_PLAY == event.getKeyCode()) {						// 再生ボタンを押したよ
//							if (audioManager.isBluetoothA2dpOn()) {								// Bluetoothで再生中
//							}
//						}
//					}
			//		getServiceDisconnected();				//RFCOMMチャンネルでSPPプロファイルの接続受け付けを可能とする

					myLog(TAG, dbMsg);
				} catch (Exception e) {		//汎用
					myErrorLog(TAG ,  dbMsg + "で" + e);
				}
//			}			//public void run() {
//		});				//btHandler.post(new Runnable() {
	}

		//APIL16		http://www.atmarkit.co.jp/ait/articles/1210/17/news018.html
		//APIL18		http://blog.fenrir-inc.com/jp/2013/10/bluetooth-le-android.html

		public void retBTinfo(String stateBaseStr , Context context){								//情報を呼出し元に返す
			final String TAG = "retBTinfo[BuletoohtReceiver]";
			String dbMsg="開始";/////////////////////////////////////
			try {
				dbMsg +=",stateBaseStr=" +stateBaseStr;
				dbMsg +=",activity=" +activity;
				if (activity != null){
//					TextView dviceStyte = (TextView) activity.findViewById(R.id.dviceStyte);
//					dviceStyte.setText(stateBaseStr);
					activity.setBTinfo(stateBaseStr);
				}
//				dbMsg +=",service=" +service;
//				if (service != null){
//					service.stateBaseStr = stateBaseStr;
//					service.setBTinfo(stateBaseStr);
//				}
		//		myLog(TAG, dbMsg);
				if( stateBaseStr != null ){
					Toast.makeText( context, stateBaseStr, Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
		}

		/**
		 * BluetoothAdapterとBluetoothProfile.ServiceListenerを生成し
		 * 接続状況を読み取る
		 * 呼出し元	onReceive
		 * */
		@SuppressLint("MissingPermission")
		public String getPairedDevices(final Context context, Intent intent){								//ペアリング機器の検索
			final String TAG = "getPairedDevices[BuletoohtReceiver]";
			String dbMsg="開始";/////////////////////////////////////
			String retStr="";
			String wStr = "";
			int prevState = 0;
			Bundle extras = null;
			try {
				if( mBluetoothAdapter == null ){
					mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
					mBluetoothAdapter.startDiscovery();														//検索開始
				}
				Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();		//接続要求している他デバイスをリストアップ
				if(pairedDevices.toArray().length>0){		//ペアリンされている対象デバイス数pairedDevices.size();と同じ
					extras = intent.getExtras();
					prevState = extras.getInt(BluetoothAdapter.EXTRA_PREVIOUS_STATE);			//ACTION_STATE_CHANGEDアクションのint型の追加フィールド。以前の電源状態を示す。とりうる値はSTATE_OFF, STATE_TURNING_ON、STATE_ON、STATE_TURNING_OFFである。
					dbMsg= "prevState=" + prevState  +",State="+mBluetoothAdapter.getState();
					for (BluetoothDevice device : pairedDevices) {
						wStr=device.getName()+";prevState="+prevState+",ACTION_REQUEST_DISCOVERABLE="+extras.getString(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			//			Log.i("getPairedDevices",device.getName()+"；describeContents"+device.describeContents()+",CREATOR;"+device.CREATOR.toString()+",contains;"+pairedDevices.contains(device)+",prevState="+prevState);
						dbMsg= dbMsg  +",getScanMode="+mBluetoothAdapter.getScanMode();
						switch (mBluetoothAdapter.getScanMode()) {
						case BluetoothAdapter.STATE_OFF:									//10;使用機のアダプタがoff
							wStr=  context.getResources().getString(R.string.bt_msg10);					//"BluetoothがOffになっています。";
							break;
						case BluetoothAdapter.STATE_TURNING_ON:
							wStr=device.getName()+ context.getResources().getString(R.string.bt_msg11);					//"がもうすぐ使えるようになります。しかし、STATE_ON メッセージが来るまでは実際に使うことはできません。
							break;
						case BluetoothAdapter.STATE_ON:										//12//BluetoothローカルアダプターがONになろうとしていることを示す。しかし、ローカルのクライアントはSTATE_ONになるのを待ってからアダプターを使うべきである。
							wStr= context.getResources().getString(R.string.bt_msg12);					//"Bluetoothは使用可能です";
							//							dviceStyte.setText(wStr);			// メッセージ出力
//							if(setuzokuBT_Address.equals("")){
//								retStr= device.getAddress();
//							}
//							dviceStyte.setText(wStr);			// メッセージ出力
							break;
						case BluetoothAdapter.STATE_TURNING_OFF:							//13/BluetoothローカルアダプターがOFFになろうとしていることを示す。ローカルのクライアントは可及的速やかにリモートとの接続を切断すべきである。
							wStr=device.getName()+  context.getResources().getString(R.string.bt_msg13);			//" がもうすぐ使えなくなります。";		//ただちに現在の Bluetooth 接続の後処理をして下さい。
							break;
						case BluetoothAdapter.SCAN_MODE_NONE:									//20	つまり、このデバイスはリモートデバイスからは発見できない。しかし以前にこのデバイスを発見しているリモートデバイスからは接続できる。
							wStr=device.getName()+  context.getResources().getString(R.string.bt_msg20);	//+" 問い合わせスキャン無効/呼び出しスキャン有効";
							break;
						case BluetoothAdapter.SCAN_MODE_CONNECTABLE:				//21//Bluetoothローカルアダプターへのな状態を示す。つまり、このデバイスはリモートデバイスから発見でき、接続も可能である。
							wStr=device.getName()+  context.getResources().getString(R.string.bt_msg21);	//+" 問い合わせ/スキャンが共に有効";
							break;
						case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:				//23Bluetoothローカルアダプターへの問い合わせスキャンと呼び出しスキャンが共に有効な状態を示す。つまり、このデバイスはリモートデバイスから発見でき、接続も可能である。
							wStr=device.getName()+  context.getResources().getString(R.string.bt_msg23);	//++"は問い合わせ/呼び出しスキャン有効";
							break;
						}
		//ublic static final String ACTION_STATE_CHANGED;		//ローカルデバイスの電源状態を変更したことを示す。例えば、Bluetoothの電源をONにする、あるいはOFFにするなど。
//				このアクションはつねにEXTRA_STATE追加フィールドに新しい電源状態、EXTRA_PREVIOUS_STATE追加フィールドの以前の電源状態を設定する。BLUETOOTHパーミッションを必要とする。
		//public static final int ERROR	//エラーの発生を示す値。
//			ERRORの値は、このクラスの他のint型の定数と重複しない値である。例えば、メソッドがエラーを返したことを判定するために以下のようにする。
//			Intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
		//public static final String EXTRA_DISCOVERABLE_DURATION;	//検出可能モードを持続させる秒数を指定する。デフォルトの持続期間は120秒で、300秒を越える指定は切り詰める。
		//public static final String EXTRA_LOCAL_NAME;	//ACTION_LOCAL_NAME_CHANGEDアクションのString型の追加フィールド。ローカルデバイス名を示す。
		//public static final String EXTRA_PREVIOUS_SCAN_MODE;	//ACTION_SCAN_MODE_CHANGEDアクションのint型の追加フィールド。以前のスキャンモードを示す。とりうる値はSCAN_MODE_NONE、SCAN_MODE_CONNECTABLE、SCAN_MODE_CONNECTABLE_DISCOVERABLEである。
		//public static final String EXTRA_SCAN_MODE			//ACTION_SCAN_MODE_CHANGEDアクションのint型の追加フィールド。現在のスキャンモードを示す。とりうる値はSCAN_MODE_NONE、SCAN_MODE_CONNECTABLE、SCAN_MODE_CONNECTABLE_DISCOVERABLEである。
		//public static final String EXTRA_STATE;				//ACTION_STATE_CHANGEDアクションのint型の追加フィールド。現在の電源状態を示す。とりうる値はSTATE_OFF, STATE_TURNING_ON、STATE_ON、STATE_TURNING_OFFである。
					}
//					if( mBluetoothAdapter.getScanMode() == BluetoothAdapter.STATE_OFF ||
//							mBluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_NONE){
//						dviceStyte.setVisibility(View.GONE);
//					} else {
//						dviceStyte.setVisibility(View.VISIBLE);
//						dviceStyte.setText(wStr);			// メッセージ出力
//					}
//				} else {
//					dviceStyte.setVisibility(View.GONE);
				}
				dbMsg +="wStr=" +wStr;
				retBTinfo( wStr ,  context);							//情報を呼出し元に返す
				myLog(TAG, dbMsg);
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
			}
			return wStr;
		}

		private ArrayAdapter<String> mConnectedDevices;
		private BluetoothHeadset mBluetoothHeadset;
		private BluetoothA2dp mBluetoothA2dp;
		private BluetoothHealth mBluetoothHealth;
		private BluetoothAdapter defaulthAdapter = BluetoothAdapter.getDefaultAdapter();
		private List<BluetoothDevice> mVoicerecognizing = Collections.synchronizedList(new ArrayList<BluetoothDevice>());
		/**
		 * http://www.atmarkit.co.jp/ait/articles/1210/17/news018.html
		 * */
		private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
			@Override
			public void onServiceConnected(int profile, BluetoothProfile proxy) {
				final String TAG = "onServiceConnected[BuletoohtReceiver]";
				String dbMsg="開始";/////////////////////////////////////
				try {
					String kind = null;
					dbMsg="profile=" + profile;
					if (profile == BluetoothProfile.HEADSET) {				//1
						mBluetoothHeadset = (BluetoothHeadset) proxy;
						mBluetoothAdapter.getProfileProxy(rContext, mProfileListener, BluetoothProfile.HEADSET);
						kind = "Headset";
						uuidStr = "00001108-0000-1000-8000-00805F9B34FB";			//接続先のサービス	http://human-ook.com/tips/archives/207
					} else if (profile == BluetoothProfile.A2DP) {			//2
						mBluetoothA2dp = (BluetoothA2dp) proxy;
						mBluetoothAdapter.getProfileProxy(rContext, mProfileListener, BluetoothProfile.A2DP);
						kind = "A2DP";
						uuidStr = "0000110A-0000-1000-8000-00805F9B34FB";			//AudioSource
			//			uuidStr = "0000110B-0000-1000-8000-00805F9B34FB";			//AudioSink
					} else if (profile == BluetoothProfile.HEALTH) {		//3
						mBluetoothHealth= (BluetoothHealth) proxy;
						mBluetoothAdapter.getProfileProxy(rContext, mProfileListener, BluetoothProfile.HEALTH);
						kind = "Health";
						uuidStr = "00001401-0000-1000-8000-00805F9B34FB";			//HDP Source
			//			uuidStr = "00001402-0000-1000-8000-00805F9B34FB";			//HDP Sink
					} else  {		//Serial Port Profile (SPP)
						uuidStr = "	00001101-0000-1000-8000-00805F9B34FB";	
					}
					dbMsg= dbMsg +";" + kind;
					dbMsg= dbMsg +"、uuidStr=" + uuidStr;
//					List<BluetoothDevice> devices = proxy.getConnectedDevices();
//					String[] names = new String[devices.size()];
//					for (int i = 0; i < names.length; i++) {
//						BluetoothDevice device = devices.get(i);
//						names[i] = kind + "\n" + device.getName() + "\n" + device.getAddress();
//					}
//					mConnectedDevices.addAll(names);
//					dbMsg= dbMsg +",mConnectedDevices=" + mConnectedDevices.getCount() + "件";
					myLog(TAG, dbMsg);
				} catch (Exception e) {		//汎用
					myErrorLog(TAG ,  dbMsg + "で" + e);
				}
			}

			@Override
			public void onServiceDisconnected(int profile) {
				final String TAG = "onServiceDisconnected[BuletoohtReceiver]";
				String dbMsg="開始";/////////////////////////////////////
				try {
					dbMsg="profile=" + profile;
					List<String> names = new LinkedList<String>();
					for (int i = 0; i < mConnectedDevices.getCount(); i++) {
						String name = mConnectedDevices.getItem(i);
						if (profile == BluetoothProfile.HEADSET && name.startsWith("Headset")) {
							names.add(name);
						} else if (profile == BluetoothProfile.A2DP && name.startsWith("A2DP")) {
							names.add(name);
						} else if (profile == BluetoothProfile.HEALTH && name.startsWith("Health")) {
							names.add(name);
						}
					}
					for (String name : names) {
						mConnectedDevices.remove(name);
					}
					if (profile == BluetoothProfile.HEADSET) {
						mBluetoothHeadset = null;
					} else if (profile == BluetoothProfile.A2DP) {
						mBluetoothA2dp = null;
					} else if (profile == BluetoothProfile.HEALTH) {
						mBluetoothHealth = null;
					}
					dbMsg= dbMsg +",mConnectedDevices=" + mConnectedDevices.getCount() + "件";
					myLog(TAG, dbMsg);
				} catch (Exception e) {		//汎用
					myErrorLog(TAG ,  dbMsg + "で" + e);
				}
			}
		};
		
		/**
		 * RFCOMMチャンネルでSPPプロファイルの接続受け付けを可能とする
		 * 参考　別の Bluetooth 機器からの接続要求を受ける（サーバー端末として振る舞う）http://cdn.commucom.jp/techinstitute/pdf/Android_Volume06_chap_14.pdf#search='Android++++BluetoothAdapter+BluetoothA2dp+%E5%84%AA%E5%85%88'
		 * **/
		public void getServiceDisconnected(){				//RFCOMMチャンネルでSPPプロファイルの接続受け付けを可能とする
			final String TAG = "getServiceDisconnected[BuletoohtReceiver]";
			String dbMsg="開始";/////////////////////////////////////
			try {
				if( mBluetoothAdapter == null){
					mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				}
				@SuppressLint("MissingPermission") BluetoothServerSocket serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("SampleServerConn",UUID.fromString(uuidStr));

				dbMsg="serverSocket = " + serverSocket;
				BluetoothSocket socket = serverSocket.accept();
				if (socket != null) { // クライアントからの接続要求を受け付け接続が完了した状態
						dbMsg +=",socket = " + socket;
				 // データの送受信処理などを行う
						serverSocket.close();
				 }	
				myLog(TAG, dbMsg);
			} catch (Exception e) {		//汎用
				myErrorLog(TAG ,  dbMsg + "で" + e);
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

//http://android.keicode.com/basics/services-communicate-broadcast-receiver.php
//http://www.sakc.jp/blog/archives/24996
//Android Bluetooth機器(HSP/HFP)のボタンイベントの受け取り方法		http://tf-web.jp/android-bluetoot-hphsp-button/
//Bluetooth通信														https://github.com/TechBooster/AndroidOpenTextbook/blob/master/articles/netdevice-01.re
