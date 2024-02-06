package com.leslie.cjpokeroddscalculator.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;


public class AddPresetHandRangeFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        EditText editTextField = new EditText(this.getContext());
        editTextField.setHint("Enter Range Name");
        editTextField.requestFocus();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(editTextField)
            .setPositiveButton("Save", (dialog, id) -> {
                Bundle bundle = new Bundle();
                bundle.putString("range_name", editTextField.getText().toString());
                requireActivity().getSupportFragmentManager().setFragmentResult("add_preset_hand_range", bundle);
            })
            .setNegativeButton("Cancel", (dialog, id) -> {});
        Dialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }
}