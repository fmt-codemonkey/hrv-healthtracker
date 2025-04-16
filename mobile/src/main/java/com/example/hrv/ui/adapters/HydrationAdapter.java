package com.example.hrv.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hrv.R;
import com.example.hrv.data.WaterEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HydrationAdapter extends RecyclerView.Adapter<HydrationAdapter.WaterViewHolder> {

    private final List<WaterEntity> waterList;

    public HydrationAdapter(List<WaterEntity> waterList) {
        this.waterList = waterList;
    }

    @NonNull
    @Override
    public WaterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_water_entry, parent, false);
        return new WaterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WaterViewHolder holder, int position) {
        WaterEntity item = waterList.get(position);
        holder.amount.setText(item.amount + " ml");

        String time = new SimpleDateFormat("hh:mm a", Locale.getDefault())
                .format(new Date(item.timestamp));
        holder.timestamp.setText(time);
    }

    @Override
    public int getItemCount() {
        return waterList.size();
    }

    static class WaterViewHolder extends RecyclerView.ViewHolder {
        TextView amount, timestamp;

        public WaterViewHolder(@NonNull View itemView) {
            super(itemView);
            amount = itemView.findViewById(R.id.water_amount);
            timestamp = itemView.findViewById(R.id.water_time);
        }
    }
}
