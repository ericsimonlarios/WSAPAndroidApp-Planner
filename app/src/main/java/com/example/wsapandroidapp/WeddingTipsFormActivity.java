package com.example.wsapandroidapp;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wsapandroidapp.Adapters.ImgArrayAdapter;
import com.example.wsapandroidapp.Classes.ComponentManager;
import com.example.wsapandroidapp.Classes.Credentials;
import com.example.wsapandroidapp.Classes.DateTime;
import com.example.wsapandroidapp.Classes.Enums;
import com.example.wsapandroidapp.Classes.Theme;
import com.example.wsapandroidapp.DataModel.TipsImages;
import com.example.wsapandroidapp.DataModel.WeddingTips;
import com.example.wsapandroidapp.DialogClasses.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeddingTipsFormActivity extends AppCompatActivity {

    private RecyclerView imgIcon;
    private EditText etTopic,etAuthor, etDescription, etTips;
    private TextView tvMessageTitle, tvImageError, tvTopicError;
    private Button btnSubmit;

    private boolean isImageChanged = false;

    private LoadingDialog loadingDialog;
    private ComponentManager componentManager;

    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private String topicLabel, author, description, tips;
    private List<Uri> imgArray = new ArrayList<>();
    private List<String> imgUriArray = new ArrayList<>();
    private List<TipsImages> tipsImages = new ArrayList<>();
    private Map<String, Object> map;
    private int counter;
    private boolean isCompleted;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_wedding_tips_form_layout);

        ImageView imgClose = findViewById(R.id.imgClose);
        tvMessageTitle = findViewById(R.id.tvMessageTitle);
        imgIcon = findViewById(R.id.imgIcon);
        tvImageError = findViewById(R.id.tvImageError);

        loadingDialog = new LoadingDialog(this);
        componentManager = new ComponentManager(this);

        etTopic = findViewById(R.id.etTopic);
        etAuthor = findViewById((R.id.etAuthor));
        etDescription = findViewById(R.id.etDescription);
        etTips = findViewById((R.id.etTips));
        tvTopicError = findViewById(R.id.tvTopicError);

        List<TextView> errorTextViewList =
                Collections.singletonList(tvTopicError);
        List<EditText> errorEditTextList =
                Collections.singletonList(etTopic);

        componentManager.initializeErrorComponents(errorTextViewList, errorEditTextList);

        Button btnChooseImage = findViewById(R.id.btnChooseImage);
        btnSubmit = findViewById(R.id.btnSubmit);

        etTopic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                topicLabel = editable != null ? editable.toString() : "";
            }
        });
        etAuthor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                author = editable != null ? editable.toString() : "";
            }
        });
        etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                description = editable != null ? editable.toString() : "";
            }
        });
        etTips.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tips = editable != null ? editable.toString() : "";
            }
        });

        imgClose.setOnClickListener(view ->{
            Intent intent = new Intent(this, AdminWeddingTipsActivity.class);
            startActivity(intent);
            finish();
        });

        btnChooseImage.setOnClickListener(view ->{
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED
            ){
                openStorage();
            }else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Enums.GENERAL_REQUEST_CODE);
            }
        });

        btnSubmit.setOnClickListener(view -> {
            submit();
        });
    }

    private void openStorage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, Enums.PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Enums.PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            imgArray.add(data.getData());
            callImgAdapter(imgArray);
        }
    }

    public void callImgAdapter(List<Uri> imgList){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        imgIcon.setLayoutManager(linearLayoutManager);
        ImgArrayAdapter imgArrayAdapter = new ImgArrayAdapter(this, imgList);
        imgIcon.setAdapter(imgArrayAdapter);
    }

    public String getFileExtension(Uri uri){
        ContentResolver cr = this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void checkLabel(String string, boolean isRequired, String fieldName,
                            TextView targetTextView, EditText targetEditText) {
        componentManager.hideInputError(targetTextView, targetEditText);

        if (Credentials.isEmpty(string) && isRequired)
            componentManager.showInputError(targetTextView,
                    context.getString(R.string.required_input_error, fieldName),
                    targetEditText);
        else if (!Credentials.isValidLength(string, Credentials.REQUIRED_LABEL_LENGTH, 0))
            componentManager.showInputError(targetTextView,
                    context.getString(R.string.length_error, fieldName, Credentials.REQUIRED_LABEL_LENGTH),
                    targetEditText);
    }

    private void submit() {
        loadingDialog.showDialog();
        imgUriArray = new ArrayList<>();
        tipsImages = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        String weddingTipsKey = databaseReference.child("weddingTips").push().getKey();
        map = new HashMap<>();
        storageReference = FirebaseStorage.getInstance().getReference("weddingTips");
        counter = 0;
        isCompleted = false;
        DateTime date = new DateTime();
        WeddingTips weddingTips = new WeddingTips(weddingTipsKey, topicLabel,
                description ,tips, author, date.getDateText());

        for(Uri uri: imgArray){
            StorageReference fileRef = storageReference.child(weddingTipsKey).child(System.currentTimeMillis() + '.' + getFileExtension(uri));
            fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri1) {
                            counter++;
                            String imageKey = databaseReference.child("weddingTips").push().getKey();
                            map.put(imageKey, uri1.toString());
                            databaseReference.child("weddingTips").child(weddingTipsKey).setValue(weddingTips);
                            databaseReference.child("weddingTips").child(weddingTipsKey).child("image").updateChildren(map);
                            if(counter == imgArray.size()){
                                isCompleted = true;
                            }
                            if(isCompleted) {
                                loadingDialog.dismissDialog();
                                Toast.makeText(WeddingTipsFormActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(WeddingTipsFormActivity.this, AdminWeddingTipsActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }
}
