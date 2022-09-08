package com.example.wsapandroidapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wsapandroidapp.DataModel.WeddingTips;
import com.example.wsapandroidapp.ImageActivity;
import com.example.wsapandroidapp.R;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class AdminWeddingTipsAdapter extends RecyclerView.Adapter<AdminWeddingTipsAdapter.ViewHolder>{
    
    private final List<WeddingTips> weddingTips;
    private final LayoutInflater layoutInflater;
    private List<ArrayList> tipsImagesList;

    private final Context context;

    public AdminWeddingTipsAdapter(Context context, List<WeddingTips> weddingTips, List<ArrayList>tipsImagesList) {
        this.tipsImagesList = tipsImagesList;
        this.weddingTips = weddingTips;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public AdminWeddingTipsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.custom_admin_exhibitor_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminWeddingTipsAdapter.ViewHolder holder, int position) {
        TextView tvTipTitle = holder.tvWeddingTips,
                tvDescription = holder.tvDescription;
        ImageView imgPoster = holder.imgPoster,
                imgMoreInfo = holder.imgMoreInfo,
                imgUpdate = holder.imgUpdate,
                imgDelete = holder.imgDelete,
                imgMove = holder.imgMove;

        imgMoreInfo.setVisibility(View.GONE);
        imgMove.setVisibility(View.GONE);

        WeddingTips weddingTip = weddingTips.get(position);
        tvTipTitle.setText(weddingTip.getTopic());
        tvDescription.setText(weddingTip.getDescription());
       Glide.with(context).load(tipsImagesList.get(position).get(0).toString()).centerCrop().placeholder(R.drawable.ic_wsap).
                error(R.drawable.ic_wsap).into(imgPoster);

        imgPoster.setOnClickListener(view -> {
            Intent intent = new Intent(context, ImageActivity.class);
            intent.putExtra("image", tipsImagesList.get(position).get(0).toString());
            context.startActivity(intent);
        });

        imgUpdate.setOnClickListener(view -> {
            if (adapterListener != null) adapterListener.onEdit(weddingTip);
        });

        imgDelete.setOnClickListener(view -> {
            if (adapterListener != null) adapterListener.onDelete(weddingTip);
        });

    }

    @Override
    public int getItemCount() {
        return weddingTips.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvWeddingTips, tvDescription;
        ImageView imgPoster, imgMoreInfo, imgUpdate, imgDelete, imgMove;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvWeddingTips = itemView.findViewById(R.id.tvExhibitor);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            imgMoreInfo = itemView.findViewById(R.id.imgMoreInfo);
            imgUpdate = itemView.findViewById(R.id.imgUpdate);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            imgMove = itemView.findViewById(R.id.imgMove);
            imgPoster = itemView.findViewById(R.id.imgPoster);
            
        }
    }
    private AdminWeddingTipsAdapter.AdapterListener adapterListener;

    public interface AdapterListener {
        void onClick(WeddingTips weddingTips);
        void onEdit(WeddingTips weddingTips);
        void onDelete(WeddingTips weddingTips);
    }

    public void setAdapterListener(AdminWeddingTipsAdapter.AdapterListener adapterListener) {
        this.adapterListener = adapterListener;
    }
}
