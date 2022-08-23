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

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wsapandroidapp.Adapters.TodoListItemAdapter;
import com.example.wsapandroidapp.DataModel.ToDoChecklist;
import com.example.wsapandroidapp.DataModel.Todo;

import java.util.ArrayList;
import java.util.List;

public class TodoListItemActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText listEditTitle, list;
    RecyclerView listItemRV;
    ImageView clearTitle;
    String title;
    ConstraintLayout mainConLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_info);

        toolbar = findViewById(R.id.toolbar2);
        listEditTitle = findViewById(R.id.listEditTitle);
        listItemRV = findViewById(R.id.listItemRV);
        clearTitle = findViewById(R.id.clearTitle);
        Intent intent = getIntent();
        title = intent.getStringExtra("id");
        listEditTitle.setText(title);

        ArrayList test = new ArrayList();
        test.add("Lorem");
        test.add(true);
        List<Todo> item= new ArrayList<Todo>();
        item.add(new Todo(test));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listItemRV.setLayoutManager(linearLayoutManager);
        TodoListItemAdapter todoListItemAdapter = new TodoListItemAdapter(item,TodoListItemActivity.this);
        listItemRV.setAdapter(todoListItemAdapter);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        editTextFocus(listEditTitle);
        clearTitle.setOnClickListener(v -> listEditTitle.getText().clear());

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