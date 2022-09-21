package com.example.wsapandroidapp.DialogClasses;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.wsapandroidapp.Classes.ComponentManager;
import com.example.wsapandroidapp.Classes.Credentials;
import com.example.wsapandroidapp.Classes.DateTime;
import com.example.wsapandroidapp.Classes.Enums;
import com.example.wsapandroidapp.DataModel.Todo;
import com.example.wsapandroidapp.R;
import com.example.wsapandroidapp.TodoChecklistActivity;
import com.example.wsapandroidapp.WeddingTipsFormActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TodoChecklistDialog {
    private TextView tvMessageTitle, tvTargetWeddingDate, tvTargetWeddingDateHint, tvTargetWeddingDateError, tvChkListTitleError;
    private EditText chkListTitle;
    private Dialog dialog;
    private Context context;
    private Button btnAdd;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private Boolean isUpdate, checked;
    private String strListTitle, userId, getDate, getKey, targetWeddingDate;
    private Todo todo;
    private long targetWeddingDateTime;
    private ComponentManager componentManager;

    public TodoChecklistDialog(Context context, Boolean isUpdate){
        this.context = context;
        this.isUpdate = isUpdate;
    }

    public void setDialog(){

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        checked = false;
        DateTime date = new DateTime();

        componentManager = new ComponentManager(context);

        getDate = date.getDateText();

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_create_new_title);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        tvMessageTitle = dialog.findViewById(R.id.tvMessageTitle);
        tvTargetWeddingDate = dialog.findViewById(R.id.tvTargetWeddingDate);
        tvTargetWeddingDateHint = dialog.findViewById(R.id.tvTargetWeddingDateHint);
        tvTargetWeddingDateError = dialog.findViewById(R.id.tvTargetWeddingDateError);
        tvChkListTitleError = dialog.findViewById(R.id.tvChkListTitleError);
        ConstraintLayout targetWeddingDateLayout = dialog.findViewById(R.id.targetWeddingDateLayout);
        chkListTitle = dialog.findViewById(R.id.chkListTitle);
        btnAdd = dialog.findViewById(R.id.btnAdd);

        tvMessageTitle.setText(context.getString(R.string.add_record, "Checklist"));

        componentManager.setDatePickerListener(new ComponentManager.DatePickerListener() {
            @Override
            public void onSelect(long dateTime, EditText targetEditText) {

            }

            @Override
            public void onSelect(String date, EditText targetEditText) {

            }

            @Override
            public void onSelect(long dateTime, TextView targetTextView) {
                if (targetTextView == tvTargetWeddingDate)
                    targetWeddingDateTime = dateTime;
            }

            @Override
            public void onSelect(String date, TextView targetTextView) {
                targetTextView.setText(date);
                targetTextView.setVisibility(View.VISIBLE);

                if (targetTextView == tvTargetWeddingDate) {
                    targetWeddingDate = date;
                    tvTargetWeddingDateHint.setVisibility(View.GONE);

                    checkDate(targetWeddingDate, true, context.getString(R.string.target_wedding_date), tvTargetWeddingDateError);
                }
            }
        });

        List<TextView> errorTextViewList =
                Collections.singletonList(tvChkListTitleError);
        List<EditText> errorEditTextList =
                Collections.singletonList(chkListTitle);

        componentManager.initializeErrorComponents(errorTextViewList, errorEditTextList);

        chkListTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable str) {
                strListTitle = str != null ? str.toString() : "";
                componentManager.setInputRightDrawable(chkListTitle, !Credentials.isEmpty(strListTitle), Enums.CLEAR_TEXT);
                checkLabel(strListTitle, true, context.getString(R.string.titleLabel), tvChkListTitleError, chkListTitle);
            }
        });

        btnAdd.setOnClickListener(view ->{
            checkLabel(strListTitle, true, context.getString(R.string.titleLabel), tvChkListTitleError, chkListTitle);
            checkDate(targetWeddingDate, true, context.getString(R.string.target_wedding_date), tvTargetWeddingDateError);
            if (componentManager.isNoInputError() && targetWeddingDate != null){
                submit();
            }
        });

        targetWeddingDateLayout.setOnClickListener(view -> componentManager.showDatePickerDialog(tvTargetWeddingDate));
    }

    public void checkLabel(String string, boolean isRequired, String fieldName,
                           TextView targetTextView, EditText targetEditText){

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

    private void checkDate(String string, boolean isRequired, String fieldName, TextView targetTextView) {
        componentManager.hideInputError(targetTextView);

        if (Credentials.isEmpty(string) && isRequired)
            componentManager.showInputError(targetTextView, context.getString(R.string.required_input_error, fieldName));
    }

    public void editQuery(Todo todo){
        this.todo = todo;
        isUpdate = true;
        chkListTitle.setText(todo.getListTitle());
        tvMessageTitle.setText(context.getString(R.string.add_record, "Update"));
        tvTargetWeddingDate.setText(todo.getDateCreated());
        tvTargetWeddingDateHint.setText(todo.getDateCreated());
        targetWeddingDate = todo.getDateCreated();
        checked = todo.isChecked();
        userId = todo.getUid();
        getKey = todo.getTitleKey();
        btnAdd.setText(context.getString(R.string.update));
    }

    public void showDialog(){
        dialog.show();
    }

    public void submit(){
        if(isUpdate){
            if(!strListTitle.equals("") || userId != null){
                assert getKey != null;
                Todo todo = new Todo(strListTitle, targetWeddingDate, userId, getKey, checked);
                databaseReference.child("TodoChecklist").child(userId).child(getKey).setValue(todo);
            }
        }else{
            getKey = databaseReference.child("TodoChecklist").push().getKey();
            Todo todo = new Todo(strListTitle, targetWeddingDate, userId, getKey, checked);
            if(!strListTitle.equals("")){
                assert getKey != null;
                databaseReference.child("TodoChecklist").child(userId).child(getKey).setValue(todo);
                DatabaseReference todoListItems = firebaseDatabase.getReference().child("TodoChecklistItems").child(userId).child(getKey);
                String listKey = todoListItems.push().getKey();
                HashMap<String, Object> result = new HashMap<>();
                result.put("listText", "");
                result.put("checked", false);
                result.put("titleKey", getKey);
                todoListItems.child(listKey).updateChildren(result);
            }
        }

        dialog.dismiss();
    }
}
