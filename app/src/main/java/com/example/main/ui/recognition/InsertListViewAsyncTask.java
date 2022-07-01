package com.example.main.ui.recognition;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import com.example.main.db.dayscount.CountDatabase;
import com.example.main.db.dayscount.DaysCount;
import com.example.main.db.dayscount.DaysCountDao;
import com.example.main.util.GetDay;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class InsertListViewAsyncTask extends AsyncTask<Void, Void, Integer> {
    /*フィールド*/
    private final WeakReference<Activity> weakReference;
    //データベースとの紐づけ（回数カウント用　→　CountDataBase）
    private final CountDatabase countDatabase;
    //リストの宣言
    private List<String> arrayList;
    private final ArrayAdapter<String> adapter;

    //日付取得機能の準備
    GetDay gt = new GetDay();

    //コンストラクタ―
    public InsertListViewAsyncTask(Activity activity, CountDatabase countDatabase, ArrayList arrayList, ArrayAdapter<String> adapter) {
        weakReference = new WeakReference<>(activity);
        this.countDatabase = countDatabase;
        this.arrayList = arrayList;
        this.adapter = adapter;
    }

    //非同期処理が行われる前に実行される処理
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //値を追加する前に現在挿入されているデータを削除する
        adapter.clear();
    }

    //非同期処理が行われる場所
    //今日の日付が登録されている行をひとつづつリストに代入していく　→　画面で認識された言葉がリストビューで見えるようにする
    @Override
    protected Integer doInBackground(Void... aVoid) {
        DaysCountDao daysCountDao = countDatabase.daysCountDao();
        List<DaysCount> atList = daysCountDao.getDayAll("%" + gt.getDate(GetDay.TODAY, "yyy/ MM/ dd") + "%");
        for (DaysCount at : atList) {
            //日付もリストに格納するのは長くなりすぎると思うので、時間だけを切り取って格納する
            //ワーカースレッドからアダプターはいじれないのでリストに情報を格納する
            arrayList.add(at.getDate().substring(12, 18) + "     " + at.getWord());
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
        //データが変更されたら、リストビューに通知する
        adapter.notifyDataSetChanged();

    }
}
