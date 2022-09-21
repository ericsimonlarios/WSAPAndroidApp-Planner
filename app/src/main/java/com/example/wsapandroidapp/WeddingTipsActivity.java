package com.example.wsapandroidapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.wsapandroidapp.Adapters.WeddingTipsAdapter;
import com.example.wsapandroidapp.Classes.ComponentManager;
import com.example.wsapandroidapp.Classes.Enums;
import com.example.wsapandroidapp.DataModel.WeddingTips;
import com.example.wsapandroidapp.DialogClasses.MessageDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WeddingTipsActivity extends AppCompatActivity {

    EditText etSearch;
    TextView tvMessage, wedTipsTitle;
    RecyclerView recyclerView;

    Context context;
    MessageDialog messageDialog;

    ComponentManager componentManager;
    FirebaseDatabase firebaseDatabase;

    Query weddingTipsQuery;
    boolean isListening;

    List<WeddingTips> weddingTips = new ArrayList<>(), weddingTipsCopy = new ArrayList<>();
    List tipsImagesArrayList = new ArrayList<>();
    WeddingTipsAdapter weddingTipsAdapter;
    String searchWeddingTips = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_tips);

        etSearch = findViewById(R.id.etSearch);
        wedTipsTitle = findViewById(R.id.wedTipsTitle);
        tvMessage = findViewById(R.id.tvMessage);
        recyclerView = findViewById(R.id.recyclerView);

        context = WeddingTipsActivity.this;
        messageDialog = new MessageDialog(context);
        firebaseDatabase = FirebaseDatabase.getInstance(getString(R.string.firebase_RTDB_url));
        weddingTipsQuery = firebaseDatabase.getReference("weddingTips");


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
        weddingTipsAdapter.notifyDataSetChanged();

    }
    private ValueEventListener getWeddingTips() {
        return new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tipsImagesArrayList = new ArrayList();
                if (isListening) {
                    weddingTips.clear();
                    if (snapshot.exists())
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            WeddingTips weddingTip = dataSnapshot.getValue(WeddingTips.class);
                            if (weddingTip != null)
                                weddingTips.add(weddingTip);
                        }

                    //weddingTips.sort(Comparator.comparing(WeddingTips::getTopic));
                    weddingTipsCopy = new ArrayList<>(weddingTips);

                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                weddingTipsAdapter = new WeddingTipsAdapter(context, weddingTips);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(weddingTipsAdapter);

                weddingTipsAdapter.setAdapterListener(weddingTips -> {
                    Intent intent = new Intent(context, WeddingTipsDetailsActivity.class);
                    intent.putExtra("weddingTipsId", weddingTips.getId());
                    context.startActivity(intent);
                });
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