package com.example.netcomic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText eUser,ePass,eRePass;
    private Button btnRegister, btnReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth= FirebaseAuth.getInstance();

        eUser= findViewById(R.id.user);
        ePass= findViewById(R.id.pass);
        eRePass= findViewById(R.id.repass);
        btnRegister= findViewById(R.id.btn_register);
        btnReturn= findViewById(R.id.btn_return);
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
        String email, pass, repass;
        email= eUser.getText().toString();
        pass= ePass.getText().toString();
        repass= eRePass.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Email không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pass)){
            Toast.makeText(this, "Password không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(repass)){
            Toast.makeText(this, "Re_password không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!repass.equals(pass)){
            Toast.makeText(this, "Mật khẩu không khớp."+repass+"  "+pass, Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "Thanh Cong", Toast.LENGTH_SHORT).show();
                    Intent i =new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(i);
                }else{
                    Toast.makeText(RegisterActivity.this, "That Bai", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}