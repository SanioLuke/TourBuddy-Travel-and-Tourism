package com.example.toursimapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class uemailverifyfragment extends Fragment {

    private final Handler ue_handler = new Handler();
    private TextView uemail_skip_btn, verifysuccess_txt;
    private Button verify_email_btn, ue_next, ue_prev, retry_verify_btn, check_verify;
    private FirebaseAuth firebaseAuth;
    private ProgressBar ue_prog;
    private FirebaseUser firebaseUser;

    public uemailverifyfragment() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        new Functions().checkTheme(getContext());
        View v = inflater.inflate(R.layout.fragment_uemailverifyfragment, container, false);

        uemailFragInits(v);
        uemail_textValuesSetMethods();
        uemailFragEventListnerMethods();

        return v;
    }

    @SuppressLint("SetTextI18n")
    private void uemailFragEventListnerMethods() {

        ue_prev.setOnClickListener(v -> {

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            transaction.replace(R.id.details_container, new ulocationfragment());
            transaction.commit();
        });

        ue_next.setOnClickListener(v -> {

            SharedPreferences.Editor editor = getContext().getSharedPreferences("sign_details", Context.MODE_PRIVATE).edit();
            editor.putBoolean("emailverify", true);
            editor.putString("sign_page_name", "upreference");
            editor.apply();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            transaction.replace(R.id.details_container, new upreferencesfragment());
            transaction.commit();
        });

        uemail_skip_btn.setOnClickListener(v -> {
            SharedPreferences.Editor editor = getContext().getSharedPreferences("sign_details", Context.MODE_PRIVATE).edit();
            editor.putBoolean("emailverify", false);
            editor.putString("sign_page_name", "upreference");
            editor.apply();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            transaction.replace(R.id.details_container, new upreferencesfragment());
            transaction.commit();
        });

        verify_email_btn.setOnClickListener(v -> {

            verify_email_btn.setVisibility(View.INVISIBLE);
            ue_prog.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = getContext().getSharedPreferences("sign_details", Context.MODE_PRIVATE).edit();
            editor.putBoolean("main_verified_clicked", true);
            editor.apply();

            Objects.requireNonNull(firebaseAuth.getCurrentUser()).reload();
            firebaseUser = firebaseAuth.getCurrentUser();
            assert firebaseUser != null;

            firebaseUser.sendEmailVerification()
                    .addOnSuccessListener(aVoid -> {
                        ue_prog.setVisibility(View.GONE);
                        verifysuccess_txt.setVisibility(View.VISIBLE);
                        verifysuccess_txt.setText("Verification mail send. Please check your mail. And you have verified, click on the below button to check");
                        verifysuccess_txt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_access_time, 0, 0, 0);
                        retry_verify_btn.setVisibility(View.GONE);
                        check_verify.setVisibility(View.VISIBLE);
                    })
                    .addOnFailureListener(e -> {

                        ue_prog.setVisibility(View.GONE);
                        verifysuccess_txt.setVisibility(View.VISIBLE);
                        verify_email_btn.setText("Verification mail not send. Please try again!!");
                        verifysuccess_txt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close, 0, 0, 0);
                        check_verify.setVisibility(View.GONE);
                        retry_verify_btn.setVisibility(View.VISIBLE);
                    });
        });

        retry_verify_btn.setOnClickListener(v -> {

            retry_verify_btn.setVisibility(View.GONE);
            check_verify.setVisibility(View.GONE);
            ue_prog.setVisibility(View.VISIBLE);
            verifysuccess_txt.setVisibility(View.GONE);

            if (firebaseUser.isEmailVerified()) {
                verifysuccess_txt.setVisibility(View.VISIBLE);
                verifysuccess_txt.setText("Email verification already done");
                verifysuccess_txt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
                ue_next.setEnabled(true);
                retry_verify_btn.setVisibility(View.GONE);
                ue_prog.setVisibility(View.GONE);
                check_verify.setVisibility(View.GONE);
            } else {
                Objects.requireNonNull(firebaseAuth.getCurrentUser()).reload();
                firebaseUser = firebaseAuth.getCurrentUser();
                assert firebaseUser != null;

                firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ue_prog.setVisibility(View.GONE);
                                verifysuccess_txt.setVisibility(View.VISIBLE);
                                verifysuccess_txt.setText("Verification mail send. Please check your mail. And you have verified, click on the below button to check");
                                verifysuccess_txt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_access_time, 0, 0, 0);
                                retry_verify_btn.setVisibility(View.GONE);
                                check_verify.setVisibility(View.VISIBLE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                ue_prog.setVisibility(View.GONE);
                                verifysuccess_txt.setVisibility(View.VISIBLE);
                                verify_email_btn.setText("Verification mail not send. Please try again!!");
                                verifysuccess_txt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close, 0, 0, 0);
                                check_verify.setVisibility(View.GONE);
                                retry_verify_btn.setVisibility(View.VISIBLE);
                            }
                        });
            }
        });

        check_verify.setOnClickListener(v -> {
            Objects.requireNonNull(firebaseAuth.getCurrentUser()).reload();
            retry_verify_btn.setVisibility(View.GONE);
            check_verify.setVisibility(View.GONE);
            ue_prog.setVisibility(View.VISIBLE);
            verifysuccess_txt.setVisibility(View.GONE);

            if (firebaseUser.isEmailVerified()) {
                SharedPreferences.Editor editor = getContext().getSharedPreferences("sign_details", Context.MODE_PRIVATE).edit();
                editor.putBoolean("sign_email_verified", true);
                editor.apply();
                verifysuccess_txt.setVisibility(View.VISIBLE);
                verifysuccess_txt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
                verifysuccess_txt.setText("Email Verified Successfully.");
                ue_next.setEnabled(true);
                ue_prog.setVisibility(View.GONE);
                retry_verify_btn.setVisibility(View.GONE);
                check_verify.setVisibility(View.GONE);
            } else {
                SharedPreferences.Editor editor = getContext().getSharedPreferences("sign_details", Context.MODE_PRIVATE).edit();
                editor.putBoolean("sign_email_verified", false);
                editor.apply();
                verifysuccess_txt.setVisibility(View.VISIBLE);
                verifysuccess_txt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close, 0, 0, 0);
                verifysuccess_txt.setText("Email verification not done. Complete it");
                ue_next.setEnabled(false);
                ue_prog.setVisibility(View.GONE);
                retry_verify_btn.setVisibility(View.VISIBLE);
                check_verify.setVisibility(View.GONE);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void uemail_textValuesSetMethods() {
        SharedPreferences prefs_2 = requireContext().getSharedPreferences("sign_details", Activity.MODE_PRIVATE);
        boolean main_verified_clicked = prefs_2.getBoolean("main_verified_clicked", false);
        boolean isprefverified = prefs_2.getBoolean("sign_email_verified", false);

        if (main_verified_clicked) {
            if (isprefverified) {
                verifysuccess_txt.setVisibility(View.VISIBLE);
                verifysuccess_txt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
                verifysuccess_txt.setText("Email Verified Successfully.");
                ue_next.setEnabled(isprefverified);
                verify_email_btn.setVisibility(View.INVISIBLE);
                ue_prog.setVisibility(View.GONE);
                retry_verify_btn.setVisibility(View.GONE);
                check_verify.setVisibility(View.GONE);
            } else {
                verifysuccess_txt.setVisibility(View.VISIBLE);
                verifysuccess_txt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close, 0, 0, 0);
                verifysuccess_txt.setText("Email verification not done. Complete it");
                ue_next.setEnabled(isprefverified);
                verify_email_btn.setVisibility(View.INVISIBLE);
                ue_prog.setVisibility(View.GONE);
                retry_verify_btn.setVisibility(View.VISIBLE);
                check_verify.setVisibility(View.GONE);
            }
        }
    }

    private void uemailFragInits(@NotNull View v) {
        uemail_skip_btn = v.findViewById(R.id.uemail_skip_btn);
        verify_email_btn = v.findViewById(R.id.verify_email_btn);
        ue_next = v.findViewById(R.id.ue_next);
        ue_prev = v.findViewById(R.id.ue_prev);
        ue_prog = v.findViewById(R.id.ue_prog);
        check_verify = v.findViewById(R.id.check_verify);
        retry_verify_btn = v.findViewById(R.id.retry_verify_btn);
        verifysuccess_txt = v.findViewById(R.id.verifysuccess_txt);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

}