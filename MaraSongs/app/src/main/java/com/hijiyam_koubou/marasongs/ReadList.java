package com.hijiyam_koubou.marasongs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ReadList extends AlertDialog implements DialogInterface {

    AlertDialog plogDialogView;
    public LinearLayout progress_dlog_ll;
    public ProgressBar progress_pb;
    public TextView progress_message_tv ;
    public TextView progress_titol_tv ;

    public Context gContext;
    public String pdTitol;
    public String pdMessage;
    public int pdMaxVal;
    private int pdCoundtVal=0;					//ProgressDialog表示値

    protected ReadList(Context context,String dTitol ,String dMessage ,int maxVal) {
        super(context);
        final String TAG = "ReadList";
        String dbMsg = "";
        try{
            gContext = context;
            pdTitol = dTitol;
            pdMessage = dMessage;
            pdMaxVal = maxVal;
            dbMsg += ",pdTitol=" + pdTitol+ ",pdMessage=" + pdMessage+ ",pdMaxVal=" + pdMaxVal;
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( gContext );		// アラートダイアログのタイトルを設定します 	getApplicationContext()
            alertDialogBuilder.setTitle(pdTitol);
            progress_message_tv = new TextView(gContext);
            alertDialogBuilder.setView(progress_message_tv);// アラートダイアログのメッセージを設定します
            progress_message_tv.setText(dMessage);
            progress_pb = new ProgressBar(gContext);
            alertDialogBuilder.setView(progress_pb);// アラートダイアログのメッセージを設定します
            progress_pb.setMax(pdMaxVal-1);
            progress_pb.setProgress(0);
//
//            plogDialog = new Builder(this)
//                    .setView(plogDialogView)
//                    .create();
//            plogDialog.show();
            alertDialogBuilder.setCancelable(true);// アラートダイアログのキャンセルが可能かどうかを設定します
            plogDialogView = alertDialogBuilder.create();	// アラートダイアログを表示します
            plogDialogView.setCanceledOnTouchOutside(false);	//背景をタップしてもダイアログを閉じない
            plogDialogView.show();
            myLog(TAG,dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG,dbMsg + "で"+e.toString());
        }
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {                                    //①起動
//        super.onCreate(savedInstanceState);
//        Bundle extras = getIntent().getExtras();
//        pdTitol=extras.getString("pdTitol");
//        pdMessage=extras.getString("pdMessage");
//        pdMaxVal=extras.getInt("pdMaxVal");
//        setContentView(R.layout.dialog_progress);				//			setContentView(R.layout.main);
//        Window gWindow = this.getWindow();
//        gWindow.setTitle(pdTitol);
//        progress_dlog_ll = findViewById(R.id.progress_dlog_ll);
//        progress_pb = findViewById(R.id.progress);
//        progress_titol_tv = findViewById(R.id.progress_titol);
//        progress_message_tv = findViewById(R.id.progress_message);
//        //       progress_titol_tv.setText(pdTitol);
//        progress_message_tv.setText(pdMessage);
//        progress_pb.setMax(pdMaxVal);
//        pdCoundtVal=0;
//
//        // Get the Intent that started this activity and extract the string
//        Intent intent = getIntent();
//        String message = intent.getStringExtra(MuList.EXTRA_MESSAGE);
////        pdCoundtVal = Integer.valueOf(message);
////        progress_pb.setProgress(pdCoundtVal);
//
//    }

    public void setMax(int maxInt) {
        progress_pb.setMax(maxInt);
    }

    public int getMax() {
        return progress_pb.getMax();
    }

    /**プログレスを更新して、maxに達すればダイアログを閉じる*/
    public void setProgVal(int progInt) {
        final String TAG = "setProgVal";
        String dbMsg = "";
        try{
            pdCoundtVal = progInt;
            dbMsg += "pdCoundtVal=" + progress_pb.getProgress();
            progress_pb.setProgress(pdCoundtVal);
            dbMsg += ">>" + progress_pb.getProgress()+ "/" + progress_pb.getProgress();
            if(progress_pb.getMax()<=progress_pb.getProgress()){
                plogDialogView.dismiss();
                dbMsg += ">>dismiss" ;

            }
            myLog(TAG,dbMsg);
        }catch (Exception e) {
            myErrorLog(TAG,dbMsg + "で"+e.toString());
        }
    }

    public int getProgress() {
        return progress_pb.getProgress();
    }

    public void setTitle(String titleStr) {
        progress_titol_tv.setText(titleStr);
    }

    public void setMessage(String msgStr) {
        progress_message_tv.setText(msgStr);
    }

    public void dismiss() {
        plogDialogView.dismiss();
    }

    /////////////////////////////////////////////////////////////////////
    public static void myLog(String TAG , String dbMsg) {
        Util UTIL = new Util();
        Util.myLog(TAG , "[ReadList]"+ dbMsg);
    }

    public static void myErrorLog(String TAG , String dbMsg) {
        Util UTIL = new Util();
        Util.myErrorLog(TAG , dbMsg);
    }

}