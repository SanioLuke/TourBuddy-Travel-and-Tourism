package com.example.toursimapp.AllActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toursimapp.Adapters.CommentAdapter;
import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Map;

public class ShowCommentActivity extends AppCompatActivity {

    ImageButton show_comment_back_btn;
    PieChart show_comment_hpie;
    TextView show_comment_allcomments_heading;
    RecyclerView show_comment_allcomments_rec;
    float rate;
    int which_page;
    ArrayList<Map<Object, String>> allcomments_array = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Functions().checkTheme(getApplicationContext());
        new Functions().lightstatusbardesign(ShowCommentActivity.this);
        setContentView(R.layout.activity_show_comment);

        show_comment_back_btn = findViewById(R.id.show_comment_back_btn);
        show_comment_hpie = findViewById(R.id.show_comment_hpie);
        show_comment_allcomments_heading = findViewById(R.id.show_comment_allcomments_heading);
        show_comment_allcomments_rec = findViewById(R.id.show_comment_allcomments_rec);

        Intent intent = getIntent();
        rate = intent.getFloatExtra("rate", 1);
        which_page = intent.getIntExtra("page_pos", 0);
        allcomments_array = (ArrayList<Map<Object, String>>) intent.getSerializableExtra("all_comments_array");

        ArrayList<PieEntry> booked_data = new ArrayList<>();
        booked_data.add(new PieEntry(rate, "Rate"));
        booked_data.add(new PieEntry(5, "Total rate"));
        PieDataSet pieDataSet = new PieDataSet(booked_data, "  -Rate Graph");
        pieDataSet.setColors(new int[]{R.color.light_color, R.color.light_grey}, getApplicationContext());
        pieDataSet.setValueTextColor(android.R.color.darker_gray);
        pieDataSet.setValueTextSize(16f);
        PieData pieData = new PieData(pieDataSet);
        show_comment_hpie.setData(pieData);

        show_comment_hpie.setEntryLabelColor(android.R.color.darker_gray);
        show_comment_hpie.getDescription().setEnabled(false);
        show_comment_hpie.setEntryLabelTextSize(17);
        show_comment_hpie.setCenterText("Rate Gragh");
        show_comment_hpie.animate();

        show_comment_allcomments_heading.setText("All Comments ( " + allcomments_array.size() + " reviews )");
        show_comment_allcomments_rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        CommentAdapter commentAdapter = new CommentAdapter(allcomments_array, getApplicationContext(), which_page);
        show_comment_allcomments_rec.setAdapter(commentAdapter);
        commentAdapter.notifyDataSetChanged();

        show_comment_back_btn.setOnClickListener(v -> finish());


    }
}