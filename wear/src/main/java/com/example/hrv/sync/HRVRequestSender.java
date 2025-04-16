package com.example.hrv.sync;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class HRVRequestSender {

    private static final String TAG = "HRVRequestSender";
    private static final String PATH = "/hrv_sync"; // 🔁 match this path on receiver

    public static void sendHRVToPhone(Context context, double hrvValue) {
        new Thread(() -> {
            try {
                List<Node> nodes = Tasks.await(Wearable.getNodeClient(context).getConnectedNodes());
                for (Node node : nodes) {
                    String payload = String.valueOf(hrvValue);
                    Tasks.await(Wearable.getMessageClient(context)
                            .sendMessage(node.getId(), PATH, payload.getBytes(StandardCharsets.UTF_8)));

                    Log.d(TAG, "✅ Sent HRV value " + payload + " to " + node.getDisplayName());
                }
            } catch (Exception e) {
                Log.e(TAG, "❌ Failed to send HRV data: ", e);
            }
        }).start();
    }
}
