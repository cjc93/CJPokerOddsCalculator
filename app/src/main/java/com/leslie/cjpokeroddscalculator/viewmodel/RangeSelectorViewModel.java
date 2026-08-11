package com.leslie.cjpokeroddscalculator.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.leslie.cjpokeroddscalculator.util.GlobalStatic;

import java.util.List;
import java.util.Set;

public class RangeSelectorViewModel extends ViewModel {
    public MutableLiveData<int[]> selectedMatrixPosition;
    public MutableLiveData<List<List<Set<String>>>> matrixInput;

    public RangeSelectorViewModel(SavedStateHandle savedStateHandle) {
        this.selectedMatrixPosition = savedStateHandle.getLiveData("selectedMatrixPosition");
        this.matrixInput = savedStateHandle.getLiveData("matrixInput");
    }

    public void updateRangeSelector(List<List<Set<String>>> matrix) {
        List<List<Set<String>>> copiedMatrix = GlobalStatic.copyMatrix(matrix);

        if (copiedMatrix != null) {
            matrixInput.setValue(copiedMatrix);
        }

        selectedMatrixPosition.setValue(null);
    }
}
