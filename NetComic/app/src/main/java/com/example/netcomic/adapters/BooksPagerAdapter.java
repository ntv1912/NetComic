package com.example.netcomic.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.netcomic.DownloadedBooksFragment;
import com.example.netcomic.FavoriteBooksFragment;

public class BooksPagerAdapter extends FragmentStateAdapter {
    public BooksPagerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FavoriteBooksFragment();
            case 1:
                return new DownloadedBooksFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Số lượng tab
    }
}
