package com.example.netcomic.models;

import java.io.Serializable;
import java.util.List;

public class Chapter  implements Serializable {
    private String Id;
    private String Images;
    private String title;
    private int number;

    public Chapter(String id, String title) {
        Id = id;
        this.title = title;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getImages() {
        return Images;
    }

    public void setImages(String images) {
        Images = images;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
