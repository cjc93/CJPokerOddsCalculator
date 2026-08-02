package com.leslie.cjpokeroddscalculator.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.leslie.cjpokeroddscalculator.util.GlobalStatic;

import java.util.List;
import java.util.Set;

public class RangeSelectorViewModel extends ViewModel {
    public MutableLiveData<int[]> selectedMatrixPosition = new MutableLiveData<>();
    public MutableLiveData<List<List<Set<String>>>> matrixInput = new MutableLiveData<>();
    public boolean initialized = false;

    public void initializeMatrix(List<List<Set<String>>> matrix) {
        if (!initialized) {
            updateRangeSelector(matrix);
            initialized = true;
        }
    }

    public void updateRangeSelector(List<List<Set<String>>> matrix) {
        List<List<Set<String>>> copiedMatrix = GlobalStatic.copyMatrix(matrix);

        if (copiedMatrix != null) {
            matrixInput.setValue(copiedMatrix);
        }

        selectedMatrixPosition.setValue(null);
    }
}
