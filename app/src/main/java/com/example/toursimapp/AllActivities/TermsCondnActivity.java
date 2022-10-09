package com.example.toursimapp.AllActivities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.R;

public class TermsCondnActivity extends AppCompatActivity {

    private WebView terms_condn_wv;
    private ImageButton tc_back_btn;
    private View tc_prog_lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Functions().checkTheme(getApplicationContext());
        setContentView(R.layout.activity_terms_condn);

        terms_condn_wv = findViewById(R.id.terms_condn_wv);
        tc_back_btn = findViewById(R.id.tc_back_btn);
        tc_prog_lay = findViewById(R.id.tc_prog_lay);

        tc_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        terms_condn_wv.zoomIn();
        terms_condn_wv.zoomOut();

        tc_prog_lay.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tc_prog_lay.setVisibility(View.GONE);
                terms_condn_wv.loadUrl("https://www.websitepolicies.com/policies/view/xQU6F25d");
            }
        }, 2000);
    }
}