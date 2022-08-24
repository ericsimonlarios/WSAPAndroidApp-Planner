package com.example.wsapandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.example.wsapandroidapp.Classes.ComponentManager;
import com.example.wsapandroidapp.Classes.Enums;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Planner_notes extends AppCompatActivity {

    FloatingActionButton newnotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner_notes);


        newnotes = findViewById(R.id.newnotes);

        newnotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Planner_notes.this,AddNotesActivity.class));
            }
        });
    }
}
