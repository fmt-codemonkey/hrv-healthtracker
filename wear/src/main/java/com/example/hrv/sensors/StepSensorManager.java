package com.example.hrv.sensors;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.lifecycle.MutableLiveData;

import java.util.Calendar;

public class StepSensorManager implements SensorEventListener {

    private final SensorManager sensorManager;
    private final Sensor stepSensor;
    private final MutableLiveData<Integer> stepLiveData = new MutableLiveData<>(0);
    private final Context context;

    private boolean isDebugEmulator = android.os.Build.FINGERPRINT.contains("generic");


    public StepSensorManager(Context context) {
        this.context = context.getApplicationContext();
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    public void start() {
        if (stepSensor != null && !isDebugEmulator) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            simulateSteps(); // fake step pump
        }
    }

    private void simulateSteps() {
        new Thread(() -> {
            try {
                int fakeStep = 1000;
                while (true) {
                    Thread.sleep(3000); // every 3 seconds
                    fakeStep += (int)(Math.random() * 20); // simulate walking
                    simulateSensorValue(fakeStep);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void simulateSensorValue(int totalSteps) {
        SharedPreferences prefs = context.getSharedPreferences("step_data", Context.MODE_PRIVATE);
        long lastSavedDate = prefs.getLong("last_saved_date", -1);
        int offset = prefs.getInt("step_offset", -1);

        long today = getTodayStartMillis();
        SharedPreferences.Editor editor = prefs.edit();

        if (lastSavedDate != today || offset == -1) {
            editor.putInt("step_offset", totalSteps);
            editor.putLong("last_saved_date", today);
            editor.apply();
            offset = totalSteps;
        }

        int todaySteps = totalSteps - offset;
        stepLiveData.postValue(Math.max(todaySteps, 0));
    }



    public void stop() {
        sensorManager.unregisterListener(this);
    }

    public MutableLiveData<Integer> getStepLiveData() {
        return stepLiveData;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int totalSteps = (int) event.values[0];

            SharedPreferences prefs = context.getSharedPreferences("step_data", Context.MODE_PRIVATE);
            long lastSavedDate = prefs.getLong("last_saved_date", -1);
            int offset = prefs.getInt("step_offset", -1);

            long today = getTodayStartMillis();
            SharedPreferences.Editor editor = prefs.edit();

            // If new day, reset offset
            if (lastSavedDate != today || offset == -1) {
                editor.putInt("step_offset", totalSteps);
                editor.putLong("last_saved_date", today);
                editor.apply();
                offset = totalSteps;
            }

            int todaySteps = totalSteps - offset;
            stepLiveData.setValue(Math.max(todaySteps, 0));
        }
    }

    private long getTodayStartMillis() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
