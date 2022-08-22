package com.example.wsapandroidapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wsapandroidapp.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class TodoChkListAdapter extends RecyclerView.Adapter<TodoChkListAdapter.ViewHolder> {
    private List<String> dataSet;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView listTitle, chkListDate;
        RecyclerView listRV;
        CardView listTitleCV, listCV;
        ConstraintLayout conLayout1, conLayout2;

        public ViewHolder(View view){
            super(view);

            listTitle = view.findViewById(R.id.listTitle);
            chkListDate = view.findViewById(R.id.chkListDate);
//          listRV = view.findViewById(R.id.listRV);
            listTitleCV = view.findViewById(R.id.listTitleCV);
//          listCV = view.findViewById(R.id.listCV);
            conLayout1 = view.findViewById(R.id.conLayout1);
            conLayout2 = view.findViewById(R.id.conLayout2);
        }
    }

    public TodoChkListAdapter(Context context, List<String> dataSet) {
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
        holder.listTitle.setText(dataSet.get(0));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}
