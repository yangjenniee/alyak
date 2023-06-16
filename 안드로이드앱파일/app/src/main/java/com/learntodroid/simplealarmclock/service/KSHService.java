package com.learntodroid.simplealarmclock.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learntodroid.simplealarmclock.data.Alarm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

// 서비스 클래스를 구현하려면, Service 를 상속받는다
public class KSHService extends Service {
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mShouldRef = mRootRef.child("Give_pill");
    DatabaseReference mPillRef = mRootRef.child("약통 현황");
    DatabaseReference mPillIndex = mRootRef.child("차례").child("번호");
    DatabaseReference mStatePill = mRootRef.child("배출 현황");
    DatabaseReference mTest = mRootRef.child("테스트 현황");
    String state = "";
    String presentTime = "";
    long tm = 0;
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mStatePill.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                state = snapshot.getValue().toString();
                tm = System.currentTimeMillis();
                presentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tm);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

                while(true)
                {
                    mTest.setValue(System.currentTimeMillis());
                    if(state.equals("Y") && ( System.currentTimeMillis()/ (1000 * 60) - tm/ (1000 * 60) )% 2 ==  1 )
                    {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.add(Calendar.MINUTE, 1);
                        Alarm alarm = new Alarm(
                                new Random().nextInt(Integer.MAX_VALUE),
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                "Snooze",
                                System.currentTimeMillis(),
                                true,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false
                        );

                        alarm.schedule(getApplicationContext());
                        Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
                    }
                    else if (state.equals("N")){
                        stopSelf();
                        break;
                    }
                }

        return super.onStartCommand(intent, flags, startId);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
