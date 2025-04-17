package com.example.hrv.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.hrv.R;
import com.example.hrv.data.WaterRepository;
import com.example.hrv.sync.HRVReceiver;
import com.example.hrv.sync.WaterReceiver;
import com.example.hrv.utils.WaterPreferences;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends AppCompatActivity {

    private WaterReceiver waterReceiver;
    private HRVReceiver hrvReceiver;
    private WaterRepository repository;

    private TextView waterAmountTextView, totalTodayText, goalStatusText;
    private TextView hrvValueTextView, hrvEmojiView, stressLabelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 🔗 Bind UI elements
        waterAmountTextView = findViewById(R.id.water_amount);
        totalTodayText = findViewById(R.id.water_total);
        goalStatusText = findViewById(R.id.water_goal_status);

        hrvValueTextView = findViewById(R.id.hrv_value);
        hrvEmojiView = findViewById(R.id.hrv_emoji);
        stressLabelView = findViewById(R.id.stress_label);

        // 🔄 Repository
        repository = new WaterRepository(this);

        // Load & show today's water + goal
        repository.getTodayTotalWater(total -> runOnUiThread(() -> {
            int goal = WaterPreferences.getGoal(this);
            String text = getString(R.string.label_water_today_with_goal, total, goal);
            totalTodayText.setText(text);
            updateGoalStatus(total, goal);
        }));

        // Tap water total to edit goal
        totalTodayText.setOnClickListener(v -> showGoalDialog());

        // ✅ Water receiver (from WearOS)
        waterReceiver = new WaterReceiver(this, amount -> runOnUiThread(() -> {
            waterAmountTextView.setText(getString(R.string.label_water_amount_format, amount));

            repository.getTodayTotalWater(total -> runOnUiThread(() -> {
                int goal = WaterPreferences.getGoal(this);
                String goalText = getString(R.string.label_water_today_with_goal, total, goal);
                totalTodayText.setText(goalText);

                Log.d("STREAK_CHECK", "Total: " + total + " / Goal: " + goal);
                updateGoalStatus(total, goal);
            }));
        }));

        // ✅ HRV receiver (from WearOS)
        hrvReceiver = new HRVReceiver(this, hrv -> runOnUiThread(() -> {
            Log.d("MainActivity", "💓 HRV received: " + hrv);

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
        }));

        // 🎧 Register WearOS receivers
        Wearable.getMessageClient(this).addListener(waterReceiver);
        Wearable.getMessageClient(this).addListener(hrvReceiver);

        // 🔘 Buttons
        Button historyBtn = findViewById(R.id.btn_view_history);
        Button chartBtn = findViewById(R.id.btn_view_chart);
        Button setGoalBtn = findViewById(R.id.btn_set_goal);

        historyBtn.setOnClickListener(v ->
                startActivity(new Intent(this, HydrationHistoryActivity.class)));

        chartBtn.setOnClickListener(v ->
                startActivity(new Intent(this, HydrationChartActivity.class)));

        setGoalBtn.setOnClickListener(v -> showGoalDialog());
    }

    private void updateGoalStatus(int total, int goal) {
        if (goalStatusText != null) {
            if (total >= goal) {
                goalStatusText.setText(getString(R.string.goal_streak));
                goalStatusText.setVisibility(TextView.VISIBLE);
            } else {
                goalStatusText.setVisibility(TextView.GONE);
            }
        }
    }

    private void showGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Daily Water Goal (ml)");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("e.g. 3000");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            try {
                int goal = Integer.parseInt(input.getText().toString().trim());
                if (goal < 1000 || goal > 5000) {
                    Toast.makeText(this, "❌ Please enter a number between 1000 and 5000 ml", Toast.LENGTH_LONG).show();
                } else {
                    WaterPreferences.setGoal(this, goal);
                    Toast.makeText(this, "✅ Goal set: " + goal + " ml", Toast.LENGTH_SHORT).show();
                    repository.getTodayTotalWater(total -> runOnUiThread(() -> {
                        totalTodayText.setText(getString(R.string.label_water_today_with_goal, total, goal));
                        updateGoalStatus(total, goal);
                    }));
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "❌ Invalid input. Please enter a number.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Wearable.getMessageClient(this).removeListener(waterReceiver);
        Wearable.getMessageClient(this).removeListener(hrvReceiver);
    }
}
