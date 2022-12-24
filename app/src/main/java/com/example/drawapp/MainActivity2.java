package com.example.drawapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity2 extends AppCompatActivity {

    boolean firstTime = false;
    private ArrayList<ImageRC> imagesList;
    private RecyclerView rcView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        imagesList = new ArrayList<ImageRC>();
        rcView = findViewById(R.id.rcViewId);

       try {
            setImages();
            setAdapter();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setAdapter() {
        RecycleAdapter adapter = new RecycleAdapter(MainActivity2.this,this,imagesList);
        RecyclerView.LayoutManager rcl = new LinearLayoutManager(getApplicationContext());
        rcView.setLayoutManager(rcl);
        rcView.setItemAnimator(new DefaultItemAnimator());
        rcView.setAdapter(adapter);
    }

    private void setImages() throws IOException {
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String pathDir = baseDir + "/Android/data/com.example.drawapp/files/Pictures";
        File folder = new File(pathDir);
        if(folder.exists()) {
            File[] allFiles = folder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"));
                }
            });
            for (int i = 0; i < allFiles.length; i++) {
                if (!allFiles[i].getName().equals("TEMP.png")) {
                    Log.d("tlag", allFiles[i].getName());
                Bitmap bitTemp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(allFiles[i]));
                imagesList.add(new ImageRC(bitTemp,Uri.fromFile(allFiles[i])));
                }
            }
        }
    }

    public void SwitchActivity(View view) {
        Intent intent = new Intent(MainActivity2.this,MainActivity.class);
        startActivity(intent);
    }

}