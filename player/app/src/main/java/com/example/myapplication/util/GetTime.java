package com.example.myapplication.util;

public class GetTime {

    public static String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
//        String ss= hours > 0 ? String.format("%02dh%02dm%02ds", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
        String ss=String.format("%02d:%02d", minutes, seconds);

        return ss;


    }
}
