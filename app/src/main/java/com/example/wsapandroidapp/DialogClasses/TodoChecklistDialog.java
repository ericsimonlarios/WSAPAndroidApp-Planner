package com.example.wsapandroidapp.DialogClasses;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.wsapandroidapp.Classes.DateTime;
import com.example.wsapandroidapp.DataModel.Todo;
import com.example.wsapandroidapp.R;
import com.example.wsapandroidapp.TodoChecklistActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class TodoChecklistDialog {
    private TextView tvMessageTitle;
    private EditText chkListTitle;
    private Dialog dialog;
    private Context context;
    private Button btnAdd;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private Boolean isUpdate;
    private String strListTitle, userId, getDate, getKey;
    private Todo todo;
    public TodoChecklistDialog(Context context, Boolean isUpdate){
        this.context = context;
        this.isUpdate = isUpdate;
    }

    public void setDialog(){

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();

        DateTime date = new DateTime();

        getDate = date.getDateText();

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_create_new_title);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        tvMessageTitle = dialog.findViewById(R.id.tvMessageTitle);
        chkListTitle = dialog.findViewById(R.id.chkListTitle);
        btnAdd = dialog.findViewById(R.id.btnAdd);

        tvMessageTitle.setText(context.getString(R.string.add_record, "Checklist"));

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
            }
        });

        btnAdd.setOnClickListener(view ->{
            submit();
        });
    }

    public void editQuery(Todo todo){
        this.todo = todo;
        chkListTitle.setText(todo.getListTitle());
        getDate = todo.getDateCreated();
        userId = todo.getUid();
        getKey = todo.getTitleKey();
        isUpdate = true;
        btnAdd.setText(context.getString(R.string.update));
    }

    public void showDialog(){
        dialog.show();
    }

    public void submit(){
        if(isUpdate){
            if(!strListTitle.equals("") || userId != null){
                assert getKey != null;
                databaseReference.child("TodoChecklist").child(userId).child(getKey).child("listTitle").setValue(strListTitle);
            }
        }else{
            getKey = databaseReference.child("TodoChecklist").push().getKey();
            Todo todo = new Todo(strListTitle, getDate, userId, getKey);
            if(!strListTitle.equals("") || userId != null){
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
