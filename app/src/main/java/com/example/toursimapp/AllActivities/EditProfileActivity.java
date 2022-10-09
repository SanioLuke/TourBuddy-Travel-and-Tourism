package com.example.toursimapp.AllActivities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.toursimapp.Fragments.GenderDialogFragment;
import com.example.toursimapp.Fragments.PrefDialogFragment;
import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity implements LocationListener, GenderDialogFragment.SingleChoiceListner, PrefDialogFragment.onMultiChoiceListner {

    private final ArrayList<String> all_prefs = new ArrayList<>();
    ArrayList<String> selected_prefs_array;
    private ImageButton editprofile_back_btn;
    private EditText editprofile_full_name, editprofile_gender, editprofile_dob, editprofile_contact_no, editprofile_add1, editprofile_add2, editprofile_add3, editprofile_yourprefs;
    private CountryCodePicker editprofile_contact_ccode;
    private Button editprofile_save_changes;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    private Button editprofile_getloctn, editprofile_add_pres_btn;
    private ProgressBar editprofile_add_prog;
    private String selected_prefs_string;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Functions().lightstatusbardesign(EditProfileActivity.this);
        new Functions().checkTheme(getApplicationContext());
        setContentView(R.layout.activity_edit_profile);

        {
            editprofile_back_btn = findViewById(R.id.editprofile_back_btn);
            editprofile_full_name = findViewById(R.id.editprofile_full_name);
            editprofile_gender = findViewById(R.id.editprofile_gender);
            editprofile_dob = findViewById(R.id.editprofile_dob);
            editprofile_contact_ccode = findViewById(R.id.editprofile_contact_ccode);
            editprofile_contact_no = findViewById(R.id.editprofile_contact_no);
            editprofile_add1 = findViewById(R.id.editprofile_add1);
            editprofile_add2 = findViewById(R.id.editprofile_add2);
            editprofile_add3 = findViewById(R.id.editprofile_add3);
            editprofile_add_pres_btn = findViewById(R.id.editprofile_add_pres_btn);
            editprofile_yourprefs = findViewById(R.id.editprofile_yourprefs);
            editprofile_save_changes = findViewById(R.id.editprofile_save_changes);
            editprofile_getloctn = findViewById(R.id.editprofile_getloctn);
            editprofile_add_prog = findViewById(R.id.editprofile_add_prog);

            firebaseAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();

            all_prefs.add("Family");
            all_prefs.add("Friends");
            all_prefs.add("Honeymoon");
            all_prefs.add("Religious");
            all_prefs.add("Solo");

            editprofile_contact_ccode.registerCarrierNumberEditText(editprofile_contact_no);

        }  // Initializations

        {
            if (!firebaseUser.isAnonymous()) {
                db.collection("users")
                        .document(firebaseUser.getUid())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot != null) {
                                    editprofile_full_name.setText(documentSnapshot.getString("fullname"));
                                    editprofile_gender.setText(documentSnapshot.getString("gender"));
                                    editprofile_dob.setText(documentSnapshot.getString("dob"));
                                    editprofile_add1.setText(documentSnapshot.getString("fulladdress"));
                                    editprofile_add2.setText(documentSnapshot.getString("localarea"));
                                    editprofile_add3.setText(documentSnapshot.getString("state_country"));

                                    if (documentSnapshot.getString("contact_number") != null) {
                                        seperate_cc_number(documentSnapshot.getString("contact_number"));
                                    }
                                    if (documentSnapshot.get("myprefs") != null) {
                                        ArrayList<String> pref_array = (ArrayList<String>) documentSnapshot.get("myprefs");
                                        addProfilePrefs(pref_array);
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditProfileActivity.this, "Unable to retrieve data " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }

        } // Retrieve Data from Firebase

        editprofile_add_pres_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences prefs_1 = getSharedPreferences("app_data", Activity.MODE_PRIVATE);
                selected_prefs_string = prefs_1.getString("prefs_string", null);

                if (selected_prefs_string != null) {
                    selected_prefs_array = new ArrayList<String>(Arrays.asList(selected_prefs_string.substring(0, selected_prefs_string.length() - 2).split(", ")));
                }

                ArrayList<Boolean> stockList = new ArrayList<>();
                for (int m = 0; m < all_prefs.size(); m++) {
                    for (int n = 0; n < selected_prefs_array.size(); n++) {
                        if (all_prefs.get(m).equals(selected_prefs_array.get(n))) {
                            stockList.add(m, true);
                            break;
                        } else {
                            stockList.add(m, null);
                        }
                    }
                    if (stockList.get(m) == null) {
                        stockList.add(m, false);
                    }
                }
                stockList.removeAll(Collections.singleton(null));

                Boolean[] stockArr = new Boolean[stockList.size()];
                boolean[] stocklis = new boolean[stockList.size()];
                stockArr = stockList.toArray(stockArr);
                for (int ch = 0; ch < stockArr.length; ch++) {
                    stocklis[ch] = stockArr[ch];
                }

                for (int d = 0; d < stockArr.length; d++) {
                    Log.d("ss_array", "Hello : " + stockArr[d] + " Size : " + d);
                }

                DialogFragment prefschoicedialog = new PrefDialogFragment(stocklis, selected_prefs_array);
                prefschoicedialog.setCancelable(true);
                prefschoicedialog.show(getSupportFragmentManager(), "Multiple Choice Dialog");
            }
        });

        editprofile_getloctn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grantpermission();
                editprofile_add_prog.setVisibility(View.VISIBLE);
                getLocations();
                CheckLocationEnabledOrNot();
            }
        });

        editprofile_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cal = Calendar.getInstance();
                int getyear = cal.get(Calendar.YEAR);
                int getmonth = cal.get(Calendar.MONTH);
                int getday = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        String date = dayOfMonth + "/" + month + "/" + year;
                        editprofile_dob.setText(date);
                    }
                }, getyear, getmonth, getday);
                datePickerDialog.show();
            }
        });

        editprofile_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selected_gen_pos;
                String selected_gen_txt = editprofile_gender.getText().toString();

                if (selected_gen_txt.equals("Male")) {
                    selected_gen_pos = 1;
                } else if (selected_gen_txt.equals("Female")) {
                    selected_gen_pos = 2;
                } else {
                    selected_gen_pos = 0;
                }

                DialogFragment genderchoicedialog = new GenderDialogFragment(selected_gen_pos);
                genderchoicedialog.setCancelable(false);
                genderchoicedialog.show(getSupportFragmentManager(), "Single Choice Dialog");
            }
        });

        editprofile_save_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ep_full_name_txt = null;
                String ep_gender_txt, ep_dob_txt;
                String ep_all_number_txt = null;
                String ep_add1_txt = null;
                String ep_add2_txt = null;
                String ep_add3_txt = null;

                String editprofile_full_name_txt = editprofile_full_name.getText().toString();
                String editprofile_gender_txt = editprofile_gender.getText().toString();
                String editprofile_dob_txt = editprofile_dob.getText().toString();

                String editprofile_contact_no_txt = editprofile_contact_no.getText().toString().trim();
                editprofile_contact_no_txt = editprofile_contact_no_txt.replaceAll("\\s+", "");
                String cc = editprofile_contact_ccode.getSelectedCountryCodeWithPlus().trim();

                String editprofile_add1_txt = editprofile_add1.getText().toString();
                String editprofile_add2_txt = editprofile_add2.getText().toString();
                String editprofile_add3_txt = editprofile_add3.getText().toString();
                String editprofile_prefs_arr_txt = editprofile_yourprefs.getText().toString();

                if (editprofile_full_name_txt.isEmpty() || editprofile_full_name_txt.matches("^[^\\s]")) {

                    if (editprofile_full_name_txt.isEmpty()) {
                        editprofile_full_name.setError("Please fill out your fullname");
                        editprofile_full_name.requestFocus();
                    } else if (editprofile_full_name_txt.matches("[^\\s-]")) {
                        editprofile_full_name.setError("Please avoid space at beginning & end of your name !!");
                        editprofile_full_name.requestFocus();
                    }
                } else if (!editprofile_contact_no_txt.matches("^[0-9]*$")) {
                    editprofile_contact_no.setError("Only numbers are allowed !!");
                    editprofile_contact_no.requestFocus();
                } else if (editprofile_add1_txt.matches("^[ \\t\\n\\x0B\\f\\r]+[-a-zA-Z[ \\t\\n\\x0B\\f\\r]]+([-a-zA-Z]+)*$")) {
                    editprofile_add1.setError("Please avoid space at beginning & end!!");
                    editprofile_add1.requestFocus();
                } else if (editprofile_add2_txt.matches("[^\\s]")) {
                    editprofile_add2.setError("Please avoid space at beginning & end !!");
                    editprofile_add2.requestFocus();
                } else if (editprofile_add3_txt.matches("[^\\s-]")) {
                    editprofile_add3.setError("Please avoid space at beginning & end of address !!");
                    editprofile_add3.requestFocus();
                } else if (editprofile_prefs_arr_txt.isEmpty()) {
                    editprofile_yourprefs.setError("Please select atleast one preference !!");
                    editprofile_yourprefs.requestFocus();
                } else {

                    Log.d("enterthis", "All correct here");
                    ep_full_name_txt = editprofile_full_name_txt;

                    if (editprofile_gender_txt.isEmpty()) {
                        Log.d("enterthis", "Gender here");
                        ep_gender_txt = null;
                    } else {
                        ep_gender_txt = editprofile_gender_txt;
                    }

                    if (editprofile_dob_txt.isEmpty()) {
                        Log.d("enterthis", "DOB here");
                        ep_dob_txt = null;
                    } else {
                        ep_dob_txt = editprofile_dob_txt;
                    }

                    if (editprofile_contact_no_txt.isEmpty()) {
                        ep_all_number_txt = null;
                    } else {
                        ep_all_number_txt = cc + "-" + editprofile_contact_no_txt;
                    }

                    if (editprofile_add1_txt.isEmpty()) {
                        editprofile_add1 = null;
                    } else {
                        ep_add1_txt = editprofile_add1_txt;
                    }

                    if (editprofile_add2_txt.isEmpty()) {
                        editprofile_add2 = null;
                    } else {
                        ep_add2_txt = editprofile_add2_txt;
                    }

                    if (editprofile_add3_txt.isEmpty()) {
                        editprofile_add3 = null;
                    } else {
                        ep_add3_txt = editprofile_add3_txt;
                    }

                    selected_prefs_array = new ArrayList<String>(Arrays.asList(editprofile_prefs_arr_txt.substring(0, editprofile_prefs_arr_txt.length() - 2).split(", ")));

                    Map<String, Object> map = new HashMap<>();
                    map.put("fullname", ep_full_name_txt);
                    map.put("gender", ep_gender_txt);
                    map.put("dob", ep_dob_txt);
                    map.put("contact_number", ep_all_number_txt);
                    map.put("fulladdress", ep_add1_txt);
                    map.put("localarea", ep_add2_txt);
                    map.put("state_country", ep_add3_txt);
                    map.put("myprefs", selected_prefs_array);

                    db.collection("users")
                            .document(firebaseUser.getUid())
                            .update(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(EditProfileActivity.this, "Changes updated successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), ProfilesettingsActivity.class));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditProfileActivity.this, "Unable to save changes.Try Again!\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        editprofile_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editprofile_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selected_gen_pos;
                String selected_gen_txt = editprofile_gender.getText().toString();

                if (selected_gen_txt.equals("Male")) {
                    selected_gen_pos = 1;
                } else if (selected_gen_txt.equals("Female")) {
                    selected_gen_pos = 2;
                } else {
                    selected_gen_pos = 0;
                }

                DialogFragment genderchoicedialog = new GenderDialogFragment(selected_gen_pos);
                genderchoicedialog.setCancelable(false);
                genderchoicedialog.show(getSupportFragmentManager(), "Single Choice Dialog");
            }
        });

    }

    private void seperate_cc_number(@NotNull String contact_number) {

        String[] phone_list;
        String cc;
        String number;

        phone_list = contact_number.split("-");
        cc = phone_list[0];
        number = phone_list[1];
        editprofile_contact_ccode.setCountryForPhoneCode(Integer.parseInt(cc));
        editprofile_contact_no.setText(number);

    }

    @SuppressLint("SetTextI18n")
    private void addProfilePrefs(@NotNull ArrayList<String> pref_array) {

        StringBuilder stringBuilder = new StringBuilder();
        for (String pref_each : pref_array) {
            stringBuilder.append(pref_each).append(", ");
        }
        SharedPreferences.Editor editor = getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
        editor.putString("prefs_string", stringBuilder.toString());
        editor.apply();
        editprofile_yourprefs.setText(stringBuilder.toString());
    }

    private void getLocations() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void CheckLocationEnabledOrNot() {
        LocationManager manager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
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
            new AlertDialog.Builder(this)
                    .setTitle("Enable GPS Service")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    }).setNegativeButton("Cancel", null)
                    .show();
        }
    }

    private void grantpermission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLocationChanged(Location location) {

        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            editprofile_add_prog.setVisibility(View.GONE);
            editprofile_add1.setText(addresses.get(0).getAddressLine(0));
            editprofile_add2.setText(addresses.get(0).getLocality() + " - " + addresses.get(0).getPostalCode());
            editprofile_add3.setText(addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());

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

    @Override
    public void onPostiveButtonClicked(String[] list, int selected_position) {
        editprofile_gender.setText(list[selected_position]);
    }

    @Override
    public void onPositiveButtonClicked(String[] list, ArrayList<String> selectedItemlist) {
        addProfilePrefs(selectedItemlist);
    }

    @Override
    public void onNegativeButtonClicked() {

    }
}