package com.example.toursimapp.AllActivities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.toursimapp.Data.TripList;
import com.example.toursimapp.LocalDB.AnalyticDBHelper;
import com.example.toursimapp.Models.CategoryModel;
import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CategoryActivity extends AppCompatActivity {

    private final TripList tripList = new TripList();
    private final ArrayList<String> trip_db_names = tripList.all_trips_db();
    private final ArrayList<String> trip_place_names = tripList.all_trips_names();
    private final ArrayList<CategoryModel> category_items_array = new ArrayList<>();
    AnalyticDBHelper dbHelper;
    View category_main_lay;
    private TextView category_total_count, category_toolbar_title;
    private RecyclerView category_items_rec;
    private CategoryModel categoryModel;
    private CategoryItemsAdapter categoryItemsAdapter;
    private ScaleAnimation scaleAnimation;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Functions().checkTheme(getApplicationContext());
        setContentView(R.layout.activity_category);

        category_main_lay = findViewById(R.id.category_main_lay);
        category_toolbar_title = findViewById(R.id.category_toolbar_title);
        category_total_count = findViewById(R.id.category_total_count);
        category_items_rec = findViewById(R.id.category_items_rec);
        ImageButton category_back_btn = findViewById(R.id.category_back_btn);
        dbHelper = new AnalyticDBHelper(getApplicationContext());
        Intent getintent = getIntent();
        int getpos = getintent.getIntExtra("category_pos", 0);
        String from_home_db = getintent.getStringExtra("category_db_name");
        String from_home_name = "Family";
        boolean analysis_counter = getintent.getBooleanExtra("analysis_counter", true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (analysis_counter) {
            dbHelper.addPlace(trip_db_names.get(getpos));
            analysis_counter = true;
        }

        category_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String fdb_name;
        assert firebaseUser != null;

        if (getpos == 0) {
            if (from_home_db != null && !from_home_db.isEmpty() && !from_home_db.equals(" ")) {
                fdb_name = from_home_db;

                for (int j = 0; j < trip_db_names.size(); j++) {
                    if (trip_db_names.get(j).equals(fdb_name)) {
                        from_home_name = trip_place_names.get(j);
                        break;
                    }
                }
                category_toolbar_title.setText(String.format("%s trips", from_home_name));
            } else {
                fdb_name = "family_trip";
                category_toolbar_title.setText(from_home_name + " trips");
            }
        } else {
            fdb_name = trip_db_names.get(getpos);
            from_home_name = trip_place_names.get(getpos);
            category_toolbar_title.setText(String.format("%s trips", from_home_name));
        }

        db.collection(fdb_name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("DefaultLocale")
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.e("category_data", "success");

                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            categoryModel = new CategoryModel(documentSnapshot.getString("place_id"),
                                    documentSnapshot.getString("place_name"),
                                    documentSnapshot.getString("description"),
                                    documentSnapshot.getString("attractions"),
                                    documentSnapshot.getString("best_time"),
                                    documentSnapshot.getString("rate_place"),
                                    documentSnapshot.getString("climate"),
                                    documentSnapshot.getString("reach_method"),
                                    (Double) documentSnapshot.get("longitude"),
                                    (Double) documentSnapshot.get("latitude"),
                                    (ArrayList<String>) documentSnapshot.get("images"),
                                    fdb_name);

                            category_items_array.add(categoryModel);
                        }

                        Collections.shuffle(category_items_array);

                        category_total_count.setText(String.format("Total %d places found", task.getResult().size()));
                        category_items_rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        categoryItemsAdapter = new CategoryItemsAdapter(getApplicationContext(), category_items_array, category_main_lay);
                        category_items_rec.setAdapter(categoryItemsAdapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("category_data", "failed");
                    }
                });

    }

    private class CategoryItemsAdapter extends RecyclerView.Adapter<CategoryItemsAdapter.ListViewHolder> {

        private final Context context;
        View category_main_lay;
        private ArrayList<CategoryModel> category_items_array = new ArrayList<>();

        public CategoryItemsAdapter(Context context, ArrayList<CategoryModel> category_items_array, View category_main_lay) {
            this.context = context;
            this.category_items_array = category_items_array;
            this.category_main_lay = category_main_lay;
        }

        @NonNull
        @Override
        public CategoryItemsAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.categeory_items, null);
            return new CategoryItemsAdapter.ListViewHolder(inflate);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryItemsAdapter.ListViewHolder holder, int position) {

            holder.cat_fav.setVisibility(View.INVISIBLE);
            db.collection("users")
                    .document(firebaseUser.getUid())
                    .collection("favourites")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            holder.cat_fav.setVisibility(View.VISIBLE);
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot documentSnapshot : task.getResult()) {

                                    String fdb_db_name = documentSnapshot.getString("fav_db_name");
                                    String fmain_place_id = documentSnapshot.getString("fav_main_place_id");
                                    String finner_place_id = documentSnapshot.getString("fav_inner_place_id");

                                    if (fdb_db_name != null && fmain_place_id != null && finner_place_id == null) {
                                        if (fdb_db_name.equals(category_items_array.get(position).getDb_name()) && fmain_place_id.equals(category_items_array.get(position).getPlace_id())) {
                                            holder.cat_fav.setBackgroundResource(R.drawable.ic_favorite);
                                            holder.cat_fav.setImageTintList(ColorStateList.valueOf(Color.RED));
                                            holder.cat_fav.setBackgroundTintList(ColorStateList.valueOf(0));
                                            break;
                                        } else {
                                            holder.cat_fav.setBackgroundResource(R.drawable.ic_favorite);
                                            holder.cat_fav.setImageTintList(ColorStateList.valueOf(Color.WHITE));
                                            holder.cat_fav.setBackgroundTintList(ColorStateList.valueOf(0));
                                        }
                                    } else {
                                        holder.cat_fav.setBackgroundResource(R.drawable.ic_favorite);
                                        holder.cat_fav.setImageTintList(ColorStateList.valueOf(Color.WHITE));
                                        holder.cat_fav.setBackgroundTintList(ColorStateList.valueOf(0));
                                    }
                                }
                            }
                        }
                    });

            holder.cat_place_name.setText(category_items_array.get(position).getPlace_name());
            holder.cat_place_attractions.setText(category_items_array.get(position).getAttractions());
            holder.cat_rate.setText(category_items_array.get(position).getRate_place());
            Glide.with(context).load(category_items_array.get(position).getArray_images().get(0)).fitCenter().into(holder.cat_img);

            holder.cat_fav.setOnClickListener(v -> {

                db.collection("users")
                        .document(firebaseUser.getUid())
                        .collection("favourites")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {

                                    int count = 1;
                                    if (Objects.requireNonNull(task.getResult()).size() > 0) {
                                        for (DocumentSnapshot documentSnapshot : task.getResult()) {

                                            String fdb_db_name = documentSnapshot.getString("fav_db_name");
                                            String fmain_place_id = documentSnapshot.getString("fav_main_place_id");
                                            String finner_place_id = documentSnapshot.getString("fav_inner_place_id");

                                            if (fdb_db_name != null && fmain_place_id != null && finner_place_id == null) {
                                                if (fdb_db_name.equals(category_items_array.get(position).getDb_name()) && fmain_place_id.equals(category_items_array.get(position).getPlace_id())) {

                                                    db.collection("users")
                                                            .document(firebaseUser.getUid())
                                                            .collection("favourites")
                                                            .document(fmain_place_id)
                                                            .delete()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Snackbar.make(category_main_lay, "Removed from favourites", Snackbar.LENGTH_SHORT).show();
                                                                        holder.cat_fav.setBackgroundResource(R.drawable.ic_favorite);
                                                                        holder.cat_fav.setImageTintList(ColorStateList.valueOf(Color.WHITE));
                                                                        holder.cat_fav.setBackgroundTintList(ColorStateList.valueOf(0));
                                                                    } else {
                                                                        Toast.makeText(context, "Not removed from fav!!!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                    break;
                                                } else {
                                                    if (count == task.getResult().size()) {

                                                        addToFav(category_items_array.get(position).getDb_name(), category_items_array.get(position).getPlace_id()
                                                                , category_items_array.get(position).getPlace_name(), null, firebaseUser.getUid(), holder.cat_fav);
                                                    }
                                                }
                                            } else {
                                                if (count == task.getResult().size()) {
                                                    addToFav(category_items_array.get(position).getDb_name(), category_items_array.get(position).getPlace_id()
                                                            , category_items_array.get(position).getPlace_name(), null, firebaseUser.getUid(), holder.cat_fav);
                                                }
                                            }
                                            count++;
                                        }
                                    } else {
                                        addToFav(category_items_array.get(position).getDb_name(), category_items_array.get(position).getPlace_id()
                                                , category_items_array.get(position).getPlace_name(), null, firebaseUser.getUid(), holder.cat_fav);
                                    }
                                }
                            }
                        });
            });

            holder.cat_main_lay.setOnClickListener(v -> {

                Bundle bundle = new Bundle();
                bundle.putString("curr_place_id", category_items_array.get(position).getPlace_id());
                bundle.putString("curr_place_name", category_items_array.get(position).getPlace_name());
                bundle.putString("curr_description", category_items_array.get(position).getDescription());
                bundle.putString("curr_attractions", category_items_array.get(position).getAttractions());
                bundle.putString("curr_besttime", category_items_array.get(position).getBest_time());
                bundle.putString("curr_rate_place", category_items_array.get(position).getRate_place());
                bundle.putString("curr_climate", category_items_array.get(position).getClimate());
                bundle.putString("curr_reach_method", category_items_array.get(position).getReach_method());
                bundle.putDouble("curr_longitude", category_items_array.get(position).getLongitude());
                bundle.putDouble("curr_latitude", category_items_array.get(position).getLatitude());
                bundle.putStringArrayList("curr_array_images", category_items_array.get(position).getArray_images());
                bundle.putString("curr_db_name", category_items_array.get(position).getDb_name());

                Intent intent = new Intent(context, MainPlaceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("curr_details_bundle", bundle);

                context.startActivity(intent);
            });
        }

        private void addToFav(String database, String main, String name, Object inner, String uid, ImageButton fav_btn) {

            Map<String, Object> map = new HashMap<>();
            map.put("fav_db_name", database);
            map.put("fav_main_place_id", main);
            map.put("fav_inner_place_id", inner);
            map.put("fav_place_name", name);

            db.collection("users")
                    .document(uid)
                    .collection("favourites")
                    .document(main)
                    .set(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Snackbar.make(category_main_lay, "Added to favourites", Snackbar.LENGTH_SHORT).show();
                                fav_btn.setBackgroundResource(R.drawable.ic_favorite);
                                fav_btn.setImageTintList(ColorStateList.valueOf(Color.RED));
                                fav_btn.setBackgroundTintList(ColorStateList.valueOf(0));
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
            return category_items_array.size();
        }

        public class ListViewHolder extends RecyclerView.ViewHolder {

            ImageButton cat_fav;
            TextView cat_place_name, cat_rate;
            TextView cat_place_attractions;
            ImageView cat_img;
            RelativeLayout cat_main_lay;


            public ListViewHolder(@NonNull View itemView) {
                super(itemView);

                cat_fav = itemView.findViewById(R.id.cat_fav);
                cat_place_name = itemView.findViewById(R.id.cat_place_name);
                cat_rate = itemView.findViewById(R.id.cat_rate);
                cat_img = itemView.findViewById(R.id.cat_img);
                cat_place_attractions = itemView.findViewById(R.id.cat_place_attractions);
                cat_main_lay = itemView.findViewById(R.id.cat_main_lay);
            }
        }
    }
}