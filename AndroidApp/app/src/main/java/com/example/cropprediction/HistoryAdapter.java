package com.example.cropprediction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<com.example.cropprediction.HistoryAdapter.ViewHolder> {

    private Context context;
    private List<HistoryDetails> history_crop_details;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Farmers");

    public HistoryAdapter(Context context, List<com.example.cropprediction.HistoryDetails> history_crop_details){
        this.context = context;
        this.history_crop_details = history_crop_details;
    }


    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_history_layout, parent, false);
        HistoryAdapter.ViewHolder viewHolder = new HistoryAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {

        com.example.cropprediction.HistoryDetails hist_crops = history_crop_details.get(position);
        holder.textViewName.setText(hist_crops.getHistoryCropName());
        Glide.with(context).load(hist_crops.getHistoryImage()).into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return history_crop_details.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.history_crop);
            imageView = itemView.findViewById(R.id.history_image);
            }
    }

}
