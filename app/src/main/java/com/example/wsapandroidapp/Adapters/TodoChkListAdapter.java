package com.example.wsapandroidapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wsapandroidapp.DataModel.Todo;
import com.example.wsapandroidapp.R;
import com.example.wsapandroidapp.TodoListItemActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.List;

public class TodoChkListAdapter extends RecyclerView.Adapter<TodoChkListAdapter.ViewHolder> {
    private final List<Todo> dataSet;
    private final Context context;
    private String titleKey;
    private Boolean checked;
    DatabaseReference mDatabase;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView listTitle, chkListDate;
        MaterialCardView listTitleCV;
        ConstraintLayout conLayout1, conLayout2;
        CheckBox finTask;
        public ViewHolder(View view){
            super(view);

            listTitle = view.findViewById(R.id.listTitle);
            chkListDate = view.findViewById(R.id.chkListDate);
            listTitleCV = view.findViewById(R.id.listTitleCV);
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int newPos = holder.getAdapterPosition();
        holder.listTitle.setText(dataSet.get(newPos).getListTitle());
        holder.chkListDate.setText(dataSet.get(newPos).getDateCreated());
        holder.finTask.setChecked(dataSet.get(newPos).isChecked());
        holder.listTitleCV.setOnClickListener(v -> {
            Intent intent = new Intent(context, TodoListItemActivity.class);
            intent.putExtra("id", dataSet.get(newPos).getTitleKey());
            intent.putExtra("listTitle", dataSet.get(newPos).getListTitle());
            intent.putExtra("checked", dataSet.get(newPos).isChecked());
            context.startActivity(intent);
           ((Activity)context).finish();
        });
        chkboxManager(holder, position);
        checked = dataSet.get(newPos).isChecked();
    }

    public void chkboxManager(ViewHolder holder, int position){
        if (dataSet.get(position).isChecked()){
            holder.listTitle.setPaintFlags(holder.listTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.listTitle.setTextColor(context.getColor(R.color.gray));
            holder.conLayout2.setBackgroundColor(context.getColor(R.color.light_gray));
        }
        holder.finTask.setOnCheckedChangeListener((v, isChecked) ->{
            int newPos = holder.getAdapterPosition();
            titleKey = dataSet.get(newPos).getTitleKey();
            if(isChecked){
               updatePosition(holder);
//                holder.listTitle.setPaintFlags(holder.listTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                holder.listTitle.setTextColor(context.getColor(R.color.gray));
//                holder.conLayout2.setBackgroundColor(context.getColor(R.color.light_gray));
//                dataSet.get(position).setChecked(true);
                checked = true;
            }
            updateChkboxDB(titleKey);
        });
    }

    public void updatePosition(TodoChkListAdapter.ViewHolder holder){
        int newPosition = holder.getAdapterPosition();
        dataSet.remove(newPosition);
        notifyItemRemoved(newPosition);
//        notifyItemRangeChanged(newPosition, item.size());
    }

    public void updateChkboxDB(String titleKey){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userId = firebaseUser.getUid();
        mDatabase.child("TodoCheckList").child(userId).child(titleKey).child("checked").setValue(checked);
    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}
