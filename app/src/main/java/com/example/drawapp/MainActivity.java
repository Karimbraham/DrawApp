package com.example.drawapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private SeekBar seekBar;
    int defaultColor;
    int defaultPenSize = 8;
    boolean firstTime = false;

    private float floatStartX = -1, floatStartY = -1,
            floatEndX = -1, floatEndY = -1;

    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    ArrayList<Point> points = new ArrayList<Point>();

    private Uri tempImageURI;
    boolean blackground = false;

    class Point {
        float x, y;
        float dx, dy;
        public Point() {
        }
        public Point(float x, float y) {
            this.x =x;
            this.y=y;
        }
        @Override
        public String toString() {
            return x + ", " + y;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this
                ,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED);
        imageView = findViewById(R.id.imageView);
        seekBar = findViewById(R.id.penSize);

         seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                paint.setStrokeWidth(i);
                seekBar.setMax(50);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

         defaultColor = ContextCompat.getColor(MainActivity.this,R.color.black);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){

        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceBundle){
        super.onSaveInstanceState(savedInstanceBundle);
        if (bitmap != null) {
            SaveTempImage();
            //savedInstanceBundle.putParcelable("uri", tempImageURI);
        }
    }

    public void changeColor(View view) {
        AmbilWarnaDialog ambilDialog = new AmbilWarnaDialog(this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defaultColor= color;
                paint.setColor(color);
            }
        });
        ambilDialog.show();
    }

    public void ChangeBackground(View view) {
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        Bitmap underlayBitmap = Bitmap.createBitmap(imageView.getWidth(),
                imageView.getHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canv = new Canvas(underlayBitmap);
        int col;
        if (blackground) {
          col = Color.WHITE;
          blackground = false;
        }else{
            col = Color.BLACK;
            blackground = true;
        }
        canv.drawColor(col);
        if (bitmap != null) {
            canv.drawBitmap(bitmap, 0F, 0F, null);
        }
        imageView.setImageBitmap(underlayBitmap);

    }

    public void onDraw(Canvas canvas) {
        Path path = new Path();

        if(points.size() > 1){
            for(int i = points.size() - 2; i < points.size(); i++){
                if(i >= 0){
                    Point point = points.get(i);

                    if(i == 0){
                        Point next = points.get(i + 1);
                        point.dx = ((next.x - point.x) / 3);
                        point.dy = ((next.y - point.y) / 3);
                    }
                    else if(i == points.size() - 1){
                        Point prev = points.get(i - 1);
                        point.dx = ((point.x - prev.x) / 3);
                        point.dy = ((point.y - prev.y) / 3);
                    }
                    else{
                        Point next = points.get(i + 1);
                        Point prev = points.get(i - 1);
                        point.dx = ((next.x - prev.x) / 3);
                        point.dy = ((next.y - prev.y) / 3);
                    }
                }
            }
        }

        boolean first = true;
        for(int i = 0; i < points.size(); i++){
            Point point = points.get(i);
            if(first){
                first = false;
                path.moveTo(point.x, point.y);
            }
            else{
                Point prev = points.get(i - 1);
                path.cubicTo(prev.x + prev.dx, prev.y + prev.dy, point.x - point.dx, point.y - point.dy, point.x, point.y);
            }
        }
        canvas.drawPath(path, paint);
    }

    private void drawPaintSketchImage() {
        CreateBitmap();
        onDraw(canvas);
        /*canvas.drawLine(floatStartX,
                floatStartY-50,
                floatEndX,
                floatEndY-50,
                paint);*/
        imageView.setImageBitmap(bitmap);
        ApplyBackground();

    }

    private void ApplyBackground(){
        Bitmap result = getBitmapBG();
        imageView.setImageBitmap(result);
    }

    private Bitmap getBitmapBG() {
        Bitmap underlayBitmap = Bitmap.createBitmap(imageView.getWidth(),
                imageView.getHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canv = new Canvas(underlayBitmap);
        int col;
        if (blackground) {
            col = Color.BLACK;
        }else{
            col = Color.WHITE;
        }
        canv.drawColor(col);
        if (bitmap != null) {
            canv.drawBitmap(bitmap, 0F, 0F, null);
        }
        return underlayBitmap;
    }

    private void CreateBitmap() {
        if (bitmap == null){
            bitmap = Bitmap.createBitmap(imageView.getWidth(),
                    imageView.getHeight(),
                    Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            paint.setColor(Color.RED);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(defaultPenSize);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point point = new Point();
        point.x = (int)event.getX();
        point.y = (int)event.getY();
        points.add(point);
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            floatStartX = event.getX();
            floatStartY = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE){
            floatEndX = event.getX();
            floatEndY = event.getY();
            drawPaintSketchImage();
            floatStartX = event.getX();
            floatStartY = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP){
            floatEndX = event.getX();
            floatEndY = event.getY();
            drawPaintSketchImage();
            points.clear();
            imageView.invalidate();
        }
        return super.onTouchEvent(event);
    }


    public void buttonSaveImage(View view){
        File fileSaveImage = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                Calendar.getInstance().getTime().toString() + ".png");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileSaveImage);
            Bitmap btBg = getBitmapBG();
            btBg.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Toast.makeText(this,
                    "File Saved Successfully",
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
        bitmap = null;
        points.clear();
        imageView.setImageBitmap(bitmap);
        File fTemp = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "TEMP.png");
        if (fTemp.exists()) {
            boolean deleted = fTemp.delete();
        }
    }

    public void SaveTempImage() {
        File fileSaveImage = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "TEMP.png");
        tempImageURI = Uri.fromFile(fileSaveImage);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileSaveImage);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ClearCanvas(View view){
        bitmap = null;
        points.clear();
        imageView.setImageBitmap(bitmap);
        File fTemp = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "TEMP.png");
        if (fTemp.exists()) {
            boolean deleted = fTemp.delete();
        }
        ApplyBackground();
    }

    public void GalleryIntent(View view) {
        Intent intent = new Intent(MainActivity.this,MainActivity2.class);
        startActivity(intent);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        if (firstTime == true) {
            return;
        }
        CreateBitmap();
        if(getIntent().hasExtra("imageUri")){
            Uri imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));
            File filePassedImage = new File(imageUri.getPath());
            if(filePassedImage.exists()) {
                try {
                    Bitmap bitTemp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(filePassedImage));
                    canvas.drawBitmap(bitTemp, 0F, 0F, null);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else {
        try {
            File fileSaveImage = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "TEMP.png");
            if (fileSaveImage.exists()) {
                tempImageURI = Uri.fromFile(fileSaveImage);
                Bitmap bitmTemp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), tempImageURI);
                canvas.drawBitmap(bitmTemp, 0F, 0F, null);
                imageView.setImageBitmap(bitmap);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        }
        firstTime=true;

    }
}