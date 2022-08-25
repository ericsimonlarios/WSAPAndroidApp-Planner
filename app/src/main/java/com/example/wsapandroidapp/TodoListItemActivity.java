package com.example.wsapandroidapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wsapandroidapp.Adapters.TodoChkListAdapter;
import com.example.wsapandroidapp.Adapters.TodoListItemAdapter;
import com.example.wsapandroidapp.Classes.DateTime;
import com.example.wsapandroidapp.DataModel.ToDoChecklist;
import com.example.wsapandroidapp.DataModel.Todo;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TodoListItemActivity extends AppCompatActivity {
    private static final String TAG = "Error";
    Toolbar toolbar;
    EditText listEditTitle;
    RecyclerView listItemRV;
    ImageView clearTitle;
    String currentDate, userId, listTitle, key, passedKey, passedTitle;
    int counter;
    MaterialCardView addTask;
    TodoListItemAdapter todoListItemAdapter;
    List<Todo> item;
    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_info);

        //counter for times the add new task button is pressed
        counter = 0;

        // Firebase References
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();

        // Element Invocations
        toolbar = findViewById(R.id.toolbar2);
        listEditTitle = findViewById(R.id.listEditTitle);
        listItemRV = findViewById(R.id.listItemRV);
        clearTitle = findViewById(R.id.clearTitle);
        addTask = findViewById(R.id.addTask);

        // Invocation of Appbar and its properties
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Other class calls
        DateTime getDate = new DateTime();
        currentDate = getDate.getDateTimeText();

        // Get passed values from TodoChecklistActivity
        Intent intent = getIntent();
        if(intent.getStringExtra("id") != null){
            DatabaseReference getId = mDatabase.child("TodoListItem").child(userId);
            passedTitle = intent.getStringExtra("listTitle");
            passedKey = intent.getStringExtra("id");
            getId.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for(DataSnapshot node: snapshot.getChildren()){
                            ArrayList getData = new ArrayList<>();
                            if(Objects.requireNonNull(node.child("titleKey").getValue()).toString().equals(passedKey)){

                                getData.add(node.child("listName").getValue().toString());
                                getData.add(node.child("isChecked").getValue().toString());
                                getData.add(node.getKey());
                                item.add(new Todo(getData));
                                todoListItemAdapter.notifyItemInserted(counter);
                                counter++;
                            }
                        }
                    }else{
                        Toast.makeText(TodoListItemActivity.this, "snapshot does not exist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG, "loadPost:onCancelled", error.toException());
                }
            });
            listEditTitle.setText(passedTitle);
        }else{
            passedKey = mDatabase.child("TodoChecklist").child(userId).push().getKey(); // Generate random key for list
        }
        item = new ArrayList<Todo>();
        listEditTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                listTitle = listEditTitle.getText().toString();
                Todo data = new Todo(listTitle, currentDate, userId);
                mDatabase.child("TodoCheckList").child(userId).child(key).setValue(data);
                todoListItemAdapter.notifyItemInserted(counter);
            }
        });

        editTextFocus(listEditTitle);
        clearTitle.setOnClickListener(v -> listEditTitle.getText().clear());
        key = passedKey;
        Toast.makeText(this, key, Toast.LENGTH_SHORT).show();
        // Adds new task
        addTask.setOnClickListener(v ->{
            String listKey = mDatabase.child("TodoChecklist").child(userId).push().getKey();
            ArrayList test1 = new ArrayList();
            test1.add("");
            test1.add(true);
            test1.add(listKey);
            item.add(new Todo(test1));
            todoListItemAdapter.notifyItemInserted(counter);
            counter++;
        });
        callAdapter(listItemRV, item, key);
    }

    public void callAdapter(RecyclerView listItemRV, List<Todo> item, String key){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listItemRV.setLayoutManager(linearLayoutManager);
        todoListItemAdapter = new TodoListItemAdapter(item,TodoListItemActivity.this, key);
        listItemRV.setAdapter(todoListItemAdapter);
    }

    public void getDatabase(DatabaseReference getId, List<Todo> item, String keyPass, String titlePassed){

    }

    public void editTextFocus(EditText listEditTitle){
        listEditTitle.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                clearTitle.setVisibility(View.GONE);
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
        return super.onOptionsItemSelected(item);
    }

}