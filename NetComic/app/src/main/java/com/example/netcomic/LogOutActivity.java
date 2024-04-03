package com.example.netcomic;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogOutActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignIn;
    Button btnLogOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignIn = GoogleSignIn.getClient(this,gso);
        setContentView(R.layout.activity_log_out);
        btnLogOut= findViewById(R.id.btn_logout);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoogleSignIn != null) {
                    mGoogleSignIn.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Đăng xuất thành công từ GoogleSignInClient
                                Toast.makeText(LogOutActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
//                                Log.d(TAG, "Đăng xuất thành công");

                                // Đăng xuất từ Firebase Authentication
                                FirebaseAuth.getInstance().signOut();

                                Intent intent = new Intent(LogOutActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Xử lý khi đăng xuất từ GoogleSignInClient không thành công
                                Toast.makeText(LogOutActivity.this, "Đăng xuất thất bại", Toast.LENGTH_SHORT).show();
//                                Log.w(TAG, "Đăng xuất thất bại", task.getException());

                                // Chuyển về MainActivity
                                Intent intent = new Intent(LogOutActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                } else {
                    // Nếu mGoogleSignIn là null, không có gì để đăng xuất
                    Toast.makeText(LogOutActivity.this, "Không thể đăng xuất", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}