package com.example.hrv.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hrv.R;
import com.example.hrv.data.WaterRepository;
import com.example.hrv.sync.HRVReceiver;
import com.example.hrv.sync.WaterReceiver;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends AppCompatActivity {

    private WaterReceiver waterReceiver;
    private HRVReceiver hrvReceiver;
    private WaterRepository repository;

    private TextView waterAmountTextView, totalTodayText;
    private TextView hrvValueTextView, hrvEmojiView, stressLabelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Water UI
        waterAmountTextView = findViewById(R.id.water_amount);
        totalTodayText = findViewById(R.id.water_total);

        // HRV UI
        hrvValueTextView = findViewById(R.id.hrv_value);
        hrvEmojiView = findViewById(R.id.hrv_emoji);
        stressLabelView = findViewById(R.id.stress_label);

        // Repository
        repository = new WaterRepository(this);
        repository.getTodayTotalWater(total ->
                runOnUiThread(() -> totalTodayText.setText(
                        getString(R.string.label_water_today, total))));

        // Water Receiver
        waterReceiver = new WaterReceiver(this, amount ->
                runOnUiThread(() -> waterAmountTextView.setText(
                        getString(R.string.label_water_amount_format, amount))));

        // HRV Receiver
        hrvReceiver = new HRVReceiver(this, hrv -> {
            Log.d("MainActivity", "💓 HRV received: " + hrv);
            runOnUiThread(() -> {
                if (hrv < 0 || Double.isNaN(hrv)) {
                    hrvValueTextView.setText(getString(R.string.label_hrv_dash));
                    hrvEmojiView.setText(getString(R.string.emoji_hrv_confused));
                    stressLabelView.setText(getString(R.string.label_stress_insufficient));
                } else {
                    hrvValueTextView.setText(getString(R.string.label_hrv_format, (int) hrv));

                    if (hrv < 30) {
                        hrvEmojiView.setText(getString(R.string.emoji_hrv_stressed));
                        stressLabelView.setText(getString(R.string.label_stress_stressed));
                    } else if (hrv < 60) {
                        hrvEmojiView.setText(getString(R.string.emoji_hrv_neutral));
                        stressLabelView.setText(getString(R.string.label_stress_neutral));
                    } else if (hrv < 120) {
                        hrvEmojiView.setText(getString(R.string.emoji_hrv_relaxed));
                        stressLabelView.setText(getString(R.string.label_stress_relaxed));
                    } else {
                        hrvEmojiView.setText(getString(R.string.emoji_hrv_very_relaxed));
                        stressLabelView.setText(getString(R.string.label_stress_very_relaxed));
                    }
                }
            });
        });

        // Register Receivers
        Wearable.getMessageClient(this).addListener(waterReceiver);
        Wearable.getMessageClient(this).addListener(hrvReceiver);

        // Buttons
        Button historyBtn = findViewById(R.id.btn_view_history);
        Button chartBtn = findViewById(R.id.btn_view_chart);

        historyBtn.setText(getString(R.string.btn_view_history_label));
        chartBtn.setText(getString(R.string.btn_view_chart_label));

        historyBtn.setOnClickListener(v ->
                startActivity(new Intent(this, HydrationHistoryActivity.class)));

        chartBtn.setOnClickListener(v ->
                startActivity(new Intent(this, HydrationChartActivity.class)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Wearable.getMessageClient(this).removeListener(waterReceiver);
        Wearable.getMessageClient(this).removeListener(hrvReceiver);
    }
}
