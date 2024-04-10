package com.example.netcomic;
import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.netcomic.models.User;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView iconLogOut,imgProfile;
    private Button btnProfile;
    private TextView profileName,profileEmail;
    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_setting, container, false);
        profileEmail= view.findViewById(R.id.profile_email);
        profileName= view.findViewById(R.id.profile_name);
        imgProfile= view.findViewById(R.id.profile_img);

//        User dataManager = User.getInstance();
//        String userName = dataManager.getUserName();
//        String userEmail = dataManager.getUserEmail();
//        String profileImageUri = dataManager.getProfileImageUri();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "");
        String userEmail = sharedPreferences.getString("userEmail", "");
        String profileImageUri = sharedPreferences.getString("profileImageUri", "");
        profileName.setText(userName);
        profileEmail.setText(userEmail);

        if (!TextUtils.isEmpty(profileImageUri)) {
            Picasso.get().load(profileImageUri).into(imgProfile);
        }

        iconLogOut = view.findViewById(R.id.logout_icon);
        iconLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLogoutConfirmationDialog();
            }
        });
        btnProfile= view.findViewById(R.id.profile_info_btn);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getActivity(),ProfileActivity.class);
                startActivity(i);

            }
        });

        return view;

    }
    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Xác nhận đăng xuất");
        builder.setMessage("Bạn có chắc chắn muốn đăng xuất?");
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Nếu người dùng đồng ý, thực hiện đăng xuất
                showlogout();
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Nếu người dùng hủy, không thực hiện gì cả
                dialog.dismiss(); // Dismiss dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showlogout() {
        Intent i= new Intent(getActivity(),LogOutActivity.class);
        startActivity(i);
    }

}