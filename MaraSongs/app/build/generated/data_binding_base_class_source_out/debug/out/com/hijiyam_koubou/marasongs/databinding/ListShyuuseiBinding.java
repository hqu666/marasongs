// Generated by view binder compiler. Do not edit!
package com.hijiyam_koubou.marasongs.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.hijiyam_koubou.marasongs.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ListShyuuseiBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final CheckBox leCb;

  @NonNull
  public final TextView leMsgTv;

  @NonNull
  public final EditText leSakiEt;

  private ListShyuuseiBinding(@NonNull LinearLayout rootView, @NonNull CheckBox leCb,
      @NonNull TextView leMsgTv, @NonNull EditText leSakiEt) {
    this.rootView = rootView;
    this.leCb = leCb;
    this.leMsgTv = leMsgTv;
    this.leSakiEt = leSakiEt;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ListShyuuseiBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ListShyuuseiBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.list_shyuusei, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ListShyuuseiBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.le_cb;
      CheckBox leCb = ViewBindings.findChildViewById(rootView, id);
      if (leCb == null) {
        break missingId;
      }

      id = R.id.le_msg_tv;
      TextView leMsgTv = ViewBindings.findChildViewById(rootView, id);
      if (leMsgTv == null) {
        break missingId;
      }

      id = R.id.le_saki_et;
      EditText leSakiEt = ViewBindings.findChildViewById(rootView, id);
      if (leSakiEt == null) {
        break missingId;
      }

      return new ListShyuuseiBinding((LinearLayout) rootView, leCb, leMsgTv, leSakiEt);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
