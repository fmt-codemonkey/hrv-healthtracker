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

        // Repositories
        repository = new WaterRepository(this);
        repository.getTodayTotalWater(total ->
                runOnUiThread(() -> totalTodayText.setText("Today: " + total + " ml")));

        // Receivers
        waterReceiver = new WaterReceiver(this, amount ->
                runOnUiThread(() -> waterAmountTextView.setText(amount + " ml")));

        hrvReceiver = new HRVReceiver(this, hrv -> {
            Log.d("MainActivity", "💓 HRV received: " + hrv);
            runOnUiThread(() -> {
                if (hrv < 0 || Double.isNaN(hrv)) {
                    hrvValueTextView.setText("--");
                    hrvEmojiView.setText("🤔");
                    stressLabelView.setText("Insufficient Data");
                } else {
                    hrvValueTextView.setText(String.format("%.0f ms", hrv));
                    if (hrv < 30) {
                        hrvEmojiView.setText("😣");
                        stressLabelView.setText("Stressed");
                    } else if (hrv < 60) {
                        hrvEmojiView.setText("😐");
                        stressLabelView.setText("Neutral");
                    } else if (hrv < 120) {
                        hrvEmojiView.setText("😌");
                        stressLabelView.setText("Relaxed");
                    } else {
                        hrvEmojiView.setText("😴");
                        stressLabelView.setText("Very Relaxed");
                    }
                }
            });
        });

        // Register receivers
        Wearable.getMessageClient(this).addListener(waterReceiver);
        Wearable.getMessageClient(this).addListener(hrvReceiver);

        // Buttons
        Button historyBtn = findViewById(R.id.btn_view_history);
        Button chartBtn = findViewById(R.id.btn_view_chart);

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
