package com.example.toursimapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.toursimapp.AllActivities.Enterdetails;
import com.example.toursimapp.AllActivities.MainActivity;
import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.Models.usermodels;
import com.example.toursimapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("all")
public class Loginfragment extends Fragment {

    private final static int RC_SIGN_IN = 1;
    private final String email_expn =
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
    private TextInputLayout log_email, log_pass;
    private TextView log_forget_pass, or_txt;
    private String log_mail, log_upass;
    private Button login_btn, guest_btn;
    private SignInButton signInButton;
    private usermodels musermodels;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private View login_basic_lay;
    private FirebaseAuth.AuthStateListener mAuthStateListner;
    private GoogleSignInClient googleSignInClient;
    private FirebaseFirestore db;
    private boolean isUser;

    public Loginfragment() {
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        new Functions().checkTheme(requireContext());
        View v = inflater.inflate(R.layout.fragment_loginfragment, container, false);

        loginFragInits(v);
        elementtransition();
        loginFragEventListnerMethods();

        return v;
    }

    private void loginFragInits(@NotNull View v) {

        log_email = v.findViewById(R.id.log_email);
        log_pass = v.findViewById(R.id.log_pass);
        log_forget_pass = v.findViewById(R.id.log_forget_pass);
        login_btn = v.findViewById(R.id.login_btn);
        guest_btn = v.findViewById(R.id.guest_btn);
        signInButton = v.findViewById(R.id.sign_in_button);
        or_txt = v.findViewById(R.id.or_txt);
        login_basic_lay = v.findViewById(R.id.login_basic_lay);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        SharedPreferences prefs_1 = requireContext().getSharedPreferences("app_data", Activity.MODE_PRIVATE);
        isUser = prefs_1.getBoolean("isUser", false);
        if (isUser) {
            login_basic_lay.setVisibility(View.GONE);
        }
    }

    private void loginFragEventListnerMethods() {

        login_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                ConnectivityManager cm = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork == null || !activeNetwork.isConnected() || !activeNetwork.isAvailable()) {
                    new Functions().no_internet_dialog(getActivity());
                } else {
                    log_mail = Objects.requireNonNull(log_email.getEditText()).getText().toString().trim();
                    log_upass = Objects.requireNonNull(log_pass.getEditText()).getText().toString().trim();

                    if (log_mail.isEmpty()) {
                        log_email.getEditText().setError("Enter your Email ID");
                        log_email.getEditText().requestFocus();
                    } else if (log_upass.isEmpty()) {
                        log_pass.getEditText().setError("Enter your password");
                        log_pass.getEditText().requestFocus();
                    } else {
                        if (!log_mail.matches(email_expn)) {
                            log_email.getEditText().setError("Incorrect Email ID pattern!!!");
                            log_email.getEditText().requestFocus();
                        } else {

                            if (isUser) {
                                final Dialog dialog = new Dialog(getActivity());
                                dialog.setContentView(R.layout.delete_accnt_dialog);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
                                TextView delete_accnt_header = dialog.findViewById(R.id.delete_accnt_header);
                                TextView delete_accnt_sub_title = dialog.findViewById(R.id.delete_accnt_sub_title);
                                TextView accnt_delete_cancel_btn = dialog.findViewById(R.id.accnt_delete_cancel_btn);
                                TextView accnt_process_btn = dialog.findViewById(R.id.accnt_process_btn);

                                delete_accnt_header.setText("Caution !!!");
                                delete_accnt_sub_title.setText("Are you sure that you want to login ? \n\nIf you login with this email ID, your guest account will be deleted permanently !! \nPlease proceed carefully.");
                                accnt_process_btn.setText("PROCEED");

                                accnt_delete_cancel_btn.setOnClickListener(v1 -> {
                                    dialog.cancel();
                                });

                                accnt_process_btn.setOnClickListener(v2 -> {

                                    db.collection("users")
                                            .document(firebaseUser.getUid())
                                            .collection("favourites")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                                        documentSnapshot.getReference().delete();
                                                    }
                                                }
                                            });

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            db.collection("users")
                                                    .document(firebaseUser.getUid())
                                                    .delete()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {

                                                                firebaseAuth.signOut();
                                                                firebaseUser.delete();

                                                                firebaseAuth.signInWithEmailAndPassword(log_mail, log_upass).addOnCompleteListener(requireActivity(), task1 -> {
                                                                    if (!task1.isSuccessful()) {
                                                                        Toast.makeText(getContext(), "Login Error. Please try again!!", Toast.LENGTH_LONG).show();
                                                                    } else {
                                                                        SharedPreferences.Editor editor1 = requireContext().getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
                                                                        editor1.putBoolean("log_status", true);
                                                                        editor1.apply();
                                                                        dialog.dismiss();

                                                                        new Handler().postDelayed(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                SharedPreferences.Editor editor = requireContext().getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
                                                                                editor.putBoolean("isUser", false);
                                                                                editor.apply();

                                                                                startActivity(new Intent(getContext(), MainActivity.class));
                                                                            }
                                                                        }, 1000);
                                                                    }
                                                                });
                                                            } else {
                                                                Toast.makeText(getContext(), "Failed to Sign-out...!!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }, 500);
                                });

                                dialog.show();
                            } else {
                                firebaseAuth.signInWithEmailAndPassword(log_mail, log_upass).addOnCompleteListener(requireActivity(), task -> {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Login Error. Please try again!!", Toast.LENGTH_LONG).show();
                                    } else {
                                        SharedPreferences.Editor editor1 = requireContext().getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
                                        editor1.putBoolean("log_status", true);
                                        editor1.apply();

                                        startActivity(new Intent(getContext(), MainActivity.class));
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                if (activeNetwork == null || !activeNetwork.isConnected() || !activeNetwork.isAvailable()) {
                    new Functions().no_internet_dialog(getActivity());
                } else {
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("isUser", false);
                    editor.apply();

                    creategooglerequest();
                    googlesignin();
                }
            }
        });

        guest_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                if (activeNetwork == null || !activeNetwork.isConnected() || !activeNetwork.isAvailable()) {
                    new Functions().no_internet_dialog(getActivity());
                } else {
                    SharedPreferences.Editor editor = requireContext().getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("isUser", false);
                    editor.apply();

                    firebaseAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            String userid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                            String fullname = "guest#" + userid.substring(0, 5);
                            usermodels musermodels1 = new usermodels(fullname, "", userid);
                            db.collection("users")
                                    .document(userid)
                                    .set(musermodels1)
                                    .addOnSuccessListener(aVoid -> {
                                        SharedPreferences.Editor editor1 = requireContext().getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
                                        editor1.putBoolean("log_status", true);
                                        editor1.apply();
                                        Toast.makeText(getContext(), "Guest account created..", Toast.LENGTH_SHORT).show();
                                    });
                            startActivity(new Intent(getContext(), MainActivity.class));
                        }
                    });
                }
            }
        });

        log_forget_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.email_dialog);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
                EditText email_txt = dialog.findViewById(R.id.email_txt);
                TextView cancel = dialog.findViewById(R.id.ecancel_btn);
                TextView send_email = dialog.findViewById(R.id.email_send_btn);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                send_email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                        if (activeNetwork == null || !activeNetwork.isConnected() || !activeNetwork.isAvailable()) {
                            Toast.makeText(getContext(), "Please check your internet connection and try again!!", Toast.LENGTH_SHORT).show();
                        } else {
                            String email_id;
                            email_id = email_txt.getText().toString();

                            if (email_id.isEmpty()) {
                                email_txt.setError("Please enter your email ID !!");
                                email_txt.requestFocus();
                            } else {
                                if (!email_id.matches(email_expn)) {
                                    email_txt.setError("Please enter a valid Email ID!!");
                                    email_txt.requestFocus();
                                } else {
                                    firebaseAuth.sendPasswordResetEmail(email_id).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    dialog.dismiss();
                                                    Toast.makeText(getContext(), "Email has been send to your email ID. Please check your email account.", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Email not send!! : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        }
                    }
                });

                dialog.show();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            if (task.isSuccessful()) {
                GoogleSignInAccount account = task.getResult();
                firebaseAuthWithGoogle(account.getIdToken());
            } else {
                Toast.makeText(getContext(), "Google Sign Error : " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void googlesignin() {
        Intent googleintent = googleSignInClient.getSignInIntent();
        startActivityForResult(googleintent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            if (Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getAdditionalUserInfo()).isNewUser()) {

                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                assert user != null;
                                String userid = user.getUid();
                                String uname = user.getDisplayName();
                                String uemail = user.getEmail();

                                musermodels = new usermodels(uname, uemail, userid);

                                db.collection("users")
                                        .document(userid)
                                        .set(musermodels)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getContext(), "Registeration Successful..", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                SharedPreferences.Editor editor = requireContext().getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
                                editor.putBoolean("sign_status", true);
                                editor.putBoolean("isGoogle", true);
                                editor.apply();

                                startActivity(new Intent(getContext(), Enterdetails.class));
                            } else {

                                SharedPreferences.Editor editor = requireContext().getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
                                editor.putBoolean("log_status", true);
                                editor.putBoolean("isGoogle", true);
                                editor.apply();

                                startActivity(new Intent(getContext(), MainActivity.class));
                            }

                        } else {
                            Toast.makeText(getContext(), "Authentication Failed!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void creategooglerequest() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions);
    }

    private void elementtransition() {

        log_email.setTranslationX(800);
        log_pass.setTranslationX(800);
        log_forget_pass.setTranslationX(800);
        login_btn.setTranslationX(800);
        signInButton.setTranslationX(800);
        or_txt.setTranslationX(800);
        guest_btn.setTranslationX(800);

        float ve = 0;
        log_email.setAlpha(ve);
        log_pass.setAlpha(ve);
        log_forget_pass.setAlpha(ve);
        login_btn.setAlpha(ve);
        signInButton.setAlpha(ve);
        or_txt.setAlpha(ve);
        guest_btn.setAlpha(ve);

        log_email.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        log_pass.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        log_forget_pass.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        login_btn.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
        signInButton.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
        or_txt.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
        guest_btn.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
    }

}