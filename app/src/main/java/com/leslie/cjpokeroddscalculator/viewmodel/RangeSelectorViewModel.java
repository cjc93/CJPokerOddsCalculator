package com.leslie.cjpokeroddscalculator.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Set;

public class RangeSelectorViewModel extends ViewModel {
    public MutableLiveData<int[]> selectedMatrixPosition = new MutableLiveData<>();
    public MutableLiveData<List<List<Set<String>>>> matrixInput = new MutableLiveData<>();
}
