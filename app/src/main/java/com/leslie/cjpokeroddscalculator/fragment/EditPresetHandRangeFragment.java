package com.leslie.cjpokeroddscalculator.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class EditPresetHandRangeFragment extends DialogFragment {
    static EditPresetHandRangeFragment newInstance(String rangeName) {
        EditPresetHandRangeFragment f = new EditPresetHandRangeFragment();

        Bundle args = new Bundle();
        args.putString("rangeName", rangeName);
        f.setArguments(args);

        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        assert getArguments() != null;
        String rangeName = getArguments().getString("rangeName");

        EditText editTextField = new EditText(this.getContext());
        editTextField.setText(rangeName);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Rename or delete this preset hand range")
            .setView(editTextField)
            .setPositiveButton("Rename", (dialog, id) -> {
                Bundle bundle = new Bundle();
                bundle.putString("old_range_name", rangeName);
                bundle.putString("new_range_name", editTextField.getText().toString());
                requireActivity().getSupportFragmentManager().setFragmentResult("rename_preset_hand_range", bundle);
            })
            .setNegativeButton("Cancel", (dialog, id) -> {})
            .setNeutralButton("Delete", (dialog, id) -> {
                Bundle bundle = new Bundle();
                bundle.putString("range_name", rangeName);
                requireActivity().getSupportFragmentManager().setFragmentResult("delete_preset_hand_range", bundle);
            });
        return builder.create();
    }
}
