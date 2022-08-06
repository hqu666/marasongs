package com.hijiyam_koubou.marasongs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

/**
 * Implementation of App Widget functionality.
 */
public class dialog_progress extends DialogFragment {
    View dialogView;
    public ProgressBar progress_pb;
    public TextView progress_message_tv ;
    public TextView progress_titol_tv ;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_progress, null);
        progress_pb = dialogView.findViewById(R.id.progress);
        progress_titol_tv = dialogView.findViewById(R.id.progress_titol);
        progress_message_tv = dialogView.findViewById(R.id.progress_message);
        builder.setView(inflater.inflate(R.layout.dialog_progress, null));
        return builder.create();
    }

    public void setMax(int maxInt) {
        progress_pb.setMax(maxInt);
    }

    public int getMax() {
       return progress_pb.getMax();
    }

    public void setProgress(int progInt) {
        progress_pb.setProgress(progInt);
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
        this.dismiss();
    }

}