package com.example.hrv.data;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class WaterRepository {

    private final WaterDao waterDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public WaterRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        waterDao = db.waterDao();
    }

    public void insert(int amount) {
        executor.execute(() -> {
            WaterEntity entry = new WaterEntity(amount, System.currentTimeMillis());
            waterDao.insert(entry);
        });
    }

    public void getTodayTotalWater(Consumer<Integer> callback) {
        executor.execute(() -> {
            int total = waterDao.getTodayTotal();
            callback.accept(total);
        });
    }
}
