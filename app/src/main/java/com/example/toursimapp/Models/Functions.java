package com.example.toursimapp.Models;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.toursimapp.Fragments.Inboxfragment.NotiModel;
import com.example.toursimapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

@SuppressWarnings("all")
public class Functions {

    int count = 0;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    public void checkTheme(@NotNull Context context) {

        SharedPreferences prefs_2 = context.getSharedPreferences("app_data", Activity.MODE_PRIVATE);
        int isCheck = prefs_2.getInt("get_theme", 0);

        if (isCheck == 0) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (isCheck == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (isCheck == 2) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public void darkstatusbardesign(@NotNull Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.heading_no));
    }

    public void lightstatusbardesign(@NotNull Activity activity) {

        SharedPreferences prefs_2 = activity.getSharedPreferences("app_data", Activity.MODE_PRIVATE);
        int isCheck = prefs_2.getInt("get_theme", 0);

        if (isCheck == 0) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else if (isCheck == 1) {
            activity.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else if (isCheck == 2) {

            int nightModeFlags = activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

            switch (nightModeFlags) {
                case Configuration.UI_MODE_NIGHT_YES:
                    activity.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    break;

                case Configuration.UI_MODE_NIGHT_NO:
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    break;

                case Configuration.UI_MODE_NIGHT_UNDEFINED:
                    activity.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    break;
            }
        } else {
            activity.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.status_white));
    }

    public void setStatusBarColor(@NotNull Activity activity, int color_id) {
        activity.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().setStatusBarColor(activity.getResources().getColor(color_id));
    }

    public void notificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notifytheapp", "Foreground Service", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public Date modifiedDate(Date date, int change) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, change);
        return cal.getTime();
    }

    public ArrayList<String> getRandomMainPlaces() {
        ArrayList<String> arrayList = new ArrayList<>();
        Random random = new Random();
        final int random_val = random.nextInt(12) + 1;

        arrayList.add("family_" + random_val);
        arrayList.add("friends_" + random_val);
        arrayList.add("honeymoon_" + random_val);
        arrayList.add("religious_" + random_val);
        arrayList.add("solo_" + random_val);

        return arrayList;
    }

    public String username(Activity activity) {
        String username;
        SharedPreferences get_user_data_prefs = activity.getSharedPreferences("user_data", Activity.MODE_PRIVATE);
        username = get_user_data_prefs.getString("fullname", "User");
        return username;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getNoDays(@NotNull Date startDate, @NotNull Date endDate) {
        int num = 1;

        LocalDate dateFrom = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate dateTo = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Period intervalPeriod = Period.between(dateFrom, dateTo);
        Log.e("num_days", "Difference of days: " + intervalPeriod.getDays());
        num = intervalPeriod.getDays();
        return num;
    }

    public int getNoRooms(int no_guests, int persons_per_room) {
        int no_trooms = 1;
        if (no_guests < persons_per_room) {
            return no_trooms;
        } else {
            int remainder = no_guests / persons_per_room;
            int quotient = no_guests % persons_per_room;
            no_trooms = remainder + quotient;
            Log.e("no_rooms1", "No of rooms are : " + no_trooms);
            return no_trooms;
        }
    }

    public int setBadgeNum(ChipNavigationBar bottom_home_navi, @NotNull ArrayList<NotiModel> notification_array) {
        int count = 0;
        for (int i = 0; i < notification_array.size(); i++) {
            if (!notification_array.get(i).isSeen()) {
                count++;
            }
        }
        return count;
    }

    public ArrayList<NotiModel> getDbNoti(ChipNavigationBar bottom_home_navi) {

        ArrayList<NotiModel> arrayList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(firebaseUser.getUid())
                .collection("user_notification")
                .orderBy("notify_date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                NotiModel notiModel = new NotiModel(
                                        documentSnapshot.getString("order_id"),
                                        documentSnapshot.getString("notify_content_txt"),
                                        documentSnapshot.getBoolean("seen"));

                                arrayList.add(notiModel);
                            }
                        }
                    } else {
                        Log.e("user_noti", "Error!!!");
                    }

                });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                for (int i = 0; i < arrayList.size(); i++) {
                    if (!arrayList.get(i).isSeen()) {
                        count++;
                    }
                }
                if (count > 0) {
                    bottom_home_navi.showBadge(R.id.btm_inbox, count);
                } else {
                    bottom_home_navi.dismissBadge(R.id.btm_inbox);
                }

            }
        }, 1000);

        return arrayList;
    }

    public void Orders_Dialog_Box(Activity activity, String msg) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.msg_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        TextView msg_content = dialog.findViewById(R.id.msg_content);
        TextView msg_ok_btn = dialog.findViewById(R.id.msg_ok_btn);

        msg_content.setText(String.format("%s \n%s", activity.getResources().getString(R.string.msg_dialog_txt), msg));
        msg_ok_btn.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    public void no_internet_dialog(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.no_net_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        Button no_net_dialog_oktbn = dialog.findViewById(R.id.no_net_dia_ok_btn);

        no_net_dialog_oktbn.setOnClickListener(v2 -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    public void whats_new_dialog(Activity activity, int pos) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.help_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        TextView title = dialog.findViewById(R.id.dialog_title);
        TextView content = dialog.findViewById(R.id.help_content);
        TextView app_rate_txt = dialog.findViewById(R.id.app_rate_text);
        TextView btn_great = dialog.findViewById(R.id.btn_ok);

        title.setText(R.string.des_title);
        content.setText(R.string.des_content);
        app_rate_txt.setVisibility(View.VISIBLE);
        btn_great.setText("GREAT!");
        btn_great.setOnClickListener(v -> {

            if (pos == 2) {
                SharedPreferences.Editor editor = activity.getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
                editor.putBoolean("start_up_news", false);
                editor.apply();
            }
            dialog.dismiss();
        });

        app_rate_txt.setOnClickListener(v -> activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.lazygeniouz.saveit"))));
        dialog.show();
    }
}
