package com.leslie.cjpokeroddscalculator.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public abstract class EquityCalculatorViewModel extends ViewModel {
    public MutableLiveData<Integer> resDesc = new MutableLiveData<>();
    public MutableLiveData<int[]> selectedCard = new MutableLiveData<>(new int[]{1, 0});
    public MutableLiveData<double[][]> stats = new MutableLiveData<>();
}
