package com.example.wsapandroidapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wsapandroidapp.Adapters.AdminWeddingTipsAdapter;
import com.example.wsapandroidapp.Classes.ComponentManager;
import com.example.wsapandroidapp.Classes.Enums;
import com.example.wsapandroidapp.DataModel.TipsImages;
import com.example.wsapandroidapp.DataModel.WeddingTips;
import com.example.wsapandroidapp.DialogClasses.ConfirmationDialog;
import com.example.wsapandroidapp.DialogClasses.MessageDialog;
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
    ConfirmationDialog confirmationDialog;

    ComponentManager componentManager;
    FirebaseDatabase firebaseDatabase;

    Query weddingTipsQuery;
    boolean isListening;

    List<WeddingTips> weddingTips = new ArrayList<>(), weddingTipsCopy = new ArrayList<>();
    List<String> tipsImgList = new ArrayList<>();
    List tipsImagesArrayList = new ArrayList<>();
    List tipsImagesList = new ArrayList<>();


    AdminWeddingTipsAdapter adminWeddingTipsAdapter;
    String searchWeddingTips = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_wedding_tips);

        etSearch = findViewById(R.id.etSearch);
        wedTipsTitle = findViewById(R.id.wedTipsTitle);
        tvMessage = findViewById(R.id.tvMessage);
        imgAdd = findViewById(R.id.imgAdd);
        recyclerView = findViewById(R.id.recyclerView);

        context = AdminWeddingTipsActivity.this;
        messageDialog = new MessageDialog(context);

        confirmationDialog = new ConfirmationDialog(context);

        firebaseDatabase = FirebaseDatabase.getInstance(getString(R.string.firebase_RTDB_url));
        weddingTipsQuery = firebaseDatabase.getReference("weddingTips");

        imgAdd.setOnClickListener(view -> {
            Intent intent = new Intent(this, WeddingTipsFormActivity.class);
            startActivity(intent);
            finish();
        });

        isListening = true;
        weddingTipsQuery.addValueEventListener(getWeddingTips());

        componentManager = new ComponentManager(context);
        componentManager.setInputRightDrawable(etSearch, true, Enums.VOICE_RECOGNITION);
        componentManager.setVoiceRecognitionListener(() -> startActivityForResult(componentManager.voiceRecognitionIntent(), Enums.VOICE_RECOGNITION_REQUEST_CODE));
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchWeddingTips = editable != null ? editable.toString() : "";

                filterWeddingTips();
            }
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    private void filterWeddingTips() {
        List<WeddingTips> weddingTipsTemp = new ArrayList<>(weddingTipsCopy);

        weddingTips.clear();

        for (int i = 0; i < weddingTipsTemp.size(); i++) {
            WeddingTips weddingTip = weddingTipsTemp.get(i);

            boolean isTopic = searchWeddingTips.trim().length() == 0 ||
                    weddingTip.getTopic().toLowerCase().contains(searchWeddingTips.toLowerCase());

            boolean isDescription = searchWeddingTips.trim().length() == 0 ||
                    weddingTip.getDescription().toLowerCase().contains(searchWeddingTips.toLowerCase());

            boolean isTips = searchWeddingTips.trim().length() == 0 ||
                    weddingTip.getTips().toLowerCase().contains(searchWeddingTips.toLowerCase());
            if (isTopic || isDescription || isTips) weddingTips.add(weddingTip);
        }

        if (weddingTips.size() == 0) {
            tvMessage.setVisibility(View.VISIBLE);

            tvMessage.setText(getString(R.string.no_record, "Record"));
        } else tvMessage.setVisibility(View.GONE);
        tvMessage.bringToFront();

        etSearch.setVisibility(View.VISIBLE);
        adminWeddingTipsAdapter.notifyDataSetChanged();

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
                            weddingTipsCopy = new ArrayList<>(weddingTips);
                            tipsImagesList.add(tipsImagesArrayList);
                        }

                }

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                adminWeddingTipsAdapter = new AdminWeddingTipsAdapter(context, weddingTips, tipsImagesList);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adminWeddingTipsAdapter);
                filterWeddingTips();
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Enums.VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            assert data != null;
            etSearch.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));

            filterWeddingTips();
        }
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