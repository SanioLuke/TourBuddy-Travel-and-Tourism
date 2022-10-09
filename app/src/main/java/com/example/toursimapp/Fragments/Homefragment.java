package com.example.toursimapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.toursimapp.Adapters.HomeMainListAdapter;
import com.example.toursimapp.Adapters.ImageSlideAdapter;
import com.example.toursimapp.Adapters.ProcessDataAdapter;
import com.example.toursimapp.AllActivities.CategoryActivity;
import com.example.toursimapp.AllActivities.InnerCategoryActivity;
import com.example.toursimapp.AllActivities.MainPlaceActivity;
import com.example.toursimapp.AllActivities.MainSettingsActivity;
import com.example.toursimapp.AllActivities.SearchActivity;
import com.example.toursimapp.AllActivities.ShowAllActivity;
import com.example.toursimapp.Data.TripList;
import com.example.toursimapp.LocalDB.AnalyticDBHelper;
import com.example.toursimapp.LocalDB.RecentPlaceDBHelper;
import com.example.toursimapp.Models.CategoryModel;
import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.Models.SearchItemModel;
import com.example.toursimapp.Models.homemain_rec_model;
import com.example.toursimapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

@SuppressWarnings("all")
public class Homefragment extends Fragment {

    private final TripList tripList = new TripList();
    private final ArrayList<String> all_trips_names = tripList.all_trips_names();
    private final ArrayList<String> all_trips_db = tripList.all_trips_db();
    private final ArrayList<String> all_trips_home_titles = tripList.all_trips_home_titles();
    private final ArrayList<Integer> all_cat_img = tripList.all_trip_images();
    private final ArrayList<homemain_rec_model> main_trip_list = new ArrayList<>();
    private final ArrayList<SearchItemModel> threndPosterarray = new ArrayList<>();
    private final ArrayList<String> selected_prefs_db = new ArrayList<>();
    private final ArrayList<String> selected_prefs_titles = new ArrayList<>();
    private final ArrayList<ArrayList<CategoryModel>> inner_list_data = new ArrayList<>();
    private final Functions functions = new Functions();
    private final ArrayList<SearchItemModel> all_viewed_array = new ArrayList<>();
    private RecyclerView main_user_recycler, home_page_some_viewed_rec;
    private ImageButton home_main_settings;
    private SliderView image_slider;
    private Toolbar home_main_toolbar;
    private RecyclerView category_recycler;
    private SwipeRefreshLayout home_frag_swipe_refersh;
    private ProgressBar home_frag_prog_bar;
    private AnalyticDBHelper analyticDBHelper;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private TextInputLayout home_searchbar;
    private AppBarLayout home_appbarlay;
    private ImageSlideAdapter imageSlideAdapter;
    private ArrayList<String> pref_list_db_names = new ArrayList<>();
    private View recent_places_container;
    private RecyclerView recent_viewed_recycler;
    private RecentPlaceDBHelper recentPlaceDBHelper;

    public Homefragment() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        new Functions().checkTheme(requireContext());
        View view = inflater.inflate(R.layout.fragment_homefragment, container, false);

        homeInits(view);
        analysisNotificationMethod();
        allHomePageCategoryRecycler();
        recentPlaceRecycler();
        viewedPlacesRecycler();
        trendPosterSliderFunction();
        homePrefsFun();
        homeFragEventListnerMoethods();

        return view;
    }

    private void viewedPlacesRecycler() {

        String each_item;
        ArrayList<String> processed_arr = new ArrayList<>();
        ArrayList<SearchItemModel> all_recent_arrays = recentPlaceDBHelper.getRecentPlacesArrays();

        if (all_recent_arrays.size() > 0) {
            for (int i = 0; i < all_recent_arrays.size(); i++) {
                each_item = all_recent_arrays.get(i).getSmain_place();
                if (processed_arr.size() > 0) {
                    for (int m = 0; m < processed_arr.size(); m++) {
                        if (!each_item.equals(processed_arr.get(m))) {
                            if (m == processed_arr.size() - 1) {
                                int itemcount = 0;
                                for (int j = i + 1; j < all_recent_arrays.size(); j++) {

                                    if (each_item.equals(all_recent_arrays.get(j).getSmain_place())) {
                                        itemcount = itemcount + 1;
                                    }
                                    if (j == all_recent_arrays.size() - 1) {
                                        if (itemcount >= 3) {
                                            processed_arr.add(each_item);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {

                    int itemcount = 0;
                    for (int j = i + 1; j < all_recent_arrays.size(); j++) {

                        if (each_item.equals(all_recent_arrays.get(j).getSmain_place())) {
                            itemcount = itemcount + 1;
                        }
                        if (j == all_recent_arrays.size() - 1) {
                            if (itemcount >= 3) {
                                processed_arr.add(each_item);
                            }
                        }
                    }
                }
            }
            if (processed_arr.size() <= 0) {
                processed_arr = new Functions().getRandomMainPlaces();
            }
        } else {
            processed_arr = new Functions().getRandomMainPlaces();
        }

        ArrayList<String> finalProcessed_arr = processed_arr;
        db.collection("search_collection")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        ArrayList<SearchItemModel> final_processed_data = new ArrayList<>();
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                SearchItemModel searchItemModel = new SearchItemModel(documentSnapshot.getString("sdb_name"),
                                        documentSnapshot.getString("smain_place"),
                                        documentSnapshot.getString("sinner_place"),
                                        documentSnapshot.getString("s_place_name"),
                                        documentSnapshot.getString("s_place_img"));
                                all_viewed_array.add(searchItemModel);
                            }

                            for (int i = 0; i < finalProcessed_arr.size(); i++) {
                                for (int j = 0; j < all_viewed_array.size(); j++) {
                                    if (finalProcessed_arr.get(i).equals(all_viewed_array.get(j).getSmain_place()) && all_viewed_array.get(j).getSinner_place() != null) {
                                        final_processed_data.add(all_viewed_array.get(j));
                                    }
                                }
                            }

                            for (int i = 0; i < final_processed_data.size(); i++) {
                                Log.e("final_processed_data", final_processed_data.get(i).getSinner_place());
                            }

                            Collections.shuffle(final_processed_data);
                            home_page_some_viewed_rec.setLayoutManager(new LinearLayoutManager(getContext()));
                            ProcessDataAdapter processDataAdapter = new ProcessDataAdapter(getContext(), final_processed_data);
                            home_page_some_viewed_rec.setAdapter(processDataAdapter);
                            processDataAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void analysisNotificationMethod() {

        SharedPreferences prefs = requireContext().getSharedPreferences("app_data", Activity.MODE_PRIVATE);
        boolean notifi_check = prefs.getBoolean("notification_check", true);

        if (notifi_check) {
            if (!firebaseUser.isAnonymous()) {
                if (analyticDBHelper.getdbcount() > 0) {
                    for (int i = 0; i < all_trips_db.size(); i++) {
                        String db_string = all_trips_names.get(i);
                        Bitmap icon_bitmap = BitmapFactory.decodeResource(getResources(), tripList.all_trip_images().get(i));
                        long getValue = analyticDBHelper.getall_db(all_trips_db.get(i));
                        if (getValue >= 3) {
                            functions.notificationChannel(getContext());
                            Intent notificationintent = new Intent(getContext(), CategoryActivity.class);
                            notificationintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            notificationintent.putExtra("category_pos", i);
                            notificationintent.putExtra("analysis_counter", false);
                            int uniqueInt = (int) (System.currentTimeMillis() & 0xff);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), uniqueInt, notificationintent, PendingIntent.FLAG_UPDATE_CURRENT);

                            Notification notification = new NotificationCompat.Builder(requireContext(), "notifytheapp")
                                    .setContentIntent(pendingIntent)
                                    .setSmallIcon(R.mipmap.ic_launcher_round)
                                    .setContentTitle("New Update for you from TourBuddy !")
                                    .setContentText("See places related to " + db_string + " trips.")
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setLargeIcon(icon_bitmap)
                                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(icon_bitmap).bigLargeIcon(null))
                                    .setAutoCancel(true).build();

                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(requireContext());
                            notificationManagerCompat.notify(200, notification);

                            analyticDBHelper.deletePlace();

                        }
                    }
                }
            }
        }
    }

    private void homeFragEventListnerMoethods() {

        home_main_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MainSettingsActivity.class));
            }
        });

        home_searchbar.getEditText().setOnClickListener(v -> {
            startActivity(new Intent(getContext(), SearchActivity.class));
            getActivity().overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
        });

        home_appbarlay.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) == home_appbarlay.getTotalScrollRange()) {
                home_searchbar.setVisibility(View.GONE);
                home_main_toolbar.setVisibility(View.VISIBLE);
                new Functions().setStatusBarColor(getActivity(), R.color.heading_no);
            } else {
                home_searchbar.setVisibility(View.VISIBLE);
                home_main_toolbar.setVisibility(View.GONE);
                functions.darkstatusbardesign(requireActivity());
            }
        });

        home_frag_swipe_refersh.setOnRefreshListener(() -> {

            allHomePageCategoryRecycler();
            recentPlaceRecycler();
            trendPosterSliderFunction();
            homePrefsFun();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    home_frag_swipe_refersh.setRefreshing(false);
                }
            }, 2000);
        });

    }

    private void homeInits(@NotNull View view) {

        home_main_settings = view.findViewById(R.id.home_main_settings);
        home_page_some_viewed_rec = view.findViewById(R.id.home_page_some_viewed_rec);
        main_user_recycler = view.findViewById(R.id.main_user_recycler);
        image_slider = view.findViewById(R.id.imageSlider);
        home_frag_swipe_refersh = view.findViewById(R.id.home_frag_swipe_refersh);
        home_frag_prog_bar = view.findViewById(R.id.home_frag_prog_bar);
        firebaseAuth = FirebaseAuth.getInstance();
        home_searchbar = view.findViewById(R.id.home_searchbar);
        home_appbarlay = view.findViewById(R.id.home_appbarlay);
        category_recycler = view.findViewById(R.id.category_recycler);
        analyticDBHelper = new AnalyticDBHelper(getContext());
        recentPlaceDBHelper = new RecentPlaceDBHelper(getContext());
        home_main_toolbar = view.findViewById(R.id.home_main_toolbar);
        recent_places_container = view.findViewById(R.id.recent_places_container);
        recent_viewed_recycler = view.findViewById(R.id.recent_viewed_recycler);
        db = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        home_frag_swipe_refersh.setEnabled(false);
        home_frag_swipe_refersh.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.light_color),
                ContextCompat.getColor(getContext(), R.color.my_orange),
                ContextCompat.getColor(getContext(), R.color.my_red));

    }

    private void homePrefsFun() {
        if (firebaseAuth.getCurrentUser() != null) {
            if (firebaseAuth.getCurrentUser().isAnonymous()) {
                processPrefs(tripList.guest_trip_array());
            } else {
                db.collection("users")
                        .document(firebaseUser.getUid())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {

                            pref_list_db_names.clear();
                            pref_list_db_names = (ArrayList<String>) documentSnapshot.get("myprefs");
                            processPrefs(pref_list_db_names);
                        });
            }
        }
    }

    private void allHomePageCategoryRecycler() {
        HomeAllCatListAdapter homeAllCatListAdapter = new HomeAllCatListAdapter(getContext(), all_cat_img, all_trips_names);
        category_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        category_recycler.setAdapter(homeAllCatListAdapter);
    }

    private void trendPosterSliderFunction() {

        db.collection("trend_collection")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        if (threndPosterarray.size() > 0) {
                            threndPosterarray.clear();
                        }

                        for (DocumentSnapshot documentSnapshot : task.getResult()) {

                            SearchItemModel searchItemModel = new SearchItemModel(documentSnapshot.getString("sdb_name"),
                                    documentSnapshot.getString("smain_place"),
                                    documentSnapshot.getString("sinner_place"),
                                    documentSnapshot.getString("s_place_name"),
                                    documentSnapshot.getString("s_place_img"));
                            threndPosterarray.add(searchItemModel);
                            imageSlideAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.e("trendPosterData", "failed");
                    }
                });
        imageSlideAdapter = new ImageSlideAdapter(getContext(), threndPosterarray, 0);
        imageSlideAdapter.notifyDataSetChanged();
        image_slider.setSliderAdapter(imageSlideAdapter);
        image_slider.setIndicatorAnimation(IndicatorAnimationType.WORM);
        image_slider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
    }

    private void recentPlaceRecycler() {

        ArrayList<SearchItemModel> to_fun_all_recent_array = new ArrayList<>();

        if (recentPlaceDBHelper.checkRecentTableExist() > 0) {
            if (recentPlaceDBHelper.getRecentcolname().size() > 0) {

                recent_places_container.setVisibility(View.VISIBLE);

                if (to_fun_all_recent_array.size() > 0) {
                    to_fun_all_recent_array.clear();
                }

                to_fun_all_recent_array = recentPlaceDBHelper.getRecentPlacesArrays();
                Collections.reverse(to_fun_all_recent_array);

                recent_viewed_recycler.setHasFixedSize(true);
                recent_viewed_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                RecentPlaceAdapter recentSearchAdapter = new RecentPlaceAdapter(getContext(), to_fun_all_recent_array, recentPlaceDBHelper);
                recentSearchAdapter.notifyDataSetChanged();
                recent_viewed_recycler.setAdapter(recentSearchAdapter);

            } else {
                recent_places_container.setVisibility(View.GONE);
            }
        }
    }

    private void processPrefs(ArrayList<String> pref_list_db_names) {

        selected_prefs_db.clear();
        selected_prefs_titles.clear();
        inner_list_data.clear();
        main_trip_list.clear();

        if (pref_list_db_names != null && !pref_list_db_names.isEmpty()) {
            for (int i = 0; i < Objects.requireNonNull(pref_list_db_names).size(); i++) {

                for (int j = 0; j < all_trips_names.size(); j++) {

                    if (pref_list_db_names.get(i).equals(all_trips_names.get(j))) {
                        selected_prefs_db.add(all_trips_db.get(j));
                        selected_prefs_titles.add(all_trips_home_titles.get(j));
                        break;
                    }
                }
            }

            for (int i = 0; i < selected_prefs_db.size(); i++) {
                int finalI = i;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<CategoryModel> prefs_array = sub_recycler_list(selected_prefs_db.get(finalI));
                        inner_list_data.add(prefs_array);
                    }
                }, 1000);
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Log.e("main_array_size", "" + inner_list_data.size());
                    if (inner_list_data.size() > 0) {

                        for (int i = 0; i < inner_list_data.size(); i++) {

                            if (inner_list_data.get(0).size() > 0) {
                                home_frag_prog_bar.setVisibility(View.GONE);
                                main_user_recycler.setVisibility(View.VISIBLE);
                            }

                            main_trip_list.add(new homemain_rec_model(selected_prefs_titles.get(i), inner_list_data.get(i)));
                        }
                        setmainlistrecycler(main_trip_list);
                    }
                }
            }, 2000);
        }

    }

    private void setmainlistrecycler(ArrayList<homemain_rec_model> main_trip_list) {
        main_user_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        HomeMainListAdapter homeMainListAdapter = new HomeMainListAdapter(getContext(), main_trip_list);
        main_user_recycler.setAdapter(homeMainListAdapter);
        home_frag_swipe_refersh.setEnabled(true);
    }

    private @NotNull ArrayList<CategoryModel> sub_recycler_list(String pref_db_name) {

        ArrayList<CategoryModel> prefs_array = new ArrayList<>();

        if (prefs_array.size() > 0) {
            prefs_array.clear();
        }

        CollectionReference collection = db.collection(pref_db_name);
        collection.limit(6)
                .get()
                .addOnCompleteListener(task -> {

                    for (DocumentSnapshot documentSnapshot : task.getResult()) {

                        ArrayList<String> main_places_images = (ArrayList<String>) documentSnapshot.get("images");
                        CategoryModel categoryModel = new CategoryModel(documentSnapshot.getString("place_id"),
                                documentSnapshot.getString("place_name"),
                                documentSnapshot.getString("description"),
                                documentSnapshot.getString("attractions"),
                                documentSnapshot.getString("best_time"),
                                documentSnapshot.getString("rate_place"),
                                documentSnapshot.getString("climate"),
                                documentSnapshot.getString("reach_method"),
                                (Double) documentSnapshot.get("longitude"),
                                (Double) documentSnapshot.get("latitude"),
                                main_places_images,
                                pref_db_name,
                                main_places_images.get(0));
                        prefs_array.add(categoryModel);
                    }

                    Collections.shuffle(prefs_array);
                })
                .addOnFailureListener(e -> Log.e("slec_int", "" + e.getMessage()));

        return prefs_array;
    }

    @Override
    public void onResume() {
        super.onResume();
        recentPlaceRecycler();
    }

    public static class RecentPlaceAdapter extends RecyclerView.Adapter<RecentPlaceAdapter.ListViewHolder> {

        private final Context context;
        ArrayList<SearchItemModel> all_recent_viewed_array;
        FirebaseFirestore db;
        RecentPlaceDBHelper recentPlaceDBHelper;

        public RecentPlaceAdapter(Context context, ArrayList<SearchItemModel> all_recent_viewed_array, RecentPlaceDBHelper recentPlaceDBHelper) {
            this.context = context;
            this.all_recent_viewed_array = all_recent_viewed_array;
            this.recentPlaceDBHelper = recentPlaceDBHelper;
        }

        @NonNull
        @Override
        public RecentPlaceAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.hinnertripitems, null);
            return new ListViewHolder(inflate);
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onBindViewHolder(@NonNull RecentPlaceAdapter.ListViewHolder holder, int position) {

            if (all_recent_viewed_array.size() <= 10) {
                db = FirebaseFirestore.getInstance();

                Log.e("adapter_place_name", all_recent_viewed_array.get(position).getS_place_name());
                holder.inner_trip_title.setText(all_recent_viewed_array.get(position).getS_place_name());
                Glide.with(context)
                        .load(all_recent_viewed_array.get(position).getS_place_img())
                        .fitCenter()
                        .into(holder.inner_trip_img);

                holder.inner_trip_lay.setOnClickListener(v -> {

                    String search_db_name = all_recent_viewed_array.get(position).getSdb_name();
                    String search_main_place = all_recent_viewed_array.get(position).getSmain_place();
                    String search_inner_place = all_recent_viewed_array.get(position).getSinner_place();

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
                db = FirebaseFirestore.getInstance();

                int remaining = all_recent_viewed_array.size() - 10;
                if (position == 10) {
                    holder.inner_trip_lay.setVisibility(View.GONE);
                    holder.hinner_moreitems_lay.setVisibility(View.VISIBLE);
                    holder.hinner_more_txt.setText(String.format("%d more places", remaining));

                    holder.hinner_moreitems_lay.setOnClickListener(v -> {
                        Intent intent = new Intent(context, ShowAllActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putParcelableArrayListExtra("recent_arraylist", all_recent_viewed_array);
                        context.startActivity(intent);
                    });
                } else {
                    holder.inner_trip_lay.setVisibility(View.VISIBLE);
                    holder.hinner_moreitems_lay.setVisibility(View.GONE);
                    Log.e("adapter_place_name", all_recent_viewed_array.get(position).getS_place_name());
                    holder.inner_trip_title.setText(all_recent_viewed_array.get(position).getS_place_name());
                    Glide.with(context)
                            .load(all_recent_viewed_array.get(position).getS_place_img())
                            .fitCenter()
                            .into(holder.inner_trip_img);

                    holder.inner_trip_lay.setOnClickListener(v -> {

                        String search_db_name = all_recent_viewed_array.get(position).getSdb_name();
                        String search_main_place = all_recent_viewed_array.get(position).getSmain_place();
                        String search_inner_place = all_recent_viewed_array.get(position).getSinner_place();

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
                }
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
            if (all_recent_viewed_array.size() > 10) {
                return 11;
            } else {
                return all_recent_viewed_array.size();
            }
        }

        public static class ListViewHolder extends RecyclerView.ViewHolder {

            View itemView;
            TextView inner_trip_title, hinner_more_txt;
            ImageView inner_trip_img;
            RelativeLayout inner_trip_lay, hinner_moreitems_lay;

            public ListViewHolder(@NonNull View itemView) {
                super(itemView);

                inner_trip_title = itemView.findViewById(R.id.inner_trip_title);
                inner_trip_img = itemView.findViewById(R.id.inner_trip_img);
                inner_trip_lay = itemView.findViewById(R.id.inner_trip_lay);

                hinner_more_txt = itemView.findViewById(R.id.hinner_more_txt);
                hinner_moreitems_lay = itemView.findViewById(R.id.hinner_moreitems_lay);
                this.itemView = itemView;
            }
        }

    }

    public static class HomeAllCatListAdapter extends RecyclerView.Adapter<HomeAllCatListAdapter.ListViewHolder> {

        private final ArrayList<Integer> img_src;
        private final ArrayList<String> img_title;
        private final Context context;

        public HomeAllCatListAdapter(Context context, ArrayList<Integer> img_src, ArrayList<String> img_title) {
            this.context = context;
            this.img_src = img_src;
            this.img_title = img_title;
        }

        @NonNull
        @Override
        public HomeAllCatListAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.placeitems, null);
            return new ListViewHolder(inflate);
        }

        @Override
        public void onBindViewHolder(@NonNull HomeAllCatListAdapter.ListViewHolder holder, int position) {
            holder.place_item_img.setImageDrawable(ContextCompat.getDrawable(context, img_src.get(position)));
            holder.place_item_title.setText(img_title.get(position));
            holder.place_item_lay.setOnClickListener(v -> {
                Intent intent = new Intent(context, CategoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("category_pos", position);
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
            return img_src.size();
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