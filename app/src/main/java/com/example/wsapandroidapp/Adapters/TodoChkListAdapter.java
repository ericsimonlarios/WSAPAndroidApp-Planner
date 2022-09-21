package com.example.wsapandroidapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wsapandroidapp.Classes.DateTime;
import com.example.wsapandroidapp.DataModel.Todo;
import com.example.wsapandroidapp.DialogClasses.LoadingDialog;
import com.example.wsapandroidapp.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TodoChkListAdapter extends RecyclerView.Adapter<TodoChkListAdapter.ViewHolder>{
    private final List<Todo> dataSet;
    private final Context context;
    private Boolean addNew;
    private String userId;
    private TodoListItemAdapter todoListItemAdapter;
    LoadingDialog loadingDialog;
    DatabaseReference mDatabase, databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DateTime dateTime;
    Date date;
    private final String TAG = "Error";

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView listTitle, chkListDate;
        MaterialCardView listTitleCV;
        ConstraintLayout conLayout1, conLayout2, checkListItemsLayout;
        ImageView itemDisplayManager, menuItem;
        final ImageView imgView18;
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
            imgView18 = view.findViewById(R.id.imgView18);
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        addNew = false;
        List<Todo> todoList = new ArrayList<>();
        dateTime = new DateTime();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = firebaseUser.getUid();
        int globalPos = holder.getBindingAdapterPosition();
        Todo todo = dataSet.get(position);
        holder.listTitle.setText(dataSet.get(globalPos).getListTitle());
        holder.chkListDate.setText(dataSet.get(globalPos).getDateCreated());
        holder.itemDisplayManager.setTag(R.drawable.ic_baseline_arrow_drop_up_24);

        Resources res = context.getResources();

        holder.listTitleCV.setOnClickListener(view ->{
            dropDownManager(holder, res);
        });

        holder.itemDisplayManager.setOnClickListener(view ->{
            dropDownManager(holder, res);
        });

        loadingDialog = new LoadingDialog(context);

        holder.menuItem.setOnClickListener(view ->{
            PopupMenu popupMenu = new PopupMenu(context, holder.menuItem);
            popupMenu.getMenuInflater().inflate(R.menu.topbar_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                switch (id){
                    case R.id.deleteList:{
                        if(onOptionsListener != null) onOptionsListener.onDelete(todo);
                        break;
                    }
                    case R.id.editTitle:{
                        if(onOptionsListener != null) onOptionsListener.onEdit(todo, holder.getBindingAdapterPosition());
                        break;
                    }
                    case R.id.finished:{
                        databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("TodoChecklist").child(userId).child(todo.getTitleKey()).child("checked").setValue(true);
                        break;
                    }
                }
                return true;
            });
            popupMenu.show();

        });
        DateFormat formatDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        try {
            date = formatDate.parse(dateTime.getDateText());
            if(todo.getDateFormat().compareTo(date) == 0){
                holder.chkListDate.setTextColor(context.getColor(R.color.yellow));
            }else if (todo.getDateFormat().compareTo(date) > 0){
                holder.chkListDate.setTextColor(context.getColor(R.color.green));
            }else{
                holder.chkListDate.setTextColor(context.getColor(R.color.red));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


        holder.imgView18.setOnClickListener(v -> {
            addNew = true;
            getLatestList(holder, addNew, todo, todoList);
            Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.ic_baseline_arrow_drop_down_24, null);
            holder.itemDisplayManager.setImageDrawable(drawable);
            holder.checkListItemsLayout.setVisibility(View.VISIBLE);
            holder.itemDisplayManager.setTag(R.drawable.ic_baseline_arrow_drop_down_24);
        });

        loadingDialog.dismissDialog();
        getLatestList(holder, addNew, todo, todoList);
    }

    public void callAdapter(ViewHolder holder, Todo todo, List<Todo> todoList){

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        holder.checkListItems.setLayoutManager(linearLayoutManager);
        todoListItemAdapter = new TodoListItemAdapter(todoList, context, todo);
        holder.checkListItems.setAdapter(todoListItemAdapter);

    }

    public void dropDownManager(ViewHolder holder, Resources res){
        int integer = (Integer) holder.itemDisplayManager.getTag();
        switch (integer){
            case R.drawable.ic_baseline_arrow_drop_down_24:{
                Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.ic_baseline_arrow_drop_up_24, null);
                holder.itemDisplayManager.setImageDrawable(drawable);
                holder.checkListItemsLayout.setVisibility(View.GONE);
                holder.itemDisplayManager.setTag(R.drawable.ic_baseline_arrow_drop_up_24);
                break;
            }

            case R.drawable.ic_baseline_arrow_drop_up_24:{
                Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.ic_baseline_arrow_drop_down_24, null);
                holder.itemDisplayManager.setImageDrawable(drawable);
                holder.checkListItemsLayout.setVisibility(View.VISIBLE);
                holder.itemDisplayManager.setTag(R.drawable.ic_baseline_arrow_drop_down_24);
                break;
            }

        }
    }

    public void getLatestList(ViewHolder holder, Boolean addNew, Todo todo,List<Todo> todoList){
        if(todo.getTitleKey() != null || !todo.getTitleKey().equals("")){
            DatabaseReference getRef = mDatabase.child("TodoChecklistItems").child(userId);
            getRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        todoList.clear();
                        for(DataSnapshot node: snapshot.getChildren()) {
                            if(Objects.equals(node.getKey(), todo.getTitleKey())){
                                for (DataSnapshot nodeChild: node.getChildren()){
                                    List chkList = new ArrayList();
                                    Boolean checked;
                                    String titleKey = Objects.requireNonNull(nodeChild.child("titleKey").getValue()).toString();
                                    String listText = String.valueOf(nodeChild.child("listText").getValue());
                                    String getListKey = nodeChild.getKey();
                                    checked = (Boolean) nodeChild.child("checked").getValue();
                                    chkList.add(listText);
                                    chkList.add(checked);
                                    chkList.add(titleKey);
                                    chkList.add(getListKey);
                                    todoList.add(new Todo(chkList));

                                }
                            }
                        }
                        if(addNew){
                            List chkList = new ArrayList();
                            chkList.clear();
                            String listKey = mDatabase.child("TodoChecklistItems").push().getKey();
                            chkList.add("");
                            chkList.add(false);
                            chkList.add(todo.getTitleKey());
                            chkList.add(listKey);
                            todoList.add(new Todo(chkList));
                            todoListItemAdapter.notifyItemInserted(todoListItemAdapter.getItemCount()+1);
                        }
                    }
                    callAdapter(holder, todo, todoList);
                    loadingDialog.dismissDialog();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG, "loadPost:onCancelled", error.toException());
                    loadingDialog.dismissDialog();
                }
            });
        }
    }

    private onOptionsListener onOptionsListener;

    public interface onOptionsListener{
        void onEdit(Todo todo, int position);
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
