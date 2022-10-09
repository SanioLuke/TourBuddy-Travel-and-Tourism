package com.example.toursimapp.AllActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.R;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class Splashscreen extends AppCompatActivity {

    CircleImageView logo_img;
    private Boolean intro_check, log_check, sign_status;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Functions().checkTheme(getApplicationContext());
        setContentView(R.layout.activity_splashscreen);
        firebaseAuth = FirebaseAuth.getInstance();

        Drawable background = ContextCompat.getDrawable(getApplicationContext(), R.drawable.bluegradient);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
        getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
        getWindow().setBackgroundDrawable(background);

        if (firebaseAuth.getCurrentUser() != null) {
            SharedPreferences.Editor editor = getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
            editor.putBoolean("isUser", false);
            editor.apply();
        }

        SharedPreferences prefs_1 = getSharedPreferences("app_data", Activity.MODE_PRIVATE);
        intro_check = prefs_1.getBoolean("intro", false);
        log_check = prefs_1.getBoolean("log_status", false);
        sign_status = prefs_1.getBoolean("sign_status", false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (intro_check) {

                    if (log_check) {
                        Log.e("splash_to_page", "To Mainactivity Page");
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else if (sign_status) {
                        Log.e("splash_to_page", "To Enterdetails Page");
                        startActivity(new Intent(getApplicationContext(), Enterdetails.class));
                        finish();
                    } else {
                        Log.e("splash_to_page", "To Logsignactivity Page");
                        startActivity(new Intent(getApplicationContext(), Logsignactivity.class));
                        finish();
                    }
                } else {
                    Log.e("splash_to_page", "To Introsliders Page");
                    startActivity(new Intent(getApplicationContext(), Introsliders.class));
                    finish();
                }
            }
        }, 1500);
    }
}