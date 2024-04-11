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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private Button btnReadFromStart, btnFollow;
    private boolean isFollowed = false; // Trạng thái follow của người dùng đối với truyện này

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Ánh xạ các view từ layout activity_detail.xml
        imageView = findViewById(R.id.comic_image);
        titleTextView = findViewById(R.id.comic_title);
        authorTextView = findViewById(R.id.comic_author);
        genreTextView = findViewById(R.id.comic_genre);
        btnReadFromStart = findViewById(R.id.read_from_start_button);
        btnFollow = findViewById(R.id.follow_button);
        btnBack=findViewById(R.id.back_button);
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

            // Kiểm tra trạng thái follow của người dùng đối với truyện này
            checkFollowStatus();

            // Lấy danh sách chương từ Firestore
            loadChapters(comic.getId());

            // Thiết lập sự kiện click cho nút Back
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // Đóng DetailActivity khi nhấn nút Back
                }
            });

            // Thiết lập sự kiện click cho nút btnReadFromStart
            btnReadFromStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFirstChapter(); // Mở chương đầu tiên khi nhấn nút Read From Start
                }
            });

            // Thiết lập sự kiện click cho nút btnFollow
            btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleFollowComic(); // Thực hiện thêm/xóa comicId khỏi danh sách followedComics của người dùng
                }
            });
        } else {
            // Nếu comic là null, thông báo và kết thúc activity
            Toast.makeText(this, "Comic information is not available", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // Kiểm tra trạng thái follow của người dùng đối với truyện này
    private void checkFollowStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userId = user.getUid();
            String comicId = comic.getId();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                List<String> followedComics = (List<String>) documentSnapshot.get("followedComics");
                                isFollowed = followedComics != null && followedComics.contains(comicId);

                                // Cập nhật giao diện nút btnFollow dựa trên trạng thái follow
                                updateFollowButton();
                            }
                        }
                    });
        }
    }

    // Cập nhật giao diện nút btnFollow dựa trên trạng thái follow
    private void updateFollowButton() {
        if (isFollowed) {
            btnFollow.setText("Followed");
        } else {
            btnFollow.setText("Follow");
        }
    }

    // Thực hiện thêm/xóa comicId khỏi danh sách followedComics của người dùng
    private void toggleFollowComic() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userId = user.getUid();
            String comicId = comic.getId();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(userId);

            if (isFollowed) {
                // Nếu đang follow, xóa comicId khỏi danh sách followedComics của người dùng
                userRef.update("followedComics", FieldValue.arrayRemove(comicId))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                isFollowed = false;
                                updateFollowButton();
                                Toast.makeText(DetailActivity.this, "Đã bỏ theo dõi truyện", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DetailActivity.this, "Lỗi khi bỏ theo dõi truyện", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                // Nếu chưa follow, thêm comicId vào danh sách followedComics của người dùng
                userRef.update("followedComics", FieldValue.arrayUnion(comicId))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                isFollowed = true;
                                updateFollowButton();
                                Toast.makeText(DetailActivity.this, "Đã theo dõi truyện", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DetailActivity.this, "Lỗi khi theo dõi truyện", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    // Hiển thị danh sách chương bằng ChapterAdapter
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
                        String chapterId = chapterSnapshot.getId();
                        String chapterTitle = chapterSnapshot.getString("title");
                        Long numberLong = chapterSnapshot.getLong("number");
                        int number = numberLong != null ? numberLong.intValue() : 0;
                        chapterList.add(new Chapter(chapterId, chapterTitle, number));
                    }
                    // Hiển thị danh sách chương bằng ChapterAdapter
                    displayChapters(chapterList);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load chapters", Toast.LENGTH_SHORT).show();
                });
    }

    // Hiển thị danh sách chương bằng ChapterAdapter trên RecyclerView
    private void displayChapters(List<Chapter> chapterList) {
        chapterAdapter = new ChapterAdapter(chapterList);
        chapterAdapter.setListener(this); // Thiết lập ChapterClickListener
        recyclerViewChapters.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChapters.setAdapter(chapterAdapter);
    }

    // Mở chương đầu tiên khi nhấn nút Read From Start
    private void openFirstChapter() {
        if (chapterAdapter != null && !chapterAdapter.getChapterList().isEmpty()) {
            List<Chapter> chapterList = chapterAdapter.getChapterList();
            Chapter firstChapter = chapterList.get(chapterList.size() - 1); // Chương đầu tiên
            Intent intent = new Intent(DetailActivity.this, ChapterActivity.class);
            intent.putExtra("COMIC_ID", comic.getId());
            intent.putExtra("CHAPTER_ID", firstChapter.getId());
            intent.putExtra("CHAPTER_Title", firstChapter.getTitle());
            intent.putExtra("CHAPTER_Number", firstChapter.getNumber());
            startActivity(intent);
        } else {
            Toast.makeText(DetailActivity.this, "No chapters available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onChapterClick(Chapter chapter) {
        // Xử lý khi người dùng nhấn vào một chapter trong RecyclerView
        Toast.makeText(this, "Clicked Chapter: " + chapter.getTitle(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ChapterActivity.class);
        intent.putExtra("COMIC_ID", comic.getId());
        intent.putExtra("CHAPTER_ID", chapter.getId());
        intent.putExtra("CHAPTER_Title", chapter.getTitle());
        intent.putExtra("CHAPTER_Number", chapter.getNumber());
        startActivity(intent);
    }
}
