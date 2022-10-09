package com.example.toursimapp.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toursimapp.AllActivities.Inbox_details;
import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

public class Inboxfragment extends Fragment {

    private final Functions functions = new Functions();
    private ChipNavigationBar from_main_nav;
    private RecyclerView inbox_noti_rec;
    private View notify_details_anim_lay, inboxfrag_loading_lay;
    private ArrayList<NotiModel> noti_array = new ArrayList<>();
    private NotiAdapter notiAdapter;

    public Inboxfragment() {
    }

    public Inboxfragment(ChipNavigationBar from_main_nav) {
        this.from_main_nav = from_main_nav;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        new Functions().checkTheme(getContext());
        View view = inflater.inflate(R.layout.fragment_inboxfragment, container, false);

        inbox_noti_rec = view.findViewById(R.id.inbox_noti_rec);
        notify_details_anim_lay = view.findViewById(R.id.notify_details_anim_lay);
        inboxfrag_loading_lay = view.findViewById(R.id.inboxfrag_loading_lay);
        inboxfrag_loading_lay.setVisibility(View.VISIBLE);
        inboxDataSetting();

        return view;
    }

    private void inboxDataSetting() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                noti_array = functions.getDbNoti(from_main_nav);
            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                inboxfrag_loading_lay.setVisibility(View.GONE);
                if (noti_array.size() > 0) {
                    notify_details_anim_lay.setVisibility(View.GONE);
                    inbox_noti_rec.setVisibility(View.VISIBLE);
                    notiAdapter = new NotiAdapter(noti_array, getContext(), from_main_nav);
                    inbox_noti_rec.setLayoutManager(new LinearLayoutManager(getContext()));
                    notiAdapter.notifyDataSetChanged();
                    inbox_noti_rec.setAdapter(notiAdapter);
                } else {
                    notify_details_anim_lay.setVisibility(View.VISIBLE);
                    inbox_noti_rec.setVisibility(View.GONE);
                }
            }
        }, 2000);
    }

    public static class NotiModel {
        String notify_content_txt, order_id;
        boolean seen;

        public NotiModel(String order_id, String notify_content_txt, boolean seen) {
            this.order_id = order_id;
            this.notify_content_txt = notify_content_txt;
            this.seen = seen;
        }

        public String getOrder_id() {
            return order_id;
        }

        public String getNotify_content_txt() {
            return notify_content_txt;
        }

        public boolean isSeen() {
            return seen;
        }
    }

    public static class NotiAdapter extends RecyclerView.Adapter<NotiAdapter.ListViewHolder> {

        ArrayList<NotiModel> noti_list = new ArrayList<>();
        Context context;
        ChipNavigationBar from_main_nav;
        Functions functions = new Functions();
        private FirebaseAuth firebaseAuth;
        private FirebaseUser firebaseUser;
        private FirebaseFirestore db;

        public NotiAdapter(ArrayList<NotiModel> noti_list, Context context, ChipNavigationBar from_main_nav) {
            this.noti_list = noti_list;
            this.context = context;
            this.from_main_nav = from_main_nav;
        }

        @NonNull
        @Override
        public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_items, null);
            return new NotiAdapter.ListViewHolder(inflate);
        }

        @Override
        public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {

            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            db = FirebaseFirestore.getInstance();

            holder.inboxitem_content.setText(noti_list.get(position).getNotify_content_txt());

            if (!noti_list.get(position).isSeen()) {
                holder.inboxitem_newitem.setVisibility(View.VISIBLE);
                holder.inboxitem_main_container.setBackgroundColor(ContextCompat.getColor(context, R.color.offwhite));
            } else {
                holder.inboxitem_newitem.setVisibility(View.GONE);
                holder.inboxitem_main_container.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            }

            holder.inboxitem_main_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ArrayList<NotiModel> not_list_array = functions.getDbNoti(from_main_nav);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!not_list_array.get(position).isSeen()) {
                                db.collection("users")
                                        .document(firebaseUser.getUid())
                                        .collection("user_notification")
                                        .document(noti_list.get(position).getOrder_id())
                                        .update("seen", true)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    new Functions().getDbNoti(from_main_nav);
                                                    holder.inboxitem_main_container.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                                                    holder.inboxitem_newitem.setVisibility(View.GONE);
                                                } else {
                                                    holder.inboxitem_main_container.setBackgroundColor(ContextCompat.getColor(context, R.color.offwhite));
                                                    holder.inboxitem_newitem.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        });
                            }
                            Intent intent = new Intent(context, Inbox_details.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("inbox_details_content", noti_list.get(position).getNotify_content_txt());
                            intent.putExtra("inbox_details_orderid", noti_list.get(position).getOrder_id());
                            context.startActivity(intent);
                        }
                    }, 1000);
                }
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
            return noti_list.size();
        }

        public static class ListViewHolder extends RecyclerView.ViewHolder {

            View inboxitem_main_container;
            TextView inboxitem_content, inboxitem_newitem;

            public ListViewHolder(@NonNull View itemView) {
                super(itemView);

                inboxitem_main_container = itemView.findViewById(R.id.inboxitem_main_container);
                inboxitem_content = itemView.findViewById(R.id.inboxitem_content);
                inboxitem_newitem = itemView.findViewById(R.id.inboxitem_newitem);
            }
        }
    }
}