package com.example.hrv.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hrv.R;
import com.example.hrv.databinding.FragmentActionsBinding;
import com.example.hrv.viewmodel.StepViewModel;
import com.example.hrv.viewmodel.WaterViewModel;
import com.example.hrv.ui.adapters.StepStreakAdapter;

import java.util.List;

public class ActionsFragment extends Fragment {

    private FragmentActionsBinding binding;
    private StepViewModel stepViewModel;
    private WaterViewModel waterViewModel;
    private StepStreakAdapter adapter;

    private int goalMl;
    private int addWaterAmount;

    public ActionsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentActionsBinding.inflate(inflater, container, false);

        // ViewModels
        stepViewModel = new ViewModelProvider(requireActivity()).get(StepViewModel.class);
        waterViewModel = new ViewModelProvider(requireActivity()).get(WaterViewModel.class);

        // Load resource values
        goalMl = getResources().getInteger(R.integer.water_goal_ml);
        addWaterAmount = getResources().getInteger(R.integer.add_water_step);

        // RecyclerView for streaks
        adapter = new StepStreakAdapter();
        binding.calendarRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.calendarRecycler.setAdapter(adapter);

        // Step Count Observer
        stepViewModel.getStepCount().observe(getViewLifecycleOwner(), steps -> {
            binding.stepValue.setText(String.valueOf(steps));
        });

        // Streaks
        stepViewModel.getStreakList().observe(getViewLifecycleOwner(), this::combineStreaks);
        waterViewModel.getStreakList().observe(getViewLifecycleOwner(), this::combineStreaks);

        // Water Intake Observer
        waterViewModel.getWaterIntake().observe(getViewLifecycleOwner(), amount -> {
            String mlUnit = getString(R.string.unit_ml_suffix, amount);
            binding.waterValue.setText(mlUnit);

            // Progress bar
            binding.waterProgress.setMax(goalMl);
            binding.waterProgress.setProgress(amount);

            int percent = (int) ((amount / (float) goalMl) * 100);
            binding.waterPercent.setText(getString(R.string.unit_percent_suffix, percent));
        });

        // Add water button
        binding.btnAddWater.setOnClickListener(v -> {
            waterViewModel.addWater(addWaterAmount);

            int currentWater = waterViewModel.getWaterIntake().getValue() != null
                    ? waterViewModel.getWaterIntake().getValue()
                    : 0;

            com.example.hrv.sync.WaterRequestSender.sendWaterToPhone(requireContext(), currentWater);
        });

        return binding.getRoot();
    }

    // Combine step + water streaks (7-day logic)
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
