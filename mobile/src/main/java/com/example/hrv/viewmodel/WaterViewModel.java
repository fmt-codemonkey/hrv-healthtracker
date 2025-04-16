package com.example.hrv.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class WaterViewModel extends ViewModel {

    private final MutableLiveData<Integer> waterIntake = new MutableLiveData<>(0);
    private final MutableLiveData<List<Boolean>> streakList = new MutableLiveData<>();

    public LiveData<Integer> getWaterIntake() {
        return waterIntake;
    }

    public void setWaterIntake(int amount) {
        waterIntake.setValue(amount);
    }

    public void addWater(int ml) {
        Integer current = waterIntake.getValue() != null ? waterIntake.getValue() : 0;
        waterIntake.setValue(current + ml);
    }

    public LiveData<List<Boolean>> getStreakList() {
        return streakList;
    }

    public void setStreakList(List<Boolean> list) {
        streakList.setValue(list);
    }
}
