package com.example.wsapandroidapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.media.Image;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.wsapandroidapp.Classes.ComponentManager;
import com.example.wsapandroidapp.DataModel.Todo;
import com.example.wsapandroidapp.R;
import com.example.wsapandroidapp.TodoListItemActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class TodoFinishedTaskAdapter extends RecyclerView.Adapter<TodoFinishedTaskAdapter.ViewHolder>{

    private final List<Todo> dataSet;
    private final Context context;
    private String titleKey;
    private Boolean checked;
    DatabaseReference mDatabase;

    public TodoFinishedTaskAdapter(List<Todo> dataSet, Context context) {
        this.dataSet = dataSet;
        this.context = context;
        this.checked = false;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView listTitle, chkListDate;
        MaterialCardView listTitleCV;
        ConstraintLayout conLayout1, conLayout2, checkListItemsLayout;
        ImageView itemDisplayManager, menuItem;
        public ViewHolder(View view){
            super(view);

            listTitle = view.findViewById(R.id.listTitle);
            chkListDate = view.findViewById(R.id.chkListDate);
            listTitleCV = view.findViewById(R.id.listTitleCV);
            conLayout1 = view.findViewById(R.id.conLayout1);
            conLayout2 = view.findViewById(R.id.conLayout2);
            itemDisplayManager = view.findViewById(R.id.itemDisplayManager);
            checkListItemsLayout = view.findViewById(R.id.checkListItemsLayout);
            menuItem = view.findViewById(R.id.menuItem);
        }
    }

    @NonNull
    @Override
    public TodoFinishedTaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_todo_checklist_title, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoFinishedTaskAdapter.ViewHolder holder, int position) {
        int newPos = holder.getAdapterPosition();
        ComponentManager componentManager = new ComponentManager(context);

        holder.listTitle.setText(dataSet.get(newPos).getListTitle());
        holder.chkListDate.setText(dataSet.get(newPos).getDateCreated());
//        holder.itemDisplayManager.setChecked(dataSet.get(newPos).isChecked());
//        holder.listTitleCV.setOnClickListener(v -> {
//            Intent intent = new Intent(context, TodoListItemActivity.class);
//            intent.putExtra("id", dataSet.get(newPos).getTitleKey());
//            intent.putExtra("listTitle", dataSet.get(newPos).getListTitle());
//            intent.putExtra("checked", dataSet.get(newPos).isChecked());
//            context.startActivity(intent);
//            ((Activity)context).finish();
//        });
//        chkboxManager(holder, position);
        holder.itemDisplayManager.setOnClickListener(view ->{
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
//                            if(isNew){
//                                Intent intent = new Intent(TodoListItemActivity.this, TodoChecklistActivity.class);
//                                startActivity(intent);
//                            }
//                            DatabaseReference getId = mDatabase.child("TodoListItem").child(userId);
//                            getId.addListenerForSingleValueEvent(deleteList());
//                            getId = mDatabase.child("TodoCheckList").child(userId);
//                            getId.addListenerForSingleValueEvent(deleteTitle());
                            Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        case R.id.markDone:{
//                            if(!isNew){
//                                mDatabase.child("TodoCheckList").child(userId).child(passedKey).child("checked").setValue(true);
//                                Intent intent = new Intent(TodoListItemActivity.this, TodoChecklistActivity.class);
//                                startActivity(intent);
//                            }else{
//                                Toast.makeText(this, "Enter a title first", Toast.LENGTH_SHORT).show();
//                            }
                            Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    }
                    return true;
                }
            });
            popupMenu.show();

        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
//
//    public void chkboxManager(TodoFinishedTaskAdapter.ViewHolder holder, int position){
//        if (dataSet.get(position).isChecked()){
//            holder.listTitle.setPaintFlags(holder.listTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//            holder.listTitle.setTextColor(context.getColor(R.color.gray));
//            holder.conLayout2.setBackgroundColor(context.getColor(R.color.light_gray));
//        }
//        holder.itemDisplayManager.setOnCheckedChangeListener((v, isChecked) ->{
//            int newPos = holder.getAdapterPosition();
//            titleKey = dataSet.get(newPos).getTitleKey();
//            if(isChecked){
//                holder.listTitle.setPaintFlags(holder.listTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                holder.listTitle.setTextColor(context.getColor(R.color.gray));
//                holder.conLayout2.setBackgroundColor(context.getColor(R.color.light_gray));
//                checked = true;
//            }else{
//                updatePosition(holder);
//                checked = false;
//            }
//            updateChkboxDB(titleKey);
//        });
//    }

    public void updatePosition(TodoFinishedTaskAdapter.ViewHolder holder){
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

}
