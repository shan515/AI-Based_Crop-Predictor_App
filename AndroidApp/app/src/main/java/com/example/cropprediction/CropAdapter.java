package com.example.cropprediction;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CropAdapter extends RecyclerView.Adapter<com.example.cropprediction.CropAdapter.ViewHolder> {

    private Context context;
    private List<com.example.cropprediction.CropDetails> crop_details;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Farmers");

    public CropAdapter(Context context, List<com.example.cropprediction.CropDetails> crop_details){
        this.context = context;
        this.crop_details = crop_details;
    }

    @NonNull
    @Override
    public com.example.cropprediction.CropAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_grid_predicted_crops, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.cropprediction.CropAdapter.ViewHolder holder, int position) {

        com.example.cropprediction.CropDetails crops = crop_details.get(position);
        holder.textViewName.setText(crops.getCropName());
        holder.percent.setText("" + crops.getCropPercent());
        Glide.with(context).load(crops.getImageURL()).into(holder.imageView);
        holder.image_progress.setVisibility(View.GONE);

        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crop_details.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(), crop_details.size());
            }
        });

        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap<Object, String> params = new HashMap<Object, String>();
                        params.put(ServerValue.TIMESTAMP, crops.getCropName().toString());
                        ref.child(crops.getCropName()).setValue(0);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                Toast.makeText(holder.itemView.getContext(), "Added to firebase!", Toast.LENGTH_LONG).show();
            }
        });

        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pdf_url = crops.getPdfLink();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(pdf_url));
                holder.itemView.getContext().startActivity(i);
            }
        });
        System.out.println(crops.getSeason());
    }

    @Override
    public int getItemCount() {
        return crop_details.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName;
        public ImageView imageView;
        public TextView percent;
        public ProgressBar image_progress;
        public Button downloadButton, cancelButton, acceptButton;


        public ViewHolder(View itemView) {
            super(itemView);

            image_progress = itemView.findViewById(R.id.progressbar);
            textViewName = itemView.findViewById(R.id.crop_name);
            imageView = itemView.findViewById(R.id.crop_image);
            percent = itemView.findViewById(R.id.crop_percentage);
            downloadButton = itemView.findViewById(R.id.button_download_pdf);
            cancelButton = itemView.findViewById(R.id.button_no);
            acceptButton = itemView.findViewById(R.id.button_yes);
        }
    }
}
