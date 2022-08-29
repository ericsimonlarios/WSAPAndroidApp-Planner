package com.example.wsapandroidapp;

import static com.example.wsapandroidapp.R.color.primary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.wsapandroidapp.Adapters.NotesAdapter;
import com.example.wsapandroidapp.Classes.ComponentManager;
import com.example.wsapandroidapp.Classes.Enums;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class Planner_notes extends AppCompatActivity {

    FloatingActionButton addnotes;
    ImageView backtoplanner;
    RecyclerView notelist;
    NotesAdapter adapter;
    FirebaseFirestore fStore;
    FirestoreRecyclerAdapter<NoteRecyclerOptions,NoteViewHolder> noteAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner_notes);

        notelist = findViewById(R.id.recyclerview_notes);

        fStore = FirebaseFirestore.getInstance();

        Query query = fStore.collection("notes").orderBy("title",Query.Direction.DESCENDING);

        //Display Notes from Firebase to Recycle view
        //changes in implementation
        FirestoreRecyclerOptions<NoteRecyclerOptions> allNotes = new FirestoreRecyclerOptions.Builder<NoteRecyclerOptions>()
                .setQuery(query,NoteRecyclerOptions.class)
                .build();

        noteAdapter = new FirestoreRecyclerAdapter<NoteRecyclerOptions, NoteViewHolder>(allNotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, int i, @NonNull final NoteRecyclerOptions note) {
                holder.noteTitle.setText(note.getTitle());
                holder.notecontent.setText(note.getContent());
                holder.mCardView.setCardBackgroundColor(holder.view.getResources().getColor(primary));

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), NoteDetails.class);
                        i.putExtra("title",note.getTitle());
                        i.putExtra("content",note.getContent());
                        v.getContext().startActivity(i);
                    }
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout,parent,false);
                return new NoteViewHolder(view);
            }
        };


        notelist.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        notelist.setAdapter(noteAdapter);


        addnotes = findViewById(R.id.newnotes);
        backtoplanner = findViewById(R.id.back_planner);

        backtoplanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });




        addnotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Addnote.class));
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }

}
