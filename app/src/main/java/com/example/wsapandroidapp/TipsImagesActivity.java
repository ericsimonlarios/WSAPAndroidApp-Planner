package com.example.wsapandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Context;
import android.os.Bundle;

import com.example.wsapandroidapp.Adapters.TipsImagesActivityAdapter;
import com.example.wsapandroidapp.DataModel.WeddingTips;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class TipsImagesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Context context;
    FirebaseDatabase firebaseDatabase;
    Query tipsImagesQuery;
    TipsImagesActivityAdapter tipsImagesActivityAdapter;
    WeddingTips weddingTip;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips_images);

        recyclerView = findViewById(R.id.tipsImagesRV);

        firebaseDatabase = FirebaseDatabase.getInstance();
        tipsImagesQuery = firebaseDatabase.getReference("weddingTips");
        List<String> tipsImagesArrayList = new ArrayList<>();

        context = TipsImagesActivity.this;
        String image = getIntent().getStringExtra("image");

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        tipsImagesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        dataSnapshot.getKey();
                        if(dataSnapshot.getKey().equals(image))
                            for (DataSnapshot imgSnapshot : dataSnapshot.child("image").getChildren()) {
                                tipsImagesArrayList.add(imgSnapshot.getValue().toString());
                            }
                    }
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    TipsImagesActivityAdapter tipsImagesActivityAdapter = new TipsImagesActivityAdapter(context, tipsImagesArrayList);
                    recyclerView.setAdapter(tipsImagesActivityAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}