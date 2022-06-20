package com.example.main.ui.recognition;

import android.app.Activity;
import android.os.AsyncTask;

import com.example.main.db.dayscount.CountDatabase;
import com.example.main.db.dayscount.DaysCount;
import com.example.main.db.dayscount.DaysCountDao;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetCountAsyncTask extends AsyncTask<Void, Void, Integer> {
    private WeakReference<Activity> weakReference;
    //データベース二つとの紐づけ
    private CountDatabase countDatabase;
    private List<String> arrayList;

    //コンストラクタ―
    public GetCountAsyncTask(Activity activity, CountDatabase countDatabase, ArrayList data) {
        weakReference = new WeakReference<>(activity);
        this.countDatabase = countDatabase;
        this.arrayList = data;
    }

    //今日の日付を取得するメソッド
    private String getDay(String format) {
        DateFormat df = new SimpleDateFormat(format);
        Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }

    //非同期処理が行われる場所
    @Override
    protected Integer doInBackground(Void... aVoid) {
        return 0;
    }

    //非同期処理が実行し終わった後に実行されるメソッド
    @Override
    protected void onPostExecute(Integer code) {
        Activity activity = weakReference.get();
        DaysCountDao daysCountDao = countDatabase.daysCountDao();
        List<DaysCount> list = daysCountDao.getDayAll(getDay("yyyy/ MM/ dd"));
        if (activity == null) {
            return;
        }
        for (DaysCount dc : list) {
            arrayList.add(dc.getWord());
        }
    }
}
