package com.example.toursimapp.AllActivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.util.Pair;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.Models.HotelDetailModel;
import com.example.toursimapp.Models.SummaryOrderModel;
import com.example.toursimapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.jetbrains.annotations.Contract;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

public class OrdersActivity extends AppCompatActivity implements PaymentResultListener {

    private ImageButton order_back_btn;
    private TextView order_review_hotel_name, order_review_place_name, order_review_room_cap, order_review_hotel_rate;
    private TextView order_guest_details_txt, order_dates_txt, order_check_in_txt, order_check_out_txt;
    private TextView order_main_price_header, order_main_price, order_service_price, order_tax_price, order_confirm_terms_txt, order_confirm_btn;
    private TextView order_guest_details_edit_btn, order_dates_edit_btn, order_check_in_edit_btn, initial_order_tprice, initial_order_tprice_days_count, order_final_tprice;
    private ImageView order_himg;
    private View orders_container, order_guest_details_lay, order_dates_lay, order_check_in_lay;

    private View order_check_out_lay;
    private CheckBox order_check_agree_book;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private Calendar calendar;

    private HotelDetailModel summaryData;
    private String emailID, contact_no;
    private int no_rooms = 1;
    private int no_days = 1;
    private int people_accomodate_no;
    private int total_no_guests;
    private int main_adult_count = 1;
    private int main_children_count = 0;
    private int main_guest_added = 1;
    private String s_am_pm = null;
    private String e_am_pm = null;
    private int updated_hour = 0;
    private Date startDate, endDate;
    private int main_price, service_fee, tax_gst, total_final_price;

    private String hotel_summary_order_id;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Functions().checkTheme(getApplicationContext());
        new Functions().lightstatusbardesign(OrdersActivity.this);
        setContentView(R.layout.activity_orders);

        orderInits();
        getHotelSummaryData();
        updatePrice();
        ordersDefaultDetails();
        orderConfirmTextFun();
        orderEventListnerFun();

    }

    @SuppressLint("SetTextI18n")
    private void getHotelSummaryData() {
        Intent intent = getIntent();
        summaryData = (HotelDetailModel) intent.getSerializableExtra("par_hotel_summary_bundle");
        hotel_summary_order_id = intent.getStringExtra("hotel_summary_order_id");

        if (summaryData != null) {

            main_price = (int) summaryData.getInitial_price();
            service_fee = (int) summaryData.getService_fee();
            tax_gst = (int) summaryData.getTax_gst_fee();
            total_no_guests = (int) summaryData.getPeople_no_booking();
            people_accomodate_no = (int) summaryData.getPeople_accomodation_no();
            total_final_price = main_price;

            Glide.with(getApplicationContext()).load(summaryData.getHotel_img().get(0)).into(order_himg);
            order_review_hotel_name.setText(summaryData.getHotel_name());
            order_review_place_name.setText(summaryData.getPlace_name());
            order_review_room_cap.setText(summaryData.getPeople_accomodation_no() + " persons / room");
            order_review_hotel_rate.setText(summaryData.getHotel_rate());

            if (summaryData.getInitial_price() <= 1) {
                order_main_price_header.setText("₹ " + summaryData.getInitial_price() + " x " + no_rooms + " room");
            } else {
                order_main_price_header.setText("₹ " + summaryData.getInitial_price() + " x " + no_rooms + " rooms");
            }

            order_main_price.setText("₹ " + summaryData.getInitial_price());
            order_service_price.setText("₹ " + summaryData.getService_fee());
            order_tax_price.setText("₹ " + summaryData.getTax_gst_fee());

        }
    }

    @SuppressLint("SetTextI18n")
    private void ordersDefaultDetails() {

        String order_guest_details = order_guest_details_txt.getText().toString();

        if (order_guest_details.equals("Add number of guests") || order_guest_details.equals("") || order_guest_details == null) {
            main_guest_added = 1;
            order_guest_details_txt.setText(main_guest_added + " guest");
        }

        {
            startDate = new Functions().modifiedDate(startDate, 2);
            endDate = new Functions().modifiedDate(endDate, 3);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                no_days = 1;
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yy");
                String defaultStartDatetxt = simpleDateFormat.format(startDate);
                String defaultEndDatetxt = simpleDateFormat.format(endDate);
                order_dates_txt.setText("(" + defaultStartDatetxt + ")" + " - (" + defaultEndDatetxt + ")");
                Log.e("getAllTime", "Date and Time is :" + startDate);
            }
        } //Dates Setup

        Log.e("getAllTime", "Time and Date is : " + startDate);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    private void orderEventListnerFun() {

        order_back_btn.setOnClickListener(v -> finish());

        order_confirm_btn.setOnClickListener(v -> {

            db.collection("users")
                    .document(firebaseUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult() != null) {
                                emailID = task.getResult().getString("emailid");
                                contact_no = task.getResult().getString("contact_number");
                            } else {
                                Log.e("TAG", "Data Get Error i.e. is null");
                            }
                        }
                    });

            String order_guest_details = order_guest_details_txt.getText().toString();
            String order_dates = order_dates_txt.getText().toString();
            String order_check_in = order_check_in_txt.getText().toString();
            boolean ischecked = order_check_agree_book.isChecked();

            if (order_check_in.equals("Add the check time") || order_check_in.equals("")) {
                new Functions().Orders_Dialog_Box(OrdersActivity.this, "Please Add your desired Check-In Time.");
            } else if (!ischecked) {
                new Functions().Orders_Dialog_Box(OrdersActivity.this, "And Please agree to the terms and conditions");
            } else {

                Checkout gway_checkout = new Checkout();
                gway_checkout.setKeyID("rzp_test_UlnYjiO6JaQc90");
                gway_checkout.setImage(R.drawable.razorpay_icon);
                JSONObject payment_details = new JSONObject();
                try {
                    double amount = total_final_price;
                    amount = amount * 100;
                    payment_details.put("name", "TourBudddy");
                    payment_details.put("description", "This is a Test Payment Session. There will be not money deduction. We will be modifying this testing session into a Real Payment Session in the future updation. \nThank You !");
                    payment_details.put("theme.color", "#4990FF");
                    payment_details.put("currency", "INR");
                    payment_details.put("amount", amount);

                    JSONObject prefill = new JSONObject();
                    prefill.put("email", emailID);
                    prefill.put("contact", contact_no);

                    payment_details.put("prefill", prefill);
                    gway_checkout.open(OrdersActivity.this, payment_details);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        order_guest_details_edit_btn.setOnClickListener(v -> orderGuestDetailsFun());

        order_guest_details_lay.setOnClickListener(v -> orderGuestDetailsFun());

        order_check_in_edit_btn.setOnClickListener(v -> orderCheckInFun());

        order_check_in_lay.setOnClickListener(v -> orderCheckInFun());

        order_dates_edit_btn.setOnClickListener(v -> orderDatesFun());

        order_dates_lay.setOnClickListener(v -> orderDatesFun());


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    private void orderGuestDetailsFun() {
        final int[] adult_count = {1};
        final int[] children_count = {0};
        final int[] guest_added = {1};

        adult_count[0] = main_adult_count;
        children_count[0] = main_children_count;
        guest_added[0] = main_guest_added;

        BottomSheetDialog guest_no_bottom_dialog;
        guest_no_bottom_dialog = new BottomSheetDialog(OrdersActivity.this, R.style.BottomSheetTheme);
        guest_no_bottom_dialog.setContentView(R.layout.guest_edit_bottom_dialog);
        guest_no_bottom_dialog.setCanceledOnTouchOutside(true);
        guest_no_bottom_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView save_btn = guest_no_bottom_dialog.findViewById(R.id.guest_bottom_save_btn);

        ImageButton adult_no_decrease_btn = guest_no_bottom_dialog.findViewById(R.id.adult_no_decrease_btn);
        ImageButton adult_no_increase_btn = guest_no_bottom_dialog.findViewById(R.id.adult_no_increase_btn);
        ImageButton children_no_decrease_btn = guest_no_bottom_dialog.findViewById(R.id.children_no_decrease_btn);
        ImageButton children_no_increase_btn = guest_no_bottom_dialog.findViewById(R.id.children_no_increase_btn);

        TextView adult_count_txt = guest_no_bottom_dialog.findViewById(R.id.adult_count_txt);
        TextView children_count_txt = guest_no_bottom_dialog.findViewById(R.id.children_count_txt);

        adult_count_txt.setText("" + adult_count[0]);
        children_count_txt.setText("" + children_count[0]);

        if (adult_count[0] == 1 || adult_count[0] < 1) {
            adult_no_decrease_btn.setEnabled(false);
            adult_no_decrease_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.light_grey)));
        } else {
            adult_no_decrease_btn.setEnabled(true);
            adult_no_decrease_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.second_black)));
        }

        if (children_count[0] == 0 || children_count[0] < 0) {
            children_no_decrease_btn.setEnabled(false);
            children_no_decrease_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.light_grey)));
        } else {
            children_no_decrease_btn.setEnabled(true);
            children_no_decrease_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.second_black)));
        }

        if (guest_added[0] >= total_no_guests) {
            adult_no_increase_btn.setEnabled(false);
            adult_no_increase_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.light_grey)));

            children_no_increase_btn.setEnabled(false);
            children_no_increase_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.light_grey)));
        } else {
            adult_no_increase_btn.setEnabled(true);
            adult_no_increase_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.second_black)));

            children_no_increase_btn.setEnabled(true);
            children_no_increase_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.second_black)));
        }

        children_no_decrease_btn.setOnClickListener(v1 -> {
            children_count[0]--;
            guest_added[0]--;
            children_count_txt.setText("" + children_count[0]);

            if (guest_added[0] >= total_no_guests) {
                adult_no_increase_btn.setEnabled(false);
                adult_no_increase_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.light_grey)));

                children_no_increase_btn.setEnabled(false);
                children_no_increase_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.light_grey)));
            } else {
                adult_no_increase_btn.setEnabled(true);
                adult_no_increase_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.second_black)));

                children_no_increase_btn.setEnabled(true);
                children_no_increase_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.second_black)));
            }

            if (children_count[0] == 0 || children_count[0] < 0) {
                children_no_decrease_btn.setEnabled(false);
                children_no_decrease_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.light_grey)));
            } else {
                children_no_decrease_btn.setEnabled(true);
                children_no_decrease_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.second_black)));
            }
        });

        children_no_increase_btn.setOnClickListener(v2 -> {
            children_count[0]++;
            guest_added[0]++;
            children_count_txt.setText("" + children_count[0]);

            if (guest_added[0] >= total_no_guests) {
                adult_no_increase_btn.setEnabled(false);
                adult_no_increase_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.light_grey)));

                children_no_increase_btn.setEnabled(false);
                children_no_increase_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.light_grey)));
            } else {
                adult_no_increase_btn.setEnabled(true);
                adult_no_increase_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.second_black)));

                children_no_increase_btn.setEnabled(true);
                children_no_increase_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.second_black)));
            }

            if (children_count[0] == 0 || children_count[0] < 0) {
                children_no_decrease_btn.setEnabled(false);
                children_no_decrease_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.light_grey)));
            } else {
                children_no_decrease_btn.setEnabled(true);
                children_no_decrease_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.second_black)));
            }

        });

        adult_no_decrease_btn.setOnClickListener(v3 -> {
            adult_count[0]--;
            guest_added[0]--;
            adult_count_txt.setText("" + adult_count[0]);

            if (guest_added[0] >= total_no_guests) {
                adult_no_increase_btn.setEnabled(false);
                adult_no_increase_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.light_grey)));

                children_no_increase_btn.setEnabled(false);
                children_no_increase_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.light_grey)));
            } else {
                adult_no_increase_btn.setEnabled(true);
                adult_no_increase_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.second_black)));

                children_no_increase_btn.setEnabled(true);
                children_no_increase_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.second_black)));
            }

            if (adult_count[0] == 1 || adult_count[0] < 1) {
                adult_no_decrease_btn.setEnabled(false);
                adult_no_decrease_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.light_grey)));
            } else {
                adult_no_decrease_btn.setEnabled(true);
                adult_no_decrease_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.second_black)));
            }
        });

        adult_no_increase_btn.setOnClickListener(v4 -> {
            adult_count[0]++;
            guest_added[0]++;
            adult_count_txt.setText("" + adult_count[0]);

            if (guest_added[0] >= total_no_guests) {
                adult_no_increase_btn.setEnabled(false);
                adult_no_increase_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.light_grey)));

                children_no_increase_btn.setEnabled(false);
                children_no_increase_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.light_grey)));
            } else {
                adult_no_increase_btn.setEnabled(true);
                adult_no_increase_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.second_black)));

                children_no_increase_btn.setEnabled(true);
                children_no_increase_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.second_black)));
            }

            if (adult_count[0] == 1 || adult_count[0] < 1) {
                adult_no_decrease_btn.setEnabled(false);
                adult_no_decrease_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.light_grey)));
            } else {
                adult_no_decrease_btn.setEnabled(true);
                adult_no_decrease_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.second_black)));
            }

        });

        TextView guest_botttom_notice = guest_no_bottom_dialog.findViewById(R.id.guest_bottom_notice);
        if (guest_botttom_notice != null) {
            guest_botttom_notice.setText("This hotel booking can accomodate " + total_no_guests + " guests booking at a time.");
        }

        save_btn.setOnClickListener(v1 -> {
            main_adult_count = adult_count[0];
            main_children_count = children_count[0];
            main_guest_added = guest_added[0];

            if (main_guest_added == 1) {
                order_guest_details_txt.setText(main_guest_added + " guest");
            } else if (main_guest_added > 1) {
                order_guest_details_txt.setText(main_guest_added + " guests");
            }

            no_rooms = new Functions().getNoRooms(main_guest_added, people_accomodate_no);
            Log.e("no_rooms", "No of rooms are : " + no_rooms);

            updatePrice();

            guest_no_bottom_dialog.dismiss();
        });
        guest_no_bottom_dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    @SuppressWarnings("unchecked")
    private void orderDatesFun() {
        Date today = new Date(calendar.get(Calendar.YEAR) - 1900, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        today = new Functions().modifiedDate(today, 2);

        Date yourSDate = startDate;
        Date yourEDate = endDate;
        yourSDate = new Functions().modifiedDate(yourSDate, 1);
        yourEDate = new Functions().modifiedDate(yourEDate, 1);

        Log.e("getDate", "Start Date is : " + yourSDate);
        Log.e("getDate", "End Date is : " + yourEDate);

        CalendarConstraints.Builder constraintbuilder = new CalendarConstraints.Builder();
        constraintbuilder.setValidator(DateValidatorPointForward.from(today.getTime()));

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("SELECT DATE RANGE");

        Pair<Long, Long> mypair = new Pair<>(yourSDate.getTime(), yourEDate.getTime());
        builder.setSelection(mypair);

        builder.setCalendarConstraints(constraintbuilder.build());
        final MaterialDatePicker materialDatePicker = builder.build();
        materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");

        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            order_dates_txt.setText(materialDatePicker.getHeaderText());
            Pair<Long, Long> new_Dates = (Pair<Long, Long>) selection;

            startDate = new Date(new_Dates.first);
            startDate.setHours(updated_hour);
            startDate.setMinutes(0);
            startDate.setSeconds(0);
            endDate = new Date(new_Dates.second);
            endDate.setHours(updated_hour);
            endDate.setMinutes(0);
            endDate.setSeconds(0);

            if (!order_check_in_txt.getText().equals("Add the check time")) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a, dd MMM yy");
                    s_am_pm = simpleDateFormat.format(startDate);
                    e_am_pm = simpleDateFormat.format(endDate);
                    order_check_in_txt.setText(s_am_pm);
                    order_check_out_txt.setText(e_am_pm);
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                no_days = new Functions().getNoDays(startDate, endDate);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yy");
                String defaultStartDatetxt = simpleDateFormat.format(startDate);
                String defaultEndDatetxt = simpleDateFormat.format(endDate);
                order_dates_txt.setText("(" + defaultStartDatetxt + ")" + " - (" + defaultEndDatetxt + ")");
                Log.e("getAllTime", "Date and Time is :" + startDate);
            }

            updatePrice();
            Log.e("getAllTime", "Date and Time is :" + startDate);
        });
    }

    private void orderCheckInFun() {
        Log.e("getAllTime", "Date and Time is :" + startDate);
        BottomSheetDialog checkin_bottom_dialog;
        checkin_bottom_dialog = new BottomSheetDialog(OrdersActivity.this, R.style.BottomSheetTheme);
        checkin_bottom_dialog.setContentView(R.layout.checkin_bottom_dialog);
        checkin_bottom_dialog.setCanceledOnTouchOutside(true);
        checkin_bottom_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView checkin_save_btn = checkin_bottom_dialog.findViewById(R.id.checkin_save_btn);
        RadioGroup checkin_radio_grp = checkin_bottom_dialog.findViewById(R.id.checkin_radio_grp);
        RadioButton checkin_morning_radio_btn = checkin_bottom_dialog.findViewById(R.id.checkin_morning_radio_btn);
        RadioButton checkin_afternoon_radio_btn = checkin_bottom_dialog.findViewById(R.id.checkin_afternoon_radio_btn);
        RadioButton checkin_night_radio_btn = checkin_bottom_dialog.findViewById(R.id.checkin_night_radio_btn);

        if (s_am_pm == null || s_am_pm.isEmpty() || s_am_pm.equals("")) {
            checkin_save_btn.setEnabled(false);
            checkin_save_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.light_grey)));
            checkin_save_btn.setTextColor(getResources().getColor(R.color.order_check_in_no));
        } else {
            checkin_save_btn.setEnabled(true);
            checkin_save_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.second_black)));
            checkin_save_btn.setTextColor(getResources().getColor(R.color.order_check_in_yes));
        }

        final int[] change_hour = {0};

        if (startDate.getHours() == 8) {
            checkin_radio_grp.check(R.id.checkin_morning_radio_btn);
        } else if (startDate.getHours() == 12) {
            checkin_radio_grp.check(R.id.checkin_afternoon_radio_btn);
        } else if (startDate.getHours() == 21) {
            checkin_radio_grp.check(R.id.checkin_night_radio_btn);
        }

        checkin_radio_grp.setOnCheckedChangeListener((group, checkedId) -> {

            checkin_save_btn.setEnabled(true);
            checkin_save_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.second_black)));
            checkin_save_btn.setTextColor(getResources().getColor(R.color.order_check_in_yes));

            if (checkin_morning_radio_btn.getId() == checkedId) {
                change_hour[0] = 8;
            } else if (checkin_afternoon_radio_btn.getId() == checkedId) {
                change_hour[0] = 12;
            } else if (checkin_night_radio_btn.getId() == checkedId) {
                change_hour[0] = 21;
            }
        });

        checkin_save_btn.setOnClickListener(v1 -> {
            startDate.setHours(change_hour[0]);
            endDate.setHours(change_hour[0]);
            Log.e("getAllTime", "Date and Time is :" + startDate);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm a, dd MMM yy");
                s_am_pm = simpleDateFormat.format(startDate);
                e_am_pm = simpleDateFormat.format(endDate);
            }

            order_check_in_txt.setText(s_am_pm);
            order_check_out_lay.setVisibility(View.VISIBLE);
            updated_hour = change_hour[0];
            order_check_out_txt.setText(e_am_pm);
            order_check_in_edit_btn.setText(getResources().getString(R.string.edit));
            checkin_bottom_dialog.dismiss();
        });

        checkin_bottom_dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    private void updatePrice() {

        DecimalFormat formatter = new DecimalFormat("#,##,###");

        if (summaryData != null) {
            main_price = (int) summaryData.getInitial_price();
            service_fee = (int) summaryData.getService_fee();
            tax_gst = (int) summaryData.getTax_gst_fee();
            total_no_guests = (int) summaryData.getPeople_no_booking();
            people_accomodate_no = (int) summaryData.getPeople_accomodation_no();
            total_final_price = main_price;
        }

        int initial_price_of_room = main_price;
        main_price = main_price * no_rooms;
        service_fee = discountedAmount(service_fee, main_guest_added, 0.2);
        tax_gst = discountedAmount(tax_gst, main_guest_added, 0.035);
        total_final_price = main_price + service_fee + tax_gst;


        initial_order_tprice.setText("₹ " + formatter.format(total_final_price));
        total_final_price = updatePriceAccTime(total_final_price, no_days);

        if (no_rooms == 1) {
            order_main_price_header.setText("₹ " + formatter.format(initial_price_of_room) + " x " + no_rooms + " room");
        } else {
            order_main_price_header.setText("₹ " + formatter.format(initial_price_of_room) + " x " + no_rooms + " rooms");
        }

        if (no_days == 1) {
            initial_order_tprice_days_count.setText("x " + no_days + " day / night");
        } else {
            initial_order_tprice_days_count.setText("x " + no_days + " days / nights");
        }
        order_main_price.setText("₹ " + formatter.format(main_price));
        order_service_price.setText("₹ " + formatter.format(service_fee));
        order_tax_price.setText("₹ " + formatter.format(tax_gst));
        order_final_tprice.setText("₹ " + formatter.format(total_final_price));
    }

    private int updatePriceAccTime(int add_tprice, int no_days) {
        add_tprice = add_tprice * no_days;
        return add_tprice;
    }

    @Contract(pure = true)
    private int discountedAmount(int ser_fee, int mtotalguests, double dis_percent) {
        int final_amount = ser_fee;
        double increment_rate = dis_percent * ser_fee;
        double increment_price = increment_rate * (mtotalguests - 1);
        final_amount = (int) (final_amount + increment_price);
        return final_amount;
    }

    private void orderConfirmTextFun() {

        String term_cond = getResources().getString(R.string.terms_conditions);

        SpannableString spannableString = new SpannableString(term_cond);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(getApplicationContext(), TermsCondnActivity.class));
            }
        };
        spannableString.setSpan(clickableSpan, 72, 92, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        order_confirm_terms_txt.setText(spannableString);
        order_confirm_terms_txt.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void orderInits() {
        orders_container = findViewById(R.id.orders_container);
        order_back_btn = findViewById(R.id.order_back_btn);
        order_review_hotel_name = findViewById(R.id.order_review_hotel_name);
        order_review_place_name = findViewById(R.id.order_review_place_name);
        order_review_room_cap = findViewById(R.id.order_review_room_cap);
        order_review_hotel_rate = findViewById(R.id.order_review_hotel_rate);
        order_guest_details_txt = findViewById(R.id.order_guest_details_txt);
        order_dates_txt = findViewById(R.id.order_dates_txt);
        order_check_in_txt = findViewById(R.id.order_check_in_txt);
        order_check_out_txt = findViewById(R.id.order_check_out_txt);
        order_himg = findViewById(R.id.order_himg);

        order_guest_details_lay = findViewById(R.id.order_guest_details_lay);
        order_dates_lay = findViewById(R.id.order_dates_lay);
        order_check_in_lay = findViewById(R.id.order_check_in_lay);

        order_guest_details_edit_btn = findViewById(R.id.order_guest_details_edit_btn);
        order_dates_edit_btn = findViewById(R.id.order_dates_edit_btn);
        order_check_in_edit_btn = findViewById(R.id.order_check_in_edit_btn);

        order_main_price_header = findViewById(R.id.order_main_price_header);
        order_main_price = findViewById(R.id.order_main_price);
        order_service_price = findViewById(R.id.order_service_price);
        order_tax_price = findViewById(R.id.order_tax_price);
        initial_order_tprice = findViewById(R.id.initial_order_tprice);
        initial_order_tprice_days_count = findViewById(R.id.initial_order_tprice_days_count);
        order_final_tprice = findViewById(R.id.order_final_tprice);
        order_confirm_terms_txt = findViewById(R.id.order_confirm_terms_txt);
        order_check_out_lay = findViewById(R.id.order_check_out_lay);
        order_confirm_btn = findViewById(R.id.order_confirm_btn);
        order_check_agree_book = findViewById(R.id.order_check_agree_book);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        startDate = new Date(calendar.get(Calendar.YEAR) - 1900, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        endDate = new Date(calendar.get(Calendar.YEAR) - 1900, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

        if (s_am_pm == null) {
            order_check_out_lay.setVisibility(View.GONE);
        } else {
            order_check_out_lay.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onPaymentSuccess(String s) {

        final Dialog dialog = new Dialog(OrdersActivity.this);
        dialog.setContentView(R.layout.order_summary_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;

        TextView summary_refernce_code = dialog.findViewById(R.id.summary_refernce_code);
        ImageButton summary_copy_btn = dialog.findViewById(R.id.summary_copy_btn);
        TextView summary_ok_btn = dialog.findViewById(R.id.summary_ok_btn);

        summary_refernce_code.setText(s);
        String order_id;

        if (hotel_summary_order_id != null) {
            order_id = hotel_summary_order_id;
        } else {
            Random r = new Random();
            int random_id1 = r.nextInt(9999);
            int random_id2 = r.nextInt(random_id1);
            order_id = "order" + firebaseUser.getUid().substring(0, 4) + random_id1 + random_id2;
        }

        String username = new Functions().username(OrdersActivity.this);
        String content_txt = "Booked: " + no_rooms + " rooms from the hotel '" + summaryData.getHotel_name() + "' has been booked successfully.\nThe rooms has been booked by " + username + " for " + no_days + " Days.\n\nBooked at: " + new Date();

        SharedPreferences prefs = getSharedPreferences("app_data", Activity.MODE_PRIVATE);
        boolean notifi_check = prefs.getBoolean("notification_check", true);
        if (notifi_check) {
            addNotficationBooking(content_txt, order_id);
        }

        Map<String, Object> map = new HashMap();
        map.put("order_id", order_id);
        map.put("notify_content_txt", content_txt);
        map.put("notify_date", new Date());
        map.put("seen", false);
        db.collection("users")
                .document(firebaseUser.getUid())
                .collection("user_notification")
                .document(order_id)
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.e("notify_add", "notify added");
                        } else {
                            Log.e("notify_add", "notify not added");
                        }
                    }
                });


        setOrderToMainDatabase(s, order_id);
        setDataToSummaryOrderDB(s, true, null, order_id);

        summary_copy_btn.setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("String", s);
            clipboardManager.setPrimaryClip(clipData);
            clipData.getDescription();
            Snackbar.make(orders_container, "Reference ID Copied", Snackbar.LENGTH_SHORT).show();
        });
        summary_ok_btn.setOnClickListener(v -> {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }, 500);
        });
        dialog.show();
    }

    private void addNotficationBooking(String content_txt, String order_id) {
        new Functions().notificationChannel(getApplicationContext());
        Intent notificationintent = new Intent(getApplicationContext(), Inbox_details.class);
        notificationintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationintent.putExtra("inbox_details_content", content_txt);
        notificationintent.putExtra("inbox_details_orderid", order_id);
        notificationintent.putExtra("inbox_details_checked", true);
        int uniqueInt = (int) (System.currentTimeMillis() & 0xff);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), uniqueInt, notificationintent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), "notifytheapp")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("The hotel has been booked")
                .setContentText(content_txt)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true).build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(200, notification);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onPaymentError(int i, String s) {
        final Dialog dialog = new Dialog(OrdersActivity.this);
        dialog.setContentView(R.layout.order_summary_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;

        try {
            JSONObject mainObject = new JSONObject(s);
            JSONObject uniObject = mainObject.getJSONObject("error");
            String error_des = uniObject.getString("description");
            String error_reason = uniObject.getString("reason");

            String order_id;

            if (hotel_summary_order_id != null) {
                order_id = hotel_summary_order_id;
            } else {
                Random r = new Random();
                int random_id1 = r.nextInt(9999);
                int random_id2 = r.nextInt(random_id1);
                order_id = "order" + firebaseUser.getUid().substring(0, 4) + random_id1 + random_id2;
            }

            setDataToSummaryOrderDB(null, false, error_des, order_id);

            LottieAnimationView summary_order_anim = dialog.findViewById(R.id.summary_order_anim);
            TextView summary_header = dialog.findViewById(R.id.summary_header);
            TextView summary_sub_title = dialog.findViewById(R.id.summary_sub_title);
            LinearLayout reference_code_lay = dialog.findViewById(R.id.reference_code_lay);
            TextView summary_ok_btn = dialog.findViewById(R.id.summary_ok_btn);

            summary_order_anim.setAnimation(R.raw.failed_anim);
            reference_code_lay.setVisibility(View.GONE);
            summary_header.setText("Your Order Failed !!");
            summary_sub_title.setText("You order has been failed as " + error_des + "\n\nError is : " + error_reason);
            summary_ok_btn.setOnClickListener(v -> {
                dialog.dismiss();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog.show();
    }

    private void setOrderToMainDatabase(String reference_id, String order_id) {

        db.collection("allOrders")
                .document(order_id)
                .set(new SummaryOrderModel(
                        summaryData.getDb_name(), summaryData.getMain_place_id(), summaryData.getInner_place_id(),
                        order_id, summaryData.getHotel_id(), summaryData.getHotel_name(),
                        summaryData.getPlace_name(), summaryData.getHotel_img().get(0), main_price,
                        tax_gst, service_fee, total_final_price,
                        no_days, main_guest_added, no_rooms,
                        summaryData.getHotel_contact().get(0), summaryData.getHotel_contact().get(1), summaryData.getHotel_contact().get(2),
                        startDate, endDate, s_am_pm, e_am_pm,
                        new Date(), reference_id
                ), SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("Summary_Data", "Data Added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Summary_Data", "Data Add Failed!!");
                    }
                });
    }

    private void setDataToSummaryOrderDB(String reference_id, boolean order_summary_status, String error_msg, String order_id) {

        db.collection("users")
                .document(firebaseUser.getUid())
                .collection("Orders")
                .document(order_id)
                .set(new SummaryOrderModel(
                        summaryData.getDb_name(), summaryData.getMain_place_id(), summaryData.getInner_place_id(),
                        order_id, summaryData.getHotel_id(), summaryData.getHotel_name(),
                        summaryData.getPlace_name(), summaryData.getHotel_img().get(0), main_price,
                        tax_gst, service_fee, total_final_price,
                        no_days, main_guest_added, no_rooms,
                        summaryData.getHotel_contact().get(0), summaryData.getHotel_contact().get(1), summaryData.getHotel_contact().get(2),
                        startDate, endDate, s_am_pm, e_am_pm,
                        new Date(), order_summary_status, reference_id, error_msg
                ))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("Summary_Data", "Data Added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Summary_Data", "Data Add Failed!!");
                    }
                });
    }
}