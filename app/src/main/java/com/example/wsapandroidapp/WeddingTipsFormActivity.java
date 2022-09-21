package com.example.wsapandroidapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.example.wsapandroidapp.Adapters.AdminWeddingTipsAdapter;
import com.example.wsapandroidapp.Adapters.ImgArrayAdapter;
import com.example.wsapandroidapp.Classes.ComponentManager;
import com.example.wsapandroidapp.Classes.Credentials;
import com.example.wsapandroidapp.Classes.DateTime;
import com.example.wsapandroidapp.Classes.Enums;
import com.example.wsapandroidapp.DataModel.TipsImages;
import com.example.wsapandroidapp.DataModel.WeddingTips;
import com.example.wsapandroidapp.DialogClasses.LoadingDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeddingTipsFormActivity extends AppCompatActivity {

    private RecyclerView imgIcon;
    private EditText etTopic,etAuthor, etDescription, etTips;
    private TextView tvMessageTitle, tvImageError, tvTopicError, tvDescError, tvTipsError;
    private Button btnSubmit;
    Context context;

    private boolean isUpdateMode;

    private LoadingDialog loadingDialog;
    private ComponentManager componentManager;

    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    Query weddingTipsQuery;
    boolean isListening;
    WeddingTips weddingTip;

    String selectedWeddingTipsId = "";
    String image = "";
    int getPos;
    ImgArrayAdapter imgArrayAdapter;

    private String topicLabel, author, description, tips;
    private List<Uri> imgArray = new ArrayList<>();
    private List<Uri> imgArrayUpdate = new ArrayList<>();
    private List<String> imgArrayUpdate2 = new ArrayList<>();
    private List<String> imgArrayUpdated = new ArrayList<>();
    private List<Uri> tipsImagesArrayList = new ArrayList<>();
    private List<String> imgUriArray = new ArrayList<>();
    private List<TipsImages> tipsImages = new ArrayList<>();
    private Map<String, Object> map;
    private int counter;
    private int counterUpdate;
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
        tvDescError = findViewById(R.id.tvDescError);
        tvTipsError = findViewById(R.id.tvTipsError);
        loadingDialog = new LoadingDialog(this);
        componentManager = new ComponentManager(this);

        context = WeddingTipsFormActivity.this;

        selectedWeddingTipsId = getIntent().getStringExtra("weddingTipsId");
        image = getIntent().getStringExtra("image");
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
        tvMessageTitle.setText(this.getString(R.string.add_record, "Wedding Tips"));
        if(selectedWeddingTipsId != null){
            isUpdateMode = true;
            initDatabaseQuery();
            tvMessageTitle.setText(this.getString(R.string.update_record, "Wedding Tips"));
            btnSubmit.setText(context.getString(R.string.update));
        }
        else{
            isUpdateMode = false;
        }


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

                componentManager.setInputRightDrawable(etTopic, !Credentials.isEmpty(topicLabel), Enums.CLEAR_TEXT);
                checkLabel(topicLabel, true, WeddingTipsFormActivity.this.getString(R.string.weddingTips_label), tvTopicError, etTopic);
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
                componentManager.setInputRightDrawable(etDescription, !Credentials.isEmpty(description), Enums.CLEAR_TEXT);
                checkLabel(description, true, WeddingTipsFormActivity.this.getString(R.string.weddingTips_label), tvDescError, etDescription);
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
                componentManager.setInputRightDrawable(etTips, !Credentials.isEmpty(tips), Enums.CLEAR_TEXT);
                checkLabel(tips, true, WeddingTipsFormActivity.this.getString(R.string.weddingTips_label), tvTipsError, etTips);
            }
        });

        imgClose.setOnClickListener(view ->{
           newIntent();
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
            checkLabel(topicLabel, true, WeddingTipsFormActivity.this.getString(R.string.weddingTips_label), tvTopicError, etTopic);
            checkDesc(description, true, WeddingTipsFormActivity.this.getString(R.string.weddingTips_label), tvDescError, etDescription);
            checkTips(tips, true, WeddingTipsFormActivity.this.getString(R.string.weddingTips_label), tvTipsError, etTips);
            checkImage();
            if(isUpdateMode)
            {
                if (componentManager.isNoInputError() && tipsImagesArrayList.size() != 0){
                    submit();
                }
            }
            else
            {
                if (componentManager.isNoInputError() && imgArray.size() != 0){
                    submit();
                }
            }

        });
    }

    private void openStorage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, Enums.PICK_IMAGE_REQUEST_CODE);
    }

    public void setUpdateMode(boolean isUpdateMode) {
        this.isUpdateMode = isUpdateMode;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(isUpdateMode) {
            if (requestCode == Enums.PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK &&
                    data != null && data.getData() != null) {
                imgArrayUpdate.add(data.getData());
                tipsImagesArrayList.add(data.getData());
                callImgAdapter(tipsImagesArrayList, isUpdateMode);
            }

        }
        else {
            if (requestCode == Enums.PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK &&
                    data != null && data.getData() != null)
            {
                imgArray.add(data.getData());
                callImgAdapter(imgArray, isUpdateMode);
            }
        }
    }

    public void callImgAdapter(List<Uri> imgList, boolean isUpdateMode){
        if(isUpdateMode){
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            imgIcon.setLayoutManager(linearLayoutManager);
            ImgArrayAdapter imgArrayAdapter = new ImgArrayAdapter(context,isUpdateMode, imgList);
            imgIcon.setAdapter(imgArrayAdapter);
            imgArrayAdapter.setAdapterListener(new ImgArrayAdapter.AdapterListener() {
                @Override
                public void passImg(int posImg) {
                    getPos = posImg;

                    imgArrayUpdate2.remove(getPos);
                }
            });
        }
        else{
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            imgIcon.setLayoutManager(linearLayoutManager);
            ImgArrayAdapter imgArrayAdapter = new ImgArrayAdapter(context,isUpdateMode, imgList);
            imgIcon.setAdapter(imgArrayAdapter);
        }
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
                    this.getString(R.string.required_input_error, fieldName),
                    targetEditText);
        else if (!Credentials.isValidLength(string, Credentials.REQUIRED_LABEL_LENGTH, 0))
            componentManager.showInputError(targetTextView,
                    this.getString(R.string.length_error, fieldName, Credentials.REQUIRED_LABEL_LENGTH),
                    targetEditText);
    }

    private void checkDesc(String string, boolean isRequired, String fieldName,
                            TextView targetTextView, EditText targetEditText) {
        componentManager.hideInputError(targetTextView, targetEditText);

        if (Credentials.isEmpty(string) && isRequired)
            componentManager.showInputError(targetTextView,
                    this.getString(R.string.required_input_error, "Description"),
                    targetEditText);
        else if (!Credentials.isValidLength(string, Credentials.REQUIRED_LABEL_LENGTH, 0))
            componentManager.showInputError(targetTextView,
                    this.getString(R.string.length_error, fieldName, Credentials.REQUIRED_LABEL_LENGTH),
                    targetEditText);
    }

    private void checkTips(String string, boolean isRequired, String fieldName,
                           TextView targetTextView, EditText targetEditText) {
        componentManager.hideInputError(targetTextView, targetEditText);

        if (Credentials.isEmpty(string) && isRequired)
            componentManager.showInputError(targetTextView,
                    this.getString(R.string.required_input_error, "Tips"),
                    targetEditText);
        else if (!Credentials.isValidLength(string, Credentials.REQUIRED_LABEL_LENGTH, 0))
            componentManager.showInputError(targetTextView,
                    this.getString(R.string.length_error, fieldName, Credentials.REQUIRED_LABEL_LENGTH),
                    targetEditText);
    }
    private void checkImage() {
        componentManager.hideInputError(tvImageError);
        if(isUpdateMode){
            if(imgArrayUpdate.size() != 0 )
            {
                if (tipsImagesArrayList.size() == 0)
                    componentManager.showInputError(tvImageError,
                            WeddingTipsFormActivity.this.getString(R.string.required_input_error, "Image"));
            }

        }
        else{
            if (imgArray.size() == 0)
                componentManager.showInputError(tvImageError,
                        WeddingTipsFormActivity.this.getString(R.string.required_input_error, "Image"));
        }

    }

    private void submit() {
        if(isUpdateMode)
        {
            loadingDialog.showDialog();
            imgUriArray = new ArrayList<>();
            tipsImages = new ArrayList<>();
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference();
            map = new HashMap<>();
            storageReference = FirebaseStorage.getInstance().getReference("weddingTips");
            counter = 0;
            counterUpdate = 0;
            isCompleted = false;
            DateTime date = new DateTime();
            String weddingTipsKey = selectedWeddingTipsId;
            WeddingTips weddingTips = new WeddingTips(weddingTipsKey, topicLabel,
                    description ,tips, author, date.getDateText());
            if(imgArrayUpdate.size() == 0)
            {   databaseReference.child("weddingTips").child(weddingTipsKey).setValue(weddingTips);
                for (String string: imgArrayUpdate2)
                {
                    counterUpdate++;
                    String imageKey2 = databaseReference.child("weddingTips").push().getKey();
                    map.put(imageKey2, string);
                    databaseReference.child("weddingTips").child(weddingTipsKey).child("image").updateChildren(map);
                    if (counterUpdate == imgArrayUpdate2.size()){
                        isCompleted = true;
                    }
                    if(isCompleted) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(WeddingTipsFormActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        newIntent();

                    }
                }
            }
            else{
                for(Uri uri: imgArrayUpdate){
                    StorageReference fileRef = storageReference.child(weddingTipsKey).child(System.currentTimeMillis() + "." + getFileExtension(uri));
                    fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri1) {
                                            counter++;
                                            String imageKey = databaseReference.child("weddingTips").push().getKey();
                                            map.put(imageKey, uri1.toString());
                                            if(counter == imgArrayUpdate.size()){
                                                for (String string: imgArrayUpdate2)
                                                {
                                                    counterUpdate++;
                                                    String imageKey2 = databaseReference.child("weddingTips").push().getKey();
                                                    map.put(imageKey2, string);
                                                    if (counterUpdate == imgArrayUpdate2                           .size()){
                                                        databaseReference.child("weddingTips").child(weddingTipsKey).setValue(weddingTips);
                                                        databaseReference.child("weddingTips").child(weddingTipsKey).child("image").updateChildren(map);
                                                        isCompleted = true;
                                                    }
                                                    if(isCompleted) {
                                                        loadingDialog.dismissDialog();
                                                        Toast.makeText(WeddingTipsFormActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                                        newIntent();
                                                    }
                                                }
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
                                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

        }
        else{
            loadingDialog.showDialog();
            imgUriArray = new ArrayList<>();
            tipsImages = new ArrayList<>();
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference();
            map = new HashMap<>();
            storageReference = FirebaseStorage.getInstance().getReference("weddingTips");
            counter = 0;
            isCompleted = false;
            DateTime date = new DateTime();
            String weddingTipsKey = databaseReference.child("weddingTips").push().getKey();
            WeddingTips weddingTips = new WeddingTips(weddingTipsKey, topicLabel,
                    description ,tips, author, date.getDateText());
            for(Uri uri: imgArray){
                StorageReference fileRef = storageReference.child(weddingTipsKey).child(System.currentTimeMillis() + "." + getFileExtension(uri));
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
                                            finish();
                                            startActivity(intent);
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
                            tipsImagesArrayList = new ArrayList();
                            for (DataSnapshot imgSnapshot : dataSnapshot.child("image").getChildren()) {
                                imgArrayUpdate2.add(imgSnapshot.getValue().toString());
                                Uri myUri = Uri.parse(imgSnapshot.getValue().toString());
                                tipsImagesArrayList.add(myUri);

                            }
                        }
                }
                etTopic.setText(weddingTip.getTopic());
                etDescription.setText(weddingTip.getDescription());
                etTips.setText(weddingTip.getTips());
                etAuthor.setText(weddingTip.getAuthor());

                callImgAdapter(tipsImagesArrayList, isUpdateMode);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG: " + context.getClass(), "onCancelled", error.toException());
            }
        };
    }

    @SuppressLint("NotifyDataSetChanged")
    public void newIntent()
    {
        Intent intent = new Intent(WeddingTipsFormActivity.this, AdminWeddingTipsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

}
