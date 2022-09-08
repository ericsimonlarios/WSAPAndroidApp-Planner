package com.example.wsapandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wsapandroidapp.Adapters.AdminWeddingTipsAdapter;
import com.example.wsapandroidapp.Adapters.WeddingTipsAdapter;
import com.example.wsapandroidapp.Classes.Enums;
import com.example.wsapandroidapp.DataModel.TipsImages;
import com.example.wsapandroidapp.DataModel.WeddingTips;
import com.example.wsapandroidapp.DialogClasses.ExhibitorFormDialog;
import com.example.wsapandroidapp.DialogClasses.MessageDialog;
import com.example.wsapandroidapp.DialogClasses.WeddingTipsFormDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminWeddingTipsActivity extends AppCompatActivity {

    EditText etSearch;
    TextView tvMessage, wedTipsTitle;
    RecyclerView recyclerView;
    ImageView imgAdd;

    Context context;
    MessageDialog messageDialog;
    WeddingTipsFormDialog weddingTipsFormDialog;

    FirebaseDatabase firebaseDatabase;

    Query weddingTipsQuery;
    boolean isListening;

    List<WeddingTips> weddingTips = new ArrayList<>();
    List tipsImagesArrayList = new ArrayList<>();
    List tipsImagesList = new ArrayList<>();
    AdminWeddingTipsAdapter adminWeddingTipsAdapter;

    TipsImages tipsImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_wedding_tips);

        etSearch = findViewById(R.id.etSearch);
        etSearch.setVisibility(View.GONE);
        wedTipsTitle = findViewById(R.id.wedTipsTitle);
        tvMessage = findViewById(R.id.tvMessage);
        imgAdd = findViewById(R.id.imgAdd);
        recyclerView = findViewById(R.id.recyclerView);


        context = AdminWeddingTipsActivity.this;
        messageDialog = new MessageDialog(context);
        weddingTipsFormDialog = new WeddingTipsFormDialog(context);

        firebaseDatabase = FirebaseDatabase.getInstance(getString(R.string.firebase_RTDB_url));
        weddingTipsQuery = firebaseDatabase.getReference("weddingTips");

        imgAdd.setOnClickListener(view -> {
            Intent intent = new Intent(context, WeddingTipsFormActivity.class);
            context.startActivity(intent);
        });

        isListening = true;
        weddingTipsQuery.addValueEventListener(getWeddingTips());

//        btnChooseImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent galleryIntent = new Intent();
//                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//                galleryIntent.setType("image/*");
//                startActivityForResult(galleryIntent, 2);
//            }
//        });
    }
    private ValueEventListener getWeddingTips() {
        return new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tipsImagesList = new ArrayList();
                if (isListening) {
                    weddingTips.clear();
                    if (snapshot.exists())
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            tipsImagesArrayList = new ArrayList();
                            WeddingTips weddingTip = dataSnapshot.getValue(WeddingTips.class);
                            if (weddingTip != null)
                                weddingTips.add(weddingTip);
                            tipsImagesArrayList = new ArrayList();
                            for (DataSnapshot imgSnapshot : dataSnapshot.child("image").getChildren()) {
                                tipsImagesArrayList.add(imgSnapshot.getValue().toString());
                            }
                            tipsImagesList.add(tipsImagesArrayList);
                        }
                }

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                adminWeddingTipsAdapter = new AdminWeddingTipsAdapter(context, weddingTips, tipsImagesList);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adminWeddingTipsAdapter);

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