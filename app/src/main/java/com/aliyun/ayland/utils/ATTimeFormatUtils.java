package com.aliyun.ayland.utils;

public class ATTimeFormatUtils {
    public static String minuteToTime(int minute) {
        long hours = minute / 60;
        long minutes = minute % 60;
        return (hours < 10 ? "0" + hours : hours) + ":" + (minutes < 10 ? "0" + minutes : minutes);
    }
}
