package com.leslie.cjpokeroddscalculator.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Objects;


public class AddPresetHandRangeFragment extends DialogFragment {
    static AddPresetHandRangeFragment newInstance(ArrayList<String> rangeNames) {
        AddPresetHandRangeFragment f = new AddPresetHandRangeFragment();

        Bundle args = new Bundle();
        args.putStringArrayList("rangeNames", rangeNames);
        f.setArguments(args);

        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        assert getArguments() != null;
        ArrayList<String> rangeNames = getArguments().getStringArrayList("rangeNames");

        EditText editTextField = new EditText(this.getContext());
        editTextField.setHint("Enter Range Name");
        editTextField.requestFocus();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Enter new range name to save this preset hand range")
            .setView(editTextField)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String rangeName = editTextField.getText().toString();
                assert rangeNames != null;
                if (rangeNames.contains(rangeName)) {
                    dialog.setMessage("This range name is already in use, please enter a different range name");
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("range_name", rangeName);
                    requireActivity().getSupportFragmentManager().setFragmentResult("add_preset_hand_range", bundle);
                    dialog.dismiss();
                }
            });
        });

        Objects.requireNonNull(dialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }
}