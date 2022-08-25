package com.example.wsapandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wsapandroidapp.Adapters.TodoChkListAdapter;
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
    TextView tvMessage;
    RecyclerView chkListRV;
    FloatingActionButton addList;

    String userId;
    List<Todo> list = new ArrayList<>();
    TodoChkListAdapter chkListAdapter;
    boolean isListening;

    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_checklist);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference("TodoCheckList").child(userId);

        etSearch = findViewById(R.id.etSearch);
        tvMessage = findViewById(R.id.tvMessage);
        chkListRV = findViewById(R.id.chkListRV);
        addList = findViewById(R.id.addList);

        isListening = true;
        mDatabase.addListenerForSingleValueEvent(getListQuery());

        addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TodoChecklistActivity.this, TodoListItemActivity.class);
                startActivity(intent);
            }
        });

    }
    public ValueEventListener getListQuery(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(isListening){
                    list.clear();
                    if(snapshot.exists()){
                        List<String> keys = new ArrayList<>();
                        for(DataSnapshot node: snapshot.getChildren()){
                            List<String> getData = new ArrayList<>();
                            keys.add(node.getKey());
                            getData.add(node.child("listTitle").getValue().toString());
                            getData.add(node.child("dateCreated").getValue().toString());
                            getData.add(node.child("uid").getValue().toString());
                            list.add(new Todo(getData.get(0),getData.get(1),getData.get(2), node.getKey()));
                            callAdapter(list);
                        }
                    }else{
                        Toast.makeText(TodoChecklistActivity.this, "snapshot does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        };
    }

    public void callAdapter(List<Todo> list){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TodoChecklistActivity.this, LinearLayoutManager.VERTICAL, false);
        chkListRV.setLayoutManager(linearLayoutManager);
        chkListAdapter = new TodoChkListAdapter(TodoChecklistActivity.this,list);
        chkListRV.setAdapter(chkListAdapter);

    }
    @Override
    public void onResume() {
        isListening = true;
        mDatabase.addListenerForSingleValueEvent(getListQuery());
        super.onResume();
    }
    @Override
    public void onStop() {
        isListening = false;

        super.onStop();
    }
    @Override
    public void onDestroy() {
        isListening = false;

        super.onDestroy();
    }

}