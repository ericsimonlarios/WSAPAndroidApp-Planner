package com.example.wsapandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

public class EditNote extends AppCompatActivity {

    Intent data;
    EditText editNoteTitle, editNoteContent;
    ImageView back;
    FloatingActionButton save_editnote;
    ProgressBar spinner;
    FirebaseFirestore fstore;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        fstore= fstore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        data = getIntent();
        back = findViewById(R.id.back_editnote);
        spinner= findViewById(R.id.progressBar_editnote);

        editNoteContent=findViewById(R.id.editnote_content);
        editNoteTitle=findViewById(R.id.editnote_title);

        String noteTitle = data.getStringExtra("title");
        String noteContent = data.getStringExtra("content");

        editNoteTitle.setText(noteTitle);
        editNoteContent.setText(noteContent);

        save_editnote = findViewById(R.id.save_editnote);
        save_editnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nTitle = editNoteTitle.getText().toString();
                String nContent = editNoteContent.getText().toString();

                if (nTitle.isEmpty() || nContent.isEmpty()){
                    Toast.makeText(EditNote.this,"Empty Field", Toast.LENGTH_SHORT).show();
                    return;
                }
                spinner.setVisibility(View.VISIBLE);

                // save note firebase
                DocumentReference docref = fstore.collection("notes").document(user.getUid()).collection("myNotes").document(data.getStringExtra("noteId"));
                Map<String,Object> note = new HashMap<>();
                note.put("title",nTitle);
                note.put("content",nContent);

                docref.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EditNote.this,"Note Saved", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditNote.this, Planner_notes.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditNote.this,"Error, Try Again", Toast.LENGTH_SHORT).show();
                        spinner.setVisibility(View.GONE);

                    }
                });
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



    }
}