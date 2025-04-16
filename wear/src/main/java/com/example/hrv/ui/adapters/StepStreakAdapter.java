package com.example.hrv.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hrv.R;

import java.util.ArrayList;
import java.util.List;

public class StepStreakAdapter extends RecyclerView.Adapter<StepStreakAdapter.StreakViewHolder> {

    private List<Boolean> streakList = new ArrayList<>();

    @NonNull
    @Override
    public StreakViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_streak, parent, false);
        return new StreakViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StreakViewHolder holder, int position) {
        boolean metGoal = streakList.get(position);
        holder.streakDot.setImageResource(metGoal ? R.drawable.ic_dot_filled : R.drawable.ic_dot_empty);
    }

    @Override
    public int getItemCount() {
        return streakList.size();
    }

    public void setData(List<Boolean> list) {
        streakList = list;
        notifyDataSetChanged();
    }

    static class StreakViewHolder extends RecyclerView.ViewHolder {
        ImageView streakDot;

        public StreakViewHolder(@NonNull View itemView) {
            super(itemView);
            streakDot = itemView.findViewById(R.id.streak_dot);
        }
    }
}
