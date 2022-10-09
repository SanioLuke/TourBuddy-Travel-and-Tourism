package com.example.toursimapp.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.toursimapp.AllActivities.InnerCategoryActivity;
import com.example.toursimapp.AllActivities.MainPlaceActivity;
import com.example.toursimapp.Models.FavModelArray;
import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@SuppressWarnings("all")
public class Favfragment extends Fragment {

    View fav_frag_main_lay;
    private TextView fav_rec_title;
    private Button fav_browse_places;
    private RecyclerView fav_recview;
    private FavSetAdapter favSetAdapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    private SwipeRefreshLayout favfrag_swipe_refresh;
    private ArrayList<FavModelArray> fav_all_list = new ArrayList<>();
    private View fav_data_con, no_fav_container, favfrag_loading_lay;

    public Favfragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        new Functions().checkTheme(requireContext());
        View view = inflater.inflate(R.layout.fragment_favfragment, container, false);

        favFragInitialize(view);
        checkFavDb();
        favfrag_swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkFavDb();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        favfrag_swipe_refresh.setRefreshing(false);
                    }
                }, 1500);
            }
        });

        return view;
    }

    private void favFragInitialize(@NotNull View view) {
        fav_frag_main_lay = view.findViewById(R.id.fav_frag_main_lay);
        favfrag_swipe_refresh = view.findViewById(R.id.favfrag_swipe_refresh);
        fav_rec_title = view.findViewById(R.id.fav_rec_title);
        fav_recview = view.findViewById(R.id.fav_recview);
        fav_data_con = view.findViewById(R.id.fav_data_con);
        no_fav_container = view.findViewById(R.id.no_fav_container);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        fav_data_con.setVisibility(View.INVISIBLE);
        no_fav_container.setVisibility(View.INVISIBLE);
        favfrag_swipe_refresh.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.my_red),
                ContextCompat.getColor(getContext(), R.color.my_orange),
                ContextCompat.getColor(getContext(), R.color.light_color));
        favfrag_loading_lay = view.findViewById(R.id.favfrag_loading_lay);
        favfrag_loading_lay.setVisibility(View.VISIBLE);
    }

    private void checkFavDb() {

        db.collection("users")
                .document(firebaseUser.getUid())
                .collection("favourites")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            favfrag_loading_lay.setVisibility(View.GONE);
                            if (task.getResult() != null) {
                                if (task.getResult().size() > 0) {
                                    fav_data_con.setVisibility(View.VISIBLE);

                                    if (fav_all_list.size() > 0) {
                                        fav_all_list.clear();
                                        favSetAdapter.notifyDataSetChanged();
                                    }

                                    fav_rec_title.setText(String.format("%d places added", task.getResult().size()));
                                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                        String fdb_db_name = documentSnapshot.getString("fav_db_name");
                                        String fmain_place_id = documentSnapshot.getString("fav_main_place_id");
                                        String finner_place_id = documentSnapshot.getString("fav_inner_place_id");
                                        String fplace_name = documentSnapshot.getString("fav_place_name");
                                        FavModelArray favModelArray = new FavModelArray(fdb_db_name, fmain_place_id, finner_place_id, fplace_name);
                                        fav_all_list.add(favModelArray);
                                    }

                                    fav_recview.setLayoutManager(new LinearLayoutManager(getContext()));
                                    favSetAdapter = new FavSetAdapter("users", fav_all_list, getContext(), firebaseUser, firebaseAuth, db, fav_rec_title, no_fav_container, fav_data_con, fav_frag_main_lay);
                                    fav_recview.setAdapter(favSetAdapter);

                                } else {
                                    no_fav_container.setVisibility(View.VISIBLE);
                                    fav_data_con.setVisibility(View.GONE);
                                }
                            } else {
                                no_fav_container.setVisibility(View.VISIBLE);
                                fav_data_con.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        checkFavDb();
    }

    public static class FavSetAdapter extends RecyclerView.Adapter<FavSetAdapter.ListViewHolder> {

        ArrayList<FavModelArray> fav_updated_list = new ArrayList<>();
        View no_fav_container, fav_data_con;
        View fav_frag_main_lay;
        private ArrayList<FavModelArray> fav_all_list = new ArrayList<>();
        private Context context;
        private FirebaseUser firebaseUser;
        private FirebaseAuth firebaseAuth;
        private FirebaseFirestore db;
        private String fav_db_name, fav_main_place_id, fav_inner_place_id;
        private TextView fav_rec_title;
        private String user_col_name;


        public FavSetAdapter(String user_col_name, ArrayList<FavModelArray> fav_all_list, Context context, FirebaseUser firebaseUser, FirebaseAuth firebaseAuth, FirebaseFirestore db, TextView fav_rec_title, View no_fav_container, View fav_data_con, View fav_frag_main_lay) {
            this.user_col_name = user_col_name;
            this.fav_all_list = fav_all_list;
            this.context = context;
            this.firebaseUser = firebaseUser;
            this.firebaseAuth = firebaseAuth;
            this.db = db;
            this.fav_rec_title = fav_rec_title;
            this.no_fav_container = no_fav_container;
            this.fav_data_con = fav_data_con;
            this.fav_frag_main_lay = fav_frag_main_lay;
        }

        @NonNull
        @Override
        public FavSetAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_cat_items, null);

            return new ListViewHolder(inflate);
        }

        private void updateAdapter(ArrayList<FavModelArray> fav_updated_list) {
            fav_all_list.clear();
            fav_all_list.addAll(fav_updated_list);
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(@NonNull FavSetAdapter.ListViewHolder holder, int position) {

            fav_db_name = fav_all_list.get(position).getFav_db_name();
            fav_main_place_id = fav_all_list.get(position).getFav_main_place_id();
            fav_inner_place_id = fav_all_list.get(position).getFav_inner_place_id();

            if (fav_db_name != null && fav_main_place_id != null && fav_inner_place_id == null) {
                db.collection(fav_db_name)
                        .document(fav_main_place_id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.e("fav_set", "fav setting success");

                                    String rate_txt = task.getResult().getString("rate_place");
                                    holder.fav_rate.setText(rate_txt);
                                    holder.fav_rate_bar.setRating(Float.parseFloat(rate_txt));

                                    holder.fav_place_name.setText(task.getResult().getString("place_name"));
                                    holder.fav_place_attractions.setText(task.getResult().getString("description"));
                                    holder.fav_btn.setImageTintList(ColorStateList.valueOf(Color.RED));

                                    ArrayList<String> fav_all_images = (ArrayList<String>) task.getResult().get("images");
                                    assert fav_all_images != null;
                                    Glide.with(context).load(fav_all_images.get(0)).fitCenter().into(holder.fav_img);
                                } else {
                                    Log.e("fav_set", "fav setting failed");
                                }
                            }
                        });
            } else {
                db.collection(fav_db_name)
                        .document(fav_main_place_id)
                        .collection("inner_places")
                        .document(fav_inner_place_id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.e("fav_set", "fav setting success");

                                    String rate_txt = task.getResult().getString("inner_rating");
                                    holder.fav_rate.setText(rate_txt);
                                    holder.fav_rate_bar.setRating(Float.parseFloat(rate_txt));

                                    ArrayList<String> fav_inner_images = (ArrayList<String>) task.getResult().get("inner_images");
                                    Glide.with(context).load(fav_inner_images.get(0)).fitCenter().into(holder.fav_img);

                                    holder.fav_place_name.setText(task.getResult().getString("inner_place_name"));
                                    holder.fav_place_attractions.setText(task.getResult().getString("inner_description"));
                                    holder.fav_btn.setImageTintList(ColorStateList.valueOf(Color.RED));
                                } else {
                                    Log.e("fav_set", "fav setting failed");
                                }
                            }
                        });
            }

            holder.fav_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fav_db_name = fav_all_list.get(position).getFav_db_name();
                    fav_main_place_id = fav_all_list.get(position).getFav_main_place_id();
                    fav_inner_place_id = fav_all_list.get(position).getFav_inner_place_id();

                    if (fav_db_name != null && fav_main_place_id != null && fav_inner_place_id == null) {
                        checkUpdate(fav_main_place_id, fav_rec_title, fav_data_con, no_fav_container);
                    } else {
                        checkUpdate(fav_inner_place_id, fav_rec_title, fav_data_con, no_fav_container);
                    }
                }
            });

            holder.fav_main_lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    fav_db_name = fav_all_list.get(position).getFav_db_name();
                    fav_main_place_id = fav_all_list.get(position).getFav_main_place_id();
                    fav_inner_place_id = fav_all_list.get(position).getFav_inner_place_id();

                    if (fav_db_name != null && fav_main_place_id != null && fav_inner_place_id == null) {
                        db.collection(fav_db_name)
                                .document(fav_main_place_id)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            Log.e("fav_to_page", "success");

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
                                            bundle.putString("curr_db_name", fav_db_name);

                                            Intent intent = new Intent(context, MainPlaceActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("curr_details_bundle", bundle);
                                            context.startActivity(intent);
                                        } else {
                                            Log.e("fav_to_page", "failed");
                                        }
                                    }
                                });
                    }
                    if (fav_db_name != null && fav_main_place_id != null && fav_inner_place_id != null) {
                        db.collection(fav_db_name)
                                .document(fav_main_place_id)
                                .collection("inner_places")
                                .document(fav_inner_place_id)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("inner_place_id", task.getResult().getString("inner_place_id"));
                                        bundle.putString("inner_place_name", task.getResult().getString("inner_place_name"));
                                        bundle.putString("inner_rating", task.getResult().getString("inner_rating"));
                                        bundle.putString("inner_description", task.getResult().getString("inner_description"));
                                        bundle.putString("db_name", fav_db_name);
                                        bundle.putString("main_place_id", fav_main_place_id);
                                        bundle.putDouble("inner_longitude", (Double) task.getResult().get("longitude"));
                                        bundle.putDouble("inner_latitude", (Double) task.getResult().get("latitude"));
                                        bundle.putStringArrayList("inner_images", (ArrayList<String>) task.getResult().get("inner_images"));
                                        bundle.putStringArrayList("inner_features", (ArrayList<String>) task.getResult().get("inner_features"));

                                        Intent intent = new Intent(context, InnerCategoryActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("curr_innerdetails_bundle", bundle);
                                        context.startActivity(intent);
                                    }
                                });
                    }
                }
            });
        }

        private void checkUpdate(String place_id, TextView fav_rec_title, View fav_data_con, View no_fav_container) {
            db.collection("users")
                    .document(firebaseUser.getUid())
                    .collection("favourites")
                    .document(place_id)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                db.collection(user_col_name)
                                        .document(firebaseUser.getUid())
                                        .collection("favourites")
                                        .document(place_id)
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    db.collection(user_col_name)
                                                            .document(firebaseUser.getUid())
                                                            .collection("favourites")
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @SuppressLint("DefaultLocale")
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.getResult() != null) {

                                                                        if (fav_updated_list.size() > 0) {
                                                                            fav_updated_list.clear();
                                                                        }
                                                                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                                                            String fdb_db_name = documentSnapshot.getString("fav_db_name");
                                                                            String fmain_place_id = documentSnapshot.getString("fav_main_place_id");
                                                                            String finner_place_id = documentSnapshot.getString("fav_inner_place_id");
                                                                            String fplace_name = documentSnapshot.getString("fav_place_name");
                                                                            FavModelArray favModelArray = new FavModelArray(fdb_db_name, fmain_place_id, finner_place_id, fplace_name);
                                                                            fav_updated_list.add(favModelArray);
                                                                        }

                                                                        if (task.getResult().size() == 0) {
                                                                            no_fav_container.setVisibility(View.VISIBLE);
                                                                            fav_data_con.setVisibility(View.GONE);
                                                                        } else {
                                                                            no_fav_container.setVisibility(View.GONE);
                                                                            fav_data_con.setVisibility(View.VISIBLE);
                                                                        }

                                                                        fav_rec_title.setText(String.format("%d places added", task.getResult().size()));
                                                                        updateAdapter(fav_updated_list);
                                                                        Snackbar.make(fav_frag_main_lay, "Removed from favourites", Snackbar.LENGTH_SHORT).show();

                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            } else {
                                Log.e("fav_btn_click", "failed");
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
            return fav_all_list.size();
        }

        public static class ListViewHolder extends RecyclerView.ViewHolder {

            View fav_main_lay;
            ImageView fav_img;
            ImageButton fav_btn;
            TextView fav_rate, fav_place_name, fav_place_attractions;
            RatingBar fav_rate_bar;

            public ListViewHolder(@NonNull View itemView) {
                super(itemView);

                fav_main_lay = itemView.findViewById(R.id.fav_main_lay);
                fav_img = itemView.findViewById(R.id.fav_img);
                fav_btn = itemView.findViewById(R.id.fav_btn);
                fav_rate = itemView.findViewById(R.id.fav_rate);
                fav_place_name = itemView.findViewById(R.id.fav_place_name);
                fav_place_attractions = itemView.findViewById(R.id.fav_place_attractions);
                fav_rate_bar = itemView.findViewById(R.id.fav_rate_bar);

            }
        }
    }

}