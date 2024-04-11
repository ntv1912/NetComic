package com.example.netcomic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.netcomic.broadcast.InternetReceiver;
import com.example.netcomic.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private InternetReceiver internet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home)
                    replaceFragment(new HomeFragment());
                if (item.getItemId() == R.id.setting)
                    replaceFragment(new SettingFragment());
                if (item.getItemId() == R.id.search)
                    replaceFragment(new SearchFragment());
                if (item.getItemId() == R.id.book)
                    replaceFragment(new BookStoreFragment());
                return true;
            }
        });
        internet= new InternetReceiver();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }
//    private long backPressedTime;
//    @Override
//    public void onBackPressed() {
//        if (isHomeFragmentVisible()) {
//            if (backPressedTime + 2000 > System.currentTimeMillis()) {
//                super.onBackPressed();
//                finishAffinity();
//                return;
//            } else {
//                Toast.makeText(this, "Press back again to exit the application", Toast.LENGTH_SHORT).show();
//            }
//            backPressedTime = System.currentTimeMillis();
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//    private boolean isHomeFragmentVisible() {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_layout);
//        return currentFragment instanceof HomeFragment;
//    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(internet, intentFilter);


    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(internet);
    }
}