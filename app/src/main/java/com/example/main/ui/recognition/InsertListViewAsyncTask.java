package com.example.main.ui.recognition;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.main.db.dayscount.CountDatabase;
import com.example.main.db.dayscount.DaysCount;
import com.example.main.db.dayscount.DaysCountDao;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InsertListViewAsyncTask extends AsyncTask<Void, Void, Integer> {
    private WeakReference<Activity> weakReference;
    //データベースとの紐づけ（回数カウント用　→　CountDataBase）
    private CountDatabase countDatabase;

    //リストの宣言
    private List<String> arrayList;

    //コンストラクタ―
    public InsertListViewAsyncTask(Activity activity, CountDatabase countDatabase, ArrayList data) {
        weakReference = new WeakReference<>(activity);
        this.countDatabase = countDatabase;
        this.arrayList = data;
    }

    //今日の日付を取得するメソッド
    //フォーマットは引数で指定する
    private String getDay(String format) {
        DateFormat df = new SimpleDateFormat(format);
        Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }

    //非同期処理が行われる場所
    //今日の日付が登録されている行をひとつづつリストに代入していく　→　画面で認識された言葉がリストビューで見えるようにする
    @Override
    protected Integer doInBackground(Void... aVoid) {
        DaysCountDao daysCountDao = countDatabase.daysCountDao();
        List<DaysCount> atList = daysCountDao.getDayAll("%" + getDay("yyyy/ MM/ dd") + "%");
        for (DaysCount at : atList) {
            //日付もリストに格納するのは少し冗長になりすぎると思うので、時間だけを切り取って格納する
            arrayList.add(at.getDate().substring(12,18) + "     " + at.getWord());
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
    }
}
