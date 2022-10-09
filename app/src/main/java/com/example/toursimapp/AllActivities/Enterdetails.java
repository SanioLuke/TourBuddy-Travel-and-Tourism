package com.example.toursimapp.AllActivities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.toursimapp.Fragments.uemailverifyfragment;
import com.example.toursimapp.Fragments.ulocationfragment;
import com.example.toursimapp.Fragments.upreferencesfragment;
import com.example.toursimapp.Fragments.userdetailsfragment;
import com.example.toursimapp.R;
import com.google.android.material.snackbar.Snackbar;

public class Enterdetails extends AppCompatActivity {

    private RelativeLayout details_container;
    private String sign_page_name;
    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_enterdetails);

        SharedPreferences prefs_2 = getSharedPreferences("app_data", Activity.MODE_PRIVATE);
        sign_page_name = prefs_2.getString("sign_page_name", null);

        details_container = findViewById(R.id.details_container);
        FragmentManager fm = getSupportFragmentManager();

        if (sign_page_name == null) {
            fm.beginTransaction().add(R.id.details_container, new userdetailsfragment()).commit();
        } else if (sign_page_name.equals("ulocation")) {
            fm.beginTransaction().add(R.id.details_container, new ulocationfragment()).commit();
        } else if (sign_page_name.equals("uemail")) {
            fm.beginTransaction().add(R.id.details_container, new uemailverifyfragment()).commit();
        } else if (sign_page_name.equals("upreference")) {
            fm.beginTransaction().add(R.id.details_container, new upreferencesfragment()).commit();
        } else {
            fm.beginTransaction().add(R.id.details_container, new userdetailsfragment()).commit();
        }
    }

    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()) {

            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (count == 0) {
                super.onBackPressed();
                finishAffinity();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        } else {
            Snackbar.make(details_container, "Press back again to Exit", Snackbar.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}