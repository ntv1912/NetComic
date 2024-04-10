package com.example.netcomic;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Ánh xạ các view từ layout activity_detail.xml
        imageView = findViewById(R.id.comic_image);
        titleTextView = findViewById(R.id.comic_title);
        authorTextView = findViewById(R.id.comic_author);
        genreTextView = findViewById(R.id.comic_genre);
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
                        chapterList.add(new Chapter(chapterId, chapterTitle));
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
        startActivity(intent);
    }
}
