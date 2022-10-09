package com.example.toursimapp.AllActivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toursimapp.Adapters.HotelsAdpater;
import com.example.toursimapp.Adapters.ImageSlideAdapter;
import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.Models.HotelDetailModel;
import com.example.toursimapp.Models.ImageSliderModel;
import com.example.toursimapp.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderView;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.N)
public class HotelActivity extends AppCompatActivity implements Serializable, OnMapReadyCallback {

    private final ArrayList<ImageSliderModel> hotel_image_array = new ArrayList<>();
    private final ArrayList<HotelDetailModel> other_hotels_array = new ArrayList<>();
    private final DecimalFormat formatter = new DecimalFormat("#,##,###");
    private HotelDetailModel one_hotel_details;
    private SliderView hotel_imageSlider;
    private TextView hotel_name, hotel_place_name, hotel_addcomment_guest_acc_msg, hotel_rate, hotel_des, hotel_des_expend, hotel_features, hotel_one_room_price, hotel_piechart_title, hotel_default_book_date;
    private RatingBar hotel_rate_bar;
    private PieChart hotel_pie_chart;
    private Double hotel_longitude, hotel_latitude;
    private SupportMapFragment hotel_location;
    private View hotel_book_guest_lay, hotel_send_comment_lay;
    private ImageSlideAdapter imageSlideAdapter;
    private ArrayList<String> hotel_imgs = new ArrayList<>();
    private Button hotel_booking_btn, hotel_signup_btn;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private int readcheck = 0;
    private RecyclerView hotel_ohotels_rec;
    private HotelsAdpater hotelsAdpater;
    private float getThisHotelSize, getAllOrdersSize;
    private View hotel_comment_lay;
    private TextView hotel_comment_user1, hotel_comment_time1, hotel_comment_text1;
    private Button hotel_more_btn;
    private ArrayList<Map<Object, String>> hotel_comments = new ArrayList<>();
    private EditText hotel_addcomment_txt;
    private ImageButton hotel_send_btn;
    private final TextWatcher hotel_sendcomment_txtWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String mplace_addcomment_txt = hotel_addcomment_txt.getText().toString();
            hotel_send_btn.setEnabled(!mplace_addcomment_txt.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hotel);
        new Functions().checkTheme(getApplicationContext());

        hotelInits();
        if (one_hotel_details != null) {
            hotel_name.setText(one_hotel_details.getHotel_name());
            hotel_place_name.setText(one_hotel_details.getPlace_name());
            hotel_piechart_title.setText("Popularity of " + one_hotel_details.getHotel_name());

            hotel_rate.setText(one_hotel_details.getHotel_rate());
            hotel_rate_bar.setRating(Float.parseFloat(one_hotel_details.getHotel_rate()));

            hotel_default_book_date.setText(one_hotel_details.getPeople_accomodation_no() + " people / room");
            hotel_des.setText(one_hotel_details.getHotel_des());
            setHotelFeatures(one_hotel_details.getHotel_fac());
            hotel_one_room_price.setText(String.format("₹ %s", formatter.format(one_hotel_details.getInitial_price())));

            hotel_longitude = one_hotel_details.getHotel_longitude();
            hotel_latitude = one_hotel_details.getHotel_latitude();
            hotel_location.getMapAsync(this);

            hotel_imgs = one_hotel_details.getHotel_img();
        }
        hotelPieChartFun();

        hotelGetAllComments();

        for (int i = 0; i < hotel_imgs.size(); i++) {
            hotel_image_array.add(new ImageSliderModel(hotel_imgs.get(i)));
        }
        imageSlideAdapter = new ImageSlideAdapter(getApplicationContext(), 1);
        imageSlideAdapter.renewItems(hotel_image_array);
        hotel_imageSlider.startAutoCycle();
        hotel_imageSlider.setSliderAdapter(imageSlideAdapter);
        hotel_imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
        hotel_imageSlider.setAutoCycle(true);

        otherHotelsRecyclerFun();

        hotel_des_expend.setOnClickListener(v -> {
            if (readcheck == 0) {
                hotel_des.setEllipsize(null);
                hotel_des.setMaxLines(Integer.MAX_VALUE);
                hotel_des_expend.setText(R.string.read_less);
                readcheck = 1;
            } else {
                hotel_des.setEllipsize(TextUtils.TruncateAt.END);
                hotel_des.setMaxLines(3);
                hotel_des_expend.setText(R.string.read_more);
                readcheck = 0;
            }
        });

        hotel_booking_btn.setOnClickListener(v -> {
            db.collection("users")
                    .document(firebaseUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult() != null) {

                                    String contact_no = task.getResult().getString("contact_number");
                                    String emailID = task.getResult().getString("emailid");
                                    if (contact_no == null || emailID == null) {
                                        final Dialog dialog = new Dialog(HotelActivity.this);
                                        dialog.setContentView(R.layout.msg_dialog);
                                        dialog.setCanceledOnTouchOutside(false);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
                                        TextView msg_content = dialog.findViewById(R.id.msg_content);
                                        TextView msg_ok_btn = dialog.findViewById(R.id.msg_ok_btn);

                                        msg_content.setText("You haven't entered you user details in your account !!\nPlease fill up the User Details like Contact Number and Email ID if you haven't entered.");
                                        msg_ok_btn.setOnClickListener(v -> {
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    dialog.dismiss();
                                                }
                                            }, 500);
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));
                                                }
                                            }, 1000);
                                        });
                                        dialog.show();

                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), OrdersActivity.class);
                                        intent.putExtra("par_hotel_summary_bundle", one_hotel_details);
                                        startActivity(intent);
                                    }
                                }
                            } else {
                                Toast.makeText(HotelActivity.this, "Error Occured : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        });

        hotel_signup_btn.setOnClickListener(v -> {
            SharedPreferences.Editor editor = getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
            editor.putBoolean("isUser", true);
            editor.apply();
            startActivity(new Intent(getApplicationContext(), Logsignactivity.class));
        });

        hotel_send_btn.setOnClickListener(v -> {

            SharedPreferences get_user_data_prefs = getSharedPreferences("user_data", Activity.MODE_PRIVATE);
            String comment_username = get_user_data_prefs.getString("fullname", "User");

            String add_comment_txt = hotel_addcomment_txt.getText().toString();

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM y, hh:mm aa");
            String formattedDate = dateFormat.format(new Date());

            Map<Object, String> my_comment_map = new HashMap<>();
            my_comment_map.put("hotels_comment_user", comment_username);
            my_comment_map.put("hotels_comment_time", formattedDate);
            my_comment_map.put("hotels_comment_txt", add_comment_txt);

            ArrayList<Map<Object, String>> comment_array = new ArrayList<>();
            if (hotel_comments != null && hotel_comments.size() > 0) {
                comment_array.addAll(hotel_comments);
                comment_array.add(0, my_comment_map);
            } else {
                comment_array.add(0, my_comment_map);
            }

            db.collection(one_hotel_details.getDb_name())
                    .document(one_hotel_details.getMain_place_id())
                    .collection("hotels_section")
                    .document(one_hotel_details.getHotel_id())
                    .update("hotels_comments", comment_array)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(HotelActivity.this, "Added Comment :)", Toast.LENGTH_SHORT).show();
                                hotel_addcomment_txt.setText("");
                                hotelGetAllComments();
                            }
                        }
                    });
        });

        hotel_more_btn.setOnClickListener(v -> {
            Intent intent1 = new Intent(getApplicationContext(), ShowCommentActivity.class);
            intent1.putExtra("all_comments_array", hotel_comments);
            intent1.putExtra("rate", Float.valueOf(one_hotel_details.getHotel_rate()));
            intent1.putExtra("page_pos", 3);
            startActivity(intent1);
        });
    }

    private void hotelGetAllComments() {
        db.collection(one_hotel_details.getDb_name())
                .document(one_hotel_details.getMain_place_id())
                .collection("hotels_section")
                .document(one_hotel_details.getHotel_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    @SuppressWarnings("unchecked")
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                hotel_comments = (ArrayList<Map<Object, String>>) task.getResult().get("hotels_comments");

                                if (hotel_comments != null && !hotel_comments.isEmpty()) {
                                    hotel_comment_lay.setVisibility(View.VISIBLE);
                                    hotel_comment_user1.setText(hotel_comments.get(0).get("hotels_comment_user"));
                                    hotel_comment_time1.setText(hotel_comments.get(0).get("hotels_comment_time"));
                                    hotel_comment_text1.setText(hotel_comments.get(0).get("hotels_comment_txt"));

                                    if (hotel_comments.size() > 1) {
                                        hotel_more_btn.setVisibility(View.VISIBLE);
                                        hotel_more_btn.setText("Show all " + hotel_comments.size() + " reviews");
                                    } else {
                                        hotel_more_btn.setVisibility(View.GONE);
                                    }
                                } else {
                                    hotel_comment_lay.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            Log.e("comments", "error");
                        }
                    }
                });
    }

    private void hotelPieChartFun() {

        if (getAllOrdersSize == 0) {
            ArrayList<PieEntry> booked_data = new ArrayList<>();
            booked_data.add(new PieEntry(1, "No Bookings Yet"));
            booked_data.add(new PieEntry(getAllOrdersSize, "Total Booked Hotels"));
            PieDataSet pieDataSet = new PieDataSet(booked_data, " ");
            pieDataSet.setColors(new int[]{R.color.light_grey, R.color.my_orange}, getApplicationContext());
            pieDataSet.setValueTextColor(android.R.color.darker_gray);
            pieDataSet.setValueTextSize(16f);
            PieData pieData = new PieData(pieDataSet);
            hotel_pie_chart.setData(pieData);
        } else {
            ArrayList<PieEntry> booked_data = new ArrayList<>();
            booked_data.add(new PieEntry(getThisHotelSize, one_hotel_details.getHotel_name()));
            booked_data.add(new PieEntry(getAllOrdersSize, "Total Booked Hotels"));
            PieDataSet pieDataSet = new PieDataSet(booked_data, " ");
            pieDataSet.setColors(new int[]{R.color.light_color, R.color.my_orange}, getApplicationContext());
            pieDataSet.setValueTextColor(android.R.color.darker_gray);
            pieDataSet.setValueTextSize(16f);
            PieData pieData = new PieData(pieDataSet);
            hotel_pie_chart.setData(pieData);
        }

        hotel_pie_chart.setEntryLabelColor(android.R.color.darker_gray);
        hotel_pie_chart.getDescription().setEnabled(false);
        hotel_pie_chart.setCenterText("Popularity Ratio");
        hotel_pie_chart.animate();

    }

    @SuppressWarnings("all")
    private void otherHotelsRecyclerFun() {
        db.collection(one_hotel_details.getDb_name())
                .document(one_hotel_details.getMain_place_id())
                .collection("hotels_section")
                .whereEqualTo("inner_place_id", one_hotel_details.getInner_place_id())
                .whereNotEqualTo("hotel_id", one_hotel_details.getHotel_id())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    Log.e("Other_Hotel_data", "Success :)");

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        if (documentSnapshot != null) {
                            HotelDetailModel hotelDetailModel = new HotelDetailModel(
                                    one_hotel_details.getDb_name(),
                                    one_hotel_details.getMain_place_id(),
                                    one_hotel_details.getInner_place_id(),
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
                            other_hotels_array.add(hotelDetailModel);
                        }
                    }

                    hotel_ohotels_rec.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
                    hotelsAdpater = new HotelsAdpater(getApplicationContext(), other_hotels_array);
                    hotelsAdpater.notifyDataSetChanged();
                    hotel_ohotels_rec.setAdapter(hotelsAdpater);
                })
                .addOnFailureListener(e -> Log.e("Other_Hotel_data", "Error followed : " + e.getMessage()));
    }

    private void hotelInits() {

        Intent intent = getIntent();
        one_hotel_details = (HotelDetailModel) intent.getSerializableExtra("par_hotel_details_bundle");
        getAllOrdersSize = intent.getFloatExtra("allOrders", 1);
        getThisHotelSize = intent.getFloatExtra("thisHotelOrders", 1);

        hotel_addcomment_txt = findViewById(R.id.hotel_addcomment_txt);
        hotel_send_btn = findViewById(R.id.hotel_send_btn);
        hotel_piechart_title = findViewById(R.id.hotel_piechart_title);
        hotel_pie_chart = findViewById(R.id.hotel_pie_chart);
        hotel_imageSlider = findViewById(R.id.hotel_imageSlider);
        hotel_name = findViewById(R.id.hotel_name);
        hotel_place_name = findViewById(R.id.hotel_place_name);
        hotel_rate = findViewById(R.id.hotel_rate);
        hotel_book_guest_lay = findViewById(R.id.hotel_book_guest_lay);
        hotel_send_comment_lay = findViewById(R.id.hotel_send_comment_lay);
        hotel_des = findViewById(R.id.hotel_des);
        hotel_addcomment_guest_acc_msg = findViewById(R.id.hotel_addcomment_guest_acc_msg);
        hotel_des_expend = findViewById(R.id.hotel_des_expend);
        hotel_features = findViewById(R.id.hotel_features);
        hotel_one_room_price = findViewById(R.id.hotel_one_room_price);
        hotel_rate_bar = findViewById(R.id.hotel_rate_bar);
        hotel_booking_btn = findViewById(R.id.hotel_booking_btn);
        hotel_signup_btn = findViewById(R.id.hotel_signup_btn);
        hotel_default_book_date = findViewById(R.id.hotel_default_book_date);
        hotel_location = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.hotel_location);

        hotel_comment_lay = findViewById(R.id.hotel_comment_lay);
        hotel_comment_user1 = findViewById(R.id.hotel_comment_user1);
        hotel_comment_time1 = findViewById(R.id.hotel_comment_time1);
        hotel_comment_text1 = findViewById(R.id.hotel_comment_text1);
        hotel_more_btn = findViewById(R.id.hotel_more_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        hotel_ohotels_rec = findViewById(R.id.hotel_ohotels_rec);
        db = FirebaseFirestore.getInstance();
        hotel_send_btn.setEnabled(false);

        if (firebaseUser.isAnonymous()) {
            hotel_book_guest_lay.setVisibility(View.VISIBLE);
            hotel_send_comment_lay.setVisibility(View.GONE);
            hotel_booking_btn.setVisibility(View.GONE);
            hotel_addcomment_guest_acc_msg.setVisibility(View.VISIBLE);
        } else {
            hotel_book_guest_lay.setVisibility(View.GONE);
            hotel_send_comment_lay.setVisibility(View.VISIBLE);
            hotel_booking_btn.setVisibility(View.VISIBLE);
            hotel_addcomment_guest_acc_msg.setVisibility(View.GONE);
            hotel_addcomment_txt.addTextChangedListener(hotel_sendcomment_txtWatcher);
        }

        Animation bounce_hotel_get_registered_lay = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce_anim);
        hotel_addcomment_guest_acc_msg.startAnimation(bounce_hotel_get_registered_lay);
    }

    private void setHotelFeatures(@NotNull ArrayList<String> hotel_feature) {
        StringBuilder all_fea_string = new StringBuilder();
        for (int i = 0; i < hotel_feature.size(); i++) {
            all_fea_string.append("\u2605" + "  ").append(hotel_feature.get(i)).append("\n\n");
        }
        hotel_features.setText(all_fea_string);
    }

    @Override
    public void onMapReady(@NotNull GoogleMap googleMap) {
        LatLng mappoints = new LatLng(hotel_longitude, hotel_latitude);
        googleMap.addMarker(new MarkerOptions().position(mappoints).title(one_hotel_details.getHotel_name()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mappoints, 16F));
    }

}