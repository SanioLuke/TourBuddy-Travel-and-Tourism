package com.example.toursimapp.AllActivities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class AccountsettingsActivity extends AppCompatActivity {

    private View change_pass_lay, updte_email_lay, delete_accnt_lay;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    private ImageButton accnt_back_btn;
    private GoogleSignInClient googleSignInClient;
    private boolean isGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Functions().lightstatusbardesign(AccountsettingsActivity.this);
        new Functions().checkTheme(getApplicationContext());
        setContentView(R.layout.activity_accountsettings);

        accountSettingsInits();
        accountSettingsEventListnerFun();
    }

    private void accountSettingsEventListnerFun() {

        accnt_back_btn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("from_page", "AccountsettingsActivity");
            startActivity(intent);
        });

        change_pass_lay.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AccountEditActivity.class);
            intent.putExtra("data_to_page", 1);
            startActivity(intent);
        });

        updte_email_lay.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AccountEditActivity.class);
            intent.putExtra("data_to_page", 2);
            startActivity(intent);
        });

        delete_accnt_lay.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(AccountsettingsActivity.this);
            dialog.setContentView(R.layout.delete_accnt_dialog);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;

            TextView accnt_delete_cancel_btn = dialog.findViewById(R.id.accnt_delete_cancel_btn);
            TextView accnt_process_btn = dialog.findViewById(R.id.accnt_process_btn);

            accnt_delete_cancel_btn.setOnClickListener(v1 -> dialog.cancel());

            accnt_process_btn.setOnClickListener(v12 -> {

                db.collection("users")
                        .document(firebaseUser.getUid())
                        .collection("favourites")
                        .get()
                        .addOnCompleteListener(task -> {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                documentSnapshot.getReference().delete();
                            }
                        });

                new Handler().postDelayed(() -> {
                    db.collection("users")
                            .document(firebaseUser.getUid())
                            .delete()
                            .addOnSuccessListener(aVoid -> Log.d("data_delete", "Data deleted successfully"))
                            .addOnFailureListener(e -> {
                                Log.d("data_delete", "Data delete failed " + e.getMessage());
                                Toast.makeText(AccountsettingsActivity.this, "Unable to delete data " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });

                    firebaseUser.delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(AccountsettingsActivity.this, "Account deleted successfully. We hope that you will visit us again :)", Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor editor = getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
                                editor.putBoolean("log_status", false);
                                editor.apply();

                                SharedPreferences.Editor user_prefs1 = getSharedPreferences("user_data", Activity.MODE_PRIVATE).edit();
                                user_prefs1.clear();
                                user_prefs1.apply();

                                verify();
                                creategooglerequest();
                                googleSignInClient.signOut();

                                dialog.dismiss();
                                new Handler().postDelayed(() -> startActivity(new Intent(getApplicationContext(), Logsignactivity.class)), 500);
                            })
                            .addOnFailureListener(e -> Toast.makeText(AccountsettingsActivity.this, "Unable to delete account " + e.getMessage(), Toast.LENGTH_LONG).show());
                }, 500);
            });

            dialog.show();
        });
    }

    private void accountSettingsInits() {
        change_pass_lay = findViewById(R.id.change_pass_lay);
        updte_email_lay = findViewById(R.id.updte_email_lay);
        delete_accnt_lay = findViewById(R.id.delete_accnt_lay);
        accnt_back_btn = findViewById(R.id.accnt_back_btn);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        SharedPreferences prefs_1 = getSharedPreferences("app_data", Activity.MODE_PRIVATE);
        isGoogle = prefs_1.getBoolean("isGoogle", false);
        if (isGoogle) {
            change_pass_lay.setVisibility(View.GONE);
            updte_email_lay.setVisibility(View.GONE);
        }
    }

    private void verify() {
        FirebaseAuth.AuthStateListener mAuthStateListner = firebaseAuth -> {
            FirebaseUser mFirebaseuser = firebaseAuth.getCurrentUser();
            if (mFirebaseuser == null) {
                Toast.makeText(getApplicationContext(), "You have already logged out", Toast.LENGTH_SHORT).show();

                Intent in7 = new Intent(getApplicationContext(), Logsignactivity.class);
                startActivity(in7);
            }
        };
    }

    private void creategooglerequest() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getApplicationContext()), googleSignInOptions);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("from_page", "AccountsettingsActivity");
        startActivity(intent);
    }
}