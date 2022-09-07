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
        View view = layoutInflater.inflate(R.layout.custom_wedding_tips_grid_photo_layout, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull WeddingTipsChildAdapter.ViewHolder holder, int position) {
        ImageView tvTipsPhoto = holder.tvTipsPhoto;
        TextView tvItemOverLay = holder.tvItemOverlay;
        Glide.with(context).load(tipsImagesArrayList.get(position)).into(tvTipsPhoto);
        switch(position){
            case 1:
                if(tipsImagesArrayList.size() > 2) {
                    int Items = tipsImagesArrayList.size() - 2;
                    String moreItems = "+" + Items;
                    tvItemOverLay.setText(moreItems);
                    tvItemOverLay.setOnClickListener(view -> {
                            Intent intent = new Intent(context, TipsImagesActivity.class);
                            intent.putExtra("image", weddingTips.get(parentAdapter.getBindingAdapterPosition()).getId());
                            context.startActivity(intent);
                        });
                }
                else
                {
                    tvItemOverLay.setText("");
                    tvItemOverLay.setOnClickListener(null);
                }
                break;
        }
    }
    @Override
     public int getItemCount() {
        return tipsImagesArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView tvTipsPhoto;
        TextView tvItemOverlay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTipsPhoto = itemView.findViewById(R.id.tvTipsPhoto);
            tvItemOverlay = itemView.findViewById(R.id.tvItemOverlay);
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
