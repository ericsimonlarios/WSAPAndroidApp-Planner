package com.example.wsapandroidapp.Adapters;

import static com.example.wsapandroidapp.R.color.primary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wsapandroidapp.NoteDetails;
import com.example.wsapandroidapp.R;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.Viewholder> {
    List<String> titles;
    List<String> content;

    public NotesAdapter(List<String> title, List<String> content){
        this.titles= title;
        this.content = content;
    }
    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout,parent,false);
        return new Viewholder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, @SuppressLint("RecyclerView") final int position) {
        holder.noteTitle.setText(titles.get(position));
        holder.notecontent.setText(content.get(position));
        holder.mCardView.setCardBackgroundColor(holder.view.getResources().getColor(primary));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), NoteDetails.class);
                i.putExtra("title",titles.get(position));
                i.putExtra("content",content.get(position));
                v.getContext().startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{
        TextView noteTitle,notecontent;
        View view;
        CardView mCardView;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.titles);
            notecontent = itemView.findViewById(R.id.content);
            mCardView = itemView.findViewById(R.id.noteCard);
            view = itemView;
        }
    }

}
