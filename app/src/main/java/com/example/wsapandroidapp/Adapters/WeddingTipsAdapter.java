package com.example.wsapandroidapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.example.wsapandroidapp.DataModel.WeddingTips;
import com.example.wsapandroidapp.R;
import com.example.wsapandroidapp.TipsImagesActivity;
import com.example.wsapandroidapp.WeddingTipsDetailsActivity;
import com.example.wsapandroidapp.DialogClasses.MessageDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WeddingTipsAdapter extends RecyclerView.Adapter<WeddingTipsAdapter.ViewHolder> {


    private final List<WeddingTips> weddingTips;
    private final LayoutInflater layoutInflater;

    private final Context context;

    FirebaseDatabase firebaseDatabase;
    Query tipsImagesQuery;


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
                CardView cardView = holder.cardView;



        firebaseDatabase = FirebaseDatabase.getInstance();
        tipsImagesQuery = firebaseDatabase.getReference("weddingTips");
        List<String> tipsImagesArrayList = new ArrayList<>();

        WeddingTips weddingTip = weddingTips.get(position);
        tvTipTitle.setText(weddingTip.getTopic());
        tvTipDescription.setText("\t\t\t" + weddingTip.getDescription());
        tvDateCreated.setText(weddingTip.getDateCreated());

        tvSeeMore.setOnClickListener(view -> {
            Intent intent = new Intent(context, WeddingTipsDetailsActivity.class);
            intent.putExtra("weddingTipsId", weddingTip.getId());
            context.startActivity(intent);
        });

        cardView.setOnClickListener(view -> {
            Intent intent = new Intent(context, WeddingTipsDetailsActivity.class);
            intent.putExtra("weddingTipsId", weddingTip.getId());
            context.startActivity(intent);
        });


        tipsImagesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        dataSnapshot.getKey();
                        if(dataSnapshot.getKey().equals(weddingTip.getId()))
                            for (DataSnapshot imgSnapshot : dataSnapshot.child("image").getChildren()) {
                               tipsImagesArrayList.add(imgSnapshot.getValue().toString());
                            }
                    }
                    if(tipsImagesArrayList.size() == 1){
                        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                        holder.childRecyclerView.setLayoutManager(staggeredGridLayoutManager);

                    }
                    else if((tipsImagesArrayList.size() == 2)) {
                        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
                        holder.childRecyclerView.setLayoutManager(staggeredGridLayoutManager);
                    }
                    else {
                        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
                        holder.childRecyclerView.setLayoutManager(staggeredGridLayoutManager);
                    }
                    WeddingTipsChildAdapter  weddingTipsChildAdapter = new WeddingTipsChildAdapter(context, weddingTips, tipsImagesArrayList, holder);
                    holder.childRecyclerView.setAdapter(weddingTipsChildAdapter);
                }   holder.childRecyclerView.setNestedScrollingEnabled(false);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return weddingTips.size();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTipTitle, tvTipDescription, tvDateCreated, tvSeeMore;
        RecyclerView childRecyclerView;
        CardView cardView;
        ConstraintLayout childRV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTipTitle = itemView.findViewById(R.id.tvTipTitle);
            tvTipDescription = itemView.findViewById(R.id.tvTipDescription);
            tvDateCreated = itemView.findViewById(R.id.tvDateCreated);
            tvSeeMore= itemView.findViewById(R.id.tvSeeMore);
            childRecyclerView = itemView.findViewById(R.id.child_recyclerView);
            cardView = itemView.findViewById(R.id.cardView4);
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



