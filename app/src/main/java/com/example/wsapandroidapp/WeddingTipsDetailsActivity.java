package com.example.wsapandroidapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.wsapandroidapp.Adapters.WeddingTipsDetailsAdapter;
import com.example.wsapandroidapp.Classes.Enums;
import com.example.wsapandroidapp.DataModel.TipsImages;
import com.example.wsapandroidapp.DataModel.WeddingTips;
import com.example.wsapandroidapp.DialogClasses.MessageDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import androidx.recyclerview.widget.SnapHelper;
import androidx.recyclerview.widget.LinearSnapHelper;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class WeddingTipsDetailsActivity extends AppCompatActivity {


    TextView tvTipTitle, tvDateCreated, tvTips, tvTipDescription, tvAuthor;
    RecyclerView recyclerView;
    Context context;
    List tipsImagesArrayList = new ArrayList<>();
    private WeddingTipsDetailsAdapter weddingTipsDetailsAdapter;

    String selectedWeddingTipsId = "";

    MessageDialog messageDialog;

    FirebaseDatabase firebaseDatabase;

    Query weddingTipsQuery,ApplicationQuery;
    boolean isListening;


    WeddingTips weddingTip;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_tips_details);

        tvTipTitle = findViewById(R.id.tvTipTitle);
        tvDateCreated = findViewById(R.id.tvDateCreated);
        tvTipDescription = findViewById(R.id.tvTipDescription);
        tvTips = findViewById(R.id.tvTips);
        tvAuthor = findViewById(R.id.tvAuthor);
        recyclerView = findViewById(R.id.recyclerView2);

        selectedWeddingTipsId = getIntent().getStringExtra("weddingTipsId");

        initDatabaseQuery();

        context = WeddingTipsDetailsActivity.this;
        messageDialog = new MessageDialog(context);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }

    private void initDatabaseQuery() {
        firebaseDatabase = FirebaseDatabase.getInstance(getString(R.string.firebase_RTDB_url));
        weddingTipsQuery = firebaseDatabase.getReference("weddingTips").orderByChild("id").equalTo(selectedWeddingTipsId);
        isListening = true;
        weddingTipsQuery.addValueEventListener(getWeddingTips());
    }

    private ValueEventListener getWeddingTips() {
        return new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TipsImages tipsImages = new TipsImages();
                if (isListening) {
                    if (snapshot.exists())
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            weddingTip = dataSnapshot.getValue(WeddingTips.class);
                            tipsImagesArrayList = new ArrayList();
                            for (DataSnapshot imgSnapshot : dataSnapshot.child("image").getChildren()) {
                                tipsImagesArrayList.add(imgSnapshot.getValue().toString());
                            }
                        }
                }
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG: " + context.getClass(), "onCancelled", error.toException());

                messageDialog.setMessage(getString(R.string.fd_on_cancelled, "WeddingTips"));
                messageDialog.setMessageType(Enums.ERROR_MESSAGE);
                messageDialog.showDialog();
            }
        };
    }


    private void updateUI() {

        tvTipTitle.setText(weddingTip.getTopic());
        tvTipDescription.setText("\t\t\t" + weddingTip.getDescription());
        tvDateCreated.setText(weddingTip.getDateCreated());

        if(weddingTip.getAuthor().equals("")){
            tvAuthor.setText("");
        }
        else {
            tvAuthor.setText("Author:\t" + weddingTip.getAuthor());
        }
        if(weddingTip.getTips().contains("_b")){
            String newTips = weddingTip.getTips().replace("_b","\n\n\n");
            tvTips.setText(newTips);
        }
        else{
            tvTips.setText(weddingTip.getTips());
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        WeddingTipsDetailsAdapter  weddingTipsDetailsAdapter = new WeddingTipsDetailsAdapter(context,selectedWeddingTipsId, tipsImagesArrayList );
        recyclerView.setAdapter(weddingTipsDetailsAdapter);

        final int time_interval = 3000;
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            int count = 0;
            @Override
            public void run() {
                if(count < tipsImagesArrayList.size())
                {
                    recyclerView.scrollToPosition(count++);
                    handler.postDelayed(this,time_interval);
                    if (count==tipsImagesArrayList.size()){
                        count = 0;
                    }
                }
            }
        };
        handler.postDelayed(runnable, time_interval);

    }
    @Override
    public void onResume() {
        isListening = true;
        weddingTipsQuery.addListenerForSingleValueEvent(getWeddingTips());
        super.onResume();
    }

    @Override
    public void onStop() {
        isListening = false;

        super.onStop();
    }

    @Override
    public void onDestroy() {
        isListening = false;

        super.onDestroy();
    }

}