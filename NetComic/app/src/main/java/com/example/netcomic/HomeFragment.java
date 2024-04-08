package com.example.netcomic;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.netcomic.adapters.ComicAdapter;
import com.example.netcomic.models.Comic;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView rcv1,rcv2;
    private ComicAdapter comicAdapter1,comicAdapter2;
    private List<Comic> comicList;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        // Inflate layout của fragment_home
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Tìm và ánh xạ RecyclerView theo chiều ngang từ layout
        rcv1 = view.findViewById(R.id.recycler_view_horizontal);
        rcv2= view.findViewById(R.id.recycler_view_vertical);
        // Thiết lập LayoutManager để hiển thị theo chiều ngang
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rcv1.setLayoutManager(layoutManager1);
        //
        LinearLayoutManager layoutManager2= new GridLayoutManager(getContext(),1,LinearLayoutManager.VERTICAL,false);
        rcv2.setLayoutManager(layoutManager2);

        // Khởi tạo danh sách truyện
        comicList = new ArrayList<>();

        // Khởi tạo adapter và gắn vào RecyclerView
        comicAdapter1 = new ComicAdapter(getContext(), comicList,true);
        rcv1.setAdapter(comicAdapter1);

        comicAdapter2 = new ComicAdapter(getContext(),comicList,false);
        rcv2.setAdapter(comicAdapter2);

        // Load danh sách truyện từ Cloud Firestore
        loadComicsFromFirestore();

        // Trả về view của fragment đã inflate và thiết lập
        return view;
    }
    private void loadComicsFromFirestore() {
        // Code để truy xuất dữ liệu truyện từ Cloud Firestore và cập nhật vào comicList
        // Đoạn code này sẽ gọi Firestore và lấy danh sách truyện, sau đó cập nhật comicList và gọi notifyDataSetChanged() trên adapter
        // Bạn cần thay thế phần này với phương thức truy xuất dữ liệu thực tế từ Firestore của bạn
        // Ví dụ:
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("comics")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Comic> comicList = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Chuyển đổi tài liệu Firestore thành đối tượng Comic
                        Comic comic = documentSnapshot.toObject(Comic.class);
                        comicList.add(comic);
                    }
                    // Sau khi có danh sách truyện, cập nhật RecyclerView thông qua ComicAdapter
                    comicAdapter1.setData(comicList); // Assume ComicAdapter has a method to set data
                    comicAdapter2.setData(comicList);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load comics: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Xử lý lỗi khi truy xuất dữ liệu từ Firestore không thành công
                    Log.e(TAG, "Error fetching comics: " + e.getMessage());
                });
    }

}