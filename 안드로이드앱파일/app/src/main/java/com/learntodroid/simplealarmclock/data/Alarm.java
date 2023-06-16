package com.learntodroid.simplealarmclock.data;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.learntodroid.simplealarmclock.broadcastreceiver.AlarmBroadcastReceiver;
import com.learntodroid.simplealarmclock.createalarm.DayUtil;

import java.util.Calendar;
import java.util.Locale;

import static com.learntodroid.simplealarmclock.broadcastreceiver.AlarmBroadcastReceiver.FRIDAY;
import static com.learntodroid.simplealarmclock.broadcastreceiver.AlarmBroadcastReceiver.MONDAY;
import static com.learntodroid.simplealarmclock.broadcastreceiver.AlarmBroadcastReceiver.RECURRING;
import static com.learntodroid.simplealarmclock.broadcastreceiver.AlarmBroadcastReceiver.SATURDAY;
import static com.learntodroid.simplealarmclock.broadcastreceiver.AlarmBroadcastReceiver.SUNDAY;
import static com.learntodroid.simplealarmclock.broadcastreceiver.AlarmBroadcastReceiver.THURSDAY;
import static com.learntodroid.simplealarmclock.broadcastreceiver.AlarmBroadcastReceiver.TITLE;
import static com.learntodroid.simplealarmclock.broadcastreceiver.AlarmBroadcastReceiver.TUESDAY;
import static com.learntodroid.simplealarmclock.broadcastreceiver.AlarmBroadcastReceiver.WEDNESDAY;
import static com.learntodroid.simplealarmclock.createalarm.DayUtil.toDay;

@Entity(tableName = "alarm_table")
public class Alarm {
    @PrimaryKey
    @NonNull
    private int alarmId; //아이디

    private int hour, minute; //알람이 울리는 시간 과 분
    private boolean started, recurring; //예약 여부, 반복 여부
    private boolean monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    private String title; //알람제목

    private long created;

    public Alarm(int alarmId, int hour, int minute, String title, long created, boolean started, boolean recurring, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, boolean sunday) {
        this.alarmId = alarmId;
        this.hour = hour;
        this.minute = minute;
        this.started = started;

        this.recurring = recurring;

        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;

        this.title = title;

        this.created = created;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public boolean isStarted() {
        return started;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public boolean isMonday() {
        return monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public void schedule(Context context) {
        //AlarmBroadCastReceiver에 대한 인텐트 생성, 알람에 대한 데이터를 인텐트 엑스트라로 제공
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        //intent로 값 받아오기
        intent.putExtra(RECURRING, recurring);
        intent.putExtra(MONDAY, monday);
        intent.putExtra(TUESDAY, tuesday);
        intent.putExtra(WEDNESDAY, wednesday);
        intent.putExtra(THURSDAY, thursday);
        intent.putExtra(FRIDAY, friday);
        intent.putExtra(SATURDAY, saturday);
        intent.putExtra(SUNDAY, sunday);

        intent.putExtra(TITLE, title);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0);

        //알람 브로드캐스트를 실행하는 밀리초 단위를 계산
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // 시간과 분 설정
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);



        // 알람 시간이 이미 지난 경우 1일씩 증가
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        if (!recurring) {
            String toastText = null;

            try {
                String s=  DayUtil.toDay(calendar.get(Calendar.DAY_OF_WEEK));
                String day = "";
                if (s == "Monday")
                    day="월요일";
                else if(s =="Tuesday")
                    day="화요일";
                else if(s =="Wednesday")
                    day="수요일";
                else if(s =="Thursday")
                    day="목요일";
                else if(s =="Friday")
                    day="금요일";
                else if(s =="Saturday")
                    day="토요일";
                else if(s =="Sunday")
                    day="일요일";




                toastText = String.format("일회 복약 %s이 %s  %02d:%02d으로 지정되었습니다", title, day, hour, minute, alarmId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();

            //반복되지 않는 경우 알람
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    alarmPendingIntent
            );
        } else {
            String toastText = String.format("매주 복약 %s이 %s  %02d:%02d에 맞춰졌어요", title, getRecurringDaysText(), hour, minute, alarmId);
            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();

            //반복되는 경우 알람
            final long RUN_DAILY = 24 * 60 * 60 * 1000;
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    RUN_DAILY,
                    alarmPendingIntent
            );
        }

        this.started = true;
    }

    public void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0);
        alarmManager.cancel(alarmPendingIntent);
        this.started = false;

        String toastText = String.format("Alarm cancelled for %02d:%02d with id %d", hour, minute, alarmId);
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
        Log.i("cancel", toastText);
    }

    public String getRecurringDaysText() {
        if (!recurring) {
            return null;
        }

        String days = "";
        if (monday) {
            days += "월요일 ";
        }
        if (tuesday) {
            days += "화요일 ";
        }
        if (wednesday) {
            days += "수요일 ";
        }
        if (thursday) {
            days += "목요일 ";
        }
        if (friday) {
            days += "금요일 ";
        }
        if (saturday) {
            days += "토요일 ";
        }
        if (sunday) {
            days += "일요일 ";
        }

        return days;
    }

    public String getTitle() {
        return title;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }
}