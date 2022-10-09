package com.example.toursimapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.toursimapp.AllActivities.OrdersDisplayActivity;
import com.example.toursimapp.Models.SummaryOrderModel;
import com.example.toursimapp.R;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SummaryBookAdpater extends RecyclerView.Adapter<SummaryBookAdpater.ListViewHolder> {

    private final DecimalFormat formatter = new DecimalFormat("#,##,###");
    ArrayList<SummaryOrderModel> summary_data_array;
    Context context;

    public SummaryBookAdpater(Context context, ArrayList<SummaryOrderModel> summary_data_array) {
        this.context = context;
        this.summary_data_array = summary_data_array;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_summary_items, null);
        return new ListViewHolder(inflate);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {

        int count = position + 1;
        holder.summary_order_count.setText("" + count);
        holder.order_summary_price.setText("₹ " + formatter.format(summary_data_array.get(position).getTotal_final_price()) + ".00");
        holder.order_summary_hname.setText(summary_data_array.get(position).getHotel_name());
        holder.order_summary_pname.setText(summary_data_array.get(position).getPlace_name());
        Glide.with(context).load(summary_data_array.get(position).getHotel_img()).into(holder.order_summary_image);

        if (summary_data_array.get(position).isOrder_summary_status()) {
            holder.order_summary_check.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check_circle));
            holder.summary_order_count.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.my_green)));
            holder.summary_status_txt.setText("Success");
        } else {
            holder.order_summary_check.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_error));
            holder.summary_order_count.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.my_red)));
            holder.summary_status_txt.setText("Failed");
        }

        holder.order_summary_main_lay.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrdersDisplayActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("booked_summary_data", summary_data_array.get(position));
            context.startActivity(intent);
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
        return summary_data_array.size();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {

        View order_summary_main_lay;
        ImageView order_summary_check;
        TextView order_summary_price, order_summary_hname, order_summary_pname, summary_order_count, summary_status_txt;
        ImageView order_summary_image;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);

            order_summary_main_lay = itemView.findViewById(R.id.order_summary_main_lay);
            summary_order_count = itemView.findViewById(R.id.summary_order_count);
            order_summary_check = itemView.findViewById(R.id.order_summary_check);
            order_summary_price = itemView.findViewById(R.id.order_summary_price);
            order_summary_image = itemView.findViewById(R.id.order_summary_image);
            order_summary_hname = itemView.findViewById(R.id.order_summary_hname);
            order_summary_pname = itemView.findViewById(R.id.order_summary_pname);
            summary_status_txt = itemView.findViewById(R.id.summary_status_txt);

        }
    }
}
