package com.example.wsapandroidapp.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wsapandroidapp.DataModel.Todo;
import com.example.wsapandroidapp.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class TodoListItemAdapter extends RecyclerView.Adapter<TodoListItemAdapter.ViewHolder> {

    private final List<Todo> item;
    private final Context context;
    private String getKey;
    private String listName;
    private boolean checked;
    private final Todo todo;
    DatabaseReference mDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    public TodoListItemAdapter(List item, Context context, Todo todo){
        this.item = item;
        this.context = context;
        this.todo = todo;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        EditText chklistItem;
        CheckBox chkBoxList;
        ConstraintLayout conLayout1;
        ImageView clearTask;
        MaterialCardView addTask, chkListCard;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chklistItem = itemView.findViewById(R.id.chklistItem);
            chkBoxList = itemView.findViewById(R.id.chkBoxList);
            conLayout1 = itemView.findViewById(R.id.conLayout1);
            clearTask = itemView.findViewById(R.id.clearTask);
            addTask = itemView.findViewById(R.id.addTask);
            chkListCard = itemView.findViewById(R.id.chkListCard);
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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String userId = firebaseUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("TodoChecklistItems").child(userId).child(todo.getTitleKey());

        int newPos = holder.getBindingAdapterPosition();

        checked = Boolean.parseBoolean(String.valueOf(item.get(newPos).getChecklist().get(1)));
        String listItemName = String.valueOf(item.get(newPos).getChecklist().get(0));
        holder.chklistItem.setText(listItemName);

        editTextListener(holder);

        chkBoxListener(holder);
        holder.clearTask.setOnClickListener(v-> removeTask(holder));
        holder.chklistItem.addTextChangedListener(onTextChanged(holder));
    }

    public TextWatcher onTextChanged(ViewHolder holder){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                listName = holder.chklistItem.getText().toString();
                updateListItem(holder);
            }
        };
    }
    @Override
    public int getItemCount() {
        return item.size();
    }

    public void updateListItem(ViewHolder holder){
        getKey = String.valueOf(item.get(holder.getBindingAdapterPosition()).getChecklist().get(3));
        chkBoxListener(holder);
        HashMap<String, Object> result = new HashMap<>();
        result.put("listText", listName);
        result.put("checked", checked);
        result.put("titleKey", todo.getTitleKey());
        mDatabase.child(getKey).updateChildren(result);
    }
    public void removeTask(ViewHolder holder){
        getKey = String.valueOf(item.get(holder.getBindingAdapterPosition()).getChecklist().get(3));
        DatabaseReference deleteID = mDatabase.child(getKey);
        deleteID.getRef().removeValue();
        updatePosition(holder);
    }

    public void updatePosition(ViewHolder holder){
        int newPosition = holder.getBindingAdapterPosition();
        item.remove(newPosition);
        notifyItemRemoved(newPosition);
        Toast.makeText(context, "Task Deleted", Toast.LENGTH_SHORT).show();
    }

    public void editTextListener(@NonNull ViewHolder holder){
        holder.chklistItem.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                holder.clearTask.setVisibility(View.GONE);
            }else{
                holder.clearTask.setVisibility(View.VISIBLE);
            }
        });
    }

    public void chkBoxListener(@NonNull ViewHolder holder){
        listName = holder.chklistItem.getText().toString();
        if(todo.isChecked()){
            holder.chkBoxList.setChecked(true);
            holder.chklistItem.setPaintFlags(holder.chklistItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.chklistItem.setTextColor(context.getColor(R.color.gray));
            holder.chkListCard.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.light_gray));
            holder.chklistItem.setEnabled(false);
        }
        if(checked){
            holder.chkBoxList.setChecked(checked);
            holder.chklistItem.setPaintFlags(holder.chklistItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.chklistItem.setTextColor(context.getColor(R.color.gray));
            holder.chkListCard.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.light_gray));
            holder.chklistItem.setEnabled(false);
        }
        holder.chkBoxList.setOnCheckedChangeListener((v, isChecked) ->{
            if(isChecked){
                holder.chklistItem.setPaintFlags(holder.chklistItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.chklistItem.setTextColor(context.getColor(R.color.gray));
                holder.chkListCard.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.light_gray));
                holder.chklistItem.setEnabled(false);
                checked = true;
            }else{
                holder.chklistItem.setPaintFlags(holder.chklistItem.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG);
                holder.chklistItem.setTextColor(context.getColor(R.color.black));
                holder.chkListCard.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.yellow));
                holder.chklistItem.setEnabled(true);
                checked = false;
            }
            updateListItem(holder);
        });
    }
}
