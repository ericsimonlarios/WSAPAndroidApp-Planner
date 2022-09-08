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

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wsapandroidapp.Classes.ComponentManager;
import com.example.wsapandroidapp.Classes.Credentials;
import com.example.wsapandroidapp.Classes.DateTime;
import com.example.wsapandroidapp.Classes.Enums;
import com.example.wsapandroidapp.DataModel.Exhibitor;
import com.example.wsapandroidapp.DataModel.WeddingTips;
import com.example.wsapandroidapp.DialogClasses.MessageDialog;
import com.example.wsapandroidapp.DialogClasses.LoadingDialog;
import com.example.wsapandroidapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.annotation.Nullable;

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

    private Uri imageUri;
    private  WeddingTips weddingTips;
    private String topicLabel, author, description, tips;


    public WeddingTipsFormDialog(Context context) {
        this.context = context;

        createDialog();
    }

    private void createDialog() {
        setDialog();
        setDialogWindow();
    }
    private void setDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wedding_tips_form_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        ImageView imgClose = dialog.findViewById(R.id.imgClose);
        tvMessageTitle = dialog.findViewById(R.id.tvMessageTitle);
        imgIcon = dialog.findViewById(R.id.imgIcon);
        imgIcon2 = dialog.findViewById(R.id.imgIcon2);
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
//            checkLabel(topicLabel, true, context.getString(R.string.weddingTips_label), tvTopicError, etTopic);
//            if (componentManager.isNoInputError())
                submit();
        });

        imgClose.setOnClickListener(view -> dismissDialog());

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

        imageUri = null;
        etTopic.getText().clear();
        etAuthor.getText().clear();
        etDescription.getText().clear();
        etTopic.getText().clear();
    }
    private void checkImage() {
        componentManager.hideInputError(tvImageError);

        if (imageUri == null)
            componentManager.showInputError(tvImageError, context.getString(R.string.required_input_error, "Image"));
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
    public void setImageData(Uri uri) {
        imageUri = uri;
        isImageChanged = true;

        Glide.with(context).load(imageUri).centerCrop().placeholder(R.drawable.ic_wsap).
                error(R.drawable.ic_wsap).into(imgIcon2);
        checkImage();

    }
    private String getFileExt(Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void submit() {

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        String weddingTipsKey = databaseReference.child("weddingTips").push().getKey();
        DateTime date = new DateTime();
        WeddingTips weddingTips = new WeddingTips(weddingTipsKey, topicLabel,
                author ,description, tips, date.getDateText());

        databaseReference.child("weddingTips").child(weddingTipsKey).setValue(weddingTips)
                        .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String msg = context.getString(R.string.add_record_success_msg, "a topic");
                        submitSuccess(msg);
                    }  else if (task.getException() != null)
                        submitFailed(task.getException().toString());
                });
    }


    private WeddingTipsFormDialog.DialogListener dialogListener;

    public interface DialogListener {
        void chooseImage();
    }

    public void setDialogListener(WeddingTipsFormDialog.DialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

}
