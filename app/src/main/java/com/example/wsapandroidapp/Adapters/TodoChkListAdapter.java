package com.example.wsapandroidapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wsapandroidapp.DataModel.Todo;
import com.example.wsapandroidapp.R;
import com.example.wsapandroidapp.TodoListItemActivity;
import com.google.android.material.card.MaterialCardView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class TodoChkListAdapter extends RecyclerView.Adapter<TodoChkListAdapter.ViewHolder> {
    private List<Todo> dataSet;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView listTitle, chkListDate;
        RecyclerView listRV;
        MaterialCardView listTitleCV, listCV;
        ConstraintLayout conLayout1, conLayout2;
        CheckBox finTask;
        public ViewHolder(View view){
            super(view);

            listTitle = view.findViewById(R.id.listTitle);
            chkListDate = view.findViewById(R.id.chkListDate);
//          listRV = view.findViewById(R.id.listRV);
            listTitleCV = view.findViewById(R.id.listTitleCV);
//          listCV = view.findViewById(R.id.listCV);
            conLayout1 = view.findViewById(R.id.conLayout1);
            conLayout2 = view.findViewById(R.id.conLayout2);
            finTask = view.findViewById(R.id.finTask);
        }
    }

    public TodoChkListAdapter(Context context, List<Todo> dataSet) {
        this.dataSet = dataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public TodoChkListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_todo_checklist_title, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoChkListAdapter.ViewHolder holder, int position) {
        holder.listTitle.setText(dataSet.get(position).getListTitle());
        holder.chkListDate.setText(dataSet.get(position).getlistDate());
        holder.listTitleCV.setOnClickListener(v -> {
            Intent intent = new Intent(context, TodoListItemActivity.class);
            intent.putExtra("id", dataSet.get(position).getListTitle());
            context.startActivity(intent);
        });
        holder.finTask.setOnCheckedChangeListener((v, isChecked) ->{
            if(isChecked){
                holder.listTitle.setPaintFlags(holder.listTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.listTitle.setTextColor(context.getColor(R.color.gray));
            }else{
                holder.listTitle.setPaintFlags(holder.listTitle.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG);
                holder.listTitle.setTextColor(context.getColor(R.color.black));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}
