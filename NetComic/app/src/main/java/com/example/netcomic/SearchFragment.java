package com.example.netcomic;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.netcomic.adapters.ComicAdapter;
import com.example.netcomic.models.Comic;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private ComicAdapter comicAdapter;
    private List<Comic> comicList=new ArrayList<>();
    private List<Comic> loadedComicList= new ArrayList<>() ;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_search, container, false);
        // Find views
        searchView = view.findViewById(R.id.search_view);
        recyclerView = view.findViewById(R.id.recycler_view_search);

        // Initialize RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        comicAdapter = new ComicAdapter(getContext(), comicList,false);
        loadAllComics();
        recyclerView.setAdapter(comicAdapter);

        // Set up SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search query submission (e.g., perform search)
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle query text change (e.g., update search results as user types)
                performSearch(newText);
                return true;
            }
        });

        return view;
    }
    private void performSearch(String query) {
        // Clear previous search results
        comicList.clear();

        if (TextUtils.isEmpty(query)) {
            // Nếu 'query' rỗng, hiển thị toàn bộ danh sách truyện
            loadAllComics(); // Phương thức này sẽ tải tất cả truyện từ Firestore hoặc cơ sở dữ liệu local
        } else {
            // Nếu 'query' không rỗng, thực hiện tìm kiếm và cập nhật danh sách truyện
            performFilteredSearch(query); // Phương thức này sẽ thực hiện tìm kiếm theo 'query' và cập nhật comicList
        }
    }

    private void loadAllComics() {
        loadedComicList.clear();
        // TODO: Thực hiện logic để tải tất cả truyện từ Firestore hoặc cơ sở dữ liệu local
        // Ví dụ: Lấy danh sách truyện từ Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("comics")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Comic comic = documentSnapshot.toObject(Comic.class);
                        loadedComicList.add(comic);
                    }
                    // Sau khi có danh sách truyện, cập nhật RecyclerView thông qua ComicAdapter
                    comicAdapter.setData(loadedComicList);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load comics: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error fetching comics: " + e.getMessage());
                });
    }

    private void performFilteredSearch(String query) {
        // Xóa các kết quả tìm kiếm trước đó
        comicList.clear();

        // Thực hiện tìm kiếm trong danh sách truyện đã tải sẵn
        String lowercaseQuery = query.toLowerCase(); // Chuyển query về chữ thường để không phân biệt chữ hoa chữ thường

        for (Comic comic : loadedComicList) {
            String title = comic.getTitle().toLowerCase(); // Lấy tiêu đề của truyện và chuyển về chữ thường

            // Kiểm tra xem tiêu đề của truyện có chứa query không (không phân biệt chữ hoa chữ thường)
            if (title.contains(lowercaseQuery)) {
                comicList.add(comic); // Thêm truyện vào danh sách kết quả tìm kiếm
            }
        }

        // Cập nhật RecyclerView với kết quả tìm kiếm
        comicAdapter.setData(comicList);
    }
}

