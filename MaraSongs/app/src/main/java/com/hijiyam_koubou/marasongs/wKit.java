package com.hijiyam_koubou.marasongs;

import java.io.File;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import net.nend.android.NendAdListener;
import net.nend.android.NendAdView;
import net.nend.android.NendAdInterstitial.NendAdInterstitialStatusCode;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class wKit extends Activity {

	public WindowManager wm;			// ウィンドウマネージャのインスタンス取得
	public int dWidth;								//ディスプレイ幅
	public int dHeigh;							//ディスプレイ高

	public WebView webView;
    public WebSettings settings;
    public Toolbar toolbar;						//このアクティビティのtoolBar
    public LinearLayout wk_bottm_ll;		//最下部のリニアレイアウト
    public LinearLayout wk_koukoku_ll;		//最下部の広告リニアレイアウト
    public ImageButton wk_rew_bt;			//戻しボタン
    public ImageButton wk_pp_bt;			//再生/ポーズボタン
    public ImageButton wk_ff_bt;			//送りボタン
    public String dbMsg="";
    public String fName=null;
    public String MLStr="";
    public String dataURI="";
    public String motoFName;				//htmlのファイル名
    public String fType="";
    public String baseUrl="";
    public boolean En_ZUP=true;			//ズームアップメニュー有効
    public boolean En_ZDW=true;			//ズームアップメニュー無効
    public boolean En_FOR=false;			//1ページ進む";
    public boolean En_BAC=false;			//1ページ戻る";
    public CharSequence btStre;
	//プリファレンス設定
	public SharedPreferences sharedPref;
	public Editor myEditor ;

	public static final int MENU_WQKIT=800;							//これメニュー
	public static final int MENU_WQKIT_ZUP = MENU_WQKIT+1;			//ズームアップ
	public static final int MENU_WQKIT_ZDW = MENU_WQKIT_ZUP+1;		//ズームダウン
	public static final int MENU_WQKIT_FOR = MENU_WQKIT_ZDW+1;		//1ページ進む
	public static final int MENU_WQKIT_BAC = MENU_WQKIT_FOR+1;		//1ページ戻る
	public static final int MENU_WQKIT_END = MENU_WQKIT_BAC+10;		//webkit終了

	public final CharSequence CTM_WQKIT_ZUP = "ズームアップ";
	public final CharSequence CTM_WQKIT_ZDW = "ズームダウン";
	public final CharSequence CTM_WQKIT_FOR  = "1ページ進む";
	public final CharSequence CTM_WQKIT_BAC = "1ページ戻る";
	public final CharSequence CTM_WQKIT_END = "表示終了";

	public Intent MPSIntent;
	public String psSarviceUri;
	public ComponentName MPSName;
	public MusicPlayerService MPS;


	public void readPref () {        //プリファレンスの読込み
		final String TAG = "readPref";
		String dbMsg = "[MuList]";
		try {
			MyPreferences myPreferences = new MyPreferences();
			dbMsg += "MyPreferencesy読込み";
			myPreferences.readPrif(this);
			sharedPref =myPreferences.sharedPref;
			myEditor =myPreferences.myEditor;
			myLog(TAG, dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG ,  dbMsg + "で" + e);
		}
	}																	//設定読込・旧バージョン設定の消去



	@Override
	protected void onCreate(Bundle savedInstanceState) {		//org;publicvoid
		super.onCreate(savedInstanceState);
		final String TAG = "onCreate[wKit]";
		String dbMsg ="開始";
		try{
			Bundle extras = getIntent().getExtras();
			dataURI = extras.getString("dataURI");						//最初に表示するページのパス
			dbMsg = "dataURI="+dataURI;////////////////////////////////////////////////////////////////////////
			fType = extras.getString("fType");							//データタイプ
			dbMsg +=",fType="+fType;////////////////////////////////////////////////////////////////////////
			if(! dataURI.startsWith("http")){				//web指定で無ければ
				String loadStr = extras.getString("loadStr");						//最初に表示するページのパス
				dbMsg +=",loadStr="+loadStr;////////////////////////////////////////////////////////////////////////
				baseUrl = "file://"+extras.getString("baseUrl");				//最初に表示するページを受け取る
				dbMsg += ",baseUrl="+baseUrl;////////////////////////////////////////////////////////////////////////
				motoFName = extras.getString("motoFName");				//htmlのファイル名
				String[] testSrA=dataURI.split(File.separator);
				fName=testSrA[testSrA.length-1];
				dbMsg +=",fName="+ fName;////////////////////////////////////////////////////////////////////////
			}
			requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 		//ローディングをタイトルバーのアイコンとして表示☆リソースを読み込む前にセットする
			wm = (WindowManager)getSystemService(WINDOW_SERVICE);			// ウィンドウマネージャのインスタンス取得
			Display disp = wm.getDefaultDisplay();										// ディスプレイのインスタンス生成
			dWidth = disp.getWidth();								//ディスプレイ幅
			dHeigh = disp.getHeight();							//ディスプレイ高
			dbMsg +="ディスプレイ["+dWidth+" × "+ dHeigh +"]" ;/////////////////////////////////////////////////////////////////////////////////////////////////////////
			setContentView(R.layout.wk_view);

			toolbar = (Toolbar) findViewById(R.id.wk_tool_bar);						//このアクティビティのtoolBar
	//		toolbar.setTitle(dataURI);
			toolbar.inflateMenu(R.menu.wk_menu);										// ツールバーにメニューをインフレート
			// toolbar（実際はmenu/main.xml）にセットされたアイテムがクリックされた時の処理
			toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					onOptionsItemSelected(item);
					return true;
				}
			});//

			webView = (WebView) findViewById(R.id.webview);		// Webビューの作成
			webView.setVerticalScrollbarOverlay(true);					//縦スクロール有効
//			setProgressBarIndeterminateVisibility(true);
			wk_bottm_ll = (LinearLayout) findViewById(R.id.wk_bottm_ll);		//最下部のリニアレイアウト
			wk_rew_bt = (ImageButton) findViewById(R.id.wk_rew_bt);			//戻しボタン
			wk_pp_bt = (ImageButton) findViewById(R.id.wk_pp_bt);				//再生/ポーズボタン
			wk_ff_bt = (ImageButton) findViewById(R.id.wk_ff_bt);				//送りボタン
			wk_koukoku_ll = (LinearLayout) findViewById(R.id.wk_koukoku_ll);		//最下部の広告リニアレイアウト

			settings = webView.getSettings();
			settings.setSupportMultipleWindows(true);
			settings.setLoadsImagesAutomatically(true);
			settings.setBuiltInZoomControls(true);						//ズームコントロールを表示し
			settings.setSupportZoom(true);								//ピンチ操作を有効化
		//	settings.setLightTouchEnabled(true);
			settings.setJavaScriptEnabled(true);						//JavaScriptを有効化

			MLStr=dataURI;
			dbMsg += "," + fType+"をMLStr="+MLStr;////////////////////////////////////////////////////////////////////////
			dbMsg += ",StandardFontFamil="+settings.getStandardFontFamily();	//ShinGo-Medium
			dbMsg += ",Encoding="+settings.getDefaultTextEncodingName();		//Shift_JIS
			webView.loadUrl(String.valueOf(MLStr));

//			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);		//タスクバーを 非表示
//			requestWindowFeature(Window.FEATURE_NO_TITLE); 							//タイトルバーを非表示

			webView.setWebViewClient(new WebViewClient() {		//リンク先もこのWebViewで表示させる；端末のブラウザを起動させない
				 @Override
				 public void onPageStarted(WebView view, String url, Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
					setProgressBarIndeterminateVisibility(true);
						setTitle(url); 	//タイトルバーに文字列を設定
					}
				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					if(fName==null){
						String tStr="";
						tStr=webView.getTitle();
						String dbMsg = "tStr="+tStr;////////////////////////////////////////////////////////////////////////
//						myLog("onPageFinished","wKit；"+dbMsg);
				//		Toast.makeText(webView.getContext(), webView.getTitle(), Toast.LENGTH_LONG).show();
						setTitle(webView.getTitle()); 	//タイトルバーに文字列を設定
						if(tStr.equals("")){
							tStr = dataURI;
						}
						wKit.this.toolbar.setTitle(tStr);
					}
					setProgressBarIndeterminateVisibility(false);
				}

			});
//			webView.loadUrl(requestToken);
			if(fType.equals("lyric")){
		//		motoFName = dataURI.substring(String.valueOf("file://").length(), dataURI.length());
				 mHandler = new Handler();
				 dbMsg= dbMsg+",mReceiver=" + mReceiver;////////////////////////
				if( mReceiver== null ){
					mFilter = new IntentFilter();
					mFilter.addAction(MusicPlayerService.ACTION_STATE_CHANGED);
					mReceiver = new MusicReceiver();
					registerReceiver(mReceiver, mFilter);
					dbMsg +=">生成>=" + mReceiver;////////////////////////
				}
				if( MPSIntent == null ){
					psSarviceUri = getPackageName() + getResources().getString(R.string.psSarviceUri);		//プレイヤーサービス	"com.hijiyam_koubou.marasongs.PlayerService";
					dbMsg= dbMsg +">>psSarviceUri=" + psSarviceUri + "";/////////////////////////////////////
					MPSIntent = new Intent(wKit.this, MusicPlayerService.class);
				}
				MPSIntent.setAction(MusicPlayerService.ACTION_DATA_OKURI);				//データ送りのみ
				MPSName = startService(MPSIntent);		//onStartCommandへ	//startService(new Intent(MusicPlayerService.ACTION_SKIP));
				dbMsg +=" ,ComponentName=" + MPSName + "";/////////////////////////////////////
				wk_rew_bt.setOnClickListener(new View.OnClickListener() {			//戻しボタン
					public void onClick(View v) {
						final String TAG = "wk_rew_bt[wKit]";
						String dbMsg= "開始;";/////////////////////////////////////
						try{
							wk_pp_bt.setContentDescription(getResources().getText(R.string.play));			//処理後は再生
							MPSIntent.setAction(MusicPlayerService.ACTION_REWIND);
							MPSName = startService(MPSIntent);	//startService(new Intent(MusicPlayerService.ACTION_REWIND));
							dbMsg +=" ,ComponentName=" + MPSName + "";/////////////////////////////////////
				//			myLog(TAG,dbMsg);
						}catch (Exception e) {
							myErrorLog(TAG,dbMsg + "で"+e + "");
						}
					}
				});
				wk_pp_bt.setOnClickListener(new View.OnClickListener() {			//再生/ポーズボタン
					public void onClick(View v) {
						final String TAG = "wk_pp_bt[wKit]";
						String dbMsg= "開始;";/////////////////////////////////////
						try{
							btStre = wk_pp_bt.getContentDescription();
							dbMsg= "btStre=" + btStre;
							if(  btStre.equals(getResources().getText(R.string.play)) ){						//&& ppPBT.getContentDescription().equals(getResources().getText(R.string.pause))
								wk_pp_bt.setImageResource(R.drawable.pouse40);
								wk_pp_bt.setContentDescription(getResources().getText(R.string.play));			//pause
							} else {
								wk_pp_bt.setImageResource(R.drawable.play40);
								wk_pp_bt.setContentDescription(getResources().getText(R.string.pause));			//play
							}
							dbMsg +=">>" +  btStre;
							dbMsg=  "MPSIntent=" + MPSIntent;/////////////////////////////////////
							dbMsg +=".getAction=" + MPSIntent.getAction();/////////////////////////////////////
							MPSIntent.setAction(MusicPlayerService.ACTION_PLAYPAUSE);
							dbMsg +=">>" + MPSIntent.getAction();/////////////////////////////////////
							MPSName = startService(MPSIntent);	//startService(new Intent(MusicPlayerService.ACTION_PAUSE));
							dbMsg +=" ,ComponentName=" + MPSName;/////////////////////////////////////
		//					myLog(TAG,dbMsg);
						}catch (Exception e) {
							myErrorLog(TAG,dbMsg + "で"+e + "");
						}
					}
				});
				wk_ff_bt.setOnClickListener(new View.OnClickListener() {			//送りボタン
					public void onClick(View v) {
						final String TAG = "wk_ff_bt[wKit]";
						String dbMsg= "開始;";/////////////////////////////////////
						try{
							wk_pp_bt.setContentDescription(getResources().getText(R.string.play));			//処理後は再生
							MPSIntent.setAction(MusicPlayerService.ACTION_SKIP);
							MPSName = startService(MPSIntent);		//onStartCommandへ	//startService(new Intent(MusicPlayerService.ACTION_SKIP));
							dbMsg +=" ,ComponentName=" + MPSName + "";/////////////////////////////////////
				//			myLog(TAG,dbMsg);
						}catch (Exception e) {
							myErrorLog(TAG,dbMsg + "で"+e + "");
						}
					}
				});
			}else{
				wk_rew_bt.setVisibility(View.GONE);			//戻しボタン
				wk_pp_bt.setVisibility(View.GONE);			//再生/ポーズボタン
				wk_ff_bt.setVisibility(View.GONE);			//送りボタン
				settings.setUseWideViewPort(true);							//読み込んだコンテンツの幅に表示倍率を自動調整
				settings.setLoadWithOverviewMode(true);						//☆setUseWideViewPortに続けて記載必要
			}

			layout_ad = (LinearLayout) findViewById(R.id.wk_ad_ll);
			layout_ad.setVisibility(View.GONE);
			adMobNow = false;				//AdView優先
			nendAdView = (NendAdView) findViewById(R.id.wk_nend);
			nenvNow = false;
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,e + "");
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {		//①ⅱヘッドのイメージは実際にローディンされた時点で設定表示と同時にウィジェットの高さや幅を取得したいときは大抵ここで取る。
		if (hasFocus) {
			final String TAG = "onWindowFocusChanged[wKit]";
			String dbMsg= "開始;";/////////////////////////////////////
			try{
				dbMsg= "fType=" + fType;/////////////////////////////////////
				dbMsg +="ディスプレイ["+dWidth+" × "+ dHeigh +"]" ;/////////////////////////////////////////////////////////////////////////////////////////////////////////
				if(fType.equals("lyric") ){
					dbMsg +=",wk_ff_bt.isShown" + wk_ff_bt.isShown();/////////////////////////////////////
					int bHight = wk_bottm_ll.getHeight();
					dbMsg +=",bHight=" + bHight;						//206SH/bHight=150
					if(50 < bHight){
					}
					CharSequence btStre = wk_pp_bt.getContentDescription();
					if( btStre == null ){
						wk_pp_bt.setContentDescription(getResources().getText(R.string.pause));			//処理後はポーズ何か設定しておかないと
					}
				}else{
					ViewGroup.LayoutParams params = wk_koukoku_ll.getLayoutParams();
					params.width = dWidth;									//wk_koukoku_ll.getHeight();				// 縦幅に合わせる
					params.height = LinearLayout.LayoutParams.WRAP_CONTENT;									//wk_koukoku_ll.getHeight();				// 縦幅に合わせる
					dbMsg= dbMsg +",params=" + params.width +"×" + params.height;/////////////////////////////////////
					wk_koukoku_ll.setLayoutParams(params);
				}
				Window window = getWindow();
				Rect rect = new Rect();
				window.getDecorView().getWindowVisibleDisplayFrame(rect);			//getGlobalVisibleRect,getLocationOnScreenと変わらず
				int statusBarHeight = rect.top;									// ステータスバーの高さ=75 	http://sukohi.blogspot.jp/2013/11/android.html
				dbMsg += ",statusBarHeight=" + statusBarHeight;
				int motoHaba = wk_koukoku_ll.getWidth();
				int motoTakasa = wk_koukoku_ll.getHeight();
				dbMsg +="motoHaba["+ motoHaba +" × "+ motoTakasa + "]" ;/////////////////////////////////////////////////////////////////////////////////////////////////////////
				int[] location = new int[2];
				wk_koukoku_ll.getLocationOnScreen(location);							//http://y-anz-m.blogspot.jp/2012/10/androidview.html			中心？
				koukokuL = location[0];			//広告表示枠幅
				koukokuT = location[1];			//広告表示枠高さ
				dbMsg +=",広告表示枠("+ koukokuL +","+ koukokuT +")]" ;						//広告表示枠[798 × 6(0.0,148.0)]
				koukokuW= dWidth - location[0] - wk_ff_bt.getWidth();			//広告表示枠
				koukokuh = wk_koukoku_ll.getHeight();			//広告表示枠高さ
				dbMsg +="["+ koukokuW +" × "+ koukokuh + "]";/////////////////////////////////////////////////////////////////////////////////////////////////////////
				if( ! nenvNow){
					layout_ad.setVisibility(View.GONE);
					nendAdView.setVisibility(View.VISIBLE);
					nendLoad();		//nendの広告設定
				}
				dbMsg +="nenvNow=" + nenvNow;/////////////////////////////////////
				dbMsg +=",adMobNow=" + adMobNow;/////////////////////////////////////
//				if(! adMobNow){
//					nendAdView.setVisibility(View.GONE);
//					layout_ad.setVisibility(View.VISIBLE);
//					dbMsg += ",layout_ad[" + layout_ad.getWidth() + "×" +  layout_ad.getHeight();
//					dbMsg += "(" + layout_ad.getX() + "," + layout_ad.getY() + ")]";
//					adMobLoad();													//Google AdMobの広告設定
//				}
	//			myLog(TAG,dbMsg);
			}catch (Exception e) {
				myErrorLog(TAG,dbMsg + "で"+e + "");
			}
		 }
		 super.onWindowFocusChanged(hasFocus);
	 }

	public int koukokuW;			//広告表示枠幅
	public int koukokuh;			//広告表示枠高さ
	public int koukokuL;			//広告表示枠幅
	public int koukokuT;			//広告表示枠高さ
//	public LinearLayout nend_aria;				//nendの読み込み範囲
	public NendAdView nendAdView;
	public int nendWith = 0;								//変更する幅
	public int tHigh;							//変更する高さ
	public float scaleXY;					//広告の縮小率
	boolean nenvNow = false;
	/**
	 * nendの広告設定
	 * 参考	https://github.com/fan-ADN/nendSDK-Android/wiki/%E3%83%90%E3%83%8A%E3%83%BC%E5%9E%8B%E5%BA%83%E5%91%8A_%E5%AE%9F%E8%A3%85%E6%89%8B%E9%A0%86
	 * */
	public void nendLoad(){		//nendの広告設定
		final String TAG = "nendLoad[wKit]";
		String dbMsg = "";//////////////////
		try{
			int[] location = new int[2];
			float dispScaleX = koukokuW /320;					//送信される時の拡大率
			float dispScaleY = koukokuh/50;
			dbMsg += ",dispScale[" + dispScaleX + "×" + dispScaleY + "]";
			if( nendAdView != null ){						//	☆二重取得防止
				dbMsg += ",元[" + nendAdView.getWidth() + "×" + nendAdView.getHeight() + "]";		//拾えずgetDrawingRect,getMeasuredWidth,getX同様
				nendAdView.getLocationOnScreen(location);
				int motoX = location[0];
				int motoY = location[1];
				dbMsg += "(motoXY;" + motoX + "," + motoY + ")";
				nendWith = koukokuW;					// koukokuW / 2
				dbMsg += ",nendWith=" + nendWith;
				scaleXY = nendWith * 1.0f/ dWidth;						//設定したサイズが読み込みリアを超えない様に320に対してマージンを設定		//kariHaba * 1.0f / tWith ; = 0.37
				dbMsg +=",scaleXY=" + scaleXY;						//scaleXY=0.36944443
				nendAdView.setScaleX(scaleXY);
				nendAdView.setScaleY(scaleXY);
				dbMsg += ",リサイズ[" + nendWith + "(" + (320 * scaleXY)  + "×" + (50 * scaleXY) + ")]";		//利かない		nendAdView.setGravity(Gravity.CENTER);
				nendAdView.getLocationOnScreen(location);
				dbMsg +=",ズレ("+ location[0] +","+ location[1] +")]" ;						//広告表示枠[798 × 6(0.0,148.0)]
				int zureX = (int) -( ((koukokuW - (koukokuW * scaleXY))/2) * dispScaleX -wk_ff_bt.getWidth()/dispScaleX);				//(int) ((motoX - location[0]) * 1/scaleXY)	motoX - location[0];
				int zureY = (int) - ( ((koukokuh - (koukokuh * scaleXY))/2) );		// * dispScaleX		//(int) ((motoY - location[1]) * 1/scaleXY);						//	((motoY - location[1]) * 1/scaleXY)
		//		int zureY = (int) ((koukokuT - motoY) * dispScaleX * 1 / scaleXY );								//(wk_ff_bt.getWidth() - koukokuh) /2 ;			//(int) ((motoY - location[1]) * 1/scaleXY);						//	((motoY - location[1]) * 1/scaleXY)
				dbMsg += "補正(" + zureX + "," + zureY + ")";			//ズレ(282,1642)
//				nendAdView.setX( zureX);							//(282,1642)>(564,3209)< * scaleXY=0.36=(386,2221)			 -(motoHaba-nendWith)
				nendAdView.setY( zureY); 				//(motoTakasa- (50 * scaleXY)) ///☆利かない	nendAdView.setX( dWidth - nendWith);		nendAdView.setGravity(Gravity.RIGHT | Gravity.TOP);
				nendAdView.getLocationOnScreen(location);							//http://y-anz-m.blogspot.jp/2012/10/androidview.html
				dbMsg +=",移動先("+ location[0] +","+ location[1] +")]" ;	//正解[0 × ?]拾えずgetWidthetPaddingLeft、、

				nendAdView.setListener(new NendAdListener() {
			//	    @Override
					public void onCompletion(NendAdInterstitialStatusCode status) {
						final String TAG = "onCompletion[nendLoad]";
						String dbMsg = "";//////////////////
						try{
							dbMsg = "NendAdInterstitialStatusCode="+status;//////////////////
							switch (status) {
							case SUCCESS:			// 成功
								dbMsg += "成功";//////////////////
								nenvNow = true;
//								if(prTT_dpad){
//									nendKeys();		//nebdの広告用のキー設定
//								}
								nenvNow = true;
								break;
							case INVALID_RESPONSE_TYPE:			// 不明な広告タイプ
								dbMsg += "不明な広告タイプ";//////////////////
								break;
							case FAILED_AD_REQUEST:		// 広告取得失敗
								dbMsg += "広告取得失敗";//////////////////
								nenvNow = false;
					//			nend_aria.setVisibility(View.GONE);
								break;
							case FAILED_AD_INCOMPLETE:		// 広告取得未完了
								dbMsg += "広告取得未完了";//////////////////
								break;
							case FAILED_AD_DOWNLOAD:		// 広告画像取得失敗
								dbMsg += "広告画像取得失敗";//////////////////
								nenvNow = false;
					//			nend_aria.setVisibility(View.GONE);
								break;
							default:
								break;
							}
							myLog(TAG,dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG,dbMsg+"で"+e);
						}
					}

					@Override
					public void onClick(NendAdView arg0) {	// クリック通知
						final String TAG = "onClick[nendLoad]";
						String dbMsg = "";//////////////////
						try{
					//		Toast.makeText(getApplicationContext(), "onClick", Toast.LENGTH_LONG).show();
							myLog(TAG,dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG,dbMsg+"で"+e);
						}
					}

					@Override
					public void onDismissScreen(NendAdView arg0) {	// 復帰通知
						final String TAG = "onDismissScreen[nendLoad]";
						String dbMsg = "";//////////////////
						try{
							dbMsg = "NendAdView = " + arg0;//////////////////
//							Toast.makeText(getApplicationContext(), "onDismissScreen", Toast.LENGTH_LONG).show();
				//			nendLoad();		//nendの広告設定
							myLog(TAG,dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG,dbMsg+"で"+e);
						}
					}

					@Override
					public void onFailedToReceiveAd(NendAdView arg0) {	// 受信エラー通知
						final String TAG = "onFailedToReceiveAd[nendLoad]";
						String dbMsg = "";//////////////////
						try{
							dbMsg = "NendAdView = " + arg0;//////////////////
//				//			Toast.makeText(getApplicationContext(), "onFailedToReceiveAd", Toast.LENGTH_LONG).show();
							nenvNow = false;
							nendAdView.setVisibility(View.GONE);
							adWidth = koukokuW -nendWith;								//( dWidth - ppPBT.getWidth() ) / 2 ;										//layout_ad.getWidth();
							int[] location = new int[2];
							wk_koukoku_ll.getLocationOnScreen(location);							//http://y-anz-m.blogspot.jp/2012/10/androidview.html			中心？
							android.view.ViewGroup.LayoutParams params = layout_ad.getLayoutParams();
							layout_ad.setLayoutParams(params);
							layout_ad.setX(location[0]);							//(282,1642)>(564,3209)< * scaleXY=0.36=(386,2221)			 -(motoHaba-nendWith)
							layout_ad.setY(location[1]); 				//(motoTakasa- (50 * scaleXY)) ///☆利かない	nendAdView.setX( dWidth - nendWith);		nendAdView.setGravity(Gravity.RIGHT | Gravity.TOP);
							layout_ad.getLocationOnScreen(location);							//http://y-anz-m.blogspot.jp/2012/10/androidview.html
					//		myLog(TAG,dbMsg);
							if(! adMobNow){
								adMobLoad();													//Google AdMobの広告設定
							}
						} catch (Exception e) {
							myErrorLog(TAG,dbMsg+"で"+e);
						}
					}

					@Override
					public void onReceiveAd(NendAdView arg0) {	// 受信成功通知
						final String TAG = "onReceiveAd[nendLoad]";
						String dbMsg = "";//////////////////
						try{
							dbMsg = "NendAdView = " + arg0;//////////////////
////				//			Toast.makeText(getApplicationContext(), "onReceiveAd", Toast.LENGTH_LONG).show();
//							dbMsg += ",dPadAri = " + prTT_dpad;//////////////////
////							if(prTT_dpad){
////								nendKeys();		//nebdの広告用のキー設定
////							}
//							myLog(TAG,dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG,dbMsg+"で"+e);
						}

					}
				});

				nenvNow = true;
			}else{
				dbMsg = "isActivated=" + nendAdView.isActivated() +  ",isEnabled=" + nendAdView.isEnabled() +  ",isShown=" + nendAdView.isShown();
			}
	//		myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);
		}
	}

	private AdView mAdView = null;							//Google広告表示エリア
	private  LinearLayout layout_ad;
	public int adWidth = 0;
	public int adHight = 0;
	private AdRequest adRequest;			// 一般的なリクエストを行う
	boolean adMobNow;
	public View adViewC;
	/**
	 * Google AdMobの広告設定
	 *
	 * 参考	https://developers.google.com/admob/android/existing-app?hl=ja
	 * 		http://ja.stackoverflow.com/questions/12820/android%E3%81%A7admob%E3%81%8C%E8%A1%A8%E7%A4%BA%E3%81%95%E3%82%8C%E3%81%AA%E3%81%84
	 * XML でバナーを作成する		https://developers.google.com/mobile-ads-sdk/docs/admob/fundamentals?hl=ja#header
	 * */
	@SuppressLint("NewApi")
	public void adMobLoad(){		//Google AdMobの広告設定
		final String TAG = "adMobLoad[wKit]";
		String dbMsg = "";//////////////////
		try{
			layout_ad.setVisibility(View.VISIBLE);
			int kariHaba = 256;
			int[] location = new int[2];
			dbMsg +=",adWidth=" + adWidth;
			if( adWidth == 0 ){
				layout_ad.getLocationOnScreen(location);							//http://y-anz-m.blogspot.jp/2012/10/androidview.html
				dbMsg +=",layout_ad("+ location[0] +","+ location[1] +")]" ;						//広告表示枠[798 × 6(0.0,148.0)]
				adWidth = koukokuW;				// ( dWidth-276) / 2 ;			// dWidth-location[0] ;//			//画面幅-ボタン幅 / 2		dWidth / 2 - dWidth / 20
				dbMsg +=">adWidth>" + adWidth;
			//	layout_ad.setX(dWidth - adWidth);
				layout_ad.getLocationOnScreen(location);							//http://y-anz-m.blogspot.jp/2012/10/androidview.html
				dbMsg +=">>layout_ad("+ location[0] +","+ location[1] +")]" ;						//広告表示枠[798 × 6(0.0,148.0)]
			}
			float scaleXY =adWidth * 1.0f / dWidth;						/// = 0.3		( koukokuW * koukokuW / dWidth)	☆設定したサイズが読み込みリアを超えない様に320に対してマージンを設定	288.0f/45		256/40
			dbMsg +=",scaleXY=" + scaleXY;
			adWidth = (int)( 320 * scaleXY);				//320x50_as;6.4	最終；getAdSize[118x18_as;6.55],(682,1621)]
			dbMsg += ",adWidth=" + adWidth;//	https://developers.google.com/mobile-ads-sdk/docs/admob/android/quick-start#faq
			adHight = (int) (50 * scaleXY);			//* kariHaba/320
			dbMsg += ",adHight=" + adHight;//	https://developers.google.com/mobile-ads-sdk/docs/admob/android/quick-start#faq
	//		if( mAdView == null ){
				layout_ad.getLocationOnScreen(location);							//http://y-anz-m.blogspot.jp/2012/10/androidview.html			中心？
				dbMsg +=",layout_ad("+ location[0] +","+ location[1] +")]" ;	//正解[0 × ?]
				mAdView = new AdView(this);
		//		LayoutParams lp = new LayoutParams(adWidth , LayoutParams.WRAP_CONTENT);
				mAdView.setAdUnitId(getResources().getString(R.string.banner_ad_unit_id2));
	//		}
			mAdView.setAdSize(new AdSize( adWidth , adHight));		//	mAdView.setAdSize(AdSize.BANNER);だと	Not enough space to show ad. Needs 320x50 dp, but only has 180x49 dp.
			layout_ad.addView(mAdView);			// ,lp

			AdRequest adRequest = new AdRequest.Builder().build();
		//	dbMsg += ",mAdView=" + mAdView;//	https://developers.google.com/mobile-ads-sdk/docs/admob/android/quick-start#faq
			//テスト		https://developers.google.com/mobile-ads-sdk/docs/admob/intermediate?hl=ja
			//未登録機はlogcatで03-21 21:21:41.232: I/Ads(10844): Use AdRequest.Builder.addTestDevice("EF6049FA0F4D49D1A08E68C5037D6302") to get test ads on this device.	を検索
			adRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)				// すべてのエミュレータ
				.addTestDevice("EF6049FA0F4D49D1A08E68C5037D6302")	//Xpelia Z5 (Android5.1.1)
				.addTestDevice("170A4E96B8EE2D03CFAB2DF201D0655D")	//SHL32(Android4.4.4/AQUOS K タッチパネル無し)
				.addTestDevice("F3B787B1C99665529E01E5BB0647FD8D")	//	304SH(Android4.4.2)
				.addTestDevice("772C6F3DB402CD1F9D8A66E1555E54C5")	//SH08E(Android4.2.2/7インチタブレット)
				.addTestDevice("B339C45F7878E57784B1940379760332")		//	206SH(Android4.2.2)
				.addTestDevice("EFF070C53D3F43AF29325F8E5529D704")	//	203SH(Android4.1.2)
				.addTestDevice("2CCFE123DEF10276C319F12B66D744FA")	//	iS15SH(Android4.0.3)Ads: Use AdRequest.Builder.addTestDevice("") to get test ads on this device.で取得
//				.tagForChildDirectedTreatment(true)									//児童向けで
				.build();
	//		dbMsg +="、getAdUnitId=" + mAdView.getAdUnitId();//ca-app-pub-3146425308522831/2530772303
	//		dbMsg += ",　request=" + adRequest;
			mAdView.loadAd(adRequest);
			mAdView.setAdListener(new AdListener() {
				@Override
				public void onAdOpened() {					// 広告オーバーレイに移動する前にアプリの状態を保存する
					final String TAG = "onAdOpened[adMobLoad]";
					String dbMsg = "広告からオーバーレイを開いて画面全体が覆われた";//////////////////
					try{
						dbMsg +="、mAdView=" + mAdView;
						myLog(TAG,dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG,dbMsg+"で"+e);
						}
					}

				@Override
				public void onAdLoaded() {
					final String TAG = "onAdLoaded[adMobLoad]";
					String dbMsg = "広告が表示された";//////////////////
					try{
						dbMsg +="、mAdView=" + mAdView;			//.AdView@41b75f70
						dbMsg += ",getChildCount=" + mAdView.getChildCount();						//1
						adMobNow = true;
						if(0<mAdView.getChildCount()){
							adViewC = mAdView.getChildAt(0);
			//				adViewC.setOnClickListener(AtarekunnActivity.this);
			//				adViewC.setOnKeyListener( AtarekunnActivity.this);			//	, View.OnKeyListener	使用時のみ
//							if(prTT_dpad){
//								adMobKeys();		//Google AdMobの広告用のキー設定
//							}
						}
						dbMsg +=",getChildAt=" + adViewC;
						dbMsg +=",ClassName=" + mAdView.getMediationAdapterClassName();		//null
						//errer発生	getTransitionName	getOutlineProvider	getOverlay
			//			myLog(TAG,dbMsg);
					} catch (Exception e) {
						myErrorLog(TAG,dbMsg+"で"+e);
					}
				}

				@SuppressLint("NewApi")
				@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
				@Override
				public void onAdClosed() {
					final String TAG = "onAdClosed[adMobLoad]";
					String dbMsg = "ユーザーが広告をクリックし、アプリケーションに戻ろうとした";//////////////////
					try{
						dbMsg = ",mAdView=" + mAdView;
						myLog(TAG,dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG,dbMsg+"で"+e);
						}
					}

				@Override
				public void onAdLeftApplication() {
					final String TAG = "onAdLeftApplication[adMobLoad]";
					String dbMsg = "広告からアプリケーションを終了した場合";//////////////////
					try{
						dbMsg +="、mAdView=" + mAdView;
						myLog(TAG,dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG,dbMsg+"で"+e);
						}
					}

				@Override
				public void onAdFailedToLoad(int errorCode) {
					final String TAG = "onAdFailedToLoad[adMobLoad]";
					String dbMsg = "広告リクエストが失敗した";//////////////////
					try{
						dbMsg += ":errorCode=" + errorCode;
						switch(errorCode) {
						case AdRequest.ERROR_CODE_INTERNAL_ERROR:
							dbMsg += ":ERROR_CODE_INTERNAL_ERROR";
							break;
						case AdRequest.ERROR_CODE_INVALID_REQUEST:
							dbMsg += ":ERROR_CODE_INVALID_REQUEST";
							break;
						case AdRequest.ERROR_CODE_NETWORK_ERROR:
							dbMsg += ":ERROR_CODE_NETWORK_ERROR";
							break;
						case AdRequest.ERROR_CODE_NO_FILL:
							dbMsg += ":広告が来ない";
			//				adMobNow = false;
					//		mAdView.setVisibility(View.GONE);
//							artist_tv.setNextFocusUpId(ppPBT.getId());
//							ppPBT.setNextFocusDownId(artist_tv.getId());
//							stopPButton.setNextFocusDownId(artist_tv.getId());		//タイトル
							break;
						}
						if( mAdView != null ){
							mAdView.destroy();
							dbMsg = ">>"+ mAdView;//////////////////
							layout_ad.setVisibility(View.GONE);
						}
						if( ! nenvNow){
							nendAdView.setVisibility(View.VISIBLE);
							nendLoad();		//nendの広告設定
						}
						myLog(TAG,dbMsg);
						} catch (Exception e) {
							myErrorLog(TAG,dbMsg+"で"+e);
						}
					}
				});
			dbMsg += "[" + mAdView.getHeight() + "×" + mAdView.getWidth() + "]";///is15[75×480]
			dbMsg +="ディスプレイ["+dWidth+" × "+ dHeigh +"]" ;/////////////////////////////////////////////////////////////////////////////////////////////////////////
//			dbMsg += ",layout_ad[" + layout_ad.getWidth() + "×" + layout_ad.getHeight() + "]";
//			layout_ad.getLocationOnScreen(location);							//http://y-anz-m.blogspot.jp/2012/10/androidview.html
//			dbMsg +="("+ location[0] +","+ location[1] +")]" ;						//広告表示枠[798 × 6(0.0,148.0)]
			dbMsg += ",最終；getAdSize[" + mAdView.getAdSize() + "]";
			mAdView.getLocationOnScreen(location);							//http://y-anz-m.blogspot.jp/2012/10/androidview.html
			dbMsg +=",("+ location[0] +","+ location[1] +")]" ;						//広告表示枠[798 × 6(0.0,148.0)]
			layout_ad.getLocationOnScreen(location);							//http://y-anz-m.blogspot.jp/2012/10/androidview.html
			dbMsg +=",layout_ad("+ location[0] +","+ location[1] +")]" ;						//広告表示枠[798 × 6(0.0,148.0)]
			adMobNow = true;
			myLog(TAG,dbMsg);
		} catch (Exception e) {
			myErrorLog(TAG,dbMsg+"で"+e);

			//04-03 20:59:46.519: W/cr_AwContents(7414): onDetachedFromWindow called when already detached. Ignoring
//04-03 20:59:46.591: W/cr_BindingManager(7414): Cannot call determinedVisibility() - never saw a connection for the pid: 7414
//			04-03 21:03:12.598: E/Ads(7414): JS: Uncaught ReferenceError: AFMA_ReceiveMessage is not defined (:1)


		}
	}
//http://pentan.info/android/app/multi_thread.html
/////////////////////////////////////////////////////////////////////////////////////////////////////

	public String retML(String dataStr){		//受け取ったデータによってHTMLを変える
		String retStr = null;
		try{
			dbMsg = "dataStr="+dataStr;////////////////////////////////////////////////////////////////////////

		} catch (Exception e) {
			myErrorLog("retML",dbMsg+"；"+e + "");
		}
		return retStr;
	}

	public void quitMe(){			//このActivtyの終了
		final String TAG = "onCreate[wKit]";
		String dbMsg ="";
		try{
			if(fType.equals("lyric")){
				 dbMsg= "mReceivert=" + mReceiver;////////////////////////
					if( mReceiver != null ){
						unregisterReceiver(mReceiver);									//MusicPlayerRemoteControlActivity
						mReceiver = null;
						dbMsg= ">>" + mReceiver;////////////////////////
					}
					if(mHandler != null){
						mHandler = null;
					}
			}
			wKit.this.finish();
		}catch (Exception e) {
			myErrorLog("quitMe","wKitで"+e + "");
		}
	}

	public boolean wZoomUp() {				//ズームアップして上限に達すればfalse
		try{
			En_ZUP=webView.zoomIn();			//ズームアップメニューのフラグ設定
		}catch (Exception e) {
			myErrorLog("wZoomUp",e + "");
			return false;
		}
		return En_ZUP;
	}

	public boolean wZoomDown() {				//ズームダウンして下限に達すればfalse
		try{
			En_ZDW=webView.zoomOut();			//ズームダウンのフラグ設定
		}catch (Exception e) {
			myErrorLog("wZoomDown",e + "");
			return false;
		}
		return En_ZDW;
	}

	public void wForward() {					//ページ履歴で1つ後のページに移動する
		try{
			webView.goForward();				//ページ履歴で1つ後のページに移動する
		}catch (Exception e) {
			myErrorLog("wForward",e + "");
		}
	}

	public void wGoBack() {					//ページ履歴で1つ前のページに移動する
		try{
			dbMsg="canGoBack="+webView.canGoBack();//+",getDisplayLabel="+String.valueOf(event.getDisplayLabel())+",getAction="+event.getAction();////////////////////////////////
	//		myLog("wGoBack",dbMsg);
			if(webView.canGoBack()){		//戻るページがあれば
				webView.goBack();					//ページ履歴で1つ前のページに移動する
			}else{							//無ければ終了
				wKit.this.finish();
			}
		}catch (Exception e) {
			myErrorLog("wGoBack",e + "");
		}
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		try{
			dbMsg="keyCode="+keyCode;//+",getDisplayLabel="+String.valueOf(event.getDisplayLabel())+",getAction="+event.getAction();////////////////////////////////
	//		myLog("onKeyDown","[wKit]"+dbMsg);
//		dbMsg="ppBtnID="+sharedPref.getBoolean("prefKouseiD_PadUMU", false);///////////////////////////////////////////////////////////////////
//			myLog("onKeyDown","[wKit]"+dbMsg);
		    dbMsg="サイドボリュームとディスプレイ下のキー；canGoBack="+webView.canGoBack();///////////////////////////////////////////////////////////////////
			switch (keyCode) {	//キーにデフォルト以外の動作を与えるもののみを記述★KEYCODE_MENUをここに書くとメニュー表示されない
			case KeyEvent.KEYCODE_DPAD_UP:		//マルチガイド上；19
			//	wZoomUp();						//ズームアップして上限に達すればfalse
			    if(! sharedPref.getBoolean("prefKouseiD_PadUMU", false)){		//キーの利用が無効になっていたら
					myEditor.putBoolean("prefKouseiD_PadUMU", true);			//キーの利用を有効にして
			    }
				return true;
			case KeyEvent.KEYCODE_DPAD_DOWN:	//マルチガイド下；20
			//	wZoomDown();					//ズームダウンして下限に達すればfalse
			    if(! sharedPref.getBoolean("prefKouseiD_PadUMU", false)){		//キーの利用が無効になっていたら
					myEditor.putBoolean("prefKouseiD_PadUMU", true);			//キーの利用を有効にして
			    }
				return true;
			case KeyEvent.KEYCODE_DPAD_LEFT:	//マルチガイド左；21
				wForward();						//ページ履歴で1つ後のページに移動する					return true;
			    if(! sharedPref.getBoolean("prefKouseiD_PadUMU", false)){		//キーの利用が無効になっていたら
					myEditor.putBoolean("prefKouseiD_PadUMU", true);			//キーの利用を有効にして
			    }
				return true;
			case KeyEvent.KEYCODE_DPAD_RIGHT:	//マルチガイド右；22
				wGoBack();					//ページ履歴で1つ前のページに移動する
			    if(! sharedPref.getBoolean("prefKouseiD_PadUMU", false)){		//キーの利用が無効になっていたら
					myEditor.putBoolean("prefKouseiD_PadUMU", true);			//キーの利用を有効にして
			    }
			    return true;
			case KeyEvent.KEYCODE_VOLUME_UP:	//24
				wZoomUp();						//ズームアップして上限に達すればfalse
				return true;
			case KeyEvent.KEYCODE_VOLUME_DOWN:	//25
				wZoomDown();					//ズームダウンして下限に達すればfalse
				return true;
			case KeyEvent.KEYCODE_BACK:			//4KEYCODE_BACK :keyCode；09SH: keyCode；4,event=KeyEvent{action=0 code=4 repeat=0 meta=0 scancode=158 mFlags=72}
				wGoBack();					//ページ履歴で1つ前のページに移動する;
				return true;
			default:
				return false;
			}
		} catch (Exception e) {
			myErrorLog("onKeyDown",dbMsg+"；"+e + "");
			return false;
		}
	}

	//メニューボタンで表示するメニュー///////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCreateOptionsMenu(Menu wkMenu) {
		//	myLog("onCreateOptionsMenu","NakedFileVeiwActivity;mlMenu="+wkMenu);
	//		makeOptionsMenu(wkMenu);	//ボタンで表示するメニューの内容の実記述
	//	return super.onCreateOptionsMenu(wkMenu);
		getMenuInflater().inflate(R.menu.wk_menu , wkMenu);		//メニューリソースの使用
		return super.onCreateOptionsMenu(wkMenu);

		}

	public boolean makeOptionsMenu(Menu wkMenu) {	//ボタンで表示するメニューの内容
		dbMsg ="MenuItem"+wkMenu + "";////////////////////////////////////////////////////////////////////////////
//		myLog("makeOptionsMenu",dbMsg);
//			wkMenu.add(0, MENU_kore, 0, "これ");	//メニューそのもので起動するパターン
//		SubMenu koreMenu = wkMenu.addSubMenu(getResources().getText(R.string.common_sousa));							//	<string name="">操作</string>
//		koreMenu.add(MENU_WQKIT, MENU_WQKIT_ZUP, 0, getResources().getText(R.string.wk_menu_zu));				//ズームアップ";
//		koreMenu.add(MENU_WQKIT, MENU_WQKIT_ZDW, 0, getResources().getText(R.string.wk_menu_zd));				//ズームダウン";
//		koreMenu.add(MENU_WQKIT, MENU_WQKIT_FOR, 0,  getResources().getText(R.string.wk_menu_for));				//1ページ進む";
//		koreMenu.add(MENU_WQKIT, MENU_WQKIT_BAC, 0, getResources().getText(R.string.wk_menu_bck));				//1ページ戻る";
//		koreMenu.add(MENU_WQKIT, MENU_WQKIT_END, 0, getResources().getText(R.string.wk_menu_end));		// = "終了";
//
		return true;
	//	return super.onCreateOptionsMenu(wkMenu);			//102SHでメニューが消えなかった
	}
//
	    @Override
	public boolean onPrepareOptionsMenu(Menu wkMenu) {			//表示直前に行う非表示や非選択設定
			dbMsg ="MenuItem"+wkMenu + ""+",進み"+webView.canGoForward()+",戻り"+webView.canGoBack();////////////////////////////////////////////////////////////////////////////
			myLog("onPrepareOptionsMenu",dbMsg);
			if(webView.canGoForward()){		//戻るページがあれば
				En_FOR=true;				//1ページ進むを表示
			}else{
				En_FOR=false;
			}
			if(webView.canGoBack()){		//戻るページがあれば
				En_BAC=true;				//1ページ戻るを表示
			}else{
				En_BAC=false;
			}
			wkMenu.findItem(R.id.wk_menu_zu).setEnabled(En_ZUP);			//ズームアップ";
			wkMenu.findItem(R.id.wk_menu_zu).setVisible(En_ZUP);
			wkMenu.findItem(R.id.wk_menu_zd).setEnabled(En_ZDW);			//ズームダウン";
			wkMenu.findItem(R.id.wk_menu_zd).setVisible(En_ZDW);
			wkMenu.findItem(R.id.wk_menu_for).setEnabled(En_FOR);			//1ページ進む"
			wkMenu.findItem(R.id.wk_menu_for).setVisible(En_FOR);
			wkMenu.findItem(R.id.wk_menu_bck).setEnabled(En_FOR);			//1ページ戻る
			wkMenu.findItem(R.id.wk_menu_bck).setVisible(En_FOR);

//			wkMenu.findItem(MENU_WQKIT_ZUP).setEnabled(En_ZUP);		//ズームアップ";
//			wkMenu.findItem(MENU_WQKIT_ZDW).setEnabled(En_ZDW);		//ズームダウン";
//			wkMenu.findItem(MENU_WQKIT_FOR).setEnabled(En_FOR);		//1ページ進む";
//			wkMenu.findItem(MENU_WQKIT_BAC).setEnabled(En_FOR);		//1ページ戻る";
	    	return true;
	    	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final String TAG = "onOptionsItemSelected[wKit]";
		String dbMsg ="";
		try{
				dbMsg ="MenuItem"+item.getItemId()+"を操作";////////////////////////////////////////////////////////////////////////////
				myLog(TAG,dbMsg);
				switch (item.getItemId()) {
				case R.id.wk_menu_zu:
		//		case MENU_WQKIT_ZUP:						//ズームアップ";
					wZoomUp();			//ズームアップして上限に達すればfalse
					return true;
				case R.id.wk_menu_zd:
		//		case MENU_WQKIT_ZDW:				//ズームダウン";
					wZoomDown();					//ズームダウンして下限に達すればfalse
					return true;
				case R.id.wk_menu_for:
		//		case MENU_WQKIT_FOR:				//1ページ進む";
					wForward();						//ページ履歴で1つ後のページに移動する
					return true;
				case R.id.wk_menu_bck:
		//		case MENU_WQKIT_BAC:				//1ページ戻る";
					wGoBack();						//ページ履歴で1つ前のページに移動する
					return true;

				case R.id.wk_menu_end:
		//		case MENU_WQKIT_END:						//終了";
					quitMe();			//このActivtyの終了
					return true;
				}
			return false;
			} catch (Exception e) {
				myErrorLog("onOptionsItemSelected","エラー発生；"+e);
				return false;
			}
		}

	@Override
	public void onOptionsMenuClosed(Menu wkMenu) {
	//		myLog("onOptionsMenuClosed","NakedFileVeiwActivity;mlMenu="+wkMenu);
		}

	@Override
	  public void onPause() {
//		mAdView.pause();
	    super.onPause();
	  }

	  @Override
	  public void onResume() {
	    super.onResume();
//	    mAdView.resume();
	  }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try{
			dbMsg ="onDestroy発生";//////////////拡張子=.m4a,ファイルタイプ=audio/*,フルパス=/mnt/sdcard/Music/AC DC/Blow Up Your Video/03 Meanstreak.m4a
//			  mAdView.destroy();
			quitMe();				//このActivtyの終了
			setVisible(false);		//エラー対策		wKit has leaked window android.widget.ZoomButtonsController$Container{444ff948 V.E..... ......I. 0,0-1080,146} that was originally added here
		}catch (Exception e) {
			myErrorLog("onDestroy","[wKit]"+"で"+e + "");
		}
	}

	public  String songLyric;					//この曲jの歌詞データ
	public  String lyricEncord;				//歌詞の再エンコード
	public  String lylicHTM = null;				//html変換した歌詞のフルパス名
	public boolean lyricAri = false;			//歌詞を取得できた

	/**
	 * 歌詞の読出し
	 * */
	public String b_filePath = null;					//読み込み済みのファイル
	private void readLyric( String filepath ) {					//歌詞の読出し
		//http://www.nilab.info/z3/20120806_02.html
		final String TAG = "readLyric[wKit]";
		String dbMsg= "開始";/////////////////////////////////////
		try{
			if(filepath != null){
				if(! filepath.equals(b_filePath)){
					songLyric = getResources().getString(R.string.yomikomi_hunou);		//e="">この曲はタグ情報を読み込めませんでした。</string>
					lyricAri = false;			//歌詞を取得できた
					dbMsg= "filepath=" + filepath;
					Intent intentTB = new Intent(wKit.this,TagBrows.class);
					intentTB.putExtra("reqCode",TagBrows.read_USLT);								// 歌詞読み込み
					intentTB.putExtra("filePath",filepath);
					lyricAri = false;			//歌詞を取得できた
					startActivityForResult(intentTB , MaraSonActivity.LyricCheck );								//歌詞の有無確認
				}
				b_filePath = filepath;
			}
			songLyric = songLyric + "\n\n" + filepath;
			myLog(TAG,dbMsg);
		//	lyric2webSouce( songLyric );
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e + "");
		}
	}

	/**startActivityForResult で起動させたアクティビティがfinish() により破棄されたときにコールされる
	 * @ requestCode : startActivityForResult の第二引数で指定した値が渡される
	 * @ resultCode : 起動先のActivity.setResult の第一引数が渡される
	 * @ Intent data : 起動先Activityから送られてくる Intent		.NullPointerException
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		final String TAG = "onActivityResult[wKit]";
		String dbMsg="開始";
		try{
			Bundle bundle = null ;
			dbMsg = "intent="+intent;////////////////////////////////////////////////////////////////////////////
			if(intent != null){
				dbMsg += ",resultCode="+resultCode;////////////////////////////////////////////////////////////////////////////
				dbMsg += ",requestCode="+requestCode;////////////////////////////////////////////////////////////////////////////
				boolean retBool ;
				String retString = null;
				bundle = intent.getExtras();
				if(resultCode == RESULT_OK){			//-1
					switch(requestCode) {
					case MaraSonActivity.LyricCheck:							//歌詞の有無確認
					case MaraSonActivity.LyricEnc:							//歌詞の再エンコード
						String wrStr =bundle.getString("songLyric");
						if( wrStr != null){
							songLyric = wrStr;
						}
						lyricAri = bundle.getBoolean("lyricAri");			//歌詞を取得できた
						 wrStr =bundle.getString("lyricEncord");
						if( wrStr != null){
							lyricEncord = wrStr;						//歌詞の再エンコード
						}
						wrStr =bundle.getString("lylicHTM");			//html変換した歌詞のフルパス名
						if( wrStr != null){
							lylicHTM = wrStr;
						}
						webView.reload();
						break;
//					default:
//						break;
					}
				}
			}
			myLog(TAG,dbMsg);
		}catch (Exception e) {
			myErrorLog(TAG,dbMsg + "で"+e + "");
		}
	}		//http://fernweh.jp/b/startactivityforresult/


	public MusicReceiver mReceiver;
	public Handler mHandler;
	public IntentFilter mFilter;
	public boolean IsSeisei;			//生成中
	public boolean IsPlaying;			//再生中か
	public String b_dataFN = null;
	public long bModified = 0;

	public class MusicReceiver extends BroadcastReceiver{
		//	BroadcastReceiver mReceiver = new BroadcastReceiver() {	//②ⅰpublic BroadcastReceiver		>>
			@Override
			public void onReceive(final Context context, final Intent intent) {
				mHandler.post(new Runnable() {
					public void run() {
						final String TAG = "MusicReceiver[wKit]";
						String dbMsg="起動";
						try{
//							dbMsg= "context=" + context;
//							if( context == null ){
//								myLog(TAG, dbMsg);
//							}
//							dbMsg= "intent=" + intent;
//							if( intent == null ){
//								myLog(TAG, dbMsg);
//							}
//							String state = intent.getStringExtra("state");
//							dbMsg +=",state=" + state;
							IsSeisei = intent.getBooleanExtra("IsSeisei", false);			//生成中
							dbMsg= dbMsg  + ",生成中= "+ IsSeisei;/////////////////////////////////////
							IsPlaying = intent.getBooleanExtra("IsPlaying", false);			//再生中か
							dbMsg= dbMsg  + ",再生中= "+ IsPlaying;/////////////////////////////////////
							if( IsPlaying ){						//&& ppPBT.getContentDescription().equals(getResources().getText(R.string.pause))
								wk_pp_bt.setImageResource(R.drawable.pouse40);
								wk_pp_bt.setContentDescription(getResources().getText(R.string.play));			//pause
							} else {
								wk_pp_bt.setImageResource(R.drawable.play40);
								wk_pp_bt.setContentDescription(getResources().getText(R.string.pause));			//play
							}
							String dataFN =intent.getStringExtra("data");		//			intent.putExtra("data", dataFN);
							if(dataFN == null ) {
								dataFN = "";
							}
							if(b_dataFN == null){
								b_dataFN = dataFN;
							}
					//		dbMsg +=dataFN + "に変更";
							if(! dataFN.equals(b_dataFN)){			// || nowList_id != b_List_id
								dbMsg= dbMsg +"、前の曲；"+ b_dataFN + " を　"+ dataFN +"に更新";
								readLyric( dataFN );					//歌詞の読出し
								String[] tStrs = dataFN.split(File.separator);
								wKit.this.toolbar.setTitle(tStrs[tStrs.length-1]);

//								dbMsg= dbMsg +"、motoFName；"+ motoFName;
//								File dFile = new File(motoFName);
//								dbMsg +=",exists=" + dFile.exists() ;
//								long nModified = dFile.lastModified();
//								if( bModified == 0 ){
//									bModified = nModified;
//								}
//								dbMsg= dbMsg  +"(" + bModified +")>>" + nModified ;
//								if(bModified < nModified){
//									webView.reload();
//									bModified = nModified;
									b_dataFN = dataFN;
//								}
								myLog(TAG, dbMsg);
							}					//if(! dataFN.equals(b_dataFN) ){
						} catch (Exception e) {
							myErrorLog(TAG,dbMsg+"で"+e);
						}
					}
				});
			}
		};			//MusicPlayerReceiver
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
