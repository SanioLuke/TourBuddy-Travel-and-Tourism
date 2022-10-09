package com.example.toursimapp.AllActivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.toursimapp.Adapters.LogsignPageAdapter;
import com.example.toursimapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Logsignactivity extends AppCompatActivity {

    private ViewPager logsign_viewpager;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListner;
    private boolean isUser;
    private long backPressedTime;
    private View logsign_main_container;

    @Override
    protected void onStart() {
        super.onStart();
        if (!isUser) {
            firebaseAuth.addAuthStateListener(mAuthStateListner);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs_1 = getSharedPreferences("app_data", Activity.MODE_PRIVATE);
        isUser = prefs_1.getBoolean("isUser", false);

        if (!isUser) {
            verify();
        }
        setContentView(R.layout.activity_logsignactivity);
        logsign_main_container = findViewById(R.id.logsign_main_container);
        TextView login_page_header = findViewById(R.id.login_page_header);
        TabLayout logsign_tab = findViewById(R.id.logsign_tab);
        logsign_viewpager = findViewById(R.id.logsign_viewpager);
        firebaseAuth = FirebaseAuth.getInstance();

        if (isUser)
            login_page_header.setText("REGISTER NOW !");
        else
            login_page_header.setText("WELCOME");

        logsign_tab.addTab(logsign_tab.newTab().setText("          Login          "));
        logsign_tab.addTab(logsign_tab.newTab().setText("          SignUp          "));
        LogsignPageAdapter logsignPageAdapter = new LogsignPageAdapter(getSupportFragmentManager(), this, logsign_tab.getTabCount());
        logsign_viewpager.setAdapter(logsignPageAdapter);

        if (isUser) {
            logsign_viewpager.setCurrentItem(2);
        }

        logsign_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                logsign_viewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        logsign_viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(logsign_tab));

        logsign_tab.setTranslationX(800);
        float v = 0;
        logsign_tab.setAlpha(v);
        logsign_tab.animate().translationX(0).alpha(1).setDuration(1500).setStartDelay(500).start();

    }

    private void verify() {

        mAuthStateListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseuser = firebaseAuth.getCurrentUser();
                if (mFirebaseuser != null) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!isUser) {
            finishAffinity();
        } else {
            SharedPreferences.Editor editor = getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
            editor.putBoolean("isUser", false);
            editor.apply();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
        editor.putBoolean("isUser", false);
        editor.apply();

    }
}