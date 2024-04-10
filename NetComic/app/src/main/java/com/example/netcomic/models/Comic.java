package com.example.netcomic.models;

import java.io.Serializable;
import java.util.List;

public class Comic  implements Serializable {
    private String Id;
    private String imageUrl;
    private String title;
    private String genre;
    private String author;
    private List<String> chapterList;

    public Comic() {
        // Default constructor required for Firestore
    }

    public Comic(String imageUrl, String title, String genre) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.genre = genre;
        this.author="";
        this.chapterList=null;
    }

    public Comic(String Id,String imageUrl, String title, String genre, String author, List<String> chapterList) {
        this.Id= Id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.genre = genre;
        this.author = author;
        this.chapterList = chapterList;
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
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public List<String> getChapterList() {
        return chapterList;
    }

    public void setChapterList(List<String> chapterList) {
        this.chapterList = chapterList;
    }
}
