package com.example.hrv.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class StepViewModel extends ViewModel {
    private final MutableLiveData<Integer> stepCount = new MutableLiveData<>(0);
    private final MutableLiveData<List<Boolean>> streakList = new MutableLiveData<>();

    public LiveData<Integer> getStepCount() {
        return stepCount;
    }

    public LiveData<List<Boolean>> getStreakList() {
        return streakList;
    }

    public void setStepCount(int steps) {
        stepCount.setValue(steps);
    }

    public void setStreakList(List<Boolean> list) {
        streakList.setValue(list);
    }
}
