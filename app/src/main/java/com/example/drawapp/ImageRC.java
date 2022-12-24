package com.example.drawapp;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;

public class ImageRC {
    private Bitmap imageBitmap;
    public Uri imageUri;

    public ImageRC(Bitmap imgF,Uri uri) {
        this.imageBitmap = imgF;
        this.imageUri = uri;
    }

    public Bitmap getImageBt() {
        return imageBitmap;
    }

    public void setImageBt(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }
}
