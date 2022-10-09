package com.example.toursimapp.AllActivities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.toursimapp.Data.TripList;
import com.example.toursimapp.LocalDB.AnalyticDBHelper;
import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.Models.SearchItemModel;
import com.example.toursimapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private final TripList tripList = new TripList();
    private final ArrayList<SearchItemModel> all_search_results = new ArrayList<>();
    private final ArrayList<SearchItemModel> all_trending_results = new ArrayList<>();
    private final ArrayList<String> s_all_catname_list = tripList.all_trips_names();
    View s_searchresult_lay, s_searchother_lay, s_recentrec_lay;
    RecyclerView s_searchresult_rec;
    private EditText s_search_box_input;
    private ImageButton s_search_close, s_speech;
    private FirebaseFirestore db;
    private SearchResultAdapter searchResultAdapter;
    private AnalyticDBHelper analyticDBHelper;
    private RecyclerView s_recent_searches_rec;
    private final TextWatcher search_textwatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String search_text = s_search_box_input.getText().toString();

            if (!search_text.isEmpty() && search_text.matches("^[^\\s]+[-a-zA-Z\\s]+([-a-zA-Z^[^ \\n]]+)*$")) {

                Drawable rightSideDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_close);
                if (rightSideDrawable != null) {
                    rightSideDrawable.setColorFilter(getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_IN);
                }
                s_search_box_input.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, rightSideDrawable, null);
                s_search_box_input.setOnTouchListener((v, event) -> {

                    Drawable rightdrawable = s_search_box_input.getCompoundDrawables()[2];
                    if (rightdrawable != null) {
                        if (event.getRawX() >= (s_search_box_input).getRight() - s_search_box_input.getCompoundDrawables()[2].getBounds().width() - 30) {
                            s_search_box_input.setText("");
                            return true;
                        }
                    }
                    return false;
                });

                s_searchresult_lay.setVisibility(View.VISIBLE);
                searchPlaceFromFirebase(search_text, db, s_search_box_input);
            } else {
                s_searchresult_lay.setVisibility(View.GONE);
                s_search_box_input.setCompoundDrawables(null, null, null, null);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private RecyclerView s_trending_places;
    private SearchResultAdapter trendingAdapter;
    private RecyclerView s_all_category_rec;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Functions().checkTheme(getApplicationContext());
        setContentView(R.layout.activity_search);

        searchInits();
        recentSearchRecycler(s_recent_searches_rec, analyticDBHelper, getApplicationContext(), s_recentrec_lay);
        trendingrecycler();
        allTripRecycler();
        searchEventListnerFun();

    }

    private void searchEventListnerFun() {

        s_search_close.setOnClickListener(v -> finish());

        s_search_box_input.addTextChangedListener(search_textwatcher);

        s_speech.setOnClickListener(v -> {
            Intent speechintent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            speechintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            speechintent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech to text..");
            startActivityForResult(speechintent, 555);
        });

    }

    private void allTripRecycler() {
        s_all_category_rec.setHasFixedSize(true);
        s_all_category_rec.setLayoutManager(new LinearLayoutManager(this));
        SearchResultAdapter allTripAdapter = new SearchResultAdapter(getApplicationContext(), 3, s_all_catname_list);
        allTripAdapter.notifyDataSetChanged();
        s_all_category_rec.setAdapter(allTripAdapter);
    }

    public void recentSearchRecycler(RecyclerView recent_recycler, @NotNull AnalyticDBHelper dbHelper, Context recentContext, View all_rec_lay) {

        ArrayList<SearchItemModel> to_fun_all_recent_array = new ArrayList<>();

        if (dbHelper.getrecentdatacount() > 0) {
            if (dbHelper.getRecentcolname().size() > 0) {

                ArrayList<String> recent_db_names = dbHelper.getRecentcolname();
                ArrayList<String> recent_main_place = dbHelper.getRecentMainPlace();
                ArrayList<String> recent_inner_place = dbHelper.getRecentInnerPlace();
                ArrayList<String> recent_place_name = dbHelper.getRecentPlaceName();
                ArrayList<String> recent_place_img = dbHelper.getRecentPlaceImg();

                if (to_fun_all_recent_array.size() > 0) {
                    to_fun_all_recent_array.clear();
                }

                for (int i = 0; i < recent_db_names.size(); i++) {
                    to_fun_all_recent_array.add(new SearchItemModel(recent_db_names.get(i), recent_main_place.get(i),
                            recent_inner_place.get(i), recent_place_name.get(i), recent_place_img.get(i)));
                }
                Collections.reverse(to_fun_all_recent_array);
            }
        }

        if (to_fun_all_recent_array.size() <= 0) {
            all_rec_lay.setVisibility(View.GONE);
        } else {
            all_rec_lay.setVisibility(View.VISIBLE);
            recent_recycler.setHasFixedSize(true);
            recent_recycler.setLayoutManager(new LinearLayoutManager(recentContext));
            SearchResultAdapter recentSearchAdapter = new SearchResultAdapter(recentContext, to_fun_all_recent_array, dbHelper, 2, recent_recycler, null, s_recentrec_lay);
            recentSearchAdapter.notifyDataSetChanged();
            recent_recycler.setAdapter(recentSearchAdapter);
        }
    }

    private void trendingrecycler() {
        s_trending_places.setHasFixedSize(true);
        s_trending_places.setLayoutManager(new LinearLayoutManager(this));

        db.collection("trend_collection")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        if (all_trending_results.size() > 0) {
                            all_trending_results.clear();
                        }

                        if (task.getResult() != null) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                SearchItemModel searchItemModel = new SearchItemModel(documentSnapshot.getString("sdb_name"),
                                        documentSnapshot.getString("smain_place"),
                                        documentSnapshot.getString("sinner_place"),
                                        documentSnapshot.getString("s_place_name"),
                                        documentSnapshot.getString("s_place_img"));
                                all_trending_results.add(searchItemModel);
                            }
                        } else {
                            Log.e("search_log", "Null Data");
                        }

                        trendingAdapter = new SearchResultAdapter(getApplicationContext(), all_trending_results, analyticDBHelper, 2, s_recent_searches_rec, null, s_recentrec_lay);
                        trendingAdapter.notifyDataSetChanged();
                        s_trending_places.setAdapter(trendingAdapter);
                    } else {
                        Log.e("Trending_data", "No Trending Data");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 555 && resultCode == RESULT_OK) {
            ArrayList<String> matches = null;
            if (data != null) {
                matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            }
            if (matches != null) {
                s_search_box_input.setText(matches.get(0));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void searchPlaceFromFirebase(String search_text, @NotNull FirebaseFirestore db, EditText input_box) {

        db.collection("search_collection")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.e("search_data", "Data received");

                        if (all_search_results.size() > 0) {
                            all_search_results.clear();
                        }

                        if (task.getResult() != null) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {

                                String from_search_pname = documentSnapshot.getString("s_place_name");

                                if (from_search_pname != null) {

                                    String toLowfrom_search_pname = from_search_pname.toLowerCase();
                                    String toLow_search_text = search_text.toLowerCase();

                                    if (toLowfrom_search_pname.contains(toLow_search_text)) {
                                        if (all_search_results.size() > 0) {

                                            for (int i = 0; i < all_search_results.size(); i++) {

                                                if (!all_search_results.get(i).getS_place_name().equals(from_search_pname)) {
                                                    if (i == all_search_results.size() - 1) {
                                                        SearchItemModel searchItemModel = new SearchItemModel(documentSnapshot.getString("sdb_name"),
                                                                documentSnapshot.getString("smain_place"),
                                                                documentSnapshot.getString("sinner_place"),
                                                                from_search_pname,
                                                                documentSnapshot.getString("s_place_img"));
                                                        all_search_results.add(searchItemModel);
                                                    }
                                                }
                                            }
                                        } else {
                                            SearchItemModel searchItemModel = new SearchItemModel(documentSnapshot.getString("sdb_name"),
                                                    documentSnapshot.getString("smain_place"),
                                                    documentSnapshot.getString("sinner_place"),
                                                    from_search_pname,
                                                    documentSnapshot.getString("s_place_img"));

                                            all_search_results.add(searchItemModel);
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.e("search_data1", "Null Data");
                        }

                        s_searchresult_rec.setHasFixedSize(true);
                        s_searchresult_rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        Log.e("search_data_size", "" + all_search_results.size());
                        searchResultAdapter = new SearchResultAdapter(getApplicationContext(), all_search_results, analyticDBHelper, 1, s_recent_searches_rec, input_box, s_recentrec_lay);
                        searchResultAdapter.notifyDataSetChanged();
                        s_searchresult_rec.setAdapter(searchResultAdapter);
                    } else {
                        Log.e("search_data", "No Data from search!!!");
                        Toast.makeText(SearchActivity.this, "No Data from search!!!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void searchInits() {
        s_search_box_input = findViewById(R.id.s_search_box_input);
        s_search_close = findViewById(R.id.s_search_close);
        s_speech = findViewById(R.id.s_speech);
        s_searchresult_rec = findViewById(R.id.s_searchresult_rec);
        s_searchresult_lay = findViewById(R.id.s_searchresult_lay);
        s_searchother_lay = findViewById(R.id.s_searchother_lay);
        s_recent_searches_rec = findViewById(R.id.s_recent_searches_rec);
        s_trending_places = findViewById(R.id.s_trending_places);
        s_all_category_rec = findViewById(R.id.s_all_category_rec);
        s_recentrec_lay = findViewById(R.id.s_recentrec_lay);
        db = FirebaseFirestore.getInstance();
        analyticDBHelper = new AnalyticDBHelper(getApplicationContext());
    }

    public static class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ListViewHolder> {

        private final Context context;
        FirebaseUser firebaseUser;
        FirebaseAuth firebaseAuth;
        FirebaseFirestore db;
        AnalyticDBHelper analyticDBHelper;
        EditText input_box;
        int counter;
        View s_recentrec_lay;
        SearchActivity searchActivity = new SearchActivity();
        RecyclerView recent_recycler;
        ArrayList<String> all_category_array = new ArrayList<>();
        private ArrayList<SearchItemModel> search_all_array = new ArrayList<>();

        public SearchResultAdapter(Context context, ArrayList<SearchItemModel> search_all_array, AnalyticDBHelper analyticDBHelper, int counter, RecyclerView recent_recycler, EditText input_box, View s_recentrec_lay) {
            this.context = context;
            this.search_all_array = search_all_array;
            this.analyticDBHelper = analyticDBHelper;
            this.counter = counter;
            this.recent_recycler = recent_recycler;
            this.input_box = input_box;
            this.s_recentrec_lay = s_recentrec_lay;
        }

        public SearchResultAdapter(Context context, int counter, ArrayList<String> all_category_array) {
            this.context = context;
            this.counter = counter;
            this.all_category_array = all_category_array;
        }

        @NonNull
        @Override
        public SearchResultAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (counter == 1) {
                @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_items, null);
                return new SearchResultAdapter.ListViewHolder(inflate);
            } else if (counter == 2 || counter == 3) {
                @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_search_items, null);
                return new SearchResultAdapter.ListViewHolder(inflate);
            } else {
                @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_items, null);
                return new SearchResultAdapter.ListViewHolder(inflate);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onBindViewHolder(@NonNull SearchResultAdapter.ListViewHolder holder, int position) {

            if (counter == 1 || counter == 2) {
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseUser = firebaseAuth.getCurrentUser();
                db = FirebaseFirestore.getInstance();

                Log.e("adapter_place_name", search_all_array.get(position).getS_place_name());
                holder.search_item_name.setText(search_all_array.get(position).getS_place_name());
                Glide.with(context)
                        .load(search_all_array.get(position).getS_place_img())
                        .into(holder.search_item_img);

                holder.searchitem_main_container.setOnClickListener(v -> {

                    String search_db_name = search_all_array.get(position).getSdb_name();
                    String search_main_place = search_all_array.get(position).getSmain_place();
                    String search_inner_place = search_all_array.get(position).getSinner_place();
                    String search_place_name = search_all_array.get(position).getS_place_name();
                    String search_place_img = search_all_array.get(position).getS_place_img();

                    if (analyticDBHelper.getRecentcolname().size() >= 5) {
                        analyticDBHelper.deleteAllRecentPlaces();
                    }

                    if (analyticDBHelper.getRecentcolname().size() > 0) {
                        for (int i = 0; i < analyticDBHelper.getRecentcolname().size(); i++) {

                            String local_placename = analyticDBHelper.getRecentPlaceName().get(i);
                            if (search_place_name.equals(local_placename)) {
                                if (analyticDBHelper.deleteRecentPlace(search_db_name, search_main_place, search_inner_place, search_place_name, search_place_img)) {
                                    if (analyticDBHelper.addRecentPlace(search_db_name, search_main_place, search_inner_place, search_place_name, search_place_img)) {
                                        Log.e("recentDBLocal", "entered1");
                                    } else {
                                        Log.e("recentDBLocal", "notentered1");
                                    }
                                    break;
                                }
                            } else {
                                if (i == analyticDBHelper.getRecentcolname().size() - 1) {
                                    if (analyticDBHelper.addRecentPlace(search_db_name, search_main_place, search_inner_place, search_place_name, search_place_img)) {
                                        Log.e("recentDBLocal", "entered3");
                                    } else {
                                        Log.e("recentDBLocal", "notentered3");
                                    }
                                    break;
                                }
                            }
                        }
                    } else {
                        if (analyticDBHelper.addRecentPlace(search_db_name, search_main_place, search_inner_place, search_place_name, search_place_img)) {
                            Log.e("recentDBLocal", "entered2");
                        } else {
                            Log.e("recentDBLocal", "notentered2");
                        }
                    }

                    new Handler().postDelayed(() -> {
                        if (counter == 1) {
                            input_box.setText("");
                        }
                        searchActivity.recentSearchRecycler(recent_recycler, analyticDBHelper, context, s_recentrec_lay);

                    }, 1500);

                    if (search_db_name != null && search_main_place != null && search_inner_place == null) {
                        db.collection(search_db_name)
                                .document(search_main_place)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.e("search_to_page", "success");

                                        if (task.getResult() != null) {

                                            Log.e("inner_long1", "The Longitude is : " + task.getResult().get("longitude"));
                                            Log.e("inner_lat1", "The Laitutde is : " + task.getResult().get("latitude"));
                                            Bundle bundle = new Bundle();
                                            Map<String, Object> hash = task.getResult().getData();

                                            if (hash != null) {
                                                bundle.putString("curr_place_id", hash.get("place_id").toString());
                                                bundle.putString("curr_place_name", hash.get("place_name").toString());
                                                bundle.putString("curr_description", hash.get("description").toString());
                                                bundle.putString("curr_attractions", hash.get("attractions").toString());
                                                bundle.putString("curr_besttime", hash.get("best_time").toString());
                                                bundle.putString("curr_rate_place", hash.get("rate_place").toString());
                                                bundle.putString("curr_climate", hash.get("climate").toString());
                                                bundle.putString("curr_reach_method", hash.get("reach_method").toString());
                                                bundle.putDouble("curr_longitude", (Double) hash.get("longitude"));
                                                bundle.putDouble("curr_latitude", (Double) hash.get("latitude"));
                                                bundle.putStringArrayList("curr_array_images", (ArrayList<String>) task.getResult().get("images"));
                                                bundle.putString("curr_db_name", search_db_name);

                                                Intent intent = new Intent(context, MainPlaceActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("curr_details_bundle", bundle);
                                                context.startActivity(intent);
                                            }
                                        }
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

                                    if (task.getResult() != null) {

                                        Bundle bundle = new Bundle();
                                        Map<String, Object> hash = task.getResult().getData();
                                        if (hash != null) {
                                            bundle.putString("inner_place_id", hash.get("inner_place_id").toString());
                                            bundle.putString("inner_place_name", hash.get("inner_place_name").toString());
                                            bundle.putString("inner_rating", hash.get("inner_rating").toString());
                                            bundle.putString("inner_description", hash.get("inner_description").toString());
                                            bundle.putString("db_name", search_db_name);
                                            bundle.putString("main_place_id", search_main_place);
                                            bundle.putDouble("inner_longitude", (Double) hash.get("longitude"));
                                            bundle.putDouble("inner_latitude", (Double) hash.get("latitude"));
                                            bundle.putStringArrayList("inner_images", (ArrayList<String>) hash.get("inner_images"));
                                            bundle.putStringArrayList("inner_features", (ArrayList<String>) hash.get("inner_features"));

                                            Intent intent = new Intent(context, InnerCategoryActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("curr_innerdetails_bundle", bundle);
                                            context.startActivity(intent);
                                        }
                                    }
                                });
                    }
                });
            } else if (counter == 3) {

                holder.rsearchitem_image_lay.setVisibility(View.GONE);
                holder.search_item_name.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_right_arrow), null);
                holder.search_item_name.setTypeface(Typeface.SERIF, Typeface.NORMAL);
                holder.search_item_name.setText(String.format("%s trips", all_category_array.get(position)));
                holder.searchitem_main_container.setOnClickListener(v -> {
                    Intent intent = new Intent(context, CategoryActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("category_pos", position);
                    context.startActivity(intent);
                });
            }
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
            if (counter == 1 || counter == 2) {
                return search_all_array.size();
            } else if (counter == 3) {
                return all_category_array.size();
            } else {
                return 0;
            }
        }

        public class ListViewHolder extends RecyclerView.ViewHolder {

            View searchitem_main_container;
            ImageView search_item_img;
            TextView search_item_name;
            CardView rsearchitem_image_lay;

            public ListViewHolder(@NonNull View itemView) {
                super(itemView);

                if (counter == 1) {
                    searchitem_main_container = itemView.findViewById(R.id.searchitem_main_container);
                    search_item_img = itemView.findViewById(R.id.search_item_img);
                    search_item_name = itemView.findViewById(R.id.search_item_name);
                } else if (counter == 2 || counter == 3) {
                    searchitem_main_container = itemView.findViewById(R.id.rsearchitem_main_container);
                    search_item_img = itemView.findViewById(R.id.rsearch_item_img);
                    search_item_name = itemView.findViewById(R.id.rsearch_item_name);
                    rsearchitem_image_lay = itemView.findViewById(R.id.rsearchitem_image_lay);
                }
            }
        }

    }
}