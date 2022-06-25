package com.example.main.ui.recognition;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.main.db.dayscount.CountDatabase;
import com.example.main.db.dayscount.DaysCount;
import com.example.main.db.dayscount.DaysCountDao;
import com.example.main.util.GetDay;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class InsertListViewAsyncTask extends AsyncTask<Void, Void, Integer> {
    /*フィールド*/
    private WeakReference<Activity> weakReference;
    //データベースとの紐づけ（回数カウント用　→　CountDataBase）
    private CountDatabase countDatabase;
    //リストの宣言
    private List<String> arrayList;

    //日付取得機能の準備
    GetDay gt = new GetDay();

    //コンストラクタ―
    public InsertListViewAsyncTask(Activity activity, CountDatabase countDatabase, ArrayList data) {
        weakReference = new WeakReference<>(activity);
        this.countDatabase = countDatabase;
        this.arrayList = data;
    }

    //非同期処理が行われる場所
    //今日の日付が登録されている行をひとつづつリストに代入していく　→　画面で認識された言葉がリストビューで見えるようにする
    @Override
    protected Integer doInBackground(Void... aVoid) {
        DaysCountDao daysCountDao = countDatabase.daysCountDao();
        List<DaysCount> atList = daysCountDao.getDayAll("%" + gt.getDate(GetDay.TODAY, "yyy/ MM/ dd") + "%");
        for (DaysCount at : atList) {
            //日付もリストに格納するのは長くなりすぎると思うので、時間だけを切り取って格納する
            arrayList.add(at.getDate().substring(12, 18) + "     " + at.getWord());
        }
        Log.d(TAG, "doInBackground: リストビューにデータを挿入しているよ！！");
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
