// Generated by view binder compiler. Do not edit!
package com.hijiyam_koubou.marasongs.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.hijiyam_koubou.marasongs.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class MuListBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final RemoteControllBinding listPlayer;

  @NonNull
  public final Toolbar listToolBar;

  @NonNull
  public final LinearLayout playerRootLl;

  @NonNull
  public final ListView pllist;

  private MuListBinding(@NonNull LinearLayout rootView, @NonNull RemoteControllBinding listPlayer,
      @NonNull Toolbar listToolBar, @NonNull LinearLayout playerRootLl, @NonNull ListView pllist) {
    this.rootView = rootView;
    this.listPlayer = listPlayer;
    this.listToolBar = listToolBar;
    this.playerRootLl = playerRootLl;
    this.pllist = pllist;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static MuListBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static MuListBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
      boolean attachToParent) {
    View root = inflater.inflate(R.layout.mu_list, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static MuListBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.list_player;
      View listPlayer = ViewBindings.findChildViewById(rootView, id);
      if (listPlayer == null) {
        break missingId;
      }
      RemoteControllBinding binding_listPlayer = RemoteControllBinding.bind(listPlayer);

      id = R.id.list_tool_bar;
      Toolbar listToolBar = ViewBindings.findChildViewById(rootView, id);
      if (listToolBar == null) {
        break missingId;
      }

      LinearLayout playerRootLl = (LinearLayout) rootView;

      id = R.id.pllist;
      ListView pllist = ViewBindings.findChildViewById(rootView, id);
      if (pllist == null) {
        break missingId;
      }

      return new MuListBinding((LinearLayout) rootView, binding_listPlayer, listToolBar,
          playerRootLl, pllist);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
