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

    private List<Boolean> streaks = new ArrayList<>();

    @NonNull
    @Override
    public StreakViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_streak_dot, parent, false);
        return new StreakViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StreakViewHolder holder, int position) {
        boolean isComplete = streaks.get(position);
        holder.dot.setImageResource(isComplete ? R.drawable.ic_dot_filled : R.drawable.ic_dot_empty);
    }

    @Override
    public int getItemCount() {
        return streaks.size();
    }

    public void setData(List<Boolean> list) {
        this.streaks = list;
        notifyDataSetChanged();
    }

    static class StreakViewHolder extends RecyclerView.ViewHolder {
        ImageView dot;

        public StreakViewHolder(@NonNull View itemView) {
            super(itemView);
            dot = itemView.findViewById(R.id.dot_image);
        }
    }
}
