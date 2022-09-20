package com.example.wsapandroidapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wsapandroidapp.R;
import com.example.wsapandroidapp.TipsImagesActivity;
import com.example.wsapandroidapp.WeddingTipsFormActivity;

import java.util.List;

public class ImgArrayAdapter extends RecyclerView.Adapter<ImgArrayAdapter.ViewHolder> {

    Context context;
    List<Uri> imgArray;
    boolean isUpdateMode;

    public ImgArrayAdapter(Context context,Boolean isUpdateMode, List<Uri> imgArray){
        this.isUpdateMode = isUpdateMode;
        this.context = context;
        this.imgArray = imgArray;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView tvTipsPhoto, removeImage;

        public ViewHolder(View view){
            super(view);
            tvTipsPhoto = view.findViewById(R.id.tvTipsPhoto);
            removeImage = view.findViewById(R.id.removeImage);
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
        if(isUpdateMode)
        {
                holder.removeImage.setOnClickListener(view ->{
                int getPos = holder.getBindingAdapterPosition();
                 imgArray.remove(getPos);
                 notifyItemRemoved(getPos);
                 if (adapterListener != null) adapterListener.passImg(getPos);
            });
        }
        else{
            holder.removeImage.setOnClickListener(view ->{
                int getPos = holder.getAdapterPosition();
                imgArray.remove(getPos);
                notifyItemRemoved(getPos);
            });
        }
        Glide.with(context).load(imgArray.get(position)).into(holder.tvTipsPhoto);
    }

    @Override
    public int getItemCount() {
        return imgArray.size();
    }

    private AdapterListener adapterListener;

    public interface AdapterListener
    {
        void passImg (int getPos);
    }
    public void setAdapterListener(ImgArrayAdapter.AdapterListener adapterListener) {
        this.adapterListener = adapterListener;
    }

}
