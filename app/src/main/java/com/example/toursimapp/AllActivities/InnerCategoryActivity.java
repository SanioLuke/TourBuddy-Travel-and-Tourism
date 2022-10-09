package com.example.toursimapp.AllActivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toursimapp.Adapters.HotelsAdpater;
import com.example.toursimapp.Adapters.ImageSlideAdapter;
import com.example.toursimapp.LocalDB.RecentPlaceDBHelper;
import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.Models.HotelDetailModel;
import com.example.toursimapp.Models.ImageSliderModel;
import com.example.toursimapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InnerCategoryActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final ArrayList<ImageSliderModel> inner_placeimageearray = new ArrayList<>();
    private final ArrayList<HotelDetailModel> hotel_details_array = new ArrayList<>();
    NestedScrollView inner_cat_nested_scroll;
    View inner_cat_main_lay;
    private TextView inner_cat_place_name, inner_cat_rate, inner_cat_des, inner_cat_features, inner_cat_addcomment_guest_acc_msg;
    private ImageButton inner_cat_back_btn;
    private FloatingActionButton inner_cat_fav;
    private String inner_place_name_txt;
    private Double inner_cat_longitude, inner_cat_latitude;
    private SupportMapFragment inner_cat_location;
    private RecentPlaceDBHelper recentPlaceDBHelper;
    private SliderView inner_cat_imageSlider;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private String db_name, main_place_id, inner_place_id;
    private TextView inner_cat_comment_user1, inner_cat_comment_time1, inner_cat_comment_text1;
    private View inner_cat_comment_lay, inner_cat_add_comment_lay;
    private Button inner_cat_more_btn;
    private EditText inner_cat_comment_add_txt;
    private ImageButton inner_comment_send_btn;
    private final TextWatcher add_comment_check = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String add_comment_txt = inner_cat_comment_add_txt.getText().toString();
            inner_comment_send_btn.setEnabled(!add_comment_txt.isEmpty() && !add_comment_txt.equals("") && add_comment_txt.length() != 0);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private String rate;
    private ArrayList<Map<Object, String>> inner_comments = new ArrayList<>();
    private RecyclerView curr_inner_hotel_rec;
    private HotelsAdpater hotelsAdpater;
    private String user_col_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Functions().checkTheme(getApplicationContext());
        setContentView(R.layout.activity_inner_category);

        innerCategoryInits();
        allValuesSet_recent_place_set_fun();
        getAllComments();
        favBtnColorSetFun();
        hotel_places_rec_fun();
        innerCategoryEventListnerFun();
        inner_cat_location.getMapAsync(this);
    }

    private void hotel_places_rec_fun() {
        db.collection(db_name)
                .document(main_place_id)
                .collection("hotels_section")
                .whereEqualTo("inner_place_id", inner_place_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    @SuppressWarnings("all")
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                Log.e("Inner_Hotel_data", "Success :)");

                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                    HotelDetailModel hotelDetailModel = new HotelDetailModel(
                                            db_name,
                                            main_place_id,
                                            inner_place_id,
                                            documentSnapshot.getString("hotel_id"),
                                            documentSnapshot.getString("hotel_name"),
                                            documentSnapshot.getString("place_name"),
                                            documentSnapshot.getString("hotel_rate"),
                                            documentSnapshot.getString("hotel_des"),
                                            (Double) documentSnapshot.get("hotel_longitude"),
                                            (Double) documentSnapshot.get("hotel_latitude"),
                                            (ArrayList<String>) documentSnapshot.get("hotel_img"),
                                            (ArrayList<String>) documentSnapshot.get("hotel_fac"),
                                            (ArrayList<String>) documentSnapshot.get("hotel_contact"),
                                            (long) documentSnapshot.get("people_no_booking"),
                                            (long) documentSnapshot.get("initial_price"),
                                            (long) documentSnapshot.get("service_fee"),
                                            (long) documentSnapshot.get("tax_gst_fee"),
                                            (long) documentSnapshot.get("people_accomodation_no"));
                                    hotel_details_array.add(hotelDetailModel);
                                }

                                curr_inner_hotel_rec.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
                                hotelsAdpater = new HotelsAdpater(getApplicationContext(), hotel_details_array);
                                hotelsAdpater.notifyDataSetChanged();
                                curr_inner_hotel_rec.setAdapter(hotelsAdpater);
                            } else {
                                Log.e("Inner_Hotel_data", "Null data");
                            }
                        } else {
                            Log.e("Inner_Hotel_data", "Error Followed !");
                        }
                    }
                });
    }

    private void innerCategoryEventListnerFun() {

        inner_cat_more_btn.setOnClickListener(v -> {
            Intent intent1 = new Intent(getApplicationContext(), ShowCommentActivity.class);
            intent1.putExtra("all_comments_array", inner_comments);
            intent1.putExtra("rate", Float.valueOf(rate));
            intent1.putExtra("page_pos", 2);
            startActivity(intent1);
        });

        inner_cat_back_btn.setOnClickListener(v -> finish());

        inner_cat_comment_add_txt.addTextChangedListener(add_comment_check);

        inner_comment_send_btn.setOnClickListener(v -> {

            SharedPreferences get_user_data_prefs = getSharedPreferences("user_data", Activity.MODE_PRIVATE);
            String comment_username = get_user_data_prefs.getString("fullname", "User");

            String add_comment_txt = inner_cat_comment_add_txt.getText().toString();

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM y, hh:mm aa");
            String formattedDate = dateFormat.format(new Date());

            Map<Object, String> my_comment_map = new HashMap<>();
            my_comment_map.put("inner_comment_user", comment_username);
            my_comment_map.put("inner_comment_time", formattedDate);
            my_comment_map.put("inner_comment_txt", add_comment_txt);

            ArrayList<Map<Object, String>> comment_array = new ArrayList<>();
            if (inner_comments != null && inner_comments.size() > 0) {
                comment_array.addAll(inner_comments);
                comment_array.add(0, my_comment_map);
            } else {
                comment_array.add(0, my_comment_map);
            }

            db.collection(db_name)
                    .document(main_place_id)
                    .collection("inner_places")
                    .document(inner_place_id)
                    .update("inner_comments", comment_array)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Snackbar.make(inner_cat_main_lay, "Comment Added", Snackbar.LENGTH_SHORT).show();
                                inner_cat_comment_add_txt.setText("");
                                getAllComments();
                            }
                        }
                    });
        });

        inner_cat_fav.setOnClickListener(v -> {
            db.collection(user_col_name)
                    .document(firebaseUser.getUid())
                    .collection("favourites")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                if (Objects.requireNonNull(task.getResult()).size() > 0) {

                                    int count = 1;
                                    for (DocumentSnapshot documentSnapshot : task.getResult()) {

                                        String fdb_db_name = documentSnapshot.getString("fav_db_name");
                                        String fmain_place_id = documentSnapshot.getString("fav_main_place_id");
                                        String finner_place_id = documentSnapshot.getString("fav_inner_place_id");

                                        if (fdb_db_name != null && fmain_place_id != null && finner_place_id != null) {
                                            if (fdb_db_name.equals(db_name) && fmain_place_id.equals(main_place_id) && finner_place_id.equals(inner_place_id)) {

                                                db.collection(user_col_name)
                                                        .document(firebaseUser.getUid())
                                                        .collection("favourites")
                                                        .document(finner_place_id)
                                                        .delete()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Snackbar.make(inner_cat_main_lay, "Removed from favourites", Snackbar.LENGTH_SHORT).show();
                                                                inner_cat_fav.setImageTintList(ColorStateList.valueOf(Color.GRAY));
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                inner_cat_fav.setImageTintList(ColorStateList.valueOf(Color.RED));
                                                            }
                                                        });
                                                break;
                                            } else {
                                                if (count == task.getResult().size()) {
                                                    addToFav(user_col_name, db_name, main_place_id, inner_place_id, inner_place_name_txt, firebaseUser.getUid(), inner_cat_fav);
                                                    break;
                                                }
                                            }
                                        } else {
                                            if (count == task.getResult().size()) {
                                                addToFav(user_col_name, db_name, main_place_id, inner_place_id, inner_place_name_txt, firebaseUser.getUid(), inner_cat_fav);
                                                break;
                                            }
                                        }
                                        count++;
                                    }
                                } else {
                                    addToFav(user_col_name, db_name, main_place_id, inner_place_id, inner_place_name_txt, firebaseUser.getUid(), inner_cat_fav);
                                }
                            }
                        }
                    });
        });

        inner_cat_nested_scroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY) {
                inner_cat_fav.hide();
            } else {
                inner_cat_fav.show();
            }
        });

    }

    private void favBtnColorSetFun() {
        db.collection(user_col_name)
                .document(firebaseUser.getUid())
                .collection("favourites")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            String fdb_db_name = documentSnapshot.getString("fav_db_name");
                            String fmain_place_id = documentSnapshot.getString("fav_main_place_id");
                            String finner_place_id = documentSnapshot.getString("fav_inner_place_id");

                            if (fdb_db_name != null && fmain_place_id != null && finner_place_id != null) {
                                if (fdb_db_name.equals(db_name) && fmain_place_id.equals(main_place_id) && finner_place_id.equals(inner_place_id)) {
                                    inner_cat_fav.setImageTintList(ColorStateList.valueOf(Color.RED));
                                    break;
                                } else {
                                    inner_cat_fav.setImageTintList(ColorStateList.valueOf(Color.GRAY));
                                }
                            }
                        }
                    }
                });
    }

    private void allValuesSet_recent_place_set_fun() {
        Intent getinnerdata = getIntent();
        Bundle bundle = getinnerdata.getBundleExtra("curr_innerdetails_bundle");

        if (bundle != null && !bundle.isEmpty()) {

            inner_place_name_txt = bundle.getString("inner_place_name");
            inner_cat_place_name.setText(inner_place_name_txt);
            rate = bundle.getString("inner_rating");
            inner_cat_des.setText(bundle.getString("inner_description"));
            db_name = bundle.getString("db_name");
            main_place_id = bundle.getString("main_place_id");
            inner_place_id = bundle.getString("inner_place_id");
            inner_cat_longitude = bundle.getDouble("inner_longitude");
            inner_cat_latitude = bundle.getDouble("inner_latitude");

            inner_cat_rate.setText(rate);
            ArrayList<String> inner_features = bundle.getStringArrayList("inner_features");
            setFeatures(inner_features);

            ArrayList<String> inner_cat_images = bundle.getStringArrayList("inner_images");
            setImageSlider(inner_cat_images);

            if (recentPlaceDBHelper.getRecentcolname().size() > 0) {
                for (int i = 0; i < recentPlaceDBHelper.getRecentcolname().size(); i++) {

                    String local_placename = recentPlaceDBHelper.getRecentPlaceName().get(i);
                    if (local_placename.equals(inner_place_name_txt)) {
                        if (recentPlaceDBHelper.deleteRecentPlace(db_name, main_place_id, inner_place_name_txt, inner_cat_images.get(0))) {
                            if (recentPlaceDBHelper.addRecentPlace(db_name, main_place_id, inner_place_id, inner_place_name_txt, inner_cat_images.get(0))) {
                                Log.e("recent_place_db", "entered");
                            } else {
                                Log.e("recent_place_db", "failed!!!");
                            }
                            break;
                        }
                    } else {
                        if (i == recentPlaceDBHelper.getRecentcolname().size() - 1) {
                            if (recentPlaceDBHelper.addRecentPlace(db_name, main_place_id, inner_place_id, inner_place_name_txt, inner_cat_images.get(0))) {
                                Log.e("recent_place_db", "entered");
                            } else {
                                Log.e("recent_place_db", "failed!!!");
                            }
                            break;
                        }
                    }
                }
            } else {
                if (recentPlaceDBHelper.addRecentPlace(db_name, main_place_id, inner_place_id, inner_place_name_txt, inner_cat_images.get(0))) {
                    Log.e("recent_place_db", "entered");
                } else {
                    Log.e("recent_place_db", "failed!!!");
                }
            }
        }
    }

    private void innerCategoryInits() {
        inner_cat_main_lay = findViewById(R.id.inner_cat_main_lay);
        inner_cat_imageSlider = findViewById(R.id.inner_cat_imageSlider);
        inner_cat_place_name = findViewById(R.id.inner_cat_place_name);
        inner_cat_rate = findViewById(R.id.inner_cat_rate);
        inner_cat_des = findViewById(R.id.inner_cat_des);
        inner_cat_features = findViewById(R.id.inner_cat_features);
        inner_cat_location = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.inner_cat_location);
        inner_cat_back_btn = findViewById(R.id.inner_cat_back_btn);
        inner_cat_fav = findViewById(R.id.inner_cat_fav);
        inner_cat_nested_scroll = findViewById(R.id.inner_cat_nested_scroll);
        recentPlaceDBHelper = new RecentPlaceDBHelper(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        inner_cat_comment_user1 = findViewById(R.id.inner_cat_comment_user1);
        inner_cat_comment_time1 = findViewById(R.id.inner_cat_comment_time1);
        inner_cat_comment_text1 = findViewById(R.id.inner_cat_comment_text1);
        inner_cat_comment_lay = findViewById(R.id.inner_cat_comment_lay);
        inner_cat_more_btn = findViewById(R.id.inner_cat_more_btn);

        inner_cat_comment_add_txt = findViewById(R.id.inner_cat_comment_add_txt);
        inner_comment_send_btn = findViewById(R.id.inner_cat_comment_send_btn);
        inner_comment_send_btn.setEnabled(false);
        user_col_name = "users";

        curr_inner_hotel_rec = findViewById(R.id.curr_inner_hotel_rec);
        inner_cat_addcomment_guest_acc_msg = findViewById(R.id.inner_cat_addcomment_guest_acc_msg);
        inner_cat_add_comment_lay = findViewById(R.id.inner_cat_add_comment_lay);

        if (firebaseUser.isAnonymous()) {
            inner_cat_addcomment_guest_acc_msg.setVisibility(View.VISIBLE);
            inner_cat_add_comment_lay.setVisibility(View.GONE);
            Animation bounce_hotel_get_registered_lay = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce_anim);
            inner_cat_addcomment_guest_acc_msg.startAnimation(bounce_hotel_get_registered_lay);
        } else {
            inner_cat_addcomment_guest_acc_msg.setVisibility(View.GONE);
            inner_cat_add_comment_lay.setVisibility(View.VISIBLE);
        }
    }

    private void getAllComments() {
        db.collection(db_name)
                .document(main_place_id)
                .collection("inner_places")
                .document(inner_place_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    @SuppressWarnings("unchecked")
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                inner_comments = (ArrayList<Map<Object, String>>) task.getResult().get("inner_comments");

                                if (inner_comments != null && !inner_comments.isEmpty()) {
                                    inner_cat_comment_lay.setVisibility(View.VISIBLE);
                                    inner_cat_comment_user1.setText(inner_comments.get(0).get("inner_comment_user"));
                                    inner_cat_comment_time1.setText(inner_comments.get(0).get("inner_comment_time"));
                                    inner_cat_comment_text1.setText(inner_comments.get(0).get("inner_comment_txt"));

                                    if (inner_comments.size() > 1) {
                                        inner_cat_more_btn.setVisibility(View.VISIBLE);
                                        inner_cat_more_btn.setText("Show all " + inner_comments.size() + " reviews");
                                    } else {
                                        inner_cat_more_btn.setVisibility(View.GONE);
                                    }
                                } else {
                                    inner_cat_comment_lay.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            Log.e("comments", "error");
                        }
                    }
                });
    }

    private void addToFav(String user_col_name, String database, String main, String inner, String name, String uid, FloatingActionButton fav_btn) {
        Map<String, Object> map = new HashMap<>();
        map.put("fav_db_name", database);
        map.put("fav_main_place_id", main);
        map.put("fav_inner_place_id", inner);
        map.put("fav_place_name", name);

        db.collection(user_col_name)
                .document(uid)
                .collection("favourites")
                .document(inner)
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(inner_cat_main_lay, "Added to favourites", Snackbar.LENGTH_SHORT).show();
                            fav_btn.setImageTintList(ColorStateList.valueOf(Color.RED));
                        } else {
                            fav_btn.setImageTintList(ColorStateList.valueOf(Color.GRAY));
                        }
                    }
                });
    }

    private void setImageSlider(@NotNull ArrayList<String> inner_cat_img) {

        for (int i = 0; i < inner_cat_img.size(); i++) {
            inner_placeimageearray.add(new ImageSliderModel(inner_cat_img.get(i)));
        }

        ImageSlideAdapter imageSlideAdapter = new ImageSlideAdapter(getApplicationContext(), 1);
        imageSlideAdapter.renewItems(inner_placeimageearray);
        inner_cat_imageSlider.startAutoCycle();
        inner_cat_imageSlider.setSliderAdapter(imageSlideAdapter);
        inner_cat_imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
        inner_cat_imageSlider.setAutoCycle(true);
    }

    private void setFeatures(@NotNull ArrayList<String> inner_features) {
        StringBuilder all_fea_string = new StringBuilder();
        for (int i = 0; i < inner_features.size(); i++) {
            all_fea_string.append("\u25A0" + "  ").append(inner_features.get(i)).append("\n\n");
        }
        inner_cat_features.setText(all_fea_string);
    }

    @Override
    public void onMapReady(@NotNull GoogleMap googleMap) {
        LatLng mappoints = new LatLng(inner_cat_longitude, inner_cat_latitude);
        googleMap.addMarker(new MarkerOptions().position(mappoints).title(inner_place_name_txt));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mappoints, 10F));
    }

    @Override
    protected void onResume() {
        super.onResume();
        inner_cat_imageSlider.startAutoCycle();
    }

    @Override
    protected void onPause() {
        super.onPause();
        inner_cat_imageSlider.stopAutoCycle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inner_cat_imageSlider.stopAutoCycle();
    }

}