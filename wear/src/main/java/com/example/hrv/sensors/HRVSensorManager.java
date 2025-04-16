package com.example.hrv.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class HRVSensorManager implements SensorEventListener {

    private final SensorManager sensorManager;
    private final Sensor heartRateSensor;

    private final List<Long> beatTimestamps = new ArrayList<>();

    private final MutableLiveData<Integer> heartRateLiveData = new MutableLiveData<>();
    private final MutableLiveData<Double> hrvLiveData = new MutableLiveData<>();

    public HRVSensorManager(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
    }

    public void start() {
        if (heartRateSensor != null) {
            sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void stop() {
        sensorManager.unregisterListener(this);
        beatTimestamps.clear();
    }

    public MutableLiveData<Integer> getHeartRateLiveData() {
        return heartRateLiveData;
    }

    public MutableLiveData<Double> getHrvLiveData() {
        return hrvLiveData;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            int bpm = Math.round(event.values[0]);
            heartRateLiveData.setValue(bpm);

            long timestamp = System.currentTimeMillis();
            beatTimestamps.add(timestamp);

            // Keep only last 10 beats
            if (beatTimestamps.size() > 10) {
                beatTimestamps.remove(0);
            }

            if (beatTimestamps.size() >= 2) {
                // Calculate RR intervals
                List<Long> rrIntervals = new ArrayList<>();
                for (int i = 1; i < beatTimestamps.size(); i++) {
                    rrIntervals.add(beatTimestamps.get(i) - beatTimestamps.get(i - 1));
                }

                // Compute RMSSD (Root Mean Square of Successive Differences)
                double sum = 0;
                for (int i = 1; i < rrIntervals.size(); i++) {
                    long diff = rrIntervals.get(i) - rrIntervals.get(i - 1);
                    sum += diff * diff;
                }

                double rmssd = Math.sqrt(sum / (rrIntervals.size() - 1));
                hrvLiveData.setValue(rmssd);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Optional: handle sensor accuracy changes
    }
}
