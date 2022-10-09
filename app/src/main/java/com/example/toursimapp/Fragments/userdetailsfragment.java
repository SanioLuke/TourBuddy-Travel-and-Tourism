package com.example.toursimapp.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.R;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class userdetailsfragment extends Fragment {

    private TextView udetails_skip_btn;
    private CountryCodePicker ccpicker;
    private TextInputLayout sign_phone, sign_dob;
    private Button ud_next;
    private RadioButton sign_male, sign_female;
    private RadioGroup gender_grp;
    private String sign_phone_txt, sign_dob_txt, cc, gender_txt;
    private final TextWatcher editextwactcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            sign_phone_txt = sign_phone.getEditText().getText().toString().trim();
            sign_phone_txt = sign_phone_txt.replaceAll("\\s+", "");
            sign_dob_txt = sign_dob.getEditText().getText().toString().trim();

            if (sign_male.isChecked() || sign_female.isChecked()) {
                ud_next.setEnabled(!sign_phone_txt.isEmpty() && !sign_dob_txt.isEmpty() && sign_phone_txt.length() == 10);
            } else {
                ud_next.setEnabled(false);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public userdetailsfragment() {
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        new Functions().checkTheme(getContext());
        View v = inflater.inflate(R.layout.fragment_userdetailsfragment, container, false);

        uDetailsFragInits(v);
        textValuesSet_basicInits_txtWatcherMethod();
        uPrefsFragEventListnerMethod();

        return v;
    }

    private void uDetailsFragInits(@NotNull View v) {
        udetails_skip_btn = v.findViewById(R.id.udetails_skip_btn);
        ccpicker = v.findViewById(R.id.ccpicker);
        sign_phone = v.findViewById(R.id.sign_phone);
        sign_dob = v.findViewById(R.id.sign_dob);
        ud_next = v.findViewById(R.id.ud_next);
        sign_male = v.findViewById(R.id.sign_male);
        sign_female = v.findViewById(R.id.sign_female);
        gender_grp = v.findViewById(R.id.gender_grp);
    }

    private void textValuesSet_basicInits_txtWatcherMethod() {

        SharedPreferences prefs_2 = requireContext().getSharedPreferences("sign_details", Activity.MODE_PRIVATE);
        String pref_cc = prefs_2.getString("sign_coun_code_txt", ccpicker.getDefaultCountryCode());
        String sign_pref_phone_txt = prefs_2.getString("sign_ph_txt", null);
        String sign_pref_dob_txt = prefs_2.getString("sign_dob_txt", null);
        String pref_gender_txt = prefs_2.getString("sign_gen_txt", null);

        if (pref_cc != null && sign_pref_phone_txt != null && sign_pref_dob_txt != null && pref_gender_txt != null) {
            ud_next.setEnabled(true);
        }
        if (pref_cc != null) {
            ccpicker.setCountryForPhoneCode(Integer.parseInt(pref_cc));
        }
        if (sign_pref_phone_txt != null && sign_phone.getEditText() != null) {
            sign_pref_phone_txt = sign_pref_phone_txt.replaceAll("\\s+", "");
            sign_phone.getEditText().setText(sign_pref_phone_txt);
        }
        if (sign_pref_dob_txt != null && sign_dob.getEditText() != null) {
            sign_dob.getEditText().setText(sign_pref_dob_txt);
        }
        if (pref_gender_txt != null) {
            if (pref_gender_txt.equals("Male")) {
                sign_male.setChecked(true);
            } else if (pref_gender_txt.equals("Female")) {
                sign_female.setChecked(true);
            }
        }

        sign_phone_txt = sign_phone.getEditText().getText().toString().trim();
        sign_phone_txt = sign_phone_txt.replaceAll("\\s+", "");
        sign_dob_txt = sign_dob.getEditText().getText().toString().trim();
        ccpicker.registerCarrierNumberEditText(sign_phone.getEditText());
        cc = ccpicker.getSelectedCountryCodeWithPlus().trim();

        sign_phone.getEditText().addTextChangedListener(editextwactcher);
        sign_dob.getEditText().addTextChangedListener(editextwactcher);

    }

    private void uPrefsFragEventListnerMethod() {

        gender_grp.setOnCheckedChangeListener((group, checkedId) -> ud_next.setEnabled(!sign_phone_txt.isEmpty() && !sign_dob_txt.isEmpty() && sign_phone_txt.length() == 10));

        Objects.requireNonNull(sign_dob.getEditText()).setOnClickListener(v1 -> {

            Calendar cal = Calendar.getInstance();
            Date s = Calendar.getInstance().getTime();
            int getyear = cal.get(Calendar.YEAR);
            int getmonth = cal.get(Calendar.MONTH);
            int getday = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                month = month + 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                sign_dob.getEditText().setText(date);
            }, getyear, getmonth, getday);
            datePickerDialog.show();
        });

        udetails_skip_btn.setOnClickListener(v12 -> {
            SharedPreferences.Editor editor = getContext().getSharedPreferences("sign_details", Context.MODE_PRIVATE).edit();
            editor.putString("sign_page_name", "ulocation");
            editor.apply();
            assert getFragmentManager() != null;
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            transaction.replace(R.id.details_container, new ulocationfragment());
            transaction.commit();
        });

        ud_next.setOnClickListener(v13 -> {

            cc = ccpicker.getSelectedCountryCodeWithPlus().trim();
            int gen_id = gender_grp.getCheckedRadioButtonId();
            if (gen_id == R.id.sign_male) {
                gender_txt = "Male";
            } else {
                gender_txt = "Female";
            }

            SharedPreferences.Editor editor = getContext().getSharedPreferences("sign_details", Context.MODE_PRIVATE).edit();
            editor.putString("sign_coun_code_txt", cc);
            editor.putString("sign_ph_txt", sign_phone_txt);
            editor.putString("sign_dob_txt", sign_dob_txt);
            editor.putString("sign_gen_txt", gender_txt);
            editor.putString("sign_page_name", "ulocation");
            editor.apply();
            assert getFragmentManager() != null;
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            transaction.replace(R.id.details_container, new ulocationfragment());
            transaction.commit();
        });

    }

}