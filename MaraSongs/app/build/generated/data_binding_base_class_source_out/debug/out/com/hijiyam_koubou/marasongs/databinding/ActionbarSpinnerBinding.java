// Generated by view binder compiler. Do not edit!
package com.hijiyam_koubou.marasongs.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.hijiyam_koubou.marasongs.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActionbarSpinnerBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Spinner actionbarSpinner;

  private ActionbarSpinnerBinding(@NonNull LinearLayout rootView,
      @NonNull Spinner actionbarSpinner) {
    this.rootView = rootView;
    this.actionbarSpinner = actionbarSpinner;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActionbarSpinnerBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActionbarSpinnerBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.actionbar_spinner, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActionbarSpinnerBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.actionbar_spinner;
      Spinner actionbarSpinner = ViewBindings.findChildViewById(rootView, id);
      if (actionbarSpinner == null) {
        break missingId;
      }

      return new ActionbarSpinnerBinding((LinearLayout) rootView, actionbarSpinner);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
