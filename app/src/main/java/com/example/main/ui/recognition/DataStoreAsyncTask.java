package com.example.main.ui.recognition;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.example.main.db.dayscount.CountDatabase;
import com.example.main.db.dayscount.DaysCount;
import com.example.main.db.dayscount.DaysCountDao;
import com.example.main.db.wordtable.WordDatabase;
import com.example.main.db.wordtable.WordTable;
import com.example.main.db.wordtable.WordTableDao;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO geDayメソッドとその類似メソッドを1つのクラスとしてまとめる

//以下からデータベース接続などの非同期処理
//メソッドとして、doInBackgroundを実装している
public class DataStoreAsyncTask extends AsyncTask<Void, Void, Integer> {
    private WeakReference<Activity> weakReference;
    //データベース二つとの紐づけ
    private CountDatabase countDatabase;
    private WordDatabase wordDatabase;
    //音声認識した言葉をここに入力する
    private String speechText;
    //マッチした回数を表示するテキストビュー
    private TextView countText;
    //1日 = 86400000ms
    //日付取得の時に使う
    final long DAY = 86400000;

    //コンストラクタ―
    public DataStoreAsyncTask(Activity activity, CountDatabase countDatabase, WordDatabase wordDatabase, String speechText, TextView countText) {
        //ここで、インスタンス化した時に渡された引数の値をフィールドの値に代入する
        weakReference = new WeakReference<>(activity);
        this.countDatabase = countDatabase;
        this.wordDatabase = wordDatabase;
        this.speechText = speechText;
        this.countText = countText;
    }


    //今日の日付を取得するメソッド
    private String getDay(int days, String format) {
        DateFormat df = new SimpleDateFormat(format);
        Date date = new Date(System.currentTimeMillis() + DAY * days);

        return df.format(date);
    }

    //AsyncTaskの実装
    //非同期処理(Activityの処理を止めることなく実行したい処理)
    //Activityに何も書いてないけどどうなんでしょうね？？
    //このフラグメントにいろんな処理書いてあるからそれでいいのかもしれないですね
    @Override
    protected Integer doInBackground(Void... params) {
        WordTableDao wordTableDao = wordDatabase.wordTableDao();
        DaysCountDao daysCountDao = countDatabase.daysCountDao();

        //データベースに値を入れる処理

//            wordTableDao.insert(new WordTable("バカ"));
//            wordTableDao.insert(new WordTable("アホ"));
//            wordTableDao.insert(new WordTable("死ね"));
//            wordTableDao.insert(new WordTable("消えろ"));
//            wordTableDao.insert(new WordTable("ハゲ"));
//            wordTableDao.insert(new WordTable("おはよう"));
//            wordTableDao.insert(new WordTable("ばーか"));

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
                daysCountDao.insert(new DaysCount(getDay(0, "yyyy/ MM/ dd HH:mm:ss"), speechText));
                //今日の日付の分を今日行った暴言の回数として扱う
                RecognitionFragment.count = daysCountDao.getCount("%" + getDay(0, "yyyy/ MM/ dd") + "%");

                //ループされても困るので、一回正規表現にマッチすればループから抜けるようにする。
                break;
            }
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
        //DBから今日の日付分の回数を取得してテキスト
        Log.d(TAG, "onPostExecute: " + RecognitionFragment.count);
        countText.setText(String.valueOf(RecognitionFragment.count));
    }
}