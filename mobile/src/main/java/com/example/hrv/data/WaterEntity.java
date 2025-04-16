package com.example.hrv.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "water_log")
public class WaterEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "amount")
    public int amount;

    @ColumnInfo(name = "timestamp")
    public long timestamp;

    public WaterEntity(int amount, long timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }
}
