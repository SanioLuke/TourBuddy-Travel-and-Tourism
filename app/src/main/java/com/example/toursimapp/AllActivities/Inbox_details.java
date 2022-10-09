package com.example.toursimapp.AllActivities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.Models.SummaryOrderModel;
import com.example.toursimapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Inbox_details extends AppCompatActivity {

    ImageButton notify_details_back_btn;
    TextView notify_details_content_txt;
    Button notify_details_summary_page;
    String content_txt, order_id;
    boolean inbox_details_checked;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    ImageView notify_details_imagegif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_details);

        new Functions().lightstatusbardesign(Inbox_details.this);
        notify_details_back_btn = findViewById(R.id.notify_details_back_btn);
        notify_details_content_txt = findViewById(R.id.notify_details_content_txt);
        notify_details_summary_page = findViewById(R.id.notify_details_summary_page);
        notify_details_imagegif = findViewById(R.id.notify_details_imagegif);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        Glide.with(getApplicationContext()).load(R.raw.inbox_anim_gif).into(notify_details_imagegif);

        Intent intent = getIntent();
        content_txt = intent.getStringExtra("inbox_details_content");
        order_id = intent.getStringExtra("inbox_details_orderid");
        inbox_details_checked = intent.getBooleanExtra("inbox_details_checked", false);

        notify_details_content_txt.setText(content_txt);
        notify_details_back_btn.setOnClickListener(v -> {
            if (inbox_details_checked) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else {
                finish();
            }
        });

        if (inbox_details_checked) {
            db.collection("users")
                    .document(firebaseUser.getUid())
                    .collection("user_notification")
                    .document(order_id)
                    .update("seen", true);
        }

        notify_details_summary_page.setOnClickListener(v -> {

            if (order_id != null) {
                db.collection("users")
                        .document(firebaseUser.getUid())
                        .collection("Orders")
                        .document(order_id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            @SuppressWarnings("all")
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {

                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot != null) {
                                        SummaryOrderModel summaryOrderModel = new SummaryOrderModel(
                                                documentSnapshot.getString("dbname"),
                                                documentSnapshot.getString("main_place_id"),
                                                documentSnapshot.getString("inner_place_id"),
                                                documentSnapshot.getString("order_id"),
                                                documentSnapshot.getString("hotel_id"),
                                                documentSnapshot.getString("hotel_name"),
                                                documentSnapshot.getString("place_name"),
                                                documentSnapshot.getString("hotel_img"),
                                                (long) documentSnapshot.get("main_price"),
                                                (long) documentSnapshot.get("tax_gst"),
                                                (long) documentSnapshot.get("service_fee"),
                                                (long) documentSnapshot.get("total_final_price"),
                                                (long) documentSnapshot.get("no_days"),
                                                (long) documentSnapshot.get("total_no_guests"),
                                                (long) documentSnapshot.get("no_rooms"),
                                                documentSnapshot.getString("hotel_contact_no"),
                                                documentSnapshot.getString("hotel_website"),
                                                documentSnapshot.getString("hotel_emailID"),
                                                documentSnapshot.getDate("startDate"),
                                                documentSnapshot.getDate("endDate"),
                                                documentSnapshot.getString("s_am_pm"),
                                                documentSnapshot.getString("e_am_pm"),
                                                documentSnapshot.getDate("bookedDate"),
                                                documentSnapshot.getBoolean("order_summary_status"),
                                                documentSnapshot.getString("reference_id"),
                                                documentSnapshot.getString("error_msg"));

                                        Intent intent = new Intent(getApplicationContext(), OrdersDisplayActivity.class);
                                        intent.putExtra("booked_summary_data", summaryOrderModel);
                                        startActivity(intent);
                                    }
                                } else {
                                    Log.e("inbox_details", "Inbox Details Failed!!!");
                                }
                            }
                        });
            } else {
                Snackbar.make(v, "Server Error. Please exit the page and try again!!!!", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (inbox_details_checked) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }
}