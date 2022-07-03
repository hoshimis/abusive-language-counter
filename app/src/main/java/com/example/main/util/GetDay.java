package com.example.main.util;

public class GetDay implements GetDayInterface {
    final public static int TODAY = 0;
    final public static int YESTERDAY = -1;
    final public static int TWO_DAYS_AGO = -2;
    final public static int THREE_DAYS_AGO = -3;
    final public static int FOUR_DAYS_AGO = -4;
    final public static int FIVE_DAYS_AGO = -5;
    final public static int SIX_DAYS_AGO = -6;

    @Override
    public String getDate(int day, String format) {
        return GetDayInterface.super.getDate(day, format);
    }
}
