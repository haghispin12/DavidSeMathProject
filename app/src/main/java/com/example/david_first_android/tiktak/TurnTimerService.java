package com.example.david_first_android.tiktak;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * שירות רקע לניהול הטיימר של התור.
 * השירות מעדכן ישירות את Firebase, מה שמאפשר לאקטיביטי להתעדכן בצורה חלקה ללא Broadcasts.
 */
public class TurnTimerService extends Service {

    private static final String CHANNEL_ID = "tiktak_timer_channel";
    private static final int NOTIFICATION_ID = 1;

    private CountDownTimer countDownTimer;
    private DatabaseReference db;

    private String currentGameId = "";
    private String currentTurnUid = "";

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseDatabase.getInstance().getReference();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String gameId = intent.getStringExtra("game_id");
            String turnUid = intent.getStringExtra("current_turn_uid");
            String p1 = intent.getStringExtra("player1_uid");
            String p2 = intent.getStringExtra("player2_uid");

            // מניעת הפעלה מחדש אם מדובר באותו משחק ואותו תור
            if (gameId != null && gameId.equals(currentGameId) && turnUid != null && turnUid.equals(currentTurnUid)) {
                return START_STICKY;
            }

            this.currentGameId = gameId != null ? gameId : "";
            this.currentTurnUid = turnUid != null ? turnUid : "";

            startForegroundServiceNotification();

            if (countDownTimer != null) {
                countDownTimer.cancel();
            }

            startTimer(gameId, turnUid, p1, p2);
        }
        return START_STICKY;
    }

    private void startTimer(String gameId, String turnUid, String p1, String p2) {
        countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;

                // עדכון ישיר לשרת - האקטיביטי מאזין לשינויים ב-Game ב-Firebase
                if (!currentGameId.isEmpty()) {
                    db.child("games").child(currentGameId).child("timer").setValue(secondsLeft);
                }
            }

            @Override
            public void onFinish() {
                // הזמן נגמר: עדכון הסטטוס ל-finished והפסד לשחקן שהזמן שלו נגמר
                if (gameId != null && p1 != null && p2 != null) {
                    String winnerUid = turnUid.equals(p1) ? p2 : p1;
                    db.child("games").child(gameId).child("winner").setValue(winnerUid);
                    db.child("games").child(gameId).child("status").setValue("finished");
                    // איפוס הטיימר בשרת
                    db.child("games").child(gameId).child("timer").setValue(0);
                }
            }
        }.start();
    }

    private void startForegroundServiceNotification() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("TikTak Turn Timer")
                .setContentText("המשחק פעיל...")
                .setSmallIcon(android.R.drawable.ic_menu_compass)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Timer Channel", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        if (countDownTimer != null) countDownTimer.cancel();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}