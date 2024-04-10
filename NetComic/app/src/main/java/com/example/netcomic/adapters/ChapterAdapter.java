package com.example.netcomic.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.netcomic.R;
import com.example.netcomic.models.Chapter;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {

    private List<Chapter> chapterList;
    private ChapterClickListener listener;

    public ChapterAdapter(List<Chapter> chapterList) {
        this.chapterList = chapterList;
    }

    public void setListener(ChapterClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter chapter = chapterList.get(position);
        holder.chapterTextView.setText(chapter.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onChapterClick(chapter); // Gửi sự kiện khi chọn chương
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView chapterTextView;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            chapterTextView = itemView.findViewById(R.id.text_view_chapter);
        }
    }

    public interface ChapterClickListener {
        void onChapterClick(Chapter chapter);
    }
}

