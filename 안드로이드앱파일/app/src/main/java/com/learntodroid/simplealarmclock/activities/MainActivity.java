package com.learntodroid.simplealarmclock.activities;

import static com.learntodroid.simplealarmclock.broadcastreceiver.AlarmBroadcastReceiver.RECURRING;
import static com.learntodroid.simplealarmclock.broadcastreceiver.AlarmBroadcastReceiver.TITLE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learntodroid.simplealarmclock.R;
import com.learntodroid.simplealarmclock.broadcastreceiver.AlarmBroadcastReceiver;
import com.learntodroid.simplealarmclock.data.Alarm;
import com.learntodroid.simplealarmclock.service.AlarmService;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    final private static String TAG = "IOT";
    ImageButton btn_photo,btn_showPillInfo, btn_giveMe ,btn_alarm, btn_present;

    /* database 레퍼런스 */
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    // 안드로이드 스튜디오와 연동된 계정의 데이터베이스 객체를 가져오는 코드


    private DatabaseReference mShouldRef = mRootRef.child("Give_pill");
    // 약을 줘야하는 상황을 나타내는 항목
    private DatabaseReference mPillShouldRef= mRootRef.child("DB object name").child("key1");
    // 초음파 센서에 감지됨을 나타내는 항목
    private DatabaseReference mPillIndex = mRootRef.child("차례").child("번호");

    // timer 코드 변수
    private PendingIntent alarmPendingIntent;
    private AlarmManager alarmManager;
    private Intent intent;
    private TimerTask task;
    private Timer timer;
    private Calendar calendar;

    // 현재 약통 차레를 나타내는 변수
    int D_index = 0;
    String mCurrentPhotoPath;   // 현재 저장할 이미지 파일의 경로를 String 변수에 저장
    final static int REQUEST_TAKE_PHOTO = 1; // 암시적 인텐트 명령 코드.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int alarmId = new Random().nextInt(Integer.MAX_VALUE);                                  // 랜덤으로 알람 아이디 부여
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);                  //
        intent = new Intent(this, AlarmBroadcastReceiver.class);                   //
        alarmPendingIntent = PendingIntent.getBroadcast(this, alarmId, intent, 0);
        timer = new Timer();
        calendar = Calendar.getInstance();
        btn_photo = findViewById(R.id.btn_photo);                   // OCR 버튼
        btn_showPillInfo = findViewById(R.id.btn_showPillInfo);     // 약 정보 보기 버튼
        btn_giveMe = findViewById(R.id.btn_giveMe);                 // 약 배출 버튼
        btn_alarm = findViewById(R.id.btn_alarm);                   // 알람 설정 버튼
        btn_present = findViewById(R.id.btn_present);               // 약 현황 버튼

        mPillIndex.addValueEventListener(new ValueEventListener() { // 파이어베이스 이벤트 리스너 등록
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                D_index =Integer.parseInt(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
       mPillShouldRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                timer = new Timer();
                // if 약이 그대로 있으면
                String s=snapshot.getValue().toString();
                if(s.equals("Y")) {
                    task = new TimerTask() {
                        @Override
                        public void run() {  // 타이머가 주기적으로 해야할 일을 정의
                            intent.putExtra(TITLE, "snooze");
                            intent.putExtra(RECURRING, false);
                            alarmManager.setRepeating( //setRepeating()의 인자로 RTC_WAKEUP과 calendar의 시간을 전달합니다.
                                    AlarmManager.RTC_WAKEUP,
                                    calendar.getTimeInMillis(), //Calendar 객체를 생성하여 알람이 울릴 정확한 시간을 설정합니다
                                    0,
                                    alarmPendingIntent
                            );
                        }
                    };
                    timer.schedule(task, 0, 60000000); //태스크를 10분에 한번씩 실행
                }

                else if (s.equals("N")) { //파이어베이스 값이 N 이면
                    if(timer != null && task != null) { //타이머와 태스크 중지
                        task.cancel();
                        timer.cancel();
                        timer.purge();
                    }
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료");
            }
            else {
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(MainActivity.this, new String[]
                        {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        btn_photo.setOnClickListener(new View.OnClickListener() {   // OCR 기능이 동작되도록 버튼
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();

            }
        });
        btn_showPillInfo.setOnClickListener(new View.OnClickListener() {    // 현재
            @Override
            public void onClick(View view) {
                // TODO make display activity  -> add intent code
                Intent intent = new Intent(MainActivity.this, ShowPillInfoActivity.class);
                startActivity(intent);
            }
        });
        btn_giveMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(D_index < 9)
                    mShouldRef.child("should").setValue("Y");
            }
        });
        btn_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
                startActivity(intent);
            }
        });
        btn_present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PresentActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] ==
                PackageManager.PERMISSION_GRANTED ) {
            Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    //촬영한 사진을 이미지 파일로 저장하는 함수 createImageFile()
    private File createImageFile() throws IOException {
        //파일 이름을 세팅 및 저장경로 세팅
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile( imageFileName, ".jpg", storageDir );
        mCurrentPhotoPath = image.getAbsolutePath();
        Intent intent = new Intent(MainActivity.this, OcrActivity.class);
        intent.putExtra("key", mCurrentPhotoPath);
        startActivity(intent);
        return image;
    }
    // 다음은 사진을 캡처하는 인텐트를 호출하는 함수입니다.
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException ex) {

            }
            if(photoFile != null)
            {
                Uri photoURI = FileProvider.getUriForFile(this, "com.learntodroid.simplealarmclock.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

}