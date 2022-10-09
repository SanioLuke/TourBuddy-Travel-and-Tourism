package com.example.toursimapp.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Arrays;

public class PrefDialogFragment extends DialogFragment {

    private final String[] prefs_list = new String[]{"Family", "Friends", "Honeymoon", "Religious", "Solo"};
    private final ArrayList<String> selected_this_array = new ArrayList<>();
    onMultiChoiceListner multiChoiceListner;
    private ArrayList<String> prefs_array = new ArrayList<>();
    private boolean[] selected_prefs_list = new boolean[]{};

    public PrefDialogFragment(boolean[] selected_prefs_list, ArrayList<String> prefs_array) {
        this.selected_prefs_list = selected_prefs_list;
        this.prefs_array = prefs_array;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            multiChoiceListner = (onMultiChoiceListner) context;
        } catch (Exception e) {
            throw new ClassCastException(getActivity().toString() + "onMultiChoiceListner must be implemented");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        ArrayList<String> all_prefs_array = new ArrayList<>(Arrays.asList(prefs_list));
        for (int ch1 = 0; ch1 < all_prefs_array.size(); ch1++) {
            for (int ch2 = 0; ch2 < prefs_array.size(); ch2++) {
                if (all_prefs_array.get(ch1).equals(prefs_array.get(ch2))) {
                    selected_this_array.add(prefs_list[ch1]);
                }
            }
        }

        builder.setTitle("Select your Preferences List")
                .setMultiChoiceItems(prefs_list, selected_prefs_list, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {

                        if (isChecked) {
                            selected_this_array.add(prefs_list[which]);
                        } else {
                            selected_this_array.remove(prefs_list[which]);
                        }

                        Button positive = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                        positive.setEnabled(selected_this_array.size() > 0);
                    }
                })
                .setPositiveButton("Select", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        multiChoiceListner.onPositiveButtonClicked(prefs_list, selected_this_array);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        multiChoiceListner.onNegativeButtonClicked();
                    }
                });

        return builder.create();

    }

    public interface onMultiChoiceListner {
        void onPositiveButtonClicked(String[] list, ArrayList<String> selectedItemlist);

        void onNegativeButtonClicked();
    }
}
