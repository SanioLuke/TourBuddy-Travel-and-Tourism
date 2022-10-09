package com.example.toursimapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.toursimapp.AllActivities.MainPlaceActivity;
import com.example.toursimapp.Models.CategoryModel;
import com.example.toursimapp.R;

import java.util.ArrayList;

public class HomeInnerListAdapter extends RecyclerView.Adapter<HomeInnerListAdapter.ListViewHolder> {

    private final Context context;
    private final ArrayList<CategoryModel> inner_list;

    public HomeInnerListAdapter(Context context, ArrayList<CategoryModel> inner_list) {
        this.context = context;
        this.inner_list = inner_list;
    }


    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.hinnertripitems, null);
        return new ListViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        holder.inner_trip_title.setText(inner_list.get(position).getPlace_name());
        Glide.with(holder.itemView)
                .load(inner_list.get(position).getFirstImage())
                .fitCenter()
                .into(holder.inner_trip_img);

        holder.inner_trip_lay.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("curr_place_id", inner_list.get(position).getPlace_id());
            bundle.putString("curr_place_name", inner_list.get(position).getPlace_name());
            bundle.putString("curr_description", inner_list.get(position).getDescription());
            bundle.putString("curr_attractions", inner_list.get(position).getAttractions());
            bundle.putString("curr_besttime", inner_list.get(position).getBest_time());
            bundle.putString("curr_rate_place", inner_list.get(position).getRate_place());
            bundle.putString("curr_climate", inner_list.get(position).getClimate());
            bundle.putString("curr_reach_method", inner_list.get(position).getReach_method());
            bundle.putDouble("curr_longitude", inner_list.get(position).getLongitude());
            bundle.putDouble("curr_latitude", inner_list.get(position).getLatitude());
            bundle.putStringArrayList("curr_array_images", inner_list.get(position).getArray_images());
            bundle.putString("curr_db_name", inner_list.get(position).getDb_name());

            Intent intent = new Intent(context, MainPlaceActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("curr_details_bundle", bundle);

            context.startActivity(intent);
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
        return inner_list.size();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView inner_trip_title;
        ImageView inner_trip_img;
        RelativeLayout inner_trip_lay;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            inner_trip_title = itemView.findViewById(R.id.inner_trip_title);
            inner_trip_img = itemView.findViewById(R.id.inner_trip_img);
            inner_trip_lay = itemView.findViewById(R.id.inner_trip_lay);
            this.itemView = itemView;
        }
    }
}
