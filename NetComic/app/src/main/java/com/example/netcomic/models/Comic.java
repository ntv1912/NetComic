package com.example.netcomic.models;
public class Comic {

    private String imageUrl;
    private String title;
    private String genre;

    public Comic() {
        // Default constructor required for Firestore
    }

    public Comic(String imageUrl, String title, String genre) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.genre = genre;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
