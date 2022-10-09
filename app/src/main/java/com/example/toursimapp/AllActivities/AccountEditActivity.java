package com.example.toursimapp.AllActivities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class AccountEditActivity extends AppCompatActivity {

    private ImageButton accntedit_back_btn;
    private LinearLayout edit_change_pass_lay, update_email_lay;
    private TextView accntedit_toolbar_title;

    private TextInputLayout editaccnt_old_pass, editaccnt_new_pass, editaccnt_conf_new_pass;
    private Button editaccnt_change_pass_btn;
    private final TextWatcher pass_textwactcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String editaccnt_old_pass_txt = editaccnt_old_pass.getEditText().getText().toString();
            String editaccnt_new_pass_txt = editaccnt_new_pass.getEditText().getText().toString();
            String editaccnt_conf_new_pass_txt = editaccnt_conf_new_pass.getEditText().getText().toString();

            editaccnt_change_pass_btn.setEnabled(!editaccnt_old_pass_txt.isEmpty() && !editaccnt_new_pass_txt.isEmpty() && !editaccnt_conf_new_pass_txt.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private TextInputLayout editaccnt_current_pass, editaccnt_new_email, editaccnt_conf_new_email;
    private Button editaccnt_update_email_btn;
    private final TextWatcher email_textwactcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String editaccnt_curr_pass_txt = editaccnt_current_pass.getEditText().getText().toString();
            String editaccnt_new_email_txt = editaccnt_new_email.getEditText().getText().toString();
            String editaccnt_conf_new_email_txt = editaccnt_conf_new_email.getEditText().getText().toString();

            editaccnt_update_email_btn.setEnabled(!editaccnt_curr_pass_txt.isEmpty() && !editaccnt_new_email_txt.isEmpty() && !editaccnt_conf_new_email_txt.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Functions().checkTheme(getApplicationContext());
        setContentView(R.layout.activity_account_edit);

        acountEditInits();
        getpagedataVisibleFun();
        accountEdit_setTxtWatcherFun();
        accountEditEventListnerFun();
    }

    private void accountEditEventListnerFun() {

        accntedit_back_btn.setOnClickListener(v -> finish());

        editaccnt_change_pass_btn.setOnClickListener(v -> {

            String old_pass_txt = editaccnt_old_pass.getEditText().getText().toString();
            String new_pass_txt = editaccnt_new_pass.getEditText().getText().toString();
            String conf_new_pass_txt = editaccnt_conf_new_pass.getEditText().getText().toString();

            if (!new_pass_txt.equals(conf_new_pass_txt)) {
                editaccnt_new_pass.getEditText().setText("");
                editaccnt_conf_new_pass.getEditText().setText("");
                editaccnt_new_pass.setError("Both passwords don't match");
                editaccnt_new_pass.requestFocus();
            } else {
                AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(firebaseUser.getEmail()), old_pass_txt);
                Objects.requireNonNull(firebaseAuth.getCurrentUser()).reauthenticateAndRetrieveData(credential).addOnSuccessListener(authResult -> firebaseUser.updatePassword(new_pass_txt).addOnSuccessListener(aVoid -> {
                                    Toast.makeText(AccountEditActivity.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), AccountsettingsActivity.class));
                                })
                                .addOnFailureListener(e -> Toast.makeText(AccountEditActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()))
                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        editaccnt_update_email_btn.setOnClickListener(v -> {
            String curr_pass_txt = editaccnt_current_pass.getEditText().getText().toString();
            String new_email_txt = editaccnt_new_email.getEditText().getText().toString();
            String conf_new_email_txt = editaccnt_conf_new_email.getEditText().getText().toString();

            if (!new_email_txt.equals(conf_new_email_txt)) {
                editaccnt_new_email.getEditText().setText("");
                editaccnt_conf_new_email.getEditText().setText("");
                editaccnt_new_email.setError("Both passwords don't match");
                editaccnt_new_email.requestFocus();
            } else {
                AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(firebaseUser.getEmail()), curr_pass_txt);
                Objects.requireNonNull(firebaseAuth.getCurrentUser()).reauthenticateAndRetrieveData(credential).addOnSuccessListener(authResult -> firebaseUser.updateEmail(new_email_txt).addOnSuccessListener(aVoid -> db.collection("users")
                                        .document(firebaseUser.getUid())
                                        .update("emailid", new_email_txt).addOnSuccessListener(aVoid1 -> {
                                            Toast.makeText(AccountEditActivity.this, "New Email ID updated successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), AccountsettingsActivity.class));
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(AccountEditActivity.this, "Failed to update email due to : " + e.getMessage(), Toast.LENGTH_LONG).show()))
                                .addOnFailureListener(e -> Toast.makeText(AccountEditActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_LONG).show()))
                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

        });
    }

    private void accountEdit_setTxtWatcherFun() {
        Objects.requireNonNull(editaccnt_old_pass.getEditText()).addTextChangedListener(pass_textwactcher);
        Objects.requireNonNull(editaccnt_new_pass.getEditText()).addTextChangedListener(pass_textwactcher);
        Objects.requireNonNull(editaccnt_conf_new_pass.getEditText()).addTextChangedListener(pass_textwactcher);

        Objects.requireNonNull(editaccnt_current_pass.getEditText()).addTextChangedListener(email_textwactcher);
        Objects.requireNonNull(editaccnt_new_email.getEditText()).addTextChangedListener(email_textwactcher);
        Objects.requireNonNull(editaccnt_conf_new_email.getEditText()).addTextChangedListener(email_textwactcher);
    }

    private void getpagedataVisibleFun() {
        Intent getpagedataintent = getIntent();
        int getpagedata = getpagedataintent.getIntExtra("data_to_page", 0);

        if (getpagedata == 1) {
            edit_change_pass_lay.setVisibility(View.VISIBLE);
            update_email_lay.setVisibility(View.GONE);
            accntedit_toolbar_title.setText("Change / Reset Password");
        } else if (getpagedata == 2) {
            edit_change_pass_lay.setVisibility(View.GONE);
            update_email_lay.setVisibility(View.VISIBLE);
            accntedit_toolbar_title.setText("Update Email Account");
        }
    }

    private void acountEditInits() {
        accntedit_back_btn = findViewById(R.id.accntedit_back_btn);
        edit_change_pass_lay = findViewById(R.id.edit_change_pass_lay);
        update_email_lay = findViewById(R.id.edit_update_email_lay);
        accntedit_toolbar_title = findViewById(R.id.accntedit_toolbar_title);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        editaccnt_old_pass = findViewById(R.id.editaccnt_old_pass);
        editaccnt_new_pass = findViewById(R.id.editaccnt_new_pass);
        editaccnt_conf_new_pass = findViewById(R.id.editaccnt_conf_new_pass);
        editaccnt_change_pass_btn = findViewById(R.id.editaccnt_change_pass_btn);

        editaccnt_current_pass = findViewById(R.id.editaccnt_current_pass);
        editaccnt_new_email = findViewById(R.id.editaccnt_new_email);
        editaccnt_conf_new_email = findViewById(R.id.editaccnt_conf_new_email);
        editaccnt_update_email_btn = findViewById(R.id.editaccnt_update_email_btn);
    }
}