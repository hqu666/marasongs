package com.hijiyam_koubou.marasongs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Implementation of App Widget functionality.DialogFragment
 */
public class DialogProgress extends Activity {
    View dialogView;
    public LinearLayout progress_dlog_ll;
    public ProgressBar progress_pb;
    public TextView progress_message_tv ;
    public TextView progress_titol_tv ;

    public Context gContext;
    public String pdTitol;
    public String pdMessage;
    public int pdMaxVal;
    public int pdCoundtVal=0;					//ProgressDialog表示値

    @Override
    public void onCreate(Bundle savedInstanceState) {                                    //①起動
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        pdTitol=extras.getString("pdTitol");
        pdMessage=extras.getString("pdMessage");
        pdMaxVal=extras.getInt("pdMaxVal");
        setContentView(R.layout.dialog_progress);				//			setContentView(R.layout.main);
        Window gWindow = this.getWindow();
        gWindow.setTitle(pdTitol);
        progress_dlog_ll = findViewById(R.id.progress_dlog_ll);
        progress_pb = findViewById(R.id.progress);
        progress_titol_tv = findViewById(R.id.progress_titol);
        progress_message_tv = findViewById(R.id.progress_message);
 //       progress_titol_tv.setText(pdTitol);
        progress_message_tv.setText(pdMessage);
        progress_pb.setMax(pdMaxVal);
        pdCoundtVal=0;

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MuList.EXTRA_MESSAGE);
//        pdCoundtVal = Integer.valueOf(message);
//        progress_pb.setProgress(pdCoundtVal);

    }

    public void setMax(int maxInt) {
        progress_pb.setMax(maxInt);
    }

    public int getMax() {
        return progress_pb.getMax();
    }

    public void setProgVal(int progInt) {
        progress_pb.setProgress(progInt);
        if(progress_pb.getMax()<=progress_pb.getProgress()){
            DialogProgress.this.finish();
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
        DialogProgress.this.finish();
    }

}