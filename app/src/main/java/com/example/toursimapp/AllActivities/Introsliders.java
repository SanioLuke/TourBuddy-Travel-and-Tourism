package com.example.toursimapp.AllActivities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.toursimapp.Adapters.SliderAdapter;
import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.R;

public class Introsliders extends AppCompatActivity {

    private final TextView[] mdots = new TextView[3];
    ViewPager introviewpager;
    LinearLayout dotlayout;
    TextView slide_next, slide_prev, slide_finish;
    private SliderAdapter sliderAdapter;
    private int currentslide;
    ViewPager.OnPageChangeListener viewListner = new ViewPager.OnPageChangeListener() {
        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            addDotsIndicator(position);
            currentslide = position;

            if (position == 0) {
                slide_next.setVisibility(View.VISIBLE);
                slide_prev.setVisibility(View.INVISIBLE);
                slide_finish.setVisibility(View.INVISIBLE);
            } else if (position == mdots.length - 1) {
                slide_prev.setVisibility(View.VISIBLE);
                slide_next.setVisibility(View.INVISIBLE);
                slide_finish.setVisibility(View.VISIBLE);
            } else {
                slide_next.setVisibility(View.VISIBLE);
                slide_prev.setVisibility(View.VISIBLE);
                slide_finish.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Functions().checkTheme(getApplicationContext());
        setContentView(R.layout.activity_introsliders);

        introviewpager = findViewById(R.id.introviewpager);
        dotlayout = findViewById(R.id.dotlayout);
        slide_next = findViewById(R.id.slide_next);
        slide_prev = findViewById(R.id.slide_prev);
        slide_finish = findViewById(R.id.slide_finish);

        addDotsIndicator(0);
        sliderAdapter = new SliderAdapter(getApplicationContext());
        introviewpager.setAdapter(sliderAdapter);


        introviewpager.addOnPageChangeListener(viewListner);

        slide_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                introviewpager.setCurrentItem(currentslide + 1);
            }
        });

        slide_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
                editor.putBoolean("intro", true);
                editor.apply();
                startActivity(new Intent(getApplicationContext(), Logsignactivity.class));
                finish();
            }
        });

        slide_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                introviewpager.setCurrentItem(currentslide - 1);
            }
        });

    }

    private void addDotsIndicator(int position) {
        dotlayout.removeAllViews();

        for (int i = 0; i < mdots.length; i++) {
            mdots[i] = new TextView(this);
            mdots[i].setText(Html.fromHtml("&#8226"));
            mdots[i].setTextSize(40);
            mdots[i].setTextColor(ColorStateList.valueOf(Color.parseColor("#0080FF")));
            dotlayout.addView(mdots[i]);
        }
        if (mdots.length > 0) {
            mdots[position].setTextColor(ColorStateList.valueOf(Color.parseColor("#002850")));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int curr_pos = introviewpager.getCurrentItem();
        if (curr_pos == 0) {
            finishAffinity();
        }
    }
}