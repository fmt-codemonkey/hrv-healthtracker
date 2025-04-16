package com.example.hrv.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HRVViewModel extends ViewModel {
    private final MutableLiveData<Double> hrvLiveData = new MutableLiveData<>();

    public void setHRV(double hrv) {
        hrvLiveData.setValue(hrv);
    }

    public LiveData<Double> getHRV() {
        return hrvLiveData;
    }
}
