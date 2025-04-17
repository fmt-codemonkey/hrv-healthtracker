package com.example.hrv.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hrv.databinding.FragmentHomeBinding;
import com.example.hrv.sensors.HRVSensorManager;
import com.example.hrv.sync.HRVRequestSender;
import com.example.hrv.utils.NotificationHelper;

import com.example.hrv.R;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HRVSensorManager hrvSensorManager;

    // ✅ Prevent notification spam
    private boolean hasShownStressNotification = false;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        hrvSensorManager = new HRVSensorManager(requireContext());
        hrvSensorManager.start();

        // ✅ Update BPM
        hrvSensorManager.getHeartRateLiveData().observe(getViewLifecycleOwner(), bpm -> {
            binding.rhrValue.setText(String.valueOf(bpm));
        });

        // ✅ Update HRV, Emoji, Notification, and Sync
        hrvSensorManager.getHrvLiveData().observe(getViewLifecycleOwner(), hrv -> {
            if (hrv < 0 || Double.isNaN(hrv)) {
                binding.hrvValue.setText("--");
                binding.hrvEmoji.setText("🤔");
                binding.stressLabel.setText("Insufficient Data");
                return;
            }

            // ✅ Display HRV
            binding.hrvValue.setText(String.format("%.0f", hrv));

            // ✅ Stress zone
            if (hrv < 30) {
                binding.hrvEmoji.setText("😣");
                binding.stressLabel.setText("Stressed");

                if (!hasShownStressNotification) {
                    hasShownStressNotification = true;
                    Log.d("NOTIFY", "🛎️ Showing stress notification");

                    NotificationHelper.showStressNotification(
                            requireContext(),
                            "⚠️ High Stress Detected",
                            "Take a breath or hydrate to relax."
                    );
                }
            }
            // ✅ Normal zones
            else {
                hasShownStressNotification = false;

                if (hrv < 60) {
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

            // ✅ Sync to mobile
            HRVRequestSender.sendHRVToPhone(requireContext(), hrv);
        });

        // 🔁 Navigate to ActionsFragment
        binding.btnActions.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new ActionsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hrvSensorManager.stop();
        binding = null;
    }
}
