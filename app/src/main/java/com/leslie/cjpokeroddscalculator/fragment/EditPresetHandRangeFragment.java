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

public class EditPresetHandRangeFragment extends DialogFragment {
    static EditPresetHandRangeFragment newInstance(String currentRangeName, ArrayList<String> rangeNames) {
        EditPresetHandRangeFragment f = new EditPresetHandRangeFragment();

        Bundle args = new Bundle();
        args.putString("CurrentRangeName", currentRangeName);
        args.putStringArrayList("RangeNames", rangeNames);
        f.setArguments(args);

        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        assert getArguments() != null;
        String currentRangeName = getArguments().getString("CurrentRangeName");
        ArrayList<String> rangeNames = getArguments().getStringArrayList("RangeNames");

        EditText editTextField = new EditText(this.getContext());
        editTextField.setText(currentRangeName);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Rename or delete this preset hand range")
            .setView(editTextField)
            .setPositiveButton("Rename", null)
            .setNegativeButton("Cancel", null)
            .setNeutralButton("Delete", (dialog, id) -> {
                Bundle bundle = new Bundle();
                bundle.putString("range_name", currentRangeName);
                requireActivity().getSupportFragmentManager().setFragmentResult("delete_preset_hand_range", bundle);
            });

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String newRangeName = editTextField.getText().toString();
                assert rangeNames != null;
                if (rangeNames.contains(newRangeName)) {
                    dialog.setMessage("This range name is already in use, please enter a different range name");
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("old_range_name", currentRangeName);
                    bundle.putString("new_range_name", newRangeName);
                    requireActivity().getSupportFragmentManager().setFragmentResult("rename_preset_hand_range", bundle);
                    dialog.dismiss();
                }
            });
        });

        return dialog;
    }
}
