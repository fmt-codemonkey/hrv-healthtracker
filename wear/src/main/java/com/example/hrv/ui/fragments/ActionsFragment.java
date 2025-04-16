package com.example.hrv.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

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

    private final int GOAL_ML = 3500;

    public ActionsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentActionsBinding.inflate(inflater, container, false);

        // ViewModels
        stepViewModel = new ViewModelProvider(requireActivity()).get(StepViewModel.class);
        waterViewModel = new ViewModelProvider(requireActivity()).get(WaterViewModel.class);

        // RecyclerView for streaks
        adapter = new StepStreakAdapter();
        binding.calendarRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.calendarRecycler.setAdapter(adapter);

        // Step Count Observer
        stepViewModel.getStepCount().observe(getViewLifecycleOwner(), steps -> {
            binding.stepValue.setText(String.valueOf(steps));
        });

        // Streak Dots (Step + Water Combined)
        stepViewModel.getStreakList().observe(getViewLifecycleOwner(), this::combineStreaks);
        waterViewModel.getStreakList().observe(getViewLifecycleOwner(), this::combineStreaks);

        // Water Intake Observer
        waterViewModel.getWaterIntake().observe(getViewLifecycleOwner(), amount -> {
            binding.waterValue.setText(amount + " ml");

            // Set ProgressBar max & current value
            binding.waterProgress.setMax(GOAL_ML);
            binding.waterProgress.setProgress(amount);

            // Update % below progress bar
            int percent = (int) ((amount / (float) GOAL_ML) * 100);
            binding.waterPercent.setText(percent + "%");
        });

        // +250ml Water Button
        binding.btnAddWater.setOnClickListener(v -> {
            waterViewModel.addWater(250);

            // Send updated water value to phone
            int currentWater = waterViewModel.getWaterIntake().getValue() != null
                    ? waterViewModel.getWaterIntake().getValue()
                    : 0;

            com.example.hrv.sync.WaterRequestSender.sendWaterToPhone(requireContext(), currentWater);
        });


        return binding.getRoot();
    }

    // Combine step + water streaks (7-day hydration+step streak logic)
    private void combineStreaks(List<Boolean> _unused) {
        List<Boolean> steps = stepViewModel.getStreakList().getValue();
        List<Boolean> water = waterViewModel.getStreakList().getValue();

        if (steps != null && water != null && steps.size() == 7 && water.size() == 7) {
            List<Boolean> combined = new java.util.ArrayList<>();
            for (int i = 0; i < 7; i++) {
                combined.add(steps.get(i) && water.get(i)); // ✅ both completed
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
