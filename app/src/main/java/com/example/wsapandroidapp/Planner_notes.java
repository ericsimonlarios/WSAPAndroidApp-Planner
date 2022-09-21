package com.example.wsapandroidapp;

import static com.example.wsapandroidapp.R.color.primary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.wsapandroidapp.Adapters.NotesAdapter;
import com.example.wsapandroidapp.Classes.ComponentManager;
import com.example.wsapandroidapp.Classes.Enums;
import com.example.wsapandroidapp.Fragments.PlannerFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
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
    FirestoreRecyclerAdapter<NoteRecyclerOptions, NoteViewHolder> noteAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseusers;
    FirebaseUser user;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner_notes);

        notelist = findViewById(R.id.recyclerview_notes);

        // Realtime Database sample
        databaseusers = FirebaseDatabase.getInstance().getReference();

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        // Firestore Database
        // note >> uid >> mynotes
        Query query = fStore.collection("notes").document(user.getUid()).collection("myNotes").orderBy("title", Query.Direction.DESCENDING);


        //Display Notes from Firebase to Recycle view
        //changes in implementation
        FirestoreRecyclerOptions<NoteRecyclerOptions> allNotes = new FirestoreRecyclerOptions.Builder<NoteRecyclerOptions>()
                .setQuery(query, NoteRecyclerOptions.class)
                .build();

        noteAdapter = new FirestoreRecyclerAdapter<NoteRecyclerOptions, NoteViewHolder>(allNotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, @SuppressLint("RecyclerView") final int i, @NonNull final NoteRecyclerOptions note) {
                holder.noteTitle.setText(note.getTitle());
                holder.notecontent.setText(note.getContent());
                holder.mCardView.setCardBackgroundColor(holder.view.getResources().getColor(primary));
                final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), NoteDetails.class);
                        i.putExtra("title", note.getTitle());
                        i.putExtra("content", note.getContent());
                        i.putExtra("noteId", docId);
                        v.getContext().startActivity(i);
                    }
                });

                ImageView menuIcon = holder.view.findViewById(R.id.menuIcon);
                menuIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();
                        PopupMenu menu = new PopupMenu(v.getContext(),v);
                        menu.setGravity(Gravity.END);
                        menu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent i = new Intent(v.getContext(),EditNote.class);
                                i.putExtra("title",note.getTitle());
                                i.putExtra("content",note.getContent());
                                i.putExtra("noteId",docId);
                                startActivity(i);
                                return false;
                            }
                        });

                        menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                DocumentReference docref = fStore.collection("notes").document(user.getUid()).collection("myNotes").document(docId);
                                docref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // note deleted
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Planner_notes.this, "Delete Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });
                        menu.show();
                    }
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout, parent, false);
                return new NoteViewHolder(view);
            }
        };


        notelist.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        notelist.setAdapter(noteAdapter);



        addnotes = findViewById(R.id.newnotes);
        backtoplanner = findViewById(R.id.back_planner);

        backtoplanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                noteAdapter.notifyDataSetChanged();
                finish();

            }
        });


        addnotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Addnote.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
        noteAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }
}

