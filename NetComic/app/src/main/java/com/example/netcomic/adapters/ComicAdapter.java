package com.example.netcomic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netcomic.R;
import com.example.netcomic.models.Comic;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ComicAdapter extends RecyclerView.Adapter<ComicAdapter.ComicViewHolder> {

    private List<Comic> comicList;
    private Context context;
    private Boolean isVertical=true;
    public ComicAdapter(Context context, List<Comic> comics,Boolean isVertical) {
        this.context = context;
        this.comicList = comics;
        this.isVertical= isVertical;
    }

    public void setData(List<Comic> comics) {
        this.comicList = comics;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ComicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(this.isVertical== true) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comic_v, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comic_h, parent, false);

        }
        return new ComicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComicViewHolder holder, int position) {
        Comic comic = comicList.get(position);

        // Load image using Picasso
        Picasso.get()
                .load(comic.getImageUrl())
                .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                .error(R.drawable.error_image) // Error image if loading fails
                .into(holder.imageView);

        holder.titleTextView.setText(comic.getTitle());
        holder.genreTextView.setText(comic.getGenre());
    }

    @Override
    public int getItemCount() {
        return comicList.size();
    }

    static class ComicViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView genreTextView;

        public ComicViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.comic_image);
            titleTextView = itemView.findViewById(R.id.comic_title);
            genreTextView = itemView.findViewById(R.id.comic_genre);
        }
    }
}