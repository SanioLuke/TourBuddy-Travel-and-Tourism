package com.example.toursimapp.AllActivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.toursimapp.Adapters.HotelsAdpater;
import com.example.toursimapp.Adapters.ImageSlideAdapter;
import com.example.toursimapp.LocalDB.RecentPlaceDBHelper;
import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.Models.HotelDetailModel;
import com.example.toursimapp.Models.ImageSliderModel;
import com.example.toursimapp.Models.InnerCategoryModel;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainPlaceActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final ArrayList<InnerCategoryModel> inner_cat_array = new ArrayList<>();
    private final ArrayList<ImageSliderModel> curr_placeimageearray = new ArrayList<>();
    NestedScrollView mplace_nest_scroll;
    View mplace_main_lay;
    String curr_rate_txt;
    private TextView curr_place_name, curr_rate, curr_des, curr_attr, curr_climate, curr_how_reach, curr_besttime, curr_des_expend, header_sub_places;
    private String curr_place_id;
    private String curr_place_name_txt, curr_db_name;
    private ImageButton mplace_back_btn;
    private FloatingActionButton curr_fav;
    private RecyclerView curr_inner_places_rec;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private InnerCategoryModel innerCategoryModel;
    private InnerCatgegoryItemAdapter innerCatgegoryItemAdapter;
    private RecentPlaceDBHelper recentPlaceDBHelper;
    private Double curr_longitude, curr_latitude;
    private GoogleMap mapAPI;
    private SupportMapFragment curr_location;
    private ArrayList<String> curr_array_images;
    private SliderView mplace_imageSlider;
    private ImageSlideAdapter imageSlideAdapter;
    private int readcheck = 0;
    private ScaleAnimation scaleAnimation;
    private BounceInterpolator bounceInterpolator;
    private ArrayList<Map<Object, String>> mplace_comments;
    private TextView mplace_addcomment_guest_acc_msg, mplace_comment_user1, mplace_comment_time1, mplace_comment_text1;
    private View mplace_addcomment_lay, mplace_comment_lay, mplace_first_comment_lay;
    private EditText mplace_add_comment_box;
    private Button mplace_more_btn;
    private ImageButton mplace_addcomment_btn;
    private final TextWatcher mplace_add_comment = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String mplace_addcomment_txt = mplace_add_comment_box.getText().toString();
            mplace_addcomment_btn.setEnabled(!mplace_addcomment_txt.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private RecyclerView mplace_hotels_rec;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Functions().checkTheme(getApplicationContext());
        setContentView(R.layout.activity_main_place);

        String user_col_name;
        mplace_main_lay = findViewById(R.id.mplace_main_lay);
        header_sub_places = findViewById(R.id.header_sub_places);
        mplace_back_btn = findViewById(R.id.mplace_back_btn);
        curr_place_name = findViewById(R.id.curr_place_name);
        mplace_nest_scroll = findViewById(R.id.mplace_nest_scroll);
        curr_inner_places_rec = findViewById(R.id.curr_inner_places_rec);
        curr_rate = findViewById(R.id.curr_rate);
        curr_des = findViewById(R.id.curr_des);
        curr_des_expend = findViewById(R.id.curr_des_expend);
        curr_attr = findViewById(R.id.curr_attr);
        curr_climate = findViewById(R.id.curr_climate);
        curr_how_reach = findViewById(R.id.curr_how_reach);
        curr_besttime = findViewById(R.id.curr_besttime);
        mplace_imageSlider = findViewById(R.id.mplace_imageSlider);
        curr_fav = findViewById(R.id.curr_fav);

        mplace_addcomment_guest_acc_msg = findViewById(R.id.mplace_addcomment_guest_acc_msg);
        mplace_comment_user1 = findViewById(R.id.mplace_comment_user1);
        mplace_comment_time1 = findViewById(R.id.mplace_comment_time1);
        mplace_comment_text1 = findViewById(R.id.mplace_comment_text1);
        mplace_addcomment_lay = findViewById(R.id.mplace_addcomment_lay);
        mplace_comment_lay = findViewById(R.id.mplace_comment_lay);
        mplace_comment_lay = findViewById(R.id.mplace_comment_lay);
        mplace_first_comment_lay = findViewById(R.id.mplace_first_comment_lay);
        mplace_add_comment_box = findViewById(R.id.mplace_add_comment_box);
        mplace_more_btn = findViewById(R.id.mplace_more_btn);
        mplace_addcomment_btn = findViewById(R.id.mplace_addcomment_btn);
        mplace_hotels_rec = findViewById(R.id.mplace_hotels_rec);
        mplace_addcomment_btn.setEnabled(false);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        recentPlaceDBHelper = new RecentPlaceDBHelper(getApplicationContext());
        curr_location = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.curr_location);

        Animation bounce_hotel_get_registered_lay = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce_anim);
        mplace_addcomment_guest_acc_msg.startAnimation(bounce_hotel_get_registered_lay);

        user_col_name = "users";

        curr_des_expend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readcheck == 0) {
                    curr_des.setEllipsize(null);
                    curr_des.setMaxLines(Integer.MAX_VALUE);
                    curr_des_expend.setText("Read   Less");
                    readcheck = 1;
                } else {
                    curr_des.setEllipsize(TextUtils.TruncateAt.END);
                    curr_des.setMaxLines(3);
                    curr_des_expend.setText("Read More");
                    readcheck = 0;
                }
            }
        });

        mplace_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle bundle = new Bundle();
        Intent intent = getIntent();
        bundle = intent.getBundleExtra("curr_details_bundle");

        if (bundle != null && !bundle.isEmpty()) {

            curr_place_name_txt = bundle.getString("curr_place_name", "Place Name");
            curr_longitude = bundle.getDouble("curr_longitude");
            curr_latitude = bundle.getDouble("curr_latitude");
            curr_db_name = bundle.getString("curr_db_name");
            curr_place_id = bundle.getString("curr_place_id");
            curr_rate_txt = bundle.getString("curr_rate_place", "1");

            curr_place_name.setText(curr_place_name_txt);
            header_sub_places.setText("Places to visit in " + curr_place_name_txt);
            curr_rate.setText(curr_rate_txt);
            curr_des.setText(bundle.getString("curr_description", "Place Description"));
            curr_attr.setText(bundle.getString("curr_attractions", "Place Attractions"));
            curr_climate.setText(bundle.getString("curr_climate", "Average climate of the place"));
            curr_how_reach.setText(bundle.getString("curr_reach_method", "Place Name"));
            curr_besttime.setText(bundle.getString("curr_besttime", "Place Name"));
            curr_array_images = bundle.getStringArrayList("curr_array_images");

            if (recentPlaceDBHelper.getRecentcolname().size() > 0) {
                for (int i = 0; i < recentPlaceDBHelper.getRecentcolname().size(); i++) {

                    String local_placename = recentPlaceDBHelper.getRecentPlaceName().get(i);
                    if (local_placename.equals(curr_place_name_txt)) {
                        if (recentPlaceDBHelper.deleteRecentPlace(curr_db_name, curr_place_id, curr_place_name_txt, curr_array_images.get(0))) {
                            if (recentPlaceDBHelper.addRecentPlace(curr_db_name, curr_place_id, null, curr_place_name_txt, curr_array_images.get(0))) {
                                Log.e("recent_place_db", "entered");
                            } else {
                                Log.e("recent_place_db", "failed!!!");
                            }
                            break;
                        }
                    } else {
                        if (i == recentPlaceDBHelper.getRecentcolname().size() - 1) {
                            if (recentPlaceDBHelper.addRecentPlace(curr_db_name, curr_place_id, null, curr_place_name_txt, curr_array_images.get(0))) {
                                Log.e("recent_place_db", "entered");
                            } else {
                                Log.e("recent_place_db", "failed!!!");
                            }
                            break;
                        }
                    }
                }
            } else {
                if (recentPlaceDBHelper.addRecentPlace(curr_db_name, curr_place_id, null, curr_place_name_txt, curr_array_images.get(0))) {
                    Log.e("recent_place_db", "entered");
                } else {
                    Log.e("recent_place_db", "failed!!!");
                }
            }
        }

        if (firebaseUser.isAnonymous()) {
            mplace_addcomment_lay.setVisibility(View.GONE);
            mplace_addcomment_guest_acc_msg.setVisibility(View.VISIBLE);
        } else {
            mplace_addcomment_lay.setVisibility(View.VISIBLE);
            mplace_addcomment_guest_acc_msg.setVisibility(View.GONE);
            mplace_add_comment_box.addTextChangedListener(mplace_add_comment);
        }
        mainPlaceGetAllComments();
        mplace_hotel_rec_fun();

        mplace_addcomment_btn.setOnClickListener(v -> {

            SharedPreferences get_user_data_prefs = getSharedPreferences("user_data", Activity.MODE_PRIVATE);
            String comment_username = get_user_data_prefs.getString("fullname", "User");

            String add_comment_txt = mplace_add_comment_box.getText().toString();

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM y, hh:mm aa");
            String formattedDate = dateFormat.format(new Date());

            Map<Object, String> my_comment_map = new HashMap<>();
            my_comment_map.put("mplace_comment_user", comment_username);
            my_comment_map.put("mplace_comment_time", formattedDate);
            my_comment_map.put("mplace_comment_txt", add_comment_txt);

            ArrayList<Map<Object, String>> comment_array = new ArrayList<>();
            if (mplace_comments != null && mplace_comments.size() > 0) {
                comment_array.addAll(mplace_comments);
                comment_array.add(0, my_comment_map);
            } else {
                comment_array.add(0, my_comment_map);
            }

            db.collection(curr_db_name)
                    .document(curr_place_id)
                    .update("mplace_comments", comment_array)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Snackbar.make(mplace_main_lay, "Comment Added", Snackbar.LENGTH_SHORT).show();
                                mplace_add_comment_box.setText("");
                                mainPlaceGetAllComments();
                            }
                        }
                    });
        });

        for (int i = 0; i < curr_array_images.size(); i++) {
            curr_placeimageearray.add(new ImageSliderModel(curr_array_images.get(i)));
        }

        imageSlideAdapter = new ImageSlideAdapter(getApplicationContext(), 1);
        imageSlideAdapter.renewItems(curr_placeimageearray);
        mplace_imageSlider.startAutoCycle();
        mplace_imageSlider.setSliderAdapter(imageSlideAdapter);
        mplace_imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
        mplace_imageSlider.setAutoCycle(true);

        curr_location.getMapAsync(this);

        db.collection(curr_db_name)
                .document(curr_place_id)
                .collection("inner_places")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.e("inner_category_data", "success");

                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            innerCategoryModel = new InnerCategoryModel(
                                    documentSnapshot.getString("inner_place_id"),
                                    documentSnapshot.getString("inner_place_name"),
                                    documentSnapshot.getString("inner_rating"),
                                    documentSnapshot.getString("inner_description"),
                                    curr_place_id,
                                    curr_db_name,
                                    (Double) documentSnapshot.get("longitude"),
                                    (Double) documentSnapshot.get("latitude"),
                                    (ArrayList<String>) documentSnapshot.get("inner_images"),
                                    (ArrayList<String>) documentSnapshot.get("inner_features")
                            );

                            inner_cat_array.add(innerCategoryModel);

                            curr_inner_places_rec.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
                            innerCatgegoryItemAdapter = new InnerCatgegoryItemAdapter(getApplicationContext(), inner_cat_array);
                            curr_inner_places_rec.setAdapter(innerCatgegoryItemAdapter);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("inner_category_data", "failed");
                    }
                });

        db.collection(user_col_name)
                .document(firebaseUser.getUid())
                .collection("favourites")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {

                                String fdb_db_name = documentSnapshot.getString("fav_db_name");
                                String fmain_place_id = documentSnapshot.getString("fav_main_place_id");
                                String finner_place_id = documentSnapshot.getString("fav_inner_place_id");

                                if (fdb_db_name != null && fmain_place_id != null && finner_place_id == null) {
                                    if (fdb_db_name.equals(curr_db_name) && fmain_place_id.equals(curr_place_id)) {
                                        curr_fav.setImageTintList(ColorStateList.valueOf(Color.RED));
                                        break;
                                    } else {
                                        curr_fav.setImageTintList(ColorStateList.valueOf(Color.GRAY));
                                    }
                                } else {
                                    curr_fav.setImageTintList(ColorStateList.valueOf(Color.GRAY));
                                }
                            }
                        }
                    }
                });

        curr_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection(user_col_name)
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
                                                if (fdb_db_name.equals(curr_db_name) && fmain_place_id.equals(curr_place_id)) {

                                                    db.collection("users")
                                                            .document(firebaseUser.getUid())
                                                            .collection("favourites")
                                                            .document(curr_place_id)
                                                            .delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Snackbar.make(mplace_main_lay, "Removed from favourites", Snackbar.LENGTH_SHORT).show();
                                                                    curr_fav.setImageTintList(ColorStateList.valueOf(Color.GRAY));
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    curr_fav.setImageTintList(ColorStateList.valueOf(Color.RED));
                                                                }
                                                            });
                                                    break;
                                                } else {
                                                    if (count == task.getResult().size()) {

                                                        addToFav(user_col_name, curr_db_name, curr_place_id, curr_place_name_txt, null, firebaseUser.getUid(), curr_fav);
                                                    }
                                                }
                                            } else {
                                                if (count == task.getResult().size()) {
                                                    addToFav(user_col_name, curr_db_name, curr_place_id, curr_place_name_txt, null, firebaseUser.getUid(), curr_fav);
                                                }
                                            }
                                            count++;
                                        }
                                    } else {
                                        addToFav(user_col_name, curr_db_name, curr_place_id, curr_place_name_txt, null, firebaseUser.getUid(), curr_fav);
                                    }
                                }
                            }
                        });
            }
        });

        mplace_more_btn.setOnClickListener(v -> {
            Intent intent1 = new Intent(getApplicationContext(), ShowCommentActivity.class);
            intent1.putExtra("all_comments_array", mplace_comments);
            intent1.putExtra("rate", Float.valueOf(curr_rate_txt));
            intent1.putExtra("page_pos", 1);
            startActivity(intent1);
        });

        mplace_nest_scroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY) {
                curr_fav.hide();
            } else {
                curr_fav.show();
            }
        });
    }

    private void mplace_hotel_rec_fun() {

        ArrayList<HotelDetailModel> hotel_details_array = new ArrayList<>();
        db.collection(curr_db_name)
                .document(curr_place_id)
                .collection("hotels_section")
                .limit(6)
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
                                            curr_db_name,
                                            curr_place_id,
                                            documentSnapshot.getString("inner_place_id"),
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

                                mplace_hotels_rec.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
                                HotelsAdpater hotelsAdpater = new HotelsAdpater(getApplicationContext(), hotel_details_array);
                                mplace_hotels_rec.setAdapter(hotelsAdpater);
                                hotelsAdpater.notifyDataSetChanged();
                            } else {
                                Log.e("Inner_Hotel_data", "Null data");
                            }
                        } else {
                            Log.e("Inner_Hotel_data", "Error Followed !");
                        }
                    }
                });
    }

    private void mainPlaceGetAllComments() {
        db.collection(curr_db_name)
                .document(curr_place_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    @SuppressWarnings("unchecked")
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                mplace_comments = (ArrayList<Map<Object, String>>) task.getResult().get("mplace_comments");

                                if (mplace_comments != null && !mplace_comments.isEmpty()) {
                                    mplace_comment_lay.setVisibility(View.VISIBLE);
                                    mplace_first_comment_lay.setVisibility(View.VISIBLE);
                                    mplace_comment_user1.setText(mplace_comments.get(0).get("mplace_comment_user"));
                                    mplace_comment_time1.setText(mplace_comments.get(0).get("mplace_comment_time"));
                                    mplace_comment_text1.setText(mplace_comments.get(0).get("mplace_comment_txt"));

                                    if (mplace_comments.size() > 1) {
                                        mplace_more_btn.setVisibility(View.VISIBLE);
                                        mplace_more_btn.setText("Show all " + mplace_comments.size() + " reviews");
                                    } else {
                                        mplace_more_btn.setVisibility(View.GONE);
                                    }
                                } else {
                                    mplace_comment_lay.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            Log.e("comments", "error");
                        }
                    }
                });
    }

    private void addToFav(String user_col_name, String database, String main, String name, Object inner, String uid, ImageButton fav_btn) {

        Map<String, Object> map = new HashMap<>();
        map.put("fav_db_name", database);
        map.put("fav_main_place_id", main);
        map.put("fav_inner_place_id", inner);
        map.put("fav_place_name", name);

        db.collection(user_col_name)
                .document(uid)
                .collection("favourites")
                .document(main)
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(mplace_main_lay, "Added to favourites", Snackbar.LENGTH_SHORT).show();
                            fav_btn.setImageTintList(ColorStateList.valueOf(Color.RED));
                        } else {
                            fav_btn.setImageTintList(ColorStateList.valueOf(Color.GRAY));
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mplace_imageSlider.startAutoCycle();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mplace_imageSlider.stopAutoCycle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mplace_imageSlider.stopAutoCycle();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapAPI = googleMap;
        LatLng mappoints = new LatLng(curr_longitude, curr_latitude);
        mapAPI.addMarker(new MarkerOptions().position(mappoints).title(curr_place_name_txt));
        mapAPI.moveCamera(CameraUpdateFactory.newLatLngZoom(mappoints, 10F));
    }

    public class InnerCatgegoryItemAdapter extends RecyclerView.Adapter<InnerCatgegoryItemAdapter.ListViewHolder> {

        private final Context context;
        private ArrayList<InnerCategoryModel> inner_cat_array = new ArrayList<>();

        public InnerCatgegoryItemAdapter(Context context, ArrayList<InnerCategoryModel> inner_cat_array) {
            this.context = context;
            this.inner_cat_array = inner_cat_array;
        }

        @NonNull
        @Override
        public InnerCatgegoryItemAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.hinnertripitems, null);
            return new InnerCatgegoryItemAdapter.ListViewHolder(inflate);
        }

        @Override
        public void onBindViewHolder(@NonNull InnerCatgegoryItemAdapter.ListViewHolder holder, int position) {
            holder.curr_inner_place_title.setText(inner_cat_array.get(position).getInner_place_name());
            Glide.with(holder.itemView)
                    .load(inner_cat_array.get(position).getInner_images().get(0))
                    .fitCenter()
                    .into(holder.curr_inner_place_img);


            holder.inner_place_lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putString("inner_place_id", inner_cat_array.get(position).getInner_place_id());
                    bundle.putString("inner_place_name", inner_cat_array.get(position).getInner_place_name());
                    bundle.putString("inner_rating", inner_cat_array.get(position).getInner_rating());
                    bundle.putString("inner_description", inner_cat_array.get(position).getInner_description());
                    bundle.putString("db_name", inner_cat_array.get(position).getDb_name());
                    bundle.putString("main_place_id", inner_cat_array.get(position).getMain_place_id());
                    bundle.putDouble("inner_longitude", inner_cat_array.get(position).getLongitude());
                    bundle.putDouble("inner_latitude", inner_cat_array.get(position).getLatitude());
                    bundle.putStringArrayList("inner_images", inner_cat_array.get(position).getInner_images());
                    bundle.putStringArrayList("inner_features", inner_cat_array.get(position).getInner_features());

                    Intent intent = new Intent(context, InnerCategoryActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("curr_innerdetails_bundle", bundle);
                    context.startActivity(intent);

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
            return inner_cat_array.size();
        }

        public class ListViewHolder extends RecyclerView.ViewHolder {

            TextView curr_inner_place_title;
            ImageView curr_inner_place_img;
            RelativeLayout inner_place_lay;

            public ListViewHolder(@NonNull View itemView) {
                super(itemView);

                curr_inner_place_title = itemView.findViewById(R.id.inner_trip_title);
                curr_inner_place_img = itemView.findViewById(R.id.inner_trip_img);
                inner_place_lay = itemView.findViewById(R.id.inner_trip_lay);
            }
        }
    }
}