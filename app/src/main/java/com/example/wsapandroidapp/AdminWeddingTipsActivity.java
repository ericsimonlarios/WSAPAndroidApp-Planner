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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wsapandroidapp.Adapters.AdminWeddingTipsAdapter;
import com.example.wsapandroidapp.Classes.Enums;
import com.example.wsapandroidapp.DataModel.TipsImages;
import com.example.wsapandroidapp.DataModel.WeddingTips;
import com.example.wsapandroidapp.DialogClasses.ConfirmationDialog;
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
    ConfirmationDialog confirmationDialog;
    WeddingTipsFormDialog weddingTipsFormDialog;

    FirebaseDatabase firebaseDatabase;

    Query weddingTipsQuery;
    boolean isListening;

    List<WeddingTips> weddingTips = new ArrayList<>();
    List<Uri> images = new ArrayList<>();
    List tipsImagesArrayList = new ArrayList<>();
    List tipsImagesList = new ArrayList<>();


   WeddingTips selectedWeddingTips = new WeddingTips();
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

        confirmationDialog = new ConfirmationDialog(context);

        confirmationDialog.setDialogListener(() -> {
            weddingTipsFormDialog.deleteWeddingTips(selectedWeddingTips);
            confirmationDialog.dismissDialog();
        });

        weddingTipsFormDialog = new WeddingTipsFormDialog(context);

        firebaseDatabase = FirebaseDatabase.getInstance(getString(R.string.firebase_RTDB_url));
        weddingTipsQuery = firebaseDatabase.getReference("weddingTips");

        imgAdd.setOnClickListener(view -> {
           images.clear();
           weddingTipsFormDialog.showDialog();
        });

        weddingTipsFormDialog.setDialogListener(() -> {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED
            ) openStorage();
            else {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Enums.GENERAL_REQUEST_CODE);
            }
        });


        isListening = true;
        weddingTipsQuery.addValueEventListener(getWeddingTips());
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

                adminWeddingTipsAdapter.setAdapterListener(new AdminWeddingTipsAdapter.AdapterListener() {
                    @Override
                    public void onEdit(WeddingTips weddingTips) {
                        // weddingTipsFormDialog.setUpdateMode(true);
                        weddingTipsFormDialog.setWeddingTips(weddingTips);
                        weddingTipsFormDialog.showDialog();
                        Toast.makeText(context, weddingTips.getId(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onDelete(WeddingTips weddingTips) {
                        selectedWeddingTips = weddingTips;
                        confirmationDialog.setMessage(getString(R.string.confirmation_prompt, "delete the topic"));
                        confirmationDialog.showDialog();
                        Toast.makeText(context, weddingTips.getId(), Toast.LENGTH_SHORT).show();
                    }
                });
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
    @SuppressWarnings("deprecation")
    private void openStorage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, Enums.PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Enums.VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            assert data != null;
            etSearch.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));

//            filterExhibitors();
        } else if (requestCode == Enums.PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            images.add(data.getData());
            weddingTipsFormDialog.getAdapter(images);
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