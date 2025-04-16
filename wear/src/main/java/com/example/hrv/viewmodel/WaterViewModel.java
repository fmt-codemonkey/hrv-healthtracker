package com.example.hrv.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WaterViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> waterIntake = new MutableLiveData<>(0);
    private final MutableLiveData<List<Boolean>> streakList = new MutableLiveData<>();
    private final SharedPreferences prefs;

    private static final String PREF_NAME = "water_data";
    private static final String WATER_KEY = "water_";
    private static final String DAY_KEY = "day_";
    private static final int GOAL_ML = 2000;

    public WaterViewModel(@NonNull Application application) {
        super(application);
        prefs = application.getSharedPreferences(PREF_NAME, Application.MODE_PRIVATE);
        loadTodayWater();
        loadStreak();
    }

    public LiveData<Integer> getWaterIntake() {
        return waterIntake;
    }

    public LiveData<List<Boolean>> getStreakList() {
        return streakList;
    }

    public void addWater(int amount) {
        String todayKey = WATER_KEY + getDayKey();
        int current = prefs.getInt(todayKey, 0);
        current += amount;

        prefs.edit().putInt(todayKey, current).apply();
        waterIntake.setValue(current);

        // update streak if goal met
        if (current >= GOAL_ML) {
            prefs.edit().putBoolean(DAY_KEY + getDayIndex(), true).apply();
        }

        loadStreak();
    }

    private void loadTodayWater() {
        int todayAmount = prefs.getInt(WATER_KEY + getDayKey(), 0);
        waterIntake.setValue(todayAmount);
    }

    private void loadStreak() {
        List<Boolean> streaks = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            streaks.add(prefs.getBoolean(DAY_KEY + i, false));
        }
        streakList.setValue(streaks);
    }

    private String getDayKey() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return year + "_" + month + "_" + day;
    }

    private int getDayIndex() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
    }
}
