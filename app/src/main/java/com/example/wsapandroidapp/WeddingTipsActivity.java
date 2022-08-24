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
import android.widget.EditText;
import android.widget.TextView;


import com.example.wsapandroidapp.Adapters.WeddingTipsAdapter;
import com.example.wsapandroidapp.Classes.ComponentManager;
import com.example.wsapandroidapp.Classes.Enums;
import com.example.wsapandroidapp.Classes.Units;
import com.example.wsapandroidapp.DataModel.Application;
import com.example.wsapandroidapp.DataModel.WeddingTips;
import com.example.wsapandroidapp.DialogClasses.AppStatusPromptDialog;
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

public class WeddingTipsActivity extends AppCompatActivity {

    EditText etSearch;
    TextView tvMessage, wedTipsTitle;
    RecyclerView recyclerView;

    Context context;


    MessageDialog messageDialog;

    FirebaseDatabase firebaseDatabase;

    //Query weddingTipsQuery;
    boolean isListening;

    //List<WeddingTips> weddingTips = new ArrayList<>(), weddingTipsCopy = new ArrayList<>();
    WeddingTipsAdapter weddingTipsAdapter;

    AppStatusPromptDialog appStatusPromptDialog;
    NewVersionPromptDialog newVersionPromptDialog;
    Query applicationQuery;
    Application application = new Application();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_tips);
        List list = new ArrayList(); //placeholder
        list.add("Test title");
        list.add("Test title2");
        List list2 = new ArrayList(); //placeholder
        list2.add("Test description");
        list2.add("Test description2");

        etSearch = findViewById(R.id.etSearch);
        wedTipsTitle = findViewById(R.id.wedTipsTitle);
        tvMessage = findViewById(R.id.tvMessage);
        recyclerView = findViewById(R.id.recyclerView);

        context = WeddingTipsActivity.this;

        messageDialog = new MessageDialog(context);
        appStatusPromptDialog = new AppStatusPromptDialog(context);
        newVersionPromptDialog = new NewVersionPromptDialog(context);

        firebaseDatabase = FirebaseDatabase.getInstance(getString(R.string.firebase_RTDB_url));
        //weddingTipsQuery = firebaseDatabase.getReference("weddingTips");
        //applicationQuery = firebaseDatabase.getReference("application");
        isListening = true;
        //weddingTipsQuery.addValueEventListener(getWeddingTips());
        //applicationQuery.addValueEventListener(getApplicationValue());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        //weddingTipsAdapter = new WeddingTipsAdapter(context, weddingTips);
        weddingTipsAdapter = new WeddingTipsAdapter(context, list, list2);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(weddingTipsAdapter);



    }
}