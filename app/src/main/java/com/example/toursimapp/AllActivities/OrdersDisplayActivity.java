package com.example.toursimapp.AllActivities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.Models.HotelDetailModel;
import com.example.toursimapp.Models.SummaryOrderModel;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.N)
public class OrdersDisplayActivity extends AppCompatActivity {

    private static final int WRITE_EXTERNAL_CODE = 1;
    private final DecimalFormat formatter = new DecimalFormat("#,##,###");
    private SummaryOrderModel summaryOrderModel;
    private ImageButton order_dis_back_btn, order_dis_referenceid_copy_btn;
    private ImageView order_dis_hotelimage;
    private TextView order_dis_referenceid, order_dis_booked_msg, order_dis_main_price, order_dis_service_fee, order_dis_tax_gst_fee, order_dis_final_price, order_dis_final_price_title;
    private TextView order_dis_hotel_name, order_dis_place_name, order_dis_no_guests, order_dis_no_rooms, order_dis_dates, order_dis_checkin_time, order_dis_checkout_time;
    private Button order_dis_visit_hotel_btn, order_dis_print_invoice, order_dis_book_process, order_dis_feedback_btn;
    private FirebaseUser firebaseUser;
    private View order_dis_invoice_page1_lay, order_dis_invoice_page2_lay, order_dis_titlebar_lay;
    private TextView order_dis_pricing_title, order_dis_hotel_details_title, order_dis_user_title, order_dis_hdetails_title;
    private TextView order_dis_guest_name, order_dis_guest_emailID, order_dis_guest_mobile, order_dis_hdetails_contact, order_dis_hdetails_website, order_dis_hdetails_emailID;
    private View order_dis_hdetails_contact_lay, order_dis_hdetails_website_lay, order_dis_hdetails_emailID_lay;
    private FirebaseFirestore db;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Functions().checkTheme(getApplicationContext());
        setContentView(R.layout.activity_orders_display);

        orderDisplayInits();
        orderDisplaySetValues();
        orderDisplayEventListnerFuns();

    }

    @SuppressWarnings("unchecked")
    private void orderDisplayEventListnerFuns() {

        order_dis_visit_hotel_btn.setOnClickListener(v -> {

            final float[] allOrdersSize = {0};
            final float[] thisHotelOrdersSize = {0};

            db.collection(summaryOrderModel.getDbname())
                    .document(summaryOrderModel.getMain_place_id())
                    .collection("hotels_section")
                    .document(summaryOrderModel.getHotel_id())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful() || task.getResult() != null) {
                                HotelDetailModel hotelDetailModel = new HotelDetailModel(
                                        summaryOrderModel.getDbname(),
                                        summaryOrderModel.getMain_place_id(),
                                        summaryOrderModel.getInner_place_id(),
                                        task.getResult().getString("hotel_id"),
                                        task.getResult().getString("hotel_name"),
                                        task.getResult().getString("place_name"),
                                        task.getResult().getString("hotel_rate"),
                                        task.getResult().getString("hotel_des"),
                                        (Double) task.getResult().get("hotel_longitude"),
                                        (Double) task.getResult().get("hotel_latitude"),
                                        (ArrayList<String>) task.getResult().get("hotel_img"),
                                        (ArrayList<String>) task.getResult().get("hotel_fac"),
                                        (ArrayList<String>) task.getResult().get("hotel_contact"),
                                        (long) task.getResult().get("people_no_booking"),
                                        (long) task.getResult().get("initial_price"),
                                        (long) task.getResult().get("service_fee"),
                                        (long) task.getResult().get("tax_gst_fee"),
                                        (long) task.getResult().get("people_accomodation_no"));

                                db.collection("allOrders")
                                        .whereEqualTo("main_place_id", hotelDetailModel.getMain_place_id())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    allOrdersSize[0] = task.getResult().size();
                                                } else {
                                                    allOrdersSize[0] = 0;
                                                }
                                                db.collection("allOrders")
                                                        .whereEqualTo("main_place_id", hotelDetailModel.getMain_place_id())
                                                        .whereEqualTo("hotel_id", hotelDetailModel.getHotel_id())
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    thisHotelOrdersSize[0] = task.getResult().size();
                                                                } else {
                                                                    thisHotelOrdersSize[0] = 0;
                                                                }
                                                                Intent intent = new Intent(getApplicationContext(), HotelActivity.class);
                                                                intent.putExtra("allOrders", allOrdersSize[0]);
                                                                intent.putExtra("thisHotelOrders", thisHotelOrdersSize[0]);
                                                                intent.putExtra("par_hotel_details_bundle", hotelDetailModel);
                                                                startActivity(intent);
                                                            }
                                                        });
                                            }
                                        });
                            } else {
                                Log.e("visit_hotel", "Empty Data");
                            }
                        }
                    });
        });

        order_dis_back_btn.setOnClickListener(v -> {
            finish();
        });

        order_dis_referenceid_copy_btn.setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("String", summaryOrderModel.getReference_id());
            clipboardManager.setPrimaryClip(clipData);
            clipData.getDescription();
            Snackbar.make(v, "Reference ID Copied", Snackbar.LENGTH_SHORT).show();
        });

        order_dis_book_process.setOnClickListener(v -> {
            db.collection(summaryOrderModel.getDbname())
                    .document(summaryOrderModel.getMain_place_id())
                    .collection("hotels_section")
                    .document(summaryOrderModel.getHotel_id())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult() != null) {
                                    HotelDetailModel hotelDetailModel = new HotelDetailModel(
                                            summaryOrderModel.getDbname(),
                                            summaryOrderModel.getMain_place_id(),
                                            summaryOrderModel.getInner_place_id(),
                                            task.getResult().getString("hotel_id"),
                                            task.getResult().getString("hotel_name"),
                                            task.getResult().getString("place_name"),
                                            task.getResult().getString("hotel_rate"),
                                            task.getResult().getString("hotel_des"),
                                            (Double) task.getResult().get("hotel_longitude"),
                                            (Double) task.getResult().get("hotel_latitude"),
                                            (ArrayList<String>) task.getResult().get("hotel_img"),
                                            (ArrayList<String>) task.getResult().get("hotel_fac"),
                                            (ArrayList<String>) task.getResult().get("hotel_contact"),
                                            (long) task.getResult().get("people_no_booking"),
                                            (long) task.getResult().get("initial_price"),
                                            (long) task.getResult().get("service_fee"),
                                            (long) task.getResult().get("tax_gst_fee"),
                                            (long) task.getResult().get("people_accomodation_no")
                                    );

                                    Intent intent = new Intent(getApplicationContext(), OrdersActivity.class);
                                    if (summaryOrderModel.getReference_id() == null) {
                                        intent.putExtra("hotel_summary_order_id", summaryOrderModel.getOrder_id());
                                    }
                                    intent.putExtra("par_hotel_summary_bundle", hotelDetailModel);
                                    startActivity(intent);
                                } else {
                                    Log.e("order_dis_book_process", "Empty Data");
                                }
                            } else {
                                Log.e("order_dis_book_process", "Process Data");
                            }
                        }
                    });
        });

        order_dis_feedback_btn.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(OrdersDisplayActivity.this);
            dialog.setContentView(R.layout.feedback_dialog);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
            EditText feedback_txt = dialog.findViewById(R.id.feedback_txt);
            TextView cancel = dialog.findViewById(R.id.feedback_cancel_btn);
            TextView send_feedback = dialog.findViewById(R.id.feedback_send_btn);

            cancel.setOnClickListener(v1 -> dialog.cancel());
            send_feedback.setOnClickListener(v12 -> {
                String feedback_msg;
                feedback_msg = feedback_txt.getText().toString();

                Map<String, Object> map = new HashMap<>();
                map.put("feedback_msg", feedback_msg);

                db.collection("user_feedback")
                        .document(firebaseUser.getUid())
                        .set(map)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getApplicationContext(), "Feedback Sent..", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to send Feedback !! Please Try Again", Toast.LENGTH_SHORT).show());

            });

            dialog.show();
        });

        order_dis_print_invoice.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission, WRITE_EXTERNAL_CODE);
                } else {
                    invoiceLayToImage(order_dis_invoice_page1_lay, order_dis_invoice_page2_lay, v);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission_for_storage", "Rejected");
            } else {
                Toast.makeText(this, "Permission Enable", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void invoiceLayToImage(@NotNull View view1, @NotNull View view2, View btn_view) {

        File getInvoiceDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/TourBuddy/");

        String getInvoiceName = summaryOrderModel.getOrder_id() + " (" + summaryOrderModel.getHotel_name() + ").pdf";
        File getInvoiceFile = new File(getInvoiceDir, getInvoiceName);

        if (!getInvoiceFile.exists()) {
            Bitmap pageImg1 = Bitmap.createBitmap(view1.getWidth(), view1.getHeight(), Bitmap.Config.ARGB_8888);
            Bitmap pageImg2 = Bitmap.createBitmap(view1.getWidth(), view1.getHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas1 = new Canvas(pageImg1);
            Drawable bgDrawable1 = view1.getBackground();
            if (bgDrawable1 != null) {
                bgDrawable1.draw(canvas1);
            } else {
                canvas1.drawColor(Color.WHITE);
            }
            view1.draw(canvas1);

            Canvas canvas2 = new Canvas(pageImg2);
            Drawable bgDrawable2 = view2.getBackground();
            if (bgDrawable2 != null) {
                bgDrawable2.draw(canvas2);
            } else {
                canvas2.drawColor(Color.WHITE);
            }
            view2.draw(canvas2);

            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageinfo1 = new PdfDocument.PageInfo.Builder(pageImg1.getWidth(), pageImg1.getHeight(), 1).create();
            PdfDocument.PageInfo pageinfo2 = new PdfDocument.PageInfo.Builder(pageImg2.getWidth(), pageImg2.getHeight(), 2).create();

            PdfDocument.Page page1 = pdfDocument.startPage(pageinfo1);
            page1.getCanvas().drawBitmap(pageImg1, 0, 0, null);
            pdfDocument.finishPage(page1);

            PdfDocument.Page page2 = pdfDocument.startPage(pageinfo2);
            page2.getCanvas().drawBitmap(pageImg2, 0, 0, null);
            pdfDocument.finishPage(page2);

            File pathDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/TourBuddy/");
            if (!pathDir.exists()) {
                pathDir.mkdirs();
            }
            String invoice_name = summaryOrderModel.getOrder_id() + " (" + summaryOrderModel.getHotel_name() + ").pdf";
            File invoice_file = new File(pathDir, invoice_name);

            try {
                pathDir.mkdir();

                FileOutputStream fileOutputStream = new FileOutputStream(invoice_file);
                pdfDocument.writeTo(fileOutputStream);

                if (getInvoiceFile.exists()) {
                    Snackbar.make(btn_view, "Invoice pdf saved to device", Snackbar.LENGTH_SHORT).show();
                }
            } catch (IOException exception) {
                Log.e("pdf_process", "File Save Error : " + exception);
                Toast.makeText(this, "File Save Error!!!\n\n" + exception, Toast.LENGTH_SHORT).show();
            }
            pdfDocument.close();
        } else {
            Snackbar.make(btn_view, "This invoice already exists in your device", Snackbar.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    private void orderDisplaySetValues() {

        if (summaryOrderModel.getReference_id() == null) {
            new Functions().setStatusBarColor(OrdersDisplayActivity.this, R.color.my_red);
            order_dis_booked_msg.setText("Hotel Booked Failed");
            order_dis_referenceid_copy_btn.setVisibility(View.GONE);
            order_dis_referenceid.setText(summaryOrderModel.getError_msg());
            order_dis_book_process.setText("Retry Booking");
            order_dis_user_title.setTextColor(getColor(R.color.my_red));
            order_dis_hdetails_title.setTextColor(getColor(R.color.my_red));
            order_dis_hdetails_contact.setTextColor(getColor(R.color.my_red));
            order_dis_hdetails_website.setTextColor(getColor(R.color.my_red));
            order_dis_hdetails_emailID.setTextColor(getColor(R.color.my_red));
            order_dis_titlebar_lay.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.my_red)));
            order_dis_pricing_title.setTextColor(getColor(R.color.my_red));
            order_dis_hotel_details_title.setTextColor(getColor(R.color.my_red));
            order_dis_feedback_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.my_red)));
            order_dis_book_process.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.my_red)));
            order_dis_print_invoice.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.my_red)));
            order_dis_print_invoice.setVisibility(View.GONE);
            order_dis_visit_hotel_btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            order_dis_visit_hotel_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.my_red)));
        } else {
            new Functions().setStatusBarColor(OrdersDisplayActivity.this, R.color.my_green);
            order_dis_booked_msg.setText("Hotel Booked Successfully");
            order_dis_referenceid_copy_btn.setVisibility(View.VISIBLE);
            order_dis_referenceid.setText(summaryOrderModel.getReference_id());
            order_dis_book_process.setText("Book Again");
            order_dis_user_title.setTextColor(getColor(R.color.my_green));
            order_dis_hdetails_title.setTextColor(getColor(R.color.my_green));
            order_dis_hdetails_contact.setTextColor(getColor(R.color.my_green));
            order_dis_hdetails_website.setTextColor(getColor(R.color.my_green));
            order_dis_hdetails_emailID.setTextColor(getColor(R.color.my_green));
            order_dis_titlebar_lay.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.my_green)));
            order_dis_pricing_title.setTextColor(getColor(R.color.my_green));
            order_dis_hotel_details_title.setTextColor(getColor(R.color.my_green));
            order_dis_feedback_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.my_green)));
            order_dis_book_process.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.my_green)));
            order_dis_print_invoice.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.my_green)));
            order_dis_print_invoice.setVisibility(View.VISIBLE);
            order_dis_visit_hotel_btn.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.my_green)));
        }

        String day, guest, room;
        if (summaryOrderModel.getNo_days() == 1)
            day = "day";
        else
            day = "days";

        if (summaryOrderModel.getTotal_no_guests() == 1)
            guest = "guest";
        else
            guest = "guests";

        if (summaryOrderModel.getNo_rooms() == 1)
            room = "room";
        else
            room = "rooms";

        Glide.with(getApplicationContext()).load(summaryOrderModel.getHotel_img()).into(order_dis_hotelimage);
        int main_price = (int) (summaryOrderModel.getMain_price() + summaryOrderModel.getNo_rooms());
        order_dis_main_price.setText("₹ " + formatter.format(main_price));
        order_dis_service_fee.setText("₹ " + formatter.format(summaryOrderModel.getService_fee()));
        order_dis_tax_gst_fee.setText("₹ " + formatter.format(summaryOrderModel.getTax_gst()));

        order_dis_final_price_title.setText("Total Price ( x " + summaryOrderModel.getNo_days() + " " + day + " ) ");
        order_dis_final_price.setText("₹ " + formatter.format(summaryOrderModel.getTotal_final_price()));
        order_dis_hotel_name.setText(summaryOrderModel.getHotel_name());
        order_dis_place_name.setText(summaryOrderModel.getPlace_name());
        order_dis_no_guests.setText(summaryOrderModel.getTotal_no_guests() + " " + guest);
        order_dis_no_rooms.setText(summaryOrderModel.getNo_rooms() + " " + room);

        if (summaryOrderModel.getHotel_contact_no() == null || summaryOrderModel.getHotel_contact_no().equals("")) {
            order_dis_hdetails_contact_lay.setVisibility(View.GONE);
        } else {
            order_dis_hdetails_contact_lay.setVisibility(View.VISIBLE);
            order_dis_hdetails_contact.setText(summaryOrderModel.getHotel_contact_no());
        }

        if (summaryOrderModel.getHotel_website() == null || summaryOrderModel.getHotel_website().equals("")) {
            order_dis_hdetails_website_lay.setVisibility(View.GONE);
        } else {
            order_dis_hdetails_website_lay.setVisibility(View.VISIBLE);
            order_dis_hdetails_website.setText(summaryOrderModel.getHotel_website());
        }

        if (summaryOrderModel.getHotel_emailID() == null || summaryOrderModel.getHotel_emailID().equals("")) {
            order_dis_hdetails_emailID_lay.setVisibility(View.GONE);
        } else {
            order_dis_hdetails_emailID_lay.setVisibility(View.VISIBLE);
            order_dis_hdetails_emailID.setText(summaryOrderModel.getHotel_emailID());
        }

        int minus_one_guest = Math.toIntExact(summaryOrderModel.getTotal_no_guests() - 1);
        SharedPreferences get_user_data_prefs = getSharedPreferences("user_data", Activity.MODE_PRIVATE);
        String guestname = get_user_data_prefs.getString("fullname", "Guest 1");
        if (minus_one_guest == 0) {
            order_dis_guest_name.setText(guestname);
        } else {
            order_dis_guest_name.setText(guestname + " ( + " + minus_one_guest + " guests )");
        }
        order_dis_guest_emailID.setText(get_user_data_prefs.getString("emailid", "guest1@gmail.com"));
        order_dis_guest_mobile.setText(get_user_data_prefs.getString("contact_number", "+919876543210"));

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yy");
        String startDatetxt = simpleDateFormat.format(summaryOrderModel.getStartDate());
        String endDatetxt = simpleDateFormat.format(summaryOrderModel.getEndDate());
        order_dis_dates.setText(startDatetxt + "  -  " + endDatetxt);

        order_dis_checkin_time.setText(summaryOrderModel.getS_am_pm());
        order_dis_checkout_time.setText(summaryOrderModel.getE_am_pm());
    }

    private void orderDisplayInits() {
        Intent getData = getIntent();
        summaryOrderModel = (SummaryOrderModel) getData.getSerializableExtra("booked_summary_data");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        order_dis_invoice_page1_lay = findViewById(R.id.order_dis_invoice_page1_lay);
        order_dis_invoice_page2_lay = findViewById(R.id.order_dis_invoice_page2_lay);
        order_dis_back_btn = findViewById(R.id.order_dis_back_btn);
        order_dis_hotelimage = findViewById(R.id.order_dis_hotelimage);
        order_dis_referenceid = findViewById(R.id.order_dis_referenceid);
        order_dis_referenceid_copy_btn = findViewById(R.id.order_dis_referenceid_copy_btn);
        order_dis_booked_msg = findViewById(R.id.order_dis_booked_msg);
        order_dis_main_price = findViewById(R.id.order_dis_main_price);
        order_dis_service_fee = findViewById(R.id.order_dis_service_fee);
        order_dis_tax_gst_fee = findViewById(R.id.order_dis_tax_gst_fee);
        order_dis_final_price = findViewById(R.id.order_dis_final_price);
        order_dis_final_price_title = findViewById(R.id.order_dis_final_price_title);
        order_dis_hotel_name = findViewById(R.id.order_dis_hotel_name);
        order_dis_place_name = findViewById(R.id.order_dis_place_name);
        order_dis_no_guests = findViewById(R.id.order_dis_no_guests);
        order_dis_no_rooms = findViewById(R.id.order_dis_no_rooms);
        order_dis_dates = findViewById(R.id.order_dis_dates);
        order_dis_checkin_time = findViewById(R.id.order_dis_checkin_time);
        order_dis_checkout_time = findViewById(R.id.order_dis_checkout_time);
        order_dis_visit_hotel_btn = findViewById(R.id.order_dis_visit_hotel_btn);
        order_dis_book_process = findViewById(R.id.order_dis_book_process);
        order_dis_print_invoice = findViewById(R.id.order_dis_print_invoice);
        order_dis_feedback_btn = findViewById(R.id.order_dis_feedback_btn);

        order_dis_titlebar_lay = findViewById(R.id.order_dis_titlebar_lay);
        order_dis_pricing_title = findViewById(R.id.order_dis_pricing_title);
        order_dis_hotel_details_title = findViewById(R.id.order_dis_hotel_details_title);

        order_dis_user_title = findViewById(R.id.order_dis_user_title);
        order_dis_hdetails_title = findViewById(R.id.order_dis_hdetails_title);

        order_dis_guest_name = findViewById(R.id.order_dis_guest_name);
        order_dis_guest_emailID = findViewById(R.id.order_dis_guest_emailID);
        order_dis_guest_mobile = findViewById(R.id.order_dis_guest_mobile);

        order_dis_hdetails_contact = findViewById(R.id.order_dis_hdetails_contact);
        order_dis_hdetails_website = findViewById(R.id.order_dis_hdetails_website);
        order_dis_hdetails_emailID = findViewById(R.id.order_dis_hdetails_emailID);

        order_dis_hdetails_contact_lay = findViewById(R.id.order_dis_hdetails_contact_lay);
        order_dis_hdetails_website_lay = findViewById(R.id.order_dis_hdetails_website_lay);
        order_dis_hdetails_emailID_lay = findViewById(R.id.order_dis_hdetails_emailID_lay);

    }
}