package com.example.wsapandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Addnote extends AppCompatActivity {

    FirebaseFirestore fstore;
    FloatingActionButton savenote;
    ImageView back,close;
    EditText noteTitle,noteContent;
    ProgressBar progressBarSave;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnote);

        fstore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        savenote = findViewById(R.id.save_note);
        back = findViewById(R.id.back_addnote);
        noteTitle=findViewById(R.id.addnotetitle);
        noteContent=findViewById(R.id.addnotecontent);
        progressBarSave= findViewById(R.id.progressBarsave);
        close = findViewById(R.id.close_addnote);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Addnote.this,"Not Saved", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), Planner_notes.class));
            }
        });

        //save note

        savenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nTitle = noteTitle.getText().toString();
                String nContent = noteContent.getText().toString();

                if (nTitle.isEmpty() || nContent.isEmpty()){
                    Toast.makeText(Addnote.this,"Empty Field", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBarSave.setVisibility(View.VISIBLE);

                // save note firebase
                DocumentReference docref = fstore.collection("notes").document(user.getUid()).collection("myNotes").document();
                Map<String,Object> note = new HashMap<>();
                note.put("title",nTitle);
                note.put("content",nContent);

                docref.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Addnote.this,"Note Saved", Toast.LENGTH_SHORT).show();
                        onBackPressed();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Addnote.this,"Error, Try Again", Toast.LENGTH_SHORT).show();
                        progressBarSave.setVisibility(View.GONE);


                    }
                });

                // notes collections

            }
        });
    }
}