package com.example.wsapandroidapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wsapandroidapp.Classes.ComponentManager;
import com.example.wsapandroidapp.DataModel.ToDoChecklist;
import com.example.wsapandroidapp.DataModel.Todo;
import com.example.wsapandroidapp.R;
import com.example.wsapandroidapp.TodoChecklistActivity;
import com.example.wsapandroidapp.TodoListItemActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.List;
import java.util.Objects;

public class TodoChkListAdapter extends RecyclerView.Adapter<TodoChkListAdapter.ViewHolder> {
    private final List<Todo> dataSet;
    private final List<ToDoChecklist> chkList;
    private final Context context;
    private Boolean checked, addNew;
    private TodoListItemAdapter todoListItemAdapter;
    private int globalPos;
    private String userId;
    DatabaseReference mDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView listTitle, chkListDate;
        MaterialCardView listTitleCV;
        ConstraintLayout conLayout1, conLayout2, checkListItemsLayout, constraintLayout14;
        ImageView itemDisplayManager, menuItem;
        RecyclerView checkListItems;
        public ViewHolder(View view){
            super(view);

            listTitle = view.findViewById(R.id.listTitle);
            chkListDate = view.findViewById(R.id.chkListDate);
            listTitleCV = view.findViewById(R.id.listTitleCV);
            conLayout1 = view.findViewById(R.id.conLayout1);
            conLayout2 = view.findViewById(R.id.conLayout2);
            itemDisplayManager = view.findViewById(R.id.itemDisplayManager);
            menuItem = view.findViewById(R.id.menuItem);
            checkListItemsLayout = view.findViewById(R.id.checkListItemsLayout);
            checkListItems = view.findViewById(R.id.checkListItems);
            constraintLayout14 = view.findViewById(R.id.constraintLayout14);
        }
    }

    public TodoChkListAdapter(Context context, List<Todo> dataSet, List<ToDoChecklist> chkList) {
        this.dataSet = dataSet;
        this.context = context;
        this.checked = false;
        this.chkList = chkList;
    }

    @NonNull
    @Override
    public TodoChkListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_todo_checklist_title, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        addNew = false;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = firebaseUser.getUid();

        int newPos = holder.getAdapterPosition();
        Todo todo = dataSet.get(position);
        holder.listTitle.setText(dataSet.get(newPos).getListTitle());
        holder.chkListDate.setText(dataSet.get(newPos).getDateCreated());
        holder.itemDisplayManager.setOnClickListener(view ->{
            ComponentManager componentManager = new ComponentManager(context);
            checked = componentManager.itemDisplayManager(checked, holder.itemDisplayManager, holder.checkListItemsLayout);
        });

        holder.menuItem.setOnClickListener(view ->{
            PopupMenu popupMenu = new PopupMenu(context, holder.menuItem);
            popupMenu.getMenuInflater().inflate(R.menu.topbar_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    switch (id){
                        case R.id.deleteList:{
                            if(onOptionsListener != null) onOptionsListener.onDelete(todo);
                            return true;
                        }
                        case R.id.markDone:{
                            Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        case R.id.editTitle:{
                            if(onOptionsListener != null) onOptionsListener.onEdit(todo);
                        }
                    }
                    return true;
                }
            });
            popupMenu.show();

        });

        holder.constraintLayout14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNew = true;
                callAdapter(holder, addNew, todo);
            }
        });

        callAdapter(holder, addNew, todo);

    }

    public void callAdapter(ViewHolder holder, Boolean addNew, Todo todo){
        globalPos = holder.getBindingAdapterPosition();
        TodoListItemAdapter todoListItemAdapter = new TodoListItemAdapter(chkList, context, todo);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        holder.checkListItems.setLayoutManager(linearLayoutManager);
        holder.checkListItems.setAdapter(todoListItemAdapter);

        if(todoListItemAdapter.getItemCount() == 0){
            String listKey = mDatabase.child("TodoChecklist").push().getKey();
            chkList.add(new ToDoChecklist("", false, listKey));
            todoListItemAdapter.notifyItemInserted(todoListItemAdapter.getItemCount()+1);
        }

        if(addNew){
            if(todoListItemAdapter.getItemCount()!= 0){
                String listKey = mDatabase.child("TodoChecklist").push().getKey();
                chkList.add(new ToDoChecklist("", false, listKey));
                todoListItemAdapter.notifyItemInserted(todoListItemAdapter.getItemCount()+1);
            }
        }
    }

    public void getLatestList(ViewHolder holder){
        String getTitleKey = dataSet.get(holder.getBindingAdapterPosition()).getTitleKey();
        if(getTitleKey != null || !getTitleKey.equals("")){
            DatabaseReference getRef = mDatabase.child("TodoChecklist").child(userId).child(getTitleKey).child("TodoListItem");
            getRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private onOptionsListener onOptionsListener;

    public interface onOptionsListener{
        void onEdit(Todo todo);
        void onDelete(Todo todo);
    }

    public void setOnOptionsListener(TodoChkListAdapter.onOptionsListener onOptionsListener){
        this.onOptionsListener = onOptionsListener;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
