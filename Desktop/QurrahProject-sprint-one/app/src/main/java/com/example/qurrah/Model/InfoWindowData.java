package com.example.qurrah.Model;

public class InfoWindowData {
    private String catogery;
    private String type;
    private String title;
    public String img;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getType() {
        return type;
    }

    public String getCatogery() {
        return catogery;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCatogery(String catogery) {
        this.catogery = catogery;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
