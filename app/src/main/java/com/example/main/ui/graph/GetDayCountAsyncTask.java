package com.example.main.ui.graph;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.main.db.dayscount.CountDatabase;
import com.example.main.db.dayscount.DaysCountDao;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class GetDayCountAsyncTask extends AsyncTask<Void, Void, Integer> {
    private WeakReference<Activity> weakReference;
    //データベースとの紐づけ（回数カウント用　→　CountDataBase）
    private CountDatabase countDatabase;

    int Count[] = new int[7];

    //1日 = 86400000ms
    //日付取得の時に使う
    final long DAY = -86400000;

    public GetDayCountAsyncTask(Activity activity, CountDatabase countDatabase) {
        weakReference = new WeakReference<>(activity);
        this.countDatabase = countDatabase;

    }

    //今日の日付を取得するメソッド
    //フォーマットは引数で指定する
    //今日の日付を取得するメソッド
    private String getDay(int days, String format) {
        DateFormat df = new SimpleDateFormat(format);
        Date date = new Date(System.currentTimeMillis() + DAY * days);

        return df.format(date);
    }

    //非同期処理が行われる場所
    @Override
    protected Integer doInBackground(Void... aVoid) {
        DaysCountDao daysCountDao = countDatabase.daysCountDao();

        for (int i = 6; i > -1; i--) {
            GraphFragment.Count[i] = daysCountDao.getCount("%" + getDay(i, "yyyy/ MM/ dd") + "%");
            Log.d(TAG, "doInBackground:ここが回数を入れてるところ！！ " + GraphFragment.Count[i] + i);
        }

        return 0;
    }

    //非同期処理が実行し終わった後に実行されるメソッド
    @Override
    protected void onPostExecute(Integer code) {
        Activity activity = weakReference.get();
        if (activity == null) {
            return;
        }

        return;
    }
}
