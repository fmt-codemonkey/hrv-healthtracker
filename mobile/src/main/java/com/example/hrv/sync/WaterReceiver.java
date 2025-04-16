package com.example.hrv.sync;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.hrv.data.WaterRepository;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;

import java.nio.charset.StandardCharsets;

public class WaterReceiver implements MessageClient.OnMessageReceivedListener {

    private static final String PATH = "/water_sync";
    private static final String TAG = "WaterReceiver";

    private final Context context;
    private final WaterListener listener;
    private final WaterRepository repository;

    public WaterReceiver(Context context, WaterListener listener) {
        this.context = context.getApplicationContext(); // Prevent memory leaks
        this.listener = listener;
        this.repository = new WaterRepository(this.context);
    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        if (!PATH.equals(messageEvent.getPath())) return;

        String message = new String(messageEvent.getData(), StandardCharsets.UTF_8);
        Log.d(TAG, "📩 Water received from watch: " + message);

        // Show toast
        Toast.makeText(context, "💧 Water: " + message + "ml", Toast.LENGTH_SHORT).show();

        // Update UI via listener
        if (listener != null) {
            listener.onWaterReceived(message);
        }

        // Store to Room DB
        try {
            int value = Integer.parseInt(message);
            repository.insert(value);
            Log.d(TAG, "✅ Water inserted into DB: " + value + "ml");
        } catch (NumberFormatException e) {
            Log.e(TAG, "❌ Failed to parse water value: " + message, e);
        }
    }

    // Callback interface for MainActivity
    public interface WaterListener {
        void onWaterReceived(String amount);
    }
}
