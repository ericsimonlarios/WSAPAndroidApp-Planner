package com.example.wsapandroidapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.wsapandroidapp.Adapters.WeddingTipsChildAdapter;
import com.example.wsapandroidapp.DataModel.WeddingTips;
import com.example.wsapandroidapp.WeddingTipsActivity;
import com.example.wsapandroidapp.R;
import com.example.wsapandroidapp.WeddingTipsDetailsActivity;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WeddingTipsAdapter extends RecyclerView.Adapter<WeddingTipsAdapter.ViewHolder> {


    private final List<WeddingTips> weddingTips;

    private final LayoutInflater layoutInflater;

    private final Context context;

    public WeddingTipsAdapter(Context context, List<WeddingTips> weddingTips) {
        this.weddingTips = weddingTips;
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
                tvDateCreated = holder.tvDateCreated,
                tvSeeMore = holder.tvSeeMore;

        WeddingTips weddingTip = weddingTips.get(position);
        tvTipTitle.setText(weddingTip.getTopic());
        tvTipDescription.setText(weddingTip.getDescription());
        tvDateCreated.setText(weddingTip.getDateCreated());

        tvSeeMore.setOnClickListener(view -> {
            Intent intent = new Intent(context, WeddingTipsDetailsActivity.class);
            intent.putExtra("weddingTipsId", weddingTip.getId());
            context.startActivity(intent);
        });

        List image = new ArrayList(); //placeholder
        image.add(R.drawable.expos);
        image.add(R.drawable.guests);
        image.add(R.drawable.exhibitors);

        //nested recycler view
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        holder.childRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        WeddingTipsChildAdapter  weddingTipsChildAdapter = new WeddingTipsChildAdapter(context, image);
        holder.childRecyclerView.setAdapter(weddingTipsChildAdapter);


    }
    @Override
    public int getItemCount() {
         return weddingTips.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTipTitle, tvTipDescription, tvDateCreated, tvSeeMore;
        RecyclerView childRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTipTitle = itemView.findViewById(R.id.tvTipTitle);
            tvTipDescription = itemView.findViewById(R.id.tvTipDescription);
            tvDateCreated = itemView.findViewById(R.id.tvDateCreated);
            tvSeeMore= itemView.findViewById(R.id.tvSeeMore);
            childRecyclerView = itemView.findViewById(R.id.child_recyclerView);

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
