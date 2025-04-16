package com.example.hrv.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hrv.R;
import com.example.hrv.data.AppDatabase;
import com.example.hrv.data.WaterEntity;
import com.example.hrv.ui.adapters.HydrationAdapter;

import java.util.List;
import java.util.concurrent.Executors;

public class HydrationHistoryActivity extends AppCompatActivity {

    private RecyclerView hydrationRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Enable back arrow in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Hydration History");
        }

        hydrationRecycler = findViewById(R.id.hydration_recycler);
        hydrationRecycler.setLayoutManager(new LinearLayoutManager(this));

        Executors.newSingleThreadExecutor().execute(() -> {
            List<WaterEntity> list = AppDatabase.getInstance(this).waterDao().getAll();
            runOnUiThread(() -> hydrationRecycler.setAdapter(new HydrationAdapter(list)));
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // closes the activity
        return true;
    }

}
