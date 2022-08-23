package com.example.wsapandroidapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.wsapandroidapp.Adapters.TodoListItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class TodoListItemActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText listEditTitle;
    RecyclerView listItemRV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_info);

        toolbar = findViewById(R.id.toolbar2);
        listEditTitle = findViewById(R.id.listEditTitle);
        listItemRV = findViewById(R.id.listItemRV);

        List item = new ArrayList();
        item.add("Testing");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listItemRV.setLayoutManager(linearLayoutManager);
        TodoListItemAdapter todoListItemAdapter = new TodoListItemAdapter(item,TodoListItemActivity.this);
        listItemRV.setAdapter(todoListItemAdapter);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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