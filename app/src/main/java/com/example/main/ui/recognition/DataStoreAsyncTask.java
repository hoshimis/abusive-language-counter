package com.example.main.ui.recognition;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.main.R;
import com.example.main.db.dayscount.CountDatabase;
import com.example.main.db.dayscount.DaysCount;
import com.example.main.db.dayscount.DaysCountDao;
import com.example.main.db.wordtable.WordDatabase;
import com.example.main.db.wordtable.WordTable;
import com.example.main.db.wordtable.WordTableDao;
import com.example.main.util.GetDay;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//以下からデータベース接続などの非同期処理
//メソッドとして、doInBackgroundを実装している
public class DataStoreAsyncTask extends AsyncTask<Void, Void, Integer> {
    /*フィールド*/
    private final WeakReference<Activity> weakReference;
    //データベース二つとの紐づけ
    private final CountDatabase countDatabase;
    private final WordDatabase wordDatabase;
    //音声認識した言葉をここに入力する
    private final String speechText;
    //カウント画面の画像を宣言
    private final ImageView jagged;
    //マッチした回数を表示するテキストビュー
    private final TextView countText;
    //日付取得機能の準備
    GetDay gt = new GetDay();

    //共有プリファレンスの宣言
    SharedPreferences preferences;
    //コンストラクタ―
    public DataStoreAsyncTask(Activity activity, CountDatabase countDatabase, WordDatabase wordDatabase, String speechText, TextView countText, ImageView jagged, SharedPreferences preferences) {

        //ここで、インスタンス化した時に渡された引数の値をフィールドの値に代入する
        weakReference = new WeakReference<>(activity);
        this.countDatabase = countDatabase;
        this.wordDatabase = wordDatabase;
        this.speechText = speechText;
        this.countText = countText;
        this.jagged = jagged;
        this.preferences = preferences;
    }

    //AsyncTaskの実装
    //非同期処理(Activityの処理を止めることなく実行したい処理)
    //Activityに何も書いてないけどどうなんでしょうね？？
    //このフラグメントにいろんな処理書いてあるからそれでいいのかもしれないですね
    @Override
    protected Integer doInBackground(Void... params) {
        WordTableDao wordTableDao = wordDatabase.wordTableDao();
        DaysCountDao daysCountDao = countDatabase.daysCountDao();

        //単語DBからすべてのワードを取得してリストに代入する。
        List<WordTable> atList = wordTableDao.getAll();

        for (WordTable at : atList) {
            //単語を１つずつ取り出して、正規表現のパターンに当てはめる
            Pattern pattern = Pattern.compile(".*" + at.getWord() + ".*");
            Matcher matcher = pattern.matcher(speechText);

            //正規表現にマッチしたらtrueしなかったらfalseになる
            if (matcher.find()) {
                //カウントDBに今日の日付をDBに挿入する
                //最終的に対応する日付の行数をカウントすればその日の日付がカウントされる
                daysCountDao.insert(new DaysCount(gt.getDate(GetDay.TODAY, "yyyy/ MM/ dd HH:mm:ss"), speechText));
                //今日の日付の分を今日行った暴言の回数として扱う
                RecognitionFragment.count = daysCountDao.getCount("%" + gt.getDate(GetDay.TODAY, "yyyy/ MM/ dd") + "%");
                //ループされても困るので、一回正規表現にマッチすればループから抜けるようにする。
                break;
            }
        }
        return 0;
    }

    //非同期処理が実行し終わった後に実行されるメソッド
    @Override
    protected void onPostExecute(Integer code) {
        //実行されるときに、呼び出し元のActivityが存在しなかったら何もしない
        Activity activity = weakReference.get();
        if (activity == null) {
            return;
        }
        //DBから今日の日付分の回数を取得してテキスト
        Log.d(TAG, "onPostExecute: " + RecognitionFragment.count);
        countText.setText(String.valueOf(RecognitionFragment.count));

        int MonthLevel1=preferences.getInt("ColorCountLevel1",20);
        int MonthLevel2=preferences.getInt("ColorCountLevel2",30);
        int MonthLevel3=preferences.getInt("ColorCountLevel3",40);

        //ちくちくの画像がカウント回数によって変化する処理
        if (RecognitionFragment.count == 0) {
            jagged.setImageResource(R.drawable.count_level_1);
        } else if (RecognitionFragment.count <= MonthLevel1) {
            jagged.setImageResource(R.drawable.count_level_2);
        } else if (RecognitionFragment.count <= MonthLevel2) {
            jagged.setImageResource(R.drawable.count_level_3);
        } else if (RecognitionFragment.count <= MonthLevel3) {
            jagged.setImageResource(R.drawable.count_level_4);
        } else {
            jagged.setImageResource(R.drawable.count_level_5);
        }
    }
}