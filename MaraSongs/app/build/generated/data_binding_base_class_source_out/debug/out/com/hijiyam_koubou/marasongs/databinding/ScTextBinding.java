// Generated by view binder compiler. Do not edit!
package com.hijiyam_koubou.marasongs.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public final class ScTextBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final LinearLayout playerRootLl;

  @NonNull
  public final TextView summary;

  private ScTextBinding(@NonNull LinearLayout rootView, @NonNull LinearLayout playerRootLl,
      @NonNull TextView summary) {
    this.rootView = rootView;
    this.playerRootLl = playerRootLl;
    this.summary = summary;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ScTextBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ScTextBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
      boolean attachToParent) {
    View root = inflater.inflate(R.layout.sc_text, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ScTextBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      LinearLayout playerRootLl = (LinearLayout) rootView;

      id = android.R.id.summary;
      TextView summary = ViewBindings.findChildViewById(rootView, id);
      if (summary == null) {
        break missingId;
      }

      return new ScTextBinding((LinearLayout) rootView, playerRootLl, summary);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}