package com.example.netcomic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText eUser, ePass, eRePass;
    private Button btnRegister, btnReturn;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        eUser = findViewById(R.id.user);
        ePass = findViewById(R.id.pass);
        eRePass = findViewById(R.id.repass);
        btnRegister = findViewById(R.id.btn_register);
        btnReturn = findViewById(R.id.btn_return);

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register() {
        String email = eUser.getText().toString().trim();
        String pass = ePass.getText().toString().trim();
        String repass = eRePass.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Password không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(repass)) {
            Toast.makeText(this, "Re_password không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!repass.equals(pass)) {
            Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Tạo người dùng thành công
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Tạo tài khoản người dùng trong Firestore và khởi tạo trường followedComics
                                createUserInFirestore(user.getUid());
                            }
                        } else {
                            // Đăng ký thất bại
                            Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createUserInFirestore(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        Map<String, Object> userData = new HashMap<>();
        userData.put("followedComics", new ArrayList<>()); // Khởi tạo trường followedComics

        userRef.set(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Đã tạo tài khoản người dùng trong Firestore", Toast.LENGTH_SHORT).show();
                            // Tạo tài khoản người dùng thành công trong Firestore
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));

                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Lỗi khi tạo tài khoản người dùng trong Firestore", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
