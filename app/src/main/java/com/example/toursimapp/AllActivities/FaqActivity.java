package com.example.toursimapp.AllActivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toursimapp.Data.TripList;
import com.example.toursimapp.Models.FaqQuestionsModel;
import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.R;

import java.util.ArrayList;

public class FaqActivity extends AppCompatActivity {

    ImageButton faqs_back_btn;
    RecyclerView faqs_rec;
    FaqAdapter faqAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        new Functions().lightstatusbardesign(FaqActivity.this);
        faqs_back_btn = findViewById(R.id.faqs_back_btn);
        faqs_rec = findViewById(R.id.faqs_rec);

        faqs_back_btn.setOnClickListener(v -> finish());

        faqs_rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        faqAdapter = new FaqAdapter(getApplicationContext(), new TripList().faq_data_array(), FaqActivity.this);
        faqs_rec.setAdapter(faqAdapter);
        faqAdapter.notifyDataSetChanged();

    }

    public static class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.ListViewHolder> {

        Context context;
        ArrayList<FaqQuestionsModel> faqs_array;
        Activity activity;

        public FaqAdapter(Context context, ArrayList<FaqQuestionsModel> faqs_array, Activity activity) {
            this.context = context;
            this.faqs_array = faqs_array;
            this.activity = activity;
        }

        @NonNull
        @Override
        public FaqAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.faqs_items, null);
            return new ListViewHolder(inflate);
        }

        @Override
        public void onBindViewHolder(@NonNull FaqAdapter.ListViewHolder holder, int position) {
            holder.header_faq.setText(faqs_array.get(position).getFaq_ques());
            holder.faq_lay.setOnClickListener(v -> {
                final Dialog dialog = new Dialog(activity);
                dialog.setContentView(R.layout.faq_dialog);
                dialog.setCanceledOnTouchOutside(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
                TextView faq_msg_content = dialog.findViewById(R.id.faq_msg_content);
                TextView faq_msg_ok_btn = dialog.findViewById(R.id.faq_msg_ok_btn);

                faq_msg_content.setMovementMethod(new ScrollingMovementMethod());
                faq_msg_content.setText(faqs_array.get(position).getFaq_ans());
                faq_msg_ok_btn.setOnClickListener(v1 -> dialog.dismiss());
                dialog.show();
            });
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return faqs_array.size();
        }

        public static class ListViewHolder extends RecyclerView.ViewHolder {

            View faq_lay;
            TextView header_faq;

            public ListViewHolder(@NonNull View itemView) {
                super(itemView);

                faq_lay = itemView.findViewById(R.id.faq_lay);
                header_faq = itemView.findViewById(R.id.header_faq);
            }
        }
    }
}