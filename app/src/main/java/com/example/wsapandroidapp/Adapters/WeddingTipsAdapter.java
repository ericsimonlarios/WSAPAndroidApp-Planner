package com.example.wsapandroidapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.wsapandroidapp.DataModel.WeddingTips;
import com.example.wsapandroidapp.WeddingTipsActivity;
import com.example.wsapandroidapp.R;
import com.example.wsapandroidapp.WeddingTipsDetailsActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WeddingTipsAdapter extends RecyclerView.Adapter<WeddingTipsAdapter.ViewHolder> {

   // private final List<WeddingTips> weddingTips;
    private List<String> dataSet;
    private List<String> dataSet2;
    private final LayoutInflater layoutInflater;

    private final Context context;
    //public WeddingTipsAdapter(Context context, List<WeddingTips> weddingTips) {
    public WeddingTipsAdapter(Context context, List<String> dataSet, List<String> dataSet2) {
       //this.weddingTips = weddingTips;
        this.dataSet = dataSet;
        this.dataSet2 = dataSet2;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.custom_wedding_tips_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeddingTipsAdapter.ViewHolder holder, int position) {
        TextView tvTipTitle = holder.tvTipTitle,
                tvTipDescription = holder.tvTipDescription,
                tvSeeMore = holder.tvSeeMore;

        // WeddingTips weddingTips = weddingTips.get(position)
        //tvTipTitle.setText(weddingTips.getTopic());
        //tvTipDescription.setText(weddingTips.getDescription());
        tvTipTitle.setText(dataSet.get(0));
        tvTipDescription.setText(dataSet2.get(0));

        tvSeeMore.setOnClickListener(view -> {
            Intent intent = new Intent(context, WeddingTipsDetailsActivity.class);
           //intent.putExtra("weddingTipsId", weddingTips.getId());
            context.startActivity(intent);
        });
    }
    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTipTitle, tvTipDescription, tvDateCreated, tvSeeMore;
        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTipTitle = itemView.findViewById(R.id.tvTipTitle);
            tvTipDescription = itemView.findViewById(R.id.tvTipDescription);
            tvDateCreated = itemView.findViewById(R.id.tvDateCreated);
            tvSeeMore= itemView.findViewById(R.id.tvSeeMore);
            recyclerView = itemView.findViewById(R.id.recyclerView);

            setIsRecyclable(false);
        }
    }

}
