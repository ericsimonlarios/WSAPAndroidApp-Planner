package com.example.wsapandroidapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wsapandroidapp.Adapters.TodoListItemAdapter;
import com.example.wsapandroidapp.Classes.DateTime;
import com.example.wsapandroidapp.DataModel.Todo;
import com.example.wsapandroidapp.DialogClasses.LoadingDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TodoListItemActivity extends AppCompatActivity {
    private static final String TAG = "Error";

    Toolbar toolbar;
    EditText listEditTitle;
    RecyclerView listItemRV;
    ImageView clearTitle;
    ConstraintLayout mainConLayout;
    Boolean isNew, isChecked;
    View disableOverlay;

    String currentDate, userId, listTitle, key, passedKey, passedTitle;
    int counter;

    MaterialCardView addTask;
    TodoListItemAdapter todoListItemAdapter;
    List<Todo> item;

    LoadingDialog loadingDialog;

    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_info);

        //counter for times the add new task button is pressed
        counter = 0;

        // Firebase References
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            userId = firebaseUser.getUid();
        }

        // Element Invocations
        toolbar = findViewById(R.id.toolbar2);
        listEditTitle = findViewById(R.id.listEditTitle);
        listItemRV = findViewById(R.id.listItemRV);
        clearTitle = findViewById(R.id.clearTitle);
        addTask = findViewById(R.id.addTask);
        disableOverlay = findViewById(R.id.disableOverlay);
        mainConLayout = findViewById(R.id.mainConLayout);

        loadingDialog = new LoadingDialog(this);

        // Invocation of Appbar and its properties
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Other class calls
        DateTime getDate = new DateTime();
        currentDate = getDate.getDateTimeText();

        // Get passed values from TodoChecklistActivity
        Intent intent = getIntent();
        isChecked = intent.getBooleanExtra("checked", false);

        if(isChecked){
            for (int i = 0; i < mainConLayout.getChildCount(); i++) {
                View child = mainConLayout.getChildAt(i);
                child.setEnabled(false);
            }
           disableOverlay.setVisibility(View.VISIBLE);
           listEditTitle.setTextColor(this.getColor(R.color.light_gray));

        }
        if(intent.getStringExtra("id") != null){
            loadingDialog.showDialog();
            getPassedData(intent);
            isNew = false;
        }else{
            passedKey = mDatabase.child("TodoChecklist").child(userId).push().getKey(); // Generate random key for list
            isNew = true;
        }

        item = new ArrayList<>();

        listEditTitle.addTextChangedListener(getLatestInput());

        editTextFocus(listEditTitle);
        clearTitle.setOnClickListener(v -> listEditTitle.getText().clear());

        key = passedKey;

        // Adds new task
        addTask.setOnClickListener(v -> addNewTask());

        callAdapter(listItemRV, item, key);
    }

    public void getPassedData(Intent intent){
        DatabaseReference getId = mDatabase.child("TodoListItem").child(userId);
        passedTitle = intent.getStringExtra("listTitle");
        passedKey = intent.getStringExtra("id");

        getId.addListenerForSingleValueEvent(getDatabase()); // read from database
        listEditTitle.setText(passedTitle);
    }

    public void addNewTask(){
        String listKey = mDatabase.child("TodoChecklist").child(userId).push().getKey();
        ArrayList test1 = new ArrayList();
        test1.add("");
        test1.add(false);
        test1.add(listKey);
        test1.add(false);
        item.add(new Todo(test1));
        todoListItemAdapter.notifyItemInserted(counter);
        counter++;
    }

    public void callAdapter(RecyclerView listItemRV, List<Todo> item, String key){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listItemRV.setLayoutManager(linearLayoutManager);
//        todoListItemAdapter = new TodoListItemAdapter(item,TodoListItemActivity.this, key);
        listItemRV.setAdapter(todoListItemAdapter);
    }

    public TextWatcher getLatestInput(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                listTitle = listEditTitle.getText().toString();
                if(!listTitle.equals("")){
                    isNew = false;
//                    Todo data = new Todo(listTitle, currentDate, userId, isChecked);
//                    mDatabase.child("TodoCheckList").child(userId).child(key).setValue(data);
                    todoListItemAdapter.notifyItemInserted(counter);
                }
            }
        };
    }

    public ValueEventListener getDatabase(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot node: snapshot.getChildren()){
                        ArrayList getData = new ArrayList<>();
                        if(Objects.requireNonNull(node.child("titleKey").getValue()).toString().equals(passedKey)){

                            getData.add(node.child("listName").getValue().toString());
                            getData.add(node.child("isChecked").getValue());
                            getData.add(node.getKey());
                            if(isChecked){
                                getData.add(true);
                            }else{
                                getData.add(false);
                            }
                            item.add(new Todo(getData));
                            todoListItemAdapter.notifyItemInserted(counter);
                            counter++;
                        }
                    }
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

    public ValueEventListener deleteTitle(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot node: snapshot.getChildren()){
                        String titleId = node.getRef().getKey();
                        if(titleId.equals(passedKey)){
                            node.getRef().removeValue();
                            Intent intent = new Intent(TodoListItemActivity.this, TodoChecklistActivity.class);
                            startActivity(intent);
                        }
                    }
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

    public ValueEventListener deleteList(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot node: snapshot.getChildren()){
                        String titleKey = node.child("titleKey").getValue().toString();
                        if(titleKey.equals(passedKey)){
                            node.getRef().removeValue();
                        }
                    }
                }
                loadingDialog.dismissDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
                loadingDialog.dismissDialog();
            }
        };
    }

    public void editTextFocus(EditText listEditTitle){
        listEditTitle.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                clearTitle.setVisibility(View.GONE);
                if(!isNew){
                    listEditTitle.setText(listTitle);
                }
            }else{
                clearTitle.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.deleteList:{
                if(isNew){
                    Intent intent = new Intent(TodoListItemActivity.this, TodoChecklistActivity.class);
                    startActivity(intent);
                }
                DatabaseReference getId = mDatabase.child("TodoListItem").child(userId);
                getId.addListenerForSingleValueEvent(deleteList());
                getId = mDatabase.child("TodoCheckList").child(userId);
                getId.addListenerForSingleValueEvent(deleteTitle());
                return true;
            }
            case R.id.markDone:{
                if(!isNew){
                    mDatabase.child("TodoCheckList").child(userId).child(passedKey).child("checked").setValue(true);
                    Intent intent = new Intent(TodoListItemActivity.this, TodoChecklistActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(this, "Enter a title first", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            default:{
                return super.onOptionsItemSelected(item);
            }
        }
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, TodoChecklistActivity.class));
        finish();
    }
}