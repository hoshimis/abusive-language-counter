package com.example.main.ui.graph;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.main.db.dayscount.CountDatabase;
import com.example.main.db.dayscount.DaysCountDao;
import com.example.main.util.GetDay;

import java.lang.ref.WeakReference;

import static android.content.ContentValues.TAG;

public class GetCountAsyncTask extends AsyncTask<Void, Void, Integer> {
    /*フィールド*/

    public static final int GET_WEEK = 0;
    public static final int GET_MONTH = 1;
    public static final int GET_YEAR = 2;

    private final WeakReference<Activity> weakReference;
    //データベースとの紐づけ（回数カウント用　→　CountDataBase）
    private final CountDatabase countDatabase;
    //日付取得機能の準備
    GetDay gt = new GetDay();
    //1週間分、1ヶ月分、年間のどの回数を取得するか区別するための変数を宣言
    int distinction;

    //コンストラクター
    public GetCountAsyncTask(Activity activity, CountDatabase countDatabase, int distinction) {
        weakReference = new WeakReference<>(activity);
        this.countDatabase = countDatabase;
        this.distinction = distinction;
    }

    //TODO ここに1週間分、1ヶ月分、年間の処理を場合分けして書くことができたらいいなぁ

    //非同期処理が行われる場所
    @Override
    protected Integer doInBackground(Void... aVoid) {
        DaysCountDao daysCountDao = countDatabase.daysCountDao();
        Log.d(TAG, "doInBackground: 非同期処理が始まるよ！！");

        switch (distinction) {
            case GET_WEEK:
                //今日から一週間前までの回数をDBから取得して、それぞれ配列に格納していく
                for (int i = 0; i < 7; i++) {
                    //o -> 今日
                    //1 -> 昨日 ...
                    GraphFragment.weekCount[i] = daysCountDao.getCount("%" + gt.getDate(i * -1, "yyyy/ MM/ dd") + "%");
                    Log.d(TAG, "doInBackground:ここが回数を入れてるところ！！ " + GraphFragment.weekCount[i] + i);
                }
                break;

            case GET_MONTH:
                getMonthCount(daysCountDao);
                break;
            case GET_YEAR:
                getYearCount(daysCountDao);
                break;
        }
        return 0;
    }

    //月ごとの認識された回数を取得するメソッド
    private int getMonthCount(DaysCountDao daysCountDao) {
        int month = Integer.parseInt(gt.getDate(GetDay.TODAY, "MM"));
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                for (int i = 0; i < 31; i++) {
                    // TODO
                    GraphMonthActivity.monthCount[i] =
                            daysCountDao.getCount("%" + gt.getDate(GetDay.TODAY, "yyyy/ MM/ ") + String.format("%02d", i + 1) + "%");
                }
                break;
            case 2:
                for (int i = 0; i < 28; i++) {
                    GraphMonthActivity.monthCount[i] =
                            daysCountDao.getCount("%" + gt.getDate(GetDay.TODAY, "yyyy/ MM/ ") + String.format("%02d", i + 1) + "%");
                }
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                for (int i = 0; i < 30; i++) {
                    Log.d(TAG, "getMonthCount: データを入れるよ！！");
                    GraphMonthActivity.monthCount[i] =
                            daysCountDao.getCount("%" + gt.getDate(GetDay.TODAY, "yyyy/ MM/ ") + String.format("%02d", i + 1) + "%");
                }
                break;
        }

        return 0;
    }

    private int getYearCount(DaysCountDao daysCountDao) {
        for (int i = 0; i < 12; i++) {
            GraphYearActivity.yearCount[i] =
                    daysCountDao.getCount("%" + gt.getDate(GetDay.TODAY, "yyyy/ ") + String.format("%02d", i + 1) + "%");
            Log.d(TAG, "getYearCount: " + gt.getDate(GetDay.TODAY, "yyyy/ ") + String.format("%02d", i + 1));
        }
        return 0;
    }


}
