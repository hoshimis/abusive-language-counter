package com.example.main.ui.recognition;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.main.R;
import com.example.main.db.dayscount.CountDatabase;
import com.example.main.db.dayscount.DaysCountDao;
import com.example.main.util.GetDay;

import java.lang.ref.WeakReference;

import static com.example.main.ui.recognition.RecognitionFragment.count;

public class GetTodayCountAsyncTask extends AsyncTask<Void, Void, Integer> {
    /*フィールド*/
    private WeakReference<Activity> weakReference;
    //データベースとの紐づけ（回数カウント用　→　CountDataBase）
    private CountDatabase countDatabase;
    //テキストビューの宣言
    private TextView textView;
    //イメージビューの宣言
    private ImageView gizagiza_image;
    //日付取得機能の準備
    GetDay gt = new GetDay();

    //コンストラクター
    public GetTodayCountAsyncTask(Activity activity, CountDatabase countDatabase, TextView textView, ImageView gizagiza_image) {
        weakReference = new WeakReference<>(activity);
        this.countDatabase = countDatabase;
        this.textView = textView;
        this.gizagiza_image = gizagiza_image;

    }

    //非同期処理が行われる場所
    //今日の日付文の行数をDBから取得する → カウント変数に代入
    @Override
    protected Integer doInBackground(Void... aVoid) {
        DaysCountDao daysCountDao = countDatabase.daysCountDao();
        count = daysCountDao.getCount("%" + gt.getDate(GetDay.TODAY, "yyyy/ MM/ dd") + "%");

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
        textView.setText(String.valueOf(count));

        //ちくちくの画像がカウント回数によって変化する処理
        if (count == 0) {
            gizagiza_image.setImageResource(R.drawable.count_level_1);
        } else if (count <= 10) {
            gizagiza_image.setImageResource(R.drawable.count_level_2);
        } else if (count <= 20) {
            gizagiza_image.setImageResource(R.drawable.count_level_3);
        } else if (count <= 30) {
            gizagiza_image.setImageResource(R.drawable.count_level_4);
        } else {
            gizagiza_image.setImageResource(R.drawable.count_level_5);
        }
    }
}
