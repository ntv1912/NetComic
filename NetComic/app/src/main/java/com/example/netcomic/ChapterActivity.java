package com.example.netcomic;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netcomic.R;
import com.example.netcomic.adapters.ChapterImageAdapter;
import com.example.netcomic.models.Chapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChapterActivity extends AppCompatActivity {
    private ConstraintLayout constraintLayout;
    private NestedScrollView nestedScrollView;
    private LinearLayout linearLayout;
    private TextView chapterTitleTextView;
    private RecyclerView recyclerView;
    private ImageButton btnPrevious, btnNext, btnBack,btnList;
    private LinearLayoutManager layoutManager;
    private ChapterImageAdapter imageAdapter;
    private List<String> chapterImages;
    private List<Chapter> sortedChapters;
    private int currentChapterIndex;

    private String comicId, chapterId, chapterTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        // Ánh xạ các view từ layout
        chapterTitleTextView = findViewById(R.id.chapter_title);
        recyclerView = findViewById(R.id.recycler_view);
        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);
        btnBack = findViewById(R.id.back_button);
        btnList= findViewById(R.id.btn_chapter_list);
        linearLayout= findViewById(R.id.linear);
        constraintLayout=findViewById(R.id.constraint);
        nestedScrollView= findViewById(R.id.nested);
        // Lấy dữ liệu từ Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            comicId = extras.getString("COMIC_ID");
            chapterId = extras.getString("CHAPTER_ID");
            chapterTitle = extras.getString("CHAPTER_Title");
            // Đây là ví dụ có thể chuyển sang int
            int chapterNumber = extras.getInt("CHAPTER_Number", 0);

            // Kiểm tra giá trị chapterId để đảm bảo không null
            if (chapterId != null) {
                // Thiết lập tiêu đề chương
                chapterTitleTextView.setText(chapterTitle);

                // Lấy danh sách chương và sắp xếp theo số thứ tự (number) từ Firebase Firestore
                loadAndSortChapters(comicId);
            } else {
                Toast.makeText(this, "ChapterId is null", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Intent extras are null", Toast.LENGTH_SHORT).show();
        }

        // Thiết lập RecyclerView và Adapter
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        chapterImages = new ArrayList<>();
        imageAdapter = new ChapterImageAdapter(chapterImages);
        recyclerView.setAdapter(imageAdapter);

        // Ẩn các nút điều hướng ban đầu
//        linearLayout.setVisibility(View.GONE);
//        btnBack.setVisibility(View.GONE);

        // Bắt sự kiện cuộn trang của NestedScrollView
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY+200) {
                    // Cuộn trang xuống (scroll down), ẩn các nút điều hướng
                    linearLayout.setVisibility(View.GONE);
                    btnBack.setVisibility(View.GONE);
                } else {
                    // Cuộn trang lên (scroll up), hiển thị các nút điều hướng
                    linearLayout.setVisibility(View.VISIBLE);
                    btnBack.setVisibility(View.VISIBLE);
                }
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentChapterIndex > 0) {
                    currentChapterIndex--;
                    Chapter previousChapter = sortedChapters.get(currentChapterIndex);
                    loadChapterImagesFromFirestore(comicId, previousChapter.getId());
                } else {
                    Toast.makeText(ChapterActivity.this, "Reached the first chapter", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentChapterIndex < sortedChapters.size() - 1) {
                    currentChapterIndex++;
                    Chapter nextChapter = sortedChapters.get(currentChapterIndex);
                    loadChapterImagesFromFirestore(comicId, nextChapter.getId());
                } else {
                    Toast.makeText(ChapterActivity.this, "Reached the last chapter", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo và hiển thị dialog danh sách các chapter
                showChapterListDialog();
            }
        });
    }

    private void loadAndSortChapters(String comicId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("comics")
                .document(comicId)
                .collection("chapters")
                .orderBy("number", Query.Direction.ASCENDING) // Sắp xếp theo number tăng dần
                .get()
                .addOnSuccessListener(chapterDocumentSnapshots -> {
                    sortedChapters = new ArrayList<>();
                    for (QueryDocumentSnapshot chapterSnapshot : chapterDocumentSnapshots) {
                        String chapterId = chapterSnapshot.getId();
                        String chapterTitle = chapterSnapshot.getString("title");
                        sortedChapters.add(new Chapter(chapterId, chapterTitle));
                    }
                    // Tìm vị trí của chapterId hiện tại trong danh sách đã sắp xếp
                    currentChapterIndex = findCurrentChapterIndex();
                    // Hiển thị ảnh của chương hiện tại
                    loadChapterImagesFromFirestore(comicId, chapterId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load and sort chapters", Toast.LENGTH_SHORT).show();
                });
    }

    private int findCurrentChapterIndex() {
        for (int i = 0; i < sortedChapters.size(); i++) {
            if (sortedChapters.get(i).getId().equals(chapterId)) {
                return i;
            }
        }
        return 0; // Trả về 0 nếu không tìm thấy
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

                        // Lấy tiêu đề của chương từ trường "title" của tài liệu chương
                        String chapterTitle = documentSnapshot.getString("title");

                        // Kiểm tra và thêm các URL ảnh vào danh sách chapterImages
                        if (images != null && !images.isEmpty()) {
                            chapterImages.clear(); // Xóa danh sách ảnh hiện có trước khi thêm mới
                            chapterImages.addAll(images);
                            imageAdapter.notifyDataSetChanged(); // Cập nhật Adapter sau khi thêm dữ liệu

                            // Cập nhật tiêu đề của chương trên TextView
                            chapterTitleTextView.setText(chapterTitle);
                        } else {
                            finish();
                            Toast.makeText(this, "No images found for this chapter", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        finish();
                        Toast.makeText(this, "Chapter not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    finish();
                    Toast.makeText(this, "Failed to load chapter images", Toast.LENGTH_SHORT).show();
                });
    }
    private void showChapterListDialog() {
        // Tạo dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Chapter");

        // Tạo danh sách các tên chương để hiển thị
        List<String> chapterTitles = new ArrayList<>();
        for (Chapter chapter : sortedChapters) {
            chapterTitles.add(chapter.getTitle());
        }

        // Chuyển danh sách thành mảng chuỗi
        String[] chapterArray = chapterTitles.toArray(new String[0]);

        // Xử lý sự kiện khi người dùng chọn một mục từ danh sách
        builder.setItems(chapterArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                // Lấy chapter được chọn
                Chapter selectedChapter = sortedChapters.get(index);
                String selectedChapterId = selectedChapter.getId();

                // Cập nhật currentChapterIndex
                currentChapterIndex = index;

                // Load và hiển thị ảnh của chapter đã chọn
                loadChapterImagesFromFirestore(comicId, selectedChapterId);

                // Đóng dialog sau khi người dùng chọn
                dialog.dismiss();
            }
        });

        // Hiển thị dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
