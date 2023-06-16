package com.learntodroid.simplealarmclock.createalarm;

import android.os.Build;
import android.widget.TimePicker;

public final class TimePickerUtil {
    public static int getTimePickerHour(TimePicker tp) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return tp.getHour(); //TIme Picker 위젯에서 시간 가져오기
        } else {
            return tp.getCurrentHour();
        }
    }

    public static int getTimePickerMinute(TimePicker tp) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return tp.getMinute(); //TIme Picker 위젯에서 분 가져오기
        } else {
            return tp.getCurrentMinute();
        }
    }
}
