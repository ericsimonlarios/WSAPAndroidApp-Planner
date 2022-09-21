package com.example.wsapandroidapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wsapandroidapp.Adapters.TodoChkListAdapter;
import com.example.wsapandroidapp.Adapters.TodoFinishedItemAdapter;
import com.example.wsapandroidapp.Classes.ComponentManager;
import com.example.wsapandroidapp.Classes.DateTime;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TodoChecklistActivity extends AppCompatActivity {
    private static final String TAG = "Error";

    ConstraintLayout constraintLayout15;
    ImageView itemDisplayManager;
    EditText etSearch;
    TextView tvMessage;
    RecyclerView chkListRV, finishedRV;
    Spinner sortSpinner;
    FloatingActionButton addList;

    LoadingDialog loadingDialog;

    String userId;
    List<Todo> list = new ArrayList<>();
    List<Todo> items = new ArrayList<>();
    List<Todo> finishedList = new ArrayList<>();
    List<Todo> getListItems = new ArrayList<>();
    List<Todo> searchItem;
    TodoChkListAdapter chkListAdapter;
    TodoFinishedItemAdapter finishedItemAdapter;
    ComponentManager componentManager;
    TodoChecklistDialog todoChecklistDialog;
    ConfirmationDialog confirmationDialog;
    DateTime dateTime;
    Date date;
    int pos;
    String searchSupplier = "";
    boolean isSearched;

    private DatabaseReference mDatabase, childRef, getChildRef;


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

        dateTime = new DateTime();

        etSearch = findViewById(R.id.etSearch);
        tvMessage = findViewById(R.id.tvMessage);
        chkListRV = findViewById(R.id.chkListRV);
        finishedRV = findViewById(R.id.finishedRV);
        addList = findViewById(R.id.addList);
        sortSpinner = findViewById(R.id.sortSpinner);
        itemDisplayManager = findViewById(R.id.itemDisplayManager);
        itemDisplayManager.setTag(R.drawable.ic_baseline_arrow_drop_down_24);
        constraintLayout15 = findViewById(R.id.constraintLayout15);

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

        constraintLayout15.setOnClickListener(view -> dropDownManager());

        mDatabase.addValueEventListener(getListQuery());
        getChildRef = childRef;
        getChildRef.addValueEventListener(getRef());
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
        List<String> checkTitle = new ArrayList<>();
        for(Todo listItem: list){
            if(listItem.getListTitle().toLowerCase().contains(searchItem)){
                items.add(listItem);
                checkTitle.add(listItem.getListTitle().toLowerCase());
                continue;
            }else if(listItem.getDateCreated().toLowerCase().contains(searchItem)){
                items.add(listItem);

            }
            for(Todo itemList: getListItems){
                if(listItem.getTitleKey().contains(String.valueOf(itemList.getChecklist().get(2)))){
                    if(String.valueOf(itemList.getChecklist().get(0)).toLowerCase().contains(searchItem)){
                            items.add(listItem);
                            break;
                    }
                }

            }
        }
        if(items.size() == 0){
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText(getString(R.string.no_record, "Record"));
            finishedRV.setVisibility(View.GONE);
            constraintLayout15.setVisibility(View.GONE);
        }else{
            tvMessage.setVisibility(View.GONE);
            finishedRV.setVisibility(View.VISIBLE);
            constraintLayout15.setVisibility(View.VISIBLE);
        }
        getPos();
    }

    public ValueEventListener getRef(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot node: snapshot.getChildren()){
                        for(DataSnapshot nodeChild: node.getChildren()){
                            List list = new ArrayList();
                            list.add(nodeChild.child("listText").getValue().toString());
                            list.add(nodeChild.child("checked").getValue());
                            list.add(nodeChild.child("titleKey").getValue().toString());
                            list.add(nodeChild.child(nodeChild.getKey()));
                            getListItems.add(new Todo(list));
                        }
                    }
                }
                getPos();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    public void getPos(){
        if(isSearched){
            searchItem = items;
            finishedRV.setVisibility(View.GONE);
            constraintLayout15.setVisibility(View.GONE);
        }else{
            searchItem = list;
        }
        DateFormat formatDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        try {
            date = formatDate.parse(dateTime.getDateText());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List test = new ArrayList();

        switch (pos){
            case 0:{
                searchItem.sort((o1, o2) -> {
                    try {
                        date = formatDate.parse(dateTime.getDateText());
                        return date.compareTo(o2.getDateFormat());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                });
                callAdapter(searchItem);
                callFinishedAdapter(finishedList);
                break;
            }
            case 1:{
                test.clear();
                for(Todo listItem: searchItem) {
                    try {
                        if (listItem.getDateFormat().compareTo(date) == 0) {
                            test.add(listItem);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                callAdapter(test);
                callFinishedAdapter(finishedList);
                break;
            }
            case 2:{
                test.clear();
                for(Todo listItem: searchItem) {
                    try {
                        if (listItem.getDateFormat().compareTo(date) < 0) {
                            test.add(listItem);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                callAdapter(test);
                callFinishedAdapter(finishedList);
                break;
            }
            case 3:{
                test.clear();
                for(Todo listItem: searchItem) {
                    try {
                        if (listItem.getDateFormat().compareTo(date) > 0) {
                            test.add(listItem);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                callAdapter(test);
                callFinishedAdapter(finishedList);
                break;
            }
            case 4:{
                searchItem.sort((o1, o2) -> o1.getListTitle().compareToIgnoreCase(o2.getListTitle()));
                callAdapter(searchItem);
                callFinishedAdapter(finishedList);
                break;
            }
            case 5:{
                searchItem.sort((o1, o2) -> o2.getListTitle().compareToIgnoreCase(o1.getListTitle()));
                callAdapter(searchItem);
                callFinishedAdapter(finishedList);
                break;
            }
        }
    }

    public void dropDownManager(){
        Resources res = this.getResources();
        int integer = (Integer) itemDisplayManager.getTag();
        switch (integer){
            case R.drawable.ic_baseline_arrow_drop_down_24:{
                Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.ic_baseline_arrow_drop_up_24, null);
                itemDisplayManager.setImageDrawable(drawable);
                finishedRV.setVisibility(View.GONE);
                itemDisplayManager.setTag(R.drawable.ic_baseline_arrow_drop_up_24);
                break;
            }

            case R.drawable.ic_baseline_arrow_drop_up_24:{
                Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.ic_baseline_arrow_drop_down_24, null);
                itemDisplayManager.setImageDrawable(drawable);
                finishedRV.setVisibility(View.VISIBLE);
                itemDisplayManager.setTag(R.drawable.ic_baseline_arrow_drop_down_24);
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
//                list.sort((o1, o2) -> {
//                    try {
//                        return o2.getDateFormat().compareTo(o1.getDateFormat());
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    return 0;
//                });
//                callAdapter(list);
            }
        };
    }

    public ValueEventListener getListQuery() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    finishedList.clear();
                    if(snapshot.exists()) {
                        for (DataSnapshot node : snapshot.getChildren()) {
                            Todo todo = node.getValue(Todo.class);
                            assert todo != null;
                            if(todo.isChecked()){
                                finishedList.add(new Todo(todo.getListTitle(), todo.getDateCreated(), todo.getUid(), todo.getTitleKey(), todo.isChecked()));
                            }else{
                                list.add(new Todo(todo.getListTitle(), todo.getDateCreated(), todo.getUid(), todo.getTitleKey(), todo.isChecked()));
                            }
                        }

                    }
                    getPos();
                    callAdapter(list);
                    callFinishedAdapter(finishedList);
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

    public void callFinishedAdapter(List<Todo> finishedList){
        if(finishedList.size() != 0 && !isSearched){
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TodoChecklistActivity.this, LinearLayoutManager.VERTICAL, false);
            finishedRV.setLayoutManager(linearLayoutManager);
            finishedItemAdapter = new TodoFinishedItemAdapter(TodoChecklistActivity.this, finishedList);
            finishedRV.setAdapter(finishedItemAdapter);

            constraintLayout15.setVisibility(View.VISIBLE);
            finishedRV.setVisibility(View.VISIBLE);

            finishedItemAdapter.setOnOptionsListener(new TodoFinishedItemAdapter.onOptionsListener() {
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
        }else{
            constraintLayout15.setVisibility(View.GONE);
            finishedRV.setVisibility(View.GONE);
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