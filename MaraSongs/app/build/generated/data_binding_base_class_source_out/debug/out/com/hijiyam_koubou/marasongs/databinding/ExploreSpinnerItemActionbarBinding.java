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

public final class ExploreSpinnerItemActionbarBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final TextView text1;

  private ExploreSpinnerItemActionbarBinding(@NonNull LinearLayout rootView,
      @NonNull TextView text1) {
    this.rootView = rootView;
    this.text1 = text1;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ExploreSpinnerItemActionbarBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ExploreSpinnerItemActionbarBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.explore_spinner_item_actionbar, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ExploreSpinnerItemActionbarBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = android.R.id.text1;
      TextView text1 = ViewBindings.findChildViewById(rootView, id);
      if (text1 == null) {
        break missingId;
      }

      return new ExploreSpinnerItemActionbarBinding((LinearLayout) rootView, text1);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}