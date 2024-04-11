package com.example.netcomic;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoriteBooksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoriteBooksFragment extends Fragment  implements ComicAdapter.ComicClickListener  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private ComicAdapter comicAdapter;
    private List<Comic> favoriteComics = new ArrayList<>();
    public FavoriteBooksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoriteBooksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoriteBooksFragment newInstance(String param1, String param2) {
        FavoriteBooksFragment fragment = new FavoriteBooksFragment();
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
        View view = inflater.inflate(R.layout.fragment_favorite_books, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_favorite_book);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        comicAdapter = new ComicAdapter(getContext(), favoriteComics, true, this);
        recyclerView.setAdapter(comicAdapter);

        // Load favorite comics
        loadFavoriteComics();

        return view;
    }

    private void loadFavoriteComics() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            List<String> followedComics = (List<String>) documentSnapshot.get("followedComics");
                            if (followedComics != null && !followedComics.isEmpty()) {
                                loadComicDetails(followedComics);
                            } else {
                                Toast.makeText(getContext(), "No followed comics found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error fetching followed comics: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void loadComicDetails(List<String> comicIds) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (String comicId : comicIds) {
            db.collection("comics")
                    .document(comicId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Comic comic = documentSnapshot.toObject(Comic.class);
                            if (comic != null) {
                                comic.setId(comicId);
                                favoriteComics.add(comic);
                                comicAdapter.setData(favoriteComics);
                            }
                        } else {
                            Toast.makeText(getContext(), "Comic details not found for ID: " + comicId, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error fetching comic details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override
    public void onComicClick(Comic comic) {
        // Xử lý sự kiện khi người dùng nhấn vào một truyện trong danh sách yêu thích
        Toast.makeText(getContext(), "Clicked: " + comic.getTitle(), Toast.LENGTH_SHORT).show();

        // Chuyển sang DetailActivity và truyền thông tin của truyện
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra("COMIC", comic); // Gửi đối tượng Comic qua Intent
        startActivity(intent);
    }
}