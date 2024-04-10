package com.example.netcomic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netcomic.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChapterImageAdapter extends RecyclerView.Adapter<ChapterImageAdapter.ChapterImageViewHolder> {

    private List<String> chapterImages;
    private Context context;

    public ChapterImageAdapter(List<String> chapterImages) {
        this.chapterImages = chapterImages;
    }

    @NonNull
    @Override
    public ChapterImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_chapter_image, parent, false);
        return new ChapterImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterImageViewHolder holder, int position) {
        String imageUrl = chapterImages.get(position);
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .fit()
                .centerInside()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return chapterImages.size();
    }

    static class ChapterImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ChapterImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_chapter);
        }
    }
}