package com.example.cyberguard.presentation.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import androidx.core.app.NotificationCompat;

import com.example.cyberguard.R;
import com.example.cyberguard.domain.scam.ScamDetectionEngine;

public class SmsReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "scam_alerts";
    private final ScamDetectionEngine engine = new ScamDetectionEngine();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                String messageBody = smsMessage.getMessageBody();
                String sender = smsMessage.getDisplayOriginatingAddress();

                ScamDetectionEngine.DetectionResult result = engine.analyzeText(messageBody);

                if (result.score >= 40) {
                    showScamNotification(context, sender, result.verdict);
                }
            }
        }
    }

    private void showScamNotification(Context context, String sender, String verdict) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Scam Alerts", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Suspicious Message from: " + sender)
                .setContentText(verdict + ". Tap to analyze in CyberGuard.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
