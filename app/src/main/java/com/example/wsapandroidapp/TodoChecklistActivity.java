package com.example.wsapandroidapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.wsapandroidapp.Adapters.TodoChkListAdapter;
import com.example.wsapandroidapp.DataModel.Todo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TodoChecklistActivity extends AppCompatActivity {
    EditText etSearch;
    TextView tvMessage;
    RecyclerView chkListRV;
    FloatingActionButton addList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_checklist);
        List<Todo> list = new ArrayList<Todo>();
        list.add(new Todo("This is the title", "Yesterday at 2:45 PM"));
        list.add(new Todo("Test", "Yesterday at 2:45 PM"
        ));
        etSearch = findViewById(R.id.etSearch);
        tvMessage = findViewById(R.id.tvMessage);
        chkListRV = findViewById(R.id.chkListRV);
        addList = findViewById(R.id.addList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        chkListRV.setLayoutManager(linearLayoutManager);
        TodoChkListAdapter chkListAdapter = new TodoChkListAdapter(this,list);
        chkListRV.setAdapter(chkListAdapter);

        addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TodoChecklistActivity.this, TodoListItemActivity.class);
                startActivity(intent);
            }
        });
    }

}