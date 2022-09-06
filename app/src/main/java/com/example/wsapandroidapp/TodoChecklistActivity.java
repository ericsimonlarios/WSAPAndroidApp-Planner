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

import com.example.wsapandroidapp.Adapters.TodoChkListAdapter;
import com.example.wsapandroidapp.Adapters.TodoFinishedTaskAdapter;
import com.example.wsapandroidapp.Classes.ComponentManager;
import com.example.wsapandroidapp.Classes.Enums;
import com.example.wsapandroidapp.DataModel.Todo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TodoChecklistActivity extends AppCompatActivity {
    private static final String TAG = "Error";

    EditText etSearch;
    TextView tvMessage, textView27;
    RecyclerView chkListRV, finishedTaskRV;
    Spinner sortSpinner;
    FloatingActionButton addList;

    String userId;
    List<Todo> list = new ArrayList<>();
    List<String> keys = new ArrayList<>();
    List<Todo> items = new ArrayList<>();
    List<Todo> finItems = new ArrayList<>();
    List<Todo> fullItems = new ArrayList<>();
    TodoChkListAdapter chkListAdapter;
    TodoFinishedTaskAdapter finListAdapter;
    ComponentManager componentManager;
    int pos = 0;
    String searchSupplier = "";
    boolean isSearched;

    private DatabaseReference mDatabase, cleanListItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_checklist);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference("TodoCheckList").child(userId);
        cleanListItems = FirebaseDatabase.getInstance().getReference().child("TodoListItem");
        etSearch = findViewById(R.id.etSearch);
        tvMessage = findViewById(R.id.tvMessage);
        textView27 = findViewById(R.id.textView27);
        chkListRV = findViewById(R.id.chkListRV);
        finishedTaskRV = findViewById(R.id.finishedTaskRV);
        addList = findViewById(R.id.addList);
        sortSpinner = findViewById(R.id.sortSpinner);

        ArrayAdapter <CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.todoSortArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);
        sortSpinner.setOnItemSelectedListener(sort());

        componentManager = new ComponentManager(this);
        componentManager.setInputRightDrawable(etSearch, true, Enums.VOICE_RECOGNITION);
        componentManager.setVoiceRecognitionListener(() -> startActivityForResult(componentManager.voiceRecognitionIntent(), Enums.VOICE_RECOGNITION_REQUEST_CODE));

        mDatabase.addValueEventListener(getListQuery());
        cleanListItems.addListenerForSingleValueEvent(cleanItems());
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
                if(s.toString().equals("")){
                    items.clear();
                    isSearched = false;
                    callAdapter(list);
                    callFinAdapter(finItems);
                }else{
                    filterSearch(s.toString());
                }
            }
        });

        addList.setOnClickListener(v -> {
            Intent intent = new Intent(TodoChecklistActivity.this, TodoListItemActivity.class);
            startActivity(intent);
        });

    }

    public void filterSearch(String searchItem){
        items.clear();
        isSearched = true;
        for(Todo listItem: fullItems){
            if(listItem.getListTitle().toLowerCase().contains(searchItem)){
                items.add(listItem);
            }
        }

        if(items.size() == 0 && finItems.size() == 0){
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText(getString(R.string.no_record, "Record"));
        }else{
            tvMessage.setVisibility(View.GONE);
        }
        finishedTaskRV.setVisibility(View.GONE);
        textView27.setVisibility(View.GONE);
        getPos();
        callAdapter(items);
    }

    public void getPos(){
        switch (pos){
            case 0:{
                if(isSearched){
                    items.sort((o1, o2) -> o2.getDateCreated().compareToIgnoreCase(o1.getDateCreated()));
                    callAdapter(items);
                }else{
                    list.sort((o1, o2) -> o2.getDateCreated().compareToIgnoreCase(o1.getDateCreated()));
                    finItems.sort((o1, o2) -> o2.getDateCreated().compareToIgnoreCase(o1.getDateCreated()));
                    callFinAdapter(finItems);
                    callAdapter(list);
                }
                break;
            }
            case 1:{
                if(isSearched){
                    items.sort((o1, o2) -> o1.getDateCreated().compareToIgnoreCase(o2.getDateCreated()));
                    callAdapter(items);
                }else{
                    list.sort((o1, o2) -> o1.getDateCreated().compareToIgnoreCase(o2.getDateCreated()));
                    finItems.sort((o1, o2) -> o1.getDateCreated().compareToIgnoreCase(o2.getDateCreated()));
                    callFinAdapter(finItems);
                    callAdapter(list);
                }
                break;
            }
            case 2:{
                if(isSearched){
                    items.sort((o1, o2) -> o1.getListTitle().compareToIgnoreCase(o2.getListTitle()));
                    callAdapter(items);
                }else{
                    list.sort((o1, o2) -> o1.getListTitle().compareToIgnoreCase(o2.getListTitle()));
                    finItems.sort((o1, o2) -> o1.getListTitle().compareToIgnoreCase(o2.getListTitle()));
                    callFinAdapter(finItems);
                    callAdapter(list);
                }
                break;
            }
            case 3:{
                if(isSearched){
                    items.sort((o1, o2) -> o2.getListTitle().compareToIgnoreCase(o1.getListTitle()));
                    callAdapter(items);
                }else{
                    list.sort((o1, o2) -> o2.getListTitle().compareToIgnoreCase(o1.getListTitle()));
                    finItems.sort((o1, o2) -> o2.getListTitle().compareToIgnoreCase(o1.getListTitle()));
                    callFinAdapter(finItems);
                    callAdapter(list);
                }
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
                list.sort((o1, o2) -> o2.getDateCreated().compareToIgnoreCase(o1.getDateCreated()));
                callAdapter(list);
            }
        };
    }

    public ValueEventListener cleanItems(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot node: snapshot.getChildren()){
                        for (DataSnapshot nodeChild: node.getChildren()){
                                if(!keys.contains(nodeChild.child("titleKey").getValue().toString())){
                                    nodeChild.getRef().removeValue();
                                }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        };
    }

    public ValueEventListener getListQuery() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    finItems.clear();
                    if (snapshot.exists()) {
                        for (DataSnapshot node : snapshot.getChildren()) {
                            List<String> getData = new ArrayList<>();
                            keys.add(node.getKey());
                            Boolean isChecked = (Boolean) node.child("checked").getValue();
                            getData.add(node.child("listTitle").getValue().toString());
                            getData.add(node.child("dateCreated").getValue().toString());
                            getData.add(node.child("uid").getValue().toString());
                            fullItems.add(new Todo(getData.get(0), getData.get(1), getData.get(2), node.getKey(), isChecked));
                            if(Boolean.TRUE.equals(isChecked)){
                                finItems.add(new Todo(getData.get(0), getData.get(1), getData.get(2), node.getKey(), isChecked));
                            }
                            else{
                                list.add(new Todo(getData.get(0), getData.get(1), getData.get(2), node.getKey(), isChecked));
                            }
                        }

                        callAdapter(list);
                        callFinAdapter(finItems);
                        getPos();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
        chkListAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void callFinAdapter(List<Todo> finList){

        if(finList.size() != 0){
            finishedTaskRV.setVisibility(View.VISIBLE);
            textView27.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TodoChecklistActivity.this, LinearLayoutManager.VERTICAL, false);
            finishedTaskRV.setLayoutManager(linearLayoutManager);
            finListAdapter = new TodoFinishedTaskAdapter(finList, TodoChecklistActivity.this);
            finishedTaskRV.setAdapter(finListAdapter);
            finListAdapter.notifyDataSetChanged();
        }else{
            finishedTaskRV.setVisibility(View.GONE);
            textView27.setVisibility(View.GONE);
        }
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