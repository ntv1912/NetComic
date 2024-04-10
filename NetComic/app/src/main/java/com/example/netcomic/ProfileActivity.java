package com.example.netcomic;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.netcomic.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private TextView  pName, pEmail, pChange, pRemove,pTitle;
    private ImageView pImg;
    GoogleSignInClient mGoogleSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        pImg = findViewById(R.id.profile_img);
        pName = findViewById(R.id.profile_name);
        pEmail = findViewById(R.id.profile_email);
        pChange = findViewById(R.id.change_pass);
        pRemove = findViewById(R.id.remove_profile);
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        pTitle = findViewById(R.id.profile_title);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Uri photoUrl = currentUser.getPhotoUrl();
            if (photoUrl != null) {
                Picasso.get().load(photoUrl.toString()).into(pImg);
            }
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            // Kiểm tra nếu tên người dùng không rỗng thì hiển thị tên, ngược lại hiển thị một thông báo
            if (TextUtils.isEmpty(name)) {
                pName.setText("Bạn chưa thiết lập tên tài khoản");
            } else {
                pName.setText("Tài khoản: "+name);
            }

            pEmail.setText("Email: " + email);
//            User dataManager = User.getInstance();
//            dataManager.setUserName(name);
//            dataManager.setUserEmail(email);
//            dataManager.setProfileImageUri((photoUrl != null) ? photoUrl.toString() : "");

            SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userName", name);
            editor.putString("userEmail", email);
            editor.putString("profileImageUri", (photoUrl != null) ? photoUrl.toString() : "");
            editor.apply();
        }

        pTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        pImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAvatar();
            }
        });

        pName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy tên người dùng hiện tại
                String currentName = pName.getText().toString();

                // Kiểm tra xem currentName có chứa tiền tố "Tài khoản: " hay không
                if (currentName.startsWith("Tài khoản: ")) {
                    // Nếu có, lấy phần tên sau tiền tố "Tài khoản: "
                    String existingName = currentName.substring("Tài khoản: ".length());
                    showNameDialog(existingName);
                } else {
                    // Nếu không, sử dụng toàn bộ currentName làm giá trị mặc định
                    showNameDialog(currentName);
                }
            }
        });

        pChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý sự kiện khi người dùng nhấn vào nút thay đổi mật khẩu
                String currentPass = "";
                showPassDialog(currentPass);
            }
        });

        pEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý sự kiện khi người dùng nhấn vào email
            }
        });
        pRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị một AlertDialog xác nhận việc xóa tài khoản
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Xác nhận xóa tài khoản");
                builder.setMessage("Bạn có chắc chắn muốn xóa tài khoản của mình?");

                // Thiết lập nút "Xác nhận" trong AlertDialog
                builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Xóa tài khoản người dùng hiện tại
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            // Xóa tài khoản người dùng từ Firebase Authentication
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Đăng xuất người dùng từ Firebase Authentication
                                        FirebaseAuth.getInstance().signOut();
                                        GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                .requestIdToken(getString(R.string.default_web_client_id))
                                                .requestEmail()
                                                .build();
                                        mGoogleSignIn = GoogleSignIn.getClient(ProfileActivity.this,gso);
                                        mGoogleSignIn.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> googleTask) {
                                                if (googleTask.isSuccessful()) {
                                                    // Xóa tài khoản thành công, chuyển đến màn hình đăng nhập
                                                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                                                    finish();
                                                    Toast.makeText(ProfileActivity.this, "Tài khoản đã được xóa thành công.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Xảy ra lỗi khi đăng xuất khỏi Google Sign-In
                                                    Toast.makeText(ProfileActivity.this, "Có lỗi xảy ra khi đăng xuất khỏi Google Sign-In.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        // Xảy ra lỗi khi xóa tài khoản
                                        Toast.makeText(ProfileActivity.this, "Có lỗi xảy ra khi xóa tài khoản.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });


                // Thiết lập nút "Hủy" trong AlertDialog
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Đóng dialog nếu người dùng bấm nút "Hủy"
                        dialog.cancel();
                    }
                });

                // Hiển thị AlertDialog
                builder.show();
            }
        });
    }
    // Phương thức hiển thị dialog cho người dùng nhập tên mới
    private void showNameDialog(String defaultValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Nhập tên mới");

        // Thiết lập trường nhập dữ liệu trong AlertDialog
        final EditText input = new EditText(ProfileActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(defaultValue);
        builder.setView(input);

        // Thiết lập nút "OK" trong AlertDialog
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Lấy tên mới từ trường nhập dữ liệu
                String newName = input.getText().toString();

                // Kiểm tra xem tên mới có rỗng không
                if (!TextUtils.isEmpty(newName)) {
                    // Thực hiện cập nhật tên trên Firebase
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(newName)
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Cập nhật tên thành công
                                            Toast.makeText(ProfileActivity.this, "Tên người dùng đã được cập nhật.", Toast.LENGTH_SHORT).show();
                                            // Cập nhật lại TextView hiển thị tên người dùng
                                            pName.setText("Tài khoản: " + newName);
                                        } else {
                                            // Xảy ra lỗi khi cập nhật tên
                                            Toast.makeText(ProfileActivity.this, "Có lỗi xảy ra khi cập nhật tên người dùng.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                } else {
                    // Hiển thị thông báo nếu tên mới rỗng
                    Toast.makeText(ProfileActivity.this, "Tên không được để trống.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Thiết lập nút "Hủy" trong AlertDialog
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Đóng dialog nếu người dùng bấm nút "Hủy"
                dialog.cancel();
            }
        });

        // Hiển thị AlertDialog
        builder.show();
    }


    public void changeAvatar() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            pImg.setImageURI(selectedImageUri); // Hiển thị ảnh được chọn trong ImageView

            // Upload ảnh lên Firebase Storage
            uploadImageToFirebase(selectedImageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            StorageReference fileRef = mStorageRef.child("avatars/" + user.getUid() + ".jpg");

            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Lấy URL của ảnh từ Firebase Storage
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Cập nhật URL của ảnh đại diện vào Firebase Authentication
                            user.updateProfile(new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(uri)
                                            .build())
                                    .addOnSuccessListener(aVoid -> {
                                        // Thông báo cập nhật ảnh đại diện thành công
                                        Toast.makeText(ProfileActivity.this, "Ảnh đại diện đã được cập nhật", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Xảy ra lỗi khi cập nhật ảnh đại diện
                                        Toast.makeText(ProfileActivity.this, "Có lỗi xảy ra khi cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Error updating profile picture", e);
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Xảy ra lỗi khi tải ảnh lên Firebase Storage
                        Toast.makeText(ProfileActivity.this, "Có lỗi xảy ra khi tải ảnh lên", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error uploading image to Firebase Storage", e);
                    });
        }
    }

    private void showPassDialog(String defaultValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Nhập mật khẩu mới");

        final EditText input = new EditText(ProfileActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(defaultValue);
        builder.setView(input);

        // Thiết lập nút "OK" trong AlertDialog
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Lấy mật khẩu mới và mật khẩu xác nhận từ trường nhập dữ liệu
                String newPass = input.getText().toString();

                // Kiểm tra xem mật khẩu mới và mật khẩu xác nhận có trùng khớp không
                if(!TextUtils.isEmpty(newPass)){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        user.updatePassword(newPass)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Cập nhật mật khẩu thành công
                                            Toast.makeText(ProfileActivity.this, "Mật khẩu đã được thay đổi.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Xảy ra lỗi khi cập nhật mật khẩu
                                            Toast.makeText(ProfileActivity.this, "Có lỗi xảy ra khi thay đổi mật khẩu.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }else {
                    // Hiển thị thông báo nếu tên mới rỗng
                    Toast.makeText(ProfileActivity.this, "Mật khẩu không được để trống.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Thiết lập nút "Hủy" trong AlertDialog
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Đóng dialog nếu người dùng bấm nút "Hủy"
                dialog.cancel();
            }
        });

        // Hiển thị AlertDialog
        builder.show();
    }
}