package com.example.toursimapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toursimapp.Adapters.SummaryBookAdpater;
import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.Models.SummaryOrderModel;
import com.example.toursimapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@SuppressWarnings("all")
public class SummaryBookFragment extends Fragment {

    private final ArrayList<SummaryOrderModel> summary_order_array = new ArrayList<>();
    private Boolean getSummaryFragCheck;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private RecyclerView summary_order_rec;
    private View summary_order_container;
    private TextView summary_order_no_data_txt;

    public SummaryBookFragment() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_summary_book, container, false);

        new Functions().checkTheme(requireContext());
        summaryBookFragInits(v);
        getOrderSummaryData(getSummaryFragCheck);
        return v;
    }

    private void getOrderSummaryData(Boolean getSummaryFragCheck) {
        db.collection("users")
                .document(firebaseUser.getUid())
                .collection("Orders")
                .whereEqualTo("order_summary_status", getSummaryFragCheck)
                .orderBy("bookedDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

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
                        summary_order_array.add(summaryOrderModel);
                    }
                    if (queryDocumentSnapshots.size() <= 0) {
                        summary_order_container.setVisibility(View.VISIBLE);
                        summary_order_rec.setVisibility(View.GONE);
                        if (getSummaryFragCheck) {
                            summary_order_no_data_txt.setText("No Booked Places Occurred.");
                        } else {
                            summary_order_no_data_txt.setText("No Cancellation Process Occurred.");
                        }
                    } else {
                        summary_order_container.setVisibility(View.GONE);
                        summary_order_rec.setVisibility(View.VISIBLE);
                        summary_order_rec.setHasFixedSize(true);
                        summary_order_rec.setLayoutManager(new LinearLayoutManager(getContext()));
                        SummaryBookAdpater summaryBookAdpater = new SummaryBookAdpater(getContext(), summary_order_array);
                        summaryBookAdpater.notifyDataSetChanged();
                        summary_order_rec.setAdapter(summaryBookAdpater);
                    }

                })
                .addOnFailureListener(e -> {
                    summary_order_container.setVisibility(View.VISIBLE);
                    summary_order_rec.setVisibility(View.GONE);
                    Log.e("summary_order_data", "Data is Null");
                });
    }

    private void summaryBookFragInits(@NotNull View v) {
        summary_order_rec = v.findViewById(R.id.summary_order_rec);
        summary_order_container = v.findViewById(R.id.summary_order_container);
        summary_order_no_data_txt = v.findViewById(R.id.summary_order_no_data_txt);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        getSummaryFragCheck = getArguments().getBoolean("summary_frag");
    }
}