package com.example.wsapandroidapp.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wsapandroidapp.R;

import java.util.List;

public class ImgArrayAdapter extends RecyclerView.Adapter<ImgArrayAdapter.ViewHolder> {

    Context context;
    List<Uri> imgArray;

    public ImgArrayAdapter(Context context, List<Uri> imgArray){
        this.context = context;
        this.imgArray = imgArray;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView tvTipsPhoto;
        TextView tvItemOverlay;
        public ViewHolder(View view){
            super(view);
            tvTipsPhoto = view.findViewById(R.id.tvTipsPhoto);
            tvItemOverlay = view.findViewById(R.id.tvItemOverlay);
        }
    }

    @NonNull
    @Override
    public ImgArrayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_add_image_wedding_tips_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImgArrayAdapter.ViewHolder holder, int position) {
        Glide.with(context).load(imgArray.get(position)).into(holder.tvTipsPhoto);
    }

    @Override
    public int getItemCount() {
        return imgArray.size();
    }
}
