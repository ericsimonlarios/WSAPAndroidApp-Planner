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
import androidx.recyclerview.widget.RecyclerView;

import com.example.wsapandroidapp.DataModel.ToDoChecklist;
import com.example.wsapandroidapp.DataModel.Todo;
import com.example.wsapandroidapp.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodoListItemAdapter extends RecyclerView.Adapter<TodoListItemAdapter.ViewHolder> {

    private List<Todo> item;
    private String titleKey;
    private Context context;
    private List<String> arrayKey;

    DatabaseReference mDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    public TodoListItemAdapter(List<Todo> item, Context context, String titleKey, List<String> arrayKey){
        this.item = item;
        this.context = context;
        this.titleKey = titleKey;
        this.arrayKey = arrayKey;
    }

    public TodoListItemAdapter(List<Todo> item, Context context, String titleKey){
        this.item = item;
        this.context = context;
        this.titleKey = titleKey;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        EditText chklistItem;
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
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        String userId = firebaseUser.getUid();
        editTextListener(holder, position);
        chkBoxListener(holder, position);
        holder.clearTask.setOnClickListener(v-> holder.chklistItem.getText().clear());
        holder.chklistItem.setText((CharSequence) item.get(position).getChecklist().get(0));
        holder.chklistItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String listName = holder.chklistItem.getText().toString();
                CharSequence getID = (CharSequence) item.get(position).getChecklist().get(2);

                String getKey = getID.toString();
                HashMap<String, Object> result = new HashMap<>();
                result.put("listName", listName);
                result.put("isChecked", "true");
                result.put("titleKey", gettitleKey());
                mDatabase.child("TodoListItem").child(userId).child(getKey).updateChildren(result);
            }
        });
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

    public String gettitleKey() {
        return titleKey;
    }

    public void settitleKey(String titleKey) {
        this.titleKey = titleKey;
    }

    public List<String> getArrayKeyKey() {
        return arrayKey;
    }

    public void setArrayKeyy(List<String> arrayKey) {
        this.arrayKey = arrayKey;
    }
}
