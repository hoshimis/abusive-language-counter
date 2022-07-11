package com.example.main;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.main.db.wordtable.WordDatabase;
import com.example.main.db.wordtable.WordTable;
import com.example.main.db.wordtable.WordTableDao;

import java.lang.ref.WeakReference;

public class InitWordDataBase extends AsyncTask<Void, Void, Integer> {
    /*フィールド*/
    //データベースの準備
    WordDatabase wordDatabase;

    private final WeakReference<Activity> weakReference;
    //受け取った文字列を格納する
    String initWord;

    //受け取った文字列をSplitで分割して格納する
    String[] word;

    //コンストラクタ―
    public InitWordDataBase(Activity activity, String initWord, WordDatabase wordDatabase) {
        weakReference = new WeakReference<>(activity);
        this.initWord = initWord;
        this.wordDatabase = wordDatabase;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //txtから入手した文字列を配列に格納する
        word = initWord.split(",");

    }

    //AsyncTaskの実装
    //非同期処理(Activityの処理を止めることなく実行したい処理)
    //Activityに何も書いてないけどどうなんでしょうね？？
    //このフラグメントにいろんな処理書いてあるからそれでいいのかもしれないですね
    @Override
    protected Integer doInBackground(Void... params) {
        WordTableDao wordTableDao = wordDatabase.wordTableDao();
        //1単語ずつDBに格納する
        for (String at : word) {
            wordTableDao.insert(new WordTable(at));
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
    }
}