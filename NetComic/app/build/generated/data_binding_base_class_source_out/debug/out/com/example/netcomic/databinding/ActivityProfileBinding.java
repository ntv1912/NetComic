// Generated by view binder compiler. Do not edit!
package com.example.netcomic.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.netcomic.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityProfileBinding implements ViewBinding {
  @NonNull
  private final LinearLayoutCompat rootView;

  @NonNull
  public final TextView changePass;

  @NonNull
  public final TextView profileEmail;

  @NonNull
  public final ImageView profileImg;

  @NonNull
  public final TextView profileName;

  @NonNull
  public final TextView profileTxt;

  @NonNull
  public final TextView removeProfile;

  private ActivityProfileBinding(@NonNull LinearLayoutCompat rootView, @NonNull TextView changePass,
      @NonNull TextView profileEmail, @NonNull ImageView profileImg, @NonNull TextView profileName,
      @NonNull TextView profileTxt, @NonNull TextView removeProfile) {
    this.rootView = rootView;
    this.changePass = changePass;
    this.profileEmail = profileEmail;
    this.profileImg = profileImg;
    this.profileName = profileName;
    this.profileTxt = profileTxt;
    this.removeProfile = removeProfile;
  }

  @Override
  @NonNull
  public LinearLayoutCompat getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityProfileBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityProfileBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_profile, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityProfileBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.change_pass;
      TextView changePass = ViewBindings.findChildViewById(rootView, id);
      if (changePass == null) {
        break missingId;
      }

      id = R.id.profile_email;
      TextView profileEmail = ViewBindings.findChildViewById(rootView, id);
      if (profileEmail == null) {
        break missingId;
      }

      id = R.id.profile_img;
      ImageView profileImg = ViewBindings.findChildViewById(rootView, id);
      if (profileImg == null) {
        break missingId;
      }

      id = R.id.profile_name;
      TextView profileName = ViewBindings.findChildViewById(rootView, id);
      if (profileName == null) {
        break missingId;
      }

      id = R.id.profile_txt;
      TextView profileTxt = ViewBindings.findChildViewById(rootView, id);
      if (profileTxt == null) {
        break missingId;
      }

      id = R.id.remove_profile;
      TextView removeProfile = ViewBindings.findChildViewById(rootView, id);
      if (removeProfile == null) {
        break missingId;
      }

      return new ActivityProfileBinding((LinearLayoutCompat) rootView, changePass, profileEmail,
          profileImg, profileName, profileTxt, removeProfile);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
