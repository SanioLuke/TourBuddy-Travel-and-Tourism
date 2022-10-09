package com.example.toursimapp.AllActivities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ContactActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mapAPI;
    private SupportMapFragment mapFragment;
    private ImageButton contact_back_btn;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Functions().checkTheme(getApplicationContext());
        setContentView(R.layout.activity_contact);

        contact_back_btn = findViewById(R.id.contact_back_btn);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googlemap_api);

        contact_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapAPI = googleMap;
        LatLng mappoints = new LatLng(19.204396, 73.109336);
        mapAPI.addMarker(new MarkerOptions().position(mappoints).title("Dombivili"));
        mapAPI.moveCamera(CameraUpdateFactory.newLatLngZoom(mappoints, 10F));

    }
}