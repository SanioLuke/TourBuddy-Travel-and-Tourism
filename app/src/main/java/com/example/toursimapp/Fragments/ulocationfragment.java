package com.example.toursimapp.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class ulocationfragment extends Fragment implements LocationListener {

    private TextView ulocation_skip_btn;
    private TextInputLayout sign_add1, sign_add2, sign_add3;
    private String sign_add1_txt, sign_add2_txt, sign_add3_txt;
    private final TextWatcher editextwactcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            sign_add1_txt = sign_add1.getEditText().getText().toString();
            sign_add2_txt = sign_add2.getEditText().getText().toString();
            sign_add3_txt = sign_add3.getEditText().getText().toString();

            ul_next.setEnabled(!sign_add1_txt.isEmpty() && !sign_add2_txt.isEmpty() && !sign_add3_txt.isEmpty());

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private Button search_loc_btn;
    private Button ul_prev, ul_next;
    private ProgressBar ul_prog;

    public ulocationfragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        new Functions().checkTheme(getContext());
        View v = inflater.inflate(R.layout.fragment_ulocationfragment, container, false);

        ulocationInits(v);
        textValuesSetMethods();
        addTextWactcherMethods();
        uLocationFragEventListnerMethod();

        return v;
    }

    private void uLocationFragEventListnerMethod() {

        SharedPreferences prefs_2 = requireContext().getSharedPreferences("app_data", Activity.MODE_PRIVATE);
        boolean isGoogle = prefs_2.getBoolean("isGoogle", false);

        search_loc_btn.setOnClickListener(v -> {
            grantpermission();
            new Handler().postDelayed(() -> {
                ul_prog.setVisibility(View.VISIBLE);
                getLocations();
                CheckLocationEnabledOrNot();
            }, 3000);
        });

        ul_prev.setOnClickListener(v -> {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            transaction.replace(R.id.details_container, new userdetailsfragment());
            transaction.commit();
        });

        ul_next.setOnClickListener(v -> {

            sign_add1_txt = sign_add1.getEditText().getText().toString();
            sign_add2_txt = sign_add2.getEditText().getText().toString();
            sign_add3_txt = sign_add3.getEditText().getText().toString();

            SharedPreferences.Editor editor = getContext().getSharedPreferences("sign_details", Context.MODE_PRIVATE).edit();
            editor.putString("sign_add1_txt", sign_add1_txt);
            editor.putString("sign_add2_txt", sign_add2_txt);
            editor.putString("sign_add3_txt", sign_add3_txt);
            editor.putString("sign_page_name", "uemail");
            editor.apply();

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            if (isGoogle) {
                SharedPreferences.Editor editor1 = getContext().getSharedPreferences("sign_details", Context.MODE_PRIVATE).edit();
                editor1.putBoolean("sign_email_verified", true);
                editor1.apply();

                transaction.replace(R.id.details_container, new upreferencesfragment());
            } else {
                transaction.replace(R.id.details_container, new uemailverifyfragment());
            }
            transaction.commit();

        });

        ulocation_skip_btn.setOnClickListener(v -> {
            SharedPreferences.Editor editor = getContext().getSharedPreferences("sign_details", Context.MODE_PRIVATE).edit();
            editor.putString("sign_page_name", "uemail");
            editor.apply();

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);

            if (isGoogle) {
                SharedPreferences.Editor editor1 = getContext().getSharedPreferences("sign_details", Context.MODE_PRIVATE).edit();
                editor1.putBoolean("sign_email_verified", true);
                editor1.apply();

                transaction.replace(R.id.details_container, new upreferencesfragment());
            } else {
                transaction.replace(R.id.details_container, new uemailverifyfragment());
            }

            transaction.commit();
        });
    }

    private void addTextWactcherMethods() {
        if (sign_add1.getEditText() != null && sign_add2.getEditText() != null && sign_add3.getEditText() != null) {
            sign_add1.getEditText().addTextChangedListener(editextwactcher);
            sign_add2.getEditText().addTextChangedListener(editextwactcher);
            sign_add3.getEditText().addTextChangedListener(editextwactcher);
        }
    }

    private void textValuesSetMethods() {

        SharedPreferences prefs_2 = requireContext().getSharedPreferences("sign_details", Activity.MODE_PRIVATE);
        String sign_pref_add1_txt = prefs_2.getString("sign_add1_txt", null);
        String sign_pref_add2_txt = prefs_2.getString("sign_add2_txt", null);
        String sign_pref_add3_txt = prefs_2.getString("sign_add3_txt", null);

        if (sign_pref_add1_txt != null && sign_pref_add2_txt != null && sign_pref_add3_txt != null) {
            ul_next.setEnabled(true);
        }
        if (sign_pref_add1_txt != null) {
            sign_add1.getEditText().setText(sign_pref_add1_txt);
        }
        if (sign_pref_add2_txt != null) {
            sign_add2.getEditText().setText(sign_pref_add2_txt);
        }
        if (sign_pref_add3_txt != null) {
            sign_add3.getEditText().setText(sign_pref_add3_txt);
        }
    }

    private void ulocationInits(@NotNull View v) {
        ulocation_skip_btn = v.findViewById(R.id.ulocation_skip_btn);
        ul_prev = v.findViewById(R.id.ul_prev);
        ul_next = v.findViewById(R.id.ul_next);
        sign_add1 = v.findViewById(R.id.sign_add1);
        sign_add2 = v.findViewById(R.id.sign_add2);
        sign_add3 = v.findViewById(R.id.sign_add3);
        search_loc_btn = v.findViewById(R.id.search_loc_btn);
        ul_prog = v.findViewById(R.id.ul_prog);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void getLocations() {
        try {
            LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void CheckLocationEnabledOrNot() {
        LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gpsenabled = false;
        boolean networkenabled = false;

        try {
            gpsenabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            networkenabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!gpsenabled && !networkenabled) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Enable GPS Service")
                    .setCancelable(false)
                    .setPositiveButton("Enable", (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))).setNegativeButton("Cancel", null)
                    .show();
        }
    }

    private void grantpermission() {
        if ((ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLocationChanged(Location location) {

        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            ul_prog.setVisibility(View.GONE);
            if (addresses.get(0).getAddressLine(0) == null) {
                sign_add1.getEditText().setHint("Unavailable. Enter your address.");
            } else {
                sign_add1.getEditText().setText(addresses.get(0).getAddressLine(0));
            }

            if (addresses.get(0).getLocality() == null || addresses.get(0).getPostalCode() == null) {
                if (addresses.get(0).getLocality() == null) {
                    sign_add2.getEditText().setText(addresses.get(0).getPostalCode());
                } else if (addresses.get(0).getPostalCode() == null) {
                    sign_add2.getEditText().setText(addresses.get(0).getLocality());
                } else {
                    sign_add2.setHint("Unavailable. Enter your area & code");
                }
            } else {
                sign_add2.getEditText().setText(addresses.get(0).getLocality() + " - " + addresses.get(0).getPostalCode());
            }

            if (addresses.get(0).getAdminArea() == null || addresses.get(0).getCountryName() == null) {
                if (addresses.get(0).getAdminArea() == null) {
                    sign_add3.getEditText().setText(addresses.get(0).getCountryName());
                } else if (addresses.get(0).getCountryName() == null) {
                    sign_add3.getEditText().setText(addresses.get(0).getAdminArea());
                } else {
                    sign_add3.setHint("Unavailable. Enter state & country.");
                }
            } else {
                sign_add3.getEditText().setText(addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}