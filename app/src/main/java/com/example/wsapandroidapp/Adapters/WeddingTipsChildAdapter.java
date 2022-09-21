package com.example.wsapandroidapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wsapandroidapp.DataModel.WeddingTips;
import com.example.wsapandroidapp.TipsImagesActivity;
import com.example.wsapandroidapp.R;


import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WeddingTipsChildAdapter extends RecyclerView.Adapter<WeddingTipsChildAdapter.ViewHolder> {

    private final List<String> tipsImagesArrayList;
    private final WeddingTipsAdapter.ViewHolder parentAdapter;
    private final List<WeddingTips> weddingTips;
    private final LayoutInflater layoutInflater;
    private final Context context;

    public WeddingTipsChildAdapter(Context context, List<WeddingTips> weddingTips, List<String> tipsImagesArrayList, WeddingTipsAdapter.ViewHolder parentAdapter) {
        this.parentAdapter = parentAdapter;
        this.weddingTips = weddingTips;
        this.tipsImagesArrayList = tipsImagesArrayList;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
      }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(tipsImagesArrayList.size() == 1) {
            View view = layoutInflater.inflate(R.layout.custom_wedding_tips_grid_photo_layout, parent, false);
            return new ViewHolder(view);
        }
        else if(tipsImagesArrayList.size() == 2){
            View view = layoutInflater.inflate(R.layout.custom_wedding_tips_grid_photo_layout1, parent, false);
            return new ViewHolder(view);
        }
        else{
            View view = layoutInflater.inflate(R.layout.custom_wedding_tips_grid_photo_layout2, parent, false);
            return new ViewHolder(view);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull WeddingTipsChildAdapter.ViewHolder holder, int position) {
        ImageView tvTipsPhoto = holder.tvTipsPhoto;
        ImageView tvTipsPhoto1 = holder.tvTipsPhoto1;
        ImageView tvTipsPhoto2 = holder.tvTipsPhoto2;
        TextView tvItemOverLay = holder.tvItemOverlay;
        View disableOverlay = holder.disableOverlay;
        if(tipsImagesArrayList.size() == 1){
            Glide.with(context).load(tipsImagesArrayList.get(position)).centerCrop().into(tvTipsPhoto);
            tvTipsPhoto.setOnClickListener(view -> {
                Intent intent = new Intent(context, TipsImagesActivity.class);
                intent.putExtra("image", weddingTips.get(parentAdapter.getBindingAdapterPosition()).getId());
                context.startActivity(intent);
            });
        }
        else if(tipsImagesArrayList.size() == 2){
            Glide.with(context).load(tipsImagesArrayList.get(0)).centerCrop().into(tvTipsPhoto);
            Glide.with(context).load(tipsImagesArrayList.get(1)).centerCrop().into(tvTipsPhoto1);
            tvTipsPhoto.setOnClickListener(view -> {
                Intent intent = new Intent(context, TipsImagesActivity.class);
                intent.putExtra("image", weddingTips.get(parentAdapter.getBindingAdapterPosition()).getId());
                context.startActivity(intent);
            });
        }
        else{
            Glide.with(context).load(tipsImagesArrayList.get(0)).centerCrop().into(tvTipsPhoto);
            Glide.with(context).load(tipsImagesArrayList.get(1)).centerCrop().into(tvTipsPhoto1);
            Glide.with(context).load(tipsImagesArrayList.get(2)).centerCrop().into(tvTipsPhoto2);
            tvTipsPhoto.setOnClickListener(view -> {
                Intent intent = new Intent(context, TipsImagesActivity.class);
                intent.putExtra("image", weddingTips.get(parentAdapter.getBindingAdapterPosition()).getId());
                context.startActivity(intent);
            });
            if(tipsImagesArrayList.size() > 3) {
                int Items = tipsImagesArrayList.size() - 3;
                String moreItems = "+" + Items;
                tvItemOverLay.setText(moreItems);
                tvItemOverLay.setOnClickListener(view -> {
                    Intent intent = new Intent(context, TipsImagesActivity.class);
                    intent.putExtra("image", weddingTips.get(parentAdapter.getBindingAdapterPosition()).getId());
                    context.startActivity(intent);
                });
                disableOverlay.setVisibility(View.VISIBLE);
            }
            else
            {
                tvItemOverLay.setText("");
                tvItemOverLay.setOnClickListener(null);
            }
        }

    }
    @Override
     public int getItemCount() {
        return tipsImagesArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView tvTipsPhoto,tvTipsPhoto1, tvTipsPhoto2;
        TextView tvItemOverlay;
        View disableOverlay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTipsPhoto = itemView.findViewById(R.id.tvTipsPhoto);
            tvTipsPhoto1 = itemView.findViewById(R.id.tvTipsPhoto1);
            tvTipsPhoto2 = itemView.findViewById(R.id.tvTipsPhoto2);
            tvItemOverlay = itemView.findViewById(R.id.tvItemOverlay);
            disableOverlay = itemView.findViewById(R.id.disableOverlay);
        }
    }
    private WeddingTipsAdapter.AdapterListener adapterListener;
    public interface AdapterListener {
        void onClick(WeddingTips weddingTip);
    }

    public void setAdapterListener(WeddingTipsAdapter.AdapterListener adapterListener) {
        this.adapterListener = adapterListener;
    }
}
