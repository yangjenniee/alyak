package com.learntodroid.simplealarmclock.activities;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.learntodroid.simplealarmclock.R;
import com.learntodroid.simplealarmclock.createalarm.TimePickerUtil;
import com.learntodroid.simplealarmclock.data.Alarm;
import com.learntodroid.simplealarmclock.service.AlarmService;
import com.learntodroid.simplealarmclock.service.KSHService;

import java.util.Calendar;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RingActivity extends AppCompatActivity {
    @BindView(R.id.activity_ring_dismiss) Button dismiss;

    @BindView(R.id.activity_ring_clock) ImageView clock;

    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mPillShouldRef = mRootRef.child("Give_pill").child("should");
    private DatabaseReference mTest = mRootRef.child("테스트 현황");
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);

        ButterKnife.bind(this);
        dismiss.setOnClickListener(new View.OnClickListener() {  // 알람을 종료함.
            @Override
            public void onClick(View v) {
                Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
                getApplicationContext().stopService(intentService); //현재 인텐트 종료
                finish(); //앱 종료
            }
        });



        animateClock();
    }

    private void animateClock() {
        //알람 이미지 Animation
        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(clock, "rotation", 0f, 20f, 0f, -20f, 0f);
        rotateAnimation.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnimation.setDuration(800);
        rotateAnimation.start();
    }
}
