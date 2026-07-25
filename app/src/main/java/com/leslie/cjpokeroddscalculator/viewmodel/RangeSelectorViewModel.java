package com.leslie.cjpokeroddscalculator.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RangeSelectorViewModel extends ViewModel {
    public MutableLiveData<int[]> selectedMatrixPosition = new MutableLiveData<>();
}
