package com.example.wsapandroidapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.wsapandroidapp.DataModel.WeddingTips;
import com.example.wsapandroidapp.WeddingTipsActivity;
import com.example.wsapandroidapp.R;
import com.example.wsapandroidapp.WeddingTipsDetailsActivity;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WeddingTipsDetailsAdapter extends RecyclerView.Adapter<WeddingTipsDetailsAdapter.ViewHolder> {

    //
    private final List<Integer> ImageSet;
    private final LayoutInflater layoutInflater;
    private final Context context;
    //public WeddingTipsAdapter(Context context, List<WeddingTips> weddingTips) {
    public WeddingTipsDetailsAdapter(Context context, List<Integer> ImageSet) {
        //this.weddingTips = weddingTips;
        this.ImageSet = ImageSet;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.custom_wedding_tips_photo_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeddingTipsDetailsAdapter.ViewHolder holder, int position) {
        ImageView tvTipsPhoto = holder.tvTipsPhoto;
        tvTipsPhoto.setImageResource(ImageSet.get(position));
    }
    @Override
    public int getItemCount() {
        return ImageSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView tvTipsPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTipsPhoto = itemView.findViewById(R.id.tvTipsPhoto);
        }
    }
}
