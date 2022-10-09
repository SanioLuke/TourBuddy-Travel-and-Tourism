package com.example.toursimapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.toursimapp.AllActivities.HotelActivity;
import com.example.toursimapp.Models.HotelDetailModel;
import com.example.toursimapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class HotelsAdpater extends RecyclerView.Adapter<HotelsAdpater.ListViewHolder> {

    private final Context context;
    private final ArrayList<HotelDetailModel> hotel_array;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    public HotelsAdpater(Context context, ArrayList<HotelDetailModel> hotel_array) {
        this.context = context;
        this.hotel_array = hotel_array;
    }

    @NonNull
    @Override
    public HotelsAdpater.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.hotel_items, null);
        return new ListViewHolder(inflate);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull HotelsAdpater.ListViewHolder holder, int position) {

        DecimalFormat formatter = new DecimalFormat("#,##,###");

        final float[] allOrdersSize = {0};
        final float[] thisHotelOrdersSize = {0};
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        holder.hotel_item_place_name.setText(hotel_array.get(position).getHotel_name());
        holder.hotel_item_price.setText(String.format("₹ %s /night", formatter.format(hotel_array.get(position).getInitial_price())));
        holder.hotel_rate.setText(hotel_array.get(position).getHotel_rate());
        holder.hotel_rate_bar.setRating(Float.parseFloat(hotel_array.get(position).getHotel_rate()));
        Glide.with(context).load(hotel_array.get(position).getHotel_img().get(0)).into(holder.inner_trip_img);

        holder.hotel_item_lay.setOnClickListener(v -> db.collection("allOrders")
                .whereEqualTo("main_place_id", hotel_array.get(position).getMain_place_id())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        allOrdersSize[0] = task.getResult().size();
                    } else {
                        allOrdersSize[0] = 0;
                    }
                    db.collection("allOrders")
                            .whereEqualTo("main_place_id", hotel_array.get(position).getMain_place_id())
                            .whereEqualTo("hotel_id", hotel_array.get(position).getHotel_id())
                            .get()
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    thisHotelOrdersSize[0] = Objects.requireNonNull(task1.getResult()).size();
                                } else {
                                    thisHotelOrdersSize[0] = 0;
                                }

                                Intent intent = new Intent(context, HotelActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("allOrders", allOrdersSize[0]);
                                intent.putExtra("thisHotelOrders", thisHotelOrdersSize[0]);
                                intent.putExtra("par_hotel_details_bundle", hotel_array.get(position));
                                context.startActivity(intent);
                            });
                }));
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
        return hotel_array.size();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {

        View hotel_item_lay;
        ImageView inner_trip_img;
        TextView hotel_rate;
        RatingBar hotel_rate_bar;
        TextView hotel_item_place_name, hotel_item_price;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);

            hotel_item_lay = itemView.findViewById(R.id.hotel_item_lay);
            inner_trip_img = itemView.findViewById(R.id.inner_trip_img);
            hotel_rate = itemView.findViewById(R.id.hotel_rate);
            hotel_rate_bar = itemView.findViewById(R.id.hotel_rate_bar);
            hotel_item_place_name = itemView.findViewById(R.id.hotel_item_place_name);
            hotel_item_price = itemView.findViewById(R.id.hotel_item_price);
        }
    }
}
