package com.example.wsapandroidapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.example.wsapandroidapp.Adapters.WeddingTipsDetailsAdapter;
import com.example.wsapandroidapp.Classes.ComponentManager;
import com.example.wsapandroidapp.Classes.Enums;
import com.example.wsapandroidapp.DataModel.Application;
import com.example.wsapandroidapp.DataModel.Supplier;
import com.example.wsapandroidapp.DataModel.WeddingTips;
import com.example.wsapandroidapp.DialogClasses.AppStatusPromptDialog;
import com.example.wsapandroidapp.DialogClasses.LoadingDialog;
import com.example.wsapandroidapp.DialogClasses.MessageDialog;
import com.example.wsapandroidapp.DialogClasses.NewVersionPromptDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


public class WeddingTipsDetailsActivity extends AppCompatActivity {


    TextView tvTipTitle, tvDateCreated, tvTips, tvTipDescription;
    ImageView imgIntro;
    RecyclerView recyclerView;
    Context context;

    String selectedWeddingTipsId = "";

    MessageDialog messageDialog;

    FirebaseDatabase firebaseDatabase;

    Query weddingTipsQuery;
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
        recyclerView = findViewById(R.id.recyclerView2);

        selectedWeddingTipsId = getIntent().getStringExtra("weddingTipsId");

        initDatabaseQuery();

        context = WeddingTipsDetailsActivity.this;
        messageDialog = new MessageDialog(context);
//

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
                if (isListening) {
                    if (snapshot.exists())
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            weddingTip = dataSnapshot.getValue(WeddingTips.class);
                            break;
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
        tvTipDescription.setText(weddingTip.getDescription());
        tvDateCreated.setText(weddingTip.getDateCreated());

        if(weddingTip.getTips().contains("_b")){
            String newTips = weddingTip.getTips().replace("_b","\n\n\n");
            tvTips.setText(newTips);
        }
        //placeholder for image
        List image = new ArrayList();
        image.add(R.drawable.expos);
        image.add(R.drawable.guests);
        image.add(R.drawable.exhibitors);
        image.add(R.drawable.ic_wsap);

        //nested recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        WeddingTipsDetailsAdapter  weddingTipsDetailsAdapter = new WeddingTipsDetailsAdapter(context, image);
        recyclerView.setAdapter(weddingTipsDetailsAdapter);

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