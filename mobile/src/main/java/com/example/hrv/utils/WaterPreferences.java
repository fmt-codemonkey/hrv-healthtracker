package com.example.hrv.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class WaterPreferences {

    private static final String PREF_NAME = "water_goal_prefs";
    private static final String KEY_GOAL = "daily_goal";
    private static final int DEFAULT_GOAL = 3000;

    public static void setGoal(Context context, int ml) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_GOAL, ml).apply();
    }

    public static int getGoal(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_GOAL, DEFAULT_GOAL);
    }
}
