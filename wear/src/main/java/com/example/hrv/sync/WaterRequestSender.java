package com.example.hrv.sync;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class WaterRequestSender {

    private static final String TAG = "WaterRequestSender";
    private static final String PATH = "/water_sync"; // can be anything

    public static void sendWaterToPhone(Context context, int waterAmount) {
        new Thread(() -> {
            try {
                List<Node> nodes = Tasks.await(Wearable.getNodeClient(context).getConnectedNodes());
                for (Node node : nodes) {
                    String payload = String.valueOf(waterAmount);
                    Tasks.await(Wearable.getMessageClient(context)
                            .sendMessage(node.getId(), PATH, payload.getBytes(StandardCharsets.UTF_8)));

                    Log.d(TAG, "✅ Sent water value " + waterAmount + " to node " + node.getDisplayName());
                }
            } catch (Exception e) {
                Log.e(TAG, "❌ Failed to send water data: ", e);
            }
        }).start();
    }
}
