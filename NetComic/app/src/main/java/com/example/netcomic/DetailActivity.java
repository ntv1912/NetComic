package com.example.netcomic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.netcomic.adapters.ChapterAdapter;
import com.example.netcomic.models.Chapter;
import com.example.netcomic.models.Comic;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements ChapterAdapter.ChapterClickListener {

    private ImageView imageView;
    private TextView titleTextView;
    private TextView authorTextView;
    private TextView genreTextView;
    private ImageButton btnBack;
    private RecyclerView recyclerViewChapters;
    private ChapterAdapter chapterAdapter;
    private Comic comic;
    private Button btnReadFromStart,btnFollow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Ánh xạ các view từ layout activity_detail.xml
        imageView = findViewById(R.id.comic_image);
        titleTextView = findViewById(R.id.comic_title);
        authorTextView = findViewById(R.id.comic_author);
        genreTextView = findViewById(R.id.comic_genre);
        btnReadFromStart= findViewById(R.id.read_from_start_button);
        btnFollow= findViewById(R.id.follow_button);
        recyclerViewChapters = findViewById(R.id.recycler_view_chapters);

        // Lấy đối tượng Comic từ Intent
        comic = (Comic) getIntent().getSerializableExtra("COMIC");

        // Kiểm tra comic không null trước khi sử dụng
        if (comic != null) {
            // Hiển thị hình ảnh của truyện sử dụng Picasso
            Picasso.get()
                    .load(comic.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(imageView);

            // Hiển thị tiêu đề, tác giả và thể loại của truyện
            titleTextView.setText(comic.getTitle());
            authorTextView.setText("Tác Giả: " + comic.getAuthor());
            genreTextView.setText("Thể Loại: " + comic.getGenre());

            // Lấy danh sách chương từ Firestore
            loadChapters(comic.getId());
        } else {
            // Nếu comic là null, thông báo và kết thúc activity
            Toast.makeText(this, "Comic information is not available", Toast.LENGTH_SHORT).show();
            finish();
        }
        btnBack= findViewById(R.id.back_button);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onBackPressed();
                finish();
            }
        });
        btnReadFromStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chapterAdapter != null && !chapterAdapter.getChapterList().isEmpty()) {
                    // Lấy danh sách các chương từ adapter
                    List<Chapter> chapterList = chapterAdapter.getChapterList();

                    // Lấy chapter đầu tiên từ danh sách
                    Chapter firstChapter = chapterList.get(chapterList.size() - 1);

                    // Lấy comicId từ Comic được truyền vào DetailActivity
                    String comicId = comic.getId();

                    // Chuyển sang ChapterActivity và truyền thông tin của chương đầu tiên
                    Intent intent = new Intent(DetailActivity.this, ChapterActivity.class);
                    intent.putExtra("COMIC_ID", comicId); // Truyền comicId của chương
                    intent.putExtra("CHAPTER_ID", firstChapter.getId()); // Truyền chapterId của chương
                    intent.putExtra("CHAPTER_Title", firstChapter.getTitle()); // Truyền tiêu đề của chương
                    intent.putExtra("CHAPTER_Number", firstChapter.getNumber()); // Truyền số thứ tự của chương
                    startActivity(intent);
                } else {
                    Toast.makeText(DetailActivity.this, "No chapters available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy ID của người dùng, ví dụ: từ Firebase Auth hoặc một trường ID đã xác định
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    String userId = user.getUid();
                    String comicId = comic.getId();

                    // Thực hiện cập nhật trường followedComics cho người dùng trong Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users")
                            .document(userId)
                            .update("followedComics", FieldValue.arrayUnion(comicId))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    btnFollow.setText("Followed");
                                    Toast.makeText(DetailActivity.this, "Đã follow truyện thành công", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(DetailActivity.this, "Lỗi khi follow truyện", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(DetailActivity.this, "Người dùng không tồn tại", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loadChapters(String comicId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("comics")
                .document(comicId)
                .collection("chapters")
                .orderBy("number", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(chapterDocumentSnapshots -> {
                    List<Chapter> chapterList = new ArrayList<>();
                    for (QueryDocumentSnapshot chapterSnapshot : chapterDocumentSnapshots) {
                        String chapterId = chapterSnapshot.getId(); // Lấy ID của chương
                        String chapterTitle = chapterSnapshot.getString("title");
                        Long numberLong = chapterSnapshot.getLong("number");
                        int number = numberLong != null ? numberLong.intValue() : 0;
                        chapterList.add(new Chapter(chapterId, chapterTitle,number));
//                        chapterList.add(new Chapter(chapterId, chapterTitle));

                    }

                    // Hiển thị danh sách chương bằng ChapterAdapter
                    displayChapters(chapterList);
                })
                .addOnFailureListener(e -> {
                    // Xử lý khi không thể lấy danh sách chương
                    Toast.makeText(this, "Failed to load chapters", Toast.LENGTH_SHORT).show();
                });
    }

    private void displayChapters(List<Chapter> chapterList) {
        // Thiết lập adapter và hiển thị danh sách chương trên RecyclerView
        chapterAdapter = new ChapterAdapter(chapterList);
        chapterAdapter.setListener(this); // Thiết lập ChapterClickListener
        recyclerViewChapters.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChapters.setAdapter(chapterAdapter);
    }
    @Override
    public void onChapterClick(Chapter chapter) {
        // Xử lý khi người dùng nhấn vào một chapter trong RecyclerView
        Toast.makeText(this, "Clicked Chapter: " + chapter.getTitle(), Toast.LENGTH_SHORT).show();

        // Lấy comicId từ Comic được truyền vào DetailActivity
        String comicId = comic.getId();

        // Chuyển sang ChapterActivity và truyền thông tin của chương
        Intent intent = new Intent(this, ChapterActivity.class);
        intent.putExtra("COMIC_ID", comicId); // Truyền comicId của chương
        intent.putExtra("CHAPTER_ID", chapter.getId()); // Truyền chapterId của chương
        intent.putExtra("CHAPTER_Title", chapter.getTitle()); // Truyền chapterId của chương
        intent.putExtra("CHAPTER_Number", chapter.getNumber());
        startActivity(intent);
    }
}
