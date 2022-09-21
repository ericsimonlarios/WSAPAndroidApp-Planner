package com.example.wsapandroidapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wsapandroidapp.DataModel.WeddingTips;
import com.example.wsapandroidapp.DialogClasses.ConfirmationDialog;
import com.example.wsapandroidapp.ImageActivity;
import com.example.wsapandroidapp.R;
import com.example.wsapandroidapp.WeddingTipsFormActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdminWeddingTipsAdapter extends RecyclerView.Adapter<AdminWeddingTipsAdapter.ViewHolder>{
    
    private final List<WeddingTips> weddingTips;
    private final LayoutInflater layoutInflater;
    private List<ArrayList> tipsImagesList;
    private ConfirmationDialog confirmationDialog;
    private final Context context;
    private int getPos;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private boolean isDeleted;
    private int counter;
    public AdminWeddingTipsAdapter(Context context, List<WeddingTips> weddingTips, List<ArrayList>tipsImagesList) {
        this.tipsImagesList = tipsImagesList;
        this.weddingTips = weddingTips;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }
    public AdminWeddingTipsAdapter(Context context, List<WeddingTips> weddingTips) {
        this.weddingTips = weddingTips;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }
    @NonNull
    @Override
    public AdminWeddingTipsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.custom_admin_exhibitor_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminWeddingTipsAdapter.ViewHolder holder, int position) {
        TextView tvTipTitle = holder.tvWeddingTips,
                tvDescription = holder.tvDescription;
        ImageView imgPoster = holder.imgPoster,
                imgMoreInfo = holder.imgMoreInfo,
                imgUpdate = holder.imgUpdate,
                imgDelete = holder.imgDelete,
                imgMove = holder.imgMove;

        imgMoreInfo.setVisibility(View.GONE);
        imgMove.setVisibility(View.GONE);
        WeddingTips weddingTip = weddingTips.get(position);
        tvTipTitle.setText(weddingTip.getTopic());
        tvDescription.setText(weddingTip.getDescription());
        if(tipsImagesList.get(position).get(0).toString() != null){
            Glide.with(context).load(tipsImagesList.get(position).get(0).toString()).centerCrop().placeholder(R.drawable.ic_wsap).
                    error(R.drawable.ic_wsap).into(imgPoster);
        }
        imgPoster.setOnClickListener(view -> {
            Intent intent = new Intent(context, ImageActivity.class);
            intent.putExtra("image", tipsImagesList.get(position).get(0).toString());
            context.startActivity(intent);
        });

        imgUpdate.setOnClickListener(view -> {
            Intent intent = new Intent(context, WeddingTipsFormActivity.class);
            intent.putExtra("weddingTipsId", weddingTip.getId());
            context.startActivity(intent);
        });
        confirmationDialog = new ConfirmationDialog(context);
        imgDelete.setOnClickListener(view -> {
//            if (adapterListener != null) adapterListener.onDelete(weddingTip, tipsImagesList.get(position));
            getPos = holder.getBindingAdapterPosition();
            confirmationDialog.setMessage(context.getString(R.string.confirmation_prompt, "delete the topic"));
            confirmationDialog.showDialog();
        });

        confirmationDialog.setDialogListener(() -> {
            isDeleted = false;
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseStorage = FirebaseStorage.getInstance();
            databaseReference = firebaseDatabase.getReference();
            if(weddingTips.get(getPos).getId() != null){
                counter = 0;
                for(int i = 0; tipsImagesList.get(getPos).size() > i; i++){
                    String deleteImg = tipsImagesList.get(getPos).get(i).toString();
                    storageReference = firebaseStorage.getReference().getStorage().getReferenceFromUrl(deleteImg);
                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Delete Failed", Toast.LENGTH_SHORT).show();
                            confirmationDialog.dismissDialog();
                        }
                    });
                }
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                databaseReference.child("weddingTips").child(weddingTips.get(getPos).getId()).removeValue();
                confirmationDialog.dismissDialog();
            }else{
                Toast.makeText(context, "Error Deleting", Toast.LENGTH_SHORT).show();
                confirmationDialog.dismissDialog();
            }

        });

    }

    @Override
    public int getItemCount() {
        return weddingTips.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvWeddingTips, tvDescription;
        ImageView imgPoster, imgMoreInfo, imgUpdate, imgDelete, imgMove;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvWeddingTips = itemView.findViewById(R.id.tvExhibitor);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            imgMoreInfo = itemView.findViewById(R.id.imgMoreInfo);
            imgUpdate = itemView.findViewById(R.id.imgUpdate);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            imgMove = itemView.findViewById(R.id.imgMove);
            imgPoster = itemView.findViewById(R.id.imgPoster);
            
        }
    }
    private AdapterListener adapterListener;

    public interface AdapterListener {
        void onEdit(WeddingTips weddingTip);
        void onDelete(WeddingTips weddingTips, List<String> tipsImagesList);
    }

    public void setAdapterListener(AdapterListener adapterListener) {
        this.adapterListener = adapterListener;
    }


}
