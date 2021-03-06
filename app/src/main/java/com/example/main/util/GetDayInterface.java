package com.example.main.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public interface GetDayInterface {
    //1日の秒数
    long DAY = 86400000;

    default String getDate(int day, String format) {
        DateFormat df = new SimpleDateFormat(format);
        Date date = new Date(System.currentTimeMillis() + DAY * day);
        return df.format(date);
    }

}
