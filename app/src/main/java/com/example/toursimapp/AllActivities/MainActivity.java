package com.example.toursimapp.AllActivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.toursimapp.Adapters.ConnectivityReceiver;
import com.example.toursimapp.Fragments.Bookingfragment;
import com.example.toursimapp.Fragments.Favfragment;
import com.example.toursimapp.Fragments.Homefragment;
import com.example.toursimapp.Fragments.Inboxfragment;
import com.example.toursimapp.Fragments.Profilefragment;
import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListner {

    private final Functions functions = new Functions();
    private ChipNavigationBar bottom_home_navi;
    @SuppressLint("NonConstantResourceId")
    private final ChipNavigationBar.OnItemSelectedListener bottom_navlistner = i -> {
        Fragment selectedfragment = null;

        switch (i) {
            case R.id.btm_home:
                selectedfragment = new Homefragment();
                functions.darkstatusbardesign(MainActivity.this);
                break;

            case R.id.btm_favourites:
                selectedfragment = new Favfragment();
                functions.lightstatusbardesign(MainActivity.this);
                break;

            case R.id.btm_booking:
                selectedfragment = new Bookingfragment();
                functions.lightstatusbardesign(MainActivity.this);
                break;

            case R.id.btm_inbox:
                selectedfragment = new Inboxfragment(bottom_home_navi);
                functions.lightstatusbardesign(MainActivity.this);
                break;

            case R.id.btm_profile:
                selectedfragment = new Profilefragment();
                functions.darkstatusbardesign(MainActivity.this);
                break;

            default:
                break;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, selectedfragment).commit();

    };
    private long backPressedTime;
    private View mainpage_container_lay;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        setContentView(R.layout.activity_main);

        SharedPreferences prefs_1 = getSharedPreferences("app_data", Activity.MODE_PRIVATE);
        boolean check_new = prefs_1.getBoolean("start_up_news", true);
        new Handler().postDelayed(() -> {
            if (check_new) {
                new Functions().whats_new_dialog(MainActivity.this, 2);
            }
        }, 1500);


        mainpage_container_lay = findViewById(R.id.mainpage_container_lay);
        bottom_home_navi = findViewById(R.id.bottom_home_navi);
        bottom_home_navi.setOnItemSelectedListener(bottom_navlistner);
        db = FirebaseFirestore.getInstance();

        userDataSharedPrefStore();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                functions.getDbNoti(bottom_home_navi);
            }
        }, 1000);

        checkInternetConnection();

        Intent frompage = getIntent();
        String frompageString = frompage.getStringExtra("from_page");
        Log.d("frompage", "" + frompageString);

        if (firebaseUser.isAnonymous()) {
            bottom_home_navi.removeView(findViewById(R.id.btm_inbox));
        }

        if (frompageString != null) {
            if (frompageString.equals("AccountsettingsActivity")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new Profilefragment()).commit();
                bottom_home_navi.setItemSelected(R.id.btm_profile, true);
            }
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new Homefragment()).commit();
            bottom_home_navi.setItemSelected(R.id.btm_home, true);
        }
    }

    private void userDataSharedPrefStore() {

        String user_name;
        String collection_name;

        user_name = "fullname";
        collection_name = "users";

        db.collection(collection_name)
                .document(firebaseUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.getResult().exists() && task.getResult() != null) {

                        Log.e("user_data", "User data stored");
                        long timestramp = firebaseAuth.getCurrentUser().getMetadata().getCreationTimestamp();
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM, y");
                        String createddate = simpleDateFormat.format(timestramp);

                        if (firebaseUser.isAnonymous()) {
                            SharedPreferences.Editor editor = getSharedPreferences("user_data", Context.MODE_PRIVATE).edit();
                            editor.putString("fullname", task.getResult().getString(user_name));
                            editor.putString("createdate", createddate);
                            editor.apply();
                        } else {
                            SharedPreferences.Editor editor = getSharedPreferences("user_data", Context.MODE_PRIVATE).edit();
                            editor.putString("fullname", task.getResult().getString(user_name));
                            editor.putString("fulladdress", task.getResult().getString("fulladdress"));
                            editor.putString("gender", task.getResult().getString("gender"));
                            editor.putString("state_country", task.getResult().getString("state_country"));
                            editor.putString("contact_number", task.getResult().getString("contact_number"));
                            editor.putString("emailid", task.getResult().getString("emailid"));
                            editor.putString("createdate", createddate);
                            editor.apply();
                        }
                    } else {
                        Log.e("user_data", "User data not stored because of null values !!");
                    }
                });
    }

    private void checkInternetConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        no_net_goto(isConnected);
    }

    private void no_net_goto(boolean isConnected) {
        if (!isConnected) {
            startActivity(new Intent(this, Noconnection.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);

        MyApp.getInstance().setConnectivityListner(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        no_net_goto(isConnected);
    }

    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity();
        } else {
            Snackbar.make(mainpage_container_lay, "Press back again to Exit", Snackbar.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();

    }
}