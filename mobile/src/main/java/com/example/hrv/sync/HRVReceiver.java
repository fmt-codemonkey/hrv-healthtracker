package com.example.hrv.sync;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;

import java.nio.charset.StandardCharsets;

public class HRVReceiver implements MessageClient.OnMessageReceivedListener {

    private static final String PATH = "/hrv_sync";
    private static final String TAG = "HRVReceiver";

    private final Context context;
    private final HRVListener listener;

    public HRVReceiver(Context context, HRVListener listener) {
        this.context = context.getApplicationContext();
        this.listener = listener;
    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        if (PATH.equals(messageEvent.getPath())) {
            String message = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Log.d(TAG, "📩 HRV received: " + message);

            try {
                double hrv = Double.parseDouble(message);
                if (listener != null) {
                    listener.onHRVReceived(hrv);
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "❌ Invalid HRV format: " + message, e);
            }
        }
    }

    public interface HRVListener {
        void onHRVReceived(double hrv);
    }
}
