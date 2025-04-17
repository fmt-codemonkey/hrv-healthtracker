package com.example.hrv.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hrv.R;
import com.example.hrv.data.AppDatabase;
import com.example.hrv.data.WaterEntity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class HydrationChartActivity extends AppCompatActivity {

    private LineChart chart;
    private static final int HYDRATION_GOAL = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        // Action bar back button and title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_chart_hydration);
        }

        chart = findViewById(R.id.hydration_chart);

        // Style the chart
        chart.setDrawGridBackground(false);
        chart.setBackgroundColor(getColor(R.color.background_dark));
        chart.getAxisLeft().setTextColor(getColor(R.color.text_primary));
        chart.getXAxis().setTextColor(getColor(R.color.text_primary));
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);

        // Chart description
        Description desc = new Description();
        desc.setText(getString(R.string.chart_description_hydration));
        desc.setTextColor(getColor(R.color.text_secondary));
        desc.setTextSize(12f);
        chart.setDescription(desc);

        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setGranularity(1f);
        chart.animateX(1000);

        // Goal line
        String goalText = getString(R.string.chart_goal_label, HYDRATION_GOAL);
        LimitLine goalLine = new LimitLine(HYDRATION_GOAL, goalText);
        goalLine.setLineColor(getColor(R.color.teal_700));
        goalLine.setLineWidth(2f);
        goalLine.setTextColor(getColor(R.color.teal_700));
        goalLine.setTextSize(12f);
        chart.getAxisLeft().addLimitLine(goalLine);

        // Load hydration data from DB in background
        Executors.newSingleThreadExecutor().execute(() -> {
            List<WaterEntity> entries = AppDatabase.getInstance(this).waterDao().getAll();

            List<Entry> chartPoints = new ArrayList<>();
            List<String> timeLabels = new ArrayList<>();
            final int[] total = {0};
            int runningTotal = 0;
            int i = 0;

            for (WaterEntity entity : entries) {
                runningTotal += entity.amount;
                chartPoints.add(new Entry(i, runningTotal));

                String time = new SimpleDateFormat("hh:mm a", Locale.getDefault())
                        .format(new Date(entity.timestamp));
                timeLabels.add(time);

                total[0] += entity.amount;
                i++;
            }

            if (total[0] >= HYDRATION_GOAL) {
                String toastText = getString(R.string.toast_goal_reached, total[0]);
                runOnUiThread(() ->
                        Toast.makeText(this, toastText, Toast.LENGTH_LONG).show()
                );
            }

            LineDataSet dataSet = new LineDataSet(chartPoints, getString(R.string.chart_dataset_label));
            dataSet.setColor(getColor(R.color.chart_line));
            dataSet.setCircleColor(getColor(R.color.chart_circle));
            dataSet.setDrawValues(true);
            dataSet.setValueTextColor(getColor(R.color.text_primary));
            dataSet.setLineWidth(2f);
            dataSet.setDrawFilled(true);
            dataSet.setFillColor(getColor(R.color.chart_line));

            LineData lineData = new LineData(dataSet);

            runOnUiThread(() -> {
                chart.setData(lineData);
                chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(timeLabels));
                chart.getXAxis().setLabelRotationAngle(-45);
                chart.invalidate(); // redraw chart
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
