package com.example.toursimapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toursimapp.AllActivities.CategoryActivity;
import com.example.toursimapp.Models.CategoryModel;
import com.example.toursimapp.Models.homemain_rec_model;
import com.example.toursimapp.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HomeMainListAdapter extends RecyclerView.Adapter<HomeMainListAdapter.ListViewHolder> {

    private final Context context;
    private final ArrayList<homemain_rec_model> main_trip_list;

    public HomeMainListAdapter(Context context, ArrayList<homemain_rec_model> main_trip_list) {
        this.context = context;
        this.main_trip_list = main_trip_list;
    }

    @NonNull
    @Override
    public HomeMainListAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.hmaintripitems, null);
        return new ListViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeMainListAdapter.ListViewHolder holder, int position) {
        holder.main_trip_title.setText(main_trip_list.get(position).getTrip_names());
        setinner_rec(holder.trip_inner_rec, main_trip_list.get(position).getTrip_places_list());

        holder.main_trip_title.setOnClickListener(v -> {
            Intent intent = new Intent(context, CategoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("category_db_name", main_trip_list.get(position).getTrip_places_list().get(0).getDb_name());
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
        return main_trip_list.size();
    }

    private void setinner_rec(@NotNull RecyclerView recyclerView, ArrayList<CategoryModel> trip_inner_array) {
        HomeInnerListAdapter homeInnerListAdapter = new HomeInnerListAdapter(context, trip_inner_array);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(homeInnerListAdapter);

    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {

        TextView main_trip_title;
        RecyclerView trip_inner_rec;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            main_trip_title = itemView.findViewById(R.id.main_trip_title);
            trip_inner_rec = itemView.findViewById(R.id.trip_inner_rec);
        }
    }
}
