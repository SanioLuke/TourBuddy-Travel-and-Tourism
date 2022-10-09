package com.example.toursimapp.AllActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;

import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.R;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ProfilesettingsActivity extends AppCompatActivity {

    MainActivity mainActivity = new MainActivity();
    private NestedScrollView mprofile_nest_lay;
    private AppBarLayout mprofile_appbarlay;
    private Toolbar mprofile_toolbar;
    private TextView mprofile_toolbar_title;
    private ImageButton mprofile_back_btn;
    private LinearLayout mprofile_add_each_prefs;
    private FloatingActionButton mprofile_edit_btn;
    private TextView mprofile_fullname, mprofile_gender, mprofile_dob, mprofile_address, mprofile_contactno, mprofile_emailid;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Functions().checkTheme(getApplicationContext());
        setContentView(R.layout.activity_profilesettings);

        {
            mprofile_nest_lay = findViewById(R.id.mprofile_nest_lay);
            mprofile_add_each_prefs = findViewById(R.id.mprofile_add_each_prefs);
            mprofile_appbarlay = findViewById(R.id.mprofile_appbarlay);
            mprofile_toolbar = findViewById(R.id.mprofile_toolbar);
            mprofile_toolbar_title = findViewById(R.id.mprofile_toolbar_title);
            mprofile_back_btn = findViewById(R.id.mprofile_back_btn);
            mprofile_edit_btn = findViewById(R.id.mprofile_edit_btn);

            mprofile_fullname = findViewById(R.id.mprofile_fullname);
            mprofile_gender = findViewById(R.id.mprofile_gender);
            mprofile_dob = findViewById(R.id.mprofile_dob);
            mprofile_address = findViewById(R.id.mprofile_address);
            mprofile_contactno = findViewById(R.id.mprofile_contactno);
            mprofile_emailid = findViewById(R.id.mprofile_emailid);
            firebaseAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();

        }  //Initializations

        {
            if (!firebaseUser.isAnonymous()) {
                db.collection("users")
                        .document(firebaseUser.getUid())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot != null) {
                                    String prof_setting_fullname = documentSnapshot.getString("fullname");
                                    String prof_setting_gender = documentSnapshot.getString("gender");
                                    String prof_setting_dob = documentSnapshot.getString("dob");
                                    String prof_setting_address = documentSnapshot.getString("fulladdress");
                                    String prof_setting_contactno = documentSnapshot.getString("contact_number");
                                    String prof_setting_emailid = documentSnapshot.getString("emailid");

                                    if (prof_setting_fullname != null) {
                                        mprofile_fullname.setText(prof_setting_fullname);
                                    } else {
                                        mprofile_fullname.setText("(Not Added)");
                                    }

                                    if (prof_setting_gender != null) {
                                        mprofile_gender.setText(prof_setting_gender);
                                    } else {
                                        mprofile_gender.setText("(Not Added)");
                                    }

                                    if (prof_setting_dob != null) {
                                        mprofile_dob.setText(prof_setting_dob);
                                    } else {
                                        mprofile_dob.setText("(Not Added)");
                                    }

                                    if (prof_setting_address != null) {
                                        mprofile_address.setText(prof_setting_address);
                                    } else {
                                        mprofile_address.setText("(Not Added)");
                                    }

                                    if (prof_setting_contactno != null) {
                                        mprofile_contactno.setText(prof_setting_contactno);
                                    } else {
                                        mprofile_contactno.setText("(Not Added)");
                                    }

                                    if (prof_setting_emailid != null) {
                                        mprofile_emailid.setText(prof_setting_emailid);
                                    } else {
                                        mprofile_emailid.setText("(Not Added)");
                                    }

                                    if (documentSnapshot.get("myprefs") != null) {
                                        ArrayList<String> pref_array = (ArrayList<String>) documentSnapshot.get("myprefs");
                                        mProfilePrefs(mprofile_add_each_prefs, pref_array);
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("mprofile_error", "" + e);
                            }
                        });
            }

        }  //Data displaying

        mprofile_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("from_page", "AccountsettingsActivity");
                startActivity(intent);
            }
        });

        mprofile_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));
            }
        });

        mprofile_appbarlay.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == mprofile_appbarlay.getTotalScrollRange()) {
                    mprofile_nest_lay.setBackgroundResource(R.color.white);
                    mprofile_toolbar.setBackground(getDrawable(R.color.heading_no));
                    mprofile_toolbar_title.setVisibility(View.VISIBLE);
                } else {
                    mprofile_nest_lay.setBackgroundResource(R.drawable.back_cur_shape);
                    mprofile_toolbar.setBackground(getDrawable(android.R.color.transparent));
                    mprofile_toolbar_title.setVisibility(View.GONE);
                }
            }
        });

    }

    private void mProfilePrefs(@NotNull LinearLayout mprofile_prefs_lay, @NotNull ArrayList<String> pref_array) {

        FlexboxLayout flexboxLayout;
        flexboxLayout = new FlexboxLayout(this);
        FlexboxLayout.LayoutParams lp1 = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp1.setMargins(0, 10, 0, 0);
        flexboxLayout.setFlexWrap(FlexWrap.WRAP);
        lp1.setFlexGrow(1);
        flexboxLayout.setLayoutParams(lp1);
        flexboxLayout.setFlexDirection(FlexDirection.ROW);
        mprofile_prefs_lay.addView(flexboxLayout);

        for (int j = 0; j < pref_array.size(); j++) {

            TextView text = new TextView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(10, 10, 0, 0);
            text.setPadding(10, 10, 10, 10);
            text.setText(pref_array.get(j));
            Typeface tf = ResourcesCompat.getFont(this, R.font.poppins_light);
            text.setTypeface(tf);
            text.setBackgroundResource(R.drawable.stroke_drawable);
            text.setLayoutParams(lp);
            flexboxLayout.addView(text);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("from_page", "AccountsettingsActivity");
        startActivity(intent);
    }

}