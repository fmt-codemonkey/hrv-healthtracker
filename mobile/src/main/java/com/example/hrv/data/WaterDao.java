package com.example.hrv.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WaterDao {

    @Insert
    void insert(WaterEntity entry);

    @Query("SELECT * FROM water_log ORDER BY timestamp DESC")
    List<WaterEntity> getAll();

    @Query("SELECT SUM(amount) FROM water_log WHERE date(timestamp/1000, 'unixepoch') = date('now')")
    int getTodayTotal();
}
