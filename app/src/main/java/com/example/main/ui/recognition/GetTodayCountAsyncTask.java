package com.example.main.ui.recognition;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;

import com.example.main.db.dayscount.CountDatabase;
import com.example.main.db.dayscount.DaysCountDao;
import com.example.main.util.GetDay;

import java.lang.ref.WeakReference;

public class GetTodayCountAsyncTask extends AsyncTask<Void, Void, Integer> {
    /*フィールド*/
    private WeakReference<Activity> weakReference;
    //データベースとの紐づけ（回数カウント用　→　CountDataBase）
    private CountDatabase countDatabase;
    //テキストビューの宣言
    private TextView textView;
    //日付取得機能の準備
    GetDay gt = new GetDay();

    //コンストラクター
    public GetTodayCountAsyncTask(Activity activity, CountDatabase countDatabase, TextView textView) {
        weakReference = new WeakReference<>(activity);
        this.countDatabase = countDatabase;
        this.textView = textView;

    }

    //非同期処理が行われる場所
    //今日の日付文の行数をDBから取得する → カウント変数に代入
    @Override
    protected Integer doInBackground(Void... aVoid) {
        DaysCountDao daysCountDao = countDatabase.daysCountDao();
        RecognitionFragment.count = daysCountDao.getCount("%" + gt.getDate(GetDay.TODAY, "yyyy/ MM/ dd") + "%");

        return 0;
    }

    //非同期処理が実行し終わった後に実行されるメソッド
    //カウント変数を画面に表示する
    @Override
    protected void onPostExecute(Integer code) {
        Activity activity = weakReference.get();
        if (activity == null) {
            return;
        }
        textView.setText(String.valueOf(RecognitionFragment.count));
    }
}
