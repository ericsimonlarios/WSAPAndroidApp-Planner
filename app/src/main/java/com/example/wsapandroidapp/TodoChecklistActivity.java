package com.example.wsapandroidapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wsapandroidapp.Adapters.TodoChkListAdapter;
import com.example.wsapandroidapp.Classes.ComponentManager;
import com.example.wsapandroidapp.Classes.Enums;
import com.example.wsapandroidapp.DataModel.Todo;
import com.example.wsapandroidapp.DialogClasses.ConfirmationDialog;
import com.example.wsapandroidapp.DialogClasses.LoadingDialog;
import com.example.wsapandroidapp.DialogClasses.TodoChecklistDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TodoChecklistActivity extends AppCompatActivity {
    private static final String TAG = "Error";

    EditText etSearch;
    TextView tvMessage, textView27;
    RecyclerView chkListRV, finishedTaskRV;
    Spinner sortSpinner;
    FloatingActionButton addList;

    LoadingDialog loadingDialog;

    String userId;
    List<Todo> list = new ArrayList<>();
    List<Todo> items = new ArrayList<>();
    List<Todo> searchItem;
    TodoChkListAdapter chkListAdapter;
    ComponentManager componentManager;
    TodoChecklistDialog todoChecklistDialog;
    ConfirmationDialog confirmationDialog;
    int pos = 0;
    String searchSupplier = "";
    boolean isSearched;

    private DatabaseReference mDatabase, childRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_checklist);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        userId = firebaseUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference("TodoChecklist").child(userId);
        childRef = FirebaseDatabase.getInstance().getReference("TodoChecklistItems").child(userId);

        etSearch = findViewById(R.id.etSearch);
        tvMessage = findViewById(R.id.tvMessage);
        chkListRV = findViewById(R.id.chkListRV);
        addList = findViewById(R.id.addList);
        sortSpinner = findViewById(R.id.sortSpinner);

        loadingDialog = new LoadingDialog(this);
        confirmationDialog = new ConfirmationDialog(this);

        todoChecklistDialog = new TodoChecklistDialog(this, false);

        ArrayAdapter <CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.todoSortArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);
        sortSpinner.setOnItemSelectedListener(sort());

        componentManager = new ComponentManager(this);
        componentManager.setInputRightDrawable(etSearch, true, Enums.VOICE_RECOGNITION);
        componentManager.setVoiceRecognitionListener(() -> startActivityForResult(componentManager.voiceRecognitionIntent(), Enums.VOICE_RECOGNITION_REQUEST_CODE));

        loadingDialog.showDialog();

        mDatabase.addValueEventListener(getListQuery());

        isSearched = false;
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                searchSupplier = s != null ? s.toString() : "";
                assert s != null;
                if(s.toString().equals("")){
                    items.clear();
                    isSearched = false;
                    getPos();
                    callAdapter(list);
                }else{
                    filterSearch(s.toString());
                }
            }
        });

        addList.setOnClickListener(v -> {
            todoChecklistDialog = new TodoChecklistDialog(this, false);
            todoChecklistDialog.setDialog();
            todoChecklistDialog.showDialog();
        });
    }

    public void filterSearch(String searchItem){
        items.clear();
        isSearched = true;
        for(Todo listItem: list){
            if(listItem.getListTitle().toLowerCase().contains(searchItem)){
                items.add(listItem);
            }else if(listItem.getDateCreated().toLowerCase().contains(searchItem)){
                items.add(listItem);
            }
        }
        if(items.size() == 0){
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText(getString(R.string.no_record, "Record"));
        }else{
            tvMessage.setVisibility(View.GONE);
        }
        getPos();
        callAdapter(items);
    }

    public void getPos(){
        if(isSearched){
            searchItem = items;
        }else{
            searchItem = list;
        }
        switch (pos){
            case 0:{
                searchItem.sort((o1, o2) -> {
                    try {
                        return o2.getDateFormat().compareTo(o1.getDateFormat());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                });
                callAdapter(searchItem);
                break;
            }
            case 1:{
                searchItem.sort((o1, o2) -> {
                    try {
                        return o1.getDateFormat().compareTo(o2.getDateFormat());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                });
                callAdapter(searchItem);
                break;
            }
            case 2:{
                searchItem.sort((o1, o2) -> o1.getListTitle().compareToIgnoreCase(o2.getListTitle()));
                callAdapter(searchItem);
                break;
            }
            case 3:{
                searchItem.sort((o1, o2) -> o2.getListTitle().compareToIgnoreCase(o1.getListTitle()));
                callAdapter(searchItem);
                break;
            }
        }
    }

    public AdapterView.OnItemSelectedListener sort(){
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                getPos();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                list.sort((o1, o2) -> {
                    try {
                        return o2.getDateFormat().compareTo(o1.getDateFormat());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                });
                callAdapter(list);
            }
        };
    }

    public ValueEventListener getListQuery() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    if(snapshot.exists()) {
                        for (DataSnapshot node : snapshot.getChildren()) {
                            Todo todo = node.getValue(Todo.class);
                            assert todo != null;
                            list.add(new Todo(todo.getListTitle(), todo.getDateCreated(), todo.getUid(), todo.getTitleKey()));
                        }
                        callAdapter(list);
                    }else{
                        callAdapter(list);
                    }
                    loadingDialog.dismissDialog();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismissDialog();
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        };
    }

    @SuppressLint("NotifyDataSetChanged")
    public void callAdapter(List<Todo> list) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TodoChecklistActivity.this, LinearLayoutManager.VERTICAL, false);
        chkListRV.setLayoutManager(linearLayoutManager);
        chkListAdapter = new TodoChkListAdapter(TodoChecklistActivity.this, list);
        chkListRV.setAdapter(chkListAdapter);
        chkListAdapter.setOnOptionsListener(new TodoChkListAdapter.onOptionsListener() {
            @Override
            public void onEdit(Todo todo, int position) {
                todoChecklistDialog.setDialog();
                todoChecklistDialog.editQuery(todo);
                todoChecklistDialog.showDialog();
            }
            @Override
            public void onDelete(Todo todo) {
                confirmationDialog.setMessage(getString(R.string.confirmation_prompt, "delete the checklist item?"));
                confirmationDialog.showDialog();

                confirmationDialog.setDialogListener(() -> {
                    DatabaseReference deleteRef = mDatabase.child(todo.getTitleKey());
                    DatabaseReference deleteChild = childRef.child(todo.getTitleKey());
                    deleteChild.getRef().removeValue();
                    deleteRef.getRef().removeValue().addOnCompleteListener(task -> Toast.makeText(TodoChecklistActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(TodoChecklistActivity.this, "Delete Failed", Toast.LENGTH_SHORT).show());
                    confirmationDialog.dismissDialog();
                });
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Enums.VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            assert data != null;
            etSearch.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
            filterSearch(etSearch.getText().toString());
        }
    }

}