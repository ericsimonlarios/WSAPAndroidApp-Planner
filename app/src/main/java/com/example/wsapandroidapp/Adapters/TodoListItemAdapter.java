package com.example.wsapandroidapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wsapandroidapp.R;

import java.util.List;

public class TodoListItemAdapter extends RecyclerView.Adapter<TodoListItemAdapter.ViewHolder> {

    private List<String> item;
    private Context context;

    public TodoListItemAdapter(List<String> item, Context context){
        this.item = item;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        EditText chklistItem;
        CheckBox chkBoxList;
        ConstraintLayout conLayout1;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            chklistItem = itemView.findViewById(R.id.chklistItem);
            chkBoxList = itemView.findViewById(R.id.chkBoxList);
            conLayout1 = itemView.findViewById(R.id.conLayout1);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_todo_checklist_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.chklistItem.setText(item.get(0));
    }

    @Override
    public int getItemCount() {
        return item.size();
    }


}
