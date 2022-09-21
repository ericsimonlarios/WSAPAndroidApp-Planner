package com.example.wsapandroidapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.wsapandroidapp.DataModel.WeddingTips;
import com.example.wsapandroidapp.R;
import com.example.wsapandroidapp.WeddingTipsDetailsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TipsImagesActivityAdapter extends RecyclerView.Adapter<TipsImagesActivityAdapter.ViewHolder> {


    private final List<String> tipsImagesArrayList;
    private final LayoutInflater layoutInflater;
    private final Context context;


    public TipsImagesActivityAdapter(Context context, List<String> tipsImagesArrayList) {
        this.tipsImagesArrayList = tipsImagesArrayList;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.custom_tips_images, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull TipsImagesActivityAdapter.ViewHolder holder, int position) {
        Glide.with(context).load(tipsImagesArrayList.get(position)).placeholder(R.drawable.ic_wsap).
                error(R.drawable.ic_wsap).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return tipsImagesArrayList.size();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
          imageView = itemView.findViewById(R.id.imageView);
        }
    }
}


