package com.example.netcomic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.netcomic.adapters.ChapterImageAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChapterActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView chapterTitleTextView;
    private RecyclerView recyclerView;
    private ImageButton btnPrevious, btnChapterList, btnNext;
    private LinearLayoutManager layoutManager;
    private ChapterImageAdapter imageAdapter;
    private List<String> chapterImages;

    private boolean isToolbarVisible = true;
    private int lastVisibleItemPosition;
    private String comicId, chapterId, chapterTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        // Ánh xạ các view từ layout
        toolbar = findViewById(R.id.toolbar);
        chapterTitleTextView = findViewById(R.id.chapter_title);
        recyclerView = findViewById(R.id.recycler_view);
        btnPrevious = toolbar.findViewById(R.id.btn_previous);
        btnChapterList = toolbar.findViewById(R.id.btn_chapter_list);
        btnNext = toolbar.findViewById(R.id.btn_next);

        // Thiết lập toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Lấy dữ liệu từ Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            comicId = extras.getString("COMIC_ID");
            chapterId = extras.getString("CHAPTER_ID");
            chapterTitle = extras.getString("CHAPTER_Title");

            // Kiểm tra giá trị chapterId để đảm bảo không null
            if (chapterId != null) {
                // Hiển thị thông tin debug
                Toast.makeText(this, "ComicId: " + comicId + ", ChapterId: " + chapterId, Toast.LENGTH_SHORT).show();

                // Thiết lập tiêu đề chương
                chapterTitleTextView.setText(chapterTitle);

                // Lấy danh sách ảnh của chương từ Firebase Firestore
                loadChapterImagesFromFirestore(comicId, chapterId);
            } else {
                Toast.makeText(this, "ChapterId is null", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Intent extras are null", Toast.LENGTH_SHORT).show();
        }


        // Xử lý sự kiện nhấn nút trở lại trên toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Xử lý sự kiện click các nút chức năng trên toolbar
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý chuyển đến chương trước
                // Ví dụ: Hiển thị chương trước đó
            }
        });

        btnChapterList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý hiển thị danh sách các chương
                // Ví dụ: Mở dialog hoặc fragment để hiển thị danh sách chương
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý chuyển đến chương tiếp theo
                // Ví dụ: Hiển thị chương tiếp theo
            }
        });

        // Thiết lập RecyclerView và Adapter
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        chapterImages = new ArrayList<>();
        imageAdapter = new ChapterImageAdapter(chapterImages);
        recyclerView.setAdapter(imageAdapter);

        // Xử lý sự kiện cuộn để ẩn hoặc hiển thị toolbar
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && isToolbarVisible) {
                    // Cuộn xuống: Ẩn toolbar
                    toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
                    isToolbarVisible = false;
                } else if (dy < 0 && !isToolbarVisible) {
                    // Cuộn lên: Hiển thị toolbar
                    toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
                    isToolbarVisible = true;
                }
            }
        });
    }

    private void loadChapterImagesFromFirestore(String comicId, String chapterId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Truy vấn để lấy tài liệu (document) của chương từ collection "chapters" trong Firestore
        db.collection("comics")
                .document(comicId)
                .collection("chapters")
                .document(chapterId) // Sử dụng chapterId để truy cập vào một chương cụ thể
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Lấy danh sách các URL ảnh từ trường "Images" của tài liệu chương
                        List<String> images = (List<String>) documentSnapshot.get("Images");

                        // Kiểm tra và thêm các URL ảnh vào danh sách chapterImages
                        if (images != null && !images.isEmpty()) {
                            chapterImages.clear(); // Xóa danh sách ảnh hiện có trước khi thêm mới
                            chapterImages.addAll(images);
                            imageAdapter.notifyDataSetChanged(); // Cập nhật Adapter sau khi thêm dữ liệu
                        } else {
                            Toast.makeText(this, "No images found for this chapter", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Chapter not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load chapter images", Toast.LENGTH_SHORT).show();
                });
    }
}
