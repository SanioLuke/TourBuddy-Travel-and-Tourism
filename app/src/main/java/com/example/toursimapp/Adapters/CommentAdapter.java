package com.example.toursimapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toursimapp.R;

import java.util.ArrayList;
import java.util.Map;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ListViewHolder> {

    ArrayList<Map<Object, String>> all_comment_array;
    Context context;
    int which_page;

    public CommentAdapter(ArrayList<Map<Object, String>> all_comment_array, Context context, int which_page) {
        this.all_comment_array = all_comment_array;
        this.context = context;
        this.which_page = which_page;
    }

    @NonNull
    @Override
    public CommentAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_items, null);
        return new CommentAdapter.ListViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ListViewHolder holder, int position) {

        if (which_page == 1) {
            holder.citem_user.setText(all_comment_array.get(position).get("mplace_comment_user"));
            holder.citem_text.setText(all_comment_array.get(position).get("mplace_comment_txt"));
            holder.citem_time.setText(all_comment_array.get(position).get("mplace_comment_time"));
        } else if (which_page == 2) {
            holder.citem_user.setText(all_comment_array.get(position).get("inner_comment_user"));
            holder.citem_text.setText(all_comment_array.get(position).get("inner_comment_txt"));
            holder.citem_time.setText(all_comment_array.get(position).get("inner_comment_time"));
        } else if (which_page == 3) {
            holder.citem_user.setText(all_comment_array.get(position).get("hotels_comment_user"));
            holder.citem_text.setText(all_comment_array.get(position).get("hotels_comment_txt"));
            holder.citem_time.setText(all_comment_array.get(position).get("hotels_comment_time"));
        }
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
        return all_comment_array.size();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {

        TextView citem_text, citem_time, citem_user;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);

            citem_text = itemView.findViewById(R.id.citem_text);
            citem_time = itemView.findViewById(R.id.citem_time);
            citem_user = itemView.findViewById(R.id.citem_user);
        }
    }
}