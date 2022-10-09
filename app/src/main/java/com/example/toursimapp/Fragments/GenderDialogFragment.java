package com.example.toursimapp.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class GenderDialogFragment extends DialogFragment {

    int selected_position;
    SingleChoiceListner mListner;

    public GenderDialogFragment(int selected_position) {
        this.selected_position = selected_position;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListner = (SingleChoiceListner) context;
        } catch (Exception e) {
            throw new ClassCastException(getActivity().toString() + "SingleChoiceListner must be implemented");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String[] gender_list = new String[]{"Select", "Male", "Female"};
        builder.setTitle("Select Your Gender")
                .setSingleChoiceItems(gender_list, selected_position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected_position = which;
                    }
                })
                .setPositiveButton("Select", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (selected_position == 0) {
                            Toast.makeText(getContext(), "Please select your respective gender !!", Toast.LENGTH_SHORT).show();
                        } else {
                            mListner.onPostiveButtonClicked(gender_list, selected_position);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListner.onNegativeButtonClicked();
                    }
                });

        return builder.create();
    }

    public interface SingleChoiceListner {
        void onPostiveButtonClicked(String[] list, int selected_position);

        void onNegativeButtonClicked();
    }
}
