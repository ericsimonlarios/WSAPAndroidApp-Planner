package com.example.wsapandroidapp.DialogClasses;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.wsapandroidapp.DataModel.Exhibitor;
import com.example.wsapandroidapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.annotation.Nullable;

public class WeddingTipsFormDialog {

    private RecyclerView imgIcon;
    private EditText etTopic,etAuthor, etDescription, etTips;
    private TextView tvMessageTitle, tvImageError, tvTopicError;
    private Button btnSubmit;

    private final Context context;
    private Dialog dialog;

    private LoadingDialog loadingDialog;
    private MessageDialog messageDialog;

    private FirebaseStorage firebaseStorage;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private Uri imageUri;
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
        tvImageError = dialog.findViewById(R.id.tvImageError);

        etTopic = dialog.findViewById(R.id.etTopic);
        etAuthor = dialog.findViewById((R.id.etAuthor));
        etDescription = dialog.findViewById(R.id.etDescription);
        etTips = dialog.findViewById((R.id.etTips));

        tvTopicError = dialog.findViewById(R.id.tvTopicError);
        Button btnChooseImage = dialog.findViewById(R.id.btnChooseImage);
        btnSubmit = dialog.findViewById(R.id.btnSubmit);


        imgClose.setOnClickListener(view -> dismissDialog());

    }


    private void setDialogWindow() {
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void showDialog() {
        dialog.show();
        tvMessageTitle.setText(context.getString(R.string.add_record, "Topic"));
    }

    public void dismissDialog() {
        dialog.dismiss();

        imageUri = null;
        etTopic.getText().clear();
        etAuthor.getText().clear();
        etDescription.getText().clear();
        etTopic.getText().clear();

    }

}
