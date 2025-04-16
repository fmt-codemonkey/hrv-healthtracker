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

public class StepViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> stepCount = new MutableLiveData<>(0);
    private final MutableLiveData<List<Boolean>> streakList = new MutableLiveData<>();

    private final SharedPreferences preferences;

    public StepViewModel(@NonNull Application application) {
        super(application);
        preferences = application.getSharedPreferences("step_data", Application.MODE_PRIVATE);
        loadStreak();
    }

    public void setStepCount(int steps) {
        stepCount.setValue(steps);
        checkAndUpdateStreak(steps);
    }

    public LiveData<Integer> getStepCount() {
        return stepCount;
    }

    public LiveData<List<Boolean>> getStreakList() {
        return streakList;
    }

    private void loadStreak() {
        List<Boolean> streaks = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            streaks.add(preferences.getBoolean("day_" + i, false));
        }
        streakList.setValue(streaks);
    }

    private void checkAndUpdateStreak(int steps) {
        int goal = 5000; // default step goal

        Calendar calendar = Calendar.getInstance();
        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 0 = Sunday

        boolean metGoal = steps >= goal;
        preferences.edit().putBoolean("day_" + dayIndex, metGoal).apply();

        loadStreak(); // refresh streak
    }
}
