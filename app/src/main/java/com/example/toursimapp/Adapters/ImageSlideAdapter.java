package com.example.toursimapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.toursimapp.AllActivities.InnerCategoryActivity;
import com.example.toursimapp.AllActivities.MainPlaceActivity;
import com.example.toursimapp.AllActivities.ViewImageActivity;
import com.example.toursimapp.Models.ImageSliderModel;
import com.example.toursimapp.Models.SearchItemModel;
import com.example.toursimapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smarteist.autoimageslider.SliderViewAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@SuppressWarnings("all")
public class ImageSlideAdapter extends SliderViewAdapter<ImageSlideAdapter.SliderAdapterVH> {

    private final Context context;
    private final int pos;
    private ArrayList<ImageSliderModel> mSliderItems = new ArrayList<>();
    private ArrayList<SearchItemModel> threndPosterItems = new ArrayList<>();
    private FirebaseFirestore db;

    public ImageSlideAdapter(Context context, int pos) {
        this.context = context;
        this.pos = pos;
    }

    public ImageSlideAdapter(Context context, ArrayList<SearchItemModel> threndPosterItems, int pos) {
        this.context = context;
        this.pos = pos;
        this.threndPosterItems = threndPosterItems;
    }

    public void renewItems(ArrayList<ImageSliderModel> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(@NotNull ViewGroup parent) {
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        if (pos == 0) {

            db = FirebaseFirestore.getInstance();
            viewHolder.textViewDescription.setText(threndPosterItems.get(position).getS_place_name());
            Glide.with(viewHolder.itemView)
                    .load(threndPosterItems.get(position).getS_place_img())
                    .fitCenter()
                    .into(viewHolder.imageViewBackground);

            viewHolder.itemView.setOnClickListener(v -> {

                String search_db_name = threndPosterItems.get(position).getSdb_name();
                String search_main_place = threndPosterItems.get(position).getSmain_place();
                String search_inner_place = threndPosterItems.get(position).getSinner_place();

                if (search_db_name != null && search_main_place != null && search_inner_place == null) {
                    db.collection(search_db_name)
                            .document(search_main_place)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.e("search_to_page", "success");

                                    Bundle bundle = new Bundle();
                                    bundle.putString("curr_place_id", task.getResult().getString("place_id"));
                                    bundle.putString("curr_place_name", task.getResult().getString("place_name"));
                                    bundle.putString("curr_description", task.getResult().getString("description"));
                                    bundle.putString("curr_attractions", task.getResult().getString("attractions"));
                                    bundle.putString("curr_besttime", task.getResult().getString("best_time"));
                                    bundle.putString("curr_rate_place", task.getResult().getString("rate_place"));
                                    bundle.putString("curr_climate", task.getResult().getString("climate"));
                                    bundle.putString("curr_reach_method", task.getResult().getString("reach_method"));
                                    bundle.putDouble("curr_longitude", (Double) task.getResult().get("longitude"));
                                    bundle.putDouble("curr_latitude", (Double) task.getResult().get("latitude"));
                                    bundle.putStringArrayList("curr_array_images", (ArrayList<String>) task.getResult().get("images"));
                                    bundle.putString("curr_db_name", search_db_name);

                                    Intent intent = new Intent(context, MainPlaceActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("curr_details_bundle", bundle);
                                    context.startActivity(intent);
                                } else {
                                    Log.e("fav_to_page", "failed");
                                }
                            });
                }
                if (search_db_name != null && search_main_place != null && search_inner_place != null) {
                    db.collection(search_db_name)
                            .document(search_main_place)
                            .collection("inner_places")
                            .document(search_inner_place)
                            .get()
                            .addOnCompleteListener(task -> {
                                Bundle bundle = new Bundle();
                                bundle.putString("inner_place_id", task.getResult().getString("inner_place_id"));
                                bundle.putString("inner_place_name", task.getResult().getString("inner_place_name"));
                                bundle.putString("inner_rating", task.getResult().getString("inner_rating"));
                                bundle.putString("inner_description", task.getResult().getString("inner_description"));
                                bundle.putString("db_name", search_db_name);
                                bundle.putString("main_place_id", search_main_place);
                                bundle.putDouble("inner_longitude", (Double) task.getResult().get("longitude"));
                                bundle.putDouble("inner_latitude", (Double) task.getResult().get("latitude"));
                                bundle.putStringArrayList("inner_images", (ArrayList<String>) task.getResult().get("inner_images"));
                                bundle.putStringArrayList("inner_features", (ArrayList<String>) task.getResult().get("inner_features"));

                                Intent intent = new Intent(context, InnerCategoryActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("curr_innerdetails_bundle", bundle);
                                context.startActivity(intent);
                            });
                }
            });
        } else {

            viewHolder.textViewDescription.setVisibility(View.GONE);
            Glide.with(viewHolder.itemView)
                    .load(mSliderItems.get(position).getImgUrl())
                    .fitCenter()
                    .into(viewHolder.imageViewBackground);

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewImageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("FullImageURL", mSliderItems.get(position).getImgUrl());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getCount() {
        if (pos == 0) {
            return threndPosterItems.size();
        } else {
            return mSliderItems.size();
        }
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemView = itemView;
        }
    }

}