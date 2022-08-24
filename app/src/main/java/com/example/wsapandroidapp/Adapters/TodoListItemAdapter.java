package com.example.wsapandroidapp.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wsapandroidapp.DataModel.Todo;
import com.example.wsapandroidapp.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class TodoListItemAdapter extends RecyclerView.Adapter<TodoListItemAdapter.ViewHolder> {

    private List<Todo> item;
    private Context context;

    public TodoListItemAdapter(List<Todo> item, Context context){
        this.item = item;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        EditText chklistItem, listEditTitle;
        CheckBox chkBoxList;
        ConstraintLayout conLayout1;
        ImageView clearTask;
        MaterialCardView addTask;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chklistItem = itemView.findViewById(R.id.chklistItem);
            chkBoxList = itemView.findViewById(R.id.chkBoxList);
            conLayout1 = itemView.findViewById(R.id.conLayout1);
            clearTask = itemView.findViewById(R.id.clearTask);
            addTask = itemView.findViewById(R.id.addTask);
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
        holder.chklistItem.setText((CharSequence) item.get(position).getChecklist().get(0));
        editTextListener(holder, position);
        chkBoxListener(holder, position);
        holder.clearTask.setOnClickListener(v-> holder.chklistItem.getText().clear());

    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public void editTextListener(@NonNull ViewHolder holder, int position){

        holder.chklistItem.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                holder.clearTask.setVisibility(View.GONE);
            }else{
                holder.clearTask.setVisibility(View.VISIBLE);
            }
        });
    }

    public void chkBoxListener(@NonNull ViewHolder holder, int position){
        holder.chkBoxList.setOnCheckedChangeListener((v, isChecked) ->{
            if(isChecked){
                holder.chklistItem.setPaintFlags(holder.chklistItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.chklistItem.setTextColor(context.getColor(R.color.gray));
            }else{
                holder.chklistItem.setPaintFlags(holder.chklistItem.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG);
                holder.chklistItem.setTextColor(context.getColor(R.color.black));
            }
        });
    }

}
