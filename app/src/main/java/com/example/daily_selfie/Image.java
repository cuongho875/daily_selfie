package com.example.daily_selfie;

import android.graphics.Bitmap;

public class Image {
    private String name;
    private Bitmap Img;
    public Image(String name, Bitmap Img){
        this.name=name;
        this.Img=Img;
    }

    public Bitmap getImg() {
        return Img;
    }

    public String getName() {
        return name;
    }
}
