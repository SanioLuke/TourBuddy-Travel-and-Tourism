package com.example.toursimapp.AllActivities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.Models.SearchItemModel;
import com.example.toursimapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ShowAllActivity extends AppCompatActivity {

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Functions().checkTheme(getApplicationContext());
        new Functions().lightstatusbardesign(ShowAllActivity.this);
        setContentView(R.layout.activity_show_all);

        Intent intent = getIntent();
        ArrayList<SearchItemModel> showalllist = intent.getParcelableArrayListExtra("recent_arraylist");

        RecyclerView showall_rec = findViewById(R.id.showall_rec);
        TextView showall_total_count = findViewById(R.id.showall_total_count);
        ImageButton showall_back_btn = findViewById(R.id.showall_back_btn);

        showall_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (showalllist != null && showalllist.size() > 0) {
            showall_total_count.setText(String.format("%d Recent Places Found", showalllist.size()));
        } else {
            showall_total_count.setVisibility(View.INVISIBLE);
        }

        showall_rec.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        ShowAllAdapter showAllAdapter = new ShowAllAdapter(getApplicationContext(), showalllist);
        showall_rec.setAdapter(showAllAdapter);
    }

    private static class ShowAllAdapter extends RecyclerView.Adapter<ShowAllAdapter.ListViewHolder> {

        private final Context context;
        ArrayList<SearchItemModel> showall_array = new ArrayList<>();
        FirebaseFirestore db;

        public ShowAllAdapter(Context context, ArrayList<SearchItemModel> showall_array) {
            this.context = context;
            this.showall_array = showall_array;
        }

        @NonNull
        @Override
        public ShowAllAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.placeitems, null);
            return new ListViewHolder(inflate);
        }

        @Override
        public void onBindViewHolder(@NonNull ShowAllAdapter.ListViewHolder holder, int position) {

            db = FirebaseFirestore.getInstance();

            holder.place_item_title.setText(showall_array.get(position).getS_place_name());
            Glide.with(context)
                    .load(showall_array.get(position).getS_place_img())
                    .fitCenter()
                    .into(holder.place_item_img);

            holder.place_item_lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String search_db_name = showall_array.get(position).getSdb_name();
                    String search_main_place = showall_array.get(position).getSmain_place();
                    String search_inner_place = showall_array.get(position).getSinner_place();

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
                }
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
            return showall_array.size();
        }

        public static class ListViewHolder extends RecyclerView.ViewHolder {

            TextView place_item_title;
            ImageView place_item_img;
            RelativeLayout place_item_lay;

            public ListViewHolder(@NonNull View itemView) {
                super(itemView);

                place_item_title = itemView.findViewById(R.id.place_item_title);
                place_item_img = itemView.findViewById(R.id.place_item_img);
                place_item_lay = itemView.findViewById(R.id.place_item_lay);

            }
        }
    }

}