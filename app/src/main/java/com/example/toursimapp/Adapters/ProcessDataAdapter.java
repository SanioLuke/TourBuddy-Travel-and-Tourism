package com.example.toursimapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.toursimapp.AllActivities.InnerCategoryActivity;
import com.example.toursimapp.Models.SearchItemModel;
import com.example.toursimapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

@SuppressWarnings("all")
public class ProcessDataAdapter extends RecyclerView.Adapter<ProcessDataAdapter.ListViewHolder> {

    Context context;
    ArrayList<SearchItemModel> processed_arr;

    public ProcessDataAdapter(Context context, ArrayList<SearchItemModel> processed_arr) {
        this.context = context;
        this.processed_arr = processed_arr;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewed_items, null);
        return new ListViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        holder.vieweditem_name.setText(processed_arr.get(position).getS_place_name());
        Glide.with(context).load(processed_arr.get(position).getS_place_img()).into(holder.vieweditem_img);

        holder.vieweditem_card.setOnClickListener(v -> {

            db.collection(processed_arr.get(position).getSdb_name())
                    .document(processed_arr.get(position).getSmain_place())
                    .collection("inner_places")
                    .document(processed_arr.get(position).getSinner_place())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @SuppressWarnings("unchecked")
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {
                                Bundle bundle = new Bundle();
                                bundle.putString("inner_place_id", task.getResult().getString("inner_place_id"));
                                bundle.putString("inner_place_name", task.getResult().getString("inner_place_name"));
                                bundle.putString("inner_rating", task.getResult().getString("inner_rating"));
                                bundle.putString("inner_description", task.getResult().getString("inner_description"));
                                bundle.putString("db_name", processed_arr.get(position).getSdb_name());
                                bundle.putString("main_place_id", processed_arr.get(position).getSmain_place());
                                bundle.putDouble("inner_longitude", (Double) task.getResult().get("longitude"));
                                bundle.putDouble("inner_latitude", (Double) task.getResult().get("latitude"));
                                bundle.putStringArrayList("inner_images", (ArrayList<String>) task.getResult().get("inner_images"));
                                bundle.putStringArrayList("inner_features", (ArrayList<String>) task.getResult().get("inner_features"));

                                Intent intent = new Intent(context, InnerCategoryActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("curr_innerdetails_bundle", bundle);
                                context.startActivity(intent);
                            }
                        }
                    });
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return processed_arr.size();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {

        CardView vieweditem_card;
        ImageView vieweditem_img;
        TextView vieweditem_name;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);

            vieweditem_card = itemView.findViewById(R.id.vieweditem_card);
            vieweditem_img = itemView.findViewById(R.id.vieweditem_img);
            vieweditem_name = itemView.findViewById(R.id.vieweditem_name);
        }
    }
}
