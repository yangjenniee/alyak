package com.learntodroid.simplealarmclock.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learntodroid.simplealarmclock.R;
import com.learntodroid.simplealarmclock.activities.RingActivity;

import static com.learntodroid.simplealarmclock.application.App.CHANNEL_ID;
import static com.learntodroid.simplealarmclock.broadcastreceiver.AlarmBroadcastReceiver.TITLE;

public class AlarmService extends Service {
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mPillShouldRef = mRootRef.child("Give_pill").child("should");
    private DatabaseReference mTest = mRootRef.child("테스트 현황");

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
        mediaPlayer.setLooping(true);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String check = String.format("%s", intent.getStringExtra(TITLE));
        if(check.equals(""))
        {
            Intent notificationIntent = new Intent(this, RingActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            String alarmTitle = String.format("%s Alarm", intent.getStringExtra(TITLE));
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(alarmTitle)
                    .setContentText( intent.getStringExtra(TITLE) + "를 챙겨드실 시간입니다.")
                    .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                    .setContentIntent(pendingIntent)
                    .build();

            mediaPlayer.start();
            long[] pattern = {0, 100, 1000};
            vibrator.vibrate(pattern, 0);

            startForeground(1, notification);

            return START_STICKY;
        }
        else
        {
            mPillShouldRef.setValue("Y");
            Intent notificationIntent = new Intent(this, RingActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            String alarmTitle = String.format("%s Alarm", intent.getStringExtra(TITLE));
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(alarmTitle)
                    .setContentText( intent.getStringExtra(TITLE) + "를 챙겨드실 시간입니다.")
                    .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                    .setContentIntent(pendingIntent)
                    .build();

            mediaPlayer.start();
            long[] pattern = {0, 100, 1000};
            vibrator.vibrate(pattern, 0);

            startForeground(1, notification);

            return START_STICKY;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mediaPlayer.stop();
        vibrator.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
