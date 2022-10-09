package com.example.toursimapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.toursimapp.Adapters.SummaryBookFragAdapter;
import com.example.toursimapp.AllActivities.Logsignactivity;
import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class Bookingfragment extends Fragment {

    TextView booking_reg_user_btn;
    private View guest_booking_container, main_booking_container, book_main_container_lay;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    private TabLayout sumary_booking_tablay;
    private ViewPager summary_booking_viewpager;
    private View bookfrag_loading_lay;

    public Bookingfragment() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        new Functions().checkTheme(requireContext());
        View v = inflater.inflate(R.layout.fragment_bookingfragment, container, false);

        bookingFragInits(v);
        summaryBookPagesFun();
        bookingFragEventListnerFuns();

        return v;
    }

    private void summaryBookPagesFun() {

        if (firebaseUser.isAnonymous()) {
            bookfrag_loading_lay.setVisibility(View.GONE);
            guest_booking_container.setVisibility(View.VISIBLE);
            main_booking_container.setVisibility(View.GONE);
            book_main_container_lay.setVisibility(View.GONE);
        } else {
            guest_booking_container.setVisibility(View.GONE);
            sumary_booking_tablay.addTab(sumary_booking_tablay.newTab().setText("Booked"));
            sumary_booking_tablay.addTab(sumary_booking_tablay.newTab().setText("Cancelled"));
            SummaryBookFragAdapter bookFragAdapter = new SummaryBookFragAdapter(getChildFragmentManager(), getContext(), sumary_booking_tablay.getTabCount());
            summary_booking_viewpager.setAdapter(bookFragAdapter);
            summary_booking_viewpager.setCurrentItem(0);

            sumary_booking_tablay.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    summary_booking_viewpager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            summary_booking_viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(sumary_booking_tablay));

            db.collection("users")
                    .document(firebaseUser.getUid())
                    .collection("Orders")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            bookfrag_loading_lay.setVisibility(View.GONE);
                            if (task.getResult() != null && task.getResult().size() > 0) {
                                main_booking_container.setVisibility(View.GONE);
                                book_main_container_lay.setVisibility(View.VISIBLE);
                            } else {
                                main_booking_container.setVisibility(View.VISIBLE);
                                book_main_container_lay.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    private void bookingFragEventListnerFuns() {

        booking_reg_user_btn.setOnClickListener(v -> {
            SharedPreferences.Editor editor = requireContext().getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
            editor.putBoolean("isUser", true);
            editor.apply();

            requireContext().startActivity(new Intent(getContext(), Logsignactivity.class));
        });
    }

    private void bookingFragInits(@NotNull View v) {
        guest_booking_container = v.findViewById(R.id.guest_booking_container);
        main_booking_container = v.findViewById(R.id.main_booking_container);
        sumary_booking_tablay = v.findViewById(R.id.sumary_booking_tablay);
        summary_booking_viewpager = v.findViewById(R.id.summary_booking_viewpager);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        booking_reg_user_btn = v.findViewById(R.id.booking_reg_user_btn);
        book_main_container_lay = v.findViewById(R.id.book_main_container_lay);
        bookfrag_loading_lay = v.findViewById(R.id.bookfrag_loading_lay);
        bookfrag_loading_lay.setVisibility(View.VISIBLE);
    }
}