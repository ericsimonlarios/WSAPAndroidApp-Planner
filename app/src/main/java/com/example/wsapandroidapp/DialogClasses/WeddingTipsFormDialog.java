package com.example.wsapandroidapp.DialogClasses;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wsapandroidapp.Classes.ComponentManager;
import com.example.wsapandroidapp.Classes.Credentials;
import com.example.wsapandroidapp.Classes.DateTime;
import com.example.wsapandroidapp.Classes.Enums;
import com.example.wsapandroidapp.Adapters.ImgArrayAdapter;
import com.example.wsapandroidapp.DataModel.TipsImages;
import com.example.wsapandroidapp.DataModel.WeddingTips;
import com.example.wsapandroidapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeddingTipsFormDialog {

    private ImageView imgIcon2;
    private RecyclerView imgIcon;
    private EditText etTopic,etAuthor, etDescription, etTips;
    private TextView tvMessageTitle, tvImageError, tvTopicError;
    private Button btnSubmit;

    private final Context context;
    private Dialog dialog;

    private boolean isImageChanged = false;

    private LoadingDialog loadingDialog;
    private MessageDialog messageDialog;

    private ComponentManager componentManager;

    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private  WeddingTips weddingTips;
    private String topicLabel, author, description, tips;
    private List<Uri> imgArray = new ArrayList<>();
    private List<String> imgUriArray = new ArrayList<>();
    private List<TipsImages> tipsImages = new ArrayList<>();
    public WeddingTipsFormDialog(Context context) {
        this.context = context;

        createDialog();
    }

    private void createDialog() {
        setDialog();
        setDialogWindow();
    }

    private DialogListener dialogListener;

    public interface DialogListener{
        void chooseImage();
    }

    public void setDialogListener(DialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    private void setDialog() {
        loadingDialog = new LoadingDialog(context);
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wedding_tips_form_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        ImageView imgClose = dialog.findViewById(R.id.imgClose);
        tvMessageTitle = dialog.findViewById(R.id.tvMessageTitle);
        imgIcon = dialog.findViewById(R.id.imgIcon);
        tvImageError = dialog.findViewById(R.id.tvImageError);

        etTopic = dialog.findViewById(R.id.etTopic);
        etAuthor = dialog.findViewById((R.id.etAuthor));
        etDescription = dialog.findViewById(R.id.etDescription);
        etTips = dialog.findViewById((R.id.etTips));

        tvTopicError = dialog.findViewById(R.id.tvTopicError);
        Button btnChooseImage = dialog.findViewById(R.id.btnChooseImage);
        btnSubmit = dialog.findViewById(R.id.btnSubmit);

        componentManager = new ComponentManager(context);
        btnChooseImage.setOnClickListener(view -> {
            if (dialogListener != null) dialogListener.chooseImage();
        });

        btnSubmit.setOnClickListener(view -> {
            submit();
        });

        imgClose.setOnClickListener(view -> dismissDialog());
        btnChooseImage.setOnClickListener(View -> {
                    dialogListener.chooseImage();
                }
        );

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
    }
        public void getAdapter(List<Uri> uri){
            imgArray = uri;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            imgIcon.setLayoutManager(linearLayoutManager);
            ImgArrayAdapter imgArrayAdapter = new ImgArrayAdapter(context, uri);
            imgIcon.setAdapter(imgArrayAdapter);

        }



        private void setDialogWindow() {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        public void showDialog() {
            dialog.show();
            tvMessageTitle.setText(context.getString(R.string.add_record, "Topic"));
            btnSubmit.setText(context.getString(R.string.add));
            isImageChanged = false;
        }

        public void dismissDialog() {
            dialog.dismiss();
            etTopic.getText().clear();
            etAuthor.getText().clear();
            etDescription.getText().clear();
            etTopic.getText().clear();
        }

        private void submitFailed(String errorMsg) {
            loadingDialog.dismissDialog();

            messageDialog.setMessageType(Enums.ERROR_MESSAGE);
            messageDialog.setMessage(errorMsg);
            messageDialog.showDialog();
        }

        private void submitSuccess(String msg) {
            loadingDialog.dismissDialog();
            dismissDialog();
            messageDialog.setMessageType(Enums.SUCCESS_MESSAGE);
            messageDialog.setMessage(msg);
            messageDialog.showDialog();
        }

        public void setDatabaseReference(DatabaseReference databaseReference) {
            this.databaseReference = databaseReference;
        }

        public void setWeddingTips(WeddingTips weddingTips) {
            this.weddingTips = weddingTips;

            etTopic.setText(weddingTips.getTopic());
            etAuthor.setText(weddingTips.getAuthor());
            etDescription.setText(weddingTips.getDescription());
            etTips.setText(weddingTips.getTips());
        }
//    public void setImageData(Uri uri) {
//        imageUri = uri;
//        isImageChanged = true;
//
//        Glide.with(context).load(imageUri).centerCrop().placeholder(R.drawable.ic_wsap).
//                error(R.drawable.ic_wsap).into(imgIcon2);
//
//    }
        private void submit() {
            imgUriArray = new ArrayList<>();
            tipsImages = new ArrayList<>();
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference();
            String weddingTipsKey = databaseReference.child("weddingTips").push().getKey();

            Map<String, Object> map = new HashMap<>();
            storageReference = FirebaseStorage.getInstance().getReference("weddingTips");

            DateTime date = new DateTime();
            WeddingTips weddingTips = new WeddingTips(weddingTipsKey, topicLabel,
                    author ,description, tips, date.getDateText());

//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            String msg = context.getString(R.string.add_record_success_msg, "a topic");
//                            submitSuccess(msg);
//                        }  else if (task.getException() != null)
//                            submitFailed(task.getException().toString());
//                    });

            for(Uri uri: imgArray){
                StorageReference fileRef = storageReference.child(weddingTipsKey).child(System.currentTimeMillis() + '.' + getFileExtension(uri));
                fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri1) {
                                String imageKey = databaseReference.child("weddingTips").push().getKey();
                                map.put(imageKey, uri1.toString());
                                Toast.makeText(context, map.keySet().toString(), Toast.LENGTH_SHORT).show();
                                databaseReference.child("weddingTips").child(weddingTipsKey).setValue(weddingTips);
                                databaseReference.child("weddingTips").child(weddingTipsKey).child("image").updateChildren(map);
                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }

        }

        public String getFileExtension(Uri uri){
            ContentResolver cr = context.getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(cr.getType(uri));
        }
}

