package com.example.hrv.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hrv.databinding.FragmentActionsBinding;
import com.example.hrv.ui.HydrationChartActivity;
import com.example.hrv.ui.HydrationHistoryActivity;
import com.example.hrv.ui.adapters.StepStreakAdapter;
import com.example.hrv.viewmodel.HRVViewModel;
import com.example.hrv.viewmodel.StepViewModel;
import com.example.hrv.viewmodel.WaterViewModel;

import java.util.List;

public class ActionsFragment extends Fragment {

    private FragmentActionsBinding binding;
    private StepViewModel stepViewModel;
    private WaterViewModel waterViewModel;
    private HRVViewModel hrvViewModel;
    private StepStreakAdapter adapter;

    public ActionsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentActionsBinding.inflate(inflater, container, false);

        stepViewModel = new ViewModelProvider(requireActivity()).get(StepViewModel.class);
        waterViewModel = new ViewModelProvider(requireActivity()).get(WaterViewModel.class);
        hrvViewModel = new ViewModelProvider(requireActivity()).get(HRVViewModel.class);

        // Observers
        stepViewModel.getStepCount().observe(getViewLifecycleOwner(), steps ->
                binding.stepValue.setText(String.valueOf(steps)));

        waterViewModel.getWaterIntake().observe(getViewLifecycleOwner(), amount ->
                binding.waterValue.setText(amount + " ml"));

        hrvViewModel.getHRV().observe(getViewLifecycleOwner(), hrv -> {
            if (hrv == null || hrv < 0 || Double.isNaN(hrv)) {
                binding.hrvValue.setText("--");
                binding.hrvEmoji.setText("🤔");
                binding.stressLabel.setText("Insufficient Data");
            } else {
                binding.hrvValue.setText(String.format("%.0f ms", hrv));
                if (hrv < 30) {
                    binding.hrvEmoji.setText("😣");
                    binding.stressLabel.setText("Stressed");
                } else if (hrv < 60) {
                    binding.hrvEmoji.setText("😐");
                    binding.stressLabel.setText("Neutral");
                } else if (hrv < 120) {
                    binding.hrvEmoji.setText("😌");
                    binding.stressLabel.setText("Relaxed");
                } else {
                    binding.hrvEmoji.setText("😴");
                    binding.stressLabel.setText("Very Relaxed");
                }
            }
        });

        // Streak Bar
        adapter = new StepStreakAdapter();
        binding.calendarRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.calendarRecycler.setAdapter(adapter);

        stepViewModel.getStreakList().observe(getViewLifecycleOwner(), this::combineStreaks);
        waterViewModel.getStreakList().observe(getViewLifecycleOwner(), this::combineStreaks);

        binding.btnAddWater.setOnClickListener(v -> waterViewModel.addWater(250));

        binding.btnViewHistory.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), HydrationHistoryActivity.class)));

        binding.btnViewChart.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), HydrationChartActivity.class)));

        return binding.getRoot();
    }

    private void combineStreaks(List<Boolean> _unused) {
        List<Boolean> steps = stepViewModel.getStreakList().getValue();
        List<Boolean> water = waterViewModel.getStreakList().getValue();

        if (steps != null && water != null && steps.size() == 7 && water.size() == 7) {
            List<Boolean> combined = new java.util.ArrayList<>();
            for (int i = 0; i < 7; i++) {
                combined.add(steps.get(i) && water.get(i));
            }
            adapter.setData(combined);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
