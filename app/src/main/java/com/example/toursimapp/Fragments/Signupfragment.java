package com.example.toursimapp.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.toursimapp.AllActivities.Enterdetails;
import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.Models.usermodels;
import com.example.toursimapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Signupfragment extends Fragment {

    private TextInputLayout sign_firstname, sign_lastname, sign_email, sign_pass, sign_conf_pass;
    private String fullname_txt;
    private String firstname_txt;
    private String lastname_txt;
    private String email_txt;
    private Button sign_btn;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private usermodels musermodels;
    private GoogleSignInClient googleSignInClient;
    private SignInButton sign_not_basic_gsign_btn;
    private boolean isUser;

    public Signupfragment() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        new Functions().checkTheme(requireContext());
        View v = inflater.inflate(R.layout.fragment_signupfragment, container, false);

        signUpFragInits(v);
        sign_btn.setOnClickListener(v1 -> {

            ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            if (activeNetwork == null || !activeNetwork.isConnected() || !activeNetwork.isAvailable()) {
                new Functions().no_internet_dialog(getActivity());
            } else {
                signUpBtnProcess();
            }
        });
        sign_not_basic_gsign_btn.setOnClickListener(v1 -> {

            ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            if (activeNetwork == null || !activeNetwork.isConnected() || !activeNetwork.isAvailable()) {
                new Functions().no_internet_dialog(getActivity());
            } else {
                SharedPreferences.Editor editor = getContext().getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
                editor.putBoolean("isUser", false);
                editor.apply();

                creategooglerequest();
                reg_gsign_process();
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            if (task.isSuccessful()) {
                GoogleSignInAccount account = task.getResult();
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } else {
                Toast.makeText(getContext(), "Google Sign In Cancelled !!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void reg_gsign_process() {
        Intent googleintent = googleSignInClient.getSignInIntent();
        startActivityForResult(googleintent, 1001);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential gcredential = GoogleAuthProvider.getCredential(idToken, null);
        if (firebaseAuth.getCurrentUser() != null) {
            firebaseAuth.getCurrentUser().linkWithCredential(gcredential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                if (user != null) {
                                    String userid = user.getUid();
                                    String uemail = user.getEmail();
                                    String uname;
                                    if (user.getDisplayName() == null) {
                                        uname = String.valueOf(uemail.charAt(0)).toUpperCase();
                                    } else {
                                        uname = user.getDisplayName();
                                    }

                                    Log.e("user_name", "Username is : " + uname);

                                    Map<String, Object> reg_user_details = new HashMap<>();
                                    reg_user_details.put("fullname", uname);
                                    reg_user_details.put("emailid", uemail);
                                    reg_user_details.put("userid", userid);

                                    db.collection("users")
                                            .document(userid)
                                            .update(reg_user_details);

                                    SharedPreferences.Editor editor = requireContext().getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
                                    editor.putBoolean("log_status", false);
                                    editor.putBoolean("sign_status", true);
                                    editor.putBoolean("isGoogle", true);
                                    editor.apply();

                                    startActivity(new Intent(getContext(), Enterdetails.class));
                                }
                            }
                        } else {
                            Toast.makeText(getContext(), "Sign Up Failed as : " + task.getException().getMessage() + " !!", Toast.LENGTH_SHORT).show();
                            googleSignInClient.revokeAccess();
                        }
                    });
        }
    }

    private void creategooglerequest() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions);
    }

    private void signUpBtnProcess() {

        SharedPreferences.Editor edit = requireContext().getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
        edit.putBoolean("isUser", false);
        edit.apply();

        if (sign_firstname.getEditText() != null && sign_lastname.getEditText() != null && sign_email.getEditText() != null
                && sign_pass.getEditText() != null && sign_conf_pass.getEditText() != null) {

            firstname_txt = sign_firstname.getEditText().getText().toString();
            lastname_txt = sign_lastname.getEditText().getText().toString();
            email_txt = sign_email.getEditText().getText().toString();
            String sign_pass_txt = sign_pass.getEditText().getText().toString();
            String conf_pass_txt = sign_conf_pass.getEditText().getText().toString();

            if (firstname_txt.isEmpty()) {
                sign_firstname.getEditText().setError("Please enter the Firstname");
                Objects.requireNonNull(sign_firstname.getEditText()).requestFocus();
            } else if (lastname_txt.isEmpty()) {
                sign_lastname.getEditText().setError("Please enter the Lastname");
                Objects.requireNonNull(sign_lastname.getEditText()).requestFocus();
            } else if (email_txt.isEmpty()) {
                sign_email.getEditText().setError("Please enter the email ID");
                Objects.requireNonNull(sign_email.getEditText()).requestFocus();
            } else if (sign_pass_txt.isEmpty()) {
                sign_pass.getEditText().setError(("Please enter the password"));
                Objects.requireNonNull(sign_pass.getEditText()).requestFocus();
            } else if (conf_pass_txt.isEmpty()) {
                sign_conf_pass.getEditText().setError("Please enter the confirm password");
                Objects.requireNonNull(sign_conf_pass.getEditText()).requestFocus();
            } else if (!sign_pass_txt.equals(conf_pass_txt)) {
                sign_pass.getEditText().setText("");
                Objects.requireNonNull(sign_conf_pass.getEditText()).setText("");
                Objects.requireNonNull(sign_pass.getEditText()).setError("Passwords don't match !!!");
                sign_pass.getEditText().requestFocus();
            } else {

                if (isUser) {
                    AuthCredential credential = EmailAuthProvider.getCredential(email_txt, sign_pass_txt);
                    Objects.requireNonNull(firebaseAuth.getCurrentUser()).linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                fullname_txt = firstname_txt + " " + lastname_txt;
                                musermodels = new usermodels(fullname_txt, email_txt, Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
                                SharedPreferences.Editor editor = requireContext().getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
                                editor.putBoolean("sign_status", true);
                                editor.putBoolean("log_status", false);
                                editor.putBoolean("isGoogle", false);
                                editor.apply();

                                Map<String, Object> reg_user_details = new HashMap<>();
                                reg_user_details.put("fullname", fullname_txt);
                                reg_user_details.put("emailid", email_txt);
                                reg_user_details.put("userid", firebaseAuth.getCurrentUser().getUid());

                                db.collection("users")
                                        .document(firebaseAuth.getCurrentUser().getUid())
                                        .update(reg_user_details);
                                startActivity(new Intent(getContext(), Enterdetails.class));
                            } else {
                                SharedPreferences.Editor editor = requireContext().getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
                                editor.putBoolean("sign_status", false);
                                editor.apply();
                                String error_msg = Objects.requireNonNull(task.getException()).getMessage();
                                if (error_msg != null && error_msg.contains("The email address is already in use by another account")) {
                                    Toast.makeText(getContext(), "Email ID already registered !!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), error_msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(email_txt, sign_pass_txt).addOnCompleteListener(requireActivity(), task -> {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getContext(), "Registration Error due to : " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                            SharedPreferences.Editor editor = requireContext().getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
                            editor.putBoolean("sign_status", false);
                            editor.apply();
                        } else {
                            fullname_txt = firstname_txt + " " + lastname_txt;
                            musermodels = new usermodels(fullname_txt, email_txt, Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
                            SharedPreferences.Editor editor = requireContext().getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
                            editor.putBoolean("sign_status", true);
                            editor.putBoolean("log_status", false);
                            editor.putBoolean("isGoogle", false);
                            editor.apply();

                            db.collection("users")
                                    .document(firebaseAuth.getCurrentUser().getUid())
                                    .set(musermodels)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Registration Successful..", Toast.LENGTH_SHORT).show());
                            startActivity(new Intent(getContext(), Enterdetails.class));
                        }
                    });
                }
            }

        }
    }

    private void signUpFragInits(@NotNull View v) {
        sign_firstname = v.findViewById(R.id.sign_firstname);
        sign_lastname = v.findViewById(R.id.sign_lastname);
        sign_email = v.findViewById(R.id.sign_email);
        sign_pass = v.findViewById(R.id.sign_pass);
        sign_conf_pass = v.findViewById(R.id.sign_conf_pass);
        sign_btn = v.findViewById(R.id.sign_btn);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        View sign_not_basic_lay = v.findViewById(R.id.sign_not_basic_lay);
        sign_not_basic_gsign_btn = v.findViewById(R.id.sign_not_basic_gsign_btn);

        SharedPreferences prefs_1 = requireContext().getSharedPreferences("app_data", Activity.MODE_PRIVATE);
        isUser = prefs_1.getBoolean("isUser", false);
        if (isUser) {
            sign_not_basic_lay.setVisibility(View.VISIBLE);
        } else {
            sign_not_basic_lay.setVisibility(View.GONE);
        }
    }
}