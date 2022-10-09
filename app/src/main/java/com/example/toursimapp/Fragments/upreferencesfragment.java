package com.example.toursimapp.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.toursimapp.AllActivities.MainActivity;
import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class upreferencesfragment extends Fragment {

    private final ArrayList<String> mypref = new ArrayList<>();
    private CheckBox family_check, friends_check, honeymoon_check, religious_check, solo_check;
    private Button get_started;
    private String pref_cc, sign_pref_phone_txt, sign_pref_dob_txt, pref_gender_txt, sign_pref_add1_txt, sign_pref_add2_txt, sign_pref_add3_txt, sign_all_phn_txt;
    private Boolean isprefverified;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private String userid;
    private RelativeLayout pref_prog_lay;

    public upreferencesfragment() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        new Functions().checkTheme(getContext());
        View v = inflater.inflate(R.layout.fragment_upreferencesfragment, container, false);

        uPrefsFragInits(v);
        textValuesSet_basicInitMethod();
        uPrefsFragEventListnerMethod();

        return v;
    }

    private void uPrefsFragEventListnerMethod() {

        family_check.setOnClickListener(v16 -> {
            if (family_check.isChecked())
                mypref.add("Family");
            else
                mypref.remove("Family");
        });

        friends_check.setOnClickListener(v15 -> {
            if (friends_check.isChecked())
                mypref.add("Friends");
            else
                mypref.remove("Friends");
        });

        honeymoon_check.setOnClickListener(v17 -> {
            if (honeymoon_check.isChecked())
                mypref.add("Honeymoon");
            else
                mypref.remove("Honeymoon");
        });

        religious_check.setOnClickListener(v14 -> {
            if (religious_check.isChecked())
                mypref.add("Religious");
            else
                mypref.remove("Religious");
        });

        solo_check.setOnClickListener(v13 -> {
            if (solo_check.isChecked())
                mypref.add("Solo");
            else
                mypref.remove("Solo");
        });

        get_started.setOnClickListener(v1 -> {

            pref_prog_lay.setVisibility(View.VISIBLE);
            if (sign_pref_phone_txt == null) {
                sign_all_phn_txt = null;
            } else {
                sign_all_phn_txt = pref_cc + "-" + sign_pref_phone_txt;
            }

            Map<String, Object> alldata = new HashMap<>();
            alldata.put("contact_number", sign_all_phn_txt);
            alldata.put("dob", sign_pref_dob_txt);
            alldata.put("gender", pref_gender_txt);
            alldata.put("fulladdress", sign_pref_add1_txt);
            alldata.put("localarea", sign_pref_add2_txt);
            alldata.put("state_country", sign_pref_add3_txt);
            alldata.put("isverify", isprefverified);
            alldata.put("myprefs", mypref);

            userid = firebaseAuth.getCurrentUser().getUid();
            db.collection("users")
                    .document(userid)
                    .update(alldata)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Data Entered Successfully", Toast.LENGTH_SHORT).show();
                        SharedPreferences sign_prefs1 = requireContext().getSharedPreferences("sign_details", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor sign_edit = sign_prefs1.edit();
                        sign_edit.clear();
                        sign_edit.apply();
                        SharedPreferences.Editor editor = requireContext().getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
                        editor.putBoolean("sign_status", false);
                        editor.putBoolean("log_status", true);
                        editor.apply();

                        new Handler().postDelayed(() -> {
                            startActivity(new Intent(getContext(), MainActivity.class));
                            getActivity().finish();
                        }, 1000);
                    })
                    .addOnFailureListener(e -> {

                    });
        });

    }

    private void textValuesSet_basicInitMethod() {

        pref_prog_lay.setVisibility(View.GONE);
        SharedPreferences prefs_2 = requireContext().getSharedPreferences("sign_details", Activity.MODE_PRIVATE);
        pref_cc = prefs_2.getString("sign_coun_code_txt", "+91");
        sign_pref_phone_txt = prefs_2.getString("sign_ph_txt", null);
        sign_pref_dob_txt = prefs_2.getString("sign_dob_txt", null);
        pref_gender_txt = prefs_2.getString("sign_gen_txt", null);
        sign_pref_add1_txt = prefs_2.getString("sign_add1_txt", null);
        sign_pref_add2_txt = prefs_2.getString("sign_add2_txt", null);
        sign_pref_add3_txt = prefs_2.getString("sign_add3_txt", null);
        isprefverified = prefs_2.getBoolean("sign_email_verified", false);

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (mypref.size() > 0) {
                            get_started.setEnabled(true);
                            get_started.setTextColor(Color.WHITE);
                            get_started.setBackgroundResource(R.color.light_color);
                        } else {
                            get_started.setEnabled(false);
                            get_started.setTextColor(ColorStateList.valueOf(Color.parseColor("#B3B3B3")));
                            get_started.setBackgroundColor(Color.parseColor("#40000000"));
                        }
                    }
                });
            }
        }).start();

    }

    private void uPrefsFragInits(@NotNull View v) {
        family_check = v.findViewById(R.id.family_check);
        friends_check = v.findViewById(R.id.friends_check);
        honeymoon_check = v.findViewById(R.id.honeymoon_check);
        religious_check = v.findViewById(R.id.religious_check);
        solo_check = v.findViewById(R.id.solo_check);
        get_started = v.findViewById(R.id.get_started);
        pref_prog_lay = v.findViewById(R.id.pref_prog_lay);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }
}