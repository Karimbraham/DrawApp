package com.example.drawapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.MyViewHolder>{
    private ArrayList<ImageRC> imagesList;
    private Context context;
    private Activity activity;

    public RecycleAdapter(Activity activity, Context context, ArrayList<ImageRC> list) {
        this.activity = activity;
        this.context = context;
        this.imagesList = list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgView;
        ConstraintLayout mainLayout;
        ImageButton deleteBtn;

        public MyViewHolder (final View view) {
            super(view);
            imgView = view.findViewById(R.id.lv_image);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }

    @NonNull
    @Override
    public RecycleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View itemView = LayoutInflater.from (parent.getContext()).inflate(R.layout.list_view,parent,false);
       return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleAdapter.MyViewHolder holder, int position) {
        Bitmap imageBt = imagesList.get(holder.getAdapterPosition()).getImageBt();
        holder.imgView.setImageBitmap(imageBt);
        holder.imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("imageUri", imagesList.get(holder.getAdapterPosition()).imageUri.toString());
                context.startActivity(intent);
            }
        });
        holder.deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                File fTemp = new File(imagesList.get(holder.getAdapterPosition()).imageUri.getPath());
                if (fTemp.exists()) {
                    boolean deleted = fTemp.delete();
                }
                Intent intent = new Intent(context, MainActivity2.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }
}
