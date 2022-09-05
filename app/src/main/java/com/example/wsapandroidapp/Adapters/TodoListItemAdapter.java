package com.example.wsapandroidapp.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class TodoListItemAdapter extends RecyclerView.Adapter<TodoListItemAdapter.ViewHolder> {

    private static final String TAG = "Error";
    private final List<Todo> item;
    private final String titleKey;
    private final Context context;
    private CharSequence getID;
    private String getKey, userId;
    DatabaseReference mDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

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
        userId = firebaseUser.getUid();
        editTextListener(holder, position);
        chkBoxListener(holder, position);
        holder.clearTask.setOnClickListener(v-> removeTask(holder));
        holder.chklistItem.setText((CharSequence) item.get(position).getChecklist().get(0));
        holder.chklistItem.addTextChangedListener(onTextChanged(holder, position, userId));
    }

    public TextWatcher onTextChanged(ViewHolder holder, int position, String userId){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int newPosition = holder.getAdapterPosition();
                String listName = holder.chklistItem.getText().toString();
                getID = (CharSequence) item.get(newPosition).getChecklist().get(2);
                getKey = getID.toString();
                HashMap<String, Object> result = new HashMap<>();
                result.put("listName", listName);
                result.put("isChecked", "true");
                result.put("titleKey", gettitleKey());
                mDatabase.child("TodoListItem").child(userId).child(getKey).updateChildren(result);
            }
        };
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public void removeTask(ViewHolder holder){
        int newPosition = holder.getAdapterPosition();
        getID = (CharSequence) item.get(newPosition).getChecklist().get(2);
        getKey = getID.toString();
        if(getKey != null){
            DatabaseReference deleteID = mDatabase.child("TodoListItem").child(userId);
            deleteID.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for (DataSnapshot node: snapshot.getChildren()){
                            String listId = node.getRef().getKey();
                            if(listId.equals(getKey)){
                                node.getRef().removeValue();
                                updatePosition(holder);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG, "loadPost:onCancelled", error.toException());
                }
            });
        }else{
            updatePosition(holder);
        }
    }

    public void updatePosition(ViewHolder holder){
        int newPosition = holder.getAdapterPosition();
        item.remove(newPosition);
        notifyItemRemoved(newPosition);
//        notifyItemRangeChanged(newPosition, item.size());
        Toast.makeText(context, "Task Deleted", Toast.LENGTH_SHORT).show();
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

    public List<Todo> getItem() {
        return item;
    }
}
