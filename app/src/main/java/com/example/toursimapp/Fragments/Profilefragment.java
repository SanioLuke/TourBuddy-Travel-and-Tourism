package com.example.toursimapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.toursimapp.AllActivities.AboutusActivity;
import com.example.toursimapp.AllActivities.AccountsettingsActivity;
import com.example.toursimapp.AllActivities.ContactActivity;
import com.example.toursimapp.AllActivities.Logsignactivity;
import com.example.toursimapp.AllActivities.MainSettingsActivity;
import com.example.toursimapp.AllActivities.ProfilesettingsActivity;
import com.example.toursimapp.AllActivities.TermsCondnActivity;
import com.example.toursimapp.LocalDB.AnalyticDBHelper;
import com.example.toursimapp.LocalDB.RecentPlaceDBHelper;
import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class Profilefragment extends Fragment {

    private TextView prof_username, prof_joined, prof_reg_user_btn;
    private Button logout;
    private LinearLayout prof_get_registered_lay, prof_person_settings, prof_acc_settings, prof_app_settings, prof_aboutus, prof_contactus, prof_feedback, prof_privacy;
    private LinearLayout info_settings;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private GoogleSignInClient googleSignInClient;

    public Profilefragment() {
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        new Functions().checkTheme(requireContext());
        View view = inflater.inflate(R.layout.fragment_profilefragment, container, false);

        profileFragInits(view);
        uname_jdateMethod();  //Username & joined Date
        profileFragEventListnerMethods();

        return view;
    }

    private void profileFragInits(@NotNull View view) {

        prof_get_registered_lay = view.findViewById(R.id.prof_get_registered_lay);
        prof_username = view.findViewById(R.id.prof_username);
        prof_joined = view.findViewById(R.id.prof_joined);
        info_settings = view.findViewById(R.id.info_settings);
        prof_person_settings = view.findViewById(R.id.prof_person_settings);
        prof_acc_settings = view.findViewById(R.id.prof_acc_settings);
        prof_app_settings = view.findViewById(R.id.prof_app_settings);
        prof_aboutus = view.findViewById(R.id.prof_aboutus);
        prof_contactus = view.findViewById(R.id.prof_contactus);
        prof_feedback = view.findViewById(R.id.prof_feedback);
        prof_privacy = view.findViewById(R.id.prof_privacy);
        logout = view.findViewById(R.id.logout);
        prof_reg_user_btn = view.findViewById(R.id.prof_reg_user_btn);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void uname_jdateMethod() {

        Animation bounce_prof_get_registered_lay = AnimationUtils.loadAnimation(getContext(), R.anim.bounce_anim);
        prof_get_registered_lay.startAnimation(bounce_prof_get_registered_lay);
        if (firebaseUser.isAnonymous()) {
            info_settings.setVisibility(View.GONE);
            prof_get_registered_lay.setVisibility(View.VISIBLE);
        }

        SharedPreferences get_user_data_prefs = requireContext().getSharedPreferences("user_data", Activity.MODE_PRIVATE);
        String username = get_user_data_prefs.getString("fullname", "User");
        String createdate = get_user_data_prefs.getString("createdate", "this year");

        prof_username.setText(MessageFormat.format("Hello, {0}", username));
        prof_joined.setText(MessageFormat.format("Joined in {0}", createdate));

    }

    private void profileFragEventListnerMethods() {

        prof_app_settings.setOnClickListener(v -> startActivity(new Intent(getContext(), MainSettingsActivity.class)));

        logout.setOnClickListener(v -> {

            if (firebaseUser != null) {
                if (firebaseUser.isAnonymous()) {

                    SharedPreferences.Editor editor = requireContext().getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("log_status", false);
                    editor.putBoolean("isUser", false);
                    editor.putBoolean("isGoogle", false);
                    editor.apply();

                    db.collection("users")
                            .document(firebaseUser.getUid())
                            .collection("favourites")
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.getResult() != null) {
                                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                        documentSnapshot.getReference().delete();
                                    }
                                }
                            });

                    new Handler().postDelayed(() -> db.collection("users")
                            .document(firebaseUser.getUid())
                            .delete()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {

                                    SharedPreferences.Editor user_prefs1 = requireContext().getSharedPreferences("user_data", Activity.MODE_PRIVATE).edit();
                                    user_prefs1.clear();
                                    user_prefs1.apply();

                                    verify();
                                    firebaseAuth.signOut();
                                    firebaseUser.delete();
                                    new RecentPlaceDBHelper(getContext()).deleteRecentPlace();
                                    new AnalyticDBHelper(getContext()).deleteAllRecentPlaces();
                                    startActivity(new Intent(getContext(), Logsignactivity.class));
                                } else {
                                    Toast.makeText(getContext(), "Failed to Sign-out...!!", Toast.LENGTH_SHORT).show();
                                }
                            }), 500);
                } else {

                    SharedPreferences.Editor editor = requireContext().getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("log_status", false);
                    editor.putBoolean("isGoogle", false);
                    editor.apply();

                    verify();
                    firebaseAuth.signOut();
                    creategooglerequest();
                    googleSignInClient.signOut();
                    new RecentPlaceDBHelper(getContext()).deleteRecentPlace();
                    new AnalyticDBHelper(getContext()).deleteAllRecentPlaces();
                    startActivity(new Intent(getContext(), Logsignactivity.class));
                }
            }
        });

        prof_reg_user_btn.setOnClickListener(v -> {

            SharedPreferences.Editor editor = requireContext().getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
            editor.putBoolean("isUser", true);
            editor.apply();
            requireContext().startActivity(new Intent(getContext(), Logsignactivity.class));
        });

        prof_get_registered_lay.setOnClickListener(v -> {
            SharedPreferences.Editor editor = requireContext().getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
            editor.putBoolean("isUser", true);
            editor.apply();

            requireContext().startActivity(new Intent(getContext(), Logsignactivity.class));
        });

        prof_person_settings.setOnClickListener(v -> startActivity(new Intent(getContext(), ProfilesettingsActivity.class)));

        prof_acc_settings.setOnClickListener(v -> startActivity(new Intent(getContext(), AccountsettingsActivity.class)));

        prof_privacy.setOnClickListener(v -> startActivity(new Intent(getContext(), TermsCondnActivity.class)));

        prof_feedback.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(getActivity());
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

                db.collection("user_feedback").document(firebaseUser.getUid())
                        .set(map).addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Feedback Sent..", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to send Feedback !! Please Try Again", Toast.LENGTH_SHORT).show());

            });

            dialog.show();
        });

        prof_aboutus.setOnClickListener(v -> startActivity(new Intent(getContext(), AboutusActivity.class)));

        prof_contactus.setOnClickListener(v -> startActivity(new Intent(getContext(), ContactActivity.class)));

    }

    private void verify() {
        FirebaseAuth.AuthStateListener mAuthStateListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseuser = firebaseAuth.getCurrentUser();
                if (mFirebaseuser == null) {
                    Toast.makeText(getContext(), "You have already logged out", Toast.LENGTH_SHORT).show();

                    Intent in7 = new Intent(getContext(), Logsignactivity.class);
                    startActivity(in7);
                }
            }
        };
    }

    private void creategooglerequest() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions);
    }
}